; ID: 2046
; Author: b32
; Date: 2007-06-26 11:45:03
; Title: display image on desktop
; Description: loads a bmp and shows it on the desktop

;--------------------------------------------------------------------------------
;								draw image on desktop
;--------------------------------------------------------------------------------


	Const bmpname$ = "earth.bmp"

	;load image	   	
	image = api_LoadImage(0, bmpname$, 0, 0, 0, $10)
	
	;get desktop dc
	hdc = api_GetDC(0)

	;create new dc and place image on it
	hdc2 = api_CreateCompatibleDC(hdc)
	api_SelectObject(hdc2, image)
			
	;copy data
	api_BitBlt(hdc, 0, 0, 256, 256, hdc2, 0, 0, $CC0020)
	
	;clean up dcs
	api_ReleaseDC(DeskHwnd, hdc)
	api_DeleteDC(hdc2)
	
	WaitKey()
	End


;--------------------------------------------------------------------------------
;save these lines as a .decls file in the \program files\blitz\userlibs directory
;--------------------------------------------------------------------------------
		
;	.lib "user32.dll"
;	
;	api_LoadImage% (hInst%, lpsz$, un1%, n1%, n2%, un2%) : "LoadImageA"
;	api_ReleaseDC%(hWnd%,hDC%) : "ReleaseDC"
;	api_GetDC%(hWnd%) : "GetDC"
;	
;	
;	.lib "gdi32.dll"
;	
;	api_BitBlt%(hDestDC%,X%,Y%,nWidth%,nHeight%,hSrcDC,XSrc,YSrc,dwRop) : "BitBlt"
;	api_CreateCompatibleDC% (hdc%) : "CreateCompatibleDC"
;	api_SelectObject% (hdc%, hObject%) : "SelectObject"
;	api_DeleteDC% (hdc%) : "DeleteDC"
