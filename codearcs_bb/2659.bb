; ID: 2659
; Author: Serpent
; Date: 2010-03-04 07:08:31
; Title: LAN Network Searching
; Description: Allows your program to find others over a LAN network.

Const SearchingMessage$ = "Join Networked Game"
Global ServerStream
Global GameInfoStream
Global SearchStream
Global ListenStream

Type NetGames
	
	Field ServerIP
	Field Name$

End Type

Function InitialiseQuerying()
	
	ListenStream = CreateUDPStream(41110)

End Function

Function EndQuerying()

	CloseUDPStream ListenStream
	
End Function

Function QueryGames()
	
	SearchStream = CreateUDPStream()
	
	For MyIPs = 1 To CountHostIPs("")

		MyIP = HostIP(MyIPs)
		
		IP = MyIP
		
		BinIP$ = Bin$(MyIP)
		
		For X = 1 To Len(BinIP$)
			
			IP = IntIP(Left$(BinIP$, Len(BinIP$) - X) + String$("1",X))
			
			WriteLine SearchStream, SearchingMessage$
			SendUDPMsg SearchStream, IP, 41112
	
		Next
	
	Next
	
	CloseUDPStream SearchStream
	
End Function

Function ListenForGames()
	
	WaitTime = MilliSecs()
	
	Repeat
	
		ReceiveIP = RecvUDPMsg(ListenStream)
		
		If ReceiveIP Then
			
			AlreadyFound = False
			
			For NetGame.NetGames = Each NetGames
			
				If NetGame\ServerIP = ReceiveIP Then AlreadyFound = True : Exit
			
			Next
			
			If Not AlreadyFound Then
			
				NetGame.NetGames = New NetGames
				NetGame\ServerIP = ReceiveIP
				NetGame\Name$ = ReadLine$(ListenStream)
				
				If DottedIP$(NetGame\ServerIP) = "127.0.0.1" Then Delete NetGame
				
			EndIf
			
		EndIf
		
	Until MilliSecs() - WaitTime > 1000 Or ReceiveIP = 0
	
End Function


Function SetupServer()

	ServerStream = CreateUDPStream(41112)
	GameInfoStream = CreateUDPStream(41113)
	
End Function


Function CloseServer()
	
	CloseUDPStream ServerStream
	CloseUDPStream GameInfoStream
	
End Function


Function MakeVisible(GameName$)
	
	If Not ServerStream Then
		ServerStream = CreateUDPStream(41112)
	EndIf
	
	If Not GameInfoStream Then
		GameInfoStream = CreateUDPStream(41113)
	EndIf
	
	If ServerStream And GameInfoStream Then
	
		ReceiveIP = RecvUDPMsg(ServerStream)
		
		If ReceiveIP Then
				
			If ReadLine$(ServerStream) = SearchingMessage$ Then
			
				WriteLine GameInfoStream, GameName$
				
				SendUDPMsg GameInfoStream, ReceiveIP, 41110
				
			EndIf
			
		EndIf
	
	EndIf
	
End Function

	
Function IntIP(BinString$)
	
	ActualIP = 0
	Multiplier = 1
	For X = Len(BinString$) To 1 Step -1
	
		ActualIP = ActualIP + (Int(Mid$(BinString$,X,1)) * Multiplier)
		
		Multiplier = Multiplier * 2
		
	Next
	
	Return ActualIP
	
End Function
