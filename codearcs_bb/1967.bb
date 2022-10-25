; ID: 1967
; Author: Matt Merkulov
; Date: 2007-03-15 11:54:58
; Title: Flood fill
; Description: Simple flood fill (like QB Paint command) algorhitm example

;Simple flood fill (like QB Paint command) algorhytm example by Matt Merkulov

Type paint
 Field x, y, dir, move
End Type

Type paint2
 Field x1, x2, y
End Type

Const rad = 48 ^ 2

Graphics 640, 480

Global ib, ib2, p, r, g, b

i = LoadImage("image3.jpg")
ib = ImageBuffer(i)
DrawBlock i, 0, 0
LockBuffer ib

i2 = CreateImage(640, 480)
ib2 = ImageBuffer(i2)
Global zero = ReadPixel(0, 0, ib2)
LockBuffer ib2

For x = 0 To 639
 For y = 0 To 479
 p = ReadPixelFast(x, y, ib)
 r = (p Shr 16) And 255
 g = (p Shr 8) And 255
 b = p And 255
 paint x, y
 Next
Next
WaitKey

Function checkpixel(x, y)
If x < 0 Or x >= 640 Or y < 0 Or y >= 480 Then Return
If ReadPixelFast(x, y, ib2) <> zero Then Return
;Calculating difference of colors
pp = ReadPixelFast(x, y, ib)
r2 = r - ((pp Shr 16) And 255)
g2 = g - ((pp Shr 8) And 255)
b2 = b - (pp And 255)
rr = r2 * r2 + g2 * g2 + b2 * b2
If rr > rad Then Return
p1.paint = New paint
p1\x = x
p1\y = y
WritePixel x, y, p
;Marking pixel in auxillary buffer
WritePixelFast x, y,  - 1, ib2
End Function

Function paint(x, y)
checkpixel x, y
Repeat
 p1.paint = First paint
 If p1 = Null Then Exit
 x = p1\x
 y = p1\y
 ;Checking adjacent pixels
 checkpixel x + 1, y
 checkpixel x - 1, y
 checkpixel x, y - 1
 checkpixel x, y + 1
 Delete p1
Forever
End Function
