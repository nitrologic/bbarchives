; ID: 3060
; Author: _PJ_
; Date: 2013-06-22 13:29:23
; Title: Improved Registry Functions (AdvApi32.dll)
; Description: Allowing Read/Write/Modify & Delete of Registry

;The following DECLS are required in "..\userlibs\" directory
;.lib "advapi32.dll"
;AdvApi32_RegOpenKey%(hKey%, subKey$, result*):"RegOpenKeyA"
;AdvApi32_RegCloseKey%(hKey%):"RegCloseKey"
;AdvApi32_RegQueryValueEx%(hKey%, lpValueName$, lpReserved%, lpType*, lpData*, lpcbData*):"RegQueryValueExA"
;AdvApi32_RegOpenKeyEx%(hKeyParent%,SubKey$,reserved,samdesired,phkResult*):"RegOpenKeyExA"  
;AdvApi32_RegFlushKey%(hKey%):"RegFlushKey" 
;AdvApi32_RegCreateKey%(hKeyParent%,SubKey$,phkResult*):"RegCreateKeyA" 
;AdvApi32_RegDeleteKey%(hKeyParent%,SubKey$):"RegDeleteKeyA" 
;AdvApi32_RegSetValueEx%(hKey%,ValueName$,Reserved%,nType%,Bytes*,size%):"RegSetValueExA" 
;AdvApi32_RegDeleteValue%(hKey%,ValueName$):"RegDeleteValueA" 
;AdvApi32_RegEnumKey%(hKey%,idx%,Key*,size%):"RegEnumKeyA" 
;AdvApi32_RegEnumValue%(hKey%,idx%,ValueName*,NameSize*,Reserved%,nType*,ValueBytes*,ValueSize*):"RegEnumValueA"

; registry roots
Const cn4_REG_HKCR%							= -2147483648	;Classes Root
Const cn4_REG_HKCU%							= -2147483647	;Current User
Const cn4_REG_HKLM%							= -2147483646	;Local Machine
Const cn4_REG_HKU%							= -2147483645	;Users
Const cn4_REG_HKPD%							= -2147483644	;Performance Data
Const cn4_REG_HKCC%							= -2147483643	;Current Config
Const cn4_REG_HKDD%							= -2147483642	;Dynamic Data
Const cn4_REG_HKLS%							= -2147483641	;Current User Local Settings
Const cn4_REG_HKPT%							= -2147483568	;Performance Text
Const cn4_REG_HKPN%							= -2147483552	;Performance NLS Text

; return value constants
Const cm_REG_ERROR_SUCCESS%					= 0
Const cm_REG_ERROR_EXPANDED%					= 2
Const cm_REG_ERROR_MORE_DATA%				= 234
Const cm_REG_ERROR_EOF%						= 259  	; no more entries in key

Const cm_REG_INVALID_KEY%						=-1

; data types For keys
Const cm_REG_NONE%							= 0	; Always as Binary
Const cm_REG_SZ%								= 1	; Data String
Const cm_REG_EXPAND_SZ%						= 2	; Compressed String value
Const cm_REG_BINARY%							= 3	; 0x1 0x2 0x3 0x4 Binary in Any Form
Const cm_REG_DWORD%							= 4 ; 0x04030201
Const cm_REG_DWORD_LITTLE_ENDIAN%				= 4 ; 0x04030201 A 32-bit number (Can also be a 4-byte REG_BINARY) = Specifically, Little-Endian
Const cm_REG_DWORD_BIG_ENDIAN%				= 5 ; 0x01020304 A 32-bit number (Can also be a 4-byte REG_BINARY) = Specifically, Big-Endian
;Const cm_REG_LINK%							= 6 ; UNSUPPORTED!
Const cm_REG_MULTI_SZ%						= 7 ; UTF-16 Not supported
Const cm_REG_RESOURCE_LIST%					= 8 ; UNSUPPORTED!
Const cm_REG_FULL_RESOURCE_DESCRIPTOR% 		= 9 ; As Series of 4-Byte Valuyes expressed as String with | separator
;Const cm_REG_RESOURCE_REQUIREMENTS_LIST%		= 10; UNSUPPORTED!
Const cm_REG_QWORD%							= 11; Double-Length (64-Bit) Integer expressed as String
Const cm_REG_QWORD_LITTLE_ENDIAN%				= 11; Double-Length (64-Bit) Integer expressed as String

