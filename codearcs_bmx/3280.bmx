; ID: 3280
; Author: coffeedotbean
; Date: 2016-06-26 18:23:15
; Title: A* (astar) routine
; Description: 4 and 8 way Path finding with and without corner cutting

'===========================================================================================================================
'===========================================================================================================================
' CREDITS
' =======
' BlitzMax port of Patrick Lester's "A* Pathfinding for Beginners" http://www.policyalmanac.org/games/aStarTutorial.htm
' Conversion by 'CoffeeDotBean' http://www.blitzbasic.com/Account/showuser.php?id=584 (June 26 2016)
'
'
' HOW TO USE
' ==========
'1. Include this file (.bmx) at the top of your program.
'
'2. Create a NEW TPath_Astar:Object. e.g myPath:TPath_Astar = New TPath_Astar; myPath is your hook going forward.
'
'3. Call Prepare() Method and pass in the required information.
'      Arr[,]             = 2d Int Array containing your Map data, does not need to be specially formatted.
'      UnWalkableValues[] = Int Array containing Values that the Search will consider as Walls & impassable terrain.
'      StartX/Y           = Start Grid ref.
'      TargetX/Y          = Target Grid ref.
'
'4. Call Search() Method to look for a Path, call PathFound() to know if a path was found
'      EightWay           = If False diaganls will be ignored.
'      CutCorners         = (only applicanle with Eightway=True), Cut diagonaly through corners.
'
'5. Return A found Path, all Methods will return (-1) if no path is avaliable
'      GetPathInt()       = Return Int Array with directions (Start ==> Target) 1,2,3,4,5,6,7,8 (1=up, 2=up&right, 3=right, 4=down&right etc..).
'      GetPathStr()       = Return String Array with compass directions (Start ==> Target) N,NE,E,SE,S,SW,W,NW.
'      GetPathRef()       = Return Int Array with Array grid references (probably not useful).
'
'
' OTHER METHODS
' =============
'*. PathFound()           = Return True if a path was found after calling Search() Method.
'*. PathClear()           = Flag the path as not found (does not nullify any other data).
'*. GetPathLength()       = Return Length of the path as steps (tiles) between Start and Target along the path.
'*. GetPathDistance()     = Return Distance between Start and Target in Pixels along the path.
'===========================================================================================================================
'===========================================================================================================================

Type TPath_Astar
	Const NOTFINISHED:Int = 0
	Const NOTSTARTED:Int = 0
	Const FOUND:Int = 1
	Const NONEXISTENT:Int = 2
	Const WALKABLE:Int = 0
	Const UNWALKABLE:Int = 1
	Const ONCLOSEDLIST:Int = 2
	Const ONOPENLIST:Int = 1
	Field MapWidth:Int
	Field MapHeight:Int
	Field Walkability:Int[,]
	Field OpenList:Int[]
	Field WhichList:Int[,]
	Field OpenX:Int[]
	Field OpenY:Int[]
	Field ParentX:Int[,]
	Field ParentY:Int[,]
	Field FCost:Int[]
	Field GCost:Int[,]
	Field HCost:Int[]
	Field StartX:Int
	Field StartY:Int
	Field TargetX:Int
	Field TargetY:Int
	Field NumberOfOpenListItems:Int
	Field NewOpenListItemID:Int
	Field Path:Int
	Field Steps:TList
	
	rem
	bbdoc:	Flag path as not exisiting
	end rem
	Method Clear:Int()
		Path = NONEXISTENT
	End Method
	
	rem
	bbdoc:	Search for a path, EightWay=False means no diagonals, CutCorners only applicable with diagonals (Eightway)
	end rem
	Method Search:Int(EightWay:Int = True, CutCorners:Int = False)
		NumberOfOpenListItems = 1
		OpenList[1] = 1
		OpenX[1] = StartX
		OpenY[1] = StartY
		
		Repeat
