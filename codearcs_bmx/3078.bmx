; ID: 3078
; Author: Pineapple
; Date: 2013-09-22 22:48:41
; Title: Generalized A* Pathfinding
; Description: Pathfinder readily adaptable to pretty much any rectangular 2D grid

' 	--+-----------------------------------------------------------------------------------------+--
'	  |   This code was originally written by Sophie Kirschner (sophiek@pineapplemachine.com)   |  
' 	  | It is released as public domain. Please don't interpret that as liberty to claim credit |  
' 	  |   that isn't yours, or to sell this code when it could otherwise be obtained for free   |  
'	  |                because that would be a really shitty thing of you to do.                |
' 	--+-----------------------------------------------------------------------------------------+--



SuperStrict

Import pine.Heap ' http://blitzbasic.com/codearcs/codearcs.php?code=2970 MIRROR: https://dl.dropboxusercontent.com/u/10116881/blitz/code/heap.bmx
Import brl.linkedlist
Import brl.math

' GetAstarPath is a generalized A* pathfinder that plays nice with rectangular grids.
' It returns a list of pathnode objects that are sequential from the start coords to the goal coords. If no path could be found, returns null.

' Mandatory arguments:
' startx		- 	Starting X coord
' starty		- 	Starting Y coord
' goalx		-	Goal X coord
' goaly		-	Goal Y coord
' width		-	Width of the grid space. (e.g. width of the array containing tile data)
' height		-	Height of the grid space. (e.g. height of the array containing tile data)
' getcost(x,y)	-	Function to use for calculating cost to traverse a tile. Impassable tiles should return a cost of -1.

' Optional arguments:
' limitsteps				-	If the function makes this many iterations without reaching the goal, disregard and return null. (Defaults to -1, which means don't stop.)
' heuristic(node,goalx,goaly)	-	Function to use for calculating the heuristic from a pathnode object to the goal. Default adds actual and manhattan distances.
' neighborhood[][]			-	An array of tuples that tells the pathfinder where something can move from one tile, and the cost multiplier for doing so. Defaults to a Von Neumann neighborhood with all costs *1. (In other words, defaults to checking only directly adjacent spaces.)

Global _astar_VonNeumannNeighborhood%[][]=[[-1,0,1],[1,0,1],[0,-1,1],[0,1,1]]

Function GetAstarPath:TList(startx%,starty%,goalx%,goaly%,width%,height%,getcost%(x%,y%),limitsteps%=-1,heuristic%(node:pathnode,goalx%,goaly%)=_astar_defheuristic,neighborhood%[][]=Null)
	Return pathtolist(GetAstarPathNode(startx,starty,goalx,goaly,width,height,getcost,limitsteps,heuristic,neighborhood))
End Function

Function GetAstarPathNode:pathnode(startx%,starty%,goalx%,goaly%,width%,height%,getcost%(x%,y%),limitsteps%=-1,heuristic%(node:pathnode,goalx%,goaly%)=_astar_defheuristic,neighborhood%[][]=Null)
	If Not neighborhood Then neighborhood=_astar_VonNeumannNeighborhood
	Local start:pathnode=pathnode.Create(startx,starty,Null,goalx,goaly,0,heuristic)
	Local nodeat:pathnode[width,height]
	Local open:THeap=CreateHeap(0,1,6)
	HeapInsert(open,start)
	Local iterations%=0
	Repeat
		Local curr:pathnode=pathnode(HeapRemove(open))
		If Not curr Exit
		If Not curr.open Then Continue
		If curr.x=goalx And curr.y=goaly Then Return curr
		curr.open=0
		nodeat[curr.x,curr.y]=curr
		For Local i%=0 Until neighborhood.length
			Local nx%=curr.x+neighborhood[i][0]
			Local ny%=curr.y+neighborhood[i][1]
			If nx<0 Or ny<0 Or nx>=width Or ny>=height Then Continue
			Local costhere%=getcost(nx,ny)
			If costhere=-1 Then Continue
			costhere:*neighborhood[i][2]
			If nodeat[nx,ny] And nodeat[nx,ny].open=0 And curr.gscore+costhere>nodeat[nx,ny].gscore Continue
			If (nodeat[nx,ny]=Null)
				Local n:pathnode=pathnode.Create(nx,ny,curr,goalx,goaly,costhere,heuristic)
				HeapInsert open,n
			ElseIf (nodeat[nx,ny].gscore>curr.gscore+costhere) Then
				nodeat[nx,ny].from=curr
				nodeat[nx,ny].gscore=curr.gscore+costhere
				nodeat[nx,ny].fscore=nodeat[nx,ny].gscore+nodeat[nx,ny].heuristic(nodeat[nx,ny],goalx,goaly)
				HeapInsert open,nodeat[nx,ny];nodeat[nx,ny].open=0
			EndIf
		Next
		iterations:+1
		If limitsteps>=0 And iterations>limitsteps Then Return Null
	Forever
	Return Null
End Function

Function pathtolist:TList(lastnode:pathnode)
	Local list:TList=CreateList()
	Local on:pathnode=lastnode
	While on
		list.addfirst on
		on=on.from
	Wend
	Return list
