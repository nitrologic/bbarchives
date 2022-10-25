; ID: 3233
; Author: Krischan
; Date: 2015-11-22 14:43:38
; Title: Gravity
; Description: Simple example of celestial body movement using newtons formula

SuperStrict

Framework sidesign.minib3d

Graphics3D DesktopWidth(), DesktopHeight(), 32, 1

SeedRnd MilliSecs()

' center mouse
MoveMouse GraphicsWidth() * 0.4, GraphicsHeight() * 0.6



' ------------------------------------------------------------------------------------------------
' Constants
' ------------------------------------------------------------------------------------------------
Const Starmass:Double = 1.9884 * 10 ^ 30
Const PlanetMass:Double = 5.974 * 10 ^ 24
Const g:Double = 6.674 * 10 ^ -11
Const correction:Double = 10 ^ 21

' basic movespeed of star
Global mxs:Double = 0.0
Global mys:Double = 0.0
Global mzs:Double = 0.0

' basic movespeed of planet
Global mxp:Double = 0.0
Global myp:Double = 0.0
Global mzp:Double = 0.1153



' ------------------------------------------------------------------------------------------------
' Variables
' ------------------------------------------------------------------------------------------------
Local vx1:Double
Local vx2:Double
Local vy1:Double
Local vy2:Double
Local vz1:Double
Local vz2:Double

Local massfactor_s:Float = 1.0
Local massfactor_p:Float = 1.0



' ------------------------------------------------------------------------------------------------
' Light
' ------------------------------------------------------------------------------------------------
CreateLight(2)
AmbientLight 32, 32, 32



' ------------------------------------------------------------------------------------------------
' Star
' ------------------------------------------------------------------------------------------------
Local star:TEntity = CreatePivot()
Local star0:TSprite = CreateSprite(star)
Local star1:TSprite = CreateSprite(star)
Local star2:TSprite = CreateSprite(star)
Local star3:TSprite = CreateSprite(star)
ScaleSprite star0, 1, 1
ScaleSprite star1, 2, 2
ScaleSprite star2, 4, 4
ScaleSprite star3, 8, 8
EntityColor star0, 255, 255, 255
EntityColor star1, 255, 192, 128
EntityColor star2, 255, 255, 224
EntityColor star3, 255, 255, 224
EntityAlpha star0, 1
EntityAlpha star1, 1
EntityAlpha star2, 0.75
EntityAlpha star3, 0.5
EntityFX star0, 1
EntityFX star1, 1
EntityFX star2, 1
EntityFX star3, 1
EntityBlend star0, 3
EntityBlend star1, 3
EntityBlend star2, 3
EntityBlend star3, 3
EntityOrder star0, -1
EntityOrder star1, -2
EntityOrder star2, -3
EntityOrder star3, -4
Local startex:TTexture = CreateSunTexture()
EntityTexture star0, startex
EntityTexture star1, startex
EntityTexture star2, startex
EntityTexture star3, startex



' ------------------------------------------------------------------------------------------------
' Planet
' ------------------------------------------------------------------------------------------------
Local planet:TMesh = CreateSphere(32)
EntityFX planet, 2
UpdateMesh(planet, 255, 128, 0)
PositionEntity planet, -25, 0, 0
ScaleEntity planet, 0.5, 0.5, 0.5



' ------------------------------------------------------------------------------------------------
' Camera
' ------------------------------------------------------------------------------------------------
Local campivot:TPivot = CreatePivot(star)
Local cam:TCamera = CreateCamera(campivot)
PositionEntity cam, 0, 0, -40
CameraRange cam, 0.1, 10000



' ------------------------------------------------------------------------------------------------
' Star Ecliptic
' ------------------------------------------------------------------------------------------------
Local ecliptic:TEntity = CreateCube(star)
EntityFX ecliptic, 1 + 16
EntityAlpha ecliptic, 0.25
EntityColor ecliptic, 0, 0, 255
ScaleEntity ecliptic, 50, 10 ^ -10, 50
Local raster:TTexture = CreateRasterTexture()
EntityTexture ecliptic, raster
ScaleTexture raster, 1.0 / 50, 1.0 / 50
PositionTexture raster, 0.5, 0.5
EntityBlend ecliptic, 3



