; ID: 3040
; Author: deus_duex
; Date: 2013-03-16 01:49:08
; Title: Metamorphic Code Example
; Description: Useful example for creating metamorphic code. (Code that alters itself during runtime.)

Graphics 1200,1080,0,2															;LET'S HAVE A NICE, BIG WINDOW FOR PLEEEENNNNTTTYYY OF OUTPUT, SHALL WE?

Global Kernel32PeekPokeBank=CreateBank(4) 										;BANK FOR MEMORY ACCESS (LONG NAME TO AVOID NAMING CONFLICTS...)

Global output% = 0																;USED TO SHOW HOW FUNCTIONS ARE BEING CALLED FROM THE C CODE, NOT BB
Global f																		;NEED THIS TO CALL BB FUNCTIONS FROM C (SYMBOLWRAP VARIABLE)
Local prog$,b,randFunc,randFuncPtr												;SOME LOCALS THAT WE WILL USE SHORTLY/LATER ON RESPECTIVELY
Local s = TCC_New()																;LETS SET UP A TCC BUILD ENVIRONMENT!
prog=prog+"int fptest ( int(*f)(int val) ) { return f(1); } "					;THE ACTUAL BB CALLING FUNCTION FROM C
prog=prog+"int fptest_w ( void * f ) { f = *((int*)(f)); return fptest(f); }"	;THIS IS JUST A WRAPPER TO GET THE ARGUMENT OUT OF THE PASSED BANK
TCC_SetOutputType(s, 0)															;OUTPUT THE C CODE TO MEMORY (COMPILING IN MEMORY)
TCC_CompileString(s, prog)														;COMPILE IT ALL!
TCC_Relocate(s)																	;CALL THIS TO LOAD THE MACHINE CODE INTO EXECUTABLE MEMORY!
f=TCC_GetSymbolWrap(s, "fptest_w")												;GET THE WRAPPER FUNCTION

startPointer = FunctionPointer()												;HERE WE ESTABLISH A STARTING POINT FOR OUR PROGRAM THROUGH THIS POINTER
Goto startskip
dummyFunction(0)
.startskip
Print "StartPointer : "+startPointer											;LET'S SEE WHAT WE ARE BEGINNING WITH...

Type memType																	;CLASS FOR HOLDING FUNCTIONS FROM MEMORY AND THEIR SIZES
	Field ptr%
	Field hexPtr$, hexPtr_1$, hexPtr_2$, hexPtr_3$, hexPtr_4$
	Field hexPtr_Rev%, hexPtr_Rev_1%, hexPtr_Rev_2%, hexPtr_Rev_3%, hexPtr_Rev_4%
	Field size%
	Field mem
End Type
Dim mem.memType(10)																;WE HAVE 9 DEMO FUNCTIONS IN TOTAL SO LETS ALLOCATE 10 CLASSES TO BE SAFE

