; ID: 352
; Author: Rob 
; Date: 2002-06-24 13:22:33
; Title: LOD handling code
; Description: Rather useful lod routine. Handles detail levels easily.

; Lod handling code. Loads three meshes and dynamically 
; swaps between all three depending on the distance.
; Useful For games containing high polygon objects.
; (Use up/down arrow to move camera.)
;
; By Rob Cummings


Graphics3D 640,480,16,2
camera=CreateCamera()
light=CreateLight(2)
PositionEntity light,1000,1000,1000
PositionEntity camera,0,0,-20

; For the purposes of code archives, I have provided a simple function to mimic
; LoadLodMesh (createlodmesh)

;player = LoadLodMesh("lowdetail.3ds","mediumdetail.3ds","highdetail.3ds")
player = CreateLodMesh()


While Not KeyHit(1)

	If KeyDown(200) Then MoveEntity camera,0,0,1
	If KeyDown(208) Then MoveEntity camera,0,0,-1
	If KeyHit(17) Then wire=1-wire : WireFrame wire

	UpdateWorld
	updatelod(player,camera,100) ;<< disbias (last parameter) controls lod changes.
	RenderWorld
	Text 0,0,"Polycount: "+TrisRendered()
	Text 0,16,"Press W to toggle wireframe."
	Text 0,32,"Up/Down arrows to move camera."
	Flip
Wend
End


;lod "distbias" controls the amount of distance between each lod. Divide your
;camerarange by three in order to get a useful approximation, but you may want to
;use a lot less. Experiment!

Function UpdateLod(lodpivot,lodcamera,distbias#=250)
	If Not EntityInView(lodpivot,lodcamera) Return 0
	distlod#=EntityDistance(lodpivot,lodcamera)
	HideEntity lodpivot
	If distlod<1*distbias
		ShowEntity GetChild(lodpivot,1)
	ElseIf distlod<2*distbias
		ShowEntity GetChild(lodpivot,2)
	ElseIf distlod<3*distbias
		ShowEntity GetChild(lodpivot,3)
	EndIf
End Function

Function LoadLodMesh(lodfile1$,lodfile2$,lodfile3$)
	temppivot=CreatePivot()
	templod=LoadAnimMesh(lodfile3$):EntityParent templod,temppivot
	templod=LoadAnimMesh(lodfile2$):EntityParent templod,temppivot
	templod=LoadAnimMesh(lodfile1$):EntityParent templod,temppivot
	Return temppivot
End Function

Function CreateLodMesh() ; for testing purposes
	temppivot=CreatePivot()
	templod=CreateSphere(32):EntityParent templod,temppivot
	templod=CreateSphere(16):EntityParent templod,temppivot
	templod=CreateSphere(8):EntityParent templod,temppivot
	ScaleEntity temppivot,10,10,10
	Return temppivot
End Function
