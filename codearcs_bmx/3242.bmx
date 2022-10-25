; ID: 3242
; Author: Bobysait
; Date: 2016-01-12 06:31:41
; Title: Icon Tabber for Maxgui
; Description: Nice minimalist looking tabber with icons instead of text

SuperStrict

Import maxgui.drivers

Const TABBER_VERTICAL:Int = 1
Const TABBER_RIGHT:Int = 2
Const TABBER_BOTTOM:Int = 4

Const STYLE_FITW:Int = 1
Const STYLE_FITH:Int = 2
Const STYLE_FIT:Int	=	STYLE_FITW | STYLE_FITH

Const STYLE_LAYOUTL:Int = 4
Const STYLE_LAYOUTR:Int = 8
Const STYLE_LAYOUTT:Int = 16
Const STYLE_LAYOUTB:Int = 32
Const STYLE_LAYOUTH:Int	= STYLE_LAYOUTL | STYLE_LAYOUTR
Const STYLE_LAYOUTV:Int	= STYLE_LAYOUTT | STYLE_LAYOUTB
Const STYLE_LAYOUT:Int	= STYLE_LAYOUTH | STYLE_LAYOUTV


Type TIconTabber Extends TGadget
	
	Const DEFAULT_BORDER_SIZE:Int = 2
	Const DEFAULT_TAB_HEIGHT:Int = 24
	Const DEFAULT_CURSOR_HEIGHT:Int = 2
	
	Global DBCLICK_DELAY:Int = 350
	' tabber root (panel -> contains buttons bar and body)
	Field core:TGadget
	' button bar (panel)
	Field tool:TGadget
	' a panel that fit the client area
	Field body:TGadget
	' index of the selected tab
	Field selectedIndex:Int
	
	Field pixmaps:TPixmap[]
	' the buttons to select tab (they are actually simple panels)
	Field buttons:TGadget[]
	' panels attached to the body corresponding to the tab "at index"
	Field panels:TGadget[]
	' a simple panel that marks the current selected tab.
	Field cursorgadget:TGadget
	
	' some parameters that define the global aspect of the tabber
	Field border:Int
	Field buttonsize:Int
	Field cursorsize:Int
	' background and foreground colors
	Field rBG:Int, gBG:Int, bBG:Int
	Field rFG:Int, gFG:Int, bFG:Int
	
	Field _clientX:Int, _clientY:Int
	
	' set the click delay between two left-clicks on the tab that will produice an avent_gadgetaction with mods=1
	Function SetDoubleClickDelay(pDelay:Int)
		DBCLICK_DELAY = pDelay
	End Function
	
	Function GetDoubleClickDelay:Int()
		Return DBCLICK_DELAY
	End Function
	
	Function Create:TIconTabber(x:Int, y:Int, w:Int, h:Int, parent:TGadget, Style:Int=0)
		Local ct:TIconTabber = New TIconTabber
			ct.selectedIndex=	-1
			ct.border		=	DEFAULT_BORDER_SIZE
			ct.cursorsize	=	DEFAULT_CURSOR_HEIGHT
			ct.buttonsize	=	DEFAULT_TAB_HEIGHT
			ct.Style		=	Style
			
			ct.buttons		=	New TGadget[0]
			ct.panels		=	New TGadget[0]
			ct.core			=	CreatePanel			( x,y,w,h, parent )
			ct.tool			=	CreatePanel			( 0, 0, 1, 1, ct.core )
			ct.body			=	CreatePanel			( 0, 0, 1, 1, ct.core )
			ct._updatecore()
			
			ct					.SetColor			( 000,175,170 )
			ct					.SetTextColor		( 001,001,001 )
			ct.core				.SetText			( "tabber_core" )
			ct.tool				.SetText			( "tabber_tool" )
			ct.cursorgadget	=	CreatePanel			( 0,0, ct.buttonsize, ct.cursorsize, ct.tool )
			ct.cursorgadget		.SetColor			( 255,255,000 )
								HideGadget			( ct.cursorgadget )
			AddHook				( EmitEventHook, EventHook, ct )
		Return ct
	End Function
	
	
	Method Cleanup()
		RemoveHook(EmitEventHook, EventHook, Null)
		Super.CleanUp()
	End Method
	
	Method _updatecore()
		
		' height of tool (or width for TABBER_VERTICAL)
		Local size1:Int	=	Self.cursorsize+Self.buttonsize
		Local b:Int = Self.border
		Local w:Int = Self.core.ClientWidth()
		Local h:Int = Self.core.ClientHeight()
		Local size2:Int
		
		If (Self.Style & TABBER_VERTICAL)
			size2 = h-b*2
			If Style & TABBER_RIGHT
				' Vertical Right
				Self.tool	.SetShape	( w - b - size1, b, size1, size2 )
				Self.tool	.SetLayout	( 0, EDGE_ALIGNED, EDGE_ALIGNED, EDGE_ALIGNED)
				Self.body	.SetShape	( b, b, w-b*2-size1-1, size2 )
			Else
				' Vertical Left
				Self.tool	.SetShape	( b, b, size1, size2 )
				Self.tool	.SetLayout	( EDGE_ALIGNED, 0, EDGE_ALIGNED, EDGE_ALIGNED)
				Self.body	.SetShape	( b + 1 + size1, b, w - b*2-size1-1, size2 )
			EndIf
		Else
			size2 = w-b*2
			If (Self.Style & TABBER_BOTTOM)
				' Horizontal Bottom
				Self.body	.SetShape	( b, b, size2, h - b*2 - size1-1 )
				Self.tool	.SetShape	( b, h-b-size1, size2, size1 )
				Self.tool	.SetLayout	( EDGE_ALIGNED, EDGE_ALIGNED, 0, EDGE_ALIGNED)
			Else
				' default : Horizontal Top
				Self.tool	.SetShape	( b, b, size2, size1 )
				Self.tool	.SetLayout	( EDGE_ALIGNED, EDGE_ALIGNED, EDGE_ALIGNED, 0)
				Self.body	.SetShape	( b, b+size1+1, size2, h - b*2 - size1-1 )
			EndIf
		EndIf
		
		' always stick the body to the core.client.area
		Self.body			.SetLayout	( EDGE_ALIGNED, EDGE_ALIGNED, EDGE_ALIGNED, EDGE_ALIGNED)
		
	End Method
	
	Method GetPanel:TGadget(index:Int)
		?Debug
		If index<0 Or index>=Self.panels.length Throw "Gadget item index out of range."
		?
		Return Self.panels[index]
	End Method
	
	'#region aspect
	Method setCursorColor(r:Int, g:Int, b:Int)
		Self.cursorgadget.SetColor(r,g,b)
	End Method
	
	Method setButtonSize(Size:Int)
		Self.buttonsize = Size
		Self._updatecore()
		Self._updateitems()
	End Method
	
	Method setCursorSize(Size:Int)
		Self.cursorsize = Size
		Self._updatecore()
		Self._updateitems()
	End Method

	Method SetIconStrip(pStrip:TIconStrip)
		?Debug
		If pStrip=Null Throw "Icon Strip is Null."
		?
		' icon strip pixmaps
		Self.pixmaps = New TPixmap[pStrip.Count]
		For Local i:Int = 0 Until Self.pixmaps.length
			Self.pixmaps[i] = pStrip.ExtractIconPixmap(i)
		Next
		' update the panels with the new fresh Icons
		Self._updateitems(True)
	EndMethod
	
	' main color goes to body and tool
	Method SetColor:Int(r:Int,g:Int,b:Int)
		Self.rBG=r; Self.gBG=g; Self.bBG=b
		For Local i:Int = 0 Until Self.buttons.length
			Self.buttons[i]	.SetColor	( Self.rBG,Self.gBG,Self.bBG)
			Self.panels[i]	.SetColor	( Self.rBG,Self.gBG,Self.bBG)
		Next
		Return Self.tool	.SetColor	( Self.rBG,Self.gBG,Self.bBG)
	End Method
	
	' convert text color to background color
	Method SetTextColor:Int(r:Int,g:Int,b:Int)
		Self.rFG=r; Self.gFG=g; Self.bFG=b
		For Local i:Int = 0 Until Self.buttons.length
			Self.buttons[i]	.SetTextColor( Self.rFG, Self.gFG, Self.bFG )
			Self.panels[i]	.SetTextColor( Self.rFG, Self.gFG, Self.bFG )
		Next
		Return Self.core	.SetColor	( Self.rFG, Self.gFG, Self.bFG )
	End Method
	
	' gadget layout defines the core layout
	Method SetLayout( lft:Int,rht:Int,top:Int,bot:Int )
		Self.core.SetLayout(lft,rht,top,bot)
	End Method
	
	Method ClientWidth:Int()
		If (Self.Style & TABBER_VERTICAL) Then Return Self.width-Self.border*2-Self.cursorsize-Self.buttonsize-1
		Return Self.width-Self.border*2
	EndMethod
	
	Method ClientHeight:Int()
		If (Self.Style & TABBER_VERTICAL) Then Return Self.Height-Self.border*2
		Return Self.Height-Self.border*2-Self.buttonsize-Self.cursorsize-1
	EndMethod
	Method ProcessEvent:Int(event:TEvent, tabGadget:TGadget)
		
		Global _lastclick:Int
		
		For Local index:Int = 0 Until Self.items.length
			
			If Self.buttons[index] = tabGadget
				
				Select event.id
					
					Case EVENT_MOUSEDOWN
						
						' double-click (left mouse button)
						If self.selectedIndex = index
							If _lastclick>MilliSecs()
								EmitEvent(CreateEvent(EVENT_GADGETACTION, Self, index, 1, event.x,event.y, Self.items[index].extra))
								_lastclick = MilliSecs()+DBCLICK_DELAY
							EndIf
						EndIf
						_lastclick = MilliSecs()+DBCLICK_DELAY
						
					Case EVENT_MOUSEUP
						If event.data = 1 ' left-click
							If (index<>self.selectedIndex)
								Self.SelectItem(index, 1)
								EmitEvent(CreateEvent(EVENT_GADGETACTION, Self, index, 0, event.x,event.y, Self.items[index].extra))
							EndIf
						ElseIf event.data = 2 ' right-click
							EmitEvent(CreateEvent(EVENT_GADGETMENU, Self, index, 0, event.x,event.y, Self.items[index].extra))
						End If
						
				End Select
				
				Return 1
				
			EndIf
			
		Next
		
		Return 0
	End Method
	
	Function EventHook:Object(id:Int,data:Object,context:Object)
		
		If (TEvent(data)<>Null)
			If (TGadget(context)<>Null)
				Local ev:TEvent = TEvent(data)
				Local src:TGadget = TGadget(ev.source)
				If (src<>Null)
					If (TIconTabber(src.extra)<>Null)
						If (TIconTabber(src.extra).ProcessEvent(ev, src)>0) Then Return Null
					EndIf
				EndIf
			EndIf
		EndIf
		
		Return data
		
	EndFunction
	Method _createitem(i:Int)
		Self.buttons[i]	=	CreatePanel		( 1+(Self.buttonsize+1)*i, 0, Self.buttonsize,Self.buttonsize, Self.tool, PANEL_ACTIVE )
		Self.buttons[i]		.SetLayout		( EDGE_ALIGNED, 0, EDGE_ALIGNED, EDGE_ALIGNED )
		Self.buttons[i]		.extra		=	Self
		Self.buttons[i]		.SetColor		( Self.rBG, Self.gBG, Self.bBG )
		Self.buttons[i]		.SetTextColor	( Self.rFG, Self.gFG, Self.bFG )
		Self.buttons[i]		.SetText		( "item "+i )
		If Self.pixmaps<>Null
			Self.buttons[i]	.SetPixmap		( Self.pixmaps[max(Self.items[i].Icon,0)Mod(Self.pixmaps.length)], PANELPIXMAP_STRETCH )
		EndIf
		Self.panels[i]=	CreatePanel		( 0,0,Self.body.ClientWidth(),Self.body.ClientHeight(), Self.body )
		Self.panels[i]	.SetLayout		( EDGE_ALIGNED, EDGE_ALIGNED, EDGE_ALIGNED, EDGE_ALIGNED )
		Self.panels[i]	.extra		=	Self
		Self.panels[i]	.SetColor		( Self.rBG, Self.gBG, Self.bBG )
		Self.panels[i]	.SetTextColor	( Self.rFG, Self.gFG, Self.bFG )
		Self.panels[i]	.SetText		( "panel "+i )
		If (self.selectedIndex = i)
			ShowGadget(Self.panels[i])
		Else
			HideGadget(Self.panels[i])
		EndIf
		_updateitemshape(i)
	End Method


	Method _updateitemshape(i:Int)
		
		Local sizeA:Int = 1 + i*(Self.buttonsize+1)
		Local sizeB:Int = 0
		If Self.Style & TABBER_VERTICAL
			If Self.Style & TABBER_BOTTOM
				sizeA = (Self.tool.ClientHeight() - Self.items.length * (Self.buttonsize+1) - 1) + sizeA
			EndIf
			If (Self.Style & TABBER_RIGHT)
				sizeB = Self.cursorsize
			EndIf
		Else
			If Self.Style & TABBER_RIGHT
				sizeA = (Self.tool.ClientWidth() - Self.items.length * (Self.buttonsize+1) - 1)  + sizeA
			EndIf
			If Self.Style & TABBER_BOTTOM
				sizeB = Self.cursorsize
			EndIf
		EndIf
		
		Local g:TGadget = Self.buttons[i]
		If (Self.Style & TABBER_VERTICAL)
			g.SetShape( sizeB, sizeA, Self.buttonsize,Self.buttonsize )
			If (Self.Style & TABBER_BOTTOM)
				g.SetLayout	( EDGE_ALIGNED, EDGE_ALIGNED, 0, EDGE_ALIGNED )
			Else
				g.SetLayout	( EDGE_ALIGNED, EDGE_ALIGNED, EDGE_ALIGNED, 0 )
			EndIf
		Else
			g.SetShape( sizeA, sizeB, Self.buttonsize,Self.buttonsize )
			If (Self.Style & TABBER_RIGHT)
				g.SetLayout	( 0, EDGE_ALIGNED, EDGE_ALIGNED, EDGE_ALIGNED )
			Else
				g.SetLayout	( EDGE_ALIGNED, 0, EDGE_ALIGNED, EDGE_ALIGNED )
			EndIf
		EndIf
		
	End Method


	Method _updateitems(force_reloadIcons:Byte = False, pUpdateCursorOnly:Byte=False, pUpdateIcon:Int=-1)
		
		Local sizeA:Int = 1 + Self.selectedIndex*(Self.buttonsize+1)
		Local sizeB:Int = 0
		If Self.Style & TABBER_VERTICAL
			If Self.Style & TABBER_BOTTOM
				sizeA = (Self.tool.ClientHeight() - Self.items.length * (Self.buttonsize+1) - 1) + sizeA
			EndIf
			If Not(Self.Style & TABBER_RIGHT)
				sizeB = Self.buttonsize
			EndIf
		Else
			If Self.Style & TABBER_RIGHT
				sizeA = (Self.tool.ClientWidth() - Self.items.length * (Self.buttonsize+1) - 1)  + sizeA
			EndIf
			If Not(Self.Style & TABBER_BOTTOM)
				sizeB = Self.buttonsize
			EndIf
		EndIf
		
		If Self.items.length>0 And self.selectedIndex>=0
			ShowGadget				( Self.cursorgadget )
			If (Self.Style & TABBER_VERTICAL)
				Self.cursorgadget.SetShape		( sizeB, sizeA, Self.cursorsize, Self.buttonsize )
				If (Self.Style & TABBER_BOTTOM)
					Self.cursorgadget.SetLayout	( EDGE_ALIGNED, EDGE_ALIGNED, 0, EDGE_ALIGNED )
				Else
					Self.cursorgadget.SetLayout	( EDGE_ALIGNED, EDGE_ALIGNED, EDGE_ALIGNED, 0 )
				EndIf
			Else
				Self.cursorgadget.SetShape		( sizeA, sizeB, Self.buttonsize, Self.cursorsize )
				If (Self.Style & TABBER_RIGHT)
					Self.cursorgadget.SetLayout	( 0, EDGE_ALIGNED, EDGE_ALIGNED, EDGE_ALIGNED )
				Else
					Self.cursorgadget.SetLayout	( EDGE_ALIGNED, 0, EDGE_ALIGNED, EDGE_ALIGNED )
				EndIf
			EndIf
			For Local i:Int = 0 Until Self.panels.length
				HideGadget			( Self.panels[i])
			Next
			ShowGadget				( Self.panels[self.selectedIndex])
		Else
			HideGadget				( Self.cursorgadget )
			For Local i:Int = 0 Until Self.panels.length
				HideGadget			( Self.panels[i])
			Next
		EndIf
		
		If (pUpdateCursorOnly And (Self.pixmaps=Null)) Then Return
		
		For Local i:Int = 0 Until Self.items.length
			If Self.items[i]<>Null
				Local g:TGadget = Self.buttons[i]
				
				' update shapes/colors if required
				If Not(pUpdateCursorOnly)
					_updateitemshape(i)
					ShowGadget (g)
					g				.SetColor		( Self.rBG, Self.gBG, Self.bBG )
					g				.SetTextColor	( Self.rFG, Self.gFG, Self.bFG )
					g				.SetTooltip		( Self.items[i].tip )
				EndIf
				
				' update toggle Icons (if any)
				If Self.pixmaps<>Null
					If ((Self.items[i].flags & GADGETITEM_TOGGLE)>0)
						g	.SetPixmap( Self.pixmaps[ max( Self.items[i].Icon+(Self.selectedIndex=i), 0 ) Mod(Self.pixmaps.length) ], PANELPIXMAP_STRETCH )
					ElseIf force_reloadIcons Or (i = pUpdateIcon)
						If ((Self.items[i].flags & GADGETITEM_TOGGLE)>0)
							g	.SetPixmap( Self.pixmaps[ max( Self.items[i].Icon+(Self.selectedIndex=i), 0 ) Mod( Self.pixmaps.length) ], PANELPIXMAP_STRETCH )
						Else
							g	.SetPixmap( Self.pixmaps[ max( Self.items[i].Icon, 0 ) Mod(Self.pixmaps.length) ], PANELPIXMAP_STRETCH )
						EndIf
					EndIf
				EndIf
			EndIf
		Next
		
	End Method


	Method ClearListItems()
		self.selectedIndex=-1
		For Local i:Int = 0 Until Self.buttons.length
			FreeGadget(Self.panels[i])
			FreeGadget(Self.buttons[i])
		Next
		Self.buttons = New TGadget[0]
		Self.panels = New TGadget[0]
		_updateitems(False, True) ' hide the cursor
	EndMethod
	
	Method SelectItem(index:Int,op:Int=1)	' op not supported !(it's here just to override the method)
		?Debug
			If index<0 Or index>=items.length Throw "Gadget item index out of range."
		?
		self.selectedIndex = index
		Self._updateitems(False, True)	' Update the cursor Position update Icons
	End Method
	
	Method InsertListItem:Int(index:Int,text:String,tip:String,Icon:Int,extra:Object)
		?Debug
		If index<0 Or index>=Self.items.length Throw "Gadget item index out of range."
		?
		If index=0
			Self.buttons = [TGadget(Null)]+Self.buttons[..Self.buttons.length]
			Self.panels = [TGadget(Null)]+Self.panels[..Self.panels.length]
		ElseIf index=Self.buttons.length
			Self.buttons = Self.buttons[..index+1]
			Self.panels = Self.panels[..index+1]
		Else
			Self.buttons = Self.buttons[..index]+[TGadget(Null)]+Self.buttons[index..]
			Self.panels = Self.panels[..index]+[TGadget(Null)]+Self.panels[index..]
		EndIf
		_createitem(index)
		If self.selectedIndex = -1 Then self.selectedIndex = index
		
		Print "new item ["+index+"] : "+Self.items[index].text+" flag="+Self.items[index].flags+" Icon="+Self.items[index].Icon
		
		_updateitems(False, False) ' update tab positions
		
		Return 1
	End Method

	Method SetItem(index:Int,text:String,tip:String,Icon:Int,extra:Object,flags:Int)
		?Debug
			If index<0 Or index>=Self.items.length Throw "Gadget item index out of range."
		?
		Local prevIcon:Int = Self.items[index].Icon ' previous Icon
		Super.SetItem(index, text, tip, Icon, extra, flags)
		_updateitems(False, True, (prevIcon<>Icon)*index) ' update Icon if modified
	End Method
	
	Method RemoveListItem:Int(index:Int)
		?Debug
		If index<0 Or index>=Self.items.length Throw "Gadget item index out of range."
		?
		FreeGadget(Self.panels[index])
		FreeGadget(Self.buttons[index])
		If index>0
			If index<Self.buttons.length
				Self.buttons = Self.buttons[..index]+Self.buttons[index+1..]
				Self.panels = Self.panels[..index]+Self.panels[index+1..]
			Else
				Self.buttons = Self.buttons[..index]
				Self.panels = Self.panels[..index]
			EndIf
		Else
			Self.buttons = Self.buttons[1..]
			Self.panels = Self.panels[1..]
		EndIf
		If (index <= self.selectedIndex)
			self.selectedIndex :- 1
			If ((self.selectedIndex=-1) And (Self.items.length>0))
				self.selectedIndex = 0
			EndIf
		EndIf
		_updateitems(False, True) ' hide cursor
		Return 0
	End Method
	
	Method ListItemState:Int(index:Int)
		?Debug
		If index<0 Or index>=Self.items.length Throw "Gadget item index out of range."
		?
		Return (index=self.selectedIndex) * STATE_SELECTED
	End Method
	
	Method SetItemState(index:Int,State:Int)
		?Debug
		If index<0 Or index>=Self.items.length Throw "Gadget item index out of range."
		?
		If State Then Self.SelectItem(index)
		
	End Method

	Method FitGadget ( gadget:TGadget, index:Int, x:Int, y:Int, Style:Int = 0 )
		?Debug
		If index<0 Or index>=Self.items.length Throw "Gadget item index out of range."
		?
		Local lParent:TGadget = Self.GetPanel(index)
		If lParent=Null Then Return
		
		Local w:Int = gadget.GetWidth()
		Local h:Int = gadget.GetHeight()
		If Style & STYLE_FITW Then w = lParent.ClientWidth() - x
		If Style & STYLE_FITH Then h = lParent.ClientHeight() - y
		
		If ( ((Style & STYLE_FITW)>0) Or ((Style & STYLE_FITH)>0) ) Then gadget.SetShape(x,y,w,h)
		
		If (Style & STYLE_FIT)
			gadget.SetLayout(	( (Style & STYLE_LAYOUTL)>0 ) * EDGE_ALIGNED, ..
								( (Style & STYLE_LAYOUTR)>0 ) * EDGE_ALIGNED, ..
								( (Style & STYLE_LAYOUTT)>0 ) * EDGE_ALIGNED, ..
								( (Style & STYLE_LAYOUTB)>0 ) * EDGE_ALIGNED )
		EndIf
	End Method
	
	Method getChild:TGadget(index:Int, childIndex:Int=0)
		?Debug
		If index<0 Or index>=Self.items.length Throw "Gadget item index out of range."
		?
		Local l:TList = Self.panels[index].kids
		If childIndex=0 Then Return TGadget(l.First())
		Return TGadget(l.FindLink(l.ValueAtIndex(childIndex)).Value())
	End Method
	
	Method countPanelChildren:Int(index:Int)
		?Debug
		If index<0 Or index>=Self.items.length Throw "Gadget item index out of range."
		?
		Return Self.GetPanel(index).kids.Count()
	End Method

End Type

Function GetTabberPanel:TGadget(tabber:TGadget, index:Int)
	Return TIconTabber(tabber).GetPanel(index)
End Function

rem
bbdoc: Create a Colored Tabber
about: each tab is an Icon without text
The body and toolbar can be "colored"
endrem
Function CreateIconTabber:TIconTabber ( x:Int, y:Int, w:Int, h:Int, group:TGadget, Style:Int=0 )
	Return TIconTabber.Create(x,y,w,h, group, Style)
End Function
