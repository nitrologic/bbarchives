; ID: 232
; Author: Giano
; Date: 2002-02-11 11:40:54
; Title: Cylinders &amp; Cones to make Trails
; Description: Cylinders & Cones to make trails or useful for 3dLines

;**********************************************************************************************
;*** Test of rays 
;**********************************************************************************************
;*** By Gianluca SCLANO (sclano@hotmail.com)
;*** Aka BitmaniaK --- 
;**********************************************************************************************

Dim rag.ray(0)

;***REM: Uncomment next line if you have a gradiend texture ***
;Global gGradientRayBrush 

;**********************************************************************************************
;*** The ray type, with coordinates and entity
;**********************************************************************************************
Type ray
	Field  x0#,y0#,z0#
	Field  x1#,y1#,z1#
	Field  entity, radius#
End Type 

;**********************************************************************************************
;*** Create A Ray from x0,y0,z0 to x1,y1,z1 of radius and parent 
;*** (coneFlag = 1 For a cone, =0 for a cylinder)
;**********************************************************************************************
Function createRay.ray(x0#,y0#,z0#, x1#,y1#,z1#, radius#, parent=0, coneFlag=1)
		
	mesh = createCylinderRay%(x0#,y0#,z0#, x1#,y1#,z1#, radius, coneFlag)
	If parent Then	EntityParent mesh,parent
	
;***REM: Uncomment next line if you have a gradiend texture ***	
	;PaintMesh mesh,gGradientRayBrush
	EntityAlpha mesh, 0.8
	r.ray=New ray
	r\x0=x0
	r\y0=y0
	r\z0=z0
	r\x1=x1
	r\y1=y1
	r\z1=z1
	r\entity = mesh
	r\radius = radius
	Return r
End Function

;**********************************************************************************************
;*** Create the ray mesh & brush, used by createRay
;*** Change this if you have other mesh type
;**********************************************************************************************
Function createCylinderRay%(x0#,y0#,z0#, x1#,y1#,z1#, radius#, coneFlag=1)
	piv0=CreatePivot()
	PositionEntity piv0,x0#,y0#,z0#
	
	piv1=CreatePivot()
	PositionEntity piv1,x1#,y1#,z1#
	
	If coneFlag 
		mesh=CreateCone(8)
	Else
		mesh=CreateCylinder(8)
	End If
	
	FitMesh mesh,-radius,0, -radius,radius*2,EntityDistance(piv1,piv0),radius*2
	RotateMesh mesh ,90,0,0
	PositionEntity mesh,x0,y0,z0
	EntityAlpha mesh, 0.5 ;*** Transparece
	EntityFX mesh,16
	ray=CopyEntity(mesh)

	PointEntity ray,piv1
	
	FreeEntity mesh
	FreeEntity piv0
	FreeEntity piv1
	Return ray
End Function

;**********************************************************************************************
;*** Now move/point the ray to an entity or to a point
;**********************************************************************************************
Function pointPositionRay(r.ray , x1#,y1#,z1#)
	piv1=CreatePivot()
	PositionEntity piv1,x1#,y1#,z1#
	PointEntity r\entity,piv1
	FreeEntity piv1
End Function 
;**********************************************************************************************
Function pointEntityRay(r.ray , e)
	PointEntity r\entity,e	
End Function 

;**********************************************************************************************
;*** The destructor
;**********************************************************************************************
Function deleteRays()
	For r.ray = Each ray
		If r\ entity Then	FreeEntity r\entity
		Delete r
	Next
End Function

;**********************************************************************************************
;*** Simple Test!!!!
;**********************************************************************************************
Function testRayManager()

	Graphics3D 800,600,32,0
	SetBuffer BackBuffer()	
	camera=CreateCamera()	
	light=CreateLight()
	RotateEntity light,90,0,0	
;***REM: Uncomment next line if you have a gradiend texture ***
	;gGradientRayBrush = 	LoadBrush("raygradientUpDown.bmp",1+2)

	x0# =0 : y0# =0 : z0# = 0
	x1# =4 : y1# =5 : z1# = 6
	s0 = CreateSphere()
	PositionEntity s0,x0,y0,z0
	EntityColor s0,255,0,255
	ScaleEntity s0,.5,.5,.5
	s1 = CreateSphere()
	PositionEntity s1,x1,y1,z1
	ScaleEntity s1,.5,.5,.5
	PositionEntity camera, 0,0,-10
	
;*** Create Rays	
	Dim rag.ray(10)
	raggio.ray = createRay(x0#,y0#,z0#, x1#,y1#,z1#,.25)
	
	For t=1 To 10
		x1# =Rnd(-4,4) : y1# =Rnd(-5,5) : z1# = Rnd(-6,6)
		rag(t) = createRay(x0#,y0#,z0#, x1#,y1#,z1#,.1,s0,0)
		EntityColor rag(t)\entity, Rand(128,255),Rand(128,255),Rand(128,255)
	Next
	
;*** Main Loop	**********************************************
	While Not KeyDown( 1 )
	
		If KeyHit(30) Then distance = distance + 1 ;*** Key A
		If KeyHit(44) Then distance = distance - 1 ;*** Key Z
	
		PositionEntity s1,(MouseX()- GraphicsWidth()/2 )/50.0  , (( GraphicsHeight()/2 ) - MouseY())  /50.0,Distance
	;*** Update Rays
		pointEntityRay(raggio,s1)
		TurnEntity s0,0,-1,1
	
		If KeyDown(20) ;*** press T for TILT!!!
			PositionEntity camera, Sin(-MilliSecs())/2,Cos(MilliSecs())/2,-8 - Sin(MilliSecs())
		End If
		
		RenderWorld
		Text 0,0,"Ray Test by Gianluca SCLANO (BitmaniaK)"
		Text 0,15,"Move your mouse , press A/Z or T"
		Flip
	Wend
;*** End *****************************************************
	deleteRays()
End Function

;**********************************************************************************************

testRayManager()

;**********************************************************************************************
;*** Bye Bye 
;**********************************************************************************************
