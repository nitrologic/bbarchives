; ID: 2243
; Author: Nilium
; Date: 2008-04-14 06:49:30
; Title: CLE - Cower Landscape Editor
; Description: A landscape/terrain editor

;#Region INPUT GLOBALS
	;; Set these globals inside your CLEUpdateGUI* function- either 2D or 3D, but they have to be set in order for CLE to work properly
	
	Global PaintMode%  = 0					;; The paint mode (0 for texture painting, 1 for raising terrain, 2 for lowering terrain, 3 for smoothing terrain)
	Global BrushSize% = 0					;; The size of the brush
	Global BrushSpeed# = .1					;; The speed of the brush
	Global BrushLayer% = 0					;; The current layer to be painted on
	Global MXS#,MYS#,MZS#,MX#,MY#,MZ#		;; Mouse X/Y/Z speed and mouse X/Y/Z position
	Global MD1%,MD2%,MD3%					;; Mouse button down
	Global Camera%							;; Camera used for moving around the terrain
	Global WindowWidth%						;; Window width
	Global WindowHeight%					;; Window height
	Global EndEditor% = 0					;; Close the editor
;#End Region

Include "CLE_GUI.bb"

;#Region TERRAIN SYSTEM
	Type CLETexture
		Field Path$
		Field Flags%
		Field Blend%
		Field SX#
		Field SY#
		Field Texture%
		Field Index
	End Type
	
	Global Terra = CreateStack()				;; Terrain meshes- uses AnimaStack
	Global TexStack = CreateStack()			;; Textures
	Global LayerTex = CreateStack()			;; Layer textures
	Global TerrainRoot = 0					;; The first layer of the terrain mesh
	Dim OptimArray(0)						;; Used in optimization of the terrain mesh on load
	
	;#Region CREATE TERRAIN [ SEGMENTS ]
		Function t_CreateTerrain(Segments=32,Layers=1)
			ClearTerrain()		;; Precaution against creating more than one terrain that isn't optimized
			
			For i = 0 To Layers-1
				om = mesh
				mesh = CreateSegQuad(Segments,0,0)
				PushObject Terra,mesh
				If i > 0
					EntityFX mesh,2+32
					EntityParent mesh,om,0
				Else
					EntityFX mesh,2
				EndIf
				TranslateEntity mesh,0,.00036*i,0
				PushObject LayerTex,CreateStack()
			Next
			
			EntityPickMode GetObject(Terra,0),2
			
			RaiseLayer 0,0,512,2
			
			TerrainRoot = GetObject(Terra,0)
			Return TerrainRoot
		End Function
	;#End Region	

	;#Region HEIGHTMAP
		;#Region APPLY HEIGHT MAP [ MAP [ SCALE ] ]
			Function ApplyHeightmap(Map$,Scale#=1.0)
				If Objects(Terra) = 0 Then Return 0
				Segments = Sqr(CountVertices(GetSurface(GetObject(Terra,i),1)))
				
				tm = LoadImage(Map$)
				
				If tm = 0 Then DebugLog( "ERROR:: Heightmap '"+Map$+"' does not exist" ) Return 0
				
				w = ImageWidth(tm)
				h = ImageHeight(tm)
				
				buffer = ImageBuffer(tm)
				
				LockBuffer(buffer)
				
				For ix# = 0 To Segments-1
					For iz# = 0 To Segments - 1
						x = (ix / segments) * w
						z = (iz / segments) * h
						v = ix + iz * segments
						
						r# = (Float(((ReadPixelFast(x,z,buffer) Shr 16) And 255) - 128) / 255) * Scale
						
						For i = 0 To Objects(Terra)-1
							s = GetSurface(GetObject(Terra,i),1)
							VertexCoords(s,v,VertexX(s,v),r,VertexZ(s,v))
						Next
					Next
				Next
				
				For i = 0 To Objects(Terra)-1
					UpdateNormals GetObject(Terra,i)
				Next
				
				UnlockBuffer(buffer)
				FreeImage tm
				Return 1
			End Function
		;#End Region
		
		;#Region EXPORT HEIGHTMAP [ PATH ]
			Function ExportHeightmap(Path$)
				If Objects(Terra) = 0 Then Return 0
				
				Mesh = GetObject(Terra,0)
				s = GetSurface(Mesh,1)
				
				segments = Sqr(CountVertices(s))
				
				image = CreateImage(segments,segments)
				
				b = ImageBuffer(image)
				LockBuffer(b)
				
				Local minx#=10000,miny#=-10000,minz#
				
				For i = 0 To CountVertices(s)-1
					v# = VertexY(s,i)
					If v < minx Then minx = v
					If v > miny Then miny = v
				Next
				
				minz = miny-minx
				
				For i = 0 To CountVertices(s)-1
					x = i Mod segments
					y = (i - x)/segments
					l# = ((VertexY(s,i)-minx)/minz)*255
					WritePixelFast x,y,255 Shl 24 Or l Shl 16 Or l Shl 8 Or l,b
				Next
				UnlockBuffer(b)
				SaveImage(image,Path$)
				FreeImage image
				Return 1
			End Function
		;#End Region
	;#End Region
		
	;#Region CLEAR TERRAIN
		Function ClearTerrain()
			While Objects(Terra) > 0
				FreeEntity PopObject(Terra)
			Wend
			While Objects(LayerTex) > 0
				FreeStack PopObject(LayerTex)
			Wend
		End Function
	;#End Region
	
	;#Region CLM FORMAT
		
		;#Region CONSTANTS
			;; The CLM (Cower Landscape Mesh) plays off the file structure of Gile[s]'s, the (damn cool) lightmapper, scene files
			
			Const CLM_HEADER = $1000			;;  Header- one string and a float
			Const CLM_AUTHOR = $1001			;;  Author of the file being saved
			
			Const CLM_TEXTURES = $3001		;;  Opening of the texture list
			Const CLM_TEXTURE = $3002		;;  Opening of a texture
			Const CLM_TEXTUREPATH = $3003		;;  Sets the path from which the texture will be loaded from
			Const CLM_TEXTUREFLAGS = $3004	;;  Sets the flags the texture is loaded with
			Const CLM_LOADTEXTURE = $3005		;;  Tells the importer to load the texture at this point because it has sufficient data to do so
			Const CLM_TEXTUREBLEND = $3006	;;  Sets the blend mode of the texture
			Const CLM_TEXTURESCALE = $3007	;;  Sets the scale of the texture
			
			Const CLM_LAYERS = $2000			;;  Opening of the layers, one byte and one int
			Const CLM_LAYER = $2001			;;  Opening of a CLE layer, one byte
			Const CLM_LAYERFORMAT = $2002		;;  1- Alpha
			Const CLM_LAYERDATA = $2003		;;  N Bytes; refer to CLM_MESHFORMAT
			Const CLM_HEIGHTDATA = $2004		;;  The heightmap information- two floats to set delta and minimum y and all bytes to determine height afterwards
			Const CLM_LAYERTEXTURES = $2005	;;  The textures applied to the layer
			Const CLM_HEIGHTFORMAT = $2006	;;  The format in which the heightmap data is stored
		;#End Region
		
		;#Region EXPORT CLM
			Function ExportCLM(Name$)
				If Objects(Terra) = 0 Then Return
				F = WriteFile(Name$+".clm")
				If F = 0 Then DebugLog "Failed to open "+Name$+".clm for writing, check file properties"+Chr(10)+FileType(Name$+".clm")+ " " + FileSize(Name$+".clm") Return 0
				
				Local Paths$[7]
				
				WriteInt F,CLM_HEADER
				WriteInt F,12
				WriteString F,"CLMesh_"
				WriteFloat F,0.6
				
				WriteInt F,CLM_TEXTURES
				WriteInt F,0
				
				For i = 0 To Objects(TexStack)-1
					t.CLETexture = Object.CLETexture(GetObject(TexStack,i))
					WriteInt F,CLM_TEXTURE
					WriteInt F,0
					
					WriteInt F,CLM_TEXTUREPATH
					WriteInt F,Len(t\Path)+1
					WriteString F,t\Path
					
					WriteInt F,CLM_FLAGS
					WriteInt F,2
					WriteShort F,t\Flags
					
					WriteInt F,CLM_TEXTUREBLEND
					WriteInt F,1
					WriteByte F,t\Blend
					
					WriteInt F,CLM_TEXTURESCALE
					WriteInt F,8
					WriteFloat F,t\SX
					WriteFloat F,t\SY
					
					WriteInt F,CLM_LOADTEXTURE
					WriteInt F,0
				Next
				
				WriteInt F,CLM_LAYERS
				WriteInt F,5
				WriteByte F,Objects(Terra)
				WriteInt F,Sqr(CountVertices(GetSurface(GetObject(Terra,0),1)))
				
				For i = 0 To Objects(Terra)-1
					s = GetSurface(GetObject(Terra,i),1)
					Bytes = CountVertices(s)
					
					WriteInt F,CLM_LAYER
					WriteInt F,1
					WriteByte F,i
					
					WriteInt F,CLM_LAYERFORMAT
					WriteInt F,1
					WriteByte F,1
					
					WriteInt F,CLM_LAYERDATA
					WriteInt F,Bytes
					
					For n = 0 To Bytes-1
						WriteByte F,VertexAlpha(s,n)*255
					Next
					
					WriteInt F,CLM_LAYERTEXTURES
					WriteInt F,2+Objects(GetObject(LayerTex,i))*2
					st = GetObject(LayerTex,i)
					WriteShort F,Objects(st)
					For n = 0 To Objects(st)-1
						WriteShort F,GetObject(st,n)
					Next
				Next
				
				minx# = -1000
				miny# = 1000
				
				WriteInt F,CLM_HEIGHTFORMAT
				WriteInt F,1
				WriteByte F,2
				
				WriteInt F,CLM_HEIGHTDATA
				s = GetSurface(GetObject(Terra,0),1)
				WriteInt F,CountVertices(s)*4
;				For n = 0 To CountVertices(s)-1
;					y# = VertexY(s,n)
;					If y < miny Then miny = y
;					If y > minx Then minx = y
;				Next
;				
;				d# = minx - miny
;				
;				WriteFloat F,d
;				WriteFloat F,miny
				
				For n = 0 To CountVertices(s)-1
					WriteFloat F,VertexY(s,n)
				Next
				
				Return 1
			End Function
		;#End Region
		
		;#Region IMPORT CLM
			;; Setting the optimize flag to 1/True will make the loaded mesh unuseable in the editor, but it can drastically reduce the polygon count of terrain
			Function ImportCLM(Name$,Optimize=0)
				Local F = ReadFile(Name$+".clm")
				If F = 0 Then DebugLog "Failed to open "+Name$+".clm for reading, check file properties"+Chr(10)+FileType(Name$+".clm")+ " " + FileSize(Name$+".clm") Return 0
				
				tex = -1
				
				
				ClearTerrain()
				While Not Eof(F)
					Class = ReadInt(F)
					Size = ReadInt(F)
					Position = FilePos(F)
					Select Class
						Case CLM_HEADER
							Header$ = ReadString(F)
							Version# = ReadFloat(F)
							
							Case CLM_AUTHOR
								Author = ReadString(F)
								
						Case CLM_TEXTURES
							
							Case CLM_TEXTURE
								tex = -1
								sx# = 1
								sy# = 1
								blend = 5
							
								Case CLM_TEXTUREPATH
									path$ = ReadString(F)
								
								Case CLM_TEXTUREFLAGS
									flags = ReadShort(F)
								
								Case CLM_TEXTUREBLEND
									blend = ReadByte(F)
									If tex >= 0 Then
										t_TextureBlend tex,blend
									EndIf
								
								Case CLM_TEXTURESCALE
									sx# = ReadFloat(F)
									sy# = ReadFloat(F)
									If tex >= 0 Then
										t_ScaleTexture tex,sx,sy
									EndIf
								
								Case CLM_LOADTEXTURE
									tex = t_LoadTexture(path$,flags,sx,sy,blend)
								
						Case CLM_LAYERS
							Layers = ReadByte(F)
							Segments = ReadInt(F)
							Terrain = t_CreateTerrain(Segments,Layers)
							
							Case CLM_LAYER
								Layer = ReadByte(F)
								
								Case CLM_LAYERFORMAT
									Format = ReadByte(F)
									If (Format And 1) = 1 Then Alpha = 1
									
								Case CLM_LAYERDATA
									Vertices = Segments*Segments-1
									s = GetSurface(GetObject(Terra,Layer),1)
									For i = 0 To Vertices
										a# = Float(ReadByte(F))/255
										VertexColor s,i,VertexRed(s,i),VertexGreen(s,i),VertexBlue(s,i),a
									Next
									UpdateNormals(GetObject(Terra,Layer))
								
								Case CLM_LAYERTEXTURES
									textures = ReadShort(F)
									For i = 1 To textures
										index = ReadShort(F)
										t_ApplyTexture Layer,index
									Next
						
						Case CLM_HEIGHTFORMAT
							hmf = ReadByte(F)
						
						Case CLM_HEIGHTDATA
							If (hmf And 2) = 2 Then
								For i = 0 To CountVertices(GetSurface(GetObject(Terra,0),1))-1
									h# = ReadFloat(F)
									For n = 0 To Objects(Terra)-1
										s = GetSurface(GetObject(Terra,n),1)
										VertexCoords s,i,VertexX(s,i),h,VertexZ(s,i)
									Next
								Next
							Else
								delta# = ReadFloat(F)
								my# = ReadFloat(F)
								For i = 0 To Segments*Segments-1
									h# = Float(ReadByte(F))/255
									For n = 0 To Objects(Terra)-1
										s = GetSurface(GetObject(Terra,n),1)	
										VertexCoords s,i,VertexX(s,i),my+h*delta,VertexZ(s,i)
									Next
								Next
							EndIf
							
							For n = 0 To Objects(Terra)-1
								UpdateNormals(GetObject(Terra,n))
							Next
									
						Default
							SeekFile(F,Position+Size)
					End Select
				Wend
				
				If Optimize Then
					told = Terrain
					Dim OptimArray(CountTriangles(GetSurface(GetObject(Terra,0),1))-1)
					
					root = 0
					
					For i = 0 To Objects(Terra)-1
						s = GetSurface(GetObject(Terra,i),1)
						m2 = M
						M = CreateMesh()
						If m2 <> 0 Then EntityParent m2,M
						ss = CreateSurface(M)
						TranslateEntity M,0,.0015*i,0
						
						If i = 0 Then
							root = M
						Else
							EntityFX M,2+32
						EndIf
						
						For v = 0 To CountVertices(s)-1
							AddVertex ss,VertexX(s,v),VertexY(s,v),VertexZ(s,v),VertexU(s,v),VertexV(s,v)
							VertexNormal ss,v,VertexNX(s,v),VertexNY(s,v),VertexNZ(s,v)
							VertexColor ss,v,VertexRed(s,v),VertexGreen(s,v),VertexBlue(s,v),VertexAlpha(s,v)
						Next
						
						For t = 0 To CountTriangles(s)-1
							If OptimArray(t) = 0 And M <> root Then
								a# = 0
								For n = 0 To 2
									a# = a# + VertexAlpha(s,TriangleVertex(s,t,n))
								Next
								If a = 3 Then OptimArray(t) = 1
								If a > 0 Then AddTriangle ss,TriangleVertex(s,t,0),TriangleVertex(s,t,1),TriangleVertex(s,t,2)
							ElseIf root = M Then
								AddTriangle ss,TriangleVertex(s,t,0),TriangleVertex(s,t,1),TriangleVertex(s,t,2)
							EndIf
						Next
					Next
					
					Dim OptimArray(0)
					
					ClearTerrain()
					Terrain = root
				EndIf
				
				Return Terrain
			End Function
		;#End Region
	;#End Region
	
	;#Region LAYER CONTROL
		;#Region RAISE LAYER [ INDEX [ LAYER [ SIZE ] ] ]
			Function RaiseLayer(Index,Layer,Size%=1,Speed#=.01,Mode=0)
				If Objects(Terra) = 0 Then Return
				If Index < 0 Then Return 0
				Segments = Sqr(CountVertices(GetSurface(GetObject(Terra,0),1)))
				
				x = Index Mod Segments
				Y = (Index-ix)/Segments
				
				ssize# = Sqr(Size*Size+Size*Size)
				
				fD# = Sqr(Size*Size+Size*Size)
				
				For ix = -Floor(fD) To Floor(fD)
					For iy = -Floor(fD) To Floor(fD)
						vx = x + ix
						vy = y + iy
						
						vertex = vx + vy * segments
						vl = vertex - 1
						vr = vertex + 1
						va = vertex - segments
						vb = vertex + segments
						
						If vx > -1 And vx < segments And vy > -1 And vy < segments Then
							
							If Size > 0 Then
								d# = Min(1.0-(Sqr(ix*ix+iy*iy)/fD),0)
							Else
								d# = 1
							EndIf
							
							For i = 1-Max(Min(Mode,0),1) To Objects(Terra)-1
								s = GetSurface(GetObject(Terra,i),1)
								If Mode = 0 Then
									a# = VertexAlpha(s,vertex)
									
									If i <> Layer Then
										a# = Min(a# -Speed*d,0)
									Else
										a# = Max(a# +Speed*d,1)
									EndIf
									
									VertexColor s,vertex,VertexRed(s,vertex),VertexGreen(s,vertex),VertexBlue(s,vertex),a#
								ElseIf Mode = 1
									VertexCoords s,vertex,VertexX(s,vertex),VertexY(s,vertex)+(Speed*d)*.5,VertexZ(s,Vertex)
								ElseIf Mode = 2
									VertexCoords s,vertex,VertexX(s,vertex),VertexY(s,vertex)-(Speed*d)*.5,VertexZ(s,Vertex)
								ElseIf Mode = 3
									div = 1
									zy# = VertexY(s,vertex)
									
									For ly = -4 To 4
										For lx = -4 To 4
											vl = vertex +ly*segments +lx
											If SameRow(vl,vertex+ly*segments,segments) And vl > -1 And vl < CountVertices(s)
												div = div + 1
												zy = zy + VertexY(s,vl)
											EndIf
										Next
									Next
									
									If div > 0 Then
										zy# = zy# / div
										dy# = zy# - VertexY(s,vertex)
										ny# = VertexY(s,vertex) + (dy * Speed);*d		;; I decided not to make this one affected by radial falloff.  you can uncomment the *d if you want it to.
										VertexCoords s,vertex,VertexX(s,vertex),ny#,VertexZ(s,Vertex)
									EndIf
								EndIf
							Next
						EndIf
					Next
				Next
				
				If Mode > 0 Then
					For i = 0 To Objects(Terra)-1
						UpdateNormals(GetObject(Terra,i))
					Next
				EndIf
			End Function
		;#End Region
		
		;#Region APPLY TEXTURE [ LAYER [ TEXTURE ] ] ]
			Function t_ApplyTexture(Layer, Texture)
				If Objects(Terra) = 0 Then Return
				If Layer < 0 Or Layer >= Objects(Terra) Then Return 0
				Index = Objects(GetObject(LayerTex,Layer))
				i.CLETexture = Object.CLETexture(GetObject(TexStack,Texture))
				If i = Null Then Return 0
				EntityTexture GetObject(Terra,Layer),i\Texture,0,Index
				PushObject GetObject(LayerTex,Layer),i\Index
			End Function
		;#End Region
		
		;#Region GENERATE
			Function GenerateTerrain(Rock=1, Sand=2,Normal#=.35)
				If Objects(Terra) = 0 Then Return
				rs = GetSurface(GetObject(Terra,Rock),1)
				For i = 0 To CountVertices(rs)-1
					y# = Abs(VertexNY(rs,i))
					If y =<  Normal Then
						a# = y / (Normal/2)
						VertexColor rs,i,VertexRed(rs,i),VertexGreen(rs,i),VertexBlue(rs,i),a
					Else
						VertexColor rs,i,VertexRed(rs,i),VertexGreen(rs,i),VertexBlue(rs,i),0
					EndIf
				Next
				
				If Sand < 0 Then Return
				
				ss = GetSurface(GetObject(Terra,Sand),1)
				segments = Sqr(CountVertices(ss))
				For i = 0 To CountVertices(ss)-1
					l = 0
					a = i
					d = i
					k=0
					For n = 1 To 6
						a = i- Segments*n
						d = i+ Segments*n
						
						If a > -1 Then
							If VertexAlpha(rs,a) = 0 Then l = l + 1
						Else
							k = k + 1
						EndIf
						
						If d < CountVertices(ss)
							If VertexAlpha(rs,d) = 0 Then l = l + 1
						Else
							k = k + 1
						EndIf
					Next
					
					If VertexAlpha(rs,i) = 0 Then l = l + 1
					
					a = i
					d = i
					
					If l = 13-k Then
					
	;					VertexColor ss,i,VertexRed(ss,i),VertexGreen(ss,i),VertexBlue(ss,i),VertexAlpha(ss,i)+.5
					
						For n = 1 To 6
						
							a = i- Segments*n
							d = i+ Segments*n
							
							If a > -1 Then
								VertexColor ss,a,VertexRed(ss,a),VertexGreen(ss,a),VertexBlue(ss,a),VertexAlpha(ss,a)+(Float(6-n)/6)*.05
							EndIf
							
							If d < CountVertices(ss)
								VertexColor ss,d,VertexRed(ss,d),VertexGreen(ss,d),VertexBlue(ss,d),VertexAlpha(ss,d)+(Float(6-n)/6)*.05
							EndIf
						
						Next
					EndIf
				Next
			End Function
		;#End Region
		
		;#Region ADD LAYER
			;; Adds another layer on top of all the others
			Function AddLayer()
				If Objects(Terra) = 0 Then Return
				b = CreateBrush()
				root = GetObject(Terra,0)
				m = CopyMesh(root)
				
				TranslateEntity m,0,.00036,0
				
				PaintEntity m,b
				FreeBrush b
				EntityFX m,2+32
				s = GetSurface(m,1)
				For i = 0 To CountVertices(s)-1
					VertexColor s,i,255,255,255,0
				Next
				EntityParent m,GetObject(Terra,Objects(Terra)-1),0
				PushObject Terra,m
				PushObject LayerTex,CreateStack()
			End Function
		;#End Region

		;#Region REMOVE LAYER
			;; Removes the top-most layer from the terrain, you have to be careful with this 'cause you may accidentally lose work if calling it at the wrong time
			Function RemoveLayer()
				If Objects(Terra) = 0 Then Return
				Layer = Objects(Terra)-1
				If Layer = 0 Then Return 0
				FreeEntity PopObject(Terra)
				For i = Layer To Objects(Terra)-1
					TranslateEntity GetObject(Terra,i),0,-.0015,0
				Next
				FreeStack PopObject(LayerTex)
				Return 1
			End Function
		;#End Region
		
		;#Region RESET LAYER
			Function ResetLayer(Layer)
				If Layer < 0 Or Layer > Objects(Terra)-1 Then Return 0
				b = CreateBrush()
				If Layer > 0 Then
					BrushFX b,2+32
				Else
					BrushFX b,2
				EndIf
				PaintEntity GetObject(Terra,Layer),b
				FreeBrush b
			End Function
		;#End Region
		
		;#Region TEXTURE
			Function t_LoadTexture(Path$,Flags%,SX#=1,SY#=1,Blend=5)
				For i.CLETexture = Each CLETexture
					If Lower(i\Path$) = Lower(Path$) Then Return i\Index
				Next
				i.CLETexture = New CLETexture
				i\Texture = LoadTexture(Path$,Flags%)
				tex = i\Texture
				i\Path = Path
				i\Flags = Flags
				ScaleTexture tex,SX,SY
				i\SX = SX
				i\SY = SY
				i\Blend = Blend
				TextureBlend tex,Blend
				i\Index = Objects(TexStack)
				PushObject TexStack,Handle(i)
				Return i\Index
			End Function
			
			Function t_TextureBlend(ind,blend)
				i.CLETexture = Object.CLETexture(GetObject(TexStack,ind))
				i\Blend = blend
				TextureBlend i\Texture,blend
			End Function
			
			Function t_ScaleTexture(ind,sx#,sy#)
				i.CLETexture = Object.CLETexture(GetObject(TexStack,ind))
				i\sx = sx : i\sy = sy
				ScaleTexture i\texture,sx,sy
			End Function
		;#End Region
	;#End Region
;#End Region

CLEInit()

;#Region SKY
;	Sky = MakeSkyBox("sky")
;#End Region

;#Region LIGHTING
	AmbientLight 128,128,128
	l = CreateLight(1)
	PositionEntity(l,25,75,25)
	LightRange(l,150)
	LightColor l,150,150,150
;#End Region

Repeat
	CLEUpdateGUI3D()

	;#Region CAMERA
		rts# = rts * .85
		pts# = pts * .85
		sts# = sts * .75
		fts# = fts * .75
		If MouseDown(2) And (Not CLEMouseInGUI()) Then
			MoveMouse WindowWidth/2,WindowHeight/2
			pts = pts + MYS*.25
			rts = rts - MXS*.25
			TurnEntity Camera,pts,rts,0
			RotateEntity Camera,EntityPitch(Camera),EntityYaw(Camera),0
			sts = sts + (KeyDown(32)-KeyDown(30))*.03
			fts = fts + (KeyDown(17)-KeyDown(31))*.03
			MoveEntity Camera,sts,0,fts
		EndIf
	;#End Region
	
	;#Region SKY
;		PositionEntity Sky,EntityX(Camera,1),EntityY(Camera,1),EntityZ(Camera,1)
	;#End Region
	
	;#Region TERRAIN
		If Objects(Terra) > 0 Then
			If Not CLEMouseInGUI()
				pick = CameraPick( Camera,MX,MY )
				If pick = GetObject(Terra,0) And MouseDown(1) Then RaiseLayer(PickedVertex(),BrushLayer,BrushSize,BrushSpeed,PaintMode)
			EndIf
		EndIf
	;#End Region
		
	;#Region SYSTEM
		UpdateWorld
		RenderWorld
		
		CLEUpdateGUI2D()
		
		Flip False
	;#End Region
Until EndEditor = 1
ClearWorld()
EndGraphics()
End()

;#Region CREATE SEG QUAD [ SEGMENTS [ SEGMENTED [ CENTERED ] ] ]
	Function CreateSegQuad(Segments=16,Segmented=0,Centered=0)
		sx = 0
		sz = 0
		st = Segments-1
		
		m = CreateMesh()
		s = CreateSurface(m)
		
		If Segmented = 0 Then
			For x# = sx To st
				For z# = sz To st
					vertex = CountVertices(s)
					AddVertex s, x, 0, z, x / Segments, z / Segments
;					VertexNormal s,vertex,0,1,0
				Next
			Next
			
			For ix = 0 To st-1
				For iz = 0 To st-1
					v1 = (iz) * Segments + (ix)
					v2 = (iz) * Segments + (ix+1)
					v3 = (iz+1) * Segments + (ix+1)
					v4 = (iz+1) * Segments + (ix)
					AddTriangle s,v1,v2,v3
					AddTriangle s,v3,v4,v1
				Next
			Next
		Else
			For x# = sx To st
				For z# = sz To st
					vertex = CountVertices(s)
					AddVertex s,x,0,z,0,0
					AddVertex s,x+1,0,z,1,0
					AddVertex s,x+1,0,z+1,1,1
					AddVertex s,x,0,z+1,0,1
					AddTriangle s,vertex,vertex+1,vertex+2
					AddTriangle s,vertex+2,vertex+3,vertex
					
					For i = 0 To 3
;						VertexNormal s,vertex+i,0,1,0
					Next
				Next
			Next
		EndIf
		
		ScaleMesh m,1.0/Segments,1,1.0/Segments
		
		Return m
	End Function
;#End Region

;#Region MATH
	Function Min#(x#,y#)   ; Returns x if x > y, else y
		If x < y Then Return y
		Return x
	End Function

	Function Max#(x#,y#)   ; Returns x if x < y, else y
		If x > y Then Return y
		Return x
	End Function
	
	Function SameRow(x,z,width)
		y = x
		x = x Mod width
		y = y - x
		z = (z - (z Mod width))/width
		If y/width <> z Then Return 0
		Return 1
	End Function
;#End Region

;#Region MESH
	Function PickedVertex()
		s = PickedSurface()
		If s = 0 Then Return -1

		x# = PickedX()
		y# = PickedY()
		z# = PickedZ()
		
		piv = CreatePivot()
		PositionEntity piv,x,y,z
		
		d# = -1
		
		piv2 = CreatePivot()
		
		t = PickedTriangle()
		
		Local d2#[2]
		Local v2%[2]
		
		For i = 0 To 2
			v = TriangleVertex(s,t,i)
			TFormPoint VertexX(s,v),VertexY(s,v),VertexZ(s,v),PickedEntity(),0
			PositionEntity piv2,TFormedX(),TFormedY(),TFormedZ()
			
			d2[i] = EntityDistance(piv,piv2)
			v2[i] = v
		Next
		
		FreeEntity piv2
		FreeEntity piv
		
		If d2[0] < d2[1] And d2[0] < d2[2] Then
			Return v2[0]
		ElseIf d2[1] < d2[0] And d2[1] < d2[2] Then
			Return v2[1]
		Else
			Return v2[0]
		EndIf
	End Function
;#End Region

;#Region COLOR
	Const RALPHA = 24	;Return Alpha when using RColor
	Const RRED = 16	;Return Red when using RColor
	Const RGREEN = 8	;Return Green when using RColor
	Const RBLUE = 0	;Return Blue when using RColor

	Function IntColor(R,G,B,A=255)
		Return A Shl 24 Or R Shl 16 Or G Shl 8 Or B Shl 0
	End Function
	
	Function RColor%(c%,d%)
		Return c Shr d And 255 Shl 0
	End Function
;#End Region

;#Region SKY [ PATH ]
	;; AGore's nice sky box code
	;; Taken from Samples/AGore/BirdDemo
	;; I'm not sure what license this is under, so it is not under the license the rest of this source is [NOTE: this is probably pub. domain now]
	Function MakeSkyBox( file$ )
	
		m=CreateMesh()
		;front face
		b=LoadBrush( file$+"_FR.bmp",49 )
		s=CreateSurface( m,b )
		AddVertex s,-1,+1,-1,0,0:AddVertex s,+1,+1,-1,1,0
		AddVertex s,+1.5,-1,-1.5,1,1:AddVertex s,-1.5,-1,-1.5,0,1
		AddTriangle s,0,1,2:AddTriangle s,0,2,3
		FreeBrush b
		;right face
		b=LoadBrush( file$+"_LF.bmp",49 )
		s=CreateSurface( m,b )
		AddVertex s,+1,+1,-1,0,0:AddVertex s,+1,+1,+1,1,0
		AddVertex s,+1.5,-1,+1.5,1,1:AddVertex s,+1.5,-1,-1.5,0,1
		AddTriangle s,0,1,2:AddTriangle s,0,2,3
		FreeBrush b
		;back face
		b=LoadBrush( file$+"_BK.bmp",49 )
		s=CreateSurface( m,b )
		AddVertex s,+1,+1,+1,0,0:AddVertex s,-1,+1,+1,1,0
		AddVertex s,-1.5,-1,+1.5,1,1:AddVertex s,+1.5,-1,+1.5,0,1
		AddTriangle s,0,1,2:AddTriangle s,0,2,3
		FreeBrush b
		;left face
		b=LoadBrush( file$+"_RT.bmp",49 )
		s=CreateSurface( m,b )
		AddVertex s,-1,+1,+1,0,0:AddVertex s,-1,+1,-1,1,0
		AddVertex s,-1.5,-1,-1.5,1,1:AddVertex s,-1.5,-1,+1.5,0,1
		AddTriangle s,0,1,2:AddTriangle s,0,2,3
		FreeBrush b
		;top face
		b=LoadBrush( file$+"_UP.bmp",49 )
		s=CreateSurface( m,b )
		AddVertex s,-1,+1,+1,0,1:AddVertex s,+1,+1,+1,0,0
		AddVertex s,+1,+1,-1,1,0:AddVertex s,-1,+1,-1,1,1
		AddTriangle s,0,1,2:AddTriangle s,0,2,3
		FreeBrush b
		;bottom face	- this bit was added by me.  'cause i wanted a bottom face.  WHAT!?  YOU DON'T SEE IT BUT I NEED IT THERE ANYWAY!
		b=LoadBrush( file$+"_BT.bmp",49 )
		s=CreateSurface( m,b )
		AddVertex s,-1.5,-1,+1.5,0,1:AddVertex s,+1.5,-1,+1.5,0,0
		AddVertex s,+1.5,-1,-1.5,1,0:AddVertex s,-1.5,-1,-1.5,1,1
		AddTriangle s,0,1,2:AddTriangle s,0,2,3
		FreeBrush b
	
		ScaleEntity m,20,20,20
		FlipMesh m
		EntityFX m,1+16+8
		EntityOrder m,100
		Return m
		
	End Function
;#End Region

;#Region TEXTS [ X [ Y [ T$ [ CX [ CY ] ] ] ] ]
	Function Texts(X,Y,T$,CX=0,CY=0)
		Color 0,0,0
		Text X-1,Y-1,T$,CX,CY
		Text X+1,Y-1,T$,CX,CY
		Text X+1,Y+1,T$,CX,CY
		Text X-1,Y+1,T$,CX,CY
		Text X,Y-1,T$,CX,CY
		Text X,Y+1,T$,CX,CY
		Text X-1,Y,T$,CX,CY
		Text X+1,Y,T$,CX,CY
		Color 255,255,255
		Text X,Y,T$,CX,CY
	End Function
;#End Region


;#Region PUSH/POP DATA
	Global PoppedValue$
	Global PoppedClass%
	
	Type Stack
		Field F.StackObj
		Field L.StackObj
		Field Objects
	End Type
	
	Type StackObj
		Field Parent.Stack
		Field N.StackObj
		Field P.StackObj
		Field Value$
		Field Class%
	End Type
	
	Function CreateStack()
		Local s.Stack
		s.Stack = New Stack
		Return Handle(s)
	End Function
	
	Function PushObject(Stack,Value$,Class%=0,ToFront=0)
		Local s.Stack,i.StackObj
		s.Stack = Object.Stack(Stack)
		s\Objects = s\Objects + 1
		i.StackObj = New StackObj
		i\Value = Value
		i\Class = Class
		i\Parent = s
		If ToFront = 0 Then
			If s\F = Null And s\L = Null Then
				s\F = i
				s\L = s\F
			Else
				i\P = s\L
				s\L\N = i
				s\L = i
			EndIf
		Else
			If s\F = Null And s\L = Null Then
				s\F = i
				s\L = s\F
			Else
				i\N = s\F
				s\F\P = i
				s\F = i
			EndIf
		EndIf
	End Function
	
	Function PopObject$(Stack,FromFront=0,RemoveData=1)
		Local s.Stack,i.StackObj,v$
		s.Stack = Object.Stack(Stack)
		If RemoveData > 0 Then s\Objects = s\Objects - 1
		If FromFront = 0 Then
			i.StackObj = s\L
			If RemoveData > 0 Then
				s\L = i\P
				If S\L <> Null Then s\L\N = Null
			EndIf
		Else
			i.StackObj = s\F
			If RemoveData > 0 Then
				s\F = i\N
				If S\F <> Null Then s\F\P = Null
			EndIf
		EndIf

		If s\L = Null Then s\L = s\F
		If s\F = Null Then s\F = s\L
		
		PoppedClass = i\Class
		PoppedValue = i\Value
		v$ = i\Value
		If RemoveData > 0 Then Delete i
		Return V
	End Function
	
	Function Objects(Stack)
		Local s.Stack
		s.Stack = Object.Stack(Stack)
		Return s\Objects
	End Function
	
	Function GetObject$(Stack,Index,RemoveData = 0,Reverse=0)
		Local s.Stack,i.StackObj,n
		s.Stack = Object.Stack(Stack)
		i.StackObj = s\F
		
		If Reverse Then Index = (s\Objects-1)-Index
		
		Repeat
			If i = Null Then
				Return False
			ElseIf n = Index Then
				PoppedValue  = i\Value
				PoppedClass = i\Class
				
				If RemoveData Then
					If i\P <> Null Then i\P\N = i\N
					If i\N <> Null Then i\N\P = i\P
					s\Objects = s\Objects - 1
					Delete i
				EndIf
				
				Return PoppedValue
			EndIf
			
			i = i\N
			n = n + 1
		Forever
	End Function
	
	Function InsertObject(Stack,Index,Value$="",Class%=0)
		Local s.Stack,i.StackObj,o.StackObj,ne.StackObj,pr.StackObj,n
		s.Stack = Object.Stack(Stack)
		
		i.StackObj = s\F
		o.StackObj = New StackObj
		o\Value$ = Value
		o\Class% = Class
		o\Parent = s
		
		Repeat
			If i\N = Null Or n = Index Then
				ne.StackObj = i\N
				pr.StackObj = i\P
				
				If ne <> Null Then ne\P =  o
				If pr <> Null Then pr\N = o

				If ne = Null Then s\L = o
				If pr = Null Then s\F = o
				
				o\N = ne
				o\P = pr
				
				Return True
			EndIf
			
			i = i\N
			n = n + 1
		Forever
	End Function
	
	Function FreeStack(Stack)
		Local s.Stack,i.StackObj
		s.Stack = Object.Stack(Stack)
		
		Delete s
		For i.StackObj = Each StackObj
			If i\Parent = Null Then Delete i
		Next
	End Function
;#End Region


;;;;;;;;;;;;;; THIS IS WHERE THE GUI BEGINS ;;;;;;;;;;;;;;;;;;;
; #gui-key

; If you intend to replace the GUI, be aware of the functions
; marked as required.  You will have to implement those, either
; the 3D set or the 2D set.


;; This is the old GUI, probably doesn't work with any version of F-UI that does
;; not include my vast array of changes (now lost to time)
Include "FUI/F-UI.bb"

Type CLEGUI
	Field Window
	Field View
	Field BrushParmsSize
	Field BrushParmsSpeed
	Field BrushModeGroup
	Field BrushParmsModePaint
	Field BrushParmsModeRaise
	Field BrushParmsModeLower
	Field BrushParmsModeSmooth
	Field LayerList
	Field ControlTab
	Field BrushPage
	Field LayerStack
	
	Field mnCLMNew
	Field mnCLMSave
	Field mnCLMLoad
	
	Field mnCreateWindow
	Field mnCreateSegments
	
	Field mnApplyHMap
	Field mnExportHMap
	
	Field mnQuit
	
	Field cmLayer
	Field cmLayerAdd
	Field cmLayerRmv
	Field cmLayerReset
	
	Field TexPage
	Field TexList
	Field BtnApplyTexture
	Field ScaleSpinner
	Field BlendGroup
	Field BlendRadioAlp
	Field BlendRadioMul
	Field BlendRadioAdd
	Field BlendRadioMod
	
	Field cmTexture
	Field cmTextureLoad
	Field tlStack
	Field selTex
	
	Field nterraWin
	Field spnSegments
	Field lblAmnt
	Field btnOK
	Field btnCancel
	
	Field hmpCancel
	Field hmpOK
	Field hmpWin
	Field hmpSize
	Field pathText
	Field opnPath
End Type

Type TextureBlock
	Field Node
	Field Index
	Field Path$
	Field Name$
End Type

Global GUI.CLEGUI

;; This function is REQUIRED
;; Use this function to intialize graphics mode, create the camera, set texture filters, etc.
Function CLEInit()
	FUI_LoadResolution("CLE.cfg")
	WINDOW_RESIZE_METHOD = 0
	FUI_Initialise(800,600,32,2,0,1,"CLE",".72b")		;; That bit about '.72b' is just bullshit, ignore it
	GUI = New CLEGUI
	GUI\Window = FUI_Window(0,0,192+12,GraphicsHeight(),"Control Panel",0,1+2,2)
;	GUI\Window = FUI_Window(0,0,GraphicsWidth(),GraphicsHeight(),"Control Panel",0,1+2,2)
;	v.View = Object.View(GUI\View)
	GUI\tlStack = CreateStack()
		FUI_LockWindow(GUI\Window)
		GUI\ControlTab = FUI_Tab(GUI\Window,6,128,192,GraphicsHeight()-128-52)
		GUI\BrushPage = FUI_TabPage(GUI\ControlTab,"Brush")
		GUI\TexPage = FUI_TabPage(GUI\ControlTab,"Textures")
		GUI\LayerList = FUI_ListBox(GUI\Window,6,6,192,128-12,0,1)
			
			FUI_Label(GUI\BrushPage,6,6,"Brush Size")
			GUI\BrushParmsSize = FUI_Slider(GUI\BrushPage,6,6+16*1+32*0,192-24,16,0,25,BrushSize,12,DIR_HORIZONTAL)
			
			FUI_Label(GUI\BrushPage,6,6+16*1+32*1,"Brush Speed")
			GUI\BrushParmsSpeed = FUI_Slider(GUI\BrushPage,6,6+16*2+32*1,192-24,16,0.01,.5,BrushSpeed,12,DIR_HORIZONTAL)

			GUI\BrushModeGroup = FUI_GroupBox(GUI\BrushPage,6,116,192-36,12+78+8,"Brush Mode")
				GUI\BrushParmsModePaint = FUI_Radio(GUI\BrushModeGroup,6,6,"Paint",1,0)
				GUI\BrushParmsModeRaise = FUI_Radio(GUI\BrushModeGroup,6,6+18,"Raise",0,0)
				GUI\BrushParmsModeLower = FUI_Radio(GUI\BrushModeGroup,6,6+36,"Lower",0,0)
				GUI\BrushParmsModeSmooth = FUI_Radio(GUI\BrushModeGroup,6,6+36+18,"Smooth",0,0)
				
			GUI\TexList = FUI_ListBox(GUI\TexPage,6,6,192-12,128-12,0,1)
			GUI\BtnApplyTexture = FUI_Button(GUI\TexPage,6,128,192-12,24,"Apply Texture")
			FUI_Label(GUI\TexPage,6,128+24+10,"Scale")
			GUI\ScaleSpinner = FUI_Spinner(GUI\TexPage, (192-12)/2-48, 128+24+6, 48+(192-12)/2, 24, 0, 20, 1.0, 0.01, DTYPE_FLOAT)
			GUI\BlendGroup = FUI_GroupBox(GUI\TexPage,6,128+48+12,192-12,22*4+4,"Blend Mode")
				GUI\BlendRadioAlp = FUI_Radio(GUI\BlendGroup,2,1+19*0,"Alpha",1,1)
				GUI\BlendRadioMul = FUI_Radio(GUI\BlendGroup,2,1+19*1,"Multiply",0,1)
				GUI\BlendRadioAdd = FUI_Radio(GUI\BlendGroup,2,1+19*2,"Add",0,1)
				GUI\BlendRadioMod = FUI_Radio(GUI\BlendGroup,2,1+19*3,"Modulate 2X",0,1)
				
	file=FUI_MenuTitle(GUI\Window,"File")
		GUI\mnCLMNew = FUI_MenuItem(file,"New")
		GUI\mnCLMLoad = FUI_MenuItem(file,"Load")
		GUI\mnCLMSave = FUI_MenuItem(file,"Save")
		FUI_MenuBar(file)
		GUI\mnApplyHMap = FUI_MenuItem(file,"Load Heightmap")
		GUI\mnExportHMap = FUI_MenuItem(file,"Save Heightmap")
		FUI_MenuBar(file)
		GUI\mnQuit = FUI_MenuItem(file,"Quit")

	GUI\cmLayer = FUI_ContextMenu()
	GUI\cmLayerAdd = FUI_ContextMenuItem(GUI\cmLayer,"Add Layer")
	GUI\cmLayerRmv = FUI_ContextMenuItem(GUI\cmLayer,"Remove Layer") 
	FUI_ContextMenuBar(GUI\cmLayer)
	GUI\cmLayerReset = FUI_ContextMenuItem(GUI\cmLayer,"Reset Textures")
	
	GUI\cmTexture = FUI_ContextMenu()
	GUI\cmTextureLoad = FUI_ContextMenuItem(GUI\cmTexture,"Load Texture")
	
	GUI\nterraWin = FUI_Window( app\W/2-101, app\H/2-50, 202, 100, "New Terrain", "", 1 )
	GUI\spnSegments = FUI_Spinner( GUI\nterraWin, 120, 8, 70, 20, 0.0, 128.0, 64.0, 1.0, DTYPE_INTEGER, "" )
	GUI\lblAmnt = FUI_Label( GUI\nterraWin, 14, 12, "Amount of Segments" )
	GUI\btnOK = FUI_Button( GUI\nterraWin, 26, 42, 70, 20, "OK" )
	GUI\btnCancel = FUI_Button( GUI\nterraWin, 110, 42, 70, 20, "Cancel" )
	FUI_HideGadget(GUI\nterraWin)
	
	hmpWin = FUI_Window( app\W/2-267/2, app\H/2-55, 267, 128, "Load Heightmap", "", 1 )
	FUI_Label( hmpWin, 10, 42, "Scale" )
	hmpSize = FUI_Spinner( hmpWin, 46, 40, 92, 20, -100.0, 100.0, 1.0, .01, DTYPE_FLOAT, "" )
	FUI_Label( hmpWin, 10, 16, "Path" )
	pathText = FUI_TextBox( hmpWin, 46, 12, 200, 22, 0 )
	opnPath = FUI_Button( hmpWin, 245, 12, 16, 22, "..." )
	hmpOK = FUI_Button( hmpWin, 30, 74, 70, 20, "OK" )
	hmpCancel = FUI_Button( hmpWin, 112, 74, 70, 20, "Cancel" )
	FUI_ModalWindow(hmpWin,0)
	
	GUI\hmpWin = hmpWin
	GUI\pathText = pathText
	GUI\opnPath = opnPath
	GUI\hmpSize = hmpSize
	GUI\hmpOK = hmpOK
	GUI\hmpCancel = hmpCancel
	
	FUI_HideGadget(GUI\hmpWin)
	
	Camera = app\Cam
	CameraRange Camera,.01,5000
	CameraFogMode Camera,1
	CameraFogColor Camera,220,250,252
	CameraFogRange Camera,35,150
	CameraClsColor Camera,192,200,240
	
	PositionEntity Camera,0,50,0

	GUI\LayerStack = CreateStack()

	TextureFilter "",1+8
	
	FUI_SetGadgetAlpha(GUI\Window,.8,1)
	FUI_SetGadgetAlpha(GUI\nterraWin,.8,1)
	FUI_SetGadgetAlpha(GUI\hmpWin,.8,1)
	
	WindowWidth = app\W
	WindowHeight = app\H
	GUI\selTex = -1
End Function

;; This function is REQUIRED
;; If you are not using a 2D GUI then leave its contents blank, but you MUST have it defined
Function CLEUpdateGUI2D()
End Function

;; This function is REQUIRED
;; If you are not using a 3D GUI then leave its contents blank, but you MUST have it defined
Function CLEUpdateGUI3D()
	
	If Objects(Terra) > 0 Then
		FUI_EnableGadget(GUI\cmLayerRmv)
		FUI_EnableGadget(GUI\cmLayerAdd)
		FUI_EnableGadget(GUI\cmLayerReset)
	Else
		FUI_DisableGadget(GUI\cmLayerRmv)
		FUI_DisableGadget(GUI\cmLayerAdd)
		FUI_DisableGadget(GUI\cmLayerReset)
	EndIf
	
	If FUI_OverGadget(GUI\LayerList) And app\MB2 Then
		FUI_OpenContextMenu(GUI\cmLayer)
	ElseIf FUI_OverGadget(GUI\TexList) And app\MB2 Then
		FUI_OpenContextMenu(GUI\cmTexture)
	EndIf
	
;	CameraClsMode Camera,1,1
	
	FUI_Update()
	
;	CameraClsMode Camera,0,0

	If Terra <> 0 Then
		While Objects(GUI\LayerStack) <> Objects(Terra)
			If Objects(Terra) > Objects(GUI\LayerStack) Then
				PushObject GUI\LayerStack,FUI_ListBoxItem(GUI\LayerList,"Layer "+(Objects(GUI\LayerStack)+1))
			Else
				FUI_DeleteGadget(PopObject(GUI\LayerStack))
			EndIf
		Wend
	EndIf
	
	MX = app\MX
	MY = app\MY
	MZ = app\MZ
	MXS = app\MXS
	MYS = app\MYS
	MZS = app\MZS
	MD1 = app\MB1
	MD2 = app\MB2
	MD3 = app\MB3
	app\currentFile = "burnInHellMemoryAccessViolation"
	
	If GUI\selTex = -1 Then
		FUI_DisableGadget(GUI\BtnApplyTexture)
;		FUI_DisableGadget(GUI\BlendGroup)
		FUI_DisableGadget(GUI\ScaleSpinner)
		FUI_DisableGadget(GUI\BlendRadioAlp)
		FUI_DisableGadget(GUI\BlendRadioMul)
		FUI_DisableGadget(GUI\BlendRadioAdd)
		FUI_DisableGadget(GUI\BlendRadioMod)
	Else
		FUI_EnableGadget(GUI\BtnApplyTexture)
;		FUI_EnableGadget(GUI\BlendGroup)
		FUI_EnableGadget(GUI\ScaleSpinner)
		FUI_EnableGadget(GUI\BlendRadioAlp)
		FUI_EnableGadget(GUI\BlendRadioMul)
		FUI_EnableGadget(GUI\BlendRadioAdd)
		FUI_EnableGadget(GUI\BlendRadioMod)
	EndIf
	
	For i = 0 To Objects(TexStack)-1
		d.CLETexture = Object.CLETexture(GetObject(TexStack,i))
		Found = 0
		
		p$ = d\Path
		For n = 1 To Len(p)
			If Left(Right(p,n),1) = "\" Or Left(Right(p,n),1) = "/" Then
				p$ = Trim(Right(p,n-1))
				Exit
			EndIf
		Next
		
		For t.TextureBlock = Each TextureBlock
			If Lower(p) = Lower(t\Name) Then
				Found = 1
				Exit
			EndIf
		Next
		
		If Found = 0 Then
			image = LoadImage(d\Path)
			If image = 0 Then
				ResizeImage image,16,16
				t.TextureBlock = New TextureBlock
				t\Node = FUI_ListBoxItem(GUI\TexList,p$,image)
				t\Name = p
				t\Path = d\Path
				FreeImage Image
				PushObject GUI\tlStack,Handle(t)
			EndIf
		EndIf
	Next
			
	
;	f.ListBox = Object.ListBox(GUI\TexList)
;	
;	For i = 0 To Objects(GUI\tlStack)-1
;		t.TextureBlock = Object.TextureBlock(GetObject(GUI\tlStack,i))
;		If f\overItem = Null
;			FUI_HideGadget t\ImageBox
;		Else
;			If Handle(f\overItem) = t\Node
;				FUI_ShowGadget t\ImageBox
;			Else
;				FUI_HideGadget t\ImageBox
;			EndIf
;		EndIf
;	Next

	For e.Event = Each Event
		Select e\EventID
			Case GUI\cmLayerRmv
				RemoveLayer()

			Case GUI\cmLayerAdd
				AddLayer()
			
			Case GUI\cmLayerReset
				ResetLayer(BrushLayer)
			
			Case GUI\BrushParmsSize
				BrushSize = e\EventData
				
			Case GUI\BrushParmsSpeed
				BrushSpeed = e\EventData
				
			Case GUI\BrushParmsModePaint
				PaintMode = 0
				
			Case GUI\BrushParmsModeRaise
				PaintMode = 1
				
			Case GUI\BrushParmsModeLower
				PaintMode = 2
			
			Case GUI\BrushParmsModeSmooth
				PaintMode = 3

			Case GUI\ScaleSpinner
				t_ScaleTexture GUI\selTex,e\EventData,e\EventData
			
			Case GUI\BlendRadioAlp
				t_TextureBlend GUI\selTex,1
			
			Case GUI\BlendRadioMul
				t_TextureBlend GUI\selTex,2
			
			Case GUI\BlendRadioAdd
				t_TextureBlend GUI\selTex,3
			
			Case GUI\BlendRadioMod
				t_TextureBlend GUI\selTex,5

			Case GUI\BtnApplyTexture
				t_ApplyTexture BrushLayer,GUI\selTex
				
			Case GUI\mnCLMSave
				FUI_SaveDialog("Save landscape..","","Cower Landscape Mesh (*.clm)|*.clm|All files (*.*)|*.*")
				path$ = Replace( Lower(app\currentFile),".clm","" )
				ExportCLM(path$)
				
			Case GUI\mnCLMLoad
				FUI_OpenDialog("Load landscape..","","Cower Landscape Mesh (*.clm)|*.clm|All files (*.*)|*.*")
				path$ = app\currentFile
				While Objects(GUI\LayerStack)
					FUI_DeleteGadget(PopObject(GUI\LayerStack))
				Wend
				If FileType(path$) = 1 Then
					path$ = Replace( Lower(app\currentFile),".clm","" )
					ter = ImportCLM(path$)
					ScaleEntity ter,50,50,50
				EndIf
				
			Case GUI\mnCLMNew
				While Objects(GUI\LayerStack)
					FUI_DeleteGadget(PopObject(GUI\LayerStack))
				Wend
				FUI_ModalWindow(GUI\Window,0)
				FUI_ShowGadget(GUI\nterraWin)
				FUI_ModalWindow(GUI\nterraWin,1)
				Delete e
				Repeat
					FUI_Update()
					
					For k.Event = Each Event
						Select k\EventID
							Case GUI\btnOK
								Segments = FUI_SendMessageI(GUI\spnSegments,M_GETVALUE)
								ter = t_CreateTerrain(Segments,1)
								If ter <> 0 Then ScaleEntity ter,50,50,50
								quitLoop = 1
							Case GUI\btnCancel
								quitLoop = 1
						End Select
						Delete k
					Next

					RenderWorld
					Flip 0
				Until quitLoop = 1
				FUI_HideGadget(GUI\nterraWin)
				FUI_ModalWindow(GUI\nterraWin,0)
				FUI_ModalWindow(GUI\Window,1)
				
			Case GUI\mnApplyHMap
				Delete e
				FUI_ModalWindow(GUI\Window,0)
				FUI_SendMessage(GUI\pathText,M_SETTEXT,"")
				FUI_SendMessage(GUI\hmpSize,M_SETVALUE,1.0)
				FUI_ShowGadget(GUI\hmpWin)
				FUI_ModalWindow(GUI\hmpWin,1)
				size# = 1.0
				path$ = ""
				Repeat
					FUI_Update()
					
					For k.Event = Each Event
						Select k\EventID
							Case GUI\hmpSize
								size# = k\EventData
							Case GUI\hmpOK
								If FileType(path$) = 1 Then
									ApplyHeightmap(path$,size#)
								EndIf
								quitLoop = 1
							Case GUI\hmpCancel
								quitLoop = 1
							Case GUI\opnPath
								FUI_OpenDialog("Load heightmap..","","PNG (*.png)|*.png|Bitmap (*.bmp)|*.bmp|Targa (*.tga)|*.tga|JPG (*.jpg)|*.jpg|All files (*.*)|*.*")
								path$ = app\currentFile
								FUI_SendMessage GUI\pathText,M_SETTEXT,path$
							Case GUI\pathText
								path$ = k\EventData
						End Select
						Delete k
					Next
					
					RenderWorld
					Flip 0
				Until quitLoop = 1
				FUI_ModalWindow(GUI\hmpWin,0)
				FUI_HideGadget(GUI\hmpWin)
				FUI_ModalWindow(GUI\Window,1)
				
			Case GUI\mnExportHMap
				FUI_SaveDialog("Save landscape..","","Bitmap (*.bmp)|*.bmp|All files (*.*)|*.*")
				path$ = app\currentFile
				ExportHeightmap(path$)
			
			Case GUI\mnQuit
				q = FUI_CustomMessageBox("Are you sure you want to quit?","Quit",MB_YESNO)
				If q = IDYES Then
					Delete GUI
					EndEditor = 1
					Exit
				EndIf
			
			Case GUI\cmTextureLoad
				FUI_OpenDialog("Load texture..","","PNG (*.png)|*.png|Bitmap (*.bmp)|*.bmp|Targa (*.tga)|*.tga|JPG (*.jpg)|*.jpg|All files (*.*)|*.*")
				path$ = app\currentFile
				If FileType(path$) = 1 Then
					tex = t_LoadTexture(Path$,1+8,1.0,1.0,5)
					For n = 1 To Len(path$)
						If Left(Right(path$,n),1) = "\" Or Left(Right(path$,n),1) = "/" Then
							name$ = Trim(Right(path$,n-1))
							Exit
						EndIf
					Next
					
					t.TextureBlock = New TextureBlock
					image = LoadImage(path)
					ResizeImage image,16,16
					t\Node = FUI_ListBoxItem(GUI\TexList,name$,image)
					t\Name = name
					t\Index = tex
					t\Path = path$
					FreeImage image
					PushObject GUI\tlStack,Handle(t)
				EndIf
			
			Default
				For i = 0 To Objects(GUI\LayerStack)-1
					If Int(GetObject(GUI\LayerStack,i)) = Int(e\EventID) Then
						BrushLayer = i
						Exit
					EndIf
				Next
				
				For i = 0 To Objects(GUI\tlStack)-1
					t.TextureBlock = Object.TextureBlock(GetObject(GUI\tlStack,i))
					If t\Node = e\EventID Then
						GUI\selTex = t\Index
						d.CLETexture = Object.CLETexture(GetObject(TexStack,i))
						FUI_SendMessage GUI\ScaleSpinner,M_SETVALUE,d\SX
						Select d\Blend
							Case 1
								FUI_SendMessage GUI\BlendRadioAlp,M_SETCHECKED,1
								FUI_SendMessage GUI\BlendRadioMul,M_SETCHECKED,0
								FUI_SendMessage GUI\BlendRadioAdd,M_SETCHECKED,0
								FUI_SendMessage GUI\BlendRadioMod,M_SETCHECKED,0
							Case 2
								FUI_SendMessage GUI\BlendRadioAlp,M_SETCHECKED,0
								FUI_SendMessage GUI\BlendRadioMul,M_SETCHECKED,1
								FUI_SendMessage GUI\BlendRadioAdd,M_SETCHECKED,0
								FUI_SendMessage GUI\BlendRadioMod,M_SETCHECKED,0
							Case 3
								FUI_SendMessage GUI\BlendRadioAlp,M_SETCHECKED,0
								FUI_SendMessage GUI\BlendRadioMul,M_SETCHECKED,0
								FUI_SendMessage GUI\BlendRadioAdd,M_SETCHECKED,1
								FUI_SendMessage GUI\BlendRadioMod,M_SETCHECKED,0
							Case 5
								FUI_SendMessage GUI\BlendRadioAlp,M_SETCHECKED,0
								FUI_SendMessage GUI\BlendRadioMul,M_SETCHECKED,0
								FUI_SendMessage GUI\BlendRadioAdd,M_SETCHECKED,0
								FUI_SendMessage GUI\BlendRadioMod,M_SETCHECKED,1
						End Select
						Exit
					EndIf
				Next
		End Select
	Next

	Delete Each Event
End Function

;; This function is REQUIRED
;; This function should return whether or not the mouse is over/inside of the
;; GUI area (the area you don't want to have the camera rotate/move when mouse
;; 2 is pressed).
Function CLEMouseInGUI()
	If GUI = Null Then Return -1
	Return (FUI_OverGadget(GUI\Window))
End Function
