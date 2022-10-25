; ID: 1134
; Author: Nilium
; Date: 2004-08-14 02:30:12
; Title: gile[s] .gls Loader
; Description: Loads gile[s] .gls scenes

Graphics3D 800,600,32,2

Camera = CreateCamera()
Ruin = LoadGLS("ruin.gls")

PositionEntity Camera,10*2.2,20*2.2,-30*2.2
If Ruin Then PointEntity Camera,Ruin
Repeat
	UpdateWorld
	RenderWorld
	Flip
Until KeyHit(1)




;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; gls Loader											;;
;; gile[s]: www.frecle.net/giles						;;
;; Written by Noel R. Cower							;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; GLOBALS
Global glsMaterialCount = 0
Global glsTextureCount = 0
Global glsLightMapCount = 0

;; TYPES
Type glsMaterial
	Field Index
	Field Brush
	Field Name$
	Field R,G,B,A#
	Field Shine#
	Field FX%
	Field Blend
End Type

Type glsTexture
	Field Index
	Field Texture
	Field Path$
	Field Flags%
End Type

Type glsLightmap
	Field Index
	Field Texture
End Type

;; CONSTANTS
;; Yes, I typed all of these and I probably don't even use half of them.
Const GLS_HEADER = $FFFF
Const GLS_AUTHOR = $F000
Const GLS_MODELS = $1000
Const GLS_MODEL = $1001
Const GLS_MODEL_NAME = $1002
Const GLS_MODEL_POSITION = $1003
Const GLS_MODEL_ROTATION = $1004
Const GLS_MODEL_SCALE = $1005
Const GLS_MODEL_CUSTOMPROPS = $1006
Const GLS_MODEL_FILE = $1007
Const GLS_MODEL_HIDDEN = $1008
Const GLS_MESH = $2000
Const GLS_MESH_OVERRIDE = $2001
Const GLS_MESH_BACKLIGHT = $2002
Const GLS_MESH_RECEIVESHADOW = $2003
Const GLS_MESH_CASTSHADOW = $2004
Const GLS_MESH_RECEIVEGI = $2005
Const GLS_MESH_AFFECTGI = $2006
Const GLS_MESH_SURFACES = $2100
Const GLS_MESH_SURF = $2101
Const GLS_MESH_SURFVERTS = $2102
Const GLS_MESH_SURFPOLYS = $2103
Const GLS_MESH_SURFMATERIAL = $2104
Const GLS_MESH_SURFVERTFORMAT = $2105
Const GLS_MESH_SURFVERTDATA = $2106
Const GLS_MESH_SURFPOLYDATA = $2107
Const GLS_PIVOT = $3000
Const GLS_LIGHT = $4000
Const GLS_LIGHT_TYPE = $4001
Const GLS_LIGHT_ACTIVE = $4002
Const GLS_LIGHT_CASTSHADOWS = $4003
Const GLS_LIGHT_INFINITE = $4004
Const GLS_LIGHT_OVERSHOOT = $4005
Const GLS_LIGHT_RADIUS = $4006
Const GLS_LIGHT_RED = $4007
Const GLS_LIGHT_GREEN = $4008
Const GLS_LIGHT_BLUE = $4009
Const GLS_LIGHT_INTENSITY = $400A
Const GLS_LIGHT_NEAR = $400B
Const GLS_LIGHT_FAR = $400C
Const GLS_LIGHT_INNER = $400D
Const GLS_LIGHT_OUTER = $400E
Const GLS_LIGHT_TOON = $400F
Const GLS_LIGHT_TOONLEVELS = $40010
Const GLS_MATERIALS = $5000
Const GLS_MAT = $5001
Const GLS_MAT_NAME = $5002
Const GLS_MAT_RED = $5003
Const GLS_MAT_GREEN = $5004
Const GLS_MAT_BLUE = $5005
Const GLS_MAT_ALPHA = $5006
Const GLS_MAT_SELFILLUMINATION = $5007
Const GLS_MAT_SHININESS = $5008
Const GLS_MAT_FX = $5009
Const GLS_MAT_BLEND = $500A
Const GLS_MAT_LIGHTMETHOD = $500B
Const GLS_MAT_LIGHTMAP = $500C
Const GLS_MAT_RECEIVEBACK = $500D
Const GLS_MAT_RECEIVESHADOW = $500E
Const GLS_MAT_CASTSHADOW = $500F
Const GLS_MAT_RECEIVEGI = $5010
Const GLS_MAT_AFFECTGI = $5011
Const GLS_MAT_TEXLAYER = $5012
Const GLS_TEXTURES = $6000
Const GLS_TEX = $6001
Const GLS_TEX_FILE = $6002
Const GLS_TEX_SCALEU = $6003
Const GLS_TEX_SCALEV = $6004
Const GLS_TEX_OFFSETU = $6005
Const GLS_TEX_OFFSETV = $6006
Const GLS_TEX_ANGLE = $6007
Const GLS_TEX_FLAGS = $6008
Const GLS_TEX_BLEND = $6009
Const GLS_TEX_COORDSET = $600A
Const GLS_LIGHTMAPS = $7000
Const GLS_LMAP = $7001
Const GLS_LMAP_NAME = $7002
Const GLS_LMAP_FILE = $7003
Const GLS_LMAP_WIDTH = $7004
Const GLS_LMAP_HEIGHT = $7005
Const GLS_LMAP_NONUNIFORM = $7006
Const GLS_LMAP_USECUSTOMTEXEL = $7007
Const GLS_LMAP_CUSTOMTEXEL = $7008
Const GLS_LMAP_REPACK = $7009
Const GLS_LMAP_DATA = $700A
Const GLS_RENDER = $8000
Const GLS_RENDER_CLEARBEFORERENDER = $8001
Const GLS_RENDER_DIRENABLE = $8002
Const GLS_RENDER_GIENABLE = $8003
Const GLS_RENDER_RAYBIAS = $8004
Const GLS_RENDER_DIRMULTIPLY = $8005
Const GLS_RENDER_DIRBACKSHAD = $8006
Const GLS_RENDER_DIRSHADOWS = $8007
Const GLS_RENDER_DIRSOFT = $8008
Const GLS_RENDER_SOFTSAMPLES = $8009
Const GLS_RENDER_GIIGNORETEX = $800A
Const GLS_RENDER_GIITERATIONS = $800B
Const GLS_RENDER_GIDENSITY = $800C
Const GLS_RENDER_GISAMPLES = $800D
Const GLS_RENDER_GIMULTIPLY = $800E
Const GLS_RENDER_SKYENABLE = $800F
Const GLS_RENDER_SKYRED = $8010
Const GLS_RENDER_SKYBLUE = $8011
Const GLS_RENDER_SKYGREEN = $8012
Const GLS_RENDER_SKYMULTIPLY = $8013
Const GLS_RENDER_AUTOBLUR = $8014
Const GLS_RENDER_AUTOEXPAND = $8015
Const GLS_RENDER_AUTOBLURRADIUS = $8016

