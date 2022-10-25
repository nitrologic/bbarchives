; ID: 815
; Author: sswift
; Date: 2003-10-25 10:58:34
; Title: Distant Object Renderer
; Description: Turns distant objects into a texture which you can then display as a sky.

; This is the location in the world where the blur camera will reside.
; This location should be far from any other geometry you have in your world.
Const DOA_BLUR_CAM_X# = 65536.0
Const DOA_BLUR_CAM_Y# = 65536.0
Const DOA_BLUR_CAM_Z# = 0.0


Const RENDER_DISTANCE_EPSILON# = 256


; Distant Object Accelerator

Type DOA_Camera

	Field Camera								; This is the camera which this DOA_Camera should augment.  If you have only one camera in your game, then set this to point to it.
	Field Camera_Zoom#							; These are the values set for the above camera.  They need to match it so that the augmented view matches the real view.
	Field Camera_Range_Near#
	Field Camera_Range_Far#
	Field Camera_FogMode
	Field Camera_FogRange_Near#
	Field Camera_FogRange_Far#	
	Field Camera_FogColor_R
	Field Camera_FogColor_G
	Field Camera_FogColor_B

	Field Resolution							; This is the resolution to render the augmented view's images at.  This must be a power of 2 in size.  The smaller this value the blurrier the objects in the distance will be.
	Field Range_Far#							; This is the distance which the augmented view should render out to.  This should be much greater than your main camera's far clipping plane.
		
	Field Cube_Mesh								
	Field Cube_Map[6]	
	Field Cube_Brush[6]
	Field Cube_Render_X#						; This is the location in the world at which the cube map was rendered.  It is used to determine if the map needs to be re-rendered.
	Field Cube_Render_Y#				
	Field Cube_Render_Z#
		
End Type	


; -------------------------------------------------------------------------------------------------------------------
; You must create a DOA_Camera and set it's properties before calling this function.
; -------------------------------------------------------------------------------------------------------------------
Function DOA_InitCam(CAM_Create.DOA_Camera)

	; Make sure we'll be able to render the textures when the time comes...
		If GraphicsHeight() < CAM_Create\Resolution   
			RuntimeError("Video mode not tall enough for this texture resolution!")
		EndIf

	; Create a mesh for this camera's skybox.
	
		CAM_Create\Cube_Mesh = DOA_CreateSkyBox()
		EntityFX CAM_Create\Cube_Mesh, 1+8+16
		;ScaleEntity CAM_Create\Cube_Mesh, 100, 100, 100
		
		; Render the skybox behind everything else.
		EntityOrder CAM_Create\Cube_Mesh, 255

	; Loop through each of the box's sides, create a brush+texture for it, and paint it.
	For LOOP_Side = 0 To 5

		CAM_Create\Cube_Brush[LOOP_Side] = CreateBrush()
		CAM_Create\Cube_Map[LOOP_Side]   = CreateTexture(CAM_Create\Resolution, CAM_Create\Resolution, 16+32+256)

		BrushTexture CAM_Create\Cube_Brush[LOOP_Side], CAM_Create\Cube_Map[LOOP_Side]
		PaintSurface GetSurface(CAM_Create\Cube_Mesh, LOOP_Side+1), CAM_Create\Cube_Brush[LOOP_Side]
		
	Next
	
End Function


