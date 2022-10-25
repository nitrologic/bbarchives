; ID: 1960
; Author: Matt Merkulov
; Date: 2007-03-15 10:36:16
; Title: Deformation FX
; Description: Image FX: Constriction, whirl and ripple

;Image FX:  Constriction,  whirl and ripple by Matt Merkulov

Const xsiz = 640, ysiz = 480
Global ib, x2#, y2#, r, g, b

Const prec = 1,  bf = 0,  fx = 1

Const stp# = 1.0 / prec,  div = prec * prec

Graphics xsiz, ysiz, 32

i = LoadImage("image2.jpg")
ib = ImageBuffer(i)
For y# = 0 To ysiz - 1
 For x# = 0 To xsiz - 1
  r = 0: g = 0: b = 0
  For xx# = 0 To .9999 Step stp#
   For yy# = 0 To .9999 Step stp#
    x2# = x# + xx#
    y2# = y# + yy#
    Select fx
     Case 1;constriction
      dx# = x2# - 460.0
      dy# = y2# - 360.0
      rad# = Sqr(dx# * dx# + dy# * dy#)
      If rad# = 0 Then rad# = 1
      If rad# < 100.0 Then
       x2# = dx# * (.5 + 50.0 / rad#) + 460.0
       y2# = dy# * (.5 + 50.0 / rad#) + 360.0
      End If
     Case 2;whirl
      x2# = x2# - 320.0
      y2# = y2# - 240.0
      ang# = Sqr(x2# * x2# + y2# * y2#) * .3 - 90;"unwrapping"
    ;ang# = 360 / (Sqr(x2# * x2# + y2# * y2#) * .05 + 1);"wrapping"
      xx2# = x2#
      x2# = x2# * Cos(ang#) + y2# * Sin(ang#)
      y2# = y2# * Cos(ang#) - xx2# * Sin(ang#)
      x2# = x2# + 320.0
      y2# = y2# + 240.0
     Case 3;ripple
      dx# = x2# - 320.0
      dy# = y2# - 240.0
      ang# = Sqr(dx# * dx# + dy# * dy#) * 4
    ;x2# = x2# - Cos(ang#) * 15
      y2# = y2# - Sin(ang#) * 15
    End Select
    pixelcol
   Next
  Next
  r = r / div
  g = g / div
  b = b / div
  WritePixel x#, y#, r Shl 16 + g Shl 8 + b
 Next
Next

WaitKey: End

Function pixelcol()
If x2# < 0 Or x2# >= xsiz Or y2# < 0 Or y2# >= ysiz Then Return
xx = Int(x2# - .5)
yy = Int(y2# - .5)
p00 = ReadPixel(xx, yy, ib)
If bf Then
 p01 = ReadPixel(xx, yy + 1, ib)
 p10 = ReadPixel(xx + 1, yy, ib)
 p11 = ReadPixel(xx + 1, yy + 1, ib)
 dx2# = x2# - xx
 dx1# = 1.0 - dx2#
 r1# = dx1# * (p00 Shr 16 And 255) + dx2# * (p10 Shr 16 And 255)
 g1# = dx1# * (p00 Shr 8 And 255) + dx2# * (p10 Shr 8 And 255)
 b1# = dx1# * (p00 And 255) + dx2# * (p10 And 255)
 r2# = dx1# * (p01 Shr 16 And 255) + dx2# * (p11 Shr 16 And 255)
 g2# = dx1# * (p01 Shr 8 And 255) + dx2# * (p11 Shr 8 And 255)
 b2# = dx1# * (p01 And 255) + dx2# * (p11 And 255)
 dx2# = y2# - yy
 dx1# = 1.0 - dx2#
 r = r + Int(dx1# * r1# + dx2# * r2#)
 g = g + Int(dx1# * g1# + dx2# * g2#)
 b = b + Int(dx1# * b1# + dx2# * b2#)
Else
 r = r + (p00 Shr 16 And 255)
 g = g + (p00 Shr 8 And 255)
 b = b + (p00 And 255)
End If
End Function
