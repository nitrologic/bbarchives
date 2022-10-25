; ID: 1924
; Author: Vic 3 Babes
; Date: 2007-02-14 18:44:46
; Title: Pac Man
; Description: Simple pathfinding in this Deluxe Pac Man example

;Harlequin Software 2007

;#######################################
;REMEBER TO KEEP NUM_MAPS IN SYNC WITH ANY YOU ADD REMOVE!!
;The game loops back to level 1 when you get to the end - could write some code
;to reduce the amount of time that gosts are scared SCARED_TIME every time the levels loop

;CONSTANTS YOU CAN EASILY CHANGE
Const SCARED_TIME=350	;number of frames ghosts should be scared after Pac eats powerpill
Const NUM_LIVES=3		;drawing more than 3 will overwrite help-text on screen
Const NUM_MAPS=11		;if you also have the level editor
Const TIMEWARP=16		;number of frames ghosts spend in warp tunnel - 16 allows pac man to get an extra square
						;away from them - but if you start to put it too high, then pathfinding will be affected
						;because would need to take into account time taken to warp - YOU'LL HAVE TO ADJUST THE
						;RELEVANT CODE IN MOVE_GHOSTS() IF YOU WANT A TIMEWARP OF ZERO.  Oh, Rocky!
Const VWAIT_DELAY=10	;amount to delay before asking if vertical blank - don't put it higher
;than the time between monitor refreshes i.e.
;At 60hz refresh rate - monitor refreshes every 1000/60 = 16.666 millisecs - so 14 is probably safe - but no higher
;it helps to keep CPU usage down - Ctrl-Alt-Del - Performance Tab
;You could take the Vwait out and do a flip false on a timer if you prefer - game is probably too fast at more than 60hz

;Don't change any other constants unless modifying the code
Const DIRECTIONS$="NE S   W      P"
Const NORTH=1, EAST=2, SOUTH=4, WEST=8
Const GHOSTPEN=$10, PILL=$20, POWERPILL=$40, ABOVEPEN=$80
Const ANY_PILLS=$60

Const NOT_NORTH=$E, NOT_EAST=$D, NOT_SOUTH=$B, NOT_WEST=$7

Const NORTHEAST=$3, SOUTHEAST=$6, SOUTHWEST=$C, NORTHWEST=$9
;used for ghost pen pathfinding
Const PENSHIFT=8
Const PEN_NORTH = NORTH Shl PENSHIFT
Const PEN_EAST  = EAST  Shl PENSHIFT
Const PEN_SOUTH = SOUTH Shl PENSHIFT
Const PEN_WEST  = WEST  Shl PENSHIFT
Const PAVED=$F00, NOT_PAVED=$FF0FF

Const HORIZONTAL=EAST Or WEST, VERTICAL = NORTH Or SOUTH

Const NO_PILLS=$FFF9F
;used for Pac Man pathfinding
Const PACSHIFT=12
Const PAC_NORTH = NORTH Shl PACSHIFT
Const PAC_EAST  = EAST  Shl PACSHIFT
Const PAC_SOUTH = SOUTH Shl PACSHIFT
Const PAC_WEST  = WEST  Shl PACSHIFT
Const PACPAVED=$F000, NOT_PACPAVED=$3F0FFF

Const WARP=$10000
Const ILLEGAL=$20000
;used in do_paths()
Const NORTHorPACPAVEDorILLEGAL = NORTH Or PACPAVED Or ILLEGAL
Const EASTorPACPAVEDorILLEGAL  = EAST Or PACPAVED Or ILLEGAL
Const SOUTHorPACPAVEDorILLEGAL = SOUTH Or PACPAVED Or ILLEGAL
Const WESTorPACPAVEDorILLEGAL  = WEST Or PACPAVED Or ILLEGAL
;bits for splitting up
Const SPLITSHIFT=18
Const SPLIT_NORTH = NORTH Shl SPLITSHIFT
Const SPLIT_EAST  = EAST  Shl SPLITSHIFT
Const SPLIT_SOUTH = SOUTH Shl SPLITSHIFT
Const SPLIT_WEST  = WEST  Shl SPLITSHIFT
Const SPLIT_PAVED = $3C0000, NOT_PACSPLITPAVED=$30FFF

Const RIGHTARROW=205, LEFTARROW=203, UPARROW=200, DOWNARROW=208
Const F5=63, F9=67, F10=68, ENTER=28

Const PACSTARTX=176, PACSTARTY=160
Const STATIC=0

;ghost states
Const SCARED=1, NORMAL=2, PENNED=4, CHOMPED=$10

Const QUITTED=1, DIED=2, COMPLETED=4, ESCAPED=8, EXITMAINLOOP=$F, ESCAPEHIT=9

Dim level(11,9)
;pacx and pacy are screen co-ordinates - px and py are array co-ordinates that get calculated
;as soon as pacx or pacy change becasue they are used in so many places - better than using px
;every time we need the array position - which is quite a lot
Global pacx, pacy, px, py, pacdir, pacframe, mapnum
Global lives, score, oldscore, ghostschomped, bonus
Global wallcolour, framecount, action
Global numpills, numpowers, leveltotal, levelmax, maxmultiplier
Global staticcount
Global show_paths, collisionoff
Global titletext$, titles

AppTitle "Pac Man"
Graphics 384,320

