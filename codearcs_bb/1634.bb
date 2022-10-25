; ID: 1634
; Author: mindstorms
; Date: 2006-03-05 19:07:26
; Title: Koals
; Description: demonstrates bouncing balls

Type point2
	Field x#,y#
	Field angle#
	Field xvel#,yvel#
	Field xthrust#,ythrust#,crash#
	Field image
End Type

Type point1
	Field angle#
	Field x#,y#
	Field x_velocity#, y_velocity#
	Field crash#,thrustx#,thrusty#
	Field image
End Type





Local speed# = 1
Local number_enemies = 1

Const change# = .2
Const vel# = 1.5

Graphics 800,600, 0, 2
SetBuffer BackBuffer()

AutoMidHandle(True)


Cls:Flip




font = LoadFont("arial",24)
SetFont font

Locate GraphicsWidth()/3,GraphicsHeight()/2
Color 255,255,255
number_enemies = Input("How many enemies? ")


SeedRnd MilliSecs()


Global Pos.point1 = New point1; Current position.

Pos\x = GraphicsWidth()/2: Pos\y = GraphicsHeight()/2
Pos\crash = 0
Pos\x_velocity = 0 : Pos\y_velocity = 0
Pos\image = LoadImage("blue dot.bmp")			;edit this out if you don't have images

For i = 1 To number_enemies
enemy.point2 = New point2 ; enemy position
Repeat
enemy\x = Rand(1,GraphicsWidth()): enemy\y = Rand(1,GraphicsHeight())
Until Not (enemy\x + 5 < Pos\x-10 And enemy\y + 5 < Pos\y-10 And enemy\x - 5 > Pos\x+10 And enemy\y - 5 > Pos\y+10)


enemy\image = LoadImage("red dot.bmp")			;edit this out if you don't have images
enemy\crash = False
Next




Repeat:Flip:Cls

update_player()
	
update_computer()



If Pos\crash = 1 Then 
	counter = counter + 1 
EndIf
If counter = 100 Then
	counter = 0 
	Pos\crash = 0
EndIf




Until KeyHit(1)
End


Function update_player()
Local old_x = Pos\x
Local old_y = Pos\y 
Local old_xv = Pos\x_velocity
Local old_yv = Pos\y_velocity

Pos\thrustx = 0
Pos\thrusty = 0

If Pos\crash = 1 Then 

If KeyDown(203) Then Pos\thrustx = -2 
If KeyDown(200) Then Pos\thrusty = -2
If KeyDown(208) Then Pos\thrusty = 2 
If KeyDown(205) Then Pos\thrustx = 2 




If Pos\thrustx > Pos\x_velocity Then Pos\x_velocity = Pos\x_velocity + change
If Pos\thrustx < Pos\x_velocity Then Pos\x_velocity = Pos\x_velocity - change
If Pos\thrusty > Pos\y_velocity Then Pos\y_velocity = Pos\y_velocity + change
If Pos\thrusty < Pos\y_velocity Then Pos\y_velocity = Pos\y_velocity - change



If Pos\x_velocity = 0 And Pos\y_velocity = 0 Then 	Pos\crash = 0 


Else 	;if crash = 0
	Pos\x_velocity = 0
	Pos\y_velocity = 0

	
	If KeyDown(203) Then Pos\x_velocity = -2 
	If KeyDown(200) Then Pos\y_velocity = -2
	If KeyDown(208) Then Pos\y_velocity = 2 
	If KeyDown(205) Then Pos\x_velocity = 2
	
	If Pos\x_velocity <> 0 Or Pos\y_velocity <> 0 Then Pos\crash = 1

EndIf
	
	
Pos\x = Pos\x + Pos\x_velocity
Pos\y = Pos\y + Pos\y_velocity

If Pos\x + 10 > GraphicsWidth() Or Pos\x - 10 < 0 Or Pos\y + 10 > GraphicsHeight() Or Pos\y - 5 < 10 Then 
	player_lose()
EndIf


For enemy_dot.point2 = Each point2
size = 10
dx#=(enemy_dot\x-Pos\x)
dy#=(enemy_dot\y-Pos\y)
		
		distance#=Sqr(dx*dx+dy*dy)
		If distance<size+5
			dx=dx/distance
			dy=dy/distance
			dz=dz/distance
			
			Pos\x_velocity = -dx*2.5*vel
			Pos\y_velocity = -dy*2.5*vel
			enemy_dot\xvel = dx*10*vel
			enemy_dot\yvel = dy*10*vel
			
			Pos\crash = 1	
		EndIf
Next


