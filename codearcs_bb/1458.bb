; ID: 1458
; Author: ozak
; Date: 2005-09-09 04:18:12
; Title: Online Patcher using the recursive CRC-32 builder
; Description: HTTP Patcher

; HTTP Patcher for use with BuildCRC by Odin Jensen (www.furi.dk)

; Global config
Global url$ = "http://www.yourwebsitehere.com"
Global patchDir$ = "yourpatchdirhere"
Global aTitle$ = "HTTP Patcher v1.0"
Global winTitle$ = "HTTP Patcher v1.0 by Odin Jensen"
Global gameExe$ = "game.exe"

; Global control handles
Global patchBtn
Global playBtn
Global curProgBar
Global totalProgBar
Global infoBox

; Number of files to be patched
Global numFiles# = 0

; Download bank
Global bank=CreateBank(16384)

; File storage struct
Type FileEntry

	Field fileName$
	
End Type

; Initialize CRC table
Dim CRC32_table(255)
CRC32_init()

; Create main window and controls
InitApp()

; Main loop
While WaitEvent()<>$803

	; Was it a gadget action event?
	If (EventID()=$401)

		; Play button
		If (EventSource()=playBtn)
			ExecFile(gameExe)
		End If 
	
		; Patch button
		If (EventSource()=patchBtn)
			; Connected
			AddGadgetItem(infoBox,"Retrieving patch information.")
	
			; Get patch info file
			If (GetHTTPFile("patchinfo.dat") = False)				
				AddGadgetItem(infoBox, "Error: Could not retrieve patch information")				
			Else
				; Inform the user of our progress
				AddGadgetItem(infoBox, "Patch information retrieved ok.")
				AddGadgetItem(infoBox, "Scanning files.")

				; Read and check files
				inFile = OpenFile("patchinfo.dat")

				While (Not Eof(inFile))

					; Read entry
					curLine$ = ReadLine(inFile)
					fe.FileEntry = New FileEntry
					fe\fileName = Left(curLine, Instr(curLine,",")-1)
					CRC$ = Mid(curLine, Instr(curLine, ",")+1, Len(curLine))					
										
					; Check CRC
					fileCRC$ = Hex(CRC32_FromFile(fe\fileName))
					If (Not fileCRC = CRC)
						AddGadgetItem(infoBox, "File '" +  fe\fileName + "' needs patching.")
						numFiles=numFiles+1
					Else
						AddGadgetItem(infoBox, "File '" +  fe\fileName + "' is up to date.")
						Delete(fe)
					End If
									
				Wend				

				; Close file
				CloseFile(infile)

				; Patch those files
				If (numFiles <> 0)			
					AddGadgetItem(infoBox, "Patching files.")
				End If
				
				progPos = 0
				For fe.FileEntry=Each FileEntry
					
					GetHTTPFile(fe\fileName)
					progPos=progPos+1
					UpdateProgBar(totalProgBar,progPos/numFiles)
				
				Next
	
				; Patching done
				UpdateProgBar(totalProgBar,1)
				AddGadgetItem(infoBox, "Ready to play!")
				EnableGadget(playBtn)
				DisableGadget(patchBtn)

				; Delete patch info file
				DeleteFile("patchInfo.dat")
			End If 					
		EndIf
	EndIf

Wend


; Create main window and controls function
Function InitApp()

	; Set app title
	AppTitle(aTitle)

	; Create main window 		
	mainWindow=CreateWindow(winTitle,(ClientWidth (Desktop ()) / 2) - 200,(ClientHeight (Desktop ()) / 2) - 200,400,400,0,1)
	
	; Create canvas
	mainCanvas = CreateCanvas(0,0,GadgetWidth(mainWindow),GadgetHeight(mainWindow),mainWindow,0)
	
	; Setup canvas for drawing
	SetBuffer(CanvasBuffer(mainCanvas))
	Color(100,100,100)
	Rect(0,0,GadgetWidth(mainWindow),GadgetHeight(mainWindow))
	
	; Create labels
	Color(255,255,255)
	Text(5, 10, "Total progress")
	Text(5, 65, "Current progress")	
	Text(5, 120, "Information")
	
	; Create progress bars
	totalProgBar=CreateProgBar(5, 30, GadgetWidth(mainWindow) - 16, 20, mainCanvas)
	curProgBar=CreateProgBar(5, 85, GadgetWidth(mainWindow) - 16, 20, mainCanvas)
	
	; Create listbox for information
	infoBox=CreateListBox(5, 140, GadgetWidth(mainWindow) - 16, GadgetHeight(mainWindow) - 200, mainCanvas)	
	
	; Create buttons
	patchBtn=CreateButton("Patch", 5, GadgetHeight(mainWindow) - 50, 50, 20, mainCanvas)
	playBtn=CreateButton("Play", GadgetWidth(mainWindow) - 60, GadgetHeight(mainWindow) - 50, 50, 20, mainCanvas)
	DisableGadget(playBtn)
	
	; Add some start info
	AddGadgetItem(infoBox, "Welcome to the patcher. Click the patch button to begin.")
	
	; Update canvas
	FlipCanvas(mainCanvas)
	
End Function

; Get HTTP file function
Function GetHTTPFile(fileName$)

	; Open stream
	tcpStream=OpenTCPStream(url,80)

	; Get and write patch info file
	WriteLine tcpStream,"GET "+url+patchDir+"/"+fileName+" HTTP/1.0" 
	WriteLine tcpStream,Chr$(10) 
	
	; Error?
	If (Eof(tcpStream))	
		Return False 	
	End If 

	; Open file for writing				
	error=False
	Local temp$
	Local size# = 0
	
	; Read header
	Repeat 
		; Read next HTTP line
		temp = ReadLine$(tcpStream)
		
		; If we got a 404 Not Found, the file is missing from the server (or mispelled somewhere :)
		If (Instr(temp, "404 Not Found")) error=True

		; Get size
		If  (Instr(temp, "Content-Length"))
			size = Mid(temp, Instr(temp,": ")+2, Len(temp))
		End If 									
		
		; End of header
		If (temp = "")
		
			Exit
		
		End If
		
	Forever

	; Error?
	If (error)	
		Return False 
	End If

	; Read and write file	
	Local outfile = WriteFile(fileName)

	progPos = 0
	While (Not Eof(tcpStream))
		n=ReadBytes(bank,tcpStream,0,16384)
		WriteBytes(bank,outfile,0,n)
		progPos=progPos+n
		UpdateProgBar(curProgBar,progPos/size)
	Wend

	CloseFile(outfile)

	; Close stream
	CloseTCPStream(tcpStream)				
	
	; All went well
	Return True
	
End Function

; CRC init function
Function CRC32_Init()

  Local i
  Local j
  Local value

  For i=0 To 255
    value=i
    For j=0 To 7
      If (value And $1) Then 
        value=(value Shr 1) Xor $EDB88320
      Else
        value=(value Shr 1)
      EndIf
    Next
    CRC32_table(i)=value
  Next
  
End Function

; Function to get CRC-32 value from a file
Function CRC32_FromFile(name$)

  Local byte
  Local crc
  Local file

  crc=$FFFFFFFF
  file=ReadFile(name$)
  If file=0 Then Return
  While Not Eof(file)
    byte=ReadByte(file)
    crc=(crc Shr 8) Xor CRC32_table(byte Xor (crc And $FF))
  Wend
  Return ~crc
  
End Function
