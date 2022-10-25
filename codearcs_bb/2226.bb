; ID: 2226
; Author: markcw
; Date: 2008-03-09 22:02:33
; Title: Angle360 for B3D/B+
; Description: Returns a 360 degree angle between 2 points

;Angle360 Example for B3D/B+

Graphics 640,480,0,2
SetBuffer BackBuffer()

While Not KeyDown(1)
 Cls

 ;Here the center of the screen is point 1 and the mouse is point 2
 angle#=Angle360(320,240,MouseX(),MouseY())
 Text 0,0,"angle#="+angle#

 ;Draw 2 points and 3 lines
 Rect MouseX()-2,MouseY()-2,4,4
 Rect 320-2,240-2,4,4
 Line 320,240,MouseX(),MouseY()
 Line 320,240,MouseX(),240
 Line 320,240,320,MouseY()

 Flip
Wend
End

Function Angle360b#(x1,y1,x2,y2)
 ;Returns a 360 degree angle between 2 points, 0..359 degrees
 ;Author: Snarkbait, 9 Mar 2008
 Return (ATan2(y2-y1,x2-x1)+450) Mod 360
End Function

Function Angle360#(x1,y1,x2,y2)
 ;Returns a 360 degree angle between 2 points, 0..359 degrees
 ;Assumes point 1 is the center and point 2 is on the edge of a circle
 ;Author: markcw, edited 12 Mar 2008

 Local adj,opp,hyp#,angle#
 adj=Abs(x2-x1)
 opp=Abs(y2-y1)
 hyp#=Sqr((adj*adj)+(opp*opp)) ;Pythagoras
 If hyp#=0 Then hyp#=1 ;Avoid divide by zero
 If opp<=adj : angle#=ASin(opp/hyp#) ;Sin=Opp/Hyp
 Else : angle#=ACos(adj/hyp#) ;Cos=Adj/Hyp
 EndIf
 If x2>x1 And y2<=y1 : angle#=90-angle# ;0..90
 ElseIf x2>x1 And y2>y1 : angle#=90+angle# ;90..180
 ElseIf x2<=x1 And y2>y1 : angle#=180+90-angle# ;180..270
 ElseIf x2<=x1 And y2<=y1 : angle#=180+90+angle# ;270..360
 EndIf
 If angle#=360 Then angle#=0
 Return angle#

End Function
