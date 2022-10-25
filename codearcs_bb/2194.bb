; ID: 2194
; Author: Nebula
; Date: 2008-01-20 12:25:43
; Title: 3 image effect functions
; Description: Useful image manipulation functions

Function fadeimage(image,mm# = 1)
If image = 0 Then Return
Local im = CreateImage(ImageWidth(image),ImageHeight(image))
im = CopyImage(image)

SetBuffer ImageBuffer(im)
LockBuffer ImageBuffer(im)
jm# = ImageWidth(im)-1
pf# = Abs(100 / jm)
pc# = 0
For x=0 To ImageWidth(im)-1
	ax# = x
	mt# = (ax*pf);*mm
	For y=0 To ImageHeight(im)-1
		
		rc = ReadPixelFast(x,y)
		ar# = (getr(rc)  / 100) * mt
		ag# = (getg(rc) / 100) * mt 
		ab# = (getb(rc) / 100) * mt 
		If ar < 0 Then ar = 0
		If ag < 0 Then ag = 0
		If ab < 0 Then ab = 0
		If ar>255 Then ar = 255
		If ag > 255 Then ag = 255
		If ab > 255 Then ab = 255
		WritePixelFast x,y,getrgb(ar,ag,ab)
	Next:Next
UnlockBuffer ImageBuffer(im)

SetBuffer BackBuffer()
Return im
End Function
;

Function fadebarrect(image,sw=4,sh=4)
If image = 0 Then Return

Local im = CreateImage(ImageWidth(image),ImageHeight(image))
im = CopyImage(image)
;
Local gw#= ImageWidth(im) - 1
Local gh# = ImageHeight(im) - 1
;
;If gh > 320 Then gh = 320
;
Local div#,rc#
Local r#,g#,b#

div = .5

SetBuffer ImageBuffer(im)
LockBuffer ImageBuffer(im)
For x=0 To gw
For y=0 To gh
If RectsOverlap(x,y,1,1,sw,sh,gw-(sw*2),gh-(sh*2)) = False Then

ax# = x
ay# = y
;If x*y > ((gw*2)+(gh*2)) Then div = -7 + ((ax/20) + (ay/100))  Else div = (5-(ax/20)) - (ay/100)
If x*y > ((gw*2)+(gh*2)) Then div = -7 + ((ax/(gw/4.8)) + (ay/(gh/3.23)))  Else div = (5-(ax/(gw/4.8))) - (ay/(gh/3.23))

rc = ReadPixelFast(x,y)

r = getr(rc)*div
g = getg(rc)*div
b = getb(rc)*div

If r > 255 Then r = 255
If g > 255 Then g = 255
If b > 255 Then b = 255
If r < 0 Then r = 0
If g < 0 Then g = 0
If b < 0 Then b = 0

WritePixelFast x,y,getrgb(r,g,b)

End If
Next
Next
UnlockBuffer ImageBuffer(im)
SetBuffer BackBuffer()
Return im
End Function


Function effectimage(image,m2#=1)
ar#=0
ag#=0
ab#=0
pc#=0
div#=0
If image = 0 Then Return 
Local im = CreateImage(ImageWidth(image),ImageHeight(image))

im = CopyImage(image)
SetBuffer ImageBuffer(im)
LockBuffer ImageBuffer(im)

sw = 0
For y=1 To ImageHeight(im)-2 
	Select sw
	Case 0
		sw = 1 : div = (1.3*m2)
	Case 1
		sw = 2 : div = (1.1*m2)
	Case 2 
		sw = 3 : div = 0
	Case 3
		sw = 4 : div = (.7*m2)
	Case 4
		sw = 5 : div = (.9*m2)
	Case 5
		sw = 0 : div = 0
	End Select
	
	If div <> 0
	qqy# = y/4 : qqy = qqy#/40000
	;DebugLog qqy
	For x=0 To ImageWidth(im)-1
		div = (div + 0.001) - qqy
		pc = ReadPixelFast(x,y)
		ar = getr(pc)*div
		ag = getg(pc)*div
		ab = getb(pc)*div
		sop = 0
		If ar > 255 Then ar = 255
		If ag > 255 Then ag = 255
		If ab > 255 Then ab = 255
		If ar < 0 Then ar = 0
		If ag < 0 Then ag = 0
		If ab < 0 Then ab = 0
		WritePixelFast x,y,getrgb(ar,ag,ab)
	Next
	End If
Next

ar#=0
ag#=0
ab#=0
pc#=0
div#=0
sw = 0
For x=1 To ImageWidth(im)-2 Step 48
	Select sw
	Case 0
		sw = 1 : div = .97
	Case 1
		sw = 2 : div = .95
	Case 2 
		sw = 3 : div = .91
	Case 3
		sw = 4 : div = 0
	Case 4
		sw = 5 : div = .94
	Case 5
		sw = 0 : div = .97
	End Select
	
	If div <> 0
	;qqy# = x/3 : qqy = qqy#/40000
	;DebugLog qqy
	For y=0 To ImageHeight(im)-1
		;div = (div + 0.001); - qqy
		pc = ReadPixelFast(x,y)
		ar = getr(pc)*div
		ag = getg(pc)*div
		ab = getb(pc)*div
		sop = 0
		If ar > 255 Then ar = 255
		If ag > 255 Then ag = 255
		If ab > 255 Then ab = 255
		If ar < 0 Then ar = 0
		If ag < 0 Then ag = 0
		If ab < 0 Then ab = 0
		WritePixelFast x,y,getrgb(ar,ag,ab)
	Next
	End If
Next


UnlockBuffer ImageBuffer(im)
SetBuffer BackBuffer()
Return im
End Function




;Standard functions for converting colour to RGB values, for WritePixelFast and ReadPixelFast
Function GetRGB(r,g,b)
	Return b Or (g Shl 8) Or (r Shl 16)
End Function

Function GetR(RGB)
    Return RGB Shr 16 And %11111111
End Function

Function GetG(RGB)
	Return RGB Shr 8 And %11111111
End Function

Function GetB(RGB)
	Return RGB And %11111111
End Function
