; ID: 2762
; Author: Malice
; Date: 2010-09-06 08:25:52
; Title: Simple Contour
; Description: Creates a simple contour map from an image

Global Path$=GetEnv("PROGRAMFILES")+"\Blitz3D\samples\birdie\thunder\hmap.bmp"

Graphics 800,600,32,6
SetBuffer BackBuffer()

	Image=LoadImage(Path$)

	ccc=ContourMap(Image)

	While Not KeyDown(1)
	
	If KeyDown(57)  And (MilliSecs()-lastkey>250)
		FlushKeys
		lastkey=MilliSecs()
		display=Not(display)
	End If
	Cls
	DrawImage (ccc*display)+(Image*(1-display)),0,0
	Color 0,255,0
	Text 0,0,"Press Space to switch image / contour map"
	Flip
	Wend

Function ContourMap%(Image%,Tolerance#=0.5)
	If (Not(Image)) Then Return 0
	Local Contour%=CreateImage(ImageWidth(Image),ImageHeight(Image))
	
		Local X1=0
		Local Y1=0
		Local X2=ImageWidth(Image)-1
		Local Y2=ImageHeight(Image)-1
		
	Buffer=(ImageBuffer(Image))
	CBuffer=(ImageBuffer(Contour))
	
	LockBuffer Buffer
	LockBuffer CBuffer
	For x=X1 To X2
	For y=Y1 To Y2
		Pixel=ReadPixelFast(x,y,Buffer)
		If (AdjacentDifference(Pixel,x,y,Image,Tolerance#)) Then WritePixelFast x,y,RGBa(255,255,255),CBuffer
	Next
	Next
	UnlockBuffer ImageBuffer(Image)
	UnlockBuffer ImageBuffer(Contour)
	Return Contour
End Function
		
	Function PixelTopLeft%(Image%,x%,y%)
		If (Not(Image)) Then Return -1
		Local X1=1
		Local Y1=1
		Local X2=ImageWidth(Image)-2
		Local Y2=ImageHeight(Image)-2
	
		If (Not(x>=X1 And y>=Y1)) Then  Return -1
		
		x=x-1
		y=y-1
		Local Buffer%=ImageBuffer(Image)
		;LockBuffer Buffer
		Local nReturn%=ReadPixelFast(x,y,Buffer)
		;UnlockBuffer Buffer
		Return nReturn
	End Function	
	
	Function PixelTopMid%(Image,x,y)
		If (Not(Image)) Then Return -1
		Local X1=1
		Local Y1=1
		Local X2=ImageWidth(Image)-2
		Local Y2=ImageHeight(Image)-2
	
		If (Not(y>=Y1)) Then  Return -1
		
		y=y-1
		Local Buffer%=ImageBuffer(Image)
		;LockBuffer Buffer
		Local nReturn%=ReadPixelFast(x,y,Buffer)
		;UnlockBuffer Buffer
		Return nReturn
	End Function

	Function PixelTopRight%(Image,x,y)
		If (Not(Image)) Then Return -1
		Local X1=1
		Local Y1=1
		Local X2=ImageWidth(Image)-2
		Local Y2=ImageHeight(Image)-2
	
		If (Not(x<=X2 And y>=Y1)) Then  Return -1
		
		x=x+1
		y=y-1
		Local Buffer%=ImageBuffer(Image)
		;LockBuffer Buffer
		Local nReturn%=ReadPixelFast(x,y,Buffer)
		;UnlockBuffer Buffer
		Return nReturn
End Function

	Function PixelMidLeft%(Image,x,y)
		If (Not(Image)) Then Return -1
		Local X1=1
		Local Y1=1
		Local X2=ImageWidth(Image)-2
		Local Y2=ImageHeight(Image)-2
		
		If (Not(x>=X1)) Then  Return -1
		
		x=x-1
		Local Buffer%=ImageBuffer(Image)
		;LockBuffer Buffer
		Local nReturn%=ReadPixelFast(x,y,Buffer)
		;UnlockBuffer Buffer
		Return nReturn
	End Function	
	
	Function PixelMidRight%(Image,x,y)
		If (Not(Image)) Then Return -1
		Local X1=1
		Local Y1=1
		Local X2=ImageWidth(Image)-2
		Local Y2=ImageHeight(Image)-2
	
		If (Not(x<=X2)) Then  Return -1
		
		x=x+1
		Local Buffer%=ImageBuffer(Image)
		;LockBuffer Buffer
		Local nReturn%=ReadPixelFast(x,y,Buffer)
		;UnlockBuffer Buffer
		Return nReturn
End Function

	Function PixelBotLeft%(Image,x,y)
		If (Not(Image)) Then Return -1
		Local X1=1
		Local Y1=1
		Local X2=ImageWidth(Image)-2
		Local Y2=ImageHeight(Image)-2
	
		If (Not(x>=X1 And y<=Y2)) Then  Return -1
		
		x=x-1
		y=y+1
		Local Buffer%=ImageBuffer(Image)
		;LockBuffer Buffer
		Local nReturn%=ReadPixelFast(x,y,Buffer)
		;UnlockBuffer Buffer
		Return nReturn
End Function

	Function PixelBotMid%(Image,x,y)
		If (Not(Image)) Then Return -1
		Local X1=1
		Local Y1=1
		Local X2=ImageWidth(Image)-2
		Local Y2=ImageHeight(Image)-2
	
		If (Not(y<=Y2)) Then  Return -1
		
		y=y+1
		
		Local Buffer%=ImageBuffer(Image)
		;LockBuffer Buffer
		Local nReturn%=ReadPixelFast(x,y,Buffer)
		;UnlockBuffer Buffer
		Return nReturn
	End Function
		
	Function PixelBotRight%(Image,x,y)
		If (Not(Image)) Then Return -1
		Local X1=1
		Local Y1=1
		Local X2=ImageWidth(Image)-2
		Local Y2=ImageHeight(Image)-2
	
		If (Not(x<=X2 And y<=Y2)) Then  Return -1
		
		x=x+1
		y=y+1
		
		Local Buffer%=ImageBuffer(Image)
		;LockBuffer Buffer
		Local nReturn%=ReadPixelFast(x,y,Buffer)
		;UnlockBuffer Buffer
		Return nReturn
		
End Function	

Function AdjacentDifference%(Test_aRGB%,x%,y%,Image%,Tolerance#)
	Local Mean_aRGB%=DeSaturatePixel(Test_aRGB%)
	Local Valid%=False

	Local Adjacent%
	
	Adjacent%=DeSaturatePixel(PixelTopLeft(Image,x,y))
	Valid=(Float(Float(Red(Test_aRGB))/Float(Red(Adjacent)))<Tolerance#)
	If (Adjacent=-1) Then Valid=False
	
	If (Valid) Return True
		Adjacent%=DeSaturatePixel%(PixelTopMid(Image,x,y))
		Valid=(Float(Float(Red(Test_aRGB))/Float(Red(Adjacent)))<Tolerance#)
		If (Adjacent=-1) Then Valid=False

	If (Valid) Return True
		Adjacent%=DeSaturatePixel%(PixelTopRight(Image,x,y))
	Valid=(Float(Float(Red(Test_aRGB))/Float(Red(Adjacent)))<Tolerance#)
		If (Adjacent=-1) Then Valid=False

	If (Valid) Return True
		Adjacent%=DeSaturatePixel%(PixelMidLeft(Image,x,y))
	Valid=(Float(Float(Red(Test_aRGB))/Float(Red(Adjacent)))<Tolerance#)
		If (Adjacent=-1) Then Valid=False

	If (Valid) Return True
	Adjacent%=DeSaturatePixel%(PixelMidRight(Image,x,y))
	Valid=(Float(Float(Red(Test_aRGB))/Float(Red(Adjacent)))<Tolerance#)
		If (Adjacent=-1) Then Valid=False

	If (Valid) Return True
		Adjacent%=DeSaturatePixel%(PixelBotLeft(Image,x,y))
		Valid=(Float(Float(Red(Test_aRGB))/Float(Red(Adjacent)))<Tolerance#)
		If (Adjacent=-1) Then Valid=False

	If (Valid) Return True
	Adjacent%=DeSaturatePixel%(PixelBotMid(Image,x,y))
	Valid=(Float(Float(Red(Test_aRGB))/Float(Red(Adjacent)))<Tolerance#)
		If (Adjacent=-1) Then Valid=False
	
	If (Valid) Return True
		Adjacent%=DeSaturatePixel%(PixelBotRight(Image,x,y))
		Valid=(Float(Float(Red(Test_aRGB))/Float(Red(Adjacent)))<Tolerance#)
		If (Adjacent=-1) Then Valid=False

	Return Valid

End Function























Function RGBa%(R%,G%,B%,a%=0)
	;	Returns aRGB Value from components.
	
	Return ((a% Shl 24) Or (R% Shl 16) Or (G% Shl 8) Or B%)
End Function

;_______________________________________________________________________________________________________________________
;_______________________________________________________________________________________________________________________

Function Red(RGBa_Value%)
	;	Returns Red component.
	
	Return (RGBa_Value% Shr 16 And 255)
End Function

;_______________________________________________________________________________________________________________________
;_______________________________________________________________________________________________________________________

Function Green(RGBa_Value%)
	;	Returns Green component.
	
	Return (RGBa_Value% Shr 8 And 255)
End Function

;_______________________________________________________________________________________________________________________
;_______________________________________________________________________________________________________________________

Function Blue(RGBa_Value%)
	;	Returns Blue component.
	
	Return (RGBa_Value% And 255)
End Function

;_______________________________________________________________________________________________________________________
;_______________________________________________________________________________________________________________________

Function Alpha%(RGBa_Value%)
	;	Returns Alpha component.
	
	Return (RGBa_Value% Shr 24 And 255)
End Function

;_______________________________________________________________________________________________________________________
;_______________________________________________________________________________________________________________________
Function DeSaturatePixel(Colour%)
	;	Returns the DeSaturated (Mean intensity) RGBa value of a particular pixel
	a%=Alpha%(Colour%)
	R%=Red%(Colour%)
	G%=Green%(Colour%)
	B%=Blue%(Colour%)
	Colour%=((R%-(R%*0.333)*0.5)+(G%-(G%*0.333)*0.5)+(B%-(B%*0.333)*0.5))
	Return RGBa%(Colour%,Colour%,Colour%,a%)
End Function
