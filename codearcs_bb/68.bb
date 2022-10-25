; ID: 68
; Author: Milky Joe
; Date: 2002-08-11 14:52:33
; Title: INI file functions
; Description: Read from and Write to INI files

; Title:	INI Read and Write functions 
; Version:	1.22
; Revised:	November 11th 2002 
; Author:	Leigh Bowers 

; Email:	leigh.bowers@curvesoftware.co.uk 
; Web:	http://www.curvesoftware.co.uk/blitz 

; History: 

; 1.22  Failed to rename a variable during the v1.21 update - Fixed.
; 1.21	Functions/variables renamed for consitency, clarity & to avoid conflicts with globals - Prefxed with "INI_".
; 1.20	Bug fix: INI files with a single section could behave weirdly (thanks to Jacob Sindelir for highlighting the problem).
; 1.13	More efficient use of Upper$.
; 1.12	Minor optimisations/tweaks.
; 1.11	First public release (1.10 + enhancement).
;		Read/Write: Added support for INI sections. 
;		Read: Now removes leader and trailer quotes where applicable (optional). 
; 1.04	Initial internal release. 

; Example usage: 

;Print "INI Value = " + INI_ReadValue("Archon", "Video", "Resolution", "800x600") 
;bSuccess% = INI_WriteValue("Archon", "Audio", "Music", "0") 

Function INI_ReadValue$(INI_sAppName$, INI_sSection$, INI_sKey$, INI_sDefault$)

; Returns: INI Value (String) - The INI_sDefault value is returned if no SECTION/KEY combination is found

	INI_sSection = "[" + Upper$(Trim$(INI_sSection)) + "]"
	INI_sKey = Upper$(Trim$(INI_sKey))
	INI_sFilename$ = CurrentDir$() + "\"  + INI_sAppName + ".ini"

; Retrieve the INI data

	INI_sContents$= INI_FileToString(INI_sFilename)
	INI_sUpperContents$ = Upper$(INI_sContents) ; Speed up checks below

; Locate the SECTION, KEY and VALUE

	INI_sValue$ = ""

	INI_lSectionPos% = Instr(INI_sUpperContents, INI_sSection)
	If (INI_lSectionPos <> 0) Then
		INI_lKeyPos% = Instr(INI_sUpperContents, INI_sKey, (INI_lSectionPos + Len(INI_sSection) + 1))
		If (INI_lKeyPos <> 0) Then
			INI_lStartPos% = Instr(INI_sUpperContents, "=", (INI_lKeyPos + 1))
			If (INI_lStartPos <> 0) Then
				INI_lEndPos% = Instr(INI_sUpperContents, Chr$(0), (INI_lStartPos + 1))
				If (INI_lEndPos <> 0) Then
					; We have located the required INI VALUE!
					INI_sValue = Trim$(Mid$(INI_sContents, INI_lStartPos + 1, (INI_lEndPos - INI_lStartPos - 1)))
					; Now remove any leader/trailer quotes (") - ** OPTIONAL **
					;If Left$(INI_sValue, 1) = Chr$(34) Then INI_sValue = Mid$(INI_sValue, 2)
					;If Right$(INI_sValue, 1) = Chr$(34) Then INI_sValue = Mid$(INI_sValue, 1, (Len(INI_sValue) - 1))
				End If
			End If
		End If
	End If

; Return the value

	If (INI_sValue <> "") Then Return INI_sValue Else Return INI_sDefault

End Function

Function INI_WriteValue%(INI_sAppName$, INI_sSection$, INI_sKey$, INI_sValue$)

; Returns: True (Success) or False (Failed)

	INI_sSection = "[" + Trim$(INI_sSection) + "]"
	INI_sUpperSection$ = Upper$(INI_sSection)
	INI_sKey = Trim$(INI_sKey)
	INI_sValue = Trim$(INI_sValue)
	INI_sFilename$ = CurrentDir$() + "\"  + INI_sAppName + ".ini"

; Retrieve the INI data (if it exists)

	INI_sContents$= INI_FileToString(INI_sFilename)

; (Re)Create the INI file updating/adding the SECTION, KEY and VALUE

	INI_bWrittenKey% = False
	INI_bSectionFound% = False
	INI_sCurrentSection$ = ""

	INI_lFileHandle = WriteFile(INI_sFilename)
	If INI_lFileHandle = 0 Then Return False ; Create file failed!

	INI_lOldPos% = 1
	INI_lPos% = Instr(INI_sContents, Chr$(0))

	While (INI_lPos <> 0)

		INI_sTemp$ =Trim$(Mid$(INI_sContents, INI_lOldPos, (INI_lPos - INI_lOldPos)))
		
		If (INI_sTemp <> "") Then

			If Left$(INI_sTemp, 1) = "[" And Right$(INI_sTemp, 1) = "]" Then

				; Process SECTION

				If (INI_sCurrentSection = INI_sUpperSection) And (INI_bWrittenKey = False) Then
					INI_bWrittenKey = INI_CreateKey(INI_lFileHandle, INI_sKey, INI_sValue)
				End If
				INI_sCurrentSection = Upper$(INI_CreateSection(INI_lFileHandle, INI_sTemp))
				If (INI_sCurrentSection = INI_sUpperSection) Then INI_bSectionFound = True

			Else

				; KEY=VALUE

				lEqualsPos% = Instr(INI_sTemp, "=")
				If (lEqualsPos <> 0) Then
					If (INI_sCurrentSection = INI_sUpperSection) And (Upper$(Trim$(Left$(INI_sTemp, (lEqualsPos - 1)))) = Upper$(INI_sKey)) Then
						If (INI_sValue <> "") Then INI_CreateKey INI_lFileHandle, INI_sKey, INI_sValue
						INI_bWrittenKey = True
					Else
						WriteLine INI_lFileHandle, INI_sTemp
					End If
				End If

			End If

		End If

		; Move through the INI file...

		INI_lOldPos = INI_lPos + 1
		INI_lPos% = Instr(INI_sContents, Chr$(0), INI_lOldPos)

	Wend

	; KEY wasn't found in the INI file - Append a new SECTION if required and create our KEY=VALUE line

	If (INI_bWrittenKey = False) Then
		If (INI_bSectionFound = False) Then INI_CreateSection INI_lFileHandle, INI_sSection
		INI_CreateKey INI_lFileHandle, INI_sKey, INI_sValue
	End If

	CloseFile INI_lFileHandle

	Return True ; Success

End Function

Function INI_FileToString$(INI_sFilename$)

	INI_sString$ = ""
	INI_lFileHandle% = ReadFile(INI_sFilename)
	If INI_lFileHandle <> 0 Then
		While Not(Eof(INI_lFileHandle))
			INI_sString = INI_sString + ReadLine$(INI_lFileHandle) + Chr$(0)
		Wend
		CloseFile INI_lFileHandle
	End If
	Return INI_sString

End Function

Function INI_CreateSection$(INI_lFileHandle%, INI_sNewSection$)

	If FilePos(INI_lFileHandle) <> 0 Then WriteLine INI_lFileHandle, "" ; Blank line between sections
	WriteLine INI_lFileHandle, INI_sNewSection
	Return INI_sNewSection

End Function

Function INI_CreateKey%(INI_lFileHandle%, INI_sKey$, INI_sValue$)

	WriteLine INI_lFileHandle, INI_sKey + "=" + INI_sValue
	Return True

End Function
