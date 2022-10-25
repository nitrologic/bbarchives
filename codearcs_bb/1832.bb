; ID: 1832
; Author: kevin8084
; Date: 2006-10-02 20:28:28
; Title: A* Pathfinding
; Description: A* Pathfinding Example

; **************************************************************************************************
; A* Pathfinding tutorial by Kevin Lee Legge (kevin8084@gmail.com)
; **************************************************************************************************
;
; First of all, what IS A* Pathfinding? In a nutshell, it is an algorithm that allows an AI to
; find the shortest (or ALMOST shortest) path from point A to point B, avoiding any obstacles
; that may be in the way.
; The most important equation in the algorithm is F = G * H
; G = the movement cost from the current location to the next, adjacent, location. If the next
; location is diagonal to the current location, then the G cost is 14. If the next location is
; either horizontal or vertical to the current location, then the G cost is 10. One important point
; to remember is that the G costs add up. Meaning that if the child is horizontal to its parent,
; for example, its G cost is 10 ADDED to its parent's G cost. So, if the parent has a G cost of
; 14, then the child's total G cost is 24 (this is the movement cost "in total" from the
; starting square.)
; H = the Heuristic cost (heuristic, meaning guess). What this means is the cost of moving from
; point A to point B in either horizontal or vertical steps. The total number of steps is then 
; multiplied by 10. This is called the Manhatten method.
; F = Final cost. This is the sum of G and H. The lower the F cost, the better.
;
; ---  ---  ---  ---  ---
; | |  | |  | |  | |  | |
; ---  ---  ---  ---  ---
; | |  | |  |O|  | |  | |
; ---  ---  ---  ---  ---
; | |  |A|  |O|  |B|  | |
; ---  ---  ---  ---  ---
; | |  | |  |O|  | |  | |
; ---  ---  ---  ---  ---
; | |  | |  | |  | |  | |
; ---  ---  ---  ---  ---
;
; In the above diagram, A=starting point, O=obstacles, B=ending point. We want to find the shortest
; path from point A to point B, avoiding the obstacles.
; Let's take a closer look at point A and the squares that are adjacent to A.
;
; ---  ---  ---  ---  ---
; |3|  |2|  |O|  | |  | |
; ---  ---  ---  ---  ---
; |4|  |A|  |O|  |B|  | |
; ---  ---  ---  ---  ---
; |5|  |1|  |O|  | |  | |
; ---  ---  ---  ---  ---
;
; In the above view you'll notice that the squares that are adjacent to point A are numbered from 1
; to 5. Let's take square1. Is it diagonal to point A? Nope. Therefore it's G cost is 10. How about
; it's H cost? If you take 2 steps to the right of square1 and 1 step up, you'll arrive at point B.
; That's a total of 3 steps. Multiply those 3 steps by 10 and you get the H cost - 30.
; So, we now know that the F value of square1 is 10 + 30 = 40
; Let's take square2 now. Again, it is vertically adjacent to point A, so the G cost is also 10.
; How many steps, horizontal and vertical, does it take to arrive at point B from square2? 2 steps
; horizontally and 1 step vertically for a combined total of 3 steps. Multiplying by 10, we get an
; H cost of 30. F = 10 + 30 = 40. The F value of square2 is the same as square1.
; Ready to tackle square3? Good! If you look at square3, you'll see that it is not vertically
; adjacent to point A. Neither is it horizontally adjacent. No, it is diagonal to point A. So it's
; G cost is 14. How about it's H cost? Well, to arrive at point B from square3 you'd need to take
; 3 horizontal steps and 1 vertical step for a total of 4 steps. Multiplying by 10, you get an H
; cost of 40. What's the F value? 14 + 40 = 54.
; Last we check square5. No surprises here - it is also diagonal to point A so it's G cost is 14.
; Like square4, square5 is also located 4 steps away from point B (3 horizontal and 1 vertical step),
; giving it an H cost of 40. The F cost is then 14 + 40 = 54.
; Notice that we didn't calculate the G, H, or F costs of the squares labelled 'O'? Why? We ignored
; them because they aren't part of the walkable area that we must traverse.
; Ready for more? Good!
;
; ---  ---  ---  ---  ---
; | |  | |  | |  | |  | |
; ---  ---  ---  ---  ---
; |3|  |2|  |O|  | |  | |
; ---  ---  ---  ---  ---
; |4|  |A|  |O|  |B|  | |
; ---  ---  ---  ---  ---
; |5|  |1|  |O|  | |  | |
; ---  ---  ---  ---  ---
; | |  | |  | |  | |  | |
; ---  ---  ---  ---  ---
;
; In the above diagram (looks familiar, yes?) the squares numbered from 1 to 5 are children of
; the starting square, point A.
; Before we go any further, we need to learn about open, closed, and undefined lists. What are they?
; Well, when we calculate the F value of the adjacent squares, we put the squares on the "open" list.
; That means that we have looked at them, though we may not be finished with them, yet.
; If a square is put on the "closed" list, that means that we are finished with the square - that
; it has been examined already. For the most part this is true, but there ARE some few exceptions.
; The "undefined" list is just that - all of the squares that we haven't looked at yet are still
; undefined.
; Another point - when we look at the adjacent squares of point A, we put point A on the "closed"
; list. So, where are we now?
; We have put point A on the closed list, so we need another square to act as our "point A"
; That is where the F value comes in. We take the square with the lowest F value and make that
; our current square - our "point A". What is the lowest F value of the squares that we have
; already looked at? It is 40. But wait - there are TWO squares that have the F value of 40! What
; do we do? We take whichever one of those 2 squares that we wish and make it our current square.
; It doesn't matter which one we take. For the sake of this tutorial, let's take square2 to
; be our current square.
;
; ---  ---  ---  ---  ---
; | |  | |  | |  | |  | |
; ---  ---  ---  ---  ---
; |3|  |A|  |O|  | |  | |
; ---  ---  ---  ---  ---
; |4|  |X|  |O|  |B|  | |
; ---  ---  ---  ---  ---
; |5|  |1|  |O|  | |  | |
; ---  ---  ---  ---  ---
; | |  | |  | |  | |  | |
; ---  ---  ---  ---  ---
;
; If you look at the above diagram you'll see that the former point A is now labelled with an "X".
; This is to let you know that this square is on the "closed" list. The former square2 is now
; labelled with an "A" to designate the new point A. Place the new point A on the "closed" list.
; And we start again.
;
; ---  ---  ---  ---  ---
; |2|  |1|  | |  | |  | |
; ---  ---  ---  ---  ---
; |3|  |A|  |O|  | |  | |
; ---  ---  ---  ---  ---
; |4|  |X|  |O|  |B|  | |
; ---  ---  ---  ---  ---
; | |  | |  |O|  | |  | |
; ---  ---  ---  ---  ---
; | |  | |  | |  | |  | |
; ---  ---  ---  ---  ---
;
; If you look at square1 you'll see that it is located directly above point A. Therefore it's G
; cost is 20 - its parent's G cost of 10 added to its OWN G cost of 10.
; What is it's H cost? 40. Why? Because you need To take 2 horizontal steps And 2
; vertical steps to get from square1 to point B. This makes its F value 60.
; Square2 is diagonal to point A, so it's G cost is 24. It takes 3 horizontal steps and 2 vertical
; steps to arrive at point B, giving it an H cost of 50 (remember, the total number of steps
; multiplied by 10). It's F value, then, is 74.
; Square3 is a square that is already on the "open" list from the former point A. So, what do we do?
; We determine if the path through the current square to square3 is shorter than from the former point A
; to square3. How do we do that? We take the G cost of the current square, which is 10, and add it
; to the G cost of square3. Since square3 is horizontally adjacent to the current square, its G cost
; is 10. So, the total G cost is 20 which is WAY higher than square3's original G cost of 14! 
; So we don't do anything with square3 because the path through the current square to square3 is NOT
; shorter (what this means is that it would be shorter to go directly from the former point A to square3).
; Finally we check square4. This square is also on the "open" list. So, again, we check to see if the
; path would be shorter if we get to square4 from the current square. Is it? The recalculated G
; cost is 24. This is much higher than the original G cost of square4, which is 10. Again, we don't
; do anything with this square.
; Now, if you look closely at the diagram, you will see that there is another, non-obstacle, square
; that is adjacent to the current square. Why aren't we checking THIS one? Well, we COULD, but we
; would be clipping the corner of the topmost obstacle if we did. If this doesn't matter in your game
; then, by all means, check this square too.
;
; ---  ---  ---  ---  ---
; |2|  |A|  |1|  | |  | |
; ---  ---  ---  ---  ---
; |3|  |X|  |O|  | |  | |
; ---  ---  ---  ---  ---
; | |  |X|  |O|  |B|  | |
; ---  ---  ---  ---  ---
; | |  | |  |O|  | |  | |
; ---  ---  ---  ---  ---
; | |  | |  | |  | |  | |
; ---  ---  ---  ---  ---
;
; The above diagram shows where we are now. I've taken the liberty of labelling the squares for you.
; The former square1 is now the current square - the point A - because it had the lowest F value of
; 60. Place the current square on the closed list and let's calculate the F value of square1.
; First, square1's G cost is 30 because it is directly horizontal to the current square. Next, it's
; H cost is 30 because you have to take 1 horizontal step and 2 vertical steps to reach point B from
; square1. That makes its F value 60.
; Square2 is already on the open list so let's see if the path is shorter to square2 by going through
; the current square. The G cost from the former point A to the current point A is 20 added to the G
; cost of square2 from the current point A - which is also 10 - makes the total G cost 30, which is
; higher than the original G cost of square2 - 24. We leave square2 alone. Checking square3 we find
; much the same, so we leave THAT square alone, as well.
; Guess that leaves us with 1 square to make the current point A, eh? Don't forget to put it on the
; closed list.
;
; ---  ---  ---  ---  ---
; | |  |X|  |A|  |1|  | |
; ---  ---  ---  ---  ---
; | |  |X|  |O|  | |  | |
; ---  ---  ---  ---  ---
; | |  |X|  |O|  |B|  | |
; ---  ---  ---  ---  ---
; | |  | |  |O|  | |  | |
; ---  ---  ---  ---  ---
; | |  | |  | |  | |  | |
; ---  ---  ---  ---  ---
; 
; Looking at square1 we find that the G cost is 40 and the H cost is 20. This means that the F value
; of square1 is 60. We don't have any more squares to check because of that clipping issue with the
; obstacle. So, what do we do? We make square1 the current square - point A - and put it on the closed
; list.
;
; ---  ---  ---  ---  ---
; | |  |X|  |X|  |A|  |1|
; ---  ---  ---  ---  ---
; | |  |X|  |O|  |3|  |2|
; ---  ---  ---  ---  ---
; | |  |X|  |O|  |B|  | |
; ---  ---  ---  ---  ---
; | |  | |  |O|  | |  | |
; ---  ---  ---  ---  ---
; | |  | |  | |  | |  | |
; ---  ---  ---  ---  ---
;
; Okay. Checking square1 we find the G cost is 50, the H cost is 30, the F value is 80. Square2's
; G cost is 54, it's H cost is 20, it's F value is 74. Square3's G cost is 50, it's H cost is 10, 
; it's F value is 60. Guess which square we'll make our current square? Yep, the one with the lowest
; F value - square3.
; Make square3 the current square - point A - and place it on the closed list.
;
; ---  ---  ---  ---  ---
; | |  |X|  |X|  |X|  |1|
; ---  ---  ---  ---  ---
; | |  |X|  |O|  |A|  |2|
; ---  ---  ---  ---  ---
; | |  |X|  |O|  |B|  |3|
; ---  ---  ---  ---  ---
; | |  | |  |O|  | |  | |
; ---  ---  ---  ---  ---
; | |  | |  | |  | |  | |
; ---  ---  ---  ---  ---
;
; Square1 is already on the open list, so we see if the path to that square is shorter by going through
; the current square. Nope. Square2 is also on the open list and the path is NOT shorter, either. How
; about square3? Since it's not on the open list, put it there. Its G cost is 64, its H cost is 10,
; it's F value is 74. That leaves one more square that is neither an obstacle, nor on the closed list.
; Yep, it's point B. For the sake of completeness let's calculate. SquareB's G cost is 70, it's H
; cost is 0, its F value is 70. Since it has the lowest F value, make it the current square and put
; it on the closed list. NOW we are done. We have reached point B from point A.
;
; ---  ---  ---  ---  ---
; | |  |X|  |X|  |X|  | |
; ---  ---  ---  ---  ---
; | |  |X|  |O|  |X|  | |
; ---  ---  ---  ---  ---
; | |  |X|  |O|  |B|  | |
; ---  ---  ---  ---  ---
; | |  | |  |O|  | |  | |
; ---  ---  ---  ---  ---
; | |  | |  | |  | |  | |
; ---  ---  ---  ---  ---
;
; Now, to get the path we just traversed, follow backwards from point B to the original point A.
; That is, follow the path backwards from the current square to its parent, to ITS parent, etc..
; In the diagram above the parents are labelled with an "X" which shows that they are on the
; closed list.
;
; I hope that this little tutorial has made understanding A* Pathfinding a little easier.
; *******************************************
; **************************************************************************************************
; A Star Pathfinding example in 3d by Kevin Lee Legge (kevin8084@gmail.com)
; **************************************************************************************************



