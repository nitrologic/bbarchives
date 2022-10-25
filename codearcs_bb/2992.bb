; ID: 2992
; Author: Yasha
; Date: 2012-10-26 15:38:26
; Title: Lambda syntax (closures)
; Description: Anonymous/inline functions, closures, and nested functions for Blitz3D/+

; Lambda library for Blitz3D/+
;==============================

; Requires FastPointer DLL (free): http://www.fastlibs.com/index.php


;Memory management (optional, but extremely useful)
Include "AutoRelease.bb"	;Get this file at http://www.blitzbasic.com/codearcs/codearcs.php?code=2978

;Basis library (useful predefined constructor functions)
Include "Lambda-Basis.bb"	;Get this file at http://www.blitzbasic.com/codearcs/codearcs.php?code=2992


Type Closure
	Field rc.RefCounted		;Remove this if not using AutoRelease
	Field argn$[9], env.Closure
	Field expr.Thunk, argv
End Type

Type Thunk
	Field argn$[9], isVarName	;isVarName = bit array; upper bits are "isThunk", bit 30 is "isBound"
	Field val[9], argv			;val = args passed in (inc. thunks, vars), argv = evaluated args passed to fptr
	Field fptr, argc, env.Closure	;Function pointer, arg count (neg for isLazy), calling lambda
End Type


Const LAMBDA_APPLY_CONSTPTR = 1
Global LAMBDA_private_CIF_.Closure


;Create an inline anonymous function and return it to external Blitz code
Function Func(a0$, a1$, a2$ = "", a3$ = "", a4$ = "", a5$ = "", a6$ = "", a7$ = "", a8$ = "", doAR = True)
	Local argc = 2 + (a2 <> "") + (a3 <> "") + (a4 <> "") + (a5 <> "") + (a6 <> "") + (a7 <> "") + (a8 <> "")
	Local f.Closure = MakeClosureInner(doAR)
	f\argn[0] = a0 : f\argn[1] = a1 : f\argn[2] = a2 : f\argn[3] = a3 : f\argn[4] = a4
	f\argn[5] = a5 : f\argn[6] = a6 : f\argn[7] = a7 : f\argn[8] = a8
	
	If Asc(f\argn[argc - 1]) <> 64 Then RuntimeError "Expecting thunk as body expression of lambda function"
	f\expr = Object.Thunk Int Mid(f\argn[argc - 1], 2)
	If LAMBDA_private_CIF_ <> Null Then PurgeThunkTreeFromCIF f\expr, LAMBDA_private_CIF_\expr
	
	Return Handle f
End Function

;Create an inline anonymous function and return it to closure code
Function Lambda$(a0$, a1$, a2$ = "", a3$ = "", a4$ = "", a5$ = "", a6$ = "", a7$ = "", a8$ = "")
	Return MakeThunk(0, Func(a0, a1, a2, a3, a4, a5, a6, a7, a8, False))
End Function

;Invoke a closure with arguments
Function CallFunc(L, a0 = 0, a1 = 0, a2 = 0, a3 = 0, a4 = 0, a5 = 0, a6 = 0, a7 = 0, a8 = 0)
	Local F.Closure = Object.Closure L 
	If Not F\argv Then F\argv = CreateBank(9 * 4)
	Local tmp[8], i, val : For i = 0 To 8
		tmp[i] = PeekInt(F\argv, i * 4)
	Next
	PokeInt F\argv,  0, a0 : PokeInt F\argv,  4, a1 : PokeInt F\argv,  8, a2
	PokeInt F\argv, 12, a3 : PokeInt F\argv, 16, a4 : PokeInt F\argv, 20, a5
	PokeInt F\argv, 24, a6 : PokeInt F\argv, 28, a7 : PokeInt F\argv, 32, a8
	val = EvalThunk(F\expr, F)
	For i = 0 To 8
		PokeInt F\argv, i * 4, tmp[i]
	Next
	Return val
End Function

