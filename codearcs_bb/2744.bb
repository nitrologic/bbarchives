; ID: 2744
; Author: ToeB
; Date: 2010-07-26 19:36:10
; Title: SimpleUDP2
; Description: Network-Library, easy to use, very fast

;+-------------------------+
;|        SimpleUDP        |
;|           by            |
;|                         |
;| Tobias 'ToeB' Hendricks |
;+-------------------------+
;|       Version 2.0       |
;+-------------------------+


Const UDP_JoinMsg = 1
Const UDP_NotJoinMsg = 2
Const UDP_PingMsg = 3
Const UDP_PingDataMsg = 4
Const UDP_CmdMsg = 5
Const UDP_EndMsg = 6
Const UDP_AnswerMsg = 7
Const UDP_KickMsg = 8
Const UDP_BannMsg = 9
Const UDP_NameMsg = 10
Const UDP_ChangeHostMsg = 11
Const UDP_GetInformationMsg = 12
Const UDP_StartSendServerFile = 13
Const UDP_AnswerSendServerFile = 14
Const UDP_SendServerFileBytes = 15
Const UDP_EndSendServerFile = 16
Const UDP_EndSend = 17
Const UDP_ReConnectMsg = 18

Const Event_NewPlayer = 1
Const Event_ConnectedServer = 2
Const Event_PlayerLeft= 3
Const Event_NoConnection = 4
Const Event_PlayerKicked = 5
Const Event_PlayerBanned = 6
Const Event_ChangeName = 7
Const Event_ReConnect = 8

Const UDP_LanGame=1, UDP_NetGame=2

Const UDP_MaxParameter = 15
Const UDP_PingSendTime = 1000;ms
Const UDP_PackedSize = 8187 ;Maximale Packetgröße : 8187
Const UDP_ServerFilePath$ = "ServerFiles\"

Global UDP_Host, UDP_Connected, UDP_Connect, UDP_ChangeHostConnect, UDP_Dediziert
Global UDP_Stream, UDP_ServerPort, UDP_ServerIP, UDP_ServerID, UDP_NetMode, UDP_MaxPlayers, UDP_ServerStream, UDP_ServerPing
Global UDP_SendJoinMsgMs, UDP_ServerGlobalPing
Global UDP_Player.UDP_Client, UDP_PlayerID, UDP_ServerClient.UDP_Client
Global UDP_SendPingTime, UDP_GetPingFromServer
Global UDP_EventID, UDP_EventMsg$, UDP_EventMsg2$, UDP_EventData$
Global UDP_MsgEntID
Global UDP_ChangeName, UDP_NewName$
Global UDP_SendServerFiles, UDP_ServerFileID, UDP_ServerFile.UDP_ServerFile
Global UDP_CmdID, UDP_CmdMaxParameter, UDP_CmdFromID, UDP_CmdBank


Dim UDP_CmdParameter$( UDP_MaxParameter )
Dim UDP_ClientInfo.UDP_Client( 0 )
Dim UDP_tmpParameter$( UDP_MaxParameter )

Type UDP_Client
	Field ID	
	Field Stream
	Field IP$,Port
	Field Ping,PingMs,WaitPing
	Field kick,host,connected
	Field name$,changename,newname$
	Field serverfile.UDP_ServerFile
	Field recon, reconms
End Type 
Type UDP_Server
	Field IP,Port
	Field Ping
End Type 
Type UDP_Cmd
	Field typ
	Field ID
	Field FromID,toID
	Field maxparameter
	Field parameter$[ UDP_MaxParameter ]
	Field info.UDP_Cmd
	Field del 
	Field send,sendms
	Field bank
End Type 
Type UDP_Event
	Field Event
	Field EventData$
	Field EventMsg$
	Field EventMsg2$
End Type
Type UDP_Ban
	Field IP
	Field time,ms
	Field reason$
End Type  
Type UDP_ServerFile
	Field transfer
	Field ID
	Field filename$
	Field datei, size 
	Field sendfile, sendms, byteID, byte
	Field client.UDP_Client
End Type 

 
	

Function UDP_HostGame( tmpMaxPlayers=16, tmpNetMode=UDP_LanGame, tmpPort=8000, tmpDediziert=0 )
	;Übergabe an Globale
	UDP_ServerPort = tmpPort
	UDP_NetMode = tmpNetMode
	UDP_Host = 1
	UDP_Connected = 1
	UDP_MaxPlayers = tmpMaxPlayers
	UDP_Dediziert = tmpDediziert
	Dim UDP_ClientInfo.UDP_Client( UDP_MaxPlayers )
	UDP_CmdBank = CreateBank( 0 )
	;Empfangs Stream erstellen
	UDP_Stream = CreateUDPStream( UDP_ServerPort )
	;Streamhandle prüfen
	If UDP_Stream Then 
		If tmpDediziert = 0 Then ;Wenn der Host mitspielen soll
			tmpClient.UDP_Client = New UDP_Client
			tmpClient\ID = 1
			tmpClient\IP = UDP_IntIP("127.0.0.1")
			tmpClient\Port = UDP_ServerPort
			tmpClient\stream = 0
			tmpClient\name$ = "unnamed"
			tmpClient\host = 1
			tmpClient\connected = 1
			UDP_Player = tmpClient
			UDP_PlayerID = tmpClient\ID
			UDP_ServerID = tmpClient\ID
			UDP_ClientInfo( UDP_PlayerID ) = tmpClient
			UDP_ServerClient = tmpClient
		EndIf 
		Return 1	
	Else
		UDP_Connected = 0
		Return 0
	EndIf 
End Function 

Function UDP_JoinGame( tmpHostIP$, tmpHostPort=8000 )
	;Übergabe an Globale
	UDP_ServerPort = tmpHostPort
	UDP_Host = 0
	UDP_Connected = 0
	UDP_CmdBank = CreateBank( 0 )
	If Instr(tmpHostIP,".")<>0 Then UDP_ServerIP = UDP_IntIP(tmpHostIP$) Else UDP_ServerIP = Int( tmpHostIP )
	;Stream erstellen
	If UDP_Stream = 0 Then UDP_Stream = CreateUDPStream( )
	;Stream überpürfen 
	If UDP_Stream = 0 Then Return 0
	;Wenn erfolgreich,
	UDP_Connect = 1
	UDP_ChangeHostConnect = 0
	Return 1
End Function 