; global var holding last error #
Global gm_REG_LAST_ERROR						= cm_REG_ERROR_SUCCESS

; Possibly useful constants
Const cs_REG_SOFTWARE_SUBKEY$					="SOFTWARE\"
Const cs_REG_HARDWARE_SUBKEY$					="HARDWARE\"
Const cs_REG_SECURITY_SUBKEY$					="SECURITY\"
Const cs_REG_SAM_SUBKEY$						="SAM\"
Const cs_REG_SYSTEM_SUBKEY$					="SYSTEM\"

Const cs_REG_DEFAULT_VALUE$					="(Default)"









Function s_REG_EnumValues$(nRegKey%,sSubKey$,sDelim$="|",ntypes=False); enumerates the keys contained in the passed subkey and returns them as a delimited string in the format: KEY=VALUE|KEY=VALUE|KEY=VALUE
	Local sRetVal$=""
	Local sKey$=""
	Local sVal$=""
	Local mKeybank=CreateBank(100)
	Local mKeybanksize=CreateBank(4)
	Local mValbank=CreateBank(100)
	Local mValbanksize=CreateBank(4)
	Local mTypebank=CreateBank(4)
	Local nChar=0
	Local nIdx=0
	Local nType=0
	
	; open the key 
	Local hKey=REG_OpenKey(nRegKey,sSubKey$)
	If (hKey<>cm_REG_INVALID_KEY)
		; read in the values
		Repeat
			; init the banks
			PokeInt(mTypebank,0,0)
			PokeInt(mValbanksize,0,100)
			PokeInt(mKeybanksize,0,100)
			
			; clear out the temp values
			sKey$=""
			sVal$=""
			
			If (AdvApi32_RegEnumValue(hKey,nIdx,mKeybank,mKeybanksize,0,mTypebank,mValbank,mValbanksize)<>cm_REG_ERROR_EOF)
				nType=PeekInt(mTypebank,0)
				
				; tack on the delimiter
				If (sVal$<>"")
					sVal$=sVal$+sDelim$
				EndIf 
				
				; build the key name
				For nChar=0 To PeekInt(mKeybanksize,0)-1
					If (Not (PeekByte(mValbank,nChar)))
						Exit
					End If 
					sKey$=sKey$+Chr(PeekByte(mKeybank,nChar))
				Next
				
				Select (nType)
					Case cm_REG_NONE:
					; Always as Binary
						For nChar=0 To PeekInt(mValbanksize,0)-1
							sVal$=sVal$+Chr(PeekByte(mValbank,nChar))
						Next
					Case cm_REG_SZ:
					; ascii bytes until 0
						For nChar=0 To PeekInt(mValbanksize,0)-1
							If (Not	(PeekByte(mValbank,nChar)))
								Exit
							End If
							sVal$=sRetVal$+Chr(PeekByte(mValbank,nChar))
						Next
					Case cm_REG_EXPAND_SZ:
					;We can retrieve the value if not the EXPANDED path
						For nChar=0 To PeekInt(mValbanksize,0)-1
							If (Not(PeekByte(mValbank,nChar)))
								Exit
							End If
							sVal$=sVal$+Chr(PeekByte(mValbank,nChar))
						Next	
						sVal=REG_ExpandValue(sRetVal)
					Case cm_REG_BINARY:
					; byte by byte
						For nChar=0 To PeekInt(mValbanksize,0)-1
						;do not stop at chr(0)
							sVal$=sVal$+Chr(PeekByte(mValbank,nChar))
						Next
					Case cm_REG_DWORD:
					; read in an integer
						sVal$=Str(PeekInt(mValbank,0))
					Case cm_REG_DWORD_LITTLE_ENDIAN:
					; read in an integer
						sVal$=Str(PeekInt(mValbank,0))	
					Case cm_REG_DWORD_BIG_ENDIAN:
					; read in an integer
						Local n3=PeekByte(mValbank,0)
						Local n2=PeekByte(mValbank,1)
						Local n1=PeekByte(mValbank,2)
						Local n0=PeekByte(mValbank,3)
						sRetVal=Str(((n3 Shl 24)+(n2 Shl 16)+(n1 Shl 8)+n0))
					Case cm_REG_MULTI_SZ:
					; build the value (ascii bytes)
						For nChar = 0 To PeekInt(mValbanksize, 0) - 1
						;Do not stop at Chr(0)
							sVal = sVal + Chr(PeekByte(mValbank,nChar))
						Next
					Case cm_REG_QWORD:
						For nChar=0 To PeekInt(mValbanksize,0)-1
						;do not stop at chr(0)
							sVal$=sVal$+Chr(PeekByte(mValbank,nChar))
						Next
					Case cm_REG_QWORD_LITTLE_ENDIAN:
						For nChar=0 To PeekInt(mValbanksize,0)-1
						;do not stop at chr(0)
							sVal$=sVal$+Chr(PeekByte(mValbank,nChar))
						Next
					Case cm_REG_FULL_RESOURCE_DESCRIPTOR:
					; Int by Int
						For nChar=0 To (PeekInt(mValbanksize,0)-1) Step 4
						;do not stop at chr(0)
							sVal$=sRetVal$+Chr(PeekInt(mValbank,nChar))+"|"
						Next
						If (Right(sVal,1)="|")
							sVal=Left(sVal,Len(sVal)-1)
						End If
				End Select
				
				If (ntypes)
					sRetVal$=(sRetVal$+PeekInt(mTypebank,0)+";"+sKey$+"="+sVal$)
				Else
					sRetVal$=(sRetVal$+sKey$+"="+sVal$)
				EndIf
			Else
				Exit
			EndIf			
			
			nIdx=nIdx+1
		Forever
		REG_CloseKey(hKey)
	EndIf
	
	FreeBank mTypebank
	FreeBank mValbank
	FreeBank mValbanksize
	FreeBank mKeybank
	FreeBank mKeybanksize
	
	Return sRetVal$
