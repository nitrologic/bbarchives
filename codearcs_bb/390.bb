; ID: 390
; Author: Zenith(Matt Griffith)
; Date: 2002-08-08 20:24:58
; Title: Math Evaluation
; Description: With this you can eval mathmatics through a string

; zenith's Math Evaluation Function (WITH EXAMPLE!)
; please give me credit if you use this in anyway shape or form (haha)
; if you have questions, please don't spam me by email
; but find me on IRC at: irc.blitzed.org on channel #blitzbasic or #blitzcoder
; If you're jsut going to steal my code and give me no credits, just remember I worked very hard on this.. :)

; Here is where you add Mathmatical operators
Const opmax=10
Global op$[opmax]
; op you use in to eval on left, asm op name on right
; use with proper math ordering, where 0=last thing it checks, opmax= first thing it checks
op[0]="+,add"
op[1]="-,sub"
op[2]="*,mul"
op[3]="/,div"
op[4]="%,mod"
; bitwise ops
op[5]="&,and"
op[6]="|,or"
op[6]="^,eor"
op[7]=">>,shr"
op[8]="<<,shl"
;logical ops
op[9]="&&,and"
op[10]="||,or"

; Create a function called int()
f.func = New func
f\name="calldll"

; ------------- EXAMPLE ------------
Global file=WriteFile("output.txt"),time=MilliSecs()
evalme$="-34--rab*345/calldll(34)" 	;what we will be evaluating
evalme$=cleanupNeg(evalme)
Printf "// problem: "+evalme
eval(evalme)								; evaluate the problem
Printf "// "+(MilliSecs()-time)+"ms taken"	; how long it took to eval
CloseFile(file)								; close the file
ExecFile "output.txt"						; open up the file
End
; ----------------------------------


; list of functions
Type func
Field name$
End Type

Function cleanupneg$(in$)
	Local out$
	For i=1 To Len(in)
		m$=Mid(in,i,1)
		n$=Mid(in,i+1,1)
		If m="-" And n="-"
			o$=Right(in,Len(in)-i-1)
			in$=Left(in,i-1)+"-(-"+o+")"
			printf in
		EndIf
	Next
	Return in
End Function 

Function eval(in$)
	; In my eval function, once something is worked out, it is pushed into the stack..
	; then what we just worked is renamed to 'stack'
	; whenever it reads stack, it pop's it to a register and then uses it
	Local vari$
	
	For i=1 To Len(in)
		m$=Mid(in,i,1)
		If m="("
			ble$=find_back(in,i,2)
			f.func = findfunc(ble)
			; we've found a function
			If f<>Null
				io$=find_next(in,i,0)			; Lets find the whole function name
				name$=f\name+"("+io+")"
				printf "// function: "+name
				in=Replace(in,name,"stack")		; now lets replace that whole function with 'stack'
				Select f\name
					Case "calldll"
						eval(io)				; io is the parameters, whereas calldll() only has one parameter
						printf "pop r1"			; asm output for this function
						Printf "run 'dll',r1"
						Printf "push 0"			; even though this function doesnt return anything, it needs to return 0
				End Select
			Else
				io$=find_next(in,i,0)			; well it's not a function, so it has to be some () we have to work out
				eval(io)
				io="("+io+")"
				in=Replace(in,io,"stack")		; now that we've worked it out, lets rename it to 'stack'
				Printf "// out: "+in
			EndIf
		EndIf
	Next
	
	; my eval function uses a first in, last out stack
	; IE: push 45, push 23, pop r1 = 23, pop r2 = 45

	For x=opmax To 0 Step -1					; we're checking for every single optoken
												; we do it backwards for proper math ordering
		If in="stack" Exit						; if in just equals stack, then we're finished
		For i=1 To Len(in)
			m$=Mid(in,i,1)
			tokop$=parse(op[x],",",0)
			If Mid(in,i,Len(tokop))=tokop
				a$=find_back(in,i)
				b$=find_next(in,i+(Len(tokop)-1))
				Printf "// in: "+a+tokop+b
				in=Replace(in,a+tokop+b,"stack")

				If a="stack"					; if a = stack
					Printf "pop r1"				; then we have to pop the value out of the stack
				ElseIf Int(a)=a					; if a = a value
					Printf "mov r1,"+a			; then we simply just mov r1 to the value
				ElseIf a=""						; a negativity
					a="-"
				ElseIf Left(a,1)="-"
					Printf "ldr r1,"+Right(a,Len(a)-1)
					Printf "sgn r1"
					;printf "mov r3,r1"
					;Printf "sub r1,r1,r1"
					;printf "sub r1,r1,r3"
				Else							; but if its a variable
					Printf "ldr r1,"+a			; we have to loadregister with that variable
				EndIf
												; the same thing goes for the next part with variable b
				If b="stack"
					If a="-"
						Printf "pop r1"
					Else
						Printf "pop r2"
					EndIf
				ElseIf Int(b)=b
					If a="-"
						Printf "mov r1,"+b
					Else
						Printf "mov r2,"+b
					EndIf
				ElseIf b=""						; a negativity
					b="-"
				ElseIf Left(b,1)="-"
					Printf "ldr r2,"+Right(b,Len(b)-1)
					Printf "sgn r2"
					;printf "mov r3,r2"
					;Printf "sub r2,r2,r2"
					;printf "sub r2,r2,r3"
				Else
					If a="-"
						Printf "ldr r1,"+b
					Else
						Printf "ldr r2,"+b
					EndIf
				EndIf
				
				If a="-"						; output negativity
					
					Printf "sgn r1"
					;printf "mov r3,r2"
					;Printf "sub r1,r2,r2"
					;printf "sub r1,r1,r3"
				ElseIf b="-"
					Printf "sgn r2"
					;printf "mov r3,r2"
					;Printf "sub r1,r1,r1"
					;printf "sub r1,r1,r3"
				Else
					Printf parse(op[x],",",1)+" r1,r1,r2"	; now we do the math for it
				EndIf
				Printf "push r1"						; last we push this value into the stack
				If in="stack" Exit				; if in just equals stack, then we're finished
				Printf "// out: "+in
			EndIf
		Next
	Next
	
	If in<>"stack"								; meanwhile, if in still doesn't equal stack
		If Int(in)=in							; which means, no work was done to it yet..
			Printf "push "+in					; we just have to push it to the stack ourselves :)
		Else
			Printf "ldr r1,"+in
			Printf "push r1"
		EndIf
	EndIf
