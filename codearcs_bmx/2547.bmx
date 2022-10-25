; ID: 2547
; Author: BlitzSupport
; Date: 2009-07-25 10:54:28
; Title: Using NTFS Alternative Data Streams
; Description: Spooky hidden text/data!

' Change this filename to one on your system! Either create an empty file on your system (on
' an NTFS-formatted drive), or use any existing (unimportant) file...

f$ = "test.txt"

stream$ = "StreamTest"

stream$ = ":" + stream$

Print ""
Print "File size: " + FileSize (f$)

file = ReadFile (f$ + stream$)

If file

	Print ""

	' Read from alternative data stream...
	
	While Not Eof (file)
		Print "Found ~q" + ReadLine (file) + "~q in alternative data stream!"
	Wend

	CloseFile file
	
EndIf

' Now to write a new data stream...

file = WriteFile (f$ + stream$)

If file

	Print ""

	' Write to alternative data stream...
	
	' Don't try *pasting* anything into the IDE output window, as a little
	' quirk means that although it looks like you've pasted text, it's only
	' gone into the output window's display. Input just receives Enter when
	' you press it, so you get an empty string!
	
	WriteLine file, Input ("Enter some text: ")
	CloseFile file
	
EndIf
	
Print ""
Print "Done!"

Print ""
Print "File size is still: " + FileSize (f$)

Print ""
Print "Now open the file in a text/hex editor to verify that it's empty,"
Print "then run the program again to see your text in the zero-byte file!"

End
