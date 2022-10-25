; ID: 2004
; Author: skidracer
; Date: 2007-04-30 08:02:13
; Title: itoh
; Description: Produces C/C++ .h files from BlitzMax modules

' itoh.bmx

' converts blitzmax .i interface files to c/c++ .h files

' WARNING: this program generates many files in currentdir

Local modpath$=RequestDir("Please select your blitzmax/mod directory as source path",CurrentDir())

MakeHeaders modpath,".release.win32.x86","bb"

' "C:/blitzmax/mod",".release.win32.x86","bb"

Function MakeHeaders(modpath$,suffix$,prefix$)
	Local suffixi$=suffix+".i"
	Local suffixa$=suffix+".a"
	For Local d$=EachIn LoadDir(modpath)
		If d[d.length-4..]<>".mod" Continue
		For Local m$=EachIn LoadDir(modpath+"/"+d)
			If m[m.length-4..]<>".mod" Continue
			Local id$=d[..d.length-4]+"_"+m[..m.length-4]
			For Local f$=EachIn LoadDir(modpath+"/"+d+"/"+m)
Rem
uncomment to archive single super mods.a
				If f[f.length-suffixa.length..]=suffixa
					Local ar$="ar -vrus mods.a "+modpath+"/"+d+"/"+m+"/"+f
					DebugLog ar
					system_ ar
					Continue
				EndIf
EndRem				
				If f[f.length-suffixi.length..]<>suffixi Continue
				Local hpath$=d[..d.length-4]+"_"+f[..f.length-suffixi.length]+".h"
				Local ipath$=modpath+"/"+d+"/"+m+"/"+f
				CreateHeader hpath,ipath,id,prefix
			Next
		Next	
	Next
	Local b$=modpath+"/brl.mod/blitz.mod/"
	CopyFile b+"blitz_types.h","blitz_types.h"
	CopyFile b+"blitz_object.h","blitz_object.h"
	CopyFile b+"blitz_array.h","blitz_array.h"
	CopyFile b+"blitz_string.h","blitz_string.h"
	CopyFile b+"blitz_debug.h","blitz_debug.h"
	CopyFile b+"blitz_handle.h","blitz_handle.h"
End Function

Function CreateHeader( hpath$,ipath$,id$,prefix$ )
	Local f:TStream
	Local l$,sname$,ssuper$,p,q
	Local struct$
	Local hdrs$,structs$,defs$,funcs$

	If id="brl_blitz"
		hdrs:+"#include ~qblitz_types.h~q~n"
		hdrs:+"#include ~qblitz_debug.h~q~n"
		hdrs:+"#include ~qblitz_object.h~q~n"
		hdrs:+"#include ~qblitz_string.h~q~n"
		hdrs:+"#include ~qblitz_array.h~q~n"
		hdrs:+"#include ~qblitz_handle.h~q~n"
	EndIf

	f=ReadStream(ipath)
	If Not f Return
	While Not Eof(f)
		l=f.ReadLine()
		If l[..10]="ModuleInfo" Continue
		
		If l[..7]="import "
			l=l[7..]	
			If l[..1]="~q" Continue 'ignore misc import directives
			p=l.findlast(".")
			If p=-1
				DebugStop
			EndIf
			l=l.Replace(".","_")
'			l=l[p+1..]
			hdrs:+"#include ~q"+l+".h~q~n"
			Continue
		EndIf
		If l[l.length-1..]="{"
			p=l.find("^")
			If p<>-1
				sname=l[..p]
				ssuper=l[p+1..l.length-1]
				p=ssuper.findlast(".")
				ssuper=":"+ssuper[p+1..]
				If ssuper=":Null" ssuper=""	
				ssuper=ssuper.Replace(":Object",":BBObject")		
				struct$="struct "+sname+ssuper+"{~n"
'				DebugLog struct
			EndIf
			Continue
		EndIf
		If struct
			If l[..2]="}=" Or l[..3]="}A=" Or l[..3]="}E="
				struct:+"};~n~n"			
'				DebugLog struct
				structs:+struct
				struct=""
				Continue
			EndIf		
			If l[..1]="."
				l=l[1..l.length-1]
				struct:+"~t"+bb2c(l)+";~n"
		
			EndIf
			Continue
		EndIf
		p=l.findlast("=")
		If p<>-1
			Select l[p-1..p]
				Case ")"
					Local bbname$,args$,ftype$
					bbname=l[p+1..]
					l=l[..p]
					p=l.find("(")
					args=l[p+1..l.length-1]
					l=l[..p]
					funcs:+funcbb2c(l,bbname,args,prefix)
'					DebugLog "FUNCTION:l="+l+" bbname="+bbname+" args="+args
				Case "%"
					defs:+"#define "+l[..p-1]+" "+l[p+1..]+"~n"
				Case "$"
					defs:+"#define "+l[..p-1]+" "+l[p+2..]+"~n"
				Case "&"
'					DebugLog "Ptr= ??? "+l
				Case "S"
