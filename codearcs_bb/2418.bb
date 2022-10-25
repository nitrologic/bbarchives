; ID: 2418
; Author: Warner
; Date: 2009-02-22 13:00:46
; Title: CSG routines
; Description: csg routines

;Example usage code at the bottom, search for EXAMPLE99.BB
;rev 1

;esc 				end
;x 					turn
;right-mouse 		lookaround
;arrow/a+z (+ctrl) 	move (cam)
;O 					texture
;P 					wire
;Space 				CSG
;WER 				shape


;-----------------------------------------------------------------------------------------------------
;-----------------------------------------------------------------------------------------------------
;											
;-----------------------------------------------------------------------------------------------------
;-----------------------------------------------------------------------------------------------------

	;ext. function ret. values
	Dim npicked#(2)
	Dim tpicked#(2)

;-----------------------------------------------------------------------------------------------------
;-----------------------------------------------------------------------------------------------------
;											CSG99.BB
;-----------------------------------------------------------------------------------------------------
;-----------------------------------------------------------------------------------------------------
	
	;constants
	Const CSG_CARVE = 0
	Const CSG_FILL = 1
	
	;types
	Type CSGTriangle
		;mesh ref.
		Field mesh
		Field surf
		;vertex coords
		Field x0#,y0#,z0#,u0#,v0#
		Field x1#,y1#,z1#,u1#,v1#
		Field x2#,y2#,z2#,u2#,v2#
		;plane normal/d
		Field nx#,ny#,nz#
		Field onx#,ony#,onz#
		Field d#
		;flags
		Field loop
	End Type

