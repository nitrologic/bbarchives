; ID: 407
; Author: skn3[ac]
; Date: 2002-08-25 15:36:28
; Title: String/Int/Float evaluator for withing Blitz
; Description: Evalulate dynamic expression during runtime

;To use this function everything is required apart from
;the part in the ----test---- section.

;To call it all you have to do is calculate$("expression")

;The function returns a string, but can work with floats / ints and strings.
;I am sure it 100% replicates blitz's expression system without of course the 
;LOGIC gates.

;have fun :)


;---[ Operators ]------------------
Const OpTotal=5
Dim op$(OpTotal)
	op$(1)="^";POW
	op$(2)="*";MUL
	op$(3)="/";DIV
	op$(4)="+";ADD
	op$(5)="-";SUB
	
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

;---[  Error's  ]------------------
Dim ers$(8)
	ers$(1)="Unexpected ')'"
	ers$(2)="Expecting expression"
	ers$(3)="Expecting operator"
	ers$(4)="Ilegal character"
	ers$(5)="Missing ')'"
	ers$(6)="Missing " + SPEC_quote$ + " end quote"
	ers$(7)="Value types incorrect"
	ers$(8)="Result of expression out of range"
	
	
	
;---Test---
;---Test---
;---Test---
.restart
Print "Type an expression"
Print "(strings work aswell EG "+Chr$(34)+"hel"+Chr$(34)+"+"+Chr$(34)+"lo"+Chr$(34)+")
Print ""
ask$=Input$(">")
Write ask$ + " = "
Color 255,0,0

Print calculate$(ask$)
Color 255,255,255
Print ""
Goto restart
;-----------
;-----------
;-----------



Function error(message$)
	;temp error message
	RuntimeError message$ + "  AT:line " + GLOBAL_line	
End Function

Function calculate$(sum$)
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
				;--< ERROR >--"Unexpected ')'"
				If BRAK_found=0 Then
					error ers$(1)
				Else
					If BRAK_x=BRAK_marker+1 Then
						;--< ERROR >--"Expecting statement"
						error ers$(2)
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
						error ers$(5)
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
				;--< ERROR >--"Unexpected End to statement"
				If BRAK_x>=Len(sum$) Then
					error ers$(6)
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
									
									;Check needs expression error
									If CALC_x=Len(sum$) Then
										error ers$(2)
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
										error ers$(4)
									End If
								End If
							End If
						Else
							If CALC_x=Len(sum$) Then
								error ers$(6)
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
								error ers$(2)
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
											;--< ERROR >--Float type already set extra '.'
											error ers$(4)
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
										error ers$(4)
									Else
									
									
										If CALC_x=Len(sum$) Then
											error ers$(2)
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
										error ers$(4)
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
								error ers$(2)
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
											End Select
										End If
										;NUMBEr OUT OF RANGE
										If MAKE_sum$="Infinity" Then
											error ers$(8)
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
									error ers$(2)
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
												End Select
											End If
											;NUMBEr OUT OF RANGE
											If MAKE_sum$="Infinity" Then
												error ers$(8)
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
										error ers$(4)
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
