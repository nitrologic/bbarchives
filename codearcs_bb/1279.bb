; ID: 1279
; Author: MattVonFat
; Date: 2005-02-05 04:37:41
; Title: Multi-Column ListBox
; Description: As used in outlook to display e-mails.

;------------------------------------------------------
;CREATES A LISTVIEW
Function CreateListView(x%,y%,width%,height%,parent%)
	
	bank = CreateBank(12)
	canvas = CreateCanvas(x,y,width,height,parent)
	
	PokeInt bank, 0, canvas
	PokeInt bank, 4, x%
	PokeInt bank, 8, y%
		
	UpdateListView(bank)
	
	Return bank
	
End Function

;------------------------------------------------------
;UPDATES THE LISTVIEW
Function UpdateListView(bank%,params%=0)
	
	If bank<12 Then RuntimeError("Gadget ListView Error")
	
	canvas = PeekInt(bank,0)
	
	
	xwidth = 0
	width = ClientWidth(canvas)
	height = ClientHeight(canvas)
	
	colNum = 1
	
	SetBuffer CanvasBuffer(canvas)
	Cls
	For c.column = Each column
		Color 235, 234, 219
		Rect xwidth%, 0, xwidth%+c\colWidth%, 18, True
		Color 226, 222, 205
		Line xwidth%, 18, xwidth%+c\colWidth%, 18
		Color 214, 210, 194
		Line xwidth%, 19, xwidth%+c\colWidth%, 19
		Color 203, 199, 184
		Line xwidth%, 20, xwidth%+c\colWidth%, 20
		SetFont listViewFont%
		Color 0, 0, 0
		Text xwidth%+8, 3, c\colName%
		Color 255, 255, 255
		Rect xwidth%, 20, width%-xwidth%, height, True
		Color 199, 197, 178
		Line xwidth%+c\colWidth%-2, 3, xwidth%+c\colWidth%-2, 17
		Color 255, 255, 255
		Line xwidth%+c\colWidth%-1, 3, xwidth%+c\colWidth%-1, 17
		textStart% = 20
		highexist = False
		deleted = False
		For c\i.item = Each item
		If params = textStart% And deleted = False And c\i\col = colNum
			Delete c\i
			deleted = True
		Else
			If c\i\col = colNum
				If highrow% > textStart Or highrow% = textStart And highrow < textStart+20
					highExist = True
					Color 49, 106, 197
					Rect xwidth%, textStart, c\colWidth%, 20, True
					Color 255, 255, 255
					Text xwidth%+10, textStart+3, c\i\itemText$
				Else
					Color 0, 0, 0
					Text xwidth%+10, textStart+3, c\i\itemText$
				End If
				textStart = textStart + 20
			End If
		End If
		Next
		If highexist = False Then highrow = 0
		xwidth% = xwidth% + c\colWidth%
		colNum = colNum+1
	Next
	Color 235, 234, 219
	Rect xwidth%, 0, width%+50, 18, True
	Color 226, 222, 205
	Line xwidth%, 18, width%+50, 18
	Color 214, 210, 194
	Line xwidth%, 19, width%+50, 19
	Color 203, 199, 184
	Line xwidth%, 20, width%+50, 20
	Color 255, 255, 255
	Rect xwidth%, 20, width%+50, height, True
	Color 0, 0, 0
	Rect 0, 0, width%, height, False
	FlipCanvas(canvas)
	
	Return
	
End Function

;------------------------------------------------------
;ADDS A COLUMN TO THE LIST VIEW
Function AddListViewColumn(listView,columnName$,width%)
	
	c.column = New column
	c\colWidth% = width%
	c\colName$ = columnName$
	UpdateListView(listView)
	
End Function

;------------------------------------------------------
;REMOVES A COLUMN FROM A LISTVIEW
Function DeleteListViewColumn(listView,col1%)
	
	colNum% = 1
	
	For c.column = Each column
		If colNum% = col1%
			For c\i.item = Each item
				If c\i\col = col1%
					Delete c\i
				Else
					c\i\col = c\i\col-1
				End If
			Next
			Delete c
			Exit
		Else
			colNum% = colNum+1
		End If
	Next
		
	UpdateListView(listView)
			
End Function

;------------------------------------------------------
;ADDS AN ITEM TO LISTVIEW
Function AddListViewItem(listView,items$) ;A COMMA SEPARATED LIST OF VALUES
	
	off% = 0
	count% = 1
	Repeat
		pos% = Instr(items$,",",off%)
		If pos% = 0
			Exit
		Else
			count% = count% + 1
			off% = pos%+1
		End If
	Forever
	
	Dim words(count%)
	
	begin% = 0
	
	For a = 1 To count%
		pos% = Instr(items$,",",begin%)
		If pos% = 0
			words(a) = Mid(items$,begin,Len(items$)-begin+1)
		Else
			words(a) = Mid(items$,begin,pos%-begin)
			begin% = pos%+1
		End If
	Next
	
	count% = 1
	
	For c.column = Each column
		c\i.item = New item
		c\i\col = count%
		c\i\itemText$ = words(count%)
		count% = count%+1
	Next
	
	rows% = rows% + 1
	UpdateListView(listView)
	
End Function

;------------------------------------------------------
;REMOVES AN ITEM FROM LISTVIEW
Function DeleteListViewItem(listView,item%)
	
	rowDel% = (item%*20)
	UpdateListView(listView,rowDel%)

End Function

;------------------------------------------------------
;REFRESHES LISTVIEW
Function ListViewRefresh(listView,event)
	
	If event = $203
		If Not EventSource() = PeekInt(listView,0) Then Return
		
		mx = EventX()
		my = EventY()
		
		For c.column = Each column
			If mx > (c\colWidth%+x)-4 And mx < (c\colWidth%+x)+4 And my > 0 And my <20
				cursor = LoadCursor(0,32644)
				SetCursor(cursor)
			End If
			x = x + c\colWidth
		Next		
	ElseIf event = $201
		If Not EventSource() = PeekInt(listView,0) Then Return
		
		x = 0
		
		For c.column = Each column
			If mx > (c\colWidth%+x)-4 And mx < (c\colWidth%+x)+4 And my > 0 And my <20
				cursor = LoadCursor(0,32644)
				SetCursor(cursor)
				While WaitEvent() <> $202
					If EventID() = $203
						c\colWidth% = EventX()-x
					End If
				Wend
				UpdateListView(listView)
				Exit
			End If
			x = c\colWidth%
		Next
		
		If my > 20
			mystr$ = Str(my)
			rowstr$ = Left(mystr,Len(my)-1) + "0"
			highrow% = Int(rowstr$)
		End If
		
	End If
	
	UpdateListView(listView)
	
End Function

;------------------------------------------------------
;GETS SELECTED LISTVIEW ITEM
Function SelectedListViewItem()
	
	If highrow = 0 Then Return 0
	
	selected% = highrow/20
	Return selected%
	
End Function

;------------------------------------------------------
;SELECTS LIST VIEW ITEM
Function SelectListViewItem(listView,item%)
	
	highrow% = (item%*20)
	UpdateListView(listView)
	
End Function
