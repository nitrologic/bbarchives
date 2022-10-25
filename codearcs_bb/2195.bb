; ID: 2195
; Author: Nebula
; Date: 2008-01-20 12:31:47
; Title: Text Editor (b+)
; Description: Edit text

;
;
;
;
;


Type bluekeywords
	Field shortkw$
	Field longkw$
End Type

Global ww = 800
Global wh = 600

; active line colors
Global bluevisualsactive = True
Global blueactiveliner = 20
Global blueactivelineg = 100
Global blueactivelineb = 180
Global blueactivelinefr = 240
Global blueactivelinefg = 240
Global blueactivelinefb = 240
Global blueactivelinesr = 190
Global blueactivelinesg = 130
Global blueactivelinesb = 90
; txt area background color
Global bluebackcolorr = 0
Global bluebackcolorg = 76
Global bluebackcolorb =  150
; txt area main color
Global bluetextcolorr  = 240
Global bluetextcolorg = 240
Global bluetextcolorb = 240
; line numbering colors
Global bluelinenumberbr = 0 ; line numbering background colors
Global bluelinenumberbg =60
Global bluelinenumberbb = 120
; line numbers
Global bluelinenumbertr = 180
Global bluelinenumbertg = 180
Global bluelinenumbertb = 180

;Global editor interactive controllers
Global bluepagedownscroll
Global bluepageupscroll
Global bluectrlpagedownscroll
Global bluectrlpageupscroll

Global linecounter = 0

Const bluedefaulttabsize = 4

Global blueinsertmode = False
Global bluelinenumberwidth = 32
Global bluelinenumbervisible = True				; Line numbers Disable Enable

Global bluelinenumberimagebuffer = CreateImage(bluelinenumberwidth,wh) 
Global bluelinenumberupdate = True ; false means do not redraw the line number bar

Global bluecursorcolorr = 255
Global bluecursorcolorg = 255
Global bluecursorcolorb = 255
Global blueinsertcursorcolorr = 255
Global blueinsertcursorcolorg = 255
Global blueinsertcursorcolorb = 255

Global bluelinecopybuffer$ =""

;line edit return commands
Const bluecloseapp = -99
Const bluepageup = -100
Const bluepagedown = -101
Const bluectrlend = -102
Const bluectrlhome = -103

Global bluenumlines = 140 ; max num of lines
Const bluepointers = 64
Dim blue$(bluenumlines,bluepointers)
Const blueactive = 0
Const bluec = 1
Const bluecursortimer = 2
Const bluecursortimerdelay = 3
Const blueshowcursor = 4
Const bluecursorpos = 5
Const blueselstart = 6
Const blueselend = 7
Const blueselactive = 8
Const blueinvselstart = 9
Const blueinvselactive = 10
Const blueshiftactive = 11
Const bluectrlactive = 12
Const bluecopybuffer = 13

Const bluec_back = 14
Const bluec_front = 15

Const bluehastab = 16
Const bluehascolor = 17
Const bluehasunderline = 18
Const bluehasbold = 19
Const bluehasitalic = 20
Const bluehasicon = 21
Const bluehasmultiplefonts = 22
Const bluetabmodifier = 23
Const blueabsfontheight  = 24

;Cursor mouse interaction
Global bluecursorx = 0a
Global bluecursory = 0
Global bluecursorupdate = False


;buffering
Global bluelinenumimagebuffer = CreateImage(bluelinenumberwidth,32)
bufferlinenumbers ; make one image buffer 
; Ask for mode

;If FileType("back.bmp") <> 1 Then RuntimeError "cannot find back.bmp"

;Global backimage = LoadImage("back.bmp")
Global backimage = CreateImage(320,200)
MaskImage backimage,0,0,0

Const blue_tab = 9

Global mx
Global my

Global bluelinewidth = 72

; Blue Core gadgets
Global lineareawin 
Global lineareacan
Global backcan

root$ = CurrentDir()
file$  = root$ + "welcome.txt"
;If FileType(file$) = 1 Then blueloadtext(root$+"welcome.txt")
If FileType(file$) = 1 Then blueloadtext(root$+"keywords.txt")
If FileType(CommandLine()) = 1 Then blueloadtext(CommandLine())


;loadkeywords

blue_ed(ww,wh,Desktop())


End


