; ID: 246
; Author: UKAndrewC
; Date: 2002-02-21 19:03:02
; Title: Rebounding balls
; Description: Example of ball rebounding angles

;*****************************************
;Collision detection and rebounding angles
;-----------------------------------------
;Andrew Constant ukandrewc@aol.com
;-----------------------------------------
;There are faster ways to detect line/ball
;collisions, but this way, you can have
;any line position & any sprite shape
;*****************************************

Const SW=640
Const SH=480


Type IMG
	Field hi,ix,iw
	Field x#,y#,xi#,yi#
End Type

Type PNT
	Field hi,ix
	Field x,y
End Type

Type OBS
	Field hi,ix,a
	Field x,y,w,h
	Field x1,y1,x2,y2
End Type

;Title bits
Global t$="Angles, balls and walls"
Global ts=-StringWidth(t$)
Global tx=0

;Image and wall indeces
Global bc=0
Global wc=0
Global pc=0


Global sp=3
Global repCount

Global oik=LoadSound("oik.wav")
Global wait=CreateTimer(30)

SeedRnd MilliSecs()
Graphics SW,SH

;Create some random balls
For c=1 To 5
	CreateBall(64,Rand(128,255),Rand(128,255))
Next

;Create some bollards
Restore BollardData
For c=1 To 5
	Read x,y
	CreatePoint x,y,255,255,0
Next

.BollardData
Data 200,150
Data 250,300
Data 400,350
Data 100,400
Data 550,070

Restore WallData
For c=1 To 9
	Read x1,y1,x2,y2	
	Createwall x1,y1,x2,y2,Rand(128,255),Rand(128,255),192
Next

.WallData
Data 010,010,629,010
Data 629,010,629,469
Data 629,469,010,469
Data 010,469,010,010

Data 150,70,550,150
Data 580,220,520,400
Data 200,220,500,220
Data 200,420,450,400
Data 170,350,70,150


;enable double buffering
SetBuffer BackBuffer()

;loop until ESC pressed...
While Not KeyDown(1)
	MoveBalls()
	While KeyDown(57)
		;Pause while space pressed
	Wend
Wend
End


Function MoveBalls()
c=WaitTimer(wait)

Cls
Color 255,255,255
;Draw walls
For wall.obs=Each OBS
	DrawImage wall\hi,wall\x,wall\y
	Text wall\x+(wall\w/2),wall\y+(wall\h/2),Str$(wall\a),True,True
	Text wall\x1,wall\y1,"*",True,True
	Text wall\x2,wall\y2,"*",True,True
Next

;Draw rebound points
For point.pnt=Each PNT
	DrawImage point\hi,point\x,point\y
	Text point\x,point\y,point\ix,True,True
Next

;Draw & check balls for collision
For ball.img=Each IMG
			
	ball\x=ball\x+ball\xi
	ball\y=ball\y+ball\yi
	DrawImage ball\hi,ball\x,ball\y
	
	;Draw line in front of ball
	ang=ATan2(ball\yi,ball\xi)
	cx=ball\x+16
	cy=ball\y+16
	Line cx,cy,cx+15*Cos(ang),cy+15*Sin(ang)
	Text cx,cy,ang,True,True
	
	collide=False
	
	;Check other balls
	For ball2.Img=Each Img
		If ball2<>ball Then
			If ImagesCollide(ball\hi,ball\x,ball\y,0,ball2\hi,ball2\x,ball2\y,0) Then
				ang=ATan2(ball\y-ball2\y,ball\x-ball2\x)
				collide=True
			EndIf
		EndIf
	Next
	
	;Check rebound points
	If collide=False Then
		For point.pnt=Each PNT
			If ImagesCollide(ball\hi,ball\x,ball\y,0,point\hi,point\x,point\y,0) Then
				ang=ATan2(ball\y-point\y,ball\x-point\x)
				collide=True
			EndIf
		Next
	EndIf

	;Check walls
	If collide=False Then
		For wall.obs=Each OBS

			;Check x1,y1 end points
			If ImageRectCollide(ball\hi,ball\x,ball\y,0,wall\x1,wall\y1,1,1) Then
				;Rebound away from line but keep some ball direction
				ang=ATan2(wall\y1-wall\y2,wall\x1-wall\x2)+(ang/2)
				repCount=RepCount+1
				collide=True

			;Check x2,y2 end points
			ElseIf ImageRectCollide(ball\hi,ball\x,ball\y,0,wall\x2,wall\y2,1,1) Then
				;Rebound away from line but keep some ball direction
				ang=ATan2(wall\y2-wall\y1,wall\x2-wall\x1)+(ang/2)
				collide=True
			
			;Check mid line
			ElseIf ImagesCollide(ball\hi,ball\x,ball\y,0,wall\hi,wall\x,wall\y,0) Then
				;Rebound compound of ball and wall angles
				ang=-ang+(wall\a*2)
				collide=True
			EndIf
		Next
	EndIf
	
	If collide Then
		;New ball direction
		ball\xi=sp*Cos(ang)
		ball\yi=sp*Sin(ang)
		;Do extra move away
		ball\x=ball\x+ball\xi
		ball\y=ball\y+ball\yi
	EndIf
	
Next

tx=tx+2
If tx=640 Then tx=ts
Color 255,255,0
Text tx,12,t$

;swap front and back buffers
Flip

End Function


;*************************
Function CreateBall(r,g,b)

bc=bc+1
ball.img = New IMG

ball\x=50
ball\y=50

ang=Rand(0,360)
ball\xi=sp*Cos(ang)
ball\yi=sp*Sin(ang)

ball\hi=CreateImage(32,32)
SetBuffer ImageBuffer(ball\hi)

;Anti-alias it a bit
Color 96,96,96
Oval 0,0,32,32

;Draw main ball
Color r,g,b
Oval 1,1,30,30

Color 0,0,0
;Text 16,16,Str$(bc),True,True

End Function


;*************************************
Function CreateWall(x1,y1,x2,y2,r,g,b)

wall.obs=New OBS

wc=wc+1
wall\ix=wc
wall\y1=y1
wall\y2=y2
wall\x1=x1
wall\x2=x2	

;Size of the image
w=Abs(x1-x2)
h=Abs(y1-y2)
wall\w=w
wall\h=h

;Adjust from real world co-ords
If x1>x2 Then
	wall\x=x2
	x1=w:x2=0
Else
	wall\x=x1
	x2=w:x1=0
EndIf

If y1>y2 Then
	wall\y=y2
	y1=h:y2=0
Else
	wall\y=y1
	y2=h:y1=0
EndIf

;Keep the line's angle
a=ATan2(y2-y1,x2-x1)
wall\a=a

;Adjust To give correct rebound
If a>0 And a<90 Then wall\a=a
If a>90 And a<180 Then wall\a=a-180

;Create & draw wall image
wall\hi=CreateImage(w+1,h+1)
SetBuffer ImageBuffer(wall\hi)
Color r,g,b
Line x1,y1,x2,y2

End Function


;******************************
Function CreatePoint(x,y,r,g,b)

point.pnt=New PNT

pc=pc+1

point\ix=pc
point\x=x
point\y=y
point\hi=CreateImage(1,1)
SetBuffer ImageBuffer(point\hi)

;Draw the point
Color r,g,b
Plot 0,0

End Function
