; ID: 2165
; Author: Ked
; Date: 2007-12-07 00:33:40
; Title: MaxMail
; Description: Sends an email in BlitzMax

Strict
Import BRL.BASIC

Global from_mail:String = "YOUR EMAIL HERE"
Global from_name:String = "YOUR NAME HERE"
Global to_mail:String = "THEIR EMAIL HERE"
Global to_name:String = "THEIR NAME HERE"
Global email_subject:String = "From MaxMail"
Global email_message:String = ..
"Hello!~r~n~r~nWhat do you think of this very nice email? I~r~n" + ..
"think it is quite swell!~r~n~r~nGoodbye!"

Global email_mailhost:String = "MAIL HOST HERE"
Global email_mailport:Int = 25

Print "Connecting..."
Global mailsocket:TSocket = CreateTCPSocket() 
If Not ConnectSocket(mailsocket, HostIp(email_mailhost), email_mailport) 
	Print "Unable to connect!"
	CloseSocket mailsocket
	End
EndIf
Print "Socket connected!"
Global t:TSocketStream=CreateSocketStream(mailsocket)

Global msg:String = ""

Rem
EMAIL PART BEGINS!!!
EndRem

msg = t.ReadLine() 
Print msg

t.WriteLine("HELO MaxMailer") 
msg = t.ReadLine() 
Print msg

t.WriteLine("MAIL FROM: <" + from_mail + ">") 
msg = t.ReadLine() 
Print msg

t.WriteLine("RCPT TO: <" + to_mail + ">") 
msg = t.ReadLine() 
Print msg

t.WriteLine("DATA") 
msg = t.ReadLine() 
Print msg

t.WriteLine("Date: " + CurrentDate:String() ) 
t.WriteLine("From: " + from_name + " <" + from_mail + ">") 
t.WriteLine("To: " + to_name + " <" + to_mail + ">") 
t.WriteLine("Subject: " + email_subject) 
t.WriteLine("X-Mailer: MaxMail") 
t.WriteLine("")

t.WriteLine(email_message) 

t.WriteLine("") 
t.WriteLine("") 
t.WriteLine(".") 
msg = t.ReadLine() 
Print msg

t.WriteLine("QUIT") 
msg = t.ReadLine() 
Print msg

CloseStream t
CloseSocket mailsocket
Print "Mailed!"
End
