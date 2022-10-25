; ID: 1232
; Author: Nikko
; Date: 2004-12-13 08:00:10
; Title: Morph animation : per vertex technic
; Description: I use a per vextex Morphing technic to Morph a face from Human to Orc character

;-------------------------------------------------------
; Morph Test : a simple morph sample, extracted from 
; the Morph city demo : www.nikko3d.com
; written by Nikko (nicolas choukroun)
; December 2004

Graphics3D 1024,768,32,2
SetBuffer BackBuffer()

Global nbrsurfaces=0 	; nombre total de surface dans le Mesh en cours de morph
Dim surfaces(100) 		; nombre max de mesh dans un b3d
Dim sx#(100000)			; mémorsation des vertices source
Dim sy#(100000)
Dim sz#(100000)

Dim sNx#(100000)			; mémorsation des vertices source
Dim sNy#(100000)
Dim sNz#(100000)

Dim mx#(100000)
Dim my#(100000)
Dim mz#(100000)

Dim mNx#(100000)
Dim mNy#(100000)
Dim mNz#(100000)


Dim dx#(100000)			; mémorisation des vertices destination
Dim dy#(100000)
Dim dz#(100000)

Dim dNx#(100000)			; mémorisation des vertices destination
Dim dNy#(100000)
Dim dNz#(100000)


Global Vertices=0
Global MaxVertices=0

; camera ---------------------
Global camera=CreateCamera()
PositionEntity camera,0,0,-100
CameraRange camera,0.01,100000
Global	dest_cam_pitch#
Global	dest_cam_yaw#
Global	cam_pitch#
Global	cam_yaw#
	
; --------------------- 
Global Pivot=CreatePivot()

; -------------------------------
AmbientLight 0,0,0

; ------------------
Global Light1=CreateLight(1)
LightColor light1,100,200,200
LightConeAngles light1,0,70
PositionEntity light1,150,0,-160
LightRange light1,120

Global Light2=CreateLight(1)
LightColor light2,200,200,100
LightConeAngles light2,0,70
PositionEntity light2,-150,-0,-160
LightRange light2,120
PositionEntity camera,-2,4,-5


MoveMouse GraphicsWidth()/2,GraphicsHeight()/2

Global theend=False
; load the Orc entities
m0=LoadAnimMesh( "orcx1.b3d",Pivot )
m1=LoadAnimMesh( "orcx2.b3d",Pivot )
m2=LoadAnimMesh( "orcx3.b3d",Pivot )
m3=LoadAnimMesh( "orcx4.b3d",Pivot )
m4=LoadAnimMesh( "orcx5.b3d",Pivot )
m5=LoadAnimMesh( "orcx6.b3d",Pivot )
m6=LoadAnimMesh( "orcx7.b3d",Pivot )

EntityShininess m0,0.4

HideEntity m4
HideEntity m0
HideEntity m1
HideEntity m2
HideEntity m3
HideEntity m4
HideEntity m5
HideEntity m6
Repeat
	ShowEntity m0
	; we can morph from any animation to any animation, no restrictions
	r=Rnd(1,6)
	Select r
		Case	1
		Morph(m0,m1,100,1)
		Case 2
		Morph(m0,m2,100,1)
		Case 3
		Morph(m0,m3,100,1)
		Case 4
		Morph(m0,m4,100,1)
		Case 5
		Morph(m0,m5,100,1)
		Case 6
		Morph(m0,m6,100,1)
	End Select
		
Until theend
FreeEntity m0
FreeEntity m1
FreeEntity m2
FreeEntity m3
FreeEntity m4
FreeEntity m5
FreeEntity m6
End

;-----------------------------------------------------------------------


Function CurveValue#(current#,destination#,curve)
	current#=current#+((destination#-current#)/curve)
	Return current#
End Function

