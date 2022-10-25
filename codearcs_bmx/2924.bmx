; ID: 2924
; Author: matibee
; Date: 2012-02-28 11:04:33
; Title: Side tabber
; Description: Custom tabview with side-on tabs

SuperStrict 
Import maxgui.drivers

Incbin "incbin//sidetab_leftedge.png"
Incbin "incbin//sidetab_rightedge.png"
Incbin "incbin//sidetab_topedge.png"
Incbin "incbin//sidetab_btmedge.png"

Incbin "incbin//sidetab_coltop_active.png"
Incbin "incbin//sidetab_coltop_inactive.png"

Incbin "incbin//sidetab_colleft_active.png"
Incbin "incbin//sidetab_colleft_inactive.png"

Incbin "incbin//sidetab_colmidtop_active.png"
Incbin "incbin//sidetab_colmidbtm_active.png"

Incbin "incbin//sidetab_colmid_inactive.png"

Incbin "incbin//sidetab_colbtm_active.png"
Incbin "incbin//sidetab_colbtm_inactive.png"

Incbin "incbin//sidetab_corner_tl.png"
Incbin "incbin//sidetab_corner_tr.png"
Incbin "incbin//sidetab_corner_bl.png"
Incbin "incbin//sidetab_corner_br.png"

'-----------------------------------------------------------------------------------------------------------------
'----------------------------------------------------------------------------------------------------------------------
Type sideTabPixmapPair
	Field activePixmap:TPixmap
	Field inactivePixmap:TPixmap
