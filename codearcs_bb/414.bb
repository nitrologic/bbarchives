; ID: 414
; Author: sswift
; Date: 2002-09-02 04:34:03
; Title: Point Inside Oriented Bounding Box
; Description: This function tells you if a point is inside an entity's oriented bounding box.

; -------------------------------------------------------------------------------------------------------------------
; This function returns true if a point is inside an object's oriented bounding box, and false if it is not.
; -------------------------------------------------------------------------------------------------------------------
Function Point_Inside_Oriented_Bounding_Box(ThisEntity, Px#, Px#, Pz#)
	
	
	; Transform point from global space to object space.
	TFormPoint Px#, Py#, Pz#, 0, ThisEntity
	Px# = TFormedX#()
	Py# = TFormedY#()
	Pz# = TFormedZ#()
	
	
	; Calculate the bounding box (in object space) for this object.
	; You can precalculate this for every object in your game for a huge speed increase!
	Surfaces = CountSurfaces(ThisEntity)

	For LOOP_Surface = 1 To Surfaces

		Surface_Handle = GetSurface(ThisEntity, LOOP_Surface)
			
		Verts = CountVertices(Surface_Handle) - 1
		For LOOP_Verts = 0 To Verts-1
			
			Vx# = VertexX#(Surface_Handle, LOOP_Verts)
			Vy# = VertexY#(Surface_Handle, LOOP_Verts)
			Vz# = VertexZ#(Surface_Handle, LOOP_Verts)
			
			If (Vx# > Max_X#) Then Max_X# = Vx#
			If (Vy# > Max_Y#) Then Max_Y# = Vy#
			If (Vz# > Max_Z#) Then Max_Z# = Vz#
				
			If (Vx# < Min_X#) Then Min_X# = Vx#
			If (Vy# < Min_Y#) Then Min_Y# = Vy#
			If (Vz# < Min_Z#) Then Min_Z# = Vz#
				
		Next
		
	Next
	
	
	; Determine if the point (in object space) is inside the bounding box (in object space).
	If (Px# > Min_X#) And (Px# < Max_X#) And (Py# > Min_Y#) And (Py# < Max_Y#) And (Pz# > Min_Z#) And (Pz# < Max_Z#)
		Return True
	Else
		Return False
	EndIf


End Function
