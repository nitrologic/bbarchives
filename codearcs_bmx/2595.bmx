; ID: 2595
; Author: _Skully
; Date: 2009-10-11 14:25:12
; Title: Projection Matrix
; Description: Projection Matrix - Scaling your game on the fly

[code]
Type TVirtualGraphics
	Global virtualWidth, virtualHeight
	Global xRatio!, yRatio!
	
	Function Set(width=640, height=480, scale#=1)
		TVirtualGraphics.virtualWidth = width
		TVirtualGraphics.virtualHeight = height
		TVirtualGraphics.xRatio! = width / Double(GraphicsWidth())
		TVirtualGraphics.yRatio! = height / Double(GraphicsHeight())
		
	?Win32
		Local dxVer:Byte
		Local D3D7Driver:TD3D7Max2DDriver = TD3D7Max2DDriver(_max2dDriver)
		Local D3D9Driver:TD3D9Max2DDriver = TD3D9Max2DDriver(_max2dDriver)

		If TD3D7Max2DDriver(_max2dDriver) <> Null
			dxVer = 7
		EndIf
 		If TD3D9Max2DDriver(_max2dDriver) <> Null
			dxVer = 9
		EndIf

		If dxVer <> 0 'dx driver was set, otherwise its GL
			Local matrix#[] = [2.0 / (width / scale#), 0.0, 0.0, 0.0,..
			 										0.0, -2.0 / (height / scale#), 0.0, 0.0,..
			 										0.0, 0.0, 1.0, 0.0,..
		 											-1 - (1.0 / width), 1 + (1.0 / height), 1.0, 1.0] ',scale#]
			
			Select dxVer
				Case 7
					D3D7Driver.device.SetTransform(D3DTS_PROJECTION, matrix)
				Case 9
					D3D9Driver._D3DDevice9.SetTransform(D3DTS_PROJECTION, matrix)
			End Select
		Else
	? 
		glMatrixMode(GL_PROJECTION)
		glLoadIdentity()
		glOrtho(0, width / scale:Float, height / scale:Float, 0, - 1, 1)
		glMatrixMode(GL_MODELVIEW)
		glLoadIdentity()
	?Win32
		EndIf
	?
	End Function
	
	Function MouseX()
		Return (BRL.PolledInput.MouseX() * TVirtualGraphics.xRatio!)
	End Function
	
	Function MouseY()
		Return (BRL.PolledInput.MouseY() * TVirtualGraphics.yRatio!)
	End Function
End Type
[/code]
