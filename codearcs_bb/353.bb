; ID: 353
; Author: superqix
; Date: 2002-06-24 13:36:20
; Title: Delphi DLLs in B3D
; Description: Write DLLs for Blitz3D in Delphi

; -------------------------------------------
; TEST.BB is a 32-bit Windows dynamic-link
; library wrapper which provides access
; to a Windows 9X/NT/2000/XP DLL
; -------------------------------------------

Const dll$ = "test.dll"

; Peek/Poke String functions by Rob Hutchinson

Function PokeString(mBankAddr,sStringOut$,iBufferOffset = 0)
	For n = 1 To Len(sStringOut$)
		PokeByte mBankAddr,iBufferOffset,Asc(Mid$(sStringOut$,n,1))
		iBufferOffset = iBufferOffset + 1
	Next
	PokeByte mBankAddr,iBufferOffset,0 ; Null terminate
End Function

Function PeekString$(mBankAddr,iBufferOffset = 0)
	Local sOutStr$ = "",iByte
	For n = 0 To BankSize(mBankAddr)
		iByte = PeekByte(mBankAddr,iBufferOffset)
		If iByte <> 0 
			sOutStr$ = sOutStr$ + Chr(iByte)
		Else
			Exit
		EndIf
		iBufferOffset = iBufferOffset + 1
	Next

	Return sOutStr$
End Function

; Function call(s)

Function UCase$(s$) ; make a string upper case

	iBankSize=Len(s$)+1
    mBankIn = CreateBank(iBankSize)
	mBankOut = CreateBank(255) ; out bank always 255
	PokeString(mBankIn,s$)

	error = CallDLL(dll$,"DLLUcase",mBankIn,mBankOut)

	t$ = PeekString$(mBankOut) ; return string
	FreeBank mBankIn  	
	FreeBank mBankOut
	
	; get errors from Delphi DLL
	
	Select error
		Case 1
			RuntimeError "Error"
	End Select
	
	Return t$
	
End Function
