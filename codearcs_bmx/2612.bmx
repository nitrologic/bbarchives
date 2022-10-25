; ID: 2612
; Author: slenkar
; Date: 2009-11-17 10:26:03
; Title: unofficial glmax2d
; Description: replacement for glmax2d

' Slenkars unofficial GL Graphics

Rem
' NOTE: open window with:
  InitGL width,height [,depth=0][,fps=30]
EndRem

Strict
Module keef.gldraw
Import bah.libxml
Import BRL.LinkedList
Import BRL.PngLoader
Import BRL.JpgLoader
Import Pub.OpenGL
Import BRL.GLGraphics
Import PUB.Glew

Const MASKBLEND=1
Const SOLIDBLEND=2
Const ALPHABLEND=3
Const LIGHTBLEND=4
Const SHADEBLEND=5

'flags for frames/images
Const MASKEDIMAGE=		$1
Const FILTEREDIMAGE=	$2
Const MIPMAPPEDIMAGE=	$4
Const DYNAMICIMAGE=		$8

Type font_char
	Field x#,y#,width#,height#,id
	Field vertex[9]
	Field pixel_width#,pixel_height#
	Field yoffset,xoffset,xadvance
EndType

Type image_font
	Global list:TList=New TList
	Method New()
		list.AddLast Self
	EndMethod
	Field tex:TImage
	Field image_width#
	Field image_height#
	Field name$
	Field backgroundr
	Field backgroundg
	Field backgroundb
	Field char_array:font_char[256]
	Field height
	Field font_height
EndType


Type TImage
	Field num,height#,width#
	Field handlex,handley
	Field path$
	Field vertex#[9]
	Field animx#[]
	Field animy#[]
	Field cell_width#
	Field cell_height#
	Field real_cell_width#
	Field real_cell_height#
	Field cell_count
	Field child_x,child_y
	Field child_rotation
	Field child_list:TList
	Field child_frame
	Field parent:TImage
	Method ResetVerts()
		Local iw#=width , ih#=height
		If cell_count>0
			iw=cell_width ; ih=cell_height
		EndIf
		vertex[0]=0 ; vertex[1]=0
		vertex[2]=iw ; vertex[3]=0
		vertex[4]=iw ; vertex[5]=ih
		vertex[6]=0 ; vertex[7]=ih
	End Method
EndType

Global rotation=0
Global scalex#=1.0
Global scaley#=1.0
Global colorr=255
Global colorg=255
Global colorb=255
Global maskcolorr=0
Global maskcolorg=0
Global maskcolorb=0
Global globalalpha#
Global imagefont:image_font
Global glzoom#

Function InitGL(xsize,ysize,screendepth=0,fps=30)
	GLGraphics(xsize,ysize,ScreenDepth,fps,GRAPHICS_BACKBUFFER|GRAPHICS_DEPTHBUFFER) '| BGL_FULLSCREEN
	glMatrixMode (GL_PROJECTION)
	glLoadIdentity ()
	glClearColor(0, 0, 0, 0)
	glOrtho (0, XSize, YSize, 0, 0, 1)
	glDisable(GL_DEPTH_TEST)
	glMatrixMode (GL_MODELVIEW)
	glLoadIdentity()
	glTranslatef(0.375, 0.375, 0)
	glEnable(GL_BLEND)
	glEnable(GL_TEXTURE_2D)
	glenable(GL_SCISSOR_TEST)
	glscissor(0,0,xsize,ysize)
	glblendfunc(GL_SRC_ALPHA,GL_ONE_MINUS_SRC_ALPHA)
	SetAlpha 1.0
	Cls ; Flip ; Cls
EndFunction


Function setfont(i:image_font)
	imagefont=i
EndFunction


Function GetScale(var1 Var,var2 Var)
	var1=scalex ; var2=scaley
EndFunction

Function GetRotation()
	Return rotation
EndFunction

Global state_blend
Global state_boundtex
Global state_texenabled

Function EnableTex(name)
	BindTex name
	If state_texenabled Return
	glEnable GL_TEXTURE_2D
	state_texenabled=True
End Function

Function DisableTex()
	If Not state_texenabled Return
	glDisable GL_TEXTURE_2D
	state_texenabled=False
End Function

