; ID: 3294
; Author: Bobysait
; Date: 2016-10-16 14:13:14
; Title: random Spore-Like planet
; Description: GeoSphere + Random stuff

; *****************************************************************************************
; * GEO SPHERE *
; *****************************************************************************************
	; temp vertex for geo sphere
	Type TGeoVertex
		Field x#,y#,z#, u#,v#
	End Type
	Function NewGeoVertex.TGeoVertex(x#,y#,z#)
		Local gv.TGeoVertex = New TGeoVertex
		gv\x = x : gv\y = y : gv\z = z
		Return gv
	End Function
	Function BuildGeoV%(S, g.TGeoVertex, u#=0,v#=0)
		Local i = AddVertex(S, g\x,g\y,g\z, u,v)
		VertexNormal S,i, g\x,g\y,g\z
		Return i
	End Function
	
	; constructor
	Function GEOcreate( Detail=2, Parent=0 )
		
		Delete Each TGeoVertex
		
		Local sens = Detail Mod(2)
		Local pivot = CreatePivot(Parent)
		
		a# = 1.0/Sqr(2)
		b# = a
		;a#=2.0/(1.0+Sqr(5.0))					
		;b#=1.0/Sqr((3.0+Sqr(5.0)) / (1.0+Sqr(5.0)))
		
		Local vNP.TGeoVertex=NewGeoVertex( 0,  1,  0)
		Local vSP.TGeoVertex=NewGeoVertex( 0, -1,  0)
		; Planes Corners
		; Plane YZ
		Local v00.TGeoVertex=NewGeoVertex( 0,  a,  b)
		Local v01.TGeoVertex=NewGeoVertex( 0,  a, -b)
		Local v02.TGeoVertex=NewGeoVertex( 0, -a,  b)
		Local v03.TGeoVertex=NewGeoVertex( 0, -a, -b)
		; Plane XY 
		Local v04.TGeoVertex=NewGeoVertex( a,  b,  0)
		Local v05.TGeoVertex=NewGeoVertex( a, -b,  0)
		Local v06.TGeoVertex=NewGeoVertex(-a,  b,  0)
		Local v07.TGeoVertex=NewGeoVertex(-a, -b,  0)
		; Plane XZ
		Local v08.TGeoVertex=NewGeoVertex( b,  0,  a)
		Local v09.TGeoVertex=NewGeoVertex( b,  0, -a)
		Local v10.TGeoVertex=NewGeoVertex(-b,  0,  a)
		Local v11.TGeoVertex=NewGeoVertex(-b,  0, -a)
		
		Local Mesh
		; North plane (Top)
		Mesh = CreateMesh( pivot ) : S = CreateSurface( Mesh )
		GEOsub( S, BuildGeoV(s,vNP, 0.625,0.000),BuildGeoV(s,v01, 0.500,0.250),BuildGeoV(s,v04, 0.750,0.250), Detail, sens )
		Mesh = CreateMesh( pivot ) : S = CreateSurface( Mesh )
		GEOsub( S, BuildGeoV(s,vNP, 0.875,0.000),BuildGeoV(s,v04, 0.750,0.250),BuildGeoV(s,v00, 1.000,0.250), Detail, sens )
		Mesh = CreateMesh( pivot ) : S = CreateSurface( Mesh )
		GEOsub( S, BuildGeoV(s,vNP, 0.125,0.000),BuildGeoV(s,v00, 0.000,0.250),BuildGeoV(s,v06, 0.250,0.250), Detail, sens )
		Mesh = CreateMesh( pivot ) : S = CreateSurface( Mesh )
		GEOsub( S, BuildGeoV(s,vNP, 0.375,0.000),BuildGeoV(s,v06, 0.250,0.250),BuildGeoV(s,v01, 0.500,0.250), Detail, sens )
		
		; south plane (Bottom)
		Mesh = CreateMesh( pivot ) : S = CreateSurface( Mesh )
		GEOsub( S, BuildGeoV(s,vSP, 0.125,1.000),BuildGeoV(s,v07, 0.250,0.750),BuildGeoV(s,v02, 0.000,0.750), Detail, sens )
		Mesh = CreateMesh( pivot ) : S = CreateSurface( Mesh )
		GEOsub( S, BuildGeoV(s,vSP, 0.375,1.000),BuildGeoV(s,v03, 0.500,0.750),BuildGeoV(s,v07, 0.250,0.750), Detail, sens )
		Mesh = CreateMesh( pivot ) : S = CreateSurface( Mesh )
		GEOsub( S, BuildGeoV(s,vSP, 0.625,1.000),BuildGeoV(s,v05, 0.750,0.750),BuildGeoV(s,v03, 0.500,0.750), Detail, sens )
		Mesh = CreateMesh( pivot ) : S = CreateSurface( Mesh )
		GEOsub( S, BuildGeoV(s,vSP, 0.875,1.000),BuildGeoV(s,v02, 1.000,0.750),BuildGeoV(s,v05, 0.750,0.750), Detail, sens )
		
		Mesh = CreateMesh( pivot ) : S = CreateSurface( Mesh )
		GEOsub( S, BuildGeoV(s,v08, 0.875,0.500),BuildGeoV(s,v02, 1.000,0.750),BuildGeoV(s,v00, 1.000,0.250), Detail, sens ) ; Back
		Mesh = CreateMesh( pivot ) : S = CreateSurface( Mesh )
		GEOsub( S, BuildGeoV(s,v10, 0.125,0.500),BuildGeoV(s,v00, 0.000,0.250),BuildGeoV(s,v02, 0.000,0.750), Detail, sens )
		
		Mesh = CreateMesh( pivot ) : S = CreateSurface( Mesh )
		GEOsub( S, BuildGeoV(s,v09, 0.625,0.500),BuildGeoV(s,v01, 0.500,0.250),BuildGeoV(s,v03, 0.500,0.750), Detail, sens ) ; Front
		Mesh = CreateMesh( pivot ) : S = CreateSurface( Mesh )
		GEOsub( S, BuildGeoV(s,v11, 0.375,0.500),BuildGeoV(s,v03, 0.500,0.750),BuildGeoV(s,v01, 0.500,0.250), Detail, sens )
		
		Mesh = CreateMesh( pivot ) : S = CreateSurface( Mesh )
		GEOsub( S, BuildGeoV(s,v06, 0.250,0.250),BuildGeoV(s,v10, 0.125,0.500),BuildGeoV(s,v11, 0.375,0.500), Detail, sens ) ; Left
		Mesh = CreateMesh( pivot ) : S = CreateSurface( Mesh )
		GEOsub( S, BuildGeoV(s,v07, 0.250,0.750),BuildGeoV(s,v11, 0.375,0.500),BuildGeoV(s,v10, 0.125,0.500), Detail, sens )
		
		Mesh = CreateMesh( pivot ) : S = CreateSurface( Mesh )
		GEOsub( S, BuildGeoV(s,v04, 0.750,0.250),BuildGeoV(s,v09, 0.625,0.500),BuildGeoV(s,v08, 0.875,0.500), Detail, sens ) ; Right
		Mesh = CreateMesh( pivot ) : S = CreateSurface( Mesh )
		GEOsub( S, BuildGeoV(s,v05, 0.750,0.750),BuildGeoV(s,v08, 0.875,0.500),BuildGeoV(s,v09, 0.625,0.500), Detail, sens )
		
		; Joints
		Mesh = CreateMesh( pivot ) : S = CreateSurface( Mesh )
		GEOsub( S, BuildGeoV(s,v00, 0.000,0.250),BuildGeoV(s,v10, 0.125,0.500),BuildGeoV(s,v06, 0.250,0.250), Detail, sens ); Top-Left-Back
		Mesh = CreateMesh( pivot ) : S = CreateSurface( Mesh )
		GEOsub( S, BuildGeoV(s,v00 ,1.000,0.250),BuildGeoV(s,v04, 0.750,0.250),BuildGeoV(s,v08, 0.875,0.500), Detail, sens ); Top-Right-Back
		
		Mesh = CreateMesh( pivot ) : S = CreateSurface( Mesh )
		GEOsub( S, BuildGeoV(s,v01, 0.500,0.250),BuildGeoV(s,v06, 0.250,0.250),BuildGeoV(s,v11, 0.375,0.500), Detail, sens ); Top-Left-Front
		Mesh = CreateMesh( pivot ) : S = CreateSurface( Mesh )
		GEOsub( S, BuildGeoV(s,v01, 0.500,0.250),BuildGeoV(s,v09, 0.625,0.500),BuildGeoV(s,v04, 0.750,0.250), Detail, sens ); Right-Left-Front
		
		Mesh = CreateMesh( pivot ) : S = CreateSurface( Mesh )
		GEOsub( S, BuildGeoV(s,v03, 0.500,0.750),BuildGeoV(s,v11, 0.375,0.500),BuildGeoV(s,v07, 0.250,0.750), Detail, sens ); Bottom-Left-Front
		Mesh = CreateMesh( pivot ) : S = CreateSurface( Mesh )
		GEOsub( S, BuildGeoV(s,v03, 0.500,0.750),BuildGeoV(s,v05, 0.750,0.750),BuildGeoV(s,v09, 0.625,0.500), Detail, sens ); Bottom-Right-Front
		
		Mesh = CreateMesh( pivot ) : S = CreateSurface( Mesh )
		GEOsub( S, BuildGeoV(s,v02, 0.000,0.750),BuildGeoV(s,v07, 0.250,0.750),BuildGeoV(s,v10, 0.125,0.500), Detail, sens ); Bottom-Left-Back
		Mesh = CreateMesh( pivot ) : S = CreateSurface( Mesh )
		GEOsub( S, BuildGeoV(s,v02, 1.000,0.750),BuildGeoV(s,v08, 0.875,0.500),BuildGeoV(s,v05, 0.750,0.750), Detail, sens ); Bottom-Right-Back
		
		Delete Each TGeoVertex
		
		Return pivot
		
	End Function
	
	Function GEOsub( S, v1 , v2 , v3 , Detail, sens% )
		
		Local u#,v#, nx#, ny#,nz#, l#
		Local va%, vb%
		If ( Detail > 0 )
			; cut segment in half
			va = v1 : vb = v2
			; half coords
			nx=(VertexX(S,va)+VertexX(S,vb)) * .5
			ny=(VertexY(S,va)+VertexY(S,vb)) * .5
			nz=(VertexZ(S,va)+VertexZ(S,vb)) * .5
			l=1.0/Sqr(nx*nx+ny*ny+nz*nz) ; inverse normal
			nx = nx*l: ny=ny*l: nz=nz*l
			; half UVs
			u=(VertexU(S,va)+VertexU(S,vb)) * .5
			v=(VertexV(S,va)+VertexV(S,vb)) * .5
			; create vertex with UVs
			n1=AddVertex(S,nx,ny,nz,u,v)
			; set normal (for a normalized sphere -> normal = coords)
			VertexNormal S, n1, nx,ny,nz
			
			va = v2 : vb = v3
			nx=(VertexX(S,va)+VertexX(S,vb)) * .5
			ny=(VertexY(S,va)+VertexY(S,vb)) * .5
			nz=(VertexZ(S,va)+VertexZ(S,vb)) * .5
			l=1.0/Sqr(nx*nx+ny*ny+nz*nz)
			nx = nx*l: ny=ny*l: nz=nz*l
			u=(VertexU(S,va)+VertexU(S,vb)) * .5
			v=(VertexV(S,va)+VertexV(S,vb)) * .5
			n2=AddVertex(S,nx,ny,nz,u,v)
			VertexNormal S, n2, nx,ny,nz
			
			va = v3 : vb = v1
			nx=(VertexX(S,va)+VertexX(S,vb)) * .5
			ny=(VertexY(S,va)+VertexY(S,vb)) * .5
			nz=(VertexZ(S,va)+VertexZ(S,vb)) * .5
			l=1.0/Sqr(nx*nx+ny*ny+nz*nz)
			nx = nx*l: ny=ny*l: nz=nz*l
			u=(VertexU(S,va)+VertexU(S,vb)) * .5
			v=(VertexV(S,va)+VertexV(S,vb)) * .5
			n3=AddVertex(S,nx,ny,nz,u,v)
			VertexNormal S, n3, nx,ny,nz
			
			; cut again (or create a triangle at lower detail)
			GEOsub( S, v1,n3,n1, Detail-1, sens )
			GEOsub( S, v2,n1,n2, Detail-1, sens )
			GEOsub( S, v3,n2,n3, Detail-1, sens )
			GEOsub( S, n3,n2,n1, Detail-1, sens )
		Else
			If sens
				AddTriangle(S,v1,v2,v3)
			Else
				AddTriangle(S,v2,v1,v3)
			EndIf
		End If
		
	End Function
	

; *****************************************************************************************
; * SOME TEXTURE STUFF *
; *****************************************************************************************
	; clear all pixels
	Function ClearTexture(tex%, c%)
		Local curbuf = GraphicsBuffer()
		Local TexW% = TextureWidth(tex)
		Local TexH% = TextureHeight(tex)
		Local i%,j%
		SetBuffer TextureBuffer (tex)
			LockBuffer()
				For j = 0 To TexH-1
					For i = 0 To TexW-1
						WritePixelFast (i,j, c)
					Next
				Next
			UnLockBuffer()
		SetBuffer curbuf
	End Function
	
	; for the geo sphere texture, fill the pink holes
	Function ExpandSeams(tex, Mask%=$FF00FF)
		Local curbuf = GraphicsBuffer()
		Local TexW% = TextureWidth(tex)
		Local TexH% = TextureHeight(tex)
		Local i%,j%
		SetBuffer TextureBuffer(tex)
			LockBuffer()
				Local j1
				Local p%
				; Expand pixels right
				For j = 0 To TexH-1
					Local pL = Mask
					For i = 1 To TexW-1
						p = ReadPixelFast(i,j) And $FFFFFF
						If (p=Mask)
							If (pL<>Mask) Then WritePixelFast (i,j, pL)
						Else
							pL = p
						EndIf
					Next
				Next
				; Expand one pixel left
				For j = 0 To TexH-1
					Local LeftIsPink = False
					For i = TexW-2 To 0 Step -1
						p = ReadPixelFast(i,j) And $FFFFFF
						If (p=Mask)
							If (pL<>Mask) Then WritePixelFast (i,j, pL)
						Else
							pL = p
						EndIf
					Next
				Next
			UnlockBuffer()
		SetBuffer curbuf
	End Function


Graphics3D 1024,768,0,2
SetBuffer BackBuffer()


; *****************************************************************************************
; * RANDOM GEO-PLANET *
; *****************************************************************************************
	
	Local FrameSize = 256
	Local TexW = FrameSize * 4
	Local TexH = FrameSize * 2
	
	Local heightmap = CreateTexture(TexW,TexH, 1) : ClearTexture (heightmap, $FFFF00FF)
	Local bufH = TextureBuffer(heightmap)
	Local tex = CreateTexture(TexW,TexH, 1) : ClearTexture (tex, $FFFF00FF)

	Local geosphere= GEOcreate(5)
	
	Function DrawProgress(percent#, title$="")
		Cls
		Color 255,255,000
		Rect 7,7,786,22, 1
		Color 000,000,100
		Rect 10,10, 780,16,1
		Color 000,128,255
		Rect 10,10, 780*percent, 16
		If (title<>"")
			Color 255,128,000
			Text 10,50, title
		EndIf
		Flip True
	End Function
	
	; ************************
	; * Create random points *
	; ************************
		
		Local surf, child, v%
		Local i%,j%
		
		; simple random heights with radius (act like smoothed spheres that deforms vertices in their range)
			Type RandHeight
				Field X#,Y#,Z#, H#, N#
			End Type
			Local rh.RandHeight
			For t=0 to 250
				rh = New RandHeight
				rh\X = Rnd(-1,1) : rh\Y = Rnd(-1,1) : rh\Z = Rnd(-1,1); position of the deformation
				Local rn# = 1.0/Sqr(rh\X*rh\X+rh\Y*rh\Y+rh\Z*rh\Z) ; normalize the position
				rh\X=rh\X*rn : rh\Y=rh\Y*rn : rh\Z=rh\Z*rn
				rh\H = 1.15*(-0.002+0.015*Rnd(-.9,1.15)); deformation factor
				rh\N = Rnd(0.1,0.4)*1.25; range
				rh\N = rh\N * rh\N ; square it
			Next
		
	; *************************
	; * Create Color Template *
	; *************************
		
		; color template depending on height
		Local MinH# = 0.950
		Local MaxH# = 1.190
		Local sumH#
		
		Local ColorH_R#[7], ColorH_G#[7], ColorH_B#[7], ColorH_H#[7]
			ColorH_R[0] = 000 : ColorH_G[0] = 010 : ColorH_B[0] = 080 : ColorH_H[0] = MinH
			ColorH_R[1] = 010 : ColorH_G[1] = 080 : ColorH_B[1] = 170 : ColorH_H[1] = 0.990 ; water
			ColorH_R[2] = 255 : ColorH_G[2] = 200 : ColorH_B[2] = 080 : ColorH_H[2] = 0.999 ; sand
			ColorH_R[3] = 050 : ColorH_G[3] = 120 : ColorH_B[3] = 020 : ColorH_H[3] = 1.050 ; grass
			ColorH_R[4] = 130 : ColorH_G[4] = 100 : ColorH_B[4] = 020 : ColorH_H[4] = 1.125 ; rocks
			ColorH_R[5] = 200 : ColorH_G[5] = 050 : ColorH_B[5] = 000 : ColorH_H[5] = 1.170 ; reddish ...
			ColorH_R[6] = 255 : ColorH_G[6] = 255 : ColorH_B[6] = 255 : ColorH_H[6] = MaxH; heigh mountains (snow)
		
	; *********************
	; * Randomize heights *
	; *********************
		
		For nc = 1 To CountChildren(geosphere)
			; debug progress
			DrawProgress(Float(nc-1)/(CountChildren(geosphere)-1), "Generate Heights")
			
			; get child mesh and its surfaces
			child = GetChild(geosphere,nc)
				; set the "material"
				EntityShininess child,.05
				EntityTexture child, tex
				
			For is = 1 To CountSurfaces(child)
				surf	=	GetSurface (child,is)
				
				For v = 0 To CountVertices(surf)-1
					Local x#=VertexX(surf, v), y#=VertexY(surf, v),z#=VertexZ(surf, v)
					; initialize height to 1.0 (for a normalized sphere, radius is "1" for any vertex)
					sumH = 1.0
					For rh = Each RandHeight
						Local dx# = (x-rh\X)
						Local dy# = (y-rh\Y)
						Local dz# = (z-rh\Z)
						; distance from the vertex to the random sphere
						Local d# = (dx*dx+dy*dy+dz*dz)
						; vertex in range : sum up the interpolated height
						If (d<rh\N) Then sumH = sumH + rh\H*(rh\N-d)/rh\N
					Next
					
					; [optional : exponential height]
					; (remove this line for smoother results)
					sumH = sumH * sumH * sumH * sumH
					
					; clamp height
					If (sumH<MinH)
						sumH = MinH
					ElseIf (sumH>MaxH)
						sumH=MaxH
					EndIf
					
					; set new coordinates (push the vertex along the normal)
					VertexCoords(surf, v, x*sumH, y*sumH, z*sumH)
					
				Next
			Next
		Next
		
	; *********************
	; * Extract heightmap *
	; *********************
		
		; -------------------------------------------------------------
		; > Unfold the scene then convert heights to color
		; -------------------------------------------------------------
			; create a temporary scene with copies of the surfaces
			; flatten the coordinates using the UVs as projection
			; render the scene to a camera with viewport sized to the
			; heightmap resolution
			; /!\ The CameraCls 255,000,255 is required to expand
			; the seams so that the pixels of the lines at the top and
			; bottom are not visible
		
		Local UVCam = CreateCamera()
			MoveEntity UVCam,32000,-32000,32000
			CameraViewPort(UVCam, 0,0, FrameSize*2,FrameSize*2)
			CameraClsColor(UVCam, 255,000,255)
			CameraProjMode(UVCam, 2)
			CameraRange(UVCam, 1,2)
			CameraZoom(UVCam, 1)
			
			; create a new mesh to happen the flattened surfaces
			Local sub = CreateMesh(UVCam)
				; fx fullbright + vertex color + 2 sided
				EntityFx sub, 1+2+16
				Local surfUV = CreateSurface(sub)
			
		For nc = 1 To CountChildren(geosphere)
			; progress bar
			DrawProgress(Float(nc-1)/(CountChildren(geosphere)-1), "Extract HeightMap")
			
			child = GetChild(geosphere,nc)
			
			Local Min_I# = 2000
			Local Min_J# = 2000
			Local DestI = TexW+1, DestJ = TexH+1
			Local UV_I%, UV_J%
			
			Local MinU# = 10
			Local MinV# = 10
			Local curU#, curV#
			
			For is = 1 To CountSurfaces(child)
				
				ClearSurface (surfUV,1,1)
				
				Local CellI = 10
				Local CellJ = 10
				Local OffsetU# = 0.0
				Local OffsetV# = 0.0
				
				surf = GetSurface(child,is)
				For v = 0 To CountVertices(surf)-1
					Local c_i = Floor(VertexU(surf,v)*8)
					Local c_j = Floor(VertexV(surf,v)*4)
					If (c_i<CellI) Then CellI = c_i
					If (c_j<CellJ) Then CellJ = c_j
				Next
				
				OffsetU = 0.125 * CellI
				OffsetV = 0.25 * CellJ
				
				Select CellJ
					Case 0 : DestJ=0 : OffsetV = 0
					Case 1,2 : DestJ = FrameSize/2 : OffsetV = 0.25
					Case 3 : DestJ = FrameSize+FrameSize/2 : OffsetV = 0.75
				End Select
				
				DestI = CellI * FrameSize / 2
				
				surf = GetSurface(child,is)
				For v = 0 To CountVertices(surf)-1
					; convert vertex uv to screen coordinates
					Local uv_v = AddVertex (surfUV, -1+4.0*(VertexU(surf,v)-OffsetU), 1.0-2.0*(VertexV(surf,v)-OffsetV), 1)
					; convert distance from center to height
					sumH = Sqr(VertexX(surf,v)^2+VertexY(surf,v)^2+VertexZ(surf,v)^2)
					; convert height to color
					Local h_c% = Int( Floor( 255.0*(sumH-MinH)/(MaxH-MinH)))
					; color the vertex
					VertexColor(surfUV,uv_v, h_c,h_c,h_c, 1.5)
				Next
				
				; build the triangles ^^
				For t = 0 To CountTriangles(surf)-1
					AddTriangle(surfUV, TriangleVertex(surf,t,0),TriangleVertex(surf,t,1),TriangleVertex(surf,t,2))
				Next
				
				; copy the screen to the heightmap texture
				RenderWorld()
				
				LockBuffer()
					LockBuffer BufH
						For j = 0 To FrameSize-1
							For i = 0 To FrameSize-1
								Local pixel = ReadPixelFast(i,j)
								If (pixel<>$FFFF00FF And pixel<>$FF00FF)
									Local i_= i+DestI
									If i_>=0 And i_ <TexW
										Local j_= j+DestJ
										If j_>=0 And j_<TexH
											WritePixelFast(i_,j_, pixel, bufH)
										EndIf
									EndIf
								EndIf
							Next
						Next
					UnLockBuffer(BufH)
				UnLockBuffer()
				
			Next
			
		Next
		
		; free the scene (the camera and all its hierarchy)
		FreeEntity UVCam
		
		
		
		; fill the pink holes
		ExpandSeams(heightmap)
		
		; set height color
		LockBuffer bufH
			SetBuffer TextureBuffer (tex)
			LockBuffer()
				Local h_Coef# = (MaxH-MinH) / 255.0
				For j = 0 To TexH-1
				For i = 0 To TexW-1
					; convert [0-255] height to [Min-Max] height
					sumH = MinH + (Float(ReadPixelFast(i,j, bufH) And $FF)) * h_Coef
					
					; initialize with default color (= default color for deepest height)
					Local v_r% = ColorH_R[0], v_g%=ColorH_G[0], v_b%=ColorH_B[0]
					
					; get color depending on height
					For n = 6 To 0 Step -1
						If (sumH>=ColorH_H[n])
							Local h_# = (sumH - ColorH_H[n])/(ColorH_H[n+1]-ColorH_H[n])
							v_r = ColorH_R[n] + (ColorH_R[n+1]-ColorH_R[n]) * h_
							v_g = ColorH_G[n] + (ColorH_G[n+1]-ColorH_G[n]) * h_
							v_b = ColorH_B[n] + (ColorH_B[n+1]-ColorH_B[n]) * h_
							found=1
							Exit
						EndIf
					Next
					
					; write the pixel color.
					WritePixelFast i,j, $FF000000 + v_r Shl(16) + v_g Shl(8) + v_b
				Next
			Next
			UnlockBuffer()
			SetBuffer BackBuffer()
		UnlockBuffer(bufH)
		
		; export height and diffuse to bmp
		SaveBuffer bufH, "geo_hmap.bmp"
		SaveBuffer TextureBuffer(tex), "geo_tex.bmp"
		
	; ******************
	; * Update Normals *
	; ******************
		
		Function CrossF3(r#[3], a#[3],b#[3])
			r[0] = a[1]*b[2]-a[2]*b[1]
			r[1] = a[2]*b[0]-a[0]*b[2]
			r[2] = a[0]*b[1]-a[1]*b[0]
		End Function
		Function TFormF3(R#[3], V#[3], X#[3],Y#[3],Z#[3])
			R[0] = v[0] * X[0] + V[1] * Y[0] + V[2] * Z[0]
			R[1] = v[0] * X[1] + V[1] * Y[1] + V[2] * Z[1]
			R[2] = v[0] * X[2] + V[1] * Y[2] + V[2] * Z[2]
		End Function
		Function MagF3(N#[3])
			Local l# = 1.0/Sqr(N[0]*N[0]+N[1]*N[1]+N[2]*N[2])
			N[0]=N[0]*l : N[1]=N[1]*l : N[2]=N[2]*l
		End Function
		
		Function QSetM3(q#[4], X#[3],Y#[3],Z#[3])
			Local t# = X[0]+Y[1]+Z[2]
			If( t>0.00000001 )
				t = Sqr( t+1.0 )*2.0;
				q[1] = (Z[1]-Y[2])/t;
				q[2] = (X[2]-Z[0])/t;
				q[3] = (Y[0]-X[1])/t;
				q[0] = t*.25;
			ElseIf( X[0]>Y[1] And X[0]>Z[2] )
				t=Sqr( X[0]-Y[1]-Z[2]+1.0 )*2.0;
				q[1]=t*.25;
				q[2]=(Y[0]+X[1])/t;
				q[3]=(X[1]+Z[0])/t;
				q[0]=(Z[2]-Y[2])/t;
			ElseIf( Y[1]>Z[2] )
				t=Sqr( Y[1]-Z[2]-X[0]+1.0 )*2;
				q[1]=(Y[0]+X[1])/t;
				q[2]=t*.25;
				q[3]=(Z[1]+Y[2])/t;
				q[0]=(X[2]-Z[0])/t;
			Else
				t=Sqr( Z[2]-Y[1]-X[0]+1.0 )*2.0;
				q[1]=(X[2]+Z[0])/t;
				q[2]=(Z[1]+Y[1])/t;
				q[3]=t*.25;
				q[0]=(Y[0]-X[1])/t;
			EndIf;
		End Function
		Function QMulV(r#[3], q#[4],v#[3])
			Local qw# = 			- q[1]*v[0]	- q[2]*v[1]	- q[3]*v[2];
			Local qx# = + q[0]*v[0] 			- q[2]*v[2]	+ q[3]*v[1];
			Local qy# = + q[0]*v[1]	+ q[1]*v[2]				- q[3]*v[0];
			Local qz# = + q[0]*v[2]	- q[1]*v[1]	+ q[2]*v[0];
			r[0] = - qw*q[1] + qx*q[0] + qy*q[3] - qz*q[2];
			r[1] = - qw*q[2] - qx*q[3] + qy*q[0] + qz*q[1];
			r[2] = - qw*q[3] + qx*q[2] - qy*q[1] + qz*q[0];
		End Function
		
		Local q#[4]
		
		; finally : use the heightmap to update the vertex normals
		For nc = 1 To CountChildren(geosphere)
			; progress bar
			DrawProgress(Float(nc-1)/(CountChildren(geosphere)-1), "Update Normals")
			
			SetBuffer bufH
			LockBuffer()
			child = GetChild(geosphere,nc)
			For is = 1 To CountSurfaces(child)
				surf = GetSurface(child,is)
				Local X_#[3], Y_#[3], Z_#[3], N_#[3], T_#[3]
				For v = 0 To CountVertices(surf)-1
					Y_[0]=VertexNX(surf, v)
					Y_[1]=VertexNY(surf, v)
					Y_[2]=VertexNZ(surf, v)
					
					MagF3(Y_)
					X_[0]=1:X_[1]=0:X_[2]=0
					If Abs(Y_[1])<1
						X_[0] = -Y_[2]
						X_[2] = Y_[0]
						MagF3(X_)
					EndIf
					CrossF3(Z_,X_,Y_)
					MagF3(Z_)
					QSetM3(q, X_,Y_,Z_)
					
					Local ti = Float(TexW-1) * VertexU(surf,v)
					Local tj = Float(TexH-1) * VertexV(surf,v)
					Local hT# = Float((ReadPixelFast(ti,tj) And $FF))/255
					Local hl# = 0, hr# = 0, hu# = 0, hd# = 0
					If (ti>0)
						hl = Float((ReadPixelFast(ti-1,tj) And $FF))/255-hT
					Else
						hl = Float((ReadPixelFast(TexW-1,tj) And $FF))/255-hT
					EndIf
					If (ti<TexW-1)
						hr = Float((ReadPixelFast(ti+1,tj) And $FF))/255-hT
					Else
						hr = Float((ReadPixelFast(0,tj) And $FF))/255-hT
					EndIf
					If (tj>0) Then hd = Float((ReadPixelFast(ti,tj-1) And $FF))/255-hT
					If (tj<TexH-1) Then hu = Float((ReadPixelFast(ti,tj+1) And $FF))/255-hT
					
					N_[0] = (hl-hr)
					N_[1] = Float(1)/128
					N_[2] = -(hd-hu)
					MagF3(N_)
					QMulV(T_,q,N_)
					
					VertexNormal surf, v, T_[0],T_[1],T_[2]
					VertexColor surf, v, 128+127*T_[0],128+127*T_[1],128+127*T_[2]
				Next
			Next
			
			UnLockBuffer()
			SetBuffer BackBuffer()
		Next
		
		
		; Weld normals
		For nc = 1 To 8
			; progress bar
			DrawProgress(Float(nc-1)/7, "Weld Normals")
			
			Local c1 = 1 + (nc>4) * 4, c2 = 4 + (nc>4) * 4
			SetBuffer bufH
			LockBuffer()
			child = GetChild(geosphere,nc)
			For is = 1 To CountSurfaces(child)
				surf = GetSurface(child,is)
				For v = 0 To CountVertices(surf)-1
					
					Local vx# = VertexX(surf,v)
					Local vy# = VertexY(surf,v)
					Local vz# = VertexZ(surf,v)
					Local vnx# = VertexNx(surf,v)
					Local vny# = VertexNy(surf,v)
					Local vnz# = VertexNz(surf,v)
					Local vnn#
					
					found = False
					; parse again
					For nc2 = nc+1 To c2
						if (nc <> nc2)
							Local child2=GetChild(geosphere, nc2)
							Local is2%, surf2, v2
							For is2 = 1 To CountSurfaces(child2)
								surf2 = GetSurface(child2,is2)
								For v2 = 0 To CountVertices(surf2)-1
									If (v<>v2)
										If (VertexX(surf2, v2)=vx) And (VertexY(surf2, v2)=vy) And (VertexZ(surf2, v2)=vz)
											vnx = (VertexNx(surf2,v2) + vnx)*0.5
											vny = (VertexNy(surf2,v2) + vny)*0.5
											vnz = (VertexNz(surf2,v2) + vnz)*0.5
											VertexNormal surf, v, vnx,vny,vnz
											VertexNormal surf2, v2, vnx,vny,vnz
											Exit
										EndIf
									EndIf
								Next
								If found Then Exit
							Next
							If found Then Exit
						EndIf
					Next
				Next
			Next
			UnLockBuffer()
			SetBuffer backBuffer()
		Next
		
		
; *****************************************************************************************
; * SAMPLE *
; *****************************************************************************************
	
	AmbientLight 0,0,0
	
	Local light1=	CreateLight		( 3 )
					PositionEntity	( light1, 200,100,-200 )
					PointEntity		( light1, geosphere )
					LightRange		( light1, 600 )
					LightColor		( light1, 255,180,100 )
					
	Local light2=	CreateLight		( 1 )
					PositionEntity	( light2, -500,-1000,500 )
					PointEntity		( light2, geosphere )
					LightRange		( light2, 10000 )
					LightColor		( light2, 000,030,100 )
					
	Local piv	=	CreatePivot		( )
	Local pit	=	CreatePivot		( piv )
	Local cam	=	CreateCamera	( pit )
					CameraRange		( cam, .1,1000 )
					MoveEntity		( cam, 0,0,-10 )
					CameraClsColor	( cam, 10,15,30 )
					
	Local Water	=	CreateSphere	( 64 )
					EntityColor		( Water, 000,100,255 )
					EntityAlpha		( Water, .5 )
					EntityShininess	( Water, .6 )
					EntityBlend		( Water, 3 )
					
	FlushMouse()
	
	Repeat
		
		; switch wireframe on keyhit F2
			If KeyHit(60) Then Wire = Not(Wire) : WireFrame Wire
			
		; Rotate Orbital Camera
			Msx# = MouseXSpeed()
			Msy# = MouseYSpeed()
			If MouseDown(2)
				TurnEntity piv, 0,-msx,0
				TurnEntity pit, +msy,0,0
			EndIf
			
		; Zoom
			PositionEntity cam, 0,0,EntityZ(cam, 0) * (1-0.05*Float(MouseZSpeed())),0
		
		RenderWorld()
			
			Color 255,128,000
			Text 10,10, "triangles : "+TrisRendered()
			Text 10,25, "< Right Mouse Down to rotate the orbital camera >"
			Text 10,40, "< Mouse wheel to zoom +/- >"
			Text 10,55, "< F2 - WireFrame >"
			
			Color 000,128,255
		Flip True
		
	Until KeyDown(1)
End
