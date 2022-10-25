; ID: 1554
; Author: ozak
; Date: 2005-12-07 04:19:00
; Title: Simple webserver
; Description: Simple webserver example

' Simple BlitzMax webserver by Odin Jensen 
' Exercise: Save all client sockets in list, update them a little each frame and then disconnect.
'           That will allow multiple connections without any delay like a real webserver :)

' EOL define
Const EOL:String = "~r~n"

' Create socket
Global mainSock:TSocket = CreateTCPSocket()

' Bind socket to port 80 (web port)
If (Not BindSocket(mainSock, 80))

	Print("Error binding socket. Someone might already be using that port.")
	End

End If

' Listen on socket
SocketListen(mainSock)

Print("Server online at port 80")

' Main loop
While (Not KeyHit(KEY_ESCAPE))

	' Check for connections
	Local clientSock:TSocket = SocketAccept(mainSock, 0)	

	' Did we get a connection?
	If (clientSock <> Null)
	
		' Print IP
		Print("Client '"+DottedIP(SocketRemoteIP(clientSock))+" connected!")
		
		' Create stream for client
		Local clientStream:TSocketStream = CreateSocketStream(clientSock)
		
		Print("")
		
		' Get first line
		Local line:String = ReadLine(clientStream)
		Print(line)
		
		' Grab request
		Local req:String = line[0..line.findLast(" ")]

		' Write rest of request		
		While (line <> "")
		
			line = ReadLine(clientStream)
			Print(line)
			
		Wend
		
		' Output client request
		Print("Client wants to: " + req)		
		
		' Do we have a get request?
		If (req.find("GET") <> -1)
		
			' Ok. Attempt to find file
			Local fileName:String = req[4..req.length]
			Print("Client requested file: " + fileName)
			
			' Is it root?
			If (fileName = "/")
			
				' Yes. Send index 
				fileName = "/index.html"
			
			End If
			
			' Fix file (remove /)
			fileName = fileName[1..fileName.length]
			
			
			' Try to open
			Local file:TStream = ReadStream(fileName)
			
			' Failed?
			If (file = Null)
			
				WriteLine(clientStream, "HTTP/1.0 404 Not found" + EOL)
				WriteLine(clientStream, EOL)
				WriteLine(clientStream, "404: " + fileName + " not found!")
			
			Else
			
				
				' Send stream
				Local bytes:Byte[512]
				While (Not Eof(file))
				
	
					' NOTE! Only byte read/write here so binaries works as expected
					Local read:Int = file.readBytes(bytes, 512)
					clientStream.writeBytes(bytes, read)
				
				Wend
				
				' Close stream
				CloseStream(file)
			
			End If
		
		
		Else
		
			' Send unsupported request message
			WriteLine(clientStream, "HTTP/1.0 405 unsupported method type: " + EOL)
			WriteLine(clientStream, "405: Unsupported method type: " + req)
		
		End If
				
		
		' Disconnect client
		FlushStream(clientStream)
		CloseSocket(clientSock)
	
	End If

Wend

' Close main socket
CloseSocket(mainSock)
