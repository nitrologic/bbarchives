; ID: 2414
; Author: Krischan
; Date: 2009-02-21 16:42:24
; Title: Image Blending Functions
; Description: Some Blendings like in Photoshop

; Image Blend Modes
;
; Idea Credit: http://www.pegtop.net/delphi/articles/blendmodes/intro.htm
; get the two images there or use your own (need to have the same size for this demo)

AppTitle "Image Blend Functions"


Graphics 400,300,32,2

image1=LoadImage("baseimage.jpg")
image2=LoadImage("blendimage.jpg")
image3=CreateImage(200,150)

ib1=ImageBuffer(image1)
ib2=ImageBuffer(image2)
ib3=ImageBuffer(image3)

start=MilliSecs()

LockBuffer ib1
LockBuffer ib2
LockBuffer ib3

For x=0 To 199
	
	For y=0 To 149
		
		rgb1=ReadPixelFast(x,y,ib1)
		rgb2=ReadPixelFast(x,y,ib2)
		
		r1%=(rgb1 And $ff0000)/$10000
		g1%=(rgb1 And $ff00)/$100
		b1%=(rgb1 And $ff)
		
		r2%=(rgb2 And $ff0000)/$10000
		g2%=(rgb2 And $ff00)/$100
		b2%=(rgb2 And $ff)
		
		r3=SoftLight(r1,r2)
		g3=SoftLight(g1,g2)
		b3=SoftLight(b1,b2)
		
		rgb3=r3*$10000+g3*$100+b3
		
		WritePixelFast x,y,rgb3,ib3
		
	Next
	
Next

UnlockBuffer ib3
UnlockBuffer ib2
UnlockBuffer ib1

ms=MilliSecs()-start

While Not KeyHit(1)
	
	DrawImage image1,0,0
	DrawImage image2,200,0
	DrawImage image3,100,150
	
	Text 0,0,ms+"ms"
	
	Flip
	
Wend

End

Function Average(a%,b%)
	
	Return (a+b) Shr 1
	
End Function

Function Multiply(a%,b%)
	
	Return (a*b) Shr 8
	
End Function

Function Screen(a%,b%)
	
	Return 255-((255-a)*(255-b) Shr 8)
	
End Function

Function Darken(a%,b%)
	
	If a<b Then Return a Else Return b
	
End Function

Function Lighten(a%,b%)
	
	If a>b Then Return a Else Return b
	
End Function

Function Difference(a%,b%)
	
	Return Abs(a-b)
	
End Function

Function Negation(a%,b%)
	
	Return 255-Abs(255-a-b)
	
End Function

Function Exclusion(a%,b%)
	
	Return a+b-(a*b Shr 7)
	
End Function

Function Overlay(a%,b%)
	
	If a<128 Then Return (a*b) Shr 7 Else Return 255-((255-a)*(255-b) Shr 7)
	
End Function

Function HardLight(a%,b%)
	
	If b<128 Then Return (a*b) Shr 7 Else Return 255-((255-b)*(255-a) Shr 7)
	
End Function

Function SoftLight(a%,b%)
	
	Local c%=a*b Shr 8
	Return (c+a*(255-((255-a)*(255-b) Shr 8)-c) Shr 8)
	
End Function

Function ColorDodge(a%,b%)
	
	If b=255 Then
		Return 255
	Else
		Local c%=Floor((a Shl 8)/(255-b))
		If c>255 Then Return 255 Else Return c
	EndIf
	
End Function

Function InverseColorDodge(a%,b%)
	
	If a=255 Then
		Return 255
	Else
		Local c%=Floor((b Shl 8)/(255-a))
		If c>255 Then Return 255 Else Return c
	EndIf
	
End Function

Function SoftColorDodge(a%,b%)
	
	Local c%
	
	If a+b<256 Then
		If b=255 Then
			Return 255
		Else
			c=Floor((a Shl 7)/(255-b))
			If c>255 Then Return 255 Else Return c
		EndIf
	Else
		c=255-Floor((((255-b) Shl 7)/a))
		If c<0 Then Return 0 Else Return c
	EndIf
	
End Function

Function ColorBurn(a%,b%)
	
	If b=0 Then
		Return 0
	Else
		Local c%=255-Floor(((255-a) Shl 8)/b)
		If c<0 Then Return 0 Else Return c
	EndIf
	
End Function

Function InverseColorBurn(a%,b%)
	
	If a=0 Then
		Return 0
	Else
		Local c%=255-Floor(((255-b) Shl 8)/a)
		If c<0 Then Return 0 Else Return c
	EndIf
	
End Function

Function SoftColorBurn(a%,b%)
	
	Local c%
	
	If a+b<256 Then
		If a=255 Then
			Return 255
		Else
			c=Floor((b Shl 7)/(255-a))
			If c>255 Then Return 255 Else Return c
		EndIf
	Else
		c=255-Floor(((255-a) Shl 7)/b)
		If c<0 Then Return 0 Else Return c
	EndIf
	
End Function

Function Reflect(a%,b%)
	
	If b = 255 Then
		Return 255
	Else
		Local c%=Floor(a*a/(255-b))
		If c>255 Then Return 255 Else Return c
	EndIf
	
End Function

Function Glow(a%,b%)
	
	If a=255 Then
		Return 255
	Else
		Local c%=Floor(b*b/(255-a))
		If c>255 Then Return 255 Else Return c
	EndIf
	
End Function
