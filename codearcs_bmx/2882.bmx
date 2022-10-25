; ID: 2882
; Author: EdzUp[GD]
; Date: 2011-08-21 11:56:01
; Title: Text like Blitz3d's :)
; Description: Draw text like Blitz3d's

Function Text( Sentence:String, X:Long, Y:Long, CenterX:Byte = False, CenterY:Byte = False )
	Local XPos:Long = X
	Local YPos:Long = Y
	
	If CenterX=True Then XPos :- ( TextWidth( Sentence ) /2 )
	If CenterY=True Then YPos :- ( TextHeight( Sentence ) /2 )
	
	DrawText Sentence, XPos, YPos
End Function
