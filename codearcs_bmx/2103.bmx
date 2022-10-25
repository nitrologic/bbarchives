; ID: 2103
; Author: ziggy
; Date: 2007-09-11 14:15:04
; Title: ultra lite script language
; Description: This is a ultra lite and very limited Scripting language written in BMX and based on ancient basic syntax

'**************************************************************************************************
' This program was written with BLIde (http://www.blide.org)
' Application:
' Author:
' License:
'**************************************************************************************************

Framework brl.StandardIO
Import pub.Win32
Import brl.Retro
Import brl.system
SuperStrict
'#Region Initializations:
Private
Const _Version:String = "BScript 0.1"
Public
Local Compiler:TCompiler = New TCompiler
'#End Region

'#Region Load the script source code and compile it
Local F:TTextStream = LoadStreamText("scripttest.bs") 
If f = Null Then
	Notify("Missing file scripttest.bs Get this file from here: http://www.blitzmax.com/codearcs/codearcs.php?code=2103#comments") 
	End
EndIf
Local code:String[] 
Print "COMPILING..."
code = compiler.Compile(F) 
'#End Region

'#Region: Execution of the compiled script:

'First of all, we check the script was properly compiled:
If compiler.Error_Line <> - 1 Then
	Print "   Compile error"
	Print "   Description:" + compiler.Error_Description
	Print "   At line number:" + compiler.Error_Line
	Print "   Where code is:" + MTrim(compiler.error_sourcecode) 
	Print "COMPILATION CANCELED."
	End
End If

'In this sample, we show compilation results information:
Print "   Source code " + compiler.CompiledLines + " lines of code."
Print "   Generated VMA code: " + code.length + " lines of code"
Print "   Debug info present: " + compiler.ProduceDebug
Print "COMPILED"
'We show the user the compiled source code (just for this sample):
Input "Press enter to view the VMA source code."
Print "."
Print "."
Print "."
Local ex:TExecuter
ex = New TExecuter

'In case there's a compilation error or empty script, there's nothing to show:
If code = Null Then
	Print "Compilation error"
	End
EndIf
'Otherwise we show the source code:
Local StrOut:String
For Local i:Int = 0 To code.Length - 1
	Print i + " : " + code[i] 
	StrOut = StrOut + code[i] + Chr(13) + Chr(10) 
Next
'We save the compiled source code, just to show how easy it is once it is placed all together:
SaveText(strout, "compiled.vma") 
'#End Region

'#Region Finally, the Script is executed:
Input "Press ENTER to run the program"
ex.Execute(code) 
End
'#End Region

