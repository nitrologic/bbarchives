; ID: 2914
; Author: ozzi789
; Date: 2012-01-30 09:16:26
; Title: Vernam-Cipher | Encryption &amp; Decryption
; Description: Encrypt & Decrypt Strings with a password

;ozzi789 - 30.01.2012
original_string$="Vernam ist toll"
Print "Original String: "+original_string$

encrypted_string$= vernam_enc(original_string$,"secret1")
Print "Encrypted String: "+encrypted_string$

decrypted_string$=vernam_dec(encrypted_string$,"secret1")
If original_string$=decrypted_string$
	Print "Decrypted String: "+decrypted_string$
Else
	Print "This should not happen :'("
EndIf 


Function vernam_enc$(strng$,key$)
	len_strng=Len(strng$)
	len_key=Len(key$)
	
	key_index=1
	For x=1 To len_strng
		current_strng$=Mid(strng$,x,1)
		current_key$=Mid(key$,key_index,1)
		new_strng$=new_strng$+Chr(Asc(current_strng$)+Asc(current_key$))
		key_index=(x Mod len_key)+1
	Next	
	Return new_strng$
End Function 

Function vernam_dec$(strng$,key$)
	len_strng=Len(strng$)
	len_key=Len(key$)
	
	key_index=1
	For x=1 To len_strng
		current_strng$=Mid(strng$,x,1)
		current_key$=Mid(key$,key_index,1)
		new_strng$=new_strng$+Chr(Asc(current_strng$)-Asc(current_key$))
		key_index=(x Mod len_key)+1
	Next	
	Return new_strng$
End Function