Global pacman=CreateImage(22,22)
Global spooks=CreateImage(22,22,4)
Global Hwalls=CreateImage(26,4,6)
Global Vwalls=CreateImage(4,26,6)
Global eyes=CreateImage(6,6)
Global pillpics=CreateImage(8,8,8)
Global bg=CreateImage(22,22,3)
Global pacbg=CreateImage(22,22)
Global cover=CreateImage(32,23)
Global scoreboard=CreateImage(150,28)

HandleImage pacman,-5,-5
HandleImage pacbg,-5,-5
HandleImage spooks,-5,-5
HandleImage bg,-5,-5

Type square	;used for flood-fill pathfinding
	Field x, y
End Type

Type ghost
	Field x, y, direction, state, frame, num, xadd, yadd
	Field safe, chompcount, warpcount, scarecount, pencount
	Field warping
End Type

setup_ghosts()
create_tiles()
SetBuffer BackBuffer()
wallcolour=-1: mapnum=1
Repeat
	Cls
	action=False
	Restore titlescreen
	For titles=0 To 17
		If titles=7 Then Color 255,255,0
		Read titletext$
		Text 192*(titles<9),16+(titles Shl 4),titletext$,titles<9,False
	Next
	DrawBlock scoreboard,0,0
	Flip
	Color 255,255,255
	FlushKeys()
	Repeat
		Delay 1
		If KeyHit(QUITTED) Then action=QUITTED
	Until KeyHit(ENTER) Or action
	If action<>QUITTED Then new_game()
	While ((action And ESCAPEHIT)=False) And (lives>0)
		new_level()
		Delay 1000
		Repeat
			If framecount<15 Then framecount=framecount+1 Else framecount=0
			If KeyHit(F10) Then show_paths=1
			If KeyHit(F9) Then show_paths=2
			move_pacman()
			move_ghosts()
			If Not(collisionoff) Then check_collided()
			draw_game()
			If KeyHit(QUITTED) Then action=ESCAPED
			If KeyHit(F5) Then collisionoff=Not(collisionoff)
		Until action And EXITMAINLOOP
		If action=DIED
			dead()
		Else
			If action=COMPLETED
				score=score+leveltotal
				score=score+(500 * lives)
				If leveltotal=levelmax
					score=score+(1000 Shl maxmultiplier)
					maxmultiplier=maxmultiplier+1
				Else
					maxmultiplier=0
				EndIf
			EndIf
		EndIf
	Wend
Until action=QUITTED
End
;####################################
Function check_collided()
If action=COMPLETED Then Return
	For ghosts.ghost=Each ghost
		If RectsOverlap(pacx+9,pacy+9,14,14,ghosts\x+9,ghosts\y+9,14,14)
			Select ghosts\state
				Case SCARED
					ghostschomped=ghostschomped+1
					leveltotal=leveltotal+(100 Shl ghostschomped)
					If ghostschomped=3 Then ghostschomped=0
					ghosts\state=CHOMPED
					ghosts\x=ghosts\x And $FFC	;align to 4 pixels for new movement rate
					ghosts\y=ghosts\y And $FFC
					ghosts\scarecount=0
					ghosts\xadd=4
					ghosts\yadd=4
					ghosts\safe=True
					If ghosts\warping
						ghosts\warping=False
						ghosts\warpcount=0
					EndIf
				Case NORMAL
					If ghosts\warping=False Then action=DIED
			End Select
		EndIf
	Next
Return
End Function
;####################################
Function create_tiles()
Local r, g, b, index
SetBuffer ImageBuffer(pacman)
	Color 255,255,0
	Oval 0,0,22,22
Restore colours
For index=0 To 3
	Read r, g, b
	SetBuffer ImageBuffer(spooks,index)
	Color r, g, b
	Oval 0,0,22,22
	Rect 0,11,22,11
Next
	Color 0,8,0
	Oval 2,2,18,18
	Rect 2,11,18,9
For index=0 To 5
	Read r,g,b
	SetBuffer ImageBuffer(Hwalls,index)
	ClsColor r,g,b
	Cls
	Color r*1.5,g*1.5,b*1.5
	Line 0,2,25,2
	Color r*2,g*2,b*2
	Line 0,1,25,1
	SetBuffer ImageBuffer(Vwalls,index)
	ClsColor r,g,b
	Cls
	Color r*1.5,g*1.5,b*1.5
	Line 1,0,1,25
	Color r*2,g*2,b*2
	Line 2,0,2,25
	SetBuffer ImageBuffer(pillpics,index)
	Color r,g,b
	Oval 0,0,8,8
	Color r*1.5,g*1.5,b*1.5
	Oval 1,1,6,6
	Color r*2,g*2,b*2
	Rect 4,2,2,2
	WritePixel 5,2,$FFFFFF
Next
SetBuffer ImageBuffer(pillpics,6)
	Color 255,255,255
	Oval 2,2,4,4
SetBuffer ImageBuffer(pillpics,7)
	Color 255,255,0
	Oval 0,0,8,8
SetBuffer ImageBuffer(eyes)
	Color 255,255,255
	Oval 0,0,6,6
	Color 0,8,0
	Rect 2,2,2,2
SetBuffer ImageBuffer(scoreboard)
	Color 0,255,0
Return
End Function
;####################################
Function dead()
	Delay 500
	;PlaySound(diedsnd)
	draw_walls()
	Flip False
	DrawImage pacman,pacx,pacy
	For framecount=1 To 11
		DrawBlockRect pacbg,pacx,pacy,0,0,22,framecount Shl 1
		Flip False
		Delay 100
	Next
	score=score+leveltotal
	lives=lives-1: leveltotal=0: maxmultiplier=0
