; ID: 3246
; Author: marksibly_v2
; Date: 2016-01-14 15:17:47
; Title: test
; Description: test

--------------- Magic text begins --------------
SuperStrict

Import maxgui.drivers

Const TABBER_VERTICAL:Int = 1;
Const TABBER_RIGHT:Int = 2;
Const TABBER_BOTTOM:Int = 4;

Const STYLE_FITW:Int = 1;
Const STYLE_FITH:Int = 2;
Const STYLE_FIT:Int = STYLE_FITW | STYLE_FITH;

Const STYLE_LAYOUTL:Int = 4;
Const STYLE_LAYOUTR:Int = 8;
Const STYLE_LAYOUTT:Int = 16;
Const STYLE_LAYOUTB:Int = 32;
Const STYLE_LAYOUTH:Int = STYLE_LAYOUTL | STYLE_LAYOUTR;
Const STYLE_LAYOUTV:Int = STYLE_LAYOUTT | STYLE_LAYOUTB;
Const STYLE_LAYOUT:Int = STYLE_LAYOUTH | STYLE_LAYOUTV;

Type TIconTabber Extends TGadget
Const DEFAULT_BORDER_SIZE:Int = 2;
Const DEFAULT_TAB_HEIGHT:Int = 24;
Const DEFAULT_CURSOR_HEIGHT:Int = 2;
Global DBCLICK_DELAY:Int = 350;
Field core:TGadget; ' tabber root (panel -> contains buttons bar and body)
Field tool:TGadget; ' button bar (panel)
Field body:TGadget; ' a panel that fit the client area
Field _selected:Int; ' index of the selected tab
Field _icons:TPixmap[]; ' extracted pixmaps from a TIconStrip
Field _gitems:TGadget[]; ' the buttons to select tab (they are actually simple panels)
Field _gpanels:TGadget[]; ' panels attached to the body corresponding to the tab "at index"
Field _cursor:TGadget; ' a simple panel that marks the current selected tab.
Field _border:Int; ' some parameters that define the global aspect of the tabber
Field _tabheight:Int;
Field _cursorheight:Int;
Field rBG:Int, gBG:Int, bBG:Int; ' background and foreground colors
Field rFG:Int, gFG:Int, bFG:Int;
Field _clientX:Int, _clientY:Int;
' set the click delay between two left-clicks on the tab that will produice an avent_gadgetaction with mods=1
Function SetDoubleClickDelay(pDelay:Int)
DBCLICK_DELAY = pDelay;
End Function
Function GetDoubleClickDelay:Int()
Return DBCLICK_DELAY;
End Function
Function Create:TIconTabber(x:Int, y:Int, w:Int, h:Int, parent:TGadget, Style:Int=0)
Local ct:TIconTabber = New TIconTabber;
ct._selected = -1;
ct._border = DEFAULT_BORDER_SIZE;
ct._cursorheight= DEFAULT_CURSOR_HEIGHT;
ct._tabheight = DEFAULT_TAB_HEIGHT;
ct.Style = Style;
ct._gitems = New TGadget[0];
ct._gpanels = New TGadget[0];
ct.core = CreatePanel ( x,y,w,h, parent );
ct.tool = CreatePanel ( 0, 0, 1, 1, ct.core );
ct.body = CreatePanel ( 0, 0, 1, 1, ct.core );
ct._updatecore();
ct .SetColor ( 000,175,170 );
ct .SetTextColor ( 001,001,001 );
ct.core .SetText ( "tabber_core" );
ct.tool .SetText ( "tabber_tool" );
ct._cursor = CreatePanel ( 0,0, ct._tabheight, ct._cursorheight, ct.tool );
ct._cursor .SetColor ( 255,255,000 );
HideGadget ( ct._cursor );
AddHook ( EmitEventHook, EventHook, ct );
Return ct;
End Function
Method Cleanup()
RemoveHook(EmitEventHook, EventHook, Null);
Super.CleanUp();
End Method
Method _updatecore()
' height of tool (or width for TABBER_VERTICAL)
Local size1:Int = Self._cursorheight+Self._tabheight;
Local b:Int = Self._border;
Local w:Int = Self.core.ClientWidth();
Local h:Int = Self.core.ClientHeight();
Local size2:Int;
If (Self.Style & TABBER_VERTICAL)
size2 = h-b*2;
If Style & TABBER_RIGHT
' Vertical Right
Self.tool .SetShape ( w - b - size1, b, size1, size2 );
Self.tool .SetLayout ( 0, EDGE_ALIGNED, EDGE_ALIGNED, EDGE_ALIGNED);
Self.body .SetShape ( b, b, w-b*2-size1-1, size2 );
Else
' Vertical Left
Self.tool .SetShape ( b, b, size1, size2 );
Self.tool .SetLayout ( EDGE_ALIGNED, 0, EDGE_ALIGNED, EDGE_ALIGNED);
Self.body .SetShape ( b + 1 + size1, b, w - b*2-size1-1, size2 );
EndIf;
Else
size2 = w-b*2;
If (Self.Style & TABBER_BOTTOM)
' Horizontal Bottom
Self.body .SetShape ( b, b, size2, h - b*2 - size1-1 );
Self.tool .SetShape ( b, h-b-size1, size2, size1 );
Self.tool .SetLayout ( EDGE_ALIGNED, EDGE_ALIGNED, 0, EDGE_ALIGNED);
Else
' default : Horizontal Top
Self.tool .SetShape ( b, b, size2, size1 );
Self.tool .SetLayout ( EDGE_ALIGNED, EDGE_ALIGNED, EDGE_ALIGNED, 0);
Self.body .SetShape ( b, b+size1+1, size2, h - b*2 - size1-1 );
EndIf;
EndIf;
' always stick the body to the core.client.area
Self.body .SetLayout ( EDGE_ALIGNED, EDGE_ALIGNED, EDGE_ALIGNED, EDGE_ALIGNED);
End Method
Method GetPanel:TGadget(index:Int)
?Debug
If index<0 Or index>=Self._gPanels.length Throw "Gadget item index out of range."
?
Return Self._gPanels[index];
End Method
'#region aspect
Method setCursorColor(r:Int, g:Int, b:Int)
Self._cursor.SetColor(r,g,b);
End Method
Method setTabSize(Size:Int)
Self._tabheight = Size;
Self._updatecore();
Self._updateitems();
End Method
Method setCursorSize(Size:Int)
Self._cursorHeight = Size;
Self._updatecore();
Self._updateitems();
End Method
Method SetIconStrip(iconstrip:TIconStrip)
' extract the pixmaps from the iconstrip
Self._icons = New TPixmap[iconstrip.Count];
For Local i:Int = 0 Until Self._icons.length
Self._icons[i] = iconstrip.ExtractIconPixmap(i);
Next;
' update the panels with the new fresh icons
Self._updateitems(True);
EndMethod
' main color goes to body and tool
Method SetColor:Int(r:Int,g:Int,b:Int)
Self.rBG=r; Self.gBG=g; Self.bBG=b;
For Local i:Int = 0 Until Self._gitems.length
Self._gitems[i] .SetColor ( Self.rBG,Self.gBG,Self.bBG);
Self._gpanels[i].SetColor ( Self.rBG,Self.gBG,Self.bBG);
Next;
Return Self.tool .SetColor ( Self.rBG,Self.gBG,Self.bBG);
End Method
' convert text color to background color
Method SetTextColor:Int(r:Int,g:Int,b:Int)
Self.rFG=r; Self.gFG=g; Self.bFG=b;
For Local i:Int = 0 Until Self._gitems.length
Self._gitems[i] .SetTextColor ( Self.rFG, Self.gFG, Self.bFG );
Self._gpanels[i].SetTextColor ( Self.rFG, Self.gFG, Self.bFG );
Next;
Return Self.core .SetColor ( Self.rFG, Self.gFG, Self.bFG );
End Method
' gadget layout defines the core layout
Method SetLayout( lft:Int,rht:Int,top:Int,bot:Int )
Self.core.SetLayout(lft,rht,top,bot);
End Method
Method ClientWidth:Int()
If (Self.Style & TABBER_VERTICAL) Then Return Self.width-Self._border*2-Self._cursorheight-Self._tabheight-1;
Return Self.width-Self._border*2;
EndMethod
Method ClientHeight:Int()
If (Self.Style & TABBER_VERTICAL) Then Return Self.Height-Self._border*2;
Return Self.Height-Self._border*2-Self._tabheight-Self._cursorheight-1;
EndMethod
'#End region
'#region logic/update
Method ProcessEvent:Int(event:TEvent, tabGadget:TGadget)
Global _lastclick:Int;
For Local index:Int = 0 Until Self.items.length
If Self._gitems[index] = tabGadget
Select event.id
Case EVENT_MOUSEDOWN
' double-click (left mouse button)
If Self._selected = index
If _lastclick>MilliSecs()
EmitEvent(CreateEvent(EVENT_GADGETACTION, Self, index, 1, event.x,event.y, Self.items[index].extra));
_lastclick = MilliSecs()+DBCLICK_DELAY;
EndIf;
EndIf;
_lastclick = MilliSecs()+DBCLICK_DELAY;
Case EVENT_MOUSEUP
If event.data = 1 ' left-click
If (index<>Self._selected)
Self.SelectItem(index, 1);
EmitEvent(CreateEvent(EVENT_GADGETACTION, Self, index, 0, event.x,event.y, Self.items[index].extra));
EndIf;
ElseIf event.data = 2 ' right-click
EmitEvent(CreateEvent(EVENT_GADGETMENU, Self, index, 0, event.x,event.y, Self.items[index].extra));
End If
End Select;
Return 1;
EndIf;
Next
Return 0;
End Method
Function EventHook:Object(id:Int,data:Object,context:Object)
If (TEvent(data)<>Null)
If (TGadget(context)<>Null)
Local ev:TEvent = TEvent(data);
Local src:TGadget = TGadget(ev.source);
If (src<>Null)
If (TIconTabber(src.extra)<>Null)
If (TIconTabber(src.extra).ProcessEvent(ev, src)>0) Then Return Null;
EndIf;
EndIf;
EndIf;
EndIf;
Return data;
EndFunction
'#end region
'#region items
Method _createitem(i:Int)
Self._gitems[i] = CreatePanel ( 1+(Self._tabheight+1)*i, 0, Self._tabheight,Self._tabheight, Self.tool, PANEL_ACTIVE );
Self._gitems[i] .SetLayout ( EDGE_ALIGNED, 0, EDGE_ALIGNED, EDGE_ALIGNED );
Self._gitems[i] .extra = Self;
Self._gitems[i] .SetColor ( Self.rBG, Self.gBG, Self.bBG );
Self._gitems[i] .SetTextColor ( Self.rFG, Self.gFG, Self.bFG );
Self._gitems[i] .SetText ( "item "+i );
If Self._icons<>Null
Self._gitems[i] .SetPixmap ( Self._icons[max(Self.items[i].icon,0)], PANELPIXMAP_STRETCH );
EndIf;
Self._gpanels[i]= CreatePanel ( 0,0,Self.body.ClientWidth(),Self.body.ClientHeight(), Self.body );
Self._gpanels[i] .SetLayout ( EDGE_ALIGNED, EDGE_ALIGNED, EDGE_ALIGNED, EDGE_ALIGNED );
Self._gpanels[i] .extra = Self;
Self._gpanels[i] .SetColor ( Self.rBG, Self.gBG, Self.bBG );
Self._gpanels[i] .SetTextColor ( Self.rFG, Self.gFG, Self.bFG );
Self._gpanels[i] .SetText ( "panel "+i );
If (Self._selected = i)
ShowGadget(Self._gpanels[i]);
Else
HideGadget(Self._gpanels[i]);
EndIf;
updateitemshape(i);
End Method
Method updateitemshape(i:Int)
Local sizeA:Int = 1 + i*(Self._tabheight+1);
Local sizeB:Int = 0;
If Self.Style & TABBER_VERTICAL
If Self.Style & TABBER_BOTTOM
sizeA = (Self.tool.ClientHeight() - Self.items.length * (Self._tabheight+1) - 1) + sizeA;
EndIf;
If (Self.Style & TABBER_RIGHT)
sizeB = Self._cursorheight;
EndIf;
Else
If Self.Style & TABBER_RIGHT
sizeA = (Self.tool.ClientWidth() - Self.items.length * (Self._tabheight+1) - 1) + sizeA;
EndIf;
If Self.Style & TABBER_BOTTOM
sizeB = Self._cursorheight;
EndIf;
EndIf;
Local g:TGadget = Self._gitems[i];
If (Self.Style & TABBER_VERTICAL)
g.SetShape( sizeB, sizeA, Self._tabheight,Self._tabheight );
If (Self.Style & TABBER_BOTTOM)
g.SetLayout ( EDGE_ALIGNED, EDGE_ALIGNED, 0, EDGE_ALIGNED );
Else
g.SetLayout ( EDGE_ALIGNED, EDGE_ALIGNED, EDGE_ALIGNED, 0 );
EndIf;
Else
g.SetShape( sizeA, sizeB, Self._tabheight,Self._tabheight );
If (Self.Style & TABBER_RIGHT)
g.SetLayout ( 0, EDGE_ALIGNED, EDGE_ALIGNED, EDGE_ALIGNED );
Else
g.SetLayout ( EDGE_ALIGNED, 0, EDGE_ALIGNED, EDGE_ALIGNED );
EndIf;
EndIf;
End Method
Method _updateitems(force_reloadicons:Byte = False, pUpdateCursorOnly:Byte=False, pUpdateIcon:Int=-1)
Local sizeA:Int = 1 + Self._selected*(Self._tabheight+1);
Local sizeB:Int = 0;
If Self.Style & TABBER_VERTICAL
If Self.Style & TABBER_BOTTOM
sizeA = (Self.tool.ClientHeight() - Self.items.length * (Self._tabheight+1) - 1) + sizeA;
EndIf;
If Not(Self.Style & TABBER_RIGHT)
sizeB = Self._tabheight;
EndIf;
Else
If Self.Style & TABBER_RIGHT
sizeA = (Self.tool.ClientWidth() - Self.items.length * (Self._tabheight+1) - 1) + sizeA;
EndIf;
If Not(Self.Style & TABBER_BOTTOM)
sizeB = Self._tabheight;
EndIf;
EndIf;
If Self.items.length>0 And Self._selected>=0
ShowGadget ( Self._cursor );
If (Self.Style & TABBER_VERTICAL)
Self._cursor.SetShape ( sizeB, sizeA, Self._cursorheight, Self._tabheight );
If (Self.Style & TABBER_BOTTOM)
Self._cursor.SetLayout ( EDGE_ALIGNED, EDGE_ALIGNED, 0, EDGE_ALIGNED );
Else
Self._cursor.SetLayout ( EDGE_ALIGNED, EDGE_ALIGNED, EDGE_ALIGNED, 0 );
EndIf;
Else
Self._cursor.SetShape ( sizeA, sizeB, Self._tabheight, Self._cursorheight );
If (Self.Style & TABBER_RIGHT)
Self._cursor.SetLayout ( 0, EDGE_ALIGNED, EDGE_ALIGNED, EDGE_ALIGNED );
Else
Self._cursor.SetLayout ( EDGE_ALIGNED, 0, EDGE_ALIGNED, EDGE_ALIGNED );
EndIf;
EndIf;
For Local i:Int = 0 Until Self._gpanels.length
HideGadget ( Self._gpanels[i]);
Next;
ShowGadget ( Self._gpanels[Self._selected]);
Else
HideGadget ( Self._cursor );
For Local i:Int = 0 Until Self._gpanels.length
HideGadget ( Self._gpanels[i]);
Next;
EndIf
If (pUpdateCursorOnly And (Self._icons=Null)) Then Return;
For Local i:Int = 0 Until Self.items.length
If Self.items[i]<>Null
Local g:TGadget = Self._gitems[i];
' update shapes/colors if required
If Not(pUpdateCursorOnly)
updateitemshape(i);
ShowGadget (g);
g .SetColor ( Self.rBG, Self.gBG, Self.bBG );
g .SetTextColor ( Self.rFG, Self.gFG, Self.bFG );
g .SetTooltip ( Self.items[i].tip );
EndIf;
' update toggle icons (if any)
If Self._icons<>Null
If ((Self.items[i].flags & GADGETITEM_TOGGLE)>0)
g .SetPixmap( Self._icons[ min(max( Self.items[i].icon+(Self._selected=i), 0 ), Self._icons.length-1) ], PANELPIXMAP_STRETCH );
ElseIf force_reloadicons Or (i = pUpdateIcon)
If ((Self.items[i].flags & GADGETITEM_TOGGLE)>0)
g .SetPixmap( Self._icons[ min(max( Self.items[i].icon+(Self._selected=i), 0 ), Self._icons.length-1) ], PANELPIXMAP_STRETCH );
Else
g .SetPixmap( Self._icons[ max( Self.items[i].icon, 0 ) ], PANELPIXMAP_STRETCH );
EndIf;
EndIf;
EndIf;
EndIf;
Next;
End Method
Method ClearListItems()
_selected=-1
For Local i:Int = 0 Until Self._gitems.length
FreeGadget(Self._gpanels[i]);
FreeGadget(Self._gitems[i]);
Next
Self._gitems = New TGadget[0];
Self._gpanels = New TGadget[0];
_updateitems(False, True); ' hide the cursor
EndMethod
Method SelectItem(index:Int,op:Int=1) '0=deselect 1=select 2=toggle
?Debug
If index<0 Or index>=items.length Throw "Gadget item index out of range."
?
_selected = index;
Self._updateitems(False, True); ' Update the cursor Position update icons
End Method
Method InsertListItem:Int(index:Int,text:String,tip:String,icon:Int,extra:Object)
If index=0
Self._gitems = [TGadget(Null)]+Self._gitems[..Self._gitems.length];
Self._gpanels = [TGadget(Null)]+Self._gpanels[..Self._gpanels.length];
ElseIf index=Self._gitems.length
Self._gitems = Self._gitems[..index+1];
Self._gpanels = Self._gpanels[..index+1];
Else
Self._gitems = Self._gitems[..index]+[TGadget(Null)]+Self._gitems[index..];
Self._gpanels = Self._gpanels[..index]+[TGadget(Null)]+Self._gpanels[index..];
EndIf;
_createitem(index);
If _selected = -1 Then _selected = index;
Print "new item ["+index+"] : "+Self.items[index].text+" flag="+Self.items[index].flags+" icon="+Self.items[index].icon
_updateitems(False, False); ' update tab positions
Return 1;
End Method
Method SetItem(index:Int,text:String,tip:String,icon:Int,extra:Object,flags:Int)
?Debug
If index<0 Or index>=Self.items.length Throw "Gadget item index out of range."
?
Local previcon:Int = Self.items[index].icon; ' previous icon
Super.SetItem(index, text, tip, icon, extra, flags);
_updateitems(False, True, (previcon<>icon)*index); ' update icon if modified
End Method
Method RemoveListItem:Int(index:Int)
FreeGadget(Self._gpanels[index]);
FreeGadget(Self._gitems[index]);
If index>0
If index<Self._gitems.length
Self._gitems = Self._gitems[..index]+Self._gitems[index+1..];
Self._gpanels = Self._gpanels[..index]+Self._gpanels[index+1..];
Else
Self._gitems = Self._gitems[..index];
Self._gpanels = Self._gpanels[..index];
EndIf;
Else
Self._gitems = Self._gitems[1..];
Self._gpanels = Self._gpanels[1..];
EndIf;
If (index <= Self._selected)
Self._selected :- 1;
If ((Self._selected=-1) And (Self.items.length>0))
Self._selected = 0;
EndIf;
EndIf;
_updateitems(False, True); ' hide cursor
Return 0;
End Method
Method ListItemState:Int(index:Int)
Return (index=Self._selected) * STATE_SELECTED;
End Method
Method SetItemState(index:Int,State:Int)
?Debug
If index<0 Or index>=items.length Throw "Gadget item index out of range."
?
If State Then Self.SelectItem(index);
End Method
'#end region
'#region panels
Method FitGadget ( gadget:TGadget, index:Int, x:Int, y:Int, Style:Int = 0 )
Local lParent:TGadget = Self.GetPanel(index);
If lParent=Null Then Return;
Local w:Int = gadget.GetWidth();
Local h:Int = gadget.GetHeight();
If Style & STYLE_FITW Then w = lParent.ClientWidth() - x;
If Style & STYLE_FITH Then h = lParent.ClientHeight() - y;
If ( ((Style & STYLE_FITW)>0) Or ((Style & STYLE_FITH)>0) ) Then gadget.SetShape(x,y,w,h);
If (Style & STYLE_FIT)
gadget.SetLayout( ( (Style & STYLE_LAYOUTL)>0 ) * EDGE_ALIGNED, ..
( (Style & STYLE_LAYOUTR)>0 ) * EDGE_ALIGNED, ..
( (Style & STYLE_LAYOUTT)>0 ) * EDGE_ALIGNED, ..
( (Style & STYLE_LAYOUTB)>0 ) * EDGE_ALIGNED );
EndIf;
End Method
Method getChild:TGadget(index:Int, childIndex:Int=0)
Local l:TList = Self._gpanels[index].kids;
If childIndex=0 Then Return TGadget(l.First());
Return TGadget(l.FindLink(l.ValueAtIndex(childIndex)).Value());
End Method
Method countPanelChildren:Int(index:Int)
Return Self.GetPanel(index).kids.Count();
End Method
'#end region
Function demo()
Local WIN_FLAG:Int = WINDOW_TITLEBAR|WINDOW_RESIZABLE|WINDOW_CLIENTCOORDS|WINDOW_CENTER;
Local w:TGadget = CreateWindow("name", 50,50,600,430, Null, WIN_FLAG);
Local icon_pixmap:TPixmap = CreatePixmap(256,32, PF_RGBA8888);
icon_pixmap.ClearPixels($FF505050);
For Local c:Int = 0 To 5
If c=3
For Local cx:Int = 0 To 31
For Local cy:Int = 0 To 31
icon_pixmap.WritePixel(32*c+cx, cy, $FF202020);
Next
Next
EndIf
For Local ct:Int = 16-(c+2)*2 To 16+(c+2)*2
icon_pixmap.WritePixel(32*c + 16-(c+2)*2, ct, $FFFFFFFF);
icon_pixmap.WritePixel(32*c + 16+(c+2)*2, ct, $FFFFFFFF);
icon_pixmap.WritePixel(32*c + ct,16-(c+2)*2, $FFFFFFFF);
icon_pixmap.WritePixel(32*c+ ct,16+(c+2)*2, $FFFFFFFF);
Next;
For Local cx:Int = 0 To c
Local dx:Int = -c*2 + cx*4;
For Local cy:Int = 16-(c+2) To 16+(c+2)
icon_pixmap.WritePixel(32*c + 16 + dx ,cy, $FF010101);
Next;
Next;
For Local ct:Int = 0 Until 32
icon_pixmap.WritePixel(32*c+ct, 0, $FF010101);
icon_pixmap.WritePixel(32*c+ct, 31, $FF010101);
icon_pixmap.WritePixel(32*c, ct, $FF010101);
icon_pixmap.WritePixel(32*c+31, ct, $FF010101);
Next;
Next;
Local ICON_STRIP:TIconStrip = LoadIconStrip ( icon_pixmap );
' modify the flag to set the look of the tabber
' -> Vertical makes a tabber on the left or right border
' -> TABBER_BOTTOM makes it appear at the bottom. If TABBER_VERTICAL -> the tabber will appear on the sides at the bottom
' -> TABBER_RIGHT align the tabber on the right.
'
' This is where the buttons will appear according to each combination
'
' -----------------
' | HTL HTR |
' -----------------------------
' | VTL | | VTR |
' | | | |
' | | | |
' | VBL | | VBR |
' -----------------------------
' | HBL HBR |
' -----------------
'
' H = Horizontal (Default) , V = TABBER_VERTICAL
' T = Top (Default) , B = TABBER_BOTTOM
' L = Left (Default) , R = TABBER_RIGHT
Local fg:Int = 0; ' default
fg :| TABBER_VERTICAL; ' add vertical style
fg :| TABBER_BOTTOM; ' align vertical to the bottom
' fg :| TABBER_RIGHT; ' use the right side
Local It:TIconTabber = CreateIconTabber(0,0, ClientWidth(w),ClientHeight(w)-20, w, fg);
' stick the tabber core to the window when resized
It.SetLayout ( EDGE_ALIGNED, EDGE_ALIGNED, EDGE_ALIGNED, EDGE_ALIGNED );
' define some colors
it.SetColor ( 20,22,25 );
it.SetTextColor ( 000,000,000 );
It.setCursorColor ( 100,105,110 );
' use our previous pixmap as icon strip (you can also load a standard iconstrip)
it.SetIconStrip ( ICON_STRIP );
' set some parameters to adapt the style.
It.setCursorSize ( 3 );
It.setTabSize ( 32 );
' not required, but I like to name every objects.
It.SetText ( "MyTabber" );
' add some buttons to the tabber
AddGadgetItem ( It, "Item 1", GADGETITEM_NORMAL , 0, "Tab 1 reddish", Null );
AddGadgetItem ( It, "Item 2", GADGETITEM_NORMAL , 1, "Tab 2 Yellowish", Null );
AddGadgetItem ( It, "Item 3", GADGETITEM_TOGGLE , 2, "Tab 3 Blueish", Null ); ' this icon switches if selected.
AddGadgetItem ( It, "item 4", GADGETITEM_DEFAULT, 4, "Tab 4 purpleish", Null ); ' this is the selected by default (GADGETITEM_DEFAULT)
AddGadgetItem ( It, "item 5", GADGETITEM_NORMAL , 5, "Tab 5 tealish", Null );
' add some panels to the internal tabber panels (create them on the fly, no need to keep track on the pointer, we'll get them later with GetChild)
It.FitGadget ( CreatePanel(0,0,1,1, It.GetPanel(0)), 0, 0,0, STYLE_FIT|STYLE_LAYOUT ); ' automatic fit to the internal panel area
It.FitGadget ( CreatePanel(0,0,1,1, It.GetPanel(1)), 1, 0,0, STYLE_FIT|STYLE_LAYOUT );
It.FitGadget ( CreatePanel(0,0,1,1, It.GetPanel(2)), 2, 0,0, STYLE_FIT|STYLE_LAYOUT );
It.FitGadget ( CreatePanel(0,0,1,1, It.GetPanel(3)), 3, 0,0, STYLE_FIT|STYLE_LAYOUT );
It.FitGadget ( CreatePanel(0,0,1,1, It.GetPanel(4)), 4, 0,0, STYLE_FIT|STYLE_LAYOUT );
' let's color the created panels
It.getChild(0,0) .SetColor(80,27,30); ' Panel at index 0, first child
It.getChild(1,0) .SetColor(80,70,30); ' Panel at index 1, first child
It.getChild(2,0) .SetColor(25,27,80); ' etc ...
It.getChild(3,0) .SetColor(70,27,80);
It.getChild(4,0) .SetColor(25,70,80);
Local label:TGadget = CreateLabel ( "status :", 0, ClientHeight(w)-20, ClientWidth(w),20, w );
label.SetLayout ( EDGE_ALIGNED, EDGE_ALIGNED, 0, EDGE_ALIGNED );
Repeat
While (PollEvent()<>Null)
Select EventID()
Case EVENT_WINDOWCLOSE
End;
Case EVENT_GADGETACTION
If EventMods()
label.SetText( "status : double-click on item "+EventData() );
Else
label.SetText( "status : selected item "+EventData() );
EndIf;
Case EVENT_GADGETMENU
label.SetText( "status : Right click on item "+EventData() );
End Select;
Wend;
Forever;
End Function
End Type

Function GetTabberPanel:TGadget(tabber:TGadget, index:Int)
Return TIconTabber(tabber).GetPanel(index);
End Function

rem
bbdoc: Create a Colored Tabber
about: each tab is an icon without text
The body and toolbar can be "colored"
endrem
Function CreateIconTabber:TIconTabber ( x:Int, y:Int, w:Int, h:Int, group:TGadget, Style:Int=0 )
Return TIconTabber.Create(x,y,w,h, group, Style);
End Function

TIconTabber.demo()
---------------- end of magic text ---------------
