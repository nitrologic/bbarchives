; ID: 2636
; Author: Yasha
; Date: 2009-12-31 22:44:06
; Title: Lexer generator
; Description: Use regular expressions to describe and automatically generate a simple lexical scanner

Write "Generating... "
BBLex_Generate("Scythe lexer.txt","BBLex_Scythe.bb")		;Change these to the desired input and output files
Print "done!"

Print ""
Print "Press any key to exit..."

WaitKey
End


Const SIZEOF_CONST = 9

Function BBLex_Generate(defFile$,lexFile$)		;Generate a .bb lexer from the definitions given in defFile and output it as lexFile
	Local dFile,dLine$,lFile,i,caseSen,userCodeOutput
	Local ruleBank,constBank,modeBank
	
	dFile=ReadFile(defFile)
	lFile=WriteFile(lexFile)
	constBank=CreateBank()
	modeBank=CreateBank(5)
	PokeInt modeBank,0,StrToBank("")
	ruleBank=CreateBank()
	
	WriteLine lFile,""
	WriteLine lFile,";This file was automatically generated using BBLex: http://www.blitzbasic.com/codearcs/codearcs.php?code=2636"
	WriteLine lFile,""
	
	While Not Eof(dFile)
		dLine=Replace(Replace(Lower(ReadLine(dFile)),Chr(9),"")," ","")
		Select dLine
			Case "caseinsensitive","case-insensitive"
				caseSen=False
			Case "casesensitive","case-sensitive"
				caseSen=True
			Case "constants:{"
				LoadConstants(constBank,dFile)
			Case "modes:{"
				LoadModes(modeBank,dFile)
			Case "rules:{"
				LoadRules(ruleBank,dFile)
			Case "code:{"
				WriteLine lFile,""
				dLine=ReadLine(dFile)
				While Not Eof(dFile)
					If Left(Trim(dLine),1)="}" Then Exit
					If userCodeOutput=False
						WriteLine lFile,""
						WriteLine lFile,Chr(9)+";User code:"
						WriteLine lFile,""
						userCodeOutput=True
					EndIf
					WriteLine lFile,dLine
					dLine=ReadLine(dFile)
				Wend
				WriteLine lFile,""
		End Select
	Wend
	
	CloseFile dFile
	ProcessRules(ruleBank,modeBank,constBank)
	
	OutputLexer(constBank,ruleBank,lFile,caseSen,userCodeOutput)
	CloseFile lFile
	
	For i=0 To BankSize(constBank)-SIZEOF_CONST Step SIZEOF_CONST
		FreeBank PeekInt(constBank,i)
		FreeBank PeekInt(constBank,i+4)
	Next
	FreeBank constBank
	For i=0 To BankSize(modeBank)-5 Step 5
		FreeBank PeekInt(modeBank,i)
	Next
	FreeBank modeBank
	
	FreeBank ruleBank
End Function

