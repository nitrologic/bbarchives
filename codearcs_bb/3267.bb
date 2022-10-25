; ID: 3267
; Author: Pakz
; Date: 2016-04-23 20:48:42
; Title: Sprite Editor 48x48
; Description: About a 1000 lines now.

.init
; main code ..............>>

; map width
Global mw = 48
Global mh = 48
; tile width
Global tw = 640/mw
Global th = 480/mh

; closed list (floodfill)
Type ol
	Field x
	Field y
End Type
Type cl
	Field x
	Field y
End Type

; for the undo
Type unre
	Field map[48*48] ; sprite sheet width x height <<<<<<<<<
	Field cols[12*3]
	Field colsf#[12*3]
End Type
Global undoredo.unre = New unre

Global win = CreateWindow("Sprite Editor 48x48 Example",100,100,800,600,0,1) 
Global txt = CreateTextArea(0,20,800,520,win) 
Global txt2 = CreateTextArea(0,20,800,520,win)
Global tab = CreateTabber(0,0,800,20,win)
Global can = CreateCanvas(0,20,800,600,win)

HideGadget(txt2)
HideGadget(can)

Dim cols(10,3)
Dim colsf#(10,3) ; the float colors for
				; brightness ect
				
; This array contains the colors that are protected
; true/false
Dim protcol(10)


Dim map(mw,mh)
Global canim = CreateImage(800,600)
SetBuffer ImageBuffer(canim)
Global font=LoadFont("verdana.ttf",12)
SetFont font 


; shade map part
Dim shademap(mw,mh) ; create the shading true/false pixelmap x 

; make the shadegrid
Local x2=0
For y=0 To mh-1 Step 2
For x=0 To mw-1 Step 2
	shademap(x,y) = True
	shademap(x+1,y+1) = True
Next
Next

Global brushindex=4
Global brushsize=4
Global lcmx ; previous mouse pos
Global lcmy
Global cmx
Global cmy

Global tileim = CreateImage(tw,th,11)
Global tileimbig = CreateImage(32,32,11)

refreshtileimages(True)

InsertGadgetItem tab,0,"MonkeyX Array",0
InsertGadgetItem tab,1,"Visual Editor",1
InsertGadgetItem tab,2,"Color Data",2


Global mytxt$

Global screen$="txt"

Global scatter = False
Global explode = False
Global normal = True
Global smudge = False
Global smudgeint = 5 ; smudge intensity (higher = less)
Global shade = False
Global mirror = False
Global pickcolor = False
Global ctrldown = False

Global linemode = False
Global linedrawn = False
Global lsx1
Global lsy1
Global lsx2
Global lsy2
Global lsxm1
Global lsym1
Global lsxm2
Global lsym2


makemonkeycode
makecolorcode

Global timer = CreateTimer(60)
Local br#,bg#,bb# ; for the color manip

