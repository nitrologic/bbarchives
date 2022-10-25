; ID: 1907
; Author: SculptureOfSoul
; Date: 2007-01-27 14:52:57
; Title: Hashtable type for BMax
; Description: A hashtable that can store single or multiple objects. Very Fast!

''Constructor( capacity:int ) - returns a new Hashtable object with a maximum capacity = to 
''							the parameter provided. Utilize this function to create 
''							any and all hashtable objects.
''
''
''InsertEntry(name:string,o:object) - add an object to the table, the name is used to
''                                    to generate an index 0-Capacity, selecting the list. 
''							      multiple entries with the same name are permitted using
''								 this method. If you want to guarantee that only one 
''								 entry exists per a given name, use the 
''								 insertUniqueName( ) method
''
''insertUniqueName( name:string, o:object) - this method guarantees that the object specified
''									    will be the only object for that particular name.
''
''insertUniqueNamedObject( name:string, obj:object) - this method guarantees that there will only be 
''											    one copy of the object specified by the obj parameter
''											    in the hashtable. There may be multiple entries for
''											    the name however. Using this instead of the 
''											    standard InsertEntry() method when you want to make
''											    sure the same object (assuming it is the same name)
''											    is not duplicated in the table.
''
''RemoveNamed(name:string) - Removes all entries with the name specified from the hash table.
''                           Note, this does not return those objects. 
''
''RemoveNamedObject(name:string, obj:object) - removes any entries from the bucket specified by the name
''										 parameter whose object value matches the object specified
''										 by the obj parameter. This method lets you quickly remove
''										 a single object without removing all objects that share the
''										 same name (such as RemoveNamed() does.)
''
''RemoveObject( obj:object ) - searches every bucket in the hash table for the specified object and removes
''						   all traces of it from the hash table. Note: This method can be relatively slow
''                            on large tables, and if possible you should use RemoveNamedObject() instead.
''							
''GetEntry:object(name:string) - grab the first entry for a given name. You must cast it back to its original 
''                               type before using it,myobject=myType(GetEntry(name:string))
''
''GetMultipleEntries:object [](name:string) - returns an array of objects (entries) that match the
''									          given name parameter. This is useful when you intend
''									          to store a list of objects with a given name index
''
''Grow( growthsize:int ) - Increases the number of buckets in the hash array by growthsize.
''
''removeAllEntries() - removes all objects from the hash table                                    
''
''removeNamed( name )	removes all entries with the given name from the hash table
''
''
''									Regarding Unique Entries
''                                  ------------------------
''If you want to ensure there is only one entry for a given name key, there are two approaches you can take.
''If you want or need to ensure there is no entry for a given name key before you do an insert - or if there
''is an entry already present you want to handle it in some manner you should
''call getEntry( name ). If this is null, it's safe to insert. If this returns an object calling InsertEntry()
''will insert another entry with the same name. While you can access multiple entries with the same name via
''the method GetMultipleEntries(name), doing so is not ideal if you simply want a single entry per name.
''So the first approach is to call getEntry, and deal with the object returned (if any) and then do your 
''insertion.
''The second approach comes into play if you don't need access to any entry that might already be stored 
''under the insert name, but simply want to guarantee the entry you are inserting is the only one for
''that name. Calling insertUniqueName(name) will erase any entries already stored under "name" and guarantees that 
''there is only one unique entry for the name provided.


'''''''''''''''''''''''''''
''
''HASHTABLE OBJECT
''
''- M.Laurenson/Defoc8 2006
''- S.Hofslund/SculptureOfSoul 2006
''
''- major modifications: the introduction of a capacity parameter, getMultipleEntries(),
''  insertUniqueName(), insertUniqueNamedObject(), removeNamed(), removeNamedObject(), removeObject(), and 
''  Grow() methods and the Constructor() function some error checking code, document modification
''  as well as a new and much more effective hash algorithm added by S.Hofslund (SculptureOfSoul)
''''''''''''''''''''''''''''
Strict

