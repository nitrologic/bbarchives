; ID: 1720
; Author: Mag
; Date: 2006-05-26 05:12:03
; Title: is online?
; Description: My simple way to check user online

If OnLine() Print "You're online" Else Print "You're not online"
WaitKey()
End

Function OnLine()
n = CountHostIPs("")
For k=1 To n
ip = HostIP(k)
ipaddress$ = DottedIP$(ip)
If ipaddress$="127.0.0.1"
	notconnect=1
EndIf
Next
Return Not(notconnect)
End Function
