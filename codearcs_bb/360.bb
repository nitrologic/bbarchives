; ID: 360
; Author: semar
; Date: 2002-07-03 04:06:44
; Title: Smart Turn
; Description: Turns An Entity Toward Another Choosing The Short Path

;=================================================
Function where_to_turn(source_pivot,target_pivot) 
;=================================================
;parameters:
;   source_pivot:  is the source pivot that we want to turn toward a target pivot
;   target_pivot:  is a target pivot where we want the source pivot to rotate to

;Returned values:
;   0  : no turn needed
;   1  : turn left
;   -1 : turn right

temp = Createpivot() ;first, create a temp entity on the source pivot

;position the temp pivot at the source pivot position
PositionEntity temp,EntityX(source_pivot),EntityY(source_pivot),EntityZ(source_pivot)

PointEntity temp,target_pivot ;turns the temp pivot to the target pivot
;now temp pivot has the yaw that the source should have

;memo start angle and end angle, I just consider the integer parts of it, using Floor
s = Floor(EntityYaw(source_pivot)) ;this is the start yaw angle, that is, the current yaw orientation of the source
t = Floor(EntityYaw(temp))         ;this is the end angle, that is, the angle we should reach

FreeEntity (temp)  ;release the temp entity, we do not need it from now

If s = t Then ;if the two angles are the same we do not need any rotation !
	Return 0
EndIf

;the angle goes from 0,180 and 0,-180; now I normalize to 0-360
If s < 0 Then s = 360 + s
If t < 0 Then t = 360 + t

;now we found the right direction where to turn, in order to choose the shortest path:

;check if the difference is greather than 180
If Abs(s-t) > 180 Then
	
	;check if the start angle is greater than the target angle
	If s > t Then
		Return 1 ;turn left
	Else
		Return -1 ;turn right
	EndIf
Else
	;check if the start angle is greater than the target angle
	If s > t Then
		Return -1 ;turn right
	Else
		Return  1 ;turn left
	EndIf

EndIf

End Function
