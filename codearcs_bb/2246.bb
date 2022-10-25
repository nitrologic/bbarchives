; ID: 2246
; Author: Baystep Productions
; Date: 2008-04-23 04:00:41
; Title: PsychoScript v0.5
; Description: Alpha scripting library

;Globals and other variables.
;============================
Global debug_mode=True,debug_file
Global last_error$,isError=False
Global last_tokcnt%

Global el$ = Chr$(10) + Chr$(13)
Global tb$ = Chr$(9)
Global dqt$ = Chr$(34)

Global inIf,isTrue=False,wasFalse=False
Global inFunction,runStart%,runFinish%,runTime%
Global temp1$,temp2$,temp3$,temp4$

Type error
	Field msg$,caller$
End Type
Global er.error

Type token
	Field value$,id%
End Type
Global tk.token,tk2.token

;===VARIABLE TYPE===
Type variable
	Field name$,kind%,parent$
	Field vStr$,vFlt#,vInt%,vFnc$ ;STRING$, FLOAT#, INTEGER%, FUNCTION-POINTER&
End Type
Global var.variable

;===COMPLEX CLASSES===
Type class
	Field name$,className$
End Type
Global obj.class

Type scrLine
	Field value$,parent$
End Type
Type fncLine
	Field value$,parent$
End Type
Type script
	Field name$,ln.scrLine
End Type
Global scr2.script
Type func
	Field name$,params$,fln.fncLine
End Type
Global fnc.func,fncE.func

;Initializing and routine functions
;==================================
Function AddError(msg$,caller$)
	er.error=New error
	er\msg$=msg$
	er\caller$=caller$
	last_error$ = "["+caller$+"]: "+msg$
	DebugOut("! ERROR: "+last_error$)
End Function
Function PrintErrors()
	For er.error = Each error
		Print "["+er\caller$+"]: "+er\msg$
	Next
End Function
Function ClearErrors()
	For er.error = Each error
		Delete er
	Next
	last_error$=""
	isError=False
End Function
Function CheckErrors(caller$="")
	For er.error = Each error
		If caller$=""
			If er\msg$<>"" Then Return True
		Else
			If caller$=er\caller$ And er\msg$<>"" Then Return True
		EndIf
	Next
End Function
Function PromptError() ;Formats a runtimerror message for you! No using variables now!
	msg$ = "AN ERROR HAS ACCURED:"+el$
	msg$ = msg$ + tb$ +last_error$+el$
	msg$ = msg$ + tb$ +"You can review the debug.log file for more information,"+el$
	msg$ = msg$ + tb$ +"there could also be more errors found there."+el$
	RuntimeError(msg$)
End Function
	
Function Initialize%(dbg%=False)
	debug_mode = dbg%
	If dbg%
		debug_file=WriteFile("Debug.log") ;Just clear and write
		If Not debug_file
			AddError("Could not write debug log file!","Initialize")
			Return False
		EndIf
		DebugOut("DEBUG LOG STARTED!")
		DebugOut("DATE: "+CurrentDate$())
		DebugOut("TIME: "+CurrentTime$())
		DebugOut("==================")
	EndIf
End Function
Function Destroy()
	If debug_mode
		CloseFile(debug_file)
	EndIf
End Function
Function DebugOut(msg$)
	If debug_mode=True
		If debug_file<>0
			WriteLine(debug_file,msg$)
		Else
			AddError("Debug file could not be writen to!","DebugOut")
		EndIf
	EndIf
End Function
Function PrintDebug%()
	Local dfile = ReadFile("debug.log")
	If Not dfile
		AddError("Could not load debug log file!","PrintDebug")
		Return False
	EndIf
	While Not Eof(dfile)
		Print ReadLine(dfile)
	Wend
	CloseFile(dfile)
	Return True
End Function

;PARSING AND TOKENIZER FUNCTIONS
;===============================
Function SimpleTokenize(inp$,sep$,prsvQt=True) ;This tokenizes by given character.
	;Always sets ID To -1 for seperation. prsvQt option is for excluding sep$ charecters in quotes.
	Local temp$,char$,stk.token,qt=False
	For i = 1 To Len(inp$)
		char$ = Mid(inp$,i,1)
		If char$=sep$
			If qt=False And prsvQt=True
				stk.token = New token
				stk\id% = -1
				stk\value$ = temp$
				temp$=""
			EndIf
		Else
			If char$=dqt$
				qt=Not qt
			Else
				temp$=temp$+char$
			EndIf
		EndIf
	Next
	If Len(temp$)>0
		stk.token = New token
		stk\id% = -1
		stk\value$ = temp$
		temp$=""
	EndIf
