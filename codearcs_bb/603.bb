; ID: 603
; Author: Russell
; Date: 2003-02-27 01:31:43
; Title: A much better input function
; Description: Prevent the user from entering certain characters while input-ing

Function GetInput$(x,y,sPrompt$,sFilter$,sDefault$,iMaxLength)
	; x = x location of the prompt, if any, or text input
	; y = y location ...
	; sPrompt$ is the prompt, such as "Please enter your name:"
	; sFilter$ is very useful. It ONLY allows the user to enter certain characters. For example, "ync" would only allow "y","n" or "c"
	;          There are also a few special 'codes': 
	;				"/all" or "" means allow anything to be entered
	; 				"/123" allows only 0 through 9 to be entered
	;				"/abc" allows only letters of the alphabet to be entered
	
	FlushKeys
	iFlashInterval = 300		; The blinking cursor speed
	sTotal$ = sDefault$
	iNumDigits = Len(sDefault$)
	
	If Lower$(sFilter$) = "/123" Then sFilter$ = "0123456789"					; All the numbers
	If Lower$(sFilter$) = "/abc" Then sFilter$ = "abcdefghijklmnopqrstuvwxyz"	; All the letters
	
	iTotalWidth = StringWidth(sPrompt$) + (iMaxLength * FontWidth())
	iTotalHeight = FontHeight()
	
	hTextBuffer = CreateImage(iTotalWidth,iTotalHeight)	; Where the text will be drawn before blitting to the backbuffer()
	hCleanCopy = CreateImage(iTotalWidth,iTotalHeight)	; Will hold a clean copy of the backbuffer (not the whole thing)
	MaskImage hTextBuffer,255,0,255						; Make the text background transparent so we can show text with BG showing
	SetBuffer ImageBuffer(hTextBuffer)					; We're going to draw to the text buffer
	ClsColor 255,0,255									; Temporarily make the cls color the transparent color (magenta)
	Cls													; Now clear to magenta
	; Foreground (text) will be drawn in the current color
	
	CopyRect x,y,iTotalWidth,iTotalHeight,0,0,BackBuffer(),ImageBuffer(hCleanCopy)	; Save a clean copy of the back buffer where the
																					; 	text is going to be

	SetBuffer BackBuffer()
	Repeat
		; Blinking cursor code *******************************************************************************************************
		iCurrentTime = MilliSecs()
		If bFlash = True Then
			If (iCurrentTime - iOldFlashTime) >= iFlashInterval Then 
				bFlash = False
				iOldFlashTime = MilliSecs()
			EndIf
		Else
			If (iCurrentTime - iOldFlashTime) >= iFlashInterval Then 
				bFlash = True
				iOldFlashTime = MilliSecs()
			EndIf
		EndIf
		
		; Input starts here **********************************************************************************************************
		iKeyPressed = GetKey()
		If iKeyPressed = 13 Then
			sKeyPressed$ = ""
		Else
			sKeyPressed$ = Chr$(iKeyPressed)
		EndIf
		
		; If the key passes, add it to the total *************************************************************************************
		If iKeyPressed Then
			If (sFilter$ = "/all") Or (sFilter$ = "") Or (Instr(sFilter$,sKeyPressed$) > 0) Then ; "all" does not filter any keys out
				If Len(sTotal$) < iMaxLength Then
					sTotal$ = sTotal$ + sKeyPressed$								; Add it to the total string if it passes
					iNumDigits = iNumDigits + 1
				EndIf
			EndIf
		EndIf
		
		; If backspace was pressed, delete the last character from the total and update the number of digits *************************
		If KeyDown(14) And iNumDigits > 0 Then
			sTotal$ = Left$(sTotal$,iNumDigits - 1)
			iNumDigits = iNumDigits - 1
			Delay 50
		EndIf
		
		; Draw the clean background and then the text on the backbuffer() ************************************************************
		DrawBlock hCleanCopy,x,y

		; Draw the cursor if enough time has passed (change iFlashInterval for different speeds) *************************************
		If Len(sTotal$) = iMaxLength Then
			rx = StringWidth(sPrompt$ + sTotal$) - StringWidth(Right$(sTotal$,1))
			rw = StringWidth(Right$(sTotal$,1))
		Else
			rx = StringWidth(sPrompt$) + (Len(sTotal$) * FontWidth())
			rw = FontWidth()
		EndIf
 
		If bFlash = True Then
			Text x,y,sPrompt$ + sTotal$
			Rect x + rx,y,rw,FontHeight(),True
		Else
			Text x,y,sPrompt$ + sTotal$
		EndIf

		Flip
	Until iKeyPressed = 13 ; This is the 'return/enter' key
	
	ClsColor 0,0,0	; Reset back to black
	Return sTotal$
End Function
