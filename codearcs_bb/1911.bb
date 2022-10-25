; ID: 1911
; Author: Vic 3 Babes
; Date: 2007-02-03 21:24:13
; Title: Binary Tetris
; Description: Algorithm for Tetris using only binary

;3FF - Harlequin Software 2002 - 2007
;Updated version 30 January 2007
AppTitle "3FF - Harlequin Software"
Graphics 320,360,0,2
Const ESCAPE=1, ENTER=28, SPACEBAR=57
Const UPARROW=200, DOWNARROW=208, LEFTARROW=203, RIGHTARROW=205, LEFTCONTROL=29
;bit values for action
Const PLAY=$1, QUITTED=$2, REDRAW=$4, LANDED=$8, GAMEOVER=$10, ESCAPED=$20
Const BITSUSED=$3F
Const DRAWN=BITSUSED Xor REDRAW, NOTLANDED=BITSUSED Xor LANDED
Const GAMEOVER_OR_ESCAPED=GAMEOVER Or ESCAPED
;
Const EMPTYROW=$801, FULLROW=$FFF, FULLVISIBLE=$7FE, HIDDENROW=$7FE
Const EAST=-1: WEST=1: CLOCKWISE=1: ANTICLOCKWISE=-1
;East and West are inverted because - erm - read the guide :)
Global currentrow, shifter, rotation, numlines, startrow
Global speed, time, action

Global tiles=CreateImage(16,16,4)

;Global line1snd=LoadSound("sound\line1.wav")
;Global line2snd=LoadSound("sound\line2.wav")
;Global line3snd=LoadSound("sound\line3.wav")
;Global line4snd=LoadSound("sound\line4.wav")
;Global landsnd=LoadSound("sound\land.wav")

Dim leveldata(23)
Dim levelpaused(21)
Dim currentbrick(3,3)
Dim nextbrick(3,3)
Dim lines(3)

SeedRnd MilliSecs()
init()
;################################ M A I N   L O O P #############################
Repeat
	draw_title()
	draw_next_brick()
	draw_score()
	Flip False
	action=0
	Repeat
		Delay 1
		If KeyHit(ENTER) Then action=PLAY
		If KeyHit(ESCAPE) Then action=QUITTED
	Until action
	If action=PLAY
		FlushKeys()
		action=REDRAW
		speed=1000
		clear_arrays(True)
		get_a_brick()
		get_a_brick() ;to get "next brick" as well
		numlines=0
		draw_score()
		time=MilliSecs()
		Repeat
			If action And LANDED
				get_a_brick()
			Else
				If MilliSecs()-time > speed
					move_down()
					time=MilliSecs()
				Else
					If KeyDown(DOWNARROW) And (MilliSecs()-time > 40)
						move_down()
						time=MilliSecs()
					Else
						If KeyHit(LEFTARROW)
							move_brick(WEST)
						Else
							If KeyHit(RIGHTARROW)
								move_brick(EAST)
							Else
								If KeyHit(UPARROW)
									rotate_brick(CLOCKWISE)
								Else
									If KeyHit(LEFTCONTROL)
										rotate_brick(ANTICLOCKWISE)
									EndIf
								EndIf
							EndIf
						EndIf
					EndIf
				EndIf
			EndIf
			If action And REDRAW
				If Not(action And LANDED) Then xor_brick()
				draw_play_area()
				Flip False
				action=action And DRAWN
			Else
				Delay 1	;keep cpu usage down
			EndIf
			If KeyHit(SPACEBAR) Then pause()
			If KeyHit(ESCAPE) Then action=action Or ESCAPED
		Until action And GAMEOVER_OR_ESCAPED
		If action And GAMEOVER Then game_over_man()
		clear_arrays(False)
	EndIf
Until action = QUITTED
End
;#############################################################
Function clear_arrays(clearlevel)
Local rownumber
	If clearlevel
		For rownumber=0 To 21
			leveldata(rownumber)=EMPTYROW
		Next
		leveldata(22)=HIDDENROW
	EndIf
	For rotation=0 To 3
		For rownumber=0 To 3
			nextbrick(rotation,rownumber)=0
		Next
	Next
Return
End Function
;-----------------------------------
Function collided(xadd)
Local x, y, collision=False
	shifter=shifter + xadd
	If shifter>-1		;shifting left or right?
		For y=currentrow To currentrow+3
			If (currentbrick(rotation,y-currentrow) Shl shifter) And leveldata(y) Then collision=True
		Next
	Else
		For y=currentrow To currentrow+3
			If (currentbrick(rotation,y-currentrow) Shr (shifter*-1)) And leveldata(y) Then collision=True	
		Next
	EndIf
	If collision Then shifter=shifter + (xadd * -1)
