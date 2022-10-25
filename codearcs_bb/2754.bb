; ID: 2754
; Author: Malice
; Date: 2010-08-17 17:25:03
; Title: More String Functons
; Description: A Few FUnctions related to Strings

Function MatchCase$(MatchString$,TextString$)
	If (Upper(MatchString$)=MatchString$) Then Return Upper(TextString$)
	If (Lower(MatchString$)=MatchString) Then Return Lower(TextString$)
	If MatchString$=(ProperString$(MatchString$)) Then TextString$=ProperString$(TextString$)
	Return TextString$
End Function

Function PaddedNumberString$(Value%,Digits%)
	Local Padding$=""
	Local IterByte
	For IterByte=1 To Digits
		Padding$=Padding$+"0"
	Next
	Local ReturnString$=Padding$+Str(Value%)
	Return Right$(ReturnString$,Digits%)
End Function

Function ReadByteString$(file%, nCount% = False)
	Local sReturnString$ = ""
	Local Char%
	Local IterByte%
	If (Not(nCount%))
		Char% = ReadByte(file)
		While (Char%)
			sReturnString$ = sReturnString$ + Chr(Char)
			Char% = ReadByte(file)
		Wend
	Else
		For IterByte% = 1 To nCount%
			sReturnString$ = sReturnString$ + Chr(ReadByte(file))
		Next
	EndIf
	Return sReturnString$
End Function

Function Parenthesise$(TextString$)
	If ((Left(TextString,1)="("))
		While (Left(TextString,1)="(")
			TextString=TrimStringLeft(TextString,1)
		Wend	
	End If
	If ((Right(TextString,1)="("))
		While (Right(TextString,1)=")")
			TextString=TrimStringRight$(TextString,1)
		Wend	
	End If
	Return "("+TextString$+")"
End Function

Function EnQuote$(TextString$)
	If ((Left(TextString,1)=Chr(34)))
		While (Left(TextString,1)=Chr(34))
			TextString=TrimStringLeft(TextString,1)
		Wend	
	End If
	If ((Right(TextString,1)=Chr(34)))
		While (Right(TextString,1)=Chr(34))
			TextString=TrimStringRight(TextString,1)
		Wend	
	End If
	Return Chr(34)+TextString$+Chr(34)
End Function

Function TrimStringLeft$(sString$,nChars%)
	If (sString$="") Then Return ""
	If (Not(nChars%)) Then Return sString$
	Local nLength%=Len(sString$)
	If (nLength%<=nChars%) Then Return ""
	Return (Right$(sString$,nLength%-nChars%))
End Function

Function TrimStringRight$(sString$,nChars%)
	If (sString$="") Then Return ""
	If (Not(nChars)) Then Return sString$
	Local nLength%=Len(sString$)
	If (nLength<=nChars) Then Return ""
	Return (Left$(sString$,nLength%-nChars))
End Function

Function TrimMidString$(sString$,nFrom%,nChars%)
	If (sString$="")Then Return ""
	If ((nFrom%)<2) Then nFrom%=1
	Local nLength%=Len(sString$)
	If (nLength%<nFrom)Then Return sString$
	If (nLength%<(nFrom+nChars)) Then nChars%=(nLength%-nFrom%)
	If (Not (nChars%)) Then Return sString$
	Local nTo%=nChars%+nFrom%
	If (nTo%>nLength%) Then nTo=nLength%
	Local sReturn$=Right(sString$,nLength%-nTo%)
	If (nFrom%>1) Then sReturn$=Left(sString$,nFrom%-1)+sReturn$
	Return sReturn$
End Function

Function ProperString$(sString$)
	If (sString="") Then Return ""
	Local nLength%=Len(sString)
	If nLength=1 Then Return Upper(sString)
	 
	Local sIterate$, sAdd$
	Local nChar%
	Local Mode%=False
	sIterate=Upper$(Left(sString,1))
	For nChar=2 To nLength
		sAdd$=Mid(sString,nChar,1)
		If (Mode)
			sAdd=Upper(sAdd)
			If (IsValidAlphanumericCharacter(sAdd))
				Mode=(False)
			End If
		Else
			sAdd=Lower(sAdd)
			If (sAdd="." Or sAdd=Chr(34) Or sAdd="!" Or sAdd="&" Or sAdd="*" Or sAdd="?")
				Mode=True
			Else
				If (nChar<nLength)
					If ((sAdd="i") Or (sAdd="o"))
						If (IsValidAlphanumericCharacter(Mid(sString,nChar+1,1))=False) And (IsValidAlphanumericCharacter(Mid(sString,nChar-1,1))=False) Then sAdd=Upper(sAdd)
					End If
				End If
			End If							
		End If
		sIterate=sIterate+sAdd
	Next
	Return sIterate$
End Function

Function PurifyString$(sString$,nAllowNumbers%=True,nAllowSpace%=True)
	If (Len(sString)<1) Then Return ""
	Local nIterChar%
	Local sChar$
	Local sReturn$
	If (Not(nAllowSpace)) Then sString$=Replace(sString," ","")
		For nIterChar=1 To Len(sString$)
			sAdd$=Mid(sString,nIterChar,1)
			If (Not(IsAlphaCharacter(sAdd)	))
				If (IsNumericCharacter(sAdd)|
					If (nAllowNumbers))
						sReturn$=sReturn$+sAdd$
					End If
				End If
			Else
				sReturn$=sReturn$+sAdd$
			End If
		Next		
	End If					
	Local sReturn$
End Function

Function IsAlphanumericCharacter%(sChar$)
	Return	(	IsNumericChar(Left(sChar,1)) + IsAlphaCharacter(Left(sChar,1))	)
End Function

Function IsNumericChar%(sChar$)
	If (sChar="") Then Return False
	Return	(	(Left(sChar,1)>="0")	*	(Left(sChar,1)<="9")	)	
End Function

Function IsAlphaCharacter%(sChar$)
	If (sChar="") Then Return False
	Return	(	(	(Upper(Left(sChar,1))>="A")*(Upper(Left(sChar,1))<="Z")	))
End Function
