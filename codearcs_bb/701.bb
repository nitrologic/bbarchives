; ID: 701
; Author: Red
; Date: 2003-05-21 16:06:08
; Title: TextAreaJumpToLine
; Description: force textarea to scroll vertically

[code]
Function TextAreaJumpToLine(txt,numline)	
	Local EM_LINESCROLL=$B6
	Local curY=TextAreaCursor( txt,2 )
	Local scrollY=numline-curY
	sendmessage(QueryObject(txt,1), EM_LINESCROLL,  0, scrollY)
End Function[/code]
