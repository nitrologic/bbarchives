; ID: 1947
; Author: Matt Merkulov
; Date: 2007-03-14 10:32:01
; Title: Color2Grayscale
; Description: Turning color image into grayscale one

;Color2Grayscale by Matt Merkulov

Graphics 640,480,32

i=LoadImage ("image1.jpg")
DrawBlock i, 0,0

For y = 0 To 479
 For x = 0 To 639
  p = ReadPixel(x, y)
  b = p And 255
  g = (p Shr 8) And 255
  r = (p Shr 16) And 255
  c = .3 * r + .59 * g + .11 * b
  WritePixel x, y, c + c Shl 8 + c Shl 16 - 16777216
 Next
Next

WaitKey
