; ID: 1383
; Author: Vertex
; Date: 2005-05-23 16:45:14
; Title: COM-Port II
; Description: now it works!

Const GENERIC_READ          = $80000000
Const GENERIC_WRITE         = $40000000
Const OPEN_EXISTING         = 3
Const FILE_ATTRIBUTE_NORMAL = $80
Const INVALID_HANDLE_VALUE  = -1

Function OpenCommPort%(Port%)
	Local File%

	If (Port < 0) Or (Port > 255) Then Return -1
	
	; Open CommPort
	File% = apiCreateFile("COM"+Port%, GENERIC_READ Or GENERIC_WRITE, 0, 0, OPEN_EXISTING, 0, 0)
	If File% = INVALID_HANDLE_VALUE Then
		Return -1
	Else
		Return File%
	EndIf
End Function

Function CloseCommPort%(File%)
	Return apiCloseHandle(File%)
End Function

Function WriteComm%(File%, Buffer%, Size%)
	Local Count%, Count2%

	If Size% > BankSize(Buffer%) Then Return 0
	
	Count% = CreateBank(4)
	
	If apiWriteFile(File%, Buffer%, Size%, Count%, 0) = 0 Then
		FreeBank Count%
		Return 0
	Else
		Count2% = PeekInt(Count%, 0)
		FreeBank Count%
		Return Count2%
	EndIf
End Function

Function ReadComm%(File%, Buffer%, Size%)
	Local Count%, Count2%
	
	If Size% > BankSize(Buffer%) Then Return 0
	
	Count% = CreateBank(4)
	
	If apiReadFile(File%, Buffer%, Size%, Count%, 0) = 0 Then
		FreeBank Count%
		Return 0
	Else
		Count2% = PeekInt(Count%, 0)
		FreeBank Count%
		Return Count2%
	EndIf
End Function

Function SetComm%(File%, Settings$)
	Local DCB%
   
	DCB% = CreateBank(28)
   
	; Get States
	If apiGetCommState(File%, DCB) = 0 Then
		FreeBank DCB%
		Return False
	ElseIf PeekInt(DCB%, 0) <> 28
		FreeBank DCB%
		Return False
	EndIf
	
	; Build DCB
	If apiBuildCommDCB(Settings$, DCB%) = 0 Then
		FreeBank DCB%
		Return False
	EndIf
   
	; Set States
	If apiSetCommState(File%, DCB%) = 0 Then
		FreeBank DCB%
		Return False
	Else
		FreeBank DCB%
		Return True
	EndIf
End Function 

Function SetCommTimeouts%(File%, ReadTime%, WriteTime%)
	Local TimeOuts%
	
	; Get timeouts
	Timeouts% = CreateBank(40)
	If apiGetCommTimeouts(File%, Timeouts%) = 0 Then
		FreeBank Timeouts
		Return False
	EndIf
	
	PokeInt Timeouts%,  8, ReadTime%  ; ReadTotalTimeoutConstant
	PokeInt Timeouts%, 16, WriteTime% ; WriteTotalTimeoutConstant
	
	; Set Timeouts
	If apiSetCommTimeouts(File%, Timeouts%) = 0 Then
		FreeBank Timeouts
		Return False
	Else
		FreeBank Timeouts
		Return True
	EndIf
End Function
