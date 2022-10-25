; ID: 1467
; Author: Baystep Productions
; Date: 2005-09-24 20:22:07
; Title: TCP ENGINE
; Description: A easier cleaner TCP engine

;CUT AND PASTE THIS INTO A NEW FILE
[code]
;Magic TCP Server
;////////////////

;FUNCTIONS (ADD THIS TO ANY GAME)
Type broadcast
	Field msg$
End Type

Type user
	Field name$,stream
	Field xpos,ypos,zpos
	Field xrot,yrot,zrot
	Field update,ready
End Type

Type logit
	Field msg$,time
End Type

Global entry.logit
Global plr.user
Global bc.broadcast
Global tcp,tcp_msg$,tcp_stream
Global tcp_upd%,tcp_curplr%
Global tcp_sent%,tcp_recv%
Global tcp_username$,tcp_new,tcp_newname$
Global tcp_sending


;//////////EDIT THESE VARIABLES\\\\\\\\\\\\\\
Global tcp_updtime%=50			;Set this to the update frames. Standard is 50
Global tcp_maxplr%=6			;Set to maximum players alloted
Global tcp_ip$="localhost"		;MUST SET TO SERVERS IP!
Global my_xpos					;Adjust these in your game to reflect positions and rotations
Global my_ypos					;	"		"
Global my_zpos					;	"		"
Global my_xrot					;	"		"
Global my_yrot					;	"		"
Global my_zrot					;	"		"
;\\\\\\\\\\\\\\\\\\\\\///////////////////////

Function Init_SERVER()
	tcp=CreateTCPServer(8067) ;That port is ASCII for PC!
	If tcp=0 Then RuntimeError "Could not create TCP server on port 8067"
	AddLog "Server initiated"
End Function

Function Update_SERVER()
	tcp_upd%=tcp_upd%+1
	If tcp_upd%>=tcp_updtime%
		Cls
		Text 0,0,"Users loged-in: "+tcp_curplr%
		Text 0,10,"Maximum users permited: "+tcp_maxplr%
		Text 0,30,"Packets Sent: "+tcp_sent%
		Text 0,40,"Packets Recieved: "+tcp_recv%
		DrawLogs()
		Flip
	End If
	
	;Find new users
	tcp_stream=AcceptTCPStream(tcp)
	If tcp_curplr%<tcp_maxplr And tcp_stream<>0
		tcp_msg$=ReadLine(tcp_stream)
		If tcp_msg$="Login request"
			tcp_msg$=ReadLine(tcp_stream)
			failsafe=False
			For plr.user=Each user
				If plr\name$=tcp_msg$
					failsafe=True
					Exit
				EndIf
			Next
			If failsafe=True
				WriteLine(tcp_stream,"Username taken.")
				AddLog "User login failed. Name was taken."
			Else
				plr.user=New user
				plr\name$=tcp_msg$
				plr\stream=tcp_stream
				tcp_curplr%=tcp_curplr%+1
				AddLog "User '"+tcp_msg$+"' was accepted into server."
				WriteLine(tcp_stream,"User accepted")
			EndIf
		EndIf
	EndIf
	
	;Update users
	For plr.user=Each user
		plr\update=plr\update-1
		If Not Eof(plr\stream)
			If ReadAvail(plr\stream)
				tcp_msg$=ReadLine(plr\stream)
				If tcp_msg$="Coordinates"						;This is where the commands come in.
					AddLog "Accepting "+plr\name$+"'s coordinates"
					plr\xpos=ReadLine(plr\stream)
					plr\ypos=ReadLine(plr\stream)
					plr\zpos=ReadLine(plr\stream)
					plr\xrot=ReadLine(plr\stream)
					plr\yrot=ReadLine(plr\stream)
					plr\zrot=ReadLine(plr\stream)
					tcp_recv=tcp_recv+1
					plr\ready=True
				Else
					If Instr(tcp_msg$,"[broadcast]")<>0
						tcp_msg$=Trim(Replace(tcp_msg$,"[broadcast]"," "))
						bc.broadcast=New broadcast
						bc\msg$="<"+plr\name+">"+tcp_msg$
					EndIf
				EndIf
			EndIf
		Else ;User disconnected
			AddLog plr\name$+" has been lost."
			Delete plr
			tcp_curplr%=tcp_curplr%-1
			Exit
		EndIf
		
		If plr\update<=0 And plr\ready=True
			plr\update=25
			plr\ready=False
			
			For other.user=Each user
				WriteLine(plr\stream,"Other coordinates")
				WriteLine(plr\stream,other\name$)
				WriteLine(plr\stream,other\xpos)
				WriteLine(plr\stream,other\ypos)
				WriteLine(plr\stream,other\zpos)
				WriteLine(plr\stream,other\xrot)
				WriteLine(plr\stream,other\yrot)
				WriteLine(plr\stream,other\zrot)
				tcp_sent=tcp_sent+1
				For bc.broadcast= Each broadcast
					WriteLine(plr\stream,bc\msg$)
				Next
			Next
			For bc.broadcast=Each broadcast
				Delete bc
			Next
		EndIf
	Next
