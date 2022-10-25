; ID: 2196
; Author: Nebula
; Date: 2008-01-20 12:33:33
; Title: Popup menu
; Description: Simple popup menu

;
; Blitz + Native Popup menu, No dll or userlib required
;
; (c) R.v.Etten / Nebula in 2004

;
; Currently minor visuals are added and the submenus only go one level deep. The menu's might also sometimes be slightly
; jitterisch.
;
; Functions Draw... can be adjusted to modify the visual apearance of the popup menu

;Todo :
;
; Display key Shotcuts
; Modularize popup control structure
; Put popup collision in timer
; Large menu scrolling (scrollbar?)
; Auto adjust popup layout to size (screen)
; Blanked out text (inactive / disable / enable)
; More user level control function
; More sub menu levels
; Update popup output handling to contents
; Better interactive control
; Inline images and user controls
; Easy user level color control
;
;Todo maybe :
;
; Menu resizing animation
; Skins and oval/round popup menus
; Sound effects
; Fullscreen version
; Menu mode
; Inline animations
; Load data from Ini file

Global winwidth = 640
Global winheight = 480

Global win = CreateWindow("Window",ClientWidth(Desktop())/2-winwidth/2,ClientHeight(Desktop())/2-winheight/2,winwidth,winheight,Desktop(),1+8)

Global popupfontwidth = 8
Global popupfontheight = 13
Global popupclose
Const numpopupmenus = 4
Const numpopupitems = 99
Dim popupdata$(numpopupmenus,numpopupitems,22)
Dim popuppoint(1) ; win,canvas switch
Const popupactive = 0
Const popuptext = 1
Const popupwidth = 2
Const popupheight = 3
Const popupx = 4
Const popupy = 5
Const popupnumitems = 6
Const popuppointer = 7 ; point to another popup menu
Const popupenabled = 8
Const popupline = 9
Const popupmouseontop = 10
Const popupoutdata = 11
Const popupisinitiated = 12 ; goes in x,0,><
Const popupmenuwidth = 13 ; goes in x,0,><
Const popupminwidth = 14 ; goes in x,0,><
Const popuplineactive = 15 ; line?

Global popuplinedr = 55 ; popup dark line
Global popuplinedg = 55
Global popuplinedb = 55
Global popuplinelr = 200 ; popup light line
Global popuplinelg = 200
Global popuplinelb = 200

Global popupcolorr = 150 ; popup surface color
Global popupcolorg = 150
Global popupcolorb = 150
Global popuprectlr = 200 ; light popup rectangle color
Global popuprectlg = 200
Global popuprectlb = 200
Global popuprectdr = 80 ; dark popup rectange color
Global popuprectdg = 80
Global popuprectdb = 80

; Setup popup and sublevels
insertpopupitem(0,0,"Load",1)
insertpopupitem(0,1,"Save",2)
insertpopupline(0,2,3) ; Insert line
insertpopupitem(0,3,"Cut",4)
insertpopupitem(0,4,"Copy",5)
insertpopupitem(0,5,"Paste",6)
insertpopupitem(0,6,"Sub",7)
insertpopupitem(1,0,"Yeah",8)
insertpopupitem(1,1,"this",9)
insertpopupitem(1,2,"is a",10)
insertpopupitem(1,3,"submenu",11)
linkitemtosubmenu(0,4,1) ; menu 0 item 4 gets linked to open menu 1
insertpopupitem(2,0,"Yeah",12)
insertpopupitem(2,1,"this",13)
insertpopupitem(2,2,"is a",14)
insertpopupitem(2,3,"submenu",15)
linkitemtosubmenu(0,6,2) ; menu 0 item 6 gets linked to open menu 2

;
main ; popup is called from this function - one call only
End

Function main()

While we<>$803
we = WaitEvent()
Select we
	Case $101 	;- Key down 
	Case $102 	;- Key up
	If EventData() = 1 Then Exit
	Case $103 	;- Key stroke 
	Case $201 	;- Mouse down 
	Case $202 	;- Mouse up
		If EventData() = 2 Then
			q = popupwindow(num,MouseX(),MouseY(),win)
			If q>0 Then RuntimeError q 
		End If
	Case $203 	;- Mouse move 
	Case $204 	;- Mouse wheel 
	Case $205 	;- Mouse enter 
	Case $206 	;- Mouse leave 
	Case $401 	;- Gadget action 
	Case $801 	;- Window move	
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
End Select
Wend
End Function

