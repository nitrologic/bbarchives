; ID: 1925
; Author: Vic 3 Babes
; Date: 2007-02-14 18:45:52
; Title: Pac Man Editor
; Description: To accompany the Pac Man example

;Harlequin Software 2007
;Design a level for Pac Man, which pre-calculates the path that
;ghosts must take to get back to the ghost pen after being
;chomped.
Const SAVEFILE$="PacLevel.dat"
Const DATANAME$="PacLevel.txt"
	;compass directions serve 2 purposes
	;1. the bit set in an array element means there is a wall there
	;2. they indicate ghosts and pac man's direction
Const NORTH=1, EAST=2, SOUTH=4, WEST=8
	;ghost pen bit - so ghost knows it's home, and pill bits
Const GHOSTPEN=$10, PILL=$20, POWERPILL=$40, ABOVEPEN=$80
Const ANY_PILLS=$60
	;NOT directions so can lose walls if user erases them by ANDing with array element
Const NOT_NORTH=$FFFFFE, NOT_EAST=$FFFFFD, NOT_SOUTH=$FFFFFB, NOT_WEST=$FFFFF7
	;diagonal directions so can draw a pixel in corner if no walls
Const NORTHEAST=$3, SOUTHEAST=$6, SOUTHWEST=$C, NORTHWEST=$9
	;bits set in array elements as directions for ghost to get back to pen
Const PEN_NORTH = NORTH Shl 8
Const PEN_EAST  = EAST Shl 8
Const PEN_SOUTH = SOUTH Shl 8
Const PEN_WEST  = WEST Shl 8
	;PAVED to test if any of above bits set - so don't need to set if there is
	;NOT_PAVED to clear above bits
Const PAVED=$F00, NOT_PAVED=$FFF0FF
	;LOCED walls - ghost pen and outer edges of level - so user can't change
Const NORTH_LOCKED = NORTH Shl 12
Const EAST_LOCKED  = EAST Shl 12
Const SOUTH_LOCKED = SOUTH Shl 12
Const WEST_LOCKED  = WEST Shl 12
	;warp - bit set to indicate off screen through exit
Const WARP=$10000
	;illegal - bit set to indicte array element outside level, and not WARP
Const ILLEGAL=$20000
	;these next few just speed up the ghost pen pathfinding
	;when checking to see whether to make an adjacent square
	;point to the current square we need to know:
	;1. is there a wall between the two squares
	;2. is the square already paved
	;3. is the square outside the level
	;e.g. If (level(x+1,y) and WESTorPAVEDorILLEGAL)=false - then make it point to x,y etc.
Const NORTHorPAVEDorILLEGAL = NORTH Or PAVED Or ILLEGAL
Const EASTorPAVEDorILLEGAL  = EAST Or PAVED Or ILLEGAL
Const SOUTHorPAVEDorILLEGAL = SOUTH Or PAVED Or ILLEGAL
Const WESTorPAVEDorILLEGAL  = WEST Or PAVED Or ILLEGAL
	;this one is for when you try to change a pill - not allowed in these
	;if Not(level(x,y) and GHOSTPENorILLEGALorWARP) then can change pill type
Const GHOSTPENorILLEGALorWARP = GHOSTPEN Or ILLEGAL Or WARP
;-----------------------------------------------------
Const QUIT=1, RESTART=2, DONE=4
Const FINISHED=QUIT Or RESTART
;for checksum in save file
Const RANDLOW=$1234, RANDHIGH=$56789ABC
;keys
Const F1=59, F2=60, F3=61, XKEY=45, YKEY=21, F10=68
;cursor colour when line drawing
Const WHITE=0, GREEN=1, RED=2

Global action, pills_on, Xsymmetry_on, Ysymmetry_on
Global mx, my, omx, omy
Global mbutton, redraw
AppTitle "Pac Man Editor"
Graphics 384,320
Global pills=CreateImage(8,8,2)
Global cursor=CreateImage(8,8,3)

