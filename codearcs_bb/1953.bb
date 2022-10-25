; ID: 1953
; Author: Matt Merkulov
; Date: 2007-03-14 11:06:27
; Title: Cubic spline interpolation
; Description: Drawing cubic spline what is passing thru random set of points

;Drawing cubic spline function graph what is passing thru random set of points by Matt Merkulov

SeedRnd MilliSecs ()

Dim ptx#(81)
Dim pty#(81)

Graphics 800,600

; The circuit from points is created
x#=0
y#=300
Color 255,0,0
Repeat
 q=q+1
 ptx#(q)=x#
 pty#(q)=y#
 Oval x#-4, y#-4,9,9
 x#=x#+ Rnd (30,100)
 y#=y#+ Rnd (-100,100)
Until x#>=800

Color 255,255,255
; A cycle on all pieces (for an extreme right point of a piece is not present)
For n=1 To q-1
 d#=pty#(n)
 If n=1 Then
 ; If the initial piece the derivative is equal 0 (since an adjacent point undertakes
 ; At the left, necessary for definition of factor is absent
 c#=0
 Else
 ; Calculation of the factor equal to derivative N1 under the formula
 c#=(pty#(n+1)-pty#(n-1)) / (ptx#(n+1)-ptx#(n-1))
 End If
 ; Derivative N2 is similarly calculated
 If n=q Then
 dy2#=0
 Else
 dy2#=(pty#(n+2)-pty#(n)) / (ptx#(n+2)-ptx#(n))
 End If
 ; Calculation of other factors of a multinominal
 x3#=ptx#(n+1)-ptx#(n)
 xx3#=x3#*x3#
 b#=(3*pty#(n+1)-dy2#*x3#-2*c#*x3#-3*d#)/xx3#
 a#=(dy2#-2*b#*x3#-c#) / (3*xx3#)
 ; Construction of a piece of a curve
 For x#=0 To x3#
 xx#=x#*x#
 y#=a#*xx#*x#+ b#*xx#+ c#*x#+ d#
 x1#=x#+ ptx#(n)  
 If x1#> 0 Then
  y1#=y#
  If y1#<-3 Then y1#=-3 ElseIf y1#> 602 Then y1#=602
  If y2#<-3 Then y2#=-3 ElseIf y2#> 602 Then y2#=602
  If y2#<y1#Then z#=y1#:y1#=y2#:y2#=z#
  For yy#=y1#To y2#
  Rect x1#-1, yy#-1,3,3
  Next
 End If
 y2#=y#
 Next
Next
WaitKey
