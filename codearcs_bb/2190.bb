; ID: 2190
; Author: Nebula
; Date: 2008-01-20 12:11:21
; Title: Quick gui designer (b+)
; Description: Design layouts quick

Writelog("Quick Gui Designer - By Nebula","vdlog.txt","new")

Dim Rectarray(100,3)
Global rectarraylen = 0
Global activerect = 0

Global winwidth = 320
Global winheight = 240

Global doubleclicktime = MilliSecs() 
Global doubleclick ; true/false

setupwindow()

win = CreateWindow("Window",ClientWidth(Desktop())/2-winwidth/2,ClientHeight(Desktop())/2-winheight/2,winwidth+7,winheight+7,Desktop(),1+8)

can = CreateCanvas(0,0,winwidth,winheight,win)
SetBuffer CanvasBuffer(can)
ClsColor 100,100,100
Cls
FlipCanvas(can)


Global gridwidth  = 32
Global gridheight = 32


timer = CreateTimer(50)
drawgrid(can,gridwidth,gridheight)
While we<>$803
we = WaitEvent()
doubleclick(we)
Select we
	Case $101 	;- Key down 
	Case $102 	;- Key up
		If EventData() = 1 Then
			If Confirm("Quit?") Then
				we = $803
			End If
		End If	
		If EventData() = 2 Then gridwidth = 16 : gridheight = 16
		If EventData() = 3 Then gridwidth = 32 : gridheight = 32
		If EventData() = 4 Then gridwidth = 48 : gridheight = 48
		If EventData() = 5 Then gridwidth = 64 : gridheight = 64	
		Cls
		drawgrid(can,gridwidth,gridheight)	
		drawoldrects(can,-1)		
	Case $103 	;- Key stroke 
	Case $201 	;- Mouse down
			;
		If EventData() = 2 Then
			If Rectarraylen > 0 Then
				Rectarray(Rectarraylen,0) = 0
				Rectarray(Rectarraylen,1) = 0
				Rectarray(Rectarraylen,2) = 0
				Rectarray(Rectarraylen,3) = 0
				If rectarraylen => 0 Then Rectarraylen = Rectarraylen - 1
				Cls
				drawgrid(can,gridwidth,gridheight)
				drawoldrects(can,-1)
				FlipCanvas(can)
				SetStatusText(win,Rectarraylen)
			End If
		End If
			;
		If EventData() = 1 Then
			drawrect = True
			x1 = EventX()
			y1 = EventY()
		End If
		If EventData() = 3  And rectarraycollision(EventX(),EventY()) = True Then					
			centerrect = True
		End If
	Case $202 	;- Mouse up
		If EventData() = 1 Then
			;
			x2 = EventX()
			y2 = EventY()
			;
			If x2>x1 And y2>y1 Then
				If x1+6<x2 And y1+6<y2 Then
					If Rectarraylen<0 Then Rectarraylen=0
					rectarraylen = rectarraylen + 1
					Rectarray(Rectarraylen,0) = x1
					Rectarray(Rectarraylen,1) = y1
					Rectarray(Rectarraylen,2) = x2
					Rectarray(Rectarraylen,3) = y2					
				End If
			End If
			;
			drawgrid(can,gridwidth,gridheight)
			drawoldrects(can,-1)
			drawrect = False
			;
			SetStatusText win,Rectarraylen + " items stored"
			;
		End If
		If EventData() = 2 Then
			Cls
			drawgrid(can,gridwidth,gridheight)
			drawoldrects(can,-1)
			FlipCanvas(can)
		End If
		If EventData() = 3 Then
			centerrect = False
		End If
	Case $203 	;- Mouse move		
		If drawrect = True Then		
			SetBuffer CanvasBuffer(Can)			
			Cls
			Color 255,255,255
			x2 = EventX()
			y2 = EventY()
			drawgrid(can,gridwidth,gridheight)
			drawoldrects(can,-1)
			Rect x1,y1,x2-x1,y2-y1
			FlipCanvas(can)			
		End If
		If centerrect = True Then		
			x3 = Rectarray(activerect,0)
			y3 = Rectarray(activerect,1)
			x4 = Rectarray(activerect,2)
			y4 = Rectarray(activerect,3)
			w = x4-x3
			h = y4-y3			
			;
			x5 = EventX()
			y5 = EventY()
			;
			If x5-w/2=>0 And x5+w/2<GadgetWidth(can)
				If y5-h/2>0 And y5+h/2<GadgetHeight(can)
					Rectarray(activerect,0) = x5-w/2
					Rectarray(activerect,1) = y5-h/2
					Rectarray(activerect,2) = x5+w/2
					Rectarray(activerect,3) = y5+h/2
				End If
			EndIf
			;
			Cls
			drawgrid(can,gridwidth,gridheight)
			drawoldrects(can)			
			FlipCanvas(can)
		End If
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
	Case $2002 	;- App Display Change 
	Case $2003 	;- App Begin Modal 
	Case $2004 	;- App End Modal
	Case $2002 	;- App resume 
	Case $4001	;- Timer tick
	;If doubleclick = True Then End
End Select
Wend


