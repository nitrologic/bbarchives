; ID: 2297
; Author: Dabhand
; Date: 2008-08-18 18:33:32
; Title: MaxGUI text area formatting
; Description: Example on how to format a MaxGUI text area

Framework MaxGUI.MaxGUI
Import BRL.EventQueue
Import BRL.Retro
Import MaxGui.Drivers

SuperStrict 

Local window:TGadget = CreateWindow("SPad",30,20,400,300,Desktop(),WINDOW_TITLEBAR | WINDOW_MENU | WINDOW_STATUS)
Local textarea:TGadget = CreateTextArea(0,0,ClientWidth(window),ClientHeight(window),window)
SetTextAreaColor(textarea,1,81,107,True)
SetTextAreaColor(textarea,255,255,255,False)


'Menu
Local filemenu:TGadget

Const MENU_OPEN:Int=101

filemenu=CreateMenu("&File",0,WindowMenu(window))

CreateMenu"&Open",MENU_OPEN,filemenu,KEY_O,MODIFIER_COMMAND
UpdateWindowMenu window

While True 
	PollEvent()
	
	Select EventID()
		Case EVENT_GADGETACTION
			Select(EventSource())
				Case textarea
					FormatLine(textarea)
			End Select 
		
		Case EVENT_WINDOWCLOSE
			End

		Case EVENT_MENUACTION
			Select EventData()					
				Case MENU_OPEN
					Local result:String = RequestFile("Open file...","All files:*",False)
					If result <> ""
						SetTextAreaText(textArea,LoadText$(result))
						FormatDocument(textArea)
					End If 
			End Select
	End Select

Wend 
		
Function FormatLine(textArea:TGadget)
	Local cursorpos:Int
	Local currentline:Int, lineString:String 
	Local lineCharStart:Int, lineCharEnd:Int
	
	cursorpos=TextAreaCursor(textArea)
	currentLine = TextAreaLine(textArea,cursorPos)
	lineCharStart = TextAreaChar(textArea,currentLine)
	lineString = ""
		
	For Local loop:Int = lineCharStart To TextAreaLen(textArea)
		Local char:String = TextAreaText$(textArea,loop,1)
		If (TextAreaLine(textArea,loop) <> currentLine) Or (loop = TextAreaLen(textArea))
			lineCharEnd = loop-1
			Exit
		End If
		lineString = lineString + char
	Next 
		
	Local lex:TLexer = New TLexer
	If lineString <> ""
		lex.mLexLine(lineString)
	End If
		
	Local r:Int,g:Int,b:Int,flag:Int
	Local inTag:Byte = False
	
	For Local loop:Int = 0 To lex.iTokenAmount
		r = 255 ; g = 255 ; b = 255 ; flag = 0
		Local tempToken:String = lex.sTokenBank[loop]
			
		If tempToken = "<"
			r = 0 ; g = 255 ; b = 255
			inTag = True 
		End If
			
		If tempToken = ">"
			r = 0 ; g = 255 ; b = 255
			inTag = False 
		End If 
			
		If tempToken = "/" Or tempToken = "!"
			If lex.sTokenBank[loop-1] = "<"
				r = 255 ; g = 255 ; b = 50
				flag = TEXTFORMAT_BOLD
			End If
		End If
			
		For Local keyWordLoop:Int = 0 To lex.sKeywords.length-1
			If intag = True 
				If lex.sKeywords[KeyWordLoop] = tempToken
					r = 255 ; g = 255 ; b = 50
					flag = TEXTFORMAT_BOLD
					Exit 
				End If
			End If 
						
			If tempToken = "h"
				If lex.sTokenBank[loop-1] = "<" Or lex.sTokenBank[loop-1] = "/"
					r = 255 ; g = 255 ; b = 50
					flag = TEXTFORMAT_BOLD
				End If
			End If  
		Next
			
		If Left$(tempToken,1) = "~q"
			r = 50 ; g = 255 ; b = 50
		End If 
			
		For Local numbersLoop:Int = 0 To lex.sNumbers.length-1
			If lex.sNumbers[numbersLoop] = Left$(tempToken,1)
				r = 192 ; g = 192 ; b = 192
				If lex.sTokenBank[loop-1] = "h"
					r = 255 ; g = 255 ; b = 50
					flag = TEXTFORMAT_BOLD
				End If
				Exit 
			End If
		Next
			
		SetTextAreaText(textArea,tempToken,lineCharStart,Len(tempToken))
		FormatTextAreaText(textArea,r,g,b,flag,lineCharStart,Len(tempToken))
		lineCharStart = lineCharStart+Len(tempToken)
	Next
