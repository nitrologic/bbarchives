; ID: 2408
; Author: Malice
; Date: 2009-02-05 21:50:01
; Title: Some 2D Colour Functions
; Description: A few hopefully useful colour manipulation routines

;Image Colour Functions - Example Code
FlushMouse()
Graphics 1024,768,32,0
SetBuffer BackBuffer()

; Example Images (Included)
Global Example_1=LoadImage("example1.bmp")
Global Example_2=LoadImage("example2.bmp")

; AVERAGE COLOUR:
; Full Tolerance:
DrawImage Example_1,0,0
DisplayAverageFromImage(Example_1,100,ImageWidth(Example_1)/2,0)
Color 255,255,255
Text 0,ImageHeight(Example_1)*1.5,"Average Image Colouration Full Tolerance Example 1"

DrawImage Example_2,512,0
DisplayAverageFromImage(Example_2,100,512+(ImageWidth(Example_2)/2),0)
Color 255,255,255
Text 512,ImageHeight(Example_1)*1.5,"Average Image Colouration Full Tolerance Example 2"

; Low Tolerance:
DrawImage Example_1,0,512
DisplayAverageFromImage(Example_1,25,ImageWidth(Example_1)/2,512)
Color 255,255,255
Text 0,512+ImageHeight(Example_1)*1.5,"Average Image Colouration Low Tolerance Example 1"

DrawImage Example_2,512,512
DisplayAverageFromImage(Example_2,25,512+(ImageWidth(Example_2)/2),512)
Color 255,255,255
Text 512,512+ImageHeight(Example_2)*1.5,"Average Image Colouration Low Tolerance Example 2"

Flip

WaitMouse()
Cls

While Not KeyDown(1)
	Color 255,255,255
	Text 0,ImageHeight(Example_1)*1.5,"Colour Morphing Example"
	DrawImage Example_1,0,0
	; 16 chosen for smoothness value - change at will NOTE Lower values are slower and smoother, higher values quicker but less smooth.
	ColourMorph(Example_1,Example_2,16)
	Flip
Wend

	


Function ColourMorph(ImageSource,ImageDestination,Speed%=16)
	QBuffer=ImageBuffer(ImageSource)
	PBuffer=ImageBuffer(ImageDestination)
	If ((ImageWidth(ImageSource)<>ImageWidth(ImageDestination)) Or (ImageHeight(ImageSource)<>ImageHeight(ImageDestination)))
		ResizeImage ImageDestination,ImageWidth(ImageSource),ImageHeight(ImageSource)
	End If
	For SlowPixel_X=0 To ImageWidth(ImageSource)
		For SlowPixel_Y=0 To ImageHeight(ImageSource)
			SetBuffer(PBuffer)
			GetColor(SlowPixel_X,SlowPixel_Y)
			DestinationColour%=GetRGBInteger%(ColorRed() ,ColorGreen() ,ColorBlue() )
			SetBuffer(QBuffer)
			GetColor(SlowPixel_X,SlowPixel_Y)
			SourceColour%=GetRGBInteger%(ColorRed() ,ColorGreen() ,ColorBlue() )
			Color GetRedComponent(SmoothColour(SourceColour,DestinationColour,Speed)),GetGreenComponent(SmoothColour(SourceColour,DestinationColour,Speed)),GetBlueComponent(SmoothColour(SourceColour,DestinationColour,Speed))
			Plot SlowPixel_X,SlowPixel_Y
		Next
	Next
	SetBuffer BackBuffer()
End Function

Function SmoothColour(Col1,Col2,Smoothness)
	Smoothness=Abs(Smoothness)
	If Smoothness=0 Then Smoothness=1
	R1=GetRedComponent(Col1)
	R2=GetRedComponent(Col2)
	G1=GetGreenComponent(Col1)
	G2=GetGreenComponent(Col2)
	B1=GetBlueComponent(Col1)
	B2=GetBlueComponent(Col2)
	rdiff=Sgn(R2-R1)
	gdiff=Sgn(G2-G1)
	bdiff=Sgn(B2-B1)
	If (Sgn((R1+rdiff)-R2)<>Sgn(R2-R1))
		R1=R1+(rdiff*Smoothness)
		Else 
			R1=R2
	End If
	If (Sgn((G1+gdiff)-G2)<>Sgn(G2-G1))
		G1=G1+(gdiff*Smoothness)
		Else 
			G1=G2
	End If
	If (Sgn((B1+bdiff)-B2)<>Sgn(B2-B1))
		B1=B1+(bdiff*Smoothness)
		Else 
			B1=B2
	End If	
	Return GetRGBInteger(R1,G1,B1)
