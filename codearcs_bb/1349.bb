; ID: 1349
; Author: Prof
; Date: 2005-04-11 17:03:24
; Title: More API Requesters
; Description: More API Requesters

; More requesters By Prof

; user32.decls
; MessageBox%(hWnd,lpText$,lpCaption$,uType):"MessageBoxA"

; Style = 0 ; OK
; Style = 1 ; OK / CANCEL
; Style = 2 ; ABORT / RETRY / IGNORE
; Style = 3 ; YES / NO / CANCEL
; Style = 4 ; YES / NO
; Style = 5 ; RETRY / CANCEL
; Style = 6 ; CANCEL / TRY AGAIN / CONTINUE

; Notice the following return values no matter which Style is used.....
;
; 1  means OK        clicked
; 2  means CANCEL    clicked
; 3  means ABORT     clicked
; 4  means RETRY     clicked
; 5  means IGNORE    clicked
; 6  means YES       clicked
; 7  means NO        clicked
; 10 means TRY AGAIN clicked
; 11 means CONTINUE  clicked

Graphics 640,480,32,2
SetBuffer BackBuffer()

STYLE = 6 
Result=MessageBox(0,"Popup message","Popup caption",Style)

Text 10,10,"Result = "+Str(Result)
Flip

Repeat
Until KeyDown(1)
End