updateinterface
addundo
.main
Repeat 
	we = WaitEvent()
	If we=$101 ; keydown
		If EventData()=29 ; left ctrl key
			pickcolor = True 
			ctrldown = True
			updateinterface
		End If		
	End If
	If we=$102;keyup
		If screen="canvas"
		If EventData()=29 ; ctrl key - pick color
			pickcolor = False
			ctrldown = False
			updateinterface
		End If		
		If EventData()=46 ;c cls
			For y=0 To mh
			For x=0 To mw
				map(x,y)=0
			Next
			Next
			addundo
			refreshtileimages(True)
			updateinterface
		End If
		If EventData() = 23;i key mirror
			If mirror = True Then mirror = False Else mirror = True
			updateinterface
		End If
		If EventData()=200 ; cursor up ; color select
			If brushindex>0 Then brushindex=brushindex-1
			updateinterface
		End If
		If EventData()=208;cursor down
			If brushindex<10 Then brushindex=brushindex+1
			updateinterface
		End If
		If EventData()=59 ; f1 darken color
			colsf(brushindex,0) = colsf(brushindex,0)/100*92
			colsf(brushindex,1) = colsf(brushindex,1)/100*92
			colsf(brushindex,2) = colsf(brushindex,2)/100*92
			If colsf(brushindex,0) < 0 Then colsf(brushindex,0) = 0
			If colsf(brushindex,1) < 0 Then colsf(brushindex,1) = 0
			If colsf(brushindex,2) < 0 Then colsf(brushindex,2) = 0
			cols(brushindex,0) = colsf(brushindex,0)
			cols(brushindex,1) = colsf(brushindex,1)
			cols(brushindex,2) = colsf(brushindex,2)
			addundo
			refreshtileimages
			updateinterface
		End If		
		If EventData()=60 ; brighten
			colsf(brushindex,0) = colsf(brushindex,0)/100*108
			colsf(brushindex,1) = colsf(brushindex,1)/100*108
			colsf(brushindex,2) = colsf(brushindex,2)/100*108
			If colsf(brushindex,0) > 255 Then colsf(brushindex,0) = 255
			If colsf(brushindex,1) > 255 Then colsf(brushindex,1) = 255
			If colsf(brushindex,2) > 255 Then colsf(brushindex,2) = 255
			cols(brushindex,0) = colsf(brushindex,0)
			cols(brushindex,1) = colsf(brushindex,1)
			cols(brushindex,2) = colsf(brushindex,2)						
			addundo
			refreshtileimages			
			updateinterface			
		End If		
			If EventData()=35 ; h shading mode
				If shade=True Then shade=False Else shade=True
				updateinterface
			End If
			If EventData()=24;o outline colorarea				
				outlinefill
				updateinterface
				addundo
			End If
			If EventData()=22;u undo
				undoback
				refreshtileimages
				updateinterface
			End If			
			If EventData()=33;f floodfill
				floodfill()
				updateinterface
				addundo
			End If		
			If EventData() = 50;m s(m)udge
				If smudge=True Then smudge = False Else smudge=True
				If smudge = True
					scatter=False
					explode=False
					normal=False
					Else
					normal = True
				End If
				updateinterface				
			End If
			If EventData()=49;n normal brush
				normal = True
				scatter = False
				explode = False
				updateinterface
			End If
			If EventData()=38;l line mode
				If linemode = True Then linemode = False Else linemode = True
				If linemode = True Then linedrawn=True
				updateinterface
			End If
			If EventData()=18;e
				If explode = False Then explode=True Else explode = False
				If explode = True Then scatter = False
				If explode = True Then normal = False
				If explode = False Then normal = True
				updateinterface
			End If
			If EventData()=25 ;p pick color
				If RectsOverlap(cmx,cmy,1,1,0,0,(mw+1)*tw,(mh+1)*th)
					brushindex = map(cmx/tw,cmy/th)
					updateinterface
				End If
			End If
			If EventData() = 31 ; s scatter
				If scatter = False Then scatter = True Else scatter = False
				If scatter = True Then explode = False
				If scatter = True Then normal = False
				If scatter = False Then normal = True
				updateinterface
			End If
			If EventData()>=2 And EventData()<=10 ; 1 to 9
				brushsize = EventData()-1
				updateinterface
			End If
		End If
	End If
	If we=$201;mosuedown
		If screen = "canvas"
		If ctrldown=False
		If RectsOverlap(cmx,cmy,1,1,0,0,(mw+1)*tw,(mh+1)*th)
			lsx1 = cmx
			lsy1 = cmy
			linedrawn=False
		End If
		End If
		End If
	End If
	If we=$202;mouseup
		If EventSource() = can
			If ctrldown=True
				If EventData()=1
					If RectsOverlap(cmx,cmy,1,1,0,0,(mw+1)*tw,(mh+1)*th)
						brushindex = map(cmx/tw,cmy/th)
						updateinterface
					End If				
				End If
			End If
			If ctrldown=False
			If EventData() = 1
				If RectsOverlap(cmx,cmy,1,1,0,0,(mw+1)*tw,(mh+1)*th)
				If linemode = True
					lsx2 = cmx
					lsy2 = cmy
					makeline(lsx1,lsy1,lsx2,lsy2)
					If mirror = True
						If lsx1<320
							lsxm1 = (mw*tw)-lsx1
							lsxm2 = (mw*tw)-lsx2
						Else
							lsxm1 = -lsx1+(mw*tw)
							lsxm2 = -lsx2+(mw*tw)
						EndIf
						lsym1 = lsy1
						lsym2 = lsy2
						makeline(lsxm1,lsym1,lsxm2,lsym2)
					End If
					updateinterface
					linedrawn=True
				End If
				Else
					linedrawn=True
				End If
				; undestructable checkboxes
				If RectsOverlap(cmx,cmy,1,1,712,0,16,11*32)					
					If protcol(cmy/32) = True
						protcol(cmy/32) = False
						Else
						protcol(cmy/32) = True
					End If					
					updateinterface
				End If				
				If RectsOverlap(cmx,cmy,1,1,680,0,32,11*32)
					brushindex=cmy/32
					updateinterface
				End If
				If linemode=False
				If RectsOverlap(cmx,cmy,1,1,0,0,(mw+1)*tw,(mh+1)*th)
					brushdown(cmx,cmy,brushindex)
					If mirror = True
						If cmx<320
							x2 = 640-cmx						
						Else
							x2 = -cmx+640
						EndIf
						y2 = cmy
						brushdown(x2,y2,brushindex)
					End If					
					updateinterface
				End If
				End If

			End If
			If EventData() = 2
				If RectsOverlap(cmx,cmy,1,1,680,0,32,11*32)
					If RequestColor(cols(brushindex,0),cols(brushindex,1),cols(brushindex,2))=True
						brushindex=cmy/32
						cols(brushindex,0) = RequestedRed()
						cols(brushindex,1) = RequestedGreen()
						cols(brushindex,2) = RequestedBlue()
						colsf(brushindex,0) = RequestedRed()
						colsf(brushindex,1) = RequestedGreen()
						colsf(brushindex,2) = RequestedBlue()

						refreshtileimages
						updateinterface
						addundo
					End If
				End If
				; undestructable checkboxes
				If RectsOverlap(cmx,cmy,1,1,712,0,16,11*32)					
					For i=0 To 10
					protcol(i)=False
					Next
					If protcol(cmy/32) = True
						protcol(cmy/32) = False
						Else
						protcol(cmy/32) = True
					End If
					updateinterface
				End If					
				If linemode = False
				If RectsOverlap(cmx,cmy,1,1,0,0,(mw+1)*tw,(mh+1)*th)
					brushdown(cmx,cmy,0)
					If mirror = True
						If cmx<320
							x2 = 640-cmx						
						Else
							x2 = -cmx+640
						EndIf
						y2 = cmy
						brushdown(x2,y2,0)
					End If					
					updateinterface
				End If
				End If
			End If
			If RectsOverlap(cmx,cmy,1,1,0,0,(mw+1)*tw,(mh+1)*th)
				addundo
			End If			
			End If
		End If
	End If
	If we=$203;mousemove
		If EventSource()=can
			lcmx = cmx
			lcmy = cmy
			cmx = EventX()
			cmy = EventY()	
			If ctrldown=False		
			If MouseDown(1) = True
			If linemode=False
			If RectsOverlap(cmx,cmy,1,1,0,0,(mw+1)*tw,(mh+1)*th)
				brushdown(cmx,cmy,brushindex)
				If mirror = True
					If cmx<320
						x2 = 640-cmx						
					Else
						x2 = -cmx+640
					EndIf
					y2 = cmy
					brushdown(x2,y2,brushindex)
				End If				
				updateinterface
			End If			
			End If
			End If
			If MouseDown(2) = True
			If linemode=False
			If RectsOverlap(cmx,cmy,1,1,0,0,(mw+1)*tw,(mh+1)*th)
				;map(cmx/tw,cmy/th) = 0
				brushdown(cmx,cmy,0)
				If mirror = True
					If cmx<320
						x2 = 640-cmx						
					Else
						x2 = -cmx+640
					EndIf
					y2 = cmy
					brushdown(x2,y2,0)
				End If
				updateinterface
			End If
			End If
			End If
			End If
			updateinterface			
		End If
	End If
	If we=$401;gadgetaction
		If EventSource() = tab
			sg = SelectedGadgetItem(tab)
			If sg = 0
				screen="txt"
				makemonkeycode
				makecolorcode
				HideGadget can
				HideGadget txt2
				ShowGadget txt
			End If
			If sg = 1
				screen="canvas"
				readmonkeycode
				readcolorcode
				HideGadget txt
				HideGadget txt2
				ShowGadget can
				refreshtileimages
				updateinterface
				FlipCanvas can
			End If
			If sg = 2
				screen="txt2"
				makecolorcode
				makemonkeycode
				HideGadget txt
				HideGadget can
				ShowGadget txt2
			End If
		End If
	End If
	If we=$4001
	End If	
	If we=$803 Then Exit 
