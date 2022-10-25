; ID: 267
; Author: Death
; Date: 2002-03-14 20:12:26
; Title: SinusBobs
; Description: 1280 moving sinus-blitter-objects

; SinusBobs - demo with loads of moving sinus blitter objects (1280 to be exact :-)
; By Death@jdg.nu
;
; This is just a test i did because i was curious how many bobs
; blitz could throw around in full frame rate...
; The awesome C64-game ARMALYTE inspired me to do this
; (loads of sprites in the title screen)
;
; My Celeron @ 790 mhz + Geforce 256 can easily move around 1300 bobs in
; full framerate at 1280x1024x32. Im pretty impressed by Blitz actually!
;
; The code would probably be faster if a lookup-table was used for
; the sinus waves instead of calling Sin() & Cos() 1280 times/sec :-)
;
; The FPS counter was "borrowed" from the Blitz Code Archive
;
; Mouse Left/Right = Change number of blitter objects
; Mouse Up/down    = Change spacing
; Esc to quit

Global test,space
Global framecounter_counter
Global framecounter_time
Global framecounter_framerate

; CHANGE RESOULTION HERE IF YOUR COMPUTER CAN'T KEEP UP
Graphics 1280,1024,32,1
SetBuffer BackBuffer()

; CHANGE THIS TO POINT TO YOUR BLITZ CDROM, WE NEED SOME IMAGES!!!
test = LoadAnimImage ("e:\Xtras\graphics\action1.bmp",34,34,0,180)


; main loop
While Not KeyHit(1)
	degrees = degrees + 1
	If degrees > 359 Then degrees = 0
	antal=MouseX()+1
	space=MouseY()+1
	Cls
	Text (GraphicsWidth()/2)-64,GraphicsHeight()/2,"Drag the mouse!!"
	For x = 1 To antal
		DrawImage test,x_pos(degrees+(x*5))+128+x,y_pos(degrees+(x*5))+128+x,x/8
	Next 
	fps()
	Text 10,25,"Bobs   : "+antal
	Text 10,40,"Spacing: "+space
	Flip
Wend
End


Function x_pos(degrees)
	If degrees>359 Then degrees = degrees - 359
	Return Cos(degrees)*space
End Function


Function y_pos(degrees)
	If degrees>359 Then degrees = degrees - 359 
	y=Sin(degrees)*space
	y=-y
Return y
End Function


Function FPS()
	Framecounter_counter=Framecounter_counter+1
	If Framecounter_time=0 Then Framecounter_time=MilliSecs()
	If Framecounter_time+1001 <MilliSecs() Then
    	Framecounter_framerate=Framecounter_counter
   		Framecounter_counter=0
    	Framecounter_time=MilliSecs()
		EndIf
	Text 10,10,"fps: "+Framecounter_framerate
End Function
