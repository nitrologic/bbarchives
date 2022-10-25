; ID: 1735
; Author: Eric
; Date: 2006-06-16 12:16:43
; Title: Centering Text
; Description: Centering Text

Function DrawText(N:String , X:Int , Y:Int , XO:Byte=False , YO:Byte=False)
	Local XL:Float = 0
	Local YL:Float = 0
	If XO = True Then XL = TextWidth(N) / 2
	
	If YO = True Then YL = TextHeight(N) / 2
	BRL.Max2D.DrawText(N , X - XL , Y - YL)
End Function
