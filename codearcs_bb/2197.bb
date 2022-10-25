; ID: 2197
; Author: Nebula
; Date: 2008-01-20 12:38:32
; Title: Top down adventure
; Description: G=grenade ; cursor movement

;
; Commando like fixed screen
;
; G grenade ; cursors movement ; 
;
; Hints : Underneath the sprite - Block , Above allow movement
;            Underneath the sprite - player = layer above, above - player layer below
;Include "f13sprites.bb"
AppTitle "2D layered game Engine - By Crom Design"
Graphics 640,480,32,2
SetBuffer BackBuffer()
;

Dim imlayer(3)
imlayer(1) = CreateImage(GraphicsWidth(),GraphicsHeight(),1)
imlayer(2) = CreateImage(GraphicsWidth(),GraphicsHeight(),1)
imlayer(3) = CreateImage(GraphicsWidth(),GraphicsHeight(),1)

;
;
Const mapgrass = 0
Const mapstone = 1
Const mappath = 2
Const maptree = 3
Const mapfence = 4
;
Const dir_left = 0
Const dir_right = 1
Const dir_up = 2
Const dir_down = 3
;
Const state_pauze = 9
Const state_findnextstate = 0
Const state_walkleft = 1
Const state_walkright = 2

Type aisoldiers
	Field x#,y#,mx#,my#,gx#,gy#,sx,sy,stilex,stiley,dir
	Field dist
	Field state,firestate,nextstate,laststate
	Field substate,subtimer
	Field ddir
	Field pauzetimer,movedelaytimer
End Type
Type weapons
	Field stilex,stiley
	Field x#,y#,mx#,my#,gx#,gy#,sx,sy
	Field dettimer,detflag
End Type
Type explosions
	Field x#,y#,mx#,my#,gx#,gy#,sx,sy,w,h
End Type
Type ai
	Field x#,y#,mx#,my#
	Field w,h,id,name$
End Type
Type largesprites
	Field x#,y#,mx#,my#
	Field w,h,id,name$
End Type
Type player
	Field cl ; currentlevel
	Field x#,y#,mx#,my#
	Field maploc	
	Field energy
	Field dir
	Field score
	Field item1;numslots
	Field item2;
	Field item3;
	Field item4;
	Field item5;
End Type

Dim zmatrix(8,8) ; area below player

Type localval
	Field x,y,id,l1_update,l2_update,l3_update,playerlayer
	Field ox,oy
End Type
l.Localval = New localval
l\playerlayer = 4

Dim map(255,30,30)
Dim aidmap(30,30)
Dim sprites32(15,15,15) ; larger stuff (larger things, trucks, planks)
Dim sprites16(15,7,7) ; larger stuff (trees ect)
Dim sprites8(16,7,7) ; smaller stuff (fences ect)
Dim spritesjunk(16,7,7) ; background stuff on the ground
Dim ai_sprites(16,15,15)
Dim large_sprites(16,32,32)
Dim treesprites(15,15,15)
Dim mapparts(0,15,7,7) ; small map details
Dim aisoldiersprites(15,15,15)
;
Dim coastlinetable(3,30)
Restore coastlinedat : For i=0 To 29 : Read a : coastlinetable(0,i)=a :Next
;
Global tw#,th# : tw = GraphicsWidth()/30 : th = GraphicsHeight()/35
Global mh,mw : mw = tw*(29) : mh = th*(34)
Global gamespeed = 4
Global minimapimage = CreateImage(GraphicsWidth()-mw+(tw*2),GraphicsHeight()-mh+(th*2));CreateImage(100,100)
;

readmap
readsprites
iniplayer
;
Global fps = 60
Global fpscounter = 0
Global fpstimer = 0
p.player = First player
p\cl = 0
ini_ai(p\cl)
drawmap
inilargesprites(p\cl)

newaisoldier(14,8)
newaisoldier(16,10)
newaisoldier(10,11)
newaisoldier(8,9)
newaisoldier(12,10)
newaisoldier(6,7)


While KeyDown(1) = False
	Cls
	;
	;
	ms = MilliSecs()
	DrawBlock imlayer(1),0,0
	
	If l\playerlayer = 1 Then drawplayer : DrawImage imlayer(2),0,0 : DrawImage imlayer(3),0,0
	If l\playerlayer = 2 Then DrawImage imlayer(2),0,0 : drawplayer  : DrawImage imlayer(3),0,0 
	If l\playerlayer = 3 Then DrawImage imlayer(2),0,0 : DrawImage imlayer(3),0,0 : drawplayer
	DrawImage minimapimage,GraphicsWidth() - ImageWidth(minimapimage)-tw,GraphicsHeight()-ImageHeight(minimapimage) - th
	;
	;drawlargesprite(0,134,224)
	;
	updateweapons
	drawweapons
	updateexplosions
	drawexplosions	
	;
	playercontrols
	gameupdate
	;
	If p\cl = 0 Then 	updateaisoldiers:drawaisoldiers	
	;
	layerplayer()
	drawlargesprites
	draw_ai(p\cl)
	switchlevel
	;Color 20,200,0:Rect px*tw,(py+y)*th,tw,th
	For y=0 To 30:	For x=0 To 30
	If aidmap(x,y) <> 0 Then Rect x*tw,y*th,tw,th,False
	Next:Next
	;
	Color 220,220,220 : Text GraphicsWidth() - 200 , 0 , fps
	Color 220,220,200 : Text GraphicsWidth() - 240,0, (ms-MilliSecs())
	Color 220,220,200 : Text GraphicsWidth() - 400,0,TotalVidMem()-AvailVidMem()
	Color 200,0,0 : Text GraphicsWidth() - 500 , 0 , l\playerlayer
	updatefps
	;
	Flip ;False
Wend
End
;
Function newaisoldier(x,y)
	ff.aisoldiers = New aisoldiers
	ff\stilex = x
	ff\stiley = y
	ff\x = ff\stilex*tw
	ff\y = ff\stiley*th
	ff\sx = ff\x
	ff\sy = ff\y
	ff\state = 0
	ff\dist = 4
End Function
Function drawaisoldiers()
	For s.aisoldiers = Each aisoldiers
		;Color 255,255,255		
		;Rect s\x+tw/2,(s\y)-th,tw,th*2,False
		drawaisoldiersprite(0,s\x,s\y)
	Next
End Function
Function updateaisoldiers()
;Type aisoldiers
;	Field x#,y#,mx#,my#,gx#,gy#,sx,sy,stilex,stiley,dir
;	field dist
;	field substate,subdelay
;	Field state,firestate,nextstate,laststate
;	Field pauzetimer,movedelaytimer
;	field ddir
;End Type
	For s.aisoldiers = Each aisoldiers
		If s\movedelaytimer < MilliSecs() Then
			s\movedelaytimer = MilliSecs() + 10
			;			
			;DebugLog s\state + " " +MilliSecs()
			Select s\state				
				Case state_pauze : If s\pauzetimer < MilliSecs() Then s\state = s\nextstate
				Case state_findnextstate	: s\state = state_walkleft
				Case state_walkleft 			: s\mx = -1	: s\dir = dir_left
				Case state_walkright 			: s\mx = 1 	: s\dir = dir_right
				Case 97 : If s\pauzetimer < MilliSecs() Then s\state = s\nextstate
				Case 98:s\x = s\x - 1.7 : If Rand(50) = 1 Then s\state = state_pauze
				Case 99:s\x = s\x + 1.7 : If Rand(50) = 1 Then s\state = state_pauze
			End Select
			Select s\substate
				Case 0 : If (s\state <> 98 And s\state<> 99) Then s\ddir = aicheckzone(s\x,s\y,s\state,s\dir)
				If s\ddir <> -1 Then
					For ss.aisoldiers = Each aisoldiers
						If ss\state <> 98 And ss\state <> 99 And ss\state <> 97
						ss\state = 97
						ss\nextstate = state_findnextstate
						ss\pauzetimer = MilliSecs()+50
						End If
					Next
				End If
				If s\ddir = dir_right Then s\state = 99
				If s\ddir = dir_left Then s\state = 98				
			End Select			
			;
			If s\mx <> 0 Then
				s\x = s\x + s\mx
				If s\mx < 0 And (s\sx - s\x) / tw > s\dist Then s\nextstate = state_walkright:s\laststate = s\state:s\state = state_pauze : s\pauzetimer = MilliSecs() + (1000+Rand(5000))
				If s\mx > 0 And -(s\sx - s\x) / tw > s\dist Then s\nextstate = state_walkleft :s\laststate = s\state:s\state = state_pauze : s\pauzetimer = MilliSecs() + (1000+Rand(5000))
				s\mx = 0
			End If
			If s\my <> 0 Then
				s\y = s\y + s\my
				s\my = 0
			End If
			;			
		End If
	Next