Return
End Function
;####################################
Function do_paths()
Local x, y, numsquares, remove
	;remove=NOT_PACSPLITPAVED Else remove=NOT_PACPAVED
	For y=1 To 8
		For x=0 To 11
			level(x,y)=level(x,y) And NOT_PACPAVED	;lose old path info
		Next
	Next
	;When pacman is half-in one square and half in another - change the start position
	;of the path to the tile he is moving into - this function called every 16 pixels moved
	Select pacdir
		Case NORTH	ty=(pacy+(pacy And $10)) Shr 5: tx=pacx Shr 5
		Case EAST	tx=(pacx+(pacx And $10)) Shr 5: ty=pacy Shr 5
		Case SOUTH	ty=(pacy-(pacy And $10)) Shr 5: tx=pacx Shr 5
		Case WEST	tx=(pacx-(pacx And $10)) Shr 5: ty=pacy Shr 5
		Default
			tx=pacx Shr 5: ty=pacy Shr 5
	End Select
	level(tx,ty)=level(tx,ty) Or PACPAVED	;temporarily set these bits in pac's square so not affected below
	squares.square=New square
	squares\x=tx
	squares\y=ty
	numsquares=1
	Repeat
		squares.square=First square
		x=squares\x: y=squares\y
		Delete squares
		numsquares=numsquares-1
		;test north
		If (level(x,y-1) And SOUTHorPACPAVEDorILLEGAL)=False
		;looks really long but is really just If (level(x,y) and $60004)=False
		;which means wall south, already paved or outside play area
			level(x,y-1)=level(x,y-1) Or PAC_SOUTH
			squares.square=New square
			squares\x=x
			squares\y=y-1
			numsquares=numsquares+1
		EndIf
		;test west
		If x>0
			If (level(x-1,y) And EASTorPACPAVEDorILLEGAL)=False
				level(x-1,y)=level(x-1,y) Or PAC_EAST
				squares.square=New square
				squares\x=x-1
				squares\y=y
				numsquares=numsquares+1
			EndIf
		Else	;warp exit
			If level(x,y) And WARP
				If (level(11,4) And PACPAVED)=False
					level(11,4)=level(11,4) Or PAC_EAST
					squares.square=New square
					squares\x=11
					squares\y=4
					numsquares=numsquares+1
				EndIf
			EndIf
		EndIf
		;test south
		If (level(x,y+1) And NORTHorPACPAVEDorILLEGAL)=False
			level(x,y+1)=level(x,y+1) Or PAC_NORTH
			squares.square=New square
			squares\x=x
			squares\y=y+1
			numsquares=numsquares+1
		EndIf
		;test east
		If x<11
			If (level(x+1,y) And WESTorPACPAVEDorILLEGAL)=False
				level(x+1,y)=level(x+1,y) Or PAC_WEST
				squares.square=New square
				squares\x=x+1
				squares\y=y
				numsquares=numsquares+1
			EndIf
		Else	;warp exit
			If level(x,y) And WARP
				If (level(0,4) And PACPAVED)=False
					level(0,4)=level(0,4) Or PAC_WEST
					squares.square=New square
					squares\x=0
					squares\y=4
					numsquares=numsquares+1
				EndIf
			EndIf
		EndIf
	Until numsquares=0
Return
End Function
;####################################
Function draw_game()
Local x, y, tile, xpos, ypos, eyenum, pathdir, eyex, eyey
	DrawImage pacman,pacx,pacy
	Select pacdir
		Case NORTH	DrawImage eyes,pacx+9,pacy+9
		Case EAST	DrawImage eyes,pacx+17,pacy+9
		Case SOUTH	DrawImage eyes,pacx+9,pacy+17
		Case WEST	DrawImage eyes,pacx+9,pacy+9
		Case STATIC DrawImage eyes,pacx+9,pacy+9
					DrawImage eyes,pacx+17,pacy+9
	End Select
	For ghosts.ghost=Each ghost
		If Not(ghosts\warping)
			If ghosts\state < CHOMPED Then DrawImage spooks,ghosts\x,ghosts\y,ghosts\frame
			DrawImage eyes,ghosts\x+9,ghosts\y+10
			DrawImage eyes,ghosts\x+17,ghosts\y+10
		EndIf
	Next
	If Not(action=died)	;cover side exits with a black tile
		DrawBlock cover,0,132
		DrawBlock cover,350,132
	EndIf
	If (score+leveltotal)>oldscore Then draw_score()
	If show_paths
		For y=1 To 9
			For x=0 To 11
				If Not(level(x,y) And ILLEGAL)
					If show_paths=1
						pathdir=(level(x,y) And PACPAVED) Shr PACSHIFT
					Else
						pathdir=(level(x,y) And PAVED) Shr PENSHIFT
					EndIf
					Text x Shl 5,y Shl 5,Mid$(DIRECTIONS$,pathdir,1)
				EndIf
			Next
		Next
	EndIf
	Delay VWAIT_DELAY
	Flip
	If show_paths
		FlushKeys()
		WaitKey()
		show_paths=False
		draw_walls()
	EndIf
Return
End Function
;####################################
Function draw_score()
	oldscore=(score+leveltotal)
	SetBuffer ImageBuffer(scoreboard)
	Cls
	Text 0,0,"Score: "+oldscore
	Text 0,14,"Level: "+(mapnum-1)+"  MMx"+maxmultiplier
	SetBuffer BackBuffer()
	DrawBlock scoreboard,0,0