End Function

Function _astar_defheuristic%(node:pathnode,goalx%,goaly%)
	Local dx%=Abs(node.x-goalx)
	Local dy%=Abs(node.y-goaly)
	Local dist%=Sqr((dx*dx)+(dy*dy))
	Local h%=(dx+dy)
	Return h+dist
End Function

Type pathnode
	Field x%,y%
	Field gscore%=0
	Field fscore%=0
	Field from:pathnode=Null
	Field open%=1
	Field heuristic%(node:pathnode,goalx%,goaly%)
	Function Create:pathnode(x%,y%,from:pathnode,goalx%,goaly%,cost%,heuristic%(node:pathnode,goalx%,goaly%))
		Local n:pathnode=New pathnode
		n.x=x;n.y=y
		n.from=from
		If from Then n.gscore=from.gscore+cost
		n.heuristic=heuristic
		n.fscore=n.gscore+n.heuristic(n,goalx,goaly)
		Return n
	End Function
	Method compare%(o2:Object)
		Local o:pathnode=pathnode(o2)
		If fscore>o.fscore 
			Return 1
		ElseIf fscore<o.fscore 
			Return -1
		Else
			Return 0
		EndIf
	End Method
	Method lastnode:pathnode()
		Local node:pathnode=Self
		While node.from
			node=node.from
		Wend
		Return node
	End Method
End Type




' Example program

Rem

' stuff for handling the map and the graphics window
Const gw%=256,gh%=256
Const tw%=8,th%=8
Const mw%=gw/tw,mh%=gh/th
Global map%[mw,mh]

' actor class. these guys demonstrate the pathfinder by getting commanded around.
Type actor
	Global list:TList=CreateList(),count%=0
	Global cols%[][]=[[255,0,0],[0,255,0]]
	Field x%,y%,col%
	Field path:TList,pathx%=-1,pathy%=-1
	Function handle()
		For Local a:actor=EachIn list
			a.drawpath
		Next
		For Local a:actor=EachIn list
			a.draw
			a.update
		Next
	End Function
	Function add:actor(x%,y%,col%)
		Local a:actor=New actor
		a.x=x;a.y=y;a.col=col
		list.addlast a;count:+1
		Return a
	End Function
	Method pathto(goalx%,goaly%)
		path=GetAstarPath(x,y,goalx,goaly,mw,mh,getmapcost)',mw*mh)
		If path Then pathx=goalx;pathy=goaly
	End Method
	Method draw()
		SetColor cols[col][0],cols[col][1],cols[col][2]
		DrawRect x*tw,y*th,tw,th
	End Method
	Method drawpath()
		If Not path Then Return
		SetColor cols[col][0]/4,cols[col][1]/4,cols[col][2]/4
		For Local node:pathnode=EachIn path
			DrawRect node.x*tw,node.y*th,tw,th
		Next
	End Method
	Method update()
		If path Then
			Local node:pathnode=pathnode(path.first())
			If node Then
				If map[node.x,node.y] And (pathx>=0 And pathy>=0) Then
					pathto pathx,pathy
					If Not path Then pathx=-1;pathy=-1
				Else
					x=node.x;y=node.y
					path.removefirst
				EndIf
			Else
				path=Null;pathx=-1;pathy=-1
			EndIf
		EndIf
	End Method
End Type

' this is for the getcost(x,y) argument of GetAstarPath
Function getmapcost%(x%,y%)
	If map[x,y] Return -1
	Return 1
End Function

' make the graphics window and set up some misc. globals
Graphics gw,gh
Global drawmode%=-1
Global commandactor%=0
Global clicked%=0

' make the actors
actor.add 2,2,0
actor.add mw-3,mh-3,1

' main loop
Repeat
	Cls
	
	' get mouse position on the map grid
	Local mx%=MouseX()/tw,my%=MouseY()/th
	
	' handle drawing with left click
	If MouseDown(1) And (mx>=0 And my>=0 And mx<mw And my<mh) Then
		If drawmode=-1 Then drawmode=Not map[mx,my]
		map[mx,my]=drawmode
		clicked=1
	Else
		drawmode=-1
	EndIf
	
	' handle commanding actors with right click
	If MouseHit(2) Then
		For Local a:actor=EachIn actor.list
			If a.col=(commandactor Mod actor.count) Then
				a.pathto mx,my
				Exit
			EndIf
		Next
		commandactor:+1
		clicked=1
	EndIf
	
	' draw the map grid
	SetColor 255,255,255
	For Local i%=0 Until mw
		For Local j%=0 Until mh
			If map[i,j] Then DrawRect i*tw,j*th,tw,th
		Next
	Next
	
	' update and draw the actors
	actor.handle
	
	' draw a little instructional message that disappears once you click somewhere
	SetColor 255,255,255
	If Not clicked Then
		DrawText "LMB to draw/erase walls.",2,230
		DrawText "RMB to pathfind.",2,242
	EndIf
	
	Flip
	Delay 20
Forever

EndRem
