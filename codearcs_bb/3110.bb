; ID: 3110
; Author: John Blackledge
; Date: 2014-02-08 18:48:38
; Title: Icon to Blitz Image
; Description: Get a windows icon into a Blitz image

Graphics3D 640,480,32

Const SM_CXICON = 11, SM_CYICON = 12
iconWidth = api_GetSystemMetrics(SM_CXICON)
iconHeight = api_GetSystemMetrics(SM_CYICON)

szExeName$ = "E:\Blitz3D\Blitz3D.exe"
hIcon = api_ExtractIcon(0, szExeName$, 0)

; Get a Windows drawing surface
hDC = api_GetDC(0)
; Get a compatible that we can use.
hMemDC= api_CreateCompatibleDC(hDC)

; Create a blank Windows bitmap
hMemBmp= api_CreateCompatibleBitmap( hDC, iconWidth, iconHeight )

; Draw the icon on the bitmap.
api_SelectObject(hMemDC, hMemBmp)
api_DrawIcon(hMemDC, 0, 0, hIcon)

; Create a blank Blitz image, and set it as the drawing buffer.
blitzImage = CreateImage( iconWidth, iconHeight )
SetBuffer ImageBuffer(blitzImage)

; Copy the icon pixels from the Windows bitmap in the Blitz image
For x = 0 To iconWidth-1
	For y = 0 To iconHeight-1
		; This was why my colours were originally wrong.
		; M$ store icon pixels in Blue-Green-Red format, not Red-Blue-Green!!!!
		; ... so swap them.
		bgr = api_GetPixel(hMemDC, x, y)
		b = bgr Shr 16 And %11111111
		g = bgr Shr 8 And %11111111
		r = bgr And %11111111
		rgb = (r Shl 16) Or (g Shl 8) Or b
		WritePixel(x, y, rgb)
	Next
Next

; Reset the drawing buffer back to Backbuffer for normal use.
SetBuffer BackBuffer()
While Not KeyHit(1)
	DrawImage blitzImage,0,0
	Flip
Wend

;Clean all memory objects.
api_ReleaseDC(0, hDC)
api_DeleteDC(hMemDC)
api_DeleteObject(hMemBMP)
End