End Function

Function s_REG_EnumKeys$(nRegKey%,sSubKey$,sDelim$="|"); enumerates the keys contained in the passed subkey and returns them as a delimited string in the format: KEY|KEY|KEY
	Local sRetVal$=""
	Local mKeybank=CreateBank(100)
	Local nIdx=0
	Local bExitLoop%=False
	Local nChar
	
	; open the key first	
	Local hKey=REG_OpenKey(nRegKey,sSubKey$)
	If (hKey<>cm_REG_INVALID_KEY)
		While (Not(bExitLoop%))			
			If (AdvApi32_RegEnumKey(hKey,nIdx,mKeybank,BankSize(mKeybank))<>cm_REG_ERROR_EOF)
				; tack on the delimiter
				If (sRetVal$<>"")
					sRetVal$=sRetVal$+sDelim$
				EndIf 
				
				For nChar=0 To BankSize(mKeybank)-1
					If (Not(PeekByte(mKeybank,nChar))) 
						bExitLoop%=True
						Exit
					End If
					sRetVal$=sRetVal$+Chr(PeekByte(mKeybank,nChar))
				Next
			Else
				bExitLoop=True
				Exit
			EndIf
			
			nIdx=nIdx+1
		Wend
		REG_CloseKey(hKey)
	EndIf
	
	FreeBank mKeybank
	Return sRetVal$
End Function

Function nb_REG_DeleteValue%(nRegKey%,SubKey$,ValueName$); deletes a value from the registry.  returns True/False.
	Local hKey=REG_OpenKey(nRegKey,SubKey$)
	Local bRetVal=False
	If (hKey<>cm_REG_INVALID_KEY)
		Local nRslt=AdvApi32_RegDeleteValue(hKey,ValueName$)		
		If (nRslt=cm_REG_ERROR_SUCCESS)
			bRetVal=True
		Else
			gm_REG_LAST_ERROR=nRslt
		EndIf
		REG_CloseKey(hKey)
	EndIf
	Return bRetVal
End Function