Function blue_ed(w,h,parent) ; main edit loop
	cx = GadgetWidth(parent)/2-w/2
	cy = GadgetHeight(parent)/2-h/2
	Local win = CreateWindow("Blue - Beta version march2004 - Crom Design",cx,cy,w,h,parent,1)	
	Local can = CreateCanvas(0,0,w,h,win)
	lineareawin = CreateWindow("",0,0,w,32,win,32)
	backcan = CreateCanvas(0,0,w,32,lineareawin) : SetBuffer CanvasBuffer(backcan) : ClsColor blueactiveliner,blueactivelineg,blueactivelineb:Cls

	Local canvasoffsety =  ClientHeight(can)-ClientHeight(win)-2 ; Fix for XP and Standard Windows interface
	;RuntimeError canvasoffsety
	Local we ;event
	Local prevline = 0 ; make global
	Local scrolldown = False
	Local scrollup = False
	Local cursorontop = False
	Local cursoronbottom = False
	SetBuffer CanvasBuffer(can)
	
	ClsColor bluebackcolorr,bluebackcolorg,bluebackcolorb:Cls
	
	tm = CreateTimer(30)
	
	SetBuffer CanvasBuffer(can)
	Cls
	Color 240,240,240
	DrawImage backimage,GadgetWidth(can)/2-ImageWidth(backimage)/2,0
	drawblue(can,linecounter)
	FlipCanvas(can)
	
	ma = 0
	fh = FontHeight()
	While we<>$803
		we = WaitEvent()
		Select we
			Case $101 	;- Key down	
			Case $102 	;- Key up
			If EventData() = 1 Then Exit
			Case $103 	;- Key stroke	
			Case $201 	;- Mouse down	
			Case $202 	;- Mouse up	
			;ma = (EventY()/fh)+linecounter
			Case $203 	;- Mouse move	
			mx = EventX():my = EventY()
			Case $204 	;- Mouse wheel 
			Case $205 	;- Mouse enter 
			Case $206 	;- Mouse leave 
			Case $401 	;- Gadget action	
			Case $801 	;- Window move
				;SetGadgetShape win,GadgetX(parent)+20,GadgetY(parent+22),GadgetWidth(win),GadgetHeight(win)
			Case $802 	;- Window size 
			Case $803 	;- Window close 
			Case $804 	;- Window activate		
			Case $1001 	;- Menu event 
			Case $2001 	;- App suspend	
			Case $2002 	;- App resume	
			Case $2003 	;- App Display Change	
			Case $2004 ;- App Begin Modal 
			Case $2005 ;- App End Modal 
			Case $4001	;- Timer tick
			
			; Editor active
			If ma > -1 Then
				prevline = ma
				DebugLog (ma-linecounter)*fh
				ma = blueline(win,blue(ma,bluec),ma,(ma-linecounter)*fh+canvasoffsety)		
				SetBuffer CanvasBuffer(can)		
				;SetGadgetText win,"Maxlines : " + bluenumlines + " ma : " + ma + " prevline : " + prevline + " linecounter : " + linecounter
				; Line feedback handler
				Select ma ; trap/translate global commands
					Case bluecloseapp
						End
					Case bluepageup						
						st = prevline - linecounter
						tp = bluemaxvislines(can)-1						
						If prevline - tp > 0 Then														
							If st = tp Then ; bottom up
									ma = linecounter									
							Else ; top up							
								If linecounter - tp-1 > 0 Then								
									linecounter = linecounter - (tp-1)
									If st = 0 Then ; if on top of page			
										ma = linecounter-1
									Else ; if in the middle
										ma = linecounter 
									End If
								End If
							End If
						Else	; if near top then set to top					
							ma = 0 : linecounter = 0
						End If
					Case bluepagedown					
						;	ma = linecounter
						sval = 10;bluectrlpagedownscroll
						st = prevline - linecounter
						tp = bluemaxvislines(can)-1
						
						If prevline + tp < bluenumlines-tp Then														
							If st = tp Then ; bottom down
								If bluecontrolispressed() Then ; if control
									ma = prevline + sval + 1									
									linecounter = linecounter + sval+1
								Else
									ma = prevline + tp + 1									
									linecounter = prevline - 1
								EndIf
							Else ; top down							
								If linecounter + tp-1 < bluenumlines Then								
									
									If st = 0 Then ; if on bottom of page

										If bluecontrolispressed() Then ; if control
											ma = prevline + sval											
										Else										
											ma = tp + st
											If linecounter > 0 Then linecounter = linecounter + st : ma = linecounter + tp
										End If
										
									Else ; if in the middle
										If bluecontrolispressed() Then ; if control is pressed
											;linecounter = prevline
											ma = prevline + sval;tp
										Else ; if not										
											
											linecounter = prevline
											ma = prevline + tp
										End If
									End If
								End If
							End If
						Else	; if near bottom then set bottom to top							
							ma = bluenumlines : linecounter = bluenumlines-tp							
						End If
					Case bluectrlhome
						ma = 0
						linecounter = 0
						bluelinenumberupdate = True
					Case bluectrlend			
						ma = bluenumlines
						linecounter = bluenumlines+1 - bluemaxvislines(can)
						;DebugLog ma
						;DebugLog linecounter
						bluelinenumberupdate = True
				End Select
		
				; Bounding
				If ma>bluenumlines Then ma = bluenumlines
		
				; Text single line scrolling
				If ma>-1 Then
					If ma-linecounter => bluemaxvislines(can) And ma>prevline And ma< bluenumlines+1 Then ; down			
						cursoronbottom = True
						ma = bluemaxvislines(can) + linecounter
						linecounter = linecounter + 1
						scrolldown= True : scrollup = False
					End If
					If ma < prevline And linecounter > 0 And cursorontop = True Then ; up			
						linecounter = linecounter - 1
						If linecounter < 0 Then linecounter = 0
						scrollup = True : scrolldown = False			
					End If
					If scrolldown = True Then bluelinenumberupdate = True
					If scrollup = True Then bluelinenumberupdate = True
					If ma = -1 And scrolldown = True Then ma = bluenumlines : scrolldown = False
					If ma =< linecounter Then cursorontop = True Else cursorontop = False
					If ma-linecounter => bluemaxvislines(can) Then cursoronbottom = True Else cursoronbottom = False
				End If
				If ma = -1 Then ma = prevline
				SetBuffer CanvasBuffer(can)
				Cls
				; Background image
				DrawImage backimage,GadgetWidth(can)/2-ImageWidth(backimage)/2,0
				; Draw the text
				drawblue(can,linecounter)
				FlipCanvas(can)
				
			End If	
		End Select		
	Wend
