; ID: 2193
; Author: Nebula
; Date: 2008-01-20 12:21:58
; Title: Animation tool (b+)
; Description: Load Select and view and Animate

;
; Show Animations Tool - Use the command line to load in a bitmap.
;
; By Nebula in 2003
;
; 

.anim
Global animwin
Global animcan

.setupwindow
Global setupwin
Global selectwin
Global Selectcan

Dim filedimensions$(999,2)

mainprogram()

Function Mainprogram()

loadprefs()

If CommandLine$()<>"" Then az$ = Replace(CommandLine$(),Chr(34),"")

If CommandLine$() <> "" Then filename$ = az$
If FileType(filename$) <> 1 Then filename$ = ""
.retry
If Not filename$ <> "" Then filename$ = RequestFile("Select a image","*.*",0)
If FileType(filename$) <> 1 Then End

If validextension(getextension(filename$)) = False Then
	If Confirm("This file might not be a valid picture file, continue?") = False Then
	filename$ = ""
	Goto retry
	End If
End If




ww = 320
wh = 200

setupwin = CreateWindow("Animation setup - by R.v.Etten in 2003",GadgetWidth(Desktop())/2-ww/2,GadgetHeight(Desktop())/2-wh/2,ww,wh,Desktop(),1+8)

loadbutton = CreateButton("Load"      ,0,wh/2-40,60,40,Setupwin)
animbutton = CreateButton("Animate"      ,ww/2-80,wh/2-40,120,40,Setupwin)
reloadbutton = CreateButton("Reload"      ,ww/2-80+120,wh/2-40,120,40,Setupwin)
labelanim = CreateLabel("Animation speed",ww/2-80+20,wh/2+20,80 ,20,setupwin)
animspeed = CreateTextField(              ww/2+20   ,wh/2+20,40 ,20,setupwin)
SetGadgetText animspeed,"100"


a = 64
b = 10
Field1 =  CreateTextField(0+a   ,0+b ,64,20,setupwin)
Field2 = CreateTextField(64+a*2,0+b ,64,20,setupwin)
Updatebutton = CreateButton("Update",64+a*3,0+b ,40,40,setupwin)
Field3 =    CreateTextField(0+a   ,20+b,64,20,setupwin)
Field4 =  CreateTextField(64+a*2,20+B,64,20,setupwin)
;
label1 = CreateLabel("    Width  : ",0    ,0+b  ,44,20,setupwin)
label2 = CreateLabel("    Height : ",0+a*2,0+b  ,44,20,setupwin)
label3 = CreateLabel("    Top    : ",0    ,20+b ,44,20,setupwin)
label4 = CreateLabel("    Left   : ",0+a*2,20+b ,44,20,setupwin)
;
SetGadgetText field1,64
SetGadgetText field2,64
SetGadgetText field3,0
SetGadgetText field4,0

animmode1 =     CreateButton("Loop"     ,25+0,100,100,14,setupwin,3)
animmode2 = CreateButton("Loop back",25+100,100,100,14,setupwin,3)
animmode3 =     CreateButton("No loop"  ,25+200,100,100,14,setupwin,3)
SetButtonState(animmode1,1)

animimage = LoadImage(filename$,2)
iw = ImageWidth(animimage)
ih = ImageHeight(animimage)
Selectwin = CreateWindow("Select Animation cells - lmb + drag - rmb = back to setup",GadgetWidth(Desktop())/2-iw/2,GadgetHeight(Desktop())/2-ih/2,iw,ih,Desktop(),1)
Selectcan = CreateCanvas(0,0,iw,ih,Selectwin)
HideGadget Selectwin

;Notify GadgetHeight(Selectwin)
;Notify ClientHeight(Selectwin)
;Notify ImageHeight(animimage)
;End


cellwidth = Int(TextFieldText(Field1))
cellheight = Int(TextFieldText(Field1)) 



; Align the windows
If ww+iw > GadgetWidth(Desktop()) And wh+ih > GadgetHeight(Desktop()) Then
	SetGadgetShape selectwin,0,0,iw,ih
	Else
	SetGadgetShape setupwin,0,GadgetHeight(Desktop())/2-wh/2,ww,wh
	SetGadgetShape selectwin,ww,GadgetHeight(Desktop())/2-ih/2,iw,ih
End If


x1 = GadgetWidth(Selectwin) + GadgetWidth(Selectwin) - ClientWidth(Selectwin)
y1 = GadgetHeight(Selectwin) + GadgetHeight(Selectwin) - ClientHeight(Selectwin)

