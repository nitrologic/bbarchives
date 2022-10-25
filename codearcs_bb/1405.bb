; ID: 1405
; Author: Gary B
; Date: 2005-06-21 13:30:19
; Title: Conway's Life Algorithm
; Description: Another Life Iteration

; Conway's Game of Life
; Coded by Gary Barnes - The Control Key (June 2005)
; I did it because I could, and it was interesting.
; A little raw, but optimised for speed
; It isn't very fancy and could stand tarting up a little
; Still it fair blitzters along given that it is updating 
; 960,000 array elements And 480,000 pixels Each display redraw

; Maybe someone will find it useful, or at least mildly interesting

Global w = 800
Global h = 600 ; size of a board (increase the size If you like)


Graphics w,h
SetBuffer BackBuffer() 


Dim tG(w,h)			; this generation
Dim nG(w,h)			; next generation

h=h-1				; set height for life array - 1 less than the screen height to avoid boundary problems
w=w-1				; set width - look above

SeedRnd MilliSecs()	; reset the random number generator	

Color 255,255,255	; set the colour of the writepixel fast routine for later
Plot 0,0
tcol = ReadPixel(0,0)

ClsColor 0,0,64		; I liked white on blue, change it at will

Repeat				; main program loop
   For y = 1 To H	; seed this generation array randomly
       For x = 1 To W
           z = Rnd(1,10)	; change from 10 to whatever you like - 2 is too crowded 
           If z = 1 Then tG(x,y) = 1 ; between 10 and 50 odd gives a pleasing result
       Next
   Next

   Repeat
      If Rnd(0,99) > 90 Then		; sets a 10% chance of reseeding a small part of the current generation matrix
         rsx = Rnd(10,w-20)
         rsy = Rnd(10,h-20)
  
         For p = 0 To 9				; do it 10 times 
             rx = Rnd(rsx,rsx+5)
             ry = Rnd(rsy,rsy+5)
             tg(rx,ry) = 1
         Next 
      EndIf
      Gosub paintscreen				; draw it
      dummy = GetKey()				; get something from the keyboard buffer
      If dummy = 32 Then WaitKey()	; it is space so pause the program
      If dummy = 27 Then End 		; it is escape so stop
   Forever 
Forever

End

.PaintScreen
For y = 1 To H
    For x = 1 To W
        sum = 0 
        sum = sum + tg(x-1,y-1) + tg(x,y-1) + tg(x+1,y-1) 	; life needs to know how many neighbours a cell has
        sum = sum + tg(x-1,y)   + tg(x+1,y) 				; this routine just adds up the number of occupied cells
        sum = sum + tg(x-1,y+1) + tg(x,y+1) + tg(x+1,Y+1)   ; around the one of interest - tg(x,y)
        Select sum                                          ; implement the algorithm
               Case 2  : If tg(x,y) = 1 Then ng(x,y) = 1 	; if the cell is alive and it has two neighbours it stays alive
               Case 3  : ng(x,y) = 1                        ; if any cell has three neighbours it bursts into life or stays alive   
               Default : ng(x,y) = 0						; for any other sum, the cells dies if it is alive
        End Select											; that is it - life game all done
	Next
Next


Cls															; clear the screen as we only write pixels if we have to
LockBuffer													; as the little routine is optimised for speed
For y = 1 To H
    For x = 1 To W
        If tg(x,y) > 0 Then WritePixelFast x,y,tcol
        tg(x,y) = ng(x,y)									; copy the next generation to the current generation to display later
    Next
Next
UnlockBuffer	; you have to lock then unlock the screen buffer otherwise writepixelfast won't work !
Flip			; all done display the new page and return						

Return
