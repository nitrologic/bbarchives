; ID: 1912
; Author: Vic 3 Babes
; Date: 2007-02-03 21:25:57
; Title: Binary Tetris in Colour
; Description: Uses same binary algorithm as Binary Tetris, but with colour

;3FF v1.2 - Harlequin Software 2002 - 2007
;Updated version 31 January 2007
Const VERSION$="v1.2"
AppTitle "3FF "+VERSION$
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
Const EAST=-1, WEST=1, CLOCKWISE=1, ANTICLOCKWISE=-1
;East and West are inverted because - oh never mind
Const ADDCOLOUR=True, ERASECOLOUR=False
;new for v1.2
Const EMPTYSQUARE=-1
Const SAMENORTH=1, SAMEEAST=2, SAMESOUTH=4, SAMEWEST=8

Global currentrow, shifter, rotation, numlines, startrow
Global speed, time, action, score

;new for v1.2
Global tiles=CreateImage(16,16,112)
Global empty=CreateImage(16,16)

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
;new for v1.2
Dim colourbrick(15,3)
Dim nextcolourbrick(15,3)
Dim colourlevel(10,21)

SeedRnd MilliSecs()
init()
;################################ M A I N   L O O P #############################
Repeat
	draw_title()
	clear_arrays(False)
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
		draw_play_area()
		time=MilliSecs()
		Repeat
			If action And LANDED
				get_a_brick()
				action=action And NOTLANDED
			Else
				If MilliSecs()-time > speed
					move_down()
					time=MilliSecs()
				Else
					If KeyDown(DOWNARROW) And (MilliSecs()-time > 40) ;delay key-repeat
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
				If Not(action And LANDED) Then xor_brick(ADDCOLOUR)
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
Local rownumber, row, column
	If clearlevel
		For rownumber=0 To 21
			leveldata(rownumber)=EMPTYROW
		Next
		leveldata(22)=HIDDENROW
		For row=2 To 21
			For column=0 To 9
				colourlevel(column,row)=EMPTYSQUARE
			Next
		Next
	EndIf
	For rownumber=0 To 3
		For rotation=0 To 15
			nextcolourbrick(rotation,rownumber)=EMPTYSQUARE
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
	For yloop=0 To 3 ;show next brick
		For square=0 To 3
			If nextcolourbrick(square,yloop) > EMPTYSQUARE
				DrawBlock tiles,220+(square Shl 4),20+(yloop Shl 4),nextcolourbrick(square,yloop)
			Else
				DrawBlock empty,220+(square Shl 4),20+(yloop Shl 4)
			EndIf
		Next
	Next
	action=action Or REDRAW
Return
End Function
;-----------------------------------
Function draw_play_area()
Local row, column
	For row=2 To 21 ;draw the current level data to the screen
		For column=0 To 9
			If colourlevel(column,row) > EMPTYSQUARE
				DrawBlock tiles,32+(column Shl 4),(row-2) Shl 4,colourlevel(column,row)
			Else
				DrawBlock empty,32+(column Shl 4),(row-2) Shl 4
			EndIf
		Next
	Next
Return
End Function
;-----------------------------------
Function draw_score()
Local x
	For x=0 To 3
		DrawBlock empty,220+(x Shl 4),112
	Next
	For x=0 To 3
		DrawBlock empty,220+(x Shl 4),162
	Next
	Text 222,113,numlines
	Text 222,163,score
Return
End Function
;-----------------------------------
Function draw_title()
Local row, column, tilenumber
	Restore title3ff
	For row=2 To 21
		Read tilenumber
		For column=0 To 9
			If tilenumber
				Read colourlevel(column,row)
			Else
				colourlevel(column,row)=EMPTYSQUARE
			EndIf
		Next
	Next
	draw_play_area()
	Text 112,192,"Harlequin Software",True
	Text 112,208,VERSION$,True
Return
End Function
;-----------------------------------
Function game_over_man()
Local x,y, rgb, r, g, b
	xor_colour(True)
	LockBuffer BackBuffer()
	For y=136 To 167
		For x=48 To 175
			rgb=ReadPixelFast(x,y) And $FFFFFF
			r=rgb Shr 16
			g=(rgb Shr 8) And $FF
			b=rgb And $FF
			r=r Shr 1
			g=g Shr 1
			b=b Shr 1
			WritePixelFast x,y,(r Shl 16) Or (g Shl 8) Or b
		Next
	Next
	UnlockBuffer BackBuffer()
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
	For rownumber=0 To 3
		For rotation=0 To 15
			colourbrick(rotation,rownumber)=nextcolourbrick(rotation,rownumber)
			Read nextcolourbrick(rotation,rownumber)
		Next
	Next
	rotation=0
	shifter=0
	If collided(0) Then action=action Or GAMEOVER Else draw_next_brick()
