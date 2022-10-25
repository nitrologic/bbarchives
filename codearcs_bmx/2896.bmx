; ID: 2896
; Author: shinkiro1
; Date: 2011-10-24 15:44:13
; Title: Trim Pixmap (trim away alpha channel)
; Description: Trim away as much alphachannel as possible

Function TrimPixmap:TPixmap( pixmap:TPixmap )
	Local x:Int
	Local y:Int
	Local pixLeft:Int = pixmap.width
	Local pixRight:Int = 0
	Local pixTop:Int = pixmap.height
	Local pixBottom:Int = 0
	'Top -> Down
	For x = 0 Until pixmap.width
		For y = 0 Until pixmap.height
			If pixmap.ReadPixel( x, y ) Shr 24 & $000000FF > 0
				If y < pixTop Then
					pixTop = y
				EndIf
			EndIf
		Next
	Next
	'Down -> Top
	For x = 0 Until pixmap.width
		For y = pixmap.height-1 To 0 Step -1
			If pixmap.ReadPixel( x, y ) Shr 24 & $000000FF > 0
				If y > pixBottom Then
					pixBottom = y
				EndIf
			EndIf
		Next
	Next
	'Left -> Right
	For x = 0 Until pixmap.width
		For y = 0 Until pixmap.height
			If pixmap.ReadPixel( x, y ) Shr 24 & $000000FF > 0
				If x < pixLeft Then
					pixLeft = x
				EndIf
			EndIf
		Next
	Next
	'Right -> Left
	For x = 0 Until pixmap.width
		For y = pixmap.height-1 To 0 Step -1
			If pixmap.ReadPixel( x, y ) Shr 24 & $000000FF > 0
				If x > pixRight Then
					pixRight = x
				EndIf
			EndIf
		Next
	Next
	Return PixmapWindow( pixmap, pixLeft, pixTop, pixRight - pixLeft, pixBottom-pixTop )
End Function
