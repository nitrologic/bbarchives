; ID: 1469
; Author: Rob Farley
; Date: 2005-09-25 07:12:05
; Title: Procedeual Texture Generation
; Description: Create textures with no media

**** Proctexfunc.bb





; RGB Functions
Global gotr#=0
Global gotg#=0
Global gotb#=0
Global texsize=256

Function hText(x,y,t$,ax=False,ay=False)
	Color 0,0,0
	Text x-1,y,t$,ax,ay
	Text x+1,y,t$,ax,ay
	Text x,y-1,t$,ax,ay
	Text x,y+1,t$,ax,ay
	Color 255,255,255
	Text x,y,t$,ax,ay
End Function

Function otext(x,y,t$,ax=False,ay=False)
	htext x,y,t$,ax,ay
	htext 256,480,"Press Space to Continue",True
End Function

Function GetRGB(image_name,x,y)
; Gets the RGB components from an image.
; The imagebuffer needs to be locked as it does a read pixel fast.
; The components are put into the global varibles gotr, gotg and gotb
	argb=ReadPixelFast(x,y,ImageBuffer(image_name))
	gotr=(ARGB Shr 16) And $ff 
	gotg=(ARGB Shr 8) And $ff 
	gotb=ARGB And $ff
End Function

Function WriteRGB(image_name,x,y,red,green,blue)
; Writes a pixel to an image.
; The imagebuffer needs to be locked as it does a write pixel fast.
argb=(blue Or (green Shl 8) Or (red Shl 16) Or ($ff000000))
WritePixelFast x,y,argb,ImageBuffer(image_name)
End Function

Function Clip(x)
	If x>255 Then x=255
	If x<0 Then x=0
	Return x
End Function

Function cosine_interpolate#(a#, b#, x#) 
	f#=(1-Cos(x*180))/2 
	Return a*(1-f)+(b*f) 
End Function

