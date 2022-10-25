; ID: 2794
; Author: Baystep Productions
; Date: 2010-12-08 09:29:22
; Title: Tower Defense
; Description: Simple begginings of a tower defense game!

Strict
Import "ScriptReader.bmx"

'WRITEN BY CHRIS PIKUL
'---------------------
'FREE TO USE/PUBLIC DOMAIN
SetGraphicsDriver D3D7Max2DDriver()
Graphics 1024,768,0,60,GRAPHICS_ALPHABUFFER

Global bg_FileName$,bg_Image:TImage
Global brd_FileName$,brd_Image:TPixmap
Global map_FileName$ = "SimpleTest"													'|<-------------- MAP FILE NAME! WILL LOOK IN MAPS DIRECTORY AND WILL ADD .DAT EXTENSION
Global startX%,startY%,endX%,endY%
Global starting:Point
Global ending:Point
Global money%=25,health%=25
Global sinceLastWave%,waveNumber%=1,waveCount%=1,waveMode%,waveCreated%
Global mouseMode%,mouseSel:Tower,permSel:Tower

Global tower1:TImage = LoadAnimImage("Res\Tower1.png",25,25,0,3)				'|<-------------- YOUR TOWER IMAGE! First frame is the base, 2 is barrel, 3 is barrel firing
MidHandleImage(tower1)

Global path:TList = New TList
Type Point
	Field x%,y%
	Field mode%
	Function Create(x%,y%,mode%=0)
		Local pnt:Point = New Point
		pnt.x = x%
		pnt.y = y%
		pnt.mode% = mode%
		path.AddLast pnt
	EndFunction
EndType

LoadMap()

starting:Point = Point(path.First())
ending:Point = Point(path.Last())

Global towers:TList = New TList
Type Tower
	Field x%,y%
	Field damage#
	Field range#
	Field fireRate%
	Field lastFire%
	Field target:Enemy
	Field targetDist#
	Field imageMode%
	Field rot#
	Function Create(setX%,setY%)
		Local newTower:Tower = New Tower
		newTower.x = setX%
		newTower.y = setY%
		newTower.fireRate%=20
		newTower.damage# = 1
		newTower.range# = 75
		towers.AddLast newTower
	EndFunction
EndType

Global wave:TList = New TList
Type Enemy
	Field x#,y#
	Field pointIndex%
	Field nextPoint:Point
	Field health#,speed#
	Function Create()
		Local newEnemy:Enemy = New Enemy
		newEnemy.x = starting.x
		newEnemy.y = starting.y - 25
		newEnemy.health# = 1+(waveNumber%*0.4)
		newEnemy.speed# = 0.9+(waveNumber%*0.2)
		newEnemy.pointIndex% = 0
		newEnemy.nextPoint:Point = starting
		wave.AddLast newEnemy
	EndFunction
EndType
Enemy.Create()

