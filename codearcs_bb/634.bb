; ID: 634
; Author: BlitzSupport
; Date: 2003-03-23 06:17:34
; Title: BlitzLeech
; Description: Crude multi-stream file downloader

; -----------------------------------------------------------------------------
; BlitzLeech -- simple HTTP 1.1 downloader... james @ hi - toro . com
; -----------------------------------------------------------------------------

; Instructions/disclaimers/excuses...

; Paste URLs into top text field. Click Leech. Slow due to unthreaded-ness and
; some hackiness to try and work around it a little... delays expected on
; connection and disconnection from server, hence apparent freeze-ups! List-view
; NOT properly updated, so don't worry. Abort or Exit will clean everything up,
; though the downloaded files will remain...

; -----------------------------------------------------------------------------
; Hacky work-in-progress! Judge not beta code lest ye beta code be judged, etc.
; -----------------------------------------------------------------------------



; CHANGE TO SUIT...

Global path$ = "C:\"	; Temp download folder -- careful, in case you have other
						; files here with the same name as a download!





; -----------------------------------------------------------------------------
; Name of download program...
; -----------------------------------------------------------------------------

Global Leecher$ = "BlitzLeech Plus"

Global list

; -----------------------------------------------------------------------------
; Download type... [NOT ALL RELEVANT. TO BE SORTED!]
; -----------------------------------------------------------------------------

Type Download

	Field folder$
	Field filename$
	Field savestream
	
	Field listpos

	Field webstream
	
	Field url$
	Field host$
	Field file$
	Field header$
	Field totalbytes
	Field currentbyte
	Field date$
	Field server$
	Field content$
	Field reply$

End Type

; -----------------------------------------------------------------------------
; StartDownload -- starts downloading a file...
; -----------------------------------------------------------------------------

; NOTE: This part causes a delay. This is because it has to connect to the
; server and then wait for it to reply. This could only really be avoided
; by creating a new thread for each download, which ain't gonna happen soon!

Function StartDownload.Download (url$, folder$, filename$ = "")

	; -------------------------------------------------------------------------
	; Create new download...
	; -------------------------------------------------------------------------

	d.Download = New Download
	
	d\listpos = -1
	
	; -------------------------------------------------------------------------
	; Remove "http://" from URL if it's there...
	; -------------------------------------------------------------------------
	
	If Left (url$, 7) = "http://" Then url$ = Right (url$, Len (url$) - 7)
	d\url = url$

	; -------------------------------------------------------------------------
	; Split into host ("www.whatever.com") and remote filename ("/text/test.txt")...
	; -------------------------------------------------------------------------

	slash = Instr (url$, "/")
	If slash
		d\host = Left (url$, slash - 1)
		d\file = Right (url$, Len (url$) - slash + 1)
	Else
		d\host$ = url$
		d\file$ = "/"
	EndIf

	; -------------------------------------------------------------------------
	; Local folder (add trailing slash if missing)...
	; -------------------------------------------------------------------------

	If Right (folder$, 1) <> "\" Then folder$ = folder$ + "\"
	d\folder$ = folder$

	; -------------------------------------------------------------------------
	; Filename...
	; -------------------------------------------------------------------------

	If filename$ = ""
		If d\file = "/"
			filename$ = "Unknown file.txt"
		Else
			For findSlash = Len (d\file$) To 1 Step - 1
				testForSlash$ = Mid (d\file$, findSlash, 1)
				If testForSlash$ = "/"
					filename$ = Right (d\file$, Len (d\file$) - findSlash)
					Exit
				EndIf
			Next
			If filename$ = "" Then filename$ = "Unknown file.txt"
		EndIf
	EndIf

	d\filename$ = filename$
	
	; -------------------------------------------------------------------------
	; Open connection to server...
	; -------------------------------------------------------------------------

	d\webstream = OpenTCPStream (d\host, 80)
	
	If d\webstream

		; ---------------------------------------------------------------------
		; Send request header...
		; ---------------------------------------------------------------------
	
		WriteLine d\webstream, "GET " + d\file + " HTTP/1.1"
		WriteLine d\webstream, "Host: " + d\host
		WriteLine d\webstream, "User-Agent: "+ Leecher$
		WriteLine d\webstream, "Accept: */*"
		WriteLine d\webstream, ""

		; ---------------------------------------------------------------------
		; Server replies with several lines followed by a blank line...
		; ---------------------------------------------------------------------
		
		Repeat

			; -----------------------------------------------------------------
			; Read one line of server's response header...
			; -----------------------------------------------------------------
			
			d\header = ReadLine (d\webstream)

			d\reply = "" ; Reset reply string...

			; -----------------------------------------------------------------
			; Replies are in the form of "Blah: xxxx" (note colon and space)...
			; -----------------------------------------------------------------
			
			pos = Instr (d\header, ": ")
			If pos
				d\reply = Left (d\header, pos + 1)
			EndIf

			; -----------------------------------------------------------------
			; ReplyContent () gets the actual information for each line...
			; -----------------------------------------------------------------

			Select Lower (d\reply)
				Case "content-length: "
					d\totalbytes = ReplyContent (d)
				Case "date: "
					d\date = ReplyContent (d)
				Case "server: "
					d\server = ReplyContent (d)
				Case "content-type: "
					d\content = ReplyContent (d)
				Default
					If gotReply = 0 Then initialReply$ = d\header: gotReply = 1
			End Select

		Until d\header = "" Or (Eof (d\webstream))

		; ---------------------------------------------------------------------
		; Fie size is zero? Weird. Oh, well, end of download...
		; ---------------------------------------------------------------------
		
		If d\totalbytes = 0
			CloseTCPStream d\webstream
			Delete d
			Return Null
		EndIf
		
		; ---------------------------------------------------------------------
		; Create new file to write downloaded bytes into...
		; ---------------------------------------------------------------------

		d\savestream = WriteFile (d\folder + d\filename)

		; ---------------------------------------------------------------------
		; Couldn't create file? Too bad. End of download...
		; ---------------------------------------------------------------------

		If Not d\savestream
			CloseTCPStream d\webstream
			Delete d
			Return Null
		EndIf

		; ---------------------------------------------------------------------
		; Got all the information we need to continue the download...
		; ---------------------------------------------------------------------
		
		Return d
		
	Else

		; ---------------------------------------------------------------------
		; Failed to connect to server...
		; ---------------------------------------------------------------------

		Delete d
		Return Null

	EndIf