End Function

Function DisplayAverageFromImage(ImageHandle,Tolerance,x,y)
	Local Col%=GetAverageColourValue%(ImageHandle,Tolerance)
	Local Red%=GetRedComponent%(Col)
	Local Green%=GetGreenComponent%(Col)
	Local Blue%=GetBlueComponent%(Col)
	Color Red,Green,Blue
	Rect x,y,ImageWidth(ImageHandle),ImageHeight(ImageHandle),True
End Function

Function GetRedComponent%(RGBColour%)
	Local Component$=Str RGBColour%
	Local nLength%=Len(Component$)		
	If nLength<7 Then Return 0
	Local RedChars=nLength%-6
	Return Int(Left$(Component$,RedChars))
End Function

Function GetGreenComponent%(RGBColour%)
	Local Component$=Str RGBColour%
	Local nLength%=Len(Component$)
	If nLength<3 Then Return 0
	Local GreenChars%=nLength-3
	Component$=Right$(Left$(Component$,GreenChars%),3)
	Return Int(Component$)
End Function

Function GetBlueComponent%(RGBColour%)
	Local Component$=Str RGBColour% 
	If Len(Component$>=3) Then Component$=Right$(Component$,3)
	Return Int(Component$)
End Function

; Functions
Function GetAverageColourValue%(ImageHandle,Tolerance%=25)
	If Tolerance%<0 Or Tolerance%>100 Then Tolerance%=25
	If (ImageHandle>0)
		Local TotalSumRed%=0
		Local TotalSumGreen%=0
		Local TotalSumBlue%=0
		Local ProgressiveMeanRed%=0
		Local ProgressiveMeanGreen%=0
		Local ProgressiveMeanBlue%=0
		Local Pixels=0
		SetBuffer ImageBuffer(ImageHandle)
		For X_Width%=0 To ImageWidth(ImageHandle)
			For Y_Height%=0 To ImageHeight(ImageHandle)	
				GetColor(X_Width%,Y_Height%)
				Pixels=Pixels+1
				TotalSumRed%=TotalSumRed%+ColorRed()
				TotalSumGreen%=TotalSumGreen%+ColorGreen()
				TotalSumBlue%=TotalSumBlue%+ColorBlue()
			Next
		Next	
		ProgressiveMeanRed%=TotalSumRed%/Pixels%
		ProgressiveMeanGreen%=TotalSumGreen%/Pixels%
		ProgressiveMeanBlue%=TotalSumBlue%/Pixels%	
		
		If Tolerance<>100
			Pixels=0
			TotalSumRed%=0
			TotalSumGreen%=0
			TotalSumBlue%=0
			For X_Width%=0 To ImageWidth(ImageHandle)
				For Y_Height%=0 To ImageHeight(ImageHandle)	
					GetColor(X_Width%,Y_Height%)
					Pixels=Pixels+1
					If (Abs(ColorRed()-ProgressiveMeanRed%)<(Tolerance%*2.56))
						TotalSumRed%=TotalSumRed%+ColorRed()
					End If
					If (Abs(ColorRed()-ProgressiveMeanGreen%)<(Tolerance%*2.56))
						TotalSumGreen%=TotalSumGreen%+ColorGreen%()
					End If
					If (Abs(ColorBlue()-ProgressiveMeanBlue%)<(Tolerance%*2.56))
						TotalSumBlue%=TotalSumBlue%+ColorBlue%()
					End If
				Next
			Next		
			ProgressiveMeanRed%=TotalSumRed%/Pixels%
			ProgressiveMeanGreen%=TotalSumGreen%/Pixels%
			ProgressiveMeanBlue%=TotalSumBlue%/Pixels%	
		End If
		SetBuffer BackBuffer()
		Return GetRGBInteger%(ProgressiveMeanRed%,ProgressiveMeanGreen%,ProgressiveMeanBlue%)
	End If
End Function

Function GetRGBInteger%(RedComponent%,GreenComponent%,BlueComponent%)
	Return Int(Right$("000"+Str RedComponent% ,3)+Right$("000"+Str GreenComponent% ,3)+Right$("000"+Str BlueComponent% ,3))
End Function