End Function

Function bluemaxvislines(can) ; return max vis lines on screen
	a =  ((GadgetHeight(can)-13) / FontHeight()-3)
	If a > bluenumlines Then a = bluenumlines+1	
	Return a
End Function
Function linewidth(num) ; core
l = Len(blue(num,bluec))

End Function
Function drawblue(can,start) ; draw the text
;	ms = MilliSecs()
	fh = FontHeight()
	ch = bluemaxvislines(can)
	
	cnt = start
	y = 0
	;set default color
	Color bluetextcolorr,bluetextcolorg,bluetextcolorb
	While y < ch
		If cnt=<bluenumlines
			bluedrawtext(x,y*fh,cnt,can)
			;			
			;
			;		Rect x,y*fh,x+32,y+fh ; line numbering
			;		If blue(cnt,bluehastab) Then
			;			bluetexttab x,y*fh,blue(cnt,bluec)
			;		Else
			;			Text x,y*fh,blue(cnt,bluec)
			;		End If
		End If
		cnt=cnt+1
		y=y+1
	Wend
	If bluelinenumbervisible = True Then DrawBlock bluelinenumberimagebuffer,x,0
	bluelinenumberupdate = False
;RuntimeError MilliSecs()-ms	
End Function

Function bluedrawtext(x,y,num,can = 0, norules = False)
	Local hastab

	If norules = False Then
		If bluelinenumbervisible = True Then mod1 = blueLinenumberwidth
	End If
	
	If blue(num,bluehastab) = True Then hastab = True

	If can > 0 Then		
		drawlinenumbering(x,y,num,can)		
	End If
	
	If hastab = True Then
		bluetexttab x+mod1,y,blue(num,bluec)
	Else ; text gets drawn here
		drawtext(x+mod1,y,blue(num,bluec))
		;Text x+mod1,y,blue(num,bluec)
	End If
	
End Function


Function drawtext(x,y,t$)
	Local cof[856]
	Local cnt = 0
	t$ = t$ + " "
	If Not Left(t$,1) = ";" Then
	For k.bluekeywords = Each bluekeywords
		;
		a = Instr(Lower(t$),k\shortkw) 
		If a
			
			cof[a] = True
			If a > 1 And Mid(t$,a-1,1) <> " " Then cof[a] = 0
			If a+Len(k\shortkw) < Len(t$)
				If  Mid(t$,a+Len(k\shortkw),1) <> " " Then cof[a] = 0
			;If Rand(5) = 1 Then DebugLog MilliSecs()		
			End If
			cnt = cnt + 1
			cof[ a + (Len(k\shortkw)) ] = -1
			If a > 1 And Mid(t$,a-1,1) <> " " Then cof[a+Len(k\shortkw)] = 0
			If a+Len(k\shortkw) < Len(t$)
				If Mid(t$,a+Len(k\shortkw),1) <> " " Then cof[a+Len(k\shortkw)] = 0
			End If
			a = a + Len(k\shortkw)

			
			
			; recurse the rest
			b = -1 : p = a+1
			
			While b<>0 
				b = Instr(Lower(t$),k\shortkw,p)
				If b
					If Mid(t$,b-1,1) = " " And Mid(t$	,	b	+ Len(k\shortkw),	1) = " " Then
						cof[b] = True
						cof[ b + (Len(k\shortkw)) ] = -1
					End If
					p = b + 1
				End If					
			Wend
			b=-1
						
		End If		
	Next
	End If
	If cnt > 0 Then		
			x1 = x
			
			For i=1 To Len(t$)
				If cof[i] = True Then Color 200,200,0 
				If cof[i] = -1 Then Color 255,255,250 
				nt$ = Mid(t$,i,1)
				Text x1,y,nt$
				x1=x1+ StringWidth(nt$)				
			Next
			Color 255,255,255
		Else
			Text x,y,t$
	End If
End Function

