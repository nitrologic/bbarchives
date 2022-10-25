; ID: 219
; Author: Blu_Matt
; Date: 2002-02-02 14:43:17
; Title: hex2dec
; Description: Converts a hexadecimal number to a decimal one

Function hex2dec%(hex_number$)
; Converts the supplied hex number into a decimal number
; If hex_number isn't a valid hex number, then returns -1
; written by Matt Burns (Blu_Matt / matt@blis.co.uk)
	Local the_hex$=Upper(Trim(hex_number$))		; the hex number
	Local base_power%=0							; the current base power
	Local base%=16								; the base to convert from
	Local the_dec%=0							; the decimal representation
	Local chars%=0
	Local hex_length%=Len(the_hex$)				; current length of the hex
	If Left(the_hex$,1)="$" Then 				; removes any leading "$"
		the_hex$=Right(the_hex$,hex_length%-1)
	EndIf
	hex_length%=Len(the_hex$)					; current length of the hex
	Local chars_left%=hex_length%				; current number of chars left
	Local hex_left$=the_hex$					; current hex left
	Repeat
		current_hex$=Right(hex_left$,1) 		; gets the current hex char
		If ((Asc(current_hex$)>=Asc("0")) And (Asc(current_hex$)<=Asc("9"))) Then
			hex_dec%=current_hex$				; digit 0-9
		ElseIf ((Asc(current_hex$)>=Asc("A")) And (Asc(current_hex$)<=Asc("F"))) Then
			hex_dec%=Asc(current_hex$)-55		; char A-F
		Else
			Return -1 							; found an illegal character, aborting...
		EndIf
		the_dec%=the_dec%+((base%^base_power%)*hex_dec%)	; add the local hex digit to the total
		base_power%=base_power%+1				; increase the base power
		chars_left%=chars_left%-1				; reduce the number of chars left
		hex_left$=Left(hex_left$,chars_left%)	; sets the remainder
	Until chars_left%=0
	Return the_dec%								; return the result
End Function

; testing, 1, 2, 3...
Print hex2dec("$ff") ; = 255
Print hex2dec("100") ; = 256
