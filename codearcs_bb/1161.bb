; ID: 1161
; Author: churchaxe
; Date: 2004-09-17 11:27:16
; Title: VisualB for quickgui
; Description: graphical point and click editor

Graphics 1024, 768, 32, 2
SetBuffer BackBuffer()

Include "quickgui.bb"	;put in correct path
Include "quickgui_filerequestor.bb"

FreeFont gui_font
gui_font = LoadFont("Arial", 12, True)
SetFont gui_font
gui_usepointer = True

ClsColor 128, 128, 128

Global act	;selected gadget in form=window under construction
Global ctrlpos
Global cyclic_ids
Global dir_app$ = SystemProperty("APPDIR")
Global dir_img$ = dir_app
Global dir_open$ = dir_app
Global dir_snd$ = dir_app
Global endprog
Global exp_fulapp
Global exp_gadselect
Global exp_modal
Global fatmark
Global form
Global formih
Global formimg$
Global frmfile$
Global gadsel
Global mx, my
Global newwidth, newheight
Global posx, posy
Global wait

Type gad
	Field id
	Field noclick
	Field rightclick
	Field inputgadget
	Field container_for
	Field slider_in
	Field vertical
	Field ni$, nih
	Field di$, dih
	Field oi$, oih
	Field os$, osh
	Field ds$, dsh
End Type

If LoadINI() Then msgbox("if you need some HELP please press F1", 0, "First start...")
AppTitle frmfile + " - VisualB"

While Not endprog
	mx = MouseX()
	my = MouseY()
	Cls
	
	ShowPointer

	gadsel = gui_update(0, wait)
	g.gad = GetGad(gadsel)

	If g <> Null		;gadget in form selected
		act = gadsel
		FillCtrl()
	ElseIf gadsel > 0	;gadget in CtrlWin selected
		Flip
	EndIf
	
	If act <> 0 And KeyDown(56)		;test slider movement
		g = GetGad(act)
		If (g\slider_in <> 0)
			tmp$ = gui_handleSlider(act, form, act, g\slider_in, g\vertical, True)
			Color 255, 255, 0
			Text 230, 30, "slider value = " + tmp
			FillCtrl
		EndIf
	EndIf

	Color 0, 0, 0
	Text 230, 0, "mouse: x = " + mx + "   y = " + my

	;red marker for selected item
	If Not gui_gethandle(1000)
		Color 255, 0, 0
		If act > 0
			rx = gui_getx(act) + gui_getwinx(form)
			rw = gui_getw(act)
		Else
			rx = gui_getwinx(form)
			rw = gui_getwinw(form)
		EndIf
		
		If gui_gethandle(1)
			If ctrlpos = 0
				If rx < 227
				rw = rw - 227 + rx
				rx = 227
				EndIf
			ElseIf rx + rw > ctrlpos
				rw = ctrlpos - rx
			EndIf
		EndIf

		If act > 0
			Rect rx, gui_gety(act) + gui_getwiny(form), rw, gui_geth(act), False
			If fatmark Then Rect rx + 1, gui_gety(act) + gui_getwiny(form) + 1, rw - 2, gui_geth(act) - 2, False
		Else
			Rect rx, gui_getwiny(form), rw, gui_getwinh(form), False
			If fatmark Then Rect rx + 1, gui_getwiny(form) + 1, rw - 2, gui_getwinh(form) - 2, False
		EndIf
		Color 0, 0, 0
	EndIf

	enteringtext = False
	For a.gui_gadget = Each gui_gadget
		If a\mode And 2048
			enteringtext =  True
			Exit
		EndIf
	Next
	If Not enteringtext Then GetuserInput()

	Flip
Wend
SaveINI()
End		

Function ApplyAll()
	If act <> 0
		g.gad = GetGad(act)
		
		align = GetAlign(3)
		gui_settext(act, gui_gettext(2), align)

		;types
		tmp1$ = gui_gettext(16)
		tmp2$ = gui_gettext(17)
		tmp3$ = gui_gettext(18)
		
		gui_settypes(act, Int(tmp1), Int(tmp2), Int(tmp3))

		x = gui_gettext(23)
		y = gui_gettext(24)
		width = gui_gettext(25)
		height = gui_gettext(26)

		Select Int(tmp1)	;fixed height for radiobtns and checkboxes
			Case 13, 14
				gui_setsize(act, width, 13)
				gui_settext(26, "13")
			Case 15, 16
				gui_setsize(act, width, 12)
				gui_settext(26, "12")
			Default
				gui_setsize(act, width, height)
		End Select

		gui_setposition(act, x, y)		

		SetGadID()	

		;set slider/container
		tmp1 = gui_gettext(34)
		If tmp1 <> ""
			g2.gad = GetGad(Int(tmp1))
			If g2 <> Null
				If g2\id <> act
					If g2\slider_in <> 0
						MsgBox("Gadget " + g2\id + " is already a slider in " + g2\slider_in, 0, "Setting container...")
						If Not(gui_getmode(34) And 2048)
							gui_setmode(34, gui_getmode(34) + 2048)
							gui_textcursorpos = 0
							gui_settext(34, "")
						EndIf
					ElseIf g2\container_for <> 0 And (g2\container_for <> g\id)
						MsgBox("Gadget " + g2\id + " is already a container for " + g2\container_for, 0, "Setting container...")
						If Not(gui_getmode(34) And 2048)
							gui_setmode(34, gui_getmode(34) + 2048)
							gui_textcursorpos = 0
							gui_settext(34, "")
						EndIf
					Else
						g3.gad = GetGad(g\slider_in)
						If g3 <> Null
							g3\container_for = 0
							g3\noclick = False
						EndIf
						g\slider_in = g2\id
						If g\vertical
							gui_settext(33, "vertical")
						Else
							gui_settext(33, "horizontal")
						EndIf
						g2\noclick = True
						g2\container_for = act
					EndIf
				Else
					If g\slider_in <> 0
						gui_settext(34, g\slider_in)
					Else
						gui_settypes(32, 15)
						gui_settext(34, "")
					EndIf
				EndIf
			Else
				MsgBox("There's no gadget number " + tmp1 + " in Form " + form + ".", 0, "Setting container...")
				If g\slider_in <> 0
					gui_settext(34, g\slider_in)
				Else
					gui_settypes(32, 15)
					gui_settext(34, "")
				EndIf
			EndIf
		EndIf
	Else	;form
		align = GetAlign(form)
		SetWinText(form, gui_gettext(2), align)

		;type
		tmp1$ = gui_gettext(16)
		For a.gui_gadget = Each gui_gadget
			If a\num = 0 And a\windownum = form Then a\typ = Int(tmp1)
		Next
	
		x = gui_gettext(23)
		y = gui_gettext(24)
		width = gui_gettext(25)
		height = gui_gettext(26)
		
		gui_setwinxy(form, x, y)
		gui_setwinsize(form, width, height)
	EndIf
	
	;find/goto gadget
	tmp1 = gui_gettext(55)
	If tmp1 <> ""
		If Int(tmp1) = form
			gui_settext(55, "")
			act = 0
			FillCtrl()
			Return
		EndIf

		For g.gad = Each gad
			If g\id = Int(tmp1)
				act = g\id
				gui_settext(55, "")
				FillCtrl()
				Return
			EndIf
		Next
		If g = Last gad Or g = Null
			MsgBox("There's no gadget number " + tmp1 + " in Form " + form + ".", 0, "Goto Gadget...")
			gui_settext(55, "")
		EndIf
	EndIf
	
	;change filename
	tmp1$ = gui_gettext(57)
	If tmp1 <> "unnamed"
		If Lower(Right(tmp1, 4)) <> ".vbf"
			If Instr(tmp1, ".") Then tmp1 = Left(tmp1, Instr(tmp1, ".") - 1)
			tmp1 = tmp1 + ".vbf"
		Else
			tmp1 = tmp1
		EndIf
		
		If frmfile <> tmp1
			frmfile = tmp1
			saveform(frmfile)
			AppTitle frmfile + " - VisualB"
		EndIf
		gui_settext(57, tmp1, 1)
	EndIf
	
	;change to new FormID
	tmp1$ = gui_gettext(49)
	If Int(tmp1) <> form 
		SetFormID(Int(tmp1))
	EndIf
	
	gui_redrawwindow(form)
	gui_redrawwindow(1)
End Function

