; ID: 2650
; Author: Warner
; Date: 2010-01-27 05:32:35
; Title: Scenegraph for Blitz3D
; Description: render scenery faster

;------
;Consts
;------
	
	Const area = 48		;render area (size of grid around camera that is rendered)
	Const scale# = 2.0	;scale of scenegraph (array size * scale = 3d world space units)
	Dim scenegraph(1023, 1023) ;should cover entire terrain

;------------------
;SCENEGRAPH EXAMPLE
;------------------

	
	Graphics3D 800, 600, 0, 2
	SetBuffer BackBuffer()

	;----------------------------
	;set up scenegraph with cubes
	;----------------------------
	cube = CreateCube() ;some example cube
	For i = 1 To 10000 ;add 10000 cubes to scenegraph	
		AddToSceneGraph(cube, Rnd(1023), 0, Rnd(1023))	
	Next
	FreeEntity cube ;remove original cube
	
	cam = CreateCamera() ;camera

Repeat

	;camera controls
	If KeyDown(203) TurnEntity cam, 0,  1, 0
	If KeyDown(205) TurnEntity cam, 0, -1, 0
	If KeyDown(200) MoveEntity cam, 0, 0,  1
	If KeyDown(208) MoveEntity cam, 0, 0, -1

	;update scenegraph around camera
	UpdateSceneGraph(cam)
		
	RenderWorld
	Flip

Until KeyHit(1)

End

;copymesh a mesh onto scenegraph
Function AddToSceneGraph(original, x#, y#, z#) 

	rx = Int(x / scale) ;mesh position relative to "scenegraph" (array)
	rz = Int(z / scale)
		
	If (rx < 0) 	DebugLog "out of range": Return ;check bounds
	If (rx > 1023) 	DebugLog "out of range": Return
	If (rz < 0) 	DebugLog "out of range": Return
	If (rz > 1023) 	DebugLog "out of range": Return	
	
	If scenegraph(rx, rz) = 0 Then ;if no mesh exists yet at this location
		scenegraph(rx, rz) = CreateMesh() ;create new, empty mesh
		PositionEntity scenegraph(rx, rz), rx * scale, 0, rz * scale ;position it in the center of this area of the scenegraph
		HideEntity scenegraph(rx, rz) ;hide the new mesh
	End If
	
	mesh = CopyMesh(original) ;create a mesh copy of the mesh that was passed to this function
	PositionMesh mesh, (x - EntityX(scenegraph(rx, rz))), y, (z - EntityZ(scenegraph(rx, rz))) ;position it relative to center of area
	AddMesh mesh, scenegraph(rx, rz) ;addmesh it to everything else that is located on this position
	FreeEntity mesh ;free the new mesh copy
		
End Function

;-----------------
;Update Scenegraph
;-----------------
Function UpdateSceneGraph(cam)

	;determine camera position relative to "scenegraph" array/grid	
	rx = Int(EntityX(cam) / scale)
	rz = Int(EntityZ(cam) / scale)

	;search area around camera	
	For i = -area To area
		rxi = rx + i ;x-location on grid/array
	
		If (rxi >= 0) And (rxi <= 1023)	Then ;check bounds
		
			For j = -area To area
				rzj = rz + j ;z-location on grid/array
				
				If (rzj >= 0) And (rzj <= 1023) ;check bounds
				
					If scenegraph(rxi, rzj) Then ;if a mesh is located on this position
					
						If (Abs(i) = area) Or (Abs(j) = area) Then ;if it is on the border of the search "area", then ..
							HideEntity scenegraph(rxi, rzj) 	   ;hide it
						Else
							ShowEntity scenegraph(rxi, rzj) ;if it is in the middle of the search "area", then
						End If								;show it
					End If
				
				End If
				
			Next
		End If
		
	Next

End Function
