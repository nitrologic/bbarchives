; ID: 1612
; Author: markcw
; Date: 2006-02-12 08:38:55
; Title: Burning Ship Fractal
; Description: fractal explorer

;Burning Ship Fractal, on 12/2/06
;Translated from source code, by Paul Bourke
;Adapted from Mandelbrot Fractal code, by filax & fredborg

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

AppTitle "Burning Ship Fractal"
Graphics width,height,16,window
SetBuffer BackBuffer()

;do: Set Palette

cmax = 256
Dim col(cmax)

DrawGradientLine(cmax-1,0,230,230,0,0,0) ;colour
;DrawGradientLine(cmax-1,230,230,230,0,0,0) ;grey

LockBuffer
For i=0 To cmax-1
 col(i)=ReadPixelFast(i,1) * 8 And $FFFFFF ;colour
 ;col(i)=ReadPixelFast(i,1) And $FFFFFF ;grey
Next
UnlockBuffer

;do: Draw Fractal

.reset
dcx# = 0.43
dcy# = 0.43
dx# = 3.25
dy# = -dx

.redraw
Cls

For y=0 To height-1
 LockBuffer
 cy# = dcy + (y - height/2) * dy / Float(height)
 For x=0 To width-1
  cx# = dcx + (x - width/2) * dx / Float(width)
  xi# = 0
  yi# = 0
  For c=0 To cmax-1
   xip1# = xi*xi - yi*yi - cx  ;x(n+1) = x(n)^2 - y(n)^2 - c(x)
   yip1# = 2 * Abs(xi*yi) - cy ;y(n+1) = 2 | x(n) y(n) | - c(y)
   xi# = xip1
   yi# = yip1
   If xi*xi + yi*yi > 200 Then Exit
  Next
  value# = Sqr(c / Float(cmax))
  colour = value * cmax-1
  WritePixelFast x,y,col(colour)
  If KeyDown(1) Then End ;Esc key
 Next
 UnlockBuffer
 If window<2 Then Flip ;two flips in fullscreen, slower
 Flip
Next

image=CreateImage(width,height)
CopyRect 0,0,width,height,0,0,BackBuffer(),ImageBuffer(image)
SetBuffer BackBuffer()

;do: Main Loop

While Not KeyDown(1)
 Cls ;clear rect in fullscreen
 DrawImage image,0,0
 Color 255,255,255
 Plot MouseX(),MouseY() ;show mouse x/y in fullscreen
 Text 0,0,MouseX()+"-"+MouseY()

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
    newdx# = dx * Float(endx) / Float(width)
    newdy# = dy * Float(endy) / Float(height)
    newdcx# = dcx + (startx + endx/2 - width/2) * dx / Float(width)
    newdcy# = dcy + (starty + endy/2 - height/2) * dy / Float(height)
    dx# = newdx
    dy# = newdy
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