'summary:This class generates VMA compiled output for arithmetic expressions
Type TCompEvaluator
	Field _IntCount:Int
	Field _IntCode:TList
	Field TargetScope:TScope
	Const AndStr:String = Chr(1) 
	Const OrStr:String = Chr(2) 
	Field ErrorDescription:String
	Method CompEvaluate:String[] (Expr:String, LastEx:String Var, IsLiteral:Int Var) 
		Try
			Self._BeginCompExpr() 
			expr = ReplaceB(expr, " AND ", AndStr, True) 
			expr = ReplaceB(expr, ")AND ", ")" + AndStr, True) 
			expr = ReplaceB(expr, " AND(", AndStr + "(", True) 
			expr = ReplaceB(expr, "]AND ", "]" + AndStr, True) 
			expr = ReplaceB(expr, " AND[", AndStr + "[", True) 
			expr = ReplaceB(expr, " OR ", OrStr, True) 
			expr = ReplaceB(expr, ")OR ", ")" + OrStr, True) 
			expr = ReplaceB(expr, " OR(", OrStr + "(", True) 
			expr = ReplaceB(expr, "]OR ", "]" + OrStr, True) 
			expr = ReplaceB(expr, " OR[", OrStr + "[", True) 
			expr = ReplaceB(Expr, " ", "", False, True) 
			LastEx = _compevaluate(Expr) 
			If GetSufix(LastEx, False) = "N" Then
				Local leftl:Int = 0, forcenum:Int = 0
				LastEx = GetLeftId(LastEx, LastEx.Length, Leftl, forcenum) 
				IsLiteral = True
			End If
	
			Local SR:String[_intcode.Count()] 
			For Local i:Int = 0 To sr.Length - 1
				sr[i] = String(_intcode.ValueAtIndex(i)) 
			Next
			Return sr
		Catch Ex:String
			Self.ErrorDescription = "Error in expression:" + Ex
			Return Null
		End Try
	End Method
	Method _CompEvaluate:String(Expr:String) 
		Local OpIndex:Int
		Local LeftL:Int = 0, RightL:Int = 0
		Local SubExIndex:Int = InstrB(Expr, "(") 
		Local FNL:Int = 0
		Local FNR:Int = 0
		While SubExIndex
			Local subS:String
			subS = Mid(Expr, InstrB(Expr, "(") + 1) 
			Local ParamCount:Int = 1
			Local found:Int = False
			Local lastindex:Int = 0
			For Local i:Int = 0 To subs.Length
				If Mid(subs, i, 1) = "(" Then ParamCount:+1
				If Mid(subs, i, 1) = ")" Then
					paramcount:-1
					If paramcount = 0 Then
						found = True
						LastIndex = i
						Exit
					EndIf
				EndIf
			Next
			If found = False
				Return "!Err"
			Else
				Local IsFunction:Int = False
				If subexindex >= 1 Then
					Local Chk:String = Mid(Expr, subexindex - 1, 1) 
					If IsOperator(CHK) = False Then
						isfunction = True
						Local chars:Int = 0
						Local FunctionId:String = GetLeftId(Expr, subexindex - 1, chars, FNL) 
						Expr = Left(Expr, InstrB(Expr, "(") - chars - 1) + CallFunction(functionid, Left(subs, lastindex - 1)) + Mid(expr, subexindex + lastindex + 1) 
					End If
				EndIf
				If isfunction = False Then
					Expr = Left(Expr, InstrB(Expr, "(") - 1) + _compEvaluate(Left(subs, lastindex - 1)) + Mid(expr, subexindex + lastindex + 1) 
				EndIf
			End If
			SubExIndex:Int = InstrB(Expr, "(") 
		Wend
		'#Region STC	->String concatenation
		OpIndex = InstrB(Expr, "++") 
		While OpIndex
			Local LI:String = GetLeftId(Expr, Opindex - 1, leftL, FNL) 
			Local RI:String = GetRightId(Expr, Opindex + 2, RightL, FNR) 
			Self.CompOutput("STC" + GetSufix(LI, FNL) + GetSufix(RI, FNR)) 
			Local ExprS:String = GetExprName() 
			Self.CompOutput(ExprS) 
			Self.CompOutput(LI) 
			Self.CompOutput(RI) 
			Expr = Left(Expr, Opindex - LeftL - 1) + ExprS + Mid(Expr, Opindex + RightL + 1 + 1) 
			OpIndex = InstrB(Expr, "++") 
			checkvariables(LI, FNL, RI, FNR) 
		Wend
		'#End Region
		'#Region EXP
		OpIndex = InstrB(Expr, "^") 
		While OpIndex
			Local LI:String = GetLeftId(Expr, Opindex - 1, leftL, FNL) 
			Local RI:String = GetRightId(Expr, Opindex + 1, RightL, FNR) 
			Self.CompOutput("EXP" + GetSufix(LI, FNL) + GetSufix(RI, FNR)) 
			Local ExprS:String = GetExprName() 
			Self.CompOutput(ExprS) 
			Self.CompOutput(LI) 
			Self.CompOutput(RI) 
			Expr = Left(Expr, Opindex - LeftL - 1) + ExprS + Mid(Expr, Opindex + RightL + 1) 
			OpIndex = InstrB(Expr, "^") 
			checkvariables(LI, FNL, RI, FNR) 

		Wend
		'#End Region
		
		'#Region DIV
		OpIndex = InstrB(Expr, "/") 
		While OpIndex
			Local LI:String = GetLeftId(Expr, Opindex - 1, leftL, FNL) 
			Local RI:String = GetRightId(Expr, Opindex + 1, RightL, FNR) 
			Self.CompOutput("DIV" + GetSufix(LI, FNL) + GetSufix(RI, FNR)) 
			Local ExprS:String = GetExprName() 
			Self.CompOutput(ExprS) 
			Self.CompOutput(LI) 
			Self.CompOutput(RI) 
			Expr = Left(Expr, Opindex - LeftL - 1) + ExprS + Mid(Expr, Opindex + RightL + 1) 
			OpIndex = InstrB(Expr, "/") 
			checkvariables(LI, FNL, RI, FNR) 
		Wend
		'#End Region


				
		'#Region MUL
		OpIndex = InstrB(Expr, "*") 
		While OpIndex
			Local LI:String = GetLeftId(Expr, Opindex - 1, leftL, FNL) 
			Local RI:String = GetRightId(Expr, Opindex + 1, RightL, FNR) 
			Self.CompOutput("MUL" + GetSufix(LI, FNL) + GetSufix(RI, FNR)) 
			Local ExprS:String = GetExprName() 
			Self.CompOutput(ExprS) 
			Self.CompOutput(LI) 
			Self.CompOutput(RI) 
			Expr = Left(Expr, Opindex - LeftL - 1) + ExprS + Mid(Expr, Opindex + RightL + 1) 
			OpIndex = InstrB(Expr, "*") 
			checkvariables(LI, FNL, RI, FNR) 

		Wend
		'#End Region

		
		'#Region SUB
		OpIndex = InstrB(Expr, "-") 
		While OpIndex
			Local LI:String = GetLeftId(Expr, Opindex - 1, leftL, FNL) 
			Local RI:String = GetRightId(Expr, Opindex + 1, RightL, FNR) 
			Self.CompOutput("SUB" + GetSufix(LI, FNL) + GetSufix(RI, FNR)) 
			Local ExprS:String = GetExprName() 
			Self.CompOutput(ExprS) 
			Self.CompOutput(LI) 
			Self.CompOutput(RI) 
			Expr = Left(Expr, Opindex - LeftL - 1) + ExprS + Mid(Expr, Opindex + RightL + 1) 
			OpIndex = InstrB(Expr, "-") 
			checkvariables(LI, FNL, RI, FNR) 
		Wend
		'#End Region
			
		'#Region +
		OpIndex = InstrB(Expr, "+") 
		While OpIndex
			Local LI:String = GetLeftId(Expr, Opindex - 1, leftL, FNL) 
			Local RI:String = GetRightId(Expr, Opindex + 1, RightL, FNR) 
			Self.CompOutput("SUM" + GetSufix(LI, FNL) + GetSufix(RI, FNR)) 
			Local ExprS:String = GetExprName() 
			Self.CompOutput(ExprS) 
			Self.CompOutput(LI) 
			Self.CompOutput(RI) 
			Expr = Left(Expr, Opindex - LeftL - 1) + ExprS + Mid(Expr, Opindex + RightL + 1) 
			OpIndex = InstrB(Expr, "+") 
			checkvariables(LI, FNL, RI, FNR) 
		Wend
		'#End Region
		
		'#Region EQS	->String comparison
		OpIndex = InstrB(Expr, "==") 
		While OpIndex
			Local LI:String = GetLeftId(Expr, Opindex - 1, leftL, FNL) 
			Local RI:String = GetRightId(Expr, Opindex + 2, RightL, FNR) 
			Self.CompOutput("EQS" + GetSufix(LI, FNL) + GetSufix(RI, FNR)) 
			Local ExprS:String = GetExprName() 
			Self.CompOutput(ExprS) 
			Self.CompOutput(LI) 
			Self.CompOutput(RI) 
			Expr = Left(Expr, Opindex - LeftL - 1) + ExprS + Mid(Expr, Opindex + RightL + 1 + 1) 
			OpIndex = InstrB(Expr, "==") 
			checkvariables(LI, FNL, RI, FNR) 
		Wend
		'#End Region
		
		'#Region GRS ->String comparison
		OpIndex = InstrB(Expr, ">>") 
		While OpIndex
			Local LI:String = GetLeftId(Expr, Opindex - 1, leftL, FNL) 
			Local RI:String = GetRightId(Expr, Opindex + 2, RightL, FNR) 
			Self.CompOutput("GRS" + GetSufix(LI, FNL) + GetSufix(RI, FNR)) 
			Local ExprS:String = GetExprName() 
			Self.CompOutput(ExprS) 
			Self.CompOutput(LI) 
			Self.CompOutput(RI) 
			Expr = Left(Expr, Opindex - LeftL - 1) + ExprS + Mid(Expr, Opindex + RightL + 1 + 1) 
			OpIndex = InstrB(Expr, ">>") 
			checkvariables(LI, FNL, RI, FNR) 
		Wend
		'#End Region
		
		'#Region SMS ->String comparison
		OpIndex = InstrB(Expr, "<<") 
		While OpIndex
			Local LI:String = GetLeftId(Expr, Opindex - 1, leftL, FNL) 
			Local RI:String = GetRightId(Expr, Opindex + 2, RightL, FNR) 
			Self.CompOutput("SMS" + GetSufix(LI, FNL) + GetSufix(RI, FNR)) 
			Local ExprS:String = GetExprName() 
			Self.CompOutput(ExprS) 
			Self.CompOutput(LI) 
			Self.CompOutput(RI) 
			Expr = Left(Expr, Opindex - LeftL - 1) + ExprS + Mid(Expr, Opindex + RightL + 1 + 1) 
			OpIndex = InstrB(Expr, "<<") 
			checkvariables(LI, FNL, RI, FNR) 
		Wend
		'#End Region
		
		'#Region GRE ->String comparison
		OpIndex = InstrB(Expr, ">=") 
		While OpIndex
			Local LI:String = GetLeftId(Expr, Opindex - 1, leftL, FNL) 
			Local RI:String = GetRightId(Expr, Opindex + 2, RightL, FNR) 
			Self.CompOutput("GRE" + GetSufix(LI, FNL) + GetSufix(RI, FNR)) 
			Local ExprS:String = GetExprName() 
			Self.CompOutput(ExprS) 
			Self.CompOutput(LI) 
			Self.CompOutput(RI) 
			Expr = Left(Expr, Opindex - LeftL - 1) + ExprS + Mid(Expr, Opindex + RightL + 1 + 1) 
			OpIndex = InstrB(Expr, ">=") 
			checkvariables(LI, FNL, RI, FNR) 
		Wend
		OpIndex = InstrB(Expr, "=>") 
		While OpIndex
			Local LI:String = GetLeftId(Expr, Opindex - 1, leftL, FNL) 
			Local RI:String = GetRightId(Expr, Opindex + 2, RightL, FNR) 
			Self.CompOutput("GRE" + GetSufix(LI, FNL) + GetSufix(RI, FNR)) 
			Local ExprS:String = GetExprName() 
			Self.CompOutput(ExprS) 
			Self.CompOutput(LI) 
			Self.CompOutput(RI) 
			Expr = Left(Expr, Opindex - LeftL - 1) + ExprS + Mid(Expr, Opindex + RightL + 1 + 1) 
			OpIndex = InstrB(Expr, "=>") 
			checkvariables(LI, FNL, RI, FNR) 
		Wend

		'#End Region

		'#Region SME ->String comparison
		OpIndex = InstrB(Expr, "<=") 
		While OpIndex
			Local LI:String = GetLeftId(Expr, Opindex - 1, leftL, FNL) 
			Local RI:String = GetRightId(Expr, Opindex + 2, RightL, FNR) 
			Self.CompOutput("SME" + GetSufix(LI, FNL) + GetSufix(RI, FNR)) 
			Local ExprS:String = GetExprName() 
			Self.CompOutput(ExprS) 
			Self.CompOutput(LI) 
			Self.CompOutput(RI) 
			Expr = Left(Expr, Opindex - LeftL - 1) + ExprS + Mid(Expr, Opindex + RightL + 1 + 1) 
			OpIndex = InstrB(Expr, "<=") 
			checkvariables(LI, FNL, RI, FNR) 
		Wend
		OpIndex = InstrB(Expr, "=<") 
		While OpIndex
			Local LI:String = GetLeftId(Expr, Opindex - 1, leftL, FNL) 
			Local RI:String = GetRightId(Expr, Opindex + 2, RightL, FNR) 
			Self.CompOutput("SME" + GetSufix(LI, FNL) + GetSufix(RI, FNR)) 
			Local ExprS:String = GetExprName() 
			Self.CompOutput(ExprS) 
			Self.CompOutput(LI) 
			Self.CompOutput(RI) 
			Expr = Left(Expr, Opindex - LeftL - 1) + ExprS + Mid(Expr, Opindex + RightL + 1 + 1) 
			OpIndex = InstrB(Expr, "=<") 
			checkvariables(LI, FNL, RI, FNR) 
		Wend
		'#End Region
		
		'#Region EQU =
		OpIndex = InstrB(Expr, "=") 
		While OpIndex
			Local LI:String = GetLeftId(Expr, Opindex - 1, leftL, FNL) 
			Local RI:String = GetRightId(Expr, Opindex + 1, RightL, FNR) 
			Self.CompOutput("EQU" + GetSufix(LI, FNL) + GetSufix(RI, FNR)) 
			Local ExprS:String = GetExprName() 
			Self.CompOutput(ExprS) 
			Self.CompOutput(LI) 
			Self.CompOutput(RI) 
			Expr = Left(Expr, Opindex - LeftL - 1) + ExprS + Mid(Expr, Opindex + RightL + 1) 
			OpIndex = InstrB(Expr, "=") 
			checkvariables(LI, FNL, RI, FNR) 
		Wend
		'#End Region

		'#Region GR
		OpIndex = InstrB(Expr, ">") 
		While OpIndex
			Local LI:String = GetLeftId(Expr, Opindex - 1, leftL, FNL) 
			Local RI:String = GetRightId(Expr, Opindex + 1, RightL, FNR) 
			Self.CompOutput("GR" + GetSufix(LI, FNL) + GetSufix(RI, FNR)) 
			Local ExprS:String = GetExprName() 
			Self.CompOutput(ExprS) 
			Self.CompOutput(LI) 
			Self.CompOutput(RI) 
			Expr = Left(Expr, Opindex - LeftL - 1) + ExprS + Mid(Expr, Opindex + RightL + 1) 
			OpIndex = InstrB(Expr, ">") 
			checkvariables(LI, FNL, RI, FNR) 
		Wend
		'#End Region

		'#Region SM
		OpIndex = InstrB(Expr, "<") 
		While OpIndex
			Local LI:String = GetLeftId(Expr, Opindex - 1, leftL, FNL) 
			Local RI:String = GetRightId(Expr, Opindex + 1, RightL, FNR) 
			Self.CompOutput("SM" + GetSufix(LI, FNL) + GetSufix(RI, FNR)) 
			Local ExprS:String = GetExprName() 
			Self.CompOutput(ExprS) 
			Self.CompOutput(LI) 
			Self.CompOutput(RI) 
			Expr = Left(Expr, Opindex - LeftL - 1) + ExprS + Mid(Expr, Opindex + RightL + 1) 
			OpIndex = InstrB(Expr, "<") 
			checkvariables(LI, FNL, RI, FNR) 
		Wend
		'#End Region

					
		'#Region AND
		OpIndex = InstrB(Expr, andstr) 
		While OpIndex
			Local LI:String = GetLeftId(Expr, Opindex - 1, leftL, FNL) 
			Local RI:String = GetRightId(Expr, Opindex + 1, RightL, FNR) 
			Self.CompOutput("AND" + GetSufix(LI, FNL) + GetSufix(RI, FNR)) 
			Local ExprS:String = GetExprName() 
			Self.CompOutput(ExprS) 
			Self.CompOutput(LI) 
			Self.CompOutput(RI) 
			Expr = Left(Expr, Opindex - LeftL - 1) + ExprS + Mid(Expr, Opindex + RightL + 1) 
			OpIndex = InstrB(Expr, andstr) 
			checkvariables(LI, FNL, RI, FNR) 
		Wend
		'#End Region
		
		'#Region OR
		OpIndex = InstrB(Expr, orstr) 
		While OpIndex
			Local LI:String = GetLeftId(Expr, Opindex - 1, leftL, FNL) 
			Local RI:String = GetRightId(Expr, Opindex + 1, RightL, FNR) 
			Self.CompOutput("OR" + GetSufix(LI, FNL) + GetSufix(RI, FNR)) 
			Local ExprS:String = GetExprName() 
			Self.CompOutput(ExprS) 
			Self.CompOutput(LI) 
			Self.CompOutput(RI) 
			Expr = Left(Expr, Opindex - LeftL - 1) + ExprS + Mid(Expr, Opindex + RightL + 1) 
			OpIndex = InstrB(Expr, orstr) 
			checkvariables(LI, FNL, RI, FNR) 
		Wend
		'#End Region
		checkvariables(expr, False, "version", False) 
		Return expr
 	End Method
	
	Method CompOutput(Value:String) 
		_intcode.AddLast(Value) 
	End Method
	Method _BeginCompExpr() 
		_intcode = New TList
		_IntCount = 0
	End Method
	Method GetExprName:String() 
		_IntCount:+1
		Return "@E" + Self._IntCount
	End Method	
	Method CheckVariables:Int(LI:String, FNL:Int, RI:String, FNR:Int) 
		If FNL = False Then
			If GetSufix(LI, False) = "V" And targetscope <> Null Then
				If targetscope.SubEx.CurItem().Contains(LI.ToLower()) = False Then
					Throw "Undefined variable " + LI
				EndIf
			End If
		EndIf
		If FNR = False Then
			If GetSufix(RI, False) = "V" And targetscope <> Null Then
				If targetscope.SubEx.CurItem().Contains(RI.ToLower()) = False Then
					Throw "Undefined variable " + RI
				EndIf
			End If
		EndIf
		Return True
	End Method
	Method CallFunction:String(FuncName:String, Parameterlist:String) 
		Local functionname:String = funcname.tolower() 
		If targetscope = Null Then Return "0"
		If targetscope.Functions.CurItem().Contains(functionname) = False Then
			'Throw("Function " + funcname + " is not defined.")
			'Return "!Err"
			Throw "Undefined function call: " + funcname
			Return "0"
		Else
			'Process parameters
			Local F:BMFunction = BMFunction(Self.TargetScope.Functions.CurItem().ValueForKey(functionname)) 
			If f = Null Then
				Throw "Undefined function call: " + funcname
			End If
			Local C:Int = 0
			For Local p:TParameter = EachIn f.Parameters
				Local dummy:Int = 0
				Local S:String = GetParam(parameterlist, c, dummy) 
				If s <> "" Then
					p.DefaultUsed = False
					Local Res:String = Self._CompEvaluate(GetParam(parameterlist, c, dummy)) 
					If dummy = False And C = f.Parameters.Length - 1 Then
						Throw "Too many parameters."
					End If
					Local isliteral:Int = False
					If GetSufix(Res, False) = "N" Then
						Local leftl:Int = 0, forcenum:Int = 0
						Res = GetLeftId(res, res.Length, Leftl, forcenum) 
						IsLiteral = True
					End If
					If isliteral Then
						Self.CompOutput("PUSHN") 
					Else
						Self.CompOutput("PUSH" + GetSufix(Res, False)) 
					EndIf
					Self.CompOutput(C) 
					Self.CompOutput(Res) 
					Self.CompOutput(0) 
				Else
					p.DefaultUsed = True
					p.Value = p.defaultvalue
					Self.CompOutput("PUSHN") 
					Self.CompOutput(C) 
					Self.CompOutput(p.DefaultValue) 
					Self.CompOutput(1) 
				EndIf
				c:+1
			Next
			Local Ex:String = Self.GetExprName() 
			Self.CompOutput("CALL") 
			Self.CompOutput(Ex) 
			Self.CompOutput(funcname.ToLower()) 
			Return Ex
		End If
	End Method	
