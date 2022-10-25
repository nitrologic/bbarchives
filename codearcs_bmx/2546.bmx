; ID: 2546
; Author: JoshK
; Date: 2009-07-25 10:27:34
; Title: Self-contained splash screen
; Description: Displays a splash screen when your program starts

SuperStrict

Import maxgui.drivers
Import brl.bmploader
Import brl.timer
Import brl.retro
Import brl.random

Type TSplashScreen

	Const IMAGEDIR:String=""'change this if you want

	Field window:TGadget
	Field timer:TTimer
	
	'Method SetText(text:String)
	'	If window
	'		SetStatusText window,text
	'	EndIf
	'EndMethod
	
	Function Create:TSplashScreen(group:TGadget)
		Local splashscreen:TSplashScreen=New TSplashScreen
		Local pixmap:TPixmap
		Local n:Int
		Local file:String
		Local splashfile:String[]
		Local panel:TGadget
		
		SeedRnd MilliSecs()
		Local dir:String[]
		dir=LoadDir(IMAGEDIR)
		If Not dir Return Null
		For file$=EachIn dir
			If ExtractExt(file).tolower()="bmp"
				If Left(file.tolower(),6)="splash"
					splashfile=splashfile[..splashfile.length+1]
					splashfile[splashfile.length-1]=file
				EndIf
			EndIf
		Next
		If Not splashfile.length Return Null
		n=Rand(0,splashfile.length-1)
		pixmap=LoadPixmap(IMAGEDIR+splashfile[n])
		If Not pixmap Return Null
		
		splashscreen.window=CreateWindow(AppTitle,0,0,pixmap.width,pixmap.height,group,WINDOW_CLIENTCOORDS|WINDOW_CENTER|WINDOW_HIDDEN)'|WINDOW_STATUS
		'SetGadgetAlpha splashscreen.window,0.8
		panel=CreatePanel(0,0,splashscreen.window.ClientWidth(),splashscreen.window.ClientHeight(),splashscreen.window)
		SetGadgetPixmap(panel,pixmap)
		ShowGadget splashscreen.window
		ActivateGadget splashscreen.window
		
		Return splashscreen
	EndFunction
	
	Method Close(wait:Int=0)
		If wait
			If timer
				timer.stop()
				RemoveHook(EmitEventHook,EventHook,Self)
			EndIf
			timer=CreateTimer(1.0/Float(wait))
			AddHook(EmitEventHook,EventHook,Self)			
		Else
			RemoveHook(EmitEventHook,EventHook,Self)
			If timer
				timer.Stop()
				timer=Null
			EndIf
			If window
				FreeGadget(window)
				window=Null	
			EndIf
		EndIf
	EndMethod
	
	Function EventHook:Object(id:Int,data:Object,context:Object)
		Local event:TEvent
		Local splashscreen:TSplashScreen
		
		event=TEvent(data)
		If event
			splashscreen=TSplashScreen(context)
			If splashscreen
				Select event.id
				Case EVENT_TIMERTICK
					If event.source=splashscreen.timer
						splashscreen.Close()					
					EndIf
				EndSelect
			EndIf
		EndIf
		Return data
	EndFunction
	
EndType
