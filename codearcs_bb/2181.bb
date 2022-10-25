; ID: 2181
; Author: Nebula
; Date: 2008-01-10 13:57:35
; Title: Graphics / Sprite Generator
; Description: Generate/Draw shop Items/Sprites

Graphics 640,480,32,2
SetBuffer BackBuffer()

ClsColor 40,40,10
Cls
While KeyDown(1) = False
	Cls
	
	zz=0
	z = Rand(10,90)
	x = Rand(-25,GraphicsWidth())
	y = Rand(-25,GraphicsHeight())
	If Rand(1,2) = 1 Then 
		q1 = 15 
		q2 = 55
		Else
		q1 = 5
		q2 = 55
	End If
	
	sx = Rand(4,q1);55
	sy = Rand(4,q2)

	While zz < 360	
	
		tmp = CreateImage(32,32)
		SetBuffer ImageBuffer(tmp)
		Color 237+Rand(-25,25),185+Rand(-38,8),163+Rand(-98,8)
		Oval 0,0,20,20,True
		Oval 5,10,10,20,True
		Rect 0,0,10,10,True
		
		aa = Abs(255-ColorRed())
		Color ColorRed()+aa,ColorGreen()+aa,ColorBlue()+aa
		Oval 3,3,12,12,True
		
		For x1=0 To 32 Step 3
		For y1=0 To 32 Step 3
			GetColor x1,y1
			
			zr = ColorRed()*2
			zg = ColorGreen()*2
			zb = ColorBlue()*2
			
			If zr>255 Then zr = 255
			If zg>255 Then zg = 255
			If zb>255 Then zb = 255
			
			Color zr,zg,zb

			Plot x1,y1
		Next:Next
		
		SetBuffer BackBuffer()
		TFormFilter 1
		If Rand(1,2) = 1 Then
			ResizeImage tmp,sx/2,sy
			Else
			ResizeImage tmp,sx,sy
		End If
		HandleImage tmp,sx/2,sy/2
		RotateImage	tmp,zz

		For qqq= 0 To 4
		x1a = Rand(500)
		y1a = Rand(300)
		
		For i=0 To 24
			If i<2 Then
			DrawImage tmp,Rand(GraphicsWidth()),Rand(GraphicsHeight())
			Else
			x1 = Cos(ia#)*32
			y1 = Sin(ia)*32
			ia=ia+1
			If ia>360 Then ia = 0
			DrawImage tmp,x1+x1a,y1+y1a
			End If
		Next
		
		Next
		If cnt > 50 Then Cls  : cnt = 0
		Flip 1200
		FreeImage tmp
		zz=zz+z
	Wend
	
	cnt=cnt+11
	Flip
	
Wend
End
