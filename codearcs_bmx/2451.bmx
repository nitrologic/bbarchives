; ID: 2451
; Author: Nate the Great
; Date: 2009-04-03 19:13:25
; Title: 2d planetary motion
; Description: accurate inversesqare algorithm

SuperStrict

Graphics 1440,900,0,60

Global plist:TList = New TList
Global damper:Float = .0001
Global timer:ttimer = CreateTimer(60)
Global mxspdx:Float = 0
Global mxspdy:Float = 0
Global omx:Int = MouseX()
Global omy:Int = MouseY()
planet.Create(1440/2,900/2,0,0,10000,50,255,255,0,1)
While Not KeyDown(key_escape)
Cls

mxspdx = MouseX()-omx
mxspdy = MouseY()-omy
omx = MouseX()
omy = MouseY()
DrawText mxspdx+"  "+mxspdy,1,1

updateplanets()

If MouseHit(1) Then
	Local mas# = Rnd(10,20)
	planet.Create(MouseX(),MouseY(),mxspdx / 30,mxspdy / 30,mas,mas*1.1,Rnd(100,255),Rnd(100,255),Rnd(100,255),.9)
EndIf

If MouseHit(2) Then
	Local mas# = Rnd(10,200)
	mas = mas / 200
	planet.Create(MouseX(),MouseY(),mxspdx / 30,mxspdy / 30,mas,mas*1.1,Rnd(100,255),Rnd(100,255),Rnd(100,255),.9)
EndIf
WaitTimer(timer)
Flip False
Wend
End

Type planet
	Field x#
	Field y#
	Field dx#	'velocity x
	Field dy#	'velocity y
	Field mass#
	Field rad#	'radius
	Field r:Int
	Field g:Int
	Field b:Int
	Field alph#	'alpha
	Field don:Int
	
	Function Create:planet(x#,y#,dx#,dy#,mass#,rad#,r:Int,g:Int,b:Int,alph#)
		Local p:planet = New planet
		p.x = x
		p.y = y
		p.dx = dx
		p.dy = dy
		p.mass = mass
		p.rad = rad
		p.r = r
		p.g = g
		p.b = b
		p.alph = alph
		
		plist.addlast(p:planet)
	End Function
	
	Method draw()
		SetColor r,g,b
		SetAlpha alph#
	
		If rad > 1 Then
			DrawOval x-rad,y-rad,2*rad,2*rad
		Else
			Plot x,y
		EndIf
	End Method
	
	Method update()
		x = x + dx
		y = y + dy
	End Method
	
	Method dist:Float(p:planet)	'returns distance squared
		If p <> Self Then
			Return (p.y - y)^2 + (p.x - x)^2
		EndIf
	End Method
End Type


Function updateplanets()

For Local p:planet = EachIn plist
	p.draw
	p.update
	p.don = True
	For Local p2:planet = EachIn plist
		If Not p2.don Then
			Local d# = p.dist(p2:planet)	' a shortcut for using 1/d^2: just dont use sqr in the distance formula and it turns into 1/d
			If d > 0 Then
				Local dx# = p.x - p2.x
				Local dy# = p.y - p2.y
				p.dx = p.dx - (dx/(d))*p2.mass*damper
				p.dy = p.dy - (dy/(d))*p2.mass*damper
				p2.dx = p2.dx + (dx/(d))*p.mass*damper
				p2.dy = p2.dy + (dy/(d))*p.mass*damper
			EndIf
		EndIf
	Next
Next

For Local p:planet = EachIn plist
	p.don = False
Next
End Function
