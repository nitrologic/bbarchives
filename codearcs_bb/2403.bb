; ID: 2403
; Author: Matty
; Date: 2009-01-29 01:47:00
; Title: Simple TCP/IP Network Library
; Description: TCP/IP Network Library with simple example.

;simple example
Graphics 800,600,0,2
InitialiseNetwork(2009,3)
Global screenx#,screeny#,dx#,dy#	;for little block that we will move about the screen on the client's 

machine.

If CommandLine$()="server" Then ;this all assumes the server code is run first.
	;do nothing
Else
	While SendMessage(100,"","192.168.1.6") :Wend ;you would have to change these lines yourself normally
EndIf 
SetBuffer BackBuffer()
Repeat 
UpdateNetwork()
If CommandLine$()="server" Then 
screenx=400.0+300.0*(Sin(MilliSecs()/50))*(Cos(MilliSecs()/250))
screeny=300.0+(100.0*Sin(MilliSecs()/30))+(150.0*Cos(MilliSecs()/125))
Else

screenx=screenx+dx
screeny=screeny+dy

EndIf 

If MilliSecs()>time And CommandLine$()="server" Then 
	time=MilliSecs()+100
	For Connection.ConnectionObject=Each ConnectionObject
		SendMessage(1,PackFloat(screenx)+PackFloat(screeny),Connection\IP)
	Next
EndIf 



Cls
Rect screenx,screeny,4,4,1
Flip
Until KeyDown(1)


CloseNetwork()
End



;Basic TCP Library below

Global MyTCPServer  ;server handle for the local machine's TCP Server. Always non zero and refers to local machine

Type ErrorObject

Field Message$

End Type 

Type ConnectionObject

Field IP$

End Type 

Function InitialiseNetwork(Port=2009,TimeoutinSeconds=10)

MyTCPServer = CreateTCPServer(Port)
If MyTCPServer = 0 Then 
	Error.ErrorObject=New ErrorObject
	Error\Message = "Unable to CreateTCPServer listening on port:"+Port
	Return 1 ;error 
EndIf 
TCPTimeouts TimeOutInSeconds*1000,0
Return 0	;no error
End Function 
Function CloseNetwork()
For Connection.ConnectionObject=Each ConnectionObject
	SendMessage(102,"",Connection\IP) ;Let others know we've quit.
Next
If MyTCPServer <> 0 Then 
	CloseTCPServer MyTCPServer	
EndIf 

End Function 

Function SendMessage(MessageType,Message$,IP$,Port=2009)

stream=OpenTCPStream(IP$,Port)
If stream = 0 Then 
	Error.ErrorObject=New ErrorObject
	Error\Message= "Unable to open TCPStream to IP:"+IP+" and on port:"+port
	

	For Connection.ConnectionObject=Each ConnectionObject
		If Connection\IP=IP Then Delete Connection 
	Next
	Return 1	;Error condition
EndIf
	WriteString stream,PackByte(MessageType)+Message
	CloseTCPStream stream
	
	Return 0	;Successful
End Function 

Function UpdateNetwork()
;Message Types - 
;100 - Request received by server for client to join
;101 - Acknowledgement received by client to say that server has accepted them
;102 - Server receives message saying client has left 
;103 - Client(s) receive message from server letting them know other client's machine on network's IP Address.
If MyTCPServer<>0 Then 

	stream=AcceptTCPStream(MyTCPServer)
	If stream<>0 Then 
		streamstring$=ReadString(stream)
		
		If Len(streamstring)>0 Then 
		Select UnPackByte(Left(streamstring,1))
		
			Case 100	;Remote machine has requested to join the network
				IsNewConnection=True	;Check if stream is from a newly connected user
				For Connection.ConnectionObject=Each ConnectionObject
					If Connection\IP$ = DottedIP(TCPStreamIP(stream)) Then 

IsNewConnection=False:Exit 
				Next		
				
				;Create a new connection object if it is a new user
				If IsNewConnection=True Then 
					Connection.ConnectionObject=New ConnectionObject
					Connection\IP$=DottedIP(TCPStreamIP(stream))	
					SendMessage(101,"",Connection\IP)	
				EndIf 
				;Server needs to update every other client with the new client's details.
				For Connection.ConnectionObject=Each ConnectionObject
					If Connection\IP$<>DottedIP(TCPStreamIP(stream)) Then 
						SendMessage(103,PackInt(TCPStreamIP(stream)),Connection\IP)
					EndIf 
				Next				
			Case 101  ;Local machine has received a 'yes you can join message' from the server
				IsNewConnection=True	;Create server connection object
				For Connection.ConnectionObject=Each ConnectionObject
					If Connection\IP$ = DottedIP(TCPStreamIP(stream)) Then 