Type square	;used for flood-filling paths used by ghosts to get back to
	Field x, y, count	;ghost pen after being chomped
End Type

Dim level(11,9)
Dim help$(6)

Restore helpstrings
For action=0 To 6
	Read help$(action)
Next

create_tiles()
SetBuffer BackBuffer()
Xsymmetry_on=True: Ysymmetry_on=False
load_level()
Repeat
	pills_on=False: mbutton=False: action=False: redraw=True
	mx=MouseX(): my=MouseY()
	Repeat
		Delay 1
		If (mx <> MouseX()) Or (my <> MouseY())
			mx = MouseX(): my = MouseY()
			If ((mx And $1E0) <> omx) Or ((my And $1E0) <> omy)
				omx=mx And $1E0: omy=my And $1E0
				redraw=True
			EndIf
		EndIf
		If MouseHit(1) Then mbutton=1
		If MouseHit(2) Then mbutton=2
		If MouseHit(3) Then DebugLog Hex$(level(mx Shr 5,my Shr 5))
		If mbutton
			If pills_on
				If mbutton=1 Then do_pills(omx Shr 5, omy Shr 5)
			Else
				do_walls()
			EndIf
			redraw=True
			mbutton=False
		EndIf
		If KeyHit(XKEY) Then Xsymmetry_on=Not(Xsymmetry_on): redraw=True
		If KeyHit(YKEY) Then Ysymmetry_on=Not(Ysymmetry_on): redraw=True
		If KeyHit(F1) Then pills_on=Not(pills_on): redraw=True
		If redraw
			draw_level()
			DrawImage cursor, (omx-4)+(pills_on Shl 4), (omy-4)+(pills_on Shl 4),WHITE
			Flip False
			redraw=False
		EndIf
		If KeyHit(F2) Then save_level()
		If KeyHit(F3) Then export_level()
		If KeyHit(F10) Then action=RESTART
		If KeyHit(QUIT) Then action=QUIT
	Until action And FINISHED
	If action<>QUIT Then setup_array()
Until action = QUIT
End
;####################################
Function create_tiles()
	SetBuffer ImageBuffer(pills,0)
	Color 128,255,128
	Oval 2,2,4,4,True
	SetBuffer ImageBuffer(pills,1)
	Color 255,128,128
	Oval 0,0,8,8,True
	SetBuffer ImageBuffer(cursor,WHITE)
	Color 255,255,255
	Rect 0,0,8,8,False
	SetBuffer ImageBuffer(cursor,GREEN)
	Color 0,255,0
	Rect 0,0,8,8,False
	SetBuffer ImageBuffer(cursor,RED)
	Color 255,0,0
	Rect 0,0,8,8,False
