; ID: 1653
; Author: Nilium
; Date: 2006-03-31 03:17:43
; Title: Object-Oriented MaxGUI Wrapper
; Description: What the title says

SuperStrict

Import "collections.bmx"

Import Brl.Event
Import Brl.EventQueue
Import MaxGui.MaxGui
?Win32
Import MaxGui.Win32MaxGui
?MacOS
Import MaxGui.CocoaMaxGui
?Linux
Import MaxGui.FLTKMaxGui
?

Private
Function _gadUpdate:Object( id%, obj:Object, ctx:Object )
    Local gad:IGadget = IGadget(ctx)
    Local evt:TEvent = TEvent(obj)
    If Not gad Or Not evt Then Return obj
    Local m:IMenu = IMenu(gad)
    If (evt.id = EVENT_MENUACTION And m = Null) Or gad.gad = Null Then Return obj
    If evt.source <> gad.gad And m = Null Then Return obj
    If gad._freed Then Return obj
    Select evt.id
        Case EVENT_MOUSEENTER
            gad.OnEnter( evt )
        Case EVENT_MOUSELEAVE
            gad.OnLeave( evt )
        Case EVENT_KEYDOWN
            gad.OnKeyDown( evt )
        Case EVENT_KEYUP
            gad.OnKeyUp( evt )
        Case EVENT_KEYCHAR
            gad.OnKeyChar( evt )
        Case EVENT_MOUSEMOVE
            gad.OnMouseMove( evt )
        Case EVENT_MOUSEWHEEL
            gad.OnMouseWheel( evt )
        Case EVENT_MOUSEDOWN
            gad.OnMouseDown( evt )
        Case EVENT_MENUACTION
            If m.id = evt.data Then m.OnAction( evt )
        Case EVENT_WINDOWMOVE
            If IWindow(gad) Then IWindow(gad).OnMove( evt )
        Case EVENT_WINDOWACTIVATE
            If IWindow(gad) Then IWindow(gad).OnFocus( evt )
        Case EVENT_WINDOWSIZE
            If IWindow(gad) Then IWindow(gad).OnResize( evt )
        Case EVENT_WINDOWACCEPT
            If IWindow(gad) Then IWindow(gad).OnDragDrop( evt )
        Case EVENT_WINDOWCLOSE
            If IWindow(gad) Then IWindow(gad).OnClose( evt )
        Case EVENT_GADGETOPEN
            If ITreeView(gad) Then ITreeView(gad).OnExpand( evt )
        Case EVENT_GADGETCLOSE
            If ITreeView(gad) Then ITreeView(gad).OnCollapse( evt )
        Case EVENT_GADGETSELECT
            If ITreeView(gad) Then ITreeView(gad).OnSelect( evt )
        Case EVENT_GADGETMENU
            Local txt:ITextbox = ITextbox( gad )
            Local tv:ITreeView = ITreeView( gad )
            If txt Then
                txt.OnMenu( evt )
            ElseIf tv Then
                tv.OnMenu( evt )
            EndIf
        Case EVENT_GADGETDONE
            If IHTMLView(gad) Then IHTMLView( gad ).OnLoaded( evt )
        Case EVENT_GADGETACTION
            gad.OnAction( evt )
        Case EVENT_GADGETPAINT
            If ICanvas(gad) Then ICanvas(gad).OnPaint( evt )
    End Select
    Return obj
End Function

