; ID: 1470
; Author: Rob Farley
; Date: 2005-09-25 07:15:27
; Title: Another GUI
; Description: GUI

**** GUI.bb





Global OK = 2
Global YesNo = 1

Global mxs,mys ;mouse x and y speeds


Global WindowR = 187
Global WindowG = 190
Global WindowB = 202

Global TitleR = 69
Global TitleG = 107
Global TitleB = 123

Global TitleTextR = 255
Global TitleTextG = 255
Global TitleTextB = 255

Global WinTextR = 0
Global WinTextG = 0
Global WinTextB = 0

Global WindowContrast = 50
Global ButtonContrast = 20



Type Win
	Field name$
	Field x
	Field y
	Field xs
	Field ys
	Field held
	Field obstruted
	Field mouseover
	Field activated
	Field close
	Field closewarning
	Field popup
	Field extra$
End Type

Type Button
	Field winname$
	Field label$
	Field x
	Field y
	Field xs
	Field ys
	Field active
End Type

Type wintext
	Field winname$
	Field wintext$
	Field label$
	Field x
	Field y
	Field centered
End Type

Type slider
	Field winname$
	Field label$
	Field x
	Field y
	Field xs
	Field value#
	Field showlabel
	Field held
End Type

Function NewSlider(winname$,label$,x,y,xs,value#=0,showlabel=True)

	n.slider = New slider
	n\winname = winname
	n\label = label
	n\x = x
	n\y = y
	n\xs = xs
	n\value = value
	n\showlabel = showlabel
	n\held = False
	
End Function
	
Function DrawSlider(x,y,sl.slider,enable)

	fRect x+5+sl\x,y+15+sl\y,sl\xs-10,2,WindowR,WindowG,WindowB,True
	
	Color WinTextR,WinTextG,WinTextB
	

	If sl\showlabel Then Text x+sl\x+(sl\xs/2),y+sl\y,sl\label,True
	
	If enable And MouseDown(1) = False And sl\held=True Then sl\held=False
	
	If drawbutton("",x+(sl\xs*sl\value)-5+sl\x,y+10+sl\y,10,20,sl\held,enable) Then sl\held = True
	
	If sl\held = True
		sl\value = sl\value + (Float(mxs)/sl\xs)
		If sl\value < 0 Then sl\value = 0
		If sl\value > 1 Then sl\value = 1
	EndIf

End Function

Function GetValue#(name$)

	Winname$ = readitem$(name$,"win")
	label$ = readitem$(name$,"label")
	
	For sl.slider = Each slider
		If sl\label = label And sl\winname = winname
			Return sl\value			
		EndIf
	Next	
	
End Function


Function newwintext(winname$,label$,wintext$,x,y,centered = False)
	n.wintext = New wintext
	n\winname = winname
	n\wintext = wintext
	n\label = label
	n\x = x
	n\y = y
	n\centered = centered
End Function



Function DeleteWindow(winname$)
	For b.Button = Each Button
		If b\winname = winname Then Delete b
	Next
	
	For wt.wintext = Each wintext
		If wt\winname = winname Then Delete wt
	Next
	
	For w.win = Each win
		If w\name = winname Then Delete w
	Next
	
	For s.slider = Each slider
		If s\winname = winname Then Delete s
	Next
	
End Function

Function settext(name$,newvalue$)

	Winname$ = readitem$(name$,"win")
	label$ = readitem$(name$,"label")	

	For wt.wintext = Each wintext
		If wt\label = label And wt\winname = winname
			wt\wintext = newvalue
			Exit
		EndIf
	Next
End Function


Function getText(name$)

	Winname$ = readitem$(name$,"win")
	label$ = readitem$(name$,"label")	

	For wt.wintext = Each wintext
		If wt\label = label And wt\winname = winname
			Return wt\wintext			
		EndIf
	Next
End Function


Function newwindow(name$,x,y,xs,ys,close,activated=False,closewarning=True,popup=False,extra$="")

	For w.win = Each win
		If w\name = name Then Return
	Next

	n.win = New win
	n\name = name$
	n\x = x
	n\y = y
	n\xs = xs
	n\ys = ys
	n\activated = activated
	n\close = close
	n\closewarning = closewarning
	n\popup = popup
	n\extra = extra
	
	If close Then newbutton(name,"x",xs-13,-12,11,11)

End Function

Function newbutton(winname$,label$,x,y,xs,ys,active = False)
	n.Button = New Button
	n\winname = winname
	n\label = label
	n\x = x
	n\y = y
	n\xs = xs
	n\ys = ys
	n\active = active
End Function


Function drawbutton(label$,x,y,xs,ys,active,enable)
	
	If enable And RectsOverlap(x,y,xs,ys,MouseX(),MouseY(),1,1) Then over = True
	r=WindowR:g=WindowG:b=WindowB
	If over = True 
		r = Clip(r+ButtonContrast)
		b = Clip(b+ButtonContrast)
		g = Clip(g+ButtonContrast)
		If MouseDown(1) Then active = True
	EndIf

	fRect x,y,xs,ys,r,g,b,active

	Color wintextr,wintextg,wintextb

	Text x+(xs/2),y+(ys/2),label,True,True
	
	If enable = True And over = True And MouseDown(1) Then Return True Else Return False
End Function

Function togglebutton(name$)
	
	win$ = readitem(name,"win")
	label$ = readitem(name,"but")
	
	If win$="?null?" Or label$="?null?" Then Return

	For b.Button = Each Button
		If b\winname = win And b\label = label
			If b\active = False Then b\active = True Else b\active = False
		EndIf
	Next
End Function

Function readitem$(l$,item$)
	ret$="?null?"
	item = Lower(item)
	lb = Instr(Lower(l$),"<"+item+">")
	rb = Instr(Lower(l$),"</"+item+">")
	If lb>0 And rb>0
		lb = lb + Len("<"+item+">")
		ret= Mid(l,lb,rb-lb)
	EndIf
	Return ret$
End Function	

Function drawwindows$()
	mouseheld = False
	newwin = False

	mxs = MouseXSpeed()
	mys = MouseYSpeed()
	
	ret$ = ""
	
	order1=0
	For w.win = Each win
		w\obstruted = False
		w\mouseover = False
		
		order1=order1+1
		
		; check to see if the mouse is over part of a window and not obscured
		If RectsOverlap(w\x,w\y,w\xs,w\ys+15,MouseX(),MouseY(),1,1)
			w\mouseover = True
			
			order2=0
			For ww.win = Each win
				order2=order2+1
				If w<>ww And RectsOverlap(ww\x,ww\y,ww\xs,ww\ys+15,MouseX(),MouseY(),1,1) And order1>order2
					ww\mouseover = False
				EndIf
			Next
			
			
		EndIf

		; check to see if a window is obscured at all if so you can't use it
		order2=0
		For ww.win = Each win
			order2=order2+1
			If ww<>w And RectsOverlap(w\x,w\y,w\xs,w\ys+15,ww\x,ww\y,ww\xs,ww\ys+15) And order1<order2
				w\obstruted = True
			EndIf
		Next
		
		; if you're dragging a window and released the mouse then stop dragging
		If w\held And MouseDown(1) = False Then w\held=False
		
		
		If w\held = True And mouseheld=False Then mouseheld = True
			
					
		If w\activated = True
			If w <> Last win Then
				x=w\x
				y=w\y
				xs=w\xs
				ys=w\ys
				name$ = w\name
				close = w\close
				closewarning = w\closewarning
				Delete w:newwin = True
			EndIf
		EndIf
		

	Next
	
	; to get the window on the top, the old one is deleted and a new one is created
	; Probably a better way to do this.
	
	If newwin = True
		newwindow(name$,x,y,xs,ys,close,True,closewarning)
		For ww.win = Each win
			If ww\name<>name Then ww\activated = False:ww\held=False
		Next
	EndIf
	
	ret$ = "nothing"	

	For w.win = Each win

		fRect w\x,w\y+15,w\xs,w\ys,windowr,windowg,windowb
		fRect w\x,w\y,w\xs,15,titler,titleg,titleb
		
		Color titletextr,titletextg,titletextb
		Text w\x+2,w\y,w\name
		
		; only activate buttons if not obstructed at all		
		If w\obstruted = False And mouseheld = False Then enable = True Else enable = False
		
		; draw buttons
		For b.Button = Each Button
			If b\winname = w\name
				If drawbutton(b\label,w\x+b\x,w\y+b\y+15,b\xs,b\ys,b\active,enable) Then ret$="<win>" + b\winname +"</win><but>"+ b\label+"</but>"
			EndIf
		Next
		
		; draw text
		Color wintextr,wintextg,wintextb
		For wt.wintext = Each wintext
			If wt\winname = w\name
				Text w\x+wt\x,w\y+wt\y+15,wt\wintext,wt\centered
			EndIf
		Next
		
		; draw sliders
		For sl.slider = Each slider
			If sl\winname = w\name
				DrawSlider(w\x,w\y,sl.slider,enable)
			EndIf
		Next

		
		
		If MouseDown(1) And mouseheld = False And w\mouseover = True And w\activated = False Then w\activated = True
		If MouseDown(1) And mouseheld = False And w\activated = True And RectsOverlap (w\x,w\y,w\xs,15,MouseX(),MouseY(),1,1) And w\held = False Then w\held=True
			
		If w\held = True
				w\x = w\x + mxs
				w\y = w\y + mys
		EndIf
		
		; auto close warning event
		If readitem(ret,"but") = "x" And w\closewarning = True
			warningbox("Close Window " + readitem(ret,"win"),"Are you sure?",yesno,readitem(ret,"win"))		
		EndIf
	Next
	
	; close window auto events
	
	For w.win = Each win
	
		If w\popup = True
			If readitem(ret,"but")="No" Or readitem(ret,"but")="OK" Then DeleteWindow(w\name)
			
			If readitem(ret,"but")="Yes"
				deletewindow(w\extra)
				deletewindow(w\name)
			EndIf
		EndIf
		
		
		
		;If readitem(ret,"win") = "Close Window "+w\name And readitem(ret,"but")="Yes"
		;	DeleteWindow("Close Window "+w\name):deletewindow(w\name)
		;EndIf
		
		
	Next		
	
	
	Return ret$
	
End Function

Function fLine(x,y,x1,y1,r=255,g=255,b=255)
; fLine will only draw horizonal or vertical lines, no diagonals
; Defaults to a white line.

argb=(b Or (g Shl 8) Or (r Shl 16) Or ($ff000000))

If x=x1
	If y>y1 Then t=y1:y1=y:y=t
	For n=y To y1
	If n>=0 And n<=GraphicsHeight() And x>=0 And x<=GraphicsWidth() Then WritePixelFast x,n,argb,BackBuffer()
	Next
EndIf

If y=y1
	If x>x1 Then t=x1:x1=x:x=t
	For n=x To x1
	If y>=0 And y<=GraphicsHeight() And n>=0 And n<=GraphicsWidth() Then WritePixelFast n,y,argb,BackBuffer()
	Next
EndIf 
End Function

Function Clip(x)
	If x>255 Then x=255
	If x<0 Then x=0
	Return x
End Function

Function frect(x,y,xs,ys,r=150,g=150,b=150,invert=False)

	;For n=y+1 To y+ys-1
	;fline x+1,n,x+xs-1,n,r,g,b
	;Next
	Color r,g,b
	Rect x+1,y+1,xs-2,ys-2,True
	
	If invert = False
	r1 = Clip(r+WindowContrast)
	g1 = Clip(g+WindowContrast)
	b1 = Clip(b+WindowContrast)
	r2 = Clip(r-WindowContrast)
	g2 = Clip(g-WindowContrast)
	b2 = Clip(b-WindowContrast)
	Else
	r1 = Clip(r-WindowContrast)
	g1 = Clip(g-WindowContrast)
	b1 = Clip(b-WindowContrast)
	r2 = Clip(r+WindowContrast)
	g2 = Clip(g+WindowContrast)
	b2 = Clip(b+WindowContrast)	
	EndIf
	
	
	
	LockBuffer BackBuffer()
	
	fline x,y,x+xs-1,y,r1,g1,b1
	fline x,y,x,y+ys-1,r1,g1,b1
	fline x+xs-1,y,x+xs-1,y+ys-1,r2,g2,b2
	fline x+xs-1,y+ys-1,x,y+ys-1,r2,g2,b2
	
	UnlockBuffer BackBuffer()

End Function

Function warningbox(title$,message$,wType=1,extra$="")
For w.win = Each win
	w\activated = False
Next
	
newwindow(title$,GraphicsWidth()/2 - 150,GraphicsHeight()/2 -50,300,100,False,True,True,True,extra)

newwintext(title$,title$+":Message",message,150,20,True)

If wType = yesno Then newbutton(title$,"Yes",50,50,90,20):newbutton(title$,"No",160,50,90,20)
If wtype = ok Then newbutton(title$,"OK",105,50,90,20)

End Function























**** GUI_Demo.bb


Graphics 1024,768,0,2

Include "gui.bb"

font = LoadFont("arial.ttf",15)
SetFont font

ClsColor 98,114,138




newwindow("Gui Window",100,100,200,100,1)

Newbutton("Gui Window","Up",20,20,40,20)
Newbutton("Gui Window","Down",140,20,40,20)
NewWinText("Gui Window","Counter","0",100,20,True)
newbutton("Gui Window","Toggler!",40,60,120,20)

newwindow("Another Window",300,100,200,200,1)

Newbutton("Another Window","Up",20,20,40,20)
Newbutton("Another Window","Down",140,20,40,20)
NewWinText("Another Window","Counter","100",100,20,True)
newbutton("Another Window","Toggler!",40,60,120,20)

NewSlider("Another Window","Slider",20,110,160,0,True)
NewWinText("Another Window","Slider Value",0,100,130,True)

NewButton("Another Window","Pop!",40,150,120,20)

SetBuffer BackBuffer()

Repeat
Cls



time = MilliSecs()

lastevent$ = event$
event$ = drawwindows()

Text 0,20,"Render Time: "+Int(MilliSecs()-time)+" millisecs"

Text 0,0,"Event: "+event

If event = "<win>Gui Window</win><but>Toggler!</but>" And lastevent <> event Then togglebutton(event)
If event = "<win>Gui Window</win><but>Up</but>" Then counter = gettext("<win>Gui Window</win><label>Counter</label>") + 1 : settext("<win>Gui Window</win><label>Counter</label>",counter)
If event = "<win>Gui Window</win><but>Down</but>" Then counter = gettext("<win>Gui Window</win><label>Counter</label>") - 1 : settext("<win>Gui Window</win><label>Counter</label>",counter)

If event = "<win>Another Window</win><but>Toggler!</but>" And lastevent <> event Then togglebutton(event)
If event = "<win>Another Window</win><but>Up</but>" Then counter = gettext("<win>Another Window</win><label>Counter</label>") + 1 : settext("<win>Another Window</win><label>Counter</label>",counter)
If event = "<win>Another Window</win><but>Down</but>" Then counter = gettext("<win>Another Window</win><label>Counter</label>") - 1 : settext("<win>Another Window</win><label>Counter</label>",counter)

If event = "<win>Another Window</win><but>Pop!</but>" Then warningbox("Pop Up Message","Can be Yes/No or just OK",OK)

settext("<win>Another Window</win><label>Slider Value</label>",Int(getvalue("<win>Another Window</win><label>Slider</label>")*100))




Flip
Until KeyHit(1)
