; ID: 3080
; Author: Pineapple
; Date: 2013-09-23 18:15:11
; Title: Generalized Cellular Automata Handler
; Description: Highly flexible ruleset system, example code includes implementations of Conway's Life, HighLife, Fredkin's Automata, Seeds, and Brian's Brain

' 	--+-----------------------------------------------------------------------------------------+--
'	  |   This code was originally written by Sophie Kirschner (sophiek@pineapplemachine.com)   |  
' 	  | It is released as public domain. Please don't interpret that as liberty to claim credit |  
' 	  |   that isn't yours, or to sell this code when it could otherwise be obtained for free   |  
'	  |                because that would be a really shitty thing of you to do.                |
' 	--+-----------------------------------------------------------------------------------------+--



SuperStrict

Import brl.retro ' Only used for tostring() methods, feel free to get rid of them if you don't need to use them.

' Example program

Rem

' Controls:
'	LMB draws "on" cells, RMB draws "off".
'	Number keys 1-5 choose ruleset: Conway's Life, HighLife, Fredkin's Automata, Seeds, Brian's Brain
'	X clears the grid
'	Pressing period or holding space simulates a generation

' Consts for relating the cell grid to the graphics window
Const gw%=512,gh%=512
Const cw%=8,ch%=8

' Define some general rules that'll be used in the various example rulesets
Global B1rule		:rulesetrule=rulesetrule.Create(0,1,[rulesetrulereq.Create(1,1,1,rulesetrulereq.CONDITIONAND)])	' Turn on with 1 on neighbor
Global B2rule		:rulesetrule=rulesetrule.Create(0,1,[rulesetrulereq.Create(1,2,2,rulesetrulereq.CONDITIONAND)])	' Turn on with 2 on neighbors
Global B3rule		:rulesetrule=rulesetrule.Create(0,1,[rulesetrulereq.Create(1,3,3,rulesetrulereq.CONDITIONAND)])	' Turn on with 3 on neighbors
Global B5rule		:rulesetrule=rulesetrule.Create(0,1,[rulesetrulereq.Create(1,5,5,rulesetrulereq.CONDITIONAND)])	' Turn on with 5 on neighbors
Global B6rule		:rulesetrule=rulesetrule.Create(0,1,[rulesetrulereq.Create(1,6,6,rulesetrulereq.CONDITIONAND)])	' Turn on with 6 on neighbors
Global B7rule		:rulesetrule=rulesetrule.Create(0,1,[rulesetrulereq.Create(1,7,7,rulesetrulereq.CONDITIONAND)])	' Turn on with 7 on neighbors
Global S23rule	:rulesetrule=rulesetrule.Create(1,0,[rulesetrulereq.Create(1,2,3,rulesetrulereq.CONDITIONNAND)])	' Turn off with anything but 2 or 3 neighbors
Global Srule		:rulesetrule=rulesetrule.Create(1,0,Null)									' Turn off regardless of neighbors

Global border0%[]=[0,0,0,0]

' John Conway's Game of Life - http://en.wikipedia.org/wiki/Conway's_Game_of_Life
Global ConwaysGOL:ruleset=ruleset.Create(2,border0,ruleset.MooresNeighborhood) ' B3-S23
ConwaysGOL.addrule(B3rule)
ConwaysGOL.addrule(S23rule)

' HighLife - http://en.wikipedia.org/wiki/HighLife
Global HighLife:ruleset=ruleset.Create(2,border0,ruleset.MooresNeighborhood) ' B36-S23
HighLife.addrule(B3rule);HighLife.addrule(B6rule)
HighLife.addrule(S23rule)