Forever 
End

.drawfuncs

Function updateinterface()
	SetBuffer ImageBuffer(canim)
	Cls
	Color 255,255,255
	For y=0 To mh-1
	For x=0 To mw-1
		DrawImage tileim,x*tw,y*th,map(x,y)
	Next
	Next
	If mirror = True 
		For y=0 To 480-16 Step 16
			cx=((mw/2)*(tw))+tw/2
			Color 255,255,255
			Line cx,y,cx,y+8
			Color 20,20,20
			Line cx,y+8,cx,y+16
		Next
	End If
	Color 255,255,255
	For y=0 To 10 
		DrawImage tileimbig,680,y*32,y
		If brushindex = y
			Rect 681,y*32,31,31,False
		End If
		; the indestructable checkbox
		Rect 712,y*32,16,32,False
		If protcol(y) = True
			Rect 714,y*32,12,30,True
		End If		
	Next	
	For y=0 To mh-1
	For x=0 To mw-1
		a = map(x,y)
		Color cols(a,0),cols(a,1),cols(a,2)
		Rect x*2+660,y*2+370,2,2
	Next
	Next
	; draw the line
	If linemode = True And linedrawn = False
	Color 255,255,0
	Line lsx1,lsy1,cmx,cmy
	End If
	; draw the brush view
	Color 255,255,255
	x1 = cmx-brushsize*tw/2
	y1 = cmy-brushsize*th/2	
	Rect cmx-brushsize*tw/2,cmy-brushsize*th/2,brushsize*tw,brushsize*th,False	
	Color 20,20,20
	Rect x1,y1,2,2
	Rect x1,y1+brushsize*th,2,2
	Rect x1+brushsize*tw,y1,2,2
	Rect x1+brushsize*tw,y1+brushsize*th,2,2
	Color 255,255,255
		
