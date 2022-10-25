; ID: 1575
; Author: Valorden
; Date: 2005-12-23 15:07:06
; Title: File Saving and Loading
; Description: Simple File Saving and Loading

; Simple file save
;
; Hope this makes sense :o)
; Any suggestions for this are welcomed.

Graphics 800, 600, 32, 2
SetBuffer BackBuffer()

Global FILENAME$									; The string that will hold the name of our file.
Global X = 1										; A random variable to demonstrate the save.
Global Y = 2										; Another random variable to demonstrate the save.

Const S = 31, L = 38

While Not KeyHit(1)

	Cls
	
	Text 0,570,"X: "+x								; Shows us the value for X
	Text 0,580,"Y: "+y								; Shows us the value for Y
	
	If KeyHit(S) = True								; Initiate the save by pressing the 's' key.
		SAVE()
	EndIf
	If KeyHit(L) = True								; Initiate the load by pressing the 'l' key.
		LOAD()
	EndIf
	
	
	Flip
	
Wend
End

Function SAVE()

	FILENAME$ = (Input("SAVE AS: ")+".file")		; .file is the extension, change it to whatever you would like to save the file with.

	FILEOUT = WriteFile(FILENAME$)					; Open file FILENAME$ to write to.
	
	WriteInt (FILEOUT, X)							; Write X to the file FILENAME$ 
	WriteInt (FILEOUT, Y)							; Write Y to the file FILENAME$
	
	CloseFile (FILEOUT)								; Since we're done writing to the file we close it.
	
	Cls												; Clear the screen of the text "Save as: ".  Remove CLS to see what i'm talking about if you don't understand.
	Text 0,0,"FILE: "+FILENAME$+" HAS BEEN SAVED"   ; Shows us that the file saved.
	WaitKey()
	
End Function

Function LOAD()

	FILENAME$ = (Input("LOAD: ")+".file")			; Loads the specified file: FILENAME$ with the extension .file 
	
	FILEIN = ReadFile(FILENAME$)					; Open the file FILENAME$ to read from.
	
	X = ReadInt(FILEIN)								; Load the value for X
	Y = ReadInt(FILEIN)								; Load the value for Y
	
	CloseFile (FILEIN)								; Since we're done reading from the file we close it.
	
	Cls												; Again, we clear the screen of the previous text.
	Text 0,0,"FILE: "+FILENAME$+" HAS BEEN LOADED"	; Shows us that the file has been loaded.
	WaitKey()
	

End Function
