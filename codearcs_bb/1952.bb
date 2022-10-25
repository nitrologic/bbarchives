; ID: 1952
; Author: Matt Merkulov
; Date: 2007-03-14 11:04:08
; Title: Lagrange interpolation
; Description: Drawing Lagrange function graph what is passing thru random set of points

;Drawing Lagrange function graph what is passing thru random set of points by Matt Merkulov

SeedRnd MilliSecs ()

Const i=10
Dim c#(i, 1)

Graphics 800,600,16

Color 255,0,0
For j=1 To i
 ; Check on concurrence of x-coordinates
 Repeat
 ; A step that points have defended from each other
 x#= 5*Rand (1,159)
 ; A cycle on all previous points
 For k=1 To j-1
  ; If coordinates coincide - the cycle interrupts
  If c#(k, 0)=x#Then Exit
 Next
 ; If the cycle has not been interrupted, k= (j-1) +1=j, differently we set new value x
 If k=j Then Exit
 Forever
 c#(j, 0)=x#
 c#(j, 1)=Rnd (200,400)
 Oval c#(j, 0)-5, c#(j, 1)-5,11,11
Next

Color 255,255,255
For x#= 0 To 799
 ; Calculation of value of the formula
 y#= 0
 For j=1 To i
 m#= c#(j, 1)
 For k=1 To i
  If k <> j Then m#= m#*(x#-c#(k, 0)) / (c#(j, 0)-c#(k, 0))
 Next
 y#= y#+ m#
 Next

 ; Correct construction of a part of the schedule (differently there will be breaks)
 If x#> 0 Then
 y1#= y#
 If y1#<-3 Then y1#=-3 ElseIf y1#> 602 Then y1#= 602
 If y2#<-3 Then y2#=-3 ElseIf y2#> 602 Then y2#= 602
 If y2#<y1#Then z#= y1#:y1#= y2#:y2#= z#
 For yy#= y1#To y2#
  Oval x#-2, yy#-2,5,5
 Next
 End If
 y2#= y#
Next

WaitKey