'6 -------------------------------------------------------------------	
			If NumberOfOpenListItems <> 0
				Local ParentXval:Int = OpenX[OpenList[1]]
				Local ParentYval:Int = OpenY[OpenList[1]]
				WhichList[ParentXval, ParentYval] = ONCLOSEDLIST
				NumberOfOpenListItems:-1
				OpenList[1] = OpenList[NumberOfOpenListItems + 1]
				Local v:Int = 1	
				Repeat
					Local u:Int = v
					If 2 * u + 1 <= NumberOfOpenListItems
						If FCost[OpenList[u]] >= FCost[OpenList[2 * u]] Then v = 2 * u
						If FCost[OpenList[v]] >= FCost[OpenList[2 * u + 1]] Then v = 2 * u + 1
					Else
						If 2 * u <= NumberOfOpenListItems
							If FCost[OpenList[u]] >= FCost[OpenList[2 * u]] Then v = 2 * u
						End If	
					End If
					If u <> v
						Local temp:Int = OpenList[u]
						OpenList[u] = OpenList[v]
						OpenList[v] = temp
					Else
						Exit
					End If
				Forever
' 7 -------------------------------------------------------------------	
				For Local b:Int = ParentYval - 1 To ParentYval + 1		
				For Local a:Int = ParentXval - 1 To ParentXval + 1
					If Not EightWay
						If a = ParentXval + 1 And b = ParentYval + 1 Then Continue
						If a = ParentXval - 1 And b = ParentYval - 1 Then Continue
						If a = ParentXval + 1 And b = ParentYval - 1 Then Continue
						If a = ParentXval - 1 And b = ParentYval + 1 Then Continue
					EndIf
					
					If a <> - 1 And b <> - 1 And a <> MapWidth And b <> MapHeight
						If whichList[a, b] <> ONCLOSEDLIST
							If walkability[a, b] <> unwalkable
								
								Local Corner:Int = WALKABLE
								If CutCorners = False
									If a = ParentXval - 1
										If b = ParentYval - 1
											If Walkability[ParentXval - 1, ParentYval] = UNWALKABLE Or Walkability[ParentXval, ParentYval - 1] = UNWALKABLE Then Corner = UNWALKABLE
										Else If b = ParentYval + 1
											If Walkability[ParentXval, ParentYval + 1] = UNWALKABLE Or Walkability[ParentXval - 1, ParentYval] = UNWALKABLE Then Corner = UNWALKABLE
										End If
									Else If a = ParentXval + 1
										If b = ParentYval - 1
											If Walkability[ParentXval, ParentYval - 1] = UNWALKABLE Or Walkability[ParentXval + 1, ParentYval] = UNWALKABLE Then Corner = UNWALKABLE
										Else If b = ParentYval + 1
											If Walkability[ParentXval + 1, ParentYval] = UNWALKABLE Or Walkability[ParentXval, ParentYval + 1] = UNWALKABLE Then Corner = UNWALKABLE
										End If
									End If
								EndIf
								
								If Corner = WALKABLE
									If whichList[a, b] <> ONOPENLIST
										NewOpenListItemID:+1
										Local m:Int = NumberOfOpenListItems + 1
										OpenList[m] = NewOpenListItemID
										OpenX[NewOpenListItemID] = a
										openY[NewOpenListItemID] = b
										
										If Abs(a - ParentXval) = 1 And Abs(b - ParentYval) = 1
											Gcost[a, b] = Gcost[ParentXval, ParentYval] + 14
										Else	
											Gcost[a, b] = Gcost[ParentXval, ParentYval] + 10
										End If
								
										HCost[OpenList[m]] = EstimateHcost(a, b)
										FCost[OpenList[m]] = GCost[a, b] + HCost[openList[m]]
										ParentX[a, b] = ParentXval
										ParentY[a, b] = ParentYval
		
										While m <> 1
											If FCost[openList[m]] <= FCost[OpenList[m / 2]]
												Local temp:Int = OpenList[m / 2]
												OpenList[m / 2] = OpenList[m]
												OpenList[m] = temp
												m = m / 2
											Else
												Exit
											End If
										Wend
										NumberOfOpenListItems:+1
										WhichList[a, b] = ONOPENLIST
