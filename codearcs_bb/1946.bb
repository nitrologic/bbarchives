; ID: 1946
; Author: Matt Merkulov
; Date: 2007-03-13 12:51:28
; Title: Image color FX
; Description: Image color management procedures

;Image color FX by Matt Merkulov

Const k# = 180.0 / 255, k2# = 360.0 / 255, fx = 2
Dim col(2)

Graphics 640,480,32

i=LoadImage ("image1.jpg")
DrawBlock i, 0,0
For y=0 To 479
 For x=0 To 639
 p = ReadPixel (x, y)
 For n = 0 To 2
  c = p And 255
  p = p Shr 8
  Select fx
  Case 1; Negative
   c = 255 - c
  Case 2; Darkening
   c = c Shr 1
  Case 3; Lightening
   c = 128 + (c Sar 1)
  Case 4; Threshold
   c = 255 * (c >= 128)
  Case 5; Saturation reduction
   c = 64 + (c Sar 1)
  Case 6; Saturation increase
   c = c Shl 1 - 128
   If c < 0 Then c = 0
   If c > 255 Then c = 255
  Case 7; Square-law darkening
   c = 255 - Sqr(255 * 255 - c * c)
  Case 8; Square-law lightening
   c = 255 - c
   c = Sqr(255 * 255 - c * c)
  Case 9; Colors quantity reduction
   c = Int(c / 51) * 51
  Case 10; Saw
   c = (c Shr 6) * 4
  Case 11; Sawtooth
   If c And 64 Then c = 255 - (c And 63) * 4 Else c = (c And 63) * 4
  Case 12; Hollow
   c = 255 * (1 - Sin(c * k#))
  Case 13; Ledge
   c = 255 * Sin(c * k#)
  Case 14; Bend
   c = Sin(c * k2#) * 127 + c
  End Select
  col(n) =c
 Next
 WritePixel x, y, col(0) + col(1) Shl 8 + col(2) Shl 16 - 16777216
 Next
Next

WaitKey
