; ID: 2022
; Author: b32
; Date: 2007-05-29 17:39:18
; Title: textfield
; Description: text area/memo/textfield/notepad

;keywords are defined at the bottom, it doesn't recognize strings "". Comments are lines that start with //
;-------------------------------------------------------------------------------------------------------
;												Globals etc
;-------------------------------------------------------------------------------------------------------
	
	;allowed characters
	Global 		abc$ = "<>{}1234567890-=QWERTYUIOP[]ASDFGHJKL;'\ZXCVBNM,./* 789-456+1230.,/?!@#$%^&():" + Chr$(34)

	;number of lines	
	Global		numlines
	Global 		ActiveText.TTextField
	
	Global		Cursor_X, Cursor_Y
	Global		curx, cury, curline.TLine
	
	Dim 		Cursor_Hit(2)

	;highlighted keywords (see ReadKeyWords)
	Type KeyWord
		Field s$
	End Type
	
	;selection type	
	Type TSelection
		Field l.Tline
		Field c
	End Type
		
	Dim tsel.TSelection(2)
	
	For i = 1 To 2
		tsel(i) = New TSelection
	Next
	
	;storage of lines
	Type TStorage
		Field s$
		Field id
		Field t.TTextField
	End Type

	;line type		
	Type TLine
		Field s$
		Field id
	End Type
	
	;textfield type
	Type TTextField
		Field x
		Field y
		Field width
		Field height
		Field font
		Field CharWidth
		Field CharHeight
		Field ofx, ofy
		Field passwordmask$
		Field limitchars
		Field noenter
		
		Field curx, cury
		Field curline.TLine

		Field backgroundcolor
		Field bordercolor
		Field textcolor
		Field sel_backgroundcolor
		Field sel_textcolor
		Field cursorcolor
		Field commentcolor
		Field keywordcolor
						
		Field idle
	End Type
	
	ReadKeyWords()




;-------------------------------------------------------------------------------------------------------
;												Test Program
;-------------------------------------------------------------------------------------------------------

	Graphics 800, 600, 0, 2
	SetBuffer BackBuffer()

	;create textfield	
	t2.TTextField = CreateTextField(50, 320, 80, 15, 10, "*", 1)
	t1.TTextField = CreateTextField(50, 50, 700, 240)
		
	;main loop
	Repeat
		
		Cls
		
		DrawTextFields()
		
		;F1/F2		
		If KeyHit(59) Then SaveTextFile("test.txt")
		If KeyHit(60) Then LoadTextFile("test.txt")
		
		Flip
		
	Until KeyHit(1)
	
	End


	
;-------------------------------------------------------------------------------------------------------
;												DrawTextFields()
;-------------------------------------------------------------------------------------------------------
Function DrawTextFields()

	Cursor_X = MouseX()
	Cursor_Y = MouseY()
	Cursor_Hit(1) = MouseHit(1)

	test = 0
	For t.TTextField = Each TTextField
		DrawTextField(t)
		If RectsOverlap(Cursor_X, Cursor_Y, 1, 1, t\x, t\y, t\width, t\height) And Cursor_Hit(1) Then
			SetActiveText t
			test = 1
		End If
	Next
	
	If Cursor_Hit(1) And (test = 0) Then SetActiveText Null

End Function

;-------------------------------------------------------------------------------------------------------
;											CreateTextField()
;-------------------------------------------------------------------------------------------------------
Function CreateTextField.TTextField(x, y, ww$, hh$, limitchars = 0, pwmask$ = "", noenter = 0)

	t.TTextField = New TTextField
	
	;position
	t\x = x
	t\y = y
	t\width = ww
	t\height = hh
	
	;font
	t\font = LoadFont("Blitz")
	SetFont t\font
	
	;font size
	t\CharWidth = StringWidth("X")
	t\CharHeight = StringHeight("X")
	
	;scroll
	t\ofx = 0
	t\ofy = 0
	
	t\limitchars = limitchars
	t\passwordmask$ = pwmask$
	t\noenter = noenter

	t\backgroundcolor		= $225588
	t\bordercolor			= $555555
	t\textcolor				= $FFFFFF
	t\keywordcolor			= $AADDFF
	t\sel_backgroundcolor	= $DDAA77
	t\sel_textcolor			= $000000
	t\cursorcolor			= $DDAA77
	t\commentcolor			= $FFEE00
		
	t\idle = CreateImage(t\width, t\height)

	SetMarker(1, 0, 0)
	SetMarker(2, 0, 0)

	SetActiveText t
		
	Return t
	
