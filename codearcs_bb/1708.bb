; ID: 1708
; Author: Nicstt
; Date: 2006-05-12 04:59:20
; Title: Option Window - Messages and request user response
; Description: function creates four standard message windows - with or without buttons

Function AdviceWindowOption(number, gfxGeneric, custom_width, custom_hight, fntGeneric, btnTextColor, message$, boolean)
	; ********************************************************************************************************************************
	; AdviceWindowOption(number, gfxGeneric, custom_width, custom_hight, fntGeneric, message$, boolean)
	; number			0, 1, 2 or 3 - determines how many buttons each window will have
	;					a number out of range defaults to 0 with a message
	; gfxGeneric		set it to 0 will let function draw window and buttons when required using defaults
	; custom_width		0 when happy with defaults or if passing a predrawn graphic - default is 300 or when window has 3 buttons 450
	; custom_hight		0 when happy with defaults or if passing a predrawn graphic - default is 120
	;					custom size won't allow a control larger than screen size
	; fntGeneric		0 when happy with defaults - which is unlikely
	; btnTextColor		black or white - does not affect fntGeneric
	; message$			text to be displayed - will be split up into three lines by function
	;					- pass empty if predrawn window has the message displayed
	;					- if string is too long outputs a default message.
	; boolean			set to false unless predrawn window (or image/texture) needs buttons adding
	;					- if image is smaller than default size it will be resized - no warning is given
	;					- however, a copy of the image is used preserving the original
	; ********************************************************************************************************************************
	; Returns			reply = 1, 2, 3 or 4 - | 1 = ok button | 2 = yes button | 3 = no button | 4 = cancel button
	;					- also returns Null when no button is displayed if a Null return is required
	; ********************************************************************************************************************************
	; Button Size 		Default is 127 x 32 - no option to change the size
	; ********************************************************************************************************************************
	; example code of use	

	;answer = AdviceWindowOption(1, 0, 0, 0, 0, "Test", False) ;  one button 'ok'
	;Select answer
	;	Case 1
			; code here would be for what to do with answer (return)
	;	Case 2
			; code here would be for what to do with answer (return)
	;	Case 3
			; code here would be for what to do with answer (return)
	;	Case 4
			; code here would be for what to do with answer (return)
	;End Select
	
	; ********************************************************************************************************************************
	; text displayed on drawn buttons
	btntextOk$ = "OK" : btntextYes$ = "Yes" : btntextNo$ = "No" : btntextCancel$ = "Cancel"

	imagefree = False : fontfree = False : fontfreelarge = False
	
	gfxFullScreen = CreateImage(GraphicsWidth(), GraphicsHeight())

	; draws button to use to show button 'activated' 127 x 32
	gfxActiveBtn = CreateImage (127, 32)
	SetBuffer (ImageBuffer(gfxActiveBtn))
	ClsColor 0, 0, 0 : Cls ; cls with color that will be masked
	x1 = 0 : y1 = 0 : x2 = 126 : y2 = 31 ; top left and bottom right coords
	Color 32, 32, 32
	Line x1, y1, x2, y1 : Line x1, y1 + 1, x2 - 1, y1 + 1
	Line x1, y1 + 2, x1, y2 : Line x1 + 1, y1 + 2, x1 + 1, y2 - 1
	Color 255, 255, 255
	Line x1 + 2, y2 - 1, x2, y2 - 1 : Line x1 + 1, y2, x2, y2
	Line x2 - 1, y1 + 2, x2 - 1, y2 - 2 : Line x2, y1 + 1, x2, y2 - 2
	SetBuffer (BackBuffer())
	MaskImage gfxActiveBtn, 0, 0, 0
	If fntGeneric = 0
		fntGeneric = LoadFont("Ariel",16, False, False, False) ; default font
		fontfree = True 
	EndIf
		; makes a copy of imported image as any changes will affect original
	If gfxGeneric <> 0
		gfxTemp = gfxGeneric
		gfxGeneric = CopyImage(gfxTemp)
		gfxTemp = 0
	EndIf
	If gfxGeneric = 0 Or (gfxGeneric <> 0 And boolean = True) ; no graphics passed, or graphic needs buttons adding
		If boolean = True	; need to test passing a gfx for window that is being used as background
			custom_width = ImageWidth(gfxGeneric)
			custom_hight = ImageHeight(gfxGeneric)
			resize = False
			If ImageWidth(gfxGeneric) < 300 And number <> 3 
				custom_width = 300 : resize = True
			ElseIf ImageWidth(gfxGeneric) < 450 And number = 3
				custom_width = 450 : resize = True
			EndIf
			If ImageHeight(gfxGeneric) < 120
				custom_hight = 120 : resize = True
			EndIf
			If resize = True Then ResizeImage gfxGeneric, custom_width, custom_hight
		EndIf
		If custom_width < 300 And number <> 3 Then custom_width = 300
		If custom_width < 450 And number = 3 Then custom_width = 450
		If custom_hight < 120 Then custom_hight = 120
		If custom_width > GraphicsWidth() Then custom_width = GraphicsWidth() : message$ = "custom_width was greater than max allowed!"
		If custom_hight > GraphicsHeight() Then custom_hight = GraphicsHeight() : message$ = "custom_height was greater than max allowed!"
		If boolean = False
			gfxGeneric = CreateImage (custom_width, custom_hight); generic variable to hold gfx data of whatever is passed - 0 will make the default gfx
			imagefree = True
		EndIf
		fntStandardLarge = LoadFont("Comic Sans MS",28, False, False, False) ; used to display text on buttons
		fontfreelarge = True
		SetBuffer (ImageBuffer(gfxGeneric))
		
		If boolean = False Then ClsColor 180, 180, 210 : Cls
		; window without buttons - draws border on predrawn background or image after cls
		Color 255, 255, 255
		Line 0, 0, custom_width - 1, 0
		Line 0, 1, custom_width - 1, 1
		Line 0, 2, 0, custom_hight - 1
		Line 1, 2, 1, custom_hight - 1
		Color 32, 32, 32
		Line 2, 2, custom_width - 1, 2
		Line 2, 3, custom_width - 1, 3
		Line 2, custom_hight - 1, custom_width - 1, custom_hight - 1
		Line 2, custom_hight - 2, custom_width - 1, custom_hight - 2
		Line 2, 4, 2, custom_hight - 1
		Line 3, 4, 3, custom_hight - 1
		Line custom_width - 1, 4, custom_width - 1, custom_hight - 1
		Line custom_width - 2, 4, custom_width - 2, custom_hight - 1
		; draws what buttons are required with text of specified colour - 0 black, 1 white
		Select number
			Case 1 ; 1 button - ok
				x1_1 = custom_width / 2 - 65 : y1_1 = custom_hight - 40
				x1_2 = x1_1 + ImageWidth(gfxActiveBtn) - 1 : y1_2 = y1_1 + ImageHeight(gfxActiveBtn) - 1
				Color 255, 255, 255
				Line x1_1, y1_1, x1_2, y1_1 : Line x1_1, y1_1 + 1, x1_2 - 1, y1_1 + 1
				Line x1_1, y1_1 + 2, x1_1, y1_2 : Line x1_1 + 1, y1_1 + 2, x1_1 + 1, y1_2 - 1
				Color 32, 32, 32
				Line x1_1 + 2, y1_2 - 1, x1_2, y1_2 - 1 : Line x1_1 + 1, y1_2, x1_2, y1_2
				Line x1_2 - 1, y1_1 + 2, x1_2 - 1, y1_2 - 2 : Line x1_2, y1_1 + 1, x1_2, y1_2 - 2
				SetFont fntStandardLarge 
				If btnTextColor = 0 Then Color 32, 32, 32 Else Color 255, 255, 255
				Text (custom_width / 2) - (StringWidth (btntextOk$) / 2), y1_1, btntextOk$
			Case 2	; 2 buttons - yes, no
				x1_1 = 9 : y1_1 = custom_hight - 40
				x1_2 = x1_1 + ImageWidth(gfxActiveBtn) - 1 : y1_2 = y1_1 + ImageHeight(gfxActiveBtn) - 1
				Color 255, 255, 255
				Line x1_1, y1_1, x1_2, y1_1 : Line x1_1, y1_1 + 1, x1_2 - 1, y1_1 + 1
				Line x1_1, y1_1 + 2, x1_1, y1_2 : Line x1_1 + 1, y1_1 + 2, x1_1 + 1, y1_2 - 1
				Color 32, 32, 32
				Line x1_1 + 2, y1_2 - 1, x1_2, y1_2 - 1 : Line x1_1 + 1, y1_2, x1_2, y1_2
				Line x1_2 - 1, y1_1 + 2, x1_2 - 1, y1_2 - 2 : Line x1_2, y1_1 + 1, x1_2, y1_2 - 2
				SetFont fntStandardLarge 
				If btnTextColor = 0 Then Color 32, 32, 32 Else Color 255, 255, 255
				Text x1_1 + (ImageWidth(gfxActiveBtn) / 2) - (StringWidth (btntextYes$) / 2), y1_1, btntextYes$
				x2_2 = custom_width - 9 : y2_1 = custom_hight - 40
				x2_1 = x2_2 - ImageWidth(gfxActiveBtn) + 1 : y2_2 = y2_1 + ImageHeight(gfxActiveBtn) - 1
				Color 255, 255, 255
				Line x2_1, y2_1, x2_2, y2_1 : Line x2_1, y2_1 + 1, x2_2 - 1, y2_1 + 1
				Line x2_1, y2_1 + 2, x2_1, y2_2 : Line x2_1 + 1, y2_1 + 2, x2_1 + 1, y2_2 - 1
				Color 32, 32, 32
				Line x2_1 + 2, y2_2 - 1, x2_2, y2_2 - 1 : Line x2_1 + 1, y2_2, x2_2, y2_2
				Line x2_2 - 1, y2_1 + 2, x2_2 - 1, y2_2 - 2 : Line x2_2, y2_1 + 1, x2_2, y2_2 - 2
				SetFont fntStandardLarge 
				If btnTextColor = 0 Then Color 32, 32, 32 Else Color 255, 255, 255
				Text x2_1 + (ImageWidth(gfxActiveBtn) / 2) - (StringWidth (btntextNo$) / 2), y2_1, btntextNo$
			Case 3 ; 3 buttons - yes, no, cancel
				x1_1 = 9 : y1_1 = custom_hight - 40
				x1_2 = x1_1 + ImageWidth(gfxActiveBtn) - 1 : y1_2 = y1_1 + ImageHeight(gfxActiveBtn) - 1
				Color 255, 255, 255
				Line x1_1, y1_1, x1_2, y1_1 : Line x1_1, y1_1 + 1, x1_2 - 1, y1_1 + 1
				Line x1_1, y1_1 + 2, x1_1, y1_2 : Line x1_1 + 1, y1_1 + 2, x1_1 + 1, y1_2 - 1
				Color 32, 32, 32
				Line x1_1 + 2, y1_2 - 1, x1_2, y1_2 - 1 : Line x1_1 + 1, y1_2, x1_2, y1_2
				Line x1_2 - 1, y1_1 + 2, x1_2 - 1, y1_2 - 2 : Line x1_2, y1_1 + 1, x1_2, y1_2 - 2
				SetFont fntStandardLarge 
				If btnTextColor = 0 Then Color 32, 32, 32 Else Color 255, 255, 255
				Text x1_1 + (ImageWidth(gfxActiveBtn) / 2) - (StringWidth (btntextYes$) / 2), y1_1, btntextYes$
				x2_1 = custom_width / 2 - 63 : y2_1 = custom_hight - 40
				x2_2 = x2_1 + ImageWidth(gfxActiveBtn) - 1 : y2_2 = y2_1 + ImageHeight(gfxActiveBtn) - 1
				Color 255, 255, 255
				Line x2_1, y2_1, x2_2, y2_1 : Line x2_1, y2_1 + 1, x2_2 - 1, y2_1 + 1
				Line x2_1, y2_1 + 2, x2_1, y2_2 : Line x2_1 + 1, y2_1 + 2, x2_1 + 1, y2_2 - 1
				Color 32, 32, 32
				Line x2_1 + 2, y2_2 - 1, x2_2, y2_2 - 1 : Line x2_1 + 1, y2_2, x2_2, y2_2
				Line x2_2 - 1, y2_1 + 2, x2_2 - 1, y2_2 - 2 : Line x2_2, y2_1 + 1, x2_2, y2_2 - 2
				SetFont fntStandardLarge 
				If btnTextColor = 0 Then Color 32, 32, 32 Else Color 255, 255, 255
				Text x2_1 + (ImageWidth(gfxActiveBtn) / 2) - (StringWidth (btntextNo$) / 2), y2_1, btntextNo$
				x3_2 = custom_width - 9 : y3_1 = custom_hight - 40
				x3_1 = x3_2 - ImageWidth(gfxActiveBtn) + 1 : y3_2 = y3_1 + ImageHeight(gfxActiveBtn) - 1
				Color 255, 255, 255
				Line x3_1, y3_1, x3_2, y3_1 : Line x3_1, y3_1 + 1, x3_2 - 1, y3_1 + 1
				Line x3_1, y3_1 + 2, x3_1, y3_2 : Line x3_1 + 1, y3_1 + 2, x3_1 + 1, y3_2 - 1
				Color 32, 32, 32
				Line x3_1 + 2, y3_2 - 1, x3_2, y3_2 - 1 : Line x3_1 + 1, y3_2, x3_2, y3_2
				Line x3_2 - 1, y3_1 + 2, x3_2 - 1, y3_2 - 2 : Line x3_2, y3_1 + 1, x3_2, y3_2 - 2
				SetFont fntStandardLarge 
				If btnTextColor = 0 Then Color 32, 32, 32 Else Color 255, 255, 255
				Text x3_1 + (ImageWidth(gfxActiveBtn) / 2) - (StringWidth (btntextCancel$) / 2), y3_1, btntextCancel$
		End Select
		SetBuffer (BackBuffer())
	ElseIf gfxGeneric <> 0 And boolean = False ; passing in a ready drawn graphic with standard buttons
		custom_width = ImageWidth(gfxGeneric)
		custom_hight = ImageHeight(gfxGeneric)
		Select number	; (standard graphic without buttons dealt with in first part of statement)
			Case 1 ; coords for one button - ok
				x1_1 = custom_width / 2 - 65 : y1_1 = custom_hight - 40
				x1_2 = x1_1 + ImageWidth(gfxActiveBtn) - 1 : y1_2 = y1_1 + ImageHeight(gfxActiveBtn) - 1
			Case 2 ; coords for two buttons 'YES' and 'NO'
				x1_1 = 9 : y1_1 = custom_hight - 40
				x1_2 = x1_1 + ImageWidth(gfxActiveBtn) - 1 : y1_2 = y1_1 + ImageHeight(gfxActiveBtn) - 1
				x2_2 = custom_width - 9 : y2_1 = custom_hight - 40
				x2_1 = x2_2 - ImageWidth(gfxActiveBtn) + 1 : y2_2 = y2_1 + ImageHeight(gfxActiveBtn) - 1
			Case 3 ; coords for three buttons 'YES', 'NO' and 'CANCEL'
				x1_1 = 9 : y1_1 = custom_hight - 40
				x1_2 = x1_1 + ImageWidth(gfxActiveBtn) - 1 : y1_2 = y1_1 + ImageHeight(gfxActiveBtn) - 1
				x2_1 = custom_width / 2 - 63 : y2_1 = custom_hight - 40
				x2_2 = x2_1 + ImageWidth(gfxActiveBtn) - 1 : y2_2 = y2_1 + ImageHeight(gfxActiveBtn) - 1
				x3_2 = custom_width - 9 : y3_1 = custom_hight - 40
				x3_1 = x3_2 - ImageWidth(gfxActiveBtn) + 1 : y3_2 = y3_1 + ImageHeight(gfxActiveBtn) - 1
		End Select
	EndIf
	CopyRect 0, 0, GraphicsWidth(), GraphicsHeight(), 0, 0, FrontBuffer(), ImageBuffer(gfxFullScreen) 
	SetFont fntGeneric	
	If number < 0 And number > 3
		number = 0 : message$ = "'number' passed was outside range - allowed are 0 to 3."
	EndIf
	dly = 5000 ; delay for case 0
	x = ((GraphicsWidth() / 2) - (ImageWidth(gfxGeneric)) / 2)
	xs = GraphicsWidth() / 2
	ys = GraphicsHeight() / 2
	y = ((GraphicsHeight() / 2) - (ImageHeight(gfxGeneric)) / 2)
	sw = StringWidth(message$)
	sh = StringHeight(message$)
	iw = ImageWidth(gfxGeneric) - 6 ; the 6 reduction allows for border
	ih = ImageHeight(gfxGeneric)
	DrawImage gfxFullScreen, 0, 0
	DrawImage gfxGeneric, x, y 
	If sw <= iw
		Color 255,255,0
		Text xs, ys - sh, message$, True, False
		dly = 3000
	Else
		If sw / 2 < iw - 15
			lenth_ok = False
			For a = 1 To Len(message$)
				If Mid$(message$, a, 1) = " " Or Mid$(message$, a, 1) = "," Or Mid$(message$, a, 1) = "." Or Mid$(message$, a, 1) = ":" Or Mid$(message$, a, 1) = ":" Or Mid$(message$, a, 1) = "!"
					message1$ = Left$(message$, a)
					message2$ = Right$(message$, Len(message$) - a)
					If StringWidth(message1$) <= iw And StringWidth(message2$) <= iw
						If Mid$(message$, a, 1) = " "
							message1$ = Left$(message$, a - 1)
						EndIf
						lenth_ok = True
						Exit
					EndIf
				EndIf
			Next
			Color 255,255,0 
			If number > 0
				sw = StringWidth (message1$)
				Text x + ((iw + 6 - sw) / 2), y + 20, message1$
				sw = StringWidth (message2$)
				Text x + ((iw + 6 - sw) / 2), y + 50, message2$
			Else
				sw = StringWidth (message1$)
				Text x + ((iw + 6 - sw) / 2), y + 30, message1$
				sw = StringWidth (message2$)
				Text x + ((iw + 6 - sw) / 2), y + 60, message2$
			EndIf
		ElseIf sw / 3 < iw
			lenth_ok = False : adjust = 25
			Repeat
				For a = adjust To Len(message$)
					If Mid$(message$, a, 1) = " " Or Mid$(message$, a, 1) = "," Or Mid$(message$, a, 1) = "." Or Mid$(message$, a, 1) = ":" Or Mid$(message$, a, 1) = ":" Or Mid$(message$, a, 1) = "!"
						message1$ = Left$(message$, a)
						messagetemp$ = Right$(message$, Len(message$) - a)
						If Mid$(message$, a, 1) = " "
							message1$ = Left$(message$, a - 1)
						EndIf
						If StringWidth(message1$) <= iw 
							Exit
						EndIf
					EndIf	
				Next		
				For a = 1 To Len(messagetemp$)
					If Mid$(messagetemp$, a, 1) = " " Or Mid$(messagetemp$, a, 1) = "," Or Mid$(messagetemp$, a, 1) = "." Or Mid$(messagetemp$, a, 1) = ":" Or Mid$(messagetemp$, a, 1) = ":" Or Mid$(messagetemp$, a, 1) = "!"
						message2$ = Left$(messagetemp$, a)
						message3$ = Right$(messagetemp$, Len(messagetemp$) - a)
						If StringWidth(message1$) <= iw And StringWidth(message2$) <= iw And StringWidth(message3$) <= iw
							If Mid$(message2$, a, 1) = " "
								message2$ = Left$(messagetemp$, a - 1)
							EndIf
							lenth_ok = True
							Exit
						EndIf
					EndIf
				Next
				adjust = adjust + 1
			Until lenth_ok = True Or adjust > Len(message$)
			Color 255,255,0 
			If adjust > Len(message$)
				sw = StringWidth ("Message Failed!")
				Text x + ((iw + 6 - sw) / 2), y + 80, "Message Failed!"
			Else
				If number > 0
					sw = StringWidth (message1$)
					Text x + ((iw + 6 - sw) / 2), y + 8, message1$
					sw = StringWidth (message2$)
					Text x + ((iw + 6 - sw) / 2), y + 30, message2$
					sw = StringWidth (message3$)
					Text x + ((iw + 6 - sw) / 2), y + 52, message3$
				Else
					sw = StringWidth (message1$)
					Text x + ((iw + 6 - sw) / 2), y + 20, message1$
					sw = StringWidth (message2$)
					Text x + ((iw + 6 - sw) / 2), y + 50, message2$
					sw = StringWidth (message3$)
					Text x + ((iw + 6 - sw) / 2), y + 80, message3$
				EndIf
			EndIf
		Else
			sw = StringWidth ("Message too Long")
			Text x + ((iw + 6 - sw) / 2), y + 40, "Message too Long"
		EndIf
	EndIf
	reply = 0
	Select number
		Case 0	; requires text to display message - no buttons
			Flip 0: Delay dly
		Case 1	; requires text to display message - one button 'OK'
			CopyRect 0, 0, GraphicsWidth(), GraphicsHeight(), 0, 0, BackBuffer(), ImageBuffer(gfxFullScreen) 
			x1_1 = x1_1 + x : y1_1 = y1_1 + y : x1_2 = x1_2 + x - 1 : y1_2 = y1_2 + y - 1
			Repeat
				ML = MouseHit(1)
				If ML = True And MouseX() > x1_1 And MouseX() < x1_2 And MouseY() > y1_1 And MouseY() < y1_2
					DrawImage gfxFullScreen, 0, 0
					DrawImage gfxActiveBtn, x1_1, y1_1
					Flip 0 : Delay 200
					DrawImage gfxFullScreen, 0, 0
					Flip 0 : Delay 100
					reply = 1 ; 1 = ok
				EndIf
				DrawImage gfxFullScreen, 0, 0
				Flip 0
			Until reply <> 0
		Case 2	; requires text to display message - two buttons 'YES' and 'NO'
			CopyRect 0, 0, GraphicsWidth(), GraphicsHeight(), 0, 0, BackBuffer(), ImageBuffer(gfxFullScreen) 
			x1_1 = x1_1 + x : y1_1 = y1_1 + y : x1_2 = x1_2 + x : y1_2 = y1_2 + y
			x2_1 = x2_1 + x : y2_1 = y2_1 + y : x2_2 = x2_2 + x : y2_2 = y2_2 + y
			Repeat
				ML = MouseHit(1)
				If ML = True And MouseX() > x1_1 And MouseX() < x1_2 And MouseY() > y1_1 And MouseY() < y1_2
					DrawImage gfxFullScreen, 0, 0
					DrawImage gfxActiveBtn, x1_1, y1_1
					Flip 0 : Delay 200
					DrawImage gfxFullScreen, 0, 0
					Flip 0 : Delay 100
					reply = 2 : ML = 0 ; 2 = yes
				EndIf
				If ML = True And MouseX() > x2_1 And MouseX() < x2_2 And MouseY() > y2_1 And MouseY() < y2_2
					DrawImage gfxFullScreen, 0, 0
					DrawImage gfxActiveBtn, x2_1, y2_1
					Flip 0 : Delay 200
					DrawImage gfxFullScreen, 0, 0
					Flip 0 : Delay 100
					reply = 3 : ML = 0 ; 3 = no
				EndIf
				DrawImage gfxFullScreen, 0, 0
				Flip 0
			Until reply <> 0
		Case 3	; requires text to display message - three buttons 'YES', 'NO' and 'CANCEL'
			CopyRect 0, 0, GraphicsWidth(), GraphicsHeight(), 0, 0, BackBuffer(), ImageBuffer(gfxFullScreen) 
			x1_1 = x1_1 + x : y1_1 = y1_1 + y : x1_2 = x1_2 + x : y1_2 = y1_2 + y
			x2_1 = x2_1 + x : y2_1 = y2_1 + y : x2_2 = x2_2 + x : y2_2 = y2_2 + y
			x3_1 = x3_1 + x : y3_1 = y3_1 + y : x3_2 = x3_2 + x : y3_2 = y3_2 + y
			Repeat
				ML = MouseHit(1)
				If ML = True And MouseX() > x1_1 And MouseX() < x1_2 And MouseY() > y1_1 And MouseY() < y1_2
					DrawImage gfxFullScreen, 0, 0
					DrawImage gfxActiveBtn, x1_1, y1_1
					Flip 0 : Delay 200
					DrawImage gfxFullScreen, 0, 0
					Flip 0 : Delay 100
					reply = 2 : ML = 0 ; 2 = yes
				EndIf
				If ML = True And MouseX() > x2_1 And MouseX() < x2_2 And MouseY() > y2_1 And MouseY() < y2_2
					DrawImage gfxFullScreen, 0, 0
					DrawImage gfxActiveBtn, x2_1, y2_1
					Flip 0 : Delay 200
					DrawImage gfxFullScreen, 0, 0
					Flip 0 : Delay 100
					reply = 3 : ML = 0 ; 3 = no
				EndIf
				If ML = True And MouseX() > x3_1 And MouseX() < x3_2 And MouseY() > y3_1 And MouseY() < y3_2
					DrawImage gfxFullScreen, 0, 0
					DrawImage gfxActiveBtn, x3_1, y3_1
					Flip 0 : Delay 200
					DrawImage gfxFullScreen, 0, 0
					Flip 0 : Delay 100
					reply = 4 : ML = 0 ; 4 = cancel
				EndIf
				DrawImage gfxFullScreen, 0, 0
				Flip 0
			Until reply <> 0
		Default
			Notify "number other than specified in function AdviceWindowOption()!"
	End Select
	FreeImage gfxGeneric
	If fontfree = True Then FreeFont fntGeneric
	If fontfreelarge = True FreeFont fntStandardLarge
	FreeImage gfxActiveBtn
	FreeImage gfxFullScreen
	Return reply