End Function
Function aicheckzone(x1,y1,stat,dir)
	x1 = x1 / tw : y1 = y1 / th
	For y=y1-2 To y1+2 : For x = x1-2 To x1+2
		If x=>0 And y=> 0 And x=<30-1 And y=<30-1
			;
			If (dir = dir_right And x>x1) Or (dir = dir_left And x=<x1) Or (stat = 97)
				If aidmap(x,y) = True Then
				If dir = dir_left Then Return dir_right
				If dir = dir_right Then  Return dir_left
				End If
			End If
			;
		End If
	Next:Next
	Return -1
End Function
;
Function updateexplosions()
	For e.explosions = Each explosions
		e\x = e\x + e\mx
		e\y = e\y + e\my
		e\mx = e\mx + e\gx
		e\my = e\my + e\gy
		If e\y > e\sy And e\my > 0 Then Delete e 
	Next	
End Function
Function iniexplosion(tp,x,y)
For i=0 To 9
e.explosions = New explosions
e\sx = x
e\sy = y + Rand(-th*4,th)
e\x = e\sx
e\y = y
e\w = Rand(2,4)
e\h = Rand(2,6)
e\mx = ((Rnd(1))-.5)*2
If Rand(6) = 1 Then e\mx = e\mx*3
e\my = Rand(-1,-4)
e\gy = .1
Next
End Function
Function drawexplosions()
For e.explosions = Each explosions

	If e\my > 0 Then
	Color 200-(Abs(e\my)*20),0,0
	Else
	z = 200-(Abs(e\my)*20)
	Color z,z,0
	End If
	Rect e\x,e\y,e\w,e\h
Next
End Function
Function fireweapon(tp,x,y,dir = 1)
	w.weapons = New weapons
	w\sx = x
	w\sy = y+((Rnd(1)*th))-th
	w\x = w\sx
	w\y = w\sy-th
	w\mx = (2)+Rnd(1)
	w\dettimer = 1200
	If dir = 0 Then w\mx = - w\mx
	w\my = -4
	w\gy = .1
End Function
Function updateweapons()
	For w.weapons = Each weapons
		;
		delthis = False
		If w\detflag = False
		w\x = w\x + w\mx
		If w\my <0 Then
			w\y = w\y + w\my
			Else
			z = w\my
			For z2 = 0 To z
				w\y = w\y + 1
				If w\y > w\sy Then delthis = True :w\detflag = True:w\y = w\sy : aidmap(w\x/tw,w\y/th) = True:Exit
			Next
		End If
		Else
		delthis = True
		End If
		;
		If delthis = False Then
			w\my = w\my + w\gy
			w\mx = w\mx + w\gx			
			Else
			If w\dettimer < 0 Then
				iniexplosion(0,w\x,w\sy)
				aidmap(w\x/tw,w\y/th) = False:
				Delete w
				delthis = True
				Else w\dettimer = w\dettimer - 1000/60
			End If
		End If
		;
		If delthis = False
		If RectsOverlap(w\x,w\y,3,3,0,0,(30-1)*tw,(30-1)*th) = False Then Delete w
		End If
		;
	Next
End Function
Function drawweapons()
	For w.weapons = Each weapons
		;
		sz# = (Abs(w\my)*2)+1
		Color 0,0,0 : Oval w\x,((w\sy)+th/2)-sz,sz,sz,True
		Color 0,130,0 : Rect w\x,w\y,6,6,True
		Color 0,0,0 : Rect w\x,w\y,6,6,False 
		
		;DebugLog w\my
		;
	Next
End Function
;
Function layerplayer() ; temp layer
	p.player = First player
	zup = False
	px = p\x / tw
	py = p\y / th
	For y=-3 To -2
		For x=-3 To 3
			If py+y > 0 And px+x > 0 And px+x <= 30-1 And py + y =< 30-1 Then
				If map(p\cl,px+x,py+y) => 3 
					zup = True
					Color 20,200,0:Rect (px+x)*tw,(py+y)*th,tw,th,False
					Viewport (px+x)*tw,(py+y)*th,tw,th+1
					drawplayer
					Viewport 0,0,GraphicsWidth(),GraphicsHeight()		
				End If
			End If
		Next
	Next
End Function

Function draw_ai(level)
For a.ai = Each ai
	;Color 40,40,40
	;Rect a\x,a\y,a\w,a\h,True
	drawaisprites(0,a\x,a\y)
Next
End Function
Function ini_ai(level,destroy = False)
Select destroy
Case False ; init
For y=0 To 30-1
	For x=0 To 30-1
		If map(level,x,y) = 9 Then
			a.ai = New ai
			a\x = x*tw
			a\y = y*th
			a\w = 16
			a\h = 16
		End If
	Next
Next
Case True ; destroy
Delete Each ai
End Select
End Function
Function switchlevel() ; Switch level maps
;
p.player = First player
nieuw = -1:oud = -1:new_x = -1:new_y = -1
lft = 0:rght = GraphicsWidth() - tw/2:tp = 0:btm = mh-(th*3.5)
oud = p\cl

Local aleft = 1
Local aright = 2
Local atop = 3
Local abottom = 4
Local newstartpos 

;0,1,6,7
;2,3,4,5
;maplinks
Select p\cl
	Case 1	; level border  collision in level switch
		If p\x < lft 		Then nieuw = 0		: newstartpos = aright
		If p\x > rght	Then nieuw = 6		: newstartpos = aleft
		If p\y > btm 	Then nieuw = 3		: newstartpos = atop		
	Case 0
		If p\x > rght 	Then nieuw = 1		: newstartpos = aleft
		If p\y > btm 	Then nieuw = 2		: newstartpos = atop
	Case 2
		If p\y < tp 		Then nieuw = 0 		: newstartpos = abottom
		If p\x > rght 	Then nieuw = 3 		: newstartpos = aleft
	Case 3
		If p\y < tp 		Then nieuw = 1		: newstartpos = abottom
		If p\x < lft 		Then nieuw = 2		: newstartpos = aright
		If p\x > rght	Then nieuw = 4		: newstartpos = aleft
	Case 4
		If p\x < lft		Then nieuw = 3		: newstartpos = aright
		If p\y < tp		Then nieuw = 6		: newstartpos = abottom
		If p\x >rght	Then nieuw = 5		: newstartpos = aleft
	Case 5
		If p\x < lft 		Then nieuw = 4		: newstartpos = aright
		If p\y < tp		Then nieuw = 7		: Newstartpos = atop
	Case 6
		If p\x < lft		Then nieuw = 1		: newstartpos = aright
		If p\y > btm	Then nieuw = 4		: newstartpos = atop
		If p\x > rght 	Then nieuw = 7		: Newstartpos = aleft		
	Case 7
		If p\x < lft		Then nieuw = 6		: Newstartpos = aright
		If p\y > btm	Then nieuw = 5		: Newstartpos = atop
End Select
; If change level then draw/set level and set new position
If nieuw <> -1 Then
	p\cl = nieuw
	drawmap
	ini_ai(oud,True) : ini_ai(nieuw)
	Select newstartpos
		Case aleft: p\x = tw/2
		Case atop: p\y = th/2: 	
		Case aright: p\x = GraphicsWidth()-tw/2 
		Case abottom:p\y = mh-(th*3.5)	
	End Select
End If
End Function
Function updatefps()
	fpscounter = fpscounter + 1
	If fpstimer < MilliSecs() Then
		fpstimer = MilliSecs() + 1000
		fps = fpscounter
		fpscounter = 0
	End If
