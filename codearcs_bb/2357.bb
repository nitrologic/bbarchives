; ID: 2357
; Author: Noobody
; Date: 2008-11-09 14:28:06
; Title: Term calculator
; Description: An algorithm that calculates the result of a mathematical term

Const GWIDTH = 800
Const GHEIGHT = 600

Graphics 800, 600, 0, 2
SetBuffer BackBuffer()

Graph1.TTerm = TermTokenize( "-(((X-400)*Scale)^2-300)" )
Graph2.TTerm = TermTokenize( "-(1/((X-400)*Scale)-300)" )
Graph3.TTerm = TermTokenize( "-(2^((X-400)*Scale)-300)" )
Graph4.TTerm = TermTokenize( "-100*(X<=(800/2))+300" )

Timer = CreateTimer( 60 )

While Not KeyHit( 1 )
   Cls
   
   Counter = MilliSecs()
   PlotGraph( Graph1, "Scale=0.05", $FFFF0000 )
   PlotGraph( Graph2, "Scale=0.0001", $FF00FF00 )
   PlotGraph( Graph3, "Scale=0.03", $FF0000FF )
   PlotGraph( Graph4, "", $FFFFFF00 )
   
   Text 0, 0, MilliSecs() - Counter
   
   Flip 0
   WaitTimer Timer
Wend
End

Function PlotGraph( Graph.TTerm, VarString$, ARGB )
   LockBuffer BackBuffer()
   
   For X = 0 To GWIDTH - 1
      WritePixel X, TermCalculate( Graph, VarString$ + ",X=" + X ), ARGB
   Next
   
   UnlockBuffer BackBuffer()
End Function

;############################# Everithing beyond this line is used by the algorithm, everything above this line is just the example.

Type TTerm
   Field Action
   Field P#
   Field PVar$
   Field PTerm.TTerm
   
   Field Result#
End Type

Const TERM_INITIATE      = 1
Const TERM_ADD         = 2
Const TERM_SUBSTRACT   = 3
Const TERM_MULTIPLY      = 4
Const TERM_DIVIDE      = 5
Const TERM_GREATER      = 6
Const TERM_SMALLER      = 7
Const TERM_EQUAL      = 8
Const TERM_GREATEREQUAL   = 9
Const TERM_SMALLEREQUAL   = 10
Const TERM_END         = 11

Global ResultTerm.TTerm

Function TermTokenize.TTerm( Calc$ )
   Term.TTerm = New TTerm
      Term\Action = TERM_INITIATE
   
   If Left( Calc$, 1 ) <> "-" Then Calc$ = "+" + Calc$
   
   While Calc$ <> ""
      Term.TTerm = New TTerm
      
      If FirstTerm.TTerm = Null Then FirstTerm = Term
      
      Select Left( Calc$, 1 )
         Case "-"
            Term\Action = TERM_SUBSTRACT
         Case "+"
            Term\Action = TERM_ADD
         Case "*"
            Term\Action = TERM_MULTIPLY
         Case "/"
            Term\Action = TERM_DIVIDE
         Case "^"
            Term\Action = TERM_EXPONENT
         Case ">"
            If Mid( Calc$, 2, 1 ) = "="  Then
               Term\Action = TERM_GREATEREQUAL
               Calc$ = Right( Calc$, Len( Calc$ ) - 1 )
            Else
               Term\Action = TERM_GREATER
            EndIf
         Case "<"
            If Mid( Calc$, 2, 1 ) = "="  Then
               Term\Action = TERM_SMALLEREQUAL
               Calc$ = Right( Calc$, Len( Calc$ ) - 1 )
            Else
               Term\Action = TERM_SMALLER
            EndIf
         Case "="
            Term\Action = TERM_EQUAL
      End Select
      
      Offset = FindOperand( Calc$, 2 )
      If Offset = 0 Then Offset = Len( Calc$ ) + 1
      
      Param$ = Mid( Calc$, 2, Offset - 2 )
      
      If IsLetter( Left( Param$, 1 ) ) Then
         Term\PVar$ = Param$
      ElseIf Left( Param$, 1 ) = "(" Then
         Term\PTerm = TermTokenize( Right( Left( Param$, Len( Param$ ) - 1 ), Len( Param$ ) - 2 ) )
      Else
         Term\P# = Float( Param$ )
      EndIf
      
      DebugLog Left( Calc$, 1 ) + Param$
      
      Calc$ = Right( Calc$, Len( Calc$ ) - Offset + 1 )
   Wend
   
   Term.TTerm = New TTerm
      Term\Action = TERM_END
   
   Return FirstTerm
