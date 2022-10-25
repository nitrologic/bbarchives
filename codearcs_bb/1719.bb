; ID: 1719
; Author: Mag
; Date: 2006-05-26 04:57:27
; Title: Max menu
; Description: A colapable menu like 3ds max

;==================================================================
Const	ProjectTitle$	= "Max Menu"    
Const 	Author$			= "Mag.ic"
Const	Date$			= "Jan 2004"              
Const	Version$		= "0.01"           
;==================================================================
;SETTING
Global FormBGColor=15526360
Global CaptionBGColor=$C0C0C0
Global CaptionFGColor=$000000
Global ContentBGColor=$F0FFFF
Global ContentFGColor=$000080
Global AutoColapseMyMenu=0 						;only one menu uncollapse
Const TotalMyMenu=6 							;total of collapse menu to create
Dim Mymenu$(TotalMyMenu):Dim MymenuCollapse(TotalMyMenu):Dim MymenuHeight(TotalMyMenu)
	MyMenu$(1)="General":MymenuCollapse(1)=0:MymenuHeight(1)=80
	MyMenu$(2)="Gadget":MymenuCollapse(2)=0:MymenuHeight(2)=150
	MyMenu$(3)="Property":MymenuCollapse(3)=0:MymenuHeight(3)=294
	MyMenu$(4)="Selection Tree":MymenuCollapse(4)=0:MymenuHeight(4)=200
	MyMenu$(5)="Menu Editor":MymenuCollapse(5)=0:MymenuHeight(5)=100
	MyMenu$(6)="Info & Setting":MymenuCollapse(6)=1:MymenuHeight(6)=275
;==================================================================

;some general use global
Global Bigmain 									;For create minimize window
Global main 									;mainwindow handle
Global lebarmain=300
Global heightmain=400 							;ClientHeight(Desktop())-30
Global maincanvas 								;main canvas handle
Global Event
Global MouseStillDown 							;for drag
Global RememberMouseX 							;for drag
Global RememberMouseY 							;for drag
Global offset 									;offset of menu drawing
Global BezaY  									;temporary offset during draging of menu

	
Function createmainwindow()
	Bigmain=CreateWindow(ProjectTitle$,0,-100,heightmain,0,Desktop(),3)
	main=CreateWindow(ProjectTitle$,0,0,lebarmain,heightmain,Bigmain,17)
	maincanvas=CreateCanvas(0,0,ClientWidth(main),ClientHeight(main),main)
	SetGadgetLayout maincanvas,1,1,1,1
	SetBuffer CanvasBuffer(maincanvas)
	SetFont LoadFont(Arial,14,1)
	ClsColor 0,0,FormBGColor
End Function
createmainwindow()

Repeat
	event=WaitEvent(1)
	DrawMaxMenu()
	Select Event
	Case $803
		If EventSource()=main Then End
	Case $802 ;size
		ActivateGadget main
	Case $801 ;move
		SetGadgetShape Bigmain,GadgetX(main),-100,heightmain,0
	End Select
Forever

Function DrawMaxMenu()
	Cls
	y=2+offset+BezaY
	For k=1 To TotalMyMenu
		;draw caption
		Color 0,0,CaptionBGColor
		Rect 2,y+1,lebarmain-10,15
		Color 0,0,CaptionFGColor
		Rect 2,y+1,lebarmain-10,15,0
		Text 15,y+2,Mymenu$(k)
		;drag tracking
		If MouseDown(1) And MouseStillDown=0 Then
			MouseStillDown=1
			RememberMouseX=MouseX(maincanvas)
			RememberMouseY=MouseY(maincanvas)
		EndIf
		;update BezaY for draging show. BezaY is reset when mouseup and the value is added to offset
		If MouseStillDown=1 And RememberMouseX=MouseX(maincanvas)<>MouseX(maincanvas) And RememberMouseY=MouseY(maincanvas)<>MouseY(maincanvas) Then
			BezaX=MouseX(maincanvas)-RememberMouseX
			BezaY=MouseY(maincanvas)-RememberMouseY
			If offset+Bezay>0
				offset=0:BezaY=0
			EndIf
		EndIf	
		If Event=$202 Then ;mouseup
			MouseStillDown=0
			If MouseY(maincanvas)<>RememberMouseY
				offset=offset+BezaY:BezaY=0
				Click=0
				Event=0
			Else			
				Click=1
			EndIf
		EndIf
		;collession caption - only when no drag
		If k<>TotalMyMenu And click=1 And MouseStillDown=0 And Event=$202 And MouseX(maincanvas)>2 And MouseX(maincanvas)<2+lebarmain-10 And MouseY(maincanvas)>y+1 And MouseY(maincanvas)<y+1+15 Then
			If MymenuCollapse(k)=1 Then
				MymenuCollapse(k)=0
			Else
				If AutoColapseMyMenu
					For kk=1 To TotalMyMenu-1
						MymenuCollapse(kk)=0
					Next
				EndIf
				MymenuCollapse(k)=1
			EndIf
			;need DoEvent() trick
			MoveMouse MouseX(maincanvas)+1,MouseY(maincanvas),maincanvas
			MoveMouse MouseX(maincanvas)-1,MouseY(maincanvas),maincanvas
		EndIf
		;draw + - except info
		If k<TotalMyMenu 
			Color 0,0,CaptionFGColor
			If MymenuCollapse(k)=1 Then
				Line 5,y+9,11,y+9		
			Else
				Line 5,y+9,11,y+9
				Line 8,y+6,8,y+12
			EndIf
		EndIf
		;draw the boder of uncolapse menu
		If MymenuCollapse(k)=1 Then
			Rect 2,y+1,lebarmain-10,MymenuHeight(k)-5,0
			Color 0,0,ContentBGColor
			Rect 3,y+1+15,lebarmain-12,MymenuHeight(k)-6-15
			;POINT TO FUNCTION RELATED TO OPEN MENU
			Select K
			Case 1
				DrawSection1(4,y+17)
			Case 2
				DrawSection2(4,y+17)
			Case 3
				DrawSection3(4,y+17)
			Case 4
				DrawSection4(4,y+17)
			Case 5
				DrawSection5(4,y+17)
			Case 6
				DrawSection6(4,y+17)
			End Select
			y=y+MymenuHeight(k)
		Else
			y=y+20
		EndIf
	Next
	;count total height
	If offset>=0 Then
		total=y-offset
	Else
		total=y+Abs(offset)
	EndIf
	;flip but only if no re offset
	If offset+total<heightmain-25 Then
		offset=offset+((heightmain-25)-(offset+total))
	Else
		FlipCanvas(maincanvas)
	EndIf
End Function

;=========================

Function DrawSection1(x,y)
	Color 0,0,ContentFGColor
	Rect x,y,10,10,1
	MymenuHeight(1)=150
End Function
Function DrawSection2(x,y)
	Color 0,0,ContentFGColor
	Rect x,y,10,10,1
End Function
Function DrawSection3(x,y)
	Color 0,0,ContentFGColor
	Rect x,y,10,10,1
End Function
Function DrawSection4(x,y)
	Color 0,0,ContentFGColor
	Rect x,y,10,10,1
End Function
Function DrawSection5(x,y)
	Color 0,0,ContentFGColor
	Rect x,y,10,10,1
End Function
Function DrawSection6(x,y)
	Color 0,0,ContentFGColor
	Rect x,y,10,10,1
End Function
