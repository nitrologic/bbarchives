; ID: 2492
; Author: jankupila
; Date: 2009-05-31 08:03:23
; Title: Balls
; Description: Example of CreateList

Strict

Local number_of_balls=100
Type Tball
	Field x:Int
	Field y:Int

	Field colorR
	Field colorG
	Field colorB

	Field addX
	Field addY

	Method Draw()
		SetColor Self.colorR,Self.colorG,Self.colorB
		DrawOval Self.x,Self.y,10,10
	End Method

	Method Add()
		Self.x=Self.x+addX
		Self.y=Self.y+addY
	End Method

	Method borders()
		If Self.x=1 Or Self.x=800 Then Self.addx=-Self.addX
		If Self.y=1 Or Self.y=600 Then Self.addy=-Self.addY
	End Method
End Type

Global ballList:TList = CreateList()

For Local x=1 To number_of_balls
	Global ball:tball	
	ball=New Tball
	ball.x=Rand(2,799)
	ball.y=Rand(2,599)
	ball.colorR=Rand(1,255)
	ball.colorG=Rand(1,255)
	ball.colorB=Rand(1,255)
	Local RndAddx=Rand(1,2)
	If RnDAddx=1 Then ball.addX=1 Else ball.addX=-1
	Local RndAddy=Rand(1,2)
	If RndAddy=1 Then ball.addY=1 Else ball.addY=-1
	ListAddLast (ballList,ball)
Next

Graphics 800,600,1
SeedRnd MilliSecs()

While Not KeyDown(KEY_ESCAPE)
	Cls
	For Local ball:Tball = EachIn balllist
		ball.draw
		ball.add
		ball.borders
	Next
	Flip
Wend
