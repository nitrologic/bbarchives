; ID: 2480
; Author: Malice
; Date: 2009-05-13 18:54:14
; Title: Desaturate Image
; Description: Desaturates an Image (with Contrast Control)

;By Malice 2009
;
;Use DeSaturate(Image%,Contrast%) to Call function.
;
; Other functions are required, but are called from within.


Function DeSaturate(Image,Contrast%=100)
	Contrast%=Contrast% Mod 100
	Local Width%=ImageWidth(Image)-1
	Local Height=ImageHeight(Image)-1
	Local X
	Local Y
	LockBuffer ImageBuffer(Image)
	For X=0 To Width
		For Y=0 To Height
			Pixel=ReadPixelFast(X,Y,ImageBuffer(Image))
			WritePixelFast X,Y,Greyscale%(Pixel,Contrast%),ImageBuffer(Image)
		Next
	Next
	UnlockBuffer ImageBuffer(Image)
End Function






Function Greyscale%(Colour_ARGB,Amount%)		
	Mean=((Red(Colour_ARGB)+Blue(Colour_ARGB)+Green(Colour_ARGB))*0.33)
	If Not( Amount)
		Return (Colour_ARGB)
	Else
		If Amount=100
			Return ARGB(Mean,Mean,Mean)
		Else
			Return ARGB(Filter(Red(Colour_ARGB),Mean)*Amount%*0.01,Filter(Green(Colour_ARGB),Mean)*Amount%*0.01,Filter(Blue(Colour_ARGB),Mean)*Amount%*0.01)
		End If	
	End If
End Function

Function ARGB(r,g,b)
	Return (b Or (g Shl 8) Or (r Shl 16) )
End Function
Function Red(ARGB)
	Return (RGB Shr 16 And 255)
End Function
Function Green(ARGB)
	Return (ARGB Shr 8 And 255)
End Function
Function Blue(ARGB)
	Return (ARGB And 255)
End Function

Function Filter%(Colour,Filter)
	Return (Colour-(Colour-Filter))
End Function
