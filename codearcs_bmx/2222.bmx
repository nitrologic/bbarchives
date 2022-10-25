; ID: 2222
; Author: klepto2
; Date: 2008-02-25 20:03:15
; Title: OpenGL Render2Texture (FBOs)
; Description: A small but simple solution for  real r2t with opengl

SuperStrict


Type TImageBuffer
	Field Image:TImage
	Field rb:Int[1]
	Field fb:Int[1]
	Field Imageframe:TGLImageframe
	Field Frame:Int = 0
	Field OrigX:Int
	Field OrigY:Int
	Field OrigW:Int
	Field OrigH:Int

	Function SetBuffer:TImageBuffer(Image:TImage,Frame:Int = 0 )
		Local IB:TImageBuffer = New TImageBuffer
		IB.Image = Image
		IB.Frame = Frame
		IB.GenerateFBO()
		IB.BindBuffer()
		Return IB
	End Function
	
	Function Init(Width:Int,Height:Int,Bit:Int=0,Mode:Int=60)
		SetGraphicsDriver(GLMax2DDriver())
		Graphics Width , Height,bit,Mode
		glewInit()
	End Function
	
	Method GenerateFBO()
		ImageFrame = TGLImageFrame(Image.frame(Frame) )
			
		imageframe.v0 = imageframe.v1
		imageframe.v1 = 0.0
	
		Local W:Int = Image.width
		Local H:Int = Image.Height
		
		AdjustTexSize(W , H) 

		
		glGenFramebuffersEXT(1, fb )
	    glGenRenderbuffersEXT(1 , rb) 
	   
	    glBindTexture(GL_TEXTURE_2D, Imageframe.name);
	    glBindFramebufferEXT(GL_FRAMEBUFFER_EXT , fb[0]) ; 
	   
	   
	 	glFramebufferTexture2DEXT(GL_FRAMEBUFFER_EXT, GL_COLOR_ATTACHMENT0_EXT, GL_TEXTURE_2D,  Imageframe.name, 0);
	    glBindRenderbufferEXT(GL_RENDERBUFFER_EXT, rb[0]);
	    glRenderbufferStorageEXT(GL_RENDERBUFFER_EXT, GL_DEPTH_COMPONENT24, W, H);
	    glFramebufferRenderbufferEXT(GL_FRAMEBUFFER_EXT , GL_DEPTH_ATTACHMENT_EXT , GL_RENDERBUFFER_EXT , rb[0])
	   
	    Local status:Int =  glCheckFramebufferStatusEXT(GL_FRAMEBUFFER_EXT)
	   
	    Select status
	       Case GL_FRAMEBUFFER_COMPLETE_EXT 
	          Print "all right" + " : " + Status
	       Case GL_FRAMEBUFFER_UNSUPPORTED_EXT
	          Print "choose different formats"
	       Default
	          End 
	    EndSelect 
   
	End Method
	
	Method BindBuffer()
		GetViewport(OrigX,OrigY,OrigW,OrigH)
		glBindFramebufferEXT(GL_FRAMEBUFFER_EXT , fb[0])
		glMatrixMode GL_PROJECTION
		glLoadIdentity
		glOrtho 0,Image.Width,Image.Width,0,-1,1
		glMatrixMode GL_MODELVIEW 
		glViewport(0 , 0 , Image.Width , Image.Height)
		glScissor 0,0, Image.Width , Image.Height
	End Method
	
	Method UnBindBuffer()
		glBindFramebufferEXT(GL_FRAMEBUFFER_EXT , 0)
		glMatrixMode GL_PROJECTION
		glLoadIdentity
		glOrtho 0,OrigW ,Origh,0,-1,1
		glMatrixMode GL_MODELVIEW 
		glViewport(0 , 0 , OrigW, OrigH)
		glScissor 0,0, OrigW ,OrigH
	End Method
	
	Method Cls(r#=0.0,g#=0.0,b#=0.0,a#=1.0)
		glClearColor r,g,b,a
		glClear GL_COLOR_BUFFER_BIT|GL_DEPTH_BUFFER_BIT
	End Method
	
	Method BufferWidth:Int()
		Return Image.Width
	End Method
	
	Method BufferHeight:Int()
		Return Image.Height
	End Method

End Type


Function AdjustTexSize( width:Int Var,height:Int Var )
	'calc texture size
	width=Pow2Size( width )
	height=Pow2Size( height )
	Repeat
		Local t:Int
		glTexImage2D GL_PROXY_TEXTURE_2D,0,4,width,height,0,GL_RGBA,GL_UNSIGNED_BYTE,Null
		glGetTexLevelParameteriv GL_PROXY_TEXTURE_2D,0,GL_TEXTURE_WIDTH,Varptr t
		If t Return
		If width=1 And height=1 RuntimeError "Unable to calculate tex size"
		If width>1 width:/2
		If height>1 height:/2
	Forever
End Function

Function Pow2Size:Int( n:Int )
	Local t:Int=1
	While t<n
		t:*2
	Wend
	Return t
End Function




TImageBuffer.Init(800,600) 'Same as Graphics but set to GLDriver + glewinit


Global Img:TImage = CreateImage(512, 512)
MidHandleImage Img

Local IB:TImageBuffer = TImageBuffer.SetBuffer(Img) 

Local x:Float = 0
Local Speed:Float = 0.1

Local Angle:Float = 0

While Not KeyHit(Key_Escape)
	SetClsColor 0 , 0 , 255

	Cls
		SetColor 0,255,0
		DrawRect 272,172,256,256
		SetBlend ALPHABLEND
		SetAlpha 1.0
		SetColor 255,255,255
		SetRotation Angle
		DrawImageRect Img , 256+144,256+44,512,512
		SetRotation 0
		SetAlpha 1.0
		Angle:+0.3 
	Flip	
	
	
		IB.BindBuffer()
		IB.Cls(1.0,,,0.4)
		SetColor 255,255,255
		DrawLine 0,0,512,512
		DrawLine 0,512,512,0
		
		IB.UnBindBuffer()
		SetViewport 0,0,800,600
		
Wend