;Invoke a closure with arguments and return it to closure code
Function Apply$(L$, a0$ = "", a1$ = "", a2$ = "", a3$ = "", a4$ = "", a5$ = "", a6$ = "", a7$ = "")
	Return MakeThunk(1, L, a0, a1, a2, a3, a4, a5, a6, a7, 9)
End Function

;Define a new nested inner function
Function InnerFunc(a0$, a1$ = "", a2$ = "", a3$ = "", a4$ = "", a5$ = "", a6$ = "", a7$ = "", a8$ = "")
	Local argc = 2 + (a2 <> "") + (a3 <> "") + (a4 <> "") + (a5 <> "") + (a6 <> "") + (a7 <> "") + (a8 <> "")
	Local f.Closure = MakeClosureInner(True)
	f\argn[0] = a0 : f\argn[1] = a1 : f\argn[2] = a2 : f\argn[3] = a3 : f\argn[4] = a4
	f\argn[5] = a5 : f\argn[6] = a6 : f\argn[7] = a7 : f\argn[8] = a8
	
	f\expr = Object.Thunk Int Mid(MakeThunk(0, ""), 2)	;Call before setting CIF!
	f\expr\argv = CreateBank(0) : f\expr\argc = 0
	f\env = LAMBDA_private_CIF_ : LAMBDA_private_CIF_ = f
	
	Return Handle f
End Function

;End a nested inner function definition
Function EndFunc()
	LAMBDA_private_CIF_ = LAMBDA_private_CIF_\env
End Function

;Manually free closure objects
Function FreeFunc(L.Closure)
	If L\expr <> Null Then FreeThunk L\expr
	If L\argv Then FreeBank L\argv
	Delete L
End Function

;(Internal) Internal constructor for closure objects: uses AutoRelease by default
Function MakeClosureInner.Closure(doAR)
	Local L.Closure = New Closure
	If doAR
		Local freePtr = FunctionPointer() : Goto skip : FreeFunc L	;Comment these lines if not using AutoRelease
		.skip
		L\rc = NewRefCounted(freePtr, TypePointer(L))
		AutoRelease L\rc
	EndIf
	Return L
End Function

;(Internal) Copy closure objects (for use with Lambda)
Function CopyFuncInner.Closure(L.Closure, doAR)
	Local C.Closure = MakeClosureInner(doAR), i
	For i = 0 To 8
		C\argn[i] = L\argn[i]
	Next
	If L\expr <> Null Then C\expr = CopyThunkInner(L\expr)
	If L\argv Then C\argv = CreateBank(BankSize(L\argv)) : CopyBank L\argv, 0, C\argv, 0, BankSize(L\argv)
	C\env = L\env
	Return C
End Function

;(Internal) Attach an environment to a function to form a complete closure
Function BindFuncEnvironment(L.Closure, env.Closure)
	If env <> Null
		L\argv = CreateBank(BankSize(env\argv) + 9 * 4)
		CopyBank env\argv, 0, L\argv, 9 * 4, BankSize(env\argv)
	Else
		L\argv = CreateBank(9 * 4)
	EndIf
	L\env = env
End Function

;(Internal) Get the Func out of a lambda-thunk for manual processing
Function GetLambdaFunc.Closure(lam$)
	Local L.Thunk = Object.Thunk Int Mid(lam, 2)
	Return Object.Closure L\val[0]
End Function