Function UDP_Update( )
	;Host Aufgabenbereich
	If UDP_Host = 1 Then 
		If UDP_Connected = 1 Then 
			While RecvUDPMsg( UDP_Stream )<>0 ;Alle Nachrichten lesen
				tmpByte = ReadByte( UDP_Stream ) ;Header-Byte auslesen
				Select tmpByte ;Gucken welches Byte empfangen worden ist
				Case UDP_JoinMsg
					tmpClientIP = UDPMsgIP( UDP_Stream )
					tmpClientPort = UDPMsgPort( UDP_Stream )
					tmpClientConnected = ReadByte( UDP_Stream )
					If tmpClientConnected = 0 Then 
						;Prüfen on ein Bann für die IP vorhanden ist
						tmpBanned = 0
						tmpBanntime = 0
						tmpReason$ = ""
						For tmpBan.UDP_Ban = Each UDP_Ban
							If tmpBan\IP = tmpClientIP Then 
								tmpBanned = 1
								If tmpBan\time > 0 Then 
									tmpBannTime = tmpBan\time - ( MilliSecs( ) - tmpBan\ms ) 
								EndIf 
								tmpReason$ = tmpBan\reason
								Exit
							EndIf 
						Next 
						If tmpBanned = 0 Then 
							
							;Clients überpürfen
							tmpGet = 0
							For tmpCLient.UDP_Client = Each UDP_Client
								If tmpClient\IP = tmpClientIP And tmpClient\Port = tmpClientPort Then tmpGet = 1 : Exit
							Next 
							If tmpGet = 0
								;Prüfen ob Platz frei ist
								tmpID = 0
								For i = 1 To UDP_MaxPlayers
									If UDP_ClientInfo( i ) = Null Then tmpID = i : Exit
								Next 
							Else
								tmpID = tmpClient\ID
							EndIf 
							If tmpID > 0 And tmpID <= UDP_MaxPlayers
								
								;Clienten erstellen
								If tmpGet = 0 Then tmpClient.UDP_Client = New UDP_Client
								tmpClient\ID = tmpID
								tmpClient\IP = tmpClientIP
								tmpClient\Port = tmpClientPort
								tmpClient\stream = UDP_Stream
								tmpClient\name$ = "unnamed"
								tmpClient\connected = 1
								If UDP_NetMode = UDP_LanGame Then 
									tmpClient\stream = CreateUDPStream( )
								EndIf
								UDP_ClientInfo( tmpClient\ID ) = tmpClient
								If UDP_SendServerFiles = 1 Then 
									For tmpFile.UDP_ServerFile = Each UDP_ServerFile	
										If tmpFile\transfer = 0 Then 
											serverfile.UDP_ServerFile = New UDP_ServerFile
											serverfile\transfer = 1
											serverfile\filename$ = tmpFile\filename$
											serverfile\datei = ReadFile( serverfile\filename$ )
											serverfile\size = tmpFile\size
											serverfile\ID = tmpFile\ID
											serverfile\byteID = 1
											serverfile\byte = CreateBank( UDP_PackedSize )
											ReadBytes( serverfile\byte, serverfile\datei, 0, UDP_PackedSize )
											serverfile\client = tmpClient 
											tmpClient\serverfile = serverfile
										EndIf 
									Next 
								EndIf 
								;Antwort geben mit Client daten
								WriteByte( tmpClient\stream, UDP_JoinMsg )
								WriteByte( tmpClient\stream, 0 )
								WriteInt( tmpClient\stream, UDP_MaxPlayers )
								WriteByte( tmpClient\stream, UDP_Dediziert )
								WriteByte( tmpClient\stream, UDP_NetMode )
								WriteInt( tmpClient\stream, tmpClient\ID )
								WriteInt( tmpClient\stream, UDP_ServerID )
								WriteByte( tmpClient\stream, tmpClient\connected )
								For tmpClientAll.UDP_Client = Each UDP_Client
									WriteInt( tmpClient\stream, tmpClientAll\ID )
									WriteInt( tmpClient\stream, tmpClientAll\IP )
									WriteInt( tmpClient\stream, tmpClientAll\Port )
									WriteString( tmpClient\stream, tmpClientAll\name$ )
									WriteByte( tmpClient\stream, tmpClientAll\connected )
								Next 
								SendUDPMsg( tmpClient\stream, tmpClient\IP, tmpClient\Port )
								;Anderen Clienten den Neuen "vorstellen"
								For tmpClientAll.UDP_Client = Each UDP_Client
									If tmpClientAll <> tmpClient And tmpClientAll <> UDP_Player Then ;Wenn der Client nicht der Neue ist und nicht der eignene
										WriteByte( tmpClientAll\stream, UDP_JoinMsg )
										WriteInt( tmpClientAll\stream, tmpClient\ID )
										WriteInt( tmpClientAll\stream, tmpClient\IP )
										WriteInt( tmpClientAll\stream, tmpClient\Port )
										SendUDPMsg( tmpClientAll\stream, tmpClientAll\IP, tmpClientAll\Port )
									EndIf
								Next 
								
								tmpEvent.UDP_Event = New UDP_Event
								tmpEvent\Event = Event_NewPlayer
								tmpEvent\EventData$ = tmpClient\ID
							Else
								WriteByte( UDP_Stream, UDP_NotJoinMsg )
								WriteByte( UDP_Stream, 0 ) ;Server Voll
								SendUDPMsg( UDP_Stream, tmpClientIP, tmpClientPort )
							EndIf 
						Else
							WriteByte( UDP_Stream, UDP_NotJoinMsg )
							WriteByte( UDP_Stream, 1 ) ;Gebannt
							WriteInt( UDP_Stream, tmpBannTime )
							WriteString( UDP_Stream, tmpReason$ ) 
							SendUDPMsg( UDP_Stream, tmpClientIP, tmpClientPort )
						EndIf 
					Else
						tmpClientID = ReadInt( UDP_Stream )
						For tmpClient.UDP_Client = Each UDP_Client
							If tmpClient\ID = tmpClientID Then Exit
						Next 
						If tmpClient <> Null Then 
							tmpClient\connected = 1
							WriteByte( tmpClient\stream, UDP_JoinMsg )
							WriteByte( tmpClient\stream, 1 )
							SendUDPMsg( tmpClient\stream, tmpClient\IP, tmpClient\Port )
						EndIf 
					EndIf 
				Case UDP_PingMsg
					tmpID = ReadInt( UDP_Stream )
					If tmpID => 0 And tmpID <= UDP_MaxPlayers
						tmpClient.UDP_Client = UDP_ClientInfo.UDP_Client( tmpID ) 
						If tmpClient <> Null
							If tmpClient\connected = 1
								If tmpClient\PingMs > 0 Then 
									;If tmpClient\waitping = 1 
										tmpClient\Ping = ( MilliSecs() - tmpClient\PingMs ) / 2 : tmpClient\PingMs = 0
										tmpClient\waitping = 0
									;EndIf
								EndIf 
								WriteByte( tmpClient\stream, UDP_PingDataMsg )
								For tmpClientAll.UDP_Client = Each UDP_Client
									WriteByte( tmpClient\stream, 1)
									WriteInt( tmpClient\stream, tmpClientAll\ID )
									WriteInt( tmpClient\stream, tmpClientAll\Ping )
									WriteByte( tmpClient\stream, tmpClientAll\connected )
								Next 
								WriteByte ( tmpClient\stream, 2 )
								SendUDPMsg( tmpClient\stream, tmpClient\IP, tmpClient\Port)
							EndIf 
						EndIf 
					EndIf 
				Case UDP_CmdMsg
					tmpSafety = ReadByte( UDP_Stream )
					tmpClientID = ReadInt( UDP_Stream )
					If tmpSafety = 1 Then tmpID = ReadInt( UDP_Stream )
					tmpCmdID = ReadInt( UDP_Stream )
					tmpToID = ReadInt( UDP_Stream )
						If tmpClientID => 0 And tmpClientID <= UDP_MaxPlayers Then 
						For tmpCmdInfo.UDP_Cmd=Each UDP_Cmd
							If tmpCmdInfo\typ=0 And tmpCmdInfo\ID=tmpCmdID Then Exit
						Next
						tmpGet = 0
						If tmpSafety = 1 Then
							For tmpCmd.UDP_Cmd = Each UDP_Cmd
								If tmpCmd\typ = 4 And tmpCmd\ID = tmpID Then tmpGet = 1: Exit
							Next 
						EndIf 
						If tmpGet = 0 Then tmpCmd.UDP_Cmd = New UDP_Cmd
						If tmpSafety = 0
							tmpCmd\Typ = 2 
							tmpCmd\ID = tmpCmdID
						Else
							tmpCmd\typ = 2
							tmpCmd\ID = tmpID
							tmpCmd\sendMs = MilliSecs()
							If tmpGet = 0
								tmpCmdNew.UDP_Cmd = New UDP_Cmd
								tmpCmdNew\Typ = 4
								tmpCmdNew\ID = tmpID
							EndIf 
						EndIf 
						tmpCmd\FromID = tmpClientID
						tmpCmd\ToID = tmpToID
						tmpCmd\info = tmpCmdInfo
						tmpCmd\MaxParameter = tmpCmdInfo\MaxParameter
						For i = 0 To tmpCmdInfo\MaxParameter
							Select tmpCmdInfo\parameter[ i ]
							Case 0 : tmpCmd\parameter[ i ] = ReadInt( UDP_Stream ) 
							Case 1 : tmpCmd\parameter[ i ] = ReadFloat( UDP_Stream ) 
							Case 2 : tmpCmd\parameter[ i ] = ReadString( UDP_Stream ) 
							Case 3 : tmpCmd\parameter[ i ] = ReadByte( UDP_Stream ) 
							Case 4 : tmpCmd\parameter[ i ] = ReadShort( UDP_Stream ) 
							Case 5
								tmpBankSize = ReadInt( UDP_Stream )
								tmpCmd\bank = CreateBank( tmpBankSize )
								For j = 0 To tmpBankSize-1
									tmpByte = ReadByte( UDP_Stream )
									PokeByte( tmpCmd\bank, j, tmpByte )
								Next  
								tmpCmd\parameter[ i ] = tmpCmd\bank
							End Select 
						Next 
						If tmpSafety = 0											
							tmpCmdNew.UDP_Cmd = New UDP_Cmd
							tmpCmdNew\ID = tmpCmdID
							tmpCmdNew\Typ = 1 
							tmpCmdNew\FromID = tmpClientID
							tmpCmdNew\toID = tmpToID
							tmpCmdNew\info = tmpCmdInfo
							tmpCmdNew\MaxParameter = tmpCmdInfo\MaxParameter
							For i = 0 To tmpCmdInfo\MaxParameter
								tmpCmdNew\parameter[ i ] = tmpCmd\parameter[ i ]
							Next							
							If tmptoID <> 1 And tmpToID > 0 And tmpToID <= UDP_MaxPlayers Then 	
								If UDP_ClientInfo( tmptoID ) <> Null Then Delete tmpCmd	
							EndIf 
						Else
							If tmpClientID => 0 And tmpClientID <= UDP_MaxPlayers Then 
								tmpClient.UDP_Client = UDP_ClientInfo( tmpClientID )
								If tmpClient\connected = 1
									WriteByte( tmpClient\stream, UDP_AnswerMsg )
									WriteInt( tmpClient\stream, tmpID )
									SendUDPMsg( tmpClient\stream, tmpClient\IP, tmpClient\Port )
									tmpCmdToID = tmPCmd\toID
									If tmpCmdToID < 0 Or tmpCmdtoID > UDP_MaxPlayers Then tmpCmdtoID = -1
									For tmpClient.UDP_Client = Each UDP_Client
										If (tmpCmdToID = tmpClient\ID  Or  tmpCmdToID = -1 ) And tmpClient\ID <> tmpClientID And tmpClient\ID <> UDP_PlayerID Then 								
											tmpCmdNew.UDP_Cmd = New UDP_Cmd
											tmpCmdNew\info = tmpCmd\info				
											tmpCmdNew\typ = 3
											tmpCmdNew\ID = UDP_MsgEntID
											UDP_MsgEntID =( UDP_MsgEntID + 1 ) Mod 2147483647
											tmpCmdNew\FromID = tmpCmd\FromID
											tmpCmdNew\ToID = tmpClient\ID
											If tmpCmdNew\toID > 0 And tmpCmdNew\toID <= UDP_MaxPlayers Then 
												If UDP_ClientInfo( tmpCmdNew\toID ) = Null Or tmpCmdNew\toID = UDP_PlayerID Then tmpCmdNew\toID = -1
											EndIf  
											tmpCmdNew\MaxParameter = tmpCmd\MaxParameter
											For i = 0 To tmpCmdNew\MaxParameter
												tmpCmdNew\Parameter[ i ] = tmpCmd\Parameter[ i ]
											Next 
										EndIf
									Next 
									If UDP_ServerClient <> Null Then 
										If tmpToID <> UDP_PlayerID And tmpToID <> -1 Then Delete tmpCmd
									Else
										If tmpCmdToID <> 0 And tmpCmdtoID <> -1 Then Delete tmpCmd
									EndIf 
								EndIf 
							EndIf 
						EndIf 
					EndIf 
				Case UDP_EndMsg
					tmpID = ReadInt( UDP_Stream )
					For tmpClient.UDP_Client = Each UDP_Client
						If tmpClient <> UDP_Player Then 
							WriteByte( tmpClient\Stream, UDP_EndMsg )
							WriteInt( tmpClient\Stream, tmpID )
							SendUDPMsg( tmpClient\Stream, tmpClient\IP, tmpClient\Port )
						EndIf
					Next 
					If UDP_ClientInfo( tmpID ) <> Null Then Delete UDP_ClientInfo( tmpID )
					tmpEvent.UDP_Event = New UDP_Event
					tmpEvent\Event = Event_PlayerLeft
					tmpEvent\EventData$ = tmpID 
				Case UDP_AnswerMsg
					tmpPlayerID = ReadInt( UDP_Stream )
					tmpID = ReadInt( UDP_Stream )
					If tmpPlayerID => 0 And tmpPlayerID <= UDP_MaxPlayers Then 
						tmpClient.UDP_Client = UDP_ClientInfo( tmpPlayerID )	
						If tmpClient\connected = 1
							If tmpClient <> Null Then 
								For tmpCmd.UDP_Cmd = Each UDP_Cmd
									If tmpCmd\typ = 3 And tmpCmd\ID = tmpID Then 
										Delete tmpCmd
										Exit	
									EndIf
								Next 
							EndIf	
						EndIf 
					EndIf 
				Case UDP_NameMsg
					tmpID = ReadInt( UDP_Stream )
					tmpNewName$ = ReadString( UDP_Stream )
					If tmpID => 0 And tmpID <= UDP_MaxPlayers Then 
						If UDP_ClientInfo( tmpID ) <> Null Then 
							UDP_ClientInfo( tmpID )\changeName = 1
							UDP_ClientInfo( tmpID )\newName$ = tmpNewName$
						EndIf
					EndIf 
				Case UDP_GetInformationMsg
					tmpIP = UDPMsgIP( UDP_Stream )
					tmpPort = UDPMsgPort( UDP_Stream )
					WriteByte( UDP_Stream, UDP_GetInformationMsg )
					SendUDPMsg( UDP_Stream, tmpIP, tmpPort )
				Case UDP_AnswerSendServerFile
					tmpPlayerID = ReadInt( UDP_Stream )
					tmpID = ReadInt( UDP_Stream )
					If tmpPlayerID => 0 And tmpPlayerID <= UDP_MaxPlayers
						If UDP_ClientInfo( tmpPlayerID ) <> Null Then 
							If UDP_ClientInfo( tmpPlayerID )\serverfile <> Null Then 
								If UDP_ClientInfo( tmpPlayerID )\serverfile\ID = tmpID Then 
									UDP_ClientInfo( tmpPlayerID )\serverfile\sendfile = 1
								EndIf
							EndIf
						EndIf
					EndIf
				Case UDP_SendServerFileBytes
					tmpPlayerID = ReadInt( UDP_Stream )
					tmpByteID = ReadInt( UDP_Stream )
					If tmpPlayerID => 0 And tmpPlayerID <= UDP_MaxPlayers
						If UDP_ClientInfo( tmpPlayerID ) <> Null Then 
							If UDP_ClientInfo( tmpPlayerID )\serverfile\byteID = tmpByteID Then 
								UDP_CLientInfo( tmpPlayerID )\serverfile\byteID = ( UDP_CLientInfo( tmpPlayerID )\serverfile\byteID + UDP_PackedSize ) 
								If UDP_CLientInfo( tmpPlayerID )\serverfile\byteID > UDP_CLientInfo( tmpPlayerID )\serverfile\size Then
									UDP_CLientInfo( tmpPlayerID )\serverfile\byteID = UDP_CLientInfo( tmpPlayerID )\serverfile\size
								EndIf 
								If Eof( UDP_ClientInfo( tmpPlayerID )\serverfile\datei ) = 1 Then 
									UDP_ClientInfo( tmpPlayerID )\serverfile\sendfile = 2
								Else
									For i = 0 To UDP_PackedSize-1 : PokeByte( UDP_CLientInfo( tmpPlayerID )\serverfile\byte, i, 0 ) : Next 
									ReadBytes( UDP_CLientInfo( tmpPlayerID )\serverfile\byte, UDP_CLientInfo( tmpPlayerID )\serverfile\datei, 0, UDP_PackedSize )
									UDP_CLientInfo( tmpPlayerID )\serverfile\sendms = 0					
								EndIf 																
							EndIf 
						EndIf 
					EndIf
				Case UDP_EndSendServerFile 	
					tmpPlayerID = ReadInt( UDP_Stream ) 
					If UDP_ClientInfo( tmpPlayerID ) <> Null Then 	
						tmpClient.UDP_Client = UDP_CLientInfo( tmpPlayerID )
						CloseFile tmpClient\serverfile\datei
						FreeBank tmpClient\serverfile\byte
						Delete tmpClient\serverfile
						For tmpfile.UDP_ServerFile = Each UDP_ServerFile
							If tmpFile\client = tmpClient And tmpFile\transfer = 1 Then 
								tmpClient\serverfile = tmpFile
								Exit
							EndIf 
						Next 
						If tmpClient\serverfile = Null Then 
							tmpClient\connected = 1
							WriteByte( tmpClient\stream, UDP_EndSend )
							SendUDPMsg( tmpClient\stream, tmpClient\IP, tmpClient\Port )
						EndIf 
					EndIf 
				Case UDP_ReConnectMsg
					tmpPlayerID = ReadInt( UDP_Stream )
					If UDP_ClientInfo( tmpPlayerID ) <> Null Then 
						UDP_ClientInfo( tmpPlayerID )\recon = 0
						UDP_ClientInfo( tmpPlayerID )\connected = 0
					EndIf 
				End Select 
			Wend 	
			;Ping updaten
			tmpSendPing = 0
			If UDP_SendPingTime <= MilliSecs() Then 
				tmpSendPing = 1
				UDP_SendPingTime = MilliSecs() + UDP_PingSendTime 
			EndIf 
			;Cmd's senden
			For tmpClient.UDP_Client = Each UDP_Client
				If tmpClient\connected = 1 Then 
					;Serverfiles senden
					If tmpClient\serverfile <> Null Then 
						tmpPing = tmpClient\Ping * 4
						If tmpPing < 64 tmpPing = 64
						If tmpClient\serverfile\sendfile = 0 And tmpClient\serverfile\sendms <= MilliSecs() - tmpPing Then 
							WriteByte( tmpClient\stream, UDP_StartSendServerFile )
							WriteInt( tmpClient\stream, tmpCLient\serverFile\ID )
							WriteInt( tmpClient\stream, tmpClient\serverFile\size )
							WriteString( tmpClient\stream, UDP_ServerFilePath$+tmpClient\serverFile\filename$ )
							SendUDPMsg( tmpClient\stream, tmpClient\IP, tmpClient\port )
							tmpClient\serverfile\sendms = MilliSecs()
						EndIf 
						If tmpClient\serverfile\sendfile = 1 And tmpClient\serverfile\sendms <= MilliSecs() - tmpPing Then 
							WriteByte( tmpClient\stream, UDP_SendServerFileBytes )
							WriteInt( tmpClient\stream, tmpClient\serverfile\byteID )
							WriteBytes( tmpCLient\serverfile\byte, tmpClient\stream, 0, UDP_PackedSize )
							SendUDPMsg( tmpClient\stream, tmpClient\IP, tmpClient\Port )
							tmpClient\serverfile\sendms = MilliSecs()
						EndIf 
						If tmpClient\serverfile\sendfile = 2 And tmpClient\serverfile\sendms <= MilliSecs() - tmpPing Then 
							WriteByte( tmpClient\stream, UDP_EndSendServerFile )
							SendUDPMsg( tmpClient\stream, tmpClient\IP, tmpClient\port )
							tmpClient\serverfile\sendms = MilliSecs()
						EndIf 
					EndIf
					If tmpSendPing = 1 Then 
						;Ping nachricht
						If tmpClient\connected = 1
							If tmpClient <> UDP_Player Then 
								If tmpClient\waitping = 1 Then tmpClient\Ping = (MilliSecs() - tmpClient\PingMS) /2
								WriteByte( tmpClient\stream, UDP_PingMsg )
								SendUDPMsg( tmpClient\stream, tmpClient\IP, tmpClient\Port )
								tmpClient\PingMS = MilliSecs()
								tmpClient\WaitPing = 1									
							EndIf
						EndIf 
					EndIf 
					If tmpClient\changeName = 1 Then 
						tmpClient\changename = 0
						For tmpClientAll.UDP_Client = Each UDP_Client
							If tmpClientAll <> UDP_Player Then 
								If tmpClientAll\stream <> 0 Then 
									WriteByte( tmpClientAll\stream, UDP_NameMsg )
									WriteInt( tmpClientAll\stream, tmpClient\ID )
									WriteString( tmpClientAll\stream, tmpClient\newname$ )
									SendUDPMsg( tmpClientAll\stream, tmpClientAll\IP, tmpClientAll\Port )
								EndIf
							EndIf 
						Next 
						tmpEvent.UDP_Event = New UDP_Event
						tmpEvent\Event = Event_ChangeName
						tmpEvent\EventData = tmpClient\ID
						tmpEvent\EventMsg$ = tmpClient\NewName$
						tmpEvent\EventMsg2$ = tmpClient\name$
						tmpClient\name$ = tmpClient\newname$
					EndIf
					If tmpClient <> UDP_Player Then
						If tmpClient\recon = 1 Then 
							tmpPing = tmpClient\Ping * 4
							If tmpPing < 64 tmpPing = 64
							If tmpClient\reconms <= MilliSecs() - tmpPing Then 
								WriteByte( tmpClient\stream, UDP_ReConnectMsg )
								SendUDPMsg( tmpClient\stream, tmpClient\IP, tmpClient\Port )
								tmpClient\reconms = MilliSecs()
							EndIf
						EndIf
						For tmpCmd.UDP_Cmd = Each UDP_Cmd
							If tmpCmd\typ = 1 Then 
								If ( tmpCmd\toID=tmpClient\ID  Or tmpCmd\toID = -1 ) And tmpCmd\FromID <> tmpClient\ID Then 
									WriteByte( tmpClient\stream, UDP_CmdMsg )
									WriteByte( tmpClient\stream, 0 )
									WriteInt( tmpClient\stream, tmpCmd\ID )
									WriteInt( tmpClient\stream, tmpCmd\FromID )
									For i = 0 To tmpCmd\MaxParameter
										Select tmpCmd\info\parameter[i]
										Case 0 : WriteInt( tmpClient\stream, tmpCmd\parameter[ i ] )
										Case 1 : WriteFloat( tmpClient\stream, tmpCmd\parameter[ i ] )
										Case 2 : WriteString( tmpClient\stream, tmpCmd\parameter[ i ] )
										Case 3 : WriteByte( tmpClient\stream, tmpCmd\parameter[ i ] )
										Case 4 : WriteShort( tmpClient\stream, tmpCmd\parameter[ i ] )
										Case 5
											tmpBankSize = BankSize( tmpCmd\parameter[ i ] )
											WriteInt( tmpClient\stream, tmpBankSize )
											For j = 0 To tmpBankSize-1
												WriteByte( tmpClient\stream, PeekByte( tmpCmd\Parameter[ i ], j ) )
											Next 										
										End Select 
									Next 
									SendUDPMsg( tmpClient\stream, tmpClient\IP, tmpClient\Port )
									tmpCmd\del = 1
								EndIf 
							EndIf
							If tmpCmd\typ = 3 And tmpCmd\del <> 1 Then								
								If ( tmpCmd\toID=tmpClient\ID ) Or tmpCmd\toID = -1 Then 
									WriteByte( tmpClient\stream, UDP_CmdMsg )
									WriteByte( tmpClient\stream, 1 )
									WriteInt( tmpClient\stream, tmpCmd\ID )
									WriteInt( tmpClient\stream, tmpCmd\info\ID )
									WriteInt( tmpClient\stream, tmpCmd\FromID ) 
									For i = 0 To tmpCmd\MaxParameter
										Select tmpCmd\info\parameter[i]
										Case 0 : WriteInt( tmpClient\stream, tmpCmd\parameter[i] )
										Case 1 : WriteFloat( tmpClient\stream, tmpCmd\parameter[i] )
										Case 2 : WriteString( tmpClient\stream, tmpCmd\parameter[i] )
										Case 3 : WriteByte( tmpClient\stream, tmpCmd\parameter[i] )
										Case 4 : WriteShort( tmpClient\stream, tmpCmd\parameter[i] )
										Case 5
											tmpBankSize = BankSize( tmpCmd\parameter[ i ] )
											WriteInt( tmpClient\stream, tmpBankSize )
											For j = 0 To tmpBankSize-1
												WriteByte( tmpClient\stream, PeekByte( tmpCmd\Parameter[ i ], j ) )
											Next 	
										End Select 
									Next 
									SendUDPMsg( tmpClient\stream, tmpClient\IP, tmpClient\Port )
									tmpCmd\del = 2
									tmpCmd\sendms = MilliSecs() 
								EndIf 
							EndIf
						Next 
					EndIf
					If tmpClient\kick = 1 Then 
						If tmpClient\stream <> UDP_Stream Then CloseUDPStream( tmpClient\stream )
						Delete tmpClient
					EndIf 
				EndIf 
			Next 
			For tmpCmd.UDP_Cmd = Each UDP_Cmd
				tmpDel = 0
				If tmpCmd\typ = 1 And tmpCmd\del = 1 Then tmpDel = 1
				If tmpCmd\typ = 3 Then 
					If tmpCmd\del = 2 Then tmpCmd\del = 1
					If tmpCmd\del = 1 Then 
						tmpPing = 512
						If tmpCmd\toID > 0 And tmpCmd\toID <= UDP_MaxPlayers
							If UDP_ClientInfo( tmpCmd\toID ) <> Null Then 
								tmpPing = UDP_ClientInfo( tmpCmd\toID )\Ping * 4
								If tmpPing < 64 tmpPing = 64
							EndIf
						EndIf 
						If MilliSecs()-tmpCmd\sendms > tmpPing Then 
							tmpCmd\del = 0 : tmpCmd\send = tmpCmd\send + 1
							If tmpCmd\send > 3 Then tmpDel = 1
						EndIf
					EndIf 
				EndIf
				If tmpCmd\typ = 4
					If MilliSecs()-tmpCmd\sendms => 20000 Then tmpDel = 1
				EndIf 
				If tmpDel = 1 Then Delete tmpCmd
			Next
			For tmpBan.UDP_Ban = Each UDP_Ban
				If tmpBan\time > 0 Then 
					If MilliSecs() - tmpBan\ms => tmpBan\time  Then Delete tmpBan
				EndIf 
			Next
		EndIf 	
	EndIf
	;Client Aufgabenbereich
	If UDP_Host = 0 Then 
		;Auf Verbunden / Nicht verbunden prüfen
		If UDP_Connected = 0 Then 
			;Wenn eine Anfrage geschickt worden ist ...
			If UDP_Connect = 1
				;Anfrage an den Server schicken
				DebugLog "CON"
				If UDP_SendJoinMsgMs <= MilliSecs() - 1000 Then 
					WriteByte( UDP_Stream, UDP_JoinMsg )
					WriteByte( UDP_Stream, UDP_ChangeHostConnect )
					If UDP_ChangeHostConnect Then 
						WriteInt( UDP_Stream, UDP_PlayerID )
					EndIf 
					SendUDPMsg( UDP_Stream, UDP_ServerIP, UDP_ServerPort)
					UDP_SendJoinMsgMs = MilliSecs()
				EndIf 
				While RecvUDPMsg( UDP_Stream )
					tmpByte = ReadByte( UDP_Stream )
					Select tmpByte
					Case UDP_JoinMsg ;Bestätigung vom Server
						tmpGetJoin = ReadByte( UDP_Stream )
						If tmpGetJoin = 0 Then 
							UDP_MaxPlayers = ReadInt( UDP_Stream )
							UDP_Dediziert = ReadByte( UDP_Stream )
							UDP_NetMode = ReadByte( UDP_Stream )
							If UDP_NetMode = UDP_NetGame Then 
								UDP_ServerStream = UDP_Stream
							Else
								UDP_ServerStream = CreateUDPStream( )
							EndIf 
							Dim UDP_ClientInfo.UDP_Client( UDP_MaxPlayers )
							UDP_PlayerID = ReadInt( UDP_Stream )
							UDP_ServerID = ReadInt( UDP_Stream )
							UDP_Connected = ReadByte( UDP_Stream )
							;Alle vorhandenen Clienten empfangen
							tmpEvent.UDP_Event = New UDP_Event
							tmpEvent\Event = Event_ConnectedServer
							While Not Eof( UDP_Stream )
								tmpClient.UDP_Client = New UDP_Client
								tmpClient\ID = ReadInt( UDP_Stream )
								tmpClient\IP = ReadInt( UDP_Stream )
								tmpClient\Port = ReadInt( UDP_Stream )
								tmpClient\Name$ = ReadString( UDP_Stream )
								tmpClient\connected = ReadByte( UDP_Stream )
								tmpClient\stream = UDP_Stream
								UDP_ClientInfo( tmpClient\ID ) = tmpClient
								If tmpClient\ID = UDP_PlayerID Then
									UDP_Player.UDP_Client = tmpClient	
									;tmpClient\Port = UDPStreamPort(UDP_Stream) 					
								EndIf
								tmpEvent.UDP_Event = New UDP_Event
								tmpEvent\Event = Event_NewPlayer
								tmpEvent\EventData$ = tmpClient\ID
								If (UDP_Dezidiert = 0 And tmpClient\ID = UDP_ServerID) Then 
									tmpClient\IP = UDP_ServerIP
									tmpClient\Port = UDP_ServerPort
									tmpClient\host = 1
									If UDP_NetMode = UDP_LanGame Then tmpClient\stream = CreateUDPStream( ) : UDP_ServerStream = tmpClient\stream
									UDP_ServerClient = tmpClient
								EndIf 
							Wend 
							UDP_Connect = 0
							UDP_Connected = 1
						Else
							UDP_Connect = 0
							UDP_Connected = 1
						EndIf 
					Case UDP_NotJoinMsg; Fehler, keine connection
						tmpID = ReadByte( UDP_Stream )
						UDP_Connect = 0
						If tmpID = 0							
							tmpEvent.UDP_Event = New UDP_Event
							tmpEvent\Event = Event_NoConnection
							tmpEvent\EventData = 0
						Else
							tmpEvent.UDP_Event = New UDP_Event
							tmpEvent\Event = Event_NoConnection
							tmpEvent\EventData = 1
							tmpEvent\EventMsg2$ = ReadInt( UDP_Stream )
							tmpEvent\EventMsg$ = ReadString( UDP_Stream )
						EndIf 
					End Select
				Wend 
			ElseIf UDP_Connect = 2 
				While RecvUDPMsg( UDP_Stream )
					tmpByte = ReadByte( UDP_Stream )
					Select tmpByte
					Case UDP_GetInformationMsg
						tmpServer.UDP_Server = New UDP_Server
						tmpServer\IP = UDPMsgIP( UDP_Stream )
						tmpServer\Port = UDPMsgPort( UDP_Stream )
						tmpServer\Ping = ( MilliSecs( ) - UDP_ServerGlobalPing ) / 2.0
					End Select 
				Wend 
			EndIf 
		ElseIf UDP_Connected = 1
			UDP_Connect = 0
			While RecvUDPMsg( UDP_Stream ) ;NAchrichten empfangen
				tmpByte = ReadByte( UDP_Stream ); Header byte auslesen und überprüfen
				Select tmpByte
				Case UDP_JoinMsg ;Neuer Spieler
					tmpID = ReadInt( UDP_Stream )
					tmpIP = ReadInt( UDP_Stream )
					tmpPort = ReadInt( UDP_Stream )
					If tmpID => 0 And tmpID <= UDP_MaxPlayers
						;Clients überpürfen
						tmpGet = 0
						For tmpCLient.UDP_Client = Each UDP_Client
							If tmpClient\IP = tmpIP And tmpClient\Port = tmpPort Then tmpGet = 1 : Exit
						Next 
						If tmpGet = 0 Then tmpClient.UDP_Client = New UDP_Client
						tmpClient\ID = tmpID
						tmpClient\IP = tmpIP
						tmpClient\Port = tmpPort
						tmpClient\name$ = "unnamed"
						tmpClient\Stream = UDP_Stream
						tmpClient\host = 0
						tmpClient\connected = 1
						UDP_ClientInfo.UDP_Client( tmpClient\ID ) = tmpClient
						tmpEvent.UDP_Event = New UDP_Event
						tmpEvent\Event = Event_NewPlayer
						tmpEvent\EventData$ = tmpClient\ID
					EndIf 
				Case UDP_PingMsg ;Ping-Anfrage vom Server
					WriteByte( UDP_ServerStream, UDP_PingMsg )
					WriteInt( UDP_ServerStream, UDP_PlayerID )
					SendUDPMsg( UDP_ServerStream, UDP_ServerIP, UDP_ServerPort )
					If UDP_ServerClient <> Null Then 
						;If UDP_ServerClient\WaitPing = 0 Then 
							UDP_ServerClient\PingMs = MilliSecs()
							UDP_ServerClient\WaitPing = 1
							UDP_GetPingFromServer = 0
						;EndIf 
					EndIf 
				Case UDP_PingDataMsg ;PingDaten der Spieler
					While ReadByte( UDP_Stream ) = 1
						tmpID = ReadInt( UDP_Stream )
						tmpPing = ReadInt( UDP_Stream )
						tmpConnected = ReadByte( UDP_Stream )
						If tmpID => 0 And tmpID <= UDP_MaxPlayers Then 
							If UDP_ClientInfo( tmpID ) <> Null Then 
								UDP_ClientInfo.UDP_Client( tmpID )\Ping = tmpPing
								UDP_ClientInfo.UDP_Client( tmpID )\Connected = tmpConnected
							EndIf
						EndIf 
					Wend 
					If UDP_ServerClient <> Null Then 
						;If UDP_ServerClient\WaitPing = 1
							UDP_ServerClient\Ping = ( MilliSecs() - UDP_ServerClient\PingMs ) / 2
							UDP_ServerClient\WaitPing = 0
							UDP_GetPingFromServer = MilliSecs()
						;EndIf 	
					EndIf 
				Case UDP_CmdMsg
					tmpSafety = ReadByte( UDP_Stream )
					If tmpSafety = 1 Then tmpID = ReadInt( UDP_Stream )
					tmpCmdID = ReadInt( UDP_Stream )
					tmpFromID = ReadInt( UDP_Stream )
					For tmpCmdInfo.UDP_Cmd = Each UDP_Cmd
						If tmpCmdInfo\typ = 0 And tmpCmdInfo\ID = tmpCmdID Then Exit
					Next
					tmpGet = 0
					If tmpSafety = 1 Then
						For tmpCmd.UDP_Cmd = Each UDP_Cmd
							If tmpCmd\typ = 4 And tmpCmd\ID = tmpID Then tmpGet = 1: Exit
						Next 
					EndIf 
					If tmpGet = 0 Then tmpCmd.UDP_Cmd = New UDP_Cmd
					If tmpCmd <> Null And tmpCmdInfo <> Null Then 
						If tmpSafety = 0 Then 
							tmpCmd\Typ = 2 
							tmpCmd\ID = tmpCmdID
						Else
							tmpCmd\typ = 2
							tmpCmd\ID = tmpID
							tmpCmd\sendms = MilliSecs()
							If tmpGet = 0
								tmpCmdNew.UDP_Cmd = New UDP_Cmd
								tmpCmdNew\Typ = 4
								tmpCmdNew\ID = tmpID
							EndIf 
						EndIf 
						tmpCmd\FromID = tmpFromID
						tmpCmd\info = tmpCmdInfo
						tmpCmd\MaxParameter = tmpCmdInfo\MaxParameter
						For i = 0 To tmpCmdInfo\MaxParameter
							Select tmpCmdInfo\parameter[ i ]
							Case 0 : tmpCmd\parameter[ i ] = ReadInt( UDP_Stream ) 
							Case 1 : tmpCmd\parameter[ i ] = ReadFloat( UDP_Stream ) 
							Case 2 : tmpCmd\parameter[ i ] = ReadString( UDP_Stream ) 
							Case 3 : tmpCmd\parameter[ i ] = ReadByte( UDP_Stream ) 
							Case 4 : tmpCmd\parameter[ i ] = ReadShort( UDP_Stream ) 
							Case 5
								tmpBankSize = ReadInt( UDP_Stream )
								tmpCmd\bank = CreateBank( tmpBankSize )
								For j = 0 To tmpBankSize-1
									tmpByte = ReadByte( UDP_Stream )
									PokeByte( tmpCmd\bank, j, tmpByte )
								Next  
								tmpCmd\parameter[ i ] = tmpCmd\bank
							End Select 
						Next 
					EndIf 
					If tmpSafety = 1
						WriteByte( UDP_ServerStream, UDP_AnswerMsg )
						WriteInt( UDP_ServerStream, UDP_PlayerID )
						WriteInt( UDP_ServerStream, tmpID )
						SendUDPMsg( UDP_ServerStream, UDP_ServerIP, UDP_ServerPort )
					EndIf 
				Case UDP_EndMsg
					tmpID = ReadInt( UDP_Stream )
					If UDP_ClientInfo( tmpID ) <> Null Then Delete UDP_ClientInfo( tmpID )
					If tmpID = 0 Then 
						UDP_Connected = 0
						If UDP_ServerStream <> UDP_Stream Then CloseUDPStream( UDP_ServerStream )
						CloseUDPStream( UDP_Stream )
						Delete Each UDP_Client
					EndIf
					tmpEvent.UDP_Event = New UDP_Event
					tmpEvent\Event = Event_PlayerLeft
					tmpEvent\EventData$ = tmpID
					Return 
				Case UDP_AnswerMsg
					tmpID = ReadInt( UDP_Stream ) 
					For tmpCmd.UDP_Cmd = Each UDP_Cmd
						If tmpCmd\typ = 3 And tmpCmd\ID = tmpID Then 
							Delete tmpCmd
							Exit	
						EndIf
					Next 
				Case UDP_KickMsg
					tmpID = ReadInt( UDP_Stream )
					tmpReason$ = ReadString( UDP_Stream )
					If tmpID = UDP_PlayerID Then 
						UDP_Connected = 0
						If UDP_ServerStream <> UDP_Stream Then CloseUDPStream( UDP_ServerStream )
						CloseUDPStream( UDP_Stream )
						Delete Each UDP_CLient
					Else
						If UDP_ClientInfo( tmpID ) <> Null Then Delete UDP_ClientInfo( tmpID )
					EndIf
					tmpEvent.UDP_Event = New UDP_Event
					tmpEvent\Event = Event_PlayerKicked
					tmpEvent\EventData = tmpID
					tmpEvent\EventMsg = tmpReason
					Return 
				Case UDP_BannMsg
					tmpID = ReadInt( UDP_Stream )
					tmpTime = ReadInt( UDP_Stream )
					tmpReason$ = ReadString( UDP_Stream )
					If tmpID = UDP_PlayerID Then 
						UDP_Connected = 0
						If UDP_ServerStream <> UDP_Stream Then CloseUDPStream( UDP_ServerStream )
						CloseUDPStream( UDP_Stream )
						Delete Each UDP_CLient
					Else
						If UDP_ClientInfo( tmpID ) <> Null Then Delete UDP_ClientInfo( tmpID )
					EndIf
					tmpEvent.UDP_Event = New UDP_Event
					tmpEvent\Event = Event_PlayerBanned
					tmpEvent\EventData = tmpID
					tmpEvent\EventMsg$ = tmpReason
					tmpEvent\EventMsg2$ = tmpTime
					Return 
				Case UDP_NameMsg
					tmpID = ReadInt( UDP_Stream )
					tmpName$ = ReadString( UDP_Stream )
					If UDP_ClientInfo( tmpID ) <> Null						
						tmpEvent.UDP_Event = New UDP_Event
						tmpEvent\Event = Event_ChangeName
						tmpEvent\EventData = tmpID
						tmpEvent\EventMsg$ = tmpName$
						tmpEvent\EventMsg2$ = UDP_CLientInfo( tmpID )\name$
						UDP_CLientInfo( tmpID )\name$ = tmpName$
					EndIf  
				Case UDP_ChangeHostMsg
					tmpMe = ReadByte( UDP_Stream )
					If tmpMe = 1 Then 
						UDP_NetMode = ReadByte( UDP_Stream )
						UDP_CmdOn = ReadByte( UDP_Stream )
						If UDP_CmdOn = 1 Then 
							tmpCmdID = ReadInt( UDP_Stream )
							tmpCmd.UDP_Cmd = New UDP_Cmd
							For tmpCmdInfo.UDP_Cmd = Each UDP_Cmd
								If tmpCmdInfo\typ = 0 And tmpCmdInfo\ID = tmpCmdID Then Exit
							Next
							tmpCmd\Typ = 2 
							tmpCmd\ID = tmpCmdID
							tmpCmd\info = tmpCmdInfo
							tmpCmd\MaxParameter = tmpCmdInfo\MaxParameter
							For i = 0 To tmpCmdInfo\MaxParameter
								Select tmpCmdInfo\parameter[ i ]
								Case 0 : tmpCmd\parameter[ i ] = ReadInt( UDP_Stream ) 
								Case 1 : tmpCmd\parameter[ i ] = ReadFloat( UDP_Stream ) 
								Case 2 : tmpCmd\parameter[ i ] = ReadString( UDP_Stream ) 
								Case 3 : tmpCmd\parameter[ i ] = ReadByte( UDP_Stream ) 
								Case 4 : tmpCmd\parameter[ i ] = ReadShort( UDP_Stream ) 
								Case 5
									tmpBankSize = ReadInt( UDP_Stream )
									tmpCmd\bank = CreateBank( tmpBankSize )
									For j = 0 To tmpBankSize-1
										tmpByte = ReadByte( UDP_Stream )
										PokeByte( tmpCmd\bank, j, tmpByte )
									Next  
									tmpCmd\parameter[ i ] = tmpCmd\bank
								End Select 
							Next 
						EndIf 
					;	Delete Each UDP_Client
						For tmpClient.UDP_CLient = Each UDP_Client
							If tmpClient\ID = UDP_PlayerID Then 
								tmpClient\Host = 1
								UDP_ServerClient = tmpCLient
								UDP_ServerIP = tmpClient\IP
								UDP_ServerPort = tmpClient\Port
								UDP_ServerID = tmpClient\ID
							Else 
								tmpClient\Host = 0
							EndIf 
							tmpClient\waitping = 0
						Next 		
						UDP_Host = 1
						For tmpClient.UDP_Client = Each UDP_Client
							tmpClient\stream = UDP_Stream
							If UDP_NetMode = UDP_LanGame Then 
								tmpClient\stream = CreateUDPStream()
							EndIf 
						Next 
					Else
						tmpID = ReadInt( UDP_Stream )
						If tmpID => 0 And tmpID <= UDP_MaxPlayers
							If UDP_ClientInfo( tmpID ) <> Null Then 
								For tmpClient.UDP_Client = Each UDP_Client 
									If tmpClient\host = 1 Then tmpClient\host = 0 : Exit 
								Next 
								UDP_ClientInfo( tmpID )\host = 1
								UDP_ServerPort = UDP_ClientInfo( tmpID )\port
								UDP_ServerIP = UDP_ClientInfo( tmpID )\IP	
								UDP_ServerClient = UDP_ClientInfo( tmpID )			
								UDP_ServerID = UDP_ClientInfo( tmpID )\ID
								UDP_Connect = 1
								UDP_Connected = 0
								UDP_ChangeHostConnect = 1  
							EndIf
						EndIf
						
					EndIf 
				Case UDP_StartSendServerFile
					tmpID = ReadInt( UDP_Stream )
					tmpGet = 0
					For tmpServerFile.UDP_ServerFile = Each UDP_ServerFile 
						If tmpID = tmpServerFile\ID Then get = 1 : Exit
					Next 
					If get = 0 Then tmpServerFile.UDP_ServerFile = New UDP_ServerFile
					tmpServerFile\ID = tmpID
					tmpServerFile\size = ReadInt( UDP_Stream )
					tmpServerFile\filename$ = ReadString( UDP_Stream )
					tmpServerFile\transfer = 1
					tmpServerFile\byte = CreateBank( UDP_PackedSize )
					If tmpServerFile\datei = 0 Then 
						tmpServerFile\datei = UDP_CreateFile( tmpServerFile\filename$ )
					EndIf 
					UDP_ServerFile = tmpServerFile
					WriteByte( UDP_ServerStream, UDP_AnswerSendServerFile )
					WriteInt( UDP_ServerStream, UDP_PlayerID )
					WriteInt( UDP_ServerStream, tmpID )
					SendUDPMsg( UDP_ServerStream, UDP_ServerIP, UDP_ServerPort )
				Case UDP_SendServerFileBytes
					tmpByteID = ReadInt( UDP_Stream )
					If UDP_ServerFile <> Null Then 
						If UDP_ServerFile\byteID <> tmpByteID Then 
							ReadBytes( UDP_ServerFile\byte, UDP_Stream, 0, UDP_PackedSize )
							If UDP_ServerFile\datei <> 0 Then WriteBytes( UDP_ServerFile\byte, UDP_ServerFile\datei, 0, UDP_PackedSize )
							UDP_ServerFile\byteID = tmpByteID
						EndIf
					EndIf 
					WriteByte( UDP_ServerStream, UDP_SendServerFileBytes )
					WriteInt( UDP_ServerStream, UDP_PlayerID )
					WriteInt( UDP_ServerStream, tmpByteID )
					SendUDPMsg( UDP_ServerStream, UDP_ServerIP, UDP_ServerPort )
				Case UDP_EndSendServerFile
					If UDP_ServerFile <> Null Then 
						If UDP_ServerFile\datei <> 0 Then CloseFile( UDP_ServerFile\datei )
						FreeBank( UDP_ServerFile\byte )
						Delete UDP_ServerFile
					EndIf 
					WriteByte( UDP_ServerStream, UDP_EndSendServerFile )
					WriteInt( UDP_ServerStream, UDP_PlayerID )
					SendUDPMsg( UDP_ServerStream, UDP_ServerIP, UDP_ServerPort )
				Case UDP_EndSend 
					UDP_Connected = 1
				Case UDP_ReConnectMsg
					UDP_ReConnect( )
					WriteByte( UDP_ServerStream, UDP_ReConnectMsg )
					WriteInt( UDP_ServerStream, UDP_PlayerID )
					SendUDPMsg( UDP_ServerStream, UDP_ServerIP, UDP_ServerPort )
				End Select 
			Wend 
			If UDP_ServerClient <> Null Then 
				tmpSendPing = 0
				If UDP_SendPingTime <= MilliSecs() Then 
					tmpSendPing = 1
					UDP_SendPingTime = MilliSecs() + UDP_PingSendTime 
				EndIf 
				If UDP_GetPingFromServer > 0 
					If (MilliSecs()-UDP_GetPingFromServer) > UDP_PingSendTime And tmpSendPing = 1 Then  
						UDP_ServerClient\Ping = ( MilliSecs() - UDP_GetPingFromServer ) / 2
					EndIf 	
				EndIf 
			EndIf 
			If UDP_ChangeName = 1 Then 
				UDP_ChangeName = 0
				WriteByte( UDP_ServerStream, UDP_NameMsg )
				WriteInt( UDP_ServerStream, UDP_PlayerID )
				WriteString( UDP_ServerStream, UDP_NewName$ )
				SendUDPMsg( UDP_ServerStream, UDP_ServerIP, UDP_ServerPort )
			EndIf  
			For tmpCmd.UDP_Cmd = Each UDP_Cmd
				tmpDel = 0
				If tmpCmd\typ = 4
					If MilliSecs()-tmpCmd\sendms => 20000 Then tmpDel = 1
				EndIf
				If tmpCmd\typ = 1 Then 
					WriteByte( UDP_ServerStream, UDP_CmdMsg )
					WriteByte( UDP_ServerStream, 0 )
					WriteInt( UDP_ServerStream, UDP_PlayerID )
					WriteInt( UDP_ServerStream, tmpCmd\ID )
					WriteInt( UDP_ServerStream, tmpCmd\toID )
					For i = 0 To tmpCmd\MaxParameter
						Select tmpCmd\info\parameter[i]
						Case 0 : WriteInt( UDP_ServerStream, tmpCmd\parameter[i] )
						Case 1 : WriteFloat( UDP_ServerStream, tmpCmd\parameter[i] )
						Case 2 : WriteString( UDP_ServerStream, tmpCmd\parameter[i] )
						Case 3 : WriteByte( UDP_ServerStream, tmpCmd\parameter[i] )
						Case 4 : WriteShort( UDP_ServerStream, tmpCmd\parameter[i] )
						Case 5
							tmpBankSize = BankSize( tmpCmd\parameter[ i ] )
							WriteInt( UDP_ServerStream, tmpBankSize )
							For j = 0 To tmpBankSize-1
								WriteByte( UDP_ServerStream, PeekByte( tmpCmd\Parameter[ i ], j ) )
							Next 
						End Select 
					Next 
					SendUDPMsg( UDP_ServerStream, UDP_ServerIP, UDP_ServerPort )
					tmpDel = 1
				EndIf
				If tmpCmd\typ = 3 Then
					If tmpCmd\del = 0 Then 
						WriteByte( UDP_ServerStream, UDP_CmdMsg )
						WriteByte( UDP_ServerStream, 1 )
						WriteInt( UDP_ServerStream, UDP_PlayerID )						
						WriteInt( UDP_ServerStream, tmpCmd\ID )
						WriteInt( UDP_ServerStream, tmpCmd\info\ID )
						WriteInt( UDP_ServerStream, tmpCmd\toID )
						For i = 0 To tmpCmd\MaxParameter
							Select tmpCmd\info\parameter[i]
							Case 0 : WriteInt( UDP_ServerStream, tmpCmd\parameter[i] )
							Case 1 : WriteFloat( UDP_ServerStream, tmpCmd\parameter[i] )
							Case 2 : WriteString( UDP_ServerStream, tmpCmd\parameter[i] )
							Case 3 : WriteByte( UDP_ServerStream, tmpCmd\parameter[i] )
							Case 4 : WriteShort( UDP_ServerStream, tmpCmd\parameter[i] )
							Case 5
								tmpBankSize = BankSize( tmpCmd\parameter[ i ] )
								WriteInt( UDP_ServerStream, tmpBankSize )
								For j = 0 To tmpBankSize-1
									WriteByte( UDP_ServerStream, PeekByte( tmpCmd\Parameter[ i ], j ) )
								Next 	
							End Select 
						Next 
						SendUDPMsg( UDP_ServerStream, UDP_ServerIP, UDP_ServerPort )
						tmpCmd\del = 1
						tmpCmd\sendms = MilliSecs() 
					Else
						tmpPing = 512
						If UDP_ServerClient <> Null Then 
							tmpPing = UDP_ServerClient\Ping * 4
							If tmpPing < 64 tmpPing = 64
						EndIf 
						If MilliSecs()-tmpCmd\sendms > tmpPing Then 
							tmpCmd\del = 0
							tmpCmd\send = tmpCmd\send + 1
							If tmpCmd\send > 3 Then tmpDel = 1
						EndIf
					EndIf
				EndIf 
				If tmpDel = 1 Then Delete tmpCmd
			Next 
		EndIf 
	EndIf 
