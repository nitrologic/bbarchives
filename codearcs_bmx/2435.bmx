; ID: 2435
; Author: Jesse
; Date: 2009-03-16 16:32:16
; Title: *Astar path finder group
; Description: find path in group

name "AstarLibrary.bmx"
[code]
'	Declare constants
'pathStatus Constants
Const NOTFINISHED			:Int = 0,..
	 NOTSTARTED			:Int = 0,..
	 FOUND				:Int = 1 
Const NONEXISTENT			:Int = 2,..
	 TEMPSTOPPED			:Int = 3
'walkability array Constants	 
Const WALKABLE 			:Int = 0,..
	 UNWALKABLE 			:Int = 1

'FindPath() mode constants
Const NORMAL				:Int = 0,..
	 RANDOMMOVE			:Int = 1

Type TAstar
	
	Field tileSize			:Int,..
		 tileWidth		:Int,..
		 tileHeight		:Int,..
		 mapWidth			:Int,..
		 mapHeight		:Int

	'Create needed arrays
	Field walkability		:Int[,],..				'array that holds wall/obstacle information	
		 openList			:Int[ ],..				'1 Globalensional array holding ID# of open list items
		 whichList		:Int[,]					'2 Globalensional array used To record 
	Field openX			:Int[ ],..				'1d array stores the x location of an item on the open list
		 openY			:Int[ ],..				'1d array stores the y location of an item on the open list
		 parentX			:Int[,],..				'2d array To store parent of each cell (x)
		 parentY			:Int[,],..				'2d array To store parent of each cell (y)
		 Fcost			:Int[ ],..				'1d array To store F cost of a cell on the open list
		 Gcost			:Int[,],..				'2d array To store G cost For each cell.
		 Hcost			:Int[ ],..				'1d array To store H cost of a cell on the open list		
		 tempUnwalkability	:Int[,],..				'array that holds info about adjacent units
		 nearByPath		:Int[,],.. 		
		 claimedNode		:TUnit[,],..				' array that stores claimed nodes	
		 gGroupUnit		:TUnit[],.. 				'used To sort a selected group of units		
		 island			:Int[,],.. 
		 gDiagonalBlockage	:Int,..
		 gPathCost		:Int,..
		 unitList			:TList,.. 	
		 onClosedList		:Int,..
		 useDijkstras		:Int
	
	Method New ()
	End Method

	Method Init(tw:Int, th:Int, mw:Int, mh:Int)

		tileWidth			= tw
		tileHeight		= th
		tileSize			= TileWidth
		mapWidth			= mw
		mapHeight			= mh
		walkability 		= New Int[mapWidth,mapHeight] 			
		openList 			= New Int[mapWidth*mapHeight] 		
		whichList 		= New Int[mapWidth,mapHeight]		 
		openX			= New Int[mapWidth*mapHeight]		
		openY			= New Int[mapWidth*mapHeight]		
		parentX			= New Int[mapWidth,mapHeight]		
		parentY			= New Int[mapWidth,mapHeight]		
		Fcost			= New Int[mapWidth*mapHeight]		
		Gcost			= New Int[mapWidth,mapHeight]		
		Hcost			= New Int[mapWidth*mapHeight]				
		tempUnwalkability 	= New Int[mapWidth,mapHeight] 	
		nearByPath		= New Int[mapWidth,mapHeight] 		
		claimedNode		= New TUnit[mapWidth,mapHeight]	
		gGroupUnit		= New TUnit[1] 		
		island			= New Int[mapWidth,mapHeight] 
		unitList			= New TList	
		onClosedList		= 10 	
		useDijkstras 		= False

	End Method
	
	Method FindPath:Int(unit:TUnit,targetX:Int,targetY:Int,mode:Int=normal)
	
		'Random move mode is used when a unit's main target is
		'currently unreachable. After the random move, the unit
		'will Try To pathfind To its original target again. Random
		'moves can break up units that happen To be approaching
		'each other from opposite directions down tight corridors.
		'See the UpdatePath() method.
		If mode = randomMove Then useDijkstras = True	
	
	'1.	Convert location data (in pixels) To coordinates in the walkability array.
		Local startX:Int = Floor(unit.xLoc/tileWidth) ; Local startY:Int = Floor(unit.yLoc/tileHeight)	
		targetX:Int = Floor(targetX/tileWidth) ; targetY:Int = Floor(targetY/tileHeight)
	
	'2.	Check For redirects	
		Local result:Int = CheckRedirect(unit,targetX,targetY)	
		If result <> failed Then 'Goto noPath 'target is unwalkable And could Not find a redirect.
				If result = succeeded Then targetX = gInt1 ; targetY = gInt2		
			
				'If starting location And target are in the same location...
				If startX = targetX And startY = targetY 
					unit.pathLength = 0 ; unit.pathLocation = 0	
					PokeShort unit.pathBank,0,startX 'store starting x value	
					PokeShort unit.pathBank,2,startY 'store starting y value		
					Return found
				End If
			
			'3.	a. Reset some variables that need To be cleared
				If onClosedList > 1000000 'occasionally reGlobal whichList
					whichList = New Int[mapWidth,mapHeight] ; onClosedList = 10
				End If	
				onClosedList = onClosedList+5 'changing the values of onOpenList And onClosed list is faster than reGlobalming whichList[) array
				Local onOpenList:Int = onClosedList-1
				Local tempUnwalkable:Int = onClosedList-2
				Local penalized:Int = onClosedList-3 
				unit.pathLength = notstarted 'i.e, = 0
				unit.pathLocation = notstarted 'i.e, = 0
				Gcost[startX,startY] = 0 'reset starting square's G value to 0
			
				'b. Create a footprint For any nearby unit that the pathfinding unit 
				'may be about To collide with. Such nodes are designated as tempUnwalkable.	
				CreateFootPrints(unit:TUnit)
			
			'4.	Add the starting location To the open list of squares To be checked.
				Local numberOfOpenListItems:Int = 1
				openList[1] = 1 'assign it as the top (And currently only) item in the open list, which is maintained as a binary heap (explained below)
				openX[1] = startX ; openY[1] = startY
			
			'5.	Do the following Until a path is found Or deemed nonexistent.
				Local u:Int,newOpenListItemID:Int
				Local path:Int,parentXval:Int,parentYVal:Int
				Repeat
				
			'6.	If the open list is Not empty, take the first cell off of the list.
				'This is the lowest F cost cell on the open list.
				If numberOfOpenListItems <> 0 Then
			
				'Pop the first item off the open list.
				parentXval:Int = openX[openList[1]]; parentYVal:Int = openY[openList[1]] 'record cell coordinates of the item
				whichList[parentXval,parentYVal] = onClosedList 'add the item To the closed list
			
				'Open List = Binary Heap; Delete this item from the open list, which
				'is maintained as a binary heap. For more information on binary heaps, see;
				'http;//www.policyalmanac.org/games/binaryHeaps.htm
				numberOfOpenListItems = numberOfOpenListItems - 1 'reduce number of open list items by 1	
				openList[1] = openList[numberOfOpenListItems+1] 'move the last item in the heap up To slot #1
				Local v:Int = 1,addedGCost:Int	
				Repeat 'Repeat the following Until the New item in slot #1 sinks To its proper spot in the heap.
					u = v	
					If 2*u+1 <= numberOfOpenListItems 'If both children exist
					 	'Check If the F cost of the parent is greater than each child.
						'Select the lowest of the two children.	
						If Fcost[openList[u]] >= Fcost[openList[2*u]] Then v = 2*u
						If Fcost[openList[v]] >= Fcost[openList[2*u+1]] Then v = 2*u+1		
					Else
						If 2*u <= numberOfOpenListItems 'If only child #1 exists
						 	'Check If the F cost of the parent is greater than child #1	
							If Fcost[openList[u]] >= Fcost[openList[2*u]] Then v = 2*u
						End If	
					End If
					If u<>v 'If parent's F is > one of its children, swap them
						Local temp:Int = openList[u]
						openList[u] = openList[v]
						openList[v] = temp				
					Else
						Exit 'otherwise, Exit loop
					End If	
				Forever
			
				
			'7.	Check the adjacent squares. (Its "children" -- these path children
				'are similar, conceptually, To the binary heap children mentioned
				'above, but don't confuse them. They are different. Path children
				'are portrayed in Demo 1 with grey pointers pointing toward
				'their parents.] Add these adjacent child squares To the open list
				'For later consideration If appropriate (see various If statements
				'below].
				For Local b:Int = parentYVal-1 To parentYVal+1
				For Local a:Int = parentXval-1 To parentXval+1
			
				'If Not off the map (do this first To avoid array out-of-bounds errors)
				If a <> -1 And b <> -1 And a <> mapWidth And b <> mapHeight
			
				'If Not already on the closed list (items on the closed list have
				'already been considered And can now be ignored).			
				If whichList[a,b] <> onClosedList 
				
				'If Not a wall/obstacle square
				If walkability[a,b] <> unwalkable
				
				'If Not an adjacent node that is temporarily unwalkable
				'as defined by CreateFootprints()
				If tempUnwalkability[a,b] <> tempUnwalkable
				
				'If Not occupied by a stopped unit
				Local node:Int = unwalkable
				If claimedNode[a,b] = Null
					node = walkable
				Else If claimedNode[a,b].pathStatus <> stopped 
					node = walkable	
				End If
				If node = walkable
						
				'Don't cut across corners (this is optional)
				Local corner:Int = walkable	
				If a = parentXVal-1 
					If b = parentYVal-1 
						If walkability[parentXval-1,parentYval] = unwalkable Then corner = unwalkable
						If walkability[parentXval,parentYval-1] = unwalkable Then corner = unwalkable						
					Else If b = parentYVal+1 
						If walkability[parentXval,parentYval+1] = unwalkable Then corner = unwalkable 
						If walkability[parentXval-1,parentYval] = unwalkable Then corner = unwalkable 							
					End If
				Else If a = parentXVal+1 
					If b = parentYVal-1 
						If walkability[parentXval,parentYval-1] = unwalkable Then corner = unwalkable 
						If walkability[parentXval+1,parentYval] = unwalkable Then corner = unwalkable 							
					Else If b = parentYVal+1 
						If walkability[parentXval+1,parentYval] = unwalkable Then corner = unwalkable 
						If walkability[parentXval,parentYval+1] = unwalkable Then corner = unwalkable 			
					End If
				End If			
				If corner = walkable
				
				'If Not already on the open list, add it To the open list.			
				If whichList[a,b] <> onOpenList	
			
					'Create a New open list item in the binary heap.
					newOpenListItemID = newOpenListItemID + 1' each New item has a unique ID #
					Local m:Int = numberOfOpenListItems+1
					openList[m] = newOpenListItemID	 'place the New open list item (actually, its ID#) at the bottom of the heap
					openX[newOpenListItemID] = a ; openY[newOpenListItemID] = b 'record the x And y coordinates of the New item
			
					'Figure out its base G cost
					If Abs(a-parentXval) = 1 And Abs(b-parentYVal) = 1 Then
						addedGCost = 14 'cost of going To diagonal squares	
					Else	
						addedGCost = 10 'cost of going To non-diagonal squares				
					End If
					Gcost[a,b] = Gcost[parentXval,parentYVal]+addedGCost
					
					'If the node lies along the path of a nearby unit, add a penalty G cost.		
					If nearByPath[a,b] = penalized
						Gcost[a,b] = Gcost[a,b]+20
					Else If a<>parentXval And b<>parentYval
						If nearByPath[a,parentYval] = penalized
							If nearByPath[parentXval,b] = penalized
								Gcost[a,b] = Gcost[a,b]+28
							End If
						End If	
					End If		
						
					'Figure out its H And F costs And parent
					If useDijkstras = False
						Hcost[openList[m]] = 10*(Abs(a - targetx) + Abs(b - targety)) ' record the H cost of the New square
					Else
						Hcost[openList[m]] = 0	
					End If		
					Fcost[openList[m]] = Gcost[a,b] + Hcost[openList[m]] 'record the F cost of the New square
					parentX[a,b] = parentXval ; parentY[a,b] = parentYVal	'record the parent of the New square	
					
					'Move the New open list item To the proper place in the binary heap.
					'Starting at the bottom, successively compare To parent items,
					'swapping as needed Until the item finds its place in the heap
					'Or bubbles all the way To the top (If it has the lowest F cost).
					While m <> 1 'While item hasn't bubbled to the top (m=1)	
						'Check If child's F cost is < parent's F cost. If so, swap them.	
						If Fcost[openList[m]] <= Fcost[openList[m/2]] Then
							Local temp:Int = openList[m/2]
							openList[m/2] = openList[m]
							openList[m] = temp
							m = m/2
						Else
							Exit
						End If
					Wend 
					numberOfOpenListItems = numberOfOpenListItems+1 'add one To the number of items in the heap
			
					'Change whichList To show that the New item is on the open list.
					whichList[a,b] = onOpenList
			
			
			'8.	If adjacent cell is already on the open list, check To see If this 
				'path To that cell from the starting location is a better one. 
				'If so, change the parent of the cell And its G And F costs.	
				Else' If whichList[a,b) = onOpenList
				
					'Figure out the base G cost of this possible New path
					If Abs(a-parentXval) = 1 And Abs(b-parentYVal) = 1 Then
						addedGCost = 14'cost of going To diagonal tiles	
					Else	
						addedGCost = 10 'cost of going To non-diagonal tiles				
					End If
					Local tempGcost:Int = Gcost[parentXval,parentYVal]+addedGCost
					
					'If the node lies along the path of a nearby unit, add a penalty G cost.			
					If nearByPath[a,b] = penalized
						tempGcost  = tempGcost+20
					Else If a<>parentXval And b<>parentYval
						If nearByPath[a,parentYval] = penalized
							If nearByPath[parentXval,b] = penalized
								tempGcost  = tempGcost+28
							End If
						End If	
					End If				
					
					'If this path is shorter (G cost is Lower) Then change
					'the parent cell, G cost And F cost. 		
					If tempGcost < Gcost[a,b] Then 	'If G cost is less,
						parentX[a,b] = parentXval 	'change the square's parent
						parentY[a,b] = parentYVal
						Gcost[a,b] = tempGcost 	'change the G cost			
			
						'Because changing the G cost also changes the F cost, If
						'the item is on the open list we need To change the item's
						'recorded F cost And its position on the open list To make
						'sure that we maintain a properly ordered open list.
						For Local x:Int = 1 To numberOfOpenListItems 'look For the item in the heap
						If openX[openList[x]] = a And openY[openList[x]] = b Then 'item found
							Fcost[openList[x]] = Gcost[a,b] + Hcost[openList[x]] 'change the F cost
							
							'See If changing the F score bubbles the item up from it's current location in the heap
							Local m:Int = x
							While m <> 1 'While item hasn't bubbled to the top (m=1)	
								'Check If child is < parent. If so, swap them.	
								If Fcost[openList[m]] < Fcost[openList[m/2]] Then
									Local temp:Int = openList[m/2]
									openList[m/2] = openList[m]
									openList[m] = temp
									m = m/2
								Else
									Exit 'While/Wend
								End If
							Wend 
							
							Exit 'For x = loop
						End If 'If openX[openList[x]] = a
						Next 'For x = 1 To numberOfOpenListItems
			
					End If 'If tempGcost < Gcost[a,b] Then			
			
				End If 'If Not already on the open list				
				End If 'If corner = walkable
				End If 'If Not occupied by a stopped unit
				End If 'If Not an adjacent, temporarily unwalkable node
				End If 'If Not a wall/obstacle cell.	
				End If 'If Not already on the closed list	
				End If 'If Not off the map.	
				Next
				Next
			
			'9.	If open list is empty Then there is no path.	
				Else
					path = nonExistent ; Exit
				End If
			
			'10.	Check To see If desired target has been found.
				If mode = normal  'Exit when target is added To open list.
					If whichList[targetX,targetY] = onOpenList Then path = found ; Exit	
				Else If mode = randomMove 
					If Gcost[parentXval,parentYVal] > 20 + Rand(20)
						targetX = parentXval ; targetY = parentYval 
						path = found ; Exit		
					End If		
				End If		
			
				Forever 'Repeat Until path is found Or deemed nonexistent
				
				
			'11.	Save the path If it exists. Copy it To a bank. 
				If path = found
					
					'a. Working backwards from the target To the starting location by checking
					'each cell's parent, figure out the length of the path.
					Local pathX:Int = targetX ; Local pathY:Int = targetY	
					Repeat
						Local tempx:Int = parentX[pathX,pathY]		
						pathY = parentY[pathX,pathY]
						pathX = tempx
						unit.pathLength = unit.pathLength + 1	
					Until pathX = startX And pathY = startY
				
					'b. Resize the data bank To the Right size (leave room To store Step 0,
					'which requires storing one more Step than the length)
					ResizeBank unit.pathBank,(unit.pathLength+1)*4
			
					'c. Now copy the path information over To the databank. Since we are
					'working backwards from the target To the start location, we copy
					'the information To the data bank in reverse order. The result is
					'a properly ordered set of path data, from the first Step To the
					'last.	
					pathX:Int = targetX ; pathY:Int = targetY				
					Local cellPosition:Int = unit.pathLength*4 'start at the End	
					While Not (pathX = startX And pathY = startY)			
						PokeShort unit.pathBank,cellPosition,pathX 'store x value	
						PokeShort unit.pathBank,cellPosition+2,pathY 'store y value	
						cellPosition = cellPosition - 4 'work backwards		
						Local tempx:Int = parentX[pathX,pathY]		
						pathY = parentY[pathX,pathY]
						pathX = tempx
					Wend	
					PokeShort unit.pathBank,0,startX 'store starting x value	
					PokeShort unit.pathBank,2,startY 'store starting y value
			
					'Record the path's cost. This is used by ClaimNodes().
					gPathCost = Gcost[targetX,targetY]
			
				End If 'If path = found Then 
			
			'12. Return info on whether a path has been found.
				Return path' Returns 1 If a path has been found, 2 If no path exists. 
			
			'13.If there is no path To the selected target, set the pathfinder's
				'xPath And yPath equal To its current location And Return that the
				'path is nonexistent.
		End If '.noPath
		unit.xPath = 0' startingX ***********************************
		unit.yPath = 0' startingY ***********************************
		Return nonexistent
	
	End Method
		
	
	'==========================================================
	'READ PATH DATA; These methods read the path data And convert
	'it To screen pixel coordinates.
	Method ReadPath(unit:TUnit)			
		unit.xPath = ReadPathX(unit:TUnit,unit.pathLocation)
		unit.yPath = ReadPathY(unit:TUnit,unit.pathLocation)
	End Method
	
	Method ReadPathX#(unit:TUnit,pathLocation:Int)
		If pathLocation <= unit.pathLength
			Local x:Int = PeekShort (unit.pathBank,pathLocation*4)
			Return tileWidth*x + .5*tileWidth 'align w/center of square	
		End If
	End Method	
	
	Method ReadPathY#(unit:TUnit,pathLocation:Int)
		If pathLocation <= unit.pathLength
			Local y:Int = PeekShort (unit.pathBank,pathLocation*4+2)
			Return tileHeight*y + .5*tileHeight 'align w/center of square		
		End If
	End Method
	
	
	'==========================================================
	'COLLISION/NODE CLAIMING methodS; These methods handle node claiming
	'And collision detection (which occurs when a unit tries To claim a node that
	'another unit has already claimed).
	
	'This method checks whether the unit is close enough To the Next
	'path node To advance To the Next one Or, If it is the last path Step,
	'To stop.
	Method CheckPathStepAdvance(unit:TUnit)	
		
		'If starting a New path ...
		If unit.pathLocation = 0			
			If unit.pathLength > 0 
				unit.pathLocation = unit.pathLocation+1
				ClaimNodes(unit:TUnit)
				ReadPath(unit) 'update xPath And yPath
			Else If unit.pathLength = 0			
				ReadPath(unit) 'update xPath And yPath
				If unit.xLoc = unit.xPath And unit.yLoc = unit.yPath
					unit.pathStatus = notstarted
					ClearNodes(unit:TUnit)							
				End If
			End If		
		
		'If reaching the Next path node.		
		Else If unit.xLoc = unit.xPath And unit.yLoc = unit.yPath
			If unit.pathLocation = unit.pathLength 
				unit.pathStatus = notstarted
				ClearNodes(unit:TUnit)	
			Else
				unit.pathLocation = unit.pathLocation + 1
				ClaimNodes(unit:TUnit)
				ReadPath(unit) 'update xPath And yPath		
			End If	
		End If
				
	End Method
	
	'This method claims nodes For a unit. It is called by CheckPathStepAdvance().
	Method ClaimNodes(unit:TUnit)
			
		'Clear previously claimed nodes And claim the node the unit is currently occupying.
		ClearNodes(unit:TUnit)
		
		'Check Next path node For a collision.
		unit.unitCollidingWith:TUnit = DetectCollision(unit:TUnit)	
		
		'If no collision is detected, claim the node And figure out
		'the distance To the node.
		If unit.unitCollidingWith = Null		
			Local x2:Int = PeekShort (unit.pathBank,unit.pathLocation*4)
			Local y2:Int = PeekShort (unit.pathBank,unit.pathLocation*4+2)	
			claimedNode[x2,y2] = unit	
			ReadPath(unit:TUnit) 'update xPath/yPath			
			unit.distanceToNextNode = GetDistance#(unit.xLoc,unit.yLoc,unit.xPath,unit.yPath)	
		
		'Otherwise, If a collision has been detected ...
		Else		
						
			'If node is occupied by a unit Not moving normally, repath.
			If unit.unitCollidingWith.pathStatus = stopped		
				unit.pathStatus = FindPath(unit:TUnit,unit.targetX,unit.targetY)					
	
			'If there is a pending collision between the two units, repath.
			Else If UnitOnOtherUnitPath(unit:TUnit,unit.unitCollidingWith)
				unit.pathStatus = FindPath(unit:TUnit,unit.targetX,unit.targetY)	
	
			'If the pending collision is Not head-on, repathing is optional. Check
			'To see If repathing produces a Short enough path, And If so, use it.
			'Otherwise, tempStop.
			Else If gDiagonalBlockage = False					
				Local pathLength:Int = unit.pathLength 'save current path stats
				Local pathLocation:Int = unit.pathLocation 'save current path stats	
				Local currentPathBank:TBank = unit.pathBank 'save current path stats
				Local currentPathCost:Int = RemainingPatHcost(unit)	
				If unit.pathBank = unit.pathBank1 'switch the pathBank
					unit.pathBank = unit.pathBank2
				Else
					unit.pathBank = unit.pathBank1
				End If
				unit.pathStatus = FindPath(unit:TUnit,unit.targetX,unit.targetY) 'check the path
				
				'Is resulting path nonexistent Or too Long? Then reset back To the
				'original path info saved above And tempStop. Otherwise, the path 
				'just generated will be used.
				If unit.pathStatus = nonexistent Or gPathCost > currentPathCost+35
					unit.pathLength = pathLength
					unit.pathLocation = pathLocation 	
					unit.pathBank = currentPathBank 
					unit.pathStatus = tempStopped									
				End If
				
			'If the pending collision is with a unit crossing diagonally Right in
			'front of the unit, Then tempStop.	This Globall variable is set by
			'the DetectCollision() method.	
			Else If gDiagonalBlockage = True	
				unit.pathStatus = tempStopped											
			End If
					
		End If
			
	End Method
	
	'This method calculates the remaining cost of the current path. This
	'is used by the ClaimNodes() method To compare the unit's current
	'path To a possible New path To determine which is better.
	Method RemainingPatHcost:Int(unit:TUnit)
		Local lastX:Int = Floor(unit.xLoc/tileWidth)
		Local lastY:Int = Floor(unit.yLoc/tileHeight)
		Local pathCost:Int
		For Local pathLocation:Int  = unit.pathLocation To unit.pathLength
			Local currentX:Int = PeekShort (unit.pathBank,pathLocation*4)
			Local currentY:Int = PeekShort (unit.pathBank,pathLocation*4+2)		
			If lastX<>currentX And lastY<>currentY 
				pathCost = pathCost+14 'cost of going To diagonal squares	
			Else	
				pathCost = pathCost+10 'cost of going To non-diagonal squares				
			End If
			lastX = currentX ; lastY = currentY		
		Next
		Return pathCost
	End Method
	
	'This method clears a unit's claimed nodes. This method is
	'called principally by ClaimNodes() before New nodes are
	'claimed. It is also called by CheckPathStepAdvance() when the 
	'Final path node is reached And by LaunchProgram() To initialize
	'eachin unitList's initial location.
	Method ClearNodes(unit:TUnit)
		Local x:Int = Floor(unit.xLoc/tileWidth) ; Local y:Int = Floor(unit.yLoc/tileHeight)
		For Local a:Int = x-1 To x+1
		For Local b:Int = y-1 To y+1
			If a>=0 And a<mapWidth And b>=0 And b<mapHeight
				If claimedNode[a,b] = unit Then claimedNode[a,b] = Null
			End If		
		Next
		Next
		claimedNode[x,y] = unit 'reclaim the one the unit is currently occupying.						
	End Method
	
	'This method checks To see If the Next path Step is free. 
	'It is called from ClaimNodes() And by UpdatePath() when the
	'unit is tempStopped.
	Method DetectCollision:TUnit(unit:TUnit)
		gDiagonalBlockage = False	
		Local x2:Int = PeekShort(unit.pathBank,unit.pathLocation*4)
		Local y2:Int = PeekShort(unit.pathBank,unit.pathLocation*4+2)			
		If claimedNode[x2,y2] = Null	
			Local x1:Int = Floor(unit.xLoc/tileWidth)
			Local y1:Int = Floor(unit.yLoc/tileHeight)	
			If x1<>x2 And y1<>y2 'If Next path Step is diagonal
				If claimedNode[x1,y2] <> Null			
					If claimedNode[x1,y2] = claimedNode[x2,y1]	
						gDiagonalBlockage = True				
						Return claimedNode[x1,y2]
					End If	
				End If								
			End If
		Else
			Return claimedNode[x2,y2]	
		End If	
	End Method
	
	'This method is used by the FindPath() method To 
	'check whether the given target location is walkable.
	'If Not, it finds a New, nearby target location that is
	'walkable. The New coordinates are written To the
	'gInt1 And gInt2 Globall variables.
	Method CheckRedirect:Int(unit:TUnit, x:Int,y:Int)
		If NodeOccupied(unit:TUnit,x,y) = True
			For Local radius:Int = 1 To 10
				For Local option:Int = 1 To 4
					If option = 1 
						gInt1 = x ; gInt2 = y-radius
					Else If option = 2 
						gInt1 = x ; gInt2 = y+radius
					Else If option = 3 
						gInt1 = x-radius ; gInt2 = y
					Else If option = 4 
						gInt1 = x+radius ; gInt2 = y	
					End If			
					If gInt1 >= 0 And gInt1 < mapWidth And gInt2 >= 0 And gInt2 < mapHeight
						If NodeOccupied(unit:TUnit,gInt1,gInt2) = False
							If x = Floor(unit.targetX/tileWidth) And y=Floor(unit.targetY/tileHeight)
								unit.targetX = gInt1*tileWidth+.5*tileWidth 
								unit.targetY = gInt2*tileHeight+.5*tileHeight 
							End If	
							Return succeeded '1	
						End If
					End If
				Next
			Next
			Return failed 'unable To find redirect (returns -1).	
		End If
	End Method
	
	'This method is used by the CheckRedirect() method To 
	'determine whether a given node is walkable For a given unit.
	Method NodeOccupied:Int(unit:TUnit,x:Int,y:Int)
		If walkability[x,y] = unwalkable Then Return True
		If island[x,y] <> island[Floor(unit.xLoc/tileWidth),Floor(unit.yLoc/tileHeight)] Then Return True		
		If claimedNode[x,y] = Null Or claimedNode[x,y] = unit 'node is free
			Return False
		Else 'there is another unit there
			If claimedNode[x,y].pathStatus = found 'but If it is moving :..
				If claimedNode[x,y]<>unit.unitCollidingWith 'And unit is Not colliding with it
				 	Return False
				End If
			End If	
		End If
		Return True
	End Method	
	
	'This method is used by the FindPath() method To lay out
	''footprints' for other nearby units. A node within 1 node of
	'the pathfinding unit that is occupied by another unit is
	'treated as unwalkable. This method also lays out the
	'current paths of any units within two units of the pathfinding
	'unit. These nodes are Then penalized within the FindPath() 
	'method. This encourages paths that do Not overlap those 
	'of nearby units.
	Method CreateFootPrints(unit:TUnit)
		Local tempUnwalkable:Int = onClosedList-2
		Local penalized:Int = onClosedList-3	
		Local unitX:Int = Floor(unit.xLoc/tileWidth) ; Local unitY:Int = Floor(unit.yLoc/tileHeight)
		For Local a:Int = unitX-2 To unitX+2
		For Local b:Int = unitY-2 To unitY+2
			If a >= 0 And a < mapWidth And b>=0 And b < mapHeight
				If claimedNode[a,b] <> Null And claimedNode[a,b] <> unit
					Local otherUnit:TUnit = claimedNode[a,b]
					
					'Lay out penalized paths For units within 2 nodes of 
					'the pathfinding unit.
					For Local pathLocation:Int = otherunit.pathLocation-1 Until otherunit.pathLength
						If pathLocation>= 0	
							Local x:Int = PeekShort (otherunit.pathBank,pathLocation*4)	
							Local y:Int = PeekShort (otherunit.pathBank,pathLocation*4+2)
							nearByPath[x,y] = penalized
						End If	
					Next
					
					'Designate nodes occupied by units within 1 node 
					'as temporarily unwalkable.
					If Abs(a-unitX) <= 1 And Abs(b-unitY) <= 1 
						tempUnwalkability[a,b] = tempUnwalkable
					End If	
					
				End If							
			End If
		Next
		Next	
	End Method
	
	
	'This method identifies nodes on the map that are Not accessible from other areas
	'of the map ("islands"). It assumes that the map does Not change during the game.
	'If so, this method must be called again. It is Not a good idea To do this too often
	'during the game, especially If it is a large map, because the method is a little slow.
	'The island information is saved To an array called island[x,y).
	Method IdentifyIslands()
		island = New Int[mapWidth+1,mapHeight+1]
		Local areaID:Int ,onOpenList:Int,OpenListItems:Int
		Local squaresChecked:Int
		For Local startX:Int = 0 To mapWidth-1
		For Local startY:Int = 0 To mapHeight-1
			If walkability[startX,startY] = walkable And island[startX,startY] = 0	
				areaID =  areaID + 1		
				onClosedList = onClosedList+5 'changing the values of onOpenList And onClosed list is faster than reGlobalming whichList[] array
				onOpenList = onClosedList-1
				openListItems = 1 ; openList[1] = 1 ; openX[1] = startX ; openY[1] = startY
				Repeat
				Local parentXval:Int = openX[openList[1]] ; Local parentYVal:Int = openY[openList[1]]
				openList[1] = openList[openListItems] 'put last item in slot #1	
				openListItems = openListItems - 1 'reduce number of open list items by 1
				whichList[parentXval,parentYVal] = onClosedList 'add cell To closed list
				island[parentXval,parentYVal] = areaID 'Assign item To areaID
				For Local b:Int = parentYVal-1 To parentYVal+1
				For Local a:Int = parentXval-1 To parentXval+1
					If a <> -1 And b <> -1 And a <> mapWidth And b <> mapHeight
					If whichList[a,b] <> onClosedList And whichList[a,b] <> onOpenList	
					If walkability[a,b] <> unwalkable 'Not = walkable because could = occupied
					If (a=parentXVal Or b=parentYVal) 'If an orthogonal square of the Right Type(s)		
						squaresChecked = squaresChecked + 1
						Local m:Int = openListItems+1 'm = New item at End of heap					
						openList[m] = squaresChecked				
						openX[squaresChecked] = a ; openY[squaresChecked] = b		
						openListItems = openListItems+1 'add one To number of items on the open list
						whichList[a,b] = onOpenList
					End If 'If an orthogonal square of the Right Type(s)		
					End If 'If walkability[a,b) <> unwalkable
					End If 'If Not on the open Or closed lists
					End If 'If Not off the map.
				Next
				Next
				Until openListItems = 0				
			End If	
		Next
		Next	
	End Method
	
	