Repeat
If x1<iw Then x1 = x1 + 1 Else bone_a = True
If y1<ih Then y1 = y1 + 1 Else bone_b = True
SetGadgetShape Selectwin,GadgetX(Selectwin),GadgetY(Selectwin),x1,y1
Until bone_a=True And bone_b = True



; Get the width and height of the image in the cells
For i=0 To 999
	If Trim(filedimensions$(i,0)) = filename$ Then
		cellwidth = filedimensions(i,1)
		cellheight = filedimensions(i,2)
		SetGadgetText(Field1,cellwidth)
		SetGadgetText(Field2,cellheight)
		Exit
	End If
Next

; Draw the grid
SetGadgetShape Selectcan,0,0,iw,ih
SetBuffer CanvasBuffer(Selectcan)
DrawImage animimage,0,0
drawgrid(iw,ih,cellwidth,cellheight,0,0,10,10)

ShowGadget Selectwin
ActivateWindow setupwin

While we <> $803
we = WaitEvent()
Select we

Case $401 ; Button down

If EventSource() = updatebutton Then
DrawImage animimage,0,0
drawgrid(iw,ih,cellwidth,cellheight,cposx,cposy,ceposx,ceposy)
End If

If EventSource() = animbutton Then

; Save the file info
filerecog = False
For i=0 To 99
	If filedimensions(i,0) = filename$
	filedimensions(i,1) = cellwidth
	filedimensions(i,2) = cellheight
	filerecog = True
End If
Next
If filerecog=False Then
	For i=0 To 99
		If filedimensions(i,0) = "" Then
			filedimensions(i,0) = filename$
			filedimensions(i,1) = cellwidth
			filedimensions(i,2) = cellheight
			Exit
		End If
	Next
End If

saveprefs()

; Do the anim
cellwidth = Int(TextFieldText(Field1))
cellheight = Int(TextFieldText(Field2)) 
a_speed = Int(TextFieldText(animspeed))
If a_speed>1 And a_speed<5000 Then
If cposx>-1 And cposy>-1 And ceposx*cellwidth<iw And ceposy*cellheight<ih Then
a = -1
b = -1
For y=0 To ih/cellheight
For x=0 To (iw-cellwidth)/(cellwidth)
a = a + 1
b = b + 1
If (x= cposx And y = cposy) Then start_frame = a 
If (x= ceposx And y = ceposy) Then End_frame = b 
Next
Next

If start_frame > -1 And End_frame > start_frame Then
If ButtonState(animmode1) = 1 Then am = 1
If ButtonState(animmode2) = 1 Then am = 2
If ButtonState(animmode3) = 1 Then am = 3
;showanimation(filename$,cellwidth,cellheight,startframe,endframe,animspeed_ms)
showanimation(filename$,cellwidth,cellheight,start_frame,end_frame,a_speed,am)
SetBuffer CanvasBuffer(Selectcan)

Else : Notify "Error in start/end of frame (Drag the mouse across the bitmap viewer)" + start_frame + " : " + End_frame
End If ; start end frame
Else : Notify "error in map position"
End If ; map position
Else : Notify "error in anim speed"
End If ; speed
End If ; button

; Update image with new dimensions
If EventSource() = Field1 Or EventSource() = Field2 Then
	cellwidth = Int(TextFieldText(Field1))
	cellheight = Int(TextFieldText(Field2))
	updateshit = True
End If

; reload the stuff
If EventSource() = reloadbutton Then
	animimage = LoadImage(filename$,2)
	iw = ImageWidth(animimage)
	ih = ImageHeight(animimage)
	SetBuffer CanvasBuffer(selectcan)
	DrawImage animimage,0,0
	drawgrid(iw,ih,cellwidth,cellheight,cposx,cposy,ceposx,ceposy)
End If

; Load a new image
If EventSource() = loadbutton Then	
	oldfilename$ = filename$
	filename$ = RequestFile("Select a image","*.*",0)
	If FileType(filename$) <> 1 Then filename$ = oldfilename$
	If validextension(getextension(filename$)) = False Then
		If Confirm("This file might not be a valid picture file, continue?") = False Then
			filename$ = oldfilename$
		End If
	End If
	animimage = LoadImage(filename$,2)
	; Get the width and height of the image in the cells
	For i=0 To 999
		If Trim(filedimensions$(i,0)) = filename$ Then
			cellwidth = filedimensions(i,1)
			cellheight = filedimensions(i,2)
			SetGadgetText(Field1,cellwidth)
			SetGadgetText(Field2,cellheight)
			Exit
		End If
	Next
	iw = ImageWidth(animimage)
	ih = ImageHeight(animimage)
	FreeGadget Selectwin
	Selectwin = CreateWindow("Select Animation cells - lmb + drag - rmb = back to setup",GadgetWidth(Desktop())/2-iw/2,GadgetHeight(Desktop())/2-ih/2,iw,ih,Desktop(),1)
	Selectcan = CreateCanvas(0,0,iw,ih,Selectwin)
	SetBuffer CanvasBuffer(Selectcan)
	ClsColor 0,0,0
	Cls
	DrawImage animimage,0,0
	drawgrid(iw,ih,cellwidth,cellheight,cposx,cposy,ceposx,ceposy)
