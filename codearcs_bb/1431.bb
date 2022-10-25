; ID: 1431
; Author: Dr. Wildrick
; Date: 2005-07-30 09:56:03
; Title: SoundEx Search
; Description: Generates SoundEx search codes

Function SoundEx$(sWord$)
;****************************************************************
; SoundEx code Generator - Cherry Cola Film Studios LLC
;****************************************************************

; Set the input lookup table and the return codes
; note the return code is the position of the letter in the
; first string +1 This weeds out non-letters. Any return code of
; 0 will return a null string
; Note A,E,I,O,U,Y,H, and W return a Null string

; to use this function is simple:
; B$ = SoundEx$(A$)
; Where A$ is the word you want the SoundEx code for
; and B$ is the returned SoundEx code as a 4 byte string
; For example the word "Gothic" returns a code of "G320"


l1$= "BFPVCGJKQSXZDTLMNR"
l2$="0111122222222334556"


    ; Get the First letter
    Num$ = Upper$(Mid$(sWord$, 1, 1)) 
    sLastCode$ = num$
		sLastCode$=Mid$(l2$,(Instr(l1$,sLastCode$)+1),1) 
		If  sLastCode$= "0" Then sLastCode$ = ""
		
   lWordLength = Len(sWord$)
        
    ;Create the code starting at the second letter.
    For I = 2 To lWordLength
        sChar$ = Upper$(Mid$(sWord$, I, 1))

	    sChar$=Mid$(l2$,(Instr(l1$,sChar$)+1),1) 
		If sChar$= "0" Then sChar$ = ""
	
        ; If two letters that are the same are Next To Each other
        ; only count one of them
        If Len(sChar$) > 0 And sLastCode$ <> sChar$ Then
            Num$ = Num$ + sChar$
        End If
        sLastCode$ = sChar$
    Next
    ; Make sure code isn't longer Then 4 letters
    SEx$ = Mid$(Num$, 1, 4) 
    ;Make sure the code is at least 4 characters long
    If Len(Num$) < 4 Then
        SEx$ = SEx$  + String$("0",4 - Len(Num$))
    End If
Return sex$
