; ID: 2236
; Author: elyobo
; Date: 2008-04-02 00:48:56
; Title: ARGB type
; Description: pixel to colour bytes

Strict

' Type ARGB permits conversion from individual colour bytes to colour pixel and back
' All original code by codename El Yobo. 
' Free to use by any lifeform in the solar system or beyond.. NO GUARANTEES - it works for me.
' I guess multiplication by 256 also works..
' For BGRA just swap all the reds and blues - I think??!

Type ARGB
Field PColPixel:Int
Field PRed:Byte
Field PGreen:Byte
Field PBlue:Byte
Field PAlpha:Byte

Function MakeARGBc:ARGB(pixred:Byte, pixgreen:Byte, pixblue:Byte, pixalpha:Byte)
Local myARGB:ARGB = New ARGB
Local col:Int = Int pixred
col = col Shl 8
Local intnum:Int = Int  pixgreen
col = col~intnum
col = col Shl 8
intnum:Int = Int  pixblue
col = col~intnum
col = col Shl 8
intnum:Int = Int pixalpha
col = col~intnum
myARGB.PColPixel = col
myARGB.PRed = pixred
myARGB.PGreen = pixgreen
myARGB.PBlue = pixblue
myARGB.PAlpha = pixalpha

Return myARGB
End Function

Function MakeARGBp:ARGB(pixcolpixel:Int)
Local myARGB:ARGB = New ARGB
Local buf:Byte = 255
Local intnum:Int = 0
intnum = Int buf
myARGB.PAlpha = Byte (pixcolpixel&intnum)
myARGB.PBlue = Byte ((pixcolpixel Shr 8)&intnum)
myARGB.PGreen = Byte((pixcolpixel Shr 16)&intnum)
myARGB.PRed = Byte((pixcolpixel Shr 24)&intnum)
myARGB.PColPixel = pixcolpixel
Return myARGB
EndFunction 

EndType

'In application..

Graphics 640, 480

Local colourX:ARGB

colourX = ARGB.MakeARGBc(34, 32, 234, 255)

Local pxmp:TPixmap = CreatePixmap(20, 20, PF_RGBA8888)

For Local i = 0 To 19
For Local j = 0 To 19

WritePixel(pxmp, i, j, colourX.PColPixel)

Next
Next

DrawPixmap(pxmp, 200, 200)
Flip
WaitKey()

Local colourY:ARGB = ARGB.MakeARGBp(234*23*123*245)

Print colourY.PRed
Print colourY.PGreen
Print colourY.PBlue
Print colourY.PAlpha

'and of course..

colourX = ARGB.MakeARGBp(colourX.PColPixel)

Print colourX.PRed
Print colourX.PGreen
Print colourX.PBlue
Print colourX.PAlpha


End
