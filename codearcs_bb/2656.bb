; ID: 2656
; Author: Yasha
; Date: 2010-02-22 19:47:16
; Title: Super-simple C-style preprocessor
; Description: Use #define, #ifdef, #include etc. with any file

;General-purpose C-like directive processor
;==========================================


;Not very useful by itself, but you can plug it in to a code processor or script compiler easily


;Commands:
;	#define NAME CONSTANT - replace all occurences of NAME with CONSTANT. Cannot refer to other #defines
;	#undef NAME - remove a #defined constant so it isn't replaced any more
;	#if EXPRESSION - either compare a constant to another constant (literal or #defined) or assert that it's nonzero
;	#ifdef NAME - include the following block of code only if NAME has been #defined
;	#ifndef NAME - include the following block of code only if NAME has not been #defined
;	#elif EXPRESSION - short for ElseIf
;	#else - if the last check evaluated false, do this instead
;	#endif - mark the end of a conditional inclusion block. These are allowed to nest
;	#include FILE - copy the entire contents of the specified filename (quotemarks optional)
;	#option OPTION - does nothing: add language-relevant options here
;	#error ERR - issues error ERR via error handler. Best used with a condition check...!

;Notes:
;	- #define does not create macros, only simple substitution constants
;	- commands must be the first text on their line
;	- any text after a command will potentially be considered part of it, or at best ignored


;Replace these with tokens that match your language
Const PP_LINECOMMENT$ = "//"		;Single-line comment
Const PP_BLOCKCOMMENTSTART$ = "/*"	;Start of block-comment
Const PP_BLOCKCOMMENTEND$ = "*/"	;End of block-comment
Const PP_ESCAPECHR$ = "\"			;Escape character (to allow " to appear in strings)

Type PP_Macro			;As defined with #define. No regular expressions, just cut and paste
	Field tok$		;Constant token to use in source
	Field def$		;Definition
End Type

