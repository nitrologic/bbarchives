; ID: 225
; Author: Cpt. Sovok
; Date: 2002-02-07 16:36:57
; Title: Mail Functions
; Description: >10 Functions for sending (SMTP) and receiving (POP) mails.

; EMail Functions v0.1
; Robert Gerlach 2.2002
; www.robsite.de


; 13 usefull Functions for receiving (POP) and sending (SMTP) mails with outstandingly ease. 

; Works only good with Plain-text-Mails

; Poorly translated from german... 



;------------
; This and the functions must be in your Program

Global com 
; In every Array-Element a mail-line is saved. easier to read afterwards... 
Global mailzeilen_anzahl
Dim mailzeilen$(mailzeilen_anzahl)
;---------------










;------------------------------------------------------------
; Testprogramm for the POP-Functions


server$ = "pop3.your_server.de"
user$ = "wombat"
pass$ = "xxxxxx"


; Connect with POP-Server
login$ = popAccountLogin$(server$, user$, pass$)




If Mid(login$, 1, 3) = "+OK" Then


; Get Number and Bytes of new mails.
statstring$ = popAccountStat$()


anzahl = zahlausstring(statstring$, 1)

; If there are any mails
If anzahl > 0 Then

popMailReceive$(1) ; receive Mail #1



Print anzahl + " new Mail(s)."
Print

sender$ =  popMailFrom$()
Print "Sendernam: " + stringausstring(sender$,1,0)
Print "Senderadress: " + stringausstring(sender$,2,0)
Print "SenderIP: " + popMailIP$()
Print "Date: " + popMailDateDate$()
Print "Day: " + popMailDateDay$()
Print "Time: " + popMailDateTime$()
Print
Print 
Print "Subject: " + popMailSubject$()
Print 

mailtext$ = popMailText$()

i = 1
Repeat 
	Print stringausstring$(mailtext$, i, 0)
	i = i + 1 
Until Len(stringausstring$(mailtext$, i, 0)) = 1 And stringausstring$(mailtext$, i, 0) = "0"


; To do more things you must seemingly login again. Mysteriously...
;Print popAccountLogin$("pop3.xxx.de", "user", "pass")
;Print popMailDelete$(1)
;Print popAccountLogout$()

Else
Print "no new messages"
EndIf
Else
Print "connection failed"
EndIf
WaitKey()
End
;...................................................................


; SMTP-Mail-Sending is much easier:

; mailtext$ = "Bla... Blablah" + Chr$(0) + "Next line" + Chr$(0) + "end"

; Print smtpSendMail$(server$, to_name$, to_adress$, from_name$, from_adress$, subject$, mailtext$)

; Sometimes you must POP-Login first to send mail via SMTP (SMTP after POP)...

;----------------------------------------------------------------------------













; Extract Sendingtime from Mailheader
Function popMailDateTime$()
 For i = 1 To Len(mailzeilen$(4))
		If Mid(mailzeilen$(4), i, 1) = ";" Then
      For ii = i+7 To Len(mailzeilen$(4))
       If Mid(mailzeilen$(4), ii, 1) = " " Then leer = leer + 1
       If leer = 4 Then Return Mid(mailzeilen$(4), i+19, ii-(i+19)) 
		  Next
		EndIf
	Next
End Function

;**********************************************************************
; Extracts Sending-Time.
Function popMailDateDate$()
  For i = 1 To Len(mailzeilen$(4))
		If Mid(mailzeilen$(4), i, 1) = ";" Then
      For ii = i+7 To Len(mailzeilen$(4))
       If Mid(mailzeilen$(4), ii, 1) = " " Then leer = leer + 1
       If leer = 3 Then Return Mid(mailzeilen$(4), i+7, ii - (i+7)) 
		  Next
		EndIf
	Next
End Function

;**********************************************************************
; Get the Sending-Day in short version (Mon, Thu, ...)
Function popMailDateDay$()
	For i = 1 To Len(mailzeilen$(4))
		If Mid(mailzeilen$(4), i, 1) = ";" Then
      For ii = i+2 To i+5
				If Mid(mailzeilen$(4), ii, 1) = "," Then Return Mid(mailzeilen$(4), i+2, ii-(i+2))
		  Next
		EndIf
	Next	
End Function

;****************************************************************
; Get the IP-Adress from Sender.
Function popMailIP$()
	For i = 1 To Len(mailzeilen$(1))
		If Mid(mailzeilen$(1), i, 1) = "[" Then
			For ii = i+1 To Len(mailzeilen$(1))
				If Mid(mailzeilen$(1), ii, 1) = "]" Then
					Return Mid(mailzeilen$(1), i+1, ii - (i+1))
				EndIf
			Next
		EndIf
	Next
