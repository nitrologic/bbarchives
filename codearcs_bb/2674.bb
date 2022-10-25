; ID: 2674
; Author: Streaksy
; Date: 2010-03-23 04:37:51
; Title: skn3[ac]'s String/Int/Float Expression Evaluator with functions, variables, and more operators
; Description: I've been playing with skn3[ac]'s expression evaluator and came up with this

;String/Int/Float evaluator for within Blitz by skn3[ac]
;Extra Operators, none-crash error trapping, functions and variables added by Streaksy

;Currently supported functions: Sin(),Cos(),Tan(),Atan(),Rnd(),Rand()


;---[ Variables ]------------------
Global MaxVars=2000,MaxStringVars=2000,MaxFloatVars=2000
Global Vars,StringVars,FloatVars
Dim VarName$(MaxVars)
Dim VarValue(MaxVars)
Dim StringVarName$(MaxVars)
Dim StringVarValue$(MaxVars)
Dim FloatVarName$(MaxVars)
Dim FloatVarValue#(MaxVars)


;---[ Operators ]------------------
Const OpTotal=15
Dim op$(OpTotal)
	op$(1)="^";POW
	op$(2)="*";MUL
	op$(3)="/";DIV
	op$(4)="+";ADD
	op$(5)="-";SUB
	op$(6)="|";OR
	op$(7)="&";AND
	op$(8)="~";XOR
	op$(9)="%";MOD
	op$(10)="=";EQU
	op$(11)="<";LES
	op$(12)=">";MOR
	op$(13)="{";ELS
	op$(14)="}";EMR
	op$(15)="@";NOT ;not really not... but "<>"
Global ParseSeps$
For t=1 To optotal:parseseps=parseseps+op(t):Next
	
;---[ Reserverd ]------------------
Const ResTotal=11
Dim res$(ResTotal)
	res$(1)="0"
	res$(2)="1"
	res$(3)="2"
	res$(4)="3"
	res$(5)="4"
	res$(6)="5"
	res$(7)="6"
	res$(8)="7"
	res$(9)="8"
   res$(10)="9"
   res$(11)="."

;---[  Special  ]------------------
Global SPEC_quote$=Chr$(34)

