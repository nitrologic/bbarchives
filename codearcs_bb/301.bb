; ID: 301
; Author: elias_t
; Date: 2002-08-06 14:20:18
; Title: File Requester
; Description: A simple file requester

;File requester
;
;activePath$ = the active path 
;
;selectedFile$ = the selected file
;
;drv$(drive) = the selected drive
;
;-------------------------------------------------------------------------

Graphics3D 400, 300, 32, 2

SetBuffer BackBuffer()

cam=CreateCamera ()

light=CreateLight()

CameraClsColor cam,40, 40, 55


;------------------------GLOBALS------------------------------------------
Global filter$=".txt"	;file filter

Dim drctrs$(0)			;temp directories
Dim realfiles$(0)		;temp files
Dim txtfiles$(0)		;temp filtered files
Dim drv$(0)				;drives

Global selectedFile$	;current selected file $
Global directories		;temp no. of directories
Global drvcnt			;no. of drives
Global check$			;$ to compare with filter$
Global txtcount			;temp no. of files in a directory
Global drd1				;start position of directories to display
Global drd2				;end    -"-      -"-
Global trd1				;start position of files to display
Global trd2				;end    -"-      -"-
Global dscr				;how many more directories then 20
Global tscr				;how many more files then 20
Global drive			;current drive no.
Global mouseInUse		;if mouse in use
Global activePath$		;the active path

;----------------OPEN FILE------------------------------------------------
count_drives

;--------------------loop
While Not  KeyHit(1)

RenderWorld