End Function

; just an easy output function
Function Printf(in$)
	WriteLine file,in
End Function

; find a number or variable until you hit another optoken (before x position)
Function find_back$(in$,x,io=1)
	Local out$
	Select io
		; looking for a function
		Case 2
			For i=x-1 To 1 Step -1
				For e=opmax To 0 Step -1
					m$=Mid(in,i,Len(parse(op[e],",",0)))
					If m=parse(op[e],",",0) Return out
				Next
				m$=Mid(in,i,1)
				If m="(" Return out
				out=m+out
			Next
		; looking for optoks
		Case 1
			For i=x-1 To 1 Step -1
				For e=opmax To 0 Step -1
					m$=Mid(in,i,Len(parse(op[e],",",0)))
					If m=parse(op[e],",",0) Return out
				Next
				m$=Mid(in,i,1)
				out=m+out
			Next
		; finding a (
		Case 0
			For i=x-1 To 1 Step -1
				m$=Mid(in,i,1)
				If m="(" Return out
				out=m+out
			Next
	End Select
	Return out
End Function

; find a number or variable until you hit another optoken (after x position)
Function find_next$(in$,x,io=1)
	Local out$
	If io=1
		; find op
		For i=x+1 To Len(in)
			For e=opmax To 0 Step -1
				m$=Mid(in,i,Len(parse(op[e],",",0)))
				If m=parse(op[e],",",0); And Mid(in,i+1,1)<>"-" And count=0
					Return out
				EndIf
			Next
			m$=Mid(in,i,1)
			out=out+m
		Next
	Else
		; find paras
		For i=x+1 To Len(in)
			m$=Mid(in,i,1)
			If m="(" sub=sub+1
			If m=")" If sub=0 Return out Else sub=sub-1
			out=out+m
		Next
	EndIf
	Return out
End Function

; just a quick find function.. function :)
Function findfunc.func(name$)
	For f.func = Each func
		If f\name=name Return f
	Next
	Return Null
End Function

; parse a string with a delim character at a position
; example: in$="var,ghe,oe,oxw"
; to get "ghe" you would do parse(in,",",1)
Function parse$(in$,de$,se)
	For x=1 To Len(in)
		v$=Mid(in,x,1)
		If v=de i=i+1
		If se=i And v<>de m$=m+v
	Next
	Return m
End Function
