; ID: 2910
; Author: ozzi789
; Date: 2012-01-11 02:44:43
; Title: Run LengtRun Length En/Decoding
; Description: Compresses redundant information

Function RLE_ENCODE$(strng$,offset=4,marker=1)
	;Reset Variables
	x=0 ;Pointer for String
	count=1 ;Runcount
	strng_len=Len(strng$)
	While (x<strng_len) ;Check if we havent run through the whole string
		x=x+1 ;increase pointer
		
		cursign$= Mid(strng$,x,1) ;read char at pointer
		nextsign$= Mid(strng$,x+1,1) ;read next char
		
		If cursign$=nextsign$ ; if the current and the next sign are the smae
			count=count+1 ; increase runcount
		Else
			If count>=offset ; If the chars werent identic, but the runcount was bigger or same
				newstrng$=newstrng$+Chr(marker)+Len(Str(count))+count ;Add coded string to marker & runcount char and runcount
			Else If count>1 ; if the count of repeating chars was smaller then the offset
				newstrng$=newstrng$+String(cursign$,count-1) ; just clone them into new string
			EndIf 
			count=1 ; reset count
			newstrng$=newstrng+cursign$ ;add the actuall char
		EndIf 
	Wend 	
Return newstrng$ ;return finished string
End Function



Function RLE_DECODE$(strng$,marker=1)
	x=0 ;pointer for the string
	strng_len=Len(strng$)
	While (x=<strng_len) ;Check if we havent run through the whole string
	
		cursign$= Mid(strng$,x,1) ;get current char
		nextsign$= Mid(strng$,x+1,1) ;get next char
		
		If cursign$=Chr(marker) ;if current char is the marker
			countlenght=Int(nextsign$) ; Get the runcount char, example if its 1 the number has then lenght 1 - example 5 . if the number is 4 the number has a lenght of 4 - example 1234
			count=Int(Mid(strng$,x+2,countlenght)) ; get the count via our runcount char
			newstrng$=newstrng$+String(Mid(strng$,x+3+countlenght-1,1),count-1) ;Add the char
			x=x+2+countlenght ; increase pointer
		Else
			newstrng$=newstrng$+cursign$ ;add the original char
			x=x+1 ; increase pointer
		EndIf  
	Wend 	
Return newstrng$ ;return string
End Function
