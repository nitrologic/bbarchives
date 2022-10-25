; ID: 1103
; Author: Nilium
; Date: 2004-07-05 23:54:12
; Title: APPP - Almost Pointless Preprocessor
; Description: Almost Pointless PreProcessor - A PreProcessor for BlitzBasic that gives you basic OOPL features

;; In order to use this, you have to compile it and pass a file to it via the commandline (or set it in your IDE, your call).

Graphics 600,400,32,2

Const UseDelimitor=0
Global Quiet = 0

Global CurDef=0
Global DefCount
Dim Defined(511)
Defined(0) = 1

Type Def
	Field Name$
	Field Index
End Type

Type tType
	Field Name$
	Field Fields$
	Field Inherits$
End Type

Type tTypeObject
	Field Name$
	Field TypeName$
End Type

Type tTypeObjectField
	Field Name$
	Field TypeName$
End Type

Type Method
	Field Contents$
	Field Name$
	Field NameArgs$
	Field T.tType
	Field Body$
	Field Arguments%
End Type

Global NoParseOpen,T

Fnt = LoadFont( "Courier New", 16, 0, 0 )
SetFont Fnt
CMDL$ = CommandLine$()
;;If Quiet = 0 Then Print CMDL$
QuoteA = 0
QuoteB = 0

For N = 1 To Len(CMDL)
	C = Asc(Mid(CMDL,N,1))
	If C = 34 Then
		Quotes = Quotes + 1
		If Quotes = 1 Then
			QuoteA = N
		ElseIf Quotes = 2 Then
			QuoteB = N
			Exit
		EndIf
	EndIf
Next

If QuoteA = 0 And QuoteB = 0 Then End

FileIn$ = Mid(CMDL,QuoteA+1,QuoteB-QuoteA-1)
Arguments$ = Left(Cmdl,QuoteA-1)
Arguments=Arguments+Right(Cmdl,Len(cmdl)-QuoteB)

If Instr(Arguments,"+q") Or Instr(Arguments,"-q") Then Quiet = 1

