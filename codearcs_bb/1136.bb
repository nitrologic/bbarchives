; ID: 1136
; Author: gman
; Date: 2004-08-19 06:38:07
; Title: registry access functions
; Description: functions that allow reading/writing to the registry

; registry roots
Const HKEY_CLASSES_ROOT			= -2147483648
Const HKEY_CURRENT_USER			= -2147483647
Const HKEY_LOCAL_MACHINE		= -2147483646
Const HKEY_USERS			= -2147483645

; return value constants
Const ERROR_SUCCESS 			= 0
Const ERROR_EOF				= 259  	; no more entries in key

; data types For keys
Const REG_SZ				= 1	; Data String
Const REG_BINARY			= 3	; Binary Data in any form.
Const REG_DWORD				= 4 	; A 32-bit number.

; global var holding last error #
Global reg_lasterr			= ERROR_SUCCESS

; enumerates the keys contained in the passed subkey and returns them as a delimited string in 
; the format: KEY=VALUE|KEY=VALUE|KEY=VALUE
Function reg_enumvalues$(RegKey%,SubKey$,delim$="|",types=False)
	Local cRetVal$="",key$="",val$=""
	Local keybank=CreateBank(100),keybanksize=CreateBank(4),valbank=CreateBank(100),valbanksize=CreateBank(4),typebank=CreateBank(4)
	Local char=0,nIdx=0,nType=0

	; open the key 
	Local hKey=reg_openkey(RegKey,SubKey$)
	If hKey<>-1	
	
		; read in the values
		Repeat
			; init the banks
			PokeInt(typebank,0,0)
			PokeInt(valbanksize,0,100)
			PokeInt(keybanksize,0,100)
		
			; clear out the temp values
			key$=""
			val$=""
			
			If RegEnumValue(hKey,nIdx,keybank,keybanksize,0,typebank,valbank,valbanksize)<>ERROR_EOF
				nType=PeekInt(typebank,0)
				
				; tack on the delimiter
				If cRetVal$<>""
					cRetVal$=cRetVal$+delim$
				EndIf 

				; build the key name
				For char=0 To PeekInt(keybanksize,0)-1
					If PeekByte(keybank,char)=0 Then Exit
					key$=key$+Chr(PeekByte(keybank,char))
				Next

				Select nType
					; read in a string or binary value
					Case REG_SZ, REG_BINARY
						; build the value
						For char=0 To PeekInt(valbanksize,0)-1
							If PeekByte(valbank,char)=0 Then Exit
							val$=val$+Chr(PeekByte(valbank,char))
						Next
					; read in an integer
					Case REG_DWORD
						val$=PeekInt(valbank,0)						
				End Select

				If types
					cRetVal$=(cRetVal$+PeekInt(typebank,0)+";"+key$+"="+val$)
				Else
					cRetVal$=(cRetVal$+key$+"="+val$)
				EndIf
			Else
				Exit
			EndIf			
			
			nIdx=nIdx+1
		Forever
		reg_closekey(hKey)
	EndIf
	
	FreeBank typebank
	FreeBank valbank
	FreeBank valbanksize
	FreeBank keybank
	FreeBank keybanksize
	
	Return cRetVal$
End Function

; enumerates the keys contained in the passed subkey and returns them as a delimited string in 
; the format: KEY|KEY|KEY
Function reg_enumkeys$(RegKey%,SubKey$,delim$="|")
	Local cRetVal$=""
	Local keybank=CreateBank(100)
	Local nIdx=0

	; open the key first	
	Local hKey=reg_openkey(RegKey,SubKey$)
	If hKey<>-1	
		Repeat			
			If RegEnumKey(hKey,nIdx,keybank,BankSize(keybank))<>ERROR_EOF			
				; tack on the delimiter
				If cRetVal$<>""
					cRetVal$=cRetVal$+delim$
				EndIf 
				
				For char=0 To BankSize(keybank)-1
					If PeekByte(keybank,char)=0 Then Exit
					cRetVal$=cRetVal$+Chr(PeekByte(keybank,char))
				Next
			Else
				Exit
			EndIf
			
			nIdx=nIdx+1
		Forever
		reg_closekey(hKey)
	EndIf

	FreeBank keybank
	Return cRetVal$
End Function

; deletes a value from the registry.  returns True/False.
Function reg_deletevalue%(RegKey%,SubKey$,ValueName$)
	Local hKey=reg_openkey(RegKey,SubKey$)
	Local lRetVal=False
	If hKey<>-1
		Local nRslt=RegDeleteValue(hKey,ValueName$)		
		If (nRslt=ERROR_SUCCESS)
			lRetVal=True
		Else
			reg_lasterr=nRslt
		EndIf
		reg_closekey(hKey)
	EndIf
	Return lRetVal
End Function