End If

Case $203 ; mouse move

If EventSource() = selectcan And selectionstarted = True Then

	
	ceposx = EventX()/cellwidth : ceposy = EventY()/cellheight

	If EventX() > iw Then Selectionstarted = False : ceposx = ceposx -1
	If EventY() > ih Then Selectionstarted = False : ceposy = ceposy - 1
	If EventX() < 0 Then Selectionstarted = False
	If EventY() < 0 Then Selectionstarted = False

	
	SetStatusText setupwin,"Start : " + cposx + ","+cposy + "  End : " + ceposx + "," + ceposy
	ClsColor 0,0,0
	Cls
	DrawImage animimage,0,0
	Color 220,0,0	
	drawgrid(iw,ih,cellwidth,cellheight,cposx,cposy,ceposx,ceposy)
End If

Case $202;  Mouse up
;
If EventData() = 2 Then ActivateWindow setupwin
;
If (EventSource() = selectcan And selectionstarted = True) And (EventData() <> 2)
	Selectionstarted = False
	ceposx = EventX()/cellwidth : ceposy = EventY()/cellheight
		 za$ = "Start : " + cposx + ","+cposy + "  End : " + ceposx + "," + ceposy


		
		x = 0 : y = 0 : counter = 0
		sf = 0 : ef = 0 : crap = False
		While crap=False				
		If (x/cellwidth = cposx) And (y/cellheight = cposy) Then sf = counter
		If (x/cellwidth = ceposx) And (y/cellheight = ceposy) Then ef = counter
		x = x + cellwidth
		counter = counter + 1
		If x > iw Then x = 0 : y=y+cellheight
		If y > ih Then crap = True		
		Wend
		 
	za$ = za$ + " ; Frame : " + sf + " to " + ef
	SetStatusText setupwin,za$
	ClsColor 0,0,0
	Cls
	DrawImage animimage,0,0
	Color 220,0,0
	a = TextFieldText(Field1)
	b = TextFieldText(Field2)
	x = cposx*cellwidth
	y = cposy*cellheight
	start_frame = -1
	End_frame = -1
	drawgrid(iw,ih,cellwidth,cellheight,cposx,cposy,ceposx,ceposy)
End If

Case $201 ; mouse down
If (EventSource() = selectcan) And (EventData() <> 2) Then
	cellwidth = Int(TextFieldText(Field1))
	cellheight = Int(TextFieldText(Field2)) 
	Selectionstarted = True
	cposx = EventX()/cellwidth : cposy = EventY()/cellheight
	SetStatusText setupwin,"Start : " + cposx + ","+cposy + "  End : " + ceposx + "," + ceposy
End If


End Select
Wend


End

End Function

Function showanimation(filename$,cellwidth,cellheight,startframe,endframe,animspeed_ms,animMode)
;animmode 0 = loop , 1 = loopback, 2 = stopafterplay
ww = 320
wh = 200


animwin = CreateWindow("Viewing Anim - lmb + drag = movement - rmb to exit",GadgetWidth(Desktop())/2-ww/2,GadgetHeight(Desktop())/2-wh/2,ww,wh,Desktop(),1)

animcan = CreateCanvas(0,0,ww,wh,animwin)

aba =LoadImage(filename$)
mw = ImageWidth(aba)
mh = ImageHeight(aba)
FreeImage aba

Stepanim = 1

x = mw/cellwidth
y = mh/cellheight
numcells = x*y

SetBuffer CanvasBuffer(animcan)
ClsColor 0,0,0
Cls

anim = LoadAnimImage(filename$,cellwidth,cellheight,0,numcells-1)

timer = CreateTimer(60)

HandleImage anim,cellwidth/2,cellheight

Cls
DrawImage anim,ww/2,wh/2,counter
counter = counter + 1
If counter > Endframe Then counter = startframe
FlipCanvas(animcan)

counter = startframe

Exitthis = False
ResetTimer(timer)