End Function

AppTitle "test of Options Function V0.1"
SeedRnd MilliSecs()

Const C_screenWidth		= 800		; width of game screen 
Const C_screenHeight	= 600		; height of game screen 
Const C_screenDepth		= 32		; depth of game screen
Const C_screenType		= 2			; type is windowed or full screen - 1 = full screen, 2 = windowed

Global fntStandard = LoadFont("Comic Sans MS",24, False, False, False)
Global fntSmallB = LoadFont("Comic Sans MS",48, True, False, False)
Global gfxTest = LoadImage ("Advice window test image.bmp") ; change this to test own graphics

Graphics C_screenWidth, C_screenHeight, C_screenDepth, C_screenType
ClsColor 0, 0, 0
SetBuffer(BackBuffer())

.again
Repeat
	Cls
	; what the function is passed - (number, gfxGeneric, custom_width, custom_hight, fntGeneric, btnTextColor, message$, boolean)
	
	SetFont fntSmallB : Color 255, 255, 255
	Text C_screenWidth / 2, C_screenHeight / 6, "Press keys 1 - 4", True, False
	; press a key to select a window
	Select True ; all options used here display as defaults - except text, default text is ""
		Case KeyHit(2) ; '1' key
			answer = AdviceWindowOption(0, 0, 0, 0, 0, 0, "Default Font", False) ;  no buttons
			mills = MilliSecs() : FlushKeys
		Case KeyHit(3) ; '2' key
			answer = AdviceWindowOption(1, 0, 0, 0, 0, 0, "Default Font", False) ;  one button 'ok'
			mills = MilliSecs() : FlushKeys
		Case KeyHit(4) ; '3' key
			answer = AdviceWindowOption(2, 0, 0, 0, 0, 0, "Default Font", False) ;  two button 'yes' & 'no'
			mills = MilliSecs() : FlushKeys
		Case KeyHit(5) ; '4' key
			answer = AdviceWindowOption(3, 0, 0, 0, 0, 0, "Default Font", False) ;  three buttons 'yes', 'no' & 'cancel'
			mills = MilliSecs() : FlushKeys
	End Select
	; outputs what response was given by user
	Select answer
		Case 0 ; no button clicked - normally not required to use return from function
			Text C_screenWidth / 2, C_screenHeight / 2, "No Buttons to 'click'", True, False
		Case 1 ; ok button
			; code here would be for what to do with answer (return)
			Text C_screenWidth / 2, C_screenHeight / 2, "OK Button", True, False
		Case 2 ; yes button
			; code here would be for what to do with answer (return)
			Text C_screenWidth / 2, C_screenHeight / 2, "Yes Button", True, False
		Case 3 ; no button
			; code here would be for what to do with answer (return)
			Text C_screenWidth / 2, C_screenHeight / 2, "No Button", True, False
		Case 4 ; cancel button
			; code here would be for what to do with answer (return)
			Text C_screenWidth / 2, C_screenHeight / 2, "Cancel Button", True, False
	End Select
	; resets variable 'answer' to stop displaying
	If mills + 1000 < MilliSecs()
		answer = 999
	EndIf
	Flip 

Until KeyHit(1)

; font changed from default
answer = AdviceWindowOption(2, 0, 0, 0, fntStandard, 0, "Please confirm you wish to quit program?", False) ;  two button 'yes' & 'no'

If answer = 3 Then Goto again

If answer = 2
	FreeFont fntStandard : FreeFont fntSmallB : FreeImage gfxTest
EndIf

EndGraphics : End

Include "AdviceWindowOption.bb" ; ensure correct path for include function or copy and paste here instead