For i=0 To 10																	;LOOP 'EM THROUGH!
	mem(i) = New memType														;CREATE THE CLASS IN MEMORY
	Select i																	;'i' DICTATES WHICH FUNCTION WE ARE GETTING THE POINTER OF
		Case 0:																	;HERE AND HEREAFTER, WE GET THE POINTER OF THE FUNCTION WE SKIP.
			mem(i)\ptr= FunctionPointer()
			Goto skip
			test0(0)
		Case 1:
			mem(i)\ptr = FunctionPointer()
			Goto skip
			test1(0)
		Case 2:
			mem(i)\ptr = FunctionPointer()
			Goto skip
			test2(0)
		Case 3:
			mem(i)\ptr = FunctionPointer()
			Goto skip
			test3(0)
		Case 4:
			mem(i)\ptr = FunctionPointer()
			Goto skip
			test4(0)
		Case 5:
			mem(i)\ptr = FunctionPointer()
			Goto skip
			test5(0)
		Case 6:
			mem(i)\ptr = FunctionPointer()
			Goto skip
			test6(0)
		Case 7:
			mem(i)\ptr = FunctionPointer()
			Goto skip
			test7(0)
		Case 8:
			mem(i)\ptr = FunctionPointer()
			Goto skip
			test8(0)
		Case 9:
			mem(i)\ptr = FunctionPointer()
			Goto skip
			test9(0)
		Case 10:																;THIS IS USED TO DETERMINE THE SIZE OF THE LAST FUNCTION ('test9')
			endPointer = FunctionPointer()
			Goto skip
			dummyFunction2(0)
	End Select
	.skip																		;WE END UP HERE AFTER EACH SKIP FROM ABOVE
	If i>0 And i<=9 Then														;IF WE AREN'T AT THE (FINAL FUNCTION)+1, [10], THEN...
		mem(i-1)\size = mem(i)\ptr - mem(i-1)\ptr								;THE SIZE OF A FUNCTION IN MEMORY IS DICTATED BY THE POINTER
																				;	LOCATED DIRECTLY AFTER IT MINUS THE POINTER TO THE FUNCTION
		mem(i-1)\mem = CreateBank(mem(i-1)\size)								;CREATE A BANK LARGE ENOUGH TO HOLD THE FUNCTION
		RtlMoveMemory2%(mem(i-1)\mem,mem(i-1)\ptr,mem(i-1)\size)				;COPY THE FUNCTION FROM MEMORY INTO THE BANK (IRONY HERE! :P)
		
		fp = WriteFile((i-1)+".txt")											;FOR DEBUGGING PURPOSES, LET'S OUPUT THE RAW MACHINE CODE OF THE
		For j=0 To mem(i-1)\size												;	FUNCTION INTO A TEXT FILE CORRESPONDING TO THE FUNCTION'S NUMBER
			WriteLine fp,Right$(Hex$(PeekB(mem(i-1)\ptr+j)),2)					;	THIS WILL HELP US SEE HOW THE MACHINE CODE BEHAVES IF WE PLUG
		Next																	;	IT INTO A DISSASSEMBLER (HTTP://ONLINEDISASSEMBLER.COM/ODAWEB/RUN_HEX)
		CloseFile fp															;	FOR EXAMPLE..
		
		Print "["+(i-1)+"] Function Size: "+mem(i-1)\size						;LETS PRINT WHAT THE FUNCTION SIZE IS AS WELL (ALSO DEBUGGING PURPOSES)
	Else If i=10 Then															;IF WE ARE ON THE (AMOUNT_OF_FUNCTIONS+1) THEN WE NEED TO USE 'endPointer'
																				;TO DETERMINE THE SIZE OF THE LAST FUNCTION ('test9')
		mem(i-1)\size  = endPointer - mem(i-1)\ptr								;ITS SIZE IS DETERMINED THE SAME WAY.. (FUNCTION_PTR_PROCEDING - FUNCTION_PTR)
		mem(i-1)\mem = CreateBank(mem(i-1)\size)								;CREATE A BANK LIKE WE DID FOR ALL THE OTHER FUNCTIONS...
		RtlMoveMemory2%(mem(i-1)\mem,mem(i-1)\ptr,mem(i-1)\size)				;STORE THE FUNCTION INTO THE BANK (STILL IRONIC!)
		
		fp = WriteFile((i-1)+".txt")											;DEBUGGING LIKE ABOVE...
		For j=0 To mem(i-1)\size
			WriteLine fp,Right$(Hex$(PeekB(mem(i-1)\ptr+j)),2)
		Next
		CloseFile fp
		
		Print "["+(i-1)+"] Function Size: "+mem(i-1)\size						;YOU GET THE DRIFT....
	End If
	Print "["+(i)+"] " + mem(i)\ptr												;WE ALSO PRINT THE FUNCTION'S POINTER FOR DEBUGGING PURPOSES
Next

Print ""
Print "================================="
Print ""

For i=0 To mem(9)\size-1								; LOOP THROUGH THE WHOLE OF THE FUNCTION'S ASM (MACHINE) CODE IN MEMORY.
	bt = PeekByte(mem(9)\mem,i)							; GET THE FUNCTION'S RAW ASM CODE...
	If bt = $e8 Then 									; $E8 = ASM FOR CALL
		times=times+1									; KEEP TRACK OF HOW MANY TIMES A CALL TO ANOTHER FUNCTION IS MADE.
		If times = 2 Then 								; PATCH THE SECOND CALL ($E8 x2)
			Print "FOUND AT "+(mem(9)\ptr)+" + "+i		; DEBUGGING PURPOSES
			n = (startPointer - (mem(9)\ptr + i)) + 5 	; ([WHERE WE WANT TO GO] - [WHERE THE ADDRESS IS NOW]) + [5 BYTES AFTERWARDS TO RETURN TO]
			PokeB(mem(9)\ptr+i+0,Int($E8)) 				; CALL (NOT REALLY NECESSARY TO RE-WRITE, BUT OH WELL! :P)
			PokeI(mem(9)\ptr+i+1,n) 					; POKE THE OFFSET TO CALL TO...
		End If
		;Print Hex$(PeekByte(mem(9)\mem,i+4)) + " " + Hex$(PeekByte(mem(9)\mem,i+3)) + " " + Hex$(PeekByte(mem(9)\mem,i+2)) + " " + Hex$((PeekByte(mem(9)\mem,i+1)-i-5))
	Else If bt = $c2 Then
		Exit
	End If
Next
Print "Found "+times+" CALLS..."

fp = WriteFile("9-PATCHED.txt")
For i=0 To mem(9)\size
	WriteLine fp,Right$(Hex$(PeekB(mem(9)\ptr+i)),2)
Next
CloseFile fp


;==========================================================================================================================
;==========================================================================================================================
; EXAMPLE OF CALLING A RANDOM FUNCTION (MAKES THINGS HARD FOR A DISSASEMBLER TO FOLLOW. PROGRAM CALLS CHANGE EVERY RUNTIME.
;==========================================================================================================================
;==========================================================================================================================
b=CreateBank(4)
SeedRnd MilliSecs()
randFunc = Rand(0,9)
randFuncPtr = mem(randFunc)\ptr
PokeInt b,0,randFuncPtr

Print "Function to Call        : "+randFunc
Print "Random Function Pointer : "+randFuncPtr
Print "Output% (before)        : "+output%
Print "Calling Faunction       : "+TCC_CallCFunc(f,b)  ;call the C function which in turn calls the passed function pointer... (round-about way of doing things...)
Print "Output% (after)         : "+output%
FreeBank b

Print ""
Print "================================="
Print ""
;==========================================================================================================================
;==========================================================================================================================
;==========================================================================================================================
;==========================================================================================================================

;**************************************************************************************************************************

;==========================================================================================================================
;==========================================================================================================================
; EXAMPLE OF CALLING A FUNCTION THAT HAS ITS INTERNAL MEMORY (MACHINE CODE) CHANGED/PATCHED TO REDIRECT TO ANOTHER
;    ONE OF OUR FUNCTIONS. THIS IS A BAREBONES OR **VERY** BASIC EXAMPLE OF METAMORPHIC CODE.
;==========================================================================================================================
;==========================================================================================================================
output%=0
b=CreateBank(4)	
PokeInt b,0,mem(9)\ptr
Print "Function to Call        : mem(9)!"
Print "Random Function Pointer : "+mem(9)\ptr
Print "Output% (before)        : "+output%
Print "Calling Faunction       : "+TCC_CallCFunc(f,b)
Print "Output% (after)         : "+output%
FreeBank b

Print ""
Print "================================="
Print ""
;==========================================================================================================================
;==========================================================================================================================
;==========================================================================================================================
;==========================================================================================================================

;**************************************************************************************************************************

;==========================================================================================================================
;==========================================================================================================================
; EXAMPLE OF MOVING FUNCTIONS AROUND IN MEMORY (PRESUMED SAME ALLOCATED SIZE FOR BOTH FUNCTIONS...) 
;
; DISSASSEMBLERS WILL NOT BE ABLE TO TELL WHAT FUNCTION IS ACTUALLY BEING CALLED IN THIS CASE EVEN IF THEY KNOW THE
;    DIFFERENT FUNCTION ADDRESSES... NOTE: THE FUNCTION'S ACTUAL CODE REMAINS UNCHANGED THOUGH SO DISAS'S MIGHT SEE IT'S 
;	 THE SAME CODE, JUST MOVED FROM ONE PLACE TO ANOTHER...
;
; FIRST, WE CALL THE UNTAMPERED FUNCTION AT STARTPOINTER. SECOND, WE CALL STARTPOINTER AFTER IT HAS BEEN FILLED WITH
;	 A DIFFERENT FUNCTION'S CODE (MACHINE CODE, THAT IS!) WHICH IN TURN EXECUTES THE NEW FUNCTION INSTEAD OF THAT WHICH
;	 WE HAD FIRST CALLED.
;
; @@@@@@@@@@@@
; @@@ NOTE @@@ -> THIS EXAMPLE FAILS WHEN EXTRA LINES OF CODE ARE ADDED INTO THIS PROGRAM'S SOURCE AND THEN COMPILED!!
; @@@@@@@@@@@@    SINCE THIS IS JUST A BASIC EXAMPLE, I WILL NOT GO INTO DEPTH ANY FURTHER.
;==========================================================================================================================
;==========================================================================================================================
output%=0
b=CreateBank(4)	
PokeInt b,0,startPointer
Print "Function to Call        : startPointer!"
Print "Function Pointer        : "+startPointer
Print "Output% (before)        : "+output%
Print "Calling Faunction       : "+TCC_CallCFunc(f,b) 	
Print "Output% (after)         : "+output%
FreeBank b

Print ""
Print "================================="
Print ""


Print (mem(0)\ptr-startPointer) + " VS " + mem(6)\size					;DEBUGGING PURPOSES! :)

If mem(6)\size <= (mem(0)\ptr-startPointer) Then						;LET'S GET STARTED. MAKE SURE THAT THE FUNCTION WE WANT TO MOVE WILL FIT INTO THE
																		;	SPACE AT STARTPOINTER
	oldProtection% = 0													;WE WILL NEED THIS LATER...
	Api_VirtualProtect(startPointer, mem(6)\size, 64, oldProtection)	;WE HAVE TO TELL THE KERNEL THAT WE WANT ACCESS TO THE MEMORY AT STARTPOINTER
	
	bnk = CreateBank(mem(6)\size)										;CREATE A BANK TO HOLD THE FUNCTION WE WANT TO MOVE TO STARTPOINTER
	RtlMoveMemory2%(bnk,mem(6)\ptr,mem(6)\size)							;MOVE THE FUNCTION AT THE GIVEN POINTER INTO THE NEWLY CREATED FUNCTION BANK
	
	For i=0 To BankSize(bnk)-1											;UNFORTUNATELY, WE HAVE TO PATCH EVERY 'CALL' COMMAND TO CORRECT THE OFFSETS IN
																		;	THE MACHINE CODE. (CALL'S REFER TO AN OFFSET FROM THE CURRENT MEMORY POSITION
																		;	TO THE FUNCTION THAT WE WANT TO GO TO)
		If PeekByte(bnk,i) = Int($E8) Then								;IF WE COME ACROSS A 'CALL' COMMAND IN THE MACHINE CODE...
			dp = PeekInt(bnk,i+1)										;GET IT'S ORIGINAL OFFSET (UNTAMPERED)
			;Print "OLD: "+dp
			n  = (dp - (startPointer - mem(6)\ptr))						;SET A NEW OFFSET RELATIVE TO THE NEW POSITION IN MEMORY (STARTPOINTER)
			;Print "NEW: "+n
			PokeInt(bnk,i+1,n)											;CORRECT THE OFFSET IN THE BANK THAT WILL BE UPDATED INTO STARTPOINTER.
		Else If PeekByte(bnk,i) = Int($C2) Then							;IF WE ENCOUNTER A 'RET' COMMAND...
			Exit														;EXIT MOTHA****AH!!!! :P
		End If															;YOU KNOW WHAT THIS DOES....
	Next																;THIS TOO, KEEP READING...
	
	Api_RtlMoveToMemory(startPointer,bnk,BankSize(bnk))					;MOVE THE PATCHED FUNCTION FROM THE BANK INTO THE GIVEN FUNCTION POINTER'S POSITION
	FreeBank bnk														;FREE THE BANK USED TO HOLD THE FUNCTION THAT WE PATCHED. WE WON'T NEED IT ANYMORE
	
	For i=(mem(6)\size+1) To (mem(0)\ptr-startPointer) 					;FOR THE REST OF THE BYTES LEFT OVER (SINCE WE HAVE EXTRA SPACE, POSSIBLY)
		PokeB(startPointer+i,$00)										;FILL IT ALL WITH $00'S (0'S) WHICH MEAN ABSOLUTELY NOTHING IN MACHINE CODE :)
	Next																;INCREMENTAL...
	
	Api_VirtualProtect(startPointer, mem(6)\size, oldprotection, 0)		;RESTORE THE READ/WRITE/EXECUTE PERMISSIONS ON THE MEMORY AT STARTPOINTER
