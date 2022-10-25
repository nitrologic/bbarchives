; ID: 1928
; Author: fredborg
; Date: 2007-02-17 21:33:19
; Title: Specular reflections
; Description: Make good looking roads and stuff easily

;
; How to make nice specular reflections with DirectX7 tech...
; Created by Mikkel Fredborg
; Use as you please!
;

Graphics3D 640,480,0,2
SetBuffer BackBuffer()

;
; Textures
ClearTextureFilters
Local tex_dif	= LoadTexture("Road_Diffuse.png",1+8)
Local tex_spc	= LoadTexture("Road_Specular.png",1+2+8)

;
; Diffuse material
Local brush_dif = CreateBrush()
BrushTexture(brush_dif,tex_dif)
BrushShininess(brush_dif,0.02)

;
; Specular material
Local brush_spc = CreateBrush()
BrushTexture(brush_spc,tex_spc)
BrushBlend(brush_spc,3)
BrushColor(brush_spc,0,0,0)
BrushShininess(brush_spc,0.5)

;
; Roads
Local road_dif = CreateRoad(5,20,8,32)
PaintEntity road_dif,brush_dif
Local road_spc = CreateRoad(5,20,8,32)
PaintEntity road_spc,brush_spc
PositionEntity road_spc,0,0.01,0

;
; Light
Local light = CreateLight(2)
PositionEntity light,1,40,200
LightColor light,200,200,160

AmbientLight 60,70,90

;
; Camera
Local cam = CreateCamera()
PositionEntity cam,0,2,0
RotateEntity cam,20,0,0

;
; Action!
While Not KeyHit(1)

	TranslateEntity cam,(KeyDown(205)-KeyDown(203))*0.1,0.0,(KeyDown(200)-KeyDown(208))*0.1

	RenderWorld
	Flip

Wend

End

Function CreateRoad(w#,d#,xseg,zseg)

	Local mesh = CreateMesh()
	Local surf = CreateSurface(mesh)
	
	Local hw# = w/2.0
	Local hd# = d/2.0
	
	Local dx# = 0.0
	Local dz# = 0.0
	
	Local vscale# = zseg / Float(xseg)
	
	For x = 0 To xseg
		dx = x / Float(xseg)
		For z = 0 To zseg
			dz = z / Float(zseg)
			v = AddVertex(surf,dx*w - hw, 0.0, dz*d - hd, dx, dz*vscale)
			VertexNormal(surf,v,0,1,0)
		Next
	Next

	For x = 0 To xseg-1
		For z = 0 To zseg-1
			v0 = x*(zseg+1) + z			
			v1 = x*(zseg+1) + z + 1		
			v2 = (x+1)*(zseg+1) + z + 1	
			v3 = (x+1)*(zseg+1) + z		 
			AddTriangle(surf,v0,v1,v2)
			AddTriangle(surf,v0,v2,v3)
		Next
	Next	

	Return mesh

End Function
