; ID: 3197
; Author: Arska
; Date: 2015-03-20 03:51:19
; Title: 2D Collision Physics (bounce)
; Description: Asteroid game wih collision physics

Strict
AppTitle = "Asteroid Wars Example"

Rem

	This is the file of a step by step example of how to make a simple asteroids clone.
	The guide can be found at www.blitzbasic.com - forums - blitzmax - tutorials
	
EndRem



Type TSpaceObject

	Field X#,Y#
	Field XSpeed#,YSpeed#
	Field Direction

	Function Create() EndFunction
	
	Function UpdateAll() EndFunction
	
	Method New() EndMethod
	
	Method Update() EndMethod

	Method Destroy() EndMethod

	Method Draw() EndMethod

End Type

Type TPlayer Extends TSpaceObject

	Global List:TList
	Global Image:Timage

	Field TurnSpeed#
		
	Function Create()
		Local Ship:TPlayer = New TPlayer	 
		If List = Null List = CreateList()
		List.AddLast Ship 'Add Ship Last in List
		
		If Not Image 
			Image = LoadImage("ship.png")
			MidHandleImage( Image )'Center
		EndIf
	EndFunction
	
	Method New()'Starting Values
		X = 300
		Y = 400
	EndMethod
	
	Method Draw()
		SetRotation( Direction )
		DrawImage( Image,X,Y )
	EndMethod

	Method Update()
		Draw()

		'Possible Actions
		Local Up,Down,Left,Right,Fire
		
		'Controls affect actions, Player 1
		If KeyDown(Key_Up) 	  	Up 		= True
		If KeyDown(Key_Down)  	Down 	= True
		If KeyDown(Key_Left)  	Left 	= True
		If KeyDown(Key_Right) 	Right 	= True 
		If KeyDown(Key_Space)  Fire	= True
	
	
		' P H Y S I C S 
		'---------------------------------------
		'Alter these to alter the ships movement
			
			Local Acceleration# 		= 0.04
			Local Friction# 			= 0
			Local TopSpeed# 			= 2.0
			
			Local TurnAcceleration#	= 0.18
			Local TurnFriction#			= 0.0		
			Local TurnMax# 				= 5
	
		'---------------------------------------	
		
								
		'M O V E M E N T  P H Y S I C S
		'--------------------------------------
		'									
		If Up
			'Create a Acceleration Vector and
			'add it to the Speed Vector
			XSpeed:+ Cos(Direction)*Acceleration
			YSpeed:+ Sin(Direction)*Acceleration	
		EndIf
		
		If Down
			'Create a Acceleration Vector and
			'substract it from the Speed Vector
			XSpeed:- Cos(Direction)*Acceleration
			YSpeed:- Sin(Direction)*Acceleration
		EndIf
		
		If Fire TShot.Fire( X, Y, Direction, XSpeed, YSpeed )
		
		'Calculate the length of the Speed Vector
		Local SpeedVectorLength# = Sqr(XSpeed*XSpeed + YSpeed*YSpeed)
		
		If SpeedVectorLength > 0
			'Decrease Speed with Friction if we are moving
			XSpeed:- (XSpeed/SpeedVectorLength)*Friction
			YSpeed:- (YSpeed/SpeedVectorLength)*Friction
		EndIf
		
		If SpeedVectorLength > TopSpeed
			'If we are going beyond the speed barrier then reduce our speed
			'with the amount in which it surpases TopSpeed
			XSpeed:+ (XSpeed/SpeedVectorLength)*(TopSpeed - SpeedVectorLength)
			YSpeed:+ (YSpeed/SpeedVectorLength)*(TopSpeed - SpeedVectorLength)
		EndIf
		
		If X > 800 X = 0
		If Y > 600 Y = 0
		If X < 0 X = 800
		If Y < 0 Y = 600
		
		'Move
		X:+ XSpeed
		Y:+ YSpeed
				
		'Rem 'Visualize the vectors		
		SetRotation 0
		SetColor 255,0,0 'Red 
			DrawLine X,Y,X,Y + YSpeed*50
		SetColor 0,255,0 'Green
			DrawLine X,Y,X+XSpeed*50,Y
		SetColor 0,0,255 'Blue
			'This is the SpeedVector's Length
			'Note that this vector is built by
			'Adding the Red and the Green
			DrawLine X,Y,X+XSpeed*50,Y+YSpeed*50
		SetColor 255,255,255
		'EndRem	
				
		'R O T A T I O N  P H Y S I C S
		'--------------------------------------
		'									
		If Left	TurnSpeed:-TurnAcceleration
		If Right	TurnSpeed:+TurnAcceleration			
		
		'Limit TurnSpeed
		If TurnSpeed >  TurnMax TurnSpeed =  TurnMax
		If TurnSpeed < -TurnMax TurnSpeed = -TurnMax
				
		Direction:+TurnSpeed
		
		If Direction > 360 	Direction:- 360
		If Direction < 0 	Direction:+ 360
		
		'Apply Friction To Rotation
		If TurnSpeed >  TurnFriction	TurnSpeed:- TurnFriction
		If TurnSpeed < -TurnFriction TurnSpeed:+ TurnFriction
		
		'If Friction is Greater than Speed then Stop
		If TurnSpeed < TurnFriction And TurnSpeed > -TurnFriction TurnSpeed = 0
		
	EndMethod

	Function UpdateAll()
		Local Ship:TPlayer
		For Ship = EachIn List
			Ship.Update()
		Next
	EndFunction