brrx = 0
brry = 0
animcounter = MilliSecs() + animspeed_ms + 300
DrawImage anim,(ww/2),(wh/2),counter
Repeat
	we = WaitEvent()
	Select we
		Case $803
		If EventSource() = animwin Then Exit
		Case $4001
		;If TimerTicks(timer) > animspeed_ms Then
		Cls
		If domoveanim =  False Then		
			DrawImage anim,(ww/2),(wh/2),counter
			Else			
			DrawImage anim,brrx,brry,counter
		End If

		If animcounter < MilliSecs() Then		
		counter = counter + Stepanim
		animcounter = MilliSecs() + animspeed_ms
		End If
		
		If counter > Endframe Then
			If animmode = 1 Then counter = startframe
			If animmode = 2 Then Stepanim = -1 : counter = Endframe -1
			If animmode = 3 Then Stepanim = 0 : counter = Endframe
		End If
		If counter < startframe Then
			If animmode = 1 Then counter = startframe
			If animmode = 2 Then Stepanim = 1 : counter = startframe + 1
			If animmode = 3 Then Stepanim = 0 : counter = Endframe
		End If
		FlipCanvas(animcan)		
		;End If
		Case $201 ; mousedown
		If EventData() = 1 Then
			domoveanim = True
			brrx = EventX()
			brry = EventY()
			HandleImage anim,0,0
		End If
		If EventData() = 2 Then Exitthis = True
		Case $202 ; mouseup
		domoveanim = False
		brrx = 0
		brry = 0
		HandleImage anim,cellwidth/2,cellheight
		Case $203 ; mousemove
		If domoveanim = True Then
		brrx = EventX()
		brry = EventY()		
		End If
		
		Case $4001
	End Select
	
Until Exitthis = True Or KeyDown(1) = True

FreeGadget animwin
FreeImage anim

End Function

Function drawgrid(iw,ih,cellwidth,cellheight,cposx,cposy,ceposx,ceposy)
	a = cellwidth
	b = cellheight
	x = cposx*cellwidth
	y = cposy*cellheight
	counter = 1
	SetBuffer CanvasBuffer(Selectcan)
	Repeat
		If a>-1 And a<129 And b>-1 And b<129
			If x = cposx*cellwidth And y=cposy*cellheight Then Text x+cellwidth/2,y+cellheight/2,"Start",1,1 
			If x = ceposx*cellwidth And y=ceposy*cellheight Then Text x+cellwidth/2,y+cellheight/2,"End",1,1 
			Rect x+3,y+3,cellwidth-6,cellheight-6,0
			Rect x+4,y+4,cellwdith-8,cellheight-8,0
		End If
		x = x + cellwidth
		If (x => ceposx*cellwidth+1) And (y => ceposy*cellheight) Then Exit
		If x>iw Then x = 0 : y=y+cellheight
	Until y>ih
	Color 0,0,0
	x = 0 : y = 0
	Repeat
		If a>-1 And a<129 And b>-1 And b<129
			Rect x,y,cellwidth,cellheight,0
		End If
		x = x + cellwidth
		If x>iw Then x = 0 : y=y+cellheight
	Until y>ih
	FlipCanvas(Selectcan)
End Function

Function saveprefs(); Save the last used filenames and cell dimensions
;
If FileType(CurrentDir()+"Animviewcfg") = 0 Then CreateDir("Animviewcfg")
;
f = WriteFile(CurrentDir()+"Animviewcfg\fileinfo.txt")
For i=0 To 999
	If filedimensions$(i,0) <> "" Then
		WriteLine(f,filedimensions$(i,0))
		WriteLine(f,filedimensions$(i,1))
		WriteLine(f,filedimensions$(i,2))
	End If
Next
End Function
Function loadprefs() ; Load the history of used filenames and cell dimensions

If FileType(CurrentDir()+"animviewcfg\fileinfo.txt") = 0 Then Return
;
f = ReadFile(CurrentDir()+"animviewcfg\fileinfo.txt")
counter = 0
While Eof(f)=False
	For ii=0 To 2
		a$ = ReadLine(f)
		If a$ = "" Then Exit
		filedimensions(counter,ii) = a$
	Next
	counter = counter + 1
Wend
CloseFile(f)
End Function

Function getextension$(filename$) ; Returns the extension minus the .
	lastdir = 1
	For i=1 To Len(filename$)
		If Mid$(filename$,i,1) = "." Then Lastdir = i
	Next
	If Lastdir > 1 Then Lastdir = Lastdir + 1
	For i=Lastdir To Len(filename$)
		a$ = a$ + Mid(filename$,i,1)
	Next
	Return a$
End Function

Function validextension(ext$)
	If ext$ = "bmp" Then Return True
	If ext$ = "png" Then Return True
	If ext$ = "jpg" Then Return True
	Return False
End Function
