; ID: 2668
; Author: Streaksy
; Date: 2010-03-18 10:41:15
; Title: String Functions
; Description: Some handy functions that deal with strings

Dim argsort(5000) ;sorting arguments





Function Capitalize$(st$,afterspaces=1) ;set afterspaces to 0 to only capitalize words after fullstops
o$=""
For t=1 To Len(st)
a$=Mid(st,t,1)
	If afterspaces=0 Then
	If a="." Or a="?" Or a="!" Then dotty=1:Goto nodotty
	If dotty And a<>" " Then a=Upper(a):dotty=0:Goto noxt
	EndIf
		If afterspaces=1 Then
		If a="." Or a="?" Or a="!" Or a=" " Then dotty=1:Goto nodotty
		If dotty Then a=Upper(a):dotty=0:Goto noxt
		EndIf
.nodotty
If t=1 Then a=Upper(a)
.noxt
o=o+a
Next
Return o$
End Function


;remove a string (or any character in it) from another string
Function FilterString$(st$,fil$,typ=0)  ;type - 0=remove all occurances of fil$   1=remove all occurances of any character in fil$
If typ=1 Then
For t=1 To Len(fil$)
st$=Replace(st$,Mid(fil,t,1),"")
Next
Return st$
EndIf
Return Replace(st$,fil$,"")
End Function

;get an argument (string component) with the given index and seperators
Function argument$(ss$,sp,seplist$=" ")
If ss$="" Then Return
stt=1:For tt=1 To Len(ss$);trim spaces off start!
mm$=Mid$(ss$,tt,1)
If mm$<>" " Then stt=tt:Exit
Next
For tt=stt To Len(ss$)	;find start of word
If sp-1=Spaces Then ws=tt:Exit
mm$=Mid$(ss$,tt,1)
If Instr(seplist$,mm$) Then Spaces=Spaces+1
Next
If ws=0 Then Return ;bad scr_argument!  out of range and stuff
If ws=Len(ss$) Then Return Right$(ss$,1)
wrd$=""
For tt = ws To Len(ss$)
k$=Mid$(ss$,tt,1)
If Instr(seplist,k$)=0 Then wrd$=wrd$+k$ Else Return wrd$
If tt=Len(ss$) Then Return wrd$
Next
End Function

;return how many arguments (string components) there are with a given seperators
Function arguments(S$,sep$=" ")
argz=0:newword=1
For t=1 To Len(s$)
m$=Mid(s$,t,1)
If newword=1 Then argz=argz+1:newword=0
	For sp=1 To Len(sep$)
	o$=Mid(sep,sp,1)
	If o$=m$ Then newword=1:Exit
	Next
Next
Return argz
End Function

;get the seperator character used between string components with the given index and seperators
Function ArgumentSeperator$(ss$,sp,seplist$=" ")
If ss$="" Then Return
stt=1:For tt=1 To Len(ss$);trim spaces off start!
mm$=Mid$(ss$,tt,1)
If mm$<>" " Then stt=tt:Exit
Next
For tt=stt To Len(ss$)	;find start of word
If sp-1=Spaces Then ws=tt:Exit
mm$=Mid$(ss$,tt,1):lastsep$=mm
If Instr(seplist$,mm$) Then Spaces=Spaces+1
Next
If ws=0 Then Return ;bad scr_argument!  out of range and stuff
If ws=Len(ss$) Then outo$=lastsep$:Goto retty;Right$(ss$,1)
wrd$=""
For tt = ws To Len(ss$)
k$=Mid$(ss$,tt,1):lastsep$=k
If Instr(seplist,k$)=0 Then wrd$=wrd$+k$ Else outo$=k$:Goto retty
If tt=Len(ss$) Then outo$=lastsep$:Goto retty
Next
Return
.retty
If Instr(seplist,outo) Then Return outo
End Function


;chop off any comments from a string (useful for code processing)
Function DeRem$(s$)
For t=1 To Len(s$)
mm$=Mid(s$,t,1)
If mm$=";" Then Return ss$ Else ss$=ss$+mm$
Next
Return s$
End Function

;like instr() but checks for several strings
Function InstrBatch(s$,c$) ;c$ contains a list of strings seperated by commas.  this checks if any of those strings are in s$
For t=1 To arguments(c,",")
If Instr(s,argument(c,t,",")) Then Return True
Next
End Function


;counts how man of the chars in C$ are in S$ !     (so really how many times each string has the same character)
Function CountChars(s$,c$) 
For ss=1 To Len(s$)
	For cc=1 To Len(c$)
	If Mid(s$,ss,1)=Mid(c$,cc,1) Then cnt=cnt+1
	Next
Next
Return cnt
End Function




;jumble a list of arguments in a string with the given seperator
Function JumbleArguments$(s$,seps$=" ") 
argz=arguments(s,seps)
For t=1 To argz:argsort(t)=t:Next
	For reps1=1 To 5
	For reps2=1 To argz
	a1=Rand(1,argz)
	a2=Rand(1,argz)
	v1=argsort(a1)
	v2=argsort(a2)
	argsort(a1)=v2
	argsort(a2)=v1
	Next
	Next
sep$=Left(seps,1)
For t=1 To argz
aa$=argument(s,argsort(t),seps)
;sep$=argumentseperator(s,argsort(t),seps)
o$=o$+aa+sep
Next
Return o$
End Function





Function XReplace$(s$,f$,t$) ;string from to (the native replace$() can be bugged)
fl=Len(f)
tl=Len(t)
sl=Len(s)
.again
If lf>ls Then Return ""
For tt=1 To sl-fl+1
If Mid(s$,tt,fl)=f$ Then
	s1$=Left(s,tt-1)
	s2$=Right(s,sl-tt-fl+1)
	s=s1+t+s2
	Goto again
EndIf
Next
Return s$
End Function




;find out where in s$ that t$ is
Function WhereInString(s$,t$)
lt=Len(t$):ls=Len(s$)
If lt>ls Then Return 0
For tt=1 To ls-lt+1
If Mid(s$,tt,lt)=t$ Then Return tt
Next
End Function