; -------------------------------------------------------------------------------------------------------------------
; -------------------------------------------------------------------------------------------------------------------
Function DOA_Render(CAM_Main.DOA_Camera, Force_Render=0)

	; Hide the camera we're currently rendering with.
		HideEntity CAM_Main\Camera

	; Hide the skybox we're going to render to.
		HideEntity CAM_Main\Cube_Mesh

	; Store the location of the main camera.
		CAM_Main_X# = EntityX#(CAM_Main\Camera, True)
		CAM_Main_Y# = EntityY#(CAM_Main\Camera, True)
		CAM_Main_Z# = EntityZ#(CAM_Main\Camera, True)
	
	; Create a new camera to render with.
		CAM_Render = CreateCamera() 
	
	; Set the camera to clear the color and zbuffer.
		CameraClsMode CAM_Render, True, True
	
	; Set the camera view to a 90 degree fov, so that we can render the six views we need for a skybox.
		DOA_SetCameraFOV(CAM_Render, 90)
	
	; Set the camera's viewport to render the size of the texture requested.
		CameraViewport CAM_Render, 0, 0, CAM_Main\Resolution, CAM_Main\Resolution

	; Adjust the render camera's settings match the settings of the real camera.
		CameraFogMode  CAM_Render, CAM_Main\Camera_FogMode
		CameraFogRange CAM_Render, CAM_Main\Camera_FogRange_Near#, CAM_Main\Camera_FogRange_Far#
		CameraFogColor CAM_Render, CAM_Main\Camera_FogColor_R, CAM_Main\Camera_FogColor_G, CAM_Main\Camera_FogColor_B

	; Set the render camera to render starting from the real camera's far clipping plane (minus some fraction of the
	; range to keep holes from appearing in the map, and render out to the specified range.
		CameraRange CAM_Render, CAM_Main\Camera_Range_Far#-(CAM_Main\Camera_Range_Far#/1.5), CAM_Main\Range_Far#
	
	; Position the camera at the same location as the real camera.
		PositionEntity CAM_Render, CAM_Main_X#, CAM_Main_Y#, CAM_Main_Z# 

	; Position the skybox at the same location as the real camera. 
		PositionEntity CAM_Main\Cube_Mesh, CAM_Main_X#, CAM_Main_Y#, CAM_Main_Z#
						
	; Determine the distance of the main camera from the last location at which we rendered the scene.
		Difference# = Sqr((CAM_Main_X#-CAM_Main\Cube_Render_X#)^2.0 + (CAM_Main_Y#-CAM_Main\Cube_Render_Y#)^2.0 + (CAM_Main_Z#-CAM_Main\Cube_Render_Z#)^2.0)

		
	; If the skybox is out of date, rerender it.
	If Difference# >= RENDER_DISTANCE_EPSILON#

		;AntiAlias True 

		; Store the location in the world at which this cube map was rendered.
			CAM_Main\Cube_Render_X# = CAM_Main_X#
			CAM_Main\Cube_Render_Y#	= CAM_Main_Y#			
			CAM_Main\Cube_Render_Z# = CAM_Main_Z#
	
	
		;For LOOP_Side = 0 To 5			
		For LOOP_Side = 0 To 0
			
			Select LOOP_Side
			
				Case 0 ; North
					RotateEntity CAM_Render, 0, 0, 0

				Case 1 ; East
					RotateEntity CAM_Render, 0, -90, 0
					
				Case 2 ; South
					RotateEntity CAM_Render, 0, 180, 0
					
				Case 3 ; West
					RotateEntity CAM_Render, 0, 90, 0
	
				Case 4 ; Up
					RotateEntity CAM_Render, -90, 0, 0
				
				Case 5 ; Down
					RotateEntity CAM_Render, 90, 0, 0
			
			End Select
			
			; Render the view.
			RenderWorld
			
			; Copy the view to the texture.
			CopyRect 0, 0, CAM_Main\Resolution, CAM_Main\Resolution, 0, 0, BackBuffer(), TextureBuffer(CAM_Main\Cube_Map[LOOP_Side])
			
			; Blur the texture.

				;HideEntity CAM_Render
				;DOA_BlurTexture2(CAM_Main\Cube_Map[LOOP_Side], 1, 1.0)
				;ShowEntity CAM_Render
	
				; Reset the texture's blending mode, scale, and position, because DOA_BlurTexture modifies them.
				;TextureBlend CAM_Main\Cube_Map[LOOP_Side], 2
				;ScaleTexture CAM_Main\Cube_Map[LOOP_Side], 1, 1
				;PositionTexture CAM_Main\Cube_Map[LOOP_Side], 0, 0
			
			;AntiAlias False
			
		Next		
	
	EndIf
	
	; Show the skybox we just rendered to.
		ShowEntity CAM_Main\Cube_Mesh
	
	; Delete the render camera.
		FreeEntity CAM_Render
		
	; Show the main camera again.
		ShowEntity CAM_Main\Camera	
				
End Function		