End Function

Function TermCalculate#( Term.TTerm, Vars$ = "" )
   Local Result# = 0
   
   If Term = Null Then Return 0
   
   While Term\Action <> TERM_END
      If Term\PVar$ <> "" Then
         Offset = Standalone( Lower( Vars$ ), Lower( Term\PVar$ ) )
         
         If Offset Then
            Offset = Offset + Len( Term\PVar$ )
            
            Offset2 = Instr( Vars$, ",", Offset )
            If Offset2 = 0 Then Offset2 = Len( Vars$ )
            
            Term\P# = Float( Mid( Vars$, Offset + 1, Offset2 - Offset ) )
         Else
            RuntimeError "Undefined Variable: '" + Term\PVar$ + "'!
         EndIf
      ElseIf Term\PTerm <> Null
         Term\P# = TermCalculate( Term\PTerm, Vars$ )
      EndIf
      
      Select Term\Action
         Case TERM_ADD
            Result# = Result# + Term\P#
            Term\Result# = Term\P#
         Case TERM_SUBSTRACT
            Result# = Result# - Term\P#
            Term\Result# = -Term\P#
         Case TERM_MULTIPLY
            ParamTerm.TTerm = Before Term
            
            Result# = Result# - ParamTerm\Result#
            Result# = Result# + Term\P#*ParamTerm\Result#
            
            Term\Result# = Term\P#*ParamTerm\Result#
         Case TERM_DIVIDE
            ParamTerm.TTerm = Before Term
            
            Result# = Result# - ParamTerm\Result#
            Result# = Result# + ParamTerm\Result#/Term\P#
            
            Term\Result# = ParamTerm\Result#/Term\P#
         Case TERM_EXPONENT
            ParamTerm.TTerm = Before Term
            
            Result# = Result# - ParamTerm\Result#
            
            Select ParamTerm\Action
               Case TERM_MULTIPLY
                  ParamTerm\Result# = ParamTerm\Result#/ParamTerm\P#
                  If Term\P# = 0 Then
                     Term\Result# = 1
                  ElseIf Float( Int( Term\P# ) ) = Term\P# Then
                     Term\Result# = ParamTerm\Result#
                     For i = 1 To Term\P# - 1
                        Term\Result# = Term\Result#*ParamTerm\Result#
                     Next
                  Else
                     Term\Result# = ParamTerm\Result#^Term\P#
                  EndIf
                  Result# = Result# + Term\Result#*ParamTerm\Result#
               Case TERM_DIVIDE
                  ParamTerm\Result# = ParamTerm\Result#*ParamTerm\P#
                  If Term\P# = 0 Then
                     Term\Result# = 1
                  ElseIf Float( Int( Term\P# ) ) = Term\P# Then
                     Term\Result# = ParamTerm\Result#
                     For i = 1 To Term\P# - 1
                        Term\Result# = Term\Result#*ParamTerm\Result#
                     Next
                  Else
                     Term\Result# = ParamTerm\Result#^Term\P#
                  EndIf
                  Result# = Result# + ParamTerm\Result#/Term\Result#
               Default
                  If Term\P# = 0 Then
                     Term\Result# = 1
                  ElseIf Float( Int( Term\P# ) ) = Term\P# Then
                     Term\Result# = ParamTerm\Result#
                     For i = 1 To Term\P# - 1
                        Term\Result# = Term\Result#*ParamTerm\Result#
                     Next
                  Else
                     Term\Result# = ParamTerm\Result#^Term\P#
                  EndIf
                  Result# = Result# + Term\Result#
            End Select
         Case TERM_GREATER
            ParamTerm.TTerm = Before Term
            
            Result = Result - ParamTerm\Result#
            
            Result# = Result# + ( ParamTerm\Result# > Term\P# )
         Case TERM_EQUAL
            ParamTerm.TTerm = Before Term
            
            Result = Result - ParamTerm\Result#
            
            Result# = Result# + ( ParamTerm\Result# = Term\P# )
         Case TERM_SMALLER
            ParamTerm.TTerm = Before Term
            
            Result = Result - ParamTerm\Result#
            
            Result# = Result# + ( ParamTerm\Result# < Term\P# )
         Case TERM_GREATEREQUAL
            ParamTerm.TTerm = Before Term
            
            Result = Result - ParamTerm\Result#
            
            Result# = Result# + ( ParamTerm\Result# >= Term\P# )
         Case TERM_SMALLEREQUAL
            ParamTerm.TTerm = Before Term
            
            Result = Result - ParamTerm\Result#
            
            Result# = Result# + ( ParamTerm\Result# <= Term\P# )
      End Select
      
      If Term\PTerm <> Null Then Term = After ResultTerm Else Term = After Term
   Wend
   
   Term\Result# = Result#
   ResultTerm = Term
   
   Return Result#
End Function

Function Standalone( SourceString$, SearchString$, Offset = 1 )
   Offset = Instr( SourceString$, SearchString$, Offset )
   
   While Offset
      If Offset > 1 Then LeftEnd$ = Mid( SourceString$, Offset - 1, 1 ) Else LeftEnd$ = ","
      RightEnd$ = Mid( SourceString$, Offset + Len( SearchString$ ), 1 )
      
      If RightEnd$ = "=" And LeftEnd$ = "," Then Return Offset Else Offset = Instr( SourceString$, SearchString$, Offset + 1 )
   Wend
   
   Return False
End Function

Function IsLetter( Char$ )
   If Asc( Char$ ) >= 65 And Asc( Char$ ) <= 90 Then Return True
   If Asc( Char$ ) >= 97 And Asc( Char$ ) <= 122 Then Return True
End Function

Function IsInBrackets( SourceString$, Offset )
   OffsetBracket = Instr( SourceString$, "(" )
   
   While OffsetBracket
      Level = 1
      OffsetOpenBracket = OffsetBracket
      OffsetCloseBracket = 0
      While Level > 0
         OffsetOpenBracket = Instr( SourceString$, "(", OffsetOpenBracket + 1 )
         OffsetCloseBracket = Instr( SourceString$, ")", OffsetCloseBracket + 1 )
         
         If OffsetCloseBracket > 0 And ( OffsetCloseBracket < OffsetOpenBracket Or OffsetOpenBracket = 0 ) Then
            If Level - 1 = 0 Then Exit
         EndIf
         
         If OffsetOpenBracket Then Level = Level + 1
         If OffsetCloseBracket Then Level = Level - 1
      Wend
      
      If Offset > OffsetBracket And Offset < OffsetCloseBracket Then
         If OffsetBracket > 1 Then
            Char$ = Mid( SourceString$, OffsetBracket - 1, 1 )
            If Not IsLetter( Char$ ) Then Return True
         EndIf
         
         Char$ = Mid( SourceString$, OffsetCloseBracket + 1, 1 )
         If Not IsLetter( Char$ ) Then Return True
      EndIf
      
      OffsetBracket = Instr( SourceString$, "(", OffsetBracket + 1 )
   Wend
   
   Return False
End Function

Function FindOperand( SourceString$, Offset = 1 )
   OffsetPlus = Instr( SourceString$, "+", Offset )
   OffsetMinus = Instr( SourceString$, "-", Offset )
   OffsetStar = Instr( SourceString$, "*", Offset )
   OffsetSlash = Instr( SourceString$, "/", Offset )
   OffsetCaret = Instr( SourceString$, "^", Offset )
   OffsetSmaller = Instr( SourceString$, "<", Offset )
   OffsetBigger = Instr( SourceString$, ">", Offset )
   OffsetEqual = Instr( SourceString$, "=", Offset )
   
   If OffsetPlus = 0 Then OffsetPlus = 999999
   If OffsetMinus = 0 Then OffsetMinus = 999999
   If OffsetStar = 0 Then OffsetStar = 999999
   If OffsetSlash = 0 Then OffsetSlash = 999999
   If OffsetCaret = 0 Then OffsetCaret = 999999
   If OffsetSmaller = 0 Then OffsetSmaller = 999999
   If OffsetBigger = 0 Then OffsetBigger = 999999
   If OffsetEqual = 0 Then OffsetEqual = 999999
   
   MinValue = Minimum( OffsetPlus, OffsetMinus )
   MinValue = Minimum( MinValue, OffsetStar )
   MinValue = Minimum( MinValue, OffsetSlash )
   MinValue = Minimum( MinValue, OffsetCaret )
   MinValue = Minimum( MinValue,  OffsetSmaller )
   MinValue = Minimum( MinValue,  OffsetBigger )
   MinValue = Minimum( MinValue,  OffsetEqual )
   If MinValue = 999999 Then Return False
   
   If IsInBrackets( SourceString$, MinValue ) Then MinValue = FindOperand( SourceString$, MinValue + 1 )
   
   Return MinValue
End Function

Function Minimum( ValueA, ValueB )
   If ValueA < ValueB Then Return ValueA Else Return ValueB
End Function