End Function 


Function UDP_AddCmd( tmpParameterList$ )
	tmpID = 1
	For tmpCmdAll.UDP_Cmd = Each UDP_Cmd
		If tmpCmdAll\typ = 0 And tmpCmdAll\ID => tmpID Then tmpID = tmpCmdAll\ID + 1
	Next 
	tmpCmd.UDP_Cmd = New UDP_Cmd
	tmpCmd\ID = tmpID
	tmpCmd\typ = 0
	;String Parsen
	If Not (  Mid( tmpParameterList$, Len( tmpParameterList$ ), 1 ) = ",") Then tmpParameterList$ = tmpParameterList$ + ","
	tmpParameterList$ = Replace( Trim( Lower( tmpParameterList$ ) ), " ", "" )
	tmpCount = -1
	Repeat
		tmpKomma = Instr( tmpParameterList$, "," )
		If tmpKomma <> 0
			tmpParameter$ = Mid( tmpParameterList$, 1, tmpKomma-1 )
			tmpCount = tmpCount + 1
			Select tmpParameter$
			Case "int" : tmpCmd\parameter[ tmpCount ] = 0
			Case "float" : tmpCmd\parameter[ tmpCount ] = 1
			Case "string" : tmpCmd\parameter[ tmpCount ] = 2
			Case "byte" : tmpCmd\parameter[ tmpCount ] = 3
			Case "short" : tmpCmd\parameter[ tmpCount ] = 4
			Case "bank" : tmpCmd\parameter[ tmpCount ] = 5
			End Select 
			tmpParameterList$ = Mid( tmpParameterList$, tmpKomma+1, Len( tmpParameterList ) )
		EndIf 
	Until tmpKomma = 0 Or tmpCount = UDP_MaxParameter
	tmpCmd\MaxParameter = tmpCount
	Return Handle( tmpCmd )
