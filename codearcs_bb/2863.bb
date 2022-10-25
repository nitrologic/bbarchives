; ID: 2863
; Author: Yasha
; Date: 2011-06-23 21:17:24
; Title: INI-format data files
; Description: Load and save data to and from human-readable text files

; INI file read/write library
;=============================


; Public API:
;
; - INI_Create: Create a new, empty INI structure from scratch with an optional comment
;               Structures created in this way will attempt to auto-format themselves
;
; - INI_Load: Load an INI structure from a file
;             Structures created in this way will not apply any auto-formatting
;
; - INI_LoadString: Load an INI structure from a string
;                   This is otherwise identical to INI_Load
;
; - INI_Write: Write an INI structure out to a text file
;
; - INI_WriteString: Write an INI structure out to a string
;                    As far as possible this is identical to INI_Write
;
; - INI_Free: Free an INI structure and all of its elements
;
; - INI_AddSection: Add a new, empty section to an INI structure
;                   No check is performed to see if a section with that name already exists
;
; - INI_RemoveSection: Remove a section (and optionally all of its properties) from an INI structure
;                      In the case of duplicate sections, only the first is removed
;                      If the section does not exist, an error is logged and no action is taken
;
; - INI_AddProperty: Add a property to an INI structure (optionally in a given section)
;                    No check is performed to see if a property with that name already exists
;
; - INI_GetValue: Retrieve a value from a given property (optionally in a given section)
;                 If the property does not exist, the empty string is returned and an error logged
;
; - INI_SetValue: Set the value of a given property (optionally in a given section)
;                 If the property does not exist, an error is logged and no action is taken
;
; - INI_GetComment: Retrieve a comment from a given property (optionally in a given section)
;                   If the property does not exist, the empty string is returned and an error logged
;
; - INI_SetComment: Set the comment of a given property (optionally in a given section)
;                   If the property does not exist, an error is logged and no action is taken
;
; - INI_RemoveProperty: Remove a property from an INI structure (optionally in a given section)
;                       If the property does not exist, an error is logged and no action is taken
;
;
; All the remaining functions, whose names begin with INI_private_, are part of the internal
; implementation and should not be called directly.
;
; The LogError function, called when an error is encountered, is not provided in this library.
; Instead, link it to a suitable logging function for your application.
;


Type INI_File
	Field imported, comment$
	Field sCount, sList.INI_Section
	Field pCount, pList.INI_Property
End Type

Type INI_Stream
	Field sPtr, sData
	Field source$
End Type

Type INI_Section
	Field name$, comment$
	Field pCount, start.INI_Property
	
	Field pv.INI_Section, nx.INI_Section
End Type

Type INI_Property
	Field hasValue
	Field key$, value$, comment$
	
	Field pv.INI_Property, nx.INI_Property
End Type


; Create a new, empty INI structure
Function INI_Create.INI_File(comment$ = "")
	Local i.INI_File = New INI_File
	
	If comment <> ""
		i\comment = comment
		i\pList = INI_private_CreateProperty(i, Null, False, "", "", "", Null)	;Spacer line
		Local c.INI_Property = INI_private_CreateProperty(i, Null, False, "", "", comment, i\pList)
		INI_private_CreateProperty(i, Null, False, "", "", "", c)	;Another spacer
	EndIf
	
	Return i
End Function

; Load an INI structure from a file
Function INI_Load.INI_File(filename$)
	Local s.INI_Stream, i.INI_File
	
	If FileType(filename) <> 1		;Replace with appropriate error function for your program
		LogError "Could not open "+Chr(34)+filename+Chr(34)+": file not found"
		Return Null
	EndIf
	
	s = INI_private_LoadINIFileStream(filename)
	i = INI_private_ReadStream(s)
	INI_private_FreeINIFileStream s
	
	Return i
End Function

; Load an INI structure from a string
Function INI_LoadString.INI_File(val$)
	Local s.INI_Stream, i.INI_File
	
	s = INI_private_ReadINIFileSTream(val)
	i = INI_private_ReadStream(s)
	INI_private_FreeINIFileStream s
	
	Return i
End Function

