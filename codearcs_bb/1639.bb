; ID: 1639
; Author: MCP
; Date: 2006-03-11 05:45:46
; Title: Blitz3D Function Pointers (Hack)
; Description: Advanced info for C++ programmers!

*** The DLL part...

// a function pointer declaration for calling Blitz3D code
void (__stdcall *ABlitz3DFunction)(void);

// standard DLL function declaration for Blitz3D userlibs
MYDLLNAME_API void BBCALL SetBlitz3DFunction(void)
{
    UINT stackpos,adrs;
    UINT FAR *func;
    __asm
    {
        mov stackpos,esp
        mov esp,ebp
        add esp,4
        pop adrs
        mov esp,stackpos
     }
     func=(UINT FAR *)&ABlitz3DFunction;
    *func=adrs;
}

// extra DLL function for test purposes only - your DLL would call ABlitz3DFunction() directly!
MYDLLNAME_API void BBCALL CallBlitz3DFunction(void)
{
    ABlitz3DFunction();
}

*** The user libs part  (decls file) ....

.lib "mydllname.dll"

SetBlitz3DFunction():"_SetBlitz3DFunction@0"
CallBlitz3DFunction():"_CallBlitz3DFunction@0"

*** Blitz3D part....

;*** set function test

Global SETUP_FUNCS%=1
Gosub test_function
SETUP_FUNCS=0

CallBlitz3DFunction()     ;<-- testing DLL access only

WaitKey
End

.test_function:
                SetBlitz3DFunction()
If SETUP_FUNCS=0
	; *** function body goes here!!!
	Print "This works!!!!"
EndIf
	Return

*** that's it folks!  your DLL will call any code setup like this correctly and without problems even with the
*** blitz debugger running and you CAN place Stop commands and step through your Blitz3D function as normal.

Anyway, I hope this is of help to someone.
If anyone has a better way of doing this,  then please let me know.  Cheers :)