Return
End Function
;####################################
Function draw_walls()
Local x, y
Cls
For y=1 To 9
	ypos=y Shl 5
	For x=1 To 11
		DrawBlock pillpics,(x Shl 5)-4,ypos-4,wallcolour
	Next
Next
For y=1 To 8
	ypos=y Shl 5
	For x=1 To 10
		tile=level(x,y)
		xpos=x Shl 5
		If tile And NORTH Then DrawBlock Hwalls,xpos+3,ypos-2,wallcolour
		If tile And WEST Then DrawBlock Vwalls,xpos-2,ypos+3,wallcolour
		If tile And ANY_PILLS Then DrawBlock pillpics,xpos+12,ypos+12,((tile Shr 6) And 1)+6
	Next
Next
For x=1 To 10
	DrawBlock Hwalls,(x Shl 5)+3,286,wallcolour
Next
For y=1 To 8
	If tile And EAST Then DrawBlock Vwalls,350,(y Shl 5)+3,wallcolour
Next
For x=1 To lives
	DrawBlock pacman,2+((x-1) * 25),290
	DrawImage eyes,19+((x-1) * 25),299
Next
DrawBlock cover,176,120
DrawBlock cover,350,133
draw_score()
Text 200,0,"F5  - collision on/off"
Text 90,290,"F9  - Show directions to ghost-pen"
Text 90,304,"F10 - Show directions to Pac Man"
Return
End Function
;####################################
Function get_new_ghost_dir(sign, x, y, direction)
Local n, e, s, w, z, tile, pacdirection
	tile=level(x,y)
	pacdirection=(tile And PACPAVED) Shr PACSHIFT
	; Is there a wall in this dir?     Can't reverse direction unless no alternative
	n=(((tile And NORTH)>0) * 1000) + ((direction=SOUTH) * 500)
	      ;+1/-1 not scared/scared			   moving in opposite direction - can't reverse unless no choice
	n=n+ ( sign * (((tile And PAC_NORTH)=0) + ( (direction Or pacdirection)=VERTICAL  )) )

	e=(((tile And EAST )>0) * 1000) + ((direction=WEST ) * 500)
	e=e+ ( sign * (((tile And PAC_EAST )=0) + ( (direction Or pacdirection)=HORIZONTAL)) )

	s=(((tile And SOUTH)>0) * 1000) + ((direction=NORTH) * 500)
	s=s+ ( sign * (((tile And PAC_SOUTH)=0) + ( (direction Or pacdirection)=VERTICAL  )) )
	
	w=(((tile And WEST )>0) * 1000) + ((direction=EAST ) * 500)
	w=w+ ( sign * (((tile And PAC_WEST )=0) + ( (direction Or pacdirection)=HORIZONTAL)) )
	z=n: direction=NORTH
	If e<z Then z=e: direction=EAST
	If s<z Then z=s: direction=SOUTH
	If w<z Then z=w: direction=WEST
Return direction
End Function
;####################################
Function get_next_map()
Local x, y
	numpills=0: numpowers=0
	For y=0 To 9
		For x=0 To 11
			Read level(x,y)
			If (level(x,y) And PILL) Then numpills=numpills+1 ElseIf (level(x,y) And POWERPILL) Then numpowers=numpowers+1
		Next
	Next
	levelmax=(numpills * 10) + (numpowers * 50) + (1400 * numpowers)
Return
End Function
;####################################
Function move_ghosts()
Local newdir, walls, x, y, canchangedir
For ghosts.ghost=Each ghost
	x=ghosts\x Shr 5: y=ghosts\y Shr 5	;makes lines shorter, and speeds up variable accessing
	newdir=ghosts\direction
	canchangedir=False

	If (Not((ghosts\x And $1F) Or (ghosts\y And $1F)))	;aligned to centre of a tile?
		If level(x,y) And WARP
			If Not(ghosts\warping)
				ghosts\warping=True
				ghosts\warpcount=TIMEWARP
				newdir=STATIC
			Else
				ghosts\warpcount=ghosts\warpcount-1
				If ghosts\warpcount=0
					ghosts\warping=False
					If x = 0
						newdir=WEST: ghosts\x=352: x=11
					Else
						newdir=EAST: ghosts\x=0: x=0
					EndIf
				EndIf
			EndIf
		Else
			canchangedir=True
		EndIf
	Else
		If (ghosts\direction=NORTH) And (ghosts\x=176)	;exiting pen
			If ghosts\y=96
				newdir=(level(5,3) And PACPAVED) Shr PACSHIFT
			EndIf
		EndIf
	EndIf

	Select ghosts\state
		Case NORMAL
			If canchangedir Then newdir=get_new_ghost_dir(1,x,y,newdir)
		Case SCARED
			ghosts\scarecount=ghosts\scarecount-1
			If (ghosts\scarecount=0)
				ghosts\frame=ghosts\num-1
				ghosts\state=NORMAL
				ghosts\x=ghosts\x And $3FE	;realign to even number pixels - or they'll bugger off
				ghosts\y=ghosts\y And $3FE
				If (Not((ghosts\x And $1F) Or (ghosts\y And $1F))) And (ghosts\warping =False) Then canchangedir=True Else canchangedir=False
				ghosts\xadd=2
				ghosts\yadd=2
				If canchangedir Then newdir=get_new_ghost_dir(1,x,y,newdir)
			Else
				If ghosts\scarecount<100
					If ghosts\scarecount And ((ghosts\scarecount / 25) + 1)
						ghosts\frame=ghosts\num-1
					Else
						ghosts\frame=3
					EndIf
				EndIf
				If canchangedir Then newdir=get_new_ghost_dir(-1,x,y,newdir)
			EndIf
		Case CHOMPED
			If level(x,y) And ABOVEPEN
				If ghosts\x=176
					newdir=SOUTH
				Else
					If canchangedir Then newdir=(level(x,y) And PAVED) Shr PENSHIFT
				EndIf
			Else
				If level(x,y) And GHOSTPEN
					ghosts\state=PENNED
					ghosts\frame=ghosts\num-1
					ghosts\chompcount=0
					ghosts\pencount=ghosts\num * 4
					newdir=EAST
					ghosts\xadd=2
					ghosts\yadd=2
				Else
					If canchangedir Then newdir=(level(x,y) And PAVED) Shr PENSHIFT
				EndIf
			EndIf
		Case PENNED
			If ghosts\x=176
				ghosts\pencount=ghosts\pencount-1
				If ghosts\pencount=0
					newdir=NORTH
					ghosts\state=NORMAL
					ghosts\safe=False
				EndIf
			Else
				If canchangedir Then newdir=ghosts\direction Xor HORIZONTAL
			EndIf
	End Select
	ghosts\direction=newdir
	Select newdir
		Case NORTH	ghosts\y=ghosts\y - ghosts\yadd
		Case EAST	ghosts\x=ghosts\x + ghosts\xadd
		Case SOUTH	ghosts\y=ghosts\y + ghosts\yadd
		Case WEST	ghosts\x=ghosts\x - ghosts\xadd
	End Select
	GrabImage bg,ghosts\x,ghosts\y,ghosts\num-1