End Function

;*****************************************************************
; Gets Subject of mail.
Function popMailSubject$()
	For i = 1 To mailzeilen_anzahl
		If Mid(mailzeilen$(i), 1, 8) = "Subject:" Then Return Mid(mailzeilen$(i), 10, Len(mailzeilen$(i)) - 9)
	Next
End Function

;*******************************************************************
; Extracts the Mail Text
Function popMailText$()
	For i = 1 To mailzeilen_anzahl
		If mailzeilen$(i) = "" Then	
			For ii = i+1 To mailzeilen_anzahl-2
				If ii = mailzeilen_anzahl-2 Then
					message$ = message$ + mailzeilen$(ii)
				Else
					message$ = message$ + mailzeilen$(ii) + Chr$(0)
				EndIf
			Next
			Return message$
		EndIf
  Next
End Function

;**********************************************************************
; Extracts Sendername and Adress from Mail-Header
; In Form : "Name" and "nickname@xxx.com"
Function popMailFrom$()
For i = 1 To mailzeilen_anzahl
	If Mid(mailzeilen$(i), 1, 5) = "From:" Then
    ; Extracting the string with both Names
    For ii = 7 To Len(mailzeilen$(i))
      If Mid(mailzeilen$(i), ii, 1) = ">" Then
        from$ = Mid(mailzeilen$(i), 7, Len(mailzeilen$(i)) - 6)
        Exit
      EndIf
    Next
    For ii = 1 To Len(from$)
      If Mid(from$, ii, 1) = " " Then
        name$ = Mid(from$, 1, ii-1)
        email$ = Mid(from$, ii+2, Len(from$)- (ii+2))
        Return name$ + Chr$(0) + email$
      EndIf
    Next
  EndIf
Next
End Function

; *************************************************************************
; Recieves the mail an stores it in the mailzeilen$()-Array
Function popMailReceive$(nummer)
mailzeilen_anzahl = 0
WriteLine com, "RETR " + nummer
i$ = ReadLine(com)
If Mid(i$, 1, 1) = "-" Then
  Return "-ERR no such mail"
Else
  Repeat
    mailzeilen_anzahl = mailzeilen_anzahl + 1
    WriteLine com, Chr$(28) ; Recieve new line with 'Return'
    i$ = ReadLine(com)
    If Mid(i$, 1, 1) = "." And Len(i$) = 1 Then
      message$ = message$ + i$
    Else
      message$ = message$ + i$ + Chr$(0); Save the whole message in a string which is divided by chr$(0).
    EndIf
  Until Mid(i$, 1, 1) = "." And Len(i$) = 1
  ; redDim the mailzeilen$()-Array
  Dim mailzeilen$(mailzeilen_anzahl)  
  ; parse through the message and save every new line in mailzeilen$()
  For z = 1 To mailzeilen_anzahl
  mailzeilen$(z) = stringausstring$(message$, z, 0)
  Next 
EndIf 
End Function

;***************************************************************************
; Deletes a mail. To delete it really you must Logout.
Function popMailDelete$(mailnummer)
	WriteLine com, "DELE " + mailnummer ; Send Mail-Delete-commando
	r$ = ReadLine(com) ; get returning-line
	If Mid(r$, 1, 3) = "+OK" Then Return "+OK mail was deleted successful"
	If Mid(r$, 1, 4) = "-ERR" Then Return "-ERR mail could not be deleted"
End Function

; *************************************************************************
; Returns the Number and bigness of all meils in the account
Function popAccountStat$()
WriteLine com, "STAT"
i$ = ReadLine(com)
If Mid(i$, 1, 1) = "-" Then
  Return "-ERR no new messages"
Else
  For z = 5 To Len(i$)
    If Mid(i$, z, 1) = " " Then
      anzahl$ = Mid(i$, 5, z-5)
      groesse$ = Mid(i$, z+1, Len(i$))
      Return anzahl$ + "," + groesse$
    EndIf
  Next   
EndIf
End Function

; **************************************************************************
; Connect and login
Function popAccountLogin$(server$, user$, pass$)
com = OpenTCPStream(server$, 110)
If com = 0 Then
  Return "-ERR connection failed"
Else
  i$ = ReadLine(com) ; Intercept the greeting
  WriteLine com, "USER " + user$
  i$ = ReadLine(com)
  If Mid(i$, 1, 1) = "-" Then
    Return "-ERR no such user"  
  Else
    WriteLine com, "PASS " + pass$
    i$ = ReadLine(com)
    If Mid(i$, 1, 1) = "-" Then
      Return "-ERR password wrong"
    Else
      Return "+OK logged in   
    EndIf
  EndIf