Function BindTex( name )
	If name=state_boundtex Return
	glBindTexture GL_TEXTURE_2D,name
	state_boundtex=name
End Function

Function SetZoom(zoom#)
	glzoom=zoom
EndFunction

Function DrawImage(img:TImage,x,y,frame=0)
	EnableTex img.num
	gltranslatef(x,y,0)
	glrotatef(rotation,0,0,1)
	glscalef(scalex,scaley,1)
	If img.cell_count=0
		glBegin(GL_QUADS)
		glTexCoord2f(0,0)
		glVertex2f(img.vertex[0],img.vertex[1] )
		glTexCoord2f(1,0)
		glVertex2f(img.vertex[2], img.vertex[3])
		glTexCoord2f(1,1)
		glVertex2f(img.vertex[4], img.vertex[5])
		glTexCoord2f(0,1)
		glVertex2f(img.vertex[6], img.vertex[7])
		glend
	EndIf
	If img.cell_count>0
		Local i:TImage=TImage(img)
		glBegin(GL_QUADS)
		glTexCoord2f(i.animx[frame],i.animy[frame])
		glVertex2f(i.vertex[0],i.vertex[1] )
		glTexCoord2f(i.animx[frame]+i.cell_width,i.animy[frame])
		glVertex2f(i.vertex[2], i.vertex[3])
		glTexCoord2f(i.animx[frame]+i.cell_width,i.animy[frame]+i.cell_height)
		glVertex2f(i.vertex[4], i.vertex[5])
		glTexCoord2f(i.animx[frame],i.animy[frame]+i.cell_height)
		glVertex2f(i.vertex[6], i.vertex[7])
		glend
	EndIf
	If img.child_list<>Null
		If img.child_list.isempty()=False
			For Local j:TImage=EachIn img.child_list
				DrawChildImage(j,x,y,rotation)
			Next
		EndIf
	EndIf
	glloadidentity()
	gltranslatef(0.375,0.375,0)
EndFunction

Function DrawChildImage(j:TImage,parentx,parenty,parentrot)
	glloadidentity()
	gltranslatef(0.375,0.375,0)
	gltranslatef(parentx,parenty,0)
	glrotatef(parentrot,0,0,1)
	gltranslatef(j.child_x,j.child_y,0)
	glrotatef(j.child_rotation,0,0,1)
	If j.cell_count=0
		glBegin(GL_QUADS)
		glTexCoord2f(0,0)
		glVertex2f(j.vertex[0],j.vertex[1] )
		glTexCoord2f(1,0)
		glVertex2f(j.vertex[2], j.vertex[3])
		glTexCoord2f(1,1)
		glVertex2f(j.vertex[4], j.vertex[5])
		glTexCoord2f(0,1)
		glVertex2f(j.vertex[6],j.vertex[7])
		glend
	EndIf
	If j.cell_count>0
		glBegin(GL_QUADS)
		glTexCoord2f(j.animx[j.child_frame],j.animy[j.child_frame])
		glVertex2f(j.vertex[0],j.vertex[1] )
		glTexCoord2f(j.animx[j.child_frame]+j.cell_width,j.animy[j.child_frame])
		glVertex2f(j.vertex[2], j.vertex[3])
		glTexCoord2f(j.animx[j.child_frame]+j.cell_width,j.animy[j.child_frame]+j.cell_height)
		glVertex2f(j.vertex[4], j.vertex[5])
		glTexCoord2f(j.animx[j.child_frame],j.animy[j.child_frame]+j.cell_height)
		glVertex2f(j.vertex[6], j.vertex[7])
		glend
	EndIf
	If j.child_list<>Null
		If j.child_list.isempty()=False
			For Local i:TImage=EachIn j.child_list
				drawchildimage(i,j.child_X,j.child_y,j.child_rotation)
			Next
		EndIf
	EndIf
EndFunction


Function DrawOval(x1,y1,width#,height#,solid=True)
	If solid
		DisableTex
		glBegin(GL_TRIANGLE_FAN)
	Else
		glBegin(GL_LINE_LOOP)
	EndIf
	gltranslatef(x1,y1,0)
	glrotatef(rotation,0,0,1)
	width:*0.5 ; height:*0.5
	For Local angle# = 0 To 360 Step 10
		glVertex2f(x1+Sin(angle)*width,y1+Cos(angle)*height)
	Next
	glEnd()
	gltranslatef(0.375,0.375,0)
EndFunction

Function DrawLine(x1,y1,x2,y2)
	glBegin(GL_LINES)
	glVertex2f(x1,y1) ; glVertex2f(x2,y2)
	glEnd()
EndFunction

Function DrawRect(posx#,posy#,width#,height#,solid=True)
	gltranslatef(posx,posy,0)
	glrotatef(rotation,0,0,1)
	If solid
		DisableTex
		glBegin(GL_QUADS)
	Else
		glBegin(GL_LINE_LOOP)
	EndIf
	glVertex2f 0,0 ; glVertex2f width,0
	glVertex2f width,height ; glVertex2f 0,height
	glEnd()
	glloadidentity()
	gltranslatef(0.375,0.375,0)
End Function

Function DrawShadeRect( posx#,posy#,width#,height#,r1,g1,b1,r2,g2,b2,r3,g3,b3,r4,g4,b4)
	DisableTex
	gltranslatef(posx,posy,0)
	glrotatef(rotation,0,0,1)
	glBegin(GL_QUADS)
	SetColor (r1,g1,b1)
	glVertex2f 0,0
	SetColor (r2,g2,b2)
	glVertex2f width,0
	SetColor (r3,g3,b3)
	glVertex2f width,height
	SetColor (r4,g4,b4)
	glVertex2f 0,height
	glend	
	glloadidentity()
	gltranslatef(0.375,0.375,0)
End Function

Function DrawTintText(txt$,x,y,tintR,tintG,tintB)
	If tintR>255
		tintR=10 ; tintG=10 ; tintB=10
	EndIf
	EnableTex imagefont.tex.num
	Local colorrr=colorr
	Local colorgg=colorg
	Local colorbb=colorb
	Local x_place=x
	Local highest_char
	For x=1 To txt.length
		Local char$=txt[x-1..x]
		Local f:font_char=imagefont.char_array[Asc(char)]
		If f<>Null
			If f.pixel_height>highest_char
				highest_char=f.pixel_height
			EndIf 
		EndIf
	Next
	For x=1 To txt.length
		Local char$= txt[x-1..x]
		Local f:font_char=imagefont.char_array[Asc(char)]
		If f<>Null
			'Local y_place=y+((highest_char-f.pixel_height)+f.yoffset)
			Local y_place=y+(f.yoffset*scaley)
			gltranslatef(x_place,y_place,0)
			glrotatef(rotation,0,0,1)
			glscalef(scalex,scaley,1)
			glBegin(GL_QUADS)
			SetColor(colorrr,colorgg,colorbb)
			glTexCoord2f(f.x,f.y)
			glVertex2f(f.vertex[0],f.vertex[1] )
			glTexCoord2f(f.x+f.width,f.y)
			glVertex2f(f.vertex[2], f.vertex[3])
			SetColor(tintr,tintg,tintb)
			glTexCoord2f(f.x+f.width,f.y+f.height)
			glVertex2f(f.vertex[4], f.vertex[5])
			glTexCoord2f(f.x,f.y+f.height)
			glVertex2f(f.vertex[6], f.vertex[7])
			glend
			glloadidentity()
			gltranslatef(0.375,0.375,0)
			x_place:+((f.xadvance*scalex))
			x_place=x_place+(-f.xoffset*scalex)
		EndIf
	Next
	SetColor(colorrr,colorgg,colorbb)
EndFunction

Function DrawText(txt$,x,y)
	If imagefont=Null
		GLDrawText txt,x,y
		Return
		'RuntimeError "No font is set yet, use SetImageFont"
	EndIf
	EnableTex imagefont.tex.num
	Local colorrr=colorr
	Local colorgg=colorg
	Local colorbb=colorb
	Local x_place=x
	Local highest_char
	For x=1 To txt.length
		Local char$=txt[x-1..x]
		Local f:font_char=imagefont.char_array[Asc(char)]
		If f<>Null
			If f.pixel_height>highest_char
				highest_char=f.pixel_height
			EndIf 
		EndIf
	Next
	For x=1 To txt.length
		Local char$= txt[x-1..x]
		Local f:font_char=imagefont.char_array[Asc(char)]
		If f<>Null
			'Local y_place=y+((highest_char-f.pixel_height)+f.yoffset)
			Local y_place=y+(f.yoffset*scaley)
			gltranslatef(x_place,y_place,0)
			glrotatef(rotation,0,0,1)
			glscalef(scalex,scaley,1)
			glBegin(GL_QUADS)
			SetColor(colorrr,colorgg,colorbb)
			glTexCoord2f(f.x,f.y)
			glVertex2f(f.vertex[0],f.vertex[1] )
			glTexCoord2f(f.x+f.width,f.y)
			glVertex2f(f.vertex[2], f.vertex[3])
			glTexCoord2f(f.x+f.width,f.y+f.height)
			glVertex2f(f.vertex[4], f.vertex[5])
			glTexCoord2f(f.x,f.y+f.height)
			glVertex2f(f.vertex[6], f.vertex[7])
			glEnd
			glloadidentity()
			gltranslatef(0.375,0.375,0)
			x_place:+((f.xadvance*scalex))
			x_place=x_place+(-f.xoffset*scalex)
		EndIf
	Next
	SetColor(colorrr,colorgg,colorbb)
EndFunction

'Function GrabImage:TImage(i:TImage,x,y)
'glBindTexture GL_TEXTURE_2D,i.num
'glCopyTexSubImage2D GL_TEXTURE_2D,0,0,0,x,y,width,height
'glCopyTexsubImage2D(GL_TEXTURE_2D, 0,0, 0,x, GraphicsHeight()-i.height-y, i.width, i.height)
'End Function

Function GrabImage:TImage(x,y,width,height)
	Local i:TImage=CreateImage(width,height)
	glBindTexture GL_TEXTURE_2D,i.num
	glCopyTexSubImage2D GL_TEXTURE_2D,0,0,0,x,GraphicsHeight()-i.height-y,width,height
	Return i
End Function

Function CreateImage:TImage(width,height)
	Local i:TImage=New TImage
	i.width=Width
	i.height=Height
	i.ResetVerts()	
	glGenTextures 1,Varptr i.num
	BindTex i.num	
	'set texture parameters
	glTexParameteri GL_TEXTURE_2D,GL_TEXTURE_WRAP_S,GL_CLAMP_TO_EDGE
	glTexParameteri GL_TEXTURE_2D,GL_TEXTURE_WRAP_T,GL_CLAMP_TO_EDGE
	glTexParameteri GL_TEXTURE_2D,GL_TEXTURE_MAG_FILTER,GL_LINEAR
	glTexParameteri GL_TEXTURE_2D,GL_TEXTURE_MIN_FILTER,GL_LINEAR
	glTexParameteri GL_TEXTURE_2D,GL_TEXTURE_MAG_FILTER,GL_NEAREST
	glTexParameteri GL_TEXTURE_2D,GL_TEXTURE_MIN_FILTER,GL_NEAREST
	Local mip_level
	glTexImage2D GL_TEXTURE_2D,mip_level,GL_RGBA8,width,height,0,GL_RGBA,GL_UNSIGNED_BYTE,Null
	Return i
EndFunction

Function LoadImage:TImage(url:String)
	If FileType(url)=1
		Local pix:TPixmap=LoadPixmap(url)
		If pix=Null
			RuntimeError "pixmap is null: "+url
		EndIf
		pix=MaskPixmap(pix,maskcolorr,maskcolorg,maskcolorb)
		Local tex=GLTexFromPixmap(pix)
		Local i:TImage=New TImage
		i.num=tex ; i.path=url
		i.width=PixmapWidth(pix)
		i.height=PixmapHeight(pix)
		i.ResetVerts()
		Return i
	Else
		RuntimeError "Image not found: "+url
	EndIf
EndFunction

Function LoadAnimImage:TImage(url:Object,cell_width,cell_height,first_cell,cell_count,flags=MASKEDIMAGE)
	Local pix:TPixmap
	If String(url)
		Local ur:String=String(url)
		If FileType(ur)=1
			pix:TPixmap=LoadPixmap(ur)
		Else
			RuntimeError "Image not found: "+ur
		EndIf
	EndIf
	If TPixmap(url)
		Local ur:TPixmap=TPixmap(url)
		pix=ur
	EndIf
	pix=MaskPixmap(pix,maskcolorr,maskcolorg,maskcolorb)
	Local tex=GLTexFromPixmap(pix)
	Local i:TImage=New TImage
	i.num=tex
	If String(url)
		Local ur:String=String(url)
		i.path=ur
	EndIf
	i.width=PixmapWidth(pix)
	i.height=PixmapHeight(pix)
	i.animx=i.animx[..cell_count]
	i.animy=i.animy[..cell_count]
	i.real_cell_width=cell_width
	i.real_cell_height=cell_height
	i.cell_count=cell_count
	i.ResetVerts()
	Local w_iter=i.width/cell_width
	Local h_iter=i.height/cell_height
	Local cell_iter=0
	For Local y=0 To h_iter-1
		For Local x=0 To w_iter-1
			Local length=x*cell_width
			Local height=y*cell_height
			i.animx[cell_iter]=length/i.width
			i.animy[cell_iter]=height/i.height
			cell_iter:+1
		Next
	Next
	i.cell_width=cell_width/i.width
	i.cell_height=cell_height/i.height
	Return i
EndFunction

Function ImageWidth(i:TImage)
	If i.cell_count=0
		Return i.width
	Else
		Return i.real_cell_width
	EndIf
EndFunction

Function ImageHeight(i:TImage)
	If i.cell_count=0
		Return i.height
	Else
		Return i.real_cell_height
EndIf
EndFunction

Function ImageParent(child:TImage,parent:TImage)
	If parent<>Null
		If parent.child_list=Null
			parent.child_list=New TList
		EndIf
		parent.child_list.addlast(child)
		child.parent=parent
	Else
		child.parent.child_list.remove(child)
		child.parent=Null
	EndIf
EndFunction

Function SetImageFont(i:image_font)
	imagefont=i
EndFunction


Function LoadImageFont:image_font(url:String,incstring$)
	Local x,y
	Local doc:TxmlDoc
	Local node:TxmlNode
	Local thisnode:TxmlNode
	Local charnode:TxmlNode
	Local filename$
	Local docname$=incstring+url
	doc = TxmlDoc.parseFile(docname) 
	If doc = Null
		RuntimeError docname + " not found"
	EndIf
	node=doc.getrootelement()
	Local i:image_font=New image_font
	i.name=StripAll(url)
	Local nodeList:TList = node.getChildren()
	For thisnode = EachIn nodeList
		If thisnode.getname()="img"
			i.image_width=Float(Int(thisnode.getattribute("width")))
			i.image_height=Float(Int(thisnode.getattribute("height")))
			i.backgroundr=Int(thisnode.getattribute("backgroundr"))
			i.backgroundg=Int(thisnode.getattribute("backgroundg"))
			i.backgroundb=Int(thisnode.getattribute("backgroundb"))
			i.font_height=Int(thisnode.getattribute("fontheight"))
			SetMaskColor (i.backgroundr,i.backgroundg,i.backgroundb)
			i.tex=LoadImage(StripExt(url)+".png")
		EndIf
		If thisnode.getName()="chars"
			Local charlist:TList=thisnode.getchildren()
			For charnode=EachIn charlist
				Local fo:font_char=New font_char
				fo.xoffset=Int(charnode.getattribute("xoffset"))
				fo.yoffset=Int(charnode.getattribute("yoffset"))
				fo.pixel_width=Float(charnode.getattribute("width"))
				fo.pixel_height=Float(charnode.getattribute("height"))
				fo.width=fo.pixel_width/i.image_width
				fo.height=fo.pixel_height/i.image_height
				fo.id=Int(charnode.getattribute("ascnum"))
				fo.xadvance=Int(charnode.getattribute("xadvance"))
				fo.x=Float(charnode.getattribute("x"))
				fo.y=Float(charnode.getattribute("y"))
				fo.x=fo.x/i.image_width
				fo.y=fo.y/i.image_height
				fo.vertex[0]=0
				fo.vertex[1]=0
				fo.vertex[2]=fo.pixel_width
				fo.vertex[3]=0
				fo.vertex[4]=fo.pixel_width
				fo.vertex[5]=fo.pixel_height
				fo.vertex[6]=0
				fo.vertex[7]=fo.pixel_height
				i.char_array[fo.id]=fo
			Next
		EndIf
	Next
	doc.free()
	SetMaskColor (0,0,0)
	Return i
End Function


Function MidHandleImage(i:TImage)
	If i.cell_count>0
		Local ij:TImage=TImage(i)
		SetImageVertices(ij,-(ij.real_cell_width/2),-(ij.real_cell_height/2),(ij.real_cell_width/2),-(ij.real_cell_height/2),(ij.real_cell_width/2),(ij.real_cell_height/2),-(ij.real_cell_width/2),(ij.real_cell_height/2))
	Else
		SetImageVertices(i,-(i.width/2),-(i.height/2),(i.width/2),-(i.height/2),(i.width/2),(i.height/2),-(i.width/2),(i.height/2))
	EndIf
EndFunction
	
Function SetAlpha( alpha# )
	Local red#=colorr/255.0
	Local green#=colorg/255.0
	Local blue#=colorb/255.0
	globalalpha=alpha
	glColor4f(red,green,blue,alpha)
End Function

Function SetLineWidth( width# )
	glLineWidth width
End Function
		
Function SetColor( red#,green#,blue#)
	colorr=red ; 	colorg=green ; colorb=blue
	glColor4f(red/255.0,green/255.0,blue/255.0,globalalpha#)
End Function

Function SetClsColor(red,green,blue )
	red=Min(Max(red,0),255)
	green=Min(Max(green,0),255)
	blue=Min(Max(blue,0),255)
	glClearColor red/255.0,green/255.0,blue/255.0,1.0
End Function
		
Function SetImageXWhenChild(i:TImage,x)
	i.child_x=x
EndFunction

Function SetImageYWhenChild(i:TImage,y)
i.child_y=y
EndFunction

Function SetImageRotationWhenChild(i:TImage,x)
i.child_rotation=x
EndFunction


Function SetImageHandle(i:TImage,x,y)
	i.handlex=x ; i.handley=y
EndFunction

Function SetScale(x:Float,y:Float)
	scalex=x ; scaley=y
EndFunction

Function SetMaskColor(r,g,b)
	maskcolorr=r ; maskcolorg=g ; maskcolorb=b
EndFunction

Function SetBlend( blend )
	If blend=state_blend Return
	state_blend=blend
	Select blend
		Case MASKBLEND
		glDisable GL_BLEND
		glEnable GL_ALPHA_TEST
		glAlphaFunc GL_GEQUAL,.5
		Case SOLIDBLEND
		glDisable GL_BLEND
		glDisable GL_ALPHA_TEST
		Case ALPHABLEND
		glEnable GL_BLEND
		glBlendFunc GL_SRC_ALPHA,GL_ONE_MINUS_SRC_ALPHA
		glDisable GL_ALPHA_TEST
		Case LIGHTBLEND
		glEnable GL_BLEND
		glBlendFunc GL_SRC_ALPHA,GL_ONE
		glDisable GL_ALPHA_TEST
		Case SHADEBLEND
		glEnable GL_BLEND
		glBlendFunc GL_DST_COLOR,GL_ZERO
		glDisable GL_ALPHA_TEST
		Default
		glDisable GL_BLEND
		glDisable GL_ALPHA_TEST
	End Select
EndFunction

Function Plot(x,y)
	DisableTex
	glBegin(GL_POINTS)
	glVertex2f(x,y)
	glEnd()
EndFunction
 
Function SetViewport(x,y,width,height)
	glviewport(x,y,width,height)
EndFunction

Function SetRotation(rot)
	rotation=rot
EndFunction

Function TextHeight(str:String)
	Return imagefont.font_height
EndFunction

Function TextWidth(str:String)
	Local width
	For Local x=1 To str.length
		Local char$= str[x-1..x]
		Local f:font_char=imagefont.char_array[Asc(char)]
		If f<>Null
			width:+(f.xadvance*scalex)
			width:+(-f.xoffset*scalex)
		EndIf
	Next
	Return width
EndFunction
	

Function Cls()
	glClear(GL_COLOR_BUFFER_BIT)
EndFunction


Function SetImageVertices(i:TImage,x0#,y0#,x1#,y1#,x2#,y2#,x3#,y3#)
	i.vertex=[x0,y0,x1,y1,x2,y2,x3,y3]
EndFunction
