; ID: 2030
; Author: Vignoli
; Date: 2007-06-08 01:36:54
; Title: megacalculs
; Description: use strings for maths

;=====================
;    megacalculs 1
;=====================


; Initialisation
;================

Dim table(9,10)
Dim retenue(9,10)

For i=0 To 9
	For j=0 To 10
		table(i,j)=i+j
		If table(i,j)>9 Then table(i,j)=table(i,j)-10 : retenue(i,j)=1 Else retenue(i,j)=0
	Next
Next

Dim table2(10,10)
Dim retenue2(10,10)
For i=0 To 10
	For j=0 To 10
		table2(i,j)=i-j
		If table2(i,j)<0 Then table2(i,j)=table2(i,j)+10 : retenue2(i,j)=1 Else retenue2(i,j)=0
	Next
Next

; Principal Loop
;===================

Repeat

Print"Premier nombre  (first number) : "
v1$=Input("")
Print"Operation (+ - * /) :"
op$=Input("")
Print"Deuxieme nombre (second number) : "
v2$=Input("")


If Left(v1$,1)="-" Then sig1$="-" Else sig1$=""
If Left(v2$,1)="-" Then sig2$="-" Else sig2$=""

If op$="+"
	sig3$=""
	If sig1$="-" And sig2$="-" Then sig3$="-" : v1$=Mid(v1$,2) : v2$=Mid(v2$,2) : sig1$="" : sig2$=""
	If sig1$="-" Then v1$=Mid(v1$,2) : a$=v1$ : v1$=v2$ : v2$=a$: sig2$="" : sig1$="" : op$="-"
	If sig2$="-" Then v2$=Mid(v2$,2) : op$="-" : sig1$="" : sig2$=""
Else
	If op$="-"
		sig3$=""
		If sig1$="-" And sig2$="" Then sig3$="-" : v1$=Mid(v1$,2) : sig1$="" : sig2$="" : op$="+"
		If sig1$="-" And sig2$="-" Then v1$=Mid(v1$,2) : v2$=Mid(v2$,2) : a$=v1$ : v1$=v2$ : v2$=a$: sig2$="" : sig1$=""
		If sig2$="-" Then v2$=Mid(v2$,2) : sig1$="" : sig2$="" : op$="+"
	Else
		If op$="*" Or op$="/"
			sig3$=""
			If sig1$="-" And sig2$="-" Then v1$=Mid(v1$,2) : v2$=Mid(v2$,2) : sig1$="" : sig2$=""
			If sig1$="-" And sig2$="" Then sig3$="-" : v1$=Mid(v1$,2) : sig1$="" : sig2$=""
			If sig2$="-" And sig1$="" Then sig3$="-" : v2$=Mid(v2$,2) : sig1$="" : sig2$=""
		EndIf
	EndIf
EndIf

virg=0
virg1=0
virg2=0
If op$="+" Or op$="-" Or op$="*"
	If Instr(v1$,".")<>0
		newv1$=v1$
		v1$=""
		While Right(newv1$,1)<>"."
			v1$=Right(newv1$,1)+v1$
			virg1=virg1+1
			newv1$=Mid(newv1$,1,Len(newv1$)-1)
		Wend
		v1$=Mid(newv1$,1,Len(newv1$)-1)+v1$
	EndIf
	If Instr(v2$,".")<>0
		newv2$=v2$
		v2$=""
		While Right(newv2$,1)<>"."
			v2$=Right(newv2$,1)+v2$
			virg2=virg2+1
			newv2$=Mid(newv2$,1,Len(newv2$)-1)
		Wend
		v2$=Mid(newv2$,1,Len(newv2$)-1)+v2$
	EndIf
	If op$="+" Or op$="-"
		If virg1>0 Or virg2>0
			If virg1>virg2
				nbz=virg1-virg2
				For i=1 To nbz
					v2$=v2$+"0"
				Next
				virg2=virg1
			EndIf
			If virg2>virg1
				nbz=virg2-virg1
				For i=1 To nbz
					v1$=v1$+"0"
				Next
				virg1=virg2
			EndIf
		EndIf
	EndIf
	If op$="*" Then virg1=virg1+virg2
	virg=virg1
EndIf

If op$="+" Then Gosub addition
If op$="-" Then Gosub soustraction
If op$="*" Then Gosub multiplicationrapide
If op$="/" Then Gosub division


If virg>0
	tempres$=res$
	res$=""
	While virg>0 And tempres$<>""
		res$=Right(tempres$,1)+res$
		tempres$=Left(tempres$,Len(tempres$)-1)
		virg=virg-1
	Wend
	res$=tempres$+"."+res$
	While Right(res$,1)="0"
		res$=Left(res$,Len(res$)-1)
	Wend
	If Right(res$,1)="." Then res$=Left(res$,Len(res$)-1)
	If res$="" Then res$="0"
