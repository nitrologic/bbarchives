; ID: 2772
; Author: Malice
; Date: 2010-09-27 10:54:36
; Title: Read/Write PureText Strings
; Description: Converts strings to pure alphanumerics to read/write

;0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz.,

;------------------------------------------------------------------------------------------------
;EXAMPLE:
;------------------------------------------------------------------------------------------------
f=WriteFile("test.txt")

WritePureTextString(f,"Hello World",True)

CloseFile f

f=ReadFile("test.txt")

Print ReadPureTextString(f)

CloseFile f


;------------------------------------------------------------------------------------------------
;FUNCTIONS:
;------------------------------------------------------------------------------------------------


Function ReadPureTextString$(FileStream)
	If (Not(Filestream)) Then Return ""
	
	Local Length%=ReadShort(FileStream)
	If (Not (Length)) Then Return ""
	
	Local sString$=""
	Local IterChar%
	Local Char$
	Local Byte%
	For IterChar= 1 To Length
		Byte=ReadByte(Filestream)
		If (Byte>64) Then Byte=Byte-128
		Char=ConvertByteToChar(Byte)
		sString=sString+Char
	Next
	
	sString=Trim(sString)
	While Instr(sString,"  ")
		sString=Replace(sString,"  "," ")
	Wend
	
	Return sString
End Function

Function WritePureTextString(Filestream%,sString$,Encrypt=False)
	If (Not(Filestream)) Then Return
	
	Local Length=Len(sString)

	; Zero Length string	
	If (Not (Length))
		WriteShort Filestream,0
		Return
	End If
	
	Local IterChar%
	Local Char$
	Local Byte%
		
	;Convert string
	For IterChar=1 To Length
		Char=Mid(sString,IterChar,1)
		Byte=ConvertCharToByte(Char)
		Replace(sString,Chr(Char),ConvertByteToChar(Char))
	Next

	;Consecutive spaces count as 1. No leading or trailing spaces.
	sString=Trim(sString)
	While Instr(sString,"  ")
		sString=Replace(sString,"  "," ")
	Wend

	Length=Len(sString)

	; Zero Length string	
	If (Not (Length))
		WriteShort Filestream,0
		Return
	End If
	
	;Truncate long strings
	If (Length>65535)
		Length=65535
		sString=Left(sString,65535)
	End If

	;Consecutive spaces count as 1. No leading or trailing spaces.
	sString=Trim(sString)
	While Instr(sString,"  ")
		sString=Replace(sString,"  "," ")
	Wend

	Length%=Len(sString)

	;Write Length as short
	WriteShort FileStream,Length
			
	For IterChar=1 To Length
		Char=Mid(sString,IterChar,1)
		Byte=ConvertCharToByte(Char)
		If (Encrypt)
			SeedRnd MilliSecs()*RndSeed()
			If Rand(0,1) Then Byte=Byte+128
		End If
		WriteByte Filestream,Byte
	Next
End Function
	

Function ConvertCharToByte%(Char$)

	Local Ascii%=Asc(Char)

	If ((Ascii>47) And (Ascii<58))
		; NUMERAL
		Return (Ascii-48)
	Else

		If ((Ascii>64) And (Ascii<91))
			; UPPER ALPHA
			Return (Ascii-54)
	
		Else
	
			If (Ascii>96) And (Ascii<123)
				; LOWER ALPHA
			Return (Ascii-61)
		
			End If
		End If
	End If	
	
	If Char="." Then Return 62
	If Char="," Then Return 63
	
	; For everything else, there's mastercard
	Return 64
		
End Function

Function ConvertByteToChar$(Byte)

	If (Byte<10)
		;NUMERAL
		Return Chr(Byte+48)
	
	Else
		If ((Byte>9) And (Byte<37))
			; UPPER ALPHA
			Return Chr(Byte+54)
		Else
			If ((Byte>36) And (Byte<62))
				; LOWER ALPHA
				Return Chr(Byte+61)
		
			End If
		End If
	End If	
	
	If Byte=62 Then Return "."
	If Byte=63 Then Return ","	
	Return " "
End Function
