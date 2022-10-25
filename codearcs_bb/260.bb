; ID: 260
; Author: dendavid
; Date: 2002-03-06 14:19:05
; Title: HTTP Server Example
; Description: HTTP Server

;HTTP Server by Daniel Kies www.miamisoft.net info@newhome.lu

Graphics 200,20,16,2
AppTitle "MelcomHTTP Server"
Global errorfile$="\error.htm", basedir$="www"
Global tcp = CreateTCPServer(80)
If Not tcp Then RuntimeError "Cant create server on port 80!"

While Not KeyHit(1)
client = AcceptTCPStream(tcp)
If client
in$=ReadLine$(client)
If Left$(in$,4)="GET "
in$=Right$(in$, Len(in$)-4)
in$=Left$(in$, Len(in$)-9)
in$=Replace$(in$,"/","\")
If fileexist(in$)
send(client,in$)
Else
If fileexist(in$+"index.html")
send(client,in$+"index.html")
ElseIf fileexist(in$+"index.htm")
send(client,in$+"index.htm")
ElseIf fileexist(in$+"index.shtml")
send(client,in$+"index.shtml")
ElseIf fileexist(in$+"start.html")
send(client,in$+"start.html")
ElseIf fileexist(in$+"start.htm")
send(client,in$+"start.htm")
ElseIf fileexist(in$+"start.shtml")
send(client,in$+"start.shtml")
ElseIf fileexist(in$+"default.html")
send(client,in$+"default.html")
ElseIf fileexist(in$+"default.htm")
send(client,in$+"default.htm")
ElseIf fileexist(in$+"default.shtml")
send(client,in$+"default.shtml")
ElseIf fileexist(in$+"welcome.html")
send(client,in$+"welcome.html")
ElseIf fileexist(in$+"welcome.htm")
send(client,in$+"welcome.htm")
ElseIf fileexist(in$+"welcome.shtml")
send(client,in$+"welcome.shtml")
Else
send(client,errorfile$)
EndIf
EndIf
EndIf
EndIf
Wend
End

Function send(an,was$)
WriteLine an, "HTTP/1.0 200 Ok"
WriteLine an, "Server: MelcomVT"
If Right$(wa$,5)=".html" Or Right$(wa$,4)=".htm" Or Right$(wa$,6)=".shtml" Or Right$(wa$,4)=".txt"
WriteLine an, "Content-Type: Text/html"
EndIf
If Right$(wa$,4)=".jpg" Or Right$(wa$,4)=".gif" Or Right$(wa$,4)=".bmp" Or Right$(wa$,4)=".tif"
WriteLine an, "Content-Type: image/gif"
EndIf
If Right$(wa$,4)=".exe" Or Right$(wa$,4)=".zip" Or Right$(wa$,4)=".rar" Or Right$(wa$,4)=".dat"
WriteLine an, "Content-Type: */*"
EndIf
If Right$(wa$,4)=".avi" Or Right$(wa$,5)=".mpeg" Or Right$(wa$,4)=".mpg"
WriteLine an, "Content-Type: video/mpeg"
EndIf
If Right$(wa$,4)=".mid" Or Right$(wa$,4)=".wav" Or Right$(wa$,4)=".mp3"
WriteLine an, "Content-Type: audio/mpeg"
EndIf
WriteLine an, "Content-Length: "+FileSize(basedir$+was$)
WriteLine an, "Last-Modified: Tue, 05 Mar 2002 "+CurrentTime$()+" GMT"
WriteLine an,""
bank=CreateBank(FileSize(basedir$+was$))
la = ReadFile(basedir$+was$)
ReadBytes bank, la, 0, FileSize(basedir$+was$)
WriteBytes bank, an, 0, FileSize(basedir$+was$)
WriteLine an,Chr$(10)
End Function

Function fileexist(was$)
test = ReadFile(basedir$+was$)
If Not test
Return False
Else
CloseFile test
Return True
EndIf
End Function
