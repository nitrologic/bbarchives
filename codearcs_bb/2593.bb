; ID: 2593
; Author: Malice
; Date: 2009-10-07 09:09:25
; Title: File Searcher
; Description: Stores results of file searches with various options for criteria

;Notes For Criteria Logic:

; This will return ALL Records EXCLUDING those with ".bb", ".exe." or "Hello" in the filename (note, pending on the IgnoreCase flag)
;CriteriaType%		=	(CRITERIA_NOT Or CRITERIA_OR)
;CriteriaString$	=	(CRITERIA_STRING$=".bb"+CRITERIA_SEPARATOR$+".exe"+CRITERIA_SEPARATOR$+"Hello")

; This will return ONLY Records whose filename exactly matches "Blitz3D.exe" (note, pending on the IgnoreCase flag)
;CriteriaType%		=	(CRITERIA_SPECIFIC)
;CriteriaString$	=	(CRITERIA_STRING$="Blitz3D.exe")

; This will return ONLY Records whose filename DOES NOT exactly match "Read Me.txt", "Read Me.pdf", "Read Me.wri" OR "Read Me.doc" (note, pending on the IgnoreCase flag)
;CriteriaType%		=	(CRITERIA_SPECIFIC Or CRITERIA_NOT)
;CriteriaString$	=	(CRITERIA_STRING$="Read Me.txt"+CRITERIA_SEPARATOR$+"Read Me.pdf"+CRITERIA_SEPARATOR$+"Read Me.wri"++CRITERIA_SEPARATOR$+"Read Me.doc")

; This will return ONLY Records whose filename contains BOTH "The Beatles" AND ".mp3" (note, pending on the IgnoreCase flag)
;CriteriaType%		=	(CRITERIA_AND)
;CriteriaString$	=	(CRITERIA_STRING$="The Beatles"+CRITERIA_SEPARATOR$+".exe"+CRITERIA_SEPARATOR$+".mp3")

; Unfortunately, As of This version, the criteria logic NOT cannot be combined. So you cannot retrieve, for example, ALL Records
;containing "The Beatles" but excluding those with ".wav" within.
;NOT can be used with OR or AND if all criteria are to be excluded. Essenitally it reverses the selection process of the criteria.
;However, careful manipulation of two separate archives can still be used to lessen any workload.

; For Ease of Use, the default folder names such as "." and ".." are skipped automatically. Attempting to retrieve these may cause error.

;**********************************************************

























































;**********************************************************************************************************************************************************************************
;*********************************************************************************  USER ENVIRONMENT  *****************************************************************************
;**********************************************************************************************************************************************************************************

; These declarations are user-confgured.
Global debug=Instr(Lower$(CommandLine()),"debug")	; Checks for "debug" command line entry - alternatively, use a simple True or False!
Global fDebuglogPath$=CurrentDir()+"Debuglog.log"	; This is where the debug log will be output to.
Const APP_NAME$="My Application"					; Largely irrelevant for the scope of this snippet, this just holds the name of the application.


;**********************************************************

;You can simply use the BuildArchive() function as it is, or, for more control, make use the following global definitions and functions

Global CRIT_STRING$=NULL_STRING$

Global CRIT_TYPE%=CRITERIA_OR%

Global FILETYPE_FILTER%=FILETYPE_EITHER%

Global RECURSIVE%=True

Global IGNORE%=True

Function ToggleIgnoreCase()
	IGNORE%=(Not(IGNORE%))
End Function

Function ToggleRecursive()
	RECURSIVE%=(Not(RECURSIVE%))
End Function

Function SetFileTypeFilter(FilterType)
	FILETYPE_FILTER%=FilterType
End Function

Function SetCriteriaTypeFilter(CritType)
	CRIT_TYPE%=CritType
End Function

Function AddToCritString$(AddString$)
	CRIT_STRING$=CRIT_STRING$+CRITERIA_SEPARATOR$+AddString$
End Function

;With the above, the following wrapper can be used to generate an archive archive much more conveniently.
Function GenerateArchive(Archive%=1,StartDirectory$=NULL_STRING$)
	BuildArchive(Archive%,IGNORE,RECURSIVE,StartDirectory$,FILETYPE_FILTER%,CRIT_TYPE%,CRIT_STRING$,True)
End Function

;**********************************************************






;**********************************************************************************************************************************************************************************
;*************************************************************************************  REQUIRED  *********************************************************************************
;**********************************************************************************************************************************************************************************

; All the following declarations are necessary and required parts of the system. Please do not alter them!
Const NULL_STRING$=""
Const SPACER$=" "
Const WILDCARD$="*"
Const BAR$="|"

