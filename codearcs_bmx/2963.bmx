; ID: 2963
; Author: Pineapple
; Date: 2012-07-27 14:32:56
; Title: Null-terminated string read/write
; Description: Read and write null-terminated strings in a stream

' 	--+-----------------------------------------------------------------------------------------+--
'	  |   This code was originally written by Sophie Kirschner (sophiek@pineapplemachine.com)   |  
' 	  | It is released as public domain. Please don't interpret that as liberty to claim credit |  
' 	  |   that isn't yours, or to sell this code when it could otherwise be obtained for free   |  
'	  |                because that would be a really shitty thing of you to do.                |
' 	--+-----------------------------------------------------------------------------------------+--

Rem
bbdoc: Write a string to a stream.
EndRem
Function WriteNullString(f:TStream,str$)
	Assert Instr(str,Chr(0))=0,"Cannot write a string containing a null character."
	WriteString f,str
	WriteByte f,0
End Function

Rem
bbdoc: Read a string from a stream.
returns: The string that was read.
EndRem
Function ReadNullString$(f:TStream)
	Local ret$=""
	Repeat
		Assert Not Eof(f),"Failed to read null-terminated string."
		Local b%=ReadByte(f)
		If b=0 Then Return ret
		ret:+Chr(b)
	Forever
End Function
