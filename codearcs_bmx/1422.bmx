; ID: 1422
; Author: Dreamora
; Date: 2005-07-19 09:42:25
; Title: DrawImageRect like function
; Description: Draws a part of an image

Rem
	image: handle of the image you want to draw
	x , y: Position at which you want to draw the imagerect
	dw,dh: Drawing height and width of the image part you want do draw.
	l,t: top left position on the image you want to start drawing (in pixels)
	w,h: width and height of the imagerect you want to draw
End Rem

Function DrawImageArea(image:TImage, x#, y#, dw#, dh#, l#, t#, w#, h#, frame:Int=0)
	Local origin_x#,origin_y#
	GetOrigin (origin_x , origin_y)
	Local tw = Pow2Size(image.width)
	Local th = Pow2Size(image.height)
	Local l1#	= l + w
	Local h1#	= t + h
	Local x0#=-image.handle_x
	Local y0# = - image.handle_y
	
	If l1 > image.width
		'x1 = x0 + rw + image.width - l1
		l1 = image.width
	EndIf
	 
	If h1 > image.height
		'y1 = y0 + rh + image.height - h1
		h1 = image.height
	EndIf

	If TGLImageFrame (image.frame(frame))
		Local frame:TGLImageFrame 	= TGLImageFrame (image.frame(frame))
								
		frame.u0 = l / tw
		frame.v0 = t / th
		frame.u1 = l1 / tw
		frame.v1 = h1 / th
		frame.Draw x0,y0,x0+dw,y0+dh,x+origin_x,y+origin_y
		
		frame.u0	= 0
		frame.v0	= 0
		frame.u1	= 1
		frame.v1	= 1
	Else
		Local frame:TD3D7ImageFrame	= TD3D7ImageFrame(image.frame(frame))
		frame.setUV (l/tw, t/th, l1 / tw, h1 / th)
		frame.Draw x0,y0,x0+dw,y0+dh,x+origin_x,y+origin_y
		frame.setUV (0,0,1,1)
	EndIf
	
	Function Pow2Size(n)
		Local ry = 1
		
		While ry < n
			ry :* 2
		Wend
		
		Return ry
	End Function
End Function
