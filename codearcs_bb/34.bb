; ID: 34
; Author: DJWoodgate
; Date: 2002-03-29 17:57:18
; Title: Format$ (number,digits [,places])
; Description: Format number with aligned digits and fixed decimal field

SeedRnd MilliSecs()
For c=1 To 20
x#=Rnd(-100000000,100000000)/Rnd(1000)
Print "Formatted: "+format$(x,6,4)+ " Unformatted: "+x#
Next
waitkey()


Function format$(v#,d,p=0) ; format a number
Local m$, e$, r$,  n,  x#=Abs(v)
If p>0 Then m$=Int((x-Floor(x))*10^p) : e$="."+Right$("000000"+m$,p)
If p=0 Or Len(m$)>p Then n=Int(x) Else n=Floor(x)
If v<0 Then r$="-"+n Else r$=n 
If Len(r$)>d or n<0 Then r$=String$("*",d): If p>0 Then e$="."+String$("*",p)
Return RSet$(r$,d)+e$
End Function

'---------Bmax version of function -----------------
Function format$(v!,d,p) ' bmax format a number
Local m$, e$, r$,  n:Long,  x!=Abs(v)
If p>0 Then m$=Long((x-Floor(x))*10^p+0.5!) ; e$="."+Right$("000000000000000000"+m$,p)
If p=0 Or Len(m$)>p Then n=Long(x+0.5!) Else n=Long(x)
If v<0 Then r$="-"+n Else r$=n 
If Len(r$)>d Or n<0 Then 
	r$=left$("*******************",d)
	If p>0 Then e$="."+left$("*******************",p)
Endif	
Return RSet$(r$,d)+e$
End Function