Function CtrlWin()
	If gui_gethandle(1) Then gui_freewindow(1)

	gui_makewindow(1, 226, 768, 0, 1)
	gui_makegadget(1, 1, 1, 27, 29, 15, 5, 8, "text", 1)
	gui_makegadget(1, 2, 27, 27, 195, 15, 261, 2, "texttext", 1)
	gui_makegadget(1, 3, 99, 6, 122, 15, 1, 1, "alignment", 1)
	gui_makegadget(1, 4, 4, 70, 42, 15, 5, 8, "modes", 1)
	gui_makegadget(1, 5, 5, 90, 55, 12, 5, 15, "noclick", 2)
	gui_makegadget(1, 6, 5, 105, 67, 12, 5, 15, "nosunken", 2)
	gui_makegadget(1, 7, 5, 120, 66, 12, 5, 15, "rightclick", 2)
	gui_makegadget(1, 8, 5, 135, 57, 12, 5, 15, "shadow", 2)
	gui_makegadget(1, 9, 5, 150, 46, 12, 5, 15, "ghost", 2)
	gui_makegadget(1, 10, 5, 165, 81, 12, 5, 15, "autobrighten", 2)
	gui_makegadget(1, 11, 5, 180, 45, 12, 5, 15, "input", 2)
	gui_makegadget(1, 12, 160, 71, 42, 15, 5, 8, "types", 2)
	gui_makegadget(1, 13, 132, 91, 42, 15, 5, 8, "normal", 2)
	gui_makegadget(1, 14, 132, 114, 42, 15, 5, 8, "over", 2)
	gui_makegadget(1, 15, 132, 137, 42, 15, 5, 8, "down", 2)
	gui_makegadget(1, 16, 176, 91, 25, 15, 261, 2, "16", 0)
	gui_makegadget(1, 17, 176, 114, 25, 15, 261, 2, "15", 0)
	gui_makegadget(1, 18, 176, 137, 25, 15, 261, 2, "11", 0)
	gui_makegadget(1, 19, 5, 210, 40, 14, 5, 8, "X", 2)
	gui_makegadget(1, 20, 5, 229, 40, 14, 5, 8, "Y", 2)
	gui_makegadget(1, 21, 5, 248, 40, 14, 5, 8, "width", 2)
	gui_makegadget(1, 22, 5, 267, 40, 14, 5, 8, "height", 2)
	gui_makegadget(1, 23, 50, 210, 35, 15, 261, 2, "xval", 0)
	gui_makegadget(1, 24, 50, 229, 35, 15, 261, 2, "yval", 0)
	gui_makegadget(1, 25, 50, 248, 35, 15, 261, 2, "wval", 0)
	gui_makegadget(1, 26, 50, 267, 35, 15, 261, 2, "hval", 0)
	gui_makegadget(1, 27, 110, 190, 110, 15, 1, 1, "compress IDs now", 0)
	gui_makegadget(1, 28, 142, 248, 64, 15, 1, 1, "duplicate", 0)
	gui_makegadget(1, 29, 142, 267, 64, 15, 1, 1, "delete", 0)
	gui_makegadget(1, 30, 1, 6, 42, 15, 5, 8, "gadget", 1)
	gui_makegadget(1, 31, 41, 6, 46, 15, 261, 2, "gadID", 0)
	gui_makegadget(1, 32, 1, 306, 108, 12, 5, 15, "slider in container", 2)
	gui_makegadget(1, 33, 163, 306, 56, 15, 1, 1, "vertical", 0)
	gui_makegadget(1, 34, 110, 306, 46, 15, 261, 2, "containerID", 0)
	gui_makegadget(1, 35, 6, 359, 45, 15, 5, 8, "images", 1)
	gui_makegadget(1, 36, 6, 379, 25, 15, 5, 8, "ni", 1)
	gui_makegadget(1, 37, 6, 399, 25, 15, 5, 8, "oi", 1)
	gui_makegadget(1, 38, 6, 419, 25, 15, 5, 8, "di", 1)
	gui_makegadget(1, 39, 6, 445, 46, 15, 5, 8, "sounds", 1)
	gui_makegadget(1, 40, 6, 465, 25, 15, 5, 8, "os", 1)
	gui_makegadget(1, 41, 6, 485, 25, 15, 5, 8, "ds", 1)
	gui_makegadget(1, 42, 21, 379, 200, 15, 1, 1, "nival", 0)
	gui_makegadget(1, 43, 21, 399, 200, 15, 1, 1, "oival", 0)
	gui_makegadget(1, 44, 21, 419, 200, 15, 1, 1, "dival", 0)
	gui_makegadget(1, 45, 21, 465, 200, 15, 1, 1, "osval", 0)
	gui_makegadget(1, 46, 21, 485, 200, 15, 1, 1, "dsval", 0)
	gui_makegadget(1, 47, 142, 229, 64, 15, 1, 1, "insert", 0)
	gui_makegadget(1, 48, 3, 520, 32, 12, 5, 8, "form", 0)
	gui_makegadget(1, 49, 38, 518, 50, 15, 261, 2, "formID", 0)
	gui_makegadget(1, 50, 100, 518, 50, 15, 1, 1, "new", 0)
	gui_makegadget(1, 51, 160, 518, 50, 15, 1, 1, "open", 0)
	gui_makegadget(1, 52, 100, 538, 50, 15, 1, 1, "save", 0)
	gui_makegadget(1, 53, 160, 598, 50, 15, 1, 1, "export", 0)
	gui_makegadget(1, 54, 1, 52, 79, 12, 5, 8, "goto", 0)
	gui_makegadget(1, 55, 80, 50, 46, 15, 261, 2, "", 0)
	gui_makegadget(1, 56, 4, 562, 52, 12, 5, 8, "filename", 1)
	gui_makegadget(1, 57, 53, 561, 166, 15, 261, 2, "filenameval", 1)
	gui_makegadget(1, 58, 156, 746, 64, 15, 1, 1, "quit", 0)
	gui_makegadget(1, 59, 5, 598, 103, 12, 5, 15, "long gadgetIDs", 2)
	gui_makegadget(1, 60, 5, 623, 201, 13, 5, 13, "full application", 2)
	gui_makegadget(1, 61, 36, 638, 136, 13, 5, 13, "without selector", 2)
	gui_makegadget(1, 62, 36, 654, 137, 13, 5, 13, "select / case selector", 2)
	gui_makegadget(1, 63, 36, 670, 137, 13, 5, 13, "if / then / else selector", 2)
	gui_makegadget(1, 64, 5, 719, 202, 13, 5, 13, "form and gadget code only", 2)
	gui_makegadget(1, 65, 119, 686, 53, 12, 5, 15, "modal", 2)
	gui_makegadget(1, 66, 1, 326, 108, 15, 5, 8, "container for slider", 0)
	gui_makegadget(1, 67, 110, 326, 46, 15, 5, 8, "sliderID", 0)
	gui_makegadget(1, 68, 110, 170, 110, 12, 5, 15, "cyclic IDs", 2)
	gui_makegadget(1, 69, 38, 538, 50, 15, 1, 1, "reload", 0)
	gui_makegadget(1, 70, 142, 50, 64, 15, 1, 1, "IDs info", 0)
	gui_makegadget(1, 71, 7, 746, 64, 15, 1, 1, "help", 0)
	gui_makegadget(1, 72, 160, 538, 50, 15, 1, 1, "save as", 0)

	gui_settiptext(2, "F2")
	gui_settiptext(28, "Ctrl/Shift+D")
	gui_settiptext(29, "Ctrl+X")
	gui_settiptext(33, "horizontal/vertical")
	gui_settiptext(47, "Ins")
	gui_settiptext(50, "Ctrl+N")
	gui_settiptext(51, "Ctrl+O")
	gui_settiptext(52, "Ctrl+S")
	gui_settiptext(53, "Ctrl+E")
	gui_settiptext(55, "F3")
	gui_settiptext(58, "Alt+F4")
	gui_settiptext(69, "Ctrl+Z")
	gui_settiptext(70, "Ctrl+I")
	gui_settiptext(71, "F1")

	gui_drawwindow(1, ctrlpos, 0)

	If exp_fulapp = True
		gui_settypes(60, 14)
		gui_settypes(64, 13)
		If exp_modal
			gui_settypes(65, 16)
		Else
			gui_settypes(65, 15)
		EndIf
		If exp_gadselect = 0
			gui_settypes(61, 14)
			gui_settypes(62, 13)
			gui_settypes(63, 13)
		ElseIf exp_gadselect = 1
			gui_settypes(61, 13)
			gui_settypes(62, 14)
			gui_settypes(63, 13)
		Else
			gui_settypes(61, 13)
			gui_settypes(62, 13)
			gui_settypes(63, 14)
		EndIf
	Else
		gui_settypes(60, 13)
		gui_settypes(64, 14)
		gui_settypes(65, 15)
		gui_settypes(61, 13)
		gui_settypes(62, 13)
		gui_settypes(63, 13)
	EndIf
	
	If cyclic_ids Then gui_settypes(68, 16)
	
	FillCtrl()
	gui_drawwindow(1, ctrlpos, 0)
End Function

