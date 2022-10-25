; ID: 1668
; Author: Grey Alien
; Date: 2006-04-14 08:34:53
; Title: High Score Entry and Saving
; Description: High Score Entry and Saving

Const MAX_HIGH_SCORES = 10
Const HIGH_SCORE_NAME_LENGTH = 12
Const HIGH_SCORE_CHAR_WIDTH = 25 ;width of a letter in the font (50 point CourierNew)

;high scores
Dim HighScoreName$(MAX_HIGH_SCORES) ;Holds the hiscore strings
Dim HighScore%(MAX_HIGH_SCORES) ;Holds the hiscore integers
Dim HighScoreLevel%(MAX_HIGH_SCORES) ;Holds the hiscore levels
Dim HighName$(HIGH_SCORE_NAME_LENGTH) ;the name currently being entered
Global HighNameChar% = 0 ;pointer to the letter currently being entered
Global EnteringHighScore% = 0 ;app should set to 1 when entering high score
Global HighScoreDone% = 0 ;app should maintain this at 0, it gets set to 1 when they've finished typing and pressed enter

;Shadow Text
Global fntCourierNew% = ccLoadFont("Courier New", 30, True, False, False)
Global ShadowTextDepth% = 1

; -----------------------------------------------------------------------------
; Clear High Score
; -----------------------------------------------------------------------------
Function HighScoreClear()	
	For i = 1 To HIGH_SCORE_NAME_LENGTH
		HighName(i) = ""
	Next
End Function

Function HighScoreResetVars()
	HighScoreDone = 0
	EnteringHighScore = 0
End Function

; -----------------------------------------------------------------------------
; Enter High Score
; -----------------------------------------------------------------------------
Function HighScoreEnter(StartY, Special)
	;call this once per frame
	
;	Local charwidth = 12 ;width of a letter in the font ;24 pitch courierNew
	Local charwidth = HIGH_SCORE_CHAR_WIDTH
	Local x = (GAME_WIDTH - (charwidth*HIGH_SCORE_NAME_LENGTH)) / 2
	SetFont fntCourierNew	
	Local lt = 0
	Local pl = 0
	
	;output the current high name
	For lt = 1 To HIGH_SCORE_NAME_LENGTH ; Draw entered letters or dots if not entered.
		If HighName$(lt) <> "" Then
			If lt = HighNameChar 
				ccShadowText(x + (lt-1)*charwidth, StartY, HighName$(lt), 0)
			Else
				ccShadowText(x + (lt-1)*charwidth, StartY, HighName$(lt), 0)
			EndIf
		Else
			If lt = HighNameChar 
				ccShadowText(x + (lt-1)*charwidth, StartY, "_", 0)
			Else
				ccShadowText(x + (lt-1)*charwidth, StartY, ".", 0)
			EndIf
		EndIf
	Next 
	
	;detect cursor and backspace keys
;	If KeyHit(RIGHT_ARROW)									; Right key.
;		HighNameChar = HighNameChar + 1						; Move one letter to right.
;		If HighNameChar > HIGH_SCORE_NAME_LENGTH Then HighNameChar = 1		; Wrap to first letter if past letter 12.
;	EndIf
;	If KeyHit(LEFT_ARROW)									; Left key.
;		HighNameChar = HighNameChar - 1						; Move one letter to left.
;		If HighNameChar < 1 Then HighNameChar = HIGH_SCORE_NAME_LENGTH		; Wrap to last letter if past letter 1.
;	EndIf
	; 97 - 122 (letters), (48 - 57) numbers, 32 = space
	ltr = GetKey()											; Get all the keys that can be entered.

	If ltr = 8 Then ;8 = backspace.  Use get key so key repeat works
		HighNameChar = HighNameChar - 1						; Delete letter.
		If HighNameChar < 1 Then HighNameChar = 1			; Keep on first if needed.
		HighName$(HighNameChar) = "" 						; Clear letter from array.
	EndIf

	If (ltr > 96 And ltr < 123) Or (ltr >= 48 And ltr <= 57) Or (ltr = 32) Or (ltr > 64 And ltr < 91) Then
		If HighNameChar <= HIGH_SCORE_NAME_LENGTH
			HighName$(HighNameChar) = Upper$(Chr$(ltr))	; Put letter in array.
			HighNameChar = HighNameChar + 1					; Move to next letter position.
		EndIf
	EndIf
				
	If KeyHit(RETURN_KEY)									; Return key.
		Local name$ = ""
		For pl = 1 To HIGH_SCORE_NAME_LENGTH				; Put name in hiscore list.
			If HighName$(pl) = ""
				name$ = name$ + " "
			Else
				name$ = name$ + HighName$(pl)
			EndIf
		Next
		HighScoresSort(name$, Score)							; Sort hiscore table.
		HighScoresSave(Special)									; Save hiscore table.
		HighScoreDone = 1
		EnteringHighScore = 0
		Return 1
	Else
		Return 0
	EndIf
End Function

; -------------------------------------------------------------------------
; Save High Scores
; -------------------------------------------------------------------------
 ; These high score functions were modified from Tracer's from the Asteroid Shower example
Function HighScoresSave(Special%)
	For q = 0 To MAX_HIGH_SCORES-1
		HighScoreName$(q) = ccPadRight(HighScoreName$(q), HIGH_SCORE_NAME_LENGTH); Add spaces to fill up empty slots for all entries.
	Next
	
	hi = WriteFile("hiscore.dat")								; Open the file for writing.
	For q = 0 To MAX_HIGH_SCORES-1								; Go through all entries.
		For p = 1 To Len(HighScoreName$(q))						; Write each letter (rather than a string so we can change the ASCII code to avoid score hacking)
			a$ = Mid$(HighScoreName$(q),p,1)					; Get letter.
			as = Asc(a$)										; Make it ascii format.
			WriteByte hi,as - 19								; Decrease by 19 to make different character.
		Next
		WriteInt hi,HighScore(q)
		WriteInt hi,HighScoreLevel(q)
	Next
	;write special value shifted by big number
	WriteInt hi,Special + 917648
	CloseFile hi												; Close the file.
