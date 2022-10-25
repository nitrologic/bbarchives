; ID: 2090
; Author: Dirk Krause
; Date: 2007-08-06 11:03:15
; Title: FTP Client
; Description: an example of an FTP Client with BlitzMax

Strict 

' http://tools.ietf.org/html/rfc959


Global Server_Host:String
Global Server_IP:Int
Global Server_Port:Int
Global Server_User:String
Global Server_pwd:String

Global Server_Socket:TSocket
Global Server_SocketStream:TSocketStream
Global Server_Stream:TStream



Global isConnected:Int  = False
Global isAborting:Int = False
Global pasvPort:Int
Global currentLine:String

Function message(s:String)
	Print s
EndFunction

Function messageOut(s:String)
	message "OUT: " + s
EndFunction

Function messageIn(s:String)
	message "IN:  " + s
EndFunction

Function messageSys(s:String)
	message "SYS: " + s
EndFunction

Function messageDebug(s:String)
	message "DBG: " + s
EndFunction

Function writeToServer(line:String)
	If Server_Stream And SocketConnected(Server_Socket)
		messageOut line
		WriteLine(Server_Stream, line)
	Else
		message "You are not connected yet"
	EndIf
EndFunction


Function receiveLineFromServer:String()
	' time in milliseconds until timeout
	Local threshold:Int = 3000
	
	Local start:Int = MilliSecs()
	Local now:Int = 0

	Local data:String
	If Server_Stream And SocketConnected(Server_Socket)
		While Not SocketReadAvail(Server_Socket) And ( (now - start) < threshold )
			now = MilliSecs()
		Wend
		If (now - start) < threshold
			If SocketReadAvail(Server_Socket)>0
		        data = ReadLine(Server_Stream)
			EndIf
	        If(data<>"")
				messageIn data
				currentLine = data
				Return ""+data
	        EndIf 
		Else
			messageSys "TIMEOUT in receiveLineFromServer"
			isAborting = True
		EndIf
	EndIf
EndFunction
	
Function send:String(s:String)
	writeToServer(s)
	Return receiveLineFromServer()
EndFunction


Function getDataAsText(p:Int)
	Local dataSocket:TSocket = CreateTCPSocket()
	Local dataSocketStream:TSocketStream
	Local dataStream:TStream

	messageDebug "trying to transfer some text on port " + String(p)
	If ConnectSocket( dataSocket, Server_IP, p)
		dataSocketStream = CreateSocketStream(dataSocket, True)
		dataStream       = OpenStream(dataSocketStream)
		If dataStream
			While Not Eof(dataStream)
				messageSys ReadLine(dataStream)
			Wend
			messageDebug "transferring some text"
			CloseStream(dataStream)
		Else
			messageSys "stream failed"
		EndIf
		CloseSocket(dataSocket)
	Else
		messageSys "socket failed"
	EndIf
EndFunction


Function getDataAsFile(p:Int, filename:String)
	Local dataSocket:TSocket = CreateTCPSocket()
	Local dataSocketStream:TSocketStream
	Local dataStream:TStream
	Local fileStream:TStream
	
	messageSys "trying to receive some data on port " + String(p)
	If ConnectSocket( dataSocket, Server_IP, p)
		dataSocketStream = CreateSocketStream(dataSocket, True)
		dataStream       = OpenStream(dataSocketStream)
		If dataStream
			fileStream=WriteStream(filename)
			CopyStream(dataSocketStream,fileStream)
			messageSys "succesfully received file "+filename
		Else
			messageSys "stream failed"
		EndIf
	Else
		messageSys "socket failed"
	EndIf
EndFunction


Function sendDataAsFile(p:Int, filename:String)
	Local dataSocket:TSocket = CreateTCPSocket()
	Local dataSocketStream:TSocketStream
	Local dataStream:TStream
	Local fileStream:TStream
	
	messageSys "trying to send some data on port " + String(p)
	If ConnectSocket( dataSocket, Server_IP, p)
		dataSocketStream = CreateSocketStream(dataSocket, True)
		dataStream       = OpenStream(dataSocketStream)
		If dataStream
			fileStream=ReadStream(filename)
			CopyStream(fileStream, dataSocketStream)
			messageSys "succesfully transfered file "+filename
		Else
			messageSys "stream failed"
		EndIf
	Else
		messageSys "socket failed"
	EndIf
EndFunction



Function _stringSplit:String[](text:String, separator:String)
	Local splitArray:String[]
	Local fieldCount:Int = 1
	
	' how many elements ?
	Local loc:Int = text.find(separator)
	While loc >= 0
		loc = text.find(separator, loc + 1)
		fieldCount:+1
	Wend
	
	' set the array with the calculated size
	splitArray = New String[fieldCount]
	
	fieldcount = 0
	While True
		loc = text.find(separator)
		If loc >= 0 Then
			splitArray[fieldCount] = text[..loc]
			text = text[loc+1..]
		Else
			splitArray[fieldCount] = text
			Exit
		End If
		fieldCount:+1
	Wend
	
	Return splitArray
