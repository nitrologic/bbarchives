; ID: 594
; Author: Milky Joe
; Date: 2003-02-19 08:02:44
; Title: INI File Functions v2.2
; Description: INI file manipulation (read/write).

; Title:	INI file manipulation functions 
; Version:	2.2
; Revised:	June 16th 2004
; Author:	Leigh Bowers 

; Email:	leigh.bowers@curvesoftware.co.uk 
; Web:		http://www.curvesoftware.co.uk/blitz 

; History: 

; 2.2		Fixed issue with locating keys.
; 2.01		Removed superfluous CR LF from file.
; 2.00		Redesigned and rewritten.

; Example usage: 

;Include "Ini2.2.bb"
;
;INI_OpenFile("Archon.ini")
;strValue$ = INI_ReadValue("Config", "Mode", "C64") 
;INI_WriteValue("Config", "Mode", "Atari")
;If Not(INI_CloseFile()) Then
; 	Print "Unable to commit INI file changes to file (" + INI_CurrentFile\strFilename + ")."
;End If

Type INI_Type
	Field strFilename$ = ""
	Field blnModified% = False
	Field strContents$ = ""
End Type
Global INI_CurrentFile.INI_Type = New INI_Type

; Core functions

Function INI_OpenFile(INI_strFilename$)