Return
End Function
;####################################
Function do_paths()
Local x, y, numsquares
	action=0
	For y=0 To 9
		For x=0 To 11
			level(x,y)=level(x,y) And NOT_PAVED
		Next
	Next
	level(5,3)=level(5,3) Or PEN_EAST
	level(6,3)=level(6,3) Or PEN_WEST
	squares.square=New square
	squares\x=5
	squares\y=3
	squares.square=New square
	squares\x=6
	squares\y=3
	numsquares=2
	Repeat
		squares.square=First square
		x=squares\x: y=squares\y
		Delete squares
		numsquares=numsquares-1
		;test square above this one
		If (level(x,y-1) And SOUTHorPAVEDorILLEGAL)=False
			level(x,y-1)=level(x,y-1) Or PEN_SOUTH
			squares.square=New square
			squares\x=x
			squares\y=y-1
			numsquares=numsquares+1
		EndIf
		;test square to left of this one
		If x>0
			If (level(x-1,y) And EASTorPAVEDorILLEGAL)=False
				level(x-1,y)=level(x-1,y) Or PEN_EAST
				squares.square=New square
				squares\x=x-1
				squares\y=y
				numsquares=numsquares+1
			EndIf
		Else
			If level(x,y) And WARP
				If (level(11,4) And PAVED)=False
					level(11,4)=level(11,4) Or PEN_EAST
					squares.square=New square
					squares\x=11
					squares\y=4
					numsquares=numsquares+1
				EndIf
			EndIf
		EndIf
		;test square below this one
		If (level(x,y+1) And NORTHorPAVEDorILLEGAL)=False
			level(x,y+1)=level(x,y+1) Or PEN_NORTH
			squares.square=New square
			squares\x=x
			squares\y=y+1
			numsquares=numsquares+1
		EndIf
		;test square to right of this one
		If x<11
			If (level(x+1,y) And WESTorPAVEDorILLEGAL)=False
				level(x+1,y)=level(x+1,y) Or PEN_WEST
				squares.square=New square
				squares\x=x+1
				squares\y=y
				numsquares=numsquares+1
			EndIf
		Else
			If level(x,y) And WARP
				If (level(0,4) And PAVED)=False
					level(0,4)=level(0,4) Or PEN_WEST
					squares.square=New square
					squares\x=0
					squares\y=4
					numsquares=numsquares+1
				EndIf
			EndIf
		EndIf
		If numsquares=0 Then action=DONE
		If KeyHit(QUIT) Then action=QUIT
	Until action And (DONE Or QUIT)
	If action=QUIT
		For squares.square=Each square
			Delete squares
		Next
	EndIf
Return
End Function
;####################################
Function do_pills(x, y)
	If Not( level(x, y) And GHOSTPENorILLEGALorWARP)
		level(x, y)=level(x, y) Xor ANY_PILLS
		If Xsymmetry_on Then level(11-x,y)=level(11-x,y) Xor ANY_PILLS
		If Ysymmetry_on Then level(x, 9-y)=level(x, 9-y) Xor ANY_PILLS
		If (Xsymmetry_on And Ysymmetry_on) Then level(11-x, 9-y)=level(11-x, 9-y) Xor ANY_PILLS
	EndIf
Return
End Function
;####################################
Function do_walls()
Local startx, starty, mouseover
	If (omx<32) Or (omy<32) Or (omx>352) Or (omy>288) Then Return
	mouseover=True
	DrawBlock cursor, omx-4, omy-4,mbutton
	Flip False
	startx=omx: starty=omy
	Repeat
		Delay 1
		If (mx <> MouseX()) Or (my <> MouseY())
			mx=MouseX(): my=MouseY()
			If ((mx And $FE0) <> omx) Or ((my And $FE0) <> omy)
				draw_level()
				omx=mx And $FE0: omy=my And $FE0
				If (omx>31) And (omx<384) And (omy>31) And (omy<320)
					DrawBlock cursor, startx-4, starty-4,mbutton
					DrawBlock cursor, omx-4, omy-4,mbutton
					If (startx<>omx) Or (starty<>omy)
						If (startx=omx) Or (starty=omy)
							Color (mbutton=2) * 255,(mbutton=1) * 255,0
							Line startx,starty,omx,omy
						EndIf
					EndIf
					Flip False
					mouseover=True
				Else
					Flip False
					mouseover=False
				EndIf
			EndIf
		EndIf
	Until Not(MouseDown(mbutton))
	If mouseover
		If (startx<>omx) Or (starty<>omy)
			If (startx=omx) Or (starty=omy)
				update_level(startx Shr 5, omx Shr 5, starty Shr 5, omy Shr 5)
				If Xsymmetry_on Then update_level(12-(startx Shr 5), 12-(omx Shr 5), starty Shr 5, omy Shr 5)
				If Ysymmetry_on Then update_level(startx Shr 5, omx Shr 5, 10-(starty Shr 5), 10-(omy Shr 5))
				If Xsymmetry_on And Ysymmetry_on
					update_level(12-(startx Shr 5), 12-(omx Shr 5), 10-(starty Shr 5), 10-(omy Shr 5))
				EndIf
			EndIf
		EndIf
	EndIf