End Function

Function getPort(tempStr:String)
	Local tempArray:String[]
	tempArray = _stringSplit(tempStr, "(" )
	
	tempStr = tempArray[Len(tempArray) - 1]
	tempStr= tempStr[0..Len(tempStr)-1]
	tempArray= _stringSplit(tempStr, "," )
	
	Return Int(tempArray[Len(tempArray) - 2])*256 + Int(tempArray[Len(tempArray) - 1])
EndFunction

Function checkResult(s:String)
	If s = "331" And currentLine[0..Len(s)] = "220"
		' if we dont look for 220, get the rest of them
		While currentLine[0..Len(s)] = "220"
			messageDebug "receiving more to get rid of the 220's"
			receiveLineFromServer()
		Wend
	EndIf

	If currentLine[0..Len(s)] <> s 
		messageDebug "check ("+s+") is wrong, got "+currentLine[0..Len(s)]+" ...aborting"
		isAborting = True
	Else
		messageDebug "check is ok ("+s+")."
		isAborting = False
	EndIf
EndFunction

Function sendQuit()
	send("quit")
EndFunction

Function sendCwd(foldername:String)
	If Not isAborting
		send("cwd "+foldername)
		checkResult("250")
	Else
		messageDebug "... aborting sendCwd "+foldername
	EndIf
EndFunction

Function sendRename(oldFilename:String, newFilename:String)
	If Not isAborting
		send("rnfr "+oldFilename)
		checkResult("350")
	Else
		messageDebug "... aborting sendRename "+oldFilename
	EndIf
	If Not isAborting
		send("rnto "+newFilename)
		checkResult("250")
	Else
		messageDebug "... aborting sendRename "+newFilename
	EndIf
EndFunction

Function sendCredentials()
	If Not isAborting
		send("user " + Server_User)
		checkResult("331")
		send("pass " + Server_pwd)
		checkResult("230")
		send("type i")
		checkResult("200")
	Else
		messageDebug "... aborting sendCredentials"
	EndIf
EndFunction

Function sendList()
	If Not isAborting
		pasvPort = getPort(send("pasv"))
		checkResult("227")
		writeToServer("list")
		getDataAsText(pasvPort)
		receiveLineFromServer()
		checkResult("150")
		receiveLineFromServer
		checkResult("226")
	Else
		messageDebug "... aborting sendlist"
	EndIf
EndFunction

Function sendGetData(filename:String)
	If Not isAborting
		pasvPort = getPort(send("pasv"))
		checkResult("227")
		writeToServer("retr "+filename)
		getDataAsFile(pasvPort, filename)
		receiveLineFromServer()
		checkResult("150")
		receiveLineFromServer
		checkResult("226")
	Else
		messageDebug "... aborting sendgetdata"
	EndIf
EndFunction

Function sendStoreData(filename:String)
	If Not isAborting
		pasvPort = getPort(send("pasv"))
		checkResult("227")
		writeToServer("stor "+filename)
		sendDataAsFile(pasvPort, filename)
		receiveLineFromServer()
		checkResult("150")
		receiveLineFromServer
		checkResult("226")
	Else
		messageDebug "... aborting sendstoredata"
	EndIf
EndFunction

Function main()
	
	' CHANGE THIS!
	Server_Host = "localhost"
	Server_IP   = HostIp(Server_Host)
	Server_Port = 21
	Server_User = "user"
	Server_pwd  = "password"
	
		
	If Server_IP = 0
		messageSys "could not resolve servername ... are you online??"
	EndIf
		
	If Server_Stream CloseStream(Server_SocketStream)
	If Server_Socket CloseSocket(Server_Socket)

	Server_Socket = CreateTCPSocket()
	
	If ConnectSocket( Server_Socket, Server_IP, Server_Port)
		Server_SocketStream = CreateSocketStream(Server_Socket, True)
		Server_Stream       = OpenStream(Server_SocketStream)
		messageSys "connect ok"
		isConnected = True
		receiveLineFromServer()
		checkResult("220")
	Else
		message "couldnt connect to " +  Server_Host
		isConnected = False
	EndIf
	
	If isConnected

		sendCredentials()
		sendList()
		sendCwd("files")

		sendRename("ddd.mp3", "ccc.mp3")

		sendRename("ccc.mp3", "ddd.mp3")

		sendList()
		sendGetData("ddd.mp3")

		sendStoreData("test.mp3")

		sendQuit()
	EndIf
EndFunction

main