' ------------------------------------------------------------------------------------------------
' Path
' ------------------------------------------------------------------------------------------------
Global path:TMesh = CreateMesh()
Global surf:TSurface = CreateSurface(path)
EntityFX path, 1 + 2



' ------------------------------------------------------------------------------------------------
' Main loop
' ------------------------------------------------------------------------------------------------
While Not AppTerminate()

	If KeyHit(KEY_ESCAPE) Then End
	
	If KeyHit(KEY_F1) Then massfactor_s = 10.0
	If KeyHit(KEY_F2) Then massfactor_p = 100000.0
	
	If KeyHit(KEY_F3) Then
	
		mxp = Rnd(-0.1, 0.1)
		myp = Rnd(-0.1, 0.1)
		mzp = Rnd(-0.1, 0.1)
		
		For Local P:TQuad = EachIn TQuad.List
	
			P.Remove
		
		Next
		
		FreeEntity path
		path = CreateMesh()
		surf = CreateSurface(path)
		EntityFX path, 1 + 2

	EndIf
		
	
	massfactor_s:+((KeyDown(KEY_UP) - KeyDown(KEY_DOWN)) / 100.0)
	massfactor_p:+((KeyDown(KEY_RIGHT) - KeyDown(KEY_LEFT)) * 1000.0)
	
	' calc distance
	Local d:Double = EntityDistance(planet, star)
	
	' calc gravitational force using newtons formula
	Local F:Double = (g * starmass * massfactor_s * planetmass * massfactor_p) / (d * d)
	
	' adjust force to scene
	F:/correction
	
	' get coordinates of objects
	Local x1:Double = EntityX(star)
	Local x2:Double = EntityX(planet)
	Local y1:Double = EntityY(star)
	Local y2:Double = EntityY(planet)
	Local z1:Double = EntityZ(star)
	Local z2:Double = EntityZ(planet)
		
	' delta position
	Local dx:Double = x1 - x2
	Local dy:Double = y1 - y2
	Local dz:Double = z1 - z2
	
	' delta vector
	Local dv1:Double = F / (starmass * massfactor_s)
	Local dv2:Double = F / (planetmass * massfactor_p)
	
	' new vector
	vx1 = vx1 - dv1 * dx
	vx2 = vx2 + dv2 * dx
	vy1 = vy1 - dv1 * dy
	vy2 = vy2 + dv2 * dy
	vz1 = vz1 - dv1 * dz
	vz2 = vz2 + dv2 * dz
					
	' move star
	MoveEntity star, mxs + vx1, mys + vy1, mzs + vz1
	
	' move planet
	MoveEntity planet, mxp + vx2, myp + vy2, mzp + vz2
	
	' camera movement
	If EntityDistance(cam, star) > 10 Then TranslateEntity cam, 0, 0, MouseDown(1) - MouseDown(2) Else TranslateEntity cam, 0, 0, -1
	RotateEntity campivot, Normalize(MouseY(), 0, GraphicsHeight() - 1, -90, 90), Normalize(MouseX(), 0, GraphicsWidth() - 1, 180, -180), 0
	PointEntity cam, campivot
		
	' add star path quads
	Local P:TQuad
	P = New TQuad
	P.x = EntityX(star)
	P.y = EntityY(star)
	P.z = EntityZ(star)
	P.scalex = 0.05
	P.scaley = 0.05
	P.RGB = [255, 255, 0]
	P.mesh = path
	P.surf = surf
	P.add()
	
	' add planet path quads
	P = New TQuad
	P.x = EntityX(planet)
	P.y = EntityY(planet)
	P.z = EntityZ(planet)
	P.scalex = 0.05
	P.scaley = 0.05
	P.RGB = [255, 128, 0]
	P.mesh = path
	P.surf = surf
	P.add()
	
	' face all path quads to cam
	For P:TQuad = EachIn TQuad.List
	
		P.Update(cam)
	
	Next
			
	RenderWorld
	
	' 2D output
	BeginMax2D()
	
		DrawText "Distance..........: " + d, 0, 0
		DrawText "Force.............: " + F, 0, 15
		DrawText "Force Star........: " + dv1, 0, 30
		DrawText "Force Planet......: " + dv2, 0, 45
		DrawText "Move X............: " + vx2, 0, 60
		DrawText "Move Y............: " + vy2, 0, 75
		DrawText "Star Mass Factor..: " + massfactor_s, 0, 90
		DrawText "Planet Mass Factor: " + massfactor_p, 0, 105

	EndMax2D()
	
	Flip