Function drawlinenumbering(x,y,num,can)
	If can = 0 Then Return
	If bluelinenumberupdate = False Then Return
	If bluelinenumbervisible = False Then Return
	SetBuffer ImageBuffer(bluelinenumberimagebuffer)	
	ro = ColorRed()
	go = ColorGreen()
	bo = ColorBlue
	If Int(blue(num,blueabsfontheight)) = 0 Then fh = FontHeight() Else fh = blue(num,blueabsfontheight)
	Local tempthing = bluelinenumimagebuffer;CreateImage(bluelinenumberwidth,fh)
	;SetBuffer ImageBuffer(bluelinenumberimagebuffer)	
	; Buffer this ; shaded color adjustement
	If bluevisualsactive = True Then	;
		DrawBlock tempthing,x,y+1
	Else
		Color bluelinenumberbr,bluelinenumberbg,bluelinenumberbb
		Rect x,y+1,x+bluelinenumberwidth,y+fh+1,True ; line numbering
	End If
	; Draw the line number
	Color bluelinenumbertr,bluelinenumbertg,bluelinenumbertb	
	Text x-3,y,bluerightalign(num,4)	
	Color ro,go,bo
	SetBuffer CanvasBuffer(can)
End Function
Function bluetexttab(x,y,a$)
;RuntimeError a$
	For i=1 To Len(a$)
		cc$ = Mid(a$,i,1)
		Select Asc(cc)
			Case 9
				For ii=1 To bluedefaulttabsize
					x = x + StringWidth("a")
				Next
			Default
			Text x,y,cc
		End Select		
		fw = StringWidth(Mid(a$,i,1))
		x=x+fw
	Next
End Function
Function bluelinenum(num) ; core
	; Structural optimalization system goes here
	;
	Return num ; return pointer to data
End Function
Function bluereadline$(num) ; return the line number
	num = bluelinenum(num) ; core
	Return blue(num,bluec)
End Function
Function bluewriteLine(num,in$) ; Write to the line number with in$
	num = bluelinenum(num) ; core
	blue(num,bluec) = in$
