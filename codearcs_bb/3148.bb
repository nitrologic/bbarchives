; ID: 3148
; Author: Pakz
; Date: 2014-10-16 11:56:15
; Title: Moving images between 2 points
; Description: Move images between two points (Getangle x1,y1,x2,y2 function)

Graphics 640,480,32,1
SetBuffer BackBuffer()
SeedRnd MilliSecs()

; Set high if you want to test your computer
Const numships = 24

Dim shipactive(numships)
Dim shipaction(numships)
Dim shipdelay(numships)
Dim shipenergy(numships)
Dim shipw(numships)
Dim shiph(numships)
Dim shipwt(numships)
Dim shipht(numships)
Dim shipx#(numships)
Dim shipy#(numships)
Dim shipangle(numships)
Dim shipcurrentangle(numships)
Dim shipdestx#(numships)
Dim shipdesty#(numships)
Dim shipdestangle(numships)
setupships()
Global lx1,ly1,lx2,ly2

While KeyDown(1) = False
	Cls
	updateships	
	updatelaser()
	drawships
	Color 255,255,255
	Line lx1,ly1,lx2,ly2
	Color 255,255,255
	Text 62,GraphicsHeight()-16,"Blitz Basic - Press the mouse on a oval and it will dissapear."
	Flip
Wend
End


Function updatelaser()
	bx = GraphicsWidth()/2
	by = GraphicsHeight()-16
	d2 = 1000
	For i=0 To numships
		d = Abs(shipx(i)-bx)+Abs(shipy(i)-by)
		If d < d2 Then
			d2 = d
			ii = i
			lx = shipx(i)
			ly = shipy(i)
		End If
	Next
	shipenergy(ii) = shipenergy(ii) - 1
	If shipenergy(ii) < 0
		shipenergy(ii) = 50+Rand(25)
		shipx(ii) = 0
		shipy(ii) = 0
		shipdestx(ii) = GraphicsWidth()
		shipdesty(ii) = GraphicsHeight()
		shipdestangle(ii) = Rand(0,359)		
	End If
	lx1 = bx
	ly1 = by
	lx2 = lx
	ly2 = ly
End Function

Function drawships()
	Color 255,255,0
	For i=0 To numships
		If shipactive(i) = 0
		Oval shipx(i)-shipw(i)/2,shipy(i)-shipw(i)/2,shipw(i),shipw(i),True
		Oval shipdestx(i)-3,shipdesty(i)-3,6,6,False
		End If
	Next
End Function

Function updateships()
	For i=0 To numships
		;
		;
		If shipw(i) = 2 And Rand(0,10) = 1 Then shipaction(i) = Rand(0,1)

		If shipaction(i) = 1 And shipactive(i) = 0
		If shipw(i) =< shipwt(i) Then shipw(i) = shipw(i) + 1 Else shipw(i) = shipw(i) -1
		If RectsOverlap(shipw(i),shiph(i),1,1,shipwt(i),shipht(i),1,1)
			If shipw(i) = 1 Then shipwt(i) = 32
			If shipw(i) => 15 Then shipwt(i) = 1
		End If

		If shipdelay(i) < MilliSecs()
		For ii=0 To numships		
			If i<>ii			
			d = Abs(shipx(ii)-shipx(i))+Abs(shipy(ii)-shipy(i))
			If d< 32 Then
				shipx(i) = 0
				shipy(i) = 0
				shipdelay(i) = MilliSecs()+7000
				shipdestx(i) = Rand(GraphicsWidth())
				shipdesty(i) = Rand(GraphicsHeight())
				shipdestangle(i) = Rand(0,359)
			EndIf
			d2 = Abs(shipx(ii)-MouseX())+Abs(shipy(ii)-MouseY())
			If d2<10 And MouseDown(1) = True
				shipx(ii) = 0
				shipy(ii) = 0
				shipdelay(ii) = MilliSecs()+7000
				shipdestx(ii) = Rand(GraphicsWidth())
				shipdesty(ii) = Rand(GraphicsHeight())
				shipdestangle(i) = Rand(0,359)
			EndIf
			
			End If			
		Next
		End If
		
		If RectsOverlap(shipx(i),shipy(i),6,6,shipdestx(i),shipdesty(i),6,6)
			shipdestx(i) = GraphicsWidth()/2+Rand(-(GraphicsWidth()/2),GraphicsWidth()/2)
			shipdesty(i) = GraphicsHeight()/2+Rand(-(GraphicsHeight()/2),GraphicsHeight()/2)
			shipdestangle(i) = Rand(0,359)
		End If
		ang = getangle(shipx(i),shipy(i),shipdestx(i),shipdesty(i))
		a = False
		c = shipcurrentangle(i)
		If ang<c Then a = True Else a = False
		If a = True Then shipcurrentangle(i) = shipcurrentangle(i) - 4
		If a = False Then shipcurrentangle(i) = shipcurrentangle(i) + 4
		
		shipx(i) = shipx(i) + Sin(shipcurrentangle(i)) * 1
		shipy(i) = shipy(i) + Cos(shipcurrentangle(i)) * 1
		shipdestx(i) = shipdestx(i) + Sin(shipdestangle(i)) * .2
		shipdesty(i) = shipdesty(i) + Cos(shipdestangle(i)) * .2
		End If
	Next
End Function

Function setupships()
	For i=0 To numships
		shipaction(i) = 1
		shipenergy(i) = 50+Rand(25)
		shipx(i) = 0
		shipy(i) = 0
		shipdelay(i) = 5000
		shipdestx(i) = GraphicsWidth()/2+Rand(-(GraphicsWidth()/2),GraphicsWidth()/2)
		shipdesty(i) = GraphicsHeight()/2+Rand(-(GraphicsHeight()/2),GraphicsHeight()/2)
		shipcurrentangle(i) = Rand(0,359)
		shipdestangle(i) = Rand(0,359)
		shipw(i) = Rand(3,16)
		shipwt(i) = 16
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
