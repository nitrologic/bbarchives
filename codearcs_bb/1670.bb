; ID: 1670
; Author: Grey Alien
; Date: 2006-04-14 08:49:17
; Title: Blitz Plus Dirty Rects System
; Description: Blitz Plus Dirty Rects System

Type RectType2	
	Field RLeft%, RTop%, Width%, Height%, id%
End Type

Const MAX_DIRTY_RECTS = 1000
Type DirtyRects
	Field Items.RectType2[MAX_DIRTY_RECTS]
	Field Count
End Type

Function DirtyRectsCreate.DirtyRects()
	d.DirtyRects = New DirtyRects
	For i = 0 To MAX_DIRTY_RECTS
		d\Items[i] = New RectType2				
	Next
	d\Count = 0
	Return d
End Function

Function DirtyRectsDelete(d.DirtyRects)
	If d = Null Then Return 0
	For i = 0 To MAX_DIRTY_RECTS
		Delete d\items[i]
	Next	
	Delete d
	Return 1
End Function

Function DirtyRectsAdd(d.DirtyRects, x, y, w, h)
	Local i = d\Count
	d\items[i]\RLeft = x
	d\items[i]\RTop = y
	d\items[i]\Width = w
	d\items[i]\Height = h
	d\items[i]\id = 0
	d\Count = d\Count + 1			
End Function

Function DirtyRectsAddImage(d.DirtyRects, x, y, image)
	Local w = ImageWidth(image)
	Local h = ImageHeight(image)
	Local i = d\Count
	d\items[i]\RLeft = x
	d\items[i]\RTop = y
	d\items[i]\Width = w
	d\items[i]\Height = h
	d\items[i]\id = 0
	d\Count = d\Count + 1			
End Function

Function DirtyRectsAddSpecial(d.DirtyRects, x, y, w, h, id%)
	Local i = d\Count
	d\items[i]\RLeft = x
	d\items[i]\RTop = y
	d\items[i]\Width = w
	d\items[i]\Height = h
	d\items[i]\id = id
	d\Count = d\Count + 1			
End Function

Function DirtyRectsDrawAll(d.DirtyRects, background%)
	;pass in background image, it draws on current buffer
	ib = ImageBuffer(background)
	For i = 0 To d\count-1		
		Local r.RectType2 = d\items[i]
		CopyRect(r\RLeft, r\RTop, r\Width, r\Height, r\RLeft, r\RTop, ib) ;dest buffer not specified			
	Next
End Function

Function DirtyRectsDrawSpecial(d.DirtyRects, background%, id%)
	;pass in background image, it draws on current buffer
	ib = ImageBuffer(background)
	For i = 0 To d\count-1		
		Local r.RectType2 = d\items[i]
		If r\id = id Then			
			CopyRect(r\RLeft, r\RTop, r\Width, r\Height, r\RLeft, r\RTop, ib) ;dest buffer not specified
			;move everything in the list above this down			
			d\Count = d\count-1
			For j = i To d\Count-1
				dest.RectType2 = d\items[j]
				s.RectType2 = d\items[j+1]
				dest\RLeft = s\RLeft
				dest\RTop = s\RTop
				dest\Width = s\Width
				dest\Height = s\Height
				dest\id = s\id
			Next			
		EndIf
	Next
End Function
