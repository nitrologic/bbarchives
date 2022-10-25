; ID: 1996
; Author: Matt Merkulov
; Date: 2007-04-21 05:16:26
; Title: Scripts using example
; Description: Simple screen-to-screen tile-based RPG script

;Scripts using example by Matt Merkulov

Graphics 640, 480, 32

; Loading image strip - tiles (last - sprite)
Global tiles = LoadAnimImage("script.png", 20, 20, 0, 12)
; Color of a mask - purple
MaskImage tiles, 255, 0, 255

; The buffer for tilemaps
Global buf = CreateImage(640, 480)
Global bufb = ImageBuffer(buf)

; Coordinates of the player, number of the screen
Global px = 16, py = 12, sc = 0

; Reading tilemaps into memory
Dim scr$(1, 19)
For n1 = 0 To 1
	For n2 = 0 To 19
		Read scr$(n1, n2)
	Next
Next

SetBuffer BackBuffer()
SetFont LoadFont("Arial", 20, True)
; Setting the screen number 0
setscr 0

Repeat
	; The increments depending on pressed keys
	dx = KeyDown(205) - KeyDown(203)
	If dx = 0 Then dy = KeyDown(208) - KeyDown(200)Else dy = 0
	; Moving
	If dx <> 0 Or dy <> 0 Then move dx, dy
	; Launching script by pressing a space before the guard
	If py > 0 And KeyHit(57) Then If land$(px, py - 1) = "J" Then runscript
Until KeyHit(1)

WaitKey

Function setscr(n1)
	; Drawing tiles in the buffer of tilemap
	SetBuffer bufb
	For n2 = 0 To 19
		For n3 = 0 To 31
			DrawBlock tiles, n3 * 20, n2 * 20, Asc(Mid$(scr$(n1, n2), n3 + 1, 1)) -65
		Next
	Next
	SetBuffer BackBuffer()
	sc = n1
	drawbuf
End Function

Function drawbuf()
	; Reinitialisation
	DrawBlock buf, 0, 0
	DrawImage tiles, px * 20, py * 20, 11
	Flip
End Function

Function move(dx, dy)
	; Restriction from exit out of the screen
	If dx + px >= 0 And dx + px =< 31 And dy + py >= 0 And dy + py <= 19 Then
		Select land$(px + dx, py + dy)
			; It is possible to go only on a grass, a floor and road
			Case"E", "H", "I"
				px = px + dx
				py = py + dy
				; Moving between screens
				If py = 0 And sc = 0 Then
					py = 18
					setscr 1
				ElseIf py = 19 And sc = 1 Then
					py = 1
					setscr 0
				End If
				drawbuf
				Delay 100
		End Select
	End If
End Function

; Returning number tile on coordinates
Function land$(x, y)
	Return Mid$(scr$(sc, y), x + 1, 1)
End Function

Function runscript()
	Restore script
	Repeat
		Read m$
		If m$ = "" Then Exit
		; Addresses of dividers in line
		i1 = Instr(m$, " ")
		i2 = Instr(m$, "/", i1 + 1)
		i3 = Instr(m$ + "/", "/", i2 + 1)
		; Extracting name of a command
		cmd$ = Mid$(m$, 1, i1 - 1)
		; Splitting a line into parameters
		par1 = Mid$(m$, i1 + 1, i2 - i1 - 1)
		par2$ = Mid$(m$, i2 + 1, i3 - i2 - 1)
		par3$ = Mid$(m$, i3 + 1)
		x = par1:y = par2$
		Select cmd$
			Case"SPEAK"
				; This command prints the text in a window
				Textwindow par1, par2$
			Case"MOVE"
				; This command moves the player to the set point
				drawbuf
				; Horizontal movement
				x = x - px
				For dx = 1 To Abs(x)
					move Sgn(x), 0
				Next
				; Vertical movement
				y = y - py
				For dy = 1 To Abs(y)
					move 0, Sgn(y)
				Next
			Case"CHANGE"
				; This command changes tile with the given coordinates
				; Drawing tile in the buffer of tilemap
				SetBuffer bufb
				DrawBlock tiles, x * 20, y * 20, Asc(par3$) -65
				SetBuffer BackBuffer()
				; Change of a map tile - replacement of a symbol in line
				scr$(sc, y) = Mid$(scr$(sc, y), 1, x) + par3$ + Mid$(scr$(sc, y), x + 2)
			Default
				; Error message
				DebugLog "Unknow command '" + cmd$ + "'"
				Stop
		End Select
	Forever
	drawbuf
End Function