Graphics3D 800,600
SetBuffer BackBuffer()


Const wall_type=1
Const player_type=2
Const ground_type=3
Const target_type=4
Const walker_type=5

Dim grid(9,9) 				; used to hold the world data

Global path[99] 			; used to store the found path
Global pathCount=0 			; how many nodes are stored in path() array

Global query=0 				; return value for checking if shorter path

Dim heap(99)  				; this is the heap
Dim nodeheap(99) 			; this corresponds to the node's position in the heap
Global heapCount=0 			; this is the index into the heap

Global currentX,currentY 	; the current node's x/y coordinate
Global currentNode 			; the current node
Global targetX,targetY 		; the target's x/y coordinate
Global found=0 				; flag for if target node is found

Type node
	Field x,y               ; x/y coordinate of the node
	Field parent.node       ; holds the node's parent
	Field gCost             ; node's G Cost
	Field hCost             ; node's H Cost
	Field fCost             ; node's F Cost
	Field state 			; 0=undefined,1=open,2=closed
	Field walkable 			; 0=no,1=yes
	Field number  			; holds the node number
End Type

Global pivot=CreatePivot()  ; just something for the walker to aim at

Global walker               ; this will be our walker

Global target=CreateSphere(); this is our target
EntityColor target,255,0,0
EntityShininess target,1
EntityRadius target,1
EntityType target,target_type
PositionEntity target,0,50,0; positioned so far out of range to avoid collision problem when
							; setting up the world

