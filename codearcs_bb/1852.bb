; ID: 1852
; Author: mindstorms
; Date: 2006-10-25 17:14:03
; Title: fractions
; Description: instead of decimals

Type fraction
	Field num
	Field den
	Field whol
End Type

;******************************************************************************************
;useful, helper functions
Function fraction%(whol=0,num=0,den=0)
	f.fraction = New fraction
	f\whol = whol
	f\num = num
	f\den = den
	Return Handle(f)
End Function

Function deleteFraction%(frac%)
	f.fraction = Object.fraction(frac)
	Delete f
	Return 0
End Function

Function textToScreen(frac,x=0,y=0,label$="")	;prints the fraction to screen at x,y,coords
	f.fraction = Object.fraction(frac)
	Text x,y,label+f\whol+" "+f\num+"/"+f\den
End Function

Function decToFrac%(dec#)		;creates a simplified fraction of a decimal
	s$ = Str(dec)
	s = Right(s,Len(s)-Instr(s,"."))
	amount = Len(s)
	place = 1
	For i = 1 To amount
		place = 10*place
	Next
	
	f = fraction(0,dec*place,place)
	Simplify(f)
	
	Return f
End Function

Function FracToDec#(frac%)	;creates a decimal from a fraction
	f.fraction = Object.fraction(frac)
	Return f\whol + (Float(f\num)/Float(f\den))
End Function

Function Simplify(frac%)	;puts a fraction in it's simplest form
	f.fraction = Object.fraction(frac)
	Repeat 
		GCD = Find_GCD(f\num,f\den)
		If GCD = 1 Then Exit
		f\num = f\num/GCD
		f\den = f\den/GCD
	Forever
	
End Function

Function make_mixed(frac)		;make the fraction a mixed number
	f.fraction = Object.fraction(frac)
	f\whol = f\whol + Floor(f\num/f\den)
	f\num = f\num Mod f\den
End Function

Function make_unbalanced(frac)		;makes the fraction have no whole part
	f.fraction = Object.fraction(frac)
	If f\whol <> 0 Then 
		f\num = f\num + (f\whol*f\den)
		f\whol = 0
	EndIf
End Function

Function copy_fraction%(frac)		;copies a fraction object into a new fraction object
	f.fraction = Object.fraction(frac)
	Return fraction(f\whol,f\num,f\den)
End Function

Function invert_frac(frac)		;makes it unbalanced and flips the numerator with the denominator
	make_unbalanced(frac)
	f.fraction = Object.fraction(frac)
	den = f\num
	num = f\den
	f\num = num
	f\den = den
End Function

Function Find_GCD%( a, b )
	Local t = 0
	If a<=0 Then If a = 0 Then Return b: Else a = -a
	If b<=0 Then If b = 0 Then Return a: Else b = -b

	While( Not( ( a Or b )And 1 ) )
		a = a Shr 1
		b = b Shr 1
		t = t + 1
	Wend

	While( Not( a And 1 ) ): a = a Shr 1: Wend
	While( Not( b And 1 ) ): b = b Shr 1: Wend
	
	While a <> b
		If a>b
			a = a - b
			Repeat: a = a Shr 1: Until a And 1
		Else
			b = b - a
			Repeat: b = b Shr 1: Until b And 1
		EndIf
	Wend
	Return a Shl t
End Function

Function find_LCM%(num1,num2)
	If num1 < num2 Then 
		c_num1 = num1	;lesser
		c_num2 = num2	;greater
	Else
		c_num1 = num2
		c_num2 = num1
	EndIf
	Local mul1 = 0	;lesser
	Local mul2 = 0	;greater
	Repeat
		mul1 = 1
		mul2 = mul2 + 1
		Repeat
			test1 = c_num1*mul1
			test2 = c_num2*mul2
			If test1 = test2 Then
				Return test1
			EndIf
			mul1 = mul1+1
		Until test1 > test2
	Forever
End Function

;******************************************************************************
;multiplication
Function multiply_fracs%(frac1,frac2,out_mixed=False)		;returns frac1*frac2 in the form of out_mixed(true=mixed,false=unbalanced)
	temp1 = copy_fraction(frac1)
	temp2 = copy_fraction(frac2)
	make_unbalanced(temp1)
	make_unbalanced(temp2)
	
	f1.fraction = Object.fraction(temp1)
	f2.fraction = Object.fraction(temp2)
	
	out = fraction(f1\whol*f2\whol,f1\num*f2\num,f1\den*f2\den)
	Simplify(out)
	If out_mixed Then make_mixed(out)
	Delete f1
	Delete f2
	Return out
End Function

Function multiplyFrac_dec%(frac,dec,out_mixed=False)	;multiplies a fraction by a decimal and returns a fraction
	temp = decToFrac(dec)
	out = multiply_fracs(frac,temp,out_mixed)
	deleteFraction(temp)
	Return out	
End Function

Function multiplyFrac_dec2#(frac,dec)		;multiplies a fraction by decimal and returns a decimal
	temp# = FracToDec(frac)
	Return temp*dec
End Function

;********************************************************************************
;division
Function divide_fracs%(frac1,frac2,out_mixed=False)	;returns frac1/frac2 (or frac1*(frac2^-1))
	temp = copy_fraction(frac2)
	invert_frac(temp)
	out = multiply_fracs(frac1,temp,out_mixed)
	deleteFraction(temp)
	Return out
End Function

Function divideFrac_dec%(frac,dec,out_mixed=False)		;returns frac/dec, or frac*(1/dec) as a fraction
	temp = decToFrac(dec)
	invert_frac(temp)
	out = multiply_fracs(frac,temp,out_mixed)	
	deleteFraction(temp)
	Return out
End Function

Function divideFrac_dec2#(frac,dec)		;returns frac/dec as a decimal
	temp# = FracToDec(frac)
	Return temp/dec
End Function

Function divideDec_frac%(dec,frac,out_mixed=False)		;returns dec/frac as a fraction
	temp = decToFrac(dec)
	temp2 = copy_fraction(frac)
	invert_frac(temp2)
	out = multiply_fracs(temp,temp2,out_mixed)
	deleteFraction(temp)
	deleteFraction(temp2)
	Return out
End Function

Function divideDec_frac2#(dec,frac)			;returns dec/frac as a decimal
	temp# = FracToDec(frac)
	Return dec/temp
End Function

;***********************************************************************************
;addition
Function add_fracs%(frac1,frac2,out_mixed=False)		;returns frac1+frac2 as a fraction
	temp1 = copy_fraction(frac1)
	temp2 = copy_fraction(frac2)
	make_unbalanced(temp1)
	make_unbalanced(temp2)
	f1.fraction = Object.fraction(temp1)
	f2.fraction = Object.fraction(temp2)
	
	LCM% = find_LCM(f1\den,f2\den)
	scaler1% = LCM/f1\den
	scaler2% = LCM/f2\den
	
	f1\num = f1\num*scaler1
	f2\num = f2\num*scaler2
	
	out = fraction(0,f1\num+f2\num,LCM)	
	
	Simplify(out)
	If out_mixed Then make_mixed(out)
	Delete f1
	Delete f2
	Return out
End Function

Function addFrac_dec%(frac,dec,out_mixed=False)			;returns frac+dec as a fraction
	temp = decToFrac(dec)
	out = add_fracs(frac,temp,out_mixed)
	deleteFraction(temp)
	Return out
End Function

Function addFrac_dec2#(frac,dec)		;returns frac+dec as a decimal
	temp# = FracToDec(frac)
	Return temp+dec
End Function

;***********************************************************************************
;subtraction
Function subtract_fracs%(frac1,frac2,out_mixed=False)		;returns frac1-frac2 as a fraction
	temp1 = copy_fraction(frac1)
	temp2 = copy_fraction(frac2)
	make_unbalanced(temp1)
	make_unbalanced(temp2)
	f1.fraction = Object.fraction(temp1)
	f2.fraction = Object.fraction(temp2)
	
	LCM% = find_LCM(f1\den,f2\den)
	scaler1% = LCM/f1\den
	scaler2% = LCM/f2\den
	
	f1\num = f1\num*scaler1
	f2\num = f2\num*scaler2
	
	out = fraction(0,f1\num-f2\num,LCM)	
	
	Simplify(out)
	If out_mixed Then make_mixed(out)
	Delete f1
	Delete f2
	Return out
End Function

Function subtractFrac_dec%(frac,dec,out_mixed=False)		;reuturns frac-dec as a fraction
	temp = decToFrac(dec)
	out = subtract_fracs(frac,temp,out_mixed)
	deleteFraction(temp)
	Return out
End Function

Function subtractFrac_dec2#(frac,dec)		;returns frac-dec as a decimal
	temp# = FracToDec(frac)
	Return temp-dec
End Function

Function subtractDec_Frac%(dec,frac,out_mixed=False)		;reuturns dec-frac as a fraction
	temp = decToFrac(dec)
	out = subtract_fracs(temp,frac,out_mixed)
	deleteFraction(temp)
	Return out
End Function

Function subtractDec_Frac2#(dec,frac)		;returns dec-frac as a decimal
	temp# = FracToDec(frac)
	Return dec-temp
End Function

;**********************************************************************************
;other useful math functions
Function fraction_abs%(frac)
	f.fraction = Object.fraction(copy_fraction(frac))
	out = fraction(Abs(f\whol),Abs(f\num),Abs(f\den))
	Delete f
	Return out
End Function

Function fraction_sqr%(frac,out_mixed=False)
	temp1 = copy_fraction(frac)
	
	f_temp# = FracToDec(temp1)
	f_temp = Sqr(f_temp)
	
	out = decToFrac(f_temp)
	deleteFraction(temp1)
	Return out
End Function
