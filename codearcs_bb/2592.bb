; ID: 2592
; Author: semar
; Date: 2009-10-04 14:24:34
; Title: UDP Scanner
; Description: Scan the entire LAN for active UDP server

;UDP Scanner - by Sergio Marcello - www.sergiomarcello.com - semar63@hotmail.com
;read config p_in e p_out
Global IN_PORT = 50000
Global OUT_PORT = 50001
Global BRO_PORT = 50000
Global local_ip$
Global local_ip_int
Global Broadcast_IP%
Global STREAM_R ;receive stream
Global STREAM_W ;write stream
Global Player_Num = 0
Global IAmServer = False
Global DEBUG
Global pcName$

Global STATUS = 0
Const STA_WAIT = 3
Const STA_SYNC = 4


Type t_client
	Field IP
	Field Port	
	Field player_ID
	Field joined
	Field pcName$
End Type
Global client.t_client

;==================== 1 
If CommandLine$() = "test" Then
	DEBUG = True
EndIf

Print "-------------------------------"
Print "--- UDP Scanner V. 2.0 Beta ---"
Print "-------------------------------"
Print

Get_Local_IP()
Print

If Not Open_Streams() Then
	WaitKey
	End
EndIf


;==================== 2

Broadcast_IP = Get_Broadcast_IP()

;loop
Local finito
Local starttime 
Local msg$

If IN_PORT = 50000 Then
	If DEBUG Then
		IAmServer = True
	EndIf
EndIf

finito = False

While Not finito
	
	If KeyHit(1) Then
		End
	EndIf

	;broadcast every sec
	If ((MilliSecs() - starttime) >= 3000) Then
		starttime = MilliSecs()
		Broadcast("PING")
	EndIf
		
	;get msg
	Local IP_check = RecvUDPMsg (STREAM_R)
	If IP_check <> 0 Then

		Local Port_Check = UDPMsgPort(STREAM_R)

		;if extern msg then
		If Extern_Message(IP_Check,Port_Check) Then
		
			;read msg
			msg$ = ReadString(stream_R)
		
			DebugLog.Print "ext: " + msg$ + " from IP " + IP_Check + " on port " + port_check

			Select Mid$(msg$,1,4)
			
				Case  "PING"
				
					If Not IAmServer Then
						;found a server
						Print "found server on port = " + Port_Check
						
						;add server to list
						;client.t_client = New t_client
						;client\ip = IP_Check
						;client\port = Port_Check
						;client\player_ID = 1 ;server
						;client\joined = True
						
						;send pong
						WriteString(stream_R,"PONG#" + pcName$)
						SendUDPMsg stream_R,IP_Check,Port_Check
						;
						
						status = STA_WAIT
						;finito = True
						Exit ;exit while loop
					Else
						;send a PING back
						WriteString(stream_R,"PING")
						SendUDPMsg stream_R,IP_Check,Port_Check
					EndIf
				
				
				Case "PONG"
					
					Local name$ = Mid$(msg$,6)
					Print "new player: " + name$ + " on port = " + Port_Check
					player_num = player_num + 1
	
					;if I'm not yet in the list
					If player_num = 1 Then
						;add me as a server to list
						client.t_client = New t_client
						client\ip = local_ip_int
						client\port = IN_PORT
						client\player_ID = 1 ;server
						client\joined = True
						client\pcName$ = pcName$
						player_num = player_num + 1
						IAmServer = True
					EndIf
					
					;add client to list
					client.t_client = New t_client
					client\ip = IP_Check
					client\port = Port_Check
					client\player_ID = player_num ;client
					client\joined = True
					client\pcName$ = name$

	
					;pause ?
					
					;send all clients info
					send_client_info()
				
				
			;end case
			End Select
		
		
		Else ;internal message
		
			DebugLog.Print "intern: <" + msg$ + "> from IP " + IP_Check + " on port " + port_check
			
		;endif
		EndIf ;if extern message
		
		;if enter then
		If KeyHit(28) Then
			status = STA_SYNC
			Exit
		EndIf
		;status = sync
		;endif
		

		
	EndIf ;if message
	
			
	
Wend
;

;==================== 3 WAIT
If status = STA_WAIT Then
	Wait_Sync()
EndIf

