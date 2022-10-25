; ID: 2482
; Author: BlitzSupport
; Date: 2009-05-19 15:05:30
; Title: Multithreaded web server
; Description: A simple multithreaded web server

' IMPORTANT: Make sure "Threaded Build" is enabled in the Program -> Build Options menu!

' -----------------------------------------------------------------------------
' BlitzServe 2 -- a very crude multithreaded HTTP server...
' -----------------------------------------------------------------------------

SuperStrict

' -----------------------------------------------------------------------------
' Set this to a folder on your hard drive containing the files to be served:
' -----------------------------------------------------------------------------

Global folder:String = "C:\Temp\"

' The value below is derived from Win32 -> GetSystemInfo (info:SYSTEM_INFO) ->
' info.dwAllocationGranularity, the 'ideal' size for optimum read/write speeds
' in Windows. Later version might report bigger numbers, but this will be fine:

Const OPTIMUM_IO:Int = 65536

' -----------------------------------------------------------------------------
' To test...
' -----------------------------------------------------------------------------

' 1) Run the program and launch a web browser;

' 2) In the browser's address bar, type 127.0.0.1 plus the file name, eg.

'		http://127.0.0.1/index.html
'		http://127.0.0.1/h0tchix.jpg

'	Don't type the folder name!

' 3) If your firewall complains, you need to unblock/allow this program!

' 4) Repeatedly hit F5/Refresh in your browser to see the server handle
'    cut-off requests (you'll see ERROR: xyz messages).

' -----------------------------------------------------------------------------

' -----------------------------------------------------------------------------
' OK...
' -----------------------------------------------------------------------------

AppTitle = "BlitzServe 2..."