Global Directory$ = Replace(FileIn$,"/","\")

For N = 1 To Len(Directory)
	If Mid(Directory,N,1) = "\" Then
		CDKA = N
	EndIf
Next

Directory = Left(Directory,CDKA)
ChangeDir Directory

If FileType(FileIn) <> 1 Then End
If Lower(Right(FileIn,3)) <> ".bc" Then NoRootParse = 1

FILE = ReadFile(FileIn)
If Not FILE Then End
	
TFile = WriteFile("Temp.A")
If Not TFile Then CloseFile(FILE) End

IncludeFile FILE,TFile,NoRootParse

LastMethod.Method = Last Method
For m.Method = Each Method
	ConTrue = 0
	CName$ = Trim(Left(m\Name,Instr(m\Name,"(")-1))
	If Lower(CName) = Lower(m\T\Name) Then
		ConTrue = 1
	EndIf
	WriteLine TFile,""
	InMeth = Instr(m\Name,"(")
	OutMeth = Instr(m\Name,")")
	NeedComma = 0
	If ConTrue = 1
		WriteLine TFile,"Function "+m\T\Name+Replace(m\Name,"(","."+m\T\Name+"(" )
		WriteLine TFile,"this."+m\T\Name+" = New "+m\T\Name+Chr(10)+m\Contents+"Return this"+Chr(10)+"End Function"
	Else
		For N2 = InMeth To OutMeth
			AC = Asc(Upper(Mid(m\Name,N2,1)))
			If (AC >= 64 And AC <= 90) Or (AC >= 48 And AC <= 57) Or (AC = 34) Then
				NeedComma = 1
				Exit
			EndIf
		Next
		If NeedComma Then
			WriteLine TFile,"Function "+m\T\Name+Replace(m\Name,"(","(this."+m\T\Name+", ")
		Else
			WriteLine TFile,"Function "+m\T\Name+Replace(m\Name,"(","(this."+m\T\Name)
		EndIf
		WriteLine TFile,m\Contents+"End Function"
	EndIf
Next

CloseFile FILE
CloseFile TFILE
FILE = OpenFile("Temp.A")
	
FileOut$ = Left(FileIn,Len(FileIn)-3)+".bb_p"
OUT = WriteFile(FileOut)
If Not OUT Then CloseFile(FILE) End
NCMDL$ = Replace(CMDL,FileIn,FileOut)
Local InheritOpen,Inherited.tType
Dim CurrentClose(1023)
While Not Eof(FILE)
	If Quiet = 0 Then
		PercentDone% = (Float(FilePos(FILE))/Float(FileSize("Temp.A")))*100
		AppTitle PercentDone+"%"
	EndIf
	
	Addition$ = ""
	Eol = 0
	SkipThisLine = 0
	StringOpen = 0
	CL$ = ReadLine(FILE)
	LC = LC + 1
	If Upper(Left(CL$,Len(";; NOPARSE"))) = ";; NOPARSE" Then NOPARSE = NoParse + 1 Eol = 1 SkipThisLine = 1
	If Upper(Left(CL$,Len(";; OPENPARSE"))) = ";; OPENPARSE" Then NoParse = NoParse - 1 Eol = 1 SkipThisLine = 1
	If StopNextLine = 1 Then Stop
	StopNextLine = 0
	If Upper(Left(CL$,Len(";; PARSESTOP"))) = ";; PARSESTOP" Then StopNextLine = 1 SkipThisLine = 1 Eol = 1
	If NOPARSE = 0 And SkipThisLine = 0 Then
		If InheritOpen Then
			WriteLine OUT,Inherited\Fields
			InheritOpen = 0
			Inherited = Null
		EndIf
		CommentStart = Instr(CL$,"//")
		If CommentStart Then CL$ = Left(CL$,CommentStart-1)
		CL$ = Trim(Replace(Replace( CL$, "	", "" ),"\"+Chr(34),Chr(3)+Chr(4)))
		
		For N = 1 To Len(CL$)
			KL$ = Mid(CL$,N,2)
			
			If Left(KL$,1) = Chr(34) Then StringOpen = Not StringOpen
				
			If N = Len(CL$) Then
				If Right(KL$,1) = Chr(34) Then StringOpen = Not StringOpen
			EndIf
			
			If KL$ = "//" And StringOpen = False Then
				CL$ = Left(CL$,N-1)
				
			ElseIf KL$ = "/*" And StringOpen = False
				CL$ = Left(CL$,N-1)
				CommentOpen = CommentOpen + 1
				
			ElseIf KL$ = "*/" And StringOpen = False
				CL$ = Right(CL$,Len(CL$)-(N+1))
				CommentOpen = CommentOpen - 1
				
			ElseIf CommentOpen > 0 Then
				CL$ = Right(CL$,Len(CL$)-1)
				
			ElseIf KL$ = "=="  And StringOpen = False Then
				CL$ = Left(CL$,N-1)+"= "+Right(CL$,Len(CL$)-(N+2))
				
			ElseIf KL$ = "!="  And StringOpen = False Then
				CL$ = Left(CL$,N-1)+"<> "+Right(CL$,Len(CL$)-(N+2))
				
			ElseIf KL$ = "++"  And StringOpen = False Then
				VarName$ = Trim(Left(CL$,N-1))
				CL$ = VarName+" = "+VarName+" + 1"
				Eol = 1
				
			ElseIf KL$ = "--"  And StringOpen = False Then
				VarName$ = Trim(Left(CL$,N-1))
				CL$ = VarName+" = "+VarName+" - 1"
				Eol = 1
				
			ElseIf KL$ = "+="  And StringOpen = False Then
				VarName$ = Trim(Left(CL$,N-1))
				If Right(CL$,1) = ";" Then
					CL$ = Left(CL$,Len(CL$)-1)
					Addition$ = ";"
				EndIf
				CL$ = VarName+" = "+VarName+" + "+Trim(Right(Cl$,Len(CL$)-(N+1))+Addition$)
				
			ElseIf KL$ = "-="  And StringOpen = False Then
				VarName$ = Trim(Left(CL$,N-1))
				If Right(CL$,1) = ";" Then
					CL$ = Left(CL$,Len(CL$)-1)
					Addition$ = ";"
				EndIf
				CL$ = VarName+" = "+VarName+" - "+Trim(Right(Cl$,Len(CL$)-(N+1))+Addition$)
				
			ElseIf KL$ = "*="  And StringOpen = False Then
				VarName$ = Trim(Left(CL$,N-1))
				If Right(CL$,1) = ";" Then
					CL$ = Left(CL$,Len(CL$)-1)
					Addition$ = ";"
				EndIf
				CL$ = VarName+" = "+VarName+" * ("+Trim(Right(Cl$,Len(CL$)-(N+1)))+" )"+Addition$
				
			ElseIf KL$ = "/="  And StringOpen = False Then
				VarName$ = Trim(Left(CL$,N-1))
				If Right(CL$,1) = ";" Then
					CL$ = Left(CL$,Len(CL$)-1)
					Addition$ = ";"
				EndIf
				CL$ = VarName+" = "+VarName+" / ("+Trim(Right(Cl$,Len(CL$)-(N+1)))+" )"+Addition$
				
			ElseIf KL$ = Chr(3)+Chr(4)  And StringOpen = True Then
				CL$ = Left(CL$,N-1)+Chr(34)+"+Chr(34)+"+Chr(34)+Right(CL$,Len(CL$)-(N+1))
				N = N + 11
			
			ElseIf KL$ = "->" And StringOpen = False Then
				CL$ = Left(CL$,N-1)+"\"+Right(CL$,Len(CL$)-(N+1))
				
			ElseIf KL$ = ">>" And StringOpen = False Then
				CL$ = Trim(Left(CL$,N-1))+" Shr "+Trim(Right(Cl$,Len(CL$)-(N+1)))
				
			ElseIf KL$ = "<<" And StringOpen = False Then
				CL$ = Trim(Left(CL$,N-1))+" Shl "+Trim(Right(Cl$,Len(CL$)-(N+1)))
				
			ElseIf KL$ = "||" And StringOpen = False Then
				CL$ = Trim(Left(CL$,N-1))+" Or "+Trim(Right(Cl$,Len(CL$)-(N+1)))
				
			ElseIf KL$ = "&&" And StringOpen = False Then
			CL$ = Trim(Left(CL$,N-1))+" And "+Trim(Right(Cl$,Len(CL$)-(N+1)))
			
			ElseIf Lower(Trim(Mid(CL$,N,7))) = "extends" Or Lower(Trim(Mid(CL$,N,8))) = "inherits" Then
				If Lower(Trim(Mid(CL$,N,7))) = "extends" Then
					Sub = 0
				Else
					Sub = 1
				EndIf
				InheritType$ = Lower(Trim(Right(CL$,Len(CL$)-(N+7+Sub))))
				If Right(InheritType,1) = ";" Then InheritType = Left(InheritType,Len(InheritType)-1)
				
				ThisStruct$ = Left(CL$,N-1)
				If Lower(Left(ThisStruct,5)) = "type " Then
					ThisStruct = Trim(Right(ThisStruct,Len(ThisStruct)-5))
				Else
					ThisStruct = Trim(Right(ThisStruct,Len(ThisStruct)-6))
				EndIf
				
				CL$ = Left(CL$,N-1)
				InheritOpen = 1
				
				For h.tType = Each tType
					If Lower(ThisStruct) = Lower(h\Name) Then Exit
				Next
				
				If h = Null Then RuntimeError "LINE: "+LC+Chr(10)+"COLUMN: "+N+Chr(10)+"Unable to find type "+thisStruct
				
				For Inherited.tType = Each tType
					If Trim(Lower(Inherited\Name)) = InheritType Then Exit
				Next
				
				If Inherited = Null Then RuntimeError "LINE: "+LC+Chr(10)+"COLUMN: "+N+Chr(10)+ThisStruct+" attempted to inherit nonexistant type "+InheritType
				
				h\Fields = h\Fields + Chr(10) + Inherited\Fields

				For m.Method = Each Method
					If m\T = Inherited Then
						thisMethodName$ = Left(m\Name,Instr(m\Name,"(")-1)
						If Right(thisMethodName,1) = "$" Or Right(thisMethodName,1) = "%" Or Right(thisMethodName,1) = "#" Then
							thisMethodName = Left(thisMethodName,Len(thisMethodName)-1)
						EndIf
							
						For jooba.Method = Each Method
							mName$ = Left(jooba\Name,Instr(jooba\Name,"(")-1)
							If Right(mName,1) = "$" Or Right(mName,1) = "%" Or Right(mName,1) = "#" Then
								mName = Left(mName,Len(mName)-1)
							EndIf
							
							If Lower(mName) = Lower(thisMethodName) And jooba\T = h Then Exit
						Next
						
						If jooba = Null Then
							dMt.Method = New Method
							For h.tType = Each tType
								If Lower(h\Name) = Lower(ThisStruct) Then
									dMt\T = h
									Exit
								EndIf
							Next
							dMt\Name = m\Name
							dMt\Contents = m\Contents
							If dMt\T = Null Then
								Delete dMt
								Exit
							EndIf
							POS = FilePos(FILE)
							CloseFile FILE
							FSize = FileSize("Temp.A")
							FILE = OpenFile("Temp.A")
							InMeth = Instr(dMt\Name,"(")
							OutMeth = Instr(dMt\Name,")")
							NeedComma = 0
							For N2 = InMeth To OutMeth
								AC = Asc(Upper(Mid(dMt\Name,N2,1)))
								If (AC >= 64 And AC <= 90) Or (AC >= 48 And AC <= 57) Or (AC = 34) Then
									NeedComma = 1
									Exit
								EndIf
							Next
							SeekFile FILE,FSize
							WriteLine FILE,""
							If NeedComma Then
								WriteLine FILE,"Function "+dMt\T\Name+Replace(dMt\Name,"(","(this."+dMt\T\Name+", ")
							Else
								WriteLine FILE,"Function "+dMt\T\Name+Replace(dMt\Name,"(","(this."+dMt\T\Name)
							EndIf
							WriteLine FILE,dMt\Contents+"End Function"
							SeekFile FILE,POS
						EndIf
					EndIf
				Next
			EndIf
			
			Previous$ = KL$
		Next
		VariableScope$ = ""
		
		If Lower(Left(Cl,7)) = "global " Then
			VariableScope$ = "Global "
			CL$ = Trim(Right(CL$,Len(CL$)-7))
			
		ElseIf Lower(Left(Cl,6)) = "local " Then
			VariableScope$ = "Local "
			CL$ = Trim(Right(CL$,Len(CL$)-6))
		EndIf
		
		For h.tType = Each tType
			Nem$ = Left(CL$,Len(h\Name)+1)
			If Lower(Nem$) = Lower(h\Name)+" " Then
				If Right(Cl$,1) = ";" Then
					Cl$ = Left(Cl$,Len(Cl$)-1)
					Addition = ";"
				EndIf
				VarName$ = Trim(Right(CL$,Len(CL$)-Len(Nem$)))
				CL$ = VarName+"."+Nem$+" = New "+Nem$+Addition
				For i.tTypeObject = Each tTypeObject
					If Lower(i\Name) = Lower(VarName)
						Exit
					EndIf
				Next
				
				If i = Null Then
					i.tTypeObject = New tTypeObject
					i\TypeName = h\Name
					i\Name = VarName
				EndIf
			EndIf
		Next
		
		For i.tTypeObject = Each tTypeObject
			For N = 1 To Len(CL$)
				GoOn = 0
				If N <= 1 Then
					GoOn = 1
				ElseIf Mid(CL,N-1,1) = "\" Then
					GoOn = 0
				Else
					GoOn = 1
				EndIf
				
				If N > 1 Then 
					If Trim(Mid(Cl,N-1,1)) <> "" Then
						Kooky = 0
					Else
						Kooky = 1
					EndIf
				Else
					Kooky = 1
				EndIf
				
				If Lower(Mid(CL$,N,Len(i\Name)+1)) = Lower(i\Name)+"\" And GoOn = 1 And Kooky = 1 Then
					
					LastSlash = N+Len(i\Name)
					InMeth = Instr(CL$,"(",N)
					If InMeth > N Then
						OutMeth = Instr(CL$,")",InMeth-1)
						MethodName$ = Trim(Mid(Cl$,N+Len(i\Name)+1,InMeth-(N+Len(i\Name)+1)))
						If Right(MethodName$,1) = "$" Or Right(MethodName$,1) = "#" Or Right(MethodName$,1) = "%" Then
							MethodName$ = Left(MethodName$,Len(MethodName$)-1)
						EndIf
						
						For m.Method = Each Method
							thisMethodName$ = Trim(Mid(m\Name,1,Instr(m\Name,"(")-1))
							If Right(thisMethodName$,1) = "$" Or Right(thisMethodName$,1) = "#" Or Right(thisMethodName$,1) = "%" Then
								thisMethodName$ = Left(thisMethodName$,Len(thisMethodName$)-1)
							EndIf
							If Lower(thisMethodName$) = Lower(MethodName$) And Lower(i\TypeName) = Lower(m\T\Name) Then
								If Quiet = 0 Then Print "Found call to known method "+MethodName$
								NeedComma = 0
								For N2 = InMeth To OutMeth
									AC = Asc(Upper(Mid(CL,N2,1)))
									If (AC >= 64 And AC <= 90) Or (AC >= 48 And AC <= 57) Or (AC = 34) Then
										NeedComma = 1
										Exit
									EndIf
								Next
								
								If NeedComma = 1
									CL$ = Left(CL$,N-1)+m\T\Name+thisMethodName+"("+i\Name+", "+Right(CL$,Len(CL)-InMeth)
								Else
									CL$ = Left(CL$,N-1)+m\T\Name+thisMethodName+"("+i\Name+" "+Right(CL$,Len(CL)-InMeth)
								EndIf
								Exit
							EndIf
						Next
					EndIf
				ElseIf Lower(Mid(Cl,N,4)) = "new " Then
					Constructor.Method = Null
					NewType = 1
					For m.Method = Each Method
						CName$ = Trim(Left(m\Name,Instr(m\Name,"(")-1))
						If Lower(i\TypeName) = Lower(m\T\Name) And Lower(CName) = Lower(m\T\Name) Then
							Constructor.Method = m
							Exit
						EndIf
					Next
					If Constructor <> Null Then
						For N3 = N To Len(CL$)
							If Lower(Mid(CL,N3,Len(CName)+2)) = " "+Lower(CName)+"(" Then
								LSide$ = Left(CL$,N-1)
								RSide$ = Right(CL$,Len(CL)-(N3+Len(CName)))
								CL$ = Trim(LSide)+" "+Constructor\T\Name+CName+Trim(RSide)
;								Stop
							EndIf
						Next
					EndIf
				EndIf
			Next
		Next
		NewType = 0
		CL$ = " "+Cl$
		
		For j.tTypeObjectField = Each tTypeObjectField
			For N = 1 To Len(CL$)
				If Mid(Cl$,N,1) = " " Then LastSpace = N
				If Lower(Mid(CL$,N,Len(j\Name)+1)) = "\"+Lower(j\Name) Then
					LastSlash = N+Len(j\Name)
					InMeth = Instr(CL$,"(",N)
					If InMeth > N Then
						OutMeth = Instr(CL$,")",InMeth-1)
						MethodName$ = Trim(Mid(Cl$,N+Len(j\Name)+2,InMeth-(N+1)-2))
						If Right(MethodName,1) = "(" Then MethodName = Left(MethodName,Len(MethodName)-1)
						If Right(MethodName$,1) = "$" Or Right(MethodName$,1) = "#" Or Right(MethodName$,1) = "%" Then
							MethodName$ = Left(MethodName$,Len(MethodName$)-1)
						EndIf
						
						For m.Method = Each Method
							thisMethodName$ = Trim(Mid(m\Name,1,Instr(m\Name,"(")-1))
							If Right(thisMethodName$,1) = "$" Or Right(thisMethodName$,1) = "#" Or Right(thisMethodName$,1) = "%" Then
								thisMethodName$ = Left(thisMethodName$,Len(thisMethodName$)-1)
							EndIf
							If Lower(thisMethodName$) = Lower(MethodName$) And Lower(j\TypeName) = Lower(m\T\Name) Then
								If Quiet = 0 Then Print "Found call to known method "+MethodName$
								NeedComma = 0
								For N2 = InMeth To OutMeth
									AC = Asc(Upper(Mid(CL,N2,1)))
									If (AC >= 64 And AC <= 90) Or (AC >= 48 And AC <= 57) Or (AC = 34) Then
										NeedComma = 1
										Exit
									EndIf
								Next
								If NeedComma = 1
									CL$ = Trim(Left(CL,LastSpace-1))+" "+m\T\Name+thisMethodName+"( "+Mid(CL,LastSpace,LastSlash-LastSpace+1)+", "+Trim(Right(CL$,Len(CL)-InMeth))
								Else
									CL$ = Trim(Left(CL,LastSpace-1))+" "+m\T\Name+thisMethodName+"( "+Mid(CL,LastSpace,LastSlash-LastSpace+1)+" "+Trim(Right(CL$,Len(CL)-InMeth))
								EndIf
								Exit
							EndIf
						Next
					EndIf
				ElseIf Lower(Mid(Cl,N,4)) = "new " Then
					Constructor.Method = Null
					NewType = 1
					For m.Method = Each Method
						CName$ = Trim(Left(m\Name,Instr(m\Name,"(")-1))
						If Lower(j\TypeName) = Lower(m\T\Name) And Lower(CName) = Lower(m\T\Name) Then
							Constructor.Method = m
							Exit
						EndIf
					Next
					If Constructor <> Null Then
						For N3 = N To Len(CL$)
							If Lower(Mid(CL,N3,Len(CName)+2)) = " "+Lower(CName)+"(" Then
								LSide$ = Left(CL$,N-1)
								RSide$ = Right(CL$,Len(CL)-(N3+Len(CName)))
								CL$ = Trim(LSide)+" "+Constructor\T\Name+CName+Trim(RSide)
;								Stop
							EndIf
						Next
					EndIf
				EndIf
			Next
		Next
		
		CL$ = Trim(CL$)
		
		NewType = 0
		
		Temp$ = Trim(Cl$)
		Temp2$ = Trim(Lower(Cl$))
		
		OpenScope = 0
		If Left( Temp2$,4 ) = "int " Then
			OpenArray = Instr(Temp2$,"[")
			If OpenArray = 1 Then
				NL$ = "Dim "+Trim(Replace(Replace(Right(Cl$,Len(Cl$)-4),"[","%("),"]",")"))
				Eol = 1
			Else
				L = L + 1
				CurrentClose(L) = 1
				NL$ = "Function "+Trim(Replace(Right(CL$,Len(CL$)-4),"(","%("))
				Scope = Scope + 1
				OpenScope = 1
				Eol = 1
			EndIf
		ElseIf Left( Temp2$,6 ) = "float " Then
			OpenArray = Instr(Temp2$,"[")
			If OpenArray = 1 Then
				NL$ = "Dim "+Trim(Replace(Replace(Right(Cl$,Len(Cl$)-6),"[","%("),"]",")"))
				Eol = 1
			Else
				L = L + 1
				CurrentClose(L) = 1
				NL$ = "Function "+Trim(Replace(Right(CL$,Len(CL$)-6),"(","%("))
				Scope = Scope + 1
				OpenScope = 1
				Eol = 1
			EndIf
		ElseIf Left( Temp2$,5 ) = "char " Then
			OpenArray = Instr(Temp2$,"[")
			If OpenArray = 1 Then
				NL$ = "Dim "+Trim(Replace(Replace(Right(Cl$,Len(Cl$)-5),"[","%("),"]",")"))
				Eol = 1
			Else
				L = L + 1
				CurrentClose(L) = 1
				NL$ = "Function "+Trim(Replace(Right(CL$,Len(CL$)-5),"(","%("))
				Scope = Scope + 1
				OpenScope = 2
				Eol = 1
			EndIf
		ElseIf Left( Temp2$, 7 ) = "switch " Or Left( Temp2$, 7 ) = "switch(" Or Left( Temp2$, 7 ) = "select " Then
			L = L + 1
			NL$ = "Select "+ Trim(Right(CL$,Len(CL$)-7))
			CurrentClose(L) = 4
			Scope = Scope + 2
			OpenScope = 2
			Eol = 1
		ElseIf Left( Temp2$, 3 ) = "if " Then
			NL$ = CL$
			If Right(Temp2,4) = "then" Then
				L = L + 1
				CurrentClose(L) = 2
				Scope = Scope + 1
				OpenScope = 1
			EndIf
			Eol = 1
		ElseIf Left( Temp2$,4) = "else" Or Left( Temp2$, 7 ) = "elseif " Then
			OpenScope = 1
			NL$ = CL$
		ElseIf Left( Temp2$, 6 ) = "while " Or Left( Temp2$, 6 ) = "while("
			L = L + 1
			NL$ = "While "+Trim(Right(CL$,Len(CL$)-6))
			CurrentClose(L) = 3
			Scope = Scope + 1
			OpenScope = 1
			Eol = 1
		ElseIf Left( Temp2$, 9 ) = "function " Then
			L = L + 1
			NL$ = CL$
			CurrentClose(L) = 1
			Scope = Scope + 1
			OpenScope = 1
			Eol = 1
		ElseIf Left( Temp2, 7 ) = "struct " Then
			L = L + 1
			NL$ = "Type "+Trim(Right(Cl$,Len(CL$)-7))
			CurrentClose(L) = 5
			Scope = Scope + 1
			OpenScope = 1
			Eol = 1
			StructOpen = 1
		ElseIf Left( Temp2, 5 ) = "type " Then
			L = L + 1
			NL$ = CL$
			CurrentClose(L) = 5
			Scope = Scope + 1
			OpenScope = 1
			Eol = 1
			StructOpen = 2
		ElseIf Left( Temp2, 4 ) = "for " Then
			L = L + 1
			NL$ = CL$
			CurrentClose(L) = 6
			Scope = Scope + 1
			Eol = 1
			OpenScope = 1
		ElseIf Left( Temp2, 6 ) = "repeat" Then
			L = L + 1
			NL$ = CL$
			CurrentClose(L) = 8
			Scope = Scope + 1
			Eol = 1
			OpenScope = 1
		ElseIf (Left( Temp2, 5 ) = "case " Or Left( Temp2, 7 ) = "default") And Right(Temp2, 1) = ":" Then
			EOL = 1
			OpenScope = 1
			NL$ = Left(CL,Len(CL)-1)
			L = L + 1
			CurrentClose(L) = 7
		ElseIf (Left(Temp2,5) = "case " Or Left( Temp2, 7 ) = "default") And Right(Temp2,1) <> ":" Then
			EOL = 1
			OpenScope = 1
			NL$ = CL$
			L = L + 1
			CurrentClose(L) = 7
		ElseIf Left( Temp2, 5 ) = "endif" Or Left( Temp2, 6 ) = "end if" Or Left( Temp2, 12 ) = "end function" Or Left( Temp2, 10 ) = "end select" Or Left( Temp2, 4 ) = "wend" Or Left( Temp2, 7 ) = "forever" Or Left( Temp2, 5 ) = "until" Or Left( Temp2, 4 ) = "next" Or Left( Temp2, 8 ) = "end type" Or Left( Temp2, 10 ) = "end switch" Then
			If Left( Temp2, 8 ) = "end type" Then StructOpen = 0
			EOL = 1
			Scope = Scope - 1
			If Left( Temp2$, 10 ) = "end switch" Or Left( Temp2$, 10 ) = "end select" Then Scope = Scope - 1
			NL$ = CL$
			If Left( Temp2$, 10 ) = "end switch" Then
				NL$ = "End Select"
				If Len(CL$)-10>0 Then
					NL$ = NL$ + Right(CL,Len(CL)-10)
				EndIf
			EndIf
			L = L - 1
		Else
			NL$ = CL$
		EndIf
		
		Temp$ = Trim(Cl$)
		Temp2$ = Trim(Lower(Cl$))
		
		If Left(Temp,1) = "}" Then
			Select CurrentClose(L)
				Case 1
					NL$ = "End Function"+Right(CL,Len(CL)-1)
				Case 2
					NL$ = "EndIf"+Right(CL,Len(CL)-1)
				Case 3
					NL$ = "Wend"+Right(CL,Len(CL)-1)
				Case 4
					NL$ = "End Select"+Right(CL,Len(CL)-1)
					Scope = Scope - 1
				Case 5
					NL$ = "End Type"+Right(CL,Len(CL)-1)
					StructOpen = 0
				Case 6
					NL$ = "Next"
				Case 7
					NL$ = ""
					SkipThisLine2 = 1
				Default
					NL$ = ""
					L = L + 1
					Scope = Scope + 1
			End Select
			L = L - 1
			Scope = Scope - 1
			Eol = 1	
		EndIf
		
		If StructOpen And OpenScope = False Then
			If Lower(Left(NL$,6)) <> "field " Then
				NL$ = "Field "+NL$
			EndIf
		EndIf
		
		CurrentLine$ = CurrentLine$ +" "+ VariableScope + NL$
		If (((Right(CurrentLine$,1) = ";" And UseDelimitor = 1) Or UseDelimitor = 0) Or Eol) And SkipThisLine2 = 0 Then
			CurrentLine$ = Trim(CurrentLine$)
			For N = 1 To Scope-OpenScope
				CurrentLine$ = "	"+CurrentLine$
			Next
			If Right(CurrentLine$,1) = ";" Then
				CurrentLine$ = Left(CurrentLine$,Len(CurrentLine$)-1)
			EndIf
			WriteLine OUT,Left(CurrentLine$,Len(CurrentLine$))
;			If Quiet = 0 Then Print Left(CurrentLine$,Len(CurrentLine$))
			CurrentLine$ = ""
		ElseIf Len(CL$) = 0 Then
			WriteLine( OUT, "" )
		EndIf
	ElseIf SkipThisLine = 0
		WriteLine OUT,CL$
	EndIf
	SkipThisLine2 = 0
Wend

CloseFile FILE
CloseFile OUT

ExecFile Chr(34)+Replace(GetEnv("blitzpath")+"\bin\blitzcc.exe","\\","\")+Chr(34)+" "+NCMDL$		;; uncomment and rename your blitzcc.exe to blitzcc_.exe and compile this as blitzcc.exe if you want to try your luck- it doesn't work for me, so i doubt it will for you.

Function IncludeFile(InStream,OutStream,NoParse)
	If Not InStream Then Return
	If Not OutStream Then Return
	Local h.tType,m.Method
	TFile = OutStream
	FILE = InStream
	If NoParse = 1  Then WriteLine TFile,";; NOPARSE"
	PO = NoParse
	While Not Eof(FILE)
		If Quiet = 0 Then
			T = T + 1	
			If T > 3
				AppTitle "Parsing..."
				T = 0
			ElseIf T > 2
				AppTitle "Parsing.."
			ElseIf T > 1 Then
				AppTitle "Parsing."
			EndIf
		EndIf
		CL$ = ReadLine(FILE)
			
		
		Temp$ = Trim(Replace(CL$,"	"," "))
		For N = 1 To Len(CL$)
			If (Mid(CL,N,1) = ";" And PO >= 1) Or (Mid(CL,N,2) = "//" And PO = 0) Then
				Temp$ = Trim(Left(CL,N-1))
				Exit
			EndIf
		Next
		Skippy = 0
		If Left(Temp,1) = "#" Then 	;;Preproc defs
			r$ = Lower(Right(Temp,Len(Temp)-1))
			If Left(r,7) = "define " Then
				DefVal$ = Trim(Right(r$,Len(r)-7))
				D.Def = New Def
				D\Name = DefVal
				DefCount = DefCount + 1
				D\Index = DefCount
			If Left(r,9) = "undefine "
				DefVal$ = Trim(Right(r$,Len(r)-7))
				For D.Def = Each Def
					If D\Name = DefVal Then
						Delete D
						Exit
					EndIf
				Next
			EndIf
			ElseIf Left(r,6) = "ifdef "
				DefVal$ = Trim(Right(r$,Len(r)-6))
				CurDef = CurDef + 1
				For D.Def = Each Def
					If D\Name = DefVal Then
						Defined(CurDef) = 1
						Exit
					Else
						Defined(CurDef) = 0
					EndIf
				Next
			ElseIf Left(r,7) = "ifndef "
				DefVal$ = Trim(Right(r$,Len(r)-7))
				CurDef = CurDef + 1
				For D.Def = Each Def
					If D\Name = DefVal Then
						Defined(CurDef) =0
						Exit
					Else
						Defined(CurDef) = 1
					EndIf
				Next
			ElseIf Left(r,4) = "else" And Left(r, 6) <> "elseif"
				Defined(CurDef) = Not Defined(CurDef)
			ElseIf Left(r,7) = "elseif "
				DefVal$ = Trim(Right(r$,Len(r)-7))
				If Left(DefVal$,1) = "!" Then
					Nono = 1
					DefVal = Right(DefVal,Len(DefVal)-1)
				EndIf
				If Defined(CurDef) = 0 Then
					If NoNo = 1 Then
						For D.Def = Each Def
							If D\Name = DefVal Then
								Defined(CurDef) =0
								Exit
							Else
								Defined(CurDef) = 1
							EndIf
						Next
					Else
						For D.Def = Each Def
							If D\Name = DefVal Then
								Defined(CurDef) =1
								Exit
							Else
								Defined(CurDef) = 0
							EndIf
						Next
					EndIf
				EndIf
			ElseIf Left(r,5) = "endif"
				CurDef = CurDef - 1
				CL$ = ""
			EndIf
			Skippy = 1
		EndIf
		If Skippy = 0 Then
			
			CL$ = Replace(Replace(Replace(CL$,","," , "),"(","( "),")"," )")
			If Left(Trim(Cl$),1) = "}" Or Left(Lower(Trim(CL$)),8) = "end type" Then
					TypeOpen = 0
			EndIf
			
			For N = 1 To Len(CL)
				If N < Len(CL)-2 Then
					Jd$ = Mid(CL$,N,3)
					LA = Asc(Upper(Left(Jd,1)))
					RA = Asc(Upper(Right(Jd,1)))
					MC$ = Mid(Jd,2,1)
					If ((LA >= 65 And LA <= 90) Or (LA >= 48 And LA <= 57) Or Chr(LA) = "\" Or Chr(LA) = "," Or Chr(LA) = "." Or Trim(Chr(LA)) = "" Or Chr(LA) = "(" Or Chr(LA) = ")" ) And ((RA >= 65 And RA <= 90) Or (RA >= 48 And RA <= 57) Or Chr(RA) = "\" Or Chr(RA) = "," Or Chr(RA) = "." Or Trim(Chr(RA)) = "" Or Chr(RA) = "(" Or Chr(RA) = ")" ) And Len(Trim(Chr(LA)))+Len(Trim(Chr(RA))) > 0
						If MC = "+" Or MC = "/" Or MC = "*" Or MC = "-" Then
							CL = Left(CL,N)+" "+MC+" "+Right(CL,Len(CL)-(N+1))
						EndIf
					EndIf
				EndIf
			Next
			
			If Lower(Left(Trim(Cl$),10)) = "end method" Then
				MethodOpen = 0
				m\Body = m\Body+Chr(10)+m\Contents+"End Function"
				MethodClosed = 1
			EndIf
			
			If Upper(Left(Trim(Cl$),10)) = ";; NOPARSE" Then PO = PO + 1
			If Upper(Left(Trim(CL$),12)) = ";; OPENPARSE" Then PO = PO - 1
	
			If Lower(Left(Temp$,8)) = "include " Then
				FName$ = Trim(Replace(Right(Temp$,Len(Temp$)-(8)),Chr(34),""))
				For N = 1 To Len(FName$)
					If Mid(FName$,N,1) = ";" Or Mid(FName,N,2) = "//" Then
						FName$ = Trim(Left(FName$,N-1))
						Exit
					EndIf
				Next
				Path$ = Directory+FName
				If Lower(Trim(Right(Path$,3))) = ".bb" Then
					PARSEOFF = 1
				Else
					PARSEOFF = 0
				EndIf
				NoParseOpen = PARSEOFF
				ICF = ReadFile(Path$)
				IncludeFile ICF,TFile,PARSEOFF
				If ICF Then CloseFile ICF
			ElseIf MethodOpen = 0 And MethodClosed = 0 And Defined(CurDef) = 1 Then
				If TypeOpen >= 1 Then
					If Lower(Left(Trim(Replace(CL$,"	"," ")),7)) <> "method " Then
						WriteLine TFile,CL$
					EndIf
				Else
					WriteLine TFile,CL$
				EndIf
			EndIf
			
			CL$ = Trim(Replace(CL$,"	"," "))
			
			If Lower(Left(CL$,6)) = "local " Then
				CL$ = Trim(Right(CL$,Len(CL$)-6))
			ElseIf Lower(Left(CL$,7)) = "global " Then
				CL$ = Trim(Right(CL$,Len(CL$)-7))
			ElseIf Left(CL$,4) = "for " Then
				CL$ = Trim(Right(CL$,Len(CL$)-4))
			EndIf
			NoDef = 0
			
			For N = 1 To Len(Cl$)
				If Mid(Cl$,N,2) = "//" Then
					Cl$ = Trim(Left(CL$,N-1))
					Exit
				ElseIf Mid(CL$,N,1) = "="
					NoDef = 1
				ElseIf Mid(Cl$,N,1) = " "
					NoDef = 1
				ElseIf Mid(CL$,N,1) = Chr(34)
					NoDef = 1
				ElseIf Mid(CL$,N,1) = "." And NoDef = 0
					j.tTypeObject = New tTypeObject
					j\Name = Left(CL,N-1)
					j\TypeName = Right(CL,Len(CL)-N)
					Eq = Instr(j\TypeName,"=")
					If Eq > 0 Then
						j\TypeName = Trim(Left(j\TypeName,Eq-1))
					EndIf
					If Quiet = 0 Then Print "Found type object "+j\Name+", struct "+j\TypeName
				EndIf
			Next
			
			If MethodOpen = 1 Then
				For N = 1 To Len(" "+Cl$)
					Kl$ = Mid(" "+Cl,N,2)
					If Mid(" "+Cl,N,1) = " \" And N > 4 Then
						If Lower(Mid(" "+Cl,N-4,4)) <> "this" Then
							Cl = Left(" "+Cl,N-1)+"this"+Right(" "+Cl,Len(Cl)-(N-1))
						EndIf
					ElseIf N > 1 And Mid(" "+Cl,N,2) = " \" Then
						Cl = Left(Cl,N-1)+"this"+Right(Cl,Len(Cl)-(N-1))
					ElseIf Mid(" "+Cl,N,2) = " \"
						Cl = "this"+Cl
					EndIf
				Next
				m\Contents = m\Contents + Cl$ + Chr(10)
			EndIf
			
			If MethodOpen = 0 And MethodClosed = 0 And TypeOpen > 0 Then
				NewField$ = Trim(Replace(Cl$,"	"," "))
				If Lower(Left(NewField$,7)) <> "method " Then
					If Lower(Left(NewField$,5)) = "field" Then
						NewField$ = "	"+NewField$
					Else
						NewField$ = "	Field "+NewField$
					EndIf
					If Instr(NewField,".") Then
						TypeName$ = Right(NewField,Len(NewField)-Instr(NewField,"."))
						Te.tTypeObjectField = New tTypeObjectField
						Te\TypeName = TypeName
						Te\Name = Trim(Replace(NewField,"	Field ",""))
						Te\Name = Left(Te\Name,Instr(Te\Name,".")-1)
					EndIf
					h\Fields = h\Fields+Chr(10)+NewField
					If Left(h\Fields,1) = Chr(10) Then h\Fields = Right(h\Fields,Len(h\Fields)-1)
					If Right(h\Fields,1) = ";" Then h\Fields = Left(h\Fields,Len(h\Fields)-1)
				EndIf
			EndIf
			
			If Left(Lower(Trim(Cl$)),7) = "method " Then
				MethodOpen = 1
				m.Method = New Method
				m\Name = Trim(Right(Trim(Cl$),Len(Trim(CL$))-7))
				m\T = h
				CName$ = Trim(Left(m\Name,Instr(m\Name,"(")-1))
				If Lower(CName) <> Lower(m\T\Name) Then m\Contents = "If this = Null Then Return False"+Chr(10)
				
				For N = 1 To Len(m\Name)
					If Mid(m\Name,N,1) = "," And ArgsOpen = 1 Then
						m\Arguments = m\Arguments + 1
					ElseIf Mid(m\Name,N,1) = "(" Then
						ArgsOpen = ArgsOpen + 1
					ElseIf Mid(m\Name,N,1) = ")" Then
						ArgsOpen = ArgsOpen - 1
					ElseIf Asc(Upper(Mid(m\Name,N,1))) >= 64 And Asc(Upper(Mid(m\Name,N,1))) <= 90 And ArgsOpen = 1 And m\Arguments = 0
						m\Arguments = 1
					EndIf
				Next
				ArgsOpen = 0
;				Stop
				
				If Quiet = 0 Then Print "Found method "+m\Name
			EndIf
			
			If Left(Lower(Trim(CL$)),7) = "inline " Then
				If Quiet = 0 Then Print "Inline function "+Trim(Right(Trim(CL$),Len(Trim(CL$))-7))+" found"
				CL$ = Right(Trim(CL$),Len(Trim(CL))-7)
			EndIf
			
			If Left(Lower(Trim(Cl$)),7) = "struct " Then
				h.tType = New tType
				h\Name = Trim(Right(Cl$,Len(Cl)-7))
				For N = 1 To Len(h\Name)
					If Mid(h\Name,N,1) = " " Then
						h\Name = Left(h\Name,N-1)
						h\Inherits = Trim(Right(CL$,Len(CL$)-7-Len(h\Name)))
						For N = 1 To Len(h\Inherits)
							If Lower(Mid(h\Inherits,N,9)) = "inherits" Or Lower(Mid(h\Inherits,N,8)) = "extends " Then
								If Lower(Mid(h\Inherits,N,9)) = "inherits " Then
									h\Inherits = Right(h\Inherits,Len(h\Inherits)-(N+8))
								Else
									h\Inherits = Right(h\Inherits,Len(h\Inherits)-(N+7))
								EndIf
							EndIf
						Next
						Exit
					EndIf
				Next
				TypeOpen = 2
				If Quiet = 0 Then Print "Found struct "+h\Name+", "+h\Inherits
			ElseIf Left(Lower(Trim(Cl$)),5) = "type " Then
				h.tType = New tType
				h\Name = Trim(Right(Cl$,Len(Cl)-4))
				For N = 1 To Len(h\Name)
					If Mid(h\Name,N,1) = " " Then
						h\Name = Left(h\Name,N-1)
						h\Inherits = Trim(Right(CL$,Len(CL$)-4-Len(h\Name)))
						For N = 1 To Len(h\Inherits)
							If Lower(Mid(h\Inherits,N,9)) = "inherits" Or Lower(Mid(h\Inherits,N,8)) = "extends " Then
								If Lower(Mid(h\Inherits,N,9)) = "inherits " Then
									h\Inherits = Right(h\Inherits,Len(h\Inherits)-(N+8))
								Else
									h\Inherits = Right(h\Inherits,Len(h\Inherits)-(N+7))
								EndIf
							EndIf
						Next
						j.tTypeObject = New tTypeObject
						j\TypeName = h\Name
						j\Name = "this"
						Exit
					EndIf
				Next
				TypeOpen = 1
				If Quiet = 0 Then Print "Found struct "+h\Name+", "+h\Inherits
			EndIf
			MethodClosed = 0
		EndIf
	Wend
	If NoParse = 1  Then WriteLine TFile,";; OPENPARSE"
	Return True
End Function
