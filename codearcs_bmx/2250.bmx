; ID: 2250
; Author: TomToad
; Date: 2008-05-02 12:39:17
; Title: Kaleidoscope
; Description: Kaleidoscope effect on image

SuperStrict
Framework BRL.D3D7Max2D
Import BRL.System
Import BRL.Pixmap

' modules which may be required:
Import BRL.PNGLoader
Import BRL.BMPLoader
Import BRL.TGALoader
Import BRL.JPGLoader


SetGraphicsDriver D3D7Max2DDriver()


Local Filename:String = RequestFile("Load Image File") 'request the image file
If Not Filename Then End

Local Pixmap:TPixmap = LoadPixmap(Filename)

If Not pixmap Then RuntimeError("Cannot open file "+Filename)
pixmap = ResizePixmap(pixmap,800,600) 'resize the image to the screen

Local Image:TImage = CreateImage(256,256) 'create the kaleidiscope "window"
MidHandleImage Image


Local Angle:Double = 0 'Angle of the Window

Graphics 800,600 'Set the graphics mode

Local Time:Int = MilliSecs() + 1000 'for displaying frame rate
Local Frame:Int = 0
Local FPS:Int

While Not KeyHit(KEY_ESCAPE) And Not AppTerminate()
	Local xo:Int = MouseX() 'get mouse position
	Local yo:Int = MouseY()
	If xo < 128 Then xo = 128
	If xo > 671 Then xo = 671
	If yo < 128 Then yo = 128
	If yo > 471 Then yo = 471 'set the mouse within a certain region to prevent out-of-bounds errors
	
	Local Lock:TPixmap = LockImage(Image) 'lock the image
	memset_(PixmapPixelPtr(Lock),0,256*256*4) 'zero all the pixels


	For Local x:Int = -128 To 127 'go through the image pixel by pixel
		For Local y:Int = -128 To 127
			Local Dist:Double = Sqr(x*x+y*y) 'we are only interested in a radius of 128 pixels
			If Dist<128
				Local PixAngle:Double = ATan2(y,x) 'find the angle of the pixel
				If Pixangle < 0 Then Pixangle :+ 360 'convert -180x180 to 0x360
				Pixangle :- Angle 'subtract the window's angle from the pixels angle
				If PixAngle >= 360 Then PixAngle :- 360
				If PixAngle < 0 Then PixAngle :+ 360
				Select True
					Case PixAngle < 45 And Pixangle >= 0 'first 45 degrees get copied
						WritePixel(Lock,x+128,y+128,ReadPixel(Pixmap,x+xo,y+yo))
					Case PixAngle <= 90 And PixAngle >= 45 'next 45 degrees get mirrored
						WritePixel(Lock,x+128,y+128,ReadPixel(Pixmap,Cos((89-pixangle)+Angle)*Dist+xo,Sin((89-pixangle)+angle)*dist+yo))
				End Select
			End If
		Next
	Next
	UnlockImage Image
			
	Cls
	For Local i:Int = 0 To 359 Step 90 'we will draw the 90' rendered above 4 times for an entire 360 image
		SetRotation i
		DrawImage Image,xo,yo
	Next
	
	SetRotation 0
	
	DrawText FPS,10,10 'FPS
	Flip
	Angle :+ 1 'add 1 to the window's angle
	If Angle >= 360 Then Angle :- 360
	If MouseHit(1) 'Left mouse button = hide cursor
		HideMouse
	End If
	If MouseHit(2) 'Right mouse button = show cursor
		ShowMouse
	End If
	Frame :+ 1
	If MilliSecs() >= Time 'Update FPS
		Time :+ 1000
		FPS = Frame
		Frame = 0
	End If
Wend