End Function 

Function UDP_SendCmd( tmpCmdHandle, tmpParameterList$, tmpSafety=0, tmpToID=-1 )
	Local tmpCmdOb.UDP_Cmd = Object.UDP_Cmd( tmpCmdHandle )
	Local tmpKomma = 0, tmpKomma2 = 0
	Local tmpCmd.UDP_Cmd = Null
	If tmpCmdOb\MaxParameter > 0 Then 
		If Not (  Mid( tmpParameterList$, Len( tmpParameterList$ ), 1 ) = ",") Then tmpParameterList$ = tmpParameterList$ + ","
		For i = 0 To tmpCmdOb\MaxParameter
			tmpPlus = 0
			If tmpCmdOb\Parameter[ i ] <> 2 Then 
				tmpKomma2 = Instr( tmpParameterList$, ",", tmpKomma+1 )
			Else
				tmp34_1 = Instr( tmpParameterList$, Chr(34), tmpKomma+1 )
				tmp34_2 = Instr( tmpParameterList$, Chr(34), tmp34_1+1 )
				If tmp34_1 <> 0 And tmp34_2 <> 0 Then
					tmpKomma = tmp34_1 
					tmpKomma2 = tmp34_2
					tmpPlus = 1
				Else
					tmpKomma2 = Instr( tmpParameterList$, ",", tmpKomma+1 )
				EndIf 
			EndIf 
			tmpParameter$ = Mid( tmpParameterList$, tmpKomma+1 , tmpKomma2 - tmpKomma - 1 )
			tmpKomma = tmpKomma2 + tmpPlus			
			UDP_tmpParameter$( i ) = tmpParameter 
		Next
	Else
		UDP_tmpParameter$( 0 ) = tmpParameterList$
	EndIf  
	If tmpSafety = 0 Then 
		tmpCmd.UDP_Cmd = New UDP_Cmd
		tmpCmd\info = tmpCmdOb
		tmpCmd\typ = 1 
		tmpCmd\ID = tmpCmdOb\ID
		tmpCmd\FromID = UDP_PlayerID
		tmpCmd\ToID = tmpToID
		If tmpCmd\toID > 0 And tmpCmd\toID <= UDP_MaxPlayers Then 
			If UDP_ClientInfo( tmpCmd\toID ) = Null Or tmpCmd\toID = UDP_PlayerID Then tmpCmd\toID = -1
		ElseIf tmpCmd\toID < 0 Or tmpCmd\toID > UDP_MaxPlayers
			tmpCmd\toID = -1
		EndIf  
		;String übergeben
		For i = 0 To tmpCmd\info\MaxParameter
			tmpCmd\Parameter[ i ] = UDP_TmpParameter$( i )
		Next 
		tmpCmd\MaxParameter = tmpCmd\info\MaxParameter		
	Else
		If UDP_Host = 1
			For tmpClient.UDP_Client = Each UDP_Client
				If ( ( tmpToID > 0 And tmpToID <= UDP_MaxPlayers ) And tmpClient\ID = tmpToID ) Or ( ( tmpToID <= 0 Or tmpToID > UDP_MaxPlayers ) And tmpClient <> UDP_Player ) Then 
					tmpCmd.UDP_Cmd = New UDP_Cmd
					tmpCmd\info = tmpCmdOb				
					tmpCmd\typ = 3
					tmpCmd\ID = UDP_MsgEntID
					UDP_MsgEntID =( UDP_MsgEntID + 1 ) Mod 2147483647
					tmpCmd\FromID = UDP_PlayerID
					tmpCmd\ToID = tmpClient\ID
					If tmpCmd\toID > 0 And tmpCmd\toID <= UDP_MaxPlayers Then 
						If UDP_ClientInfo( tmpCmd\toID ) = Null Or tmpCmd\toID = UDP_PlayerID Then tmpCmd\toID = -1
					ElseIf tmpCmd\toID < 0 Or tmpCmd\toID > UDP_MaxPlayers
						tmpCmd\toID = -1
					EndIf  
					;String übergeben
					For i = 0 To tmpCmd\info\MaxParameter
						tmpCmd\Parameter[ i ] = UDP_TmpParameter$( i )
					Next 
					tmpCmd\MaxParameter = tmpCmd\info\MaxParameter		
				EndIf
			Next 
		Else
			tmpCmdOb.UDP_Cmd = Object.UDP_Cmd( tmpCmdHandle )
			tmpCmd.UDP_Cmd = New UDP_Cmd
			tmpCmd\info = tmpCmdOb				
			tmpCmd\typ = 3
			tmpCmd\ID = UDP_MsgEntID
			UDP_MsgEntID =( UDP_MsgEntID + 1 ) Mod 2147483647
			tmpCmd\FromID = UDP_PlayerID
			tmpCmd\ToID = tmpToID
			If tmpCmd\toID > 0 And tmpCmd\toID <= UDP_MaxPlayers Then 
				If UDP_ClientInfo( tmpCmd\toID ) = Null Or tmpCmd\toID = UDP_PlayerID Then tmpCmd\toID = -1
			ElseIf tmpCmd\toID < 0 Or tmpCmd\toID > UDP_MaxPlayers
				tmpCmd\toID = -1
			EndIf  
			;String übergeben
			For i = 0 To tmpCmd\info\MaxParameter
				tmpCmd\Parameter[ i ] = UDP_TmpParameter$( i )
			Next 
			tmpCmd\MaxParameter = tmpCmd\info\MaxParameter		
		EndIf 
	EndIf		
