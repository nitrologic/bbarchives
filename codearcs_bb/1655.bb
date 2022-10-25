; ID: 1655
; Author: Nilium
; Date: 2006-03-31 21:20:31
; Title: Vein R3 GUI
; Description: Graphical user interface designed for Vein R3

;#Region DESCRIPTION
	;; Functions for grabbing input
;#End Region

;#Region CLASSES
	Type InputEngine
		Field MX%,MY%,MZ%,MXS%,MYS%,MZS%
		Field MD1%,MD2%,MD3%,OMD1%,OMD2%,OMD3%
		
		Field Key%[255]
		Field OldKey%[255]
	End Type
	
	Global InEngine.InputEngine = New InputEngine
;#End Region

;#Region PROCEDURES
	Function GrabInput()
		For i = 1 To 237
			InEngine\OldKey[i] = InEngine\Key[i]
			InEngine\Key[i] = KeyDown(i)
		Next
		
		InEngine\MXS = MouseXSpeed()
		InEngine\MYS = MouseYSpeed()
		InEngine\MZS = MouseZSpeed()
		
		InEngine\MX = MouseX()
		InEngine\MY = MouseY()
		InEngine\MZ = MouseZ()
		
		InEngine\OMD1 = InEngine\MD1
		InEngine\OMD2 = InEngine\MD2
		InEngine\OMD3 = InEngine\MD3
		
		InEngine\MD1 = MouseDown(1)
		InEngine\MD2 = MouseDown(2)
		InEngine\MD3 = MouseDown(3)
	End Function
	
	Function iMouseXSpeed()
		Return InEngine\MXS
	End Function
	
	Function iMouseYSpeed()
		Return InEngine\MYS
	End Function
	
	Function iMouseZSpeed()
		Return InEngine\MZS
	End Function
	
	Function iMouseX()
		Return InEngine\MX
	End Function
	
	Function iMouseY()
		Return InEngine\MY
	End Function
	
	Function iMouseZ()
		Return InEngine\MZ
	End Function
	
	Function iMouseDown(Mouse)
		Select Mouse
			Case 1
				md = InEngine\MD1
			Case 2
				md = InEngine\MD2
			Case 3
				md = InEngine\MD3
		End Select
		
		Return md
	End Function
	
	Function iMouseHit(Mouse)
		Select Mouse
			Case 1
				omd = InEngine\OMD1
				md = InEngine\MD1
			Case 2
				omd = InEngine\OMD2
				md = InEngine\MD2
			Case 3
				omd = InEngine\OMD3
				md = InEngine\MD3
		End Select
		
		Return (omd = 0 And md = 1)
	End Function
	
	Function iMouseUp(Mouse)
		Select Mouse
			Case 1
				omd = InEngine\OMD1
				md = InEngine\MD1
			Case 2
				omd = InEngine\OMD2
				md = InEngine\MD2
			Case 3
				omd = InEngine\OMD3
				md = InEngine\MD3
		End Select
		
		Return (omd = 1 And md = 0)
	End Function
	
	Function iKeyHit(Key)
		If InEngine\Key[Key] = 1 And InEngine\OldKey[Key] = 0 Then Return 1
		Return 0
	End Function
	
	Function iKeyUp(Key)
		If InEngine\Key[Key] = 0 And InEngine\OldKey[Key] = 1 Then Return 1
		Return 0
	End Function
	
	Function iKeyDown(Key)
		Return InEngine\Key[Key]
	End Function
	
	Function iMouseInRect(X,Y,W,H)
		If (InEngine\mx >= X) And (InEngine\my >= Y) And (InEngine\mx <= X+W) And (InEngine\my <= Y+H) Return 1
		Return 0
	End Function
	
	Function iSendMouseHit(Mouse)
		Select Mouse
			Case 1
				InEngine\MD1 = 1
				InEngine\OMD1 = 0
			Case 2
				InEngine\MD2 = 1
				InEngine\OMD2 = 0
			Case 3
				InEngine\MD3 = 1
				InEngine\OMD3 = 0
		End Select
	End Function
	
	Function iSendMouseUp(Mouse)
		Select Mouse
			Case 1
				InEngine\MD1 = 0
				InEngine\OMD1 = 1
			Case 2
				InEngine\MD2 = 0
				InEngine\OMD2 = 1
			Case 3
				InEngine\MD3 = 0
				InEngine\OMD3 = 1
		End Select
	End Function
	
	Function iSendMouseDown(Mouse)
		Select Mouse
			Case 1
				InEngine\MD1 = 1
			Case 2
				InEngine\MD2 = 1
			Case 3
				InEngine\MD3 = 1
		End Select
	End Function
;#End Region


;#Region CLASSES
	Type Event
		Field GadgetID
		Field Info$
	End Type
;#End Region

;#Region PROCEDURES
	Function Event(i.Gadget,Info$)
		Local e.Event = New Event
		e\GadgetID = Handle(i)
		e\Info = Info
		Return Handle(e)
	End Function
;#End Region


;#Region CLASSES
	Type Gadget
		Field X%,Y%,Z%					;; Local X position, local Y position, Z stack height
		Field Width%,Height%
		Field Parent.Gadget				;; Parent gadget
		
		Field Gadgets					;; Stack
		
		Field Class					;; Gadget class (refer to GUI_ constants)
		Field State%[32]				;; State
		Field Params%[32]				;; Parameters
		
		Field Content$
		Field Caption$
		
		Field MenuStrip.Gadget
		Field ToolStrip.Gadget
		
		Field GroupID%
		
		Field Min#
		Field Max#
		Field Val#
		
		Field Over%
		
		Field Image					;; Image used in gadget drawing
		
		Field Mode
		
		Field Name$
		
		Field Icon%
	End Type
	
	;#Region GADGET CLASSES
		Const GUI_WINDOW = $4000
		
		Const GUI_LABEL = $4001
		Const GUI_BUTTON = $4002
		
		Const GUI_GROUPBOX = $4003
		Const GUI_ROLLOUT = $4004
		Const GUI_VIEWPORT = $4013
		
		Const GUI_PROGRESSBAR = $4005
		Const GUI_HORSCROLL = $4006
		Const GUI_VERSCROLL = $4007
		Const GUI_TRACKBAR = $4008
		Const GUI_SLIDER = $4009
		
		Const GUI_RADIO = $400A
		Const GUI_CHECKBOX = $400B
		
		Const GUI_MENUSTRIP = $400C
		Const GUI_MENUTITLE = $400D
		Const GUI_MENUITEM = $400E
		Const GUI_MENUBAR = $400F
		
		Const GUI_CONTEXTMENU = $40010
		Const GUI_CONTEXTMENUITEM = $4011
		Const GUI_CONTEXTMENUBAR = $4012
		
		Const GUI_DIAL = $4013
		
		Const GUI_LISTBOX = $4014
		Const GUI_LISTBOXITEM = $4015
		
		Const GUI_COMBOBOX = $4016
		Const GUI_COMBOBOXITEM = $4017
		
		Const GUI_TREEVIEW = $4018
		Const GUI_TREEVIEWNODE = $4019
		
		Const GUI_TEXTBOX = $401A
		
		Const GUI_TABSTRIP = $401B
		Const GUI_TABPAGE = $401C
		
		Const GUI_STATUSSTRIP = $401D
		Const GUI_STATUSITEM = $401E
		
		Const GUI_TOOLSTRIP = $401F
		Const GUI_TOOLITEM = $4020
		Const GUI_TOOLBAR  = $4021
		
		Const GUI_PANEL = $4022
		
		Const GUI_IMAGEBOX = $4023
	;#End Region
	
	;#Region WINDOW MODES
		Const WM_TITLEBAR = 1
		Const WM_CLOSEBTN = 2
		Const WM_MINBTN = 4
		Const WM_TITLEBARSHADOW = 8
	;#End Region
	
	;#Region PROGRESS BAR MODES
		Const SLIDER_HOR = 1
		Const SLIDER_VER = 2
		
		Const SLIDER_INTEGER = 4
		Const SLIDER_FLOAT = 8
	;#End Region
	
	;#Region STATES
		Const GADGET_HIDDEN = 30			;; Globally used
		
		Const WINDOW_DRAG = 0
		Const WINDOW_DRAGX = 1
		Const WINDOW_DRAGY = 2
		Const WINDOW_DROPX = 3
		Const WINDOW_DROPY = 4
		Const WINDOW_LOCKED = 5
		Const WINDOW_CLOSEBUTTON = 6
		Const WINDOW_MINBUTTON = 7
		Const WINDOW_MINIMIZED = 8
		Const WINDOW_CLOSED = 9
		
		Const BUTTON_DOWN = 0
		
		Const ROLLOUT_CLOSED = 0
		
		Const TEXTBOX_ACTIVE = 0
		
		Const TICK_CHECKED = 0
	;#End Region
	
	;#Region COLORS
		Const WINDOW_TITLE_INACTIVE_F = $FF8DE1EB
		Const WINDOW_TITLE_INACTIVE_T = $FF9EACBA
		Const WINDOW_TITLE_ACTIVE_F = $FF97C1EB
		Const WINDOW_TITLE_ACTIVE_T = $FF6790B9
		Const WINDOW_DIALOG_F = $C8C8C8
		Const WINDOW_DIALOG_T = $B4B4B4
	;#End Region
	
	Global GUI_RootGadgets					;; Gadgets with no parent (windows)
	Global GUI_ActiveContextMenu.Gadget		;; Active (open) context menu
	Global GUI_ContextMenus					;; Context menus
	Global GUI_ContextMenuImage				;; Context menu graphic
	Global GUI_ContextMenuItemImage			;; Context menu item graphic
	Global GUI_ModalWindow.Gadget				;; Modal window
	Global GUI_Font						;; Normal font
	Global GUI_FontBold						;; Bold font
	Global GUI_LFont						;; Large normal font
	Global GUI_LFontBold					;; Large bold font
	Global GUI_MouseCursor					;; Mouse cursor image
	Global GUI_PointerHidden					;; Whether or not the cursor is visible