Return
End Function
;-----------------------------------
Function init()
Local tilenumber, i, x, y, w, help$, error, r, g, b, highlight
	SetBuffer ImageBuffer(empty)
	ClsColor 32,32,32
	Cls
	Color 64,64,64
	Text 3,1,"0"
	Restore brickcolours
	For bricknum=0 To 6
		Read r,g,b
		For tilenumber=0 To 15
			SetBuffer ImageBuffer(tiles,tilenumber+(bricknum Shl 4))
			ClsColor r,g,b
			Cls
		Next
		;lowlights
		For tilenumber=0 To 15
			SetBuffer ImageBuffer(tiles,tilenumber+(bricknum Shl 4))
			Color r Shr 1, g Shr 1, b Shr 1
			If Not(tilenumber And 8) Then Line 0,0,0,15
			If Not(tilenumber And 4) Then Line 0,15,15,15
		Next
		r=r Shl 1
		If r>255 Then r=255
		g=g Shl 1
		If g>255 Then g=255
		b=b Shl 1
		If b>255 Then b=255
		;hightlights
		For tilenumber=0 To 15
			SetBuffer ImageBuffer(tiles,tilenumber+(bricknum Shl 4))
			Color r,g,b
			Text 3,1,bricknum+1
			If Not(tilenumber And 1) Then Line 0,0,15,0
			If Not(tilenumber And 2) Then Line 15,0,15,15
		Next
	Next
	SetBuffer BackBuffer()
	Color 192,164,0
	For y=0 To 5
		Read help$
		Text 210,188+(26 * y),help$
	Next
	Color 128,96,0
	For y=0 To 5
		Read help$
		Text 220,201+(26 * y),help$
	Next
	Color 255,255,255
	Text 220,90,"Lines:"
	Text 220,140,"Score:"
Return
End Function
;-----------------------------------
Function join_bricks()
Local row, column, firsttile, adjacents
;this function originaly checked all 20 rows (2-21) - but only a maximum 6 rows can be affected by any given brick
;unfortunately, that means I now have to test that the row number isn't less than 2, or greater than 21 - but
;it's a small overhead considering we are no longer checking all 20 rows of the play area.
	For row=currentrow-1 To currentrow+4
		If row > 1	;would cause array index out of bounds if at top of level if less than 2
			If leveldata(row) And FULLVISIBLE
				For column=0 To 9
					adjacents=0
					If row<22	;hidden row off bottom - don't check it
						If colourlevel(column,row) > EMPTYSQUARE
							firsttile=(colourlevel(column,row) And $F0) ;first tile of that colour
							If column > 0
								If colourlevel(column-1,row) > EMPTYSQUARE
									If (colourlevel(column-1,row) And $F0)=firsttile Then adjacents=SAMEWEST
								EndIf
							EndIf
							If column < 9
								If colourlevel(column+1,row) > EMPTYSQUARE
									If (colourlevel(column+1,row) And $F0)=firsttile Then adjacents=adjacents Or SAMEEAST
								EndIf
							EndIf
							If row < 21
								If colourlevel(column,row+1) > EMPTYSQUARE
									If (colourlevel(column,row+1) And $F0)=firsttile Then adjacents=adjacents Or SAMESOUTH
								EndIf
							EndIf
							If colourlevel(column,row-1) > EMPTYSQUARE
								If (colourlevel(column,row-1) And $F0)=firsttile Then adjacents=adjacents Or SAMENORTH
							EndIf
							colourlevel(column,row)=firsttile+adjacents
						EndIf
					EndIf
				Next
			EndIf
		EndIf
	Next
Return
End Function
;-----------------------------------
Function move_brick(xadd)
	xor_brick(ERASECOLOUR)
	If collided(xadd)
		xor_brick(ADDCOLOUR)
	Else
		action=action Or REDRAW
	EndIf
Return
End Function
;-----------------------------------
Function move_down()
Local newlines, rownumber, column, row, emptyrows, n, square
	xor_brick(ERASECOLOUR)
	currentrow=currentrow+1
	If collided(0)
		newlines=0
		action=action Or LANDED
		currentrow=currentrow-1
		xor_brick(ADDCOLOUR)
		For row=0 To 3
			For column=0 To 3
				If colourbrick((rotation Shl 2)+column,row) > EMPTYSQUARE
					colourlevel(column+3-shifter,currentrow+row)=colourbrick((rotation Shl 2)+column,row)
				EndIf
			Next
		Next
		For rownumber=currentrow To currentrow+3
			If leveldata(rownumber)=FULLROW
				newlines=newlines+1
				lines(rownumber-currentrow)=rownumber
				currentbrick(rotation,rownumber-currentrow)=0
				leveldata(rownumber)=EMPTYROW
			EndIf
		Next
		If newlines
			score=score+(newlines*10) + (((newlines-1)*0.5)*(newlines*10))
