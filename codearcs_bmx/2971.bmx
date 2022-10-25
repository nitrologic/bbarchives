; ID: 2971
; Author: Pineapple
; Date: 2012-08-17 09:58:08
; Title: Save animated GIFs
; Description: Write pixmaps as animated (or static) GIFs, including optional automatic color reduction/palette detection

' 	--+-----------------------------------------------------------------------------------------+--
'	  |   This code was originally written by Sophie Kirschner (sophiek@pineapplemachine.com)   |  
' 	  | It is released as public domain. Please don't interpret that as liberty to claim credit |  
' 	  |   that isn't yours, or to sell this code when it could otherwise be obtained for free   |  
'	  |                because that would be a really shitty thing of you to do.                |
' 	--+-----------------------------------------------------------------------------------------+--


SuperStrict

Import brl.stream
Import brl.math
Import brl.pixmap
Import pine.HashTable ' http://blitzbasic.com/Community/posts.php?topic=97992#1141947
Import pine.heap ' http://blitzbasic.com/codearcs/codearcs.php?code=2970
Import "log2.bmx" ' http://blitzbasic.com/codearcs/codearcs.php?code=2965


' Example code
Rem
Local t:TPixmap[4]
For Local i%=0 To 3
	t[i]=CreatePixmap(128,128,pf_rgb888)
Next
ClearPixels t[0],$ff0000
ClearPixels t[1],$00ff00
ClearPixels t[2],$0070ff
ClearPixels t[3],$eeee00
SavePixmapsGIF t,"test.gif",100
EndRem


Rem
bbdoc: Save a Pixmap in GIF format
EndRem
Function SavePixmapGIF%(pix:TPixmap,url:Object,maxcolorcount%=256,tolerance%=-1,sub%=1,add%=2)
	If tolerance=-1 Then tolerance=768/maxcolorcount
	Local pal%[]=MakePalFromPixmap([pix],maxcolorcount,tolerance,sub,add,0)
	Local f:TStream=WriteStream(url)
	If Not f Then Return False
	WriteGif f,[pix],pal
	CloseStream f
	Return True
End Function

Rem
bbdoc: Save multiple pixmaps as an animation in GIF format
EndRem
Function SavePixmapsGIF%(pix:TPixmap[],url:Object,framedelay%,maxcolorcount%=256,tolerance%=-1,sub%=1,add%=2)
	If tolerance=-1 Then tolerance=768/maxcolorcount
	Local pal%[]=MakePalFromPixmap(pix,maxcolorcount,tolerance,sub,add,0)
	Local f:TStream=WriteStream(url)
	If Not f Then Return False
	WriteGif f,pix,pal,[framedelay]
	CloseStream f
	Return True
End Function

