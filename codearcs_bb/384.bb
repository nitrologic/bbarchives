; ID: 384
; Author: Wiebo
; Date: 2002-08-05 10:48:35
; Title: move object shadow along terrains
; Description: This example shows how to use a texture to simulate shadows

; use cursor keys (left right) to move ball.
; watch the shadow!

Graphics3D 800,600,32
SetBuffer BackBuffer ()

EditCam = CreateCamera ()
PositionEntity EditCam, -2,4,-2
RotateEntity EditCam, 20, -45, 0

; create terrain. provide your own bitmap
terrain = LoadTerrain ("d:\blitz\3d\heli\editor\maps\hmap.bmp")
terrain_scale = 1

ScaleEntity terrain, terrain_scale, 16 ,terrain_scale

; colormap. provide your own texture
grasstex = LoadTexture ("d:\blitz\3d\heli\editor\maps\hmap_tex2.bmp")

; create shadow texture. clamp uv so texture doesn't repeat
shadowtex = CreateTexture (128,128, 1+16+32+256)

; create texture
ClsColor 255,255,255
Cls
Color 100,100,100
Oval 2, 2, 124, 124, 1
CopyRect 0,0,128,128,0,0, BackBuffer(), TextureBuffer(shadowtex)

; apply textures to terrain.
EntityTexture terrain, grasstex, 0, 0
EntityTexture terrain, shadowtex, 0, 1
TextureBlend grasstex, 1

; make a test object
testbal = CreateSphere()
PositionEntity testbal, 2, 4, 2
ScaleEntity testbal, 0.5,0.5,0.5

; set scale of shadowtexture
tex_scale# = 2
ScaleTexture shadowtex, tex_scale,tex_scale

; set initial uv values of shadowtexture. put it underneath our entity.
; uv 0,0 is topleft of terrain, while worldspace 0,0 is bottomleft of terrain. so we need some offsets.
; note that x and z in these variables is really u and v. I just named them x and z so they match my entity
; position naming

tex_xpos# = (EntityX(testbal) - (tex_scale / 2)) / tex_scale
tex_zpos# = (TerrainSize(terrain) - EntityZ(testbal) - (tex_scale / 2)) / tex_scale
PositionTexture shadowtex, tex_xpos, tex_zpos

While Not KeyHit(1)

	If KeyDown (203)
		TranslateEntity testbal, -0.1, 0, 0

		; add xspeed divided by texscale divided by terrain x size
		tex_xpos = tex_xpos - (0.1 / tex_scale) / terrain_scale
		PositionTexture shadowtex, tex_xpos , tex_zpos
	EndIf
		
	If KeyDown (205)
		TranslateEntity testbal, 0.1, 0, 0

		; add xspeed divided by texscale divided by terrain z size
		tex_xpos = tex_xpos + (0.1 / tex_scale) / terrain_scale
		PositionTexture shadowtex, tex_xpos , tex_zpos
	EndIf

	UpdateWorld 
	RenderWorld
	Flip
Wend
End
