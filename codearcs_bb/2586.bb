; ID: 2586
; Author: GIB3D
; Date: 2009-09-22 12:33:36
; Title: BlitzPlay Lite + eNet
; Description: The easiness of BlitzPlay with the functionality of eNet

;=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-Constants=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
;------These are the values that JoinSession will return
Const BP_NOREPLY = 0			;No reply from host within 15 seconds
Const BP_IAMBANNED = 1			;Local player's IP has been banned
Const BP_GAMEISFULL = 2		;The game has maxed out players
Const BP_PORTNOTAVAILABLE = 3	;The local port wasn't available
Const BP_SUCCESS = 4			;The game was joined!
Const BP_USERABORT = 5			;The user pushed ESC while joining
Const BP_INVALIDHOSTIP = 6		;The IP used for the Host was invalid
;------These are all the messages BP can generate for the end user.
Const BP_PLAYERHASJOINED = 255	;msgData = new player name|msgFrom = player's id
Const BP_PLAYERHASLEFT = 254	;msgData = T/F on if intentionally|msgFrom = player's id
Const BP_HOSTHASLEFT = 253		;msgData = T/F on if intentionally|msgFrom = old host's id
Const BP_PLAYERWASKICKED = 252	;msgData = null|msgFrom = kicked player's id

;=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=Globals=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
;---------------The following shouldn't be modified externally------------------
;**Although these ones might be useful to you**
Global BP_Bank = CreateBank(128)
Global BP_Bank_PeerID = CreateBank(4)
Global BP_Bank_DataSize = CreateBank(4)

Global BP_GameType			    ;Type of game. (0-255)
Global BP_TotRecvPacket		;Packets received
Global BP_TotSentPacket		;Packets sent
Global BP_Host				    ;T/F, am I the Host?
Global BP_Host_ID				;Host's ID #
Global BP_Host_IP$				;Host's IP; (in integer format) not anymore
Global BP_Host_Port			;Host's Port
Global BP_MaxPlayers = 255 	;Maximum # of players
Global BP_My_Name$				;Local player's Name
Global BP_My_ID				;Local player's ID #
Global BP_My_Port				;Local port being used by BlitzPlay
Global BP_NumPlayers			;How many players are connected to the game right now
Global BP_LocalHost = BP_ConvertIp ("127.0.0.1");integer local loopback address
Global BP_Online = False		;T/F
Global BP_My_IP				;This computers IP. Set to local if no IP
Global BP_LogFile$ = ""			;Define if you want logging enabled.
Global BP_TimeoutPeriod=15000	;How long before we assuming connection dropped(in ms)
Global BP_Log					;Log file handle, 0 if logging disabled
Global BP_AutoLogging			;True or False on if BP should internally do the logging
Global BP_UDPdebug			    ;Odds (in %) that packets do NOT get sent (for testing)
;**These ones probably not as useful..
Global BP_UDP_Stream			;UDP Stream handle
Global BP_CompressBank = CreateBank(4);Bank used for converting Floats to Strings
;=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=Types=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
Type NetInfo					;--The general player info.
	Field Name$				;Player's Name
	Field Net_id%			;Players unique ID #
	Field eNet_id			;eNet uses a different ID system
	Field LastHeard%		;When the last packet was received from them.
	Field Alive%			;Boolean on if we think this player is still there
End Type

Type MsgInfo					;--Messages that have arrived are stored here
	Field msgData$			;actual packet contents
	Field msgType%			;Msg type(0-255)
	Field msgFrom%			;ID of msg sender
End Type

Type DiscID						;--Keeps track of disconnects' ID's
	Field id%				;ID of disconnect
End Type

Type Connecting					;--For players trying to connect
	Field Name$				;Connect name
	Field Net_id%			;new player's ID
	Field eNet_id			;eNet uses a different ID system
	Field LastHeard%		;When the last packet was received from them.
	Field Alive%			;Boolean on if we think this new connect is still trying
End Type

Type UnrecMsgQueue				;--Messages from unrecognized IP+Port
	Field msgData$			;Hm. Self explanatory.
	Field msgType%
	Field Time%				;These will be stored for up to 1 second then disregarded
	Field Net_id%
	Field eNet_id			;eNet uses a different ID system
End Type

;=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=Functions=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
Function BP_ClearSession()
;-=-=-=Clears out any data that could be leftover from previous sessions.
	If First NetInfo<>Null Then Delete Each NetInfo
	If First UnrecMsgQueue<>Null Then Delete Each UnrecMsgQueue
	If First Connecting<>Null Then Delete Each Connecting
	If First MsgInfo<>Null Then Delete Each MsgInfo
	If First DiscID<>Null Then Delete Each DiscID
	BP_TotSentPacket = 0
	BP_TotRecvPacket = 0
	BP_StopLogFile()
	If BP_Online ENetDeInitialize()
End Function

Function BP_ConvertIp(IP$)
;-=-=-=Convert an IP from x.x.x.x to integer format.
	Local dot1 = Instr(IP$,".")
	Local dot2 = Instr(IP$,".",dot1+1)
	Local dot3 = Instr(IP$,".",dot2+1)
	Local Octet1% = Mid$(IP$,1,dot1-1)
	Local Octet2% = Mid$(IP$,dot1+1,dot2-1)
	Local Octet3% = Mid$(IP$,dot2+1,dot3-1)
	Local Octet4% = Mid$(IP$,dot3+1)
	Return (((((Octet1 Shl 8) Or Octet2) Shl 8) Or Octet3) Shl 8) Or Octet4
