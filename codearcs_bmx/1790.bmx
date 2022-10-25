; ID: 1790
; Author: dmaz
; Date: 2006-08-20 19:44:00
; Title: Number Format
; Description: takes a number and outputs a string with decimal placement, commas and left padding

Print Format(123456789:Double,2,3,14)
Print
Print Format(1234567891.23:Double,2,3,14)
Print Format(12345678.9123:Double,2,3,14)
Print Format(123456.789123:Double,2,3,14)
Print
Print Format(123456789123:Double,2,0,14)
Print Format(123456789123:Double,2,3,14)

Function Format:String( number:Double, decimal:Int=4, comma:Int=0, padleft:Int=0 )
	Assert decimal > -1 And comma > -1 And padleft > -1, "Negative decimal,comma or padleft not allowed in Format()"

	Local str:String = number
	Local dl:Int = str.Find(".")
	If decimal = 0 Then decimal = -1
	str = str[..dl+decimal+1]
	If comma
		While dl>comma
			str = str[..dl-comma] + "," + str[dl-comma..]
			dl :- comma
		Wend
	EndIf
	If padleft
		Local paddedLength:Int = padleft+decimal+1
		If paddedLength < str.Length Then str = "Error"
		str = RSet(str,paddedLength)
	EndIf
	Return str
End Function