Writelog("Window width : " + winwidth,"vdlog.txt","add")
Writelog("Window height : " + winheight,"vdlog.txt","add")
For i=1 To Rectarraylen
	a$ = Str$(Rectarray(i,0)) + ","
	b$ = Str$(Rectarray(i,1)) + ","
	c$ = Str$(Rectarray(i,2)-Rectarray(i,0)) + ","
	d$ = Str$(Rectarray(i,3)-Rectarray(i,1))
	Writelog(a$+b$+c$+d$,"vdlog.txt","add")
Next

If Rectarraylen>0 Then
	ExecFile("notepad.exe vdlog.txt")
End If

Function setupwindow()
w = 320
h = 240

ww = Int(Readlog$("GB_settings.txt",3))
wh = Int(Readlog$("GB_settings.txt",5))

;Notify ww : End

If ww <1 Or wh<1 Then ww = 320 : wh = 240

w = CreateWindow("Select the window dimensions",ClientWidth(Desktop())/2-winwidth/2,ClientHeight(Desktop())/2-winheight/2,winwidth,winheight,Desktop(),16+1+8)

te1 = CreateTextField(128,31,224-128,22,w) ; width
te2 = CreateTextField(128,95,224-128,22,w) ; height
SetGadgetText(te1,ww)
SetGadgetText(te2,wh)
butexit = CreateButton("Go",224,143,305-224,175-143,w,1+8) ;continue
lab1 = CreateLabel("Width : ",15,31,112-15,63-31,w) ; text1
lab2 = CreateLabel("Height : ",16,96,112-16,126-96,w) ; text2

While we<>$803
we = WaitEvent()
Select we
	Case $101 	;- Key down 
	Case $102 	;- Key up 
	Case $103 	;- Key stroke 
	Case $201 	;- Mouse down 
	Case $202 	;- Mouse up 
	Case $203 	;- Mouse move 
	Case $204 	;- Mouse wheel 
	Case $205 	;- Mouse enter 
	Case $206 	;- Mouse leave 
	Case $401 	;- Gadget action
	If EventSource() = butexit Then
		winwidth = TextFieldText(te1)
		winheight = TextFieldText(te2)
		Writelog("Settings for Guibuilder","GB_settings.txt","new")
		Writelog(Trim(Str(winwidth)),"GB_settings.txt","add")
		Writelog(Trim(Str(winheight)),"GB_settings.txt","add")
		FreeGadget(w)
		Return
	End If
	Case $801 	;- Window move 
	Case $802 	;- Window size 
	Case $803 	;- Window close 
	Case $804 	;- Window activate 
	Case $1001 	;- Menu event 
	Case $2001 	;- App suspend 
	Case $2002 	;- App Display Change 
	Case $2003 	;- App Begin Modal 
	Case $2004 	;- App End Modal
	Case $2002 	;- App resume 
	Case $4001	;- Timer tick 
End Select
Wend
End
End Function

Function Rectarraycollision(x,y)
	If Rectarraylen<0 Then Return
	For i = Rectarraylen To 0 Step -1
		If RectsOverlap(Rectarray(i,0),Rectarray(i,1),Rectarray(i,2)-Rectarray(i,0),Rectarray(i,3)-Rectarray(i,1),x,y,1,1) Then
			activerect = i : Return True
		End If
	Next
End Function

Function drawoldrects(can,num = -1)
If Rectarraylen = 0 Then Return
SetBuffer CanvasBuffer(can)
For i=0 To Rectarraylen; To 0 Step -1
If num <> 1
Color 50,50,50
Rect Rectarray(i,0),Rectarray(i,1),Rectarray(i,2)-Rectarray(i,0),Rectarray(i,3)-Rectarray(i,1)
End If
Color 200,200,200
Rect Rectarray(i,0),Rectarray(i,1),Rectarray(i,2)-Rectarray(i,0),Rectarray(i,3)-Rectarray(i,1),0
Next
FlipCanvas(can)
End Function

Function drawgrid(can,width,height)
SetBuffer CanvasBuffer(can)
Color 0,0,0

Repeat
Rect x,y,width,height,0

x=x + width
If x=> GadgetWidth(can) Then y=y + height : x = 0
Until y > GadgetHeight(can)


FlipCanvas(can)
End Function

Function doubleclick(we) ; Fills in the variable doubleclick when double clicked	
	;
	; Place this function in your loop and feed it events.
	;
	; If doubleclick = true then suprise()
	;
	Select we
		Case $201 ; mouse down
		If MilliSecs()-doubleclicktime < 200 Then doubleclick = True Else doubleclick = False
		doubleclicktime = MilliSecs()
	End Select
End Function

Function Writelog(out$,filename$,method$)
Local back$[1024]
method$ = Lower(method$)
If method$ = "add" Then
	f = ReadFile(filename$)
		zi=0
		While Eof(f) = False
			back[zi] = ReadLine(f)
			zi=zi+1
		Wend
	CloseFile(f)
End If
;
f = WriteFile(filename$)
;
If method="add" Then
	For i=0 To zi
		WriteLine(f,back[i])
	Next
End If
;
WriteLine(f,out$)
;
CloseFile(f)
;
End Function

Function Readlog$(filename$,lin)
	If FileType(filename$) <> 1 Then Return -1	
	f = ReadFile(filename$)
		While Eof(f) = False
			cnt = cnt+1			
			a$ = ReadLine(f)
			If lin = cnt Then Return a$
		Wend				
	CloseFile(f)
End Function