End Function

;-------------------------------------------------------------------------------------------------------
;											DrawTextField()
;-------------------------------------------------------------------------------------------------------
Function DrawTextField(t.TTextField, update = 0)

	If (t <> ActiveText) And (Not update) Then
		DrawBlock t\idle, t\x, t\y
		Return
	End If
	
	limitchars = t\limitchars

	curline.TLine = t\curline
	curx = t\curx
	cury = t\cury
	If limitchars > 0 Then If curx > limitchars Then curx = limitchars
	If t\noenter Then If cury > 0 Then cury = 0

	;max width/height in characters
	maxchar = (t\width / t\CharWidth)
	maxlines = (t\height / t\CharHeight)

	;scroll textfield
	If cury - t\ofy >= maxlines Then t\ofy = cury - maxlines + 1
	If cury - t\ofy < 0 Then t\ofy = cury
	If curx - t\ofx < 0 Then t\ofx = curx
	If curx - t\ofx >= maxchar Then t\ofx = curx - maxchar + 1
	

	;draw frame	
	Color 0, 0, t\backgroundcolor
	Rect t\x, t\y, t\width, t\height
	Color 0, 0, t\bordercolor
	Rect t\x, t\y, t\width, t\height, 0
	
	Viewport t\x, t\y, t\width, t\height

	;determine bottom	
	bottom = t\y + t\height
		
	;get selection
	If tsel(1)\l <> Null Then 
		sel1y = tsel(1)\l\id
		sel1x = tsel(1)\c - t\ofx
	Else
		sel1y = 0
		sel1x = 0
	End If
	
	If tsel(2)\l <> Null Then 
		sel2y = tsel(2)\l\id
		sel2x = 0
	Else
		sel2y = 0
		sel2x = 0
	End If

	sel2x = tsel(2)\c - t\ofx

	If sel1x < 0 Then sel1x = 0
	If sel1x > maxchar Then sel1x = maxchar
	If sel2x < 0 Then sel2x = 0
	If sel2x > maxchar Then sel2x = maxchar

	;determine order		
	If sel2y < sel1y Then
		tempx = sel1x
		tempy = sel1y
		sel1x = sel2x
		sel1y = sel2y
		sel2x = tempx
		sel2y = tempy
	End If
	
	If sel1y = sel2y Then
		If sel2x < sel1x Then
			tempx = sel1x
			tempy = sel1y
			sel1x = sel2x
			sel1y = sel2y
			sel2x = tempx
			sel2y = tempy
		End If
	End If

	;delete lines if needed
	For l.TLine = Each TLine
		If t\noenter Then If l <> First TLine Then Delete l
	Next

	;draw text area
	SetFont t\font
	iy = t\y - t\ofy * t\CharHeight
	init = 0
	For l.TLine = Each TLine
		If limitchars > 0 Then If Len(l\s$ > limitchars) Then l\s$ = Left$(l\s$, limitchars)
		If l\id = t\ofy Then init = 1
		
		linetext$ = l\s$
		If t\passwordmask$ <> "" Then linetext$ = String$(t\passwordmask$, Len(l\s$))
		
		If init Then
		
			;draw text
			If (l\id > sel1y) And (l\id < sel2y) Then

				SelText t, t\x, iy, Mid$(linetext$, t\ofx + 1, maxchar)

			ElseIf (l\id = sel1y) And (l\id < sel2y) Then

				d$ = Mid$(linetext$, t\ofx + 1, maxchar)
								
				d1$ = Left$(d$, sel1x)
				d2$ = Mid$(d$, sel1x + 1)
							
				;BoxText t, t\x, iy, d1$
				BoxText2 t, t\x, iy, linetext$, t\ofx + 1, sel1x
				SelText t, t\x + Len(d1$) * t\CharWidth, iy, d2$

			ElseIf (l\id > sel1y) And (l\id = sel2y) Then

				d$ = Mid$(linetext$, t\ofx + 1, maxchar)
								
				d1$ = Left$(d$, sel2x)
				d2$ = Mid$(d$, sel2x + 1)
							
				;BoxText t, t\x + Len(d1$) * t\CharWidth, iy, d2$
				BoxText2 t, t\x + Len(d1$) * t\CharWidth, iy, linetext$, t\ofx + 1 + sel2x, maxchar - sel2x
				SelText t, t\x, iy, d1$

			ElseIf (l\id = sel1y) And (l\id = sel2y) Then

				d$ = Mid$(linetext$, t\ofx + 1, maxchar)
				d2$ = Mid$(d$, sel1x + 1, sel2x - sel1x)
				
				BoxText2 t, t\x, iy, linetext$, t\ofx + 1, maxchar
				;BoxText t, t\x, iy, d$
				SelText t, t\x + (sel1x * t\CharWidth), iy, d2$

			Else

				;BoxText t, t\x, iy, Mid$(linetext$, t\ofx + 1, maxchar)
				BoxText2 t, t\x, iy, linetext$, t\ofx + 1, maxchar

			End If
						
		End If

		iy = iy + t\CharHeight
		If iy + t\CharHeight > bottom Then Exit

	Next

	;get cursor line
	curline.TLine = GetLine(cury)
	maxdd = Len(curline\s$)
	
	;draw cursor	
	cgx = t\x + (t\CharWidth * (curx - t\ofx))
	cgy = t\y + (t\CharHeight * (cury - t\ofy))
	Color 0, 0, t\sel_backgroundcolor
	If Not update Then Line cgx, cgy, cgx, cgy + t\CharHeight
