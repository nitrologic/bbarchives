; ID: 1954
; Author: Matt Merkulov
; Date: 2007-03-14 11:12:12
; Title: Smoothly moving object
; Description: Object in 2D smoothly passing thru random set of points visiting them in certain time (dual cubic spline interpolation)

;Object in 2D smoothly passing thru random set of points visiting them in certain time (dual cubic spline interpolation) by Matt Merkulov

SeedRnd MilliSecs ()

Const q=10

; As coordinates x and y are processed equally, files, where are created
; These coordinates and factors for functions on a current interval of time are stored(kept)
Dim ptc#(q+2,1)
Dim a#(1)
Dim b#(1)
Dim c#(1)
Dim d#(1)
; A file for time in which the object should visit(attend) the set point
Dim tim (q+2)
; A file of coordinates of object
Dim oc#(1)

Graphics 800,600
SetFont LoadFont ("Arial", 16)

x#=0
y#=300
Color 255,0,0
For n=1 To q
 x#=Rnd (50,750)
 y#=Rnd (50,550)
 ptc#(n, 0)=x#
 ptc#(n, 1)=y#
 tim (n)=t
 Oval x#-4, y#-4,9,9
 Text x#, y#+ 4, .001*t + "s", True
 t=t+Rand (1000,3000)
Next

; Values of parameters of extreme points for maintenance of cyclicity are set
For nn=0 To 1
 ptc#(0, nn)=ptc#(q, nn)
 ptc#(q+1, nn)=ptc#(1, nn)
 ptc#(q+2, nn)=ptc#(2, nn)
Next
tim (q+1)=t
tim (q+2)=t+tim (2)
tim (0)=tim (q)-t

Color 255,255,255
; We keep a background
i=CreateImage (800,600)
GrabImage i, 0,0
SetBuffer BackBuffer ()

; time counter it is established(installed) outside an interval of a cycle, that
; To calculate factors for an initial interval of time, having passed(having taken place) through a condition
t=t+1
n=q+1
Repeat
 If t> tim (n+1) Then
 n=n+1
 ; If number of unit has fallen outside the limits a file - return on unit 1, zeroing
 ; The counter of time
 If n> q Then
  n=1
  ms=0
  tbeg=MilliSecs ()
 End If
 For nn=0 To 1
  d#(nn)=ptc#(n, nn)
  c#(nn)=(ptc#(n+1, nn)-ptc#(n-1, nn)) / (tim (n+1)-tim (n-1))
  dy2#=(ptc#(n+2, nn)-ptc#(n, nn)) / (tim (n+2)-tim (n))
  x3#=tim (n+1)-tim (n)
  xx3#=x3#*x3#
  b#(nn)=(3*ptc#(n+1, nn)-dy2#*x3#-2*c#(nn)*x3#-3*d#(nn))/xx3#
  a#(nn)=(dy2#-2*b#(nn)*x3#-c#(nn)) / (3*xx3#)
 Next
 End If

 ; Calculation of coordinates of object
 For nn=0 To 1
 v#=t-tim (n)
 vv#=v#*v#
 oc#(nn)=a#(nn)*vv#*v#+ b#(nn)*vv#+ c#(nn)*v#+ d#(nn)
 Next

 ; Display of a background, object and current time
 DrawBlock i, 0,0
 Oval oc#(0)-9, oc#(1)-9,19,19
 Text 0,0, " Time: " + (.001*t) + "s"
 Flip

 t=MilliSecs ()-tbeg
Until KeyHit (1)
