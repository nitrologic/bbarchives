; ID: 794
; Author: sswift
; Date: 2003-09-14 10:38:04
; Title: Yet Another Lightmapper Improvement
; Description: This update to version 1.4 of YAL fixes numerous graphical glitches.

; ID: 514
; Author: Marcelo
; Date: 2002-11-28 19:49:40
; Title: YAL - Yet Another LightMapper (update 1.4 )
; Description: Based on starfox's portable lightmapper

;
;	YAL - Yet Another Lightmapper
;	Version: 1.4
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
;		- Other light types, such as directional and spot
;		- Test with complex meshes to see if the surface self shadowing is working with no problems
;		- Merge with the Olive's 1.2 version new features (directional light, etc)
;		- Easy way to work out the light coefficients
;		- Show the percentage statistics on the terrain too
;
;
;	History:
;		1.0	(28/11/2002)	- Initial version (marcelo@greenlandstudios.com)
;
;		1.1 (30/11/2002)	- Generate surfaces with more than one triangle
;							  Per light attenuation and brightness
;							  Functions to apply, save and load the lightmap
;							  Lightmap sharing (starfox)
;
;		1.3b1(23/12/2002)	- Different shadow ray checking, now it generates more precise shadows.
;							  New LM_DRAWSURFS const to paint the surfaces for debbuging purposes
;							  SaveLightMap() and LoadLightMap() should work in multiple surface meshes now. (surface fingerprint)
;							  Weld() the mesh to reduce the number of verts (Peter Scheutz and Terabit)
;							  Shows the percentage, elapsed and approximate remaining time
;							  Bug fixes (thanks to Olive), optimizations, etc.
;
;		1.4 (21/6/2003)     - Bug fixes and extensive checking made this a stable version
;							  Compresses the lightmaps based on the contrast (thanks again to elias for the idea)


Include "..\..\myprojects\functions\ray_intersect.bb"

; Call example 
LMExample()

; Set to True to draw the triangle edges on the texture
Const LM_DRAWTRIS = False

; True to color each surface
Const LM_DRAWSURFS = False
Const LM_DISABLESELFSHADOW = False

; Max polys per surface
Const LM_SURFTRIS = 256
Const LM_SURFADJTRIS = 256

; Num verts per poly
Const LM_VERTS = 2

; Angle between normals tolerance
Const LM_NORMAL_EPSILON# = 0.997
Const LM_NORMAL_EPSILON2# = 0.984

; This moves the lumel pivot away from the surface in the direction of it's normal by this distance.
; Increasing this value will prevent objects which are flush against the floor from darkening lumels around
; their base.
Const LM_LUMEL_PULL_INWARD# = 0.001

; Vertex distance tolerance
Const LM_VERTPOS_EPSILON# = 0.01
Const LM_VERTPOS_EPSILON2# = 0.05

; If the intensity is less that it ignore
Const LM_INTENSITY_EPSILON# = 0.9999 / 255.0

; Mapping plane
Const LMPLANE_XY = 0
Const LMPLANE_XZ = 1
Const LMPLANE_YZ = 2

; Mininum texture size
Const LM_MINTEXSIZE = 2

; Types
Type LMTriangle
	; Vertex info
	Field OX#[LM_VERTS], OY#[LM_VERTS], OZ#[LM_VERTS]
	
	Field X#[LM_VERTS], Y#[LM_VERTS], Z#[LM_VERTS]
	Field U#[LM_VERTS], V#[LM_VERTS]
	Field VertIndex[LM_VERTS]
	
	; Normal
	Field NX#, NY#, NZ#

	; Original surface pointer
	Field Surf
	
	; Surface that owns this triangle
	Field LMSurf.LMSurface
End Type

Type LMSurface
	; Triangle list
	Field Tris.LMTriangle[LM_SURFTRIS]
	Field NTris%
	
	Field AdjTris.LMTriangle[LM_SURFADJTRIS]
	Field NAdjTris%

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
	
	Field CastShadows
End Type


; This list contains all entities which should obscure light.
Type LMObscurer
	Field Entity
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

