; ID: 2516
; Author: Warner
; Date: 2009-06-30 10:07:23
; Title: wxMax + minib3d
; Description: place minib3d on wxmax window

SuperStrict

Framework wx.wxApp
Import wx.wxFrame
Import wx.wxPanel
Import wx.wxGLCanvas
Import wx.wxglmax2D

Import wx.wxTimer

Import sidesign.minib3d

'Since i want to call 'refresh' from inside the timer, i need to store
'this handle. That way i can address 'prog.canvas'
Global prog:MyApp = New MyApp
prog.Run()

Type MyApp Extends wxApp

	Field frame:wxFrame
	Field panel:wxPanel
	Field canvas:TMiniB3D

	Method OnInit:Int()

		'create a 640x480 window
		frame = New wxFrame.Create(,,"MiniB3d Sample", 0, 0, 640, 480)
		frame.Center()
		
		'create a 320x240 wxPanel
		panel = New wxPanel.Create(frame, wxID_ANY, 160, 0, 320, 240)
		'create wxGLCanvas with minib3d on it
		canvas = TMiniB3D(New TMiniB3D.Create(panel, wxID_ANY, GRAPHICS_BACKBUFFER|GRAPHICS_DEPTHBUFFER, 0, 0, 320, 240))

		'create timer
		Local timer:wxTimer = MyTimer(New MyTimer.Create())
		timer.Start(25)
								
		frame.show()				

		Return True
	
	End Method
			
End Type

Type TMiniB3D Extends wxGLCanvas

	Field init:Int = 0
				
	Method OnPaint(event:wxPaintEvent)
	
		'this could be done better, i'm sure, but for me it was the only way it works
		'when onpaint is called, the canvas is valid and can be initialized by minib3d
		If init = 0 Then
		
			'init minib3d
			SetGraphics CanvasGraphics2D( Self )
			TGlobal.width = 320
			TGlobal.height = 240
			TGlobal.depth = 32
			TGlobal.mode = 2
			TGlobal.GraphicsInit()
			init = 1

			'function for creating camera's etc.			
			InitMB3D()
			
		End If

		'after that, every time onpaint is called, the scene can be rendered
		RenderMB3D()

	End Method

End Type

Global cam:TCamera
Global cube:TEntity

'setup camera and cube
Function InitMB3D()

	cam  = CreateCamera()
	cube = CreateCube()
	MoveEntity cam, 0, 0, -5
	
	EntityTexture cube, LoadTexture("C:\Program Files\BlitzMax\docs\html\bmax120.png") 'hopefully you have this file too
	EntityFX cube, 1
	
	RenderWorld 'i need to do this in my modded version of minib3d, i don't think it is needed in the orig. version

End Function

'render stuff
Function RenderMB3D()

	RenderWorld
	
	BeginMax2D 'max2d works as well
	DrawText "hello", 0, 0
	EndMax2D
	
	Flip

End Function

'timer
Type MyTimer Extends wxTimer

	Field count:Int

	Method Notify()
		TurnEntity cube, 1, 2, 3 'turn cube
		prog.canvas.Refresh() 'render scene
	End Method

End Type
