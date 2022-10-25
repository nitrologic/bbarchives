; ID: 1844
; Author: Devils Child
; Date: 2006-10-24 22:57:21
; Title: GetEntityBox()
; Description: This function returns the entity's bounding box sizes.

Global EntityBoxX#, EntityBoxY#, EntityBoxZ#
Function GetEntityBox(ent, recursive = True, root = 0)
If root = 0 Then
	EntityBoxX# = 0
	EntityBoxY# = 0
	EntityBoxZ# = 0
Else
	ox# = EntityX(ent, True) - EntityX(root, True)
	oy# = EntityY(ent, True) - EntityY(root, True)
	oz# = EntityZ(ent, True) - EntityZ(root, True)
EndIf
cnt_surf = CountSurfaces(ent)
For s = 1 To cnt_surf
	surf = GetSurface(ent, s)
	cnt_verts = CountVertices(surf) - 1
	For v = 0 To cnt_verts
		vx# = Abs(VertexX(surf, v) + ox#)
		vy# = Abs(VertexY(surf, v) + oy#)
		vz# = Abs(VertexZ(surf, v) + oz#)
		If (vx# > EntityBoxX#) Then EntityBoxX# = vx#
		If (vy# > EntityBoxY#) Then EntityBoxY# = vy#
		If (vz# > EntityBoxZ#) Then EntityBoxZ# = vz#
	Next
Next
If recursive Then
	If root = 0 Then root = ent
	cnt_children = CountChildren(ent)
	For i = 1 To cnt_children
		GetEntityBox(GetChild(ent, i), True, root)
	Next
EndIf
End Function