End Function
Function blueline(ms,def$="",num,offset_y);; ; edit single line - core
	num = bluelinenum(num) ; optimalization	
	;offset_y=offset_y+24
	offset_y=offset_y;+GadgetY(ms)
	offset_x = 2
	width_mod = 0
	height_mod = 0
	;
	If bluelinenumbervisible = True Then		
		offset_x = bluelinenumberwidth + 3
		width_mod = bluelinenumberwidth + 3
	End If
	;
	
	Local wwidth = GadgetWidth(ms)-7
	Local wheight = FontHeight()
	;Local win = CreateWindow("",GadgetX(ms)+offset_x,GadgetY(ms)+offset_y,wwidth-width_mod,wheight,ms,32)
	Local win = lineareawin
	;Local can = CreateCanvas(GadgetX(ms)+offset_x,GadgetY(ms)+offset_y,wwidth-width_mod,wheight,lineareawin)
	Local can = CreateCanvas(0,0,wwidth-width_mod,wheight,lineareawin)	
	;SetGadgetShape win,GadgetX(ms)+offset_x,GadgetY(ms)+offset_y,wwidth-width_mod,wheight	
	SetGadgetShape win,GadgetX(ms)+offset_x,GadgetY(ms)+offset_y,wwidth-width_mod,wheight ; align text window to parent window!!
	SetGadgetShape can,0,0,wwidth-width_mod,wheight	
	;ActivateWindow win	
	;Local can = lineareacan
	
	;SetGadgetShape can,0,0,wwidth,wheight
	SetBuffer CanvasBuffer(can)
	HideGadget backcan
	Local r = blueactiveliner
	Local g = blueactivelineg
	Local b = blueactivelineb
	Local fr = blueactivelinefr
	Local fg = blueactivelinefg
	Local fb = blueactivelinefb
	Local sr = blueactivelinesr
	Local sg = blueactivelinesg
	Local sb = blueactivelinesb
	ClsColor r,g,b:Cls
	FlipCanvas(can)
	;RuntimeError "er"
	Local c$ = def$
	Local cursortimer = MilliSecs()+1000
	Local cursortimerdelay = 1000
	Local showcursor = True
	;oboe = Int(blue(num,bluecursorpos)) + ( bluelinenumberwidth / fontwidth() )
	Local cursorpos = blue(num,bluecursorpos)
	cursorpos = bluemousecursorupdate()
	;cursorpos = (bluecursorx - (bluelinenumberwidth/fontwidth()))
	Local selstart = blue(num,blueselstart)
	Local selend = blue(num,blueselend)
	Local selactive = blue(num,blueselactive)
	Local invselstart = blue(num,blueinvselstart)
	Local invselactive = False
	Local shiftactive = False
	Local ctrlactive = False
	Local altactive = False
	Local copybuffer$ = bluelinecopybuffer;blue(num,bluecopybuffer)
	c$ = def$
	Local c_back$ = Right(c$,Len(c$)-cursorpos);blue(num,bluec_back)
	Local c_front$ = Left(c$,cursorpos);blue(num,bluec_front)
	
	Local MousX = 0
	Local MousY = 0
	Local exitline = -1
	Local exitwithreturn = False
	Local exitwithcursup = False
	Local exitwithcursdown = False

	Local functionkeys[12] ; 1 --- 12	
	
	Local maxtextlen = (GadgetWidth(can)/StringWidth("a"))-1
	
	;RuntimeError c$
	;Local c_back$ = ""
	
	Local timer = CreateTimer(60)	
	While we<>$803
		we = WaitEvent()
		Select we
			Case $101 	;- Key down	
				; 54 r-shft, 42 - l-shft ; 157- rctrl, 29 - lctrl, 184-ralt,56-lalt
				ed = EventData()		
				Select ed
					Case 54 ; rshift
						If selactive = False Then selactive = True : selstart = cursorpos : invselstart = cursorpos : sellen = 0 : shiftactive = True
					Case 42 ;lshift
						If selactive = False Then selactive = True : selstart = cursorpos : invselstart = cursorpos : sellen = 0: shiftactive = True
					Case 157 ; rctrl			
						ctrlactive = True
					Case 29 ; lctrl
						ctrlactive = True
					Case 184 ; l alt
						altactive = True
					Case 56 ; r alt
						altactive = True						
				End Select		
			Case $102 	;- Key up			
				ed = EventData()
				Select ed
					Case 54 : shiftactive = False
					Case 42 : shiftactive = False
					Case 29 : ctrlactive = False
					Case 184: ctrlactive = False
				End Select
			;If EventData() = 1 Then Exit
			Case $103 	;- Key stroke (EDIT)
				ed = EventData():If ed = 13 Then we = $803
				showcursor = True : cursortimer = MilliSecs() + cursortimerdelay
				;backspa = 8 , 32 = space, 63273 ; homr ;
				;63276 - pageup,  63277 pagedown
				;RuntimeError ed		
				Select ed
					Case 63276 ; Page up
						
						we = $803
						exitline = bluepageup
					Case 63277 ; Page down
						we = $803
						exitline = bluepagedown
					Case 9 ; Tab
						If shiftenabled = True Then
						End If
						If altenabled = True Then
						End If
						If ctrlenabled = True Then
						End If
						If shiftenabled = False And altenabled = False And ctrlenabled = False Then
							c_front = c_front + String(Chr(32),bluedefaulttabsize)
							cursorpos = cursorpos + bluedefaulttabsize
							;store tab locations here
						End If
					Case 63271 ; Insert
						If blueinsertmode = True Then blueinsertmode = False Else blueinsertmode = True						
					Case 63239 ; F4 
						If altactive = True Then
							we = $803
							exitline = bluecloseapp
						End If
					Case 63272 ;del
						If selactive = False And shiftactive = False Then
							If Len(c_back$) > 0
								c_back$ = Right(c_back$,Len(c_back$)-1)
							EndIf
						Else				
							c_front$ = Left(c$,selstart)
							c_back$ = Right(c$,Len(c$)-selend)
							selactive = False : invselactive = False				
							cursorpos = selstart
							If Len(c_back$) + Len(c_front$) = 0 Then cursorpos = 0
						End If
					Case 63273 ; home
						If ctrlactive = False Then
							cursorpos = 0
							c_front$ = Left(c$,cursorpos)
							c_back$ = Right(c$,Len(c$)-cursorpos)
							If shiftactive = False Then selactive = False
						Else ;Ctrl + home
							exitline = bluectrlhome
							we=$803
						End If
					Case 63275 ; end
						If ctrlactive = False Then
							cursorpos = Len(c$)
							c_front$ = Left(c$,cursorpos)
							c_back$ = Right(c$,Len(c$)-cursorpos)
							If shiftactive = False Then selactive = False
						Else ;CTRL + End
							exitline = bluectrlend
							we=$803
						End If
					Case 8 ; backspace
						;If Len(c$) > 0 c$ = Left(c$,Len(c$)-2)
						c_front$ = Left(c_front$,Len(c_front$) - 1)
						If cursorpos > 0 Then cursorpos = cursorpos - 1
						If shiftactive = False Then selactive = False
					Case 13 ; enter
						exitline = num + 1 
						exitwithreturn = True
						we = $803
					Case 27 ; escape
						we = $803
					Case 63232 ; curs up
						If num > 0 Then
							exitwithcursup = True
							exitline = num - 1				
							If cursorpos > Len(blue(exitline,bluec)) Then
								blue(exitline,bluecursorpos) = Len(blue(exitline,bluec))
								Else
								blue(exitline,bluecursorpos) = cursorpos
							End If
							we = $803
						End If
					Case 63233 ; curs down
						If num < bluenumlines Then
							exitwithcursdown = True
							exitline = num + 1
							If cursorpos > Len(blue(num,bluec)) Then
								blue(exitline,bluecursorpos) = Len(blue(exitline,bluec))
							Else
								blue(exitline,bluecursorpos) = cursorpos
							End If
							we = $803
						Else
							we = $803
							exitline = num
						End If
					Case 63235 ; cursright
						If ctrlactive = True Then
							If shiftactive = False Then selactive = False
							z = movecursorright(c$,cursorpos+1)
							;DebugLog z
							If z =0 Then z=Len(c$)
							cursorpos = z
							c_front$ = Left(c$,cursorpos)
							c_back$ = Right(c$,Len(c$) - cursorpos)
						Else
							If cursorpos < Len(c$) Then cursorpos = cursorpos + 1
							c_front$ = Left(c$,cursorpos)
							c_back$ = Right(c$,Len(c$)-cursorpos)
							If shiftactive = False Then selactive = False
						End If
					Case 63234 ; curs left
						If ctrlactive = True Then
							If shiftactive = False Then selactive = False
							z = movecursorleft(c$,cursorpos-1)					
							If z <0 Then z=0
							cursorpos = z
							c_front$ = Left(c$,cursorpos)
							c_back$ = Right(c$,Len(c$) - cursorpos)
							Else
							If cursorpos > 0 Then cursorpos = cursorpos - 1
							c_front$ = Left(c$,cursorpos)
							c_back$ = Right(c$,Len(c$)-cursorpos)
							If shiftactive = False Then selactive = False
						End If
					Default ; all other keys
						If ctrlactive=False And altactive = False
							If blueinsertmode = False Then ; Regular type without Insert
								If cursorpos < maxtextlen And Len(c$) < maxtextlen
									c_front$ = c_front$ + Chr(ed)
									cursorpos = cursorpos + 1
									selactive = False
								End If								
							Else							
								If cursorpos < maxtextlen ; Regular type with insert
									c_front$ = c_front$ + Chr(ed)
									c_back$ = Right(c_back$,Len(c_back$)-1)									
									cursorpos = cursorpos + 1
									selactive = False
								End If
							End If
							If shiftactive = False Then selactive = False
						End If
				End Select
				;
				;RuntimeError
				; CTRL things
				If ctrlactive = True Then		
					Select ed
						Case 22 ; Ctrl + v ; paste ; bluelinewidth						
							If copybuffer$<> "" Then
								; Single line copy paste!!
								If Len(copybuffer$)  + Len(c$) < bluelinewidth Then
									If selactive = True Then
										c$ = c_front$ + c_back$										
										c$ = bluereplacelineselection(c$,copybuffer$,selstart,selend)
										cursorpos = (cursorpos - sellen) + Len(copybuffer$)
										c_front$ = Left(c$,cursorpos)
										c_back$ = Right(c$,Len(c$)-cursorpos)
										selactive = False
									Else									
											c_front$ = c_front$ + copybuffer$
											cursorpos = cursorpos + Len(copybuffer)										
									End If
								End If
							End If
						Case 3	; CTRL + C
							If selactive = True Then
								copybuffer$ = Mid(c$,selstart+1,sellen)
								DebugLog copybuffer$								
							End If
						Default				
					End Select
				EndIf
				c$ = c_front$ + c_back$				
			Case $201 	;- Mouse down
				; Position cursor
				bluecursorx = EventX() / FontWidth()
				If obluecursorx <> bluecursorx Then DebugLog bluecursorx
				cursorpos = bluecursorx
				c_front$ = Left(c$,cursorpos)
				c_back$ = Right(c$,Len(c$)-cursorpos)
				mup = False
			Case $202 	;- Mouse up
				mup = True
				nl = EventY() / FontHeight() + linecounter
				;DebugLog "Exit val : " + nl + " : : " + num
				If nl <> 0 Then cursorpos = bluecursorx : bluecursorupdate = True
				If moc = False Then we = $803		
			Case $203 	;- Mouse move
				mousx = EventX()
				mousy = EventY()
			Case $204 	;- Mouse wheel 
			Case $205 	;- Mouse enter
				If EventSource() = can
					moc=True
				End If
			Case $206 	;- Mouse leave
				If EventSource() = can
					moc=False
				End If
			Case $401 	;- Gadget action 
			Case $801 	;- Window move
				SetGadgetShape win,GadgetX(ms)+offset_x,GadgetY(ms)+offset_y,wwidth-width_mod,wheight ; align text window to parent window!!
				SetGadgetShape can,0,0,wwidth-width_mod,wheight				
			Case $802 	;- Window size 
			Case $803 	;- Window close
				If EventSource() = ms Then End
			Case $804 	;- Window activate 
			Case $1001 	;- Menu event 
			Case $2001 	;- App suspend	
			Case $2002 	;- App resume
				SetGadgetShape win,GadgetX(ms)+offset_x,GadgetY(ms)+offset_y,wwidth-width_mod,wheight ; align text window to parent window!!
				SetGadgetShape can,0,0,wwidth-width_mod,wheight
			Case $2003 	;- App Display Change
			Case $2004 ;- App Begin Modal 
			Case $2005 ;- App End Modal 
			Case $4001	;- Timer tick
				SetBuffer CanvasBuffer(can)
				Cls				
				; quick hack to fix control handling
				If KeyDown(29) = True Or  KeyDown(157) = True Then ctrlactive = True Else ctrlactive = False; lctrl				
				If KeyDown(42) = True Or KeyDown(54) = True Then shiftactive = True Else shiftactive = False ; lshift				
					
				;editline highlight
				Color r+10,g+10,b+10 ; Set line higlight color
				Line 0,0,GadgetWidth(can),0 ; draw higlight line
				Color r,g,b 
				Line 0,GadgetHeight(can)-1,GadgetWidth(can),GadgetHeight(can)-1
				Color r-10,g-10,b-10
				seldraw = False

				If selactive = True And sellen <>0 ;And selstart<>cursorpos Then
					Color sr,sg,sb
					Rect StringWidth(Left(c$,selstart)),0,StringWidth(Mid$(c$,selstart+1,sellen)),FontHeight()
					seldraw = True
				End If
				blue(num,bluec) = c$
				Color fr,fg,fb:bluedrawtext 0,-1,num,can,True
				;Color 0,0,0
				;Rect 0,0,30,10,True
				;Color fr,fg,fb:Text 0,-1,c$
				
				If cursortimer < MilliSecs() Then					
					cursortimer = MilliSecs() + cursortimerdelay
					If showcursor = True Then showcursor = False Else showcursor = True	
				End If
					
				sellen = selend-selstart
				selend = cursorpos
				If selactive=True And sellen = 0 And selreset = False Then
					;DebugLog"er"
					invselactive = False
					invselstart = cursorpos
					selend = cursorpos
					selstart = cursorpos
					selreset = True
					ElseIf sellen <> 0
					selreset = False
				End If
				If sellen < 0 And invselactive = False And selactive = True Then		
					invselactive = True
					ElseIf invselactive=True And sellen < 0 Then ; hit home with inversed sel;ection
						invselactive = False
						selstart = invselstart
				End If
				If invselactive = True Then
					selend = invselstart
					selstart = cursorpos
					sellen = invselstart-(cursorpos-1)
				End If

			; drawcursor (yikes!)
			If showcursor = True Then bluedrawcursor( StringWidth(Left(c$,cursorpos))+(StringWidth(String(" ",bluecursorposx(num)))), FontHeight()-2,StringWidth("a"),blueinsertmode)
			
			;	Line 0,220+3,200,220+3 y
			;	Text 0,220,c_front$ + "|" + c_back	
			;	Text 0,240,"string len : " + Len(c$)
			;	If selactive = True Then Text 0,250,"sellen : " + sellen + " brr : " + Mid(c$,selstart+1,sellen)	
			;	Text 0,260,"invselactive:" +invselactive+" invselstart:"+invselstart
			;	Text 0,280,"seldraw:"+seldraw + " selactive:"+selactive
			;	Text 0,300,"selstart:" + selstart + " cursorpos:" + cursorpos
			;	Text 0,320,"selend:"+selend+" sellen:"+sellen
			;	Text 0,340,"ctrlactive:"+ctrlactive
			FlipCanvas(can)
		End Select
	Wend
	
	; store changes	
	blue(num,blueactive) = 0
	blue(num,bluec) = c$
	blue(num,bluecursortimer) = cursortimer
	blue(num,bluecursortimerdelay) = cursortimerdelay
	blue(num,blueshowcursor) = showcursor
	blue(num,bluecursorpos) = cursorpos
	blue(num,blueselstart) = selstart
	blue(num,blueselend) = selend
	blue(num,blueselactive) = selactive
	blue(num,blueinvselstart) = invselstart
	blue(num,blueinvselactive) = invselactive
	blue(num,blueshiftactive) = shiftactive
	blue(num,bluectrlactive) = ctrlactive
	;blue(num,bluecopybuffer) = copybuffer$
	bluelinecopybuffer$ = copybuffer$
	ShowGadget backcan
	FreeGadget can
	If exitline <-50 Then Return exitline
	If exitwithcursup = True Then Return exitline
	If exitwithcursdown = True Then Return exitline
	If exitwithreturn= True Then Return exitline
	If mup = True Then Return nl
	
	
	Return -1