Function REG_GetValue$(nRegKey%,sSubKey$,sValueName$,sDflt$="",nTypes=False); gets a value from the registry and returns it as a string.  will return the passed default if the value requested is not found in the registry.
	Local sRetVal$=sDflt$
	Local hKey=REG_OpenKey(nRegKey,sSubKey$)
	Local nChar=0
	Local nType=0
	
	; open the key 
	If (hKey<>cm_REG_INVALID_KEY)
		Local mValbank=CreateBank(255)
		Local mValBankSize=CreateBank(4)
		Local mTypeBank=CreateBank(4)
		
		; init the banks
		PokeInt(mTypeBank,0,0)
		PokeInt(mValBankSize,0,0)
		
		Local nRslt=AdvApi32_RegQueryValueEx(hKey,sValueName$,0,mTypeBank,mValbank,mValBankSize)
		If (nRslt=cm_REG_ERROR_MORE_DATA)
			ResizeBank mValbank,PeekInt(mValBankSize,0)
			nRslt=AdvApi32_RegQueryValueEx(hKey,sValueName$,0,mTypeBank,mValbank,mValBankSize)
		End If
		If (nRslt=cm_REG_ERROR_SUCCESS)
			sRetVal$=""
			
			nType=PeekInt(mTypeBank,0)
			
			; build the value
			Select (nType)
				Case cm_REG_NONE:
					; Always as Binary
					For nChar=0 To PeekInt(mValBankSize,0)-1 Step 2
						;do not stop at chr(0)
						sRetVal$=sRetVal$+Upper(Right(Hex((PeekByte(mValbank,nChar)) And 255),2))
					Next
				Case cm_REG_SZ:
					; ascii bytes until 0
					For nChar=0 To PeekInt(mValBankSize,0)-1
						If (Not	(PeekByte(mValbank,nChar)))
							Exit
						End If
						sRetVal$=sRetVal$+Chr(PeekByte(mValbank,nChar))
					Next
				Case cm_REG_EXPAND_SZ:
					;We can retrieve the value if not the EXPANDED path
					For nChar=0 To PeekInt(mValBankSize,0)-1
						If (Not(PeekByte(mValbank,nChar)))
							Exit
						End If
						sRetVal$=sRetVal$+Chr(PeekByte(mValbank,nChar))
					Next	
					sRetVal=REG_ExpandValue(sRetVal)
				Case cm_REG_BINARY:
					; byte by byte
					
					For nChar=0 To PeekInt(mValBankSize,0)-1; Step 2
						;do not stop at chr(0)
						sRetVal$=sRetVal$+Chr(PeekByte(mValbank,nChar))
					Next
					
				Case cm_REG_DWORD:
					; read in an integer
					sRetVal$=Str(PeekInt(mValbank,0))
				Case cm_REG_DWORD_LITTLE_ENDIAN:
					; read in an integer
					sRetVal$=Str(PeekInt(mValbank,0))	
				Case cm_REG_DWORD_BIG_ENDIAN:
					; read in an integer
					Local n3=PeekByte(mValbank,0)
					Local n2=PeekByte(mValbank,1)
					Local n1=PeekByte(mValbank,2)
					Local n0=PeekByte(mValbank,3)
					sRetVal=Str(((n3 Shl 24)+(n2 Shl 16)+(n1 Shl 8)+n0))
				Case cm_REG_MULTI_SZ:
					; build the value (ascii bytes)
					For nChar = 0 To PeekInt(mValBankSize, 0) - 1
						;Do not stop at Chr(0)
						sRetVal = sRetVal + Chr(PeekByte(mValbank,nChar))
					Next
				Case cm_REG_QWORD:
					For nChar=0 To PeekInt(mValBankSize,0)-1
						;do not stop at chr(0)
						sRetVal$=sRetVal$+Chr(PeekByte(mValbank,nChar))
					Next
				Case cm_REG_QWORD_LITTLE_ENDIAN:
					For nChar=0 To PeekInt(mValBankSize,0)-1
						;do not stop at chr(0)
						sRetVal$=sRetVal$+Chr(PeekByte(mValbank,nChar))
					Next
				Case cm_REG_FULL_RESOURCE_DESCRIPTOR:
					; Int by Int
					For nChar=0 To (PeekInt(mValBankSize,0)-1) Step 4
						;do not stop at chr(0)
						sRetVal$=sRetVal$+Chr(PeekInt(mValbank,nChar))+"|"
					Next
					If (Right(sRetVal,1)="|")
						sRetVal=Left(sRetVal,Len(sRetVal)-1)
					End If
			End Select
			
			; tack on the type if requested
			If (nTypes)
				sRetVal$=Str(nType)+";"+sRetVal$
			EndIf
		Else
			gm_REG_LAST_ERROR=nRslt
		EndIf
		REG_CloseKey(hKey)
	EndIf
	Return sRetVal$
