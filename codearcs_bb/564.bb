; ID: 564
; Author: Rimmsy
; Date: 2003-01-31 07:58:01
; Title: simple graph library
; Description: allows you to monitor variables with a funky graph

; -------------------------------------
; author: matthew griffiths
; Funky Graph Library (v1.1); use as you like.
; Updated: 1st Feb, 2003
;	There was a bug with the update
;	time in the updateMonitor function.
;	works ok now.
;
; contact: matt.birdinsky@gmail.com
; -------------------------------------

; available functions:
; createMonitor			; constructor
; processTime			; gets the time taken to process a cycle
; getValue				; get a value from the monitor
; updateMonitor			; updates the monitor with data
; drawMonitor			; draws the graph to the screen
; getMonitorFromName	; an function that gets an object from a name
; setSaveHistory		; sets and starts a save log to an image

; there are two ways you can access these function:
; either by name$ or using the object. For more info, see the functions themselves

Const MONITOR_MAXIMUM_IMAGE_WIDTH=1000


Type monitor
	Field value#[2]
	Field w,h
	Field r,g,b
	Field name$
	Field image,invert
	Field maxValue,processTime
	Field born,updateTime
	Field x,saveHistory$
End Type

Function createMonitor.monitor(name$,r,g,b,maxValue,h,w, updateTime=500,invert=-1)
	m.monitor=New monitor
	m\name=Lower(name)
	m\r=r : m\g=g : m\b=b	
	m\maxValue=maxValue
	m\w=w : m\h=h
	m\image=CreateImage(w,h)
	m\born=MilliSecs()
	m\updateTime=updateTime
	m\saveHistory=""
	m\x=0
	m\invert=invert
	
	Return m
End Function

; set to "" for no save history,
; othewise, set to location and file, like: "c:\game\output"
Function setSaveHistory(o.monitor,n$,location$="")
	If o=Null
		v.monitor=getMonitorFromName(n)
	Else
		v.monitor=o
	EndIf	
	v\saveHistory=location
End Function


Function processTime(o.monitor,n$)
	If o=Null
		v.monitor=getMonitorFromName(n)
	Else
		v.monitor=o
	EndIf	
	
	If v= Null Then Return -1
	Return v\processTime		
End Function


Function getValue#(o.monitor,n$,a=0)
	If o=Null
		v.monitor=getMonitorFromName(n)
	Else
		v.monitor=o
	EndIf	
	If v= Null Then Return
	Return v\value[a]
End Function


Function deletePreviousLogs(n$)
	f=ReadDir(CurrentDir())
	Repeat
		file$=Lower(NextFile(f))
		If file="" Then Exit
		If Instr(file,Lower(n))
			If Lower(Right(file,4))=".bmp"
				DeleteFile file
			EndIf
		EndIf
	Forever	
	CloseDir f
End Function