End Function

; -------------------------------------------------------------------------
; Load High Scores
; -------------------------------------------------------------------------
Function HighScoresLoad%(Special)
	hi = OpenFile("hiscore.dat")								; Open the file.
	If hi = 0													; Does the file exist?
		HighScoreName$(0) = "JAKE"							; If not then we fill the hiscore table with these.
		HighScore(0) = 50000
		HighScoreLevel(0) = 10
		HighScoreName$(1) = "HELEN"
		HighScore(1) = 45000
		HighScoreLevel(1) = 9
		HighScoreName$(2) = "CONAN"
		HighScore(2) = 40000
		HighScoreLevel(2) = 8
		HighScoreName$(3) = "CALLUM"
		HighScore(3) = 35000
		HighScoreLevel(3) = 7
		HighScoreName$(4) = "JASON"
		HighScore(4) = 30000
		HighScoreLevel(4) = 6
		HighScoreName$(5) = "DAMIEN"
		HighScore(5) = 25000
		HighScoreLevel(5) = 5
		HighScoreName$(6) = "PENNY"
		HighScore(6) = 20000
		HighScoreLevel(6) = 4
		HighScoreName$(7) = "TIM"
		HighScore(7) = 15000
		HighScoreLevel(7) = 3
		HighScoreName$(8) = "CHARLES"
		HighScore(8) = 10000
		HighScoreLevel(8) = 2
		HighScoreName$(9) = "ANDREW"  
		HighScore(9) = 5000
		HighScoreLevel(9) = 1
		HighScoresSave(Special)
	Else														; File does exist.
		For q = 0 To MAX_HIGH_SCORES-1							; Read all 10 hiscore entries.
			For p = 1 To HIGH_SCORE_NAME_LENGTH					; Read all bytes of the name.
				a = ReadByte(hi)								; Read a single byte.
				a = a + 19										; Increase by 19 to get good character.
				ac$ = ac$ + Chr$(a)								; Put character in temp string.
			Next
			HighScoreName$(q) = ac$								; Put loaded name in hiscore array.
			ac$ = ""											; Clear temp string.
			HighScore%(q) = ReadInt(hi)							; Put loaded score in hiscore array.
			HighScoreLevel%(q) = ReadInt(hi)							; Put loaded level in hiscore array.
		Next
		;read special value shifted by big number
		Special = ReadInt(hi) - 917648		
		CloseFile hi
	EndIf
	Return Special
End Function

; -------------------------------------------------------------------------
; Sort High Scores
; -------------------------------------------------------------------------
; This function will physically sort the
; hiscore table to put highest on top and
; lowest at the bottom. It uses a simple
; Bubble sort.
Function HighScoresSort(name$, Score%)
	HighScoreName$(MAX_HIGH_SCORES) = name$									; Not saved part of hiscore array gets the entered name.
	HighScore(MAX_HIGH_SCORES) = score									; Not saved part of hiscore array gets the gotten score.
	HighScoreLevel(MAX_HIGH_SCORES) = Level									; Not saved part of hiscore array gets the gotten score.
	
	; A bubble sort!
	; It checks to see if the score below is higher than the one above.
	; If so it will swap them.. after going through the entire
	; array it will have sorted it.. slow but effective.
	For bub1 = 0 To MAX_HIGH_SCORES
		counter = 0
		For bub2 = 0 To MAX_HIGH_SCORES-1-bub1
			a = HighScore(counter)
			b = HighScore(counter + 1)
			If b > a
				n1$ = HighScoreName$(counter)
				n2$ = HighScoreName$(counter + 1)
				l1% = HighScoreLevel(counter)
				l2% = HighScoreLevel(counter + 1)
				HighScore(counter) = b
				HighScore(counter + 1) = a
				HighScoreName$(counter) = n2$
				HighScoreName$(counter + 1) = n1$
				HighScoreLevel(counter) = l2
				HighScoreLevel(counter + 1) = l1				
			EndIf
			counter = counter + 1
		Next
	Next
End Function

; -----------------------------------------------------------------------------
; ccShadowText
; -----------------------------------------------------------------------------
Function ccShadowText(x%, y%, TheText$, Centre)
	;make a black shadow in the same font behind the text so it shows up on top of images
	;first store the current color
	red = ColorRed()
	green = ColorGreen()
	blue = ColorBlue()
	
	Color 0,0,0
	Text x + ShadowTextDepth, y + ShadowTextDepth, TheText, Centre
	Color red, green, blue
	Text x, y, TheText, Centre	
End Function

; -----------------------------------------------------------------------------
; pad a string with spaces on the right (best for left aligning strings)
; -----------------------------------------------------------------------------
Function ccPadRight$(TheString$, TheSize)
	sl = TheSize - Len(TheString$) ; Get length of string
	If sl > 0 Then ; Needs padding?
		For p = 1 To sl										
			z$ = z$ + " " ; make a blank string
		Next
		TheString$ = TheString$ + z$; add the blank string to the main string
	EndIf
	Return TheString$
End Function 

; -----------------------------------------------------------------------------
; Load a font and error if not found
; -----------------------------------------------------------------------------
Function ccLoadFont (TheFont$, Size, Bold, Italic, Underline)
	pointer = LoadFont(TheFont$, Size, Bold, Italic, Underline)
	If Not pointer Then
    	RuntimeError ("Error loading font "+TheFont$)
		End
	Else
    	Return Pointer	
  	EndIf
End Function
