; ID: 3241
; Author: Bobysait
; Date: 2016-01-09 23:41:01
; Title: Photoshop Like ColorPicker
; Description: using Maxgui proxy - generate event in realtime

'Module mdt.uisdk

Import maxgui.drivers

Global EVENT_COLOR:Int			=	AllocHookId			( );

Type TColorPicker Extends TProxygadget
	
	Const _cursorS:Int = 12;
	
	Field _sourceEvent:Object;
	
	Field _initColor:Int;
	Field _color:Int;
	Field _rgb:Int[];
	Field _hsl:Int[];
	
	Field style:Int;
	Field _win:TGadget;
	Field _core:TGadget;
	
	Field _pix_sl:TPixmap;
	Field _pix_hue:TPixmap;
	
	Field _col_sl:TGadget;
	Field _col_hue:TGadget;
	Field _col_col:TGadget;
	Field _col_cH:TGadget;
	Field _col_tH:TGadget;
	Field _col_cS:TGadget;
	Field _col_tS:TGadget;
	Field _col_cL:TGadget;
	Field _col_tL:TGadget;
	Field _col_cR:TGadget;
	Field _col_tR:TGadget;
	Field _col_cG:TGadget;
	Field _col_tG:TGadget;
	Field _col_cB:TGadget;
	Field _col_tB:TGadget;
	Field _col_tHex:TGadget;
	
	Field _cursorHue:TGadget;
	
	Field _ok:TGadget;
	Field _cc:TGadget;
	
	Field _hueHit:Byte;
	Field _slHit:Byte;
	
	Function RGB2HSL:Int[](rgb:Int[])
		Local i0:Int = 0, i1:Int = 1, i2:Int = 2, t:Int = 0;
		If rgb[i1]>rgb[i0] Then t = i0; i0 = i1; i1=t;
		If rgb[i2]>rgb[i0] Then t = i0; i0 = i2; i2=t;
		If rgb[i2]>rgb[i1] Then t = i1; i1 = i2; i2=t;
		i1 = (i0+1)Mod(3); i2 = (i1+1)Mod(3);
		Local c:Int = rgb[i0] - rgb[i2]; If c=0 Then Return [0,0, (100*rgb[i0])/255];
		Return [(60*(rgb[i1]-rgb[i2]))/c+i0*120, (100*c)/rgb[i0], (100*rgb[i0])/255];
	End Function
	
	Function HSL2RGB :Int [](hsl :Int [])
		Local h:Float = Float(hsl[0])/60.0;
		Local s:Float = Float(hsl[1])/100.0;
		Local l:Float = Float(hsl[2])/100.0;
		Local r:Int, g:Int, b:Int, t:Float = h Mod(1.0);
		If h<1
			r = 255;
			g = 255*t;
			b = 0;
		ElseIf h<2
			r = 255-255*t;
			g = 255;
			b = 0;
		ElseIf h<3
			r = 0;
			g = 255;
			b = 255*t;
		ElseIf h<4
			r = 0;
			g = 255-255*t;
			b = 255;
		ElseIf h<5
			r = 255*t;
			g = 0;
			b = 255;
		Else
			r = 255
			g = 0
			b = 255-255*t;
		End If
		Return [Int ( (255+(r-255)*s)*l), Int ( (255+(g-255)*s)*l),Int ( (255+(b-255)*s)*l)  ];
	End Function
	
	Function rgbFromRGB:Int[](rgb:Int)
		Return [rgb Shr(16) & $FF, rgb Shr(8) & $FF, rgb & $FF];
	End Function
	
	
	Function getInstance:TColorPicker ( initRGBColor:Int=$FFFFFF, source:Object = Null, Style:Int = 0 )
		
		Local cp:TColorPicker		=	New TColorPicker;
		cp._sourceEvent				=	source;
		
		cp._initColor				=	initRGBColor;
		cp._rgb						=	rgbFromRGB(initRGBColor);
		cp._hsl						=	rgb2hsl(cp._rgb);
		
		cp.style					=	style;
		
		cp._win						=	CreateWindow		( "Color Picker" ..
																		, 0,0..
																		, 384, 278..
																		, Null..
																		, WINDOW_TITLEBAR|WINDOW_CLIENTCOORDS|..
																		  WINDOW_CENTER|WINDOW_HIDDEN..
															);
										
		cp._pix_sl					=	CreatePixmap		( 256, 256, PF_RGB888 );ClearPixels(cp._pix_sl, initRGBColor );
		cp._pix_hue					=	CreatePixmap		( 1, 360, PF_RGB888 );ClearPixels(cp._pix_hue, initRGBColor );
										
			cp._core				=	CreatePanel			( 0,0, ClientWidth(cp._win), ClientHeight(cp._win), cp._win);
										
			cp._col_hue				=	CreatePanel			( 276,010, 020, 256, cp._core, PANEL_ACTIVE );
										
			cp._col_sl				=	CreatePanel			( 010,010, 256, 256, cp._core, PANEL_ACTIVE );
										
			cp._col_col				=	CreatePanel			( 306,010, 065, 035, cp._core, PANEL_SUNKEN | PANEL_ACTIVE );
			
			cp._col_cH				=	CreateLabel			( "H", 306, 055, 030, 020, cp._core );
			cp._col_tH				=	CreateTextField		( 339, 055, 032, 020, cp._core );
										SetButtonState		( cp._col_cH, True );
			cp._col_cS				=	CreateLabel			( "S", 306, 077, 030, 020, cp._core );
			cp._col_tS				=	CreateTextField		( 339, 077, 032, 020, cp._core );
			cp._col_cL				=	CreateLabel			( "L", 306, 099, 030, 020, cp._core );
			cp._col_tL				=	CreateTextField		( 339, 099, 032, 020, cp._core );
										
			cp._col_cR				=	CreateLabel			( "R", 306, 124, 030, 020, cp._core );
			cp._col_tR				=	CreateTextField		( 339, 124, 032, 020, cp._core );
			cp._col_cG				=	CreateLabel			( "G", 306, 146, 030, 020, cp._core );
			cp._col_tG				=	CreateTextField		( 339, 146, 032, 020, cp._core );
			cp._col_cB				=	CreateLabel			( "B", 306, 168, 030, 020, cp._core );
			cp._col_tB				=	CreateTextField		( 339, 168, 032, 020, cp._core );
										
										CreateLabel			( "#", 306, 190, 015, 020, cp._core );
			cp._col_tHex			=	CreateTextField		( 321, 190, 050, 020, cp._core );
										
			cp._ok					=	CreateButton		( "Ok", 306, 214, 065, 020, cp._core, BUTTON_OK );
			cp._cc					=	CreateButton		( "Cancel", 306, 236, 065, 020, cp._core, BUTTON_CANCEL );
										
			cp._cursorHue			=	CreatePanel			( 271,010, 030,001, cp._core );
										SetPanelColor		( cp._cursorHue, 1,1,1 );
			
		cp.SetProxy(cp._win);
		AddHook							( EmitEventHook, EventHook, cp );
		
		cp								.SetCurrentColor	( initRGBColor );
			
			cp							._updateHuePix		( );
			cp							._updateSLPix		( );
			cp							._updateUI			( );
			
			SetGadgetPixmap				( cp._col_hue , cp._pix_hue, PANELPIXMAP_STRETCH );
			SetGadgetPixmap				( cp._col_sl , cp._pix_sl );
		ShowGadget						( cp._win );
		RedrawGadget					( cp._win );
		
		Return cp;
		
	End Function
	
	Function clampI(i:Int var, m0:Int, m1:Int)
		i=max(min(i, m1), m0);
	End Function
	
	Method pickHueColor(r:Int Var, g:Int Var, b:Int Var )
		Local rgb:Int = Self._pix_hue.ReadPixel(0,359-Self._hsl[0]);
		r = rgb Shr(16) & $FF;
		g = rgb Shr(8) & $FF;
		b = rgb & $FF;
		Return;
	End Method
	
	' update ui gadgets
	Method _updateUI ( )
		' update color
		Self._color		=	(Self._rgb[0] Shl(16)) + (Self._rgb[1] Shl(8)) + Self._rgb[2];
		' textfields
		Self._col_tHex		.SetText			( Right(Hex(Self._color),6) );
		Self._col_tH		.SetText			( Self._hsl[0] );
		Self._col_tS		.SetText			( Self._hsl[1] );
		Self._col_tL		.SetText			( Self._hsl[2] );
		Self._col_tR		.SetText			( Self._rgb[0] );
		Self._col_tG		.SetText			( Self._rgb[1] );
		Self._col_tB		.SetText			( Self._rgb[2] );
		' hue cursor
		SetGadgetShape		( Self._cursorHue, 271, 10+255-Self._hsl[0]*255/359, 30, 1);
		' refresh color panel
		SetPanelColor		( Self._col_col, Self._rgb[0], Self._rgb[1], Self._rgb[2] );
		
		RedrawGadget		( Self._win );
		
		' emit the event for the main program to receive the modification in realtime.
		EmitEvent			( CreateEvent(EVENT_COLOR, Self._sourceEvent, Self._color) );
		
	End Method
	
	Method SetCurrentColor(RGB:Int)
		Self._rgb		=	rgbFromRGB			( RGB );
		Self._hsl		=	rgb2hsl				( Self._rgb );
							clampI				( Self._hsl[0], 0,359 );
							clampI				( Self._hsl[1], 0,99 );
							clampI				( Self._hsl[2], 0,99 );
		Self				._updateHuePix		( );
		Self				._updateSLPix		( );
		Self				._updateUI			( );
	End Method
	
	
	Method _updateHuePix();
		Local r:Int, g:Int, b:Int, u0:Int, u1:Int;
		Local du1:Int = 60;
		Local du2:Int = 60;
		
		Local x:Int = 0;
		
		u1 = 0;
		
		u0 = u1; u1 = u0+du1;
		' R(255) - B(0->255)
		For Local y0:Int = u0 Until u1
			r = 255; g = 0; b = 255*(y0-u0)/(u1-u0);
			Self._pix_hue.WritePixel(x,y0, r Shl(16) | g Shl(8) | b);
		Next;
		
		u0 = u1; u1 = u0+du2;
		' R(255->0) - B(255)
		For Local y1:Int = u0 Until u1
			r = 255-255*(y1-u0)/(u1-u0); g = 0; b = 255;
			Self._pix_hue.WritePixel(x,y1, r Shl(16) | g Shl(8) | b);
		Next;
		
		u0 = u1; u1 = u0+du1;
		' B(255) - G(0->255)
		For Local y2:Int = u0 Until u1
			r = 0; g = 255*(y2-u0)/(u1-u0); b = 255;
			Self._pix_hue.WritePixel(x,y2, r Shl(16) | g Shl(8) | b);
		Next
		
		u0 = u1; u1 = u0+du2;
		' B(255->0) - G(255)
		For Local y3:Int = u0 Until u1
			r = 0; g = 255; b = 255-255*(y3-u0)/(u1-u0);
			Self._pix_hue.WritePixel(x,y3, r Shl(16) | g Shl(8) | b);
		Next;
		
		u0 = u1; u1 = u0+du1;
		' G(255) - R(0->255)
		For Local y4:Int = u0 Until u1
			r = 255*(y4-u0)/(u1-u0); g = 255; b = 0;
			Self._pix_hue.WritePixel(x,y4, r Shl(16) | g Shl(8) | b);
		Next;
		
		u0 = u1; u1 = u0+du2;
		' G(255->0) - R(255)
		For Local y5:Int = u0 Until u1
			r = 255; g = 255-255*(y5-u0)/(u1-u0); b = 0;
			Self._pix_hue.WritePixel(x,y5, r Shl(16) | g Shl(8) | b);
		Next;
		
		SetGadgetPixmap	( Self._col_hue, Self._pix_hue, PANELPIXMAP_STRETCH );
	End Method
	
	Method _updateSLPix()
		
		Local r:Int=0, g:Int=0, b:Int=0;
		Self.pickHueColor(r,g,b);
		
		For Local y:Int = 0 To 255
			Local rnb:Float = 1.0-Float(y) / 255;
			For Local x:Int = 0 To 255
				Local whyte:Float = Float(x)/255;
				Local r_:Int = (255+(r-255)*whyte)*rnb;
				Local g_:Int = (255+(g-255)*whyte)*rnb;
				Local b_:Int = (255+(b-255)*whyte)*rnb;
				Self._pix_sl.WritePixel(x,y, r_ Shl(16) | g_ Shl(8) | b_);
			Next;
		Next;
		
		Self._updateSLCursor();
		
		SetGadgetPixmap		( Self._col_sl , Self._pix_sl );
		
	End Method
	
	Method _updateSLCursor()
		
		' draw the new cursor
		Local cr0:Int = Floor(Float(_cursorS)*.5);
		Local perimeter:Int = cr0 * 2 * Pi;
		Local cr1:Int = cr0-1;
		Local sat:Int = (255 * Self._hsl[1])/100;
		Local lit:Int = (255 * (100-Self._hsl[2]))/100;
		For Local p:Int = 0 To perimeter
			Local ang:Float = Float(p*360)/perimeter;
			Local x0:Int = sat + Cos(ang)*cr0;
			Local y0:Int = lit + Sin(ang)*cr0;
			Local x1:Int = sat + Cos(ang)*cr1;
			Local y1:Int = lit + Sin(ang)*cr1;
			If x0>=0 And x0<256
				If y0>=0 And y0<256 Then Self._pix_sl.WritePixel(x0,y0, $FF010101);
			EndIf;
			If x1>=0 And x1<256
				If y1>=0 And y1<256 Then Self._pix_sl.WritePixel(x1,y1, $FFFFFFFF);
			EndIf;
		Next;
		
		SetGadgetPixmap		( Self._col_sl , Self._pix_sl );
		
	End Method
	
	
	Method clearSLCursor()
		
		' overwrite old cursor
		Local r:Int=Self._rgb[0], g:Int=Self._rgb[1], b:Int=Self._rgb[2];
		Self.pickHueColor(r,g,b);
		Local sat:Int = 255*Float(Self._hsl[1])/99;
		Local lit:Int = 255*Float(99-Self._hsl[2])/99;
		For Local y:Int = -_cursorS-1 To _cursorS+1
			Local j:Int = lit+y;
			If j>=0 And j<256
				Local rnb:Float = 1.0-Float(j) / 255;
				For Local x:Int = -_cursorS-1 To _cursorS+1
					Local i:Int = sat+x;
					If i>=0 And i<256
						Local whyte:Float = Float(i)/255;
						Local r_:Int = (255+(r-255)*whyte)*rnb;
						Local g_:Int = (255+(g-255)*whyte)*rnb;
						Local b_:Int = (255+(b-255)*whyte)*rnb;
						Self._pix_sl.WritePixel(i,j, r_ Shl(16) | g_ Shl(8) | b_);
					End If;
				Next;
			End If;
		Next;
		
	End Method
	
	Method setHue ( h:Int )
		
		' clamp hue
		clampI(h, 0,359);
		
		' apply only if different from current pos
		If Self._hsl[0]<>h
			
			Self._hsl[0]	=	h;
			
			' update current color
			Self._rgb		=	hsl2rgb(Self._hsl);
			
			' update gadgets
			Self				._updateSLPix	( );
			Self				._updateUI		( );
			
		EndIf;
		
	End Method
	
	Method setSL ( s:Int, l:Int )
		
		clampI(s, 0,100);
		clampI(l, 0,100);
		
		If ( (s<>Self._hsl[1]) Or (l<> Self._hsl[2]) )
			Self				.clearSLCursor	( )
			Self._hsl[1]	=	s;
			Self._hsl[2]	=	l;
			Self._rgb		=	hsl2rgb(Self._hsl);
			Self				._updateSLCursor( );
			Self				._updateUI		( );
		EndIf;
		
	End Method
	
	Method setSat(s:Int)
		clampI(s, 0,100);
		If (Self._hsl[1]<>s)
			Self				.clearSLCursor	( );
			Self._hsl[1]	=	s;
			Self._rgb		=	HSL2RGB(Self._hsl);
			Self				._updateSLCursor( );
			Self._updateUI ();
		EndIf;
	End Method
	
	Method setLight(l:Int)
		clampI(l, 0, 100);
		If Self._hsl[2]<>l
			Self				.clearSLCursor	( );
			Self._hsl[2]	=	l;
			Self._rgb		=	HSL2RGB(Self._hsl);
			Self				._updateSLCursor( );
			Self._updateUI ();
		EndIf;
	End Method
	
	Method setRed(r:Int)
		clampI(r, 0, 255);
		If Self._rgb[0]<>r
			Self._rgb[0]	=	r;
			Self._hsl		=	rgb2hsl(Self._rgb);
			Self._updateUI ();
		EndIf;
	End Method
	
	Method setGreen(g:Int)
		clampI(g, 0, 255);
		If Self._rgb[1]<>g
			Self._rgb[1]	=	g;
			Self._hsl		=	rgb2hsl(Self._rgb);
			Self._updateUI();
		EndIf;
	End Method
	
	Method setBlue(b:Int)
		clampI(b, 0, 255);
		If Self._rgb[2]<>b
			Self._rgb[2]	=	b;
			Self._hsl		=	RGB2HSL(Self._rgb);
			Self._updateUI();
		EndIf;
	End Method
	
	
	Method ProcessEvent:Int(event:TEvent)
		Select event.id
			
			Case EVENT_WINDOWCLOSE
				
				If event.Source=Self
					FreeGadget Self;
					Return True;
				EndIf;
				
			Case EVENT_MOUSEMOVE
				
				Select event.source
					
					' mouse down on hue pixmap
					Case Self._col_hue
						
						If Self._hueHit Then Self.setHue(359-Float(359*event.y)/255);
						Self._slHit = False;
						Return True;
						
					' mouse down on saturation/brightness pixmap
					Case Self._col_sl
						
						If Self._slHit Then Self.setsl(100*Float(event.x)/255.0, 100-100*Float(event.y)/255.0);
						Self._hueHit = False;
						Return True;
						
					Default
						
						Self._slHit = False;
						Self._hueHit = False;
						Return False;
						
				End Select
				
			Case EVENT_MOUSEUP
				
				Self._slHit = False;
				Self._hueHit = False;
				Return False;
				
			Case EVENT_MOUSEDOWN
				
				Select event.source
					
					Case Self._col_hue
						
						Self.setHue(359-Float(359*event.y)/255);
						
						Self._slHit = False;
						Self._hueHit = True;
						Return True;
						
					Case Self._col_sl
						
						Self.setSL(100*Float(event.x)/255.0, 100-100*Float(event.y)/255.0);
						
						Self._slHit = True;
						Self._hueHit = False;
						Return True;
						
				End Select
				
			'Case EVENT_MOUSEENTER
				
			'Case EVENT_MOUSELEAVE
				
			Case EVENT_GADGETACTION
				
				Select event.Source
					
					Case Self._col_tHex
						
						Self.SetCurrentColor ( ("$"+Self._col_tHex.GetText()).ToInt() ); Return True;
						
					Case Self._col_tH
						
						Self.setHue(Self._col_tH.GetText().ToInt()); Return True;
						
					Case Self._col_tS
						
						Self.setSat(Self._col_tS.GetText().ToInt()); Return True;
					
					Case Self._col_tL
						
						Self.setLight(Self._col_tL.GetText().ToInt()); Return True;
						
					Case Self._col_tR
						
						Self.setRed(Self._col_tR.GetText().ToInt()); Return True;
					
					Case Self._col_tG
						
						Self.setGreen(Self._col_tG.GetText().ToInt()); Return True;
						
					Case Self._col_tB
						
						Self.setBlue(Self._col_tB.GetText().ToInt()); Return True;
						
					Case Self._ok
						
						FreeGadget Self;
						Return True;
						
					' cancel
					Case Self._cc
						
						' reset color to its initial value
						EmitEvent CreateEvent ( EVENT_COLOR, Self._sourceEvent, Self._initColor );
						FreeGadget Self;
						Return True;
						
				End Select;
				
		End Select;
		
		Return False;
		
	EndMethod
	
	
	
	Function EventHook:Object(id:Int,data:Object,context:Object)
		
		If TEvent(data)<>Null Then If TColorPicker(context)<>Null Then If TColorPicker(context).ProcessEvent(TEvent(data)) Then Return Null;
		Return data;
		
	EndFunction
	
	Method CleanUp()
		_sourceEvent=Null;
		_rgb =Null;
		_hsl=Null;
		_col_col = Null; _pix_sl = Null; _col_tHex = Null;
		_pix_hue = Null; _col_sl = Null; _col_hue = Null;
		_col_cH = Null; _col_tH = Null;
		_col_cS = Null; _col_tS = Null;
		_col_cL = Null; _col_tL = Null;
		_col_cR = Null; _col_tR = Null;
		_col_cG = Null; _col_tG = Null;
		_col_cB = Null; _col_tB = Null;
		_cursorHue=Null;
		_ok=Null;
		_cc=Null;
		If _core<>Null Then FreeGadget(_core); _core=Null;
		If _win<>Null Then FreeGadget(_win); _win=Null;
		RemoveHook(EmitEventHook, EventHook, Self);
		Super.CleanUp();
	End Method
	
	
	Function Demo()
		
		Local WIN:TGadget = CreateWindow("test", 0,0,600,300,Null, WINDOW_TITLEBAR|WINDOW_CENTER|WINDOW_RESIZABLE)
		Local button:TGadget = CreateButton("pick", 2,2,ClientWidth(WIN)-4,ClientHeight(WIN)-4,WIN)
		SetGadgetLayout(button, EDGE_ALIGNED, EDGE_ALIGNED, EDGE_ALIGNED, EDGE_ALIGNED);
		
		Local rgb:Int = $FF8000
		
		Repeat
			While PollEvent()<>Null
				Select EventID()
					Case EVENT_WINDOWCLOSE
						End
						
					Case EVENT_GADGETACTION
						If EventSource() = button Then pickColor(rgb)
						
					Case EVENT_COLOR
						rgb = EventData(); SetGadgetColor button, rgb Shr(16) & $ff, rgb Shr(8) & $ff, rgb & $ff, 1
						
				End Select
			Wend;
			
			Delay 30;
			
		Forever
		
	End Function

End Type

Function pickColor ( initColor:Int=$FFFFFF, source:Object=Null, Style:Int = 0 )
	TColorPicker.getInstance ( initColor, source, Style);
End Function



TColorPicker.Demo(); End;