End Function

Function FormatDocument(textArea:TGadget)
	Local lineCount:Int = TextAreaLine(textArea,TextAreaLen(textArea))
	Local currentline:Int, lineString:String 
	Local lineCharStart:Int, lineCharEnd:Int
						
						
	For Local loopx:Int = 0 To lineCount	
		lineCharStart = TextAreaChar(textArea,loopx)
		lineString = ""
		
		For Local loop:Int = lineCharStart To TextAreaLen(textArea)
			Local char:String = TextAreaText$(textArea,loop,1)
			 			
			If (TextAreaLine(textArea,loop) <> loopx) Or (loopx = TextAreaLen(textArea))
				lineCharEnd = loop-1
				Exit
			End If
			lineString = lineString + char
		Next 
		
		Local lex:TLexer = New TLexer
					
		lex.mLexLine(lineString)
					
		Local r:Int,g:Int,b:Int,flag:Int
		Local inTag:Byte = False 
		
		For Local loop:Int = 0 To lex.iTokenAmount
			r = 255 ; g = 255 ; b = 255 ; flag = 0
			Local tempToken:String = lex.sTokenBank[loop]
			
			If tempToken = "<"
				r = 0 ; g = 255 ; b = 255
				inTag = True 
			End If
			
			If tempToken = ">"
				r = 0 ; g = 255 ; b = 255
				inTag = False 
			End If 
			
			If tempToken = "/" Or tempToken = "!"
				If lex.sTokenBank[loop-1] = "<"
					r = 255 ; g = 255 ; b = 50
					flag = TEXTFORMAT_BOLD
				End If
			End If
			
			For Local keyWordLoop:Int = 0 To lex.sKeywords.length-1
				If intag = True 
					If lex.sKeywords[KeyWordLoop] = tempToken
						r = 255 ; g = 255 ; b = 50
						flag = TEXTFORMAT_BOLD
						Exit 
					End If
							
					If tempToken = "h"
						If lex.sTokenBank[loop-1] = "<" Or lex.sTokenBank[loop-1] = "/"
							r = 255 ; g = 255 ; b = 50
							flag = TEXTFORMAT_BOLD
						End If  
					End If
				End If  
			Next
			
		If Left$(tempToken,1) = "~q"
			r = 50 ; g = 255 ; b = 50
		End If 
			
		For Local numbersLoop:Int = 0 To lex.sNumbers.length-1
			If lex.sNumbers[numbersLoop] = Left$(tempToken,1)
				r = 192 ; g = 192 ; b = 192
				
				If lex.sTokenBank[loop-1] = "h"
					r = 255 ; g = 255 ; b = 50
					flag = TEXTFORMAT_BOLD
				End If
				
				Exit 
			End If
		Next
			
		SetTextAreaText(textArea,tempToken,lineCharStart,Len(tempToken))
		FormatTextAreaText(textArea,r,g,b,flag,lineCharStart,Len(tempToken))
		
		lineCharStart = lineCharStart+Len(tempToken)
	Next
Next
End Function




Type TLexer
	Field sSymbols:String[] = [" ","+","-","*","^","/","%","=",">","<",":","{","}","[","]","(",")",";","&"]
	 
	Field sAlpha:String[] = ["A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","Z","B","a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z","_"]
	
	Field sNumbers:String[] = ["0","1","2","3","4","5","6","7","8","9","."]
	
	Field sKeywords:String[] = ["body","doctype","head","html","meta","address","blockquote","br","div","em","h1","h2","h3","h4","h5","h6","p","pre","strong","sub","sup","b","big","hr","i","small","tt","dd","dl","dt","li","ol","ul","a","caption","table","td","th","tr","frame","frameset","iframe","noframes","img","map","object","style","button","form","input","label","legend","option","select","textarea","script","noscript"]
	Field sTok:String = ""
	Field bInStruct:Byte, sCurrentStructIdentifier:String 

	Field iTOKENS_PER_LINE:Int = 512
	Field sTokenBank:String[iTOKENS_PER_LINE] 
	Field iTokenAmount:Int
	'Field lstTokenList:TList = New TList 
	Field lstSymbolTable:TList = New TList
	
	Field TYPE_UNKNOWN:Int = 0, TYPE_STRING:Int = 1, TYPE_IDENTIFIER:Int = 2, TYPE_COMMAND:Int = 3, TYPE_INTEGER:Int = 4, TYPE_FLOAT:Int = 5
	Field TYPE_SYMBOL:Int = 6, TYPE_VARIABLE:Int = 7, TYPE_CONSTANT:Int = 8, TYPE_FUNCTION:Int = 9,TYPE_ARRAY:Int = 10, TYPE_STRUCT:Int = 11
	
	Field DATA_UNKNOWN:Int = 0, DATA_INTEGER:Int = 1, DATA_FLOAT:Int = 2, DATA_STRING:Int = 3
	
	Method TokenAmount:Int()
		Return(iTokenAmount)
	End Method
	
	Method mLexLine:Int(sLinein:String)
		Local sTempToken:String 
		Local iLoopLine:Int, iLoopOther:Int, sChar:String
		Local bInString:Byte = False 
		
		iTokenAmount = 0 
