; ID: 2175
; Author: tesuji
; Date: 2008-01-01 04:03:44
; Title: Interstellar Overdrive
; Description: Another simple 3D Starfield example with motion blur

' -------------------------------------
' Interstellar Overdrive
' 3D Starfield example
' Use Up/Down to accelerate/decelerate
' Tesuji 2008
' -------------------------------------

SuperStrict

'Graphics(1280,800,32,0)
Graphics(800,600,32,0)

Const MAX_STARS:Int = 2048

Local starImage:TImage = drawStarImage()
Local stars:Star[MAX_STARS]
For Local i:Int = 0 To MAX_STARS-1
	stars[i] = Star.Create(starImage)
Next

Local speed:Float = .5

While Not KeyHit(KEY_ESCAPE)

	' motion blur cls
	SetOrigin 0,0
	SetAlpha .35
	SetBlend ALPHABLEND
	SetScale 1.0,1.0
	SetColor 0,0,0
	DrawRect 0,0,GraphicsWidth(),GraphicsHeight()

	For Local s:Star = EachIn stars
		s.update(speed)
		s.render()
	Next
	
	If KeyDown(KEY_UP) speed :* 1.01
	If KeyDown(KEY_DOWN) speed :* .99	
	
	Flip

Wend

End

' -----------------------------------------------------------------------------

Type Star

	Field x:Float,y:Float
	Field xPos:Float,yPos:Float,zPos:Float
	Field zVel:Float
	Field intensity:Float
	Field size:Float
	Field image:TImage
	
	Function Create:Star(starImage:TImage)
          Local s:Star = New Star
		  s.init()
		  s.image = starImage
          Return s
    End Function

	Method init()
		xPos = Rnd(-1,1)
		yPos = Rnd(-1,1)
		zPos = Rnd(900,1000)
		zVel = Rnd(0.5,5)
		size = Rnd(0.1,.5)
		If Rand(0,100) = 0 Then size = size * 4
	End Method
	
	Method update(speed:Float=1.0)

		zPos :- Abs(zVel*speed)
		x = (xPos/zPos) * 256
		y = (yPos/zPos) * 256
		intensity = 1-(zPos*.001)
		If x < -1 Or x > 1 Or y < -1 Or y > 1 Or zPos < 1 Or zPos > 1000
			init()
		End If
		
	End Method
	
	Method render()

		SetOrigin GraphicsWidth()/2, GraphicsHeight()/2	
		SetImageHandle image,ImageWidth(image)/2,ImageHeight(image)/2
		
		SetScale size*intensity*.5, size*intensity*.5
		SetBlend LIGHTBLEND
		SetRotation 0
		SetAlpha intensity
		SetColor 255,255*intensity,255*intensity
		DrawImage image, x*(GraphicsWidth()/2), y*(GraphicsHeight()/2)
	
	End Method
	
End Type

' -----------------------------------------------------------------------------

Function drawStarImage:TImage()

	Local starImage:TImage = CreateImage(64,64)

	Cls
	SetBlend LIGHTBLEND
	SetAlpha .05
	For Local i:Int = 1 To 32
		SetColor (32-i)*8,(32-i)*8,255
		DrawOval (32-i),(32-i), i*2,i*2  
	Next
	SetLineWidth 3.0
	SetColor 128,128,255
	SetAlpha .125
	DrawLine 32,0,32,64
	DrawLine 8,32,56,32
	SetLineWidth 1.0
	DrawLine 32,4,32,60
	DrawLine 12,32,52,32
	DrawLine 4,4,60,60
	DrawLine 60,4,4,60
	
	GrabImage starImage,0,0
	Return starImage
	 
End Function
