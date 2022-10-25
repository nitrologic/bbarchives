; ID: 3236
; Author: dw817
; Date: 2016-01-05 10:41:40
; Title: Correct Filled Polygon
; Description: A quick and dirty way of building a filled polygon where Blitzmax's own FillPoly() does not work effectively.

'    ___________________________________________
'   //                                        //
'  //    "Perfect" Filled Polygon            //
' //    Written by David W (dw817) 01/12/16 //
'//________________________________________//

' What's up ?? Added boundary check, thanks to Bobysait

Strict
SetGraphicsDriver(GLMax2DDriver()) ' no problems w intensive pixel reading
Graphics 640,480
SetBlend alphablend ' allow for transparency

Global vec$="3U^jV\lf]7RGN3P" ' all vector images are in 8x8 grid
Global typ

typ=0
' 0 (zero) to use my polygon plotter
' 1 (one) to use BlitzMAX's own flawed plotter

If typ=0 Then drawvec vec$
If typ=1 Then drawvec2 vec$
Flip
WaitKey

Function drawvec(t$)
Local x,y,x2,y2,p,a,i,j,k,m,z=Len(t$),ok,pic:TPixmap
  For p=1 To z
    a=Asc(Mid$(t$,p))-40 ' pull out a vector
    x=a Mod 9*16 ' retrieve horizontal
    y=a/9*16 ' retrieve vertical
    If p>1 And p<z ' if not first iteration nor last, draw vector
      DrawLine x2,y2,x,y
    ElseIf p=z ' last iteration, let's do our fill
      pic=GrabPixmap(0,0,128,128) ' grab image for massive pixel updates
      WritePixel pic,x,y+8,$fffffffe ' +8 as paint vector falls on existing edge
' needs to be $fe to differentiate between edges that are $ff
      Repeat
        ok=1 ' flag that no changes have been made
        For i=0 To 127 ' field of 8x8 grid, snap on 16
          For j=0 To 127
            If j+m>=0 And i+k>=0 ' confirm within range of screen
              If ReadPixel(pic,j,i)=$fffffffe ' found our paint pixel
                For k=-1 To 1
                  For m=-1 To 1
                    If j+m>=0 And i+k>=0 And j+m<=127 And i+k<=127 ' confirm within ange
                      If ReadPixel(pic,j+m,i+k)=$ff000000 And(k=0 Or m=0) ' ensure search is cross and not square
                        WritePixel pic,j+m,i+k,$fffffffe ' it's empty so fill it in
                        ok=0 ' flag for cannot exit just yet
                      EndIf
                    EndIf
                  Next
                Next
              EndIf
            EndIf
          Next
        Next
      Until ok ' no changes above means we're finally done
      DrawPixmap pic,0,0 ' draw back the changes
    EndIf
    x2=x
    y2=y
  Next
EndFunction

Function drawvec2(t$) ' System's own polygon plotter
Local vec#[Len(t$)*2],a,i
  For i=1 To Len(t$)
    a=Asc(Mid$(t$,i))-40
    vec#[i*2-2]=a Mod 9*16+20 ' store X
    vec#[i*2-1]=a/9*16+20 ' store Y
  Next
  DrawPoly vec# ' use system's own plotter, flawed
EndFunction