End Type 
'----------------------------------------------------------------------------------------------------------------------
Type sideTab
	
	Global stImages:TPixmap[17]
	Global STIC_LEFT_EDGE%		= 	0
	Global STIC_RIGHT_EDGE%		= 	1
	Global STIC_TOP_EDGE%		= 	2
	Global STIC_BTM_EDGE%		= 	3
	Global STIC_COL_TOP_ACTIVE%	= 	4
	Global STIC_COL_TOP_INACTIVE% = 	5
	Global STIC_COL_LEFT_ACTIVE%	= 	6
	Global STIC_COL_LEFT_INACTIVE% = 	7
	Global STIC_COL_MID_TOP_ACTIVE% = 	8
	Global STIC_COL_MID_BTM_ACTIVE% = 	9
	Global STIC_COL_MID_INACTIVE%	= 	10
	Global STIC_COL_BTM_ACTIVE% 	= 	11
	Global STIC_COL_BTM_INACTIVE% = 	12
	Global STIC_TL_CORNER%		=	13
	Global STIC_TR_CORNER%		=	14
	Global STIC_BL_CORNER%		=	15
	Global STIC_BR_CORNER%		= 	16
	
	'-----------------------------------------------------------------------------------------------------------------
	Global borderSize:Int		' pixel size taken from the images
	Global tabWidth:Int			' pixel size taken from the images
	Global sideTabList:TList		' a list of all created sideTabs for the event handler

	'-----------------------------------------------------------------------------------------------------------------
	Field parent:TGadget		' parent gadget of this tabview
	Field xPos:Int				' position inside the parent
	Field yPos:Int
	Field currentTab:Int = -1 	' currently active tab (this is invalid before any tabs are created)
	Field leftBorderPanel:TGadget ' the only panel we have to resize when the user adds a tab
	Field tabButtons:TList		' an active panel for each tab, that acts as a clickable area
	Field leftColumnPanels:TList	' list of panels that make up the left column of images
	Field tabPixmaps:TList		' a list of pixmaps in use
	Field managedPanels:TList 	' a panel is automatically created for each newly created tab
	Field client:TGadget		' the main "client area" (a panel) inside the tab view
	'-----------------------------------------------------------------------------------------------------------------
	'-----------------------------------------------------------------------------------------------------------------	
	Function Create:sideTab( parent:TGadget, X:Int, Y:Int, Width:Int, Height:Int, r:Int, g:Int, b:Int ) 
		
		Local st:sideTab = New sideTab
		
		If ( sideTabList = Null )
			sideTabList = New TList 
			AddHook EmitEventHook, eventHandler, Null, 1
		End If 
		
		sideTabList.AddLast( st )
	
		st.parent = parent
		st.managedPanels = New TList 

		st.tabButtons = New TList 
		st.leftColumnPanels = New TList
		st.tabPixmaps = New TList 
		st.xPos = X
		st.yPos = Y
		
		If ( stImages[0] = Null )
			LoadImages()
		End If 
		
		Local x2:Int = X + tabWidth			' start of first corner
		Local x3:Int = x2 + borderSize		' start of client area
		Local x4:Int = X + Width - bordersize	' end of client area
		
		Local y2:Int = Y + borderSize			' start of client area
		Local y3:Int = Height - bordersize
		
		Local cliHeight:Int = Height - bordersize * 2
		Local cliWidth:Int = Width - tabWidth - bordersize * 2
		
		
		Local p:Tgadget = CreatePanel( x2, Y, borderSize, borderSize, parent ) ' top left corner
		SetGadgetLayout( p, 1,0,1,0 )
		SetGadgetPixmap( p, stImages[STIC_TL_CORNER] )
				
		p = CreatePanel( x3, Y, cliWidth, bordersize, parent ) 	' top border
		SetGadgetLayout( p, 1,1,1,0 )
		SetGadgetPixmap( p, stImages[STIC_TOP_EDGE] )
				
		p = CreatePanel( x4, Y, bordersize, bordersize, parent ) 	' top right corner
		SetGadgetLayout( p, 0,1,1,0 )
		SetGadgetPixmap( p, stImages[STIC_TR_CORNER] )
				
		st.client = CreatePanel( x3, y2, cliWidth, cliHeight, parent ) 	' client area
		SetGadgetLayout( st.client, 1,1,1,1 )
		SetPanelColor( st.client, r, g, b )
				
		st.leftBorderPanel = CreatePanel( x2, y2, bordersize, cliHeight, parent ) ' left border
		SetGadgetLayout( st.leftBorderPanel, 1,0,1,1 )
		SetGadgetPixmap( st.leftBorderPanel, stImages[STIC_LEFT_EDGE] )
		
		p = CreatePanel( x4, y2, bordersize, cliHeight, parent ) 	' right border
		SetGadgetLayout( p, 0,1,1,1 )
		SetGadgetPixmap( p, stImages[STIC_RIGHT_EDGE] )
		
		p = CreatePanel( x2, y3, bordersize, bordersize, parent ) 	' bottom left corner
		SetGadgetLayout( p, 1,0,0,1 )
		SetGadgetPixmap( p, stImages[STIC_BL_CORNER] )

		p = CreatePanel( x4, y3, bordersize, bordersize, parent ) 	' bottom right corner
		SetGadgetLayout( p, 0,1,0,1 )
		SetGadgetPixmap( p, stImages[STIC_BR_CORNER] )
		
		p = CreatePanel( x3, y3, cliWidth, bordersize, parent ) 	' bottom border
		SetGadgetLayout( p, 1,1,0,1 )
		SetGadgetPixmap( p, stImages[STIC_BTM_EDGE] )
		
		Return st
	End Function 
	'-----------------------------------------------------------------------------------------------------------------	
	'-----------------------------------------------------------------------------------------------------------------
	Function LoadImages()
		stImages[STIC_LEFT_EDGE] = LoadPixmap( "incbin::incbin//sidetab_leftedge.png" )
		stImages[STIC_RIGHT_EDGE] = LoadPixmap( "incbin::incbin//sidetab_rightedge.png" )
		stImages[STIC_TOP_EDGE] = LoadPixmap( "incbin::incbin//sidetab_topedge.png" )
		stImages[STIC_BTM_EDGE] = LoadPixmap( "incbin::incbin//sidetab_btmedge.png" )
		stImages[STIC_COL_TOP_ACTIVE] = LoadPixmap( "incbin::incbin//sidetab_coltop_active.png" )
		stImages[STIC_COL_TOP_INACTIVE] = LoadPixmap( "incbin::incbin//sidetab_coltop_inactive.png" )
		stImages[STIC_COL_LEFT_ACTIVE] = LoadPixmap( "incbin::incbin//sidetab_colleft_active.png" )
		stImages[STIC_COL_LEFT_INACTIVE] = LoadPixmap( "incbin::incbin//sidetab_colleft_inactive.png" )
		stImages[STIC_COL_MID_TOP_ACTIVE] = LoadPixmap( "incbin::incbin//sidetab_colmidtop_active.png" )
		stImages[STIC_COL_MID_BTM_ACTIVE] = LoadPixmap( "incbin::incbin//sidetab_colmidbtm_active.png" )
		stImages[STIC_COL_MID_INACTIVE] = LoadPixmap( "incbin::incbin//sidetab_colmid_inactive.png" )
		stImages[STIC_COL_BTM_ACTIVE] = LoadPixmap( "incbin::incbin//sidetab_colbtm_active.png" )
		stImages[STIC_COL_BTM_INACTIVE] = LoadPixmap( "incbin::incbin//sidetab_colbtm_inactive.png" )
		stImages[STIC_TL_CORNER] = LoadPixmap( "incbin::incbin//sidetab_corner_tl.png" )
		stImages[STIC_TR_CORNER] = LoadPixmap( "incbin::incbin//sidetab_corner_tr.png" )
		stImages[STIC_BL_CORNER] = LoadPixmap( "incbin::incbin//sidetab_corner_bl.png" )
		stImages[STIC_BR_CORNER] = LoadPixmap( "incbin::incbin//sidetab_corner_br.png" )
		borderSize = PixmapWidth( stImages[STIC_TL_CORNER] ) ' borders must be square
		tabWidth = PixmapWidth( stImages[STIC_COL_BTM_ACTIVE] ) - borderSize
	End Function 
	'-----------------------------------------------------------------------------------------------------------------
	'-----------------------------------------------------------------------------------------------------------------
	Function eventHandler:Object( pID%, pData:Object, pContext:Object )
		Local event:TEvent = TEvent(pData)
		If event
			If ( event.id = EVENT_MOUSEUP )
				Local tabber:sideTab
				For tabber = EachIn sideTabList
					Local i:Int = 0
					For Local b:TGadget = EachIn tabber.tabButtons
						If ( event.source = b )
							tabber.ActivateTab( i )
							Return event
						End If 
						i :+ 1
					Next 
				Next 
			End If 
		EndIf
		Return pData
	EndFunction
	'-----------------------------------------------------------------------------------------------------------------
	'-----------------------------------------------------------------------------------------------------------------	
	Method AddTab:TGadget( activePixmap:TPixmap, inactivePixmap:TPixmap )
		
		If ( currentTab < 0 ) currentTab = 0
		
		Local p:TGadget
		Local yStart:Int = yPos + borderSize
		
		For Local p:TGadget = EachIn leftColumnPanels
			yStart :+ GadgetHeight( p )
		Next 
		
		Local yEnd:Int = yStart + borderSize
		
		If ( leftColumnPanels.Count() = 0 )
			p = CreatePanel( xPos, yStart, tabwidth + borderSize, borderSize, parent )
			SetGadgetLayout( p, 1,0,1,0 )
			leftColumnPanels.AddLast( p )
		Else 
			yStart :- borderSize
			yEnd :- borderSize
		End If 
		
		p = CreatePanel( xPos, yStart + bordersize, bordersize, PixmapHeight( activePixmap ), parent )
		SetGadgetLayout( p, 1,0,1,0)

		yEnd :+ PixmapHeight( activePixmap )
		leftColumnPanels.AddLast( p )
		
		Local pp:sideTabPixmapPair = New sideTabPixmapPair
		pp.activePixmap = activePixmap
		pp.inactivePixmap = inactivePixmap
		tabPixmaps.AddLast( pp )
		
		p = CreatePanel( xpos, yStart + borderSize + PixmapHeight( activePixmap ), tabWidth + borderSize, ..
						borderSize, parent )
		SetGadgetLayout( p, 1,0,1,0 )

		yEnd :+ borderSize
		leftColumnPanels.AddLast( p )
		p = CreatePanel( xpos + borderSize, yStart + borderSize, tabWidth + bordersize, ..
						PixmapHeight( activePixmap ), parent, PANEL_ACTIVE  )
		SetGadgetLayout( p, 1,0,1,0 )
		
		If ( tabButtons.Count() = 0 )
			SetPanelPixmap( p, activePixmap )
		Else 
			SetPanelPixmap( p, inactivePixmap )
		End If 
		tabButtons.AddLast( p )
		
		SetGadgetShape( leftBorderPanel, xpos + tabWidth, yEnd, borderSize, ..
						yPos + GadgetHeight( client ) - yEnd + borderSize )
		
		AdjustActiveImages()
		
		Local newPanel:TGadget = CreatePanel( 0, 0, GadgetWidth( client ), GadgetHeight( client ), client )
		SetGadgetLayout( newPanel, 1, 1, 1, 1 )
		managedPanels.AddLast( newPanel )
		Return newPanel
		
	End Method 
	'-----------------------------------------------------------------------------------------------------------------
	'-----------------------------------------------------------------------------------------------------------------	
	Method AdjustActiveImages()
		Local tab:Int = 0
		Local c:Int = 0
		Local lasttab:Int = tabButtons.Count() - 1
		For Local p:TGadget = EachIn leftColumnPanels
			Select c
			Case 0
				If tab = 0
					If currentTab = tab
						SetPanelPixmap( p, stImages[STIC_COL_TOP_ACTIVE] )
					Else 
						SetPanelPixmap( p, stImages[STIC_COL_TOP_INACTIVE] )
					End If 
				End If 
			Case 1
				If currentTab = tab
					SetPanelPixmap( p, stImages[STIC_COL_LEFT_ACTIVE] )
				Else 
					SetPanelPixmap( p, stImages[STIC_COL_LEFT_INACTIVE] )
				End If 
			Case 2
				If currentTab = tab
					If ( tab = lasttab )
						SetPanelPixmap( p, stImages[STIC_COL_BTM_ACTIVE] )
					Else 
						SetPanelPixmap( p, stImages[STIC_COL_MID_BTM_ACTIVE] )
					End If 
				Else If currentTab = tab + 1
					SetPanelPixmap( p, stImages[STIC_COL_MID_TOP_ACTIVE] )
				Else 
					If ( tab < lasttab )
						SetPanelPixmap( p, stImages[STIC_COL_MID_INACTIVE] )
					Else 
						SetPanelPixmap( p, stImages[STIC_COL_BTM_INACTIVE] )
					End If 
				End If 	
			End Select 
			
			c :+ 1
			If ( c = 3 ) 
				c = 1
				tab :+ 1
			End If 
			
		Next 	
		Local i:Int = 0
		For Local p:TGadget = EachIn tabButtons
			If ( currentTab = i )
				SetGadgetPixmap( p, sideTabPixmapPair(tabPixmaps.ValueAtIndex( i )).activePixmap )
			Else 
				SetGadgetPixmap( p, sideTabPixmapPair(tabPixmaps.ValueAtIndex( i )).inactivePixmap )
			End If 
			i :+ 1
		Next 
	End Method 
	'-----------------------------------------------------------------------------------------------------------------
	'-----------------------------------------------------------------------------------------------------------------	
	Method ActivateTab( tabID:Int )
		Assert( managedPanels <> Null )
		Assert( tabID < managedPanels.Count() )
		For Local p:TGadget = EachIn managedPanels
			If ( Not GadgetHidden( p ) ) Then HideGadget( p )
		Next 
		Local p:TGadget = TGadget( managedPanels.ValueAtIndex( tabID ) )
		ShowGadget( p )					
		currentTab = tabID
		AdjustActiveImages()
		Local e:TEvent = New TEvent
		e.id = EVENT_GADGETACTION
		e.source = Self 
		e.data = tabID		
		PostEvent( e )
	End Method 
	'-----------------------------------------------------------------------------------------------------------------
	'-----------------------------------------------------------------------------------------------------------------	
	Method ActiveTab:Int( )
		Return currentTab	' the current active tab
	End Method 
	'-----------------------------------------------------------------------------------------------------------------
	'-----------------------------------------------------------------------------------------------------------------	
	Method ClientArea:TGadget()
		Return client		' main client area
	End Method
	'-----------------------------------------------------------------------------------------------------------------
	'-----------------------------------------------------------------------------------------------------------------	
	Method RetrieveManagedPanel:TGadget( tabID:Int )
		Assert( managedPanels <> Null )
		Assert( tabID < managedPanels.Count() )
		Return TGadget( managedPanels.ValueAtIndex( tabID ) ) ' the managed panel for this tab
	End Method 
	'-----------------------------------------------------------------------------------------------------------------
	