'		lstTokenList.Clear()
		
		For iLoopOther = 0 To iTOKENS_PER_LINE-1
			sTokenBank[iLoopOther] = ""
		Next
		
		sLinein = sLinein + " "
			For iLoopLine = 1 To Len(sLinein)		
				sChar = Mid$(sLinein,iLoopLine,1)
								
				
				If sChar = "/"
					If Mid$(sLinein,iLoopLine+1,1) = "/" Then Exit 
				End If 
				
				'check for strings
				If sChar = "~q"
					sTempToken = sChar
					For iLoopLine = iLoopLine+1 To Len(sLinein)
						sChar = Mid$(sLinein,iLoopLine,1)
						If sChar = "~q" Then Exit
						sTempToken = sTempToken + sChar
					Next
					sTempToken = sTempToken + sChar
					iLoopLine = iLoopLine
					iTokenAmount = iTokenAmount + 1
					sTokenBank[iTokenAmount] = sTempToken
					sTempToken = ""  
				End If
				
				Local bDoExit:Byte
				'Check for letters such as commands or identifiers
				For iLoopOther = 0 To sAlpha.length-1
					
					If sChar = sAlpha[iLoopOther]
						sTempToken = sChar
						For iLoopLine = iLoopLine+1 To Len(sLinein) 
							  
							sChar = Mid$(sLinein,iLoopLine,1)
							For iLoopOther = 0 To sAlpha.length-1
								If sChar <> sAlpha[iLoopOther]
									bDoExit = True  
								End If
								
								If sChar = sAlpha[iLoopOther]
									sTempToken = sTempToken + sChar
									bDoExit = False
									Exit
								End If 
							Next
							If bDoExit = True
								iLoopLine = iLoopLine - 1
								iTokenAmount = iTokenAmount + 1
								sTokenBank[iTokenAmount] = Lower$(sTempToken)
								sTempToken = ""
								sChar = "" 
								Exit 
							End If
						Next
					End If
					If bDoExit = True Then Exit 
				Next
				
				'Check for numbers, including floats:-
				bDoExit = False 
				For iLoopOther = 0 To sNumbers.length-1
					
					If sChar = sNumbers[iLoopOther]
						sTempToken = sChar
						For iLoopLine = iLoopLine+1 To Len(sLinein) 
							  
							sChar = Mid$(sLinein,iLoopLine,1)
							For iLoopOther = 0 To sNumbers.length-1
								If sChar <> sNumbers[iLoopOther]
									bDoExit = True  
								End If
								
								If sChar = sNumbers[iLoopOther]
									sTempToken = sTempToken + sChar
									bDoExit = False
									Exit
								End If 
							Next
							If bDoExit = True
								iLoopLine = iLoopLine - 1
								iTokenAmount = iTokenAmount + 1
								sTokenBank[iTokenAmount] = sTempToken
								sTempToken = ""
								sChar = "" 
								Exit 
							End If
						Next
					End If
					If bDoExit = True Then Exit 
				Next
				
				For iLoopOther = 0 To sSymbols.length-1
					If sChar = sSymbols[iLoopOther]
						sTempToken = sChar
						iTokenAmount = iTokenAmount + 1
						sTokenBank[iTokenAmount] = sTempToken
						sTempToken = ""
						sChar = "" 
						Exit 
					End If
				Next 
				
				If sChar = " "
					iTokenAmount = iTokenAmount + 1
					sTokenBank[iTokenAmount] = sTempToken
					sTempToken = ""
					sChar = ""
				End If
				
				If sChar = "~t"
					iTokenAmount = iTokenAmount + 1
					sTokenBank[iTokenAmount] = sTempToken
					sTempToken = ""
					sChar = ""
				End If
			Next
			iTokenAmount = iTokenAmount - 1
		Return(iTokenAmount)
	End Method 
End Type
