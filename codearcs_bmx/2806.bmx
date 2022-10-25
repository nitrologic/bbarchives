; ID: 2806
; Author: Jesse
; Date: 2011-01-03 13:52:15
; Title: collision
; Description: rotated image(rectangle) collision

SuperStrict
Type Tentity

	Field x:Float
	Field y:Float
	Field image:TImage
	Field angle:Float
	Field x1:Float
	Field y1:Float
	Field x3:Float
	Field y3:Float
	
	Function Create:Tentity(x:Float,y:Float,image:TImage,angle:Float)
		Local e:Tentity = New Tentity
		e.x = x
		e.y = y
		e.image = image
		e.angle = angle
		e.x1:Float = -image.handle_x
		e.y1:Float = -image.handle_y
		e.x3:Float =  image.width - image.handle_x
		e.y3:Float =  image.height - image.handle_y
		Return e
	End Function

	Method collidedpoint:Int(px:Float,py:Float)
		px = px - x
		py = py - y
		Local tx:Float = px*Cos(-angle) - py*Sin(-angle) 
		Local ty:Float = py*Cos(-angle) + px*Sin(-angle)  
		If tx > x1  
			If ty >y1 
				If tx < x3
					If ty < y3
						Return True
					EndIf
				EndIf
			EndIf
		EndIf
		Return False
	End Method
		
	Method display()
		SetRotation angle
		DrawImage image,x,y
		SetRotation 0
		DrawOval x-3,y-3,6,6
	End Method

End Type

Local img:TImage = CreateImage(64,64)
Local pixls:Int Ptr = Int Ptr(LockImage(img).pixels)
For Local i:Int = 0 Until img.width*img.height
	pixls[Rand(img.width*img.height-1)] = $ff00ff00
Next

SetImageHandle img,32,-32

Graphics 800,600
Local entity:tentity = Tentity.Create(300,300,img,45)

Local angle:Float  = 0

Repeat
	Cls()
	entity.angle = angle
	If entity.collidedpoint(MouseX(),MouseY())
		DrawText "collided",400,300
	EndIf
	entity.display()
	angle :-.5
	Flip()
Until KeyDown(key_escape)
