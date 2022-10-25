; ID: 711
; Author: PRJ
; Date: 2003-06-04 02:43:36
; Title: Kumppa
; Description: The Kumppa effect, as seen on the Amiga 500, twirling, zoomin, color fractal thingy...

;
; Kumppa v1.0
;
; Ported by Paul Rene Jørgensen, paulrene@pilen33.org
;
Global screenWidth, screenHeight
Global middleX, middleY
Global stateX, stateY
Global rotSizeX, rotSizeY
Global rx, ry
Dim xRotations(1)
Dim xRotTable(1)
Dim yRotations(1)
Dim yRotTable(1)
Dim chks(1)
Dim rotateX(1)
Dim rotatey(1)

screenWidth = 800
screenHeight = 600

middleX = screenWidth/2
middleY = screenHeight/2

Graphics screenWidth,screenHeight

initializeRotations(0.05,0.05)

While Not KeyHit(1)
	SetBuffer BackBuffer()

	r=Sin(a+0)*255
	g=Sin(a+90)*255
	b=Sin(a+180)*255
	Color r,g,b
	If(Rand(0,10)<2) Then Color 0,0,0
	a=a+4 : If a>=360 Then a=a-360
	If Not KeyDown(15)
		Line middleX,middleY,middleX+Cos(a)*4,middleY+Sin(a)*4
	EndIf
	
	While KeyDown(57) : Delay 250 : Wend
	
	rotate()
	CopyRect 0,0,screenWidth,screenHeight,0,0,FrontBuffer(),BackBuffer()
	VWait
	
Wend


Function rotate()
	rx=xRotTable(stateX+1)-xRotTable(stateX)
	ry=yRotTable(stateY+1)-yRotTable(stateY)
	For x=0 To rx
		If x<>0 Then rotateX(x)=middleX-1-xRotations(xRotTable(stateX+1)-x) Else rotateX(x)=0
	Next
	For x=0 To rx
		If x=rx Then rotateX(x+rx+1)=screenWidth-1 Else rotateX(x+rx+1)=middleX+xRotations(xRotTable(stateX)+x)
	Next
	For y=0 To ry
		If y<>0 Then rotateY(y)=middleY-1-yRotations(yRotTable(stateY+1)-y) Else rotateY(y)=0
	Next
	For y=0 To ry
		If y=ry Then rotateY(y+ry+1)=screenHeight-1 Else rotateY(y+ry+1)=middleY+yRotations(yRotTable(stateY)+y)
	Next

	If rx>ry Then x=rx Else x=ry
	
	For dy=0 To ((x+1)*2)-1
		For dx=0 To ((x+1)*2)-1
			If rx>ry Then y=ry-rx Else y=0
			If ((dy+y)>=0 And dy<((ry+1)*2) And dx<((rx+1)*2))
				If ((dy+y+dx)<=(ry+rx) And (dy+y-dx)<=(ry-rx))
					palaRotate((rx*2)+1-dx,dy+y)
					palaRotate(dx,(ry*2)+1-dy-y)
				EndIf
			EndIf
			If ry>rx Then y=rx-ry Else y=0
			If ((dy+y)>=0 And dx<((ry+1)*2) And dy<((rx+1)*2))
				If ((dy+y+dx)<=(ry+rx) And (dx-dy-y)>=(ry-rx))
					palaRotate(dy+y,dx)
					palaRotate((rx*2)+1-dy-y,(ry*2)+1-dx)
				EndIf
			EndIf
		Next
	Next
	stateX=stateX+1
	If stateX=rotSizeX Then stateX=0
	stateY=stateY+1
	If stateY=rotSizeY Then stateY=0
End Function

Function palaRotate(x,y)
	SetBuffer FrontBuffer()
	ax=rotateX(x)
	ay=rotateY(y)
	bx=rotateX(x+1)+2
	by=rotateY(y+1)+2
	cx=rotateX(x)-(y-ry)+x-rx
	cy=rotateY(y)+(x-rx)+y-ry
	If cx<0
		ax=ax-cx
		cx=0
	EndIf
	If cy<0
		ay=ay-cy
		cy=0
	EndIf
	If (cx+bx-ax)>screenWidth Then bx=ax-cx+screenWidth
	If (cy+by-ay)>screenHeight Then by=ay-cy+screenHeight
	If (ax<bx And ay<by)
		CopyRect ax,ay,bx-ax,by-ay,cx,cy,BackBuffer(),FrontBuffer()
	EndIf
