; ID: 215
; Author: Wiebo
; Date: 2002-02-02 04:29:53
; Title: Sticky Shadow
; Description: This function will allow you to let a shadow mesh 'hug' the underlying mesh

Function StickyShadow ()

	For count = 0 To shadow_vertex_count -1

		; translate x and z pos of shadow vertex to world coordinates
		
		TFormPoint ( VertexX (shadow_surf, count), 5, VertexZ (shadow_surf, count), shadowmesh, 0)

		; pick down from each vertex, get new world ypos from pickedy
	
		LinePick ( TFormedX() , 5, TFormedZ(), 0, -6, 0)
	
		; translate back to shadow local coords
		
		TFormPoint ( PickedX(), PickedY()+ .03, PickedZ(), 0, shadowmesh)		
	
		; move current vertex there
		
		VertexCoords shadow_surf, count, VertexX (shadow_surf, count), TFormedY(), VertexZ (shadow_surf, count)
		
	Next
	
End Function
