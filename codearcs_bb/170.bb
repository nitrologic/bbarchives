; ID: 170
; Author: wedoe
; Date: 2002-06-15 04:03:36
; Title: Bezier Function
; Description: Draw a bezier curve

AppTitle "Bezier"

Graphics 640,480,16,2

Global x1=100     ; X start
Global y1=100     ; Y start
Global vx1=100    ; X vector start
Global vy1=100     ; Y vector start
Global x2=540     ; X end
Global y2=380     ; Y end
Global vx2=540     ; X vector-end
Global vy2=380     ; Y vector-end

Global point=0 ; Counter for what position to drag

SetBuffer BackBuffer()
Repeat
Cls

drawbezier (x1,y1,vx1,vy1,x2,y2,vx2,vy2)

Color 255,0,0
Select point
Case 0
Oval x1-2,y1-2,5,5
If MouseDown(2) Then 
        x1=MouseX()
        y1=MouseY()
EndIf
Case 1
Oval vx1-2,vy1-2,5,5
If MouseDown(2) Then 
        vx1=MouseX()
        vy1=MouseY()
EndIf
Case 2
Oval x2-2,y2-2,5,5
If MouseDown(2) Then 
        x2=MouseX()
        y2=MouseY()
EndIf
Case 3
Oval vx2-2,vy2-2,5,5
If MouseDown(2) Then 
        vx2=MouseX()
        vy2=MouseY()
EndIf
End Select

Color 255,255,255
Text 10,10,"x1= "+x1
Text 10,25,"y1= "+y1
Text 10,40,"vx1="+vx1
Text 10,55,"vy1="+vy1
Text 10,70,"x2= "+x2
Text 10,85,"y2= "+y2
Text 10,100,"vx2="+vx2
Text 10,115,"vy2="+vy2

If MouseDown(1) Then changepoint

Flip
Until KeyDown(1)

WaitKey()
End

Function changepoint()
point=(point+1) Mod 4
While MouseDown(1)
Wend
End Function


; Bezierline by Wedoe
; x1=startpoint x
; y1=startpoint y
; vx1=referencepoint x1
; vy1=referencepoint y1
; x2=endpoint x
; y2=endpoint y
; vx2=referencepoint x2
; vy2=referencepoint y2

Function drawbezier (x1,y1,vx1,vy1,x2,y2,vx2,vy2)
For t#=0 To 1 Step.01
pointx# = x1*(1-t)^3 + 3*vx1*(1-t)^2*t + 3*vx2*(1-t)*t^2 + x2*t^3
pointy# = y1*(1-t)^3 + 3*vy1*(1-t)^2*t + 3*vy2*(1-t)*t^2 + y2*t^3
WritePixel pointx,pointy,$ffffff
Next
End Function