Global camera=CreateCamera(); we need to see
CameraRange camera,.1,300
PositionEntity camera,0,50,0
EntityRadius camera,1
EntityType camera,player_type

light=CreateLight()
PositionEntity light,0,100,0
LightRange light,1000
RotateEntity light,90,0,0

Global ground=CreatePlane()
PositionEntity ground,0,0,0
EntityColor ground,10,50,10
EntityType ground,ground_type

Collisions player_type,wall_type,2,2
Collisions player_type,ground_type,2,2
Collisions walker_type,wall_type,2,1
Collisions walker_type,ground_type,2,2

; set up the world
readData()
; make sure that the camera is pointing in the right direction
PointEntity camera,walker

; since we already read in currentX and currentY, we can position the pivot now
PositionEntity pivot,currentX,1,currentY

quit=0

While Not quit
If KeyHit(1) Then quit=1
If KeyDown(200) Then MoveEntity camera,0,0,.1
If KeyDown(208) Then MoveEntity camera,0,0,-.1
If KeyDown(203) Then TurnEntity camera,0,1,0
If KeyDown(205) Then TurnEntity camera,0,-1,0
If KeyHit(57) And found<>1 Then findChildren() ; spacebar to walk through path

TranslateEntity camera,0,-1,0

updateWalker() ; let the walker move