Next
Return
End Function
;####################################
Function move_pacman()
Local key, x, y
	DrawBlock pacbg,pacx,pacy
	For ghosts.ghost=Each ghost
		DrawBlock bg,ghosts\x,ghosts\y,ghosts\num-1
	Next
	Select pacdir
		Case NORTH	pacy=pacy-2: staticcount=0
		Case EAST	pacx=pacx+2: staticcount=0
		Case SOUTH	pacy=pacy+2: staticcount=0
		Case WEST	pacx=pacx-2: staticcount=0
		Case STATIC staticcount=staticcount+1
	End Select
	px=pacx Shr 5: py=pacy Shr 5
	If KeyDown(UPARROW) Then key=NORTH
	If KeyDown(RIGHTARROW) Then key=EAST
	If KeyDown(DOWNARROW) Then key=SOUTH
	If KeyDown(LEFTARROW) Then key=WEST
	If key
		If (key And VERTICAL)
			If Not(pacx And $1F)
				If key=NORTH
					If Not(level(px,py) And NORTH) Then pacdir=NORTH
				Else
					If Not(level(px,py) And SOUTH) Then pacdir=SOUTH
				EndIf
			EndIf
		Else
			If Not(pacy And $1F)
				If key=EAST
					If Not(level(px,py) And EAST) Then pacdir=EAST
				Else
					If Not(level(px,py) And WEST) Then pacdir=WEST: pacframe=12
				EndIf
			EndIf
		EndIf
	EndIf
	If Not((pacx And $1F) Or (pacy And $1F))	;centre of a square
		If level(px, py) And WARP
			If px=0 Then pacx=352: px=11 Else pacx=0: px=0
		EndIf
		If level(px,py) And ANY_PILLS
			If level(px,py) And POWERPILL
				For ghosts.ghost=Each ghost
					If Not(ghosts\safe)
						ghosts\state=SCARED
						ghosts\frame=3
						ghosts\scarecount=ghosts\scarecount+SCARED_TIME
						ghosts\xadd=1
						ghosts\yadd=1
					EndIf
				Next
				leveltotal=leveltotal+50
				numpowers=numpowers-1
			Else
				leveltotal=leveltotal+10
				numpills=numpills-1
			EndIf
			level(px,py)=level(px,py) And NO_PILLS
			SetBuffer ImageBuffer(pacbg)
			Cls
			SetBuffer BackBuffer()
			DrawBlock pacbg,pacx,pacy
			If (numpills + numpowers)=0 Then action=COMPLETED
		EndIf
		If (pacdir<>STATIC) Or (staticcount=16) Then do_paths()
		If level(px, py) And pacdir Then pacdir=STATIC
	EndIf
	If ((pacx And $1F)=$10) Or ((pacy And $1F)=$10) Then do_paths()	;after moving every 16 pixels
	GrabImage pacbg,pacx,pacy
Return
End Function
;####################################
Function new_game()
Local ghostnum, backgrounds
	lives=NUM_LIVES: score=0: oldscore=0
	mapnum=1
	action=False
	maxmultiplier=0
