; ID: 139
; Author: Skully
; Date: 2002-01-24 21:39:27
; Title: IPManager
; Description: Gets your IP, converts padded IP's into integer versions

; Get the IP on this machine
ips=CountHostIPs("")
If Not ips End ; No countee, no IP

IP=HostIP(1)
DotIP$=DottedIP(ip)

Type octet
	Field value
End Type
Dim octets(3)

Print "Source Integer IP:"+IP
Print "Sourse Padded  IP:"+DotIP$
Print
Print "=---Converting---="
Print

If ParseIP(DotIP)
	count=3
	For o.octet=Each octet
		octets(count)=o\value
		Print "Octet "+count+" ="+octets(count)
		count=count-1
	Next
EndIf

byte4=octets(3) Shl 24
byte3=octets(2) Shl 16
byte2=octets(1) Shl 8
byte1=octets(0)
ip2=byte4 Or byte3 Or byte2 Or byte1

Print "Converted:" + DotIP +" back to :"+ ip2

WaitKey()
End

Function parseip(ip$)
	If Len(ip$)>6
		ip$=ip$
		lastperiod=1
		For t=1 To 4							; four octets
			period=Instr(ip,".",lastperiod)		; Separtated by 3 periods
			If period=0
				If t=4							; if its the last octet then we dont expect to find a period
					period=Len(ip)+1			; stay within bounds
				Else	
					Return False
				EndIf
			EndIf
		
			If period>lastperiod
				octet=Mid$(ip,lastperiod,period-lastperiod)
				If octet>=0 And octet<=255
					o.octet=New octet
					o\value=octet
				Else
					Return False
				EndIf
			Else
				Return False
			EndIf
			lastperiod=period+1
		Next
		octet=0
		Return True
	Else
		Return False
	EndIf
End Function