Function Textwindow(v, txt$)
	; A window of the text
	FlushKeys
	drawbuf
	; Drawing a window
	Color 255, 255, 255
	Rect 0, 400, 640, 80
	Color 128, 128, 128
	Rect 3, 403, 637, 77
	Color 192, 192, 192
	Rect 2, 402, 636, 76
	; Drawing the character who is speaking
	DrawImage tiles, 10, 410, 9 + v
	Color 0, 0, 0
	y = 410

	For n = 1 To Len(txt$)
		k$ = Mid$(txt$, n, 1)
		; A blank - the beginning of a new word
		If k$ = " " Then
			; If length of a piece of a phrase with a new word is more admissible - transition to a new line
			If StringWidth(m$ + w$) > 490 Then
				Text 40, y, m$
				y = y + 20
				m$ = w$
			Else
				; Else we'll add this word to a phrase
				m$ = m$ + " " + w$
			End If
			w$ = ""
		Else
			w$ = w$ + k$
		End If
	Next
	; Printing the rest
	Text 40, y, m$ + " " + w$
	Flip
	WaitKey
End Function

; Maps
Data"AAAAAAAAAAAAAAAEEAAAAAAAAAAAAAAA"
Data"BBBBBBBBBBBBBBAEEABBBBBBBBBBBBBB"
Data"FFFFFFFFFFFFFFBDDBFFFFFFFFFFFFFF"
Data"GGGGGGGGGGGGGGGIIGGGGGGGGGGGGGGG"
Data"GGGGGGGGGGGGGGGIIGGGGGGGGGGGGGGG"
Data"GGGGGGGGGGGGGGGIIGGGGGGGGGGGGGGG"
Data"GGGGGGGGGGGGGGGIIGGGGGGGGGGGGGGG"
Data"HHHHHHHHHHHHHHJJJJHHHHHHHHHHHHHH"
Data"HHHHHHHHHHHHHHJHHJHHHHHHHHHHHHHH"
Data"HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH"
Data"HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH"
Data"HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH"
Data"HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH"
Data"HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH"
Data"HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH"
Data"HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH"
Data"HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH"
Data"HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH"
Data"HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH"
Data"HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH"

Data"AAAAAAAAAAAAAAAEEAAAAAAAAAAAAAAA"
Data"BCBBBCBBBCBBBCBEEBCBBBCBBBCBBBCB"
Data"EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE"
Data"EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE"
Data"AAAAAEAAAAAEAAAEEAAAEAAAAAEAAAAA"
Data"BBABBDBBABBDBBAEEABBDBBABBDBBAEE"
Data"EEAEEEEEAEEEEEAEEAEEEEEAEEEEEAEE"
Data"EEAEEEEEAEEEEEAEEAEEEEEAEEEEEAEE"
Data"EEAEEEEEAEEEEEAEEAEEEEEAEEEEEAEE"
Data"AAAAAAAAAAAAAAAEEAAAAAAAAAAAAAAA"
Data"CBABCBCBABCBCBAEEABCBCBABCBBCABC"
Data"EEAEEEEEAEEEEEAEEAEEEEEAEEEEEAEE"
Data"EEAEEKEEAEEEEEAEEAEEEEEAEEEEEAEE"
Data"EEAEEEEEAEEEEEAEEAEEEEEAEEEEEAEE"
Data"EEAEEEEEAEEEEEAEEAEEEEEAEEEEEAEE"
Data"AAAAAEAAAAAEAAAEEAAAEAAAAAEAAAAA"
Data"BBCBBDBBCBBDBCBEEBCBDBBCBBDBBCBB"
Data"EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE"
Data"AAAAAAAAAAAAAAAEEAAAAAAAAAAAAAAA"
Data"BBBBBBBBBBBBBBAEEABBBBBBBBBBBBBB"

; Script data
.script
Data"SPEAK 0/Stop! Who are there?"
Data"SPEAK 2/My name is Leonard, I have come from the city of SandWorm to battle huge spiders!"
Data"SPEAK 0/Leonard? I heard about you. You may pass. Speak with the captain of palace guards, 2-nd door along the corridor to the left."
Data"CHANGE 15/7/H"
Data"CHANGE 16/7/H"
Data"CHANGE 13/7/J"
Data"CHANGE 18/7/J"
Data"CHANGE 15/2/E"
Data"CHANGE 16/2/E"
Data"MOVE 16/1"
Data"CHANGE 15/2/D"
Data"CHANGE 16/2/D"
Data"MOVE 16/0"
Data"MOVE 16/17"
Data"MOVE 5/17"
Data"CHANGE 5/16/E"
Data"MOVE 5/13"
Data"SPEAK 2/Greetings."
Data"SPEAK 1/Greetings. Your name is Leonard, isn't it?"
Data"SPEAK 2/Yes, I am Leonard."
Data"SPEAK 1/My name is Jean. I listen to you."
Data"SPEAK 2/I heard, you are collecting people for a mountain campaign, to destroy huge spiders."
Data"SPEAK 1/Yes, these spiders already represent threat."
Data"SPEAK 2/I wish to join you."
Data"SPEAK 1/Well, Leonard. I heard about your feats, think, we shall do without examinations. For now, go eat something. Tomorrow morning we will go."
Data"MOVE 5/17"
Data"CHANGE 5/16/D"
; An empty line - the end of a script
Data""
