; ID: 2707
; Author: Oddball
; Date: 2010-05-05 20:42:44
; Title: Aspect correction for SetVirtualResolution()
; Description: Corrects the aspect ratio when using virtual resolution, and also automatically applies letterboxing.

'Code: David 'Oddball' Williamson
'6th May 2010
'Enjoy!

SuperStrict

Const ASPECT_KEEP:Int=1
Const ASPECT_STRETCH:Int=0
Const ASPECT_LETTERBOX_FILL:Int=2
Const ASPECT_LETTERBOX_BORDER:Int=3
Const ASPECT_BESTFIT_FILL:Int=4
Const ASPECT_BESTFIT_BORDER:Int=5	'Bestfit border may not work with all drivers(i.e. DirectX)

Private

Global _xoffset:Float
Global _yoffset:Float
Global _vwidth:Float
Global _vheight:Float
Global _mode:Int=ASPECT_STRETCH
Global _bred:Int=0
Global _bgreen:Int=0
Global _bblue:Int=0

Public

Function SetVirtualResolution( width:Float, height:Float, mode:Int=ASPECT_KEEP )
	If mode<>ASPECT_KEEP Then _mode=mode
	If _mode=ASPECT_STRETCH
		_xoffset=0;_yoffset=0;_vwidth=width;_vheight=height;_mode=mode
		brl.max2d.SetVirtualResolution(_vwidth,_vheight)
		brl.max2d.SetViewport _xoffset,_yoffset,Ceil(_vwidth),Ceil(_vheight)
		brl.max2d.SetOrigin _xoffset,_yoffset
		Return
	EndIf
	Local gwidth:Int=GraphicsWidth()
	Local gheight:Int=GraphicsHeight()
	_vwidth=width
	_vheight=height
	Local wratio:Float=gwidth/_vwidth
	Local hratio:Float=gheight/_vheight
	If wratio<hratio Then hratio=wratio
	If _mode&4
		hratio=Floor(hratio)
		hratio=Max(hratio,1)
	EndIf
	height=gheight/hratio
	width=gwidth/hratio
	
	_xoffset=(width-_vwidth)*.5
	_yoffset=(height-_vheight)*.5
	If _mode&4
		_xoffset=Floor(_xoffset)
		_yoffset=Floor(_yoffset)
	EndIf
	brl.max2d.SetVirtualResolution(width,height)
	
	If _mode&1
		Local red:Int,green:Int,blue:Int
		GetClsColor red,green,blue
		SetClsColor _bred,_bgreen,_bblue
		Cls
		SetClsColor red,green,blue
		brl.max2d.SetViewport _xoffset,_yoffset,_vwidth,_vheight
	Else
		brl.max2d.SetViewport 0,0,Ceil(brl.max2d.VirtualResolutionWidth()),Ceil(brl.max2d.VirtualResolutionHeight())
	EndIf
	
	brl.max2d.SetOrigin _xoffset,_yoffset
	
End Function

Function SetBorderColor( red:Int, green:Int, blue:Int )
	_bred=red
	_bgreen=green
	_bblue=blue
End Function

Function SwitchAspectMode( mode:Int )
	SetVirtualResolution _vwidth,_vheight,mode
End Function

Function Flip( sync:Int=-1 )
	brl.Graphics.Flip sync
	If _mode&1
		brl.max2d.SetViewport 0,0,Ceil(brl.max2d.VirtualResolutionWidth())+1,Ceil(brl.max2d.VirtualResolutionHeight())+1
		Local red:Int,green:Int,blue:Int
		GetClsColor red,green,blue
		SetClsColor _bred,_bgreen,_bblue
		Cls
		SetClsColor red,green,blue
		brl.max2d.SetViewport _xoffset,_yoffset,_vwidth,_vheight
	EndIf
End Function

Function SetOrigin( x:Float, y:Float )
	brl.max2d.SetOrigin x+_xoffset,y+_yoffset
End Function

Function GetOrigin( x:Float Var, y:Float Var )
	brl.max2d.GetOrigin x,y
	x:-_xoffset
	y:-_yoffset
End Function

Function SetViewport( x:Int, y:Int, width:Int, height:Int )
	Local x2:Float=Min(x+width,_xoffset+_vwidth)
	Local y2:Float=Min(y+height,_yoffset+_vheight)
	x=Max(x,_xoffset)
	y=Max(y,_yoffset)
	brl.max2d.SetViewport x,y,x2-x,y2-y
End Function

Function GetViewport( x:Int Var,y:Int Var,width:Int Var,height:Int Var )
	brl.max2d.GetViewport x,y,width,height
	x:-_xoffset
	y:-_yoffset
End Function

Function VirtualMouseX:Float()
	Return brl.max2d.VirtualMouseX()-_xoffset
End Function

Function VirtualMouseY:Float()
	Return brl.max2d.VirtualMouseY()-_yoffset
End Function

Function MoveVirtualtMouse( x:Float, y:Float )
	brl.max2d.MoveVirtualMouse x+_xoffset,y+_yoffset
End Function

Function VirtualResolutionWidth:Float()
	Return _vwidth
End Function

Function VirtualResolutionHeight:Float()
	Return _vheight
End Function

'-----------------------
'Test code
'-----------------------
SeedRnd MilliSecs()
Graphics Rand(320,DesktopWidth()),Rand(240,DesktopHeight())

SetBorderColor 32,32,32
SetClsColor 223,223,223

SetVirtualResolution 320,240,ASPECT_LETTERBOX_BORDER

Repeat
	If KeyHit(KEY_1) Then SwitchAspectMode ASPECT_STRETCH
	If KeyHit(KEY_2) Then SwitchAspectMode ASPECT_LETTERBOX_FILL
	If KeyHit(KEY_3) Then SwitchAspectMode ASPECT_LETTERBOX_BORDER
	If KeyHit(KEY_4) Then SwitchAspectMode ASPECT_BESTFIT_FILL
	If KeyHit(KEY_5) Then SwitchAspectMode ASPECT_BESTFIT_BORDER
	If KeyHit(KEY_SPACE)
		Graphics Rand(320,DesktopWidth()),Rand(240,DesktopHeight())
		SetVirtualResolution 320,240
		SetClsColor 223,223,223
		SetColor 255,127,0
	EndIf
	Cls
	SetColor 0,0,255
	DrawText Int(VirtualMouseX())+","+Int(VirtualMouseY()),VirtualMouseX(),VirtualMouseY()
	SetColor 255,0,0
	DrawText "Aspect correction demo",5,5
	SetColor 255,127,0
	DrawText "[1] Stretch",5,35
	DrawText "[2] Letterbox(no border)",5,50
	DrawText "[3] Letterbox",5,65
	DrawText "[4] Best fit(no border)",5,80
	DrawText "[5] Best fit - May not work with DirectX Drivers",5,95
	DrawText "[SPACE] Resize window(random size)",5,125
	Flip
Until KeyHit(KEY_ESCAPE)
