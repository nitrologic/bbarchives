; ID: 514
; Author: Marcelo
; Date: 2002-11-28 19:49:40
; Title: YAL - Yet Another LightMapper (update 1.5 )
; Description: Based on starfox's portable lightmapper

;
;	YAL - Yet Another Lightmapper
;	Version: 1.5
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
;							       Compresses the lightmaps based on the contrast (thanks again to elias for the idea)
;
;		1.5 (15/9/2003)   - Fixed the math bug that offset's the lumel wrongly
;								- Code to create a one pixel border (to prevent bilinear filtering from catching neighboor pixels)
;							   - 5 ray's checking per lumel reduces the need for image blurring (softer shadows)
;    							- Uses vertex normals to smooth the result

; Call example 
LMExample()

; Set to True to draw the triangle edges on the texture
Const LM_DRAWTRIS = False

; True to color each surface
Const LM_DRAWSURFS = False

; Max polys per surface
Const LM_SURFTRIS = 256

; Num verts per poly
Const LM_VERTS = 2

; Angle between normals tolerance
Const LM_NORMAL_EPSILON# = 0.997
Const LM_NORMAL_EPSILON2# = 0.984

; Vertex distance tolerance
Const LM_VERTPOS_EPSILON# = 0.01
Const LM_VERTPOS_EPSILON2# = 0.05

Const LM_NORMALOFFSET# = 0.00001

; If the intensity is less that it ignore
Const LM_INTENSITY_EPSILON# = 0.9999 / 255

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
	Field VNX#[LM_VERTS], VNY#[LM_VERTS], VNZ#[LM_VERTS]
	
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
	Field ExtraCheck
End Type

; Light
Type LMLight
	Field X#, Y#, Z#
	Field R#, G#, B#
	Field Pitch#, Yaw#, Roll#
	
	Field Range#
	Field Att#[2]
	Field Bright#
	
	Field Directional
	
	Field CastShadows
End Type

Type LMEntity
	Field Invisible
	Field Entity
	
	; Box sizes
	Field Width#, Height#, Depth#
End Type

Function AddLMEntity(Entity, Invisible = False)
	Ent.LMEntity = New LMEntity
	Ent\Entity = Entity
	Ent\Invisible = Invisible
	
	Ent\Width = MeshWidth(Entity)
	Ent\Height = MeshHeight(Entity)
	Ent\Depth = MeshDepth(Entity)
	Return True
End Function

Function LMEntityVisible(Entity1, Entity2)
	

End Function

; Store global parameters
Global g_LMParams.LMParams = Null


;	*****************
;	
;	Public functions
;	
;	*****************

; Create and setup global parameters
; AmbR, AmbG, AmbB is the ambient light color
Function BeginLightMap(AmbR = 0, AmbG = 0, AmbB = 0, ExtraCheck = False)
	g_LMParams = New LMParams
	
	g_LMParams\AmbR = AmbR
	g_LMParams\AmbG = AmbG
	g_LMParams\AmbB = AmbB
	g_LMParams\ExtraCheck = ExtraCheck
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
	
	Delete Each LMEntity
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