IsNewConnection=False:Exit 
				Next		
				
				;Create a new connection object.
				If IsNewConnection=True Then 
					Connection.ConnectionObject=New ConnectionObject
					Connection\IP$=DottedIP(TCPStreamIP(stream))	
				EndIf 
			Case 102 ;Remote machine has let us know it is leaving....used to prevent any 'hangs' that 

can occur if a user is not connected
				For Connection.ConnectionObject=Each ConnectionObject
					If Connection\IP$ = DottedIP(TCPStreamIP(stream)) Then Delete Connection
				Next		
			Case 103
				IsNewConnection=True	
				For Connection.ConnectionObject=Each ConnectionObject
					If Connection\IP$ = DottedIP(UnPackInt(Mid(streamstring,2,4))) Then 

IsNewConnection=False:Exit 
				Next		
				;Create a new connection object.
				If IsNewConnection=True Then 
					Connection.ConnectionObject=New ConnectionObject
					Connection\IP$=DottedIP(UnPackInt(Mid(streamstring,2,4)))
				EndIf 

			
			;User defined message types now.
			Case 1	;this is for the simple example above, you will most likely want to remove it
				scrx#=UnPackFloat(Mid(streamstring,2,4))		
				scry#=UnPackFloat(Mid(streamstring,6,4))
				dx#=(scrx-screenx)/6.0
				dy#=(scry-screeny)/6.0
			End Select 
		EndIf 
		
		CloseTCPStream stream
	Else
	
		;no packets received, so don't do anything.
	EndIf 
Else

	Return 1	;Error condition - in this case the MyTCPServer value is zero.

EndIf
Return 0	;Successful
End Function 


Function PackInt$(value)
;Pack a 4 byte integer value into a 4 byte string

A=(value Shr 24) And 255
B=(value Shr 16) And 255
C=(value Shr 8) And 255
D=value And 255
Return Chr$(A)+Chr$(B)+Chr$(C)+Chr$(D)

End Function 
Function PackShort$(value)
;Pack a 2 byte short value into a 2 byte string

A=(Value Shr 8) And 255
B=Value And 255
Return Chr$(A)+Chr$(B)

End Function 
Function PackByte$(value)
;Pack a 1 byte value into a 1 byte string

Return Chr$(Value And 255)

End Function 
Function PackFloat$(value#)
;Pack a 4 byte float into a 4 byte string

Bank=CreateBank(4)
PokeFloat Bank,0,value
A=PeekByte (Bank,0)
B=PeekByte (Bank,1)
C=PeekByte (Bank,2)
D=PeekByte (Bank,3)
FreeBank Bank
Return Chr$(A)+Chr$(B)+Chr$(C)+Chr$(D)

End Function 
Function UnPackInt(value$)
;Unpack a 4 byte string into a 4 byte integer

Return (Asc(Mid(value,1,1)) Shl 24) Or (Asc(Mid(value,2,1)) Shl 16) Or (Asc(Mid(value,3,1)) Shl 8) Or (Asc(Mid

(value,4,1)) )

End Function
Function UnPackShort(value$)
;Unpack a 2 byte string into a 2 byte short

Return (Asc(Mid(Value,1,1)) Shl 8) Or (Asc(Mid(Value,2,1)) )

End Function 
Function UnPackByte(value$)
;Unpack a 1 byte string into a 1 byte value
Return Asc(Value)
End Function 
Function UnPackFloat#(value$)
;Unpack a 4 byte string into a 4 byte float

Bank=CreateBank(4)
PokeByte Bank,0,Asc(Mid(value,1,1))
PokeByte Bank,1,Asc(Mid(value,2,1))
PokeByte Bank,2,Asc(Mid(value,3,1))
PokeByte Bank,3,Asc(Mid(value,4,1))
fvalue#=PeekFloat(bank,0)

FreeBank bank 
Return fvalue
End Function
