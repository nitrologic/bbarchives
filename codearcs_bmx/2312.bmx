; ID: 2312
; Author: degac
; Date: 2008-09-12 09:21:21
; Title: LikeString
; Description: Check for multi Instring

Rem
		LikeString function
		
		return	=	LikeString("Gadget","G*adg",UpperCase=0|1)
					LikeString(where$,what$,uppercase=0|1)
					
		return 1 : it contains what
		
' ----- example

local parole$[]=["computer","transputer","company","DisableGadget","GadgetPrint","GadgetCopy"]

Print "Contains something..."
Print
For Local ss$=EachIn parole
	Print ss+" contains TER "+LikeString(ss,"ter")
Next
Print
Print "Finish with TER"
Print

For ss$=EachIn parole
	Print ss+" finishes with "+LikeString(ss,"*ter")
Next
Print
Print "Start with GADGET"
Print
For ss$=EachIn parole
	Print ss+" starts with "+LikeString(ss,"Gadget*")
Next
Print

Print "Contains ABLE & Gad (no case)"
Print
For ss$=EachIn parole
	Print ss+" contains (able*gad) "+LikeString(ss,"*abl*Gad*",1)
Next

Print LikeString("gadgetprint","g*pr*")
Print LikeString("gadgetprint","g*p*t")
End Rem

Function LikeString:Int(where$="",find$="",_case:Int=0)

If where="" Return 0
If find="" Return 0

Local _start:Int,_finish:Int
Local what$
Local lenwhat:Int

If _case=1 where=Upper(where);find=Upper(find)

If Left(find,1)="*"  _finish=1;what=find[1..]
If Right(find,1)="*" _start=1;what=find[..Len(find)-1]

lenwhat=Len(what)

If _finish=1 
	If Right(where,lenwhat)=WHAT Return 1
End If


If _start=1 
	If Left(where,lenwhat)=WHAT 
				Return 1
	End If
End If
find=find+"*"
Local p1:Int
Local last_pos:Int,pa_count:Int,pa$[],papos:Int[]

find=Replace(find,"**","*")

While p1<Len(find)
If find[p1]=Asc("*")
		pa=pa[..pa_count+1]
		papos=papos[..pa_count+1]
		pa[pa_count]=find[last_pos..p1]
		papos[pa_count]=last_pos
		last_pos=p1+1
		pa_count:+1
EndIf

p1:+1

Wend

Local result:Int,counter:Int,cpos:Int
For Local ss$=EachIn pa
	cpos=Instr(where,ss)
	If cpos>0
		If papos[counter]<=cpos result:+1
	End If
	counter:+1
Next
If result=pa.length Return 1 Else Return 0
End Function