; gets a value from the registry and returns it as a string.  will return the passed default
; if the value requested is not found in the registry.
Function reg_getvalue$(RegKey%,SubKey$,ValueName$,Dflt$="",types=False)
	Local cRetVal$=Dflt$
	Local hKey=reg_openkey(RegKey,SubKey$)
	Local char=0,nType=0
	
	; open the key 
	If hKey<>-1
		Local valbank=CreateBank(100),valbanksize=CreateBank(4),typebank=CreateBank(4)
	
		; init the banks
		PokeInt(typebank,0,0)
		PokeInt(valbanksize,0,100)
	
		Local nRslt=RegQueryValueEx(hKey,ValueName$,0,typebank,valbank,valbanksize)
		If (nRslt=ERROR_SUCCESS)
			cRetVal$=""
		
			nType=PeekInt(typebank,0)
			
			; build the value
			Select nType
				; read in a string or binary value
				Case REG_SZ, REG_BINARY
					; build the value
					For char=0 To PeekInt(valbanksize,0)-1
						If PeekByte(valbank,char)=0 Then Exit
						cRetVal$=cRetVal$+Chr(PeekByte(valbank,char))
					Next
				; read in an integer
				Case REG_DWORD
					cRetVal$=PeekInt(valbank,0)						
			End Select
			
			; tack on the type if requested
			If types
				cRetVal$=nType+";"+cRetVal$
			EndIf
		Else
			reg_lasterr=nRslt
		EndIf
		reg_closekey(hKey)
	EndIf
	Return cRetVal$
End Function

; sets a value in the registry.  defaults to type string, but can pass REG_DWORD and REG_BINARY.
; returns True/False.
Function reg_setvalue%(RegKey%,SubKey$,ValueName$,Value$,nType=REG_SZ)
	Local hKey=reg_openkey(RegKey,SubKey$)
	Local lRetVal=False
	If hKey<>-1
		Local valbank
		
		; create a bank to hold the info
		Select nType
			Case REG_SZ, REG_BINARY
				valbank=CreateBank(Len(Value$))				
				For i=1 To Len(Value$)
					PokeByte(valbank,i-1,Asc(Mid(Value$,i,1)))
				Next				
			Case REG_DWORD
				valbank=CreateBank(4)
				PokeInt(valbank,0,Int(Value$))
		End Select
	
		Local nRslt=RegSetValueEx(hKey,ValueName$,0,nType,valbank,BankSize(valbank))
		If (nRslt=ERROR_SUCCESS)
			lRetVal=True
		Else
			reg_lasterr=nRslt
		EndIf
		reg_closekey(hKey)
	EndIf
	Return lRetVal
End Function

; deletes the passed key from the registry.  returns True/False.
Function reg_deletekey%(RegKey%,KeyName$)
	reg_lasterr=ERROR_SUCCESS
	Local nRslt=RegDeleteKey(RegKey,KeyName$)
	
	If nRslt<>ERROR_SUCCESS
		reg_lasterr=nRslt
	EndIf
	
	Return (nRslt=ERROR_SUCCESS)
End Function

; returns the registry handle or -1 if failed.
Function reg_createkey%(RegKey%,KeyName$)
	reg_lasterr=ERROR_SUCCESS
	Local regbank=CreateBank(4)
	Local hKey=-1

	Local nRslt=RegCreateKey(RegKey%,KeyName$,regbank)

	If (nRslt=ERROR_SUCCESS)
		hKey=PeekInt(regbank,0)
	Else
		reg_lasterr=nRslt
	EndIf

	FreeBank regbank	
	Return hKey
End Function 

; returns the registry handle or -1 if failed
Function reg_openkey%(RegKey%,KeyName$)
	reg_lasterr=ERROR_SUCCESS
	Local regbank=CreateBank(4)
	Local hKey=-1

	Local nRslt=RegOpenKey(RegKey%,KeyName$,regbank)

	If (nRslt=ERROR_SUCCESS)
		hKey=PeekInt(regbank,0)
	Else
		reg_lasterr=nRslt
	EndIf

	FreeBank regbank
	Return hKey
End Function 

; closes the registry key.  returns True/False.
Function reg_closekey%(RegKey%)
	reg_lasterr=ERROR_SUCCESS
	Local nRslt=RegCloseKey(RegKey)
	
	If nRslt<>ERROR_SUCCESS
		reg_lasterr=nRslt
	EndIf
	
	Return (nRslt=ERROR_SUCCESS)
End Function

; returns true if the key exists
Function reg_iskey%(RegKey%,KeyName$)
	Local hKey=reg_openkey(RegKey,KeyName$)
	Local lRetVal=False

	If hKey<>-1
		reg_closekey(hKey)
		lRetVal=True
	EndIf
	
	Return lRetVal
End Function