End Function
Function iniplayer()
	a.player = New player
	a\x = 32
	a\y = 32
	a\maploc = 0
	a\energy = 100
	a\score = 0
End Function
Function drawplayer()
	p.player = First player
	x = p\x
	y = p\y
	
	Color 40,40,40
	Rect (x)-tw/3,(y)-(th*2),tw/1.6,th*2,True
	Color 0,0,0
	Rect (x)-tw/2,(y)-(th*1.5),tw,th*1.5,True

End Function
Function playercontrols()
	Local mx#,my#
	p.player = First player
	If KeyDown(200);up
		my = -.7
	End If
	If KeyDown(208);down
		my = .7
	End If
	If KeyDown(203);left
		mx = -1
		p\dir = 0
	End If
	If KeyDown(205);right
		mx = 1
		p\dir = 1
	End If
	If KeyHit(34) Then ; 'g'
		fireweapon(0,p\x,p\y,p\dir)
	End If
	p\mx = mx
	p\my = my
End Function
;
Function collision(x,y)
	l.localval = First localval
	p.player = First player
	x = x / tw
	y = y / th
	;
	If map(p\cl,x,y) > 0 Then Return map(p\cl,x,y)
	;
End Function
Function spritecollision16(x,y)
	l.localval = First localval
	p.player = First player
	x = x / tw
	y = y / th
	;	
	For y1=y To y+3
		For x1=x-2 To x+2
			If x1=>0 And y1=>0 And x1=<30-1 And y1<=30-1
					If map(p\cl,x1,y1) = 3 Then
						l \ x = x1
						l \ y = y1
						l \ id = map(p\cl,x1,y1)
						oke = True
						Exit
				End If
			End If
		Next
	Next
	;		
	If oke = True Then Return l\id
End Function
Function spritecollision8(x,y)
	l.localval = First localval
	p.player = First player
	x = (x / tw)
	x1 = x
	y = ((y +2)/ th) 
	For y1 = y To y+1
		If x1 => 0 And x1 <= 30-1 And y1 => 0 And y1 <= 30-1
			If map(p\cl,x1,y1) = 4 Then
				;DebugLog MilliSecs()
				l \ x = x1
				l \ y = y1
				l \ id = map(p\cl,x1,y1)
				oke = True				
			End If
		End If
	Next
	If oke = True Then Return  l \ id
End Function
;
Function drawminimap()
	SetBuffer ImageBuffer(minimapimage)
	Cls
	w = ImageWidth(minimapimage) / 10
	h = ImageHeight(minimapimage) / 10
		For y=0 To 8
		For x=0 To 8
			If zmatrix(x,y) > 0 Then
			Color 255,0,0
			Rect x*w,y*h,w,h,True
		Else
			Color 0,255,0
			Rect x*w,y*h,w,h,False
		End If
		If x=4 And y=3 Then
			Color 255,255,255
			Rect x*w,y*h,w,h,True
		End If
		Next
		Next
	SetBuffer BackBuffer()
End Function
Function gameupdate()
	l.localval = First localval
	l\playerlayer = 3
	p.player = First player
	ox = p\x
	oy = p\y
	For i=1 To gamespeed		
		p\x = p\x + p\mx
		p\y = p\y + p\my
	Next	
	Select spritecollision16(p\x,(p\y-1))
		Case 3 ; tree layer 3						
			l\playerlayer = 2	
	End Select
	;
		For y = -4 To 4 Step 4
		If spritecollision16(p\x,p\y-y) = 3
			z = Floor(p\y Mod th)
			For y2 = -gamespeed To gamespeed Step 2
				If Floor((p\y+y2) / th) > l\y
				p\x = ox
				p\y = oy
			End If
		Next 
		End If
		Next
	;
	For x = -tw/2 To tw/2  Step 8
		For y = -8 To 8 Step 4
		If spritecollision8((p\x)+x,p\y+y) = 4
			z = Floor(p\y Mod th)			
			If (z  =<4)
				If Floor((p\y)/th) > l\y				
					p\x = ox
					p\y = oy								
				End If
			End If
		End If
		Next
		Select spritecollision8((p\x)+x,p\y-3)
			Case 4 ; fenc		
				l\playerlayer = 1				
		End Select
	Next

	If tilecollision(p\cl,p\x-(tw/2),(p\y-th/2)) 
		p\x = ox
		p\y = oy
	End If

	; it updates the minimap
	q1 = Floor(p\x / tw)
	q2 = Floor(p\y / th)
	If l\ox <> q1 Or l\oy <> q2 Then
		l\ox = q1:l\oy = q2
		For y=-4 To 4
			For x=-4 To 4
				zmatrix(x+4,y+4) = 0
				x1 = (((p\x-tw/2))/tw )+ (x)
				y1 = (((p\y))/th )+ (y)
				If x1=>0 And x1=<30-1 And y1=>0 And y1=<30-1
					If map(p\cl,x1,y1) <> 0 Then
						zmatrix(x+4,y+4) = map(p\cl,x1,y1)
					End If
				End If
			Next
		Next
		drawminimap
	End If
	


	
End Function
Function tilecollision(mp,x,y)
	x=x/tw : y=y/th
	If x<0 Or x>30-1 Or y<0 Or y>30-1 Then Return 
	Select map(mp,x,y) 
		Case 3 : Return True
		Case 4 : Return True
		Case 8 : Return True
		Default : Return False
	End Select
End Function
Function drawmap(layer=0,x1=-1,y1=-1)
	l.localval = First localval
	p.player = First player
	If layer = 0 Or layer = 1
		SetBuffer ImageBuffer(imlayer(1)) : Cls
		; layer 1
		For y=0 To 30-1
			For x=0 To 30-1
				dx = x*tw
				dy = y*th
				Select map(p\cl,x,y)
					Case 0 : Color 12,60,4 ;  green
					Case 1 : Color 80,80,80 ; grey
					Case 2:  Color 100,40,0 ; brown
					Case 4: If Not map(p\cl,x,y+1) = 7 Then Color 12,60,4 Else Color 10,15,100;  green
					Case 7: Color 10,15,100;water
					;Default : Color 0,0,0
				End Select
				Rect dx,dy,tw,th
				Select map(p\cl,x,y)
					Case 4 : If map(p\cl,x,y+1) = 7 Then drawspritesjunk(4,dx,dy)
					Case 2 : If map(p\cl,x,y+1) = 7 Then drawmappartsprite(0,coastlinetable(0,x),dx,dy)
					Case 5 : drawspritesjunk(5,dx,dy)
					Case 6 : drawspritesjunk(6,dx,dy)
				End Select
			Next
		Next
		l\l1_update = True
	End If
	If layer = 0 Or layer = 2 Then
		SetBuffer ImageBuffer(imlayer(2)) : Cls
			For y=0 To 30-1
				For x=0 To 30-1
					dx = x*tw
					dy = y*th
					If (map(p\cl,x,y)=4) And (Not map(p\cl,x,y+1) = 7) Then
						drawsprite8(4,dx,dy)
						Color Rand(100),0,0 : Rect 0,0,10,10
					End If
					If (map(p\cl,x,y)=8) And (Not map(p\cl,x,y+1) = 7) Then
						drawsprite8(8,dx,dy)
						Color Rand(100),0,0 : Rect 0,0,10,10
					End If
				Next
			Next		
		l\l2_update = True
	End If	
	If layer = 0 Or layer = 3 Then
		SetBuffer ImageBuffer(imlayer(3)) : Cls
		; layer 3
			For y=0 To 30-1
				For x=0 To 30-1
					dx = x*tw
					dy = y*th
					If map(p\cl,x,y)  = 3 And map(p\cl,x,y+1) <> 7 Then
						;drawsprite16(map(p\cl,x,y),dx,dy)
						drawtreesprite(Rand(0,2),dx,dy)
					End If
					If map(p\cl,x,y) = 1 And map(p\cl,x,y+1) = 7 Then
						drawsprite32(map(p\cl,x,y),dx,dy)
					End If
				Next
			Next		
		l\l3_update = True
	End If
	SetBuffer BackBuffer()
