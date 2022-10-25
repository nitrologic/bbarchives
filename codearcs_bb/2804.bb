; ID: 2804
; Author: schilcote
; Date: 2011-01-01 10:52:07
; Title: IRCLib
; Description: Library for connecting to an IRC network

;Open an IRC stream. Pass the address of the server (i.e. irc.freenode.net), the hostname and nickname of the bot, and a channel to join immediately after connecting.
Function OpenIRC(address$,Hostname$="B+IRCLib",NickName$="",channel$="")

If NickName$="" Then
	NickName$=Hostname$
EndIf

;open the stream
stream=OpenTCPStream(Address$,6667)
; if the stream is null, then there's a  problem.
If Not stream Then 
Return 0
EndIf

;tell the server we want to connect to it and then tell it the channel
WriteLine stream,"USER "+NickName$+" "+Hostname$+" "+Address$+" :"+NickName$
WriteLine stream,"NICK "+NickName$
If channel$ <> "" Then
	WriteLine Stream, "JOIN "+channel$
EndIf

GetIRC(stream)

Return stream

End Function


Function JoinIRCChannel(stream,channel$)
	WriteLine Stream, "JOIN "+channel$
	Delay(1500)
End Function

;Return a string containing all unread data since the last time GetIRC was called. Pass stream to read from.
Function GetIRC$(stream)

	Bytes=ReadAvail(Stream)
		While Bytes
			tLine$=ReadLine$(Stream)
			
			If tline$ <> "" Then Print tline$
			
			AILine$=AIline$+tLine
			Bytes=ReadAvail(Stream)
			
						;if we got pinged, pong.
  			If Left(Trim(Upper(tLine$)),4) = "PING" Then
  		 		WriteLine Stream,"PONG "+Mid(Trim(Upper(tLine$)),5)
  			EndIf
			
			;If tLine$="" Then
			;	Exit ;if there's no input, just exit the loop.
			;EndIf

			tLine$=""
		
		Wend
	Return AILine$

End Function

Function WriteIRC(out$,stream,channel$)
	WriteLine stream, "PRIVMSG "+Channel$+" :"+out$
End Function

Function WriteIRCAction(out$,stream,channel$)
	
	out$=Chr$(001)+"ACTION "+out$+Chr$(001)
	
	WriteLine stream, "PRIVMSG "+Channel$+" :"+out$
End Function

Function GetIRCMessage$(tLine$,channel$,NickName$="")

	t=Instr(tLine$,"PRIVMSG "+channel$+" :")

	If t=0 Then
		t=Instr(tLine$,"PRIVMSG "+NickName$+" :")
	Else
		t=t+Len("PRIVMSG "+channel$+" :")
	EndIf

	If t=0 Then
		Return ""
	Else
		t=t+Len("PRIVMSG "+NickName$+" :")
	EndIf
	
	ret$=Mid$(tLine$,t,Len(tLine$)-t+1)


	For t=1 To Len(tLine$)
		chara$=Mid$(tLine$,t,1)
	Next

	Return ret$ 

End Function

Function GetIRCNickname$(tLine$)
	
	If Left$(tLine$,1)=":" Then
		
		For t=2 To Len(tLine$)
			chara$=Mid$(tLine$,t,1)
			
			If chara$="!" Then
				Exit
			Else
				Nickname$=Nickname$+chara$
			EndIf
		Next
	EndIf
	
	Return(Nickname$)
End Function
			 

Function QuitIRC(stream,message$="")
	WriteLine(stream,"QUIT: ")
End Function

Function ExitChannel(stream,channel$)
	WriteLine(stream,"PART: "+channel$)
End Function
