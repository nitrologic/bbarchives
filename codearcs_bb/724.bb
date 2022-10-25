; ID: 724
; Author: Rob
; Date: 2003-06-21 07:40:58
; Title: Object and Handle with Entities
; Description: Object and Handle to avoid looking up an entity in thousands of types

; Example on how to use object and handle
; to hold unlimited data about entities with
; very low lookup time.
; By Rob Cummings

Graphics3D 640,480,0,2

Type info
	Field name$,a,b,c,stuff,mass#,physics,etc
End Type

;create a ball and some related information.
a.info=New info
a\name$="bowling ball"
a\mass#=0.9
ball=CreateSphere()
NameEntity ball,Handle(a) ; the type handle held within the entity name.


;now all you do is grab the entity using any of Blitz's own commands.
;This could be LinePicks, Collisions And so forth.


;uncomment for a proper example within a collision framework
;ent = EntityCollided(bat,collisiontype_ball)
ent=ball ; test

If ent
	a.info = Object.info(EntityName(ent)) ; quickly retrieve source type
	Print a\name$
	Print a\mass#
EndIf