Function updateMonitor(o.monitor,n$,value#, average=0, updateTime=-1 ,drawLineAtLength=2)
	If o=Null
		v.monitor=getMonitorFromName(n)
	Else
		v.monitor=o
	EndIf	
	If v= Null Then Return	

	marker_colour=80
	marker_height#=0.15


	If (MilliSecs()-v\born) > v\updateTime
		If updateTime <> -1 Then v\updateTime=updateTime
		v\born=MilliSecs()
	Else
		Return
	EndIf
	m=MilliSecs()	
	
	If updateTime <> -1
		; update our... erm... update time
		If v\updateTime <> updateTime
			v\updateTime=updateTime
			v\born=MilliSecs()
		EndIf
	EndIf
	
	v\value[2]=v\value[1]
			
	If average
		v\value[1]=(value+v\value[1])/2
	Else		
		v\value[1]=value
	EndIf
	
	v\x=v\x+1
	
	If v\x > v\w
		v\x=1	
		If Len(v\saveHistory)
			; save this to an image			
			If FileSize(v\saveHistory+".bmp")
				i=LoadImage(v\saveHistory+".bmp")
				new_width=ImageWidth(i)+v\w							
				If new_width < MONITOR_MAXIMUM_IMAGE_WIDTH
					p=CreateImage(new_width,v\h) ; create a new "frame"
					CopyRect(0,0,ImageWidth(i),v\h,0,0,ImageBuffer(i),ImageBuffer(p))
					CopyRect(0,0,v\w,v\h,new_width-v\w,0,ImageBuffer(v\image),ImageBuffer(p))
						SetBuffer ImageBuffer(p)
						Color marker_colour,marker_colour,marker_colour
						Rect ImageWidth(i)-1,0,1,(v\h*marker_height)
						Rect ImageWidth(i)-1,v\h-(v\h*marker_height),1,(v\h*marker_height)
						SetBuffer BackBuffer()
					SaveBuffer(ImageBuffer(p),v\saveHistory+".bmp")
				Else
					; it's a bit big, don't bother					
				EndIf
				FreeImage i
			Else
				p=CreateImage(v\w,v\h)
				CopyRect(0,0,v\w,v\h,0,0,ImageBuffer(v\image),ImageBuffer(p))
				SetBuffer ImageBuffer(p)
				Color marker_colour,marker_colour,marker_colour
				Rect 0,0,1,(v\h*marker_height)
				Rect 0,v\h-(v\h*marker_height),1,(v\h*marker_height)
				Rect v\w-1,0,1,(v\h*marker_height)
				Rect v\w-1,v\h-(v\h*marker_height),1,(v\h*marker_height)
				SetBuffer BackBuffer()
				SaveBuffer(ImageBuffer(v\image),v\saveHistory+".bmp")
			EndIf
		EndIf
		SetBuffer ImageBuffer(v\image)
		Color 0,0,0
		Rect 0,0,v\w,v\h,1
		SetBuffer BackBuffer()
	EndIf
	
	SetBuffer ImageBuffer(v\image)
	Color v\r,v\g,v\b	
	delta#=(Float(v\h)/Float(v\MaxValue))
	
	If v\invert=-1
		y1#=v\h-(delta*v\value[1])
		y2#=v\h-(delta*v\value[2])	
	Else
		y1#=(delta*v\value[1])
		y2#=(delta*v\value[2])		
	EndIf

	svy#=Abs(y1-y2)
	length# = Sqr((svy * svy))
	
	If length > drawLineAtLength
		Line v\x-1,y2,v\x,y1
	EndIf
	Rect v\x,y1-1,1,1
		
	SetBuffer BackBuffer()	
	v\processTime=MilliSecs()-m	
End Function

; set outline to 0 to get no outline, otherwise,
; set it to a colour, 20 being 20,20,20

Function drawMonitor(o.monitor,n$,x,y,outputText$="",outline=20)
	If o=Null
		v.monitor=getMonitorFromName(n)
	Else
		v.monitor=o
	EndIf	
	If v= Null Then Return
	If outline
		Color outline,outline,outline
		Rect x,y,		v\w,1
		Rect x,y+v\h,	v\w,1
	EndIf
	
	DrawImage v\image,x,y		 

	If Len(outputText)
		outPutText=outPutText+v\value[1]
		Color v\r,v\g,v\b
		Text (x+v\w)-StringWidth(outPutText),(y+v\h)-FontHeight(),outputText
	EndIf
End Function

Function getMonitorFromName.monitor(n$)
	For m.monitor=Each monitor
		If Lower(m\name)=Lower(n)
			Return m
		EndIf
	Next
End Function

Function deleteMonitor(o.monitor,n$)
	If o=Null
		v.monitor=getMonitorFromName(n)
	Else
		v.monitor=o
	EndIf	
	If v= Null Then Return
	FreeImage v\image
	Delete v
End Function


; -----------------------------------
; Very simple example
; -----------------------------------
Include "monitor.bb"

Graphics 640,480,0,2
SetBuffer BackBuffer()


createMonitor("monitor",255,255,0,GraphicsHeight(),20,60,500) ; every half a second


While KeyHit(1)=0
	Cls
	
	updateMonitor(Null,"monitor",MouseY()) 	; update value
	drawMonitor(Null,"monitor",0,1)  			; draw it
 	;drawMonitor(Null,"monitor",2,2,"",255)	; try adding two drawMonitors.
 
	Flip
Wend
deleteMonitor(Null,"monitor")
End

; -----------------------------------
; Extended example
; -----------------------------------
; monitor example

; the monitor allows you to create a graph and add data to it.
; the graph is split up into three section:
;	1) create it and set certain values (float or int)
;	2) update it with a value
;	3) draw it to the screen


; LOGGING
; there's also an option to save upto 1000 pixels worth of graph to an image
; to see how this works, please see the example below. You can change the 1000 is you wish.
; you'll need to set the log to active using 'setSaveHistory(o.monitor,n$,location$="")', location
; being the filename and location you want to save, like "c:\game\fps log". the .bmp is added automatically
; in order to stop this being affected by old logs, you can use the 'deletePreviousLogs(n$)' function
; which will delete all files with n in their name.




; CREATING:
; when you create a monitor, you specify the name (case insenstive) and colour of the monitor graph 
; in r,g,b; it's maximum value (to scale), like an fps graph would probably have a maximum value 
; of 120 Or something. then specifiy the width and height of the graph, and the updatetime in milliseconds.
;
;	createMonitor.monitor(name$,r,g,b,maxValue,height,width, updateTime=500,invert=-1)
;
; 	invert.. well, inverts the graph values. so if it is set to -1, which is default,
; 	a value of 100 in a graph of maximum 100 will be at the top.
; 	set it to 1 and 100 in a max 100 graph will be at the bottom.



