; ID: 1908
; Author: Ghost Dancer
; Date: 2007-01-31 02:09:00
; Title: GNet framework
; Description: GNet functionality that can be easily integrated into your code

'------------------------------------
'--- multiplayer.bmx include file ---
'------------------------------------

'******************************************************************************
'Blitz Max multiplayer framework

'Version: 	V1.2
'Date:		January 2007
'Author:	Ghost Dancer
'
'this has been written so it can be easily slotted into any game
'this code is reusable so any custom code should be done externally
'see multiplayer_test.bmx for an example of how to use it
'******************************************************************************

'connection types
Const netcon_none = 0, netcon_host = 1, netcon_client = -1

'===================================================================
Type TMultiplayer
'===================================================================
	Const netind_disconnect = 0, netind_connect = 1, netind_ping = 2	'other indexes should be set outside this module
	Const restartTicks = 5									'number of ticks after disconnect before recreating host objects
	
	Field host:TGNetHost = CreateGNetHost()					'host type
	Field localObj:TGNetObject = CreateGNetObject(host)		'local net object
	Field remoteObj:TGNetObject								'remote net object
	Field objList:TList 									'list of received, remote objects

	Field port = 8086, timeout = 5000						'these can be changed using the methods below
	Field conType, connected, ping, lastPing, currentMs, lastMs, pingTimer, displayPing, restartHost
	
	Field messageQ:TNetMessageQueue = New TNetMessageQueue		'for error messages to be output by main code
	
	Field localIp$, internetIp$
	
	
	'-------------------------------------------------------------------
	Method New()
	'-------------------------------------------------------------------
		'get local ip address
		Local ip_array[] = HostIps("")
		Local ip = HostIp("")
		
		localIp$ = DottedIP(ip_array[0])
		
		'get internet ip address
		'*** you will need to specify your own URl here & change the text search below ***
		Local ipStream:TStream = ReadStream("http::www.yourGetIpAddressScript.com") 
		Local stringstart, stringsize
		
		If ipStream
			While Not Eof(ipStream)
				internetIp$ :+ ReadLine(ipStream)
			Wend
			
			stringstart	= internetIp$.Find("<IP_Address>")
			stringstart	:+ "<IP_Address>".length
			stringsize	= internetIp$.Find("</IP_Address>")
			
			internetIp$	= internetIp$[stringstart..stringsize]
		End If
		
		CloseStream ipStream
		
	End Method
	
	
	'-------------------------------------------------------------------
	Method startHost()
	'use this to set up host player
	'-------------------------------------------------------------------
		conType = netcon_host
		createObjectIndexes
		
		If Not GNetListen(host, port) Then
			conType = netcon_none
			messageQ.add "GNetListen failed"
		End If
	End Method
	
	
	'-------------------------------------------------------------------
	Method startClient()
	'use this to set up client
	'-------------------------------------------------------------------
		conType = netcon_client
		createObjectIndexes
	End Method
	
	
	'-------------------------------------------------------------------
	Method connectToHost(ip$)
	'connect to host (used by client)
	'-------------------------------------------------------------------
		If conType = netcon_client Then
			If GNetConnect(host, ip$, port, timeout) Then
				setInt netind_connect, True		'send connect notification to other player
			Else
				conType = netcon_none
				messageQ.add "GNetConnect failed"
			End If
		End If
	End Method
	
	
	'-------------------------------------------------------------------
	Method setInt(index, data)
	'set data in local object - use to set data from your own code
	'-------------------------------------------------------------------
		SetGNetInt localObj, index, data
	End Method
	
	
	'-------------------------------------------------------------------
	Method update()
	'listen for join request
	'-------------------------------------------------------------------
		If restartHost > 0 Then
			'give it a few ticks to send disconect notification before recreating host objects
			
			restartHost:-1
			
			If restartHost = 0 Then
				'close old host & objects
				CloseGNetObject localObj
				CloseGNetHost host
				
				'create new objects (thus clearing any old values)
				host = CreateGNetHost()
				
				localObj = CreateGNetObject:TGNetObject(host)
				
				GCCollect
			End If
		End If
		
		If conType <> netcon_none Then
			GNetSync host ' send to other instance & get updates
			
			'iterate through net objects & process
			objList = GNetObjects(host, GNET_MODIFIED)
			
			For remoteObj = EachIn objList
				'check for connection notification
				If GetGNetInt(remoteObj, netind_connect) And Not connected Then
					setInt netind_connect, True		'send notification back
					connected = True
				End If
				
				'check for disconnect
				If GetGNetInt(remoteObj, netind_disconnect) Then
					disconnect
				End If
				
				'custom processing (define function in external code)
				processObjects(remoteObj)
				
				'get ping data
				currentMs = GetGNetInt(remoteObj, netind_ping)
				
				If currentMs <> lastMs Then
					'remote ping has changed - update vars
					lastMs = currentMs
					
					lastPing = MilliSecs()
				End If
			Next
			
			If connected Then
				'send current ms to remote player
				setInt netind_ping, MilliSecs()
				
				'calc current ping
				If lastPing > 0 Then ping = MilliSecs() - lastPing
					
				'calc new ping every second
				If MilliSecs() - pingTimer >= 1000 Then
					displayPing = ping

					'update ping timer
					pingTimer = MilliSecs()
				End If
				
				'check for timeout
				If ping > timeout Then disconnect
			End If

		End If
	End Method
	
	
	'-------------------------------------------------------------------
	Method disconnect()
	'disconnect from opponent
	'-------------------------------------------------------------------
		'notify remote player
		setInt netind_disconnect, True
		GNetSync host

		clearConnection
		
		'set flag to recreate host objects etc.
		restartHost = restartTicks
	End Method
	
	
	'-------------------------------------------------------------------
	Method clearConnection()
	'clear connection vars
	'-------------------------------------------------------------------
		'set disconnect vars
		conType = netcon_none
		connected = False
		
		'clear ping data
		ping = 0
		lastPing = 0
		displayPing = 0
	End Method
	
	
	'-------------------------------------------------------------------
	Method createObjectIndexes()
	'create indexes in object to prevent assert failures
	'-------------------------------------------------------------------
		For Local i = 0 To 31
			setInt i, 0
		Next
	End Method
	
	
	'-------------------------------------------------------------------
	Method getPing()
	'exactly what it says on the tin
	'-------------------------------------------------------------------
		Return displayPing
	End Method
	
	
	'-------------------------------------------------------------------
	Method getConnectionType()
	'-------------------------------------------------------------------
		Return conType
	End Method
	
	
	'-------------------------------------------------------------------
	Method isConnected()
	'-------------------------------------------------------------------
		Return connected
	End Method
	
	
	'-------------------------------------------------------------------
	Method getMessage$()
	'get message from queue
	'-------------------------------------------------------------------
		Return messageQ.getFirst$()
	End Method
	

	'-------------------------------------------------------------------
	Method getLocalIp$()
	'return local ip address
	'-------------------------------------------------------------------
		Return localIp$
	End Method
	
	
	'-------------------------------------------------------------------
	Method getInternetIp$()
	'return internet ip address
	'-------------------------------------------------------------------
		Return internetIp$
	End Method
	
	
	'-------------------------------------------------------------------
	Method setPort(newPort)
	'-------------------------------------------------------------------
		port = newPort
	End Method
	
	
	'-------------------------------------------------------------------
	Method setTimeout(newTimeout)
	'-------------------------------------------------------------------
		timeout = newTimeout
	End Method
	
	
