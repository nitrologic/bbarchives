; ID: 1648
; Author: Andres
; Date: 2006-03-27 16:04:35
; Title: Graphical User Interface
; Description: A free GUI for everyone

;Variables
Global AllowedChars$ = "ABCDEFGHIJKLMNOPQRSTUVW’ƒ÷‹XYabcdefghijklmnopqrstuvwı‰ˆ¸xy 1234567890+!" + Chr(34) + "#§%&/()=?`¥<>,.-;:_"
Global ImageMask$ = "255000255"
Global WindowTitleColor$ = "000000000"
Global TextfieldColor$ = "000000000"
Global TextareaColor$ = "000000000"
Global ButtonColor$ = "000000000"
Global LabelColor$ = "000000000"
Global PanelColor$ = "000000000"
Global CheckBoxColor$ = "000000000"
Global RadioColor$ = "000000000"
Global ListboxSelectionColor$ = "100100200"
Global CursorBlinkingSpeed = 3
Global Extension$ = ".bmp"
Global GUIFolder$ = "gui\"
Global GUIMenuFont = LoadFont("Arial", 16, True)

;Images
TotalImages = 8 * 9 + 7
Dim GadgetImage(TotalImages)
	;Titlebar
	For i = 1 To 9
		GadgetImage(i) = LoadImage(GUIFolder$ + "title" + i + Extension$)
	Next
	;Window
	For i = 1 To 9
		GadgetImage(i + 1 * 9) = LoadImage(GUIFolder$ + "window" + (i) + Extension$)
	Next
	;ButtonUp
	For i = 1 To 9
		GadgetImage(i + 2 * 9) = LoadImage(GUIFolder$ + "buttonup" + (i) + Extension$)
	Next
	;ButtonDown
	For i = 1 To 9
		GadgetImage(i + 3 * 9) = LoadImage(GUIFolder$ + "buttondown" + (i) + Extension$)
	Next
	;TextField
	For i = 1 To 9
		GadgetImage(i + 4 * 9) = LoadImage(GUIFolder$ + "textfield" + (i) + Extension$)
	Next
	;Panel
	For i = 1 To 9
		GadgetImage(i + 5 * 9) = LoadImage(GUIFolder$ + "panel" + (i) + Extension$)
	Next
	;Slider
	For i = 1 To 9
		GadgetImage(i + 6 * 9) = LoadImage(GUIFolder$ + "slider" + (i) + Extension$)
	Next
	;TextArea
	For i = 1 To 9
		GadgetImage(i + 7 * 9) = LoadImage(GUIFolder$ + "textarea" + (i) + Extension$)
	Next
	;Checkbox
	GadgetImage(8 * 9 + 1) = LoadImage(GUIFolder$ + "checkboxfalse" + Extension$)
	GadgetImage(8 * 9 + 2) = LoadImage(GUIFolder$ + "checkboxtrue" + Extension$)
	;Radio button
	GadgetImage(8 * 9 + 3) = LoadImage(GUIFolder$ + "radiofalse" + Extension$)
	GadgetImage(8 * 9 + 4) = LoadImage(GUIFolder$ + "radiotrue" + Extension$)
	;SliderButton
	GadgetImage(8 * 9 + 5) = LoadImage(GUIFolder$ + "sliderbutton" + Extension$)
	;Close button
	GadgetImage(8 * 9 + 6) = LoadImage(GUIFolder$ + "closebuttonfalse" + Extension$)
	GadgetImage(8 * 9 + 7) = LoadImage(GUIFolder$ + "closebuttontrue" + Extension$)
	
	For i = 1 + 2 * 9 To TotalImages
		MaskImage GadgetImage(i), Mid$(Imagemask, 1, 3), Mid$(Imagemask, 4, 3), Mid$(Imagemask, 7, 3)
	Next
	
;Gadget IDs
Global WindowID = 1
Global ButtonID = 2
Global LabelID = 3
Global CheckboxID = 4
Global PanelID = 5
Global SliderID = 6
Global TextfieldID = 7
Global RadioID = 8
Global TextareaID = 9
Global CloseButtonID = 10
Global ImageID = 11
Global ListboxID = 12

;Events
Global GuiRuntimeID
Global GuiRuntimeHandler
Global GuiEventSource

Type gadget
	Field id%
	Field handler%
	Field txt$
	Field x%
	Field y%
	Field width%
	Field height%
	Field selected%
	Field visible%
	Field parent%
	Field state#
	Field group%
	Field flag%
	Field image
	Field uptodate
End Type