Function CreateLMLight.LMLight(x#, y#, z#, r#, g#, b#, range# = 0, castshadows = True, bright# = 10.0, att0# = 0, att1# = 1, att2# = 0)
	l.LMLight = New LMLight
	l\X = x : l\Y = y : l\Z = z
	l\R = r : l\G = g : l\B =b
	l\Range = range
	
	l\Bright = bright
	
	l\Att[0] = att0
	l\Att[1] = att1
	l\Att[2] = att2
	l\Directional = False
	
	l\CastShadows = castshadows
	
	If l\Range = 0
		l\Range = 9999999.0
	EndIf
	
	Return l
End Function


Function CreateLMLight2.LMLight(x#, y#, z#, pitch#, yaw#, roll#, r#, g#, b#, castshadows = True, bright# = 10.0)
	l.LMLight = New LMLight
	l\X = x : l\Y = y : l\Z = z
	l\R = r : l\G = g : l\B =b
	
	l\pitch = pitch : l\yaw = yaw: l\roll = roll
		
	l\Bright = bright
	
	l\Att[0] = 1
	l\Att[1] = 0
	l\Att[2] = 0
	l\Directional = True
	
	l\CastShadows = castshadows
	
	If l\Range = 0
		l\Range = 9999999.0
	EndIf
	
	Return l
End Function



; Apply an lightmap created with LightMapMesh or LightMapTerrain
Function ApplyLightMap(mesh, tex, layer = 4, imgfile2$ = "")
	If Not tex
		Return False
	EndIf
	
	If EntityClass(mesh) = "Terrain"
		; Try to blend secondary texture
		tex2 = LoadTexture(imgfile2$)
		If tex2
			BlendTextureMultiply(tex, tex2)
			FreeTexture(tex2)
		EndIf
	EndIf
	
	EntityFX(mesh, 1)
	EntityTexture(mesh, tex, 0, layer)
	FreeTexture(tex)
	
	If EntityClass(mesh) = "Mesh"
		Weld(mesh)
	EndIf
	
	Return True
End Function

; Save to a bmp file and a luv file the information about a lightmapped entity
Function SaveLightMap(mesh, tex, imgfile$, luvfile$)
	If Not tex
		Return False
	EndIf
	
	SaveBuffer(TextureBuffer(tex), imgfile$)
	
	If luvfile$ <> ""
		CreateLUVs(mesh, luvfile$, 1) 
	EndIf
End Function

Function BlendTextureMultiply(Tex1, Tex2)
	w = TextureWidth(Tex1)
	h = TextureHeight(Tex1)
	
	If (w <> TextureWidth(Tex2)) Or (h <> TextureHeight(Tex2))
		Return False
	EndIf

	buf1 = TextureBuffer(Tex1)
	buf2 = TextureBuffer(Tex2)
	
	LockBuffer(buf1)
	LockBuffer(buf2)
	
	For y = 0 To h - 1
		For x = 0 To w - 1
			argb1 = ReadPixelFast(x, y, buf1)
			r1 = (argb1 Shr 16 And %11111111)
			g1 = (argb1 Shr 8 And %11111111)
			b1 = (argb1 And %11111111)

			argb2 = ReadPixelFast(x, y, buf2)
			r2 = (argb2 Shr 16 And %11111111)
			g2 = (argb2 Shr 8 And %11111111)
			b2 = (argb2 And %11111111)
			
			; Multiply
			fr = Float(r1) * (Float(r2) / 255)
			fg = Float(g1) * (Float(g2) / 255)
			fb = Float(b1) * (Float(b2) / 255)
			
			; Write back on tex1 buffer
			frgb = fb Or (fg Shl 8) Or (fr Shl 16)
			WritePixelFast(x, y, frgb, buf1)
		Next
	Next
	
	UnlockBuffer(buf1)
	UnlockBuffer(buf2)
	
	Return Tex1
End Function

; Load an image file and the luv file into the entity 
Function LoadLightMap(mesh, imgfile$, luvfile$, layer = 4)
	If EntityClass(mesh) = "Mesh"
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
			
			Return tex
		EndIf
	EndIf
	
	Return False
End Function

Function LoadTerrainLightmap(mesh, imgfile$, imgfile2$, layer = 4)
	If EntityClass(mesh) = "Terrain"
		tex1 = LoadTexture(imgfile$)
		If tex1
			; Try to blend secondary texture
			tex2 = LoadTexture(imgfile2$)
			If tex2
				BlendTextureMultiply(tex1, tex2)
				FreeTexture(tex2)
			EndIf

			TSize = TerrainSize(mesh)
			ScaleTexture(tex1, TSize, TSize)
			
			If layer >= 0
				EntityFX(mesh, 1)
				EntityTexture(mesh, tex1, 0, layer)
			EndIf
			Return tex1
		EndIf
	EndIf
	
	Return False
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
				
				;Tri\VNX[i] = VertexNX(surf, vertn) : Tri\VNY[i] = VertexNY(surf, vertn) : Tri\VNZ[i] = VertexNZ(surf, vertn)
				TFormNormal(VertexNX(surf, vertn), VertexNY(surf, vertn), VertexNZ(surf, vertn), mesh, 0)
				Tri\VNX[i] = TFormedX() : Tri\VNY[i] = TFormedY() : Tri\VNZ[i] = TFormedZ()
				
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
		
		LMSetupSurface(LMSurf, lumelsize, blurradius)
		LumelCount = LumelCount + LMSurf\ImageSize
	Wend
	
	lcount = 0
	count = 0
	SpdSum# = 0

	InitialTime = MilliSecs()
	
	If (Not LM_DRAWSURFS)
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
Function LightMapTerrain(terrain, texsize% = 0, blurradius% = 1, selfshadow = False, TotalInfo$)
	ClsColor(0, 0, 0)
	Cls()
	Print(TotalInfo$)
	Print("Percentage : 0%")
	Print("Time       : 0s  (0s to go)")
	Flip()

	TSize# = TerrainSize(terrain)
	
	If texsize = 0
		texsize = TSize
	EndIf
	
	; Get the entity scale
	vx# = GetMatElement(terrain, 0, 0)
	vy# = GetMatElement(terrain, 0, 1)
	vz# = GetMatElement(terrain, 0, 2)
	XSize# = Sqr(vx*vx + vy*vy + vz*vz) * TSize
	vx# = GetMatElement(terrain, 1, 0)
	vy# = GetMatElement(terrain, 1, 1)
	vz# = GetMatElement(terrain, 1, 2)
	YSize# = Sqr(vx*vx + vy*vy + vz*vz)
	vx# = GetMatElement(terrain, 2, 0)
	vy# = GetMatElement(terrain, 2, 1)
	vz# = GetMatElement(terrain, 2, 2)
	ZSize# = Sqr(vx*vx + vy*vy + vz*vz) * TSize
	
	Img = CreateImage(texsize, texsize)

	ImgBuf = ImageBuffer(Img)
	SetBuffer(ImgBuf)
	
	; Set the ambient light
	ClsColor(g_LMParams\AmbR, g_LMParams\AmbG, g_LMParams\AmbB)
	Cls()
	ClsColor(0, 0, 0)
	
	LockBuffer(ImgBuf)
	
	LightPivot = CreatePivot()
	LumelPivot = CreatePivot()
	
	lumelsize# = XSize / Float(texsize)
	halflumel# = lumelsize/2
	
	xpos# = EntityX(terrain, 1) : ypos# = EntityY(terrain, 1) : zpos# = EntityZ(terrain, 1)
	scale# = Float(TSize) / Float(texsize)
	
	NLights = 0
	For Light.LMLight = Each LMLight
		NLights = NLights + 1
	Next
	
	LumelCount = (texsize * texsize) * NLights
	count = 0 : lcount = 0
	
	SpdSum# = 0
	InitialTime = MilliSecs()
	
	For Light.LMLight = Each LMLight
		For z% = 0 To texsize-1
			Time = MilliSecs()
			
			For x% = 0 To texsize-1
				tx# = Float(x) * scale
				tz# = Float(z) * scale
			
				y# = TerrainHeight(terrain, tx, tz)
				LumY# = (ypos + y * YSize) + LM_NORMALOFFSET
				
				LumX# = (xpos + Float(x) * lumelsize) + halflumel
				LumZ# = (zpos + Float(z) * lumelsize) + halflumel

				PositionEntity(LumelPivot, LumX, LumY, LumZ)
				
				lx# = Light\X : ly# = Light\Y : lz# = Light\Z
				RotateEntity(LightPivot, Light\Pitch, Light\Yaw, Light\Roll)

				If Light\Directional
					TFormVector(0, 0, -1, LightPivot, 0)
					lx# = LumX + TFormedX() * 9999				
					ly# = LumY + TFormedY() * 9999
					lz# = LumZ + TFormedZ() * 9999
					Dist# = 0.0001
				EndIf

				PositionEntity(LightPivot, lx, ly, lz)
				
				If Not Light\Directional
					Dist# = EntityDistance(LightPivot, LumelPivot)
				EndIf	
				
				; If this light can light this lumel		
				If (Dist <= Light\Range) And (Dist > 0)
					If Not selfshadow
						LMLightProcess(x, z, Light, Dist, 1.0, LumelPivot, LightPivot, lumelsize)
					Else If LinePick(LumX, LumY + 0.2, LumZ, 0, -1.0, 0, 0)
						; Normal vector between lumel and light
						NX# = (lx - LumX) / Dist
						NY# = (ly - LumY) / Dist
						NZ# = (lz - LumZ) / Dist
						
						CosAngle# = (NX * PickedNX()) + (NY * PickedNY()) + (NZ * PickedNZ())
						
						If CosAngle > 0
							LMLightProcess(x, z, Light, Dist, CosAngle, LumelPivot, LightPivot, lumelsize)
						EndIf
					EndIf
				EndIf
			Next ; x
			
			lcount = lcount + texsize
			count = count + 1

			Now = MilliSecs()
			Elapsed = Now - Time
			
			If Elapsed > 0
				Spd# = texsize / Float(Elapsed) * 1000
				SpdSum# = SpdSum# + Spd
			EndIf
			
			AvgSpd# = SpdSum / Float(count)
			Est = Float(LumelCount - lcount) / AvgSpd#

			; Display status
			SetBuffer(BackBuffer())	
			ClsColor(0, 0, 0)
			Cls()
			Print(TotalInfo$)
			Print("Percentage : " + (Float(lcount) / Float(LumelCount) * 100) + "%")
			Print("Time       : " + ((Now - InitialTime)/1000) + "s  (" + Est + "s to go)")
			Flip()
			
			SetBuffer(ImgBuf)
		Next ; z
	Next
	
	UnlockBuffer(ImgBuf)
	
	; Blur resulting image
	If blurradius > 0
		LMBlurImage(Img, blurradius)
	EndIf

	ScaleImage (Img, 1, -1)
	HandleImage(Img, 0, 0)
	
	Tex = CreateTexture(texsize, texsize, 512)
	CopyRect(0, 0, texsize, texsize, 0, 0, ImageBuffer(Img), TextureBuffer(Tex))
	
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
				Rect(Img\X1+1, Img\Y1+1, IW-2, IH-2, True)
				
				Color(0, 0, 0)
				Text(Img\X1 + IW/2, Img\Y1 + IH/2, Handle(LMSurf), True, True)
			Else
				CopyRect(0, 0, IW, IH, Img\X1, Img\Y1, ImageBuffer(LMSurf\Image), TextureBuffer(Tex))
			EndIf
			
			; Scale the original UV's to the new position and scale
			DX# = (Float(Img\X1)+1.5) / Float(lmapsize)
			DY# = (Float(Img\Y1)+1.5) / Float(lmapsize)
			
			ScaleU# = (Float(IW)-3) / Float(lmapsize)
			ScaleV# = (Float(IH)-3) / Float(lmapsize)
			
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
					x1% = Int((LMSurf\Tris[i]\U[0]) * Float(lmapsize))
					y1% = Int((LMSurf\Tris[i]\V[0]) * Float(lmapsize))
					x2% = Int((LMSurf\Tris[i]\U[1]) * Float(lmapsize))
					y2% = Int((LMSurf\Tris[i]\V[1]) * Float(lmapsize))
					Line(x1, y1, x2, y2)

					x1% = Int((LMSurf\Tris[i]\U[1]) * Float(lmapsize))
					y1% = Int((LMSurf\Tris[i]\V[1]) * Float(lmapsize))
					x2% = Int((LMSurf\Tris[i]\U[2]) * Float(lmapsize))
					y2% = Int((LMSurf\Tris[i]\V[2]) * Float(lmapsize))
					Line(x1, y1, x2, y2)

					x1% = Int((LMSurf\Tris[i]\U[2]) * Float(lmapsize))
					y1% = Int((LMSurf\Tris[i]\V[2]) * Float(lmapsize))
					x2% = Int((LMSurf\Tris[i]\U[0]) * Float(lmapsize))
					y2% = Int((LMSurf\Tris[i]\V[0]) * Float(lmapsize))
					Line(x1, y1, x2, y2)
					
					;Rect(Img\X1+1, Img\Y1+1, IW-2, IH-2, False)
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
			
			If r1 > maxvalue_r Then maxvalue_r = r1
			If g1 > maxvalue_g Then maxvalue_g = g1
			If b1 > maxvalue_b Then maxvalue_b = b1

			If r1 < minvalue_r Then minvalue_r = r1
			If g1 < minvalue_g Then minvalue_g = g1
			If b1 < minvalue_b Then minvalue_b = b1

		Next
	Next
	UnlockBuffer(ImageBuffer(img))
	
	contrast_r = maxvalue_r - minvalue_r
	contrast_g = maxvalue_g - minvalue_g
	contrast_b = maxvalue_b - minvalue_b

	If (contrast_r > contrast_g) And (contrast_r > contrast_b) Then Return contrast_r
	If (contrast_g > contrast_r) And (contrast_g > contrast_b) Then Return contrast_g

	Return contrast_b
End Function

;
;	Setup the surface
;
Function LMSetupSurface.LMSurface(LMSurf.LMSurface, lumelsize#, blurradius)

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
	
	; Create image size based on the lumel density
	LMSizeX% = (LMSurf\UDelta / lumelsize)
	LMSizeY% = (LMSurf\VDelta / lumelsize)
	
	; Minimum texture size
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
	For i = 0 To LMSurf\NTris-1
		For j = 0 To LM_VERTS
			VertexCoords(LMSurf\Tris[i]\Surf, LMSurf\Tris[i]\VertIndex[j], 99999, 99999, 99999)
		Next
	Next

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
	
	CenterU# = (1.0 / Float(LMSizeX)) / 2
	CenterV# = (1.0 / Float(LMSizeY)) / 2
	
	For Light.LMLight = Each LMLight
		For y% = 0 To LMSizeY-1
			For x% = 0 To LMSizeX-1
				
				; Find the UV
				u# = Float(x) / Float(LMSizeX) + CenterU
				v# = Float(y) / Float(LMSizeY) + CenterV
				
				; Transform to world coordinates
				N_UEdgeX# = LMSurf\UEdgeX * u#  :  N_UEdgeY# = LMSurf\UEdgeY * u#  :  N_UEdgeZ# = LMSurf\UEdgeZ * u#
				N_VEdgeX# = LMSurf\VEdgeX * v#  :  N_VEdgeY# = LMSurf\VEdgeY * v#  :  N_VEdgeZ# = LMSurf\VEdgeZ * v#
				
				LumX# = (LMSurf\OriginX + N_UEdgeX + N_VEdgeX)
				LumY# = (LMSurf\OriginY + N_UEdgeY + N_VEdgeY)
				LumZ# = (LMSurf\OriginZ + N_UEdgeZ + N_VEdgeZ)
				
				PositionEntity(LumelPivot, LumX, LumY, LumZ)
				RotateEntity(LumelPivot, 0, 0, 0)
				AlignToVector(LumelPivot, LMSurf\UEdgeX, LMSurf\UEdgeY, LMSurf\UEdgeZ, 1)
				AlignToVector(LumelPivot, LMSurf\VEdgeX, LMSurf\VEdgeY, LMSurf\VEdgeZ, 3)
				
				lx# = Light\X : ly# = Light\Y : lz# = Light\Z
				RotateEntity(LightPivot, Light\Pitch, Light\Yaw, Light\Roll)

				If Light\Directional
					TFormVector(0, 0, -1, LightPivot, 0)
					lx# = LumX + TFormedX() * 9999				
					ly# = LumY + TFormedY() * 9999
					lz# = LumZ + TFormedZ() * 9999
					Dist# = 0.0001
				EndIf

				PositionEntity(LightPivot, lx, ly, lz)
				
				If Not Light\Directional
					Dist# = EntityDistance(LightPivot, LumelPivot)
				EndIf	
				
				; If this light can light this lumel		
				If (Dist <= Light\Range) And (Dist > 0)
					DX# = LumX - lx
					DY# = LumY - ly
					DZ# = LumZ - lz
				
					TriFound = False
					
					For i = 0 To LMSurf\NTris-1
						Tri.LMTriangle = LMSurf\Tris[i]
						x1# = Tri\X[0] : y1# = Tri\Y[0] : z1# = Tri\Z[0]
						x2# = Tri\X[1] : y2# = Tri\Y[1] : z2# = Tri\Z[1]
						x3# = Tri\X[2] : y3# = Tri\Y[2] : z3# = Tri\Z[2]
						
						If Ray_Intersect_Triangle(lx, ly, lz, DX, DY, DZ, x1, y1, z1, x2, y2, z2, x3, y3, z3, True, False)
							px# = LumX
							py# = LumY
							pz# = LumZ
							
							a#  = GetTriangleArea(x1, y1, z1, x2, y2, z2, x3, y3, z3)
							bu# = GetTriangleArea(x2, y2, z2, x3, y3, z3, px, py, pz) / a
							bv# = GetTriangleArea(x3, y3, z3, x1, y1, z1, px, py, pz) / a
							bw# = GetTriangleArea(x1, y1, z1, x2, y2, z2, px, py, pz) / a
							
							INX# = ((bu * Tri\VNX[0]) + (bv * Tri\VNX[1]) + (bw * Tri\VNX[2]))
							INY# = ((bu * Tri\VNY[0]) + (bv * Tri\VNY[1]) + (bw * Tri\VNY[2]))
							INZ# = ((bu * Tri\VNZ[0]) + (bv * Tri\VNZ[1]) + (bw * Tri\VNZ[2]))
							
							size# = Sqr(INX*INX + INY*INY + INZ*INZ)
							INX = INX / size
							INY = INY / size
							INZ = INZ / size
														
							TriFound = True
							Exit
						EndIf
					Next
					
					If Not TriFound
						INX# = LMSurf\NX
						INY# = LMSurf\NY
						INZ# = LMSurf\NZ
					EndIf

					; Normal vector between lumel and light
					NX# = (lx-LumX) / Dist
					NY# = (ly-LumY) / Dist
					NZ# = (lz-LumZ) / Dist
					
					; Dot product to find the cosine angle between the surface normal and incident light normal
					CosAngle# = (NX * INX) + (NY * INY) + (NZ * INZ)
					
					; Lumel face front of the light
					If CosAngle > 0
						LMLightProcess(x, y, Light, Dist, CosAngle, LumelPivot, LightPivot, lumelsize)
					EndIf 
				EndIf ; Dist < Light\Range
				
			Next ; x
		Next ; y
	Next ;Light
	
	; Move it back
	For i = 0 To LMSurf\NTris-1
		For j = 0 To LM_VERTS
			VertexCoords(LMSurf\Tris[i]\Surf, LMSurf\Tris[i]\VertIndex[j], LMSurf\Tris[i]\OX[j], LMSurf\Tris[i]\OY[j], LMSurf\Tris[i]\OZ[j])
		Next
	Next

	UnlockBuffer(ImgBuf)

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
	
	; Create one pixel border	
	IW = ImageWidth(LMSurf\Image)
	IH = ImageHeight(LMSurf\Image) 
	
	img2 = CreateImage(IW + 2, IH + 2)
	SetBuffer(ImageBuffer(img2))
	ClsColor(255, 0, 255)
	Cls()
	CopyRect(0, 0, IW, IH, 1, 1, ImageBuffer(LMSurf\Image), ImageBuffer(img2))
	FreeImage(LMSurf\Image)
	LMSurf\Image = img2
	
	; Extend the color to the edges
	LockBuffer()
	IW = ImageWidth(LMSurf\Image)
	IH = ImageHeight(LMSurf\Image) 
	For x = 0 To IW - 1
		ARGB = ReadPixelFast(x, 1)
		WritePixelFast(x, 0, ARGB)

		ARGB = ReadPixelFast(x, IH-2)
		WritePixelFast(x, IH-1, ARGB)
	Next

	For y = 0 To IH - 1
		ARGB = ReadPixelFast(1, y)
		WritePixelFast(0, y, ARGB)

		ARGB = ReadPixelFast(IW-2, y)
		WritePixelFast(IW-1, y, ARGB)
	Next

	UnlockBuffer()
	
	LMSurf\ImageSize = IW * IH
	SetBuffer(BackBuffer())
	
End Function


Function LMLightProcess(x%, y%, Light.LMLight, Dist#, CosAngle#, LumelPivot, LightPivot, lumelsize#)
	; Measure attenuation
	Att# = 1 / (Light\Att[0] + (Light\Att[1] * Dist) + (Light\Att[2] * Dist * Dist))
			
	; Lambert + attenuation
	Intensity# = (Light\Bright * CosAngle) * Att
	
	If Intensity < 0.0 Then Intensity = 0.0
	If Intensity > 1.0 Then Intensity = 1.0
	
	If Intensity > LM_INTENSITY_EPSILON
		NHits = 0
		NFired = 0
		
		If Light\CastShadows
			; Center pick
			NFired = NFired + 1
			If EntityVisible(LightPivot, LumelPivot)
				NHits = NHits + 1
			EndIf
			
			If g_LMParams\ExtraCheck
				; Left up
				NFired = NFired + 1
				MoveEntity(LumelPivot, -lumelsize/3, 0, -lumelsize/3)
				If EntityVisible(LightPivot, LumelPivot)
					NHits = NHits + 1
				EndIf
				
				; Right up
				NFired = NFired + 1
				MoveEntity(LumelPivot, lumelsize/1.5, 0, 0)
				If EntityVisible(LightPivot, LumelPivot)
					NHits = NHits + 1
				EndIf
				
				; Right bottom
				NFired = NFired + 1
				MoveEntity(LumelPivot, 0, 0, lumelsize/1.5)
				If EntityVisible(LightPivot, LumelPivot)
					NHits = NHits + 1
				EndIf
	
				; Left bottom
				NFired = NFired + 1
				MoveEntity(LumelPivot, -lumelsize/1.5, 0, 0)
				If EntityVisible(LightPivot, LumelPivot)
					NHits = NHits + 1
				EndIf
			EndIf
		Else
			NHits = 1
			NFired = 1
		EndIf
	
		If NHits > 0
			Intensity# = Intensity# * (Float(NHits) / Float(NFired))
		
			;
			; Add the incident light the pixel
			;
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

Function GetTriangleArea#(x1#, y1#, z1#, x2#, y2#, z2#, x3#, y3#, z3#)
  	ux# = x2# - x1#
   	uy# = y2# - y1#
  	uz# = z2# - z1#
    vx# = x3# - x1#
    vy# = y3# - y1#
   	vz# = z3# - z1#

	nx# = (uy# * vz#) - (vy# * uz#)
	ny# = (uz# * vx#) - (vz# * ux#)
	nz# = (ux# * vy#) - (vx# * uy#)
	
	Return Sqr(nx*nx + ny*ny + nz*nz) * 0.5
End Function


Global g_TriNormalX#, g_TriNormalY#, g_TriNormalZ#

Function GetTriangleNormal(x1#, y1#, z1#, x2#, y2#, z2#, x3#, y3#, z3#)
  	ux# = x2# - x1#
   	uy# = y2# - y1#
  	uz# = z2# - z1#
    vx# = x3# - x1#
    vy# = y3# - y1#
   	vz# = z3# - z1#

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
		
		fprint$ = SurfaceFingerPrint(mesh, surf)
		WriteString(file, fprint)
		
		count = CountVertices(surf)
		WriteInt(file, count)
		For vercount = 0 To count-1
			WriteFloat(file,VertexU(surf,vercount,coordset))
			WriteFloat(file,VertexV(surf,vercount,coordset))
		Next
	Next
	
	WriteString(file, "")
	
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
	
	fprint$ = ReadString(file)
	
	While fprint <> ""
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
		fprint = ReadString(file)
	Wend
	
	CloseFile file
End Function

Function FindSurfFingerPrint(mesh, fingerprint$)
	For surfcount = 1 To CountSurfaces(mesh)
		surf = GetSurface(mesh, surfcount)
		
		If SurfaceFingerPrint(mesh, surf) = fingerprint
			Return surf
		EndIf
	Next
	
	Return 0
End Function

Function SurfaceFingerPrint$(mesh, surf)
	fingerprint$ = ""
	
	brush = GetSurfaceBrush(surf)
	If brush
		tex = GetBrushTexture(brush)
		If tex
			fingerprint = StripPath(TextureName(tex)) + "|"
			FreeTexture(tex)
		EndIf
		FreeBrush(brush)
	EndIf
	
	fingerprint = fingerprint + CountTriangles(surf) + "," +CountVertices(surf)

	Return fingerprint
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

;Include "waterplane.bb"

;
;	Example function
;
;	Hold 2th mouse button do turn the cam
;	Use the arrrows to move
; 	Click on a object to lightmap it
;	Press F2 to load the saved lightmap
Function LMExample()
	Graphics3D(800, 600)
	SetBuffer(BackBuffer())
	
	; Create some stuff in the world
	camera = CreateCamera()
	PositionEntity(camera, 17, 18, 18)
	
	;wplane.TWaterPlane = CreateWaterPlane("plane.b3d", 256, 2, 2)
	;MoveEntity(wplane\Entity, 0, 2, 0)

	cube1 = CreateCube()
	FlipMesh(cube1)	
	PositionMesh(cube1, 0, 1.0, 0)
	ScaleMesh(cube1, 20, 15, 20)
	EntityPickMode(cube1, 2)
	NameEntity(cube1, "cube1")
	
	PointEntity(camera, cube1) ; Look at the cube
	
	cube2 = CreateSphere(10)
	PositionMesh(cube2, 0, 4, 0) 
	ScaleMesh(cube2, 2, 2, 2)
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
				
				CreateLMLight( -8.1, 3, -8, 219, 219, 255, 0, True, 3)
				CreateLMLight(  8, 3,  3.1, 255, 255, 219, 0, True, 3)
				
				If EntityName(ent) = "cube1"
					tex = LightMapMesh(ent, 0.2, 1024, 1, "Lightmapping " + EntityName(ent))
				Else
					tex = LightMapMesh(ent, 0.02, 1024, 1, "Lightmapping " + EntityName(ent))	
				EndIf
				
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
		;UpdateWaterPlane(wplane)
		UpdateWorld()

		RenderWorld()
		Flip()	
		
		;RenderWaterPlane(wplane, camera)
	Wend
	
	EndGraphics()
End Function


; -------------------------------------------------------------------------------------------------------------------
; This function returns TRUE if a ray intersects a triangle.
; 	It also calculates the UV coordinates of said colision as part of the intersection test,
; 	but does not return them.
;
; V0xyz, V1xyz, and V2xyz are the locations of the three vertices of the triangle.
;
; 	These vertices should be wound in CLOCKWISE order.  By clockwise I mean that if you face the front side of the
; 	trianngle, the vertcies go around the trangle from V0 to V1 to V2 in a clockwise direction.  This is the same
; 	as Blitz, so just pass the vertcies for a triangle in the same order that Blitz does.
;
;	The UV's generated by the function are set up as follows:
; 		V0 is the location of UV(0,0)
;  		V1 is the location of UV(0,1)
;  		V2 is the location of UV(1,0)
;
; 	This is useful to know if you want to know the exact location in texture space of the collision.
;	You can easily modify the function to return the values of T#, U#, and V#.
;
; Pxyz is a the start of the line.
;	Triangles which are "behind" this point are ignored. 
; 	"Behind" is defined as the direction opposite that which Dxyz points in.
;
; Dxyz is a vector providing the slope of the line.
; 	Dxyz does not have to be normalized.
;
; If Extend_To_Infinity is set to false, then the length of Dxyz is how far the ray extends. 
; 	So if you want an endpoint on your ray beyond which no triangles will be detected, subtract the position 
; 	of Pxyz from your endpoint's position, and pass that ato the function as Dxyz.  Ie: (Dx = P2x-P1x)
;
; If Cull_Backfaces is set to true, then if the specified ray passes through the triangle from it's back side, then
; it will not register that it hit that triangle.
; -------------------------------------------------------------------------------------------------------------------
Function Ray_Intersect_Triangle(Px#, Py#, Pz#, Dx#, Dy#, Dz#, V0x#, V0y#, V0z#, V1x#, V1y#, V1z#, V2x#, V2y#, V2z#, Extend_To_Infinity=True, Cull_Backfaces=False)
	
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
	
	; The ray intersects the triangle!		
	Return True

End Function

; Returns the file from the specifed path 
Function StripPath$(path$) 
    For a = Len(path$) To 1 Step -1 
        byte$ = Mid(path$,a,1) 
        If byte$ = "\" 
            Return Right(path$,Len(path$)-a) 
        EndIf 
    Next 
    Return path$ 
End Function
