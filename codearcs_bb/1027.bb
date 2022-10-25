; ID: 1027
; Author: Taiphoz
; Date: 2004-05-11 17:40:22
; Title: Relay Hunter (Hacking)
; Description: This is a Proof of concept Code, for INFO use only.

;
;
;	SendMail Relay Scanner.
;
;

Graphics3D 200,100,16,2
SetBuffer BackBuffer()

SeedRnd MilliSecs()

Global SendMail
Global l$,i$,IP$,com

Global Logfile
Global logfile_path$="output.txt"
Global Time_Delay%=2000
Global Counter%=0


logfile=WriteFile(logfile_path$)
WriteLine logfile,"New Scan (DATE HERE)"
WriteLine logfile," "
CloseFile logfile
logfile=OpenFile(logfile_path$)

TCPTimeouts 12,12
	
Repeat
	
	Print "Scanning ";:Delay 20


	;Get a Random IP
	Local octet
	octet=Rnd(20,255)
	ip$=Str(octet)
	For looper=1 To 3
		octet=Rnd(20,255)
		ip$=ip$+"."+Str(octet)
	Next
	
	
		
	sendmail = OpenTCPStream(IP$,25)
	
	If sendmail<>0
		logdata("Scanning [ "+IP$+" ] - Port Open")
		logdata(" ")
		logdata("******************************************************")
		logdata("*  "+IP$)
		logdata("******************************************************")
		
		Read_Incoming()	
		;Send the Mail
		WriteLine sendmail , "HELO SMSCAN"
		Read_Incoming()	
		WriteLine sendmail , "MAIL FROM: sendmail@test.com"
		Read_Incoming()
		WriteLine sendmail , "RCPT TO: your@email.uk.net"
		Read_Incoming()
		
		test%=Instr(l$,"denied",1)
		If test%<>0
			logdata("******************************************************")
			logdata("* DENIED")
			logdata("******************************************************")	
			
		Else
			logdata("******************************************************")
			logdata("*                                       POSSIBLE RELAY")
			logdata("******************************************************")			
		End If
		
		WriteLine sendmail , "DATA"
		WriteLine sendmail , "HELLO We found an Open Relay!!!!"
		WriteLine sendmail , IP$
		WriteLine sendmail , "."	
	
		CloseTCPStream(sendmail)
					
	Else
		logdata("Scanning [ "+IP$+" ] - Port Closed")
	End If
	;hold(600)
	counter=counter+1
	
	
	

	
Until KeyDown(1) Or counter=1000
CloseFile logfile



Function hold(a%)
	Local c%
	Repeat
		c=c+1
	Until (c%=a%) Or KeyDown(1)
	 
End Function

Function logdata(info$)
	DebugLog info$
	WriteLine logfile,info$
End Function

Function Read_Incoming()
	l$ = ReadLine$(sendmail)
	logdata(l$)
End Function
