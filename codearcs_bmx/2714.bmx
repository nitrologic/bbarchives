; ID: 2714
; Author: Nilium
; Date: 2010-05-15 01:40:15
; Title: NinePatch Image
; Description: NinePatch image similar to the ninepatch drawables in Android OS.

SuperStrict

Type NNinePatch
	Field _img:TImage
	Field _left_border#, _right_border#, _top_border#, _bottom_border#
	Field _width%, _height%
	Field _border_scale#=1.0
	
	Method DrawRect(x#, y#, width#, height#, frame%=0)
		Const NINEPATCH_MINIMUM#=0.5#
		Local lb%, rb%, tb%, bb%
		lb = (NINEPATCH_MINIMUM <= _left_border)
		rb = (NINEPATCH_MINIMUM <= _right_border)
		tb = (NINEPATCH_MINIMUM <= _top_border)
		bb = (NINEPATCH_MINIMUM <= _bottom_border)
		
		Local lw# = lb*_left_border
		Local rw# = rb*_right_border
		Local th# = tb*_top_border
		Local bh# = bb*_bottom_border
		
		Local dw# = width-(lw+rw)*_border_scale
		Local dh# = height-(th+bh)*_border_scale
		Local sw# = _width-lw-rw
		Local sh# = _height-th-bh
		
		Local handlex#, handley#
		GetHandle(handlex,handley)
		
		If tb Then
			If lb Then
				DrawSubImageRect(_img, x, y, lw*_border_scale, th*_border_scale, 0, 0, lw, th, 0, 0, frame)
			EndIf
			DrawSubImageRect(_img, x+lw*_border_scale, y, dw, th*_border_scale, lw, 0, sw, th, 0, 0, frame )
			If rb Then
				DrawSubImageRect(_img, x+dw+lw*_border_scale, y, rw*_border_scale, th*_border_scale, sw+lw, 0, rw, th, 0, 0, frame)
			EndIf
		EndIf
		
		If lb Then
			DrawSubImageRect(_img, x, y+th*_border_scale, lw*_border_scale, dh, 0, th, lw, sh, 0, 0, frame)
		EndIf
		DrawSubImageRect(_img, x+lw*_border_scale, y+th*_border_scale, dw, dh, lw, th, sw, sh, 0, 0, frame )
		If rb Then
			DrawSubImageRect(_img, x+dw+lw*_border_scale, y+th*_border_scale, rw*_border_scale, dh, sw+lw, th, rw, sh, 0, 0, frame)
		EndIf
		
		If tb Then
			If lb Then
				DrawSubImageRect(_img, x, y+dh+th*_border_scale, lw*_border_scale, bh*_border_scale, 0, sh+th, lw, bh, 0, 0, frame)
			EndIf
			DrawSubImageRect(_img, x+lw*_border_scale, y+dh+th*_border_scale, dw, bh*_border_scale, lw, sh+th, sw, bh, 0, 0, frame )
			If rb Then
				DrawSubImageRect(_img, x+dw+lw*_border_scale, y+dh+th*_border_scale, rw*_border_scale, bh*_border_scale, sw+lw, sh+th, rw, bh, 0, 0, frame)
			EndIf
		EndIf
	End Method
	
	Method InitWithImage:NNinePatch(img:TImage, left_border#=8, right_border#=8, top_border#=8, bottom_border#=8, border_scale#=1.0)
		_img = img
		_width = ImageWidth(img)
		_height = ImageHeight(img)
		_left_border = left_border
		_right_border = right_border
		_top_border = top_border
		_bottom_border = bottom_border
		_border_scale = border_scale
		Return Self
	End Method
End Type