; When using the functions, the first parameter is a monitor object. if you specifiy NULL, 
; the function will use the name you specifiy next to search for the monitor.
; obviously the former method is faster, but the latter easier to use.
; ---------------------------------------------------------------------------
; UPDATING
; when you update the monitor, you may update it every cycle, as the monitor uses
; an internal method of updating depeding on your initial values. In this function though,
; you can change has fast the monitor updates with a parameter.
;
;	updateMonitor(o.monitor,n$,value#, average=0, updateTime=-1 ,drawLineAtLength=2)
;	
; 	when you set average to 1, the previous value is added to the current and divided by two
; 	giving a slightly smoother graph.
; 	the updateTime parameter allows you to change the updatetime. set this to -1 if you do not widh
; 	to change the value. 
; 	ie: 
; 		updateMonitor(Null,"fps",myFPS, 0, -1)
; 	is the same as doing:
;		updateMonitor(Null,"fps",myFPS, 0) 
; 	as the updateTime defaults to -1



; DRAWING
; you have to draw the monitor to the screen to be able to see it.
; you don't have to, obviously.
;
;	drawMonitor(o.monitor,n$,x,y,outputText$="",outline=20)
;
;	the output text will output the text you add in the parameter and add the value on the end,
;	so, "fps:" will output "fps:12.2". to change where, mess with the functions in 'monitor.bb'
;	outline is set to 20 by default. if this is 0, there will be no line drawn, otherwise,
;	a line of colour outline,outline,outline (always a grey of some sort) will draw
;	a maximum and minimum line. good so you know what the hell is a high and low value.

Include "monitor.bb"

Graphics 640,480,0,2
SetBuffer BackBuffer()


updateTime=80 
average=0
logHistory=0

; you can invert the graph result, by adding a 1 at the end. the default is -1

m1.monitor=createMonitor("mousey()",		255,0,	0,  	GraphicsHeight(),	100,300, updateTime)
m2.monitor=createMonitor("mousex()",		0,	255,0,  	GraphicsWidth(),	100,300, updateTime)

m1.monitor=createMonitor("mousey() invert",		255,0,	120,  	GraphicsHeight(),	100,300, updateTime,1)
m2.monitor=createMonitor("mousex() invert",		120,	255,0,  GraphicsWidth(),	100,300, updateTime,1)

; delete any previously created log images in the directory
; use with caution
deletePreviousLogs("mousex() log")
deletePreviousLogs("mousey() log")
deletePreviousLogs("mousey() invert log")
deletePreviousLogs("mousex() invert log")




While KeyHit(1)=0
	Cls
	
		updateMonitor(Null,"mousey()",MouseY(),average, updateTime)	
		updateMonitor(Null,"mousex()",MouseX(),average, updateTime)
		updateMonitor(Null,"mousex() invert",MouseX(),average, updateTime)
		updateMonitor(Null,"mousey() invert",MouseY(),average, updateTime)
			
		drawMonitor(Null,"mousey()",0,150, "",20)
		drawMonitor(Null,"mousex()",0,150, "",20)
		drawMonitor(Null,"mousey() invert",0,150, "",20)
		drawMonitor(Null,"mousex() invert",0,150, "",20)		
		
		
		
		
		; and some debug code..		
		Color 255,255,255		
		Text 0,20,"Time to process mousey(): "+processTime(Null,"mousey()")+" ms"
		Text 0,40,"Time to process mousex(): "+processTime(Null,"mousex()")+" ms"
		Text 0,60,"Average: "+average
		Text 0,80,"Log History: "+logHistory
		Text 0,100,"Update time: "+updateTime
		
		If KeyHit(57) Then average=1-average
		If KeyHit(35)
			logHistory=1-logHistory			
			If logHistory
				setSaveHistory(Null,"mousex()","mousex() log")
				setSaveHistory(Null,"mousey()","mousey() log")
			Else
				setSaveHistory(Null,"mousex()","")
				setSaveHistory(Null,"mousey()","")			
			EndIf	
		EndIf
		If KeyDown(2) Then updateTime=updateTime-10
		If KeyDown(3) Then updateTime=updateTime+10
		If updateTime < 0 Then updateTime=0
	Flip
Wend


deleteMonitor(Null,"mousey()")
deleteMonitor(Null,"mousex()")
deleteMonitor(Null,"mousey() invert")
deleteMonitor(Null,"mousex() invert")
End
