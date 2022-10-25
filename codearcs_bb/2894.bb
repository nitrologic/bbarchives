; ID: 2894
; Author: MusicianKool
; Date: 2011-10-21 01:34:55
; Title: ScanCode to ASCII
; Description: ScanCode To ASCII

Dim ScanCode_Array(237,2)
Restore ScanCodesToAscii_Data
While Indx <> 999
	Read Indx
	If indx <> 999 Then
		Read ascii
		Read scn 
		ScanCode_Array(scn,Indx)=ascii
	EndIf
Wend

While Not KeyHit(1)
	a = ScanCode_To_Ascii()
	If a <> 0 Then Print a + "    " + Chr(a)
Wend


Function ScanCode_To_Ascii()
	For ScanCodeCycle = 1 To 237
		If KeyDown(ScanCodeCycle)=True  Then
			If ScancodeCycle <> 42 And scancodecycle <> 54 Then scancode = ScanCodeCycle
		EndIf
	Next
	If KeyDown(42) = True Or KeyDown(54) = True Then 
		ShiftDown = 2
	Else
		ShiftDown = 1
	EndIf
	If shiftdown = 2 Then 
		If scancode =  42  Or scancode = 54 Then Return
	EndIf
	Return ScanCode_Array(scancode,ShiftDown)
End Function

.ScanCodesToAscii_Data
Data 1,27,1
Data 1,96,41
Data 1,49,2
Data 1,50,3
Data 1,51,4
Data 1,52,5
Data 1,53,6
Data 1,54,7
Data 1,55,8
Data 1,56,9
Data 1,57,10
Data 1,48,11
Data 1,45,12
Data 1,61,13
Data 1,8,14
Data 1,3,210
Data 1,1,199
Data 1,5,201
Data 1,6,209
Data 1,2,207
Data 1,4,211
Data 1,9,15
Data 1,113,16
Data 1,119,17
Data 1,101,18
Data 1,114,19
Data 1,116,20
Data 1,121,21
Data 1,117,22
Data 1,105,23
Data 1,111,24
Data 1,112,25
Data 1,91,26
Data 1,93,27
Data 1,92,43
Data 1,97,30
Data 1,115,31
Data 1,100,32
Data 1,102,33
Data 1,103,34
Data 1,104,35
Data 1,106,36
Data 1,107,37
Data 1,108,38
Data 1,59,39
Data 1,39,40
Data 1,13,28
Data 1,122,44
Data 1,120,45
Data 1,99,46
Data 1,118,47
Data 1,98,48
Data 1,110,49
Data 1,109,50
Data 1,44,51
Data 1,46,52
Data 1,47,53
Data 1,32,57
Data 1,31,203
Data 1,29,208
Data 1,30,205
Data 1,28,200
Data 2,126,41
Data 2,33,2
Data 2,64,3
Data 2,35,4
Data 2,36,5
Data 2,37,6
Data 2,94,7
Data 2,38,8
Data 2,42,9
Data 2,40,10
Data 2,41,11
Data 2,95,12
Data 2,43,13
Data 2,81,16
Data 2,87,17
Data 2,69,18
Data 2,82,19
Data 2,84,20
Data 2,89,21
Data 2,85,22
Data 2,73,23
Data 2,79,24
Data 2,80,25
Data 2,123,26
Data 2,125,27
Data 2,124,43
Data 2,9,15
Data 2,65,30
Data 2,83,31
Data 2,68,32
Data 2,70,33
Data 2,71,34
Data 2,72,35
Data 2,74,36
Data 2,75,37
Data 2,76,38
Data 2,58,39
Data 2,34,40
Data 2,90,44
Data 2,88,45
Data 2,67,46
Data 2,86,47
Data 2,66,48
Data 2,78,49
Data 2,77,50
Data 2,60,51
Data 2,62,52
Data 2,63,53
Data 999
