; ID: 2274
; Author: JoshK
; Date: 2008-06-17 04:34:32
; Title: PixmapFilterDot3
; Description: Convert a height image to a normal map

Strict

Import brl.pixmap


Function PixmapFilterDot3:TPixmap(pixmap:TPixmap,height#=1.0,parallax=0)
	Local lp,rp,tp,bp
	Local bumpmap:TPixmap
	Local vx#,vy#,vz#,m#
	Local tl#,bl#,ml#,tr#,mr#,br#,mm#,bm#,tm#
	Local isq2#,sum#,al#,ar#,at#,ab#,r,g,b,a,x,y
	Local format
	
	If parallax
		bumpmap=CreatePixmap(pixmap.width,pixmap.height,PF_RGBA8888)
	Else
		bumpmap=CreatePixmap(pixmap.width,pixmap.height,PF_RGB888)	
	EndIf
	
	For x=0 To pixmap.width-1
		For y=0 To pixmap.height-1
			
			lp=x-1
			rp=x+1
			tp=y-1
			bp=y+1
			If lp<0 lp=pixmap.width-1
			If lp>pixmap.width-1 lp=0
			If rp<0 rp=pixmap.width-1
			If rp>pixmap.width-1 rp=0
			If tp<0 tp=pixmap.width-1
			If tp>pixmap.height-1 tp=0
			If bp<0 bp=pixmap.width-1
			If bp>pixmap.height-1 bp=0
			
			tl#=ReadHeight(pixmap,x-1,y-1)
			tm#=ReadHeight(pixmap,x,y-1)
			tr#=ReadHeight(pixmap,x+1,y-1)
			ml#=ReadHeight(pixmap,x-1,y)
			mm#=ReadHeight(pixmap,x,y)
			mr#=ReadHeight(pixmap,x+1,y)
			bl#=ReadHeight(pixmap,x-1,y+1)
			bm#=ReadHeight(pixmap,x,y+1)
			br#=ReadHeight(pixmap,x+1,y+1)
			
			vx#=0.0
			vy#=0.0
			vz#=1.0
			
			isq2#=1.0/Sqr(2.0)
			sum#=1.0+isq2+isq2
			
			al#=(tl*isq2+ml+bl*isq2)/sum
			ar#=(tr*isq2+mr+br*isq2)/sum
			at#=(tl*isq2+tm+tr*isq2)/sum
			ab#=(bl*isq2+bm+br*isq2)/sum			

			vx#=(al-ar)/255.0
			vy#=(at-ab)/255.0
			m=Max(0,vx*vx+vy*vy)
			m=Min(m,1.0)

			vz=Sqr(1.0-m) 
			
			If height<>0.0
				vz:/height
				m#=Sqr(vx*vx+vy*vy+vz*vz)
				vx:/m
				vy:/m
				vz:/m
			EndIf
			
			r=vx*127.5+127.5+0.5'0.5 added for rounding
			g=vy*127.5+127.5+0.5
			b=vz*127.5+127.5+0.5
			a=mm'store height value in alpha channel
			
			r=Min(r,255)
			r=Max(r,0)
			g=Min(g,255)
			g=Max(g,0)
			b=Min(b,255)
			b=Max(b,0)
			
			WritePixel bumpmap,x,y,b+(g Shl 8)+(r Shl 16)+(a Shl 24)
		Next
	Next
	Return bumpmap
EndFunction

Private

Function ReadHeight:Float(pixmap:TPixmap,x,y)
	Local hue,r,g,b
	While x<0
		x:+pixmap.width
	Wend
	While x>pixmap.width-1
		x:-pixmap.width
	Wend
	While y<0
		y:+pixmap.height
	Wend
	While y>pixmap.height-1
		y:-pixmap.height
	Wend
	hue=ReadPixel(pixmap,x,y)
	r=(hue & $00FF0000) Shr 16
	g=(hue & $0000FF00) Shr 8
	b=(hue & $000000FF)
	Return Float(r)*0.3+Float(g)*0.59+Float(b)*0.11
EndFunction

Public
