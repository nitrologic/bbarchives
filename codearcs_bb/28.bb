; ID: 28
; Author: semar
; Date: 2001-08-30 02:20:37
; Title: TCP client server chat program
; Description: A full working internet chat program

;=========== CLIENT CODE =======================
;TCP Client program
;by Sergio Marcello
;semar63@hotmail.com
;http://www.sergiomarcello.com

;the port which to connect to - the server and the client should communicate on the same port value
port = 40000 ;was 1024, but sometime is used by nowadays pc.

;sets the graphics to a windowed scaled mode
Graphics 640,480,16,3
AppTitle "CHAT CLIENT"

;the channel stream that will be assigned from the server once connected.
Global client_stream

;const for keyboard space and esc keys
Const k_space = 57
Const k_esc = 1

;ask for a nickname
client_id$ = Input$("Enter your nickname: ")

;if no nickame then end
If client_id$ = "" Then End

;attempts to open a socket (stream) on the remote server, with the default port = port
;========= ATTENTION ========================
;here the IP address should be the one of the server !!!
;right now this value is just to allow testing on a local machine. Prompt the user to
;type in the right ip address, if you want a remote connection !!!
strmClient=OpenTCPStream("127.0.0.1",port)

;checks if the stream connection was successfully estabilished
If strmClient<>0 Then 
Print "Client successfully connected"
Else
Print "Server failed to connect - quitting."
WaitKey 
End
End If


;first, write the client nickname to the server
msg$ = "UID" + client_id$
WriteLine strmClient,msg$

;until ESC is pressed
While Not KeyDown(k_esc)

;if space bar pressed, then input a message
If KeyDown(k_space)
	
	;waits until space bar is released
	While KeyDown(k_space)
	Wend

	;ask for a message to send
	message$ = Input$("Your message: ")
			
	;writes the message on the stream channel - that is, sends a message to the server
	msg$ =	"MSG" + message$	
	WriteLine strmClient, msg$
EndIf

;listen for incoming messages

;if a message is queued then retrieves it
If ReadAvail (strmClient)  Then

	;gets the message
	message$ = ReadLine$ (strmClient)
		
	;checks if is a message from the server
	If Mid$(message$,1,3) = "SRV" Then
		
		;select the second part of the header
		what$ = Mid$(message$,4,3)
		Select what$
		
			Case "_ID"
				;the server has sent this client unique ID stream
				client_stream = Mid$(message$,7)
				;Print "client_stream = " + client_stream
			
			Case "BYE"
				;server disconnected !
				Color 255,0,0
				Print " - The server has disconnected - press esc key to quit - "
				Color 255,255,255
				
			Default
				;unknown message
				Print " - Unknown message from the server - "
		
		End Select
		
	Else
		;it's a simple broadcast message
		;prints in red color a disconnection message
		If Instr(Upper(message$),Upper("disconnected")) Then
			Color 255,0,0
		EndIf
		
		;shows the message	
		Print "<" + message$ + ">"
		Color 255,255,255
				
	EndIf
	
EndIf

;small delay
Delay 30

Wend

;logging out from server
msg$ = "ESC" + client_stream
WriteLine strmClient,msg$
Delay 30

End

;========== SERVER CODE ==================
;TCP SERVER program
;by Sergio Marcello
;semar63@hotmail.com
;www.sergiomarcello.com

Graphics 640,480,16,3 ;windowed scaled mode

;sets the title of the application
AppTitle "CHAT SERVER"

Global received_stream
Const k_space = 57
Const k_esc = 1

;creates a type of client with two items: client name, and client ID stream
Type client
	Field uid$
	Field stream
End Type

;declare a global type variable for the client
Global c.client

;sets the port where to listen from, and where the client can connect to.
port = 40000 ;was 1024, but sometime is used by nowadays pc.


;Create a server and listen for client connections
svrGame=CreateTCPServer(port)

;checks if the server was successfully created; if not, ends the program
If svrGame<>0 Then 
Print "Server started successfully - listening on port " + port
Else
Print "Server failed to start on port " + port
End
End If

;=====================================================================

;Main loop until ESC is pressed
While Not KeyDown(k_esc)
message$ = ""

;checks if there is a NEW client connection
strStream=AcceptTCPStream(svrGame)

If strStream Then ;a new client has joined the chat; his ID stream is strStream

;memorize the current connection stream
received_stream = strStream

;reads info from the client just connected (see tcp_client.bb)
If ReadAvail(strStream) Then
message$ =  ReadLine$(strStream)
EndIf

Else 

;there are NOT new connections; so checks if there are incoming data from clients
For c.client = Each client
If ReadAvail(c\stream) Then
	;Print "incoming message from client: " + c\UID
	received_stream = c\stream
	the_user$ = c\uid$
	message$ =  ReadLine$(c\stream)
	Exit
EndIf
Next


EndIf


If message$ <> "" Then ;a message has arrived, or a new client has joined the chat

	;processes the data from the client:

	;checks the header of the message
	header$ = Mid$(message$,1,3)
	
	;tests the header
	Select 	header$
	
	Case "UID" ;it's a NEW client connection
		
		;extracts the user ID
		the_user$ = Mid$(message$,4)
		
		;creates a new client type structure and memorize userid and stream number
		c.client = New client
		c\uid = the_user$
		c\stream = strstream
		
		;notifyes to the client his stream number
		WriteLine c\stream, "SRV_ID" + c\stream
				
		;broadcasts the new client connection
		msg_new$ = "<" + the_user$ + "> connected !"
		notify_message(msg_new$)
		
	Case "MSG" ;it's a message from a client
		

		;broadcasts the user message
		msg_new$ = "<" + the_user$ + ">  " + Mid$(message,4)
		notify_message(msg_new$)
		
	Case "ESC";a client has disconnected from the chat
	
		;extracts the client ID stream that was sent from the client in the message
		client_stream = Mid(message$,4)
	
		;locate the client name from the list of the client
		For c.client = Each client
			
			If c\stream = client_stream Then
				
				;retrieve the client id
				the_user$ = c\uid	
				
				;delete the client
				Delete c
				
				;exit for
				Exit
			EndIf
		Next

		;broadcasts the message
		msg_new$ = "<" + the_user$ + ">  Disconnected"
		notify_message(msg_new$)
		
	End Select

	;small pause
	Delay 20
	
EndIf

;if space bar pressed, then input a message
If KeyDown(k_space)
	
	;waits until space bar is released
	While KeyDown(k_space)
	Wend

	;ask for a message to send
	message$ = Input$("Your message: ")
			
	;prepare the message to be sent
	msg_new$ =	"<server> " + message$
	
	;broadcasts the message
	notify_message(msg_new$)
	
EndIf



Wend

;quitting
;sends a logoff message to all the client
notify_message("SRVBYE")
Delay 20

End

;===============================
Function notify_message(msg_new$)
;===============================

;notifyes the message on the server itself
If Instr(Upper(msg_new$),Upper("disconnected")) Then
	Color 255,0,0
EndIf

;shows only client messages
If received_stream <> 0 Then
	Print msg_new$
EndIf

Color 255,255,255

;notifyes the message to all the logged clients, but the sender
c.client = First client
For c.client = Each client
	If c\stream <> received_stream Then
		WriteLine c\stream,msg_new$
	EndIf
Next

received_stream = 0

End Function
