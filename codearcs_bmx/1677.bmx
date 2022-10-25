; ID: 1677
; Author: Yan
; Date: 2006-04-17 10:53:15
; Title: Windows Screen Saver Framework
; Description: A simple windows screen saver framework for BMax + MaxGUI

Strict

Import "icons.o"

Framework BRL.D3D7Max2D
Import BRL.RamStream
Import BRL.PNGLoader
Import BRL.Timer
Import BRL.Random
Import MaxGUI.Drivers ' Pre 1.30 use - BRL.Win32MaxGUI
Import BRL.EventQueue

Extern "win32"
	Function GetClientRect(hWnd, lpRect:Byte Ptr) ' I wanna use a type damnit...
	Function GetWindowRect(hWnd, lpRect:Byte Ptr) ' ...ditto...
	Function SetWindowLong(hWnd, index, nIndex) = "SetWindowLongA@12"
	Function SetParent(hWnd, parenthWnd)
	Function ReleaseDC(hWnd, hDC)
	Function SystemParametersInfo(uiAction, uiParam, pvParam Var, fWinIni) = "SystemParametersInfoA@16"
End Extern

Incbin "gfx\cheese.png"

Const SPI_SETSCREENSAVERRUNNING = $61 

Type TRect
	Field x, y, w, h
End Type

