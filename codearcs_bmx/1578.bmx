; ID: 1578
; Author: WendellM
; Date: 2005-12-27 14:29:05
; Title: Picture Viewer with scoll and zoom
; Description: Load an image then scroll via mouse-drag (instead of using scrollbars) and zoom (with mouse wheel or right-click popup menu)

Rem

	A sample picture viewer, mostly to explore type-enclosed event handling and 
	Tesuji's 256x256 tiled handling of large images.  Also an experiment of
	mouse dragging and right-click popup menu (and mouse wheel) for scaling.
	
	by Wendell Martin, December 27, 2005

End Rem

Framework BRL.D3D7Max2D
Import BRL.System
Import BRL.FileSystem
Import BRL.Pixmap
Import BRL.MaxGUI
Import BRL.Win32MaxGUI

Import BRL.PNGLoader
Import BRL.BMPLoader
Import BRL.JPGLoader

SetGraphicsDriver D3D7Max2DDriver()

AppTitle = "Picture Viewer (Drag with left mouse, zoom with wheel or right click)"


' Load an image and tile in 256x256 chunks:

Const FRAGSIZE = 256 ' maximum image fragment size

Local imageUrl:String
Local pixmap:TPixmap

Local filter$="Picture Files (*.bmp,jpeg,jpg,png):bmp,jpeg,jpg,png;All Files:*"
imageUrl = RequestFile( "Load Picture",filter$, False, CurrentDir()+"/" )
pixmap:TPixmap = LoadPixmap(imageUrl)
If pixmap = Null Then End ' user didn't choose an image, so quit

Global img:BigImage = BigImage.Create(pixmap)

img.scale = 1


' Set up window and viewer:

Local winW# = GadgetWidth( Desktop() ) * .75
Local winH# = GadgetHeight( Desktop() ) * .75
Local offsetX# = ( GadgetWidth( Desktop() ) - winW ) / 2.0
Local offsetY# = ( GadgetHeight( Desktop() ) - winH ) / 2.0
Local style = WINDOW_TITLEBAR|WINDOW_RESIZABLE|WINDOW_CLIENTCOORDS ' ClientCoords for Maximize

Global Win:TGadget=CreateWindow( AppTitle, offsetX,offsetY, WinW,WinH, Null, style)
SetMinWindowSize Win,200,200

Local Viewer:TViewer = New TViewer.create( 0,0, winW,winH, Win )

Local Quit:Int = False


' Main loop:

Repeat
	WaitEvent()
	Select EventSource()
		Case Win
			Select EventID()
				Case EVENT_WINDOWCLOSE
					Quit=True
			End Select
	End Select
Until Quit=True
End


Type TViewer

	Field  can:TGadget
	Global drag
	Global startx, starty
	Global endx, endy
	Field Popup_Gadget:TGadget
	Field Popup_Menu:TGadget
	
	Method Create:TViewer( x,y, w,h, group:TGadget )
		can = CreateCanvas( x,y, w,h, group )
		SetGadgetLayout can, EDGE_ALIGNED, EDGE_ALIGNED, EDGE_ALIGNED, EDGE_ALIGNED ' Lock the size 
		Popup_Menu=CreateMenu( "", Null, Popup_Gadget )
		CreateMenu "10%", 10, popup_menu
		CreateMenu "25%", 25, popup_menu
		CreateMenu "50%", 50, popup_menu
		CreateMenu "75%", 75, popup_menu
		CreateMenu "100%", 100, popup_menu
		CreateMenu "150%", 150, popup_menu
		CreateMenu "200%", 200, popup_menu
		CreateMenu "400%", 400, popup_menu
		CreateMenu "1000%", 1000, popup_menu
		AddHook EmitEventHook, EventHook, Self
		Return Self
	End Method
	
	Function EventHook:Object( id, data:Object, context:Object )
		If data <> Null Then Return TViewer(context).EventHandler( TEvent(data) )
	End Function

	Method EventHandler:Object ( event:TEvent )
		If event.source = can And event.id = EVENT_GADGETPAINT Then
		  SetGraphics CanvasGraphics(can)
		  SetViewport 0,0,GadgetWidth(can),GadgetHeight(can)
		SetBlend maskblend
		  Cls
			img.render
		  Flip
		ElseIf event.id = EVENT_MOUSEDOWN
			If event.data = MOUSE_LEFT Then
				drag = True
				SetPointer POINTER_HAND
				startx = event.x
				starty = event.y
			ElseIf event.data = MOUSE_RIGHT Then
				PopupWindowMenu( Win, popup_menu )
			EndIf
		ElseIf event.id = EVENT_MOUSEUP Then
			drag = False
			SetPointer POINTER_DEFAULT
		ElseIf drag And (event.id = EVENT_MOUSEMOVE) Then
			endx = event.x
			endy = event.y
			img.x :+ endx  - startx
			img.y :+ endy - starty
			startx = endx
			starty = endy
			Cls
			img.render
			Flip
		ElseIf event.id = EVENT_MOUSEWHEEL Then
			If event.data >0 Then
				img.scale :* 1.1
				If img.scale > 10 Then
					img.scale = 10
				Else
					img.x :* 1.1
					img.y :* 1.1
				EndIf
			End If
			If event.data <0 Then
				img.scale :/ 1.1
				If img.scale < 0.1 Then
					img.scale = 0.1
				Else
					img.x :/ 1.1
					img.y :/ 1.1
				EndIf
			EndIf
			Cls
			img.render
			Flip
		ElseIf event.id = EVENT_MENUACTION Then 
			Local scale:Float = event.data / 100.0
			Local deltascale:Float = scale / img.scale
			img.scale :* deltascale
			img.x :* deltascale
			img.y :* deltascale
			Cls
			img.render
			Flip
		Else
			Return event ' not handled here, so pass along
		EndIf
	End Method
			
