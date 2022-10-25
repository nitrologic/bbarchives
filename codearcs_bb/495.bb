; ID: 495
; Author: Ghoula
; Date: 2002-11-19 16:11:23
; Title: Radar Scan: Is that enemy within range?
; Description: Will scan a given radius and return true/false if target is in range.

;---------------------------------------------------------------
;------------------Radar Check 1.0------------------------------
;---------------------by  Ghoula--------------------------------

;This example will show you how to check if an enemy unit 
;is within a freindly unit's attack radius. This example 
;is a visual representation of the logic involved, so about 95%
;of this code is for pulling off the visuals. Function: 'RealCheckRange'
;at the bottom of this code shows only the hardcore logic needed.
;It's actually quite small :)


;I use this function in my current project for satellites and
;performing air unit strikes (if enemy is in range) and also
;for rocket launcher units (again: if enemy is in range he can be shot)
;I also searched high and low for a routine or snipet of code
;that could do this and found nothing. So with a little patience i made
;my own Function. Maybe someone has a use for this if so feel free To
;use Or abuse code in anyway! :)

Graphics 640,480

Global unit_x,unit_y		;Storage - Freindly unit X and Y map location
Global target_x,target_y	;Storage - Enemy unit X and Y map location
Global range				;Storage - Scan range of unit

;Setup a small 2d map for visuals
Type tiledata
	Field x_loc
	Field y_loc
End Type
SeedRnd MilliSecs()			;Always seed the timer for randomness
SetBuffer FrontBuffer()		;We will draw straight to the frontbuffer nothing fancy

;Set friendly unit location and his radar range
unit_x=10
unit_Y=7
range=6 ;This value is from unit outward. Since we scan a radius the actual scan area is x2 or 12 if value is 6

;Main loop for visuals, will keep scanning until ESC is hit
Repeat

	;Draw map tiles
	DrawTiles()
	;Check our radar to see if enemy is within range. Will return true or false
	InRange=Func_CheckRange(unit_x,unit_y,target_x,target_y,range)

	;Check range returned true so enemy is within range
	If InRange=True
		Color 0,255,0
		Text 0,0,"Target is in range!! hooah!"
	;Returned false, enemy not in range
	Else
		Color 255,0,0
		Text 0,0,"Target not in range"
	EndIf
	Delay 1000 ;Delay fer visuals
	
Until KeyHit(1)
End

;----Func: Check range---------------------------------------------------
Function Func_CheckRange(ux,uy,tx,ty,rng)

	;For illustration purposes this function has been tailored to simulate
	;32x32 pixel sized tiles but is easily adjustable to work on different
	;size tiles. Also keep in mind mmost of this crap is for the visuals :)
	
	;Draw radius circle
	Color 255,0,0
	Oval ((ux*32)-rng*32)+16,((uy*32)-rng*32)+16,rng*64,rng*64,0
	
	;Scan logic, notice that only a 90 degree sweep is needed to effectively
	;scan a 360 degree radius. The visuals will show you the logic in action
	
	For degrees=0 To 90 Step 5 ;The smaller the scan area the higher the step can be
							   ;if you are scanning large areas then lower the step value
							   ;to aviod 'gaps'. Observe the blue line when program is running.
							   ;In this example 5 is actually overkill could get away with
							   ;a higher value since our scan radius is small.
							
		Delay 100 ;A delay simply for visuals, remove delay and it's lightning quick
		scanx=ux*32+16+Sin(degrees)*rng*32 ;<< The SIN logic
		scany=uy*32+16+Cos(degrees)*rng*32 ;<< The COS logic
		Color 0,50,255
		Line scanx,scany,ux*32+16,uy*32+16 ;Blue degree line (visual)
		scanx=Abs(scanx-ux*32) ;<< Some gnomish magic (using ABS to keep the number posistive)
		scany=Abs(scany-uy*32) ;<< "
		
		;Here we will color the tiles we have scanned yellow
		For tile.tiledata=Each tiledata
			If tile\x_loc>(ux*32)-scanx And tile\x_loc<(ux*32)+scanx And tile\y_loc>(uy*32)-scany And tile\y_loc<(uy*32)+scany
				Color 220,220,0
				Rect tile\x_loc,tile\y_loc,32,32,0
				;Target in range return true
				If tile\x_loc=tx*32 And tile\y_loc=ty*32 Then Return True
			EndIf
		Next
	Next
	
	;Target not in range, return false
	Return False

End Function

;----Draw tiles (illustration purposes)-----------------------------------
Function DrawTiles()

	;Draw map grid
	Cls
	x=0
	y=0
	Color 111,111,111
	For t=1 To 300
		Rect x,y,32,32,0
		x=x+32
		tile.tiledata=New tiledata
		tile\x_loc=x
		tile\y_loc=y
		If x=>640
			x=0
			y=y+32
		EndIf
	Next
	;Draw unit
	Color 0,220,0
	Rect unit_x*32,unit_y*32,32,32,1
	;Draw target unit at random location
	target_x=Rand(19) ;Remember Rand for integers and Rnd fer floating numbers! :)
	target_y=Rand(14) ; 						""
	Color 255,0,0
	Rect target_x*32,target_y*32,32,32,1

End Function




;---------------------------------------------------------------------------
;----Here is the radar check function stripped down and ready fer action----
;----If your are using diffrent size tiles say 48x48 then change all 32s----
;----to 48 and all 16s to 24. You get the picture :P
;---------------------------------------------------------------------------

;----Func: Check range------------------------------------------------------
Function Func_RealCheckRange(ux,uy,tx,ty,rng)

	For degrees=0 To 90 Step 5
		scanx=ux*32+16+Sin(degrees)*rng*32
		scany=uy*32+16+Cos(degrees)*rng*32
		scanx=Abs(scanx-ux*32)
		scany=Abs(scany-uy*32)
		;Scan tiles		
		For tile.tiledata=Each tiledata
			If tile\x_loc>(ux*32)-scanx And tile\x_loc<(ux*32)+scanx And tile\y_loc>(uy*32)-scany And tile\y_loc<(uy*32)+scany
				;In range
				If tile\x_loc=tx*32 And tile\y_loc=ty*32 Then Return True
			EndIf
		Next
	Next
	;Not in range
	Return False

End Function
