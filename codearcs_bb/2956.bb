; ID: 2956
; Author: Raul
; Date: 2012-07-01 04:42:16
; Title: 3D sprite 'pixel perfect'
; Description: 3D sprites with 64,128 or 256 pixels combinations without any blur

;---------------------------------------------------------------------------------------------------------------------------
;3D SPRITE PIXEL PERFECT example :::: work with 64 128 and 256 pixels combinations 64x64, 256x128, 64x128, ...
;---------------------------------------------------------------------------------------------------------------------------
Graphics3D 800,600,GraphicsDepth(),6 ;NOTE: Width an Height must be multiple of 2
SetBuffer BackBuffer()
;---------------------------------------------------------------------------------------------------------------------------
font=LoadFont("arial",18,1)
SetFont font
;---------------------------------------------------------------------------------------------------------------------------
;function function function function function function function function function function function function function functi
Function MAKEM(s,x1,x2,y1,y2)
							v0=AddVertex (s,x1,y1,0,0,1)
							v1=AddVertex (s,x2,y1,0,1,1)
							v2=AddVertex (s,x2,y2,0,1,0)
							v3=AddVertex (s,x1,y2,0,0,0)
							AddTriangle (s,v0,v2,v1)
							AddTriangle (s,v0,v3,v2)
							AddTriangle (s,v0,v1,v2)
							AddTriangle (s,v0,v2,v3)
End Function
;function function function function function function function function function function function function function functi
;---------------------------------------------------------------------------------------------------------------------------
;sprite 3D sprite 3D sprite 3D sprite 3D sprite 3D sprite 3D sprite 3D sprite 3D sprite 3D sprite 3D sprite 3D sprite 3D spr
							poly1_tex=CreateTexture(64,128)
							poly1=CreateMesh()
			EntityFX		poly1,5
							surf=CreateSurface(poly1)
							MAKEM(surf,-32,32,-64,64) ;<-- 'MidHandle'
			EntityTexture	poly1,poly1_tex
							;-----------------------------------------
							;you can load a 64x64 pixels image instead
							ClsColor 200,150,100
							SetBuffer TextureBuffer(poly1_tex)
							Cls
							Rect 0,0,64,128,0
							Text 32,4,"Sprite",1
							Text 32,24,"3D",1
							Text 32,104,"64x128",1
							SetBuffer BackBuffer()
							;-----------------------------------------
;sprite 3D sprite 3D sprite 3D sprite 3D sprite 3D sprite 3D sprite 3D sprite 3D sprite 3D sprite 3D sprite 3D sprite 3D spr
;---------------------------------------------------------------------------------------------------------------------------
;camera camera camera camera camera camera camera camera camera camera camera camera camera camera camera camera camera came
							cam=CreateCamera()
			CameraClsMode	cam,False,True
			PositionEntity	cam,Float#(GraphicsWidth()/2)+.5,-Float#(GraphicsHeight()/2)+.5,-(GraphicsWidth()/2)
;camera camera camera camera camera camera camera camera camera camera camera camera camera camera camera camera camera came
;---------------------------------------------------------------------------------------------------------------------------
ClsColor 100,150,200
;---------------------------------------------------------------------------------------------------------------------------
;MAIN MAIN MAIN MAIN MAIN MAIN MAIN MAIN MAIN MAIN MAIN MAIN MAIN MAIN MAIN MAIN MAIN MAIN MAIN MAIN MAIN MAIN MAIN MAIN MAI
While Not KeyDown(1)
Cls
Text 10,10,"Lmouse  to fade"
Text 10,50,"Rmouse  to turn"
Text 10,90,"[space] to flip"
PositionEntity poly1,MouseX(),-MouseY(),0
EntityAlpha    poly1,1-poly1_alpha#
RenderWorld
If MouseDown(1) Then poly1_alpha#=poly1_alpha#+.05 : If poly1_alpha#>1 Then poly1_alpha#=0
If MouseHit(2)  Then TurnEntity poly1,0,0,-90
If KeyHit(57)   Then TurnEntity poly1,0,180,0
Flip
Wend
;MAIN MAIN MAIN MAIN MAIN MAIN MAIN MAIN MAIN MAIN MAIN MAIN MAIN MAIN MAIN MAIN MAIN MAIN MAIN MAIN MAIN MAIN MAIN MAIN MAI
;---------------------------------------------------------------------------------------------------------------------------
End