End Function
;
Function inilargesprites(level)
For y=0 To 30-1
For x=0 To 30-1
If (map(level,x,y) = 3 And map(level,x,y+1) = 7) Then
	a.largesprites = New largesprites
	a\x = x*tw
	a\y = y*th
	a\id = 3
	a\name$ = "boat"
EndIf
Next
Next
End Function
Function drawlargesprites()
;	For a.largesprites = Each largesprites
;	a.largesprites = First largesprites
;	drawlargesprite(0,a\x,a\y)
	;Next
End Function
;
Function drawsprite32(num,x1,y1) ;water - layer 3?
Local draw
For y=0 To 15
For x=0 To 15
	dx = (( x*(tw/2) ) + x1) - (tw*1.5)
	dy = (( y*(th/2) ) + y1);- th * 3
	draw = True
	Select sprites32(num,x,y)
		Case 1 ; yellow (plank)
		Color 184,165,0
		Case 2
		Color 150,124,0
		Case 3
		Color 80,90,0
	Default
	draw = False
	End Select
	If draw = True Then Rect dx,dy,(tw/2)+1,(th/2)+1,True
Next
Next
End Function
Function drawsprite16(num,x1,y1)
	Local draw
	For y=0 To 7
		For x=0 To 7
			dx = (( x*(tw/2) ) + x1) - (tw*1.5);1.5
			dy = (( y*(th/2) ) + y1) - th * 3
			draw = True
			Select sprites16(num,x,y)
				Case 0
					draw = False					
				Case 2 ; brown
					Color 100,20,0					
				Case 1 ; green
					Color 20,100,0
				Case 3 ; light green
					Color 25,120,0
				Case 4 ; light brown
					Color 120,25,0
				Case 5 ; dark green
					Color 18,80,0
				Case 6 ; lighter green
					Color 30,130,0
				Case 7 ;blue				
					Color 20,30,100
			End Select
			If draw = True Then Rect dx,dy,(tw/2)+1,(th/2)+1,True
		Next
	Next
End Function
Function drawsprite8(num,x1,y1)
	Local draw
	For y=0 To 7
		For x = 0 To 7
			dx = (( x*(tw/7.6) ) + x1) 
			dy = (( y*(th/5)) + y1-th/2)
			draw = True
			Select sprites8(num,x,y)
				Case 0
					draw = False
				Case 3 ; dark brown
					Color 80,9,0
				Case 2 ; brown				
					Color 100,20,0					
				Case 1 ; green
					Color 20,100,0
				
			End Select
			If draw = True Then Rect dx,dy,(tw/5),(th/5)+1,True
		Next
	Next
End Function
Function drawspritesjunk(num,x1,y1)
	Local draw
	For y=0 To 7
		For x = 0 To 7
			dx = (( x*(tw/8) ) + x1) 
			dy = (( y*(th/6) ) + y1)  - (th/2.7)
			draw = True
			Select spritesjunk(num,x,y)
				Case 0
					draw = False					
				Case 1 ; yellow bright				
					Color 250,250,0					
				Case 2 ; yellow dark
					Color 160,170,20
				Case 3 ; green light
					Color 0,200,0					
				Case 4 ; green dark
					Color 0,100,0
				Case 5 ; lighter water
					Color 40,60,200
				Case 6 ; darker water
					Color 40,60,170
			End Select
			If draw = True Then Rect dx,dy,(tw/8),(th/8)+1,True
		Next
	Next
End Function
Function drawaisprites(num,x1,y1)
	Local draw
	For y=0 To 15
		For x=0 To 15
			dx = (( x*(tw/8) ) + x1) 
			dy = (( y*(th/6) ) + y1)  - (th/2.7)
			draw = True
			Select ai_sprites(num,x,y)
				Case 1
				Color 100,40,0 ; brown
				Case 2
				Color 120,46,0 ; lighter brown
				Case 3 ; dark grey
				Color 50,50,50
				Case 4 ;  grey
				Color 80,80,80
				Case 5 ; dark grey
				Color 40,40,40
				Case 7
				;Color 20,20
				Case 8
				Color 5,5,5
				Case 9
				Color 205,205,205 ; white
				Default
				draw = False
			End Select
			If draw = True Then Rect dx,dy,(tw/8),(th/8)+1,True
		Next
	Next
End Function
Function drawlargesprite(num,x1,y1)
	Local draw
	For y=0 To 15
		For x = 0 To 15
			dx = (( x*(tw/3) ) + x1) 
			dy = (( y*(th/3)) + y1-th/2)
			draw = True		
			Select large_sprites(num,x,y)
				Case 0
					draw = False
				Case 3 ; dark brown
					Color 80,9,0
				Case 2 ; brown				
					Color 100,20,0
				Case 7 ; light brown
					Color 150,30,6
				Case 1 ; grey
					Color 100,100,100
				Case 4 ; black
					Color 0,0,0
				Case 5 ; light blue
					Color 20,60,200
				Case 6 ; white
					Color 190,190,190
			End Select
			If draw = True Then Rect dx,dy,(tw/3)+1,(th/3)+1,True
		Next
	Next
	;Color 255,255,255
End Function
Function drawtreesprite(num,x1,y1)
	Local draw = False
	For y=0 To 15
		For x=0 To 15
			dx# = (( x*(tw/4) ) + x1) - (tw*1.5);1.5
			dy# = ((( y*(th/4) ) + y1) - (th * 3))
			draw = True
			Select treesprites(num,x,y)
				Case 0 : draw = False
				Case 1 : Color 50,200,50 ; light green
				Case 2 : Color 36,166,36 ; dark green
				Case 3 : Color 80,40,20; dark brown
				Case 4 : Color 100,50,25; brown
				Case 5 : Color 160,160,160
				Case 9 : Color 5,5,5 
			End Select
			If draw = True Then Rect dx,dy,(tw/4)+1,(th/4)+1,True
		Next
	Next
End Function
Function drawmappartsprite(tp,num,x1,y1)
	Local draw
	For y=0 To 7
		For x=0 To 7
			dx = (( x*(tw/8) ) + x1) 
			dy = (( y*(th/5) ) + y1)  - (th/2.7)
			draw = True
			Select mapparts(tp,num,x,y)
				Case 0 : draw = False
				Case 1 : Color 190,190,190 ; white
				Case 2 : Color 12,60,4 ; green
				Case 3 : Color 140,50,7 ; light brown
				Case 7 : Color 10,15,100 ; water
			End Select
			If draw = True Then Rect dx,dy,(tw/3),(th/3),True
		Next
	Next
End Function
Function drawaisoldiersprite(num,x1,y1)
	Local draw
	For y=0 To 15
		For x=0 To 15
			dx = (( x*(tw/12) ) + x1+(th/2)) 
			dy = (( y*(th/8) ) + y1)  - (th)
			draw = True
			Select aisoldiersprites(num,x,y)
				Case 1
					Color 100,40,0		; brown
				Case 2
					Color 240,220,208 		; it be skin
				Case 3						; dark grey
					Color 50,50,50
				Case 4 						; grey
					Color 80,80,80
				Case 5 						; dark grey
					Color 40,40,40
				Case 7				
				Case 8
					Color 5,5,5
				Case 9
					Color 5,5,5 			; is black
				Default
				draw = False
			End Select
			If draw = True Then Rect dx,dy,1,1,True
		Next
	Next
End Function
Function readmap()
	Restore lvl0
	For y=0 To 30-1
		For x=0 To 30-1
			Read a
			map(0,x,y) = a
		Next
	Next
	Restore lvl1
	For y=0 To 30-1
		For x=0 To 30-1
			Read a
			map(1,x,y) = a
		Next
	Next
	Restore lvl2
	For y=0 To 30-1
		For x=0 To 30-1
			Read a
			map(2,x,y) = a
		Next
	Next
	Restore lvl3
	For y=0 To 30-1
		For x=0 To 30-1
			Read a
			map(3,x,y) = a
		Next
	Next
		Restore lvl4
	For y=0 To 30-1
		For x=0 To 30-1
			Read a
			map(4,x,y) = a
		Next
	Next
		Restore lvl5
	For y=0 To 30-1
		For x=0 To 30-1
			Read a
			map(5,x,y) = a
		Next
	Next
		Restore lvl6
	For y=0 To 30-1
		For x=0 To 30-1
			Read a
			map(6,x,y) = a
		Next
	Next
	Restore lvl7 : For y=0 To 30-1 : For x=0 To 30-1 : Read a : map(7,x,y) = a : Next : Next
