; ID: 1883
; Author: Petron
; Date: 2006-12-19 10:05:47
; Title: Simple System Commands
; Description: Read the title

window=CreateWindow( "System",0,0,192,56,0,1 )
button=CreateButton( "Turn Off",0,0,64,24,window )
buttona=CreateButton( "Log Off",64,0,64,24,window )
buttonb=CreateButton( "Restart",128,0,64,24,window )
While WaitEvent()<>$803
If EventID()=$401
If EventSource()=button	Then ExecFile("Shutdown.exe -s") 
If EventSource()=buttona Then ExecFile("Shutdown.exe -l")
If EventSource()=buttonb Then ExecFile("Shutdown.exe -r")
EndIf
Wend
End
