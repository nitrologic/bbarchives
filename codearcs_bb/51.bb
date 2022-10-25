; ID: 51
; Author: marksibly
; Date: 2001-09-24 18:44:29
; Title: Blitz Compiler
; Description: A simple compiler written in Blitz


;Very simple compiler.
;
;Blitz does NOT generate code like this! But the parsing is similar...
;
;Statements supported: let, print, end, if, then, else
;
;Operators supported: binary +, -, *, /, <, =, >, <=, <>, >= and unary -
;
;*, / have precedence over +, -, which have precendence over <,=,>,<=,<>,>=
;
;Parenthesis can be used to group subexpressions.

;**** The program *****
;
Data "let a=1"
Data "let b=2"
Data "let c=a*b+a/b"
Data "if a<b then print a else print b"
Data "end"

Graphics 320,700,0,2
SetFont LoadFont( "blitz" )
Color 0,255,0

Type Glob
	Field name$
End Type

Global toke$,lin$,label

Const TOKE_IDENT=1,TOKE_CONST=2
Const TOKE_LET=3,TOKE_PRINT=4,TOKE_END=5
Const TOKE_ADD=6,TOKE_SUB=7,TOKE_MUL=8,TOKE_DIV=9
Const TOKE_OPENPAR=10,TOKE_CLOSEPAR=11
Const TOKE_EQ=12,TOKE_LT=13,TOKE_GT=14,TOKE_LE=15,TOKE_GE=16,TOKE_NE=17
Const TOKE_IF=18,TOKE_THEN=19,TOKE_ELSE=20

Print ";startup..."
Print "    sub   edx,edx"	;'coz idiv uses edx...

t=GetToke()
Repeat
	Print ";"+toke$+lin$
	t=ParseStmt( t )
Forever

Function ParseStmt( t )
	Select t
	Case TOKE_LET
		If GetToke()<>TOKE_IDENT SynErr()
		id$=toke$
		AddGlob( toke$ )
		If GetToke()<>TOKE_EQ SynErr()
		t=ParseExpr()
		Print "    mov   [_"+id$+"],eax"
	Case TOKE_IF
		If ParseExpr()<>TOKE_THEN SynErr()
		label=label+1:lab=label
		Print "    and   eax,eax"
		Print "    jz    __"+lab
		t=ParseStmt( GetToke() )
		If t=TOKE_ELSE
			label=label+1:lab2=label
			Print "    jmp   __"+lab2
			Print "__"+lab+":"
			t=ParseStmt( GetToke() )
			lab=lab2
		EndIf
		Print "__"+lab+":"
	Case TOKE_PRINT
		t=ParseExpr()
		Print "    call  PRINT"
	Case TOKE_END
		Print "    ret"
		Print
		For gl.Glob=Each Glob
			Print "_"+gl\name+":"
			Print "    dd    0"
		Next
		Print:Print "Done!":WaitKey
		End
	Default
		SynErr()
	End Select
	Return t
End Function

Function ParseExpr()
	Return ParseComp()
End Function

Function ParseComp()
	t=ParseTerm()
	Repeat
		Select t
		Case TOKE_LT op$="lt"	;these ops are wrong! x86 is weird!
		Case TOKE_GT op$="gt"
		Case TOKE_LE op$="le"
		Case TOKE_GE op$="ge"
		Case TOKE_EQ op$="eq"
		Case TOKE_NE op$="ne"
		Default Return t
		End Select
		Print "    push  eax"
		t=ParseTerm()
		Print "    pop   ecx"
		Print "    cmp   ecx,eax"
		Print "    s"+op$+"   eax"
		Print "    and   eax,255"
	Forever
End Function

Function ParseTerm()
	t=ParseFact()
	Repeat
		Select t
		Case TOKE_ADD
			Print "    push  eax"
			t=ParseFact()
			Print "    pop   ecx"
			Print "    add   eax,ecx"
		Case TOKE_SUB
			Print "    push  eax"
			t=ParseFact()
			Print "    mov   ecx,eax"
			Print "    pop   eax"
			Print "    sub   eax,ecx"
		Default
			Return t
		End Select
	Forever
End Function

Function ParseFact()
	t=ParseLeaf()
	Repeat
		Select t
		Case TOKE_MUL
			Print "    push  eax"
			t=ParseLeaf()
			Print "    pop   ecx"
			Print "    imul  eax,ecx"
		Case TOKE_DIV
			Print "    push  eax"
			t=ParseLeaf()
			Print "    mov   ecx,eax"
			Print "    pop   ecx"
			Print "    idiv  eax,ecx"
		Default
			Return t
		End Select
	Forever
End Function

Function ParseLeaf()
	t=GetToke()
	Select t
	Case TOKE_SUB
		t=ParseLeaf()
		Print "    neg   eax"
	Case TOKE_OPENPAR
		If ParseExpr() <> TOKE_CLOSEPAR SynErr()
		t=GetToke()
	Case TOKE_IDENT
		Print "    mov   eax,[_"+toke$+"]"
		AddGlob( toke$ )
		t=GetToke()
	Case TOKE_CONST
		Print "    mov   eax,"+toke$
		t=GetToke()
	Default
		SynErr()
	End Select
	Return t
End Function

Function AddGlob( name$ )
	For gl.Glob=Each Glob
		If gl\name=name Return
	Next
	gl=New Glob
	gl\name=name
End Function

Function SynErr()
	RuntimeError "Syntax Error"
End Function
	
Function CurrCh$()
	If lin$=""
		Read lin$
		lin$=Lower$( lin$ )+" "
	EndIf
	Return Left( lin$,1 )
End Function

Function NextCh()
	lin$=Mid$( lin$,2 )
End Function

Function GetToke()

	Repeat
		toke$=CurrCh$()
		NextCh()
	Until toke$<>" "
	
	If toke$>="0" And toke$<="9"
		Repeat
			t$=CurrCh$()
			If t$<"0" Or t$>"9" Return TOKE_CONST
			toke$=toke$+t$
			nextCh()
		Forever
	EndIf
	
	If toke$>="a" And toke$<="z"
		Repeat
			t$=CurrCh$()
			If t$<"a" Or t$>="z"
				Select toke$
				Case "let" Return TOKE_LET
				Case "print" Return TOKE_PRINT
				Case "end" Return TOKE_END
				Case "if" Return TOKE_IF
				Case "then" Return TOKE_THEN
				Case "else" Return TOKE_ELSE
				End Select
				Return TOKE_IDENT
			EndIf
			toke$=toke$+t$
			NextCh()
		Forever
	EndIf
	
	Select toke$
	Case "+" Return TOKE_ADD
	Case "-" Return TOKE_SUB
	Case "*" Return TOKE_MUL
	Case "/" Return TOKE_DIV
	Case "(" Return TOKE_OPENPAR
	Case ")" Return TOKE_CLOSEPAR
	Case "="
		If CurrCh$()="<" NextCh():Return TOKE_LE
		If CurrCh$()=">" NextCh():Return TOKE_GE
		Return TOKE_EQ
	Case "<"
		If CurrCh$()="=" NextCh():Return TOKE_LE
		If currCh$()=">" NextCh():Return TOKE_NE
		Return TOKE_LT
	Case ">"
		If CurrCh$()="=" NextCh():Return TOKE_GE
		If CurrCh$()="<" NextCh():Return TOKE_NE
		Return TOKE_GT
	End Select
	
	SynErr()
	
End Function
