; ID: 1527
; Author: Warpy
; Date: 2005-11-10 11:41:21
; Title: Save a list of objects
; Description: Saves a list of custom objects to a text file

'Here's the first example type. It's got 4 fields - an int, a float, a string, and a pointer to an object of another type
Type mytype1
	Field anint%,afloat#,astring$
	Field anotherobject:mytype2
	
	Method save(f:TStream) 
	'Call this method to save the object to a given filestream, which must be writeable
	
		'Write the int, float and string to the file, they're easy to do
		WriteInt f,anint
		WriteFloat f,afloat
		WriteLine f,astring 'watch out, it's got to be writeline so the string's terminated and it can be loaded back in properly
		'For the pointer, we first need an int that shows whether it's null or not (you can't write 'Null' to a file!)
		If anotherobject
			WriteInt f,1
			'As the pointer isn't null, call the object's save method
			anotherobject.save(f)
		Else 
			WriteInt f,0
		EndIf
	End Method
	
	Function load:mytype1(f:TStream) 
	'Call this function (in the form m:mytype1=mytype1.load() because the object doesn't 
	'exist yet) To load an Object from a filestream f, which must be readable.

		m:mytype1=New mytype1 'Create a new blank object to fill in
		'Load the easy bits in
		m.anint=Readint(f)
		m.afloat=ReadFloat(f)
		m.astring=ReadLine(f)
		
		hasanotherobject=Readint(f) 'This int will = 1 if there's the other object to load
		If hasanotherobject
			m.anotherobject=mytype2.load(f) 'Load in the other object
		EndIf
		Return m
	End Function
End Type

'This is the other demo type, just used to show how to save and load pointers to other objects
Type mytype2
	Field anotherint%
	
	Method save(f:TStream)
		WriteInt f,anotherint
	End Method
	
	Function load:mytype2(f:TStream)
		m:mytype2=New mytype2
		m.anotherint=Readint(f)
		Return m
	End Function
End Type

'This function saves a list of mytype1 objects to the given filestream, which must be writeable
Function savelistofmytype1(list:TList,f:TStream)
	For m:mytype1=EachIn list
		m.save(f)
	Next
End Function

'This function loads a list of mytype1 objects from the given filestream, which must be readable
Function loadlistofmytype1:TList(f:TStream)
	list:TList=New TList
	While Not Eof(f)
		m:mytype1=mytype1.load(f)
		list.addlast m
	Wend
	Return list
End Function

'This function prints out the values of all the objects in the list, 
'so you can compare the saved and loaded data to check they're the same.
Function printmytype1list(list:TList)
	For m:mytype1=EachIn list 
		Print m.anint
		Print m.afloat
		Print m.astring
		If m.anotherobject
			Print "another object: "+String(m.anotherobject.anotherint)
		EndIf		
	Next
End Function


'demo: Create a list, fill it with 10 objects, half of them with the anotherobject pointer
' non-null, save it, forget about it, then load it in again.
mylist:TList=New TList
For c=1 To 10
	m:mytype1=New mytype1
	m.anint=c
	m.afloat=Sin(c)
	m.astring=String(c)+"!"
	If c Mod 2 = 0
		m.anotherobject=New mytype2
		m.anotherobject.anotherint=c*2
	EndIf

	mylist.addlast m
Next

'Print created data
printmytype1list(mylist)

Print "///SAVING///"
f:TStream=OpenFile("save.txt",0,1) 'open save file, writeable
savelistofmytype1(mylist,f) 'save list of objects
CloseFile f 'close file
mylist=Null 'get rid of list
FlushMem 'make sure it's gone :)

Print "///LOADING///"
f:TStream=OpenFile("save.txt",1,0) 'open save file again, readable
mylist:TList=loadlistofmytype1(f) 'load list in again
CloseFile f

'Print loaded data
printmytype1list(mylist)
