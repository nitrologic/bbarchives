; ID: 3103
; Author: GW
; Date: 2014-02-01 15:25:16
; Title: Simple Expression Compiler
; Description: Compile basic expressions to x86 assembly

'
'
'
'
'
---------------------------------
-=[ Here is an example input: ]=-
---------------------------------
var x;
var myvar;
var anothervar;
x=0.5*2;
if(x>1) then
	myvar=1+2-3*4/5;
	if(myvar==1.1) then
		anothervar=x*(-myvar+123.456)*-0.1;
	endif
endif

-------------------------------
-=[ and here is the output ]=-
-------------------------------

;------Begin------;
_func:
	push ebp
	mov ebp,esp
	sub esp, 12
	fld dword [_0]
	fld dword [_1]
	fmulp 
	fstp dword [ebp-4]		;store in x
	fld dword [ebp-4]			;load 'x'
	fld dword [_2]
	fxch
	fucompp
	fnstsw ax
	sahf
	setbe al
	movzx eax,al
	cmp eax,0
	jne _endif1:
	fld dword [_2]
	fld dword [_1]
	faddp 
	fld dword [_3]
	fld dword [_4]
	fmulp 
	fld dword [_5]
	fdivp 
	fsubp 
	fstp dword [ebp-8]		;store in myvar
	fld dword [ebp-8]			;load 'myvar'
	fld dword [_6]
	fxch
	fucompp
	fnstsw ax
	sahf
	setnz al
	movzx eax,al
	cmp eax,0
	jne _endif2:
	fld dword [ebp-4]			;load 'x'
	fld dword [ebp-8]			;load 'myvar'
	fld dword [_7]
	faddp 
	fchs
	fmulp 
	fld dword [_8]
	fmulp 
	fstp dword [ebp-12]		;store in anothervar
_endif2:
_endif1:

	mov esp,ebp
	pop ebp
	ret
;------End------;

section "data"
_0:
dd 0x3F000000		;-> 0.500000000
align 4
_1:
dd 0x40000000		;-> 2.00000000
align 4
_2:
dd 0x3F800000		;-> 1.00000000
align 4
_3:
dd 0x40400000		;-> 3.00000000
align 4
_4:
dd 0x40800000		;-> 4.00000000
align 4
_5:
dd 0x40A00000		;-> 5.00000000
align 4
_6:
dd 0x3F8CCCCD		;-> 1.10000002
align 4
_7:
dd 0x42F6E979		;-> 123.456001
align 4
_8:
dd 0xBDCCCCCD		;-> -0.100000001
align 4
