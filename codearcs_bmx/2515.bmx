; ID: 2515
; Author: Warner
; Date: 2009-06-30 09:46:08
; Title: wxmax generate wxicon
; Description: generate wxicon

Function MatteIcon:wxIcon(r:Int, g:Int, b:Int)

	Local imagePtr:Byte Ptr = bmx_wximage_createempty(16, 16)		
	bmx_wximage_setrgbrange(imagePtr, 0, 0, 16, 16, r, g, b)
	Local bitmapPtr:Byte Ptr = bmx_wxbitmap_createfromimage(imagePtr, -1)
		
	Local icon:wxIcon = New wxIcon.Create()
	bmx_wxicon_copyfrombitmap(icon.wxObjectPtr, bitmapPtr)
						
	bmx_wximage_delete(imagePtr)
	bmx_wxbitmap_delete(bitmapPtr)
	
	Return icon
	
End Function

'create icon from image
Function PixmapToIcon:wxIcon(pm:TPixmap)
				
	Local imagePtr:Byte Ptr = bmx_wximage_createempty(16, 16)		
			
	Local x:Int, y:Int
	For x = 0 To 15
	For y = 0 To 15
		Local col:Int = pm.ReadPixel(x * pm.width / 16, y * pm.height / 16)
		Local b:Int = col & 255
		Local g:Int = (col shr 8) & 255
		Local r:Int = (col shr 16) & 255
		bmx_wximage_setrgb(imagePtr, x, y, r, g, b)
	Next
	Next	
		
	Local bitmapPtr:Byte Ptr = bmx_wxbitmap_createfromimage(imagePtr, -1)
		
	Local icon:wxIcon = New wxIcon.Create()
	bmx_wxicon_copyfrombitmap(icon.wxObjectPtr, bitmapPtr)
						
	bmx_wximage_delete(imagePtr)
	bmx_wxbitmap_delete(bitmapPtr)
	
	Return icon
		
End Function
