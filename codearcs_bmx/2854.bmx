; ID: 2854
; Author: Luke111
; Date: 2011-05-25 09:17:26
; Title: Multithreaded PHP Server
; Description: A Multithreaded PHP Server Made In BMax Based On BlitzServe2

'Lukehttpd Web Server (Based on BlitzServe2)
'v0.0.0.2 Added Base PHP Support
'WINDOWS ONLY (for now)
'------------------------------------------
'Well this is pretty much the same as BlitzServe2
'For PHP, change phproot$ to the base php directory
'Also, change php.exe to the PHP binary name
'The CacheRoot Structure:
'Your Base Cache Root Location(C:\cacheroot)
'Inside there is 800000 folders, all with a file named batch.bat inside.
'Yes, I know this is not the easiest way of going about the serving, but I have
'2x2TB Hard Drives and 8 GB RAM and really don't care at the moment.
'If you want a different algorithm and a different solution for the Dining Philosopher's problem
'then you implement it yourself.
'The folders are named 0-800000
'That's about it. It is pretty slow, but good for learning; as is most code-archives work.
'There is a rogue print statement somewhere in there (that I used for debugging),
'I am not in the mood to find it.
'Look for InstallCacheRoot in the Miscellaneous Archives. It is the program that installs the
'800000 folder-file combo. (Unless you implement your own method, which is strongly reccommended).
Import pub.freeprocess
SuperStrict
'Document Root
'The place where all the files to serve are stored
Global documentroot:String = "D:\htdocs"
'Set the apptitle, or the title of the application
AppTitle = "LUHTTPD (Luke HTTP Server)"
'fix the document root for windows
documentroot$ = Replace(documentroot$,"/","\")
Global phproot:String = "D:\php"
Global phpmodule:String = "php.exe"
Global cacheroot:String = "D:\cacheroot"
If Right(documentroot$,1) = "\" Or Right(documentroot$,1) = "/" Then
	documentroot$ = Left(documentroot$,Len(documentroot$) - 1)
EndIf
If FileType(documentroot$) <> 2 Then
	RuntimeError "Error 0001: Document Root Invalid"
EndIf
'OPTIMUM_IO
Const OPTIMUM_IO:Int = 65535
'Document root is now fixed, or has errored.
'PLIST: Threading support type
Type PLIST
	'main field
	Field list:TList = CreateList()
	'add something to the list field
	Method Add(inf:String)
		ListAddLast list, inf
	End Method
	'print the whole list field
	Method PAll()
		For Local i:String = EachIn list
			Print i
			ListRemove list, i
		Next
	End Method
End Type
'CON: Each connection has a socket and a socketstream
Type CON
	'socket
	Field socket:TSocket
	'socketstream
	Field stream:TSocketStream
End Type
'ToPass: Container for the parameters to pass to the thread
Type ToPass
	Field nthread:Long
	Field nsock:TSocket
End Type
'Thread list and mutex, to stop the client from reconnecting and corruption
'and stuff of that sort
'Mutex
Global ThreadListMutex:TMutex = CreateMutex()
'List
Global ThreadList:TList = CreateList()
'start of the good stuff :)
Local Thread:TThread
Local NumOfThreads:Int = 0
'set up the array of threadnumber
Global threadnumary:Byte[800000]
Local y:Long = 0
For y = 0 To 800000 Step 1
	threadnumary[y] = 0
Next
'the grabber socket
Local serversock:TSocket = CreateTCPSocket()
'was the grabber socket successfully created?
If serversock Then
	'yes
	'bind the socket to port 80
	If BindSocket(serversock, 80) Then
		'success!
		'some backlog stuff
		SocketListen serversock, 5
		Print "LUHTTPD Listening on Port 80"
		Print "LUHTTPD Serving Document Root: "+documentroot$
		Print "LUHTTPD Ready To Roll!"
		Print "Awaiting Connections..."
		'main loop
		Repeat
			'accept a connection
			Local remote:TSocket = SocketAccept(serversock)
			'is there a new connection?
			If remote Then
				'yes
				'create the new ToPass
				Local passer:ToPass = New ToPass
				'create the new thread
				Local threadnumber:Long = FindOpenThreadNumber()
				passer.nthread = threadnumber
				passer.nsock = remote
				Thread = CreateThread(ProcessConnection,passer:ToPass)
				'add the thread to the thread list
				ListAddLast ThreadList, Thread
				'Increment the number of threads, because we have a new one
				NumOfThreads = NumOfThreads + 1
			EndIf
			'kill done threads
			For thread = EachIn ThreadList
				If Not ThreadRunning(thread) Then
					ListRemove ThreadList, thread
					NumOfThreads = NumOfThreads - 1
				EndIf
			Next
			'stop being a cpu whore
			Delay 5
		'start shutdown if the esc key is hit
		Forever
		'shutting down
		Print ""
		Print "Waiting for connections to close..."
		'close it all!!!
		LockMutex ThreadListMutex
			For thread = EachIn ThreadList
				WaitThread thread
			Next
		UnlockMutex ThreadListMutex
		'complete
		CloseSocket serversock
	Else
		RuntimeError "Error 0002: Could not bind to port 80"
	EndIf
Else
	RuntimeError "Error 0003: Could not create server"
EndIf
End
Function ProcessConnection:Object (obj:Object)
	Local obji:ToPass = ToPass(obj)
	Local g:Long = obji.nthread
	Local p:PLIST = New PLIST
	Local c:CON = New CON
	c.socket = obji.nsock
	If SocketConnected (c.socket)
		p.Add ""
		p.Add "Request from " + DottedIP (SocketRemoteIP (c.socket))
		c.stream = CreateSocketStream (c.socket)
	Else
		p.Add "ERROR: No stream created from socket!"
		' No stream was created -- this can happen!
		KillConnection c
		p.PAll
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
			p.PAll
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
	p.PAll
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
			p.PAll
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
		Local file_type:Int = FileType (documentroot + file)
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
					p.PAll
					Return Null
				End Try
			Case 1 ' File exists!
				Try
					If SocketConnected (c.socket) And Not Eof (c.stream) Then WriteLine c.stream, "HTTP/1.1 200 OK"
					If SocketConnected (c.socket) And Not Eof (c.stream) Then WriteLine c.stream, ""
				Catch error:Object
					p.Add "ERROR (" + file + "): Stream failed outside error-checking!"
					KillConnection c
					p.PAll
					Return Null					
				End Try
				Local bit:Byte = 0
				If Lower(Right(file,4)) = ".php" Then
					Local oofile:TStream
					oofile = WriteFile(cacheroot$+"\"+String.FromLong(g)+"\batch.bat")
					WriteLine(oofile,phproot$+"\"+phpmodule$+" -f "+documentroot$+file+" > "+cacheroot$+"\"+String.FromLong(g)+file)
					CloseFile oofile
					system_(cacheroot$+"\"+String.FromLong(g)+"\batch.bat")
					bit = 1
				EndIf
				Local requested:TStream
				If bit = 0 Then
					requested = ReadFile (documentroot + file)
				Else
					requested:TStream = ReadFile (cacheroot + "\" + String.FromLong(g) + file)
				EndIf
				If requested Then
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
							p.PAll
							Return Null
						EndIf
					Catch error:Object
						' This can be triggered during file read/write While/Wend loop,
						' for reasons unknown (socket lost just after socket and stream
						' checked valid?)...
						p.Add "ERROR (" + file + "): Error while writing file to stream"
						KillConnection c
						p.PAll
						Return Null
					End Try
				EndIf
			Case 2 ' Folder...
				' Try index.htm and index.html...
				Local redirect:String = file + "\index.php"
				If FileType (documentroot + redirect) = 0 ' Nope!
					redirect = file + "\index.htm"
					If FileType(documentroot + redirect) = 0 Then
						redirect = file + "\index.html" ' We'll see...
						If FileType (documentroot + redirect) = 0
							redirect = ""
						EndIf
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
					p.PAll
					Return Null
				End Try
		End Select
	EndIf
	p.PAll
	KillConnection c
	'fix the thread number array with this thread's index
	threadnumary[g] = 0
	Return Null
End Function

' Made separate as it's referenced a lot...

Function KillConnection (c:CON)
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
'Added Function to find the first open thread number
Function FindOpenThreadNumber:Long()
	Local z:Long = 0
	For z = 0 To 800000 Step 1
		If threadnumary[z] = 0 Then
			Return z
		EndIf
	Next
	RuntimeError "Error: Max Connection Limit Reached!"
End Function
