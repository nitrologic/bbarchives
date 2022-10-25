; ID: 2915
; Author: TAS
; Date: 2012-02-02 21:23:30
; Title: Text extractor
; Description: Extracts all text inside quotes

;Use this code to extract all the text enclosed in quotes from a series of 
;bb files to a single text file so it can be spelled checked.

;create output file
f2=WriteFile("Game text.txt")
If f2=0 Then End		;abort on fail

Repeat 
	fn$=RequestFile("Select File for processing","bb")
	If Len(fn)=0 Then Exit ;Done
	
	f=ReadFile(fn)	
	If f=0 Then Exit ;done
	r$=";********** "+fn+Chr(13)	;save for first line of output file
	WriteLine f2,r$	;title line
	n=0
	While Not Eof(f)
		n=n+1	;line number
		s$=ReadLine(f)			;read next line of text
		k=Instr(s,Chr(34))	;check for quote mark
		
		;if quote mark found process line to close quote
		While k>0 
			j=Instr(s,Chr(34),k+1)	;find next quote mark right of quote at position k
			If j=0 Then j=Len(s)		;if none take remaining line as quote
			Print Str(n)+Chr(9)+Mid(s,k+1,j-k-1)	;output line # and text within quotes to screen
			WriteLine f2,Str(n)+Chr(9)+Mid(s,k+1,j-k-1)+Chr(13)	;to file
			If j=Len(s) Then s="" Else s=Right(s,Len(s)-j-1)		;strip text up to second quote from string
			k=Instr(s,Chr(34))	;check for a 3rd, 5th, ... quote mark in string
		Wend			
	Wend
	CloseFile(f)
	m=m+n
Forever	

CloseFile(f2)
Notify "Lines checked "+m
End
