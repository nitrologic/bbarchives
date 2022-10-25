; ID: 1166
; Author: Mr Brine
; Date: 2004-09-25 07:01:09
; Title: Dynamic string array
; Description: Allows one to modify / create one string containing multiple substrings.

; (c)oded by Mr Brine
;
;
; - doesnt require a split char
; - any character can be passed
; - use the provided functions to create the split string
; - a split string probably wont display properly if viewed in its raw state. 
; - max string length that can be added to a split string is $ffff
; - fast, only 2 characters accessed per substring when navigiating the sub strings
;
; ----------------------------------------------------------------------------------------------------


Function split_debuglog(raw$)

	Local l = Len(raw)
	Local o = 1
	Local i = 0
	Local s$
	
	DebugLog "========================================================================================"
	DebugLog "count: " + Split_Count(raw$)
	DebugLog "len:   " + Len(raw)
	DebugLog "raw:   " + raw
	DebugLog "----------------------------------------------------------------------------------------"
	DebugLog "substr|   raw  | string | value"
	DebugLog "index | offset |  len   |"
	DebugLog "----------------------------------------------------------------------------------------"
	

	While o < l
		
		DebugLog i + String(" ", 6 - Len(i)) + "|" + o + String(" ", 8 - Len(o)) + "|" + split_ssi_getlen(raw, o) + String(" ", 8 - Len(split_ssi_getlen(raw, o))) + "|"+ "!" + split_ssi_gettext$(raw, o) + "!"
		o = split_ssi_gotonext(raw, o)
		i = i + 1

	Wend

End Function 


; ----------------------------------------------------------------------------------------------------


Function Split_Append$(raw$, sub$)

	Return raw + split_ssi_create(sub, 1)

End Function



Function Split_Count(raw$)
	
	Local c
	Local l = Len(raw)
	Local o = 1

	While o < l
		
		c = c + 1
		o = split_ssi_gotonext(raw, o)

	Wend

	Return c

End Function 



Function Split_Get$(raw$, ndx)

	Local l = Len(raw)
	Local o = 1
	
	While o < l
	
		If(Not ndx) Return Mid(raw, split_ssi_getoff(o), split_ssi_getlen(raw, o))
		o = split_ssi_gotonext(raw, o)
		ndx = ndx - 1
	
	Wend 

End Function 



Function Split_GetLen(raw$, ndx)

	Local l = Len(raw)
	Local o = 1
	
	While o < l
	
		If(Not ndx) Return split_ssi_getlen(raw, o)
		o = split_ssi_gotonext(raw, o)
		ndx = ndx - 1
	
	Wend 

End Function 



Function Split_Insert$(raw$, ndx, sub$)

	Return split_ss_getrange(raw, 0, ndx) + split_ssi_create(sub, 1) + split_ss_getrange(raw, ndx, -1)

End Function 



Function Split_Remove$(raw$, ndx)

	Return split_ss_getrange(raw, 0, ndx) + split_ss_getrange(raw, ndx + 1, -1)

End Function



Function Split_Set$(raw$, ndx, sub$)

	Return split_ss_getrange(raw, 0, ndx) + split_ssi_create(sub, 1) + split_ss_getrange(raw, ndx + 1, -1)

End Function


; ----------------------------------------------------------------------------------------------------

; - fi: first index
; - ct: sub string count	-1: return all the remaining substrings
; 							 0: returns nothing
;							>0: number of substrings to return
;
; - if the number of ss defined by ct is greater then the number of ss in raw, then getrange will
;   create however many blank ss is req to make sure the returned value has the stated number of ss
;
Function split_ss_getrange$(raw$, fi, ct)

	Local l = Len(raw)
	Local ssi = 1
	Local ssi2
	
	If(ct < 0)
	
		While fi
	
			ssi = split_ssi_gotonext(raw$, ssi)
			If(ssi => l) Return ""
			fi = fi - 1
	
		Wend 
	
		Return Mid(raw, ssi, l)
	
	Else If(ct > 0)

		While fi
				
			ssi = split_ssi_gotonext(raw$, ssi)		
			fi = fi - 1
			If(ssi => l) Exit
		
		Wend 
		
		If(fi) Return split_ssi_create("", ct)
		
		ssi2 = ssi
		
		While ct
		
			If(ssi2 => l) Exit
			ssi2 = split_ssi_gotonext(raw$, ssi2)
			ct = ct - 1		
		
		Wend 
		
		If(ct) Return Mid(raw, ssi, l) + split_ssi_create("", ct)
	
		Return Mid(raw, ssi, ssi2 - ssi)

	End If 
	
End Function


; ----------------------------------------------------------------------------------------------------
; ssi - substring index
;
; - the first substring index (ssi) = 1


Function split_ssi_create$(sub$, rpeat)
	
	Return String(Chr(Len(sub) And $00000ff) + Chr((Len(sub) And $000ff00) Shr 8) + Mid(sub, 1, Len(sub) And $ffff), rpeat)
	
End Function



Function split_ssi_gotonext(raw$, ssi)

	Return ssi + Asc(Mid(raw, ssi, 1)) + Asc(Mid(raw, ssi + 1, 1)) Shl 8 + 2

End Function 



Function split_ssi_getlen(raw$, ssi)

	Return Asc(Mid(raw, ssi, 1)) + Asc(Mid(raw, ssi + 1, 1)) Shl 8

End Function 



Function split_ssi_getoff(ssi)

	Return ssi + 2

End Function 



Function split_ssi_gettext$(raw$, ssi)

	Return Mid(raw, ssi + 2, Asc(Mid(raw, ssi, 1)) + Asc(Mid(raw, ssi + 1, 1)) Shl 8)

End Function