Rem
bbdoc: Automatically generate a palette from one or more pixmaps.
about: 
images:TPixmap[] is an array containing one or more pixmaps from which to operate
maxcolorcount% is the maximum number of colors allowed - set to 0 to return a basic palette of 8 colors.
tolerance% is a variable controlling how different two colors should be in order to be counted as separate
sub%, add% control how colors are prioritized and their precise effect can be very arbitrary. experimentation with different values for these and tolerance is highly recommended.
note: sub:add is treated as a ratio - sub=1,add=2 will produce the same result as sub=2,add=4.
transparency% specifies whether colors which are fully transparent should be ignored
EndRem
Global basicpal%[]=[$ffffff,$ff0000,$00ff00,$0000ff,$ffff00,$ff00ff,$00ffff,$000000,$808080]
Function MakePalFromPixmap%[](images:TPixmap[],maxcolorcount%=256,tolerance%=64,sub%=1,add%=2,transparency%=0)
	If maxcolorcount=0 Then Return basicpal
	Local colors:TList=CreateList(),rgb%
	For Local p:TPixmap=EachIn images
		If Not p Then Continue
		For Local x%=0 To PixmapWidth(p)-1
		For Local y%=0 To PixmapHeight(p)-1
			If (y Mod 4) Then Continue
			rgb=ReadPixel(p,x,y)
			If transparency And Not (rgb & $ff000000) Then Continue
			Local this:_cdata=New _cdata
			this.r=rgb Shr 16
			this.g=rgb Shr 8
			this.b=rgb
			Local found:TLink=Null,foundd%=0
			Local on:TLink=colors._head._succ,c:_cdata
			While on<>on._value
				c=_cdata(on._value)
				Local fd%=c.distance(this)
				If fd<=tolerance Then
					If (Not found) Or fd<=foundd
						found=on
						foundd=fd
						_cdata(found._value).pop:-sub
						If fd=0 Then Exit
					EndIf
				EndIf
				on=on._succ
			Wend
			If found
				_cdata(found._value).pop:+add
				movelinktofront found,colors
			Else
				colors.addfirst this
			EndIf
		Next
		Next
	Next
	Local cheap:THeap=CreateHeap()
	For Local c:_cdata=EachIn colors
		HeapInsert cheap,c
	Next
	Local ret%[]=New Int[Min(maxcolorcount,CountHeap(cheap))]
	For Local x%=0 To ret.length-1
		Local c:_cdata=_cdata(HeapRemove(cheap))
		ret[x]=$ff000000|(c.r Shl 16)|(c.g Shl 8)|c.b
	Next
	Return ret
End Function

Rem
bbdoc: Write an array of pixmaps to a stream as an animated GIF
about: 
f:TStream is the stream to which the GIF will be written
pixmaps:TPixmap[] is an array containing each frame as an individual pixmap
pal%[] is an array containing up to 256 colors and will be used as the palette
animdelay%[] is an array containing the duration (in 100ths of a second) of each frame. if this array is shorter than the pixmaps array then the frame delay to be used is the value in the index of the current frame modulo the number of frame delays defined.
looptimes% is how many times the animation should loop when viewed. $ffff indicates that it should loop forever.
transparentcolor% defines which index of the palette should be flagged as the transparent background color. -1 indicates that there is no transparent color.
animwidth% is the width of the animation. -1 indicates that it should be determined automatically as the maximum width of all the frames.
animheight% is the height of the animation. -1 indicates that it should be determined automatically as the maximum height of all the frames.
EndRem
Function WriteGIF(f:TStream,pixmaps:TPixmap[],pal%[],animdelay%[]=Null,looptimes%=$ffff,transparentcolor%=-1,animwidth%=-1,animheight%=-1)
	Assert f,"Encountered null stream."
	Assert pal,"Encountered null palette."
	Assert pixmaps,"Encountered null pixmap array."
	Assert pal.length<=256,"Palette too long. (GIF supports sizes only up to 256)"
	Local logical_width%=animwidth,logical_height%=animheight
	Local detw%=animwidth=-1,deth%=animheight=-1
	For Local pix:TPixmap=EachIn pixmaps
		Assert pix,"Encountered null pixmap."
		If detw logical_width=Max(pix.width,logical_width)
		If deth logical_height=Max(pix.height,logical_height)
	Next
	' header
	WriteString f,"GIF89a"
	' logical screen descriptor
	WriteShort f,logical_width
	WriteShort f,logical_height
	Local lbits%=%11110000 | ((clog2(pal.length)-1))
	WriteByte f,lbits
	WriteByte f,0
	WriteByte f,0
	' global color table
	gifwritepalette f,pal
	' application extension block
	WriteByte f,$21
	WriteByte f,$ff
	WriteByte f,11
	WriteString f,"NETSCAPE"
	WriteString f,"2.0"
	WriteByte f,3
	WriteByte f,1
	WriteShort f,looptimes
	WriteByte f,0
	' write frames
	Local di%=0
	For Local pix:TPixmap=EachIn pixmaps
		Local del%=1
		If animdelay Then del=animdelay[di Mod animdelay.length]
		WriteGIFFrame f,pix,pal,del,0,0,transparentcolor,False
		di:+1
	Next
	WriteByte f,$3b