End Type


'This function checks To see whether a unit is on another unit's 
'path. It is called by ClaimNodes().
Function UnitOnOtherUnitPath:Int(unit:TUnit,otherUnit:TUnit)	
	Local unitX:Int = Floor(unit.xLoc/path.tileWidth)
	Local unitY:Int = Floor(unit.yLoc/path.tileHeight)		
	For Local pathLocation:Int = otherunit.pathLocation To otherunit.pathLength		
		If unitX = PeekShort (otherunit.pathBank,pathLocation*4)
			If unitY = PeekShort (otherunit.pathBank,pathLocation*4+2)		
				Return True
			End If
		End If
		If pathLocation > otherunit.pathLocation+1 Then Return False
	Next
End Function


'This method chooses separate destinations For each member of
'of a group of selected units. When we choose a destination
'For the group, we don't want them to all try to go that exact
'location. Instead we want them To go To separate locations close 
'To that group target location.
'	If the units are all close enough together, the method merely
'returns that eachin unitList should stay in the same place relative To one
'another. If the units are spread out, the method chooses a relative
'location For the unit.
Function ChooseGroupLocations(path:TAstar)
	Local totalX:Int,totalY:Int
	Local numberOfUnitsInGroup:Int
	'Figure out the group center
	For Local unit:TUnit = EachIn path.unitList
		If unit.selected = True
			totalX = totalX + unit.xLoc#
			totalY = totalY + unit.yLoc#	
			numberOfUnitsInGroup = numberOfUnitsInGroup+1
		End If	
	Next
	If numberOfUnitsInGroup = 0 Then Return
	Local groupCenterX# = totalX/numberOfUnitsInGroup
	Local groupCenterY# = totalY/numberOfUnitsInGroup	
	
	'Figure out If all of the units in the selected group are close enough To
	'each other To keep them more Or less in the same locations relative
	'To one another.
	Local unitOutsideMaxDistance:Int
	Local maxDistance:Int = path.tileSize*Sqr(numberOfUnitsInGroup)	
	For Local unit:TUnit = EachIn path.unitList
		If unit.selected = True
			unit.xDistanceFromGroupCenter# = unit.xLoc#-groupCenterX#
			unit.yDistanceFromGroupCenter# = unit.yLoc#-groupCenterY#
			If Abs(unit.xDistanceFromGroupCenter#) > maxDistance
				unitOutsideMaxDistance = True
			Else If Abs(unit.yDistanceFromGroupCenter#) > maxDistance
				unitOutsideMaxDistance = True			
			End If
		End If	
	Next
	
	'If they are all close enough together, we don't need to adjust their relative 
	'locations.
	If unitOutsideMaxDistance = False 
		'do nothing

	'If one Or more group members is too far away, we need To generate a New
	'set of relative locations For the group members.
	Else If numberOfUnitsInGroup = 2

		For Local unit:TUnit = EachIn path.unitList
			If unit.selected = True
				unit.actualAngleFromGroupCenter = 0			
				unit.assignedAngleFromGroupCenter = 0	
				unit.xDistanceFromGroupCenter# = Sgn(unit.xDistanceFromGroupCenter#)*path.tileWidth/2
				unit.yDistanceFromGroupCenter# = Sgn(unit.yDistanceFromGroupCenter#)*path.tileHeight/2	
			End If	
		Next	
				
	Else 'If 3+ units

		'Figure out the angles between eachin unitList in the group And the group center.
		'Also, save unit Type pointers To an array For sorting purposes	
		path.gGroupUnit = New TUnit[numberOfUnitsInGroup+1]			
		Local x:Int
		For Local unit:TUnit = EachIn path.unitList
			If unit.selected = True
				x = x+1
				path.gGroupUnit:TUnit[x] = unit
				unit.actualAngleFromGroupCenter = GetAngle(groupCenterX#,groupCenterY#,unit.xLoc#,unit.yLoc#)		
			End If	
		Next	
		
		'Sort the units in the group according To their angle, from lowest To highest 
		Local topItemNotSorted:Int = numberOfUnitsInGroup
		While topItemNotSorted <> 1
	
			'Find the highest value in the list
			Local highestValueItem:Int = 1
			For Local sortItem:Int = 1 To topItemNotSorted
				If path.gGroupUnit[sortItem].actualAngleFromGroupCenter >= path.gGroupUnit[highestValueItem].actualAngleFromGroupCenter 
					highestValueItem = sortItem
				End If
			Next
		
			'Now swap it with the highest item in the list
			Local temp:TUnit = path.gGroupUnit[topItemNotSorted]
			path.gGroupUnit[topItemNotSorted] = path.gGroupUnit[highestValueItem]
			path.gGroupUnit[highestValueItem] = temp
		
			topItemNotSorted = topItemNotSorted - 1			
		Wend
		
		'Now assign angles To each of the units in the group
		path.gGroupUnit[1].assignedAngleFromGroupCenter = path.gGroupUnit[1].actualAngleFromGroupCenter
		Local addAngle# = 360/numberOfUnitsInGroup		
		For x = 2 To numberOfUnitsInGroup
			path.gGroupUnit[x].assignedAngleFromGroupCenter = path.gGroupUnit[x-1].assignedAngleFromGroupCenter + addAngle
 			If path.gGroupUnit[x].assignedAngleFromGroupCenter >= 360
				path.gGroupUnit[x].assignedAngleFromGroupCenter = path.gGroupUnit[x].assignedAngleFromGroupCenter-360
			End If
		Next
	
		'Now assign the xDistanceFromGroupCenter And yDistanceFromGroupCenter
		If numberOfUnitsInGroup <= 6
			Local radius# = Sqr(numberOfUnitsInGroup)*0.8*path.tileSize
			For Local unit:TUnit = EachIn path.unitList
				If unit.selected = True
					unit.xDistanceFromGroupCenter# =  radius*Cos(unit.assignedAngleFromGroupCenter)+(unit.ID Mod(2))
					unit.yDistanceFromGroupCenter# = -radius*Sin(unit.assignedAngleFromGroupCenter)+(unit.ID Mod(2))	
				End If	
			Next	
		
		'If there are more than 6 units in the group, Create two rings of units.
		Else
			Local innerRadius# = Sqr(numberOfUnitsInGroup/2)*0.8*path.tileSize
			Local outerRadius# = 2.5*Sqr(numberOfUnitsInGroup/2)*0.8*path.tileSize
			x = 0		
			For Local unit:TUnit = EachIn path.unitList
				If unit.selected = True
					x = x+1
					If x Mod 2 = 0
						unit.xDistanceFromGroupCenter# =  innerRadius*Cos(unit.assignedAngleFromGroupCenter)
						unit.yDistanceFromGroupCenter# = -innerRadius*Sin(unit.assignedAngleFromGroupCenter)		
					Else
						unit.xDistanceFromGroupCenter# =  outerRadius*Cos(unit.assignedAngleFromGroupCenter)
						unit.yDistanceFromGroupCenter# = -outerRadius*Sin(unit.assignedAngleFromGroupCenter)					
					End If
				End If	
			Next					
		End If
			
	End If  'If group.numberOfUnitsInGroup = 2

	'Now that the relative locations have been determined, we use this info
	'To generate the units' destination locations.
	For Local unit:TUnit = EachIn path.unitList
		If unit.selected = True
			unit.targetX# = MouseX() + unit.xDistanceFromGroupCenter#
			unit.targetY# = MouseY() + unit.yDistanceFromGroupCenter#
			If unit.targetX < 0 Then unit.targetX = 0			
			If unit.targetX >= 800 Then unit.targetX = 799
			If unit.targetY < 0 Then unit.targetY = 0			
			If unit.targetY >= 600 Then unit.targetY = 599	
		End If	
	Next
		
End Function
[/code]

name "Shared functions.bmx" 


[code]
'Common Functions
'==================================================================
'This Include file contains functions that are used by several demos. It does Not contain
'any significant pathfinding-related code.

Global gInt1:Int, gInt2:Int
Const failed:Int = -1, succeeded:Int = 1
Global gScreenCaptureNumber:Int, gLoops:Int, gLoopTime:Float, gGameTime:Float

'Returns the angle between the first point And the second point.
'Zero degrees is at the 3 o'clock position. Angles proceed in a
'counterclockwise direction. For example, 90 degrees is at
'12 o'clock. 180 degrees is at 9 o'clock, and 270 degrees
'is at 6 o'clock.
'	Also, please note that this Function is using screen coordinates,
'where y increases in value as it goes down.
'	Note that the Blitz ATan2() Function returns -180 To 180 with
'zero being the 12 o'clock position if y increases as you move up
'the screen, And 6'oclock if y increases as you move down the screen.
'This functions adjusts For that.
Function GetAngle:Float(x1#,y1#,x2#,y2#)
	Local angle:Float = ATan2(x2-x1,y2-y1)
	If angle >= 90 And angle <= 180
		Return angle-90
	Else
		Return angle+270			
	End If
End Function

'Note: Blitz calculates squares very slowly For some reason,
'so it is much faster To multiply the values rather than using
'the shorthand "^2".
Function GetDistance#(x1#,y1#,x2#,y2#)
	Return Sqr( (x1#-x2#)*(x1#-x2#) + (y1#-y2#)*(y1#-y2#) )	
End Function

'This subroutine copies the map image into one large image For
'faster rendering. This isn't really necessary, and it doesn't effect
'pathfinding at all. It just makes map drawing faster because drawing
'one big image is a lot faster than separately drawing each of the wall
'images And grids. 
Function CopyMapImage()
	'FreeImage gMap
	gMap = CreateImage(800,600) 'Create a New map image.
	Cls
	For Local x:Int = 0 To 800/path.tileWidth-1
	For Local y:Int = 0 To 600/path.tileHeight-1
		If path.walkability[x,y] = unwalkable Then DrawImage(wallBlock,x*path.tileWidth,y*path.tileheight)
		DrawImage grid,x*path.tileWidth,y*path.tileHeight			
		
	Next
	Next
			
	GrabImage(gmap,0,0)
	Cls
End Function

'This Function draws the blue walls And grid.
Function DrawMapImage()

	If gGameStarted = False	
		Cls

		For Local x:Int = 0 To 800/path.tileWidth-1
		For Local y:Int = 0 To 600/path.tileHeight-1
			If path.walkability[x,y] = unwalkable Then DrawImage wallBlock,x*path.tileWidth,y*path.tileHeight
			DrawImage grid,x*path.tileWidth,y*path.tileHeight			
		Next
		Next
	Else
		DrawPixmap LockImage(gMap),0,0
	End If
End Function	

'This Function loads path.walkability data from a file.
Function LoadMapData(fileName$)
	Local filein:TStream = ReadFile(fileName$)
	If filein
		For Local x:Int = 0 To 800/path.tileWidth-1
		For Local y:Int = 0 To 600/path.tileHeight-1
			path.walkability[x,y] = ReadByte(filein)			
		Next
		Next	 
		CloseFile(filein)	
	End If
End Function	

'This Function saves path.walkability data To a file.
Function SaveMapData(fileName$)
	Local fileout:TStream = WriteFile(fileName$)	
	For Local x:Int = 0 To 800/path.tileWidth-1
	For Local y:Int = 0 To 600/path.tileHeight-1
		If path.walkability[x,y] <> 1Then WriteByte(fileout,0)
		If path.walkability[x,y] = 1 Then WriteByte(fileout,path.walkability[x,y])					
	Next
	Next
	CloseFile(fileout)	
End Function

'ScreenCapture by pressing Print screen Or F12. Successive screen
'captures during the same program run will be saved separately.
'Function ScreenCapture()
'	If KeyHit(88) Or KeyHit(183)
'		SaveBuffer(BackBuffer(),"screenshot"+gScreenCaptureNumber+".png")
'		gScreenCaptureNumber = gScreenCaptureNumber+1 'Global enables multiple captures
'	EndIf	
'End Function

'This Function draws unit claimed nodes. It is called by the 
'RenderScreen() Function.
Function DrawClaimedNodes(drawSelectedOnly:Int=False)
	If gDrawMore = True	
		For Local x:Int = 0 To 800/path.tileWidth-1
		For Local y:Int = 0 To 600/path.tileHeight-1
			If path.claimedNode[x,y] <> Null'claimed nodes
				Local unit:TUnit = path.claimedNode[x,y]
				If drawSelectedOnly=False Or unit.ID <= 3			
					If unit.pathStatus <> stopped
						SetColor unit.red,unit.green,unit.blue
						DrawRect x*path.tileWidth+.4*path.tileWidth,y*path.tileHeight+.4*path.tileHeight,.2*path.tileWidth,.2*path.tileHeight
					End If
				End If	
			End If
		Next
		Next	
		For Local unit:TUnit = EachIn path.unitList 'draw square when unit is tempStopped
			If drawSelectedOnly=False Or unit.ID <= 3			
			If unit.pathStatus = tempStopped
				SetColor unit.red,unit.green,unit.blue
				DrawRect path.ReadPathX(unit,unit.pathLocation)-.25*path.tileWidth,path.ReadPathY(unit,unit.pathLocation)-.25*path.tileHeight,..
									.5*path.tileWidth,.5*path.tileHeight
			End If
			End If
		Next		
	End If
End Function


'This Function calculates the average amount time that has passed
'per loop over the past 20 game loops. This rolling average
'is combined with speed information (expressed in pixels/second) To
'determine how far To move a unit in a given loop. 
'	We use this time-based approach To ensure consistent unit 
'movement speeds. If units instead moved a fixed distance every 
'loop, the movement speed would be inconsistent from one PC
'To the Next because of different chip speeds And monitor refresh
'rates.
'	A rolling average is used because the MilliSecs() Function does
'Not always Return a reliably accurate time down To the millisecond. 
'Using an average over the past 20 game loops is more reliable.
Global savedClockTime#[20]
Global savedClockCount:Int
Function UpdateGameClock()
	savedClockCount = (savedClockCount+1) Mod 20
	Local time# = MilliSecs()
	gLoopTime# = (time#-savedClockTime#[savedClockCount])/20000	
	savedClockTime#[savedClockCount] = time#
	If gLoopTime# > .1 Then gLoopTime# = .0167	
End Function

'**********************************************************************************
'*
'* Following functions draw unfilled/filled shapes.
'**********************************************************************************

Function Draw_Oval(cx:Int,cy:Int,width:Int,height:Int=Null,filled:Int = True)
	
	Local Rx:Int,Ry:Int
	Local p:Int,px:Int,py:Int,x:Int,y:Int
	Local Rx2:Int,Ry2:Int,twoRx2:Int,twoRy2:Int
	Local pFloat:Float
	
	Rx=Abs(width/2)
	Ry=Abs(height/2)
	cx :+rx
	cy :+ry
	Rx2=Rx*Rx
	Ry2=Ry*Ry
	twoRx2=Rx2 Shl 1
	twoRy2=Ry2 Shl 1
	'Region 1
	x=0
	y=Ry
	If filled Then 
		DrawLine cx+x,cy+y,cx-x,cy+y
		DrawLine cx+x,cy-y,cx-x,cy-y
	Else
		Plot cx+x,cy+y
		Plot cx-x,cy+y
		Plot cx+x,cy-y
		Plot cx-x,cy-y
	EndIf	
	pFloat=(Ry2-(Rx2*Ry))+(0.25*Rx2)
	p=Int(pFloat)
	If pFloat Mod 1.0>=0.5 Then p:+1
		px=0
		py=twoRx2*y
		While px<py
			x:+1
        		px:+twoRy2
           		If p>=0
				y:-1
				py:-twoRx2
			EndIf
			If p<0 Then p:+Ry2+px Else p:+Ry2+px-py
			If filled Then
				DrawLine cx+x,cy+y,cx-x,cy+y
				DrawLine cx+x,cy-y,cx-x,cy-y
			Else
				Plot cx+x,cy+y
				Plot cx-x,cy+y
				Plot cx+x,cy-y
				Plot cx-x,cy-y
			EndIf
		Wend
		'Region 2
		pFloat=(Ry2*(x+0.5)*(x+0.5))+(Rx2*(y-1.0)*(y-1.0))-(Rx2*(Float(Ry2)))
		p=Int(pFloat)
		If pFloat Mod 1.0>=0.5 Then p:+1
		While y>0
			y:-1
			py:-twoRx2
			If p<=0
				x:+1
				px:+twoRy2
			EndIf
			If p>0 Then p:+Rx2-py Else p:+Rx2-py+px
			If filled Then 
				DrawLine cx+x,cy+y,cx-x,cy+y
				DrawLine cx+x,cy-y,cx-x,cy-y
			Else
				Plot cx+x,cy+y
				Plot cx-x,cy+y
				Plot cx+x,cy-y
				Plot cx-x,cy-y
			EndIf
		Wend
End Function

Function Draw_Circle(cx:Int,cy:Int,radius:Int,filled:Int = True) 

		If (cx-radius) > GraphicsWidth()  Return  
		If (cy-radius) > GraphicsHeight()  Return
		If (cx+radius) < 0 Then Return
		If (cy+radius) < 0 Then Return
		Local x:Int = 0
		Local d:Int = (2*Radius)						
		Local y:Int=Radius
		While x<y
			If d < 0 Then
				d = d + (4 * x) + 6
			Else
				d = d + 4 * (x - y) + 10
				y = y - 1
			End If
		If filled Then
		 	DrawLine cx + X, cy + Y,cx + X, cy - Y
			DrawLine cx - X, cy + Y,cx - X, cy - Y
			DrawLine cx + Y, cy + X,cx + Y, cy - X
			DrawLine cx - Y, cy + X,cx - Y, cy - X

		Else
			Plot(cx + X, cy + Y)
			Plot(cx + X, cy - Y)
			Plot(cx - X, cy + Y)
			Plot(cx - X, cy - Y)
			Plot(cx + Y, cy + X)
			Plot(cx + Y, cy - X)
			Plot(cx - Y, cy + X)
			Plot(cx - Y, cy - X)
		EndIf
			x=x+1
		Wend

End Function

Function  Draw_Rectangle(x:Int,y:Int,width:Int,height:Int=Null,filled:Int = True) 
	If filled
		DrawRect x,y,width,height
	Else
		DrawLine x,y,x+width,y
		DrawLine x,y,x,y+height
		DrawLine x+width,y,x+width,y+height
		DrawLine x,y+height,x+width,y+height
	EndIf
End Function


Function Draw_Line_Trapezoid(x:Int,y:Int,length:Int,height:Int)
	Local topleftx:Int = length/4+x
	Local lowerlefty:Int = y+height
	Local lowerrightx:Int = x+length
	Local lowerrighty:Int = y+height
	Local upperrightx:Int = length/4*3+x
	DrawLine topleftx,y,x,lowerlefty
	DrawLine x,lowerlefty,lowerrightx,lowerrighty
	DrawLine lowerrightx,lowerrighty,upperrightx,y
	DrawLine upperrightx,y,topleftx,y
EndFunction


Function Draw_Polygon( xy:Float[], x:Float=0, y:Float=0, fill:Int=True )
	Local origin_x:Float
	Local origin_y:Float
	GetOrigin origin_x,origin_y
	Local handle_x:Float
	Local handle_y:Float
	GetHandle handle_x,handle_y

	If fill
		_max2dDriver.DrawPoly xy,..
		handle_x,handle_y,..
		x+origin_x,y+origin_y
	Else
		Local x1:Float=xy[xy.Length-2]
		Local y1:Float=xy[xy.Length-1]
		For Local i:Int=0 Until Len xy Step 2
			Local x2:Float=xy[i]
			Local y2:Float=xy[i+1]
			DrawLine handle_x+x1,handle_y+y1, handle_x+x2,handle_y+y2',..
			x1=x2
			y1=y2
		Next
	EndIf
End Function
[/code]

test with this:
[codde]
SuperStrict


'PATHFINDING - Group Movement
'===============================================================
'By Patrick Lester (Updated 4/7/04)

'An article describing A* And this code in particular can be found at;
'http://www.policyalmanac.org/games/aStarTutorial.htm

'Features
'----------
'This demo adds a few features that facilitate group movement. These
'New features Include;

'- When a group of units is selected And sent To a given location on the 
'  map, different destinations are chosen For each group member.
'- Paths already claimed by nearby units have a small terrain penalty 
'  added To them. This encourages units To find paths that are slightly
'  different from nearby units, which reduces Collisions.
'- When a collision occurs, units will always repath, but will Not use the 
'  New path If it is too Long compared To simply temporarily stopping in 
'  place And waiting For the other TUnitTo move out of the way. This makes 
'  movement a bit smoother than tempstopping most of the time.
'- Areas of the map that are completely walled off from the rest of the map 
'  are identified as separate islands. The CheckRedirect() Function is updated To 
'  reflect this information.
'- The FindPath() Function has been modified To allow small random movement 
'  when a nonexistent path is found. This prevents units from locking up If they 
'  are approaching from opposite directions down a corridor.

'Instructions
'------------
'- Press enter Or Right click To activate the sprites, Then 
'  Right click on any reachable spot on the map. The selected 
'  sprite will use AStar To find the best path To that spot And Then
'  go there. You may Continue To Left Or Right click To find more
'  paths.
'- Select different units by Left clicking on them Or box-selecting them.
'- Press the space bar To toggle drawing paths on And off.
'- Draw blue walls by Left clicking on the screen. 
'- Erase blue walls by Left clicking on them.
'- Press enter again To deactivate the sprites And Return		
'  To map drawing mode.
'- Press escape To Exit the program.

'===============================================================

'A* set up

Framework BRL.D3D7Max2D
Import BRL.PNGLoader
Import BRL.Random

' modules which may be required:
' Import BRL.BMPLoader
' Import BRL.TGALoader
' Import BRL.JPGLoader


SetGraphicsDriver D3D7Max2DDriver()

Include "aStarLibrary.bmx" 'contains A* code.
Include "shared functions.bmx"

'Declare globals And types
Global cursor:TImage, grid:TImage, wallBlock:TImage, gMap:TImage, gSelectedCircle:TImage  'Graphics
Global gDrawing:Int, gErasing:Int, gGameStarted:Int, gDrawMore:Int, gDrawText:Int
Global gBoxSelecting:Int, gBoxSelectX:Int,gBoxSelectY:Int, gMouseHit1:Int, gMouseHit2:Int
Global Stopped:Int,smiley:TImage,chaser:TImage
Const userControlled:Int = 1, patrol:Int = 2, random:Int = 3
Global path:TAstar = New TAstar
Type TUnit
	Field ID:Int, xLoc#, yLoc#, speed# 'speed is in pixels/second
	Field pathAI:Int, pathStatus:Int, pathLength:Int, pathLocation:Int
	Field pathBank:TBank, pathBank1:TBank, pathBank2:TBank
	Field xPath#, yPath#, distanceToNextNode# 'in pixels
	Field targetX#, targetY#, target:TUnit, unitCollidingWith:TUnit
	Field startNewPath:Int 'used For delayed-action pathfinding
	Field sprite:TImage, red:Int, green:Int, blue:Int
	Field selected:Int
	Field xDistanceFromGroupCenter#, yDistanceFromGroupCenter# 
	Field actualAngleFromGroupCenter:Int, assignedAngleFromGroupCenter:Int 	
End Type

'Full game sequence
LaunchProgram()		
RunProgram()
End
	
'Main program loop	
Function RunProgram()
	While Not KeyHit(KEY_ESCAPE) 'While escape key isn't pressed		
		UpdateGameClock() 'see shared functions.bb Include file
		UserInput() 'process Mouse And keyboard Input	
		MoveUnits() 'move sprites			
		RenderScreen() 'draw stuff on the screen		
	Wend
End Function

Function LaunchProgram()
	If Not GraphicsModeExists(800,600,16) RuntimeError "Sorry, this program won't work with your graphics card."	
	Graphics 800,600 ; 'SetBuffer BackBuffer()
	HideMouse ; gDrawMore = True
	
	'Load Graphics
	cursor = LoadImage("red_pointer.png")
	grid = LoadImage("grid.png")		
	wallBlock = LoadImage("wall.png")	
	smiley = LoadImage("smiley.png")
	MidHandleImage smiley
	chaser = LoadImage("ghost.png")
	MidHandleImage(chaser)
	'Create green selection circle
	gSelectedCircle = CreateImage(25,25)
	SetColor 0,255,0 ; Draw_Oval 0,0,24,24,0 
	GrabImage(gSelectedCircle,0,0)
	MidHandleImage gSelectedCircle  
	SetColor 255,255,255
	'Load map walkability data
	path.init(25,25,32,24)
	LoadMapData("myTerrainData.dat")'see common functions.bb Include file
	
	'Create And initialize some units
	For Local ID:Int = 1 To 6
		Local unit:TUnit= New Tunit
		path.unitList.addlast(unit)
		unit.ID = ID ; unit.sprite = smiley ; unit.speed = 200
	
		If ID = 1 
			unit.xLoc = 112.5 ; unit.yLoc = 337.5 ; unit.selected = True		
			unit.red = 255 ; unit.green = 255 ; unit.blue = 0 			
		Else If ID = 2 
			unit.xLoc = 737.5 ; unit.yLoc = 337.5 ; unit.selected = True	
			unit.red = 255 ; unit.green = 0  ; unit.blue = 255						
		Else If ID = 3 
			unit.xLoc = 437.5 ; unit.yLoc = 237.5 ; unit.selected = True	
			unit.red = 0 ; unit.green = 0 ; unit.blue = 255			
		Else
			Local x:Int
			Local y:Int
			Repeat
				x:Int = path.tileWidth*(Rand(775)/path.tileWidth)+path.tileWidth/2
				y:Int = path.tileHeight*(Rand(575)/path.tileHeight)+path.tileHeight/2
			Until path.walkability[x/path.tileWidth,y/path.tileHeight] = walkable
			unit.xLoc = x ; unit.yLoc = y ; unit.sprite = chaser
			unit.pathAI = random							
			unit.red = Rand(255) ; unit.green = Rand(255) ; unit.blue = Rand(255)						
		End If

		unit.pathBank1 = CreateBank(4)			
		unit.pathBank2 = CreateBank(4)
		unit.pathBank = 	unit.pathBank1 
		unit.targetX = unit.xLoc ; unit.targetY = unit.yLoc 'required			
		unit.xPath = unit.xLoc ; unit.yPath = unit.yLoc 'required			
		path.ClearNodes(unit)	
	Next		

End Function

'This Function handles most user Mouse And keyboard Input
Function UserInput()

	'Record the Mouse hit variables
	gMouseHit1 = MouseHit(1)
	gMouseHit2 = MouseHit(2)	

	'If in map edit mode
	If gGameStarted = False

		If (Not MouseDown(1)) Then gDrawing = False
		If (Not MouseDown(1)) Then gErasing = False
	
		'Edit map by drawing Or erasing walls
		If MouseDown(1)
		
			'Draw walls
			If path.walkability[MouseX()/path.tileWidth,MouseY()/path.tileHeight] = walkable And gErasing = False
			 	path.walkability[MouseX()/path.tileWidth,MouseY()/path.tileHeight] = unwalkable
				gDrawing = True
			End If

			'Erase walls	
			If path.walkability[MouseX()/path.tileWidth,MouseY()/path.tileHeight] = unwalkable And gDrawing = False
			 	path.walkability[MouseX()/path.tileWidth,MouseY()/path.tileHeight] = walkable
				gErasing = True
			End If	
	
		'Start game If Return/enter Or Right Mouse button is hit
		Else If KeyHit(KEY_RETURN) Or gMouseHit2 = True
			gGameStarted = True ; gMouseHit2 = False
			CopyMapImage()			
			path.IdentifyIslands() 'update islands() array.			
		End If

	'If in game/pathfinding mode ...
	Else
		'Start box selection by Left clicking on the map
		Local oneSelected:Int
		If gMouseHit1
			For Local unit:TUnit= EachIn path.unitList
				unit.selected = False
				Local x:Int = MouseX()
				Local y:Int = MouseY()
				If unit.xLoc-unit.sprite.width/2 < x Then
					If unit.xloc+unit.sprite.width/2 > x Then
						If unit.yLoc-unit.sprite.height/2 < y Then
							If unit.yLoc+unit.sprite.Height/2 > y
								unit.selected = True ; oneSelected = True
							End If
						EndIf
					EndIf
				EndIf
			Next
			If oneSelected = False
				gBoxSelecting = True
				gBoxSelectX = MouseX()
				gBoxSelectY = MouseY()
			End If	

		'Box Select once the Mouse is released.	
		Else If MouseDown(1) = False And gBoxSelecting = True	
			BoxSelectUnits()

		'If Right clicking, update target locations For the selected group.	
		Else If gMouseHit2		
			ChooseGroupLocations(path)	
	
		'Reenter map edit mode by pressing enter/Return key
		Else If KeyHit(KEY_RETURN) Then 
			gGameStarted = False	
			For Local unit:TUnit= EachIn path.unitList
				unit.pathStatus = notstarted
			Next	
		End If		
				
	End If

	'Save the map by pressing the "s" key
	If KeyHit(KEY_S) Then SaveMapData("myTerrainData.dat") 'see common functions.bb Include file	

	'Toggle drawing more by pressing space bar
	If KeyHit(KEY_SPACE) Then gDrawMore = 1-gDrawMore

	'Toggle drawing ] by pressing "t" key
	If KeyHit(KEY_T) Then gDrawText = 1-gDrawText	
	
End Function

'This Function selects units when the Mouse has been released. It is
'called from the UserInput() Function.
Function BoxSelectUnits()
	gBoxSelecting = False
	Local x1:Int = Min(gBoxSelectX,MouseX()) ; Local x2:Int = Max(gBoxSelectX,MouseX())	
	Local y1:Int = Min(gBoxSelectY,MouseY()) ; Local y2:Int = Max(gBoxSelectY,MouseY())	
	For Local unit:TUnit= EachIn path.unitList
		If unit.xLoc > x1 And unit.xLoc < x2 And unit.yLoc > y1 And unit.yLoc < y2
			unit.selected = True
		Else
			unit.selected = False	
		End If
	Next
End Function


'This Function draws stuff on the screen.
Function RenderScreen()
	
	'Draw the walls And the grid 	overlay
	DrawMapImage() 'see shared functions.bb Include file

	'Draw paths
	If gDrawMore = True
		For Local unit:TUnit= EachIn path.unitList
			If unit.selected = True
				SetColor unit.red,unit.green,unit.blue			
				For Local pathLocation:Int = 1 To unit.pathLength
					Local x1:Int = path.ReadPathX(unit:TUnit,pathLocation-1)
					Local y1:Int = path.ReadPathY(unit:TUnit,pathLocation-1)
					Local x2:Int = path.ReadPathX(unit:TUnit,pathLocation)
					Local y2:Int = path.ReadPathY(unit:TUnit,pathLocation)
					x1=x1+unit.ID*2 ; x2=x2+unit.ID*2 ; y1=y1+unit.ID ; y2=y2+unit.ID									
					DrawLine x1,y1,x2,y2		
				Next	
			End If	
		Next	
	End If
	
	'Draw units
	Local r:Int,g:Int,b:Int
	GetColor r,g,b
	SetColor 255,255,255
	For Local unit:TUnit= EachIn path.unitList

		DrawImage unit.sprite,unit.xLoc,unit.yLoc	
		If unit.selected = True Then DrawImage gSelectedCircle,unit.xLoc,unit.yLoc					
	Next

	'Draw TUnitclaimed nodes
	DrawClaimedNodes(True)'see shared functions.bb Include file

	'Draw selection box
	If gBoxSelecting = True
		SetColor 0,255,0
		DrawLine gBoxSelectX,gBoxSelectY,MouseX(),gBoxSelectY
		DrawLine MouseX(),gBoxSelectY,MouseX(),MouseY()
		DrawLine MouseX(),MouseY(),gBoxSelectX,MouseY()
		DrawLine gBoxSelectX,MouseY(),gBoxSelectX,gBoxSelectY							
	End If
	
	'Draw text on the screen showing some TUnitstatistics.
	If gDrawText = True
		Local x:Int = 50 ; Local y:Int = 50
		SetColor 255,255,255
		For Local unit:TUnit= EachIn path.unitList
			DrawText unit.ID,x,y+0
			DrawText unit.selected,x,y+15			
			DrawText unit.pathStatus,x,y+30
			DrawText unit.xLoc,x,y+45 
			DrawText unit.yLoc,x,y+60 
			DrawText unit.xPath,x,y+75
			DrawText unit.yPath,x,y+90 	
			DrawText unit.pathLocation+"/"+unit.pathLength,x,y+105			
			x = x + 100	
			If unit.ID Mod 8 = 7 Then y = y+200 ; x = 50				
		Next
	End If	
	SetColor 255,0,0	
	DrawImage cursor,MouseX(),MouseY() 'Draw the Mouse
	DrawText "<space> display/hide path",10,10
	DrawText "<enter> edit/play mode",10,25
	DrawText "<s> save map",10 ,40
	DrawText "<l> loadmap",10 ,55
	DrawText "mouse: selects unit(s)/ edit map",10,70
	SetColor r,g,b
	
	Flip
End Function

'This Function performs pathfinding And moves the units.
Function MoveUnits()
	If gGameStarted = True 
		For Local unit:TUnit= EachIn path.unitList
			UpdatePath(unit)
			If unit.pathStatus = found Then MoveUnit(unit)				
		Next
	End If		
End Function	

'This Function checks For path updates And calls the
'FindPath() Function when needed.
Function UpdatePath(unit:TUnit)
		
	'If the TUnitis tempStopped, keep checking the
	'blocked path node Until it is free And the TUnitis able Continue
	'along its path. If the Next Step is blocked by a stopped unit
	'Then repath.	
	If unit.pathStatus = tempStopped 		
		Local otherUnit:TUnit= path.DetectCollision(unit:TUnit)
		If otherUnit= Null
			unit.pathStatus = found
			path.ClaimNodes(unit:TUnit)	
		Else If otherunit.pathStatus <> found 'node is blocked by nonmoving unit
			unit.unitCollidingWith = otherUnit
			unit.pathStatus = path.FindPath(unit:TUnit,unit.targetX,unit.targetY)	
		End If						
	
	'If the unit's path is nonexistent, find a path to a random location that
	'is nearby. This will tend To break up units that are locked in place.
	Else If unit.pathStatus = nonexistent
		unit.pathStatus = path.FindPath(unit:TUnit,0,0,randomMove)
		
	'If the unit's pathStatus = notStarted, and the TUnitis not at its target location, then
	'generate a New path To that location. This can be True If a TUnithas found a path
	'To a random location after experiencing a nonexistent path (see above).
	Else If unit.pathStatus = stopped
		Local x1:Int = Floor(unit.xLoc/path.tileWidth) ; Local y1:Int = Floor(unit.yLoc/path.tileheight) 
		Local x2:Int = Floor(unit.targetX/path.tileWidth) ; Local y2:Int = Floor(unit.targetY/path.tileHeight) 		
		If  x1 <> x2 Or y1 <> y2			
			unit.pathStatus = path.FindPath(unit:TUnit,unit.targetX,unit.targetY)										
		End If			
	End If			

	'If the TUnithas been selected, trigger New paths using the Mouse.
	'There is a Delay built in so the New path isn't implemented
	'Until the Next node is reached.
	If unit.selected = True		
		If gMouseHit2 = True					 												
			If unit.distanceToNextNode = 0
				unit.pathStatus = path.FindPath(unit:TUnit,unit.targetX,unit.targetY)	
			Else		
				unit.startNewPath = True 'wait To trigger path (see below)
			End If
		Else If unit.startNewPath = True And unit.distanceToNextNode = 0 
			unit.pathStatus = path.FindPath(unit:TUnit,unit.targetX,unit.targetY)
			unit.startNewPath = False					
		End If
		
	'If pathAI = random, choose a random spot on the screen To pathfind To.
	Else If unit.pathAI = random
		If unit.pathstatus = stopped
			unit.targetX = Rand(0,799)
			unit.targetY = Rand(0,599)
			unit.pathStatus = path.FindPath(unit:TUnit,unit.targetX,unit.targetY)	
		End If						
	End If
	
End Function
	

'This Function moves sprites around on the screen. 
Function MoveUnit(unit:TUnit)

	'Move toward the Next path node
	Local remainingDistance# = MoveTowardNode#(unit:TUnit,gLoopTime*unit.speed)	
	
	'If there is any remaining distance Left after moving toward the node, Then
	'check For path Step advances And move To the Next one. This two Step
	'process ensures smooth movement from node To node.
	If remainingDistance <> 0 And unit.startNewPath = False	
		MoveTowardNode#(unit:TUnit,remainingDistance#)		
	End If		

End Function


'This Function checks For path Step advances And Then moves toward the
'Next path node. If the Next node is reached, the Function returns any
'remaining distance Left To be travelled.
Function MoveTowardNode#(unit:TUnit,distanceTravelled#)
	path.CheckPathStepAdvance(unit:TUnit)
	If unit.pathStatus <> found Then Return 0
	If distanceTravelled# <= unit.distanceToNextNode#
		Local xVector# = unit.xPath-unit.xLoc
		Local yVector# = unit.yPath-unit.yLoc	
		Local angle# = ATan2(yVector#,xVector#)
		unit.xLoc = unit.xLoc + Cos(angle)*distanceTravelled
		unit.yLoc = unit.yLoc + Sin(angle)*distanceTravelled
		unit.distanceToNextNode# = unit.distanceToNextNode#-distanceTravelled# 
	Else 'Next path node has been reached
		unit.xLoc = unit.xPath ; unit.yLoc = unit.yPath	
		Local remainingDistance# = distanceTravelled#-unit.distanceToNextNode# 
		unit.distanceToNextNode = 0
		Return remainingDistance#	
	End If	
End Function
[/code]