End Function

Function BP_ConvertDomain(domain$)
;-=-=-=Converts from www.domain.com to integer IP address.
	Return HostIP(CountHostIPs (domain))
End Function

Function BP_EndSession()
;-=-=-=Disconnect from everything
	If BP_Online
		If BP_Host Then
			BP_UDPMessage(0,251,Chr(BP_My_ID))
		Else
			BP_UDPMessage(BP_Host_ID,253,Chr(BP_My_ID))
		EndIf
		
		BP_Online = False
	EndIf
	
	BP_UDP_Stream = 0
	BP_ClearSession()
	BP_StopLogFile()
End Function

Function BP_FindConnect.Connecting(id,enet=False)
;-=-=-=Go through the Connecting type list and search by IP+Port
	Local a.Connecting
	
	For a.Connecting = Each Connecting
		If enet
			If a\eNet_id = id Then Return a
		Else
			If a\Net_id = id Then Return a
		EndIf
	Next
End Function

Function BP_FindID.NetInfo(id,enet=False)
;-=-=-=Go through the NetInfo type list and find a specific instance, based on the ID
	Local a.NetInfo
	
	For a.NetInfo = Each NetInfo
		If enet
			If a\eNet_id = id Then Return a
		Else
			If a\Net_id = id Then Return a
		EndIf
	Next
End Function

