; ID: 2157
; Author: Adam Novagen
; Date: 2007-11-21 20:59:25
; Title: FloodFill - Really!!!
; Description: A real, working Blitz Basic Flood Fill routine.

Type Pixel
	Field X,Y
End Type


Function FloodFill(FillImage,FX,FY,FR,FG,FB);COMPLETE


If FX < 0 Or FX > ImageWidth(FillImage) - 1 Or FY < 0 Or FY > ImageHeight(FillImage) - 1
	RuntimeError("GHZLIB ERROR" + Chr(10) + "Invalid coordinates: " + FX + "," + FY + Chr(10) + "Coords must be within image boundaries")
EndIf


Local CurrentBuffer = GraphicsBuffer()
Local CurrentRed = ColorRed()
Local CurrentGreen = ColorGreen()
Local CurrentBlue = ColorBlue()


SetBuffer ImageBuffer(FillImage)
LockBuffer


Local TarR = PixelRed(FX,FY)
Local TarG = PixelGreen(FX,FY)
Local TarB = PixelBlue(FX,FY)


Color FR,FG,FB
WritePixelFast FX,FY,FB + (FG * 256) + (FR * (256 * 256))
Pixel.Pixel = New Pixel
Pixel\X = FX
Pixel\Y = FY


Repeat
Local PixelsRemaining = False
For Pixel.Pixel = Each Pixel
Local PixelLeft = False
Local PixelAbove = False
Local PixelRight = False
Local PixelBelow = False
Local PixelX = Pixel\X
Local PixelY = Pixel\Y
If Pixel\X > 0;check left
	If PixelRed(Pixel\X - 1,Pixel\Y) = TarR And PixelGreen(Pixel\X - 1,Pixel\Y) = TarG And PixelBlue(Pixel\X - 1,Pixel\Y) = TarB
		PixelLeft = True
		PixelsRemaining = True
	EndIf
EndIf
If Pixel\Y > 0;check above
	If PixelRed(Pixel\X,Pixel\Y - 1) = TarR And PixelGreen(Pixel\X,Pixel\Y - 1) = TarG And PixelBlue(Pixel\X,Pixel\Y - 1) = TarB
		PixelAbove = True
		PixelsRemaining = True
	EndIf
EndIf
If Pixel\X < ImageWidth(FillImage) - 1;check right
	If PixelRed(Pixel\X + 1,Pixel\Y) = TarR And PixelGreen(Pixel\X + 1,Pixel\Y) = TarG And PixelBlue(Pixel\X + 1,Pixel\Y) = TarB
		PixelRight = True
		PixelsRemaining = True
	EndIf
Else
	PixelRight = False
EndIf
If Pixel\Y < ImageHeight(FillImage) - 1;check below
	If PixelRed(Pixel\X,Pixel\Y + 1) = TarR And PixelGreen(Pixel\X,Pixel\Y + 1) = TarG And PixelBlue(Pixel\X,Pixel\Y + 1) = TarB
		PixelBelow = True
		PixelsRemaining = True
	EndIf
Else
	PixelBelow = False
EndIf
;If KeyDown(1) Then End
Delete Pixel
PixelNum = PixelNum - 1
;LockBuffer
If PixelLeft = True
	Pixel.Pixel = New Pixel
	Pixel\X = PixelX - 1
	Pixel\Y = PixelY
	PixelNum = PixelNum + 1
	PixelLeft = False
	WritePixelFast Pixel\X,Pixel\Y,FB + (FG * 256) + (FR * (256 * 256))
EndIf
If PixelAbove = True
	Pixel.Pixel = New Pixel
	Pixel\X = PixelX
	Pixel\Y = PixelY - 1
	PixelNum = PixelNum + 1
	PixelAbove = False
	WritePixelFast Pixel\X,Pixel\Y,FB + (FG * 256) + (FR * (256 * 256))
