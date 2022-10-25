; ID: 2810
; Author: degac
; Date: 2011-01-16 02:07:15
; Title: Convert pixmap to grey
; Description: Convert pixmap to grey

Rem
From a SOURCE pixmap returns a grey converted one

Usage:

Local grey_pix:tpixmap=MakeGrey(LoadPixmap("colored_pixmap.png"))

End Rem

Function MakeGrey:TPixmap(source:TPixmap=Null)
		If source=Null Return Null
		Local r:Int,g:Int,b:Int,argb:Int,a:Int,c:Int
		Local x:Int,y:Int
		Local tmp_grey:TImage
		tmp_grey=CreateImage(source.WIDTH,source.HEIGHT)
		Local pm2:TPixmap=LockImage(tmp_grey)
		For y=0 Until source.HEIGHT
		For x=0 Until source.WIDTH
			argb=ReadPixel(source,x,y)
			a=argb Shr 24 & $ff
			r = (argb Shr 16) & $ff
			g = (argb Shr 8) & $ff
			b = argb & $ff
			'c=(r+g+b)/3
                        c=(r*0.30)+(g*0.59)+(b*0.11)
			Local pixcol:Int=(a Shl 24)|(c Shl 16)|(c Shl 8)|(c)
			WritePixel(pm2,x,y,pixcol)
		Next
		Next
		Return pm2	
End Function