SetBlend ALPHABLEND
While Not KeyHit(KEY_ESCAPE)
	Cls
	DrawImage(bg_Image,0,0)
	For Local enmy:Enemy = EachIn wave
		DrawOval(enmy.x-5,enmy.y-5,10,10)
		If enmy.x<enmy.nextPoint.x
			If (enmy.nextPoint.x-enmy.x)<enmy.speed#
				enmy.x = enmy.nextPoint.x
			Else
				enmy.x = enmy.x+enmy.speed#
			EndIf
		ElseIf enmy.x>enmy.nextPoint.x
			If (enmy.x-enmy.nextPoint.x)<enmy.speed#
				enmy.x = enmy.nextPoint.x
			Else
				enmy.x = enmy.x-enmy.speed#
			EndIf
		EndIf
		If enmy.y<enmy.nextPoint.y
			If (enmy.nextPoint.y-enmy.y)<enmy.speed#
				enmy.y = enmy.nextPoint.y
			Else
				enmy.y = enmy.y+enmy.speed#
			EndIf
		ElseIf enmy.y>enmy.nextPoint.y
			If (enmy.y-enmy.nextPoint.y)<enmy.speed#
				enmy.y = enmy.nextPoint.y
			Else
				enmy.y = enmy.y-enmy.speed#
			EndIf
		EndIf
		If enmy.x<=(enmy.nextPoint.x+enmy.speed) And enmy.x>=(enmy.nextPoint.x-enmy.speed) And enmy.y<=(enmy.nextPoint.y+enmy.speed) And enmy.y>=(enmy.nextPoint.y-enmy.speed)
			enmy.pointIndex% = enmy.pointIndex%+1
			If enmy.nextPoint = ending
				wave.Remove enmy
				waveCount = waveCount-1
				health = health - 1
			Else
				enmy.nextPoint = Point(path.ValueAtIndex(enmy.pointIndex%))
			EndIf
		EndIf
		If enmy.health<=0
			wave.Remove enmy
			money=money+1
			wave.Remove enmy
			waveCount% = waveCount%-1
		EndIf
	Next
	For Local twr:Tower = EachIn towers
		twr.lastFire% = twr.lastFire%+1
		For Local enmy:Enemy = EachIn wave
			Local dist# = Sqr((enmy.x-twr.x)^2+(enmy.y-twr.y)^2)
			If twr.target
				If dist#<twr.targetDist#
					twr.targetDist#=dist#
					twr.target = enmy
				EndIf
			Else
				If dist#<twr.range
					twr.targetDist#=dist#
					twr.target=enmy
				EndIf
			EndIf
		Next
		If twr.target
			twr.targetDist# = Sqr((twr.target.x-twr.x)^2+(twr.target.y-twr.y)^2)
			twr.rot# = ATan2((twr.y-twr.target.y),(twr.x-twr.target.x))
			If twr.lastFire%>=twr.fireRate%
				twr.imageMode%=5
				twr.lastFire%=0
				twr.target.health = twr.target.health-twr.damage#
				If twr.target.health<=0
					twr.target = Null
				EndIf
			EndIf
			If twr.targetDist#>twr.range#
				twr.target = Null
			EndIf
		EndIf
		If MouseX()>=(twr.x-10) And MouseX()<=(twr.x+10) And MouseY()>=(twr.y-10) And MouseY()<=(twr.y+10)
			mouseSel = twr
		ElseIf mouseSel=twr
			mouseSel = Null
		EndIf
		DrawImage(tower1,twr.x,twr.y)
		SetRotation(twr.rot#)
		If twr.imageMode%>0
			twr.imageMode% = twr.imageMode%-1
			DrawImage(tower1,twr.x,twr.y,2)
		Else
			DrawImage(tower1,twr.x,twr.y,1)
		EndIf
		SetRotation(0)
		Rem
		SetAlpha(0.1)
		SetColor(255,0,0)
		DrawOval(twr.x-twr.range,twr.y-twr.range,twr.range*2,twr.range*2)
		SetColor(255,255,255)
		SetAlpha(1)
		EndRem
	Next
	If KeyHit(KEY_SPACE) And money>=10 Then mouseMode% = 1
		Select mouseMode%
			Case 0
				If mouseSel
					SetAlpha(0.1)
					SetColor(255,0,0)
					DrawOval(mouseSel.x-mouseSel.range,mouseSel.y-mouseSel.range,mouseSel.range*2,mouseSel.range*2)
					SetColor(255,255,255)
					SetAlpha(1)
					If MouseHit(1)
						mouseMode%=2
						permSel = mouseSel
					EndIf
				EndIf
			Case 1 'Building crap
				SetAlpha(0.1)
				SetColor(255,0,0)
				DrawOval(MouseX()-75,MouseY()-75,150,150)
				SetColor(255,255,255)
				SetAlpha(1)
				If MouseHit(1)
					Local pix = brd_Image.ReadPixel(MouseX(),MouseY())
					Local pixRed = (pix Shr 16) & $FF
					Local pixGreen = (pix Shr 8) & $FF
					Local pixBlue = pix & $FF
					If pixRed=0 And pixGreen=0 And pixBlue=0
						tower.Create(MouseX(),MouseY())
					EndIf
					money=money-10
					mouseMode%=0
				EndIf
			Case 2 'Selecting crap
				DrawText("Damage: "+permSel.damage#+" upgrade cost $"+(2*Int(permSel.damage)),705,55)
				DrawText("Range: "+permSel.range#+" upgrade cost $"+Int(Int(permSel.range)*0.026),705,65)
				DrawText("Fire Rate: "+permSel.fireRate%+" upgrade cost $"+Int(100-(Int(permSel.fireRate%)*4.75)),705,75)
				If KeyHit(KEY_D) And money>=(2*Int(permSel.damage))
					money = money - (2*Int(permSel.damage))
					permSel.damage# = permSel.damage#+(permSel.damage#*0.25)
				EndIf
				If KeyHit(KEY_R) And money>=Int(Int(permSel.range)*0.026)
					money = money - Int(Int(permSel.range)/0.026)
					permSel.range# = permSel.range#+(permSel.range#*0.25)
				EndIf
				If KeyHit(KEY_F) And money>=Int(100-(Int(permSel.fireRate%)*4.75))
					money = money - Int(100-(Int(permSel.fireRate%)*4.75))
					permSel.fireRate% = permSel.fireRate%-(permSel.fireRate%*0.10)
				EndIf
				If KeyHit(KEY_RETURN)
					mouseMode% = 0
					permSel = Null
					FlushKeys()
					FlushMouse()
				EndIf
		EndSelect
	DrawText("Cash: $"+money,705,5)
	DrawText("Health: "+health,705,15)
	DrawText("Wave: #"+waveNumber+"/"+waveCount,705,25)
	DrawText("Enemy: Health="+(1+(waveNumber%*0.4))+", Speed="+(0.9+(waveNumber%*0.2)),705,35)
	Flip
	
	If waveCount<=0 And waveMode=0
		waveNumber = waveNumber+1
		sinceLastWave=MilliSecs()
		waveMode=1
	EndIf
	Select waveMode
		Case 1
			If MilliSecs()>=sinceLastWave+3000
				waveMode = 2
				waveCreated = 0
			EndIf
		Case 2
			Enemy.Create()
			waveCount=waveCount+1
			waveCreated = waveCreated+1
			sinceLastWave = MilliSecs()
			waveMode = 3
		Case 3
			If MilliSecs()>=sinceLastWave+200
				waveMode = 2
				If waveCreated>=(waveNumber*2)
					waveMode=0
				EndIf
			EndIf
	EndSelect
	If AppSuspended() Or KeyHit(KEY_P)
		SetAlpha(0.5)
		SetColor(0,0,0)
		DrawRect(0,0,1024,768)
		SetAlpha(1)
		SetColor(255,255,255)
		DrawText("[PAUSED]",500,300)
		Flip
		While Not KeyHit(KEY_P)
			DrawText "PAUSED",0,0
		Wend
		FlushKeys()
		FlushMouse()
	EndIf
	If AppTerminate()
		If Confirm("Are you sure you want to quit?") Then Exit
	EndIf
Wend
End

Function LoadMap()
	Local file:TStream = ReadFile("Maps\"+map_FileName$+".dat")
	If Not file Then RuntimeError("Could not load map file!")
	bg_FileName$ = file.ReadLine()
	brd_FileName$ = file.ReadLine()
	Local points% = file.ReadByte()
	For Local temp% = 1 To points%
		Local tempX% = file.ReadInt()
		Local tempY% = file.ReadInt()
		Local tempM% = file.ReadByte()
		Point.Create(tempX%,tempY%,tempM%)
	Next
	CloseFile(file)
	bg_Image:TImage = LoadImage("Maps\"+bg_FileName$)
	brd_Image:TPixmap = LoadPixmap("Maps\"+brd_FileName$)
EndFunction

'WRITEN BY CHRIS PIKUL
'---------------------
'FREE TO USE/PUBLIC DOMAIN
