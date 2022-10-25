; ID: 2646
; Author: blubberwasser
; Date: 2010-01-23 03:58:09
; Title: Multiline DrawText
; Description: Text

Function DrawMultilineText:Int( text:String, x:Int, y:Int, lineWidth:Int = 0, cWidth:Int = 0, cHeigth:Int = 0, autowrap:Byte = 0 )

	Local linePos:Int = 0
	Local textPos:Int = 0
	
	Local width:Int = 0
	Local height:Int = TextHeight( "A" )

	For Local pos:Int = 1 To text.length
	
		Local character:Byte = Asc( Mid( text, pos, 1 ) )
		
		width = TextWidth( Chr( character ) )
		
		If ( textPos / ( cWidth + 1 ) ) >= lineWidth And autowrap = 1
			linePos:+ 1
			textPos = 0
		EndIf
		
		If character = 10	'break
			linePos:+1
			textPos = 0	
		Else
			DrawText Chr( character ), x + textPos, y + linePos * ( height + cHeigth)
			textPos:+ ( width + cWidth )		
		EndIf
	
	Next
	
	Return linePos

EndFunction
