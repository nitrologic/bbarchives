; ID: 2972
; Author: Yasha
; Date: 2012-08-20 16:10:27
; Title: FAST bank to string
; Description: Super fast conversion between bank and string, 20x faster than native BB

; The decls:

; Quickly copy data from string to bank and bank to string using system functions
; Both of these functions are more usefully wrapped, to hide the dirty details

.lib "Kernel32.dll"

API_StringToBank_(Dest*, Src$, Sz%) : "RtlMoveMemory"

.lib "msvcrt.dll"

; Dest and Src should ideally be the same bank (dirty hack)
API_BankToString_$(Dest*, Src*, Sz%) : "memmove"

; The wrappers:
;
;Function StringToBank_FAST(s$)
;	Local b = CreateBank(Len(s))
;	API_StringToBank_ b, s, BankSize(b)
;	Return b
;End Function
;
;Function BankToString_FAST$(b)    ;The extra code around the API function here is important!
;	Local hasExtra = False, sz = BankSize(b)
;	If sz = 0
;		ResizeBank b, 1 : PokeByte b, 0, 0 : hasExtra = True
;	ElseIf PeekByte(b, sz - 1)
;		ResizeBank(b), sz + 1 : PokeByte b, sz, 0 : hasExtra = True
;	EndIf
;	Local res$ = API_BankToString_(b, b, 0)    ; 0 should technically be fine here
;	If hasExtra Then ResizeBank(b), sz
;	Return res
;End Function
;