;(Internal) Package a delayed action
Function MakeThunk$(fptr, a0$, a1$ = "", a2$ = "", a3$ = "", a4$ = "", a5$ = "", a6$ = "", a7$ = "", a8$ = "", maxArgC = 0)
	Local t.Thunk = New Thunk, i
	If maxArgC
		t\argc = maxArgC
	Else	;Count them
		t\argc = 1 + (a1 <> "") + (a2 <> "") + (a3 <> "") + (a4 <> "") + (a5 <> "") + (a6 <> "") + (a7 <> "") + (a8 <> "")
	EndIf
	t\fptr = fptr
	t\argn[0] = a0
	If fptr
		t\argn[1] = a1 : t\argn[2] = a2 : t\argn[3] = a3 : t\argn[4] = a4
		t\argn[5] = a5 : t\argn[6] = a6 : t\argn[7] = a7 : t\argn[8] = a8
		t\argv = CreateBank((Abs t\argc) * 4 + 4)
	EndIf
	For i = 0 To (Abs t\argc) - 1
		If Asc(t\argn[i]) = 64 Then t\val[i] = Int Mid(t\argn[i], 2) : Else t\val[i] = Int t\argn[i]
	Next
	If LAMBDA_private_CIF_ <> Null	;Add statement to InnerFunc, but remove child expressions from it
		For i = 0 To (Abs t\argc) - 1
			If Asc(t\argn[i]) = 64 Then RemoveThunkFromCIF Object.Thunk t\val[i], LAMBDA_private_CIF_\expr
		Next
		LAMBDA_private_CIF_\expr\argc = LAMBDA_private_CIF_\expr\argc + 1
		ResizeBank LAMBDA_private_CIF_\expr\argv, LAMBDA_private_CIF_\expr\argc * 4
		PokeInt LAMBDA_private_CIF_\expr\argv, (LAMBDA_private_CIF_\expr\argc - 1) * 4, Handle t
	EndIf
	Return "@" + Handle t
End Function

;(Internal) Copy a Thunk (for use with Lambda)
Function CopyThunkInner.Thunk(t.Thunk)
	Local i, C.Thunk = New Thunk
	C\fptr = t\fptr : C\argc = t\argc : C\isVarName = t\isVarName
	If t\argv Then C\argv = CreateBank(BankSize(t\argv)) : CopyBank t\argv, 0, C\argv, 0, BankSize(t\argv)
	If t\fptr
		For i = 0 To (Abs t\argc - 1)
			If Asc(t\argn[i]) = 64
				C\val[i] = Handle CopyThunkInner(Object.Thunk t\val[i]) : C\argn[i] = "@" + C\val[i]
			Else
				C\val[i] = t\val[i] : C\argn[i] = t\argn[i]
			EndIf
		Next
	Else
		If t\argv	;An InnerFunc
			For i = 0 To t\argc - 1
				PokeInt C\argv, i * 4, Handle CopyThunkInner(Object.Thunk PeekInt(t\argv, i * 4))
			Next
		Else		;A lambda thunk
			C\val[0] = Handle CopyFuncInner(Object.Closure t\val[0], False)
		EndIf
	EndIf
	Return C
End Function

;(Internal) Free a delayed action
Function FreeThunk(t.Thunk)
	Local i
	If t\fptr
		For i = 0 To (Abs t\argc) - 1
			If Asc(t\argn[i]) = 64 Then FreeThunk Object.Thunk t\val[i]
		Next
		FreeBank t\argv
	Else
		If t\argv	;An InnerFunc
			For i = 0 To t\argc - 1
				FreeThunk Object.Thunk PeekInt(t\argv, i * 4)
			Next
			FreeBank t\argv
		Else		;A lambda thunk
			FreeFunc Object.Closure t\val[0]
		EndIf
	EndIf
	Delete t
End Function

