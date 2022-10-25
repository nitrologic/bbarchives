; ID: 2044
; Author: MadMunky
; Date: 2007-06-25 13:50:21
; Title: Improved Dropdown (v2.0)
; Description: Improved version of Yahfree's dropdown.

Graphics 800, 600, 0, 2

;----------------------------------
;             DROPDOWN
;        By Wishbone/Madmunky
;    Special thanks to Yahfree ;)
;
Const DD_UNORDERED = 0, DD_ORDERBYID = 1, DD_ORDERBYVALUE = 2

Global dd_CursorX, dd_CursorY, dd_ListLast$, dd_fontheight
Global dd_MouseHold, dd_screenGFX = CreateImage(GraphicsWidth(), GraphicsHeight())

Type dd_list
	Field id%
	Field valueRaw$
	Field name$, value$
	Field orderedBy%
End Type

Type dd_DropDown
	Field id%
	Field name$, value$
	Field width%, height%
	Field style%
	Field scrolly#, shown#
	Field x%, y%
End Type

Function dd_createDropDown$(dd_id, dd_style=0, dd_height=14)
	While MouseDown(1): Wend
	
	dd_fontheight = FontHeight()+4

	dd_d.dd_DropDown = New dd_DropDown
	dd_d\id = dd_id
	
	i = 0: dd_d\width = 0
	For dd_L.dd_list = Each dd_list
		If dd_L\id = dd_id
			i = i+1
			If dd_d\width < StringWidth(dd_L\value) Then dd_d\width = StringWidth(dd_L\value)
		EndIf
	Next
	dd_d\width = dd_d\width+22
	dd_d\height = dd_height
	dd_heightorig = dd_height
	If dd_d\height > i Or (dd_d\height < i And dd_heightorig > dd_d\height) Then dd_d\height = i
	
	dd_d\x = MouseX()
	dd_d\y = MouseY()-dd_fontheight+2
	If dd_d\x+dd_d\width > GraphicsWidth() Then dd_d\x = GraphicsWidth()-dd_d\width-1
	If dd_d\y+dd_fontheight*(dd_d\height+1) > GraphicsHeight() Then dd_d\y = MouseY()-(dd_fontheight*(dd_d\height+1))-1
	
	dd_d\scrolly = dd_d\y+dd_fontheight
	dd_d\style = dd_style
	dd_d\shown = dd_d\y
	dd_d\name = ""
	dd_d\value = ""
	
	res$ = dd_drawDropDowns()
	Return res$
End Function

