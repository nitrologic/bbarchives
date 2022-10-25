; ID: 394
; Author: Mustang
; Date: 2002-08-12 08:04:28
; Title: Game Menu Settings module
; Description: Bolt-on easily configurable bitmap-based menu system

Part 1, Game.bb

[code];
; Blitz3D "general purpose Game Settings Menu" 
; by "Mustang" 8/2002 www.caffeineoverdose.net
; 
; DrawAlphaImage-function by simon@acid.co.nz
;
; My GameMenu Code & Gfx > fully Public Domain
; ---> http:\\www.caffeineoverdose.net\Blitz3d
;
;=======================================================================================
; 
;=======================================================================================

AppTitle = "GameMenu 1.0 by Mustang"

.GameMenu ;marker

;Let's go to the GameMenu to set the settings...

Include "GameMenu.bb"

;And let's apply the selected settings to our "Game"

AntiAlias 	= AntiAliasValue$
WBuffer 	= DepthBuffer$
HWMultiTex	= MultiTexValue$

If GameSmode$ = "fullscreen"
	Graphics3D GameScreenX,GameScreenY,GameScreenC,1	;fullscreen
	Else
	Graphics3D GameScreenX,GameScreenY,GameScreenC,2	;windowed
EndIf

;=======================================================================================
; fake GameLoop starts [insert your actual gamecode inside this loop]
;=======================================================================================

cam = CreateCamera()
CameraClsMode cam,0,1
MoveEntity cam,0,0,-5

light = CreateLight()
MoveEntity light,-10,10,-10

cube = CreateCube()
EntityColor cube,51,51,204
EntityAlpha cube,.75

y = -220: LineSpacing = 12: z = 0

Repeat

	Cls
	Z=Z+1
	Color 153,153,255
	Rect 40,y-10,345,250
	Color 0,0,92
	Rect 50,y,325,230
	
	If z=<40 Then
	Color 153,153,255
	Rect 50,y+62,9,10
	EndIf
	If z=80 Then z=0
	
	y = y+1
	If y > GraphicsHeight() Then y=-220
	
	Color 153,153,255
	w=6
	
	Text 50,(LineSpacing*0)+y,"    **** COMMODORE 64 BASIC V2 ****"
	Text 50,(LineSpacing*2)+y," 64K RAM SYSTEM  38911 BASIC BYTES FREE"
	Text 50,(LineSpacing*4)+y,"READY"

		For s.SMenuItem = Each SMenuItem
			
			Text 50,(LineSpacing*w)+y,s\Tag$+":"
			
				Select s\DChoice
				Case 1
					Text 160,(LineSpacing*w)+y,s\Choice1$
				Case 2
					Text 160,(LineSpacing*w)+y,s\Choice2$
				Case 3
					Text 160,(LineSpacing*w)+y,s\Choice3$
				Case 4
					Text 160,(LineSpacing*w)+y,s\Choice4$
				End Select
				
			w = w + 1
			
		Next
		
	Text 50,(LineSpacing*w+1)+y,"PRESS 'ESC' TO GET BACK TO THE NENU"
	
	TurnEntity cube,2,1,.5 
	RenderWorld				
	
		;=====================================================
		;DEBUG INFO
		;=====================================================
		If DEBUGinfo = True
		If GfxModeExists (GameScreenX,GameScreenY,GameScreenC)
			Text 0,0,"OK - can do this:"
		Else
			Text 0,0,"Ooops - no can do:"
		EndIf
		Text 150,0, GameScreenX+"x"+GameScreenY+"x"+GameScreenC
		Text 0,20, GameSmode$
		Text 0,40, MenuSmode$
		Text 0,60, AntiAliasValue$
		Text 0,80, DepthBuffer$
		Text 0,100, MultiTexValue$
		Text 0,120, Difficulty$
		Text 0,140, GFXquality$
		Text 0,160, SoundFXvol$	
		Text 0,180, MusicFXvol$
		EndIf
		;=====================================================
	
	Flip
	
Until KeyHit (1)

;=======================================================================================
; fake GameLoop ends
;=======================================================================================

For m.MMenuItem = Each MMenuItem ;Cleaning...
Delete m
Next
						
For s.SMenuItem = Each SMenuItem ;Cleaning...
Delete s
Next
						
Goto GameMenu ;let's go back to the GameMenu if the user exits the game with ESC

End

Part 2, GameMenu.bb

