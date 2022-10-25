; ID: 1728
; Author: tonyg
; Date: 2006-06-07 07:38:26
; Title: Bin2dec
; Description: Binary string to decimal int

SuperStrict
Print bin2dec("010011100")
Function bin2dec:Int(binval:String)
	Local Val:Int = 1
	Local total:Int
	For Local x :Int = 0 To binval.length
		Local numval:Int = Int(Mid(binval , binval.length - x , 1) ) 
		If numval <> 0 And numval <> 1 RuntimeError "Non-binary input"
		total :+ Int(Mid(binval , binval.length - x , 1) ) * Val
'		debuglog Int(Mid(binval, binval.length - x , 1)) + " * " + val + " = " + total
		val = Val * 2
	Next
	Return total
End Function
