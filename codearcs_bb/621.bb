; ID: 621
; Author: BlitzSupport
; Date: 2003-03-15 13:57:09
; Title: BlitzServe
; Description: A crude multi-stream HTTP server...

; -----------------------------------------------------------------------------
; BlitzServe: a very crude HTTP server...
; -----------------------------------------------------------------------------
; james @ hi - toro . com
; -----------------------------------------------------------------------------

; Although this can accept unlimited connections, it'll only actually serve one
; file at a time because I can't launch the file-send part of the code as a
; separate thread, meaning the program flow is held up while a file is being
; sent. Also, for reasons unknown, sending a file to a browser on the same
; machine is VERY slow, though it's nice and quick over a network...

AppTitle "BlitzServe..."

; -----------------------------------------------------------------------------
; The place you'll put any files to be served. It's like an FTP directory...
; -----------------------------------------------------------------------------

folder$ = "C:\My Documents"

; Just stripping any "/" characters you or I might accidentally stick on the end ;)

If (Right (folder$, 1) = "/") Or (Right (folder$, 1) = "\")	
	folder$ = Left (folder$, Len (folder$) - 1)
EndIf

; -----------------------------------------------------------------------------
; To test, open Internet Explorer and type http://127.0.0.1/whatever/
; -----------------------------------------------------------------------------
; For example, if you have a file called hotpr0n.html in the above folder,
; you'd type 127.0.0.1/hotpr0n.html into your browser...
; -----------------------------------------------------------------------------

; (Note: 127.0.0.1 is always the local PC. If you're on a network, you can run
; IE on another PC and replace 127.0.0.1 with the actual IP address of the
; PC this server is running on.)

; -----------------------------------------------------------------------------
; Connection list type...
; -----------------------------------------------------------------------------

Type Connection
	Field number ; Stream identifier
	Field stream ; Stream handle
End Type

; -----------------------------------------------------------------------------
; Create HTTP server (always on port 80)...
; -----------------------------------------------------------------------------

server = CreateTCPServer (80)

If server

	Print
	Print "BlitzServe: awaiting incoming connections..."
	Print
	
	Repeat
	
		; -------------------------------------------------------------------------
		; See if there's been an incoming connection attempt...
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
			
			; ---------------------------------------------------------------------
			; A HTTP request end with a blank line, so we read until we get that...
			; ---------------------------------------------------------------------
	
			Repeat
			
				; -----------------------------------------------------------------
				; Read a line from an incoming HTTP request...
				; -----------------------------------------------------------------

				; The format of an incoming request line is:
				
				;		"Command" [space] "parameters"

				; Examples...
								
				;		"GET /thisfile.txt"
				;		"User-Agent: AcmeBrowse"
				
				incoming$ = ReadLine (c\stream)

;				DebugLog "Stream " + c\number + ": " + incoming$

				If incoming$ <> ""

					; -------------------------------------------------------------
					; Got a line? Let's parse! Split command and parameter(s)...
					; -------------------------------------------------------------

					eoc = Instr (incoming$, " ")				; End of command part of incoming$
					command$ = Lower (Left (incoming$, eoc))	; Command part of incoming$
					parameter$ = Mid (incoming$, eoc + 1)		; Parameter part of incoming$

				EndIf
					
				; -----------------------------------------------------------------
				; Let's see what command we've got...
				; -----------------------------------------------------------------

				Select command$
				
					Case "get "

						; ---------------------------------------------------------
						; Got a HTTP file request!
						; ---------------------------------------------------------

						; Format of GET is: "GET /thisfile.txt"
						
						eop = Instr (parameter$, " ")			; End of first parameter ("GET")
						file$ = Mid (parameter$, 1, eop - 1)	; First parameter ("GET")
						http$ = Mid (parameter$, eop + 1)		; Second parameter ("/thisfile.txt")

						; ---------------------------------------------------------
						; Requesting program's name/identifier...
						; ---------------------------------------------------------

					Case "user-agent: "
						program$ = Mid (incoming$, eoc + 1)
						
				End Select
				
			Until incoming$ = "" ; Got blank line after headers, all done here...

			; ---------------------------------------------------------------------
			; Lessee what we've got...
			; ---------------------------------------------------------------------
			
			Print "Requested file: " + file$
			Print "Requested by: " + program$
			Print "Requested HTTP version: " + http$

			; ---------------------------------------------------------------------
			; 'K, we barely know what we're doing as it is, so only HTTP 1.1...
			; ---------------------------------------------------------------------
			
			If http$ <> "HTTP/1.1"

				WriteLine c\stream, "HTTP/1.1 505 This server only accepts HTTP version 1.1"
				WriteLine c\stream, ""
				CloseTCPStream c\stream: Delete c

			Else
			
				; -----------------------------------------------------------------
				; It was a HTTP 1.1 request...
				; -----------------------------------------------------------------

				If file$ <> ""

					; Convert any %xx (hex) codes in URL to Chr (ascii) character...
					; (Eg. %20 is ascii 57, ie. Chr (57), ie. a Space.)
					
					file$ = UnHexURL (file$)

					Print
					Print "Attempting to send " + file$ + "..."
					Print
					
					If Left (file$, 1) <> "/"
						file$ = "/" + file$ ; Strip leading "/" if any...
					EndIf

					; -------------------------------------------------------------
					; Does the requested file exist in our 'site' folder?
					; -------------------------------------------------------------

					Select FileType (folder$ + file$)
					
						Case 0 ; File does not exist...
						
							WriteLine c\stream, "HTTP/1.1 404 Not found"
							WriteLine c\stream, ""
							
							; -----------------------------------------------------
							; Close the stream and free the Connection...
							; -----------------------------------------------------
							
							CloseTCPStream c\stream
							Delete c
							
						Case 1 ; File exists!
						
							Print "Writing file..."
							Print
							
							; -----------------------------------------------------
							; Send the file. This part sucks.
							; -----------------------------------------------------

							; It's slow and needs to be performed as a separate thread,
							; since it holds program flow up! We also don't reply with a
							; polite HTTP response...

							writing = ReadFile (folder$ + file$)
							If writing
								While Not Eof (writing)
									WriteByte c\stream, ReadByte (writing)
								Wend
								Print "Sent!"
								Print
								CloseFile writing
								WriteLine c\stream, ""
							
								; -------------------------------------------------
								; Close the stream and free the Connection...
								; -------------------------------------------------
								
								CloseTCPStream c\stream
								Delete c

							EndIf
							
						Case 2 ; Folder...
						
							WriteLine c\stream, "That's a folder, dummy..."
							WriteLine c\stream, ""
							
							; -----------------------------------------------------
							; Close the stream and free the Connection...
							; -----------------------------------------------------
							
							CloseTCPStream c\stream
							Delete c
	
					End Select
					
				EndIf

			EndIf
						
			count = count + 1 ; Just the Connection number parameter for the next Connection...
			
		EndIf
		
		; -------------------------------------------------------------------------
		; Don't wanna hog CPU...
		; -------------------------------------------------------------------------
	
		Delay 100
	
	Until KeyHit (1)
	
	; -----------------------------------------------------------------------------
	; Free any open TCP streams...
	; -----------------------------------------------------------------------------
	
	For c.Connection = Each Connection
		CloseTCPStream c\stream
		Delete c
	Next

	; -----------------------------------------------------------------------------
	; Freeze! FBI! We're shutting this server down!
	; -----------------------------------------------------------------------------
	
	CloseTCPServer server

Else

	Print "Couldn't create server! Press ENTER to exit..."
	Repeat: Delay 100: Until KeyHit (28)
	
EndIf
	
End

Function UnHexURL$ (url$)
	Repeat
		pos = Instr (url$, "%")
		If pos
			hexx$ = Mid (url$, pos, 3)
			url$ = Replace (url$, hexx$, Chr (HexToDec (hexx$)))
		EndIf
	Until pos = 0
	Return url$
End Function

; From PureBasic code by 'PB'...
; (h$ can be 0-FFFFFFF)

Function HexToDec (h$)
	If Left (h$, 1) = "%" Then h$ = Right (h$, Len (h$) - 1)
	h$ = Upper (h$)
	For r=1 To Len (h$)
		d = d Shl 4: a$ = Mid (h$, r, 1)
		If Asc (a$) > 60
			d = d + Asc (a$) - 55
		Else
			d = d + Asc (a$) - 48
		EndIf
	Next
	Return d
End Function
