; ID: 1763
; Author: Alaric
; Date: 2006-07-26 11:29:45
; Title: BB Code Formatter
; Description: Strips away and re-adds blank lines and tabs

;Average Commands
Global pluscoms$=Lower$(";if;select;repeat;for;while;function;type;")
Global mincoms$=Lower$(";until;forever;next;wend;endif;end;")
Global backcoms$=Lower$(";then;case;default;elseif;else;")
Global comends$=Chr$(34)+Chr$(9)+" ~!@#$%^&*()_+|`-=\{}:<>?[];',./"

;Header commands
Const headercoms$=";graphics;apptitle;automidhandle;seedrnd;setbuffer;"
Const constcoms$=";const;"
Const globalcoms$=";global;"
Const localcoms$=";local;"
Const arraycoms$=";dim;"

;Globalize some assorted variables
Global hitfuncs=0
Global needspace=0
Global LastComSet=0 ;Used to see which header command set (declaration or such) was used
Global crlf$=Chr$(10)+Chr$(13)

;Select the files
Global fileloc$=RequestFile$("Select a File to reformat")
If Not FileType(fileloc$) Then Notify("Could not open file!"):End
Global filein=ReadFile(fileloc)
Global fileout=WriteFile(parseoutext$(fileloc)+"_clean"+parseext$(fileloc))

;Begin the Main Loop
While Not Eof(filein)
	linefeed$=ReadLine(filein)
	linefeed=Trim$(Replace$(linefeed,Chr$(9),""))
	If Trim$(linefeed) <> "" Then
		If Left$(linefeed,1)=";" Then WriteLine(fileout,"")
		newtab%=needstab%(linefeed)
		cleanheader(linefeed)

		;Check to see if the program has come to the function section near the end
		If hitfuncs = 1 Then
			For I=1 To 5
				WriteLine(fileout,"")
				Print ""
			Next

			hitfuncs=2
		End If


		;See if a blank space is necessary
		If needspace Then
			WriteLine(fileout,"")
			Print ""
			needspace=0
		End If


		;Add the Tabs
		If newtab > 1 Then tab=tab-1 ;See if the tab needs to be taken down
		If tab< 0 Then tab=0
		I=0
		While I < tab
			linefeed=Chr$(9)+linefeed
			I=I+1
		Wend

		If newtab Mod(2) Then tab=tab+1 ;See if the tab should be reversed or added to

		;Makes sure that there isn't a break for an Ending (forever, next, end function, etc.) command just Before another such command
		If newtab=2
			LastWasEnd=True
		Else
			If lastwasend Then
				Print ""
				WriteLine(fileout,"")
				lastwasend=False
			End If
		End If

		Print linefeed$
		WriteLine(fileout,linefeed) ;write the edited line
	End If
Wend


;I like to have plenty of space at the bottom of my code
For I = 1 To 50
	WriteLine(fileout,"")
Next

Print "Finished"
CloseFile(filein)
CloseFile(fileout)
Delay(10000)





Function ParseOutExt$(loc$);parses the name and location of a file w/o extension
	Repeat
		If Not Instr(loc,".",placement+1) Then Exit
		placement=Instr(loc,".",placement+1)
	Forever

	Return Left$(loc,placement-1)
End Function


;parses the extension of a file location
Function ParseExt$(loc$)
	Repeat
		If Not Instr(loc,".",placement+1) Then Exit
		placement=Instr(loc,".",placement+1)
	Forever

	Return Right$(loc,Len(loc)-placement+1)
End Function

Function needstab%(LineIn$)
	If linein <> ""

		;Prepare the input
		linein=Lower$(Trim$(linein))
		If linein="end" Then Return 0 ;Ensure there isn't a random "End" statement that messes with tabbing
		nostrings$=stripstrings$(linein)
		nocomments$=stripcomments$(linein)

		;parse out the first command of a line
		com$=";"+firstcom$(linein)+";"

		;check to see if the functions section of the program has begun
		If com$=";function;" And hitfuncs=0 Then hitfuncs=1

		;ensure that an "if" statement w/o an "end if" doesn't screw up the tabbing
		If Instr(nostrings,"then",0)
			If Right$(nostrings,4) <> "then" Or Instr(nostrings,":") Then Return 0
		End If


		;find out how to classify the command
		If Instr(pluscoms,com$,0) ;add a tab
			Return 1
		ElseIf Instr(mincoms$,com$,0) ;delete a tab
			Return 2
		ElseIf Instr(backcoms,com$,0) ;temporarily delete a tab
			Return 3
		End If
	End If


	;command does not require a tab change
	Return 0
End Function


;parses out the first word on a line
Function firstcom$(linein$)
	linein=Trim$(Replace$(linein,Chr$(9),""))
	I=1
	While (Not Instr(comends,Mid$(linein,I,1),0)) And (I <= Len(linein$))
		I=I+1
	Wend

	Return Lower$(Trim$(Left$(linein,I-1)))
End Function


;strips away comments
Function StripComments$(linein$)
	I=Instr(linein,";")
	Return Left$(Trim$(linein),I-1)
End Function


;strips away strings
Function stripstrings$(linein$)
	While Instr(linein,Chr$(34))
		placement=Instr(linein,Chr$(34))
		If placement
			endquote=Instr(linein,Chr$(34),placement+1)
			linein=Left$(linein,placement-1)+Right$(linein,Len(linein)-endquote)
		End If
	Wend

	Return linein
End Function


;used for identifying declaration functions (global, const, local, etc.)
Function cleanheader(linein$)
	Local com$=";"+firstcom$(linein)+";"
	Select 1
	Case Instr(Globalcoms,com$,0)
		If lastcomset=0 Then lastcomset=1
		If lastcomset <> 1 Then WriteLine(fileout,"")
		lastcomset=1
	Case Instr(constcoms,com$,0)
		If lastcomset=0 Then lastcomset=2
		If lastcomset <> 2 Then WriteLine(fileout,"")
		lastcomset=2
	Case Instr(arraycoms,com$,0)
		If lastcomset=0 Then lastcomset=3
		If lastcomset <> 3 Then WriteLine(fileout,"")
		lastcomset=3
	Case Instr(localcoms,com$,0)
		If lastcomset=0 Then lastcomset=4
		If lastcomset <> 4 Then WriteLine(fileout,"")
		lastcomset=4
	Case Instr(headercoms,com$,0)
		If lastcomset=0 Then lastcomset=5
		If lastcomset <> 5 Then WriteLine(fileout,"")
		lastcomset=5
	Default
		lastcomset=0
	End Select
End Function
