; ID: 2273
; Author: JoshK
; Date: 2008-06-16 18:17:25
; Title: SavePixmapBMP
; Description: Save a bitmap image file

Strict

Import brl.pixmap
Import brl.stream
Import brl.EndianStream

Function SavePixmapBMP(pixmap:TPixmap,url:Object)
	Local stream:TStream
	Local buf:Byte[]
	Local hsize,hoffset
	Local size,width,height
	Local planes,bits,compression,isize,xpels,ypels,cols,inuse
	Local w,y
	stream=WriteStream(url)
	If Not stream Return
	width=pixmap.width
	height=pixmap.height
	w=width*3
	w=(w+3)&$fffc
	hsize=w*height+54
	size=40
	hoffset=54
	planes=1
	bits=24
	compression=0
	isize=40
	xpels=2834
	ypels=2834
	cols=0
	inuse=0
	stream=LittleEndianStream(stream)
	WriteByte stream,Asc("B")
	WriteByte stream,Asc("M")
	WriteInt stream,hsize
	WriteInt stream,0
	WriteInt stream,hoffset
	WriteInt stream,size
	WriteInt stream,width
	WriteInt stream,height
	WriteShort stream,planes
	WriteShort stream,bits
	WriteInt stream,compression
	WriteInt stream,isize
	WriteInt stream,xpels
	WriteInt stream,ypels
	WriteInt stream,cols
	WriteInt stream,inuse	
	buf=New Byte[w]
	For y=height-1 To 0 Step -1
		ConvertPixels(pixmap.pixelptr(0,y),pixmap.format,buf,PF_BGR888,width)
		stream.WriteBytes(buf,w)
	Next
	stream.close()
	Return 1
EndFunction