;	Color 255, 255, 255
;	Text cgx, cgy, Mid$(curline\s$, curx + 1, 1)

	;shift hit
	If KeyHit(42) Then
		SetMarker(1, cury, curx)
		SetMarker(2, cury, curx)
	End If
	
;	;current line size
;	maxdd = Len(curline\s$)
		
	;ctrl
	ctrl = KeyDown(29)
	If ctrl Then
	
		;CTRL+A
		If KeyHit(30) Then
			SetMarker(1, 0, 0)
			l.TLine = GetLine(numlines - 1)
			SetMarker(2, numlines - 1, Len(l\s$))
			FlushKeys()
		End If
		;CTRL+D
		If KeyHit(32) Then
			SetMarker(1, 0, 0)
			SetMarker(2, 0, 0)
			FlushKeys()
		End If
		;CTRL+X
		If KeyHit(45) Then
			WriteClipBoardText(GetSelection$())
			DeleteSel()
			ResetSel()
			FlushKeys()
		End If
		;CTRL+C
		If KeyHit(46) Then
			WriteClipBoardText(GetSelection$())
			ResetSel()
			FlushKeys()
		End If
		;CTRL+V
		If MyKeyHit(47) Then
			If CheckSelected() Then DeleteSel(): ResetSel(1)
			rok$ = ReadClipBoardText$()
			InsertLines(rok$, curx, cury, t\limitchars)
			curline = GetLine(cury)
			maxdd = Len(curline\s$)
			ResetSel()
		End If		

		;home
		If KeyHit(199) Then 
			curx = 0
			cury = 0
			curline = GetLine(cury)
			maxdd = Len(curline\s$)
			ResetSel
		End If
		
		;end
		If KeyHit(207) Then 
			cury = numlines - 1
			curline = GetLine(cury)
			maxdd = Len(curline\s$)
			curx = maxdd
			ResetSel
		End If

	Else		

		;KEYBOARD INPUT	
		ok = GetKey()
		;INSERT
		If MyKeyHit(210) Then ok = 32
		;A-Z keys	
		If ok <> 0 Then
			If curx < 0 Then curx = 0
			If cury < 0 Then cury = 0
			;check against abc$
			If Instr(abc$, Upper$(Chr$(ok))) > 0 Then
				DeleteSel()
				;add character			
				curline\s$ = Left$(curline\s$, curx) + Chr$(ok) + Mid$(curline\s$, curx + 1)
				maxdd = Len(curline\s$)
				curx = curx + 1
				ResetSel(1)
			End If
		End If
		
		;tab
		If MyKeyHit(15) Then
			If CheckSelected() Then 
				TabSelected(0)
			Else
				;add tab
				curline\s$ = Left$(curline\s$, curx) + "    " + Mid$(curline\s$, curx + 1)
				maxdd = Len(curline\s$)
				curx = curx + 4
				ResetSel(1)
			End If
		End If		
		
		;enter
		If MyKeyHit(28) Then
			DeleteSel()
			nl$ = Mid$(curline\s$, curx + 1)
			curline\s$ = Left$(curline\s$, curx)
			l.TLine = AddLine(nl$)
			Insert l After curline
			cury = cury + 1
			curx = 0
			curline = l
			maxdd = Len(curline\s$)
			UpdateLines()
			ResetSel(1)
		End If
		
		;backspace
		If MyKeyHit(14) Then
			If CheckSelected() Then
				DeleteSel()
				ResetSel(1)
			Else
				If curx > 0 Then
					curline\s$ = Left$(curline\s$, curx - 1) + Mid$(curline\s$, curx + 1)
					curx = curx - 1
					maxdd = Len(curline\s$)
					ResetSel(1)
				Else
					If cury > 0 Then
						l.TLine = GetLine(cury - 1)
						curx = Len(l\s$)
						l\s$ = l\s$ + curline\s$
						Delete curline
						numlines = numlines - 1
						UpdateLines()
						cury = cury - 1
						curline = l
						maxdd = Len(l\s$)
						ResetSel(1)
					End If
				End If
			End If
		End If

		;home/end
		If KeyHit(199) Then curx = 0: ResetSel
		If KeyHit(207) Then curx = maxdd: ResetSel
	
	End If

	;pgup	
	If MyKeyHit(201) Then 
		ncury = cury - maxlines
		If ncury < 0 Then ncury = 0
		cury = ncury
		curline = GetLine(cury)
		maxdd = Len(curline\s$)
		ResetSel
	End If
	
	;pgdn
	If MyKeyHit(209) Then 
		ncury = cury + maxlines
		If ncury >= numlines Then ncury = numlines - 1
		cury = ncury
		curline = GetLine(cury)
		maxdd = Len(curline\s$)
		ResetSel
	End If
	
	;left	
	If MyKeyHit(203) Then 
		If ctrl Then
			Repeat
				curx = curx - 1
				If curx < 1 Then Exit
				If Mid$(curline\s$, curx, 1) = " " Then Exit
			Forever
		Else
			curx = curx - 1
		End If
		If curx < 0 Then 
			If cury > 0 Then
				cury = cury - 1
				curline.TLine = GetLine(cury)	
				maxdd = Len(curline\s$)
				curx = maxdd
			Else
				curx = 0
			End If
		End If
		ResetSel
	End If

	;right	
	If MyKeyHit(205) Then 
		If ctrl Then
			Repeat
				curx = curx + 1
				If curx >= maxdd Then Exit
				If Mid$(curline\s$, curx, 1) = " " Then Exit
			Forever
		Else
			curx = curx + 1
		End If
		If curx > maxdd Then 
			If cury < numlines - 1 Then 
				curx = 0
				cury = cury + 1
				curline = GetLine(cury)
				maxdd = Len(curline\s$)
			End If
		End If
		ResetSel
	End If

	;up	
	If MyKeyHit(200) Then 
		cury = cury - 1
		If cury < 0 Then cury = 0
		curline.TLine = GetLine(cury)
		maxdd = Len(curline\s$)
		ResetSel
	End If
	
	;down
	If MyKeyHit(208) Then 
		cury = cury + 1
		If cury >= numlines Then cury = numlines - 1
		curline.TLine = GetLine(cury)
		maxdd = Len(curline\s$)
		ResetSel
	End If
	
	If curx > maxdd Then curx = maxdd
	
	;delete knop
	If KeyHit(211) Then 
		If CheckSelected() Then 
			DeleteSel(): ResetSel(1)
		Else
			If curx >= 0 Then
				curline\s$ = Left$(curline\s$, curx) + Mid$(curline\s$, curx + 2)
				maxdd = Len(curline\s$)
				ResetSel(1)
			End If
		End If
	End If

	t\curline = curline
	t\curx = curx
	t\cury = cury
	
	Viewport 0, 0, GraphicsWidth(), GraphicsHeight()
						