;
	If normal = True
		Text 10,480,"Brush normal on (n)"
		Else
		Text 10,480,"Brush normal off (n)"		
	End If
	If scatter = True Then
		Text 180,480,"Brush Scatter on (s)"
		Else
		Text 180,480,"Brush Scatter off (s)"
	End If
	If smudge = True Then
		Text 180,490,"Brush Smudge On (m)"
		Else
		Text 180,490,"Brush Smudge Off (m)"
	End If
	If shade = True Then
		Text 180,500,"Shade mode on (h)"
		Else
		Text 180,500,"Shade mode off (h)"
	End If
	If mirror = True Then
		Text 180,510,"Mirror mode on (i)"
		Else
		Text 180,510,"Mirror mode off (i)"
	End If
	
	If explode = True
		Text 380,480,"Brush Explode on (e)"
		Else
		Text 380,480,"Brush Explode off (e)"		
	End If
	If linemode = True
		Text 10,490,"Line Mode On (l)"
		Else
		Text 10,490,"Line Mode Off (l)"	
	End If
	
	Text 10,500,"Outline at pos (o)"
	Text 380,490,"Pick color (p)"
	Text 380,510,"Darken/Brighten (F1-F2)"
	Text 380,500,"Color Sel. (Cur. Up/Down"
	Text 580,480,"Flood fill (f)"
	If RectsOverlap(0,0,mw*tw,mh*th,cmx,cmy,1,1)=True
		Text 690,480,"X:"+ cmx/tw + " Y:"+cmy/th
	EndIf
	If pickcolor=True
		Text 690,490,"Pick color"
	End If
	Text 580,490,"Undo (u)"
	Text 10,510,"color Set (rmb)"
	Text 10,520,"Cls (c)"
	Text 580,510,"Size :"+brushsize
	Text 580,500,"brushsize (1/9)"
	SetBuffer CanvasBuffer(can)
	Cls
	DrawImage canim,0,0
	FlipCanvas can