;---[  Eval_Error's  ]------------------
Dim ers$(8)
	ers$(1)="Unexpected ')'"
	ers$(2)="Expecting expression"
	ers$(3)="Expecting operator"
	ers$(4)="Ilegal character"
	ers$(5)="Missing ')'"
	ers$(6)="Missing " + SPEC_quote$ + " end quote"
	ers$(7)="Value types incorrect"
	ers$(8)="Result of expression out of range"
	
Global CalcError$
	
	
;---Test---
;---Test---
;---Test---
SetVar "st$","teststring" ;setting strings doesnt evaluate the value.  Use SetVar "st$",calculate("teststring"+"teststring") for that.
SetVar "t","51+12"
Graphics 800,300,32,2
Color 255,255,255
Print "Type an expression:"
Print "The following variables are set: st$="+Chr(34)+getvar("st$")+Chr(34)+", t="+getvar("t")
Print "The following functions are available: Sin(), Cos(), Tan(), ATan(), Rnd(), Rand()"
Print ""
.restart

Color 255,255,0
ask$=Input$(">")
If ask="" Then End
result$=calculate$(ask$)
;Color 100,255,100:Print "Tokenised: "+filtersum(ask)
If CalcError<>"" Then Color 255,0,0:Print CalcError Else Color 100,255,255:Print result
Print ""
Goto restart
;-----------
;-----------
;-----------






Function GetVar$(n$)
n=Lower(n)
If Right(n,1)="#" Then typ=1:n=Left(n,Len(n)-1)
If Right(n,1)="$" Then typ=2:n=Left(n,Len(n)-1)
	If typ=0 Then
	For t=1 To vars
	If varname(t)=n Then Return varvalue(t)
	Next
	EndIf
		If typ=0 Or typ=1
		For t=1 To floatvars
		If floatvarname(t)=n Then Return floatvarvalue(t)
		Next
		EndIf
			If typ=0 Or typ=2
			For t=1 To stringvars
			If stringvarname(t)=n Then Return stringvarvalue(t)
			Next
			EndIf
If typ=0 Then Return 0
If typ=1 Then Return Float(0)
If typ=2 Then Return ""
End Function





Function SetVar(n$,ex$) ;dont let a variable name start with anything other than a letter!!  This function should really check for that...
n=Lower(n)
If Right(n,1)="#" Then typ=1:n=Left(n,Len(n)-1)
If Right(n,1)="$" Then typ=2:n=Left(n,Len(n)-1)
If typ=0 Then
For t=1 To vars
If varname(t)=n Then varvalue(t)=calculate(ex):Return
Next
EndIf
	If typ=0 Or typ=1
	For t=1 To floatvars
	If floatvarname(t)=n Then floatvarvalue(t)=calculate(ex):Return
	Next
	EndIf
		If typ=0 Or typ=2
		For t=1 To stringvars
		If stringvarname(t)=n Then stringvarvalue(t)=ex:Return
		Next
		EndIf
If typ=0 Then
If vars=maxvars Then RuntimeError "Out of integer variable space."
vars=vars+1:varname(vars)=n:varvalue(vars)=calculate(ex)
Return
EndIf
	If typ=1 Then
	If floatvars=maxfloatvars Then RuntimeError "Out of float variable space."
	floatvars=floatvars+1:floatvarname(floatvars)=n:floatvarvalue(floatvars)=calculate(ex)
	Return
	EndIf
		If typ=2 Then
		If stringvars=maxstringvars Then RuntimeError "Out of string variable space."
		gotspeech=Instr(ex,Chr(34))
		stringvars=stringvars+1:stringvarname(stringvars)=n
		If gotspeech Then stringvarvalue(stringvars)=calculate(ex)
		If Not gotspeech Then stringvarvalue(stringvars)=calculate(Chr(34)+ex+Chr(34))
		Return
		EndIf
End Function











Function calculate$(sum$)
If Trim(sum)="" Then Return 0
calcerror=""
sum$=filtersum(sum)
	BRAK_x=1
	BRAK_found=0
	BRAK_getchar$=""
	BRAK_mode=False
	BRAK_marker=1
	
	;--[ Info ] ----
	;This loop cycles through the string 'sum$' finding the 
	;highest bracket. Once it finds it will calculate within the bracket.
	;With the result from that calculation, it replaces the (x) bracket
	;and its contense wit hthe result of the calculation.
	Repeat
		;Get character from 'sum$' using the offset of 'BRAK_x'
		BRAK_getchar$=Mid$(sum$,BRAK_x,1)
		;Test character's properties
		If BRAK_mode=False Then
			If BRAK_getchar$=SPEC_quote$ Then
				BRAK_mode=True
				BRAK_x=BRAK_x+1
			ElseIf BRAK_getchar$="("
				BRAK_found=BRAK_found+1
				BRAK_marker=BRAK_x
				BRAK_x=BRAK_x+1
			ElseIf BRAK_getchar$=")"
				;--< Eval_Error >--"Unexpected ')'"
				If BRAK_found=0 Then
					Eval_Error ers$(1):Return
				Else
					If BRAK_x=BRAK_marker+1 Then
						;--< Eval_Error >--"Expecting statement"
						Eval_Error ers$(2):Return
					Else
						;--[ Found suitable bracket ]----
						;Calculate bracket contents
						GET_left$=Left$(sum$,BRAK_marker-1)
						GET_sum$=Mid$(sum$,BRAK_marker+1,BRAK_x-BRAK_marker-1)
						GET_right$=Right$(sum$,Len(sum$)-BRAK_x)
						sum$=GET_left$ + calculateSUB$(GET_sum$) + GET_right$
						;reset values
						BRAK_x=1
						BRAK_found=BRAK_found-2
						BRAK_marker=1
					End If
				End If
			Else
				If BRAK_x>Len(sum$) Then
					If BRAK_found>0 Then
						Eval_Error ers$(5):Return
					Else
						;--[ Found end of sum sucessfully ]----
						;check not end
						;Calculate sum$
						If Len(sum$)>2 Then
							sum$=calculateSUB$(sum$)
						End If
						;END
						Return sum$
					End If
				Else
					BRAK_x=BRAK_x+1
				End If
			End If
		Else
			;--[ Info ] ----
			;This section is to make sure quotes are read properly.
			If BRAK_getchar$=SPEC_quote$ Then
				BRAK_x=BRAK_x+1
				BRAK_mode=False
			Else
				;--< Eval_Error >--"Unexpected End to statement"
				If BRAK_x>=Len(sum$) Then
					Eval_Error ers$(6):Return
				Else
					BRAK_x=BRAK_x+1
				End If
			End If
		End If
		
	Forever 
End Function






Function calculateSUB$(sum$)
	CALC_x=1
	CALC_mode=1
	CALC_start=False
	CALC_getchar$=""
	CALC_makeTok$=""
	CALC_marker=1
	
	CALC_op$=""
	
	CALC_val1$=""
	CALC_readtype1$=""
	
	CALC_val2$=""
	CALC_readtype2$=""
	
	CALC_type$=""
	;---LOOP---
	;This loop checks the entire sum in order of OP preferance
	For TEST_loop=1 To optotal
		Repeat
			CALC_getchar$=Mid$(sum$,CALC_x,1)

			If CALC_mode=1 Then
				;expected end of statement
				If CALC_x>Len(sum$) Then
					CALC_x=1
					CALC_start=False
					Exit
				End If
				;Find start of first value
				If CALC_Start=False Then
					If CALC_getchar$=" " Then
						CALC_x=CALC_x+1
					Else
						CALC_marker=CALC_x
						;Detect type of value to read in
						If CALC_getchar$=SPEC_quote$ Then
							;IS a string
							CALC_readtype1$="string"
							CALC_start=True
							CALC_maketok$=""
							CALC_x=CALC_x+1
						Else
							;IS a value
							CALC_readtype1$="value"
							CALC_start=True
							CALC_maketok$=""
						End If
					End If
				Else
					;---[ Parse value until operater is met ]------
					If CALC_readtype1$="string" Then
						;Looking for string
						If CALC_getchar$=SPEC_quote$ Then
							If CALC_x=Len(sum$) Then
								CALC_x=1
								CALC_start=False
								Exit
							Else
								;Find operator after quote
								GETSUB$=""
								Repeat
									CALC_x=CALC_x+1
									If CALC_x=Len(sum$) Then
										CALC_x=1
										CALC_start=False
										Exit
									End If
									GETSUB$=Mid$(sum$,CALC_x,1)
								Until GETSUB$<>" "
								;Character after quote has been found
								;
								;If character = add and test loop then continue
								If op$(TEST_loop)=GETSUB$ Then
									;Everything matches and is ready
									
									;Check needs expression Eval_Error
									If CALC_x=Len(sum$) Then
										Eval_Error ers$(2):Return
									Else
										;SWITCH TO MODE 2
										CALC_val1$=CALC_maketok$
										CALC_op$=op$(4)
										CALC_mode=2
										CALC_x=CALC_x+1
										CALC_maketok$=""
										CALC_start=False
										;Stop
									End If
								Else
									;Character was not ADD 
									;(only OP that can follow a String)
									;so check OP is valid
									TEST_skip=False
									For TEST_array=1 To optotal
										If op$(TEST_array)=GETSUB$ Then
											TEST_skip=True
											Exit
										End If
									Next
									If TEST_skip=True Then
										CALC_start=False
										CALC_x=CALC_x+1
									Else
										Eval_Error ers$(4) :Return
									End If
								End If
							End If
						Else
							If CALC_x=Len(sum$) Then
								Eval_Error ers$(6):Return
							Else
								CALC_maketok$=CALC_maketok$+CALC_getchar$
								CALC_x=CALC_x+1
							End If
						End If
					Else
						;Looking for int / float
						;--[info]--
						;Look For negative value symol First
						If CALC_getchar$="-" And Len(CALC_maketok$)=0 Then
							CALC_maketok$=CALC_maketok$+CALC_getchar$
							If CALC_x=Len(sum$) Then
								Eval_Error ers$(2):Return
							Else
								CALC_x=CALC_x+1
							End If
						Else
							;CALC_getchar$ is not a negative symbol
							;of the number so detect reserved chars
							TEST_found=False
							For TEST_array=1 To restotal
								If res$(TEST_array)=CALC_getchar$ Then
									;Test to see if it turns INT into FLOAT
									If TEST_Array=11 Then
										If CALC_readtype1$="float" Then
											;--< Eval_Error >--Float type already set extra '.'
											Eval_Error ers$(4):Return
										Else
											CALC_readtype1$="float"
										End If
									End If
									TEST_found=True
									Exit
								End If
							Next
							;If Reserved character was met then add it to the token
							If TEST_found=True Then
								CALC_maketok$=CALC_maketok$+CALC_getchar$
								CALC_x=CALC_x+1
							Else
								If op$(TEST_loop)=CALC_getchar$ Then
									If Len(CALC_maketok$)=0 Then
										Eval_Error ers$(4):Return
									Else
									
									
										If CALC_x=Len(sum$) Then
											Eval_Error ers$(2):Return
										Else
											;set values
											CALC_val1$=CALC_maketok$
											CALC_op$=CALC_getchar$
											CALC_mode=2
											;reset values
											CALC_start=False
											CALC_x=CALC_x+1
											CALC_maketok$=""
										End If
									
									End If
								Else
									;Make sure is proper character
									TEST_found=False
									For TEST_array=1 To optotal
										If op$(TEST_array)=CALC_getchar$ Then
											TEST_found=True
											Exit
										End If
									Next
									
									If TEST_found=True Then
										;reset values
										CALC_start=False
										CALC_x=CALC_x+1
										CALC_maketok$=""
										CALC_readtype1$=""									
									Else
										Eval_Error ers$(4):Return
									End If
								End If
							End If
						End If
					End If
				End If
				
			;---[ INFO ]--------------
			;The all important condition.
			;This will compare the two values and effect them with the
			;VAL1 and the OP that was got in MODE1
			
			ElseIf CALC_mode=2
				If CALC_start=False Then
					If CALC_getchar$=" " Then
						CALC_x=CALC_x+1
					Else
						;Value 3=string
						If CALC_getchar$=SPEC_quote$ Then
							;set
							CALC_readtype2$="string"
							CALC_x=CALC_x+1
							CALC_start=True
							CALC_maketok$=""
						Else
							CALC_readtype2$="value"
							CALC_start=True
							CALC_maketok$=""
						End If
					End If
				Else
					;Parse until end
					If CALC_readtype2$="string" Then
						If CALC_getchar$=SPEC_quote$ Then
							;----------------------
							;VALUE found finish off
							;----------------------
							;End quote found so do sum and update
							MAKE_left$=Left$(sum$,CALC_marker-1)
							MAKE_right$=Right$(sum$,Len(sum$)-CALC_x)
							MAKE_sum$=CALC_val1$+CALC_maketok$
							sum$=MAKE_left$+SPEC_quote$+MAKE_sum$+SPEC_quote$+MAKE_right$
							;reset
							CALC_x=1
							CALC_mode=1
							CALC_start=False
							CALC_type$=""
							CALC_val1$=""
							CALC_val2$=""
							CALC_op$=""
							CALC_readtype1$=""
							CALC_readtype2$=""
							CALC_maketok$=""
							CALC_marker=1
						Else
							If CALC_x=Len(sum$) Then
								CALC_x=1
								CALC_start=False
							End If
							CALC_maketok$=CALC_maketok$+CALC_getchar$
							CALC_x=CALC_x+1
						End If
					Else
						;Look to make value negative ?
						If CALC_getchar$="-" And Len(CALC_maketok$)=0 Then
							CALC_maketok$=CALC_maketok$+CALC_getchar$
							If CALC_x=Len(sum$) Then
								Eval_Error ers$(2):Return
							Else
								CALC_x=CALC_x+1
							End If
						Else
							;Is character a reserved character
							TEST_found=False
							For TEST_array=1 To restotal
								If res$(TEST_array)=CALC_getchar$ Then
									TEST_found=True
									If TEST_Array=11 Then
										CALC_readtype2$="float"
									End If
									Exit
								End If
							Next
							;Character matches reserved character
							If TEST_found=True Then
								If CALC_x=Len(sum$) Then
									CALC_maketok$=CALC_maketok$+CALC_getchar$
									;----------------------
									;VALUE found finish off
									;----------------------
									MAKE_left$=Left$(sum$,CALC_marker-1)
									MAKE_right$=Right$(sum$,Len(sum$)-CALC_x)
									CALC_val2$=CALC_maketok$
									
									If CALC_readtype1$="string" Then
										MAKE_sum$=CALC_val1$+CALC_val2$
										sum$=MAKE_left$+SPEC_quote$+MAKE_sum$+SPEC_quote$+MAKE_right$
									Else
										If CALC_readtype1$="value" Then
											Select CALC_op$
												Case op$(1);POW
													MAKE_sum$=(Int(CALC_val1$)^Int(CALC_val2$))
												Case op$(2);MUL
													MAKE_sum$=(Int(CALC_val1$)*Int(CALC_val2$))
												Case op$(3);DIV
													MAKE_sum$=(Int(CALC_val1$)/Int(CALC_val2$))
												Case op$(4);ADD
													MAKE_sum$=(Int(CALC_val1$)+Int(CALC_val2$))
												Case op$(5);SUB
													MAKE_sum$=(Int(CALC_val1$)-Int(CALC_val2$))
												Case op$(6);OR
													MAKE_sum$=(Int(CALC_val1$) Or Int(CALC_val2$))
												Case op$(7);AND
													MAKE_sum$=(Int(CALC_val1$) And Int(CALC_val2$))
												Case op$(8);XOR
													MAKE_sum$=(Int(CALC_val1$) Xor Int(CALC_val2$))
												Case op$(9);MOD
													MAKE_sum$=(Int(CALC_val1$) Mod Int(CALC_val2$))
												Case op$(10);EQU
													MAKE_sum$=(Int(CALC_val1$) = Int(CALC_val2$))
												Case op$(11);LES
													MAKE_sum$=(Int(CALC_val1$) < Int(CALC_val2$))
												Case op$(12);MOR
													MAKE_sum$=(Int(CALC_val1$) > Int(CALC_val2$))
												Case op$(13);ELS
													MAKE_sum$=(Int(CALC_val1$) =< Int(CALC_val2$))
												Case op$(14);EMR
													MAKE_sum$=(Int(CALC_val1$) => Int(CALC_val2$))
												Case op$(15);NOT
													MAKE_sum$=(Int(CALC_val1$) <> Int(CALC_val2$))

											End Select
										Else
											Select CALC_op$
												Case op$(1);POW
													MAKE_sum$=(Float#(CALC_val1$)^Float#(CALC_val2$))
												Case op$(2);MUL
													MAKE_sum$=(Float#(CALC_val1$)*Float#(CALC_val2$))
												Case op$(3);DIV
													MAKE_sum$=(Float#(CALC_val1$)/Float#(CALC_val2$))
												Case op$(4);ADD
													MAKE_sum$=(Float#(CALC_val1$)+Float#(CALC_val2$))
												Case op$(5);SUB
													MAKE_sum$=(Float#(CALC_val1$)-Float#(CALC_val2$))
												Case op$(6);OR
													MAKE_sum$=(Float#(CALC_val1$) Or Float#(CALC_val2$))
												Case op$(7);AND
													MAKE_sum$=(Float#(CALC_val1$) And Float#(CALC_val2$))
												Case op$(8);XOR
													MAKE_sum$=(Float#(CALC_val1$) Xor Float#(CALC_val2$))
												Case op$(9);MOD
													MAKE_sum$=(Float#(CALC_val1$) Mod Float#(CALC_val2$))
												Case op$(10);EQU
													MAKE_sum$=(Float#(CALC_val1$) = Float#(CALC_val2$))
												Case op$(11);LES
													MAKE_sum$=(Float#(CALC_val1$) < Float#(CALC_val2$))
												Case op$(12);MOR
													MAKE_sum$=(Float#(CALC_val1$) > Float#(CALC_val2$))
												Case op$(13);ELS
													MAKE_sum$=(Float#(CALC_val1$) =< Float#(CALC_val2$))
												Case op$(14);EMR
													MAKE_sum$=(Float#(CALC_val1$) => Float#(CALC_val2$))
												Case op$(15);NOT
													MAKE_sum$=(Float#(CALC_val1$) <> Float#(CALC_val2$))
											End Select
										End If
										;NUMBEr OUT OF RANGE
										If MAKE_sum$="Infinity" Then
											Eval_Error ers$(8):Return
										Else
											sum$=MAKE_left$+MAKE_sum$+MAKE_right$
										End If
									End If
									;RESET VALUES
									CALC_x=1
									CALC_mode=1
									CALC_start=False
									CALC_type$=""
									CALC_val1$=""
									CALC_val2$=""
									CALC_op$=""
									CALC_readtype1$=""
									CALC_readtype2$=""
									CALC_maketok$=""
									CALC_marker=1
									;///////////FINISH END/////////////
									
								Else
									CALC_maketok$=CALC_maketok$+CALC_getchar$
									CALC_x=CALC_x+1
								End If
							Else
								;is not reserved character
								If CALC_x>Len(sum$) Then
									Eval_Error ers$(2):Return
								Else
									;Check it is a valid character
									TEST_found=False
									For TEST_array=1 To optotal
										If op$(TEST_array)=CALC_getchar$ Then
											TEST_found=True
											Exit
										End If
									Next
									If TEST_found=True Then
										;----------------------
										;VALUE found finish off
										;----------------------
										MAKE_left$=Left$(sum$,CALC_marker-1)
										MAKE_right$=Right$(sum$,Len(sum$)-CALC_x+1)
										CALC_val2$=CALC_maketok$
										
										If CALC_readtype1$="string" Then
											MAKE_sum$=CALC_val1$+CALC_val2$
											sum$=MAKE_left$+SPEC_quote$+MAKE_sum$+SPEC_quote$+MAKE_right$
										Else
											If CALC_readtype1$="value" Then
												Select CALC_op$
													Case op$(1);POW
														MAKE_sum$=(Int(CALC_val1$)^Int(CALC_val2$))
													Case op$(2);MUL
														MAKE_sum$=(Int(CALC_val1$)*Int(CALC_val2$))
													Case op$(3);DIV
														MAKE_sum$=(Int(CALC_val1$)/Int(CALC_val2$))
													Case op$(4);ADD
														MAKE_sum$=(Int(CALC_val1$)+Int(CALC_val2$))
													Case op$(5);SUB
														MAKE_sum$=(Int(CALC_val1$)-Int(CALC_val2$))
													Case op$(6);OR
														MAKE_sum$=(Int(CALC_val1$) Or Int(CALC_val2$))
													Case op$(7);AND
														MAKE_sum$=(Int(CALC_val1$) And Int(CALC_val2$))
													Case op$(8);XOR
														MAKE_sum$=(Int(CALC_val1$) Xor Int(CALC_val2$))
													Case op$(9);MOD
														MAKE_sum$=(Int(CALC_val1$) Mod Int(CALC_val2$))
													Case op$(10);EQU
														MAKE_sum$=(Int(CALC_val1$) = Int(CALC_val2$))
													Case op$(11);LES
														MAKE_sum$=(Int(CALC_val1$) < Int(CALC_val2$))
													Case op$(12);MOR
														MAKE_sum$=(Int(CALC_val1$) > Int(CALC_val2$))
													Case op$(13);ELS
														MAKE_sum$=(Int(CALC_val1$) =< Int(CALC_val2$))
													Case op$(14);EMR
														MAKE_sum$=(Int(CALC_val1$) => Int(CALC_val2$))
													Case op$(15);NOT
														MAKE_sum$=(Int(CALC_val1$) <> Int(CALC_val2$))
												End Select
											Else
												Select CALC_op$
													Case op$(1);POW
														MAKE_sum$=(Float#(CALC_val1$)^Float#(CALC_val2$))
													Case op$(2);MUL
														MAKE_sum$=(Float#(CALC_val1$)*Float#(CALC_val2$))
													Case op$(3);DIV
														MAKE_sum$=(Float#(CALC_val1$)/Float#(CALC_val2$))
													Case op$(4);ADD
														MAKE_sum$=(Float#(CALC_val1$)+Float#(CALC_val2$))
													Case op$(5);SUB
														MAKE_sum$=(Float#(CALC_val1$)-Float#(CALC_val2$))
													Case op$(6);OR
														MAKE_sum$=(Float#(CALC_val1$) Or Float#(CALC_val2$))
													Case op$(7);AND
														MAKE_sum$=(Float#(CALC_val1$) And Float#(CALC_val2$))
													Case op$(8);XOR
														MAKE_sum$=(Float#(CALC_val1$) Xor Float#(CALC_val2$))
													Case op$(9);MOD
														MAKE_sum$=(Float#(CALC_val1$) Mod Float#(CALC_val2$))
													Case op$(10);EQU
														MAKE_sum$=(Float#(CALC_val1$) = Float#(CALC_val2$))
													Case op$(11);LES
														MAKE_sum$=(Float#(CALC_val1$) < Float#(CALC_val2$))
													Case op$(12);MOR
														MAKE_sum$=(Float#(CALC_val1$) > Float#(CALC_val2$))
													Case op$(13);ELS
														MAKE_sum$=(Float#(CALC_val1$) =< Float#(CALC_val2$))
													Case op$(14);EMR
														MAKE_sum$=(Float#(CALC_val1$) => Float#(CALC_val2$))
													Case op$(15);NOT
														MAKE_sum$=(Float#(CALC_val1$) <> Float#(CALC_val2$))
												End Select
											End If
											;NUMBEr OUT OF RANGE
											If MAKE_sum$="Infinity" Then
												Eval_Error ers$(8):Return
											Else
												sum$=MAKE_left$+MAKE_sum$+MAKE_right$
											End If
										End If
										;RESET VALUES
										CALC_x=1
										CALC_mode=1
										CALC_start=False
										CALC_type$=""
										CALC_val1$=""
										CALC_val2$=""
										CALC_op$=""
										CALC_readtype1$=""
										CALC_readtype2$=""
										CALC_maketok$=""
										CALC_marker=1
										;///////////FINISH END/////////////
									Else
										Eval_Error ers$(4):Return
									End If
								End If
									
							End If
						End If
					End If
					
				End If
			End If
		
		Forever
	Next
	If Left$(sum$,1)=SPEC_quote$ Then
		Return Mid$(sum,2,Len(sum$)-2)
	Else
		Return sum$
	End If
End Function





Function Parseargument$(ss$,sp,seplist$=" ")
If ss$="" Then Return
stt=1:For tt=1 To Len(ss$);trim spaces off start!
mm$=Mid$(ss$,tt,1)
If mm$<>" " Then stt=tt:Exit
Next
For tt=stt To Len(ss$)	;find start of word
If sp-1=Spaces Then ws=tt:Exit
mm$=Mid$(ss$,tt,1)
If Instr(seplist$,mm$) Then Spaces=Spaces+1
Next
If ws=0 Then Return ;bad scr_argument!  out of range and stuff
If ws=Len(ss$) Then Return Right$(ss$,1)
wrd$=""
For tt = ws To Len(ss$)
k$=Mid$(ss$,tt,1)
If Instr(seplist,k$)=0 Then wrd$=wrd$+k$ Else Return wrd$
If tt=Len(ss$) Then Return wrd$
Next
End Function

Function Parsearguments(S$,sep$=" ")
argz=0:newword=1
For t=1 To Len(s$)
m$=Mid(s$,t,1)
If newword=1 Then argz=argz+1:newword=0
	For sp=1 To Len(sep$)
	o$=Mid(sep,sp,1)
	If o$=m$ Then newword=1:Exit
	Next
Next
Return argz
End Function


Function parseArgumentSeperator$(ss$,sp,seplist$=" ")
If ss$="" Then Return
stt=1:For tt=1 To Len(ss$);trim spaces off start!
mm$=Mid$(ss$,tt,1)
If mm$<>" " Then stt=tt:Exit
Next
For tt=stt To Len(ss$)	;find start of word
If sp-1=Spaces Then ws=tt:Exit
mm$=Mid$(ss$,tt,1):lastsep$=mm
If Instr(seplist$,mm$) Then Spaces=Spaces+1
Next
If ws=0 Then Return ;bad scr_argument!  out of range and stuff
If ws=Len(ss$) Then outo$=lastsep$:Goto retty;Right$(ss$,1)
wrd$=""
For tt = ws To Len(ss$)
k$=Mid$(ss$,tt,1):lastsep$=k
If Instr(seplist,k$)=0 Then wrd$=wrd$+k$ Else outo$=k$:Goto retty
If tt=Len(ss$) Then outo$=lastsep$:Goto retty
Next
Return
.retty
If Instr(seplist,outo) Then Return outo
End Function










Function FilterSum$(Sum$)
If Len(sum)=1 Then Return filtersegment(sum)
sum2$="":lastpoint=1
Repeat:t=t+1
c$=Mid(sum,t,1)

If c=Chr(34) Then
speech=speech+1
	If speech=1 Then
		seg$=Mid(sum,lastpoint,t-lastpoint) ;outside a string
		seg=FilterSegment(seg)
		sum2=sum2+""+seg+""
		lastpoint=t+1
	Else
		seg$=Mid(sum,lastpoint,t-lastpoint) ;inside a string
		sum2=sum2+""+Chr(34)+seg+Chr(34)+""
		speech=0
		lastpoint=t+1
	EndIf
c=0
EndIf
Until t=Len(sum)
		If lastpoint<t Then
			seg$=Mid(sum,lastpoint,t-lastpoint+1) ;outside a string
			seg=FilterSegment(seg)
			sum2=sum2+""+seg+""
			lastpoint=t+1
		EndIf
Return sum2
End Function


Function FilterSegment$(seg$)
seg=Lower(seg)
seg=Replace(seg,"=<","{")
seg=Replace(seg,"<=","{")
seg=Replace(seg,"=>","}")
seg=Replace(seg,">=","}")
seg=Replace(seg,"><","@")
seg=Replace(seg,"<>","@")
args=ParseArguments(seg,parseseps+" ")
For a=1 To args
arg$=ParseArgument(seg,a,ParseSeps+" ")
sep$=ParseArgumentSeperator(seg,a,ParseSeps+" ")
If arg="and" Then arg="&"
If arg="or" Then arg="|"
If arg="mod" Then arg="%"
If arg="xor" Then arg="~"
	If Len(arg)>1 Then
	rt$=Right(arg,1)
	If rt="#" Then ctyp=1:arg=Left(arg,Len(arg)-1)
	If rt="$" Then ctyp=2:arg=Left(arg,Len(arg)-1)
	EndIf
				If Len(arg)>2 Then ;FUNCTIONS
				If Right(arg,1)=")" Then
				Fnc$=parseargument(arg,1,"(")
				If Right(fnc,1)="%" Then fnc=Left(fnc,Len(fnc)-1)
				If Right(fnc,1)="$" Then fnc=Left(fnc,Len(fnc)-1)
				If Right(fnc,1)="#" Then fnc=Left(fnc,Len(fnc)-1)
				FncArgs$=Mid(arg,Len(fnc)+2,Len(arg)-Len(fnc)-2)
				If Fnc="sin" Then arg=Sin(calculate(fncargs))
				If Fnc="cos" Then arg=Cos(calculate(fncargs))
				If Fnc="tan" Then arg=Tan(calculate(fncargs))
				If Fnc="atan" Then arg=ATan(calculate(fncargs))
				If Fnc="rnd" Then arg=Rnd( calculate(parseargument(fncargs,1,",)")) , calculate(parseargument(fncargs,2,",)")) )
				If Fnc="rand" Then arg=Rand( calculate(parseargument(fncargs,1,",)")) , calculate(parseargument(fncargs,2,",)")) )
				EndIf
				EndIf
	If vtyp=0 Then
	For t=1 To vars    ;VARIABLES
	If varname(t)=arg Then arg=varvalue(t):Goto gotvar
	Next
	EndIf
		If vtyp=0 Or vtyp=1 Then
		For t=1 To stringvars
		If stringvarname(t)=arg Then arg=Chr(34)+stringvarvalue(t)+Chr(34):Goto gotvar
		Next
		EndIf
			If vtyp=0 Or vtyp=1 Then
			For t=1 To floatvars
			If floatvarname(t)=arg Then arg=floatvarvalue(t):Goto gotvar
			Next
			EndIf
	.gotvar
segout$=segout+arg+sep
Next
segout=Replace(segout," ","")
	If Len(segout)>0 Then
	L$=Right(segout,1)
	For t=1 To optotal
	If l=op(t) Then segout=Left(segout,Len(segout)-1):Exit
	Next
	EndIf 
Return segout
End Function





Function Eval_Error(message$)
	;temp error message
	CalcError=Message
	;RuntimeError message$ + "  AT:line " + GLOBAL_line	
End Function
