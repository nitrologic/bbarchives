; ID: 1258
; Author: Cygnus
; Date: 2005-01-10 07:37:42
; Title: Pole Position/Outrun Racetrack code (2d)
; Description: Draws a 2d track in perspective on screen

Global scx=1024,scy=768

Global pathsize=1024	 	'This is the paths TRUE length
Global multiplier#=50		'This is how much this path is interpolated. (pathsize is multiplied by this)
Global pathlength#=pathsize*multiplier 'This is the virtual paths length.

Global pathc2#[pathsize+1]
Global pathd2#[pathsize+1]
Global maxcars=500		'500 cars at one time on the track!?!?! lower this for less ;)
Global carx#[maxcars+1]	'Where on the track the car is (left-right. Values -1 to +1)
Global cary#[maxcars+1]	'Not used yet- Maybe add jumping?
Global carpos#[maxcars+1]	' where along the path each car is stored
Global drawcar[maxcars+1] 	'Flag for each car- does it get drawn?
Global drawx[maxcars+1]	'X coordinate for drawing car. Set when you set Drawcar[car]=1
Global drawy[maxcars+1]	'y coordinate for drawing car. Set when you set Drawcar[car]=1
Global drawsz#[maxcars+1]	'size for drawing car. Set when you set Drawcar[car]=1
Global carspeed#[maxcars+1]  'Speed of car. Best results are between 1 and 2. values 0-2 work good.

SetGraphicsDriver GLMax2DDriver()
Graphics scx,scy',32,60

'Position the cars randomly
For n=0 To maxcars
	carx#(n)=Rnd(-.8,.8)
	carspeed(n)=Rnd(.6,1.1)
	carpos(n)=Rand(pathlength)
Next

'Generate the level data using random info
c#=0
d#=0

targd#=Rand(-5,5)
targc#=Rand(-5,5)
For n=0 To pathlength
	If Rand(0,100)>99 Then targc#=Rand(-3,3);If Rand(0,10)>5 Then targc=0
	mc#=(c#-targc#)/10.0;If Abs(mc)>.1 Then mc=Sgn(mc)*.1
	c#=c#-mc
	If Rand(0,100)>90 Then targd#=Rand(-3,3);If Rand(0,10)>2 Then targd=0
	md#=(d#-targd#)/10.0;If Abs(md)>.1 Then md=Sgn(md)*.1
	d#=d#-md
	pathc2[n/multiplier]=c
	pathd2[n/multiplier]=d*.8
Next

pathpos#=0

Repeat

	For n=1 To maxcars
		If carx(n)<0 Then carpos(n)=carpos(n)+carspeed(n)
		If carx(n)>=0 Then carpos(n)=carpos(n)-carspeed(n)
		carpos(n)=carpos(n) Mod pathlength
		If carpos(n)<0 Then carpos(n)=carpos(n)+pathlength
		carpos(n)=carpos(n)drawcar(n)=0
	Next

	pathpos=(pathpos+1) Mod pathlength
	pathpos2#=pathpos
	Cls

	For DODRAW=0 To 1
		pos#=pathpos
		y#=scy
		x#=scx/2.0
		z#=scx/2.0
		d#=10
		D2#=10*120
		oy#=y
		miny#=scy
		Repeat
			ok=0
			d#=d#/(1.03*1.03)
			If Abs(D2)>2 Then D2=D2/(1.029*1.03)
				'D2='D*120
				x1=x-(pathc(pos))-d2
				x2=x+(pathc(pos))+d2
				dq=0':If Abs(d)>.1 Then dq=1
				If Abs(oy-y)>.01 Then dq=1
				If y>miny And oy<=miny Then
				SetColor 255,255,255
				DrawLine x1,y,x2,y
			EndIf
			miny=scy
			If y<=miny Then
				If dodraw=1 Then
'tarmac colouring
					If (Int(pos/7) Mod 2)=0 Then SetColor 120,120,120 Else SetColor 80,80,80		
'draw tarmac
					If dq=1 Then
						drawquad x1,y,x2,y,ox2,oy,ox1,oy
					EndIf		
