; ID: 2433
; Author: Spencer
; Date: 2009-03-11 22:40:25
; Title: Infinitely Large +/- Ints
; Description: Add and subtract infinitely large positive and negative integers using strings

' Examples Calculating Very Large numbers with Type Inf                                                                 
'                                                                                                                       
'      CREATED: MARCH 11, 2009                                                                                          
                                                                                
' TERMS OF USE: Any person/entity can use this code. Note that it's a work in prog.                                     
'              (a little credit would be nice too!) ;)                                                                  
'____________________________________________________________________________________                                   
                                                                                                                        
    Global A:String                                                                                                     
    Global B:String                                                                                                     
    Global C:String                                                                                                     
                                                                                                                        
    A = "-1000000000000000000000000000000000000"                                                                        
    B = "+                         00000 001233"                                                                        
    'NOTE: "+" sign is always optional.                                                                                 
    'NOTE: spaces and leading zeros are ignored                                                                         
                                                                                                                        
    Print Inf.Add(A,B)                                                                                                  
    Print Inf.Sub(A,B)                                                                                                  
                                                                                                                        
    '---------------------------------------------------------------                                                    
    ' what is 2^768 ?                                                                                                   
                                                                                                                        
    A = "2" ' 2^1 = 2                                                                                                   
                                                                                                                        
    For x = 2 To 768                                                                                                    
                                                                                                                        
        A = Inf.Add(A,A)                                                                                                
        Print "2^"+(x+1) + " = " + A                                                                                    
                                                                                                                        
    Next                                                                                                                
    '---------------------------------------------------------------                                                    
                                                                                                                        
    '---------------------------------------------------------------                                                    
    'can be used as an Object too                                                                                       
                                                                                                                        
        Global Atype:Inf = New INf                                                                                      
        Atype.Value = "-1000000000000000000000"                                                                         
        Atype.Plus(B)                                                                                                   
        Print "FROM AType.Value=" + Atype.Value                                                                         
        Atype = Null                                                                                                    
                                                                                                                        
    '---------------------------------------------------------------                                                    
                                                                                                                        
    GCCollect()                                                                                                         
                                                                                                                        
