; ID: 2337
; Author: schilcote
; Date: 2008-10-17 13:33:38
; Title: IP scanner
; Description: Finds all computers on the network (eventually)

firstthree$=Input$("What is the IP? (-the last section): ")
port=Input("What port to scan on?: ")

For t=0 To 255 

ip$=firstthree$+t

stream=OpenTCPStream(ip$,port)

If stream Then
DebugLog "Found a computer: "+ip$ 
Print ip$
Else
DebugLog "Nothing found on "+ip$
EndIf

Next

WaitKey
End
