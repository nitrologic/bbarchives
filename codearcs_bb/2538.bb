; ID: 2538
; Author: WildCat
; Date: 2009-07-21 18:54:52
; Title: Simple Tetris
; Description: 300 lines that make you playing tetris :)

Graphics3D 1024, 768, 32, 2 
SetBuffer (BackBuffer())

Global cx = 300
Global cy = 80
Global fld$ = String (" ", 260)
Global pcodes$[7]
Global score = 0

pcodes[1] = "  x   x   x   x "
pcodes[2] = "     xxx x"
pcodes[3] = "     xx  xx "
;what do you think? the game pieces coded in 4x4 space :)
pcodes[4] = " x   xx   x"
pcodes[5] = " x  xxx"
pcodes[6] = "     xx   xx"
pcodes[7] = "     xxx   x"
				   
;#Region [Image Data and Output]
Data 567
Data $89, $50, $4E, $47, $0D, $0A, $1A, $0A, $00, $00, $00, $0D, $49, $48, $44
Data $52, $00, $00, $00, $4C, $00, $00, $00, $10, $08, $03, $00, $00, $00, $8B
Data $05, $69, $95, $00, $00, $00, $04, $67, $41, $4D, $41, $00, $00, $AF, $C8
Data $37, $05, $8A, $E9, $00, $00, $00, $19, $74, $45, $58, $74, $53, $6F, $66
Data $74, $77, $61, $72, $65, $00, $41, $64, $6F, $62, $65, $20, $49, $6D, $61
Data $67, $65, $52, $65, $61, $64, $79, $71, $C9, $65, $3C, $00, $00, $00, $60
Data $50, $4C, $54, $45, $ED, $ED, $ED, $2E, $6A, $B2, $FB, $4D, $03, $DC, $9F
Data $57, $9E, $2F, $01, $F0, $F0, $F0, $D2, $30, $00, $A7, $A2, $A1, $BA, $78
Data $2B, $FF, $62, $12, $91, $75, $6E, $C7, $D0, $D2, $DA, $E6, $B3, $06, $0B
Data $61, $6D, $2A, $12, $A7, $BF, $CE, $5B, $61, $1B, $4A, $00, $2B, $7F, $1E
Data $04, $00, $00, $00, $61, $A2, $EB, $E3, $3D, $00, $D7, $C5, $86, $74, $3A
Data $2D, $4A, $00, $00, $FF, $6F, $1E, $C2, $BE, $BD, $25, $00, $2B, $26, $00
Data $00, $00, $00, $2B, $E2, $E6, $DF, $DD, $E0, $E1, $D9, $87, $2B, $0A, $00
Data $00, $01, $5D, $49, $44, $41, $54, $78, $DA, $A4, $93, $8B, $92, $83, $20
Data $0C, $45, $11, $41, $05, $7C, $AC, $28, $2A, $6E, $6B, $FF, $FF, $2F, $37
Data $09, $58, $75, $EA, $D6, $9D, $6E, $B4, $3A, $A3, $CD, $F1, $E6, $26, $61
Data $6C, $61, $97, $21, $B7, $43, $D2, $EF, $97, $78, $CD, $34, $7A, $D4, $33
Data $93, $73, $0E, $B7, $03, $E1, $82, $74, $06, $CB, $AD, $E3, $CE, $6A, $6D
Data $05, $17, $36, $97, $A4, $8B, $B4, $95, $6D, $F3, $27, $65, $69, $D7, $75
Data $7D, $D4, $65, $95, $F2, $4A, $59, $01, $57, $EE, $6A, $83, $94, $09, $5F
Data $23, $2C, $9C, $31, $73, $A9, $BB, $EE, $DE, $AC, $6F, $23, $0C, $3D, $33
Data $6D, $13, $85, $69, $A7, $BC, $F7, $CA, $59, $20, $72, $6E, $35, $3C, $82
Data $7C, $96, $DE, $4D, $50, $B6, $C1, $86, $44, $CA, $A2, $7A, $E0, $83, $E1
Data $96, $EC, $CA, $2C, $9F, $B0, $91, $7B, $8A, $02, $F5, $B9, $6C, $8C, $F9
Data $43, $6B, $48, $56, $0E, $72, $2A, $0D, $5A, $3A, $4A, $1F, $2A, $FA, $86
Data $4C, $2B, $73, $06, $AB, $B9, $22, $98, $A3, $32, $B3, $7A, $55, $46, $12
Data $B6, $32, $87, $6F, $BA, $89, $24, $48, $2D, $A7, $FE, $0C, $06, $65, $06
Data $9A, $F7, $1C, $60, $FA, $DC, $B3, $90, $5C, $90, $67, $07, $D8, $D1, $B3
Data $19, $9A, $49, $AE, $29, $64, $4D, $73, $FC, $12, $28, $D9, $C3, $44, $F0
Data $08, $CB, $7B, $A7, $8C, $69, $01, $DA, $54, $60, $D9, $7C, $9D, $09, $F8
Data $F7, $0E, $56, $04, $8B, $A8, $DA, $77, $9E, $E1, $A0, $09, $CE, $11, $95
Data $4D, $39, $CD, $58, $E8, $E6, $AA, $0C, $34, $04, $C3, $52, $10, $53, $24
Data $B1, $9B, $FD, $E9, $9C, $61, $CD, $A3, $E0, $0E, $58, $E3, $23, $CC, $FF
Data $C1, $33, $30, $AA, $FA, $C2, $6E, $56, $C3, $0D, $2E, $E6, $64, $CE, $8E
Data $B1, $68, $9B, $65, $34, $62, $FF, $DE, $4D, $4C, $86, $65, $D2, $2F, $84
Data $0F, $76, $33, $36, $75, $5E, $77, $F2, $B9, $9B, $9F, $2A, $3B, $56, $C6
Data $2E, $F4, $6C, $F1, $23, $C0, $00, $F1, $CE, $23, $39, $DF, $D0, $1E, $CD
Data $00, $00, $00, $00, $49, $45, $4E, $44, $AE, $42, $60, $82