Function popupwindow(num,x,y,parent)
	Local win = CreateWindow("",x,y,popupmaxwidth(num),popupmaxheight(num),parent,32)
	Local can = CreateCanvas(0,0,popupmaxwidth(num),popupmaxheight(num),win)
	drawpopup(num,can)
	
	popupclose = False

	Local subwin[5]
	Local subcanvas[5]
	Local subitem[5] ; sub level
	Local subitemmenu[5]
	Local subitemnum[5]
	Local subitemoffsetx[5]
	Local subitemoffsety[5]
	Local subitemactive[5]
	Local subitemorigin[5]

	Local subremovetimer = 0
	Local removesubs = False
	
	olditem = -1
	timer = CreateTimer(40)
	While we<>$803
		we = WaitEvent()
		Select we
			Case $101 	;- Key down 
			Case $102 	;- Key up
			If EventData() = 1 Then Exit
			Case $103 	;- Key stroke 
			Case $201 	;- Mouse down			
				If currentitem = -1 And subitemactive[0] = False Then we=$803
			Case $202 	;- Mouse up
				If subitemactive[0] = True And subitemnum[0] >-1 Then
					If removesubs = False Then
						FreeGadget subwin[0]
						FreeGadget win					
						Return popupoutdata(subitem[0],subitemnum[0])
					End If
				End If
				If subitemactive[0] = False Then
					Return popupoutdata(num,currentitem)
				End If
			Case $203 	;- Mouse move
			currentitem =  popupcollision(num,x,y)
			If subitemactive[0] = True Then subitemnum[0] = popupcollision(subitem[0],subitemoffsetx[0],subitemoffsety[0])
			If subitemactive[0] = True Then drawpopup(subitem[0],subcanvas[0]) :popupsetitemmousestate(num,subitemorigin[0],True) :drawpopup(num,can)
			If subitemactive[0] = True And currentitem <> currentsub And subitemnum[0] = -1 Then currentitem = -1		

			DebugLog "Num : " + num + " : " + currentitem + " : " + subitemnum[0]
			;If  currentitem <> subitemorigin[0] And subitemnum[0] = 0 And subitemactive[0] = True Then
			If subitemnum[0] = -1 And subitemactive[0] = True  Then			
				If  (Not currentitem = subitemorigin[0])
						If removesubs = False Then
							subremovetimer = MilliSecs() + 400
							removesubs = True
							subitemactive[0] = False
						End If
				End If
			End If
			;
			If currentitem >-1
				If olditem <> currentitem Then
					If subitemactive[0] = False Then drawpopup(num,can)
					ex = drawpopupsubitem(num,currentitem,can)
					If ex>-1 And ex<> num Then
						currentsub = currentitem
						If subitemactive[0] = False Then
							If removesubs = True Then
								FreeGadget subwin[0]
							End If
							removesubs = False
							subwin[0] = subpopupwindow(ex,x+popupmaxwidth(num),y+currentitem*popupfontheight,win)
							subcanvas[0] = popuppoint(1)							
							subitem[0] = ex
							;subitemmenu[0]
							subitemoffsetx[0] = x+popupmaxwidth(num)
							subitemoffsety[0] = y+currentitem*popupfontheight
							subitemactive[0] = True
							subitemorigin[0] = currentitem
							subitemnum[0] = 0 ; which level
							olditem = currentitem
						End If
					End If
				End If
			End If
			Case $204 	;- Mouse wheel 
			Case $205 	;- Mouse enter
			currentitem=0
						
			Case $206 	;- Mouse leave
				popupmouseleave(num)
				drawpopup(num,can)
				currentitem = -1
			Case $401 	;- Gadget action 
			Case $801 	;- Window move		
				popupclosegroup()
			Case $802 	;- Window size 
			Case $803 	;- Window close 
			Case $804 	;- Window activate
				popupclosegroup()		
			Case $1001 	;- Menu event
			Case $2001 	;- App suspend	
			Case $2002 	;- App resume		
			Case $2003 	;- App Display Change 
			Case $2004 ;- App Begin Modal 
			Case $2005 ;- App End Modal 
			Case $4001	;- Timer tick
			If popupclose = True Then we=$803
			
			If subremovetimer < MilliSecs() And removesubs = True Then				
				FreeGadget subwin[0] : subitemactive[0] = False : olditem = -1
				removesubs = False
			End If

		End Select
	Wend
	FreeGadget win
