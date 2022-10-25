; ID: 1487
; Author: Wayne
; Date: 2005-10-11 19:32:22
; Title: I/O Ports, Parallel Port
; Description: Allows Easy I/O Port Control

; Parallel Port I/O made easy.

; Inpout32.dll for WIN 98/NT/2000/XP
; Get binary, source, and other info here:

; http://www.logix4u.net/inpout32.htm

-----------------------------------------------
InpOut32.decls:

.lib "inpout32.dll"

Inp32%( port% )
Out32 ( port%, value% )

-----------------------------------------------

; B3D Parallel Port I/O

Global	Lpt1= $378

Graphics 800,600,16,2
SetBuffer BackBuffer()

; Output data
Out32(Lpt1,1)

; Read port status
DebugLog Bin$(Inp32(Lpt1))
	
While Not KeyHit(1)
Wend

End