Type TScreenSaver
	Const SAVER_NAME$ = "Cheesy"
	
	Field graphicsMode = -1 ' Get desktop settings
	Field imagePath$ = "incbin::gfx\cheese.png" ' Hard coded for this example
	Field imageScale# = 0.5, imageNumber = 8
	Field screenWidth, screenHeight
	
	Field image:TImage
	
	Method Start()
		ReadCFG()
		
		If appargs.length = 1
			AppArgs = AppArgs[..2]
			AppArgs[1] = "/c"
		EndIf
		
		Select AppArgs[1].ToLower()[..2]
			Case "/s", "-s"
				Local oldState ' (Win9x only)
				SystemParametersInfo(SPI_SETSCREENSAVERRUNNING, True, oldState, 0) ' (Win9x only)
				If oldstate Then Return ' (Win9x only) untested
						
				DoFullScreen()
				
				SystemParametersInfo(SPI_SETSCREENSAVERRUNNING, False, oldState, 0) ' (Win9x only)
				
			Case "/c", "-c"
				DoConfig()
		
			Case "/a", "-a"
				'Start password verification (Win9x only)
				
			Case "/p", "-p", "/l", "-l"
				DoPreviewWindow()
				
		End Select
	End Method
	
	Method DoFullScreen()
		Local screenDepth, screenHertz
		
		If graphicsMode < 0
			Local hWnd = QueryGadget(Desktop(), QUERY_HWND)
			Local hdc = GetDC(hWnd)
			If hdc = Null Then Return
			
			screenWidth = GetDeviceCaps(hdc, HORZRES)
			screenHeight = GetDeviceCaps(hdc, VERTRES)
			screenDepth = GetDeviceCaps(hdc, BITSPIXEL)
			If screenDepth < 16
				screenDepth = GetDeviceCaps(hdc, PLANES)
				If screenDepth < 16
					ReleaseDC(hWnd, hdc)
					Return
				EndIf
			EndIf	
			screenHertz = GetDeviceCaps(hdc, VREFRESH)
			If screenHertz < 60 Then screenHertz = -1
			
			ReleaseDC(hWnd, hdc)
		Else
			GetGraphicsMode(graphicsMode, screenWidth, screenHeight, screenDepth, screenHertz)
		EndIf	
		
		Graphics screenWidth, screenHeight, screenDepth, screenHertz
		HideMouse
		
		Initialise(imageNumber)
		
		ClearEventQueue()
					
		Repeat
			Select PollEvent()			
				Case EVENT_MOUSEDOWN, EVENT_KEYDOWN
					Return
				
				Case EVENT_APPTERMINATE, EVENT_WINDOWCLOSE
					Return
				
				Case EVENT_MOUSEMOVE
					Global mx = EventX(), my = EventY()
					
					If (Abs(mx - EventX()) > 1) Or (Abs(my - EventY()) > 1) Then Return
					
					mx = EventX()
					my = EventY()
									
			End Select
			
			Update()
			Draw()
			
			Flip
		Forever
	End Method		
			
	Method DoPreviewWindow()
		Local previewTimer:TTimer = CreateTimer(100)
		
		Local prevRect:TRect = New TRect
		Local prevHWnd
		If AppArgs[1].length = 2
			prevHWnd = Int(AppArgs[2]) 		
		Else
			prevHWnd = Int(AppArgs[1][3..AppArgs[1].length])
		EndIf
		
		GetClientRect(prevhWnd, prevRect)
		screenWidth = prevRect.w
		screenHeight = prevRect.h
		
		Local previewWindow:TGadget = CreateWindow(SAVER_NAME$ + " Preview", prevRect.x, prevRect.y, prevRect.w, prevRect.h, Null, WINDOW_HIDDEN)
		Local BMXHWnd = QueryGadget(previewWindow, QUERY_HWND)
		
		SetWindowLong(BMXHWnd, GWL_STYLE, WS_VISIBLE | WS_CHILD)
		SetParent(BMXHWnd, prevHWnd)
		SetWindowLong(BMXHWnd, GWL_HWNDPARENT, prevHWnd)
		
		Local previewCanvas:TGadget = CreateCanvas(0, 0, prevRect.w, prevRect.h, previewWindow)
		SetGraphics CanvasGraphics(previewCanvas)
		
		Initialise(imageNumber)
		
		Repeat
			Select WaitEvent()
				Case EVENT_TIMERTICK
					Update()
					
					RedrawGadget previewCanvas
					
				Case EVENT_GADGETPAINT
					Draw()
	
					Flip
									
				Case EVENT_APPTERMINATE, EVENT_WINDOWCLOSE
					Return
					
			End Select
		Forever
	End Method
	
	'Change folowing methods to suit saver
	Method DoConfig()
		Local configWidth = 300, configHeight = 159
		Local previewTimer:TTimer = CreateTimer(100)
		Local PropertiesHWnd
		Local configWindow:Tgadget
		Local previewCanvas:TGadget
		Local sizeSlider:TGadget, numberSlider:TGadget, numberText:TGadget
		Local okButton:TGadget, cancelButton:TGadget
		
		If AppArgs[1].length = 2
			If AppArgs.length > 2 Then PropertiesHWnd = Int(AppArgs[2]) 		
		Else
			PropertiesHWnd = Int(AppArgs[1][3..AppArgs[1].length])
		EndIf
				
		If PropertiesHWnd
			Local propRect:TRect = New TRect
			GetWindowRect(PropertiesHWnd, propRect)
			configWindow = CreateWindow(SAVER_NAME$ + " Preferences",..
																	propRect.x + (((propRect.w - propRect.x) - configWidth) / 2),..
																	propRect.y + (((propRect.h - propRect.y) - configHeight) / 2),..
																	configWidth, configHeight, Null, WINDOW_TITLEBAR | WINDOW_CLIENTCOORDS)
		Else	
			configWindow = CreateWindow(SAVER_NAME$ + " Preferences", (GadgetWidth(Desktop()) - configWidth) / 2,..
																	(GadgetHeight(Desktop()) - configHeight) / 2, configWidth, configHeight,..
																	Null, WINDOW_TITLEBAR | WINDOW_CLIENTCOORDS)
		EndIf
		
		screenWidth = 133
		screenHeight = 100
		
		CreateLabel("Image Size", 153, 15, 137, 20, configWindow, LABEL_CENTER)
		sizeSlider = CreateSlider(153, 35, 137, 20, configWindow, SLIDER_HORIZONTAL | SLIDER_TRACKBAR)
		SetSliderRange sizeSlider, 1, 30
		SetSliderValue sizeSlider, imageScale# * 15.0
		
		CreateLabel("Image Number :", 153, 82, 87, 20, configWindow, LABEL_CENTER)
		numberText = CreateTextField(240, 80, 26, 20, configWindow)
		SetGadgetText(numberText, imageNumber)
		numberSlider = CreateSlider(267, 80, 18, 20, configWindow, SLIDER_VERTICAL | SLIDER_STEPPER)
		SetSliderRange numberSlider, 1, 200
		SetSliderValue numberSlider, imageNumber
		
		CreateLabel("Written by Yan - 2005", 10, 130, 120, 20, configWindow, LABEL_CENTER)
		
		okButton = CreateButton("OK", 140, 125, 70, 24, configWindow)
		cancelButton = CreateButton("Cancel", 220, 125, 70, 24, configWindow)
		
		ActivateGadget cancelButton 
		
		previewCanvas = CreateCanvas(10, 10, screenWidth, screenHeight, configWindow, 1)
		SetGraphics  CanvasGraphics(previewCanvas)
		
		Initialise(imageNumber)
		
		Repeat
			Select WaitEvent()
				Case EVENT_GADGETACTION
					Select EventSource()
						Case sizeSlider
							imageScale# = EventData() / 15.0
							
						Case numberSlider	
							AdjustImageNumber(EventData() - ImageNumber)
							imageNumber = EventData()
							SetGadgetText(numberText, imageNumber)
							
						Case okButton
							WriteCFG()
							Return
							
						Case cancelButton
							Return
							
					End Select
				
				Case EVENT_TIMERTICK
					Update()
					
					RedrawGadget previewCanvas
					
				Case EVENT_GADGETPAINT
					Draw()
					
					Flip
									
				Case EVENT_APPTERMINATE, EVENT_WINDOWCLOSE, EVENT_APPSUSPEND
					Return
					
			End Select
		Forever
	End Method
	
	Method Initialise(number)
		AutoMidHandle True
		SetBlend ALPHABLEND
		
		If image = Null
			image:TImage = LoadImage(imagepath$, FILTEREDIMAGE | MIPMAPPEDIMAGE)
			If image = Null Then Return
		EndIf
		
		Local scaleX# = (screenWidth / 1024.0)
		Local scaleY# = (screenHeight / 768.0)
		
		For Local c=1 To number
			TSprite.Create(image, Rand(0, screenWidth), Rand(0, screenHeight), Rnd(-4, 4) * scaleX#,..
												Rnd(-4, 4) * scaleY#, 0, scaleX#, scaleY#, Rand(0, 359), Rnd(-4, 4), 1, 0)
		Next
	End Method
	
	Method AdjustImageNumber(number)
		If number = 0 Then Return
		
		If Sgn(number) = 1
			Initialise(number)
		Else
			For Local c = number To -1
				spriteList.RemoveLast()
			Next
		EndIf
	End Method
	
	Method Update()
		For Local thisSprite:TSprite = EachIn spriteList
				thisSprite.Update(screenWidth, screenHeight)
		Next
	End Method
	
	Method Draw()
		Cls
		For Local thisSprite:TSprite = EachIn spriteList
			thisSprite.Draw(imageScale#)
		Next
		
	End Method
	
	Method WriteCFG()
		Local stream:TStream
		
		stream = OpenStream(AppDir$ + "\" + SAVER_NAME$ + "_saver.cfg", False, True)
		If stream = Null Then Return
		
		stream.WriteLine(imageNumber)
		stream.WriteLine(imageScale#)
		
		stream.Close()
	End Method
	
	Method ReadCFG()
		Local stream:TStream
	
		stream = OpenStream(AppDir$ + "\" + SAVER_NAME$ + "_saver.cfg", True, False)
		If stream = Null
			WriteCFG()
			Return
		EndIf
		
		imageNumber = Int(stream.ReadLine())
		imageScale# = Float(stream.ReadLine())
	
		stream.Close()
	End Method
	
	Function ClearEventQueue()
		Repeat ; Until PollEvent() = 0
	End Function
End Type

Global spriteList:TList

Type TSprite
	Field image:TImage
	Field x#, y#, vX#, vY#
	Field gravity#, scaleX#, scaleY#
	Field angle#, rotation#
	Field alpha#, fade#			
																
	Function Create(image:TImage, x#, y#, vX#, vY#, gravity#, scaleX#, scaleY#, angle#, rotation#, alpha#, fade#)
		Local thisSprite:TSprite = New TSprite
		
		thisSprite.image = image
		thisSprite.x# = x#
		thisSprite.y# = y#
		thisSprite.vX# = vX#
		thisSprite.vY# = vY#
 		thisSprite.gravity# = gravity#
		thisSprite.scaleX# = scaleX#
		thisSprite.scaleY# = scaleY#
		thisSprite.angle# = angle#
		thisSprite.rotation# = rotation#
		thisSprite.alpha# = alpha#
		thisSprite.fade# = fade#
		
		If spriteList = Null Then spriteList = New TList
		spriteList.AddLast(thisSprite)
	End Function
	
	Method Update(maxWidth, maxHeight)
		x# :+ vX#
		If x# > maxWidth
			x# = maxWidth
			vX# = -vX#
		EndIf
		If x# < 0
			x# = 0
			vX# = -vX#
		EndIf
		
		y# :+ vY#
		If y# > maxHeight
			y# = maxHeight
			vY# = -vY#
		EndIf
		If y# < 0
			y# = 0
			vY# = -vY#
		EndIf

		vY# :+ gravity#
		angle# :+ rotation#
		alpha# :- fade#
	End Method
	
	Method Draw(imageScale#)
		SetTransform(angle#, scaleX# * imagescale#, scaleY# * imageScale#)
		SetAlpha alpha#
		DrawImage image, x#, y#
	End Method
End Type

'---------------------------------------- MAIN ----------------------------------------

SetGraphicsDriver D3D7Max2DDriver()

SeedRnd MilliSecs()

Local saver:TScreenSaver = New TScreenSaver
saver.Start()

End

'---------------------------------- GLOBAL FUNCTIONS ----------------------------------
						
Function ClearEventQueue()
	Repeat ; Until PollEvent() = 0
End Function
