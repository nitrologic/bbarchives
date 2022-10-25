; ID: 2904
; Author: BlitzSupport
; Date: 2011-11-30 16:08:12
; Title: Multi-threaded Code Archives downloader
; Description: Downloads all Blitz Code Archives entries

'

' *** Rather than copy and paste this code (it takes about 20 minutes to download the entire Code Archives from scratch!),
' please download the code and complete archives up to 22 July 2012 from the link below, THEN run it occasionally
' to update your collection:

' http://www.hi-toro.com/blitz/codearc/codearc.zip









' -----------------------------------------------------------------------------
'   * IMPORTANT! TURN "THREADED BUILD" ON IN PROGRAM MENU -> BUILD OPTIONS! *
' -----------------------------------------------------------------------------






SuperStrict

Const AppName:String = "Blitz Code Archiver"

Const OVERWRITE_ALL:Int = False	' Only download new entries. True will download
								' and overwrite all entries...

' -----------------------------------------------------------------------------
' Multi-threaded Code Archives downloader for www.blitzbasic.com...
' -----------------------------------------------------------------------------

' Sorts code into sub-folders: BlitzBasic/ for .bb files and BlitzMax/ for .bmx;
' also creates category sub-folders within each, eg. Graphics, Networking, etc.

' Stores code with entry number and title.

' Skips entries already downloaded/known missing.

' Code not declared as Public Domain will be flagged with a warning at the top.

' Works with site on 30 Nov 2011. Will fail if site layout changes, so page
' parsing code will need to be updated!

' -----------------------------------------------------------------------------

' -----------------------------------------------------------------------------
' How it works...
' -----------------------------------------------------------------------------

' The main thread connects to blitzbasic.com and holds open the connection for
' as long as the server allows, using the same connection to request each
' Code Archives web page in turn.

' The server eventually says "go away" after a certain amount of time/data, so
' the program detects this, re-connects and then resumes its requests on the
' new connection.

' Each page is parsed to find the URL for the downloadable source. A thread is
' then spawned for each separate code downloaded.

' -----------------------------------------------------------------------------
' Structures...
' -----------------------------------------------------------------------------

' Handles connection...

Type Session
	Field www:String
	Field ip:Int
	Field socket:TSocket
	Field stream:TSocketStream
	Field working_index:Int
End Type

' Contains download URL and category. (Passed to DownloadCode thread by
' ParseCodePage function.)

Type Download
	Field url:String
	Field category:String
	Field pd:Int
End Type

' -----------------------------------------------------------------------------
' Some constants...
' -----------------------------------------------------------------------------

Const CODE_PD:Int				= 1
Const CODE_NOT_PD:Int			= 2
Const CODE_NOT_FOUND:Int		= 4
Const CONNECTION_DROPPED:Int	= 8
Const URL_ERROR:Int				= 16

Const BB_DOMAIN:String = "www.blitzbasic.com"
Const CODE_ARCS:String = "/codearcs/codearcs.php?code="

' -----------------------------------------------------------------------------
' Couple of mutexes...
' -----------------------------------------------------------------------------

Global PRINT_MUTEX:TMutex = CreateMutex ()
Global DIR_MUTEX:TMutex = CreateMutex ()

' -----------------------------------------------------------------------------
' Thread stuff...
' -----------------------------------------------------------------------------

Const NUM_THREADS:Int = 8 ' Spawn (up to) this many download threads at once...

Global Thread:TThread [NUM_THREADS] ' Array of threads
Global ThreadTrack:Int = 0			' Thread counter used later

' -----------------------------------------------------------------------------
' Multi-threaded Print helper...
' -----------------------------------------------------------------------------

Function TPrint (message:String)

	LockMutex PRINT_MUTEX
		Print message
	UnlockMutex PRINT_MUTEX
	
End Function

' -----------------------------------------------------------------------------
' GO!
' -----------------------------------------------------------------------------

' These entries are known to be missing (deleted) at time of writing...

