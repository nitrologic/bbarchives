; ID: 1948
; Author: Matt Merkulov
; Date: 2007-03-14 10:34:33
; Title: Image color effect
; Description: Simple linear transformation of color

;Image color effect by Matt Merkulov

Graphics 640,480,32

i = LoadImage ("image1.jpg")
DrawBlock i, 0,0

For y = 0 To 479
 For x = 0 To 639
  p = ReadPixel(x, y)
  r = p And 255
  g = (p Shr 8) And 255
  b = (p Shr 16) And 255
  r2 = (g + b) Sar 1
  g2 = (r + b) Sar 1
  b2 = (r + g) Sar 1
  WritePixel x, y, r2 + g2 Shl 8 + b2 Shl 16 - 16777216
 Next
Next

WaitKey