' -----------------------------------------------------------------------------
' Remove trailing slash from server's folder (HTTP GET command prefixes file
' name with a forward slash)...
' -----------------------------------------------------------------------------

folder = Replace (folder, "/", "\")

If (Right (folder, 1) = "\")'"/") Or (Right (folder, 1) = "\")	
	folder = Left (folder, Len (folder) - 1)
EndIf

If FileType (folder) = 0
	RuntimeError "Set folder:String to a folder on your computer!"
EndIf

' -----------------------------------------------------------------------------
' Print queue for threads -- multiple threads running at the same time will
' cause corrupted output for Print, so queue within the thread and print all
' the thread's messages when ready...
' -----------------------------------------------------------------------------

Type PrintList

	Field list:TList = CreateList ()

	Method Add (info:String)
		ListAddLast list, info
	End Method

	Method PrintAll ()
		For Local i:String = EachIn list
			Print i
			ListRemove list, i
		Next
	End Method

End Type

' -----------------------------------------------------------------------------
' Each connection has a socket and an associated stream to read from...
' -----------------------------------------------------------------------------

Type Connection
	Field socket:TSocket
	Field stream:TSocketStream
End Type

' -----------------------------------------------------------------------------
' Thread list and mutex for safe access...
' -----------------------------------------------------------------------------

Global ThreadListMutex:TMutex = CreateMutex ()
Global ThreadList:TList = CreateList ()

' -----------------------------------------------------------------------------
' Temporary graphics window...
' -----------------------------------------------------------------------------

' This is just to allow keyboard input to be captured. Don't press ESC on
' the IDE output, as that just terminates the program. Press ESC with the
' graphics window highlighted so the program can close all connections. It won't
' cause any harm if you don't -- this is just for completeness' sake...

' Note that in real life, this would just be running in an infinite loop
' so there would be no need to test for ESC!

Graphics 320, 200

' -----------------------------------------------------------------------------
' Just a local pointer
' -----------------------------------------------------------------------------

Local thread:TThread

Local threads:Int = 0

' -----------------------------------------------------------------------------
' Create HTTP server (always on port 80)...
' -----------------------------------------------------------------------------

Local server:TSocket = CreateTCPSocket ()

If server

	' Bind to port 80...
	
	If BindSocket (server, 80)

		' Start listening for incoming connections on port 80...
		
		' NOTE: Don't use default 0 for backlog parameter, as this will cause
		' web pages to fail occasionally. The web browser will be requesting
		' all the files in any HTML page you request, and a 0-sized backlog
		' won't process the requests quickly enough. If the backlog isn't
		' cleared between SocketListen and the call to SocketAccept, the
		' request will fail, meaning images, etc, fail to display.
		
		' 5 is an old Windows standard, but there is lots of disagreement
		' as to what it should be, and different use scenarios. A server
		' expecting to be Slashdotted will require a much larger amount,
		' while a lowly desktop serving local files like this won't need
		' as much...
		
		' More information here:
		
		' http://tangentsoft.net/wskfaq/advanced.html#backlog
		' http://stackoverflow.com/questions/114874/socket-listen-backlog-parameter-how-to-determine-this-value
		' http://patchwork.kernel.org/patch/2297/
		
		SocketListen server, 5
		
		Print
		Print "BlitzServe 2: awaiting incoming connections..."
		Print
		Print "Launch your web browser and direct it to http://127.0.0.1/myfilename.html"
		Print "where myfilename.html is a file inside the folder you specified..."
		Print ""
		
		Repeat
		
			' -------------------------------------------------------------------------
			' See if there's been an incoming connection attempt...
			' -------------------------------------------------------------------------
		
			Local remote:TSocket = SocketAccept (server)

			If remote

				thread = CreateThread (ProcessConnection, remote)
				ListAddLast ThreadList, thread
				threads = threads + 1

			EndIf
			
			For thread = EachIn ThreadList
				If Not ThreadRunning (thread)
					ListRemove ThreadList, thread
					threads = threads - 1
				EndIf
			Next

			' -------------------------------------------------------------------------
			' Don't wanna hog CPU (also update temp graphics window)...
			' -------------------------------------------------------------------------
		
			' Graphics/Cls/Flip not needed in a real server!
			
			Delay 10
			Cls
			DrawText "Direct ESC to this window, not IDE!", 20, 20
			Flip
			
		Until KeyHit (KEY_ESCAPE)
		
		Print ""
		Print "Waiting for connections to close..."
		
		' -----------------------------------------------------------------------------
		' Free any open TCP streams...
		' -----------------------------------------------------------------------------

		' Wait for each thread to finish its current job and close its own stream/socket...

		LockMutex ThreadListMutex
			For thread = EachIn ThreadList
				WaitThread thread
			Next
		UnlockMutex ThreadListMutex

		' -----------------------------------------------------------------------------
		' All done!
		' -----------------------------------------------------------------------------
		
		CloseSocket server

	Else
		Print "Couldn't bind to port 80!"
	EndIf
	
Else

	Print "Couldn't create server!..."
	
EndIf

End

' -----------------------------------------------------------------------------
' Threaded file serve function...
' -----------------------------------------------------------------------------

Function ProcessConnection:Object (obj:Object)

	Local p:PrintList = New PrintList

	Local c:Connection = New Connection
	c.socket = TSocket (obj)
	
	If SocketConnected (c.socket)

		p.Add ""
		p.Add "Request from " + DottedIP (SocketRemoteIP (c.socket))
		
		c.stream = CreateSocketStream (c.socket)

	Else
		
		p.Add "ERROR: No stream created from socket!"
		
		' No stream was created -- this can happen!

		KillConnection c
		p.PrintAll
		
		Return Null

	EndIf

	' ---------------------------------------------------------------------
	' Some variables (see further down for meanings)...
	' ---------------------------------------------------------------------
	
	Local eoc:Int
	Local eop:Int

	Local command:String
	Local parameter:String
	Local file:String
	Local http:String
	Local program:String
	Local incoming:String
	
	' ---------------------------------------------------------------------
	' HTTP requests end with a blank line, so we read until we get that...
	' ---------------------------------------------------------------------

	Repeat
	
		' -----------------------------------------------------------------
		' Read a line from an incoming HTTP request...
		' -----------------------------------------------------------------

		' The format of an incoming request line is:
		
		'		"Command" [space] "parameters"

		' Examples...
						
		'		"GET /thisfile.txt"
		'		"User-Agent; AcmeBrowse"
		
		If SocketConnected (c.socket)
		
			incoming = ReadLine (c.stream)
			
		Else
		
			KillConnection c
			p.PrintAll
			
			Return Null
			
		EndIf

		If incoming <> ""

			' -------------------------------------------------------------
			' Got a line? Let's parse! Split command and parameter(s)...
			' -------------------------------------------------------------

			eoc = Instr (incoming, " ")				' End of command part of incoming
			command = Lower (Left (incoming, eoc))		' Command part of incoming
			parameter = Mid (incoming, eoc + 1)		' Parameter part of incoming

		EndIf
			
		' -----------------------------------------------------------------
		' Let's see what command we've got...
		' -----------------------------------------------------------------

		Select command$
		
			Case "get "

				' ---------------------------------------------------------
				' Got a HTTP file request!
				' ---------------------------------------------------------

				' Format of GET is: "GET /thisfile.txt"
				
				eop = Instr (parameter, " ")			' End of first parameter ("GET")
				file = Mid (parameter, 1, eop - 1)		' First parameter ("GET")
				http = Mid (parameter, eop + 1)		' Second parameter ("/thisfile.txt")

				' ---------------------------------------------------------
				' Requesting program's name/identifier...
				' ---------------------------------------------------------

			Case "user-agent: "

				program = Mid (incoming, eoc + 1)
				
		End Select
		
	Until incoming = "" ' Got blank line after headers, so all done here...

	file = Replace (file, "/", "\")

	' -------------------------------------------------------------
	' Remove \ from end of filename (used for folder redirection)...
	' -------------------------------------------------------------

	If Right (file, 1) = "\" Then file = Left (file, Len (file) - 1)
	
	' ---------------------------------------------------------------------
	' Lessee what we've got...
	' ---------------------------------------------------------------------
	
	p.Add "Requested file: " + file
	p.Add "Requested by: " + program
	p.Add "Requested HTTP version: " + http
	p.PrintAll

	' ---------------------------------------------------------------------
	' OK, we barely know what we're doing, so only accept HTTP 1.1...
	' ---------------------------------------------------------------------
	
	If http$ <> "HTTP/1.1"

		' Very wary of streams now!
		
		Try
		
			If SocketConnected (c.socket) And Not Eof (c.stream) Then WriteLine c.stream, "HTTP/1.1 505 This server only accepts HTTP version 1.1"
			If SocketConnected (c.socket) And Not Eof (c.stream) Then WriteLine c.stream, ""

		Catch error:Object
		
			p.Add "ERROR (" + file + "): Received non-HTTP 1.1 request, but stream failed outside error-checking!"
			
			KillConnection c
			p.PrintAll
			
			Return Null
			
		End Try
		
	Else

		' -----------------------------------------------------------------
		' It was a HTTP 1.1 request...
		' -----------------------------------------------------------------

		' Convert any %xx (Hex) codes in URL to Chr (ascii) character...
		' (Eg. %20 is ascii 57, ie. Chr (57), ie. a Space.)
		
		file = UnHexURL (file)

		If file
			If Left (file, 1) <> "\"
				file = "\" + file ' Add leading "/" if not found...
			EndIf
		EndIf
		
		' -------------------------------------------------------------
		' Does the requested file exist in our 'site' folder?
		' -------------------------------------------------------------

		Local file_type:Int = FileType (folder + file)
		
		If file = "" Then file_type = 2
		
		Select file_type
		
			Case 0 ' File does not exist...

				p.Add "404: File not found (" + file + ")"

				Try
				
					If SocketConnected (c.socket) And Not Eof (c.stream) Then WriteLine c.stream, "HTTP/1.1 404 Not Found"
					If SocketConnected (c.socket) And Not Eof (c.stream) Then WriteLine c.stream, ""

				Catch error:Object
				
					p.Add "ERROR (" + file + "): Stream failed outside error-checking!"
					
					KillConnection c
					p.PrintAll
					
					Return Null
					
				End Try
				
			Case 1 ' File exists!

				Try
				
					If SocketConnected (c.socket) And Not Eof (c.stream) Then WriteLine c.stream, "HTTP/1.1 200 OK"
					If SocketConnected (c.socket) And Not Eof (c.stream) Then WriteLine c.stream, ""

				Catch error:Object
				
					p.Add "ERROR (" + file + "): Stream failed outside error-checking!"
					
					KillConnection c
					p.PrintAll
					
					Return Null
					
				End Try

				Local requested:TStream = ReadFile (folder + file)

				If requested

					Try

						While Not Eof (requested)
						
							If SocketConnected (c.socket) And Not Eof (c.stream)
							
								Local buffer:TBank = CreateBank (OPTIMUM_IO)
								Local bytesread:Int = 0
								Local offset:Int = 0
								
								If buffer

									Repeat

										bytesread = ReadBank (buffer, requested, 0, OPTIMUM_IO)

										' This line may trigger Try/Catch error (write-to-stream)...

										WriteBank buffer, c.stream, 0, bytesread
										offset = offset + bytesread

									Until Eof (requested)

								EndIf
								
								buffer = Null
								
							Else
						
								p.Add "ERROR (" + file + "): Socket lost while writing file"
								Exit
						
							EndIf
						
						Wend
						
						If requested
							CloseFile requested
						EndIf

						If SocketConnected (c.socket)

							If Not Eof (c.stream)
								WriteLine c.stream, ""
							EndIf

						Else

							p.Add "ERROR (" + file + "): No socket for sending blank line after file"
							
							KillConnection c
							p.PrintAll

							Return Null

						EndIf

					Catch error:Object
				
						' This can be triggered during file read/write While/Wend loop,
						' for reasons unknown (socket lost just after socket and stream
						' checked valid?)...
						
						p.Add "ERROR (" + file + "): Error while writing file to stream"
						
						KillConnection c
						p.PrintAll
						
						Return Null
					
					End Try
			
				EndIf

			Case 2 ' Folder...

				' Try index.htm and index.html...
				
				Local redirect:String = file + "\index.htm"
				
				If FileType (folder + redirect) = 0 ' Nope!
					redirect = file + "\index.html" ' We'll see...
					If FileType (folder + redirect) = 0
						redirect = ""
					EndIf
				EndIf
				
				Try

					If redirect

						' Re-direct browser to index.htm or index.html if present (browser will make new request, ie. new thread will kick in)...
						
						If SocketConnected (c.socket) And Not Eof (c.stream) Then WriteLine c.stream, "HTTP/1.1 307 Temporary Redirect"
						If SocketConnected (c.socket) And Not Eof (c.stream) Then WriteLine c.stream, "location: " + redirect			
						If SocketConnected (c.socket) And Not Eof (c.stream) Then WriteLine c.stream, ""

					Else
						
						' No index.htm or index.html found, so show folder listing message...

						Local html:String = "<HTML><TITLE>Folder request</TITLE><BODY>Folder listings not allowed!</BODY></HTML>"

						If SocketConnected (c.socket) And Not Eof (c.stream) Then WriteLine c.stream, "HTTP/1.1 200 OK"
						If SocketConnected (c.socket) And Not Eof (c.stream) Then WriteLine c.stream, ""
						If SocketConnected (c.socket) And Not Eof (c.stream) Then WriteLine c.stream, html

					EndIf
					
				Catch error:Object
				
					p.Add "ERROR (" + file + "): Stream failed outside error-checking!"
					
					KillConnection c
					p.PrintAll
					
					Return Null
					
				End Try

		End Select

	EndIf

	p.PrintAll
	KillConnection c
	
	Return Null
	
End Function

' Made separate as it's referenced a lot...

Function KillConnection (c:Connection)
	If SocketConnected (c.socket) Then CloseSocket c.socket
	If c.stream And Not Eof (c.stream) Then CloseStream c.stream
End Function

' Helper functions...

Function UnHexURL:String (url:String)
	Local pos:Int
	Repeat
		pos = Instr (url, "%")
		If pos
			Local hexx:String = Mid (url$, pos, 3)
			url = Replace (url, hexx, Chr (HexToDec (hexx)))
		EndIf
	Until pos = 0
	Return url
End Function

Function HexToDec:Int (h:String)

	' From PureBasic code by 'PB'...

	If Left (h, 1) = "%" Then h = Right (h, Len (h) - 1)
	h = Upper (h)

	Local a:String
	Local d:Int

	For Local r:Int = 1 To Len (h)
		d = d Shl 4; a = Mid (h, r, 1)
		If Asc (a) > 60
			d = d + Asc (a) - 55
		Else
			d = d + Asc (a) - 48
		EndIf
	Next
	
	Return d
	
End Function