Return
End Function
;####################################
Function draw_level()
Local x, y, tile, xpos, ypos
Cls
Color 255,255,255
Text 4,0,help$(pills_on)
Text 4,290,help$(Xsymmetry_on + 3)
Text 4,306,help$(Ysymmetry_on + 5)
Text 4,14,help$(2)
For y=1 To 8
	ypos=y Shl 5
	For x=0 To 11
		tile=level(x,y)
		If Not(tile And ILLEGAL)
			xpos=x Shl 5
			If tile And NORTH Then Line xpos,ypos,xpos+31,ypos
			If tile And EAST Then Line xpos+31,ypos,xpos+31,ypos+31
			If tile And SOUTH Then Line xpos,ypos+31,xpos+31,ypos+31
			If tile And WEST Then Line xpos,ypos,xpos,ypos+31
			If Not(tile And NORTHEAST) Then WritePixel xpos+31,ypos,$FFFFFF
			If Not(tile And SOUTHEAST) Then WritePixel xpos+31,ypos+31,$FFFFFF
			If Not(tile And SOUTHWEST) Then WritePixel xpos,ypos+31,$FFFFFF
			If Not(tile And NORTHWEST) Then WritePixel xpos,ypos,$FFFFFF
			If pills_on
				If tile And ANY_PILLS Then DrawBlock pills,xpos+12,ypos+12,(tile Shr 6) And 1
			EndIf
		EndIf
	Next
Next
Return
End Function
;####################################
Function export_level()
Local file, x, y, datastring$
	do_paths()
	If map_ok()
		file=WriteFile(DATANAME$)
		If file
			WriteLine file,"REMEMBER TO CHANGE NUM_MAPS CONSTANT IN PAC MAN"
			For y=0 To 9
				datastring$="Data $"
				For x=0 To 11
					datastring$=datastring$+Right$(Hex$((level(x,y) And $F0FFF)),5)
					If x<11 Then datastring$=datastring$+",$"
				Next
				WriteLine file,datastring$
			Next
			CloseFile file
			Notify "Level exported as data statements"+Chr$(13)+Chr$(10)+"to PacLevel.txt"
		Else
			Notify "Couldn't save text file"
		EndIf
	Else
		Notify "All pills must be accessible by Pac Man"
	EndIf
Return
End Function
;####################################
Function load_level()
Local checksum ,x ,y, file, randnum
	checksum=0
	file=ReadFile(SAVEFILE$)
	If file
		For y=0 To 9
			For x=0 To 11
				level(x,y)=ReadInt(file)
			Next
		Next
		randnum=ReadInt(file)
		CloseFile file
		For y=0 To 9
			For x=0 To 11
				checksum=checksum Xor level(x,y)
			Next
		Next
		SeedRnd checksum
		checksum=Rand(RANDLOW,RANDHIGH)
		If checksum<>randnum Then RuntimeError "Checksum Error in file"
	Else
		setup_array()
	EndIf
Return
End Function
;####################################
Function map_ok()
Local x, y, notpavedcount
	notpavedcount=-2	;for 2 ghostpen squares
	For y=1 To 8
		For x=1 To 10
			If Not(level(x,y) And PAVED) Then notpavedcount=notpavedcount+1
		Next
	Next
Return notpavedcount=0
End Function
;####################################
Function save_level()
Local file, x, y, checksum
	file=WriteFile(SAVEFILE$)
	If file
		checksum=0
		For y=0 To 9
			For x=0 To 11
				checksum=checksum Xor level(x,y)
				WriteInt file,level(x,y)
			Next
		Next
		SeedRnd checksum
		checksum=Rand(RANDLOW,RANDHIGH)
		WriteInt file,checksum
		CloseFile file
		Notify "Level saved - will be reloaded"+Chr$(13)+Chr$(10)+"on next startup"
	Else
		Notify "Couldn't save level"
	EndIf
Return
End Function
;####################################
Function setup_array()
Local x, y
	Restore startlevel
	For y=0 To 9
		For x=0 To 11
			Read level(x,y)
		Next
	Next
