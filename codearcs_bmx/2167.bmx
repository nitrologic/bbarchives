; ID: 2167
; Author: JoshK
; Date: 2007-12-08 20:47:28
; Title: LoadPixmapPGM
; Description: Load a portable greyscale map

Function LoadPixmapPGM:TPixmap(url:Object)
	Local stream:TStream
	Local w,h
	Local pixmap:TPixmap
	Local version
	Local luminance#
	stream=ReadStream(url)
	If Not stream Return
	Select PGMReadLine(stream,2)
		Case "P2"
			version=2
		Case "P5"
			version=5
		Default
			stream.close()
			Return
	EndSelect
	w=Int(PGMReadLine(stream,version))
	h=Int(PGMReadLine(stream,version))
	pixmap=CreatePixmap(w,h,PF_I8)
	maxval=Int(PGMReadLine(stream,version))
	
	For y=0 To h-1
		For x=0 To w-1
			Select version
				Case 2
					luminance=Float(PGMReadLine(stream,version))
				Case 5
					If maxval<256
						luminance=Float(stream.ReadByte())
					Else
						luminance=Float(stream.ReadShort())
					EndIf
			EndSelect
			WritePixel pixmap,x,y,luminance/Float(maxval)*255.0
		Next
	Next
	
	stream.close()
	Return pixmap
	
	Function PGMReadLine$(stream:TStream,version)
		Local s$,c$
		Repeat
			c$=Chr(stream.ReadByte())
			If Trim(c)=""
				If s<>"" Return s
			Else
				s:+c
			EndIf
			If version=2
				If s.length=70 Return s
			EndIf
		Forever
	EndFunction
	
EndFunction
