; ID: 2952
; Author: ozzi789
; Date: 2012-06-22 03:52:05
; Title: HTTP 1.1 GET - Download a File
; Description: A simple function to download any desired file from a website via a TCP connection and a HTTP 1.1 GET request

target_path$="C:\CHANGEME.png"

Print "Downloading"
Print "Return code: "+download_file("http://www.blitzforum.de/header/header.png",target_path$)
ExecFile (target_path$)

WaitKey 


;If -1 is returned the TCP-Connection could not be established
;If -2 is returned the TCP-Connection got aborted when trying to send the GET-Request
;If -3 is returned the target file could not be written - check if it is a valid path & you have write rights
;If  1 is returned the download was successful

Function download_file(source$,target$)


	Local max_download_bytes = 1024 , host$, file$
	
	host$=splitt_fqdn(source$,1)
	file$=splitt_fqdn(source$,2)

	tcp=OpenTCPStream(host$,80) ;seite öffnen
	
	If Not tcp Then Return -1
	
	
	WriteLine tcp,"GET "+file$+" HTTP/1.1"+Chr(13);Datei öffnen
	WriteLine tcp,"Host: "+host$+Chr(13);Seiten host festlegen
	WriteLine tcp,"Connection: close"+Chr(13)
	WriteLine tcp,"User-Agent: bb-dwnldr"+Chr(13)
	WriteLine tcp,Chr$(10)
	
	If Eof(tcp) Then Return -2
	
	
	Repeat
		response$=ReadLine(tcp)
	Until response$=""
	
	
	Delay(2)
	
	
	file=WriteFile(target$)
	If file=0 Then Return -3
	
	buffer = CreateBank(max_download_bytes)
	
	While Not Eof(tcp)
	   Size = ReadBytes(buffer, tcp, 0, max_download_bytes)
	   WriteBytes(buffer, file, 0, Size)
	Wend 
	
	CloseFile(file)
	
	Return 1
	
End Function 



Function splitt_fqdn$(url$,part)
	Local pos=0
	url$=Lower(url$)

	If Left(url$,7)="http://" Then pos=7
	If Left(url$,8)="https://"  Then pos=8
	
	slash_pos=Instr(url$,"/",pos+1)
	If part=1
		Return Mid(url$,pos+1,slash_pos-pos-1)
	ElseIf part=2
		Return Mid(url$,slash_pos)
	Else
		Return "Invalid part parameter!"
	EndIf 
End Function
