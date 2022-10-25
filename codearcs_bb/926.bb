; ID: 926
; Author: Zenith
; Date: 2004-02-08 21:21:15
; Title: Infix to Postfix
; Description: Infix to Postfix conversion (without Brackets, heh)

;Example:
Print "Postfix: "+cPostFix(Input())
;End of Example

Function cPostFix$(eval$)
	Local stack$[64],sp
	
	If eval = "exit" Or eval = "" Return
	Local oper$ = "+-*/"
	Local postfix$
	For i=1 To Len(eval)
		m$ = Mid(eval,i,1)
		Select m
			Case "-","+","*","/"							; is an operator
				If sp>0
					testA = Instr(oper,stack[sp-1])			; topStack operator
					testB = Instr(oper,          m)			; newOp operator
					If testA > testB						; does topStack have higher precedence than newOp
						While sp>0
							sp=sp-1							; pop the stack
							postfix=postfix + stack[sp]
						Wend
						stack[sp] = m
						sp=sp+1
					Else
						stack[sp] = m						; otherwise push the new operator to the list
						sp=sp+1
					EndIf
				Else
					stack[sp] = m							; push new operator
					sp=sp+1
				EndIf
			Default											; is an operand
				postfix=postfix+m
		End Select
	Next
	
	While sp>0
		sp=sp-1
		postfix = postfix + stack[sp]
	Wend
	Return postfix
End Function
