; ID: 1285
; Author: John J.
; Date: 2005-02-09 17:27:45
; Title: Polygon Reduction
; Description: A function to reduce the polygons in a mesh

;=========================================================================
;Polygon Reduction Function - Based on the Blitz3D example at "examples\birdie\lodmesh" by David Bird
;-------------------------------------------------------------------------
;
;Using the reduction function is extremely simple. Just call the
;MeshPolygonsReduce Function, with the mesh and reduction amount. Example:
;
;MeshPolygonsReduce(ent,500) ;Reduce 500 polygons from 'ent'
;
;=========================================================================

;**** Internal data ****
Const MAXTRIS=100000

Dim PR_TriVisible(MAXTRIS)
Dim PR_TriCost#(MAXTRIS)
Dim PR_TriVert(MAXTRIS,3)
Dim PR_TriConnect(MAXTRIS,2)

Global PR_TriangleCount=0,PR_VertexCount=0
Global PR_CurrentSurface



;============================== Main Function ============================
;MeshPolygonsReduce (mesh, n)
;  inputmesh: The mesh to reduce
;  n: The amount of polygons to reduce from the inputmesh
Function MeshPolygonsReduce(mesh,n)
	;Make sure "mesh" is really a mesh
	If EntityClass$(mesh)<>"Mesh" Then
		RuntimeError "Can't reduce polygons of a "+Lower$(EntityClass$(mesh))
		Return
	End If
	
	If n<=0 Then Return ;Don't need to do anything
	
	;Divide n by the surface count
	n=n/CountSurfaces(mesh)
	If n<1 Then n=1 ;Don't do nothing

	;For every surface
	For surf=1 To CountSurfaces(mesh)
		;Get surface
		PR_CurrentSurface=GetSurface(mesh,surf)

		;Calculate surface data
		CalculateMeshData(mesh)
	
		;Remove n lowest cost polys from mesh
		For i=1 To n
			tri=LeastNeededTriangle()
			If tri<>-1 Then RemoveTriangle(tri)
		Next
	
		;Clear the surface's triangles
		ClearSurface PR_CurrentSurface,False,True

		;Regenerate the mesh with the new level of detail
		For i=0 To PR_TriangleCount
			If PR_TriVisible(i) Then
				AddTriangle PR_CurrentSurface,PR_TriVert(i,0),PR_TriVert(i,1),PR_TriVert(i,2)
			End If
		Next
	Next

	;Update the new mesh's normals
	UpdateNormals mesh
End Function
;-------------------------------------------------------------------------



;********* Internal Functions *********
Function CalculateMeshData(mesh)
	;Clear data
	PR_TriangleCount=0
	PR_VertexCount=0
	
	;Get vertex count
	PR_VertexCount=CountVertices(PR_CurrentSurface)
	
	;Initialise triangles
	PR_TriangleCount=CountTriangles(PR_CurrentSurface)-1
	For triindex=0 To PR_TriangleCount
		For i=0 To 2
			PR_TriVert(triindex,i)=TriangleVertex(PR_CurrentSurface,triindex,i)
		Next
		PR_TriVisible(triindex)=True
	Next
	
	;Process triangles
	For triindex=0 To PR_TriangleCount
		CalculateTriangleCost#(triindex) ;Calculate the triangle's importance
	Next
End Function

Function LeastNeededTriangle()
	;Find minimum cost
	minimumcost#=1000000
	triindex=-1
	For i=0 To PR_TriangleCount
		If PR_TriVisible(i) Then
			If PR_TriCost(i)<minimumcost Then 
				triindex=i
				minimumcost=PR_TriCost(i)
			End If
		End If
	Next
	;Return lowest triangle
	Return triindex
End Function

Function RemoveTriangle(tri)
	PR_TriVisible(tri)=False
	
	;Re-attach vertices
	oldvert=PR_TriVert(tri,PR_TriConnect(tri,0))
	newvert=PR_TriVert(tri,PR_TriConnect(tri,1))
	For i=0 To PR_TriangleCount
		changed=False
		If PR_TriVert(i,0)=oldvert Then PR_TriVert(i,0)=newvert:changed=True
		If PR_TriVert(i,1)=oldvert Then PR_TriVert(i,1)=newvert:changed=True
		If PR_TriVert(i,2)=oldvert Then PR_TriVert(i,2)=newvert:changed=True
		If changed Then ;Recalculate triangle cost if the triangle has been re-attached
			CalculateTriangleCost(i)
		End If
	Next
End Function

Function CalculateTriangleCost#(tri)
	;Calculate the edge sizes of the triangle
	x0#=VertexX(PR_CurrentSurface,PR_TriVert(tri,0)):y0#=VertexY(PR_CurrentSurface,PR_TriVert(tri,0)):z0#=VertexZ(PR_CurrentSurface,PR_TriVert(tri,0))
	x1#=VertexX(PR_CurrentSurface,PR_TriVert(tri,1)):y1#=VertexY(PR_CurrentSurface,PR_TriVert(tri,1)):z1#=VertexZ(PR_CurrentSurface,PR_TriVert(tri,1))
	x2#=VertexX(PR_CurrentSurface,PR_TriVert(tri,2)):y2#=VertexY(PR_CurrentSurface,PR_TriVert(tri,2)):z2#=VertexZ(PR_CurrentSurface,PR_TriVert(tri,2))
	coord0#=Sqr((x0-x1)^2+(y0-y1)^2+(z0-z1)^2)
	coord1#=Sqr((x1-x2)^2+(y1-y2)^2+(z1-z2)^2)
	coord2#=Sqr((x2-x0)^2+(y2-y0)^2+(z2-z0)^2)

	;Pick the best vertice to use when removing the triangle	
	If coord0<coord1 And coord0<coord2 Then n=1
	If coord1<coord0 And coord1<coord2 Then n=2
	If coord2<coord0 And coord2<coord1 Then n=3
	
	Select n
		Case 0
			PR_TriConnect(tri,0)=1:PR_TriConnect(tri,1)=0
		Case 1
			PR_TriConnect(tri,0)=1:PR_TriConnect(tri,1)=0
		Case 2
			PR_TriConnect(tri,0)=2:PR_TriConnect(tri,1)=1
		Case 3
			PR_TriConnect(tri,0)=0:PR_TriConnect(tri,1)=2
	End Select

	;Set the cost to the sum of the triangle's edges
	PR_TriCost#(tri)=coord0+coord1+coord2
End Function