End Function

;-------------------------------------------------------------------------------------------------------
;										      AddLine()
;-------------------------------------------------------------------------------------------------------
Function AddLine.TLine(s$)

	l.TLine = New TLine
	l\s$ = s$
	UpdateLines()
	
	Return l
	
End Function

;-------------------------------------------------------------------------------------------------------
;									       UpdateLines()
;-------------------------------------------------------------------------------------------------------
Function UpdateLines()

	id = 0
	For l.TLine = Each TLine
		l\id = id
		id = id + 1
	Next
	
	numlines = id 
;	curline.TLine = GetLine(cury)
	
End Function

;-------------------------------------------------------------------------------------------------------
;											SetMarker()
;-------------------------------------------------------------------------------------------------------
Function SetMarker(id, liney, char)

	l.TLine = Null
	For il.TLine = Each TLine
		If il\id = liney Then l = il: Exit
	Next
	If l = Null Then Return

	tsel(id)\l = l
	tsel(id)\c = char
	
	If tsel(1)\l = Null Then Return
	If tsel(2)\l = Null Then Return
		
End Function

;-------------------------------------------------------------------------------------------------------
;											   SelText()
;-------------------------------------------------------------------------------------------------------
;draw selected text
Function SelText(t.TTextField, x, y, s$)
	
	ww = StringWidth(s$)
	hh = StringHeight(s$)
	Color 0, 0, t\sel_backgroundcolor
	Rect x, y, ww, hh
	Color 0, 0, t\sel_textcolor
	Text x, y, Replace$(s$, Chr$(13), "")
		
