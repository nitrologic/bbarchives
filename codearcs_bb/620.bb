; ID: 620
; Author: BlitzSupport
; Date: 2003-03-15 12:02:56
; Title: Basic multiple-connection server
; Description: How to handle multiple incoming streams...

; -----------------------------------------------------------------------------
; How to handle multiple server connections...
; -----------------------------------------------------------------------------

; This example just waits for a connection and prints the request header your
; browser sends to it...

; To test, open Internet Explorer and type http://127.0.0.1/whatever

; 127.0.0.1 is always the local PC. If you're on a network, you can run
; IE on another PC and replace 127.0.0.1 with the actual IP address of the
; PC this server is running on... :)

; -----------------------------------------------------------------------------
; Connection list type...
; -----------------------------------------------------------------------------

Type Connection
	Field number ; Stream identifier
	Field stream ; Stream handle
End Type

; -----------------------------------------------------------------------------
; Create server... HTTP (port 80) is just for example!
; -----------------------------------------------------------------------------

server = CreateTCPServer (80)

If server

	Print
	Print "Server created. Awaiting incoming connections..."
	Print
	
	Repeat
	
		; -------------------------------------------------------------------------
		; See if there's an incoming connection attempt...
		; -------------------------------------------------------------------------
	
		tstream = AcceptTCPStream (server)
	
		If tstream
	
			; ---------------------------------------------------------------------
			; Got one? OK, create a new Connection entry...
			; ---------------------------------------------------------------------
	
			Print
			Print "Got new connection..."
			Print
			
			c.Connection = New Connection
			c\number = count
			c\stream = tstream
			
			count = count + 1
			
		EndIf
		
		; -------------------------------------------------------------------------
		; Don't wanna hog CPU...
		; -------------------------------------------------------------------------
	
		Delay 100
	
		; -------------------------------------------------------------------------
		; Read any incoming data from the open connections...
		; -------------------------------------------------------------------------
		
		; In this case, when you type 127.0.0.1/filename into your browser, the
		; browser will send several lines forming a HTTP file request, followed
		; by one blank line. This code will print each line as it arrives...
		
		For c.Connection = Each Connection
			If c\stream
				While ReadAvail (c\stream)
					Print "Stream " + c\number + ": " + ReadLine (c\stream)
				Wend
			EndIf
		Next
		
	Until KeyHit (1)
	
	; -----------------------------------------------------------------------------
	; Free all open TCP streams...
	; -----------------------------------------------------------------------------
	
	For c.Connection = Each Connection
		CloseTCPStream c\stream
		Delete c
	Next

	CloseTCPServer server
	
EndIf
	
End
