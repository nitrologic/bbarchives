; ID: 462
; Author: Klapster
; Date: 2002-10-16 15:19:17
; Title: Snake Code
; Description: An implementation of Snake in Blitz. Might prove helpful to someone.

Graphics 800,600,32,1:SetBuffer BackBuffer()

Global sX=10,sY=10
Global sD=0

Global mvT=0
Global Speed=3

Type Tail
	Field t
	Field X,Y
End Type

Const TS=10

Global TLife=200

Global aX=16,aY=20

Global Score=0

Repeat
	
	If KeyDown(205) And sD<>2
		sD=3
	ElseIf KeyDown(203) And sD<>3
		sD=2
	EndIf
	If KeyDown(200) And sD<>1
		sD=0
	ElseIf KeyDown(208) And sD<>0
		sD=1
	EndIf
	

	If sX=aX And sY=aY
		aX=Rand(800/TS)-1
		aY=Rand(600/TS)-1
		score=score+10
		TLife=TLife+10
	EndIf	

	
	mvT=mvT+1
	If mvT>Speed
		T.Tail = New Tail
		T\X=sX:T\Y=sY
		Select sD
			Case 0;Up
				sY=sY-1
			Case 1;Down
				sY=sY+1
			Case 2;Left
				sX=sX-1
			Case 3;Right
				sX=sX+1	
		End Select
		mvT=0
	EndIf	
	
	If sX<0 Or sX>(800/TS) Or sY<0 Or sY>(600/TS) Then End
	
	For T.Tail = Each Tail
		T\T=T\T+1
		If sX=T\X And sY=T\Y Then End
		If T\T>TLife
			Delete T
		EndIf
	Next
	
	Color 255,0,0
	Rect sX*TS,sY*TS,TS,TS,1
	
	Color 150,0,0
	For T.Tail = Each Tail
		Rect T\X*TS,T\Y*TS,TS,TS,1
	Next
	
	Color 0,255,0
	Rect aX*TS,aY*TS,TS,TS,1
	
	Color 255,255,255
	Text 400,50,"Score "+Score,1,1
	
	Flip
	Cls
Until KeyHit(1)
End