End Function 


Function UDP_RecvCmd( )
	For tmpCmd.UDP_Cmd = Each UDP_Cmd
		If tmpCmd\typ = 2 Then 
			tmpCmdOb.UDP_Cmd = Object.UDP_Cmd( UDP_CmdID )
			For i = 0 To tmpCmd\MaxParameter
				If tmpCmdOb <> Null Then 
					If tmpCmdOb\Parameter[ i ] = 5 Then 
						FreeBank( UDP_CmdParameter( i ) )
						UDP_CmdParameter( i ) = 0
					EndIf
				EndIf
				UDP_CmdParameter( i ) = tmpCmd\Parameter[ i ]
			Next 
			UDP_CmdID = Handle( tmpCmd\info )
			UDP_CmdMaxParameter = tmpCmd\MaxParameter
			UDP_CmdFromID = tmpCmd\FromID
			Delete tmpCmd
			Return 1
		EndIf
	Next 
	Return 0
End Function 

Function UDP_End( )
	If UDP_Host = 1 Then
		If UDP_Connected = 1 
			For tmpClient.UDP_Client = Each UDP_Client
				If tmpClient <> UDP_Player Then 
					WriteByte( tmpClient\Stream, UDP_EndMsg )
					WriteInt( tmpClient\Stream, 0 )
					SendUDPMsg( tmpClient\Stream, tmpClient\IP, tmpClient\Port )
					If tmpClient\stream <> UDP_Stream Then CloseUDPStream( tmpClient\stream )
					Delete tmpClient.UDP_Client
				EndIf
			Next   
			CloseUDPStream( UDP_Stream )
		EndIf 
	Else
		If UDP_Connected = 1
			If UDP_ServerPing < 64 Then UDP_ServerPing = 64
			For i = 1 To 5
				WriteByte( UDP_ServerStream, UDP_EndMsg )
				WriteInt( UDP_ServerStream, UDP_PlayerID )
				SendUDPMsg( UDP_ServerStream, UDP_ServerIP, UDP_ServerPort )
				ms = MilliSecs()
				tmpGet = 0
				Repeat 
					If RecvUDPMsg( UDP_Stream ) Then 
						byte = ReadByte( UDP_Stream )
						If byte = UDP_End Then 
							tmpID = ReadInt( UDP_Stream )
							If tmpID = UDP_PlayerID Then tmpGet = 1 : Exit
						EndIf
					EndIf
				Until ms <= MilliSecs() - UDP_ServerPing  
				If tmpGet = 1 Then Exit
			Next 
			If UDP_ServerStream <> UDP_Stream Then CloseUDPStream( UDP_ServerStream )
			CloseUDPStream( UDP_Stream )
			Delete Each UDP_Client
		EndIf 
	EndIf
