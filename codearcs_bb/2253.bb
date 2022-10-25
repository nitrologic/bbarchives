; ID: 2253
; Author: The_Black_Knight
; Date: 2008-05-14 15:59:02
; Title: Ai
; Description: A Good Ai Script

; Ai
; ------------------
;Made By Nathanael Senn - The Black Knight
;-------------------

;Set Graphics
Graphics3D 1024,768,32,1
SetBuffer BackBuffer()

; Create the timer to 60 fps
frameTimer=CreateTimer(60)

;Make Player Cam
camera=CreateCamera()

;Make light
light=CreateLight()
RotateEntity light,90,0,0

;Set types
enemy_type = 1
box_type   = 2
allie_type = 3

;Make enemy
enemy=CreateSphere()
EntityType enemy,enemy_type

;Make Allie
allie=CreateSphere()
EntityType allie,allie_Type
PositionEntity allie, 130, 0, 130

; Create cubes
hun=100
Dim cube(hun)

For box = 1 To 100

    If x >= 100
        x=10
        z=z+10
    EndIf
    
    x=x+10
    cube(box)=CreateCube()
    EntityType cube(box),box_type
    PositionEntity cube(box),x,0,z
    
Next

;Make ran points
nine = 9
Dim ranpoint(nine)

For r=1 To 9

    If r = 1
        x = 120
        z = 120
    EndIf
    
    If r = 2
        x = 120
        z = 55
    EndIf
    
    If r = 3
        x = 120
        z = 0
    End If

    If r = 4
        x = 55
        z = 120
    End If

    If r = 5
        x = 55
        z = 55
    EndIf

    If r = 6
        x = 55
        z = 0
    EndIf

    If r = 7
        x = 0
        z = 120
    EndIf
    
    If r = 8
        x = 0
        z = 55
    EndIf
    
    If r = 9
        x = 0
        z = 0
    End If
    
    ranpoint(r) = CreateCone()
    PositionEntity ranpoint(r), x, 0, z
    
Next

;Set enemy move
moveenemy = 0
moveallie = 0

;Set Collisions
Collisions enemy_type,box_type,2,2
Collisions allie_type,box_type,2,2

;Main Loop
While Not KeyDown( 1 )

;Set enemy and allie radius
EntityRadius enemy ,1
EntityRadius allie ,1

;Set fps to 60
WaitTimer(frameTimer) ; Pause until the timer reaches 60
Cls

;Add 1 to ran timer
entime = entime + 1
altime = altime + 1

;Enemy ai
If moveenemy = 1
    MoveEntity enemy, 0, 0, .3
EndIf

If moveenemy = 0
    e = Rnd (1,9)
    PointEntity enemy,ranpoint(e)
    moveenemy = 1
EndIf

For rote=1 To 100

    If entime >=25
        PointEntity enemy,ranpoint(e)
    EndIf

Next

If EntityDistance (enemy, ranpoint(e)) <= 1
    moveenemy = 0
EndIf

For rote=1 To 100
    
    If EntityDistance (enemy, cube(rote)) <=2
        rot = Rnd(1,10)
        entime = 0
    
    If rot <= 5
        TurnEntity enemy, 0,45,0
    EndIf
    
    If rot >= 6
        TurnEntity enemy, 0,-45,0
    EndIf
    
    EndIf

Next

;Allie ai
If moveallie = 1
    MoveEntity allie, 0, 0, .3
EndIf

If moveallie = 0
    r = Rnd (1,9)
    PointEntity allie,ranpoint(r)
    moveallie = 1
EndIf

For rote=1 To 100

    If altime >=25
        PointEntity allie,ranpoint(r)
    EndIf

Next

If alchase = 1 And altime >=25
    PointEntity allie,enemy
EndIf

If EntityDistance (allie, enemy) <=10
    moveallie = 3
EndIf  

If EntityDistance (allie, enemy) <=20 And EntityDistance (allie, enemy) >= 10 And EntityVisible ( allie,enemy )
    alchase = 1
    moveallie = 1
EndIf

If EntityDistance (allie, ranpoint(r)) <= 1
    moveallie = 0
EndIf

For rote=1 To 100
    
    If EntityDistance (allie, cube(rote)) <=2
        rot = Rnd(1,10)
        altime = 0
    
    If rot <= 5
        TurnEntity allie, 0,45,0
    EndIf
    
    If rot >= 6
        TurnEntity allie, 0,-45,0
    EndIf
    
    EndIf

Next

;Player movement
If KeyDown( 200 )=True Then MoveEntity camera,0,0,1
If KeyDown( 208 )=True Then MoveEntity camera,0,0,-1
If KeyDown( 205)=True Then yaw#=yaw#-1
If KeyDown( 203 )=True Then yaw#=yaw#+1
If KeyDown( 45 )=True Then roll#=roll#-1
If KeyDown( 44 )=True Then roll#=roll#+1

;Mouse look
myspd#=MouseYSpeed()*0.1
mxspd#=MouseXSpeed()*.1
MoveMouse GraphicsWidth()/2,GraphicsHeight()/2
yaw#=yaw#-mxspd#
pitch#=pitch#+myspd#
RotateEntity camera,pitch#,yaw#,roll#

UpdateWorld
RenderWorld
Text 0,20,"Roll: "+EntityRoll#( enemy )
Text 0,35,"Roll: "+EntityYaw#( enemy )
Text 0,50,"Roll: "+EntityPitch#( enemy )
Flip

Wend

End