;==================== 4 SYNC
If status = STA_SYNC Then
	Print "sync..."
	;Send_Sync()
	Delay 100
	If DEBUG Then
		For c.t_client = Each t_client
			WriteString(stream_R,"SYNC")
			SendUDPMsg stream_R, c\IP, c\Port
		Next
			
	Else
		Broadcast("SYNC")
	EndIf
EndIf



WaitKey
End

;============================================
Function Wait_Sync()
;============================================
Local msg$
Local cmd$
Local starttime
Print "waiting sync..."

While 1
;read msg every 50 msec
If ((MilliSecs() - starttime) >= 50) Then

	;get msg
	Local IP_check = RecvUDPMsg (STREAM_R)
	If IP_check <> 0 Then
	
		Local Port_Check = UDPMsgPort(STREAM_R)
	
		;if extern msg then
		If Extern_Message(IP_Check,Port_Check) Then
			
			;read msg
			msg$ = ReadString(stream_R)
			
			cmd$ = Mid$(msg$,1,4)
			
			Select cmd$
				Case "LIST"
					Get_Client(msg$)
					Print msg$
					
				Case "SYNC"
					End	
			End Select

		Else
		
			;intern message
			DebugLog.Print "intern:<" + msg$ + "> from IP " + IP_Check + " on port " + port_check
			
		EndIf	
		
	EndIf		
	
	starttime = MilliSecs()

EndIf

If KeyHit(1) Then
	End
EndIf

Wend

End Function

;============================================
Function Extern_Message(IP_Check,Port_Check)
;============================================
;consider only the broadcast that comes from other client

If DEBUG Then
	extern_msg = ((Str$(IP_Check) + Str$(Port_Check)) <> (Str$(Broadcast_IP) + Str$(IN_PORT)))
	;extern_msg = extern_msg And (((Str$(IP_Check) + Str$(Port_Check)) <> (Str$(local_ip_int) + Str$(IN_PORT))))
Else
	extern_msg = (IP_check <> local_ip_int)		
EndIf
	
Return extern_msg

End Function

;============================================
Function send_client_info()
;============================================
;send client info to all the clients
Local msg$
;For c.t_client = Each t_client

;	If c\ip <> local_ip_int Then
;		msg = msg + "#" + c\IP + "#" + c\port
;	EndIf

;Next

For a.t_client = Each t_client

	If a\ip <> local_ip_int Then

		For c.t_client = Each t_client
		
		
				msg$ = "LIST#" + c\pcName$ + "#" + c\player_id + "#" + c\IP + "#" + c\port
				WriteString(stream_R,msg$)
				SendUDPMsg stream_R, a\IP, a\Port
				Delay 50
			
				DebugLog.Print "lista---------"
				DebugLog.Print msg$
				DebugLog.Print "ip= " + a\ip
				DebugLog.Print a\port
				DebugLog.Print "------------"

		Next
		
	EndIf

Next

;broadcast ?
;SendUDPMsg stream_W,IP_BROADCAST,IN_PORT
End Function

;===========================
Function Broadcast(msg$)
;===========================

WriteString(STREAM_R,msg$)
SendUDPMsg STREAM_R, Broadcast_IP, BRO_PORT
DebugLog.Print "Broadcast: " + msg + " to IP: " +  Broadcast_IP + " on port: " + BRO_PORT

End Function


;===========================
Function Open_Streams()
;===========================

STREAM_R = CreateUDPStream(IN_PORT) ; Read Stream
If Not DEBUG Then
	If Not STREAM_R Then
		Print "Unable to open input stream on port " + IN_PORT
		Print
		Print "please edit the config file on all clients and"
		Print "change the in and out port to other values."
		Print "if running behind a firewall, you may need"
		Print "to disable it."
		Return False
	EndIf
EndIf

While Not STREAM_R
	IN_PORT = IN_PORT + 1
	If IN_PORT > 65536 Then
		Print "Unable to open input stream on port " + IN_PORT
		Return False			
	EndIf
	STREAM_R = CreateUDPStream(IN_PORT)
Wend

STREAM_W = CreateUDPStream(OUT_PORT) ; Write Stream
While Not STREAM_W
	OUT_PORT = OUT_PORT + 1
	If OUT_PORT > 65536 Then
		Print "Unable to open output stream on port " + OUT_PORT
		Return False			
	EndIf
	STREAM_W = CreateUDPStream(OUT_PORT)
