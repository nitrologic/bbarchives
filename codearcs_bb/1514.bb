; ID: 1514
; Author: Regular K
; Date: 2005-10-31 21:52:27
; Title: Bubble Sorting
; Description: Simple array bubble sorting

; Note this is made in BlitzPlus, the print commands may not work the same in Blitz3D
length%=5
Dim Array(length%)
Print "==Creating Random List=="
For i%=0 To length%
	Array(i%)=Rand(1,300)
	Print Array(i%)
Next
Print "==Bubble Sorting List=="
For i%=length% To 1 Step -1
	For j%=0 To i%-1
		If Array(j%)>Array(j%+1)
			Temp%=Array(j%)
			Array(j%)=Array(j%+1)
			Array(j%+1)=Temp%
		EndIf
	Next
Next
Time%=MilliSecs()-Start%
Print "Took " +Time%+ " ms to calculate."
Print "==Sort Result=="
For i%=0 To length%
	Print Array(i%)
Next
Print "===="
Repeat
Forever
