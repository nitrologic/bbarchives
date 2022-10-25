; ID: 1009
; Author: skidracer
; Date: 2004-04-29 23:46:30
; Title: acidstub
; Description: Builds external interface for a Blitz project using .dll callback mechanism

; acidstub.bb
; by simon@nitrologic.net

; builds external interface for a Blitz project using .dll callback mechanism
; creates both the bootstrap.bb and the acidstub.h header file For use by external project
; link to your own blitz functions by publishing a .decls file for your blitz routines

; inputs:	a valid blitzpath 
; outputs:	bootstrap.bb and acidstub.h file 

; build instructions
; 1. run acidstub
; 2. build mygame.dll from mygame.cpp 
; 3. run bootstrap.bb

; example dll project source
;
;	//acidstub.cpp
;
;	#define WIN32_LEAN_AND_MEAN
;	#include <windows.h>
;
;	DWORD WINAPI StubMain(void *);
;
;	#include "acidstub.h"
;
;	DWORD WINAPI StubMain(void *)
;	{
;		Graphics(640,480,0,2);
;		MouseWait();
;		End();
;		return 0;
;	}

Const dllname$="acidstub"
Const blitzpath$="c:\blitz\blitz3d\"

; dump keywords

blitzcc$=Chr$(34)+blitzpath+"bin\blitzcc"+Chr$(34)
f=WriteFile("stublist.bat")
WriteLine f,"set blitzpath="+blitzpath$
WriteLine f,blitzcc+" +k "+">"+"commands.txt"
CloseFile f
ExecFile "stublist"
Delay (500)

; process dump

Const NULLTYPE=0
Const INTTYPE=1
Const FLOATTYPE=2
Const STRINGTYPE=3

Type command
	Field	name$,help$
	Field	args
	Field	arg[10]
End Type

Function CommandInvoke$(c.command)
	Local	a$,b$,v$
	a$=""
	For i=1 To c\args
		Select c\arg[i]
			Case INTTYPE:v$="v"+Chr$(96+i):a$=a$+v$+"=argint():"
			Case FLOATTYPE:v$="f"+Chr$(96+i)+"#":a$=a$+v$+"=argfloat():"
			Case STRINGTYPE:v$="s"+Chr$(96+i)+"$":a$=a$+v$+"=argstring():"
		End Select
		b$=b$+v$
		If i<c\args b$=b$+","
	Next
	Select c\arg[0]
		Case NULLTYPE:a$=a$+c\name+" "+b$
		Case INTTYPE::a$=a$+"PokeInt(out,0,"+c\name+"("+b$+"))"
		Case FLOATTYPE:a$=a$+"PokeFloat(out,0,"+c\name+"("+b$+"))"
		Case STRINGTYPE:a$=a$+"PokeString(out,0,"+c\name+"("+b$+"))"
	End Select
	Return a$
End Function

Function CommandDef$(c.command,id)
	Local	a$,d$,v$,r$,dd$,vv$
	Select c\arg[0]
		Case NULLTYPE:a$="void "
		Case INTTYPE::a$="int ":r$="return *(int*)result;"
		Case FLOATTYPE:a$="float ":r$="return *(float*)result;"
		Case STRINGTYPE:a$="char *":r$="return (char*)result;"
	End Select
	vv$=id
	For i=1 To c\args
		Select c\arg[i]
			Case INTTYPE:v$="v"+Chr$(96+i):d$="int "+v$
			Case FLOATTYPE:v$="f"+Chr$(96+i):d$="float "+v$
			Case STRINGTYPE:v$="s"+Chr$(96+i):d$="char *"+v$	
		End Select
		vv$=vv$+","+v$
		dd$=dd$+d$
		If i<c\args dd$=dd$+","
	Next
	a$=a$+c\name+"("+dd$+") {invoke("+vv$+");"
	a$=a$+r$
	a$=a$+"}"
	Return a$
End Function