Function Export()
	If Instr(frmfile, ".")
		expfile$ = dir_open + Left(frmfile, Instr(frmfile, ".") - 1) + ".bb"
	Else
		expfile$ = dir_open + frmfile + ".bb"
	EndIf

	fileh = WriteFile(expfile)

		WriteLine(fileh, ";created by VisualB")
		
		If exp_fulapp	;sample app code
			WriteLine(fileh, "Graphics 1024, 768, 32, 2")
			WriteLine(fileh, "SetBuffer BackBuffer()")
			WriteLine(fileh, "Include " + Chr(34) + "quickGui.bb" + Chr(34) + "	;complete with correct path")
			WriteLine(fileh, "")
			WriteLine(fileh, "FreeFont gui_font")
			WriteLine(fileh, "gui_font = LoadFont(" + Chr(34) + "Arial" + Chr(34) + ", 12, True)")
			WriteLine(fileh, "SetFont gui_font")
			WriteLine(fileh, "gui_usepointer = True")
			WriteLine(fileh, "Global debugtext$")

			If exp_gadselect > 0
				WriteLine(fileh, "Global gui_wait")
				WriteLine(fileh, "Global gadsel")
				WriteLine(fileh, "")
			EndIf

			If Not exp_modal Then WriteLine(fileh, "CreateWindow()")

			WriteLine(fileh, "")
			WriteLine(fileh, "While Not KeyHit(1)		;mainloop")
			WriteLine(fileh, "	Cls")
			If exp_gadselect > 0
				WriteLine(fileh, "	gadsel = gui_update(0, gui_wait)")
			Else
				WriteLine(fileh, "	gadsel = gui_update()")
			EndIf
			
			If exp_modal = False
				For g.gad = Each gad
					If g\slider_in <> 0
						If actline = False
							WriteLine(fileh, "	If gadsel Then act = gadsel")
							actline = True
						EndIf

						If gui_gettype(59) = 16
							exp_ID$ = Str(form) + Str(g\id - form)
							exp_ID2$ = Str(form) + Str(g\slider_in - form)
						Else
							exp_ID = g\id
							exp_ID2 = g\slider_in
						EndIf
			
						WriteLine(fileh, "	If act = " + exp_ID + " Then debugtext = gui_HandleSlider(act, " + form + ", " + exp_ID + ", " + exp_ID2 + ", " + g\vertical + ", True)")
					EndIf
				Next

				WriteLine(fileh, "")
				If exp_gadselect > 0 Then WriteLine(fileh, "	HandleGuiInput()")
				WriteLine(fileh, "	If debugtext <> " + Chr(34) + Chr(34))
				WriteLine(fileh, "		Color 255, 0, 0")
				WriteLine(fileh, "		Text 0, 0, debugtext")
				WriteLine(fileh, "	EndIf")
				WriteLine(fileh, "")
			ElseIf exp_modal
				WriteLine(fileh, "	Color 255, 255, 255")
				WriteLine(fileh, "	If retval Then t$ = " + Chr(34) + " again" + Chr(34))
				WriteLine(fileh, "	Text 0, 0, " + Chr(34) + "hit SPACE to start window function" + Chr(34) + " + t + " + Chr(34) + ", ESC To Exit program" + Chr(34))
				WriteLine(fileh, "	If KeyHit(57)")
				WriteLine(fileh, "		FlushKeys")
				WriteLine(fileh, "		retval = CreateWindow()")
				WriteLine(fileh, "	EndIf")
				WriteLine(fileh, "")
				WriteLine(fileh, "	If retval")
				WriteLine(fileh, "		Color 255, 255, 0")
				WriteLine(fileh, "		Text 0, 30, " + Chr(34) + "returned value = " + Chr(34) + " + retval")
				WriteLine(fileh, "	EndIf")
				WriteLine(fileh, "")
			EndIf

			WriteLine(fileh, "	Flip")
			WriteLine(fileh, "Wend")
			WriteLine(fileh, "End")
			WriteLine(fileh, "")

			If exp_gadselect
				WriteLine(fileh, "Function HandleGUIInput()")

				If exp_gadselect = 1
					WriteLine(fileh, ";gadget selector type: select/case")
					WriteLine(fileh, "	Select gadsel")
					For g.gad = Each gad
						If Not g\noclick
							If gui_gettype(59) = 16
								exp_ID$ = Str(form) + Str(g\id - form)
								WriteLine(fileh, "	Case " + exp_ID)
								WriteLine(fileh, "		debugtext = " + Chr(34) + "gadget " + Chr(34) + " + " + exp_ID + " + " + Chr(34) + " selected" + Chr(34))
							Else
								WriteLine(fileh, "	Case " + g\id)
								WriteLine(fileh, "		debugtext = " + Chr(34) + "gadget " + Chr(34) + " + " + g\id + " + " + Chr(34) + " selected" + Chr(34))
							EndIf
						EndIf
					Next
					WriteLine(fileh, "	End Select")
					WriteLine(fileh, "")
				ElseIf exp_gadselect = 2
					WriteLine(fileh, ";gadget selector type: if/then/else")
					For g.gad = Each gad
						If gui_gettype(59) = 16
							exp_ID$ = Str(form) + Str(g\id - form)
							If g = First gad
								WriteLine(fileh, "	If gadsel = " + exp_ID)
								WriteLine(fileh, "		debugtext = " + Chr(34) + "gadget " + Chr(34) + " + " + exp_ID + " + " + Chr(34) + " selected" + Chr(34))
							ElseIf Not g\noclick
								WriteLine(fileh, "	ElseIf gadsel = " + exp_ID)
								WriteLine(fileh, "		debugtext = " + Chr(34) + "gadget " + Chr(34) + " + " + exp_ID + " + " + Chr(34) + " selected" + Chr(34))
							EndIf
						Else
							If g = First gad
								WriteLine(fileh, "	If gadsel = " + g\id)
								WriteLine(fileh, "		debugtext = " + Chr(34) + "gadget " + Chr(34) + " + " + g\id + " + " + Chr(34) + " selected" + Chr(34))
							ElseIf Not g\noclick
								WriteLine(fileh, "	ElseIf gadsel = " + g\id)
								WriteLine(fileh, "		debugtext = " + Chr(34) + "gadget " + Chr(34) + " + " + g\id + " + " + Chr(34) + " selected" + Chr(34))
							EndIf
						EndIf
					Next
					WriteLine(fileh, "	EndIf")
				EndIf

				WriteLine(fileh, "	If MouseDown(1)")
				WriteLine(fileh, "		gui_wait = True")
				WriteLine(fileh, "	Else")
				WriteLine(fileh, "		gui_wait = False")
				WriteLine(fileh, "	EndIf")
				WriteLine(fileh, "End Function")
				WriteLine(fileh, "")
			EndIf

			WriteLine(fileh, "Function CreateWindow()")

			If exp_modal		;modal test win
				WriteLine(fileh, ";program stops until dialog is terminated")
				WriteLine(fileh, ";returns 1 when ended with ESC, 2 when ended with ENTER")
				WriteLine(fileh, "")
			EndIf
		EndIf

		w_x = gui_getwinx(form)
		w_y = gui_getwiny(form)
		w_width = gui_getwinw(form)
		w_height = gui_getwinh(form)
		w_type = GetWinType(form)
		WriteLine(fileh, "	gui_makewindow(" + form + ", " + w_width + ", " + w_height + ", 0, " + w_type + ")")

		For g.gad = Each gad	;gadgets
			If gui_gettype(59) = 16
				exp_ID = Str(form) + Str(g\id - form)
			Else
				exp_ID = g\id
			EndIf
			
			width = gui_getw(g\id)
			height = gui_geth(g\id)
			mode =  gui_getmode(g\id)
			If g\noclick Then mode = mode + 2
			If g\rightclick Then mode = mode + 8
			If g\Inputgadget Then mode = mode + 256

			nt =  gui_gettype(g\id)
			ot = GetOverType(g\id)
			dt = GetDownType(g\id)

			ni$ = g\ni
			oi$ = g\oi
			di$ = g\di
			os$ =  g\os
			ds$ =  g\ds

			WriteLine(fileh, "	gui_makegadget(" + form + ", " + exp_ID + ", " + gui_getx(g\id) + ", " + gui_gety(g\id) + ", " + width + ", " + height + ", " + mode + ", " + nt + ", " + Chr(34) + gui_gettext(g\id) + Chr(34) + ", " + GetAlign(g\id) + ")")
			If (ot Or dt) Then WriteLine(fileh, "		gui_settypes(" + exp_ID + ", " + nt + ", " + ot + ", " + dt + ")")
			If g\ni <> ""
				WriteLine(fileh, "		nih = LoadImage(" + Chr(34) + g\ni + Chr(34) + ")")
				WriteLine(fileh, "		If nih <> 0 Then nih = gui_givebutton(nih, " + width + ", " + height + ")")
				tmp1$ = "nih"
			Else
				tmp1$ = Chr(34) + Chr(34)
			EndIf
			If g\oi <> ""
				WriteLine(fileh, "		oih = LoadImage(" + Chr(34) + g\oi + Chr(34) +  ")")
				WriteLine(fileh, "		If oih <> 0 Then oih = gui_givebutton(oih, " + width + ", " + height + ")")
				tmp2$ = "oih"
			Else
				tmp2$ = Chr(34) + Chr(34)
			EndIf
			If g\di <> ""
				WriteLine(fileh, "		dih = LoadImage(" + Chr(34) + g\di + Chr(34) +  ")")
				WriteLine(fileh, "		If dih <> 0 Then dih = gui_givebutton(dih, " + width + ", " + height + ")")
				tmp3$ = "dih"
			Else
				tmp3$ = Chr(34) + Chr(34)
			EndIf
			If g\ni <> "" Or g\oi <> "" Or g\di <> ""
				WriteLine(fileh, "		gui_setimages(" + exp_ID + ", 0, 0, " + tmp1 + ", " + tmp2 + ", " + tmp3 + ")")
				WriteLine(fileh, "")
			EndIf

			If g\os <> ""
				WriteLine(fileh, "		osh = LoadSound(" + Chr(34) + g\os + Chr(34) + ")")
				tmp1$ = "osh"
			Else
				tmp1$ = Chr(34) + Chr(34)
			EndIf
			If g\ds <> ""
				WriteLine(fileh, "		dsh = LoadSound(" + Chr(34) + g\ds + Chr(34) +  ")")
				tmp2$ = "dsh"
			Else
				tmp2$ = Chr(34) + Chr(34)
			EndIf
			If g\os <> "" Or g\ds <> ""
				WriteLine(fileh, "		gui_setsounds(" + exp_ID + ", " + tmp1 + ", " + tmp2 + ")")
				WriteLine(fileh, "")
			EndIf
		Next

		WriteLine(fileh, "")
		WriteLine(fileh, "	gui_drawwindow(" + form + ", " + w_x + ", " + w_y + ")")
		If exp_modal And exp_fulapp
			WriteLine(fileh, "")
			WriteLine(fileh, "	Repeat		;loop")
			WriteLine(fileh, "		Cls")
			WriteLine(fileh, "		Color 255, 255, 255")
			WriteLine(fileh, "		Text 0, 0, " + Chr(34) + "hit ESC to exit and return 1" + Chr(34))
			WriteLine(fileh, "		Text 0, 10, " + Chr(34) + "hit ENTER to exit and return 2" + Chr(34))
			WriteLine(fileh, "		gadsel = gui_update(" +  form + ", gui_wait)")

				For g.gad = Each gad
					If g\slider_in <> 0
						If actline = False
							WriteLine(fileh, "		If gadsel Then act = gadsel")
							WriteLine(fileh, "")
							actline = True
						EndIf

						If gui_gettype(59) = 16
							exp_ID$ = Str(form) + Str(g\id - form)
							exp_ID2$ = Str(form) + Str(g\slider_in - form)
						Else
							exp_ID = g\id
							exp_ID2 = g\slider_in
						EndIf
			
						WriteLine(fileh, "		If act = " + exp_ID + " Then debugtext = gui_HandleSlider(act, " + form + ", " + exp_ID + ", " + exp_ID2 + ", " + g\vertical + ", True)")
					EndIf
				Next
				
			If exp_gadselect > 0
				WriteLine(fileh, "")
				WriteLine(fileh, "		HandleGuiInput()")
			EndIf

			WriteLine(fileh, "")
			WriteLine(fileh, "		If debugtext <> " + Chr(34) + Chr(34))
			WriteLine(fileh, "			Color 255, 0, 0")
			WriteLine(fileh, "			Text 0, 30, debugtext")
			WriteLine(fileh, "		EndIf")
			WriteLine(fileh, "")

			WriteLine(fileh, "		Flip")
			WriteLine(fileh, "")
			WriteLine(fileh, "		If KeyHit(1)")
			WriteLine(fileh, "			FlushKeys")
			WriteLine(fileh, "			gui_freewindow(" + form + ")")
			If exp_gadselect Then WriteLine(fileh, "			debugtext = " + Chr(34) + Chr(34))
			WriteLine(fileh, "			Return 1")
			WriteLine(fileh, "		ElseIf KeyHit(28)")
			WriteLine(fileh, "			FlushKeys")
			WriteLine(fileh, "			gui_freewindow(" + form + ")")
			If exp_gadselect Then WriteLine(fileh, "			debugtext = " + Chr(34) + Chr(34))
			WriteLine(fileh, "			Return 2")
			WriteLine(fileh, "		EndIf")
			WriteLine(fileh, "	Forever")
		EndIf
		If exp_fulapp Then WriteLine(fileh, "End Function")

	CloseFile(fileh)
End Function

