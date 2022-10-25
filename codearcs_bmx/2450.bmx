; ID: 2450
; Author: Warpy
; Date: 2009-04-01 14:35:28
; Title: Shunting yard algorithm
; Description: Convert infix notation to postfix notation

Type shuntingyard
	Field in$
	Field out$
	Field ops$[][]
	Field token$[]
	
	Method parse$()
		While in
			nexttoken()
			Select token[0]
			Case "number"
				output
			Case "function"
				push
			Case ","
				While ops[0][0]<>"("
					pop
					If token[0]<>"(" output
				Wend
			Case "("
				push
			Case ")"
				While pop()<>"("
					output
				Wend
				If ops[0][0]="function"
					pop
					output
				EndIf
			Default
				If token[0][..2]="op"
					Local otoken$[]=token
					op=Int(token[0][2..])
					While Len(ops) And ops[0][0][..2]="op" And Int(ops[0][0][2..])<op
						pop
						output
					Wend
					token=otoken
					push
				EndIf
			End Select
		Wend
		While Len(ops)
			pop
			output
		Wend
		Return out
	End Method
	
	Method nexttoken()
		Select Chr(in[0])
		Case "0","1","2","3","4","5","6","7","8","9","0"
			n=0
			While n<Len(in) And in[n]>47 And in[n]<58
				n:+1
			Wend
			token=["number",in[..n]]
			in=in[n..]
		Case "*","/"
			token=["op1",in[..1]]
			in=in[1..]
		Case "+","-"
			token=["op2",in[..1]]
			in=in[1..]
		Case "("
			token=["(","("]
			in=in[1..]
		Case ")"
			token=[")",")"]
			in=in[1..]
		Case ","
			token=[",",","]
			in=in[1..]
		Default
			n=0
			While in[n]<>Asc("(")
				n:+1
			Wend
			token=["function",in[..n]]
			in=in[n..]
		End Select
	End Method
				
	Method pop$()
		token=ops[0]
		ops=ops[1..]
		Return token[0]
	End Method
	
	Method push()
		ops=[token]+ops
	End Method	
	
	Method output()
		out:+token[1]+" "
	End Method
End Type

Function shunt$(in$)
	s:shuntingyard=New shuntingyard
	s.in=in
	Return s.parse()
End Function


While 1
	Print shunt(Input())
Wend
