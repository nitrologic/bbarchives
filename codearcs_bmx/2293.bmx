; ID: 2293
; Author: Ghost Dancer
; Date: 2008-08-03 14:53:23
; Title: Multi-Column List Box
; Description: Multi-Column List Box with sort (Windows only)

'******************************************************************************
'Multi-column list box with sort V1.2
'Original code: Ziltch 29 August 2006.
'Modified by Ghost Dancer 06 August 2008
'You can use this code if you credit Ziltch & Ghost Dancer
'
'V1.2 update
'	- Added sortEnable & sortDisable methods.
'	- Fixed sorting bug.
'	- Added update method which can be called on EVENT_WINDOWSIZE to reset first column width.
'	- Renamed some methods.
'
'V1.1 updates
'	- Fixed to work in Max 1.30.
'	- TListView now extends TProxyGadget.
'	- Is now unicode compliant.
'	- Remembers the selected item after a sort.
'******************************************************************************

'******************************************************************************
'example usage
'******************************************************************************
Strict

Import MaxGUI.Drivers

'set up
Local window:TGadget = CreateWindow("Multi-Column List Example", 100, 100, 320, 250, Null, WINDOW_RESIZABLE|WINDOW_TITLEBAR|WINDOW_CLIENTCOORDS)
Local listView:TListView = TListView.Create(2, 2, 310, 150, window, "Name")
SetGadgetLayout listView, EDGE_ALIGNED, EDGE_ALIGNED, EDGE_ALIGNED, EDGE_CENTERED

'add 2nd & 3rd columns
listView.addListViewColumn("Sex", 80)
listView.addListViewColumn("Age", 80)

'add some data
listView.addListViewItem(["Simon", "Male", "34"])
listView.addListViewItem(["Jane", "Female", "29"])
listView.addListViewItem(["Peter", "Male", "38"])
listView.addListViewItem(["Sally", "Female", "44"])

'2nd list
Local listView2:TListView = TListView.Create(2, 160, 310, 80, window, "one")
SetGadgetLayout listView2, EDGE_ALIGNED, EDGE_ALIGNED, EDGE_ALIGNED, EDGE_ALIGNED

listView2.addListViewColumn("two", 80)
listView2.addListViewColumn("three", 80)

'add some data
listView2.addListViewItem(["xtest 1-1", "xtest 1-2", "xtest 1-3"])
listView2.addListViewItem(["test 2-1", "test 2-2", "test 2-3"])

Local Gadget:TGadget

'Main Loop
Repeat
	WaitEvent()
	Gadget = TGadget(EventSource())
	
	Select EventID()
		Case EVENT_WINDOWSIZE
			TListView.update
		Case EVENT_WINDOWCLOSE
			Select gadget
			Case window
				Exit
			End Select
	End Select
Forever

'print selected data
Print "Selected Item: " + listView.getSelectedItem() + ", aged " + listView.getSelectedItem(2)

End