Function FillCtrl()
	gui_settext(57, frmfile, 1)
	gui_settext(49, form)
	If act > 0
		g.gad = GetGad(act)
		
		If g\container_for <> 0
			gui_settext(67, g\container_for)
		Else
			gui_settext(67, "")
		EndIf

		If g\slider_in <> 0
			gui_settypes(32, 16)
			gui_settext(34, g\slider_in)
			If g\vertical
				gui_settext(33, "vertical")
			Else
				gui_settext(33, "horizontal")
			EndIf
		Else
			gui_settypes(32, 15)
			gui_settext(33, "")
			gui_settext(34, "")
		EndIf

		gui_settext(2, gui_gettext(act), 1)
		
		;modes
		mode = gui_getmode(act)
		If g\noclick Then
			gui_settypes(5, 16)
		Else
			gui_Settypes(5, 15)
		EndIf

		If mode And 4 Then
			gui_settypes(6, 16)
		Else
			gui_settypes(6, 15)
		EndIf

		If g\rightclick Then
			gui_settypes(7, 16)
		Else
			gui_settypes(7, 15)
		EndIf

		If g\inputgadget Then
			gui_settypes(11, 16)
		Else
			gui_settypes(11, 15)
		EndIf

		If mode And 16 Then
			gui_settypes(8, 16)
		Else
			gui_settypes(8, 15)
		EndIf

		If mode And 32 Then
			gui_settypes(9, 16)
		Else
			gui_settypes(9, 15)
		EndIf
		If mode And 64 Then
			gui_settypes(10, 16)
		Else
			gui_settypes(10, 15)
		EndIf
		
		gui_settext(31, act)
		ntyp = gui_gettype(act)
		gui_settext(16, ntyp)
		gui_settext(17, GetOverType(act))
		gui_settext(18, GetDownType(act))

		align = GetAlign(act)
		If ntyp > 12 And ntyp < 17
			tmp1$ = "right"
			align = 2
		Else
			If align = 0
				tmp1$ = "center"
			ElseIf align = 1
				tmp1 = "left"
			ElseIf align = 2
				tmp1 = "right"
			EndIf
		EndIf
		
		gui_settext(3, tmp1 + " align", align)
		gui_settypes(3, 1)

		If g\ni <> ""
			gui_settext(42, g\ni)
		Else
			gui_settext(42, "(none)")
		EndIf
		If g\oi <> ""
			gui_settext(43, g\oi)
		Else
			gui_settext(43, "(none)")
		EndIf
		If g\di <> ""
			gui_settext(44, g\di)
		Else
			gui_settext(44, "(none)")
		EndIf
		If g\os <> ""
			gui_settext(45, g\os)
		Else
			gui_settext(45, "(none)")
		EndIf
		If g\ds <> ""
			gui_settext(46, g\ds)
		Else
			gui_settext(46, "(none)")
		EndIf
		gui_settext(23, gui_getx(act))
		gui_settext(24, gui_gety(act))
		gui_settext(25, gui_getw(act))
		gui_settext(26, gui_geth(act))
	Else	;form
		gui_settypes(32, 15)
		gui_settext(33, "")
		gui_settext(34, "")
		gui_settext(67, "")

		tmp1$ = GetWinText(form)

		If tmp1 = ""
			gui_settext(2, tmp1)
		Else
			gui_settext(2, tmp1, 1)
		EndIf

		align = GetAlign(form)
		If align = 0
			tmp1$ = "center"
		ElseIf align = 1
			tmp1 = "left"
		ElseIf align = 2
			tmp1 = "right"
		EndIf
		
		gui_settext(3, tmp1 + " align", align)
		gui_settypes(3, 1)

		gui_settypes(5, 15)
		gui_settypes(6, 15)
		gui_settypes(7, 15)
		gui_settypes(8, 15)
		gui_settypes(9, 15)
		gui_settypes(10, 15)
		gui_settypes(11, 15)

		mode = gui_getmode(0)
		If mode And 2
			gui_settypes(5, 16)
		ElseIf mode And 4
			gui_settypes(6, 16)
		ElseIf mode And 8
			gui_settypes(7, 16)
		ElseIf mode And 16
			gui_settypes(8, 16)
		ElseIf mode And 32
			gui_settypes(9, 16)
		ElseIf mode And 64
			gui_settypes(10, 16)
		ElseIf mode And 256
			gui_settypes(11, 16)
		EndIf

		ntyp = GetWinType(form)
		gui_settext(16, ntyp)
	
		gui_settext(31, "")
		gui_settext(43, "")
		gui_settext(44, "")
		gui_settext(45, "")
		gui_settext(46, "")
		If formimg <> ""
			gui_settext(42, formimg)
		Else
			gui_settext(42, "(none)")
		EndIf
		gui_settext(23, gui_getwinx(form))
		gui_settext(24, gui_getwiny(form))
		gui_settext(25, gui_getwinw(form))
		gui_settext(26, gui_getwinh(form))
		gui_settext(17, "")
		gui_settext(18, "")
	EndIf
	If gui_gethandle(1) Then gui_redrawwindow(1)
End Function

Function GetAlign(name)
	For w.gui_gadget = Each gui_gadget
		If w\num = name Or (w\windownum = name And w\num = 0)
			Return w\justify
		EndIf
	Next
End Function

Function GetDownType(name)
	For w.gui_gadget = Each gui_gadget
		If w\num = name
			Return w\down_gad_type
		EndIf
	Next
End Function

Function GetFreeID(num)
	If num < form + 1 Then num = form + 1
	g.gad = First gad
	If g = Null Then Return num
	
	Repeat
		For g = Each gad
			If num = g\id Then Exit	;not free
			If g = Last gad
				Return num
			EndIf
		Next
		num = num + 1
	Forever
End Function

Function GetGad.gad(id)
	For g.gad = Each gad
		If g\id = id Then Return g
	Next
End Function

Function GetOverType(name)
	For w.gui_gadget = Each gui_gadget
		If w\num = name
			Return w\over_gad_type
		EndIf
	Next
End Function