End Function

;-------------------------------------------------------------------------------------------------------
;											   BoxText()
;-------------------------------------------------------------------------------------------------------
;draw non-selected text
Function BoxText(t.TTextField, x, y, s$)

	q$ = Trim$(s$)
	If Left$(q$, 2) = "//" Then 
		Color 0, 0, t\commentcolor
		test = 1
	Else
		Color 0, 0, t\textcolor
		test = 0
	End If
	Text x, y, s$
	
	If test Then Return

	Color 0, 0, t\keywordcolor
	l$ = " " + Lower$(s$)	+ " "
	For k.KeyWord = Each KeyWord
		
		If Instr(l$, Lower$(k\s$)) Then

			test = 0
			Repeat
			
				test = Instr(l$, Lower$(k\s$), test + 1)
				If test < 1 Then Exit
								
				Text x + (test - 1) * t\CharWidth, y, Mid$(s$, test, Len(k\s$) - 2)
				
			Forever
			
		End If
		
	Next
		
End Function


;-------------------------------------------------------------------------------------------------------
;											   BoxText2()
;-------------------------------------------------------------------------------------------------------
;draw non-selected text
Function BoxText2(t.TTextField, x, y, s$, st, ll)

	q$ = Trim$(s$)
	If Left$(q$, 2) = "//" Then 
		Color 0, 0, t\commentcolor
		test = 1
	Else
		Color 0, 0, t\textcolor
		test = 0
	End If
	Text x, y, Mid$(s$, st, ll)
	
	If test Then Return

	Color 0, 0, t\keywordcolor
	l$ = " " + Lower$(s$)	+ " "
	For k.KeyWord = Each KeyWord
		
		If Instr(l$, Lower$(k\s$)) Then

			test = 0
			Repeat
			
				test = Instr(l$, Lower$(k\s$), test + 1)
				If test < 1 Then Exit
				
				x1 = x + (test - st) * t\CharWidth
				ll = (Len(k\s$) - 2) * t\CharWidth
				
				If (x1 + ll >= t\x) And (x1 <= t\x + t\width) Then
					Text x1, y, Mid$(s$, test, Len(k\s$) - 2)
				End If
				
			Forever
			
		End If
		
	Next
		
End Function


;-------------------------------------------------------------------------------------------------------
;												GetLine()
;-------------------------------------------------------------------------------------------------------
;gets a specific line, else creates it
Function GetLine.TLine(i)

	If i < 0 Then i = 0
	If i > 65536 Then i = 65536

	il.TLine = Null
	For l.TLine = Each TLine
		If l\id = i Then il = l: Exit
	Next
	
	If il = Null Then
		Repeat
			il.TLine = AddLine("")
			If il\id = i Then Exit
		Until il\id = 65536
	End If
		
	Return il
	
End Function

