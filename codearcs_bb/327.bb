; ID: 327
; Author: Chroma
; Date: 2002-05-24 08:53:48
; Title: Return Entity Angle 0-360
; Description: This takes an entity's yaw and converts it to the correct 0-360 component.

;-------------------------------------;
;-=Return Entity Angle from 0 to 360=-;
;-------------------------------------;
Function Angle360(entity)
     Local TempYaw
     TempYaw=EntityYaw(entity)
     If TempYaw<0 Then TempYaw=TempYaw+360
     Return TempYaw
End Function