End Type

Type TShot Extends TSpaceObject

	Global List:TList
	Global Image:TImage

	Function Fire( X, Y, Direction, XSpeed, YSpeed 	)
		Local Shot:TShot = New TShot	 
		If List = Null List = CreateList()
		List.AddLast Shot
		
		If Not Image'First Time
			Image = LoadImage("bullet2.png")
			MidHandleImage( Image )
		EndIf

		Local ShotSpeed#= 8
		Shot.X 			= X
		Shot.Y 			= Y
		Shot.Direction 	= Direction
		
		'Add Shot Start Speed
		Shot.XSpeed= Cos(Direction)*ShotSpeed + XSpeed
		Shot.YSpeed= Sin(Direction)*ShotSpeed + YSpeed
	EndFunction
	
	Method Draw()
		SetRotation( Direction )
		DrawImage( Image,X,Y )
	EndMethod
	
	Method Update()
		Draw()
		X:+ XSpeed
		Y:+ YSpeed					
		If X > 800 Or Y > 600 Or X < 0 Or Y < 0 Destroy()
	EndMethod

	Function UpdateAll()
		If Not List Return 
		
		Local Shot:TShot
		For Shot = EachIn List
			Shot.Update()
		Next
	EndFunction

	Method Destroy() 
		List.Remove( Self )
	EndMethod
	
EndType	


Type TRock Extends TSpaceObject

	Global List:TList
	Global Image:TImage
	Field Size
	Field Rotation#
	
	Const LARGE  = 3
	Const MEDIUM = 2
	Const SMALL = 1
	
	Function Spawn( X, Y ,Size )
		Local Rock:TRock = New TRock	 
		If Not List List = CreateList()
		List.AddLast Rock
		
		If Not Image'First Time
			Image = LoadImage("asteroid01.png")
			MidHandleImage( Image )
		EndIf
				
		Local RockSpeed# = 0
		Select Size
			Case LARGE'Large		
				RockSpeed = 0.2
			Case MEDIUM'Medium
				RockSpeed = 0.5
			Case SMALL'Small	
				RockSpeed = 1
		EndSelect
		
		Rock.X 			 = X
		Rock.Y 			 = Y
		Rock.Direction 	 = Rand(360)
		Rock.Rotation 	 = Rock.Direction
		Rock.Size 		 = Size
		
		'Add Rock Start Speed
		Rock.XSpeed= Cos(Rock.Direction)*RockSpeed
		Rock.YSpeed= Sin(Rock.Direction)*RockSpeed
	EndFunction
	
	Method Draw()

		Select Self.Size
			Case LARGE		
				SetScale 1.2, 1.2
			Case MEDIUM'Medium
				SetScale 0.8, 0.8
			Case SMALL'Small	
				SetScale 0.4, 0.4
		EndSelect
			
		SetRotation( Rotation )
		Rotation:+0.5
		DrawImage( Image,X,Y )
		SetScale 1,1
	EndMethod
	
	Method Update()
		Draw()
		X:+ XSpeed
		Y:+ YSpeed			
		
		Collision()
				
		If X > 800 X = 0
		If Y > 600 Y = 0
		If X < 0 X = 800
		If Y < 0 Y = 600 	
	EndMethod

	Function UpdateAll()
		If Not List Return 
		
		For Local Rock:TRock = EachIn List
			Rock.Update()
		Next
	EndFunction

	Method Destroy() 
		List.Remove( Self )
	EndMethod
	
	Method Collision()
		Local Radius
		Select Size
			Case LARGE		
				Radius = 100
			Case MEDIUM'Medium
				Radius = 50
			Case SMALL
				Radius = 20
		EndSelect
	
		' Player collision to asteroid
		If TPlayer.list
			For Local Ship:TPlayer = EachIn TPlayer.list
			
				If ImagesCollide(Image, X, Y, 0, ship.image, ship.X, ship.Y, 0)
					
					Local collisionAngle:Float = AngleTO:Float( x, y, Ship.X, Ship.Y )
					Local collisionForce:Float = (XSpeed + YSpeed) - (Ship.XSpeed + Ship.YSpeed)
					If collisionForce < 0 Then collisionForce = collisionForce * - 1
					
					
					Ship.XSpeed:- ( Cos(collisionAngle) / 2)
					Ship.YSpeed:- ( Sin(collisionAngle) / 2)
					
					' If asteroid is small change also it's direction
					If size = SMALL Then
						XSpeed:+ ( Cos(collisionAngle) / 2)
						YSpeed:+ ( Sin(collisionAngle) / 2)
					EndIf
					
				EndIf
				
			Next
		EndIf
	
		
		
		'If a shot hit a rock
		If Not TShot.List Return
		For Local Shot:TShot = EachIn TShot.List
			If Distance( X, Y, Shot.X, Shot.Y ) < Radius
				
				Shot.Destroy()
				
				Select Size
					Case LARGE	
						Destroy()'Destory the rock	
						Spawn( X, Y , MEDIUM )
						Spawn( X, Y , MEDIUM )
					Case MEDIUM
						Destroy()	
						Spawn( X, Y , SMALL )
						Spawn( X, Y , SMALL )
					Case SMALL
						Destroy()'
				EndSelect
	
			EndIf
		Next	
	EndMethod