Const EXTENSION_SEPARATOR$="."
Const PATH_SEPARATOR$="\"
Const PATH_SEPARATOR_REVERSE$="/"

; These provide help for readability and allow logic clauses.
Const FILETYPE_FILE%=1
Const FILETYPE_DIRECTORY%=2
Const FILETYPE_EITHER%=3
Const FILETYPE_INVALID%=0

; Sets the Bitwise values for the Criteria logic.
Const CRITERIA_AND%=1
Const CRITERIA_OR%=2
Const CRITERIA_SPECIFIC%=4
Const CRITERIA_NOT%=8

; This is used to imply separate entries.
Const CRITERIA_SEPARATOR$="!*?"		

; These declarations are used by the Debugger functionality.
Global DBG_ERROR_CODE$[6]
DBG_ERROR_CODE[0]="Undefined Error"
DBG_ERROR_CODE[1]="Debugger consistency message"

DBG_ERROR_CODE[2]="Archive limit reached. Process not applied"

DBG_ERROR_CODE[3]="File cannot be written or bad path"
DBG_ERROR_CODE[4]="File not found or bad path"
DBG_ERROR_CODE[5]="File already deleted missing, or bad path"

Const DBG_ERC_NOT_AN_ERROR%=1

Const DBG_ERC_ARC_LIMITREACHED%=2

Const DBG_ERC_FILE_CANT_WRITE%=3
Const DBG_ERC_FILE_MISSING%=4
Const DBG_ERC_FILE_DELETE_MISSING%=5

; Due to unknown potential of large number of Records, this Type is used to contain the results.
;Results split into separate parts for user functionality.
Type Records
	Field Returned_FileType%
	Field Returned_Path$
	Field Returned_Filename$
	Field Returned_Extension$
	Field Archive%
End Type

;**********************************************************************************************************************************************************************************
;*************************************************************************************  DEBUGGER  *********************************************************************************
;**********************************************************************************************************************************************************************************

Function DebugLine(sDebugFunction$="DEBUGGER:NULLFUNCTION",nCode%=0,bExitFlag=False,sDebugReason$=NULL_STRING$)
	If (Not(debug))
		If (bExitFlag)
			AppTitle APP_NAME$+" Terminated by Debugger."
			RuntimeError Str(nCode)+" ("+DBG_ERROR_CODE[nCode%]+")"
		End If			
		Return
	End If
	Local fDebug%=WriteFile%(fDebuglogPath$)
	If (Not(fDebug))
		If (bExitFlag) Then ExitApplication(nCode%,sDebugFunction$,sDebugReason$)
		debug=False
		Return
	End If
	SeekFile(fDebug,FileSize(fDebuglogPath$))
	Local sLine$=LSet$(CurrentDate(),11)+Chr(9)+BAR$+LSet$(CurrentTime(),8)+Chr(9)+BAR$+LSet$(Trim$(Replace$(sDebugFunction$,SPACER+SPACER,SPACER)),50)+Chr(9)+BAR$+LSet$(Trim$(Replace$(Str(nCode%)+" ("+DBG_ERROR_CODE[nCode%]+")",SPACER+SPACER,SPACER)),50)+Chr$(9)+BAR$+Trim(Replace$(sDebugReason$,SPACER+SPACER,SPACER))
	WriteLine(fDebug,sLine$)
	CloseFile fDebug
	If (bExitFlag)Then ExitApplication(nCode%,sDebugFunction$,sDebugReason$)
End Function

Function ExitApplication(sFunction$,nCode%, sReason$)
	ClearWorld
	EndGraphics
	AppTitle APP_NAME$+" Terminated by Debugger."
	Local sReport$=APP_NAME$+" has been terminated by Debugger due to a critical error in function process:"+Chr(10)
	sReport=sReport+Str(nCode%)+" ("+DBG_ERROR_CODE[nCode%]+")"+Chr$(10)
	sReport=sReport+Str(nCode%)+" ("+DBG_ERROR_CODE[nCode%]+")"+Chr$(10)
	sReport$=sReport$+"Debugger cites a possible reason: "+Chr(10)+sReason$
	sReport$=sReport$+"For more information: "+Chr(10)+fDebuglogPath$
	
	AppTitle "Application Needs To Close",sReport$
	
	RuntimeError sReport$
End Function

;**********************************************************************************************************************************************************************************
;**********************************************************************************  CORE FUNCTIONS  ******************************************************************************
;**********************************************************************************************************************************************************************************


