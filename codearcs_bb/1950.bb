; ID: 1950
; Author: Matt Merkulov
; Date: 2007-03-14 10:43:56
; Title: Color replacement - proportions
; Description: Replacing one color with another with smooth transition between colors using proportions

;Color replacement - proportions by Matt Merkulov

Graphics 640,480,32

i = LoadImage("image1.jpg")
DrawBlock i, 0,0

; Color to be replaced
r1# = 255
g1# = 64
b1# = 64
; Calculating its proportional coefficients
s1 = r1# + g1# + b1#
kr1# = r1# / s1
kg1# = g1# / s1
kb1# = b1# / s1
; Color to replace with
r2# = 0
g2# = 0
b2# = 255
; Calculating its proportional coefficients
s2=r2# + g2# + b2#
r2# = r2# / s2
g2# = g2# / s2
b2# = b2# / s2
; An admissible difference of factors
skmax# =.5

For y = 0 To 479
 For x = 0 To 639
 ; Decompositing color on components
 p = ReadPixel(x, y)
 b# = p And 255
 g# = (p Shr 8) And 255
 r# = (p Shr 16) And 255
 ; Calculating proportion coefficients of initial color
 s = r# + g# + b#
 kr# = r# / s
 kg# = g# / s
 kb# = b# / s
 sk# = Abs (kr1# - kr#) + Abs (kg1# - kg#) + Abs (kb1# - kb#)
 ; Check on an admissible difference of coefficients
 If sk# <= skmax# Then
  ; Calculating coefficients for mixing
  sk1# = sk# / skmax#
  sk2# = (1 - sk1#)*s
  ; Intensivity components' values
  rr = Int (sk1#*r+sk2#*r2#)
  If rr < 0 Then rr = 0 Else If rr > 255 Then rr = 255
  gg = Int (sk1# * g + sk2# * g2#)
  If gg < 0 Then gg = 0 Else If gg > 255 Then gg = 255
  bb = Int (sk1# * b + sk2# * b2#)
  If bb < 0 Then bb = 0 Else If bb > 255 Then bb = 255
  ; Drawing pixel
  WritePixel x, y, bb + (gg Shl 8) + (rr Shl 16)
 End If
 Next
Next

WaitKey