End Function
Function readsprites()
	Restore tree 					: For y=0 To 7:For x=0 To 7 		: Read a : sprites16(3,x,y) = a		: Next:Next
	Restore fence 				: For y=0 To 7:For x=0 To 7		: Read a : sprites8(4,x,y) = a 			: Next:Next
	Restore fenceup 			: For y=0 To 7:For x=0 To 7 		: Read a : sprites8(8,x,y) = a 			: Next:Next
	;spritejunk
	Restore stro 					: For y=0 To 7:For x= 0 To 7 		: Read a : spritesjunk(5,x,y) = a		: Next:Next
	Restore lightwater 			: For y=0 To 7:For x= 0 To 7 		: Read a : spritesjunk(4,x,y) = a		: Next:Next
	Restore grass 				: For y=0 To 7:For x= 0 To 7 		: Read a : spritesjunk(6,x,y) = a 		: Next:Next
	;sprites32
	Restore waterboard 		: For y=0 To 15:For x=0 To 15	: Read a : sprites32(1,x,y) = a 		: Next:Next
	;ai sprites
	Restore badger 				: For y=0 To 15:For x=0 To 15 	: Read a : ai_sprites(0,x,y) = a 		: Next:Next
	; large usable sprites
	Restore boat 				: For y=0 To 15:For x=0 To 15 	: Read a : large_sprites(0,x,y) = a 	: Next:Next
	; Small coast parts
	Restore coastpart1 		: For y=0 To 7:For x=0 To 7		: Read a : mapparts(0,0,x,y) = a 	: Next:Next
	Restore coastpart2 		: For y=0 To 7:For x=0 To 7 		: Read a : mapparts(0,1,x,y) = a 	: Next:Next
	Restore coastpart3 		: For y=0 To 7:For x=0 To 7 		: Read a : mapparts(0,2,x,y) = a 	: Next:Next
	Restore coastpart4 		: For y=0 To 7:For x=0 To 7 		: Read a : mapparts(0,3,x,y) = a 	: Next:Next
	Restore coastpart5 		: For y=0 To 7:For x=0 To 7		: Read a : mapparts(0,4,x,y) = a		: Next:Next
	Restore coastpart6		: For y=0 To 7:For x=0 To 7		: Read a : mapparts(0,5,x,y) = a		: Next:Next
	Restore coastpart7 		: For y=0 To 7:For x=0 To 7		: Read a : mapparts(0,6,x,y) = a		: Next:Next
	; Trees
	Restore tree1				: For y=0 To 15:For x=0 To 15	: Read a : treesprites(0,x,y) = a 		: Next:Next
	Restore tree2				: For y=0 To 15:For x=0 To 15	: Read a : treesprites(1,x,y) = a 		: Next:Next
	Restore tree3 				: For y=0 To 15:For x=0 To 15	: Read a : treesprites(2,x,y) = a 		: Next:Next
	Restore soldierstanding1: For y=0 To 15:For x=0 To 15		: Read a : aisoldiersprites(0,x,y) = a: Next:Next
End Function











;
;
;SPRITES
;

