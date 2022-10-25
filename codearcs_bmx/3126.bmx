; ID: 3126
; Author: zoqfotpik
; Date: 2014-06-04 00:13:21
; Title: Mousewheel Menu
; Description: Mousewheel Menu.

Strict

Global numitems = 7 	
Global currentitem:Int  ' Which item is currently selected 
Global olditem:Int

Global currenttextline:Int
Graphics 640,480

Global ticks = 0
Global namearray:String[100]
Global menufadeouttimer:Int ' When this has elapsed, the menu disappears automatically

namearray[0]="Thing1"
namearray[1]="Thing2"
namearray[2]="Redthing"
namearray[3]="Bluething"
namearray[4]="BarThing"
namearray[5]="FooThing"
namearray[6]="Exit"

While Not KeyDown(KEY_ESCAPE) And Not (currentitem=6 And menufadeouttimer<0)
	ticks = ticks + 1
	Cls
	currenttextline=0
	olditem= currentitem
	currentitem= Abs(MouseZ()) Mod numitems ' Mousewheel Switches Tile Types
	If olditem<> currentitem
		menufadeouttimer = 60
	EndIf
	menufadeouttimer:-1		
	If menufadeouttimer > 0 drawmenu()
	Flip
Wend

Function drawmenu()
	SetBlend alphablend
	SetAlpha .5  ' draw semi-transparent menu background
	SetColor 0,0,0
	DrawRect 0,0,200,1000
	SetAlpha 1
	If menufadeouttimer < 50 And menufadeouttimer > 0 SetAlpha 1*(51/menufadeouttimer)
	'SetColor 255,0,0
	Local recty = 0*12
	DrawRect 0,recty,200,24
	SetScale 2,2
SetColor 255,0,0
	DrawText namearray[currentitem], 0,-2
	SetScale 1,1
	SetColor 255,255,255
	recty = currentitem*12 + 24
	DrawRect 0,recty,200,12
	For Local i = 0 To numitems 
		If i = currentitem
			SetColor 0,0,0
		Else 
			SetColor 255,255,255
		EndIf
		DrawText namearray[i], 10,i*12 + 24
		SetColor 255,255,255
	Next
	SetAlpha 1
End Function

Function consoleprint(toprint$)
	currenttextline:+12
	DrawText(toprint$,10,currenttextline)
End Function