End Type

Type TScope
	Field SubEx:TMapStack
	Field functions:TMapStack
	Method New() 
		If subex = Null Then subex = New TMapStack
		If functions = Null Then functions = New TMapStack
		NewScope() 
	End Method
	Method NewScope() 
		SubEx.NewItem() 
		SubEx.CurItem().Insert("true", "1") 
		SubEx.CurItem().Insert("dq", Chr(34)) 
		SubEx.CurItem().Insert("cr", Chr(13) + Chr(10)) 
		SubEx.CurItem().Insert("false", "0") 
		SubEx.CurItem().Insert("version", _Version) 
		functions.NewItem() 
	End Method
	Method DelScope() 
		SubEx.Remove() 
	End Method	
	Method RegisterBMFunction(Name:String, parameters:Tparameter[] , redirect:String(parameters:TParameter[] ) = Null) 
		For Local Prm:TParameter = EachIn parameters
			prm.Name = prm.Name.ToLower() 
		Next
		Local BMF:BMFunction = New BMFunction
		BMF.Parameters = parameters
		bmf.Redirect = redirect
		Functions.curitem().Insert(Name.ToLower(), BMF) 
	End Method

End Type

Type TMapStack
	Field z_Tlist:TList
	Field z_IntCount:Int
	Method New() 
		z_Tlist = New TList
	End Method
	Method NewItem() 
		z_tlist.AddLast(New TMap) 
		z_IntCount:+1
	End Method
	Method CurItem:TMap() 
		Return TMap(z_tlist.Last()) 
	End Method
	Method Remove:TMap() 
		z_tlist.RemoveLast() 
	End Method
	Method Count:Int() 
		Return z_IntCount
	End Method
End Type

