; ID: 2722
; Author: Ked
; Date: 2010-05-28 13:24:01
; Title: Bitmap Font
; Description: Another bitmap font method.

SuperStrict

Type TBitmapFontViewer
	Field img:TImage
	Field x:Float,y:Float
	Field w:Int,h:Int
	Field holding:Int=False
	Field holdx:Float,holdy:Float
	
	Field path:String="n/a"
	Field chardata:TBitmapCharData[96]
	
	Method Open:TBitmapFontViewer(url:Object)
		img=LoadImage(String(url)+"_img.png")
		If img=Null Return Null
		
		x=0
		y=0
		w=ImageWidth(img)
		h=ImageHeight(img)
		
		path=String(url)
		
		Return Self
	EndMethod
	
	Method CreateNew:TBitmapFontViewer(furl:Object,size:Int)
		Local imgfont:TImageFont=LoadImageFont(furl,size)
		If imgfont=Null Return Null
		SetImageFont(imgfont)
		
		x=0
		y=0
		
		path="n/a"
		
		Local spacing:Int=(size*2)
		Local dx:Int=0,dy:Int=0
		
		Cls()
		SetBlend ALPHABLEND
		
		For Local i:Int=32 To 127
			DrawText(Chr(i),dx,dy)
			
			Local c:TBitmapCharData=New TBitmapCharData
			c.ind=i
			c.x=dx
			c.y=dy
			c.w=TextWidth(Chr(i))
			c.h=TextHeight(Chr(i))
			
			chardata[(i-32)]=c
			
			dx:+spacing
			If dx=(16*spacing)
				dx=0
				dy:+spacing
			EndIf
		Next
		
		Flip()
		
		dy:+spacing
		
		w=(16*spacing)
		h=dy
		
		img=CreateImage(w,h,1,DYNAMICIMAGE|FILTEREDIMAGE)
		GrabImage(img,0,0)
		
		Return Self
	EndMethod
	
	Method Save(name:String)
		If path<>"n/a" Return
		If img=Null Return
		
		Local pix:TPixmap=img.pixmaps[0]
		SavePixmapPNG(pix,name+"_img.png")
		pix=Null
		
		Local dat:TStream=WriteStream(name+"_data.dat")
		For Local i:Int=0 To 94
			Local c:TBitmapCharData=chardata[i]
			WriteInt(dat,c.ind)
			WriteInt(dat,c.x)
			WriteInt(dat,c.y)
			WriteInt(dat,c.w)
			WriteInt(dat,c.h)
		Next
		CloseStream(dat)
		
		path=name
	EndMethod
	
	Method Update()
		Local md:Int=MouseDown(MOUSE_LEFT)
		Local mx:Int=MouseX()
		Local my:Int=MouseY()
		
		If (mx>x And my>y And mx<(x+w) And my<(y+h))=False
			If md=True md=False
		EndIf
		
		If md=True
			If holding=False
				holding=True
				holdx=MouseX()
				holdy=MouseY()
			EndIf
			
			If holding=True
				Local xdist:Float=(MouseX()-holdx)
				Local ydist:Float=(MouseY()-holdy)
				
				x:+xdist
				y:+ydist
				
				holdx:+xdist
				holdy:+ydist
			EndIf
		Else
			If holding=True
				holding=False
				holdx=0
				holdy=0
			EndIf
		EndIf
	EndMethod
	
	Method Draw()
		SetBlend ALPHABLEND
		SetScale 1,1
		SetAlpha 1.0
		SetRotation 0
		SetColor 255,255,255
		
		DrawImage img,x,y
	EndMethod
EndType

Type TBitmapCharData
	Field ind:Int
	Field x:Int
	Field y:Int
	Field w:Int
	Field h:Int
EndType

Type TBitmapFont
	Field img:TImage
	Field chardata:TBitmapCharData[96]
	
	Method Load:TBitmapFont(url:Object)
		img=LoadImage(String(url)+"_img.png")
		If img=Null Return Null
		
		Local dat:TStream=ReadStream(String(url)+"_data.dat")
		If dat=Null Return Null
		
		For Local i:Int=0 To 94
			Local c:TBitmapCharData=New TBitmapCharData
			c.ind=ReadInt(dat)
			c.x=ReadInt(dat)
			c.y=ReadInt(dat)
			c.w=ReadInt(dat)
			c.h=ReadInt(dat)
			chardata[i]=c
		Next
		CloseStream(dat)
		
		Return Self
	EndMethod
	
	Method Draw(t:String,x:Float,y:Float)
		Local dx:Float=x,dy:Float=y
		Local h:Float=GetHeight(" ")
		Local xscale:Float,yscale:Float
		Local rot:Float
		
		GetScale(xscale,yscale)
		rot=GetRotation()
		SetRotation 0
		
		For Local i:Int=0 To t.length-1
			Local ind:Int=t[i]
			If Chr(ind)="~n"
				dx=x
				dy:+(h*yscale)
			Else
				Local c:TBitmapCharData=chardata[ind-32]
				DrawSubImageRect(img,dx,dy,c.w,c.h,c.x,c.y,c.w,c.h)
				dx:+(c.w*xscale)
			EndIf
		Next
		
		SetRotation rot
	EndMethod
	
	Method GetHeight:Int(t:String)
		Local h:Int=0
		
		For Local i:Int=0 To t.length-1
			Local ind:Int=t[i]
			If Chr(ind)="~n" Continue
			
			Local c:TBitmapCharData=chardata[ind-32]
			If c.h>h h=c.h
		Next
		
		Return h
	EndMethod
	
	Method GetWidth:Int(t:String)
		Local w:Int=0
		
		For Local i:Int=0 To t.length-1
			Local ind:Int=t[i]
			If Chr(ind)="~n" Continue
			
			Local c:TBitmapCharData=chardata[ind-32]
			If c.w>w w=c.w
		Next
		
		Return w
	EndMethod
	
	Method Delete()
		chardata=Null
		img=Null
	EndMethod
EndType

Rem
'Example 1 : Create a bitmap font
Graphics 800,600

Global fview:TBitmapFontViewer=New TBitmapFontViewer.CreateNew(GetEnv_("systemroot")+"/fonts/trebuc.ttf",16)
fview.Save("TrebuchetMS16")

Repeat
	If AppTerminate() End
	If KeyHit(KEY_ESCAPE) End
	
	fview.Update()
	
	Cls
	
	fview.Draw()
	
	Flip
Forever
EndRem

Rem
'Example 2 : Use a bitmap font
Graphics 800,600

Global f:TBitmapFont=New TBitmapFont.Load("TrebuchetMS16")

Repeat
	If AppTerminate() End
	If KeyHit(KEY_ESCAPE) End
	
	Cls
	
	SetScale 1,1
	SetRotation 90
	f.Draw("Hello! This is a bitmap font routine for BlitzMax.~nI hope you enjoy it.~n~nOr else...",MouseX(),MouseY())
	
	Flip()
Forever
EndRem