End Function

; -----------------------------------------------------------------------------
; UpdateDownloads -- updates downloads and returns number of active downloads...
; -----------------------------------------------------------------------------

Function UpdateDownloads ()

	; -------------------------------------------------------------------------
	; Go through each .Download...
	; -------------------------------------------------------------------------

	For d.Download = Each Download

		; ---------------------------------------------------------------------
		; Count downloads still ongoing...
		; ---------------------------------------------------------------------

		downloads = downloads + 1

		If Not Eof (d\webstream)

			; -----------------------------------------------------------------
			; Still receiving from server, so write bytes to local file...
			; -----------------------------------------------------------------

			For a = 1 To 10
				If Not Eof (d\webstream)
					WriteByte d\savestream, ReadByte (d\webstream)
					d\currentbyte = d\currentbyte + 1
					ModifyGadgetItem list, d\listpos, d\url + " (" + d\currentbyte + " / " + d\totalbytes + " bytes)"
				Else
					Exit
				EndIf
			Next

		Else

			; -----------------------------------------------------------------
			; Server's all done. End of download... [ADD FILE SIZE CHECK!]
			; -----------------------------------------------------------------

			RemoveGadgetItem list, d\listpos
			ClearGadgetItems list
			For d2.Download = Each Download
				If d2 <> d
					AddGadgetItem list, d2\url + " (" + d2\currentbyte + " / " + d2\totalbytes + " bytes)", True
					d2\listpos = SelectedGadgetItem (list)
				EndIf
			Next
			
			CloseFile d\savestream
			CloseTCPStream d\webstream
;			Notify d\url + " complete!"
			Delete d
			
			downloads = downloads - 1
			
		EndIf

	Next

	; -------------------------------------------------------------------------
	; Return number of downloads still ongoing...
	; -------------------------------------------------------------------------

	Return downloads

End Function

; -----------------------------------------------------------------------------
; ReplyContent -- just strips left-side of server reply ("Blah: " part)...
; -----------------------------------------------------------------------------

Function ReplyContent$ (d.Download)
	Return Right (d\header, Len (d\header) - Len (d\reply))
End Function

; -----------------------------------------------------------------------------
; Percent -- returns percentage of 'current byte' / 'total bytes' ...
; -----------------------------------------------------------------------------

Function Percent# (part#, total#)
	Return part / total
End Function

; -----------------------------------------------------------------------------
; AbortDownloads -- safely abort all downloads (eg. on quitting early)...
; -----------------------------------------------------------------------------

Function AbortDownloads ()
	For d.Download = Each Download
		CloseFile d\savestream
		CloseTCPStream d\webstream
		Delete d
		ClearGadgetItems list
	Next
End Function

; -----------------------------------------------------------------------------
; D E M O . . .
; -----------------------------------------------------------------------------

Function CenterWindow (title$, width, height, group = 0, style = 15)
	Return CreateWindow (title$, (ClientWidth (Desktop ()) / 2) - (width / 2), (ClientHeight (Desktop ()) / 2) - (height / 2), width, height, group, style)
End Function

