; ID: 852
; Author: GC-Martijn
; Date: 2003-12-11 04:12:51
; Title: Domain Check
; Description: If you want a domain check , here it is :)

AppTitle "Domain Check"
Graphics 420,540,16,2
SetBuffer FrontBuffer() 

Print "Made by GC-Martijn 2003"
Print "extensies: nl,com,net,org,info,nu,de"
Print ""

While Not KeyHit(1)
	DomainCheck()
	WaitKey
	Cls
Wend

Function DomainCheck()

Color 255,255,0
domein$	= Input$("Domain:") 
Color 255,255,255

lenght 	= Len(domein$)

ext		= Instr(domein$,".",1) 
leng 	= lenght-ext

extensie$ 	= Right$(domein$, leng)

	If extensie$ = "com" Or extensie$ = "net" Then
		wh$		="whois.networksolutions.com"
	EndIf
	
	If extensie$ = "nl" Then
		wh$		="whois.domain-registry.nl"
	EndIf
	
	If extensie$ = "org" Or extensie$ = "info" Then
		wh$		="whois.networksolutions.com"
	EndIf

	If extensie$ = "nu" Then
		wh$		="whois.nic.nu"
	EndIf
	
	If extensie$ = "de" Then
		wh$		="whois.nic.de"
	EndIf		
	
www	= OpenTCPStream(wh$,43)

If Not www Then
	Print "Error in TCPStream"
Else
	WriteLine www,domein$
	While Not Eof(www)
    	Print ReadLine(www)
	Wend
	CloseTCPStream www
EndIf

End Function
