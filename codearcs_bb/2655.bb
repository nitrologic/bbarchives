; ID: 2655
; Author: GIB3D
; Date: 2010-02-13 20:47:49
; Title: FastExt postprocess to groups
; Description: Use a postprocess effect such as glow, on certain objects.

Include "FastExt.bb"

Const RG_Normal = 1
Const RG_Glow = 2

Global Camera
Global GlowStick

Global GlowOverlay


InitMedia
Main

Function Main()
	While Not KeyDown(1)
		
		pxRenderPhysic 30,0
		
		TurnEntity GlowStick,0,1,0,1
		
		UpdateWorld
		
		;1. Render Glow Objects with Black Objects
		;2. Put into overlay texture
		;3. Cls
		;4. Render normal objects
		;5. Place glow overlay
		
		ColorFilter 0,0,0
		RenderGroup RG_Normal,Camera,1
		ColorFilter
		RenderGroup RG_Glow,Camera,0
		SpecialRender
		
		CopyRectStretch 0,0,GraphicsWidth(),GraphicsHeight(),0,0,TextureWidth(GlowOverlay),TextureHeight(GlowOverlay),BackBuffer(),TextureBuffer(GlowOverlay)
		
		RenderGroup RG_Normal,Camera,1
		CustomPostprocessOverlay(1,1,255,255,255,GlowOverlay)
		RenderPostprocess(FE_Overlay)
		
		;CopyRectStretch 0,0,TextureWidth(GlowOverlay),TextureHeight(GlowOverlay),0,0,256,256,TextureBuffer(GlowOverlay),BackBuffer()
		
		Flip
	Wend
End Function

Function InitMedia()
	Graphics3D 800,600,0,2
	InitExt()
	SetBuffer BackBuffer()
	
	Local Light = CreateLight()
	GroupAttach RG_Normal,Light
	GroupAttach RG_Glow,Light
	
	Camera = CreateCamera()
	PositionEntity Camera,0,5,-10
	RotateEntity Camera,30,0,0
	
	GlowStick = CreateCylinder()
	PositionMesh GlowStick,0,1,0
	PositionEntity GlowStick,2,-1,4
	ScaleEntity GlowStick,.125,2,.125
	RotateEntity GlowStick,30,0,0
	EntityFX GlowStick,1
	GroupAttach RG_Glow,GlowStick
	
	Local Sphere = CreateSphere(8)
	PositionEntity Sphere,2,1,-2
	EntityColor Sphere,255,80,0
	EntityFX Sphere,4
	GroupAttach RG_Glow,Sphere
	
	Local Ground = CreatePlane()
	EntityTexture Ground,GTexture(100,50,20)
	GroupAttach RG_Normal,Ground
	
	Local Obstruction = CreateCube()
	ScaleMesh Obstruction,1,4,1
	EntityColor Obstruction,150,50,10
	EntityFX Obstruction,1
	GroupAttach RG_Normal,Obstruction
	
	GlowOverlay = CreateTexture(1024,1024,1+16+32)
End Function

Function GTexture(r,g,b) ; Green Checkered Texture
	Local T = CreateTexture(64,64)
	
	SetBuffer TextureBuffer(T)
		Color r,g,b
		Rect 0,0,64,64
		
		Color r*1.14,g*1.14,b*1.14
		Rect 32,0,32,32
		Rect 0,32,32,32
	SetBuffer BackBuffer()
	ScaleTexture T,4,4
	PositionTexture T,.25,.25
	
	Return T
End Function

Function SpecialRender()
	CustomPostprocessGlow(1,0,4)
	CustomPostprocessGlowEx(2,2)
	RenderPostprocess(FE_Glow)
End Function