End Function
Function bluemousecursorupdate() ; core
	; Update the cursor with the activities of the mouse pointer ; trigger flag gets inverted!
	If bluecursorupdate = True Then
		cursorpos = bluecursorx- (bluelinenumberwidth/FontWidth())
		bluecursorupdate = False
	End If
	Return cursorpos
End Function
Function bluereplacelineselection$(in$,repl$,st,nd) ; core
	a$ = Left(in$,st)
	b$ = Right(in$,Len(in$)-nd)
	Return a$+repl$+b$
End Function
Function bluedrawcursor(x,y,w,t) ; t = 0 = regular 1 = insert
	Select t
		Case 0 ; Regular cursor
			Color bluecursorcolorr,bluecursorcolorg,bluecursorcolorb
			Rect x,y-FontHeight(),2,FontHeight(),True		
		Case 1 ; Insert Cursor
			Color blueinsertcursorcolorr,blueinsertcursorcolorg,blueinsertcursorcolorb
			Rect x,y,w,2,True		
	End Select
End Function
Function bluecursorposx(num)
	;DebugLog blue(num,bluetabmodifier)
	Return blue(num,bluetabmodifier)
End Function
Function movecursorleft(c$,cursorpos)
	z = instrleft(c$," ",cursorpos)
	While Mid(c$,z) = " "
		z=z-1
		If z<1 Then Exit
	Wend		
	Return z-1
