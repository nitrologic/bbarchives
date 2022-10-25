; ID: 1375
; Author: Booticus
; Date: 2005-05-15 02:22:36
; Title: Roman Numerals
; Description: Function to convert an INT to Roman Numerals

'***********************************************************************
'
'		         * * * * README * * * *
'			 **********************
'   Thanks for use this program. The use and distribution of this source
'code is totally FREE.
'   
'Manuel F Martínez
'Universidad Galileo
'Ingeniería en Electrónica, Informática y
'Ciencias de la Computación
'Guatemala Abril del 2001
'
' BlitzMax conversion by Mario Roberti. Enjoy!
' 5/14/05

' Print a text roman numeral, 1972 for example
print romannumeral(1972) ' Insert your number from 1 to 3000 in there!

Function romannumeral:String(arabicalNumeral:Int)
	Local result:Int              ' "result" store the position value           */
	Local roman:String
	' Obtain the value of thousands */
	If ((arabicalNumeral <= 3000) And (arabicalNumeral >= 1000))
		result = arabicalNumeral / 1000
		roman=roman+ a2roman(result, "M", " ", " ")
		arabicalNumeral :- (result * 1000)
	EndIf
	' Obtain the value of hundreds */
	If ((arabicalNumeral < 1000) And (arabicalNumeral >= 100))
		result = arabicalNumeral / 100
		roman=roman+a2roman(result, "C", "D", "M")
		arabicalNumeral :- (result * 100)
	EndIf
	' Obtain the value of tens */
	If ((arabicalNumeral < 100) And (arabicalNumeral >= 10))
		result = arabicalNumeral / 10
		roman=roman+a2roman(result, "X", "L", "C")
		arabicalNumeral :- (result * 10)
	EndIf
	' Obtain the value of units */
	If ((arabicalNumeral < 10) And (arabicalNumeral >= 1))
		roman=roman+a2roman(arabicalNumeral, "I", "V", "X")
	EndIf
	Return roman
End Function

Function a2roman:String(value:Int,c1:String,c2:String,c3:String)
	Local i:Int 	' "i" is the index of the iteration */
	Local rroman:String=""
	' If value = 1, 2, 3 */
	If ((value >= 1) And (value <= 3))
		For i = 1 To value
			rroman=rroman+c1
		Next 
	EndIf
	' If value = 5, 6, 7, 8 */
	If ((value >= 5) And (value <= 8))
		rroman=rroman+c2
		For i = 1 To value-5
			rroman=rroman+c1
		Next
	EndIf
	' If value = 4 */
	If (value = 4)
		rroman=rroman+c1
		rroman=rroman+c2
	EndIf
	' If value = 9 */
	If (value = 9)
		rroman=rroman+ c1
		rroman=rroman+ c3
	EndIf
	Return (rRoman)
End Function