.lvl0
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,3,0,0,6,0,0,0,0,0,0,0,0,0,0,0,4,4,4,4,4,4,4,4,4,4,4
Data 6,0,0,4,4,4,4,4,4,4,4,0,3,0,0,0,0,0,0,0,0,0,1,1,1,1,0,0,0,0
Data 2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,0,3,0,0,0,0,0,1,1,1,1,0,0,0,0
Data 2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,0,0,0,0,0,0,1,1,1,1,0,0,3,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2
Data 0,0,0,0,0,0,0,6,0,0,0,0,0,0,0,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,6,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,0,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,4,4,4,4
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,4,4,4,4
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 4,4,4,4,4,4,4,4,4,8,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,6,0
Data 0,0,8,3,2,2,2,2,0,8,0,0,6,0,0,0,0,3,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,8,2,2,2,2,2,0,8,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,8,2,2,2,2,2,0,8,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,8,2,2,2,2,2,0,8,0,0,0,0,0,0,0,0,0,6,0,0,0,0,0,0,0,0,0,0
Data 0,0,8,0,0,0,5,0,0,8,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,8,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,8,0,0,0,0,0,0,6,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,6,0,0
Data 0,0,8,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,0,0,0,0,0
Data 0,0,8,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,8,0,0,0,0,0,0,0,0,0,0,0,0,0,4,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,8,0,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,8,0,6,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,8,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,6,0,0,0,0,0,0
Data 0,0,8,0,0,6,0,0,0,0,0,0,0,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 4,4,4,4,4,4,4,4,4,0,0,0,4,4,4,4,4,4,4,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
.lvl1
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,8,0,0,4,4,4,4,4,4,4,4,4
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,8,0,0,8,0,0,0,0,0,0,0,0
Data 0,0,6,0,0,0,0,1,1,1,1,1,0,0,0,3,0,0,0,0,0,0,0,3,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,6,0,0,0
Data 2,2,2,2,2,2,0,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 2,2,2,2,2,2,2,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,2,2,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,6,0,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,0,6,0,0,0,0,0,0,0,0,6,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,6,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,4,4,4,4,4,4,4,4,4,4,4,4,0,0,0,0,0,4,4,4,4,4,4,4,4,4,4,4
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,6,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,0,6,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,6,0,0,0,3,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,3,0,0,3,0,0,0,3,0,3,0,0,0,0,0,3,0,0,0,0,0,3,0,0,3,0,0,0,0
Data 0,0,0,0,0,0,6,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
.lvl2
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,0,6,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,4,4,4,4,4,4,4,4,4,4,4
Data 0,0,0,0,0,9,0,0,0,6,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,6,0,0,0,0,0,3,0,0,0,0,0,6,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,9,0,0,0,0,0,3,0,0,0,0
Data 4,4,4,4,4,4,4,0,0,0,0,0,0,6,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,3,0,0,0,0,0,0,9,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,6,0,0
Data 0,0,0,6,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,6,0,3,0,0,0,0,3,0,0,0
Data 0,0,0,0,0,0,0,0,6,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 2,2,2,2,2,2,2,2,2,2,2,2,2,2,1,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2
Data 7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7
Data 7,7,7,7,4,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7
Data 7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7
Data 7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,4,7,7,7
Data 7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7
Data 7,7,7,7,7,4,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7
Data 7,7,7,7,7,7,7,7,7,7,7,7,3,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7
Data 7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7
Data 7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,4,7,7,7,7,7,7,7,7,7,7
Data 7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7
Data 7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7
Data 7,4,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7
Data 7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7
Data 7,7,7,7,7,7,7,7,7,7,4,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7
Data 7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7
Data 7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7
Data 7,7,7,7,7,7,4,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,4,7,7,7,7,7,7
Data 7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7
;
.lvl3
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,4,4,4,4,4,4,4
Data 4,4,4,6,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,6,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,6,0,0,0,3,0,0,3,0,0,0,0,6,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,6,0,0,0,0,0,0,0,0,0,0,0,0,0,3,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,3,0,0,0,0,0,0,0,0,0,0,3,0,0,0,0,0,6,0,0,0
Data 0,0,0,6,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,6,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2
Data 7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7
Data 7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7
Data 7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,4,7,7,7,7,7,7,7,7,7
Data 7,7,4,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7
Data 7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7
Data 7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7
Data 7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7
Data 7,7,7,7,7,7,7,7,7,7,7,7,4,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7
Data 7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,4,7,7,7,7
Data 7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7
Data 7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7
Data 7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,4,7,7,7,7,7,7,7,7,7,7,7,7,7
Data 7,7,7,4,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7
Data 7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,4
Data 7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7
Data 7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7
Data 7,7,7,7,7,4,7,7,7,7,7,7,7,7,7,7,7,7,4,7,7,7,7,7,7,7,7,7,7,7
Data 7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7
;
.lvl4
Data 0,0,0,0,0,0,0,0,0,6,0,0,0,0,2,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7
Data 0,0,6,0,3,0,0,0,0,0,0,0,0,0,2,7,7,7,7,7,7,7,7,7,7,7,7,4,7,7
Data 0,0,0,0,0,6,0,0,0,0,0,0,0,0,2,7,7,7,4,7,7,7,7,7,7,7,7,7,7,7
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7
Data 0,3,0,0,0,0,0,0,0,0,0,0,6,0,2,7,7,7,7,7,7,7,7,7,7,4,7,7,7,7
Data 0,6,0,0,0,0,0,0,0,0,0,0,0,0,2,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7
Data 0,0,0,0,0,0,0,0,0,0,0,3,0,0,2,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7
Data 0,0,0,0,0,6,0,0,3,0,0,0,0,0,2,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7
Data 0,0,0,0,0,0,0,0,0,0,0,6,0,0,2,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7
Data 2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7
Data 7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7
Data 7,7,7,7,7,7,4,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,4,7,7,7,7
Data 7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7
Data 7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7
Data 7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7
Data 7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,4,7,7,7,7,7,7,7,7,7,7,7,7,7,7
Data 7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7
Data 7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7
Data 7,7,7,7,7,7,4,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7
Data 7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7
Data 7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7
Data 7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7
Data 7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,4,7,7,7,7,7
Data 7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7
Data 7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7
Data 7,7,7,4,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7
Data 7,7,7,7,7,7,7,7,7,7,7,7,7,7,4,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7
Data 7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7
;
.lvl5
Data 7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7
Data 7,7,7,7,7,7,7,7,7,7,7,7,7,7,4,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7
Data 7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,4,7,7,7,7,7,7,7
Data 7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7
Data 7,7,7,4,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7
Data 7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7
Data 7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7
Data 7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7
Data 7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7
Data 7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7
Data 7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7
Data 7,7,7,7,7,7,7,7,7,7,7,7,7,1,7,7,7,7,7,7,7,7,4,7,7,7,7,7,7,7
Data 7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7
Data 7,7,7,7,4,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7
Data 7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7
Data 7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7
Data 7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7
Data 7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7
Data 7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,4,7,7,7
Data 7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7
Data 7,7,7,7,7,7,7,7,4,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7
Data 7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7
Data 7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7
Data 7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,4,7,7,7,7,7,7,7,7,7
Data 7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7
Data 7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7
Data 7,7,7,7,4,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7
Data 7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,4,7,7,7
Data 7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7
Data 7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7
.lvl6
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,7,7,7,7,7,7,7,7,7,7,7,7,4,7,7
Data 0,6,0,0,0,0,0,0,0,0,0,6,0,0,2,7,7,7,4,7,7,7,7,7,7,7,7,7,7,7
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7
Data 0,3,0,0,0,0,0,0,0,0,0,0,0,0,2,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,7,7,7,4,7,7,7,7,7,7,7,7,7,7,7
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,7,7,7,7,7,7,7,7,7,7,7,7,7,4,7
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7
Data 4,4,4,4,4,4,4,4,4,4,4,4,4,4,2,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7
Data 0,0,6,0,0,0,0,0,0,0,0,0,0,0,2,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7
Data 0,0,0,0,0,0,0,0,0,0,0,0,6,0,2,7,7,4,7,7,7,7,7,7,7,7,7,7,7,7
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,7,7,7,7,7,7,7,7,7,7,7,4,7,7,7
Data 0,0,0,6,0,0,0,0,0,0,0,0,0,0,2,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7
Data 0,0,0,0,0,0,0,0,0,0,0,3,0,0,2,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,7,7,7,7,7,7,7,7,7,7,7,4,7,7,7
Data 0,3,0,0,0,0,0,0,0,0,0,6,0,0,2,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,7,7,7,4,7,7,7,7,7,7,7,7,7,7,7
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,7,7,7,7,7,7,7,7,7,7,7,4,7,7,7
Data 0,6,0,0,0,0,0,0,0,0,0,0,0,0,2,7,7,4,7,7,7,7,7,7,7,7,7,7,7,7
Data 0,0,0,0,0,0,0,0,0,0,0,6,0,0,2,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7
.lvl7
Data 0,0,0,0,0,0,0,0,8,0,0,0,0,0,0,0,0,0,0,0,2,2,2,2,0,0,0,0,0,0
Data 0,6,0,0,0,0,0,0,8,0,0,0,0,0,3,0,0,0,0,2,2,2,2,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,8,0,0,0,0,0,0,0,0,2,2,2,2,0,0,0,6,0,0,0,2,2
Data 0,0,0,0,0,6,0,0,8,0,6,0,0,0,0,0,2,2,2,2,0,0,0,0,0,0,0,2,2,7
Data 0,0,0,0,0,0,0,0,8,0,0,0,0,0,0,2,2,2,0,0,0,3,0,0,0,0,2,2,7,7
Data 0,0,0,0,0,0,0,0,8,0,0,0,3,0,2,2,2,0,0,0,0,0,0,0,0,0,2,7,7,7
Data 0,0,0,0,0,0,0,0,8,0,0,0,0,0,2,2,2,0,6,0,0,0,0,0,0,2,2,7,4,7
Data 0,0,0,0,0,0,0,0,8,0,0,0,0,0,2,2,2,0,0,0,0,0,0,0,0,2,7,7,7,7
Data 0,0,0,0,0,0,0,0,8,0,0,0,0,0,0,2,2,2,0,3,0,0,0,0,2,2,7,7,7,7
Data 0,0,0,0,0,0,6,0,8,0,0,0,0,0,0,0,2,2,2,0,0,0,0,0,2,7,7,7,7,7
Data 0,6,0,0,0,0,0,0,8,0,0,0,0,0,0,0,2,2,2,0,0,0,0,0,2,7,7,7,7,7
Data 0,0,0,0,0,0,0,0,8,0,0,0,0,3,0,0,2,2,2,0,0,0,6,0,2,7,7,7,7,7
Data 0,0,0,0,0,0,0,0,8,0,0,0,0,0,0,0,2,2,2,0,0,0,0,0,2,7,7,7,4,7
Data 0,0,0,0,0,0,0,0,8,0,6,0,0,0,0,0,2,2,2,0,3,0,0,0,2,2,7,7,7,7
Data 0,0,0,0,0,6,0,0,8,0,0,0,0,6,0,2,2,2,0,0,0,0,0,2,2,7,7,7,7,7
Data 0,0,0,0,0,0,0,4,8,0,0,0,0,0,2,2,2,0,0,0,0,0,0,2,7,7,7,7,7,7
Data 0,0,0,0,0,0,0,8,0,0,0,0,0,2,2,2,0,0,0,0,0,0,0,2,7,7,7,7,7,7
Data 0,0,6,0,0,0,4,8,0,0,3,0,0,2,2,2,0,3,0,0,0,0,0,2,7,7,7,7,7,7
Data 0,0,0,0,0,0,8,0,0,0,0,0,2,2,2,0,0,0,0,0,0,0,0,2,2,7,7,7,4,7
Data 0,0,0,0,0,0,8,0,0,0,0,0,2,2,2,6,0,0,0,0,6,0,0,0,0,2,7,7,7,7
Data 0,0,0,6,0,0,8,0,0,0,0,2,2,2,0,0,0,0,0,0,0,0,0,0,2,7,7,4,7,7
Data 0,0,0,0,0,0,8,0,0,0,0,2,2,2,0,0,3,0,0,0,0,0,0,0,2,7,7,7,7,7
Data 4,4,4,4,4,4,0,0,3,0,0,2,2,2,0,0,0,0,0,0,0,0,0,2,2,7,7,7,7,7
Data 0,0,0,0,0,0,0,0,0,0,0,2,2,2,0,0,0,0,0,0,6,0,0,2,7,7,7,7,7,7
Data 2,2,2,2,0,0,0,0,6,0,0,2,2,2,0,0,3,0,0,0,0,0,0,2,7,7,7,7,7,7
Data 2,2,2,2,2,2,0,0,0,0,2,2,2,2,0,0,0,0,0,0,0,2,2,7,7,7,7,7,7,7
Data 2,2,2,2,2,2,2,2,2,2,2,2,2,2,0,3,0,0,3,0,0,2,7,7,7,7,7,7,7,7
Data 0,0,0,2,2,2,2,2,2,2,2,2,2,0,0,0,0,0,0,0,2,2,7,7,7,7,7,4,7,7
Data 0,3,0,0,0,2,2,2,2,2,2,0,6,0,0,3,0,0,0,0,2,7,7,7,4,7,7,7,7,7
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,0,0,2,7,7,7,7,7,7,7,7,7
; Sprite junk ( junk and stuff)
.grass
Data 0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0
Data 0,4,4,0,4,0,4,4
Data 0,0,4,0,4,4,0,0
Data 0,0,0,4,4,0,0,0
Data 0,0,0,0,0,0,0,0
.lightwater
Data 0,0,0,0,0,0,0,0
Data 0,0,0,5,5,0,0,0
Data 5,5,5,0,0,5,5,5
Data 0,0,0,0,0,0,0,0
Data 0,0,6,6,0,0,0,0
Data 0,6,0,0,6,6,6,6
Data 0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0
.stro
Data 0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0
Data 0,1,1,1,0,0,0,0
Data 0,0,0,0,1,1,1,0
Data 0,1,1,1,0,0,0,1
Data 0,0,0,0,1,1,1,0
Data 0,0,0,0,0,0,0,1
Data 0,0,0,0,0,0,0,0

