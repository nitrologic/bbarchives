; ID: 929
; Author: MPZ
; Date: 2004-02-11 04:47:36
; Title: ColorPicker with WinApi
; Description: ColorPicker for Blitz3D in WindowsModus

; This Procedure is for free MPZ (@) from Berlin
; Version 0.1 1/2004
; 
; in the USERLIBS must be the file kernel32.decls
;.lib "kernel32.dll"
;api_RtlMoveMemory(Destination*,Source,Length) : "RtlMoveMemory"

; in the USERLIBS must be the file comdlg32.decls
;.lib "comdlg32.dll"
;api_ChooseColor% (pChoosecolor*) : "ChooseColorA"



Cls
Graphics 640,480,0,2

Print ChooseColor() 

While MouseHit(1) <> 1
Wend

End

;--------------------------You can use it as BlitzLIB

Function ChooseColor()

CC_ANYCOLOR = $100 
CC_FULLOPEN = $2   
CC_RGBINIT  = $1   

nextOffset%=0 
theBank=CreateBank(36)

lStructSize=36
PokeInt theBank,nextOffset%,lStructSize
nextOffset%=nextOffset%+4 
	
hwndOwner=0
PokeInt theBank,nextOffset%,hwndOwner
nextOffset%=nextOffset%+4 

hInstance=0
PokeInt theBank,nextOffset%,hInstance
nextOffset%=nextOffset%+4 
		
rgbResult=0
PokeInt theBank,nextOffset%,rgbResult
nextOffset%=nextOffset%+4 

lpCustColors_ = CreateBank(64) 
PokeInt theBank,nextOffset%,AddressOf(lpCustColors_)
nextOffset%=nextOffset%+4 

flags=CC_ANYCOLOR Or CC_FULLOPEN Or CC_RGBINIT
PokeInt theBank,nextOffset%,flags
nextOffset%=nextOffset%+4 

lCustData=0 	
PokeInt theBank,nextOffset%,lCustData
nextOffset%=nextOffset%+4 
	
lpfnHook=0 	
PokeInt theBank,nextOffset%,lpfnHook
nextOffset%=nextOffset%+4 

lpTemplateName$=""+Chr$(0)
lpTemplateName_ = CreateBank(Len(lpTemplateName$)) 
string_in_bank(lpTemplateName$,lpTemplateName_)
PokeInt theBank,nextOffset%,AddressOf(lpTemplateName_)
nextOffset%=nextOffset%+4 
			
If api_ChooseColor%(thebank) Then
	Return PeekInt (thebank,16) 
Else 
	Return 0
EndIf 	
FreeBank theBank
FreeBank lpTemplateName_
End Function

Function AddressOf(Bank) ; Find the correct Adress of a Bank (for C *Pointer)
	Local Address = CreateBank(4) 
	api_RtlMoveMemory(Address,Bank+4,4) 
	Return PeekInt(Address,0) 
End Function

Function string_in_bank(s$,bankhandle) ; Put a String in a Bank
	Local pos=1
	Local pos2=0
	Repeat
		PokeByte(bankhandle,pos2,Asc(Mid(s$,pos,Len(s$))))
		pos=pos+1
		pos2=pos2+1
	Until pos=Len(s$)+1
End Function

Function bank_in_string$(bankhandle) ; Get a String from a Bank
	Local s$=""
	Local pos=0
	Repeat
		s$=s$+Chr(PeekByte(bankhandle,pos))
		pos=pos+1
	Until pos=BankSize(bankhandle)
	s$=Replace$(s$,Chr(0)," ")
	Return s$
End Function
