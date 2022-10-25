; ID: 3122
; Author: munch
; Date: 2014-04-25 17:11:05
; Title: GetLocalIP function
; Description: Get pc's local (lan) ip, win/mac/linux

' Get pc's local (lan) ip, win/mac/linux

SuperStrict

Framework Brl.EventQueue
Import Brl.StandardIO
Import pub.freeprocess
Import brl.socket

' example
Local ip:String=GetLocalIP()

Print "host name:"+HostName(HostIp(ip)) ' get pc's local name from ip

Local ips:Int[] = HostIps(HostName(HostIp(ip))) ' get pc's local ip from name
For Local i:Int = EachIn ips
	If i=HostIp(ip) Then Print "host ip:"+DottedIP(i)
Next

Function GetLocalIP:String()
?Win32
	Return DottedIP(HostIp("",0))
?Not Win32
	Local proc:TProcess=TProcess.Create("ifconfig",0)
	Local ps:String,is:String,spi:Int,ssi:Int,sci:Int
	While proc.Status() ' get ifconfig results
		If proc.pipe.ReadAvail()
			ps=proc.pipe.ReadString(proc.pipe.ReadAvail())
		EndIf
	Wend
	While spi>-1 ' get pc's local ip
		spi=ps.Find("inet ",spi+1)
		For sci=spi+5 To spi+15
			If ps[sci..sci+1]>="0" And ps[sci..sci+1]<="9" Then Exit
		Next
		ssi=ps.Find(" ",sci)
		is=ps[sci..ssi]
		If is<>"127.0.0.1" And is<>"" Then Exit
	Wend
	Return is
?
End Function