Function dd_drawDropDown(dd_d.dd_DropDown, dd_md=False)
	dd_id = dd_d\id
	dd_scrolly# = dd_d\scrolly
	dd_shown# = dd_d\shown
	dd_x = dd_d\x
	dd_y = dd_d\y
	dd_name$ = dd_d\name$
	dd_value$ = dd_d\value$
	dd_style = dd_d\style
	dd_width = dd_d\width
	dd_height = dd_d\height
	dd_heightorig = dd_d\height
	dd_colR = 0
	dd_colG = 0
	dd_colB = 0
	dd_buttonwidth = 15
	dd_hold = False
	
	If Not(dd_md) Then dd_MouseHold = False

	Select dd_style
		Case 0: c1=255: c2=128
		Case 1: c1=224: c2=64
	End Select
	Color c1, c1, c1
	Rect dd_x-1, dd_y+dd_fontheight-2, dd_width-dd_buttonwidth+2, dd_height*dd_fontheight+3
	
	Color c2, c2, c2
	Rect dd_x-1, dd_y+dd_fontheight-2, dd_width-dd_buttonwidth+2, dd_height*dd_fontheight+3, False
	Line dd_x, dd_y+(dd_height+1)*dd_fontheight+1, dd_x+dd_width-dd_buttonwidth, dd_y+(dd_height+1)*dd_fontheight+1
	
	Line dd_x+dd_width-dd_buttonwidth+1, dd_y+dd_fontheight-1, dd_x+dd_width-dd_buttonwidth+1, dd_y+(dd_height+1)*dd_fontheight+1
	
	st = -1
	k$=Lower$(Chr$(GetKey()))
	If k$ >= "a" And k$ <= "z"
		i=0
		For dd_l.dd_list = Each dd_list
			If dd_l\id=dd_id
				i1 = 1
				Repeat
					k1$=Lower$(Mid$(dd_l\value, i1, 1))
					i1 = i1+1
				Until i1 > Len(dd_l\value) Or (k1$ >= "a" And k1$ <= "z")
				If k1$ = k$
					st = i*dd_fontheight
					Exit
				ElseIf k1$ < k$
					st = i*dd_fontheight
				EndIf
				i=i+1
			EndIf
		Next
	EndIf

	Viewport dd_x, dd_y+dd_fontheight, dd_width-dd_buttonwidth, dd_height*dd_fontheight-1
	i=0
	For dd_l.dd_list = Each dd_list
		If dd_l\id=dd_id
			i=i+1
			If RectsOverlap(dd_x, dd_shown+(i*dd_fontheight), dd_width-dd_buttonwidth, dd_fontheight, dd_CursorX, dd_CursorY, 1, 1)
				If dd_l\value<>"\s"
					If RectsOverlap(dd_x, dd_y+dd_fontheight, dd_width-dd_buttonwidth, dd_height*dd_fontheight-1, dd_CursorX, dd_CursorY, 1, 1)
						Color 0, 0, 128
						Rect dd_x+1, dd_shown+(i*dd_fontheight), dd_width-dd_buttonwidth-2, dd_fontheight
						If dd_MouseHold = False
							If dd_md And MouseDown(2) = False
								While MouseDown(1): Wend
								dd_md = False
								dd_name$=dd_l\name
								dd_value$=dd_l\value
								dd_shown=dd_y
								dd_scrolly=dd_y+dd_fontheight
								Exit
							EndIf
						EndIf
						Color c1, c1, c1
					EndIf
				EndIf
			Else
				Color dd_colR, dd_colG, dd_colB
			End If
			If dd_l\value="\s"
				Color 192, 192, 192
				Line dd_x+4, dd_shown+((i+0.5)*dd_fontheight), dd_x+dd_width-dd_buttonwidth-5, dd_shown+((i+0.5)*dd_fontheight)
			Else
				t = Instr(dd_l\value, "\t")
				If t > 0
					v1$ = Mid$(dd_l\value, 1, t-1)
					v2$ = Mid$(dd_l\value, t+2)
				Else
					v1$ = dd_l\value
				EndIf
				
				Text dd_x+4, dd_shown+(i*dd_fontheight)+2, v1$
				If t > 0 Then Text dd_x+dd_width-dd_buttonwidth-StringWidth(v2$)-4, dd_shown+(i*dd_fontheight)+2, v2$
			EndIf
		End If
	Next
	
	Viewport 0, 0, GraphicsWidth(), GraphicsHeight()
	If i>dd_height
		mzs = MouseZSpeed(): mz = MouseZ()
		If mz = 0 And (mzs < -1 Or mzs > 1) Then mzs = 0 ;Stupid Windows bug

		Color c1, c1, c1
		Rect dd_x+dd_width-dd_buttonwidth, dd_y+dd_fontheight-2, dd_buttonwidth+1, dd_height*dd_fontheight+3, True
		Color c2, c2, c2
		Rect dd_x+dd_width-dd_buttonwidth, dd_y+dd_fontheight-2, dd_buttonwidth+1, dd_height*dd_fontheight+3, False
		Line dd_x+dd_width-dd_buttonwidth, dd_y+(dd_height+1)*dd_fontheight+1, dd_x+dd_width, dd_y+(dd_height+1)*dd_fontheight+1
		Line dd_x+dd_width+1, dd_y+dd_fontheight-1, dd_x+dd_width+1, dd_y+(dd_height+1)*dd_fontheight+1
		
		Color 192, 192, 192
		Rect dd_x+dd_width-dd_buttonwidth+2, dd_scrolly, dd_buttonwidth-3, dd_fontheight-1, True
		If dd_md
			If RectsOverlap(dd_x+dd_width-dd_buttonwidth, dd_y+dd_fontheight, dd_buttonwidth, dd_height*dd_fontheight, dd_CursorX, dd_CursorY, 1, 1)
				dd_MouseHold = True
			EndIf
		EndIf
		If dd_MouseHold = True Then dd_scrolly = MouseY() - dd_yo
		dd_scrolly = dd_scrolly-mzs*8
		
		dd_ScrollbarMax# = dd_y+dd_height*dd_fontheight-dd_y-dd_fontheight
		dd_ListSize = (i - dd_height) * dd_fontheight
		If st <> -1
			dd_shown = dd_y - st
			dd_ScrollbarPos# = (dd_y - dd_shown) * (dd_scrollbarmax / dd_ListSize) + dd_fontheight
			dd_scrolly = dd_y + dd_ScrollbarPos
		EndIf
		If dd_scrolly < dd_y+dd_fontheight Then dd_scrolly=dd_y+dd_fontheight
		If dd_scrolly > dd_y+dd_height*dd_fontheight Then dd_scrolly=dd_y+dd_height*dd_fontheight
		dd_ScrollbarPos# = dd_ScrollY - dd_y - dd_fontheight
		dd_shown = dd_y - (1.0 * dd_scrollbarpos * dd_ListSize / dd_scrollbarmax)
	End If
	
	dd_d\scrolly = dd_scrolly
	dd_d\shown = dd_shown
	dd_d\name$ = dd_name$
	dd_d\value$ = dd_value$