;-------------------------------------------------------------------------------------------------------
;												ResetSel()
;-------------------------------------------------------------------------------------------------------
Function ResetSel(rs = 0)

	;shift
	If KeyDown(42) And (Not rs) Then
		SetMarker(2, cury, curx)		
	Else
		If rs Then
			SetMarker(1, 0, 0)
			SetMarker(2, 0, 0)
		Else
			SetMarker(1, cury, curx)		
			SetMarker(2, cury, curx)		
		End If
	End If
	
	FlushKeys()
	
End Function

;-------------------------------------------------------------------------------------------------------
;											   DeleteSel()
;-------------------------------------------------------------------------------------------------------
Function DeleteSel()

	;retreive selection
	If tsel(1)\l <> Null Then sel1y = tsel(1)\l\id Else Return
	If tsel(2)\l <> Null Then sel2y = tsel(2)\l\id Else Return
	
	If (tsel(1)\l = tsel(2)\l) And (tsel(1)\c = tsel(2)\c) Then Return
	
	sel1x = tsel(1)\c
	sel2x = tsel(2)\c

	If sel1x < 0 Then sel1x = 0
	If sel2x < 0 Then sel2x = 0

	;determine order
		
	id1 = 1
	id2 = 2
	
	If sel2y < sel1y Then
		id1 = 2
		id2 = 1
		tempx = sel1x
		tempy = sel1y
		sel1x = sel2x
		sel1y = sel2y
		sel2x = tempx
		sel2y = tempy
	End If
	
	If sel1y = sel2y Then
		If sel2x < sel1x Then
			id1 = 2
			id2 = 1
			tempx = sel1x
			tempy = sel1y
			sel1x = sel2x
			sel1y = sel2y
			sel2x = tempx
			sel2y = tempy
		End If
	End If

	;reset cursor	
	curx = tsel(id1)\c
	cury = tsel(id1)\l\id

	;remove lines in between	
	For l.TLine = Each TLine
		If (l\id > sel1y) And (l\id < sel2y) Then Delete l
	Next

	;same line?
	If tsel(id1)\l = tsel(id2)\l Then
		If (sel1x = 0) And (sel2x = Len(tsel(id2)\l\s$)) Then
			Delete tsel(id1)\l
		Else
			tsel(id1)\l\s$ = Left$(tsel(id1)\l\s$, sel1x) + Mid$(tsel(id1)\l\s$, sel2x + 1)
		End If
	Else
		test = 1

		;cut first line		
		If sel1x = 0 Then 
			Delete tsel(id1)\l
			test = 0
		Else
			tsel(id1)\l\s$ = Left$(tsel(id1)\l\s$, sel1x)			
		End If

		;cut last line		
		If sel2x = Len(tsel(id2)\l\s$) Then
			Delete tsel(id2)\l
			test = 0
		Else
			tsel(id2)\l\s$ = Mid$(tsel(id2)\l\s$, sel2x + 1)
		End If

		;paste together if needed		
		If test Then 
			tsel(id1)\l\s$ = tsel(id1)\l\s$ + tsel(id2)\l\s$
			Delete tsel(id2)\l
		End If
		
	End If

	;update indexes and reset selection		
	UpdateLines()	
	ResetSel(1)
	
	curline = GetLine(cury)
		
End Function

;-------------------------------------------------------------------------------------------------------
;												GetSelection()
;-------------------------------------------------------------------------------------------------------
Function GetSelection$()

	;retreive selection
	If tsel(1)\l <> Null Then sel1y = tsel(1)\l\id Else Return
	If tsel(2)\l <> Null Then sel2y = tsel(2)\l\id Else Return
	
	sel1x = tsel(1)\c
	sel2x = tsel(2)\c
	
	id1 = 1
	id2 = 2
	
	;determine order
	
	If sel2y < sel1y Then
		id1 = 2
		id2 = 1
	End If
	
	If sel1y = sel2y Then
		If sel2x < sel1x Then
			id1 = 2
			id2 = 1
		End If
	End If

	;same line ?		
	If tsel(id1)\l = tsel(id2)\l Then 
		If tsel(id1)\c = tsel(id2)\c Then 
			Return
		Else
			ss = tsel(id1)\c + 1
			ll = tsel(id2)\c - ss + 1
			Return Mid$(tsel(id1)\l\s$, ss, ll)
		End If
	End If

	;get selection
	s$ = ""
	init = 0
	For l.TLine = Each TLine
		If l = tsel(id2)\l Then 
			s$ = s$ + Left$(tsel(id2)\l\s$, tsel(id2)\c)
			init = 0
		End If
		If init Then
			s$ = s$ + l\s$ + Chr$(13)
		End If
		If l = tsel(id1)\l Then 
			s$ = s$ + Mid$(tsel(id1)\l\s$, tsel(id1)\c + 1) + Chr(13)
			init = 1
		End If
	Next

	;return selection	
	Return s$

