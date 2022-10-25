; ID: 662
; Author: podperson
; Date: 2003-04-26 15:05:09
; Title: Text and Data Utilities
; Description: Useful code for dealing with text and data

; NOTE cannot assign function results to constants
global TAB = chr(9)
global CR = chr(13)

; TEXT UTILITIES
.TextParsing

; Given a string and a delimiter tells you how many fields there are in it
Function CountFields(s$, delim$)
	o = 1
	c = 0
	While o > 0
		o = o + 1
		o = Instr(s, delim, o)
		c = c + 1
	Wend
	Return c
End Function

; Given a string, a delimiter, and a n -- returns the nth field
Function NthField$(s$, delim$, n)
	o = 1
	For i = 1 To n - 1
		o = Instr(s, delim, o)
		If o = 0 Then
			Return ""
		End If
		o = o + 1
	Next
	p = Instr(s, delim, o)
	If p = 0 Then
		Return Mid(s, o)
	Else
		Return Mid(s, o, p - o)
	End If
End Function

; SOUP UTILITIES
; A soup is a string that is being treated as a tagged array
; With some minor limitations you can just shove named properties into
; it and retrieve them later. The restriction: no TAB or CR in the name of
; a property or its content.
;
; Modifying this code to allow TAB and CR in properties would be pretty
; trivial BUT incur a (slight) performance hit.
.Soups

; Soups are simply tagged arrays; stick any string you like in them and get it back using
; the name you used.

; Set a value (replacing the existing value if necessary)
; Function returns TRUE if a value of that name already existed (and was replaced)
; in case that's useful...
Function SetProp(s$, label$, value$)
	o = Instr(s, CR + label + TAB)
	If o = 0 Then
		; APPEND THE VALUE
		s = s + CR + label + TAB + value
		Return s
	Else
		p = Instr(s, CR, o + Len(label) + 2)
		If p = 0 Then
			; CHOP AND APPEND
			s = Left(s, o) + label + TAB + value
		Else
			; SPLICE
			s = Left(s, o) + label + TAB + value + Mid(s, p)
		End If
		Return s
	End If	
End Function

; Retrieve a value from the soup if it exists; if not an empty string is returned
Function GetProp$(s$, label$)
	o = Instr(s, CR + label + TAB)
	v$ = ""
	If o > 0 Then
		o = o + Len(label) + 2
		p = Instr(s, CR, o)
		If p = 0 Then
			v = Mid(s, o)
		Else
			v = Mid(s, o, p - o)
		End If
	End If
	Return v
End Function 

; TEXT FILES
.textfiles

Function LoadText$( file$ )
	t$ = ""
	
	If FileType( file ) = 0 Then
		DebugLog "File: " + file + " does Not exist!"
		Return ""
	End If
	
	filein = ReadFile( file )
	While Not Eof(filein)
		t = t + ReadLine(filein) + Chr(13)
	Wend
	CloseFile filein
	
	Return t
End Function

Function SaveText( file$, content$ )
	Select FileType( file )
	Case 1
		DeleteFile file
	Case 2
		DebugLog "File: " + file + " could not be written. A directory of that name exists."
		Return
	End Select
	
	fileout = WriteFile ( file )
	WriteLine fileout, content
	CloseFile fileout
End Function