;			Select newlines
;				Case 1 PlaySound(line1snd)
;				Case 2 PlaySound(line2snd)
;				Case 3 PlaySound(line3snd)
;				Case 4 PlaySound(line4snd)
;			End Select
			For column=0 To 9
				For row=0 To 3
					If lines(row)
						For square=0 To 9-column
							If lines(row) And 1
								If column=9
									colourlevel(0,lines(row))=EMPTYSQUARE
								Else
									If square=9
										colourlevel(9,lines(row))=EMPTYSQUARE
									Else
										colourlevel(square,lines(row))=colourlevel(square+1,lines(row))
									EndIf
								EndIf
							Else
								If column=9
									colourlevel(9,lines(row))=EMPTYSQUARE
								Else
									If square=9
										colourlevel(0,lines(row))=EMPTYSQUARE
									Else
										colourlevel(9-square,lines(row))=colourlevel(8-square,lines(row))
									EndIf
								EndIf
							EndIf
						Next
					EndIf
				Next
				join_bricks()
				draw_play_area()
				Delay 30: Flip False
			Next
			Delay 50
			For row=0 To 3
				lines(row)=0
			Next
			For column=0 To newlines
				For row=currentrow+3 To 1 Step-1
					If leveldata(row)=EMPTYROW And leveldata(row-1)>EMPTYROW
						leveldata(row)=leveldata(row-1)
						leveldata(row-1)=EMPTYROW
						For square=0 To 9
							colourlevel(square,row)=colourlevel(square,row-1)
							colourlevel(square,row-1)=EMPTYSQUARE
						Next
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
		join_bricks()
		draw_play_area()
		Flip False
	Else
		action=action Or REDRAW
	EndIf
Return
End Function
;-----------------------------------
Function pause()
Local oldtime, x, y
	oldtime=MilliSecs()-time
	For y=0 To 19
		For x=2 To 11
			DrawBlock empty,x Shl 4,y Shl 4
		Next
	Next
	Text(112,147,"PAUSED",True)
	Flip False
	Repeat
		Delay 1
		If KeyHit(ESCAPE) Then action=action Or ESCAPED
	Until KeyHit(SPACEBAR) Or (action And ESCAPED)
	FlushKeys()
	If Not(action And ESCAPED)
		draw_play_area()
		draw_next_brick()
		xor_colour(ADDCOLOUR)
		Flip False
		action=action And DRAWN
		time=MilliSecs()-oldtime
	EndIf
Return
End Function
;-----------------------------------
Function rotate_brick(modifier)
Local oldrotation
	xor_brick(ERASECOLOUR)
	oldrotation=rotation
	rotation=rotation + modifier
	If rotation > 3 Then rotation=0 ElseIf rotation < 0 Then rotation=3
	If collided(0)
		If shift_collided()
			rotation=oldrotation
			xor_brick(ADDCOLOUR)
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
Function xor_brick(drawcolour)
Local y
	xor_colour(drawcolour)
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
Function xor_colour(draw)
Local row, column
	For row=0 To 3
		For column=0 To 3
			If colourbrick((rotation Shl 2)+column,row) > EMPTYSQUARE
				If draw
					DrawBlock tiles,80+(shifter * -16)+(column Shl 4),(currentrow+row-2) Shl 4,colourbrick((rotation Shl 2)+column,row)
				Else
					DrawBlock empty,80+(shifter * -16)+(column Shl 4),(currentrow+row-2) Shl 4
				EndIf
			EndIf
		Next
	Next
