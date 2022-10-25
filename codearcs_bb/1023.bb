; ID: 1023
; Author: skn3[ac]
; Date: 2004-05-10 11:22:38
; Title: HttpGet(server,path,port,proxy,proxyport)
; Description: HttpGet with proxy servers. Returns a handle to a tcp stream

;example of non proxy http get
tcp = HttpGet("www.blitzbasic.com","/Home/_index_.php")
If tcp = False RuntimeError "unable to connect to address"
While Eof(tcp) = False
	DebugLog ReadLine$(tcp)
Wend
CloseTCPStream(tcp)

;example of using a proxy server
tcp = HttpGet("www.blitzbasic.com","/Home/_index_.php",80,"planet1.scs.cs.nyu.edu",3128)
If tcp = False RuntimeError "unable to connect to address"
While Eof(tcp) = False
	DebugLog ReadLine$(tcp)
Wend
CloseTCPStream(tcp)


;function
Function HttpGet(server$,path$,port=80,proxy$="",proxyport=0)
	Local www
	If Len(proxy$) = 0 proxy$ = server$
	If proxyport = 0 proxyport = port
	www = OpenTCPStream(proxy$,proxyport)
	If www = False Return False
	WriteLine www,"GET http://" + server$ + ":" + port + path$ + " HTTP/1.1" + Chr$(13)+Chr$(10) + "Host: " + server$ + Chr$(13)+Chr$(10) + "User-Agent: blitzbasic" + Chr$(13)+Chr$(10) + "Accept: */*" + Chr$(13)+Chr$(10)
	Return www
End Function