Return
End Function
;####################################
Function new_level()
	FlushKeys()
	leveltotal=0
	If (action<>DIED)
		If ((mapnum-1) Mod NUM_MAPS) =0 Then Restore leveldata
		get_next_map()
		mapnum=mapnum+1
	EndIf
	action=False
	SeedRnd MilliSecs()
	For ghosts.ghost=Each ghost
		ghosts\x=160+((ghosts\num-1) Shl 4)
		ghosts\y=128
		ghosts\state=PENNED
		If ghosts\num=1 Then ghosts\direction=WEST Else ghosts\direction=EAST
		ghosts\xadd=2
		ghosts\yadd=2
		ghosts\frame=ghosts\num-1
		ghosts\warping=False
		ghosts\safe=True
		ghosts\chompcount=0
		ghosts\scarecount=0
		ghosts\warpcount=0
		ghosts\pencount=1+((ghosts\num>1)*ghosts\num)	;delay exits so not bunched
		SetBuffer ImageBuffer(bg,ghosts\num-1)
		Cls
	Next
	SetBuffer ImageBuffer(pacbg)
	Cls
	SetBuffer BackBuffer()
	ghostschomped=0
	wallcolour=(mapnum - 1) Mod 5
	pacdir=EAST
	framecount=0
	pacx=PACSTARTX: pacy=PACSTARTY
	px=pacx Shr 5: py=pacy Shr 5
	draw_walls()
	GrabImage pacbg,pacx,pacy
	For ghosts.ghost=Each ghost
		GrabImage bg,ghosts\x,ghosts\y,ghosts\num-1
	Next
	draw_game()
Return
End Function
;####################################
Function setup_ghosts()
Local ghostnum
For ghostnum=0 To 2
	ghosts.ghost=New ghost
	ghosts\num=ghostnum+1
