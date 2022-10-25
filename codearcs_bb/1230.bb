; ID: 1230
; Author: skn3[ac]
; Date: 2004-12-12 03:40:05
; Title: Get integer IP from any hostname EG
; Description: Allows you to retrieve the proper ip from a given hostname. Useful for udp games wanting to turn named address into usable ip address!!

;userlib    dnsip.decls
;-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
;requires the following userlib
;.lib " "
;GetIpByHost%(name$)
;
;.lib "Ws2_32.dll"
;Dns_GetHostByName%(name$):"gethostbyname"
;Dns_WSAGetLastError%():"WSAGetLastError"
;
;.lib "kernel32.dll"
;Dns_GetPointer%(a*,b%,c%):"MulDiv"
;Dns_MoveMemory(Destination%,Source%,Length%):"RtlMoveMemory"
;-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=

;bb include    dnsip.bb
;-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
Function GetIpByHost(name$)
	;Function written by Jonathan Pittock 2004
	Local hostent,char,pointer,ip,flag,tempasc
	
	;check for numerical ip
	flag = False
	For pointer = 1 To Len(name$)
		tempasc = Asc(Mid$(name$,pointer,1))
		If tempasc <> 46 And tempasc <> 48 And tempasc <> 49 And tempasc <> 50 And tempasc <> 51 And tempasc <> 52 And tempasc <> 53 And tempasc <> 54 And tempasc <> 55 And tempasc <> 56 And tempasc <> 57
			flag = True
			Exit
		End If
	Next
	
	If flag = False
		off1=Instr(name$,".")
		ip1=Left$(name$,off1-1)
		off2=Instr(name$,".",off1+1)
		ip2=Mid$(name$,off1+1,off2-off1-1)
		off3=Instr(name$,".",off2+1)
		ip3=Mid$(name$,off2+1,off3-off2-1)
		off4=Instr(name$," ",off3+1)
		ip4=Mid$(name$,off3+1,off4-off3-1)
		Return ip1 Shl 24 + ip2 Shl 16 + ip3 Shl 8 + ip4
	Else
		pointer = Dns_GetHostByName(name$)
		Select Dns_WSAGetLastError()
			Case 11001,11002,11003,11004 : Return 0
		End Select
		hostent = CreateBank(20)
		char = CreateBank(4)
		Dns_MoveMemory(Dns_GetPointer(hostent,1,1),pointer,20)
		Dns_MoveMemory(Dns_GetPointer(char,1,1),PeekInt(hostent,12),4)
		Dns_MoveMemory(Dns_GetPointer(char,1,1),PeekInt(char,0),4)
		ip = PeekInt(char,0)
		FreeBank(hostent)
		FreeBank(char)
		Return (((ip And $000000ff) Shr 0) Shl 24) Or (((ip And $0000ff00) Shr 8) Shl 16) Or (((ip And $00ff0000) Shr 16) Shl 8) Or (((ip And $ff000000) Shr 24) Shl 0)
	End If
End Function
;-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=

;Example of usage
;-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
;include "dnsip.bb"
RuntimeError "www.google.com = "+DottedIP(GetIpByHost("www.google.com")) + Chr$(13)+Chr$(10) + "localhost = " + DottedIP(GetIpByHost("localhost"))
;-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