Function BuildArchive(Archive%=1,Ignore_Case%=True,RecursiveSearch%=True,StartDir$=NULL_STRING$,RecordsType%=FILETYPE_EITHER%,CriteriaType%=CRITERIA_OR%,CriteriaString$=NULL_STRING$,FirstRun%=True)
	
	If (FirstRun%)
		Local nTotalArchives%=CountArchives()
		If (nTotalArchives%>29)
			DebugLine("CONFIGARCHIVE:BUILDARCHIVE",DBG_ERC_ARC_LIMITREACHED,False,"Archive total is: "+Str(nTotalArchives%)+" Maximum: 30")
			Return
		End If
		DebugLine("CONFIGARCHIVE:BUILDARCHIVE",DBG_ERC_NOT_AN_ERROR,False,"Building archive "+Archive%)
		DebugLine("CONFIGARCHIVE:BUILDARCHIVE",DBG_ERC_NOT_AN_ERROR,False,"Clearing old archive data")
		DeleteArchive(Archive%)
	End If
	
	If (StartDir$=NULL_STRING$) Then StartDir$=CurrentDir()
	Local SearchDir$=StartDir$
	SearchDir$=FixPath$(SearchDir$,True)
	Local SearchHandle%=ReadDir(SearchDir$)
	
	If (Not(SearchHandle)) Then SearchHandle%=ReadDir(CurrentDir())
	
	DebugLine("CONFIGARCHIVE:BUILDARCHIVE",DBG_ERC_NOT_AN_ERROR,False,"Searching directory "+SearchDir$)
	
	Local CurrentFile$=NextFile(SearchHandle%)
	Local FullFilePath$=FixPath$(SearchDir$+CurrentFile$)
 	Local CurrentFileType%=FileType(FullFilePath$)
	While (CurrentFile$<>NULL_STRING)
		If (Right$(CurrentFile$,1)<>EXTENSION_SEPARATOR)
			CurrentFileType%=FileType(FullFilePath$)		
			If FileTypeCriteriaValid%(CurrentFileType%,RecordsType%)
				If (CheckCriteria%(CurrentFile$,CriteriaString$,CriteriaType%,Ignore_Case%))
					DebugLine("CONFIGARCHIVE:BUILDARCHIVE",DBG_ERC_NOT_AN_ERROR,False,"Valid file found: "+FullFilePath$)
					AddFile(FullFilePath$,Archive%)
				End If
			End If
			If (CurrentFileType=FILETYPE_DIRECTORY)
				FullFilePath$=FixPath(FullFilePath$,True)
				DebugLine("CONFIGARCHIVE:BUILDARCHIVE",DBG_ERC_NOT_AN_ERROR,False,"Directory found: "+FullFilePath$)
				If (RecursiveSearch)
					BuildArchive(Archive,Ignore_Case,True,FixPath$(FullFilePath$,True),RecordsType,CriteriaType%,CriteriaString$,False)
				End If
			End If
		End If
		CurrentFile$=NextFile(SearchHandle%)
		FullFilePath$=SearchDir$+CurrentFile$
	Wend
	CloseDir SearchHandle
	DebugLine("CONFIGARCHIVE:BUILDARCHIVE",DBG_ERC_NOT_AN_ERROR,False,"Archive: "+Str(Archive)+" built with "+Str(CountAllRecordsInArchive%(Archive%))+" records.")
End Function		

;**********************************************************

; What happens with the generated archive archives is very much up to the user, but here's some typical functions that may
;be of use...

;**********************************************************

; Deletes all archives From Memory
Function DeleteAllArchives()
	Local nArchiveCount%=CountArchives%()
	DebugLine("CONFIGARCHIVE:DELETEALLARCHIVES",DBG_ERC_NOT_AN_ERROR,False,"Deleting all "+Str(nArchiveCount%))
	Local Del.Records
	For Del.Records=Each Records
		Delete Del.Records
	Next
	DebugLine("CONFIGARCHIVE:DELETEALLARCHIVES",DBG_ERC_NOT_AN_ERROR,False,"Archives total remaining: "+ZERO_STRING$)
End Function

