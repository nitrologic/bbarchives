; ID: 245
; Author: Curtastic
; Date: 2002-04-25 03:53:39
; Title: faster bin to int :D
; Description: converts a binary string to an integer

Function bin2int(b$)
    blen=Len(b)
    For f=1 To blen
        n=n Shl 1 + (Mid(b,f,1)="1")
    Next
    Return n
End Function