' 8 -------------------------------------------------------------------										
									Else 'whichList[a, b] <> ONOPENLIST
										Local tempGcost:Int
										If Abs(a - ParentXval) = 1 And Abs(b - ParentYval) = 1
											tempGcost = Gcost[ParentXval, ParentYval] + 14
										Else	
											tempGcost = Gcost[ParentXval, ParentYval] + 10
										End If
										If tempGcost < GCost[a, b]
											ParentX[a, b] = ParentXval
											ParentY[a, b] = ParentYval
											GCost[a, b] = tempGcost
										
											For Local x:Int = 1 To NumberOfOpenListItems
												If OpenX[OpenList[x]] = a And OpenY[OpenList[x]] = b
													FCost[OpenList[x]] = Gcost[a, b] + HCost[OpenList[x]]
													Local m:Int = x
													While m <> 1
														If FCost[OpenList[m]] < FCost[OpenList[m / 2]] Then
															Local temp:Int = OpenList[m / 2]
															OpenList[m / 2] = openList[m]
															OpenList[m] = temp
															m = m/2
														Else
															Exit 'while/wend
														End If
													Wend
													Exit 'for x = loop
												End If 'If openX(openList(x)) = a
											Next 'For x = 1 To numberOfOpenListItems	
										End If 'If tempGcost < Gcost(a,b)		
									End If ' If not already on the open list
								End If ' If corner = walkable
							End If ' If not a wall/obstacle cell
						End If 'If not already on the closed list
					End If 'If not off the map.
				Next
				Next
' 9 -------------------------------------------------------------------				
			Else  'NumberOfOpenListItems / If open list is empty then there is no path.
				Path = NONEXISTENT
				Exit
			EndIf 'NumberOfOpenListItems
			
			If WhichList[TargetX, TargetY] = ONOPENLIST
				Path = FOUND
				Exit
			EndIf

		Forever
