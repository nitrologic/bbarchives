; ID: 525
; Author: Olive
; Date: 2002-12-09 08:23:51
; Title: YAL  improvments
; Description: A new light type, some bugs corrections

;
;	YAL - Yet Another Lightmapper
;	Version: 1.2
;
;	Please post any code improvement into blitz basic main site www.blitzbasic.co.nz, and include your data into history
;	Thanks to David Dawkins (startfox) and elias_t, that produced the base code for this file.
;
;	References:
;		http://members.net-tech.com.au/alaneb/lightmapping_tutorial.html  	(lightmap tutorial)
;		http://polygone.flipcode.com/tut_lightmap.htm						(lightmap tutorial)
;		http://www.blackpawn.com/texts/lightmaps/default.html				(lightmap packing)
;	
;
;
;	To Do:
;		- Terrain lightmap precision  (seems that the shadows are one or two lumels offset from the correct position)
;		- An weld(mesh) function that re-weld the vertices that have the same values
;		- Other light types, such as directional and spot
;
;
;	History:
;		1.0	(28/11/2002)	- Initial version (marcelo@greenlandstudios.com)
;		1.1 (30/11/2002)	- Generate surfaces with more than one triangle (marcelo)
;							  Per light attenuation and brightness (marcelo)
;							  Functions to apply, save and load the lightmap (marcelo)
;							  Lightmap sharing (starfox)

;Modifications made by O.Arguimbau : 
;
;		1.2 (9/12/2002)		- Bug correction in surface detection
;							- Lights now have types, POINT_LIGHT (normal lights), and a new one : SUN_LIGHT (a directional light without distances considerations)
;							- A little bug correction in loadlightmap
;							- Modification of the exemple code to show lightmapterrain in action
;							- Added the LightMapParams function to modify the light parameters (maybe usefull)
;
;I recommend to calculate objects lightmapping BEFORE adding a Sun_Light because it's VERY slow : Createlmlights(point_lights).....  LightmapMeshes... CreateLmlight(sun_light) Lightmapterrain
;but if you have a powerfull PC...you can try it.
;PS : I choose to upgrade the version to 1.2, maybe the original author have better ideas ??? So Marcelo, it's up to you.


; Call example 
LMExample()

Const FLT_MAX = 65535
Const SUN_LIGHT = 0
Const POINT_LIGHT = 1

; Set to True to draw the triangle edges on the texture
Const LM_DRAWTRIS = False

; Max polys per surface
Const LM_SURFTRIS = 256

; Num verts per poly
Const LM_VERTS = 2

; Angle between normals tolerance
Const LM_NORMAL_EPSILON# = 1.0

; Vertex distance tolerance
Const LM_VERTPOS_EPSILON# = 0.01

; Mapping plane
Const LMPLANE_XY = 0
Const LMPLANE_XZ = 1
Const LMPLANE_YZ = 2

; Mininum texture size
Const LM_MINTEXSIZE = 2

; Types
Type LMTriangle

	; Vertex info
	Field X#[LM_VERTS], Y#[LM_VERTS], Z#[LM_VERTS]
	Field U#[LM_VERTS], V#[LM_VERTS]
	Field VertIndex[LM_VERTS]
	
	; Normal
	Field NX#, NY#, NZ#

	; True if the triangle is part of a surface
	Field Linked
	
	; Original surface pointer
	Field Surf
End Type

Type LMSurface
	; Triangle list
	Field Tris.LMTriangle[LM_SURFTRIS]
	Field NTris%

	; Plane
	Field NX#, NY#, NZ#
	Field Plane%
	
	; UV Bound Box
	Field UMin#, UMax#, UDelta#
	Field VMin#, VMax#, VDelta#
	
	; UV to worldspace transformations
	Field UEdgeX#, UEdgeY#, UEdgeZ#
	Field VEdgeX#, VEdgeY#, VEdgeZ#
	Field OriginX#, OriginY#, OriginZ#
	
	; Misc
	Field Image
	Field ImageSize
End Type

; Wrapper to sort the surfaces
Type LMSortedSurface
	Field Surf.LMSurface
End Type

; Node for the packer
Type LMImgNode
	Field Child.LMImgNode[1]
	Field Surf.LMSurface
	
	Field X1%, Y1%
	Field X2%, Y2%
End Type

; Global parameters
Type LMParams
	Field AmbR, AmbG, AmbB
End Type

; Light
Type LMLight
	Field X#, Y#, Z#
	Field R#, G#, B#
	
	Field Range#
	Field Att#[2]
	Field Bright#
	Field TypeLight
End Type

; Store global parameters
Global g_LMParams.LMParams = Null


;	*****************
;	
;	Public functions
;	
;	*****************

; Create and setup global parameters
; AmbR, AmbG, AmbB is the ambient light color
Function BeginLightMap(AmbR = 0, AmbG = 0, AmbB = 0)
	g_LMParams = New LMParams
	
	g_LMParams\AmbR = AmbR
	g_LMParams\AmbG = AmbG
	g_LMParams\AmbB = AmbB
End Function

Function LightMapParams(AmbR = 0, AmbG = 0, AmbB = 0)
	g_LMParams\AmbR = AmbR
	g_LMParams\AmbG = AmbG
	g_LMParams\AmbB = AmbB
End Function

; Free parameters and stuff
Function EndLightMap()
	If g_LMParams <> Null
		; Delete all lights
		For Light.LMLight = Each LMLight
			Delete Light
		Next
		
		Delete g_LMParams
		g_LMParams = Null
	EndIf
End Function

; Create a new Light for lightmapping, only point lights until now
; x, y, z - world space coordinates
; r, g, b - Red, Green and Blue amounts (0..255)
; range  - Maximum distance that the light will affect
;   (only clamps the distance, If you want a falloff effect use the attenuation coefficients)
;
; bright - Light brightness
;
; att0, att1, att2 - Coefficients for light attenuation (control the falloff curve) 
;   lumel attenuation# = 1.0 / (att0 + (att1 * dist) + (att2 * dist^2)
;   where dist is the distance from light source to lumel

