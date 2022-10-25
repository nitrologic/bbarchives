; ID: 1484
; Author: CS_TBL
; Date: 2005-10-11 09:27:46
; Title: NotifyImage
; Description: like Notify, but then for images (B+ only)

Function NotifyImage(img)
	;
	; by CS_TBL
	;
	
	FlushEvents()
	If Not img Return

	w=256
	h=160

	window=CreateWindow("NotifyImage",ClientWidth(Desktop())/2-(w/2),ClientHeight(Desktop())/2-(h/2),w,h,0,1)

	canvas=CreateCanvas(0,0,ClientWidth(window),ClientHeight(window),window)

	SetBuffer CanvasBuffer(canvas)
		DrawBlock img,0,0
	FlipCanvas canvas

	Repeat
		WaitEvent()
		If EventID()=$803
			If EventSource()=window notifyimagequit=True
		EndIf
	Until notifyimagequit
	
	FreeGadget window
	SetBuffer DesktopBuffer()
End Function
