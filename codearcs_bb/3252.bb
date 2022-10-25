; ID: 3252
; Author: Bobysait
; Date: 2016-02-04 09:47:55
; Title: Foldable Panel Gadget
; Description: Create Collapsable Gadgets

SuperStrict

Import maxgui.drivers

Const COLLAPSEITEM_RIGHT:Int	=	8192

Type TCollapserItem Extends TProxyGadget
	
	Field core:TGadget;
		Field bt:TGadget;
		Field title:TGadget;
		Field realheight:Int;
	Field client:TGadget;
	
	Field _style:Int;
	Field title_height:Int;
	Field _pix:TPixmap[];
	Field _state:Byte;
	Field _on:Byte;
	
	'#region private (internal) stuff
	Method CleanUp :Int ()
		If Self.getCollapser()<>Null Then Self.getCollapser().RemoveGadget(Self);
		Return Self.proxy.CleanUp();
	End Method
	
	' update the pixmap icon of the top button
	Method updatePixmap()
		Self.bt		.SetPixmap		( Self._pix[Self._on*2+Self._state], PANELPIXMAP_STRETCH );
	End Method
	
	' when mouse enter the button
	Method onEnter()
		Self._on = 1;
		Self.updatePixmap	( );
	End Method
	
	' when mouse leave the button
	Method onLeave()
		Self._on = 0;
		Self.updatePixmap	( );
	End Method
	
	Method updateShape()
		If Self._state
			Self.core.SetShape(Self.xpos,Self.ypos,Self.width,Self.realheight);
			Self.client.SetShape(1,Self.title_height,Self.width-2,Self.realheight-Self.title_height-1);
			Self.client.SetShow(True);
		Else
			Self.core.SetShape(Self.xpos,Self.ypos,Self.width,Self.title_height);
			Self.client.SetShape(1,Self.title_height,Self.width-2,Self.realheight-Self.title_height-1);
			Self.client.SetShow(False);
		EndIf;
		If ( (Self._style & COLLAPSEITEM_RIGHT) > 0 )
			' title is left / button is right
			Self.title	.SetShape	( 1, 1, Self.width-Self.title_height-1, Self.title_height-2 );
			Self.bt		.SetShape	( Self.width-Self.title_height+1, 1, Self.title_height-2, Self.title_height-2 );
		Else
			' default : button is left
			Self.bt		.SetShape	( 1,1,Self.title_height-2,Self.title_height-2 );
			Self.title	.SetShape	( Self.title_height, 1, Self.width-Self.title_height-1, Self.title_height-2 );
		EndIf
	End Method
	'#end region
	
	'#region create Self
	Method Create:TCollapserItem ( pTitle:String, pX:Int, pY:Int, pW:Int,pH:Int, pCollapser:TCollapser, pStyle:Int=0)
		
		' only add items to a Collapser gadget !
			If pCollapser = Null Then Return Null;
			Self.title_height=	Int(Floor(pCollapser.title_height*0.5))*2+1;
			Self.realheight	=	pH;
			
			Self._state		=	True;
			Self._style		=	pStyle;
			Self.parent		=	pCollapser;
			Self._on		=	0;
			
		' generate pixmaps
			Local l_th:Int	=	Self.title_height;
			Local pix_h:Int	=	(l_th-2);
			Self._pix		=	[	CreatePixmap(pix_h,pix_h, PF_RGB888), ..
									CreatePixmap(pix_h,pix_h, PF_RGB888), ..
									CreatePixmap(pix_h,pix_h, PF_RGB888), ..
									CreatePixmap(pix_h,pix_h, PF_RGB888)];
									
				Self._pix[0]		.ClearPixels($808080);
				Self._pix[1]		.ClearPixels($808080);
				Self._pix[2]		.ClearPixels($A0A0A0);
				Self._pix[3]		.ClearPixels($A0A0A0);
				
				Local pix_x:Int;
				Local pix_m:Int = Floor(pix_h*0.5);
				For pix_x = 3 Until pix_h-3
					Self._pix[0].WritePixel(pix_x, pix_m, $010101);
					Self._pix[0].WritePixel(pix_m, pix_x, $010101);
					Self._pix[1].WritePixel(pix_x, pix_m, $010101);
					Self._pix[2].WritePixel(pix_x, pix_m, $010101);
					Self._pix[2].WritePixel(pix_m, pix_x, $010101);
					Self._pix[3].WritePixel(pix_x, pix_m, $010101);
				Next
				
		' title panel
			Self.core	=	CreatePanel		( pX,pY,pW,ph, pCollapser.getClient(), pStyle );
				Self.core	.SetColor		( $01,$01,$01 );
				Self.core	.SetLayout		( EDGE_ALIGNED, EDGE_ALIGNED, EDGE_ALIGNED, 0 );
				
		' button + title
			If ( (pStyle & COLLAPSEITEM_RIGHT) > 0 )
				' title is left / button is right
				Self.title=	CreateLabel		( " "+pTitle+" ", 1, 1, pw-l_th-1, l_th-2, Self.core, pStyle );
				Self.bt	=	CreatePanel		( pw-l_th+1, 1, l_th-2, l_th-2, Self.core, PANEL_ACTIVE );
				Self.bt		.SetLayout		( 0, EDGE_ALIGNED, EDGE_ALIGNED, EDGE_ALIGNED );
			Else
				' default : button is left
				Self.bt	=	CreatePanel		( 1,1,l_th-2,l_th-2, Self.core, PANEL_ACTIVE );
				Self.bt		.SetLayout		( EDGE_ALIGNED, 0, EDGE_ALIGNED, EDGE_ALIGNED );
				Self.title=	CreateLabel		( " "+pTitle+" ", l_th, 1, pw-l_th-1, l_th-2, Self.core, pStyle );
			EndIf
				Self.bt		.SetColor		( $80,$80,$80 );
				Self.title	.SetColor		( $60,$60,$60 );
				Self.title	.SetTextColor	( $FF,$80,$20 );
				Self.title	.SetLayout		( EDGE_ALIGNED, EDGE_ALIGNED, EDGE_ALIGNED, EDGE_ALIGNED );
				
				Self		.updatePixmap	( );
				Self.bt.extra = Self;
				
		' client panel
			Self.client	=	CreatePanel		( 1, l_th, pw-2, ph-l_th-1, Self.core );
				Self.client	.SetLayout		( EDGE_ALIGNED, EDGE_ALIGNED, EDGE_ALIGNED, EDGE_ALIGNED );
				Self.client	.SetColor		( $40,$40,$40 );
				
		' set proxy
			Self.proxy	=	Self.core;
			
		' Update layout (by Default : stick Left And Right)
			' we align on top, not bottom, but whatever we set, top and bottom will be forced after collapser update
			' ( and collapser update is called by addItem, so it's updated immediatly after setlayout. )
			Self.SetLayout(EDGE_ALIGNED,EDGE_ALIGNED,EDGE_ALIGNED,0);
			
			pCollapser.addItem(Self);
			
		Return Self;
		
	End Method
	'#end region
	
	'#region text
	' text is sent to the title gadget
	Method SetText(text:String)
		Self.title.SetText(text);
	End Method
	
	Method GetText:String()
		Return Self.title.GetText();
	End Method
	'#end region
	
	'#region fold/unfold
	' returns true if gadget is expanded else returns false.
	Method State:Int()
		Return Self._state;
	End Method
	
	' alternates the collapsed/expanded state relatives to the previous state
	Method SwitchState()
		Self.setState(Not(Self._state));
	End Method
	
	' collapse (0) or expand (1) the gadget
	Method SetState(bool:Byte)
		If bool
			Self.Expand();
		Else
			Self.Collapse();
		EndIf
	End Method
	
	' collapse the gadget
	Method Collapse()
		If ( Self._state = False )	Then Return;
		Self._state				=	0;
		Self						.updatePixmap	( )
		Self						.updateShape	( );
		Self.getCollapser()			.Update			( );
		EmitEvent					( CreateEvent	( EVENT_GADGETACTION, Self, 0 ) );
	End Method
	
	' expand the gadget
	Method Expand()
		If ( Self._state = True )	Then Return;
		Self._state				=	1;
		Self						.updatePixmap	( );
		Self						.updateShape	( );
		Self.getCollapser()			.Update			( );
		EmitEvent					( CreateEvent	( EVENT_GADGETACTION, Self, 1 ) );
	End Method
	
	' fold the gadget (alias for collapse)
	Method fold()
		Self.Collapse();
	End Method
	
	' unfold the gadget (alias for expand)
	Method unfold()
		Self.Expand();
	End Method
	'#end region
	
	'#region shape
	' returns the real Height the item is supposed To be (Not the collapsed Height)
	Method GetHeight:Int()
		Return Self.realheight;
	End Method
	
	' the client gadget.
	Method getClient:TGadget()
		Return Self.client;
	End Method
	
	' client size
	Method ClientWidth:Int()
		Return Self.client.ClientWidth()
	End Method
	
	Method ClientHeight:Int()
		Return Self.client.ClientHeight()
	End Method
	
	Method SetShape(pX:Int,pY:Int,pW:Int,pH:Int)
		' copy shape pos and size
		Self.xpos = px;
		Self.ypos = py;
		Self.width = pw;
		Self.Height = ph;
		Self.realheight = ph; ' get real size of the gadget (whatever it is collapsed or not)
		
		' the core gadget is updated after this
		Self.updateShape();
	End Method
	
	Method SetLayout( lft:Int,rht:Int,top:Int,bot:Int )
		Self.proxy.SetLayout(lft,rht,top,bot);
		' copy core layout to self
		Self.lockl = Self.proxy.lockl;
		Self.lockr = Self.proxy.lockr;
		Self.lockt = Self.proxy.lockt;
		Self.lockb = Self.proxy.lockb;
		Self.lockx = Self.proxy.lockx;
		Self.locky = Self.proxy.locky;
		Self.lockw = Self.proxy.lockw;
		Self.lockh = Self.proxy.lockh;
		Self.lockcw = Self.proxy.lockcw;
		Self.lockch = Self.proxy.lockch;
	End Method
	'#end region
	
	
	' the Collapser object that owns this item
	Method getCollapser:TCollapser()
		Return TCollapser(Self.parent);
	End Method
	
	Function gadgetCollapserItem:TCollapserItem(gad:TGadget)
		If gad=Null Then Return Null;
		If gad.GetGroup() = Null Then Return Null; ' button.parent = core
		Return TCollapserItem(gad.extra);
	End Function
	