End Function


Function initializeRotations(xSpeed#, ySpeed#)
	a=0:b=0:c=0:f=0:g=0:j=0:k=0:l=0:maxi=0
	m#=0.0:om#=0.0:ok#=0.0:d#=0.0:ix#=0.0:iy#=0.0


	rotSizeX = (2.0/xSpeed#)+1
	ix = (Float middleX+1.0) / Float rotSizeX

	rotSizeY = (2.0/ySpeed#)+1
	iy = (Float middleY+1.0) / Float rotSizeY

	Dim xRotations(middleX+2)
	Dim xRotTable(rotSizeX+1)
	Dim yRotations(middleY+2)
	Dim yRotTable(rotSizeY+1)
	
	m = middleX
	If(middleY>middleX) Then m = middleY
	Dim chks(m)
	
	maxi = 0
	c = 0
	d = 0
	g = 0
	For a=0 To (middleX-1)
		chks(a) = True
	Next
	
	For a=0 To (rotSizeX-1)
		xRotTable(a)=c
		f=Int (d+ix#)-g
		g=g+f
		If g>middleX
			f=f-(g-middleX)
			g=middleX
		EndIf
		For b=0 To (f-1)
			m=0
			For j=0 To (middleX-1)
				If chks(j)=True
					om=0
					ok=1
					l=0
					While ((j+l)<middleX And (om+12*ok)>m)
						If (j-l)>=0
							If chks(j-l)
								om=om+ok
							EndIf
						ElseIf chks(l-j)
							om=om+ok
						EndIf
						If chks(j+l) Then om=om+ok
						ok=ok/1.5
						l=l+1
					Wend
					If om>=m
						k=j
						m=om
					EndIf
				EndIf
			Next
			chks(k)=False
			l=c
			While (l>=xRotTable(a))
				If l<>xRotTable(a) Then xRotations(l)=xRotations(l-1)
				If (k>xRotations(l) Or l=xRotTable(a))
					xRotations(l)=k
					c=c+1
					l=xRotTable(a)
				EndIf
				l=l-1
			Wend
		Next
		d=d+ix
		If maxi<(c-xRotTable(a)) Then maxi=c-xRotTable(a)
	Next
	xRotTable(a)=c
	Dim rotateX((maxi+2)*2)

	maxi=0
	c=0
	d=0
	g=0
	For a=0 To (middleY-1)
		chks(a) = True
	Next
	
	For a=0 To (rotSizeY-1)
		yRotTable(a)=c
		f=(d+iy)-g
		g=g+f
		If g>middleY
			f=f-(g-middleY)
			g=middleY
		EndIf
		For b=0 To (f-1)
			m=0
			For j=0 To (middleY-1)
				If chks(j) Then
					om=0
					ok=1
					l=0
					While ((j+l)<middleY And (om+12*ok)>m)
						If (j-l)>=0
							If chks(j-l)
								om=om+ok
							EndIf
						ElseIf chks(l-j)
							om=om+ok
						EndIf
						If chks(j+l) Then om=om+ok
						ok=ok/1.5
						l=l+1
					Wend
					If om>=m
						k=j
						m=om
					EndIf
				EndIf
			Next
			chks(k)=False
			l=c
			While (l>=yRotTable(a))
				If l<>yRotTable(a) Then yRotations(l)=yRotations(l-1)
				If (k>yRotations(l) Or l=yRotTable(a))
					yRotations(l)=k
					c=c+1
					l=yRotTable(a)
				EndIf
				l=l-1
			Wend
		Next
		d=d+iy
		If maxi<(c-yRotTable(a)) Then maxi=c-yRotTable(a)
	Next
	yRotTable(a)=c
	Dim rotateY((maxi+2)*2)

	Dim chks(1) ; Free mem
End Function