End Type


'===================================================================
Type TNetMessageQueue
'message queue for handling error messages so you can easily handle
'them in your game by calling the TMultiplayer.getMessage() method
'===================================================================
	'store message in a list
	Field messageList:TList = CreateList()
	
	'-------------------------------------------------------------------
	Method add(message$)
	'add a new message to the queue
	'-------------------------------------------------------------------
		messageList.AddLast(message)
	End Method
	

	'-------------------------------------------------------------------
	Method getFirst$()
	'get first message from the queue & delete it
	'-------------------------------------------------------------------
		If CountList(messageList) Then Return String(messageList.RemoveFirst())
	End Method
End Type


'------------------------------------
'--- multiplayer_test.bmx example ---
'------------------------------------

'example of how to use multiplayer.bmx
'this emulates how a game might handle network play

Framework BRL.GLMax2D
Import BRL.StandardIO
Import BRL.GNet

Strict

AppTitle = "Multiplayer test"

Include "multiplayer.bmx"

'custom indexes for net object
Const netind_startData = 10, netind_mouseData1 = 11, netind_mouseData2 = 12

'set up variables we need
Global multi:TMultiplayer = New TMultiplayer
Global notifyStart, gameStarted

Global mouse[2+1]
Local i, ipAddress$ = "127.0.0.1", temp$, message$


