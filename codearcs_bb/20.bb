; ID: 20
; Author: marksibly
; Date: 2001-08-21 23:17:32
; Title: BlitzMail
; Description: An example of sending email

t=OpenTCPStream( "smtp.yourisphere",25 )
If Not t RuntimeError( "Error connecting" )
WriteLine t,"HELO BlitzMail"
WriteLine t,"MAIL FROM: <from@fromhost>"
WriteLine t,"RCPT TO: <to@tohost>"
WriteLine t,"DATA"
WriteLine t,"Hello there Mark!"
WriteLine t,"This is from Blitz Mail!"
WriteLine t,""
WriteLine t,"."
WriteLine t,"QUIT"
Print "Done. Press any key...":WaitKey

