; ID: 2750
; Author: Polan
; Date: 2010-08-07 13:27:20
; Title: Fighting AI
; Description: 2 AI teams fighting each other

Framework BRL.GLMax2D
Import BRL.Random
SetGraphicsDriver GLMax2DDriver()
SeedRnd(MilliSecs())
Global gw = 1024,gh = 768
Graphics gw,gh
Type dot
	Field x#,y#
	Field sp#
	Field rot#,rotdoc#
	Field zwrot#
	Field team
	Field id
	Field status,stat
	Field chaser
	Field hp
	Field frate,rate
	Method update()
		x :+ Cos(rot)
		y :+ Sin(rot)
		'If status = 0
			status1 = Rand(0,1)
			If status1 = 1
				status = 2
				dist# = 65000
				For x1 = 0 To count-1
				If d[x1] And d[x1].team <> team
					dist1# = Sqr((x-d[x1].x)^2+(y-d[x1].y)^2)
					If dist1 < dist
						dist = dist1
						chaser = d[x1].id
						d[x1].status = 3
						d[x1].chaser = id
							EndIf
						EndIf
				Next
					EndIf
				'EndIf
		rate :+ 1
		If status = 2
			If d[chaser]
				rotdoc = ATan2(-y+d[chaser].y,-x+d[chaser].x)
				dist# = Sqr((x-d[chaser].x)^2+(y-d[chaser].y)^2)
				If dist < 100
					If rot > rotdoc-10 And rot < rotdoc+10
						If rate > frate 
							rate = 0
							b:bullet = New bullet
							b.team = team
							b.x = x
							b.y = y
							b.rot = rot + Rand(-2,2)
							ListAddLast(list,b)
								EndIf
							EndIf
						EndIf
				If dist > 150
					d[chaser].status = 0
					status = 0
						EndIf
				Else
				status = 0
					EndIf
				EndIf
		If status = 3
			
				EndIf
		If status = 3 Or status = 0
			If x < 50
				rotdoc = ATan2(0,x)
					EndIf
			If x > gw-50
				rotdoc = ATan2(0,gw-50-x)
					EndIf
			If y < 50
				rotdoc = ATan2(y,0)
					EndIf
			If y > gh-50
				rotdoc = ATan2(gh-50-y,0)
					EndIf
				EndIf
		If rot < rotdoc Then rot :+ zwrot
		If rot > rotdoc Then rot :- zwrot
		temp# = rot-rotdoc
		If temp < 0 Then temp = -temp
		If temp < zwrot Then rot = rotdoc
		If team = 1
			SetColor 0,0,255
			Else
			SetColor 255,0,0
				EndIf
		SetRotation rot
		DrawRect x-1,y-1,3,3
			End Method
		End Type
Global list:TList = CreateList()
Type bullet
	Field x#,y#,rot#
	Field sp# = 5,team
	Field life=40
	Method update()
		life :- 1
		If life < 0 Then ListRemove(list,Self)
		If team = 1
			SetColor 100,100,200
			Else
			SetColor 200,100,100
				EndIf
		x :+ Cos(rot)*sp
		y :+ Sin(rot)*sp
		If x < 0 Or y < 0 Or x > gw Or y > gh Then ListRemove(list,Self)
		For x1 = 0 To count-1
		If d[x1] And d[x1].team <> team
			dist# = Sqr((x-d[x1].x)^2+(y-d[x1].y)^2)
			If dist < 2
				'boom(d[x1].x,d[y1].y)
				d[x1].hp :- 1
				If d[x1].hp <= 0 Then d[x1] = Null
				ListRemove(list,Self)
					EndIf
				EndIf
		Next
		SetRotation rot-90
		DrawLine x,y,x,y-3
			End Method
		End Type
Type boom
	
		End Type
Global timer = 0
Global count = 100 , d:dot[count]
For x = 0 To count-1
newdot()
Next

While Not KeyHit(key_escape)
Cls

timer :+ 1
If timer > 3
	timer = 0
	newdot()
		EndIf

For b:bullet = EachIn list
b.update()
Next
For x = 0 To count-1
If d[x]
	d[x].update()
		EndIf
Next

Flip
Wend

Function newdot()
	For x = 0 To count-1
	If d[x]
		Else
		d:dot[x] = New dot
		d[x].x = Rand(0,1023)
		d[x].y = Rand(0,767)
		d[x].sp = Rand(1,4)
		d[x].zwrot = Rand(1,4)
		d[x].team = x Mod 2 + 1
		d[x].rot = Rand(0,360)
		d[x].rotdoc = Rand(0,360)
		d[x].id = x
		d[x].frate = Rand(1,5)
		d[x].hp = Rand(1,6)
			EndIf
	Next
End Function