' Fredkin's Automata - http://www.rennard.org/alife/english/acintrogb01.html
Global FredkinsAutomata:ruleset=ruleset.Create(2,border0,ruleset.MooresNeighborhood) ' B1357-S02468
FredkinsAutomata.addrule(B1rule);FredkinsAutomata.addrule(B3rule);FredkinsAutomata.addrule(B5rule);FredkinsAutomata.addrule(B7rule)
FredkinsAutomata.addrule(rulesetrule.Create(1,0,[	rulesetrulereq.Create(1,0,0,rulesetrulereq.CONDITIONNAND), ..	' Turn off with anything but 0, 2, 4, 6, or 8 neighbors
								rulesetrulereq.Create(1,2,2,rulesetrulereq.CONDITIONNAND), ..
								rulesetrulereq.Create(1,4,4,rulesetrulereq.CONDITIONNAND), ..
								rulesetrulereq.Create(1,6,6,rulesetrulereq.CONDITIONNAND), ..
								rulesetrulereq.Create(1,8,8,rulesetrulereq.CONDITIONNAND)  ]))

' Seeds - http://en.wikipedia.org/wiki/Seeds_(cellular_automaton)
Global Seeds:ruleset=ruleset.Create(2,border0,ruleset.MooresNeighborhood) ' B2-S
Seeds.addrule(B2rule)
Seeds.addrule(Srule)

' Brian's Brain - http://en.wikipedia.org/wiki/Brian's_Brain
Global BriansBrain:ruleset=ruleset.Create(3,border0,ruleset.MooresNeighborhood) ' B2-S with a third "refractory" state
BriansBrain.addrule(B2rule)
BriansBrain.addrule(rulesetrule.Create(1,2,Null))											' Turn on to refractory unconditionally
BriansBrain.addrule(rulesetrule.Create(2,0,Null))											' Turn refractory to off unconditionally

' graphics window
Graphics gw,gh

' render the cells with these colors
Global cellcolors%[][]=[[0,0,0],[255,255,255],[0,0,255]]

' cellular automata object containing a grid of cells
Global cells:automata=automata.Create(gw/cw,gh/ch,ConwaysGOL)

' main loop
Repeat
	Cls
	
	' draw on grid with mouse - left mouse turns on, right mouse turns off
	Local mx%=MouseX()/cw,my%=MouseY()/ch
	mx=Max(0,Min(cells.width-1,mx));my=Max(0,Min(cells.height-1,my))
	If MouseDown(1) Then
		cells.setcell(mx,my,1)
	ElseIf MouseDown(2)
		cells.setcell(mx,my,0)
	EndIf
	
	' clear grid on pressing x
	If KeyHit(key_x) Then cells.clearcells
	
	' switch ruleset with number keys
	If KeyHit(key_1) Then cells.rules=ConwaysGOL
	If KeyHit(key_2) Then cells.rules=HighLife
	If KeyHit(key_3) Then cells.rules=FredkinsAutomata
	If KeyHit(key_4) Then cells.rules=Seeds
	If KeyHit(key_5) Then cells.rules=BriansBrain
	
	' draw it
	SetColor 255,255,0
	For Local i%=0 Until cells.width
		For Local j%=0 Until cells.height
			Local c%=cells.getcell(i,j)
			SetColor cellcolors[c][0],cellcolors[c][1],cellcolors[c][2]
			DrawRect i*cw,j*ch,cw,ch
		Next
	Next
	
	' update when pressing period or holding down space
	If KeyDown(key_space) Or KeyHit(key_period) Then cells.update
	
	Flip
	If KeyDown(27) Or AppTerminate() Then End
Forever

EndRem



