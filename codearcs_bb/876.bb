; ID: 876
; Author: Wayne
; Date: 2004-01-05 15:17:18
; Title: UDP Client/Server Sample
; Description: UDP communications program (Client/Server)

;
; Sample UDP communications program (Client/Server same machine)
;
; Author: W.Gray  Last updated: 1/5/4
; Comment: Free code no restrictions, use at your own risk.
;
; Instructions:
; Start program, select mode, your IP will be displayed, enter destination IP , type.

; Testing client and server on same machine, run server with 127.0.0.1, and run client with 127.0.0.1


Print "Sample UDP Communications"

; Ask for mode

Print "1-Server"
Print "2-Client"
Mode$=Input$(">")

Select Mode$
	Case "1"
	Udp_Port=4000 ; I/O Port
	Dest_Port=4001
	AppTitle "Server on UDP Port "+Str(Udp_Port)
	
	Case "2"
	Udp_Port=4001 ; I/O Port
	Dest_Port=4000
	AppTitle "Client on UDP Port "+Str(Udp_Port)
	
	Default 
	Udp_Port=4000 ; I/O Port
	Dest_Port=4001
	AppTitle "Server on UDP Port "+Str(Udp_Port)
End Select


; Display all local user IP's
;
Print "Local IPs:"
For i=1 To CountHostIPs(GetEnv("localhost"))
    Print DottedIP(HostIP(i))
Next

Print "Enter Destination IP#: xxx.xxx.xxx.xxx"
Dest_IP$=Input$(">")

; Convert to integer IP
IP= Int_IP(Dest_IP$)

Print
Print "Starting Chat <esc> to quit"

; Allocate ports, get stream id's
udp_stream=CreateUDPStream(Udp_Port) ; Read Stream


.loop
; Poll Keyboard for Keypress.
byte1=GetKey()

; transmit each key press.
If byte1>0 Then
	; If 'Enter' pressed force new line locally, and send it.
    	If Byte1=13 Then 
        Print
	; put character in stream buffer.
        WriteByte(udp_stream,byte1)
	; send all characters in the stream buffer.
        SendUDPMsg udp_stream,IP,Dest_Port
    Else
	; Show character before we send it.
        Write Chr(Byte1)
	; put character in stream buffer.
        WriteByte(udp_stream,byte1)
	; send all characters in the stream buffer.
        SendUDPMsg udp_stream,IP,Dest_Port
    EndIf
EndIf

; Load UDP message into stream
IP_rd=RecvUDPMsg(udp_stream)

; Test for messages in stream
If IP_rd <> 0 Then    

;Get number of bytes in UDP stream
    Buflen=ReadAvail(udp_stream)
    If Buflen > 0 Then
    
; Get received character and print to display
        byte1=ReadByte(udp_stream)
        
; Translate <enter> key    
        If byte1=13 Then
            Print
        Else
            Write Chr$(byte1)
        EndIf
        
    EndIf
    
EndIf

; Look for <escape>
If KeyDown(1) Then
    Goto quit
EndIf

Goto loop

; Cleanup and terminate
.quit
CloseUDPStream udp_stream



;--------------------------------------
; FUNCTION: INT_IP(IP$)
; 
; Returns integer value of a IP Address string
; IP$ Format n.n.n.n

Function Int_IP(IP$)
a1=val(Left(IP$,Instr(IP$,".")-1)):IP$=Right(IP$,Len(IP$)-Instr(IP$,"."))
a2=val(Left(IP$,Instr(IP$,".")-1)):IP$=Right(IP$,Len(IP$)-Instr(IP$,"."))
a3=val(Left(IP$,Instr(IP$,".")-1)):IP$=Right(IP$,Len(IP$)-Instr(IP$,"."))
a4=val(IP$)
Return (a1 Shl 24) + (a2 Shl 16) + (a3 Shl 8 ) +a4
End Function


;--------------------------------------
; FUNCTION: VAL(STRING$)
; 
; Returns integer value of a string

Function val(String$) 
ac=String$
Return String$
End Function
