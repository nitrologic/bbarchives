; ID: 881
; Author: MuffinRemnant
; Date: 2004-01-11 02:41:08
; Title: QuoteLint
; Description: Simple debugging utility

; Name: quotelint.bb

; Author: MuffinRemnant

; Date: 11/01/2004

; Description: A small utility to check a given source file
; for mis-matched quotes - it just checks there are an even
; number of quotes on each source line.

; Notes: Blitz will accept unterminated strings in sourcecode
; (for example a$ = "Hello World ) that can cause unpredictable
; and difficult to trace errors. Run your source file through this
; simple utility to check everything is in order.
; Run the program as it stands to see a demonstration - it'll pick up
; the errors in the data statements at the end of the program and the
; Hello World example above.
;
; Needs a filepicker to deal with multiple files etc.
; and should write the errors out to a file rather than the screen.


Global iLineNumber = 0, iErrorCount = 0

;insert your .bb filename in the following line...
fileSourceCode = ReadFile("quotelint.bb")


Print "List of mis-matched quotes:"
Print

While Not Eof(fileSourceCode)

	strSourceLine$ = ReadLine(fileSourceCode)
	
	If parse_line(strSourceLine) Then
		
		Print "Line " + iLineNumber + ": " + strSourceLine
		iErrorCount = iErrorCount + 1
	
	EndIf
	iLineNumber = iLineNumber + 1

Wend

Print
If iErrorCount = 0 Then Print "None" Else Print iErrorCount + " line(s) with mismatched quotes"
WaitKey()
End


Function parse_line(strText$)


	Local iCounter = 0, iLoop

	For iLoop = 1 To Len(strText$)
	
		If Mid$(strText$, iLoop, 1) = Chr$(34) Then iCounter = iCounter + 1
	
	Next

	If iCounter And 1 Then Return True Else Return False


End Function


; these are just a couple of errors 
Data "I am an error
Data "I am not"
Data "I am too!