End Function

Function linkitemtosubmenu(parent,id,child) ; menu , item, >open menu<
	popupdata(parent,id,popuppointer) = child
End Function
Function insertpopupitem(num,id,newtext$,outid,child=-1)
	popupdata(num,id,popupactive) = True
	popupdata(num,id,popupisinitiated)  = True
	popupdata(num,id,popupminwidth) = 64
	popupdata(num,id,popuptext) = newtext$
	popupdata(num,id,popuppointer) = -1
	popupData(num,id,popupoutdata) = outid
	popupdata(num,id,popuplineactive) = False
	If child <> -1 Then
		popupdata(num,id,popuppointer)  = child ; open another popup menu
	End If
End Function
Function insertpopupline(num,id,outid)
	popupdata(num,id,popupactive) = True
	popupdata(num,id,popupisinitiated)  = True
	popupdata(num,id,popupminwidth) = 64
	popupdata(num,id,popuptext) = " "
	popupdata(num,id,popuppointer) = -1
	popupData(num,id,popupoutdata) = outid
	popupdata(num,id,popuplineactive) = True
End Function

Function deletepopupitem(num,id)
	popupdata(num,id,popupactive) = False
End Function

Function subpopupwindow(num,x,y,parent)
	Local win = CreateWindow("",x,y,popupmaxwidth(num),popupmaxheight(num),parent,32)
	Local can = CreateCanvas(0,0,popupmaxwidth(num),popupmaxheight(num),win)
	drawpopup(num,can)
	popuppoint(0) = win
	popuppoint(1) = can
	Return win
End Function
Function getpopupnumitems(num)
	If popupisinitiated(num) = False Then Return
	cnt=0
	For i=0 To numpopupitems
		If popupactive(num,i) = True Then cnt=cnt+1
	Next
	Return cnt
End Function
Function getpopuptext$(num,id) ; get
	If popupdata(num,id,popupactive) = True Then
		Return popupdata(num,id,popuptext)
	End If
End Function
Function popuptext(num,id,newtext$) ; set
	popupdata(num,id,popuptext) = newtext$
End Function

Function popupactive(num,id)
	; returns if a popup menu item is active
	If popupdata(num,id,popupisinitiated) = True Then Return True
	Return False
End Function
Function popuphassubmenu(num,id)
	If popupactive(num,id) = False Then Return False
	If popupdata(num,id,popuppointer) > -1 Then Return True
	Return False
End Function
Function popupitemisline(num,id)
	If popupactive(num,id) = False Then Return
	If popupdata(num,id,popuplineactive) = True Then Return True
End Function
Function popupcollision(num,x,y)
	w = popupmaxwidth(num)
	h = popupmaxheight(num)
	n = getpopupnumitems(num)
	y=y+10
	If RectsOverlap(MouseX(),MouseY(),1,1,x,y,w-1,h-1) = True Then
		x2 = MouseX()-x
		y2 = MouseY()-y
		y3 = y2/popupfontheight
		popupsetitemmousestate(num,y3,True)
		Return y3
	End If
	Return -1
End Function
Function popupmouseleave(num)
	popupsetitemmousestate(num,0,False)
End Function
Function popupsetitemmousestate(num,id,st)
	If id<0 Then Return
	;erase prev ; optimize!
	For i=0 To numpopupitems
		popupdata(num,i,popupmouseontop) = False
	Next
	;
	popupdata(num,id,popupmouseontop) = st
End Function
Function popupmouseitemcollision(num,id)
	If Int(popupdata(num,id,popupmouseontop)) = True Then Return True
	Return False
End Function
Function popupisinitiated(num)
	If popupdata(num,0,popupisinitiated) = True Then Return True
End Function
Function popupclosegroup()
	popupclose = True