; Deletes A Specific archive From Memory
Function DeleteArchive(Archive%=1)	
	Local Del.Records
	Local nArchiveCount%=CountArchives%()
	If (nArchiveCount%)
		DebugLine("CONFIGARCHIVE:DELETEARCHIVE",DBG_ERC_NOT_AN_ERROR,False,"Deleting archive: "+Str(Archive%)+" from total of "+(Str(nArchiveCount%)))
		
		For Del.Records=Each Records
			If (Del.Records<>Null)
				If 	(ArchiveRecordValid(Archive%,Del.Records))
					Delete Del.Records
				Else
					If ((nArchiveCount%>1) And (Archive%<nArchiveCount))
						If (ArchiveOfRecord%(Del.Records)>Archive%) Then Del\Archive%=Del\Archive%-1
					End If
				End If
			End If
		Next
		nArchiveCount%=nArchiveCount%-1
	End If
	DebugLine("CONFIGARCHIVE:DELETEARCHIVE",DBG_ERC_NOT_AN_ERROR,False,"Archives total remaining: "+(Str(nArchiveCount%)))
End Function
; Combines Archives. The NewArchive Flag, if set, will combine the archives as a new archive, otherwise the entries from
;ArchiveToCombine will be added to ArchiveMaster.
Function CombineArchivesAs(ArchiveMaster%=1,ArchiveToCombine%=1,AllowDuplicates%=True,NewArchive%=True)
	Local DoAdd%=True
	Local AddArchive%=ArchiveMaster%
	Local TotalArchives%=CountArchives%()
	If (NewArchive%=True) Then AddArchive%=TotalArchives%+1
	If (TotalArchives%>30)
		DebugLine("CONFIGARCHIVE:COMBINEARCHIVEAS",DBG_ERC_ARC_LIMITREACHED,False,"Arhieve total is: "+Str(TotalArchives%-1)+" Maximum: 30")
	Else	
		DebugLine("CONFIGARCHIVE:COMBINEARCHIVEAS",DBG_ERC_NOT_AN_ERROR,False,"Seeking records to combine from archive: "+Str(ArchiveToCombine%)+" to archive: "+Str(AddArchive%))
		Local nCount%=CountAllRecordsInArchive(ArchiveToCombine%)
		DebugLine("CONFIGARCHIVE:COMBINEARCHIVEAS",DBG_ERC_NOT_AN_ERROR,False,"Total records in archive "+Str(ArchiveToCombine%)+": "+Str(nCount%))
		Local IterRecords
		Local CombineRecord.Records
		Local RetrieveString$=NULL_STRING$
		For IterRecords=1 To nCount%
			DoAdd%=True
			CombineRecord.Records=GrabRecord.Records(IterRecords,ArchiveToCombine)
			RetrieveString$=GrabArchiveRecordEntireString$(IterRecords,ArchiveToCombine)
			;DebugLine("CONFIGARCHIVE:COMBINEARCHIVEAS",DBG_ERC_NOT_AN_ERROR,False,"Verifying record: "+Str(IterRecords%)+" contents "+RetrieveString$)
			If (GetIndexForSearch%(RetrieveString$,True,ArchiveMaster%))
				If (Not(AllowDuplicates%))
					DebugLine("CONFIGARCHIVE:COMBINEARCHIVEAS",DBG_ERC_NOT_AN_ERROR,False,"Record "+Str(IterRecords%)+" of "+Str(nCount%)+" invalid. Duplicates disallowed")
					DoAdd%=False
				End If
			End If
			If (DoAdd%)
				DebugLine("CONFIGARCHIVE:COMBINEARCHIVEAS",DBG_ERC_NOT_AN_ERROR,False,"Copying record: "+Str(IterRecords%)+" of "+Str(nCount%)+" from archive: "+Str(ArchiveToCombine%)+" to Archive: "+Str(AddArchive%))
				AddFile(RetrieveString,AddArchive%)
			End If
			RemoveArchiveRecordsByString(RetrieveString$,True,ArchiveToCombine%)
		Next	
		DebugLine("CONFIGARCHIVE:COMBINEARCHIVEAS",DBG_ERC_NOT_AN_ERROR,False,"Archives "+Str(ArchiveToCombine)+" and "+Str(ArchiveMaster%)+" combined into "+(AddArchive%))
	End If
End Function

Function RemoveRecordByIndex(Index%=1,Archive%=1)
	DebugLine("CONFIGARCHIVE:REMOVERECORDBYINDEX",DBG_ERC_NOT_AN_ERROR,False,"Removing record: "+Str(Index%)+" from archive: "+Str(Archive%))
	Delete GrabRecord.Records(Index%,Archive%)
End Function