;-----------------------------------------------------------------------------------------------------
;												MakeTriangle()
;-----------------------------------------------------------------------------------------------------
Function MakeTriangle.CSGTriangle(mesh, surf, x0#,y0#,z0#,u0#,v0#, x1#,y1#,z1#,u1#,v1#, x2#,y2#,z2#,u2#,v2#, loop, org.CSGTriangle, nx#=0,ny#=0,nz#=0,d#=0 )

	;distance between corners
	d1# = dist(x0,y0,z0, x1,y1,z1)
	d2# = dist(x1,y1,z1, x2,y2,z2)
	d3# = dist(x2,y2,z2, x0,y0,z0)
			
	;size of triangle
	opp# = PointDistanceToLine#	(x0,y0,z0,x1,y1,z1,x2,y2,z2)
	
	;remove empty triangles
	ep# = 0.01
	If ((opp < ep) Or (d1<ep) Or (d2<ep) Or (d3<ep)) Then Return

	t.CSGTriangle = New CSGTriangle

	;store mesh/surf pointer	
	t\mesh = mesh
	t\surf = surf

	;store coords		
	t\x0 = x0
	t\y0 = y0
	t\z0 = z0
	t\x1 = x1
	t\y1 = y1
	t\z1 = z1
	t\x2 = x2
	t\y2 = y2
	t\z2 = z2
	
	t\u0 = u0
	t\v0 = v0
	t\u1 = u1
	t\v1 = v1
	t\u2 = u2
	t\v2 = v2
	
	t\loop = loop

	;calculate normal
	ax# = x1 - x0
	ay# = y1 - y0
	az# = z1 - z0
	bx# = x2 - x1
	by# = y2 - y1
	bz# = z2 - z1
	Nx# = ( ay * bz ) - ( az * by )
	Ny# = ( az * bx ) - ( ax * bz )
	Nz# = ( ax * by ) - ( ay * bx )
	Ns# = Sqr( Nx*Nx + Ny*Ny + Nz*Nz )
	If Ns > 0 Then
		Nx = Nx / Ns
		Ny = Ny / Ns
		Nz = Nz / Ns
	End If
	
	If org <> Null Then
		t\onx = org\onx
		t\ony = org\ony
		t\onz = org\onz
;		t\d  = org\d
	Else
		t\onx = nx
		t\ony = ny
		t\onz = nz
	End If
		t\nx = nx
		t\ny = ny
		t\nz = nz
		t\d = d
		
	t\d# = -((t\nx * x0) + (t\ny * y0) + (t\nz * z0))
		
	Return t

End Function

;-----------------------------------------------------------------------------------------------------
;												  ScanObject()
;-----------------------------------------------------------------------------------------------------
;read all triangles from object and store them into the CSGTriangle type
Function ScanObject(mesh)

	;scan each surface
	For si = 1 To CountSurfaces(mesh)

		;get surface	
		s = GetSurface(mesh, si)
		
		;scan each triangle
		For t = 0 To CountTriangles(s) - 1

			;get triangle vertices		
			v0 = TriangleVertex(s, t, 0)
			v1 = TriangleVertex(s, t, 1)
			v2 = TriangleVertex(s, t, 2)

			;get vertex coords			
			v0x# = VertexX(s, v0)
			v0y# = VertexY(s, v0)
			v0z# = VertexZ(s, v0)
			TFormPoint v0x, v0y, v0z, mesh, 0
			v0x# = TFormedX()
			v0y# = TFormedY()
			v0z# = TFormedZ()

			v1x# = VertexX(s, v1)
			v1y# = VertexY(s, v1)
			v1z# = VertexZ(s, v1)
			TFormPoint v1x, v1y, v1z, mesh, 0
			v1x# = TFormedX()
			v1y# = TFormedY()
			v1z# = TFormedZ()

			v2x# = VertexX(s, v2)
			v2y# = VertexY(s, v2)
			v2z# = VertexZ(s, v2)
			TFormPoint v2x, v2y, v2z, mesh, 0
			v2x# = TFormedX()
			v2y# = TFormedY()
			v2z# = TFormedZ()

			;get triangle uv coords		
			uu0# = VertexU(s, v0)
			vv0# = VertexV(s, v0)
			uu1# = VertexU(s, v1)
			vv1# = VertexV(s, v1)
			uu2# = VertexU(s, v2)
			vv2# = VertexV(s, v2)

			;create triangle
			tr.CSGTriangle = MakeTriangle(mesh, s, v0x,v0y,v0z, uu0,vv0, v1x,v1y,v1z, uu1,vv1, v2x,v2y,v2z, uu2, vv2, 0, Null)
			
		Next
		
	Next
		
End Function


;-----------------------------------------------------------------------------------------------------
;												SplitTriangle()
;-----------------------------------------------------------------------------------------------------
;split triangle t1 by the plane of triangle t2, optional 'dosplit'=actually do split, loop=marker flag
Function SplitTriangle(t1.CSGTriangle, t2.CSGTriangle, dosplit = 0, loop = -1)

	;temp arrays
	Local stest[3]
	Local ssx#[3]
	Local ssy#[3]
	Local ssz#[3]
	Local ssu#[3]
	Local ssv#[3]

	nx# = t2\nx#
	ny# = t2\ny#
	nz# = t2\nz#
	d# = t2\d#

	;get world space coords t1
	v0x# = t1\x0: v0y# = t1\y0: v0z# = t1\z0
	v1x# = t1\x1: v1y# = t1\y1: v1z# = t1\z1
	v2x# = t1\x2: v2y# = t1\y2: v2z# = t1\z2

	;calculate plane intersection		
	stest[0] = ray_plane(v0x,v0y,v0z, v1x,v1y,v1z, nx,ny,nz,d)
	ssx[0] = npicked(0)
	ssy[0] = npicked(1)
	ssz[0] = npicked(2)
	stest[1] = ray_plane(v1x,v1y,v1z, v2x,v2y,v2z, nx,ny,nz,d)
	ssx[1] = npicked(0)
	ssy[1] = npicked(1)
	ssz[1] = npicked(2)
	stest[2] = ray_plane(v2x,v2y,v2z, v0x,v0y,v0z, nx,ny,nz,d)
	ssx[2] = npicked(0)
	ssy[2] = npicked(1)
	ssz[2] = npicked(2)
	
	;get triangle uv coords
	u0# = t1\u0
	v0# = t1\v0
	u1# = t1\u1
	v1# = t1\v1
	u2# = t1\u2
	v2# = t1\v2

	;get picked uv coords
	d1# = dist(v0x,v0y,v0z, ssx[0],ssy[0],ssz[0]) ;distance from side 1
	d2# = dist(ssx[0],ssy[0],ssz[0],v1x,v1y,v1z)  ;distance from side 2
	dd# = d1 + d2								  ;total distance
	If dd = 0 Then
		ssu[0] = u0
		ssv[0] = v0
	Else
		ssu[0] = u0 + (u1-u0) * d1 / dd			  ;interpolate u
		ssv[0] = v0 + (v1-v0) * d1 / dd		      ;interpolate v
	End If

	d1# = dist(v1x,v1y,v1z, ssx[1],ssy[1],ssz[1]) ;distance from side 1
	d2# = dist(ssx[1],ssy[1],ssz[1],v2x,v2y,v2z)  ;distance from side 2
	dd# = d1 + d2								  ;total distance
	If dd = 0 Then
		ssu[1] = u1
		ssv[1] = v1
	Else
		ssu[1] = u1 + (u2-u1) * d1 / dd			  ;interpolate u
		ssv[1] = v1 + (v2-v1) * d1 / dd	          ;interpolate v
	End If

	d1# = dist(v2x,v2y,v2z, ssx[2],ssy[2],ssz[2]) ;distance from side 1
	d2# = dist(ssx[2],ssy[2],ssz[2],v0x,v0y,v0z)  ;distance from side 2
	dd# = d1 + d2								  ;total distance
	If dd = 0 Then
		ssu[2] = u2
		ssv[2] = v2
	Else
		ssu[2] = u2 + (u0-u2) * d1 / dd			  ;interpolate u
		ssv[2] = v2 + (v0-v2) * d1 / dd			  ;interpolate v
	End If
				
	;all edges (which is a strange situation..)
	If stest[0] And stest[1] And stest[2] Then 
		split = False
		If dosplit Then
		    ;determine which corner should be dropped
			;based on the distance  corner<->intersection point
			d01# = dist(ssx[0],ssy[0],ssz[0], v0x,v0y,v0z)
			d02# = dist(ssx[1],ssy[1],ssz[1], v0x,v0y,v0z)
			d11# = dist(ssx[1],ssy[1],ssz[1], v1x,v1y,v1z)
			d12# = dist(ssx[2],ssy[2],ssz[2], v1x,v1y,v1z)
			d21# = dist(ssx[2],ssy[2],ssz[2], v2x,v2y,v2z)
			d22# = dist(ssx[0],ssy[0],ssz[0], v2x,v2y,v2z)
			If d02<d01 Then d01=d02
			If d12<d11 Then d11=d12
			If d22<d21 Then d21=d22
			If (d01 > d11) And (d01 > d21) Then stest[0] = False
			If (d11 > d01) And (d11 > d21) Then stest[1] = False
			If (d21 > d01) And (d21 > d11) Then stest[2] = False
		End If
	End If
	
	;edge 0
	If stest[0] And stest[1] Then
		split = True
		If dosplit Then
			MakeTriangle(t1\mesh, t1\surf, v0x,v0y,v0z,u0,v0, ssx[0],ssy[0],ssz[0],ssu[0],ssv[0], ssx[1],ssy[1],ssz[1],ssu[1],ssv[1], loop, t1)
			MakeTriangle(t1\mesh, t1\surf, v0x,v0y,v0z,u0,v0, ssx[1],ssy[1],ssz[1],ssu[1],ssv[1], v2x, v2y, v2z,u2,v2, loop, t1)
			MakeTriangle(t1\mesh, t1\surf, ssx[0],ssy[0],ssz[0],ssu[0],ssv[0], v1x,v1y,v1z,u1,v1, ssx[1],ssy[1], ssz[1],ssu[1], ssv[1], loop, t1)
		End If
	End If
	;edge 1
	If stest[1] And stest[2] Then
		split = True
		If dosplit Then
			MakeTriangle(t1\mesh, t1\surf,  v0x,v0y,v0z,u0,v0, v1x,v1y,v1z,u1,v1, ssx[2],ssy[2],ssz[2],ssu[2],ssv[2], loop, t1)
			MakeTriangle(t1\mesh, t1\surf,  ssx[2],ssy[2],ssz[2],ssu[2],ssv[2], v1x,v1y,v1z,u1,v1, ssx[1],ssy[1], ssz[1],ssu[1],ssv[1], loop, t1)
			MakeTriangle(t1\mesh, t1\surf,  ssx[2],ssy[2],ssz[2],ssu[2],ssv[2], ssx[1],ssy[1],ssz[1],ssu[1],ssv[1], v2x,v2y,v2z,u2,v2, loop, t1)
		End If
	End If
	;edge 2
	If stest[2] And stest[0] Then
		split = True
		If dosplit Then
			MakeTriangle(t1\mesh, t1\surf,  v0x,v0y,v0z,u0,v0, ssx[0],ssy[0],ssz[0],ssu[0],ssv[0], ssx[2],ssy[2],ssz[2],ssu[2],ssv[2], loop, t1)
			MakeTriangle(t1\mesh, t1\surf,  ssx[0],ssy[0],ssz[0],ssu[0],ssv[0], v1x,v1y,v1z,u1,v1, ssx[2],ssy[2],ssz[2],ssu[2],ssv[2], loop, t1)
			MakeTriangle(t1\mesh, t1\surf,  ssx[2],ssy[2],ssz[2],ssu[2],ssv[2], v1x,v1y,v1z,u1,v1, v2x,v2y,v2z,u2,v2, loop, t1)
		End If
	End If
	;only corner 0 (which is also very weird)
	If stest[0] And (Not stest[1]) And (Not stest[2]) Then
		split = True
		If dosplit Then
			MakeTriangle(t1\mesh, t1\surf,  v0x,v0y,v0z,u0,v0, ssx[0],ssy[0],ssz[0],ssu[0],ssv[0], v2x,v2y,v2z,u2,v2, loop, t1)
			MakeTriangle(t1\mesh, t1\surf,  ssx[0],ssy[0],ssz[0],ssu[0],ssv[0], v1x,v1y,v1z,u1,v1, v2x,v2y,v2z,u2,v2, loop, t1)
		End If
	End If
	;only corner 1 (idem)
	If stest[1] And (Not stest[2]) And (Not stest[0]) Then
		split = True
		If dosplit Then
			MakeTriangle(t1\mesh, t1\surf,  v1x,v1y,v1z,u1,v1, ssx[1],ssy[1],ssz[1],ssu[1],ssv[1], v0x,v0y,v0z,u0,v0, loop, t1)
			MakeTriangle(t1\mesh, t1\surf,  ssx[1],ssy[1],ssz[1],ssu[1],ssv[1], v2x,v2y,v2z,u2,v2, v0x,v0y,v0z,u0,v0, loop, t1)
		End If
	End If
	;only corner 2 (idem)
	If stest[2] And (Not stest[0]) And (Not stest[1]) Then
		split = True
		If dosplit Then
			MakeTriangle(t1\mesh, t1\surf,  v2x,v2y,v2z,u2,v2, ssx[2],ssy[2],ssz[2],ssu[2],ssv[2], v1x,v1y,v1z,u1,v1, loop, t1)
			MakeTriangle(t1\mesh, t1\surf,  ssx[2],ssy[2],ssz[2],ssu[2],ssv[2], v0x,v0y,v0z,u0,v0, v1x,v1y,v1z,u1,v1, loop, t1)
		End If	
	End If
	;none
	If Not(stest[0] Or stest[1] Or stest[2]) Then
		split = False
	End If
			
	Return split
	
End Function

;-----------------------------------------------------------------------------------------------------
;												SplitTriangles()
;-----------------------------------------------------------------------------------------------------
Function SplitTriangles(obj1)

	;reset 'loop' flag for all triangles
	For t1.CSGTriangle = Each CSGTriangle
		t1\loop = 0
	Next

	loop = 0
	;loop through each triangle
	For t2.CSGTriangle = Each CSGTriangle
		
		;if triangle doesn't belong to mesh, start testing	
		If (t2\mesh <> obj1) Then
		
			loop = loop + 1
					
			;loop through each triangles
			For t1.CSGTriangle = Each CSGTriangle
			
				;test if triangle belongs to mesh, and isn't bisected yet
				If (t1\mesh = obj1) And (t1\loop < loop) Then
				
					;if tr1 and tr2 intersect
					If CSGTrisIntersect(t1, t2) Then
										
						;test if triangles intersect each other
						test1 = SplitTriangle(t1, t2, False, loop)
						test2 = SplitTriangle(t2, t1, False, loop)
							
						;if so, bisect t1 by t2
						If test1 And test2 Then
							SplitTriangle(t1, t2, True, loop)
							;remove original triangle
							Delete t1
						End If
						
					End If
					
				End If
			
			Next
		
		End If
		
	Next

End Function

;-----------------------------------------------------------------------------------------------------
;												CSGTrisIntersect()
;-----------------------------------------------------------------------------------------------------
;quickly checks if two triangles intersect/overlap
Function CSGTrisIntersect(t1.CSGTriangle,t2.CSGTriangle)

 ;get coords t1
 x0# = t1\x0: y0# = t1\y0: z0# = t1\z0
 x1# = t1\x1: y1# = t1\y1: z1# = t1\z1
 x2# = t1\x2: y2# = t1\y2: z2# = t1\z2

 ;get coords t2
 tx0# = t2\x0: ty0# = t2\y0: tz0# = t2\z0
 tx1# = t2\x1: ty1# = t2\y1: tz1# = t2\z1
 tx2# = t2\x2: ty2# = t2\y2: tz2# = t2\z2


 ;determine min/max x/y/z values
 nx1# = findmin(x0,x1,x2)
 nx2# = findmax(x0,x1,x2)
 ny1# = findmin(y0,y1,y2)
 ny2# = findmax(y0,y1,y2)
 nz1# = findmin(z0,z1,z2)
 nz2# = findmax(z0,z1,z2)

 ;width/height/depth	
 nx2# = nx2 - nx1: If nx2 < 0.01 Then nx2 = 0.01
 ny2# = ny2 - ny1: If ny2 < 0.01 Then ny2 = 0.01
 nz2# = nz2 - nz1: If nz2 < 0.01 Then nz2 = 0.01

 ;determine min/max x/y/z values
 tnx1# = findmin(tx0,tx1,tx2)
 tnx2# = findmax(tx0,tx1,tx2)
 tny1# = findmin(ty0,ty1,ty2)
 tny2# = findmax(ty0,ty1,ty2)
 tnz1# = findmin(tz0,tz1,tz2)
 tnz2# = findmax(tz0,tz1,tz2)

 ;with/height/depth	
 tnx2# = tnx2 - tnx1: If tnx2 < 0.01 Then tnx2 = 0.01
 tny2# = tny2 - tny1: If tny2 < 0.01 Then tny2 = 0.01
 tnz2# = tnz2 - tnz1: If tnz2 < 0.01 Then tnz2 = 0.01

 Return BoxesOverlap(nx1#,ny1#,nz1#,nx2#,ny2#,nz2#,tnx1#,tny1#,tnz1#,tnx2#,tny2#,tnz2#)

End Function


;-----------------------------------------------------------------------------------------------------
;												RebuildMesh()
;-----------------------------------------------------------------------------------------------------
Function RebuildMesh(mesh, invert = False, keepshared = False, mesh2)
		
	;setup dummy mesh, used for picking
	dummy = CopyMeshAt(mesh2)
	
	;enable picking on dummy object	
	dummy2 = CopyMesh(dummy)
	FlipMesh dummy2
	EntityPickMode dummy, 2
	EntityPickMode dummy2, 2
	
	;loop through each surface
	For si = 1 To CountSurfaces(mesh)
	
		;get and clear surface
		surf = GetSurface(mesh, si)
		ClearSurface surf, True, True
	
		;rebuild triangles that belong to this surface
		For c.CSGTriangle = Each CSGTriangle

			If c\surf = surf Then

				;convert vertex world coords into mesh coords
				TFormPoint c\x0,c\y0,c\z0, 0, mesh
				x0# = TFormedX()
				y0# = TFormedY()
				z0# = TFormedZ()

				TFormPoint c\x1,c\y1,c\z1, 0, mesh
				x1# = TFormedX()
				y1# = TFormedY()
				z1# = TFormedZ()

				TFormPoint c\x2,c\y2,c\z2, 0, mesh
				x2# = TFormedX()
				y2# = TFormedY()
				z2# = TFormedZ()
			
				;distance between corners
				d1# = dist(x0,y0,z0, x1,y1,z1)
				d2# = dist(x1,y1,z1, x2,y2,z2)
				d3# = dist(x2,y2,z2, x0,y0,z0)
			
				;size of triangle
				opp# = PointDistanceToLine#	(x0,y0,z0,x1,y1,z1,x2,y2,z2)
	
				;remove empty triangles
				ep# = 0.0001
				If Not((opp < ep) Or (d1<ep) Or (d2<ep) Or (d3<ep)) Then 

					;get middle point				
					mx# = (c\x0+c\x1+c\x2) / 3.0
					my# = (c\y0+c\y1+c\y2) / 3.0
					mz# = (c\z0+c\z1+c\z2) / 3.0
					
					;offset middle point slightly (don't ask why - tweak)
					;mx=mx+(c\nx*0.0001)
					;my=my+(c\ny*0.0001)
					;mz=mz+(c\nz*0.0001)
					
					;METHOD 1
					;linepick from middle in direction normal
					LinePick mx, my, mz, -c\nx * 1000, -c\ny * 1000, -c\nz * 1000
					finside = (PickedEntity() <> dummy2)
					time# = dist(mx,my,mz,PickedX(),PickedY(),PickedZ())
										
					;;METHOD 2
					;d1# = Ray_Intersect_Mesh_Max(dummy, mx, my, mz, -c\nx, -c\ny, -c\nz, True, True, False)
					;d2# = Ray_Intersect_Mesh_Max(dummy, mx, my, mz, -c\nx, -c\ny, -c\nz, True, True, True)
					;finside = (d1 <= d2)
					;If finside Then time# = d1 Else time# = d2
					
					;test for shared edges (they are not detected as being inside the dummy mesh)					
					fshared = (time < 0.001) And finside
											
					;invert if requested
					If invert Then finside = Not(finside)

					If fshared Then
						finside = keepshared
					End If
					
					;if picked the inside of the other shape
					If finside Then

						;actual add triangle to surface
						v0 = AddVertex(surf, x0, y0, z0, c\u0, c\v0)
						v1 = AddVertex(surf, x1, y1, z1, c\u1, c\v1)
						v2 = AddVertex(surf, x2, y2, z2, c\u2, c\v2)
						VertexNormal surf, v0, c\nx, c\ny, c\nz
						VertexNormal surf, v1, c\nx, c\ny, c\nz
						VertexNormal surf, v2, c\nx, c\ny, c\nz						
						AddTriangle surf, v0, v1, v2
						
					End If

					face = face + 1
				
				End If
				
			End If
		
		Next
		
	Next
	
	FreeEntity dummy
	FreeEntity dummy2
				
End Function

;-----------------------------------------------------------------------------------------------------
;													CSG()
;-----------------------------------------------------------------------------------------------------
;perform all actions in order to achieve csg
Function CSG(m1, m2, method = CSG_FILL)

	;part A
	mesh1 = CopyMeshAt(m1)
	mesh2 = CopyMeshAt(m2)

	;scan triangles mesh1/mesh2	
	ScanObject(mesh1)
	ScanObject(mesh2)

	;split triangles mesh 1 vs other scanned triangles	
	SplitTriangles(mesh1)	
	SplitTriangles(mesh1)	

	;rebuild 1st mesh, and leave out triangles inside/outside 2nd mesh (dep. on method)
	RebuildMesh(mesh1, False, False, m2) ;mesh, invert, keepshared, other mesh
	
	;setup second mesh
	FreeEntity mesh2
	
	Delete Each CSGTriangle

	;setup partA	
	partA = mesh1
	
	;part B
	mesh1 = CopyMeshAt(m1)
	mesh2 = CopyMeshAt(m2)

	;scan triangles
	ScanObject(mesh1)
	ScanObject(mesh2)

	;split mesh2	
	SplitTriangles(mesh2)
	SplitTriangles(mesh2)
	
	;rebuild mesh2
	RebuildMesh(mesh2, method = 0, method = CSG_FILL, m1) ;mesh, invert, keepshared, other mesh
	FreeEntity mesh1
	
	Delete Each CSGTriangle

	;setup partB
	partB = mesh2
	If method = 0 Then FlipMesh partB

	;add partB to partA	
	AddMesh partB, partA
	FreeEntity partB
		
	mesh1 = partA
	
	Return mesh1

End Function

;-----------------------------------------------------------------------------------------------------
;												BoxesOverlap()
;-----------------------------------------------------------------------------------------------------
Function BoxesOverlap%(x0#, y0#, z0#, w0#, h0#, d0#, x2#, y2#, z2#, w2#, h2#, d2#)
	If (x0 > (x2 + w2)) Or ((x0 + w0) < x2) Then Return False
	If (y0 > (y2 + h2)) Or ((y0 + h0) < y2) Then Return False
	If (z0 > (z2 + d2)) Or ((z0 + d0) < z2) Then Return False
	Return True
End Function





;-----------------------------------------------------------------------------------------------------
;-----------------------------------------------------------------------------------------------------
;											MATH99.BB
;-----------------------------------------------------------------------------------------------------
;-----------------------------------------------------------------------------------------------------


;-----------------------------------------------------------------------------------------------------
;												dist#()
;-----------------------------------------------------------------------------------------------------
;returns distance between xyz and xyz2
Function dist#(x#,y#,z#,x2#,y2#,z2#)

	x2=x2-x
	y2=y2-y
	z2=z2-z
	Return Sqr(x2*x2+y2*y2+z2*z2)

End Function

;-----------------------------------------------------------------------------------------------------
;												PointDistToLine()
;-----------------------------------------------------------------------------------------------------
; ID: 1870
; Author: Danny
; Date: 2006-11-24 19:01:55
; Title: Point distance to a Line
; Description: Calculates the shortest distance between a point P and a line

Function PointDistanceToLine#( ax#,ay#,az#, bx#,by#,bz#, px#,py#,pz# )
;| Calculates the shortest distance between a point P(xyz) and a line segment defined by A(xyz) and B(xyz) - danny.

	;get the length of each side of the triangle ABP
	ab# = Sqr( (bx-ax)*(bx-ax) + (by-ay)*(by-ay) + (bz-az)*(bz-az) )
	bp# = Sqr( (px-bx)*(px-bx) + (py-by)*(py-by) + (pz-bz)*(pz-bz) )
	pa# = Sqr( (ax-px)*(ax-px) + (ay-py)*(ay-py) + (az-pz)*(az-pz) )

	;get the triangle's semiperimeter
	semi# = (ab+bp+pa) / 2.0
	
	;get the triangle's area
	area# = Sqr( semi * (semi-ab) * (semi-bp) * (semi-pa) )
	
	;return closest distance P to AB
	Return (2.0 * (area/ab))
	
End Function

;-------------------------------------------------------------------------------------------------------
;-------------------------------------------------------------------------------------------------------
; Author: sswift
;calculate ray-triangle intersection
Function Ray_Intersect_Triangle(Px#, Py#, Pz#, Dx#, Dy#, Dz#, V0x#, V0y#, V0z#, V1x#, V1y#, V1z#, V2x#, V2y#, V2z#, Extend_To_Infinity=True, Cull_Backfaces=False, Flip_Faces=False)
	
	Dx=Dx-Px
	Dy=Dy-Py
	Dz=Dz-Pz	
	; crossproduct(b,c) =
	; ax = (by * cz) - (cy * bz) 
	; ay = (bz * cx) - (cz * bx) 	
	; az = (bx * cy) - (cx * by)

	; dotproduct(v,q) =
	; (vx * qx) + (vy * qy) + (vz * qz)	
	; DP =  1 = Vectors point in same direction.          (  0 degrees of seperation)
	; DP =  0 = Vectors are perpendicular to one another. ( 90 degrees of seperation)
	; DP = -1 = Vectors point in opposite directions.     (180 degrees of seperation) 
	;
	; The dot product is also reffered to as "the determinant" or "the inner product"

	If (Flip_Faces) Then 
		tx# = v0x#
		ty# = v0y#
		tz# = v0z#
		v0x# = v2x#
		v0y# = v2y#
		v0z# = v2z#
		v2x# = tx#
		v2y# = ty#
		v2z# = tz#
	End If

	; Calculate the vector that represents the first side of the triangle.
	E1x# = V2x# - V0x#
	E1y# = V2y# - V0y#
	E1z# = V2z# - V0z#

	; Calculate the vector that represents the second side of the triangle.
	E2x# = V1x# - V0x#
	E2y# = V1y# - V0y#
	E2z# = V1z# - V0z#

	; Calculate a vector which is perpendicular to the vector between point 0 and point 1,
	; and the direction vector for the ray.
	; Hxyz = Crossproduct(Dxyz, E2xyz)
	Hx# = (Dy# * E2z#) - (E2y# * Dz#)
	Hy# = (Dz# * E2x#) - (E2z# * Dx#)
	Hz# = (Dx# * E2y#) - (E2x# * Dy#)

	; Calculate the dot product of the above vector and the vector between point 0 and point 2.
	A# = (E1x# * Hx#) + (E1y# * Hy#) + (E1z# * Hz#)

	; If we should ignore triangles the ray passes through the back side of,
	; and the ray points in the same direction as the normal of the plane,
	; then the ray passed through the back side of the plane,  
	; and the ray does not intersect the plane the triangle lies in.
	If (Cull_Backfaces = True) And (A# >= 0) Then Return False
		
	; If the ray is almost parralel to the plane,
	; then the ray does not intersect the plane the triangle lies in.
	If (A# > -0.00001) And (A# < 0.00001) Then Return False
	
	; Inverse Determinant. (Dot Product) 
	; (Scaling factor for UV's?)
	F# = 1.0 / A#

	; Calculate a vector between the starting point of our ray, and the first point of the triangle,
	; which is at UV(0,0)
	Sx# = Px# - V0x#
	Sy# = Py# - V0y#
	Sz# = Pz# - V0z#
	
	; Calculate the U coordinate of the intersection point.
	;
	;	Sxyz is the vector between the start of our ray and the first point of the triangle.
	;	Hxyz is the normal of our triangle.
	;	
	; U# = F# * (DotProduct(Sxyz, Hxyz))
	U# = F# * ((Sx# * Hx#) + (Sy# * Hy#) + (Sz# * Hz#))
	
	; Is the U coordinate outside the range of values inside the triangle?
	If (U# < 0.0) Or (U# > 1.0)

		; The ray has intersected the plane outside the triangle.
		Return False
	
	EndIf

	; Not sure what this is, but it's definitely NOT the intersection point.
	;
	;	Sxyz is the vector from the starting point of the ray to the first corner of the triangle.
	;	E1xyz is the vector which represents the first side of the triangle.
	;	The crossproduct of these two would be a vector which is perpendicular to both.
	;
	; Qxyz = CrossProduct(Sxyz, E1xyz)
	Qx# = (Sy# * E1z#) - (E1y# * Sz#)
	Qy# = (Sz# * E1x#) - (E1z# * Sx#)
	Qz# = (Sx# * E1y#) - (E1x# * Sy#)
	
	; Calculate the V coordinate of the intersection point.
	;	
	;	Dxyz is the vector which represents the direction the ray is pointing in.
	;	Qxyz is the intersection point I think?
	;
	; V# = F# * DotProduct(Dxyz, Qxyz)
	V# = F# * ((Dx# * Qx#) + (Dy# * Qy#) + (Dz# * Qz#))
	
	; Is the V coordinate outside the range of values inside the triangle?	
	; Does U+V exceed 1.0?  
	If (V# < 0.0) Or ((U# + V#) > 1.0)

		; The ray has intersected the plane outside the triangle.		
		Return False

		; The reason we check U+V is because if you imagine the triangle as half a square, U=1 V=1 would
		; be in the lower left hand corner which would be in the lower left triangle making up the square.
		; We are looking for the upper right triangle, and if you think about it, U+V will always be less
		; than or equal to 1.0 if the point is in the upper right of the triangle.

	EndIf

	; Calculate the distance of the intersection point from the starting point of the ray, Pxyz.
	; This distance is scaled so that at Pxyz, the start of the ray, T=0, and at Dxyz, the end of the ray, T=1.
	; If the intersection point is behind Pxyz, then T will be negative, and if the intersection point is
	; beyond Dxyz then T will be greater than 1. 
	T# = F# * ((E2x# * Qx#) + (E2y# * Qy#) + (E2z# * Qz#))

	; If the triangle is behind Pxyz, ignore this intersection.
	; We want a directional ray, which only intersects triangles in the direction it points.
	If (T# < 0) Then Return False

	; If the plane is beyond Dxyz, amd we do not want the ray to extend to infinity, then ignore this intersection.
	If (Extend_To_Infinity = False) And (T# > 1) Return False

	;-------------------------------------------------
	;Calculate intersection point
	nx#=(E1y*E2z)-(E1z*E2y)
	ny#=(E1z*E2x)-(E1x*E2z)
	nz#=(E1x*E2y)-(E1y*E2x)
	d# = -  nx*V0x - ny*V0y - nz*V0z 
	
	denom# = nx*Dx + ny*Dy + nz*Dz
	mu# = - (d + nx*Px + ny*Py + nz*Pz) / denom
	
	tpicked(0) = Px + mu * DX
	tpicked(1) = Py + mu * Dy
	tpicked(2) = Pz + mu * Dz
	;-------------------------------------------------

	; The ray intersects the triangle!		
	Return True

End Function

;elias_t
;calculate ray-plane intersection
Function ray_plane(p1x#,p1y#,p1z#, p2x#,p2y#,p2z#, nx#, ny#, nz#, d#)

	Local total#,denom#,mu#
	
	;Calculate the position on the Line that intersects the plane
	denom = nx * (p2x - p1x) + ny * (p2y - p1y) + nz * (p2z - p1z);
	
	If (Abs(denom) < 0.0001) Return 0;Line And plane don't intersect
	      
	mu = - (d + nx * p1x + ny * p1y + nz * p1z) / denom
	npicked(0) = (p1x + mu * (p2x - p1x))
	npicked(1) = (p1y + mu * (p2y - p1y))
	npicked(2) = (p1z + mu * (p2z - p1z))

	;comment this out if you want an infinite ray
	If (mu < 0) Or (mu > 1) Return 0;Intersection Not along Line segment
		      
	Return 1

End Function
	
;-----------------------------------------------------------------------------------------------------
;												CopyMeshAt()
;-----------------------------------------------------------------------------------------------------
;copy mesh and put new entity on original entities position/rotation
;could use GlobalEntityPitch and EntityScale from archive
Function CopyMeshAt(mesh)

	mesh2 = CopyMesh(mesh)
	ScaleMesh mesh2, EntityWidth(mesh), EntityHeight(mesh), EntityDepth(mesh)
	RotateMesh mesh2, GlobalEntityPitch(mesh), GlobalEntityYaw(mesh), GlobalEntityRoll(mesh)
	PositionMesh mesh2, EntityX(mesh), EntityY(mesh), EntityZ(mesh)
	
	Return mesh2
	
End Function


;-----------------------------------------------------------------------------------------------------
;											EntityWidth()
;-----------------------------------------------------------------------------------------------------
;returns width of an entity
Function EntityWidth#( mesh )
	
	If EntityClass$(mesh) <> "Mesh" Then Return 1
	If MeshWidth(mesh) = 0 Then Return 1

	TFormPoint MeshWidth(mesh), 0, 0, mesh, 0	
	xx# = TFormedX()
	yy# = TFormedY()
	zz# = TFormedZ()
	TFormPoint 0, 0, 0, mesh, 0	
	xx# = TFormedX()-xx
	yy# = TFormedY()-yy
	zz# = TFormedZ()-zz	
	ll# = Sqr(xx * xx + yy * yy + zz * zz) / MeshWidth(mesh)
	
	If ll = 0 Then ll = 1
	Return ll
	
End Function

;-----------------------------------------------------------------------------------------------------
;											EntityHeight()
;-----------------------------------------------------------------------------------------------------
;returns height of an entity
Function EntityHeight#( mesh )

	If EntityClass$(mesh) <> "Mesh" Then Return 1
	If MeshHeight(mesh) = 0 Then Return 1
	
	TFormPoint 0, MeshHeight(mesh), 0, mesh, 0
	xx# = TFormedX()
	yy# = TFormedY()
	zz# = TFormedZ()
	TFormPoint 0, 0, 0, mesh, 0
	xx# = TFormedX()-xx
	yy# = TFormedY()-yy
	zz# = TFormedZ()-zz
	ll# = Sqr(xx * xx + yy * yy + zz * zz) / MeshHeight(mesh)
	
	If ll = 0 Then ll = 1
	Return ll
	
End Function

;-----------------------------------------------------------------------------------------------------
;											EntityDepth()
;-----------------------------------------------------------------------------------------------------
;returns depth of an entity
Function EntityDepth#( mesh )

	If EntityClass$(mesh) <> "Mesh" Then Return 1
	If MeshDepth(mesh) = 0 Then Return 1
	
	TFormPoint 0, 0, MeshDepth(mesh), mesh, 0	
	xx# = TFormedX()
	yy# = TFormedY()
	zz# = TFormedZ()
	TFormPoint 0, 0, 0, mesh, 0
	xx# = TFormedX()-xx
	yy# = TFormedY()-yy
	zz# = TFormedZ()-zz
	
	ll# = Sqr(xx * xx + yy * yy + zz * zz) / MeshDepth(mesh)
	
	If ll = 0 Then ll = 1
	Return ll
		
End Function

;-----------------------------------------------------------------------------------------------------
;											GlobalEntityPitch()
;-----------------------------------------------------------------------------------------------------
Function GlobalEntityPitch#(entity)

	pit# = Int(EntityPitch(entity)*1000)/1000.0
	
	Return pit

End Function

;-----------------------------------------------------------------------------------------------------
;											GlobalEntityYaw()
;-----------------------------------------------------------------------------------------------------
Function GlobalEntityYaw#(entity)
	
	pit# = Int(EntityPitch(entity)*1000)/1000.0
	yaw# = Int(EntityYaw(entity)*1000)/1000.0
	
	If Abs(pit)>GIMBAL_LIMIT
		temp = CreatePivot(entity)
		EntityParent temp, GetParent(entity)
		TurnEntity temp,Sgn(pit)*-90.0,0,0
		yaw# = Int(EntityYaw(temp)*1000)/1000.0
		FreeEntity temp
	EndIf

	Return yaw
	
End Function

;-----------------------------------------------------------------------------------------------------
;											GlobalEntityRoll()
;-----------------------------------------------------------------------------------------------------
Function GlobalEntityRoll#(entity)

	pit# = Int(EntityPitch(entity)*1000)/1000.0
	rol# = Int(EntityRoll(entity)*1000)/1000.0

	If Abs(pit)>GIMBAL_LIMIT
		temp = CreatePivot(entity)
		EntityParent temp, GetParent(entity)
		TurnEntity temp,Sgn(pit)*-90.0,0,0
		rol# = Int(EntityRoll(temp)*1000)/1000.0
		FreeEntity temp
	EndIf

	Return rol

End Function




; -------------------------------------------------------------------------------------------------------------------
; This function returns true if a ray intersects a mesh.
;
; This function differs from LinePick in that the specified mesh does not need to have a pickmode set,
; and this function can optionally ignore backfacing polygons in the mesh.  So if you do a pick from 
; inside a mesh, it will not register a hit.
; -------------------------------------------------------------------------------------------------------------------
Function Ray_Intersect_Mesh_Max#(Mesh, Px#, Py#, Pz#, Dx#, Dy#, Dz#, Extend_To_Infinity=True, Cull_Backfaces=False, Flip_Mesh=False, Method=1)

	max# = 65536
	count = 0
	
	Surfaces = CountSurfaces(Mesh)

	; Make sure there's a surface, because the mesh might be empty.
	If Surfaces > 0

		For SurfaceLoop = 1 To Surfaces

			Surface = GetSurface(Mesh, SurfaceLoop)
	
			; Examine all triangles in this surface.	
			Tris  = CountTriangles(Surface)
			For TriLoop = 0 To Tris-1
	
				V0 = TriangleVertex(Surface, TriLoop, 0)
				V1 = TriangleVertex(Surface, TriLoop, 1)
				V2 = TriangleVertex(Surface, TriLoop, 2)
		
				V0x# = VertexX#(Surface, V0)
				V0y# = VertexY#(Surface, V0)
				V0z# = VertexZ#(Surface, V0)

				V1x# = VertexX#(Surface, V1)
				V1y# = VertexY#(Surface, V1)
				V1z# = VertexZ#(Surface, V1)

				V2x# = VertexX#(Surface, V2)
				V2y# = VertexY#(Surface, V2)
				V2z# = VertexZ#(Surface, V2)

				TFormPoint V0x#, V0y#, V0z#, Mesh, 0
				V0x# = TFormedX#()
				V0y# = TFormedY#()
				V0z# = TFormedZ#()

				TFormPoint V1x#, V1y#, V1z#, Mesh, 0
				V1x# = TFormedX#()
				V1y# = TFormedY#()
				V1z# = TFormedZ#()
			
				TFormPoint V2x#, V2y#, V2z#, Mesh, 0
				V2x# = TFormedX#()
				V2y# = TFormedY#()
				V2z# = TFormedZ#()
				
				Intersected = Ray_Intersect_Triangle(Px#, Py#, Pz#, Px+Dx#, Py+Dy#, Pz+Dz#, V0x#, V0y#, V0z#, V1x#, V1y#, V1z#, V2x#, V2y#, V2z#, Extend_To_Infinity, Cull_Backfaces, Flip_Mesh)
				If Intersected Then
					
					d# = dist#(Px, Py, Pz, tpicked(0), tpicked(1), tpicked(2))
					If d < max Then max# = d
					count = count + 1
					
				End If
		
			Next
	
		Next
		
	EndIf
	
	If method = 1 Then Return max Else Return count

End Function

;find min. val of abc
Function findmin#(a#,b#,c#)

	If b < a Then a = b
	If c < a Then a = c
	
	Return a
	
End Function

;find max. val of abc
Function findmax#(a#,b#,c#)

	If b > a Then a = b
	If c > a Then a = c
	
	Return a
	
End Function


;-----------------------------------------------------------------------------------------------------
;-----------------------------------------------------------------------------------------------------
;											EXAMPLE99.BB
;-----------------------------------------------------------------------------------------------------
;-----------------------------------------------------------------------------------------------------

Type light
	Field x#,y#,z#
	Field r,g,b
End Type

Local wire=0

Graphics3D 640,480,0,2
SetBuffer BackBuffer()
Flip

cube = CreateCube()
ScaleMesh cube,10,2,10
cube2 = CreateCube()
ScaleMesh cube2,5,5,5
PositionEntity cube2, 5, 0, 0

tex = CreateCheckerTexture()
brush = CreateBrush()
BrushTexture brush, tex

tex2 = CreateNoiseTexture()
brush2 = CreateBrush()
BrushTexture brush2, tex2

tex3 = CreateGreenTexture()
brush3 = CreateBrush()
BrushTexture brush3, tex3

tex4 = CreateWhiteTexture()
brush4 = CreateBrush()
BrushTexture brush4, tex4

pcam = CreatePivot()
Global cam = CreateCamera(pcam)
MoveEntity pcam,0,10,-20
CameraClsColor cam, 24,128,255

TurnEntity CreateLight(),45,45,0

EntityFX cube,1+16+2
EntityAlpha cube2,.5
PaintMesh cube, brush
PaintMesh cube2, brush2


ttx = True
While Not KeyHit(1)

	If KeyDown(45) Then TurnEntity cube2,0,1,0
	
	If MouseDown(2) Then
		TurnEntity cam, MouseYSpeed() * 0.1, -MouseXSpeed() * 0.1, 0
		MoveMouse 400, 300
	Else
		RotateEntity cam, 0, 0, 0
	End If

	If KeyDown(29) Then
	
		If KeyDown(203) TurnEntity pcam, 0, 1, 0
		If KeyDown(205) TurnEntity pcam, 0, -1, 0
		If KeyDown(30) MoveEntity pcam, 0,  0.1, 0
		If KeyDown(44) MoveEntity pcam, 0, -0.1, 0
		If KeyDown(200) MoveEntity pcam, 0, 0, 0.1
		If KeyDown(208) MoveEntity pcam, 0, 0, -0.1

	Else
		
		If KeyDown(205) Then MoveEntity cube2,.1,0,0
		If KeyDown(203) Then MoveEntity cube2,-.1,0,0
		If KeyDown(200) Then MoveEntity cube2,0,0,.1
		If KeyDown(208) Then MoveEntity cube2,0,0,-.1
		If KeyHit(30) Then MoveEntity cube2,0,4.3,0
		If KeyHit(44) Then MoveEntity cube2,0,-4.3,0

	End If	
	
	If KeyHit(24)
		ttx = (ttx + 1) Mod 4
		Select ttx
		Case 0
			PaintMesh cube2, brush2
		Case 1
			PaintMesh cube2, brush
		Case 2
			PaintMesh cube2, brush3
		Case 3
			PaintMesh cube2, brush4
		End Select
	End If
	
	If KeyHit(25)
		wire = 1 - wire
		WireFrame wire
	EndIf
			
	If KeyHit(57)
			
		e = MilliSecs()		
		man = csg(cube,cube2,CSG_FILL)
		e = MilliSecs() -e
		FreeEntity cube
		cube = man

		EntityFX cube, 1+16+2
		
	EndIf
		
	If KeyHit(17) Then
		FreeEntity cube2
		cube2 = CreateCube()
		ScaleEntity cube2, 5, 5, 5
		PositionEntity cube2, 5, 0, 0
		PaintMesh cube2, brush2
	End If

	If KeyHit(18) Then
		FreeEntity cube2
		cube2 = CreateSphere()
		ScaleEntity cube2, 5, 5, 5
		PositionEntity cube2, 5, 0, 0
		PaintMesh cube2, brush2
	End If
	
	If KeyHit(19) Then
		FreeEntity cube2
		cube2 = CreateCylinder()
		ScaleEntity cube2, 5, 5, 5
		PositionEntity cube2, 5, 0, 0
		PaintMesh cube2, brush2
	End If

	If KeyHit(16)
		e2 = MilliSecs()		
		FlipMesh cube		
		FlipMesh cube
		e2 = MilliSecs() -e2
		EntityFX cube, 1+16+2
	End If	
	
	UpdateWorld		
	RenderWorld
	
	Color 255,255,255	
	Text 0,0,"CSG Time: "+e+" m"
	Text 0,15,"Lightmap Time: "+e2+" m"
	Text 0,30,"Use cursor keys/AZ to move block"
	Text 0,45,"CTRL+cursor keys/AZ to move camera"
	Text 0,60,"P=Wireframe O=toggle texture"
	Text 0,75,"Tris: "+TrisRendered()/2
	Flip
Wend

End

Function CreateWhiteTexture()

	Cls
	Color 255,255,255
	Rect 0, 0, 256, 256
			
	tex = CreateTexture(256, 256, 1+8)
	CopyRect 0, 0, 256, 256, 0, 0, BackBuffer(), TextureBuffer(tex)
	
	Color 255,255,255
	
;	SaveBuffer TextureBuffer(tex), "white.bmp"
	
	Return tex

End Function		


Function CreateCheckerTexture()

	For i = 0 To 256/32+1
	For j = 0 To 256/32+1
		c = (i + j) Mod 2 * 255
		Color c, c, c
		Rect i * 32, j * 32, 32, 32
	Next
	Next
	
	tex = CreateTexture(256, 256, 1+8)
	CopyRect 0, 0, 256, 256, 0, 0, BackBuffer(), TextureBuffer(tex)

;	SaveBuffer TextureBuffer(tex), "checker.bmp"
	
	Color 255,255,255
	
	Return tex
	
End Function

Function CreateNoiseTexture()

	For i = 0 To 256
	For j = 0 To 256
		c = Rand(0, 255)
		Color c, c, c
		Plot i, j
	Next
	Next

	tex = CreateTexture(256, 256, 1+8)
	CopyRect 0, 0, 256, 256, 0, 0, BackBuffer(), TextureBuffer(tex)

;	SaveBuffer TextureBuffer(tex), "noise.bmp"
	
	Color 255,255,255
	
	Return tex
	
End Function
	
Function CreateGreenTexture()

	Cls
	For i = 0 To 1000
		x = Rand(0, 256)
		y = Rand(0, 256)
		For c = 5 To 1 Step -1
			cc = c * 51
			Color 0,cc,0
			r = c * 5
			Oval x - r, y - r, r * 2, r * 2
		Next
	Next
			
	tex = CreateTexture(256, 256, 1+8)
	CopyRect 0, 0, 256, 256, 0, 0, BackBuffer(), TextureBuffer(tex)

;	SaveBuffer TextureBuffer(tex), "green.bmp"
	
	Color 255,255,255
	
	Return tex

End Function