End Function
Function AdvancedTokenize(inp$,id%=0,smb%=0) ;This tokenizes by symbols and characters! Spiffy huh?
	;Allows for stack ordering with the id% parameter
	;smb% is to start tokenizing by symbols first.
	Local temp$,mode%=smb
	Local char$,kind%,qt=False,par=False
	For i = 1 To Len(inp$)
		char$ = Mid(inp$,i,1)
		;Check type of character symbol or character!
		kind%=1
		If Asc(char$)>=48 And Asc(char$)<=57;Number, counts as character
			kind%=0
		Else If Asc(Lower(char$))>=97 And Asc(Lower(char$))<=122;letter
			kind%=0
		Else If Asc(char$)=34 ;Quotes count too! SET QUOTE MODE
			kind%=0
			qt = Not qt
		Else If Asc(char$)<>32 ;don't count spaces, symbol!
			kind%=1
		EndIf
		;Check last type with mode and add to temp
		If Not mode% ;characters
			If Not kind% ;If character
				temp$=temp$+char$
			Else ;Broke loop add token and switch
				If qt=False
					If Trim(temp$)<>"" Then PushToken(temp$,id%)
					If Trim(char$)<>"" Then PushToken(char$,id%)
					mode%=1
				Else
					temp$=temp$+char$
				EndIf
			EndIf
		Else ;Symbols
			If kind%
				If Trim(char$)<>"" Then PushToken(char$,id%)
			Else ;Broke loop add token and switch back
				temp$=char$;Right(temp$,Len(temp$)-1)
				mode%=0
			EndIf
		EndIf
	Next
	If temp$<>""
		PushToken(temp$,id%)
	EndIf
End Function
Function PushToken(inp$,id%) ;Pushes a token to stack. Dunno why.
	tk.token = New token
	tk\value$ = inp$
	tk\id% = id%
	last_tokcnt% = last_tokcnt% + 1
End Function
Function PullToken$(id%=-1,del=True) ;Pull first token of matching ID, deletes after if del=True
	Local rtn$
	For tk.token = Each token
		If tk\id%=id% Or id%=-1
			rtn$=tk\value$
			If del=True
				Delete tk
			EndIf
			Exit
		EndIf
	Next
	Return rtn$
End Function
Function GetToken$(pos%,id%=-1) ;Will retrieve token #
	Local num%
	For tk.token = Each token
		If tk\id%=id% Or id%=-1
			num%=num%+1
			If num%=pos%
				Return tk\value$
			EndIf
		EndIf
	Next
	AddError("Token number does not exist! Check ID# or Position#","GetToken()")
	Return ""
End Function
Function GotoToken%(pos%)
	Local cnt%
	For tk.token = Each token
		cnt%=cnt%+1
		If cnt%=pos%
			Return True
		EndIf
	Next
	AddError("Token number does not exist! Check ID# or Position#","GotoToken()")
	Return False
End Function
Function GetNextToken$(id%=0)
	If id%=0
		tk = After tk
		Return tk\value$
	Else
		Repeat
			tk = After tk
		Until tk\id% = id%
		Return tk\value$
	EndIf
End Function
Function ClearTokens(id%=-1)
	For tk.token = Each token
		If tk\id%=id% Or id%=-1
			Delete tk
		EndIf
	Next
	last_tokcnt%=0
End Function
Function DebugTokens(id%=-1)
	Local temp$
	For tk.token = Each token
		If tk\id%=id% Or id%=-1
			temp$=temp$+"'"+tk\value$+"' "
		EndIf
	Next
	DebugOut("~ TOKENS: "+temp$)
End Function

Function ContainsChar(inp$) ;Checks only for letters
	If Asc(Lower(inp$))>=97 And Asc(Lower(inp$))<=122
		Return True
	EndIf
	Return False
End Function

Function TrimEnds$(inp$,amnt=1) ;Trims off the ends
	Return Mid(inp$,1+amnt,Len(inp$)-(1+amnt))
End Function

;DATA EDITING
;============
Function SetClassVariable(clName$,varName$,varValue$,varType$="$")
	;Formats a variable to this...
	;  var [ClassName]_[VarName] = [VALUE] parent to cl_[ClassName]
	SetVariable(clName$+"_"+varName$,varValue$,varType$,"cl_"+clName$)
End Function
Function GetClassVariable$(objName$,varName$) ;Returns the value from a obj.var run
	Return FillVariable(objName$+"_"+varName$,True)
End Function
Function SetVariable(vname$,vvalue$,vtype$="$",parent$="",noChange=False)
	Local found=False
	For var.variable = Each variable
		If var\name$=vname$ And var\parent$=parent$;Change value
			If noChange=True
				Return False
			EndIf
			Select var\kind%
				Case 1 ;String
					vvalue$ = Mid(vvalue$,2,Len(vvalue$)-2) ;Strip quotes
					var\vStr$=vvalue$
				Case 2 ;Float
					var\vFlt#=Float(vvalue$)
				Case 3 ;Integer
					var\vInt%=Int(vvalue$)
				Case 4 ;Function Pointer
					vvalue$ = Mid(vvalue$,2,Len(vvalue$)-2) ;Strip quotes
					var\vFnc$=vvalue$
				Default
					AddError("Memory Access Violation, variable type incorrect!","SetVariable")
			End Select
		EndIf
	Next
	If Not found
		var.variable = New variable
		var\name$ = vname$
		var\parent$ = parent$
		Select vtype$
			Case "$"
				var\kind%=1
				vvalue$ = Mid(vvalue$,2,Len(vvalue$)-2) ;Strip quotes
				var\vStr$=vvalue$
			Case "#"
				var\kind%=2
				var\vFlt#=Float(vvalue$)
			Case "%"
				var\kind%=3
				var\vInt%=Int(vvalue$)
			Case "&"
				var\kind%=4
				vvalue$ = Mid(vvalue$,2,Len(vvalue$)-2) ;Strip quotes
				var\vFnc$=vvalue$
			Default
				AddError("Memory Access Violation, variable type incorrect!","SetVariable")
		End Select
	EndIf
