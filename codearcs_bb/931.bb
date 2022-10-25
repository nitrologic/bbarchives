; ID: 931
; Author: Zenith
; Date: 2004-02-11 12:26:48
; Title: quick math evaluator
; Description: Quick stack based math evaluator that converts infix to postfix, and then writes intermediate output according to your code

; ------- START OF TEST.BB
Global nState = nInfo
Include "eval.bb"

Notice("z Compiler 0.1")
Repeat
	time = MilliSecs() : Evaluate("(a-b)*sin(3-(43-5)*43+35*(433-3-sin(40)))")
	Print "Finished compiling: "+(MilliSecs()-time)+"ms"
Until Input("> ") = "quit"
Print "byeee....!"
End

; ------- END OF TEST.BB

; ------- START OF EVAL.BB
Const nNote=0,nDebug=1,nInfo=2, oper$ = "+-*/"

Type var
Field name$,adr
Field funcADR
End Type

; This evaluator is based on postfix notation, (which is also known as Reverse Polish notation)
; Once an infix statement is converted to a postfix statement, it is alot easier to output as assembler
; using a simple stack machine, you could build an interpretor/virtual machine
; -------------------------------------
; The main evaluation function, call this with your infix math expression to output intermediate assembler code
; Or whatever you want, using the RPN function and IMcode function
Function Evaluate(in$)
	Notice("evaluating: "+in,nInfo)
	test = Instr(in,")")
	While test>0
		For i=test-1 To 0 Step -1
			Select Mid(in,i,1)
				Case "("
					eval$ = Mid(in,i,test-i+1)
					RPN(eval)
					in = Replace(in,eval,"~stack")
					Exit
				Default
					If i = 0
						Error("Expecting (")
						Return
					EndIf
			End Select
		Next
		test = Instr(in,")")
	Wend
	RPN(in)
End Function

Function cPostFix$(eval$)
	Local stack$[64],sp,postfix$,var$
	If eval = "exit" Or eval = "" Return 
	For i=1 To Len(eval)
		m$ = Mid(eval,i,1)
		If Instr(oper,m)>0 ;	========================= is an operator
			If sp>0
				If Instr(oper,stack[sp-1]) > Instr(oper,m)	; does topStack have higher precedence than newOp
					While sp>0
						sp=sp-1								; pop the stack
						postfix=postfix + "," + stack[sp]
					Wend
				EndIf
			EndIf
			stack[sp] = m : sp=sp+1
			postfix = postfix+ ","+var : var = ""
		Else 				;	========================= is an operand
			var$ = var + m
		EndIf
	Next
	
	postfix = postfix + "," + var
	While sp>0
		sp=sp-1
		postfix = postfix + "," + stack[sp]
	Wend
	If Left(postfix,1)="," postfix = Right(postfix,Len(postfix)-1)
	Return postfix
End Function

; This is where you write the output of what the Reverse Polish Notation detects
Function RPN(in$)
	If Instr(in,"(")>0 Or Instr(in,")")>0
		in = Replace(in,"(","")
		in = Replace(in,")","")
	EndIf

	in = cPostFix(in)
	Print "; "+in
	For i=0 To countp(in,",")
		op$ = parse(in,",",i)
		Select op
			Case "+"		; add
				IMcode("add")
			Case "-"		; subtract or negative
				If parse(in,",",i+1)="-"
					IMcode("neg")
				Else
					IMcode("sub")
				EndIf
			Case "*"		; multiply
				IMcode("mul")
			Case "/"		; divide
				IMcode("div")
			Case "~stack"	; get a variable from the stack
			Default			; variables and functions
				If op<>""
					name$ = parse(op,"~",0)
					If Right(op,6)="~stack"
						v.var = AddVar(Left(op,Len(op)-6))
						IMcode("call",v\name)
					Else
						If isNumber(op)
							IMcode("push",op)
						Else
							v.var = AddVar(name)
							IMcode("load",op)
						EndIf
					EndIf
				EndIf
		End Select
	Next
End Function

; Intermediate code output
; Write how you want it to output your code here
Function IMcode(in$,var$="")
	Print Chr(9)+in+Chr(9)+var
End Function

; Detects whether an operand is a number or not
Function isNumber(in$)
	For i=0 To 9
		If Left(in,1) = i
			Return 1
		EndIf
	Next
End Function

; Simple variable/function adding system
Function AddVar.var(name$)
	For v.var = Each var
		If v\name = name
			Return v
		EndIf
	Next
	
	v.var = New var
	v\name = name
	
	Return v
End Function

; Debug notices for the compiler
Function Notice(in$,n_lvl=0)
	If n_lvl =< nState
		If n_lvl = nNote
			Print in
		Else
			Print "n:"+n_lvl+": "+in
		EndIf
	EndIf
End Function

Function Error(in$)
	Print "Error: "+in
End Function

; Simple parse/count tokens functions
Function parse$(in$,de$,se)
	For x=1 To Len(in)
		v$=Mid(in,x,1)
		If v=de i=i+1
		If se=i And v<>de m$=m+v
	Next
	Return m
End Function

Function countp(in$,dlm$)
	For x=1 To Len(in)
		o$=Mid(in,x,1)
		If o=dlm
			cnt=cnt+1
		EndIf
	Next
	Return cnt
End Function

; ------- END OF EVAL.BB
