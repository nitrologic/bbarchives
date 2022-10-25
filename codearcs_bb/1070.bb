; ID: 1070
; Author: Nilium
; Date: 2004-06-04 23:41:48
; Title: MiniShoot
; Description: A small top-down 2D shooter.  Enemies, projectiles, and a player

Type Enemy
	Field X#,Y#
	Field Angle#
	Field Wait%
	Field Health
End Type

Type Bolt
	Field X#,Y#
	Field Angle#
	Field Class
End Type

Global PlayerX#,PlayerY#
Global PlayerAngle#,PlayerHealth%

PlayerX = 320
PlayerY = 240
PlayerAngle = 180
PlayerHealth = 20

Graphics 640,480,32,2
SetBuffer BackBuffer()

For N = 0 To Rand(5,20)
	E.Enemy = New Enemy
	EX = Rand(640) : EY = Rand(480) : EAngle = Rand(360)
	EHealth = Rand(4,8)
Next

Repeat
	Delay 12

	If PlayerHealth <= 0 Then End

	PlayerAngle = PlayerAngle + (KeyDown(203) - KeyDown(205))*3
	PlayerX = PlayerX + Sin(PlayerAngle)*(KeyDown(200)-KeyDown(208))*3
	PlayerY = PlayerY + Cos(PlayerAngle)*(KeyDown(200)-KeyDown(208))*3
	
	Wait = Wait + 1
	If KeyDown(57) And Wait >= 5
		Wait = 0
		B.Bolt = New Bolt
		BX = PlayerX
		BY = PlayerY
		BAngle = PlayerAngle
		BClass = 1
	EndIf
	
	For E.Enemy = Each Enemy
		S# = PlayerY - EY
		T# = PlayerX - EX
		Mag# = Sqr(S*S+T*T)
		S = S/Mag
		T = T/Mag
		Angle# = ATan2(T,S)-EAngle
		EAngle = EAngle + Min(Max(Angle,.75),-.75)
		EX = EX + Sin(EAngle)
		EY = EY + Cos(EAngle)
		EWait = EWait + 1
		If Abs(Angle) < 10 And EWait >= 24 Then
			EWait = 0
			B.Bolt = New Bolt
			BX = EX+Sin(EAngle)*20
			BY = EY+Cos(EAngle)*20
			EX = EX - Sin(EAngle)*20
			EY = EY - Cos(EAngle)*20
			BAngle = EAngle + Rnd(-20,20)
			BClass = 2
		EndIf
		Oval EX-10,EY-10,21,21,True
		Line EX,EY,EX+Sin(EAngle)*24,EY+Cos(EAngle)*24
		Text EX,EY-40,EHealth,1,0
		If EHealth <= 0 Then Delete E
	Next
	
	For B.Bolt = Each Bolt
		For N = 0 To 6
			If B <> Null Then
				BX = BX + Sin(BAngle)*(3-BClass)
				BY = BY + Cos(BAngle)*(3-BClass)
				If BClass = 2 Then
					If RectsOverlap(BX-1,BY-1,3,3,PlayerX-10,PlayerY-10,21,21) Then Delete B PlayerHealth = PlayerHealth-1 Exit
				EndIf
				For E.Enemy = Each Enemy
					If RectsOverlap(BX-1,BY-1,3,3,EX-10,EY-10,21,21) Then Delete B EHealth = EHealth - 1 Exit
				Next
				If B <> Null Then If BX < 0 Or BX > 640 Or BY < 0 Or BY > 480 Then Delete B Exit
			EndIf
		Next
		If B <> Null Then Line BX,BY,BX - Sin(BAngle)*6,BY - Cos(BAngle)*6
	Next
	
	Oval PlayerX-10,PlayerY-10,21,21,True
	Line PlayerX,PlayerY,PlayerX+Sin(PlayerAngle)*24,PlayerY+Cos(PlayerAngle)*24
	Text PlayerX,PlayerY-40,PlayerHealth,1,0
	
	Flip
	Cls
Until KeyHit(1)

Function Min#(X#,Y#)
	If X < Y Then Return Y
	Return X
End Function

Function Max#(X#,Y#)
	If X > Y Then Return Y
	Return X
End Function
