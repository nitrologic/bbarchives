; ID: 569
; Author: skn3[ac]
; Date: 2003-02-03 00:08:15
; Title: Use any form of Address with UDP communication
; Description: Allows you to connect using UDP, using various types of Network Address's. EG localhost/xxx.xxx.xxx.xxx/www.address.com

;How To use
;Client gets the servers IP by calling

;MyIp = GetIp("www.address.com",[port])
;this can also be used ...
;MyIp = GetIp("localhost",[port])
;or this...
;MyIp = GetIp("xxx.xxx.xxx.xxx",[port])

;the Function will return 0 if no ip could be resolved
;or an integer ip if it was!.

;IMPORTANT
;The server must open a TCP server whilst listening for connections.
;The TCP server must be on the same port as the UDP server (logical)

;What happens is, if the address is identified as a name address. Then 
;an attempt to connect to a TCP server on the port is made.
;If the TCP server exsists, it will extract the IP, and instantly shut down (client)
;the tcp connection without sending or recieving any data.

;EXAMPLE FOR HOST -------------------------------
Global MyUdpPort=101
Global NameIpBounce=CreateTCPServer(MyUdpPort)
;------------------------------------------------


;Here are the functions that do the business for the client

Function GetIp(Address$,Port=0)
	;Check for character type
	Found=False
	For I=1 To Len(Address$)
		GetAsc=Asc(Mid$(Address$,I,1))
		If GetAsc < 48 Or GetAsc > 57 
			If GetAsc <> 46 Then
				Found=True
				Exit
			End If
		End If
	Next
	If Found=False Then
		Return GetIpFromDotted(Address$)
	Else
		Return GetIpFromName(Address$,Port)
	End If
End Function

Function GetIpFromDotted(inputip$)
	break1 = Instr(inputIP$,".") : break2 = Instr(inputIP$,".",break1+1) : break3 = Instr(inputIP$,".",break2+1)
	add1 = Mid(inputIP$,1,break1-1):add2 = Mid(inputIP$,break1+1,break2-1):add3 = Mid(inputIP$,break2+1,break3-1):add4 = Mid(inputIP$,break3+1)
	ipreturn=(add1 Shl 24) + (add2 Shl 16) + (add3 Shl 8) + add4
	Return ipreturn	
End Function

Function GetIpFromName(Name$,Port)
	OpenStream=OpenTCPStream(Name$,Port)
	If OpenStream=0 Then Return 0
	RealAddress=TCPStreamIP(OpenStream)
	CloseTCPStream(OpenStream)
	Return RealAddress
End Function
