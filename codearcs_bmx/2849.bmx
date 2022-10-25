; ID: 2849
; Author: Afke
; Date: 2011-05-08 14:59:16
; Title: Animation Strip for bah.freeImage
; Description: Animation Strip for bah.freeImage

Strict
Import bah.FreeImage
Import brl.glmax2d
Import maxgui.Drivers

Type TFreeAnimImage Extends TImage
	Field images:TFreeImage[]
'	
	Method SetPixmap(index, pixmap:TPixmap)
		If (flags & MASKEDIMAGE) And AlphaBitsPerPixel[pixmap.format]=0
			pixmap=MaskPixmap( pixmap,mask_r,mask_g,mask_b )
		EndIf
		images[index] = TFreeImage.CreateFromPixmap(pixmap)
		seqs[index]=0
		frames[index]=Null
	End Method
	Function CreateNew:TFreeAnimImage(width, height, frames = 1, flags = -1, mr = 0, mg = 0, mb = 0)
		Local t:TFreeAnimImage = New TFreeAnimImage
		t.width=width
		t.height=height
		t.flags=flags
		t.mask_r=mr
		t.mask_g=mg
		t.mask_b=mb
		t.images = New TFreeImage[frames]
		t.frames=New TImageFrame[frames]
		t.seqs=New Int[frames]
		Return t

	End Function
	Function LoadAnim:TFreeAnimImage(url:Object, cell_width, cell_height, first = 0, count = 1, flags = -1, mr = 0, mg = 0, mb = 0)
		Local pixmap:TPixmap=TPixmap(url)
		If Not pixmap pixmap=LoadPixmap(url)
		If Not pixmap Return

		Local x_cells=pixmap.width/cell_width
		Local y_cells=pixmap.height/cell_height
		If first+count>x_cells*y_cells Return
		
		Local t:TFreeAnimImage = CreateNew(cell_width, cell_height, count, flags, mr, mg, mb)

		For Local cell=first To first+count-1
			Local x=cell Mod x_cells * cell_width
			Local y=cell / x_cells * cell_height
			Local window:TPixmap=pixmap.Window( x,y,cell_width,cell_height )
			t.SetPixmap cell-first,window.Copy()
		Next
		Return t
	End Function
	
End Type
