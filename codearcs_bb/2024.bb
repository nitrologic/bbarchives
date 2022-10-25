; ID: 2024
; Author: Nebula
; Date: 2007-05-31 12:58:53
; Title: Live audio data gaming example
; Description: See with sound - live frogger w.i.p

;
; Download Audacity and set it up.
; Select the Waveform Db.
; The audio quality should be
; Mono 8000khz+
; The bar should be about 2 mousecursors high.
; The purple rectangle on your desktop 
; should be on the end of the
; sound input channel in Audacity.
;
; 
; Run this program in blitz+
;
; You can use the readout to
; live any game engine type.
; 
; You would be able to extract
; people walking by.Cars
; trucks. ect..
; Youself in the room.
; Sound waves flashing by.
; Walls being higlighted.
; you name it.

SetBuffer DesktopBuffer()

Color 200,200,200
;
temp = CreateImage(86,32)

gx = 700; grab rectangle at x area ; wave recording output
gy = 200; grab rectangle at y area ; wave recording output

GrabImage temp,gx,gy

Color 255,0,255

Rect gx,gy,100,100,False

DrawImage temp,200,100

Graphics 200,200,16,2

While KeyDown(1) = False
	SetBuffer DesktopBuffer()
	GrabImage temp,gx,gy
	SetBuffer BackBuffer()
	DrawImage temp,0,0	
	linestretch
	Flip
Wend
End


Function linestretch()
	For y = 0 To 100
		CopyRect 0 , y , 100 , 1 , 0, y + 100 + (y*2)
;		CopyRect 0 , y , 100 , 9 , 0, y + 201 + (y*2)
	Next
End Function


;
;
;
;
;
;
;
;
;
;
;
;;;;;;;
//////////// ; 2nd 

;
;
;
;


; Step 2 - live frogger
; 
; You need Audacity for this example :
; Put the recorder bar to double the mouse height
; and enlarge the height of the input sound to
; meet the top of the bar.
;
;------------------
;| -------------- |
;| ************** |
;|----------------<---Audacity recording
;| ************** |
;| -------------- |
;__________________ ; screen capture area
; The input at ; lets say 8000+ khz
; 
; Put audacity at the top of the screen and
; make sure that, the new rectange on the desktop
; is located on the end of the wave input.
; 
; press the mouse button on the red grid
; to edit the waveform Db map.
; The selected parts will be copied 
; to the right side of the screen.
; Use the right mouse button to
; deselect the grid parts.
;
; Take a look at the sound read out. 
; It should be animated and should contain
; information about your surrounding.
;
; Regards cromdesign
;
;
Dim grid(10,10)
;
SetBuffer DesktopBuffer()
;
Flip

temp = CreateImage(86,32)

gx = 700;
gy = 200;

GrabImage temp,gx,gy

Color 255,0,255

Rect gx,gy,100,100,False

DrawImage temp,200,100

Graphics 200,200,16,2

While KeyDown(1) = False
	SetBuffer DesktopBuffer()
	GrabImage temp,gx,gy
	SetBuffer BackBuffer()
	DrawImage temp,0,0	
	linestretch
	editgrid 0,100
	straat 
;	lemmingloop
	Color 0,0,0:Rect 100,0,100,100,True
	copygrid 0,100,100,0
	drawgrid 0,100

	Flip
Wend
End

Function lemmingloop()
	For y=0 To 10
		CopyRect 0,MouseY(),100,2,100,115+y
	Next
End Function

Function straat()
	Color 255,255,255
	Line 100,100,200,100
	Line 100,130,200,130
End Function
;
Function linestretch()
	For y = 0 To 100
		CopyRect 0 , y , 100 , 1 , 0, y + 100 + (y*2)
		CopyRect 0 , y , 100 , 9 , 0, y + 101 + (y*2)
	Next
End Function
;
Function editgrid(x,y)
	For x1 = 0 To 10
	For y1 = 0 To 10
		;
		If RectsOverlap(MouseX(),MouseY(),1,1,x+x1 * 10,y+y1 * 10 ,10,10) = True
			If MouseDown(1) = True Then
				grid( x1 , y1 ) = True
			End If
			If MouseDown(2) = True Then
				grid( x1 , y1 ) = False
			End If
		End If
	Next:Next
	;
End Function
;
Function drawgrid(x,y);
	For x1 = 0 To 10
	For y1 = 0 To 10
		Color 0,200,0 : If grid(x1,y1) = True Then  Rect x1 * 10 + x , y1 * 10 + y , 10,10,True
		Color 200,0,0 : If grid(x1,y1) = False Then Rect x1 * 10 + x , y1 * 10 + y , 10,10,False
	Next:Next
End Function
;
Function copygrid(x1,y1,x2,y2)
	For x=0 To 10;
	For y=0 To 10;
		;
		If grid( x , y ) = True Then
			CopyRect x1+x*10 , y1+y*10 , 10 , 10 , x2 + x*10 , y2 + y*10
		End If
		;
	Next:Next
End Function
