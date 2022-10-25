; ID: 562
; Author: Chroma
; Date: 2003-01-30 12:33:50
; Title: Get what's on the other side of the Equal sign.
; Description: Fast value extractor.

Function ReadMeat$(txt$)
length=Len(txt)
For i=1 To length
     a$=Mid$(txt,i,1)
     If a$="=" Return Mid$(txt,i+1,length-i)
Next
End Function