;; FUNCTIONS
;; Loads a .gls model
Function LoadGLS(Path$)
	glsMaterialCount = 0
	glsLightMapCount = 0
	glsTextureCount = 0
	If FileType(Path$) <> 1 Then Return 0
	F = ReadFile(Path$)
	If F = 0 Then Return 0
	Local Pivot = CreatePivot()
	While Not Eof(F)
		ChunkID% = ReadInt(F)
		ChunkSize% = ReadInt(F)
		FPos = FilePos(F)
		Select ChunkID
			Case GLS_HEADER
				Header$ = ReadString(F)
				Version# = ReadFloat(F)
			
			Case GLS_AUTHOR
				Author$ = ReadString(F)
				NameEntity(Pivot,Header+" "+Version+"    Made by "+Author)
			
			Case GLS_MODELS
				DebugLog "Entering GLS_MODELS chunk"
			
			Case GLS_MODEL
				DebugLog "Entering GLS_MODEL chunk"
				SetScale = 0
				SetRotation = 0
				SetPosition = 0
			
			Case GLS_MODEL_NAME
				ModelName$ = ReadString(F)
			
			Case GLS_MODEL_POSITION
				SetPosition = 1
				PX# = ReadFloat(F)
				PY# = ReadFloat(F)
				PZ# = ReadFloat(F)
			
			Case GLS_MODEL_ROTATION
				SetRotation = 1
				RX# = ReadFloat(F)
				RY# = ReadFloat(F)
				RZ# = ReadFloat(F)
			
			Case GLS_MODEL_SCALE
				SetScale = 1
				SX# = ReadFloat(F)
				SY# = ReadFloat(F)
				SZ# = ReadFloat(F)
			
			Case GLS_MODEL_CUSTOMPROPS
				Properties$ = ReadString(F)
				ModelName$ = Trim(ModelName$+" "+Properties)
			
			Case GLS_MODEL_HIDDEN
				ModelHidden% = ReadByte(F)
			
			Case GLS_MESH
				Local Mesh = CreateMesh()
				EntityParent Mesh,Pivot,0
				If SetScale Then ScaleEntity Mesh,SX,SY,SZ,0
				If SetRotation Then RotateEntity Mesh,RX,RY,RZ,0
				If SetPosition Then PositionEntity Mesh,PX,PY,PZ,0
				NameEntity Mesh,ModelName
				If ModelHidden Then HideEntity Mesh
			
			Case GLS_MESH_OVERRIDE
				MeshOverride = ReadByte(F)
			
			Case GLS_MESH_BACKLIGHT
				MeshBacklight = ReadByte(F)
			
			Case GLS_MESH_RECEIVESHADOW
				MeshReceiveShadow = ReadByte(F)
				
			Case GLS_MESH_CASTSHADOW
				MeshCastShadow = ReadByte(F)
			
			Case GLS_MESH_RECEIVEGI
				MeshReceiveGI = ReadByte(F)
			
			Case GLS_MESH_AFFECTGI
				MeshAffectGI = ReadByte(F)
			
			Case GLS_MESH_SURFACES
				DebugLog "Entering GLS_MESH_SURFACES chunk"
			
			Case GLS_MESH_SURF
				Surface = CreateSurface(Mesh)
			
			Case GLS_MESH_SURFVERTS
				Vertices = ReadShort(F)
			
			Case GLS_MESH_SURFPOLYS
				Polygons = ReadShort(F)
			
			Case GLS_MESH_SURFMATERIAL
				MaterialIndex = ReadInt(F)
				For M.glsMaterial = Each glsMaterial
					If M\Index = MaterialIndex Then
						PaintSurface Surface,M\Brush
						Exit
					EndIf
				Next
			
			Case GLS_MESH_SURFVERTFORMAT
				VertFormat = ReadInt(F)
				VPosition = 0
				VNormals = 0
				VColor = 0
				VLight = 0
				VLMap = 0
				VTexCoords = 0
				If (VertFormat And 0) = 0 Then VPosition = 1
				If (VertFormat And 1) = 1 Then VNormals = 1
				If (VertFormat And 2) = 2 Then VColor = 1
				If (VertFormat And 4) = 4 Then VLight = 1
				If (VertFormat And 8) = 8 Then VLMap = 1
				If (VertFormat And 16) = 16 Then VTexCoords = 1
			
			Case GLS_MESH_SURFVERTDATA
				For N = 1 To Vertices
					If VPosition Then
						VX# = ReadFloat(F)
						VY# = ReadFloat(F)
						VZ# = ReadFloat(F)
					Else
						VX = 0
						VY = 0
						VZ = 0
					EndIf
					
					If VNormals Then
						VNX# = ReadFloat(F)
						VNY# = ReadFloat(F)
						VNZ# = ReadFloat(F)	
					Else
						VNX = 0
						VNY = 0
						VNZ = 0
					EndIf
					
					If VColor Then
						VCR = ReadByte(F)
						VCG = ReadByte(F)
						VCB = ReadByte(F)
						VCA = ReadByte(F)
					Else
						VCR = 255
						VCG = 255
						VCB = 255
						VCA = 255
					EndIf
					
					If VLight Then
						VLR# = Float(ReadByte(F))/255
						VLG# = Float(ReadByte(F))/255
						VLB# = Float(ReadByte(F))/255
					Else
						VLR = 1
						VLG = 1
						VLB = 1
					EndIf
					
					If VLMap Then
						VLU# = ReadFloat(F)
						VLV# = ReadFloat(F)
					Else
						VLU = 0
						VLV = 0
					EndIf
					
					If VTexCoords Then
						VU# = ReadFloat(F)
						VV# = ReadFloat(F)
					Else
						VU = 0
						VV = 0
					EndIf
					
					V = AddVertex(Surface,VX,VY,VZ,VU,VV)
					VertexTexCoords Surface,V,VLU,VLV,0,1
					VertexNormal Surface,V,VNX,VNY,VNZ
					VertexColor Surface,V,VCR*VLR,VCG*VLG,VCB*VLB,VCA
				Next
			
			Case GLS_MESH_SURFPOLYDATA
				For N = 1 To Polygons
					VI0 = ReadShort(F)
					VI1 = ReadShort(F)
					VI2 = ReadShort(F)
					AddTriangle Surface,VI0,VI1,VI2
				Next
			
			Case GLS_PIVOT
				Mesh = CreatePivot()
				EntityParent Mesh,Pivot,0
				If SetScale Then ScaleEntity Mesh,SX,SY,SZ,0
				If SetRotation Then RotateEntity Mesh,RX,RY,RZ,0
				If SetPosition Then PositionEntity Mesh,PX,PY,PZ,0
				NameEntity Mesh,ModelName
				If ModelHidden Then HideEntity Mesh
			
			Case GLS_LIGHT_TYPE
				InnerCone# = -1
				OuterCone# = -1
				LightType = ReadByte(F)
				If LightType = 4 Then LightType = 2
				Mesh = CreateLight(LightType)
				EntityParent Mesh,Pivot,0
				If SetScale Then ScaleEntity Mesh,SX,SY,SZ,0
				If SetRotation Then RotateEntity Mesh,RX,RY,RZ,0
				If SetPosition Then PositionEntity Mesh,PX,PY,PZ,0
				NameEntity Mesh,ModelName
				If ModelHidden Then HideEntity Mesh
			
			Case GLS_LIGHT_ACTIVE
				LightActive = ReadByte(F)
				If LightActive=0 Then HideEntity Mesh
			
			Case GLS_LIGHT_RADIUS
				LightRadius = ReadFloat(F)
				LightRange Mesh,LightRadius
			
			Case GLS_LIGHT_RED
				LR% = ReadFloat(F)*255
			
			Case GLS_LIGHT_GREEN
				LG% = ReadFloat(F)*255
			
			Case GLS_LIGHT_BLUE
				LB% = ReadFloat(F)*255
			
			Case GLS_LIGHT_INTENSITY
				Intensity# = ReadFloat(F)
				LightColor Mesh,LR*Intensity,LG*Intensity,LB*Intensity
			
			Case GLS_LIGHT_INNER
				InnerCone = ReadFloat(F)
				If OuterCone <> -1 Then
					LightConeAngles Mesh,InnerCone,OuterCone
				EndIf
			
			Case GLS_LIGHT_OUTER
				OuterCone = ReadFloat(F)
				If InnerCone <> -1 Then
					LightConeAngles Mesh,InnerCone,OuterCone
				EndIf
			
			Case GLS_MATERIALS
				DebugLog "Entering GLS_MATERIALS chunk"
			
			Case GLS_MAT
				M.glsMaterial = New glsMaterial
				M\Index = glsMaterialCount
				M\Brush = CreateBrush()
				glsMaterialCount = glsMaterialCount + 1
				ReSet = 0
				GrSet = 0
				BlSet = 0
			
			Case GLS_MAT_NAME
				M\Name = ReadString(F)
			
			Case GLS_MAT_RED
				M\R = ReadFloat(F)*255
				ReSet = 1
				If BlSet And ReSet And GrSet Then
					BrushColor M\Brush,M\R,M\G,M\B
				EndIf
			
			Case GLS_MAT_GREEN
				M\G = ReadFloat(F)*255
				ReSet = 1
				If BlSet And ReSet And GrSet Then
					BrushColor M\Brush,M\R,M\G,M\B
				EndIf
			
			Case GLS_MAT_BLUE
				M\B = ReadFloat(F)*255
				BlSet = 1
				If BlSet And ReSet And GrSet Then
					BrushColor M\Brush,M\R,M\G,M\B
				EndIf
			
			Case GLS_MAT_ALPHA
				M\A = ReadFloat(F)
				BrushAlpha M\Brush,M\A
			
			Case GLS_MAT_SHININESS
				M\Shine = ReadFloat(F)
				BrushShininess M\Brush,M\Shine
			
			Case GLS_MAT_FX
				M\FX = ReadInt(F)
				BrushFX M\Brush,M\FX
			
			Case GLS_MAT_BLEND
				M\Blend = ReadInt(F)
				BrushBlend M\Brush,M\Blend
			
			Case GLS_MAT_TEXLAYER
				Layer = ReadByte(F)
				Index = ReadShort(F)
				For T.glsTexture = Each glsTexture
					If T\Index = Index Then
						BrushTexture M\Brush,T\Texture,0,Layer
						Exit
					EndIf
				Next
			
			Case GLS_MAT_LIGHTMAP
				Index = ReadShort(F)
				For L.glsLightmap = Each glsLightmap
					If L\Index = Index Then
						BrushTexture M\Brush,L\Texture,0,7
					EndIf
				Next
			
			Case GLS_TEXTURES
				DebugLog "Entering GLS_TEXTURES chunk"
			
			Case GLS_TEX
				T.glsTexture = New glsTexture
				T\Index = glsTextureCount
				glsTextureCount = glsTextureCount + 1
				eTOV# = 0
				eTOU# = 0
				eSOV# = 1
				eSOU# = 1
				TBlend = 1
				TCoords = 0
				TAngle# = 0
				T\Flags = 9
			
			Case GLS_TEX_FILE
				T\Path$ = ReadString(F)
			
			Case GLS_TEX_FLAGS
				T\Flags = ReadInt(F)
				T\Texture = LoadTexture(T\Path,T\Flags)
				TextureCoords T\Texture,TCoords
				TextureBlend T\Texture,TBlend
				ScaleTexture T\Texture,eSOU,eSOV
				PositionTexture T\Texture,ePOU,ePOV
				RotateTexture T\Texture,TAngle
			
			Case GLS_TEX_OFFSETU
				eTOU# = ReadFloat(F)
			
			Case GLS_TEX_OFFSETV
				eTOV# = ReadFloat(F)
			
			Case GLS_TEX_SCALEU
				eSOU# = ReadFloat(F)
			
			Case GLS_TEX_SCALEV
				eSOV# = ReadFloat(F)
			
			Case GLS_TEX_ANGLE
				TAngle# = ReadFloat(F)
			
			Case GLS_TEX_BLEND
				TBlend = ReadInt(F)
			
			Case GLS_TEX_COORDSET
				TCoords =  ReadByte(F)
			
			Case GLS_LIGHTMAPS
				DebugLog "Entering GLS_LIGHTMAPS chunk"
			
			Case GLS_LMAP
				L.glsLightmap = New glsLightmap
				glsLightMapCount = glsLightMapCount + 1
				L\Index = glsLightMapCount
				WSet=0
				HSet=0
			
			Case GLS_LMAP_WIDTH
				WSet = 1
				LWidth = ReadShort(F)
				If HSet And WSet Then L\Texture = CreateTexture(LWidth,LHeight,1)
			
			Case GLS_LMAP_HEIGHT
				HSet = 1
				LHeight = ReadShort(F)
				If HSet And WSet Then L\Texture = CreateTexture(LWidth,LHeight,1)
			
			Case GLS_LMAP_DATA
				TextureBlend L\Texture,2
				TextureCoords L\Texture,1
				Buffer = TextureBuffer(L\Texture)
				LockBuffer(Buffer)
				For u = 1 To LWidth
					For v = 1 To LHeight
						R = ReadByte(F)
						G = ReadByte(F)
						B = ReadByte(F)
						Pixel = $FF Shl 24 Or R Shl 16 Or G Shl 8 Or B
						WritePixelFast u,v,Pixel,Buffer
					Next
				Next
				UnlockBuffer(Buffer)		
			
			Default
				SeekFile F,FPos+ChunkSize
		End Select
	Wend
	
	For M.glsMaterial = Each glsMaterial
		FreeBrush M\Brush
	Next
	
	For L.glsLightmap = Each glsLightmap
		FreeTexture L\Texture
	Next
	
	For T.glsTexture = Each glsTexture
		FreeTexture T\Texture
	Next
	
	Delete Each glsMaterial
	Delete Each glsLightmap
	Delete Each glsTexture
	
	CloseFile F
	Return Pivot
End Function

;; Overloads the default ReadString() function with one that isn't as piss
Function ReadString$(Stream,BreakCharacter = 0)
	If Stream = 0 Then Return ""
	If Eof(Stream) Then Return ""
	Repeat
		C = ReadByte(Stream)
		If C = BreakCharacter Or Eof(Stream) Then Exit
		S$ = S$ + Chr(C)
	Forever
	Return S$
End Function