; Blitz3D "general purpose Game Settings Menu" 
; by "Mustang" 8/2002 www.caffeineoverdose.net
; 
; DrawAlphaImage-function by simon@acid.co.nz
;
; My GameMenu Code & Gfx > fully Public Domain
; ---> http:\\www.caffeineoverdose.net\Blitz3d
;
;=======================================================================================
; GameMenu configuration starts
;=======================================================================================

	.Start ;marker

	AppTitle = "GameMenu 1.0 by Mustang"
	
	Const MMItems = 4 			;how many items on the M(ain)Menu?
	Const SMItems = 12			;how many items on the S(ub)Menu?
	
	Global Menu$ = "main"		;this is used to track the menu we are in
	Global MMactive = 1			;tag to track selected MMenu item
	Global SMactive = 1			;tag to track selected SMenu item
	Const YOffset = 0			;SubMenu choice Y offset
	Const XOffset = 350			;SubMenu choice X offset
	
	;>>make sure these 3 values below are correct!<<
	
	Const MFrHeight = 64 		;MainMenu item frame height
	Const SFrHeight = 32 		;SubMenu item frame height
	Const CFrHeight = 32 		;SubMenuChoice item frame height
	
	Global AntiAliasValue$
	Global DepthBuffer$
	Global MultiTexValue$
	Global Difficulty$
	Global GFXquality$
	Global SoundFXvol$	
	Global MusicFXvol$
	
	Type MMenuItem
		Field Frame				;"serialnumber" of the menuitem
		Field Xpos				;x-position of the menuitem on the screen
		Field Ypos				;y-position of the menuitem on the screen
		Field Link$				;link to what function
	End Type
	
	Type SMenuItem
		Field Frame				;"serialnumber" of the menuitem
		Field DChoice			;DefaultChoice for choices
		Field Xpos				;x-position of the menuitem on the screen
		Field Ypos				;y-position of the menuitem on the screen
		Field Tag$				;
		Field Choice1$			;1st choice
		Field Choice2$			;2nd choice
		Field Choice3$			;3rd choice
		Field Choice4$			;4th choice
		Field MaxChoice 		;how many choices out of four maximum
	End Type

;MMenu item data, properties: framenumber,x-pos,y-pos,sound_active,sound_clicked,link

	Data 0,128,128+92,"game"		
	Data 1,128,192+92,"setup"	
	Data 2,128,256+92,"credits"
	Data 3,128,320+92,"quit"

;SMenu item data, properties: 
;framenumber,default,x-pos,y-pos,txt-tag,option1,option2,option3,option4 (x=no choice)
;"OK" is used as a marker to get back to the mainmenu ----> no user choices

	Data 0,	1,64,128+44,	"resolution",	"640x480",		"800x600",	"1024x768",	"1280x960"	
	Data 1,	1,64,160+44,	"color depth",	"16",			"32",		"x",		"x"
	Data 2,	1,64,192+44,	"game smode",	"fullscreen",	"windowed",	"x",		"x"
	Data 3,	1,64,224+44,	"menu smode",	"fullscreen",	"windowed",	"x",		"x"
	Data 4,	1,64,256+44,	"antialias",	"true",			"false",	"x",		"x"
	Data 5,	1,64,288+44,	"depth buffer",	"false",		"true",		"x",		"x"	
	Data 6,	1,64,320+44,	"HW-multitex",	"true",			"false",	"x",		"x"
	Data 7,	1,64,352+44,	"difficulty",	"easy",			"medium",	"hard",		"ultra"
	Data 8,	1,64,384+44,	"GFX quality",	"low",			"medium",	"high",		"x"
	Data 9,	1,64,416+44,	"soundFX vol",	"0",			"1",		"2",		"3"	
	Data 10,1,64,448+44,	"musicFX vol",	"0",			"1",		"2",		"3"

	Data 11,0,146,545,		"OK",			"x",			"x",		"x",		"x"
	
	
	Global Gdir$ 	= "gfx\" 			;GFX root directory
	Global SFXroot$ = "sfx\" 			;SFX root directory
	
	Global MMenu_1$ = "MMenu_1"			;MainMenu notselected gfx
	Global MMenu_a$ = "MMenu_a"			;MainMenu alpha gfx
	Global MMenu_2$ = "MMenu_2"			;MainMenu selected gfx
		
	Global SMenu_1$ = "SMenu_1" 		;SubMenu notselected gfx
	Global SMenu_a$ = "SMenu_a" 		;SubMenu alpha gfx
	Global SMenu_2$ = "SMenu_2" 		;SubMenu selected gfx
		
	Global SMChoice_1$ = "SMChoice_1" 	;SubMenuChoice notselected gfx
	Global SMChoice_a$ = "SMChoice_a" 	;SubMenuChoice alpha gfx
	Global SMChoice_2$ = "SMChoice_2" 	;SubMenuChoice selcted gfx
	
	Global Logo$   = "Logo"				;logo color, for screenshots
	Global Logo_a$ = "Logo_a"			;logo alpha, for screenshots
	
	Global SaveGFX2Disk = True
	
	Global MenuScreenX = 800	;menu default screen x resolution (you need to have BG-GFX to match)
	Global MenuScreenY = 600	;menu default screen y resolution
	Global MenuScreenC = 32		;menu default screen color depth
	
	Global GameScreenX = 640	;game default screen x resolution
	Global GameScreenY = 480	;game default screen y resolution
	Global GameScreenC = 32		;game default screen color depth
	
	Global MenuSmode$ = "windowed"		;MENU screenmode flag
	Global GameSmode$ = "fullscreen"	;GAME screenmode flag
	
	Const DEBUGinfo = False				;set this true to see some debug info
	
	Global Error$ = "Your PC cannot do the minimum graphics mode required. Get a new PC you sorry sod."