;(Internal) Evaluate a delayed action
Function EvalThunk(t.Thunk, env.Closure)
	Local i, val
	
	If t\fptr	;It's an action thunk
		If Not t\isVarName
			For i = 0 To (Abs t\argc) - 1
				If Asc(t\argn[i]) = 64
					t\isVarName = t\isVarName Or (1 Shl (i + 9))
				Else
					Local idx = GetThunkArgBinding(t\argn[i], env)
					If idx
						t\val[i] = idx - 1 : t\isVarName = t\isVarName Or (1 Shl i)
					Else
						PokeInt t\argv, i * 4, t\val[i]		;Simple value, poke it here (so we only do it once)
					EndIf
				EndIf
			Next
			t\isVarName = t\isVarName Or (1 Shl 30)	;Set a very high bit to nonzero
		EndIf
		
		If t\argc < 0
			t\env = env : PokeInt t\argv, (Abs t\argc) * 4, Handle t	;Lazy thunks need an eval environment
		Else
			Local tmp[9]
			For i = 0 To t\argc - 1
				If t\isVarName And (1 Shl (i + 9))
					tmp[i] = EvalThunk(Object.Thunk t\val[i], env)
				EndIf
			Next
			For i = 0 To t\argc - 1
				If t\isVarName And (1 Shl i)
					PokeInt t\argv, i * 4, PeekInt(env\argv, t\val[i])
				ElseIf t\isVarName And (1 Shl (i + 9))
					PokeInt t\argv, i * 4, tmp[i]
				EndIf
			Next
		EndIf
		
		If t\fptr = LAMBDA_APPLY_CONSTPTR	;Apply (change this to check against Apply's actual fptr sometime)
			Local F = PeekInt(t\argv, 0), a0 = PeekInt(t\argv, 4), a1 = PeekInt(t\argv, 8), a2 = PeekInt(t\argv, 12), a3 = PeekInt(t\argv, 16)
			Return CallFunc(F, a0, a1, a2, a3, PeekInt(t\argv, 20), PeekInt(t\argv, 24), PeekInt(t\argv, 28), PeekInt(t\argv, 32))
		Else
			CallFunctionVarInt t\fptr, t\argv
			Return PeekInt(t\argv, (Abs t\argc) * 4)
		EndIf
		
	Else
		If t\argv	;It's an InnerFunc
			For i = 0 To t\argc - 1		;If we're here, t\argc should never be negative
				val = EvalThunk(Object.Thunk PeekInt(t\argv, i * 4), env)
			Next
			Return val
			
		Else		;It's a thunk of lambda: return copy of value to CallFunc
			Local L.Closure = CopyFuncInner(Object.Closure t\val[0], True)
			If Not L\argv Then BindFuncEnvironment L, env
			Return Handle L
		EndIf
	EndIf
End Function

;(Internal) Find the offset of a name in the var table of the environment
Function GetThunkArgBinding(arg$, env.Closure)
	If arg = "" Then Return 0
	Local i, count : While env <> Null
		For i = 0 To 8
			If env\argn[i] = arg Then Return count + 1
			count = count + 4
		Next
		env = env\env
	Wend
	Return 0
End Function

;(Internal) Evaluate a packaged action lazily
Function GetLazyThunkArgValue(t.Thunk, a)
	If t\isVarName And (1 Shl a)
		Return PeekInt(t\env\argv, t\val[a])
	ElseIf t\isVarName And (1 Shl (a + 9))
		Return EvalThunk(Object.Thunk t\val[a], t\env)
	Else
		Return PeekInt(t\argv, a * 4)
	EndIf
End Function

;(Internal) Remove thunks from the current inner function if they shouldn't be there
Function PurgeThunkTreeFromCIF(t.Thunk, cif.Thunk)
	Local i
	RemoveThunkFromCIF t, cif
	If t\fptr
		For i = 0 To (Abs t\argc) - 1
			If Asc(t\argn[i]) = 64 Then PurgeThunkTreeFromCIF Object.Thunk t\val[i], cif
		Next
	Else
		Local f.Closure = Object.Closure t\val[0]
		PurgeThunkTreeFromCIF f\expr, cif
	EndIf
End Function

;(Internal) Remove one thunk (see above)
Function RemoveThunkFromCIF(t.Thunk, cif.Thunk)
	Local ht = Handle t, i
	For i = 0 To BankSize(cif\argv) - 4 Step 4
		If PeekInt(cif\argv, i) = ht
			CopyBank cif\argv, i + 4, cif\argv, i, BankSize(cif\argv) - (i + 4)
			ResizeBank cif\argv, BankSize(cif\argv) - 4
			cif\argc = cif\argc - 1
			Return
		EndIf
	Next
End Function


;~IDEal Editor Parameters:
;~F#E#14#20#2E#33#44#49#57#5C#63#6F#7B#86#8C#A9#C2#D7#115#122#12D
;~F#13B
;~C#Blitz3D