Function BP_FloatToStr$(num#)
;-=-=-=Convert a floating point number to a 4 byte string
	Local st$ = "",i%
	PokeFloat BP_CompressBank,0,num
	For i = 0 To 3 
		st = st + Chr(PeekByte(BP_CompressBank,i))
	Next 
	Return st
End Function

Function BP_GetGameType()
;-=-=-=Returns the currently set game type
    Return BP_GameType%
End Function

Function BP_GetHostID()
;-=-=-=Returns the Host ID
    Return BP_Host_ID%
End Function

Function BP_GetHostIP$()
;-=-=-=Returns the Host IP address
    Return BP_Host_IP
End Function

Function BP_GetHostPort()
;-=-=-=Returns the Host Port
    Return BP_Host_Port%
End Function

Function BP_GetLogFileName$()
;-=-=-=Returns the currently set Log File name
    Return BP_LogFile$
End Function

Function BP_GetMaxPlayers()
;-=-=-=Returns the currently set Max Players value
    Return BP_MaxPlayers%
End Function

Function BP_GetMyID()
;-=-=-=Returns this users ID
    Return BP_My_ID%
End Function

Function BP_GetMyIP$()
;-=-=-=Returns this users IP address
	Local ip%
	If CountHostIPs ("") Then ip = HostIP(CountHostIPs("")) Else ip = BP_LocalHost
	Return DottedIP$(ip)
End Function

Function BP_GetMyName$()
;-=-=-=Returns this users name
	Return BP_My_Name$
End Function

Function BP_GetMyPort()
;-=-=-=Returns this users Port
	Return BP_My_Port%
End Function

Function BP_GetNumberOfPlayers()
;-=-=-=Returns the current number of players
	Return BP_NumPlayers%
End Function

Function BP_GetPacketsReceived()
;-=-=-=Returns the number of packets that have been received
	Return BP_TotRecvPacket%
End Function

Function BP_GetPacketsSent()
;-=-=-=Returns the number of packets that have been sent
	Return BP_TotSentPacket%
End Function

Function BP_GetPlayerName$(ID)
;-=-=-=Find a player's name based on the ID
    Local a.NetInfo
	
	For a.NetInfo = Each NetInfo
		If a\Net_id = ID Then Return a\Name$
	Next
End Function

Function BP_GetTimeoutPeriod()
;-=-=-=Returns the current Timeout Period
    Return BP_TimeoutPeriod / 1000
End Function

Function BP_HostSession(HostName$,MaxPlayers%,GameType%,LocalPort%,TimeoutPeriod%)
;-=-=-=Host the game
;First clear out any left over data from a previous session
	BP_ClearSession()
	ENetInitialize()
;Now initialize the Host information and open the specified port.
	BP_NumPlayers = 1
	BP_MaxPlayers = MaxPlayers
	BP_Host = True
	BP_My_IP = BP_ConvertIp(BP_GetMyIP())
	BP_My_Port = LocalPort
	BP_Host_Port = BP_My_Port
	BP_Host_ID = 1
	BP_Host_IP = BP_My_IP
	Local nInfo.NetInfo = New NetInfo
	nInfo\Name = HostName
	BP_My_Name = HostName
	BP_My_ID = 1
	nInfo\Net_id = 1
	nInfo\eNet_id = -1
	
	BP_UDP_Stream = ENetCreate(True,BP_My_IP,BP_My_Port,BP_MaxPlayers,0,0)
	
;And set up the game information
	BP_GameType = GameType
	BP_TimeoutPeriod = TimeoutPeriod * 1000 ;TimeoutPeriod is converted to milliseconds
	
	If BP_UDP_Stream Then
		BP_Online = True
	Else
		BP_Online = False
		ENetDeInitialize()
	EndIf
	
	Return BP_Online
End Function

Function BP_IntToStr$(Num%, StrLen% = 4)
;-=-=-=Take an Integer and compress it to a string, of "strlen" bytes long.
	Local shiftin%
	Local st$ = Chr(Num And 255)
	For shiftin = 1 To (StrLen - 1)
		st$ = st$ + Chr(Num Sar (8 * shiftin))
	Next
	Return st
End Function 

Function BP_JoinSession(ClientName$,HostPort%,strHostIP$)
;-=-=-=Join a game already in progress
;JoinSession will return:
;0=No reply from BP_Host
;1=This IP is banned
;2=Game is full
;3=Local port not available
;4=Joined game!

;Also notice the constants which coincide w/ these values:
;BP_NOREPLY, BP_IAMBANNED, BP_GAMEISFULL, BP_PORTNOTAVAILABLE, BP_SUCCESS
	DebugLog strHostIP
	Local msg.MsgInfo,nInfo.NetInfo
	Local Starttime
	Local Reason
	Local Counter
	Local IntHostIP
	Local Message$
	
;Clear out any left over data from a previous session and initialize this session
	BP_ClearSession()
	
	;Directly convert the Host's IP into an integer, to test to see if the IP entered is simply a number (w/ no periods)
	IntHostIP = strHostIP
	If IntHostIP Then	;First, error check for valid IP's
		If Not(Instr(strHostIP, ".")) Then
			BP_UpdateLog ("Connection attempt aborted. Host IP is invalid.")
			Return BP_INVALIDHOSTIP%
		EndIf
	EndIf
	
	;Now, convert the IP/Domain to an integer
	IntHostIP = BP_DotToInt(strHostIP);BP_ConvertDomain(strHostIP)
	BP_UpdateLog ("New connection attempt for " + ClientName + " on Port " + HostPort + ". Server: " + DottedIP(IntHostIP) + ":" + HostPort)

	;Error check again
	If Not(IntHostIP) Then
		BP_UpdateLog ("Connection attempt aborted. Host IP is invalid.")
		Return BP_INVALIDHOSTIP%
	EndIf

	BP_Host = False
	BP_My_IP = BP_ConvertIp(BP_GetMyIP())
	;BP_My_Port = LocalPort
	BP_Host_IP = strHostIP;IntHostIP
	BP_Host_Port = HostPort
	
	;Start connecting with BP_Host
	ENetInitialize()
	BP_UDP_Stream = ENetCreate(False,"",0,0,0,0)
	
	If BP_UDP_Stream Then
		DebugLog "BP_Host_IP = " + BP_Host_IP
		
		Local Connect = ENetConnect(BP_Host_IP,HostPort)
		If Connect
			DebugLog "Connected = " + Connect
			Message = Chr(254) + Chr(0) + Chr(1) + ClientName
			DebugLog "Message = " + (Chr(254) + Chr(0) + Chr(1) + ClientName)
			ENetSendData(Message,Len(Message),0,True)
			
			BP_NumPlayers = 255
			
			Starttime = MilliSecs()
			
			Reason = 0
			BP_Online = True
			
			;Receive info on game session as well as other player information
			Repeat
				BP_UpdateNetwork ()
				
				If (MilliSecs() - Starttime) > 15000 Then
					BP_Online = False
					Exit
				EndIf
				
				For msg.MsgInfo = Each MsgInfo
					If msg\msgType = 256 Then
						Reason = msg\msgData
						BP_Online = False
						Exit
					EndIf
				Next
				
				Counter = 0
				
				For nInfo.NetInfo = Each NetInfo
					Counter = Counter + 1
				Next
				
				If Counter = BP_NumPlayers Then
					Reason = BP_SUCCESS
					BP_Online = True
					Exit
				EndIf
				
				If KeyHit(1) Then
					Reason = BP_USERABORT
					BP_Online = False
				EndIf
				
			Until Reason
			
			If BP_Online Then
				nInfo.NetInfo = New NetInfo
				nInfo\Name = ClientName
				nInfo\Net_id = BP_My_ID
				nInfo\Alive = True
				
				BP_My_Name$ = ClientName
				Message = Chr(254) + Chr(0) + Chr(2)
				ENetSendData(Message,Len(Message),0,True)
				
				BP_NumPlayers = BP_NumPlayers + 1
			Else
				ENetDeInitialize()
				BP_UDP_Stream = 0
			EndIf
		Else
			BP_Online = False
			Reason = BP_NOREPLY
			ENetDeInitialize()
		EndIf
	Else
		BP_Online = False
		Reason = BP_PORTNOTAVAILABLE
		ENetDeInitialize()
	EndIf
	
	Return Reason
End Function

Function BP_KickID(id%, ban% = False)
;-=-=-=Kick an ID, maybe even ban 'em
	Local nInfo.NetInfo
	Local msg.MsgInfo
	
	If BP_My_ID = BP_Host_ID Then
		nInfo.NetInfo = BP_FindID (id)
		If nInfo<>Null And id <> BP_My_ID Then
			BP_UDPMessage (0, 249, Chr(id)+Chr(ban))
			msg.MsgInfo = New MsgInfo
			msg\msgType = 252
			msg\msgFrom = id
			msg\msgData = ban
;			If ban Then
;				BP_BanIP (nInfo\IP)
;				If BP_Log Then BP_UpdateLog ("You banned: " + nInfo\Name)
;			Else
;				If BP_Log Then BP_UpdateLog ("You kicked: " + nInfo\Name)
;			EndIf
			Delete nInfo
			BP_NumPlayers = BP_NumPlayers - 1
		EndIf
	EndIf
End Function

Function BP_NextAvailID%()
;-=-=-=Find out the Next available ID # that is Not in use
	Local a.NetInfo
	Local testing
	Local foundit
	Local temp_array[256]
	
	For a.NetInfo = Each NetInfo
		temp_array[a\Net_id] = True
	Next

	For testing = 1 To BP_MaxPlayers
		If Not temp_array[testing] Then
			foundit = testing
			Exit
		EndIf
	Next
	Return foundit
End Function

Function BP_SetGameType(GameType%)
;-=-=-=Allows the user to control the numeric game type value
	If BP_My_ID = BP_Host_ID Then
	    BP_GameType% = GameType%
		BP_UDPMessage (0,248,"1"+GameType)
	EndIf
End Function

Function BP_SetMaxPlayers(MaximumPlayers%)
;-=-=-=Allows the user to control the maximum allowable players
	If (BP_My_ID = BP_Host_ID) Then
		If MaximumPlayers > 255 Then MaximumPlayers = 255
		If MaximumPlayers < 0 Then MaximumPlayers = 0
	    BP_MaxPlayers% = MaximumPlayers%
		BP_UDPMessage (0,248,"2"+MaximumPlayers)
	EndIf
End Function

Function BP_SetTimeoutPeriod(TimeoutPeriod%)
;-=-=-=Allows the user to set or change the TimeoutPeriod value
    BP_TimeoutPeriod% = TimeoutPeriod%
End Function

Function BP_SimulatePacketLoss(Odds%)
;-=-=-=Allows the user to control simulated packet loss
    BP_UDPdebug = Odds%
End Function

Function BP_StartLogFile(FileName$, Append% = True, Automatic% = True)
;-=-=-=Opens up the log file. Also, optionally appends to the file instead of overwriting.
;Will only start a log if there isn't one already, and if the filename is valid.
	If (Len(FileName$) > 0) And (BP_Log = 0) Then
		BP_LogFile$ = FileName$
		BP_AutoLogging = Automatic
		;Check to see if the file exists already
		If FileType(FileName$) = 1 Then
			Select Append	;If it does, check to see if we're going to append or overwrite
				Case True
					BP_Log = OpenFile (FileName$)
					SeekFile (BP_Log, FileSize(FileName$))
					WriteLine BP_Log, ""
				Case False
					DeleteFile FileName$
					BP_Log = WriteFile (FileName$)
			End Select
		Else
			BP_Log = WriteFile (FileName$)
		EndIf			
		;Now that the file is opened, insert the header information
		WriteLine BP_Log, "**Logging enabled at " + CurrentTime$() + " for " + BP_GetMyName() + "."
		If BP_Online Then
			WriteLine BP_Log, "Connection Status: Online  Local IP/Port = " + DottedIP$(BP_GetMyIP()) + "/" + BP_GetMyPort() + "  Host IP/Port = " + DottedIP$(BP_GetHostIP()) + "/" + BP_GetHostPort()
		Else
			WriteLine BP_Log, "Connection Status: Offline  Local IP/Port = " + DottedIP$(BP_GetMyIP()) + "/" + BP_GetMyPort() + "  Host IP/Port = " + DottedIP$(BP_GetHostIP()) + "/" + BP_GetHostPort()
		EndIf
		WriteLine BP_Log, "Current Session Stats: GameType = " + BP_GetGameType() + "  NumPlayers = " + BP_GetNumberOfPlayers() + "  Local ID/Host ID = " + BP_GetMyID() + "/" + BP_GetHostID()
	EndIf
End Function

Function BP_StopLogFile()
;-=-=-=Allows the user to stop the logfile
	If BP_Log
		WriteLine BP_Log, "**Logging stopped at " + CurrentTime$() + "."
		CloseFile BP_Log
		BP_Log = 0
	EndIf
	BP_AutoLogging = False
End Function

Function BP_StrToInt%(st$)
;-=-=-=Take a String of any length and turn it into an integer again.
	Local shiftin%
	Local num%
	For shiftin = 0 To (Len (st$) - 1)
		num = num Or (Asc (Mid$ (st$, shiftin + 1, 1)) Shl shiftin * 8)
	Next
	Return num
End Function

Function BP_StrToFloat#(st$)
;-=-=-=Take a 4 byte string and turn it back into a floating point #.
	Local num#,i%
	For i = 0 To 3
		PokeByte BP_CompressBank,i,Asc(Mid$(st$,i+1,1))
	Next
	num# = PeekFloat(BP_CompressBank,0)
	Return num
End Function

Function BP_DotToInt%(ip$)
	Local off1=Instr(ip$,".")	  :Local ip1=Left$(ip$,off1-1)
	Local off2=Instr(ip$,".",off1+1):Local ip2=Mid$(ip$,off1+1,off2-off1-1)
	Local off3=Instr(ip$,".",off2+1):Local ip3=Mid$(ip$,off2+1,off3-off2-1)
	Local off4=Instr(ip$," ",off3+1):Local ip4=Mid$(ip$,off3+1,off4-off3-1)
	Return ip1 Shl 24 + ip2 Shl 16 + ip3 Shl 8 + ip4
End Function

Function BP_UpdateNetwork()		;This is the -meat- of the library.
	;Host mostly uses the eNet ID system for checking clients
	;Client only uses the BlitzPlay ID system for checking
	
	
;-=-=-=Check for messages, disconnects, new players, and UDP resends.
	;First lets get the variables defined as local to this function only
	Local CurTime
	Local MsgFrom,eNetID
	Local MsgType
	Local MsgTarget
	Local MsgToSend$
	Local MsgData$
	Local Counter
	Local Allowed
	Local KickedID
	Local Event
	
;***Check UDP Messages first
	If BP_Online
		If BP_Host
			;[Block] Host's UpdateNetwork
			
			While( ENetDoEventCheck(0) > 0 )
				Event = ENetCheckEvents(BP_Bank_PeerID,BP_Bank_DataSize,BP_Bank)
				
				If Event = 3
					CurTime = MilliSecs ()
					BP_TotRecvPacket = BP_TotRecvPacket + 1
					
					eNetID = PeekInt(BP_Bank_PeerID,0)
					;DebugLog "MsgData1 = " + MsgData
					MsgData = BP_ReturnMessage(BP_Bank,BP_Bank_DataSize)
					MsgType = Asc(Mid(MsgData,1,1))
					MsgTarget = Asc(Mid(MsgData,2,1))
					MsgData = Mid(MsgData,3,Len(MsgData)-2)
					
					;DebugLog "eNetID = " + eNetID
					;DebugLog "MsgData2 = " + MsgData
					;DebugLog "MsgType = " + MsgType
					
					Local nInfo.NetInfo = BP_FindID(eNetID,True)
					If nInfo<>Null Then nInfo\LastHeard = CurTime : nInfo\Alive = True : MsgFrom = nInfo\Net_id;Make sure we don't timeout
					
					;DebugLog "MsgFrom = " + MsgFrom
					
					Select MsgType
						Case 255			;If it was a keep alive packet
							If nInfo <> Null Then
								nInfo\LastHeard = CurTime
								nInfo\Alive = True
							EndIf
							
						Case 254			;A packet with connecting info for a new player
							Select Asc(MsgData)
								Case 1
									Local c.Connecting = BP_FindConnect(eNetID,True)
									If c = Null Then	;New join! Time to see if we'll let 'em in
										;check to see that they aren't banned
										;allowed is the code that we assign to this connect
										;1 = banned|2 = no room|4 = allowed!
										Allowed% = BP_SUCCESS
										
										Local ccount.Connecting
										;make sure there's room, counting people in middle of connecting
										Counter = 0
										For ccount.Connecting = Each Connecting
											Counter = Counter + 1
										Next
										If (BP_NumPlayers + Counter) => BP_MaxPlayers Then Allowed = BP_GAMEISFULL
										If Allowed = BP_SUCCESS Then
											c.Connecting = New Connecting
											c\Name = Mid$ (MsgData,2)
											c\Net_id = BP_NextAvailID()
											c\eNet_id = eNetID
											c\LastHeard = CurTime
											MsgToSend = Chr(254) + Chr(BP_My_ID) + Chr(1) + Chr(c\Net_id) + Chr(BP_My_ID) + Chr(BP_NumPlayers) + Chr(BP_MaxPlayers) + Chr(BP_GameType) + Chr(BP_TimeoutPeriod/1000)
											ENetSendData(MsgToSend,Len(MsgToSend),c\eNet_id,True)
											
											For nInfo.NetInfo = Each NetInfo
												MsgToSend = Chr(254) + Chr(BP_My_ID) + Chr(2) +  Chr(nInfo\Net_id) + nInfo\Name
												ENetSendData(MsgToSend,Len(MsgToSend),c\eNet_id,True)
											Next
											If BP_Log Then BP_UpdateLog (c\Name + " is attempting to join the game..")
										Else
											MsgToSend = Chr(254) + Chr(BP_My_ID) + Chr(3) + Chr(Allowed)
											ENetSendData(MsgToSend,Len(MsgToSend),eNetID,True)
										EndIf
									EndIf
								Case 2
									c.Connecting = BP_FindConnect(eNetID,True)
									If c<>Null Then
										Local sendmsg$ = Chr(c\Net_id) + c\Name
										BP_UDPMessage (0,252,sendmsg)
										nInfo.NetInfo = New NetInfo
										nInfo\Name = c\Name
										nInfo\Net_id = c\Net_id
										nInfo\eNet_id = eNetID
										nInfo\LastHeard = CurTime
										nInfo\Alive = True
										Delete c
										Local msg.MsgInfo = New MsgInfo
										msg\msgType = 255
										msg\msgFrom = nInfo\Net_id
										msg\msgData = nInfo\Name
										BP_NumPlayers = BP_NumPlayers + 1
										If BP_Log Then BP_UpdateLog (nInfo\Name + " has joined the game  ID #" + nInfo\Net_id)
									EndIf
							End Select
							
						Case 253			;Someone has left the game
							nInfo.NetInfo = BP_FindID(Asc(MsgData))	;Since this player is the Host, then tell everyone
							If nInfo<>Null Then						;else about it too
								Local disc_id = nInfo\Net_id
								msg.MsgInfo = New MsgInfo
								msg\msgData = True
								msg\msgType = BP_PLAYERHASLEFT
								msg\msgFrom = nInfo\Net_id
								If BP_Log Then BP_UpdateLog (nInfo\Name + " has left the game.")
								Delete nInfo
								BP_UDPMessage(0,253,Chr(disc_id) + Chr(True))
								For c.Connecting = Each Connecting
									MsgToSend = Chr(253) + Chr(BP_My_ID) + Chr(disc_id) + Chr(True)
									ENetSendData(MsgToSend,Len(MsgToSend),c\eNet_id,True)
								Next
								BP_NumPlayers = BP_NumPlayers - 1
							EndIf
							
						Case 252		;Someone has successfully joined the game
						
						Case 251		;The Host has disconnected
							
						Case 250		;This was a "are you still there??" packet from someone.
							MsgToSend = Chr(255) + Chr(BP_My_ID) + "yup"
							ENetSendData(MsgToSend,Len(MsgToSend),eNetID,True)
							
						Case 249		;Someone got kicked
							
						Default			;Nothing internal, a user packet. ***User Message
							Local nInfo2.NetInfo = nInfo
							If nInfo2 <> Null Then	;Do we recognize the sender?
								If MsgTarget <> BP_My_ID Then
									If MsgTarget = 0 Then			;If its a UDP broadcast..
										MsgToSend = Chr(MsgType) + Chr(nInfo2\Net_id) + MsgData
										For nInfo.NetInfo = Each NetInfo
											If nInfo\Net_id <> BP_My_ID And nInfo\Net_id <> nInfo2\Net_id Then
												ENetSendData(MsgToSend,Len(MsgToSend),nInfo\eNet_id,True)
											EndIf
										Next
									Else							;Ah, a specific target.
										MsgToSend = Chr(MsgType) + Chr(nInfo2\Net_id) + MsgData
										nInfo.NetInfo = BP_FindID(MsgTarget)
										ENetSendData(MsgToSend,Len(MsgToSend),nInfo\eNet_id,True)
									EndIf
								EndIf
								
								If MsgTarget = 0 Or MsgTarget = BP_My_ID Then
									msg.MsgInfo = New MsgInfo
									msg\msgData = MsgData
									msg\msgType = MsgType
									msg\msgFrom = nInfo2\Net_id
									nInfo2\LastHeard = CurTime
									nInfo2\Alive = True
									If BP_AutoLogging Then BP_UpdateLog ("[Incoming] From: " + LSet(nInfo2\Name,20) + " Type: " + LSet(MsgType,3) + " {" + MsgData$ + "}")
								EndIf
							Else
								Local msgq.UnrecMsgQueue = New UnrecMsgQueue
								msgq\msgData = MsgData
								msgq\msgType = MsgType
								msgq\Time = CurTime
							EndIf
					End Select
				Else
					Exit			;Ah finally done receiving UDP messages? Outta this loop then!
				EndIf
			Wend
			
			CurTime = MilliSecs ()
			
			If BP_Online		;Have to check again because something *could* have made us offline in prev loop
				;Now look through messages from unrecognized players and see if they've recently joined
				For msgq.UnrecMsgQueue = Each UnrecMsgQueue
					nInfo.NetInfo = BP_FindID(eNetID, True)
					If nInfo <> Null Then
						msg.MsgInfo = New MsgInfo
						msg\msgData = MsgData
						msg\msgType = MsgType
						msg\msgFrom = nInfo\Net_id
						If BP_AutoLogging Then BP_UpdateLog ("[Incoming] From: " + LSet(nInfo\Name,20) + " Type: " + LSet(msg\msgType,3) + " {" + msg\msgData$ + "}")
						Delete msgq
					Else
						If (msgq\Time + 1000) < CurTime Then Delete msgq
					EndIf
				Next
				
				;Check to see who might have been disconnected
				For nInfo.NetInfo = Each NetInfo
					If nInfo\Net_id <> BP_My_ID Then
						If ((nInfo\LastHeard + (BP_TimeoutPeriod / 2)) < CurTime) And (nInfo\Alive) Then	;It's been 5 secs?
							BP_UDPMessage (nInfo\Net_id, 250, "hello?")
							nInfo\Alive = False
							If BP_Log Then BP_UpdateLog (nInfo\Name + " hasn't been heard from in: " + (BP_TimeoutPeriod/1000) + " seconds. Testing to see if still connected.")
						EndIf
						If ((nInfo\LastHeard + BP_TimeoutPeriod) < CurTime) And (nInfo\Alive = False) Then					;It's been 10 secs!?
							disc_id = nInfo\Net_id
							msg.MsgInfo = New MsgInfo
							msg\msgType = BP_PLAYERHASLEFT
							msg\msgFrom = nInfo\Net_id
							msg\msgData = False
							BP_UDPMessage (disc_id,249,Chr(disc_id))
							If BP_Log Then BP_UpdateLog (nInfo\Name + " has lagged out of the game.")
							Delete nInfo
							BP_UDPMessage (0,253,Chr(disc_id) + Chr(False))
							BP_NumPlayers = BP_NumPlayers - 1
						EndIf
					EndIf
				Next
				;Scan through the list of people connecting, and see if we haven't heard from them in 10 secs		
				For c.Connecting = Each Connecting
					If (c\LastHeard + BP_TimeoutPeriod) < CurTime Then						;It's been 10 secs!?
						If BP_Log Then BP_UpdateLog (c\Name + " didn't reply fast enough. Deleting from connecting queue.")
						Delete c
					EndIf
				Next
			EndIf
			;[End]
		Else
			;[Block] Client's UpdateNetwork
			While( ENetDoEventCheck(0) > 0 )
				Event = ENetCheckEvents(BP_Bank_PeerID,BP_Bank_DataSize,BP_Bank)
				
				;DebugLog "Event = " + Event
				
				If Event = 3
					CurTime = MilliSecs ()
					BP_TotRecvPacket = BP_TotRecvPacket + 1
					
					eNetID = 0 ; Host
					MsgData = BP_ReturnMessage(BP_Bank,BP_Bank_DataSize)
					MsgType = Asc(Mid(MsgData,1,1))
					MsgFrom = Asc(Mid(MsgData,2,1))
					MsgData = Mid(MsgData,3,Len(MsgData)-2)
					
					CurTime = MilliSecs ()
					nInfo.NetInfo = BP_FindID (BP_Host_ID)
					If nInfo<>Null Then nInfo\LastHeard = CurTime:nInfo\Alive = True
					BP_TotRecvPacket = BP_TotRecvPacket + 1
					;Msg will be in format: 123 1=Type|2=Sender|3=Data
					
					;DebugLog "MsgData = " + MsgData
					;DebugLog "MsgType = " + MsgType
					;DebugLog "MsgFrom = " + MsgFrom
					
					Select MsgType
						Case 255			;If it was a keep alive packet..
							nInfo.NetInfo = BP_FindID(MsgFrom)
							If nInfo<>Null Then
								nInfo\LastHeard = CurTime
								nInfo\Alive = True
							EndIf
							
						Case 254			;A packet with connecting info for a new player
							Select Asc(MsgData)
								Case 1
									BP_My_ID = Asc(Mid$(MsgData,2))
									BP_Host_ID = Asc(Mid$(MsgData,3))
									BP_NumPlayers = Asc(Mid$(MsgData,4))
									BP_MaxPlayers = Asc(Mid$(MsgData,5))
									BP_GameType = Asc(Mid$(MsgData,6))
									BP_TimeoutPeriod = Asc(Mid$(MsgData,7)) * 1000
									
								Case 2
									nInfo.NetInfo = BP_FindID(Asc(Mid$(MsgData,2)))
									If nInfo=Null Then
										MsgData = Mid$(MsgData,2)
										nInfo.NetInfo = New NetInfo
										nInfo\Net_id = Asc(Mid$(MsgData,1))
										nInfo\Name = Mid$(MsgData,2)
										nInfo\Alive = True
										nInfo\LastHeard = CurTime
										msg.MsgInfo = New MsgInfo
										msg\msgType = 255
										msg\msgFrom = nInfo\Net_id
										msg\msgData = nInfo\Name
									EndIf
									
								Case 3
									Local reason% = Asc(Mid$(MsgData,2,1))
									msg.MsgInfo = New MsgInfo
									msg\msgType = 256
									msg\msgFrom = 0
									msg\msgData = reason
									
							End Select
							
						Case 253			;Someone has left the game
							nInfo.NetInfo = BP_FindID(Asc(MsgData))	;If they're still in the game
							If nInfo<>Null Then
								msg.MsgInfo = New MsgInfo			;And make a new msg about it
								msg\msgData = Asc(Mid$(MsgData,2))
								msg\msgType = 254
								msg\msgFrom = nInfo\Net_id
								Delete nInfo
								BP_NumPlayers = BP_NumPlayers - 1
								If BP_Log Then
									If msg\msgData Then BP_UpdateLog (nInfo\Name + " has left the game.") Else BP_UpdateLog (nInfo\Name + " has lagged out of the game." + nInfo\Net_id)
								EndIf
							EndIf
							
						Case 252		;Someone has successfully joined the game
							CurTime = MilliSecs ()
							nInfo.NetInfo = New NetInfo
							nInfo\Net_id = Asc(Mid$(MsgData,1))
							nInfo\eNet_id = MsgFrom
							nInfo\Name = Mid$(MsgData,2)
							nInfo\LastHeard = CurTime
							nInfo\Alive = True
							BP_NumPlayers = BP_NumPlayers + 1
							msg.MsgInfo = New MsgInfo
							msg\msgData = nInfo\Name
							msg\msgType = 255
							msg\msgFrom = nInfo\Net_id
							If BP_Log Then BP_UpdateLog (nInfo\Name + " has joined the game w/ ID #" + nInfo\Net_id)
							
						Case 251		;The Host has disconnected
							nInfo.NetInfo = BP_FindID(Asc(MsgData))
							If nInfo<>Null Then
								BP_Online = False
								ENetDeInitialize()
								BP_UDP_Stream = 0
								msg.MsgInfo = New MsgInfo
								msg\msgType = 253
								msg\msgFrom = nInfo\Net_id
								msg\msgData = True
								If First NetInfo<>Null Then Delete Each NetInfo
								BP_NumPlayers = 0
							EndIf
							If BP_Log Then BP_UpdateLog ("The Host ended the session.")
							
						Case 250		;This was a "are you still there??" packet from someone.
							MsgToSend = Chr(255) + Chr(BP_My_ID) + "yup"
							ENetSendData(MsgToSend,Len(MsgToSend),0,True)
							
						Case 249		;Someone got kicked
							KickedID = Asc(MsgData)
							nInfo.NetInfo = BP_FindID(KickedID)
							If nInfo <> Null Then
								msg.MsgInfo = New MsgInfo
								msg\msgType = 252
								msg\msgFrom = nInfo\Net_id
								msg\msgData = Asc(Mid$(MsgData,2))
								
								If BP_Log Then
									If msg\msgData Then BP_UpdateLog (nInfo\Name + " was banned") Else BP_UpdateLog (nInfo\Name + " was kicked")
								EndIf
								
								If KickedID = BP_My_ID Then
									If First NetInfo<>Null Then Delete Each NetInfo
									BP_Online = False
									ENetDeInitialize()
									BP_UDP_Stream = 0
									BP_NumPlayers = 0
									Return
								Else
									Delete nInfo
									BP_NumPlayers = BP_NumPlayers - 1
								EndIf
							EndIf
							
						Case 248
							Select Asc(MsgData)
								Case 1
									BP_GameType = Asc(Mid$(MsgData,2))
								Case 2
									BP_MaxPlayers = Asc(Mid$(MsgData,2))
							End Select
							
						Default			;Nothing 'special'
							nInfo.NetInfo = BP_FindID(MsgFrom)
							CurTime = MilliSecs ()
							If nInfo <> Null Then
								msg.MsgInfo = New MsgInfo
								msg\msgData = MsgData
								msg\msgType = MsgType
								msg\msgFrom = MsgFrom
								nInfo\LastHeard = CurTime
								nInfo\Alive = True
								If BP_AutoLogging Then BP_UpdateLog ("[Incoming] UDP From: " + LSet(nInfo\Name,20) + " Type: " + LSet(MsgType,3) + " {" + MsgData$ + "}")
							Else
								msgq.UnrecMsgQueue = New UnrecMsgQueue
								msgq\msgData = MsgData
								msgq\msgType = MsgType
								msgq\Net_id = MsgFrom
								msgq\Time = CurTime
							EndIf
							
					End Select
				Else
					Exit	;Ah finally done receiving messages? Outta this loop then!
				EndIf
			Wend
			
			If BP_Online		;Have to check again because something *could* have made us offline in prev loop
				CurTime = MilliSecs ()
				;Now look through messages from unrecognized players and see if they've recently joined
				For msgq.UnrecMsgQueue = Each UnrecMsgQueue
					nInfo.NetInfo = BP_FindID(msgq\Net_id)
					If nInfo <> Null Then
						msg.MsgInfo = New MsgInfo
						msg\msgData = MsgData
						msg\msgType = MsgType
						msg\msgFrom = nInfo\Net_id
						If BP_AutoLogging Then BP_UpdateLog ("[Incoming] From: " + LSet(nInfo\Name,20) + " Type: " + LSet(msg\msgType,3) + " {" + msg\msgData$ + "}")
						Delete msgq
					Else
						If (msgq\Time + 1000) < CurTime Then Delete msgq
					EndIf
				Next		
				
				;Check for disconnection from Host
				nInfo.NetInfo = BP_FindID (BP_Host_ID)
				If nInfo<>Null
					If ((nInfo\LastHeard + (BP_TimeoutPeriod/2)) < CurTime) And (nInfo\Alive) Then	;It's been awhile..
						BP_UDPMessage (nInfo\Net_id, 250, "hello?")
						nInfo\Alive = False
					EndIf
					If ((nInfo\LastHeard + BP_TimeoutPeriod) < CurTime) And (nInfo\Alive = False) Then	;It's been BP_TimeoutPeriod secs!?
						BP_Online = False
						ENetDeInitialize()
						BP_UDP_Stream = 0
						msg.MsgInfo = New MsgInfo
						msg\msgType = 253
						msg\msgFrom = nInfo\Net_id
						Delete Each NetInfo
						BP_NumPlayers = 0
						If BP_Log Then BP_UpdateLog ("Host hasn't replied in " + (BP_TimeoutPeriod/1000) + " seconds, game ended")
					EndIf
				EndIf;If nInfo<>Null
			EndIf;If BP_Online
			;[End]
		EndIf
	EndIf
End Function

Function BP_UDPMessage(msgTarget%, msgType%, msgData$,reliable=True)
	Local a.NetInfo
;-=-=-=Prepare a UDP message to send.
	If BP_Online
	;Insert the message type
		If BP_Host
			If msgTarget = 0 Then			;If its a UDP broadcast..
				msgData = Chr(msgType) + Chr(BP_My_ID) + msgData
				For a.NetInfo = Each NetInfo
					If a\Net_id <> BP_My_ID Then
						If BP_AutoLogging Then BP_UpdateLog ("[Outgoing] UDP   To: " + LSet(a\Name,20) + " Type: " + LSet(msgType,3) + " {" + msgData + "}")
						ENetSendData(msgData,Len(msgData),a\eNet_id,reliable)
					EndIf
				Next
			Else							;Ah, a specific target.
				msgData = Chr(msgType) + Chr(BP_My_ID) + msgData
				a.NetInfo = BP_FindID(msgTarget)
				If BP_AutoLogging Then BP_UpdateLog ("[Outgoing] UDP   To: " + LSet(a\Name,20) + " Type: " + LSet(msgType,3) + " {" + msgData + "}")
				ENetSendData(msgData,Len(msgData),a\eNet_id,reliable)
			EndIf
		Else			;Client doing a send
			msgData = Chr(msgType) + Chr(msgTarget) + msgData
			If BP_AutoLogging Then BP_UpdateLog ("[Outgoing] UDP   To: " + msgTarget + " Type: " + LSet(msgType,3) + " {" + msgData + "}")
			ENetSendData(msgData,Len(msgData),0,reliable)
		EndIf
	EndIf
End Function

Function BP_ReturnMessage$(bank,dataSize)
	Local Message$
	Local size = PeekInt(dataSize, 0)-1
	
	For n = 0 To size-1
		Message = Message + Chr(PeekByte(bank, n))
	Next
	
	Return Message
End Function

Function BP_UpdateLog(txt$)
;-=-=-=Updates the log file, checks to see if its been started
	If BP_Log Then
		WriteLine BP_Log, txt$
	EndIf
End Function