End If																	;NOW WE'RE DONE. GRAB A COLD ONE, YOU DESERVE IT! :)

output%=0
b=CreateBank(4)	
PokeInt b,0,startPointer
Print "Function to Call        : startPointer (FILLED WITH ANOTHER FUNCTION)!"
Print "Function Pointer        : "+startPointer
Print "Output% (before)        : "+output%
Print "Calling Faunction       : "+TCC_CallCFunc(f,b) 							;call the C function which in turn calls the passed function pointer... (round-about way of doing things...)
Print "Output% (after)         : "+output%
FreeBank b
;==========================================================================================================================
;==========================================================================================================================
;==========================================================================================================================
;==========================================================================================================================

;**************************************************************************************************************************

;==========================================================================================================================
;==========================================================================================================================
; THE REST OF THIS PROGRAM IS PRETTY SELF-EXPLANATORY... (DELETE TCC PROGRAM FROM MEMORY, PRINT A WAIT MESSAGE THEN
; 	WAIT FOR A KEY TO BE PRESSED FOR PROGRAM TERMINATION THEREAFTER.
;==========================================================================================================================
;==========================================================================================================================
TCC_Delete s	;Free the C code

Print ""
Print "================================="
Print "WAITKEY()"
WaitKey()
End
;==========================================================================================================================
;==========================================================================================================================
;==========================================================================================================================
;==========================================================================================================================

;**************************************************************************************************************************

;==========================================================================================================================
;==========================================================================================================================
; THE FOLLLOWING FUNCTIONS ARE USED FOR THE EXAMPLES ABOVE. DO NOT MODIFY THEM UNLESS YOU REALLY HAVE A GRASP ON HOW THE
; ABOVE ROUTINES AND EXAMPLES WORK. MODIFICATION WITHOUT THIS KNOWLEDGE WILL LEAD TO MANY SLEEPLESS NIGHTS WITH COUNTLESS
; 'MEMORY ACCESS VIOLATION'S OR 'ILLEGAL INSTRUCTION'S OR, MY PERSONAL FAVORITE, 'UNKNOWN RUNTIME ERROR'S! SO PLEASE, JUST
; DO YOUR RESEARCH! :)
;==========================================================================================================================
;==========================================================================================================================
Function dummyFunction(argv%)
	VWait Rand(0,100)
	VWait Rand(0,100)
	VWait Rand(0,100)
	VWait Rand(0,100)
	Color 255,255,0
	Print "HELO! THIS IS SOME FILLER CODE"
	Delay 100
	Color 0,255,0
	Color 255,255,255