End Function

Function REG_SetValue%(nRegKey%,sSubKey$,sValueName$,sValue$,nType=cm_REG_SZ); sets a value in the registry.  defaults to type string, but can pass REG_DWORD and REG_BINARY. returns True/False.
	Local hKey=REG_OpenKey(nRegKey,sSubKey$)
	Local bRetVal=False
	If (hKey<>cm_REG_INVALID_KEY)
		Local mValbank
		Local i
		; create a bank to hold the info
		Select nType
			Case cm_REG_NONE:
				;Typically 0-Length
				mValbank=CreateBank(Len(sValue$))
				If (sValue<>"")
					For i=1 To Len(sValue$)
						PokeByte(mValbank,i-1,Asc(Mid(sValue$,i,1)))
					Next
				End If
			Case cm_REG_SZ:
				mValbank=CreateBank(Len(sValue$))
				For i=1 To Len(sValue$)
					PokeByte(mValbank,i-1,Asc(Mid(sValue$,i,1)))
				Next
			Case cm_REG_BINARY:
				mValbank=CreateBank(Len(sValue$))
				For i=1 To Len(sValue$)
					PokeByte(mValbank,i-1,Asc(Mid(sValue$,i,1)))
				Next			
			Case cm_REG_DWORD:
				mValbank=CreateBank(4)
				PokeInt(mValbank,0,Int(sValue$))
			Case cm_REG_DWORD_LITTLE_ENDIAN:
				mValbank=CreateBank(4)
				For i= 3 To 0 Step -1
					PokeByte(mValbank,i,Asc(Mid(sValue$,3-i,1)))
				Next
			Case cm_REG_DWORD_BIG_ENDIAN:
				mValbank=CreateBank(4)
				PokeInt(mValbank,0,Int(sValue$))	
			Case cm_REG_EXPAND_SZ:
				mValbank=CreateBank(Len(sValue$))
				For i=1 To Len(sValue$)
					PokeByte(mValbank,i-1,Asc(Mid(sValue$,i,1)))
				Next	
			Case cm_REG_MULTI_SZ:
				mValbank=CreateBank(Len(sValue$))
				For i=1 To Len(sValue$)
					PokeByte(mValbank,i-1,Asc(Mid(sValue$,i,1)))
				Next	
			Case cm_REG_QWORD:
				mValbank=CreateBank(Len(sValue$))
				For i=1 To Len(sValue$)
					PokeByte(mValbank,i-1,Asc(Mid(sValue$,i,1)))
				Next		
			Case cm_REG_QWORD_LITTLE_ENDIAN:
				mValbank=CreateBank(Len(sValue$))
				For i=1 To Len(sValue$)
					PokeByte(mValbank,i-1,Asc(Mid(sValue$,i,1)))
				Next
			Case cm_REG_FULL_RESOURCE_DESCRIPTOR: 
				; Int by Int
				If (Right(sValue,1)="|")
					sValue=Left(sValue,Len(sValue)-1)
				End If
				mValbank=CreateBank(Len(sValue$))
				For i=1 To Len(sValue) Step 4
						;do not stop at chr(0)
					PokeInt mValbank,i,Int(Mid(sValue,i,4))
				Next
		End Select
		
		Local nRslt=AdvApi32_RegSetValueEx(hKey,sValueName$,0,nType,mValbank,BankSize(mValbank))
		If (nRslt=cm_REG_ERROR_SUCCESS)
			bRetVal=True
		Else
			gm_REG_LAST_ERROR=nRslt
		EndIf
		REG_CloseKey(hKey)
	EndIf
	Return bRetVal
End Function