'==============================================================================
Type TListView Extends TProxyGadget
'==============================================================================
	Const LBS_MULTICOLUMN = 512
	
	'globals are used for sorting
	Global oldListProc							'used for column heading selections
	Global gadgetList:TList = CreateList()		'global list of type instances
	
	Field curColumn								'number of columns in list
	Field columnHeading:TColumnHeading[1]		'array of column data
	
	Field listBox:TGadget						'the gadget
	Field listboxHwnd							'listbox's HWND handle
	Field sortColumns = True					'enable/disable sorting
	
	
	'------------------------------------------------------------------------------
	Function create:TListView(x, y, w, h, parent:TGadget, heading$ = "", width = 100)
	'heading & width are for first column
	'------------------------------------------------------------------------------
		Local newListView:TListView = New TListView 
		
		newListView.listBox = CreateListBox(x, y, w, h, parent)
		newListView.SetProxy newListView.listBox
		
		Local TempCanvas:TGadget = CreateCanvas(0, 0, 0, 0, parent)
		
		newListView.listboxHwnd = QueryGadget(newListView.listBox, QUERY_HWND)
		
		oldListProc = SetWindowLongW(newListView.listboxHwnd, GWL_WNDPROC, Int(Byte Ptr NewListProc))
		
		newListView.setHeading(0, heading$, width)
		
		'store the gadget in global list...
		gadgetList.AddLast newListView
		
		'and also return it for standard OO usage
		Return newListView
	End Function
	
	
	'------------------------------------------------------------------------------
	Method setHeading(column, heading$, width, action = LVM_SETCOLUMNW)
	'------------------------------------------------------------------------------
		Local col:LVCOLUMNW = New LVCOLUMNW
		
		columnHeading[column] = New TColumnHeading
		columnHeading[column].width = width
		
		If columnHeading[Column].width = 0 Then
			Col.mask = LVCF_TEXT| LVCF_FMT 
		Else
			Col.mask = LVCF_TEXT| LVCF_FMT | LVCF_WIDTH
			col.cx   = columnHeading[Column].width
		End If
		
		col.pszText = heading$.ToWString()
		
		Local ListBoxstyle = GetWindowLongW(ListboxHwnd , GWL_STYLE)
		
		If (ListBoxstyle &  LVS_NOCOLUMNHEADER ) Then
			ListBoxstyle  = ListBoxstyle  ~LVS_NOCOLUMNHEADER 
			If ListBoxstyle & LVS_EDITLABELS=0 Then ListBoxstyle  = ListBoxstyle  | LVS_EDITLABELS
			SetWindowLongW(ListboxHwnd , GWL_STYLE,  ListBoxstyle )  'change the style so that we have headings
		End If
		
		SendMessageW(ListboxHwnd, action, Column, Int(Byte Ptr Col))
		
		columnWidth
	End Method
	
	
	'------------------------------------------------------------------------------
	Method addListViewColumn(heading$, width)
	'------------------------------------------------------------------------------
		curColumn:+ 1
		
		columnHeading = columnHeading[..curColumn+1]
		
		setHeading curColumn, heading, width, LVM_INSERTCOLUMNW
	End Method
	
	
	'------------------------------------------------------------------------------
	Method addListViewItem(text$[])
	'add full row from array
	'------------------------------------------------------------------------------
		Local curRow = CountGadgetItems(listBox)
		
		'add first column & reset width
		AddGadgetItem(listBox, text$[0])
		columnWidth
		
		For Local column = 1 To text$.Length - 1
			Local ListboxHwnd = QueryGadget(listBox, QUERY_HWND)
			Local ColItem:LVITEMW  = New LVITEMW
			
			ColItem.mask = LVIF_TEXT
			ColItem.iSubItem = column
			ColItem.iItem = curRow
			ColItem.pszText = Text$[column].ToWString()
			ColItem.cchTextMax =  Text$[column].Length + 1
			SendMessageW( ListboxHwnd, LVM_SETITEMW ,0, Int(Byte Ptr ColItem))
			ColItem = Null
		Next
	End Method
	
	
	'------------------------------------------------------------------------------
	Method getListViewItem:String(Row, Column)
	'------------------------------------------------------------------------------
		If ListboxHwnd Then
			Local Ans$
			Local TextStorage:Short[1024]
			Local ColItem:LVITEMW  = New LVITEMW
			
			ColItem.mask = LVIF_TEXT
			ColItem.iSubItem = Column
			ColItem.iItem = Row
			ColItem.pszText = TextStorage
			ColItem.cchTextMax =  1024 
			SendMessageW( ListboxHwnd,LVM_GETITEMW,0,Int(Byte Ptr ColItem))
			
			If ColItem.pszText Then
				Ans$=String.FromWString(ColItem.pszText)
				ColItem=Null
			End If
			
			Return Trim(Ans$)
		End If
	End Method
	
	
	'------------------------------------------------------------------------------
	Method setListViewItem(Text:String, Row, Column = 0)
	'set text in a specific row, column
	'------------------------------------------------------------------------------
		If Column = 0 Then listBox.items[Row].Text = text
		
		If ListboxHwnd Then
			Local ColItem:LVITEMW  = New LVITEMW
			ColItem.mask = LVIF_TEXT
			ColItem.iSubItem = Column
			ColItem.iItem = Row
			ColItem.pszText = Text.ToWString()
			ColItem.cchTextMax =  Len(Text) 
			
			Return SendMessageW( ListboxHwnd,LVM_SETITEMW,0,Int(Byte Ptr ColItem))
		End If
	End Method
	
	
	'------------------------------------------------------------------------------
	Method columnWidth(Column = 0)
	'------------------------------------------------------------------------------
		Local col:LVCOLUMNW = New LVCOLUMNW
		
		Col.mask = LVCF_WIDTH
		col.cx   = columnHeading[Column].width
		SendMessageW(ListboxHwnd,LVM_SETCOLUMNW,Column,Int(Byte Ptr Col))
	End Method
	
	
	'------------------------------------------------------------------------------
	Method listBoxetMultiSelect()
	'------------------------------------------------------------------------------
		Local ListBoxstyle = GetWindowLongW(ListboxHwnd , GWL_STYLE)
		
		If  (ListBoxstyle & LVS_SINGLESEL) = LVS_SINGLESEL Then
			Return SetWindowLongW(ListboxHwnd , GWL_STYLE,  ListBoxstyle  ~ LVS_SINGLESEL )  'change the style 		
		End If
	End Method
	
	
	'------------------------------------------------------------------------------
	Method getSelectedItem$(col = 0)
	'------------------------------------------------------------------------------
		If SelectedGadgetItem(listBox) >= 0 Then
			Return getListViewItem(SelectedGadgetItem(listBox), col)
		End If
	End Method
	
	
	'------------------------------------------------------------------------------
	Method sort(sortColumn)
	'------------------------------------------------------------------------------
		If sortColumns Then
			Local itemCount = CountGadgetItems(listBox)
			
			If itemCount Then
				Local c, r, sortCount
				Local selectedItem, selectedItemNew = -1
				Local newList:TList = CreateList()
				Local rowCount = CountGadgetItems(listBox)	'total number of rows/items in list
				
				'store sorted rows in temp list
				Repeat
					Local rowNum = 0, rowText$
					If columnHeading[sortColumn].sortDir = 1 Then rowText$ = "zzzzzz"
					
					'find next row in sequence
					For r = 0 To CountGadgetItems(listBox) - 1
						If getListViewItem(r, sortColumn).ToLower() <= rowText$ = columnHeading[sortColumn].sortDir Then
							rowText$ = getListViewItem(r, sortColumn).ToLower()
							rowNum = r
						End If
					Next
					
					'update selected index
					If selectedItemNew = -1 Then
						 selectedItem = SelectedGadgetItem(listBox)
						If selectedItem = rowNum Then selectedItemNew = sortCount
					End If
					
					'create an temp array to store row data
					Local listRow$[curColumn+1]
					
					'copy this row to new list
					For c = 0 To curColumn
						listRow$[c] = getListViewItem(rowNum, c)
					Next
					
					newList.AddLast listRow$
					
					'remove row from original list & update counter
					listBox.RemoveItem rowNum
					sortCount:+ 1
				Until sortCount = rowCount
				
				'copy sorted data to new list
				For Local row$[] = EachIn newList
					addListViewItem(row$)
				Next
				
				'change sort direction for this column
				If columnHeading[sortColumn].sortDir = 1 Then
					columnHeading[sortColumn].sortDir = 0
				Else
					columnHeading[sortColumn].sortDir = 1
				End If
				
				'reselect item
				If selectedItemNew >= 0 Then SelectGadgetItem listBox, selectedItemNew
			End If
		End If
	End Method
	
	
	'------------------------------------------------------------------------------
	Method sortEnable()
	'------------------------------------------------------------------------------
		sortColumns = True
	End Method
	
	
	'------------------------------------------------------------------------------
	Method sortDisable()
	'------------------------------------------------------------------------------
		sortColumns = False
	End Method
	
	
	'------------------------------------------------------------------------------
	Function update()
	'call on EVENT_WINDOWSIZE to reset width of first column
	'------------------------------------------------------------------------------
		For Local newListView:TListView = EachIn gadgetList
			newListView.columnWidth
		Next
	End Function
	
	
	'------------------------------------------------------------------------------
	Function NewListProc:Int(hWnd:Int, Msg:Int, wParam:Int, lParam) "win32"
	'------------------------------------------------------------------------------
		Const HDN_ITEMCLICKW = -322
		
		If Msg = WM_NOTIFY Then
			Local NotifyMess:HD_NOTIFY = New HD_NOTIFY
			Local Tstr$
			
			MemCopy( NotifyMess, Byte Ptr lParam, SizeOf(HD_NOTIFY) )
			
			If NotifyMess.code = HDN_ITEMCLICKW Then
				'find gadget that was clicked & sort it
				For Local newListView:TListView = EachIn gadgetList
					If newListView.listboxHwnd = hWnd Then newListView.sort NotifyMess.iitem
				Next
			End If
		End If
		
		If oldListProc <>0 Then Return CallWindowProcW(Byte Ptr oldListProc, hWnd, Msg, wParam, lParam)
	End Function

End Type


'==============================================================================
Type TColumnHeading
'used by TListView
'==============================================================================
	Field width
	Field sortDir = 1
EndType


'==============================================================================
Type HD_NOTIFY 
'used by TListView
'==============================================================================
	Field hwndFrom
	Field idFrom
	Field code
	Field iItem
	Field iButton
	Field pitem
EndType