Function RemoveArchiveRecordsByString(CheckString$=NULL_STRING$,Exact%=True,Archive%=1)
	DebugLine("CONFIGARCHIVE:REMOVEARCHIVERECORDSBYSTRING",DBG_ERC_NOT_AN_ERROR,False,"Seeking matching records: "+CheckString$+" from archive: "+Str(Archive))
	Local DoRemove%=GetIndexForSearch%(CheckString$,Exact%,Archive)
	While (DoRemove)
		DebugLine("CONFIGARCHIVE:REMOVEARCHIVERECORDSBYSTRING",DBG_ERC_NOT_AN_ERROR,False,"Found matching record: "+Str(DoRemove%)+" in archive: "+Str(Archive))
		RemoveRecordByIndex(DoRemove%,Archive%)
		DoRemove=GetIndexForSearch%(CheckString$,Exact%,Archive%)
	Wend
End Function

; Introduxes a hard limit of 30 archives, though it's unlikely so many would ever be needed.
Function CountArchives%()
	Local nCount%
	Local itercount%
	Local BitCheck%
	Local CountRecords.Records
	For CountRecords.Records = Each Records
		If (CountRecords.Records<>Null)
			BitCheck%=(2^(CountRecords\Archive%))
			If (Not(LogicCheck(nCount%,BitCheck%))) Then nCount%=nCount%+BitCheck%
		End If
	Next
	For itercount%=1 To 30
		If (Not(LogicCheck((1 Shr itercount),nCount%))) Then Exit
	Next
	DebugLine("CONFIGARCHIVE:COUNTARCHIVES",DBG_ERC_NOT_AN_ERROR,False,"Found "+Str(itercount%)+" archives")
	Return itercount%
End Function

Function CountAllRecordsInArchive%(Archive%=1)
	Local nCount%
	Local CountRecords.Records
	For CountRecords.Records = Each Records
		If (ArchiveRecordValid%(Archive%,CountRecords.Records)) Then nCount%=nCount%+1
	Next
	DebugLine("CONFIGARCHIVE:COUNTALLRECORDSINARCHIVE",DBG_ERC_NOT_AN_ERROR,False,"Total records counted in archive "+Str(Archive%)+": "+Str(nCount%))
	Return nCount%
End Function

Function GrabRecord.Records(RecordIndex%=1,Archive%=1)
	DebugLine("CONFIGARCHIVE:GRABRECORD",DBG_ERC_NOT_AN_ERROR,False,"Retrieving record data: "+Str(RecordIndex%)+" from archive: "+Str(Archive%))
	Return ArchiveRecordCount.Records(Archive%,RecordIndex%)
End Function

Function GetIndexForSearch%(CheckString$=NULL_STRING$,Exact%=True,Archive%=1)
	Local nCount%
	Local CountRecords.Records
	Local TestString$
	;DebugLine("CONFIGARCHIVE:GETINDEXFORSEARCH",DBG_ERC_NOT_AN_ERROR,False,"Seeking matching records: "+CheckString$+" from archive: "+Str(Archive))
	For CountRecords.Records=Each Records
		If ArchiveRecordValid%(Archive%,CountRecords.Records)
			nCount%=nCount%+1
			TestString$=GrabArchiveRecordEntireString$(nCount%,Archive%)Lower$(CheckString$)
			If CheckString$=GrabArchiveRecordEntireString$(nCount%,Archive%) Then Return nCount
			If  (Instr(Lower$(TestString$),CheckString$>0))
				If (Not(Exact%))
					DebugLine("CONFIGARCHIVE:GETINDEXFORSEARCH",DBG_ERC_NOT_AN_ERROR,False,"Found matching record: "+Str(nCount%))
					Return nCount%
				End If
			End If	
		End If
	Next
	DebugLine("CONFIGARCHIVE:GETINDEXFORSEARCH",DBG_ERC_NOT_AN_ERROR,False,"No matching records")
	Return False
End Function
;Returns the actual pathname stored in record
Function GrabArchiveRecordPathString$(RecordIndex=1,Archive=1)
	
	Local sReturn$=NULL_STRING$
	Local ReturnRecord.Records=GrabRecord.Records(RecordIndex,Archive)
	If (ReturnRecord.Records<>Null)
		sReturn$=EndDirPath$(ReturnRecord\Returned_Path$)
	End If
	DebugLine("CONFIGARCHIVE:GRABARCHIVERECORDEXTENSIONSTRING",DBG_ERC_NOT_AN_ERROR,False,"Retrieving record "+RecordIndex+" of archive: "+Str(Archive%)+" File Extension: "+Chr$(34)+sReturn$+Chr(34))
	Return sReturn$			
End Function

