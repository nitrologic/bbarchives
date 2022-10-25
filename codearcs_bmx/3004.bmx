; ID: 3004
; Author: BlitzSupport
; Date: 2012-11-24 08:57:32
; Title: Get PC's local IP address
; Description: Returns a string containing your PC's local (LAN) IP address

Function LocalIP:String ()
	Return DottedIP (HostIp ("", 0))
End Function

Print LocalIP ()
