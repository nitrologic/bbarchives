; ID: 1306
; Author: n8r2k
; Date: 2005-03-03 16:50:55
; Title: Hyper Screen Fader
; Description: Currently Uncommented

Graphics 1024,768,0,1
n =0
n2 = 0
n1 = 0
While Not KeyHit(1)
Cls
n = n + 3

If n1 = 0
	ClsColor n,0,0
	n2 = 0
ElseIf n1 = 1
	ClsColor 0,n,0
	n2 = 0
ElseIf n1 = 2
	ClsColor 0,0,n
	n2 = 0
ElseIf n1 = 3
	ClsColor n,0,n
	n2 = 0
ElseIf n1 = 4
	ClsColor n,n,0
	n2 = 0
ElseIf n1 = 5
	ClsColor 0,n,n
	n2 = 0
EndIf

If n > 250
	n= 0
	n1 = n1 + 1
EndIf

If n1 = 6
	n1 = 0
EndIf

Flip
Wend
