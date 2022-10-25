; ID: 2600
; Author: Nate the Great
; Date: 2009-10-31 22:48:53
; Title: anti alias/glow a single entity
; Description: anti alias an entity or make it glow

Graphics3D 800,600,0,2

cam = CreateCamera()
MoveEntity cam,0,0,-10

cub = CreateCube()
EntityColor cub,100,100,255
cubeaa.aamesh = createaamesh(cub,cam,9,.0033333,1,True,100,100,255)   ;the parameters are as follows.  entity you want to antialias, camera to do it in, layers,distance between layers, how "light" you want the aa, will the aa scale depending on how close the camera is?, red, green, blue for glow fx.


lit = CreateLight()
TurnEntity lit,90,0,0


hideaaflag = False

While Not KeyDown(1)
	Cls
	TurnEntity cub,.3,.3,0
	PositionEntity(cam,0,0,-200.0+MouseX()/4.0)
	If hideaaflag = True Then
		hideaa(cubeaa.aamesh)
	Else
		showaa(cubeaa.aamesh)
	EndIf
	If KeyHit(57) Then hideaaflag = 1-hideaaflag
	
	updateaameshes(cam)
	
	RenderWorld()
	Flip
Wend


Type AAMesh
	Field entity
	Field ccount	;copy count, number of times to render the mesh
	Field gdist#		;gap- distance between meshes
	Field SDist		;Starting distance from the camera.
	Field cam
	Field meshes[10]
	Field hide
	Field scale
End Type

Function CreateAAMesh.AAMesh(entity,cam,ccount,gdist#,alphadiv#,scale,r = 255,g = 255,b = 255,sdist# = 0)
	Local a.aamesh = New aamesh
	a\entity = entity
	a\ccount = ccount
	a\gdist = gdist
	a\sdist = sdist
	a\scale = scale
	a\cam = cam
	If sdist = 0 Then
		a\sdist = EntityDistance(cam,a\entity)
	EndIf
	
	For i = 1 To ccount
		a\meshes[i] = CopyMesh(entity)
		EntityAlpha a\meshes[i],((1.0/(ccount+1))*(ccount+1-i))/alphadiv
		ScaleEntity(a\meshes[i],i*gdist+1,i*gdist+1,i*gdist+1)
		PositionEntity(a\meshes[i],EntityX(entity),EntityY(entity),EntityZ(entity))
		TurnEntity(a\meshes[i],EntityPitch(entity),EntityYaw(entity),EntityRoll(entity))
		EntityColor a\meshes[i],r,g,b
		EntityParent(a\meshes[i],entity)	
	Next
	
	Return a.aamesh
End Function


Function hideAA(a.aamesh)
	a\hide = True
End Function
Function showAA(a.aamesh)
	a\hide = False
End Function


Function UpdateAAMeshes(cam)
	For a.aamesh = Each aamesh
		If a\cam = cam And a\hide = False Then
			For i = 1 To a\ccount
				ShowEntity a\meshes[i]
				If a\scale = True Then
					Local dist# = EntityDistance(a\entity,cam)
					Local scl# = (i*a\gdist*(dist#/a\sdist#))+1.0
					ScaleEntity a\meshes[i],scl,scl,scl
				EndIf
				
			Next
		Else
			For i = 1 To a\ccount
				HideEntity a\meshes[i]
			Next
		EndIf
	Next
End Function
