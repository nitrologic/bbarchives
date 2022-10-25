; ID: 188
; Author: Chroma
; Date: 2002-01-11 22:33:15
; Title: VB Split Command
; Description: Here's a Visual Basic Command for Blitz.

;Visual Basic Split Command
;by Chroma


;Set up variables
;These need to go in your program
Dim word$(1)
Global max_word=0


;Here's the test string
test$="Blitz3D is the fastest 2d/3d gaming language!"

;Split string at spaces
Split(test$)

;Print split string
For i = 1 To max_word
	Print word$(i)
Next

;Pause
WaitKey

;End the Program
End


;-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
;Split Function a la Visual Basic
;-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
Function Split(mystring$)

count=Len(mystring$)+1
Dim word$(count)
max_word=0 : start=1

For t=1 To count
	If Mid(mystring$,t,1)=" " Or Mid(mystring$,t,1)=""
		max_word=max_word+1
		word$(max_word)=Mid(mystring$,start,t-start)
		start=t+1
	EndIf
Next
End Function
;-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
