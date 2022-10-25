; ID: 25
; Author: BlitzSupport
; Date: 2001-08-30 02:54:56
; Title: LoadWebImage
; Description: Loads an image from the web, straight into your game!

; -----------------------------------------------------------------------------
; LoadWebImage -- uses BlitzGet Deluxe, based on Mark Sibly's HTTPGet
; -----------------------------------------------------------------------------
; james@hi-toro.com
; -----------------------------------------------------------------------------

AppTitle "LoadWebImage"

Graphics 640, 480

SetBuffer BackBuffer ()

; -----------------------------------------------------------------------------
; Load an image from the web, straight into our game!
; -----------------------------------------------------------------------------
rocket = LoadWebImage ("http://www.hi-toro.com/boing.png")

; -----------------------------------------------------------------------------
; Check for failure
; -----------------------------------------------------------------------------
If rocket = 0

	RuntimeError "Failed to load web image!": End
	
	; Alternative (BETTER) failure method -- use a default local image supplied with your game...
	; rocket = LoadImage ("rocket.bmp")
	
EndIf

MaskImage rocket, 255, 0, 255

x = 50
y = 50

ClsColor 70, 110, 190

Repeat

	Cls
	
	DrawImage rocket, MouseX (), MouseY ()

	Flip

Until KeyDown (1) = 1

End

Function LoadWebImage (webFile$)
	If BlitzGet (webFile$, CurrentDir (), "temp_web_image.bmp")
		image = LoadImage ("temp_web_image.bmp")
		DeleteFile "temp_web_image.bmp"
	EndIf
	Return image
End Function

Function BlitzGet (webFile$, saveDir$, saveFile$)

	; -------------------------------------------------------------------------
	; Strip "http://" if provided
	; -------------------------------------------------------------------------
	If Left (webFile$, 7) = "http://" Then webFile$ = Right (webFile$, Len (webFile$) - 7)

	; -------------------------------------------------------------------------
	; Split into hostname and path/filename to download
	; -------------------------------------------------------------------------
	slash = Instr (webFile$, "/")
	If slash
		webHost$ = Left (webFile$, slash - 1)
		webFile$ = Right (webFile$, Len (webFile$) - slash + 1)
	Else
		webHost$ = webFile$
		webFile$ = "/"
	EndIf
		
	; -------------------------------------------------------------------------
	; Add trailing slash to download dir if not given
	; -------------------------------------------------------------------------
	If Right (saveDir$, 1) <> "\" Then saveDir$ = saveDir$ + "\"

	; -------------------------------------------------------------------------
	; Save filename -- get from webFile$ if not provided
	; -------------------------------------------------------------------------
	If saveFile$ = ""
		If webFile = "/"
			saveFile$ = "Unknown file.txt"
		Else
			For findSlash = Len (webFile$) To 1 Step - 1
				testForSlash$ = Mid (webFile$, findSlash, 1)
				If testForSlash$ = "/"
					saveFile$ = Right (webFile$, Len (webFile$) - findSlash)
					Exit
				EndIf
			Next
			If saveFile$ = "" Then saveFile$ = "Unknown file.txt"
		EndIf
	EndIf

	; DEBUG
	; RuntimeError "Web host: " + webHost$ + Chr (10) + "Web file: " + webFile$ + Chr (10) + "Save dir: " + saveDir$ + Chr (10) + "Save file: " + saveFile$

	www = OpenTCPStream (webHost$, 80)

	If www
	
		WriteLine www, "GET " + webFile$ + " HTTP/1.1" ; GET / gets default page...
		WriteLine www, "Host: " + webHost$
		WriteLine www, "User-Agent: BlitzGet Deluxe"
		WriteLine www, "Accept: */*"
		WriteLine www, ""
		
		; ---------------------------------------------------------------------
		; Find blank line after header data, where the action begins...
		; ---------------------------------------------------------------------
				
		Repeat
			header$ = ReadLine (www)
			If Left (header$, 16) = "Content-Length: "	; Number of bytes to read
				bytesToRead = Right (header$, Len (header$) - 16)
			EndIf
		Until header$ = "" Or (Eof (www))
		
		If bytesToRead = 0 Then Goto skipDownLoad
		
		; ---------------------------------------------------------------------
		; Create new file to write downloaded bytes into
		; ---------------------------------------------------------------------
		save = WriteFile (saveDir$ + saveFile$)
		If Not save Then Goto skipDownload

		; ---------------------------------------------------------------------
		; Incredibly complex download-to-file routine...
		; ---------------------------------------------------------------------

		For readWebFile = 1 To bytesToRead
		
			If Not Eof (www) Then WriteByte save, ReadByte (www)
			
			; Call BytesReceived with position and size every 100 bytes (slows down a LOT with smaller updates)
			
			tReadWebFile = readWebFile
			If tReadWebFile Mod 100 = 0 Then BytesReceived (readWebFile, bytesToRead)

		Next

		CloseFile save
		
		; Fully downloaded?
		If (readWebFile - 1) = bytesToRead
			success = 1
		EndIf
		
		; Final update (so it's not rounded to nearest 100 bytes!)
		BytesReceived (bytesToRead, bytesToRead)
		
		.skipDownload
		CloseTCPStream www
		
	Else
	
		RuntimeError "Failed to connect"
		
	EndIf
	
	Return success
	
End Function

; -----------------------------------------------------------------------------
; User-defined update function, called every 100 bytes of download -- alter to suit!
; -----------------------------------------------------------------------------
; TIP: Pass a user-defined type instead, with all data (this stuff plus URL, local filename, etc)
; -----------------------------------------------------------------------------
Function BytesReceived (posByte, totalBytes)
	; Example update code...
	Cls
	Text 20, 20, "Downloading file -- please wait..."
	Text 20, 40, "Received: " + posByte + "/" + totalBytes + " bytes (" + Percent (posByte, totalBytes) + "%)"
	Flip
End Function

; -----------------------------------------------------------------------------
; Handy percentage function
; -----------------------------------------------------------------------------
Function Percent (part#, total#)
	Return Int (100 * (part / total))
End Function