;Returns the actual filename stored in record
Function GrabArchiveRecordFilenameString$(RecordIndex=1,Archive=1)
	Local sReturn$=NULL_STRING$
	Local ReturnRecord.Records=GrabRecord.Records(RecordIndex%,Archive%)
	If (ReturnRecord.Records<>Null)
		sReturn$=ReturnRecord\Returned_Filename$
	End If
	DebugLine("CONFIGARCHIVE:GRABARCHIVERECORDFILENAMESTRING",DBG_ERC_NOT_AN_ERROR,False,"Retrieving record "+RecordIndex+" of archive: "+Str(Archive%)+" File Name: "+Chr$(34)+sReturn$+Chr(34))
	Return sReturn$			
End Function

;Returns the actual fileextension stored in record
Function GrabArchiveRecordExtensionString$(RecordIndex%=1,Archive%=1)
	Local sReturn$=NULL_STRING$
	Local ReturnRecord.Records=GrabRecord.Records(RecordIndex,Archive)
	If (ReturnRecord.Records<>Null)
		sReturn$=ReturnRecord\Returned_Extension$
	End If
	DebugLine("CONFIGARCHIVE:GRABARCHIVERECORDEXTENSIONSTRING",DBG_ERC_NOT_AN_ERROR,False,"Retrieving record "+RecordIndex+" of archive: "+Str(Archive%)+" File Extension: "+Chr$(34)+sReturn$+Chr(34))
	Return sReturn$			
End Function

;Returns the entire File path, name plus extension string stored in record
Function GrabArchiveRecordEntireString$(RecordIndex=1,Archive%=1)
	Local sReturn$=NULL_STRING$
	Local ReturnRecord.Records=GrabRecord.Records(RecordIndex%,Archive%)
	If (ReturnRecord.Records<>Null)
		sReturn$=EndDirPath$(ReturnRecord\Returned_Path$)
		sReturn=sReturn$+ReturnRecord\Returned_Filename$
		sReturn=sReturn$+ReturnRecord\Returned_Extension$
	End If
	DebugLine("CONFIGARCHIVE:GRABARCHIVERECORDENTIRESTRING",DBG_ERC_NOT_AN_ERROR,False,"Retrieving entire record "+RecordIndex+" of archive: "+Str(Archive%)+SPACER$+sReturn$)
	Return sReturn$			
End Function

;Returns the actual fileExtension stored in record
Function GrabArchiveRecordFileType%(RecordIndex%=1,Archive%=1)
	Local nReturn%=FILETYPE_INVALID
	Local ReturnRecord.Records=GrabRecord.Records(RecordIndex,Archive)
	If (ReturnRecord.Records<>Null)
		nReturn%=ReturnRecord\Returned_FileType%
	End If
	DebugLine("CONFIGARCHIVE:GRABARCHIVERECORDFILETYPE",DBG_ERC_NOT_AN_ERROR,False,"Retrieving record "+RecordIndex+" of archive: "+Str(Archive%)+" File Type: "+Str(nReturn%))
	Return nReturn%			
End Function

;**********************************************************
;System functions for the above to work. Not user-called.

Function AddFile(FullFilePath$,Archive%=1)
	
	FullFilePath$=FixPath$(FullFilePath$)
	
	Local AddRecords.Records
	Local AddPath$=NULL_STRING$
	
	Local AddFileName$=NULL_STRING$
	Local AddExtension$=NULL_STRING$
	Local AddFileType%=FileType(FullFilePath$)
	
	AddPath$=FixPath$(GetContainerDir$(FullFilePath$),True)
	
	AddFileName$=FixPath$(GetFilename$(FullFilePath$))
	AddExtension$=GetExtension$(AddFileName$)
	
	If (AddExtension$<>NULL_STRING$)
		AddFileName$=Left$(AddFileName$,Len(AddFileName$)-Len(AddExtension$))
	End If
	
	If (AddFileType=FILETYPE_INVALID)
		DebugLine("CONFIGARCHIVE:ADDFILE",DBG_ERC_FILE_DELETE_MISSING,False,FullFilePath$)
	Else
		
		If (AddFileType=FILETYPE_DIRECTORY)
			AddPath$=FixPath$(AddPath$,True)
			AddFileName$=NULL_STRING
			AddExtension$=NULL_STRING
		End If
		
		DebugLine("CONFIGARCHIVE:ADDFILE",DBG_ERC_NOT_AN_ERROR,False,"Adding "+FullFilePath$+" to archive: "+Str(Archive%))
		
		AddRecords.Records=New Records
		
		AddRecords\Returned_Path$=AddPath$
		AddRecords\Returned_Filename$=AddFileName$
		AddRecords\Returned_Extension$=AddExtension$
		
		AddRecords\Archive%=Archive%
		AddRecords\Returned_FileType=AddFileType%
	End If
	
End Function