out = WriteFile ("lb3d.png")
Read size
For a = 1 To size
	Read b
	WriteByte out,b
Next
CloseFile (out)
;#End Region

Function DrawGrids()	
	Color 64,64,64
	For a = 80 To 480 Step 20
		Line (cx, cy+a, cx+260, cy+a)
	Next
	For a = 0 To 260 Step 20
		Line (cx+a, cy+80, cx+a, cy+480)
	Next	
		
	Color 192,192,192
	Rect (cx, cy, 400, 560, 0)
	Line (cx, cy+80, cx+399, cy+80)
	Line (cx, cy+480, cx+399, cy+480)
	Line (cx+260, cy+80, cx+260, cy+480)
End Function

Function DrawBox (gx, gy, col)
	Color (col And $ff0000) Shr 16, (col And $ff00) Shr 8, col And $ff
	Rect (cx+(gx-1)*20+1, cy+80+(gy-1)*20+1, 19, 19)
End Function

Function DrawPiece (gx, gy, col, pcode$)
	a = 1
	For y = 1 To 4
		For x = 1 To 4
			If Mid$(pcode$, a, 1) = "x" Then DrawBox (gx+x-1, gy+y-1, col)
			a=a+1
		Next
	Next
End Function

Function DrawField (pcode$)
	a = 1
	For y = 1 To 20
		For x = 1 To 13
			sym$ = Mid$(pcode$, a, 1)
			Select True
				Case sym$ = "1": DrawBox (x, y, $ff0000)
				Case sym$ = "2": DrawBox (x, y, $00ff00)
				Case sym$ = "3": DrawBox (x, y, $0000ff)
				Case sym$ = "4": DrawBox (x, y, $ff00ff)
				Case sym$ = "5": DrawBox (x, y, $ffff00)
				Case sym$ = "6": DrawBox (x, y, $00ffff)
				Case sym$ = "7": DrawBox (x, y, $ffffff)
			End Select
			
			a=a+1
		Next
	Next	
End Function

Function InsertPiece (gx, gy, col, pcode$)
	a = 1
	For y = 1 To 4
		For x = 1 To 4
			If Mid$(pcode$, a, 1) = "x" Then 
				fx = x+gx-1
				fy = y+gy-1
				If (fx < 1) Or (fx > 13) Then Return 0
				If (fy > 20) Then Return 0
				
				If fy >= 1 Then 
					sym$ = Mid(fld$, fx+((fy-1)*13), 1)
					
					If sym$ <> " " Then Return 0
					
					fld$ = Left(fld$, fx+((fy-1)*13)-1)+col+Mid(fld$, fx+((fy-1)*13)+1)	
				EndIf			
			EndIf
			a=a+1
		Next
	Next	
	Return 1	