Local known_missing:String [] = ["4", "5", "36", "40", "57", "69", "70", "71", "72",..
"73", "74", "75", "76", "77", "78", "79", "80", "81", "82", "83", "84", "85", "86",..
"87", "88", "89", "90", "91", "122", "154", "186", "210", "234", "240", "256", "269",..
"270", "271", "272", "296", "297", "303", "304", "305", "306", "308", "309", "310",..
"311", "334", "344", "398", "405", "408", "409", "424", "425", "440", "484", "486",..
"496", "516", "522", "523", "524", "535", "536", "543", "572", "609", "623", "636",..
"637", "657", "666", "679", "722", "723", "736", "737", "738", "739", "740", "741",..
"742", "759", "760", "761", "762", "763", "767", "768", "770", "771", "772", "774",..
"775", "777", "780", "800", "834", "842", "885", "886", "887", "899", "900", "925",..
"941", "945", "953", "957", "964", "965", "978", "982", "984", "986", "1005", "1007",..
"1012", "1018", "1019", "1020", "1021", "1032", "1037", "1038", "1041", "1042", "1043",..
"1044", "1045", "1046", "1047", "1048", "1049", "1050", "1051", "1052", "1054", "1055",..
"1056", "1058", "1066", "1074", "1075", "1076", "1077", "1155", "1209", "1210", "1211",..
"1212", "1213", "1218", "1226", "1280", "1291", "1301", "1312", "1313", "1317", "1391",..
"1409", "1418", "1459", "1490", "1506", "1526", "1543", "1549", "1559", "1613", "1614",..
"1615", "1616", "1617", "1618", "1619", "1620", "1622", "1698", "1743", "1756", "1797",..
"1984", "2021", "2049", "2062", "2085", "2149", "2158", "2159", "2160", "2229", "2256",..
"2317", "2319", "2326", "2424", "2438", "2445", "2694", "2726", "2729", "2793", "2827",..
"2898", "2899", "2902", "2905", "3028", "3084", "3098", "3135", "3139", "3174"]

' List to be filled with missing entries and entries already downloaded...

Global SkipList:TList = ListFromArray (known_missing)

Local highest:Int = FindLatestEntry ()

TPrint ""
TPrint "Requesting entries from #1 to #" + highest + ", skipping missing/already downloaded entries..."

' Check both sub-folders for downloaded entries...

ParseDir "BlitzBasic"
ParseDir "BlitzMax"

Local downloader:Session = CreateSession (BB_DOMAIN)

If downloader

	Local index:Int = 1		' Code Archives index
	Local result:Int		' Result returned by RequestCode
	Local dropped:Int		' Flag for dropped connections
	
	For index = index To highest
	
		Local ignored:Int = False
		
		If Not OVERWRITE_ALL

			' Check if already downloaded...

			For Local entry:String = EachIn SkipList
				If entry = index
					ignored = True
					ListRemove SkipList, entry
					Exit
				EndIf
			Next

		EndIf
		
		If Not ignored

			TPrint ""
			TPrint "Requesting Code Archives entry #" + index + "..."

			' RequestCode does NOT guarantee that an individual request completed
			' its download, just that the request was for a valid entry and a
			' download thread was spawned...
			
			result = RequestCode (downloader, index)
			
			' Note if connection was dropped by the server during download request...
			
			If result & CONNECTION_DROPPED
				dropped = True
				result = result & ~CONNECTION_DROPPED
			Else
				dropped = False
			EndIf
			
			Select result
			
				Case CODE_PD
			
					'TPrint "Got code entry #" + index
					
				Case CODE_NOT_PD
			
					'TPrint "Code entry #" + index + " is not PD"
					
				Case CODE_NOT_FOUND
					
					TPrint ""
					TPrint "~t~tNo code entry #" + index
					
				Case URL_ERROR
			
					TPrint ""
					TPrint "~t~tURL doesn't exist for #" + index ' Shouldn't happen!
					
			End Select
			
			' Reconnect if session was dropped...
	
			' Doesn't need to re-request: the bb.com server drops the connection
			' after a certain amount of time/data but only after completing the
			' file it was on. (Random accidental drops NOT tested/accounted for!)
			
			If dropped
				
				TPrint ""
				TPrint "Connection dropped"
				TPrint ""
				
				CloseSession downloader
				
				downloader = CreateSession ("www.blitzbasic.com")

			EndIf

		EndIf
		
	Next
	
	' Wait for remaining download threads to finish...

	For ThreadTrack = 0 Until NUM_THREADS
		If Thread [ThreadTrack]
			TPrint ""
			TPrint "Waiting for thread #" + ThreadTrack + " to finish"
			WaitThread (Thread [ThreadTrack])
		EndIf
	Next
		
	CloseSession downloader