Return collision
End Function
;-----------------------------------
Function draw_next_brick()
Local x, y
	For y=0 To 3
		For x=0 To 3
			DrawBlock tiles,284-(x Shl 4),20+(y Shl 4),((nextbrick(0,y) Shr (x+4)) And 1)+2
		Next
	Next
	action=action Or REDRAW
Return
End Function
;-----------------------------------
Function draw_play_area()
Local row, column
	For row=2 To 21
		For column=1 To 10
			DrawBlock tiles,192-(column Shl 4),(row-2) Shl 4,(leveldata(row) Shr column) And 1
		Next
	Next
Return
End Function
;-----------------------------------
Function draw_score()
Local x
	;this function assumes no one will ever score more than 255 lines
	For x=0 To 7
		DrawBlock tiles, 176-(x Shl 4), 340, ((numlines Shr x) And 1) + 2
	Next
Return
End Function
;-----------------------------------
Function draw_title()
Local rownumber
	Restore title3ff
	For rownumber=2 To 21
		Read leveldata(rownumber)
	Next
	draw_play_area()
Return
End Function
;-----------------------------------
Function game_over_man()
	Color 0,0,0
	Rect 48,136,128,32,True
	Color 255,255,255
	Text 112,146,"GAME OVER!",True
	Rect 47,135,130,34,False
	Flip False
	Repeat
		Delay 1
	Until KeyHit(ENTER) Or KeyHit(ESCAPE)
	FlushKeys()
Return
End Function
;-----------------------------------
Function get_a_brick()
Local randbrick, rownumber
	currentrow=startrow	;startrow is next brick's start row
	randbrick=Rand(0,6)
	Select randbrick
		Case 0 Restore column
		Case 1 Restore L
		Case 2 Restore r
		Case 3 Restore cube
		Case 4 Restore T
		Case 5 Restore Z
		Case 6 Restore S
	End Select
	startrow=(randbrick > 2)
	;startrow depends on height of brick
	For rownumber=0 To 3
		For rotation=0 To 3
			currentbrick(rotation,rownumber)=nextbrick(rotation,rownumber)
			Read nextbrick(rotation,rownumber)
		Next
	Next
	rotation=0
	action=action And NOTLANDED
	shifter=0
	If collided(0) Then action=action Or GAMEOVER Else draw_next_brick()
Return
End Function
;-----------------------------------
Function init()
Local tilenumber, y, help$, error, r, g, b
	Restore initdata
	For tilenumber=0 To 3
		Read r,g,b
		SetBuffer ImageBuffer(tiles,tilenumber)
		ClsColor r,g,b
		Cls
		Color r Shl 1, g Shl 1, b Shl 1
		Text 4,1,tilenumber And 1
	Next
	SetBuffer BackBuffer()
	Color 192,164,0
	Text 16,342,"Lines:"
	For y=0 To 5
		Read help$
		Text 210,100+(26 * y),help$
	Next
	Color 128,96,0
	For y=0 To 5
		Read help$
		Text 220,113+(26 * y),help$
	Next
Return
End Function
;-----------------------------------
Function move_brick(xadd)
	xor_brick()
	If collided(xadd)
		xor_brick()
	Else
		action=action Or REDRAW
	EndIf
Return
End Function
;-----------------------------------
Function move_down()
Local newlines, rownumber, scrolloff, emptyrows, n
	xor_brick()
	currentrow=currentrow+1
	If collided(0)
		newlines=0
		action=action Or LANDED
		currentrow=currentrow-1
		xor_brick()
		For rownumber=currentrow To currentrow+3
			If leveldata(rownumber)=FULLROW
				newlines=newlines+1
				lines(rownumber-currentrow)=rownumber
				currentbrick(rotation,rownumber-currentrow)=0
				leveldata(rownumber)=FULLVISIBLE	;ready to scroll off
			EndIf
		Next
		If newlines
