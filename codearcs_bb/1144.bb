; ID: 1144
; Author: Inner
; Date: 2004-08-23 05:33:14
; Title: 3D Line Mesh Fx
; Description: Sinus3D Line & Random3D Lines

;
; Adds a 3D line to the specified mesh.
; Note: 3D lines are only properly visible when rendered in wireframe mode!
; 
; Params:
; mesh     - Mesh to add 3D line to. If 0, a new mesh is created.
; x0,y0,z0 - Start point of line.
; x1,y2,z1 - End point of line.
; r,g,b    - Line colour.
;
; Returns:
; Handle of mesh the 3D line was added to.
;
Function create_3D_line(mesh,x0#,y0#,z0#,x1#,y1#,z1#,r%=255,g%=255,b%=255) 

	If mesh = 0 
		mesh = CreateMesh() 
		surf = CreateSurface(mesh) 
		EntityFX mesh,1+2+16
	Else 
		last_surf = CountSurfaces(mesh)
		surf = GetSurface(mesh,last_surf)
		If CountVertices(surf) > 30000 Then surf = CreateSurface(mesh)
	End If 

	v0 = AddVertex(surf,x0,y0,z0) 
	v1 = AddVertex(surf,x1,y1,z1)  
	v2 = AddVertex(surf,x0,y0,z0)  
	AddTriangle surf,v0,v1,v2
	
	VertexColor surf,v0,r,g,b
	VertexColor surf,v1,r,g,b
	VertexColor surf,v2,r,g,b

	Return mesh 

End Function
;
; CamCtrl
;
;
Function CameraControl(camera,camPiv)
	; --- camera controls
	scrollwheel = MouseZSpeed()
	If MouseDown(1) Then 
		TurnEntity(camPiv, MouseYSpeed(),-MouseXSpeed(),0)
	Else If scrollwheel <> 0 Then 
		MoveEntity(camera, 0,0,scrollwheel*3)
	Else
		dummy = MouseYSpeed():dummy = MouseXSpeed():dummy = MouseZSpeed() ; prevent mousespeed blips.
	End If
	MoveMouse GraphicsWidth()/2,GraphicsHeight()/2
End Function
; --- set graphics
Graphics3D 640,480,32,0
SetBuffer(BackBuffer())

AntiAlias False

; --- create scene setup
camPiv = CreatePivot()
camera = CreateCamera(camPiv)
CameraClsColor camera,128,128,128
PositionEntity(camera, 0,0,-10)

light=CreateLight(2) 
PositionEntity(light,4,10,0) 
LightRange(light,10)

Function Random_Lines()
	llen#=15.0
	For i=0 To 1000
		If i=0 Then 
			x1#=Rnd(-llen#,llen#)
			y1#=Rnd(-llen#,llen#)
			z1#=Rnd(-llen#,llen#)
		Else
			x1#=x2#
			y1#=y2#
			z1#=z2#
		EndIf
		x2#=Rnd(-llen#,llen#)
		y2#=Rnd(-llen#,llen#)
		z2#=Rnd(-llen#,llen#)
		create_3D_line(0,x1#,y1#,z1#,x2#,y2#,z2#)
	Next
End Function

Function Sinus3D_Lines()
	llen#=15.0
	rx#=Rnd(5.0)
	ry#=Rnd(5.0)
	rz#=Rnd(5.0)
	For i=0 To 550 
		If i=0 Then 		
			x1#=Sin(xs#)*llen# : xs#=xs#+Cos(lsx#)*llen# : lsx#=lsx#+rx#
			y1#=Sin(ys#)*llen# : ys#=ys#+Cos(lsy#)*llen# : lsy#=lsy#+ry#
			z1#=Cos(zs#)*llen# : zs#=zs#+Sin(lsz#)*llen# : lsz#=lsz#+rz#
		Else
			x1#=x2#
			y1#=y2#
			z1#=z2#
		EndIf
		x2#=Sin(xs#)*llen# : xs#=xs#+Cos(lsx#)*llen# : lsx#=lsx#+rx# 
		y2#=Sin(ys#)*llen# : ys#=ys#+Cos(lsy#)*llen# : lsy#=lsy#+ry#
		z2#=Cos(zs#)*llen# : zs#=zs#+Sin(lsz#)*llen# : lsz#=lsz#+rz#
		c=c+1
		If c=5
			rx#=Rnd(-5.0,5.0)
			ry#=Rnd(-5.0,5.0)
			rz#=Rnd(-5.0,5.0)
			c=0
		EndIf
		create_3D_line(0,x1#,y1#,z1#,x2#,y2#,z2#)
		cube=CreateSphere()
		ScaleEntity cube,.1,.1,.1
		PositionEntity cube,x1#,y1#,z1#
		EntityColor cube,255,0,0
	Next
End Function

;Random_Lines()
Sinus3D_Lines()

While Not KeyHit(1)

	CameraControl(camera,camPiv)
	; --- rendering
	CameraClsMode(camera, 1, 1)
	WireFrame(0)
	RenderWorld()
	
	CameraClsMode(camera, 0, 0)
	WireFrame(1)
	RenderWorld()

	Flip()
Wend