End Function

Function Close_SERVER()
	For plr.user=Each user
		WriteLine(plr\stream,"SERVER CLOSING")
		Delete plr
	Next
	CloseTCPStream(tcp)
End Function



Function Init_CLIENT()
	While Len(tcp_username$)<4
		tcp_username$=Input("Desired username (over 4 charecters): ")
	Wend
	tcp=OpenTCPStream(tcp_ip$,8067)	;Remember that magic port number?
	If tcp=0
		For i=1 To 3	;Try 3 more times.
			tcp=OpenTCPStream(tcp_ip$,8067)
			If tcp<>0
				Exit
			EndIf
		Next
		If tcp=0
			RuntimeError "Could not connect to server '"+tcp_ip$+"' on port 8067"
		EndIf
	EndIf
	WriteLine(tcp,"Login request")
	WriteLine(tcp,tcp_username$)
	
	tcp_msg$=ReadLine(tcp)
	If tcp_msg$="Username taken"
		RuntimeError "The username was taken. Try again."
	Else If tcp_msg$="User accepted"
		WriteLine(tcp,"Coordinates")
		WriteLine(tcp,"0")								;YOU CAN CHANGE THESE TO STARTING COORDINATES
		WriteLine(tcp,"0")
		WriteLine(tcp,"0")
		WriteLine(tcp,"0")
		WriteLine(tcp,"0")
		WriteLine(tcp,"0")
	EndIf
End Function

Function Update_CLIENT()
	If ReadAvail(tcp)
		tcp_msg$=ReadLine(tcp)
		If tcp_msg$="Other coordinates"
			tcp_new=True
			tcp_newname$=ReadLine(tcp)
			If tcp_newname<>tcp_username$
				For plr.user=Each user
					If plr\name$=tcp_newname$
						tcp_new=False
						Exit
					EndIf
				Next
				If tcp_new=True
					plr.user=New user
					plr\name$=tcp_newname$
				EndIf
				plr\xpos=ReadLine(tcp)
				plr\ypos=ReadLine(tcp)
				plr\zpos=ReadLine(tcp)
				plr\xrot=ReadLine(tcp)
				plr\yrot=ReadLine(tcp)
				plr\zrot=ReadLine(tcp)
				tcp_curplr%=tcp_curplr%+1
			EndIf
			tcp_sending=True
		Else If tcp_msg$<>"0"	;Broadcast
			Print tcp_msg$	;Replace with log entry system
		EndIf
		If tcp_sending=True
			WriteLine(tcp,"Coordinates")
			WriteLine(tcp,my_xpos)
			WriteLine(tcp,my_ypos)
			WriteLine(tcp,my_zpos)
			WriteLine(tcp,my_xrot)
			WriteLine(tcp,my_yrot)
			WriteLine(tcp,my_zrot)
			tcp_sending=False
		EndIf
	EndIf
End Function

Function Close_CLIENT()
	CloseTCPStream(tcp)
End Function

Function BroadcastMsg(mesg$)
	WriteLine(tcp,"[broadcast]"+mesg$)
End Function

Function DrawLogs()
	cnt=0
	For entry.logit=Each logit
		Text 0,50+(10*cnt),entry\msg$
		If MilliSecs()>=entry\time
			Delete entry
		EndIf
		cnt=cnt+1
	Next
End Function

Function AddLog(mesg$)
	entry.logit=New logit
	entry\msg$=mesg$
	entry\time=MilliSecs()+2000	;Adjust this number to fit the length the log shows.
End Function
[/code]

;The small examples
;SERVER is just this easy.
[code]
Graphics 500,300,16,2
AppTitle "Amgroben's magic TCP server example!"

Include "TCP_CONTROL.bb"

Init_SERVER()

While Not KeyHit(1)
	Update_SERVER()
Wend
Close_SERVER()
End
[/code]

;CLIENT is rough beware.
Graphics 500,300,16,2
AppTitle "Amgroben's magic TCP client example!"

Include "TCP_CONTROL.bb"

Init_CLIENT()

SetBuffer BackBuffer()
While Not KeyHit(1)
	If KeyHit(28)
		FlushKeys()
		key=0
		outmsg$=""
		While Not KeyHit(28)
			Cls
			key=GetKey()
			If key<>0 And key<>8
				outmsg$=outmsg$+Chr(key)
			Else If key=8
				outmsg$=Left(outmsg$,Len(outmsg$)-2)
			EndIf
			Text 100,290,outmsg$
			Flip
			Update_CLIENT
		Wend
		BroadcastMsg(outmsg$)
		FlushKeys()
	EndIf
	Update_CLIENT
Wend
Close_CLIENT()
End
[/code]
