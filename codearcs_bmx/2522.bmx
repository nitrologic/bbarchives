; ID: 2522
; Author: Ked
; Date: 2009-07-05 17:12:25
; Title: Ramp Mesh
; Description: A ramp mesh like what you see in Maplet.

Function CreateRamp:TMesh(parent_ent:TEntity=Null)
	Local mesh:TMesh=TMesh.CreateMesh(parent_ent)
	Local surf:TSurface=mesh.CreateSurface()
	
	Local v0:Int,v1:Int,v2:Int,v3:Int
	
	v0=surf.AddVertex(-1.0,-1.0,-1.0)
	v1=surf.AddVertex(-1.0, 1.0,-1.0)
	v2=surf.AddVertex( 1.0,-1.0,-1.0)
	surf.AddTriangle(v0,v1,v2)
	
	surf.VertexTexCoords(v1,0.0,0.0)
	surf.VertexTexCoords(v0,0.0,1.0)
	surf.VertexTexCoords(v2,1.0,1.0)
	
	v0=surf.AddVertex( 1.0,-1.0, 1.0)
	v1=surf.AddVertex(-1.0, 1.0, 1.0)
	v2=surf.AddVertex(-1.0,-1.0, 1.0)
	surf.AddTriangle(v0,v1,v2)
	
	surf.VertexTexCoords(v0,1.0,1.0)
	surf.VertexTexCoords(v1,0.0,0.0)
	surf.VertexTexCoords(v2,0.0,1.0)
	
	v0=surf.AddVertex(-1.0,-1.0,-1.0)
	v1=surf.AddVertex( 1.0,-1.0,-1.0)
	v2=surf.AddVertex(-1.0,-1.0, 1.0)
	v3=surf.AddVertex( 1.0,-1.0, 1.0)
	surf.AddTriangle(v0,v1,v3)
	surf.AddTriangle(v2,v0,v3)
	
	surf.VertexTexCoords(v0,0.0,0.0)
	surf.VertexTexCoords(v1,0.0,1.0)
	surf.VertexTexCoords(v2,1.0,0.0)
	surf.VertexTexCoords(v3,1.0,1.0)
	
	v0=surf.AddVertex(-1.0,-1.0,-1.0)
	v1=surf.AddVertex(-1.0, 1.0,-1.0)
	v2=surf.AddVertex(-1.0,-1.0, 1.0)
	v3=surf.AddVertex(-1.0, 1.0, 1.0)
	surf.AddTriangle(v2,v1,v0)
	surf.AddTriangle(v3,v1,v2)
	
	surf.VertexTexCoords(v0,1.0,1.0)
	surf.VertexTexCoords(v1,1.0,0.0)
	surf.VertexTexCoords(v2,0.0,1.0)
	surf.VertexTexCoords(v3,0.0,0.0)
	
	v0=surf.AddVertex(-1.0, 1.0,-1.0)
	v1=surf.AddVertex(-1.0, 1.0, 1.0)
	v2=surf.AddVertex( 1.0,-1.0,-1.0)
	v3=surf.AddVertex( 1.0,-1.0, 1.0)
	surf.AddTriangle(v0,v1,v3)
	surf.AddTriangle(v2,v0,v3)
	
	surf.VertexTexCoords(v0,0.0,0.0)
	surf.VertexTexCoords(v1,1.0,0.0)
	surf.VertexTexCoords(v2,0.0,1.0)
	surf.VertexTexCoords(v3,1.0,1.0)
	
	mesh.UpdateNormals()
	Return mesh
EndFunction
