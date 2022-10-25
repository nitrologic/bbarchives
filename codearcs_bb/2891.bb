; ID: 2891
; Author: Spencer
; Date: 2011-10-02 23:34:58
; Title: BlitzMax Big Numbers
; Description: Add and Subtract Big Numbers

'-------------------------------------------------
'Example Program

	Global x:String = "0"
	Global y:String = "0"
	Global R:String = "0"
	
	x = "9999999999999999999999999999999999999"
	y = "1"
	R = BigNumbers.Add(x,y)
	Print R
	
	x = "11900"
	y = "11800"
	R = BigNumbers.Subtract(x,y)
	Print R
	
End
'-------------------------------------------------



'***************************************************************************************************************
'BigNumbers 
'***************************************************************************************************************
Type BigNumbers	

	Const EnumCompareResultLessThan = -1
	Const EnumCompareResultEqualTo = 0
	Const EnumCompareResultGreaterThan = 1

	Function GetNinesComplement:String(Value:String,MinuendDigitCount:Int)
		Local Result:String
		For Local CharPos:Int = 0 To Value.Length-1  Step 1
			Result = Result + Trim(String( 9-Int(Chr(Value[CharPos]))) )	
		Next
		While Result.Length < MinuendDigitCount
			Result = "9" + Result
		Wend
		Return Result
	EndFunction

	Function StringPadLeft:String(SourceValue:String,Value:String,Count:Int)
		For Local x:Int = 1 To Count Step 1
			SourceValue = Value + SourceValue
		Next
		Return SourceValue	
	EndFunction
	
	Function StripLeadingZeros:String(Value:String)
		For Local CharPos:Int = 0 To Value.Length-1 Step 1
			If( Chr(Value[CharPos]) <> " " ) And ( Chr(Value[CharPos]) <> "0" ) Then
				Return Trim(Value[CharPos..])
			EndIf
		Next
		Return "0" 'value was all spaces and/or zeros
	EndFunction
	
	Function CompareNumbers:Int(Number1:String,Number2:String)
		Local Difference:Int = Number2.Length - Number1.Length
		Local Number1Padded:String = BigNumbers.StringPadLeft(Number1,"0",(Difference>0)*Difference) 
		Local Number2Padded:String = BigNumbers.StringPadLeft(Number2,"0",(Difference<0)*(Abs(Difference))) 
		Local Digit1:Byte
		Local Digit2:Byte
		For Local CharPos:Int = 0 To Number1Padded.Length-1 Step 1
			Digit1 = Int(Chr(Number1Padded[CharPos]))
			Digit2 = Int(Chr(Number2Padded[CharPos]))
			If(Digit1 > Digit2)Then 
				Return BigNumbers.EnumCompareResultGreaterThan
			ElseIf(Digit2 > Digit1)Then
				Return BigNumbers.EnumCompareResultLessThan 
			EndIf
		Next
		Return BigNumbers.EnumCompareResultEqualTo
	EndFunction
	
	Function GetLargest:String(Number1:String,Number2:String)
		If(BigNumbers.CompareNumbers(Number2,Number1) = BigNumbers.EnumCompareResultGreaterThan)Then
			Return Number2
		Else
			Return Number1
		EndIf
	EndFunction
	
	Function GetSmallest:String(Number1:String,Number2:String)
		If(BigNumbers.CompareNumbers(Number2,Number1) = BigNumbers.EnumCompareResultLessThan)Then
			Return Number2
		Else
			Return Number1
		EndIf
	EndFunction

	Function Add:String(Number1:String,Number2:String)
		Local Difference:Int = Number2.Length - Number1.Length
		Local Number1Padded:String = BigNumbers.StringPadLeft(Number1,"0",(Difference>0)*Difference) 
		Local Number2Padded:String = BigNumbers.StringPadLeft(Number2,"0",(Difference<0)*(Abs(Difference))) 
		Local Result:String
		Local A:Byte
		Local B:Byte
		Local C:Byte
		Local R:Byte	
		For Local CharPos:Int = Number1Padded.Length-1 To 0 Step -1
			C = (R > 9)
			A = Int(Chr(Number1Padded[CharPos]))
			B = Int(Chr(Number2Padded[CharPos]))
			R = A + B + C
			Result = Right(String(R),1) + Result
		Next
		If(C > 0) Then
			Result = "1" + Result
		EndIf
		Return BigNumbers.StripLeadingZeros(Result)	
	EndFunction
	
	Function Subtract:String(Number1:String,Number2:String)
		Local Smallest:String = BigNumbers.GetSmallest(Number1,Number2)
		Local Largest:String = BigNumbers.GetLargest(Number1,Number2)
		Local NinesComp:String = BigNumbers.GetNinesComplement(Smallest,Largest.Length)
		Local TempResult:String = BigNumbers.Add(Largest,NinesComp)  'nines complement of smaller number
		TempResult = BigNumbers.Add(TempResult,"1")
		TempResult = BigNumbers.StripLeadingZeros(TempResult)
		TempResult = TempResult[1..]
		Return BigNumbers.StripLeadingZeros(TempResult)
	EndFunction

EndType
'***************************************************************************************************************
'End of BigNumbers
'***************************************************************************************************************
