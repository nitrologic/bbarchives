; ID: 872
; Author: Perturbatio
; Date: 2004-01-03 10:16:37
; Title: Dotted IP to Int Function
; Description: Converts an IP in the form xxx.xxx.xxx.xxx to an IP Integer

IP = DottedIPToInt("104.154.21.1")

Print IP

Print DottedIP(IP)

WaitKey()


End


;;;;;;;;;;;;;;;;;;;;;;;;;;
; FUNCTION DottedIPToInt ;
;;;;;;;;;;;;;;;;;;;;;;;;;;

Function DottedIPToInt%(sIPAddress$);pass IP as a string e.g. "127.0.0.1"

;VARS
	Local iIP%
	Local iDotPos% = 0
	Local iOldDotPos% = 0
	Local strTemp$
	Local Counter = 3

;MAIN
While Counter > 0 
	iOldDotPos = iDotPos
	iDotPos = Instr(sIPAddress, ".", iOldDotPos+1)
	strTemp = Mid(sIPAddress,iOldDotPos + 1, (iDotPos - iOldDotPos)-1)
	iIP = iIP + (strTemp Shl (Counter * 8))
	Counter = Counter - 1
Wend

strTemp = Right(sIPAddress, (Len(sIPAddress) - iDotPos) )
iIP = iIP + (strTemp Shl 0)	

Return iIP 
End Function