Type TExecuter
	Field Scope:TScope = New TScope
	Field ParamPush:TParameter[256] 
	Field LastParam:Int
	Method Execute(Codigo:String[] ) 
		Local SI:Int = 0
		lastparam = -1
		RegisterBuiltInFunctions(scope) 
		While si < codigo.Length
			Select codigo[si] 
				Case "PUSHN"
					Local TP:TParameter = New TParameter
					lastparam = Int(codigo[si + 1] ) 
					ParaMPush[lastparam] = TP
					tp.Value = codigo[si + 2] 
					tp.DefaultUsed = Int(codigo[si + 3] ) 
					si:+3
				Case "PUSHV", "PUSHE"
					Local TP:TParameter = New TParameter
					lastparam = Int(codigo[si + 1] ) 
					ParaMPush[lastparam] = TP
					tp.Value = Self.GetEx(codigo[si + 2] ) 
					tp.DefaultUsed = Int(codigo[si + 3] ) 
					si:+3				
				Case "CALL"
					Local PL:TParameter[lastparam + 1] 
					For Local i:Int = 0 To lastparam
						pl[i] = parampush[i] 
					Next
					Local BF:BMFunction = BMFunction(scope.functions.CurItem().ValueForKey(codigo[si + 2] )) 
					Self.SetEx(codigo[si + 1] , BF.Redirect(pl)) 
					lastparam = -1
					si:+2
				Case "DBG"
					si:+2
				Case "DIVNN"
					Self.SetEx(codigo[si + 1] , Double(codigo[si + 2] ) / Double(codigo[si + 3] )) 
					si:+3
				Case "DIVNE", "DIVNV"
					Self.SetEx(codigo[si + 1] , Double(codigo[si + 2] ) / Double(Self.GetEx(codigo[si + 3] ))) 
					si:+3
				Case "DIVEN", "DIVVN"
					Self.SetEx(codigo[si + 1] , Double(Self.GetEx(codigo[si + 2] )) / Double(codigo[si + 3] )) 
					si:+3
				Case "DIVEE", "DIVVV", "DIVVE", "DIVEV"
					Self.SetEx(codigo[si + 1] , Double(Self.GetEx(codigo[si + 2] )) / Double(Self.GetEx(codigo[si + 3] ))) 
					si:+3
				Case "ANDNN"
					Self.SetEx(codigo[si + 1] , Int(codigo[si + 2] ) & Int(codigo[si + 3] )) 
					si:+3
				Case "ANDNE", "ANDNV"
					Self.SetEx(codigo[si + 1] , Int(codigo[si + 2] ) & Int(Self.GetEx(codigo[si + 3] ))) 
					si:+3
				Case "ANDEN", "ANDVN"
					Self.SetEx(codigo[si + 1] , Int(Self.GetEx(codigo[si + 2] )) & Int(codigo[si + 3] )) 
					si:+3
				Case "ANDEE", "ANDVV", "ANDVE", "ANDEV"
					Self.SetEx(codigo[si + 1] , Int(Self.GetEx(codigo[si + 2] )) & Int(Self.GetEx(codigo[si + 3] ))) 
					si:+3
				Case "ORNN"
					Self.SetEx(codigo[si + 1] , Int(codigo[si + 2] ) | Int(codigo[si + 3] )) 
					si:+3
				Case "ORNE", "ORNV"
					Self.SetEx(codigo[si + 1] , Int(codigo[si + 2] ) | Int(Self.GetEx(codigo[si + 3] ))) 
					si:+3
				Case "OREN", "ORVN"
					Self.SetEx(codigo[si + 1] , Int(Self.GetEx(codigo[si + 2] )) | Int(codigo[si + 3] )) 
					si:+3
				Case "OREE", "ORVV", "ORVE", "OREV"
					Self.SetEx(codigo[si + 1] , Int(Self.GetEx(codigo[si + 2] )) | Int(Self.GetEx(codigo[si + 3] ))) 
					si:+3												
				Case "MULNN"
					Self.SetEx(codigo[si + 1] , Double(codigo[si + 2] ) * Double(codigo[si + 3] )) 
					si:+3
				Case "MULNE", "MULNV"
					Self.SetEx(codigo[si + 1] , Double(codigo[si + 2] ) * Double(Self.GetEx(codigo[si + 3] ))) 
					si:+3
				Case "MULEN", "MULVN"
					Self.SetEx(codigo[si + 1] , Double(Self.GetEx(codigo[si + 2] )) * Double(codigo[si + 3] )) 
					si:+3
				Case "MULEE", "MULVV", "MULVE", "MULEV"
					Self.SetEx(codigo[si + 1] , Double(Self.GetEx(codigo[si + 2] )) * Double(Self.GetEx(codigo[si + 3] ))) 
					si:+3		
				Case "SUMNN"
					Self.SetEx(codigo[si + 1] , Double(codigo[si + 2] ) + Double(codigo[si + 3] )) 
					si:+3
				Case "SUMNE", "SUMNV"
					Self.SetEx(codigo[si + 1] , Double(codigo[si + 2] ) + Double(Self.GetEx(codigo[si + 3] ))) 
					si:+3
				Case "SUMEN", "SUMVN"
					Self.SetEx(codigo[si + 1] , Double(Self.GetEx(codigo[si + 2] )) + Double(codigo[si + 3] )) 
					si:+3
				Case "SUMEE", "SUMVV", "SUMVE", "SUMEV"
					Self.SetEx(codigo[si + 1] , Double(Self.GetEx(codigo[si + 2] )) + Double(Self.GetEx(codigo[si + 3] ))) 
					si:+3		
				Case "EXPNN"
					Self.SetEx(codigo[si + 1] , Double(codigo[si + 2] ) ^ Double(codigo[si + 3] )) 
					si:+3
				Case "EXPNE", "EXPNV"
					Self.SetEx(codigo[si + 1] , Double(codigo[si + 2] ) ^ Double(Self.GetEx(codigo[si + 3] ))) 
					si:+3
				Case "EXPEN", "EXPVN"
					Self.SetEx(codigo[si + 1] , Double(Self.GetEx(codigo[si + 2] )) ^ Double(codigo[si + 3] )) 
					si:+3
				Case "EXPEE", "EXPVV", "EXPVE", "EXPEV"
					Self.SetEx(codigo[si + 1] , Double(Self.GetEx(codigo[si + 2] )) ^ Double(Self.GetEx(codigo[si + 3] ))) 
					si:+3
				Case "EQSNN"
					Self.SetEx(codigo[si + 1] , codigo[si + 2] = codigo[si + 3] ) 
					si:+3
				Case "EQSNE", "EQSNV"
					Self.SetEx(codigo[si + 1] , codigo[si + 2] = Self.GetEx(codigo[si + 3] )) 
					si:+3
				Case "EQSEN", "EQSVN"
					Self.SetEx(codigo[si + 1] , Self.GetEx(codigo[si + 2] ) = codigo[si + 3] ) 
					si:+3
				Case "EQSEE", "EQSVV", "EQSVE", "EQSEV"
					Self.SetEx(codigo[si + 1] , Self.GetEx(codigo[si + 2] ) = Self.GetEx(codigo[si + 3] )) 
					si:+3
				Case "EQUNN"
					Self.SetEx(codigo[si + 1] , Double(codigo[si + 2] ) = Double(codigo[si + 3] )) 
					si:+3
				Case "EQUNE", "EXPNV"
					Self.SetEx(codigo[si + 1] , Double(codigo[si + 2] ) = Double(Self.GetEx(codigo[si + 3] ))) 
					si:+3
				Case "EQUEN", "EQUVN"
					Self.SetEx(codigo[si + 1] , Double(Self.GetEx(codigo[si + 2] )) = Double(codigo[si + 3] )) 
					si:+3
				Case "EQUEE", "EQUVV", "EQUVE", "EQUEV"
					Self.SetEx(codigo[si + 1] , Double(Self.GetEx(codigo[si + 2] )) = Double(Self.GetEx(codigo[si + 3] ))) 
					si:+3
				Case "GRNN"
					Self.SetEx(codigo[si + 1] , Double(codigo[si + 2] ) > Double(codigo[si + 3] )) 
					si:+3
				Case "GRNE", "GRNV"
					Self.SetEx(codigo[si + 1] , Double(codigo[si + 2] ) > Double(Self.GetEx(codigo[si + 3] ))) 
					si:+3
				Case "GREN", "GRVN"
					Self.SetEx(codigo[si + 1] , Double(Self.GetEx(codigo[si + 2] )) > Double(codigo[si + 3] )) 
					si:+3
				Case "GREE", "GRVV", "GRVE", "GREV"
					Self.SetEx(codigo[si + 1] , Double(Self.GetEx(codigo[si + 2] )) > Double(Self.GetEx(codigo[si + 3] ))) 
					si:+3
				Case "GRENN"
					Self.SetEx(codigo[si + 1] , Double(codigo[si + 2] ) >= Double(codigo[si + 3] )) 
					si:+3
				Case "GRENE", "GRENV"
					Self.SetEx(codigo[si + 1] , Double(codigo[si + 2] ) >= Double(Self.GetEx(codigo[si + 3] ))) 
					si:+3
				Case "GREEN", "GREVN"
					Self.SetEx(codigo[si + 1] , Double(Self.GetEx(codigo[si + 2] )) >= Double(codigo[si + 3] )) 
					si:+3
				Case "GREEE", "GREVV", "GREVE", "GREEV"
					Self.SetEx(codigo[si + 1] , Double(Self.GetEx(codigo[si + 2] )) >= Double(Self.GetEx(codigo[si + 3] ))) 
					si:+3
				Case "SMENN"
					Self.SetEx(codigo[si + 1] , Double(codigo[si + 2] ) <= Double(codigo[si + 3] )) 
					si:+3
				Case "SMENE", "SMENV"
					Self.SetEx(codigo[si + 1] , Double(codigo[si + 2] ) <= Double(Self.GetEx(codigo[si + 3] ))) 
					si:+3
				Case "SMEEN", "SMEVN"
					Self.SetEx(codigo[si + 1] , Double(Self.GetEx(codigo[si + 2] )) <= Double(codigo[si + 3] )) 
					si:+3
				Case "SMEEE", "SMEVV", "SMEVE", "SMEEV"
					Self.SetEx(codigo[si + 1] , Double(Self.GetEx(codigo[si + 2] )) <= Double(Self.GetEx(codigo[si + 3] ))) 
					si:+3
				Case "SMNN"
					Self.SetEx(codigo[si + 1] , Double(codigo[si + 2] ) < Double(codigo[si + 3] )) 
					si:+3
				Case "SMNE", "SMNV"
					Self.SetEx(codigo[si + 1] , Double(codigo[si + 2] ) < Double(Self.GetEx(codigo[si + 3] ))) 
					si:+3
				Case "SMEN", "SMVN"
					Self.SetEx(codigo[si + 1] , Double(Self.GetEx(codigo[si + 2] )) < Double(codigo[si + 3] )) 
					si:+3
				Case "SMEE", "SMVV", "SMVE", "SMEV"
					Self.SetEx(codigo[si + 1] , Double(Self.GetEx(codigo[si + 2] )) < Double(Self.GetEx(codigo[si + 3] ))) 
					si:+3
				Case "GRSNN"
					Self.SetEx(codigo[si + 1] , codigo[si + 2] > codigo[si + 3] ) 
					si:+3
				Case "GRSNE", "GRSNV"
					Self.SetEx(codigo[si + 1] , codigo[si + 2] > Self.GetEx(codigo[si + 3] )) 
					si:+3
				Case "GRSEN", "GRSVN"
					Self.SetEx(codigo[si + 1] , Self.GetEx(codigo[si + 2] ) > codigo[si + 3] ) 
					si:+3
				Case "GRSEE", "GRSVV", "GRSVE", "GRSEV"
					Self.SetEx(codigo[si + 1] , Self.GetEx(codigo[si + 2] ) > Self.GetEx(codigo[si + 3] )) 
					si:+3
				Case "SMSNN"
					Self.SetEx(codigo[si + 1] , codigo[si + 2] < codigo[si + 3] ) 
					si:+3
				Case "SMSNE", "SMSNV"
					Self.SetEx(codigo[si + 1] , codigo[si + 2] < Self.GetEx(codigo[si + 3] )) 
					si:+3
				Case "SMSEN", "SMSVN"
					Self.SetEx(codigo[si + 1] , Self.GetEx(codigo[si + 2] ) < codigo[si + 3] ) 
					si:+3
				Case "SMSEE", "SMSVV", "SMSVE", "SMSEV"
					Self.SetEx(codigo[si + 1] , Self.GetEx(codigo[si + 2] ) < Self.GetEx(codigo[si + 3] )) 
					si:+3
				Case "SUBNN"
					Self.SetEx(codigo[si + 1] , Double(codigo[si + 2] ) - Double(codigo[si + 3] )) 
					si:+3
				Case "SUBNE", "SUBNV"
					Self.SetEx(codigo[si + 1] , Double(codigo[si + 2] ) - Double(Self.GetEx(codigo[si + 3] ))) 
					si:+3
				Case "SUBEN", "SUBVN"
					Self.SetEx(codigo[si + 1] , Double(Self.GetEx(codigo[si + 2] )) - Double(codigo[si + 3] )) 
					si:+3
				Case "SUBEE", "SUBVV", "SUBVE", "SUBEV"
					Self.SetEx(codigo[si + 1] , Double(Self.GetEx(codigo[si + 2] )) - Double(Self.GetEx(codigo[si + 3] ))) 
					si:+3
 				Case "STCNN"
					Self.SetEx(codigo[si + 1] , codigo[si + 2] + codigo[si + 3] ) 
					si:+3
				Case "STCNE", "STCNV"
					Self.SetEx(codigo[si + 1] , codigo[si + 2] + Self.GetEx(codigo[si + 3] )) 
					si:+3
				Case "STCEN", "STCVN"
					Self.SetEx(codigo[si + 1] , Self.GetEx(codigo[si + 2] ) + codigo[si + 3] ) 
					si:+3
				Case "STCEE", "STCVV", "STCVE", "STCEV"
					Self.SetEx(codigo[si + 1] , Self.GetEx(codigo[si + 2] ) + Self.GetEx(codigo[si + 3] )) 
					si:+3
				Case "OUTN"
					Print codigo[si + 1] 
					sI:+1
				Case "OUTE", "OUTV"
					Print Self.GetEx(codigo[si + 1] ) 
					sI:+1
				Case "IFN"
				
					If Int(codigo[si + 1] ) = 0 Then
						si = Int(codigo[si + 2] ) - 1
					Else
						si:+2
					End If
				Case "IFE", "IFV"
				
					If Int(Self.GetEx(codigo[si + 1] )) = 0 Then
						si = Int(codigo[si + 2] ) - 1
					Else
						si:+2
					End If
				Case "JMP"
					si = Int(codigo[si + 1] ) - 1

				Case "ALOCV"
					Self.SetEx(codigo[si + 1] , "") 
					si:+1
				Case "SETVN"
					Self.SetEx(codigo[si + 1] , codigo[si + 2] ) 
					si:+2
				Case "SETVE", "SETVV"
					Self.SetEx(codigo[si + 1] , Self.GetEx(codigo[si + 2] )) 
					si:+2
				Case "END"	'Stop program execution
					si = codigo.Length + 1
				Default
					If codigo[si] <> "" Then
						Print "UNKNOWN VIRTUAL MACHINE INDENTIFIER."
						Print "CODE BASE: " + codigo[si] 
						Print "PROGRAM ENDED BY RUN-TIME ERROR"
					Return
					EndIf
			End Select
			si:+1
		Wend
	End Method
	Method SetEx(Name:String, Value:String) 
		Self.scope.SubEx.CurItem().Insert(name.ToLower(), value) 
	End Method
	Method GetEx:String(Name:String) 
		Return String(Self.Scope.SubEx.CurItem().ValueForKey(name.ToLower())) 
	End Method