;=======================================================================================
; GameMenu configuration ends
;=======================================================================================

	Restore

	For i = 1 To MMItems				;let's read the menudata
		m.MMenuItem = New MMenuItem
		Read Frame
		m.MMenuItem\Frame = Frame
		Read Xpos
		m.MMenuitem\Xpos = Xpos
		Read Ypos
		m.MMenuitem\Ypos = Ypos
		Read Link$
		m.MMenuItem\Link$ = Link$
	Next
	
	file = ReadFile("game.cfg")
	
	If file <> 0 ;do we have a saved cfg-file that exists?
	
		For i = 1 To SMItems
			s.SMenuItem = New SMenuItem
			s.SMenuItem\Frame 		= ReadLine (file) 
			s.SMenuItem\DChoice 	= ReadLine (file)
			s.SMenuItem\Xpos 		= ReadLine (file)
			s.SMenuItem\Ypos 		= ReadLine (file)
			s.SMenuItem\Tag$ 		= ReadLine (file)
			s.SMenuItem\Choice1$ 	= ReadLine (file)
			s.SMenuItem\Choice2$ 	= ReadLine (file)
			s.SMenuItem\Choice3$ 	= ReadLine (file)
			s.SMenuItem\Choice4$ 	= ReadLine (file)
			s.SMenuItem\MaxChoice 	= ReadLine (file)
			
			Select i
			Case 3
				If s.SMenuItem\DChoice = 1 GameSmode$ = s.SMenuItem\Choice1$
				If s.SMenuItem\DChoice = 2 GameSmode$ = s.SMenuItem\Choice2$
				If s.SMenuItem\DChoice = 3 GameSmode$ = s.SMenuItem\Choice3$
				If s.SMenuItem\DChoice = 4 GameSmode$ = s.SMenuItem\Choice4$
			Case 4
				If s.SMenuItem\DChoice = 1 MenuSmode$ = s.SMenuItem\Choice1$
				If s.SMenuItem\DChoice = 2 MenuSmode$ = s.SMenuItem\Choice2$
				If s.SMenuItem\DChoice = 3 MenuSmode$ = s.SMenuItem\Choice3$
				If s.SMenuItem\DChoice = 4 MenuSmode$ = s.SMenuItem\Choice4$
			Case 5
				If s.SMenuItem\DChoice = 1 AntiAliasValue$ = s.SMenuItem\Choice1$
				If s.SMenuItem\DChoice = 2 AntiAliasValue$ = s.SMenuItem\Choice2$
			Case 6
				If s.SMenuItem\DChoice = 1 DepthBuffer$ = s.SMenuItem\Choice1$
				If s.SMenuItem\DChoice = 2 DepthBuffer$ = s.SMenuItem\Choice2$
			Case 7
				If s.SMenuItem\DChoice = 1 WMultiTexValue$ = s.SMenuItem\Choice1$
				If s.SMenuItem\DChoice = 2 WMultiTexValue$ = s.SMenuItem\Choice2$
			Case 8
				If s.SMenuItem\DChoice = 1 Difficulty$ = s.SMenuItem\Choice1$
				If s.SMenuItem\DChoice = 2 Difficulty$ = s.SMenuItem\Choice2$
				If s.SMenuItem\DChoice = 3 Difficulty$ = s.SMenuItem\Choice3$
				If s.SMenuItem\DChoice = 4 Difficulty$ = s.SMenuItem\Choice4$
			Case 9
				If s.SMenuItem\DChoice = 1 GFXquality$ = s.SMenuItem\Choice1$
				If s.SMenuItem\DChoice = 2 GFXquality$ = s.SMenuItem\Choice2$
				If s.SMenuItem\DChoice = 3 GFXquality$ = s.SMenuItem\Choice3$
				If s.SMenuItem\DChoice = 4 GFXquality$ = s.SMenuItem\Choice4$
			Case 10
				If s.SMenuItem\DChoice = 1 SoundFXvol$ = s.SMenuItem\Choice1$
				If s.SMenuItem\DChoice = 2 SoundFXvol$ = s.SMenuItem\Choice2$
				If s.SMenuItem\DChoice = 3 SoundFXvol$ = s.SMenuItem\Choice3$
				If s.SMenuItem\DChoice = 4 SoundFXvol$ = s.SMenuItem\Choice4$
			Case 11	
				If s.SMenuItem\DChoice = 1 MusicFXvol$ = s.SMenuItem\Choice1$
				If s.SMenuItem\DChoice = 2 MusicFXvol$ = s.SMenuItem\Choice2$
				If s.SMenuItem\DChoice = 3 MusicFXvol$ = s.SMenuItem\Choice3$
				If s.SMenuItem\DChoice = 4 MusicFXvol$ = s.SMenuItem\Choice4$
			End Select
			
		Next 
		CloseFile file
		
	Else ;no saved file found so we are using the default values
	
		For i = 1 To SMItems
			max = 4
			lock = False
			s.SMenuItem = New SMenuItem
			Read Frame
			s.SMenuItem\Frame = Frame
			Read DChoice
			s.SMenuItem\DChoice = DChoice
			Read Xpos
			s.SMenuitem\Xpos = Xpos
			Read Ypos
			s.SMenuitem\Ypos = Ypos
			Read Tag$
			s.SMenuItem\Tag$ = Tag$
			Read Choice1$
			s.SMenuItem\Choice1$ = Choice1$
				If Choice1$ = "x" And lock = False
				max = 0
				lock = True
				EndIf
			Read Choice2$
			s.SMenuItem\Choice2$ = Choice2$
				If Choice2$ = "x" And lock = False 
				max = 1
				lock = True
				EndIf
			Read Choice3$
			s.SMenuItem\Choice3$ = Choice3$
				If Choice3$ = "x" And lock = False 
				max = 2
				lock = True
				EndIf
			Read Choice4$
			s.SMenuItem\Choice4$ = Choice4$
				If Choice4$ = "x" And lock = False
				max = 3
				lock = True
				EndIf
			s.SMenuItem\MaxChoice = max
			
			Select i
			Case 3
				If s.SMenuItem\DChoice = 1 GameSmode$ = s.SMenuItem\Choice1$
				If s.SMenuItem\DChoice = 2 GameSmode$ = s.SMenuItem\Choice2$
				If s.SMenuItem\DChoice = 3 GameSmode$ = s.SMenuItem\Choice3$
				If s.SMenuItem\DChoice = 4 GameSmode$ = s.SMenuItem\Choice4$
			Case 4
				If s.SMenuItem\DChoice = 1 MenuSmode$ = s.SMenuItem\Choice1$
				If s.SMenuItem\DChoice = 2 MenuSmode$ = s.SMenuItem\Choice2$
				If s.SMenuItem\DChoice = 3 MenuSmode$ = s.SMenuItem\Choice3$
				If s.SMenuItem\DChoice = 4 MenuSmode$ = s.SMenuItem\Choice4$
			Case 5
				If s.SMenuItem\DChoice = 1 AntiAliasValue$ = s.SMenuItem\Choice1$
				If s.SMenuItem\DChoice = 2 AntiAliasValue$ = s.SMenuItem\Choice2$
			Case 6
				If s.SMenuItem\DChoice = 1 DepthBuffer$ = s.SMenuItem\Choice1$
				If s.SMenuItem\DChoice = 2 DepthBuffer$ = s.SMenuItem\Choice2$
			Case 7
				If s.SMenuItem\DChoice = 1 MultiTexValue$ = s.SMenuItem\Choice1$
				If s.SMenuItem\DChoice = 2 MultiTexValue$ = s.SMenuItem\Choice2$
			Case 8
				If s.SMenuItem\DChoice = 1 Difficulty$ = s.SMenuItem\Choice1$
				If s.SMenuItem\DChoice = 2 Difficulty$ = s.SMenuItem\Choice2$
				If s.SMenuItem\DChoice = 3 Difficulty$ = s.SMenuItem\Choice3$
				If s.SMenuItem\DChoice = 4 Difficulty$ = s.SMenuItem\Choice4$
			Case 9
				If s.SMenuItem\DChoice = 1 GFXquality$ = s.SMenuItem\Choice1$
				If s.SMenuItem\DChoice = 2 GFXquality$ = s.SMenuItem\Choice2$
				If s.SMenuItem\DChoice = 3 GFXquality$ = s.SMenuItem\Choice3$
				If s.SMenuItem\DChoice = 4 GFXquality$ = s.SMenuItem\Choice4$
			Case 10
				If s.SMenuItem\DChoice = 1 SoundFXvol$ = s.SMenuItem\Choice1$
				If s.SMenuItem\DChoice = 2 SoundFXvol$ = s.SMenuItem\Choice2$
				If s.SMenuItem\DChoice = 3 SoundFXvol$ = s.SMenuItem\Choice3$
				If s.SMenuItem\DChoice = 4 SoundFXvol$ = s.SMenuItem\Choice4$
			Case 11	
				If s.SMenuItem\DChoice = 1 MusicFXvol$ = s.SMenuItem\Choice1$
				If s.SMenuItem\DChoice = 2 MusicFXvol$ = s.SMenuItem\Choice2$
				If s.SMenuItem\DChoice = 3 MusicFXvol$ = s.SMenuItem\Choice3$
				If s.SMenuItem\DChoice = 4 MusicFXvol$ = s.SMenuItem\Choice4$
			End Select
	
		Next
		
	EndIf
	
	SetBuffer BackBuffer()
	
	If MenuSmode$ = "fullscreen"
		mode=GfxModeExists(MenuScreenX,MenuScreenY,MenuScreenC)
		If mode=1 
			Graphics3D MenuScreenX,MenuScreenY,MenuScreenC,1 ;success - fullscreen!
			Else
			RuntimeError Error$ ;no-go... let's do the error dialog...
		End If
		Else
		Graphics3D MenuScreenX,MenuScreenY,MenuScreenC,2 ;windowed
	EndIf
	
	Background1	= LoadImage(Gdir$+"MMenu_bg1.jpg") 				;background for the MainMenu
	Background2	= LoadImage(Gdir$+"MMenu_bg2.jpg") 				;background for the Credits
	
	Logo   = LoadAnimImage (Gdir$+Logo$+".jpg",512,32,0,1)		;screenshot logo color bitmap
	Logo_a = LoadAnimImage (Gdir$+Logo_a$+".jpg",512,32,0,1)	;screenshot logo alpha bitmap
	
	Click1 = LoadSound(SFXroot$+"click1.wav")					;sound1
	Click2 = LoadSound(SFXroot$+"click2.wav")					;sound2

	check = ReadFile(Gdir$+"x_"+MMenu_1$+".bmp")
	
	If check <> 0 ;saved gfx-menu file found... (note that now we don't need any alphamasks!)

		MMenu_1 = LoadAnimImage (Gdir$+"x_"+MMenu_1$+".bmp",256,MFrHeight,0,MMItems)
		MMenu_2 = LoadAnimImage (Gdir$+"x_"+MMenu_2$+".bmp",256,MFrHeight,0,MMItems)
		
		SMenu_1 = LoadAnimImage (Gdir$+"x_"+SMenu_1$+".bmp",512,SFrHeight,0,SMItems)
		SMenu_2 = LoadAnimImage (Gdir$+"x_"+SMenu_2$+".bmp",512,SFrHeight,0,SMItems)
		
		SMChoice_1 = LoadAnimImage (Gdir$+"x_"+SMChoice_1$+".bmp",200,CFrHeight,0,SMItems*4)
		SMChoice_2 = LoadAnimImage (Gdir$+"x_"+SMChoice_2$+".bmp",200,CFrHeight,0,SMItems*4)
	
	Else ;saved gfx-menu file NOT found... so we'll create new set with the help of alphamasks

		MMenu_1 = LoadAnimImage (Gdir$+MMenu_1$+".jpg",256,MFrHeight,0,MMItems)
		MMenu_a = LoadAnimImage (Gdir$+MMenu_a$+".jpg",256,MFrHeight,0,MMItems)
		MMenu_2 = LoadAnimImage (Gdir$+MMenu_2$+".jpg",256,MFrHeight,0,MMItems)
		
		SMenu_1 = LoadAnimImage (Gdir$+SMenu_1$+".jpg",512,SFrHeight,0,SMItems)
		SMenu_a = LoadAnimImage (Gdir$+SMenu_a$+".jpg",512,SFrHeight,0,SMItems)
		SMenu_2 = LoadAnimImage (Gdir$+SMenu_2$+".jpg",512,SFrHeight,0,SMItems)
		
		SMChoice_1 = LoadAnimImage (Gdir$+SMChoice_1$+".jpg",200,CFrHeight,0,SMItems*4)
		SMChoice_a = LoadAnimImage (Gdir$+SMChoice_a$+".jpg",200,CFrHeight,0,SMItems*4)
		SMChoice_2 = LoadAnimImage (Gdir$+SMChoice_2$+".jpg",200,CFrHeight,0,SMItems*4)
	
		Gosub PrepareMenuGFX
	
	EndIf
	
;=======================================================================================
; GameMenu Idle Loop starts
;=======================================================================================

	Repeat
		
		If KeyHit(208) 								;Down arrow
			If Menu$="main"
				If MMactive<MMItems
					PlaySound Click1
				EndIf
					MMactive=MMactive+1
				If MMactive>MMItems
					MMactive=MMItems
				EndIf
			EndIf
			If Menu$="sub"
				If SMactive<SMItems
					PlaySound Click1
				EndIf
					SMactive=SMactive+1
				If SMactive>SMItems
					SMactive=SMItems
				EndIf
			EndIf
		EndIf
		
		If KeyHit(200) 								;Up arrow
			If Menu$="main"
				If MMactive>1
					PlaySound Click1
				EndIf
					MMactive=MMactive-1
				If MMactive<1 
					MMactive=1
				EndIf
			EndIf
			If Menu$="sub"
				If SMactive>1
					PlaySound Click1
				EndIf
					SMactive=SMactive-1
				If SMactive<1 
					SMactive=1
				EndIf
			EndIf
		EndIf
		
		If KeyHit(28) 								;return-key?
			PlaySound Click2
			
			If Menu$ = "main"
					Select MMactive
					Case 1 							;go back to main game part & save user settings
						FreeImage (Background1)
						FreeImage (Background2)
						FreeImage (MMenu_1)
						FreeImage (MMenu_a)
						FreeImage (MMenu_2)
						FreeImage (SMenu_1)
						FreeImage (SMenu_a)
						FreeImage (SMenu_2)
						FreeImage (SMChoice_1)
						FreeImage (SMChoice_a)
						FreeImage (SMChoice_2)
						Goto TheEnd
					Case 2 							;go to SMenu (settings)
						Menu$ = "sub"
						SMactive = 1
						FlushKeys
					Case 3 							;go to SMenu (credits)
						DrawBlock Background2,0,0 	;draw background1
						Flip
						FlushKeys
						WaitKey
						PlaySound Click2
						FlushKeys
					Case 4 							;quit to windows!
						End
					End Select
			EndIf
			
			If Menu$ = "sub" And SMactive = SMItems
					Gosub SaveSettings
					Menu$ = "main"
			EndIf
			
		EndIf
			
		If KeyHit(205) And Menu$ = "sub" 			;right-arrow? Change user settings up
				PlaySound Click2

				If SMactive < SMItems 
					For s.SMenuItem = Each SMenuItem
					If s\DChoice = s\MaxChoice And s\Frame = SMactive -1
						s\DChoice = 0
					EndIf
					If s\DChoice < s\MaxChoice And s\Frame = SMactive - 1
						s\DChoice = s\DChoice + 1
					EndIf
					Next
				EndIf
				
				Gosub Check3D

		EndIf
				
		If KeyHit(203) And Menu$ = "sub" 			;left-arrow? Change user settings down
				PlaySound Click2
				
				If SMactive < SMItems 
					For s.SMenuItem = Each SMenuItem
					If s\DChoice = 1 And s\Frame = SMactive -1
						s\DChoice = s\MaxChoice + 1
					EndIf
					If s\DChoice > 1 And s\Frame = SMactive - 1
						s\DChoice = s\DChoice - 1
					EndIf
					Next
				EndIf
				
				Gosub Check3D
				
		EndIf
		
		Gosub UpdateMenus
		
		;=====================================================
		;DEBUG INFO
		;=====================================================
		If DEBUGinfo = True
		If GfxModeExists (GameScreenX,GameScreenY,GameScreenC)
			Text 0,0,"OK - can do this:"
		Else
			Text 0,0,"Ooops - no can do:"
		EndIf
		Text 150,0, GameScreenX+"x"+GameScreenY+"x"+GameScreenC
		Text 0,20, GameSmode$
		Text 0,40, MenuSmode$
		Text 0,60, AntiAliasValue$
		Text 0,80, DepthBuffer$
		Text 0,100, MultiTexValue$
		Text 0,120, Difficulty$
		Text 0,140, GFXquality$
		Text 0,160, SoundFXvol$	
		Text 0,180, MusicFXvol$
		EndIf
		;=====================================================
		
		If KeyHit(88) 								;F12 saves a screenshot (with "alpha-ed" logo)
			Gosub SaveScreenshot
		EndIf
			
		Flip
		
		If Menu$ = "Exit"
			Exit
		EndIf
		
	Forever

;=======================================================================================
; GameMenu Idle Loop ends
;=======================================================================================

.Check3D

	For z = 1 To 2
	For s.SMenuItem = Each SMenuItem
	For y = 1 To 4
						
	If SMactive = z And s\Frame = 0
		Select s\DChoice
		Case 1
			GameScreenX = s\Choice1$
			GameScreenY = Right$(s\Choice1$,Len(s\Choice1$)-Instr(s\Choice1$,"x"))
		Case 2
			GameScreenX = s\Choice2$
			GameScreenY = Right$(s\Choice2$,Len(s\Choice2$)-Instr(s\Choice2$,"x"))
		Case 3
			GameScreenX = s\Choice3$
			GameScreenY = Right$(s\Choice3$,Len(s\Choice3$)-Instr(s\Choice3$,"x"))
		Case 4
			GameScreenX = s\Choice4$
			GameScreenY = Right$(s\Choice4$,Len(s\Choice4$)-Instr(s\Choice4$,"x"))
		End Select
	EndIf
												
	If SMactive = z And s\Frame = 1
		Select s\DChoice
		Case 1
			GameScreenC = s\Choice1$
		Case 2
			GameScreenC = s\Choice2$
		Case 3
			GameScreenC = s\Choice3$
		Case 4
			GameScreenC = s\Choice4$
		End Select
	EndIf
	
	Next 
	Next
	Next
	
	For z = 3 To 11
	For s.SMenuItem = Each SMenuItem
	For y = 1 To 4
	
	If SMactive = z And s\Frame = 2
		Select s\DChoice
		Case 1
			GameSmode$ = s\Choice1$
		Case 2
			GameSmode$ = s\Choice2$
		End Select
	EndIf
	
	If SMactive = z And s\Frame = 3
		Select s\DChoice
		Case 1
			MenuSmode$ = s\Choice1$
		Case 2
			MenuSmode$ = s\Choice2$
		End Select
	EndIf
	
	If SMactive = z And s\Frame = 4
		Select s\DChoice
		Case 1
			AntiAliasValue$ = s\Choice1$
		Case 2
			AntiAliasValue$ = s\Choice2$
		End Select
	EndIf
	
	If SMactive = z And s\Frame = 5
		Select s\DChoice
		Case 1
			DepthBuffer$ = s\Choice1$
		Case 2
			DepthBuffer$ = s\Choice2$
		End Select
	EndIf
	
	If SMactive = z And s\Frame = 6
		Select s\DChoice
		Case 1
			MultiTexValue$ = s\Choice1$
		Case 2
			MultiTexValue$ = s\Choice2$
		End Select
	EndIf
	
	If SMactive = z And s\Frame = 7
		Select s\DChoice
		Case 1
			Difficulty$ = s\Choice1$
		Case 2
			Difficulty$ = s\Choice2$
		Case 3
			Difficulty$ = s\Choice3$
		Case 4
			Difficulty$ = s\Choice4$
		End Select
	EndIf
	
	If SMactive = z And s\Frame = 8
		Select s\DChoice
		Case 1
			GFXquality$ = s\Choice1$
		Case 2
			GFXquality$ = s\Choice2$
		Case 3
			GFXquality$ = s\Choice3$
		Case 4
			GFXquality$ = s\Choice4$
		End Select
	EndIf
		
	If SMactive = z And s\Frame = 9
		Select s\DChoice
		Case 1
			SoundFXvol$ = s\Choice1$
		Case 2
			SoundFXvol$ = s\Choice2$
		Case 3
			SoundFXvol$ = s\Choice3$
		Case 4
			SoundFXvol$ = s\Choice4$
		End Select
	EndIf
	
	If SMactive = z And s\Frame = 10
		Select s\DChoice
		Case 1
			MusicFXvol$ = s\Choice1$
		Case 2
			MusicFXvol$ = s\Choice2$
		Case 3
			MusicFXvol$ = s\Choice3$
		Case 4
			MusicFXvol$ = s\Choice4$
		End Select
	EndIf
	
	Next 
	Next
	Next
	
Return

.UpdateMenus 											;well-- draw the menus on the screen

	If Menu$ = "main"
	
		DrawBlock Background1,0,0 						;draw background1
		
		For m.MMenuItem = Each MMenuItem
			If m\Frame = MMactive-1
			DrawBlock (MMenu_2,m\Xpos,m\Ypos,m\Frame)
			Else
			DrawBlock (MMenu_1,m\Xpos,m\Ypos,m\Frame)
			EndIf 
		Next
	EndIf
	
	If Menu$ = "sub"
	
		DrawBlock Background1,0,0 						;draw background1
		
		For s.SMenuItem = Each SMenuItem
			If s\Frame = SMactive-1
			DrawBlock (SMenu_2,s\Xpos,s\Ypos,s\Frame)
			Else
			DrawBlock (SMenu_1,s\Xpos,s\Ypos,s\Frame)
			EndIf
			
		If s\DChoice <> 0
		If s\Frame = SMactive-1
		DrawBlock (SMChoice_2,s\Xpos+XOffset,s\Ypos+YOffset,((s\DChoice-1)*SMItems)+s\Frame)
		Else
		DrawBlock (SMChoice_1,s\Xpos+XOffset,s\Ypos+YOffset,((s\DChoice-1)*SMItems)+s\Frame)
		EndIf
		EndIf
		Next

	EndIf

Return

.PrepareMenuGFX ;and... prepare (and save if needed) the menu gfx bits :)

		Text GraphicsWidth()/2,GraphicsHeight()/2,"Please wait - generating menu graphics bitmaps",True,True
		Flip

		;MMenu_nonactive alpha images
		DrawBlock Background1,0,0 ;draw background1
		For m.MMenuItem = Each MMenuItem
			DrawAlphaImage (MMenu_2,m\Xpos,m\Ypos,MMenu_a,m\Frame)
			GrabImage MMenu_2,m\Xpos,m\Ypos,m\Frame
		Next
		;MMenu_active alpha images
		DrawBlock Background1,0,0 ;draw background1
		For m.MMenuItem = Each MMenuItem
			DrawAlphaImage (MMenu_1,m\Xpos,m\Ypos,MMenu_a,m\Frame)
			GrabImage MMenu_1,m\Xpos,m\Ypos,m\Frame
		Next
		;SMenu_nonactive alpha images
		DrawBlock Background1,0,0 ;draw background1
		For s.SMenuItem = Each SMenuItem
			DrawAlphaImage (SMenu_2,s\Xpos,s\Ypos,SMenu_a,s\Frame)
			GrabImage SMenu_2,s\Xpos,s\Ypos,s\Frame
		Next
		;SMenu_active alpha images
		DrawBlock Background1,0,0 ;draw background1
		For s.SMenuItem = Each SMenuItem
			DrawAlphaImage (SMenu_1,s\Xpos,s\Ypos,SMenu_a,s\Frame)
			GrabImage SMenu_1,s\Xpos,s\Ypos,s\Frame
		Next
		;SMenu_choices_nonactive alpha images
		For x = 0 To (SMItems*4)-1 Step 12
		DrawBlock Background1,0,0 ;draw background1
		For s.SMenuItem = Each SMenuItem
			DrawAlphaImage (SMChoice_2,s\Xpos+XOffset,s\Ypos+YOffset,SMChoice_a,s\Frame+x)
			GrabImage SMChoice_2,s\Xpos+XOffset,s\Ypos+YOffset,s\Frame+x
		Next
		Next
		;SMenu_choices_active alpha images
		For x = 0 To (SMItems*4)-1 Step SMItems
		DrawBlock Background1,0,0 ;draw background1
		For s.SMenuItem = Each SMenuItem
			DrawAlphaImage (SMChoice_1,s\Xpos+XOffset,s\Ypos+YOffset,SMChoice_a,s\Frame+x)
			GrabImage SMChoice_1,s\Xpos+XOffset,s\Ypos+YOffset,s\Frame+x
		Next
		Next
		
		If SaveGFX2Disk = True
		
			Frames = MMItems
			Dump1 = CreateImage (ImageWidth(MMenu_2),Frames*MFrHeight)
			SetBuffer ImageBuffer(Dump1)
				For x = 0 To Frames-1
					DrawBlock MMenu_2,0,x*MFrHeight,x
				Next
			ok=SaveImage (Dump1,Gdir$+"x_MMenu_2.bmp")
			
			Dump2 = CreateImage (ImageWidth(MMenu_1),Frames*MFrHeight)
			SetBuffer ImageBuffer(Dump2)
				For x = 0 To Frames-1
					DrawBlock MMenu_1,0,x*MFrHeight,x
				Next
			ok=SaveImage (Dump2,Gdir$+"x_MMenu_1.bmp")
			
			Frames = SMItems
			Dump3 = CreateImage (ImageWidth(SMenu_2),Frames*SFrHeight)
			SetBuffer ImageBuffer(Dump3)
				For x = 0 To Frames-1
					DrawBlock SMenu_2,0,x*SFrHeight,x
				Next
			ok=SaveImage (Dump3,Gdir$+"x_SMenu_2.bmp")
			
			Dump4 = CreateImage (ImageWidth(SMenu_1),Frames*SFrHeight)
			SetBuffer ImageBuffer(Dump4)
				For x = 0 To Frames-1
					DrawBlock SMenu_1,0,x*SFrHeight,x
				Next
			ok=SaveImage (Dump4,Gdir$+"x_SMenu_1.bmp")
		
			Frames = MMItems*SMItems
			Dump5 = CreateImage (ImageWidth(SMChoice_2),Frames*CFrHeight)
			SetBuffer ImageBuffer(Dump5)
				For x = 0 To Frames-1
					DrawBlock SMChoice_2,0,x*CFrHeight,x
				Next
			ok=SaveImage (Dump5,Gdir$+"x_SMChoice_2.bmp")
				
			Dump6 = CreateImage (ImageWidth(SMChoice_1),Frames*CFrHeight)
			SetBuffer ImageBuffer(Dump6)
				For x = 0 To Frames-1
					DrawBlock SMChoice_1,0,x*CFrHeight,x
				Next
			ok=SaveImage (Dump6,Gdir$+"x_SMChoice_1.bmp")
		
		EndIf
		
		For m.MMenuItem = Each MMenuItem ;Cleaning...
		Delete m
		Next
								
		For s.SMenuItem = Each SMenuItem ;Cleaning...
		Delete s
		Next

		Goto Start ;after creating the gfx-bits let's start again this menu, shall we?
		
