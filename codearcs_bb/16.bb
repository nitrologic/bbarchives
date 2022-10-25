; ID: 16
; Author: marksibly
; Date: 2001-08-16 22:17:07
; Title: BlitzFTP
; Description: A simple FTP client

Graphics 800,600

Color 255,0,64
Print "*****************"
Print "* BlitzFTP V1.0 *"
Print "*****************"

Dim dp(6),args$(3)

Color 0,255,64
ftp$=Input$( "ftp://" ):If ftp$="" Then End

com=OpenTCPStream( ftp$,21 ):If Not com RuntimeError( "Unable to connect" )

Repeat
	;receive reply from FTP
	Color 0,192,255
	Repeat
		Repeat
			ln$=ReadLine$( com )
			n=Eof( com ):If n=1 Then End
			If n RuntimeError( "Stream Error" )
			Print ln$
		Until Len( ln$ )>3
		t=Left$( ln$,3 )
	Until t>=100 And t<600 And Mid$( ln$,4,1 )=" "
	Color 0,255,64

	;update state	
	If t<400	;not an error?
		st=t
	Else		;error! Hack 'n' kludge!
		If file CloseFile file:file=0 
		If dat CloseTCPStream dat:dat=0
		If t=530 Then st=220 Else If st<300 And st<>220 st=200
	EndIf
	
	;act on state
	Select st
	Case 220
		WriteLine com,"USER "+Input$( "Username:" )
	Case 331
		WriteLine com,"PASS "+Input$( "Password:" )
	Case 332
		WriteLine com,"ACCT "+Input$( "Account:" )
	Case 230
		WriteLine com,"TYPE I"	;binary file transfer mode!
	Case 227
		;extra ip and port from (h1,h2,h3,h4,p1,p2)
		i1=Instr( ln$,"(" )
		i2=Instr( ln$,")",i1 )
		pt$=Mid$( ln$,i1+1,i2-i1-1 )+","
		For k=1 To 6
			i=Instr( pt$,"," )
			dp(k)=Left$( pt$,i-1 )
			pt$=Mid$(pt$,i+1)
		Next
		ip$=dp(1)+"."+dp(2)+"."+dp(3)+"."+dp(4)
		port=(dp(5) Shl 8) Or dp(6)
		dat=OpenTCPStream( ip$,port )
		If Not dat RuntimeError "Failed to open data port"
		WriteLine com,dat_com$
	Case 150
		;data transfer...
		If file 
			;file transfer
			If Left$( dat_com$,4 )="STOR"
				rd=file:wt=dat:Write "Uploading "+Mid$( dat_com$,6 )
			Else
				rd=dat:wt=file:Write "Downloading "+Mid$( dat_com$,6 )
			EndIf
			b=CreateBank(16384)
			size=0:time=MilliSecs()
			Repeat
				n=ReadBytes( b,rd,0,16384 )
				WriteBytes b,wt,0,n
				size=size+n
				Write "."
			Until n<>16384
			time=MilliSecs()-time
			secs#=time/1000.0
			Print:Print size+" bytes transferred in "+secs+" seconds."
			FreeBank b:CloseFile file:file=0
		Else
			;just a directory dump...
			While Not Eof( dat )
				Print ReadLine$( dat )
			Wend
		EndIf
		CloseTCPStream dat:dat=0
	Default
		If st<200 Or st>=300 RuntimeError "Fatally confused error!"
		send$=""
		Repeat
			Repeat
				arg$=Trim$( Input$( "]" ) )
			Until arg$<>""
			For n=1 To 3
				i=Instr( arg$," " )
				If i=0 Then args$(n)=arg$:Exit
				args$(n)=Left$(arg$,i-1)
				arg$=Trim$( Mid$(arg$,i+1) )
			Next
			Select Lower$( args$(1) )
			Case "cd"
				If n=2
					send$="CWD "+args$(2)
				Else
					Print "Usage: cd remote_dir"
				EndIf
			Case "lcd"
				If n>2
					Print "Usage: lcd local_dir"
				Else
					If n=2 ChangeDir args$(2)
					Print "Current dir="+CurrentDir$()
				EndIf
			Case "cdup"
				send$="CDUP"
			Case "cls"
				Cls:Locate 0,0
			Case "dir"
				If n=1
					send$="PASV":dat_com$="LIST"
				Else If n=2
					send$="PASV":dat_com$="LIST "+args$(2)
				Else
					Print "Usage: dir [remote_dir]"
				EndIf
			Case "get"
				If n=2
					fi$=Replace$( args$(2),"\","/" )
					While Instr( fi$,"/" )
						fi$=Mid$( fi$,Instr( fi$,"/" )+1 )
					Wend
					file=WriteFile( fi$ )
					If file 
						send$="PASV":dat_com$="RETR "+args$(2)
					Else
						Print "Unable to open file for writing"
					EndIf
				Else
					Print "Usage: get filename"
				EndIf
			Case "put"
				If n=2
					fi$=Replace$( args$(2),"\","/" )
					While Instr( fi$,"/" )
						fi$=Mid$( fi$,Instr( fi$,"/" )+1 )
					Wend
					file=ReadFile( fi$ )
					If file
						send$="PASV":dat_com$="STOR "+args$(2)
					Else
						Print "Unable to open file for reading" 
					EndIf
				Else
					Print "Usage: put filename"
				EndIf
			Case "help"
				Print "Local help:"
				Print "cdup, cls, help, quit, exit"
				Print "cd remote_dir, lcd [local_dir], dir [remote_dir], get remote_file, put remote_file"
				Print "Remote help:"
				send$="HELP"
			Case "shot"
				SaveBuffer FrontBuffer(),"ftpshot.bmp"
			Case "quit","exit"
				send$="QUIT"
			Default:
				Print "Unrecognized command "+Chr$(34)+args$(1)+Chr$(34)+"."
			End Select
		Until send$<>""
		WriteLine com,send$
	End Select
Forever

