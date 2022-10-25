; ID: 1900
; Author: grable
; Date: 2007-01-19 07:16:30
; Title: Expression Evaluator
; Description: Evaluates string expressions, with variables and functions

Import BRL.StandardIO
Import BRL.Map
Import BRL.Math

Private

Const EXPR_VAR:Int = 1
Const EXPR_VALUE:Int = 2
Const EXPR_CONST:Int = 3
Const EXPR_FUNC0:Int = 4
Const EXPR_FUNC1:Int = 5
Const EXPR_FUNC2:Int = 6

Type TExprIdent
	Field Tag:Int
	Field Value:Double
	Field V:Double Ptr	
	Field F0:Double()
	Field F1:Double( value:Double)
	Field F2:Double( value1:Double, value2:Double)

	Function CreateVar:TExprIdent( p:Double Ptr)
		Local v:TExprIdent = New TExprIdent
		v.Tag = EXPR_VAR
		v.V = p
		Return v
	EndFunction
	
	Function CreateConst:TExprIdent( value:Double)
		Local v:TExprIdent = New TExprIdent
		v.Tag = EXPR_CONST
		v.Value = value
		Return v		
	EndFunction
	
	Function CreateValue:TExprIdent( value:Double)
		Local v:TExprIdent = New TExprIdent
		v.Tag = EXPR_VALUE
		v.Value = value
		Return v		
	EndFunction	

	Function CreateFunc:TExprIdent( pcount:Int, p:Byte Ptr)
		Local v:TExprIdent = New TExprIdent
		Select pcount
			Case 0 
				v.Tag = EXPR_FUNC0
				v.F0 = p
			Case 1 
				v.Tag = EXPR_FUNC1
				v.F1 = p
			Case 2 
				v.Tag = EXPR_FUNC2				
				v.F2 = p
		EndSelect		
		Return v
	EndFunction	
EndType

Global source:String
Global pos:Int
Global idents:TMap = New TMap

Function ReportError( s:String, printpos:Int = True)
	If printpos Then 
		Print "ERROR: pos="+pos+" : "+s
	Else
		Print "ERROR: "+s
	EndIf
EndFunction

Function LookupVariable:Double( ident:String)
	Local v:TExprIdent = TExprIdent( idents.ValueForKey( ident))
	If v Then 
		Select v.Tag
			Case EXPR_VAR 
				Return v.V[0]
			Case EXPR_VALUE, EXPR_CONST 
				Return v.Value
			Default
				ReportError( "identifier not a variable => " + ident, False)
		EndSelect		
		Return 0
	EndIf
	ReportError( "variable not defined => " + ident, False)
	Return 0
EndFunction

Function CallFunction:Double( ident:String, pcount:Int=0,  value1:Double=0, value2:Double=0)
	Local v:TExprIdent = TExprIdent( idents.ValueForKey( ident))
	If v Then
		Select v.Tag
			Case EXPR_FUNC0 
				If pcount = 0 Then Return v.F0()
				ReportError( "invalid parameter count " + pcount + " expected 0")
			Case EXPR_FUNC1 
				If pcount = 1 Then Return v.F1( value1)
				ReportError( "invalid parameter count " + pcount + " expected 1")
			Case EXPR_FUNC2 
				If pcount = 2 Then Return v.F2( value1, value2)
				ReportError( "invalid parameter count " + pcount + " expected 2")
			Default
				ReportError( "identifier not a function => " + ident, False)				
		EndSelect
		Return 0
	EndIf
	ReportError( "function not defined => " + ident, False)
	Return 0
EndFunction

Function EatWhitespace()
	While (source[pos] = Asc(" ")) Or (source[pos] = Asc("~t")) Or (source[pos] = Asc("~n")) Or (source[pos] = Asc("~r"))
		pos :+ 1
	Wend
EndFunction

Function EatIdent:String()
	Local start:Int = pos
	While ((source[pos] >= Asc("a")) And (source[pos] <= Asc("z"))) Or ..
		((source[pos] >= Asc("A")) And (source[pos] <= Asc("Z"))) Or ..
		((source[pos] >= Asc("0")) And (source[pos] <= Asc("9"))) Or (source[pos] = Asc("_"))
		pos :+ 1
	Wend
	Return source[start..pos]
EndFunction

Function EatNumber:Double()
	Local start:Int = pos
	Local gotsep:Int = False
	Local res:String	
	While (source[pos] >= Asc("0")) And (source[pos] <= Asc("9"))
		pos :+ 1
		If source[pos] = Asc(".") Then
			If gotsep Then ReportError( "error in Double number")
			gotsep = True
			pos :+ 1
		EndIf
	Wend
	Return source[start..pos].ToDouble()
EndFunction