Type THashTable

 Field _table:TList[]
 Field _capacity:Int
 ?debug
 Global _indirectconstruct:Int
 ?
 Method New()
  ?debug
  Assert _indirectconstruct, "Use THashtable.Constructor() to create a new hash table."
  ?
 EndMethod

 Function Constructor:THashTable( capacity:Int )
 ?debug
  _indirectconstruct = True
 ?
  Local retobj:THashTable = New THashTable
   retobj._capacity=capacity
   retobj._table=retobj._table[..capacity]
	For Local n:Int=0 Until capacity
     retobj._table[n]=New TList
	Next
  ?debug
  _indirectconstruct = False
  ?
  Return retobj
 EndFunction

 Method Grow( growthsize:Int )
  If growthsize <= 0 Return  

  Local oldtable:TList[] 
  oldtable = _table
  _capacity = (_capacity + growthsize)
  _table = New TList[_capacity]
  
  For Local i:Int = 0 Until _capacity
	_table[i] = New TList
  Next
  'regenerate indexes for and reinsert all of the old tables entries
  For Local n:Int = 0 Until oldtable.length
   For Local entry:gHashEntry = EachIn oldtable[n]
	InsertEntry( entry.name, entry.obj )
   Next
  Next

 EndMethod

 Method genIndex(name:String)
  Local val:Int=0
  Local temp:Int	
   For Local n:Int=0 Until name.length
'the following is commented out because it is neither as fast or as efficient as the one used below
'	val:+ (name[n])^2 + (name[n]*(n^2)) + (name[n]Mod 3 * name[n])
	val= (val Shl 3) + val + name[n] 
'commented out for the same reason 
'	val:+ (name[n])^(((name.length + 1 - n)Mod 2) + 2)
   Next
'call Abs on the index because it might be negative (in the case of an integer overflow). 
  Return  Abs(val Mod (_capacity ) )
 EndMethod

 Method insertEntry(name:String ,obj:Object)
  Local index:Int=genIndex(name$)
  Local entry:gHashEntry=New gHashEntry
    entry.name=name
    entry.obj=obj 
    entry.link=_table[index].AddLast(entry)
 EndMethod

'object must have an overriden compare method!
 Method insertSortedEntry(name:String,obj:Object,ascending:Int=True)
  Local index:Int=genIndex(name$)
  Local entry:gHashEntry=New gHashEntry
    entry.name=name
    entry.obj=obj 
    entry.link=_table[index].AddLast(entry)
    _table[index].Sort(ascending)
 EndMethod 

 Method sortEntriesNamed(name:String,ascending=True)
  Local index:Int=genIndex(name)
  _table[index].sort(ascending)
 EndMethod

 Method sortAll(ascending:Int=True)
  For Local iter:Int=0 Until _table.length
   _table[iter].sort(ascending)
  Next
 EndMethod

 Method insertUniqueNamedObject( name:String, obj:Object )
  Local index:Int = genIndex(name)
  internal_removeNamedObject( obj, index )
  Local entry:gHashEntry = New gHashEntry
   entry.name = name
   entry.obj = obj
   entry.link = _table[index].Addlast(entry)
 EndMethod
 
 Method insertUniqueName(name:String,obj:Object)
  Local index:Int=genIndex(name$)
  internal_removeNamed( name, index )
  Local entry:gHashEntry=New gHashEntry
    entry.name=name
    entry.obj=obj 
    entry.link=_table[index].AddLast(entry)
 EndMethod

 
 Method getEntry:Object(name:String)
  Local index:Int=genIndex(name)
  Local link:TLink = _table[index]._head
  Local entry:Object
 Rem
 the old for eachin variety of the loop, replaced below by the hand rolled loop. Uncomment this and comment the 