End Function


;-----------------------------------------------------------------------------------------------------
;											WriteClipBoardText()
;-----------------------------------------------------------------------------------------------------
Function WriteClipBoardText(txt$)

	If txt$="" Then Return 
	
	txt$ = Replace$(txt$, Chr$(13), Chr$(13) + Chr$(10))
		
	Local cb_TEXT=1
	If OpenClipboard(0)
		EmptyClipboard
		SetClipboardData cb_TEXT,txt$
		CloseClipboard
	EndIf
	
	FreeBank txtbuffer
	
End Function

;-----------------------------------------------------------------------------------------------------
;											  ReadClipBoardText()
;-----------------------------------------------------------------------------------------------------
Function ReadClipBoardText$()

	Local cb_TEXT=1
	Local txt$=""
	If OpenClipboard(0)
		If ExamineClipboard(cb_TEXT) 
			txt$=GetClipboardData$(cb_TEXT)
		EndIf
		CloseClipboard
	EndIf
	txt$ = Replace$(txt$, Chr$(13) + Chr$(10), Chr$(13))
	txt$ = Replace$(txt$, Chr$(9), "    ")
	Return txt$
	
End Function

;-----------------------------------------------------------------------------------------------------
;												InsertLine()
;-----------------------------------------------------------------------------------------------------
Function InsertLine(r$, x, y)

	If Right$(r$, 1) = Chr$(13) Then add = 1: r$ = Left$(r$, Len(r$) - 1)

	If y < 0 Then y = 0
	If x < 0 Then x = 0
	If y > numlines Then y = numlines
	
	l.TLine = GetLine(y)

	If add Then	
		If x > 0 Then 
			r2$ = Mid$(l\s$, x + 1)
			l\s$ = Left$(l\s$, x) + r$
			l2.TLine = AddLine(r2$)
			Insert l2 After l
			curx = 0
			cury = cury + 1
		Else		
			l2.TLine = AddLine(r$)
			Insert l2 Before l
			curx = 0
			cury = cury + 1
		End If
	Else
		l\s$ = Left$(l\s$, x) + r$ + Mid$(l\s$, x + 1)
		curx = curx + Len(r$)
	End If
	
	UpdateLines()
			
End Function		

;-----------------------------------------------------------------------------------------------------
;												InsertLines()
;-----------------------------------------------------------------------------------------------------
Function InsertLines(r$, x, y, limitchars = 0)

	If Instr(r$, Chr$(13)) > 0 Then
		
		Repeat
		
			cc = Instr(r$, Chr$(13))
			If cc = 0 Then Exit
			
			r1$ = Left$(r$, cc)
			r2$ = Mid$(r$, cc + 1)
			InsertLine(r1$, x, y)
			init = 0
			x = 0
			y = y + 1
			r$ = r2$
			
		Forever
	
	End If
	
	InsertLine(r$, x, y)
	
	If limitchars > 0 Then
		For l.TLine = Each TLine
			If Len(l\s$) > limitchars Then l\s$ = Left$(l\s$, limitchars)
		Next
	End If
	
End Function

;-----------------------------------------------------------------------------------------------------
;											UpdateText()
;-----------------------------------------------------------------------------------------------------
;grab screenshot to 'idle' image
Function UpdateText(t.TTextField)

	DrawTextField t, 1
	;store idle
	CopyRect t\x, t\y, t\width, t\height, 0, 0, BackBuffer(), ImageBuffer(t\idle)

End Function

;-----------------------------------------------------------------------------------------------------
;											DeleteTextField()
;-----------------------------------------------------------------------------------------------------
Function DeleteTextField(t.TTextField)

	;free image
	FreeImage t\idle
	
	;delete storage
	For st.TStorage = Each TStorage
		If st\t = t Then Delete st
	Next

	;delete temp lines
	If t = ActiveText Then Delete Each TLine

	;free font	
	FreeFont t\font
	
	;delete type
	Delete t
	
