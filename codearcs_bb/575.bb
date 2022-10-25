; ID: 575
; Author: sswift
; Date: 2003-02-04 21:58:17
; Title: AddMeshToSurface
; Description: This function allows you to otpimize your levels by addding a single surface mesh to a specific surface of another mesh.

; -------------------------------------------------------------------------------------------------------------------
; This function copies a mesh with a single surface to a specific surface of another mesh.
;
; The function can optionally offset the mesh within the destination mesh as it copies it.
; The last six parameters may be omitted when calling the function if no transformation is desired.
; -------------------------------------------------------------------------------------------------------------------
Function AddMeshToSurface(SrcMesh, DestMesh, DestSurface, Voffsetx#=0, Voffsety#=0, Voffsetz#=0, Vpitch#=0, Vyaw#=0, Vroll#=0, Vscalex#=1, Vscaley#=1, VscaleZ#=1)

	; Determine if we can optimize the mesh copying.
	TransformVertices = True
	If (Vpitch#=0) And (Vyaw#=0) And (Vroll#=0) 
		TransformVertices = False
	EndIf


	; Make sure there's a surface to copy, because the mesh might be empty.
	If CountSurfaces(SrcMesh) > 0

		SrcSurface = GetSurface(SrcMesh, 1)

		DestVerts  = CountVertices(DestSurface)
		SrcVerts   = CountVertices(SrcSurface)
			
		; If we need to transform the vertices in a complex way...
		If TransformVertices

			; Do slower copy method because we need to rotate the vertices.

			; Create a pivot to do the transformations with.
			ThisPivot = CreatePivot()
			PositionEntity ThisPivot, Voffsetx#, Voffsety#, Voffsetz#, True
			RotateEntity ThisPivot, Vpitch#, Vyaw#, Vroll#, True
			ScaleEntity ThisPivot, Vscalex#, Vscaley#, Vscalez#

			; Copy all the vertices from the source mesh to the destination surface.
			For VertLoop = 0 To SrcVerts-1
					
				Vu#  = VertexU#(SrcSurface, VertLoop)
				Vv#  = VertexV#(SrcSurface, VertLoop)		
				Vw#  = VertexW#(SrcSurface, VertLoop)
				Vr   = VertexRed(SrcSurface, VertLoop)
				Vg   = VertexGreen(SrcSurface, VertLoop)
				Vb   = VertexBlue(SrcSurface, VertLoop)
			
				TFormPoint VertexX#(SrcSurface, VertLoop), VertexY#(SrcSurface, VertLoop), VertexZ#(SrcSurface, VertLoop), ThisPivot, 0
				Vx# = TFormedX#()
				Vy# = TFormedY#()
				Vz# = TFormedZ#()

				TFormNormal VertexNX#(SrcSurface, VertLoop), VertexNY#(SrcSurface, VertLoop), VertexNZ#(SrcSurface, VertLoop), ThisPivot, 0
				Vnx# = TFormedX#()
				Vny# = TFormedY#()
				Vnz# = TFormedZ#()
			
				AddVertex(DestSurface, Vx#, Vy#, Vz#, Vu#, Vv#, Vw#)
				VertexNormal(DestSurface, VertLoop+DestVerts, Vnx#, Vny#, Vnz#)
				VertexColor(DestSurface, VertLoop+DestVerts, Vr, Vg, Vb) 
	
			Next

			FreeEntity ThisPivot

		Else

			; Do the fast copy.
			; Fast copy can do offset and scaling, but not rotation.

			; Copy all the vertices from the source mesh to the destination surface.
			For VertLoop = 0 To SrcVerts-1

					
				Vx#  = VertexX#(SrcSurface, VertLoop)
				Vy#  = VertexY#(SrcSurface, VertLoop)
				Vz#  = VertexZ#(SrcSurface, VertLoop)
				Vu#  = VertexU#(SrcSurface, VertLoop)
				Vv#  = VertexV#(SrcSurface, VertLoop)		
				Vw#  = VertexW#(SrcSurface, VertLoop)
				Vnx# = VertexNX#(SrcSurface, VertLoop)
				Vny# = VertexNY#(SrcSurface, VertLoop)
				Vnz# = VertexNZ#(SrcSurface, VertLoop)						
				Vr   = VertexRed(SrcSurface, VertLoop)
				Vg   = VertexGreen(SrcSurface, VertLoop)
				Vb   = VertexBlue(SrcSurface, VertLoop)
		
				AddVertex(DestSurface, (Vx#*Vscalex#)+Voffsetx#, (Vy#*Vscaley#)+Voffsety#, (Vz#*Vscalez#)+Voffsetz#, Vu#, Vv#, Vw#)
				VertexNormal(DestSurface, VertLoop+DestVerts, Vnx#, Vny#, Vnz#)
				VertexColor(DestSurface, VertLoop+DestVerts, Vr, Vg, Vb) 
	
			Next

		EndIf


		; Copy all triangles from the source surface to the destination surface.	
		SrcTris  = CountTriangles(SrcSurface)
		For TriLoop = 0 To SrcTris-1
	
			V0 = TriangleVertex(SrcSurface, TriLoop, 0)
			V1 = TriangleVertex(SrcSurface, TriLoop, 1)
			V2 = TriangleVertex(SrcSurface, TriLoop, 2)
		
			AddTriangle(DestSurface, V0+DestVerts, V1+DestVerts, V2+DestVerts)
	
		Next
		
	EndIf

			
End Function
