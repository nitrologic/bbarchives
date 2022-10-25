; ID: 161
; Author: add
; Date: 2001-12-10 07:15:34
; Title: Parser Function
; Description: Breaks strings into elements.Includes Val function.

; PARSER AND VAL FUNCTION
;==========================
;by adam barton V1.0
;
;bugs and wish list to add3d@talk21.com
;
;check the example For demostration of how it works!





Function parse(in$,t$) 
; parser functions to break up strings into words and numbers
; returns the number of elements
; fills the type back with the words,integers,and floats
; 
Local rc=0
Local sp=0
Local p
Local q
For back.parsereturn=Each parsereturn ; empty old data
	Delete back
Next
in$=in$+"X"
While Len(in$)>0
	nx=Len(in$)
	For q=1 To Len(t$)
		p=Instr(in$,Mid$(t$,q,sp+1))
		If p<>0 And p<nx Then nx=p
	Next
	r$=Left$(in$,nx-1)
	If r$<>"" Then 
		rc=rc+1
		back.parsereturn=New parsereturn
		back\word$=r$
		back\num=val(r$)
		back\real=realreturn#
		;back\num=Int(realreturn#)
	End If
	in$=Right$(in$,Len(in$)-nx)
Wend
Return rc
End Function 
;========================================================
Function val(txt$)
; val converts a string to a number
; handles negative,fractions and exponents
; the realvalue is returned in realreturn# DEFINE GLOBALY!!!!
;											Global RealReturn#=0
; the integer is returned
Local d$="" ;the whole number
Local dn
Local f$=""	;the fraction
Local fn#
Local e$=""	;the exponent
Local en#
Local s=0
; This did use an array but it has to be declared outside the function which was a pain
got=0

For q=1 To Len(txt$)
	l$=Mid$(txt$,q,1)
	If l$="e" And (Len(d$)+Len(f$))>0 Then got=2
	If l$="." Then If got=0 Then got=1 Else got=3
	If got<>3 And Instr("-1234567890",l,1)>0 Then
		If got=0 Then d$=d$+l$
		If got=1 Then f$=f$+l$
		If got=2 Then e$=e$+l$
	End If
Next
dn=vint(d$):s=Sgn(dn):dn=Abs(dn):If s=0 Then s=1
fn#=(vint(f$))/(10.0^Len(f$))
en#=10^vint(e$)
realreturn#=s*((dn+fn#)*en#)
Return (Int(realreturn#))
End Function
;========================================================
Function vint(txt$)
; vint converts a string of digits into a signed integer
vi=0
If Left$(txt$,1)="-" Then s=-1:txt$=Right$(txt$,Len(txt$)-1) Else s=1
For q=1 To Len(txt$)
	v=Asc(Mid$(txt$,q,1))-48
	vi=vi*10+v
Next
vi=vi*s
Return vi
End Function