Return

.SaveScreenshot ;save a screenshot with a logo (with alpha-mask)

	x = ImageWidth(Logo)
	y = ImageHeight(Logo)
	xoff = 5 ;define the logo placement offset values from right/bottom corner
	yoff = 5
	DrawAlphaImage (Logo,(GraphicsWidth()-(x+xoff)),(GraphicsHeight()-(y+yoff)),Logo_a,0)
	iFileNumber% = 0 
    Repeat 
        iFileNumber = iFileNumber+1 
        sFileName$ = "Shot" + String$("0",3-Len(Str$(iFileNumber))) + iFileNumber + ".bmp" 
    Until Not(FileType(sFileName))
    SaveBuffer BackBuffer(),sFileName

Return

.SaveSettings ;save all settings to the harddisk

	file = WriteFile ("game.cfg")
	For s.SMenuItem = Each SMenuItem
	WriteLine ( file,s\Frame ) 
	WriteLine ( file,s\DChoice )
	WriteLine ( file,s\Xpos )
	WriteLine ( file,s\Ypos )
	WriteLine ( file,s\Tag$ )
	WriteLine ( file,s\Choice1$ )
	WriteLine ( file,s\Choice2$ )
	WriteLine ( file,s\Choice3$ )
	WriteLine ( file,s\Choice4$ )
	WriteLine ( file,s\MaxChoice )
	Next 
	CloseFile file
	
