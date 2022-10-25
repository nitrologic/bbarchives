; ID: 822
; Author: Filax
; Date: 2003-11-05 11:04:39
; Title: How encrypt a string
; Description: Function for crypt a string with a key

Tmp$=FBK_CryptString$("This is a test",$E2CDF032)
Print Tmp$

Tmp2$=FBK_CryptString$(Tmp$,$E2CDF032)
Print Tmp2$

WaitKey

Function FBK_CryptString$(Source$,Key)
	For C=1 To Len(Source$)
		Char$=Char$+Chr$(Asc(Mid$(Source$,C,1) ) Xor Key)
	Next

	Return Char$
End Function