Wend

End



' ------------------------------------------------------------------------------------------------
' Normalize value
' ------------------------------------------------------------------------------------------------
Function Normalize:Float(value:Float = 128.0, value_min:Float = 0.0, value_max:Float = 255.0, norm_min:Float = 0.0, norm_max:Float = 1.0, limit:Int = False)

	' normalize	
	Local result:Float=((value-value_min)/(value_max-value_min))*(norm_max-norm_min)+norm_min

	' limit	
	If value>norm_max Then value=norm_max Else If value<norm_min Then value=norm_min

	Return result
	
End Function



' ------------------------------------------------------------------------------------------------
' Create Raster Texture
' ------------------------------------------------------------------------------------------------
Function CreateRasterTexture:TTexture()
	
	Local tex:TTexture = CreateTexture(64, 64, 1)
	
	BeginMax2D()
	Cls
	SetColor 64, 64, 64
	DrawRect 0, 0, 63, 63
	
	SetColor 255, 255, 255
	DrawLine 0, 0, 63, 0
	DrawLine 0, 0, 0, 63
	DrawLine 0, 63, 63, 63
	DrawLine 63, 63, 63, 0
	
	EndMax2D()

	BackBufferToTex(tex, 0)
	
	Return tex

End Function



' ------------------------------------------------------------------------------------------------
' Create Sun Texture
' ------------------------------------------------------------------------------------------------
Function CreateSunTexture:TTexture()

	Local pixmap:TPixmap = CreatePixmap(256, 256, PF_RGBA8888)
	
	Local i:Float, j:Int, col:Int, rgb:Int
	
	For j = 0 To 255
		
		col=255-j
		If col > 255 Then col = 255
		rgb=col*$1000000+col*$10000+col*$100+col
		
		For i=0 To 360 Step 0.1
			
			WritePixel(pixmap, 128 + (Sin(i) * j * 0.5), 128 + (Cos(i) * j * 0.5), rgb)
			
		Next
		
	Next
	
	Local tex:TTexture = LoadTexturePixmap(pixmap, 2)
	
	Return tex

End Function



' ------------------------------------------------------------------------------------------------
' Colorize mesh vertex colors
' ------------------------------------------------------------------------------------------------
Function UpdateMesh(mesh:TMesh, r:Int = 255, g:Int = 255, b:Int = 255, a:Float = 1.0)
	
	Local surf:TSurface = GetSurface(mesh, 1)
		
	For Local v:Int = 0 To CountVertices(surf) - 1
	
		VertexColor surf, v, r, g, b, a
		
	Next
	
End Function



