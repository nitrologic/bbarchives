; ID: 428
; Author: Shagwana
; Date: 2002-09-16 12:41:56
; Title: Plot a circle
; Description: A sexy Beshenham circle drawing algo :)

Function PlotCircle(xpos,ypos,radius)
  x=0 
  y=radius
  h=1-radius  
  Plot(xpos+x,ypos+y)  ;Draw the starting pixels
  Plot(xpos-x,ypos-y)
  Plot(xpos+x,ypos-y)
  Plot(xpos-x,ypos+y)
  Plot(xpos+y,ypos+x)
  Plot(xpos-y,ypos-x)
  Plot(xpos+y,ypos-x)
  Plot(xpos-y,ypos+x)
  While y>x            ;Loop the arc
   If h<0
     h=h+(2*(x+1))
     x=x+1
     Else
     h=h+(2*(x-y))+5
     x=x+1
     y=y-1
     EndIf  
    Plot(xpos+x,ypos+y) ;Draw 1/8 at a time 
    Plot(xpos+y,ypos+x)
    Plot(xpos-x,ypos-y)
    Plot(xpos-y,ypos-x)
    Plot(xpos-x,ypos+y)
    Plot(xpos-y,ypos+x)
    Plot(xpos+x,ypos-y)
    Plot(xpos+y,ypos-x)
    Wend
  End Function
