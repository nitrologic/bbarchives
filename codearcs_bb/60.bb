; ID: 60
; Author: Vector Viper
; Date: 2001-09-27 00:38:46
; Title: Small keyboard handler
; Description: Allows easy config of keys+very flexible key reading

;This is a little keyboard handler-I hope it is useful to someone...
;By Aaron Howald (aka Vector Viper)
;It can time key presses, releases, and holds.
;It also allows for easier keyboard configuration.

;please report back how well this works-my keyboard refuses
;to register more than 3 keys pressed at the same time,
;and no more than 5 pressed and held down at once
;also, holding down Lctrl,alt,space,altgr,Rctrl makes the left
;arrow key not register, but the other arrows still do!
;I was planning some neat weapons select by holding keys, but now...hmmm...
;(update)
;I since got a different keyboard-it CAN register any# of keys...

;uses these keycodes here
;u=200, d=208, l=203, r=205,space=57,l-alt=56 ,l ctrl=29,altgr=184,rctrl=157

Const screenwidth=640
Const screenheight=480
Const screendepth=16
Const displaymode=0;0=FS, 2=windowed
Graphics screenwidth,screenheight,screendepth,displaymode

Dim keytranslate(10)
;each slot is hard-coded function, to the program.
;the# returned is the key# attached to the function...
;                0      1   2   3    4      5     6     7     8     9      10                
;tempxvalues;QUIT/MENU UP DOWN LEFT RIGHT FIRE1 FIRE2 FIRE3 FIRE4 SHIELD    ?
;               ESC    (same            ) space l-alt l-ctrl altgr rctrl
;esc=1, u=200, d=208, l=203, r=205,        57     56   29    184    157
Restore keyboard_load
.keyboard_load
Data 1,200,208,203,205,57,56,29,184,157
For x=0 To 9
 Read temp
 keytranslate(x)=temp
Next

Dim keyread(10);status of key assiged to the function

While KeyDown(1)=0

	Flip
	
	Cls

; put this in at the top of the main loop
      For x=0 To 10 ;scan keytranslate
    	y=KeyDown(keytranslate(x)) ;returns 0 or 1

        Select y+1
        Case 1;key is NOT pressed NOW
        If keyread(x)<0 Then keyread(x)=0;second cycle, set to not pressed
        If keyread(x)>0 Then keyread(x)=-1;JUST released, 1st cycle         
        ;can have more "cycles" if desired...can count from -10 to 0 say... 

        ;can time releases too-just put these in place of the 2 above:
        ;If keyread(x)>0 Then keyread(x)=0
        ;keyread(x)=keyread(x)-1
        ;0 would then be the "key has changed" flag

        Case 2 ;key pressed NOW
        If keyread(x)<0 Then keyread(x)=0
        keyread(x)=keyread(x)+1 ;times out keypress as long as held down
        ;actual time varies of course-sync to vblank?
        End Select
      Next

;General keyread values:
; 0=not pressed, 1=just pressed, 2 or more=held, -1=just released

;examples:
;If keyread(5)=-1;fire when key just released (weapons charge, or fighting game move)

;If keyread(5)>1;key is pressed,returns every cycle
;if keyread(5)=1;pressed key-returns only once!
;If keyread(5)>50 ;returns "pressed" continously after a delay
;if keyread(5)=50 ;returns "pressed" once after a delay
;if keyread(5)=1 or keyread(5)>30 ;returns once, then delay to repeating...

;I think this works very well!

;do rest of program, blah....

;show the values
For x=0 To 10
Text x*30,100,keyread(x)
Next


Wend
End