End Type
Type CompStackItem
	Method SType:String() Abstract
End Type

Type IfStack Extends compstackitem
	Field JumpToCodeLine:Int
	Method SType:String() 
		Return "IF"
	End Method
End Type
Type WhileStack Extends CompStackItem
	Field FirstCodeLine:Int
	Field JumpToCodeLine:Int
	Method SType:String() 
		Return "WHILE"
	End Method
End Type

Type TCompiler
	Field Error_Description:String
	Field Error_SourceCode:String
	Field Error_Line:Int = -1
	Field OutPut:TList
	Field Scope:TScope = New TScope
	Field CompiledLines:Int
	Field ProduceDebug:Int = False
	Field IntStack:CTStack = New CTStack
	Field _LN:Int
	Method New() 
		Output = New TList
	End Method
	Method Compile:String[] (Data:TTextStream) 
		_LN = 0
		RegisterBuiltInFunctions(Self.Scope) 
		'Local LN:Int = 0
		Local Lin:String
		Local OutLin:String
		Try
			While Not Data.Eof() 
				_ln:+1
				Lin = Data.ReadLine() 
				OutLin = Lin	'To get comp output only.
				If InstrB(lin, "'") > 0 Then
					lin = MTrim(Left(lin, Instr(lin, "'") - 1)) 
				End If
				lin = ReplaceB(lin, "(", " ( ") 
				lin = ReplaceB(lin, ")", " ) ") 
				lin = ReplaceB(lin, "[", " [ ") 
				lin = ReplaceB(lin, "]", " ] ") 
				While Asc(lin) > 255 'Avoid a UTF8-BUG ON STREAMS
					lin = Mid(lin, 2) 
				Wend
				lin = MTrim(lin) 
				If Not Begins(lin, "'") And lin <> "" Then
					If ProduceDebug = True
						Self.WriteBCode("DBG") 
						Self.WriteBCode(_ln) 
						Self.WriteBCode(MTrim(OutLin)) 
					End If
				End If
				If Begins(Lin, "'") Or lin = "" Then
					'Nothing! it is a comment!
				ElseIf Begins(lin, "Dim ") Then
					CompVar(Lin) 
				ElseIf Begins(lin, "print ") Then
					compprint(lin) 
				ElseIf Begins(lin, "print(") Then
					compprint("Print " + Mid(lin, 6)) 
				ElseIf lin.ToLower() = "print" Then
					compprint("Print " + Chr(34) + Chr(34)) 
				ElseIf Begins(lin, "if ") Then
					compif(lin) 
				ElseIf lin.ToLower() = "endif" Or lin.ToLower() = "end if" Then
					compendif(lin, _LN) 
				ElseIf lin.ToLower() = "else"
					compelse(lin, _LN) 
				ElseIf lin.ToLower() = "wend"
					compwend(lin, _LN) 
				ElseIf lin.ToLower() = "end" Then
					Self.WriteBCode("END") 
				ElseIf Begins(lin, "while ") 
					compwhile(lin) 
				Else
					If InstrB(Lin, "=") <> 0 Then
						Local Aux:String = MTrim(Left(lin, InstrB(lin, "=") - 1)) 
						If GetSufix(Aux, False) = "V" Then
							CompAssign(Aux, Mid(lin, InstrB(lin, "=") + 1)) 
						Else
							Throw "Syntax error in line " + _LN
						End If
						
					Else
						'DebugStop
						Local dummy:Int = 0
						'Local ev:String = ..
						Self.CompileExpression(lin, dummy) 
					EndIf
				End If
			Wend
			Local SR:String[Output.Count()] 
			For Local i:Int = 0 To sr.Length - 1
				sr[i] = String(Output.ValueAtIndex(i)) 
			Next
			CompiledLines = _LN
			Self._LN = 0
			Return sr
		Catch ex:String
			'Print "Compile Error"
			'Print ex + "(line " + LN + ")"
			'Print Chr(9) + outlin
			Self.Error_Description = ex
			Self.Error_Line = _LN
			Self._LN = 0	'Refreshing
			Self.Error_SourceCode = outlin
			Return Null
		End Try
	End Method
	Method CompPrint(Data:String) 
		data = MTrim(Mid(data, 7)) 
		Local nsufix:Int = 0
		Local Ex:String = Self.CompileExpression(data, Nsufix) 
		If nsufix = False
			Self.WriteBCode("OUT" + GetSufix(ex, False)) 
		Else
			Self.WriteBCode("OUTN") 
		EndIf
		
		Self.writebcode(ex) 
	End Method
	Method CompWhile(Data:String) 
		data = MTrim(Mid(data, 7)) 
		Local WS:WhileStack = New WhileStack
		ws.firstcodeline = output.Count() 
		Local nsufix:Int = 0
		Local Ex:String = Self.CompileExpression(data, Nsufix) 
		If nsufix = False
			Self.WriteBCode("IF" + GetSufix(ex, False)) 
		Else
			Self.WriteBCode("IFN") 
		EndIf
		Self.writebcode(ex) 
		Self.WriteBCode("TOBEERASED") 
		ws.JumpToCodeLine = Self.OutPut.Count() - 1
		Self.IntStack.AddItem(ws) 
	End Method
	Method CompWend(Data:String, LN:Int) 
		Local CS:CompStackItem = CompStackItem(Self.IntStack.GetLastItem()) 
		If cs.SType() <> "WHILE" Then
			Print "Wend is not closing any While."
			Return
		Else
			Local IS:WhileStack = WhileStack(cs) 
			Local JTCL:Int = is.jumptocodeline
			'Print "END IF DIRECTED TO " + String(output.Count())
			Local AUXS:String[] = New String[output.count()] 
			For Local i:Int = 0 To Len(auxs) - 1
				auxs[i] = String(Output.ValueAtIndex(i)) 
			Next
			Auxs[JTCL] = String(output.Count() + 2) 
			Self.OutPut = OutPut.FromArray(Auxs) 
			Self.WriteBCode("JMP") 
			Self.WriteBCode(is.FirstCodeLine) 
			Self.IntStack.RemoveItem() 
		End If
	End Method
	Method Compif(Data:String) 
		data = MTrim(Mid(data, 4)) 
		Local nsufix:Int = 0
		Local Ex:String = Self.CompileExpression(data, Nsufix) 
		If nsufix = False
			Self.WriteBCode("IF" + GetSufix(ex, False)) 
		Else
			Self.WriteBCode("IFN") 
		EndIf
		Local IS:IfStack = New IfStack
		Self.writebcode(ex) 
		Self.WriteBCode("TOBEERASED") 
		is.JumpToCodeLine = Self.OutPut.Count() - 1
		Self.IntStack.AddItem(IS) 
	End Method
	Method compEndIf(Data:String, LN:Int) 
		Local CS:CompStackItem = CompStackItem(Self.IntStack.GetLastItem()) 
		If cs.SType() <> "IF" Then
			Print "End if is not closing any IF."
			Return
		Else
			Local IS:IfStack = IfStack(cs) 
			Local JTCL:Int = is.jumptocodeline
			'Print "END IF DIRECTED TO " + String(output.Count())
			Local AUXS:String[] = New String[output.count()] 
			For Local i:Int = 0 To Len(auxs) - 1
				auxs[i] = String(Output.ValueAtIndex(i)) 
			Next
			Auxs[JTCL] = String(output.Count()) 
			Self.OutPut = OutPut.FromArray(Auxs) 
			Self.IntStack.RemoveItem() 
		End If
	End Method
	Method CompElse(Data:String, LN:Int) 
		Local CS:CompStackItem = CompStackItem(Self.IntStack.GetLastItem()) 
		If cs.SType() <> "IF" Then
			Print "Else is not part of any IF."
			Return
		Else
			Local IS:IfStack = IfStack(cs) 
			Local JTCL:Int = is.jumptocodeline '+2	'Avoid entering the JMP
			Local AUXS:String[] = New String[output.count()] 
			For Local i:Int = 0 To Len(auxs) - 1
				auxs[i] = String(Output.ValueAtIndex(i)) 
			Next
			Auxs[JTCL] = String(output.Count() + 2) 
			Self.OutPut = OutPut.FromArray(Auxs) 
			Self.IntStack.RemoveItem() 
			IS = New IfStack
			Self.writebcode("JMP") 
			Self.WriteBCode("TOBEERASED") 
			is.JumpToCodeLine = Self.OutPut.Count() - 1
			Self.IntStack.AddItem(IS) 
		End If
	End Method		
	Method CompVar(Data:String) 
		Local IsLast:Int = False
		Local Param:Int = 0
		data = MTrim(Mid(data, 5)) 
		Local Vari:String = MTrim(GetParam(data, Param, islast)) 
		Local value:String
		While islast = False
			If Instr(vari, "=") Then
				value = Mid(vari, Instr(vari, "=") + 1) 
				vari = MTrim(Left(vari, Instr(vari, "=") - 1)) 
			Else
				vari = MTrim(vari) 
				value = ""
			End If
			Self.CompNewVar(vari, value) 
			Param:+1
			Vari = GetParam(data, Param, islast) 
		Wend

		If Instr(vari, "=") Then
			value = Mid(vari, Instr(vari, "=") + 1) 
			vari = MTrim(Left(vari, Instr(vari, "=") - 1)) 
		Else
			vari = MTrim(vari) 
			value = ""
		End If
		Self.CompNewVar(vari, value) 
		
	End Method
	Method CompAssign(Varname:String, Expresion:String) 
		If Self.Scope.SubEx.CurItem().Contains(varname.ToLower()) = False Then
			Throw "Undefined target variable " + varname
		EndIf
		Local NSufix:Int = 0
		Local ExpReturn:String = compileexpression(Expresion, nsufix) 
		If nsufix = False Then
			Self.writebcode("SETV" + GetSufix(ExpReturn, False)) 
		Else
			Self.writebcode("SETVN") 
		EndIf
		Self.WriteBCode(VarName) 
		Self.WriteBCode(ExpReturn) 

	End Method
	Method CompNewVar(VarName:String, Value:String) 
		If Self.Scope.SubEx.CurItem().Contains(varname.ToLower()) Then
			Print "DUPLICATE VARIABLE DEFINITION!"
			Return
		End If
		Self.scope.SubEx.CurItem().Insert(varname.ToLower(), varname.ToLower()) 
		If value <> "" Then
			Local NSufix:Int = 0
			Local ExpReturn:String = compileexpression(Value, nsufix) 
			writebcode("ALOCV") 
			writebcode(VarName) 
			If nsufix = False Then
				Self.writebcode("SETV" + GetSufix(ExpReturn, False)) 
			Else
				Self.writebcode("SETVN") 
			EndIf
			Self.WriteBCode(VarName) 
			Self.WriteBCode(ExpReturn) 
		End If
	End Method
	
	Method CompileExpression:String(Expression:String, NSufix:Int Var) 
			Local EV:TCompEvaluator = New TCompEvaluator
			ev.TargetScope = Self.scope
			Local ExpReturn:String = ""
			Local IsLiteral:Int = 0
			Local code:String[] = ev.CompEvaluate(Expression, ExpReturn, IsLiteral) 
			If ev.ErrorDescription <> "" Then
				Throw ev.ErrorDescription
			End If
			For Local i:Int = 0 To code.Length - 1
				Self.WriteBCode(code[i] ) 
			Next
			ev.TargetScope = Null
			ev = Null
			nsufix = isliteral
			Return ExpReturn
	End Method
	
	Method WriteBCode(S:String) 
		Self.OutPut.AddLast(S) 
	End Method