; Sprite 8 (smaller stuff)
.fence
Data 0,3,0,0,0,3,0,0
Data 3,2,3,3,3,2,3,3
Data 2,2,2,2,2,2,2,2
Data 0,2,0,0,0,2,0,0
Data 3,2,3,3,3,2,3,3
Data 2,2,2,2,2,2,2,2
Data 0,2,0,0,0,2,0,0
Data 0,2,0,0,0,2,0,0
.fenceup
Data 3,3,0,0,0,0,0,0
Data 3,3,0,0,0,0,0,0
Data 2,2,0,0,0,0,0,0
Data 3,3,0,0,0,0,0,0
Data 3,3,0,0,0,0,0,0
Data 2,2,0,0,0,0,0,0
Data 3,3,0,0,0,0,0,0
Data 3,3,0,0,0,0,0,0

; Sprite 16 (larger stuff)
.tree
Data 0,0,3,3,3,1,0,0
Data 0,3,3,3,1,1,1,0
Data 0,3,1,6,1,1,5,0
Data 0,6,1,1,1,6,5,0
Data 0,0,1,5,5,5,0,0
Data 0,0,0,4,2,0,0,0
Data 0,0,0,4,2,0,0,0
Data 0,0,0,4,2,0,0,0

;sprite 32 ; large stuf
.waterboard
Data 0,0,1,1,1,1,1,1,1,1,1,1,0,0,0,0
Data 0,0,1,1,1,1,1,1,1,1,1,1,0,0,0,0
Data 0,0,2,2,2,2,2,2,2,2,2,2,0,0,0,0
Data 0,0,1,1,1,1,1,1,1,1,1,1,0,0,0,0
Data 0,0,1,1,1,1,1,1,1,1,1,1,0,0,0,0
Data 0,0,2,2,2,2,2,2,2,2,2,2,0,0,0,0
Data 0,0,1,1,1,1,1,1,1,1,1,1,0,0,0,0
Data 0,0,1,1,1,1,1,1,1,1,1,1,0,0,0,0
Data 0,0,2,2,2,2,2,2,2,2,2,2,0,0,0,0
Data 0,0,1,1,1,1,1,1,1,1,1,1,0,0,0,0
Data 0,0,1,1,1,1,1,1,1,1,1,1,0,0,0,0
Data 0,0,1,1,1,1,1,1,1,1,1,1,0,0,0,0
Data 0,0,2,2,2,2,2,2,2,2,2,2,0,0,0,0
Data 0,0,0,3,0,0,0,0,0,0,3,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
; Large useable sprites
.boat
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 6,6,0,0,0,0,0,0,3,5,0,0,0,0,0,0
Data 6,6,2,2,2,2,2,2,2,5,7,7,2,2,0,0
Data 6,6,4,4,4,4,4,4,4,3,3,7,7,2,2,2
Data 1,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2
Data 1,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2
Data 1,3,3,3,3,3,3,3,3,3,3,3,3,3,0,0
Data 0,4,3,3,3,3,4,3,3,3,3,3,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data "end","1rfd"
.boatdown
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,0,2,2,2,2,2,2,0,0,0,0,0
Data 0,0,0,0,0,2,2,2,2,2,2,0,0,0,0,0
Data 0,0,0,0,1,2,2,2,2,2,2,1,0,0,0,0
Data 0,0,0,0,2,1,1,1,1,1,1,2,0,0,0,0
Data 0,0,0,0,2,1,1,1,1,1,1,2,0,0,0,0
Data 0,0,0,0,1,3,1,1.1,1,3,1,0,0,0,0
Data 0,0,0,0,1,2,3,3,3,3,2,1,0,0,0,0
Data 0,0,0,0,1,1,2,2,2,2,1,1,0,0,0,0
Data 0,0,0,0,0,1,1,1,1,1,1,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0


; ai sprites
.badger
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,9,8,0,0,0
Data 0,0,0,0,0,0,9,9,9,0,8,5,9,8,0,0
Data 0,0,0,0,9,9,8,8,8,8,8,8,9,9,9,8
Data 0,9,9,9,8,8,8,8,8,8,8,8,8,8,8,8
Data 9,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8
Data 0,8,0,0,8,8,8,8,8,8,8,8,0,8,0,0
Data 0,0,0,0,8,8,0,0,0,8,8,0,0,0,0,0
Data 0,0,0,0,8,0,0,0,0,0,8,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0

;.badger
;Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
;Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
;Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
;Data 0,0,0,0,0,0,0,0,0,0,0,9,8,0,0,0
;Data 0,0,0,0,0,0,9,9,9,0,8,5,9,8,0,0
;Data 0,0,0,0,9,9,8,8,8,8,8,8,9,9,9,8
;Data 0,9,9,9,8,8,8,8,8,8,8,8,8,8,8,8
;Data 9,8,0,0,8,8,8,8,8,8,8,8,0,8,0,0
;Data 0,0,0,0,8,8,0,0,0,8,8,0,0,0,0,0
;Data 0,0,0,0,8,0,0,0,0,0,8,0,0,0,0,0
;Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
;Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
;Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
;Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
;Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
;Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
; Houses

.coastlinedat
Data 0,0,1,0,2,3,0,0,2,3,4,5,5,5,6,1,0,0,1,0,0,0,2,3,0,0,0,2,3,0

;small parts
.coastpart1
Data 2,2,2,2,2,2,2,2
Data 3,3,3,3,3,3,3,3
Data 3,3,3,3,3,3,3,3
Data 1,1,1,1,1,1,1,1
Data 7,7,7,7,7,7,7,7
Data 7,7,7,7,7,7,7,7
Data 7,7,7,7,7,7,7,7
Data 7,7,7,7,7,7,7,7
.coastpart2
Data 2,3,3,3,3,3,3,2
Data 3,3,3,1,1,3,3,3
Data 3,3,1,7,7,1,3,3
Data 1,1,7,7,7,7,1,1
Data 7,7,7,7,7,7,7,7
Data 7,7,7,7,7,7,7,7
Data 7,7,7,7,7,7,7,7
Data 7,7,7,7,7,7,7,7
.coastpart3
Data 2,2,3,3,3,3,3,3
Data 3,3,3,3,3,3,3,3
Data 3,3,1,1,1,1,1,1
Data 1,1,7,7,7,7,7,7
Data 7,7,7,7,7,7,7,7
Data 7,7,7,7,7,7,7,7
Data 7,7,7,7,7,7,7,7
Data 7,7,7,7,7,7,7,7
.coastpart4
Data 3,3,3,3,3,3,2,2
Data 3,3,3,3,3,3,3,3
Data 1,1,1,1,1,3,3,3
Data 7,7,7,7,7,1,1,1
Data 7,7,7,7,7,7,7,7
Data 7,7,7,7,7,7,7,7
Data 7,7,7,7,7,7,7,7
Data 7,7,7,7,7,7,7,7
.coastpart5
Data 3,3,3,3,3,3,2,2
Data 3,3,3,3,3,3,2,2
Data 3,3,3,3,3,2,2,2
Data 1,1,1,1,1,3,3,3
Data 7,7,7,7,7,3,3,3
Data 7,7,7,7,7,1,1,1
Data 7,7,7,7,7,7,7,7
Data 7,7,7,7,7,7,7,7
.coastpart6
Data 2,2,2,2,2,2,2,2
Data 2,2,2,2,2,2,2,2
Data 2,2,2,2,2,2,2,2
Data 3,3,3,3,3,3,3,3
Data 3,3,3,3,3,3,3,3
Data 1,1,1,1,1,1,1,1
Data 7,7,7,7,7,7,7,7
Data 7,7,7,7,7,7,7,7
.coastpart7
Data 2,2,3,3,3,3,3,3
Data 2,2,3,3,3,3,3,3
Data 2,2,2,3,3,3,3,3
Data 3,3,3,3,1,1,1,1
Data 3,3,3,3,7,7,7,7
Data 1,1,1,1,7,7,7,7
Data 7,7,7,7,7,7,7,7
Data 7,7,7,7,7,7,7,7