Function GetUserInput()
	If (MouseHit(3) Or MouseHit(2))		;init pos for pan/scale
		If act > 0
			posx = mx - gui_getwinx(form) - gui_getx(act)
			posy = my - gui_getwiny(form) - gui_gety(act)
		Else
			posx = mx - gui_getwinx(form)
			posy = my - gui_getwiny(form)
		EndIf
		MouseXSpeed()
		MouseYSpeed()
	EndIf

	If MouseDown(2)		;pan
		If act > 0
			If KeyDown(42) Or KeyDown(54)		;horiz
				newx = mx - gui_getwinx(form) - posx
				newy = gui_gety(act)
			ElseIf KeyDown(29) Or KeyDown(157)	;vert
				newx = gui_getx(act)
				newy = my - gui_getwiny(form) - posy
			Else								;both
				newx = mx - gui_getwinx(form) - posx
				newy = my - gui_getwiny(form) - posy
			EndIf

			gui_setposition(act, newx, newy)
		Else	;form
			If KeyDown(42) Or KeyDown(54)
				newx = mx - posx
				newy = gui_getwiny(form)
			ElseIf KeyDown(29) Or KeyDown(157)
				newx = gui_getwinx(form)
				newy = my - posy
			Else
				newx = mx - posx
				newy = my - posy
			EndIf
			gui_setwinxy(form, newx, newy)
		EndIf
		gui_redrawwindow(form)
		FillCtrl()
	ElseIf MouseDown(3)	;scale
		mxspd# = MouseXSpeed()
		myspd# = MouseYSpeed()
		If act > 0
			If KeyDown(42) Or KeyDown(54)		;width
				newwidth = gui_getw(act) + mxspd
				newheight = gui_geth(act)
			ElseIf KeyDown(29) Or KeyDown(157)	;height
				newwidth = gui_getw(act)
				newheight = gui_geth(act) + myspd
			Else								;both
				newwidth = gui_getw(act) + mxspd
				newheight = gui_geth(act) + myspd
			EndIf
			If newwidth < 25 Then newwidth = 25
			If newheight < 10 Then newheight = 10
			nt = gui_gettype(act)

			Select nt	;radiobtns/checkboxes (needs fix in quickgui.bb)
				Case 13, 14
					newheight = 13
				Case 15, 16
					newheight = 12
			End Select
			gui_setsize(act, newwidth, newheight)

		Else	;form
			If KeyDown(42) Or KeyDown(54)
				newwidth = gui_getwinw(form) + mxspd
				newheight = gui_getwinh(form)
			ElseIf KeyDown(29) Or KeyDown(157)
				newwidth = gui_getwinw(form)
				newheight = gui_getwinh(form) + myspd
			Else
				newwidth = gui_getwinw(form) + mxspd
				newheight = gui_getwinh(form) + myspd
			EndIf
			If newwidth < 100 Then newwidth = 100
			If newheight < 50 Then newheight = 50
			gui_setwinsize(form, newwidth, newheight)
		EndIf
		gui_redrawwindow(form)
		FillCtrl()
	EndIf
	
	If MouseDown(3) = False And newwidth <> 0 And newheight <> 0
		If act > 0
			UpdImages(GetGad(act))
			gui_redrawwindow(form)
		Else
			UpdImages(Null)
			gui_redrawwindow(form)
		EndIf
		newwidth = 0
		newheight = 0
	EndIf
	
	If MouseDown(1) Or MouseDown(2) Or MouseDown(3)
		wait = True
	Else
		wait = False
	EndIf
	
	If KeyHit(28)	;return
		ApplyAll()
	ElseIf gadsel = 58
		waitformouse()
		endprog = True
	ElseIf KeyHit(62)	;f4
		If gui_gethandle(1)
			gui_freewindow(1)
		Else
			CtrlWin()
		EndIf
	ElseIf KeyHit(45) And (KeyDown(29) Or KeyDown(157)) Or gadsel = 29	;ctrl+X
		waitformouse()
		If act = 0 Then Return
		g.gad = GetGad(act)
		FreeImage g\nih
		FreeImage g\oih
		FreeImage g\dih
		FreeSound g\osh
		FreeSound g\dsh
		
		If g\container_for
			g2.gad = GetGad(g\container_for)
			g2\slider_in = 0
		EndIf
		
		If g\slider_in
			g2.gad = GetGad(g\slider_in)
			g2\container_for = 0
		EndIf

		Delete g
		
		For w.gui_gadget = Each gui_gadget
			If w\num = act
				Delete w.gui_gadget
				Exit
			EndIf
		Next
		If cyclic_IDs Then SortIDs()
		act = 0
		gui_redrawwindow(form)
		FillCtrl()
	ElseIf KeyHit(31) And (KeyDown(29) Or KeyDown(157)) Or gadsel = 52 Or gadsel = 72	;save
		waitformouse()
		tmp$ = gui_gettext(57)
		
		If Lower(Left(tmp, 9) = "unnamed") Or gadsel = 72
			gui_filter(0) = ".vbf"
			tmp = gui_Filerequestor("Save file as...", dir_open, "", GraphicsWidth()/2 - 225, GraphicsHeight()/2 - 160)
	
			If tmp <> ""
				For position = Len(tmp) - 1 To 1 Step - 1
					If Mid(tmp, position, 1) = "\"
						path$ = Left(tmp, position)
						Exit
					EndIf
				Next
	
				If Lower(Right(tmp, 4)) <> ".vbf" Then tmp = tmp + ".vbf"
		
				dir_open = path			
				SaveForm(Right(tmp, Len(tmp) - Len(path)))
			EndIf
		ElseIf tmp <> ""
			If Lower(Right(tmp, 4)) <> ".vbf" Then tmp = tmp + ".vbf"
			SaveForm(tmp)
		EndIf
	ElseIf KeyHit(24) And (KeyDown(29) Or KeyDown(157)) Or gadsel = 51	;open
		waitformouse()
		FlushKeys
		gui_filter(0) = ".vbf"
		tmp$ = gui_filerequestor("Open file...", dir_open, "", GraphicsWidth()/2 - 225, GraphicsHeight()/2 - 160)

		If tmp <> ""
			For position = Len(tmp) - 1 To 1 Step - 1
				If Mid(tmp, position, 1) = "\"
					path$ = Left(tmp, position)
					Exit
				EndIf
			Next

			dir_open = path
			LoadForm(Right(tmp, Len(tmp) - Len(path)))
		EndIf
	ElseIf KeyHit(32) And (KeyDown(29) Or KeyDown(42)) Or gadsel = 28	;dup gad, shift=horiz, ctrl=vert
		If act = 0 Then Return
		oldgad.gad = GetGad(act)

		width = gui_getw(act)
		height = gui_geth(act)

		If KeyDown(42)
			x = gui_getx(act) + width + 5
			y = gui_gety(act)
		Else
			x = gui_getx(act)
			y = gui_gety(act) + height + 5
		EndIf

		mode = gui_getmode(act)
		nt = gui_gettype(act)
		ot = GetOverType(act)
		dt = GetDownType(act)
		align = GetAlign(act)
		
		g.gad = New gad
		g\id = GetFreeID(form + 1)
		g\noclick = oldgad\noclick
		g\rightclick = oldgad\rightclick
		g\inputgadget = oldgad\inputgadget
		g\ni = oldgad\ni
		g\oi = oldgad\oi
		g\di = oldgad\di
		g\os = oldgad\os
		g\ds = oldgad\ds
		
		gui_makegadget(form, g\id, x, y, width, height, mode, 1, g\id, align)
		gui_settypes(g\id, nt, ot, dt)
		
		act = g\id
		UpdImages(g)
		UpdSounds(g)
		gui_redrawwindow(form)
		FillCtrl()
		waitformouse()
	ElseIf KeyHit(49) And (KeyDown(29) Or KeyDown(157)) Or gadsel = 50	;new form
		waitformouse()
		For g.gad = Each gad
			FreeImage g\nih
			FreeImage g\oih
			FreeImage g\dih
			FreeSound g\osh
			FreeSound g\dsh
			Delete g
		Next
		frmfile = "unnamed"
		
		gui_freewindow(form)
		form = 100
		gui_makewindow(form, 400, 300, 0, 1)
		gui_drawwindow(form, 260, 60)
		
		act = 0

		AppTitle frmfile + " - VisualB"
		CtrlWin()
		FillCtrl()
	ElseIf KeyHit(210) Or gadsel = 47	;ins-new gad
		g.gad = New gad
		g\id = GetFreeID(form + 1)
		
		gui_makegadget(form, g\id, 50, 50, 60, 25, 1, 1, g\id)
		act = g\id
		FillCtrl()
		gui_redrawwindow(form)
		waitformouse()
	ElseIf gadsel = 27	;compress IDs
		waitformouse()
		retval = MsgBox("Gadget IDs will be changed. Continue?", 1, "Sort and compress gadget IDs...")
		If retval = 1
			SortIDs()
		EndIf
	ElseIf gadsel = 0 And MouseHit(1) And mx>gui_getwinx(form) And mx<gui_getwinx(form)+gui_getwinw(form) And my>gui_getwiny(form) And my<gui_getwiny(form)+gui_getwinh(form) And KeyDown(56) = False
		;Lclick: activate form
		act = 0
		FillCtrl()
	ElseIf KeyHit(60)	;F2 edit text
		FlushKeys
		If Not(gui_getmode(2) And 2048)
			gui_settext(2, "", GetAlign(2))
			gui_setmode(2, gui_getmode(2) + 2048)
			gui_textcursorpos = 0
		EndIf
	ElseIf KeyHit(59) Or gadsel = 71	;F1 help
		waitformouse()
		txt$ = "Ins - adds new gadget at (form-) position x=50, y=50" + Chr(31) + "LMB - selects item. MMB - resizes, RMB - moves selected item." + Chr(31) + "Shift - limits resizing/moving to horizontal only, "
		txt = txt + "Ctrl - limits to vertical" + Chr(31) + "Enter - applies changes made in control window"  + Chr(31) + Chr(31) + "Some hotkeys can be retrieved by holding the mouse over a button for about "
		txt = txt + "two secs..." + Chr(31) + Chr(31) + "F1 - displays this help" + Chr(31) + "F2 - edit gadget text" + Chr(31) + "F3 - find/goto gadget" + Chr(31) + "F4 - show/hide control window"
		txt = txt + Chr(31) + "F5 - left/right position of control window" + Chr(31) + "F6 - slim/fat gadget marker" + Chr(31) + "Alt+F4 - ends program" + Chr(31) + "Tab/Shift+Tab - jumps from item To item" 
		txt = txt + Chr(31) + "Alt+drag - tests slider movement" + Chr(31) + Chr(31) + "SPECIAL THANKS TO THE PEOPLE OF WICKEDRUSH SOFTWARE FOR THE WORK AND POWER THEY PUT IN QUICKGUI!"
		MsgBox(txt, 0, "Help...")
	ElseIf KeyHit(1)
		endprog = True
	ElseIf KeyHit(61)	;F3	goto
		FlushKeys
		If Not(gui_getmode(55) And 2048)
			gui_setmode(55, gui_getmode(55) + 2048)
			gui_textcursorpos = 0
		EndIf
	ElseIf KeyHit(63)	;F5 left/right position
		If gui_gethandle(1)
			If  ctrlpos = 0
				ctrlpos = GraphicsWidth() - 227
			Else
				ctrlpos = 0
			EndIf
			CtrlWin()
		EndIf
	ElseIf KeyHit(64)
		fatmark = Not fatmark
	ElseIf KeyHit(23) And (KeyDown(29) Or KeyDown(157)) Or gadsel = 70		;ctrl+i gad info
		waitformouse()
		For g.gad = Each gad
			gadcount = gadcount + 1
		Next

		tmp1$ = gadcount + " gadgets" + Chr(31) + "Used IDs: "
		For g.gad = Each gad
			If tmp1 = ""
				tmp1 = g\id
			Else
				tmp1$ = tmp1 + " " + g\id
			EndIf
		Next

		For g.gad = Each gad
			If g\id > max Then max = g\id
		Next

		tmp1 = tmp1 + Chr(31) + Chr(31) + "Free:"
		For i = form + 1 To max
			free = GetFreeID(i)
			If free = i
				tmp1 = tmp1 + " " + i
			EndIf
		Next
		If Right(tmp1, 1) = ":" Then tmp1 = tmp1 + " (none)"
		MsgBox(tmp1, 0, "Info...")

	ElseIf KeyHit(15)	;Tab
		If act <> 0
			g.gad = GetGad(act)
			If KeyDown(42)	;Lshift previous gad
				If g = First gad
					g = Last gad
				Else
					g = Before g
				EndIf
			Else			;next gad
				If g = Last gad
					g = First gad
				Else
					g = After g
				EndIf
			EndIf
			act = g\id
		Else
			If KeyDown(42)
				g = Last gad
			Else
				g = First gad
			EndIf
			If g <> Null
				act = g\id
			Else
				act = 0
			EndIf
		EndIf
		FillCtrl()
	ElseIf gadsel = 42 Or gadsel = 43 Or gadsel = 44	;load image
		tmp1 = gadsel
		waitformouse()
		If act <> 0 Or tmp1 = 42
			gui_filter(0) = ".bmp"
			gui_filter(1) = ".jpg"
			tmp2$ = gui_filerequestor("Load image...", dir_img, "", GraphicsWidth()/2 - 225, GraphicsHeight()/2 - 160)
			FlushMouse()
		Else
			Return
		EndIf

		If act <> 0
			g.gad = GetGad(act)
	
			If tmp1 = 42		;normalimg
				g\ni = tmp2
			ElseIf tmp1 = 43	;overimg
				g\oi = tmp2
			ElseIf tmp1 = 44	;downimg
				g\di = tmp2
			EndIf
			UpdImages(g)
		ElseIf tmp1 = 42
			formimg = tmp2
			UpdImages(Null)
		EndIf

		;checkout path...
		If tmp2 <> ""
			For position = Len(tmp2) - 1 To 1 Step - 1
				If Mid(tmp2, position, 1) = "\"
					path$ = Left(tmp2, position)
					Exit
				EndIf
			Next
			dir_img = path
		EndIf

		gui_redrawwindow(form)
		FillCtrl()
	ElseIf act <> 0 And (gadsel = 45 Or gadsel = 46)	;load sound
		waitformouse()
		tmp1 = gadsel
		gui_filter(0) = ".wav"
		tmp2$ = gui_filerequestor("Load sound...", dir_snd, "", GraphicsWidth()/2 - 225, GraphicsHeight()/2 - 160)
		FlushMouse()
		g.gad = GetGad(act)

		If tmp1 = 45		;oversnd
			g\os = tmp2
		ElseIf tmp1 = 46	;downsnd
			g\ds = tmp2
		EndIf

		If tmp2 <> ""
			For position = Len(tmp2) - 1 To 1 Step - 1
				If Mid(tmp2, position, 1) = "\"
					path$ = Left(tmp2, position)
					Exit
				EndIf
			Next
			dir_snd = path
		EndIf

		UpdSounds(g)
		FillCtrl()
	ElseIf gadsel = 3	;align left/center/right (right needs fix in quickgui)
		If act > 0
			align = GetAlign(act)
			align = align + 1
			If align > 2 Then align = 0
			gui_settext(act, gui_gettext(act), align)
		Else
			align = GetAlign(form)
			align = align + 1
			If align > 2 Then align = 0
			SetWinText(form, gui_gettext(2), align)
		EndIf
		gui_redrawwindow(form)
		FillCtrl()
		waitformouse()

									;gad-modes (left out 128)
	ElseIf gadsel = 5 And act > 0	;2
		g.gad = GetGad(act)
		If gui_gettype(5) = 15
			gui_settypes(5, 16)
			g\noclick = True
		Else
			gui_settypes(5, 15)
			g\noclick = False
		EndIf
		ApplyAll()
		waitformouse()
	ElseIf gadsel = 6 And act > 0	;4
		mode = gui_getmode(act)
		If gui_gettype(6) = 15
			gui_settypes(6, 16)
			If Not(mode And 4) Then mode = mode + 4
		Else
			gui_settypes(6, 15)
			If mode And 4 Then mode = mode - 4
		EndIf
		gui_setmode(act, mode)
		ApplyAll()
		waitformouse()
	ElseIf gadsel = 7 And act > 0	;8
		g.gad = GetGad(act)
		If gui_gettype(7) = 15
			gui_settypes(7, 16)
			g\rightclick = True
		Else
			gui_settypes(7, 15)
			g\rightclick = False
		EndIf
		ApplyAll()
		waitformouse()
	ElseIf gadsel = 8 And act > 0	;16
		mode = gui_getmode(act)
		If gui_gettype(8) = 15
			gui_settypes(8, 16)
			If Not(mode And 16) Then mode = mode + 16
		Else
			gui_settypes(8, 15)
			If mode And 16 Then mode = mode - 16
		EndIf
		gui_setmode(act, mode)
		ApplyAll()
		waitformouse()
	ElseIf gadsel = 9 And act > 0	;32
		mode = gui_getmode(act)
		If gui_gettype(9) = 15
			gui_settypes(9, 16)
			If Not(mode And 32) Then mode = mode + 32
		Else
			gui_settypes(9, 15)
			If mode And 32 Then mode = mode - 32
		EndIf
		gui_setmode(act, mode)
		ApplyAll()
		waitformouse()
	ElseIf gadsel = 10 And act > 0	;64
		mode = gui_getmode(act)
		If gui_gettype(10) = 15
			gui_settypes(10, 16)
			If Not(mode And 64) Then mode = mode + 64
		Else
			gui_settypes(10, 15)
			If mode And 64 Then mode = mode - 64
		EndIf
		gui_setmode(act, mode)
		ApplyAll()
		waitformouse()
	ElseIf gadsel = 11 And act > 0	;256
		g.gad = GetGad(act)
		If gui_gettype(11) = 15
			gui_settypes(11, 16)
			g\inputgadget = True
		Else
			gui_settypes(11, 15)
			g\inputgadget = False
		EndIf
		ApplyAll()
		waitformouse()
	ElseIf KeyHit(18) And (KeyDown(29) Or KeyDown(157)) Or gadsel = 53	;ctrl-e
		waitformouse()
		export()
	ElseIf gadsel = 60	;sample app code
		waitformouse()
		gui_settypes(60, 14)
		gui_settypes(64, 13)
		If exp_modal
			gui_settypes(65, 16)
		Else
			gui_settypes(65, 15)
		EndIf
		If exp_gadselect = 0
			gui_settypes(61, 14)
		ElseIf exp_gadselect = 1
			gui_settypes(62, 14)
		Else
			gui_settypes(63, 14)
		EndIf
		exp_fulapp = True
		gui_redrawwindow(1)
	ElseIf gadsel = 64	;no app code
		waitformouse()
		gui_settypes(64, 14)
		gui_settypes(60, 13)
		gui_settypes(61, 13)
		gui_settypes(62, 13)
		gui_settypes(63, 13)
		gui_settypes(65, 15)
		exp_fulapp = False
		gui_redrawwindow(1)
	ElseIf gadsel = 61	;no selector
		waitformouse()
		gui_settypes(60, 14)
		gui_settypes(64, 13)
		gui_settypes(61, 14)
		gui_settypes(62, 13)
		gui_settypes(63, 13)
		If exp_modal
			gui_settypes(65, 16)
		Else
			gui_settypes(65, 15)
		EndIf
		exp_gadselect = 0
		exp_fulapp = True
		gui_redrawwindow(1)
	ElseIf gadsel = 62	;select/case
		waitformouse()
		gui_settypes(60, 14)
		gui_settypes(64, 13)
		gui_settypes(61, 13)
		gui_settypes(62, 14)
		gui_settypes(63, 13)
		If exp_modal
			gui_settypes(65, 16)
		Else
			gui_settypes(65, 15)
		EndIf
		exp_gadselect = 1
		exp_fulapp = True
		gui_redrawwindow(1)
	ElseIf gadsel = 63	;if/then/else
		waitformouse()
		gui_settypes(60, 14)
		gui_settypes(64, 13)
		gui_settypes(61, 13)
		gui_settypes(62, 13)
		gui_settypes(63, 14)
		If exp_modal
			gui_settypes(65, 16)
		Else
			gui_settypes(65, 15)
		EndIf
		exp_gadselect = 2
		exp_fulapp = True
		gui_redrawwindow(1)
	ElseIf gadsel = 65	;modal
		waitformouse()
		If gui_gettype(65) = 15
			gui_settypes(65, 16)
			gui_settypes(60, 14)
			gui_settypes(64, 13)

			If exp_gadselect = 0
				gui_settypes(61, 14)
			ElseIf exp_gadselect = 1
				gui_settypes(62, 14)
			Else
				gui_settypes(63, 14)
			EndIf

			exp_fulapp = True
			exp_modal = True
		Else
			gui_settypes(65, 15)
			exp_modal = False
		EndIf
		gui_redrawwindow(1)
	ElseIf gadsel = 59	;long IDs, export only
		waitformouse()
		If gui_gettype(59) = 15
			gui_settypes(59, 16)
		Else
			gui_settypes(59, 15)
		EndIf
		gui_redrawwindow(1)
	ElseIf gadsel = 32	;slidergad
		FlushKeys
		If act
			g.gad = GetGad(act)
			waitformouse()
			
			If g\container_for <> 0
				g2.gad = GetGad(g\container_for)
				MsgBox("Gadget " + act + " is already a container for " + g2\id, 0, "Make slider gadget...")
				Return
			EndIf

			If gui_gettype(32) = 15
				gui_settypes(32, 16)
				If Not(gui_getmode(34) And 2048)
					gui_setmode(34, gui_getmode(34) + 2048)	;container-ID
					gui_textcursorpos = 0
					gui_settext(34, "")
				EndIf
			Else
				If g\slider_in <> 0
					g2.gad = GetGad(g\slider_in)
					g2\container_for = 0
				EndIf
				g\slider_in = 0
				g\vertical = False
				gui_settext(34, "")
				gui_settypes(32, 15)
				gui_settext(33, "")
			EndIf

			gui_redrawwindow(1)
		EndIf

	ElseIf gadsel = 33	;vert/horiz slider
		If act
			g.gad = GetGad(act)
			
			waitformouse()
			If g\slider_in
				If gui_gettext(33) = "vertical"
					gui_settext(33, "horizontal")
					g\vertical = False
				Else
					gui_settext(33, "vertical")
					g\vertical = True
				EndIf
			Else
				gui_settext(33, "")
			EndIf
			gui_redrawwindow(1)
		EndIf
	ElseIf gadsel = 68						
		waitformouse()

		If cyclic_ids	;starting at form + 1, no gaps in between, e.g. 2001-2002-2003
			cyclic_ids = False
			gui_settypes(68, 15)
		Else			;noncyclic=IDs only have to be unique, e.g. 2002-2007-2011
			retval = MsgBox("Activating cyclic ID-handling will line up gadget IDs with no gaps in between. " + Chr(31) + "Continue?", 1, "Make IDs cyclic...")
			If retval = 1
				cyclic_ids = True
				gui_settypes(68, 16)
				SortIDs()
			EndIf
		EndIf
		gui_redrawwindow(1)
	ElseIf KeyHit(21) And (KeyDown(29) Or KeyDown(157)) Or gadsel = 69	;ctrl-z reload
		waitformouse()
		LoadForm(frmfile)
	EndIf