Function CheckCriteria%(Recordstring$,CritString$,CriteriaType,Case_Insensitive%=True)
	
	If (Not(IGNORE))
		CritString$=Upper$(CritString$)
		Recordstring$=Upper$(Recordstring$)
	End If
	
	Local ReturnCheck%=False
	Local MaxCritTypes%=CountAllCritStrings%(CritString$)
	If (MaxCritTypes%=0)
		CritString$=Replace(CritString$,CRITERIA_SEPARATOR$,NULL_STRING$)
		MaxCritTypes%=1		
	End If
	Local IterCrits%
	For IterCrits%=1 To MaxCritTypes%
		ReturnCheck%=DoCheck%(CriteriaType%,ResolveStringPart$(CritString$,IterCrits),Recordstring$,ReturnCheck%)
	Next		
	Return ReturnCheck%
End Function

Function DoCheck%(CriteriaType%,CritString$,Recordstring$,CurrentCheck%)
	
	;DebugLine("CONFIGARCHIVE:DOCHECK",DBG_ERC_NOT_AN_ERROR,False,"Seeking matching criteria in "+Recordstring$)
	
	Local ORCheck%=False,ANDCheck%=False,NOTCheck%=False,SPECCheck%=False,ThisCheck%
	
	If (LogicCheck%(CriteriaType%,CRITERIA_OR%)) Then ORCheck%=(Instr(Recordstring$,CritString$))
	If (LogicCheck%(CriteriaType%,CRITERIA_AND%))Then ANDCheck%=(Instr(Recordstring$,CritString$))
	
	If (LogicCheck%(CriteriaType%,CRITERIA_SPECIFIC%)) Then SPECCheck%=True
	If (LogicCheck%(CriteriaType%,CRITERIA_NOT%))Then NOTCheck%=True
	
	If (ORCheck%) Then ThisCheck%=True
	If (ANDCheck%) Then ThisCheck%=CurrentCheck%
	If (SPECCheck) Then ThisCheck%=ThisCheck*(Recordstring$=CritString$)
	
	If (NOTCheck) Then ThisCheck=(Not(ThisCheck))
	
	If (ORCheck) Then ThisCheck%=(ThisCheck% Or CurrentCheck%)
	
	;DebugLine("CONFIGARCHIVE:DOCHECK",DBG_ERC_NOT_AN_ERROR%,False,"Substring: "+CritString$+" considered: ("+Str(ThisCheck%)+")")
	;DebugLine("CONFIGARCHIVE:DOCHECK",DBG_ERC_NOT_AN_ERROR%,False,"Cumulative progress on: "+Recordstring$+" considered: ("+Str(CurrentCheck%)+")")
	
	Return ThisCheck%
	
End Function

Function LogicCheck%(Bit1,Bit2)
	Return ((Bit1 And Bit2)=Bit2)
End Function

Function CountAllCritStrings%(CritString$)
	Local DebugFullstring$=CritString$
	Local nCount%=0
	Local Position%
	If (CritString$<>NULL_STRING$)
		nCount%=nCount%+1
		Position%=(Instr(CritString$,CRITERIA_SEPARATOR$))
		While (Position)
			nCount%=nCount%+1
			CritString$=Right$(CritString$,Len(CritString$)-(Instr(CritString$,CRITERIA_SEPARATOR$,Position%)+2))
			Position%=(Instr(CritString$,CRITERIA_SEPARATOR$,Position%))
		Wend
	End If
	;DebugLine("CONFIGARCHIVE:COUNTALLCRITSTRINGS",DBG_ERC_NOT_AN_ERROR%,False,"Total of "+nCount%+" substrings found within "+DebugFullstring$)
	Return nCount%
End Function

Function ResolveStringPart$(CritString$,Count%=1)
	Local nCount%=0
	If (Not(Instr(CritString,CRITERIA_SEPARATOR)) Or (Count%=1) Or (nCount%=Count%) Or (CritString$=NULL_STRING$))
		Return FixCriteria(CritString$)	
	End If
	Local Position%=1
	Local MyString$=CritString$
	While(nCount%<Count%)
		Position%=Instr(CritString,CRITERIA_SEPARATOR,Position)
		nCount%=nCount%+1
		If ((nCount%=Count%) Or (Position%=0)) Then Exit
		MyString$=Left(CritString$,Position%-1)
	Wend
	Position%=Instr(MyString$,CRITERIA_SEPARATOR$,1)
	If (Position%=1) Then Position%=Instr(MyString$,CRITERIA_SEPARATOR$,2)
	If (Position%=0) Then Position%=-2
	MyString$=FixCriteria$(Right(MyString,Len(MyString$)-(Position%+2)))
	;DebugLine("CONFIGARCHIVE:RESOLVESTRINGPARTS",DBG_ERC_NOT_AN_ERROR%,False,"Substring: "+Count%+" resolved as "+MyString$)
	Return MyString$
