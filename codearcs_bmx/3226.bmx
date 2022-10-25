; ID: 3226
; Author: Mainsworthy
; Date: 2015-09-26 14:40:15
; Title: boardgame creator
; Description: create hex & chit

'you must provide 2 files for this program to work troops.png 20 pixels high 40 across 2 chits
'a 1024x768 backdrop

Graphics 1024,768,32,60
Global chit = LoadAnimImage(".\troops.PNG",20,20,0,2,flags=ALPHABITS  ) '2 twenty by twenty chits
Global backdrop = LoadImage(".\backdrop.PNG"  ) 'plain backdrop

Global gameboard[10,10,20] '10 x 10 grid gameboard with 20 items of info per location
Global x1 = 0
Global y1 = 0
'try setting the gameboard as shown below
gameboard[1,1,1] = 1
gameboard[1,3,1] = 1


While Not KeyHit(KEY_ESCAPE) 'hit escape to exit

Cls 'clear screen before redrawing each loop through

DrawImage(backdrop ,0,0) 'draw backdrop

'use a for next loop to draw chits
For x= 0 To 9 
For y = 0 To 9 
If gameboard[x,y,1] = 1 Then DrawImage(chit,x*20,y*20,frame=1) 'frame is chit number
Next
Next

' devise x and y by 20 pixels, this is because the chits are 20 pixels
'the idea is to find where the mouse pointer is 
x1 = MouseX() /20
y1 = MouseY() /20

'this sets gameboard on off by left or right clicking
If x1 < 10 And x1 > -1 And y1 < 10 And y1 > -1
If MouseDown(1) 'Left click set on
gameboard[x1,y1,1] = 1
EndIf
If MouseDown(2) 'right click set off
gameboard[x1,y1,1] = 0
EndIf
EndIf



Flip 'this flips the board onto the screen

Wend

End
