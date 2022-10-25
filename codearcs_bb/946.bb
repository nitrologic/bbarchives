; ID: 946
; Author: EdzUp[GD]
; Date: 2004-02-26 12:01:38
; Title: Predator cloak
; Description: A nice classy predator cloaking effect

Function EFPSDoPred( Camera, Entity, Xoffset, YOffset, FX=0 )
	;Create some temp graphics images
	Local PredBacking = CreateImage( GraphicsWidth(), GraphicsHeight() )
	Local PredImage = CreateImage( GraphicsWidth(), GraphicsHeight() )
	Local PredImage2 = CreateImage( GraphicsWidth(), GraphicsHeight() )
	Local PredImage3 = CreateImage( GraphicsWidth(), GraphicsHeight() )
	Local PredImage4 = CreateImage( GraphicsWidth(), GraphicsHeight() )

	;distance system for calculations
	Local PredDist# = EntityDistance#( Camera, Entity )

	If PredDist#>0 Then PredDist# = PredDist# /10 Else PredDist# = 0.0001

	;colour entity
	EntityColor Entity, 0, 0, 0
	EntityFX Entity, 1
	
	HideEntity Entity
	RenderWorld
	GrabImage PredBacking, 0, 0			;grab background
	ShowEntity Entity
	RenderWorld
	GrabImage PredImage, 0, 0			;grab templates
	GrabImage PredImage2, 0, 0
	GrabImage PredImage3, 0, 0
	GrabImage PredImage4, 0, 0
	
	Cls
	DrawImage PredBacking, 0, 0			;so there is no black bits
	DrawImage PredBacking, ( XOffset /PredDist# )*4, ( YOffset /PredDist# )*4
	DrawImage PredImage2, ( XOffset /PredDist# )*3, ( YOffset /PredDist# )*3
	DrawImage PredImage3, ( XOffset /PredDist# )*2, ( YOffset /PredDist# )*2
	DrawImage PredImage4, XOffset/PredDist#, YOffset/PredDist#
	DrawImage PredImage, 0, 0

	EntityColor Entity, 255, 255, 255	;reset entity
	EntityFX Entity, FX
	
	FreeImage PredImage2				;free images
	FreeImage PredImage3
	FreeImage PredImage4
	FreeImage PredImage
	FreeImage PredBacking
End Function
