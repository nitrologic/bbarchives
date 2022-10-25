; ID: 355
; Author: starfox
; Date: 2002-06-26 07:30:42
; Title: Starfox's LOD Mesh System
; Description: Manages LOD Meshes for Blitz

;Lod(Level Of Detail) Mesh System By Starfox (aka David Dawkins)
;What is an Lod mesh?
;A mesh that changes when you get closer or farther to the mesh, it is
;sufficent when you have lots of this meshes displayed.
;This system support unlimited levels of lod
;and unlimited number of cameras
;Put the meshes in order from most detail to least detail
;Only one thing is required if you use this system: credit
;Have fun

;The Types
Type lodmesh
Field piv,tparts,cloc
Field curpart,active
End Type

Type lodpart
Field mesh,nrange,par
Field frange,index
End Type

Type lodcam
Field cam
End Type

;Functions For Camera's
Function ActivateLodCamera(cam)
l.lodcam = New lodcam
l\cam = cam
End Function

Function DeactivateLodCamera(cam)
For l.lodcam = Each lodcam
If l\cam = cam Then Delete l : Exit
Next
End Function

;Lod Mesh Functions
Function CreateLodMesh()
l.lodmesh = New lodmesh
l\piv = CreatePivot()
l\active = 1
Return l\piv
End Function

Function AddToLodMesh.lodpart(entity,lodpiv,nrang,frang,index)
For l.lodmesh = Each lodmesh
	If l\piv = lodpiv
	lo.lodpart = New lodpart
	lo\par = l\piv
	lo\mesh = entity
	lo\nrange = nrang : lo\frange = frang
	lo\index = index
	EntityParent lo\mesh,lo\par,0
	l\tparts = l\tparts + 1
	If l\curpart > 0
	HideEntity l\curpart
	EndIf
	ShowEntity lo\mesh
	l\curpart = lo\mesh
	Return lo
	EndIf
Next
End Function

Function ToggleLodActivation(lodpiv)
For l.lodmesh = Each lodmesh
If l\piv = lodpiv
	l\active = 1 - l\active
	Exit
EndIf
Next
End Function

Function UpdateLod()
For l.lodcam = Each lodcam
	For lv.lodmesh = Each lodmesh
	If lv\active = 1
	If lv\cloc = 0
	lv\cloc = l\cam
	EndIf
	If EntityDistance(l\cam,lv\piv) < EntityDistance(lv\piv,lv\cloc)
	lv\cloc = l\cam
	EndIf
	EndIf
	Next
Next

For l.lodcam = Each lodcam
	For lv.lodmesh = Each lodmesh
	If lv\active = 1
	If lv\cloc = l\cam
	dist = EntityDistance(lv\cloc,lv\piv)
		For lm.lodpart = Each lodpart
		If lm\par = lv\piv
		
		If dist => lm\nrange And dist <= lm\frange
		If lv\curpart <> lm\mesh
		HideEntity lv\curpart
		ShowEntity lm\mesh
		lv\curpart = lm\mesh
		EndIf
		EndIf
		
		If dist >= lm\nrange
			If lm\index = lv\tparts
				If lv\curpart <> lm\mesh
				HideEntity lv\curpart
				ShowEntity lm\mesh
				lv\curpart = lm\mesh
				EndIf
			EndIf
		EndIf
		
		EndIf
		Next
		lv\cloc = 0
	EndIf
	ElseIf lv\active = 0
	For lm.lodpart = Each lodpart
	If lm\par = lv\piv
		If lm\index = 1
		If lv\curpart <> lm\mesh
		HideEntity lv\curpart
		ShowEntity lm\mesh
		lv\curpart = lm\mesh
		EndIf
		EndIf
		lv\cloc = 0
	EndIf
	Next
	EndIf
	Next
Next
End Function
			
	
;Example
Graphics3D 640,480
light = CreateLight() : TurnEntity light,45,45,0
SetBuffer FrontBuffer()
Print "Loading"
SetBuffer BackBuffer()
cam = CreateCamera()
CameraViewport cam,0,0,640,480/2-1
cam2 = CreateCamera()
CameraViewport cam2,0,480/2+1,640,480/2-1
MoveEntity cam,0,0,-100
MoveEntity cam2,-2000,0,100
CameraRange cam,1,5000
CameraRange cam2,1,5000
Local lodon = 1
Local ship[8]
For e = 1 To 8
ship[e] = LoadMesh("tran0"+e+".x")
RotateMesh ship[e],0,180,0
FitMesh ship[e],-1.5,-1,-3,3,2,6
ScaleEntity ship[e],20,20,20
Next
;Activate Both Cameras
activatelodcamera(cam)
activatelodcamera(cam2)
;Create the actuall lodmesh
piv = createlodmesh()
;Add all models to lodmesh and set boundries
addtolodmesh(ship[1],piv,0,200,1)
addtolodmesh(ship[2],piv,201,400,2)
addtolodmesh(ship[3],piv,401,600,3)
addtolodmesh(ship[4],piv,601,800,4)
addtolodmesh(ship[5],piv,801,1000,5)
addtolodmesh(ship[6],piv,1001,1200,6)
addtolodmesh(ship[7],piv,1201,1400,7)
addtolodmesh(ship[8],piv,1401,1600,8)
;Make sure none of them overlap
PositionEntity piv,3000,0,0 : TurnEntity piv,0,90,0

While Not KeyHit(1)
PointEntity cam,piv
PointEntity cam2,piv
MoveEntity piv,0,0,5
;Update LOD
updatelod()
UpdateWorld
RenderWorld
Text 0,0,"Tris:"+TrisRendered()
Text 640/2,480/2,"Starfox's Dynamic LOD System",1,1
If lodon = 1 Then lodt$ = "On"
If lodon = 0 Then lodt$ = "Off"
Text 640/2,480/2+10,"Space To Toggle LOD: "+lodt,1,1
If KeyHit(57)
togglelodactivation(piv)
lodon = 1 - lodon
EndIf
Flip
Wend
End