;			Select newlines
;				Case 1 PlaySound(line1snd)
;				Case 2 PlaySound(line2snd)
;				Case 3 PlaySound(line3snd)
;				Case 4 PlaySound(line4snd)
;			End Select
			For scrolloff=0 To 9
				For rownumber=0 To 3
					If lines(rownumber)
						If (rownumber And 1)
							leveldata(lines(rownumber))=leveldata(lines(rownumber)) Shr 1
						Else
							leveldata(lines(rownumber))=leveldata(lines(rownumber)) Shl 1
						EndIf
					EndIf
				Next
				draw_play_area()
				Delay 30: Flip False
			Next
			Delay 50
			For rownumber=0 To 3
				If lines(rownumber) Then leveldata(lines(rownumber))=EMPTYROW
				lines(rownumber)=0
			Next
			For emptyrows=0 To newlines
				For rownumber=currentrow+3 To 1 Step-1
					If leveldata(rownumber)=EMPTYROW And leveldata(rownumber-1)>EMPTYROW
						leveldata(rownumber)=leveldata(rownumber-1)
						leveldata(rownumber-1)=EMPTYROW
					EndIf
				Next
				draw_play_area()
				Delay 30: Flip False
			Next
			n=numlines+newlines
			If (n -(n Mod 10)) > (numlines-(numlines Mod 10)) Then speed=speed-(speed * 0.1)
			;speed + 10% every 10 lines
			numlines=numlines+newlines
			draw_score()
;		Else
;			PlaySound(landsnd)
		EndIf
	Else
		action=action Or REDRAW
	EndIf
Return
End Function
;-----------------------------------
Function pause()
Local oldtime, rownumber
	oldtime=MilliSecs()-time
	Restore pauseddata
	For rownumber=2 To 21
		levelpaused(rownumber)=leveldata(rownumber)
		Read leveldata(rownumber)
	Next
	draw_play_area()
	Flip False
	Repeat
		Delay 1
		If KeyHit(ESCAPE) Then action=action Or ESCAPED
	Until KeyHit(SPACEBAR) Or (action And ESCAPED)
	FlushKeys()
	If Not(action And ESCAPED)
		For rownumber=2 To 21
			leveldata(rownumber)=levelpaused(rownumber)
		Next
		draw_play_area()
		Flip False
		time=MilliSecs()-oldtime
	EndIf
Return
End Function
;-----------------------------------
Function rotate_brick(modifier)
Local oldrotation
	xor_brick()
	oldrotation=rotation
	rotation=rotation + modifier
	If rotation > 3 Then rotation=0 ElseIf rotation < 0 Then rotation=3
	If collided(0)
		If shift_collided()
			rotation=oldrotation
			xor_brick()
		Else
			action=action Or REDRAW
		EndIf
	Else
		action=action Or REDRAW
	EndIf
Return
End Function
;-----------------------------------
Function shift_collided()
;rotation caused a collision - can we shift brick sideways?
	If collided(-1)
		If collided(1)
			If collided(-2)
				Return collided(2)
			EndIf
		EndIf
	EndIf
Return False
End Function
;-----------------------------------
Function xor_brick()
Local y
	If shifter>-1
		For y=currentrow To currentrow+3
			leveldata(y) = (leveldata(y)) Xor currentbrick(rotation,y-currentrow) Shl shifter
		Next
	Else
		For y=currentrow To currentrow+3
			leveldata(y) = (leveldata(y)) Xor currentbrick(rotation,y-currentrow) Shr (shifter*-1)
		Next
	EndIf
Return
End Function
;-----------------------------------
.initdata
Data 32,32,32		;Grey 0
Data 127,127,127	;Grey 1
Data 48,32,64		;Purple 0
Data 96,64,127		;Purple 1
Data "Play:","Move:","Rotate right:","Rotate left:","Pause:","Quit:"
Data "Enter","Cursor keys","Up-arrow","L-Ctrl","Spacebar","Escape"
.title3ff
Data 0,0,0,0,$7FE,0,$36C,$148,$36C,$148,$348,0,$7FE,0,0,0,0,0,0,0,0,$7FE
;brickpatterns
.column
Data $40,$00,$40,$00, $40,$00,$40,$00, $40,$F0,$40,$F0, $40,$00,$40,$00
.T
Data $00,$00,$00,$00, $40,$40,$00,$40, $E0,$60,$E0,$C0, $00,$40,$40,$40
.cube
Data $00,$00,$00,$00, $60,$60,$60,$60, $60,$60,$60,$60, $00,$00,$00,$00
.r
Data $00,$00,$00,$00,$20,$40,$30,$00,$20,$70,$20,$70,$60,$00,$20,$10
.L
Data $00,$00,$00,$00,$40,$00,$C0,$20,$40,$E0,$40,$E0,$60,$80,$40,$00
.Z
Data $00,$00,$00,$00, $C0,$20,$C0,$20, $60,$60,$60,$60, $00,$40,$00,$40
.S
Data $00,$00,$00,$00, $60,$40,$60,$40, $C0,$60,$C0,$60, $00,$20,$00,$20

.pauseddata
Data $FFE,$FFE,$FFE,$60E,$666,$666,$666,$666,$666,$60E,$67E,$67E,$67E,$67E
Data $67E,$67E,$67E,$FFE,$FFE,$FFE