End Type


Const COLLAPSER_AUTORESIZE:Int	=	512;	' set collapser client height to match kids
Const COLLAPSER_AUTOSCROLL:Int	=	1024;	' add scrollbar if required

Type TCollapser Extends TProxyGadget
	
	Global collapsedTime:Int;
	
	Field title_height:Int;
	
	Field client:TGadget;
	
	' TODO > add sliders to scroll client gadgets
	'	Field scrollbarX:TGadget;
	'	Field scrollbarY:TGadget;
	
	Field paddingy:Int;
	
	Field items:TList;
	
	Method Create:TCollapser(pX:Int, pY:Int, pW:Int, pH:Int, pGroup:TGadget, pStyle:Int)
		
		collapsedTime = MilliSecs()-101;
		
		Self.items		=	New TList;
		
		Self.title_height=	17;
		Self.paddingy	=	5;
		
		Self.SetProxy		( CreatePanel(pX,pY,pW,pH,pGroup,pStyle) );
		
		' not implemented yet
		rem
		Self.scrollbarY	=	CreateSlider	( pw-14,0,14,ph, Self.proxy, SLIDER_VERTICAL );
		Self.scrollbarY		.SetLayout		( 0,EDGE_ALIGNED, EDGE_ALIGNED,EDGE_ALIGNED );
		Self.scrollbarY		.SetShow		( False );
		
		Self.scrollbarX	=	CreateSlider	( 0,ph-14,pw-14,14, Self.proxy, SLIDER_VERTICAL );
		Self.scrollbarX		.SetLayout		( EDGE_ALIGNED,EDGE_ALIGNED, 0,EDGE_ALIGNED );
		Self.scrollbarX		.SetShow		( False );
		endrem
		
		Self.client		=	CreatePanel		( 0,0,pw,ph, Self.proxy );
		Self.client			.SetLayout(EDGE_ALIGNED,EDGE_ALIGNED, EDGE_ALIGNED,EDGE_ALIGNED);
		
		AddHook				( EmitEventHook, EventHook, Self );
		
		Return Self;
		
	End Method
	
	Method Rethink()
		Self.Update();
		Super.Rethink();
	End Method
	
	Function EventHook:Object(id:Int, data:Object, context:Object)
		If TEvent(data) = Null Then Return data;
		Local ev:TEvent = TEvent(data);
		
		Local col:TCollapser
		Local itm:TCollapserItem;
		
		Select ev.id
			
			Case EVENT_WINDOWSIZE
				
				col = TCollapser(context); If col<>Null Then col.Rethink();
				Return data;
				
			Case EVENT_MOUSEENTER
				
				If TGadget(ev.source)<>Null
					itm = TCollapserItem.gadgetCollapserItem(TGadget(ev.source));
					If (itm<>Null) Then If itm.getCollapser()=context Then itm.onEnter(); Return data;
				EndIf;
				Return data;
				
			Case EVENT_MOUSELEAVE
				
				If TGadget(ev.source)<>Null
					itm = TCollapserItem.gadgetCollapserItem(TGadget(ev.source));
					If (itm<>Null) Then If itm.getCollapser()=context Then itm.onLeave(); Return data;
				EndIf;
				Return data;
				
			Case EVENT_MOUSEUP
				If (ev.data = 1)
					If TGadget(ev.source)<>Null
						itm = TCollapserItem.gadgetCollapserItem(TGadget(ev.source));
						If (itm<>Null)
							If (itm.getCollapser()=context)
								If (ev.x>=0 And ev.x<itm.bt.GetWidth())
									If (ev.y>=0 And ev.y<itm.bt.GetHeight())
										If Abs(MilliSecs()-TCollapser.collapsedTime)>100
											itm.SwitchState();
											TCollapser.collapsedTime = MilliSecs();
										EndIf;
									EndIf;
								EndIf;
								Return Null;
							EndIf;
						EndIf;
					EndIf;
				EndIf;
		End Select;
		Return data;
	End Function
	
	Method CleanUp :Int ()
		Self.proxy.CleanUp();
		RemoveHook(EmitEventHook, EventHook, Null);
		Return Super.CleanUp();
	End Method
	
	Method AddItem(item:TGadget)
		' Always layout stuff with <ALIGN TOP> and <NOT ALIGNED BOTTOM> !
		item.SetLayout(item.lockl,item.lockr, EDGE_ALIGNED,0);
		Self.items.AddLast(item);
		Self.Update();
	End Method
	
	Method RemoveItem(index:Int)
		If index<0 Or index>=Self.items.Count() Then Return;
		Self.items.Remove(Self.items.ValueAtIndex(index));
		Self.Update();
	End Method
	
	Method RemoveGadget:Int(gad:TGadget)
		Self.items.Remove(gad);
		Self.Update();
	End Method
	
	Method GetItem:TGadget(index:Int)
		Return TGadget(Self.items.ValueAtIndex(index));
	End Method
	
	Method GetClient:TGadget()
		Return Self.client;
	End Method
	
	Method ClientWidth:Int()
		Return Self.client.ClientWidth();
	End Method
	
	Method ClientHeight:Int()
		Return Self.client.ClientHeight();
	End Method
	
	Method Update()
		
		' resize/repos clients
		Self.items.Sort(True, compareGadgetY)
		Local item:TGadget;
		Local posy:Int = Self.paddingy;
		Local gadh:Int = 0;
		
		For item = EachIn Self.items
			If ( TCollapserItem(item)<>Null )
				If TCollapserItem(item)._state
					gadh = TCollapserItem(item).GetHeight();
				Else
					gadh = TCollapserItem(item).title_height;
				EndIf;
			Else
				gadh = item.GetHeight();
			EndIf;
			item.SetShape ( item.GetXPos(), posy, item.GetWidth(), item.GetHeight() );
			posy :+ gadh + Self.paddingy;
		Next;
		
		gadh :+ posy;
		Self.client.SetShape(0,0,Self.client.width, gadh);
		
	End Method
	
	Function compareGadgetY:Int(o1:Object, o2:Object)
		Return -1+2*(TGadget(o1).GetYPos()>TGadget(o2).GetYPos());
	End Function
	
