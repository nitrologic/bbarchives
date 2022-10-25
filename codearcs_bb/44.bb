; ID: 44
; Author: Neo Genesis10
; Date: 2001-09-18 05:49:44
; Title: Auto-set graphics mode
; Description: Automatically sets up a comaptible graphics setting and saves it.

Function GraphicsMode()

ini = ReadFile("setup.ini")		; reads ini file

If ini <> 0				; Check if ini file exists
	Repeat
		a$ = ReadLine(ini)	; read lines
	Until a$ = "[graphics]"		; ...until it finds the one we need
	a$ = ReadLine(ini)		; read the following line
	For x = 1 To Len(a$)		; cycle through till it finds a comma
		l$ = Mid$(a$,x,1)
		If l$ = ","
			num = x
			Exit
		EndIf
	Next
	depth = ReadLine(ini)
	If depth = 0 depth = 16		; if omitted, default to 16-bit for compatibility
	scr_w = Left(a$, num-1)		; grab the screen width
	scr_h = Right(a$, (Len(a$) - num) )
	If scr_h < 480 Or scr_w < 640
		scr_h = 480
		scr_w = 640
	EndIf
	If GfxModeExists(scr_w,scr_h,depth) = False	; if the graphics mode doesnt exist
		scr_w = GfxModeWidth(1)			; set it up in basic mode
		scr_h = GfxModeHeight(1)
		depth = 16
	EndIf
	CloseFile(ini)
Else
	scr_w = 0
	scr_h = 0
	Cls
	Flip
	Cls

	Print "Set Graphics Mode"
	For i = 1 To CountGfxModes()
		Print i+") "+GfxModeWidth(i)+" x "+GfxModeHeight(i)+" x "+GfxModeDepth(i)
	Next
	
	.entermode
	mode = Input(">")
	If mode < 0 Or Mode > CountGfxModes() Goto entermode
	scr_w = GfxModeWidth( mode )
	scr_h = GfxModeHeight( mode )
	depth = GfxModeDepth( mode )
		
	ini = WriteFile("setup.ini")
	WriteLine ini,"[graphics]"
	WriteLine ini,scr_w+","+scr_h
	CloseFile(ini)
EndIf

Graphics scr_w,scr_h,depth
SetBuffer BackBuffer()
Return

End Function