Function PP_Preprocess(inputFilename$,outputFilename$)		;Loads the source from file and applies preprocessor commands
	Local m.PP_Macro,n.PP_Macro,pos,ptr,i
	Local com,sep,skip,debugout,srcline$,remain$
	Local inquote,incomment,cfile,filelist=CreateBank(4)
	Local sourceBank=CreateBank(4),iStack=CreateBank(0)
	
	cfile=ReadFile(inputFilename)
	PokeInt(filelist,0,cfile)
	Repeat
		srcline=ReadLine(cfile)
		
		If Left(Trim(srcline),1)="#"
			srcline=Replace(srcline,Chr(9)," ")
			com=Instr(srcline,PP_BLOCKCOMMENTSTART)
			If com>0
				remain=Right(srcline,Len(srcline)-(com-1))		;Rescue comments with /* as otherwise there will be parse errors later
				
				ResizeBank sourceBank,BankSize(sourceBank)+Len(remain)+2
				For ptr=0 To Len(remain)-1
					PokeByte sourceBank,pos+ptr,Asc(Mid(remain,ptr+1,1))
				Next
				PokeShort sourceBank,pos+ptr,$A0D		;CR+LF - end of line
				pos=pos+ptr+2
				
				srcline=Left(srcline,com-1)
			EndIf
			
			com=Instr(srcline,PP_LINECOMMENT)
			If com>0 Then srcline=Left(srcline,com-1)
			srcline=Trim(srcline)+" "	;Force control line to end in a space
			
			Select True
				Case Lower(Left(srcline,8))="#define "				;Add a macro
					If skip=0
						m=New PP_Macro
						srcline=Trim(Mid(srcline,8))
						sep=Instr(srcline," ")
						
						If sep
							m\tok=Trim(Left(srcline,sep))
							m\def=Trim(Mid(srcline,sep+1))
						Else
							m\tok=srcline
							m\def=""
						EndIf
						
						For n=Each PP_Macro
							If n<>m And m\tok=n\tok Then PP_Error("Token "+m\tok+" is already defined")
						Next
					EndIf
					
				Case Lower(Left(srcline,7))="#undef "				;Remove a macro
					If skip=0
						srcline=Trim(Mid(srcline,7))
						For m=Each PP_Macro
							If m\tok=srcline Then Delete m:Exit
						Next
						If m=Null Then PP_Error("token "+Chr(34)+srcline+Chr(34)+" does not exist")
					EndIf
					
				Case Lower(Left(srcline,4))="#if "					;Conditional compilation by value
					srcline=Trim(Mid(srcline,4))
					If skip=0
						ResizeBank iStack,BankSize(iStack)+1
						skip=skip+(Not(PP_EvalDirective(srcline)))
						PokeByte iStack,BankSize(iStack)-1,Not(skip)
					Else
						skip=skip+1
					EndIf
					
				Case Lower(Left(srcline,7))="#ifdef "				;Conditional compilation by definition
					srcline=Trim(Mid(srcline,7))
					If skip=0
						ResizeBank iStack,BankSize(iStack)+1
						skip=skip+1
						For m=Each PP_Macro
							If m\tok=srcline Then skip=skip-1:Exit
						Next
						PokeByte iStack,BankSize(iStack)-1,Not(skip)
					Else
						skip=skip+1
					EndIf
					
				Case Lower(Left(srcline,8))="#ifndef "				;Conditional lack of compilation by definition
					srcline=Trim(Mid(srcline,8))
					If skip=0
						ResizeBank iStack,BankSize(iStack)+1
						For m=Each PP_Macro
							If m\tok=srcline Then skip=skip+1:Exit
						Next
						PokeByte iStack,BankSize(iStack)-1,Not(skip)
					Else
						skip=skip+1
					EndIf
					
				Case Lower(Left(srcline,6))="#elif "				;Alternate condition check
					If skip=1
						srcline=Trim(Mid(srcline,6))
						If PeekByte(iStack,BankSize(iStack)-1)=0	;Only try if this #if level hasn't done anything yet
							If PP_EvalDirective(srcline)
								PokeByte iStack,BankSize(iStack)-1,1
								skip=0
							EndIf
						Else
							skip=1
						EndIf
					EndIf
					
				Case Lower(Left(srcline,6))="#else "				;Default condition
					If skip=1
						If PeekByte(iStack,BankSize(iStack)-1)=0 Then skip=0
					ElseIf skip=0
						skip=1
					EndIf
					
				Case Lower(Left(srcline,7))="#endif "				;End of conditional compilation block
					If skip>0 Then skip=skip-1
					If skip=0
						If BankSize(iStack) Then ResizeBank iStack,BankSize(iStack)-1
					EndIf
					
				Case Lower(Left(srcline,9))="#include "				;Include files
					If skip=0
						srcline=Trim(Mid(srcline,9))
						If Left(srcline,1)=Chr(34) And Right(srcline,1)=Chr(34) Then srcline=Mid(srcline,2,Len(srcline)-2)	;Cut off quote marks
						cfile=ReadFile(srcline)
						If cfile=0 Then PP_Error "file "+Chr(34)+srcline+Chr(34)+" does not exist"
						ResizeBank(filelist,BankSize(filelist)+4)
						PokeInt filelist,BankSize(filelist)-4,cfile
					EndIf
					
				Case Lower(Left(srcline,8))="#option "				;Set compilation options
					If skip=0
						srcline=Trim(Mid(srcline,8))
						Select srcline
							;Left empty for future expansion
							Default:PP_Error("unrecognised compiler option")
						End Select
					EndIf
					
				Case Lower(Left(srcline,7))="#error "
					If skip=0
						srcline=Trim(Mid(srcline,7))
						PP_Error(srcline)
					EndIf
					
				Default
					PP_Error("unrecognised preprocessor command: "+Chr(34)+srcline+Chr(34))
			End Select
		Else
			If skip=0
				If First PP_Macro<>Null		;Replace #defined tokens
					inquote=0
					For ptr=1 To Len(srcline)
						If Mid(srcline,ptr,2)=PP_LINECOMMENT Then Exit
						If Mid(srcline,ptr,1)=Chr(34)
							If inquote=0
								inquote=True
							Else
								If Mid(srcline,ptr-1,1)<>PP_ESCAPECHR Then inquote=False
							EndIf
						EndIf
						If Mid(srcline,ptr,2)=PP_BLOCKCOMMENTSTART Then incomment=True:ElseIf Mid(srcline,ptr,2)=PP_BLOCKCOMMENTEND Then incomment=False
						If inquote=False And incomment=False
							For m=Each PP_Macro
								If ptr>1 Then i=Asc(Mid(srcline,ptr-1,1)):Else i=0
								If Not((i>47 And i<58) Or (i>64 And i<91) Or i=95 Or (i>96 And i<123))
									i=Asc(Mid(srcline,ptr+Len(m\tok),1))
									If Not((i>47 And i<58) Or (i>64 And i<91) Or i=95 Or (i>96 And i<123))
										If Mid(srcline,ptr,Len(m\tok))=m\tok
											srcline=Left(srcline,ptr-1)+m\def+Right(srcline,Len(srcline)-(ptr-1)-Len(m\tok))
										EndIf
									EndIf
								EndIf
							Next
						EndIf
					Next
				EndIf
				
				ResizeBank sourceBank,BankSize(sourceBank)+Len(srcline)+2
				For ptr=0 To Len(srcline)-1
					PokeByte sourceBank,pos+ptr,Asc(Mid(srcline,ptr+1,1))
				Next
				PokeShort sourceBank,pos+ptr,$A0D		;CR+LF - end of line
				pos=pos+ptr+2
			EndIf
		EndIf
		
		If Eof(cfile)
			CloseFile cfile
			ResizeBank filelist,BankSize(filelist)-4
			If BankSize(filelist)=0 Then Exit:Else cfile=PeekInt(filelist,BankSize(filelist)-4)
		EndIf
	Forever
	
	;Dump out a copy of the source to be read by the tokeniser
	debugout=WriteFile(outputFilename)
	WriteBytes(sourceBank,debugout,0,BankSize(sourceBank))
	CloseFile debugout
	
	FreeBank filelist		;Tidy up
	FreeBank sourceBank
	FreeBank iStack
	Delete Each PP_Macro
End Function

Function PP_EvalDirective(directive$)	;Evaluate an #if directive
	Local lArg$,op$,rArg$,i,m.PP_Macro
	
	i=Instr(directive," ")
	lArg=Trim(Left(directive,i))
	directive=Trim(Mid(directive,i))
	
	i=Instr(directive," ")
	op=Trim(Left(directive,i))
	rArg=Trim(Mid(directive,i))
	
	For m=Each PP_Macro
		If m\tok=lArg Then lArg=m\def
		If m\tok=rArg Then rArg=m\def
	Next
	
	Select op
		Case "=","=="		;On tests for equality, strings will be compared directly so "6.0" != "6" - must be converted if floats are involved
			If Instr(lArg,".") Or Instr(rArg,".")
				Return (Float(lArg) = Float(rArg))
			Else
				Return lArg=rArg
			EndIf
			
		Case "<>","!="
			If Instr(lArg,".") Or Instr(rArg,".")
				Return (Float(lArg) <> Float(rArg))
			Else
				Return lArg <> rArg
			EndIf
			
		Case "<"			;On tests that imply numerical value, strings will be automagically converted!
			Return lArg < rArg
			
		Case ">"
			Return lArg > rArg
			
		Case "<="
			Return lArg <= rArg
			
		Case ">="
			Return lArg >= rArg
			
		Case ""
			Return lArg<>0
			
	End Select
End Function

Function PP_Error(errorMessage$)		;Replace calls to this with your compiler's main error handler function
	Print "ERROR: "+errorMessage
	WaitKey:End
End Function
