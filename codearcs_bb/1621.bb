; ID: 1621
; Author: markcw
; Date: 2006-02-16 18:14:08
; Title: Logistic Equation Bifurcation Diagram
; Description: fractal-type explorer

;Logistic Equation Bifurcation Diagram, on 16/2/06
;From C source code by Frizzi, University of Milan
;Adapted from Mandelbrot Fractal code, by filax & fredborg
;The standard logistic equation is: f(x) = R x (1 - x)

;do: Init

window=2 ;window mode
resmode=0 ;resolution

If resmode=0
 width=640
 height=480
Else
 width=800
 height=600
EndIf

AppTitle "Logistic Equation Bifurcation Diagram"
Graphics width,height,16,window
SetBuffer BackBuffer()

;do: Set Palette

cmax = 24
Dim col(cmax)

DrawGradientLine(cmax-1,0,200,200,0,0,0) ;colour
;DrawGradientLine(cmax-1,200,200,200,0,0,0) ;grey

LockBuffer
For i=0 To cmax-1
 ;col(i)=ReadPixelFast(i,1) * 8 And $FFFFFF ;colour
 col(i)=ReadPixelFast(i,1) And $FFFFFF ;grey
Next
UnlockBuffer

iterx = 500 ;set iterances to filter attractor
itery = 500 ;set iterances for every wrap to attractor (y density)
fixr# = 2.9 ;set population growth rate (r), range is 1-4

;do: Draw Fractal

.reset

dxy# = height / Abs(fixr-4) ;xy scale
dcx# = 0 ;x axis
dcy# = dxy * fixr ;y axis
scale# = 1 ;zoom
lastx# = 0
lasty# = 0

.redraw

r# = fixr
r# = r + (4-fixr) * Float(lasty-1) / Float(height)
x# = 0.4
rinc# = 1/fixr/scale/itery
If rinc<0.0000002 Then rinc=0.0000002 ;limit min, 2.-e7
n = 0
px = 0
py = 0

ClsColor 255,255,255
Cls
Text 0,0,"scale="+Int(scale)

While r<=4 ;max growth rate
 LockBuffer
 For j=1 To 16 ;speed up rate
  For i=1 To iterx-1 ;filter attractor
   x# = x * r * (1-x)
  Next
  n = n + 1
  For i=1 To itery-1 ;wrap attractor
   x# = x * r * (1-x)
   colour = (n Mod 15) + 1 ;colour 1-15
   n = n + 1
   px = dxy * x - dcx
   py = dxy * r - dcy
   If px>=0 And px<width And py>=0 And py<height
    WritePixelFast px,py,col(colour)
   EndIf
   If KeyDown(1) Then End ;Esc key
  Next
  If py<-20 ;speed up rate
   r# = r + 1/fixr/scale/10
  Else
   r# = r + rinc
  EndIf
  For i=1 To 8 ;speed up rate
   If py+height/4<-scale/Float(i*i)
    r# = r + 1 / scale / Float(i*i)
   EndIf
  Next
 Next
 UnlockBuffer
 If window<2 Then Flip ;two flips in fullscreen
 Flip
 If py>height-1 Then Exit ;stop drawing
Wend

image=CreateImage(width,height)
CopyRect 0,0,width,height,0,0,BackBuffer(),ImageBuffer(image)
SetBuffer BackBuffer()

;do: Main Loop

While Not KeyDown(1)
 Cls ;clear rect in fullscreen
 DrawImage image,0,0
 Color 0,0,0
 Plot MouseX(),MouseY() ;show mouse x/y in fullscreen
 Text 112,0,MouseX()+"-"+MouseY()

 If mousepress=0

  If MouseDown(1)
   mousepress=1
   sx=MouseX() ;start rect x/y
   sy=MouseY()
  EndIf

  If MouseDown(2)
   Cls ;clear in fullscreen
   Flip
   mousepress=0
   Goto reset
  EndIf

 Else

  If MouseDown(1)

   ;do: Draw Rect

   ex=MouseX() ;end rect x/y
   ey=MouseY()
   mx=MouseX() ;set mouse x/y
   my=MouseY()

   If sx>mx And sy>my ;upleft, recalculate true screen rect
    If sx-mx>sy-my
     ey=sy-(sx-mx)*3/4 ;x>
    Else
     ex=sx-(sy-my)*4/3 ;y>
    EndIf
   EndIf
   If sx<=mx And sy>my ;upright
    If mx-sx>sy-my ;x>
     ey=sy+(sx-mx)*3/4
    Else
     ex=sx+(sy-my)*4/3 ;y>
    EndIf
   EndIf
   If sx>mx And sy<=my ;downleft
    If sx-mx>my-sy
     ey=sy+(sx-mx)*3/4 ;x>
    Else
     ex=sx+(sy-my)*4/3 ;y>
    EndIf
   EndIf
   If sx<=mx And sy<=my ;downright
    If mx-sx>my-sy
     ey=sy-(sx-mx)*3/4 ;x>
    Else
     ex=sx-(sy-my)*4/3 ;y>
    EndIf
   EndIf

   startx=sx
   starty=sy
   endx=Abs(ex-sx) ;set rect width/height
   endy=Abs(ey-sy)
   If ex<sx Then startx=ex ;set inverse rect x/y
   If ey<sy Then starty=ey

   Rect startx,starty,endx,endy,False

  Else

   ;do: New Fractal

   mousepress=0

   If Abs(sx-ex)>4 And Abs(sy-ey)>3 ;set minimum selection area
    lastx# = lastx + startx / scale
    lasty# = lasty + starty / scale
    scale# = scale * Float(height) / Float(endy)
    newdxy# = dxy * Float(height) / Float(endy)
    dxy# = newdxy
    newdcx# = lastx * scale
    newdcy# = dxy * fixr + lasty * scale
    dcx# = newdcx
    dcy# = newdcy
    Cls ;clear in fullscreen
    Flip
    Goto redraw
   EndIf

  EndIf

 EndIf

 Flip
Wend
End

;do: Functions

Function DrawGradientLine(Nclr,Sred#,Sgreen#,Sblue#,Ered#,Egreen#,Eblue#)

Gred#=Ered-Sred/Nclr
Ggreen#=Egreen-Sgreen/Nclr
Gblue#=Eblue-Sblue/Nclr

For g=0 To Nclr
 Color Sred,Sgreen,Sblue
 Line g,0,g,5
 Sred#=Sred+Gred
 Sgreen#=Sgreen+Ggreen
 Sblue#=Sblue+Gblue
Next

End Function