' Cellular automata grid type
Type automata
	Field width%,height%	' Dimensions of the cell grid
	Field grid%[][]		' Actual cell grid
	Field buffer%[][]		' Buffer for the cell grid, gets important when updating
	Field rules:ruleset		' Defines the actual ruleset to be used
	' Create a new automata object
	Function Create:automata(w%,h%,rules:ruleset)
		Local n:automata=New automata
		n.setsize w,h
		n.rules=rules
		Return n
	End Function
	' Set cell grid size
	Method setsize(w%,h%)
		width=w;height=h
		grid=New Int[][w]
		buffer=New Int[][w]
		For Local i%=0 Until w
			grid[i]=New Int[h]
			buffer[i]=New Int[h]
		Next
	End Method
	' Get the cell at a coord
	Method getcell%(x%,y%)
		Assert grid
		Assert x>=0 And y>=0 And x<width And y<height
		Return grid[x][y]
	End Method
	' Set the cell at a coord
	Method setcell(x%,y%,element%)
		Assert grid
		Assert x>=0 And y>=0 And x<width And y<height
		grid[x][y]=element
	End Method
	' Clear all cells to a value
	Method clearcells(element%=0)
		Assert grid
		For Local i%=0 Until width
			For Local j%=0 Until height
				grid[i][j]=element
			Next
		Next
	End Method
	' Update the simulation
	Method update()
		Assert grid And buffer
		' Iterate through all cells
		For Local i%=0 Until width
			Assert i<grid.length
			For Local j%=0 Until height
				Assert j<grid[i].length
				Assert grid[i][j]<rules.rules.length
				Local rulesarray:rulesetrule[]=rules.rules[grid[i][j]]
				buffer[i][j]=grid[i][j]
				' Do nothing if no rules apply directly to this cell state
				If rulesarray And rulesarray.length Then
					Local neighbors%[rules.elements]
					Assert rules.neighborhood
					' Iterate through all the cells in this one's neighborhood and tally up the numbers of each cell state
					For Local coord%[]=EachIn rules.neighborhood
						Local nx%=i+coord[0]
						Local ny%=j+coord[1]
						If nx<0
							neighbors[rules.border[rules.BORDERWEST]]:+1
						ElseIf nx>=width
							neighbors[rules.border[rules.BORDEREAST]]:+1
						ElseIf ny<0
							neighbors[rules.border[rules.BORDERNORTH]]:+1
						ElseIf ny>=height
							neighbors[rules.border[rules.BORDERSOUTH]]:+1
						Else
							neighbors[grid[nx][ny]]:+1
						EndIf
					Next
					' Iterate through all rules which apply to this cell state
					For Local r:rulesetrule=EachIn rulesarray
						Local allreqs%=1
						Assert r.reqs
						' Iterate through all the rule's rulereqs
						For Local req:rulesetrulereq=EachIn r.reqs
							Local atleast%=(req.at_least=-1) Or (neighbors[req.element]>=req.at_least)
							Local atmost%=(req.at_most=-1) Or (neighbors[req.element]<=req.at_most)
							If Not	((req.condition=rulesetrulereq.CONDITIONAND And (atleast And atmost)) Or ..		' AND conditional
								 (req.condition=rulesetrulereq.CONDITIONNAND And Not (atleast And atmost)) Or ..	' NAND conditional
								 (req.condition=rulesetrulereq.CONDITIONOR And (atleast Or atmost)) Or ..			' OR conditional
								 (req.condition=rulesetrulereq.CONDITIONNOR And Not (atleast Or atmost)) Or ..		' NOR conditional
								 (req.condition=rulesetrulereq.UNCONDITIONAL)) Then						' unconditional
								allreqs=0;Exit
							EndIf
						Next
						' Only apply the rule (change the cell to the result) if all of the rulereqs are satisfied
						If allreqs Then
							buffer[i][j]=r.result
							Exit
						EndIf
					Next
				EndIf
			Next
		Next
		' Swap the main grid of cells for the new one that just got put in the buffer
		Local t%[][]=grid
		grid=buffer;buffer=t
	End Method
End Type

