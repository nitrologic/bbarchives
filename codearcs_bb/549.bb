; ID: 549
; Author: Klaas
; Date: 2003-01-13 11:01:01
; Title: Getting Drive Info with KERNEL32.DLL
; Description: This short proggy lists all used driveletters ...

wert=CallDLL ("kernel32.dll", "GetLogicalDrives")
For i=0 To 25
	ShiftedData=wert Shr i
	BitValue = ShiftedData And 1;
	If BitValue<>0
		Print Chr(65+i)+":\\"
	EndIf
Next
WaitKey
End
