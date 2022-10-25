; ID: 555
; Author: Shambler
; Date: 2003-01-19 08:10:47
; Title: Entity Chase/Flee
; Description: Makes an entity follow or run away from another

;Chase
;assumes earlier 'target=CreatePivot(Monster)'
;Run towards Player until they are 10 units away


If EntityDistance(Monster,Player)>10
PointEntity target,Player
targetangle#=EntityYaw(target,True)
currentangle#=EntityYaw(Monster)

If Abs(currentangle#-targetangle#)<180 Then turn=2 Else turn=-2 

If currentangle#<targetangle# 
TurnEntity Monster,0,turn,0 
Else
If currentangle>targetangle
TurnEntity Monster,0,-turn,0
EndIf
EndIf
MoveEntity Monster,0,-0.5,.5 
EndIf



;Flee
;assumes earlier 'target=CreatePivot(Monster)'
;Run away from Player if they are closer than 100 units

If EntityDistance(Monster,Player)<100
PointEntity target,Player
targetangle#=EntityYaw(target,True) 
currentangle#=EntityYaw(Monster)

If Abs(currentangle#-targetangle#)<180 Then turn=-2 Else turn=2 

If currentangle#<targetangle# 
TurnEntity Monster,0,turn,0 
Else
If currentangle>targetangle
TurnEntity Monster,0,-turn,0
EndIf
EndIf
MoveEntity Monster,0,-0.5,.5 
EndIf