End Function
Function movecursorright(c$,cursorpos)
	z = Instr(c$," ",cursorpos)
	While Mid(c$,z) = " "
		z=z+1
		If z>Len(c$) Then Exit
	Wend		
	Return z
End Function
Function instrleft(c$,f$,pos)
	
	If f$="" Then Return 0
	If Len(c$) = 0 Then Return 0
	If pos<0 Or pos>Len(c$) Then Return 0
	pos2 = 1
	
	While pos2 <> 0
		q = Instr(c$,f$,pos2)
		;If Confirm(q+"|"+pos2) Then End
		If q>pos Then Exit
		If q = 0 Then Exit	
		pos2 = q+1
	Wend	
	Return pos2-1
End Function
Function bluerightalign$(in$,tlen)
	If tlen =<0 Then Return
	While Len(in$) < tlen in$=" " + in$ : Wend
	Return in$
End Function
Function bluecountchar(num,in$)
	; char Input ascii
	;
	ms = MilliSecs()
	pos = 1
	While  pos > 0  ; timeout after 2000 millisecs()!!!
		q = Instr(blue(num,bluec),in$,pos)
		num2 = num2 + 1
		If q = 0 Then Exit
		pos = q+1
	Wend	
	Return num2-1
End Function
Function blueloadtext(in$) ; load text
	If FileType(in$) <> 1 Then RuntimeError in$
	cnt = 0
	f = ReadFile(in$) ; count lines	
		While Eof(f) = False
			a$ = ReadLine(f)
			cnt=cnt + 1
		Wend
	CloseFile(f)
	bluenumlines = cnt :redimblue(bluenumlines) : cnt = 0 ; redimension blue data array
	f = ReadFile(in$) ; Load the text (capped at linewidth)
		While Eof(f) = False And cnt < bluenumlines 
			a$ = Left(ReadLine(f),bluelinewidth)
			a$ = Replace(a$,Chr(9),String(Chr(32),bluedefaulttabsize))
			;If Asc(a$) = Chr(9) Then a$ = String(Chr(32),bluedefaulttabsize)
			blue(cnt,bluec) = a$
			cnt=cnt+1
		Wend
	CloseFile(f)
