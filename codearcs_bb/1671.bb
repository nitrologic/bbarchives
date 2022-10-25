; ID: 1671
; Author: Grey Alien
; Date: 2006-04-14 09:16:00
; Title: Full-Screen Text Input
; Description: Full-Screen Text Input

Const Screenwidth = 800
Graphics Screenwidth,600,32

Const ESCAPE = 1
Const RETURN_KEY = 28

;Text Input
Const ccTextInputMax = 255
Global ccTextInputMode = 0 ;calling app may want to set this to 1 so it doesn't process other input (it won't affect the proc here though)
Dim ccTextArray$(ccTextInputMax) ;variable used by TextInputMode to store what the user is typing.
Global ccText$ ;final output of ccTextArray
Global ccTextCurrentChar% ;used by ccTextInput to track the current character
Global ccTextInputLength = 20 ;default 20 chars.  Change to whatever you want.
Global ccTextInputCaption$ = "" ;this prints above the input box.

Global fntCourierNew% = ccLoadFont("Courier New", 30, True, False, False)		

ccTextInputInit()
ccTextInputMode = 1
Repeat
	Cls
		If ccTextInputMode = 1 Then ccTextInput(fntCourierNew, 100,20)
	Flip
Until KeyHit(ESCAPE)

; -----------------------------------------------------------------------------
; Text Input
; -----------------------------------------------------------------------------
Function ccTextInputInit()
	;clear the text array
	For i = 0 To ccTextInputMax
		ccTextArray(i) = ""		
	Next
	;clear the other variables
	ccText = "" ;initialise the text
	ccTextCurrentChar = 1	
	ccTextInputCaption = ""
	ccTextInputMode = 0

	;clear any keys so they don't interfere with ccTextInput()
	FlushKeys;
	FlushMouse;
End Function		

Function ccTextInputFill(StartText$)
	;fill the text array
	For i = 1 To Len(StartText)
		ccTextArray(i) = Mid(StartText, i, 1)		
	Next
End Function
		
Function ccTextInput(Font%, StartY, MaxLength%)
	Local charwidth = 15
	Local x = (ScreenWidth - (charwidth*MaxLength)) / 2
	SetFont Font
	
	Local lt = 0
	
	;output the caption
	Color 240,240,0
	ccShadowText(ScreenWidth/2, StartY-50, ccTextInputCaption, 1)
	
	Color 240,240,240
	;output the current text
	For lt = 1 To MaxLength ; Draw entered letters or dots if not entered.
		If ccTextArray$(lt) <> "" Then
			If lt = ccTextCurrentChar 
				ccShadowText(x + (lt-1)*charwidth, StartY, ccTextArray$(lt),0)
			Else
				ccShadowText(x + (lt-1)*charwidth, StartY, ccTextArray$(lt),0)
			EndIf
		Else
			;cursor (_) or blank space (.)
			If lt = ccTextCurrentChar 
				ccShadowText(x + (lt-1)*charwidth, StartY, "_",0)
			Else
				ccShadowText(x + (lt-1)*charwidth, StartY, ".",0)
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
		ccTextCurrentChar = ccTextCurrentChar - 1						; Delete letter.
		If ccTextCurrentChar < 1 Then ccTextCurrentChar = 1			; Keep on first if needed.
		ccTextArray$(ccTextCurrentChar) = "" 						; Clear letter from array.
	EndIf

	If (ltr >= 32 And ltr <= 126) Then ;all chars
	;If (ltr > 96 And ltr < 123) Or (ltr >= 48 And ltr <= 57) Or (ltr = 32) Or (ltr > 64 And ltr < 91) Then ;no special chars
		If ccTextCurrentChar <= MaxLength
			ccTextArray$(ccTextCurrentChar) = Chr$(ltr)	; Put letter in array.
			ccTextCurrentChar = ccTextCurrentChar + 1					; Move to next letter position.
		EndIf
	EndIf

	Local pl=0
					
	If KeyHit(RETURN_KEY)									; Return key.
		ccText$ = ""
		For pl = 1 To MaxLength				
			If ccTextArray$(pl) = ""
				;don't put spaces in ;ccText$ = ccText$ + " "
			Else
				ccText$ = ccText$ + ccTextArray$(pl)
			EndIf
		Next
				
		FlushKeys
		FlushMouse
		ccTextInputMode = 0 ;switch the mode off so calling proc knows user is done.
		Return 1 ;OK
	Else
		;if escape is pressed, abort.
		If KeyHit(ESCAPE)
			ccText$ = ""
			FlushKeys
			FlushMouse
			ccTextInputMode = 0 ;switch the mode off so calling proc knows user is done.
			Return -1 ;cancel
		EndIf
		
		Return 0 ;user has not finished yet
	EndIf
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
