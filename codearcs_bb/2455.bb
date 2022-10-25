; ID: 2455
; Author: Nate the Great
; Date: 2009-04-09 21:51:17
; Title: blitz + 3d realtime software renderer
; Description: just for fun software renderer I made for b+

Dim xval(20)
Dim yval(20)

Global tricount = 0
Global camzoom = 1.1
Dim tsfm_ary#(4000,1,1,1)
Global tsfm_cnt

Type tris
	Field x1#,y1#,z1#
	Field x2#,y2#,z2#
	Field x3#,y3#,z3#
	Field r,g,b
	Field dist#
	Field vdd
	Field force
	Field forceb
End Type

Function drawtriangle(x1#,y1#,z1#,x2#,y2#,z2#,x3#,y3#,z3#,r,g,b,force = 0,fback = 0)
	y1 = -y1
	y2 = -y2
	y3 = -y3
	tricount = tricount + 1
	t.tris = New tris
	t\x1# = x1#
	t\y1# = y1#
	t\z1# = z1#
	t\x2# = x2#
	t\y2# = y2#
	t\z2# = z2#
	t\x3# = x3#
	t\y3# = y3#
	t\z3# = z3#
	t\r = r
	t\g = g
	t\b = b
	t\force = force
	t\forceb = fback
	If tsfm_cnt > 0 Then
		t2 = Int(tsfm_cnt-1)
		For b = t2 To 0 Step -1
			Select tsfm_ary(b,0,0,0)
			Case 1
				roll# = ATan2(t\y1,t\x1)
				d# = Sqr(t\y1*t\y1 + t\x1*t\x1)
				ra# = tsfm_ary(b,0,0,1)
				roll# = roll# + ra#
				t\x1 = Cos(roll#)*d#
				t\y1 = Sin(roll#)*d#
				roll# = ATan2(t\y2,t\x2)
				d# = Sqr(t\y2*t\y2 + t\x2*t\x2)
				roll# = roll# + ra#
				t\x2 = Cos(roll#)*d#
				t\y2 = Sin(roll#)*d#
				roll# = ATan2(t\y3,t\x3)
				d# = Sqr(t\y3*t\y3 + t\x3*t\x3)
				roll# = roll# + ra#
				t\x3 = Cos(roll#)*d#
				t\y3 = Sin(roll#)*d#
				
				yaw# = ATan2(t\z1,t\x1)
				d# = Sqr(t\z1*t\z1 + t\x1*t\x1)
				ya# = tsfm_ary(b,0,1,0)
				yaw# = yaw# + ya#
				t\x1 = Cos(yaw#)*d#
				t\z1 = Sin(yaw#)*d#
				yaw# = ATan2(t\z2,t\x2)
				d# = Sqr(t\z2*t\z2 + t\x2*t\x2)
				yaw# = yaw# + ya#
				t\x2 = Cos(yaw#)*d#
				t\z2 = Sin(yaw#)*d#
				yaw# = ATan2(t\z3,t\x3)
				d# = Sqr(t\z3*t\z3 + t\x3*t\x3)
				yaw# = yaw# + ya#
				t\x3 = Cos(yaw#)*d#
				t\z3 = Sin(yaw#)*d#
				
				yaw# = ATan2(t\y1,t\z1)	;sorry yaw means pitch in this case :p
				d# = Sqr(t\y1*t\y1 + t\z1*t\z1)
				ya# = tsfm_ary(b,1,0,0)
				yaw# = yaw# + ya#
				t\z1 = Cos(yaw#)*d#
				t\y1 = Sin(yaw#)*d#
				yaw# = ATan2(t\y2,t\z2)
				d# = Sqr(t\y2*t\y2 + t\z2*t\z2)
				yaw# = yaw# + ya#
				t\z2 = Cos(yaw#)*d#
				t\y2 = Sin(yaw#)*d#
				yaw# = ATan2(t\y3,t\z3)
				d# = Sqr(t\y3*t\y3 + t\z3*t\z3)
				yaw# = yaw# + ya#
				t\z3 = Cos(yaw#)*d#
				t\y3 = Sin(yaw#)*d#
				
			Case 2
				t\x1 = t\x1 * tsfm_ary(b,1,0,0)
				t\y1 = t\y1 * tsfm_ary(b,0,1,0)
				t\z1 = t\z1 * tsfm_ary(b,0,0,1)
				t\x2 = t\x2 * tsfm_ary(b,1,0,0)
				t\y2 = t\y2 * tsfm_ary(b,0,1,0)
				t\z2 = t\z2 * tsfm_ary(b,0,0,1)
				t\x3 = t\x3 * tsfm_ary(b,1,0,0)
				t\y3 = t\y3 * tsfm_ary(b,0,1,0)
				t\z3 = t\z3 * tsfm_ary(b,0,0,1)
			Case 3
				t\x1 = t\x1 + tsfm_ary(b,1,0,0)
				t\y1 = t\y1 + tsfm_ary(b,0,1,0)
				t\z1 = t\z1 + tsfm_ary(b,0,0,1)
				t\x2 = t\x2 + tsfm_ary(b,1,0,0)
				t\y2 = t\y2 + tsfm_ary(b,0,1,0)
				t\z2 = t\z2 + tsfm_ary(b,0,0,1)
				t\x3 = t\x3 + tsfm_ary(b,1,0,0)
				t\y3 = t\y3 + tsfm_ary(b,0,1,0)
				t\z3 = t\z3 + tsfm_ary(b,0,0,1)
			End Select
		Next
	EndIf
End Function


Function Render()

maxdist# = 0

For t.tris = Each tris
	
	x# = t\x1# + t\x2# + t\x3#
	y# = t\y1# + t\y2# + t\y3#
	z# = t\z1# + t\z2# + t\z3#
	
	x# = x# / 3
	y# = y# / 3
	z# = z# / 3
	
	t\dist# = Sqr(x#^2 + y#^2 + z#^2)
	If t\forceb Then t\dist = 100000
	If t\dist# > maxdist# Then maxdist# = t\dist#
	
	t\vdd = False
Next


mindist# = maxdist#
mindisttmp# = 0
If tricount > 0 Then
	For a = 1 To tricount
		
		For t.tris = Each tris
			
			If t\vdd = False Then
				If T\dist# = mindist# Then
					
					Color t\r,t\g,t\b
					
					;Triangle(t\x1#,t\y1#,t\z1#,t\x2#,t\y2#,t\z2#,t\x3#,t\y3#,t\z3#,t\force)
					Triangle(t\x2#,t\y2#,t\z2#,t\x1#,t\y1#,t\z1#,t\x3#,t\y3#,t\z3#,t\force)
					
					T\vdd = True
					
				Else
					If T\dist# > mindisttmp# Then
						mindisttmp# = T\dist#
					EndIf
				EndIf
			EndIf
			
		Next
		
		mindist# = mindisttmp#
		mindisttmp# = 0
		
	Next
EndIf

cleartris()
cleartsfm()

End Function








Function Triangle(x0#,y0#,z0#,x1#,y1#,z1#,x2#,y2#,z2#,force)
	z0 = z0 + transformz
	z1 = z1 + transformz
	z2 = z2 + transformz
	If (z1*z2*z0 > .1 And z1 > 1.1 And z2 > 1.1 And z0 > 1.1) Or force Then
		If (Abs(x0) < z0 Or Abs(x1) < z1 Or Abs(x2) < z2) Or force Then
			gfxh# = GraphicsHeight()/2 * 1.6
			m0# = (1/((z0)^(1/camzoom)))
			x0 = x0 * m0*gfxh
			y0 = y0 * m0*gfxh
			m1# = (1/((z1)^(1/camzoom)))
			x1 = x1 * m1*gfxh
			y1 = y1 * m1*gfxh
			m2# = (1/((z2)^(1/camzoom)))
			x2 = x2 * m2*gfxh
			y2 = y2 * m2*gfxh
			
			gfxw# = GraphicsWidth()/2
			gfxh# = GraphicsHeight()/2

			xval(0)=x0+gfxw
			yval(0)=y0+gfxh
			xval(1)=x1+gfxw
			yval(1)=y1+gfxh
			xval(2)=x2+gfxw
			yval(2)=y2+gfxh
			
			poly(3)
		EndIf
	EndIf
End Function








Function poly(vcount)

; get clipping region
	width=GraphicsWidth()
	height=GraphicsHeight()
; find top verticy
	b=vcount-1
	y=yval(0)
	While c<>b
		c=c+1
		yy=yval(c)
		If yy<y y=yy d=c
	Wend
	c=d 
	t=c
; draw top to bottom
	While y<height
; get left gradient
		If y=yval(c)
			While y=yval(c)
				x0=xval(c) Shl 16
				c=c+1
				If c>b c=a
				If c=t Return
				If y>yval(c) Return
			Wend
			h=yval(c)-y
			g0=((xval(c) Shl 16)-x0)/h
		EndIf
; get right gradient
		If y=yval(d)
			While y=yval(d)
				x1=xval(d) Shl 16
				d=d-1
				If d<a d=b
				If y>yval(d) Return
			Wend
			h=yval(d)-y
			g1=((xval(d) Shl 16)-x1)/h
		EndIf
; calc horizontal span
		x=x1 Sar 16
		w=((x0 Sar 16)-x)+1
; draw down to next vert
		If (w>0 And y>-1 And x<width And x+w>0)
			If x<0 w=w+x x=0	;crop left
			If x+w>width w=width-x	;crop right
			Rect x,y,w,1
		EndIf
; next	
		x0=x0+g0
		x1=x1+g1
		y=y+1
	Wend
End Function

Function cleartris()
	For t.tris = Each tris
		Delete t.tris
	Next
End Function


Function rotate(pitch#,yaw#,roll#)

tsfm_ary(tsfm_cnt,0,0,0) = 1
tsfm_ary(tsfm_cnt,1,0,0) = pitch#
tsfm_ary(tsfm_cnt,0,1,0) = yaw#
tsfm_ary(tsfm_cnt,0,0,1) = roll#

tsfm_cnt = tsfm_cnt + 1

End Function

Function scale(x#,y#,z#)

tsfm_ary(tsfm_cnt,0,0,0) = 2
tsfm_ary(tsfm_cnt,1,0,0) = x
tsfm_ary(tsfm_cnt,0,1,0) = y
tsfm_ary(tsfm_cnt,0,0,1) = z

tsfm_cnt = tsfm_cnt + 1

End Function

Function translate(x#,y#,z#)

tsfm_ary(tsfm_cnt,0,0,0) = 3
tsfm_ary(tsfm_cnt,1,0,0) = x
tsfm_ary(tsfm_cnt,0,1,0) = y
tsfm_ary(tsfm_cnt,0,0,1) = z

tsfm_cnt = tsfm_cnt + 1

End Function

Function cleartsfm()

For i = 0 To tsfm_cnt - 1
	tsfm_ary(i,0,0,0) = 0
Next

tsfm_cnt = 0

End Function