Function CreateCommand.command(d$)
	c.command=New command
	c\help=d$	
	p=Instr(d$,"(")
	If p>2
		q=p-1
		r$=Mid$(d$,p-2,1)
		t=INTTYPE
		If r$="#" t=FLOATTYPE:q=p-2
		If r$="$" t=STRINGTYPE:q=p-2
		c\arg[0]=t
		c\name=Mid$(d$,1,q-1)
		p=p+1
	Else
		p=Instr(d$," ")
		If p=0 c\name=d$:Return c		;c\arg[0]=INTTYPE:Return c
		c\name=Mid$(d$,1,p-1)
	EndIf

	p=Instr(d$," ")
	If (p) d$=Mid$(d$,p)
	d$=Replace$(d$,"(","")
	d$=Replace$(d$,")","")
	d$=Replace$(d$,"[","")
	d$=Replace$(d$,"]","")
	d$=Replace$(d$," ","")
	p=1
	If d$="" Return c

	While True
		q=Instr(d$,",",p)
		If q=0 q=Len(d$)+1
		If q=0 Return c
		r$=Mid$(d$,q-1,1)
		t=INTTYPE
		If r$="#" t=FLOATTYPE
		If r$="$" t=STRINGTYPE
		c\args=c\args+1
		c\arg[c\args]=t
		p=q+1
		If p>Len(d$) Or c\args=10 Return c
	Wend
End Function

f=ReadFile("commands.txt")
If f=0 Input("blitzcc command dump failure"):End

i=0
While Not Eof(f)
	cmd$=ReadLine(f)
	If i>58 c.command=CreateCommand(cmd$)
	i=i+1
Wend
CloseFile f

; create bootstrap.bb file

f=WriteFile("bootstrap.bb")
If f=0 Input("failed create bootstrap.bb file"):End
While True
	Read a$
	a$=Replace$(a$,"'",Chr$(34))
	If a$="" Exit
	WriteLine f,a$
Wend
i=0
For c.command=Each command
	a$="Case "+i+":"+CommandInvoke(c)
	WriteLine f,Chr$(9)+a$
	i=i+1
Next
WriteLine f,"End Select"
WriteLine f,"Wend"
CloseFile f

; create blitzstub.h file

f=WriteFile("acidstub.h")
If f=0 Input("failed to create acidstub.h file"):End
While True
	Read a$
	a$=Replace$(a$,"'",Chr$(34))
	If a$="" Exit
	WriteLine f,a$
Wend
i=0
For c.command=Each command
	a$=CommandDef(c,i)
	WriteLine f,a$
	i=i+1
Next
CloseFile f

Input "CreateStub Complete"
End


; bootstrap.bb core

Data "; bootstrap.bb"
Data "; launchpad for "+dllname$+".dll"
Data " "
Data "Function PokeString(bank,offset,a$)"
Data "	l=Len(a$)"
Data "	If (l>4000) l=4000"
Data "	PokeInt bank,offset,l"
Data "	For i=1 To l"
Data "		PokeByte bank,offset+3+i,Asc(Mid$(a$,i,1))"
Data "	Next"
Data "End Function"
Data " "
Data "Function argstring$()"
Data "	l=PeekInt(in,arg)"
Data "	arg=arg+4"
Data "	If (l>4000) l=4000"
Data "	For i=1 To l"
Data "		a$=a$+Chr$(PeekByte(in,arg))"
Data "		arg=arg+1"
Data "	Next"
Data "	Return a$"
Data "End Function"
Data " "
Data "Function argint()"
Data "	arg=arg+4"
Data "	Return PeekInt(in,arg-4)"
Data "End Function"
Data " "
Data "Function argfloat()"
Data "	arg=arg+4"
Data "	Return PeekFloat(in,arg-4)"
Data "End Function"
Data " "
Data "Global in=CreateBank(4096)"
Data "Global out=CreateBank(4096)"
Data "Global arg"
Data " "
Data "While True"
Data "	res=CallDLL('"+dllname$+"','call',in,out)"
Data "	arg=0"
Data "	Select res"
Data ""

; acidstub.h core