; Write an INI structure out to a text file
Function INI_Write(ini.INI_File, filename$)
	Local f = WriteFile(filename), p.INI_Property, outL$
	
	If f = 0
		LogError "Unable to write to file "+Chr(34)+filename+Chr(34)
		Return
	EndIf
	
	p = ini\pList
	While p <> Null
		If p\hasValue
			outL = p\key + " = " + p\value
			If p\comment <> "" Then outL = outL + "    ;" + p\comment
		Else
			If p\value = ""
				If p\comment <> "" Then outL = ";" + p\comment : Else outL = ""
			Else
				outL = "[ " + p\value + " ]"
				If p\comment <> "" Then outL = outL + "    ;" + p\comment
			EndIf
		EndIf
		WriteLine f, outL
		
		p = p\nx
	Wend
	If Not ini\imported Then WriteLine f, ""
	
	CloseFile f
End Function

; Write an INI structure out to a string
Function INI_WriteString$(ini.INI_File)
	Local outS$, p.INI_Property, outL$
	
	p = ini\pList
	While p <> Null
		If p\hasValue
			outL = p\key + " = " + p\value
			If p\comment <> "" Then outL = outL + "    ;" + p\comment
		Else
			If p\value = ""
				If p\comment <> "" Then outL = ";" + p\comment : Else outL = ""
			Else
				outL = "[ " + p\value + " ]"
				If p\comment <> "" Then outL = outL + "    ;" + p\comment
			EndIf
		EndIf
		outS = outS + outL + Chr(13) + Chr(10)
		
		p = p\nx
	Wend
	If Not ini\imported Then outS = outS + Chr(13) + Chr(10)
	
	Return outS
End Function

; Free an INI structure and all of its elements
Function INI_Free(ini.INI_File)
	Local p.INI_Property, op.INI_Property
	
	p = ini\pList
	While p <> Null
		op = p
		p = p\nx
		Delete op
	Wend
	
	Local s.INI_Section, os.INI_Section
	
	s = ini\sList
	While s <> Null
		os = s
		s = s\nx
		Delete os
	Wend
	
	Delete ini
End Function


; Add a new, empty section to an INI structure
Function INI_AddSection(ini.INI_File, name$, comment$)
	Local s.INI_Section, os.INI_Section
	
	s = ini\sList
	While s <> Null
		os = s
		s = s\nx
	Wend
	
	Local p.INI_Property, op.INI_Property
	
	p = ini\pList
	While p <> Null
		op = p
		p = p\nx
	Wend
	
	INI_private_CreateSection ini, name, comment, os, op
End Function

; Remove a section (and optionally all of its properties) from an INI structure
Function INI_RemoveSection(ini.INI_File, name$, freeProperties = True)
	Local s.INI_Section, s_pv.INI_Section, s_nx.INI_Section
	
	s = ini\sList
	While s <> Null
		If s\name = name Then Exit
		s = s\nx
	Wend
	
	If s = Null
		LogError "Could not find section "+Chr(34)+name+Chr(34)+" to remove"
		Return
	EndIf
	
	If freeProperties
		Local  p.INI_Property, p_pv.INI_Property, p_nx.INI_Property, i
		
		p_pv = s\start\pv
		p = s\start
		p_nx = p\nx
		
		For i = 0 To s\pCount	;Not an error - we're also processing the zero element
			p_nx = p\nx
			Delete p
			p = p_nx
		Next
		
		If p_pv <> Null Then p_pv\nx = p_nx
		If p_nx <> Null Then p_nx\pv = p_pv
	EndIf
	
	If s\pv <> Null Then s\pv\nx = s\nx
	If s\nx <> Null Then s\nx\pv = s\pv
	Delete s
End Function


; Add a property to an INI structure (optionally in a given section)
Function INI_AddProperty(ini.INI_File, sec$, key$, value$, comment$ = "")
	Local s.INI_Section = Null, i, p.INI_Property, n.INI_Property
	
	If sec <> ""
		s = ini\sList
		While s <> Null
			If s\name = sec Then Exit
			s = s\nx
		Wend
		
		If s = Null
			LogError "Could not find section "+Chr(34)+sec+Chr(34)+"; property not added"
			Return
		EndIf
		
		p = s\start
		For i = 1 To s\pCount
			p = p\nx
		Next
	Else
		n = ini\pList
		While n <> Null
			p = n
			n = n\nx
		Wend
	EndIf
	
	;Note that we did NOT check for duplicate names - this is not an error
	INI_private_CreateProperty(ini, s, True, key, value, comment, p)
