; ID: 2429
; Author: ZJP
; Date: 2009-03-06 22:39:50
; Title: What is my public IP?
; Description: How to get your public IP on the net

;**********************************
;* Get Your Public IP
;**********************************

Print "My public IP is "+WanIP$()
WaitKey

Function WanIP$()
Local www,x$,ip$
    www=OpenTCPStream( "www.whatismyip.org",80 )
    If Not www Return "127.0.0.1"
    WriteLine www,"GET / HTTP/1.1"
    WriteLine www,"User-Agent: Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1)"
    WriteLine www,"Accept: */*"
    WriteLine www,""
    x$= ReadLine(www)
    x$= ReadLine(www)
    x$= ReadLine(www)
    ip$= ReadLine(www)
    CloseTCPStream www
    Return ip$
End Function
