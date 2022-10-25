; ID: 3161
; Author: Pakz
; Date: 2014-11-12 22:28:41
; Title: 3D homing missiles
; Description: Homing missile example - moves a swarm of cubes towards 3d targets

; 3d Homing missile example by Pakz (Rudy van Etten)

Graphics3D 640,480 
SetBuffer BackBuffer() 
SeedRnd MilliSecs()

Global CamPivot = CreatePivot( )					;create camera pivot
Global Camera = CreateCamera( )						;create camera with CamPivot as parent

Global FlyMode=1									;if 1 then flymode is on
Global WalkSpeed#=.5								;this handled the walking motion
Global Jumped=0										;Jump check

camera=CreateCamera() 
PositionEntity camera,0,0,-120

light=CreateLight() 
RotateEntity light,90,0,0 

Type cube
	Field c
	Field sp,ep
	Field dx#,dy#,dz#
	Field angle1,angle2
End Type



; Make the homing cubes
For i=0 To 40
this.cube = New cube
this\angle1 = Rand(-180,180)
this\angle2 = Rand(-180,180)
this\c = CreateCube()
this\sp = CreateCube()
this\ep = CreateCube()
EntityColor this\c,255,0,0
EntityColor this\sp,0,255,0
EntityColor this\ep,0,0,255
PositionEntity this\c,Rand(-50,50),Rand(-50,50),Rand(-50,50)
PositionEntity this\sp,EntityX(this\c),EntityY(this\c),EntityZ(this\c)
this\dx = Rand(-50,50)
this\dy = Rand(-50,50)
this\dz = Rand(-50,50)
PositionEntity this\ep,this\dx,this\dy,this\dz
HideEntity this\sp
HideEntity this\ep
Next

timer = CreateTimer(60)
While Not KeyDown( 1 ) 
	WaitTimer timer

	For this.cube = Each cube
		; Here we get the angles where we need to go
		a = getangle(EntityX(this\ep),EntityY(this\ep),EntityX(this\c),EntityY(this\c))
		b = getangle(EntityY(this\ep),EntityZ(this\ep),EntityY(this\c),EntityZ(this\c))
		


		; a1 wil count down until it reaches the target angle.
		; it starts at the current missile angle
		a1 = this\angle1
		; v1 adds up 1 every step. it is used to see if left v1 or right v2 is bigger
		v1 = 0
		exitloop = False
		While exitloop = False
			a1 = a1 - 1
			v1 = v1 + 1
			; if near target angle
			If RectsOverlap(a1,a1,4,4,a,a,4,4) Then exitloop = True
			; boundries
			If a1 =< -180 Then a1 = 181	
		
		Wend
		exitloop = False
		a1 = this\angle1
		v2 = 0
		While exitloop = False
			a1 = a1 + 1
			v2 = v2 + 1
			If KeyDown(57) Then DebugLog a1
			If RectsOverlap(a1,a1,4,4,a,a,4,4) Then exitloop = True
			If a1 >= 180 Then a1 = -181
		Wend
		; If go left is shorter turn then decrease angle by value else increase
		If v1 > v2 Then this\angle1 = this\angle1 - 3 Else this\angle1  = this\angle1  + 3
		; bounds
		If this\angle1  > 180 Then this\angle1  = -180
		If this\angle1  < -180 Then this\angle1  = 180
	





		; a1 wil count down until it reaches the target angle.
		; it starts at the current missile angle
		a1 = this\angle2
		; v1 adds up 1 every step. it is used to see if left v1 or right v2 is bigger
		v1 = 0
		exitloop = False
		While exitloop = False
			a1 = a1 - 1
			v1 = v1 + 1
			; if near target angle
			If RectsOverlap(a1,a1,4,4,b,b,4,4) Then exitloop = True
			; boundries
			If a1 =< -180 Then a1 = 181	
		
		Wend
		exitloop = False
		a1 = this\angle2
		v2 = 0
		While exitloop = False
			a1 = a1 + 1
			v2 = v2 + 1
			If KeyDown(57) Then DebugLog a1
			If RectsOverlap(a1,a1,4,4,b,b,4,4) Then exitloop = True
			If a1 >= 180 Then a1 = -181
		Wend
		; If go left is shorter turn then decrease angle by value else increase
		If v1 > v2 Then this\angle2 = this\angle2 - 3 Else this\angle2  = this\angle2  + 3
		; bounds
		If this\angle2  > 180 Then this\angle2  = -180
		If this\angle2  < -180 Then this\angle2  = 180











		
		; here we move the cube into the direction it should go
		MoveEntity this\c,Cos(this\angle1)/4,Sin(this\angle1)/4,Sin(this\angle2)/4

		; if close by then new destination
		If RectsOverlap(EntityX(this\c),EntityY(this\c),10,10,EntityX(this\ep),EntityY(this\ep),10,10)
			If RectsOverlap(EntityZ(this\c),0,10,10,EntityZ(this\ep),0,10,10)
			PositionEntity this\ep,Rand(-50,50),Rand(-50,50),Rand(-50,50)
			PositionEntity this\sp,EntityX(this\c),EntityY(this\c),EntityZ(this\c)
			End If
		End If				
	Next

	TurnEntity CamPivot, 0, 0 -MouseXSpeed(), 0		;left/right rotation
	TurnEntity Camera, MouseYSpeed(), 0, 0			;up/down rotation
	RotateEntity CamPivot, EntityPitch#( CamPivot ), EntityYaw#( CamPivot ), 0	;z roll correction
	MoveMouse GraphicsWidth()/2, GraphicsHeight()/2	;move mouse pointer to center of screen

	RotateEntity Camera, EntityPitch#( Camera ), EntityYaw#( CamPivot ), 0		;Z roll correction

	If KeyDown( 54 ) =1 							;Walk key (Right Shift)
		WalkSpeed# = .05
	Else
		WalkSpeed# = .5
	EndIf

	If KeyDown( 17 ) =1 Then MoveEntity Camera, 0, 0, WalkSpeed#
	If KeyDown( 208 ) =1 Then MoveEntity Camera, 0, 0, 0-WalkSpeed#
	PositionEntity CamPivot, EntityX#( Camera ), EntityY#( Camera ), EntityZ#( Camera )
	If KeyDown( 203 ) =1 Then MoveEntity CamPivot, -1, 0, 0
	If KeyDown( 205 ) =1 Then MoveEntity CamPivot, 1, 0, 0

	; ******* END OF GRAVITY CHECK ************
	TranslateEntity CamPivot, 0, PlayerGravity#, 0	;move camera pivot according to current gravity force
	
	RenderWorld 
	Text 0,0,"Use mouse to look and use w to move forward" 
Flip 
Wend 

End  


Function getangle#(x1#,y1#,x2#,y2#)
         Local dx# = x2 - x1
         Local dy# = y2 - y1
         Return ATan2(dy,dx)+360 Mod 360
End Function