End Function

Function test0(argv%)		;Test function: returns a value and has a side-effect
	output% = argv%
	Color 255,0,0
	Print "FUNCTION0               : "+argv%
	;Print "FUNCTION                : "+PeekInt%(argv%,0)
	;b=CreateBank(4)
	;PokeInt b,0,PeekInt%(argv%,0)
	;TCC_CallCFunc(f,b) 
	;FreeBank b
	Color 255,255,255
End Function

Function test1(argv%)
	Local s = 1241512515
	Color 255,0,0
	Print "FUNCTION1               : "+argv%
	Print "FUNCTION1               : "+s
	Color 255,255,255
End Function

Function test2(argv%)
	Color 255,0,0
	Print "FUNCTION2               : "+argv%
	Print "FUNCTION2               : "+argv%
	Print "FUNCTION2               : "+argv%
	Color 255,255,255
End Function

Function test3(argv%)
	Color 255,0,0
	Print "FUNCTION3               : "+argv%
	Color 0,0,255
	Rect 0,0,100,200,1
	Color 255,255,255
End Function

Function test4(argv%)
	Color 255,0,0
	Print "FUNCTION4               : "+argv%
	Print "FUNCTION4               : "+CurrentDir$()
	Color 255,255,255
End Function

Function test5(argv%)
	Color 255,0,0
	Print "FUNCTION5               : "+argv%
	Print "FUNCTION5               : "+CurrentDate$()
	Color 255,255,255
