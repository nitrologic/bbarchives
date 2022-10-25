; ID: 804
; Author: Andy_A
; Date: 2003-10-05 01:33:56
; Title: Base 64 Encoder/Decoder Functions
; Description: Base 64 encode/decode

;########     BASE 64 ENCODER by Mangus     ###########
;########   BASE 64 DECODER by Andy Amaya   ###########

org$ = "I have detected a Fatal Mouse Error - shall I spank the cat?"
Print "String to Encode:"
Print org$
Print""

Print "Encoded string:"
Print b64enc$(org$) ;little example ;)
Print""


msg$ = b64enc$(org$)
Print "Decoded string:"
Print b64dec$(msg$)
Input()


Function b64enc$(a$) 
	b64$="ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"
	m$=""
	f$=""
	largo=Len(a$)
;Encode a$ into one long string of bits
	cx$=""
	For encode=1 To largo
		x$=Mid$(a$,encode,1)  ;get one char at a time
		Tx=Asc(x$)            ;Tx = ASCII code
		b$=Bin$(Tx)           ;convert Tx into string of 32 bits
		b$=Right$(b$,8)       ;get the right most 8 bits out of the 32 bits
		cx$=cx$+b$            ;add string of 8 bits to cx$
	Next
	;largo = number of bits stored in cx$
	largo=Len(cx$)
	For encode=1 To largo Step 6
		x$=Mid$(cx$,encode,6)	
		bbb=Len(x$)
		bbbx=6-bbb 		;check for 6 bits
			;If not full 6 bits at end of bit string, then add "=" to end of encoded string
			If bbbx>0 Then
				f$="="
			EndIf
		x$=x$ + Left$("00000000",bbbx)			;pad with zeroes to make 6 bits
		res=0
		For y=0 To 5
			by  = Asc(Mid$(x$, 6-y, 1)) - 48	;get bits from right to left (least significant to most) 
			res = res + ( 2^y * by)				;raise to power of 2 and add to res (result)
		Next
		m$=m$+Mid$(b64$,res+1,1)+f$
	Next
	Return m$
End Function

Function b64dec$(a$)
	b64$="ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"
	m$=""
	f$=""
	length = Len(a$)
	cx$ = ""
	If Right$(a$,1) = "=" Then length = length - 2: flag = 1
	
	For decode = 1 To length
		x$ = Mid$(a$,decode,1)
		pos = Instr(b64$,x$)-1
		b$ = Bin$(pos)
		b$ = Right$(b$,6)
		cx$ = cx$ + b$
	Next

	If flag = 1 Then
		numBits = 6 * (Len(a$)-2)
		rmdr = (Floor(numBits/8)+1)*8-numBits
		oddChar$ = Mid$(a$,Len(a$)-1,1)
		pos =Instr(b64$, oddChar$)-1
		b$ = Right$(Bin$(pos),6)
		b$ = Left$(b$,rmdr)
		cx$ = cx$ + b$
	End If
	
	length = Len(cx$)

	For decode = 1 To length Step 8
		b$ = Mid$(cx$,decode,8)
		res = 0
		For y = 0 To 7
			bit =Asc( Mid$(b$,8-y,1)) - 48
			If bit <> 0 Then
				res = res Or (2^y * bit)
			End If
		Next
		m$ = m$ + Chr$(res)
	Next
	Return m$
End Function
