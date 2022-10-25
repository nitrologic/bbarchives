; ID: 2845
; Author: Nate the Great
; Date: 2011-05-03 07:08:31
; Title: an interesting method of calculating the squareroot
; Description: with strings!!!

Local sp:String = Input("Calculate the Squareroot of... ")

Local dec:Int = Int(Input("Number of decimal places?"))

Local pairs:Int[dec]
Local i:Int

Local places:Int = Ceil((Instr(sp,".")-1)/2.0)
If Instr(sp,".") = 0 Then places = Ceil((Len(sp))/2.0)
Print places

If Instr(sp$,".") Then
	If (Instr(sp$,".")/2.0) = Int(Instr(sp$,".")/2.0) Then
		sp = "0" + sp
	EndIf
	If ((Len(sp)-Instr(sp,"."))/2.0) <> Int((Len(sp)-Instr(sp,"."))/2.0) Then
		sp = sp + "0"
	EndIf
	sp = Left(sp,Instr(sp$,".")-1) + "" + Right(sp,Len(sp)-Instr(sp$,"."))
	For i = 0 Until Len(sp)/2
		pairs[i] = Int(Mid(sp,i*2+1,2))
	Next
Else
	If (Len(sp)/2.0) >< Int(Len(sp)/2.0)
		sp$ = "0" + sp$
	EndIf
	For i = 0 Until Len(sp)/2
		pairs[i] = Int(Mid(sp,i*2+1,2))
	Next
EndIf

i = 0

Local ANS$
Local Remain$
Local x$

Print "Decimals Calculated:"

For i = 0 Until dec
	
	remain = mul("100",remain)
	remain = add(remain,pairs[i])
	x = "0"
	Repeat
		x = add(x,"1")
	Until Instr(sub(remain,mul(x,add(mul("20",ans),x))),"-")
	x = sub(x,"1")
	remain = sub(remain,mul(x,add(mul("20",ans),x)))
	ans = mul("10",ans)
	ans = add(ans,x)
	Print i
Next
Print "Answer: "
Print Left(ans,places)+"."+Right(ans,Len(ans)-places)


               
Function Mul:String(a$,b$)
	Local c$
	
	While a <> "0"
		c = add(c,b)
		a = sub(a,"1")
	Wend
	
	Return c
End Function

Function add$(a$,b$)
	Local ans$
	a = "0" + a
	b = "0" + b
	While Len(a) < Len(b)
		a = "0" + a
	Wend
	While Len(b) < Len(a)
		b = "0" + b
	Wend
	
	Local dec:Int = Len(b)
	Local carry:Int
	For Local i:Int = dec To 1 Step -1
		Local c:Int = 0
		c = Int(Mid(a,i,1)) + Int(Mid(b,i,1)) + carry
		carry = 0
		While c > 9
			c = c - 10
			carry :+1
		Wend
		
		ans = c + ans
	Next
	
	While Left(ans,1) = "0" And ans.length > 1
		ans = Right(ans,Len(ans)-1)
	Wend
	If ans = "-0" Then Return "0"
	Return ans 
End Function

Function sub$(a$,b$)
	Local ans$
	While Len(a) < Len(b)
		a = "0" + a
	Wend
	While Len(b) < Len(a)
		b = "0" + b
	Wend
	
	Local dec:Int = Len(b)
	
	Local exmp:Int
	For Local i:Int = 1 To dec
		If Int(Mid(a,i,1)) > Int(Mid(b,i,1)) Then
			exmp = True
			Exit
		ElseIf Int(Mid(a,i,1)) < Int(Mid(b,i,1)) Then
			exmp = False
			Exit
		EndIf
	Next
	If exmp = False Then
		Local tmp$ = a
		a = b
		b = tmp
	EndIf
	
	Local take:Int
	For i = dec To 1 Step -1
		c = Int(Mid(a,i,1)) - Int(Mid(b,i,1)) - take
		take = 0
		While c < 0
			c = c + 10
			take :+ 1
		Wend
		
		ans = c + ans
	Next
	
	While Left(ans,1) = "0" And ans.length > 1
		ans = Right(ans,Len(ans)-1)
	Wend
	If exmp = False Then ans = "-"+ans
	If ans = "-0" Then Return "0"
	Return ans
End Function