End Function

Function test6(argv%)
	Color 255,0,0
	Print "FUNCTION6               : "+argv%
	Print "FUNCTION6               : "+CurrentTime$()
	Color 255,255,255
End Function

Function test7(argv%)
	Color 255,0,0
	Print "FUNCTION7               : "+argv%
	Print "FUNCTION7               : "+MilliSecs()
	Color 255,255,255
End Function

Function test8(argv%)
	Color 255,0,0
	Print "FUNCTION8               : "+argv%
	Print "FUNCTION8               : HABEUS CORPUS!"
	Color 255,255,255
End Function

Function test9(argv%)
	test1(0)
	;test5(125) ; PATCHED WITH test(1) EARLIER IN THE PROGRAM THROUGH ASM MODDING!
	Print "HELP!" ; ANY FUNCTION WITH A SINGLE PARAMETER CAN BE PATCHED HERE
	;Color 255,0,0
	;Print "FUNCTION9               : "+argv%
	;Color 0,255,0
	;Print "FUNCTION9               : TWO CAN TANGO!"
	;Color 255,255,255
End Function

Function allFunctions() ;This function is used to get the function pointers for all our functions from memory in the ASM code...
	test0(0)
	test1(0)
	test2(0)
	test3(0)
	test4(0)
	test5(0)
	test6(0)
	test7(0)
	test8(0)
	test9(0)
