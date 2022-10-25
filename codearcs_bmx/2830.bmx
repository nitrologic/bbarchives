; ID: 2830
; Author: tesuji
; Date: 2011-03-05 15:34:29
; Title: Spaghetti
; Description: Spaghetti doodle drawing code

SuperStrict

' --------------------
' spaghetti - tesuji 2011
' --------------------

Graphics 1024,768,32

Local img:TImage = TImageFactory. createGradientImage()
Local bimg:TImage = TImageFactory. createGradientImage()
Local simg:TImage = TImageFactory. createGradientImage(255,128)

Local MAX_TENDRILS:Int = 50
Local tendrils:TList = New TList
For Local i:Int = 0 To MAX_TENDRILS-1
	tendrils.addLast(New TTendril.Create(img,simg))
Next
renderBackground(bimg)

Local frame:Int = 0
While Not KeyHit(KEY_ESCAPE)

	For Local tendril:TTendril = EachIn tendrils
		If frame Mod 2 = 0 Then tendril.update()
		tendril.render()
	Next

	If KeyHit(KEY_SPACE) Then renderBackground(bimg, .25) 

	Flip 0
	If frame Mod 4 = 0 Then Delay 1
	frame :+ 1
	
Wend
End

' --------------------------------------------------------

Type TImageFactory

	Function createGradientImage:TImage(c0#=0,c1#=255)
	
		Local image:TImage = CreateImage(256,256, FILTEREDIMAGE)
		Local pix:TPixmap = LockImage(image)

		SetBlend SOLIDBLEND
		Local inc# = (c1-c0)/128.0
		For Local y:Int = 0 To 255
			c0 :+ inc
			If y = 128
				inc = -inc 
			End If
			SetColor c0,c0,c0
			DrawLine 0,y, 256,y
		Next
		GrabImage image,0,0
		UnlockImage image
		Return image 	
	
	End Function

End Type

Type TTendril

	Field x:Float=GraphicsWidth()/2,y:Float=GraphicsHeight()/2
	Field speed:Float=.5
	Field angle:Float = 90
	Field direction:Float = .25
	Field img:TImage
	Field shadowimg:TImage
	Field scale:Float =.125*.35
	
	Method Create:TTendril(img:TImage,shadowImg:TImage)
		Self.img = img
		Self.shadowImg = shadowImg
		MidHandleImage Self.img
		MidHandleImage Self.shadowImg
		Return Self
	End Method
	
	Method update()
		x :+ (Sin(90-angle)*speed)
		y :+ (Cos(90-angle)*speed)
		angle :+ direction
		
		If  Rnd(1.0) < .005 direction = -direction				
		If x < -GraphicsWidth()*.25 Then x = -GraphicsWidth()*.25
		If x > GraphicsWidth()+GraphicsWidth()*.25 Then x = GraphicsWidth()+GraphicsWidth()*.25
		If y < -GraphicsHeight()*.25 Then y = -GraphicsHeight()*.25
		If y > GraphicsHeight()+GraphicsHeight()*.25 Then y = GraphicsHeight()+GraphicsHeight()*.25
		
	End Method
	
	Method render()
	
		SetAlpha 1.0
		SetRotation angle
		SetColor 255,255,255

		SetBlend SHADEBLEND
		SetScale scale*.1,scale
		DrawImage shadowImg ,x+(scale*96),y+(scale*96)
		
		SetBlend ALPHABLEND
		SetScale scale*.1,scale
		DrawImage img ,x,y
		
		SetRotation 0
		SetScale 1,1
	
	End Method
	
End Type

Function renderBackground(img:TImage, alpha:Float=1.0)
		SetAlpha alpha
		SetBlend ALPHABLEND
		SetColor 255,255,255
		DrawImageRect img,0,0,GraphicsWidth(),GraphicsHeight()
		Flip 0
		DrawImageRect img,0,0,GraphicsWidth(),GraphicsHeight()
		Flip 0
End Function
