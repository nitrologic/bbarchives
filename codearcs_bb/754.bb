; ID: 754
; Author: sswift
; Date: 2003-07-31 06:09:20
; Title: BlurTexture()
; Description: This function blurs a texture using 3D acceleration.

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
Function BlurTexture(Texture, Blur_Quality, Blur_Radius#)

	; This is used for temporary storage of the meshes used for soft shadow blurring.
	Local BlurMesh[16*4]
	Local Loop
	Local Blur_Cam

	Local BLUR_CAM_X# = 65536.0
	Local BLUR_CAM_Y# = 65536.0
	Local BLUR_CAM_Z# = 0.0

	; If blurring is enabled...
	If Blur_Quality > 0

		Blur_Cam = CreateCamera()

		; Set the camera viewport to the same size as the texture.		
		CameraViewport Blur_Cam, 0, 0, TextureWidth(Texture), TextureHeight(Texture)

		; Set the camera so it clears the color buffer before rendering the texture.
		CameraClsColor Blur_Cam, 0, 0, 0
		CameraClsMode  Blur_Cam, True, True						

		; Set the camera's range to be very small so as to reduce the possiblity of extra objects making it into the scene.
		CameraRange Blur_Cam, 0.1, 100
	
		; Set the camera to zoom in on the object to reduce perspective error from the object being too close to the camera.
		CameraZoom Blur_Cam, 16.0

		; Aim camera straight down.	
		RotateEntity Blur_Cam, 90, 0, 0, True
		
		; Position the blur camera far from other entities in the world.
		PositionEntity Blur_Cam, BLUR_CAM_X#, BLUR_CAM_Y#, BLUR_CAM_Z#
		
		; Create the sprites to use for blurring the shadow maps.
		For Loop = 0 To (Blur_Quality*4)-1
			BlurMesh[Loop] = CreateSprite()
		Next
		
		; Set the texture blend mode to multiply.
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
			EntityAlpha BlurMesh[Loop], 1.0 / Float(Loop+1)
			ScaleSprite BlurMesh[Loop], 2, 2
																						
			BlurAngle# = BlurAngleStep# * Float(Loop) + 180.0*(Loop Mod 2)
							
			Xoff# = BlurRadius# * Cos(BlurAngle#)
			Yoff# = BlurRadius# * Sin(BlurAngle#)

			PositionEntity BlurMesh[Loop], BLUR_CAM_X# + Xoff#, BLUR_CAM_Y# - 16.0, BLUR_CAM_Z# + Yoff#, True
					
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