UpdateWorld
RenderWorld
If found=1 Then ; target node has been found
	Text 0,0,"Found target node: "+currentNode
End If
Text 0,10,"Press spacebar to step through the nodes. First node takes two presses of space bar..."
Flip
Delay 10
Wend
End

Function findChildren()
; this function searches the 8 nodes that surround the current node
PositionEntity pivot,currentX,1,currentY ; keep positioning the pivot on current node location
For y=currentY-10 To currentY+10 ; the 10 is because we scaled the world by 10
	For x=currentX-10 To currentX+10
		this.node = getNode(x,y)    ; get the node
		If this <> Null Then        ; is it a real, live node?
			If this\walkable=1 And this\state <> 2 Then ; yes? Is it walkable and not on closed list?
				If this\state=1 Then                    ; Is it already on open list?
					query=isShorterPath(this)           ; yep...so see if we have a shorter path
				End If
					If this\state=0 Then                ; node is not on open list
						this\state=1                    ; so put it there
						this\parent=getNodeParent()     ; record its parent node
						calculateGCost(this)            ; get its G Cost
						calculateHCost(this)            ; get its H Cost
						calculateFCost(this)            ; get its F Cost
						this\number=returnNodeNumber(this) ; record the node's number
						heap(heapCount)=this\fCost         ; put the F Cost on the heap
						nodeheap(heapCount)=this\number    ; put the node's number on another heap
						heapCount=heapCount+1              ; increment counter for next node
					End If
				
			End If
		End If
	Next