Function OutputLexer(constBank,ruleBank,lexFile,caseSen,userCodeOutput)
	Local newLine$,i,j,action$
	
	newLine=Chr(13)+Chr(10)
	
	If userCodeOutput Then WriteLine lexFile,newLine+newLine+Chr(9)+";Generated code:"
	WriteLine lexFile,newLine+"Include "+Chr(34)+"BBLex_Functions.bb"+Chr(34)+newLine
	
	If BankSize(constBank)
		For i=0 To BankSize(constBank)-SIZEOF_CONST Step SIZEOF_CONST
			If PeekByte(constBank,i+8)=True
				WriteLine lexFile,"Const "+BankToStr(PeekInt(constBank,i))+" = "+BankToStr(PeekInt(constBank,i+4))
			EndIf
		Next
		WriteLine lexFile, ""
	EndIf
	
	WriteLine lexFile,"Function BBLex_ScanData(sBank)"
	WriteLine lexFile,Chr(9)+"Local rBank, mBank, tBank, cPtr"+Chr(9)+newLine+Chr(9)+"Local token$, cMatch$, rID, i, cMode"+newLine
	WriteLine lexFile,Chr(9)+"rBank = BBLex_InitRegexen()"+newLine+Chr(9)+"mBank = BBLex_InitModes()"
	WriteLine lexFile,Chr(9)+"tBank = CreateBank()"+newLine
	WriteLine lexFile,Chr(9)+"While cPtr < BankSize(sBank)"+newLine+Chr(9)+Chr(9)+"token = "+Chr(34)+Chr(34)+newLine
	
	WriteLine lexFile,Chr(9)+Chr(9)+"For i = 0 to "+((BankSize(ruleBank)/12)-1)
	WriteLine lexFile,Chr(9)+Chr(9)+Chr(9)+"If BBLex_ModeMatch(i, mBank, cMode)"
	WriteLine lexFile,Chr(9)+Chr(9)+Chr(9)+Chr(9)+"cMatch = Regex_Match(Object.RegEx_Node(PeekInt(rBank, i * 4)), sBank, cPtr)"
	WriteLine lexFile,Chr(9)+Chr(9)+Chr(9)+Chr(9)+"If Len(cMatch) > Len(token) Then token = cMatch : rID = i"
	WriteLine lexFile,Chr(9)+Chr(9)+Chr(9)+"EndIf"+newLine+Chr(9)+Chr(9)+"Next"+newLine
	
	WriteLine lexFile,Chr(9)+Chr(9)+"If token = "+Chr(34)+Chr(34)+newLine+Chr(9)+Chr(9)+Chr(9)+"cPtr = cPtr + 1"
	WriteLine lexFile,Chr(9)+Chr(9)+"Else"+newLine+Chr(9)+Chr(9)+Chr(9)+"Select rID"
	
	For i=0 To ((BankSize(ruleBank)/12)-1)
		WriteLine lexFile,Chr(9)+Chr(9)+Chr(9)+Chr(9)+"Case "+i
		action=BankToStr(PeekInt(ruleBank,i*12+8))
		Select Lower(Left(action,1))
			Case "s"
				WriteLine lexFile,Chr(9)+Chr(9)+Chr(9)+Chr(9)+Chr(9)+"BBLex_StoreToken tBank, "+Trim(Mid(action,6))+", token"
			Case "t"
				WriteLine lexFile,Chr(9)+Chr(9)+Chr(9)+Chr(9)+Chr(9)+"BBLex_StoreType tBank, "+Trim(Mid(action,6))
			Case "m"
				WriteLine lexFile,Chr(9)+Chr(9)+Chr(9)+Chr(9)+Chr(9)+"cMode = "+Trim(Mid(action,5))
			Case "{"
				WriteLine lexFile,Chr(9)+Chr(9)+Chr(9)+Chr(9)+Chr(9)+Mid(action,2)
		End Select
	Next
	WriteLine lexFile,Chr(9)+Chr(9)+Chr(9)+"End Select"
	WriteLine lexFile,Chr(9)+Chr(9)+Chr(9)+"cPtr = cPtr + Len(token)"+newLine+Chr(9)+Chr(9)+"EndIf"+newLine+Chr(9)+"Wend"+newLine
	WriteLine lexFile,Chr(9)+"BBLex_DeleteRegexen(rBank)"+newLine+Chr(9)+"BBLex_ClearModes(mBank)"
	WriteLine lexFile,Chr(9)+"FreeBank sBank"+newLine
	WriteLine lexFile,Chr(9)+"Return tBank"+newLine+"End Function"+newLine
	
	WriteLine lexFile,"Function BBLex_InitRegexen()"
	WriteLine lexFile,Chr(9)+"Local regexBank"+newLine
	WriteLine lexFile,Chr(9)+"regexBank = CreateBank("+(BankSize(ruleBank)/3)+")"+newLine
	
	For i=0 To BankSize(ruleBank)/12-1
		WriteLine lexFile,Chr(9)+"PokeInt regexBank, "+(i*4)+", Handle(Regex_Parse("+ExpandQuotes(BankToStr(PeekInt(ruleBank,i*12+4)))+", "+StrFromBool(caseSen)+"))"
	Next
	
	WriteLine lexFile,newLine+Chr(9)+"Return regexBank"+newLine+"End Function"+newLine
	
	WriteLine lexFile,"Function BBLex_InitModes()"
	WriteLine lexFile,Chr(9)+"Local modeBank"+newLine+Chr(9)+"modeBank = CreateBank("+(BankSize(ruleBank)/3)+")"+newLine
	
	For i = 0 To BankSize(ruleBank) / 12 - 1
		If BankSize(PeekInt(ruleBank, i * 12))
			WriteLine lexFile, Chr(9)+"PokeInt modeBank, " + i * 4 + ", CreateBank("+BankSize(PeekInt(ruleBank, i * 12)) + ")"
			For j = 0 To BankSize(PeekInt(ruleBank, i * 12)) - 4 Step 4
				WriteLine lexFile, Chr(9) + "PokeInt PeekInt(modeBank, "+i*4+"), "+j+", " + PeekInt(PeekInt(ruleBank, i * 12), j)
			Next
		Else
			WriteLine lexFile, Chr(9) + "PokeInt modeBank, " + i * 4 + ", 0"
		EndIf
	Next
	WriteLine lexFile,newLine+Chr(9)+"Return modeBank"+newLine+"End Function"+newLine
