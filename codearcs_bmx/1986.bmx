; ID: 1986
; Author: Justus
; Date: 2007-04-08 05:28:44
; Title: MirrorImage
; Description: Mirrors an image and adds a transparency gradient making it look like a real mirror.

Function mirrorImage:TImage(img:TImage)
   Local a:Int
   Local r:Int
   Local g:Int
   Local b:Int
   Local i:Int
   Local j:Int
   Local argb:Int
   Local pixmap:TPixmap = LockImage(img)
   pixmap = ConvertPixmap(pixmap,PF_RGBA8888)
   pixmap = YFlipPixmap(pixmap)
   For i = 0 To PixmapWidth(pixmap)-1
      For j = 0 To PixmapHeight(pixmap)-1
         argb = ReadPixel(pixmap,i,j)
         a:Int = Int(76.0*(1.0-((Float(j)/Float(ImageHeight(img))))))
         If (argb Shr 24) = 0 Then a = 0
         r:Int = (argb Shr 16) & $ff
           g:Int = (argb Shr 8)  & $ff
           b:Int = argb & $ff
         argb = a*$1000000 + r*$10000 + g*$100 + b
         WritePixel pixmap,i,j,argb
      Next
   Next
   Return LoadImage(pixmap)
EndFunction
