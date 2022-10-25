; ID: 2523
; Author: Ked
; Date: 2009-07-08 13:41:02
; Title: [BMX] Wedge Mesh
; Description: A wedge mesh like you would see in Maplet.

Function CreateWedge:TMesh(parent_ent:TEntity=Null)
	Local mesh:TMesh=TMesh.CreateMesh(parent_ent)
	Local surf:TSurface=mesh.CreateSurface()
	
	Local v0:Int,v1:Int,v2:Int,v3:Int
	
	v0=surf.AddVertex(-1.0, 1.0,-1.0,0.0,0.0)
	v1=surf.AddVertex( 1.0, 1.0,-1.0,1.0,0.0)
	v2=surf.AddVertex(-1.0,-1.0,-1.0,0.0,1.0)
	v3=surf.AddVertex( 1.0,-1.0,-1.0,1.0,1.0)
	surf.AddTriangle(v0,v1,v2)
	surf.AddTriangle(v2,v1,v3)
	
	v0=surf.AddVertex(-1.0, 1.0,-1.0,0.0,1.0)
	v1=surf.AddVertex( 1.0, 1.0,-1.0,1.0,1.0)
	v2=surf.AddVertex( 0.0, 1.0, 1.0,0.5,0.0)
	surf.AddTriangle(v2,v1,v0)
	
	v0=surf.AddVertex(-1.0,-1.0,-1.0,0.0,1.0)
	v1=surf.AddVertex( 1.0,-1.0,-1.0,1.0,1.0)
	v2=surf.AddVertex( 0.0,-1.0, 1.0,0.5,0.0)
	surf.AddTriangle(v0,v1,v2)
	
	v0=surf.AddVertex(-1.0, 1.0,-1.0,1.0,0.0)
	v1=surf.AddVertex(-1.0,-1.0,-1.0,1.0,1.0)
	v2=surf.AddVertex( 0.0, 1.0, 1.0,0.0,0.0)
	v3=surf.AddVertex( 0.0,-1.0, 1.0,0.0,1.0)
	surf.AddTriangle(v0,v1,v2)
	surf.AddTriangle(v2,v1,v3)
	
	v0=surf.AddVertex( 1.0, 1.0,-1.0,0.0,0.0)
	v1=surf.AddVertex( 1.0,-1.0,-1.0,0.0,1.0)
	v2=surf.AddVertex( 0.0, 1.0, 1.0,1.0,0.0)
	v3=surf.AddVertex( 0.0,-1.0, 1.0,1.0,1.0)
	surf.AddTriangle(v2,v1,v0)
	surf.AddTriangle(v3,v1,v2)
	
	mesh.UpdateNormals()
	Return mesh
EndFunction