Return

;
; drawalphaimage.bb 
; by simon@acid.co.nz 
;

Function DrawAlphaImage(image,px,py,alphaimage=0,frame) ;do the images with alpha-mask

	If alphaimage=0 alphaimage=image 
	; size 
	w=ImageWidth(image) 
	h=ImageHeight(image) 
	gw=GraphicsWidth() 
	gh=GraphicsHeight() 
	; clip 
	x0=px:y0=py 
	If x0<0 w=w+x0 x0=0 
	If y0<0 h=h+y0 y0=0 
	If x0+w>gw w=gw-x0 
	If y0+h>gh h=gh-y0 
	If w<=0 Or h<=0 Return 
	x1=x0+w-1 
	y1=y0+h-1 
	; lock buffers 
	ibuffer=ImageBuffer(image,frame) 
	abuffer=ImageBuffer(alphaimage,frame) 
	gbuffer=GraphicsBuffer() 
	LockBuffer ibuffer 
	LockBuffer abuffer 
	LockBuffer gbuffer 
	; draw 
	For y=y0 To y1 
	For x=x0 To x1 
	alpha=ReadPixelFast(x-px,y-py,abuffer) And 255 
	If alpha>1 
	rgb0=ReadPixelFast(x-px,y-py,ibuffer) 
	rgb1=ReadPixelFast(x,y,gbuffer) 
	bit=$80 
	rgb=0 
	While bit>1 
	rgb0=(rgb0 Shr 1) And $7f7f7f 
	rgb1=(rgb1 Shr 1) And $7f7f7f 
	If (alpha And bit) rgb=rgb+rgb0 Else rgb=rgb+rgb1 
	bit=bit Shr 1 
	Wend 
	WritePixelFast x,y,rgb 
	EndIf 
	Next 
	Next 
	; unlock 
	UnlockBuffer gbuffer 
	UnlockBuffer ibuffer 
	UnlockBuffer abuffer
	
End Function

.TheEnd ;marker[/code]
