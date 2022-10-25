; ID: 1197
; Author: Jonathan Nguyen
; Date: 2004-11-14 15:20:41
; Title: Pong
; Description: A quick pong example with AI that adjusts difficulty.

; Setup
Graphics 320,240,16,0
SeedRnd MilliSecs()
Print "Pong by Jonathan Nguyen"
Print "Difficulty (0.0 hardest, 1.0+ easiest)"
difficulty#=Float#(Input("  ")) ;difficulty#=0.0 ; DIFFICULTY: 0.0 being the hardest, 1.0+ being the easiest
Print "Sensitivity (default 100[%])"
sensitivity#=Float#(Input("  "))/100.0
; Initiate
hold=1 : sync=CreateTimer(60)
SetBuffer BackBuffer()
FlushMouse
; Program Loop
While Not KeyDown(1)=True
	WaitTimer sync
	Cls : Color 96,96,96 : Line 160,0,160,240
	Text 160-2-StringWidth(Str$(playerscore)),228,Str$(playerscore) : Text 160+2,228,Str$(computerscore)
	Color 255,255,255
	paddlespeed#=MouseYSpeed()*sensitivity# : playery#=playery#+paddlespeed#
	If playery#<24 Then playery#=24
	If playery#>216 Then playery#=216
	Rect 4,playery#-24,8,48
	If hold=-1 Then computerai#=computerai#-1 : compspeed#=Cos(computerai#*8+30)*12/(difficulty#*2+1.0) Else compspeed#=(bally#-computery#)/Rnd(8.0,64.0)/((320.0+difficulty#*100-ballx#)/320.0)
	If compspeed#>30 Then compspeed#=30
	If compspeed#<-30 Then compspeed#=-30
	computery#=computery#+compspeed#
	If computery#<24 Then computery#=24
	If computery#>216 Then computery#=216
	Rect 308,computery#-24,8,48
	; State
	Select hold
		Case 1
			ballx#=22
			bally#=playery#
			If MouseHit(1)=True Then ballxv#=8 : ballyv#=paddlespeed# : hold=0
		Case 0
			ballx#=ballx#+ballxv# : bally#=bally#+ballyv#
			If bally#<8 Then bally#=8 : ballyv#=ballyv#*-1.0
			If bally#>232 Then bally#=232 : ballyv#=ballyv#*-1.0
			If bally#=>playery#-30 And bally#=<playery#+30 And ballx#<28 Then ballx#=28 : ballxv#=ballxv#*-1.025 : ballyv#=(ballyv#+paddlespeed#)*0.5+Rnd(-0.5,0.5)
			If bally#=>computery#-30 And bally#=<computery#+30 And ballx#>292 Then ballx#=292 : ballxv#=ballxv#*-1.025 : ballyv#=(ballyv#+compspeed#)*0.5+Rnd(-0.5,0.5)
			If ballx#=<0 Then computerscore=computerscore+1 : difficulty#=difficulty#+0.05 : totdifficulty#=totdifficulty#+difficulty# : hold=1
			If ballx#=>320 Then playerscore=playerscore+1 : difficulty#=difficulty#-0.05 : totdifficulty#=totdifficulty#+difficulty# : hold=-1 : computerai#=Rnd(120,240)
			If difficulty#<0 Then difficulty#=0
		Case -1
			ballx#=292
			bally#=computery#
			If computerai#=<0 Then ballxv#=8 : ballyv#=compspeed# : hold=0
	End Select
	Text 0,0,difficulty#
	Oval ballx#-8,bally#-8,16,16 : Flip
Wend
; Results
FlushKeys
avgdifficulty#=totdifficulty#/Float#(computerscore+playerscore)
SetBuffer FrontBuffer() : Text 160,96,"Average Difficulty",True : Text 160,108,Str$(avgdifficulty#),True : Text 160,132,"Press any key...",True : WaitKey
