; ID: 2866
; Author: Mahan
; Date: 2011-07-03 13:15:18
; Title: Using Membase (or Memcached) from Blitz+ (or Blitz3D)
; Description: Membase and Memcached interface

;---------------------------------------------------------------------
; Basic MemBase/MemCached API for Blitz3D / BlitzPlus
;
; Mattias Hansson (MaHan) - 2011
; 

Type TMembaseConnection
	Field ipaddr$
	Field port%
	Field tcpStream%
	Field anticipatedRecvBytes% ; 0 = we are not waiting for data
	Field tempBank% 			; used when receiving strings
	Field inRequest% 			; Indicates if this connection is currently receiving data from a request
End Type


Function paramNoFromString$(s$, paramNo%, separator$=" ")
	Local currOffset% = 0
	Local paramCount% = 1
	
	Repeat
		Local newOffset = Instr(s, separator, currOffset + 1)
		If newOffset = 0 Then newOffset = Len(s) + 1
		If paramNo = paramCount
			Return Mid(s, currOffset+1, (newOffset - currOffset) -1)
		EndIf
		currOffset = newOffset
		paramCount = paramCount+1
	Until currOffset >= Len(s)
	Return ""
End Function


Function MB_Create.TMembaseConnection(ip$, port% = 11211)
	Local mb.TMembaseConnection = New TMembaseConnection
	mb\ipaddr = ip
	mb\port = port
	mb\tcpStream = OpenTCPStream(mb\ipaddr, mb\port)
	Return mb
End Function

Function MB_Free(mb.TMembaseConnection)
	If mb\tempBank Then FreeBank(mb\tempBank)
	If mb\tcpStream Then CloseTCPStream(mb\tcpStream)
	Delete mb
End Function

Function MB_Set(mb.TMembaseConnection, key$, bankId%)
	MB_internal_clearBuffer(mb)
	WriteLine(mb\tcpStream, "set " + key + " 0 0 " + BankSize(bankId))
	WriteBytes(bankId, mb\tcpStream, 0, BankSize(bankId)-1)
	WriteLine(mb\tcpStream, "")
End Function

Function MB_SetStr(mb.TMembaseConnection, key$, value$)
	MB_internal_clearBuffer(mb)
	WriteLine(mb\tcpStream, "set " + key + " 0 0 " + Len(value))
	WriteLine(mb\tcpStream, value)
End Function

; Keys might be a single key or several separated with whitespace
Function MB_Request(mb.TMembaseConnection, keys$)
	MB_internal_clearBuffer(mb)
	WriteLine(mb\tcpStream, "get " + keys)
	mb\inRequest = True
End Function	

Function MB_Delete(mb.TMembaseConnection, key$)
	MB_internal_clearBuffer(mb)
	WriteLine(mb\tcpStream, "delete " + key)
End Function	

Function MB_Avail(mb.TMembaseConnection)
	Local result = False
	If Not mb\anticipatedRecvBytes Then
		If Not Eof(mb\tcpStream)
			Local answer$=ReadLine(mb\tcpStream)
			
			Select paramNoFromString(answer, 1)
				Case "VALUE"
					mb\anticipatedRecvBytes = Int(paramNoFromString(answer, 4))
					;Print "Set anticipated bytes: " + mb\anticipatedRecvBytes
				Case "END"
					;Print "Ended request"
					mb\inRequest = False
			End Select
			
		EndIf
	EndIf
	;Print "ReadAvail() now: " + ReadAvail(mb\tcpStream)
	Return (mb\anticipatedRecvBytes > 0) And (ReadAvail(mb\tcpStream) >= mb\anticipatedRecvBytes + 2) ; 2 for CRLF
End Function

Function MB_Get(mb.TMembaseConnection, bankId%)
	If mb\anticipatedRecvBytes = 0 Then RuntimeError "MB_Get() - not waiting for answer. Try MB_Requst first?"
	If mb\anticipatedRecvBytes > BankSize(bankId) Then RuntimeError "MB_Get() - trying to stuff to much into passed bank."
	If Not MB_Avail(mb) Then RuntimeError "MB_Get() - Requested value for key is not ready. Try waiting for positive MB_Avail() first?"
	ReadBytes(bankId, mb\tcpStream, 0, mb\anticipatedRecvBytes)
	; remove ending CRLF
	ReadByte(mb\tcpStream)
	ReadByte(mb\tcpStream)
	mb\anticipatedRecvBytes = 0
End Function

Function MB_GetStr$(mb.TMembaseConnection)
	If mb\anticipatedRecvBytes = 0 Then RuntimeError "MB_GetStr$() - not waiting for answer. Try MB_Requst first?"
	If Not mb\tempBank Then mb\tempBank = CreateBank(mb\anticipatedRecvBytes)
	If BankSize(mb\tempBank) < mb\anticipatedRecvBytes Then ResizeBank(mb\tempBank, mb\anticipatedRecvBytes)
	
	Local tempRecvBytes% = mb\anticipatedRecvBytes
	MB_Get(mb, mb\tempBank)
	
	Local i
	Local s$
	For i = 0 To tempRecvBytes-1
		s = s + Chr(PeekByte(mb\tempBank, i))
	Next
	Return s
End Function

Function MB_internal_clearBuffer(mb.TMembaseConnection)
	While ReadAvail(mb\tcpStream)
		ReadLine(mb\tcpStream)
	Wend
End Function
