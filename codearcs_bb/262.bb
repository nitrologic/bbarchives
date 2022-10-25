; ID: 262
; Author: mangus
; Date: 2002-03-09 16:18:02
; Title: Base 64 Encoder
; Description: Function to encode string text to base64

;##########################################
;########    BASE 64 ENCODER    ###########
;##########################################


Print b64enc$("This text is encoded in base 64") ;little example ;)
;Decoder in progress, but is a piece of cake if you understand the encoding process!


;##########################################
;######## FUNCTION STARTS HERE! ###########
;##########################################
Function b64enc$(a$) 

	b64$="ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"
	m$=""
	f$=""

	largo=Len(a$)

	cx$=""
	For decode=1 To largo

		x$=Mid$(a$,decode,1)
		Tx=Asc(x$)
		b$=Bin$(tx)
		b$=Right$(b$,8)
		cx$=cx$+b$

	Next

	largo=Len(cx$)

	For decode=1 To largo Step 6

		x$=Mid$(cx$,decode,6)	
		bbb=Len(x$)
		bbbx=6-bbb
			If bbbx>0 Then
				f$="="
			EndIf

		x$=x$+Left$("00000000",bbbx)
		res=0

		For y=1 To 6
			by=Mid$(x$,7-y,1)
			res=res+(2^(y-1)*by)
		Next

	m$=m$+Mid$(b64$,res+1,1)+f$
	
	Next

	Return m$
