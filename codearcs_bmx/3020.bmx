; ID: 3020
; Author: BlitzSupport
; Date: 2013-02-01 16:41:44
; Title: Check if IPs are on same LAN
; Description: Checks if two IP addresses are on the same subnet

Function SameSubnet:Int (ip1:String, ip2:String, mask:String = "255.255.255.0")
	If HostIp (ip1) & HostIp (mask) = HostIp (ip2) & HostIp (mask) Then Return True
End Function

' D E M O . . .

If SameSubnet ("192.168.0.2", "192.168.0.3") ' "x.x.0.x" is on same subnet as "x.x.0.x"
	Print "Same subnet"
Else
	Print "Different subnet"
EndIf

If SameSubnet ("192.168.0.2", "192.168.1.3") ' "x.x.0.x" is on a different subnet to "x.x.1.x"
	Print "Same subnet"
Else
	Print "Different subnet"
EndIf

If SameSubnet ("10.1.0.1", "10.1.0.5") ' "x.1.x.x" is on same subnet as "x.1.x.x"
	Print "Same subnet"
Else
	Print "Different subnet"
EndIf