End Function

Function dd_drawDropDowns$()
	While MouseDown(1) Or MouseDown(2): Wend
	
	GrabImage dd_screenGFX, 0, 0
	Repeat
		DrawBlock dd_screenGFX, 0, 0
		dd_CursorX = MouseX()
		dd_CursorY = MouseY()
		dd_md = (MouseDown(1) Or MouseDown(2))
		dd_name$ = "~"
		For dd_d1.dd_DropDown = Each dd_DropDown
			dd_drawDropDown(dd_d1.dd_DropDown, dd_md)
			dd_name$ = dd_d1\name$
		Next
		If ((dd_md) And dd_MouseHold = False) Or KeyDown(1) Or (dd_name$ <> "" And dd_name$ <> "~")
			While MouseDown(1) Or MouseDown(2): Wend
			FlushMouse: FlushKeys
			dd_deleteDropDowns()
		EndIf
		
		Flip
		Delay 5
	Until dd_name$<>""
	DrawBlock dd_screenGFX, 0, 0
	If dd_name$ = "~" Then Return "" Else Return dd_name$
End Function

Function dd_deleteDropDowns()
	For dd_d1.dd_DropDown = Each dd_DropDown
		dd_deleteList(dd_d1\id)
		Delete dd_d1
		Return True
	Next
	Return False
End Function

Function dd_getDropDownName$()
	For dd_d1.dd_DropDown = Each dd_DropDown
		Return dd_d1.dd_DropDown\name$
	Next
	Return ""
End Function

Function dd_getDropDownValue$()
	For dd_d1.dd_DropDown = Each dd_DropDown
		Return dd_d1.dd_DropDown\value$
	Next
	Return ""
End Function

