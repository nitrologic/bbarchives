; ID: 1432
; Author: Nicstt
; Date: 2005-08-02 07:46:44
; Title: Look for duplication
; Description: searches through a list of numbers for a duplicate number

check = 0
multiplier# = ((Float(TILES_Pos) + Float(TILES_Neg)) / 2.0) - 0.5
mult# = multiplier# * Float(howManyTilesX(0))
Repeat
     For a = 1 To howManyTilesX(0)
	For b = a+1 To howManyTilesX(0)
		If howManyTilesX(a)	= howManyTilesX(b) And howManyTilesY(a) = howManyTilesY(b) 
             		Repeat
				random = Rnd(0,16)
			Until random > 0 And random < 16
			c = Rnd(0,1)
			If c = 1
				howManyTilesX(b) = random
			Else
				howManyTilesY(b) = random
			EndIf
		Else
			check = check + 1
		EndIf
	Next
     Next
     If check <> Int mult# Then check = 0
Until check = Int mult#