Function add(source,output,s#=1)

	tmp = CreateImage(texsize,texsize)
	LockBuffer ImageBuffer(tmp)
	LockBuffer ImageBuffer(source)
	LockBuffer ImageBuffer(output)
	
	For x=0 To texsize-1
	For y=0 To texsize-1
	
	GetRGB(source,x,y)
	sr#=Float(gotr)*s
	sg#=Float(gotg)*s
	sb#=Float(gotb)*s
	
	GetRGB(output,x,y)
	r#=Clip(sr+gotr)
	g#=Clip(sg+gotg)
	b#=Clip(sb+gotb)
	
	WriteRGB(tmp,x,y,r,g,b)
	
	Next
	Next
	
	UnlockBuffer ImageBuffer(source)
	UnlockBuffer ImageBuffer(output)
	UnlockBuffer ImageBuffer(tmp)
	Return tmp
	FreeImage tmp

End Function

Function multiply(source,output,s#=1)

	tmp = CreateImage(texsize,texsize)
	LockBuffer ImageBuffer(tmp)
	LockBuffer ImageBuffer(source)
	LockBuffer ImageBuffer(output)
	
	For x=0 To texsize-1
	For y=0 To texsize-1
	
	GetRGB(source,x,y)
	sr#=1-((Float(255-gotr)/255)*s)
	sg#=1-((Float(255-gotg)/255)*s)
	sb#=1-((Float(255-gotb)/255)*s)
	
	GetRGB(output,x,y)
	r#=Float(gotr)*sr
	g#=Float(gotg)*sg
	b#=Float(gotb)*sb
	
	WriteRGB(tmp,x,y,r,g,b)
	
	Next
	Next
	
	UnlockBuffer ImageBuffer(source)
	UnlockBuffer ImageBuffer(output)
	UnlockBuffer ImageBuffer(tmp)
	Return tmp
	FreeImage tmp

End Function

Function multiplyx2(source,output,s#=1)

	tmp = CreateImage(texsize,texsize)
	LockBuffer ImageBuffer(tmp)
	LockBuffer ImageBuffer(source)
	LockBuffer ImageBuffer(output)
	
	For x=0 To texsize-1
	For y=0 To texsize-1
	
	GetRGB(source,x,y)
	sr#=2-((Float(255-gotr)/255)*2)
	sg#=2-((Float(255-gotg)/255)*2)
	sb#=2-((Float(255-gotb)/255)*2)
	
	sr=sr-1
	sg=sg-1
	sb=sb-1
	
	sr=sr*s
	sg=sg*s
	sb=sb*s
	
	sr=sr+1
	sg=sg+1
	sb=sb+1
	
	GetRGB(output,x,y)
	r#=Clip(Float(gotr)*sr)
	g#=Clip(Float(gotg)*sg)
	b#=Clip(Float(gotb)*sb)
	
	WriteRGB(tmp,x,y,r,g,b)
	
	Next
	Next
	
	UnlockBuffer ImageBuffer(source)
	UnlockBuffer ImageBuffer(output)
	UnlockBuffer ImageBuffer(tmp)
	Return tmp
	FreeImage tmp

End Function

Function colorlayer(r,g,b,noise)

	tmp = CreateImage(texsize,texsize)
	
	LockBuffer ImageBuffer(tmp)
	
	For x=0 To texsize-1
	For y=0 To texsize-1
	rad = Rand(-noise,noise)
	WriteRGB(tmp,x,y,Clip(r+Rad),Clip(g+Rad),Clip(b+Rad))
	
	Next
	Next
	
	UnlockBuffer ImageBuffer(tmp)
	
	Return tmp
	FreeImage tmp

End Function


Function cracks(qty,length)
	tmp = CreateImage(texsize,texsize)
	
	LockBuffer ImageBuffer(tmp)
	
	For x=0 To texsize-1
	For y=0 To texsize-1
	WriteRGB(tmp,x,y,128,128,128)
	Next
	Next
	
	; cracks
	For y=1 To qty
	x1#=Rand(0,texsize-1)
	y1#=Rand(0,texsize-1)
	d=Rand(0,360)
	l=Rand(5,50)
	For x=1 To Rand(1,length)

	GetRGB(tmp,x1#,y1#+1)
	gotr=Clip(gotr+(l/2))
	gotg=Clip(gotg+(l/2))
	gotb=Clip(gotb+(l/2))
	WriteRGB(tmp,x1,y1+1,gotr,gotg,gotb)
	GetRGB(tmp,x1#,y1#)
	gotr=Clip(gotr-l)
	gotg=Clip(gotg-l)
	gotb=Clip(gotb-l)
	WriteRGB(tmp,x1,y1,gotr,gotg,gotb)
	x1=(x1+Sin(d)) Mod texsize
	y1=(y1+Cos(d)) Mod texsize
	d=d+Rand(-15,15)
	l=l+Rand(-1,1)
	Next
	Next
		
	UnlockBuffer ImageBuffer(tmp)
	
	Return tmp
	FreeImage tmp
	
End Function


Function streak(qty,scale,noise,shake,rr,gg,bb,hori)

	tmp = CreateImage(texsize,texsize)
	
	LockBuffer ImageBuffer(tmp)
	
	For x=0 To texsize-1
	For y=0 To texsize-1
	WriteRGB(tmp,x,y,128,128,128)
	Next
	Next

	For y=1 To qty
		rad = Rand(-noise,noise)
		r=rr+rad
		g=gg+rad
		b=bb+rad
		sc=Rand(1,scale)
		yy=Rand(0,texsize-1)
		xx=Rand(0,texsize-1)
		For x=0 To 180/sc
			xxx=(xx+x) Mod texsize
			yyy=(yy+Rnd(-shake,shake)) Mod texsize
			GetRGB(tmp,xxx,yyy)
			gotr=Clip(gotr+(Sin(x*sc)*r))
			gotg=Clip(gotg+(Sin(x*sc)*g))
			gotb=Clip(gotb+(Sin(x*sc)*b))
			If hori
				WriteRGB(tmp,xxx,yyy,gotr,gotg,gotb)
				Else
				WriteRGB(tmp,yyy,xxx,gotr,gotg,gotb)
			EndIf
		Next
	Next
	
	UnlockBuffer ImageBuffer(tmp)
	
	Return tmp
	FreeImage tmp
	
End Function


Function hstreak(qty,scale,noise,shake,rr=0,gg=0,bb=0)
	Return streak(qty,scale,noise,shake,rr,gg,bb,True)
End Function

Function vstreak(qty,scale,noise,shake,rr=0,gg=0,bb=0)
	Return streak(qty,scale,noise,shake,rr,gg,bb,False)
End Function

Function baseshadow()
	
	tmp = CreateImage(texsize,texsize)
	
	LockBuffer ImageBuffer(tmp)
	
	For x=0 To texsize-1
	For y=0 To texsize-1
	WriteRGB(tmp,x,y,255,255,255)
	Next
	Next
	
	
	height = Rand(texsize/8,texsize/4)
	inith = height
	newheight = Rand(texsize/8,texsize/4)
	width# = Rand(texsize/50,texsize/4)
	pos#=0
	
	
	For x=0 To texsize-1
	
	l#=cosine_interpolate(height,newheight,pos/width)
	pos=pos+1
	If pos>=width
		height=newheight
		width# = Rand(texsize/50,texsize/4)
		newheight = newheight + Rand(-texsize/20,texsize/20)
		If newheight<texsize/8 Then newheight=newheight+(texsize/7)
		If x+width>texsize-1
			width=texsize-1-x
			newheight = inith
			EndIf
		pos=0
		EndIf
	
	For y=0 To l
	
	c = Clip((y/l)*255)
	
	WriteRGB(tmp,x,texsize-1-y,c,c,c)
	
	Next
	
	Next
	
	UnlockBuffer ImageBuffer(tmp)
	
	Return tmp
	
End Function


Function noise(qty,depth,r,g,b,bgr=0,bgg=0,bgb=0)

	tmp = CreateImage(texsize,texsize)
	
	LockBuffer ImageBuffer(tmp)
	
	If bgr>0 And bgg>0 And bgb>0
		For x=0 To texsize-1
		For y=0 To texsize-1
		WriteRGB(tmp,x,y,bgr,bgg,bgb)
		Next
		Next
	EndIf
		
	For x=1 To qty
	rad=Rand(-depth,depth)
	WriteRGB(tmp,Rand(0,texsize-1),Rand(0,texsize-1),r+rad,g+rad,b+rad)
	Next
	
	UnlockBuffer ImageBuffer(tmp)
	
	Return tmp
	
End Function
	
	














--------------------------------------------------------------------------------------------------------------------------


**** ProcTex.bb





Graphics 512,512,32,2

;SeedRnd MilliSecs()
font=LoadFont("Arial.tff",15)
SetFont font

Include "proctexfunc.bb"

texsize=256

SetBuffer BackBuffer()
AppTitle "Processing..."

t1 = colorlayer(126,143,158,10)
t1 = multiplyx2(cracks(50,500),t1,.5)
t1 = multiplyx2(hstreak(100,1,20,1,0,0,0),t1,.6)
t1 = multiplyx2(vstreak(50,20,30,1,-30,0,-30),t1,1)
t1 = multiply(baseshadow(),t1,.5)
DrawBlock t1,0,0

t1 = colorlayer(50,90,50,30)
t1 = add(noise(1000,30,80,70,50),t1)
DrawBlock t1,256,0

t1 = colorlayer(50,50,50,20)
t1 = multiplyx2(noise(5000,100,128,128,128,128,128,128),t1,1)
t1 = multiplyx2(cracks(100,1000),t1,2)
DrawBlock t1,0,256

t1 = colorlayer(200,150,100,10)
t1 = multiplyx2(hstreak(100,1,20,0,0,0,0),t1,.6)
t1 = multiplyx2(vstreak(100,1,20,0,0,0,0),t1,.6)
DrawBlock t1,256,256



AppTitle "Procedual Texture Creator"
oText 256,0,"Textures created with no media",True
Flip
WaitKey
AppTitle "Processing..."

t1 = colorlayer(126,143,158,10)
t1 = multiplyx2(cracks(50,500),t1,.5)
t1 = multiplyx2(hstreak(100,1,20,1,0,0,0),t1,.6)
t1 = multiplyx2(vstreak(50,20,30,1,-30,0,-30),t1,1)
DrawBlock t1,0,0
DrawBlock t1,256,0
t1 = multiply(baseshadow(),t1,.5)
DrawBlock t1,0,256
DrawBlock t1,256,256

AppTitle "Procedual Texture Creator"
oText 256,0,"Texutre created and stored, added to then stored again",True
Flip
WaitKey
AppTitle "Processing..."

t1 = colorlayer(50,50,50,20)
t1 = multiplyx2(noise(5000,100,128,128,128,128,128,128),t1,1)
t1 = multiplyx2(cracks(100,1000),t1,2)
DrawBlock t1,0,0
DrawBlock t1,256,0
DrawBlock t1,0,256
DrawBlock t1,256,256

AppTitle "Procedual Texture Creator"
oText 256,0,"Textures all 100% tileable",True
Flip
WaitKey
AppTitle "Processing..."

texsize=512
t1 = colorlayer(200,150,100,10)
t1 = multiplyx2(hstreak(100,1,20,0,0,0,0),t1,.6)
t1 = multiplyx2(vstreak(100,1,20,0,0,0,0),t1,.6)
DrawBlock t1,0,0

AppTitle "Procedual Texture Creator"
oText 256,0,"Any size texture can be created",True
Flip
WaitKey
AppTitle "Processing..."

texsize=256
t1 = Colorlayer(128,128,128,20)
DrawBlock t1,0,0
t1 = cracks(50,500)
DrawBlock t1,256,0
t1 = hstreak(100,1,20,1,0,0,0)
DrawBlock t1,0,256
t1 = noise(1000,30,80,70,50)
DrawBlock t1,256,256

AppTitle "Procedual Texture Creator"
oText 256,0,"FX include, base layer, cracks, streaks and noise",True
Flip
WaitKey
AppTitle "Processing..."

t1 = Colorlayer(128,128,128,20)
DrawBlock t1,0,0
t2 = cracks(50,500)
t2 = add(t2,t1,1)
DrawBlock t2,256,0
t2 = cracks(50,500)
t2 = multiply(t2,t1,1)
DrawBlock t2,0,256
t2 = cracks(50,500)
t2 = multiplyx2(t2,t1,1)
DrawBlock t2,256,256

AppTitle "Procedual Texture Creator"
oText 256,0,"With Add, Multiply and Multiply x n blend modes",True
Flip
WaitKey
AppTitle "Processing..."

t1 = colorlayer(128,128,128,20)
t2 = cracks(50,500)

n#=0
d#=.2

FlushKeys
Cls

Repeat

otext 256,0,"Variable blend power",True
t3 = multiplyx2(t2,t1,n)

DrawBlock t3,128,128
Flip

n=n+d
If n>=3 Or n<=0 Then d=-d

Until KeyHit(57)