;Trees
.tree1
Data 0,0,0,0,0,0,0,1,1,0,0,0,0,0,0,0
Data 0,0,0,0,0,1,1,1,1,2,2,0,0,0,0,0
Data 0,0,0,0,1,1,2,1,1,2,2,2,0,0,0,0
Data 0,0,0,1,1,1,1,1,1,1,2,2,2,0,0,0
Data 0,0,0,1,1,1,1,1,1,1,1,2,2,0,0,0
Data 0,0,1,1,1,1,1,1,2,1,1,2,2,2,0,0
Data 0,0,1,1,1,1,1,1,1,1,1,1,2,2,0,0
Data 0,1,1,1,2,1,1,1,1,1,1,1,2,2,2,0
Data 0,1,1,1,1,1,1,2,1,1,1,1,1,2,2,0
Data 0,1,1,2,1,1,1,1,1,1,1,1,1,2,2,0
Data 1,1,1,1,1,1,1,2,1,1,1,1,1,2,2,2
Data 1,1,1,1,1,2,1,1,1,1,1,1,1,1,2,2
Data 0,1,1,1,1,1,1,1,1,1,1,1,2,2,2,0
Data 0,9,1,1,1,1,1,1,1,1,1,2,2,2,9,0
Data 9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9
Data 0,9,9,9,9,9,9,9,9,9,9,9,9,9,9,0
.tree2
Data 0,0,0,0,0,0,0,0,0,0,0,0,4,0,0,0
Data 0,0,0,0,0,0,0,0,0,4,0,0,4,0,0,0
Data 0,0,0,0,0,0,0,0,0,4,3,3,0,0,0,0
Data 0,0,0,4,0,0,0,0,0,4,3,0,0,0,0,0
Data 0,0,0,0,4,0,0,0,0,4,3,0,0,4,4,0
Data 0,0,0,0,4,3,4,3,0,0,4,3,4,0,0,0
Data 0,0,0,0,0,0,4,3,0,0,4,3,0,0,0,0
Data 0,0,0,0,0,0,4,3,0,4,3,3,0,0,0,0
Data 0,0,0,4,0,4,4,3,4,4,3,3,0,0,0,0
Data 0,0,0,0,4,3,3,3,3,3,3,0,0,0,0,0
Data 0,0,0,0,0,4,3,3,0,0,0,0,0,0,0,0
Data 0,0,0,0,0,4,3,3,4,4,0,0,0,0,0,0
Data 0,0,0,0,4,4,3,3,0,0,4,0,0,0,0,0
Data 0,0,0,0,0,4,3,3,0,0,0,0,0,0,0,0
Data 0,0,0,9,9,4,3,3,9,9,0,0,0,0,0,0
Data 0,0,0,0,9,9,9,9,9,0,0,0,0,0,0,0
.tree3
Data 0,0,0,0,0,0,0,1,1,0,0,0,0,0,0,0
Data 0,0,0,0,0,1,1,1,1,2,2,0,0,0,0,0
Data 0,0,1,1,1,1,1,2,2,2,2,2,0,0,0,0
Data 0,1,1,1,1,1,1,2,2,2,2,2,2,2,0,0
Data 0,1,1,1,1,3,2,2,2,2,3,2,2,2,2,0
Data 0,0,0,1,2,2,3,3,3,3,3,3,2,2,2,2
Data 0,1,1,1,3,2,2,3,3,3,2,2,3,2,2,2
Data 1,1,1,1,2,3,2,3,3,2,2,2,2,2,2,0
Data 1,2,1,3,2,2,3,3,3,3,2,2,2,2,0,0
Data 0,0,2,2,3,3,3,3,3,3,3,2,3,2,2,0
Data 0,0,0,2,0,0,4,3,3,0,0,3,2,2,2,0
Data 0,0,0,0,0,0,4,3,3,0,0,0,3,2,0,0
Data 0,0,0,0,0,0,4,3,3,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,4,3,3,0,0,0,0,0,0,0
Data 0,0,0,0,9,9,4,3,3,9,9,0,0,0,0,0
Data 0,0,0,0,0,9,9,9,9,9,0,0,0,0,0,0
.tree4
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
.tree5
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0

; Soldiers
.soldierbase
Data 0,0,0,0,0,0,9,9,9,9,0,0,0,0,0,0
Data 0,0,0,0,0,9,9,9,9,9,9,0,0,0,0,0
Data 0,0,0,0,0,9,2,2,2,2,9,0,0,0,0,0
Data 0,0,0,0,0,2,2,2,2,2,2,0,0,0,0,0
Data 0,0,0,1,1,1,1,2,2,1,1,1,1,0,0,0
Data 0,0,0,1,1,1,1,1,1,1,1,1,1,0,0,0
Data 0,0,0,1,1,1,1,1,1,1,1,1,1,0,0,0
Data 0,0,0,1,1,2,2,2,1,1,1,1,1,0,0,0
Data 0,0,0,0,0,1,1,2,2,2,2,0,0,0,0,0
Data 0,0,0,0,0,1,1,1,1,1,1,0,0,0,0,0
Data 0,0,0,0,0,1,1,1,1,1,1,0,0,0,0,0
Data 0,0,0,0,0,1,1,0,0,1,1,0,0,0,0,0
Data 0,0,0,0,0,1,1,0,0,1,1,0,0,0,0,0
Data 0,0,0,0,0,1,1,0,0,1,1,0,0,0,0,0
Data 0,0,0,0,2,2,2,0,0,2,2,2,0,0,0,0
Data 0,0,0,2,2,2,2,0,0,2,2,2,2,0,0,0
; Soldiers
.soldierstanding1
Data 0,0,0,0,0,0,9,9,9,9,0,0,0,0,0,0
Data 0,0,0,0,0,9,9,9,9,9,9,0,0,0,0,0
Data 0,0,0,0,0,9,2,2,2,2,9,0,0,0,0,0
Data 0,0,0,0,0,2,2,2,2,2,2,0,0,0,0,0
Data 0,0,0,1,1,9,9,2,2,9,9,1,1,0,0,0
Data 0,0,0,1,1,9,9,9,9,9,1,1,1,0,0,0
Data 0,0,0,1,1,9,9,9,9,1,1,1,1,0,0,0
Data 0,0,0,1,1,2,2,2,9,1,1,1,1,0,0,0
Data 0,0,0,0,0,1,1,2,2,2,2,0,0,0,0,0
Data 0,0,0,0,0,1,1,1,1,1,1,0,0,0,0,0
Data 0,0,0,0,0,1,1,1,1,1,1,0,0,0,0,0
Data 0,0,0,0,0,1,1,0,0,1,1,0,0,0,0,0
Data 0,0,0,0,0,1,1,0,0,1,1,0,0,0,0,0
Data 0,0,0,0,0,1,1,0,0,1,1,0,0,0,0,0
Data 0,0,0,0,2,2,2,0,0,2,2,2,0,0,0,0
Data 0,0,0,2,2,2,2,0,0,2,2,2,2,0,0,0