'grass colouring
					If (Int(pos/4) Mod 2)=0 Then SetColor 0,255,0 Else SetColor 0,128,0
	
					If dq=1 Then
						Drawquad 0,y,x1,y,ox1,oy,0,oy
						Drawquad scx,y,x2,y,ox2,oy,scx,oy
					EndIf
			
					If (Int(pos/2) Mod 2)=0 Then SetColor 255,0,0 Else SetColor 255,255,255
			
					drawquad x1-d*8,y,x1+d*8,y,ox1+od#*8,oy,ox1-od*8,oy
					drawquad x2-d*8,y,x2+d*8,y,ox2+od*8,oy,ox2-od*8,oy
					
					If (Int(pos/9) Mod 2)=0 And dq=1 Then 
						SetColor 255,255,255
						Drawquad x-d,y,x+d,y,ox+od#,oy,ox-od#,oy
						'DrawLine x,y,ox,oy
					EndIf
					
					SetColor 255,255,255

					For n=1 To maxcars
'set flag and store where toDraw the cars!?					
						If Int(carpos[n])=pos Then drawcar(n)=1;drawx(n)=x+carx(n)*(d2);drawy(n)=y;drawsz#(n)=d*10	
					Next
			
				EndIf
				ox1=x1
				ox2=x2
				
				ox=x
				oy=y
				If Floor(oy)=Floor(y) Then oy=y+1
				od#=d#		
			EndIf
				
			If y<miny Then miny=y
'Change X value to make the tracks corners work properly
			
			x=x+Float(pathc(pos)*(40.0-d)/5.0)
			x=x-Float(pathc(pathpos2)*(40.0-d#)/5.0)
			
'Change y value to make the tracks corners work properly and give perspective
			y=y+pathd(pos)*d#
			y=y-pathd(pathos2)*d
			y=y-d*1.5
			If d<.001  Then ok=1  'set the OK flag if weve drawn enough track.
			pos=(pos+1) Mod pathlength

		Until ok=1 Or KeyDown(key_escape)

		If dodraw=0 Then SetColor 70,50,200;DrawRect 0,0,scx,miny-1 'draw sky
	Next
		
	For n=1 To maxcars
		If drawcar(n)=1 Then
			DrawRect drawx(n)-drawsz(n)/2.0,drawy(n)-drawsz(n)/2.0,drawsz(n),drawsz(n)/2.0
		EndIf 
	Next
	
	DrawText pathpos,10,10
	Flip
Until KeyDown(key_escape)

End


Function drawtri(x1#,y1#,x2#,y2#,x3#,y3#)
	Local poly#[]=[Float(x1),Float(y2),Float(x2),Float(y2),Float(x3),Float(y3)]
	If x1<0 Then x1=0
	If x2<0 Then x2=0
	If x3<0 Then x3=0
	If y1<0 Then y1=0
	If y2<0 Then y2=0
	If y3<0 Then y3=0
	If x1>scx Then x1=scx
	If x2>scx Then x2=scx
	If x3>scx Then x3=scx
	If y1>scy Then y1=scy
	If y2>scy Then y2=scy
	If y3>scy Then y3=scy
	DrawPoly poly
End Function

Function drawquad(x1#,y1#,x2#,y2#,x3#,y3#,x4#,y4#)
	If x1<0 Then x1=0
	If x2<0 Then x2=0
	If x3<0 Then x3=0
	If x4<0 Then x4=0
	If y1<0 Then y1=0
	If y2<0 Then y2=0
	If y3<0 Then y3=0
	If y4<0 Then y4=0
	
	If x1>scx Then x1=scx
	If x2>scx Then x2=scx
	If x3>scx Then x3=scx
	If x4>scx Then x4=scx
	If y1>scy Then y1=scy
	If y2>scy Then y2=scy
	If y3>scy Then y3=scy
	If y4>scy Then y4=scy
	Local poly#[]=[Float(x1),Float(y2),Float(x2),Float(y2),Float(x3),Float(y3),Float(x4),Float(y4)]
	DrawPoly poly
End Function

Function pathc#(pos#)
	pos=pos/multiplier
	pos=pos Mod pathsize
	Local pos2=(pos+1) Mod pathsize
	val1#=pathc2#(Floor(pos))
	val2#=pathc2#(Floor(pos2))
	val3#=pos#-Floor(pos#)
	Return interpolate#(val1#,val2#,val3#)
End Function

Function pathd#(pos#)
	pos=pos/multiplier
	pos=pos Mod pathsize
	Local pos2=(pos+1) Mod pathsize
	val1#=pathd2#(Floor(pos))
	val2#=pathd2#(Floor(pos2))
	val3#=pos#-Floor(pos#)
	'val4#=val1#+(val2#-val1#)*val3#
	Return interpolate#(val1#,val2#,val3#)
End Function

Function interpolate#(val1#,val2#,decimal#)
	Return Float(val1)+Float(Float(val2)-Float(val1))*decimal
End Function
