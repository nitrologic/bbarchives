; ID: 2199
; Author: Nebula
; Date: 2008-01-20 13:45:35
; Title: Interface maker bluegrid (b+)
; Description: spacy Windows gui

;
;
;
Global wwidth = 640
Global wheight =  480

Global win = CreateWindow("Test Window - Basic (3)",200,100,wwidth,wheight,0,3^2) 
Global can = CreateCanvas(0,0,GadgetWidth(win),GadgetHeight(win),win)
;
Global md,mu
; wait until the user closes one of the windows 
;
Dim coppermap(1,640)
Dim genmap(100,100)
subdivide 0,0,100,100
Type editor
	Field x,y,w,h
	Field x1,y1,x2,y2,ix1,iy1,mc
	Field gt
End Type
Global editor.editor = New editor
editor\gt=1
Type shape
	Field bmap
	Field x,y,w,h,div#
	Field tp	
End Type
;
Type bmap
	Field gridmap,colormap,effectmap,tempmap
		
End Type
Global bmap.bmap = New bmap
bmap\gridmap = CreateImage(GadgetWidth(can),GadgetHeight(can))
bmap\colormap = CreateImage(GadgetWidth(can),GadgetHeight(can))
bmap\effectmap = CreateImage(GadgetWidth(can),GadgetHeight(can))
bmap\tempmap = CreateImage(GadgetWidth(can),GadgetHeight(can))
;
drawgrid
;
makecoppermap
;
t = CreateTimer(20)
;
ms = MilliSecs()
;flashyblendoval 100,100,100,100,1.5
;flashyblendoval 200,200,64,64,3.5
ms = MilliSecs()-ms
;
Repeat 
	;
	vw$ = WaitEvent()
	If vw = $803 Then Exit 
	;
	Select vw
		Case $205
			ActivateGadget can
			
		Case $102
			Select EventData()
				Case 2:editor\gt = 1
				Case 3:editor\gt = 2
			End Select
		Case $201 ; mouse down
				If RectsOverlap(EventX(),EventY(),1,1,GadgetX(can),GadgetY(can),GadgetWidth(can),GadgetHeight(can)) = True Then
				md = True : mu = False			
				;this.shape = New shape
				;this\tp = 1
				;this\x = EventX()
				;this\y = EventY()
				;this\w = 100
				;this\h  = 100
				;this\div = 1.5
				editor\ix1 = EventX()
				editor\iy1 = EventY()
				editor\x1 = EventX()
				editor\y1 = EventY()
				editor\x2 = EventX()
				editor\y2 = EventY()
				End If
		Case $203
			If EventX() < GadgetWidth(can) And EventX() > 0
				editor\x2 = EventX()
			End If
			If EventY() < GadgetHeight(can)-32 And EventY()>0
				editor\y2 = EventY()
			End If
			
			
			
		Case $202 ; mouse up
			md = False : mu=True
			this.shape = New shape
			this\x = editor\x1
			this\y = editor\y1
			this\w = editor\x2-editor\x1
			this\h = editor\y2 - editor\y1
			this\div=Rnd(1,5)
			this\tp=editor\gt
				  If editor\x2 < editor\ix1 Then this\x = editor\x2 : this\w = editor\x1 - editor\x2
				  If editor\y2 < editor\iy1 Then this\y = editor\y2 : this\h = editor\y1 - editor\y2

			flashyblendoval(this.shape)
			FlushMouse()
		Case $4001
			SetBuffer CanvasBuffer(can)
			Cls
			DrawBlock bmap\gridmap,0,0
			DrawImage bmap\effectmap,0,0
			Color 255,255,255
			Text 0,0,md
			Text 0,20,mu
			;Rect 100,100,200,200
			;drawrectangles()
			;
			ax = Sin(n1) * 128
			n1=n1+16
			;DebugLog ax
			;
			If md = True Then ovalmouserect
			;
			Text 320,0, ms
			Text 320,20,shapecount()
			;flashyoval 128+ax,128+ax,64,64,128,128+ax,1
			;blendcopypasteoval 128+ax,128+ax,64,64,1.5
			;colmapdisplay
			;
			;blendcopypasteoval 320+ax,140-ax,32,32,1.5
			;
			FlipCanvas can
	End Select
	;
Forever 

End ; bye! 
Function drawrectangles()
	;
	For this.shape = Each shape
		;
		Select this\tp
			Case 1
				Color 200,0,0
				Oval this\x,this\y,this\w,this\h,True
		End Select
		;
	Next
	;