End Function


Function addundo()
	undoredo.unre = New unre
	cnt=0
	For y=0 To mh-1
	For x=0 To mw-1
		undoredo\map[cnt] = map(x,y)
		cnt=cnt+1
	Next
	Next
	cnt3=0
	For i=0 To 11*3
		undoredo\cols[cnt3] = cols(cnt1,cnt2)
		undoredo\colsf[cnt3] = colsf(cnt1,cnt2)
		cnt2=cnt2+1
		If cnt2>3 Then cnt2 =0:cnt1=cnt1+1
		cnt3=cnt3+1
	Next
	cnt=0
	For ii.unre = Each unre
	cnt=cnt+1
	Next
	DebugLog "num undo "+cnt
End Function

Function undoback()
	Local c=0
	For ii.unre = Each unre
	c=c+1
	Next
	DebugLog "initialize undo "+c
	If c<3 Then Return
	DebugLog "undoing'
	Local cnt=0
	undoredo.unre = Last unre
	Delete undoredo
	undoredo.unre = Last unre
	For y=0 To 47
	For x=0 To 47
		map(x,y) = undoredo\map[cnt]
		cnt=cnt+1			
	Next
	Next	
	cnt1=0
	cnt2=0
	cnt3=0
	For i=0 To 11*3
		cols(cnt1,cnt2) = undoredo\cols[cnt3]
		colsf(cnt1,cnt2) = undoredo\colsf[cnt3]		
		cnt2=cnt2+1
		If cnt2>3 Then cnt2 =0:cnt1=cnt1+1
		cnt3=cnt3+1
	Next
End Function
Function makemonkeycode()
	mytxt$="Global sprite:Int[][] = ["+Chr(13)+Chr(10)
	For y=0 To mh-1
	mytxt$=mytxt$+"["
	For x=0 To mw-1
		mytxt$=mytxt$+map(x,y)
		mytxt$=mytxt$+","
	Next
		mytxt$=Left(mytxt$,Len(mytxt$)-1)
		mytxt$=mytxt$+"]"
		mytxt$=mytxt$+","
		mytxt$=mytxt$+Chr(13)+Chr(10)
	Next
	mytxt$=Left(mytxt$,Len(mytxt$)-3)
	mytxt$=mytxt$+"]"	
	SetTextAreaText txt,mytxt$
End Function
Function readmonkeycode()
	mytxt$ = TextAreaText(txt)
	Local cnt=0
	Local stp=1
	Local exitloop=False
	While exitloop=False
		stp=Instr(mytxt$,",",stp)
		If stp=0 Then exitloop=True
		stp=stp+1
		cnt=cnt+1
	Wend
	If cnt <> ((mw)*(mh)) Then Notify "Not valid map data"
	Local mytxt2$
	Local a$=""
	Local b$=""
	Local c$=""
	For i = 1 To Len(mytxt$)
		a$=Mid(mytxt$,i,1)
		If a$="," Then b$=b$+a$		
		If Asc(a$) >= 48 And Asc(a$)<= 57 Then b$=b$+a$
	Next
	For i=1 To Len(b$)
		a$=Mid(b$,i,1)
		If Asc(a$)>=48 And Asc(a$)<=57 
			c$=c$+a$
		End If
		If a$="," Then
			map(x,y) = Int(c)
			c$=""
			x=x+1
			If x>=mw Then x=0:y=y+1
		End If
	Next
