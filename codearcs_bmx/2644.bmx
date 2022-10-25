; ID: 2644
; Author: Yeshu777
; Date: 2010-01-21 18:12:31
; Title: BMax Serial COM Port - Linux
; Description: Send & Recieve Strings Easily

'*************************************************************
'* Description... Simple Serial COM String Handler
'* Date.......... 20.1.10
'* Author........ Yeshu777
'*************************************************************

'*************************************************************
' * Vars
'*************************************************************

Global	TXCom:TStream	  ' Transmit Stream 

Global	bytes_rcvd         ' New Bytes Recieved.
Global	prev_bytes_rcvd	  ' Previous Bytes Recieved

Global   log_file:TStream

'*************************************************************
'* Description... Init The Comms
'* Date.......... 20.1.10
'* Author........ Yeshu777
'*************************************************************

Function	InitSerialComms( )

	TXCom = WriteStream("/dev/ttyS0");
	
	prev_bytes_rcvd = 0;

	log_file = OpenFile("/var/tmp/ttyS0.log")

	bytes_rcvd = StreamSize(log_file)'

	CloseStream(log_file)
	
End Function

'*************************************************************
'* Description... Close The TX Stream
'* Date.......... 20.1.10
'* Author........ Yeshu777
'*************************************************************

Function	EndSerialComms()

	CloseStream(TXCom)'

End Function

'*************************************************************
'* Description... Command Handler (In Main Game Loop)
'* Date.......... 20.1.10
'* Author........ Yeshu777
'*************************************************************

Function	CommandHandler()

	Local	cmd$'
	
	cmd$ = RxCommand$()'
	
	If(Len(cmd$) <> 0) Then
	
		If(Instr(cmd$, "TEST", 0) > 0) Then

                         'Do what you need here.

			DebugLog("TEST Recieved")
			SendCommand("ack")
	
		End If

	End If

End Function

'*************************************************************
'* Description... Send A String
'* Date.......... 20.1.10
'* Author........ Yeshu777
'*************************************************************

Function	SendCommand( buf$ )
	
	WriteString(TXCom, buf$)'
	FlushStream(TXCom)'
	
End Function
	
'*************************************************************
'* Description... Recieve a String via the log file.
'* Date.......... 20.1.10
'* Author........ Yeshu777
'*************************************************************

Function	RxCommand$()

	Local	buf$
	
	log_file = OpenFile("/var/tmp/ttyS0.log")
		
	If(log_file) Then

	        bytes_rcvd = StreamSize(log_file)'

		If(bytes_rcvd > prev_bytes_rcvd) Then
  
                      SeekStream(log_file, prev_bytes_rcvd)

		     buf$ = ReadLine(log_file)

		     prev_bytes_rcvd = bytes_rcvd'

                     CloseStream(log_file)

		    Return(buf$)

              End If
		
              CloseStream(log_file)

	End If
							
End Function

'*************************************************************
