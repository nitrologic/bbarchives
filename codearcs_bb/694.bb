; ID: 694
; Author: Rob Farley
; Date: 2003-05-16 07:35:05
; Title: Smooth Marquee
; Description: Smoothly scroll messages across the screen

;smooth marquee
; 2003 Rob Farley
; http://www.mentalillusion.co.uk
; rob@mentalillusion.co.uk

Graphics 640,480

message$="Example smooth horizonal scroller using text command, press esc to quit. This works with any size font, however, you may want to mask the edges either by scrolling the entire screen width or by adding a couple of blocks either side after the text command."

font=LoadFont("arial.ttf",30,True)
SetFont font




SetBuffer BackBuffer()

;width in pixels of the scroll (this is slightly bigger than the screen)
width=680

; create space padding for the beginning and end of message
padding$=""
Repeat
	padding=padding+" "
	Until StringWidth(padding)>=width

; add the padding to be beginning and end of message
message=padding+message+padding

; where you want to put the scroller (right over on the left so you don't get nasty bits at the edges)
xoffset=0
yoffset=240-(StringHeight("A")/2)



a=1

Color 255,255,255

Repeat

; find the next section of text to display
wid=1
Repeat
	If a+wid>Len(message$) Then a=1 ;loop back at end of message
	display$=Mid$(message$,a,wid)
	wid=wid+1
	Until StringWidth(display)>=width

; find the amount of scroll you need to move it
scroll=StringWidth(Mid$(message$,a,1))

; smoothly move the text
For n=0 To scroll-1
Cls
Text xoffset-n,yoffset,display
Flip
Next

;go to the next letter
a=a+1

Until KeyHit(1)
