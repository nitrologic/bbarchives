; ID: 372
; Author: Wiebo
; Date: 2002-08-04 09:23:53
; Title: Create object shadows on big terrains
; Description: Create object shadows on terrains

Function CreateShadowMap()

	; rotate world objects and color them black. you could also set entityalpha, obj\entity_near, 0.5

	 angle = 90		; adjust this according to your world time. 0 = noon, 90 = almost dark
		
	For obj.obj = Each obj
	
		RotateEntity obj\entity_near, 0, 0, angle, True
		EntityColor obj\entity_near, 0, 0, 0
		ShowEntity obj\entity_near
		EntityFX obj\entity_near, 1
	Next

	; texture terrain with lightmap, so we can grab it.
	TextureBlend world\texture1,1
	TextureBlend world\alphamap,1
	TextureBlend world\texture2,1
	TextureBlend world\lightmap,1
	EntityTexture world\terrain, world\lightmap, 0, 1
	
	; setup cam
	; make sure that camera is at correct height, so that terrain is in the middle of the screen.
	
	CameraFogMode world\camera, 0
	CameraRange world\camera, 1, 810
	PositionEntity heli\camerapivot, world\terrain_scale /4, 0, world\terrain_scale /4; middle of terrain
	RotateEntity world\camera, 90, 0, 0, True
	PositionEntity world\camera, -.05, 800, 0
	CameraZoom world\camera, 16

	; grab new texture. my terrain texture dimension is 512.
	
	RenderWorld()
	CopyRect (GraphicsWidth() / 2) - 256, (GraphicsHeight() / 2) - 256, 512,512, 0,0, BackBuffer(), TextureBuffer (world\lightmap)

	; restore obj settings
	
	For obj.obj = Each obj
		EntityColor obj\entity_near, 255,255,255
		EntityAlpha obj\entity_near, 1
		RotateEntity obj\entity_near, obj\pitch, obj\yaw, obj\roll		; get original rotational angles
	Next
	
	; restore terrain (with NEW shadowmap)
	TextureBlend world\texture1, 2
	TextureBlend world\alphamap, 2
	TextureBlend world\texture2, 3
	TextureBlend world\lightmap, 2
	EntityTexture world\terrain, world\texture1, 0, 0
	EntityTexture world\terrain, world\alphamap, 0, 1
	EntityTexture world\terrain, world\texture2, 0, 2
	EntityTexture world\terrain, world\lightmap, 0, 3

	; restore camera to original position
	; [code snipped]

End Function
