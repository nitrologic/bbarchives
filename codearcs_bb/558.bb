; ID: 558
; Author: Rob Farley
; Date: 2003-01-24 13:43:12
; Title: Credits Scroller
; Description: Gives you a movie end credits thing

; Credits scroller
; 2003 Mental Illusion
; rob@mentalillusion.co.uk
; http://www.mentalillusion.co.uk

;---------------------------------------------------------------------------

; Text file format is rrrgggbbbx, where rrr is a 3 digit red value ie 054, ggg and bbb are for green and blue
; x is the font controller set up below.
; if you set 9999999991 this is for an image, the x value can be any legal font value you then follow this
; with the image path (see example below).

; This assumes you've got blitz3d installed in c:\program files if not change the line in the text file
; 9999999992c:\program files\blitz3d\tutorials\gcuk_tuts\b3dlogo_small.jpg
; to the correct path.

; hopefully this should be self explanitary


;set graphics mode
Graphics 640,480,32
SetBuffer BackBuffer()

; set up fonts
font1=LoadFont("Arial.TTF",11,True,False,False)
font2=LoadFont("Arial.TTF",21,False,False,False)
font3=LoadFont("Times.TTF",31,False,True,False)


; count the lines in the file
lines=0
filein = ReadFile("credits.txt")
Repeat
temp$=ReadLine$(filein)
lines=lines+1
Until temp$="end"
CloseFile( filein )


; set up arrays for holding text, images, and colours
Dim textlines$(lines)
Dim textcolour(lines,3)
Dim textsize(lines)
Dim images(lines)


; open up the file again and read it all in
filein = ReadFile("credits.txt")
For n=1 To lines
	temp$=ReadLine$(filein)
	textcolour(n,1)=Int(Left$(temp$,3))
	textcolour(n,2)=Int(Mid$(temp$,4,3))
	textcolour(n,3)=Int(Mid$(temp$,7,3))
	textsize(n)=Mid$(temp$,10,1)
	textlines$(n)=Mid$(temp$,11,Len(temp$)-10)
	If textcolour(n,1)+textcolour(n,2)+textcolour(n,3)=2997 Then images(n)=LoadImage(textlines$(n)) ;check for an image
	Next
CloseFile( filein )

;---------------------------------------------------------------------------


; Run the scroller
Gosub scroller



End

;---------------------------------------------------------------------------

.scroller

voffset=60
rows_displayed=20

;voffset will set how far off the top of the screen it will roll, with bigger fonts/images this needs to be higher
;if you have it too high you will lose rows off the bottom and you will therefore need to change the rows displayed
;on top of this you have to put blank padding at the beginning and end of the text file to make it seamless

For l=1 To lines-rows_displayed

For move=1 To 30 ; The lines are 30 pixels apart


Cls

; you'll see a lot of 640s and 320s here, this is because it's hard coded for 640x480, but you can change this
; the problem you'll encounter is as the screen y size increases you need to add more rows displayed

; draw the background
For n=0 To 240
	Color 0,0,n
	Rect 0,2*n,640,2,True
	Next

; draw the text/images
For n=0 To rows_displayed

	Color textcolour(n+l,1),textcolour(n+l,2),textcolour(n+l,3)
	If textsize(n+l)=1 SetFont font1
	If textsize(n+l)=2 SetFont font2
	If textsize(n+l)=3 SetFont font3

	If textcolour(n+l,1)+textcolour(n+l,2)+textcolour(n+l,3)<2997
		Text 320,(n*30)-move-voffset,textlines$(n+l),True,True
		Else
		DrawImage images(n+l),320-(ImageWidth(images(n+l))/2),(n*30)-move-voffset-(ImageHeight(images(n+l))/2)
		EndIf
	Next

If KeyDown(1) Return ;quit out

; wait for vblank and flip the screen
VWait
Flip
Delay 20
Next
Next
Return