EndType


Graphics 800,600,0


Const Intro 	= 1
Const Death 	= 2
Const Playing	= 3

Global Mode = Intro
Global Score, HighestScore
Global Level , Rank$

TRock.Spawn( 50,50, TRock.LARGE )
For Local NR = 1 To 20
	TRock.Spawn( 400,300, TRock.SMALL )
Next	

'Main Loop
Repeat' - - - - - - - - - - - - - - - - - 
	
	TShot.UpdateAll()	
	TRock.UpdateAll()
	
	Select Mode

		Case Intro
		
			DrawText "Welcome to Asteroids Wars Beta Version",50,100
			
			DrawText "Highest Score this run: "+HighestScore+" rocks.",50,130
			
			DrawText "Steer with LEFT, RIGHT      ",50,170
			DrawText "Thrust with UP and DOWN     ",50,190
			DrawText "Fire with SPACE             ",50,210
			
			DrawText " ---------  P R E S S   S P A C E   T O  S T A R T  ---------- ",50,260
		
			If KeyHit(Key_Space) StartGame()
			
		Case Playing
			DrawText "Score: "+Score, 20, 20
			TPlayer.UpdateAll()
			If TPlayer.List.Count() = 0 
				Mode = Death
				GetRanking()
				Level = 0
				FlushKeys
			EndIf
			If TRock.List.Count() = 0 StartGame()
			
			
		Case Death
		
			DrawText "~~ You Lost Your Ship - G A M E  O V E R ~~ ",50,100
			
			DrawText "You managed to destroy "+Score+" rocks in "+level+" different zones.",50,130
			
			DrawText "------------------------  ",50,170
			DrawText " Your RANK : "+Rank$		 ,50,190
			DrawText "------------------------  ",50,210
			
			DrawText " ---------  P R E S S  [R]  T O   R E T R Y  ---------- ",50,260					
			
			If KeyHit(KEY_R) Mode = Intro
		
	EndSelect	
			
Flip
Cls
		If KeyHit(Key_Q) End
Until KeyHit(Key_Escape)'- - - - - - - - 
End 
Function StartGame()
	
	If TRock.List.Count() > 0 'First Time
		'Clear
		For Local Rock:TRock = EachIn TRock.List
			Rock.Destroy()
		Next
			
		'Start
		TPlayer.Create()

	Else'In play
	
		Level:+1
		Score:+13		

	EndIf
		
	CreateRocks()
	
	Mode = Playing
						
EndFunction

Function CreateRocks()

	Local Danger = Level*20 + Rand(0,10)
	Local Ship:TPlayer = TPlayer( TPlayer.List.First() )
		
	Repeat 
	
		Local X,Y	
		
		Repeat 
			X = Rand(0,800)
			Y = Rand(0,600)	
		Until Distance(Ship.X, Ship.Y, X, Y ) > 200

		Select Rand(1,2)		
			Case 1
				TRock.Spawn( X, Y, TRock.LARGE )  ;Danger:- 5
			Case 2
				TRock.Spawn( X, Y, TRock.MEDIUM ) ;Danger:- 3
		EndSelect	
			
	Until Danger <= 0
	
EndFunction

Function GetRanking()

	Rank$ = "Classified"
	If Level = 1 Rank = "You didn't get a single one?!" 		
	If Level = 1 Rank = "ROFL" 
	If Level = 2 Rank = "LOL"	
	If Level = 3 Rank = "Newbie"
	If Level = 4 Rank = "Beginner"
	If Level = 5 Rank = "Average"
	If Level = 6 Rank = "Fair"
	If Level = 7 Rank = "Almost got one"	
	If Level = 8 Rank = "Starfighter"
	If Level = 9 Rank = "Rock Destroyer"
	If Level = 10 Rank = "Duplicator"
	'...
	'....
	
	HighestScore = Score
EndFunction

Function Distance#( X1, Y1, X2, Y2 )
	Local DX# = X2 - X1
	Local DY# = Y2 - Y1
	Return Sqr(Dx*Dx + Dy*Dy)
EndFunction

Function AngleTO:Float(X1:Float, Y1:Float, X2:Float, Y2:Float)
	Local dx:Float=x2-x1
	Local dy:Float=y2-y1
	Local Angle:Float=(ATan2(dy,dx)) + 180
	
	If Angle>0 Then
		While(Angle>= 360)
			Angle:-360
		Wend
	ElseIf Angle<0 Then
		While(Angle<0)
			Angle:+360
		Wend
	End If
	Return Angle 
End Function