End Function
Function readcolorcode()
	Local lst[1000]
	a$ = TextAreaText(txt2)
	a$ = Replace(a$,Chr(13),",")
	a$ = Replace(a$,Chr(10),"")
	For i=1 To Len(a$)
		b$=Mid(a$,i,1)
		If b$=Chr(13)
			lst[cnt] = c$
			cnt=cnt+1
			c$=""
		End If
		If b$=","
			lst[cnt] = c$
			cnt=cnt+1
			c$=""
		End If
		If Asc(b$) >= 48 And Asc(b$) <= 57
			c$=c$+b$
		End If
	Next
	cnt2=0
	For i=0 To 10
		cols(i,0) = lst[cnt2]
		cnt2=cnt2+1
		cols(i,1) = lst[cnt2]
		cnt2=cnt2+1
		cols(i,2) = lst[cnt2]
		cnt2=cnt2+1		
	Next
End Function
Function makecolorcode()
	a$ = ""
	For i=0 To 10
		a$=a$ + cols(i,0) + ","
		a$=a$ + cols(i,1) + ","
		a$=a$ + cols(i,2)
		a$=a$ + Chr(13)+Chr(10)
	Next
	SetTextAreaText(txt2,a$)
End Function
Function refreshtileimages(init=False)
	For i = 0 To 10
		SetBuffer ImageBuffer(tileim,i)
		If init=True
			Color 200+i*3,10+i*20,10
			cols(i,0) = ColorRed()
			cols(i,1) = ColorGreen()
			cols(i,2) = ColorBlue()	
			colsf(i,0) = ColorRed()
			colsf(i,1) = ColorGreen()
			colsf(i,2) = ColorBlue()		
		End If
		Color cols(i,0),cols(i,1),cols(i,2)
		Rect 0,0,tw,th,True
		SetBuffer ImageBuffer(tileimbig,i)
		Color cols(i,0),cols(i,1),cols(i,2)
		Rect 0,0,32,32
		For x=-1 To 1
		For y=-1 To 1
			Color 4,4,4
			Text tw/2+x,th/2+y,i,1,1
		Next
		Next
		Color 255,255,255
		Text tw/2,th/2,i,1,1
	Next
End Function
.paintfuncs
Function outlinefill()
	For this.ol = Each ol
		Delete this
	Next
	For that.cl = Each cl
		Delete that
	Next
	
	Local fillc
	Local st[10]
	st[0] = 0
	st[1] = -1
	st[2] = 1
	st[3] = 0
	st[4] = 0
	st[5] = 1
	st[6] = -1
	st[7] = 0
	If screen="canvas"
	If RectsOverlap(cmx,cmy,1,1,0,0,(mw+1)*tw,(mh+1)*th)	
		
		Local sx = cmx/tw
		Local sy = cmy/th	
		ffaddlist(sx,sy)
		fillc = map(sx,sy)
		Local xm
		Local my
		While fflistopen() = True
			this.ol = Last ol
			mx = this\x
			my = this\y
			Delete this
			a.cl = New cl
			a\x = mx
			a\y = my
			For i=0 To 6 Step 2
				x = st[i]
				y = st[i+1]
				If mx+x >=0 And mx+x<=mw
				If my+y >=0 And my+y<=mh
				If isonclosedlist(mx+x,my+y)=False
				If map(mx+x,my+y) = fillc				
					z.ol = New ol
					z\x = mx+x
					z\y = my+y
				End If
				End If
				End If
				End If
			Next
		Wend
	End If
	End If
	For e.cl = Each cl
		x1 = e\x
		y1 = e\y
		For i=0 To 6 Step 2
			x2 = st[i]
			y2 = st[i+1]
			If x1+x2 >=0 And x1+x2<=mw
			If y1+y2 >=0 And y1+y2<=mh
				If map(x1+x2,y1+y2)<>fillc
				If shade = True
					If shademap(x1+x2,y1+y2) = False
					map(x1+x2,y1+y2)=brushindex
					End If
				End If
				If shade=False
				map(x1+x2,y1+y2)=brushindex			
				End If
				End If
			End If
			End If
		Next
	Next
