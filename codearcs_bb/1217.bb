; ID: 1217
; Author: MPZ
; Date: 2004-12-01 15:53:30
; Title: user and computername with winapi
; Description: give you the user and computername of you pc

; This Procedure is for free MPZ (@) from Berlin
; Version 0.1 12/2004
; 
; Write the following files in the blitz/userlibs 

; file with name "advapi32.decl" and the content:
; .lib "advapi32.dll"
; api_GetUserName% (lpBuffer*, nSize*) : "GetUserNameA"


; file with name "kernel32.decl" and the content:
; .lib "kernel32.dll"
; api_GetComputerName% (lpBuffer*, nSize*) : "GetComputerNameA"



; This program give you the user and the computername of your pc

Username_=CreateBank(1024)
Computername_=CreateBank(1024)

charcount_=CreateBank(4)
PokeInt charcount_,0,1024 ; 1024 character for the names (onlx for big big big networksystems...)




If api_GetUserName (Username_, charcount_)= 1 Then Print "Username = "+bank_in_string(Username_)
If api_GetComputerName (Computername_, charcount_)= 1 Then Print "Computername = "+bank_in_string(Computername_)

FreeBank Username
FreeBank Computername
FreeBank charcount

While Not KeyDown(1) 
Wend 
End


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