;#End Region

;#Region PROCEDURES
	Function UpdateGUI()		
		If GUI_ModalWindow <> Null Then
			z = GUI_ModalWindow\Z
			For g.Gadget = Each Gadget
				If g\Class = GUI_WINDOW And g <> GUI_ModalWindow And g\Z > z
					g\Z = g\Z - 1
				EndIf
			Next
			GUI_ModalWindow\Z = MoveObjectToBack(GUI_RootGadgets,GUI_ModalWindow\Z)
		EndIf
		
		UpdateGadget(GUI_ActiveContextMenu)
		
		If GUI_ActiveContextMenu <> Null Then
			cm.Gadget = GUI_ActiveContextMenu
			If cm\Over = 1 Then Return
		EndIf
		
		For i = 0 To Objects(GUI_RootGadgets)-1
			g.Gadget = Object.Gadget(GetObject(GUI_RootGadgets,i))
			UpdateGadget(g)
		Next
	End Function
	
	Function UpdateGadget(i.Gadget)
		If i = Null Then Return
		
		If i\State[30] = 1 Then Return
		
		Local x = GetGadgetSX(i)
		Local y = GetGadgetSY(i) + (16*(i\MenuStrip <> Null) + 20*(i\ToolStrip <> Null))*(i\Class <> GUI_WINDOW)
		
		Local r.Gadget = GetRoot(i)
		Local isActive = (r\Z = Objects(GUI_RootGadgets)-1)
		If r <> Null Then
			If r\Class = GUI_CONTEXTMENU Then
				isActive = 1
				x = GetGadgetSX(i\Parent)
				y = GetGadgetSY(i\Parent)+i\Z*20
				i\Width = r\Width
				i\Height = 19
			EndIf
		EndIf
		
		Local mh1 = iMouseHit(1)
		Local mu1 = iMouseUp(1)
		Local md1 = iMouseDown(1)
		Local mx = iMouseX()
		Local my = iMouseY()
		Local mz = iMouseZ()
		
		Local rStack
		
		If i\Parent <> Null Then
			rStack = i\Parent\Gadgets
		Else
			rStack = GUI_RootGadgets
		EndIf
		
		Local az = Objects(rStack)-1
		
		Local over = iMouseInRect(x,y,i\Width,i\Height*(Not i\State[WINDOW_MINIMIZED]) + (24*(i\Class = GUI_WINDOW)*(i\Mode And WM_TITLEBAR))+(16*(i\MenuStrip <> Null) + 20*(i\ToolStrip <> Null))*(i\Class = GUI_WINDOW))
		i\Over = over
		
		Local g.Gadget = Null
		
		Select i\Class
		
			Case GUI_WINDOW
				If i\State[WINDOW_CLOSED] = 1 Then Return
				If GUI_ModalWindow = Null Then
					If mh1 And over And i\Z < az Then
						For g.Gadget = Each Gadget
							If g\Class = GUI_WINDOW And g\Z > i\Z And g <> i And g\Parent = i\Parent Then
								gx = GetGadgetSX(g)
								gy = GetGadgetSY(g)
								If iMouseInRect(gx,gy,g\Width,g\Height*(Not g\State[WINDOW_MINIMIZED])+24*((g\Mode And WM_TITLEBAR) = WM_TITLEBAR)) Then Exit
							EndIf
						Next
						
						If g = Null Then
							For g.Gadget = Each Gadget
								If g\Class = GUI_WINDOW And g\Z > i\Z And g <> i Then g\Z = g\Z - 1
							Next
							i\Z = MoveObjectToBack(rStack,i\Z)
						EndIf
					EndIf
				EndIf
				
				If (i\Mode And WM_TITLEBAR) = WM_TITLEBAR And i\Z = az Then
					overClose = MaxI(iMouseInRect(x+i\Width-20,y+4,16,16),((i\Mode And WM_CLOSEBTN) = WM_CLOSEBTN))
					overMin = MaxI(iMouseInRect(x+i\Width-20-20*((i\Mode And WM_CLOSEBTN)=WM_CLOSEBTN),y+4,16,16),((i\Mode And WM_MINBTN) = WM_MINBTN))
					If i\State[5] = 0 Then
						over = iMouseInRect(x,y,i\Width,24)
						
						If mh1 And over And overClose = 0 And overMin = 0 Then
							i\State[0] = 1
							i\State[1] = iMouseX()
							i\State[2] = iMouseY()
							i\State[3] = i\X
							i\State[4] = i\Y
						ElseIf md1 And i\State[0] = 1 Then
							i\X = i\State[3] + (iMouseX()-i\State[1])
							i\Y = i\State[4] + (iMouseY()-i\State[2])
						EndIf
						
						If mu1 And i\State[0] = 1 Then
							i\State[0] = 0
						EndIf
					EndIf
					
					If (i\Mode And WM_CLOSEBTN) = WM_CLOSEBTN Then
						over = overClose
						
						If mh1 And over Then
							i\State[WINDOW_CLOSEBUTTON] = 1
						ElseIf i\State[WINDOW_CLOSEBUTTON] <> 1 And over Then
							i\State[WINDOW_CLOSEBUTTON] = 2
						ElseIf i\State[WINDOW_CLOSEBUTTON] = 1 And mu1 And over Then
							i\State[WINDOW_CLOSEBUTTON] = over*2
							i\State[WINDOW_CLOSED] = 1
							Event(i,"Closed")
							For g.Gadget = Each Gadget
								If g <> i And g\Class = GUI_WINDOW Then g\Z = g\Z + 1
							Next
							i\Z = MoveObjectToFront(GUI_RootGadgets,i\Z)
						ElseIf (i\State[WINDOW_CLOSEBUTTON] <> 1) Or (i\State[WINDOW_CLOSEBUTTON] = 1 And mu1)
							i\State[WINDOW_CLOSEBUTTON] = over*2
						EndIf
					EndIf
					
					If (i\Mode And WM_MINBTN) = WM_MINBTN Then
						over = overMin
						
						If mh1 And over Then		;; You know.. I could have avoided all these i\Z = az things early on, but at the time I was hyped up on coffee and didn't think about it.
							i\State[WINDOW_MINBUTTON] = 1
						ElseIf mu1 And over And i\State[WINDOW_MINBUTTON] = 1 Then
							i\State[WINDOW_MINBUTTON] = over*2
							i\State[WINDOW_MINIMIZED] = Not i\State[WINDOW_MINIMIZED]
							If i\State[WINDOW_MINIMIZED] = 1 Then
								Event(i,"Shaded")
							Else
								Event(i,"Unshaded")
							EndIf
						ElseIf (i\State[WINDOW_MINBUTTON] <> 1) Or (i\State[WINDOW_MINBUTTON] = 1 And mu1)
							i\State[WINDOW_MINBUTTON] = over*2
						EndIf
					EndIf
				EndIf
			
			Case GUI_BUTTON
				If isActive Then
					If i\State[0] = 3 Then
					ElseIf mh1 And over
						i\State[0] = 2
					ElseIf mu1 And i\State[0] = 2
						i\State[0] = over
						Event(i,"Pressed")
					ElseIf md1 And i\State[0] = 2
						i\State[0] = 2		;; Pointless state check, but it's there for the sake of completeness
					ElseIf over Then
						i\State[0] = 1
					Else
						i\State[0] = 0
					EndIf
				EndIf
			
			Case GUI_TEXTBOX
				If isActive Then
					If mh1 And over Then
						i\State[0] = 1
						FlushKeys()
					ElseIf i\State[0] = 1
						k = GetKey()
						
						If k = 13 Then
							i\State[0] = 0
							Event(i,i\Caption)
						ElseIf k = 8 Then
							i\Caption = Left(i\Caption,MinI(Len(i\Caption)-1,0))
						ElseIf over = 0 And mh1 Then
							i\State[0] = 0
							Event(i,i\Caption)
						ElseIf k > 31 Then
							i\Caption = i\Caption + Chr(k)
						EndIf
					EndIf
				EndIf
				
			Case GUI_ROLLOUT
				If isActive Then
					over = iMouseInRect(x,y,i\Width,16)
					If mh1 And over Then
						i\State[ROLLOUT_CLOSED] = Not i\State[ROLLOUT_CLOSED]
						For g.Gadget = Each Gadget
							If g\Parent = i\Parent And g <> i And RectsOverlap(i\X,i\Y,i\Width,2048,g\X,g\Y,g\Width,g\Height) Then
								g\Y = g\Y + (i\Height)*((i\State[ROLLOUT_CLOSED] = 0)-(i\State[ROLLOUT_CLOSED] = 1))
							EndIf
						Next
					EndIf
				EndIf
				
				If i\State[0] = 1 Then Return
			
			Case GUI_RADIO
				If isActive Then
					If over And mh1 Then
						i\State[TICK_CHECKED] = 2
					ElseIf md1 And i\State[TICK_CHECKED] = 2
						i\State[TICK_CHECKED] = 2
					ElseIf mu1 And i\State[TICK_CHECKED] = 2 And over Then
						For g.Gadget = Each Gadget
							If g\Parent = i\Parent And g <> i And g\Class = GUI_RADIO Then
								g\Val = 0
							EndIf
						Next
						i\Val = 1
						Event(i,1)
					Else
						i\State[TICK_CHECKED] = over
					EndIf
				EndIf
			
			Case GUI_CHECKBOX
				If isActive
					If over And mh1 Then
						i\State[TICK_CHECKED] = 2
					ElseIf md1 And i\State[TICK_CHECKED] = 2
						i\State[TICK_CHECKED] = 2
					ElseIf mu1 And i\State[TICK_CHECKED] = 2 And over Then
						i\Val = Not i\Val
						Event(i,Int i\Val)
					Else
						i\State[TICK_CHECKED] = over
					EndIf
				EndIf
			
			Case GUI_CONTEXTMENU
				i\Height = 0
				For n = 0 To Objects(i\Gadgets)-1
					g.Gadget = Object.Gadget(GetObject(i\Gadgets,n))
					width = MinI(ImageWidth(g\Image)+20,width)
					i\Height = i\Height + 16
				Next
				i\Width = width
				
				i\Over = iMouseInRect(i\X,i\Y,i\Width,i\Height)
				
				If (iMouseHit(1) Or iMouseHit(2) Or iMouseHit(3)) And (i\Over=0) Then
					GUI_ActiveContextMenu = Null
				EndIf
			
			Case GUI_CONTEXTMENUITEM
				x = GetGadgetSX(i\Parent)
				y = GetGadgetSY(i\Parent)+i\Z*16
				i\State[0] = iMouseInRect(x,y,i\Width,i\Height-1)
				If i\State[0] And mu1 Then
					Event(i,1)
					GUI_ActiveContextMenu = Null
				EndIf
			
			Case GUI_SLIDER
				d# = i\Max-i\Min
				bar# = -(i\Val/d)*(i\State[1])
				
				If (i\Mode And SLIDER_HOR) = SLIDER_HOR
					p# = (i\Val/d)*(i\Width-2)
					
					over = iMouseInRect(x+p+bar+1,y+1,i\State[1],i\Height-2)
					If mh1 And over Then
						i\State[2] = 1
					ElseIf mu1 And i\State[2] = 1 Then
						i\State[2] = 0
						v# = i\Val
						If (i\Mode And SLIDER_INTEGER) = SLIDER_INTEGER Then v# = Int v
						Event(i,v)
					ElseIf i\State[2] = 1 And md1 Then
						i\Val = MaxF(MinF(i\Min+(Float(mx-(x+1))/(i\width-2))*d,i\Min),i\Max)
						v# = i\Val
						If (i\Mode And SLIDER_INTEGER) = SLIDER_INTEGER Then v# = Int v
						Event(i,v)
					EndIf
				Else
					p# = (i\Val/d)*(i\Height-2)
					
					over = iMouseInRect(x+1,y+1+p+bar,i\Width-2,i\State[1])
					If mh1 And over Then
						i\State[2] = 1
					ElseIf mu1 And i\State[2] = 1 Then
						i\State[2] = 0
						v# = i\Val
						If (i\Mode And SLIDER_INTEGER) = SLIDER_INTEGER Then v# = Int v
						Event(i,v)
					ElseIf i\State[2] = 1 And md1 Then
						i\Val = MaxF(MinF(i\Min+(Float(my-(y+1))/(i\Height-2))*d,i\Min),i\Max)
						v# = i\Val
						If (i\Mode And SLIDER_INTEGER) = SLIDER_INTEGER Then v# = Int v
						Event(i,v)
					EndIf
				EndIf
				
			Case GUI_TRACKBAR
				d# = i\Max-i\Min
				pos = Int(( (i\Val-i\Min) / d )*i\State[1])*i\State[2]
				bar = Int(((i\Val-i\Min)/d))*i\State[2]
				over = iMouseInRect(x+pos-bar+1,y+1,i\State[2],i\Height-2)
				If mh1 And over Then
					i\State[0] = 1
				ElseIf md1 And i\State[0] = 1
					i\Val = MinF(MaxF(( Float(mx-(x+1)) / i\Width )*d,i\Max-(d/i\State[1])),i\Min)
					If (i\Mode And 16) = 16 Then
						Event(i,i\Min+d-i\Val)
					Else
						Event(i,i\Val)
					EndIf
				ElseIf mu1 And i\State[0] = 1
					i\State[0] = 0
					If (i\Mode And 16) = 16 Then
						Event(i,i\Min+d-i\Val)
					Else
						Event(i,i\Val)
					EndIf
				EndIf
			
			Case GUI_TABPAGE
				over = iMouseInRect(x+i\State[1],y-16,i\Width,i\Height)
				If mh1 And over Then
					i\Parent\State[0] = i\Z
				EndIf
				
				If i\Parent\State[0] <> i\Z Then Return
			
			Case GUI_LISTBOXITEM
				x = GetGadgetSX(i\Parent)
				y = GetGadgetSY(i\Parent)+i\Z*20
				i\Over = iMouseInRect(x,y,i\Parent\Width,20)
				If i\Over And mh1 Then
					Event(i,i\Content)
					Event(i\Parent,Handle(i))
					i\Parent\State[LISTBOX_SELECTED] = i\Z
					i\Parent\Content  = i\Name
				EndIf
			
		End Select
		
		For n = 0 To Objects(i\Gadgets)-1
			g.Gadget = Object.Gadget(GetObject(i\Gadgets,n))
			UpdateGadget(g)
		Next
	End Function
	
	Function Gadget.Gadget(Class,Parent=0,Mode=0,X=0,Y=0,W=0,H=0,Caption$="",GroupID%=0,Min#=0,Max#=0,Val#=0,Name$="",icon$="")
		Local i.Gadget = New Gadget
		i\Class = Class
		i\Parent = Object.Gadget(Parent)
		
		If i\Parent <> Null Then
			i\Z = PushObject(i\Parent\Gadgets,Handle(i))
		ElseIf i\Parent = Null And i\Class = GUI_WINDOW
			i\Z = PushObject(GUI_RootGadgets,Handle(i))
		ElseIf i\Parent = Null And i\Class = GUI_CONTEXTMENU
			i\Z = PushObject(GUI_ContextMenus,Handle(i))
		Else
			Delete i
			Return Null
		EndIf
		i\X = X
		i\Y = Y
		i\Width = W
		i\Height = H
		i\Caption = Caption
		i\Min = Min
		i\Max = Max
		i\Val = Val
		i\Gadgets = CreateStack()
		i\Mode = Mode
		i\Icon = LoadImage(icon)
		If i\Icon <> 0 Then
			MaskImage i\Icon,255,0,255
		EndIf
		If i\Class = GUI_TOOLSTRIP Then
			If i\Parent <> Null i\Parent\ToolStrip = i
		EndIf
		
		If i\Class = GUI_MENUSTRIP Then
			If i\Parent <> Null i\Parent\MenuStrip = i
		EndIf
		
		If i\Class = GUI_SLIDER Or i\Class = GUI_TRACKBAR Then
			i\State[1] = GroupID
		EndIf
		
		If Name$ = "" Then
			Select i\Class
				Case GUI_WINDOW
					Name="wnd_"
				Case GUI_BUTTON
					Name="btn_"
				Case GUI_ROLLOUT
					Name="rol_"
				Case GUI_SLIDER
					Name="sld_"
				Case GUI_PROGRESSBAR
					Name="prg_"
				Case GUI_TRACKBAR
					Name="trk_"
				Case GUI_HORSCROLL
					Name="hrs_"
				Case GUI_VERSCROLL
					Name="vrs_"
				Case GUI_PANEL
					Name="pnl_"
				Case GUI_LISTBOX
					Name="lst_"
				Case GUI_LISTBOXITEM
					Name="lsi_"
				Case GUI_COMBOBOX
					Name="cmb_"
				Case GUI_TREEVIEW
					Name="trv_"
				Case GUI_RADIO
					Name="rad_"
				Case GUI_CHECKBOX
					Name="cbx_"
				Case GUI_CONTEXTMENU
					Name="cxm_"
				Case GUI_CONTEXTMENUITEM
					Name="cxi_"
				Case GUI_CONTEXTMENUBAR
					Name="cxb_"
				Case GUI_TABSTRIP
					Name="tab_"
				Case GUI_TABPAGE
					Name="pag_"
				Case GUI_VIEWPORT
					Name="v3d_"
			End Select
			index = 1
			For g.Gadget = Each Gadget
				If g <> i And g\Class = i\Class Then index = index + 1
			Next
			Name = Name+index
		EndIf
		
		i\Name = Name
		
		CreateGUIImage(i)
		Return i
	End Function
	
	Function GetGadgetSX(g.Gadget)
		Local x
		While g <> Null
			x = x + g\X
			If g\Class = GUI_MENUSTRIP Then
				For i = 0 To Objects(g\Gadgets)-1
					item.Gadget = Object.Gadget(GetObject(g\Gadgets,i))
					x = x + item\Width
				Next
			EndIf
			g = g\Parent
		Wend
		Return x
	End Function
	
	Function GetGadgetSY(g.Gadget)
		Local y
		While g <> Null
			y = y + g\y
			g = g\Parent
			If g <> Null Then
				y = y + ((g\Class = GUI_WINDOW)*23*((g\Mode And WM_TITLEBAR)=WM_TITLEBAR)) + (g\Class = GUI_ROLLOUT)*15 + 16*(g\MenuStrip <> Null) + 20*(g\ToolStrip <> Null)
			EndIf
		Wend
		Return y
	End Function
	
	Function GetRoot.Gadget(g.Gadget)
		While g <> Null
			If g\Parent = Null Then Exit
			g = g\Parent
		Wend
		Return g
	End Function
	
	Function OverGadget(ID)
		i.Gadget = Object.Gadget(ID)
		If i = Null Then Return
		x = GetGadgetSX(i)
		y = GetGadgetSY(i)
		
		p.Gadget = GetRoot(i)
		If p <> Null And p <> i Then
			If p\Class = GUI_WINDOW And p\Z <> Objects(GUI_RootGadgets)-1 Then Return 0
			If p\Class = GUI_WINDOW And p\State[WINDOW_MINIMIZED] = 1 Then Return 0
		EndIf
		
		If i\Class = GUI_TABPAGE Then Return iMouseInRect(x,y,i\Parent\Width,i\Parent\Height)*(i\Z = i\Parent\State[0])
		
		Return i\Over
		
		Select i\Class
			Case GUI_WINDOW
				For g.Gadget = Each Gadget
					If g\Z > i\Z And g\Class = GUI_WINDOW And g <> i Then
						If iMouseInRect(x,y,g\Width,g\Height*(Not g\State[WINDOW_MINIMIZED])+24*((g\Mode And WM_TITLEBAR) = WM_TITLEBAR)) Then Return 0
					EndIf
				Next
				Return iMouseInRect(x,y,i\Width,i\Height*(Not i\State[WINDOW_MINIMIZED])+24*((i\Mode And WM_TITLEBAR) = WM_TITLEBAR))
			Case GUI_ROLLOUT
				Return iMouseInRect(x,y,i\Width,16+i\Height*(i\State[ROLLOUT_CLOSED] = 0))
			Case GUI_RADIO,GUI_CHECKBOX
				Return iMouseInRect(x,y,16,16)
			Case GUI_TABPAGE
				Return iMouseInRect(x,y,i\Parent\Width,i\Parent\Height)*(i\Z = i\Parent\State[0])
			Default
				Return iMouseInRect(x,y,i\Width,i\Height)
		End Select
	End Function
	
	Function Window(X,Y,Width,Height,Caption$="",Mode=WM_TITLEBAR Or WM_CLOSEBTN Or WM_MINBTN,icon$="",name$="")
		Return Handle(Gadget(GUI_WINDOW,0,Mode,X,Y,Width,Height,Caption,0,0,0,0,name,icon))
	End Function
	
	Function Button(Parent,X,Y,Width,Height,Caption$="",icon$="",name$="")
		Return Handle(Gadget(GUI_BUTTON,Parent,Mode,X,Y,Width,Height,Caption,0,0,0,0,name,icon))
	End Function
	
	Function Radio(Parent,X,Y,Caption$="",Group=0,Ticked=0,name$="")
		Return Handle(Gadget(GUI_RADIO,Parent,Mode,X,Y,0,0,Caption,Group,0,0,Ticked,name))
	End Function
	
	Function Checkbox(Parent,X,Y,Caption$="",Ticked=0,name$="")
		Return Handle(Gadget(GUI_CHECKBOX,Parent,Mode,X,Y,0,0,Caption,Group,0,0,Ticked,name))
	End Function
	
	Function Groupbox(Parent,X,Y,Width,Height,Caption$="",icon$="",name$="")
		Return Handle(Gadget(GUI_GROUPBOX,Parent,Mode,X,Y,Width,Height,Caption,0,0,0,0,name,icon))
	End Function
	
	Function Rollout(Parent,X,Y,Width,Height,Caption$="",Closed=0,name$="")
		Rollout = Handle(Gadget(GUI_ROLLOUT,Parent,Mode,X,Y,Width,Height,Caption,0,0,0,0,name))
		If Closed = 1 Then
			CloseGadget(Rollout)
		EndIf
		Return Rollout
	End Function
	
	Function Panel(Parent,X,Y,Width,Height,name$="")
		Return Handle(Gadget(GUI_PANEL,Parent,Mode,X,Y,Width,Height,0,0,0,0,0,name))
	End Function
	
	Function Slider(Parent,X,Y,Width,Height,MinVal#=0,MaxVal#=1,Val#=0,Orientation=SLIDER_HOR,ValueType=SLIDER_INTEGER,SliderWidth=8,Invert=0,name$="")
		Return Handle(Gadget(GUI_SLIDER,Parent,Orientation Or ValueType Or (Invert*16),X,Y,Width,Height,"",SliderWidth,MinVal,MaxVal,Val,name))
	End Function
	
	Function TrackBar(Parent,X,Y,Width,Height,MinVal#=0,MaxVal#=1,Val#=0,Segments=8,name$="")
		Return Handle(Gadget(GUI_TRACKBAR,Parent,0,X,Y,Width,Height,"",Segments,MinVal,MaxVal,Val,name))
	End Function
	
	Function TabStrip(Parent,X,Y,Width,Height,name$="")
		Return Handle(Gadget(GUI_TABSTRIP,Parent,0,X,Y,Width,Height,"",0,0,0,0,name))
	End Function
	
	Function TabPage(Strip,Caption$="",icon$="",name$="")
		Return Handle(Gadget(GUI_TABPAGE,Strip,0,0,0,0,0,Caption,0,0,0,0,name,icon))
	End Function
	
	Function TextBox(Parent,X,Y,Width,Height,Caption$="",name$="")
		Return Handle(Gadget(GUI_TEXTBOX,Parent,0,X,Y,Width,Height,Caption,0,0,0,0,name))
	End Function
	
	Function ContextMenu(name$="")
		Return Handle(Gadget(GUI_CONTEXTMENU,0,0,0,0,0,0,"",0,0,0,0,name))
	End Function
	
	Function ContextMenuItem(Menu,Caption$,icon$="",name$="")
		Return Handle(Gadget(GUI_CONTEXTMENUITEM,Menu,0,0,0,0,0,Caption,0,0,0,0,name,icon))
	End Function
	
	Function ContextMenuBar(Menu,name$="")
		Return Handle(Gadget(GUI_CONTEXTMENUBAR,Menu,0,0,0,0,0,"",0,0,0,0,name))
	End Function
	
	Function Label(Parent,X,Y,Caption$,name$="")
		Return Handle(Gadget(GUI_LABEL,Parent,0,X,Y,0,0,Caption,0,0,0,0,name))
	End Function
	
	Function Viewport3D(Parent,X,Y,Width,Height,name$="")
		Return Handle(Gadget(GUI_VIEWPORT,Parent,0,X,Y,Width,Height,0,0,0,0,0,name))
	End Function
	
	Function GetGadget(Name$)
		For g.Gadget = Each Gadget
			If g\Name = Name Then Return Handle(g)
		Next
		Return 0
	End Function
	
	Function CloseGadget(ID)
		i.Gadget = Object.Gadget(ID)
		If i = Null Then Return
		Select i\Class
			Case GUI_WINDOW
				i\State[WINDOW_CLOSED] = 1
			Case GUI_ROLLOUT
				i\State[ROLLOUT_CLOSED] = 1
		End Select
	End Function
	
	Function OpenGadget(ID)
		i.Gadget = Object.Gadget(ID)
		If i = Null Then Return
		Select i\Class
			Case GUI_WINDOW
				i\State[WINDOW_CLOSED] = 0
			Case GUI_ROLLOUT
				i\State[ROLLOUT_CLOSED] = 0
		End Select
	End Function
	
	Function GetViewportCamera(ID)
		i.Gadget = Object.Gadget(ID)
		If i = Null Then Return
		Return i\State[29]*(i\Class=GUI_VIEWPORT)
	End Function
	
	Function LockWindow(ID)
		i.Gadget = Object.Gadget(ID)
		If i = Null Then Return
		If i\Class = GUI_WINDOW Then i\State[WINDOW_LOCKED] = 1
	End Function
	
	Function UnlockWindow(ID)
		i.Gadget = Object.Gadget(ID)
		If i = Null Then Return
		If i\Class = GUI_WINDOW Then i\State[WINDOW_LOCKED] = 0
	End Function
	
	Function OpenContextMenu(ID,X=65535,Y=65535)
		i.Gadget = Object.Gadget(ID)
		If i = Null Then Return
		If i\Class = GUI_CONTEXTMENU Then
			GUI_ActiveContextMenu = i
			i\X = iMouseX()*(X=65535)+X*(X<>65535)
			i\Y = iMouseY()*(Y=65535)+Y*(Y<>65535)
		EndIf
	End Function
	
	Function ModalWindow(ID)
		i.Gadget = Object.Gadget(ID)
		GUI_ModalWindow = i
	End Function
	
	Function HideGadget(ID)
		i.Gadget = Object.Gadget(ID)
		If i = Null Then Return
		i\State[GADGET_HIDDEN] = 1
	End Function
	
	Function ShowGadget(ID)
		i.Gadget = Object.Gadget(ID)
		If i = Null Then Return
		i\State[GADGET_HIDDEN] = 0
	End Function
	
	Function GadgetHidden(ID)
		i.Gadget = Object.Gadget(ID)
		If i = Null Then Return -1
		Return i\State[GADGET_HIDDEN]
	End Function
	
	Function HideUIPointer()
		GUI_PointerHidden = 1
	End Function
	
	Function ShowUIPointer()
		GUI_PointerHidden = 0
	End Function
;#End Region


;#Region DESCRIPTION
	;; Functions for drawing the GUI and other operations pertaining to the generation of images
;#End Region

;#Region CLASSES
	Const GRAD_HOR = $5000		;; Horizontal gradient
	Const GRAD_VER = $5001		;; Vertical gradient
	Const GRAD_BOTH = $5002		;; Square gradient
	Const GRAD_RADIAL = $5003	;; Radial gradient		Starts in upper left corner
	Const GRAD_RHOR = $5004		;; Horizontal mirrored gradient
	Const GRAD_RVER = $5005		;; Vertical mirrored gradient
;#End Region

;#Region PROCEDURES
	Function DrawGUI()
		For i = 0 To Objects(GUI_RootGadgets)-1
			g.Gadget = Object.Gadget(GetObject(GUI_RootGadgets,i))
			DrawGadget(g)
		Next
		
		DrawGadget(GUI_ActiveContextMenu)
		
		If GUI_PointerHidden=0 Then DrawImage GUI_MouseCursor,iMouseX(),iMouseY()
	End Function
	
	Function DrawGadget(i.Gadget)
		If i = Null Then Return
		
		If i\State[30] = 1 Then Return
		
		AX = GetGadgetSX(i)
		AY = GetGadgetSY(i) + 16*(i\MenuStrip <> Null) + 20*(i\ToolStrip <> Null)
		
		r.Gadget = GetRoot(i)
		If r <> Null And r <> i Then
			If r\Class = GUI_CONTEXTMENU Then
				AX = GetGadgetSX(i\Parent)
				AY = GetGadgetSY(i\Parent)+i\Z*16
				
				If i\Over = 1 Then
					DrawImageRect GUI_ContextMenuItemImage,AX,AY,0,0,3,16
					DrawImageRect GUI_ContextMenuItemImage,AX+i\Width-3,AY,13,0,3,16
					For x = AX+3 To AX+i\Width-3 Step 10
						DrawImageRect GUI_ContextMenuItemImage,x,AY,3,0,MaxI(10,AX+i\Width-3-x),16
					Next
				EndIf
			EndIf
		EndIf
		
		Select i\Class
			Case GUI_WINDOW
				If i\State[29] = 1 Then Return
				If (i\Mode And WM_TITLEBAR) = WM_TITLEBAR Then
					AY = AY-(16*(i\MenuStrip <> Null) + 20*(i\ToolStrip <> Null))
					If i\Z = Objects(GUI_RootGadgets)-1 Then
						DrawImageRect i\Image,AX,AY,0,i\Height,i\Width,24
					Else
						DrawImageRect i\Image,AX,AY,0,i\Height+24,i\Width,24
					EndIf
					
					If (i\Mode And WM_CLOSEBTN) = WM_CLOSEBTN
						Select i\State[WINDOW_CLOSEBUTTON]
							Case 0
								DrawImageRect i\Image,AX+i\Width-20,AY+4,0,i\Height+48,16,16
							Case 1
								DrawImageRect i\Image,AX+i\Width-20,AY+4,32,i\Height+48,16,16
							Case 2
								DrawImageRect i\Image,AX+i\Width-20,AY+4,16,i\Height+48,16,16
						End Select
					EndIf
					
					If (i\Mode And WM_MINBTN) = WM_MINBTN
						Select i\State[WINDOW_MINBUTTON]
							Case 0
								DrawImageRect i\Image,AX+i\Width-20-20*((i\Mode And WM_CLOSEBTN) = WM_CLOSEBTN),AY+4,0,i\Height+48+16*(i\State[WINDOW_MINIMIZED]+1),16,16
							Case 1
								DrawImageRect i\Image,AX+i\Width-20-20*((i\Mode And WM_CLOSEBTN) = WM_CLOSEBTN),AY+4,32,i\Height+48+16*(i\State[WINDOW_MINIMIZED]+1),16,16
							Case 2
								DrawImageRect i\Image,AX+i\Width-20-20*((i\Mode And WM_CLOSEBTN) = WM_CLOSEBTN),AY+4,16,i\Height+48+16*(i\State[WINDOW_MINIMIZED]+1),16,16
						End Select
					EndIf
					AY = AY+(16*(i\MenuStrip <> Null) + 20*(i\ToolStrip <> Null))
				EndIf
				
				If i\State[WINDOW_MINIMIZED] = 1 Then Return
				
				DrawImageRect i\Image,AX,AY+24*((i\Mode And WM_TITLEBAR)=WM_TITLEBAR),0,0,i\Width,i\Height
			
			Case GUI_GROUPBOX
				DrawImage i\Image,AX,AY
			
			Case GUI_BUTTON
				Select i\State[0]
					Case 0		;; Normal
						DrawImageRect i\Image,AX,AY,0,0,i\Width,i\Height
					Case 1		;; Over
						DrawImageRect i\Image,AX,AY,0,i\Height,i\Width,i\Height
					Case 2		;; Down
						DrawImageRect i\Image,AX,AY,0,i\Height*2,i\Width,i\Height
					Case 3		;; Disabled
						DrawImageRect i\Image,AX,AY,0,i\Height*3,i\Width,i\Height
				End Select
			
			Case GUI_TEXTBOX
				DrawImage i\Image,AX,AY
				
				b = GB()
				SetBuffer(IB(i\State[31]))
				Color 255,255,255
				Rect 0,0,256,256
				SetFont GUI_Font
				Color 0,0,0
				Text 4,i\Height/2,i\Caption,0,1
				SetBuffer b
				
				DrawImage i\State[31],AX+1,AY+1
			
			Case GUI_ROLLOUT
				DrawImageRect i\Image,AX,AY,0,0,i\Width,16+(i\Height*(i\State[0] = 0))
				DrawImageRect i\Image,AX,AY,16*(i\State[0]=0),i\Height+16,16,16
				
				If i\State[0] = 1 Then DrawGadget(i\ToolStrip) DrawGadget(i\MenuStrip) Return 1
				
			Case GUI_RADIO,GUI_CHECKBOX
				Select i\State[TICK_CHECKED]
					Case 0
						DrawImageRect i\Image,AX,AY,0,0,ImageWidth(i\Image),16
					Case 1
						DrawImageRect i\Image,AX,AY,0,16,ImageWidth(i\Image),16
					Case 2
						DrawImageRect i\Image,AX,AY,0,32,ImageWidth(i\Image),16
					Case 3
						DrawImageRect i\Image,AX,AY,0,48,ImageWidth(i\Image),16
				End Select
				
				If i\Val Then
					Color 255,255,255
					Rect AX+4,AY+4,8,8,1
					Color 0,0,0
					Rect AX+4,AY+4,8,8,0
				EndIf
				
				i\Width = ImageWidth(i\Image)
				i\Height = 16
			
			Case GUI_LABEL
				DrawImage i\Image,AX+(ImageWidth(i\Image)/2)*(i\State[0] = 1),AY+(ImageHeight(i\Image)/2)*(i\State[1] = 1)
			
			Case GUI_VIEWPORT
				DrawImage i\Image,AX,AY
			
			Case GUI_MENUSTRIP
				If i\Parent <> Null Then
					Select i\Parent\Class
						Case GUI_ROLLOUT
							DrawImage i\Image,AX,AY+1-16
						Default
							DrawImage i\Image,AX,AY+1
					End Select
				Else
					DrawImage i\Image,0,0
				EndIf
			
			Case GUI_MENUITEM
				DrawImage i\Image,AX,AY
			
			Case GUI_CONTEXTMENU
				DrawImageRect i\Image,i\X-3,i\Y-3,0,0,3,3
				DrawImageRect i\Image,i\X+16-3,i\Y-3,13,0,3,3
				DrawImageRect i\Image,i\X-3,i\Y+i\Height,0,13,3,3
				DrawImageRect i\Image,i\X+16-3,i\Y+i\Height,13,13,3,3
				
				DrawImageRect i\Image,i\X+i\Width,i\Y-3,29,0,3,3
				DrawImageRect i\Image,i\X+i\Width,i\Y+i\Height,29,13,3,3
				
				For x = i\X+16 To i\X+i\Width Step 10
					DrawImageRect i\Image,x,i\Y-3,19,0,MaxI(10,i\X+i\Width-x),3
					DrawImageRect i\Image,x,i\Y+i\Height,19,13,MaxI(10,i\X+i\Width-x),3
					For y = i\Y To i\Y+i\Height Step 10
						DrawImageRect i\Image,x,y,19,3,MaxI(10,i\X+i\Width-x),MaxI(10,i\Y+i\Height-y)
					Next
				Next
				
				For y = i\Y To i\Y+i\Height Step 10
					DrawImageRect i\Image,i\X-3,y,0,3,3,MaxI(10,i\Y+i\Height-y)
					DrawImageRect i\Image,i\X+i\Width,y,29,3,3,MaxI(10,i\Y+i\Height-y)
					DrawImageRect i\Image,i\X+16-3,y,13,3,3,MaxI(10,i\Y+i\Height-y)					
				Next
				
				For x = i\X To i\X+16-3
					DrawImageRect i\Image,x,i\Y-3,3,0,3,MaxI(10,i\X+16-3-x)
					DrawImageRect i\Image,x,i\Y+i\Height,3,13,3,MaxI(10,i\X+16-3-x)
					For y = i\Y To i\Y+i\Height Step 10
						DrawImageRect i\Image,x,y,3,3,MaxI(10,i\X+16-3-x),MaxI(10,i\Y+i\Height-y)
					Next
				Next
			
			Case GUI_CONTEXTMENUITEM
				DrawImage i\Image,AX+2,AY+(16-ImageHeight(i\Image))/2
				If i\Icon <> 0 Then DrawImage i\Icon,AX-1,AY+(16-ImageHeight(i\Image))/2
				
			Case GUI_SLIDER
				v# = i\Val
				If (i\Mode And SLIDER_INTEGER) = SLIDER_INTEGER Then v = Int v
				
				DrawImageRect i\Image,AX,AY,0,0,i\Width,i\Height
				d# = i\Max-i\Min
				If d# = 0 Then d# = .01
				bar# = ((v-i\Min)/d)*i\State[1]
				If bar+1 = bar Then bar = 0
				
				If (i\Mode And SLIDER_HOR) = SLIDER_HOR Then
					p# = (v/d)*(i\Width-2)
					If p+1 = p Then p=0
					DrawImageRect i\Image,AX+1+p-bar,AY+1,i\Width,0,i\State[1],i\Height-2
				Else
					p# = (v/d)*(i\Height-2)
					If p+1 = p Then p=0
					DrawImageRect i\Image,AX+1,AY+1+p-bar,0,i\Height,i\Width-2,i\State[1]
				EndIf
			
			Case GUI_TRACKBAR
				DrawImageRect i\Image,AX,AY,0,0,i\Width+1,i\Height+4
				d# = i\Max-i\Min
				bar = Int(((i\Val-i\Min)/d))*i\State[2]
				p = Int(( (i\Val-i\Min) / d )*i\State[1])*i\State[2]
				DrawImageRect i\Image,AX+p-bar+1,AY+1,i\Width+1,0,i\State[2]-1,i\Height-2
			
			Case GUI_TABSTRIP
				DrawImage i\Image,AX,AY
			
			Case GUI_TABPAGE
				If i\Z <> i\Parent\State[0] Then
					DrawImageRect i\Image,AX+i\State[1],AY-(i\Height-2),0,i\Height,i\Width,i\Height-2
					Return
				Else
					DrawImageRect i\Image,AX+i\State[1],AY-i\Height,0,0,i\Width,i\Height
				EndIf
			
			Case GUI_IMAGEBOX
				DrawImage i\Image,AX,AY
			
			Case GUI_LISTBOX
				DrawImage i\Image,AX-1,AY-1
			
			Case GUI_LISTBOXITEM
				y = GetGadgetSY(i\Parent)
				AX = GetGadgetSX(i\Parent)
				AY = y+i\Z*20-i\State[LISTBOX_SCROLL]
				
				If AY < y+i\Parent\Height Then
					If i\Z = i\Parent\State[LISTBOX_SELECTED] Then
;						Stop
						Color 64,140,220
						Rect AX,AY,i\Width,MinI(MaxI((y+i\Parent\Height)-(AY),20),0),1
					EndIf
					
					DrawImageRect i\Image,AX,AY,0,0,i\Parent\Width,MinI(MaxI((y+i\Parent\Height)-(AY),20),0)
				EndIf
				
		End Select
		
		For n = 0 To Objects(i\Gadgets)-1
			g.Gadget = Object.Gadget(GetObject(i\Gadgets,n))
			DrawGadget(g)
		Next
	End Function
	
	Function CreateGUIImage(i.Gadget)
		b = GraphicsBuffer()
		
		Select i\Class
			Case GUI_WINDOW
				If i\State[WINDOW_CLOSED] = 1 Then Return
				hasTitle = ((i\Mode And WM_TITLEBAR) = WM_TITLEBAR)
				If hasTitle Then i\Height = i\Height - 24
				
				i\Image = CreateImage(i\Width,i\Height+96)
				
				SetBuffer(ImageBuffer(i\Image))
				
				Color 64,64,64
				Rect 0,0,MinI(i\Width,48),i\Height+96
				
				GradientRect(1,Not hasTitle,i\Width-2,i\Height-(2-hasTitle),i\Width,i\Height,WINDOW_DIALOG_F,WINDOW_DIALOG_T,GRAD_VER,2,1)
				
				Color 200,200,200
				Line 1,1,i\Width-2,1
				Line 1,i\Height-2,i\Width-2,i\Height-2
				Line 1,1,1,i\Height-2
				Line i\Width-2,1,i\Width-2,i\Height-2
				
				Color 61*.75,99*.75,139*.75
				Rect 0,i\Height,i\Width,24,1
				GradientRect(1,i\Height+1,i\Width-2,23-hasTitle,i\Width,i\Height+24*2,WINDOW_TITLE_ACTIVE_F,WINDOW_TITLE_ACTIVE_T,GRAD_VER,2,1)
				GradientRect(1,i\Height+1+24,i\Width-2,23-hasTitle,i\Width,i\Height+24*2,WINDOW_TITLE_INACTIVE_F,WINDOW_TITLE_INACTIVE_T,GRAD_VER,2,1)
				
				SetFont GUI_LFontBold
				
				If (i\Mode And WM_TITLEBARSHADOW) = WM_TITLEBARSHADOW Then
					Color 0,0,0
					For x = -1 To 1
						For y = -1 To 1
							Text 4+x,i\Height+12+y,i\Caption,0,1
							Text 4+x,i\Height+36+y,i\Caption,0,1
						Next
					Next
					Color 255,255,255
				Else
					Color 0,0,0
				EndIf
				
				Text 20*(i\Icon <> 0)+4,i\Height+12,i\Caption,0,1
				Text 20*(i\Icon <> 0)+4,i\Height+36,i\Caption,0,1
				
				If i\Icon <> 0 Then
					DrawImage i\Icon,2,i\Height+4
					DrawImage i\Icon,2,i\Height+28
				EndIf
				
				Color 32,32,32
				
				Rect 0,i\Height+48,i\Width,48
				
				GradientRect 1,i\Height+49,14,14,i\Width,i\Height+96,ColorToInt(190,190,190),ColorToInt(210,210,210),GRAD_VER,2,1,0
				GradientRect 1+16,i\Height+49,14,14,i\Width,i\Height+96,ColorToInt(190,190,190),ColorToInt(255,255,255),GRAD_VER,2,1,0
				GradientRect 1+32,i\Height+49,14,14,i\Width,i\Height+96,ColorToInt(220,220,220),ColorToInt(160,160,160),GRAD_VER,2,1,0
				
				GradientRect 1,i\Height+49+16,14,14,i\Width,i\Height+96,ColorToInt(190,190,190),ColorToInt(210,210,210),GRAD_VER,2,1,0
				GradientRect 1+16,i\Height+49+16,14,14,i\Width,i\Height+96,ColorToInt(190,190,190),ColorToInt(255,255,255),GRAD_VER,2,1,0
				GradientRect 1+32,i\Height+49+16,14,14,i\Width,i\Height+96,ColorToInt(220,220,220),ColorToInt(160,160,160),GRAD_VER,2,1,0
				
				GradientRect 1,i\Height+49+16*2,14,14,i\Width,i\Height+96,ColorToInt(190,190,190),ColorToInt(210,210,210),GRAD_VER,2,1,0
				GradientRect 1+16,i\Height+49+16*2,14,14,i\Width,i\Height+96,ColorToInt(190,190,190),ColorToInt(255,255,255),GRAD_VER,2,1,0
				GradientRect 1+32,i\Height+49+16*2,14,14,i\Width,i\Height+96,ColorToInt(220,220,220),ColorToInt(160,160,160),GRAD_VER,2,1,0
				
				Color 255,255,255
				Line 4,i\Height+48+4+1,11,i\Height+48+11+1
				Line 11,i\Height+48+4+1,4,i\Height+48+11+1
				
				Line 4+16,i\Height+48+4+1,11+16,i\Height+48+11+1
				Line 11+16,i\Height+48+4+1,4+16,i\Height+48+11+1
				
				Line 4+32,i\Height+48+4+2,11+32,i\Height+48+11+2
				Line 11+32,i\Height+48+4+2,4+32,i\Height+48+11+2
				
				Rect 3,i\Height+48+3+1+16,10,3,0
				Rect 3,i\Height+48+3+1+32,10,3,0
				Rect 3,i\Height+48+3+1+32,10,9,0
				
				Rect 3+16,i\Height+48+3+1+16,10,3,0
				Rect 3+16,i\Height+48+3+1+32,10,3,0
				Rect 3+16,i\Height+48+3+1+32,10,9,0
				
				Rect 3+16*2,i\Height+48+3+2+16,10,3,0
				Rect 3+16*2,i\Height+48+3+2+32,10,3,0
				Rect 3+16*2,i\Height+48+3+2+32,10,9,0
				
				Color 0,0,0
				
				Rect 3,i\Height+48+3+16,10,3,0
				Rect 3,i\Height+48+3+32,10,3,0
				Rect 3,i\Height+48+3+32,10,9,0
				
				Rect 3+16,i\Height+48+3+16,10,3,0
				Rect 3+16,i\Height+48+3+32,10,3,0
				Rect 3+16,i\Height+48+3+32,10,9,0
				
				Rect 3+16*2,i\Height+48+3+1+16,10,3,0
				Rect 3+16*2,i\Height+48+3+1+32,10,3,0
				Rect 3+16*2,i\Height+48+3+1+32,10,9,0
				
				Line 4,i\Height+48+4,11,i\Height+48+11
				Line 11,i\Height+48+4,4,i\Height+48+11
				
				Line 4+16,i\Height+48+4,11+16,i\Height+48+11
				Line 11+16,i\Height+48+4,4+16,i\Height+48+11
				
				Line 4+32,i\Height+48+4+1,11+32,i\Height+48+11+1
				Line 11+32,i\Height+48+4+1,4+32,i\Height+48+11+1
				
			Case GUI_BUTTON
				i\Image = CreateImage(i\Width+24*(i\Icon<>0),i\Height*4)
				
				SetBuffer(IB(i\Image))
				
				Color 64,64,64
				Rect 0,0,i\Width+24*(i\Icon<>0),i\Height+24*2				
				
				GradientRect(1,1,i\Width-2+24*(i\Icon<>0),i\Height-2,i\Width+24*(i\Icon<>0),i\Height*4,ColorToInt(190,190,190),ColorToInt(230,230,230),GRAD_VER,2,1)
				
				GradientRect(1,1+i\Height*1,i\Width-2+24*(i\Icon<>0),i\Height-2,i\Width+24*(i\Icon<>0),i\Height*4,ColorToInt(190,190,190),ColorToInt(255,255,255),GRAD_VER,2,1)
				
				GradientRect(1,1+i\Height*2,i\Width-2+24*(i\Icon<>0),i\Height-2,i\Width+24*(i\Icon<>0),i\Height*4,ColorToInt(230,230,230),ColorToInt(190,190,190),GRAD_VER,1,1)
				
				GradientRect(1,1+i\Height*3,i\Width-2+24*(i\Icon<>0),i\Height-2,i\Width+24*(i\Icon<>0),i\Height*4,ColorToInt(130,130,130),ColorToInt(150,150,150),GRAD_VER,2,1)
				
				SetFont GUI_Font
				Color 48,48,48
				Text i\Width/2+24*(i\Icon<>0),i\Height/2,i\Caption,1,1
				Color 0,0,0
				Text i\Width/2+24*(i\Icon<>0),i\Height/2+i\Height,i\Caption,1,1
				Color 128,128,128
				Text i\Width/2+24*(i\Icon<>0),i\Height/2+i\Height*3,i\Caption,1,1
				Color 0,0,0
				Text i\Width/2+1+24*(i\Icon<>0),i\Height/2+i\Height*2+1,i\Caption,1,1
				
				If i\Icon <> 0 Then
					DrawImage i\Icon,2,4
					DrawImage i\Icon,2,i\Height+4
					DrawImage i\Icon,2,i\Height*2+4
					DrawImage i\Icon,2,i\Height*3+4
				EndIf
				
			Case GUI_GROUPBOX
				i\Image = CreateImage(i\Width,i\Height+1)
				
				SetBuffer(IB(i\Image))
				
				Color 255,0,255
				Rect 0,0,i\Width,i\Height+1,1
				
				SetFont GUI_Font
				bLine 1,4,8,4
				bLine 1,4,1,i\Height-1
				bLine 14+StringWidth(i\Caption)+24*(i\Icon <> 0),4,i\Width-3,4
				bLine i\Width-2,4,i\Width-2,i\Height-1
				bLine 1,i\Height-1,i\Width-2,i\Height-1
				Color 0,0,0
				Text 12+24*(i\Icon <> 0),4,i\Caption,0,1
				
			Case GUI_TEXTBOX
				i\Image = CreateImage(i\Width,i\Height)
				
				Color 0,0,0
				Rect 0,0,i\Width,i\Height,1
				
				Color 255,255,255
				Rect 1,1,256,256,1
				
				i\State[31] = CreateImage(i\Width-2,i\Height-2)
				MaskImage i\State[31],255,0,255
			
			Case GUI_RADIO,GUI_CHECKBOX
				SetFont GUI_Font
				i\Image = CreateImage(18+StringWidth(i\Caption),64)
				
				w = ImageWidth(i\Image)
				
				SetBuffer(IB(i\Image))
				
				Color 255,0,255
				Rect 0,0,w,64,1
				
				Color 0,0,0
				SetFont GUI_Font
				Text 18,8,i\Caption,0,1
				Text 18,8+16,i\Caption,0,1
				Text 18,8+16*2,i\Caption,0,1
				Color 128,128,128
				Text 18,8+16*3,i\Caption,0,1
				
				;; Normal
				Color 0,0,0
				Rect 1,1,14,14,1
				
				Color 110,190,255
				Rect 2,2,12,12,1
				
				Color 100,120,150
				Rect 4,4,8,8,1
				
				;; Over
				Color 0,0,0
				Rect 1,1+16,14,14,1
				
				Color 160,220,255
				Rect 2,2+16,12,12,1
				
				Color 100,120,150
				Rect 4,4+16,8,8,1
				
				;; Down
				Color 0,0,0
				Rect 1,1+16*2,14,14,1
				
				Color 110,190,255
				Rect 2,2+16*2,12,12,1
				
				Color 100,120,150
				Rect 4,4+16*2,8,8,1
				
				;; Disabled
				Color 0,0,0
				Rect 1,1+16*3,14,14,1
				
				Color 110,190,255
				Rect 2,2+16*3,12,12,1
				
				Color 100,120,150
				Rect 4,4+16*3,8,8,1
			
			Case GUI_ROLLOUT
				i\Image = CreateImage(i\Width,i\Height+32)
				SetBuffer(IB(i\Image))
				Color 0,0,0
				Rect 0,0,i\Width,i\Height+16,1
				GradientRect 1,1,i\Width-2,14,i\Width,i\Height+16,ColorToInt(250,250,250),ColorToInt(230,230,230),GRAD_VER,2,2,0
				Color 255,0,255
				Rect 0,i\Height+16,i\Width,16
				Color 0,0,0
				SetFont GUI_Font
				Text i\Width/2,8,i\Caption,1,1
				
				Color 0,0,0
				Rect 4,-1+i\Height+16+7,8,2,1
				Rect 4+16,-1+i\Height+16+7,8,2,1
				Rect 16+7,-1+i\Height+16+4,2,8,1
				
				Color 255,255,255
				Rect 4,1+i\Height+16+7,8,2,1
				Rect 4+16,1+i\Height+16+7,8,2,1
				Rect 16+7,1+i\Height+16+4,2,8,1
				
				Color 96,96,96
				Rect 4,i\Height+16+7,8,2,1
				Rect 4+16,i\Height+16+7,8,2,1
				Rect 16+7,i\Height+16+4,2,8,1
				
				Color 255,0,255
				Rect 1,16,i\Width-2,i\Height-1
				
			Case GUI_LABEL
				SetFont GUI_Font
				i\Image = CreateImage(StringWidth(i\Caption),FontHeight())
				SetBuffer(IB(i\Image))
				Color 255,0,255
				Rect 0,0,ImageWidth(i\Image),ImageHeight(i\Image),1
				Color 0,0,0
				Text 0,0,i\Caption,0,0
			
			Case GUI_VIEWPORT
				i\State[29] = CreateCamera()
				i\Image = CreateImage(i\Width,i\Height+1)
				CameraViewport i\State[29],0,0,i\Width-2,i\Height-2
				HideEntity i\State[29]
				
				SetBuffer(IB(i\Image))
				Color 230,230,230
				Rect 0,0,i\Width,i\Height+1,1
				Color 0,0,0
				Rect 0,0,i\Width,i\Height,1
			
			Case GUI_MENUSTRIP
				If i\Parent <> Null Then
					i\Width = i\Parent\Width
				Else
					i\Width = GraphicsWidth( )
				EndIf
				i\Height = 16
				i\X = 0
				i\Y = -16
				i\Image = CreateImage(i\Width,i\Height)
				
				SetBuffer(IB(i\Image))
				Color 0,0,0
				Rect 0,0,i\Width,i\Height,1
				GradientRect 1,1,i\Width-2,i\Height-1,i\Width,i\Height,ColorToInt(240,240,240),ColorToInt(220,220,220),GRAD_VER,2,1,0
			
			Case GUI_CONTEXTMENU
				i\Image = GUI_ContextMenuImage
			
			Case GUI_CONTEXTMENUITEM
				i\Parent\Width = MinI(i\Parent\Width,20+StringWidth(i\Caption))
				i\Image = CreateImage(20+StringWidth(i\Caption),16)
				Color 255,0,255
				SetBuffer(IB(i\Image))
				Rect 0,0,ImageWidth(i\Image),16,1
				Color 0,0,0
				SetFont GUI_Font
				Text 18,8,i\Caption,0,1
			
			Case GUI_SLIDER
				If (i\Mode And SLIDER_HOR) Then
					i\Image = CreateImage(i\State[1]+i\Width,i\Height)
					w = i\Width+i\State[1]
					h = i\Height
					SetBuffer(IB(i\Image))
					
					Color 64,64,64
					
					Rect 0,0,ImageWidth(i\Image),ImageHeight(i\Image)
					
					GradientRect 1,1,i\Width-2,i\Height-2,i\Width,i\Height,ColorToInt(170,230,255),ColorToInt(130,175,210),GRAD_HOR,1,1,0
					
					GradientRect i\Width+1,0,i\State[1]-2,i\Height-2,w*2,h*2,ColorToInt(220,220,220),ColorToInt(190,190,190),GRAD_VER,2,1,0
				Else
					i\Image = CreateImage(i\Width,i\Height+i\State[1])
					w = i\Width
					h = i\Height+i\State[1]
					SetBuffer(IB(i\Image))
					
					Color 64,64,64
					
					Rect 0,0,ImageWidth(i\Image),ImageHeight(i\Image)
					
					GradientRect 1,1,i\Width-2,i\Height-2,i\Width,i\Height,ColorToInt(170,230,255),ColorToInt(130,175,210),GRAD_VER,1,1,0
					
					GradientRect 0,i\Height+1,i\Width-2,i\State[1]-2,w*2,h*2,ColorToInt(220,220,220),ColorToInt(190,190,190),GRAD_VER,2,1,0
				EndIf
			
			Case GUI_TRACKBAR
				st = Float i\Width/i\State[1]
				
				i\Image = CreateImage(i\Width+1+st,i\Height+4)
				SetBuffer(IB(i\Image))
				
				Color 255,0,255
				Rect 0,0,i\Width+st,i\Height+4,1
				
				GradientRect 1,1,i\Width-2,i\Height-2,i\Width,i\Height,ColorToInt(200,200,200),ColorToInt(220,220,220),GRAD_VER,0
				
				i\State[2] = st
				x=st
				While x < i\Width
					Color 255,255,255
					Rect x+1,0,1,i\Height,1
					Color 64,64,64
					Rect x,0,1,i\Height,1
					x = x + st
				Wend
				
				Color 255,255,255
				Rect 0,0,i\Width+1,i\Height+1,0
				Color 64,64,64
				Rect 0,0,i\Width,i\Height,0
				
				x=0
				While x < i\Width
					Color 255,255,255
					Rect x+1,i\Height,1,4,1
					Color 64,64,64
					Rect x,i\Height,1,3,1
					x = x + st
				Wend
				x = x - 1
				Color 255,255,255
				Rect x+1,i\Height,1,4,1
				Color 64,64,64
				Rect x,i\Height,1,3,1
				
				GradientRect i\Width+1,0,st-1,i\Height-2,i\Width+st+2,i\Height+4,ColorToInt(66,157,221),ColorToInt(32,116,175),GRAD_BOTH,2,1,0
				
			Case GUI_TABSTRIP
				i\Image = CreateImage(i\Width,i\Height)
				SetBuffer(IB(i\Image))
				Color 64,64,64
				Rect 0,0,i\Width,i\Height,1
				GradientRect 1,1,i\Width-2,i\Height-2,i\Width,i\Height,ColorToInt(180,180,180),ColorToInt(170,170,170),GRAD_VER,0,0,0
			
			Case GUI_TABPAGE
				SetFont GUI_Font
				i\Image = CreateImage(8+StringWidth(i\Caption)+20*(i\Icon <> 0),44)
				SetBuffer(IB(i\Image))
				i\Width = 8+StringWidth(i\Caption)+20*(i\Icon <> 0)
				i\Height = 22
				Color 64,64,64
				Rect 0,0,i\Width,i\Height*2,1
				GradientRect 1,1,i\Width-2,i\Height-1,i\Width,i\Height*2,ColorToInt(220,220,220),ColorToInt(225,225,225),GRAD_VER,2,1,0
				GradientRect 1,i\Height+1,i\Width-2,i\Height-1,i\Width,i\Height*2,ColorToInt(190,190,190),ColorToInt(198,198,198),GRAD_VER,2,1,0
				Text 4+18*(i\Icon <> 0),i\Height/2,i\Caption,0,1
				Text 4+18*(i\Icon <> 0),i\Height*1.5,i\Caption,0,1
				
				i\State[1] = 0
				i\X = 0
				i\Y = 0
				For g.Gadget = Each Gadget
					If g <> i And g\Parent = i\Parent And g\Class = i\Class Then
						i\State[1] = i\State[1] + g\Width-1
					EndIf
				Next
				
				If i\Icon <> 0 Then
					DrawImage i\Icon,3,3
					DrawImage i\Icon,3,3+22
				EndIf
			
			Case GUI_IMAGEBOX
				i\Image = CreateImage(ImageWidth(i\GroupID)+2,ImageHeight(i\GroupID)+2)
				i\Width = ImageWidth(i\Image)
				i\Height = ImageWidth(i\Image)
				SetBuffer(IB(i\Image))
				Color 64,64,64
				Rect 0,0,i\Width,i\Height,0
			
			Case GUI_LISTBOX
				i\Image = CreateImage(i\Width+2,i\Height+2)
				SetBuffer(IB(i\Image))
				Color 0,0,0
				Rect 0,0,i\Width+2,i\Height+2,1
				Color 255,255,255
				Rect 1,1,i\Width,i\Height,1
			
			Case GUI_LISTBOXITEM
				i\Image = CreateImage(i\Parent\Width,20)
				SetBuffer(IB(i\Image))
				Color 255,0,255
				Rect 0,0,i\Parent\Width,20,1
				Color 0,0,0
				SetFont GUI_Font
				Text 2+18*(i\Icon <> 0),10,i\Caption,0,1
				If i\Icon <> 0 Then
					DrawImage i\Icon,2,2
				EndIf
				
		End Select
		
		If i\Image <> 0 Then MaskImage i\Image,255,0,255
		
		SetBuffer b
	End Function
	
	Function UpdateViewport(ID,Tween#=1,ClearColor=1,ClearDepth=1,CopyToBuffer=1)
		g.Gadget = Object.Gadget(ID)
		If g <> Null Then
			If g\Class = GUI_VIEWPORT Then
				ShowEntity g\State[29]
				CameraClsMode g\State[29],ClearColor,ClearDepth
				RenderWorld Tween
				If CopyToBuffer Then CopyRect 0,0,g\Width-2,g\Height-2,1,1,BackBuffer(),ImageBuffer(g\Image)
				HideEntity g\State[29]
			EndIf
		EndIf
	End Function
	
	Function GradientRect(X,Y,W,H,Mx,My,CF,CT,Dir=GRAD_HOR,Bevel=0,BevelSize=1,Blend=0)
		Local RF = CF Shr 16 And 255
		Local GF = CF Shr 8 And 255
		Local BF = CF And 255
		
		Local RT = CT Shr 16 And 255
		Local GT = CT Shr 8 And 255
		Local BT = CT And 255
		
		Local ix,iy
		
		Local b = GraphicsBuffer()
		
		LockBuffer(b)
		
		Local d# = 0
		
		For ix = 0 To W-1
			For iy = 0 To H-1
				If X+ix < Mx And Y+iy < My And X+ix > -1 And Y+iy > -1 Then
					Select Dir
						Case GRAD_HOR
							d# = 1.0 - Float ix/W
						Case GRAD_VER
							d# = 1.0 - Float iy/H
						Case GRAD_BOTH,GRAD_RADIAL
							d# = 1.0 - (Float iy/H + Float ix/W)/2
						Case GRAD_RHOR
							d# = Float Abs(ix-w/2)/(W/2)
						Case GRAD_RVER
							d# = Float Abs(iy-h/2)/(H/2)
					End Select
					
					cr = RF*d + RT*(1.0-d)
					cg = GF*d + GT*(1.0-d)
					cb = BF*d + BT*(1.0-d)
					
					Select Blend
						Case 1		;; Multiply
							pix = ReadPixelFast(X+ix,Y+iy)
							pr = pix Shr 16 And 255
							pg = pix Shr 8 And 255
							pb = pix And 255
							cr = cr*(Float pr/255)
							cg = cg*(Float pg/255)
							cb = cb*(Float pb/255)
						Case 2		;; Additive
							pix = ReadPixelFast(X+ix,Y+iy)
							pr = pix Shr 16 And 255
							pg = pix Shr 8 And 255
							pb = pix And 255
							cr = cr + pr
							cg = cg + pg
							cb = cb + pb
						Case 3		;; The two combined
							pix = ReadPixelFast(X+ix,Y+iy)
							pr = pix Shr 16 And 255
							pg = pix Shr 8 And 255
							pb = pix And 255
							If pr > 127 Then
								cr = cr + pr
							Else
								cr = cr*(Float pr/128)
							EndIf
							
							If pg > 127 Then
								cg = cg + pg
							Else
								cg = cg*(Float pg/128)
							EndIf
							
							If pb > 127 Then
								cb = cb + pb
							Else
								cb = cb*(Float pb/128)
							EndIf
					End Select
					
					
					Select Bevel
						Case 1
							If ix <= BevelSize-1 Or iy <= BevelSize-1
								cr = MaxI(cr * .6,255)
								cg = MaxI(cg * .6,255)
								cb = MaxI(cb * .6,255)
							EndIf
							If ix => W-BevelSize+1 Or iy => H-BevelSize+1
								cr = MaxI(cr * 1.4,255)
								cg = MaxI(cg * 1.4,255)
								cb = MaxI(cb * 1.4,255)
							EndIf
						Case 2
							If ix => W-BevelSize+1 Or iy => H-BevelSize+1
								cr = MaxI(cr * .6,255)
								cg = MaxI(cg * .6,255)
								cb = MaxI(cb * .6,255)
							EndIf
							If ix <= BevelSize-1 Or iy <= BevelSize-1
								cr = MaxI(cr * 1.4,255)
								cg = MaxI(cg * 1.4,255)
								cb = MaxI(cb * 1.4,255)
							EndIf
					End Select
					
					
					c = 255 Shl 24 Or MaxI(cr,255) Shl 16 Or MaxI(cg,255) Shl 8 Or MaxI(cb,255)
					
					WritePixelFast X+ix,Y+iy,c,b
				EndIf
			Next
		Next
		
		UnlockBuffer(b)
		
		Return
	End Function
	
	Function bLine(X,Y,TX,TY)
		Color 64,64,64
		Line X,Y,TX,TY
		Color 225,225,225
		Line X+1,Y+1,TX+1,TY+1
	End Function
	
	Function ColorToInt(R%,G%,B%,A%=255)
		Return A Shl 24 Or R Shl 16 Or G Shl 8 Or B
	End Function
;#End Region

;#Region DESCRIPTION
	;; Convenience functions for returning buffer addresses
;#End Region

;#Region PROCEDURES
	Function IB(Image)
		Return ImageBuffer(Image)
	End Function
	
	Function TB(Texture)
		Return TextureBuffer(Texture)
	End Function
	
	Function GB()
		Return GraphicsBuffer()
	End Function
	
	Function BB()
		Return BackBuffer()
	End Function
	
	Function FB()
		Return FrontBuffer()
	End Function
;#End Region

;#Region DESCRIPTION
	;; Convenience functions for getting the minimum/maximum values of two values
;#End Region

;#Region PROCEDURES
	Function MinF#(A#,B#)
		If A < B Then Return B
		Return A
	End Function
	
	Function MinI%(A%,B%)
		If A < B Then Return B
		Return A
	End Function
	
	Function MaxF#(A#,B#)
		If A > B Then Return B
		Return A
	End Function
		
	Function MaxI%(A%,B%)
		If A > B Then Return B
		Return A
	End Function

;#End Region
