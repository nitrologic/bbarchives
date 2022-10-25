; ID: 358
; Author: GrahamK
; Date: 2002-07-01 16:22:30
; Title: FloatTo4Bytes
; Description: Convert floating point number to 32bit IEEE representation

; convert a floating point to it's ieee representation (ie. same a poking into a bank)
; works using binary strings, not the best way, but ok for now.
Function FloatTo4Byte$(v#)
	; special case, if asking for representation of 0
	If v=0 Then Return String$(Chr$(0),4)
	
	; set the sign
	If v< 0 Then sign$="1" Else sign$="0"
	
	; get the part before the point
	v1#=Floor(Abs(v))
	
	; and after
	v2#=(Abs(v)-Abs(v1))
	
	; first convert the integer part to binary (using strings for now)
	o$=Bin$(v1)
	While Left$(o$,1)="0" And Len(o$)>0:o$=Mid$(o$,2):Wend	; trim off leading 0's
	
	; create binary string equivelent of right hand side of decimal point (no more that 23 bits)
	o2$=""
	While v2<> 0 And Len(o2$)<23 
		v2=v2*2
		If v2>=1 Then 
			o2$=o2$+"1"
			v2=v2-1
		Else
			o2$=o2$+"0"
		End If
	Wend
	
	; work out exponent portion
	If Floor(Abs(v))>0 Then 
		; positive exponent
		e=Len(o$)-1
	Else
		; negative exponent
		e=Instr(o2$,"1")
		o2$=Mid$(o2$,e)
		e = -e
	End If

	o$=Mid$(o$+o2$,2) ; create binary string represention for mantissa
	While Len(o$)<23:o$=o$+"0":Wend ; pad out to 23 bits

	; create the rest of the binary string by including sign and exponent (now 32 bits)	
	o$=sign$+Right$(Bin(e+127),8)+o$
	
	; create byte return string
	ret$=""
	For i=1 To 4
			ret$=Chr$(bin2int(Mid$(o$,(i-1)*8+1,8)))+ret$
	Next

	Return ret$

End Function

; binary string to integer helper function
Function bin2Int(b$)
	v#=0
	For i = 1 To Len(b$)	
		v=v*2.0+(Mid$(b$,i,1)="1")
	Next
	Return v
End Function
