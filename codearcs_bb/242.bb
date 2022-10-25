; ID: 242
; Author: Glitch01
; Date: 2002-02-14 15:31:41
; Title: Verify LoadImage() and LoadAnimImage()
; Description: LoadImage() functions to verify image exists or otherwise display detailed error .

;-------------------------------------------------;
; vLoadImage
;
;-------------------------------------------------
Function vLoadImage(file$)

	pointer = LoadImage(file$)
	
	If Not pointer Then
		RuntimeError ("Error loading file [" + file$ + "].")
		End
	Else
		Return pointer
	EndIf
		
End Function


;-------------------------------------------------;
; vLoadAnimImage
;
;-------------------------------------------------
Function vLoadAnimImage(file$,CellWidth,CellHeight,FirstCell,CellCount)

	pointer = LoadAnimImage(file$,CellWidth,CellHeight,FirstCell,CellCount)
	
	If Not pointer Then
		RuntimeError ("Error loading file [" + file$ + "].")
		End
	Else
		Return pointer
	EndIf
		
End Function
