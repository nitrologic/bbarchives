; ID: 21
; Author: marksibly
; Date: 2001-08-22 15:15:30
; Title: HTTPGet
; Description: Perform an HTTP Get

www=OpenTCPStream( "www.yahoo.com",80 )
If Not www RuntimeError "Failed to connect"
WriteLine www,"GET / HTTP/1.1"
WriteLine www,"Host: www.yahoo.com"
WriteLine www,"User-Agent: BlitzBrowser"
WriteLine www,"Accept: */*"
WriteLine www,""
While Not Eof(www)
    Print "RECV: " + ReadLine(www)
Wend
CloseTCPStream www

