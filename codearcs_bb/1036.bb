; ID: 1036
; Author: slenkar
; Date: 2004-05-24 18:27:37
; Title: Simple 3D Mouselook
; Description: Easy mouselook for a 3D entity

Function Mouselook(entity)

my=MouseXSpeed()
mx=MouseYSpeed()
my=my*-1


TurnEntity entity,mx,my,0

End Function