End Type


Type CTStack
	Field Objs:CompStackItem[] 
	Method New() 
		Objs = New CompStackItem[0] 
	End Method
	Method AddItem(Obj:CompStackItem) 
		Objs = objs[..Len(objs) + 1] 
		objs[Len(objs) - 1] = Obj
	End Method
	Method GetLastItem:CompStackItem () 
		Return objs[Len(objs) - 1] 
	End Method
	Method RemoveItem() 
		objs = objs[..Len(objs) - 1] 
	End Method
End Type


'Little function to get a text stream with the correct encoding (unicode, ansi, whatever)
Function LoadStreamText:TTextStream(url:Object) 
	Local format:Int, Size:Int = 0, c:Int, d:Int, e:Int
	Local stream:TStream = ReadStream(url) 
	If stream = Null Then Return Null
	If Not stream.Eof() 
		c = stream.ReadByte() 
		Size:+1
		If Not stream.Eof() 
			d = stream.ReadByte() 
			Size:+1
			If c = $fe And d = $ff
				format = TTextStream.UTF16BE
			Else If c = $ff And d = $fe
				format = TTextStream.UTF16LE
			Else If c = $ef And d = $bb
				If Not stream.Eof() Then
					e = stream.ReadByte() 
					Size:+1
					If e = $bf format = TTextStream.UTF8
				EndIf
			EndIf
		EndIf
	EndIf
	If Not format
		Local Stream2:TTextStream = TTextStream.Create(ReadStream(url), TTextStream.LATIN1) 
		Return Stream2
	EndIf
	stream:TStream = ReadStream(url) 
	Local TStream:TTextStream = TTextStream.Create(stream, format) 
	Return TStream
End Function

'------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
'INTERNALLY USED COMPILER PARSING OPERATIONS:

Const IntSep:String = Chr(34)    'Internal parser separator For literals.
'summary: Replace a substring, ignoring text enclosed in " " and ignoring case.
Function ReplaceB:String(Expr:String, Find:String, ReplaceS:String, Ignorecase:Int = False, RECURSIVE:Int = False) 
	Const TmpChar:String = Chr(27) 
	
	If Not ignorecase Then
		If Instr(expr, find) = 0 Then Return expr
		If InstrB(expr, find) = 0 Then Return expr
		If Instr(expr, IntSep) = 0 Then Return expr.Replace(find, replaceS) 
	EndIf
	Local Aux:String = ""
	Local Flag:Int = False
	Local m:String
	For Local i:Int = 1 To expr.Length
		m = Mid(expr, i, 1) 
		If m = Chr(34) Then
			flag = Not flag
		End If
		If Not flag Then
			aux = aux + M
		Else
			aux = aux + chr(7)
		End If
	Next
	If ignorecase = True Then
		aux = aux.ToLower() 
		find = find.ToLower() 
	EndIf
	Local RETREPLACE:String
	If RECURSIVE = False Then
		RETREPLACE = REPLACES
		ReplaceS = TmpChar
		While Instr(aux, find) <> 0
			Local i:Int = Instr(aux, find) 
			aux = Left(aux, i - 1) + replaceS + Mid(aux, i + find.Length) 
			expr = Left(expr, i - 1) + replaceS + Mid(expr, i + find.Length) 
		Wend
		replaces = retreplace
		find = TmpChar
		While Instr(aux, find) <> 0
			Local i:Int = Instr(aux, find) 
			aux = Left(aux, i - 1) + replaceS + Mid(aux, i + find.Length) 
			expr = Left(expr, i - 1) + replaceS + Mid(expr, i + find.Length) 
		Wend
	Else
		Local OLDFIND:String = ""
		While Instr(AUX, FIND) <> 0
			RETREPLACE = REPLACES
			ReplaceS = TmpChar
			While Instr(aux, find) <> 0
				Local i:Int = Instr(aux, find) 
				aux = Left(aux, i - 1) + replaceS + Mid(aux, i + find.Length) 
				expr = Left(expr, i - 1) + replaceS + Mid(expr, i + find.Length) 
			Wend
			replaces = retreplace
			oldfind = find
			find = TmpChar
			While Instr(aux, find) <> 0
				Local i:Int = Instr(aux, find) 
				aux = Left(aux, i - 1) + replaceS + Mid(aux, i + find.Length) 
				expr = Left(expr, i - 1) + replaceS + Mid(expr, i + find.Length) 
			Wend
			find = oldfind
		Wend
	EndIf
	Return expr
End Function

'summary: Finds a substring, ignoring text enclosed in " " and ignoring case.
Function InstrB:Int(Expr:String, ToFind:String) 
	If Instr(expr, tofind) = 0 Then Return 0
	If Instr(expr, IntSep) = 0 Then Return Instr(expr, tofind) 
	Local Inside:Int = False
	Local Res:String = ""
	For Local i:Int = 1 To expr.Length
		Local M:String = Mid(Expr, i, 1) 
		If M = IntSep Then
			If Inside = False Then Inside = True Else inside = False
		End If
		If inside = True Then M = IntSep
		Res:+M
	Next
	Return Instr(Res, ToFind) 
End Function

'summary: Gets a parameter text in a x,y,z stringby parameter number. It processes properly literals, parenthesis,etc.
Function GetParam:String(Expr:String, count:Int, IsLast:Int Var) 
	Local Opened:Int = False
	Local Parentesi:Int = 0
	Local ParNum:Int = 0
	Local CurParam:String = ""
	For Local i:Int = 1 To expr.Length
		Local SubS:String = Mid(expr, i, 1) 
		If subs = Chr(34) Then opened = Not opened
		If Not opened Then
			If subs = "(" Then
				parentesi:+1
				If parentesi > 0 Then curparam:+subs
			ElseIf subs = ")" Then
				parentesi:-1
				If parentesi >= 0 Then curparam:+subs
			ElseIf subs = "," And parentesi = 0 Then
				ParNum:+1
				If ParNum > count Then
					Return CurParam
				End If
				curparam = ""
			Else
				CurParam:+subs
			EndIf
		Else
			CurParam:+subs
		End If
	Next
	If parnum = count Then
		islast = True
		Return curparam
	Else
		Return ""
	End If
End Function

Function GetLeftId:String(Expr:String, subexindex:Int, chars:Int Var, forceNum:Int Var) 
	Local FName:String = ""
	Forcenum = False
	If Mid(expr, subexindex, 1) = Chr(34) 
		For Local x:Int = subexindex To 0 Step - 1
			fname = Mid(expr, x, 1) + fname
			If x <> subexindex And Mid(expr, x, 1) = Chr(34) Then
				Exit
			EndIf
		Next
		chars = fname.Length
		Forcenum = True
		Return Mid(fname, 2, fname.Length - 2) 
	Else
		For Local x:Int = subexindex To 0 Step - 1
			If IsOperator(Mid(expr, x, 1)) Then
				If Mid(expr, x, 1) = "-" Then
					
					If x = 1 Then
						fname = Mid(expr, x, 1) + fname
					ElseIf x > 1 Then
						If IsOperator(Mid(expr, x - 1, 1)) Then
							fname = Mid(expr, x, 1) + fname
						End If
					End If
				End If
				Exit
			Else
				fname = Mid(expr, x, 1) + fname
			End If
		Next
		chars = fname.Length
		Return fname
	EndIf
End Function