Next
sortHeap()  ; sort the heap
End Function

Function isShorterPath%(node.node)
; if path to this already open node is shorter then process it, otherwise ignore it
If Abs(node\x-currentX)=10 And Abs(node\y-currentY)=10 Then 
	tempGCost=14 ; node is diagonal to current node
Else
	tempGCost=10 ; node is horizontal or vertical to current node
End If
For this.node = Each node ; iterate through each node
	If this\x=currentX And this\y=currentY Then
		myGCost=this\gCost+tempGCost ; adding together current node's GCost and child's GCost
	End If
Next
If myGCost<node\gCost Then
; if node's gCost is higher then the total found above, it's a shorter path through current node
	node\state=0
	Return 1
Else
	Return 0 ; nope...not a shorter path
End If
End Function


Function getNode.node(x,y)
; returns the node with the x/y coordinates given
For this.node = Each node
	If this\x=x And this\y=y Then
		Return this
	End If
Next
End Function

Function calculateGCost(this.node)
; calculates the G Cost of the node
If Abs(this\x-currentX)=10 And Abs(this\y-currentY)=10 Then
	this\gCost=14+this\parent\gCost ; node is diagonal to parent
Else
	this\gCost=10+this\parent\gCost ; node is horizontal or vertical to parent
End If
End Function

Function calculateHCost(this.node)
; calculates the heuristic (H) cost. This is nothing but a guess at the distance
this\hCost=(Abs(this\x-targetX)+Abs(this\y-targetY))*10 ; absolute distance multiplied by 10
End Function

Function calculateFCost(this.node)
; add together the G Cost and H Cost of the node
this\fCost=this\gCost+this\hCost
End Function

Function sortHeap()
; sorts the heap so that heap(0) contains the lowest number
; this is just a quick and dirty bubble sort routine
For i=0 To heapCount-1
	For j=0 To heapCount-1
		If heap(j+1)>0 Then ; make sure that the heap isn't at the end
			If heap(j)>heap(j+1) Then ; first heap item is larger than second heap item
				temp=heap(j) ; store first item in temporary variable
				tempn=nodeheap(j) ; store first item in nodeheap in temp variable, as well
				heap(j)=heap(j+1) ; take second item in heap and put it in first position
				nodeheap(j)=nodeheap(j+1) ; same with the nodeheap
				heap(j+1)=temp ; take the item from temp and put it in the heap's second position
				nodeheap(j+1)=tempn ; same with nodeheap
			End If
		End If
	Next