Else
	TPrint "Session failed"
EndIf

Print ""
Input "Press Return/Enter to exit..."

' -----------------------------------------------------------------------------
' The workers...
' -----------------------------------------------------------------------------

' Find number of latest entry by parsing Code Archives pages...

Function FindLatestEntry:Int ()

	Function GetLatest:Int (url:String)
	
		TPrint "~tChecking " + "http://" + BB_DOMAIN + url
		
		Local readpage:TStream = ReadStream ("http::" + BB_DOMAIN + url)
		Local coderef:String = "<a href=codearcs.php?code="
		Local latest:Int
		
		If readpage
		
			Repeat
				
				Local line:String = ReadLine (readpage)
				
				Local pos:Int = Instr (line, coderef)
				
				Local endref:Int = Instr (line, ">", pos + Len (coderef))
	
				latest = Int (Mid (line, pos + Len (coderef), endref - (pos + Len (coderef))))
				
			Until Eof (readpage)
			
			CloseStream readpage
			
		EndIf
	
		Return latest
		
	End Function
	
	TPrint ""
	TPrint "Finding latest Code Archives entry from category pages..."
	TPrint ""
	
	Local response:String
	Local length:Int

	Local caturl:String = "/codearcs/codearcs.php"
	Local catlink:String = "<a href=~qcodearcs.php?cat="

	Local highest:Int
	
	Local readcats:TStream = ReadStream ("http::" + BB_DOMAIN + caturl)

	If readcats
	
		Repeat
			
			Local line:String = ReadLine (readcats)
			
			Local pos:Int = 1
			
			Repeat
			
				pos = Instr (line, catlink, pos + 1)
				
				If pos
				
					Local endquote:Int = Instr (line, "~q", pos + Len (catlink))
					
					Local url:String = Mid (line, pos + 21, (endquote - pos) - 21)
					
					Local hi:Int = GetLatest (caturl + url)
				
					If hi > highest Then highest = hi
				
				EndIf
				
			Until pos = 0
			
		Until Eof (readcats)
		
		CloseStream readcats
		
	EndIf
	
	Return highest
	
End Function

' Adds already downloaded entries to a list...

Function MarkDownloaded (file:String)

	If Lower (ExtractExt (file)) = "bb" Or Lower (ExtractExt (file)) = "bmx"

		' Find entry number from file name (eg. "24 - Title.bb")...
		
		file = StripAll (file)

		Local pos:Int = Instr (StripAll (file), " - ")
		Local entry:String = Left (file, pos - 1)

		If entry Then ListAddLast SkipList, entry
	
	EndIf
	
End Function

' Folder parser...

Function ParseDir (dir:String)

	If Right (dir, 1) <> "\" And Right (dir, 1) <> "/"
		dir = dir + "/"
	EndIf
	
	Local folder:Int = ReadDir (dir)

	If folder
	
		Repeat

			Local entry:String = NextFile (folder)

			If entry = "" Then Exit
			
			If entry <> "." And entry <> ".."

				If FileType (dir + entry) = FILETYPE_FILE
	
					Local file:String = entry
		
					Local full:String = dir
		
					If Right (full, 1) <> "\" And Right (full, 1) <> "/"
						full = full + "\"
					EndIf
		
					full = full + file
		
					MarkDownloaded (full)
		
				Else
		
					If FileType (dir + entry) = FILETYPE_DIR
	
						Local file:String = entry
		
						If file <> "." And file <> ".."
		
							Local ffolder:String = dir
		
							If Right (ffolder, 1) <> "\" And Right (ffolder, 1) <> "/"
								ffolder = ffolder + "\"
							EndIf
	
							ffolder = ffolder + file
								
							ParseDir (ffolder)
		
						EndIf
		
					EndIf
		
				EndIf
	
			EndIf

		Forever
	
	EndIf

End Function

' Turned out to be pointless...

