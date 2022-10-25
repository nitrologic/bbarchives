; ID: 147
; Author: Milky Joe
; Date: 2002-01-08 08:58:28
; Title: Calculate Sound Pan
; Description: Calculates the pan value (-1 to 1) for a given screen position

Function CalculateSoundPan#(iX%)

; Returns a value in the range -1 to 1 representing "iX" on the screen

	Return 2 * iX / Float(GraphicsWidth - 1) - 1

End Function