Graphics 640, 480

'main loop
Repeat
	Cls
	
	DrawText "Ping: " + multi.getPing() + ", Connected: " + multi.isConnected(), 400, 10
	
	'get any new messages from queue
	temp$ = multi.getMessage()
	If temp$ <> "" Then message$ = temp$
	If message$ <> "" Then DrawText message$, 400, 30
	
	'escape resets everything
	If KeyHit(KEY_ESCAPE) Then
		multi.disconnect
		gameStarted = False
		notifyStart = False
		
		message$ = ""
	End If
	
	If gameStarted Then
		If multi.isConnected() Then
			DrawText "Game started", 10, 10
			DrawText "Press Esc to Disconnect", 10, 30
			
			For i = 1 To 2
				'show oponnent mouse status
				DrawText "Opponent mouse " + i + " = " + mouse[i], 10, 50 + (i*10)
				
				'set your mouse status in net object
				multi.setInt i + 10, MouseDown(i)
			Next
		Else
			notifyStart = False
			gameStarted = False
		End If
	Else
		Select multi.getConnectionType()
		Case netcon_none
			'first ask player if they wish to host or join game
			DrawText "Press H to Host, or J to Join", 10, 10
			
			If KeyHit(KEY_H) Then multi.startHost
			If KeyHit(KEY_J) Then multi.startClient
			
		Case netcon_client
			If multi.isConnected() = False ' Not connected
				'if joining, they would normally enter the IP of host computer
				'but for testing purposes we are on same machine
				DrawText "Press C to Connect to " + ipAddress$, 10, 10
				If KeyHit(KEY_C) Then multi.connectToHost(ipAddress$)
			Else
				DrawText "Waiting for host to start...", 10, 10
			End If
			
		Case netcon_host
			If multi.isConnected() = False ' Not connected
				DrawText "Waiting for opponent to join...", 10, 10
			ElseIf notifyStart Then
				DrawText "Client attempting to connect...", 10, 10
			Else
				'you are connected to opponent, game setup options can go here if required
				DrawText "Press S to Start game", 10, 10
				If KeyHit(KEY_S) Then
					multi.setInt netind_startData, True		'send game info to client
					notifyStart = True
				End If
			End If
				
		End Select
	End If
	
	multi.update
	
	Delay 2		'free up some cpu time for other apps
	Flip
Until AppTerminate()

End


'this function is required by TMultiplayer and handles any custom processing
'e.g. any data specific to your game
Function processObjects(remoteObj:TGNetObject)
	If GetGNetInt(remoteObj, netind_startData) And Not gameStarted Then
		gameStarted = True									'start game
		multi.setInt netind_startData, True					'notify other player to start
	Else
		'game data/events (we'll use mouse buttons to keep it simple for this example)
		For Local i = 1 To 2
			mouse[i] = GetGNetInt(remoteObj, i + 10)			'get opponent mouse info
		Next
	End If
End Function