Wend


Print "Stream on port" + IN_PORT  +  " successfully opened"
Print "Stream on port" + OUT_PORT + " successfully opened"

Return True

End Function
;===========================


;===========================
Function Get_Local_Ip()
;===========================
;Print "Computer name: " + GetEnv$("COMPUTERNAME")
;Print "User Domain:   " + GetEnv$("USERDOMAIN")
;Print "User name:     " + GetEnv$("USERNAME")
pcName$ = GetEnv$("COMPUTERNAME") + "/" + GetEnv$("USERNAME")
pcName$ = Replace (pcName$,"#","_")
pcName$ = Mid$(pcName$,1,10)
Print "pc name = " + pcName$



;enumerate all the IP on this local pc
ip_count=CountHostIPs(GetEnv("localhost"))

Print "IP address count: " + ip_count
Print

For i = 1 To ip_count

	local_ip_int = HostIP(i)
    local_ip$ = DottedIP(HostIP(i))
	Print i + "   " + local_ip$ + " <" + local_ip_int + ">"
	
	;if debugging, will take the local address
	If DEBUG Then
		If (Instr(local_ip$,"127.0")) Then
			Print
			Print "* * * * * * Debug mode * * * * * * "
			Exit
		EndIf
	Else
		If (Instr(local_ip$,"192.168")) Then
			Exit
		EndIf
	EndIf
	;If (Not Instr(local_ip,"192.168")) Then
	;If (Not Instr(local_ip,"127.0")) Then
	;	Exit
	;EndIf
Next

End Function

;===========================
Function Get_Broadcast_IP()
;===========================
;build the broadcast IP; it is made by the ip address, only the third triplet is 255
Local point = 1

For n = 1 To 3
	point = Instr(local_ip$,".",point)
	point = point + 1
Next
point = point - 1 ;now point is the position of the third "." in the IP address
Local broadcast$ = Mid$(local_ip$,1,point) + "255"
Local IP_broadcast% = Int_IP(broadcast$)

Print "Broadcast IP = " + Broadcast$ + " <" + IP_Broadcast% + ">"
Return IP_Broadcast%

End Function

;============================================
Function Int_IP(IP$)
;============================================
;convert a string formatted IP address to an integer value
a1=val(Left(IP$,Instr(IP$,".")-1)):IP$=Right(IP$,Len(IP$)-Instr(IP$,"."))
a2=val(Left(IP$,Instr(IP$,".")-1)):IP$=Right(IP$,Len(IP$)-Instr(IP$,"."))
a3=val(Left(IP$,Instr(IP$,".")-1)):IP$=Right(IP$,Len(IP$)-Instr(IP$,"."))
a4=val(IP$)
Return (a1 Shl 24) + (a2 Shl 16) + (a3 Shl 8 ) +a4
End Function

;============================================
Function val(String$)
;============================================
; Returns integer value of a string
ac=String$
Return String$
End Function

;============================================
Function Get_Client(msg$)
;============================================
;retrieve the new joined client
	
;the message is usually like: << LIST#ID#IP#PORT >>  ID, IP and PORT are all numeric values.
Local p
Local id
Local ip
Local port
Local name$

msg$ = Mid$(msg$,6)

;get pcname
p = Instr(msg$,"#")
name$ = Mid$(msg$,1,p-1)

;get player id
msg$ = Mid$(msg$,p+1)
p = Instr(msg$,"#")
id = Int(Mid$(msg$,1,p-1))

;get ip
msg$ = Mid$(msg,p+1)
p = Instr(msg$,"#")
ip = Int(Mid$(msg$,1,p-1))

;get port
msg$ = Mid$(msg$,p+1)
port = Int(msg$)

If Not (ListContains(id)) Then
	;add the new client
	client.t_client = New t_client
	client\ip = ip
	client\port = port
	client\player_ID = id
	client\joined = True ;joined
	client\pcName$ = name$
	Print "New player: " + name$ + ", " + id + ", " + ip + ", " + port
	;Text 0,yy, "New Player: " + NewIP + ", " + NewPort
	;yy = yy + dy
EndIf
	
End Function

;==================================
Function ListContains(id)
;==================================
For c.t_client = Each t_client
	If c\player_ID = id Then
		Return True
	EndIf
Next
End Function
