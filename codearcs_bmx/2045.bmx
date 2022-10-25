; ID: 2045
; Author: Jesse
; Date: 2007-06-25 22:16:35
; Title: Guided missile trail
; Description: :)

SuperStrict
Framework BRL.GLMax2D
Import BRL.Random



Const Acceleration# 	= 00.5
Const TopSpeed# 		= 05.0
Const TurnAcceleration#	= 01.0
Const TurnMax# 		= 10.0
Const MaxPoints#		= 50.0
Const MissileSpeed%	= 2
Local Missiles% = 20
Local Missile:TMisile[Missiles] ' create missiles
Local streak:tstreak[Missiles]  ' create streak for missiles


Graphics 800,600,32,60
Global width% = GraphicsWidth()
Global height% = GraphicsHeight()

SetBlend alphablend
glEnable(GL_LINE_SMOOTH)	'Quick antaliasing hack
glHint(GL_LINE_SMOOTH_HINT,GL_NICEST)
glLineWidth(3.0)

' create missiles and streaks.

For Local i% = 0 To Missiles-1
	Missile[i] = TMisile.create(Rand(700)+50,Rand(500)+50,Rand(360))
	streak[i] =tstreak.create(Missile[i].x,Missile[i].y,Rand(255),Rand(255),Rand(255))
Next

SeedRnd MilliSecs()

Repeat
	SetColor 20,200,40
	DrawText "Press (esc) to exit",300,30
	For Local i% = 0 To Missiles-1
		For Local s% = 1 To MissileSpeed
			Missile[i].update(MouseX(),MouseY())
			streak[i].add(Missile[i].x,Missile[i].y)
		Next 
		streak[i].draw()
	Next
	Flip()
	Cls 
Until KeyHit(key_escape)


'
'				Resources
'-----------------------------------------------------------------------------------
'

Type Tpoint
	Field x#
	Field y#
End Type

' used to draw a streak. 
'Note:  does become jugged If points are separated To far And
'		angles are set sharp.

Type Tstreak
	Field PointList:TList 
	Field lastpoint:tpoint
	Field active%
	Field Red%,Green%,Blue%
	Field Tint#
	Function create:tstreak(x#,y#,Red%,Green%,Blue%)
		Local s:tstreak = New tstreak
		Local p:tpoint  = New tpoint
		If Not s.PointList Then 
			s.PointList      = CreateList()
			s.lastpoint = New tpoint
			s.active    = True
		EndIf
		p.x = x
		p.y = y
		s.PointList.addlast(p)
		s.lastpoint.x = p.x
		s.lastpoint.y = p.y
		s.Red    = Red
		s.Green  = Green
		s.Blue   = Blue
		s.tint 	= 1.0 /MaxPoints
		Return s
	End Function
	' adds a segment to a streak.
	
	Method add(x#,y#)
		If PointList.count()<Int(MaxPoints) 
			Local p:tpoint = New tpoint
			p.x = x
			p.y = y
			PointList.addlast(p)
			lastpoint.x = p.x
			lastpoint.y = p.y 
		Else
			PointList.remove(PointList.first())
		EndIf
	End Method
	
	'
	
	Method draw()
		
		If Not PointList.count() Return 	
		If PointList.count() > 1 Then 
			Local Alpha# = 0.0
			Local p1:Tpoint = Tpoint(PointList.first())
			SetColor Red,Green,Blue
			For Local p2:tpoint = EachIn PointList
				If p2<>p1 Then 
					SetAlpha alpha
					DrawLine(p1.x,p1.y,p2.x,p2.y,False)
					p1 = p2	
				EndIf
				alpha :+Tint
			Next		
		EndIf 
	End Method
End Type


Type TMisile 	
	Field Red%
	Field Green%
	Field Blue%
	Field x#,y#
	Field DirectionX#,DirectionY#
	Field XS#,YS#
	Field Done:Int
	Field Degree#
	Field Direction#
	Field TurnSpeed#
	Function create:TMisile(x#,y#,dir#)
	
		Local s:TMisile = New TMisile
		s.x = x
		s.y = y
		s.Direction = Dir 
		s.Done = False
		Return s
		
	End Function
	
	'Used To Draw Missile (currently unused)
	
	Method draw() ' used to draw missile
	' use from field in type
	' x, y -- is the current missile position also used for head of streak
	'direction -- is the angle the missile is facing in degrees. 
	End Method
	
	' Target chassing logic
	
	Method Update%(nx%,ny%)
		
		'Set acceleration
	
		XS:+ Cos(Direction)*Acceleration
		YS:+ Sin(Direction)*Acceleration
		
		Local CurrentSpeed# = Sqr(XS*XS + YS*YS)
		
		If CurrentSpeed > TopSpeed 
			XS:+ (XS/CurrentSpeed)*(TopSpeed - CurrentSpeed)
			YS:+ (YS/CurrentSpeed)*(TopSpeed - CurrentSpeed)
		EndIf
		
		X:+ XS 
		Y:+ YS 
				
		'Set Rotation
										
		Local distance# = Sqr((x-nx)^2+(y-ny)^2) '
		Local TargetAngle# = (ATan2(y-ny,x-nx)+180.0) Mod 360.0		
		Local difference# = Abs(TargetAngle-Direction)

		'turn toward target

		If TargetAngle < Direction				
			If difference > 180.0 TurnSpeed:+TurnAcceleration Else TurnSpeed:-TurnAcceleration
		ElseIf TargetAngle > Direction
			If difference > 180.0 TurnSpeed:-TurnAcceleration Else TurnSpeed:+TurnAcceleration
		EndIf
		
		'If found stop turning
		
		If difference < 1.0 TurnSpeed = 0.0			
				
		'Limit TurnSpeed

		If TurnSpeed >  TurnMax TurnSpeed =  TurnMax
		If TurnSpeed < -TurnMax TurnSpeed = -TurnMax
		Direction = (Direction+TurnSpeed+360) Mod 360
		
	EndMethod
End Type