End Function

Function CheckInsertPiece (gx, gy, pcode$)
	a = 1
	For y = 1 To 4
		For x = 1 To 4
			If Mid$(pcode$, a, 1) = "x" Then 
				fx = x+gx-1
				fy = y+gy-1
				If (fx < 1) Or (fx > 13) Then Return 0
				If (fy > 20) Then Return 0
				
				If fy >= 1 Then
					sym$ = Mid(fld$, fx+((fy-1)*13), 1)
					
					If sym$ <> " " Then Return 0
				EndIf
			EndIf
			a=a+1
		Next
	Next	
	Return 1	
End Function

Function RotatePiece$(piece$)
	piece$ = piece$+String(" ", 16-Len(piece$)) ;adding spaces upto 16 symbols
	r$ = Mid(piece$, 13, 1)+Mid(piece$, 9, 1)+Mid(piece$, 5, 1)+Mid(piece$, 1, 1)
	r$ = r$ + Mid(piece$, 14, 1)+Mid(piece$, 10, 1)+Mid(piece$, 6, 1)+Mid(piece$, 2, 1)
	r$ = r$ + Mid(piece$, 15, 1)+Mid(piece$, 11, 1)+Mid(piece$, 7, 1)+Mid(piece$, 3, 1)
	r$ = r$ + Mid(piece$, 16, 1)+Mid(piece$, 12, 1)+Mid(piece$, 8, 1)+Mid(piece$, 4, 1)
	Return r$
End Function

Function WipeLines()
	For y = 20 To 1 Step -1
		wipe = 1
		For x = 1 To 13
			sym$ = Mid$(fld$, x+((y-1)*13), 1)
			If sym$ = " " Then 
				wipe = 0
				Exit
			EndIf
		Next		
		
		If wipe Then
			fld$ = String(" ", 13)+Left(fld$, (y-1)*13)+Mid(fld$, y*13+1)
			score=score+1
			y=y+1
		EndIf		
	Next
End Function

pnum = Rand(1, 7)
pnext = Rand(1, 7)
piece$ = pcodes[pnum]
pnextcode$ = pcodes[pnext]
px = 5
py = 0

pStart = MilliSecs()

gameover = 0

lb3d = LoadImage ("lb3d.png")

Repeat
	Cls

	DrawGrids()
	
	If Not gameover Then		
		opx = px
		opy = py
		DrawPiece (px, py, $ff0000, piece$)
		
		If (pStart+500<MilliSecs()) Or (KeyDown(208)) Then 
			py=py+1
			pStart = MilliSecs()
		EndIf
		
		If CheckInsertPiece (px, py, piece$) = 0 Then 
			InsertPiece (opx, opy, pnum, piece$)
			If opy <= 1 Then gameover=1
						
			WipeLines()
			pnum = pnext
			piece$ = pnextcode$
			
			pnext = Rand(1, 7)
			pnextcode$ = pcodes[pnum]
			px = 5
			py = -2	
		EndIf	
	
		opx = px
		opy = py
		
		If KeyHit(203) Then px=px-1
		If KeyHit(205) Then px=px+1
			
		If CheckInsertPiece (px, py, piece$) = 0 Then 
			px = opx
			py = opy			
		EndIf
			
		If KeyHit (200) Then
			savePiece$ = piece$
			piece$ = RotatePiece$(piece$)	
			If CheckInsertPiece (px, py, piece$) = 0 Then piece$ = savePiece$
		EndIf
		DrawField (fld$)
	Else
		DrawField (fld$)
		Text 0,0, "Game"
		Text 0,12, "Over"
		For x = 1 To 40
			For y = 1 To 30
				pix = ReadPixel (x, y)
				If pix And $ff Then DrawBox (x-5, y-3, $ffffff)
			Next
		Next		
	EndIf
		

	Rect (564, 286, 132, 272, 0)
	Text 570,408, "Place for an ad"
	
	If KeyHit(1) Then Exit
	
	Color 255,255,255
	Text cx+264, cy+86, "Score: "+score
	Text cx+264, cy+106, "Next: "
	
	DrawPiece (14, 3, $ffff00, pnextcode$)
	
	DrawImage (lb3d, 622, 143)
	
	Text 0, 0, "X: "+MouseX()+" Y: "+MouseY()+" Z: "+MouseZ()
	Flip
Forever

End
