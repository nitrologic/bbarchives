; ID: 492
; Author: Otus
; Date: 2002-11-18 14:10:41
; Title: some win graphics
; Description: win graphics

;win.bb
Global FontMenu=LoadFont("Tahoma",13,False,False,False)
global check=loadimage("checked.bmp");7*7 pixels
global obimg=loadanimimage("optbutton.bmp",14,14,0,3)
maskimage obimg,255,0,255
maskimage check,255,0,255

global NumWindows,NumButtons,NumCBoxes,NumTBoxes

type Window
	field x,y,w,h,capt$,xbut.Button
end type

type Button
	field capt$,x,y,w=100,h=30,grey=0,down
end type

type CheckBox
	field x,y,checked,grey,down
end type

type TextBox
	field x,y,w,h,capt$,active,cursor,maxsize=0,tx,cflash,grey
end type

type OptButGroup
	field firstbut.OptionButton,NumButs
end type

type OptionButton
	field x,y,checked,grey,down,nextbut.OptionButton
end type

function DrawWinStuff()
;works with only one window
	for w.Window=each Window
		DrawWindow(w)
	next
;	for b.Button=each Button
;		DrawButton(b)
;	next
;	for cb.CheckBox=each CheckBox
;		DrawCheckBox(cb)
;	next
;	for tb.TextBox=each TextBox
;		DrawTextBox(tb)
;	next
end function

function CreateWindow.Window(wx,wy,ww,wh,wcapt$="_def",xbutton=1)
	if wcapt$="_def" then wcapt$="Window "+(NumWindows+1)
	w.Window=new Window
	w\x=wx
	w\y=wy
	w\w=ww
	w\h=wh
	w\capt$=wcapt$
	if xbutton then w\xbut.Button=CreateButton("X",w\x+w\h-4-15,w\y+4,15,15)
	NumWindows=NumWindows+1
	return w.Window
end function

function DeleteWindow(w.Window)
	DeleteButton(w\xbut.Button)
	delete w.Window
	NumWindows=NumWindows-1
end function

function DrawWindow(w.window)
	setfont FontMenu
	color 0,0,0
	rect w\x,w\y,w\w,w\h
	color 192,192,192
	rect w\x,w\y,w\w-1,w\h-1
	color 128,128,128
	rect w\x+1,w\y+1,w\w-2,w\h-2
	color 255,255,255
	rect w\x+1,w\y+1,w\w-3,w\h-3
	color 192,192,192
	rect w\x+2,w\y+2,w\w-4,w\h-4
	if not w\capt$=""
		color 128,128,128
		line w\x+5,w\y+10,w\x+w\w-5,w\y+10
		color 255,255,255
		line w\x+5,w\y+11,w\x+w\w-5,w\y+11
		color 192,192,192
		rect w\x+(w\w/2-stringwidth(w\capt$)/2),w\y+10,stringwidth(w\capt$),2
		color 0,0,0
		text w\x+w\w/2,w\y+10.5,w\capt$,1,1
	end if
	if not w\xbut=null then return DrawButton(w\xbut)
end function

function CreateButton.Button(bcapt$,bx,by,bw=100,bh=30,center=0)
	if center then bx=bx-bw/2:by=by-bh/2
	if bcapt$="_def" then bcapt$="Button "+(NumButtons+1)
	b.Button=new Button
	b\x=bx
	b\y=by
	b\w=bw
	b\h=bh
	b\capt$=bcapt$
	NumButtons=NumButtons+1
	return b.Button
end function

function DeleteButton(b.Button)
	delete b
	NumButtons=NumButtons-1
end function

function DrawButton(b.Button)
;returns true if button is pressed
	if mousedown(1) and mousex()>=b\x and mousex()<=b\x+b\w and mousey()>=b\y and mousey()<=b\y+b\h then b\down=1
	color 0,0,0
	rect b\x,b\y,b\w,b\h
	color 255,255,255
	if not b\down then rect b\x,b\y,b\w-1,b\h-1
	color 128,128,128
	rect b\x+1,b\y+1,b\w-2,b\h-2
	color 192,192,192
	if not b\down then rect b\x+1,b\y+1,b\w-3,b\h-3 else rect b\x+2,b\y+2,b\w-4,b\h-4
	plot b\x+b\w,b\y
	plot b\x,b\y+b\h
	color 0,0,0
	text b\x+(b\w/2),b\y+(b\h/2),b\capt$,1,1
	if b\grey
	  color 255,255,255
	  text b\x+(b\w/2),b\y+(b\h/2),b\capt$,1,1
	  color 128,128,128
	  text b\x+(b\w/2)-1,b\y+(b\h/2)-1,b\capt$,1,1
	end if
	if b\grey=0 and b\down=1 and mousedown(1)=0 then ret=true
	if mousedown(1)
		if mousex()>=b\x and mousex<=b\x+b\w and mousey()>=b\y and mousey()<=b\y+b\h then b\down=1 else b\down = 0
		else
		b\down=0
	end if
	return ret
end function

function CreateCheckBox.CheckBox(cbx,cby,checked=0,center=0,greyed=0)
	cb.CheckBox=new CheckBox
	if center then cbx=cbx-7:cby=cby-7
	cb\x=cbx
	cb\y=cby
	cb\checked=checked
	cb\grey=greyed
	NumCBoxes=NumCBoxes+1
	return cb
end function

function DeleteCheckBox(cb.CheckBox)
	delete cb
	NumCBoxes=NumCBoxes-1
end function

