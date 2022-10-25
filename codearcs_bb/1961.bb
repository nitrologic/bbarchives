; ID: 1961
; Author: Matt Merkulov
; Date: 2007-03-15 10:43:30
; Title: Image warping
; Description: Free image transformation - smooth transition of points

;Free image transformation - point warping by Matt Merkulov

;Controls:
; Drag mouse with left button pressed - from initial point position to resulting
; Press right mouse button to render picture

Type dot
 Field x#, y#, dx#, dy#
End Type

Graphics 640, 480, 32, 2

i = LoadImage("image2.jpg")
DrawBlock i, 0, 0

For x = 0 To 639 Step 639
 For y = 0 To 479 Step 479
  d.dot = New dot
  d\x = x
  d\y = y
 Next
Next

Repeat
 If MouseDown(1) And md = 0 Then
  md = 1
  d.dot = New dot
  d\x = MouseX()
  d\y = MouseY()
 End If
 If MouseDown(1) = 0 And md Then
  d\dx = d\x - MouseX()
  d\dy = d\y - MouseY()
  Line d\x, d\y, MouseX(), MouseY()
  md = 0
 End If
 If MouseDown(2) Then Exit
Forever

For y = 0 To 479
 For x = 0 To 639
  vx# = 0
  vy# = 0
  k# = 0
  For d = Each dot
   dx# = x - d\x
   dy# = y - d\y
   r# = 1.0 / (dx# * dx# + dy# * dy# + .001)
   vx# = vx# + r# * d\dx
   vy# = vy# + r# * d\dy
   k# = k# + r#
  Next
  vx# = vx# / k#
  vy# = vy# / k#
  WritePixel x, y, ReadPixel(x + vx#, y + vy#, ImageBuffer(i))
 Next
Next
WaitKey
