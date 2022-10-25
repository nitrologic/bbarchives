; ID: 2026
; Author: Xzider
; Date: 2007-06-01 20:38:18
; Title: Port Scanner
; Description: Scans Open/Closed Ports

Port% = Input("Port to start checking with - ")

Print "Port starting at - " + Port% + " - "
Print "Press any key to start"

WaitKey

While Not KeyHit(1)

stream% = CreateTCPServer(Port%)

 If stream%

		Print "Port - " + Port% + " - open"
		
  Else

		Print "Port - " + Port% + " - was closed"
		Print "Press any key to continue"
		WaitKey
		
  End If

		Port% = Port% + 1
Wend