End Type


' Tesuji's tile functions below:

' =============================
' Image Fragment
' =============================

Type ImageFragment

    Field img:TImage
    Field x,y
    Field rotation:Float = 0
    Field angle:Double
    Field distance:Double

    ' ----------------------------------
    ' constructor
    ' ----------------------------------
    Function create:ImageFragment(pmap:TPixmap,x:Float,y:Float,w,h)
    
        Local frag:ImageFragment = New ImageFragment
        frag.img = LoadImage(PixmapWindow(pmap,x,y,w,h),0|FILTEREDIMAGE)
        x = (pmap.width*.5) - x
        y = (pmap.height*.5) - y
        frag.x = x
        frag.y = y
        frag.angle = ATan2(y,x)-180
        frag.distance = Sqr(x*x + y*y)

        Return frag
 
    End Function

    ' --------------------
    ' Draw individual tile
    ' --------------------
    Method render(scale:Float,xoff:Float=0,yoff:Float=0,rot:Float=0)

        SetRotation rot
        Local d:Float = Self.distance*scale
        SetScale(scale,scale)
        DrawImage(Self.img,(Cos(rot+Self.angle)*d)+xoff,(Sin(rot+Self.angle)*d)+yoff )

    End Method


End Type


' ==================================
' Big Image
' ==================================

Type BigImage 

    Field pixmap:TPixmap
    Field px,py
    Field fragments:TList
    Field scale:Float = 1
    Field width
    Field height
    Field x:Float = 0
    Field y:Float = 0
    Field rotation:Float = 0

    ' ----------------------------------
    ' constructor
    ' ----------------------------------
    Function create:BigImage(p:TPixmap)

        Local bi:BigImage = New BigImage
        bi.pixmap = p
        bi.width = p.width
        bi.height = p.height
        bi.fragments = CreateList()
        bi.load()

        Return bi

    End Function

    ' -------------------------------------
    ' convert pixmap into image fragments
    ' -------------------------------------
    Method load()

        Local px = 0
        Local py = 0
        Local loading = True

        While (loading)

            'FlushMem
            Local w = FRAGSIZE
            If Self.pixmap.width - px < FRAGSIZE w = Self.pixmap.width - px
            Local h = FRAGSIZE
            If Self.pixmap.height - py < FRAGSIZE h = Self.pixmap.height - py
            Local f1:ImageFragment = ImageFragment.create(Self.pixmap,px,py,w,h)
            ListAddLast Self.fragments,f1
            px:+FRAGSIZE
            If px >= Self.pixmap.width
                px = 0
                py:+FRAGSIZE
                If py >= Self.pixmap.height loading = False
            End If
        
        Wend

    End Method

    ' -----------------
    ' Draw entire image
    ' -----------------
    Method render()

        SetOrigin(GraphicsWidth()*.5,GraphicsHeight()*.5)
        For Local f:ImageFragment = EachIn Self.fragments
            f.render(Self.scale,Self.x,Self.y,Self.rotation)
        Next
        SetOrigin(0,0)

    End Method

End Type