; Description:	Open the specified INI file for manipulation (if said file exists).
; Returns:		[] n/a

	INI_CurrentFile\strFilename = Trim(INI_strFilename) : If Right(Upper(INI_strFilename), 4) <> ".INI" Then INI_CurrentFile\strFilename = INI_CurrentFile\strFilename + ".ini"
	INI_CurrentFile\blnModified = False
	INI_CurrentFile\strContents = ""

	INI_lngFileHandle% = ReadFile(CurrentDir$() + "\"  + INI_CurrentFile\strFilename)
	If INI_lngFileHandle <> 0 Then
		While Not(Eof(INI_lngFileHandle))
			INI_CurrentFile\strContents = INI_CurrentFile\strContents + ReadLine$(INI_lngFileHandle) + Chr$($0D) + Chr$($0A)
		Wend
		CloseFile INI_lngFileHandle
	End If

End Function

Function INI_CloseFile%()

; Description:	If INI modifications have been made (via INI_WriteValue), they are saved back to the original INI file.
; Returns:		[boolean] True (success) or False (unable to save modifications back to the INI file, where applicable)

	INI_blnResult% = True

	If (INI_CurrentFile\blnModified = True) Then
		; Commit internal changes back to the INI file
		INI_lngFileHandle = WriteFile(CurrentDir$() + "\" + INI_CurrentFile\strFilename)
		If INI_lngFileHandle <> 0 Then
			WriteLine INI_lngFileHandle, Left$(INI_CurrentFile\strContents, Len(INI_CurrentFile\strContents) - 2)
			CloseFile INI_lngFileHandle
			INI_CurrentFile\blnModified = False
		Else
			INI_blnResult = False ; Create file failed
		End If
	End If
	
	Return INI_blnResult

End Function

Function INI_ReadValue$(INI_strSection$, INI_strKey$, INI_strDefault$ = "")

; Description:	Retrieve the INI file value for the specified INI Section/Key combination.
; Returns:		[string] INI Value - The optional INI_strDefault value is returned if no SECTION/KEY combination is found

	INI_strSection = "[" + Upper$(Trim$(INI_strSection)) + "]"
	INI_strKey = Chr$($0A) + Upper$(Trim$(INI_strKey))

	INI_strUpperContents$ = Upper$(INI_CurrentFile\strContents)

; Locate the SECTION, KEY and VALUE

	INI_strValue$ = ""

	INI_lngSectionPos% = Instr(INI_strUpperContents, INI_strSection)
	If (INI_lngSectionPos <> 0) Then
		INI_lngKeyPos% = Instr(INI_strUpperContents, INI_strKey, (INI_lngSectionPos + Len(INI_strSection) + 1))
		If (INI_lngKeyPos <> 0) Then
			INI_NextSection% = Instr(INI_strUpperContents, "[", (INI_lngSectionPos + Len(INI_strSection) + 1))
			If (INI_NextSection = 0) Or (INI_lngKeyPos < INI_NextSection) Then
				INI_lngStartPos% = Instr(INI_strUpperContents, "=", (INI_lngKeyPos + 1))
				If (INI_lngStartPos <> 0) Then
					INI_lngEndPos% = Instr(INI_strUpperContents, Chr$($0D), (INI_lngStartPos + 1))
					If (INI_lngEndPos <> 0) Then
						; We have located the required INI key and it's corresponding value
						INI_strValue = Trim$(Mid$(INI_CurrentFile\strContents, INI_lngStartPos + 1, (INI_lngEndPos - INI_lngStartPos - 1)))
					End If
				End If
			End If
		End If
	End If

; Return the appropriate value

	If (INI_strValue <> "") Then Return INI_strValue Else Return INI_strDefault

End Function

Function INI_WriteValue(INI_strSection$, INI_strKey$, INI_strValue$)

; Description:	Add/update key values within the current INI file (internally).
;				Note: Changes are not committed back To file until you explicitly call INI_CloseFile.
; Returns:		[] n/a

	INI_strSection = "[" + Trim$(INI_strSection) + "]"
	INI_strUpperSection$ = Upper$(INI_strSection)
	INI_strKey = Trim$(INI_strKey)
	INI_strValue = Trim$(INI_strValue)

; (Re)Create the INI contents updating/adding the SECTION, KEY and VALUE

	INI_blnWrittenKey% = False
	INI_blnSectionFound% = False
	INI_strCurrentSection$ = ""

	INI_strTempContents$ = INI_CurrentFile\strContents
	INI_CurrentFile\strContents = ""

	INI_lngOldPos% = 1
	INI_lngPos% = Instr(INI_strTempContents, Chr$($0D))
	
	While (INI_lngPos <> 0)

		INI_strTemp$ =Trim$(Mid$(INI_strTempContents, INI_lngOldPos, (INI_lngPos - INI_lngOldPos)))
		
		If (INI_strTemp <> "") Then
			If Left$(INI_strTemp, 1) = "[" And Right$(INI_strTemp, 1) = "]" Then
				; Process SECTION
				If (INI_strCurrentSection = INI_strUpperSection) And (INI_blnWrittenKey = False) Then
					INI_blnWrittenKey = INI_CreateKey(INI_strKey, INI_strValue)
				End If
				INI_CreateSection INI_strTemp
				INI_strCurrentSection = Upper$(INI_strTemp)
				If (INI_strCurrentSection = INI_strUpperSection) Then INI_blnSectionFound = True
			Else
				; KEY=VALUE
				INI_lngEqualsPos% = Instr(INI_strTemp, "=")
				If (INI_lngEqualsPos <> 0) Then
					If (INI_strCurrentSection = INI_strUpperSection) And (Upper$(Trim$(Left$(INI_strTemp, (INI_lngEqualsPos - 1)))) = Upper$(INI_strKey)) Then
						INI_blnWrittenKey = INI_CreateKey(INI_strKey, INI_strValue)
					Else
						INI_CurrentFile\strContents = INI_CurrentFile\strContents + INI_strTemp + Chr$($0D) + Chr$($0A)
					End If
				End If
			End If
		End If

		; Move through the INI contents...

		INI_lngOldPos = INI_lngPos + 1
		INI_lngPos% = Instr(INI_strTempContents, Chr$($0D), INI_lngOldPos)

	Wend

	; KEY wasn't found in the INI contents - Append a new SECTION if required and create our KEY=VALUE line

	If (INI_strValue <> "") And (INI_blnWrittenKey = False) Then
		If (INI_blnSectionFound = False) Then
			INI_CreateSection INI_strSection
		End If
		INI_CreateKey INI_strKey, INI_strValue
	End If
	
	INI_CurrentFile\blnModified = True

End Function

Function INI_CreateSection(INI_strNewSection$)

	If INI_CurrentFile\strContents <> "" Then
		; Blank line between sections
		INI_CurrentFile\strContents = INI_CurrentFile\strContents + Chr$($0D) + Chr$($0A)
	End If
	INI_CurrentFile\strContents = INI_CurrentFile\strContents + INI_strNewSection + Chr$($0D) + Chr$($0A)

End Function

Function INI_CreateKey%(INI_strKey$, INI_strValue$)

	If (INI_strValue <> "") Then
		INI_CurrentFile\strContents = INI_CurrentFile\strContents + INI_strKey + "=" + INI_strValue + Chr$($0D) + Chr$($0A)
	End If
	
	Return True

End Function