EndIf



If sig3$="-" And res$<>"0" Then res$="-"+res$

res2$=""
If op$="/" And reste$<>"0" Then res2$=" reste "+reste$
Print ""
Print ""
Print "Resultat : "
Print res$+res2$
Print ""
Print ""

Forever

End

; SubRoutines & fonctions
;=========================

; add quickly
.addition
l=maxi(Len(v1$),Len(v2$))
If Len(v1$)<l Then a$=v1$ : v1$=v2$ : v2$=a$
While Len(v2$)<l
	v2$="0"+v2$
Wend
res$=""
For i=l To 1 Step -1
	c1=Int(Mid(v1$,i,1))	
	c2=Int(Mid(v2$,i,1))
	If i<l Then c2=c2+retenue(exc1,exc2)
	exc1=c1
	exc2=c2
	res$=Str(table(c1,c2))+res$
Next
If retenue(exc1,exc2)>0 Then res$="1"+res$
While Len(res$)>1 And Left(res$,1)="0"
	res$=Mid(res$,2)
Wend
Return

; substract quickly
.soustraction
l=maxi(Len(v1$),Len(v2$))
If Len(v1$)<l Then a$=v1$ : v1$=v2$ : v2$=a$ : sig$="-" Else sig$=""
While Len(v2$)<l
	v2$="0"+v2$
Wend
i=1
While i<=l
	If Int(Mid(v1$,i,1))<Int(Mid(v2$,i,1)) Then flag=1 : i=l+1
	If Int(Mid(v1$,i,1))>Int(Mid(v2$,i,1)) Then flag=0 : i=l+1
	i=i+1
Wend
If flag=1 Then a$=v1$ : v1$=v2$ : v2$=a$ : If sig$="" Then sig$="-" Else sig$=""
If Len(v1$)=Len(v2$) Then v1$="0"+v1$ : v2$="0"+v2$ : l=l+1
res$=""
For i=l To 1 Step -1
	c1=Int(Mid(v1$,i,1))	
	c2=Int(Mid(v2$,i,1))
	If i<l Then c2=c2+retenue2(exc1,exc2)
	exc1=c1
	exc2=c2
	res$=Str(table2(c1,c2))+res$
Next
While Len(res$)>1 And Left(res$,1)="0"
	res$=Mid(res$,2)
Wend
res$=sig$+res$
Return

; slow multiplication (used by quick multiplication)
.multiplication
If IsZero(v1$)=True Or IsZero(v2$)=True Then res$="0" : Return
memv2$=v2$
memv11$=v1$
v2$=v1$
Repeat
If IsZero(memv2$)=False
	Gosub addition
	memv1$=res$
	v1$=memv2$
	v2$="1"
	Gosub soustraction
	memv2$=res$
	v1$=memv1$
	v2$=memv11$
EndIf
If IsZero(memv2$)=True Then Exit
Forever
Gosub soustraction
While Len(res$)>1 And Left(res$,1)="0"
	res$=Mid(res$,2)
Wend
Return

; slow division
.division
If IsZero(v1$)=True Or IsZero(v2$)=True Then res$="0" : Return
memv11$="0"
memv2$=v2$
memv1$=v1$
Repeat
If IsZero(v1$)=False And Left(v1$,1)<>"-"
	memres$=v1$
	Gosub soustraction
	memv1$=res$
	If Left(res$,1)<>"-"
		v1$=memv11$
		v2$="1"
		Gosub addition
		memv11$=res$
	EndIf
	v1$=memv1$
	v2$=memv2$
EndIf
If IsZero(v1$)=True Or Left(v1$,1)="-" Then Exit
Forever
If Left(v1$,1)="-" Then a$=v1$ : v1$=v2$ : v2$=a$ : v2$=Mid(v2$,2) : Gosub soustraction : reste$=res$ Else reste$="0"
res$=memv11$
If Left(reste$,1)="-" Then reste$=Mid(reste$,2)
While Len(res$)>1 And Left(res$,1)="0"
	res$=Mid(res$,2)
Wend
Return

Function IsZero(a$)
For i=1 To 9
	If Instr(a$,Str(i))<>0 Then Return False
Next
Return True
End Function

Function maxi(a,b)
If a>=b Then Return a Else Return b
End Function

; quick multiplication
.multiplicationrapide
total$="0"
myv1$=v1$
myv2$=v2$
For k=Len(myv2$) To 1 Step -1
	v1$=myv1$
	v2$=Mid(myv2$,k,1)
	Gosub multiplication
	kzero$=""
	j=Len(myv2$)-k
	While j>0
		kzero$=kzero$+"0"
		j=j-1
	Wend
	v1$=res$+kzero$
	v2$=total$
	Gosub addition
	total$=res$
Next
res$=total$
Return
