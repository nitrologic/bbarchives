; ID: 2730
; Author: Luke111
; Date: 2010-06-13 10:10:50
; Title: Tough Binary Encryption
; Description: Good Luck Making A Decoder

toen$ = Input$("> ")
encbinenc(toen$)
Function encbinenc(toencrypt$)
writer = WriteFile("encrypted.txt")
For a = 1 To Len(toencrypt$) Step 1
b$ = Mid(toencrypt$,a,1)
c = Asc(b$)
d$ = Bin(c)
For e = 1 To Len(d$) Step 1
	f$ = Mid(d,e,1)
	l = Asc(f$)
	g$ = Hex(l)
	For h = 1 To Len(g$) Step 1
		i$ = Mid(g$,h,1)
		j = Asc(i$)
		k$ = Bin(j)
		WriteLine writer,k
	Next
Next
Next
CloseFile(writer)
reader = ReadFile("encrypted.txt")
While Not Eof(reader)
Print ReadLine(reader)
Wend
CloseFile(reader)
Return 1
End Function