; -------------------------------------------------------------------------------------------------------------------
; -------------------------------------------------------------------------------------------------------------------
Function DOA_CreateSkyBox()

	Sky = CreateMesh()

	; North

		SURF_Sky = CreateSurface(Sky)

		AddVertex SURF_Sky,+1,+1,+1,1,0
		AddVertex SURF_Sky,-1,+1,+1,0,0
		AddVertex SURF_Sky,-1,-1,+1,0,1
		AddVertex SURF_Sky,+1,-1,+1,1,1
		
		AddTriangle SURF_Sky,0,1,2
		AddTriangle SURF_Sky,0,2,3

	; East

		SURF_Sky = CreateSurface(Sky)
	
		AddVertex SURF_Sky,+1,+1,-1,1,0
		AddVertex SURF_Sky,+1,+1,+1,0,0
		AddVertex SURF_Sky,+1,-1,+1,0,1
		AddVertex SURF_Sky,+1,-1,-1,1,1
	
		AddTriangle SURF_Sky,0,1,2
		AddTriangle SURF_Sky,0,2,3

	; South

		SURF_Sky = CreateSurface(Sky)

		AddVertex SURF_Sky,-1,+1,-1,1,0 
		AddVertex SURF_Sky,+1,+1,-1,0,0
		AddVertex SURF_Sky,+1,-1,-1,0,1
		AddVertex SURF_Sky,-1,-1,-1,1,1

		AddTriangle SURF_Sky,0,1,2
		AddTriangle SURF_Sky,0,2,3

	; West

		SURF_Sky = CreateSurface(Sky)
	
		AddVertex SURF_Sky,-1,+1,+1,1,0
		AddVertex SURF_Sky,-1,+1,-1,0,0
		AddVertex SURF_Sky,-1,-1,-1,0,1
		AddVertex SURF_Sky,-1,-1,+1,1,1
	
		AddTriangle SURF_Sky,0,1,2
		AddTriangle SURF_Sky,0,2,3

	; Top

		SURF_Sky = CreateSurface(Sky)

		AddVertex SURF_Sky,-1,+1,+1,1,1
		AddVertex SURF_Sky,+1,+1,+1,1,0
		AddVertex SURF_Sky,+1,+1,-1,0,0
		AddVertex SURF_Sky,-1,+1,-1,0,1

		AddTriangle SURF_Sky,0,1,2
		AddTriangle SURF_Sky,0,2,3

	; Bottom	

		SURF_Sky = CreateSurface(Sky)
	
		AddVertex SURF_Sky,-1,-1,-1,0,1
		AddVertex SURF_Sky,+1,-1,-1,0,0
		AddVertex SURF_Sky,+1,-1,+1,1,0
		AddVertex SURF_Sky,-1,-1,+1,1,1
		
		AddTriangle SURF_Sky,0,1,2
		AddTriangle SURF_Sky,0,2,3

	;FlipMesh Sky
	Return Sky

End Function


