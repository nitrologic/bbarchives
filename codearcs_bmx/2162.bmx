; ID: 2162
; Author: Curtastic
; Date: 2007-11-25 07:06:44
; Title: General A* Pathfinder Import
; Description: Just finds the path. Nothing game-specific.

'''''''''''''''''''''''''
'A General A* Pathfinder'
'Coded by Curtastic 2007'
'Coded in Lightning IDE.'
'''''''''''''''''''''''''
SuperStrict

Type TPathfinder Abstract
	'0=no diagonal movement.
	'1=diagonal movement and cutting corners allowed.
	'2=diagonal movement but no cutting corners.
	Global Diagonals:Int
	
	'The higher this number, the more the path will randomly differ from what is optimum.
	Global Randomity:Float
	
	'Map is a float. The closer to 1 the harder it is to move into this tile.
	'All values 1 or greater are considered walls.
	Global Map:Float[, ]
	Global MapWidth:Int
	Global MapHeight:Int
	
	'The amount of steps in the route. (Read only)
	Global Paths:Int
	
	'The resulting path is a 'resliced' route[].
	' as [x0, y0, x1, y1, x2, y2, x3, y3, ..etc. .. xn,yn]
	' The size of this array is paths*2.
	Global Route:Int[]
	
	'The higher the BasicCost, the more accurate and slow pathfinding will be.
	Const BasicCost:Float = .17
	
	