End Function

Function ExpandQuotes$(s$)
	Local i
	
	If s = Chr(34) Then Return "Chr(34)"
	
	Local l$ = Left(s, 1), r$ = Right(s, 1), m$ = Mid(s, 2, Len(s) - 2)
	
	If l = Chr(34) Then l = "Chr(34) + " + Chr(34) : Else l = Chr(34) + l
	If r = Chr(34) Then r = Chr(34) + " + Chr(34)" : Else r = r + Chr(34)
	m = Replace(m, Chr(34), Chr(34) + " + Chr(34) + " + Chr(34))
	
	Return l + m + r
End Function

Function StrFromBool$(b)
	If b Then Return "True" Else Return "False"
End Function

Function LoadConstants(constBank,dFile)
	Local dLine$,cName$,cValue$,i, export
	
	While Not Eof(dFile)
		dLine=Trim(ReadLine(dFile))
		If Left(dLine,1)="}" Then Exit
		
		If dLine<>""
			If Left(dLine,1)<>";"
				For i=1 To Len(dLine)
					If i>1
						If Mid(dLine,i-1,1)<>"\"
							If Asc(Mid(dLine,i,1))<=32 Then Exit
						EndIf
					EndIf
					cName=cName+Mid(dLine,i,1)
				Next
				
				dLine=Trim(Mid(dLine,i+1))
				
				For i=1 To Len(dLine)
					If i>1
						If Mid(dLine,i-1,1)<>"\"
							If Asc(Mid(dLine,i,1))<=32 Then Exit
						EndIf
					EndIf
					cValue=cValue+Mid(dLine,i,1)
				Next
				dLine=Trim(Mid(dLine,i))
				
				ResizeBank constBank,BankSize(constBank)+SIZEOF_CONST
				PokeInt constBank,BankSize(constBank)-SIZEOF_CONST,StrToBank(cName)
				PokeInt constBank,BankSize(constBank)-(SIZEOF_CONST-4),StrToBank(cValue)
				PokeByte constBank,BankSize(constBank)-(SIZEOF_CONST-8),(Lower(Left(dLine,6))="export")
				cName=""
				cValue=""
			EndIf
		EndIf
	Wend
End Function