End Function
Function drawrectangle(x,y,w,h)
	Color 200,0,0
	Rect x,y,w,h,True
End Function

Function drawgrid()
	;
	SetBuffer ImageBuffer(bmap\gridmap)
		n = 0
		Color n,n,255
		nn = GadgetHeight(can)/16
		sw = 1
		For y = 0 To ImageHeight(bmap\gridmap) Step 32
		For x = 0 To ImageWidth(bmap\gridmap) Step 32	
			oldn = n
			n2 = getcolormapcolor(x,y,2)
			Color n2,n2,n2
			Rect x,y,33,33,False
			sw = -sw			
			n = oldn			
		Next:
			Color n,n,255
			n = n + nn
			If n > 256 - 32 Then nn = -nn
			;
			;DebugLog n
			;
		Next		
		; color map plotted
		Color 255,100,100
		For y=0 To ImageHeight(bmap\gridmap) Step 4
		For x=0 To ImageWidth(bmap\gridmap) Step 4
			;
			r = getcolormapcolor(x,y,3)
			Color r,r,r
			
			Plot x,y
			;
		Next:Next
		;
		;
				Color 255,100,100
			
			For i=0 To 460
				x=Rand(GadgetWidth(can))
				y=Rand(GadgetHeight(can))
				r = getcolormapcolor(x,y,2)
				;
				Color r,r,r
				;
				;
				;
				Plot x-Rand(16),y-Rand(16)
				;
			Next
			
	SetBuffer CanvasBuffer(can)
	;
End Function

