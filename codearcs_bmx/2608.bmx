; ID: 2608
; Author: Enyaw
; Date: 2009-11-04 19:24:02
; Title: Isometric mouse position
; Description: calculate mouse position on a Isometric grid

Const offsetx = 416
Const offsety = 0

Global xs = 16
Global ys = 8

Function px(x:Int, y:Int, z:Int)
	xp = offsetx - 16 + (x - y) Shl 4
	Return xp
End Function

Function py(x:Int, y:Int, z:Int)
	yp = offsety + (x + x + y + y - z) Shl 2
	Return yp
End Function

Function Iso(x:Float, y:Float, xs:Float, ys:Float)
	Local p:Float[] = [x + 0 * xs, y + 1 * ys, x + 1 * xs, y + 0 * ys, x + 1 * xs, y + 0 * ys, x + 2 * xs, y + 1 * ys, x + 2 * xs, y + 1 * ys, x + 1 * xs, y + 2 * ys]
	DrawPoly p
End Function

Graphics 800, 600

Repeat
Cls
xm = MouseX() ; ym = MouseY() ; z = MouseZ()
xc = Int((xm + ym + ym - offsety) Shr 5)
yc = Int((ym + ym - xm + offsetx) Shr 5)


For x = 0 To 25
	For y = 0 To 25
		SetColor 255, 255, 255
		Plot px(x, y, z) , py(x, y, z)
	Next
Next

Iso(px(xc, yc, 0) - 208, py(xc, yc, 0) - 96, xs, ys)

Iso(px(xc, yc, z) - 208, py(xc, yc, z) - 96, xs, ys)

DrawText " " + (xc - 12) + " " + yc + " " + z + " ", 0, 0
Flip
Until KeyDown(KEY_ESCAPE) Or AppTerminate()
