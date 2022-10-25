; ID: 1119
; Author: PantsOn
; Date: 2004-07-29 14:14:18
; Title: OIL painting
; Description: turn any image into an oil painting

Dim OILcol(1,0)
; OILimage - Rich Hanson (PantsOn Software)
;            v3
Function OILimage(image_img,brush_detail=5)
	; set variables
	iw = ImageWidth(image_img)
	ih = ImageHeight(image_img)
	output_img = CreateImage(iw,ih)
	ib = ImageBuffer(image_img)
	ob = ImageBuffer(output_img)
	scan = brush_detail / 2
	brush_detail2 = brush_detail*brush_detail
	
	; create temp bank for pic data
	; bank is larger than needed so no need to error check later
	LockBuffer ib
	tmp_bnk = CreateBank(4*(iw+brush_detail)*(ih+brush_detail))
	For x = 0 To iw - 1
		For y = 0 To ih - 1
			PokeInt tmp_bnk,(x+scan)*4 + (y+scan)*(iw+brush_detail)*4, ReadPixelFast(x,y,ib) And %00000000111111001111110011111100
		Next
	Next
	UnlockBuffer ib	
	
	; convert pic data
	LockBuffer ob
	For x = 0 To iw - 1
		For y = 0 To ih - 1
		
			Dim OILcol(1,brush_detail2)
			count = 0
			
			; scan around pixel. No more error checking
			For x1 = -scan To scan
				For y1 = -scan To scan						
					; read values from bank rather than screen
					c = PeekInt(tmp_bnk,(x+x1+scan)*4 + (y+y1+scan)*(iw+brush_detail)*4) 
					; stpre populatiry of colour (needs opt)
					For i = 0 To count
						If OILcol(0,i) = c
							OILcol(1,i) = OILcol(1,i) + 1
							i = brush_detail2 + 2
						EndIf
					Next
					If i = count + 1
						OILcol(0,count) = c
						OILcol(1,count) = 1
						count = count + 1
					EndIf			
				Next
			Next
			
			; find highest most common colour (needs opt)
			high = 0
			For a = 0 To brush_detail2
				If OILcol(1,a) > high
					OILcolour = OILcol(0,a)
					high = OILcol(1,a)
				EndIf
			Next
			
			WritePixelFast x,y,OILcolour,ob
			
			Dim OILcol(0,0)
		Next
	Next
	UnlockBuffer ob
	
	; free temp stuff
	FreeBank tmp_bnk
	Dim OILcol(0,0)
	
	; return new piccy
	Return output_img
End Function
