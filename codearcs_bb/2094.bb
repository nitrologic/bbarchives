; ID: 2094
; Author: Ben(t)
; Date: 2007-08-20 18:04:38
; Title: Random buildings
; Description: Random buildings

Type tower
	Field entity
	Field scale#
	Field x#,z#
	Field growth#
	Field shrink#
	Field time#
	Field timeout#
	Field move#
	Field reach#
	Field xsc#
	Field zsc#
End Type

Global tower=CreateCube()
EntityType tower,walls
EntityPickMode tower,2
HideEntity tower


TG#=TG#+1

If TG# > 15 Then

t.tower=New tower
	t\entity=CopyEntity (tower)
			t\x#=Rnd(-500,500)
			t\z#=Rnd(-500,500)
			t\xsc#=Rnd(25)
			t\zsc#=Rnd(25)
		t\growth#=Rnd(1)
		t\shrink#=Rnd(1)
		t\reach#=Rnd(1,200)
		t\timeout#=Rnd(250)
	TG#=0
EndIf


For t.tower=Each tower
	If t\time# < t\timeout# Then
	t\scale#=t\scale#+t\growth#
	Else
	t\scale#=t\scale#-t\shrink#
	EndIf
	
ScaleEntity t\entity,t\xsc#,t\scale#,t\zsc#
	PositionEntity t\entity,t\x#,t\scale#,t\z
	
	If t\scale# >= t\reach# Then t\time#=t\time#+1
	If t\scale# < 0 Then FreeEntity t\entity:Delete t
	
Next