Function CreateGuiWindow(title$, x, y, w, h)
	For this.gadget = Each gadget
		this\selected = False
	Next
	
	this.gadget = New gadget
		this\id = WindowID
		this\handler = Rand(9999999)
		this\txt = title$
		this\x = x
		this\y = y
		this\width = w
		this\height = h
		this\selected = True
		this\visible = True
		this\state# = False
		this\parent = False
		this\image = CreateImage(w, h, 1):MaskImage this\image, 255, 0, 255
		
	UpdateWindow(this\handler)
	Return this\handler
End Function

Function CreateGuiButton(title$, x, y, w, h, parent)
	this.gadget = New gadget
		this\id = ButtonID
		this\handler = Rand(9999999)
		this\txt$ = title$
		this\x = x
		this\y = y
		this\width = w
		this\height = h
		this\visible = True
		this\state# = False
		this\parent = parent
		
	UpdateWindow(this\parent)
	Return this\handler
End Function

Function CreateGuiCloseButton(parent)
	this.gadget = New gadget
		this\id = CloseButtonID
		this\handler = Rand(9999999)
		this\x = GuiGadgetWidth(parent) - FontHeight() / 2 - ImageWidth(GadgetImage(8 * 9 + 6)) / 2 - 2
		this\y = FontHeight() / 2 - ImageHeight(GadgetImage(8 * 9 + 6)) / 2 + 2
		this\width = ImageWidth(GadgetImage(8 * 9 + 6))
		this\height = ImageHeight(GadgetImage(8 * 9 + 6))
		this\visible = True
		this\state# = False
		this\parent = parent
		
	UpdateWindow(this\parent)
	Return this\handler
End Function

Function CreateGuiLabel(title$, x, y, w, h, parent)
	this.gadget = New gadget
		this\id = LabelID
		this\handler = Rand(9999999)
		this\txt$ = title$
		this\x = x
		this\y = y
		this\width = w
		this\height = h
		this\visible = True
		this\parent = parent
		
	UpdateWindow(this\parent)
	Return this\handler
End Function

Function CreateGuiCheckbox(title$, x, y, w, h, parent)
	this.gadget = New gadget
		this\id = CheckboxID
		this\handler = Rand(9999999)
		this\txt$ = title$
		this\x = x
		this\y = y
		this\width = w
		this\height = h
		this\visible = True
		this\parent = parent
		this\state# = False
		
	UpdateWindow(this\parent)
	Return this\handler
End Function

Function CreateGuiRadioButton(title$, x, y, w, h, parent, group)
	this.gadget = New gadget
		this\id = RadioID
		this\handler = Rand(9999999)
		this\txt$ = title$
		this\x = x
		this\y = y
		this\width = w
		this\height = h
		this\visible = True
		this\parent = parent
		this\state# = False
		this\group = group
		
	UpdateWindow(this\parent)
	Return this\handler
End Function

Function CreateGuiPanel(title$, x, y, w, h, parent)
	this.gadget = New gadget
		this\id = PanelID
		this\handler = Rand(9999999)
		this\txt$ = title$
		this\x = x
		this\y = y
		this\width = w
		this\height = h
		this\visible = True
		this\parent = parent
		
	UpdateWindow(this\parent)
	Return this\handler
End Function

Function CreateGuiSlider(x, y, w, h, parent)
	this.gadget = New gadget
		this\id = SliderID
		this\handler = Rand(9999999)
		this\x = x
		this\y = y
		this\width = w
		this\height = h
		this\visible = True
		this\parent = parent
		this\state# = False
		
	UpdateWindow(this\parent)
	Return this\handler
End Function

Function CreateGuiTextField(title$, x, y, w, h, parent, flag=0)
	this.gadget = New gadget
		this\id = TextFieldID
		this\handler = Rand(9999999)
		this\txt$ = title$
		this\x = x
		this\y = y
		this\width = w
		this\height = h
		this\visible = True
		this\parent = parent
		this\state# = False
		this\flag = flag
		
	UpdateWindow(this\parent)
	Return this\handler
End Function

Function CreateGuiTextArea(title$, x, y, w, h, parent)
	this.gadget = New gadget
		this\id = TextAreaID
		this\handler = Rand(9999999)
		this\txt$ = title$
		this\x = x
		this\y = y
		this\width = w
		this\height = h
		this\visible = True
		this\parent = parent
		this\state# = False
		
	UpdateWindow(this\parent)
	Return this\handler
End Function

Function CreateGuiImage(image, x, y, parent, frame=0)
	this.gadget = New gadget
		this\id = ImageID
		this\handler = image
		this\x = x
		this\y = y
		this\visible = True
		this\parent = parent
		this\state# = frame
		
	UpdateWindow(this\parent)
	Return this\handler
End Function