End Function
	

;-----------------------------------------------------------------------------------------------------
;										   SetActiveText()
;-----------------------------------------------------------------------------------------------------
Function SetActiveText(t.TTextField)

	;if another is selected
	If ActiveText <> Null Then 
		;delete storage
		For st.TStorage = Each TStorage
			If st\t = ActiveText Then Delete st
		Next
		;store temp lines
		For l.TLine = Each TLine
			st.TStorage = New TStorage
			st\s$ = l\s$
			st\id = l\id
			st\t = ActiveText
		Next
		;store screenshot
		UpdateText(ActiveText)
	End If
		
	;delete temp lines
	Delete Each TLine

	;select another text
	ActiveText = t
	If ActiveText = Null Then Return
	
	;get lines from storage
	For st.TStorage = Each TStorage
		If st\t = t Then
			l.TLine = New TLine
			l\s$ = st\s$
			l\id = st\id
		End If
	Next
	UpdateLines()

	;reset cursor	
	SetMarker 1, 0, 0
	SetMarker 2, 0, 0
		
End Function

;-----------------------------------------------------------------------------------------------------
;												SaveTextFile()
;-----------------------------------------------------------------------------------------------------
Function SaveTextFile(f$)

	ff = WriteFile(f$)
	For l.TLine = Each TLine
		WriteLine ff, l\s$
	Next
	CloseFile ff
	
End Function

;-----------------------------------------------------------------------------------------------------
;												LoadTextFile()
;-----------------------------------------------------------------------------------------------------
Function LoadTextFile(f$)

	If FileType(f$) <> 1 Then Return

	Delete Each TLine
	ff = ReadFile(f$)
	While Not(Eof(ff))
		AddLine(ReadLine(ff))
	Wend
	CloseFile ff
	
	UpdateLines()

	;reset cursor	
	SetMarker 1, 0, 0
	SetMarker 2, 0, 0
	
End Function

;-----------------------------------------------------------------------------------------------------
;												  MyKeyHit()
;-----------------------------------------------------------------------------------------------------
Global toldkey, timestart
Function MyKeyhit(key)

	If KeyHit(key) Then 
		timestart = MilliSecs()
		Return 1
	End If
	
	If KeyDown(key) Then
		Return (MilliSecs() - timestart) > 500
	End If
	
End Function

;-----------------------------------------------------------------------------------------------------
;												CheckSelected()
;-----------------------------------------------------------------------------------------------------
Function CheckSelected()

	Return (tsel(1)\l <> tsel(2)\l) Or (tsel(1)\c <> tsel(2)\c)
	
End Function


Function TabSelected(tab)

	;retreive selection
	If tsel(1)\l <> Null Then sel1y = tsel(1)\l\id Else Return
	If tsel(2)\l <> Null Then sel2y = tsel(2)\l\id Else Return
	
	sel1x = tsel(1)\c
	sel2x = tsel(2)\c
	
	;determine order	
	If sel2y < sel1y Then
		sel1y = tsel(2)\l\id
		sel2y = tsel(1)\l\id
	End If

	Select tab
	
	Case 0
		;add tabs
		For l.TLine = Each TLine
			If (l\id >= sel1y) And (l\id <= sel2y) Then l\s$ = "    " + l\s$
		Next
		
	Case 1
		;remove tabs
		For l.TLine = Each TLine
			If (l\id >= sel1y) And (l\id <= sel2y) Then 
				If Left$(l\s$, 4) = "    " Then l\s$ = Mid$(l\s$, 5)
			End If
		Next
		
	End Select
	
End Function

;-----------------------------------------------------------------------------------------------------
;												ReadKeyWord()
;-----------------------------------------------------------------------------------------------------
Function ReadKeyWords()

	Restore
	
	Repeat
	
		Read o$
		If o$ = "*STOP*" Then Exit
		
		k.KeyWord = New KeyWord
		k\s$ = " " + o$ + " "
		
	Forever
	
End Function

Data "Else"
Data "Then"
Data "Position"
Data "Move"
Data "Turn"
Data "Locate"
Data "If"
Data "Print"
Data "Call"
Data "Set"
Data "End"
Data "Return"
Data "{"
Data "}"
Data "*STOP*"