;--------------------------------------------
; Store the source and dest mesh properties.
; optionnaly we can store the normals
Function StoreMesh(obj1,obj2,f#)
	nbrsurfaces=CountSurfaces(obj1)
	exvertices=0
	For surf=1 To nbrsurfaces
		Local s1=GetSurface(obj1,surf)
		Local vertices1=CountVertices(s1)
		Local s2=GetSurface(obj2,surf)
		Local vertices2=CountVertices(s2)
	
		If vertices1>vertices2 Then 
			verticesmax=vertices1
		Else
			verticesmax=vertices2
		EndIf		
	
		For i=exvertices To exvertices+vertices1-1
			sx#(i)=VertexX(s1,i-exvertices)
			sy#(i)=VertexY(s1,i-exvertices)
			sz#(i)=VertexZ(s1,i-exvertices)
		Next 	
		For i=exvertices To exvertices+vertices2-1
			dx#(i)=VertexX(s2,i-exvertices)
			dy#(i)=VertexY(s2,i-exvertices)
			dz#(i)=VertexZ(s2,i-exvertices)
		Next 	
		For i=exvertices To exvertices+vertices1-1
			sNx#(i)=VertexNX(s1,i-exvertices)
			sNy#(i)=VertexNY(s1,i-exvertices)
			sNz#(i)=VertexNZ(s1,i-exvertices)
		Next 	
		For i=exvertices To exvertices+vertices2-1
			dNx#(i)=VertexNX(s2,i-exvertices)
			dNy#(i)=VertexNY(s2,i-exvertices)
			dNz#(i)=VertexNZ(s2,i-exvertices)
		Next 	
		; not so useful
;		If vertices1<vertices2 Then 
;			For i=exvertices+vertices1 To exvertices+verticesmax
;				AddVertex s1,sx#(i Mod exvertices+vertices1),sy#(i Mod exvertices+vertices1),sz#(i Mod exvertices+vertices1) 
;			Next 	
;		Else
;			If exvertices+vertices1>exvertices+vertices2 Then 
;				For i=exvertices+vertices2 To exvertices+verticesmax
;					AddVertex s2,dx#(i Mod exvertices+vertices2),dy#(i Mod exvertices+vertices2),dz#(i Mod exvertices+vertices2) 
;				Next 	
;			EndIf
;		EndIf		
;	
		For i=exvertices To exvertices+verticesmax-1
			mx#(i)=(dx#(i)-sx#(i))/f#
			my#(i)=(dy#(i)-sy#(i))/f#
			mz#(i)=(dz#(i)-sz#(i))/f#
		Next 	
		For i=exvertices To exvertices+verticesmax-1
			mNx#(i)=(dNx#(i)-sNx#(i))/f#
			mNy#(i)=(dNy#(i)-sNy#(i))/f#
			mNz#(i)=(dNz#(i)-sNz#(i))/f#
		Next 	
		exvertices=exvertices+verticesmax
	Next	
	Return exvertices
	
End Function

;----------------------------------------------------------------------
; do the transformation
Function TransformMesh(mx1,v#)
	nbrsurfaces=CountSurfaces(mx1)
	exvertices=0
	For surf=1 To nbrsurfaces
		Local s1=GetSurface(mx1,surf)
		Local vertices1=CountVertices(s1)
		For i=exvertices To exvertices+vertices1-1
			VertexCoords s1,i-exvertices,sx#(i)+(mx#(i)*v#),sy#(i)+(my#(i)*v#),sz#(i)+(mz#(i)*v#)
			; morph the normals as well 
;			VertexNormal s1,i-exvertices,sNx#(i)+(mNx#(i)*v#),sNy#(i)+(mNy#(i)*v#),sNz#(i)+(mNz#(i)*v#)
		Next 	
		exvertices=exvertices+vertices1
	Next
End Function

;##############################################################
; Morph main loop
; objet0 = source
; objet1 = dest
; fr# = number of frames
; del# animation speed
;
Function Morph(Objet0,Objet1,fr#,del#)
	frames#=fr#
	MaxVertices=StoreMesh(Objet0,Objet1,frames#)
	
	HideEntity objet1
	
	; Rotation
	; --------
	speed#=0.2
	mxs=MouseXSpeed()/2
	mys=MouseYSpeed()/2
	
	v#=1.0	
	exitthis=False
	Repeat
		Cls
		If KeyDown(1) Then theend=True
		If MouseDown(1)
			MoveEntity camera,0,0,speed#
		Else If MouseDown(2)
			MoveEntity camera,0,0,-speed#
		EndIf
	
		If KeyDown(200) Then MoveEntity camera,0,+speed#,0

		If KeyDown(208) Then MoveEntity camera,0,-speed#,0

		If KeyDown(203) Then MoveEntity camera,-speed#,0,0

		If KeyDown(205) Then MoveEntity camera,+speed#,0,0

	
		TransformMesh(objet0,v#)	
		
		; Mouse look code
		; ---------------
		curve=3 ; change this to alter smoothness
	
		; Mouse x and y speed#
		; -------------------
		mxs=MouseXSpeed()/2
		mys=MouseYSpeed()/2
	
		; Destination camera pitch and yaw values
		; ---------------------------------------
		dest_cam_pitch#=dest_cam_pitch#+mys
		dest_cam_yaw#=dest_cam_yaw#-mxs
	
		; Current camera pitch and yaw values
		; -----------------------------------
		cam_pitch#=CurveValue(dest_cam_pitch#,cam_pitch#,curve)
		cam_yaw#=CurveValue(dest_cam_yaw#,cam_yaw#,curve)
	
		RotateEntity camera,cam_pitch#,cam_yaw#,0

		; Reset mouse position to centre of screen
		; ----------------------------------------

;		TurnEntity Pivot,0,0.05,0
		v#=v#+del#
		If v#>=frames# Then Exit
		UpdateWorld
		RenderWorld
		Text 0,20,"Camera Movement"
		Text 0,30,"Vertices:"+vertices
		Text 0,40,"Frames#:"+v#
		Text 0,50,"Maxvertices:"+MaxVertices
		Text 0,60,"x:"+EntityX(camera)+ "   y:"+EntityY(camera)+" z:"+EntityZ(camera)
		Text 0,70,"Mem Used: "+((TotalVidMem()/1024)-(AvailVidMem ())/1024)
		Text 0,80,"Mem FREE: "+(AvailVidMem ()/1024)
		Text 0,90,"Mem Total: "+(TotalVidMem()/1024)
		Flip
	Until theend 
End Function
