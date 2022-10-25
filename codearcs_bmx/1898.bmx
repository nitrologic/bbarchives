; ID: 1898
; Author: EdzUp[GD]
; Date: 2007-01-14 06:02:06
; Title: BlitzMax ConvertIP function
; Description: converts a string IP to integer equivalent

Function ConvertIP:Int( sIPAddress:String )	'pass IP as a String e.g. "127.0.0.1"

	'VARS
	Local iIP:Int
	Local iDotPos:Int = 0
	Local iOldDotPos:Int = 0
	Local strTemp:String
	Local Counter:Int = 3

	'MAIN
	While Counter > 0 
		iOldDotPos = iDotPos
		iDotPos = Instr(sIPAddress, ".", iOldDotPos+1)
		strTemp = Mid(sIPAddress,iOldDotPos + 1, (iDotPos - iOldDotPos)-1)
		iIP = iIP + (Int( strTemp ) Shl (Counter * 8))
		Counter = Counter - 1
	Wend

	strTemp = Right(sIPAddress, (Len(sIPAddress) - iDotPos) )
	iIP = iIP + (Int( strTemp ) Shl 0)	

	Return iIP 
End Function