Function REG_DeleteKey%(nRegKey%,sKeyName$); deletes the passed key from the registry.  returns True/False.
	gm_REG_LAST_ERROR=cm_REG_ERROR_SUCCESS
	Local nRslt=AdvApi32_RegDeleteKey(nRegKey,sKeyName$)
	
	If (nRslt<>cm_REG_ERROR_SUCCESS)
		gm_REG_LAST_ERROR=nRslt
	Else
		Local sSubKeys$ = s_REG_EnumKeys(nRegKey%, sKeyName$)
		While (Len(sSubKeys$))
			If (Instr(sSubKeys$,Chr(13)))
				REG_DeleteKey(nRegKey%, sKeyName$+"\"+Left(sSubKeys$,Instr(sSubKeys$,Chr(13))-1))
				sSubKeys$ = Mid(sSubKeys$,Instr(sSubKeys$,Chr(13))+1)
			Else
				REG_DeleteKey(nRegKey%, sKeyName$+"\"+sSubKeys$)
				Exit
			End If
		Wend
	End If
	Return (nRslt=cm_REG_ERROR_SUCCESS)
End Function

Function REG_CreateKey%(nRegKey%,sKeyName$); returns the registry handle or -1 if failed.
	gm_REG_LAST_ERROR=cm_REG_ERROR_SUCCESS
	Local mRegbank=CreateBank(4)
	Local hKey=cm_REG_INVALID_KEY
	
	Local nRslt=AdvApi32_RegCreateKey(nRegKey%,sKeyName$,mRegbank)
	
	If (nRslt=cm_REG_ERROR_SUCCESS)
		hKey=PeekInt(mRegbank,0)
	Else
		gm_REG_LAST_ERROR=nRslt
	EndIf
	
	FreeBank mRegbank
	Return hKey
End Function 

Function REG_OpenKey%(nRegKey%,sKeyName$); returns the registry handle or -1 if failed
	Local mRegbank=CreateBank(4)
	Local hKey=cm_REG_INVALID_KEY
	
	Local nRslt=AdvApi32_RegOpenKey(nRegKey%,sKeyName$,mRegbank)
	
	If (nRslt=cm_REG_ERROR_SUCCESS)
		hKey=PeekInt(mRegbank,0)
	Else
		gm_REG_LAST_ERROR=nRslt
	EndIf
	
	FreeBank mRegbank
	Return hKey
End Function 

Function REG_CloseKey%(hRegKey%); closes the registry key.  returns True/False.
	Local nRslt=AdvApi32_RegCloseKey(hRegKey)
	
	If (gm_REG_LAST_ERROR=cm_REG_ERROR_SUCCESS)
		If (nRslt<>cm_REG_ERROR_SUCCESS)
			gm_REG_LAST_ERROR=nRslt
			Return False
		End If	
	EndIf
	
	Return True
End Function

Function REG_IsKey%(nRegKey%,sKeyName$); returns True if the key exists
	Local hKey=REG_OpenKey(nRegKey,sKeyName$)
	Local bRetVal=False
	
	If (hKey<>cm_REG_INVALID_KEY)
		REG_CloseKey(hKey)
		bRetVal=True
	EndIf
	
	Return bRetVal
End Function

Function REG_ExpandValue$(sValue$)
	;Remove FilePath Quote marks
	If (Left(sValue,1)=Chr(34))
		Local Close=Instr(sValue,Chr(34),2)
		If (Not(Close))
			sValue=Right(sValue,Len(sValue)-1)
		Else
			Local sLine$=Right(sValue,Len(sValue)-Close)
			sValue=Replace(Left(sValue,Close),Chr(34),"")+sLine
		End If
	End If
	
	If (Instr(sValue,"\\"))
		;NOT network paths
		If (Left(sValue,2)<>"\\")
			;NOT URL Paths
			If (Not(Instr(sValue,":\\")))
				sValue=Replace(sValue,"\\","\")
			End If
		End If
	Else
		sValue=Replace(sValue,"/","\")
	End If
	
	If (Left(sValue,1)="@")
		;Ignore MountPoint derivations
		sValue=Replace(sValue,"@","")
	End If
	
	;Obtain expanded Environment Vars or System Props
	If (Left(sValue,1)="%")
		Local EndEnv=Instr(sValue,"%",2)
		Local EnvName$=Upper(Mid(sValue,2,EndEnv-2))
		Local Env$=GetEnv(EnvName)
		If (Env="")
			Env=SystemProperty(EnvName)
		End If
		If (Env<>"")
			sValue=Env+Right(sValue,Len(sValue)-EndEnv)
		End If
	End If
	
	Return sValue
End Function
