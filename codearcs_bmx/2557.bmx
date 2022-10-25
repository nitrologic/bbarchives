; ID: 2557
; Author: Mahan
; Date: 2009-08-08 07:12:37
; Title: TSprite
; Description: A very basic sprite system

'
' TSprite - a very basic sprite system.
'
' Although rudimentary it keeps track of each sprites unique properties
' and draws them all to the screen.
'
' zOrder, Accessed through GetzOrder()/SetzOrder(), makes it simple
' for the user to control the order in which they are drawn.
' (the higher the zOrder the later it is drawn and thus appearing "on top"
' of previously draw sprites)
'
' Usage:
'	Create Sprites:
'		local mySprite:TSprite=TSprite.createSprite(image:TImage)
'
'	The createSprite() call also has lots of default params (see source below)
'   to set the sprites initial values.
'
'	Delete Sprites:
'		TSprite.deleteSprite(sprite:TSprite)
'
' 	Draw all sprites:
' 
'		TSprite.drawAll()
' 
'
' A sprite uses a TImage for its appearance. (The same TImage can be used for several sprites.)
'
' More on zOrder: If several sprites share the same zOrder, they are drawn in the order they got
' their zOrder (or where created, if not set manually later, as zOrder is set during creation)
'
'
' Author: Mattias Hansson aka MaHan (2009)
'

Type TSprite

	Const EPSILON:Float = 0.0001
	Const WHITE:Int = $ff
	
	'The sprite list keeps the sprites in drawing order
	Global _spriteList:TList = New TList
	
	Field image:TImage  ' The image-ref containing the sprite appearance 
	Field angle:Float   ' angle (in degrees) of sprite
	Field alpha:Float   ' sprite alpha
	Field blendMode:Int ' sprite blendmode
	Field colorR:Int    ' Red color component
	Field colorG:Int    ' Green color component
	Field colorB:Int    ' blue color component
	Field scaleX:Float  ' horizontal scale (where 1.0 is original size)
	Field scaleY:Float  ' vertical scale (where 1.0 is original size)
	Field x:Int         ' x-position
	Field y:Int         ' y-position
	Field height:Int    ' height of sprite
	Field width:Int     ' width of sprite
	Field _zOrder:Int   ' Z-order of the sprite
	Rem
	bbdoc: Get the zOrder value in this TSprite object.
	End Rem
	Method GetzOrder:Int()
		Return _zOrder
	End Method
	Rem
	bbdoc: Set the zOrder value for this TSprite object.
	End Rem
	Method SetzOrder(Value:Int)
		_zOrder = Value
		_spriteList.Remove(Self)
		Local inserted:Int = False
		For Local sprite:TSprite = EachIn Self._spriteList
			If sprite._zOrder > Self._zOrder Then
				Self._spriteList.InsertBeforeLink(Self, Self._spriteList.FindLink(sprite))
				inserted = True
				Exit
			End If
		Next
		If Not inserted Then
			Self._spriteList.AddLast(Self)
		End If
	End Method
	
	
	Function drawAll()
		For Local sprite:TSprite = EachIn Self._spriteList
			sprite._draw()
		Next
	End Function
	
	Method _draw()
		Local rotated:Int = False
		Local scaled:Int = False
		Local colored:Int = False
		Local alphad:Int = False

				
		'Set rotation and scaling (of not default)
		If (Abs(Self.angle - 0.0) > EPSILON) Then
			rotated = True
		End If
		
		' scale this sprite (if needed)
		If (Abs(Self.scaleX - 1.0) > EPSILON) Or (Abs(Self.scaleY - 1.0) > EPSILON) Then
			scaled = True
		End If

		If scaled Or rotated Then
			SetTransform(Self.angle, Self.scaleX, Self.scaleY)
		End If
		
		If Not ((Self.colorR = WHITE) And (Self.colorG = WHITE) And (Self.colorB = WHITE)) Then
			SetColor(Self.colorR, Self.colorG, Self.colorB)
			colored = True
		End If

		If Not (Self.blendMode = MASKBLEND) Then
			SetBlend(Self.blendMode)
		End If
		
		If (Abs(Self.alpha - 1.0) > EPSILON) Then
			SetAlpha(Self.alpha)
			alphad = True
		End If
		
						
		DrawImage(Self.image, Self.x, Self.y)

		
		If alphad Then
			SetAlpha(1)
		End If
		
		If Not (Self.blendMode = MASKBLEND) Then
			SetBlend(MASKBLEND)
		End If
		
		If colored Then
			SetColor(WHITE, WHITE, WHITE)
		End If

		If scaled Or rotated Then
			SetTransform()
		End If
		
		
	End Method
	
	' factory method
	Function createSprite:TSprite(image:TImage, x:Int = 0, y:Int = 0, zOrder:Int = 0, scaleX:Float = 1.0, scaleY:Float = 1.0, angle:Float = 0, alpha:Float = 1.0, blendmode:Int = MASKBLEND)
		Local ns:TSprite = New TSprite
		
		ns.image = image
		ns.x = x
		ns.y = y
		ns.scaleX = scaleX
		ns.scaleY = scaleY
		ns.angle = angle
		ns.alpha = alpha
		ns.blendMode = blendMode
		ns.colorR = WHITE
		ns.colorG = WHITE
		ns.colorB = WHITE

		ns.SetzOrder(zOrder)
		
		Return ns
	End Function
	
	Function deleteSprite(sprite:TSprite)
		_spriteList.Remove(sprite)
	End Function
	
End Type