; -------------------------------------------------------------------------------------------------------------------
; This function sets the camera's horizontal FOV, in degrees.
; -------------------------------------------------------------------------------------------------------------------
Function DOA_SetCameraFOV(Camera, FOV#)
	CameraZoom Camera, 1.0 / Tan(FOV#/2.0)	
End Function


; -------------------------------------------------------------------------------------------------------------------
; This function blurs a texture using a technique that takes advantage of 3D acceleration.  
;
; * You MUST hide all other cameras before calling this function!
; * You MUST reset your texture's blending mode, scale, and position after calling this function!
;
; Texture is the texture you want blurred.
;
; Blur_Quality defines the quality of the blur.  1 = 4 passes, 2 = 8 passes, 3 = 12 passes, etc.
;
; 	(The reason that the passes are in multiples of four is because interference artifacts are created when
; 	the number of passes is not a multiple of four... meaning that ten passes will actually look far worse
; 	than eight.)
;
; Blur_Radius# defines the radius of the blur, in pixels, assuming a map size of 256x256.
;
;	(Ie, a radius of 16 will be the same width regardless of whether the texture is 16x16 or 512x512.  It will
; 	only be exactly 16 pixels wide if the map is 256x256.)
; -------------------------------------------------------------------------------------------------------------------
Function DOA_BlurTexture(Texture, Blur_Quality, Blur_Radius#)

	; This is used for temporary storage of the meshes used for soft shadow blurring.
	Local BlurMesh[16*4]


	; If blurring is enabled...
	If Blur_Quality > 0

		Blur_Cam = CreateCamera()

		; Set the camera's range to be very small so as to reduce the possiblity of extra objects making it into the scene.
		CameraRange Blur_Cam, 0.1, 100
	
		; Set the camera to zoom in on the object to reduce perspective error from the object being too close to the camera.
		CameraZoom Blur_Cam, 16.0

		; Aim camera straight down.	
		RotateEntity Blur_Cam, 90, 0, 0, True

		; Set the camera viewport to the same size as the texture.		
		CameraViewport Blur_Cam, 0, 0, TextureWidth(Texture), TextureHeight(Texture)
				
		; Set the camera so it clears the color buffer before rendering the texture.
		CameraClsColor Blur_Cam, 0,0,0
		CameraClsMode  Blur_Cam, True, True						

		; Position the blur camera far from other entities in the world.
		PositionEntity Blur_Cam, DOA_BLUR_CAM_X#, DOA_BLUR_CAM_Y#, DOA_BLUR_CAM_Z#
		
		; Create the sprites to use for blurring the shadow maps.
		For Loop = 0 To (Blur_Quality*4)-1
			BlurMesh[Loop] = CreateSprite()
		Next

		; Set the caster texture to multiply blend mode so we can darken it by changing the entity
		; color when blurring.
		TextureBlend Texture, 2
												
		; Scale the texture down because we scale the sprites up so they fill a larger area of the
		; screen.  (Otherwise the edges of the texture are darker than the middle because they don't
		; get covered.
		ScaleTexture    Texture, 0.5, 0.5
		PositionTexture Texture, 0.5, 0.5
						
		; Blur texture by blitting semi-transparent copies of it on top of it.
		BlurRadius# = Blur_Radius# * (1.0 / 256.0)
		BlurAngleStep# = 360.0 / Float(Blur_Quality*4)

		; Normally we would just divide 255 by the number of passes so that adding all the passes
		; together would not exceed 256.  However, if we did that, then we could not have a number of
		; passes which does not divide 256 evenly, or else the error would result in the white part of
		; the image being slightly less than white.  So we round partial values up to ensure that
		; white will always be white, even if it ends up being a little whiter than white as a result
		; when all the colors are added, since going higher than white just clamps to white.
		BlurShade = Ceil(255.0 / Float(Blur_Quality*4))
		
		; Place each of the blur objects around a circle of radius blur_radius.
		For Loop = 0 To (Blur_Quality*4)-1
				
			EntityTexture BlurMesh[Loop], Texture
			EntityFX BlurMesh[Loop], 1+8
			EntityBlend BlurMesh[Loop], 3
			EntityColor BlurMesh[Loop], BlurShade, BlurShade, BlurShade
			ScaleSprite BlurMesh[Loop], 2, 2
																				
			BlurAngle# = BlurAngleStep# * Float(Loop) + 180.0*(Loop Mod 2)
							
			Xoff# = BlurRadius# * Cos(BlurAngle#)
			Yoff# = BlurRadius# * Sin(BlurAngle#)

			PositionEntity BlurMesh[Loop], DOA_BLUR_CAM_X# + Xoff#, DOA_BLUR_CAM_Y# - 16.0, DOA_BLUR_CAM_Z# + Yoff#, True
					
		Next
					
		; Render the new texture.
		RenderWorld
		
		; Copy the new texture from the screen buffer to the texture buffer.		
		CopyRect 0, 0, TextureWidth(Texture), TextureHeight(Texture), 0, 0, BackBuffer(), TextureBuffer(Texture)
						
		; Free the blur entities.
		For Loop = 0 To (Blur_Quality*4)-1
			FreeEntity BlurMesh[Loop]
		Next

		; Free the blur camera.
		FreeEntity Blur_Cam
			
	EndIf

End Function



; -------------------------------------------------------------------------------------------------------------------
; This function blurs a texture using a technique that takes advantage of 3D acceleration.  
;
; * You MUST hide all other cameras before calling this function!
; * You MUST reset your texture's blending mode, scale, and position after calling this function!
;
; Texture is the texture you want blurred.
;
; Blur_Quality defines the quality of the blur.  1 = 4 passes, 2 = 8 passes, 3 = 12 passes, etc.
;
; 	(The reason that the passes are in multiples of four is because interference artifacts are created when
; 	the number of passes is not a multiple of four... meaning that ten passes will actually look far worse
; 	than eight.)
;
; Blur_Radius# defines the radius of the blur, in pixels, assuming a map size of 256x256.
;
;	(Ie, a radius of 16 will be the same width regardless of whether the texture is 16x16 or 512x512.  It will
; 	only be exactly 16 pixels wide if the map is 256x256.)
; -------------------------------------------------------------------------------------------------------------------
Function DOA_BlurTexture2(Texture, Blur_Quality, Blur_Radius#)

	; This is used for temporary storage of the meshes used for soft shadow blurring.
	Local BlurMesh[16*4]


	; If blurring is enabled...
	If Blur_Quality > 0

		Blur_Cam = CreateCamera()

		; Set the camera's range to be very small so as to reduce the possiblity of extra objects making it into the scene.
		CameraRange Blur_Cam, 0.1, 100
	
		; Set the camera to zoom in on the object to reduce perspective error from the object being too close to the camera.
		CameraZoom Blur_Cam, 16.0

		; Aim camera straight down.	
		RotateEntity Blur_Cam, 90, 0, 0, True

		; Set the camera viewport to the same size as the texture.		
		CameraViewport Blur_Cam, 0, 0, TextureWidth(Texture), TextureHeight(Texture)
				
		; Set the camera so it clears the color buffer before rendering the texture.
		CameraClsColor Blur_Cam, 0,0,0
		CameraClsMode  Blur_Cam, True, True						

		; Position the blur camera far from other entities in the world.
		PositionEntity Blur_Cam, DOA_BLUR_CAM_X#, DOA_BLUR_CAM_Y#, DOA_BLUR_CAM_Z#
		
		; Create the sprites to use for blurring the shadow maps.
		For Loop = 0 To (Blur_Quality*4)-1
			BlurMesh[Loop] = CreateSprite()
		Next
												
		; Scale the texture down because we scale the sprites up so they fill a larger area of the
		; screen.  (Otherwise the edges of the texture are darker than the middle because they don't
		; get covered.
		ScaleTexture    Texture, 0.5, 0.5
		PositionTexture Texture, 0.5, 0.5
						
		; Blur texture by blitting semi-transparent copies of it on top of it.
		BlurRadius# = Blur_Radius# * (1.0 / 256.0)
		BlurAngleStep# = 360.0 / Float(Blur_Quality*4)

		; Normally we would just divide 255 by the number of passes so that adding all the passes
		; together would not exceed 256.  However, if we did that, then we could not have a number of
		; passes which does not divide 256 evenly, or else the error would result in the white part of
		; the image being slightly less than white.  So we round partial values up to ensure that
		; white will always be white, even if it ends up being a little whiter than white as a result
		; when all the colors are added, since going higher than white just clamps to white.
		BlurShade = Ceil(255.0 / Float(Blur_Quality*4))
		
		; Place each of the blur objects around a circle of radius blur_radius.
		For Loop = 0 To (Blur_Quality*4)-1
				
			EntityTexture BlurMesh[Loop], Texture
			EntityFX BlurMesh[Loop], 1+8
			EntityAlpha BlurMesh[Loop], 1.0 / Float(Loop+1)
			ScaleSprite BlurMesh[Loop], 2, 2
																							
			BlurAngle# = BlurAngleStep# * Float(Loop) + 180.0*(Loop Mod 2)
							
			Xoff# = BlurRadius# * Cos(BlurAngle#)
			Yoff# = BlurRadius# * Sin(BlurAngle#)

			PositionEntity BlurMesh[Loop], DOA_BLUR_CAM_X# + Xoff#, DOA_BLUR_CAM_Y# - 16.0, DOA_BLUR_CAM_Z# + Yoff#, True
					
		Next
					
		; Render the new texture.
		RenderWorld
		
		; Copy the new texture from the screen buffer to the texture buffer.		
		CopyRect 0, 0, TextureWidth(Texture), TextureHeight(Texture), 0, 0, BackBuffer(), TextureBuffer(Texture)
						
		; Free the blur entities.
		For Loop = 0 To (Blur_Quality*4)-1
			FreeEntity BlurMesh[Loop]
		Next

		; Free the blur camera.
		FreeEntity Blur_Cam
			
	EndIf

End Function
