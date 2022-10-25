; ID: 1517
; Author: Filax
; Date: 2005-11-04 06:21:03
; Title: Simple ini functions for Bmax
; Description: Original code by skn3[ac]

Strict

CFG_WriteValue("Archon", "Video resolution", "Res1", "640")
CFG_WriteValue("Archon", "Video resolution", "Res2", "600")
CFG_WriteValue("Archon", "Video resolution", "Res3", "800")

CFG_WriteValue("Archon", "Video frequence", "Freq1", "60")
CFG_WriteValue("Archon", "Video frequence", "Freq2", "70")
CFG_WriteValue("Archon", "Video frequence", "Freq3", "75")

Print "INI Value = " + CFG_ReadValue("Archon", "Video resolution", "Res1") 
Print "INI Value = " + CFG_ReadValue("Archon", "Video frequence", "Freq4",80) 
End

' --------------------------------
' Read a value under a config file
' --------------------------------
Function CFG_ReadValue:String(Filename:String, Section:String, Key:String, DefaultValue:String="")
	' --------------------
	' Formating parameters
	' --------------------
	Section:String = "[" + Upper(Trim(Section)) + "]" ; Key:String = Upper(Trim(Key))
	Filename:String = CurrentDir() + "\"  + Filename + ".ini"
	
	' ---------------------------
	' Read the configuration file
	' ---------------------------
	Local Content:String= CFG_FileToString(Filename)
	Local UpperContents:String = Upper(Content) 

	' ----------------------------
	' Search the key and the value
	' ----------------------------
	Local Value:String = "" ; Local SectionPos:Int = Instr(UpperContents, Section)
	
	If SectionPos <> 0 Then
		Local KeyPos:Int = Instr(UpperContents, Key, (SectionPos + Len(Section) + 1))
		
		If KeyPos <> 0 Then
			Local StartPos:Int = Instr(UpperContents, "=", (KeyPos + 1))
			
			If StartPos <> 0 Then
				Local EndPos:Int = Instr(UpperContents, Chr(0), (StartPos + 1))
				
				If EndPos <> 0 Then
					Value = Trim(Mid(Content, StartPos + 1, (EndPos - StartPos - 1)))
				End If
				
			End If
		End If
	End If

	' ----------------------------------------------------
	' If match then return value else return default value
	' ----------------------------------------------------
	If Value <> "" Then Return Value Else Return DefaultValue
End Function

' ---------------------------------
' Write a value under a config file
' ---------------------------------
Function CFG_WriteValue(Filename:String, Section:String, Key:String, Value:String)
	' --------------------
	' Formating parameters
	' --------------------
	Section = "[" + Trim(Section) + "]" ; Key = Trim(Key) ; Value = Trim(Value)
	Filename:String = CurrentDir() + "\"  + Filename + ".ini"

	' ---------------------------
	' Read the configuration file
	' ---------------------------	
	Local UpperSection:String = Upper(Section)
	Local Content:String= CFG_FileToString(Filename)

	' --------------
	' Init local var
	' --------------
	Local WrittenKey:Int = False
	Local SectionFound:Int = False
	Local CurrentSection:String = ""

	' ----------------------------
	' If the file cannot be opened
	' ----------------------------
	Local FileHandle:TStream = WriteFile(Filename)
	If Not FileHandle Then Return False

	Local OldPos:Int = 1
	Local Position:Int = Instr(Content, Chr(0))

	' ---------------------------
	' Read the configuration file
	' ---------------------------
	While Position <> 0

		Local TempString:String =Trim(Mid(Content, OldPos, (Position - OldPos)))
		
		If TempString <> "" Then

			If Left(TempString, 1) = "[" And Right(TempString, 1) = "]" Then
				If CurrentSection = UpperSection And (WrittenKey = False) Then
					WrittenKey = CFG_CreateKey(FileHandle, Key, Value)
				End If
				
				CurrentSection = Upper(CFG_CreateSection(FileHandle, TempString))
				
				If CurrentSection = UpperSection Then SectionFound = True
			Else
				Local EqualPos:Int = Instr(TempString, "=")
				
				If EqualPos <> 0 Then
					If CurrentSection = UpperSection And (Upper(Trim(Left(TempString, (EqualPos - 1)))) = Upper(Key)) Then
						If Value <> "" Then CFG_CreateKey(FileHandle, Key, Value)
						WrittenKey = True
					Else
						WriteLine(FileHandle, TempString)
					End If
				End If

			End If

		End If

		OldPos = Position + 1 ; Position = Instr(Content, Chr(0), OldPos)
	Wend

	' ---------------------------------------
	' If the key is not under the config file
	' ---------------------------------------
	If WrittenKey = False Then
		If SectionFound = False Then CFG_CreateSection FileHandle, Section
		CFG_CreateKey(FileHandle, Key, Value)
	End If

	CloseFile FileHandle
	
	' -----------
	' Return TRUE 
	' -----------
	Return True 
End Function

' -------------------------------------
' Return the config file under a string
' -------------------------------------
Function CFG_FileToString:String(Filename:String)
	Local TempString:String = ""
	Local FileHandle:TStream = ReadFile(Filename)
	
	If FileHandle Then
		While Not Eof(FileHandle)
			TempString = TempString + ReadLine(FileHandle) + Chr(0)
		Wend
		
		CloseFile FileHandle
	End If
	
	Return TempString
End Function

' -----------------------------------
' Write a section under a config file
' -----------------------------------
Function CFG_CreateSection:String(FileHandle:TStream, NewSection:String)
	If StreamPos(FileHandle) <> 0 Then WriteLine FileHandle, "" 
	WriteLine FileHandle, NewSection ; Return NewSection
End Function

' ---------------------------------
' Write a value under a config file
' ---------------------------------
Function CFG_CreateKey(FileHandle:TStream, Key:String, Value:String)
	WriteLine FileHandle, Key + "=" + Value ; Return True
End Function