Const EVENT_None		= $0		; No event (eg. a WaitEvent timeout)
Const EVENT_KeyDown		= $101		; Key pressed
Const EVENT_KeyUp		= $102		; Key released
Const EVENT_ASCII		= $103		; ASCII key pressed
Const EVENT_MouseDown	= $201		; Mouse button pressed
Const EVENT_MouseUp		= $202		; Mouse button released
Const EVENT_MouseMove	= $203		; Mouse moved
Const EVENT_Gadget		= $401		; Gadget clicked
Const EVENT_Move		= $801		; Window moved
Const EVENT_Size		= $802		; Window resized
Const EVENT_Close		= $803		; Window closed
Const EVENT_Front		= $804		; Window brought to front
Const EVENT_Menu		= $1001		; Menu item selected
Const EVENT_LostFocus	= $2001		; App lost focus
Const EVENT_GotFocus	= $2002		; App got focus
Const EVENT_Timer		= $4001		; Timer event occurred

AppTitle Leecher$

window = CenterWindow (Leecher$, 400, 300, 0, 1 + 2)
SetMinWindowSize window, 340, 200

url = CreateTextField (5, 5, ClientWidth (window) - 80, 25, window)
go = CreateButton ("Leech!", ClientWidth (window) - 70, 5, 65, 25, window)
list = CreateListBox (5, 35, ClientWidth (window) - 10, ClientHeight (window) - 110, window)
prog = CreateProgBar (5, ClientHeight (window) - 65, ClientWidth (window) - 10, 25, window)
abort = CreateButton ("Abort all downloads", 5, ClientHeight (window) - 30, 150, 25, window)
quit = CreateButton ("Exit program", ClientWidth (window) - 160, ClientHeight (window) - 30, 150, 25, window)

SetGadgetLayout url, 1, 1, 1, 0
SetGadgetLayout go, 0, 1, 1, 0
SetGadgetLayout list, 1, 1, 1, 1
SetGadgetLayout prog, 1, 1, 0, 1
SetGadgetLayout abort, 1, 0, 0, 1
SetGadgetLayout quit, 0, 1, 0, 1

ActivateGadget url

Repeat

	e = WaitEvent (10)
	
	Select e
		Case EVENT_Gadget
	
			Select EventSource ()

				Case url
					If EventData () = 13
						leech$ = TextFieldText (url)
						If leech$ <> ""
							DisableGadget url
							DisableGadget go
							dl.Download = StartDownload (leech$, path$, "")
							If dl = Null
								Notify "Download of " + Chr (34) + leech$ + Chr (34) + " failed!"
							Else
								count = count + 1
								AddGadgetItem list, dl\url + " (" + dl\currentbyte + " / " + dl\totalbytes + " bytes)", True
								dl\listpos = SelectedGadgetItem (list)
							EndIf
							SetGadgetText url, ""
							EnableGadget url
							EnableGadget go
							ActivateGadget url
						EndIf
					EndIf
				Case go
					leech$ = TextFieldText (url)
					If leech$ <> ""
						DisableGadget url
						DisableGadget go
						dl.Download = StartDownload (leech$, path$, "")
						If dl = Null
							Notify "Download of " + Chr (34) + leech$ + Chr (34) + " failed!"
						Else
							count = count + 1
							AddGadgetItem list, dl\url + " (" + dl\currentbyte + " / " + dl\totalbytes + " bytes)", True
							dl\listpos = SelectedGadgetItem (list)
						EndIf
						SetGadgetText url, ""
						EnableGadget url
						EnableGadget go
						ActivateGadget url
					EndIf
				Case abort
					AbortDownloads ()
					UpdateProgBar prog, 0
				Case quit
					AbortDownloads ()
					UpdateProgBar prog, 0
					End
				Case list
					pos = SelectedGadgetItem (list)
					For dl.Download = Each Download
						If dl\listpos = pos
							message$ = "Folder: " + dl\folder
							message$ = message$ + Chr (10) + "Filename: " + dl\filename
							message$ = message$ + Chr (10) + "List position: " + dl\listpos + " (check = " + pos + ")"
							message$ = message$ + Chr (10) + "URL: " + dl\url
							message$ = message$ + Chr (10) + "Host: " + dl\host
							message$ = message$ + Chr (10) + "File: " + dl\file
							message$ = message$ + Chr (10) + "Header: " + dl\header
							message$ = message$ + Chr (10) + "Total bytes: " + dl\totalbytes
							message$ = message$ + Chr (10) + "Current byte: " + dl\currentbyte
							message$ = message$ + Chr (10) + "Date: " + dl\date
							message$ = message$ + Chr (10) + "Server: " + dl\server
							message$ = message$ + Chr (10) + "Content: " + dl\content
							message$ = message$ + Chr (10) + "Reply: " + dl\reply
							Notify message$
							Exit
						EndIf
					Next
									
			End Select
				
	End Select

	UpdateDownloads ()

	pos = SelectedGadgetItem (list)
	For dl.Download = Each Download
		If dl\listpos = pos
			UpdateProgBar prog, Percent (dl\currentbyte, dl\totalbytes)
		EndIf
	Next

Until e = EVENT_Close

End