End Type


Function CreateCollapser:TCollapser ( pX:Int, pY:Int, pW:Int,pH:Int, pGroup:TGadget, pStyle:Int=0 )
	Return New TCollapser.Create(pX,pY,pW,pH,pGroup,pStyle);
End Function
Function CreateCollapsable:TGadget ( pTitle:String, pX:Int, pY:Int, pW:Int, pH:Int, pCollapser:TGadget, pStyle:Int=0 )
	If TCollapser(pCollapser)=Null Then Return Null;
	Return New TCollapserItem.Create(pTitle, pX,pY,pW,pH, TCollapser(pCollapser), pStyle);
End Function
Function CollapserItem:TGadget(pCollapser:TGadget, index:Int)
	If TCollapser(pCollapser) = Null Then Return Null;
	Return TCollapser(pCollapser).GetItem(index);
End Function

Function CollapserAddItem(pCollapser:TGadget, item:TGadget)
	If TCollapser(pCollapser) = Null Then Return;
	TCollapser(pCollapser).AddItem(item);
End Function








Function TCollapserDemo()
	
	Local Win:TGadget = CreateWindow("window", 10,10,800,600,, WINDOW_TITLEBAR | WINDOW_CLIENTCOORDS | WINDOW_CENTER | WINDOW_RESIZABLE | WINDOW_STATUS );
	SetMinWindowSize(Win, 640,400)
	
	Local body:TGadget[2]
	Local a:Int;
	Local panI:TGadget[2];
	Local killMe:TGadget[2];
	For a = 0 To 1
		
		body[a] = CreatePanel(a*ClientWidth(Win)/2+5,5,ClientWidth(Win)/2-10, ClientHeight(Win)-10,Win)
		body[a].SetColor($90,$90,$90);
		If a=0
			body[a].SetLayout(EDGE_ALIGNED,EDGE_RELATIVE,EDGE_ALIGNED,EDGE_ALIGNED);
		Else
			body[a].SetLayout(EDGE_RELATIVE,EDGE_ALIGNED,EDGE_ALIGNED,EDGE_ALIGNED);
		EndIf;
		
		' create a collapser
			Local col:TCollapser = CreateCollapser( 5,5, ClientWidth(body[a])-10, ClientHeight(body[a])-10, body[a] );
			col.SetColor($20,$20,$20);
			col.SetLayout(EDGE_ALIGNED,EDGE_ALIGNED,EDGE_ALIGNED,EDGE_ALIGNED);
			
		' create collapsable region in the collapser
			Print "<Collapsables>"
			CreateCollapsable	( "Buttons"				, 005,005,ClientWidth(col)-10, 100, col );
			CreateCollapsable	( "Radio Buttons"		, 005,110,ClientWidth(col)-10, 100, col );
			CreateCollapsable	( "it's really tight"	, 005,215,ClientWidth(col)-10, 200, col );
			Print "</Collapsables>"
			
		' get collapsables from collapser
			Local pan0:TGadget = TCollapserItem(col.getitem(0)).getClient(); ' > Add stuff to the client !
			Local pan1:TGadget = TCollapserItem(col.getitem(1)).getClient();
			Local pan2:TGadget = TCollapserItem(col.getitem(2)).getClient();
			Local colI:TCollapser = CreateCollapser( 5,5,ClientWidth(pan2)-10, ClientHeight(pan2)-10, pan2 );
				colI.SetColor($30,$30,$30);
				colI.SetLayout(EDGE_ALIGNED,EDGE_ALIGNED,EDGE_ALIGNED,EDGE_ALIGNED);
				' you can use "Y" position at the creation to specify order of gadgets.
				' gadgets, when collapser updates, are sorted by their "Y" position.
				' for example, here the "btKill" is created on top of the two regions.
			CreateCollapsable("let me get out i'm locked :'(", 5, 1,ClientWidth(colI)-10, 100, colI);
			CreateCollapsable("speak for you bro ...", 5, 2,ClientWidth(colI)-10, 100, colI);
			Local btKill:TGadget = CreateButton("Kill Me Please", 5, 0, 100,20, colI.GetClient() );
				btKill.SetLayout(EDGE_ALIGNED,0,EDGE_ALIGNED,0);
			ColI.additem ( btKill );
			
			panI[a] = colI;
			
			' as mentioned, items are sorted, so, the collapsable regions are at index 1 and 2
			Local panI0:TGadget = TCollapserItem(colI.getitem(1)).getClient();
			Local panI1:TGadget = TCollapserItem(colI.getitem(2)).getClient();
			' and the button is at index 0
			killMe[a] = colI.getitem(0);
			
		' fill regions with some buttons
			
			Local i:Int, j:Int;
			For j = 0 To 2
				For i = 0 To 2
					Local b:TGadget = CreateButton("button "+i+"-"+j,5+85*i,5+25*j,80,20,pan0, BUTTON_OK);
					b.SetLayout(1,0,1,0);
				Next
			Next
			For j = 0 To 2
				For i = 0 To 2
					Local b:TGadget = CreateButton("radio "+i+"-"+j,5+85*i,5+25*j,80,20,pan1, BUTTON_RADIO);
					b.SetLayout(1,0,1,0);
					b.SetColor($50,$50,$50);
					b.SetTextColor($FF,$70,$00);
				Next
			Next
			For j = 0 To 2
				For i = 0 To 2
					Local b:TGadget = CreateButton("check "+i+"-"+j,5+85*i,5+25*j,80,20,panI0, BUTTON_CHECKBOX);
					b.SetLayout(1,0,1,0);
					b.SetColor($50,$50,$50);
					b.SetTextColor($FF,$70,$00);
				Next
			Next
			For j = 0 To 2
				For i = 0 To 2
					Local b:TGadget = CreateLabel("label "+i+"-"+j,5+85*i,5+25*j,80,20,panI1, LABEL_CENTER);
					b.SetLayout(1,0,1,0);
					b.SetColor($50,$50,$50);
					b.SetTextColor($FF,$70,$00);
				Next
			Next
			
		' add a button in the collapser (outside collapsable regions)
		Local button:TGadget = CreateButton("click", 5,320, 90, 20, col.getClient() );
				button.SetLayout(EDGE_ALIGNED,0,EDGE_ALIGNED,0);
				
		col.AddItem(button);
	Next;
	
	Repeat
		
		WaitEvent()
		Select EventID()
			Case EVENT_WINDOWCLOSE
				Exit;
			Case EVENT_GADGETACTION
				SetStatusText (Win, "Hit a button '"+TGadget(EventSource()).GetText()+"' state='"+TGadget(EventSource()).State()+"'")
				For a = 0 To 1
					If killMe[a]<>Null
						If EventSource()=killMe[a]
							FreeGadget(panI[a])
							killMe[a] = Null;
							panI[a] = Null;
						EndIf;
					EndIf;
				Next;
			'	If EventSource() = panI1
			'		
			'	EndIf;
		End Select
		
	Forever
	
	End
	
End Function



TCollapserDemo()