Function CreateLMLight.LMLight(x#, y#, z#, r#, g#, b#, range#, bright# = 10.0, att0# = 0, att1# = 1, att2# = 0, typelight = POINT_LIGHT)
	l.LMLight = New LMLight
	l\X = x : l\Y = y : l\Z = z
	l\R = r : l\G = g : l\B =b
	l\Range = range
	
	l\Bright = bright
	l\TypeLight = typelight
	
	l\Att[0] = att0
	l\Att[1] = att1
	l\Att[2] = att2
	
	Return l
End Function


; Apply an lightmap created with LightMapMesh or LightMapTerrain
Function ApplyLightMap(mesh, tex, layer = 1)
	If Not tex
		Return False
	EndIf

	EntityFX(mesh, 1)
	EntityTexture(mesh, tex, 0, layer)
	FreeTexture(tex)
	
	Return True
End Function

; Save to a bmp file and a luv file the information about a lightmapped entity
Function SaveLightMap(mesh, tex, imgfile$, luvfile$)
	If Not tex
		Return False
	EndIf

	SaveBuffer(TextureBuffer(tex), imgfile$)
	CreateLUVs(mesh, luvfile$, 1) 
End Function

; Load an image file and the luv file into the entity 
Function LoadLightMap(mesh, imgfile$, luvfile$, layer = 1)
	Unweld(mesh)

	If FileType(luvfile$)
		LoadLUVs(mesh, luvfile$) 
	EndIf

	tex = LoadTexture(imgfile$) 
	If tex
		EntityFX(mesh, 1)
		TextureCoords(tex, 1) 
		EntityTexture(mesh, tex, 0, layer)
		FreeTexture tex
	EndIf
End Function


; Assigns a 2nd channel planar mapping coordinates to the mesh and returns a packed texture that can be applied for lightmapping
;
; NOTES: 
;
; - The world objects must have EntityPickMode() set to produce shadows
; - The mesh is changed in the process (unwelded)
; - Lumel is the equivalent of an texel, but for lightmaps
; - lumelsize# is the size of the lumel in the world units to control the resolution of the lightmap
;		Example: If you use the metric system, a 0.2 lumelsize will create a lumel at each 20 centimeters
; - maxmapsize : maximum texture size that the lightmapper can pack (only used if needed)
; - blurradius : blur the resul image by this radius			
;
Function LightMapMesh(mesh, lumelsize# = 0.5, maxmapsize = 1024, blurradius = 1)

	SetBuffer(BackBuffer())
	Cls
	sMsg$="Creating lightmap object "+EntityName(mesh)+"..."
	font=LoadFont("Arial",20,True)
	SetFont font
	Color 250,250,150
	Text GraphicsWidth()/2,(GraphicsHeight()/2),sMsg$,True,True
	;progress bar
	progW=400
	progX=(GraphicsWidth()/2)-(progW/2)
	progY=(GraphicsHeight()/2)+50
	progH=20
	Color 0,0,200
	Rect progX-4,progY-4,progW+8,progH+8
	Flip

	UnWeld(mesh)

	NbSurf = CountSurfaces(mesh)
	; Run thru all surfaces & triangles storing the info into LMTriangle
	For surfcount = 1 To NbSurf

		surf = GetSurface(mesh, surfcount)
		For tricount = 0 To CountTriangles(surf) - 1

			Tri.LMTriangle = New LMTriangle

			For i = 0 To LM_VERTS
				vertn = TriangleVertex(surf, tricount, i)
				TFormPoint(VertexX(surf, vertn), VertexY(surf, vertn), VertexZ(surf, vertn), mesh, 0)
				Tri\X[i] = TFormedX() : Tri\Y[i] = TFormedY() : Tri\Z[i] = TFormedZ()
				
				Tri\VertIndex[i] = vertn
			Next
		
			Tri\Surf = Surf
	
			GetTriangleNormal(Tri\X[0], Tri\Y[0], Tri\Z[0], Tri\X[1], Tri\Y[1], Tri\Z[1], Tri\X[2], Tri\Y[2], Tri\Z[2])
			Tri\NX = TriangleNormalX() : Tri\NY = TriangleNormalY() : Tri\NZ = TriangleNormalZ()
		Next	
	Next

	; Create the surfaces
	SurfaceCount = 0
	While True
		LMSurf.LMSurface = New LMSurface
		
		SurfaceCount = SurfaceCount + 1
		
		; Find the first unlinked triangle
		For Tri.LMTriangle = Each LMTriangle
			If Not Tri\Linked
				Exit
			EndIf
		Next
		
		; No more unlinked tris
		If Tri = Null 
			Exit
		EndIf
		
		Tri\Linked = True
		LMSurf\Tris[LMSurf\NTris] = Tri
		LMSurf\NTris = LMSurf\NTris + 1
				
		; Search for adjacent tri's with the same caracteristics and append to list

		; Three waves to assure that all the poly's will be get
		For Wave = 1 To 3
			For STri.LMTriangle = Each LMTriangle
				If Not STri\Linked
	
					; Compare the triangle normal 
;Bug ???
;					Ang# = Abs((STri\NX * Tri\NX) + (STri\NY * Tri\NY) + (STri\NZ * Tri\NZ))

					Ang# = ((STri\NX * Tri\NX) + (STri\NY * Tri\NY) + (STri\NZ * Tri\NZ))
					If ACos(Ang) <= LM_NORMAL_EPSILON
						
						NSharedVerts = 0
						
						; Check if it shares vertices with one of the current surface triangles
						For i = 0 To LMSurf\NTris-1
							VTri.LMTriangle = LMSurf\Tris[i]
							
							For j = 0 To LM_VERTS
								For k = 0 To LM_VERTS
									DX# = STri\X[j] - VTri\X[k]
									DY# = STri\Y[j] - VTri\Y[k]
									DZ# = STri\Z[j] - VTri\Z[k]
								
									Dist# = Sqr(DX*DX + DY*DY + DZ*DZ)
								
									If Dist <= LM_VERTPOS_EPSILON
										NSharedVerts = NSharedVerts + 1
										Exit
									EndIf
								Next
							Next
						Next
						
						If NSharedVerts > 0
							STri\Linked = True		
							LMSurf\Tris[LMSurf\NTris] = STri
							LMSurf\NTris = LMSurf\NTris + 1
					
							If LMSurf\NTris > LM_SURFTRIS
								Exit
							EndIf
						EndIf
					EndIf
				EndIf
			Next
			
			If LMSurf\NTris > LM_SURFTRIS
				Exit
			EndIf
		Next
		
		LMSetupSurface(LMSurf, lumelsize)
	Wend
	
	CurrentCount = 0
	For LMSurf.LMSurface = Each LMSurface
	
		CurrentCount = CurrentCount + 1

		If KeyHit(1) Then End
		
		; Create the light texture
		LMLightSurface(LMSurf, lumelsize)
		
		; Blur resulting image
		If blurradius > 0
			LMBlurImage(LMSurf\Image, blurradius)
		EndIf
		
		;Progression
		SetBuffer BackBuffer()
		Color 0,0,200
		Cls
		Rect progX-4,progY-4,progW+8,progH+8
		
		Color 255,0,0
		Rect progX,progY,progW/Float(SurfaceCount)*Float(CurrentCount),progH
		
		Color 250,250,150
		Text GraphicsWidth()/2,(GraphicsHeight()/2),sMsg$,True,True
		
		Flip
		
	Next
			
	; First sort it by image size, larger images enter first
	For LMSurf.LMSurface = Each LMSurface
		
		; Search for a lower image size
		For SLMSurf.LMSortedSurface = Each LMSortedSurface
			If SLMSurf\Surf\ImageSize <= LMSurf\ImageSize
				Exit
			EndIf
		Next
		
		NLMSurf.LMSortedSurface = New LMSortedSurface
		NLMSurf\Surf = LMSurf
		
		If SLMSurf <> Null
			Insert NLMSurf Before SLMSurf
		EndIf
	Next
	
	; Get the mininum map size possible
	lmapsize% = LMPacker_FitTexSize(maxmapsize)
	
	; Pack into a big texture
	Tex = LMPacker_Pack(lmapsize%)
	
	; Free temporary stuff
	For LMSurf.LMSurface = Each LMSurface
		FreeImage(LMSurf\Image)
		Delete LMSurf
	Next
	
	Delete Each LMSortedSurface
	Delete Each LMTriangle
	
	Return Tex
End Function

;
;	Same as the lightmapmesh, but for terrains. detail% is the texture map size
;
Function LightMapTerrain(terrain, detail% = 0, blurradius% = 1)

	SetBuffer(BackBuffer())
	Cls
	sMsg$="Creating terrain lightmap..."
	font=LoadFont("Arial",20,True)
	SetFont font
	Color 250,250,150
	Text GraphicsWidth()/2,(GraphicsHeight()/2),sMsg$+" 1",True,True
	;progress bar
	progW=400
	progX=(GraphicsWidth()/2)-(progW/2)
	progY=(GraphicsHeight()/2)+50
	progH=20
	Color 0,0,200
	Rect progX-4,progY-4,progW+8,progH+8
	Flip

	TSize# = TerrainSize(terrain)

	If detail = 0
		detail = TSize
	EndIf
	
	; Get the entity scale
	vx# = GetMatElement(terrain, 0, 0)
	vy# = GetMatElement(terrain, 0, 1)
	vz# = GetMatElement(terrain, 0, 2)
	XScale# = Sqr(vx*vx + vy*vy + vz*vz)
	vx# = GetMatElement(terrain, 1, 0)
	vy# = GetMatElement(terrain, 1, 1)
	vz# = GetMatElement(terrain, 1, 2)
	YScale# = Sqr(vx*vx + vy*vy + vz*vz)
	vx# = GetMatElement(terrain, 2, 0)
	vy# = GetMatElement(terrain, 2, 1)
	vz# = GetMatElement(terrain, 2, 2)
	ZScale# = Sqr(vx*vx + vy*vy + vz*vz)
	
	; Relation between detail and texture size
	Scale# = 1
	If detail < TSize
		Scale# = Float(detail)/Float(TSize)
	EndIf

	LMSize = detail
	Img = CreateImage(LMSize, LMSize)

	ImgBuf = ImageBuffer(Img)
	SetBuffer(ImgBuf)
	
	; Set the ambient light
	ClsColor(g_LMParams\AmbR, g_LMParams\AmbG, g_LMParams\AmbB)
	Cls()
	ClsColor(0, 0, 0)
	
	LockBuffer(ImgBuf)
	
	LightPivot = CreatePivot()

	LumelPivot = CreatePivot()
	EntityPickMode(LumelPivot, 1)
	EntityRadius(LumelPivot, 0.625)
	
	xpos# = EntityX(terrain) : ypos# = EntityY(terrain) : zpos# = EntityZ(terrain)
	
	cptlight = 0
	For Light.LMLight = Each LMLight
	
		cptlight = cptlight + 1
		
		PositionEntity(LightPivot, Light\X, Light\Y, Light\Z)
		
		;Added by O.Arguimbau : Determination of the light influence (aproximation) with a simple vertical projection (NEED OPTIMISATION/CORRECTION ?)
		If (Light\TypeLight = POINT_LIGHT)
			zmin = (LMSize-1)-Ceil(((Light\Z+Light\Range)-zpos)/zscale) - 2
			zmax = (LMSize-1)-Floor(((Light\Z-Light\Range)-zpos)/zscale) + 2
	
			xmin = Floor(((Light\X-Light\Range)-xpos)/xscale) - 2
			xmax = Ceil(((Light\X+Light\Range)-xpos)/xscale) + 2
	
			If zmin < 0 Then zmin = 0
			If zmax > LMSize-1 Then zmax = LMSize-1
					
			If xmin < 0 Then xmin = 0
			If xmax > LMSize-1 Then xmax = LMSize-1
		Else
			xmin = 0 : zmin = 0
			xmax = LMSize-1 : zmax = LMSize-1
		EndIf

		For z% = zmin To zmax
			If KeyHit(1) Then End
			For x% = xmin To xmax

				zp% = TSize - z
				y# = TerrainHeight(terrain, x+1, zp)
				
				LumX# = (xpos + Float(x)  * XScale) / Scale
				LumY# = (ypos + Float(y)  * YScale) / Scale
				LumZ# = (zpos + Float(zp) * ZScale) / Scale

				PositionEntity(LumelPivot, LumX, LumY, LumZ)
				Dist# = EntityDistance(LightPivot, LumelPivot)
				
				Select Light\TypeLight
					;Added by O.Arguimbau : Sun light is a directionnal light, all rays are parallels
					Case SUN_LIGHT
						;Check for visibility
						xi# = EntityX(LumelPivot) : yi# = EntityY(LumelPivot) : zi# = EntityZ(LumelPivot)
						PositionEntity LightPivot, Light\X + xi, Light\Y + yi, Light\Z + zi
								
						If EntityVisible(LumelPivot,LightPivot)
                            Intensity# = Light\Bright
		
							ARGB = ReadPixelFast(x, z) And $FFFFFF
							R = (ARGB Shr 16 And %11111111)
							G = (ARGB Shr 8 And %11111111)
							B = (ARGB And %11111111)
							
							R = R + (Light\R * Intensity)
							G = G + (Light\G * Intensity)
							B = B + (Light\B * Intensity)
							
							If R > 255 Then R = 255
							If G > 255 Then G = 255
							If B > 255 Then B = 255
							
							RGB = B Or (G Shl 8) Or (R Shl 16)
							WritePixelFast(x, z, RGB)
						EndIf
					
					Case POINT_LIGHT
						; If this light can light this lumel		
						If (Dist <= Light\Range) And (Dist > 0)
							LMLightProcess(x, z, Light, LumX, LumY, LumZ, Dist, 1.0, LumelPivot)
						EndIf ; Dist < Light\Range
				End Select								
			Next ; x
			;Progression
			UnlockBuffer(ImgBuf)
			
			SetBuffer(BackBuffer())
			ClsColor 0,0,0:Cls
			Color 0,0,200
			Rect progX-4,progY-4,progW+8,progH+8
			
			Color 255,0,0
			Rect progX,progY,progW/Float(zmax)*Float(z+1),progH
			
			Color 250,250,150
			Text GraphicsWidth()/2,(GraphicsHeight()/2),sMsg$ + " " + cptLight,True,True
			
			Flip
			
			SetBuffer (ImgBuf)
			LockBuffer(ImgBuf)		
		Next ; z
	Next
	
	UnlockBuffer(ImgBuf)
	
	; Blur resulting image
	If blurradius > 0
		LMBlurImage(Img, blurradius)
	EndIf
	
	Tex = CreateTexture(LMSize, LMSize, 512)
	CopyRect(0, 0, LMSize, LMSize, 0, 0, ImageBuffer(Img), TextureBuffer(Tex))
	
	TextureCoords(Tex, 1)
	ScaleTexture(Tex, TSize, TSize)

	FreeImage(Img)
	
	SetBuffer(BackBuffer())
	
	FreeEntity(LightPivot)
	FreeEntity(LumelPivot)
	
	Return Tex
End Function






;	******************
;
;	Private functions
;
;   ******************



;  Lightmap packing functions
Function LMPacker_Pack(lmapsize)
	Tex = CreateTexture(lmapsize, lmapsize, 512)
	SetBuffer(TextureBuffer(Tex))	
	
	LMRoot.LMImgNode = New LMImgNode
	LMRoot\X1 = 0 : LMRoot\Y1 = 0
	LMRoot\X2 = lmapsize : LMRoot\Y2 = lmapsize
	LMRoot\Surf = Null
	
	SurfCnt = 0
	
	For SLMSurf.LMSortedSurface = Each LMSortedSurface
		
		; Insert in the best location
		Img.LMImgNode = LMPacker_Insert(LMRoot, SLMSurf\Surf)
		
		If Img <> Null
		
			LMSurf.LMSurface = Img\Surf
			
			IW = ImageWidth(LMSurf\Image)
			IH = ImageHeight(LMSurf\Image)
			
			CopyRect(0, 0, IW, IH, Img\X1, Img\Y1, ImageBuffer(LMSurf\Image), TextureBuffer(Tex))
			
			; Scale the original UV's to the new position and scale
			DX# = Float(Img\X1) / Float(lmapsize)
			DY# = Float(Img\Y1) / Float(lmapsize)
			
			ScaleU# = Float(IW) / Float(lmapsize)
			ScaleV# = Float(IH) / Float(lmapsize)
			
			For i = 0 To LMSurf\NTris-1
				For j = 0 To LM_VERTS
					LMSurf\Tris[i]\U[j] = (LMSurf\Tris[i]\U[j] * ScaleU) + DX
					LMSurf\Tris[i]\V[j] = (LMSurf\Tris[i]\V[j] * ScaleV) + DY
					
					VertexTexCoords(LMSurf\Tris[i]\Surf, LMSurf\Tris[i]\VertIndex[j], LMSurf\Tris[i]\U[j], LMSurf\Tris[i]\V[j], 0, 1)
				Next
			Next
			
			; Draw debug stuff if needed
			If LM_DRAWTRIS
				; Triangles
				Color(255, 255, 255)
				For i = 0 To LMSurf\NTris-1
					x1% = LMSurf\Tris[i]\U[0] * Float(lmapsize)
					y1% = LMSurf\Tris[i]\V[0] * Float(lmapsize)
					x2% = LMSurf\Tris[i]\U[1] * Float(lmapsize)
					y2% = LMSurf\Tris[i]\V[1] * Float(lmapsize)
					Line(x1, y1, x2, y2)
					x1% = LMSurf\Tris[i]\U[1] * Float(lmapsize)
					y1% = LMSurf\Tris[i]\V[1] * Float(lmapsize)
					x2% = LMSurf\Tris[i]\U[2] * Float(lmapsize)
					y2% = LMSurf\Tris[i]\V[2] * Float(lmapsize)
					Line(x1, y1, x2, y2)
					x1% = LMSurf\Tris[i]\U[2] * Float(lmapsize)
					y1% = LMSurf\Tris[i]\V[2] * Float(lmapsize)
					x2% = LMSurf\Tris[i]\U[0] * Float(lmapsize)
					y2% = LMSurf\Tris[i]\V[0] * Float(lmapsize)
					Line(x1, y1, x2, y2)
				Next
			EndIf
			
			SurfCnt = SurfCnt + 1
		Else
			DebugLog("Lightmap doesn't fit into the maxmapsize, increase the lumelsize or increase the maxmapsize")
			Exit
		EndIf
	Next
	
	TextureCoords(Tex, 1)
	
	SetBuffer(BackBuffer())

	For LMNode.LMImgNode = Each LMImgNode
		Delete LMNode
	Next
	
	Return Tex
End Function

;
;  Find of the minimum texture size up to maxmapsize% that will fit all the lightmap images
;
Function LMPacker_FitTexSize%(maxmapsize%)
	lmapsize = LM_MINTEXSIZE
	
	While lmapsize <= maxmapsize
		LMRoot.LMImgNode = New LMImgNode
		LMRoot\X1 = 0 : LMRoot\Y1 = 0
		LMRoot\X2 = lmapsize : LMRoot\Y2 = lmapsize
		LMRoot\Surf = Null
		
		bFit = True
		
		For SLMSurf.LMSortedSurface = Each LMSortedSurface
			Img.LMImgNode = LMPacker_Insert(LMRoot, SLMSurf\Surf)
			
			If Img = Null
				bFit = False
				Exit
			EndIf
		Next
		
		For LMNode.LMImgNode = Each LMImgNode
			Delete LMNode
		Next
		
		If bFit
			Return lmapsize
		EndIf
		
		lmapsize = lmapsize * 2
	Wend
	
	Return maxmapsize
End Function


;
;  Recursive function to pack the lightmaps
;
Function LMPacker_Insert.LMImgNode(Node.LMImgNode, LMSurf.LMSurface)
	; We are not in a leaf
	If (Node\Child[0] <> Null) And (Node\Child[1] <> Null)
		
		; Try first child
		NewNode.LMImgNode = LMPacker_Insert(Node\Child[0], LMSurf)
		If NewNode <> Null Return NewNode
		
		; No room, use the second
		Return LMPacker_Insert(Node\Child[1], LMSurf)
	Else
		; Already have a lightmap here
		If Node\Surf <> Null 
		
			; If the lightmap is the same image use it
			If LMImageAlike(Node\Surf\Image, LMSurf\Image)
				Node\Surf = LMSurf
				Return Node
			Else
				Return Null
			EndIf
		EndIf 
		
		IW% = ImageWidth(LMSurf\Image)
		IH% = ImageHeight(LMSurf\Image)
		
		NW% = Node\X2 - Node\X1
		NH% = Node\Y2 - Node\Y1
		
		; Check if image doesn't fit this node
		If (IW > NW) Or (IH > NH)
			Return Null
		EndIf

		; If it fits perfectly
		If (IW = NW) And (IH = NH)
			Node\Surf = LMSurf
			Return Node
		EndIf
	
		; We need to spit the node
		Node\Child[0] = New LMImgNode
		Node\Child[1] = New LMImgNode
		
		DW% = NW - IW
		DH% = NH - IH
		
		; Choose the best axis to split
		If DW > DH
			Node\Child[0]\X1 = Node\X1
			Node\Child[0]\Y1 = Node\Y1
			Node\Child[0]\X2 = Node\X1 + IW
			Node\Child[0]\Y2 = Node\Y2

			Node\Child[1]\X1 = Node\X1 + IW
			Node\Child[1]\Y1 = Node\Y1
			Node\Child[1]\X2 = Node\X2
			Node\Child[1]\Y2 = Node\Y2
		Else
			Node\Child[0]\X1 = Node\X1
			Node\Child[0]\Y1 = Node\Y1
			Node\Child[0]\X2 = Node\X2
			Node\Child[0]\Y2 = Node\Y1 + IH

			Node\Child[1]\X1 = Node\X1
			Node\Child[1]\Y1 = Node\Y1 + IH
			Node\Child[1]\X2 = Node\X2
			Node\Child[1]\Y2 = Node\Y2
		EndIf
		
		Return LMPacker_Insert(Node\Child[0], LMSurf)
	EndIf
End Function

Function LMImageAlike(img1, img2)
	;Check if imagess are congruent
	width1 = ImageWidth(img1)
	width2 = ImageWidth(img2)
	If width1 <> width2 Then Return False
	
	height1 = ImageHeight(img1)
	height2 = ImageHeight(img2)
	If height1 <> height2 Then Return 0
	
	LockBuffer(ImageBuffer(img1))
	LockBuffer(ImageBuffer(img2))
	
	For y = 0 To height1-1
		For x = 0 To width1-1
			rgb1 = ReadPixelFast(x, y, ImageBuffer(img1))
			rgb2 = ReadPixelFast(x, y, ImageBuffer(img2))
			If rgb1 <> rgb2
				UnlockBuffer(ImageBuffer(img1))
				UnlockBuffer(ImageBuffer(img2))
				Return 0
			EndIf
		Next
	Next
	UnlockBuffer(ImageBuffer(img1))
	UnlockBuffer(ImageBuffer(img2))

	Return True
End Function


;
;	Setup the surface
;
Function LMSetupSurface.LMSurface(LMSurf.LMSurface, lumelsize#)

	; Get the averaged normal
	NX# = 0 : NY# = 0 : NZ# = 0
	For i = 0 To LMSurf\NTris-1
		GetTriangleNormal(LMSurf\Tris[i]\X[0], LMSurf\Tris[i]\Y[0], LMSurf\Tris[i]\Z[0], LMSurf\Tris[i]\X[1], LMSurf\Tris[i]\Y[1], LMSurf\Tris[i]\Z[1], LMSurf\Tris[i]\X[2], LMSurf\Tris[i]\Y[2], LMSurf\Tris[i]\Z[2])
	
		NX = NX + TriangleNormalX()
		NY = NY + TriangleNormalY()
		NZ = NZ + TriangleNormalZ()
	Next
	
	LMSurf\NX = NX / Float(LMSurf\NTris)
	LMSurf\NY = NY / Float(LMSurf\NTris)
	LMSurf\NZ = NZ / Float(LMSurf\NTris)
	
	; Find out the best plane to map on (which have the largest normal)
	NX# = Abs(LMSurf\NX) :	NY# = Abs(LMSurf\NY) :	NZ# = Abs(LMSurf\NZ)
	
	If (NZ > NX) And (NZ > NY)
		LMSurf\Plane = LMPLANE_XY
	Else If (NY > NX) And (NY > NZ)
		LMSurf\Plane = LMPLANE_XZ
	Else
		LMSurf\Plane = LMPLANE_YZ
	EndIf

	Select LMSurf\Plane
		Case LMPLANE_XY
			For i = 0 To LMSurf\NTris-1
				For j = 0 To LM_VERTS
					LMSurf\Tris[i]\U[j] = LMSurf\Tris[i]\X[j]
					LMSurf\Tris[i]\V[j] = LMSurf\Tris[i]\Y[j]
				Next
			Next
	
		Case LMPLANE_XZ
			For i = 0 To LMSurf\NTris-1
				For j = 0 To LM_VERTS
					LMSurf\Tris[i]\U[j] = LMSurf\Tris[i]\X[j]
					LMSurf\Tris[i]\V[j] = LMSurf\Tris[i]\Z[j]
				Next
			Next
	
		Case LMPLANE_YZ
			For i = 0 To LMSurf\NTris-1
				For j = 0 To LM_VERTS
					LMSurf\Tris[i]\U[j] = LMSurf\Tris[i]\Y[j]
					LMSurf\Tris[i]\V[j] = LMSurf\Tris[i]\Z[j]
				Next
			Next
	End Select 
	
	; Measure the UV bound box
	LMSurf\UMin = LMSurf\Tris[0]\U[0] : LMSurf\UMax = LMSurf\Tris[0]\U[0]
	LMSurf\VMin = LMSurf\Tris[0]\V[0] : LMSurf\VMax = LMSurf\Tris[0]\V[0]
	
	For i = 0 To LMSurf\NTris-1
		For j = 0 To LM_VERTS
			If LMSurf\Tris[i]\U[j] < LMSurf\UMin Then LMSurf\UMin = LMSurf\Tris[i]\U[j]
			If LMSurf\Tris[i]\U[j] > LMSurf\UMax Then LMSurf\UMax = LMSurf\Tris[i]\U[j]
	
			If LMSurf\Tris[i]\V[j] < LMSurf\VMin Then LMSurf\VMin = LMSurf\Tris[i]\V[j]
			If LMSurf\Tris[i]\V[j] > LMSurf\VMax Then LMSurf\VMax = LMSurf\Tris[i]\V[j]
		Next
	Next
	
	; Reduce black borders
	DT# = lumelsize
	LMSurf\UMax = LMSurf\UMax + DT
	LMSurf\VMax = LMSurf\VMax + DT
	LMSurf\UMin = LMSurf\UMin - DT
	LMSurf\VMin = LMSurf\VMin - DT
	
	; Bound Box size
	LMSurf\UDelta = LMSurf\UMax - LMSurf\UMin
	LMSurf\VDelta = LMSurf\VMax - LMSurf\VMin
	
	; Normalize the UV's, making it range from 0.0 to 1.0
	For i = 0 To LMSurf\NTris-1
		For j = 0 To LM_VERTS
			; Translate it to the origin
			LMSurf\Tris[i]\U[j] = LMSurf\Tris[i]\U[j] - LMSurf\UMin
			LMSurf\Tris[i]\V[j] = LMSurf\Tris[i]\V[j] - LMSurf\VMin
		
			; Normalize
			LMSurf\Tris[i]\U[j] = LMSurf\Tris[i]\U[j] / LMSurf\UDelta
			LMSurf\Tris[i]\V[j] = LMSurf\Tris[i]\V[j] / LMSurf\VDelta
		Next
	Next
	
	;
	; Calculate the UV space to world space equations
	;
	
	; Distance of the plane
	Dist# = -(LMSurf\NX * LMSurf\Tris[0]\X[0] + LMSurf\NY * LMSurf\Tris[0]\Y[0] + LMSurf\NZ * LMSurf\Tris[0]\Z[0])
	
	Local UVX#, UVY#, UVZ#
	Local V1X#, V1Y#, V1Z#
	Local V2X#, V2Y#, V2Z#
	
	; Messy stuff based on the plane equation:  Ax + By + Cz + D = 0
	Select LMSurf\Plane

		Case LMPLANE_XY
		Z# = -(LMSurf\NX * LMSurf\UMin + LMSurf\NY * LMSurf\VMin + Dist) / LMSurf\NZ
		UVX# = LMSurf\UMin : UVY# = LMSurf\VMin : UVZ# = Z
		
		Z# = -(LMSurf\NX * LMSurf\UMax + LMSurf\NY * LMSurf\VMin + Dist) / LMSurf\NZ
		V1X# = LMSurf\UMax : V1Y# = LMSurf\VMin : V1Z# = Z
	
		Z# = -(LMSurf\NX * LMSurf\UMin + LMSurf\NY * LMSurf\VMax + Dist) / LMSurf\NZ
		V2X# = LMSurf\UMin : V2Y# = LMSurf\VMax : V2Z# = Z

		Case LMPLANE_XZ
		Y# = -(LMSurf\NX * LMSurf\UMin + LMSurf\NZ * LMSurf\VMin + Dist) / LMSurf\NY
		UVX# = LMSurf\UMin : UVY# = Y : UVZ# = LMSurf\VMin
		
		Y# = -(LMSurf\NX * LMSurf\UMax + LMSurf\NZ * LMSurf\VMin + Dist) / LMSurf\NY
		V1X# = LMSurf\UMax : V1Y# = Y : V1Z# = LMSurf\VMin
	
		Y# = -(LMSurf\NX * LMSurf\UMin + LMSurf\NZ * LMSurf\VMax + Dist) / LMSurf\NY
		V2X# = LMSurf\UMin : V2Y# = Y : V2Z# = LMSurf\VMax

	
		Case LMPLANE_YZ
		X# = -(LMSurf\NY * LMSurf\UMin + LMSurf\NZ * LMSurf\VMin + Dist) / LMSurf\NX
		UVX# = X : UVY# = LMSurf\UMin : UVZ# = LMSurf\VMin
		
		X# = -(LMSurf\NY * LMSurf\UMax + LMSurf\NZ * LMSurf\VMin + Dist) / LMSurf\NX
		V1X# = X : V1Y# = LMSurf\UMax : V1Z# = LMSurf\VMin
	
		X# = -(LMSurf\NY * LMSurf\UMin + LMSurf\NZ * LMSurf\VMax + Dist) / LMSurf\NX
		V2X# = X : V2Y# = LMSurf\UMin : V2Z# = LMSurf\VMax
	
	End Select

	LMSurf\UEdgeX = V1X - UVX : LMSurf\UEdgeY = V1Y - UVY : LMSurf\UEdgeZ = V1Z - UVZ
	LMSurf\VEdgeX = V2X - UVX : LMSurf\VEdgeY = V2Y - UVY : LMSurf\VEdgeZ = V2Z - UVZ
	
	LMSurf\OriginX = UVX# : LMSurf\OriginY = UVY# : LMSurf\OriginZ = UVZ#
	Return LMSurf
End Function

;
;	Create the lightmap texture
;
Function LMLightSurface(LMSurf.LMSurface, lumelsize#)
	; Create image size based on the lumel density
	LMSizeX% = (LMSurf\UDelta / lumelsize)
	LMSizeY% = (LMSurf\VDelta / lumelsize)
	
	; Mininum texture size
	If LMSizeX < LM_MINTEXSIZE Then LMSizeX = LM_MINTEXSIZE
	If LMSizeY < LM_MINTEXSIZE Then LMSizeY = LM_MINTEXSIZE

	LMSurf\Image = CreateImage(LMSizeX, LMSizeY)
 	LMSurf\ImageSize = LMSizeX * LMSizeY
	
	ImgBuf = ImageBuffer(LMSurf\Image)
	SetBuffer(ImgBuf)
	
	; Set the ambient light
	ClsColor(g_LMParams\AmbR, g_LMParams\AmbG, g_LMParams\AmbB)
	Cls()
	ClsColor(0, 0, 0)
	
	LockBuffer(ImgBuf)
	
	LightPivot = CreatePivot()

	LumelPivot = CreatePivot()
	EntityPickMode(LumelPivot, 1)
	EntityRadius(LumelPivot, 0.625)	; Found by trial and error
	
	For Light.LMLight = Each LMLight
	
		PositionEntity(LightPivot, Light\X, Light\Y, Light\Z)
		
		For y% = 0 To LMSizeY-1
			For x% = 0 To LMSizeX-1
				
				; Find the UV
				u# = Float(x) / Float(LMSizeX)
				v# = Float(y) / Float(LMSizeY)
				
				; Transform to world coordinates
				N_UEdgeX# = LMSurf\UEdgeX * u#  :  N_UEdgeY# = LMSurf\UEdgeY * u#  :  N_UEdgeZ# = LMSurf\UEdgeZ * u#
				N_VEdgeX# = LMSurf\VEdgeX * v#  :  N_VEdgeY# = LMSurf\VEdgeY * v#  :  N_VEdgeZ# = LMSurf\VEdgeZ * v#
				
				LumX# = (LMSurf\OriginX + N_UEdgeX + N_VEdgeX)
				LumY# = (LMSurf\OriginY + N_UEdgeY + N_VEdgeY)
				LumZ# = (LMSurf\OriginZ + N_UEdgeZ + N_VEdgeZ)
				
				PositionEntity(LumelPivot, LumX, LumY, LumZ)
				Dist# = EntityDistance(LightPivot, LumelPivot)
				
				Select Light\TypeLight
				
					Case SUN_LIGHT

						; Normal vector between lumel and light
						NX# = (-Light\X);/Dist			;Sun_light is not affected by distance, maybe i can add a directionnal light that is influenced by distance...
						NY# = (-Light\Y);/Dist			;Directionnal_light type maybe :)
						NZ# = (-Light\Z);/Dist
	
						; Dot product to find the cosine angle between the surface normal and incident light normal
						CosAngle# = (NX * LMSurf\NX) + (NY * LMSurf\NY) + (NZ * LMSurf\NZ)
						
						; Poly face front of the light
						If CosAngle > 0
						
							NHits = 0

							; Center pick
							dx# = - Light\X
							dy# = - Light\Y
							dz# = - Light\Z
							If LinePick(Light\X+LumX, Light\Y+LumY, Light\Z+LumZ, dx*FLT_MAX, dy*FLT_MAX, dz*FLT_MAX, 0) = LumelPivot
								NHits = NHits + 1
							EndIf
						
							If NHits > 0
								;
								; Add the incident light the pixel
								;
								; Lambert + Attenuation + Shadow 
								Intensity# = (Light\Bright * CosAngle)
								
								If Intensity < 0.0 Then Intensity = 0.0
								If Intensity > 1.0 Then Intensity = 1.0
								
								ARGB = ReadPixelFast(x, y) And $FFFFFF
								R = (ARGB Shr 16 And %11111111)
								G = (ARGB Shr 8 And %11111111)
								B = (ARGB And %11111111)
								
								R = R + (Light\R * Intensity)
								G = G + (Light\G * Intensity)
								B = B + (Light\B * Intensity)
								
								If R > 255 Then R = 255
								If G > 255 Then G = 255
								If B > 255 Then B = 255
								
								RGB = B Or (G Shl 8) Or (R Shl 16)
								WritePixelFast(x, y, RGB)
	
							EndIf	; Visible
	
						EndIf ; CosAngle > 0
					
					Case POINT_LIGHT								
						; If this light can light this lumel		
						If (Dist <= Light\Range) And (Dist > 0)
							
							; Normal vector between lumel and light
							NX# = (LumX-Light\X) / Dist
							NY# = (LumY-Light\Y) / Dist
							NZ# = (LumZ-Light\Z) / Dist
		
							; Dot product to find the cosine angle between the surface normal and incident light normal
							CosAngle# = (NX * LMSurf\NX) + (NY * LMSurf\NY) + (NZ * LMSurf\NZ)
							
							; Poly face front of the light
							If CosAngle > 0
								LMLightProcess(x, y, Light, LumX, LumY, LumZ, Dist, CosAngle, LumelPivot)
							EndIf 
							
						EndIf ; Dist < Light\Range
					End Select
			Next ; x
		Next ; y

	Next ;Light
	
	UnlockBuffer(ImgBuf)

	SetBuffer(BackBuffer())
	
	FreeEntity(LightPivot)
	FreeEntity(LumelPivot)
End Function


Function LMLightProcess(x%, y%, Light.LMLight, LumX#, LumY#, LumZ#, Dist#, CosAngle#, LumelPivot)
	NHits = 0
	
	; Center pick
	dx# = LumX - Light\X
	dy# = LumY - Light\Y
	dz# = LumZ - Light\Z
	If LinePick(Light\X, Light\Y, Light\Z, dx, dy, dz, 0) = LumelPivot
		NHits = NHits + 1
	EndIf

	If NHits > 0
		;
		; Add the incident light the pixel
		;

		; Measure attenuation
		Att# = 1 / (Light\Att[0] + (Light\Att[1] * Dist) + (Light\Att[2] * Dist * Dist))
				
		; Lambert + attenuation
		Intensity# = (Light\Bright * CosAngle) * Att
		
		If Intensity < 0.0 Then Intensity = 0.0
		If Intensity > 1.0 Then Intensity = 1.0
		
		ARGB = ReadPixelFast(x, y) And $FFFFFF
		R = (ARGB Shr 16 And %11111111)
		G = (ARGB Shr 8 And %11111111)
		B = (ARGB And %11111111)
		
		R = R + (Light\R * Intensity)
		G = G + (Light\G * Intensity)
		B = B + (Light\B * Intensity)
		
		If R > 255 Then R = 255
		If G > 255 Then G = 255
		If B > 255 Then B = 255
		
		RGB = B Or (G Shl 8) Or (R Shl 16)
		WritePixelFast(x, y, RGB)

	EndIf	; Visible
End Function

;
;	Blur an image using radius
;
Function LMBlurImage(Image, radius = 1)
	TmpImg = CopyImage(Image)
	
	TmpBuf = ImageBuffer(TmpImg)
	ImgBuf = ImageBuffer(Image)
	
	LockBuffer(ImgBuf)
	LockBuffer(TmpBuf)
	
	W% = ImageWidth(Image)
	H% = ImageHeight(Image)

	; Go thru all the pixels
	For y% = 0 To H-1
		For x% = 0 To W-1
		
			; Measure the box to get the pixel samples from
			ix1 = x - radius
			iy1 = y - radius
			ix2 = x + radius
			iy2 = y + radius
			
			; Prevent it going out of bound
			If ix1 < 0 Then ix1 = 0
			If iy1 < 0 Then iy1 = 0
			If ix2 > W-1 Then ix2 = W-1
			If iy2 > H-1 Then iy2 = H-1
			
			r = 0 : g = 0 : b = 0
			num = 0
			
			; Run thru all the sampled box
			For y2% = iy1 To iy2
				For x2% = ix1 To ix2
					
					; Sum the sampled pixel 
					argb = ReadPixelFast(x2, y2, TmpBuf) And $FFFFFF
					ar = (argb Shr 16 And %11111111)
					ag = (argb Shr 8 And %11111111)
					ab = (argb And %11111111)
					
					r = r + ar
					g = g + ag
					b = b + ab
					
					num = num + 1
				Next	
			Next
			
			; Get the medium value
			r = r / num
			g = g / num
			b = b / num
			
			; Clamp
			If r > 255 Then r = 255
			If g > 255 Then g = 255
			If b > 255 Then b = 255

			rgb = b Or (g Shl 8) Or (r Shl 16)
			WritePixelFast(x, y, rgb, ImgBuf)

		Next
	Next
	
	UnlockBuffer(TmpBuf)
	UnlockBuffer(ImgBuf)
	
	FreeImage(TmpBuf)
End Function


;
; Helper functions
;

Global g_TriNormalX#, g_TriNormalY#, g_TriNormalZ#

Function GetTriangleNormal(x1#, y1#, z1#, x2#, y2#, z2#, x3#, y3#, z3#)
  	ux# = x1# - x2#
   	uy# = y1# - y2#
  	uz# = z1# - z2#
    vx# = x3# - x2#
    vy# = y3# - y2#
   	vz# = z3# - z2#

	nx# = (uy# * vz#) - (vy# * uz#)
	ny# = (uz# * vx#) - (vz# * ux#)
	nz# = (ux# * vy#) - (vx# * uy#)

	; Normalize it
	NormLen# = Sqr((nx*nx) + (ny*ny) + (nz*nz))
	If NormLen > 0
		nx = nx/NormLen : ny = ny/NormLen: nz = nz/NormLen
	Else
		nx = 0 : ny = 0 : nz = 1
	EndIf

	g_TriNormalX = nx
	g_TriNormalY = ny
	g_TriNormalZ = nz
End Function 

Function TriangleNormalX#()
	Return g_TriNormalX
End Function

Function TriangleNormalY#()
	Return g_TriNormalY
End Function

Function TriangleNormalZ#()
	Return g_TriNormalZ
End Function



;
;	Starfox's functions (unmodified)
;

Type tris
	Field x#[3],y#[3],z#[3]
	Field surf,index,mesh
	Field ver[3],u#[3],v#[3]
	Field tex,size#
End Type

Function CreateLUVs(mesh,filename$,coordset=1)
	file = WriteFile(filename)
	For surfcount = 1 To CountSurfaces(mesh)
		surf = GetSurface(mesh,surfcount)
		For vercount = 0 To CountVertices(surf)-1
			WriteFloat(file,VertexU(surf,vercount,coordset))
			WriteFloat(file,VertexV(surf,vercount,coordset))
		Next
	Next
	CloseFile file
End Function

Function LoadLUVs(mesh,filename$,coordset=1)
	file = ReadFile(filename)
	For surfcount = 1 To CountSurfaces(mesh)
		surf = GetSurface(mesh,surfcount)
		For vercount = 0 To CountVertices(surf)-1
			u# = ReadFloat(file)
			v# = ReadFloat(file)
			VertexTexCoords surf,vercount,u,v,0,coordset
		Next
	Next
	CloseFile file
End Function

Function Unweld(mesh)
	;Unweld a mesh, retaining all of its textures coords and textures
	For surfcount = 1 To CountSurfaces(mesh)
		surf = GetSurface(mesh,surfcount)
		For tricount = 0 To CountTriangles(surf)-1
		t.tris = New tris
		t\surf = surf : t\mesh = mesh
		t\index = tricount
		in = TriangleVertex(t\surf,t\index,0)
		t\x[1] = VertexX(surf,in) : t\y[1] = VertexY(surf,in)
		t\z[1] = VertexZ(surf,in) : t\ver[1] = in
		t\u[1] = VertexU(surf,in) : t\v[1] = VertexV(surf,in)
		in = TriangleVertex(t\surf,t\index,1)
		t\x[2] = VertexX(surf,in) : t\y[2] = VertexY(surf,in)
		t\z[2] = VertexZ(surf,in) : t\ver[2] = in
		t\u[2] = VertexU(surf,in) : t\v[2] = VertexV(surf,in)
		in = TriangleVertex(t\surf,t\index,2)
		t\x[3] = VertexX(surf,in) : t\y[3] = VertexY(surf,in)
		t\z[3] = VertexZ(surf,in) : t\ver[3] = in
		t\u[3] = VertexU(surf,in) : t\v[3] = VertexV(surf,in)
		Next
		ClearSurface(surf,True,True)
		For t.tris = Each tris
		t\ver[1] = AddVertex(t\surf,t\x[1],t\y[1],t\z[1],t\u[1],t\v[1])
		t\ver[2] = AddVertex(t\surf,t\x[2],t\y[2],t\z[2],t\u[2],t\v[2])
		t\ver[3] = AddVertex(t\surf,t\x[3],t\y[3],t\z[3],t\u[3],t\v[3])
		AddTriangle(t\surf,t\ver[1],t\ver[2],t\ver[3])
		Delete t
		Next
	Next
	UpdateNormals mesh
	Return mesh
End Function



;
;	Example function
;
;	Hold 2th mouse button do turn the cam
;	Use the arrrows to move
; 	Click on a object to lightmap it
;	Press F2 to load the saved lightmaps

Function LMExample()
	Graphics3D(640, 480)
	SetBuffer(BackBuffer())
	
	; Create some stuff in the world
	camera = CreateCamera()
	PositionEntity(camera, 17, 18, 18)

	terr=CreateTerrain(512) : PositionEntity terr, -512,0,-512: ScaleEntity terr, 4, 1, 4 : EntityColor terr, 128, 64 , 0
	
	sun=CreateSphere(): PositionEntity sun, -50,60,0
	
	cube1 = CreateCube() : EntityColor cube1, 64,20,20
	PositionMesh(cube1, -2, 1.0, 0)
	ScaleEntity(cube1, 20, 5, 20)
	EntityPickMode(cube1, 2)
	NameEntity(cube1, "cube1")
	
	PointEntity(camera, cube1) ; Look at the cube
	
	cube2 = CreateCube() : EntityColor cube2, 20,64,20
	PositionMesh(cube2, 0, 1.0, 0) 
	ScaleEntity(cube2, 2, 2, 2)
	EntityPickMode(cube2, 2)
	NameEntity(cube2, "cube2")
	
	AmbientLight(50, 50, 50)
	light = CreateLight(1)
	RotateEntity(light, 45, 30, 0)
	
	; Timing control
	OldTime% = MilliSecs()

	While Not KeyHit(1)
		
		; Time elapsed between last frame
		Time% = MilliSecs()
		DeltaTime# = Float(Time - OldTime) / 1000   ; in seconds
		OldTime% = Time
	
		; Camera movement
		CamSpd# = 10 * DeltaTime
		MoveEntity(camera, Float(KeyDown(205) - KeyDown(203)) * CamSpd, 0, Float(KeyDown(200) - KeyDown(208)) * CamSpd)

		If MouseDown(2)
			TurnSpeed# = 0.8
			TurnEntity(camera, Float(MouseYSpeed())  * TurnSpeed#, 0, 0, False)
			TurnEntity(camera, 0, -Float(MouseXSpeed()) * TurnSpeed#, 0, True)
		Else
			MouseXSpeed() : MouseYSpeed()
		EndIf

		; Lightmap the picked entity
		If MouseHit(1)
			ent = CameraPick(camera, MouseX(), MouseY())
			If ent
				
				BeginLightMap(40, 40, 40)
				
				CreateLMLight( -50, 60, 0, 255, 255, 10, 1, 1,0,1,0,SUN_LIGHT)
				CreateLMLight(  8, 3,  3, 255, 255, 219, 40, 2,0,1,0,POINT_LIGHT)

				EntityPickMode(terr, 2)
				tex=lightmapterrain(terr)
				ApplyLightmap(terr, tex)
				EntityPickMode(terr, 0)
			
				tex = LightMapMesh(ent, 0.5)
				If tex
					SaveLightMap(ent, tex, EntityName(ent) + "_lm.bmp", EntityName(ent) + ".luv")
					ApplyLightMap(ent, tex)
				EndIf
				
				EndLightMap()
				
			EndIf
		EndIf		
		
		If KeyHit(60) ;  F2 key
			ent = CameraPick(camera, MouseX(), MouseY())
			If ent
				LoadLightMap(ent, EntityName(ent) + "_lm.bmp", EntityName(ent) + ".luv")
			EndIf
		EndIf

		UpdateWorld()

		RenderWorld()
		Flip()	
		
	Wend
	
	EndGraphics()
End Function
