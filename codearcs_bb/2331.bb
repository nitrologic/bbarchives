; ID: 2331
; Author: boomboom
; Date: 2008-10-10 16:06:34
; Title: OSALib
; Description: OSAKit Communication Library

;- OSA KIT LIBRARY -------------------------------------------------------
;-------------------------------------------------------------------------
;Communication library for OSA Kit (http://www.osakit.com/).
;By Chris 'boomboom' Bate (me@chrisbate.com).
;Version 1.21
;
;Use the Public Functions
;Put the Update Function in your main loop, with your application title as the string.
;Currently suports 1 download (another download request will overwrite the last)
;Doesn't Support browser communication scripts
;
;Uses User32.decls
;
;If you make it better please rerelease it, or email it to me at the address above :)

;Public
Function OSA_Download(URL$)
	
	If URL <> "" Then
		OSA_DownloadingURL = URL
		OSA_DownloadPercent = 0
		OSA_DownloadingSomething = True
		OSA_DownloadStalledTimer = 0
		OSA_DownloadStalledLastPercent = 0
		OSA_CreateOutgoingMessage("DOWNLOAD",URL)
	Else
		RuntimeError "OSA_Download | File not found on server"
	End If
	
End Function
Function OSA_GetPercent%()

	Return OSA_DownloadPercent

End Function
Function OSA_GetDownloadLocation$()

	;Local Variables -----
	Local Location$
	;---------------------
	
	If OSA_DownloadLocation <> ""
		;Reset Variables
		OSA_DownloadingSomething = False
		OSA_DownloadingURL =""
		Location = OSA_DownloadLocation
		OSA_DownloadLocation = ""
		Return Location
	Else
		Return ""
	End If

End Function
Function OSA_Browse(URL$)

	If URL <> "" Then
		OSA_CreateOutgoingMessage("BROWSE",URL)
	Else
		RuntimeError "OSA_Browse| Please Enter Valid URL"
	End If

End Function
Function OSA_BrowseNew(URL$)

	If URL <> "" Then
		OSA_CreateOutgoingMessage("BROWSENEW",URL)
	Else
		RuntimeError "OSA_Browse| Please Enter Valid URL"
	End If

End Function
Function OSA_EmbedMe()

	OSA_CreateOutgoingMessage("EMBEDME")

End Function
Function OSA_ReleaseMe()

	OSA_CreateOutgoingMessage("RELEASEME")

End Function
Function OSA_ShowMe()

	OSA_CreateOutgoingMessage("SHOWME")

End Function
Function OSA_HideMe()

	OSA_CreateOutgoingMessage("HIDEME")

End Function

Function OSA_UpdateOSA(NormalAppTitle$)

	;Local Variables -----
	Local tOSA_System.OSA_System ;Define OSA_System
	Local tMsgOut.OSA_OutgoingMessage ;Define Outgoing Message Type
	Local InMessage$
	Local OutMessage$
	;---------------------
	
	;Gather Message & Reset
	InMessage = OSA_GetWindowText() : OSA_SetWindowText(NormalAppTitle)
	
	;Return OSA_System Type
	tOSA_System.OSA_System = First OSA_System
	
	;Make if Null
	If tOSA_System = Null And InMessage <> NormalAppTitle Then 
		tOSA_System = New OSA_System
			tOSA_System\ID = Handle(tOSA_System)
			tOSA_System\DefaultAppTitle = NormalAppTitle
			tOSA_System\OSAhwnd = Int(InMessage)
	End If
	
	;Use if not Null
	If tOSA_System <> Null
		
		;INCOMING
		Select OSA_ParseMessageCommand(InMessage)
			
			Case "DOWNLOADED"
				OSA_DownloadLocation$ = OSA_ParseMessageParameter(InMessage)
			
			Case "PERCENT"
				OSA_DownloadPercent = Int(OSA_ParseMessageParameter(InMessage))
			
		End Select
		
		;OUTGOING
		tMsgOut = First OSA_OutgoingMessage
		If tMsgOut <> Null
			
			;Process Outgoing Message
			OutMessage = tMsgOut\Command
			If tMsgOut\Parameter <> "" Then OutMessage = OutMessage + "|" + tMsgOut\Parameter
			
			;Send Message
			OSA_SetWindowText(OutMessage,tOSA_System\OSAhwnd)
			
			;Remove Message
			Delete tMsgOut
			
		Else
			
			;Message System Idle? Update Checks
			If OSA_DelayedCheck > 20 Then
				
				;Updates File Download Percentage
				If OSA_DownloadingSomething = True Then OSA_CreateOutgoingMessage("GETPERCENT")
				
				;Forces Destroy if OSAKit Destruction
				If api_IsWindow(tOSA_System\OSAhwnd) = False End
				
				OSA_DelayedCheck = 0
			Else
				OSA_DelayedCheck = OSA_DelayedCheck + 1
			End If
			
			;Download Stalled? Restart Download
			If OSA_DownloadingSomething = True
				If OSA_DownloadStalledTimer = 150
					
					If OSA_GetPercent() = OSA_DownloadStalledLastPercent Then OSA_Download(OSA_DownloadingURL)
					If OSA_GetPercent() > OSA_DownloadStalledLastPercent Then OSA_DownloadStalledLastPercent = OSA_GetPercent()
					
					OSA_DownloadStalledTimer = 0
				Else
					OSA_DownloadStalledTimer = OSA_DownloadStalledTimer + 1
				End If
			End If
			
		End If
		
	End If
	
