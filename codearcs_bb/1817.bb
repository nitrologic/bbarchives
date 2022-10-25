; ID: 1817
; Author: Chroma
; Date: 2006-09-18 11:37:27
; Title: Spacesphere
; Description: A simple good looking skybox, for space!

;Spacesphere

;Create the Texture
sptex = CreateTexture(1024,1024,1+8)
SetBuffer TextureBuffer(sptex)
For a = 1 To 200
	Plot Rand(0,1023),Rand(0,1023)
Next
SetBuffer BackBuffer()
TextureBlend sptex,5

;Create the Sphere
spbox = CreateSphere(5)
ScaleEntity spbox,10000,10000,10000
EntityTexture spbox,sptex
ScaleTexture sptex,.25,.5
EntityFX spbox,1
FlipMesh spbox
EntityOrder spbox,99999
