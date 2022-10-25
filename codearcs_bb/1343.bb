; ID: 1343
; Author: RifRaf
; Date: 2005-04-05 08:21:03
; Title: ClusterizeMesh Revisited
; Description: break up large meshes

Type remove_surf
	Field id
	End Type

Type Cluster
	Field XC#, YC#, ZC#
	Field Mesh, Surf[200]
	End Type

Function ChunkTerrain(mesh,xsize=50,ysize=1,zsize=50)
	Delete Each Cluster
    Delete Each Remove_surf 
	; First we'll need to get the original terrain scale for matching scale after the chunking
    vx# = GetMatElement#(Mesh, 0, 0)
	vy# = GetMatElement#(Mesh, 0, 1)
	vz# = GetMatElement#(Mesh, 0, 2)
	XScale# = Sqr(vx# * vx# + vy# * vy# + vz# * vz#)
	vx# = GetMatElement#(Mesh, 1, 0)
	vy# = GetMatElement#(Mesh, 1, 1)
	vz# = GetMatElement#(Mesh, 1, 2)
	YScale# = Sqr(vx# * vx# + vy# * vy# + vz# * vz#)
	vx# = GetMatElement#(Mesh, 2, 0)
	vy# = GetMatElement#(Mesh, 2, 1)
	vz# = GetMatElement#(Mesh, 2, 2)
	ZScale# = Sqr(vx# * vx# + vy# * vy# + vz# * vz#)

	cx# = Int((MeshWidth#(Mesh)) / xsize)
	cy# = Int((MeshHeight#(Mesh)) / ysize)
	cz# = Int((MeshDepth#(Mesh)) / zsize)

	; Let the chunking begin
	sos = CountSurfaces(mesh)
	For s = 1 To sos
		surf = GetSurface(mesh, s)
		brush = GetSurfaceBrush(surf)

		For t = 0 To CountTriangles(surf) - 1
			x0#  = VertexX#(surf, TriangleVertex(surf, t, 0))
			y0#  = VertexY#(surf, TriangleVertex(surf, t, 0))
			z0#  = VertexZ#(surf, TriangleVertex(surf, t, 0))
			nx0# = VertexNX#(surf, TriangleVertex(surf, t, 0))
			ny0# = VertexNY#(surf, TriangleVertex(surf, t, 0))
			nz0# = VertexNZ#(surf, TriangleVertex(surf, t, 0))
			al0# = VertexAlpha#(surf, TriangleVertex(surf, t, 0))
			cr0# = VertexRed#(surf, TriangleVertex(surf, t, 0))
			cg0# = VertexGreen#(surf, TriangleVertex(surf, t, 0))
			cb0# = VertexBlue#(surf, TriangleVertex(surf, t, 0))
			x1#  = VertexX#(surf, TriangleVertex(surf, t, 1))
			y1#  = VertexY#(surf, TriangleVertex(surf, t, 1))
			z1#  = VertexZ#(surf, TriangleVertex(surf, t, 1))
			nx1# = VertexNX#(surf, TriangleVertex(surf, t, 1))
			ny1# = VertexNY#(surf, TriangleVertex(surf, t, 1))
			nz1# = VertexNZ#(surf, TriangleVertex(surf, t, 1))
			al1# = VertexAlpha#(surf, TriangleVertex(surf, t, 1))
			cr1# = VertexRed#(surf, TriangleVertex(surf, t, 1))
			cg1# = VertexGreen#(surf, TriangleVertex(surf, t, 1))
			cb1# = VertexBlue#(surf, TriangleVertex(surf, t, 1))
			x2#  = VertexX#(surf, TriangleVertex(surf, t, 2))
			y2#  = VertexY#(surf, TriangleVertex(surf, t, 2))
			z2#  = VertexZ#(surf, TriangleVertex(surf, t, 2))
			u0a# = VertexU#(surf, TriangleVertex(surf, t, 0), 0)
			v0a# = VertexV#(surf, TriangleVertex(surf, t, 0), 0)
			u1a# = VertexU#(surf, TriangleVertex(surf, t, 1), 0)
			v1a# = VertexV#(surf, TriangleVertex(surf, t, 1), 0)
			u2a# = VertexU#(surf, TriangleVertex(surf, t, 2), 0)
			v2a# = VertexV#(surf, TriangleVertex(surf, t, 2), 0)
			nx2# = VertexNX#(surf, TriangleVertex(surf, t, 2))
			ny2# = VertexNY#(surf, TriangleVertex(surf, t, 2))
			nz2# = VertexNZ#(surf, TriangleVertex(surf, t, 2))
			al2# = VertexAlpha#(surf, TriangleVertex(surf, t, 2))
			cr2# = VertexRed#(surf, TriangleVertex(surf, t, 2))
			cg2# = VertexGreen#(surf, TriangleVertex(surf, t, 2))
			cb2# = VertexBlue#(surf, TriangleVertex(surf, t, 2))

			; Let's see which chunk we'll assign this vert to
			x_c# = NearestPower(VertexX#(surf, TriangleVertex(surf, t, 0)), CX)
			y_c# = NearestPower(VertexY#(surf, TriangleVertex(surf, t, 0)), CY)
			z_c# = NearestPower(VertexZ#(surf, TriangleVertex(surf, t, 0)), CZ)
			Found = False
			For cl.cluster = Each cluster
				If x_c = cl\xc And y_c = cl\yc And z_c = cl\zc
					If cl\surf[s] <> 0
						Found = True
						v0 = AddVertex(cl\surf[s], x0, y0, z0)
						VertexTexCoords cl\surf[s], v0, u0a, v0a, 0
						VertexColor cl\surf[s], v0, cr0, cg0, cb0, al0
						VertexNormal cl\surf[s], v0, nx0, ny0, nz0
						v1 = AddVertex(cl\surf[s], x1, y1, z1)
						VertexTexCoords cl\surf[s], v1, u1a, v1a, 0
						VertexColor cl\surf[s], v1, cr1, cg1, cb1, al1
						VertexNormal cl\surf[s], v1, nx1, ny1, nz1
						v2 = AddVertex(cl\surf[s], x2, y2, z2)
						VertexTexCoords cl\surf[s], v2, u2a, v2a, 0
						VertexColor cl\surf[s], v2, cr2, cg2, cb2, al2
						VertexNormal cl\surf[s], v2, nx2, ny2, nz2
						nope = AddTriangle(cl\surf[s], v0, v1, v2)
						Exit
					EndIf
				EndIf
			Next

			; If there was no chunk for that area, we'll make it here
			If Found = False
				cl.cluster = New cluster
				nsegs = nsegs + 1
				cl\xc# = x_c
				cl\yc# = y_c
				cl\zc# = z_c
				cl\mesh = CreateMesh()
				For ss = 1 To sos
					cl\surf[ss] = CreateSurface(cl\mesh)
					surf2 = GetSurface(mesh, ss) 
					brush = GetSurfaceBrush(surf2)
                    PaintSurface cl\surf[ss], brush
				Next
				v0 = AddVertex(cl\surf[s], x0, y0, z0)
				VertexTexCoords cl\surf[s], v0, u0a, v0a, 0
				VertexColor cl\surf[s], v0, cr0, cg0, cb0, al0
				VertexNormal cl\surf[s], v0, nx0, ny0, nz0
				v1 = AddVertex(cl\surf[s], x1, y1, z1)
				VertexTexCoords cl\surf[s], v1, u1a, v1a, 0
				VertexColor cl\surf[s], v1, cr1, cg1, cb1, al1
				VertexNormal cl\surf[s], v1, nx1, ny1, nz1
				v2 = AddVertex(cl\surf[s], x2, y2, z2)
				VertexTexCoords cl\surf[s], v2, u2a, v2a, 0
				VertexColor cl\surf[s], v2, cr2, cg2, cb2, al2
				VertexNormal cl\surf[s], v2, nx2, ny2, nz2
				nope = AddTriangle(cl\surf[s], v0, v1, v2)
			EndIf
		Next
	Next