'Private:
	Global PathMap:TPath[, ]
	Const Root2:Float = 1.4142
	
	Function SetUp(MapPassed:Float[,], Diag:Int=1, Random:Float=0)
		Assert Random>0, "Randomity must be positive."
		Assert Diag = 0 Or Diag = 1 Or Diag = 2, "Diagonals must be 0 or 1 or 2."
		Assert MapPassed <> Null, "Map must not be null."
		
		Map = MapPassed
		MapWidth = MapPassed.Dimensions()[0]
		MapHeight = MapPassed.Dimensions()[1]
		Diagonals = Diag
		Randomity = Random
		
	EndFunction
	
	'Returns 1 if successful and 0 if unseccessful.
	'Fills the route[] array if successful.
	Function FindPath:Int(StartX:Int, StartY:Int, EndX:Int, EndY:Int)
		
		Assert Not(StartX < 0 Or StartY < 0 Or StartX >= MapWidth Or StartY >= MapHeight), ..
		 "Starting point out of bounds: " + StartX + "," + StartY
		Assert Not(EndX < 0 Or EndY < 0 Or EndX >= MapWidth Or EndY >= MapHeight), ..
		 "End point out of bounds: " + EndX + "," + EndY
		Assert Map <> Null, ..
		 "SetUp() must be called before FindPath"
		
		'If already on target.
		If StartX = EndX And StartY = EndY Then
			Route = New Int[2]
			Route[0] = StartX
			Route[1] = StartY
			Paths = 1
			Return 1
		EndIf
		
		Paths = 0
		
		'If target is a wall.
		If Map[EndX, EndY] >= 1 Then Return 0
		
		
		Local P:TPath
		Local P2:TPath
		Local NewP:TPath
		Local NewX:Int
		Local NewY:Int
		Local Dir:Int
		Local DirMax:Int
		Local Done:Int
		Local PHead:TPath
		Local MapHere:Float
		Local DistX:Int
		Local DistY:Int
		
		PathMap = New TPath[MapWidth, MapHeight]
		
		'Make first path node at start.
		P = New TPath
		PHead = P
		P.X = StartX
		P.Y = StartY
		PathMap[StartX, StartY] = P
		
		If Diagonals Then
			DirMax = 7
		Else
			DirMax = 3
		EndIf
		
		
		Repeat
			
			
			For Dir = 0 To DirMax
				
				'Move based on direction.
				Select Dir
					Case 0; NewX = P.X + 1; NewY = P.Y
					Case 1; NewX = P.X    ; NewY = P.Y + 1
					Case 2; NewX = P.X - 1; NewY = P.Y
					Case 3; NewX = P.X    ; NewY = P.Y - 1
					Case 4; NewX = P.X + 1; NewY = P.Y + 1
					Case 5; NewX = P.X - 1; NewY = P.Y + 1
					Case 6; NewX = P.X - 1; NewY = P.Y - 1
					Case 7; NewX = P.X + 1; NewY = P.Y - 1
				EndSelect
				
				'Check if it is ok to make a new path node here.
				If NewX >= 0 And NewY >= 0 And NewX < MapWidth And NewY < MapHeight Then
					MapHere = Map[NewX, NewY]
					If MapHere < 1 Then
						
						'No cutting corners.
						If Diagonals = 2 And Dir > 3 Then
							If Map[NewX, P.Y] >= 1 Then Continue
							If Map[P.X, NewY] >= 1 Then Continue
						EndIf
						
						P2 = PathMap[NewX, NewY]
						
						'Check if there already is a path here.
						If P2 = Null Then
							
							'DrawRect newx*29,newy*29,29,29
							'Flip False
							'If KeyHit(key_escape) Then End
							
							'Make new node.
							NewP = New TPath
							PathMap[NewX, NewY] = NewP
							NewP.Parent = P
							NewP.X = NewX
							NewP.Y = NewY
							
							'Cost is slightly more for diagnols.
							If Dir < 4 Then
								NewP.Cost = P.Cost + BasicCost + MapHere + Rnd(0, Randomity)
							Else
								NewP.Cost = P.Cost + (BasicCost + MapHere + Rnd(0, Randomity)) * Root2
							EndIf
							
							'Calculate distance from this node to target.
							If Diagonals Then
								DistX = Abs(NewX - EndX)
								DistY = Abs(NewY - EndY)
								If DistX > DistY Then
									NewP.Dist = DistX - DistY + DistY * Root2
								Else
									NewP.Dist = DistY - DistX + DistX * Root2
								EndIf
								NewP.Dist :* .1
							Else
								NewP.Dist = (Abs(NewX - EndX) + Abs(NewY - EndY)) / 8.0
							EndIf
							
							'Insert node at appropriate spot in list.
							P2 = P
							Repeat
								If P2.After = Null Then
									P2.After = NewP
									Exit
								EndIf
								If P2.After.Dist + P2.After.Cost > NewP.Dist + NewP.Cost Then
									NewP.After = P2.After
									P2.After = NewP
									Exit
								EndIf
								P2 = P2.After
							Forever
							
							'Check if found the end.
							If NewX = EndX And NewY = EndY Then
								Done = 1
								Exit
							EndIf
						Else
							'Overwrite existing path node if this way costs less.
							If P2.Cost > P.Cost + BasicCost + MapHere * Root2 + Randomity Then
								P2.Parent = P
								'Cost is slightly more for diagnols.
								If Dir < 4 Then
									P2.Cost = P.Cost + BasicCost + MapHere + Rnd(0, Randomity)
								Else
									P2.Cost = P.Cost + (BasicCost + MapHere + Rnd(0, Randomity)) * Root2
								EndIf
							EndIf
						EndIf
					EndIf
				EndIf
			Next
			
			If Done = 1 Then Exit
			
			P = P.After
			If P = Null Then Exit
			
		Forever
		
		
		If Done Then
			'Count how many paths.
			P2 = NewP
			Repeat
				Paths:+ 1
				P2 = P2.Parent
				If P2 = Null Then Exit
				'If KeyDown(key_space) Then DebugStop
			Forever
			
			'Make route from end to start.
			Route = New Int[Paths * 2]
			Local i:Int = 0
			P2 = NewP
			Repeat
				Route[i] = P2.X
				i:+ 1
				Route[i] = P2.Y
				i:+ 1
				P2 = P2.Parent
				If P2 = Null Then Exit
			Forever
		EndIf
		
		'Nullify parent pointers so mem will be deallocated.
		P = PHead
		Repeat
			P.Parent = Null
			P = P.After
			If P = Null Then Exit
		Forever
		
		Return Done
	EndFunction
EndType


'Private
Type TPath
	Field X:Int
	Field Y:Int
	Field Parent:TPath
	Field Cost:Float
	Field Dist:Float
	Field After:TPath
EndType