DrawImage(Pos\image,Pos\x,Pos\y); Draw sprite	(edit out if you don't have images)
;Color 0,0,255		;put in if you don't have images
;Oval Pos\x-10,Pos\y-10,20,20	;put in if you don't have images

End Function



Function update_computer()
Local counter = 0
For enemy_dot.point2 = Each point2
	
	counter = counter + 1

		size = 10
		dx#=(Pos\x-enemy_dot\x)
		dy#=(Pos\y-enemy_dot\y)
				

		
		distance#=Sqr(dx*dx+dy*dy)
		
		
		dx=dx/distance
		dy=dy/distance
		
		
		enemy_dot\xthrust = dx*vel
		enemy_dot\ythrust = dy*vel
	
		For enemydot.point2=Each point2
			

			If enemydot<>enemy_dot
			

				dx#=(enemydot\x-enemy_dot\x)
				dy#=(enemydot\y-enemy_dot\y)
				

				
				distance=Sqr(dx*dx+dy*dy)
				
				

				
				If distance<size
					
					
					
					dx=dx/distance
					dy=dy/distance
	
					enemy_dot\xvel = -dx*5*vel
					enemy_dot\yvel = -dy*5*vel
					enemydot\xvel = dx*5*vel
					enemydot\yvel = dy*5*vel

					
				EndIf
					
			EndIf
			
		Next
		
		dx#=(Pos\x-enemy_dot\x)
		dy#=(Pos\y-enemy_dot\y)
		
		distance#=Sqr(dx*dx+dy*dy)
		If distance<size+5
			dx=dx/distance
			dy=dy/distance
		
			
			enemy_dot\xvel = -dx*5*vel
			enemy_dot\yvel = -dy*5*vel
			Pos\x_velocity = dx*2.5*vel
			Pos\y_velocity = dy*2.5*vel	
		EndIf
		
	If 	enemy_dot\crash = 1 Then 
		If enemy_dot\xthrust > enemy_dot\xvel Then enemy_dot\xvel = enemy_dot\xvel + change
		If enemy_dot\xthrust < enemy_dot\xvel Then enemy_dot\xvel = enemy_dot\xvel - change
		If enemy_dot\ythrust > enemy_dot\yvel Then enemy_dot\yvel = enemy_dot\yvel + change
		If enemy_dot\ythrust < enemy_dot\yvel Then enemy_dot\yvel = enemy_dot\yvel - change
		
		If enemy_dot\xvel = 0 And enemy_dot\yvel = 0 Then enemy_dot\crash = 0
	Else
		enemy_dot\xvel = enemy_dot\xthrust
		enemy_dot\yvel = enemy_dot\ythrust
		
		If enemy_dot\xvel <> 0 Or enemy_dot\yvel <> 0 Then enemy_dot\crash = 1
	EndIf
	
	
	enemy_dot\x = enemy_dot\x + enemy_dot\xvel
	enemy_dot\y = enemy_dot\y + enemy_dot\yvel
		
	DrawImage enemy_dot\image,enemy_dot\x,enemy_dot\y		;edit out if you don't have images
	
	;Color 255,0,0 	;put on if you don't have images
	;Oval enemy_dot\x-5,enemy_dot\y-5,10,10		;put on if you don't have images
		
		If enemy_dot\x+5 > GraphicsWidth() Or enemy_dot\x-5 < 0 Or enemy_dot\y+5 > GraphicsHeight() Or enemy_dot\y-5 < 0 Then 
			FreeImage enemy_dot\image
			Delete enemy_dot
			
		EndIf
				
	

Next
If counter = 0 Then 
	Player_wins()
EndIf
End Function


Function player_lose() 
		Color 0,0,255
		font = LoadFont("arial",48)
		SetFont(font)
		
		Cls:Flip
		Text GraphicsWidth()/2,GraphicsHeight()/2,"You Lose",1,1
		Flip
		
		Delay 2000
		
		For enemy_dot.point2 = Each point2
		FreeImage enemy_dot\image
			Delete enemy_dot
		Next
		
		FreeImage Pos\image
		Delete Pos
		End
	
End Function

Function Player_wins() 
		Color 0,0,255
		font = LoadFont("arial",48)
		SetFont(font)
		
		Cls:Flip
		Text GraphicsWidth()/2,GraphicsHeight()/2,"You Win",1,1
		Flip
		
		Delay 2000
		
		For enemy_dot.point2 = Each point2
		FreeImage enemy_dot\image
			Delete enemy_dot
		Next
		
		FreeImage Pos\image
		Delete Pos
		End
	
End Function
