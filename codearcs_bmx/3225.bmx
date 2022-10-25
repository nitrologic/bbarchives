; ID: 3225
; Author: GW
; Date: 2015-09-20 03:26:30
; Title: DX9 Render to Texture
; Description: Render to texture for the DirectX 9 Target

SuperStrict
Import brl.max2d
Import brl.d3d9max2d



Type tImageBufferDX9
	Global currentGraphics:TGraphics
	Global currentDriver:TGraphicsDriver
	Global d3dDev:IDirect3DDevice9
	Field d3dImageSurface:IDirect3DSurface9
	Field d3dBackBufferSurface:IDirect3DSurface9
	Field pow2Width:Int, pow2Height:Int
	Field bilinear:Int = False
	
	Field image:TImage
	Field imageFrame:TD3D9ImageFrame
	Field Frame:Int = 0
	Field OrigX:Int
	Field OrigY:Int
	Field OrigW:Int
	Field OrigH:Int

	Function SetBuffer:tImageBufferDX9(Image:TImage, Frame:Int = 0, bfilter:Int = False)
		Local IB:tImageBufferDX9 = New tImageBufferDX9
		IB.Image = Image
		IB.Frame = Frame
		IB.bilinear = bfilter
		IB.GenerateFBO()
		Return IB
	End Function
	
	Function Init(Width:Int, Height:Int, Bit:Int = 0, Mode:Int = 60)
		currentGraphics = Graphics(Width, Height, Bit, Mode)
		currentDriver = GetGraphicsDriver()
		d3dDev = TD3D9Graphics(TMax2DGraphics(currentGraphics)._graphics).GetDirect3DDevice()
	End Function
	
	Method GenerateFBO()
		createD3DRenderTarget()
	End Method
	
	Method BindBuffer()
		beginD3DRenderTarget()
		TMax2DDriver(currentDriver).SetResolution(pow2Width, pow2Height)
	End Method
	
	Method UnBindBuffer()
		endD3DRenderTarget()
		TMax2dDriver( currentDriver ).SetResolution( GraphicsWidth(), GraphicsHeight() )
	End Method
	
	Method Cls(r:Int = 0, g:Int = 0, b:Int = 0, a:Int = 255)
		Local col:Int
		col = b | (G Shl 8) | (r Shl 16) | (A Shl 24)
		d3dDev.Clear(0, Null, D3DCLEAR_TARGET, col, 1.0, 0)
	End Method
	
	Method BufferWidth:Int()
		Return Image.Width
	End Method
	
	Method BufferHeight:Int()
		Return Image.Height
	End Method
	
	Method createD3DRenderTarget()
		image.seqs[0] = GraphicsSeq
		
		'Set the first frame to be a manipulated D3D image frame.
		
		Local d3dFrame:TD3D9ImageFrame = New TD3D9ImageFrame
		
		pow2Width = Pow2Size(image.width)
		pow2Height = Pow2Size(image.height)
		
		If d3dDev.CreateTexture( pow2width, pow2height, 1, D3DUSAGE_RENDERTARGET, D3DFMT_A8R8G8B8, D3DPOOL_DEFAULT, d3dFrame._texture, Null ) < 0
			RuntimeError "Unable to create a D3D 9 render target.~n"
		EndIf
			
		'Taken from 'd3d9max2d.bmx' from the 'D3D9Max2D' brl module.
		
		TD3D9Graphics( TMax2dGraphics( currentGraphics )._graphics ).AutoRelease d3dFrame._texture
		If bilinear Then
			d3dFrame._magfilter = D3DTFG_LINEAR'D3DTFG_POINT 'Optionally, D3DTFG_LINEAR on all three, for a filtered render-target.
			d3dFrame._minfilter = D3DTFG_LINEAR
			d3dFrame._mipfilter = D3DTFG_LINEAR
		Else
			d3dFrame._magfilter = D3DTFG_POINT
			d3dFrame._minfilter = D3DTFG_POINT
			d3dFrame._mipfilter = D3DTFG_POINT
		EndIf
		
		d3dFrame._uscale=1.0 / pow2width
		d3dFrame._vscale=1.0 / pow2height
		
		Local u0:Float, u1:Float = Image.width * d3dFrame._uscale
		Local v0:Float, v1:Float = Image.width * d3dFrame._vscale
	
		d3dFrame._fverts[4]=u0
		d3dFrame._fverts[5]=v0
		d3dFrame._fverts[10]=u1
		d3dFrame._fverts[11]=v0
		d3dFrame._fverts[16]=u1
		d3dFrame._fverts[17]=v1
		d3dFrame._fverts[22]=u0
		d3dFrame._fverts[23]=v1
			
		d3dFrame._seq = GraphicsSeq ''GraphicsSeq' is a global defined in 'brl.mod\graphics.mod\graphics.bmx.'
		image.frames[ 0 ] = d3dFrame
		
		'Get the render-target image surface.
		
		If d3dFrame._texture.GetSurfaceLevel( 0, d3dImageSurface ) < 0
			RuntimeError "Unable to obtain a D3D 9 render target.~n"
			'Return Null
		EndIf
		
		'Get the backbuffer surface so we can return to it later.
		d3dDev.GetRenderTarget(0, d3dBackBufferSurface)
	End Method
	
	Method beginD3DRenderTarget()
		d3dDev.SetRenderTarget(0, d3dImageSurface) 'Set the image as the render-target.
	End Method
	
	Method endD3DRenderTarget()
		d3dDev.SetRenderTarget(0, d3dBackBufferSurface) 'Restore the backbuffer rendering.
	End Method
End Type
Function Pow2Size:Int( n:Int )
	Local t:Int=1
	While t<n
		t:*2
	Wend
	Return t
End Function


'// Test Code Here
'----------------
TestTest

Function TestTest()
	SetGraphicsDriver D3D9Max2DDriver()
	
	Const GW% = 1024
	Const GH% = 768
	
	tImageBufferDX9.Init(GW, GH)	'<-- starting graphics mode need to happen this way 
	
	Global rtImage:TImage = CreateImage(256, 256, 1)
	Global dx9RTT:tImageBufferDX9 = tImageBufferDX9.SetBuffer(rtimage,, True)
	
	While (Not KeyHit(KEY_ESCAPE) Or AppTerminate())
		Cls
			dx9RTT.BindBuffer()
				dx9RTT.Cls(0, 0, 255, 255)
				DrawRect(50, 50 + Sin(MilliSecs() / 5) * 20, 50, 50)
			dx9RTT.UnBindBuffer()
			SetScale 2, 2
			SetRotation 33
			DrawImage(rtImage, MouseX(), MouseY())
			SetScale 1, 1
			SetRotation 0
			DrawText(Fps(), 1, 1)
		Flip 0
	Wend
	
	Function Fps:Int()
	    Global FPStime%, frameCounter%, frameTimer%, totalFrames%
	    frameCounter:+ 1
	    totalFrames:+ 1
	    
	    If frameTimer < MilliSecs()
		FPStime = frameCounter
		frameTimer = 1000 + MilliSecs()
		frameCounter = 0  
	    EndIf
	    return FPStime
	End Function
End Function
'// End Test 
'------------
