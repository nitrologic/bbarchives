; ID: 599
; Author: Nigel Brown
; Date: 2003-02-24 08:05:06
; Title: PortIO Blitz+
; Description: IO from any port using userlib

;the declaration...

.lib "dlportio.dll"

DLPortReadPortUchar%( port% )
DLPortReadPortUshort%( port% )
DLPortReadPortUlong%( port% )

DlPortReadPortBufferUchar( port%, buffer*, count% )
DlPortReadPortBufferUshort( port%, buffer*, count% )
DlPortReadPortBufferUlong( port%, buffer*, count% )

DlPortWritePortUchar( port%, value% )
DlPortWritePortUshort( port%, value% )
DlPortWritePortUlong( port%, value% )

DlPortWritePortBufferUchar( port%, buffer*, count% )
DlPortWritePortBufferUshort( port%, buffer*, count% )
DlPortWritePortBufferUlong( port%, buffer*, count% )

; the blitz code.

; Direct Port access from Blitz Basic
; Check this for more info
; http://www.fapo.com/files/ecp_reg.pdf

Global	LPT1		= $378

Global	DAT			= $0
Global	DSR			= $1
Global	DCR			= $2

Graphics 800,600,16,2
SetBuffer BackBuffer()

; Set the direction bit of the device control register,
; make port direction (read).
DlPortWritePortUchar( LPT1 + DCR, $20 )

;--------------------------------------------------
Repeat
;--------------------------------------------------
	; Read from data register
	Print DlPortReadPortUchar( LPT1 + DAT )
	
Until KeyDown(1)

End

for more info, and the required dll. Check the code download @
[a http://www.nigelibrown.pwp.blueyonder.co.uk/blitz/userlibs/index.htm]Click Here![/a]