End Function
Rem
bbdoc: Writes an individual pixmap to a stream as the frame of a GIF image.
f:TStream is the stream to which the GIF will be written
pix:TPixmap is the pixmap to write
pal%[] is an array containing up to 256 colors and will be used as the palette
framedelay% is how long in 100ths of a second the frame should last when animating
xcorner% is the x offset (left toward right) of this frame in the animation
ycorner% is the y offset (top toward bottom) of this frame in the animation
transparentcolor% defines which index of the palette should be flagged as the transparent background color. -1 indicates that there is no transparent color.
localtable% is a flag that decides whether the palette should be considered unique to this frame of the GIF animation
EndRem
Function WriteGIFFrame(f:TStream,pix:TPixmap,pal%[],framedelay%=50,xcorner%=0,ycorner%=0,transparentcolor%=-1,localtable%=True)
	' graphic control exension
	WriteByte f,$21
	WriteByte f,$f9
	WriteByte f,$04
	Local hastransparentcolor%=(transparentcolor>-1)
	WriteByte f,hastransparentcolor
	WriteShort f,framedelay
	WriteByte f,transparentcolor*hastransparentcolor
	WriteByte f,$00
	' image descriptor
	WriteByte f,$2c
	WriteShort f,xcorner ' location of x corner
	WriteShort f,ycorner ' y corner
	WriteShort f,pix.width
	WriteShort f,pix.height
	If localtable Then
		WriteByte f,%10000000 | ((clog2(pal.length)-1) Shl 4)
		gifwritepalette f,pal
	Else
		WriteByte f,0
	EndIf
	Local bpp%=Max(clog2(pal.length),2)
	WriteByte f,bpp
	' lzw-compress the data
	Local minsize%=bpp+1
	Const maxsize%=12
	Local clearcode%=1 Shl bpp
	Local endcode%=clearcode+1
	Local startoncode%=endcode+1
	Local oncode%=startoncode
	Local currentsize%=minsize
	Local firstcode:lzwc=lzwc.Create(currentsize,clearcode)
	Local thiscode:lzwc=firstcode
	Local c$="",ck$,k$
	Local bitlength%=currentsize
	Local table:HashTable=_lzwtable(minsize)
	Local yv%=0
	For Local y%=0 Until pix.height
		For Local x%=0 Until pix.width
			Local val%=gifgetclosestpalcolor(pix.ReadPixel(x,y),pal,pal.length)
			k=Chr(val);ck=c+k
			If table.find(ck)
				c=ck
			Else
				thiscode.succ=lzwc.Create(currentsize,lzwi(table.find(c)).value)
				bitlength:+currentsize
				thiscode=thiscode.succ
				table.insert ck,lzwi.Create(oncode);oncode:+1
				c=k
				If clog2(oncode)>currentsize Then 
					If currentsize=maxsize Then
						thiscode.succ=lzwc.Create(currentsize,clearcode)
						thiscode=thiscode.succ
						bitlength:+currentsize
						currentsize=minsize
						oncode=startoncode
						table=_lzwtable(minsize)
					Else
						currentsize:+1
					EndIf
				EndIf
			EndIf
		Next
		yv:+pix.width
	Next
	thiscode.succ=lzwc.Create(currentsize,lzwi(table.find(c)).value)
	thiscode.succ.succ=lzwc.Create(currentsize,endcode)
	bitlength:+currentsize+currentsize
	' turn into an array of bytes
	Local data@[Ceil(bitlength/8.0)],onbit%=0
	thiscode=firstcode;firstcode=Null
	While thiscode
		For Local i%=0 Until thiscode.bits
			Local di%=onbit Shr 3
			Local thisbit%=(((thiscode.value Shr i)&1) Shl (onbit&7))
			data[di]=data[di] | thisbit
			onbit:+1
		Next
		Local n:lzwc=thiscode.succ;thiscode.succ=Null;thiscode=n
	Wend
	' write the bytes
	For Local i%=0 Until data.length
		If (i Mod 255)=0 Then 
			Local chunksize%=Min(255,data.length-i)
			WriteByte f,chunksize
		EndIf
		WriteByte f,data[i]
	Next
	WriteByte f,$00