End Type 
'----------------------------------------------------------------------------------------------------------------------
'----------------------------------------------------------------------------------------------------------------------
		
'Rem
'----------------------------------------------------------------------------------------------------------------------
' DEMO
'----------------------------------------------------------------------------------------------------------------------
Global window:TGadget = CreateWindow( "Side tab test", 400, 200, 600, 600, Null, ..
								WINDOW_TITLEBAR | WINDOW_RESIZABLE | WINDOW_CLIENTCOORDS )
SetMinWindowSize( window, 600, 600 )
						
' The tabber can use unique pixmaps for each tab, and each state (active or inactive)
' Here we just apply the same pixmaps to each tab.
' These blank pixmaps should be edited to create your own unique tab descriptors.  They
' can be any size in Y and not limited to the size provided
Local activePixmap:TPixmap = LoadPixmap( "incbin//sidetab_label_active.png" )
Local inactivePixmap:TPixmap = LoadPixmap( "incbin//sidetab_label_inactive.png" )

Global tabPanel:TGadget
Global lbl:Tgadget

' create a panel to house the first tabber
Global p1:TGadget = CreatePanel( 0, 0, 208, 560, window )
SetGadgetLayout( p1, 1, 1, 1, 1 )
Global st1:sideTab = sideTab.Create( p1, 4, 4, 200, 550, 255, 255, 255 )

