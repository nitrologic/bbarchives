; ID: 2409
; Author: fireshadow4126
; Date: 2009-02-08 17:05:54
; Title: Terrainedit
; Description: Simple Terrain Function

Function terrainedit(terrain,x,z,width,depth,height)

	end_x = x + width
	end_z = z + depth
	
	For a = x To end_x
	For b = z To end_z
		ModifyTerrain terrain,a,b,height
	Next
	Next
	
End Function