' Cellular automata ruleset type
Type ruleset
	' Frequently-used neighborhoods for convenience
	Global VonNeumannNeighborhood%[][]=[[-1,0],[1,0],[0,-1],[0,1]]
	Global MooresNeighborhood%[][]=[[-1,-1],[-1,0],[-1,1],[0,-1],[0,1],[1,-1],[1,0],[1,1]]
	Field elements%=2								' Number of elements the ruleset deals with
	Field border%[] 								' Composition of borders when they get considered as neighbors
	Field neighborhood%[][]							' Neighborhood; see this type's globals for examples
	Field rules:rulesetrule[][]						' Array containing an actual set of rules
	Const BORDEREAST%=0,BORDERSOUTH%=1,BORDERWEST%=2,BORDERNORTH%=3	' Constants for use with the border[] array
	' Create a new ruleset object
	Function Create:ruleset(elements%,border%[],neighborhood%[][])
		Local n:ruleset=New ruleset
		n.elements=elements
		n.border=border
		n.neighborhood=neighborhood
		n.rules=New rulesetrule[][n.elements]
		Return n
	End Function
	' Add a rulesetrule to the ruleset's array of rules
	Method addrule(r:rulesetrule)
		Local length%
		If Not rules[r.element] Then
			length=0
			rules[r.element]=New rulesetrule[1]
		Else
			length=rules[r.element].length
			rules[r.element]=rules[r.element][..length+1]
		EndIf
		rules[r.element][length]=r
	End Method
	' Useful for debugging, since rulesets can be a little esoteric
	Method tostring$() 
		Local str$=""
		For Local rulesarray:rulesetrule[]=EachIn rules
			Local rstr$=""
			For Local rule:rulesetrule=EachIn rulesarray
				rstr:+rule.tostring()+"; "
			Next
			rstr=Left(rstr,rstr.length-2)
			If rstr Then str:+rstr+"~n"
		Next
		Return Left(str,str.length-1)
	End Method
End Type

' Cellular automata rule type; rulesets contain these
Type rulesetrule
	Field element%			' Cell state that the rule gets applied to
	Field result%				' State that the cell becomes if all the rule's requirements ("reqs") are met
	Field reqs:rulesetrulereq[]	' Array containing a set of requirements ("reqs") to be met
	' Creates a new rulesetrule object
	Function Create:rulesetrule(element%,result%,reqs:rulesetrulereq[])
		Local n:rulesetrule=New rulesetrule
		n.element=element
		n.result=result
		n.reqs=reqs
		Return n
	End Function
	' Useful for debugging, since rulesets can be a little esoteric
	Method tostring$() 
		Local str$="Element: "+element+", Result: "+result+": "
		For Local req:rulesetrulereq=EachIn reqs
			str:+"("+req.tostring()+") AND "
		Next
		If reqs.length Then str=Left(str,str.length-5)
		Return str
	End Method
End Type

' Cellular automata rule requirement type; rulesetrules contain these, and for a rule to take effect all of its rulereqs must be satisfied
Type rulesetrulereq
	Field element%			' Which cell state is being counted from one cell's neighbors
	Field at_least%=-1			' The minimum number of neighboring cells of the specified state; -1 is analogous to "At least zero"
	Field at_most%=-1			' The maximum number of neighboring cells of the specified state; -1 is analogous to "At most [highest possible number of neighbors]"
	Field condition%=CONDITIONAND	' Important! Logical operators for whether the min AND max should be met, min OR max, min NAND max, min NOR max, or whether the requirement is just bollocks and "UNCONDITIONAL"
	Const CONDITIONAND%=0,CONDITIONOR%=1,CONDITIONNAND%=2,CONDITIONNOR%=3,UNCONDITIONAL%=4
	' Creates a new rulesetrulereq object
	Function Create:rulesetrulereq(element%,at_least%=-1,at_most%=-1,condition%=CONDITIONAND)
		Local n:rulesetrulereq=New rulesetrulereq
		n.element=element
		n.at_least=at_least
		n.at_most=at_most
		n.condition=condition
		Return n
	End Function
	' Useful for debugging, since rulesets can be a little esoteric
	Method tostring$() 
		If condition=UNCONDITIONAL Then Return "UNCONDITIONAL"
		Local cond$[]=["AND","OR","NAND","NOR"]
		Local least$="[1]"
		If at_least>=0 Then least="At least "+at_least
		Local most$="[1]"
		If at_most>=0 Then most="At most "+at_most
		Return least+" "+cond[condition]+" "+most
	End Method
End Type