;remove blank surfaces from chunk
	For cl.Cluster = Each Cluster
        For rems.remove_surf=Each remove_surf
             Delete rems
               Next
	    For scn=1 To CountSurfaces(cl\mesh)
            sf=GetSurface(cl\mesh,scn)
 			vn=CountVertices(sf)-1
            If vn=<0 Then 
               rems.Remove_surf=New remove_surf
               rems\id=scn	
               EndIf
               Next
            fb=fb*-1
            cl\mesh=removesurface(cl\mesh)
            EntityPickMode cl\mesh,2,True
			Next


;free the original large mesh         
	FreeEntity Mesh

End Function


Function NearestPower(N#, Snapper#)

	Return Float#(Int(Abs(N#) / Snapper#)) * Snapper# * Sgn(N#)

End Function

Function uPdate_clusters(ent,maxdist#=500)
For C.CLUSTER=Each CLUSTER
				dist# = chunk_distance(ent,c\mesh)
				If dist>maxdist# Then 
 				  HideEntity c\mesh 
                 Else
                   ShowEntity c\mesh
                  EndIf 
				Next
				End Function
				

Function Chunk_distance(entity1,entity2)
	s=GetSurface(entity2,1)
	Return Sqr#((EntityX#(entity1,1) - VertexX#(s,1))^2 + (EntityZ#(entity1,1) - VertexZ(s,1))^2)
    End Function

Function removesurface(ent)
DebugLog "testing removal"
;ok we need to rebuild the mesh
DebugLog "creating mesh target"
 newmesh=CreateMesh()
 ns=CountSurfaces(ent)
DebugLog ns+" original surfaces"

For i=1 To ns
nogo=0
For rems.remove_surf=Each remove_surf
  If i=rems\id Then nogo=1
  Next
 If nogo=0  Then
    DebugLog "making surf"
 	  surf=GetSurface(ent,i)	
	  newsurf=CreateSurface(newmesh)
  	  brush = GetSurfaceBrush(surf)

		tc=CountTriangles(surf)
             For tri=0 To tc-1
			    v_r1#=VertexRed(surf,TriangleVertex(Surf,tri,0) )
			    v_g1#=VertexGreen(surf,TriangleVertex(Surf,tri,0)) 
			    v_b1#=VertexBlue(surf,TriangleVertex(Surf,tri,0) )
			    v_r2#=VertexRed(surf,TriangleVertex(Surf,tri,1) )
			    v_g2#=VertexGreen(surf,TriangleVertex(Surf,tri,1)) 
			    v_b2#=VertexBlue(surf,TriangleVertex(Surf,tri,1) )
			    v_r3#=VertexRed(surf,TriangleVertex(Surf,tri,2) )
			    v_g3#=VertexGreen(surf,TriangleVertex(Surf,tri,2)) 
			    v_b3#=VertexBlue(surf,TriangleVertex(Surf,tri,2) )

			    v_x0#=VertexX(surf,TriangleVertex(surf,tri,0))
			    v_x1#=VertexX(surf,TriangleVertex(surf,tri,1))
			    v_x2#=VertexX(surf,TriangleVertex(surf,tri,2))

			    v_y0#=VertexY(surf,TriangleVertex(surf,tri,0))
			    v_y1#=VertexY(surf,TriangleVertex(surf,tri,1))
			    v_y2#=VertexY(surf,TriangleVertex(surf,tri,2))

			    v_z0#=VertexZ(surf,TriangleVertex(surf,tri,0))
			    v_z1#=VertexZ(surf,TriangleVertex(surf,tri,1))
			    v_z2#=VertexZ(surf,TriangleVertex(surf,tri,2))

			    v_u0#=VertexU(surf,TriangleVertex(surf,tri,0))
			    v_u1#=VertexU(surf,TriangleVertex(surf,tri,1))
			    v_u2#=VertexU(surf,TriangleVertex(surf,tri,2))

			    v_v0#=VertexV(surf,TriangleVertex(surf,tri,0))
			    v_v1#=VertexV(surf,TriangleVertex(surf,tri,1))
			    v_v2#=VertexV(surf,TriangleVertex(surf,tri,2))

			    v_a0#=VertexAlpha(surf,TriangleVertex(surf,tri,0))
 		        v_a1#=VertexAlpha(surf,TriangleVertex(surf,tri,1))
			    v_a2#=VertexAlpha(surf,TriangleVertex(surf,tri,2))

                 v0=AddVertex(newsurf,v_x0,v_y0,v_z0,v_u0,v_v0)
			     v1=AddVertex(newsurf,v_x1,v_y1,v_z1,v_u1,v_v1)
			     v2=AddVertex(newsurf,v_x2,v_y2,v_z2,v_u2,v_v2)
			     AddTriangle(newsurf,v0,v1,v2)

			    VertexColor newsurf, v0,v_r1,v_g1,v_b1,v_a0
			    VertexColor newsurf, v1,v_r2,v_g2,v_b2,v_a1
			    VertexColor newsurf, v2,v_r3,v_g3,v_b3,v_a2
              Next 
             PaintSurface newsurf,brush
             UpdateNormals newmesh
       EndIf 
       Next
;free the old mesh
FreeEntity ent
;return the updated mesh
Return newmesh
End Function
