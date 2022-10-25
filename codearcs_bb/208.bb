; ID: 208
; Author: SopiSoft
; Date: 2002-01-29 09:42:06
; Title: Distance between 2 entities in 3D space for collision!
; Description: This function checks the distance between 2 entities for collision!

; distance(entity1, entity2, distance#)
;
; Parameters:
;
; entity1 - entity1 handle
; entity2 - entity2 handle
; distance# - The distance between entity1 and entity2
;
; Description:
;
; Returns True if the distance between entity1 and entity2 is smaller or equals the distance-parameter provided. 
;

Function distance(entity1,entity2,distance#)
If Sqr#((EntityX#(entity1) - EntityX#(entity2))^2 + (EntityY#(entity1) - EntityY#(entity2))^2 + (EntityZ#(entity1) - EntityZ#(entity2))^2)<= distance#
  Return True
Else
  Return False
EndIf
End Function