End Function

; Retrieve a value from a given property (optionally in a given section)
Function INI_GetValue$(ini.INI_File, sec$, key$)
	Local p.INI_Property[0], s.INI_Section[0]
	
	INI_private_GetProperty ini, sec, key, p, s		;Use out parameters
	
	If p[0] <> Null Then Return p[0]\value : Else Return ""
End Function

; Set the value of a given property (optionally in a given section)
Function INI_SetValue(ini.INI_File, sec$, key$, val$)
	Local p.INI_Property[0], s.INI_Section[0]
	
	INI_private_GetProperty ini, sec, key, p, s		;Use out parameters
	
	If p[0] <> Null Then p[0]\value = val
End Function

; Retrieve a comment from a given property (optionally in a given section)
Function INI_GetComment$(ini.INI_File, sec$, key$)
	Local p.INI_Property[0], s.INI_Section[0]
	
	INI_private_GetProperty ini, sec, key, p, s		;Use out parameters
	
	If p[0] <> Null Then Return p[0]\comment : Else Return ""
End Function

; Set the comment of a given property (optionally in a given section)
Function INI_SetComment(ini.INI_File, sec$, key$, cmmt$)
	Local p.INI_Property[0], s.INI_Section[0]
	
	INI_private_GetProperty ini, sec, key, p, s		;Use out parameters
	
	If p[0] <> Null Then p[0]\comment = cmmt
End Function

; Remove a property from an INI structure (optionally in a given section)
Function INI_RemoveProperty(ini.INI_File, sec$, key$)
	Local p.INI_Property[0], s.INI_Section[0], s2.INI_Section, p2.INI_Property, i
	
	INI_private_GetProperty ini, sec, key, p, s		;Use out parameters
	
	If sec = ""		;Make sure that if the property belongs to a section, it gets removed properly
		s2 = ini\sList
		While s2 <> Null
			p2 = s2\start
			For i = 1 To s2\pCount
				p2 = p2\nx
				If p2\hasValue
					If p2\key = key Then Exit	;The list is ordered, so break on the first match
				EndIf
			Next
			If i <= s2\pCount Then Exit
			s2 = s2\nx
		Wend
		If s2 <> Null
			If p2 = p[0] Then s[0] = s2
		EndIf
	EndIf
	
	If p[0] <> Null
		If s[0] <> Null Then s[0]\pCount = s[0]\pCount - 1
		If p[0]\pv <> Null Then p[0]\pv\nx = p[0]\nx
		If p[0]\nx <> Null Then p[0]\nx\pv = p[0]\pv
		Delete p[0]
	EndIf
End Function



