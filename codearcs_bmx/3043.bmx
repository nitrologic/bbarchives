; ID: 3043
; Author: Pineapple
; Date: 2013-03-22 10:18:50
; Title: Color Correction by Level Adjusting
; Description: Adjust colors by RGB channel and by luminosity

' 	--+-----------------------------------------------------------------------------------------+--
'	  |   This code was originally written by Sophie Kirschner (sophiek@pineapplemachine.com)   |  
' 	  | It is released as public domain. Please don't interpret that as liberty to claim credit |  
' 	  |   that isn't yours, or to sell this code when it could otherwise be obtained for free   |  
'	  |                because that would be a really shitty thing of you to do.                |
' 	--+-----------------------------------------------------------------------------------------+--

SuperStrict

Import brl.pixmap

' Needed for example code:
'Import brl.glmax2d
'Import brl.pngloader
'Import brl.polledinput
' Also required: an image file named "test.png"

Type colorcorrector
	Field minr#=0,midr#=.5,maxr#=1 ' red
	Field ming#=0,midg#=.5,maxg#=1 ' green
	Field minb#=0,midb#=.5,maxb#=1 ' blue
	Field minl#=0,midl#=.5,maxl#=1 ' luminosity
	Const rconst#=.30,gconst#=.59,bconst#=.11
	Method CorrectPixmap(pix:TPixmap)
		Assert pix,"Attempted to color-correct null pixmap."
		For Local i%=0 Until pix.width
		For Local j%=0 Until pix.height
			pix.WritePixel i,j,CorrectPixel(pix.ReadPixel(i,j))
		Next
		Next
	End Method
	Method GetCorrectedPixmap:TPixmap(pix:TPixmap)
		Assert pix,"Attempted to color-correct null pixmap."
		Local ret:TPixmap=CreatePixmap(pix.width,pix.height,pix.format)
		For Local i%=0 Until pix.width
		For Local j%=0 Until pix.height
			ret.WritePixel i,j,CorrectPixel(pix.ReadPixel(i,j))
		Next
		Next
		Return ret
	End Method
	Method CorrectPixel%(rgb%)
		Local r#=((rgb Shr 16) & $ff)/255#
		Local g#=((rgb Shr 8 ) & $ff)/255#
		Local b#=((rgb       ) & $ff)/255#
		CorrectColor(r,g,b)
		Local byter%=r*255,byteg%=g*255,byteb%=b*255
		Return (rgb&$ff000000)|(byter Shl 16)|(byteg Shl 8)|(byteb)
	End Method
	Method CorrectColor(r# Var,g# Var,b# Var)
		HandleChannel(r,minr,midr,maxr)
		HandleChannel(g,ming,midg,maxg)
		HandleChannel(b,minb,midb,maxb)
		Local ol#=r*rconst+g*gconst+b*bconst,l#=ol
		HandleChannel(l,minl,midl,maxl)
		Local ldiff#=l-ol
		r:+ldiff
		g:+ldiff
		b:+ldiff
		r=Min(1,Max(0,r))
		g=Min(1,Max(0,g))
		b=Min(1,Max(0,b))
	End Method
	Method HandleChannel(v# Var,minv#,midv#,maxv#)
		If v<.5 Then
			v=v*2
			v=v*(midv-minv)+minv
		ElseIf v>.5 Then
			v=(v-.5)*2
			v=v*(maxv-midv)+midv
		EndIf
	End Method
End Type











' Example code
Rem

Graphics 800,600
Local pix:TPixmap=LoadPixmap("test.png")
Local pixcorrected:TPixmap

Local cc:colorcorrector=New colorcorrector

Local rmin:slider=slider.Create(20,360)
Local rmid:slider=slider.Create(20,380)
Local rmax:slider=slider.Create(20,400)
Local gmin:slider=slider.Create(20,420)
Local gmid:slider=slider.Create(20,440)
Local gmax:slider=slider.Create(20,460)
Local bmin:slider=slider.Create(20,480)
Local bmid:slider=slider.Create(20,500)
Local bmax:slider=slider.Create(20,520)
Local lmin:slider=slider.Create(20,540)
Local lmid:slider=slider.Create(20,560)
Local lmax:slider=slider.Create(20,580)
rmax.sx=rmax.w
gmax.sx=gmax.w
bmax.sx=bmax.w
lmax.sx=lmax.w
rmid.sx=rmax.w/2
gmid.sx=gmax.w/2
bmid.sx=bmax.w/2
lmid.sx=lmax.w/2

Local frames%=0
Repeat
	If AppTerminate() Then End
	Cls
	If pixcorrected DrawPixmap pixcorrected,0,0 Else DrawPixmap pix,0,0
	
	SetColor 255,64,0
	rmin.handle;rmid.handle;rmax.handle
	SetColor 0,255,0
	gmin.handle;gmid.handle;gmax.handle
	SetColor 0,128,255
	bmin.handle;bmid.handle;bmax.handle
	SetColor 255,255,255
	lmin.handle;lmid.handle;lmax.handle
	Local rn#=Min(rmin.value(),rmax.value()),rx#=Max(rmin.value(),rmax.value())
	Local gn#=Min(gmin.value(),gmax.value()),gx#=Max(gmin.value(),gmax.value())
	Local bn#=Min(bmin.value(),bmax.value()),bx#=Max(bmin.value(),bmax.value())
	Local ln#=Min(lmin.value(),lmax.value()),lx#=Max(lmin.value(),lmax.value())
	cc.minr=rn;cc.maxr=rx
	cc.ming=gn;cc.maxg=gx
	cc.minb=bn;cc.maxb=bx
	cc.minl=ln;cc.maxl=lx
	cc.midr=rmid.value()
	cc.midg=gmid.value()
	cc.midb=bmid.value()
	cc.midl=lmid.value()
	
	frames:+1
	If frames Mod 100=0 Then
		pixcorrected=cc.GetCorrectedPixmap(pix)
		Cls
	EndIf
	Flip
Forever

Type slider
	Field x%,y%,w%=200,h%=10
	Field minv#=0,maxv#=1,sx%
	Method handle()
		'DrawText value(),x-60,y
		Local ly%=y+h/2;DrawLine x,ly,x+w,ly
		DrawLine sx+x,y,sx+x,y+h
		Local mx%=MouseX(),my%=MouseY(),lmb%=MouseDown(1)
		If mx>=x-10 And my>=y And mx<x+w+10 And my<y+h Then
			If lmb
				sx=mx-x
				If sx<0 Then sx=0
				If sx>w Then sx=w
			EndIf
		EndIf
	End Method
	Method value#()
		Return (sx/Float(w))*(maxv-minv)+minv
	End Method
	Function Create:slider(x%,y%)
		Local n:slider=New slider
		n.x=x;n.y=y
		Return n
	End Function
End Type

EndRem
