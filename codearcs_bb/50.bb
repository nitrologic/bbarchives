; ID: 50
; Author: smitty
; Date: 2001-09-24 07:30:39
; Title: flood fill
; Description: non recursive flood fill routine

; non-recursive flood fill routine 
; steven smith.2001 
; Adapted from  code by Petter Holmberg.
; Get the original source code from www.basicguru.com/abc/graphics.htm
;*********
;lots of colours
Graphics 640,480,32
; type to hold pixel coords
Type point
Field x,y
End Type
; size of internal stack to hold fill coords.
;Increase this number if you get holes in complex fills. 
Const fillstack=5000
;Create a stack of point types
Dim pixdata.point(fillstack)
For f=0 To fillstack
 pixdata(f)=New point
Next
; Limit the fill routine to screen size. 
Global fillheight=GraphicsHeight()
Global fillwidth=GraphicsWidth()
;
;draw some cicles and fill them with random colours
	For x=0 To 150
		Color Rnd(255),Rnd(255),Rnd(255)
		Oval Rnd(640),Rnd(480),Rnd(250)+100,Rnd(250)+100,0
	Next
;****READ HERE FOR DEBUG DISABLED
For f=0 To 1000
	mcol=(Rnd(255)*$10000)+Rnd(255)*$100+Rnd(255)
	floodfill(Rnd(640)+50,Rnd(480)+50,mcol)
	Flip;rem this out if you have debug disabled
	If KeyHit(1) Then Exit
Next
;
WaitKey
End
;***************
; flood fill routine
Function floodfill(x,y,col)
;x,y= coords to fill from
;col= colour to fill with
LockBuffer FrontBuffer()
;get the background colour
bcol=ReadPixelFast(x,y)
;exit if both colours the same.otherwise the function will loop indefinitely.
If bcol=col Then Goto endfunc
;clear the coord stack
For f=0 To fillstack
	pixdata(f)\x=0
	pixdata(f)\y=0
Next

firstentry=0
lastentry=1
Repeat
	fx=pixdata(firstentry)\x
	fy=pixdata(firstentry)\y
		Repeat	
			If ReadPixelFast(x+fx,y+fy)=bcol And x+fx>=0 And x+fx<fillwidth And y+fy>=0 And y+fy<fillheight
			WritePixelFast x+fx,y+fy,col
				If ReadPixelFast(x+fx,y+fy-1)=bcol
					pixdata(lastentry)\x=fx
					pixdata(lastentry)\y=fy-1
					lastentry=lastentry+1
					If lastentry=fillstack+1 Then lastentry=0
				EndIf
				If ReadPixelFast(x+fx,y+fy+1)=bcol 
					pixdata(lastentry)\x=fx
					pixdata(lastentry)\y=fy+1
					lastentry=lastentry+1
					If lastentry=fillstack+1 Then 	lastentry=0
				EndIf
			Else
				Exit
			EndIf
			fx=fx+1
		Forever	
 
	fx=pixdata(firstentry)\x-1
	fy=pixdata(firstentry)\y
	    Repeat	
			If ReadPixelFast(x+fx,y+fy)=bcol And x+fx>=0 And x+fx<fillwidth And y+fy>=0 And y+fy<fillheight
			WritePixelFast x+fx,y+fy,col
				If ReadPixelFast(x+fx,y+fy-1)=bcol
					pixdata(lastentry)\x=fx
					pixdata(lastentry)\y=fy-1
					lastentry=lastentry+1
					If lastentry=fillstack+1 Then lastentry=0
				EndIf
				If ReadPixelFast(x+fx,y+fy+1)=bcol 
					pixdata(lastentry)\x=fx
					pixdata(lastentry)\y=fy+1
					lastentry=lastentry+1
					If lastentry=fillstack+1 Then lastentry=0
				EndIf
			Else
				Exit
			EndIf
			fx=fx-1
		Forever
	
	firstentry=firstentry+1
	If firstentry=fillstack+1 Then firstentry=0
Until firstentry=lastentry ;
.endfunc
UnlockBuffer FrontBuffer()
End Function
;*********************
