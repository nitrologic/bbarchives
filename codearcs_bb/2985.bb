; ID: 2985
; Author: Yasha
; Date: 2012-10-13 20:21:13
; Title: Lexical scanner framework
; Description: API for creating general-purpose lexical scanners/tokenisers

; Generic lexical scanner/tokeniser framework
;=============================================


;Dependencies
Include "LList.bb"	;Get this file at http://www.blitzbasic.com/codearcs/codearcs.php?code=2873
Include "Regex.bb"	;Get this file at http://www.blitzbasic.com/codearcs/codearcs.php?code=2632

;User-specialised interface: supplies LEX_Error and LEX_HandleAction
Include "Lexer-Interface.bb"


Type LEX_Token
	Field value$, tType$
	Field file$, l, c
End Type

Type LEX_Lexer
	Field rules.LList
	Field cFile.LEX_File
	Field out.LList		;Output list is not "owned" by the lexer
	Field csMode, guardMode, errState
	Field istk.LList, prev.LList
End Type

Type LEX_LexRule
	Field rule.RegEx_Node, pattern$
	Field action, result$, mode
End Type

Type LEX_File
	Field dir$, name$
	Field stream, sLen, cPtr, cLine, cCol
End Type

Type LEX_Guard
	Field name$
End Type


Const LEX_ACTION_STORE = -1, LEX_ACTION_MODE = -2, LEX_ACTION_ERROR = -3, LEX_ACTION_DISCARD = -4, LEX_ACTION_INCLUDE = -5


;Create a new, empty lexer object
Function LEX_CreateLexer.LEX_Lexer()
	Local l.LEX_Lexer = New LEX_Lexer
	lrules = CreateList()
	lcsMode = True
	lguardMode = False
	Return l
End Function

;Set the output token list for the lexer
Function LEX_SetOutput(l.LEX_Lexer, out.LList)
	lout = out
End Function