End Function

;Private
Global OSA_DownloadingURL$
Global OSA_DownloadPercent%
Global OSA_DelayedCheck%
Global OSA_DownloadLocation$
Global OSA_DownloadingSomething%

Global OSA_DownloadStalledTimer%
Global OSA_DownloadStalledLastPercent%

Type OSA_System

	Field ID%
	
	Field DefaultAppTitle$
	Field OSAhwnd%

End Type
Type OSA_OutgoingMessage

	Field ID%
	
	Field Command$
	Field Parameter$

End Type

Function OSA_GetWindowText$(hwnd%=0)
	
	;Local Variables -----
	Local MessageBank% ;Bank for incoming data
	Local length% ;Length of message (how many characters)
	Local i% ;Standard loop varible
	Local InMessage$ ;Message as string
	;---------------------

	If hwnd = 0 Then hwnd = SystemProperty("AppHWND")

	MessageBank = CreateBank(254)
	
	length = api_GetWindowText(hwnd,MessageBank,254)
	
	If length <> 0 Then
		
		For i = 0 To length - 1
			InMessage = InMessage + Chr((PeekByte(MessageBank,i)))
		Next
		
		FreeBank MessageBank
		
		Return InMessage
		
	End If
	
End Function
Function OSA_SetWindowText(OutMessage$,hwnd%=0)

	If hwnd = 0 Then hwnd = SystemProperty("AppHWND")

	api_SetWindowText(hwnd%, OutMessage$)
	
End Function
Function OSA_CreateOutgoingMessage%(Command$,Parameter$="")
	
	;Local Variables -----
	Local tMsgOut.OSA_OutgoingMessage ;Define Outgoing Message Type
	;---------------------
	
	tMsgOut = New OSA_OutgoingMessage
		tMsgOut\ID = Handle(tMsgOut)
		tMsgOut\Command = Command
		tMsgOut\Parameter = Parameter
	
	Return tMsgOut\ID

End Function
Function OSA_ParseMessageCommand$(Msg$)

	;Local Variables -----
	Local i% ;Standard loop variable.
	Local Current$ ;Current character being parsed.
	;---------------------

	For i = 1 To Len(Msg)
		Current = Mid(Msg,i,1)
		If Current = "|" Then Return Left(Msg,i-1)
	Next

End Function
Function OSA_ParseMessageParameter$(Msg$)

	;Local Variables -----
	Local i% ;Standard loop variable.
	Local Current$ ;Current character being parsed.
	;---------------------

	For i = 1 To Len(Msg)
		Current=Mid(Msg,i,1)
		If Current="|" Then Return Mid(Msg,i+1,Len(Msg))
	Next

End Function
;=========================================================================
;=========================================================================
