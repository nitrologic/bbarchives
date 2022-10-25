; ID: 422
; Author: starfox
; Date: 2002-09-09 14:16:20
; Title: Unweld
; Description: This function unwelds all the points

Function Unweld(mesh)
;Unweld a mesh, retaining all of its textures coords and textures
For surfcount = 1 To CountSurfaces(mesh)
	surf = GetSurface(mesh,surfcount)

	count = CountTriangles(surf)
	bank = CreateBank((15*count)*4)
	For tricount = 0 To count-1
	off = (tricount*15)*4
	in = TriangleVertex(surf,tricount,0)
	x# = VertexX(surf,in):y#=VertexY(surf,in):z#=VertexZ(surf,in)
	u# = VertexU(surf,in):v#=VertexV(surf,in)
	PokeFloat(bank,off,x)
	PokeFloat(bank,off+4,y)
	PokeFloat(bank,off+8,z)
	PokeFloat(bank,off+12,u)
	PokeFloat(bank,off+16,v)

	in = TriangleVertex(surf,tricount,1)
	x# = VertexX(surf,in):y#=VertexY(surf,in):z#=VertexZ(surf,in)
	u# = VertexU(surf,in):v#=VertexV(surf,in)
	PokeFloat(bank,off+20,x)
	PokeFloat(bank,off+24,y)
	PokeFloat(bank,off+28,z)
	PokeFloat(bank,off+32,u)
	PokeFloat(bank,off+36,v)

	in = TriangleVertex(surf,tricount,2)
	x# = VertexX(surf,in):y#=VertexY(surf,in):z#=VertexZ(surf,in)
	u# = VertexU(surf,in):v#=VertexV(surf,in)
	PokeFloat(bank,off+40,x)
	PokeFloat(bank,off+44,y)
	PokeFloat(bank,off+48,z)
	PokeFloat(bank,off+52,u)
	PokeFloat(bank,off+56,v)

	Next
	
	ClearSurface(surf,True,True)
	
	For tricount = 0 To count-1
	off = (tricount*15)*4
	x# = PeekFloat(bank,off)
	y# = PeekFloat(bank,off+4)
	z# = PeekFloat(bank,off+8)
	u# = PeekFloat(bank,off+12)
	v# = PeekFloat(bank,off+16)
	a = AddVertex(surf,x,y,z,u,v)
	x# = PeekFloat(bank,off+20)
	y# = PeekFloat(bank,off+24)
	z# = PeekFloat(bank,off+28)
	u# = PeekFloat(bank,off+32)
	v# = PeekFloat(bank,off+36)
	b = AddVertex(surf,x,y,z,u,v)
	x# = PeekFloat(bank,off+40)
	y# = PeekFloat(bank,off+44)
	z# = PeekFloat(bank,off+48)
	u# = PeekFloat(bank,off+52)
	v# = PeekFloat(bank,off+56)
	c = AddVertex(surf,x,y,z,u,v)
	AddTriangle(surf,a,b,c)
	Next
	FreeBank bank
Next
UpdateNormals mesh
Return mesh
End Function