Function CreateGuiListbox(txt$, x, y, w, h, parent)
	this.gadget = New gadget
		this\id = ListboxID
		this\handler = Rand(9999999)
		this\txt$ = txt$
		this\x = x
		this\y = y
		this\width = w
		this\height = h
		this\visible = True
		this\parent = parent
	UpdateWindow(this\parent)
	Return this\handler
End Function

;---------------------------------------------------------------------------------------------------------------------------------------
;---------------------------------------------------------------------------------------------------------------------------------------
;---------------------------------------------------------------------------------------------------------------------------------------
;---------------------------------------------------------------------------------------------------------------------------------------

Function Refresh()
	For this.gadget = Each gadget
		If this\id = WindowID And this\visible
			If Not this\uptodate ReDrawWindow(this\handler)
			DrawImage this\image, this\x, this\y
		EndIf
	Next
End Function

Function DrawWindow(title$, x, y, w, h)
	DrawImageGroup(1, x, y, w, FontHeight() + 2)
	DrawImageGroup(10, x, y + FontHeight(), w, h - FontHeight())
	Color Mid$(WindowTitleColor$, 1, 3), Mid$(WindowTitleColor$, 4, 3), Mid$(WindowTitleColor$, 7, 3)
	Text x + 6, y + FontHeight() / 2, title$, False, True
End Function

Function DrawButton(title$, x, y, w, h, state)
	If state
		DrawImageGroup(28, x, y, w, h)
	Else
		DrawImageGroup(19, x, y, w, h)
	EndIf
	Color Mid$(ButtonColor$, 1, 3), Mid$(ButtonColor$, 4, 3), Mid$(ButtonColor$, 7, 3)
	Text x + w / 2 + state, y + h / 2 + state, title$, True, True
End Function

Function DrawCloseButton(x, y, state)
	If state DrawImage GadgetImage(8 * 9 + 7), x, y Else DrawImage GadgetImage(8 * 9 + 6), x, y
End Function


Function DrawLabel(title$, x, y, w, h)
	Color Mid$(LabelColor$, 1, 3), Mid$(LabelColor$, 4, 3), Mid$(LabelColor$, 7, 3)
	Text x, y + h / 2, title$, False, True
End Function

Function DrawCheckbox(title$, x, y, w, h, state)
	If state 
		DrawImage GadgetImage(8 * 9 + 2), x, y
	Else
		DrawImage GadgetImage(8 * 9 + 1),x , y
	EndIf
	Color Mid$(CheckBoxColor$, 1, 3), Mid$(CheckBoxColor$, 4, 3), Mid$(CheckBoxColor$, 7, 3)
	Text x + 16, y + h / 2, title$, False, True
End Function

Function DrawRadioButton(title$, x, y, w, h, state)
	If state
		DrawImage GadgetImage(8 * 9 + 4), x, y
	Else
		DrawImage GadgetImage(8 * 9 + 3), x, y
	EndIf
	Color Mid$(RadioColor$, 1, 3), Mid$(RadioColor$, 4, 3), Mid$(RadioColor$, 7, 3)
	Text x + 16, y + h / 2, title$, False, True
End Function

Function DrawPanel(title$, x, y, w, h)
	DrawImageGroup(46, x, y, w, h)
	Color Mid$(PanelColor$, 1, 3), Mid$(PanelColor$, 4, 3), Mid$(PanelColor$, 7, 3)
	HorizontalTileImage(GadgetImage(14), x + 6, y, StringWidth(title$), 1)
	Text x + 6, y, title$, False, True
End Function