function DrawCheckBox(cb.CheckBox)
	color 255,255,255
	rect cb\x,cb\y,13,13
	color 128,128,128
	rect cb\x,cb\y,13-1,13-1
	color 192,192,192
	rect cb\x+1,cb\y+1,13-2,13-2
	color 0,0,0
	rect cb\x+1,cb\y+1,13-3,13-3
	color 255,255,255
	rect cb\x+2,cb\y+2,13-4,13-4
	if cb\down and mousedown(1)=0 then cb\checked=not cb\checked
	cb\down=mousedown(1) and mousex()>=cb\x and mousex()<=cb\x+13 and mousey()>=cb\y and mousey()<=cb\y+13
	if cb\grey or cb\down
		color 192,192,192
		rect cb\x+2,cb\y+2,13-4,13-4
	end if
	if cb\checked then drawimage check,cb\x+3,cb\y+3
end function

function CreateTextBox.TextBox(capt$,tbx,tby,tbw,tbh=0,center=0,greyed=0)
	if tbh=0 then tbh=fontheight()+6
	if center
		tbx=tbx-tbw/2
		tby=tby-tbh/2
	end if
	tb.TextBox=new TextBox
	tb\capt$=capt$
	tb\x=tbx
	tb\y=tby
	tb\w=tbw
	tb\h=tbh
	tb\grey=greyed
	NumTBoxes=NumTBoxes+1
	return tb
end function

function DeleteTextBox(tb.TextBox)
	delete tb
	NumTBoxes=NumTBoxes-1
end function

function DrawTextBox(tb.TextBox)
	setfont FontMenu
	color 255,255,255
	rect tb\x,tb\y,tb\w,tb\h
	color 128,128,128
	rect tb\x,tb\y,tb\w-1,tb\h-1
	color 192,192,192
	rect tb\x+1,tb\y+1,tb\w-2,tb\h-2
	color 0,0,0
	rect tb\x+1,tb\y+1,tb\w-3,tb\h-3
	color 255,255,255
	if tb\grey then color 192,192,192
	rect tb\x+2,tb\y+2,tb\w-4,tb\h-4
	
	if mousedown(1) and mousex()>=tb\x and mousey()>=tb\y and mousex()<=tb\x+tb\w and mousey()<=tb\y+tb\h
		tb\active=1
		else if mousedown(1)
		tb\active=0
	end if
	
	if tb\grey then tb\active=0
	
	if tb\active
		a=getkey()
		if a<>0 and ((32<=a and a<=126)or a=145 or a=146 or(a>160))
			tb\capt$=left(tb\capt$,tb\cursor)+chr(a)+right(tb\capt$,len(tb\capt$)-tb\cursor)
			tb\cursor=tb\cursor+1
		elseif a=8 and tb\cursor>0
			tb\capt$=left(tb\capt$,tb\cursor-1)+right(tb\capt$,len(tb\capt$)-tb\cursor)
			tb\cursor=tb\cursor-1
		elseif a=30 and len(tb\capt$)>tb\cursor
			tb\cursor=tb\cursor+1
		elseif a=31 and tb\cursor>0
			tb\cursor=tb\cursor-1
		end if
		if tb\maxsize
			if len(tb\capt$)>tb\maxsize then tb\capt$=left$(tb\capt$,tb\maxsize)
		end if
	end if
	
	if tb\active
		color 0,0,0
		tb\cflash=tb\cflash+1
		if tb\cflash>10 then tb\cflash=-10
		if tb\cflash>0 then line tb\x+3+stringwidth(left(tb\capt$,tb\cursor)),tb\y+tb\h/2-fontheight()/2,tb\x+3+stringwidth(left(tb\capt$,tb\cursor)),tb\y+tb\h/2+fontheight()/2
	end if
	color 0,0,0
	text tb\x+3,tb\y+tb\h/2,tb\capt$,0,1
end function

function CreateOptButGroup.OptButGroup()
	NomOBGroups=NomOBGroups+1
	return new OptButGroup
end function

function DrawOptButGroup(obg.OptButGroup)
	for i=1 to obg\NumButs
		if i=1 then temp.OptionButton=obg\firstbut else temp=temp\nextbut
		DrawOptionButton(temp,obg)
	next
end function

function CreateOptionButton.OptionButton(OBgroup.OptButGroup,obx,oby,checked=0,center=0,greyed=0)
	ob.OptionButton=new OptionButton
	if center then obx=obx-7:oby=oby-7
	ob\x=obx
	ob\y=oby
	ob\checked=checked
	ob\grey=greyed
	if OBgroup\NumButs=0
		OBgroup\firstbut=ob
	else
		for i=1 to OBgroup\NumButs
			if i=1 then temp.OptionButton=OBgroup\firstbut else temp=temp\nextbut
		next
		temp\nextbut=ob
	end if
	OBgroup\NumButs=OBgroup\NumButs+1
	return ob
end function

function DrawOptionButton(ob.OptionButton,OBgroup.OptButGroup)
	drawimage obimg,ob\x,ob\y,ob\grey or ob\down
	if ob\down and mousedown(1)=0 and ob\grey=0
		for i=1 to OBgroup\NumButs
			if i=1 then temp.OptionButton=OBgroup\firstbut else temp=temp\nextbut
			temp\checked=0
		next
		ob\checked=1
	end if
	if ob\checked then drawimage obimg,ob\x,ob\y,2
	ob\down=mousedown(1) and imagerectcollide(obimg,ob\x,ob\y,0,mousex(),mousey(),1,1)
end function
