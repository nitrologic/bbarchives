; ID: 1394
; Author: ozak
; Date: 2005-06-12 07:06:47
; Title: Sprite class
; Description: Sprite class

' Frame independent sprite class with animation support by Odin Jensen (www.furi.dk)
' Free to use as you please :)

' Sprite class
Type Sprite

	' Position
	Field x:Float = 0
	Field y:Float = 0
	
	' Velocity in pixels
	Field velx:Float = 0
	Field vely:Float = 0
	
	' Sprite image
	Field Image:TImage
	
	' Current animation frame
	Field CurAnimFrame:Int = 0
	
	' Width and height of sprite
	Field Width:Int = 0
	Field Height:Int = 0
	
	' Animation frame list
	Field FrameIndices:Int[] = Null
	
	' Animation timer vars
	Field AnimDelay:Int = 0
	Field AnimTime:Int = 1000
	Field CurAnimIndex:Int = 0
	
	' Load sprite
	Method Load(URL:Object, TileWidth:Int, TileHeight:Int, FirstTile:Int, NumTiles:Int)
	
		' Load image
		Image = LoadAnimImage(URL, TileWidth, TileHeight, FirstTile, NumTiles)
		
		' Save dimensions
		Width = TileWidth
		Height = TileHeight
		
		' Set cur anim frame
		CurAnimFrame = 0
		
	EndMethod
	
	' Clone sprite
	Method Clone:Sprite()
	
		' Create new copy
		Local NewSprite:Sprite = New Sprite
		
		' Copy pointers to indices/image
		NewSprite.SetFrameIndices(FrameIndices)
		NewSprite.SetImage(Image, Width, Height)
		NewSprite.SetAnimDelay(AnimDelay)
		NewSprite.SetX(x)
		NewSprite.SetY(y)
		NewSprite.SetVelocityX(velx)
		NewSprite.SetVelocityY(vely)
		
		Return NewSprite		
	
	EndMethod
	
	' Update sprite
	Method Update(DeltaTime:Int)
	
		' Update movement
		x = x + (velx * DeltaTime)
        y = y + (vely * DeltaTime)

		' Update animation
		If (FrameIndices)
		
			' Increase anim timer
			AnimTime = AnimTime + DeltaTime
			
			' Is it time?
			If (AnimTime > AnimDelay)
			
				' Reset anim time
				AnimTime = 0
				
				' Ok to advance?
				If (CurAnimIndex < (FrameIndices.length-1))	
				
					CurAnimIndex = CurAnimIndex + 1
									
				Else
				
					CurAnimIndex = 0
				
				EndIf
				
				CurAnimFrame = FrameIndices[CurAnimIndex]
			EndIf
		
		EndIf
	
	EndMethod
	
	' Set image
	Method SetImage(Image:TImage, Width:Int, Height:Int)
	
		self.Image = Image
		self.Width = Width
		self.Height = Height
	
	EndMethod
	
	' Draw sprite
	Method Draw()
	
		DrawImage(Image, x, y, CurAnimFrame)
	
	EndMethod
	
	' Set current frame
	Method SetCurFrame(Frame:Int)
	
		CurAnimFrame = Frame
	
	EndMethod
	
	' Set animation delay
	Method SetAnimDelay(NewDelay:Int)
	
		AnimDelay = NewDelay
	
	EndMethod
	
	' Set frame index list
	Method SetFrameIndices(Indices:Int[])
	
		FrameIndices = Indices
	
	EndMethod
	
	' Get image for manipulation
	Method GetImage:TImage()
	
		Return Image
	
	EndMethod
	
	' Set X
	Method SetX(x:Float)
	
		self.x = x	
	
	EndMethod
	
	' Set Y
	Method SetY(y:Float)
	
		self.y = y
	
	EndMethod
	
	' Get X
	Method GetX:Float()
	
		Return x
	
	EndMethod
	
	' Get Y
	Method GetY:Float()
	
		Return y
	
	EndMethod
	
	' Set x velocity
	Method SetVelocityX(vx:Float)
	
		velx = vx
	
	EndMethod
	
	' Set y velocity
	Method SetVelocityY(vy:Float)
	
		vely = vy
	
	EndMethod

	' Get width of sprite
	Method GetWidth:Int()
	
		Return Width
	
	EndMethod
	
	' Get height of sprite
	Method GetHeight:Int()
	
		Return Height
	
	EndMethod		

EndType