End Function
Function isonclosedlist(x,y)
	For this.cl = Each cl
		If this\x = x And this\y = y Then Return True
	Next
	Return False
End Function
Function olistopen()
	Local cnt=0
	For this.ol = Each ol
	cnt=cnt+1
	If cnt>0 Then Return True
	Next
	Return False
End Function
Function floodfill()
	For aa.ol = Each ol
		Delete aa		
	Next
	For bb.cl = Each cl
		Delete	bb
	Next
	Local st[10]
	st[0] = 0
	st[1] = -1
	st[2] = 1
	st[3] = 0
	st[4] = 0
	st[5] = 1
	st[6] = -1
	st[7] = 0
	If screen="canvas"
	If RectsOverlap(cmx,cmy,1,1,0,0,(mw+1)*tw,(mh+1)*th)	
		Local sx = cmx/tw
		Local sy = cmy/th	
		Local fillc
		ffaddlist(sx,sy)
		fillc = map(sx,sy)
		If brushindex = fillc Then Return
		Local xm
		Local my
		While fflistopen() = True
			this.ol = Last ol
			mx = this\x
			my = this\y
			map(mx,my) = brushindex
			ffremlist(mx,my)
			For i=0 To 6 Step 2
				x = st[i]
				y = st[i+1]
				If mx+x >=0 And mx+x<=mw
				If my+y >=0 And my+y<=mh
					If map(mx+x,my+y) = fillc
						ffaddlist(mx+x,my+y)
					End If
				End If				
				End If
			Next
		Wend
	End If
	End If
End Function
Function ffremlist(x,y)
	For this.ol = Each ol
		If this\x = x And this\y = y
			Delete this
			Return
		End If
	Next
End Function
Function fflistopen()
	Local cnt=0
	For this.ol = Each ol
		cnt=cnt+1
	Next
	If cnt>0 Then Return True
	Return False
End Function
Function ffaddlist(x,y)	
	this.ol = New ol
	this\x = x
	this\y = y
End Function
Function makeline(x1,y1,x2,y2)	
	x1=x1/tw
	y1=y1/th
	x2=x2/tw
	y2=y2/th
;	Local x1=lsx1/tw
;	Local y1=lsy1/th
;	Local x2=lsx2/tw
;	Local y2=lsy2/th
    Local dx
	Local dy
	Local sx
	Local sy
	Local e
      dx = Abs(x2 - x1)
      sx = -1
      If x1 < x2 Then sx = 1      
      dy = Abs(y2 - y1)
      sy = -1
      If y1 < y2 Then sy = 1
      If dx < dy Then 
          e = dx / 2 
      Else 
          e = dy / 2          
      End If
      Local exitloop=False
      While exitloop = False 
        ; map(x1,y1) = brushindex
		brushdown(x1*tw,y1*th,brushindex)
        If x1 = x2 
            If y1 = y2
                exitloop = True
            End If
        End If
        If dx > dy Then
            x1 = x1 + sx :			e = e - dy 
              If e < 0 Then e = e + dx : y1 = y1 + sy
        Else
            y1 = y1 + sy : e = e - dx 
            If e < 0 Then e = e + dy : x1 = x1 + sx
        EndIf
	Wend
