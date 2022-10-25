; ID: 725
; Author: BlitzSupport
; Date: 2003-06-23 15:40:21
; Title: POP3 email retrieval
; Description: Retrieves emails from your POP3 account...

; ----------------------------------------------------------------------------
; POP3 access... james @ hi - toro . com
; ----------------------------------------------------------------------------

; IMPORTANT! Enter your POP3 server, username and password in the demo code at the bottom, or it won't do much!

; Very raw example of connecting to POP3 server and reading all emails. Hit
; ESC to stop if you have too many! The demo code is at the bottom...

; NOTE: this demo doesn't delete mails from the server, so you'll still be able
; to access them from your normal client afterwards.

; ----------------------------------------------------------------------------
; Required types, globals and constants...
; ----------------------------------------------------------------------------

Type Email
	Field number
End Type

Global POP3_Fail

Const FAIL_None		= 0
Const FAIL_NoServer	= 1
Const FAIL_User		= 2
Const FAIL_Pass		= 3

; ----------------------------------------------------------------------------
; Opens POP3 connection and returns stream (if 0, check POP3_Fail global)...
; ----------------------------------------------------------------------------

Function OpenPOP (pop3$, user$, pass$)

	; ------------------------------------------------------------------------
	; Open POP3 connection...
	; ------------------------------------------------------------------------
	
	pop = OpenTCPStream (pop3$, 110)
	
	If pop
	
		; --------------------------------------------------------------------
		; Get welcome message...
		; --------------------------------------------------------------------
	
		welcome$ = ReadLine (pop)
	
		If OK (welcome$)
	
			; ----------------------------------------------------------------
			; Send POP3 username...
			; ----------------------------------------------------------------
			
			WriteLine pop, "USER " + user$

			user$ = ReadLine (pop)
	
			If OK (user$)
	
				; ------------------------------------------------------------
				; Send POP3 password...
				; ------------------------------------------------------------
							
				WriteLine pop, "PASS " + pass$
				pass$ = ReadLine (pop)

				If OK (pass$)
					POP3_Fail = FAIL_None
					Return pop
				Else
					ClosePOP (pop)
					POP3_Fail = FAIL_Pass
					Return False
				EndIf

			Else
				ClosePOP (pop)
				POP3_Fail = FAIL_User	; May return password failure
				Return False			; instead, depending on server...

			EndIf
	
		EndIf

	Else
		POP3_Fail = FAIL_NoServer
		Return False
	EndIf

End Function

; Closes the given POP3 connection...

Function ClosePOP (stream, remove = 0)
	If stream
		If Not remove
			WriteLine stream, "RSET"
			ReadLine (stream) ; "+OK Markers cleared"
		EndIf
		WriteLine stream, "QUIT"
		quit$ = ReadLine (stream)
		CloseTCPStream stream
		If OK (quit$) = False
			Return False
		EndIf
		Return True
	EndIf
End Function

; Fills the .Email type list with available email index numbers (clears
; any existing list first)...

Function GetEmailList (stream)
	If stream
		Delete Each Email
		WriteLine stream, "LIST"
		list$ = ReadLine (stream)
		If OK (list$)
			Repeat
				entry$ = ReadLine (stream)
				If entry$ = "." Then Exit
				e.Email = New Email
				e\number = Left (entry$, Instr (entry$, " ") - 1)
			Forever
		EndIf
	EndIf
End Function

; Reads a single specified message. Call GetEmailList () to fill the .Email
; type list with available emails before calling this on each one you're
; interested in...

; The 'remove' parameter will mark a message for deletion. This doesn't occur until
; you call ClosePOP (), which must also have its failsafe 'remove' parameter set to
; True, or nothing will be deleted...

Function ReadMessage (stream, number, remove = 0)
	If stream
		WriteLine stream, "RETR " + number ; Retrieve email number x...
		; Get result...
		msg$ = ReadLine (stream)
		If OK (msg$)
			Print "----------------------------------------------------------------------------"
			; Read header (read until blank line)...
			Repeat
				header$ = ReadLine (stream)
				pos = Instr (header$, ": ")
				If pos
					Select Left (header$, pos - 1)
						Case "From"
							Print headers ; Mid (header$, pos + 2)
						Case "To"
							Print header$
						Case "Subject"
							Print header$
						Case "X-Mailer"
							Print header$
						Case "Date"
							Print header$
						Case "Message-ID"
							Print header$
;						Default
;							Print header$
					End Select
				EndIf
			Until header$ = ""
			; Message body (read until single period)...
			Print "----------------------------------------------------------------------------"
			Print ""
			Repeat
				msg$ = ReadLine (stream)
				If msg$ = "." Then Exit ; End of message; exit loop
				If msg$ = ".." Then msg$ = "." ; We get ".." if original line was "."
				Print msg$
				If KeyHit (1) Then Exit
			Forever
			Print ""
		EndIf
		If remove
			WriteLine stream, "DELE " + number
		EndIf
	EndIf
End Function

; Convenience function that calls ReadMessage () for each email in the list...

; The 'remove' parameter will mark a message for deletion. This doesn't occur until
; you call ClosePOP (), which must also have its failsafe 'remove' parameter set to
; True, or nothing will be deleted...

Function ReadAllEmails (stream, remove = 0)
	If stream
		For e.Email = Each Email
			ReadMessage (stream, e\number, remove)
		Next
	EndIf
End Function

; Used internally by other functions; checks result is "+OK"...

Function OK (result$)
	If Left (result$, 3) = "+OK" Then Return True
End Function

; Returns raw stats (eg. "10 64000", where 10 is the number of emails and
; 64000 is the number of bytes in total for all emails)...

Function GetStat$ (stream)
	If stream
		WriteLine stream, "STAT"
		Return ReadLine (stream)
	EndIf
End Function

; ----------------------------------------------------------------------------
; Demo...
; ----------------------------------------------------------------------------

AppTitle "POP3 Test..."
Graphics 640, 480, 0, 2

pop3$ = "your_pop3_server"
user$ = "your_pop3_username"
pass$ = "your_password"

Print ""
Print "Connecting to POP3 server..."
Print ""

pop = OpenPOP (pop3$, user$, pass$)

If pop

	GetEmailList (pop)
	ReadAllEmails (pop)
	ClosePOP (pop)

Else

	Select POP3_Fail
		Case FAIL_NoServer
			Print "Cannot find server!"
		Case FAIL_User
			Print "Username doesn't exist!"
		Case FAIL_Pass
			Print "Incorrect username or password!"
	End Select

EndIf

Print ""
Print "Click mouse to exit..."
Print ""

MouseWait
End
