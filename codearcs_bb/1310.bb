; ID: 1310
; Author: Jeppe Nielsen
; Date: 2005-03-06 21:10:27
; Title: Bezier Patches - Editable 3D surfaces.
; Description: Shows how bezier patches can be created, example demonstrates an interactive 3d surface.

;*****************************************************
;*               Bezier Patch example                *
;*****************************************************
;*              By Jeppe Nielsen 2005                *
;*****************************************************
;*                jeppe@gameart.dk                   *
;*****************************************************

Graphics3D 800,600

segments=10

b.bezierpatch = BezierPatchNew()

patch=BezierPatchMesh(b,segments,segments)

patchtexture=CreateTexture(32,32)
SetBuffer TextureBuffer(patchtexture)
	ClsColor 255,0,0
	Cls
	Color 255,255,255
	Rect 0,0,16,16,True
	Rect 16,16,16,16,True
	
SetBuffer BackBuffer()

ScaleTexture patchtexture,0.25,0.25
EntityTexture patch,patchtexture

Global camera,camerarx#,camerary#

CameraInit

light=CreateLight(2)
PositionEntity light,0,10,0
LightRange light,5



plane=CreatePlane()
EntityPickMode plane,2
EntityAlpha plane,0

selectedpoint=-1

Repeat

cameraupdate 0.1