End Function 

Function UDP_GetEvent( )
	tmpEvent.UDP_Event = First UDP_Event
	If tmpEvent = Null Then Return 0
	UDP_EventID = tmpEvent\Event
	UDP_EventData = tmpEvent\EventData$
	UDP_EventMsg = tmpEvent\EventMsg$
	UDP_EventMsg2$ = tmpEvent\EventMsg2$
	Delete tmpEvent
	Return 1
End Function

Function UDP_KickPlayer( tmpPlayerID$, tmpReason$="" )
	If UDP_Host = 0 Return 
	tmpID = 0
	If Int( tmpPlayerID ) = tmpPlayerID Then 
		tmpGet = 0
		For tmpClient.UDP_Client = Each UDP_Client
			If Int( tmpPlayerID ) = tmpClient\ID Then  tmpID = tmpClient\ID : Exit
			If Int( tmpPlayerID ) = tmpClient\IP Then
				UDP_KickPlayer( tmpClient\ID, tmpReason$ )
				tmpGet = 1
			EndIf
		Next 
		If tmpGet = 1 Then Return 
	Else
		For tmpClient.UDP_Client = Each UDP_Client
			If tmpClient\IP = UDP_IntIP( tmpPlayerID ) Then 
				UDP_KickPlayer( tmpClient\ID, tmpReason$ )
			EndIf
		Next 
		Return 
	EndIf 
	If tmpID = 0 Return
	For tmpClient.UDP_Client = Each UDP_Client
		If tmpClient <> UDP_Player Then 
			WriteByte( tmpClient\stream, UDP_KickMsg )
			WriteInt( tmpClient\stream, tmpID )
			WriteString( tmpClient\stream, tmpReason )
			SendUDPMsg( tmpClient\stream, tmpClient\IP, tmpClient\Port )
		EndIf 
	Next 
	If UDP_ClientInfo( tmpID ) <> Null
		UDP_ClientInfo( tmpID )\kick = 1
	EndIf 
	tmpEvent.UDP_Event = New UDP_Event
	tmpEvent\Event = UDP_EventPlayerKicked 
	tmpEvent\EventData$ = tmpID
	tmpEvent\EventMsg$ = tmpReason
