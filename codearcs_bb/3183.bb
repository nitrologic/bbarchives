; ID: 3183
; Author: Matty
; Date: 2015-01-29 19:09:12
; Title: Predict time of collision for two moving objects
; Description: A function for detecting the time when a collision will occur between two moving entities (maths only - no blitz specific 3d functions actually used)

;Author - Matt Lloyd 2015
;
;Function - whenwillicollide#()
;
;Used to determine the time at which a moving object (straight line) will collide with another moving object (straight line)
;
;The example (see example function) lets the user move a little 'spaceship' around the map by clicking on the screen, the AI unit
;will 'chase' and shoot at the player when they have a likely shot....of course - if the user changes direction then it will miss! (I'm not 
;a fortune teller!)
;
;
;Hopefully this comes in useful - I will find it useful anyway....
;




;test example of usage...
example()


;function definition..
Function whenwillicollide#(dimensions,posx#,posy#,posz#,velx#,vely#,velz#,targetposx#,targetposy#,targetposz#,targetvelx#,targetvely#,targetvelz#,mintime#,maxtime#,tolerance#=0.1)
;Parameters are:
;
;Dimensions is a value from 1 to 3....indicates the number of dimensions to use...
;For example - a 2d game would use '2', a 3d game would use '3', and I'm not sure why you would use less than 2 dimensions...but hey...
;
;
;Position of first entity x,y,z (floats)
;Velocity of first entity in x,y,z (floats)
;
;Position of second entity x,y,z (floats)
;Velocity of second entity in x,y,z (floats)
;
;Minimum time to accept (float)
;Maximum time to accept (float)
;
;Tolerance (float) - allowable difference between times in each coordinate system...optional value........
;
;Return values are:
;
;-1 if the vectors never intersect (either because they are skew lines or because they intersect outside of the allowable range or because they do not
;intersect on the same axis within the specified tolerance
;
;-2 if the mintime or maxtime are set incorrectly (ie less than zero or maxtime is less than mintime)
;
;-3 if the dimensions has an invalid value (must be either 2 or 3)
;
;
;Assumes two entities are NOT at the same location..ie not already collided!
;


Local intersectiontime#[2] ;array containing the intersection time of each coordinate....
;0 = x index, 1 = y index, 2 = z index

If(dimensions<2 Or dimensions>3) Then Return -3

If(mintime<0 Or maxtime<0 Or maxtime<mintime) Then Return -2


If(targetvelx - velx)=0  Then Return -1
intersectiontime[0] = (posx - targetposx) / (targetvelx - velx)
If(intersectiontime[0]<mintime Or intersectiontime[0]>maxtime) Then Return -1
If(targetvely - vely)=0 Then Return -1
intersectiontime[1] = (posy - targetposy) / (targetvely - vely)
If(intersectiontime[1]<mintime Or intersectiontime[1]>maxtime) Then Return -1
If (Abs(intersectiontime[1]-intersectiontime[0]))>tolerance Then Return -1
If(dimensions=2) Then Return (intersectiontime[0]+intersectiontime[1])*0.5
If(targetvelz - velz)=0 Then Return -1
intersectiontime[2] = (posz - targetposz) / (targetvelz - velz)
If(intersectiontime[2]<mintime Or intersectiontime[2]>maxtime) Then Return -1
If (Abs(intersectiontime[2]-intersectiontime[1]))>tolerance Then Return -1
If (Abs(intersectiontime[2]-intersectiontime[0]))>tolerance Then Return -1

Return (intersectiontime[0]+intersectiontime[1]+intersectiontime[2])*0.33333

End Function

Function example()
;
;
;2 dimensional example....

Local maxvel = 2.0,maxbulletvel = 4.0
Local playerX#,playerY#,playervelX#,playervelY#,playeraccelX#,playeraccelY#
Local enemyX#,enemyY#,enemyvelX#,enemyvelY#,enemyaccelX#,enemyaccelY#
Local enemybulletX#,enemybulletY#,enemybulletvelX#,enemybulletvelY#,enemybulletlife
Local startedchasing=0


Graphics 512,512,0,2

playerx = Rnd(256)+128
playery = Rnd(256)+128
playervelx = 0
playervely = 0
playeraccelx = 0
playeraccely = 0

enemyx = Rnd(256)+128
enemyy = Rnd(256)+128

enemybulletx = -1
enemybullety = -1
enemybulletlife = 0


SetBuffer BackBuffer()
Repeat

playerx = playerx + playervelx
playery = playery + playervely

playervelx = playervelx + playeraccelx
playervely = playervely + playeraccely

If(playervelx*playervelx+playervely*playervely)>maxvel*maxvel Then 
	playervelx = playervelx * maxvel/Sqr((playervelx*playervelx+playervely*playervely))
	playervely = playervely * maxvel/Sqr((playervelx*playervelx+playervely*playervely))
EndIf

enemyx = enemyx + enemyvelx
enemyy = enemyy + enemyvely

enemyvelx = enemyvelx + enemyaccelx
enemyvely = enemyvely + enemyaccely

If(enemyvelx*enemyvelx+enemyvely*enemyvely)>maxvel*maxvel Then 
	enemyvelx = enemyvelx * maxvel/Sqr((enemyvelx*enemyvelx+enemyvely*enemyvely))
	enemyvely = enemyvely * maxvel/Sqr((enemyvelx*enemyvelx+enemyvely*enemyvely))
EndIf

;move the bullet....
If(enemybulletlife>0) Then

enemybulletx = enemybulletx + enemybulletvelx
enemybullety = enemybullety + enemybulletvely
enemybulletlife = enemybulletlife - 1


EndIf


If(MouseDown(1)) Then ;player provides thrust towards point pressed by mouse....
	dx# = MouseX() - playerx
	dy# = MouseY() - playery
	length# = dx*dx+dy*dy
	If(length>0)
		length = Sqr(length)
		dx = 0.65*dx/length
		dy = 0.65*dy/length
		
		playeraccelx = dx
		playeraccely = dy
		startedchasing = 1
	EndIf

EndIf 

;enemy tracks player....and moves around chasing them......(after the player starts moving)
If(startedchasing=1) Then 
	dx# = playerx - enemyx
	dy# = playery - enemyy
	length# = dx*dx+dy*dy
	If(length>0)
		length = Sqr(length)
		dx = 0.55*dx/length
		dy = 0.55*dy/length
		
		enemyaccelx = dx
		enemyaccely = dy
		startedchasing = 1
	EndIf

	If(enemybulletlife<=0) Then 
		;;;see if we should fire a bullet.....
		
		;bullet starts from enemy pos
		enemybulletx = enemyx
		enemybullety = enemyy
		
		;bullet starts with same velocity as enemy (ie good old vector addition - assuming non relativistic speeds!
		enemybulletvelx = enemyvelx
		enemybulletvely = enemyvely
		
		
		dx# = playerx - enemybulletx
		dy# = playery - enemybullety
		length# = dx*dx+dy*dy
		If(length>0)
			length = Sqr(length)
			dx = dx / length
			dy = dy / length
			
			;bullet velocity will be faster than ships - it will be..twice as fast as the maxvel...
			enemybulletvelx = enemybulletvelx + dx
			enemybulletvely = enemybulletvely + dy
			
			If(enemybulletvelx*enemybulletvelx+enemybulletvely*enemybulletvely)>(maxbulletvel)*(maxbulletvel) Then
				enemybulletvelx = enemybulletvelx * (maxbulletvel) / Sqr((enemybulletvelx*enemybulletvelx+enemybulletvely*enemybulletvely))
				enemybulletvely = enemybulletvely * (maxbulletvel) / Sqr((enemybulletvelx*enemybulletvelx+enemybulletvely*enemybulletvely))
			EndIf 
			
			;okay but will we hit our target? if so then we want to fire...
			;now....the life of the bullet we will say is...60 frames....just an arbitrary number...can be anything....but most games I've written usually have a maximum 'lifetime' of the bullet (especially useful if it continues to fly off into deep space!)
			
			;this is where I call the function above - if it returns a non zero value then I want to fire!
			enemybulletlife = whenwillicollide(2,enemybulletx,enemybullety,0,enemybulletvelx,enemybulletvely,0,playerx,playery,0,playervelx,playervely,0,0,300,(maxbulletvel+maxvel+2))			

			If(enemybulletlife<0)
				enemybulletlife = 0
			Else
				;enemybulletlife = 60 ;(uncomment this and the bullet will only travel until it reaches where it should have hit the player at...)
			EndIf	
		EndIf
	EndIf 


EndIf
Cls
Color 0,255,0
Rect playerx-1,playery-1,3,3,1

Color 255,0,255
Rect enemyx-1,enemyy-1,3,3,1

Color 255,255,0
If(enemybulletlife>0) Then 
	Rect enemybulletx-1,enemybullety-1,3,3,1
EndIf
Flip

Until KeyDown(1)

End


End Function