End Function
Function FillVariable$(inp$,clr=True) ;Returns variable value as string
	;CLR is whether to return empty "" if not found, else return original input
	For var.variable = Each variable
		If var\name$ = inp$
			Select var\kind%
				Case 1 ;String
					Return var\vStr$
				Case 2 ;Float
					Return Str(var\vFlt#)
				Case 3 ;Integer
					Return Str(var\vInt%)
				Case 4 ;Function Pointer
					Return var\vFnc$
				Default
					AddError("Memory Access Violation, variable type incorrect!","FillVariable")
			End Select
		EndIf
	Next
	Return inp$
End Function
		
;SCRIPTING FUNCTIONS
;===================
Function CreateNewScript%(name$) ;Pretty easy huh? Works off the global variable scr2.script to add to
	;Search if name is taken
	For scr2.script = Each script
		If scr2\name$ = name$
			AddError("A script with that name already exists!","CreateNewScript")
			Return False
		EndIf
	Next
	scr2.script = New script
	scr2\name$ = name$
	DebugOut("- INFO: Created new script named '"+name$+"'")
	Return True
End Function
Function AddLineToScript%(inp$,name$) ;Add line of inp$ to script named name$
	If Trim(inp$)="" Then Return True
	If Left(inp$,2)="//" Then Return True
	For scr2.script = Each script
		If scr2\name$ = name$
			;!!! Parse inputting line!
			inp$ = Replace(inp$,Chr$(9),"")
			ClearTokens()
			AdvancedTokenize(inp$)
			tk.token = First token
			Select Lower(Trim(tk\value$))
				Case "function"
					If inFunction=True
						AddError("Can not declare function inside of another!","AddLineToScript")
						Return False
					EndIf
					temp1$ = GetNextToken$()
					temp2$ = ""
					temp3$ = ""
					If GetNextToken$()="(" ;Just make sure
						For i=1 To last_tokcnt%-3
							temp$ = GetNextToken$()
							If temp$=")" ;Ship out parameters
								Exit
							Else
								If temp$="," ;Register variable
									SetVariable(Left(temp3$,Len(temp3$)-1),"",Right(temp3$,1),temp1$,True)
									temp3$ = temp3$ + temp$
									temp2$ = temp2$ + temp3$
									temp3$ = ""
								Else
									temp3$ = temp3$ + temp$
								EndIf
							EndIf
						Next
						If Len(temp3$)<>0
							SetVariable(Left(temp3$,Len(temp3$)-1),"",Right(temp3$,1),temp1$,True)
							temp3$ = temp3$ + temp$
							temp2$ = temp2$ + temp3$
							temp3$ = ""
						EndIf
						;!!! NOW SAVE FUNCTION AND SET TO READ MODE!
						fnc.func = New func
						fnc\name$ = temp1$
						fnc\params$ = temp2$
						inFunction = True
					Else
						AddError("Incorrect format for function!","AddLineToScript")
					EndIf
				Case "endfunction"	;Now end it!
					If Not inFunction
						AddError("Function must first be initialized before ended!","AddLineToScript")
						Return False
					EndIf
					inFunction = False
				Default ;Just add the line to the script
					If Not inFunction
						scr2\ln.scrLine = New scrLine
						scr2\ln\parent$ = name$
						scr2\ln\value$ = inp$
					Else
						fnc\fln.fncLine = New fncLine
						fnc\fln\parent$ = fnc\name$
						fnc\fln\value$ = inp$
					EndIf
			End Select
			Return True
		EndIf
	Next
	AddError("Could not add line to script named '"+name$+"'!","AddLineToScript")
	Return False
End Function
Function LoadScript%(name$)	;Loads a script named 'Scripts\'+name$+'.ps'
	For scr2.script = Each script
		If scr2\name$ = name$
			AddError("A script with that name already exists!","LoadScript")
			Return False
		EndIf
	Next
	file = ReadFile("Scripts\"+name$+".ps")
	If Not file
		AddError("Could not find file named Scripts\"+name$+".ps!","LoadScript")
		Return False
	EndIf
	scr2.script=New script
	scr2\name$=name$
	While Not Eof(file)
		AddLineToScript(ReadLine(file),name$)
	Wend
	CloseFile(file)
	DebugOut("- INFO: Loaded script with name '"+name$+"'")
	Return True
End Function
Function ExportScript(name$) ;Exports the script, results is after pre-parseing.
	;Only useful if saving programaticly generated scripts. Its de-commented and de-blank-lined
	For scr2.script = Each script
		If scr2\name$ = name$
			Local temp = WriteFile("Scripts\"+name$+"_exp.ps")
			For scr2\ln.scrLine = Each scrLine
				If scr2\ln\parent$ = name$
					WriteLine(temp,scr2\ln\value$)
				EndIf
			Next
			CloseFile(temp)
		EndIf
	Next
End Function
Function ExecuteScript(name$)
	runStart% = MilliSecs()
	Local found=False
	For scr2.script = Each script
		If scr2\name$ = name$
			found = True
			Exit
		EndIf
	Next
	If Not found Then AddError("No script found with the name '"+name$+"'","ExecuteScript")
	
	;Now we can start execution. We don't bother with ; characters. each line is a single call.
	For scr2\ln.scrLine = Each scrLine
		If scr2\ln\parent$ = name$
			ClearTokens()
			AdvancedTokenize(scr2\ln\value$)		;BUILD TOKEN BANK
			DebugTokens()						;PRINT OUT THE TOKENS
			ParseTokens() 						;ILLETERATE AND PARSE (NO IDEA ABOUT ID NUMS)
		EndIf
	Next
	runFinish% = MilliSecs()
	runTime% = runFinish%-runStart%
	DebugOut("- INFO: Time to execute = "+runTime%+"/ms")
End Function
Function ExecuteFunction()	;Works off global variable fncE.func for function to be executed
	For fncE\fln.fncLine = Each fncLine
		If fncE\fln\parent$ = fncE\name$
			ClearTokens()
			AdvancedTokenize(fncE\fln\value$)
			DebugTokens()
			ParseTokens()
		EndIf
	Next
End Function

Function ParseTokens() ;Parsing! Check the first, then start formatting!
	tk.token = First token ;Some other functions set this to the end
	Select Lower(Trim(tk\value$)) ;Only need to check the first one
		Case "var" ;Settting variable!
			If inIf=False Or ifTrue=True
				temp1$=GetNextToken$()	;Name
				temp2$=GetNextToken$()	;Type
				If GetNextToken$()="=" ;Set initial value
					temp3$ = GetNextToken$()
					If last_tokcnt%>5 ;THERES MORE!?!?
						For i=1 To last_tokcnt%-5
							temp3$=temp3$+GetNextToken$()
						Next
						temp3$ = ParseVariables(temp3$)
					EndIf
				Else ;Set empty
					If temp2$="$"
						temp3$=""
					Else
						temp3$="0"
					EndIf
				EndIf
				SetVariable(temp1$,temp3$,temp2$)
				DebugOut("- INFO: Variable '"+temp1$+"' was assigned '"+temp3$+"'!")
			EndIf
		Case "obj" ;Object handle
			If inIf=False Or ifTrue=True ;If statements... take up space but oh well
				temp1$=GetNextToken$() ;Object name
				If GetNextToken$()="=" ;As it should be
					temp2$=GetNextToken$() ;Operator, just use new
					Select temp2$
						Case "new"
							temp3$=GetNextToken$() ;Class name!
							obj = ClassConstructor(temp3$,temp1$)
						Default
							AddError("Unknown creation operator!","ExecuteScript")
					End Select
				Else
					AddError("Expected = operator at object creation!","ExecuteScript")
				EndIf
			EndIf
		Case "if"
			isTrue=False
			If inIf=False
				inIf=True
				If (last_tokcnt%-1)>4 ;Muliple statements
					For i=1 To (last_tokcnt%-1)/4
						
					Next
				Else ;Just a single statement
					temp1$=GetNextToken$()	;value 1
					temp2$=GetNextToken$()	;operator
					temp3$=GetNextToken$()	;operator
					temp4$=GetNextToken$()	;value 2
					temp1$=ParseVariables(temp1$)
					temp4$=ParseVariables(temp4$)
					If ContainsChar(temp1$)=False And ContainsChar(temp4$)=False
						var1#=Float(temp1$)
						var2#=Float(temp2$)
					EndIf
					DebugOut(temp1$+" "+temp2$+temp3$+" "+temp4$)
					If temp2$="=" And temp3$="=" 		;EQUAL
						If temp1$=temp4$ Then isTrue=True
					Else If temp2$=">" And temp3$="="	;LESS THEN EQUAL
						If temp1$>=temp4$ Then isTrue=True
					Else If temp2$="<" And temp3$="="	;GREATER THEN EQUAL
						If temp1$<=temp4$ Then isTrue=True
					Else If temp2$=">" And temp3$=">"	;GREATER THEN
						If temp1$>temp4$ Then isTrue=True
					Else If temp2$="<" And temp3$="<"	;LESS THEN
						If temp1$<temp4$ Then isTrue=True
					Else If temp2$="<" And temp3$=">"	;NOT EQUAL
						If temp1$<>temp4$ Then isTrue=True
					Else If temp2$="!" And temp3$="="	;NOT EQUAL
						If temp1$<>temp4$ Then isTrue=True
					Else
						AddError("ParseTokens::If - Invalid operator!","ExecuteScript")
					EndIf
				EndIf
				If isTrue=False
					wasFalse=True
				Else
					wasFalse=False
					DebugOut("False")
				EndIf
			Else
				AddError("ParseTokens::If - IF can only appear once in this statement!","ExecuteScript")
			EndIf
		Case "elseif"
			isTrue=False
			If inIf=True
			  If wasFalse=True
				If (last_tokcnt%-1)>4 ;Muliple statements
					For i=1 To (last_tokcnt%-1)/4
						
					Next
				Else ;Just a single statement
					temp1$=GetNextToken$()	;value 1
					temp2$=GetNextToken$()	;operator
					temp3$=GetNextToken$()	;operator
					temp4$=GetNextToken$()	;value 2
					temp1$=ParseVariables(temp1$)
					temp4$=ParseVariables(temp4$)
					If ContainsChar(temp1$)=False And ContainsChar(temp4$)=False
						var1#=Float(temp1$)
						var2#=Float(temp2$)
					EndIf
					DebugOut(temp1$+" "+temp2$+temp3$+" "+temp4$)
					isTrue=False
					If temp2$="=" And temp3$="=" 		;EQUAL
						If temp1$=temp4$ Then isTrue=True
					Else If temp2$=">" And temp3$="="	;LESS THEN EQUAL
						If temp1$>=temp4$ Then isTrue=True
					Else If temp2$="<" And temp3$="="	;GREATER THEN EQUAL
						If temp1$<=temp4$ Then isTrue=True
					Else If temp2$=">" And temp3$=">"	;GREATER THEN
						If temp1$>temp4$ Then isTrue=True
					Else If temp2$="<" And temp3$="<"	;LESS THEN
						If temp1$<temp4$ Then isTrue=True
					Else If temp2$="<" And temp3$=">"	;NOT EQUAL
						If temp1$<>temp4$ Then isTrue=True
					Else If temp2$="!" And temp3$="="	;NOT EQUAL
						If temp1$<>temp4$ Then isTrue=True
					Else
						AddError("ParseTokens::If - Invalid operator!","ExecuteScript")
					EndIf
				EndIf
				If isTrue=False
					wasFalse=True
				Else
					wasFalse=False
					DebugOut("False")
				EndIf
			  EndIf
			Else
				AddError("ParseTokens::ElseIf - Requires an IF statement first!","ExecuteScript")
			EndIf
		Case "else"
			isTrue=False
			If inIf=True
			  If wasFalse=True ;No true statements yet
				isTrue = True ;This one is true, execute script
			  EndIf
			Else
				AddError("ParseTokens::Else - Requires an IF statement first!","ExecuteScript")
			EndIf
		Case "endif"
			If inIf=True
				inIf=False
			Else
				AddError("ParseTokens::EndIf - Requires an IF statement first!","ExecuteScript")
			EndIf
		Default ;Function!
			If inIf=False Or isTrue=True
				temp1$ = tk\value$ ;Function, or variable/object to change
				temp2$ = GetNextToken$() ;If ( then parse for function, else if = then variable
				temp3$ = ""
				If temp2$ = "(" 		;Dealing with Function
					For i=1 To last_tokcnt%-2
						temp$ = GetNextToken$()
						If temp$=")" ;Ship out parameters
							Exit
						Else
							temp3$ = temp3$ + temp$
						EndIf
					Next
					RunFunction(temp1$,temp3$)
				Else If temp2$ = "="	;Re-assigning variable
					temp3$ = GetNextToken$()
					If last_tokcnt%>3 ;THERES MORE!?!?
						For i=1 To last_tokcnt%-3
							temp3$=temp3$+GetNextToken$()
						Next
						temp3$ = ParseVariables(temp3$)
					EndIf
					SetVariable(temp1$,temp3$)
				Else If temp2$ = "."	;Object operation
					temp2$ = GetNextToken$() ;Var Name, or function name
					temp4$ = GetNextToken$()
					If temp4$ = "=" ;Setting variable
						temp3$ = GetNextToken$()
						If last_tokcnt%>5 ;THERES MORE!?!?
							For i=1 To last_tokcnt%-5
								temp3$=temp3$+GetNextToken$()
							Next
							temp3$ = ParseVariables(temp3$)
						EndIf
						SetClassVariable(temp1$,temp2$,temp3$)
						DebugOut("SETTING TO var "+temp1$+"."+temp2$+"="+temp3$)
					Else temp4$ = "("
						For i=1 To last_tokcnt%-4
							temp$ = GetNextToken$()
							If temp$=")" ;Ship out parameters
								Exit
							Else
								temp3$ = temp3$ + temp$
							EndIf
						Next
						For obj.class = Each class
							If obj\name$ = temp1$ ;Getting className$
								temp2$=obj\className$+"::"+temp2$ ;Compile the function for calling
								Exit
							EndIf
						Next
						CallFunction(temp2$,temp3$)
					EndIf
				Else					;Show error
					AddError("ParseTokens::Default - Unknown command, or incorrect format!","ExecuteScript")
				EndIf
			EndIf
	End Select
End Function

Function ParseVariables$(inp$) ;Fills variables AND Formats string concenations
	Local temp$,temp2$,found,mode%
	ClearTokens()
	AdvancedTokenize(inp$)
	For tk.token = Each token
		found=False
		For tmp.class = Each class
			If tk\value$=tmp\name$ ;Heres a class!
				If GetNextToken$()="."
					tname$=GetNextToken$()
					temp2$=FillVariable(tmp\name$+"_"+tname$,True)
					temp2$=Replace(temp2$,dqt$,"") 			;Remove quotes
					temp$ = temp$ + temp2$
					DebugOut("- INFO: Filled class variable "+tmp\name$+"."+tname$+" with "+temp2$)
					found=True
					Exit
				Else
					AddError("A period after the object name is required!","ParseVariable")
				EndIf
			EndIf
		Next
		If found=False
			temp2$ = FillVariable(tk\value$,False)	;Fill all variables
			temp2$=Replace(temp2$,dqt$,"") 			;Remove quotes
			temp$ = temp$ + temp2$
		EndIf
	Next
	If ContainsChar(temp$) ;Concenation
		temp$ = Replace(temp$,"+","") ;This should do it. No advanced parsing
	Else
		temp$ = MathToString$(temp$)
	EndIf
	Return temp$
End Function
Function ParseParameters(fname$,params$) ;Fills function variables with params
	Local toknum%=0,varnum%,found
	ClearTokens()
	SimpleTokenize(params$,",")
	For tk2.token = Each token
		If tk2\id%=-1 ;This is our simple tokens, each one is conveniently our parameter
			toknum%=toknum%+1
			varnum%=0:found=False
			For var.variable = Each variable
				If var\parent$ = fname$
					varnum% = varnum% + 1
					If varnum% = toknum% ;Alignment is crucial
						found=True
						Select var\kind%
							Case 1
								var\vStr$ = tk2\value$
							Case 2
								var\vFlt# = Float(tk2\value$)
							Case 3
								var\vInt% = Int(tk2\value$)
							Case 4
								var\vFnc$ = tk2\value$
						End Select
					EndIf
				EndIf
			Next
			If Not found
				AddError("To many parameters!","ParseParameters")
			EndIf
		EndIf
	Next
End Function

Function RunFunction(fname$,fparam$) ;Function name, and compiled parameters.
	fparam$ = ParseVariables$(fparam$) ; Fill variables now
	DebugOut("- INFO: Function '"+fname$+"' called with parameter(s) '"+fparam$+"'")
	;ORDER ENABLES OVER-RIDING! BEWARE!
	For fnc.func = Each func
		If fnc\name$=fname$
			fncE = fnc
			ParseParameters(fname$,fparam$)
			ExecuteFunction()
			Return True
		EndIf
	Next
	Select Lower(fname$) ;Pick function, will parse parameters per function
		Case "print" ;OUR FIRST FUNCTION!
			Print ">"+fparam$ ;Simple huh. Doesn't parse number or params or anything
		Default
			CallFunction(Lower(fname$),fparam$)
	End Select
End Function


;THIRD PARTY FUNCTIONS (I DIDN'T WRITE THESE BUT THEY ARE GNU)
;=============================================================
Function MathToString$(TheMath$, unit = 0, divnow = 0)
  Local MyParam$ = "*/^+-=<>&|%@", MyNumbs$ = "0123456789.", MyDivParam$ = "*/^"
  Local Ziffer$, ScanPos, MathAnswer#, MathArt$, MathPower#, OldMathPower#
  Local Scan, ScanNumber$, OldScanNumber$, MathScan$, MyScanText$
  Local bscan, bscannow, bscanhave, ScanPosA, ScanPosB
  Local deScan, deMathScan$, deMath
  Local debsScan

  TheMath$ = Lower(TheMath$)
  TheMath$ = Replace(TheMath$, "and", "&")
  TheMath$ = Replace(TheMath$, "xor", "@")
  TheMath$ = Replace(TheMath$, "or", "|")
  TheMath$ = Replace(TheMath$, "mod", "%")

  MathScan$ = Replace(TheMath$, " ", "") : debsScan = 1

  While bscan < Len(MathScan$) 
    bscan = bscan + 1 
    If Mid$(MathScan$, bscan, 1) = "(" Then 
      ScanPosA = bscan : bscannow = 1 
      While bscannow 
       If Mid$(MathScan$, bscan, 1) = "(" Then bscanhave = bscanhave + 1 
       If Mid$(MathScan$, bscan, 1) = ")" Then bscanhave = bscanhave - 1 
       If bscanhave = 0 Then bscannow = 0 
       bscan = bscan + 1 
       If KeyDown(1) Then End 
      Wend 

      ScanPosB = bscan 

      MyScanText$ = Mid$(MathScan$, ScanPosA+1, ScanPosB - ScanPosA - 2)

      MyScanText$ = MathToString$(MyScanText$, unit + 1) 
      MathScan$ = Replace(MathScan$, Mid$(MathScan$, ScanPosA, ScanPosB - ScanPosA), MyScanText$)
      bscan = 0
    End If 

    If KeyDown(1) Then End 
  Wend 

  .NewMathScan

  deMathScan$ = MathScan$

  Scan = InMid$(MathScan$, MyParam$)
  If Scan Then
    ScanNumber$ = Mid$(MathScan$, 1, Scan-1)
    MathScan$ = Mid$(MathScan$, Scan)
    MathAnswer = val2(ScanNumber$)
  Else
    Return MathScan$
  End If

  deScan = 1

  While Not MathScan$ = ""
    uu$ = MathScan$

    MathArt$ = Mid$(MathScan$, 1, 1)
    MathScan$ = Mid$(MathScan$, 2)

    If Mid$(MathScan$,1,1) = "-" Then
      MathPower# = -1
      MathScan$ = Mid$(MathScan$, 2)
    Else
      MathPower# = 1
    End If

    Scan = InMid$(MathScan$, MyParam$)
    OldScanNumber$ = ScanNumber$
    OldMathPower# = MathPower#
    ScanNumber$ = Mid$(MathScan$, 1, Scan-1)

    MathScan$ = Mid$(MathScan$, Len(ScanNumber$)+1)

    If MathArt$ = "+" Then
       MathAnswer# = MathAnswer# + (val2(ScanNumber$)*MathPower#)
    ElseIf MathArt$ = "-" Then
       MathAnswer# = MathAnswer# - (val2(ScanNumber$)*MathPower#)
    ElseIf MathArt$ = "*" Then
       MathAnswer# = (val2(OldScanNumber$)*OldMathPower#) * (val2(ScanNumber$)*MathPower#)
       If MathPower# = -1 Then
         MathScan$ = Replace(deMathScan$, OldScanNumber$ + "*-" + ScanNumber$, "-" + Str$(MathAnswer))
       ElseIf MathPower# = 1 Then
         MathScan$ = Replace(deMathScan$, OldScanNumber$ + "*" + ScanNumber$, Str$(MathAnswer))
       End If
       Goto NewMathScan
    ElseIf MathArt$ = "/" Then
       MathAnswer# = (val2(OldScanNumber$)*OldMathPower#) / (val2(ScanNumber$)*MathPower#)
       If MathPower# = -1 Then
         MathScan$ = Replace(deMathScan$, OldScanNumber$ + "/-" + ScanNumber$, "-" + Str$(MathAnswer))
       ElseIf MathPower# = 1 Then
         MathScan$ = Replace(deMathScan$, OldScanNumber$ + "/" + ScanNumber$, Str$(MathAnswer))
       End If
       Goto NewMathScan
    ElseIf MathArt$ = "^" Then
       MathAnswer# = (val2(OldScanNumber$)*OldMathPower#) ^ (val2(ScanNumber$)*MathPower#)
       If MathPower# = -1 Then
         MathScan$ = Replace(deMathScan$, OldScanNumber$ + "^-" + ScanNumber$, "-" + Str$(MathAnswer))
       ElseIf MathPower# = 1 Then
         MathScan$ = Replace(deMathScan$, OldScanNumber$ + "^" + ScanNumber$, Str$(MathAnswer))
       End If
       Goto NewMathScan
    ElseIf MathArt$ = "=" Then
       MathAnswer# = (val2(OldScanNumber$)*OldMathPower#) = (val2(ScanNumber$)*MathPower#)
       If MathPower# = -1 Then
         MathScan$ = Replace(deMathScan$, OldScanNumber$ + "=-" + ScanNumber$, "-" + Str$(MathAnswer))
       ElseIf MathPower# = 1 Then
         MathScan$ = Replace(deMathScan$, OldScanNumber$ + "=" + ScanNumber$, Str$(MathAnswer))
       End If
       Goto NewMathScan
    ElseIf MathArt$ = "<" Then
       MathAnswer# = (val2(OldScanNumber$)*OldMathPower#) < (val2(ScanNumber$)*MathPower#)
       If MathPower# = -1 Then
         MathScan$ = Replace(deMathScan$, OldScanNumber$ + "<-" + ScanNumber$, "-" + Str$(MathAnswer))
       ElseIf MathPower# = 1 Then
         MathScan$ = Replace(deMathScan$, OldScanNumber$ + "<" + ScanNumber$, Str$(MathAnswer))
       End If
       Goto NewMathScan
    ElseIf MathArt$ = ">" Then
       MathAnswer# = (val2(OldScanNumber$)*OldMathPower#) > (val2(ScanNumber$)*MathPower#)
       If MathPower# = -1 Then
         MathScan$ = Replace(deMathScan$, OldScanNumber$ + ">-" + ScanNumber$, "-" + Str$(MathAnswer))
       ElseIf MathPower# = 1 Then
         MathScan$ = Replace(deMathScan$, OldScanNumber$ + ">" + ScanNumber$, Str$(MathAnswer))
       End If
       Goto NewMathScan
    ElseIf MathArt$ = "&" Then
       MathAnswer# = (val2(OldScanNumber$)*OldMathPower#) And (val2(ScanNumber$)*MathPower#)
       If MathPower# = -1 Then
         MathScan$ = Replace(deMathScan$, OldScanNumber$ + "&-" + ScanNumber$, "-" + Str$(MathAnswer))
       ElseIf MathPower# = 1 Then
         MathScan$ = Replace(deMathScan$, OldScanNumber$ + "&" + ScanNumber$, Str$(MathAnswer))
       End If
       Goto NewMathScan
    ElseIf MathArt$ = "|" Then
       MathAnswer# = (val2(OldScanNumber$)*OldMathPower#) Or (val2(ScanNumber$)*MathPower#)
       If MathPower# = -1 Then
         MathScan$ = Replace(deMathScan$, OldScanNumber$ + "|-" + ScanNumber$, "-" + Str$(MathAnswer))
       ElseIf MathPower# = 1 Then
         MathScan$ = Replace(deMathScan$, OldScanNumber$ + "|" + ScanNumber$, Str$(MathAnswer))
       End If
       Goto NewMathScan
    ElseIf MathArt$ = "%" Then
       MathAnswer# = (val2(OldScanNumber$)*OldMathPower#) Mod (val2(ScanNumber$)*MathPower#)
       If MathPower# = -1 Then
         MathScan$ = Replace(deMathScan$, OldScanNumber$ + "%-" + ScanNumber$, "-" + Str$(MathAnswer))
       ElseIf MathPower# = 1 Then
         MathScan$ = Replace(deMathScan$, OldScanNumber$ + "%" + ScanNumber$, Str$(MathAnswer))
       End If
       Goto NewMathScan
    ElseIf MathArt$ = "@" Then
       MathAnswer# = (val2(OldScanNumber$)*OldMathPower#) Xor (val2(ScanNumber$)*MathPower#)
       If MathPower# = -1 Then
         MathScan$ = Replace(deMathScan$, OldScanNumber$ + "@-" + ScanNumber$, "-" + Str$(MathAnswer))
       ElseIf MathPower# = 1 Then
         MathScan$ = Replace(deMathScan$, OldScanNumber$ + "@" + ScanNumber$, Str$(MathAnswer))
       End If
       Goto NewMathScan
    Else
       Return "SYNTAX ERROR"
    End If
  Wend

  Return Str(MathAnswer)
End Function

Function InMid$(A$, B$) ; in benutzung
  Local C, Q, W
  C = 0
  For Q = 1 To Len(A$)
    For W = 1 To Len(B$)
      If (Mid$(A$, Q, 1) = Mid$(B$, W, 1)) And C = 0 Then C = Q : Exit
    Next
    If C>0 Then Exit
  Next
  Return C
End Function

Function val2#(sstring$)
Local temp#=0
Local decimal=0
Local sign=1
Local a
Local b
Local c
Local base=10
a=Instr(sstring$,"-",1)
If a Then negative=-1
b=Instr(sstring$,"&",a+1)
If b Then
  Select Mid$(sstring$,a+1,1)
  Case "B", "b"
    base=2
    a=b+1
  Case "O", "o"
    base=8
    a=b+1
  Case "H", "h"
    base=16
    a=b+1
  Default
    base=10
  End Select
End If
decimal=0
For b=a+1 To Len(sstring$)
  c=Asc(Mid(sstring$,b,1))
  Select c
  Case 44          ;","
    Goto skip
  Case 45          ;"-"
    sign=-sign
  Case 46          ;"."
    decimal=1
  Case 48,49,50,51,52,53,54,55,56,57   ;"0" To "9"
    temp#=temp*base+c-48
    If decimal Then decimal=decimal*base
  Case 65,66,67,68,69,60    ;"A" to "F"
    If base=16 Then
      temp#=temp#*base+c-55
      If decimal Then decimal=decimal*base
    Else
      Goto fini
    EndIf
  Case 97,98,99,100,101,102   ;"a" to "f"
    If base=16 Then
      temp#=temp#*base+c-87
      If decimal Then decimal=decimal*base
    Else
      Goto fini
    EndIf
  Default
    Goto fini
  End Select
.skip
Next
.fini
If decimal Then temp#=temp#/decimal

If negative = -1 Then
  Return -(temp#*sign)
Else
  Return temp#*sign
End If
End Function
