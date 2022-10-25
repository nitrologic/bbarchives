; ID: 2883
; Author: RifRaf
; Date: 2011-08-23 13:58:07
; Title: Ambient Volume Lib
; Description: Create and save ambient volumes to shade moving entities and shade map large maps

Include "amb_volume.bb"
;//----------------------------------------------------------       ----TEST LIB---
;//----------------------------------------------------------       ----TEST LIB---
;//----------------------------------------------------------       ----TEST LIB---
;// PRESS 1 TO DROP A RANDOM COLORED LIGHT
;// click SPACE BAR button to toggle ambient volume map on the level mesh

Print "AMBIENT VOLUME LIB v1.0"
Print ""
Print ""
Print ""
Print "[ SPACEBAR ] : Toggle ambient scene mapping."
Print "[ 1 ]        : Drop a ambient volume light."
Print "[ ESCAPE ]        : Quit program. "
Print "[ MOUSE BUTTONS ] : move around."
Print ""
Print "press any key to start "
WaitKey()

amb_smoothContrastdown_Startat=3
amb_smoothContrastdown_Endat=5

amb_smoothContrastDown#=.95

amb_SmoothContrastup_Startat=2
amb_SmoothContrastup_Endat=3

amb_SmoothContrastup#=1.01


Graphics3D 800,600,0,2

 displayamb=1
 camera=CreateCamera()
 CameraClsColor camera,0,55,125
 CameraRange camera,1,13000
 avatar=CreateSphere()
 EntityFX avatar,1
 
 PositionEntity avatar,0,-2,3
 EntityParent avatar,camera
 mesh=LoadMesh("yourmodelhere.b3d")
 ;toonmeshuv mesh
 EntityPickMode mesh,2
 Amb_width# = MeshWidth(mesh)
 Amb_Height#= MeshHeight(mesh)
 Amb_Depth# = MeshDepth(mesh)
 AmbientLight 190,190,190
 
 PositionEntity camera,-100,-250,0

 ;//if you want to play with different settings in this test, just change 
 ;//the filename here to something that will never exist.. such as "ambientvolume44.dat"
 ;//if you dont then it will only create the volume once, then load it the next time.
 If FileType("ambientvolume.dat")<>1 Then 
     
	 amb_smoothContrastdown_Startat=2
	 amb_smoothContrastdown_Endat=3
	 amb_smoothContrastDown#=.95
	 amb_SmoothContrastup_Startat=3
	 amb_SmoothContrastup_Endat=5
	 amb_SmoothContrastup#=1.5
     smooth_times=5
     lowest_Ambient=15
     x_cells=100
     y_cells=60
     z_cells=100

    ;another setting to try
    ; amb_smoothContrastdown_Startat=3
	; amb_smoothContrastdown_Endat=5
	; amb_smoothContrastDown#=.95
	; amb_SmoothContrastup_Startat=1
	; amb_SmoothContrastup_Endat=5
	; amb_SmoothContrastup#=1.01
    ; smooth_times=5
    ; lowest_Ambient=30
    ; x_cells=120
    ; y_cells=90
    ; z_cells=120

     ;prep and create flipped mesh copy 
	 Amb_prepMeshBeforeCreation(mesh)
    
     ;//the following two calls accomplish the same thing, but can be useful to use area if youre level mesh is not centered in global space 
	 ;//you can specify how many smooths to do in the creaion.. or you can do it later with a direct call to smoothambientvolume()

	 ;createambientvolume_area(-1660,-1250,-1700,1660,1250,1700,smooth_times,lowest_ambient,x_cells,y_cells,z_cells) 
     ;CreateblankAmbientVolume	(mesh,Smooth_Times,Lowest_Ambient,x_cells,y_cells,z_cells)
     CreateAmbientVolume_SizeOfMesh	(mesh,Smooth_Times,Lowest_Ambient,x_cells,y_cells,z_cells)
     ;remove flipped mesh copy    
	 Amb_freesparemeshes()

     ;save and apply the volume
	 saveambientvolume ("ambientvolume.dat")
	 ApplyAmbientVolumeToMesh(mesh)

 Else 
 
     ;if the file exists load and apply volume 
	 loadambientvolume("ambientvolume.dat")
	 ApplyAmbientVolumeToMesh(mesh)
 EndIf



While Not KeyDown(1)
	;hit SPACEBAR  to see with and without volume applied to level mesh
	If KeyHit(57) Then
         displayamb=displayamb-1
         If displayamb<=0 Then displayamb=3
         Select displayamb
         Case 2
             FreeEntity mesh 
			 mesh=LoadMesh("cabin.b3d")
			 EntityPickMode mesh,2
             
			removeambientvolumefrommesh(mesh,0)
			EntityFX mesh,2
         Case 3
			removeambientvolumefrommesh(mesh,1)
  		    applyambientvolumetomesh(mesh)
			EntityFX mesh,2
 		 Case 1
 			 applyambientvolumetomesh(mesh)
			EntityFX mesh,2
		End Select         
	EndIf

    ;press 1 on the keyboard to drop a random colored volume light
	If KeyHit(2) Then
        ;go ahead and prep main mesh (make flipped copy) to help the light accuracy (needs all the help it can get)
        Amb_prepMeshBeforeCreation(mesh)
		r=Rand(120,255)
		g=Rand(120,255)
		b=Rand(120,255)
	    Amb_InsertLight(EntityX(camera),EntityY(Camera),EntityZ(Camera),350,r,g,b)
	    Amb_freesparemeshes()
    	applyambientvolumetomesh(mesh)
	EndIf

    ;allow camera movement with the mouse buttons
	MoveEntity Camera,(KeyDown(205)-KeyDown(203))*5,0,(MouseDown(1)-MouseDown(2))*5
	TurnEntity Camera,MouseYSpeed()*0.1,-MouseXSpeed()*0.1,0
	RotateEntity Camera,EntityPitch(Camera,True),EntityYaw(Camera,True),0

    ;use amb_entitycolor() to show how an enity can be shaded by the ambient volume
	amb_entitycolor(Avatar)
    ;position mouse in middle so mouselook can work
	MoveMouse GraphicsWidth()*.5,GraphicsHeight()*.5
	UpdateWorld()
	RenderWorld()
	Flip
Wend
amb_vol_clearall()
EndGraphics()
End
;//----------------------------------------------------------       ----END TEST---
;//----------------------------------------------------------       ----END TEST---
;//----------------------------------------------------------       ----END TEST---
