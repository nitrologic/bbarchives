; ID: 46
; Author: Snarty
; Date: 2001-09-21 06:03:17
; Title: Mouse Routine
; Description: A mouse routine for full screen programs including double buffering.

; mx,my = Mouse position
; hmbut = wether Left mouse button held down
; mbut  = last mouse button pressed
; mstop = Send this as true from ALL functions to stop over hitting mouse button

Global mx,my,hmbut,mbut,mstop,mmx,mmy
Dim mouse(4)

Graphics 800,600,0,1				; Set full screen, for proper testing.
ChangeDir "C:\My Documents\My Projects\Sprite Editor"

mouse(0)=LoadImage("Interface GFX\mouse.bmp")		; Grab these images from SMB editors (Interface GFX) drawer
AutoMidHandle True									; Or name your own images.
mouse(1)=LoadImage("Interface GFX\mouseplot.bmp")
AutoMidHandle False

SetBuffer BackBuffer()
holdscreen = CreateImage(800,600)	; create, in effect, a 3rd buffer screen.
;
;  ** Create all of fixed/background screen gfx here, ie - build the main screen gfx (speeds gfx drawing with one block)
;
GrabImage holdscreen,0,0 ; Place static background into memory

While Not KeyDown(1)				; Wait for escape key,
	setup(holdscreen,False,0)		; First pass
	;
	; ** Do gfx updates, mouse checking etc. (Replace with your code)
	;
	setup(holdscreen,True,0)		; Finally overlay mouse, and update display
Wend

EndGraphics
End

Function SetUp(Image,Way,MouseStyle)				; Use Way=False for initial setup of screen, then "True" for gfx & mouse updates.

	DebugLog mouse(0)
	
	getf1=KeyDown(59)								; 
	If getf1=True									;
		GrabImage screengrab,0,0					; F1 Screen grab function
		SaveImage screengrab,"Screengrab.bmp"		;
	EndIf											;

	If way=False
	
		Cls											; Start of double buffer routine
		DrawBlock Image,0,0							; Draw the 3rd buffer on the back buffer with no transparancy.
		If mousestyle>-2							; See Below about mousestyles.
			mmx=MouseXSpeed():mmy=MouseYSpeed()		; Holds the last registered mouse speeds in a global, for stability.
			mbut=GetMouse():hmbut=MouseDown(1)		; grab main left mouse button (hmbut) and also check button clicked last.
			If hmbut=True And mstop=True			; mstop = global var to stop mouse clicks being overlapped in routines.
				hmbut=False:mbut=False				; is mstop = true, and left button is pressed down, then ignore new click
			Else									; or
				If hmbut=False And mstop=True		; reset mstop to false if the left mouse button has been released.
					mstop=False
				EndIf
			EndIf
		EndIf
	
	Else
		If mousestyle<>-1						; mousestyle set at -1 = no mouse image to draw.
			If mousestyle>-2					; mousestyles you wish to display above -1 (ie, check above).
				mx=MouseX() : my=MouseY()		; grab the mouse x and y position into vars mx,my to keep all checks the same.
				MoveMouse mx+mmx,my+mmy			; move the hardware mouse co-ords to the last check pos, mmx & mmy = speed.
			Else
				mousestyle=Abs(mousestyle)-2	; Used to avoid moving, and re-checking the mouse if the program uses its own routine.
			EndIf
			DrawImage mouse(mousestyle),mx,my	; Actually Draw the mouse image on the screen, in the original check position.
		Else
			MouseStyle=0		
		EndIf
		
		
		Flip									; Double buffer stuff
		VWait									; Wait for a vertical blank, in order to sync.

	EndIf
End Function
