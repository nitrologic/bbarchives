; ID: 152
; Author: Wayne
; Date: 2002-02-01 17:30:43
; Title: UDP Communications
; Description: Sample UDP Chat program

;
; Sample UDP communications program
;
; Author: W.Gray  Last updated: 1/2/4
; Comment: Free code no restrictions, use at your own risk.
;
; Instructions:
; Start program, your IP will be displayed, enter your friends IP addy, type.

Print "Sample UDP Communications"

Inp_Port=4000 ; Incoming Port
Out_Port=4001 ; Outgoing Port


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
udp_rd=CreateUDPStream(Inp_Port) ; Read Stream
udp_wr=CreateUDPStream(Out_Port) ; Write Stream

.loop
; Poll Keyboard for Keypress.
byte1=GetKey()

; transmit each key press.
If byte1>0 Then
	; If 'Enter' pressed force new line locally, and send it.
    	If Byte1=13 Then 
        Print
	; put character in stream buffer.
        WriteByte(udp_wr,byte1)
	; send all characters in the stream buffer.
        SendUDPMsg udp_wr,IP,Inp_Port
    Else
	; Show character before we send it.
        Write Chr(Byte1)
	; put character in stream buffer.
        WriteByte(udp_wr,byte1)
	; send all characters in the stream buffer.
        SendUDPMsg udp_wr,IP,Inp_Port
    EndIf
EndIf

; Load UDP message into stream
IP_rd=RecvUDPMsg(udp_rd)

; Test for messages in stream
If IP_rd <> 0 Then    

;Get number of bytes in UDP stream
    Buflen=ReadAvail(udp_rd)
    If Buflen > 0 Then
    
; Get received character and print to display
        byte1=ReadByte(udp_rd)
        
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
CloseUDPStream udp_wr
CloseUDPStream udp_rd


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
