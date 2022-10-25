; ID: 389
; Author: David Bird(Birdie)
; Date: 2002-08-07 19:34:59
; Title: Mesh Merge
; Description: Merge your high entity count into one entity matching surfaces

;Merge all your entities into one
;matching surfaces.

;Call this function to merge your entities.
Function BEGINMERGE(ent)
	DestMesh=CreateMesh()
	RotateMesh ent,EntityPitch(ent,True),EntityYaw(ent,True),EntityRoll(ent,True)
	PositionMesh ent,EntityX(ent,True),EntityY(ent,True),EntityZ(ent,True)
	AddMesh ent,DestMesh
	
	If CountChildren(ent)>0 Then
		For s=1 To CountChildren(ent)
			MERGEMESH destmesh,GetChild(ent,s)
		Next
	EndIf
	Return destmesh
End Function

Function MERGEMESH(destmesh,ent)
	RotateMesh ent,EntityPitch(ent,True),EntityYaw(ent,True),EntityRoll(ent,True)
	PositionMesh ent,EntityX(ent,True),EntityY(ent,True),EntityZ(ent,True)
	AddMesh ent,destmesh
	If CountChildren(ent)>0 Then
		For s=1 To CountChildren(ent)
			MERGEMESH destmesh,GetChild(ent,s)
		Next
	EndIf
End Function