End Function
Function redimblue(num)
	Dim blue$(num,bluepointers)
End Function
;
Function bufferlinenumbers() ; make a image that gets blocked into the background of the linenumbers  - run after linenumber change
	Local tempthing = bluelinenumimagebuffer
	SetBuffer ImageBuffer(tempthing)
	ra# = bluelinenumberbr
	ga# = bluelinenumberbg
	ba# = bluelinenumberbb
	mod1# = ra/ImageWidth(tempthing)
	mod2# = ga/ImageWidth(tempthing)
	mod3# = ba/ImageWidth(tempthing)
	For x1=0 To ImageWidth(tempthing)-2
		Color cnta#,cntb#,cntc#
		cnta# = cnta + mod1#
		cntb# = cntb + mod2#
		cntc# = cntc + mod3#
		Line x1,0,x1,ImageHeight(tempthing)
	Next
	Color cnta#/2,cntb#/2,cntc#/2
	Line x1,0,x1,ImageHeight(tempthing)	
End Function
;
Function design_docs()
;
; Tabs are stored as chr code 9. Drawing these on the screen is check by seing if a tab is present in the array hastab flag
; the tabflag bluehastab needs to be cleared in the lines datafield when tabs are removed.
; Currently rethinking if I should use regular spaces and store the tab data seperatly....
;
; Line numbering is buffered in a image. this has 3 times the height size and only rebuilds when a flag is set
;
; The mouse 2 text cursor x position needs alignment with the text area offset. Line numbering ect. The regular cursor positioning
; is done inside the line edit section. Outside code needs to take this into account seing the line edit uses Zero as it most left
; offset.
;
;
End Function


Function bluecontrolispressed()
	If KeyDown(29) Then Return True
	If KeyDown(157) Then Return True
	Return False
End Function




Function loadkeywords()
	;load the keywords
	Local a$
	Local kw$[1512]
	If FileType("keywords.txt") <> 1 Then Notify "can not load" : End ;RuntimeError "Reinstall!!"
	cnt = 0
	f = ReadFile("keywords.txt")
	;
	While Eof(f) = False
		a$ = ReadLine(f)
		If Len(a$) > 0
			;k.bluekeywords = New bluekeywords
			;k\kw = Lower(a$)
			kw[cnt] = Lower(a$)
			cnt=cnt+1
		End If
	Wend
	;
	If cnt = 0 Then RuntimeError "No keywords loaded"
	;
	For i=0 To cnt-1
		;
		If Instr(kw[i]," ") Then
			k.bluekeywords = New bluekeywords
			k\longkw = kw[i]
			Else
			k.bluekeywords = New bluekeywords
			k\shortkw = kw[i]
			DebugLog kw[i]
		End If
		;
	Next
	;
	CloseFile(f)
	Return
	
End Function
