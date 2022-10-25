; ID: 2361
; Author: Pongo
; Date: 2008-11-21 04:47:59
; Title: Simple buttons
; Description: Text and Graphical button code.

Graphics 640,480,0,2
SetBuffer BackBuffer()

Global mx,my,info$
Global buttonheld,ID_clicked

Type button
	Field image$	;image containing button graphics
	Field x				;x position of button
	Field y				;y pos of button
	Field width		;width of button
	Field height		;height of button
	Field id			;so we know which button has been pressed
	Field state		;mouse over/up/down
End Type

; create text button examples
;Create a button like this: CreateTextButton.button (Label$,	x,y,	width,height,	id,	centered = False)
b1.button = CreateTextButton ("Test A",		50,50,	100,20,	 1,	0)
b2.button = CreateTextButton ("Test B",	50,80,	100,20,	 2,	0)
;if you use the centered option, the button will x position will be overidden and the button will center on the screen vertical axis
centered.button = CreateTextButton ("Centered long type",50,80,150,20, 3,1)

;##	Create an image button like this:  CreateImageButton.button (image$,x,y,id,centered = False)
;##	buttonimage = LoadAnimImage ("buttontest1.png",128,32,0,3)
;##	b.button = CreateImageButton (buttonimage,	50,250,5)

; create image button example. You will need to supply your own image with 3 frames,... normal, hit and rollover)
;Create an image button like this:  CreateImageButton.button (image,x,y,id,centered = False)


;b.button = CreateImageButton (LoadAnimImage ("buttontest1.png",128,32,0,3),	50,250,	5)


While Not KeyHit(1)
	mx = MouseX () : my = MouseY() ; grab the mouse position at the top of the loop

	updatebuttons() ; check rollover state and draw buttons to screen. Call this every loop.
	buttonaction() ;this will execute the results of any button presses.

	Text 50,420,Info$

	If KeyDown(57)
		;draw a cursor crosshair on the screen with the mouse coords
		Line mx,0,mx,GraphicsHeight() : Line 0,my,GraphicsWidth(),my ; draw crosshair on screen
		Text 0,0,mx + "," + my
	EndIf 

	Flip
	Cls
Wend
End

Function CreateTextButton.button (Label$,x,y,width,height,id,centered = False)
	b.button = New button

	b\image = CreateImage(width,height,3)

	;normal state of button
	SetBuffer ImageBuffer(b\image,0)
	Color 140,140,140
	Rect 0,0,width,height,1 ;fill
	Color 0,255,0
	Rect 0,0,width,height,0 ;outline
	Color 255,255,255
	Text width*.5,height*.5,label$,1,1

	;hit state of button
	SetBuffer ImageBuffer(b\image,1)
	Color 220,220,220
	Rect 0,0,width,height,1 ;fill
	Color 255,0,0
	Rect 0,0,width,height,0 ;outline
	Color 0,0,0
	Text width*.5,height*.5,label$,1,1

	;Rollover state of button
	SetBuffer ImageBuffer(b\image,2)
	Color 180,180,180
	Rect 0,0,width,height,1 ;fill
	Color 0,255,0
	Rect 0,0,width,height,0 ;outline
	Color 255,255,255
	Text width*.5,height*.5,label$,1,1

	SetBuffer BackBuffer()

	b\x = x
	b\y = y
	b\width = ImageWidth(b\image)
	b\height = ImageHeight(b\image)
	b\id = id
	b\state = 0

	If centered Then b\x = (GraphicsWidth()*.5) - (b\width*.5) ;center on screen vertically

	Return b
End Function

Function CreateImageButton.button (image,x,y,id,centered = False)
	b.button = New button
	b\image = image
	MaskImage b\image,255,0,255 ; mask out pink color
	b\x = x
	b\y = y
	b\width = ImageWidth(image)
	b\height = ImageHeight(image)
	b\id = id
	b\state = 0

	If centered Then b\x = (GraphicsWidth()*.5) - (b\width*.5) ;center on screen vertically

	Return b
End Function

Function updatebuttons()
	For b.button = Each button
		If RectsOverlap(mx,my,1,1,  b\x, b\y, b\width, b\height)
			If MouseDown(1) Then
				b\state = 1 ; button is being held down over button
				buttonheld = 1 
			Else
				b\state = 2 ; mouse is hovering over button with no button down
				If buttonheld = 1	; was the button down last time? If so, mouse has just clicked up.
					ID_clicked = b\id ;this button has been clicked. set to ID of clicked button
					buttonheld = 0	;reset the button down value
				EndIf 					
			EndIf
		Else		; mouse is not over button
			b\state = 0	;normal state of button				
		EndIf
		
		DrawImage	b\image,b\x,b\y,b\state ; draw the button to screen
	Next 
End Function

Function ButtonAction()
	;Use this function to take action based on which button has been pressed. Each time you make a button, give it a unique ID.
	;Then add a new case for that ID here.
	Select ID_clicked
		Case 1
			;this button ID has been clicked. Do stuff here
			info$ = "Button A has been clicked"
						
		Case 2
			;this button ID has been clicked. Do stuff here
			info$ = "Button B has been clicked"

		Case 3
			;this button ID has been clicked. Do stuff here
			info$ = "Centered button has been clicked"

		Case 5
			;this button ID has been clicked. Do stuff here
			info$ = "Image button has been pressed"

	End Select

	ID_clicked = -1

End Function