; Internal function: load an INI structure from a bank stream
Function INI_private_ReadStream.INI_File(s.INI_Stream)
	Local i.INI_File = INI_Create(), error, ls.INI_Section, lp.INI_Property
	
	i\imported = True
	
	While s\sPtr < BankSize(s\sData) - 2
		INI_private_SkipWhitespace s
		Local c = PeekByte(s\sData, s\sPtr)
		
		If c = 59		;Semicolon
			lp = INI_private_ReadCommentLine(s, i, ls, lp)	;Doesn't raise any errors
			
		ElseIf c = 91		;[ (open bracket)
			ls = INI_private_ReadSectionDef(s, i, ls, lp)
			If ls = Null Then error = True : Else lp = ls\start
			
		ElseIf INI_private_IsValidNameChar(c)		;Key name
			lp = INI_private_ReadPropertyDef(s, i, ls, lp)
			If lp = Null Then error = True
			
		ElseIf INI_private_CheckNewline(s)		;Try to swallow a newline
			lp = INI_private_CreateProperty(i, ls, False, "", "", "", lp)
			
		Else
			error = True
		EndIf
		
		If error	;In the event of error, break off (should we try to continue instead?)
			Local lineNo[0], lineVal$[0]
			INI_private_GetCurrentLine s, lineNo, lineVal
			LogError "Error reading data from "+s\source+": line "+lineNo[0]+" ( "+lineVal[0]+" ) is not a valid property or section definition"
			INI_Free i
			Return Null
		EndIf
	Wend
	
	Return i
End Function

; Internal function: read a comment line into the INI structure for preservation
Function INI_private_ReadCommentLine.INI_Property(s.INI_Stream, i.INI_File, ls.INI_Section, lp.INI_Property)
	Local cmmt$
	s\sPtr = s\sPtr + 1
	While Not INI_private_CheckNewline(s)
		cmmt = cmmt + Chr(PeekByte(s\sData, s\sPtr))
		s\sPtr = s\sPtr + 1
	Wend
	Return INI_private_CreateProperty(i, ls, False, "", "", cmmt, lp)
End Function

; Internal function: read a section definition into the INI structure
Function INI_private_ReadSectionDef.INI_Section(s.INI_Stream, i.INI_File, ls.INI_Section, lp.INI_Property)
	Local name$, c, cmmt$
	
	s\sPtr = s\sPtr + 1
	INI_private_SkipWhitespace s
	
	c = PeekByte(s\sData, s\sPtr)
	While INI_private_IsValidNameChar(c)
		name = name + Chr(c)
		s\sPtr = s\sPtr + 1
		c = PeekByte(s\sData, s\sPtr)
	Wend
	
	INI_private_SkipWhitespace s
	
	; If there was no closing bracket, or there is an invalid character, return Null as error
	If PeekByte(s\sData, s\sPtr) <> 93 Or name = "" Then Return Null
	s\sPtr = s\sPtr + 1
	
	INI_private_SkipWhitespace s
	
	If PeekByte(s\sData, s\sPtr) = 59		;Semicolon - comment
		s\sPtr = s\sPtr + 1
		While Not INI_private_CheckNewline(s)
			cmmt = cmmt + Chr(PeekByte(s\sData, s\sPtr))
			s\sPtr = s\sPtr + 1
		Wend
	ElseIf Not INI_private_CheckNewline(s)		;Newline or comments only; otherwise, return Null as error
		Return Null
	EndIf
	
	Return INI_private_CreateSection(i, name, cmmt, ls, lp)
End Function

; Internal function: read a property definition into the INI structure
Function INI_private_ReadPropertyDef.INI_Property(s.INI_Stream, i.INI_File, ls.INI_Section, lp.INI_Property)
	Local name$, val$, cmmt$, c, inQuotes, inBraces, esc
	
	INI_private_SkipWhitespace s
	
	c = PeekByte(s\sData, s\sPtr)
	While INI_private_IsValidNameChar(c)
		name = name + Chr(c)
		s\sPtr = s\sPtr + 1
		c = PeekByte(s\sData, s\sPtr)
	Wend
	
	INI_private_SkipWhitespace s
	
	; If there is no equal sign, or there is an invalid character, return Null as error
	If PeekByte(s\sData, s\sPtr) <> 61 Or name = "" Then Return Null
	s\sPtr = s\sPtr + 1
	
	INI_private_SkipWhitespace s
	
	Repeat
		c = PeekByte(s\sData, s\sPtr)
		
		If Not inBraces
			If INI_private_CheckNewline(s) Then Exit		;Braces can contain newlines
			If (Not inQuotes) And (c = 59) Then Exit		;Semicolon
			
			If c = 92 Then esc = True		;Backslash (escape character)
			If c = 34 And esc = False Then inQuotes = Not inQuotes : Else esc = -1
			If c <> 92 Then esc = False
		EndIf
		
		If Not inQuotes
			If c = 123 Then inBraces = inBraces + 1		;Opening brace
			If c = 125 And inBraces > 0 Then inBraces = inBraces - 1		;Closing brace
		EndIf
		
		If esc = -1 Then val = Left(val, Len(val) - 1) : esc = False
		val = val + Chr(c)
		s\sPtr = s\sPtr + 1
		
		If s\sPtr = BankSize(s\sData)		;Unmatched brace could lead to hitting the EOF
			LogError "Unmatched braces in peoperty value"
			Return Null
		EndIf
	Forever
	
	If val = "" Then Return Null		;If there is no value, return Null as an error
	
	val = Trim(val)		;Remove trailing unquoted whitespace
	
	;If the whole of val is quoted or braced, remove the outer layer of quoting
	If (Left(val, 1) = Chr(34) And Right(val, 1) = Chr(34)) Or (Left(val, 1) = "{" And Right(val, 1) = "}")
		val = Mid(val, 2, Len(val) - 2)
	EndIf
	
	If c = 59		;Semicolon - comment
		s\sPtr = s\sPtr + 1
		While Not INI_private_CheckNewline(s)
			cmmt = cmmt + Chr(PeekByte(s\sData, s\sPtr))
			s\sPtr = s\sPtr + 1
		Wend
	EndIf
	
	Return INI_private_CreateProperty(i, ls, True, name, val, cmmt, lp)
End Function

; Internal function: create a property object
Function INI_private_CreateProperty.INI_Property(i.INI_File, s.INI_Section, hasVal, key$, val$, cmmt$, pv.INI_Property)
	Local p.INI_Property = New INI_Property
	
	p\hasValue = hasVal
	p\key = key
	p\value = val
	p\comment = cmmt
	p\pv = pv
	
	i\pCount = i\pCount + 1
	If i\pList = Null Then i\pList = p
	
	If s <> Null Then s\pCount = s\pCount + 1
	
	If pv <> Null
		p\nx = pv\nx
		pv\nx = p
		If p\nx <> Null Then p\nx\pv = p
	EndIf
	
	Return p
End Function

; Internal function: create a section object
Function INI_private_CreateSection.INI_Section(i.INI_File, name$, cmmt$, pv.INI_Section, pp.INI_Property)
	Local s.INI_Section = New INI_Section
	
	s\name = name
	s\comment = cmmt
	s\pv = pv
	
	If Not i\imported		;If this is being created ex nihilo, apply basic formatting here
		pp = INI_private_CreateProperty(i, Null, False, "", "", "", pp)
		pp = INI_private_CreateProperty(i, Null, False, "", "", "", pp)		;Two spacers?
		pp = INI_private_CreateProperty(i, Null, False, "", "", cmmt, pp)		;Comment
		s\start = INI_private_CreateProperty(i, s, False, "", name, "", pp)		;And the start line
		INI_private_CreateProperty i, Null, False, "", "", "", s\start		;Final spacer
	Else
		s\start = INI_private_CreateProperty(i, s, False, "", name, cmmt, pp)	;Otherwise, don't adjust the formatting
	EndIf
	
	s\pCount = 0	;Reset after adding s\start
	
	i\sCount = i\sCount + 1
	If i\sList = Null Then i\sList = s
	
	If pv <> Null Then pv\nx = s
	
	Return s
End Function

; Internal function: retrieve a property object by key, optionally in a given section
Function INI_private_GetProperty.INI_Property(ini.INI_File, sec$, key$, p_out.INI_Property[0], s_out.INI_Section[0])
	Local p.INI_Property, s.INI_Section, i
	
	If sec = ""		;No section, check all properties in order
		p = ini\pList
		While p <> Null
			If p\hasValue
				If p\key = key Then Exit
			EndIf
			p = p\nx
		Wend
		
		If p = Null Then LogError "Could not find property "+Chr(34)+key+Chr(34)
		
	Else
		s = ini\sList
		While s <> Null
			If s\name = sec Then Exit
			s = s\nx
		Wend
		
		If s = Null
			LogError "Could not find section "+Chr(34)+sec+Chr(34)+" to retrieve property"
			Return Null
		EndIf
		
		p = s\start		;Start itself is a placeholder
		For i = 1 To s\pCount
			p = p\nx
			If p\hasValue
				If p\key = key Then Exit
			EndIf
		Next
		
		If i > s\pCount
			LogError "Could not find property "+Chr(34)+key+Chr(34)+" in section "+Chr(34)+sec+Chr(34)
			Return Null
		EndIf
	EndIf
	
	; Use out parameters to return multiple values
	p_out[0] = p
	s_out[0] = s
End Function

; Internal function: create a new bank stream object
Function INI_private_CreateINIFileStream.INI_Stream(source$, size)
	Local s.INI_Stream = New INI_Stream
	
	s = New INI_Stream
	s\sData = CreateBank(size + 2)
	PokeShort s\sData, size, $0A	;Append a final line-ending to simplify checking
	s\sPtr = 0
	s\source = source
	
	Return s
End Function

; Internal function: load a bank stream from a file
Function INI_private_LoadINIFileStream.INI_Stream(filename$)
	Local size, s.INI_Stream, f
	
	If FileType(filename) <> 1		;Replace with appropriate error function for your program
		LogError "Could not open "+Chr(34)+filename+Chr(34)+": file not found"
		Return Null
	EndIf
	
	size = FileSize(filename)
	s = INI_private_CreateINIFileStream(filename, size)
	
	f = ReadFile(filename)
	ReadBytes s\sData, f, 0, size
	CloseFile f
	
	Return s
End Function

; Internal function: create a bank stream from a string
Function INI_private_ReadINIFileSTream.INI_Stream(val$)
	Local c, s.INI_Stream, i, l = Len(val)
	
	s = INI_private_CreateINIFileStream("string", l)
	
	For i = 1 To l
		PokeByte s\sData, i - 1, Asc(Mid(val, i, 1))
	Next
	
	Return s
End Function

; Internal function: free a bank stream
Function INI_private_FreeINIFileStream(s.INI_Stream)
	FreeBank s\sData
	Delete s
End Function

; Internal function: skip tabs and spaces in a bank stream
Function INI_private_SkipWhitespace(s.INI_Stream)
	Local c = PeekByte(s\sData, s\sPtr)
	While c = 9 Or c = 32
		s\sPtr = s\sPtr + 1
		c = PeekByte(s\sData, s\sPtr)
	Wend
End Function

; Internal function: test if a character is valid for use in a property or section name
Function INI_private_IsValidNameChar(c)
	If c >= 48 And c <= 57 Then Return True		;Digit 0-9
	If c >= 65 And c <= 90 Then Return True		;Letter A-Z
	If c >= 97 And c <= 122 Then Return True		;Letter a-z
	If c = 92 Or c = 58 Or c = 95 Or c = 46 Then Return True		;Colon, backslash, underscore, dot
	Return False
End Function

; Internal function: test for a newline (and swallow it if present)
Function INI_private_CheckNewline(s.INI_Stream)
	If PeekShort(s\sData, s\sPtr) = $A0D
		s\sPtr = s\sPtr + 2
		Return True
	ElseIf PeekByte(s\sData, s\sPtr) = 10 Or PeekByte(s\sData, s\sPtr) = 13
		s\sPtr = s\sPtr + 1
		Return True
	Else
		Return False
	EndIf
End Function

; Internal function: Get the current line contents and number (for error messages) - uses out-parameters
Function INI_private_GetCurrentLine(s.INI_Stream, lineNo[0], lineVal$[0])
	Local i, lIndex, lNo = 1	;Start at 1
	
	For i = 0 To s\sPtr
		If PeekShort(s\sData, i) = $A0D
			lNo = lNo + 1
			lIndex = i + 2
			i = i + 1
		ElseIf PeekByte(s\sData, i) = 10 Or PeekByte(s\sData, i) = 13
			lNo = lNo + 1
			lIndex = i + 1
		EndIf
	Next
	lineNo[0] = lNo
	
	lineVal[0] = ""
	While PeekByte(s\sData, lIndex) <> 10 And PeekByte(s\sData, lIndex) <> 13
		lineVal[0] = lineVal[0] + Chr(PeekByte(s\sData, lIndex))
		lIndex = lIndex + 1
	Wend
End Function


;~IDEal Editor Parameters:
;~F#3A#40#45#4C#55#63#73#7E#9D#B7#CF#E4#10A#12A#133#13C#145#14E#170#198
;~F#1A3#1C6#20A#222#23E#26C#279#28C#299#29F#2A8#2B1#2BE
;~C#Blitz3D