' Create 5 tabs..
For Local t:Int = 1 To 5
tabPanel = st1.AddTab( activePixmap , inactivePixmap  )
lbl=CreateLabel( "Tab " + t, 0, 0, 40, 20, tabPanel )
SetGadgetLayout( lbl, 1,0,1,0 )
Next 

' create a panel to house the 2nd tabber
Global p2:TGadget = CreatePanel( 240, 0, 208, 400, window )
SetGadgetLayout( p2, 0, 1, 1, 1 )
Global st2:sideTab = sideTab.Create( p2, 4, 4, 200, 390, 255, 255, 255 )

' Create 3 tabs..
For Local t:Int = 1 To 3
tabPanel = st2.AddTab( activePixmap , inactivePixmap  )
lbl=CreateLabel( "Tab " + t, 0, 0, 40, 20, tabPanel )
SetGadgetLayout( lbl, 1,0,1,0 )
Next 

Repeat
	WaitEvent()
	Select EventID()
		Case EVENT_GADGETACTION
			Select EventSource() 
			Case st1
				Print "side tabber 1 changed to tab " + st1.ActiveTab()
			Case st2
				Print "side tabber 2 changed to tab " + st2.ActiveTab()
			End Select 
		Case EVENT_WINDOWCLOSE
			End
	EndSelect
Forever
'End Rem
