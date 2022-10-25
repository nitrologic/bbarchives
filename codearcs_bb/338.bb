; ID: 338
; Author: Richard Betson
; Date: 2002-06-07 16:49:03
; Title: Blur Image
; Description: One way to blur an image.

;**************************************************************************
;							Blur Demo 1.a
;							  RedEyeWare 
;					   www.redeyeware.50megs.com
;					 Copyright 2002, Richard Betson
;						Email: vidiot@getgoin.net
;
; This code is free for all to use!
; This code is supplied "as-is"  use at your own risk!
;
;
;**************************************************************************

Const sw=640
Const sh=480

Graphics sw,sh,16

;===========================================================================
;				Variables.......
;===========================================================================
Dim pal(9)
Global blur=.001
Global fade=0
Const  reps=0

;=============================================================
;              Load Image and get size)
;=============================================================
logo=LoadImage("YourFile.bmp")
iw=ImageWidth(logo)
ih=ImageHeight(logo)


SetBuffer FrontBuffer()

;==========================================
;         Draw my logo on the screen :)
;==========================================
For i= 0 To (sh/ih)

	For ii=0 To (sw/iw)
		DrawImage logo,x,y 
		x=x+iw
		If x>=sw Then x=0
	Next
	y=y+ih
	
Next

;==========================================
;        Setfont
;==========================================
fntArialB=LoadFont("Arial",24,True,False,False)
SetFont fntArialB

Color 255,255,255
Text (sw/2),(sh/2),"Blur and Fade using Wirtepixelfast",True,True
Text (sw/2),(sh/2)+20,"and Readpixelfast..",True,True
Color 0,0,255
a$=Input$("Press 1. for standard blur/fade. Press 2. or 3. for a diffrent kind :)")

;================================================================
;                        MAIN LOOP
;              Lock buffer, draw, unlock buffer
;================================================================

For i=0 To reps

	LockBuffer FrontBuffer()

		If a$="1"
		update_screen_1()
				EndIf

		If a$="2"
		fade=1
		update_screen_1()
		EndIf

		If a$="3"
		update_screen_2()
		update_screen_1()
		EndIf

	UnlockBuffer FrontBuffer()
	

Next 

Delay 4000

Function update_screen_1()

For y=1 To sh
;--------------------------------------------------------------
;Read pixel above,below,left, right of the current x,y position
;--------------------------------------------------------------
	For x=1 To sw
		pal(1)=ReadPixelFast (x,y)And $FFFFFF
		pal(2)=ReadPixelFast (x+1,y)And $FFFFFF
		pal(3)=ReadPixelFast (x-1,y)And $FFFFFF
		pal(4)=ReadPixelFast (x,y+1)And $FFFFFF
		pal(5)=ReadPixelFast (x,y-1)And $FFFFFF
;--------------------------------------------------------------
;Average the above by adding and then dividing.
;Then write to screen. Fade offsets the pixel that is written.
;--------------------------------------------------------------
		pal2=((pal(1)*blur)+pal(2)+pal(3)+pal(4)+pal(5))/(4+blur)
		

			If pal2<=0 Then pal2=0
		WritePixelFast x,y+fade,pal2
	Next
Next

End Function


Function update_screen_2()

For y=1 To sh
;--------------------------------------------------------------
;Read pixel above,below,left, right of the current x,y position
;--------------------------------------------------------------
	For x=1 To sw
		pal(1)=ReadPixelFast (x,y)And $FFFFFF
		pal(2)=ReadPixelFast (x+1,y)And $FFFFFF
		pal(3)=ReadPixelFast (x-1,y)And $FFFFFF
		pal(4)=ReadPixelFast (x,y+1)And $FFFFFF
		pal(5)=ReadPixelFast (x,y-1)And $FFFFFF
		pal(6)=ReadPixelFast (x+1,y-1)And $FFFFFF
		pal(7)=ReadPixelFast (x-1,y+1)And $FFFFFF
		pal(8)=ReadPixelFast (x+1,y+1)And $FFFFFF
		pal(9)=ReadPixelFast (x-1,y-1)And $FFFFFF
;--------------------------------------------------------------
;Average the above by adding and then dividing.
;Then write to screen. Fade offsets the pixel that is written.
;--------------------------------------------------------------
		pal2=((pal(1)*blur)+(pal(2)+pal(3)+pal(4)+pal(5)+pal(6)+pal(7)+pal(8)+pal(9)))/(8+blur)
	
			If pal2<=0 Then pal2=0
		WritePixelFast x,y,pal2
		
	Next
Next

End Function
