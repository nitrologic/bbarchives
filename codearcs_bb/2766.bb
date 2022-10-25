; ID: 2766
; Author: epiblitikos
; Date: 2010-09-11 16:01:19
; Title: Use blitzcc.exe to run BlitzPlus/3D code in your BlitzPlus/3D Program
; Description: A start at an interpreter using TCP and loopback

; Loopback Interpreter Script server/client

; INITILIALIZATION ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
DebugLog "Initializing BlitzPlus Interpreter v"+VERSION

; Set up a BLITZPATH variable in the environment, if it isn't already there (necessary to run blitzcc.exe)
If GetEnv("BLITZPATH") = "" Then SetEnv "BLITZPATH", GetEnv("ProgramFiles")+"\BlitzPlus"
DebugLog "BLITZPATH="+GetEnv("BLITZPATH")

; it isn't really necessary, but making this global up here saves room in the function below (bps=BlitzPlus script)
Global runbps$ = "cmd /C "+Chr(34)+GetEnv("BLITZPATH")+"\bin\blitzcc.exe"+Chr(34)+" cmd.bps >output.txt"


; get a server up to accept your input and "interpret" it
Global server = CreateTCPServer(9876)

; open a stream on the loopback address to send commands through
Global typer = OpenTCPStream("127.0.0.1", 9876)

; accept the stream above as the interpreter's I/O
Global interpreter = AcceptTCPStream(server)

DebugLog "runbps = " + runbps
DebugLog "server = " + server
DebugLog "typer = " + typer
DebugLog "interpreter = " + interpreter
DebugLog "Initialization Complete. Awaiting command."

Print "BlitzPlus Interpreter v0.0.1"

; GAME/PROGRAM LOOP ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; This loop here uses the Input function to take commands from the user. This is not a limitation,
; but a demo of how it could be used. Basically to send something to the interpreter Write[String,
; Byte, Line, etc] it to the typer stream.
While resp$ <> "exit"
	
	; Once again, the Input part is just for demo, you can generate your cmd anyway you can think of
	cmd$ = Input(">>> ")
	
	; get the right-before-send time
	start=MilliSecs()
	
	; Take the input and throw it on the input stream
	WriteString typer, cmd
	
	; Run all the data on the interpreter stream
	While ReadAvail(interpreter)
		RunScript()
	Wend
	
	; print the results to screen, as they are grabbed form the typer buffer
	While ReadAvail(typer)
		resp$ = ReadString(typer)
		If resp = "exit" Then Exit Else Print resp
	Wend
	
	; Logs the time from enter-key to the next prompt/exit
	DebugLog(Str(MilliSecs()-start)+"ms roundtrip")
	
Wend

; Close the input, and kill the server (kills the output stream)
CloseTCPStream in
CloseTCPServer server


Function RunScript()
	
	; Take the cmd as a string off the stream
	cmd$ = ReadString(interpreter)
	DebugLog "Input: " + cmd
	
	; For convenience in this interactive version, I added this exit functionality.
	If Lower(cmd) = "exit" Then
		DebugLog "Exiting"
		WriteString interpreter, "exit"
		Return
	EndIf

	; Type a bang (!) to stop the interpreter in debug mode. VERY USEFUL
	If cmd = "!" Then
		Stop
		
	; Blank lines get ignored, otherwise try doing the cmd
	ElseIf cmd <> "" Then
		
		; Open a file to write our command to. (blitzcc.exe accepts files)
		DebugLog "Opening ./cmd.bps for writing"
		Local outfile = WriteFile("./cmd.bps")
		DebugLog "outfile = " + outfile
		
		; About the only ounce of error-checking with I bothered with for now
		If Not outfile Then
			DebugLog "Unable to open cmd.bps for writing"
			RuntimeError "Unable to open cmd.bps for writing"
		EndIf

		; Write our cmd to the file
		DebugLog "Writing to cmd.bps"
		WriteLine outfile, cmd

		; Close the file... we can't go sending open files around.
		DebugLog "Closing cmd.bps"
		CloseFile outfile

		; Delete old output -- artifact of when I had some latency issues before.
		; Better safe than sorry
		DebugLog "Checking for (and deleting) old output"
		If FileType("./output.txt") = 1 Then DeleteFile "./output.txt"
		
		; Run blitzcc on the cmd file
		DebugLog "Calling blitzcc to run cmd.bps"
		ExecFile(runbps$)

		; Look for the file--not there? KEEP LOOKING!
		While Not FileType("./output.txt")
			DebugLog "No output file yet"
			Delay 17
		Wend

		; We need to wait until the file is not being written to, so we get the whole output.
		; I couldn't think of anything better yet.
		DebugLog "Waiting for file size to stop changing"
		Local newsize = FileSize("./output.txt")
		Local oldsize = -1
		While oldsize <> newsize
			oldsize = newsize
			newsize = FileSize("./output.txt")

			; The reason it takes half a second to do a print :(
			; You might be able to shorten the delays depending on the machine running this.
			Delay 365
		Wend
		
		; Part of our command was to pipe output into a file, so now we read it
		Local ostream = ReadFile("./output.txt")
		DebugLog "ostream = " + ostream + "   Available: " + FileSize(ostream)
		
		; There's a mess of stuff that prints out when you run blitzcc at the command line
		; The loop goes through the file and when something unusual comes up (errors, your output)
		; it writes it to the stream so we can get it in a minute
		Local expected$ = "BlitzCC"
		While Not Eof(ostream)
			Local outline$ = ReadLine(ostream)
			DebugLog "Output: " + outline
			If (Len(expected) <> 0) And (expected = Left(outline, Len(expected))) Then
			
				; A mutually exclusive list of expected values that gets the interpreter through the header
				If expected = "Executing..." Then expected = ""
				If expected = "Assembling..." Then expected = "Executing..."
				If expected = "Translating..." Then expected = "Assembling..."
				If expected = "Generating..." Then expected = "Translating..."
				If expected = "Parsing..." Then expected = "Generating..."
				If expected = "Compiling" Then expected = "Parsing..."
				If expected = "(C)" Then expected = "Compiling"
				If expected = "BlitzCC" Then expected = "(C)"
				
			Else
				; This is where the output, good or bad, gets sent back
				WriteString interpreter, outline
			EndIf
		Wend
	EndIf
	        
	; Close and delete the output file -- under redundant, see redundant :)
	DebugLog "Closing ostream"
	CloseFile ostream
	DebugLog "Deleting output.txt"
	DeleteFile "./output.txt"

End Function