EndIf
End Function

;*******************************************************************************
; Disconnect from server. After that the mails get really deleted
Function popAccountLogout$()
	WriteLine com, "QUIT" ; Disconnect-Commando
	r$ = ReadLine(com) ; Get Returnstring
	If Mid(r$, 1, 3) = "+OK" Then Return "+OK disconnected"
	If Mid(r$, 1, 4) = "-ERR" Then Return "-ERR not disconnected. waaah!"
End Function


;********************************************************************************
; Sending a mail with smtp and Error-handling. Much shorter than BlitzMail Deluxe ^_^
Function smtpSendMail$(server$, an_name$, an_adresse$, von_name$, von_adresse$, subject$, mailtext$)
	com2 = OpenTCPStream(server$ ,25)
	If com2 = 0 Then
		Return "-ERR smtp connection failed"
	Else
		s$ = ReadLine(com) ; Die Begrüßung abfangen
		WriteLine com2, "HELO R-Mail"
  	s$ = ReadLine(com2)
		If Mid(s$, 1, 3) <> "250" Then
		  Return "-ERR HELO failed"
		Else
		  WriteLine com2, "MAIL FROM: " + von_adresse$
		  s$ = ReadLine(com)
  	  If Mid(s$, 1, 3) <> "250" Then
		    If Mid(s$, 1, 3) = "501" Then
		      Return "-ERR use POP-login first"
		    Else
		      Return "-ERR MAIL FROM failed"
        EndIf
		  Else
		    WriteLine com2, "RCPT TO: " + an_adresse$
		    s$ = ReadLine(com2)
		    If Mid(s$, 1, 3) <> "250" Then
		      Return "-ERR RCPT TO failed"
		    Else
		      WriteLine com2, "DATA"
		      s$ = ReadLine(com2)
		      If Mid(s$, 1, 3) <> "354" Then
		        Return "DATA failed"
		      Else
		        WriteLine com2, "Date: " + CurrentDate$ ()
		        WriteLine com2, "From: " + von_name$ + " <" + von_adresse$ + ">"
    		    WriteLine com2, "To: " + an_name$ + " <" + an_adresse$ + ">"
        		WriteLine com2, "Subject: " + subject$
        		WriteLine com2, "X-Mailer: R-Mail"
		        i = 1
						Repeat 
							WriteLine com2, stringausstring$(mailtext$, i, 0)
							i = i + 1 
						Until Len(stringausstring$(mailtext$, i, 0)) = 1 And stringausstring$(mailtext$, i, 0) = "0"
			      WriteLine com2, ""
			      WriteLine com2, "."
			      s$ = ReadLine(com2)
			      If Mid(s$, 1, 3) <> "250" Then
		  	      Return "SendMail failed"
		    	  Else
		     	    WriteLine com2, "QUIT" 
		    	      s$ = ReadLine(com2)
		   	      If Mid(s$, 1, 3) <> "221" Then
		  	        Return "Mail delivery failed"   
		  	      Else
		  	        CloseTCPStream(com2)
		  	        Return "+OK mail delivered"
		  	      EndIf 
	 	   	    EndIf  
		      EndIf  
		    EndIf    
		  EndIf       
		EndIf         
	EndIf	    
End Function

;...............................................................................

; String-Functions


; extracts Numbers that are stored within a string, divided by commas...
 Function zahlausstring(zahlstring$, stelle)
	anzahl = 1 
	letzteskomma = 1 
	For i = 1 To Len(zahlstring$)
		If Mid(zahlstring$, i, 1) = "," Or i = Len(zahlstring$) Then 
			If anzahl = stelle Then zahl = Mid(zahlstring$, letzteskomma, i) 
			letzteskomma = i+1 
			anzahl = anzahl + 1
		EndIf
	Next
	Return zahl 
End Function



; Extracts a string from an big string ("string1|string2|string2")
 Function stringausstring$(s$, stelle, trenncode)
	anzahl = 1 
	letzteskomma = 1 
	For i = 1 To Len(s$)
		If Asc(Mid(s$, i, 1)) = trenncode Or i = Len(s$) Then 
			If anzahl = stelle Then 
				s2$ = Mid(s$, letzteskomma, i-letzteskomma) 
				If i = Len(s$) Then s2$ = Mid(s$, letzteskomma, i-(letzteskomma-1)) 
			  If Len(Mid(s$,letzteskomma,i-letzteskomma)) = 1 Then s2$ = Chr$(0)
			EndIf
			letzteskomma = i+1
			anzahl = anzahl + 1
		EndIf
	Next
	If stelle > (anzahl-1) Then Return "0"
	Return s2$
End Function


;----------------------------------------------------------------------------------