;Set whether rules added to this lexer will be case-sensitive (changing this doesn't affect old rules)
Function LEX_SetCaseSensitivity(l.LEX_Lexer, csMode)
	lcsMode = csMode
End Function

;Set whether included files are auto-guarded or not (i.e. can they be included twice?)
Function LEX_SetIncludeMode(l.LEX_Lexer, guarded)
	lguardMode = guarded
End Function

;(Internal) Try to include a source file, guards and recursion checks permitting (returns boolean indicating success)
Function LEX_TryIncludeFile(l.LEX_Lexer, file$)
	Local i.Iterator, fd$[0], fn$[0]
	LEX_GetPathCmpts file, fd, fn : file = fd[0] + fn[0]	;Force the path to be absolute for easier comparison
	
	If lguardMode	;Auto-guarded includes: check if it's been used already, if so ignore it
		i = GetIterator(lprev) : While EachIn(i)
			Local g.LEX_Guard = Object.LEX_Guard iValue
			If gname = file Then Return True	;...return without actually changing it
		Wend
	EndIf
	
	i = GetIterator(listk) : While EachIn(i)	;Check against the currently-open files
		Local f.LEX_File = Object.LEX_File iValue
		If fdir + fname = file
			LEX_Error l, "Cannot recursively include '" + file + "'"
			Return False
		EndIf
	Wend
	
	lcFile = LEX_ReadFile(file)
	If lcFile <> Null
		ListAddLast listk, Handle lcFile : LEX_GuardFileName l, lcFile
		Return True
	Else
		Return False	;Error has already been issued by LEX_ReadFile
	EndIf
End Function

;(Internal) Clear the include stacks when a scan has stopped, closing any "owned" files
Function LEX_ClearStacks(l.LEX_Lexer, keep.LEX_File)
	Local i.Iterator
	If listk <> Null
		i = GetIterator(listk) : While EachIn(i)
			Local f.LEX_File = Object.LEX_File iValue : If f <> keep Then LEX_CloseFile f
		Wend
		FreeList listk
	EndIf
	If lprev <> Null
		i = GetIterator(lprev) : While EachIn(i)
			Delete Object.LEX_Guard iValue
		Wend
		FreeList lprev
	EndIf
	lcFile = Null
End Function

;Delete a lexer object and its resources (not output)
Function LEX_FreeLexer(l.LEX_Lexer)
	Local i.Iterator = GetIterator(lrules) : While EachIn(i)
		Local r.LEX_LexRule = Object.LEX_LexRule(iValue)
		RegEx_Delete rrule
		Delete r
	Wend
	FreeList lrules
	
	LEX_ClearStacks l, Null
	If lcFile <> Null Then LEX_CloseFile lcFile
	
	Delete l
End Function

;Delete an output token stream (convenience function)
Function LEX_FreeTokenStream(s.LList)
	Local i.Iterator = GetIterator(s) : While EachIn(i)
		Delete Object.LEX_Token iValue
	Wend
	FreeList s
End Function

;Add a scan rule, consisting of a match pattern, action, "result" (additional data such as type or error message) and permitted mode
Function LEX_AddLexRule(l.LEX_Lexer, rule$, action, result$ = "", mode = 0)
	Local r.LEX_LexRule = New LEX_LexRule
	rrule = RegEx_Parse(rule, lcsMode)
	rpattern = rule
	raction = action
	rresult = result
	rmode = mode
	ListAddLast lrules, Handle r
End Function

;Scan a file (opened with LEX_ReadFile) using the given lexer
Function LEX_ScanFile(l.LEX_Lexer, f.LEX_File)
	LEX_ResetFile f
	lerrState = False
	lcFile = f
	
	listk = CreateList()
	lprev = CreateList()
	ListAddLast listk, Handle f
	LEX_GuardFileName l, f
	
	Repeat
		Local token$, rule.LEX_LexRule, mode = 0
		
		While lcFilecPtr < lcFilesLen
			token = "" : rule = Null
			
			Local i.Iterator = GetIterator(lrules) : While EachIn(i)
				Local r.LEX_LexRule = Object.LEX_LexRule iValue
				If mode = rmode
					Local cMatch$ = RegEx_Match(rrule, lcFilestream, lcFilecPtr)
					If Len(cMatch) > Len(token) Then token = cMatch : rule = r
				EndIf
			Wend
			
			If rule <> Null		;Something matched successfully!
				Select ruleaction
					Case LEX_ACTION_STORE
						ListAddLast lout, Handle LEX_NewToken(token, ruleresult, lcFilename, lcFilecLine, lcFilecCol)
					Case LEX_ACTION_MODE
						mode = Int ruleresult
					Case LEX_ACTION_ERROR
						LEX_Error l, ruleresult
						LEX_ClearStacks l, f : Return	;Clear the include stacks and stop scanning
					Case LEX_ACTION_DISCARD
						;Do nothing
					Case LEX_ACTION_INCLUDE
						LEX_IncrementFilePtr lcFile, Len(token)
						token = LEX_FilterIncludeString(token, ruleresult)	;Shorten the token to just the file path
						If Not LEX_TryIncludeFile(l, token) Then LEX_ClearStacks l, f : Return
						
					Default
						LEX_HandleAction l, rule, token
						If lerrState Then LEX_ClearStacks l, f : Return
				End Select
				
				If ruleaction <> LEX_ACTION_INCLUDE Then LEX_IncrementFilePtr lcFile, Len(token)
			Else
				LEX_IncrementFilePtr lcFile, 1
			EndIf
		Wend
		
		If lcFile <> f		;Pop back to the previous file in the include stack
			LEX_CloseFile lcFile
			ListRemoveLast listk
			lcFile = Object.LEX_File ListLast(listk)
		Else
			Exit	;If it's f, we're done
		EndIf
	Forever
	
	LEX_ClearStacks l, f
End Function

;Load a file into a suitable format for scanning
Function LEX_ReadFile.LEX_File(path$)
	path = Replace(path, "", "/")	;Normalise slashes
	
	Local dirName$[0], fileName$[0] : LEX_GetPathCmpts path, dirName, fileName
	
	Local stream = ReadFile(path) : If Not stream
		LEX_Error Null, "Unable to open file '" + path + "' ('" + dirName[0] + fileName[0] + "')"
		Return Null
	EndIf
	
	Local f.LEX_File = New LEX_File
	fdir = dirName[0] : fname = fileName[0]
	
	fstream = CreateBank(FileSize(path))
	ReadBytes fstream, stream, 0, BankSize(fstream)
	CloseFile stream
	fsLen = BankSize(fstream)
	
	LEX_ResetFile f
	Return f
End Function

;Flag an error, preventing the lexer from continuing and possibly logging it via the interface
Function LEX_Error(l.LEX_Lexer, msg$)
	LEX_LogError l, msg
	lerrState = True
End Function

;(Internal) Use a simple set of filter chars to chop the path out of an include directive
Function LEX_FilterIncludeString$(inc$, filter$)
	Local i, p
	For i = 1 To Len(filter)
		p = Instr(inc, Mid(filter, i, 1))
		If p Then inc = Mid(inc, p + 1) : Exit
	Next
	For i = 1 To Len(filter)
		p = Instr(inc, Mid(filter, i, 1))
		If p Then inc = Left(inc, p - 1) : Exit
	Next
	Return inc
End Function

;(Internal) Get the absolute directory and stripped filename from a path
Function LEX_GetPathCmpts(path$, dirOut$[0], fileOut$[0])
	dirOut[0] = "" : fileOut[0] = path
	Local tgt$ : If Instr(path, "/") Then tgt = "/" : Else tgt = ":"
	
	Local c : For c = Len(path) To 1 Step -1
		If Mid(path, c, 1) = tgt
			dirOut[0] = Left(path, c) : If Left(dirOut[0], 1) = "/" Then dirOut[0] = Mid(dirOut[0], 2)
			fileOut[0] = Mid(path, c + 1)
			Exit
		EndIf
	Next
	
	Local isAbsolute = (Left(path, 2) = "//") Or Instr(path, ":")	;UNC/driveletter/URL
	If Not isAbsolute Then dirOut[0] = dirOut[0] + Replace(CurrentDir(), "", "/")
End Function

;(Internal) Reset a LEX_File's pointer fields
Function LEX_ResetFile(f.LEX_File)
	fcPtr = 0
	fcLine = 1
	fcCol = 1
End Function

;(Internal) Increment a LEX_File's pointer fields
Function LEX_IncrementFilePtr(f.LEX_File, count)
	Local c : For c = 1 To count
		Local char = PeekByte(fstream, fcPtr)
		If char < 32	;Only count printable characters in the column field
			If char = 10 Then fcLine = fcLine + 1 : fcCol = 1
		Else
			fcCol = fcCol + 1
		EndIf
		fcPtr = fcPtr + 1
	Next
End Function

;(Internal) "Guard" a file against being included twice
Function LEX_GuardFileName(l.LEX_Lexer, f.LEX_File)
	Local g.LEX_Guard = New LEX_Guard
	gname = fdir + fname
	ListAddLast lprev, Handle g
End Function

;"Close" a file opened for scanning
Function LEX_CloseFile(f.LEX_File)
	If fstream Then FreeBank fstream
	Delete f
End Function

;Treat the contents of a string as a lexer file (useful for short inputs)
Function LEX_FileFromString.LEX_File(s$)
	Local f.LEX_File = New LEX_File
	fdir = "" : fname = "<string>"
	
	fstream = CreateBank(Len(s))
	Local c : For c = 1 To BankSize(fstream)
		PokeByte fstream, c - 1, Asc(Mid(s, c, 1))
	Next
	fsLen = BankSize(fstream)
	
	LEX_ResetFile f
	Return f
End Function

;Create a new match result (token object)
Function LEX_NewToken.LEX_Token(val$, tt$, file$, l = 0, c = 0)
	Local t.LEX_Token = New LEX_Token
	tvalue = val : ttType = tt
	tfile = file : tl = l : tc = c
	Return t
End Function


;~IDEal Editor Parameters:
;~F#D#12#1A#1F#24#2D#36#3B#40#45#62#74#83#8B#96#D6#ED#F3#101#112
;~F#119#126#12D#133#142
;~C#Blitz3D
