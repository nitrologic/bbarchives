; ID: 2653
; Author: PowerPC603
; Date: 2010-02-05 17:38:13
; Title: Server and client example
; Description: Illustrates communication between server and multiple clients

;***************************************************************************************************
; Server-code
;***************************************************************************************************

; Set application title
AppTitle "Server"
; Setup window size
Graphics 600, 200, 0, 2



; Setup the player-instance
Type TPlayer
	Field Name$ ; The name of the player
	Field stream ; The stream-handle for this player (all messages sent to / received from this player use this TCP-stream)
End Type



; Define a global variable that declares the port-number on which tcp-streams are sent and received
Global Port = 9876
; Create a TCP-server on the given port
Global Server = CreateTCPServer(Port)


; Check if the server was created successfully or not
If Server <> 0 Then
	; If the server was created successfully, print it on the screen
	Print "Server started successfully: " + Str$(Server)
Else
	; If the server couldn't be created, print it on the screen
	Print "Server failed to start."
	; Add 1 second delay
	Delay 1000
	; And end the program
	End
EndIf



; Main loop
While Not KeyHit(1)
	Local msg$ ; A variable used to read the data sent by a player
	Local stream = AcceptTCPStream(Server) ; Check if a new TCPstream has been found (new player requests a connection with the server)

	; If a new connection has been found
	If stream Then
		; Create a new TPlayer instance
		Player.TPlayer = New TPlayer
		; Read the name of the player from the stream
		Player\Name$ = ReadString$(stream)
		; Save the stream-handle in the TPlayer instance
		Player\stream = stream
		; Print the new playername to the screen
		Print "Found new player: " + Player\Name$
	EndIf



	; Process all players
	For Player.TPlayer = Each TPlayer
		; If data was received from this player
		If ReadAvail(Player\stream) Then
			; Read the integer value that was sent from the player's client to the server
			msg$ = ReadString$(Player\stream)

			; Print the message onscreen that was sent by the player
			Print "Message " + Chr$(34) + msg$ + Chr$(34) + " sent by " + Player\Name$

			; Return a message to the player (includes the message that was sent to the server and the playername)
			WriteString Player\stream, "Message " + Chr$(34) + msg$ + Chr$(34) + " received, " + Player\Name$
		EndIf

		; Check if the client disconnected
		Select Eof(Player\stream)
			Case 1 ; Player disconnected nicely
				; Print a message in the server-window to indicate which player disconnected
				Print "Player " + Player\Name$ + " closed"
				; Delete the TPlayer instance (the server would otherwise continue to send messages to this player)
				Delete Player
			Case -1 ; Connection lost
				; Print a message in the server-window to indicate which player disconnected
				Print "Player " + Player\Name$ + " aborted unexpectedly"
				; Delete the TPlayer instance (the server would otherwise continue to send messages to this player)
				Delete Player
		End Select
	Next

	; Wait 2ms (don't let the server-program use up all processor-power)
	Delay 2
Wend

; End the server
End





;***************************************************************************************************
; Client-code
;***************************************************************************************************

; Setup window size
Graphics 600, 200, 0, 2



; Declare port and IP-adress of the server
Global Port = 9876
Global IP$ = "127.0.0.1"
; Setup a global variable that holds the TCP-stream handle (used to talk to the server)
Global stream

; A global timer to time some events (used to send a message every second)
Global Timer = MilliSecs()

; Seed the random number generator
SeedRnd MilliSecs()

; Create an array with some playernames
Dim ANames$(10)
ANames$(1) = "Suzy"
ANames$(2) = "Tom"
ANames$(3) = "Adrian"
ANames$(4) = "Melissa"
ANames$(5) = "Ronaldo"
ANames$(6) = "Richard"
ANames$(7) = "Christine"
ANames$(8) = "Jackie"
ANames$(9) = "Arnold"
ANames$(10) = "Kevin"



; Try to connect to server
While stream = 0
	; Wait 1 second
	Delay 1000

	; Use a counter to count the number of tries
	Counter = Counter + 1

	; Try to open a TCP-stream to the server on the given port and IP-address
	stream = OpenTCPStream(IP$, Port)
	; Let the user know that the client is trying to connect to the server
	Print "Connecting to server..."
	Print "on IP$: " + IP$ + ", using port " + Str$(Port)

	; If the counter reached 5, then the client tried 5 times to connect to the server, but wasn't able to connect -> generate an error
	If Counter = 5 Then RuntimeError "Cannot find server on IP: " + IP$
Wend

; Connection established
Print ""
Print "Successfully connected to server..."
Print ""



; Use a random playername for this client
Global Name$ = ANames$(Rand(1, 10))
; Send the playername to the server
WriteString stream, Name$
; Set application title
AppTitle "Client for: " + Name$



; Main loop
While Not KeyHit(1)
	Local msg$ ; Variable used to read messages sent by the server

	; Send a random number (as a string) to the server every second
	If MilliSecs() > (Timer + 1000) Then
		Timer = Timer + 1000

		; Generate a random number for sending to the server and convert it to a string
		msg$ = Str$(Rand(0, 1000))
		; Print the number to the screen for debugging
		Print msg$
		; Send the random number as a string to the server
		WriteString stream, msg$
	EndIf

	; Check if the server has sent a response (or some other message)
	If ReadAvail(stream) Then
		; Read the server's message into a variable
		msg$ = ReadString$(stream)
		; Print the server's response to the screen
		Print msg$
	EndIf

	; Check if the connection hasn't been lost
	Select Eof(stream)
		Case 1 ; Server has been closed
			; Print it to the screen
			Print "Server has closed"
			; Wait 1 second
			Delay 1000
			; End the client program
			End
		Case -1 ; Connection lost
			; Print it to the screen
			Print "Server has aborted unexpectedly"
			; Wait 1 second
			Delay 1000
			; End the client program
			End
	End Select

	; Wait 10ms
	Delay 10
Wend

; End the client program
End
