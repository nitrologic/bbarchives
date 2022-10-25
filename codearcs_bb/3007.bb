; ID: 3007
; Author: Yasha
; Date: 2012-11-30 12:32:34
; Title: Code position identity
; Description: Generate a unique ID for each PLACE the function is called

; /* CodePosition.decls:

.lib "CodePosition.dll"

; Return the current code position (a unique ID for each call point)
JIT_ReturnAddress%() : "JIT_ReturnAddress@0"
 
;Return the current code's caller's position, plus respective levels
JIT_ReturnAddress1%() : "JIT_ReturnAddress1@0"
JIT_ReturnAddress2%() : "JIT_ReturnAddress2@0"
JIT_ReturnAddress3%() : "JIT_ReturnAddress3@0"

; */


// Requires GCC extensions
#ifndef __GNUC__
#  error GCC with builtins required for this to work
#endif
void * __stdcall JIT_ReturnAddress(void) {
	return __builtin_return_address(0);
}

void * __stdcall JIT_ReturnAddress1(void) {
	return __builtin_return_address(1);
}

void * __stdcall JIT_ReturnAddress2(void) {
	return __builtin_return_address(2);
}

void * __stdcall JIT_ReturnAddress3(void) {
	return __builtin_return_address(3);
}
