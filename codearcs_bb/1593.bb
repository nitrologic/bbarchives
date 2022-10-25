; ID: 1593
; Author: cermit
; Date: 2006-01-09 04:11:03
; Title: UDP Protocol Example
; Description: UDP Protocol Example

; UDP Protocol Example
;


; Set font
font% = LoadFont( "Lucida Console", 10, True, False, False )
SetFont font%


; Initialize Network
host_count% = CountHostIPs( "" )
If host_count = 0 Then
	Print_Error( "Failed to initiate network" )
	WaitKey
	End
Else
	integer_ip% = HostIP( 1 )
	dotted_ip$ = DottedIP( integer_ip )
	Print_Status( "Network initialized successfuly", "  IP " + dotted_ip )
EndIf


; Create UDP Streams
udp_a% = CreateUDPStream( 1000 )
udp_b% = CreateUDPStream( 2000 )
If udp_a + udp_b = False Then
	Print_Error( "Failed to create one stream or another" )
	WaitKey
	End
EndIf


	; Send Message
	msg$ = "Hello sheep!"
	WriteString( udp_a, msg )
	SendUDPMsg udp_a, integer_ip, 2000
	Print_Status( "Udp_a sent message", "  " + msg )


	; Receive Message
	Repeat
		If RecvUDPMsg( udp_b )
			msg = ReadString( udp_b )
			Print_Status( "Udp_b received message", "  " + msg )
			Exit
		EndIf
	Forever


; Neatly close UDP Streams
CloseUDPStream udp_a
CloseUDPStream udp_b
Print_Status( "UDP streams were closed", "" )


; End program
Print_Status( "Have a nice day!", "  Any key .." )
FreeFont font%
WaitKey
End


; Print Status function
Function Print_Status( main_str$, sub_str$ )
	Color 0, 255, 0
	Print main_str
	Color 255, 255, 0
	Print sub_str
	Print ""
End Function


; Print Error Function
Function Print_Error( main_str$ )
	Color 255, 0, 0
	Print main_str$
	Print "  Any key .."
End Function