Function CreateLMLight.LMLight(x#, y#, z#, r#, g#, b#, range# = 0, castshadows = True, bright# = 10.0, att0# = 0, att1# = 1, att2# = 0, Area#=0, AreaQuality=0)
	
	l.LMLight = New LMLight
	
		l\X# = x# 
		l\Y# = y# 
		l\Z# = z#
	
		l\R# = r# 
		l\G# = g# 
		l\B# = b#
		
		l\Range# = range#
	
		l\Bright# = bright#
	
		l\Att#[0] = att0#
		l\Att#[1] = att1#
		l\Att#[2] = att2#
	
		l\CastShadows = castshadows
	
		If l\Range# = 0
			l\Range# = 9999999.0
		EndIf
	
	Return l
		
End Function


; Apply an lightmap created with LightMapMesh or LightMapTerrain
Function ApplyLightMap(mesh, tex, layer = 4)
	If Not tex
		Return False
	EndIf

	EntityFX(mesh, 1)
	EntityTexture(mesh, tex, 0, layer)
	FreeTexture(tex)
	
	Weld(mesh)
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
Function LoadLightMap(mesh, imgfile$, luvfile$, layer = 4)
	Unweld(mesh)

	If FileType(luvfile$)
		LoadLUVs(mesh, luvfile$) 
	EndIf

	tex = LoadTexture(imgfile$) 
	If tex
		EntityFX(mesh, 1)
		TextureCoords(tex, 1) 
		EntityTexture(mesh, tex, 0, layer)
		Weld(mesh)
		Return tex;FreeTexture(tex)
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
Function LightMapMesh(mesh, lumelsize# = 0.5, maxmapsize = 1024, blurradius = 1, TotalInfo$ = "")
	UnWeld(mesh)

	; Run thru all surfaces & triangles storing the info into LMTriangle
	For surfcount = 1 To CountSurfaces(mesh)

		surf = GetSurface(mesh, surfcount)
		For tricount = 0 To CountTriangles(surf) - 1
			Tri.LMTriangle = New LMTriangle

			For i = 0 To LM_VERTS
				vertn = TriangleVertex(surf, tricount, i)
				TFormPoint(VertexX(surf, vertn), VertexY(surf, vertn), VertexZ(surf, vertn), mesh, 0)
				Tri\X[i] = TFormedX() : Tri\Y[i] = TFormedY() : Tri\Z[i] = TFormedZ()
				
				Tri\OX[i] = VertexX(surf, vertn) : Tri\OY[i] = VertexY(surf, vertn) : Tri\OZ[i] = VertexZ(surf, vertn)
				
				Tri\VertIndex[i] = vertn
			Next
		
			Tri\Surf = Surf
	
			GetTriangleNormal(Tri\X[0], Tri\Y[0], Tri\Z[0], Tri\X[1], Tri\Y[1], Tri\Z[1], Tri\X[2], Tri\Y[2], Tri\Z[2])
			Tri\NX = TriangleNormalX() : Tri\NY = TriangleNormalY() : Tri\NZ = TriangleNormalZ()
		Next	
	Next
	
	LumelCount = 0
	
	; Create the surfaces
	While True
		; Find the first unlinked triangle
		For Tri.LMTriangle = Each LMTriangle
			If Tri\LMSurf = Null
				Exit
			EndIf
		Next

		; No more unlinked tris
		If Tri = Null 
			Exit
		EndIf

		LMSurf.LMSurface = New LMSurface
		
		Tri\LMSurf = LMSurf
		LMSurf\Tris[LMSurf\NTris] = Tri
		LMSurf\NTris = LMSurf\NTris + 1
				
		; Search for adjacent tri's with the same caracteristics and append to list

		; Loop while no poly's get added
		While True
			bNewPoly = False
		
			For STri.LMTriangle = Each LMTriangle
				If STri\LMSurf = Null
					; Compare the triangle normal 
					Ang# = ((STri\NX * Tri\NX) + (STri\NY * Tri\NY) + (STri\NZ * Tri\NZ))
					
					If Ang >= LM_NORMAL_EPSILON
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
							STri\LMSurf = LMSurf		
							LMSurf\Tris[LMSurf\NTris] = STri
							LMSurf\NTris = LMSurf\NTris + 1
							bNewPoly = True
					
							If LMSurf\NTris > LM_SURFTRIS
								Exit
							EndIf
						EndIf
					EndIf
				EndIf
			Next
			
			If Not bNewPoly
				Exit
			EndIf
			
			If LMSurf\NTris > LM_SURFTRIS
				Exit
			EndIf
		Wend

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
		
		; Search for directly adjacent triangles (that can be hidden on self shadow check)
		If LM_DISABLESELFSHADOW
			For STri.LMTriangle = Each LMTriangle
				If STri\LMSurf <> LMSurf
					
					; Compare the triangle normal 
					Ang# = ((STri\NX * LMSurf\NX) + (STri\NY * LMSurf\NY) + (STri\NZ * LMSurf\NZ))
					
					If Ang >= LM_NORMAL_EPSILON2
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
								
									If Dist <= LM_VERTPOS_EPSILON2
										NSharedVerts = NSharedVerts + 1
										Exit
									EndIf
								Next
							Next
						Next
						
						If NSharedVerts > 0
							LMSurf\AdjTris[LMSurf\NAdjTris] = STri
							LMSurf\NAdjTris = LMSurf\NAdjTris + 1
					
							If LMSurf\NAdjTris > LM_SURFADJTRIS
								Exit
							EndIf
						EndIf
					EndIf
					
				EndIf
			Next
		EndIf
				
		LMSetupSurface(LMSurf, lumelsize, blurradius)
		LumelCount = LumelCount + LMSurf\ImageSize
	Wend
	
	lcount = 0
	count = 0
	SpdSum# = 0

	InitialTime = MilliSecs()
	
	If Not LM_DRAWSURFS
		ClsColor(0, 0, 0)
		Cls()
		Print(TotalInfo$)
		Print("Percentage : 0%")
		Print("Time       : 0s  (0s to go)")
		Flip()
	
		For LMSurf.LMSurface = Each LMSurface
			Time = MilliSecs()
			
			; Create the light texture
			LMLightSurface(LMSurf, lumelsize)
			
			; Blur resulting image
			If blurradius > 0
				LMBlurImage(LMSurf\Image, blurradius)
			EndIf
	
			lcount = lcount + LMSurf\ImageSize
			count = count + 1
			
			Now = MilliSecs()
			Elapsed = Now - Time
			
			If Elapsed > 0
				Spd# = Float(LMSurf\ImageSize) / Float(Elapsed) * 1000
				SpdSum# = SpdSum# + Spd
			EndIf
			
			AvgSpd# = SpdSum / Float(count)
			Est = Float(LumelCount - lcount) / AvgSpd#
	
			; Display status		
			ClsColor(0, 0, 0)
			Cls()
			Print(TotalInfo$)
			Print("Percentage : " + (Float(lcount) / Float(LumelCount) * 100) + "%")
			Print("Time       : " + ((Now - InitialTime)/1000) + "s  (" + Est + "s to go)")
			Flip()
		Next
	Else
		SeedRnd(MilliSecs())
	EndIf

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
	
	SetBuffer(BackBuffer())
	
	Return Tex
End Function

;
;	Same as the lightmapmesh, but for terrains. detail% is the texture map size
;
Function LightMapTerrain(terrain, detail% = 0, blurradius% = 1)
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
	
	For Light.LMLight = Each LMLight
	
		PositionEntity(LightPivot, Light\X, Light\Y, Light\Z)
		
		For z% = 0 To LMSize-1
			For x% = 0 To LMSize-1

				zp% = TSize - z
				y# = TerrainHeight(terrain, x+1, zp)
				
				LumX# = (xpos + Float(x)  * XScale) / Scale
				LumY# = (ypos + Float(y)  * YScale) / Scale
				LumZ# = (zpos + Float(zp) * ZScale) / Scale

				PositionEntity(LumelPivot, LumX, LumY, LumZ)
				Dist# = EntityDistance(LightPivot, LumelPivot)
				
				; If this light can light this lumel		
				If (Dist <= Light\Range) And (Dist > 0)
					LMLightProcess(x, z, Light, LumX, LumY, LumZ, Dist, 1.0, LumelPivot, LightPivot)
				EndIf 
				
			Next ; x
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
	
	; Set the ambient light
	ClsColor(g_LMParams\AmbR, g_LMParams\AmbG, g_LMParams\AmbB) 
	Cls()
	ClsColor(0, 0, 0)
		
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
			
			If LM_DRAWSURFS
				Color(Rand(0,220), Rand(0,220), Rand(0,220))
				Rect(Img\X1, Img\Y1, IW, IH, True)
				
				Color(0, 0, 0)
				Text(Img\X1 + IW/2, Img\Y1 + IH/2, Handle(LMSurf), True, True)
			Else
				CopyRect(0, 0, IW, IH, Img\X1, Img\Y1, ImageBuffer(LMSurf\Image), TextureBuffer(Tex))
			EndIf
			
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
			If LM_DRAWSURFS
				Return Null
			EndIf
			
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
			rgb1 = ReadPixelFast(x, y, ImageBuffer(img1)) And $FFFFFF
			rgb2 = ReadPixelFast(x, y, ImageBuffer(img2)) And $FFFFFF
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


Function LMImageMeasureContrast%(img)

	;minvalue = 255
	;maxvalue = 0
		
	minvalue_r = 255
	minvalue_g = 255
	minvalue_b = 255
	
	width = ImageWidth(img)
	height = ImageHeight(img)
	
	LockBuffer(ImageBuffer(img))
	
	For y = 0 To height-1
		For x = 0 To width-1
			rgb1 = ReadPixelFast(x, y, ImageBuffer(img)) And $FFFFFF
			r1 = (rgb1 Shr 16 And %11111111)
			g1 = (rgb1 Shr 8 And %11111111)
			b1 = (rgb1 And %11111111)
			
;			If r1 > maxvalue Then maxvalue = r1
;			If g1 > maxvalue Then maxvalue = g1
;			If b1 > maxvalue Then maxvalue = b1

;			If r1 < minvalue Then minvalue = r1
;			If g1 < minvalue Then minvalue = g1
;			If b1 < minvalue Then minvalue = b1

			; (sswift)
			; What you really want to measure is the contrast of each channel, and then select the channel with the
			; max contrast.  What you're doing above would assume that an image that is pure red has a lot of contrast
			; because blue and green are 0 and red is 255.
			
			If r1 > maxvalue_r Then maxvalue_r = r1
			If g1 > maxvalue_g Then maxvalue_g = g1
			If b1 > maxvalue_b Then maxvalue_b = b1

			If r1 < minvalue_r Then minvalue_r = r1
			If g1 < minvalue_g Then minvalue_g = g1
			If b1 < minvalue_b Then minvalue_b = b1

		Next
	Next
	UnlockBuffer(ImageBuffer(img))
	
	;(sswift)
	
		contrast_r = maxvalue_r - minvalue_r
		contrast_g = maxvalue_g - minvalue_g
		contrast_b = maxvalue_b - minvalue_b
	
		If (contrast_r > contrast_g) And (contrast_r > contrast_b) Then Return contrast_r
		If (contrast_g > contrast_r) And (contrast_g > contrast_b) Then Return contrast_g
		Return contrast_b
	
	;Return maxvalue - minvalue
		
End Function


; Setup the surface
; (Map the surface's UV's to a plane aligned with a world axis.)
Function LMSetupSurface.LMSurface(LMSurf.LMSurface, lumelsize#, blurradius#)

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
					LMSurf\Tris[i]\U#[j] = LMSurf\Tris[i]\X#[j]
					LMSurf\Tris[i]\V#[j] = LMSurf\Tris[i]\Y#[j]
				Next
			Next
	
		Case LMPLANE_XZ
			For i = 0 To LMSurf\NTris-1
				For j = 0 To LM_VERTS
					LMSurf\Tris[i]\U#[j] = LMSurf\Tris[i]\X#[j]
					LMSurf\Tris[i]\V#[j] = LMSurf\Tris[i]\Z#[j]
				Next
			Next
	
		Case LMPLANE_YZ
			For i = 0 To LMSurf\NTris-1
				For j = 0 To LM_VERTS
					LMSurf\Tris[i]\U#[j] = LMSurf\Tris[i]\Y#[j]
					LMSurf\Tris[i]\V#[j] = LMSurf\Tris[i]\Z#[j]
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
	; (sswift)
	; Multiplying by 3.0 eradicates the light bleeding from adjacent lightmaps, but I don't know why, or what
	; effect changing the lumel size will have.
	;DT# = lumelsize * Float(blurradius + 1)
	;DT# = LumelSize# * Float(BlurRadius# + 1.0) * 3.0
	DT# = LumelSize# * Float(BlurRadius# + 5.0)	

	LMSurf\UMax# = LMSurf\UMax# + DT#
	LMSurf\VMax# = LMSurf\VMax# + DT#
	LMSurf\UMin# = LMSurf\UMin# - DT#
	LMSurf\VMin# = LMSurf\VMin# - DT#

	; Bound Box size
	LMSurf\UDelta# = LMSurf\UMax# - LMSurf\UMin# 
	LMSurf\VDelta# = LMSurf\VMax# - LMSurf\VMin#
	
	; Normalize the UV's, making it range from 0.0 to 1.0
	For i = 0 To LMSurf\NTris-1
		For j = 0 To LM_VERTS
			; Translate it to the origin
			LMSurf\Tris[i]\U[j] = LMSurf\Tris[i]\U[j] - LMSurf\UMin#
			LMSurf\Tris[i]\V[j] = LMSurf\Tris[i]\V[j] - LMSurf\VMin#
		
			; Normalize
			LMSurf\Tris[i]\U[j] = LMSurf\Tris[i]\U[j] / LMSurf\UDelta#
			LMSurf\Tris[i]\V[j] = LMSurf\Tris[i]\V[j] / LMSurf\VDelta#
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
	
	; Create image size based on the lumel density
	LMSizeX% = (LMSurf\UDelta / lumelsize)
	LMSizeY% = (LMSurf\VDelta / lumelsize)
	
	; Mininum texture size
	If LMSizeX < LM_MINTEXSIZE Then LMSizeX = LM_MINTEXSIZE
	If LMSizeY < LM_MINTEXSIZE Then LMSizeY = LM_MINTEXSIZE

	LMSurf\Image = CreateImage(LMSizeX, LMSizeY)
 	LMSurf\ImageSize = LMSizeX * LMSizeY
	
	Return LMSurf
End Function

;
;	Create the lightmap texture
;
Function LMLightSurface(LMSurf.LMSurface, lumelsize#)


	; Move poly to far away
	; (sswift)
	; This is unneccessary.  Moving the lumel slightly away from the surface prevents any possiblity of the
	; polygons in the surface self shadowing themselves, and you WANT the other polygons in a surface to shadow
	; the others if you have a large enough angle for polygon combining.
	
;	For i = 0 To LMSurf\NTris-1
;		For j = 0 To LM_VERTS
;			VertexCoords(LMSurf\Tris[i]\Surf, LMSurf\Tris[i]\VertIndex[j], 99999, 99999, 99999)
;		Next
;	Next

;	For i = 0 To LMSurf\NAdjTris-1
;		For j = 0 To LM_VERTS
;			VertexCoords(LMSurf\AdjTris[i]\Surf, LMSurf\AdjTris[i]\VertIndex[j], 99999, 99999, 99999)
;		Next
;	Next

	LMSizeX% = ImageWidth(LMSurf\Image)
	LMSizeY% = ImageHeight(LMSurf\Image)
	
	ImgBuf = ImageBuffer(LMSurf\Image)
	SetBuffer(ImgBuf)
	
	; Set the ambient light
	ClsColor(g_LMParams\AmbR, g_LMParams\AmbG, g_LMParams\AmbB)
	Cls()
	ClsColor(0, 0, 0)
	
	LockBuffer(ImgBuf)
	
	LightPivot = CreatePivot()
	LumelPivot = CreatePivot()

	;EntityPickMode(LightPivot, 1, False)
	;EntityRadius(LightPivot, 0.625)	; Found by trial and error
	;EntityRadius(LightPivot, 10.0)	

	;EntityPickMode(LumelPivot, 1)
	;EntityRadius(LumelPivot, 0.625)	; Found by trial and error
	;EntityRadius(LumelPivot, 0.3)
	;EntityRadius(LumelPivot, lumelsize*2)

	; (sswift)
	; Calculate one half the width of a lumel, in UV coordinates.
	centeruvoffset# = (1.0 / Float(LMSizeX)) / 2.0
		
	For Light.LMLight = Each LMLight
	
		PositionEntity(LightPivot, Light\X, Light\Y, Light\Z)
		
		For Y = 0 To LMSizeY-1
			For X = 0 To LMSizeX-1
				
				; Find the UV
				; (sswift) Of the TOP LEFT corner of the lumel!  Not the center!
				U# = Float(x) / Float(LMSizeX)
				V# = Float(y) / Float(LMSizeY)
								
				; (sswift)
				; Offset the UV coordinates so that we are at the center of the lumel.
				U# = U# + CenterUVOffset#
				V# = V# + CenterUVOffset#
								
				; Transform to world coordinates
				N_UEdgeX# = LMSurf\UEdgeX# * u#  :  N_UEdgeY# = LMSurf\UEdgeY# * u#  :  N_UEdgeZ# = LMSurf\UEdgeZ# * u#
				N_VEdgeX# = LMSurf\VEdgeX# * v#  :  N_VEdgeY# = LMSurf\VEdgeY# * v#  :  N_VEdgeZ# = LMSurf\VEdgeZ# * v#
				
				LumX# = (LMSurf\OriginX# + N_UEdgeX# + N_VEdgeX#)
				LumY# = (LMSurf\OriginY# + N_UEdgeY# + N_VEdgeY#)
				LumZ# = (LMSurf\OriginZ# + N_UEdgeZ# + N_VEdgeZ#)
				
				PositionEntity(LumelPivot, LumX#, LumY#, LumZ#)
				RotateEntity(LumelPivot, 0, 0, 0)
				AlignToVector(LumelPivot, LMSurf\UEdgeX#, LMSurf\UEdgeY#, LMSurf\UEdgeZ#, 1)
				AlignToVector(LumelPivot, LMSurf\VEdgeX#, LMSurf\VEdgeY#, LMSurf\VEdgeZ#, 3)
				
				Dist# = EntityDistance(LightPivot, LumelPivot)
				
				; If this light can light this lumel		
				If (Dist# <= Light\Range#) And (Dist# > 0)
					
					; Normal vector between lumel and light
					NX# = (LumX# - Light\X#) / Dist#
					NY# = (LumY# - Light\Y#) / Dist#
					NZ# = (LumZ# - Light\Z#) / Dist#

					; Dot product to find the cosine angle between the surface normal and incident light normal
					CosAngle# = (NX# * LMSurf\NX#) + (NY# * LMSurf\NY#) + (NZ# * LMSurf\NZ#)
					
					; Poly face front of the light
					If CosAngle# > 0
						LMLightProcess(x, y, Light, LumX#, LumY#, LumZ#, Dist#, CosAngle#, LumelPivot, LightPivot, LumelSize#)
					EndIf 
					
				EndIf ; Dist < Light\Range
				
			Next ; x
		Next ; y

	Next ;Light
	

	UnlockBuffer(ImgBuf)

	SetBuffer(BackBuffer())
	
	; Move it back
;	For i = 0 To LMSurf\NTris-1
;		For j = 0 To LM_VERTS
;			VertexCoords(LMSurf\Tris[i]\Surf, LMSurf\Tris[i]\VertIndex[j], LMSurf\Tris[i]\OX[j], LMSurf\Tris[i]\OY[j], LMSurf\Tris[i]\OZ[j])
;		Next
;	Next

;	For i = 0 To LMSurf\NAdjTris-1
;		For j = 0 To LM_VERTS
;			VertexCoords(LMSurf\AdjTris[i]\Surf, LMSurf\AdjTris[i]\VertIndex[j], LMSurf\AdjTris[i]\OX[j], LMSurf\AdjTris[i]\OY[j], LMSurf\AdjTris[i]\OZ[j])
;		Next
;	Next

	FreeEntity(LightPivot)
	FreeEntity(LumelPivot)
	
	If (LMSizeX > 2) And (LMSizeY > 2)
		TFormFilter(True)
		Contrast = LMImageMeasureContrast(LMSurf\Image)
		
		NSizeX = LMSizeX
		NSizeY = LMSizeY
		
		Select True
			Case (Contrast <= 4)
				NSizeX = 2 : NSizeY = 2
				
			Case (Contrast > 4) And (Contrast <= 20)
				NSizeX = NSizeX / 4
				NSizeY = NSizeY / 4

			Case (Contrast > 20) And (Contrast <= 80)
				NSizeX = NSizeX / 2
				NSizeY = NSizeY / 2
		End Select
		
		If NSizeX < 2 Then NSizeX = 2
		If NSizeY < 2 Then NSizeY = 2
		
		If (NSizeX <> LMSizeX) Or (NSizeY <> LMSizeY)
			ResizeImage(LMSurf\Image, NSizeX, NSizeY)
		EndIf
	EndIf
		
	LMSurf\ImageSize = ImageWidth(LMSurf\Image) * ImageHeight(LMSurf\Image)
End Function


; LumelRadius# is in world units, not texture UV.
Function LMLightProcess(x%, y%, Light.LMLight, LumX#, LumY#, LumZ#, Dist#, CosAngle#, LumelPivot, LightPivot, LumelRadius#=0)
	
	; Measure attenuation
	Att# = 1.0 / (Light\Att#[0] + (Light\Att#[1] * Dist#) + (Light\Att#[2] * Dist# * Dist#))
			
	; Lambert + attenuation
	Intensity# = (Light\Bright# * CosAngle#) * Att#
	
	If (Intensity# < 0.0) Then Intensity# = 0.0
	If (Intensity# > 1.0) Then Intensity# = 1.0
	
	If Intensity# > LM_INTENSITY_EPSILON#
	
		NHits = 0
		NFired = 0
		
		If Light\CastShadows
		
				NFired = NFired + 1

				Obscured = False
				For LumelCorner = 1 To 4
				
					Select LumelCorner
				
						; Top left	
						Case 1
							TFormPoint -LumelRadius#, LM_LUMEL_PULL_INWARD#, -LumelRadius#, LumelPivot, 0
							
						; Top right	
						Case 2
							TFormPoint  LumelRadius#, LM_LUMEL_PULL_INWARD#, -LumelRadius#, LumelPivot, 0
							
						; Bottom left	
						Case 3
							TFormPoint -LumelRadius#, LM_LUMEL_PULL_INWARD#,  LumelRadius#, LumelPivot, 0

						; Bottom right
						Case 4
							TFormPoint  LumelRadius#, LM_LUMEL_PULL_INWARD#,  LumelRadius#, LumelPivot, 0
							
					End Select
					
					; Get the location of this corner.
					Px# = TFormedX#()
					Py# = TFormedY#()
					Pz# = TFormedZ#()
													
					; Calculate the vector between this corner and the light.
					Dx# = Light\X# - Px#  
					Dy# = Light\Y# - Py#
					Dz# = Light\Z# - Pz#

					; Check each obscuring object to see if it is blocking the light. 
					; Exit early if one is found.								
					; Might get some additional speed with a lot of obscurers by sorting them so that those
					; nearest the light source or the lumel are examined first.
					For ThisObscurer.LMObscurer = Each LMObscurer
						If Ray_Intersect_Mesh(ThisObscurer\Entity, Px#, Py#, Pz#, Dx#, Dy#, Dz#, False, True)
							Obscured = True
							Exit
						EndIf
					Next

					If Obscured Then Exit
					
				Next

				If Not Obscured Then NHits = NHits + 1
		
		Else

			; This light does not cast shadows.
			NHits = 1
			NFired = 1

		EndIf
	
	
		; If this lumel is illuminated...
		If (NHits > 0)  
						
			Intensity# = Intensity# * Float(NHits) / Float(NFired)
			
			; Add the incident light the pixel
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
	
		EndIf	
		
	
	EndIf
	
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
			
			; Get the average value
			r = r / num
			g = g / num
			b = b / num
			
			; Clamp
			; (sswift: Impossible to get RGB value greater than 255 with averaging!)
			;If r > 255 Then r = 255
			;If g > 255 Then g = 255
			;If b > 255 Then b = 255

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


Function CreateLUVs(mesh,filename$,coordset=1)
	file = WriteFile(filename)
	
	WriteInt(file, CountSurfaces(mesh))
	
	For surfcount = 1 To CountSurfaces(mesh)
		surf = GetSurface(mesh,surfcount)
		
		fprint = SurfaceFingerPrint(mesh, surf)
		WriteInt(file, fprint)
		
		count = CountVertices(surf)
		WriteInt(file, count)
		For vercount = 0 To count-1
			WriteFloat(file,VertexU(surf,vercount,coordset))
			WriteFloat(file,VertexV(surf,vercount,coordset))
		Next
	Next
	
	WriteInt(file, 0)
	
	CloseFile file
End Function

Function LoadLUVs(mesh,filename$,coordset=1)
	file = ReadFile(filename)
	
	surfcount = ReadInt(File)
	If surfcount <> CountSurfaces(mesh)
		DebugLog "Wrong number of surfaces"
		CloseFile(file)
		Return False
	EndIf
	
	fprint = ReadInt(file)
	
	While fprint
		surf = FindSurfFingerPrint(mesh, fprint)
		If surf
			count = ReadInt(file)
			For vercount = 0 To count-1
				u# = ReadFloat(file)
				v# = ReadFloat(file)
				VertexTexCoords(surf,vercount,u,v,0,coordset)
			Next
		Else
			DebugLog "Surface fingerprint " + fprint + " not found"
			count = ReadInt(file)
			For vercount = 0 To count-1
				ReadFloat(file):ReadFloat(file)
			Next			
		EndIf	
		fprint = ReadInt(file)
	Wend
	
	CloseFile file
End Function

Function FindSurfFingerPrint(mesh, fingerprint)
	For surfcount = 1 To CountSurfaces(mesh)
		surf = GetSurface(mesh, surfcount)
		
		If SurfaceFingerPrint(mesh, surf) = fingerprint
			Return surf
		EndIf
	Next
	
	Return 0
End Function

Function SurfaceFingerPrint%(mesh, surf)
	tricount = CountTriangles(surf)
	
	CoordSum = 0
	
	For tri = 0 To tricount - 1
		For i = 0 To 2
			in = TriangleVertex(surf, tri, i)
			
			s$ = VertexX(surf, in)
			Pos = Instr(s, ".")
			If Pos <> 0
				x# = Left(s$, pos + 3)
			Else
				x# = s$
			EndIf
			
			s$ = VertexY(surf, in)
			Pos = Instr(s, ".")
			If Pos <> 0
				y# = Left(s$, pos + 3)
			Else
				y# = s$
			EndIf

			s$ = VertexZ(surf, in)
			Pos = Instr(s, ".")
			If Pos <> 0
				z# = Left(s$, pos + 3)
			Else
				z# = s$
			EndIf
			
			CoordSum = CoordSum + Abs(x * 3 * (i+1))
			CoordSum = CoordSum + Abs(y * 2 * (i+1))
			CoordSum = CoordSum + Abs(z * 1 * (i+1))
		Next
	Next
	
	Return CoordSum
End Function


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


Dim txv(3)

Type TRIS
	Field x0#
	Field y0#
	Field z0#

	Field u0#
	Field v0#
	Field U20#
	Field V20#
	
	Field x1#
	Field y1#
	Field z1#

	Field u1#
	Field v1#
	Field U21#
	Field V21#
	
	Field x2#
	Field y2#
	Field z2#

	Field u2#
	Field v2#

	Field U22#
	Field V22#
	
	Field surface
End Type


Function Weld(mish)
	Dim txv(3)
	
	For nsurf = 1 To CountSurfaces(mish)
		su=GetSurface(mish,nsurf)
		For tq = 0 To CountTriangles(su)-1
			txv(0) = TriangleVertex(su,tq,0)
			txv(1) = TriangleVertex(su,tq,1)
			txv(2) = TriangleVertex(su,tq,2)

			vq.TRIS = New TRIS
			vq\x0# = VertexX(su,txv(0))
			vq\y0# = VertexY(su,txv(0))
			vq\z0# = VertexZ(su,txv(0))
			vq\u0# = VertexU(su,txv(0),0)
			vq\v0# = VertexV(su,txv(0),0)
			vq\u20# = VertexU(su,txv(0),1)
			vq\v20# = VertexV(su,txv(0),1)
			
			vq\x1# = VertexX(su,txv(1))
			vq\y1# = VertexY(su,txv(1))
			vq\z1# = VertexZ(su,txv(1))
			vq\u1# = VertexU(su,txv(1),0)
			vq\v1# = VertexV(su,txv(1),0)
			vq\u21# = VertexU(su,txv(1),1)
			vq\v21# = VertexV(su,txv(1),1)
		
			vq\x2# = VertexX(su,txv(2))
			vq\y2# = VertexY(su,txv(2))
			vq\z2# = VertexZ(su,txv(2))
			vq\u2# = VertexU(su,txv(2),0)
			vq\v2# = VertexV(su,txv(2),0)
			vq\u22# = VertexU(su,txv(2),1)
			vq\v22# = VertexV(su,txv(2),1)
		Next
		
		ClearSurface su
		
		For vq.tris = Each tris
		
				vt1=findvert(su,vq\x0#,vq\y0#,vq\z0#,vq\u0#,vq\v0#,vq\u20#,vq\v20#)
				
				If vt1=-1 Then
					vt1=AddVertex(su,vq\x0#,vq\y0#,vq\z0#,vq\u0#,vq\v0#)
					VertexTexCoords su,mycount,vq\u20#,vq\v20#,0,1
					vt1 = mycount
					mycount = mycount +1
				EndIf
		
				vt2=findvert(su,vq\x1#,vq\y1#,vq\z1#,vq\u1#,vq\v1#,vq\u21#,vq\v21#)
				If Vt2=-1 Then
					vt2=AddVertex( su,vq\x1#,vq\y1#,vq\z1#,vq\u1#,vq\v1#)
					VertexTexCoords su,mycount,vq\u21#,vq\v21#,0,1
					vt2 = mycount
					mycount = mycount +1
				EndIf
				
				vt3=findvert(su,vq\x2#,vq\y2#,vq\z2#,vq\u2#,vq\v2#,vq\u22#,vq\v22#)
				
				If vt3=-1 Then 
					vt3=AddVertex(su,vq\x2#,vq\y2#,vq\z2#,vq\u2#,vq\v2#)
					VertexTexCoords su,mycount,vq\u22#,vq\v22#,0,1
					vt3 = mycount
					mycount = mycount +1
				EndIf
		
			AddTriangle su,vt1,vt2,vt3
		
		Next
		
		Delete Each tris
		mycount=0
	Next
End Function
	
Function findvert(su,x2#,y2#,z2#,u2#,v2#,u22#,v22#)
	Local thresh# =0.001
	
	For t=0 To CountVertices(su)-1
		If Abs(VertexX(su,t)-x2#)<thresh# Then 
			If Abs(VertexY(su,t)-y2#)<thresh# Then 
				If Abs(VertexZ(su,t)-z2#)<thresh# Then 
					If Abs(VertexU(su,t,0)-u2#)<thresh# Then 
						If Abs(VertexV(su,t,0)-v2#)<thresh# Then 
							If Abs(VertexU(su,t,1)-u22#)<thresh# Then 
								If Abs(VertexV(su,t,1)-v22#)<thresh# Then
									Return t
								EndIf
							EndIf
						EndIf
					EndIf
				EndIf
			EndIf
		EndIf
	Next
	Return -1
End Function

;
;	Example function
;
;	Hold 2th mouse button do turn the cam
;	Use the arrrows to move
; 	Click on a object to lightmap it
;	Press F2 to load the saved lightmap
Function LMExample()
	Graphics3D 1024, 768, 32, 2
	SetBuffer(BackBuffer())
	
	; Create some stuff in the world
	camera = CreateCamera()
	PositionEntity(camera, 17, 18, 18)

	cube1 = CreateCube()
	FlipMesh(cube1)	
	PositionMesh(cube1, 0, 1.0, 0)
	ScaleEntity(cube1, 20, 5, 20)
	EntityPickMode(cube1, 2)
	NameEntity(cube1, "cube1")
	
	PointEntity(camera, cube1) ; Look at the cube
	
	cube2 = CreateCube()
	;PositionMesh(cube2, 0, 2, 0)
	PositionMesh(cube2, 0, 1, 0)
	;PositionMesh(cube2, 0, 0.5, 0) 
	ScaleEntity(cube2, 2, 2, 2)
	EntityPickMode(cube2, 2)
	NameEntity(cube2, "cube2")
	
	AmbientLight(50, 50, 50)
	light = CreateLight(1)
	RotateEntity(light, 45, 30, 0)
	
	ThisObscurer.LMObscurer = New LMObscurer
	ThisObscurer\Entity = Cube1

	ThisObscurer.LMObscurer = New LMObscurer
	ThisObscurer\Entity = Cube2
	
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
			
;EntityPickMode(cube1, 0)
			
				BeginLightMap(40, 40, 40)
				
				CreateLMLight( -8, 3, -8, 219, 219, 255, 0, True, 3)
				CreateLMLight(  8, 3,  3, 255, 255, 219, 0, True, 3)

				;(mesh, lumelsize# = 0.5, maxmapsize = 1024, blurradius = 1, TotalInfo$ = "")
				;tex = LightMapMesh(ent, 0.25, 1024, 1, "Lightmapping " + EntityName(ent))

				tex = LightMapMesh(ent, 0.25, 1024, 1, "Lightmapping " + EntityName(ent))

				If tex
					SaveLightMap(ent, tex, EntityName(ent) + "_lm.bmp", EntityName(ent) + ".luv")
					ApplyLightMap(ent, tex)
				EndIf
				
				EndLightMap()
				
;EntityPickMode(cube1, 2)

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

		ent = CameraPick(camera, MouseX(), MouseY())
		If ent Then Text 0,0, EntityName$(ent) 

		Flip()	
		
	Wend
	
	EndGraphics()
End Function