Function GetRightId:String(Expr:String, subexindex:Int, chars:Int Var, ForceNum:Int Var) 
	Forcenum = False
	Local FName:String = ""
	Local minuscount:Int = 0
	Local NotMinus:Int = False
	If Mid(expr, subexindex, 1) = Chr(34) 
		For Local x:Int = subexindex To expr.Length
			fname = fname + Mid(expr, x, 1) 
			If x <> subexindex And Mid(expr, x, 1) = Chr(34) Then
				Exit
			EndIf
		Next
		chars = fname.Length
		forcenum = True
		Return Mid(fname, 2, fname.Length - 2) 
	Else
		For Local x:Int = subexindex To expr.Length
			If IsOperator(Mid(expr, x, 1)) Then
				If Mid(expr, x, 1) = "-" And notminus = False Then
					minuscount:+1
				Else
					Exit
				End If
			Else
				notminus = True
				fname = fname + Mid(expr, x, 1) 
			End If
		Next
		chars = Len(fname) + minuscount
		If minuscount Mod 2 = 1 Then fname = "-" + fname
		Return fname
	EndIf
End Function


'summary: Returns true if a given char is an operator.
Function IsOperator:Int(Char:String) 
	Return (InstrB("+-*/=<>&|^" + TCompEvaluator.AndStr + TCompEvaluator.OrStr, char) <> 0) 
End Function

'summary: Returns true if a given expression is a number.
Function IsNum:Int(Expr:String) 
	Local result:Int = True
	Local decimal:Int = False
	If Left(expr, 1) = Chr(34) Then
		If Right(expr, 1) = Chr(34) Then
			Local Aux:String = Mid(expr, 2, Len(expr) - 2) 
			If Instr(aux, Chr(34)) = 0 Then
				Return True
			End If
		End If
	Else
		For Local x:Int = 1 To expr.Length
			If x = 1 And Mid(expr, x, 1) = "-"
			ElseIf Mid(expr, x, 1) >= "0" And Mid(expr, x, 1) <= "9" Then
			ElseIf Mid(expr, x, 1) = "." And decimal = False
				decimal = True
			Else
				Return False
			End If
		Next
		Return result
	End If
	Return False
End Function

'summary: Returns true if a given expression is a valid variable name.
Function IsVar:Int(Expr:String) 
	Local NoNum:Int = True
	Local digit:String
	For Local x:Int = 1 To expr.Length
		digit = Mid(expr, x, 1).ToUpper() 
		If digit >= "0" And digit <= "9" Then
			If nonum = True Then Return False
		ElseIf digit >= "A" And digit <= "Z"
			nonum = False
		ElseIf digit = "_" Then
			nonum = False
		ElseIf digit = "."
			nonum = True
		Else
			Return False
		End If
	Next
	Return True
End Function

Function IsEx:Int(Expr:String) 
	Local digit:String
	If Left(expr, 2) <> "@E" Then Return False
	If expr.Length <= 2 Then Return False
	For Local i:Int = 3 To expr.Length
		digit = Mid(expr, i, 1).ToUpper() 
		If digit < "0" Or digit > "9" Then
			Return False
		End If
	Next
	Return True
End Function

Function GetSufix:String(Expr:String, Force:Int) 
	If Force = True Then Return "N"
	If IsNum(Expr) Then Return "N"
	If IsVar(Expr) Then Return "V"
	If IsEx(Expr) Then Return "E"
	Throw "syntax error in expression: " + Expr + " is not a valid identifier."
	Return "#"	'->Runtime error will be generated!
End Function

'summary: Returns true if a given string begins with a given value ignoring case
Function Begins:Int(Str1:String, str2:String) 
	Local L:Int = Str2.Length
	Return Left(str1.ToLower(), L) = str2.ToLower() 
End Function

'summary: Returns true if a given string ends with a given value ignoring case
Function Ends:Int(Str1:String, Str2:String) 
	Local L:Int = Str2.Length
	Return Right(str1.ToLower(), L) = str2.ToLower() 
End Function

'summary: Returns the same given string but striping spaces and tabs at the end and at the begining of the string.
Function MTrim:String(Str:String) 
 	str = ReplaceB(str, Chr(9), " ") 
	While InstrB(str, "  ") 
		str = ReplaceB(str, "  ", " ") 
	Wend
	str = Trim(str) 
	Return Str
End Function

'------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
'EXTERNAL FUNCTIONS HANDLERS:
'#Region: Types to handle external BMX functions:
'summary: This type handles a BMX function from within the script. Each function has to be registered in the script language.
Type BMFunction
	'summary: This is the name of the function in the script language.
	Field Name:String
	'This is an array that describes each parameter of the function, by order.
	Field Parameters:TParameter[] 
	'This is a pointer to the compiled function to be called when the script requieres it.
	Field Redirect:String(parameters:TParameter[] ) 
End Type

'summary: This is the base class for all parameters in a function declaration
Type TParameter
	'summary: This is the name of the parameter.
	Field Name:String = "<Void>"
	'summary: This is the value of the parameter in the current function call.<br>This field will be automatically filled by the evaluator before a function call is requested.
	Field Value:String = ""
	'summary: This field contains the default value for this parameter.<br>This value will be automatically selected when a function call is performed and the parameter in question is missing.
	Field DefaultValue:String = ""
	'summary: This is a boolean field that indicates if this parameter was missing or not when the function call was performed.
	Field DefaultUsed:Int = False
End Type
'------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
'BUILT IN FUNCTIONS:
'Each function that can be invoked from the Script langua, has to have the following
'declaration under BMX:
'Function FunctionName:String(Paramenters:Tparameter[])
'All functions return STRINGS, as the script language handles all its data
'using STRINGS. The Parameters[] array will contain each TParameter stated at execution time on the script.
'Example:
'	Function MathCos:String(parameters:Tparameter[] ) 
'		Local Value:Double = Double(Parameters[0].Value)	'Get the value and convert it to numeric
'		Value = Cos(Value)									'Perform calculation
'		Return String(Value)								'Return it as a string
'	End Function
'This function returns the cosinus of the first given parameter.
'As the value field in the parameter[0] object is a string, to make the calculation
'we first convert it to double. Then calculate the COS, and return it as string.
	Function MathCos:String(parameters:Tparameter[] ) 
		Return String(Cos(Double(parameters[0].Value))) 
	End Function
	
	Function MathTan:String(parameters:Tparameter[] ) 
		Return String(Tan(Double(parameters[0].Value))) 
	End Function
	
	Function MathSin:String(parameters:Tparameter[] ) 
		Return String(Sin(Double(parameters[0].Value))) 
	End Function

	Function MathACos:String(parameters:Tparameter[] ) 
		Return String(ACos(Double(parameters[0].Value))) 
	End Function
	
	Function MathATan:String(parameters:Tparameter[] ) 
		Return String(ATan(Double(parameters[0].Value))) 
	End Function
	
	Function MathASin:String(parameters:Tparameter[] ) 
		Return String(ASin(Double(parameters[0].Value))) 
	End Function
'#Region Hiperbolic Sin/Cos/Tan
	Function MathCosh:String(parameters:Tparameter[] ) 
		Return String(Cosh(Double(parameters[0].Value))) 
	End Function
	
	Function MathTanh:String(parameters:Tparameter[] ) 
		Return String(Tanh(Double(parameters[0].Value))) 
	End Function
	
	Function MathSinh:String(parameters:Tparameter[] ) 
		Return String(Sinh(Double(parameters[0].Value))) 
	End Function
'#End Region

Function MathATan2:String(parameters:Tparameter[] ) 
	Return String(ATan2(Double(parameters[0].Value), Double(parameters[1].Value))) 
End Function

Function MathCeil:String(parameters:Tparameter[] ) 
	Return String(Ceil(Double(parameters[0].Value))) 
End Function

Function Version:String(parameters:Tparameter[] ) 
	Return _Version
End Function

Function MathSQR:String(parameters:Tparameter[] ) 
	Return String(Sqr(Double(parameters[0].Value))) 
End Function

Function MathLog:String(parameters:Tparameter[] ) 
	Return String(Log(Double(parameters[0].Value))) 
End Function

Function MathLog10:String(parameters:Tparameter[] ) 
	Return String(Log10(Double(parameters[0].Value))) 
End Function

Function MathIsNan:String(parameters:Tparameter[] ) 
	Return String(IsNan(Double(parameters[0].Value))) 
End Function

Function MathIsInf:String(parameters:Tparameter[] ) 
	Return String(IsInf(Double(parameters[0].Value))) 
End Function

Function MathFloor:String(parameters:Tparameter[] ) 
	Return String(Floor(Double(parameters[0].Value))) 
End Function

Function MathExp:String(parameters:Tparameter[] ) 
	Return String(Exp(Double(parameters[0].Value))) 
End Function

Function MathMult:String(parameters:Tparameter[] ) 
	Return String(Double(parameters[0].Value) * Double(parameters[1].Value)) 
End Function

Function MathInt:String(parameters:Tparameter[] ) 
	If Int(Parameters[1].Value) = 1 Then
		parameters[0].Value = String(Double(parameters[0].Value) +.5) 
	End If
	Return String(Int(parameters[0].Value)) 
End Function

Function BSMillisecs:String(parameters:Tparameter[] ) 
	Return String(MilliSecs()) 
End Function

Function BSChr:String(parameters:Tparameter[] ) 
	Return Chr(Int(parameters[0].Value)) 
End Function

Function BSAsc:String(parameters:Tparameter[] ) 
	Return Asc(parameters[0].Value) 
End Function

Function BSLeft:String(parameters:Tparameter[] ) 
	Return Left(parameters[0].value, Int(parameters[1].value)) 
End Function

Function BSRight:String(parameters:Tparameter[] ) 
	Return Right(parameters[0].value, Int(parameters[1].value)) 
End Function

Function BSMid:String(parameters:Tparameter[] ) 
	Return Mid(parameters[0].value, Int(parameters[1].value), Int(Parameters[2].value)) 
End Function

Function Lcase:String(parameters:Tparameter[] ) 
	Return Parameters[0].Value.ToLower() 
End Function

Function UCase:String(parameters:Tparameter[] ) 
	Return Parameters[0].Value.ToUpper() 
End Function

Function BsInput:String(parameters:Tparameter[] ) 
	Return Input(parameters[0].value) 