End Function

Function dummyFunction2(argv%)
	Delay Rand(0,200)
End Function
;==========================================================================================================================
;==========================================================================================================================
;==========================================================================================================================
;==========================================================================================================================

;**************************************************************************************************************************

;==========================================================================================================================
;==========================================================================================================================
; THE FOLLOWING FUNCTION WAS TO BE USED TO GRAB ALL THE CALLS OUT OF A FUNCTION'S MACHINE CODE IN MEMORY AND REPORT THEM
; BACK TO THE MAIN PROGRAM. WOULD HAVE BEEN NEAT TO MAKE A FULL DISASSEMBLER THIS WAY... (FUTURE PROJECT ANYONE? :P)
;==========================================================================================================================
;==========================================================================================================================
Function getFunctionPointers(stPointer = 0)
	fp=WriteFile("AllFunctions.txt")
	Local i = 0, bt = 0, minus = -5
	If stPointer=0 Then Return
	While bt <> Int($C2) ;$C2 = RET
		bt = PeekB(stPointer+i)
		WriteLine fp,Right$(Hex$(bt),2)
		If bt = Int($E8) Then ;$E8 = CALL
			;WriteLine fp, "($E8) CALL TO : "+Right$(Hex$(PeekB(stPointer+i+4)),2) + " " + Right$(Hex$(PeekB(stPointer+i+3)),2) + " " + Right$(Hex$(PeekB(stPointer+i+2)),2) + " " + Right$(Hex$((PeekB(stPointer+i+1)+i+5)),2)
			;Print "IS       : "+Right$(Hex$(PeekB(stPointer+i+1)),2) + " " + Right$(Hex$(PeekB(stPointer+i+2)),2) + " " + Right$(Hex$(PeekB(stPointer+i+3)),2) + " " + Right$(Hex$(PeekB(stPointer+i+4)),2)
			;Print "SHOULD BE: "+Hex$(PeekB(stPointer+i+4)) + " " + Hex$(PeekB(stPointer+i+3)) + " " + Hex$(PeekB(stPointer+i+2)) + " " + Hex$(PeekB(stPointer+i+1))
			Print "CALL TO : "+Right$(Hex$(PeekB(stPointer+i+4)),2) + " " + Right$(Hex$(PeekB(stPointer+i+3)),2) + " " + Right$(Hex$(PeekB(stPointer+i+2)),2) + " " + Right$(Hex$((PeekB(stPointer+i+1)+i+5)),2)
			If (cnt <= 10 And cnt>1) Then
				mem(cnt-1)\hexPtr$ = Right$(Hex$(PeekB(stPointer+i+4)),2) + Right$(Hex$(PeekB(stPointer+i+3)),2) + Right$(Hex$(PeekB(stPointer+i+2)),2) + Right$(Hex$((PeekB(stPointer+i+1)+i+5)),2)
				mem(cnt-1)\hexPtr_1$ = Right$(Hex$(PeekB(stPointer+i+4)),2)
				mem(cnt-1)\hexPtr_2$ = Right$(Hex$(PeekB(stPointer+i+3)),2)
				mem(cnt-1)\hexPtr_3$ = Right$(Hex$(PeekB(stPointer+i+2)),2)
				mem(cnt-1)\hexPtr_4$ = Right$(Hex$((PeekB(stPointer+i+1)+i+5)),2)
				
				add% = stPointer + PeekI(stPointer+i+1) - i - 5
				mem(cnt-1)\hexPtr_Rev% = add% ;Right$(Hex$((PeekB(stPointer+i+1)+i+5)),2) + Right$(Hex$(PeekB(stPointer+i+2)),2) + Right$(Hex$(PeekB(stPointer+i+3)),2) + Right$(Hex$(PeekB(stPointer+i+4)),2)
				mem(cnt-1)\hexPtr_Rev_1% = PeekB(stPointer+i+1)+i;-5
				mem(cnt-1)\hexPtr_Rev_2% = PeekB(stPointer+i+2)
				mem(cnt-1)\hexPtr_Rev_3% = PeekB(stPointer+i+3)
				mem(cnt-1)\hexPtr_Rev_4% = PeekB(stPointer+i+4)
			End If
			cnt=cnt+1
		End If
		i=i+1
	Wend
	Print cnt+" CALL's FOUND!!"
	CloseFile fp
	Return
