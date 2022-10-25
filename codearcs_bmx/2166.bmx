; ID: 2166
; Author: grable
; Date: 2007-12-07 14:53:08
; Title: [Win32] Icon to Pixmap/Image
; Description: Load icons from files into TPixmap or TImage

?Win32
Type TIconInfo
	Field fIcon:Int
	Field xHotspot:Int
	Field yHotspot:Int
	Field hbmMask:Int
	Field hbmColor:Int
EndType

Type TBitmap
	Field bmType:Int
	Field bmWidth:Int
	Field bmHeight:Int
	Field bmWidthBytes:Int
	Field bmPlanes:Short
	Field bmBitsPixel:Short
	Field bmBits:Byte Ptr
EndType

Extern "Win32"
	Const DI_MASK:Int = 1
	Const DI_IMAGE:Int = 2
	Const DI_NORMAL:Int = 3
	Const DI_COMPAT:Int = 4
	Const DI_DEFAULTSIZE:Int = 8
	
	Function ExtractIconA:Int( hwnd:Int, filename$z, index:Int)
	Function GetIconInfo( icon:Int, iinfo:Byte Ptr)
	Function GetObjectA:Int( handle:Int, size:Int, binfo:Byte Ptr)
	Function DrawIconEx:Int( dc:Int, x:Int,y:Int, icon:Int, w:Int,h:Int, anicurstep:Int, flickerfreebrush:Int, flags:Int)
	Function GetDIBits:Int( dc:Int, bm:Int, x:Int,y:Int, src:Byte Ptr, bmi:Byte Ptr, flags:Int)	
EndExtern	

Function IconToPixmap:TPixmap( icon:Int, getmask:Int = False)
	Local width:Int,height:Int, bits:Int
	Local info:TIconInfo = New TIconInfo	
	Local bitmap:TBitmap = New TBitmap
	If icon <> 0 And GetIconInfo( icon, info) Then
		If GetObjectA( info.hbmColor, SizeOf(bitmap), bitmap) <> 0 Then
			width = bitmap.bmWidth
			height = bitmap.bmHeight
			'bits = bitmap.bmBitsPixel
			' seems most icons work with 24 bits, but some need 32 bits. how to detect this?
			bits = 24	
      	EndIf    		
		DeleteObject( info.hbmMask)
		DeleteObject( info.hbmColor)
	EndIf
	If width <> 0 And height <> 0 Then
			
		Local dc:Int = GetDC(0)
		Local mdc:Int = CreateCompatibleDC(dc)
		Local bm:Int = CreateCompatibleBitmap( dc, width,height)
		If Not bm Then Return Null
		Local oldbm:Int = SelectObject( mdc, bm)

		If getmask Then
			DrawIconEx( mdc, 0, 0, icon, 0,0,0, Null, DI_MASK)
		Else
			DrawIconEx( mdc, 0, 0, icon, 0,0,0, Null, DI_IMAGE)
		EndIf
		
		Local bi:BITMAPINFOHEADER = New BITMAPINFOHEADER
		bi.biSize = SizeOf(bi)
		bi.biWidth = width
		bi.biHeight = height
		bi.biPlanes = 1
		bi.biBitCount = bits
		bi.biCompression = BI_RGB
		
		Local pixmap:TPixmap
		Select bits
			Case 24	pixmap = CreatePixmap( width,height, PF_BGR888)
			Case 32	pixmap = CreatePixmap( width,height, PF_BGRA8888)
			Default
				Throw "IconToPixmap() supports only 24 and 32 bits (got " + bits + ")"
		EndSelect		
		
		Local src:Byte Ptr = pixmap.Pixels
		For Local y:Int = 0 Until height
			GetDIBits( mdc, bm, height - y - 1,1, src, bi, DIB_RGB_COLORS)
			src :+ pixmap.Pitch
		Next
		
		SelectObject( mdc, oldbm)
		DeleteObject( bm)
		DeleteDC( mdc)
		Return pixmap
	EndIf
EndFunction
?

Function LoadFileIconImage:TImage( fn:String, id:Int = 0)
?Win32
	Local icon:Int = ExtractIconA( 0, fn, id)
	If icon <> 0 Then
		Local mask:TPixmap = IconToPixmap( icon, True).Convert( PF_BGRA8888)
		Local pix:TPixmap = IconToPixmap( icon).Convert( PF_BGRA8888)
		Local mp:Int Ptr = Int Ptr mask.Pixels
		Local pp:Int Ptr = Int Ptr pix.Pixels
		For Local i:Int = 0 Until pix.Width * pix.Height
			If mp[i] = $FFFFFFFF Then
				pp[i] :& $00FFFFFF
			EndIf
		Next
		Return LoadImage( pix, 0)
	EndIf
?
	Return Null
EndFunction
