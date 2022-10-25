; ID: 1949
; Author: Matt Merkulov
; Date: 2007-03-14 10:36:51
; Title: Color replacement - distance
; Description: Replacing one color with another with smooth transition between colors using distance from current pixel color to choosen assuming them as 3D points (r,g,b)

;Color replacement - distance by Matt Merkulov

Graphics 640,480,32

i = LoadImage ("image1.jpg")
DrawBlock i, 0,0

; What color is to be replaced
r1=224
g1=224
b1=0
; What color is to be replaced with
r2=64
g2=64
b2=255
; Radius
rad# = 128

For y = 0 To 479
 For x = 0 To 639
  ; Decompositing color on components
  p = ReadPixel (x, y)
  b = p And 255
  g = (p Shr 8) And 255
  r = (p Shr 16) And 255
  ; Distance between initial color and replaced one
  d# = Sqr((r - r1) * (r - r1) + (g - g1) * (g - g1) + (b - b1) * (b - b1))
  ; Checking is the current color is inside of sphere
  If d# <= rad# Then
   ; Calculating factors
   d1# = d#/rad#
   d2# = 1-d1#
   ; Components' intensivity values
   r = Int (d1# * r + d2# * r2)
   g = Int (d1# * g + d2# * g2)
   b = Int (d1# * b + d2# * b2)
  End If
  ; Writing pixel
  WritePixel x, y, b + (g Shl 8) + (r Shl 16)
 Next
Next

WaitKey