End Function 

Function UDP_BannPlayer( tmpPlayerID$, tmpTime=0, tmpReason$="" )
	If UDP_Host = 0 Return 
	If Int( tmpPlayerID ) = tmpPlayerID Then 
		tmpIP = 0
		tmpID = 0
		For tmpClient.UDP_Client = Each UDP_Client
			If Int( tmpPlayerID ) = tmpClient\ID Then 
				tmpIP = tmpClient\IP
				tmpID = tmpClient\ID
				Exit
			EndIf 
			If Int( tmpPlayerID ) = tmpClient\IP Then 
				UDP_BannPlayer( tmpClient\ID, tmpTime, tmpReason$ )
			EndIf 
		Next 
		If tmpIP = 0 Return 
		tmpGetBann = 0
		For tmpBanCheck.UDP_Ban = Each UDP_Ban
			If tmpBanCheck\IP = tmpIP Then 
				tmpBanCheck\time = tmpTime
				tmpBanCheck\ms = MilliSecs()
				tmpGetBann = 1
			EndIf  
		Next 
		If tmpGetBann = 0
			tmpBan.UDP_ban = New UDP_Ban
			tmpBan\IP = tmpIP
			tmpBan\time = tmpTime
			If tmpBan\Time < 0 Then tmpBan\time = 0  : Else : tmpBan\ms = MilliSecs()
			tmpBan\reason = tmpReason
		EndIf 
		If UDP_ClientInfo( tmpID ) <> Null
			UDP_ClientInfo( tmpID )\kick = 1
		EndIf 
		For tmpClient.UDP_Client = Each UDP_Client
			If tmpClient <> UDP_Player Then 
				WriteByte( tmpClient\stream, UDP_BannMsg )
				WriteInt( tmpClient\stream, tmpID )
				WriteInt( tmpClient\stream, tmpTime )
				WriteString( tmpClient\stream, tmpReason$ )
				SendUDPMsg( tmpClient\stream, tmpClient\IP, tmpClient\Port )
			EndIf 
		Next 
		tmpEvent.UDP_Event = New UDP_Event
		tmpEvent\Event = Event_PlayerBanned
		tmpEvent\EventData = tmpID
		tmpEvent\EventMsg$ = tmpReason
		tmpEvent\EventMsg2$ = tmpTime
	Else
		tmpBan.UDP_ban = New UDP_Ban
		tmpBan\IP = UDP_IntIP( tmpPlayerID )
		tmpBan\time = tmpTime
		If tmpBan\Time < 0 Then tmpBan\time = 0  : Else : tmpBan\ms = MilliSecs()
		tmpBan\reason = tmpReason
		For tmpClient.UDP_Client = Each UDP_Client
			If tmpClient\IP = UDP_IntIP( tmpPlayerID ) Then 
				UDP_BannPlayer( tmpClient\ID, tmpTime, tmpReason$ )
			EndIf 
		Next 			
	EndIf 
End Function 

Function UDP_ChangeName$( tmpNewName$ )
	If UDP_Host = 1 Then 
		If UDP_ServerClient = Null Then Return 
		UDP_ServerClient\changename = 1
		UDP_ServerClient\newname$ = tmpNewName$
	Else
		UDP_ChangeName = 1
		UDP_NewName$ = tmpNewName$
	EndIf