Next
; since we now have a new low number in the heap's first position, let's make it a new parent
getNewParent()
End Function

Function getNewParent()
; this function creates a new parent node
path[pathCount]=currentNode ; store path
pathCount=pathCount+1       ; increment counter
	For this.node = Each node
		; iterate through nodes looking for the node that is in first position in the nodeheap
		If this\number = nodeheap(0) Then
			currentX=this\x ; make currentX equal to this node's x
			currentY=this\y ; make currentY equal to this node's y
			currentNode=this\number ; make sure to record that this node is the current node
			this\state = 2 ; flag this node as "closed"
			; if this node's x/y coordinate are the same as the target's then target is found
			If this\x=targetX And this\y=targetY Then found=1
			heap(0)=heap(heapCount-1) ;since we no longer need this particular heap item, we get
			                          ;rid of it by sticking the last heap item in the first
									  ;position
			nodeheap(0)=nodeheap(heapCount-1) ; same with the nodeheap
			heapCount=heapCount-1             ; and decrementing the counter
			Exit ; we're done here, so let's leave
		End If
	Next
If found=0 Then
;findChildren() ; *************************************
End If
End Function

Function getNodeParent.node()
; returns the node's parent
;If currentNode > 9 Then
;	tempX = currentNode Mod 10
;	tempY = (currentNode-tempX)/10
;Else
;	tempX=currentNode
;	tempY=0
;End If
;For this.node = Each node
;	If this\x=tempX And this\y=tempY Then
;		Return this
;	End If
;Next
 For this.node = Each node
	If this\x=currentX And this\y=currentY Then
		Return this
	End If
 Next
End Function

Function returnNodeNumber%(node.node)
Return node\x+((node\y)*10)
End Function

Function updateWalker()
PointEntity walker,pivot
MoveEntity walker,0,0,.1
End Function

Function readData()
Restore nodeData
Read xVal,yVal
;For y=0 To yVal-1
For y=yVal-1 To 0 Step-1
	For x=0 To xVal-1
		Read grid(x,y)
		node.node=New node
		node\x=x*10:node\y=y*10
		Select grid(x,y)
			Case 0
				; we have a wall here, so let's build it
				node\walkable=0
				cube=CreateCube()
				ScaleEntity cube,5,1,5
				EntityColor cube,50,30,10
				PositionEntity cube,node\x,1,node\y
				EntityType cube,wall_type
			Case 1
				; free areas to walk on
				node\walkable=1
			Case 2
				; this is the start position
				node\walkable=1
				node\state = 2 ; flag it as closed
				currentX=node\x
				currentY=node\y
				currentNode = returnNodeNumber(node)
				node\number=currentNode
				path[pathCount]=node\number
				pathCount=pathCount+1
				walker=CreateSphere()
				EntityColor walker,0,0,255
				EntityShininess walker,1
				PositionEntity walker,node\x,1,node\y
				
			Case 3
				; this is the target
				node\walkable=1
				targetX=node\x
				targetY=node\y
				PositionEntity target,node\x,1,node\y
			Case 4
				; the camera here
				node\walkable=1
				PositionEntity camera,node\x,1,node\y
		End Select
	Next
Next
End Function


.nodeData
Data 10,10
Data 0,0,0,0,0,0,0,0,0,0
Data 0,1,1,1,0,0,1,1,1,0
Data 0,1,2,1,0,1,1,1,1,0
Data 0,1,1,1,0,1,1,1,1,0
Data 0,1,1,1,0,1,1,1,1,0
Data 0,1,1,1,1,1,1,1,1,0
Data 0,1,1,1,0,0,0,0,1,0
Data 0,1,4,1,0,1,1,1,1,0
Data 0,1,1,1,0,1,3,1,1,0
Data 0,0,0,0,0,0,0,0,0,0
