; ID: 1411
; Author: Braincell
; Date: 2005-06-28 07:09:47
; Title: Rotatatable movable square to movable point collisions in 2D
; Description: Checks collision of a square with any rotation and any position in 2D space with a movable point

;Rotating movable square to movable point collisions
;By Lenn <spirala7_NOSPAM_at_yahoo_dot_co_dot_uk>
;
;Should be very efficient: On my P4 2.6ghz it takes an average 0.004 ms 
;to check collision between a square and a point.

Graphics3D 640,480,0,2
camera=CreateCamera()
light=CreateLight()
RotateEntity light,90,0,0

square=CreateCube()
ScaleEntity square, 5,5,0.1 ;scale of 5 makes the width used in the function = 10
EntityColor square, 0,100,250
PositionEntity square, 0,0,20 ;fixed Z depth of 20 in this example

point=CreateSphere()
ScaleEntity point, 0.2,0.2,0.2
EntityColor point, 250,0,0
PositionEntity point, 0,0,20

pointx#=10 ;start positions 
pointy#=10
PositionEntity point,pointx#,pointy#,20


;square_rotation#=-30

While Not KeyDown(1)

rotatespeed#=-0.2
square_rotation#=square_rotation#+rotatespeed#
RotateEntity square,0,0,-square_rotation# ;Angles measured by function are ANTIclockwise

movespeed#=0.1
If KeyDown(203) Then pointx#=pointx#-movespeed#
If KeyDown(205) Then pointx#=pointx#+movespeed#
If KeyDown(200) Then pointy#=pointy#+movespeed#
If KeyDown(208) Then pointy#=pointy#-movespeed#

If KeyDown(17) Then squarey#=squarey#+movespeed#
If KeyDown(31) Then squarey#=squarey#-movespeed#
If KeyDown(30) Then squarex#=squarex#-movespeed#
If KeyDown(32) Then squarex#=squarex#+movespeed#

;prevent from going off screen:
If pointx# > 20 Then pointx#=20
If pointx# < -20 Then pointx#=-20
If pointy# > 15 Then pointy#=15
If pointy# < -15 Then pointy#=-15
If squarex# > 20 Then squarex#=20
If squarex# < -20 Then squarex#=-20
If squarey# > 15 Then squarey#=15
If squarey# < -15 Then squarey#=-15

PositionEntity point, pointx#, pointy#, 20
PositionEntity square, squarex#,squarey#, 20

m1=MilliSecs()
collided=PointInSquare(squarex#,squarey#,10,square_rotation#,pointx#,pointy#)
m2=MilliSecs()

m3=m3+(m2-m1)

act=act+1
If act=750 Then
act=0
m3f#=Float(m3)
ma#=m3f#/750
m3=0
End If

If collided  
	EntityColor point, 0,250,0
Else 
	EntityColor point, 250,0,0
End If


RenderWorld
Text 10,10, "Use cursor keys to move the point."
Text 10,22, "Use W,A,S,D to move the square."
Text 10,34, "Point will become green when collided."
Text 10,46, "Collided: "+collided
Text 10,56, "Average time taken for calculating :"+ma#+ " milliseconds"


Flip
Wend





Function Get360Angle#(xc#,yc#,x#,y#)
;xc,yc : X and Y of center
;x,y : X and Y of point 
;Note: 0 degrees is an angle pointing horizontally to the right

dx#=x#-xc#
dy#=y#-yc#
an#=ATan(dy#/dx#)
If x#>=xc# And y#>yc# Then
;++
an#=an#
ElseIf x#<=xc# And y#>=yc# Then
;-+
an#=180+an#
ElseIf x#<xc# And y#<yc# Then
;--
an#=180+an#
ElseIf x#>=xc# And y#<=yc# Then
;+-
an#=360+an#
End If

Return an#
End Function 



Function PointInSquare(sx#,sy#,sw#,sr#,x#,y#)
;Returns True if the point is inside the square
;sx,sy : X and Y of center of the square
;sw : Square width
;sr : Square rotation angle anticlockwise (in 360 degrees)
;x,y : X and Y of point

If sr# < 0 Then
sr#=sr#+(Ceil(sr#/-360)*360)
End If

aBeta#=sr#
aGamma#=Get360Angle(sx#,sy#,x#,y#)
aAlpha#=aBeta#+aGamma#

remainder#=aAlpha# Mod 45
wholes#=(aAlpha#-remainder#)/45
If IsOdd(wholes#) Then 
	aRho#=45-remainder#
Else
	aRho#=remainder#
End If

l#=(sw#/2)/Cos(aRho#)

dist#=Sqr((x#-sx#)^2+(y#-sy#)^2)

If l# > dist # Then
	Return True
Else
	Return False
End If

End Function



Function IsOdd( value )
	If value =0 Then Return 0
	If Float( value Mod 2 ) <>0 Then Return 1 Else Return 0
End Function
