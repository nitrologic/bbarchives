; ID: 3
; Author: BlitzSupport
; Date: 2001-08-16 18:12:11
; Title: Real RGB
; Description: Weird results with GetColor? Not what you expect?


Type RGB
	Field r
	Field g
	Field b
End Type

Function GetRealRGB.RGB (r, g, b)

; Being nice and drawing on my own pixel :)

	tempbuffer = GraphicsBuffer ()
	temp = CreateImage (1, 1)
	SetBuffer ImageBuffer (temp)
	real.RGB = New RGB
	Color r, g, b: Plot 0, 0: GetColor 0, 0
	real\r = ColorRed ()
	real\g = ColorGreen ()
	real\b = ColorBlue ()
	SetBuffer tempbuffer
	FreeImage temp
	Return real

End Function

; Use it like this: supply the RGB values you *think* you're using,
; and it returns the *actual* RGB values your gfx card is using...

 real.RGB = GetRealRGB (255, 255, 255)

 Print real\r
 Print real\g
 Print real\b