Return
End Function
;-----------------------------------
.brickcolours
Data 88,80,192		;blue
Data 220,174,132	;tan
Data 108,162,196	;light blue
Data 145,98,200		;lilac
Data 140,166,100	;green
Data 188,186,4		;yellow
Data 156,94,92		;terracotta
Data "Play:","Move:","Rotate right:","Rotate left:","Pause:","Quit:"
Data "Enter","Cursor keys","Up-arrow","L-Ctrl","Spacebar","Escape"
.title3ff
Data 0,0
Data 1,$2,$A,$A,$A,$A,$A,$A,$A,$A,$8			;RULE
Data 0
Data 1,-1, $32,$3C, -1,$36,$38, -1,$36,$38,-1	;3FF
Data 1,-1, -1,$35,  -1,$35,-1,  -1,$35,-1, -1
Data 1,-1, $32,$3D, -1,$37,$38, -1,$37,$38,-1
Data 1,-1, -1,$35,  -1,$35,-1,  -1,$35,-1,-1
Data 1,-1, $32,$39, -1,$31,-1,  -1,$31,-1,-1
Data 0
Data 1,$2,$A,$A,$A,$A,$A,$A,$A,$A,$8			;RULE
Data 0,0,0,0,0,0,0,0,0
;brickpatterns
.column
;binary brick
Data $40,$00,$40,$00, $40,$00,$40,$00, $40,$F0,$40,$F0, $40,$00,$40,$00
;colour brick
Data -1,$4,-1,-1, -1,-1,-1,-1, -1,$4,-1,-1, -1,-1,-1,-1
Data -1,$5,-1,-1, -1,-1,-1,-1, -1,$5,-1,-1, -1,-1,-1,-1
Data -1,$5,-1,-1, $2,$A,$A,$8, -1,$5,-1,-1, $2,$A,$A,$8
Data -1,$1,-1,-1, -1,-1,-1,-1, -1,$1,-1,-1, -1,-1,-1,-1
.T
;binary brick
Data $00,$00,$00,$00, $40,$40,$00,$40, $E0,$60,$E0,$C0, $00,$40,$40,$40
;colour brick
Data -1,-1,-1,-1,    -1,-1,-1,-1,   -1,-1,-1,-1,    -1,-1,-1,-1
Data -1,$44,-1,-1,   -1,$44,-1,-1,  -1,-1,-1,-1,    -1,$44,-1,-1
Data $42,$4B,$48,-1, -1,$47,$48,-1, $42,$4E,$48,-1, $42,$4D,-1,-1
Data -1,-1,-1,-1,    -1,$41,-1,-1,  -1,$41,-1,-1,   -1,$41,-1,-1
.cube
;binary brick
Data $00,$00,$00,$00, $60,$60,$60,$60, $60,$60,$60,$60, $00,$00,$00,$00
;colour brick
Data -1,-1,-1,-1,   -1,-1,-1,-1,   -1,-1,-1,-1,   -1,-1,-1,-1
Data -1,$36,$3C,-1, -1,$36,$3C,-1, -1,$36,$3C,-1, -1,$36,$3C,-1
Data -1,$33,$39,-1, -1,$33,$39,-1, -1,$33,$39,-1, -1,$33,$39,-1
Data -1,-1,-1,-1,   -1,-1,-1,-1,   -1,-1,-1,-1,   -1,-1,-1,-1
.r
;binary brick
Data $00,$00,$00,$00,$20,$40,$30,$00,$20,$70,$20,$70,$60,$00,$20,$10
;colour brick
Data -1,-1,-1,-1,   -1,-1,-1,-1,    -1,-1,-1,-1,   -1,-1,-1,-1
Data -1,-1,$24,-1,  -1,$24,-1,-1,   -1,-1,$26,$28, -1,-1,-1,-1
Data -1,-1,$25,-1,  -1,$23,$2A,$28, -1,-1,$25,-1,  -1,$22,$2A,$2C
Data -1,$22,$29,-1, -1,-1,-1,-1,    -1,-1,$21,-1,  -1,-1,-1,$21
.L
;binary brick
Data $00,$00,$00,$00,$40,$00,$C0,$20,$40,$E0,$40,$E0,$60,$80,$40,$00
;colour brick
Data -1,-1,-1,-1,   -1,-1,-1,-1,    -1,-1,-1,-1,   -1,-1,-1,-1
Data -1,$14,-1,-1,  -1,-1,-1,-1,    $12,$1C,-1,-1, -1,-1,$14,-1
Data -1,$15,-1,-1,  $16,$1A,$18,-1, -1,$15,-1,-1,  $12,$1A,$19,-1
Data -1,$13,$18,-1, $11,-1,-1,-1,   -1,$11,-1,-1,  -1,-1,-1,-1
.Z
;binary brick
Data $00,$00,$00,$00, $C0,$20,$C0,$20, $60,$60,$60,$60, $00,$40,$00,$40
;colour brick
Data -1,-1,-1,-1,   -1,-1,-1,-1,   -1,-1,-1,-1,   -1,-1,-1,-1
Data $52,$5C,-1,-1, -1,-1,$54,-1,  $52,$5C,-1,-1, -1,-1,$54,-1
Data -1,$53,$58,-1, -1,$56,$59,-1, -1,$53,$58,-1, -1,$56,$59,-1
Data -1,-1,-1,-1,   -1,$51,-1,-1,  -1,-1,-1,-1,   -1,$51,-1,-1
.S
;binary brick
Data $00,$00,$00,$00, $60,$40,$60,$40, $C0,$60,$C0,$60, $00,$20,$00,$20
;colour brick
Data -1,-1,-1,-1,   -1,-1,-1,-1,    -1,-1,-1,-1,   -1,-1,-1,-1
Data -1,$66,$68,-1, -1,$64,-1,-1,   -1,$66,$68,-1, -1,$64,-1,-1
Data $62,$69,-1,-1, -1,$63,$6C,-1,  $62,$69,-1,-1, -1,$63,$6C,-1
Data -1,-1,-1,-1,   -1,-1,$61,-1,   -1,-1,-1,-1,   -1,-1,$61,-1