the below code to see for yourself the speed difference it makes (only noticeable when doing 1000's of operations though)
   For Local entry:gHashEntry=EachIn _table[index]
    If(entry.name=name)
     Return(entry.obj)
    EndIf
   Next
 EndRem
   
  'Print "_table[index].count() =" + _table[index].count()
  For Local iter = 0 Until _table[index].count()
    'handrolling the loop as it turns out to be much faster than a For..Eachin
    entry = link._succ._value   

    'do the keys match?
    If gHashEntry(entry).name = name
      Return gHashEntry(entry).obj
    EndIf
    link = link._succ
     'Return(entry.obj)
    'EndIf
   Next
  Return Null
 EndMethod

 Method getMultipleEntries:Object[](name:String)
  Local index:Int = genIndex(name)
  Local retarray:Object[] = New Object[getEntryCount(index)]
  Local objectcount:Int

   For Local entry:gHashEntry=EachIn _table[index]
     If entry.name = name
	  retarray[objectcount] = entry.obj
	  objectcount:+ 1
	 EndIf
   Next
		
	'resize the array to objectcount elements.
    	retarray = retarray[..objectcount]    
	Return retarray
 EndMethod
Rem

The below are leftovers from a variety of tests. I figured someone might find these of interest. Anyhow, except in
certain special situations, I found them to be slower than the code I'm using for getMultipleEntries. Feel free
to experiment though.

 Method getMultipleEntries2:Object[](name:String)
  Local index:Int = genIndex(name)
  Local objectcount:Int

   For Local entry:gHashEntry=EachIn _table[index]
     If entry.name = name
	  objectcount:+ 1
	 EndIf
   Next
	Local retarray:Object[] = New Object[objectcount]
	objectcount = 0
   For Local entry:gHashEntry = EachIn _table[index]
     If entry.name = name
	  retarray[objectcount] = entry.obj
	  objectcount:+ 1
	 EndIf
   Next
	'resize the array to objectcount elements.
	'MemCopy( retarray, retarray, SizeOf(retarray[0]) * objectcount)
    
	'retarray = retarray[..objectcount]    'above method is faster
	Return retarray
 EndMethod
EndRem
Rem
 Method getMultipleEntries3:Objwrapper(name:String)
  Local index:Int = genIndex(name)
  Local retarray:Object[] = New Object[getEntryCount(index)]
  Local objectcount:Int
  Local wrapper:objwrapper = New objwrapper
   For Local entry:gHashEntry=EachIn _table[index]
     If entry.name = name
	  retarray[objectcount] = entry.obj
	  objectcount:+ 1
	 EndIf
   Next

	'resize the array to objectcount elements.
	'MemCopy( retarray, retarray, SizeOf(retarray[0]) * objectcount)
    wrapper.objarray = retarray
    wrapper.length = retarray.length
	
	Return wrapper
 EndMethod
EndRem

'simply a slightly faster version of removeNamed. Faster because the index is provided
'and doesn't need to be generated. This method is called by insertUniqueName
 Method internal_removeNamed( name:String, index:Int )
  For Local entry:gHashEntry = EachIn _table[index]
   If(entry.name = name)
    entry.link.Remove()
   EndIf
  Next
 EndMethod

 Method removeNamed:Int( name:String )	'returns the # of objects removed
  Local index:Int = genIndex(name)
  Local remove_count:Int = 0
  For Local entry:gHashEntry = EachIn _table[index]
   If(entry.name = name)
    entry.link.Remove()
    remove_count:+ 1
   EndIf
  Next
  Return remove_count
 EndMethod

 Method internal_removeNamedObject( obj:Object, index:Int )
   For Local entry:gHashEntry = EachIn _table[index]
	If(entry.obj = obj)
	 entry.link.Remove()
	EndIf
   Next
  EndMethod

 Method removeNamedObject:Int( name:String, obj:Object ) 'returns # removed
   Local index:Int = genIndex(name)
   Local remove_count:Int = 0
   For Local entry:gHashEntry = EachIn _table[index]
	If(entry.obj = obj)
	 entry.link.Remove()
	 remove_count:+ 1
	EndIf
   Next
   Return remove_count
  EndMethod
'this loops through every bucket in the hash table
 Method removeObject:Int( obj:Object )	'returns # removed
  Local remove_count:Int = 0
  For Local iter = 0 Until _table.length
   For Local entry:gHashEntry = EachIn _table[iter]
    If entry.obj = obj
     entry.link.remove()
     remove_count:+ 1
    EndIf
   Next
  Next
  Return remove_count
 EndMethod 

 Method removeAllEntries() 
  For Local n:Int=0 Until _capacity
   _table[n].clear()
  Next
 EndMethod 

 Method getEntryCount:Int(index:Int)
  If (index >= 0) And (index < _capacity)
   Return _table[index].count()
  EndIf
 EndMethod
EndType

Type gHashEntry 
 Field name:String
 Field obj:Object
 Field link:TLink
 Method Compare(pHashEntry:Object)
  'Try
?debug 
  Print "gHashEntry.compare called"
?
 ' Print "Compare result: " + obj.Compare2(gHashEntry(pHashEntry).obj)
  Return obj.Compare(gHashEntry(pHashEntry).obj)
  'Catch ex:Object
  'EndTry
 EndMethod
EndType








                                          
'--------------------------------------------'Test Section Below--------------------------------------------
'these are included just to demonstrate the usage and speed of the table. Comment them out at your leisure.
'-----------------------------------------------------------------------------------------------------------













Global HashTable:THashTable = THashTable.Constructor( 1024 )
Global testint:intobj = New intobj
Global tempint:Int
Global str1$ 
Global str2$ 
Global obj:Object

'this type is used as a wrapper for ints allowing them to be treated as objects (and thus stored in the hash table.)
Type intobj
Field val:Int
EndType

'str1 = our key string, str2 = our object to be inserted.
str1 = "Strength"
str2 = "3"
testint.val = 55
Hashtable.insertEntry( str1, str2)


'----------------------Test: 900,000 lookups and assignments
Local starttime:Int = MilliSecs()
For Local iter = 0 To 900000
 obj =  Hashtable.getentry( str1 )

 'the following assignment works because strings are objects. If you wanted to assign a numeric 4 you'd have to wrap it 
 'in an object (as is done below with the intobj type.)
 obj = "4"
Next

Local endtime:Int = MilliSecs()

Print "~n"
Print "*********************************************************************************************"
Print "Millisecs to execute 900,000 lookups and assignments:" + (endtime - starttime)
Print "*********************************************************************************************"
'-------------------------------------------------------------------



'clear the table for the next test.
HashTable.RemoveAllEntries()





'---------------Test: Insert 1024 entries into a hash table of size 1024 to see then print out the 
'---------------distribution list to see how well the hash algorithm is working
Print "~n"
Print "*********************************************************************************************"
Print "The following is a list of the # of entries per hash table element given 1024 entries in a hash table with 1024 elements."
Print "The string used as a key is the following: ~qc:\documents And settings\random\testing\directory\~q plus up to 8 random letters."
Print "This test should prove that the distribution, even given extremely similar strings, is still quite uniform."
Print "The format of the list below is N:x where X is the element # in the hash table (0-1024 here since the table has 1024 elements."
Print "And the entry count is the # of entries stored at that hash element (ideally there will be as few duplicates as possible.)"
Print "If you are interested try altering the hash algorithm and see how it affects the distribution and duplication count."
Print "Also note that I'm not currently seeding the random generator anywhere, so you'll get the same results each time even though"
Print "in a real world situation the results will vary (possibly quite a bit) with a different seed in place, so feel free to seed the random"
Print "generator to get more real world results. That, or try altering the test string that is used in the key - this can lead to drastically different"
Print "results as well, and a wide range of tests are needed to verify that the algorithm is efficient with it's distribution given a wide range of inputs."
Print "My tests have shown the algorithm above to be better than those I've commented out (and many others I tried). YMMV."
Print "*********************************************************************************************"
Global teststr:String

'generate and insert the entries. We start with the base string and add up to 8 random characters before inserting.
For Local iter = 0 Until 1024
 teststr = "c:\documents and settings\random\testing\directory\"

	'generate up to 8 random characters to add to the test string
	For Local iter2 = 0 Until 8
	If Rand(2) = 1
	teststr:+ Chr(Rand( 65,125 ))
	EndIf
	Next	 
	
 HashTable.insertEntry( teststr, testint )
Next

'this loops through the entire hash table, counting the # of objects stored at each index and also the total # of 
'duplicates. A duplicate is equivalent to a hash collision - a duplicate ID generated from different inputs.
Local duplicatecount:Int
For Local iter6 = 0 Until hashtable._table.length
	Print "N:" + iter6 + "....entry count:" + HashTable._table[iter6].count()
	If HashTable._table[iter6].count() > 1
	 duplicatecount:+ HashTable._table[iter6].count() -1
	EndIf
Next
Print "number of duplicates:" + duplicatecount



'clear the table for the next test.
HashTable.RemoveAllEntries()





'Insert 1024 entries with the key "testXX" where XX is a number from 0-49. The object inserted is the string "entryXX" where
'XX is again a number from 0-49.
Print "~n"
Print "*********************************************************************************************"
Print "Inserting 1024 entries with the key ~qtestXX~q where XX is a number from 1-50. The Object inserted is the String ~qentryXX~q where"
Print "XX is again a number from 1-50 that may or may not match the key number."
Print "*********************************************************************************************"
Print "~n"

'insert 1024 entries with a key of testXX where XX = a number from 1-50. Obviously, there will be multiple entries 
'for any given key.
For Local iter = 0 Until 1024
	tempint = Rand(50)
	HashTable.insertentry("test" + tempint, "entry" + Rand(50) )
Next


Print "*********************************************************************************************"
Print "Retrieving multiple entries for the key ~qtest25~q. The entry #'s were randomly generated during the insertion and are from 1-50."
Print "*********************************************************************************************"

'this is how you retrieve multiple entries for a given key! The entries are returned as an array of 
'objects that you'll need to cast before using.
	Local objarray:Object[]
	objarray = HashTable.getMultipleEntries( "test25" )
	For Local objiter = 0 Until objarray.length
	'since we know the object stored is a string, we cast to string and then print.
	 Print String(objarray[objiter])
	Next

Print "~n"		
Print "*********************************************************************************************"
Print "The following is the time it took to call ~qgetMultipleEntries~q 900,000 times." 
Print "*********************************************************************************************"

'This just demonstrates the speed of "getMultipleEntries." It is considerably slower than getEntry() - especially
'when there are many duplicate entries, but is obviously necessary if you need to retrieve more than one entry.
	starttime = MilliSecs()
	For Local iter5 = 0 Until 900000
	objarray = HashTable.getMultipleEntries( "test25" )
	Next
	Print "Millisecs:" + (MilliSecs() - starttime)

Print 

'And now to prove that InsertUniqueName() works, we'll call it with the key "test25" which we've already shown
'above to have multiple entries stored. The entries that were stored with the key "test25" will be deleted and
'only the entry inserted with "InsertUniqueName()" will exist.
Print "*********************************************************************************************"
Print "Now calling Hashtable.InsertUniqueName( ~qtest25~q, ~qthis is the only entry that exists now!~q)."
Print "This function will guarantee that the key provided - in this case ~qtest25~q will only have one unique entry"
Print "We know from the above test that there are currently multiple entries stored under the key ~qtest25~q - they"
Print "will be deleted by this function and replaced by the object provided in this function call."
Print "*********************************************************************************************"
Print "~n"

Hashtable.insertUniqueName( "test25", "this is the only entry that exists now!" )
objarray = HashTable.getMultipleEntries( "test25" )

For Local iter4 = 0 Until objarray.length
		Print  "The object at key ~qtest25~q:" + String(objarray[iter4])
Next

	
Print "So it is clear that insertUniqueName does indeed work."

Print "~n~n"

Print "***Scroll to the top to read all the test results!***"









'------------------------------------------------deprecated--------------------------------------------

'commented the lines below out because the objwrapper type is only used in a version of getMultipleEntries that I was testing
'but decided not to go with.
Rem
Global wrapper:objwrapper = New objwrapper
Type Objwrapper
Field objarray:Object[]
Field length:Int
EndType


'Version 3 is faster when there are few duplicates
Rem
Print "Time b. v3:" + MilliSecs()
For Local iter = 0 Until 900000
wrapper = HashTable.getMultipleEntries3( "test25" )
Next
Print "Time a. v3:" + MilliSecs()



'version 1 is faster when there are multiple duplicates

Rem
Print "Time b. v1:" + MilliSecs()
For Local iter = 0 Until 900000
objarray = HashTable.getMultipleEntries( "test25" )
Next
Print "Time a. v1:" + MilliSecs()

EndRem
'---------------------------------------------------------------------------------------------------------
