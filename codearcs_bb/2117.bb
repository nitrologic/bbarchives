; ID: 2117
; Author: XtremeCoder
; Date: 2007-10-07 00:29:42
; Title: TCP - Sending a File
; Description: Sending a File from Server to Client

;Server Code
Graphics 640,480,16,2
AppTitle "Server Example"
filetosend$=Input("File:")
file1=OpenFile(filetosend$)
FileSize1=FileSize(filetosend$)
Print "File Size:"+filetosend$+" "+FileSize1
server=CreateTCPServer(7000)
a=1
Repeat
stream=AcceptTCPStream(server)
	If stream Then
	If a=1 Then
	WriteLine(stream, filetosend$)
	WriteLine(stream, filesize1)
	a=0
	EndIf
		While Not Eof (file1)
			Data1=ReadByte(file1)
			WriteLine(stream, Data1)
		Wend
	EndIf
Forever
;End of Server Code






;Client Code
AppTitle "Client Example"
ip$=Input("IP:")
port=Input("PORT:")
strmGame=OpenTCPStream(ip$,port)

If strmGame<>0 Then 
Print "Client Connected successfully."
Else
Print "Server failed to connect."
WaitKey 
End
End If
filetoget$=ReadLine(strmgame)
file=WriteFile("1"+filetoget$)
filesize2#=ReadLine(strmgame)
Print "Downloading 1"+filetoget$+" from "+ip$+"("+port+")"
Print "Size: " + filesize2# + " Bytes"
Repeat
	Data1=ReadLine( strmGame )
	a#=a#+1
	printpercent#=a#/filesize2#
	printpercent#=printpercent#*100
	AppTitle printpercent#+"%"
	WriteByte(file, data1)
Until a# = filesize2#
;End of Client Code
