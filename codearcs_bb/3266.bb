; ID: 3266
; Author: Pakz
; Date: 2016-04-22 20:27:21
; Title: Map editor to MonkeyX code
; Description: Simple Map editor 20x15 that reads/writes monkey arrays in textarea

; This tool is a map editor that turns the data into a 
; piece of copyable code. Its in the form of an
; multidimensional array.
;
;
;


Global win = CreateWindow("Make Monkey array (tilemap) Example",100,100,800,600,0,1) 
Global txt = CreateTextArea(0,20,800,600,win) 
Global tab = CreateTabber(0,0,800,20,win)
Global can = CreateCanvas(0,20,800,600,win)


Global mw = 20
Global mh = 15
Global tw = 32
Global th = 32
Dim map(mw,mh)
Global canim = CreateImage(800,600)

Global brushindex=0
Global cmx
Global cmy

Global tileim = CreateImage(32,32,11)
For i = 0 To 10
	SetBuffer ImageBuffer(tileim,i)
	Rect 0,0,33,33,False
	Text 16,16,i,1,1
Next

InsertGadgetItem tab,0,"TextArea",0
InsertGadgetItem tab,1,"Canvas",1


Global mytxt$

makemonkeycode

Global timer = CreateTimer(60)

updateinterface

Repeat 
	we = WaitEvent()
	
	If we=$201
		If EventSource() = can
			If EventData() = 1
			End If
		End If
	End If
	If we=$202
		If EventSource() = can
			If EventData() = 1
				If RectsOverlap(cmx,cmy,1,1,680,0,32,11*th)
					brushindex=cmy/th
					updateinterface
				End If
			End If
		End If
	End If
	If we=$203
		If EventSource()=can
			cmx = EventX()
			cmy = EventY()
			If MouseDown(1) = True
			If RectsOverlap(cmx,cmy,1,1,0,0,(mw+1)*tw,(mh+1)*th)
				map(cmx/tw,cmy/th) = brushindex
				updateinterface
			End If
			End If
			If MouseDown(2) = True
			If RectsOverlap(cmx,cmy,1,1,0,0,(mw+1)*tw,(mh+1)*th)
				map(cmx/tw,cmy/th) = 0
				updateinterface
			End If
			End If
		End If
	End If
	If we=$401
		If EventSource() = tab
			sg = SelectedGadgetItem(tab)
			If sg = 0
				makemonkeycode
				HideGadget can
				ShowGadget txt
			End If
			If sg = 1
				readmonkeycode
				HideGadget txt
				ShowGadget can
				updateinterface
				FlipCanvas can
			End If
		End If
	End If
	If we=$4001
	End If	
	If we=$803 Then Exit 
Forever 
End

Function updateinterface()
	SetBuffer ImageBuffer(canim)
	Cls
	Color 255,255,255
	For y=0 To mh-1
	For x=0 To mw-1
		DrawImage tileim,x*tw,y*th,map(x,y)
;		Rect x*tw,y*th,33,33,False
;		Text x*tw+tw/2,y*th+th/2,map(x,y),1,1
	Next
	Next
	For y=0 To 10 
		Rect 680,y*th,33,33,False
		Text 680+16,y*th+16,y,True,True
		If brushindex = y
		Rect 681,y*th,31,31,False
		End If
	Next
	SetBuffer CanvasBuffer(can)
	Cls
	DrawImage canim,0,0
	FlipCanvas can
End Function

Function makemonkeycode()
	mytxt$="Global map:Int[][] = ["+Chr(13)+Chr(10)
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