End Function

Function ArchiveOfRecord%(Record.Records)
	If (Record.Records = Null) Then Return False
	Return Record\Archive%
End Function

Function ArchiveRecordValid%(Archive%=1,Record.Records)
	Return (ArchiveOfRecord%(Record.Records)=Archive%)
End Function

Function ArchiveRecordCount.Records(Archive%,CountIndex%)
	Local Count%
	Local CountRecords.Records
	For CountRecords.Records=Each Records
		Count%=Count%+1
		If ((ArchiveRecordValid%(Archive%,CountRecords.Records)) And (Count%=CountIndex%)) Then Return CountRecords.Records
	Next
	Return Null
End Function

Function FixCriteria$(CritString$)
	Return Trim(Replace(CritString$,CRITERIA_SEPARATOR$,NULL_STRING$))
End Function

Function GetContainerDir$(path$) ; Returns the Directory from the specifed path
	Local iterbyte
	For iterbyte = Len(path$) To 1 Step -1
		If ((Mid(path$,iterbyte,1)= PATH_SEPARATOR) Or (Mid(path$,iterbyte,1)= PATH_SEPARATOR_REVERSE))
			Return FixPath$(Left$(path$,iterbyte),True)
			Exit
		EndIf
	Next
	Return NULL_STRING$
End Function

Function GetFilename$(path$) ; Returns the file from the specifed path
	Local iterbyte%
	For iterbyte% = Len%(path$) To 1 Step -1
		If ((Mid$(path$,iterbyte,1)= PATH_SEPARATOR) Or (Mid$(path$,iterbyte,1)=PATH_SEPARATOR_REVERSE))
			Return FixPath$(Right(path$,Len(path$)-iterbyte),False)
			Exit
		EndIf
	Next
	Return path$
End Function

Function GetExtension$(FileName$) ; Returns the Extension from the specifed path or filename
	If ((Instr(FileName$,EXTENSION_SEPARATOR)>0) And (Len%(FileName$)>2))
		Local nCount
		Local sReturn$=NULL_STRING$
		For nCount = Len%(FileName$) To 1 Step -1
			If (Mid$(FileName$,nCount,1)=EXTENSION_SEPARATOR)
				sReturn$=FixPath$(Right$(Lower$(FileName$),Len(FileName$)-(nCount)),False)
				Exit
			End If
		Next
	End If
	Return sReturn
End Function

Function FixPath$(Path$,Dir%=False)
	Path$=Replace$(Path$,PATH_SEPARATOR_REVERSE$,PATH_SEPARATOR$)
	Path$=Replace$(Path$,PATH_SEPARATOR$+PATH_SEPARATOR$,PATH_SEPARATOR$)
	If ((Right$(Path$,1)=PATH_SEPARATOR$)) Then Path$=Left$(Path$,Len(Path$)-1)
	If (Dir%) Then Path$=EndDirPath$(Path$)
	Return Path$
End Function	

Function FixLink$(Path$)
	Path$=Replace$(Path$,PATH_SEPARATOR$,PATH_SEPARATOR_REVERSE$)
	Path$=Replace$(Path$,PATH_SEPARATOR_REVERSE$+PATH_SEPARATOR_REVERSE$,PATH_SEPARATOR_REVERSE$)
	Path$=Replace$(Path$,NET_HTTP$+TIME_SEPARATOR$+PATH_SEPARATOR_REVERSE$,NET_HTTP$+TIME_SEPARATOR$+PATH_SEPARATOR_REVERSE$+PATH_SEPARATOR_REVERSE$)
	If ((Right$(Path$,1)=PATH_SEPARATOR_REVERSE$)) Then Path$=Left$(Path$,Len(Path$)-1)
	Return Path$
End Function	

Function EndDirPath$(Path$)
	If (Path$=NULL_STRING) Then Path$=CurrentDir()
	If (Right$(Path$,1)<>PATH_SEPARATOR$)
		Path$=Path$+PATH_SEPARATOR$
	End If
	Return Path$
End Function

Function FileTypeCriteriaValid(CurrentFileType%,CriteriaSelection%=FILETYPE_EITHER)
	If CurrentFileType=FILETYPE_INVALID Then Return False
	If (CriteriaSelection=FILETYPE_EITHER) Then Return True
	Return (CriteriaSelection%=CurrentFileType%)
End Function
