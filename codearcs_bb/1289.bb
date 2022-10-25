; ID: 1289
; Author: sswift
; Date: 2005-02-13 11:31:26
; Title: SetPointer
; Description: This function allows you to change the mouse pointer in Blitz

; -----------------------------------------------------------------------------------------------------------------------------------
;
; This code allows you to change the mouse pointer in Blitz.
; To use these functions, first place the following in the file "user32.decls" in your userlibs folder:
;
; 	.lib "user32.dll"
; 	LoadCursor%(Instance, CursorName):"LoadCursorA"
;	LoadCursorFromFile%(Filename$):"LoadCursorFromFileA"
; 	SetCursor%(Cursor):"SetCursor"
;   SetClassLong%(hWnd%, nIndex%, dwNewLong%):"SetClassLongA"
;
; Then, in your program, call SetPointer() using one of the constants below, like so:
;
; 	SetPointer(PTR_WAIT)
;
;
; Additional notes:
;
; 	The LoadCursor function loads the cursor resource only if it has not been loaded.  Otherwise, it retrieves the handle
; 	to the existing resource.  In other words, loading a cursor multiple times in the same app without freeing it should 
; 	not cause a memory leak.
;
; 	See this page for information on these functions and on how to load a cursor from a file:
; 	http://msdn.microsoft.com/library/default.asp?url=/library/en-us/winui/winui/windowsuserinterface/resources/cursors/cursorreference/cursorfunctions/loadcursor.asp
;
; -----------------------------------------------------------------------------------------------------------------------------------


; Normal
	Const PTR_ARROW			= $7F00		; Normal cursor.
	Const PTR_SIZENONE		= $7F81   	; No cursor
	
; Busy
	Const PTR_WAIT			= $7F02		; Hourglass.
	Const PTR_SIZEHOURGLASS = $7F8A		; Normal cursor, with small hourglass in the top right corner.
	Const PTR_SIZECD		= $7F97 	; Normal cursor, with a CD loading icon in the top right corner.

; Text
	Const PTR_IBEAM			= $7F01		; Cursor used over text that can be selected or modified.
	Const PTR_SIZEHAND 		= $7F89		; Hand cursor - Index finger pointing up.  Used for web links.


; Resizing / Movement
	Const PTR_SIZENWSE 		= $7F82		; Black arrows, top left to bottom right.
	Const PTR_SIZENESW 		= $7F83		; Black arrows, top right to bottom left.
	Const PTR_SIZEWE 		= $7F84 	; Black arrows, left to right
	Const PTR_SIZENS 		= $7F85 	; Black arrows, top to bottom
	Const PTR_SIZEMOVE 		= $7F86		; Black arrows, in a cross formation. 
	Const PTR_SIZEDENIED	= $7F88 	; Circle with diagonal line

; Help
	Const PTR_SIZEHELP 		= $7F8B		; Normal cursor, with a large black question mark to the right of it.

; Mouse wheel
	Const PTR_SIZEMOVENS	= $7F8C		; Large black arrows with a dot between them, pointing top to bottom.
	Const PTR_SIZEMOVEWE	= $7F8D		; Large black arrows with a dot between them, pointing left to right.
	Const PTR_SIZEMOVENSEW	= $7F8E		; Large black arrows with a dot between them, pointing in all four directions.
	Const PTR_SIZEMOVEN		= $7F8F		; Large black arrow with a dot below it pointing up.
	Const PTR_SIZEMOVES		= $7F90		; Large black arrow with a dot below it pointing down.
	Const PTR_SIZEMOVEW		= $7F91		; Large black arrow with a dot below it pointing left.
	Const PTR_SIZEMOVEE		= $7F92		; Large black arrow with a dot below it pointing right.
	Const PTR_SIZEMOVENW	= $7F93		; Large black arrow with a dot below it pointing towards the top left.
	Const PTR_SIZEMOVENE	= $7F94		; Large black arrow with a dot below it pointing towards the top right.
	Const PTR_SIZEMOVESW	= $7F95		; Large black arrow with a dot below it pointing towards the bottom left.
	Const PTR_SIZEMOVESE	= $7F96		; Large black arrow with a dot below it pointing towards the bottom right.

; Graphics
	Const PTR_CROSS 		= $7F03 	; Plus sign for picking accurately.
	Const PTR_PEN			= $7F77		; A diagonal pen tablet pen with the tip at the top left. (Couldn't find this constant's real name.)

; Misc
	Const PTR_UPARROW 		= $7F04		; Thin black arrow with white outline, pointing up.  Not sure what this is used for.

; Custom pointers

	Const MAX_CUSTOM_POINTERS = 10		; Allocate storage for the custom pointers.
	Dim PTR_CUSTOM(MAX_CUSTOM_POINTERS)		
		
	Const PTR_ZOOMIN		= $0000		; Store the array index of each custom pointer here for easy access.
	Const PTR_ZOOMOUT		= $0001

; -----------------------------------------------------------------------------------------------------------------------------------
; This function allows you to load custom pointers from files.
; Call it once at the start of your program.
;
; Note:
; Cursors loaded with this function DO NOT NEED TO BE, and SHOULD NOT BE, "freed".
;
; According to MSDN, both LoadCursor and LoadCursorFromFile load "shared" cursors. 
; MSDN indicates that shared cursors are deleted automatically when the application they were loaded from is shut down. 
; It also specfically says that you should not use the DestroyCursor function to delete shared cursors loaded by these functions.
;
; See this page for more info:
; http://msdn.microsoft.com/library/default.asp?url=/library/en-us/winui/winui/windowsuserinterface/resources/cursors/cursorreference/cursorfunctions/destroycursor.asp
; -----------------------------------------------------------------------------------------------------------------------------------
Function LoadPointers()

	; One entry for each custom cursor.
	PTR_CUSTOM(PTR_ZOOMIN)  = LoadCursorFromFile("zoomin.cur")
	PTR_CUSTOM(PTR_ZOOMOUT) = LoadCursorFromFile("zoomout.cur")

End Function


; -----------------------------------------------------------------------------------------------------------------------------------
; This functoon changes the mouse pointer. 
; It can accept constants for system pointers, or custom pointers you have loaded in the loadpointers() function.
; -----------------------------------------------------------------------------------------------------------------------------------
Function SetPointer(Pointer)

	Local Cursor
	
	; If pointer is greater than the max number of custom pointers, then it must be referencing a system pointer.
	If (Pointer > MAX_CUSTOM_POINTERS)
		Cursor = LoadCursor(0, Pointer)
	Else
		Cursor = PTR_CUSTOM(Pointer)
	EndIf
	
	SetCursor(Cursor)	
	
End Function


; -----------------------------------------------------------------------------------------------------------------------------------
; You should call this function at the start of your program if you want to change the pointer in your program.
; If you don't, then windows will occasionally reset the pointer to the arrow cursor after you have changed it.
;
; Window is the pointer to your main window. 
;
; You may need to call this function once for all windows you create in your application.
; -----------------------------------------------------------------------------------------------------------------------------------
Function DisablePointerReset(Window)

	Local hWnd	
	Local GCL_HCURSOR = -12

	hWnd = QueryObject(Window, 1)
	SetClassLong(hWnd, GCL_HCURSOR, 0)
	
End Function