End Function
;==========================================================================================================================
;==========================================================================================================================
;==========================================================================================================================
;==========================================================================================================================

;**************************************************************************************************************************

;==========================================================================================================================
;==========================================================================================================================
; THE FOLLOWING ARE BORROWED FUNCTIONS FROM THE BLITZBASIC ARCHIVES. THESE ARE NOT MY CREATION; YOU (AND I) SHOULD PAY
; A HUGE THANKS TO THOSE AT THE BLITZBASIC ARCHIVES FOR COMING UP WITH THIS CODE. THANK YOU :)
;==========================================================================================================================
;==========================================================================================================================
Function SaveBank(bank,file$ = "Bank.txt")
	fp = WriteFile(file$)
	WriteBytes(bank,fp,0,BankSize(bank))
	CloseFile fp
End Function

Function PeekB(address) ;PeekByte
	Api_RtlMoveToBank(Kernel32PeekPokeBank,address,1)
	Return PeekByte(Kernel32PeekPokeBank,0)
End Function

Function PeekS(address) ;PeekShort
	Api_RtlMoveToBank(Kernel32PeekPokeBank,address,2)
	Return PeekShort(Kernel32PeekPokeBank,0)
End Function

Function PeekI(address) ;PeekInteger
	Api_RtlMoveToBank(Kernel32PeekPokeBank,address,4)
	Return PeekInt(Kernel32PeekPokeBank,0)
End Function

Function PeekF#(address) ;PeekFloat
	Api_RtlMoveToBank(Kernel32PeekPokeBank,address,4)
	Return PeekFloat(Kernel32PeekPokeBank,0)
End Function

Function PokeB(address,value) ;PokeByte
	PokeByte Kernel32PeekPokeBank,0,value
	Api_RtlMoveToMemory(address,Kernel32PeekPokeBank,1)
End Function

Function PokeS(address,value) ;PokeShort
	PokeShort Kernel32PeekPokeBank,0,value
	Api_RtlMoveToMemory(address,Kernel32PeekPokeBank,2)
End Function

Function PokeI(address,value) ;PokeInteger
	PokeInt Kernel32PeekPokeBank,0,value
	Api_RtlMoveToMemory(address,Kernel32PeekPokeBank,4)
End Function

Function PokeF(address,value#) ;PokeFloat
	PokeFloat Kernel32PeekPokeBank,0,value#
	Api_RtlMoveToMemory(address,Kernel32PeekPokeBank,4)
End Function
;==========================================================================================================================
;==========================================================================================================================
;==========================================================================================================================
;==========================================================================================================================

;**************************************************************************************************************************
;END*END*END*END*END*END*END*END*END*END*END*END*END*END*END*END*END*END*END*END*END*END*END*END*END*END*END*END*END*END*EN
