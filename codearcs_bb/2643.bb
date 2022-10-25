; ID: 2643
; Author: mutantleg
; Date: 2010-01-21 17:44:26
; Title: Platform movement with slopes
; Description: for 3d platformer games

; slopes for 3d platformers
; by mutantleg (mutantleg@gmail.com)
; 2010 jan 21

Graphics3D 640,480,32,2


SetBuffer BackBuffer()
;--------------------------------------------

cam = CreateCamera()
MoveEntity cam, 0,15,-20
TurnEntity cam, 20, 0, 0

light = CreateLight(1)


wrld = CreateCube()
ScaleMesh wrld, 30, 1, 8
RotateMesh wrld, 0,0, 5
EntityType wrld, 2

wall = CreateCube()
ScaleMesh wall, 1, 10, 8
MoveEntity wall, 6, 0,0
EntityAlpha wall, 0.5
EntityType wall, 2
EntityColor wall, 64, 12, 128

bigslp = CreateCube()
ScaleMesh bigslp, 5, 5, 8
RotateMesh bigslp, 0, 0, 45
MoveEntity bigslp, -6, -3 ,0
EntityColor bigslp, 0, 128, 128
EntityType bigslp, 2

;make sure polygon picking mode is turned on as well for meshes to collide with 
EntityPickMode wrld, 2, True
EntityPickMode bigslp, 2, True
EntityPickMode wall, 2, True


p1 = CreateCube()
EntityType p1, 1
EntityColor p1, 255,0,0
MoveEntity p1, 0, 15,6


Collisions 1,  2, 2, 2


;you can often get in trouble because by default blitz thinks youre using integers
;so i just pre declare some floats first here
grav# = 0.0
ny# = 0.0
dist# = 0.0
onground = 0

;-------------------------------main loop start
Repeat
;--------------------------------------





;get ground vector
pickground = LinePick(EntityX(p1), EntityY(p1), EntityZ(p1), 0, -3, 0)

;align to ground vector if found ground
If pickground <> 0 Then 
	AlignToVector p1, PickedNX(), PickedNY(), PickedNZ(),2
Else
	AlignToVector p1, 0, 1, 0, 2
EndIf


;move left/right (A D or arrows)
;note there is still a error if you move into the wall from a slope climbing up
; (just move right without jumping to see what i mean)
;but linepicking before moving would might result in that you wont be able to climb some slopes
;so im not sure how to fix it

If KeyDown(32) Or KeyDown(205) Then
	 MoveEntity p1, 0.4,0,0
EndIf

If KeyDown(30) Or KeyDown(203) Then
	 MoveEntity p1, -0.4,0,0
EndIf


;jumping (hold space)

If KeyDown(57) Then
	If onground = 1 Then grav = 0.75
EndIf

;limit height of jump if space not held down
If onground = 0 And grav > 0 And (Not(KeyDown(57))) Then
	grav = grav * 0.75
EndIf



UpdateWorld()

MoveEntity cam, (EntityX(p1) - EntityX(cam)) * 0.1, 0 ,0 

;apply gravity
TranslateEntity p1, 0, grav, 0

;update now at gravity -- so this is one disadvantage of blitz and updating all movement data all at once
;as we need a seperate vector for gravity and movement to not stick to walls (comment out one updateworld to see what i mean)
UpdateWorld() 


;note this check is actually for disabling a sliding collision 
;if you want sliding only the    If grav > -1.0 grav = grav - 0.03   part is needed
;its made this way cuz i wanted to do surfaces with different sliding velocity but i gave up on that
;check if gravity needs to be changed (luckily picking doesnt wait for updateworld)
picked = LinePick(EntityX(p1), EntityY(p1), EntityZ(p1),  0, -10 + grav, 0)

dist# = EntityY(p1) - PickedY() 

If (dist > 1.5) ;dont increase gravity if the ground isnt below this distance -- this value is the radius of the object with some trial and error tweaking
	If grav > -1.0 grav = grav - 0.03
	onground = 0
Else
	onground = 1
EndIf
;


;test if hit ground -- need to check collision vector y -- aka don't reset gravity if we only hit a wall
If CountCollisions(p1) > 0
	For i = 1 To CountCollisions(p1)
		ent = CollisionEntity(p1, i)
		If GetEntityType(ent) = 2 Then
			ny = CollisionNY(p1, i) 
			;0 means a completely vertical wall and 1 means a completely horizontal ground
			;so changing this value is changing when to slide -- so with 0.1 you wont slide on very steep surfaces either
			If   ny > 0.3
				grav = 0
			EndIf
		EndIf ;gettype
	Next 
EndIf

RenderWorld()

Text 16, 16, "Picked: " + picked + " pickedy " + PickedY()
Text 16, 32, "Dist: " + dist + "  p1 y " + EntityY(p1)
Text 16, 48, "Grav: " +grav + " onground " + onground + " ny " + ny
;Text  16,16, "p2 pos: [" + ox +"] [" + oy + "] [" + oz + "] "
;Text  16,32, "p2 new pos: [" + EntityX(p2) +"] [" + EntityY(p2) + "] [" + EntityZ(p2) + "] "

Flip()

Until KeyHit(1)
End
;-------------------------------main loop end