EndIf
If PixelRight = True
	Pixel.Pixel = New Pixel
	Pixel\X = PixelX + 1
	Pixel\Y = PixelY
	PixelNum = PixelNum + 1
	PixelRight = False
	WritePixelFast Pixel\X,Pixel\Y,FB + (FG * 256) + (FR * (256 * 256))
EndIf
If PixelBelow = True
	Pixel.Pixel = New Pixel
	Pixel\X = PixelX
	Pixel\Y = PixelY + 1
	PixelNum = PixelNum + 1
	PixelBelow = False
	WritePixelFast Pixel\X,Pixel\Y,FB + (FG * 256) + (FR * (256 * 256))
EndIf
;!UNCOMMENT THE FOLLOWING CODE TO SEE FLOODFILL IN ACTION!
;UnlockBuffer
;SetBuffer CurrentBuffer
;DrawImage FillImage,0,0
;Flip
;SetBuffer ImageBuffer(FillImage)
;LockBuffer
Next
Until PixelsRemaining = False

UnlockBuffer
SetBuffer CurrentBuffer
Color CurrentRed,CurrentGreen,CurrentBlue


End Function


Function PixelGreen(Ghz_Lib_Var_PixelX,Ghz_Lib_Var_PixelY)


Local Ghz_Lib_Var_CurrentRed = ColorRed()
Local Ghz_Lib_Var_CurrentGreen = ColorGreen()
Local Ghz_Lib_Var_CurrentBlue = ColorBlue()

GetColor(Ghz_Lib_Var_PixelX,Ghz_Lib_Var_PixelY)
Local Ghz_Lib_Var_Green = ColorGreen()

Color Ghz_Lib_Var_CurrentRed,Ghz_Lib_Var_CurrentGreen,Ghz_Lib_Var_CurrentBlue

Return Ghz_Lib_Var_Green


End Function


Function PixelRed(Ghz_Lib_Var_PixelX,Ghz_Lib_Var_PixelY)


Local Ghz_Lib_Var_CurrentRed = ColorRed()
Local Ghz_Lib_Var_CurrentGreen = ColorGreen()
Local Ghz_Lib_Var_CurrentBlue = ColorBlue()

GetColor(Ghz_Lib_Var_PixelX,Ghz_Lib_Var_PixelY)
Local Ghz_Lib_Var_Red = ColorRed()

Color Ghz_Lib_Var_CurrentRed,Ghz_Lib_Var_CurrentGreen,Ghz_Lib_Var_CurrentBlue

Return Ghz_Lib_Var_Red


End Function


Function PixelBlue(Ghz_Lib_Var_PixelX,Ghz_Lib_Var_PixelY)


Local Ghz_Lib_Var_CurrentRed = ColorRed()
Local Ghz_Lib_Var_CurrentGreen = ColorGreen()
Local Ghz_Lib_Var_CurrentBlue = ColorBlue()

GetColor(Ghz_Lib_Var_PixelX,Ghz_Lib_Var_PixelY)
Local Ghz_Lib_Var_Blue = ColorBlue()

Color Ghz_Lib_Var_CurrentRed,Ghz_Lib_Var_CurrentGreen,Ghz_Lib_Var_CurrentBlue

Return Ghz_Lib_Var_Blue


End Function


;!TESTING LOOP
;To use this mini-program, just click anywhere on the image.
;The cursor is represented by a flashing pixel.
Graphics 800,600,0,2
Global Image = LoadImage("sample image 1.bmp");replace "image.bmp" with a valid filename
SetBuffer BackBuffer()
SeedRnd MilliSecs()
While Not KeyDown(1)
Cls
If MouseHit(1) Then FloodFill(Image,MouseX(),MouseY(),Rand(255),Rand(255),Rand(255))
DrawImage Image,0,0
Color Rand(255),Rand(255),Rand(255)
Plot MouseX(),MouseY();the makeshift cursor
Flip
Wend
End