' 10 -------------------------------------------------------------------			
		If Path = FOUND
			Local pathX:Int = TargetX
			Local pathY:Int = TargetY
			Steps.AddFirst(String(pathX + (pathY * MapWidth)))
			Repeat
				Local tempx:Int = ParentX[pathX, pathY]
				pathY = ParentY[pathX, pathY]
				pathX = tempx
				Steps.AddFirst(String(pathX + (pathY * MapWidth)))
			Until pathX = StartX And pathY = StartY
		End If
		
	End Method
	
	rem
	bbdoc:	Prepare to do a search, ALWAYS call before calling Search() Method
	end rem
	Method Prepare:Int(Arr:Int[,], UnWalkableValues:Int[], StartX:Int, StartY:Int, TargetX:Int, TargetY:Int)
		'Store start and target locations
		Self.StartX = StartX
		Self.StartY = StartY
		Self.TargetX = TargetX
		Self.TargetY = TargetY
		'Initalise arrays
		MapWidth = arr.Dimensions()[0]
		MapHeight = arr.Dimensions()[1]
		Walkability = New Int[MapWidth, MapHeight]
		WhichList = New Int[MapWidth, MapHeight]
		ParentX = New Int[MapWidth, MapHeight]
		ParentY = New Int[MapWidth, MapHeight]
		GCost = New Int[MapWidth, MapHeight]
		OpenList = New Int[MapWidth * MapHeight + 2]
		OpenX = New Int[MapWidth * MapHeight + 2]
		OpenY = New Int[MapWidth * MapHeight + 2]
		FCost = New Int[MapWidth * MapHeight + 2]
		HCost = New Int[MapWidth * MapHeight + 2]
		'Reset Fields
		NumberOfOpenListItems = 0
		NewOpenListItemID = 0
		Steps = New TList
		'Make array to check walkable squares
		For Local x:Int = 0 Until MapWidth
			For Local y:Int = 0 Until MapHeight
					For Local a:Int = 0 Until UnWalkableValues.Length
						If Arr[x, y] = UnWalkableValues[a] Then Walkability[x, y] = UNWALKABLE
					Next
			Next
		Next	
	End Method

	rem
	bbdoc:	Return Estimated cost (H)euristic (used in search Method), three methods available, default is Manhatten
	end rem
	Method EstimateHcost:Int(a:Int, b:Int, Formula:Int = 0)
		Local h:Int	
		Select Formula
			Case 0 'Manhattan (dx+dy) (DEFAULT)	
				h = 10 * (Abs(a - TargetX) + Abs(b - TargetY))
			Case 1 'Diagonal Shortcut Estimation Method (NOT USED BY DEFAULT)
				Local xDistance:Int = Abs(a - TargetX)
				Local yDistance:Int = Abs(b - TargetY)
				If xDistance > yDistance Then 
					h = 14 * yDistance + 10 * (xDistance - yDistance)
				Else
					h = 14 * xDistance + 10 * (yDistance - xDistance)
				End If
			Case 2 'Max(dx,dy) (NOT USED BY DEFAULT)
				Local xDistance:Int = Abs(a - TargetX)
				Local yDistance:Int = Abs(b - TargetY)
				If xDistance > yDistance Then 
					H = 10*xDistance
				Else
					H = 10*yDistance
				End If
		End Select			
		Return h
	End Method
	
	rem
	bbdoc:	DEBUG - draw path results (wouldn't typicaly use this, but useful for testing/debugging)
	end rem
	Method Render:Int(TileSize:Int, JustShowPath:Int = True)
		If Path <> FOUND Return 0
		
		If Not JustShowPath
			'List type, green=openlist, red=closedlist
			For Local x:Int = 0 Until MapWidth
				For Local y:Int = 0 Until MapHeight
					If WhichList[x, y] = ONOPENLIST
						SetColor 106, 193, 1
					ElseIf WhichList[x, y] = ONCLOSEDLIST
						SetColor 195, 15, 1
					Else
						Continue
					EndIf
					If (x = StartX And y = StartY) Or (x = TargetX And y = TargetY)
						SetColor 42, 155, 196
						DrawRect((x * TileSize), (y * TileSize), TileSize, TileSize)
					Else
						DrawRect((x * TileSize), (y * TileSize), TileSize, TileSize)
					End If
					
				Next
			Next
			
			'Parent pointers
			SetColor 0, 0, 0
			For Local x:Int = 0 Until MapWidth
				For Local y:Int = 0 Until MapHeight
					If WhichList[x, y] = 0 Continue
					If x = StartX And y = StartY Continue
					Local index:Int = (x + (y * MapWidth))
					Local val:Int = (ParentX[x, y] + (ParentY[x, y] * MapWidth))
					Local angle:Int
					Select val - index
						Case 1 ;angle = 90
						Case - 1;angle = 270
						Case MapWidth;angle = 180
						Case - MapWidth;angle = 0
						Case MapWidth + 1;angle = 135
						Case MapWidth - 1;angle = 225
						Case - MapWidth - 1;angle = 315
						Case - MapWidth + 1;angle = 45
					End Select
					DrawOval(((x + 0.5) * TileSize) - ((TileSize / 5) / 2),  ..
					((y + 0.5) * TileSize) - ((TileSize / 5) / 2),  ..
					(TileSize / 5), (TileSize / 5))	
					DrawLine((x + 0.5) * TileSize, (y + 0.5) * TileSize,  ..
					((x + 0.5) * TileSize) + (Sin(180 - angle) * (TileSize / 3)),  ..
					((y + 0.5) * TileSize) + (Cos(180-angle) * (TileSize/3)))
				Next
			Next
			
			'Costs
			SetColor 0, 0, 0
			For Local x:Int = 0 Until MapWidth
				For Local y:Int = 0 Until MapHeight
					If WhichList[x, y] = 0 Continue
					If x = StartX And y = StartY Continue
					DrawText(GCost[x, y] + EstimateHcost(x, y), (x * TileSize) + 2, (y * TileSize) + 2)
					DrawText(GCost[x, y], (x * TileSize) + 2, ((y + 1) * TileSize) - TextHeight(""))
					DrawText(EstimateHcost(x, y), ((x + 1) * TileSize) - TextWidth(String EstimateHcost(x, y)) - 4, ((y + 1) * TileSize) - TextHeight(""))
				Next
			Next
			SetColor 255, 255, 255
		EndIf
		
		'Path
		If Path <> FOUND Then Return 0
		Local b:Int[] = GetPathRef()
		For Local x:Int = 0 Until MapWidth
			For Local y:Int = 0 Until MapHeight
				For Local a:Int = 0 Until b.Length
					If b[a] = (x + (y * MapWidth))
						DrawOval(((x + 0.5) * TileSize)-(TileSize / 8), ((y + 0.5) * TileSize)-(TileSize / 8), (TileSize / 4), (TileSize / 4))
					Local angle:Int
					Local lineLen:Int = TileSize
					If a = b.Length - 1 Continue
					Select b[a + 1] - b[a]
						Case 1 ;angle = 90
						Case - 1;angle = 270
						Case MapWidth;angle = 180
						Case - MapWidth;angle = 0
						Case MapWidth + 1;angle = 135 ; lineLen:+(TileSize / 3)
						Case MapWidth - 1;angle = 225 ; lineLen:+(TileSize / 3)
						Case - MapWidth - 1;angle = 315; lineLen:+(TileSize / 3)
						Case - MapWidth + 1;angle = 45 ; lineLen:+(TileSize / 3)
					End Select
					DrawLine((x + 0.5) * TileSize, (y + 0.5) * TileSize,  ..
					((x + 0.5) * TileSize) + (Sin(180 - angle) * (lineLen)),  ..
					((y + 0.5) * TileSize) + (Cos(180 - angle) * (lineLen)))
					EndIf
				Next
			Next
		Next
	End Method
	
	rem
	bbdoc:	Return a FOUND path as INT[] Array (Start --> Target) representing directions CLOCKWISE; (1)=up, (2)=up&right, (3)=right, (4)=down&right .. etc .. 
	end rem
	Method GetPathInt:Int[] ()
		If Path <> FOUND Return[- 1]
		Local b:Int[Steps.Count() - 1]
		For Local i:Int = 0 Until b.Length
			Local a:Int = Int(String Steps.ValueAtIndex(i))
			Local c:Int = Int(String Steps.ValueAtIndex(i + 1))
			Select a - c
				Case 1 ;b[i] = 7
				Case - 1;b[i] = 3
				Case MapWidth;b[i] = 1
				Case - MapWidth;b[i] = 5
				Case MapWidth + 1;b[i] = 8
				Case MapWidth - 1;b[i] = 2
				Case - MapWidth - 1; b[i] = 4
				Case - MapWidth + 1;b[i] = 6
			End Select
		Next
		Return b
	End Method
	
	rem
	bbdoc:	Return a FOUND path as STRING[] Array (Start --> Target) representing compass directions CLOCKWISE; "N", "NE", "E", "SE", "S", "SW", "W", "NW" 
	end rem
	Method GetPathStr:String[] ()
		If Path <> FOUND Return["NO PATH"]
		Local a:Int[] = GetPathInt()
		Local b:String[a.Length]
		For Local i:Int = 0 Until a.Length
			Select a[i]
				Case 1;b[i] = "N"
				Case 2;b[i] = "NE"
				Case 3;b[i] = "E"
				Case 4;b[i] = "SE"
				Case 5;b[i] = "S"
				Case 6;b[i] = "SW"
				Case 7;b[i] = "W"
				Case 8;b[i] = "NW"
			End Select
		Next
		Return b
	End Method
	
	rem
	bbdoc:	Return a FOUND path as Array (x,y) Tile reference values (probably not that useful)
	end rem
	Method GetPathRef:Int[] ()
		If Path <> FOUND Return[- 1]
		Local b:Int[GetPathInt().Length + 1]
		Local pathX:Int = TargetX
		Local pathY:Int = TargetY
		Local i:Int = b.Length - 1
		b[i] = (TargetX + (TargetY * MapWidth)) ; i:-1
		Repeat
			Local tempx:Int = ParentX[pathX, pathY]
			pathY = ParentY[pathX, pathY]
			pathX = tempx
			b[i] = (pathX + (pathY * MapWidth))
			i:-1
		Until pathX = StartX And pathY = StartY
		Return b
	End Method
	
	rem
	bbdoc:	Return how many steps are in the FOUND path
	end rem
	Method GetPathLength:Int()
		If Path <> FOUND Return - 1
		Return Steps.Count()
	End Method
	
	rem
	bbdoc:	Return the length (in pixels) of the FOUND path (going through the center of each tile)
	end rem
	Method GetPathDistance:Int(DiagonalCost:Int = 14, VertHorzCost:Int = 10)
		If Path <> FOUND Return - 1
		Local a:Int[] = GetPathInt()
		Local cost:Int
		For Local i:Int = 0 Until a.Length
			Select a[i]
				Case 1;cost:+VertHorzCost
				Case 2;cost:+DiagonalCost
				Case 3;cost:+VertHorzCost
				Case 4;cost:+DiagonalCost
				Case 5;cost:+VertHorzCost
				Case 6;cost:+DiagonalCost
				Case 7;cost:+VertHorzCost
				Case 8;cost:+DiagonalCost
			End Select
		Next
		Return cost
	End Method
	
	rem
	bbdoc:	Return True if a path has been found after the last Search() call
	end rem
	Method PathFound:Int()
		If Path = FOUND Then Return 1
	End Method
	
End Type
