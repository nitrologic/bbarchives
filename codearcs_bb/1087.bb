; ID: 1087
; Author: Vertex
; Date: 2004-06-15 06:42:15
; Title: COM-Port
; Description: Show how to receive and transmit any datas with the COM-Port

Const  parityEnable        = False ; flag if parity is enabled
Const  protocol            = 0     ; stores protocol type
Const  dataBits            = 8     ; stores number if data bits
Const  parityBit           = 0     ; stores type of parity bit
Const  stopBits            = 0     ; stores number of stop bits
Global timeouts            = CreateBank(20)

Const INVALID_HANDLE_VALUE = -1

Const  GENERIC_READ        = $80000000
Const  GENERIC_WRITE       = $40000000
Const  OPEN_EXISTING       = 3

Const  CBR_110             = 110
Const  CBR_300             = 300
Const  CBR_600             = 600
Const  CBR_1200            = 1200
Const  CBR_2400            = 2400
Const  CBR_4800            = 4800
Const  CBR_9600            = 9600
Const  CBR_14400           = 14400
Const  CBR_19200           = 19200
Const  CBR_38400           = 38400
Const  CBR_56000           = 56000
Const  CBR_57600           = 57600
Const  CBR_115200          = 115200

Const  DTR_CONTROL_DISABLE = 0
Const  DTR_CONTROL_ENABLE  = 1
Const  RTS_CONTROL_DISABLE = 0
Const  RTS_CONTROL_ENABLE  = 1
Const  EV_TXEMPTY          = 4

Const  SETRTS              = 3
Const  CLRRTS              = 4

; Example ---------------------------------------------------------------------------
Print "Open COM1 with 9600 baudrate ..."
hCom = openComport(1, 9600)
If hCom <> 0 Then
	Print "   Succesfully"
Else
	Print "   Error"
	WaitKey : End
EndIf


; Transmit:
buffer = CreateBank(6)
PokeByte buffer, 0, Asc("H")
PokeByte buffer, 1, Asc("e")
PokeByte buffer, 2, Asc("l")
PokeByte buffer, 3, Asc("l")
PokeByte buffer, 4, Asc("o")
PokeByte buffer, 5, 0
Print "Sending "+Chr$(34)+"Hello"+Chr$(34)+" ..."
If writeComPort(hCom, 6, buffer) Then
	Print "   Succesfully"
Else
	Print "   Error"
	closeComport(hCom)
	WaitKey : End
EndIf
FreeBank buffer

; Receive
buffer = CreateBank(1024)
bytes  = CreateBank(4)
Print "Receiving datas ..."
If readComport(hCom, bytes, 1024, buffer, 10000) Then ; 10 Sekunden timeout
	Print "   Succesfully"
	Print "   Number of receiving bytes "+PeekInt(bytes, 0)
	; Datas are in the bank buffer
Else
	Print "   Error"
EndIf

; Close and end
closeComport(hCom)
WaitKey : End
; -----------------------------------------------------------------------------------

Function openComport(comport, baudRate)
   Local dcbBaudRate, driverHandle, dcb
   
   If comport > 255 Or comport < 0 Then
      Return False
   EndIf
   
   Select baudRate
      Case 110
         dcbBaudRate = CBR_110
      Case 300
         dcbBaudRate = CBR_300
      Case 600
         dcbBaudRate = CBR_600
      Case 1200
         dcbBaudRate = CBR_1200
      Case 2400
         dcbBaudRate = CBR_2400
      Case 4800
         dcbBaudRate = CBR_4800
      Case 9600
         dcbBaudRate = CBR_9600
      Case 14400
         dcbBaudRate = CBR_14400
      Case 19200
         dcbBaudRate = CBR_19200
      Case 38400
         dcbBaudRate = CBR_38400
      Case 56000
         dcbBaudRate = CBR_56000
      Case 57600
         dcbBaudRate = CBR_57600
      Case 115200
         dcbBaudRate = CBR_115200
      Default
         Return False
   End Select
   
   driverHandle = apiCreateFile("COM"+comport, GENERIC_READ Or GENERIC_WRITE, 0, 0, OPEN_EXISTING, 0, 0)
   If driverHandle = INVALID_HANDLE_VALUE Then
      Return INVALID_HANDLE_VALUE
   Else
      apiSetupComm(driverHandle, 1024, 1024)
      dcb = CreateBank(80)
      PokeInt dcb, 00, 80                         ; sizeof(DCB)
      PokeInt dcb, 04, dcbBaudRate                ; current baud rate
      PokeInt dcb, 08, 1                          ; binary mode, no EOF check
      If parityEnable = True Then
         PokeInt dcb, 12, 1                       ; enable parity checking
      Else
         PokeInt dcb, 12, 0                       ; disable parity checking
      EndIf
      If protocol = 0 Then
         PokeInt dcb, 24, DTR_CONTROL_ENABLE      ; DTR flow control type
      Else
         PokeInt dcb, 24, DTR_CONTROL_DISABLE     ; DTR flow control type
      EndIf
      PokeInt dcb, 28, False                      ; DSR sensitivity
      If protocol = 1 Then
         PokeInt dcb, 52, RTS_CONTROL_ENABLE      ; RTS flow control
      Else
         PokeInt dcb, 52, RTS_CONTROL_DISABLE     ; RTS flow control
      EndIf
      PokeInt  dcb, 60, 17                        ; reserved
      PokeByte dcb, 70, dataBits                  ; number of bits/byte, 4-8
      PokeByte dcb, 71, parityBit                 ; 0-4=no, odd, even, mark, space
      PokeByte dcb, 72, stopBits                  ; 0,1,2 = 1, 1.5, 2
      
      apiSetCommState(driverHandle, dcb)
      apiSetCommMask(driverHandle, EV_TXEMPTY)
   EndIf
   
   FreeBank dcb
   Return driverHandle
End Function

Function closeComport(driverHandle)
   If driverHandle = INVALID_HANDLE_VALUE Or driverHandle = 0 Then
      Return False
   Else
      Return apiCloseHandle(driverHandle)
   EndIf
End Function

Function writeComport(driverHandle, numBytes, buffer)
   Local state, temp

   If driverHandle = INVALID_HANDLE_VALUE Or driverHandle = 0 Then
      Return False
   EndIf
   
   apiEscapeCommFunction(driverHandle, SETRTS)
   
   temp = CreateBank(4)
   state = apiWriteFile(driverHandle, buffer, numBytes, temp, 0)
   
   apiEscapeCommFunction(driverHandle, CLRRTS)
   
   FreeBank temp
   Return state
End Function

Function readComport(driverHandle, bytesRead, bufferSize, buffer, tOut)
	Local comErrors, comStat
	
   If driverHandle = INVALID_HANDLE_VALUE Or driverHandle = 0 Then
      Return False
   EndIf
	
	PokeInt timeouts, 00, 0
	PokeInt timeouts, 04, 0
	PokeInt timeouts, 08, tOut
	PokeInt timeouts, 12, 0
	apiSetCommTimeouts(driverHandle, timeouts)
	
	comErrors = CreateBank(4)
	comStat   = CreateBank(10)
	apiClearCommError(driverHandle, comErrors, comStat)
	
	apiReadFile(driverHandle, buffer, bufferSize, bytesRead, 0)
	
	FreeBank comErrors
	FreeBank comStat
	Return True
End Function