'*******************************************************************************************************                
'*                                                                                                    '*                
'*******************************************************************************************************                
'*     TYPE: Inf                                                                                      '*                
'*  PURPOSE: Calculate the addition and subtraction of (n)-digit long positive and negative integers  '*                
'*           Functional applications include high score counters and monetary values.                 '*                
'*  CREATED: MARCH 11, 2009                                                                           '*                
'*                           (USA)                                                                    '*                
'*                                                                                                    '*                
'*  TERMS OF USE: Any person/entity can use this code. (a little credit would be nice too!) ;)        '*                
'*          NOTE: It's a work in progress.                                                            '*                
'*******************************************************************************************************                
Type Inf                                                                                              '*                
                                                                                                      '*                
    Field Value:String                                                                                '*                
                                                                                                      '*                
    '-----------------------------------------------------------                                      '*                
     Method Plus(ValueToAdd:String)                                                                   '*                
        Value = Inf.Add(Value,ValueToAdd)                                                             '*                
     End Method                                                                                       '*                
    '-----------------------------------------------------------                                      '*                
                                                                                                      '*                
    '-----------------------------------------------------------                                      '*                
     Method Minus(ValueToSubtract:String)                                                             '*                
        Value = Inf.Sub(Value,ValueToSubtract)                                                        '*                
     End Method                                                                                       '*                
    '-----------------------------------------------------------                                      '*                
                                                                                                      '*                
    '--------------------------------------------------------------------------------                 '*                
    Function Add:String(Value1:String,Value2:String)                                                  '*                
        Return Replace(Inf.FormatValue(Inf.Calculate("+",Value1,Value2)),"+","")                      '*                
    End Function                                                                                      '*                
    '--------------------------------------------------------------------------------                 '*                
                                                                                                      '*                
    '--------------------------------------------------------------------------------                 '*                
    Function Sub:String(Value1:String,Value2:String)                                                  '*                
        Return Replace(Inf.FormatValue(Inf.Calculate("-",Value1,Value2)),"+","")                      '*                
    End Function                                                                                      '*                
    '--------------------------------------------------------------------------------                 '*                
                                                                                                      '*                
                                                                                                      '*                
    '--------------------------------------------------------------------------------                 '*                
    Function Calculate:String(Opperator:String, Value1:String, Value2:String)                         '*                
                                                                                                      '*                
        Local Result   : String                                                                       '*                
        Local Sign1    : String                                                                       '*                
        Local Sign2    : String                                                                       '*                
        Local SignCombo: String                                                                       '*                
                                                                                                      '*                
        Value1 = FormatValue(Value1)                                                                  '*                
        Value2 = FormatValue(Value2)                                                                  '*                
                                                                                                      '*                
        Sign1 = Left(Value1,1)                                                                        '*                
        Sign2 = Left(Value2,1)                                                                        '*                
                                                                                                      '*                
        SignCombo = Sign1+Opperator+Sign2                                                             '*                
                                                                                                      '*                
        Value1 = Right(Value1,Value1.Length-1)                                                        '*                
        Value2 = Right(Value2,Value2.Length-1)                                                        '*                
                                                                                                      '*                
        Select SignCombo                                                                              '*                
                                                                                                      '*                
            Case "+++"                                                                                '*                
                Result = Inf.InternalAdditionFunction(Value1,Value2)                                  '*                
                                                                                                      '*                
            Case "-++"                                                                                '*                
                Result = Inf.InternalSubtractionFunction(Value2,Value1)                               '*                
            Case "++-"                                                                                '*                
                Result = Inf.InternalSubtractionFunction(Value1,Value2)                               '*                
            Case "-+-"                                                                                '*                
                Result = "-" + Inf.InternalAdditionFunction(Value1,value2)                            '*                
            Case "+-+"                                                                                '*                
                Result = Inf.InternalSubtractionFunction(Value1,Value2)                               '*                
                                                                                                      '*                
            Case "--+"                                                                                '*                
                Result = "-" + Inf.InternalAdditionFunction(Value1,value2)                            '*                
            Case "+--"                                                                                '*                
                Result =  Inf.InternalAdditionFunction(Value1,value2)                                 '*                
                                                                                                      '*                
            Case "---"                                                                                '*                
                Result = Inf.InternalSubtractionFunction(Value2,Value1)                               '*                
                                                                                                      '*                
        End Select                                                                                    '*                
                                                                                                      '*                
        Return Result                                                                                 '*                
                                                                                                      '*                
    End Function                                                                                      '*                
    '--------------------------------------------------------------------------------                 '*                
                                                                                                      '*                
    '--------------------------------------------------------------------------------                 '*                
    Function InternalAdditionFunction:String(Value1:String, Value2:String)                            '*                
                                                                                                      '*                
        Value1 = "0" + Value1                                                                         '*                
        Value2 = "0" + Value2                                                                         '*                
                                                                                                      '*                
        While Value1.Length < Value2.Length                                                           '*                
            Value1 = "0" + Value1                                                                     '*                
        Wend                                                                                          '*                
                                                                                                      '*                
        While Value2.Length < Value1.Length                                                           '*                
            Value2 = "0" + Value2                                                                     '*                
        Wend                                                                                          '*                
                                                                                                      '*                
        Local TempDecimalPlace : Int                                                                  '*                
        Local DecimalPlaces    : Int                                                                  '*                
        Local CarryValue       : Byte                                                                 '*                
        Local Digit            : Byte                                                                 '*                
        Local DigitToAdd       : Byte                                                                 '*                
        Local DigitSum         : Byte                                                                 '*                
        Local Result           : String                                                               '*                
                                                                                                      '*                
        DecimalPlaces = Value1.Length                                                                 '*                
                                                                                                      '*                
        For TempDecimalPlace = DecimalPlaces To 1 Step -1                                             '*                
                                                                                                      '*                
            Digit      = Int(Mid(Value1 ,TempDecimalPlace ,1))                                        '*                
            DigitToAdd = Int(Mid(Value2,TempDecimalPlace ,1))                                         '*                
            DigitSum   = Digit + DigitToAdd + CarryValue                                              '*                
                                                                                                      '*                
            If DigitSum > 9 Then                                                                      '*                
                CarryValue = 1                                                                        '*                
            Else                                                                                      '*                
                CarryValue = 0                                                                        '*                
            EndIf                                                                                     '*                
                                                                                                      '*                
            Result = Right(String(DigitSum),1) + Result                                               '*                
        Next                                                                                          '*                
                                                                                                      '*                
        If Left(Result,1) = "0" Then                                                                  '*                
            Return Right(Result,Len(Result)-1)                                                        '*                
        Else                                                                                          '*                
            Return Result                                                                             '*                
        EndIf                                                                                         '*                
                                                                                                      '*                
    End Function                                                                                      '*                
    '--------------------------------------------------------------------------------                 '*                
                                                                                                      '*                
    '--------------------------------------------------------------------------------                 '*                
    'REF: http://en.wikipedia.org/wiki/Ten%27s_complement                                             '*                
     Function GetTensComplement:String(Value:String)                                                  '*                
                                                                                                      '*                
        Local Complement  : String                                                                    '*                
        Local TempDigit   : Byte                                                                      '*                
        Local TempRadix   : Byte                                                                      '*                
        Local Decimals    : Int                                                                       '*                
                                                                                                      '*                
        Decimals = Value.Length                                                                       '*                
                                                                                                      '*                
        For Local TempDecimal:Int = Decimals To 1 Step -1                                             '*                
                                                                                                      '*                
            TempDigit  = Int(Mid(Value,TempDecimal,1))                                                '*                
            TempRadix  = 9 - TempDigit                                                                '*                
            Complement = String(TempRadix) + Complement                                               '*                
                                                                                                      '*                
        Next                                                                                          '*                
                                                                                                      '*                
        Return Complement                                                                             '*                
                                                                                                      '*                
    End Function                                                                                      '*                
    '--------------------------------------------------------------------------------                 '*                
                                                                                                      '*                
                                                                                                      '*                
    '--------------------------------------------------------------------------------                 '*                
    Function InternalSubtractionFunction:String(Value1:String, Value2:String)                         '*                
                                                                                                      '*                
        Local TempValue            : String                                                           '*                
        Local Value1Digit          : Int                                                              '*                
        Local Value2Digit          : Int                                                              '*                
        Local ValuesAreEqual       : Byte = False                                                     '*                
        Local ResultWillBeNegative : Byte = False                                                     '*                
        Local Result               : String                                                           '*                
                                                                                                      '*                
        If Value2.Length > Value1.Length Then                                                         '*                
                                                                                                      '*                
            TempValue = Value1                                                                        '*                
            Value1 = Value2                                                                           '*                
            Value2 = TempValue                                                                        '*                
            ResultWillBeNegative = True                                                               '*                
                                                                                                      '*                
        ElseIf Value2.Length = Value1.Length  Then                                                    '*                
                                                                                                      '*                
            ValuesAreEqual = True                                                                     '*                
                                                                                                      '*                
            For Local CurrentDigitPos = 1 To Value2.Length Step 1                                     '*                
                                                                                                      '*                
                Value1Digit = Int(Mid(Value1,CurrentDigitPos,1))                                      '*                
                Value2Digit = Int(Mid(Value2,CurrentDigitPos,1))                                      '*                
                                                                                                      '*                
                If Value1Digit > Value2Digit Then                                                     '*                
                                                                                                      '*                
                    ValuesAreEqual = False                                                            '*                
                    Exit 'For Loop                                                                    '*                
                                                                                                      '*                
                ElseIf Value2Digit > Value1Digit Then                                                 '*                
                                                                                                      '*                
                    ValuesAreEqual = False                                                            '*                
                    TempValue = Value1                                                                '*                
                    Value1 = Value2                                                                   '*                
                    Value2 = TempValue                                                                '*                
                    ResultWillBeNegative = True                                                       '*                
                    Exit 'For Loop                                                                    '*                
                                                                                                      '*                
                EndIf                                                                                 '*                
                                                                                                      '*                
            Next                                                                                      '*                
                                                                                                      '*                
            If ValuesAreEqual Then                                                                    '*                
                                                                                                      '*                
                Return "0"                                                                            '*                
                                                                                                      '*                
            EndIf                                                                                     '*                
                                                                                                      '*                
        EndIf                                                                                         '*                
                                                                                                      '*                
                                                                                                      '*                
        While Value1.Length < Value2.Length                                                           '*                
            Value1 = "0" + Value1                                                                     '*                
        Wend                                                                                          '*                
                                                                                                      '*                
        While Value2.Length < Value1.Length                                                           '*                
            Value2 = "0" + Value2                                                                     '*                
        Wend                                                                                          '*                
                                                                                                      '*                
        Local TensComplement:String = Inf.GetTensComplement(Value2)                                   '*                
                                                                                                      '*                
        Result = Inf.Add(Value1, TensComplement)                                                      '*                
        Result = Inf.Add(Result,"1")                                                                  '*                
                                                                                                      '*                
        If Int(Left(Result,1)) > 1 Then                                                               '*                
                                                                                                      '*                
            Result = String(Int(Left(Result,1))-1) + Right(Result,Result.Length-1)                    '*                
        Else                                                                                          '*                
            Result = Right(Result,Result.Length-1)                                                    '*                
                                                                                                      '*                
        EndIf                                                                                         '*                
                                                                                                      '*                
                                                                                                      '*                
        If ResultWillBeNegative Then                                                                  '*                
                                                                                                      '*                
            Result = "-" + Result                                                                     '*                
                                                                                                      '*                
        EndIf                                                                                         '*                
                                                                                                      '*                
        Return Result                                                                                 '*                
                                                                                                      '*                
    End Function                                                                                      '*                
    '--------------------------------------------------------------------------------                 '*                
                                                                                                      '*                
    '--------------------------------------------------------------------------------                 '*                
    Function FormatValue:String(Value:String)                                                         '*                
                                                                                                      '*                
        Local ReturnValue  : String                                                                   '*                
        Local CurrentDigit : String                                                                   '*                
        Local ValueSign    : String                                                                   '*                
        Local TempPosition : Int                                                                      '*                
                                                                                                      '*                
        Value = Trim(Value)                                                                           '*                
                                                                                                      '*                
        If Left(Value,1) = "-" Then                                                                   '*                
                                                                                                      '*                
            ValueSign = "-"                                                                           '*                
            Value = Right(Value,Value.Length-1)                                                       '*                
                                                                                                      '*                
        ElseIf Left(Value,1) = "+" Then                                                               '*                
                                                                                                      '*                
            ValueSign = "+"                                                                           '*                
            Value = Right(Value,Value.Length-1)                                                       '*                
        Else                                                                                          '*                
                                                                                                      '*                
            ValueSign = "+"                                                                           '*                
        EndIf                                                                                         '*                
                                                                                                      '*                
        TempPosition = 1                                                                              '*                
                                                                                                      '*                
        While Mid(Value,TempPosition,1)="0"                                                           '*                
                                                                                                      '*                
            Value = Right(Value,Value.Length-(TempPosition-1))                                        '*                
            TempPosition = TempPosition + 1                                                           '*                
                                                                                                      '*                
        Wend                                                                                          '*                
                                                                                                      '*                
        For Local DigitPosition = 1 To Value.Length Step 1                                            '*                
                                                                                                      '*                
            CurrentDigit = Mid(Value,DigitPosition,1)                                                 '*                
                                                                                                      '*                
            Select CurrentDigit                                                                       '*                
                                                                                                      '*                
                Case "0","1","2","3","4","5","6","7","8","9"                                          '*                
                    ReturnValue = ReturnValue + CurrentDigit                                          '*                
                Default                                                                               '*                
                    ReturnValue = ReturnValue + "0"                                                   '*                
                                                                                                      '*                
            End Select                                                                                '*                
                                                                                                      '*                
        Next                                                                                          '*                
                                                                                                      '*                
        ReturnValue = ValueSign + ReturnValue                                                         '*                
                                                                                                      '*                
        Return ReturnValue                                                                            '*                
                                                                                                      '*                
    End Function                                                                                      '*                
    '--------------------------------------------------------------------------------                 '*                
                                                                                                      '*                
End Type                                                                                              '*                
'*******************************************************************************************************                
'*End of Type Inf                                                                                     '*                
'*                                                                                                    '*                
'*                                                                                                    '*                
'*                                                                                                    '*                
'*                                                                                                    '*                
'*                                                                                                    '*                
'*******************************************************************************************************
