; ID: 1173
; Author: poopla
; Date: 2004-10-11 07:17:38
; Title: Single surface pixel perfect sprite base.
; Description: Base code for single surface PP sprites.

Global Cam
	
Type SpriteSource
	
	Field Mesh%, Surf%
	Field Texture%
	Field Mask%
	Field Frames%, FramesPerRow%
	Field Animated%
	
	Field RefImage%
	
End Type

Function SpriteSource_Create%(ParentObject%, File$, Flags% = 4, FramesPerRow% = -1)

	viewwidth=GraphicsWidth()
	viewheight=GraphicsHeight()
	
	S.SpriteSource = New SpriteSource
;	NameEntity(magic,"pixiespace")
;	aspect#=Float(viewheight)/viewwidth
;	PositionEntity magic,-1,aspect,1 
;	scale#=2.0/viewwidth 
;	ScaleEntity magic,scale,-scale,-scale 
	
	S\Mesh = CreateMesh(ParentObject)
	EntityFX(S\Mesh, 2+32)
	S\Surf = CreateSurface(S\Mesh)
	PositionEntity S\Mesh, -.5, .5, 0
	
	S\Texture = LoadTexture(File, Flags)
	S\RefImage = LoadImage(File)
	
	If S\Texture <> 0 Then
		EntityTexture S\Mesh, S\Texture
		
		If FramesPerRow <> -1 And FramesPerRow > 0 Then
			S\FramesPerRow = FramesPerRow
			S\Animated = True	
		EndIf
	EndIf
	
	Return Handle(S)
	
End Function

Function SpriteSource_Mask(Source%, MaskTex%)

	SR.SpriteSource = Object.SpriteSource(Source)
	SR\Mask = MaskTex
	EntityTexture(SR\Mesh, SR\Mask, 0, 1)

End Function

Function SpriteSource_TexBlend(Source%, Blend%)

	S.SpriteSource = Object.SpriteSource(Source)
	TextureBlend(S\Texture, Blend)

End Function

Function SpriteSource_Blend(Source%, Blend%)

	SR.SpriteSource = Object.SpriteSource(Source)
	EntityBlend(SR\Mesh, Blend)

End Function

Function SpriteSource_SetAnimation%(Source%, FramesPerRow%)

	S.SpriteSource = Object.SpriteSource(Source)
	S\FramesPerRow = FramesPerRow
	S\Animated = True

End Function

Function SpriteSource_SetDrawOrder(Source%, Order%)

	S.SpriteSource = Object.SpriteSource(Source)
	EntityOrder(S\Mesh, Order)
	
End Function
;Build a sprite cache for the specified sprite source.   Grab available sprites in the cache with
;SpriteSource_AvailSprite()
Function SpriteSource_CacheSprites(Source%, W%, H%, Count%)
	
	S.SpriteSource = Object.SpriteSource(Source)
	;If S = Null Then End
		
	For CS = 1 To Count
		
		Sprite_Create%(Source, W, H)
		
	Next
	
End Function

Function SpriteSource_AvailSprite%(Source%)

	SR.SpriteSource = Object.SpriteSource(Source)

	For S.Sprite = Each Sprite
		If S\Source = SR And S\Avail = True Then
			S\Avail = False
			Return Handle(S)
		EndIf
	Next

End Function

Type Sprite

	Field Source.SpriteSource
	Field VertRef%
	Field TriRef%
	
	Field Draw%
	
	Field DrawX%, DrawY%
	Field Width%, Height%
	
	Field HandleX%, HandleY%
	
	Field RotAngle#, OffsetAngle%

	Field R%,G%, B%
	
	Field CurFrameColumn#, CurFrame#, AnimTime%, AnimTimeMax%
	Field Animated%
	
	Field Active%
	Field Avail%
	
End Type

Function Sprite_Create%(Source%, W% = 0, H% = 0)
	
	S.Sprite = New Sprite
	S\Source = Object.SpriteSource(Source)
	S\Avail = True
	
	If W = 0 Or H = 0 Then
		S\Width = ImageWidth(S\Source\RefImage)
		S\Height = ImageWidth(S\Source\RefImage)
	Else
		S\Width = W
		S\Height = H	
	EndIf

	S\R = 255
	S\G = 255
	S\B = 255
	
	Sprite_CreateQuad(S)

	Return Handle(S)
	
