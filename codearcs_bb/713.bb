; ID: 713
; Author: BlitzSupport
; Date: 2003-06-07 09:54:37
; Title: ScaleImageFast
; Description: This code is someone else's -- a fast image scaler

; These functions were NOT written by me! Someone posted them somewhere ages ago,
; but didn't put their name in the source. I have a vague feeling the guy's name
; might have been Daniel something, but I may just be hallucinating. If you wrote
; these, let me know!

; I have just added two lines to each function to store and restore the current
; graphics buffer and made this crappy little demo -- use the mouse buttons
; to big up/smallify...

adder = 16

Graphics 640,480
SetBuffer BackBuffer ()

image = LoadImage ("F:\My Documents\Development\Blitz 2D Sources\boing.bmp")

scalex# = ImageWidth (image)
scaley# = ImageHeight (image)

While Not KeyHit(1)
	
    Cls

    If MouseDown(1)
		scalex = scalex + adder
		scaley = scaley + adder
	Else
	    If MouseDown(2)
			scalex = scalex - adder: If scalex <= 2 Then scalex = 2
			scaley = scaley - adder: If scaley <= 2 Then scaley = 2
		EndIf
	EndIf

    scaledimage = ScaleImageFast (image, scalex, scaley)

	MidHandle scaledimage
    DrawImage scaledimage, MouseX (), MouseY ()
    FreeImage scaledimage

    Flip

Wend

;___________________________________________________________________

Function ScaleImageFast(image,newwidth,newheight,frame=0)

	tbuffer = GraphicsBuffer ()
	
    oldwidth = ImageWidth(image)
    oldheight = ImageHeight(image)

    newwidth=newwidth-1
    newheight=newheight-1
    ni=CreateImage(newwidth+1,oldheight)
    dest = CreateImage(newwidth+1,newheight+1)
    SetBuffer ImageBuffer(ni)
    For x = 0 To newwidth
        LineRef = Floor((oldwidth*x)/newwidth)
        DrawBlockRect image,x,0,LineRef,0,1,oldheight,frame
    Next
    SetBuffer ImageBuffer(dest)
    For y = 0 To newheight
        LineRef = Floor((oldheight*y)/newheight)
        DrawBlockRect ni,0,y,0,LineRef,newwidth,1
    Next 
    FreeImage ni

	SetBuffer tbuffer
	
    Return dest
End Function

Function ScaleImageFast1(image,newwidth#,newheight#,frame=0)

	tbuffer = GraphicsBuffer ()
	
    oldwidth# = ImageWidth(image)
    oldheight# = ImageHeight(image)

    newwidth=newwidth-1
    newheight=newheight-1
    ni=CreateImage(newwidth+1,oldheight)
    dest = CreateImage(newwidth+1,newheight+1)
    SetBuffer ImageBuffer(ni)
    For x# = 0 To newwidth
        LineRef# = Floor((oldwidth*x)/newwidth)
        DrawBlockRect image,x,0,LineRef,0,1,oldheight,frame
    Next
    SetBuffer ImageBuffer(dest)
    For y# = 0 To newheight
        LineRef = Floor((oldheight*y)/newheight)
        DrawBlockRect ni,0,y,0,LineRef,newwidth,1
    Next 
    FreeImage ni
	SetBuffer tbuffer
	
    Return dest
End Function