Public
Type IGadget Abstract
    Field _freed:Int = 0
    Field gad:TGadget
    Field _children:IList = New IList
    Field _parent:IGadget
    Field _link:ILink
    Field OnActionHandler(sender:IGadget, event:TEvent)
    Field OnEnterHandler(sender:IGadget, event:TEvent)
    Field OnLeaveHandler(sender:IGadget, event:TEvent)
    Field OnKeyUpHandler(sender:IGadget, event:TEvent)
    Field OnKeyDownHandler(sender:IGadget, event:TEvent)
    Field OnKeyCharHandler(sender:IGadget, event:TEvent)
    Field OnMouseWheelHandler(sender:IGadget, event:TEvent)
    Field OnMouseUpHandler(sender:IGadget, event:TEvent)
    Field OnMouseDownHandler(sender:IGadget, event:TEvent)
    Field OnMouseMoveHandler(sender:IGadget, event:TEvent)
    Field OnPaintHandler(sender:IGadget, event:TEvent)
    Field OnHotKeyHitHandler(sender:IGadget, event:TEvent)
    Field OnInitHandler(sender:IGadget, event:TEvent)'event should always be null for OnInitHandler
    
    Method Init:TGadget( parent:IGadget )
        If _freed Then Return Null
        _parent = parent
        If _parent Then _link = _parent._children.AddLast( Self )
        Local par:TGadget = Null
        If _parent Then par = _parent.gad
        Return par
    End Method
    
    Method OnInit( evt:TEvent )
        If OnInitHandler Then OnInitHandler(Self, evt)
    End Method
    
    Method OnAction( evt:TEvent )
        If OnActionHandler Then OnActionHandler(Self, evt)
    End Method
    
    Method OnEnter( evt:TEvent )
        If _freed Then Return
        If OnEnterHandler Then OnEnterHandler(Self, evt)
    End Method
    
    Method OnLeave( evt:TEvent )
        If _freed Then Return
        If OnLeaveHandler Then OnLeaveHandler(Self, evt)
    End Method
    
    Method OnKeyDown( evt:TEvent )
        If _freed Then Return
        If OnKeyDownHandler Then OnKeyDownHandler(Self, evt)
    End Method
    
    Method OnKeyUp( evt:TEvent )
        If _freed Then Return
        If OnKeyUpHandler Then OnKeyUpHandler(Self, evt)
    End Method
    
    Method OnKeyChar( evt:TEvent )
        If _freed Then Return
        If OnKeyCharHandler Then OnKeyCharHandler(Self, evt)
    End Method
    
    Method OnMouseWheel( evt:TEvent )
        If _freed Then Return
        If OnMouseWheelHandler Then OnMouseWheelHandler(Self, evt)
    End Method
    
    Method OnMouseDown( evt:TEvent )
        If _freed Then Return
        If OnMouseDownHandler Then OnMouseDownHandler(Self, evt)
    End Method
    
    Method OnMouseUp( evt:TEvent )
        If _freed Then Return
        If OnMouseUpHandler Then OnMouseUpHandler(Self, evt)
    End Method
    
    Method OnMouseMove( evt:TEvent )
        If _freed Then Return
        If OnMouseMoveHandler Then OnMouseMoveHandler(Self, evt)
    End Method
    
    Method OnHotkeyHit( evt:TEvent )
        If _freed Then Return
        If OnHotkeyHitHandler Then OnHotkeyHitHandler(Self, evt)
    End Method
    
    Method New( )
        AddHook( EmitEventHook, _gadUpdate, Self )
    End Method
    
    Method Delete( )
        Free( )
    End Method
    
    Method Free( )
        If _freed Then Return
        RemoveHook( EmitEventHook, _gadUpdate, Self )
        For Local i:IGadget = EachIn _children
            i.Free( )
        Next
        gad.Free( )
        gad = Null
        OnActionHandler = Null
        OnEnterHandler = Null
        OnLeaveHandler = Null
        OnKeyUpHandler = Null
        OnKeyDownHandler = Null
        OnKeyCharHandler = Null
        OnMouseWheelHandler = Null
        OnMouseUpHandler = Null
        OnMouseDownHandler = Null
        OnMouseMoveHandler = Null
        OnHotKeyHitHandler = Null
        OnInitHandler = Null
        _freed = True
    End Method
        
    Method GetState%( )
        If _freed Then Return 0
        Return gad.State( )
    End Method
        
    ' Layout
    Method Layout( l%, r%, t%, b% )
        If _freed Then Return
        gad.SetLayout( l, r, t, b )
    End Method
    
    ' parenting
    Method GetParent:IGadget( )
        If _freed Then Return Null
        Return _parent
    End Method
    
    Method GetChild:IGadget( idx% )
        If _freed Then Return Null
        Return IGadget(_children.ValueAtIndex(idx))
    End Method
    
    ' position
    Method SetPosition( x%, y% )
        If _freed Then Return
        gad.SetShape( x, y, gad.width, gad.height )
    End Method
    
    Method SetSize( w%, h% )
        If _freed Then Return
        gad.SetShape( gad.xpos, gad.ypos, w, h )
    End Method
    
    Method GetPosition( x% Var, y% Var )
        If _freed Then Return
        x = gad.xpos
        y = gad.ypos
    End Method
    
    Method GetSize( x% Var, y% Var )
        If _freed Then Return
        x = gad.width
        y = gad.height
    End Method
    
    Method GetClientSize( x% Var, y% Var )
        If _freed Then Return
        x = gad.ClientWidth( )
        y = gad.ClientHeight( )
    End Method
    
    Method GetClientWidth%( )
        If _freed Then Return 0
        Local x%,y%
        GetClientSize( x, y )
        Return x
    End Method
    
    Method GetClientHeight%( )
        If _freed Then Return 0
        Local x%,y%
        GetClientSize( x, y )
        Return y
    End Method
    
    Method GetX%( )
        If _freed Then Return 0
        Local x%,y%
        GetPosition( x, y )
        Return x
    End Method
    
    Method GetY%( )
        If _freed Then Return 0
        Local x%,y%
        GetPosition( x, y )
        Return y
    End Method
    
    Method GetWidth%( )
        If _freed Then Return 0
        Local x%,y%
        GetSize( x, y )
        Return x
    End Method
    
    Method GetHeight%( )
        If _freed Then Return 0
        Local x%,y%
        GetSize( x, y )
        Return y
    End Method
    
    ' Enabled
    Method Disable( )
        If _freed Then Return
        gad.SetEnabled( False )
    End Method
    
    Method Enable( )
        If _freed Then Return
        gad.SetEnabled( True )
    End Method
    
    ' Hidden
    Method Hide( )
        If _freed Then Return
        gad.SetShow( False )
    End Method
    
    Method Show( )
        If _freed Then Return
        gad.SetShow( True )
    End Method
    
    ' Active
    Method Activate( cmd% = ACTIVATE_FOCUS )
        If _freed Then Return
        gad.Activate( cmd )
    End Method
    
    ' Focus
    Method Focus( )
        If _freed Then Return
        Activate( ACTIVATE_FOCUS )
    End Method
    
    ' Redraw
    Method Redraw( )
        If _freed Then Return
        Activate( ACTIVATE_REDRAW )
    End Method
    
    ' Alpha
    Method SetAlpha( a# )
        If _freed Then Return
        gad.SetAlpha( a )
    End Method
    
    ' Color
    Method SetTextColor( r%, g%, b% )
        If _freed Then Return
        gad.SetTextColor( r, g, b )
    End Method
    
    Method SetBackColor( r%, g%, b% )
        If _freed Then Return
        gad.SetColor( r, g, b )
    End Method
    
    ' Font
    Method SetFont( font:TGuiFont )
        If _freed Then Return
        gad.SetFont( font )
    End Method
    
    Method SetCaption( text$ )
        If _freed Then Return
        gad.SetText( text )
    End Method
    
    ' Text
    Method GetText$( )
        If _freed then Return ""
        Return gad.GetText( )
    End Method
    
    Method SetText( t$ )
        If _freed Then Return
        gad.SetText( t )
    End Method
    
    ' Query
    Method Query%( q% )
        If _freed Then Return 0
        Return gad.Query( q )
    End Method
End Type

Type IDesktop Extends IGadget
    Method New( )
        gad = Desktop( )
    End Method
End Type

Type IWindow Extends IGadget
    Field rootMenu:IMenu
    Field OnFocusHandler(sender:IGadget, e:TEvent)
    Field OnCloseHandler(sender:IGadget, e:TEvent)
    Field OnMoveHandler(sender:IGadget, e:TEvent)
    Field OnResizeHandler(sender:IGadget, e:TEvent)
    Field OnDragDropHandler(sender:IGadget, e:TEvent)
    
    Method OnFocus( evt:TEvent )
        If _freed Then Return
        If OnFocusHandler Then OnFocusHandler(Self, evt)
    End Method
    
    Method OnClose( evt:TEvent )
        If _freed Then Return
        If OnCloseHandler Then OnCloseHandler(Self, evt)
    End Method
    
    Method OnMove( evt:TEvent )
        If _freed Then Return
        If OnMoveHandler Then OnMoveHandler(Self, evt)
    End Method
    
    Method OnResize( evt:TEvent )
        If _freed Then Return
        If OnResizeHandler Then OnResizeHandler(Self, evt)
    End Method
    
    Method OnDragDrop( evt:TEvent )
        If _freed Then Return
        If OnDragDropHandler Then OnDragDropHandler(Self, evt)
    End Method
    
    Method Free( )
        If _freed Then Return
        For Local i:IGadget = eachIn rootmenu._children
            i.Free( )
        Next
        rootmenu._children.Clear( )
        rootmenu._children = Null
        rootmenu._parent = Null
        rootmenu.gad = Null
        rootmenu = Null
        OnFocusHandler = Null
        OnCloseHandler = Null
        OnMoveHandler = Null
        OnResizeHandler = Null
        OnDragDropHandler = Null
        Super.Free( )
    End Method
    
    Method Create( parent:IGadget, x%, y%, w%, h%, caption$, titlebar%=1, sizable%=1, menu%=1, statusbar%=1, hidden%=1, dragdrop%=0, tool%=0 )
        If _freed Then Return
        Local f:Int = WINDOW_CLIENTCOORDS
        If titlebar>0 Then f :| WINDOW_TITLEBAR
        If sizable>0 Then f :| WINDOW_RESIZABLE
        If menu>0 Then f:| WINDOW_MENU
        If statusbar>0 Then f :| WINDOW_STATUS
        If hidden>0 Then f :| WINDOW_HIDDEN
        If dragdrop>0 Then f:| WINDOW_ACCEPTFILES
        If tool>0 Then f :| WINDOW_TOOL
        gad = CreateWindow( caption, x, y, w, h, Init( parent ), f )
        rootmenu = New IMenu
        rootmenu.gad = gad.GetMenu( )
        rootmenu._parent = Self
        OnInit( Null )
    End Method
    
    Method AddMenu:IMenu( caption$, tag%, hotkey%=0, modifier%=0 )
        If _freed Then Return Null
        Local menu:IMenu = New IMenu
        menu.Create( rootmenu, caption, tag, hotkey, modifier )
        Return menu
    End Method
    
    Method UpdateMenu( )
        If _freed then Return
        gad.UpdateMenu( )
    End Method
    
    Method Maximized%( )
        If _freed Then Return 0
        Return GetState( ) & STATE_MAXIMIZED
    End Method
    
    Method Maximize( )
        If _freed Then Return
        Activate( ACTIVATE_MAXIMIZE )
    End Method
    
    Method Minimized%( )
        If _freed Then Return 0
        Return GetState( ) & STATE_MINIMIZED
    End Method
    
    Method Minimize( )
        If _freed Then Return
        Activate( ACTIVATE_MINIMIZE )
    End Method
    
    Method Restore( )
        If _freed Then Return
        Activate( ACTIVATE_RESTORE )
    End Method
    
    Method SetMinimumSize( w%, h% )
        If _freed Then Return
        gad.SetMinimumSize( w, h )
    End Method
    
    Method SetStatusText( text$ )
        If _freed Then Return
        gad.SetStatus( text )
    End Method
    
    Method Hwnd%( )
        Return Query( QUERY_HWND )
    End Method
End Type

Const BUTTON_NORMAL%=0
Type IButton Extends IGadget
    Method Create( parent:IGadget, x%, y%, w%, h%, caption$, type_%=BUTTON_NORMAL ) 'type_ can be BUTTON_OK, BUTTON_CANCEL, or BUTTON_NORMAL
        If _freed Then Return
        Local f% = BUTTON_PUSH | type_
        gad = CreateButton( caption, x, y, w, h, Init( parent ), f )
        OnInit( Null )
    End Method
End Type

Type ICheckbox Extends IGadget
    Method Create( parent:IGadget, x%, y%, w%, h%, caption$, checked%=0 )
        If _freed Then Return
        Local f% = BUTTON_CHECKBOX
        gad = CreateButton( caption, x, y, w, h, Init( parent ), f )
        If checked>0 Then Check( )
        OnInit( Null )
    End Method
    
    Field chk:Int = 0
    
    Method Check( )
        If _freed Then Return
        gad.SetSelected( True )
        chk = True
    End Method
    
    Method Uncheck( )
        If _freed Then Return
        gad.SetSelected( False )
        chk = False
    End Method
    
    Method Checked%( )
        If _freed Then Return -1
        Return chk
    End Method
End Type

Type IRadioGroup
    Field _link:ILink
    Field group%
    Field tickers:IList = New IList
    
    Method New( )
        _link = radioGroups.AddLast( Self )
    End Method
    
    Method Remove( gad:IRadiobox )
        gad._glink.Remove( )
        gad._glink = Null
        If tickers.Count( ) = 0 Then
            _link.Remove( )
            _link = Null
            tickers = Null
        EndIf
    End Method
End Type
Private
Global radioGroups:IList = New IList
Public

Type IRadiobox Extends IGadget
    Field group:Object
    Field _glink:ILink
    Field chk%=0
    
    Method Free( )
        If _freed then Return
        IRadioGroup(group).Remove( Self )
        Super.Free( )
    End Method
    
    Method Create( parent:IGadget, x%, y%, w%, h%, caption$, group%, ticked%=0 )
        If _freed Then Return
        Local f% = BUTTON_RADIO
        gad = CreateButton( caption, x, y, w, h, Init( parent ), f )
        
        Local g:IRadioGroup = Null
        For Local i:IRadioGroup = EachIn radioGroups
            If i.group = group Then
                g = i
                Exit
            EndIf
        Next
        
        If Not g Then
            g = New IRadioGroup
            g.group = group
        EndIf
        
        _glink = g.tickers.AddLast( Self )
        
        OnInit( Null )
    End Method
    
    Method Check( )
        If _freed Then Return
        gad.SetSelected( True )
        chk = True
        For Local i:IRadiobox = EachIn IRadioGroup(group).tickers
            If i <> Self Then i.Uncheck( )
        Next
    End Method
    
    Method Uncheck( )
        If _freed Then Return
        gad.SetSelected( False )
        chk = False
    End Method
    
    Method Checked%( )
        If _freed Then Return -1
        Return chk
    End Method
    
    Method OnAction( evt:TEvent )
        Check( )
        Super.OnAction( evt )
    End Method
End Type

Type ICanvas Extends IGadget
    Field gfx:TGraphics
    Field OnPaintHandler( s:IGadget, e:TEvent )
    
    Method OnPaint( evt:TEvent )
        If _freed Then Return
        If OnPaintHandler Then OnPaintHandler(Self, evt)
    End Method
    
    Method Free( )
        If _freed Then Return
        OnPaintHandler = Null
        Super.Free( )
    End Method
    
    Method Create( parent:IGadget, x%, y%, w%, h%, border%=0 )
        If _freed Then Return
        Local f% = (border>0)*PANEL_BORDER
        gad = CreateCanvas( x, y, w, h, Init( parent ), f )
        gfx = gad.CanvasGraphics( )
        OnInit( Null )
    End Method
End Type

Type IPanel Extends IGadget
    Method Create( parent:IGadget, x%, y%, w%, h%, border%=0, moveEvent%=0 )
        If _freed Then Return
        Local f% = (border>0)*PANEL_BORDER|((moveEvent>0)*PANEL_ACTIVE)
        gad = CreatePanel( x, y, w, h, Init( parent ), f, "" )
        OnInit( Null )
    End Method
    
    Method SetPixmap( pix:TPixmap, flags%=PANELPIXMAP_TILE )
        If _freed Then Return
        gad.SetPixmap( pix, flags )
    End Method
End Type

Type IGroupbox Extends IGadget
    Method Create( parent:IGadget, x%, y%, w%, h%, caption$, moveEvent%=0 )
        If _freed Then Return
        Local f% = PANEL_GROUP|((moveEvent>0)*PANEL_ACTIVE)
        gad = CreatePanel( x, y, w, h, Init( parent ), f, caption )
        OnInit( Null )
    End Method
End Type

Type IComboBox Extends IGadget
    Method Create( parent:IGadget, x%, y%, w%, h%, editable%=0 )
        If _freed Then Return
        Local f% = (editable>0)*COMBOBOX_EDITABLE
        gad = CreateComboBox( x, y, w, h, Init( parent ), f )
        OnInit( Null )
    End Method
    
    Method SelectedItem%( )
        If _freed Then Return 0
        Return gad.SelectedItem( )
    End Method
    
    Method SelectItem( idx% )
        If _freed Then Return
        gad.SelectItem( idx, 1 )
    End Method
    
    Method AddItem%( caption$, icon%=-1, isDefault%=0, tip$="", extra:Object=Null )
        If _freed Then Return 0
        AddGadgetItem( gad, caption, (isDefault>0)*GADGETITEM_DEFAULT, icon, tip, extra )
        Return gad.ItemCount( ) - 1
    End Method
    
    Method RemoveItem( idx% )
        If _freed Then Return
        RemoveGadgetItem( gad, idx )
    End Method
    
    Method GetItemCaption$( idx% )
        If _freed Then Return ""
        Return GadgetItemText( gad, idx )
    End Method
    
    Method GetItemExtra:Object( idx% )
        If _freed Then Return Null
        Return GadgetItemExtra( gad, idx )
    End Method
End Type

Type IHTMLView Extends IGadget
    Field OnLoadedHandler(sender:IGadget, e:TEvent)
    
    Method Create( parent:IGadget, x%, y%, w%, h%, nonav%=0, noctx%=0 )
        If _freed Then Return
        Local f% = 0
        If nonav>0 Then f :| HTMLVIEW_NONAVIGATE
        If noctx>0 Then f :| HTMLVIEW_NOCONTEXTMENU
        gad = CreateHTMLView( x, y, w, h, Init( parent ), f )
        OnInit( Null )
    End Method
    
    Method GetURL$( )
        If _freed Then Return ""
        Return gad.GetText( )
    End Method
    
    Method SetURL( url$ )
        If _freed Then Return
        gad.SetText( url )
    End Method
    
    Method Back( )
        If _freed Then Return
        Activate( ACTIVATE_BACK )
    End Method
    
    Method Forward( )
        If _freed Then Return
        Activate( ACTIVATE_FORWARD )
    End Method
    
    Method RunScript$( script$ )
        If _freed Then Return ""
        Return gad.Run( script )
    End Method
    
    Method OnLoaded( evt:TEvent )
        If OnLoadedHandler Then OnLoadedHandler(Self, evt)
    End Method
End Type

Const LABEL_LEFT% = 0
Const LABEL_NOFRAME% = 0
Type ILabel Extends IGadget
    Method Create( parent:IGadget, x%, y%, w%, h%, text$, frameType%=LABEL_NOFRAME, align%=LABEL_LEFT )
        If _freed Then Return
        Local f% = frameType|align
        If text="--" Then f :| LABEL_SEPARATOR
        gad = CreateLabel( text, x, y, w, h, Init( parent ), f )
        OnInit( Null )
    End Method
End Type

Type IListBox Extends IGadget
    Method Create( parent:IGadget, x%, y%, w%, h% )
        If _freed Then Return
        Local f% = 0
        gad = CreateListBox( x, y, w, h, Init( parent ), f )
        OnInit( Null )
    End Method
    
    Method AddItem%( caption$, icon%=-1, isDefault%=0, tip$="", extra:Object=Null )
        If _freed Then Return 0
        AddGadgetItem( gad, caption, (isDefault>0)*GADGETITEM_DEFAULT, icon, tip, extra )
        Return gad.ItemCount( ) - 1
    End Method
    
    Method SelectItem( idx% )
        If _freed Then Return
        gad.SelectItem( idx, 1 )
    End Method
    
    Method RemoveItem( idx% )
        If _freed Then Return
        RemoveGadgetItem( gad, idx )
    End Method
    
    Method SelectedItem%( )
        If _freed Then Return 0
        Return gad.SelectedItem( )
    End Method
    
    Method SelectedItems%[]( )
        If _freed Then Return Null
        Return gad.SelectedItems( )
    End Method
    
    Method GetItemCaption$( idx% )
        If _freed Then Return ""
        Return GadgetItemText( gad, idx )
    End Method
    
    Method GetItemExtra:Object( idx% )
        If _freed Then Return Null
        Return GadgetItemExtra( gad, idx )
    End Method
End Type

Type IMenu Extends IGadget
    Field OnSelectHandler(sender:IGadget, e:TEvent)
    Field id:Int
    Field chk:Int=0
    
    Method Free( )
        If _freed Then Return
        OnSelectHandler = Null
        Super.Free( )
    End Method
    
    Method Create( parent:IGadget, caption$, tag%, hotkey%=0, modifier%=0 )
        If _freed Then Return
        gad = CreateMenu( caption, tag, Init(parent), hotkey, modifier )
        id = tag
        OnInit( Null )
    End Method
    
    Method AddMenu:IMenu( caption$, tag%, hotkey%=0, modifier%=0 )
        If _freed Then Return Null
        Local menu:IMenu = New IMenu
        menu.Create( Self, caption, tag, hotkey, modifier )
        Return menu
    End Method
    
    Method OnSelect( evt:TEvent )
        If OnSelectHandler Then OnSelectHandler(Self, evt)
    End Method
    
    Method Popup( on:IGadget )
        If _freed Then Return
        on.gad.PopupMenu( gad )
    End Method
    
    Method Check( )
        If _freed Then Return
        gad.SetSelected( True )
        chk = True
    End Method
    
    Method Uncheck( )
        If _freed Then Return
        gad.SetSelected( False )
        chk = False
    End Method
    
    Method Checked%( )
        If _freed Then Return -1
        Return chk
    End Method
End Type

Type IProgressBar Extends IGadget
    Method Create( parent:IGadget, x%, y%, w%, h% )
        If _freed Then Return
        gad = CreateProgBar( x, y, w, h, Init(parent), 0 )
        OnInit( Null )
    End Method
    
    Method SetValue( progress# )
        If _freed Then Return
        gad.SetValue( progress )
    End Method
End Type

Type ISlider Extends IGadget
    Method SetRange( min_%, max_% )
        If _freed Then Return
        gad.SetRange( min_, max_ )
    End Method
    
    Method SetValue( value% )
        If _freed Then Return
        gad.SetProp( value )
    End Method
    
    Method GetValue%( )
        If _freed Then Return 0
        Return gad.GetProp( )
    End Method
End Type

Type IScrollbar Extends ISlider
    Const _flags%=SLIDER_SCROLLBAR
    
    Method Create( parent:IGadget, x%, y%, w%, h%, vertical%=0, range_min%=1, range_max%=10 )
        If _freed Then Return
        Local f% = SLIDER_HORIZONTAL
        If vertical>0 Then f = SLIDER_VERTICAL
        f :| _flags
        gad = CreateSlider( x, y, w, h, Init(parent), f )
        SetRange( range_min, range_max )
        OnInit( Null )
    End Method
End Type

Type ITracker Extends ISlider
    Const _flags%=SLIDER_TRACKBAR
    
    Method Create( parent:IGadget, x%, y%, w%, h%, vertical%=0, range_min%=1, range_max%=10 )
        If _freed Then Return
        Local f% = SLIDER_HORIZONTAL
        If vertical>0 Then f = SLIDER_VERTICAL
        f :| _flags
        gad = CreateSlider( x, y, w, h, Init(parent), f )
        SetRange( range_min, range_max )
        OnInit( Null )
    End Method
End Type

Type IStepper Extends ISlider
    Const _flags%=SLIDER_STEPPER
    
    Method Create( parent:IGadget, x%, y%, w%, h%, vertical%=0, range_min%=1, range_max%=10 )
        If _freed Then Return
        Local f% = SLIDER_HORIZONTAL
        If vertical>0 Then f = SLIDER_VERTICAL
        f :| _flags
        gad = CreateSlider( x, y, w, h, Init(parent), f )
        SetRange( range_min, range_max )
        OnInit( Null )
    End Method
End Type

' Dial slider apparently isn't supported by Win32MaxGUI?

Type ITabStrip Extends IGadget
    Method Create( parent:IGadget, x%, y%, w%, h% )
        If _freed Then Return
        gad = CreateTabber( x, y, w, h, Init(parent), 0 )
        OnInit( Null )
    End Method
    
    Method AddPage%( caption$, icon%=-1, tip$="", extra:Object=Null )
        If _freed Then Return -1
        AddGadgetItem( gad, caption, False, icon, tip, extra )
        Return gad.ItemCount( ) - 1
    End Method
    
    Method RemovePage( idx% )
        RemoveGadgetItem( gad, idx )
    End Method
    
    Method SelectPage( page% )
        If _freed Then Return
        gad.SelectItem( page, 1 )
    End MEthod
    
    Method SelectedPage%( )
        If _freed Then Return 0
        Return gad.SelectedItem( )
    End Method
End Type

Type ITextBox Extends IGadget
    Field _area:Int=0
    Field OnMenuHandler(sender:IGadget, e:TEvent)
    
    Method Free( )
        If _freed Then Return
        OnMenuHandler = Null
        Super.Free( )
    End Method
    
    Method Create( parent:IGadget, x%, y%, w%, h%, text$="", multiline%=0, readonly%=0, wordwrap%=0, password%=0 )
        If _freed Then Return
        Local par:TGadget = Init(parent)
        Local f% = 0
        If multiline>0 Then
            _area = True
            If readonly>0 Then f :| TEXTAREA_READONLY
            If wordwrap>0 Then f :| TEXTAREA_WORDWRAP
            gad = CreateTextArea( x, y, w, h, par, f )
        Else
            If password>0 Then f :| TEXTFIELD_PASSWORD
            gad = CreateTextField( x, y, w, h, par, f )
        EndIf
        OnInit( Null )
    End Method
    
    Method OnMenu( evt:TEvent )
        If OnMenuHandler Then OnMenuHandler(Self, evt)
    End Method
    
    Field _fbuf$=""
    Field _lock%=0
    
    Method SelectText$( from%=0, length%=-1, selectLines%=0 )
        If _freed Then Return ""
        If _area Then
            Local f% = TEXTAREA_CHARS
            If selectLines>0 Then f = TEXTAREA_LINES
            If length = -1 Then length = TEXTAREA_ALL
            Return gad.AreaText( from, length, f% )
        Else
            Local s$
            If Not _lock Then
                s = gad.GetText( )
            Else
                s = _fbuf
            EndIf
            If (length = -1 Or length=s.Length) And from = 0 Then Return s
            If length = -1 Then length = s.Length-from
            Return s[from..from+length]
        EndIf
    End Method
    
    Method GetText$( )
        If _freed Then Return ""
        Return SelectText( )
    End Method
    
    Method SetText( t$ )
        If _freed then Return
        ReplaceText( t )
    End Method
    
    Method ReplaceText( t$, pos%=0, length%=-1, lines%=0 )
        If _freed Then Return
        If _area Then
            If length = -1 Then length = TEXTAREA_ALL
            If lines>0 Then
                lines = TEXTAREA_LINES
            Else
                lines = TEXTAREA_CHARS
            EndIf
            gad.ReplaceText( pos, length, t, lines )
        Else
            If _lock Then
                If pos = 0 And (length = -1 Or length=_fbuf.Length) Then
                    _fbuf = t
                    Return
                EndIf
                
                If length = -1 Then length = _fbuf.Length-pos
                
                If pos=_fbuf.Length Then
                    _fbuf :+ t
                ElseIf pos=0 And length=0 Then
                    _fbuf = t+_fbuf
                ElseIf pos+length = _fbuf.Length Then
                    _fbuf = t+_fbuf[..pos]
                ElseIf pos=0 Then
                    _fbuf = _fbuf[length..]+t
                Else
                    _fbuf = _fbuf[0..pos]+t+_fbuf[pos+length..]
                EndIf
            Else
                Local s$
                If pos = 0 And (length = -1 Or length=_fbuf.Length) Then
                    s = t
                Else
                    If length = -1 Then length = _fbuf.Length-pos
                    s = GetText( )
                    
                    If pos=_fbuf.Length Then
                        s :+ t
                    ElseIf pos=0 And length=0 Then
                        s = t+s
                    ElseIf pos+length = s.Length Then
                        s = s[..pos]+t
                    ElseIf pos=0 Then
                        s = t+s[length..]
                    Else
                        s = s[..pos]+t+s[pos+length..]
                    EndIf
                EndIf
                gad.SetText( s )
            EndIf
        EndIf
    End Method
    
    Method GetPos%( lines%=False )
        If _freed Then Return 0
        If Not _area Then Return 0
        Local f% = TEXTAREA_CHARS
        If lines>0 Then f = TEXTAREA_LINES
        Return gad.GetCursorPos( f )
    End Method
    
    Method Lock( )
        If _freed Then Return
        _lock :+ 1
        If _lock = 1 And _area Then
            gad.LockText( )
        ElseIf _lock = 1 Then
            _fbuf = GetText( )
        EndIf
    End Method
    
    Method Unlock( )
        If _freed Then Return
        Assert _lock>0, "Text box not locked"
        _lock :- 1
        If _lock = 0 And _area Then
            gad.UnlockText( )
        ElseIf _lock = 0 Then
            SetText( _fbuf )
        EndIf
    End Method
End Type

Type IToolbar Extends IGadget
    Method Create( parent:IGadget, x%, y%, w%, h%, source:Object )
        If _freed Then Return
        gad = CreateToolbar( source, x, y, w, h, Init(parent), 0 )
        OnInit( Null )
    End Method
    
    Method SetTips( tips$[] )
        For Local i:Int = 0 To tips.Length-1
            SetItem( i, "", 0, i, tips[i] )
        Next
    End Method
    
    Method SetItem( idx%, caption$, toggle%=0, icon%=-1, tip$="", extra:Object=Null )
        ModifyGadgetItem( gad, idx, caption, (toggle>0)*GADGETITEM_TOGGLE, icon, tip, extra )
    End Method
    
    Method AddItem( caption$, toggle%=0, icon%=-1, tip$="", extra:Object=Null )
        If _freed Then Return
        AddGadgetItem( gad, caption, (toggle>0)*GADGETITEM_TOGGLE, icon, tip, extra )
    End Method
    
    Method RemoveItem( idx% )
        If _freed Then Return
        RemoveGadgetItem( gad, idx )
    End Method
    
    Method GetItemCaption$( idx% )
        If _freed Then Return ""
        Return GadgetItemText( gad, idx )
    End Method
    
    Method GetItemExtra:Object( idx% )
        If _freed Then Return Null
        Return GadgetItemExtra( gad, idx )
    End Method
End Type

Type ITreeView Extends IGadget
    Field OnMenuHandler(sender:IGadget, e:TEvent)
    Field OnSelectHandler(sender:IGadget, e:TEvent)
    Field OnExpandHandler(sender:IGadget, e:TEvent)
    Field OnCollapseHandler(sender:IGadget, e:TEvent)
    Field root:ITreeNode
    
    Method Free( )
        If _freed = True Then Return
        For Local i:IGadget = EachIn root._children
            i.Free( )
        Next
        root._children.Clear( )
        root._children = Null
        root._parent = Null
        root.gad = Null
        root = Null
        OnMenuHandler = Null
        OnSelectHandler = Null
        OnExpandHandler = Null
        OnCollapseHandler = Null
        Super.Free( )
    End Method
    
    Method Create( parent:IGadget, x%, y%, w%, h% )
        If _freed Then Return
        gad = CreateTreeView( x,y,w,h,Init(parent),0 )
        root = New ITreeNode
        root._parent = Self
        root.gad = TreeViewRoot( gad )
        OnInit( Null )
    End Method
    
    Method AddNode:ITreeNode( text$, icon%=-1 )
        If _freed Then Return Null
        Local node:ITreeNode = New ITreeNode
        node.Create( root, text, icon )
        Return node
    End Method
    
    Method OnMenu( evt:TEvent )
        If OnMenuHandler Then OnMenuHandler(Self, evt)
    End Method
    
    Method OnSelect( evt:TEvent )
        If OnSelectHandler Then OnSelectHandler(Self, evt)
    End Method
    
    Method OnExpand( evt:TEvent )
        If OnExpandHandler Then OnExpandHandler(Self, evt)
    End Method
    
    Method OnCollapse( evt:TEvent )
        If OnCollapseHandler Then OnCollapseHandler(Self, evt)
    End Method
    
    Method Clear( )
        If _freed Then Return
        For Local i:IGadget = EachIn root._children
            i.Free( )
        Next
    End Method
    
    Method GetRoot:ITreeNode( )
        If _freed Then Return Null
        Return root
    End Method
    
    Method GetSelected:ITreeNode( )
        If _freed Then Return Null
        Local sel:TGadget = gad.SelectedNode( )
        If sel = Null Then Return Null
        Return root.FindByGad( sel )
    End Method
End Type

Type ITreeNode Extends IGadget
    Method Create( parent:IGadget, text$, icon%=-1 )
        If _freed Then Return
        gad = AddTreeViewNode( text, Init(parent), icon )
        OnInit( Null )
    End Method
    
    Method Clear( )
        If _freed Then Return
        For Local i:IGadget = EachIn _children
            i.Free( )
        Next
    End Method
    
    Method AddNode:ITreeNode( text$, icon%=-1 )
        If _freed Then Return Null
        Local node:ITreeNode = New ITreeNode
        node.Create( Self, text, icon )
        Return node
    End Method
    
    Method SelectNode( )
        If _freed Then Return
        gad.Activate( ACTIVATE_SELECT )
    End Method
    
    Method Expand( )
        If _freed Then Return
        gad.Activate( ACTIVATE_EXPAND )
    End Method
    
    Method Collapse( )
        If _freed Then Return
        gad.Activate( ACTIVATE_COLLAPSE )
    End Method
    
    Method FindByGad:ITreeNode( g:TGadget )
        If _freed Then Return Null
        If gad = g Then Return Self
        For Local i:ITreeNode = EachIn _children
            Local o:ITreeNode = i.FindByGad( g )
            If o Then Return o
        Next
        Return Null
    End Method
End Type