End Function

Function Sprite_Load(ParentObject, File$, Flags%, FramesPerRow= -1)

	SpriteSource = SpriteSource_Create%(ParentObject, File, Flags, FramesPerRow)
	Sprite = Sprite_Create(SpriteSource)

	Return Spr

End Function

Function Sprite_Copy(Sprite)

	S.Sprite = Object.Sprite(Sprite)

	Return Sprite_Create(Handle(S\Source))

End Function

;Call this when you are no longer using the sprite in question.
Function Sprite_MakeAvailable(Sprite%)

	S.Sprite = Object.Sprite(Sprite)
	S\Avail = True
	
	S\CurFrame = 0
	S\CurFrameColumn = 0
	
End Function

;Resize the sprite to the specified dimensions
Function Sprite_Resize%(Sprite%, W%, H%)

	S.Sprite = Object.Sprite(Sprite)
	S\Width = W
	S\Height = H

End Function

Function Sprite_GetWidth(Sprite%)

	S.Sprite = Object.Sprite(Sprite)
	Return S\Width
	
End Function

Function Sprite_GetHeight(Sprite%)

	S.Sprite = Object.Sprite(Sprite)
	Return S\Height
	
End Function

Function Sprite_Animate(Sprite%, Time%)
	
	S.Sprite = Object.Sprite(Sprite)
	If S\Source\Animated = True
		S\AnimTimeMax = Time%
		S\AnimTime = Time
		S\Animated = True
	EndIf
	
End Function

Function Sprite_SetFrame(Sprite%, Frame%)

	S.Sprite = Object.Sprite(Sprite)

	If Frame < S\Source\FramesPerRow ^ 2
		CColumn% = Frame / S\Source\FramesPerRow
		CFrame% = Frame - (Column * S\Source\FramesPerRow)
	EndIf

	S\CurFrame = CFrame
	S\CurFrameColumn = CColumn

End Function

Function Sprite_Color(Sprite%, R%, G%, B%)

	S.Sprite = Object.Sprite(Sprite)

	S\R = R
	S\G = G
	S\B = B

	VertexColor(S\Source\Surf, S\VertRef, R, G, B)
	VertexColor(S\Source\Surf, S\VertRef+1, R, G, B)
	VertexColor(S\Source\Surf, S\VertRef+2, R, G, B)
	VertexColor(S\Source\Surf, S\VertRef+3, R, G, B)
	
End Function