Function CodeArcURL:String (index:Int)
	Return CODE_ARCS + index
End Function

' Check still connected before sending request to server...

Function SafeWriteLine (s:Session, message:String)
	If s
		If SocketConnected (s.socket)
			WriteLine s.stream, message
		EndIf
	EndIf
End Function

' Check still connected before reading line from server...

Function SafeReadLine:String (s:Session)
	If s
		If SocketConnected (s.socket)
			Return ReadLine (s.stream)
		EndIf
	EndIf
End Function

' Check still connected before reading string from server...

Function SafeReadString:String (s:Session, bytes:Int)
	If s
		If SocketConnected (s.socket)
			Return ReadString (s.stream, bytes)
		EndIf
	EndIf
End Function

' Extract "404", "200", etc from server response...

Function ReplyCode:String (message:String)

	If Left (message, 5) = "HTTP/"
		Local pos:Int = Instr (message, " ")
		Return Mid (message, pos + 1, 3)
	EndIf
	
End Function

' Establish connection to server...

Function CreateSession:Session (domain:String)

	TPrint ""
	TPrint "Creating new TCP session..."
	TPrint ""
	
	Local s:Session = New Session

	s.www = domain
	s.ip = HostIp (s.www)
	
	s.socket = CreateTCPSocket ()
	
	If s.socket
	
		If ConnectSocket (s.socket, s.ip, 80)
	
			s.stream = CreateSocketStream (s.socket)
	
			If s.stream = Null
				TPrint "Couldn't create socket stream"
			EndIf
	
		Else
			TPrint "Couldn't connect to server " + HostName (s.ip)
		EndIf
	
	Else
		TPrint "Couldn't create TCP socket"
	EndIf

	If s.socket = Null Or s.stream = Null Then s = Null
	
	Return s
	
End Function

' Close down connection to server...

Function CloseSession (s:Session)

	If s

		TPrint ""
		TPrint "Closing session..."
		
		If s.stream Then CloseStream s.stream
		If s.socket Then CloseSocket s.socket
		
		s = Null
		
	EndIf
	
End Function

' Wraps up page request into helper function...

Function RequestCode:Int (s:Session, index:Int)

	If s

		s.working_index = index

		If SocketConnected (s.socket)
			Return GetPage (s:Session, CodeArcURL (s.working_index))
		Else
			Return CONNECTION_DROPPED
		EndIf

	EndIf

End Function

' Request Code Archives entry page...

Function GetPage:Int (s:Session, path:String)

	If s

		SafeWriteLine s, "GET "		+ path + " HTTP/1.1"	' State file name and HTTP protocol
		SafeWriteLine s, "Host: "	+ s.www					' Not sure what the point of this is -- we're connected!
		
		SafeWriteLine s, "User-Agent: " + AppName			' Name of this program -- irrelevant
		SafeWriteLine s, "Accept: text/plain"				' Code Archives are plain text
		
		SafeWriteLine s, ""									' Tell server to go ahead...
		
		Local response:String
		Local found:Int
		Local chunked:Int = False
		
		Repeat

			response = SafeReadLine (s)
			
			If Lower (Left (response, 26)) = "transfer-encoding: chunked" Then chunked = True
			
			'TPrint "HEADER: " + response
			
			If Left (response, 5) = "HTTP/"
			
				Select ReplyCode (response)
			
					Case "200"

						found = True
					
					Case "404"
						found = False
						
					Default
						found = False
						
				End Select

			EndIf
			
		Until response = ""

		If found

			If Not chunked Then CloseSession s; RuntimeError " Server has changed; not using chunked encoding. New download function needed!"

			Return ParseCodePage (s)

		Else
			
			Local error:Int = 0
			
			Repeat
				response = SafeReadLine (s)
				TPrint "FLUSH: " + response
			Until response = ""
			
			error = error | URL_ERROR
			
			If Not SocketConnected (s.socket)
				error = error | CONNECTION_DROPPED
			EndIf
			
			Return error
			
		EndIf
		
	EndIf

End Function

' Parses entry's HTML page to find URL to code, Public Domain status, etc, then
' spawns download thread if valid...

