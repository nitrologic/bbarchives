; ID: 1604
; Author: Paul Murray
; Date: 2006-01-27 13:59:31
; Title: 2D Click 'n' Move
; Description: Move an image to the last position clicked with the mouse

;************************************************
;	2D Click 'n' Move Example - written by Muzzer 2006	
;************************************************		
;				
; 	This code should give beginners an idea about	  
; 	moving an image to a position clicked on screen 	
;
;	Experienced Blitz users may think it's written
;	incorrectly, and they're probably right.
;
;	Anyway, it works.
;
;	Soldier sprite ripped from Metal Slug and grabbed from http://www.emugifs.emuita.it
;
;************************************************


AppTitle "2D Click 'n' Move Example"

Graphics 640,480,0,2

SetBuffer BackBuffer()

Global LastClickX = 400		;these coords are the same as the image start position to 
Global LastClickY = 240		;prevent it movingas soon as the program runs

Global ShipX = 400			;Image start postions
Global ShipY = 240

imgMove = LoadImage("soldier.PNG")
MaskImage imgMove,255,0,255

;------------------
;Main Loop
;------------------

While Not KeyHit(1)
Cls

	;set the LastClick positions
	If MouseDown(1) Then LastClickX = MouseX() LastClickY =  MouseY()	;if left button is pressed, set the LastClick variables to the current MouseX() and MouseY() coordinates
			
	
;DrawImage imgBack,0,0
DrawImage imgMove,ShipX - 32, ShipY -32	; the -32 offsets the image so the centre moves to the clicked point

	If ShipX < LastClickX Then ShipX = ShipX + 2	;if the image's X position is less than the LastClickX coord we set, move 2 units to the right
	
	If ShipX > LastClickX Then ShipX = ShipX - 2	;if the image's X position is greater than LastClickX, move 2 units to the left
		
	If ShipY < LastClickY Then ShipY = ShipY + 2	;if the image is lower than the LastClickY variable, move the image down
	
	If ShipY > LastClickY Then ShipY = ShipY - 2	;if the image is higher than the LastCLickY variable, move the image up
	
	
	;------------------
	;Debug Text	- simply to display the coordinates on screen
	;------------------
	

	Text 20,15, "Last ClickX = " + LastClickX	;displays the last 'X' coordinates clicked
	Text 20,30, "Last ClickY = " + LastClickY	;displays the last 'Y' coordinates clicked
	
	;constantly displays the Mouse coordinates
	Text 20,45, "Mouse X = " + MouseX()			
	Text 20,60, "Mouse Y = " + MouseY()
	
Flip

Wend

End