Function LoadModes(modeBank,dFile)
	Local dLine$,mName$,i
	
	While Not Eof(dFile)
		dLine=Trim(ReadLine(dFile))
		If Left(dLine,1)="}" Then Exit
		
		If dLine<>""
			If Left(dLine,1)<>";"
				For i=1 To Len(dLine)
					If i>1
						If Mid(dLine,i-1,1)<>"\"
							If Asc(Mid(dLine,i,1))<=32 Then Exit
						EndIf
					EndIf
					mName=mName+Mid(dLine,i,1)
				Next
				
				dLine=Trim(Mid(dLine,i+1))
				
				ResizeBank modeBank,BankSize(modeBank)+5
				PokeInt modeBank,BankSize(modeBank)-5,StrToBank(mName)
				
				If Lower(Left(dLine,2))="in" Then PokeByte modeBank,BankSize(modeBank)-1,1:Else PokeByte modeBank,BankSize(modeBank)-1,0
				mName=""
			EndIf
		EndIf
	Wend
End Function

Function LoadRules(ruleBank,dFile)
	Local dLine$,cPtr,mode$,rule$,action$
	
	While Not Eof(dFile)
		dLine=Trim(ReadLine(dFile))
		If Left(dLine,1)="}" Then Exit
		
		If dLine<>""
			If Left(dLine,1)<>";"
				mode=""
				
				If Left(dLine,1)="<"
					cPtr=2
					While Mid(dLine,cPtr,1)<>">"
						If Asc(Mid(dLine,cPtr,1))>32 Then mode=mode+Mid(dLine,cPtr,1)
						cPtr=cPtr+1
					Wend
					dLine=Trim(Mid(dLine,cPtr+1))
				EndIf
				
				rule=""
				For cPtr=1 To Len(dLine)
					If cPtr>2
						If Mid(dLine,cPtr-1,1)<>"\"
							If Asc(Mid(dLine,cPtr,1))<=32 Then Exit
						ElseIf Mid(dLine,cPtr-2,2)="\\"
							If Asc(Mid(dLine,cPtr,1))<=32 Then Exit	;If that backslash was part of the pattern
						EndIf
					ElseIf cPtr=2
						If Left(dLine,1)<>"\"
							If Asc(Mid(dLine,cPtr,1))<=32 Then Exit
						EndIf
					EndIf
					rule=rule+Mid(dLine,cPtr,1)
				Next
				dLine=Trim(Mid(dLine,cPtr))
				
				action=dLine
				If Left(dLine,1)="{"
					While Not Eof(dFile)
						dLine=Trim(ReadLine(dFile))
						If Left(dLine,1)="}" Then action=action+Chr(13)+Chr(10):Exit
						
						action=action+Chr(13)+Chr(10)+dLine
					Wend
				EndIf
				
				ResizeBank ruleBank,BankSize(ruleBank)+12
				PokeInt ruleBank,BankSize(ruleBank)-12,StrToBank(mode)
				PokeInt ruleBank,BankSize(ruleBank)-8,StrToBank(rule)
				PokeInt ruleBank,BankSize(ruleBank)-4,StrToBank(action)
			EndIf
		EndIf
	Wend
End Function

