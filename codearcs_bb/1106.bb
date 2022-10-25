; ID: 1106
; Author: aab
; Date: 2004-07-12 21:18:54
; Title: 2d ReboundCollide() function
; Description: a 'Sort of ' 'homepieced' function made from imagecollides

;Rebound Collide
Graphics 400,300
SetBuffer BackBuffer()
SeedRnd MilliSecs()
AutoMidHandle True


speed1#=1
speed2#=1

image_1=LoadImage( "charsbb\drag.png" )
Global x1#=0
Global y1#=0
image_2=LoadImage( "charsbb\ship.bmp" )
Global x2#=200
Global y2#=150
mass1#=1
mass2#=10
If image_1=0 Or image_2=0 Then RuntimeError "Image Unfound!"

While Not KeyHit(1)
Cls



reboundcollide#(image_1,x1#,y1#,frame1,mass1#,speed1#,image_2,x2#,y2#,frame2,mass2#,speed2#,1)

DrawImage image_1,x1#,y1#
DrawImage image_2,x2#,y2#

If KeyDown(203) x1#=x1#-speed1#
If KeyDown(205) x1#=x1#+speed1#
If KeyDown(200) y1#=y1#-speed1#
If KeyDown(208) y1#=y1#+speed1#

If KeyDown(75) x2#=x2#-speed2#
If KeyDown(77) x2#=x2#+speed2#
If KeyDown(72) y2#=y2#-speed2#
If KeyDown(80) y2#=y2#+speed2#


If KeyDown(16) speed1#=speed1#-0.1
If KeyDown(17) speed1#=speed1#+0.1
If speed#<0.1 speed#=0.1
If speed#>10 speed#=10
Text 0,0,("S1: "+Int(speed1#)+"S2: "+Int(speed2#))
Text 0,10,(Int(x1)+","+Int(y1))
Text 0,20,(Int(x2)+","+Int(y2))

Text 0,50,(reboundcollide#(image_1,x1#,y1#,frame1,mass1#,speed1#,image_2,x2#,y2#,frame2,mass2#,speed2#,1))

VWait:Flip False
Wend
End

Function reboundcollide#(image_a,xa#,ya#,framea,massa#,speeda#,image_b,xb#,yb#,frameb,massb#,speedb#,flag)
	Text 0,30,(Int(xa#)+","+Int(ya#))
	Text 0,40,(Int(xb#)+","+Int(yb#))
	If ImagesCollide(image_a,xa#,ya#,framea,image_b,xb#,yb#,frameb)
		If massa#=0 Or massb#=0 Then RuntimeError "0 Mass? that object can't exist! try 0.00001!"
		If image_a=0 Or image_b=0 Then RuntimeError "Image Handle unparameterised!"
		xa#=xa#+(Sgn(xa#-xb#)*speeda#)/massa#
		xb#=xb#+(Sgn(xb#-xa#)*speedb#)/massb#
		ya#=ya#+(Sgn(ya#-yb#)*speeda#)/massa#
		yb#=yb#+(Sgn(yb#-ya#)*speedb#)/massb#
		Select flag ;this is the part u must annotate for each possible co-ord situation:the FLAG!
			Case 1 x1#=xa# x2#=xb# y1#=ya# y2#=yb#
		End Select
		If Not massa#=0 And massb#=0 Then Return True
	EndIf
	Return False
End Function
