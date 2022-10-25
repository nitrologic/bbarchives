; ID: 1451
; Author: Grey Alien
; Date: 2005-08-25 19:15:44
; Title: Fast Circle
; Description: Beshenham circle using WritePixelFast

Function PlotFastCircle(xpos,ypos,radius) ;JB modification 28/08/05
  ;originally by Shagwana 2002-09-16 
  x=0 
  y=radius
  h=1-radius  
  c=255
  LockBuffer(BackBuffer)
  WritePixelFast(xpos+x,ypos+y,c)  ;Draw the starting pixels
  WritePixelFast(xpos-x,ypos-y,c)
  WritePixelFast(xpos+x,ypos-y,c)
  WritePixelFast(xpos-x,ypos+y,c)
  WritePixelFast(xpos+y,ypos+x,c)
  WritePixelFast(xpos-y,ypos-x,c)
  WritePixelFast(xpos+y,ypos-x,c)
  WritePixelFast(xpos-y,ypos+x,c)
  While y>x            ;Loop the arc
   If h<0
     h=h+(2*(x+1))
     x=x+1
     Else
     h=h+(2*(x-y))+5
     x=x+1
     y=y-1
     EndIf  
    WritePixelFast(xpos+x,ypos+y,c) ;Draw 1/8 at a time 
    WritePixelFast(xpos+y,ypos+x,c)
    WritePixelFast(xpos-x,ypos-y,c)
    WritePixelFast(xpos-y,ypos-x,c)
    WritePixelFast(xpos-x,ypos+y,c)
    WritePixelFast(xpos-y,ypos+x,c)
    WritePixelFast(xpos+x,ypos-y,c)
    WritePixelFast(xpos+y,ypos-x,c)
    Wend
  UnlockBuffer(BackBuffer)
End Function