Function makecolormap()
End Function
;
Function getColormapcolor(x#,y#,m#)
If x<0 Then Return
If y>GadgetHeight(can) Then Return
If x>GadgetWidth(can) Then Return
If x<0 Then Return
	a# = 100
	Local mx# = ( 640  / a )
	Local my# = ( 480  / a )
	;DebugLog x + ": " + x/mx
	;DebugLog y
	;DebugLog genmap(x / mx , y / my)
	r = (genmap(x / mx,y / my) + 34) * m#
	If r<0 Then r = 0
	If r>255 Then r=255
	Return r
End Function
;
Function colmapdisplay()
	For x=0 To GadgetWidth(can) Step 32
	For y=0 To GadgetHeight(can) Step 16
		n$ = getcolormapcolor(x,y,3)
		Color 255,255,n
		Text x,y,n$
	Next:Next
End Function
;
Function SubDivide(x1,y1,x2,y2); 
 If (x2-x1<2) And (y2-y1<2) Then Return; 
;  {If this is pointing at just on pixel, Exit because 
;   it doesn't need doing} 
 
  dist=(x2-x1+y2-y1); {Find distance between points.  Use when generating a random number} 
  hdist=dist / 2; 
 
  midx=(x1+x2) / 2; {Find Middle Point} 
  midy=(y1+y2) / 2; 
 
  c1=Genmap(x1,y1); {Get pixel colors of corners} 
  c2=Genmap(x2,y1); 
  c3=Genmap(x2,y2); 
  c4=Genmap(x1,y2); 
 
;  { If Not already defined, work out the midpoints of the corners of 
;   the rectangle by means of an average plus a random number. } 
  If Genmap(midx,y1)=0 Then Genmap(midx,y1)=((c1+c2+Rand(dist)-hdist) / 2); 
  If Genmap(midx,y2)=0 Then Genmap(midx,y2)=((c4+c3+Rand(dist)-hdist) / 2); 
  If Genmap(x1,midy)=0 Then Genmap(x1,midy)=((c1+c4+Rand(dist)-hdist) / 2); 
  If Genmap(x2,midy)=0 Then Genmap(x2,midy)=((c2+c3+Rand(dist)-hdist) / 2); 
 
;  { Work out the middle point... } 
  genmap(midx,midy) = ((c1+c2+c3+c4+Rand(dist)-hdist) / 4)
 ;  { Now divide this rectangle into 4, And call again For Each smaller 
;   rectangle } 
  SubDivide(x1,y1,midx,midy); 
  SubDivide(midx,y1,x2,midy); 
  SubDivide(x1,midy,midx,y2); 
  SubDivide(midx,midy,x2,y2); 
End Function

Function flashyoval_old(this.shape,dx,dy,w,h,offx,offy,dark#=2)
	If w=<0 Then Return
	If h=<0 Then Return
	Local brrb = CreateImage(w,h)
		SetBuffer ImageBuffer(brrb)
		Color 255,255,255
		Oval 0,0,w,h,True
		For y = 0 To h-1
		For x = 0 To w-1
		GetColor x,y
		If ColorRed()>0 Then 
			k = getcolormapcolor(x+offx,y+offy,dark)
			kk = coppermap(0,y)
			SetBuffer ImageBuffer(bmap\gridmap)
			GetColor x,y
			zr = ColorRed()/2
			zg = ColorGreen()/2
			zb = ColorBlue()/2
			SetBuffer ImageBuffer(brrb)
			ar = k/2
			ag = (k/2)+(kk/3)
			ab = k+kk/3
			;Color k/2,k/2+(kk/3),k+(kk/3)
			qr = zr+ar
			qg = zg+ag
			qb = zb+ab
			If qr>255 Then qr=255
			If qg>255 Then qg=255
			If qb>255 Then qb=255
			If qr<0 Then qr = 0
			If qg<0 Then qg = 0
			If qb<0 Then qb = 0
			Color qr,qg,qb
			Plot x,y
		End If
		Next:Next
		
		For i=0 To 5
			Color k/2+(i*5),k/2+(kk/3),k+(kk/3)

			Oval i,i,w-i*2,h-i*2,False
			Oval i+1,i,w-i*2,h-i*2,False
		Next
			Color (k/2+(i*5))+20,(k/2+(kk/3))+20,(k+(kk/3))+20
			Oval 0,0,w,h,False
 
		;SetBuffer CanvasBuffer(can)
		;SetBuffer ImageBuffer(bmap\tempmap)
		;DrawImage brrb,dx,dy
		this\bmap = CreateImage(this\w,this\h)
		SetBuffer ImageBuffer(this\bmap)				
		DrawImage brrb,0,0
		FreeImage brrb
End Function
;
Function flashyoval(this.shape,dx,dy,w,h,offx,offy,dark#=2)
	ms = MilliSecs()
	If w=<0 Then Return
	If h=<0 Then Return
	Local brrb = CreateImage(w,h)
		SetBuffer ImageBuffer(brrb)
		Color 255,255,255
		Oval 0,0,w,h,True
		For y = 0 To h-1
		For x = 0 To w-1
		;GetColor x,y
		LockBuffer ImageBuffer(brrb)
		 pff = ReadPixelFast(x,y)
		;DebugLog getr(pff)
		UnlockBuffer ImageBuffer(brrb)
		;If ColorRed()>0 Then 
		;DebugLog getr(pff)
		;DebugLog getr(pff)
		If getr(pff) > 0 Then 
		;End
			;DebugLog getr(pff)
			k = getcolormapcolor(x+offx,y+offy,dark)
			kk = coppermap(0,y)
			SetBuffer ImageBuffer(bmap\gridmap)
			LockBuffer ImageBuffer(bmap\gridmap)
			krr = ReadPixelFast(x,y)
			zr = getr(krr)/2
			zg = getg(krr)/2
			zb = getb(krr)/2
			UnlockBuffer ImageBuffer(bmap\gridmap)
			;GetColor x,y
			;zr = ColorRed()/2
			;zg = ColorGreen()/2
			;zb = ColorBlue()/2
			SetBuffer ImageBuffer(brrb)
			ar = k/2
			ag = (k/2)+(kk/3)
			ab = k+kk/3
			;Color k/2,k/2+(kk/3),k+(kk/3)
			qr = zr+ar
			qg = zg+ag
			qb = zb+ab
			If qr>255 Then qr=255
			If qg>255 Then qg=255
			If qb>255 Then qb=255
			If qr<0 Then qr = 0
			If qg<0 Then qg = 0
			If qb<0 Then qb = 0
			;			
			;
			LockBuffer ImageBuffer(brrb)
			WritePixelFast x,y,getrgb(qr,qg,qb)
			UnlockBuffer ImageBuffer(brrb)
			;
			;Color qr,qg,qb
			;Plot x,y
		End If
		Next:Next
		
		For i=0 To 5
			Color k/2+(i*5),k/2+(kk/3),k+(kk/3)

			Oval i,i,w-i*2,h-i*2,False
			Oval i+1,i,w-i*2,h-i*2,False
		Next
			Color (k/2+(i*5))+20,(k/2+(kk/3))+20,(k+(kk/3))+20
			Oval 0,0,w,h,False
 
		;SetBuffer CanvasBuffer(can)
		;SetBuffer ImageBuffer(bmap\tempmap)
		;DrawImage brrb,dx,dy
		this\bmap = CreateImage(this\w,this\h)
		SetBuffer ImageBuffer(this\bmap)				
		DrawImage brrb,0,0
		FreeImage brrb
		DebugLog MilliSecs()-ms
End Function

;
Function makecoppermap()
	a# = 255
	b# = 480
	c# = a/b
	For y=0 To 480-1
		r# = r# + c
		coppermap(0,y) = r
		;DebugLog r
	Next
End Function

Function blendcopypasteoval(xb,yb,w,h,div#);paste 1 , paste many
Local m = CreateImage(w,h)
Local mm = CreateImage(w,h)
Local aa#
Local bb#
Local cc#
Local cr#,cg#,cb#

MaskImage m,0,0,0
SetBuffer ImageBuffer(m)
Color 255,255,255
Oval 0,0,w,h,True
LockBuffer ImageBuffer(m)
For y=0 To h-1
For x=0 To w-1
pff = ReadPixelFast(x,y)
;GetColor x,y
If getr(pff) > 0 Then
;If ColorRed() > 0 Then 
;Color 0,0,0
WritePixelFast x,y,getrgb(0,0,0)
;Plot x,y
Else
;Color 255,255,255
;Plot x,y
WritePixelFast x,y,getrgb(255,255,255)
End If
Next:Next
UnlockBuffer ImageBuffer(m)
MaskImage mm,255,255,255
SetBuffer ImageBuffer(bmap\tempmap)
GrabImage mm,xb,yb
SetBuffer ImageBuffer(mm)
For y=0 To ImageHeight(mm);
For x=0 To ImageWidth(mm)
GetColor x,y
cr# = ColorRed() 
cg# = ColorGreen()
cb# = ColorBlue()
cr#=cr#*div#
cg#=cg#*div#
cb#=cb#*div#
If cr<0 Then cr=0
If cg<0 Then cg=0
If cb<0 Then cb=0
If cr>255 Then cr=255
If cg>255 Then cg=255
If cb>255 Then cb=255

;If aa<255
	Color cr,cg,cb
	Plot x,y
	
;End If

Next:Next
DrawImage m,0,0
;SetBuffer CanvasBuffer(can)
SetBuffer ImageBuffer(bmap\tempmap)
DrawImage mm,xb,yb
FreeImage m
FreeImage mm
End Function

Function flashyblendoval(this.shape)

;	For this.shape = Each shape	`
		If this\tp = 1 Then
		x=this\x
		y=this\y
		w=this\w
		h=this\h
		div = this\div
		flashyoval this,x,y,w,h,x,y,div
		End If
;	Next
	
	SetBuffer ImageBuffer(bmap\effectmap)
	For that.shape = Each shape
		Select that\tp
		Case 1
		DrawImage that\bmap,that\x,that\y
		Case 2
		drawcircrect that.shape
		End Select
	Next
	;bmap\effectmap = CopyImage(bmap\tempmap)

End Function


Function ovalmouserect()
	Color 255,255,0
	mx = editor\x1
	my = editor\y1
	mw = editor\x2 - editor\x1
	mh = editor\y2 - editor\y1
	;
	If editor\x2 < editor\ix1 Then mx = editor\x2 : mw = editor\x1 - editor\x2
	If editor\y2 < editor\iy1 Then my = editor\y2 : mh = editor\y1 - editor\y2
	;
	Oval mx,my,mw,mh,False
	Rect mx,my,mw,mh,False

End Function
Function setmouse()
	a = editor\x1
	b = editor\y1
	c = editor\x2
	d = editor\y2
	e = editor\w
	f = editor\h
	;
	
	;
End Function

;Standard functions for converting colour to RGB values, for WritePixelFast and ReadPixelFast
Function GetRGB(r,g,b)
	Return b Or (g Shl 8) Or (r Shl 16)
End Function

Function GetR(rgb)
    Return rgb Shr 16 And %11111111
End Function

Function GetG(rgb)
	Return rgb Shr 8 And %11111111
End Function

Function GetB(rgb)
	Return rgb And %11111111
End Function

Function shapecount()
	For this.shape = Each shape
		cnt=cnt+1
	Next
	Return cnt
End Function

Function drawcircrect(this.shape)

	Color 255,255,0
	mx = this\x
	my = this\y
	mw = this\w 
	mh = this\h
	;If editor\x2 < editor\ix1 Then mx = editor\x2 : mw = editor\x1 - editor\x2
	;If editor\y2 < editor\iy1 Then my = editor\y2 : mh = editor\y1 - editor\y2
	;
	Oval mx,my,mw,mh,False
	Rect mx,my,mw,mh,False

End Function