Function Primary:Double()
	Local lvalue:Double
	EatWhitespace()
	If source[pos] = Asc("(") Then
		pos :+ 1
		lvalue = AddExpression()
    		If source[pos] <> Asc(")") Then ReportError( "expected )")
    		pos :+ 1
	ElseIf (source[pos] >= Asc("0")) And (source[pos] <= Asc("9")) Then
		lvalue = EatNumber()
	ElseIf source[pos] = Asc("-") Then
		pos :+ 1
		lvalue = - AddExpression()
	ElseIf ((source[pos] >= Asc("a")) And (source[pos] <= Asc("z"))) Or ..
		((source[pos] >= Asc("A")) And (source[pos] <= Asc("Z"))) Or (source[pos] = Asc("_")) Then
		Local ident:String = EatIdent()
		If source[pos] = Asc("(") Then
			pos :+ 1
			EatWhitespace()
			If source[pos] = Asc(")") Then
				' no parameters
				pos :+ 1
				lvalue = CallFunction( ident)
			Else			
				Local rvalue1:Double = AddExpression()
				If source[pos] = Asc(")") Then
					' 1 parameter
					pos :+ 1
					lvalue = CallFunction( ident, 1, rvalue1)
				ElseIf source[pos] = Asc(",") Then
					' 2 parameters
					pos :+ 1
					Local rvalue2:Double = AddExpression()					
		    			If source[pos] <> Asc(")") Then ReportError( "expected )")
	    				pos :+ 1			
					lvalue = CallFunction( ident, 2, rvalue1, rvalue2)
				Else
					ReportError( "invalid function expression => " + ident)
				EndIf
			EndIf
		Else
			' variable lookup
			lvalue = LookupVariable( ident)
		EndIf
	Else
		ReportError( "expected number or -number or (expression)")
	EndIf
	EatWhitespace()
	Return lvalue
EndFunction

Function MulExpression:Double()
	Local lvalue:Double, rvalue:Double
	EatWhitespace()
	lvalue = Primary()
	While (source[pos] = Asc("*")) Or (source[pos] = Asc("/"))
		If source[pos] = Asc("*") Then
			pos :+ 1
			rvalue = Primary()
			lvalue = lvalue * rvalue
		ElseIf source[pos] = Asc("/") Then
			pos :+ 1
			rvalue = Primary()
			lvalue = lvalue / rvalue
		EndIf
	Wend
	EatWhitespace()
	Return lvalue
EndFunction
	
Function AddExpression:Double()
	Local lvalue:Double, rvalue:Double
	EatWhitespace()
	lvalue = MulExpression()
	While (source[pos] = Asc("+")) Or (source[pos] = Asc("-")) 
		If source[pos] = Asc("+") Then
			pos :+ 1
			rvalue = MulExpression()
			lvalue = lvalue + rvalue
		ElseIf source[pos] = Asc("-") Then
			pos :+ 1
			rvalue = MulExpression()
			lvalue = lvalue - rvalue		
		EndIf
	Wend
	EatWhitespace()
	Return lvalue
EndFunction

Public

Function RegisterVariable( ident:String, p:Double Ptr)
	idents.Insert( ident, TExprIdent.CreateVar( p))	
EndFunction

Function RegisterConstant( ident:String, value:Double)
	idents.Insert( ident, TExprIdent.CreateConst( value))	
EndFunction

Function RegisterFunction( ident:String, p:Byte Ptr, pcount:Int)
	If (pcount >= 0) And (pcount <= 2) Then idents.Insert( ident, TExprIdent.CreateFunc( pcount, p))
EndFunction

Function UnregisterIdent( ident:String)
	idents.Remove( ident)
EndFunction

Function UnregisterAllIdents()
	idents.Clear()
EndFunction

Function ClearIdentValues()
	For Local node:TNode = EachIn idents
		If TExprIdent(node._value).Tag = EXPR_VALUE Then idents.Remove( node._key)
	Next
EndFunction

Function Expression:Double( s:String)
	Local result:Double, ident:String, idx:Int
	source = s.Trim()
	pos = 0
	idx = source.Find( "=")
	If idx > 0 Then
		ident = EatIdent()
		EatWhitespace()
		pos :+ 1
	ElseIf idx = 0 Then
		ReportError( "invalid assignment", False)
		Return 0
	EndIf
	result = AddExpression()
	If ident.Length > 0 Then 
		Local v:TExprIdent = TExprIdent( idents.ValueForKey( ident))
		If v Then 
			Select v.Tag
				Case EXPR_VAR 
					v.V[0] = result
				Case EXPR_VALUE
					v.Value = result
				Case EXPR_CONST 
					ReportError( "unable to assign to constant " + ident, False)
			EndSelect
		Else
			idents.Insert( ident, TExprIdent.CreateValue( result))
		EndIf		
	EndIf
	' more "lines" ?
	If pos < source.Length Then
		If source[pos] = Asc(";") Then
			pos :+ 1
			If pos < source.Length Then
				EatWhitespace()
				Return Expression( source[pos..])
			EndIf
			Return result
		EndIf
		ReportError( "invalid expression")
		Return 0		
	EndIf
	Return result
EndFunction