Function ProcessRules(ruleBank,modeBank,constBank)
	Local r,c,m,mode$,rule$,action$
	
	For r=0 To BankSize(ruleBank)-12 Step 12
		mode=BankToStr(PeekInt(ruleBank,r))
		FreeBank PeekInt(ruleBank,r)
		PokeInt ruleBank,r,CreateBank()
		
		While mode<>""
			If Right(mode,1)=","
				ResizeBank PeekInt(ruleBank,r),BankSize(PeekInt(ruleBank,r))+4
				PokeInt PeekInt(ruleBank,r),BankSize(PeekInt(ruleBank,r))-4,0
				mode=Trim(Left(mode,Len(mode)-1))
			EndIf
			If Instr(mode,",")>0
				For m=0 To BankSize(modeBank)-5 Step 5
					If Left(mode,Instr(mode,",")-1)=BankToStr(PeekInt(modeBank,m))
						ResizeBank PeekInt(ruleBank,r),BankSize(PeekInt(ruleBank,r))+4
						If PeekByte(modeBank,m+4)=1
							PokeInt PeekInt(ruleBank,r),BankSize(PeekInt(ruleBank,r))-4,-(m/5)
						Else
							PokeInt PeekInt(ruleBank,r),BankSize(PeekInt(ruleBank,r))-4,m/5
						EndIf
					EndIf
				Next
				mode=Mid(mode,Instr(mode,",")+1)
			Else
				For m=0 To BankSize(modeBank)-5 Step 5
					If mode=BankToStr(PeekInt(modeBank,m))
						ResizeBank PeekInt(ruleBank,r),BankSize(PeekInt(ruleBank,r))+4
						If PeekByte(modeBank,m+4)=1
							PokeInt PeekInt(ruleBank,r),BankSize(PeekInt(ruleBank,r))-4,-(m/5)
						Else
							PokeInt PeekInt(ruleBank,r),BankSize(PeekInt(ruleBank,r))-4,m/5
						EndIf
					EndIf
				Next
				mode=""
			EndIf
		Wend
		
		rule=BankToStr(PeekInt(ruleBank,r+4))
		FreeBank PeekInt(ruleBank,r+4)
		For c=0 To BankSize(constBank)-SIZEOF_CONST Step SIZEOF_CONST
			rule=Replace(rule,"{"+BankToStr(PeekInt(constBank,c))+"}",BankToStr(PeekInt(constBank,c+4)))
		Next
		PokeInt ruleBank,r+4,StrToBank(rule)
		
		action=BankToStr(PeekInt(ruleBank,r+8))
		If Left(action,1)<>"{"
			FreeBank PeekInt(ruleBank,r+8)
			If Lower(Left(action,5))="store"
				For c=0 To BankSize(constBank)-SIZEOF_CONST Step SIZEOF_CONST
					If PeekByte(constBank, c + 8) = False
						action="store "+Replace(Mid(action,6),"{"+BankToStr(PeekInt(constBank,c))+"}",BankToStr(PeekInt(constBank,c+4)))
					EndIf
				Next
			ElseIf Lower(Left(action,4))="type"
				For c=0 To BankSize(constBank)-SIZEOF_CONST Step SIZEOF_CONST
					If PeekByte(constBank, c + 8) = False
						action="type "+Replace(Mid(action,5),"{"+BankToStr(PeekInt(constBank,c))+"}",BankToStr(PeekInt(constBank,c+4)))
					EndIf
				Next
			ElseIf Lower(Left(action,4))="mode"
				For m=0 To BankSize(modeBank)-5 Step 5
					If PeekByte(modeBank,m+4)=1
						action=Replace(action,"<"+BankToStr(PeekInt(modeBank,m))+">",-(m/5))
					Else
						action=Replace(action,"<"+BankToStr(PeekInt(modeBank,m))+">",m/5)
					EndIf
				Next
			EndIf
			PokeInt ruleBank,r+8,StrToBank(action)
		EndIf
	Next
End Function

Function StrToBank(s$)		;Return a bank containing the binary value of the given string
	Local i,bank
	bank=CreateBank(Len(s))
	For i=0 To Len(s)-1
		PokeByte bank,i,Asc(Mid(s,i+1,1))
	Next
	Return bank
End Function

Function BankToStr$(bank)		;Return a string containing the ASCII value of the given bank
	Local i,s$
	For i=0 To BankSize(bank)-1
		s=s+Chr(PeekByte(bank,i))
	Next
	Return s
End Function

;~IDEal Editor Parameters:
;~F#E#4F#9D#AB#AF#D8#F6#12E#17B#184
;~C#Blitz3D