End Function
Function popupoutdata(num,id)
	Return popupdata(num,id,popupoutdata)
End Function

Function drawpopup(num,can) ; visuals
	SetBuffer CanvasBuffer(can)
	ClsColor popupcolorr,popupcolorg,popupcolorb	
	Cls	
	x=0
	y=0
	drawpopuprect(x,y,popupmaxwidth(num),popupmaxheight(num))
	fh = FontHeight()
	For i=0 To numpopupitems
		If popupdata(num,i,popupactive) = True Then
			If popupitemisline(num,i) = True Then
				drawpopupln(x,y,popupmaxwidth(num))
			Else
				drawpopupline(num,x,y,popupdata(num,i,popuptext),popupmouseitemcollision(num,i)) ; draw line non selected
				If popuphassubmenu(num,i) = True Then drawpopuparrow(x+popupmaxwidth(num)-10,y)
			End If
			y=y+fh
		End If
	Next
	FlipCanvas(can)
End Function
Function drawpopuprect(x,y,w,h)
	;	Color 0,0,0 ; Oldskool
	;	Rect x,y,w,h,False
	;	Color 200,200,200
	;	x=x+1 : w=w-2
	;	y=y+1 : h=h-2
	;	Rect x,y,w,h,False
	
	Color popuprectdr,popuprectdg,popuprectdb  ; top to right, top right to bottom (outer)
	Line x,y,x+w,y
	Line x,y,x,y+h
	Color 200,200,200 ; top to right, top right to bottom (inner)
	x=x+1 : w=w-2 : y=y+1 : h=h-2
	Line x,y,x+w,y
	Line x,y,x,y+h
	Color 0,0,0 ; bottom right to top, bottom right to left (outer)
	Line x+w,y+h,x+w,y
	Line x+w,y+h,x,y+h
	Color popuprectdr,popuprectdg,popuprectdb ; bottom right to top, bottom right to left (inner)
	y=y-1 : x=x-1
	Line x+w,y+h,x+w,y-1
	Line x+w,y+h,x+1,y+h
End Function
Function drawpopupline(num,x,y,newtext$,state) ; visuals
	If state = True Then
		Color 50,50,50
		Rect x,y+10,popupmaxwidth(num),FontHeight(),True
	End If
	Color 255,255,255
	;x = x + popupmaxwidth(num)/2
	
	Text x+10,y+10,newtext$,0,0
	
End Function
Function drawpopupln(x,y,w)
	y = y + 10 + (popuplineheight()/2)
	x=x+3
	w=w-9
	Color popuplinedr,popuplinedg,popuplinedb
	Line x,y,x+w,y
	Color popuplinelr,popuplinelg,popuplinelb
	y=y+1
	Line x,y,x+w,y
End Function
Function drawpopupsubitem(num,id,can) ; temp
	If popupdata(num,id,popuppointer) > -1 Then Return popupdata(num,id,popuppointer)
	Return -1
End Function
Function drawpopuparrow(x,y)
	; Needs buffering!!
	y=y+(popuplineheight())+3
	Color 0,0,0
	cnt=cnt+1
	y2# = 7
	Line x,y-4,x,y+4
	For x1=x To x+4
		Line x1,y,x1,y+(y2/2)
		Line x1,y,x1,y-(y2/2)
		y2#=y2#-1.7
	Next
End Function

Function popupmaxwidth(num)
	w=0
	For i=0 To numpopupitems
		If popupactive(num,i) = True Then
			If w < popupitemwidth(num,i) Then w = popupitemwidth(num,i)			
		End If
	Next
	w2 = popupminwidth(num)
	If w2 > w Then Return w2 + 20
	Return w + 20
End Function
Function popupmaxheight(num)
	Return (getpopupnumitems(num) * popupfontheight)+20
End Function
Function popupminwidth(num)
	If popupisinitiated(num) = False Then Return False 
	Return popupdata(num,0,popupminwidth)
End Function
Function popupitemwidth(num,id)
	If popupactive(num,id) = True Then		
		Return (Len(getpopuptext(num,id))) * popupfontwidth
	End If
End Function
Function popupitemheight(num,id)
	Return popupfontheight
End Function
Function popuplineheight()
	Return popupfontheight
End Function
