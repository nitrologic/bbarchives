; ID: 2338
; Author: Warpy
; Date: 2008-10-20 15:47:13
; Title: Map, Filter, Reduce functions for TLists
; Description: a bit of functional programming

'you can very easily rewrite these functions as methods in the TList definition in brl.mod/linkedlist.mod/linkedlist.bmx
'just take out the il:TList parameter and change '=Eachin il' to '=EachIn Self'

Function Filter:TList( il:TList, func(o:Object), inplace=0 )
	Local l:TList
	If inplace
		l=il
	Else
		l=New TList
	EndIf
	Local o:Object
	For o:Object=EachIn il
		If inplace
			If Not func(o) il.remove(o)
		Else
			If func(o) l.addlast o
		EndIf
	Next
	Return l
End Function

Function Map:TList( il:TList, func:Object(o:Object), inplace=0 )
	Local l:TList
	If inplace
		l=il
	Else
		l=New TList
	EndIf
	Local o:Object
	For o:Object=EachIn il
		If inplace
			il.remove o
			il.addlast func(o)
		Else
			l.addlast func(o)
		EndIf
	Next
	Return l
End Function

Function Reduce:Object( il:TList, func:Object( o1:Object, o2:Object ) )
	Local oo:Object, o:Object
	For o:Object=EachIn il
		If oo
			o=func(oo,o)
		EndIf
		oo=o
	Next
	Return oo
End Function



'An example for each of the functions

Function startswitha( o:Object )
	s$=String(o)
	If Not s Return 0
	If Lower(Chr(s[0]))="a" Return 1
End Function

Function prettify:Object( o:Object )
	s$=String(o)
	s=Upper(s[..1])+s[1..]
	Select s[Len(s)-1..]
	Case ",",".","?","!"
	
	Default
		s:+"."
	End Select
	Return s
End Function

Function longest:Object(o1:Object,o2:Object)
	If Len(String(o1))>Len(String(o2))
		Return o1
	Else
		Return o2
	EndIf
End Function

'make a list of test inputs
l:TList=New TList
txt$="aardvark bee armadillo Armature zygote twist! boogie. alack"
Print "input list~n-----------"
For word$=EachIn txt.split(" ")
	l.addlast word
	Print word
Next

'filter out words that don't start with the letter A
Print "~nfilter: starts with a~n-----------"
For word$=EachIn filter(l, startswitha)
	Print word
Next

'make each word look nice
Print "~nmap: prettify~n-----------"
For word$=EachIn map(l, prettify)
	Print word
Next

'join all the words together with hyphens
Print "~nreduce: longest~n-----------"
Print String( reduce(l, longest) )


dir=ReadDir("./")
files:TList=New TList
t$=NextFile(dir)
While t
	files.addlast t
	t=NextFile(dir)
Wend


'for a grand finale, an incredibly clever usage

'this function takes a suffix, and returns a function which tells you if the given string ends in the suffix or not
Function endsin(o:Object)(suffix$)
	Global a$=suffix
	Function f(o:Object)
		s$=String(o)
		If s[Len(s)-4..]=a Return 1 Else Return 0
	End Function
	Return f
End Function

'this function adds the current path to the given string
Function addpath:Object(o:Object)
	s$=String(o)
	Return CurrentDir()+s
End Function

Print "~n~nAll .bmx files in this directory~n-----------"
For t$=EachIn Map(Filter(files,endsin(".bmx")),addpath)
	Print t
Next