End Function
Function brushdown(cmx,cmy,ind)
	If brushsize=1 Then 
		If protcol(map(cmx/tw,cmy/th)) = False
			If shade = False
				map(cmx/tw,cmy/th) = ind
			End If
			If shade = True
				If shademap(cmx/tw,cmy/th) = False
					map(cmx/tw,cmy/th) = ind
				End If
			End If
		End If
	End If
	If brushsize>1 Then
		Local cnt
		Local brushbuffer[10*10]
		If smudge=True
			If lcmx<>cmx And lcmy<>cmy	
			; put data under brush (prev pos)
			; in temp array
			cnt=0
			For y=-brushsize/2 To brushsize/2
			For x=-brushsize/2 To brushsize/2
				brushbuffer[cnt]=-1
				If lcmx/tw+x >=0 And lcmx/tw+x <=mw
				If lcmy/th+y >=0 And lcmy/th+y <=mh			
				brushbuffer[cnt] = map(lcmx/tw+x,lcmy/th+y)
				End If
				End If
				cnt=cnt+1
			Next
			Next
			; copy temp array data to
			; current brush position
			; uses intensity(rand)
			cnt=0
			For y=-brushsize/2 To brushsize/2
			For x=-brushsize/2 To brushsize/2
				If cmx/tw+x >=0 And cmx/tw+x <=mw
				If cmy/th+y >=0 And cmy/th+y <=mh			
				If brushbuffer[cnt]>-1
				If Rnd(smudgeint)<2
				If protcol(map(cmx/tw+x,cmy/th+y)) = False
					If shade=False
						map(cmx/tw+x,cmy/th+y) = brushbuffer[cnt]
					End If
					If shade = True
						If shademap(cmx/tw+x,cmy/th+y) = False
						map(cmx/tw+x,cmy/th+y) = brushbuffer[cnt]
					End If				
					End If
				
				End If
				End If							
				End If
				End If
				End If
				cnt=cnt+1
			Next
			Next
			
			;
			EndIf			
		End If
		If normal=True
			For y=-brushsize/2 To brushsize/2
			For x=-brushsize/2 To brushsize/2 
				If cmx/tw+x >=0 And cmx/tw+x <=mw
				If cmy/th+y >=0 And cmy/th+y <=mh
				If protcol(map(cmx/tw+x,cmy/th+y)) = False
					If shade = False
						map(cmx/tw+x,cmy/th+y) = ind
					End If
					If shade = True
					If shademap(cmx/tw+x,cmy/th+y) = False
						map(cmx/tw+x,cmy/th+y) = ind
					End If
					End If
				End If
				End If
				End If
			Next
			Next
		End If
		If scatter=True
			For y=-brushsize/2 To brushsize/2
			For x=-brushsize/2 To brushsize/2 
				If cmx/tw+x >=0 And cmx/tw+x <=mw
				If cmy/th+y >=0 And cmy/th+y <=mh
				If Rnd(brushsize)<2
				If protcol(map(cmx/tw+x,cmy/th+y)) = False
				
					If shade=False
						map(cmx/tw+x,cmy/th+y) = ind
					End If
					If shade = True
					If shademap(cmx/tw+x,cmy/th+y)=False
						map(cmx/tw+x,cmy/th+y) = ind
					End If
					End If
				
				End If
				End If
				End If
				End If
			Next
			Next
		End If
		If explode = True
			For y=-brushsize/2 To brushsize/2
			For x=-brushsize/2 To brushsize/2 
				If cmx/tw+x >=0 And cmx/tw+x <=mw
				If cmy/th+y >=0 And cmy/th+y <=mh
					If Rnd(brushsize)<2
					b = Rnd(cmx/tw-brushsize/2,cmx/tw+brushsize/2)
					c = Rnd(cmy/th-brushsize/2,cmy/th+brushsize/2)
					If b>=0 And b<=mw And c>=0 And c<=mh
					If protcol(map(cmx/tw+x,cmy/th+y)) = False
						a = map(b,c)
						If shade = False
							map(cmx/tw+x,cmy/th+y) = a
						End If
						If shade = True
						If shademap(cmx/tw+x,cmy/th+y) = False
							map(cmx/tw+x,cmy/th+y) = a
						End If
						End If
						
					End If
					End If
					End If
				End If
				End If			
			Next
			Next			
		End If
	End If
End Function