End Function 


Function UDP_ChangeHost( tmpNewHostID, tmpCmdHandle=-1, tmpParameterList$="" )
	If UDP_Host = 0 Or UDP_Dediziert = 1 Then Return
	If tmpNewHostID > 0 And tmpNewHostID <= UDP_MaxPlayers Then 
		If UDP_ClientInfo( tmpNewHostID ) <> Null Then  
			tmpClient.UDP_Client = UDP_ClientInfo( tmpNewHostID )
			WriteByte( tmpClient\stream, UDP_ChangeHostMsg )
			WriteByte( tmpClient\stream, 1 )
			WriteByte( tmpClient\stream, UDP_NetMode )
			If tmpCmdHandle > -1 Then 
				WriteByte( tmpClient\stream, 1 )
				Local tmpCmdOb.UDP_Cmd = Object.UDP_Cmd( tmpCmdHandle )
				Local tmpKomma = 0, tmpKomma2 = 0
				Local tmpCmd.UDP_Cmd = Null
				If tmpCmdOb\MaxParameter > 0 Then 
					If Not (  Mid( tmpParameterList$, Len( tmpParameterList$ ), 1 ) = ",") Then tmpParameterList$ = tmpParameterList$ + ","
					For i = 0 To tmpCmdOb\MaxParameter
						tmpPlus = 0
						If tmpCmdOb\Parameter[ i ] <> 2 Then 
							tmpKomma2 = Instr( tmpParameterList$, ",", tmpKomma+1 )
						Else
							tmp34_1 = Instr( tmpParameterList$, Chr(34), tmpKomma+1 )
							tmp34_2 = Instr( tmpParameterList$, Chr(34), tmp34_1+1 )
							If tmp34_1 <> 0 And tmp34_2 <> 0 Then
								tmpKomma = tmp34_1 
								tmpKomma2 = tmp34_2
								tmpPlus = 1
							Else
								tmpKomma2 = Instr( tmpParameterList$, ",", tmpKomma+1 )
							EndIf 
						EndIf 
						tmpParameter$ = Mid( tmpParameterList$, tmpKomma+1 , tmpKomma2 - tmpKomma - 1 )
						tmpKomma = tmpKomma2 + tmpPlus			
						UDP_tmpParameter$( i ) = tmpParameter 
					Next
				Else
					UDP_tmpParameter$( 0 ) = tmpParameterList$
				EndIf  
				WriteInt( tmpClient\stream, tmpCmdOb\ID )
				For i = 0 To tmpCmdOb\MaxParameter
					Select tmpCmdOb\parameter[i]
					Case 0 : WriteInt( tmpClient\stream, UDP_tmpParameter( i ) )
					Case 1 : WriteFloat( tmpClient\stream, UDP_tmpParameter( i ) )
					Case 2 : WriteString( tmpClient\stream, UDP_tmpParameter( i ) )
					Case 3 : WriteByte( tmpClient\stream, UDP_tmpParameter( i ) )
					Case 4 : WriteShort( tmpClient\stream, UDP_tmpParameter( i ) )
					Case 5
						tmpBankSize = BankSize( UDP_tmpParameter( i ) )
						WriteInt( tmpClient\stream, tmpBankSize )
						For j = 0 To tmpBankSize-1
							WriteByte( tmpClient\stream, PeekByte( UDP_tmpParameter( i ), j ) )
						Next 										
					End Select 
				Next 
			Else
				WriteByte( tmpClient\stream, 0 )
			EndIf 
			SendUDPMsg( tmpClient\stream, tmpClient\IP, tmpClient\Port )
			For tmpClientAll.UDP_Client = Each UDP_Client
				If tmpClientAll\stream <> 0 Then 
					If tmpClientAll <> tmpClient Then 
						WriteByte( tmpClientAll\stream, UDP_ChangeHostMsg )
						WriteByte( tmpClientAll\stream, 0 )
						WriteInt( tmpClientAll\stream, tmpClient\ID )
						SendUDPMsg( tmpClientAll\stream, tmpClientAll\IP, tmpClientAll\Port )
					EndIf 
					If tmpClientAll\stream <> UDP_Stream Then CloseUDPStream( tmpClientAll\stream )
				EndIf 
				tmpClientAll\waitping = 0
			Next 
			tmpClient\host = 1
			UDP_Player\host = 0
			UDP_ServerPort = tmpClient\port
			UDP_ServerIP = tmpClient\IP
			UDP_ServerClient = tmpClient
			UDP_Host = 0			
			UDP_Connect = 1
			UDP_Connected = 0
			UDP_ServerID = tmpClient\ID
			UDP_ChangeHostConnect = 1
			UDP_ServerStream = UDP_Stream
			If UDP_NetMode = UDP_LanGame Then UDP_ServerStream = CreateUDPStream( )
		EndIf  
	EndIf 
End Function 


Function UDP_CountServer( tmpNetMode = UDP_LanGame, tmpServerPort = 8000 )
	UDP_Stream = CreateUDPStream( )
	UDP_Connect = 2
	UDP_ServerIP = UDP_IntIP( UDP_BroadcastIP$() )	
	UDP_ServerPort = tmpServerPort
	WriteByte( UDP_Stream, UDP_GetInformationMsg )
	SendUDPMsg( UDP_Stream, UDP_ServerIP, tmpServerPort )
	UDP_ServerGlobalPing = MilliSecs()
End Function 

Function UDP_AddServerFile( tmpFileName$ )
	If UDP_Host = 1 Then
		tmpFileTyp = FileType( tmpFileName$ )
		If  tmpFileTyp = 1 Then 
			If FileSize( tmpFileName$ ) < 2147483647 Then 
				UDP_ServerFileID = ( UDP_ServerFileID + 1 )
				tmpFile.UDP_ServerFile = New UDP_ServerFile
				tmpFile\filename$ = tmpFileName$
				tmpFile\size = FileSize( tmpFile\filename$ )
				tmpFile\ID = UDP_ServerFileID
				tmpFile\transfer = 0
				UDP_SendServerFiles = 1
			EndIf 
		ElseIf tmpFileTyp = 2 Then
			tmpDir = ReadDir( tmpFileName$ )
			If Right( tmpFilename$, 1 ) = "\" Then tmpFilename = Mid( tmpFilename$, 1, Len( tmpFilename$ ) - 1 )
			NextFile( tmpDir ) : NextFile( tmpDir )
			file$ = NextFile( tmpDir )
			While file$ <> ""  
				UDP_AddServerFile( tmpFilename$ + "\" + file$ )
				file$ = NextFile( tmpDir )
			Wend 
		EndIf 		
	EndIf
End Function 


Function UDP_CreateFile( tmpPath$, tmpDir$ = "" )
	tmpPath$ = Replace( tmpPath$, "/", "\" )
	If Instr( tmpPath$, "\" ) > 0 Then 		
		tmp_Dir$ =  Mid( tmpPath$, 1, Instr( tmpPath$, "\" )-1 )	
		tmpNewDir$ = CurrentDir() + tmpDir$ + tmp_Dir$
		;DebugLog tmpNewDir$
		If FileType( tmpNewDir ) = 0 Then 
			CreateDir( tmpNewDir$ ) 
		EndIf 
		tmpNewPath$ = Mid( tmpPath$, Instr( tmpPath$, "\" )+1, Len( tmpPath$ ) )
		If tmpDir$ <> "" Then
			If Right(tmpDir, 1) = "\" Then tmpDir = Mid(tmpDir, 1, Len(tmpDir) - 1 ) 
			If Right(tmp_Dir, 1) = "\" Then tmp_Dir = Mid(tmp_Dir, 1, Len(tmp_Dir) - 1 ) 
			tmpOldPath$ = tmpdir$ + "\" + tmp_Dir$ + "\"
		Else
			tmpOldPath$ = tmp_Dir$ + "\"
		EndIf 
		tmpDat = UDP_CreateFile( tmpNewPath$, tmpOldPath$ )
		Return tmpDat
	Else
		tmpDat = WriteFile( tmpDir$ + tmpPath$ )
		Return tmpDat
	EndIf 
End Function 


Function UDP_ReConnect( )
	If UDP_Host = 1 Then 
		For tmpClient.UDP_Client = Each UDP_Client
			tmpClient\recon = 1
			tmpClient\reconms = 0
		Next 
	Else
		UDP_End()
		UDP_Stream = CreateUDPStream( )
		UDP_Connected = 0
		UDP_Connect = 1
	EndIf
End Function 	

 
		
Function UDP_BroadcastIP$() 
	CountHostIPs("")
	Local IP$ = DottedIP(HostIP(1))	
	Local Subnetmask$ = "255.255.255.0"	
	Local IPDigits[3], SubnetDigits[3] 
	Local pos, i 	
	For i = 0 To 3 
		pos = Instr( IP$, "." ) 
		If pos > 0 Then 
			IPDigits[i] = Left( IP$, pos -1 ) 
			IP$ = Mid( IP$, pos +1 ) 
		Else 
			IPDigits[i] = IP$ 
		EndIf 
	Next 		
	For i = 0 To 3 
		pos = Instr( Subnetmask$, "." ) 
		If pos > 0 Then 
			SubnetDigits[i] = Left( Subnetmask$, pos -1 ) Xor $FF 
			Subnetmask$ = Mid( Subnetmask$, pos +1 ) 
		Else 
			SubnetDigits[i] = Subnetmask$ Xor $FF 
		EndIf 
	Next
	For i = 0 To 3 
		IPDigits[i] = IPDigits[i] Or SubnetDigits[i] 
	Next		
	Return IPDigits[0] +"." +IPDigits[1] +"." +IPDigits[2] +"." +IPDigits[3] 	
End Function

	
	
	
Function UDP_IntIP( IP$ )
	If Instr( IP$, "." )<>0 Then a1 = Int( Left( IP$, Instr( IP$, "." )-1 ) ) : IP$ = Right( IP$, Len( IP$ ) - Instr( IP$, "." ) )
	If Instr( IP$, "." )<>0 Then a2 = Int( Left( IP$, Instr( IP$, "." )-1 ) ) : IP$ = Right( IP$, Len( IP$ ) - Instr( IP$, "." ) )
	If Instr( IP$, "." )<>0 Then a3 = Int( Left( IP$, Instr( IP$, "." )-1 ) ) : IP$ = Right( IP$, Len( IP$ ) - Instr( IP$, "." ) )
	a4 = Int( IP$ )
	Return ( a1 Shl 24 ) + ( a2 Shl 16 ) + ( a3 Shl 8 ) + a4
End Function