Data "// acidstub.h"
Data " "
Data "DWORD WINAPI StubMain(void *);"
Data "const void	*result;"
Data "char		*argptr;"
Data "int			stubcmd;"
Data "void arg(char *a);"
Data "void arg(int a) {*(int*)argptr=a;argptr+=4;}"
Data "void arg(float a) {*(float*)argptr=a;argptr+=4;}"
Data "void invoke(int cmd);"
Data "void invoke(int cmd,int i0) {arg(i0);invoke(cmd);}"
Data "void invoke(int cmd,int i0,int i1) {arg(i0);arg(i1);invoke(cmd);}"
Data "void invoke(int cmd,int i0,int i1,int i2) {arg(i0);arg(i1);arg(i2);invoke(cmd);}"
Data "void invoke(int cmd,int i0,int i1,int i2,int i3) {arg(i0);arg(i1);arg(i2);arg(i3);invoke(cmd);}"
Data "void invoke(int cmd,int i0,int i1,int i2,int i3,int i4) {arg(i0);arg(i1);arg(i2);arg(i3);arg(i4);invoke(cmd);}"
Data "void invoke(int cmd,int i0,int i1,int i2,int i3,int i4,int i5) {arg(i0);arg(i1);arg(i2);arg(i3);arg(i4);arg(i5);invoke(cmd);}"
Data "void invoke(int cmd,int i0,int i1,int i2,int i3,int i4,int i5,int i6) {arg(i0);arg(i1);arg(i2);arg(i3);arg(i4);arg(i5);arg(i6);invoke(cmd);}"
Data "void invoke(int cmd,int i0,int i1,int i2,int i3,int i4,int i5,int i6,int i7) {arg(i0);arg(i1);arg(i2);arg(i3);arg(i4);arg(i5);arg(i6);arg(i7);invoke(cmd);}"
Data "void invoke(int cmd,int i0,int i1,float i2,int i3,int i4,int i5) {arg(i0);arg(i1);arg(i2);arg(i3);arg(i4);arg(i5);invoke(cmd);}"
Data "void invoke(int cmd,int i0,int i1,float i2,float i3,int i4,int i5) {arg(i0);arg(i1);arg(i2);arg(i3);arg(i4);arg(i5);invoke(cmd);}"
Data "void invoke(int cmd,int i0,int i1,float i2,float i3,float i4) {arg(i0);arg(i1);arg(i2);arg(i3);arg(i4);invoke(cmd);}"
Data "void invoke(int cmd,int i0,int i1,float i2,int i3,float i4) {arg(i0);arg(i1);arg(i2);arg(i3);arg(i4);invoke(cmd);}"
Data "void invoke(int cmd,int i0,int i1,int i2,float i3,float i4,float i5) {arg(i0);arg(i1);arg(i2);arg(i3);arg(i4);arg(i5);invoke(cmd);}"
Data "void invoke(int cmd,int i0,int i1,float i2) {arg(i0);arg(i1);arg(i2);invoke(cmd);}"
Data "void invoke(int cmd,int i0,int i1,int i2,float i3,int i4) {arg(i0);arg(i1);arg(i2);arg(i3);arg(i4);invoke(cmd);}"
Data "void invoke(int cmd,int i0,int i1,float i2,float i3,float i4,int i5) {arg(i0);arg(i1);arg(i2);arg(i3);arg(i4);arg(i5);arg(i5);invoke(cmd);}"
Data "void invoke(int cmd,int i0,char *c1) {arg(i0);arg(c1);invoke(cmd);}"
Data "void invoke(int cmd,int i0,char *c1,int i2) {arg(i0);arg(c1);arg(i2);invoke(cmd);}"
Data "void invoke(int cmd,int i0,char *i1,int i2,int i3) {arg(i0);arg(i1);arg(i2);arg(i3);invoke(cmd);}"
Data "void invoke(int cmd,int i0,char *i1,int i2,int i3,int i4) {arg(i0);arg(i1);arg(i2);arg(i3);arg(i4);invoke(cmd);}"
Data "void invoke(int cmd,int i0,int i1,char *i2,int i3,int i4) {arg(i0);arg(i1);arg(i2);arg(i3);arg(i4);invoke(cmd);}"
Data "void invoke(int cmd,int i0,float i1) {arg(i0);arg(i1);invoke(cmd);}"
Data "void invoke(int cmd,int i0,float i1,float i2) {arg(i0);arg(i1);arg(i2);invoke(cmd);}"
Data "void invoke(int cmd,int i0,float i1,int i2) {arg(i0);arg(i1);arg(i2);invoke(cmd);}"
Data "void invoke(int cmd,int i0,float i1,float i2,float i3) {arg(i0);arg(i1);arg(i2);arg(i3);invoke(cmd);}"
Data "void invoke(int cmd,int i0,float i1,float i2,float i3,float i4) {arg(i0);arg(i1);arg(i2);arg(i3);arg(i4);invoke(cmd);}"
Data "void invoke(int cmd,int i0,float i1,float i2,float i3,float i4,float i5) {arg(i0);arg(i1);arg(i2);arg(i3);arg(i4);arg(i5);invoke(cmd);}"
Data "void invoke(int cmd,int i0,float i1,float i2,float i3,float i4,float i5,float i6) {arg(i0);arg(i1);arg(i2);arg(i3);arg(i4);arg(i5);arg(i6);invoke(cmd);}"
Data "void invoke(int cmd,int i0,float i1,float i2,int i3,int i4) {arg(i0);arg(i1);arg(i2);arg(i3);arg(i4);invoke(cmd);}"
Data "void invoke(int cmd,int i0,float i1,float i2,float i3,int i4,int i5) {arg(i0);arg(i1);arg(i2);arg(i3);arg(i4);arg(i5);invoke(cmd);}"
Data "void invoke(int cmd,int i0,float i1,float i2,int i3,int i4,int i5,int i6,int i7) {arg(i0);arg(i1);arg(i2);arg(i3);arg(i4);arg(i5);arg(i6);arg(i7);invoke(cmd);}"
Data "void invoke(int cmd,int i0,float i1,float i2,int i3,int i4,int i5,int i6) {arg(i0);arg(i1);arg(i2);arg(i3);arg(i4);arg(i5);arg(i6);invoke(cmd);}"
Data "void invoke(int cmd,int i0,float i1,float i2,float i3,float i4,float i5,int i6,int i7) {arg(i0);arg(i1);arg(i2);arg(i3);arg(i4);arg(i5);arg(i6);arg(i7);invoke(cmd);}"
Data "void invoke(int cmd,int i0,float i1,float i2,float i3,int i4,float i5) {arg(i0);arg(i1);arg(i2);arg(i3);arg(i4);arg(i5);invoke(cmd);}"
Data "void invoke(int cmd,int i0,float i1,float i2,float i3,int i4) {arg(i0);arg(i1);arg(i2);arg(i3);arg(i4);invoke(cmd);}"
Data "void invoke(int cmd,int i0,int i1,float i2,int i3,int i4,float i5) {arg(i0);arg(i1);arg(i2);arg(i3);arg(i4);arg(i5);invoke(cmd);}"
Data "void invoke(int cmd,int i0,int i1,float i2,float i3,float i4,float i5) {arg(i0);arg(i1);arg(i2);arg(i3);arg(i4);arg(i5);invoke(cmd);}"
Data "void invoke(int cmd,int i0,float i1,float i2,float i3,float i4,float i5,float i6,float i7) {arg(i0);arg(i1);arg(i2);arg(i3);arg(i4);arg(i5);arg(i6);arg(i7);invoke(cmd);}"
Data "void invoke(int cmd,int i0,float i1,float i2,float i3,float i4,float i5,float i6,int i7) {arg(i0);arg(i1);arg(i2);arg(i3);arg(i4);arg(i5);arg(i6);arg(i7);invoke(cmd);}"
Data "void invoke(int cmd,float i0) {arg(i0);invoke(cmd);}"
Data "void invoke(int cmd,float i0,float i1) {arg(i0);arg(i1);invoke(cmd);}"
Data "void invoke(int cmd,float i0,float i1,float i2) {arg(i0);arg(i1);arg(i2);invoke(cmd);}"
Data "void invoke(int cmd,float i0,float i1,float i2,int i3,int i4) {arg(i0);arg(i1);arg(i2);arg(i3);arg(i4);invoke(cmd);}"
Data "void invoke(int cmd,float i0,float i1,float i2,float i3,float i4,int i5,int i6) {arg(i0);arg(i1);arg(i2);arg(i3);arg(i4);arg(i5);arg(i6);invoke(cmd);}"
Data "void invoke(int cmd,float i0,float i1,float i2,float i3,float i4,float i5,float i6) {arg(i0);arg(i1);arg(i2);arg(i3);arg(i4);arg(i5);arg(i6);invoke(cmd);}"
Data "void invoke(int cmd,char *c0) {arg(c0);invoke(cmd);}"
Data "void invoke(int cmd,char *c0,char *c1) {arg(c0);arg(c1);invoke(cmd);}"
Data "void invoke(int cmd,char *i0,int i1,int i2,int i3,int i4,int i5) {arg(i0);arg(i1);arg(i2);arg(i3);arg(i4);arg(i5);invoke(cmd);}"
Data "void invoke(int cmd,char *i0,float i1,float i2,float i3,float i4,float i5,float i6,float i7) {arg(i0);arg(i1);arg(i2);arg(i3);arg(i4);arg(i5);arg(i6);arg(i7);invoke(cmd);}"
Data "void invoke(int cmd,char *i0,float i1,float i2,float i3,float i4,float i5,float i6,float i7,float i8,float i9) {arg(i0);arg(i1);arg(i2);arg(i3);arg(i4);arg(i5);arg(i6);arg(i7);arg(i8);arg(i9);invoke(cmd);}"
Data "void invoke(int cmd,char *i0,int i1) {arg(i0);arg(i1);invoke(cmd);}"
Data "void invoke(int cmd,char *i0,int i1,int i2) {arg(i0);arg(i1);arg(i2);invoke(cmd);}"
Data "void invoke(int cmd,char *i0,int i1,int i2,int i3) {arg(i0);arg(i1);arg(i2);arg(i3);invoke(cmd);}"
Data "void invoke(int cmd,char *i0,int i1,int i2,int i3,int i4) {arg(i0);arg(i1);arg(i2);arg(i3);arg(i4);invoke(cmd);}"
Data "void invoke(int cmd,char *i0,char *i1,char *i2) {arg(i0);arg(i1);arg(i2);invoke(cmd);}"
Data "void invoke(int cmd,char *i0,float i1,int i2) {arg(i0);arg(i1);arg(i2);invoke(cmd);}"
Data "void invoke(int cmd,char *i0,char *i1,int i2) {arg(i0);arg(i1);arg(i2);invoke(cmd);}"
Data "void invoke(int cmd,char *i0,char *i1,int i2,int i3) {arg(i0);arg(i1);arg(i2);arg(i3);invoke(cmd);}"
Data "void invoke(int cmd,char *i0,int i1,float i2,float i3) {arg(i0);arg(i1);arg(i2);arg(i3);invoke(cmd);}"
Data "DWORD		threadid=0;"
Data "HANDLE		thread,callevent,resevent;"
Data "int __stdcall DllMain(int,int,void *)"
Data "{"
Data "	return 1;"
Data "}"
Data "extern 'C' _declspec(dllexport) int _cdecl call(const void *in,int ibytes,void *out,int outbytes)"
Data "{"
Data "	DWORD		res;"
Data "	result=out;"
Data "	argptr=(char*)in;"
Data "	if (threadid==0)"
Data "	{"
Data "		callevent=CreateEvent(0,0,0,0);"
Data "		resevent=CreateEvent(0,0,0,0);"
Data "		thread=CreateThread(0,0,StubMain,0,0,&threadid);"
Data "	}"
Data "	else"
Data "	{"
Data "		SetEvent(resevent);"
Data "	}"
Data "	while (true)"
Data "	{"
Data "		res=WaitForSingleObject(callevent,INFINITE);"
Data "		if (res==WAIT_OBJECT_0) break;"
Data "	}"
Data "	return stubcmd;"
Data "}"
Data "void invoke(int cmd)"
Data "{"
Data "	DWORD	res;"
Data "	stubcmd=cmd;"
Data "	SetEvent(callevent);"
Data "	while (true)"
Data "	{"
Data "		res=WaitForSingleObject(resevent,INFINITE);"
Data "		if (res==WAIT_OBJECT_0) break;"
Data "	}"
Data "}"
Data "void arg(char *a)"
Data "{"
Data "	int		n;"
Data "	char	*p;"
Data "	p=argptr+4;n=0;"
Data "	while (*p++=*a++)"
Data "	{"
Data "		n++;"
Data "		if (n==4000) {*p++=0;break;}"
Data "	}"
Data "	*(int*)argptr=n-1;"
Data "	argptr=p;"
Data "}"
Data ""
