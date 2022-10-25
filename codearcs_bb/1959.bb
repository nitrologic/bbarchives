; ID: 1959
; Author: Matt Merkulov
; Date: 2007-03-15 10:31:12
; Title: Image deformation
; Description: Simple deformation with bilinear filtering

;Simple deformation with bilinear filtering by Matt Merkulov

Const xsiz = 640, ysiz = 480
Global ib, x2#, y2#, r, g, b

;Const fx = 1, prec = 1, bf = 0; Pixelization
;Const fx = 2, prec = 1, bf = 0; Blocky

;Const fx = 1, prec = 1, bf = 1
;Const fx = 2, prec = 4, bf = 0
;Const fx = 3, prec = 2, bf = 1
Const fx = 4, prec = 2, bf = 1

Const stp# = 1.0 / prec, div = prec * prec

Graphics xsiz, ysiz, 32

i = LoadImage("image2.jpg")
ib = ImageBuffer(i)
For y# = 0 To ysiz - 1
 For x# = 0 To xsiz - 1
 r = 0:g = 0:b = 0
 For xx# = 0 To .9999 Step stp#
  For yy# = 0 To .9999 Step stp#
   x2# = x# + xx#
   y2# = y# + yy#
   move -320, -240
   Select fx
    Case 1
     scale .33, .33
     move 200, 100
    Case 2
     scale 2, 3
     x2# = -x2#; mirror relative to OY
    Case 3
     rotate 30
    Case 4
     scale 2, 2
     rotate 10
     bevel .4, .4
   End Select
   move 320, 240
   pixelcol
  Next
 Next
 r = r / div
 g = g / div
 b = b / div
 WritePixel x#, y#, r Shl 16 + g Shl 8 + b
 Next
Next

WaitKey:End

Function pixelcol()
If x2# < 0 Or x2# >= xsiz Or y2# < 0 Or y2# >= ysiz Then Return
xx = Int(x2# - .5)
yy = Int(y2# - .5)
p00 = ReadPixel(xx, yy, ib)
If bf Then
 p01 = ReadPixel(xx, yy+1, ib)
 p10 = ReadPixel(xx+1, yy, ib)
 p11 = ReadPixel(xx+1, yy+1, ib)
 dx2# = x2# - xx
 dx1# = 1.0 - dx2#
 r1# = dx1# * (p00 Shr 16 And 255) + dx2# * (p10 Shr 16 And 255)
 g1# = dx1# * (p00 Shr 8 And 255) + dx2# * (p10 Shr 8 And 255)
 b1# = dx1# * (p00 And 255) + dx2# * (p10 And 255)
 r2# = dx1# * (p01 Shr 16 And 255) + dx2# * (p11 Shr 16 And 255)
 g2# = dx1#*(p01 Shr 8 And 255) + dx2#*(p11 Shr 8 And 255)
 b2# = dx1# * (p01 And 255) +dx2# * (p11 And 255)
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

; Shift
Function move(dx#, dy#)
x2# = x2# + dx#
y2# = y2# + dy#
End Function

; Scaling
Function scale(mx#, my#)
x2# = x2# * mx#
y2# = y2# * my#
End Function

; Turn
Function rotate(ang#)
xx2# = x2#
x2# = x2# * Cos(ang#) + y2# * Sin(ang#)
y2# = y2# * Cos(ang#) - xx2# * Sin(ang#)
End Function

; Mirror relative to a point
Function mirrordot(x#, y#)
x2# = 2 * x# - x2#
y2# = 2 * y# - y2#
End Function

; Mirror relative to a straight line
Function mirrorline(a#, b#, c#)
c2# = a# * x2# - b# * y2#
k# = 1.0 / (a# * a# + b# * b#)
x2# =(-a# * c# - b# * c2#) * k#
y2# =(a# * c2# - b# * c#) * k#
End Function

; An inclination
Function bevel(dx#, dy#)
xx2# = x2#
x2# = x2# + y2# * dx#
y2# = y2# + xx2# * dy#
End Function
