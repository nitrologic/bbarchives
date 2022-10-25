; ID: 1966
; Author: Matt Merkulov
; Date: 2007-03-15 11:50:57
; Title: Image FX - neighbour pixels
; Description: Blur, diffusion, emboss image FX with parameters

;Blur, diffusion, emboss image FX by Matt Merkulov

;Const wid = 2, fx = 1
;Const wid = 2, fx = 2
Const wid = 2, fx = 3

Const k1 = (wid * 2 + 1) ^ 2, k2 = (wid + 1) ^ 2 - 1

Graphics 640,480,32

i = LoadImage("image3.jpg")
DrawBlock i, 0,0

ib = ImageBuffer(i)
LockBuffer ib

Color 0,0,0
k3 = 1
For n = 0 To wid - 1
 Rect n, n, 640 - n * 2,480 - n * 2,0
 k3 = k3 + (n + 2) * (n + 2)
Next

For y = wid To 479 - wid
 For x = wid To 639 - wid
 r = 0:g = 0:b = 0
 Select fx
  Case 1;Blur
  For xx = -wid To wid
   For yy = -wid To wid
   p = ReadPixelFast(x + xx, y + yy, ib)
   r = r + ((p Shr 16) And 255)
   g = g + ((p Shr 8) And 255)
   b = b + (p And 255)
   Next
  Next
  WritePixel x, y, Int(b / k1) + Int(g / k1) Shl 8 + Int(r / k1) Shl 16
  Case 2;Diffusion
  WritePixel x, y, ReadPixelFast(x + Rand(-wid, wid), y + Rand(-wid, wid), ib)
  Case 3;Emboss
  k = 0
  For xx = -wid To 0
   For yy = -wid To 0
   p = ReadPixelFast(x + xx, y + yy, ib)
   r = (p Shr 16) And 255
   g = (p Shr 8) And 255
   b = p And 255
   c = .35 * r + .45 * g + .2 * b
   If xx + yy = 0 Then k = (c - k / k2 + 255) Sar 1 Else k = k + c
   Next
  Next
  If k < 0 Then k = 0
  If k > 255 Then k = 255
  WritePixel x, y, k * 65793
 End Select
 Next
Next
WaitKey
