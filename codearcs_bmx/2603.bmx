; ID: 2603
; Author: Ked
; Date: 2009-11-01 13:02:14
; Title: GetPixmapAlphaChannel, AttachPixmapAlphaChannel
; Description: A couple useful pixmap functions for getting and attaching the pixmap's alpha channel.

Function GetPixmapAlphaChannel:TPixmap(pixmap:TPixmap)
	If Not pixmap.format=PF_RGBA8888 Or Not pixmap.format=PF_BGRA8888 Return Null
	
	Local res:TPixmap=CreatePixmap(pixmap.width,pixmap.height,PF_RGB888)
	ClearPixels(res)
	
	For Local x:Int=0 To pixmap.width-1
	For Local y:Int=0 To pixmap.height-1
		Local p1:Int=ReadPixel(pixmap,x,y)
		Local clr:Int=(p1 Shr 24) & 255
		Local p2:Int=(255 Shl 24)|(clr Shl 16)|(clr Shl 8)|clr
		WritePixel(res,x,y,p2)
	Next
	Next
	
	Return res
EndFunction

Function AttachPixmapAlphaChannel:TPixmap(pixmap:TPixmap,alphapixmap:TPixmap)
	If Not (pixmap.width=alphapixmap.width) And Not (pixmap.height=alphapixmap.height) Return Null
	
	Local res:TPixmap=CreatePixmap(pixmap.width,pixmap.height,PF_RGBA8888)
	ClearPixels(res)
	
	pixmap=ConvertPixmap(pixmap,PF_RGB888)
	alphapixmap=ConvertPixmap(alphapixmap,PF_RGB888)
	
	For Local x:Int=0 To pixmap.width-1
	For Local y:Int=0 To pixmap.height-1
		Local p1:Int=ReadPixel(pixmap,x,y)
		Local r:Int,g:Int,b:Int
		r=(p1 & 255)
		g=(p1 Shr 8) & 255
		b=(p1 Shr 16) & 255
		
		Local p2:Int=ReadPixel(alphapixmap,x,y)
		Local a:Int
		a=(p2 & 255)
		
		Local pix:Int=(a Shl 24)|(b Shl 16)|(g Shl 8)|(r)
		WritePixel(res,x,y,pix)
	Next
	Next
	
	Return res
EndFunction
