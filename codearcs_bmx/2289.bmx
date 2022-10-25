; ID: 2289
; Author: Vignoli
; Date: 2008-07-20 10:55:38
; Title: special prime numbers
; Description: find prime numbers based on big pair numbers

Local cpt:Long=0

Local i:Long=0

Local k:Long=0


a$=Input("Nombre multiple de 6 ou 'q' pour quitter (Number multiple of 6 or 'q' to quit)  :")

If Lower(a$)="q" Then End

cpt=Long(a$)

i=5
flag=0
While flag=0
		k=cpt+i
		flag=IsPremier(k)
		If KeyDown(KEY_ESCAPE)=True Then End
		If flag=1
			Print cpt+" + "+i+" = "+(cpt+i)+" est premier (is prime)"
		Else 
			Print cpt+" + "+i+" = "+(cpt+i)+" n'est PAS premier (is not prime)"
			i=i+1
			While IsPremier(i)=0
				If KeyDown(KEY_ESCAPE)=True Then End
				i=i+1
			Wend
		EndIf
Wend

End


SetClsColor 0,0,0
Cls
SetColor 255,255,255

Function IsPremier(a:Long)
	Local j:Long
	If a=0 Then Return 0
	If a=1 Then Return 0
	If a=2 Then Return 1
	If (a Mod 2)=0 Then Return 0
	For j=1 To Long((a-3)/2)
		If KeyDown(KEY_ESCAPE)=True Then End
		If (a Mod (j+1))=0 Then Return 0
	Next
	Return 1
EndFunction