' ------------------------------------------------------------------------------------------------
' Quad Type Library
' ------------------------------------------------------------------------------------------------
Type TQuad

	Field x:Float = 0.0										' position x
	Field y:Float = 0.0										' position y
	Field z:Float = 0.0										' position z
	
	Field scalex:Float = 1.0								' current size X
	Field scaley:Float = 1.0								' current size Y
	
	Field RGB:Int[] = [255, 255, 255, 255]					' star vertex color
	Field Alpha:Float = 1.0									' vertex alpha

	Field v:Int = 0											' vertex counter
	Field mesh:TMesh = Null									' mesh pointer
	Field surf:TSurface = Null								' surface pointer
				
	Global List:TList = CreateList()



	' --------------------------------------------------------------------------------------------
	' METHOD: Create new Object in TList
	' --------------------------------------------------------------------------------------------
	Method New()

		ListAddLast(List, Self)

	End Method

	
	
	' --------------------------------------------------------------------------------------------
	' METHOD: Remove Object in TList
	' --------------------------------------------------------------------------------------------
	Method Remove()

		ListRemove(List, Self)
		

	End Method

	' --------------------------------------------------------------------------------------------
	' METHOD: Add new Quad
	' --------------------------------------------------------------------------------------------
	Method Add(col:Int = 0, row:Int = 0)

		' surface setup
		If surf = Null Then surf = surf
		If v + 4 > 32768 Then
			v = 0
			surf = CreateSurface(mesh)
		EndIf

		' add Vertices
		Local V0:Int = AddVertex(surf, 0, 0, 0, 1, 0)
		Local V1:Int = AddVertex(surf, 0, 0, 0, 1, 1)
		Local V2:Int = AddVertex(surf, 0, 0, 0, 0, 1)
		Local V3:Int = AddVertex(surf, 0, 0, 0, 0, 0)

		' color vertices
		VertexColor surf, V0, RGB[0], RGB[1], RGB[2], Alpha
		VertexColor surf, V1, RGB[0], RGB[1], RGB[2], Alpha
		VertexColor surf, V2, RGB[0], RGB[1], RGB[2], Alpha
		VertexColor surf, V3, RGB[0], RGB[1], RGB[2], Alpha

		' connect triangles
		AddTriangle surf, V0, V1, V2
		AddTriangle surf, V0, V2, V3
		
		VertexTexCoords surf, V0, col + 1, row
		VertexTexCoords surf, V1, col + 1, row + 1
		VertexTexCoords surf, V2, col, row + 1
		VertexTexCoords surf, V3, col, row

		' increase vertex counter
		If v >= 4 Then v = V0 + 4 Else v = V0

	End Method

	' --------------------------------------------------------------------------------------------
	' METHOD: Update a Quad
	' --------------------------------------------------------------------------------------------
	Method Update(target:TEntity)
	
		TFormVector scalex, 0, 0, target, Null
		Local X1:Float = TFormedX()
		Local Y1:Float = TFormedY()
		Local Z1:Float = TFormedZ()
    
		TFormVector 0, scaley, 0, target, Null
		Local X2:Float = TFormedX()
		Local Y2:Float = TFormedY()
		Local Z2:Float = TFormedZ()
		
		' set vertices
		VertexCoords surf, v + 0, x - x1 - x2, y - y1 - y2, z - z1 - z2
		VertexCoords surf, v + 1, x - x1 + x2, y - y1 + y2, z - z1 + z2
		VertexCoords surf, v + 2, x + x1 + x2, y + y1 + y2, z + z1 + z2
		VertexCoords surf, v + 3, x + x1 - x2, y + y1 - y2, z + z1 - z2
	
		' set colors
		VertexColor surf, v + 0, RGB[0], RGB[1], RGB[2], Alpha
		VertexColor surf, v + 1, RGB[0], RGB[1], RGB[2], Alpha
		VertexColor surf, v + 2, RGB[0], RGB[1], RGB[2], Alpha
		VertexColor surf, v + 3, RGB[0], RGB[1], RGB[2], Alpha
						
	End Method

End Type



' ----------------------------------------------------------------------------
' Load a pixmap to a brush
' ----------------------------------------------------------------------------
Function LoadBrushPixmap:TBrush(pixmapin:TPixmap,flags:Int=1,u_scale:Float=1.0,v_scale:Float=1.0)

	Return PixTBrush.LoadBrushPixmap(pixmapin,flags,u_scale,v_scale)
	
End Function



' ----------------------------------------------------------------------------
' Load a pixmap to a texture
' ----------------------------------------------------------------------------
Function LoadTexturePixmap:TTexture(pixmapin:TPixmap,flags:Int=1)

	Return PixTTexture.LoadTexturePixmap(pixmapin,flags)
	
End Function

Type PixTBrush Extends TBrush

	Function LoadBrushPixmap:TBrush(pixmapin:TPixmap,flags:Int=1,u_scale:Float=1.0,v_scale:Float=1.0)
	
		Local brush:TBrush=New TBrush
		
		brush.tex[0]=PixTTexture.LoadTexturePixmap:TTexture(pixmapin,flags)
		brush.no_texs=1
		
		brush.tex[0].u_scale#=u_scale#
		brush.tex[0].v_scale#=v_scale#
				
		Return brush
		
	End Function
	
End Type