;highlight ^up--------------------------------------------------
If MouseY()>47 And MouseY()<60 And MouseX()>144 And MouseX()<170
Color 64,80,110
Rect 144,47,27,12
 If MouseDown(1)
				ypos=0
				ypos2=0
	actualpos=0
	pos=1
	While (pos>actualpos)And (pos<Len(activePath$))
 		actualpos=pos
		pos=Instr(activePath$,"\",pos+1)
	Wend
	If (actualpos=1) Then actualpos=0
	activePath$ =Left$(activePath$,actualpos)
	
	read_dir(drive,activePath$)
	selectedFile$=""	
  Repeat
  Until (Not MouseDown(1))
 EndIf
EndIf

;----------------------
Color 56,78,112:Rect 0,30,40,270:Rect 0,0,400,29
Line 41,45,400,45:Line 41,60,400,60
Line 384,46,384,300:Line 180,46,180,300:Line 196,46,196,300
Color 50,50,75:Rect 41,30,359,15
Color 50,255,100:Line 40,0,40,300:Line 0,29,400,29

Text 1,2,"Select"
Text 1,13,"Drive"
Text 50,7,"Open file     Back"
Text 50,48,"Directories                    File:"
Text 50,32,"Path: "
Color 255,255,80:Text 80,32,drv$(drive)+":\"+Left$(activePath$, 60)
Text 145,48,"|^Up|":Text 233,48,Left$(selectedFile$,29)
Text 200,7,"Filter: "+filter$

;-----------------------------------------------------

If MouseY()<29
		;----open file --------------------
		If MouseX()>50 And MouseX()<100
		Color 50,170,170:Rect 47,3,51,23,0

;Put here your action when open file is pressed
;			If MouseDown(1) Then .....

		EndIf
		; back--------------------------
		If MouseX()>113 And MouseX()<145
		Color 50,170,170:Rect 113,3,36,23,0
		
			If MouseDown(1) Then End			
				
		EndIf		
		
	EndIf

;------------------------------------------------

For t=0 To drvcnt-1

Color 50,255,100
Text 16,35+t*20,drv$(t)+":"

	If MouseX()<29 And MouseY()>30+t*20
	
		If MouseY()<30+(drvcnt)*20 And MouseDown(1)
	
		drive=(MouseY()-30)/20
		read_dir(drive,"")
		activePath$=""
		selectedFile$=""
		ypos=0
		ypos2=0
	
	EndIf
;---------------------------------------------	
	If MouseY()>30+(drvcnt)*20 Then Goto skip
	
	Color 50,170,170	
	Rect 1,31+t*20,38,20,0
	
		If t=0 Then Goto skip
		Color 56,78,112
		Rect 1,31+(t-1)*20,38,20,0
.skip
		EndIf
	Next


;--------------------scroller position----------------------------
If directories>19		
	FlushMouse()
	If MouseDown(1)
		If (MouseX()>179 And MouseX()<197 Or (mouseInUse And MouseX()<210 And MouseX()>160)) And MouseY()>61 And MouseY()<299

		ypos = MouseY()-6
		If dscr >0 
		met#=227.0/dscr
		drd1=Floor ((ypos-56)/met#)
		drd2=drd1+19
		EndIf
		mouseInUse =True

		EndIf
	Else
		mouseInUse =False
	EndIf

	Color 255,234,99
	If (ypos<62) Then ypos=62
	If (ypos>284) Then ypos=284
	Rect 182,ypos,13,15		
	
EndIf
	
	;-----------------------------------------------	
If txtcount>19
	FlushMouse()
	If MouseDown(1)	
		If (MouseX()>383 And MouseX()<400 Or (mouseInUse And MouseX()<400 And MouseX()>360)) And MouseY()>61 And MouseY()<292
			ypos2 = MouseY()-6
			If tscr >0 
			met2#=227.0/tscr
			trd1=Floor ((ypos2-56)/met2#)
			trd2=trd1+19
			EndIf
		mouseInUse =True
		EndIf 
	Else
		mouseInUse =False
	EndIf

	Color 255,234,99
	If (ypos2<62) Then ypos2=62
	If (ypos2>284) Then ypos2=284
	Rect 386,ypos2,13,15

EndIf

;---------------------file selector highlight-----------------------------

	If MouseY()>60 And MouseX()>50 And MouseX()<180 And (Not mouseInUse)
	
	Color 64,80,110
	Rect 49,49+((MouseY()-46)/12)*12,130,11
			FlushMouse()
			If MouseDown(1)
			
			dirmet=(drd1+Floor ((MouseY()-46)/12))-1
			
			If dirmet<directories
				activePath$ = activePath$+drctrs$(dirmet)+"\"
				read_dir(drive,activePath$)
				ypos=0
				ypos2=0
				selectedFile$=""
			EndIf 

			Repeat 
			Until Not MouseDown(1)
			
		EndIf
		
	EndIf
;--------------------------------------------------------------------	

If MouseY()>60 And MouseX()>202 And MouseX()<384 And (Not mouseInUse)
	
	Color 64,80,110
	Rect 201,49+((MouseY()-46)/12)*12,182,11


		If MouseDown(1)

		filmet=(trd1+Floor ((MouseY()-46)/12))-1
		If filmet<txtcount
			selectedFile$ = txtfiles$(filmet)
		EndIf

			Repeat 
			Until Not MouseDown(1)
			
		EndIf
	
	EndIf

;----------display Directories and Files-------------
display
	
Flip

Wend



;---------------F U N C T I O N S ----------------------------------------



;---------------READ_DIR--------------------------------------------------

Function read_dir(drive,path$)
directories=0
drf=0
drd1=0
trd1=0

dir=ReadDir (drv$(drive)+":\"+path$)

Repeat

file$=NextFile$(dir)

If file$="" Then Goto readfiles
	drf=drf+1
	If file$<>".." And file$<>"."
	If FileType (drv$(drive)+":\"+path$+file$)=2 Then directories=directories+1
	EndIf
Forever

;--------readfiles---------------------------

.readfiles
Dim drctrs$(directories)
Dim realfiles$(drf-directories)


tempdir=(ReadDir(drv$(drive)+":\"+path$))
tmpcnt=0

For i=0 To drf-1
temp$=NextFile$(tempdir)
 
	If FileType (drv$(drive)+":\"+path$+temp$)=2 
	If temp$<>".." And temp$<>"."
	drctrs$(tmpcnt)=temp$
	tmpcnt=tmpcnt+1
	EndIf
	
	Else realfiles$(i-tmpcnt)=temp$
	EndIf
		
Next

; sort directories
For i=1 To directories-1
If Upper(drctrs$(i))< Upper(drctrs$(i-1))
 temp$ = Upper(drctrs$(i-1))
 drctrs$(i-1) = Upper(drctrs$(i))
 drctrs$(i) =Upper$(temp$)
 For j= i-1 To 1 Step -1
  If drctrs$(j)<drctrs$(j-1)
   temp$ = Upper(drctrs$(j-1))
   drctrs$(j-1) = Upper(drctrs$(j))
   drctrs$(j) =Upper(temp$)
  Else 
   j=1
  EndIf
 Next 
EndIf
Next 

; get only filtered files
tmpcnt=0
For i=0 To drf-directories-1
 check$ = Right(realfiles$(i),4)
 check$=Lower(check$)
 If (check$=filter$)
  tmpcnt=tmpcnt+1
 EndIf	 	
Next
txtcount =tmpcnt

Dim txtfiles$(txtcount+21)
tmpcnt=0
For i=0 To drf-directories-1
 check$ = Right(realfiles$(i),4)
 check$=Lower(check$)
 If (check$=filter$)
  txtfiles$(tmpcnt)=realfiles$(i)
  tmpcnt=tmpcnt+1
 EndIf	 	
Next
 
; sort filtered files
For i=1 To txtcount-1
If Upper(txtfiles$(i)) < Upper(txtfiles$(i-1))
 temp$ = Upper(txtfiles$(i-1))
 txtfiles$(i-1) = Upper(txtfiles$(i))
 txtfiles$(i) =temp$
 For j= i-1 To 1 Step -1
  If txtfiles$(j)<txtfiles$(j-1)
   temp$ = Upper(txtfiles$(j-1))
   txtfiles$(j-1) = Upper(txtfiles$(j))
    txtfiles$(j) = Upper(temp$)
  Else 
   j=1
  EndIf
 Next 
EndIf
Next 

;---if more then 20 directories or files calculate how many more
	If directories<20
		drd2=directories-1
				Else drd2=19:dscr=directories-20
			EndIf

	If txtcount<20
		trd2=txtcount-1
			Else trd2=19:tscr=txtcount-20
		EndIf

End Function

;-------------------DISPLAY-----------------------------------------------
Function display()

Color 70,255,220
For i=drd1 To drd2
Text 50,60+12*(i-drd1), Left$(drctrs$(i),25)
Next

Color 180,180,230
For i=trd1 To trd2
Text 205,60+12*(i-trd1), Left$(txtfiles$(i),35)
Next

End Function

;---------COUNT_DRIVES----------------------------------------------------
Function count_drives()
drvcnt=0

Dim drv$(14)
    
	For i=Asc("C") To Asc("K")
;For i=Asc("c") To Asc("k");put this if the above doesn't work
	fr=ReadDir(Chr$(i)+":\")
	If fr<>0 Then
	drv$(drvcnt)=Chr$(i)
	drvcnt=drvcnt+1
	CloseDir(fr)
	EndIf
	Next

End Function
