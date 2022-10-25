; ID: 2510
; Author: Gorley
; Date: 2009-06-16 15:31:42
; Title: Jelly
; Description: 3d model viewer with controls for all axis

Graphics3D 800,600,16
AppTitle "Jelly 3D model viewer"
.next_file
light=CreateLight(1)
cam=CreateCamera()




Print "Please type the file path of the 3D object you would like to view, "
Print "Only .b3d, .x or .3ds files supported "
Print"When finished viewing press 1 To view a New Object, or press escape to exit " 
Print "Use up and down arrows To change the pitch of the object "
Print "Use left and right keys to rotate the object clockwise and counterclockwise "
Print "Use the keys G And H to rotate in place, use the A and Z keys to zoom "
Print "Use Delete and Page Down to pan left and right,"
Print "Use Home and end to pan up and down"
Print "This is a product of Kamikaze! programming."


thing=LoadMesh(Input$())

PointEntity light,thing

While Not KeyDown(2)
ShowEntity thing

If KeyHit(1)
Goto finished
EndIf

z#=0
x#=0
y#=0


;left
If KeyHit(211)
x#=-1
EndIf

;Right
If KeyDown(209)
x#=1
EndIf

;up
If KeyDown(207)
y#=-1
EndIf

;down
If KeyDown(199)
y#=1
EndIf


roll#=0
yaw#=0
pitch#=0 

If KeyDown(208)
pitch#=1
EndIf

If KeyDown(200)
pitch#=-1
EndIf

If KeyDown(203)
roll#=1
EndIf

If KeyDown(205)
roll#=-1
EndIf

If KeyDown(44)
z#=.5
EndIf

If KeyDown(30)
z#=-.5
EndIf



If KeyDown(34)
yaw#=-1
EndIf 

If KeyDown(35)
yaw#=1
EndIf

TurnEntity thing,pitch#,yaw#,roll# 


MoveEntity thing,x#,y#,z#

RenderWorld

Flip
Wend
HideEntity thing
Goto next_file
.finished
RuntimeError "Thank You for using Jelly. Jelly is a product of Kamikaze! programming.