Type PixTTexture Extends TTexture

	Function LoadTexturePixMap:TTexture(pixmapin:TPixmap,flags:Int=1,tex:TTexture=Null)
	
		Return LoadAnimTexturePixMap:TTexture(pixmapin,flags,0,0,0,1,tex)
		
	End Function

	Function LoadAnimTexturePixMap:TTexture(pixmapin:TPixmap,flags:Int,frame_width:Int,frame_height:Int,first_frame:Int,frame_count:Int,tex:TTexture=Null)
	
		Local pixmapFileName:String="pixmap"+MilliSecs()+Rnd()
		
		If flags&128 Then Return LoadCubeMapTexture(pixmapFileName$,flags,tex)
	
		If tex = Null Then tex:TTexture = New TTexture
		
		tex.file:String=pixmapFileName
		tex.file_abs:String=pixmapFileName
		
		' set tex.flags before TexInList
		tex.flags=flags
		tex.FilterFlags()
		
		' check to see if texture with same properties exists already, if so return existing texture
		Local old_tex:TTexture
		old_tex=tex.TexInList()
		If old_tex<>Null And old_tex<>tex
			Return old_tex
		Else
			If old_tex<>tex
				ListAddLast(tex_list,tex)
			EndIf
		EndIf
	
		' load pixmap
		tex.pixmap = CopyPixmap(pixmapin)
		
		' check to see if pixmap contain alpha layer, set alpha_present to true if so (do this before converting)
		Local alpha_present:Int=False
		If tex.pixmap.format=PF_RGBA8888 Or tex.pixmap.format=PF_BGRA8888 Or tex.pixmap.format=PF_A8 Then alpha_present=True
	
		' convert pixmap to appropriate format
		If tex.pixmap.format<>PF_RGBA8888
			tex.pixmap=tex.pixmap.Convert(PF_RGBA8888)
		EndIf
		
		' if alpha flag is true and pixmap doesn't contain alpha info, apply alpha based on color values
		If tex.flags&2 And alpha_present=False
			tex.pixmap=ApplyAlpha(tex.pixmap)
		EndIf		
	
		' if mask flag is true, mask pixmap
		If tex.flags&4
			tex.pixmap=MaskPixmap(tex.pixmap,0,0,0)
		EndIf
		
		' ---
		
		' if tex not anim tex, get frame width and height
		If frame_width=0 And frame_height=0
			frame_width=tex.pixmap.width
			frame_height=tex.pixmap.height
		EndIf
	
		' ---
		
		tex.no_frames=frame_count
		tex.gltex=tex.gltex[..tex.no_frames]
	
		' ---
		
		' pixmap -> tex
	
		Local xframes:Int=tex.pixmap.width/frame_width
		Local yframes:Int=tex.pixmap.height/frame_height
			
		Local startx:Int=first_frame Mod xframes
		Local starty:Int=(first_frame/yframes) Mod yframes
			
		Local x:Int=startx
		Local y:Int=starty
	
		Local pixmap:TPixmap
	
		For Local i:Int=0 To tex.no_frames-1
	
			' get static pixmap window. when resize pixmap is called new pixmap will be returned.
			pixmap=tex.pixmap.Window(x*frame_width,y*frame_height,frame_width,frame_height)
			x=x+1
			If x>=xframes
				x=0
				y=y+1
			EndIf
		
			' ---
		
			pixmap=AdjustPixmap(pixmap)
			tex.width=pixmap.width
			tex.height=pixmap.height
			Local width:Int=pixmap.width
			Local height:Int=pixmap.height
	
			Local name:Int
			glGenTextures 1,Varptr name
			glBindtexture GL_TEXTURE_2D,name
	
			Local mipmap:Int
			If tex.flags&8 Then mipmap=True
			Local mip_level:Int=0
			Repeat
				glPixelStorei GL_UNPACK_ROW_LENGTH,pixmap.pitch/BytesPerPixel[pixmap.format]
				glTexImage2D GL_TEXTURE_2D,mip_level,GL_RGBA8,width,height,0,GL_RGBA,GL_UNSIGNED_BYTE,pixmap.pixels
				If Not mipmap Then Exit
				If width=1 And height=1 Exit
				If width>1 width:/2
				If height>1 height:/2
	
				pixmap=ResizePixmap(pixmap,width,height)
				mip_level:+1
			Forever
			tex.no_mipmaps=mip_level
	
			tex.gltex[i]=name
	
		Next
				
		Return tex
		
	End Function
	
End Type
