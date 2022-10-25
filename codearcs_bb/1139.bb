; ID: 1139
; Author: gman
; Date: 2004-08-20 07:32:34
; Title: INI access functions
; Description: functions that allow reading/writing to INI files

; writes a value to an INI file
Function ini_write(Section$,Key$,Value$,File$) 
	IniWrite(Section$, Key$, Value$, File$) 
End Function

; reads a value from an INI file
Function ini_read$(Section$,Key$,File$,Dflt$="",nReadSize=100) 
	Local bank,nRet=0,cRetVal$=Dflt$,nCnt=0,char=0
	
	bank=CreateBank(nReadSize)
	nRet=IniRead(Section$,Key$,Dflt$,bank,BankSize(bank),File$)
	
	; make sure we have something
	If nRet>0
		cRetVal$=""
		
		; loop thru the characters returned
		For nCnt=0 To nRet-1
			; get the next character
			char=PeekByte(bank,nCnt)
			; check if at string end
			If char=0 Then Exit
			; add the character to the return value
			cRetVal$=cRetVal$+Chr(char)
		Next
	EndIf
	
	FreeBank bank
	
	Return cRetVal$
End Function

; reads in all the value names from a section in an INI file and returns a delimited string list
; can optionally return values with the names
Function ini_enumvalues$(Section$,File$,lValues=False,ValDflt$="",delim$="|",nReadSize=2000) 
	Local bank,nRet=0,nCnt=0,char=0,temp$="",cRetVal$=""
	
	bank=CreateBank(nReadSize)
	nRet=IniEnumValues(Section$,0,"",bank,BankSize(bank),File$)
	
	; make sure we have something
	If nRet>0
		; loop thru the characters returned
		For nCnt=0 To nRet-1
			; get the next char
			char=PeekByte(bank,nCnt)
			
			; char 0 means we are at the end of the current value
			If char=0
				; we are done with the current value name, now get the value and add both to the
				; final result
				cRetVal$=cRetVal$+temp$
				
				; get the value if needed
				If lValues
					cRetVal$=cRetVal$+"="+ini_read(Section$,temp$,File$,ValDflt$)
				EndIf
				
				; if this is not the last name/value pair, add on the delimiter
				If nCnt<(nRet-1)
					cRetVal$=cRetVal$+delim$
				EndIf
				
				; clear out the temp to make way for the next value
				temp$=""
			Else 
				; add in the current character
				temp$=temp$+Chr(char)
			EndIf
		Next
	EndIf
		
	FreeBank bank
	
	Return cRetVal$
End Function

; reads in all the sections from an INI file and returns a delimited string list
Function ini_enumsections$(File$,delim$="|",nReadSize=2000) 
	Local bank,nRet=0,nCnt=0,char=0,temp$="",cRetVal$=""
	
	bank=CreateBank(nReadSize)
	nRet=IniEnumSections(0,0,"",bank,BankSize(bank),File$)
	
	; make sure we have something
	If nRet>0	
		; loop thru the characters returned
		For nCnt=0 To nRet-1
			; get the next char
			char=PeekByte(bank,nCnt)
			
			; char 0 means we are at the end of the current section name
			If char=0
				; add the current section to the final result
				cRetVal$=cRetVal$+temp$				

				; if this is not the last name, add on the delimiter
				If nCnt<(nRet-1)
					cRetVal$=cRetVal$+delim$
				EndIf
				
				; clear out the temp to make way for the next section name
				temp$=""
			Else 
				; add in the current character
				temp$=temp$+Chr(char)
			EndIf
		Next
	EndIf
		
	FreeBank bank
	
	Return cRetVal$
End Function