Function Sprite_Alpha(Sprite%, Alpha#)

	S.Sprite = Object.Sprite(Sprite)

	VertexColor(S\Source\Surf, S\VertRef, S\R, S\G, S\B, Alpha)
	VertexColor(S\Source\Surf, S\VertRef+1, S\R, S\G, S\B, Alpha)
	VertexColor(S\Source\Surf, S\VertRef+2, S\R, S\G, S\B, Alpha)
	VertexColor(S\Source\Surf, S\VertRef+3, S\R, S\G, S\B, Alpha)
	
End Function

Function Sprite_Draw(Sprite%, X, Y, Angle# = 0)
	
	S.Sprite = Object.Sprite(Sprite)
	
	S\Draw = True
	S\DrawX = X
	S\DrawY = Y

	S\RotAngle = Angle
	
End Function

Function Sprite_Update(Delta# = 1.0)
	
	For S.Sprite = Each Sprite
		If S\Draw = True Then
			If S\Source\Animated = True
				S\AnimTime = S\AnimTime - 1
				If S\AnimTime = 0 Then
					S\AnimTime = S\AnimTimeMax

					S\CurFrame = S\CurFrame + 1
					If S\CurFrame = S\Source\FramesPerRow
						S\CurFrame = 0
						
						S\CurFrameColumn = S\CurFrameColumn + 1
						If S\CurFrameColumn = S\Source\FramesPerRow
							S\CurFrameColumn = 0
						EndIf
					EndIf
					
	
					Dims# = 1.0 / Float(S\Source\FramesPerRow)					
					SU# = Dims * S\CurFrame
					SV# = Dims * S\CurFrameColumn
								
					VertexTexCoords(S\Source\Surf, S\VertRef, SU, SV)
					VertexTexCoords(S\Source\Surf, S\VertRef+1, SU+Dims, SV)
					VertexTexCoords(S\Source\Surf, S\VertRef+2, SU, SV+Dims)
					VertexTexCoords(S\Source\Surf, S\VertRef+3, SU+Dims, SV+Dims)
				EndIf
			EndIf			
			
			S\Draw = False
		EndIf
	Next
	
End Function

Function Sprite_RenderAll()

	For S.Sprite = Each Sprite
		If S\Draw = True Then

			;Top or "rear" vertices
			Mag# = Sqr(S\Width*S\Width + S\Height*S\Height)*.5 

			CosVal# = Cos(S\RotAngle+S\OffsetAngle)
			SinVal# = Sin(S\RotAngle+S\OffsetAngle)
			
			Atan2Val# = ATan2(S\Height, S\Width)
			
			VX# = (Cos((S\RotAngle+S\OffsetAngle) - Atan2Val)*Mag) + S\DrawX
			VY# = (Sin((S\RotAngle+S\OffsetAngle) - Atan2Val)*Mag) + S\DrawY			
			VertexCoords(S\Source\Surf, S\VertRef+1, VX , VY, 0)

			VX# = (Cos((S\RotAngle+S\OffsetAngle) + Atan2Val)*Mag) + S\DrawX
			VY# = (Sin((S\RotAngle+S\OffsetAngle) + Atan2Val)*Mag) + S\DrawY			
			VertexCoords(S\Source\Surf, S\VertRef+3, VX , VY, 0)


			VX# = (Cos((S\RotAngle+S\OffsetAngle+180) - Atan2Val)*Mag) + S\DrawX
			VY# = (Sin((S\RotAngle+S\OffsetAngle+180) - Atan2Val)*Mag) + S\DrawY			
			VertexCoords(S\Source\Surf, S\VertRef+2, VX , VY, 0)


			VX# = (Cos((S\RotAngle+S\OffsetAngle+180) + Atan2Val)*Mag) + S\DrawX
			VY# = (Sin((S\RotAngle+S\OffsetAngle+180) + Atan2Val)*Mag) + S\DrawY			
			VertexCoords(S\Source\Surf, S\VertRef, VX , VY, 0)
		
		ElseIf S\Draw = False Then
			VX# = -100
			
			VertexCoords(S\Source\Surf, S\VertRef+1, VX , VY, 0)	
			VertexCoords(S\Source\Surf, S\VertRef+3, VX , VY, 0)	
			VertexCoords(S\Source\Surf, S\VertRef+2, VX , VY, 0)	
			VertexCoords(S\Source\Surf, S\VertRef, VX , VY, 0)			 
								
		EndIf
	Next

End Function

Function Sprite_Flip(Sprite%, Dir%)

	S.Sprite = Object.Sprite(Sprite)

	If Dir = False Then
		
	Else

	EndIf

End Function

Function Sprite_CreateQuad(S.Sprite)

	V1 = AddVertex(S\Source\Surf, 0, 0, 0, 0, 0)
	V2 = AddVertex(S\Source\Surf, 0, 0, 0, 1.0, 0.0)
	V3 = AddVertex(S\Source\Surf, 0, 0, 0, 0, 1.0)
	V4 = AddVertex(S\Source\Surf, 0, 0, 0, 1.0, 1.0)
	
;	VertexColor S\Source\Surf, V1, 255, 0, 0
;	VertexColor S\Source\Surf, V2, 0, 255, 0
;	VertexColor S\Source\Surf, V3, 0, 0, 255
;	VertexColor S\Source\Surf, V4, 255, 255, 255	
	S\VertRef = V1
	
	T1 = AddTriangle(S\Source\Surf, V1, V2, V3)
	AddTriangle(S\Source\Surf, V3, V2, V4)
	
End Function
