; ID: 1263
; Author: oracle1124
; Date: 2005-01-21 05:10:51
; Title: Mail Sender
; Description: A mail sender

;------------------------------------------------------------------------------
; M A I L E R
;------------------------------------------------------------------------------
;
; Application Name: Mailer (Module)
; Author: Ben Pearson (oracle)
; Description: 	A simple Email API to interface with a game that needs some
; 				email interactivity
;------------------------------------------------------------------------------
; Started: Dec 2004
; Last Modified: Jan 2005
;------------------------------------------------------------------------------
;
;------------------------------------------------------------------------------
; U S A G E
;------------------------------------------------------------------------------
; To use simply place the game code section into your main game (minus the ;'s)
; Then refer to the code by SendMail() or SendMailAttach() depending on what you want.
; Based on BlitzMail by Mark Sibly and BlitzMail Deluxe by Blitz Support
;
;------------------------------------------------------------------------------
; G A M E  C O D E
;------------------------------------------------------------------------------
;Global Mailer$="GameMailer"
;Global MailServer$="your.mail.server.com"
;Global GameDir$=SystemProperty("appdir")
;Global status$=""
;Include "mail.bb"
;.
;.
;.
;status$ = SendMail$(MailTo$, EmailTo$, MailFrom$, EmailFrom$, MailSubject$, MailBody$)
; -OR-
;status$ = SendMailAttach$(MailTo$, EmailTo$, MailFrom$, EmailFrom$, MailSubject$, MailBody$, MailAttachment$)
;------------------------------------------------------------------------------
;
;------------------------------------------------------------------------------
; L I M I T A T I O N S
;------------------------------------------------------------------------------
; 1. Using the Attachment portion, you can't use directories with space? (Need to test)
; 2. Cannot use with HTML Email Accounts like Hotmail. Need SMTP Accounts.
;
;------------------------------------------------------------------------------
; V A R I A B L E  D E S C R I P T I O N S
;------------------------------------------------------------------------------
; FromName$ - The name of the person the email is coming from
; FromEmail$ - The email address of the person the email is coming from
; ToName$ - The name of the person the email is going to
; ToEmail$ - The email address of the person the email is going to
; Subject$ - This line goes into the Subject of the email
; Body$ - This line goes into the body of the email (haven't tested multi-line yet, but should work with a | character as the new line marker (NOT Enter))
; Attach$ - The name of the attachment file (under the GameDir$)
; Mailer$ - Just a string to identify what mail program is
; MailServer$ - The mail server address eg. mail.somesite.com
; GameDir$ - The location of the game binary (or whatever you use this program for!)
; status$ - Returns if the command was successful or not (success is a "" string)
;
;------------------------------------------------------------------------------
; S E N D M A I L  -  W I T H  N O  A T T A C H M E N T
;------------------------------------------------------------------------------
Function SendMail$(FromName$, FromEmail$, ToName$, ToEmail$, Subject$, Body$)
	
	Body$ = Replace$(Body$,"|",Chr(13)+Chr(10))
		
	t = OpenTCPStream (MailServer$, 25)

	If t

		; ---------------------------------------------------------------------
		; Service available?
		; ---------------------------------------------------------------------
		response$ = ReadLine (t)
		code$ = Code (response$)
		If code$ <> "220"
			If code$ = "421"
				error$ = "Service not available"
			Else
				error$ = response$
			EndIf		
			Goto abortSMTP
		EndIf
		
		; ---------------------------------------------------------------------
		; Say "Hi"
		; ---------------------------------------------------------------------
		WriteLine t, "HELO "+Mailer$
		response$ = ReadLine (t)
		If Code (response$) <> "250"
			error$ = response$
			Goto abortSMTP
		EndIf

		; ---------------------------------------------------------------------
		; Tell server who's sending
		; ---------------------------------------------------------------------
		WriteLine t, "MAIL FROM: <" + FromEmail$ + ">"
		response$ = ReadLine (t)
		code$ = Code (response$)
		If code$ <> "250"
			If code$ = "501"
				error$ = "Email sender not specified (or invalid address)"
			Else
				error$ = response$
			EndIf
			Goto abortSMTP
		EndIf

		; ---------------------------------------------------------------------
		; Tell server who it's going to		
		; ---------------------------------------------------------------------
		WriteLine t, "RCPT TO: <" + ToEmail$ + ">"
		response$ = ReadLine (t)
		code$ = Code (response$)
		If code$ <> "250"
			If code$ = "501"
				error$ = "Email recipient not specified (or invalid address)"
			Else
				error$ = response$
			EndIf
			Goto abortSMTP
		EndIf

		; ---------------------------------------------------------------------
		; Email data
		; ---------------------------------------------------------------------
		WriteLine t, "DATA"
		response$ = ReadLine (t)
		If Code (response$) <> "354"
			error$ = response$
			Goto abortSMTP
		EndIf

		; ---------------------------------------------------------------------
		; Headers
		; ---------------------------------------------------------------------
		WriteLine t, "Content-Transfer-Encoding: base64"
	  	WriteLine t, "Content-Type: multipart/mixed; boundary="+Chr(34)+"_----------=_10167391557129230"+Chr(34)
		WriteLine t, "MIME-Version: 1.0"
		WriteLine t, "Date: "+CurrentDate$()
		WriteLine t, "From: "+FromName$+" <"+FromEmail$+">"
		WriteLine t, "To: "+ToName$+" <"+ToEmail$+">"
		WriteLine t, "Subject: "+Subject$
		WriteLine t, "X-Mailer: "+Mailer$

		; ---------------------------------------------------------------------
		; Email message
		; ---------------------------------------------------------------------
		WriteLine t, "--_----------=_10167391557129230"
		WriteLine t, "Content-Transfer-Encoding: binary"
	  	WriteLine t, "Content-Type: text/plain"
		WriteLine t, " "
		WriteLine t, message$

		; ---------------------------------------------------------------------
		; End of message
		; ---------------------------------------------------------------------
		WriteLine t, ""
		WriteLine t, "."
		response$ = ReadLine (t)
		If Code (response$) <> "250"
			error$ = response$
		EndIf

		; ---------------------------------------------------------------------
		; Say "ciao"
		; ---------------------------------------------------------------------
		WriteLine t, "QUIT"
		response$ = ReadLine (t)
		If Code (response$) <> "221"
			error$ = response$
		EndIf

		; ---------------------------------------------------------------------
		; Return error message, if any
		; ---------------------------------------------------------------------
		.abortSMTP
		CloseTCPStream t
		;If error$ = "" Then error$ = "Timeout error"
		Return error$
		
	Else

		; ---------------------------------------------------------------------
		; Oops. Forgot to go online (or server isn't there)	
		; ---------------------------------------------------------------------
		Return "Failed to connect to server at "+MailServer$
		
	EndIf
		
End Function

;------------------------------------------------------------------------------
; S E N D M A I L  -  W I T H  A T T A C H M E N T
;------------------------------------------------------------------------------
Function SendMailAttach$(FromName$, FromEmail$, ToName$, ToEmail$, Subject$, Body$, Attach$)
	
	; Make the Attachment
	opentemp = WriteFile("temp.att")
	WriteLine opentemp, "temp.att"
	CloseFile(opentemp)
	
	Body$ = Replace$(Body$,"|",Chr(13)+Chr(10))
		
	t = OpenTCPStream (MailServer$, 25)

	If t

		; ---------------------------------------------------------------------
		; Service available?
		; ---------------------------------------------------------------------
		response$ = ReadLine (t)
		code$ = Code (response$)
		If code$ <> "220"
			If code$ = "421"
				error$ = "Service not available"
			Else
				error$ = response$
			EndIf		
			Goto abortSMTP
		EndIf
		
		; ---------------------------------------------------------------------
		; Say "Hi"
		; ---------------------------------------------------------------------
		WriteLine t, "HELO "+Mailer$
		response$ = ReadLine (t)
		If Code (response$) <> "250"
			error$ = response$
			Goto abortSMTP
		EndIf

		; ---------------------------------------------------------------------
		; Tell server who's sending
		; ---------------------------------------------------------------------
		WriteLine t, "MAIL FROM: <" + FromEmail$ + ">"
		response$ = ReadLine (t)
		code$ = Code (response$)
		If code$ <> "250"
			If code$ = "501"
				error$ = "Email sender not specified (or invalid address)"
			Else
				error$ = response$
			EndIf
			Goto abortSMTP
		EndIf

		; ---------------------------------------------------------------------
		; Tell server who it's going to		
		; ---------------------------------------------------------------------
		WriteLine t, "RCPT TO: <" + ToEmail$ + ">"
		response$ = ReadLine (t)
		code$ = Code (response$)
		If code$ <> "250"
			If code$ = "501"
				error$ = "Email recipient not specified (or invalid address)"
			Else
				error$ = response$
			EndIf
			Goto abortSMTP
		EndIf

		; ---------------------------------------------------------------------
		; Email data
		; ---------------------------------------------------------------------
		WriteLine t, "DATA"
		response$ = ReadLine (t)
		If Code (response$) <> "354"
			error$ = response$
			Goto abortSMTP
		EndIf

		; ---------------------------------------------------------------------
		; Headers
		; ---------------------------------------------------------------------
		WriteLine t, "Content-Transfer-Encoding: base64"
	  	WriteLine t, "Content-Type: multipart/mixed; boundary="+Chr(34)+"_----------=_10167391557129230"+Chr(34)
		WriteLine t, "MIME-Version: 1.0"
		WriteLine t, "Date: "+CurrentDate$()
		WriteLine t, "From: "+FromName$+" <"+FromEmail$+">"
		WriteLine t, "To: "+ToName$+" <"+ToEmail$+">"
		WriteLine t, "Subject: "+Subject$
		WriteLine t, "X-Mailer: "+Mailer$

		; ---------------------------------------------------------------------
		; Email message
		; ---------------------------------------------------------------------
		WriteLine t, "--_----------=_10167391557129230"
		WriteLine t, "Content-Transfer-Encoding: binary"
	  	WriteLine t, "Content-Type: text/plain"
		WriteLine t, " "
		WriteLine t, message$

		; ---------------------------------------------------------------------
		; Add the Attachment
		; ---------------------------------------------------------------------
		; Notice the >>? This adds the stuff to the end, the 1 line at the top fixes
		; the email chopping the first line
		DosCmd$="command /C "+GameDir$+"base64 -e "+Attach$+" >> temp.att"
		ExecFile(DosCmd$)
		
		Delay 2000		; Allow some time for the DOS command to run
		
		; Add to the email
		WriteLine t, "--_----------=_10167391557129230"
		WriteLine t, "Content-Transfer-Encoding: base64"
	  	WriteLine t, "Content-Type: application/type?; name="+Chr(34)+Attach$+Chr(34)
		WriteLine t, " "
		
		fa = OpenFile("temp.att")
		FAEOF=0
		
		While FAEOF=0
		 rfa$ = ReadLine$(fa)
		 
		 If rfa$ = "" Then
			FAEOF=1
		 End If
		 
		 WriteLine t, rfa$
		Wend

		CloseFile(fa)		
		
		WriteLine t, "--_----------=_10167391557129230"		

		; ---------------------------------------------------------------------
		; End of message
		; ---------------------------------------------------------------------
		WriteLine t, ""
		WriteLine t, "."
		response$ = ReadLine (t)
		If Code (response$) <> "250"
			error$ = response$
		EndIf

		; ---------------------------------------------------------------------
		; Say "ciao"
		; ---------------------------------------------------------------------
		WriteLine t, "QUIT"
		response$ = ReadLine (t)
		If Code (response$) <> "221"
			error$ = response$
		EndIf

		; ---------------------------------------------------------------------
		; Return error message, if any
		; ---------------------------------------------------------------------
		.abortSMTP
		CloseTCPStream t
		;If error$ = "" Then error$ = "Timeout error"
		Return error$
		
	Else

		; ---------------------------------------------------------------------
		; Oops. Forgot to go online (or server isn't there)	
		; ---------------------------------------------------------------------
		Return "Failed to connect to server at "+MailServer$
		
	EndIf
		
End Function

;------------------------------------------------------------------------------
; Return 3 digit code from server's response
;------------------------------------------------------------------------------

Function Code$ (code$)
	;If debugSMTP
	;	Print code$
	;EndIf
	Return Left (code$, 3)
End Function