Function ParseCodePage:Int (s:Session, debug:Int = False)

	' Read HTML page and get the bits we need...
	
	Local response:String
	Local length:Int

	Local pd:Int = False
	Local url:String
	Local exists:Int = True
	
	Local category:String
	
	Local codestart:String = "<tr ><td class=~qcell~q><pre class=~qcode~q>"
	Local codeend:String = "</pre></td></tr>"
	
	Local incode:Int = False
	
	Repeat
	
		' Length of data in bytes; hex string possibly followed
		' by semi-colon and undefined data...
		
		response = SafeReadLine (s)

		If Instr (response, ";")
			response = Left (response, Instr (response, ";") - 1)
		EndIf
		
		length = Int ("$" + response)
		
		If length

			' Read data...
			
			' Can't use ReadLine as content can contain CR/LF...
			
			response = SafeReadString (s, length)

			SafeReadLine s ' Clear the CR/LF following content...

			Local line:String [] = response.Split ("<br>")
			
			For Local loop:Int = 0 Until line.length

				If Left (line [loop], Len (codestart)) = codestart And Right (line [loop], Len (codeend)) = codeend
					incode = True
				Else
					incode = False
				EndIf

			Next

			If Not incode ' Only interested in first codebox. Need to check what happens if there's a codebox in the description!
			
				' Store category for later use... and figure out HOW to use it...
			
				Local seek:String = "<a href=codearcs.php>Code archives</a>/<a href=codearcs.php?cat="
				Local catref:Int = Instr (response, seek)
				
				If catref
				
					' Find next > and < symbols...
					
					Local startcat:Int = Instr (response, ">", catref + Len (seek))
					Local endcat:Int = Instr (response, "<", startcat) - 1
					
					' Category name lies in-between...
					
					category = response [startcat..endcat]
					
				EndIf
				
				' Non-existent entries generate custom page, NOT a 404...
				
				If Instr (response, "ERROR: Internal error")
					exists = False
				EndIf
				
				' PD notice found...
				
				If Instr (response, "This code has been declared by its author to be Public Domain code.")
					pd = True
				EndIf
				
				' Find URL from this line...
				
				If Instr (response, ">Download source code<")
					Local q1:Int = Instr (response, "~q")
					Local q2:Int = Instr (response, "~q", q1 + 1)
					url = Mid (response, q1 + 1, (q2 - 1) - q1)
				EndIf
				
			EndIf

		Else
		
			Repeat
				response = SafeReadLine (s)
			Until response = ""
			
			Exit
			
		EndIf

	Until response = ""

	' The server disconnects the TCP session after a certain time or number of bytes,
	' but only on completing the current file. This means we should return success but
	' also that the server closed the session so we can re-establish it...
	
	Local result:Int = 0
	
	If Eof (s.stream)
		If Not SocketConnected (s.socket)
			result = result | CONNECTION_DROPPED
		EndIf
	EndIf

	If exists

		Local dl:Download = New Download
		
		dl.url = url
		dl.category = category
		
		If pd
			result = result | CODE_PD
			dl.pd = CODE_PD
		Else
			result = result | CODE_NOT_PD
			dl.pd = CODE_NOT_PD
		EndIf

		' ---------------------------------------------------------------------
		' Dumb thread-spawner...
		' ---------------------------------------------------------------------

		' Note: dependent on the fact that this function is NOT multi-threaded!
		
		If Thread [ThreadTrack]
			'TPrint "Waiting for thread #" + ThreadTrack + " to finish"
			WaitThread (Thread [ThreadTrack])
		EndIf
		
		'TPrint "Spawning thread # " + ThreadTrack
		Thread [ThreadTrack] = CreateThread (DownloadCode, dl)

		ThreadTrack = ThreadTrack + 1
		If ThreadTrack = NUM_THREADS Then ThreadTrack = 0
		
		' ---------------------------------------------------------------------

	Else
		result = result | CODE_NOT_FOUND
	EndIf

	Return result
	
End Function

' Code download child thread retrieves the standalone .bb or .bmx code...