Function DrawSlider(x, y, w#, h, state#)
	DrawImageGroup(55, x - 2, y, w + 4, h)
	DrawImage GadgetImage(8 * 9 + 5), x - 2 + w * state, y + h / 2 - ImageHeight(GadgetImage(8 * 9 + 5)) / 2
End Function

Function DrawTextfield(title$, x, y, w, h, flag)
	If flag = 1
		l = Len(title$):title$ = ""
		For i = 1 To l:title$ = title$ + "*":Next
	EndIf
	
	DrawImageGroup(37, x, y, w, h)
	Color Mid$(TextFieldColor$, 1, 3), Mid$(TextFieldColor$, 4, 3), Mid$(TextFieldColor$, 7, 3)
	While StringWidth(title$) => w - 7
		title$ = Right$(title$, Len(title$) - 1)
	Wend
	Text x + 4, y + h / 2, title$, False, True
End Function

Function DrawTextarea(title$, x, y, w, h)
	DrawImageGroup(64, x, y, w, h)
	Color Mid$(TextFieldColor$, 1, 3), Mid$(TextFieldColor$, 4, 3), Mid$(TextFieldColor$, 7, 3)
	
	txt$ = title$
	Repeat
		cur$ = WrapText$(txt$, w)
		txt$ = Right$(txt$, Len(txt$) - Len(cur$))
		Text x + 4, y + 3 + FontHeight() * i, cur$
		i = i + 1
	Until txt$ = ""
End Function

Function DrawListbox(txt$, x, y, w, h, state)
	DrawImageGroup(64, x, y, w, h)
	
	count = StringCount(txt$, Chr(13)) + 1
	For i = 0 To count - 1
		If i + 1 = state
			Color Mid$(ListboxSelectionColor$, 1, 3), Mid$(ListboxSelectionColor$, 4, 3), Mid$(ListboxSelectionColor$, 7, 3)
			Rect x + 2, y + 2 + i * FontHeight(), w - 3, FontHeight()
		EndIf
		Color Mid$(TextFieldColor$, 1, 3), Mid$(TextFieldColor$, 4, 3), Mid$(TextFieldColor$, 7, 3)
		Text x + 3, y + 2 + i * FontHeight(), Sector(txt$, Chr(13), i)
	Next
End Function

;---------------------------------------------------------------------------------------------------------------------------------------
;---------------------------------------------------------------------------------------------------------------------------------------
;---------------------------------------------------------------------------------------------------------------------------------------
;---------------------------------------------------------------------------------------------------------------------------------------

Function UpdateWindow(handler)
	For this.gadget = Each gadget
		If this\handler = handler
			this\uptodate = False
			Return
		EndIf
	Next
End Function

Function ReDrawWindow(handler)
	For this.gadget = Each gadget
		If this\handler = handler
			buffer = GraphicsBuffer()
			SetBuffer ImageBuffer(this\image)
			SetFont GUIMenuFont
			DrawWindow(this\txt$, 0, 0, this\width, this\height)
			
			For that.gadget = Each gadget
				If that\parent = this\handler And that\visible
					Select that\id
						Case ButtonID
							DrawButton(that\txt$, that\x, that\y, that\width, that\height, that\state)
						Case CloseButtonID
							DrawCloseButton(that\x, that\y, that\state)
						Case LabelID
							DrawLabel(that\txt$, that\x, that\y, that\width, that\height)
						Case CheckboxID
							DrawCheckbox(that\txt$, that\x, that\y, that\width, that\height, that\state)
						Case RadioID
							DrawRadiobutton(that\txt$, that\x, that\y, that\width, that\height, that\state)
						Case PanelID
							DrawPanel(that\txt$, that\x, that\y, that\width, that\height)
						Case SliderID
							DrawSlider(that\x, that\y, that\width, that\height, that\state)
						Case TextfieldID
							DrawTextfield(that\txt$, that\x, that\y, that\width, that\height, that\flag)
						Case TextareaID
							DrawTextarea(that\txt$, that\x, that\y, that\width, that\height)
						Case ImageID
							DrawImage that\handler, that\x, that\y, that\state
						Case ListboxID
							DrawListbox(that\txt$, that\x, that\y, that\width, that\height, that\state)
					End Select
				EndIf
			Next
			
			SetBuffer buffer
			this\uptodate = True
			Return
		EndIf
	Next
End Function

Function HideGuiGadget(handler)
	For this.gadget = Each gadget
		If this\handler = handler
			this\visible = False
			UpdateWindow(this\parent)
			Return
		EndIf
	Next
End Function

Function ShowGuiGadget(handler)
	For this.gadget = Each gadget
		If this\handler = handler 
			this\visible = True
			UpdateWindow(this\parent)
			Return
		EndIf
	Next
End Function

Function FreeGuiGadget(handler)
	For this.gadget = Each gadget
		If this\handler = handler Or this\parent = handler
			If this\image FreeImage this\image
			Delete this
		EndIf
	Next
End Function

Function FreeGuiGroup(group)
	For this.gadget = Each gadget
		If this\group = group Delete this
	Next
End Function

Function GuiGadgetX(handler)
	For this.gadget = Each gadget
		If this\handler = handler Return this\x
	Next
End Function

Function GuiGadgetY(handler)
	For this.gadget = Each gadget
		If this\handler = handler Return this\y
	Next
End Function

Function GuiGadgetWidth(handler)
	For this.gadget = Each gadget
		If this\handler = handler Return this\width
	Next
End Function

Function GuiGadgetHeight(handler)
	For this.gadget = Each gadget
		If this\handler = handler Return this\height
	Next
End Function

Function GuiGadgetParent(handler)
	For this.gadget = Each gadget
		If this\handler = handler Return this\parent
	Next
End Function

Function GuiGadgetText$(handler)
	For this.gadget = Each gadget
		If this\handler = handler Return this\txt$
	Next
End Function

Function SetGuiGadgetText(handler, title$)
	For this.gadget = Each gadget
		If this\handler = handler
			this\txt$ = title$
			UpdateWindow(this\parent)
			Return
		EndIf
	Next
End Function

Function GuiGadgetGroup#(handler)
	For this.gadget = Each gadget
		If this\handler = handler Return this\group
	Next
End Function

Function GuiGadgetState#(handler)
	For this.gadget = Each gadget
		If this\handler = handler Return this\state#
	Next
End Function

Function SetGuiGadgetState(handler, state#)
	For this.gadget = Each gadget
		If this\handler = handler 
			this\state = state#
			UpdateWindow(this\parent)
			Return
		EndIf
	Next
End Function

Function SetGuiGadgetShape(handler, x, y, w, h)
	For this.gadget = Each gadget
		If this\handler = handler
			this\x = x
			this\y = y
			this\width = w
			this\height = h
			UpdateWindow(this\parent)
			Return
		EndIf
	Next
End Function

Function GuiGadgetExistance(handler)
	For this.gadget = Each gadget
		If this\handler = handler Return True
	Next
End Function

Function GuiGadgetHidden(handler)
	For this.gadget = Each gadget
		If this\handler = handler Then Return (1 - this\visible)
	Next
End Function

Function GuiGadgetFlag(handler)
	For this.gadget = Each gadget
		If this\handler = handler Then Return this\flag
	Next
End Function

Function SelectedGuiWindow()
	For this.gadget = Each gadget
		If this\id = WindowID And this\selected Return this\handler
	Next
End Function

Function SelectGuiWindow(handler)
	For this.gadget = Each gadget
		this\selected = False
	Next
	For this.gadget = Each gadget
		If this\handler = handler And this\id = WindowID
			this\selected = True
			Insert this After Last gadget
		EndIf
	Next
End Function

;---------------------------------------------------------------------------------------------------------------------------------------
;---------------------------------------------------------------------------------------------------------------------------------------
;---------------------------------------------------------------------------------------------------------------------------------------
;---------------------------------------------------------------------------------------------------------------------------------------

Function Runtime(MX, MY, MXS, MYS, MH)
	GuiEventSource = False
	Key = GetKey()
	
	; Textfield tabbing
	If key = 9
		If GuiRuntimeID = TextfieldID
			For this.gadget = Each gadget
				If nxt And this\id = TextfieldID
					GuiRuntimeHandler = this\handler
					Exit
				EndIf
				If this\handler = GuiRuntimeHandler
					nxt = True
				EndIf
			Next
		EndIf
	EndIf
	
	If MH
		;Window
		For this.gadget = Each gadget
			If this\id = WindowID And this\visible
				If RectsOverlap(this\x, this\y, this\width, this\height, MX, MY, 1, 1)
					WHandler = this\handler
					GuiRuntimeID = WindowID
					GuiRuntimeHandler = WHandler
				EndIf
			EndIf
		Next
		SelectGuiWindow(WHandler)

		;Gadget
		For this.gadget = Each gadget
			If this\parent = WHandler And this\visible
				If RectsOverlap(GuiGadgetX(WHandler) + this\x, GuiGadgetY(WHandler) + this\y, this\width, this\height, MX, MY, 1, 1)
					GuiRuntimeID = this\id
					GuiRuntimeHandler = this\handler
				EndIf
			EndIf
		Next
		
		Select GuiRuntimeID
			Case ButtonID
				SetGuiGadgetState(GuiRuntimeHandler, True)
			Case CloseButtonID
				SetGuiGadgetState(GuiRuntimeHandler, True)
			Case CheckboxID
				SetGuiGadgetState(GuiRuntimeHandler, 1 - GuiGadgetState(GuiRuntimeHandler))
				GuiEventSource = GuiRuntimeHandler
			Case RadioID
				For that.gadget = Each gadget
					If that\group = GuiGadgetGroup(GuiRuntimeHandler) Then SetGuiGadgetState(that\handler, False)
				Next
				SetGuiGadgetState(GuiRuntimeHandler, True)
				GuiEventSource = GuiRuntimeHandler
			Case LabelID, PanelID
				GuiRuntimeID = WindowID
				GuiRuntimeHandler = GuiGadgetParent(GuiRuntimeHandler)
			Case ListboxID
				For that.gadget = Each gadget
					If that\handler = GuiRuntimeHandler
						If Not GuigadgetText(GuiRuntimeHandler) = "" Then s = Ceil((MouseY() - (GuiGadgetY(GuiRuntimeHandler) + GuiGadgetY(GuigadgetParent(GuiRuntimeHandler)) + 4)) / FontHeight()) + 1
						If s =< StringCount(GuigadgetText(GuiRuntimeHandler), Chr(13)) + 1 Then that\state = s
						ReDrawWindow(GuigadgetParent(GuiRuntimeHandler))
					EndIf
				Next
		End Select
	EndIf
	
	If MouseDown(1)
		Select GuiRuntimeID
			Case WindowID
				SetGuiGadgetShape(GuiRuntimeHandler, GuiGadgetX(GuiRuntimeHandler) + MXS, GuiGadgetY(GuiRuntimeHandler) + MYS, GuiGadgetWidth(GuiRuntimeHandler), GuiGadgetHeight(GuiRuntimeHandler))
				If GuiGadgetX(GuiRuntimeHandler) < 0 SetGuiGadgetShape(GuiRuntimeHandler, 0, GuiGadgetY(GuiRuntimeHandler), GuiGadgetWidth(GuiRuntimeHandler), GuiGadgetHeight(GuiRuntimeHandler))
				If GuiGadgetY(GuiRuntimeHandler) < 0 SetGuiGadgetShape(GuiRuntimeHandler, GuiGadgetX(GuiRuntimeHandler), 0, GuiGadgetWidth(GuiRuntimeHandler), GuiGadgetHeight(GuiRuntimeHandler))
				If GuiGadgetX(GuiRuntimeHandler) + GuiGadgetWidth(GuiRuntimeHandler) > GraphicsWidth() SetGuiGadgetShape(GuiRuntimeHandler, GraphicsWidth() - GuiGadgetWidth(GuiRuntimeHandler), GuiGadgetY(GuiRuntimeHandler), GuiGadgetWidth(GuiRuntimeHandler), GuiGadgetHeight(GuiRuntimeHandler))
				If GuiGadgetY(GuiRuntimeHandler) + GuiGadgetHeight(GuiRuntimeHandler) > GraphicsHeight() SetGuiGadgetShape(GuiRuntimeHandler, GuiGadgetX(GuiRuntimeHandler), GraphicsHeight() - GuiGadgetHeight(GuiRuntimeHandler), GuiGadgetWidth(GuiRuntimeHandler), GuiGadgetHeight(GuiRuntimeHandler))
				
			Case ButtonID
				If RectsOverlap(GuiGadgetX(GuiGadgetParent(GuiRuntimeHandler)) + GuiGadgetX(GuiRuntimeHandler), GuiGadgetY(GuiGadgetParent(GuiRuntimeHandler)) + GuiGadgetY(GuiRuntimeHandler), GuiGadgetWidth(GuiRuntimeHandler), GuiGadgetHeight(GuiRuntimeHandler), MX, MY, 1, 1)
					If Not GuiGadgetState(GuiRuntimeHandler) Then SetGuiGadgetState(GuiRuntimeHandler, True)
				Else
					If GuiGadgetState(GuiRuntimeHandler) Then SetGuiGadgetState(GuiRuntimeHandler, False)
				EndIf
			Case CloseButtonID
				If RectsOverlap(GuiGadgetX(GuiGadgetParent(GuiRuntimeHandler)) + GuiGadgetX(GuiRuntimeHandler), GuiGadgetY(GuiGadgetParent(GuiRuntimeHandler)) + GuiGadgetY(GuiRuntimeHandler), GuiGadgetWidth(GuiRuntimeHandler), GuiGadgetHeight(GuiRuntimeHandler), MX, MY, 1, 1)
					If Not GuiGadgetState(GuiRuntimeHandler) Then SetGuiGadgetState(GuiRuntimeHandler, True)
				Else
					If GuiGadgetState(GuiRuntimeHandler) Then SetGuiGadgetState(GuiRuntimeHandler, False)
				EndIf
			Case SliderID
				If MXS Or MYS Or MH
					state# = Float (MX - GuiGadgetX(GuiGadgetParent(GuiRuntimeHandler)) - GuiGadgetX(GuiRuntimeHandler)) / GuiGadgetWidth(GuiRuntimeHandler)
					If state < 0 Then state = 0 ElseIf state > 1 Then state = 1
					If Not GuiGadgetState(GuiRuntimeHandler) = state# SetGuiGadgetState(GuiRuntimeHandler,  state#)
					GuiEventSource = GuiRuntimeHandler
				EndIf
		End Select
	Else
		Select GuiRuntimeID
			Case WindowID
				FreeRuntime()
			Case ButtonID
				SetGuiGadgetState(GuiRuntimeHandler, False)
				If RectsOverlap(GuiGadgetX(GuiGadgetParent(GuiRuntimeHandler)) + GuiGadgetX(GuiRuntimeHandler), GuiGadgetY(GuiGadgetParent(GuiRuntimeHandler)) + GuiGadgetY(GuiRuntimeHandler), GuiGadgetWidth(GuiRuntimeHandler), GuiGadgetHeight(GuiRuntimeHandler), MX, MY, 1, 1) GuiEventSource = GuiRuntimeHandler
				FreeRuntime()
			Case CloseButtonID
				SetGuiGadgetState(GuiRuntimeHandler, False)
				If RectsOverlap(GuiGadgetX(GuiGadgetParent(GuiRuntimeHandler)) + GuiGadgetX(GuiRuntimeHandler), GuiGadgetY(GuiGadgetParent(GuiRuntimeHandler)) + GuiGadgetY(GuiRuntimeHandler), GuiGadgetWidth(GuiRuntimeHandler), GuiGadgetHeight(GuiRuntimeHandler), MX, MY, 1, 1)
					FreeGuiGadget(GuiGadgetParent(GuiRuntimeHandler))
				EndIf
				FreeRuntime()
			Case TextfieldID
				If MilliSecs() Mod (1000 / CursorBlinkingSpeed * 2) < (1000 / CursorBlinkingSpeed)
					txt$ = GuiGadgetText(GuiRuntimeHandler):l = Len(txt$)
					If GuiGadgetFlag(GuiRuntimeHandler) = 1 Then:txt$ = "":For i = 1 To l:txt$ = txt$ + "*":Next:EndIf
					w = StringWidth(txt$)
					If w > GuiGadgetWidth(GuiRuntimeHandler) - 8 w = GuiGadgetWidth(GuiRuntimeHandler) - 8
					Color Mid$(TextFieldColor$, 1, 3), Mid$(TextFieldColor$, 4, 3), Mid$(TextFieldColor$, 7, 3)
					Rect GuiGadgetX(GuiGadgetParent(GuiRuntimeHandler)) + GuiGadgetX(GuiRuntimeHandler) + 4 + w, GuiGadgetY(GuiGadgetParent(GuiRuntimeHandler)) + GuiGadgetY(GuiRuntimeHandler) + 3, 1, GuiGadgetHeight(GuiRuntimeHandler) - 6
				EndIf
				If Instr(AllowedChars$, Chr(Key))
					SetGuiGadgetText(GuiRuntimeHandler, GuiGadgetText(GuiRuntimeHandler) + Chr(Key))
				ElseIf Key = 8
					txt$ = GuiGadgetText(GuiRuntimeHandler)
					If Len(txt$) > 0 Then SetGuiGadgetText(GuiRuntimeHandler, Left$(txt$, Len(txt$) - 1))
				EndIf
			Case TextareaID
				If MilliSecs() Mod (1000 / CursorBlinkingSpeed * 2) < (1000 / CursorBlinkingSpeed)
					Color Mid$(TextareaColor$, 1, 3), Mid$(TextareaColor$, 4, 3), Mid$(TextareaColor$, 7, 3)
					txt$ = GuiGadgetText(GuiRuntimeHandler)
					w = GuiGadgetWidth(GuiRuntimeHandler)
					h = GuiGadgetHeight(GuiRuntimeHandler)
					x = GuiGadgetX(GuiGadgetParent(GuiRuntimeHandler)) + GuiGadgetX(GuiRuntimeHandler)
					y = GuiGadgetY(GuiGadgetParent(GuiRuntimeHandler)) + GuiGadgetY(GuiRuntimeHandler)
					Repeat
						cur$ = WrapText$(txt$, w)
						txt$ = Right$(txt$, Len(txt$) - Len(cur$))
						If txt$ = "" Exit
						yy = yy + 1
					Forever
					If 3 + yy * FontHeight() - FontHeight() < height
						Rect x + 4 + StringWidth(cur$), y + 3 + yy * FontHeight(), 1, FontHeight()
					EndIf
				EndIf
				If Instr(AllowedChars$, Chr(Key))
					SetGuiGadgetText(GuiRuntimeHandler, GuiGadgetText(GuiRuntimeHandler) + Chr(Key))
				ElseIf Key = 13
					SetGuiGadgetText(GuiRuntimeHandler, GuiGadgetText(GuiRuntimeHandler) + Chr(Key))
				ElseIf Key = 8
					txt$ = GuiGadgetText(GuiRuntimeHandler)
					If Len(txt$) > 0 Then SetGuiGadgetText(GuiRuntimeHandler, Left$(txt$, Len(txt$) - 1))
				EndIf
			Default
				FreeRuntime()
		End Select
		
	EndIf
End Function

Function FreeRuntime()
	GuiRuntimeID = False
	GuiRuntimeHandler = False
End Function

Function DrawImageGroup(start, x, y, w, h)
	start = start - 1
	DrawImage GadgetImage(start + 1), x, y
	DrawImage GadgetImage(start + 3), x + w - ImageWidth(GadgetImage(start + 3)), y
	DrawImage GadgetImage(start + 7), x, y + h - ImageHeight(GadgetImage(start + 7))
	DrawImage GadgetImage(start + 9), x + w - ImageWidth(GadgetImage(start + 9)), y + h - ImageHeight(GadgetImage(start + 9))
	
	ww =  w - ImageWidth(GadgetImage(start + 3))
	hh = h - ImageHeight(GadgetImage(start + 7))
	xtime = (ww / ImageWidth(GadgetImage(start + 5)))
	ytime = (hh / ImageHeight(GadgetImage(start + 5)))
	
	;Top/Bottom side
	time = (ww / ImageWidth(GadgetImage(start + 2)))
	For xx = 0 To time
		DrawImageRect GadgetImage(start + 2), x + xx * ImageWidth(GadgetImage(start + 2)) + ImageWidth(GadgetImage(start + 1)), y, 0, 0, ww - xx * ImageWidth(GadgetImage(start + 2)) - ImageHeight(GadgetImage(start + 3)), ImageHeight(GadgetImage(start + 2))
	Next
	For xx = 0 To xtime
		DrawImageRect GadgetImage(start + 8), x + xx * ImageWidth(GadgetImage(start + 8)) + ImageWidth(GadgetImage(start + 1)), y + h - ImageHeight(GadgetImage(start + 7)), 0, 0, ww - xx * ImageWidth(GadgetImage(start + 8)) - ImageHeight(GadgetImage(start + 9)), ImageHeight(GadgetImage(start + 8))
	Next
	
	;Left/Right side
	time = (hh / ImageHeight(GadgetImage(start + 4)))
	For yy = 0 To ytime
		DrawImageRect GadgetImage(start + 4), x, y + ImageHeight(GadgetImage(start + 1)) + yy* ImageHeight(GadgetImage(start + 4)), 0, 0, ImageWidth(GadgetImage(start + 4)), hh - yy * ImageHeight(GadgetImage(start + 4)) - ImageHeight(GadgetImage(start + 7))
	Next
	For yy = 0 To ytime
		DrawImageRect GadgetImage(start + 6), x + w - ImageWidth(GadgetImage(start + 9)), y + ImageHeight(GadgetImage(start + 1)) + yy* ImageHeight(GadgetImage(start + 4)), 0, 0, ImageWidth(GadgetImage(start + 4)), hh - yy * ImageHeight(GadgetImage(start + 4)) - ImageHeight(GadgetImage(start + 7))
	Next
	
	;Middle
	For yy = 0 To ytime
		For xx = 0 To xtime
			DrawImageRect GadgetImage(start + 5), x + xx * ImageWidth(GadgetImage(start + 5)) + ImageWidth(GadgetImage(start + 1)), y + yy * ImageHeight(GadgetImage(start + 5)) + ImageHeight(GadgetImage(start + 1)), 0, 0, ww - xx * ImageWidth(GadgetImage(start + 5)) - ImageWidth(GadgetImage(start + 9)), hh - yy * ImageHeight(GadgetImage(start + 5)) - ImageHeight(GadgetImage(start + 9))
		Next
	Next
End Function

Function HorizontalTileImage(Image, x, y, width, height)
	time = width / ImageWidth(Image)
	For xx = 0 To time
		DrawImageRect Image, x + xx * ImageWidth(Image), y, 0, 0, width - xx * ImageWidth(Image), height
	Next
End Function

Function WrapText$(txt$, width)
	If Len(txt$)
		width = width - 16
		Repeat
			cur$ = cur$ + Mid$(txt, 1, 1)
			txt$ = Mid$(txt, 2)
			
			If Right$(cur$, 1) = " " result$ = cur$
			If txt$ = "" 
				result$ = cur$
				Exit
			EndIf
			
			If Right$(cur$, 1) = Chr(13)
				cur$ = Left$(cur$, Len(cur$) - 1) + "  "
				Exit
			EndIf
		Until StringWidth(cur$) => width
		
		If result$ = ""
			result$ = Left$(cur$, Len(cur$) - 1)
		EndIf
		
		Return result$
	EndIf
End Function

Function StringCount(txt$, needle$)
	i = 1
	While Instr(txt$, needle$, i)
		i = Instr(txt$, needle$, i) + Len(needle$)
		a = a + 1
	Wend
	Return a
End Function

Function Sector$(txt$, separator$, sector%)
	Local result$ = "", occ
	For i = 1 To Len(txt$)
		If Mid$(txt$, i, 1) = separator$
			occ = occ + 1
		Else
			If occ = sector Then result$ = result$ + Mid$(txt$, i, 1)
		EndIf
		If occ > sector Then Exit
	Next
	Return result$
End Function