End Function

Function GetWinText$(name)
	For w.gui_gadget = Each gui_gadget
		If w\windownum = name And w\num = 0
			Return w\textstr$
		EndIf
	Next
End Function

Function GetWinType(name)
	For w.gui_gadget = Each gui_gadget
		If w\num = 0 And w\windownum = name
			Return w\typ
		EndIf
	Next
End Function

Function LoadForm(fn$)
	If fn <> "tempfile" Then frmfile = fn

	If FileType(dir_open + fn) = 0
		CtrlWin()
		Return
	EndIf

	For g.gad = Each gad
		FreeImage g\nih
		FreeImage g\oih
		FreeImage g\dih
		FreeSound g\osh
		FreeSound g\dsh
		Delete g
	Next
	gui_freewindow(form)
	act = 0

	file = ReadFile(dir_open + fn)
	While Not Eof(file)
		zeile$ = ReadLine(file)
		separatorposition = Instr(zeile, "=")
		wert$ = Mid(zeile, separatorposition + 1, -1)

		Select Trim(Mid(zeile, 1, separatorposition - 1))
			Case "cyclic_ids"
				cyclic_ids = wert
			Case "ctrlpos"
				ctrlpos = wert
			Case "form"
				form = wert
				If form < 100 Then form = 100
			Case "w_x"
				w_x = wert
			Case "w_y"
				w_y = wert
			Case "w_width"
				w_width = wert
			Case "w_height"
				w_height = wert
			Case "w_type"
				w_type = wert
				gui_makewindow(form, w_width, w_height, 0, w_type)
			Case "w_img"
				formimg = wert
				If formimg <> "" Then UpdImages(Null)
			Case "w_text"
				txt$ = wert
			Case "w_align"
				align = wert
				SetWinText(form, txt, align)
			Case "gadget"
				id = wert
			Case "noclick"
				noclick = wert
			Case "rightclick"
				rightclick = wert
			Case "inputgadget"
				inputgadget = wert
			Case "slider_in"
				slider_in = wert
			Case "vertical"
				vertical = wert
			Case "x"
				x = wert
			Case "y"
				y = wert
			Case "width"
				width = wert
			Case "height"
				height = wert
			Case "mode"
				mode = wert
			Case "nt"
				nt = wert
			Case "ot"
				ot = wert
			Case "dt"
				dt = wert
			Case "text"
				txt$ = wert
			Case "align"
				align = wert
			Case "ni"
				ni$ = wert
				If ni <> "" And Instr(ni, "\") = 0 Then ni = dir_app + ni
			Case "oi"
				oi$ = wert
				If oi <> "" And Instr(oi, "\") = 0 Then oi = dir_app + oi
			Case "di"
				di$ = wert
				If di <> "" And Instr(di, "\") = 0 Then di = dir_app + di
			Case "os"
				os$ = wert
				If os <> "" And Instr(os, "\") = 0 Then os = dir_app + os
			Case "ds"
				ds$ = wert
				If ds <> "" And Instr(ds, "\") = 0 Then ds = dir_app + ds

				gui_makegadget(form, id, x, y, width, height, mode, nt, txt, align)
				gui_settypes(id, nt, ot, dt)
				g.gad = New gad
				g\noclick = noclick
				g\rightclick = rightclick
				g\inputgadget = inputgadget
				g\slider_in = slider_in
				g\vertical = vertical
				g\id = id
				g\ni = ni
				g\oi = oi
				g\di = di
				g\os = os
				g\ds = ds
				If g\ni <> "" Or g\oi <> "" Or g\di <> "" Then UpdImages(g)
				If g\os <> "" Or g\ds <> "" Then UpdSounds(g)
		End Select
	Wend
	gui_drawwindow(form, w_x, w_y)
	CloseFile(file)

	For g.gad = Each gad
		If g\slider_in
			tmpgad.gad = GetGad(g\slider_in)
			tmpgad\noclick = True
			tmpgad\container_for = g\id
		EndIf
	Next

	CtrlWin()
	AppTitle frmfile + " - VisualB"
End Function

Function LoadINI()
	If FileType(dir_app + "visualb.ini") = 0
		firststart = True
		file = WriteFile(dir_app + "visualb.ini")	;new ini
			WriteLine(file, "dir_app=" + dir_app)
			WriteLine(file, "dir_open=" + dir_app)
			WriteLine(file, "dir_img=" + dir_img)
			WriteLine(file, "dir_snd=" + dir_snd)
			WriteLine(file, "exp_fulapp=" + exp_fulapp)
			WriteLine(file, "exp_modal=" + exp_modal)
			WriteLine(file, "exp_gadselect=" + exp_gadselect)
			WriteLine(file, "fatmark=" + fatmark)
			WriteLine(file, "lastform=")
		CloseFile(file)
	EndIf

	file = ReadFile(dir_app + "visualb.ini")
		While Not Eof(file)
			zeile$ = ReadLine(file)
			separator = Instr(zeile, "=")
			Wert$ = Mid(zeile, separator + 1, -1)
	
			Select Trim(Mid(zeile, 1, separator - 1))
			Case "dir_app"
				dir_app = wert
			Case "dir_open"
				dir_open = wert
			Case "dir_img"
				dir_img = wert
			Case "dir_snd"
				dir_snd = wert
			Case "exp_fulapp"
				exp_fulapp = wert
			Case "exp_modal"
				exp_modal = wert
			Case "exp_gadselect"
				exp_gadselect = wert
			Case "fatmark"
				fatmark = wert
			Case "lastform"
				If wert = "" Or FileType(dir_open + wert) = 0
					frmfile = "unnamed"
					form = 100
					gui_makewindow(form, 400, 300, 0, 1)
					gui_drawwindow(form, 260, 60)
					
					act = 0
					FillCtrl()
				Else
					frmfile = wert
				EndIf
			End Select
		Wend
	CloseFile(file)
	LoadForm(frmfile)
	Return firststart
End Function

Function MsgBox$(msg$, typ=0, title$="Message")
;types 0=Ok, 1=Ok/Cancel, 2=Yes/No/Cancel
	If gui_Gethandle(1000) Then gui_freewindow(1000)

	;limit title
	If Len(title) > 150 Then title = Left(title, 140) + "..."

	;min width
	If typ = 1
		If width < 210 Then width = 210
	ElseIf typ = 2
		If width < 310 Then width = 310
	Else
		If width < 100 Then width = 100
	EndIf

	;temp subdiv
	originalmsg$ = msg
	j = 1
	pos = Instr(msg, Chr(31))
	While pos
		While pos > 150
			For i = 150 To 1 Step -1
				pos = Instr(msg, " ", i)
				If pos < 150 Then Exit
			Next
			tmp$ = Left(msg, pos)
			If width < StringWidth(tmp) Then width = StringWidth(tmp)
			msg = Right(msg, Len(msg) - pos)
			pos = Instr(msg, Chr(31))
			j = j + 1
		Wend

		tmp = Left(msg, pos - 1)
			If width < StringWidth(tmp) Then width = StringWidth(tmp)
		msg = Right(msg, Len(msg) - pos)
		pos = Instr(msg, Chr(31))
		j = j + 1
	Wend

	If msg <> ""
		While Len(msg) > 150
			For i = 150 To 1 Step -1
				pos = Instr(msg, " ", i)
				If pos < 150 Then Exit
			Next
			tmp = Left(msg, pos)
			If width < StringWidth(tmp) Then width = StringWidth(tmp)
			msg = Right(msg, Len(msg) - pos)
			j = j + 1
		Wend
		If msg <> ""
			If width < StringWidth(msg) Then width = StringWidth(msg)
		EndIf
	EndIf

	If width < StringWidth(title) Then width = StringWidth(title)
	If width > GraphicsWidth() * .78  Then width = GraphicsWidth() * .78
	height = j * StringHeight(tmp) * 1.5 + 65

	gui_makewindow(1000, width + 16, height, 0, 1)
	gui_makegadget(1000, 1001, 2, 2, width + 13, 18, 7, 7, title, 1)
		gui_settypes(1001, 7, 0, 0)

	If typ = 0
		gui_makegadget(1000, 1003, width/2 - 35, height - 30, 82, 20, 1, 1, "Ok", 0)
			gui_settypes(1003, 1, 0, 0)
	ElseIf typ = 1
		gui_makegadget(1000, 1003, width/2 - 80, height - 30, 82, 20, 1, 1, "Ok", 0)
			gui_settypes(1003, 1, 0, 0)
		gui_makegadget(1000, 1004, width/2 + 15, height - 30, 82, 20, 1, 1, "Cancel", 0)
			gui_settypes(1004, 1, 0, 0)
	Else
		gui_makegadget(1000, 1003, width/2 - 130, height - 30, 82, 20, 1, 1, "Yes", 0)
			gui_settypes(1003, 1, 0, 0)
		gui_makegadget(1000, 1005, width/2 - 33, height - 30, 82, 20, 1, 1, "No", 0)
			gui_settypes(1005, 1, 0, 0)
		gui_makegadget(1000, 1004, width/2 + 65, height - 30, 82, 20, 1, 1, "Cancel", 0)
			gui_settypes(1004, 1, 0, 0)
	EndIf

	;actual subdiv
	msg = originalmsg
	j = 1
	pos = Instr(msg, Chr(31))
	While pos
		While pos > 150
			For i = 150 To 1 Step -1
				pos = Instr(msg, " ", i)
				If pos < 150 Then Exit
			Next
			tmp = Left(msg, pos)
			gui_makegadget(1000, 1010 + j, 4, 10 + j * 15, width + 10, 15, 7, 8, tmp, 1)
			msg = Right(msg, Len(msg) - pos)
			pos = Instr(msg, Chr(31))
			j = j + 1
		Wend

		tmp = Left(msg, pos - 1)
		gui_makegadget(1000, 1010 + j, 4, 10 + j * 15, width + 10, 15, 7, 8, tmp, 1)
		msg = Right(msg, Len(msg) - pos)
		pos = Instr(msg, Chr(31))
		j = j + 1
	Wend

	If msg <> ""
		While Len(msg) > 150
			For i = 150 To 1 Step -1
				pos = Instr(msg, " ", i)
				If pos < 150 Then Exit
			Next
			tmp = Left(msg, pos)
			gui_makegadget(1000, 1010 + j, 4, 10 + j * 15, width + 10, 15, 7, 8, tmp, 1)
			msg = Right(msg, Len(msg) - pos)
			j = j + 1
		Wend
		If msg <> ""
			gui_makegadget(1000, 1010 + j, 4, 10 + j * 15, width + 10, 15, 7, 8, msg, 1)
		EndIf
	EndIf

	gui_drawwindow(1000, GraphicsWidth()/2 - width/2, GraphicsHeight()/2 - height/2)
	
	Repeat
		Cls
		gadsel = gui_update(1000)
		Flip

		If gadsel=1003 Or gadsel=1004 Or gadsel=1005 Or KeyHit(28) Or KeyHit(1)
			FlushKeys
			FlushMouse
			waitformouse
			gui_freewindow(1000)

			If gadsel = 1003 Or KeyDown(28)		;ok-yes-ENTER
				Return 1
			ElseIf gadsel = 1004 Or KeyDown(1)	;cancel-ESC
				Return 0
			ElseIf gadsel = 1005				;no
				Return 2
			EndIf
		EndIf
	Forever
End Function

Function SaveForm(fn$)
	If fn <> "tempfile" Then frmfile = fn
	file = WriteFile(dir_open + fn)
		WriteLine(file, "cyclic_ids=" + cyclic_ids)
		WriteLine(file, "ctrlpos=" + ctrlpos)
		WriteLine(file, "form=" + form)
		WriteLine(file, "w_x=" + gui_getwinx(form))
		WriteLine(file, "w_y=" + gui_getwiny(form))
		WriteLine(file, "w_width=" + gui_getwinw(form))
		WriteLine(file, "w_height=" + gui_getwinh(form))
		WriteLine(file, "w_type=" + GetWinType(form))
		WriteLine(file, "w_img=" + formimg)
		WriteLine(file, "w_text=" + GetWinText(form))
		WriteLine(file, "w_align=" + GetAlign(form))

		For g.gad = Each gad
			WriteLine(file, "gadget=" + g\id)
			WriteLine(file, "noclick=" + g\noclick)
			WriteLine(file, "rightclick=" + g\rightclick)
			WriteLine(file, "inputgadget=" + g\inputgadget)
			WriteLine(file, "slider_in=" + g\slider_in)
			WriteLine(file, "vertical=" + g\vertical)
			WriteLine(file, "x=" + gui_getx(g\id))
			WriteLine(file, "y=" + gui_gety(g\id))
			WriteLine(file, "width=" + gui_getw(g\id))
			WriteLine(file, "height=" + gui_geth(g\id))
			WriteLine(file, "mode=" + gui_getmode(g\id))
			WriteLine(file, "nt=" + gui_gettype(g\id))
			WriteLine(file, "ot=" + GetOverType(g\id))
			WriteLine(file, "dt=" + GetDownType(g\id))
			WriteLine(file, "text=" + gui_gettext(g\id))
			WriteLine(file, "align=" + GetAlign(g\id))
			WriteLine(file, "ni=" + g\ni)
			WriteLine(file, "oi=" + g\oi)
			WriteLine(file, "di=" + g\di)
			WriteLine(file, "os=" + g\os)
			WriteLine(file, "ds=" + g\ds)
		Next
	CloseFile(file)

	AppTitle frmfile + " - VisualB"
	gui_settext(57, frmfile, 1)
	gui_redrawwindow(1)
End Function

Function SaveINI()
	file = WriteFile(dir_app + "visualb.ini")
		WriteLine(file, "dir_app=" + dir_app)
		WriteLine(file, "dir_open=" + dir_open)
		WriteLine(file, "dir_img=" + dir_img)
		WriteLine(file, "dir_snd=" + dir_snd)
		WriteLine(file, "exp_fulapp=" + exp_fulapp)
		WriteLine(file, "exp_modal=" + exp_modal)
		WriteLine(file, "exp_gadselect=" + exp_gadselect)
		WriteLine(file, "fatmark=" + fatmark)

		If frmfile = "unnamed"
			WriteLine(file, "lastform=")
		Else
			WriteLine(file, "lastform=" + frmfile)
		EndIf
	CloseFile(file)
End Function

Function SetFormID(nf)
;1000 used for MsgBox, 1-99 for CtrlWin
	If nf = 1000 Or nf = 1001 Or nf < 100
		MsgBox(nf + " can't be used as new form ID. Values < 100, and 1000/1001 are reserved. Going back to " + form, 0, "Change form ID...")
		nf = form
	EndIf
	act = 0

	For g.gad = Each gad
		oldid = g\id			;save old ID
		g\id = g\id - form		;remove form-offset
		g\id = g\id + nf		;add new form-offset

			;check out if container/slider
			If g\slider_in <> 0
				g2.gad = GetGad(g\slider_in)
				g2\container_for = g\id
			EndIf
			
			If g\container_for <> 0
				g2.gad = GetGad(g\container_for)
				g2\slider_in = g\id
			EndIf
	
		SwapIDs(oldid, g\id)
	Next

	For a.gui_gadget = Each gui_gadget
		If a\windownum = form
			a\windownum = nf
		EndIf
	Next

	form = nf
	FillCtrl()
End Function

Function SetGadID()
	newid = Int(gui_gettext(31))
	If cyclic_ids
		For g.gad = Each gad
			If g\id > maxid Then maxid = g\id
		Next
		If newid > maxid
			FillCtrl()
			Return
		EndIf
	EndIf

	g.gad = GetGad(act)
	If newid = g\id Then Return

	If cyclic_ids
		If newid < g\id
			tmpid = GetFreeID(g\id)
			For i = g\id - 1 To newid Step -1
				tmpgad.gad = GetGad(i)
				tmpgad\id = tmpgad\id + 1
				
				If tmpgad\slider_in <> 0
					g2.gad = GetGad(tmpgad\slider_in)
					g2\container_for = tmpgad\id
				EndIf
				
				If tmpgad\container_for <> 0
					g2.gad = GetGad(tmpgad\container_for)
					g2\slider_in = tmpgad\id
				EndIf
				
				If i = g\id - 1
					SwapIDs(i, tmpid)
				Else
					SwapIDs(i, i + 1)
				EndIf
			Next
			oldid = g\id
			g\id = newid
			SwapIDs(oldid, newid)
			SwapIDs(tmpid, oldid)
			Insert g Before tmpgad
			act = newid
		ElseIf newid > g\id
			;old->temp
			tmpid = GetFreeID(g\id)
			oldid = g\id
			SwapIDs(oldid, tmpid)
			g\id = tmpid
			If g\slider_in
				g2.gad = GetGad(g\slider_in)
				g2\container_for = g\id
			EndIf
			If g\container_for
				g2.gad = GetGad(g\container_for)
				g2\slider_in = g\id
			EndIf
			
			;shift intermediate ids
			For i = oldid + 1 To newid
				tmpgad.gad = GetGad(i)
				tmpgad\id = tmpgad\id - 1

				If tmpgad\slider_in
					g2.gad = GetGad(tmpgad\slider_in)
					g2\container_for = tmpgad\id
				EndIf
				If tmpgad\container_for
					g2.gad = GetGad(tmpgad\container_for)
					g2\slider_in = tmpgad\id
				EndIf
				SwapIDs(i, i - 1)
			Next
			
			;temp->new
			SwapIDs(g\id, newid)
			g\id = newid
			If g\slider_in
				g2.gad = GetGad(g\slider_in)
				g2\container_for = newid
			EndIf
			If g\container_for
				g2.gad = GetGad(g\container_for)
				g2\slider_in = newid
			EndIf
			Insert g After tmpgad
			act = newid
		EndIf
	Else
		tmp = GetFreeID(newid)
		If tmp <> newid		;not free
			msgtext$ = ""
			For i = form + 1 To newid
				tmp = GetFreeID(i)
				If tmp = i
					msgtext = msgtext + " " + i
					count = count + 1
				EndIf
				If count > 5 Then Exit
			Next
			If count = 0 Then msgtext = GetFreeID(form + 1)
			MsgBox("The number you've chosen is already in use. Use instead one of these: " + msgtext, 0, "Selecting new Gadget ID...")
			gui_settext(31, g\id)		;undo all
		Else				;free
			If g\slider_in <> 0
				g2.gad = GetGad(g\slider_in)
				g2\container_for = tmp
			EndIf
			
			If g\container_for <> 0
				g2.gad = GetGad(g\container_for)
				g2\slider_in = tmp
			EndIf

			g\id = tmp
			SwapIDs(act, g\id)
			act = g\id
			FillCtrl()
		EndIf
	EndIf
End Function

Function SetWinText(name, t$="", j=0, tx=0, ty=0)
	For a.gui_gadget = Each gui_gadget
		If a\windownum = name And a\num = 0
			a\textstr = t$
			a\justify = j			
			a\text_x = tx
			a\text_y = ty
		EndIf
	Next
End Function

Function SortIDs()
	form = form + 100
	For g.gad = Each gad
		oldid = g\id

		g\id = GetFreeID(form)

		If g\slider_in <> 0
			g2.gad = GetGad(g\slider_in)
			g2\container_for = g\id
		EndIf

		If g\container_for <> 0
			g2.gad = GetGad(g\container_for)
			g2\slider_in = g\id
		EndIf
		
		SwapIDs(oldid, g\id)
	Next
	SetFormID(form - 100)

	;containers in front of sliders (drawing order)
	For g = Each gad
		If g\container_for <> 0
			g2.gad = GetGad(g\container_for)
			Insert g Before g2
		EndIf
	Next

	form = form + 100
	For g.gad = Each gad
		oldid = g\id

		g\id = GetFreeID(form)

		If g\slider_in <> 0
			g2.gad = GetGad(g\slider_in)
			g2\container_for = g\id
		EndIf

		If g\container_for <> 0
			g2.gad = GetGad(g\container_for)
			g2\slider_in = g\id
		EndIf
		
		SwapIDs(oldid, g\id)
	Next
	SetFormID(form - 100)
	
	saveform("tempfile")
	LoadForm("tempfile")
	DeleteFile(dir_open + "tempfile")
End Function

Function SwapIDs(oldname, newname)
	If oldname <> newname
		For a.gui_gadget = Each gui_gadget
			If a\num = oldname Then a\num = newname
		Next
	EndIf
End Function

Function UpdImages(g.gad)
	If MouseDown(3) Then Return
	Color 0, 0, 0
	Text 230, 10, "processing images..."
	Flip

	If g <> Null
		width = gui_getw(g\id)
		height = gui_geth(g\id)
		If g\nih <> 0
			FreeImage(g\nih)
			g\nih = 0
		EndIf
	
		If g\oih <> 0
			FreeImage(g\oih)
			g\oih = 0
		EndIf
	
		If g\dih <> 0
			FreeImage(g\dih)
			g\dih = 0
		EndIf
		
		If g\ni <> ""
			g\nih = LoadImage(g\ni)
			If g\nih <> 0
				g\nih = gui_givebutton(g\nih, width, height)
			Else
				g\ni = ""
			EndIf
		EndIf
	
		If g\oi <> ""
			g\oih = LoadImage(g\oi)
			If g\oih <> 0
				g\oih = gui_givebutton(g\oih, width, height)
			Else
				g\oi = ""
			EndIf
		EndIf
	
		If g\di <> ""
			g\dih = LoadImage(g\di)
			If g\dih <> 0
				g\dih = gui_givebutton(g\dih, width, height)
			Else
				g\di = ""
			EndIf
		EndIf
		gui_setimages(g\id, 0, 0, g\nih, g\oih, g\dih)
	Else
		width = gui_getwinw(form)
		height = gui_getwinh(form)
		If formih <> 0
			FreeImage(formih)
			formih = 0
		EndIf
		
		If formimg <> ""
			formih = LoadImage(formimg)
			If formih <> 0
				formih = gui_givebutton(formih, width, height)
			Else
				formimg = ""
			EndIf
		EndIf
		gui_setwindowimage(form, formih)
	EndIf
End Function

Function UpdSounds(g.gad)
	If g\osh <> 0
		FreeSound(g\osh)
		g\osh = 0
	EndIf

	If g\dsh <> 0
		FreeSound(g\dsh)
		g\dsh = 0
	EndIf
	
	If g\os <> ""
		g\osh = LoadSound(g\os)
		If g\osh = 0 Then g\os = ""
	EndIf
	
	If g\ds <> ""
		g\dsh = LoadSound(g\ds)
		If g\dsh = 0 Then g\ds = ""
	EndIf
	
	gui_setsounds(g\id, g\osh, g\dsh)
End Function