Function DownloadCode:Object (obj:Object)

	Local dl:Download = Download (obj)

	Local category:String = dl.category
	If category = "" Then category = "Unknown Category"
	
	Local file:String = "/codearcs/" + dl.url
	Local url:String = "http::" + BB_DOMAIN + file
	
	Local ip:Int = HostIp (BB_DOMAIN)
	Local socket:TSocket = CreateTCPSocket ()
	
	If socket
	
		If ConnectSocket (socket, ip, 80)

			Local stream:TStream = CreateSocketStream (socket)
	
			If stream

				Local blitzcode:String = ExtractExt (url)
				Local blitzfolder:String
				Local comment_symbol:String

				Select blitzcode
					Case "bb"
						blitzfolder = "BlitzBasic"
						comment_symbol = ";"
					Case "bmx"
						blitzfolder = "BlitzMax"
						comment_symbol = "'"
					Default
'						TPrint "Unexpected file type ~q" + blitzcode + "~q! " + dl.url + " (" + dl.category + ")"
'						End
						RuntimeError "Unexpected file type ~q" + blitzcode + "~q! See DownloadCode Function..."
				End Select
				
				blitzfolder = blitzfolder + "/" + category
				
				' Only have one thread creating folder...
				
				LockMutex DIR_MUTEX
					If FileType (blitzfolder) = 0 Then CreateDir blitzfolder, True
				UnlockMutex DIR_MUTEX

				Local saveas:String = blitzfolder + "/" + StripDir (url)
				
				Local save:TStream = WriteStream (saveas)
				
				If save
		
					WriteLine stream, "GET "	+ file + " HTTP/1.1"
					WriteLine stream, "Host: "	+ BB_DOMAIN
			
					WriteLine stream, "User-Agent: " + AppName
					WriteLine stream, "Accept: text/plain"
			
					WriteLine stream, ""
				
					Repeat
						' Skip headers...
					Until ReadLine (stream) = ""
					
					Local linecount:Int = 0
					
					TPrint ""

					If dl.pd = CODE_NOT_PD
						WriteLine save, ""
						WriteLine save, comment_symbol + " " + Replace (LSet (" ", 78), " ", "-")
						WriteLine save, comment_symbol + " WARNING: This code has NOT been declared as Public Domain!"
						WriteLine save, comment_symbol + " " + Replace (LSet (" ", 78), " ", "-")
						WriteLine save, ""
					EndIf
					
					TPrint "~tCategory: " + category
					
					Local title:String = ""
					
					Repeat
						
						Local line:String = ReadLine (stream)
						
						If linecount < 5

							If Left (line, 2) = "; "
							
								Local colon:Int = Instr (line, ": ", 3)
								
								If colon
								
									Local comment:String = Mid (line, colon + 2)
									
									Local autocomment:Int = False ' Automatic comment inserted by server...
									
									Select Mid (line, 3, (colon - 1) - 2)
										Case "ID"
											Print "~tEntry: #" + comment
											autocomment = True
										Case "Author"
											Print "~tAuthor: " + comment
											autocomment = True
										Case "Date"
											Print "~tDate submitted: " + comment
											autocomment = True
										Case "Title"
											Print "~tTitle: " + comment
											title = comment
											autocomment = True
										Case "Description"
											Print "~tDescription: " + comment
											autocomment = True
									End Select
									
									If autocomment
										' Swap ; symbol for ' symbol in BlitzMax code!
										If blitzcode = "bmx" Then line = comment_symbol + Right (line, Len (line) - 1)
									EndIf
									
								EndIf
								
							EndIf
						
						EndIf
						
						WriteLine save, line
						
						linecount = linecount + 1
						
					Until Eof (stream)

					CloseStream save
					
					' Replace invalid characters (based on Windows) with underscore...
					
					Local invalid:String[] = ["\", "/", ":", "*", "?", "~q", "<", ">", "|"]
					
					For Local char:String = EachIn invalid
						title = Replace (title, char, "_")
					Next
					
					' Rename from "24.bb" to "24 - Title.bb"...
					
					If Not RenameFile (saveas, blitzfolder + "/" + StripAll (url) + " - " + title + "." + blitzcode)
						DeleteFile saveas	' This will delete files that were orphaned with old name on
											' last run, eg. in event of early program/thread termination...
					EndIf
					
				EndIf
				
				CloseStream stream
				
			EndIf

		EndIf
	
	EndIf
		
	Return Null
	
End Function