Return
End Function
;####################################
Function update_level(sx, fx, sy, fy)
Local row, column, x1, y1, x2, y2

If sx > fx Then x1=fx: x2=sx Else x1=sx: x2=fx
If sy > fy Then y1=fy: y2=sy Else y1=sy: y2=fy

If mbutton=1
	If x1=x2
		For row=y1 To y2-1
			If Not(level(x1-1,row) And EAST_LOCKED) Then level(x1-1,row)=level(x1-1,row) Or EAST
			If Not(level(x1,row) And WEST_LOCKED) Then level(x1,row)=level(x1,row) Or WEST
		Next
	Else
		For column=x1 To x2-1
			If Not(level(column,y1-1) And SOUTH_LOCKED) Then level(column,y1-1)=level(column,y1-1) Or SOUTH
			If Not(level(column,y1) And NORTH_LOCKED) Then level(column,y1)=level(column,y1) Or NORTH
		Next
	EndIf
Else
	If x1=x2
		For row=y1 To y2-1
			If Not(level(x1-1,row) And EAST_LOCKED) Then level(x1-1,row)=level(x1-1,row) And NOT_EAST
			If Not(level(x1,row) And WEST_LOCKED) Then level(x1,row)=level(x1,row) And NOT_WEST
		Next
	Else
		For column=x1 To x2-1
			If Not(level(column,y1-1) And SOUTH_LOCKED) Then level(column,y1-1)=level(column,y1-1) And NOT_SOUTH
			If Not(level(column,y1) And NORTH_LOCKED) Then level(column,y1)=level(column,y1) And NOT_NORTH
		Next
	EndIf
EndIf
Return
End Function
;####################################
.startlevel
;individual hex digits read from right to left
;1. Walls (1=N, 2=E, 4=S, 8=W)
;2. Ghostpen or pills (1=GP, 2=Pill, 4=Power Pill)
;3. Directions for ghost to get back to pen (1=N, 2=E, 4=S, 8=W) - only the two squares above pen set at start
;4. Walls LOCKED - can't be drawn over/erased - (1=N, 2=E, 4=S, 8=W) - not exported in data statements
;5. These indicate array element is off edges of level (1=WARP, 2=ILLEGAL)
Data $20000,$20000,$20000,$20000,$20000,$20000,$20000,$20000,$20000,$20000,$20000,$20000
Data $20000,$9049,$1021,$1021,$1021,$1021,$1021,$1021,$1021,$1021,$3043,$20000
Data $20000,$8028,$0020,$0020,$0020,$0020,$0020,$0020,$0020,$0020,$2022,$20000
Data $20000,$8028,$0020,$0020,$0020,$64A4,$C4A4,$0020,$0020,$0020,$2022,$20000
Data $1F00D,$8020,$0020,$0020,$2022,$F01D,$F017,$8028,$0020,$0020,$2020,$1F007
Data $20000,$8028,$0020,$0020,$0020,$3021,$9021,$0020,$0020,$0020,$2022,$20000
Data $20000,$8028,$0020,$0020,$0020,$0020,$0020,$0020,$0020,$0020,$2022,$20000
Data $20000,$8028,$0020,$0020,$0020,$0020,$0020,$0020,$0020,$0020,$2022,$20000
Data $20000,$C04C,$4024,$4024,$4024,$4024,$4024,$4024,$4024,$4024,$6046,$20000
Data $20000,$20000,$20000,$20000,$20000,$20000,$20000,$20000,$20000,$20000,$20000,$20000
.helpstrings
Data "LMB - Draw  RMB - Erase   F1 Switch to pills"
Data "LMB - Change pill-type    F1 Switch to walls"
Data "F2 Save for reloading     F3 Export Data as Txt"
Data "X-symmetry OFF (press X)"
Data "X-symmetry ON  (press X)"
Data "Y-symmetry OFF (press Y)       F10 - Restart"
Data "Y-symmetry ON  (press Y)       F10 - Restart"
