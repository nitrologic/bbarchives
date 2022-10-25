; ID: 2155
; Author: Nebula
; Date: 2007-11-18 08:14:25
; Title: Rhyme detection
; Description: Does a line rhyme.

;
; Crom Design.
;
; When does a line rhyme.
; 
; Counts the last 3 characters of a word in a line. 
; Double or higher.
;
; How would a robot know the difference?
;

; Rijmt het of niet.
;
; Hoe slim kan een computer zijn?
;
; Een listing waarmee een computer kan weten of
; een tekst kan rijmen.
;
; De tel tussen spaties met de laatste 3 karakters.
;
; Hoe zou een robot dit kunnen weten?

;
AppTitle "Rijmt een regel."

SeedRnd MilliSecs()

Dim q$(10)

q$(0) ="A rhyming thing can sing"
q$(1) ="A fine time to dine."
q$(2) ="The sword was bought. Then it was brought to."
q$(3) ="The word and the bird."

a$ = q(Rand(2))

z = coulditrhyme(a$)
DebugLog a$
;
If z=True Then Notify "This line may rhyme. '" + a$ + "'"
If z=False Then Notify "This line may not rhyme. '" + a$ + "'"

Type end3
	Field veld$
End Type
Type dubs
	Field veld$
	Field tel
End Type

Function coulditrhyme(in$)
	;
	For i = 1 To Len(in$)
		b$ = Mid(in$,i,1)
		If b$ = "." Then b$ = " "
		a$ = a$ + b$
	Next
	;
	in$ = a$
	in$=in$+ " "
	;
	For i=1 To Len(in$)
		If Mid(in$,i,1) = " "
			a$ = Mid(in$,i-3,3)
			this.end3 = New end3
			this\veld = a$
		End If
	Next
	;
	For thatto.end3 = Each end3
		a=0
		For thzt.dubs = Each dubs
			If thzt\veld$ = thatto\veld$ Then 
				a=a+1
				thzt\tel = thzt\tel + 1
			End If
		Next
		If a=0 Then 
			z.dubs = New dubs
			If Len(thatto\veld$) > 1
				z\veld$ = thatto\veld$
			End If
		End If
	Next
	;
	For thot.dubs = Each dubs
		DebugLog thot\veld$
		DebugLog thot\tel
	Next
	;
	For thet.dubs = Each dubs
		If thet\tel > 0 Then Return True
	Next
	;
End Function
