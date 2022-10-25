; ID: 3131
; Author: Pineapple
; Date: 2014-06-13 03:29:05
; Title: TStringStream
; Description: String streams: Allows reading and writing data in strings using an extension of TStream

' 	--+-----------------------------------------------------------------------------------------+--
'	  |   This code was originally written by Sophie Kirschner (sophiek@pineapplemachine.com)   |  
' 	  | It is released as public domain. Please don't interpret that as liberty to claim credit |  
' 	  |   that isn't yours, or to sell this code when it could otherwise be obtained for free   |  
'	  |                because that would be a really shitty thing of you to do.                |
' 	--+-----------------------------------------------------------------------------------------+--



SuperStrict

Import brl.stream

Rem

' Example code

Import brl.standardio

Local data$=	"Solving the following riddle will reveal the awful secret behind the universe, "+ ..
			"assuming you do not go utterly mad in the attempt. If you already happen to know "+..
			"the awful secret behind the universe, feel free to skip ahead."

' Read from the data variable as though it were a stream
Print "~nResult of opening a string stream with data string and applying ReadLine():~n"
Local stream:TStringStream=TStringStream.Create(data)
Print ReadLine(stream)
	
If TestTStringStream()
	
	' Write over a few bytes
	Print "~nNow overwriting the first several bytes of the string stream..."
	SeekStream(stream,0)
	For Local i%=0 Until 16
		WriteByte stream,Asc("A")+i
	Next
	
	' Print the string as it now exists
	Print "~nThis is what the string looks like after writing some stuff to it:~n"
	Print data

	' Demonstrate how bugs can be made if you're not careful with which strings you're writing to
	Print "~nNow testing buggy behavior of writing to the stream."
	data="Hello World"
	stream.open(data)
	Print "This is what the new string looks like before tampering with it: ~q"+data+"~q"
	WriteByte(stream,Asc("Y"))
	Print "This is what the string looks like after tampering with it: ~q"+data+"~q"
	Local hiworld$="Hello World"
	Print "This is what the string literal ~qHello World~q now points to when assigned to another variable: "+hiworld
	hiworld="Hello "+"World"
	Print "This is what the string literal ~qHello ~q + ~qWorld~q now points to when assigned to another variable: "+hiworld
Else

	Print "~nWhat do you know? Writing data isn't going to work out-of-the-box. Check the ~qabout~q bit of the Write method to see about fixing it for your particular compiler."
		
EndIf

EndRem


' Here's a handy function that will return true if the TStringStream Write method is going to function like it's supposed to.
' Contents of teststring doesn't really matter, just maybe use 8 or so characters at the very least.
' Returns true if everything looks good, false otherwise.
Function TestTStringStream%(teststring$="This is a test")
	Local dataptr@@ Ptr=Short Ptr(Int Ptr(Varptr teststring)[0])+6
	For Local i%=0 Until teststring.length
		If dataptr[i]<>teststring[i] Return False
	Next
	Return True
End Function

Rem
bbdoc: String stream type
about:
Useful if you'd like to use the same code for reading data from a string as reading from
another stream, such as a file stream. This class does not support writing past EOF.
End Rem
Type TStringStream Extends TStream
	Field source$,position%
	
	Rem
	bbdoc: Create a stream based on a #String object
	End Rem
	Function Create:TStringStream(source$)
		Local stream:TStringStream=New TStringStream
		stream.source=source
		stream.position=0
		Return stream
	End Function
	
	Rem
	bbdoc: Have the stream read a different #String object
	End Rem
	Method Open(openstring$)
		Flush
		source=openstring
	End Method
		
	Rem
	bbdoc: Get stream end of file status
	returns: True for end of file reached, otherwise False
	End Rem
	Method Eof%()
		Return position>=source.length
	End Method
	
	Rem
	bbdoc: Get position of seekable stream
	returns: Stream position as a byte offset
	End Rem
	Method Pos%()
		Return position
	End Method
	
	Rem
	bbdoc: Get size of seekable stream
	returns: Size, in bytes, of seekable stream
	End Rem
	Method Size%()
		Return source.length
	End Method
	
	Rem
	bbdoc: Seek to position in seekable stream
	returns: New stream position
	End Rem
	Method Seek%(pos%)
		position=pos
		Return position
	End Method
	
	Rem
	bbdoc: Flush stream
	about:
	Flushes any internal stream buffers.
	End Rem
	Method Flush()
		source=Null
		position=0
	End Method
	
	Rem
	bbdoc: Close stream
	about:
	Closes the stream after flushing any internal stream buffers.
	End Rem
	Method Close()
		Flush
	End Method
	
	Rem
	bbdoc: Read at least 1 byte from a stream
	returns: Number of bytes successfully read
	about:
	If this method returns 0, the stream has reached end of file.
	End Rem
	Method Read%(buf@ Ptr,count%)
		If position+count>source.length
			count=source.length-position
			If count<=0 Return 0
		EndIf
		For Local i%=0 Until count
			buf[i]=source[position]
			position:+1
		Next
		Return count
	End Method
	
	Rem
	bbdoc: Write at least 1 byte to a stream
	returns: Number of bytes successfully written
	about:
	If this method returns 0, the stream has reached end of file.
	Note that this method depends on the structure of the BBString data
	type (see mod\brl.mod\blitz.mod\blitz_string.h) being the same
	across all platforms and versions. Which probably isn't going to
	happen. Chances are you'll have to tweak this method to cooperate
	with your particular BlitzMax compiler. For the record, I wrote
	this particular implementation for Blitzmax 1.50 on Windows 8.
	If you're having trouble I recommend simply checking the data
	located at the pointer pointed at by a string's pointer (yo dawg)
	and finding where the characters start in relation to it. Also
	beware the odd behavior that can result from overwriting the
	contents of a BBString. Writing over a string doesn't just
	change that string, it essentially causes future strings that
	would point to the same unaltered literals to point to the altered
	data instead. (Since technically they're one and the same after
	writing over stuff.)
	End Rem
	Method Write%(buf@ Ptr,count%)
		If position+count>source.length
			count=source.length-position
			If count<=0 Return 0
		EndIf
		' This next line might warrant an explanation. Here it is broken down into its constituent parts with each step explained:
		'	Local dataptr@@ Ptr=						This part's simple. The location of the source string's data belongs in this variable as a pointer to a short.
		'	Short Ptr(                           )			Convert the integer about to be grabbed from the String to the location of its corresponding BBString struct.
		'	           Int Ptr(Varptr source)[0]			Actually grab the pointer to that struct, which defined by the integer at the location of the string's pointer
		'	                                       +6			This just happens to be the amount of string-related data preceding the struct's array of BBChars.
		'	                                          +position	Finally, just add the position in the string stream to the location of the pointer.
		Local dataptr@@ Ptr=Short Ptr(Int Ptr(Varptr source)[0])+6+position
		For Local i%=0 Until count
			dataptr[i]=buf[i]
			position:+1
		Next
		Return count
	End Method
End Type