End Function
' Writes a palette for a GIF image
Function gifwritepalette(f:TStream,pal%[])
	For Local i%=0 Until (1 Shl clog2(pal.length))
		If i<pal.length
			WriteByte f,(pal[i] Shr 16) '& $ff
			WriteByte f,(pal[i] Shr 8) '& $ff
			WriteByte f,(pal[i]) '& $ff
		Else
			WriteByte f,0;WriteByte f,0;WriteByte f,0
		EndIf
	Next
End Function
' Finds the closest color in a palette to a given color
Global cachedcolor%=0
Function gifgetclosestpalcolor%(argb%,pal% Ptr,pallength%)
	' note: ignores alpha
	Local besti%=cachedcolor,bestdist%=gifgetcolordistance(argb,pal[cachedcolor])
	For Local i%=0 Until pallength
		Local d%=gifgetcolordistance(argb,pal[i])
		If d<bestdist Then
			besti=i;bestdist=d
		EndIf
		If bestdist=0 Exit
	Next
	Return besti
End Function
' Gets the distance from one color to another, slightly adjusted to account for luminosity
Function gifgetcolordistance%(argb1%,argb2%)
	Local r1%=(argb1 Shr 16)&$ff
	Local g1%=(argb1 Shr 8)&$ff
	Local b1%=(argb1)&$ff
	Local r2%=(argb2 Shr 16)&$ff
	Local g2%=(argb2 Shr 8)&$ff
	Local b2%=(argb2)&$ff
	Return (Abs(r1-r2) Shl 1)+(Abs(g1-g2) Shl 2)+Abs(b1-b2)
End Function

Private
' Returns a new hash table to be used as the string table in lzw compression
Function _lzwtable:HashTable(minsize%)
	Local ret:HashTable=CreateHash(1 Shl minsize,_lzwhash)
	For Local i%=0 Until (1 Shl minsize)
		ret.insert Chr(i),lzwi.Create(i)
	Next
	Return ret
End Function
' Hash function, should be faster and well-suited to the specific data going in from the lzw compression
Function _lzwhash%(str$)
	Local ret%=str.length
	For Local i%=0 Until str.length
		ret:+(str[i] Shl ((i&7) Shl 4))
	Next
	Return ret
End Function
' integer container object because I couldn't be arsed to write a hash table specifically for the lzw compression algorithm (and pine.hash can only contain objects)
Type lzwi
	Field value%
	Function Create:lzwi(value%)
		Local n:lzwi=New lzwi
		n.value=value
		Return n
	End Function
End Type
' lzw code object containing a value, the number of bits needed to represent that value, and the next code in the series
Type lzwc
	Field bits%
	Field value%
	Field succ:lzwc
	Function Create:lzwc(bits%,value%)
		Local n:lzwc=New lzwc
		n.bits=bits
		n.value=value
		Return n
	End Function
End Type
' object used for constructing a limited palette from a pixmap
Type _cdata
	Field r@,g@,b@
	Field pop%=1
	Function Create:_cdata(r%,g%,b%)
		Local c:_cdata=New _cdata
		c.r=r;c.g=g;c.b=b
		Return c
	End Function
	Method distance%(o:_cdata)
		Return (Abs(r-o.r) Shl 1)+(Abs(g-o.g) Shl 2)+Abs(b-o.b)
	End Method
	Method compare%(o1:Object)
		If pop>_cdata(o1).pop Return 1
		Return -1
	End Method
End Type
' moves a TLink to the front of a TList
Function movelinktofront(link:TLink,list:TList)
	If link=list.firstlink() Then Return
	link._succ._pred=link._pred
	link._pred._succ=link._succ
	link._pred=list._head
	link._succ=list._head._succ
	link._succ._pred=link
	list._head._succ=link
End Function
