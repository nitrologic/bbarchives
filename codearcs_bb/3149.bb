; ID: 3149
; Author: Pakz
; Date: 2014-10-16 14:44:33
; Title: Topdown racing game ai
; Description: 6 Ai cars that drive around a track

; topdown racing game ai
Graphics 640,480,32,2
SetBuffer BackBuffer()
SeedRnd MilliSecs()
; number of cars
Const numcars = 6
Dim cx#(numcars) ; car x
Dim cy#(numcars) ; car y
Dim ctx(numcars) ; car destination x
Dim cty(numcars) ; car destination y
Dim ca#(numcars) ; current car angle
Dim cs#(numcars) ; car speed
Dim tp(numcars) ; next point location on map
Dim locktime(numcars)

Const numpoints = 5 ; race track destination points
Dim px(numpoints) ; pointx ; makes up the race track  
Dim py(numpoints) ; pointy

;make race track image

Global myim = CreateImage(GraphicsWidth(),GraphicsHeight())
setupracetrack()



SetBuffer BackBuffer()
While KeyDown(1) = False
	Cls
	updatecars
	DrawImage myim,0,0
	Color 255,0,0
	For i=0 To numpoints
		Rect px(i)-3,py(i)-3,6,6,False
	Next
	For i=0 To numcars
		Color 0,0,255
		Oval cx(i)-3,cy(i)-3,6,6,True
	Next
	Flip
Wend
End

Function updatecars()
	SetBuffer ImageBuffer(myim)

	For i=0 To numcars	
		
	
		If cs(i)< 2 Then cs(i) = cs(i) + .01 ; increase car speed
		
		; Get target angle to stear towards
		a = getangle(cx(i),cy(i),ctx(i),cty(i))
		a1 = ca(i)
		a2 = ca(i)
		c1 = 0
		c2 = 0
		exitloop = False
		While exitloop = False
			a1=a1+1
			c1=c1+1
			If a1 = a Then exitloop = True
			If a1=>360 Then a1=-1
		Wend
		exitloop = False
		While exitloop = False
			a2=a2-1
			c2=c2+1
			If a2 = a Then exitloop = True
			If a2=<0 Then a2=361
		Wend
		; turn towards the destination point
		If c1>c2 Then 			
			ca(i) = ca(i) - 3 
			If ca(i) < 0 Then ca(i)=361
		Else 
			ca(i) = ca(i) + 3		
			If ca(i)>360 Then ca(i) = -1
		End If
		
		; See if next position is inside other car and crash
		b1# = cx(i) + Sin(ca(i)) * cs(i)
		b2# = cy(i) + Cos(ca(i)) * cs(i)
		col = False
		For ii=0 To numcars
			If Not i = ii
				If RectsOverlap(b1,b2,3,3,cx(ii),cy(ii),3,3)
					col=True					
				End If
			End If
		Next
		If col = False ; if no crash then move
			cx(i) = cx(i) + Sin(ca(i)) * cs(i)
			cy(i) = cy(i) + Cos(ca(i)) * cs(i)			
			Else ; if crash then go real slow
			cs(i) = Rnd(0.1,0.2)
			cx(i)=cx(i)+Rand(-3,3)
			cy(i)=cy(i)+Rand(-3,3)
		End If

		; If reached next point
		If RectsOverlap(cx(i),cy(i),3,3,ctx(i),cty(i),3,3) = True Then 
			tp(i) = tp(i) + 1
			If tp(i) > numpoints Then tp(i) = 0
			ctx(i) = px(tp(i)) + Rand(-8,8)
			cty(i) = py(tp(i)) + Rand(-8,8)
		End If
		
		; 	if on dirt then slow down
		If Rand(10) = 1		
			GetColor cx(i),cy(i)
			If ColorRed() = 0 Then cs(i) = 0.1
		EndIf
	Next
	SetBuffer BackBuffer()
End Function

Function setupracetrack()
	px(0) = 100
	py(0) = 100
	px(1) = 320
	py(1) = 100
	px(2) = 500
	py(2) = 200
	px(3) = 500
	py(3) = 370
	px(4) = 320
	py(4) = 400
	px(5) = 100
	py(5) = 300

	For i=0 To numcars	
		cx(i) = px(0)+Rand(-16,16)
		cy(i) = py(0)+Rand(-16,16)
		ca(i) = getangle(cx(i),cy(i),px(1),py(1))
		tp(i) = 1
		ctx(i) = px(tp(i))+Rand(-8,8)
		cty(i) = py(tp(i))+Rand(-8,8)
		cs(i) = Rnd(0.4,0.7)
	Next
	
	SetBuffer ImageBuffer(myim)
	For i=0 To numpoints-1
		For y1=-32 To 32
		For x1=-32 To 32
			Line px(i)+x1,py(i)+y1,px(i+1)+x1,py(i+1)+y1
		Next
		Next
	Next
	For y1=-32 To 32
	For x1=-32 To 32
		Line px(numpoint)+x1,py(numpoints)+y1,px(0)+x1,py(0)+y1
	Next
	Next
End Function

Function getangle(x1,y1,x2,y2)
   	at = ATan2( x1 - x2 , y1 - y2 )
   	at = at - 180;
   	If at > 360
   		at = at - 360
   	ElseIf ( at < 0) 
    	at = at + 360
	End If
	Return at
End Function
