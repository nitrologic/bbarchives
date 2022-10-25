; ID: 1100
; Author: Sweenie
; Date: 2004-07-01 04:24:13
; Title: Natural Cubic Spline
; Description: Cubic Spline

; Natural Cubic Spline 
; Based on a java-applet by Tim Lambert
;
; Drag the red dots around with the mouse

Graphics 800,600,0,2
SetBuffer BackBuffer()

Type Cubic
 Field a#
 Field b#
 Field c#
 Field d#
End Type

Const Points = 8 ; Number of points
Const Steps = 24 ; Number of substeps between points (Higher number = Smoother curve = Slower drawing)

;Arrays for calculations
Const n=Points-1  
Dim xp#(n)
Dim yp#(n)
Dim xc.Cubic(n)
Dim yc.Cubic(n)
Dim gamma#(n+1)
Dim delta#(n+1)
Dim D#(n+1)

;Place the points
For i=0 To n
 xp#(i)=i*(GraphicsWidth()/Points+1)+((GraphicsWidth()/Points+1)/2)
 yp#(i)=GraphicsHeight()/2
Next

;Init the CubicsArrays
InitCubics()

CalcCubics()
Cls
DrawSpline()
Flip

Dragging=False

While Not KeyHit(1)

 If MouseDown(1) And Dragging=False Then
  mx=MouseX():my=MouseY()
  For i=0 To n
   px=xp(i):py=yp(i)
   If mx>px-3 And mx<px+3 And my>py-3 And my<py+3 Then
    DragPoint=i
	Dragging=True
    Exit
   EndIf
  Next
 EndIf 

 If MouseDown(1)=False And Dragging=True Then
  Dragging=False
 EndIf

 If Dragging Then
  xp#(DragPoint)=MouseX()
  yp#(DragPoint)=MouseY()
  CalcCubics()
  Cls
  DrawSpline()	
  Flip
 EndIf 

Wend
End


Function DrawSpline()
 x#=CubicEval(xc(0),0.0)
 y#=CubicEval(yc(0),0.0)

  Color 255,255,255

For i=0 To n-1
 For j=1 To Steps
  oldx#=x#
  oldy#=y#
  stp#=Float(j)/Float(Steps)
  x#=CubicEval(xc(i),stp#)
  y#=CubicEval(yc(i),stp#)
  Line oldx#,oldy#,x#,y#
 Next
Next

  Color 255,0,0 
  For i=0 To n
   Oval xp#(i)-3,yp#(i)-3,6,6,True
  Next
End Function


Function CalcCubics()

gamma(0) = 1.0/2.0
For i=1 To n-1
 gamma(i) = 1/(4-gamma(i-1))
Next
gamma(n) = 1/(2-gamma(n-1))

; First the X-points

delta(0) = 3*(xp(1)-xp(0))*gamma(0)
For i=1 To n-1
 delta(i) = (3*(xp(i+1)-xp(i-1))-delta(i-1))*gamma(i)
Next
delta(n)=(3*(xp(n)-xp(n-1))-delta(n-1))*gamma(n)

D(n)=delta(n)
For i=n-1 To 0 Step -1
 D(i) = delta(i) - gamma(i)*D(i+1)
Next

For i=0 To n-1
 xc(i)\a# = xp(i)
 xc(i)\b# = D(i)
 xc(i)\c# = 3 * (xp(i+1)-xp(i)) - 2*D(i) - D(i+1)
 xc(i)\d# = 2 * (xp(i)-xp(i+1)) + D(i) + D(i+1)
Next

; Then the Y-points

delta(0) = 3*(yp(1)-yp(0))*gamma(0)
For i=1 To n-1
 delta(i) = (3*(yp(i+1)-yp(i-1))-delta(i-1))*gamma(i)
Next
delta(n)=(3*(yp(n)-yp(n-1))-delta(n-1))*gamma(n)

D(n)=delta(n)
For i=n-1 To 0 Step -1
 D(i) = delta(i) - gamma(i)*D(i+1)
Next

For i=0 To n-1
 yc(i)\a# = yp(i)
 yc(i)\b# = D(i)
 yc(i)\c# = 3 * (yp(i+1)-yp(i)) - 2*D(i) - D(i+1)
 yc(i)\d# = 2 * (yp(i)-yp(i+1)) + D(i) + D(i+1)
Next

End Function

Function CubicEval#(Cubic.Cubic,u#)
 ta#=Cubic\a#
 tb#=Cubic\b#
 tc#=Cubic\c#
 td#=Cubic\d#	

 Return (((td#*u#)+tc#)*u#+tb#)*u#+ta#
End Function

Function InitCubics()
 For i=0 To n
  xc.Cubic(i) = New Cubic
  yc.Cubic(i) = New Cubic
 Next
End Function