;dd_orderedBy
;0 = unordered
;1 = name
;2 = value
Function dd_addToList.dd_list(dd_id, dd_name$, dd_value$, dd_orderedBy=DD_UNORDERED)
	For l1.dd_list = Each dd_list
		If l1\id = dd_id And l1\name = dd_name Then Return Null
	Next
	l.dd_list = New dd_list
	l\id = dd_id
	l\orderedBy = dd_orderedBy
	l\valueRaw = dd_value
	l\name = dd_name
	l\value = dd_filterString(dd_value)
	
	For l1.dd_list = Each dd_list
		Select l\orderedBy
			Case 1:
			If Lower$(l\name) < Lower$(l1\name) Then Insert l Before l1: Exit
			Case 2:
			If Lower$(l\valueRaw) < Lower$(l1\valueRaw) Then Insert l Before l1: Exit
		End Select
	Next

	Return l
End Function

Function dd_deleteList(dd_id)
	For l.dd_list = Each dd_list
		If l\id = dd_id Then Delete l
	Next
End Function

Function dd_filterString$(s$)
	i=1: While Mid$(s$, i, 1) = "!": i = i+1: Wend
	Return Mid$(s$, i)
End Function
;
;         END OF DROPDOWN
;----------------------------------


;----------------------------------
;          EXAMPLE CODE
;
SeedRnd MilliSecs()

SetBuffer BackBuffer()

font = LoadFont("Courier New", 15, True)
font1 = LoadFont("Arial", 14)
font2 = LoadFont("Arial", 24)

ClsColor 224, 224, 224

While Not KeyHit(1)
	Cls
	
	SetFont font
	Color 0, 0, 0
	Text 65, 50, "Click box to open dropdown #1"
	Text 465, 50, "Click box to open dropdown #2"
	Text 65, 100, "Click box to open dropdown #3"
	
	Color 255, 0, 0
	Rect 50, 50, 12, 12, True
	Rect 450, 50, 12, 12, True
	Rect 50, 100, 12, 12, True
	
	If MouseHit(1)
		
		;Dropdown example #1
		;The "\t" in the dd_addToList makes all following text align to the right.
		;The "\s" in the dd_addToList generates a line separator.
		;The second parameter in the dd_CreateDropDown function is the dropdown style. Only 0 or 1 at the moment.
		;The third parameter in the dd_CreateDropDown function is the length of the dropdown.
		If RectsOverlap(50, 50, 12, 12, MouseX(), MouseY(), 1, 1)
			dd_addToList(1, "cut", "Cut\t(Ctrl+X)")
			dd_addToList(1, "copy", "Copy\t(Ctrl+C)")
			dd_addToList(1, "paste", "Paste\t(Ctrl+V)")
			dd_addToList(1, "", "\s")
			dd_addToList(1, "edit", "Edit\t(Ctrl+E)")
			SetFont font1
			dd$ = dd_createDropDown(1, 1, 5): dn = 1
		EndIf

		;Dropdown example #2
		If RectsOverlap(450, 50, 12, 12, MouseX(), MouseY(), 1, 1)
			For i = 1 To 100
				dd_addToList(2, i, "Item #"+i)
			Next
			SetFont font1
			dd$ = dd_createDropDown(2, 0, 8): dn = 2
		EndIf
		
		;Dropdown example #3
		;The fourth parameter in the dd_addToList makes it order by the IDs of the list (in this case; A, B, C, etc...)
		If RectsOverlap(50, 100, 12, 12, MouseX(), MouseY(), 1, 1)
			dd_addToList(3, "D", "1 Daisy", DD_ORDERBYID)
			dd_addToList(3, "B", "2 Bert", DD_ORDERBYID)
			dd_addToList(3, "C", "3 Charlotte", DD_ORDERBYID)
			dd_addToList(3, "A", "4 Artie", DD_ORDERBYID)
			dd_addToList(3, "E", "5 Edward", DD_ORDERBYID)
			SetFont font2
			dd$ = dd_createDropDown(3): dn = 3
		EndIf
	
	EndIf
	If dd$ <> ""
		SetFont font
		Color 255, 0, 0
		Text 400, 300, "The selected ID is '"+dd$+"' from dropdown #"+dn, True, True
	EndIf
	Flip
Wend
;
;        END OF EXAMPLE CODE
;----------------------------------
