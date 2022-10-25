; ID: 1355
; Author: Mikle
; Date: 2005-04-17 09:08:04
; Title: OverlayImage
; Description: Draws "repeating"/"cycling" image

; This function was made by Mikle's friend Dima (DiGlib)
; with the help of MathLab. Thanks to MathLab developers!

; Draws "repeating"/"cycling" image

Function OverlayImage(img, x, y, rect_x, rect_y, rect_w, rect_h, frame = 0)
	Local x_draw, y_draw
	Local cell_ix, cell_ix_start, cell_ix_end
	Local cell_iy, cell_iy_start, cell_iy_end
	Local cell_x, cell_y
	Local wDraw_x, wDraw_y, wGet_x, wGet_y, wGet_w, wGet_h
	Local img_w, img_h
	img_w = ImageWidth(img)
	img_h = ImageHeight(img)
	x_draw = x - ImageXHandle(img) - rect_x
	y_draw = y - ImageYHandle(img) - rect_y
	cell_ix_start = Floor(Float(x_draw)/Float(rect_w))
	cell_ix_end = Floor(Float(x_draw + img_w)/Float(rect_w)) - cell_ix_start
	cell_iy_start = Floor(Float(y_draw)/Float(rect_h))
	cell_iy_end = Floor(Float(y_draw + img_h)/Float(rect_h)) - cell_iy_start
	x_draw = x_draw - cell_ix_start*rect_w
	y_draw = y_draw - cell_iy_start*rect_h
	For cell_iy = 0 To cell_iy_end
		cell_y = cell_iy*rect_h
		For cell_ix = 0 To cell_ix_end
			cell_x = cell_ix*rect_w
			wGet_x = cell_x - x_draw
			wGet_y = cell_y - y_draw
			wGet_w = rect_w
			wGet_h = rect_h
			If (wGet_x+rect_w > img_w) Then wGet_w = img_w-wGet_x
			If (wGet_y+rect_h > img_h) Then wGet_h = img_h-wGet_y
			If (x_draw > cell_x) Then wGet_w = rect_w - x_draw
			If (y_draw > cell_y) Then wGet_h = rect_h - y_draw
			If (wGet_x < 0) Then wGet_x = 0
			If (wGet_y < 0) Then wGet_y = 0
			wDraw_x = wGet_x - cell_x + x_draw
			wDraw_y = wGet_y - cell_y + y_draw
			If (wDraw_x < 0) Then wDraw_x = 0
			If (wDraw_y < 0) Then wDraw_y = 0
			wDraw_x = rect_x + wDraw_x + ImageXHandle(img)
			wDraw_y = rect_y + wDraw_y + ImageYHandle(img)
			DrawImageRect img, wDraw_x, wDraw_y, wGet_x, wGet_y, wGet_w, wGet_h, frame
		Next
	Next
End Function

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

; TEST

Global GW = 640
Global GH = 480

Graphics GW, GH, 16, 2
SetBuffer BackBuffer()

;HidePointer

Font = LoadFont("Courier", 16, True, False, False)
SetFont Font

w = 128
h = 128
img = CreateImage(w, h)
SetBuffer ImageBuffer(img)
Color 255, 0, 0
Oval 0, 0, w, h, False
Color 0, 255, 0
Oval 0, h*0.25, w, h*0.5, False
Color 0, 0, 255
Oval w*0.25, 0, w*0.5, h, False
SetBuffer BackBuffer()
MidHandle img

rect_w# = 200
rect_h# = 200
rect_y# = GH*0.5-rect_h*0.5
rect_x# = GW*0.5+rect_w*0.5

ClsColor 255, 255, 255

While Not KeyHit(1)
	Cls

	rect_h = rect_h+2*(KeyDown(200) - KeyDown(208))
	rect_w = rect_w+2*(KeyDown(203) - KeyDown(205))
	rect_y = GH*0.5-rect_h*0.5
	rect_x = GW*0.5-rect_w*0.5

	OverlayImage(img, MouseX(), MouseY(), rect_x, rect_y, rect_w, rect_h)

	Color 0, 0, 0
	Rect rect_x, rect_y, rect_w, rect_h, False

	; Output results
	Color 0, 0, 0
	txt_x = 0
	txt_y = 0
	Text txt_x, txt_y, "Press arrow keys to scale viewport."
	txt_y = txt_y + FontHeight()
	Text txt_x, txt_y, "Width = "+rect_w
	txt_y = txt_y + FontHeight()
	Text txt_x, txt_y, "Height = "+rect_h
	txt_y = txt_y + FontHeight()

	Flip
Wend

FreeFont Font

EndGraphics

End