End Function

Function BsNot:String(parameters:Tparameter[] ) 
	Return String(Not Int(parameters[0].value)) 
End Function

Function BSRand:String(parameters:Tparameter[] ) 
	Return Rand(Int(Parameters[0].Value), Int(parameters[1].Value)) 
End Function

Function BSSeedRnd:String(parameters:Tparameter[] ) 
	SeedRnd(Int(parameters[0].Value)) 
	Return "1"
End Function

Function RegisterBuiltInFunctions(ev:Tscope) 
	ev.registerbmfunction("Millisecs", Null, BSMillisecs) 
	ev.registerbmfunction("Version", Null, Version) 
	'Register the SIN function:
	Local P:TParameter[1] 	'Parameter array of 1 item
	p[0] = New TParameter	'Instantiate the first parameter
	p[0].Name = "Angle"		'Name of the parameter
	p[0].DefaultValue = ""	'No default value for this parameter
	ev.RegisterBMFunction("Sin", p, MathSin) 	'The function is registered in the evaluator object.
	'The same for the COS function:
	p = New TParameter[1] 
	p[0] = New TParameter
	p[0].Name = "Angle"
	ev.RegisterBMFunction("Cos", p, MathCos) 
	'The same for the TAN function:
	p = New TParameter[1] 
	p[0] = New TParameter
	p[0].Name = "Angle"
	ev.RegisterBMFunction("Tan", p, MathTan) 
	'The same for the ASIN function:
	p = New TParameter[1] 
	p[0] = New TParameter
	p[0].Name = "Angle"
	ev.RegisterBMFunction("ASin", p, MathASin) 
	'The same for the ACOS function:
	p = New TParameter[1] 
	p[0] = New TParameter
	p[0].Name = "Angle"
	ev.RegisterBMFunction("ACos", p, MathACos) 
	'The same for the ATAN function:
	p = New TParameter[1] 
	p[0] = New TParameter
	p[0].Name = "Angle"
	ev.RegisterBMFunction("ATan", p, MathATan) 
	'The same for the SINH function:
	p = New TParameter[1] 
	p[0] = New TParameter
	p[0].Name = "Angle"
	ev.RegisterBMFunction("SinH", p, MathSinh) 
	'The same for the COSH function:
	p = New TParameter[1] 
	p[0] = New TParameter
	p[0].Name = "Angle"
	ev.RegisterBMFunction("CosH", p, MathCosh) 
	'The same for the TANH function:
	p = New TParameter[1] 
	p[0] = New TParameter
	p[0].Name = "Angle"
	ev.RegisterBMFunction("TanH", p, MathTanh) 
	'The same for the ATAN2 function:
	p = New TParameter[2] 
	p[0] = New TParameter
	p[0].Name = "Angle"
	p[1] = New TParameter
	p[1].Name = "Angle2"
	ev.RegisterBMFunction("ATan2", p, MathATan2) 
	'The same for the TAN function:
	p = New TParameter[1] 
	p[0] = New TParameter
	p[0].Name = "Value"
	ev.RegisterBMFunction("Ceil", p, MathCeil) 
	'The same for the INT function:
	p = New TParameter[2] 
	p[0] = New TParameter
	p[0].Name = "Value"
	p[1] = New TParameter
	p[1].Name = "Round"
	p[1].DefaultValue = True
	ev.RegisterBMFunction("Int", p, MathInt) 
	'Register the Mult function, with 2 parameters, the second one optional.
	p = New TParameter[2] 	'We instantiate the parameter array
	p[0] = New TParameter	'Instantiate the first parameter
	p[0].Name = "Num1"		'Name of the first parameter
	p[1] = New TParameter	'Instantiate the second parameter
	p[1].Name = "Num2"		'Name of the second parameter
	p[1].DefaultValue = 2	'Default value of the second parameter
	ev.RegisterBMFunction("Mult", p, MathMult)  	'The function is registered in the evaluator object.
	'Function MathSQR:String(parameters:Tparameter[], Evaluator:TEvaluator)
	p = New TParameter[1] 
	p[0] = New TParameter
	p[0].Name = "Value"
	ev.RegisterBMFunction("Sqr", p, MathSQR) 
	'Function MathLog:String(parameters:Tparameter[], Evaluator:TEvaluator)
	p = New TParameter[1] 
	p[0] = New TParameter
	p[0].Name = "Value"
	ev.RegisterBMFunction("Log", p, MathLog) 
	'Function MathLog10:String(parameters:Tparameter[], Evaluator:TEvaluator)
	p = New TParameter[1] 
	p[0] = New TParameter
	p[0].Name = "Value"
	ev.RegisterBMFunction("Log10", p, MathLog10) 
	'Function MathIsNan:String(parameters:Tparameter[], Evaluator:TEvaluator)
	p = New TParameter[1] 
	p[0] = New TParameter
	p[0].Name = "Value"
	ev.RegisterBMFunction("IsNaN", p, MathIsNan) 
	'Function MathIsInf:String(parameters:Tparameter[], Evaluator:TEvaluator)
	p = New TParameter[1] 
	p[0] = New TParameter
	p[0].Name = "Value"
	ev.RegisterBMFunction("IsInf", p, MathIsInf) 
	'Function MathFloor:String(parameters:Tparameter[], Evaluator:TEvaluator)
	p = New TParameter[1] 
	p[0] = New TParameter
	p[0].Name = "Value"
	ev.RegisterBMFunction("Floor", p, MathFloor) 
	'Function MathExp:String(parameters:Tparameter[], Evaluator:TEvaluator)
	p = New TParameter[1] 
	p[0] = New TParameter
	p[0].Name = "Value"
	ev.RegisterBMFunction("Exp", p, MathExp) 
	'Register the chr function:
	p = New TParameter[1] 
	p[0] = New TParameter	'Instantiate the first parameter
	p[0].Name = "CharNum"		'Name of the parameter
	p[0].DefaultValue = ""	'No default value for this parameter
	ev.RegisterBMFunction("Chr", p, BSChr) 	'The function is registered in the evaluator object.
	'Register the Asc function:
	p = New TParameter[1] 
	p[0] = New TParameter	'Instantiate the first parameter
	p[0].Name = "Char"		'Name of the parameter
	p[0].DefaultValue = ""	'No default value for this parameter
	ev.RegisterBMFunction("Asc", p, BSAsc) 	'The function is registered in the evaluator object.
	'Function BSLeft:String(parameters:Tparameter[], Evaluator:TEvaluator)
	p = New TParameter[2] 	'We instantiate the parameter array
	p[0] = New TParameter	'Instantiate the first parameter
	p[0].Name = "Variable"		'Name of the first parameter
	p[1] = New TParameter	'Instantiate the second parameter
	p[1].Name = "Length"		'Name of the second parameter
	p[1].DefaultValue = 1	'Default value of the second parameter
	ev.RegisterBMFunction("Left", p, BSLeft) 
	'Function BSRight:String(parameters:Tparameter[], Evaluator:TEvaluator)
	p = New TParameter[2] 	'We instantiate the parameter array
	p[0] = New TParameter	'Instantiate the first parameter
	p[0].Name = "Variable"		'Name of the first parameter
	p[1] = New TParameter	'Instantiate the second parameter
	p[1].Name = "Length"		'Name of the second parameter
	p[1].DefaultValue = 1	'Default value of the second parameter
	ev.RegisterBMFunction("Right", p, BSRight) 
	'Function BSMid:String(parameters:Tparameter[], Evaluator:TEvaluator)
	p = New TParameter[3] 	'We instantiate the parameter array
	p[0] = New TParameter	'Instantiate the first parameter
	p[0].Name = "Variable"		'Name of the first parameter
	p[1] = New TParameter	'Instantiate the second parameter
	p[1].Name = "Start"		'Name of the second parameter
	p[1].DefaultValue = 1	'Default value of the second parameter
	p[2] = New TParameter	'Instantiate the second parameter
	p[2].Name = "Length"		'Name of the second parameter
	p[2].DefaultValue = -1	'Default value of the second parameter
	ev.RegisterBMFunction("Mid", p, BSMid) 
	'Function Lcase:String(parameters:Tparameter[], Evaluator:TEvaluator)
	p = New TParameter[1] 
	p[0] = New TParameter	'Instantiate the first parameter
	p[0].Name = "Value"		'Name of the parameter
	p[0].DefaultValue = ""	'No default value for this parameter
	ev.RegisterBMFunction("LCase", p, Lcase) 
	'Function UCase:String(parameters:Tparameter[], Evaluator:TEvaluator)
	p = New TParameter[1] 
	p[0] = New TParameter	'Instantiate the first parameter
	p[0].Name = "Value"		'Name of the parameter
	p[0].DefaultValue = ""	'No default value for this parameter
	ev.RegisterBMFunction("UCase", p, UCase) 
	'Function Input:String(parameters:Tparameter[], Evaluator:TEvaluator)
	p = New TParameter[1] 
	p[0] = New TParameter	'Instantiate the first parameter
	p[0].Name = "Prompt"		'Name of the parameter
	p[0].DefaultValue = ">"	'No default value for this parameter
	ev.RegisterBMFunction("Input", p, BsInput) 
	'Function Input:String(parameters:Tparameter[], Evaluator:TEvaluator)
	p = New TParameter[1] 
	p[0] = New TParameter	'Instantiate the first parameter
	p[0].Name = "Value"		'Name of the parameter
	p[0].DefaultValue = "1"	'No default value for this parameter
	ev.RegisterBMFunction("Not", p, BsNot) 
	'The same for the RAND function:
	p = New TParameter[2] 
	p[0] = New TParameter
	p[0].Name = "MinValue"
	p[1] = New TParameter
	p[1].Name = "MaxValue"
	ev.RegisterBMFunction("Rand", p, BSRand) 
	'SEDDRND
	p = New TParameter[1] 
	p[0] = New TParameter	'Instantiate the first parameter
	p[0].Name = "Value"		'Name of the parameter
	p[0].DefaultValue = "1"	'No default value for this parameter
	ev.RegisterBMFunction("SeedRnd", p, BSSeedRnd) 
End Function