Next
Return
End Function
;####################################
.colours
Data 255,0,0, 0,160,0, 192,0,255, 128,128,255
Data 64,64,127
Data 96,32,127
Data 127,64,64
Data 64,127,64
Data 127,127,127
Data 127,80,50
.titlescreen
Data "PAC MAN"
Data "Harlequin Software 2007"
Data ""
Data "ENTER TO PLAY"
Data "ESCAPE TO QUIT"
Data ""
Data "KEYS"
Data "UP, DOWN, LEFT, RIGHT"
Data ""
Data "  Pills                 + 10 points"
Data "  Power-pills           + 50 points"
Data "  Eat Ghosts            + 200, 400, 800"
Data "  Bonus for Max points  + 1000 Shl Multiplier"
Data "  Lives remaining       + 500 points each"
Data ""
Data "  F5  - turn off collision detection"
Data "  F9  - show pre-calced path to ghost-pen"
Data "  F10 - show real-time calced path to Pac Man"
.leveldata
;level 1
Data $20000,$20000,$20000,$20000,$20000,$20000,$20000,$20000,$20000,$20000,$20000,$20000
Data $20000,$00249,$00225,$00225,$00225,$00421,$00421,$00825,$00825,$00825,$00843,$20000
Data $20000,$0042A,$00229,$00225,$00421,$00826,$0022C,$00421,$00825,$00823,$0042A,$20000
Data $20000,$0042A,$0012A,$00229,$00220,$002A5,$008A5,$00820,$00823,$0012A,$0042A,$20000
Data $1020D,$00220,$00220,$00126,$0012A,$0001D,$00017,$0012A,$0012C,$00820,$00820,$10807
Data $20000,$0012A,$00128,$00823,$00128,$00825,$00225,$00122,$00429,$00122,$0012A,$20000
Data $20000,$0012A,$0012A,$0022C,$00120,$00825,$00225,$00120,$00826,$0012A,$0012A,$20000
Data $20000,$0012A,$0012C,$00225,$00124,$00823,$00229,$00124,$00825,$00826,$0012A,$20000
Data $20000,$0014C,$00825,$00225,$00225,$00124,$00124,$00825,$00825,$00825,$00146,$20000
Data $20000,$20000,$20000,$20000,$20000,$20000,$20000,$20000,$20000,$20000,$20000,$20000
;level 2
Data $20000,$20000,$20000,$20000,$20000,$20000,$20000,$20000,$20000,$20000,$20000,$20000
Data $20000,$00249,$00225,$00225,$00421,$00825,$00225,$00421,$00825,$00825,$00843,$20000
Data $20000,$0012A,$00229,$00221,$00220,$00423,$00429,$00820,$00821,$00823,$0012A,$20000
Data $20000,$00228,$00124,$00126,$00228,$002A4,$008A4,$00822,$0012C,$00824,$00822,$20000
Data $1020D,$00220,$00225,$00225,$00122,$0001D,$00017,$00128,$00825,$00825,$00820,$10807
Data $20000,$00128,$00821,$00423,$00128,$00825,$00225,$00122,$00429,$00821,$00122,$20000
Data $20000,$0012A,$0022C,$00224,$00122,$00429,$00423,$00128,$00824,$00826,$0012A,$20000
Data $20000,$00128,$00225,$00225,$00124,$00824,$00224,$00124,$00825,$00825,$00822,$20000
Data $20000,$0012C,$00825,$00825,$00825,$00847,$0024D,$00225,$00225,$00225,$00126,$20000
Data $20000,$20000,$20000,$20000,$20000,$20000,$20000,$20000,$20000,$20000,$20000,$20000
;level 3
Data $20000,$20000,$20000,$20000,$20000,$20000,$20000,$20000,$20000,$20000,$20000,$20000
Data $20000,$00249,$00221,$00421,$00825,$00823,$00229,$00225,$00421,$00821,$00843,$20000
Data $20000,$00128,$00126,$0022C,$00423,$0012C,$00126,$00429,$00826,$0012C,$00822,$20000
Data $20000,$00228,$00423,$00229,$00220,$002A5,$008A5,$00820,$00823,$00429,$00822,$20000
Data $1020D,$00122,$0022C,$00126,$0012A,$0001D,$00017,$0012A,$0012C,$00826,$00128,$10807
Data $20000,$0012A,$00229,$00423,$00128,$00821,$00221,$00122,$00429,$00823,$0012A,$20000
Data $20000,$00128,$00126,$0022C,$00120,$00826,$0012C,$00120,$00826,$0012C,$00822,$20000
Data $20000,$00128,$00423,$00229,$00126,$00429,$00423,$0012C,$00823,$00429,$00822,$20000
Data $20000,$0024C,$00224,$00124,$00825,$00826,$0022C,$00225,$00124,$00824,$00846,$20000
Data $20000,$20000,$20000,$20000,$20000,$20000,$20000,$20000,$20000,$20000,$20000,$20000
;level 4
Data $20000,$20000,$20000,$20000,$20000,$20000,$20000,$20000,$20000,$20000,$20000,$20000
Data $20000,$00249,$00225,$00421,$00825,$00421,$00421,$00825,$00421,$00825,$00843,$20000
Data $20000,$00128,$00823,$0022C,$00421,$00826,$0022C,$00421,$00826,$00229,$00122,$20000
Data $20000,$0012A,$0022C,$00423,$00228,$002A5,$008A5,$00822,$00429,$00826,$0012A,$20000
Data $1020D,$00220,$00225,$00422,$0012A,$0001D,$00017,$0012A,$00428,$00825,$00820,$10807
Data $20000,$00228,$00225,$00220,$00120,$00825,$00225,$00120,$00820,$00825,$00822,$20000
Data $20000,$0012A,$00229,$00126,$0012C,$00823,$00229,$00126,$0012C,$00823,$0012A,$20000
Data $20000,$0012A,$0012C,$00823,$00229,$00124,$00124,$00823,$00229,$00126,$0012A,$20000
Data $20000,$0014C,$00825,$00124,$00124,$00825,$00225,$00124,$00824,$00825,$00146,$20000
Data $20000,$20000,$20000,$20000,$20000,$20000,$20000,$20000,$20000,$20000,$20000,$20000
;level 5
Data $20000,$20000,$20000,$20000,$20000,$20000,$20000,$20000,$20000,$20000,$20000,$20000
Data $20000,$00249,$00225,$00423,$00429,$00825,$00225,$00423,$00429,$00825,$00843,$20000
Data $20000,$00228,$00225,$00220,$00224,$00423,$00429,$00824,$00820,$00825,$00822,$20000
Data $20000,$0012C,$00823,$0012A,$00229,$002A4,$008A4,$00823,$0012A,$00229,$00126,$20000
Data $1020D,$00225,$00122,$0012A,$0012A,$0001D,$00017,$0012A,$0012A,$00128,$00825,$10807
Data $20000,$00229,$00126,$0012A,$0012C,$00821,$00221,$00126,$0012A,$0012C,$00423,$20000
Data $20000,$00228,$00221,$00124,$00221,$00126,$0012C,$00821,$00124,$00821,$00822,$20000
Data $20000,$0012A,$0012C,$00823,$0012C,$00823,$00229,$00126,$00229,$00126,$0012A,$20000
Data $20000,$0014C,$00825,$00124,$00225,$00124,$00124,$00825,$00124,$00825,$00146,$20000
Data $20000,$20000,$20000,$20000,$20000,$20000,$20000,$20000,$20000,$20000,$20000,$20000
;level 6
Data $20000,$20000,$20000,$20000,$20000,$20000,$20000,$20000,$20000,$20000,$20000,$20000
Data $20000,$00249,$00225,$00423,$00429,$00825,$00225,$00423,$00429,$00825,$00843,$20000
Data $20000,$0012A,$00229,$00420,$00824,$00825,$00225,$00224,$00420,$00823,$0012A,$20000
Data $20000,$00228,$00126,$00228,$00221,$002A5,$008A5,$00821,$00822,$0012C,$00822,$20000
Data $1020D,$00122,$00229,$00126,$0012A,$0001D,$00017,$0012A,$0012C,$00823,$00128,$10807
Data $20000,$0022C,$00120,$00823,$00128,$00825,$00225,$00122,$00429,$00120,$00826,$20000
Data $20000,$00229,$00122,$00228,$00126,$00429,$00423,$0012C,$00822,$00128,$00823,$20000
Data $20000,$0012A,$0012C,$00120,$00825,$00822,$00228,$00225,$00120,$00826,$0012A,$20000
Data $20000,$0014C,$00225,$00124,$00825,$00824,$00124,$00225,$00124,$00825,$00846,$20000
Data $20000,$20000,$20000,$20000,$20000,$20000,$20000,$20000,$20000,$20000,$20000,$20000
;level 7
Data $20000,$20000,$20000,$20000,$20000,$20000,$20000,$20000,$20000,$20000,$20000,$20000
Data $20000,$00249,$00225,$00421,$00825,$00825,$00225,$00225,$00421,$00825,$00843,$20000
Data $20000,$00128,$00823,$00428,$00825,$00825,$00225,$00225,$00422,$00229,$00122,$20000
Data $20000,$0012A,$0012A,$0022C,$00225,$002A5,$008A5,$00825,$00826,$0012A,$0012A,$20000
Data $1020D,$00120,$00824,$00823,$0042B,$0001D,$00017,$0042B,$00229,$00124,$00120,$10807
Data $20000,$00128,$00823,$0012C,$00820,$00825,$00225,$00220,$00126,$00229,$00122,$20000
Data $20000,$0012A,$00128,$00825,$00824,$00823,$00229,$00124,$00225,$00122,$0012A,$20000
Data $20000,$0012A,$0012A,$00429,$00821,$00126,$0012C,$00821,$00423,$0012A,$0012A,$20000
Data $20000,$0014C,$00824,$00826,$0012C,$00825,$00225,$00126,$0022C,$00124,$00146,$20000
Data $20000,$20000,$20000,$20000,$20000,$20000,$20000,$20000,$20000,$20000,$20000,$20000
;level 8
Data $20000,$20000,$20000,$20000,$20000,$20000,$20000,$20000,$20000,$20000,$20000,$20000
Data $20000,$00249,$00421,$00225,$00421,$00825,$00225,$00421,$00825,$00421,$00843,$20000
Data $20000,$0042A,$0042A,$00429,$00826,$00429,$00423,$0022C,$00423,$0042A,$0042A,$20000
Data $20000,$00228,$00224,$00224,$00221,$002A4,$008A4,$00821,$00824,$00824,$00822,$20000
Data $1020D,$00124,$00823,$00229,$00122,$0001D,$00017,$00128,$00823,$00429,$00124,$10807
Data $20000,$00229,$00224,$00122,$0012C,$00821,$00221,$00126,$00128,$00824,$00823,$20000
Data $20000,$00128,$00225,$00124,$00821,$00126,$0012C,$00821,$00124,$00825,$00822,$20000
Data $20000,$0012A,$00229,$00225,$00120,$00825,$00225,$00120,$00825,$00823,$0012A,$20000
Data $20000,$0014C,$00824,$00225,$00124,$00825,$00225,$00124,$00825,$00824,$00146,$20000
Data $20000,$20000,$20000,$20000,$20000,$20000,$20000,$20000,$20000,$20000,$20000,$20000
;level 9
Data $20000,$20000,$20000,$20000,$20000,$20000,$20000,$20000,$20000,$20000,$20000,$20000
Data $20000,$00449,$00225,$00225,$00221,$00423,$00429,$00825,$00825,$00421,$00843,$20000
Data $20000,$00228,$00225,$00421,$00826,$0022C,$00224,$00421,$00825,$00826,$0042A,$20000
Data $20000,$00228,$00423,$0042A,$00229,$002A5,$008A5,$00824,$00825,$00825,$00826,$20000
Data $1020D,$00224,$00224,$00224,$00126,$0001D,$00017,$00229,$00221,$00225,$00221,$10207
Data $20000,$00229,$00225,$00221,$00423,$00229,$00221,$00126,$0012C,$00221,$00122,$20000
Data $20000,$0012A,$00229,$00126,$0022C,$00122,$0012C,$00225,$00225,$00126,$0012A,$20000
Data $20000,$00128,$00124,$00225,$00423,$00128,$00825,$00423,$00229,$00225,$00122,$20000
Data $20000,$0024C,$00225,$00225,$00224,$00124,$00225,$00224,$00124,$00225,$00146,$20000
Data $20000,$20000,$20000,$20000,$20000,$20000,$20000,$20000,$20000,$20000,$20000,$20000
;level 10
Data $20000,$20000,$20000,$20000,$20000,$20000,$20000,$20000,$20000,$20000,$20000,$20000
Data $20000,$00249,$00221,$00221,$00421,$00825,$00225,$00421,$00821,$00821,$00843,$20000
Data $20000,$0012A,$0012A,$0012A,$00428,$00825,$00225,$00422,$0012A,$0012A,$0012A,$20000
Data $20000,$0012A,$0012A,$0012A,$00228,$002A5,$008A5,$00822,$0012A,$0012A,$0012A,$20000
Data $1020D,$00422,$0042A,$0042A,$0012A,$0001D,$00017,$0012A,$0042A,$0042A,$00428,$10807
Data $20000,$00228,$00224,$00224,$00120,$00821,$00221,$00120,$00824,$00824,$00822,$20000
Data $20000,$00128,$00225,$00225,$00122,$0012A,$0012A,$00128,$00825,$00825,$00822,$20000
Data $20000,$00128,$00225,$00225,$00122,$0012A,$0012A,$00128,$00825,$00825,$00822,$20000
Data $20000,$0014C,$00225,$00225,$00124,$00824,$00124,$00124,$00825,$00825,$00846,$20000
Data $20000,$20000,$20000,$20000,$20000,$20000,$20000,$20000,$20000,$20000,$20000,$20000
;level 11
Data $20000,$20000,$20000,$20000,$20000,$20000,$20000,$20000,$20000,$20000,$20000,$20000
Data $20000,$00249,$00221,$00221,$00221,$00421,$00421,$00821,$00821,$00821,$00843,$20000
Data $20000,$00228,$00220,$00220,$00220,$00420,$00420,$00820,$00820,$00820,$00822,$20000
Data $20000,$00228,$00220,$00220,$00220,$002A4,$008A4,$00820,$00820,$00820,$00822,$20000
Data $1020D,$00120,$00120,$00122,$0012A,$0001D,$00017,$0012A,$00128,$00820,$00820,$10807
Data $20000,$00128,$00120,$00122,$0012C,$00825,$00225,$00126,$00128,$00820,$00822,$20000
Data $20000,$00128,$00120,$00120,$00821,$00821,$00221,$00221,$00120,$00820,$00822,$20000
Data $20000,$00128,$00120,$00120,$00820,$00820,$00120,$00120,$00120,$00820,$00822,$20000
Data $20000,$0014C,$00124,$00124,$00824,$00824,$00124,$00124,$00124,$00824,$00846,$20000
Data $20000,$20000,$20000,$20000,$20000,$20000,$20000,$20000,$20000,$20000,$20000,$20000