Select state

	Case 0
	
		If MouseDown(1)
			selectedpoint=BezierPatchGetPoint(b,camera,MouseX(),MouseY())
			If selectedpoint<>-1
				state=1
				PositionEntity plane,b\px[selectedpoint],b\py[selectedpoint],b\pz[selectedpoint]
			EndIf
		EndIf
		
	Case 1
	
		If MouseDown(1)
			
			CameraPick(camera,MouseX(),MouseY())
		
			BezierPatchSetPoint(b,selectedpoint,PickedX#(),PickedY#(),PickedZ#())
			If patch<>0
				FreeEntity patch
				patch=0
			EndIf
			
			patch=BezierPatchMesh(b,segments,segments)
			EntityTexture patch,patchtexture
				
		Else
		
			state=0
			
		EndIf
		
End Select

If selectedpoint<>-1
	
	If KeyDown(201)
	
		b\py[selectedpoint]=b\py[selectedpoint]+0.5
		If patch<>0
			FreeEntity patch
			patch=0
		EndIf
		
		patch=BezierPatchMesh(b,segments,segments)
		EntityTexture patch,patchtexture

	
	ElseIf KeyDown(209)	
	
		b\py[selectedpoint]=b\py[selectedpoint]-0.5
		If patch<>0
			FreeEntity patch
			patch=0
		EndIf
		
		patch=BezierPatchMesh(b,segments,segments)
		EntityTexture patch,patchtexture


	EndIf
	

EndIf

If KeyHit(78)

	segments=segments+1
	If patch<>0
		FreeEntity patch
		patch=0
	EndIf
	
	patch=BezierPatchMesh(b,segments,segments)
	EntityTexture patch,patchtexture


ElseIf KeyHit(74)	

	segments=segments-1
	If segments<1
		segments=1
	EndIf
	If patch<>0
		FreeEntity patch
		patch=0
	EndIf
	
	patch=BezierPatchMesh(b,segments,segments)
	EntityTexture patch,patchtexture

EndIf

If KeyHit(57)

	wire=1-wire
	WireFrame wire

EndIf


RenderWorld

Color 0,0,255
BezierPatchDraw(b,camera,selectedpoint)

Color 0,0,0
Text 401,11,"Bezier Patch example by Jeppe Nielsen, 2005",True
Text 401,31,"Left mouse button to move control points",True
Text 401,51,"Right mouse button & mouse to rotate camera, cursor keys to move camera",True
Text 401,71,"Page Up/down to move selected control point up/down",True
Text 401,91,"+ / - to adjust polygon segments : "+segments,True
Text 401,111,"Space to toggle wireframe",True

Color 0,128,255
Text 400,10,"Bezier Patch example by Jeppe Nielsen, 2005",True
Text 400,30,"Left mouse button to move control points",True
Text 401,51,"Right mouse button & mouse to rotate camera, cursor keys to move camera",True
Text 400,70,"Page Up/down to move selected control point up/down",True
Text 400,90,"+ / - to adjust polygon segments : "+segments,True
Text 400,110,"Space to toggle wireframe",True

x=MouseX()
y=MouseY()

Color 255,255,255

Rect x-6,y,13,1
Rect x,y-6,1,13


Flip

Until KeyDown(1)
End


Type bezierpatch

Field px#[15]
Field py#[15]
Field pz#[15]

End Type

Function BezierPatchNew.bezierpatch()

b.bezierpatch=New bezierpatch

For u=0 To 3
	
	For v=0 To 3
	
		b\px[u+v*4]=u*3
		b\py[u+v*4]=0
		b\pz[u+v*4]=v*3
		
	Next
	
Next

Return b
End Function

Function BezierPatchSetPoint(b.bezierpatch,point,x#,y#,z#)

	b\px[point]=x
	b\py[point]=y
	b\pz[point]=z

End Function

Function BezierPatchGetPoint(b.bezierpatch,cam,x,y,size=15)

sizesq=size*size

For n=0 To 15
	
	CameraProject cam,b\px[n],b\py[n],b\pz[n]
	
	dx=x-ProjectedX()
	dy=y-ProjectedY()

	dist=dx*dx+dy*dy

	If dist<=sizesq
		Return n
	EndIf
	
Next

Return -1
End Function

Function BezierPatchMesh(b.bezierpatch,useg=10,vseg=10)

mesh=CreateMesh()

surf=CreateSurface(mesh)

usegstep#=1/Float(useg)
vsegstep#=1/Float(vseg)

v#=0

While v<0.99

	u#=0

	While u<0.99
	
		uu#=u
		
		px1#=b\px[0]*(1-uu)^3 + b\px[1]*3*(1-uu)^2*uu + b\px[2]*3*(1-uu)*uu*uu + b\px[3]*uu^3
		py1#=b\py[0]*(1-uu)^3 + b\py[1]*3*(1-uu)^2*uu + b\py[2]*3*(1-uu)*uu*uu + b\py[3]*uu^3
		pz1#=b\pz[0]*(1-uu)^3 + b\pz[1]*3*(1-uu)^2*uu + b\pz[2]*3*(1-uu)*uu*uu + b\pz[3]*uu^3
		
		px2#=b\px[4]*(1-uu)^3 + b\px[5]*3*(1-uu)^2*uu + b\px[6]*3*(1-uu)*uu*uu + b\px[7]*uu^3
		py2#=b\py[4]*(1-uu)^3 + b\py[5]*3*(1-uu)^2*uu + b\py[6]*3*(1-uu)*uu*uu + b\py[7]*uu^3
		pz2#=b\pz[4]*(1-uu)^3 + b\pz[5]*3*(1-uu)^2*uu + b\pz[6]*3*(1-uu)*uu*uu + b\pz[7]*uu^3
		
		px3#=b\px[8]*(1-uu)^3 + b\px[9]*3*(1-uu)^2*uu + b\px[10]*3*(1-uu)*uu*uu + b\px[11]*uu^3
		py3#=b\py[8]*(1-uu)^3 + b\py[9]*3*(1-uu)^2*uu + b\py[10]*3*(1-uu)*uu*uu + b\py[11]*uu^3
		pz3#=b\pz[8]*(1-uu)^3 + b\pz[9]*3*(1-uu)^2*uu + b\pz[10]*3*(1-uu)*uu*uu + b\pz[11]*uu^3
		
		px4#=b\px[12]*(1-uu)^3 + b\px[13]*3*(1-uu)^2*uu + b\px[14]*3*(1-uu)*uu*uu + b\px[15]*uu^3
		py4#=b\py[12]*(1-uu)^3 + b\py[13]*3*(1-uu)^2*uu + b\py[14]*3*(1-uu)*uu*uu + b\py[15]*uu^3
		pz4#=b\pz[12]*(1-uu)^3 + b\pz[13]*3*(1-uu)^2*uu + b\pz[14]*3*(1-uu)*uu*uu + b\pz[15]*uu^3
		
		vv#=v
		
		x1# = px1*(1-vv)^3 + 3*px2*(1-vv)^2*vv + 3*px3*(1-vv)*vv*vv + px4*vv^3
		y1# = py1*(1-vv)^3 + 3*py2*(1-vv)^2*vv + 3*py3*(1-vv)*vv*vv + py4*vv^3
		z1# = pz1*(1-vv)^3 + 3*pz2*(1-vv)^2*vv + 3*pz3*(1-vv)*vv*vv + pz4*vv^3
		
		uu#=u+usegstep
		
		px1#=b\px[0]*(1-uu)^3 + b\px[1]*3*(1-uu)^2*uu + b\px[2]*3*(1-uu)*uu*uu + b\px[3]*uu^3
		py1#=b\py[0]*(1-uu)^3 + b\py[1]*3*(1-uu)^2*uu + b\py[2]*3*(1-uu)*uu*uu + b\py[3]*uu^3
		pz1#=b\pz[0]*(1-uu)^3 + b\pz[1]*3*(1-uu)^2*uu + b\pz[2]*3*(1-uu)*uu*uu + b\pz[3]*uu^3
		
		px2#=b\px[4]*(1-uu)^3 + b\px[5]*3*(1-uu)^2*uu + b\px[6]*3*(1-uu)*uu*uu + b\px[7]*uu^3
		py2#=b\py[4]*(1-uu)^3 + b\py[5]*3*(1-uu)^2*uu + b\py[6]*3*(1-uu)*uu*uu + b\py[7]*uu^3
		pz2#=b\pz[4]*(1-uu)^3 + b\pz[5]*3*(1-uu)^2*uu + b\pz[6]*3*(1-uu)*uu*uu + b\pz[7]*uu^3
		
		px3#=b\px[8]*(1-uu)^3 + b\px[9]*3*(1-uu)^2*uu + b\px[10]*3*(1-uu)*uu*uu + b\px[11]*uu^3
		py3#=b\py[8]*(1-uu)^3 + b\py[9]*3*(1-uu)^2*uu + b\py[10]*3*(1-uu)*uu*uu + b\py[11]*uu^3
		pz3#=b\pz[8]*(1-uu)^3 + b\pz[9]*3*(1-uu)^2*uu + b\pz[10]*3*(1-uu)*uu*uu + b\pz[11]*uu^3
		
		px4#=b\px[12]*(1-uu)^3 + b\px[13]*3*(1-uu)^2*uu + b\px[14]*3*(1-uu)*uu*uu + b\px[15]*uu^3
		py4#=b\py[12]*(1-uu)^3 + b\py[13]*3*(1-uu)^2*uu + b\py[14]*3*(1-uu)*uu*uu + b\py[15]*uu^3
		pz4#=b\pz[12]*(1-uu)^3 + b\pz[13]*3*(1-uu)^2*uu + b\pz[14]*3*(1-uu)*uu*uu + b\pz[15]*uu^3
		
		vv#=v
		
		x2# = px1*(1-vv)^3 + 3*px2*(1-vv)^2*vv + 3*px3*(1-vv)*vv*vv + px4*vv^3
		y2# = py1*(1-vv)^3 + 3*py2*(1-vv)^2*vv + 3*py3*(1-vv)*vv*vv + py4*vv^3
		z2# = pz1*(1-vv)^3 + 3*pz2*(1-vv)^2*vv + 3*pz3*(1-vv)*vv*vv + pz4*vv^3
		
		uu#=u
		
		px1#=b\px[0]*(1-uu)^3 + b\px[1]*3*(1-uu)^2*uu + b\px[2]*3*(1-uu)*uu*uu + b\px[3]*uu^3
		py1#=b\py[0]*(1-uu)^3 + b\py[1]*3*(1-uu)^2*uu + b\py[2]*3*(1-uu)*uu*uu + b\py[3]*uu^3
		pz1#=b\pz[0]*(1-uu)^3 + b\pz[1]*3*(1-uu)^2*uu + b\pz[2]*3*(1-uu)*uu*uu + b\pz[3]*uu^3
		
		px2#=b\px[4]*(1-uu)^3 + b\px[5]*3*(1-uu)^2*uu + b\px[6]*3*(1-uu)*uu*uu + b\px[7]*uu^3
		py2#=b\py[4]*(1-uu)^3 + b\py[5]*3*(1-uu)^2*uu + b\py[6]*3*(1-uu)*uu*uu + b\py[7]*uu^3
		pz2#=b\pz[4]*(1-uu)^3 + b\pz[5]*3*(1-uu)^2*uu + b\pz[6]*3*(1-uu)*uu*uu + b\pz[7]*uu^3
		
		px3#=b\px[8]*(1-uu)^3 + b\px[9]*3*(1-uu)^2*uu + b\px[10]*3*(1-uu)*uu*uu + b\px[11]*uu^3
		py3#=b\py[8]*(1-uu)^3 + b\py[9]*3*(1-uu)^2*uu + b\py[10]*3*(1-uu)*uu*uu + b\py[11]*uu^3
		pz3#=b\pz[8]*(1-uu)^3 + b\pz[9]*3*(1-uu)^2*uu + b\pz[10]*3*(1-uu)*uu*uu + b\pz[11]*uu^3
		
		px4#=b\px[12]*(1-uu)^3 + b\px[13]*3*(1-uu)^2*uu + b\px[14]*3*(1-uu)*uu*uu + b\px[15]*uu^3
		py4#=b\py[12]*(1-uu)^3 + b\py[13]*3*(1-uu)^2*uu + b\py[14]*3*(1-uu)*uu*uu + b\py[15]*uu^3
		pz4#=b\pz[12]*(1-uu)^3 + b\pz[13]*3*(1-uu)^2*uu + b\pz[14]*3*(1-uu)*uu*uu + b\pz[15]*uu^3
		
		vv#=v+vsegstep
		
		x3# = px1*(1-vv)^3 + 3*px2*(1-vv)^2*vv + 3*px3*(1-vv)*vv*vv + px4*vv^3
		y3# = py1*(1-vv)^3 + 3*py2*(1-vv)^2*vv + 3*py3*(1-vv)*vv*vv + py4*vv^3
		z3# = pz1*(1-vv)^3 + 3*pz2*(1-vv)^2*vv + 3*pz3*(1-vv)*vv*vv + pz4*vv^3
		
		uu#=u+usegstep
		
		px1#=b\px[0]*(1-uu)^3 + b\px[1]*3*(1-uu)^2*uu + b\px[2]*3*(1-uu)*uu*uu + b\px[3]*uu^3
		py1#=b\py[0]*(1-uu)^3 + b\py[1]*3*(1-uu)^2*uu + b\py[2]*3*(1-uu)*uu*uu + b\py[3]*uu^3
		pz1#=b\pz[0]*(1-uu)^3 + b\pz[1]*3*(1-uu)^2*uu + b\pz[2]*3*(1-uu)*uu*uu + b\pz[3]*uu^3
		
		px2#=b\px[4]*(1-uu)^3 + b\px[5]*3*(1-uu)^2*uu + b\px[6]*3*(1-uu)*uu*uu + b\px[7]*uu^3
		py2#=b\py[4]*(1-uu)^3 + b\py[5]*3*(1-uu)^2*uu + b\py[6]*3*(1-uu)*uu*uu + b\py[7]*uu^3
		pz2#=b\pz[4]*(1-uu)^3 + b\pz[5]*3*(1-uu)^2*uu + b\pz[6]*3*(1-uu)*uu*uu + b\pz[7]*uu^3
		
		px3#=b\px[8]*(1-uu)^3 + b\px[9]*3*(1-uu)^2*uu + b\px[10]*3*(1-uu)*uu*uu + b\px[11]*uu^3
		py3#=b\py[8]*(1-uu)^3 + b\py[9]*3*(1-uu)^2*uu + b\py[10]*3*(1-uu)*uu*uu + b\py[11]*uu^3
		pz3#=b\pz[8]*(1-uu)^3 + b\pz[9]*3*(1-uu)^2*uu + b\pz[10]*3*(1-uu)*uu*uu + b\pz[11]*uu^3
		
		px4#=b\px[12]*(1-uu)^3 + b\px[13]*3*(1-uu)^2*uu + b\px[14]*3*(1-uu)*uu*uu + b\px[15]*uu^3
		py4#=b\py[12]*(1-uu)^3 + b\py[13]*3*(1-uu)^2*uu + b\py[14]*3*(1-uu)*uu*uu + b\py[15]*uu^3
		pz4#=b\pz[12]*(1-uu)^3 + b\pz[13]*3*(1-uu)^2*uu + b\pz[14]*3*(1-uu)*uu*uu + b\pz[15]*uu^3
		
		vv#=v+vsegstep
		
		x4# = px1*(1-vv)^3 + 3*px2*(1-vv)^2*vv + 3*px3*(1-vv)*vv*vv + px4*vv^3
		y4# = py1*(1-vv)^3 + 3*py2*(1-vv)^2*vv + 3*py3*(1-vv)*vv*vv + py4*vv^3
		z4# = pz1*(1-vv)^3 + 3*pz2*(1-vv)^2*vv + 3*pz3*(1-vv)*vv*vv + pz4*vv^3
		
		
		vert=AddVertex(surf,x1,y1,z1,u,v)
		     AddVertex(surf,x2,y2,z2,u+usegstep,v)
		     AddVertex(surf,x3,y3,z3,u,v+vsegstep)
		     AddVertex(surf,x4,y4,z4,u+usegstep,v+vsegstep)
		
			AddTriangle surf,vert+0,vert+3,vert+1
			AddTriangle surf,vert+0,vert+2,vert+3
		
		u=u+usegstep

	Wend

	v=v+vsegstep

Wend

UpdateNormals mesh

Return mesh
End Function

Function BezierPatchDraw(b.bezierpatch,cam,selectedpoint)

Local projx[15],projy[15]

For n=0 To 15
	
	CameraProject cam,b\px[n],b\py[n],b\pz[n]
	
	projx[n]=ProjectedX()
	projy[n]=ProjectedY()
	
	If selectedpoint=n
	
		Color 255,255,0
	
	Else
	
		Color 0,0,255
	
	EndIf
	
	Oval projx[n]-5,projy[n]-5,10,10,True
	
	
Next

Color 0,255,0

For u=0 To 3
	
	For v=0 To 3
		If u<3
		Line projx[u+v*4],projy[u+v*4],projx[u+v*4+1],projy[u+v*4+1]
		EndIf
		If v<3
		Line projx[u+v*4],projy[u+v*4],projx[u+v*4+4],projy[u+v*4+4]
		EndIf
		
	Next
	
Next

End Function

Function CameraInit(r=0,g=0,b=0,x#=12,y#=10,z#=12,px#=0,py#=0,pz#=0)

	camera=CreateCamera()
	CameraClsColor camera,r,g,b
	PositionEntity camera,x,y,z
	p=CreatePivot()
	PositionEntity p,px,py,pz
	PointEntity camera,p
	FreeEntity p
	MoveMouse GraphicsWidth()/2,GraphicsHeight()/2
	camerarx=EntityPitch(camera)
	camerary=EntityYaw(camera)
		
End Function

Function CameraClear()

	If camera<>0 Then FreeEntity camera	: camera=0
	
End Function

Function CameraUpdate(speed#=1)

		
	If MouseHit(2)
	
	MoveMouse GraphicsWidth()/2,GraphicsHeight()/2
		
	EndIf
	
	If MouseDown(2)

		camerarx#=camerarx#+MouseYSpeed()*0.5
		camerary#=camerary#-MouseXSpeed()*0.5
		If camerarx#>89
			camerarx#=89
		ElseIf camerarx#<-89
			camerarx#=-89
		EndIf
		RotateEntity camera,camerarx,camerary,0
		MoveMouse GraphicsWidth()/2,GraphicsHeight()/2
		
	EndIf
	
	If KeyDown(200) Or KeyDown(17) Then MoveEntity camera,0,0,speed#
	If KeyDown(208) Or KeyDown(31) Then MoveEntity camera,0,0,-speed#
	If KeyDown(203) Or KeyDown(30) Then MoveEntity camera,-speed#,0,0
	If KeyDown(205) Or KeyDown(32) Then MoveEntity camera,speed#,0,0
	
End Function
