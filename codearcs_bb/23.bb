; ID: 23
; Author: BlitzSupport
; Date: 2001-08-25 01:23:39
; Title: BlitzMail Deluxe
; Description: BlitzMail with error-checking


AppTitle "BlitzMail Deluxe"
Graphics 640, 480

; -----------------------------------------------------------------------------
; BlitzMail Deluxe -- adapted from Mark Sibly's BlitzMail code
; -----------------------------------------------------------------------------
; Sends an email message with decent error checking...
; -----------------------------------------------------------------------------
; ***** INSERT EMAIL ADDRESSES below, as specified! *****
; -----------------------------------------------------------------------------

Const DebugSMTP = 1					; Debug printout = 1
Color 0, 255, 0						; '80s hacker job...

; -----------------------------------------------------------------------------
; . User settings
; -----------------------------------------------------------------------------

	; ---------------------------------------------------------------------
	; SMTP server (CrossWind's is public, but use your own if known)
	; ---------------------------------------------------------------------
		Global mailhost$ = "smtp.crosswinds.net"

	; ---------------------------------------------------------------------
	; Email client name (this program)
	; ---------------------------------------------------------------------
		Global mailer$ = "BlitzMail Deluxe"
		
	; ---------------------------------------------------------------------
	; Sender's email address (***** INSERT YOUR ADDRESS *****)
	; ---------------------------------------------------------------------
		Global mailfrom$ = ""

	; ---------------------------------------------------------------------
	; Sender's real name
	; ---------------------------------------------------------------------
		Global mailname$ = "John Wayne"

; -----------------------------------------------------------------------------
; . Message data
; -----------------------------------------------------------------------------

	; ---------------------------------------------------------------------
	; Send message to this address (***** INSERT RECIPIENT'S ADDRESS *****)
	; ---------------------------------------------------------------------
		mailto$ = ""

	; ---------------------------------------------------------------------
	; Message to send (use | for newline)... keep it fairly short!
	; ---------------------------------------------------------------------
		message$ = "Hi there,||I think you'll agree, BlitzMail rules!||Your friend, John Wayne.|Sent via BlitzMail Deluxe"

; -----------------------------------------------------------------------------
; . Send message, show response ("BlitzMailed!" means success)
; -----------------------------------------------------------------------------

	reply$ = BlitzMail (mailto$, "BlitzMail Deluxe Test!", message$)
	Delay 1000: RuntimeError reply$
	End














; -----------------------------------------------------------------------------
; . SMTP functions
; -----------------------------------------------------------------------------



; -----------------------------------------------------------------------------
; BlitzMail...
; -----------------------------------------------------------------------------
Function BlitzMail$ (mailto$, subject$, message$)
	
	If debugSMTP Then Print "": Print "BlitzMailing...": Print ""
	
	message$ = Replace$ (message$, "|", Chr (13) + Chr (10))
	error$ = "BlitzMailed!"
		
	t = OpenTCPStream (mailhost$, 25)

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
		WriteLine t, "HELO BlitzMail Deluxe"
		response$ = ReadLine (t)
		If Code (response$) <> "250"
			error$ = response$
			Goto abortSMTP
		EndIf


		; ---------------------------------------------------------------------
		; Non-existent command -- try it for error message!
		; ---------------------------------------------------------------------
;		WriteLine t, "LALA BlitzMail Deluxe"
;		response$ = ReadLine (t)
;		If Code (response$) <> "250"
;			error$ = response$
;			Goto abortSMTP
;		EndIf


		; ---------------------------------------------------------------------
		; Tell server who's sending
		; ---------------------------------------------------------------------
		WriteLine t, "MAIL FROM: <" + mailfrom$ + ">"
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
		WriteLine t, "RCPT TO: <" + mailto$ + ">"
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
		WriteLine t, "Date: "		+ CurrentDate$ ()
		WriteLine t, "From: "		+ mailname$ + " <" + mailfrom$ + ">"
		WriteLine t, "To: "			+ mailto$ + " <" + mailto$ + ">"
		WriteLine t, "Subject: "	+ subject$
		WriteLine t, "X-Mailer: "	+ mailer$

		; ---------------------------------------------------------------------
		; Email message
		; ---------------------------------------------------------------------
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
		If error$ = "" Then error$ = "Timeout error"
		Return error$
		
	Else

		; ---------------------------------------------------------------------
		; Oops. Forgot to go online (or server isn't there)	
		; ---------------------------------------------------------------------
		Return "Failed to connect to server at " + mailhost$
		
	EndIf
		
End Function

; -----------------------------------------------------------------------------
; Ask server for help (usually list of commands)... not much use, just example
; -----------------------------------------------------------------------------

Function GetHelp (server$)
	t = OpenTCPStream (mailhost$, 25)
	If t
		ReadLine (t) ; 220
		WriteLine t, "HELO BlitzMail Deluxe"
		ReadLine (t) ; 250
		WriteLine t, "HELP"
		response$ = ReadLine (t)
		error$ = Left (response$, 3)
		If error$ = "214"
			help$ = response$
			Repeat
				readhelp$ = ReadLine (t)
				help$ = help$ + Chr (10) + readhelp$
			Until readhelp$ = ""
			RuntimeError help$
		Else
			RuntimeError "Couldn't get help information!"
		EndIf
		CloseTCPStream (t)
	EndIf
End Function

; -----------------------------------------------------------------------------
; Return 3 digit code from server's response
; -----------------------------------------------------------------------------

Function Code$ (code$)
	If debugSMTP
		Print code$
	EndIf
	Return Left (code$, 3)
End Function

