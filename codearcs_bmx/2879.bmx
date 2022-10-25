; ID: 2879
; Author: BlitzSupport
; Date: 2011-08-12 14:47:41
; Title: VirtualGfx
; Description: Provides virtual display/aspect ratio correction

SuperStrict

' -----------------------------------------------------------------------------
' USAGE: Note that the order is important!
' -----------------------------------------------------------------------------

' 1) Call InitVirtualGraphics before anything else;
' 2) Call Graphics as normal to create display;
' 3) Call SetVirtualGraphics to set virtual display size.

' The optional 'monitor_stretch' parameter of SetVirtualGraphics is there
' because some monitors have the option to stretch non-native ratios to native
' ratios, and you cannot detect this programmatically.

' For instance, my monitor's native resolution is 1920 x 1080, and if I set the
' Graphics mode to 1024, 768, it defaults to stretching that to fill the screen,
' meaning the image is stretched horizontally, so a square will appear non-
' square; however, it also provides an option to scale to the correct aspect
' ratio. Since this is set on the monitor, there's no way to detect or correct
' it other than by offering the option to the user. Leave it off if unsure...

' -----------------------------------------------------------------------------
' Copy this...
' -----------------------------------------------------------------------------

Type VirtualGfx

	Global VG:VirtualGfx
	
	Global DTInitComplete:Int = False
	Global DTW:Int
	Global DTH:Int

	Field vwidth:Int
	Field vheight:Int

	Field vxoff:Float
	Field vyoff:Float

	Field vscale:Float
	
	Function CreateVG:VirtualGfx (width:Int, height:Int)
		VG = New VirtualGfx
		VG.vwidth = width
		VG.vheight = height
	End Function
	
	Function SetVirtualGraphics (vwidth:Int, vheight:Int, monitor_stretch:Int = False)

		' InitVirtualGraphics has been called...
		
		If VirtualGfx.DTInitComplete
		
			' Graphics has been called...
			
			If GraphicsWidth () = 0 Or GraphicsHeight () = 0
				Notify "Programmer error! Must call Graphics before SetVirtualGraphics", True; End
			EndIf
			
		Else
			EndGraphics; Notify "Programmer error! Call InitVirtualGraphics before Graphics!", True; End
		EndIf

		' Reset of display needed when re-calculating virtual graphics stuff/clearing borders...
			
		SetVirtualResolution GraphicsWidth (), GraphicsHeight ()
		SetViewport 0, 0, GraphicsWidth (), GraphicsHeight ()
		SetOrigin 0, 0

		' Store current Cls colours...
		
		Local clsr:Int, clsg:Int, clsb:Int
		GetClsColor clsr, clsg, clsb
		
		' Set to black...
		
		SetClsColor 0, 0, 0
		
		' Got to clear both front AND back buffers or it flickers if new display area is smaller...
		
		Cls
		Flip

		Cls
		Flip
		
		Cls
		Flip
		
		' EDIT, 10 Nov 2011: I had a 3rd Cls/Flip here in case triple-buffering was enabled, but have finally
		' tested this and it wasn't needed. (Tested on NVIDIA GTX 260, Windows 7, with driver
		' version 275.33.)

		' EDIT 2, 30 Dec 2011: Reinstated 3rd Cls/Flip for triple-buffering; see first thread comment!

		' Put back Cls colours...

		SetClsColor clsr, clsg, clsb
		
		' Create new (global) virtual display object...
		
		VirtualGfx.CreateVG (vwidth, vheight)
		
		' Real Graphics width/height...
		
		Local gwidth:Int
		Local gheight:Int
		
		' If monitor is correcting aspect ratio IN FULL-SCREEN MODE, use desktop size, otherwise use
		' specified Graphics size. NB. This assumes user's desktop is using native monitor resolution,
		' as most laptops would be by default...
		
		If monitor_stretch And GraphicsDepth ()

			' Pretend real Graphics mode is desktop width/height...

			gwidth = DTW
			gheight = DTH

		Else

			' Use real Graphics width/height...

			gwidth = GraphicsWidth ()
			gheight = GraphicsHeight ()

		EndIf
		
		' Width/height ratios...
		
		Local graphicsratio:Float = Float (gwidth) / Float (gheight)
		Local virtualratio:Float = Float (VirtualGfx.VG.vwidth) / Float (VirtualGfx.VG.vheight)
		
		' Ratio-to-ratio. Don't even know what you'd call this, but hours of trial and error
		' provided the right numbers in the end...
		
		Local gtovratio:Float = graphicsratio / virtualratio
		Local vtogratio:Float = virtualratio / graphicsratio
		
		' Compare ratios...

		If graphicsratio => virtualratio
			
			' Graphics ratio wider than (or same as) virtual graphics ratio...

			VirtualGfx.VG.vscale = Float (gheight) / Float (VirtualGfx.VG.vheight)
			
			' Now go crazy with trial-and-error... ooh, it works! This tiny bit of code took FOREVER.
			
			Local pixels:Float = Float (VirtualGfx.VG.vwidth) / (1.0 / VirtualGfx.VG.vscale) ' Width after scaling
			Local half_scale:Float = (1.0 / VirtualGfx.VG.vscale) / 2.0

			SetVirtualResolution VirtualGfx.VG.vwidth * gtovratio, VirtualGfx.VG.vheight

			' Offset into 'real' display area...
			
			VirtualGfx.VG.vxoff = (gwidth - pixels) * half_scale
			VirtualGfx.VG.vyoff = 0
		
			' Set up virtual graphics area...
			
			SetViewport VirtualGfx.VG.vxoff, VirtualGfx.VG.vyoff, VirtualGfx.VG.vwidth, VirtualGfx.VG.vheight
			SetOrigin VirtualGfx.VG.vxoff, VirtualGfx.VG.vyoff
			
		Else
			
			' Graphics ratio narrower...
		
			VirtualGfx.VG.vscale = Float (gwidth) / Float (VirtualGfx.VG.vwidth)
			
			Local pixels:Float = Float (VirtualGfx.VG.vheight) / (1.0 / VirtualGfx.VG.vscale) ' Height after scaling
			Local half_scale:Float = (1.0 / VirtualGfx.VG.vscale) / 2.0
			
			SetVirtualResolution VirtualGfx.VG.vwidth, VirtualGfx.VG.vheight * vtogratio
		
			VirtualGfx.VG.vxoff = 0
			VirtualGfx.VG.vyoff = (gheight - pixels) * half_scale
		
			SetViewport VirtualGfx.VG.vxoff, VirtualGfx.VG.vyoff, VirtualGfx.VG.vwidth, VirtualGfx.VG.vheight
			SetOrigin VirtualGfx.VG.vxoff, VirtualGfx.VG.vyoff
			
		EndIf
	
	End Function

	Method VMouseX:Float ()
		Local mx:Float = VirtualMouseX () - vxoff
		If mx < 0 Then mx = 0 Else If mx > vwidth - 1 Then mx = vwidth - 1
		Return mx
	End Method
	
	Method VMouseY:Float ()
		Local my:Float = VirtualMouseY () - vyoff
		If my < 0 Then my = 0 Else If my > vheight - 1 Then my = vheight - 1
		Return my
	End Method
	
	Method VirtualWidth:Int ()
		Return vwidth
	End Method

	Method VirtualHeight:Int ()
		Return vheight
	End Method
	
End Type

' -----------------------------------------------------------------------------
' ... and these helper functions (required)...
' -----------------------------------------------------------------------------

Function InitVirtualGraphics ()

	' There must be a smarter way to check if Graphics has been called...
	
	If GraphicsWidth () > 0 Or GraphicsHeight () > 0 Then EndGraphics; Notify "Programmer error! Call InitVirtualGraphics BEFORE Graphics!", True; End

	VirtualGfx.DTW = DesktopWidth ()
	VirtualGfx.DTH = DesktopHeight ()

	' This only checks once... best to call InitVirtualGraphics again before any further Graphics calls (if you call EndGraphics at all)...
	
	VirtualGfx.DTInitComplete = True

End Function

Function SetVirtualGraphics (vwidth:Int, vheight:Int, monitor_stretch:Int = False)
	VirtualGfx.SetVirtualGraphics (vwidth, vheight, monitor_stretch)
End Function

Function VMouseX:Float ()
	Return VirtualGfx.VG.VMouseX ()
End Function

Function VMouseY:Float ()
	Return VirtualGfx.VG.VMouseY ()
End Function

' Don't need VirtualMouseXSpeed/YSpeed replacements!

Function VirtualWidth:Int ()
	Return VirtualGfx.VG.VirtualWidth ()
End Function

Function VirtualHeight:Int ()
	Return VirtualGfx.VG.VirtualHeight ()
End Function

' -----------------------------------------------------------------------------
' Don't copy after this...
' -----------------------------------------------------------------------------






' -----------------------------------------------------------------------------
' D E M O . . .
' -----------------------------------------------------------------------------

' REQUIRED! BEFORE Graphics!

InitVirtualGraphics

' Toggle for demo's windowed/full-screen switching...

Local FULLSCREEN_TOGGLE:Int = 0

' Change this if your monitor doesn't support it!

Local fullscreenwidth:Int = 1024
Local fullscreenheight:Int = 768
Local fullscreendepth:Int = 32

Local windowedwidth:Int = 640
Local windowedheight:Int = 400
Local windoweddepth:Int = 0

Graphics windowedwidth, windowedheight, windoweddepth

' If user's monitor is set to stretch to its native resolution, this needs to be set to True. I
' would recommend making this a selectable option in your game's configuration as there's no way
' to check this from any program...

Local monitor_adjusting:Int = False

' REQUIRED! AFTER InitVirtualGraphics!

SetVirtualGraphics 1280, 1024, monitor_adjusting

SetClsColor 64, 96, 128
SetMaskColor 255, 0, 255

' Some boxes...

Type Box
	Field x:Float, y:Float
	Field xs:Float, ys:Float
	Field size:Int
	Field r:Int, g:Int, b:Int
End Type

Local boxes:TList = CreateList ()

For Local loop:Int = 0 Until 100
	Local b:Box = New Box
	b.size = Rand (32)
	b.x = Rnd (VirtualWidth () - b.size)
	b.y = Rnd (VirtualHeight () - b.size)
	b.xs = Rnd (-4.0, 4.0)
	b.ys = Rnd (-4.0, 4.0)
	b.r = Rand (100, 255)
	b.g = Rand (100, 255)
	b.b = Rand (100, 255)
	ListAddLast boxes, b
Next

Repeat

	If FULLSCREEN_TOGGLE And KeyHit (KEY_RETURN)

		monitor_adjusting = 1 - monitor_adjusting
		SetVirtualGraphics 1280, 1024, monitor_adjusting

	EndIf
	
	If KeyHit (KEY_SPACE)

		FULLSCREEN_TOGGLE = 1 - FULLSCREEN_TOGGLE

		' Demo of how to change graphics mode...

		EndGraphics
		
		' I recommend calling this on the off-chance that the user's desktop size has changed...
		
		InitVirtualGraphics
		
		' Windowed/full-screen...
		
		If FULLSCREEN_TOGGLE
			Graphics fullscreenwidth, fullscreenheight, fullscreendepth
			SetVirtualGraphics 1280, 1024, monitor_adjusting
		Else
			Graphics windowedwidth, windowedheight, windoweddepth
			SetVirtualGraphics 1280, 1024
		EndIf
		
		' These get reset when exiting graphics mode...
		
		SetClsColor 64, 96, 128
		SetMaskColor 255, 0, 255
		
	EndIf
	
	Cls
	
	SetScale 1, 1

	For Local b:Box = EachIn boxes
		b.x = b.x + b.xs
		b.y = b.y + b.ys
		If b.x < 0 Or (b.x + b.size) > VirtualWidth () Then b.xs = -b.xs; b.x = b.x + b.xs
		If b.y < 0 Or (b.y + b.size) > VirtualHeight () Then b.ys = -b.ys; b.y = b.y + b.ys
		SetColor b.r, b.g, b.b
		DrawRect b.x, b.y, b.size, b.size
	Next
	
	SetColor 255, 0, 0
	DrawRect 0, 0, 16, 16
	DrawRect 0, VirtualHeight () - 16, 16, 16
	DrawRect VirtualWidth () - 16, 0, 16, 16
	DrawRect VirtualWidth () - 16, VirtualHeight () - 16, 16, 16
	
	SetColor 0, 0, 255
	DrawRect VMouseX () - 16, VMouseY () - 16, 32, 32
	
	SetScale 4, 4
	SetColor 255, 255, 255
	DrawText "SPACE: Windowed/full-screen", 20, 20
	If FULLSCREEN_TOGGLE Then DrawText "ENTER: Monitor stretch correction", 20, 60
	
	Flip
	
Until KeyHit (KEY_ESCAPE)

End
