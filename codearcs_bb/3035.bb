; ID: 3035
; Author: Yasha
; Date: 2013-03-02 18:53:26
; Title: Minimalist exceptions
; Description: Use setjmp/longjmp to make a mess of B3D code

;/* Exceptions.decls:

.lib "Exceptions.dll"

WithExceptionHandler%(proc%, handler%, arg%) : "WithExceptionHandler@12"
ThrowException(code%) : "ThrowException@4"

; */

#include <stdlib.h>
#include <setjmp.h>

#define BBCALL __attribute((stdcall))

static jmp_buf * topBuffer;
static int code;

BBCALL int WithExceptionHandler(int (* proc)(int) BBCALL,
                         int (* handler)(int) BBCALL,
                         int arg) {
	jmp_buf env;
	jmp_buf * prev;
	prev = topBuffer;
	topBuffer = &env;
	int res;
	if (!setjmp(env)) {
		res = proc(arg);
		topBuffer = prev;
	} else {
		topBuffer = prev;	// Can't return here on further problems
		res = handler(code);
	}
	return res;
}

BBCALL void ThrowException(int c) {
	code = c;
	longjmp(*topBuffer, 1);
}