'					DebugLog "STDCALL Function= ??? "+l
				Default
					DebugStop
			End Select
			Continue
		EndIf
	Wend
	CloseFile f
	
	id=id.ToUpper()+"_H"


	Local h$=hdrs+"~n"+defs+"~n"+structs+funcs
	
	h="#ifndef "+id+"~n#define "+id+"~n~n"+h+"#endif~n"
		
	SaveText h,hpath

End Function


Function funcbb2c$(l$,bbname$,args$,prefix$)
	Local h$,a$
	bbname=bbname[1..bbname.length-1]
	h=bb2c(l)+"("	'strips l and returns c decl
	h=h.Replace(l+"(",bbname+"(")
	While True
		a=getarg(args)
		If Not a Exit
		h:+bb2c(a)
		If args h:+","	
	Wend
	h:+");~n"
	l=prefix+l
	If l<>bbname
		h:+"#define "+l+" "+bbname+"~n~n"
	EndIf
'	DebugLog h
	Return h
End Function


Function bb2c$(bb$ Var)	'on return strips input string to friendly name
	Local p,t$,eq$,fn$,ar$,std$

	Local o$=bb
	
	p=bb.find("=")
	If p<>-1
		eq$=bb[p..]
		bb=bb[..p]
		If eq[..2]="=~q"
			eq=eq[2..eq.length-1]
			If eq[..2]="bb" 
				eq="&"+eq
			EndIf
			eq="="+eq
		EndIf
		If eq[..2]="=$"
			If eq="=$~q~q"
				eq="=&bbEmptyString"
			Else
				DebugLog "&&&&&&&&"+eq
				eq=""
			EndIf
		EndIf
		Select eq[eq.length-1..]
			Case "!","#"
				eq=eq[..eq.length-1]
				If eq.find(".")=-1 eq=eq+".0"
				eq=eq+"f"
		End Select
	EndIf

	While bb.length
		If bb[bb.length-4..]=" Var"
			t="&"+t
			bb=bb[..bb.length-4]
			Continue		
		EndIf

		If bb[bb.length-2..]=")S"
			bb=bb[..bb.length-1]
			std="__stdcall "
		EndIf

		
		Select bb[bb.length-1..]
			Case ")"
				p=bb.find("(")
				fn=bb[p..bb.length]
				bb=bb[..p]
				
				Local args$=fn[1..fn.length-1]
				If args
					fn=""
					While True
						Local a$=getarg(args)
						If Not a Exit
						fn:+bb2c(a)
						If args fn:+","	
					Wend
					fn="("+fn+")"
				EndIf

				Continue				
			Case "%"
				If bb[bb.length-2..]="%%"
					t="BBInt64"+t
					bb=bb[..bb.length-2]
					Continue
				EndIf
				t="int"+t
			Case "#"
				t="float"+t
			Case "!"
				t="double"+t
			Case "$"
				t="BBString*"+t
			Case "z"
				If bb[bb.length-2..]="$z"
					t="const char *"+t
					bb=bb[..bb.length-2]
					Continue
				EndIf
			Case "@"
				If bb[bb.length-2..]="@@"
					t="BBSHORT"+t
					bb=bb[..bb.length-2]
					Continue
				EndIf
				t="BBBYTE"+t
			Case "*"
				t="*"+t
'Function LoadDir$[]( dir$,skip_dots=True )
'	Object&[]* _datas;
			Case "]"
				p=bb.find("&[")
'				If bb[bb.length-3..]="&[]"
				If p<>-1
					ar="BBArray*"+t
					bb=bb[..p]
					Continue
				EndIf			
				DebugStop
			Default

				p=bb.find(":")
				If p<>-1
			
					If t DebugStop
					
					o=bb[p+1..]
					bb=bb[..p]
					p=o.findlast(".")	'typedef
					o=o[p+1..]
					
					If o="Object" 
						o="BBObject"
					Else
						If eq
							eq="=("+o+"*)"+eq[1..]
						EndIf
					EndIf
					
'					If bb="_datas" DebugStop
			
					If ar Return ar+" "+bb

'					DebugLog"default:"+o+" *"+bb
					
					Return o+" *"+bb+eq
				EndIf

				If bb="char" bb="c"
				If ar t=ar	'ignore type of array for now
				If fn					
'					DebugLog "FN:"+t+"("+bb+")"+fn+eq
					Return t+"("+std+bb+")"+fn+eq
				EndIf
				If t="Object&[]*" DebugStop
				
				Return t$+" "+bb+eq
		End Select			
		bb=bb[..bb.length-1]
	Wend
End Function

Function getarg$(arg$ Var)
	Local p=arg.find(",")
	Local q=arg.find("(")
	If q<>-1 And q<p	'needs to handle nest
		q=arg.find(")",q)
		p=arg.find(",",q)
	EndIf
	If p=-1 p=arg.length
	Local a$=arg[..p]
	arg=arg[p+1..]
	Return a
End Function
