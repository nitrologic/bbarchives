; ID: 2176
; Author: BIG BUG
; Date: 2008-01-01 23:23:29
; Title: UpdateNormalsAngle
; Description: Enhanced UpdateNormals

;by Robert Hierl / www.mein-murks.de / 30.12.2007
Type tVertexVector

	Field vertex
	Field x#
	Field y#
	Field z#
	
End Type


Type tVertexTree

	Field x#
	Field y#
	Field z#
	Field vertices.tVertexVector[50]
	Field octree.tVertexTree[7]

End Type



Function UpdateNormalsAngle( pMesh, pAngle# = 89 )
;This function is used much like the regular UpdateNormals in B3D. Unlike the original function, 
;it doesn't just smooth all edges, but provides an option to set an angle, to which two faces are smoothed.
;So it's a very easy way to improve shading on a mesh, without setting up each normal manually.
;Author Robert Hierl / www.mein-murks.de

	Local surf, numSurface
	Local vert, numVertex
	Local vertexTree.tVertexTree
	
	Local triNormal.tVertexVector
	
	
	Local v1.tVertexVector, v2.tVertexVector, v3.tVertexVector

	;handle special ones
	If pAngle# =  0   Then UpdateNormalsFlat( pMesh ) : Return
	;regular UpdateNormals works only, when mesh was modified, so line is commented for this example
	;If pAngle# >= 180 Then UpdateNormals( pMesh ) : Return


	For numSurface = 1 To CountSurfaces( pMesh )

		surf = GetSurface( pMesh, numSurface )

		Delete Each tVertexTree
		vertexTree = New tVertexTree

		;gather all possible vertex coordinates with their triangle normal		
		For numTriangle = 0 To CountTriangles( surf ) - 1

			v1 = GetVertexVector(surf, TriangleVertex( surf, numTriangle, 0 ))
			v2 = GetVertexVector(surf, TriangleVertex( surf, numTriangle, 1 ))
			v3 = GetVertexVector(surf, TriangleVertex( surf, numTriangle, 2 ))	
			
			;calculate triangle normal
			triNormal = GetTriangleNormal(surf, v1\vertex, v2\vertex, v3\vertex)
			
			;add each vertex with calculated normal
			AddVertex2Tree(vertexTree, v1, triNormal)
			AddVertex2Tree(vertexTree, v2, triNormal)
			AddVertex2Tree(vertexTree, v3, triNormal)
				
			;clean up
			Delete triNormal
			Delete v1
			Delete v2
			Delete v3
		
		Next

		;calculate and set new vertex normals			
		For vertexTree = Each tVertexTree	
		
			SetNormalsMulti(vertexTree, surf, pAngle#)

		Next

		;clean up
		Delete Each tVertexTree
		Delete Each tVertexVector

	Next

End Function


Function UpdateNormalsFlat(mesh)
;This simple function is used to disable smoothing on a mesh. It is faster than UpdateNormalsAngle with value 0.

	Local surf, numSurface, numTriangle
	Local v1, v2, v3
	Local triNormal.tVertexVector

	For numSurface = 1 To CountSurfaces(mesh)

		surf = GetSurface(mesh, numSurface)
		
		For numTriangle = 0 To CountTriangles( surf ) - 1
		
			;calculate normal for each triangle
        	v1   = TriangleVertex(surf, numTriangle, 0)
        	v2   = TriangleVertex(surf, numTriangle, 1)
        	v3   = TriangleVertex(surf, numTriangle, 2) 

			triNormal = GetTriangleNormal(surf, v1, v2, v3)
		
			;set normals for vertex
			VertexNormal surf, v1, triNormal\x, triNormal\y, triNormal\z
 
			;when using EntityFX 4, only the first vertex normal is relevant, in this case just comment the following two lines
			VertexNormal surf, v2, triNormal\x, triNormal\y, triNormal\z
			VertexNormal surf, v3, triNormal\x, triNormal\y, triNormal\z
			
		Next
     Next

End Function



Function SetNormalsMulti(vertexTree.tVertexTree, surf, pAngle#)
	;calculate new normals
	
	Local ax#, ay#, az#
	Local lx#, ly#, lz#
	Local nx#, ny#, nz#
	Local factor#, merged
	Local diffAngle#, vertex.tVertexVector
	
	Local i, l


		Repeat
		
			merged = False

			For i = 0 To 50	
	
				If vertexTree\vertices[i] = Null Then Exit
				
				vertexcount = 0
				nx# = 0
				ny# = 0
				nz# = 0		
	
				For l = 0 To 50
				
					If vertexTree\vertices[l] = Null Then Exit	
									
					diffAngle# = VectorAngle#(vertexTree\vertices[l], vertexTree\vertices[i])
					
					If diffAngle# <= pAngle# Then
					
						If diffAngle# > 0 Then merged = True;						

						vertex.tVertexVector = vertexTree\vertices[l]
						vertexcount = vertexcount + 1
						nx# = nx# + vertex\x
						ny# = ny# + vertex\y
						nz# = nz# + vertex\z
						
					EndIf
				
				Next
			
				nx# = nx# / vertexcount
				ny# = ny# / vertexcount
				nz# = nz# / vertexcount

				
				;normalize result
				factor# = Sqr((nx# * nx#)+(ny# * ny#)+(nz# * nz#))   
				nx# = nx# / factor#
				ny# = ny# / factor#
				nz# = nz# / factor#
				
				
				VertexNormal surf, vertexTree\vertices[i]\vertex, nx#, ny#, nz#
			Next


			If Not merged Then Exit
						
			For i = 0 To 50	
	
				If vertexTree\vertices[i] = Null Then Exit	
				
				vertex = vertexTree\vertices[i]
								
				vertex\x = VertexNX(surf, vertex\vertex)
				vertex\y = VertexNY(surf, vertex\vertex)
				vertex\z = VertexNZ(surf, vertex\vertex)
				
			Next							
			

		Forever


End Function


Function GetVertexVector.tVertexVector(pSurface, pVertex)
	;this one provides the coordinate of a given vertex as vector
	Return VertexVector(VertexX#(pSurface, pVertex), VertexY#(pSurface, pVertex), VertexZ#(pSurface, pVertex), pVertex)

End Function 


Function AddVertex2Tree( pNode.tVertexTree, pVertex.tVertexVector, pNormal.tVertexVector)
	;adds a vertex to our octree
	Local i, treePosition

	;if our coordinate matches, we just add the given vertex normal to the list
	If pNode\x = pVertex\x And pNode\y = pVertex\y And pNode\z = pVertex\z Then
	
		For i = 0 To 50
			If pNode\vertices[i] = Null Then 
			   pNode\vertices[i] = VertexVector(pNormal\x#, pNormal\y#, pNormal\z#, pVertex\vertex)
			   Return
			EndIf		
		Next
		
	Else

		If pNode\x >= pVertex\x Then treePosition = treePosition Or 1
		If pNode\y >= pVertex\y Then treePosition = treePosition Or 2
		If pNode\z >= pVertex\z Then treePosition = treePosition Or 4	

		If pNode\octree[treePosition] = Null Then
		
			pNode\octree[treePosition] 		= New tVertexTree
			pNode\octree[treePosition]\x# 	= pVertex\x#
	   		pNode\octree[treePosition]\y# 	= pVertex\y#
			pNode\octree[treePosition]\z# 	= pVertex\z#
			pNode\octree[treePosition]\vertices[0] 	= VertexVector(pNormal\x#, pNormal\y#, pNormal\z#, pVertex\vertex)
	
		Else
	
			AddVertex2Tree( pNode\octree[treePosition], pVertex, pNormal)	
	
		EndIf

	EndIf

End Function




Function GetTriangleNormal.tVertexVector(pSurface, v1, v2, v3)
	;return normal of given triangle as vector
	Local factor#

	;v1 to v2 as vector      
	Local lx# = VertexX#(pSurface,v1) - VertexX#(pSurface,v2)
	Local ly# = VertexY#(pSurface,v1) - VertexY#(pSurface,v2)
	Local lz# = VertexZ#(pSurface,v1) - VertexZ#(pSurface,v2)

	;v1 to v3 as vector  
	Local ax# = VertexX#(pSurface,v1) - VertexX#(pSurface,v3)
	Local ay# = VertexY#(pSurface,v1) - VertexY#(pSurface,v3)
	Local az# = VertexZ#(pSurface,v1) - VertexZ#(pSurface,v3)

	;cross product of these two vectors
	Local nx# = (ly# * az#)-(lz# * ay#)
	Local ny# = (lz# * ax#)-(lx# * az#)
	Local nz# = (lx# * ay#)-(ly# * ax#)
		
	;normalize result ( set vector length to 1 )
	factor# = Sqr((nx# * nx#)+(ny# * ny#)+(nz# * nz#))   
	nx# = nx# / factor#
	ny# = ny# / factor#
	nz# = nz# / factor#

	Return VertexVector(nx#, ny#, nz#) 

End Function


Function VertexVector.tVertexVector(x#, y#, z#, pVertex = -1)
	;creates a VertexVector type, storing coordinates and related mesh vertex

	Local Vector.tVertexVector
	
	Vector  	  = New tVertexVector
	Vector\x#	  = x#
	Vector\y#	  = y#
	Vector\z#	  = z#
	Vector\vertex = pVertex

	Return Vector

End Function


Function VectorAngle#(v1.tVertexVector,v2.tVertexVector)
	;returns angle between two normalized vectors
	;dot product is converted to integer and back to avoid some weird float issues
	;(as a matter of fact I don't know what the problem is exactly, maybe rounding differences, maybe NaN)
	Local dot = ((v1\X * v2\X) + ( v1\Y * v2\Y) + (v1\Z*v2\Z)) * 10000
	Return ACos#( dot / 10000.0 )	
			
End Function
