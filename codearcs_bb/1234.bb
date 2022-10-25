; ID: 1234
; Author: AntMan - Banned in the line of duty.
; Date: 2004-12-14 11:09:19
; Title: The Ambiguity Engine.
; Description: Ffront end to a compiler/anything.

nSwi("print","printf")

;-[Consts]
Const Max_Lines =5000

;-[]
Type Sep
	Field dat$
End Type

Type scope
	Field scopeType ;In most cases we can blindly assume
	Field m.map[ Max_Lines],mc
	Field sub.scope[2555],sc
	Field name$
End Type
Const Scope_Main=1,Scope_Function =2,Scope_Class=3

Type chunk
	Field real$,isSep
	Field map$,entry
    Field C_Name$,amb ;ambiguity flag. Nice..
	Field ext$ ;Extra dat field, for code operations.
End Type

Type map
	Field c.chunk[2555],cc
	Field dat$
End Type
Type code
	Field m.map[ Max_Lines],mc
	Field file$
End Type



Main.scope=New scope


;-define seperators
nSep("*"):nSep("(")
nSep(")"):nSep("/")
nSep(","):nSep("'")
nSep(":"):nSep("*")
nSep("-"):nSep("(")
nSep(")"):nSep("+")
nSep("<"):nSep("[")
nSep("]"):nSep(Chr(34))
nSep(">"):nSep("\")
nSep(" "):nSep("->")
nsep("="):nRes("function")
nRes("if")
fScop.scop=nScop( "function")
cScop.scop=nScop( "class")
tScop.scop=nScop("type")


;test.code=loadCode("helloworld.vnx")
test.code=loadCode("vCom.bb")
code_toCpp(test)

code_toConsoleApp(test,"SimpleTest.txt")
debug_code(test,"DebugPrisim.txt")

;objMap("function(test,nope  )")
;lM.map=genMap("if a->2 then b=(5*6)+2",map)


WaitKey

End
Function nSep(c$)
	s.sep=New sep
	s\dat=c
End Function

Function genMap(real$,map$)
	
End Function

Function loadCode.code( file$)
	fo=ReadFile(file)
	If Not fo Return
	out.code=New code
	While Not Eof(fo)
		dat$=ReadLine( fo)
		If Len(dat)>2
			m.map=objMap(dat)
			out\m[out\mc]=m
			out\mc=out\mc+1
		EndIf
	Wend
	Print "Loaded >"+out\mc+" Lines of raw code"
	CloseFile fo
	Return out
End Function

;--C++ Back end.

Global l_file,l_life
Function code_toConsoleAPP(in.code,file$)
	fo=WriteFile(file)
	l_life=fo
	If Not fo Return
	write_ConsoleHeaderC()
	write_codeC(in)
	write_consoleFooterC()
	CloseFile fo
End Function

Function write_codeC(in.code)
	For j=0 To in\mc-1
		out$=""
		For k=0 To in\m[j]\cc-1
			out=out+in\m[j]\c[k]\c_name
		Next
		WriteLine l_life,out
	Next
End Function

Function write_ConsoleHeaderC()
	WriteLine l_life,"#include <stdio.h>"
	WriteLine l_life,"int main(){"
End Function

Function write_consoleFooterC()
	WriteLine l_Life,"    return 0; /* Program termination. */"
	WriteLine l_life,"}"
End Function


Function code_toWindowApp(in.code)

End Function

Function code_toCPP( in.code)
	For j=0 To in\mc-1
		m.map=in\m[j]
		amb=False
		amb_m$=""
	        For k=0 To m\cc-1

			c.chunk=m\c[k]
			map$=c\map
			If c\isSep
				;-raw data enclapsulators
				If amb
					Select map
						Case Chr(34)
							If map=amb_m
								amb=False
								amb_m=""
							EndIf
					End Select
				Else
					Select map
						Case Chr(34)
							amb=True
							amb_m$=map
							over_amb=True
					End Select
				EndIf
				c\c_name=map
			Else
				c\c_name=map
				If isRes( map)
					
				Else
					c\c_name=switch(map)
				EndIf
			
			EndIf
			If amb
				If over_amb
					over_amb=0
				Else
					c\c_name=map
					c\amb=True
				EndIf
			EndIf
		Next
	Next
	;-- Scope Set up.
	main.scope=New scope
	main\scopeType=scope_main
	feedScope(main,in)
	debug_scope(main,"Scope_Debug.txt")
End Function

Function feedScope(base.scope,c.code,from=0,endAt$="")
Print "Scope From >"+from
	For j=from To c\mc-1
		.redo
		If j=>c\mc Exit
		m.map=c\m[j]
		Print "Obj Line"
		
		For k=0 To m\cc-1
			a.chunk=m\c[k]
			n.chunk=nObj(m,k) 
			p.chunk=nObj(m,k)
			If isScop(a\map) And j<>from And a\amb=False;Can't scope into thyself.
				ns.scope=New scope
				base\sub[base\sc]=ns
				base\sc=base\sc+1
				If  n<>Null
					Print "Scope Called >"+n\map
					ns\name=n\map
				Else
					Print "Warning -? Nameless Scope"
					ns\name="None specified"
				EndIf
				j=feedScope( ns,c,j,a\map)
				Goto redo
			Else
				;-determine if exit point.
				Select a\map
					Case "end"
						If n<>Null
						Select n\map
								Case endAt
									exitScope=True
									Exit ;no need to parse on. anything else is excess.
						End Select
						EndIf
				End Select
				;---
			EndIf
		Next
		base\m[base\mc]=m
		base\mc=base\mc+1
		If exitScope Return j+1
	Next
	If from>0
		Stop
		Print "Compile Error"
		Print endAt+" Without end "+endAt
	EndIf
	Print "Scope Out"
	Return j
End Function

Function debug_scope(base.scope,file$,debugSubs=True,useFile=0,pad$="--")
	If Not useFile
		 fo=WriteFile( file)
    	If Not fo Return
	Else
		 fo=useFile
	EndIf
	WriteLine fo,pad+"Scope Name:"+base\name
	WriteLine fo,pad+"Obj Maps:"+base\mc
	For j=0 To base\mc-1
		out$=""
		For k=0 To base\m[j]\cc-1
			out=out+base\m[j]\c[k]\map
		Next
		WriteLine fo,pad+"["+out
	Next
	WriteLine fo,String("/\",Len(pad))
	 If debugSubs
	 	For j=0 To base\sc-1
	 		debug_scope( base\sub[j],file,True,fo,pad+"--")
		Next
	 EndIf
	 If Not useFile
	 	CloseFile fo
	 EndIf
End Function


Function nObj.chunk( m.map,from=0)
	from=from+1
	If from=>m\cc Return 
	For j=from To m\cc-1
		If Not m\c[j]\isSep Return m\c[j]
	Next
End Function

Function pOIbj.chunk( m.map,from=0)
	from=from-1
	If from<0 Return 
	For j=from To 0 Step -1
		If Not m\c[j]\isSep Return m\c[j]
	Next
End Function



Type switch
	Field from$,go$
End Type

Function nswi(from$,go$)
	s.switch=New switch
	s\from=from
	s\go=go
End Function

Function switch$(dat$)
	For s.switch=Each switch
		If dat=s\from Return s\go
	Next
	Return dat
End Function


Function debug_code( in.code,file$)
	fo=WriteFile(file)
	If Not fo Return
	For j=0 To in\mc-1
		out$=""
		For k=0 To in\m[j]\cc-1
			out=out+"  "+String(in\m[j]\c[k]\amb,Len(in\m[j]\c[k]\c_name))+"  "
		Next
		
		
		WriteLine fo,out
		
		out$=""
		For k=0 To in\m[j]\cc-1
			out=out+"_/"+in\m[j]\c[k]\c_name+"\_"
		Next
		WriteLine fo,out
		out=""
		For k=0 To in\m[j]\cc-1
			out=out+" \"+String("_",Len(in\m[j]\c[k]\c_name))+"/ "
		Next
		WriteLine fo,out
	Next
	CloseFile fo
End Function



Function objMap.map(dat$)
	Dat=Lower(clean(dat))
	l=Len(dat)
	out=""
	fs=1
	m.map=New map
	m\dat=dat
	For j=1 To l ;examine single char seps
		c$=Mid( dat,j,1)
		If isSep(c)
			If j-lj>1
				obj$=Lower(Mid( dat,lj+1,j-lj-1))
				nc.chunk =New chunk
				nc\map=obj
				m\c[m\cc]=nc
				m\cc=m\cc+1
			EndIf
			lj=j
			
				nc.chunk =New chunk
				nc\map=c
				nc\isSep=True
				m\c[m\cc]=nc
				m\cc=m\cc+1
		
		Else
		
		EndIf
	Next
	Return m
End Function

Function isSep(v$)
For s.sep=Each sep
	If v=s\dat Return True
Next
End Function

;-[ Simple Example prisim operation. A simple text compression algo]

Function CompressCode(in.code,out$)
;-compression phase
fo=WriteFile( out)
If Not fo Return
Local cache$[25000],cc ;temp string buffer.
	For j=0 To in\mc-1
		m.map=in\m[j]
		For k=0 To m\cc-1
			dat$=m\c[k]\map
				linkTo=-1
		
		
			For a=0 To cc-1
				If cache[a]=dat
					linkTo=a
					Exit
				EndIf
			Next
				If linkTo=-1
					cache[cc]=dat
					cc=cc+1
				EndIf
	
			If linkTo>-1
				WriteByte fo,1
	    		WriteInt fo,linkTo
			Else
				WriteByte fo,0
				WriteString fo,dat
			EndIf
		Next
	Next
	CloseFile fo
End Function


Function clean$(dat$)
	out$=dat
	Return " "+Trim(out)+" "
End Function

Type res
	Field dat$
End Type
Type scop ;scope indentifier only.
	Field dat$
	Field moveMode
End Type

Const move_Up=1,move_Down=2


Function nScop.scop(txt$)
	s.scop=New scop
	s\dat=txt
	Return s
End Function
Function scopMove(n.scop,move)
    n\moveMode=move
End Function

Function isScop(txt$)
	For s.scop=Each scop
		If s\dat=txt Return True
	Next
	Return False
End Function

Function nRes(txt$)
	n.res=New res
	n\dat=Lower(txt)
End Function

Function isRes(txt$)
	For n.res= Each res
	   If n\dat=txt Return True
	Next
End Function
