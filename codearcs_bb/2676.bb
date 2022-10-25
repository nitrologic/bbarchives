; ID: 2676
; Author: Malice
; Date: 2010-03-24 14:24:36
; Title: RenameFile
; Description: Functions to allow for file renaming

Function RenameFile(Path$="",NewFile$="",Count_Renamed=False)
	;Count_Renamed should be incremented each call for batch processing
	Local Consecutor$=""
	If (Count_Renamed) Then Consecutor$=Str(Count_Renamed)
	If (Right(path,1)=".") Then Return
	If (Path$="") Then Path$=CurrentDir()
	If (Right$(Path,1)="\") Then path$=Left(path$,Len(path$)-1)
	If (NewFile$="")
		If (FileType(Path$)=2)
			NewFile$="Renamed Folder"
			ExecuteRename(Path$,NewFile$+Consecutor$)
		Else
			If (FileType(Path$)=1)
				NewFile$="Renamed File"
			Else
				Return
			End If
		End If
	End If	
	Local Extension_separator=Instr(Path$,".")
	Local Extension$=""
	Local Path_Separators
	Local LastPath=0
	While (Path_separators)
		LastPath=Path_Separators
		Path_Separators=Instr(Path$,"\",Path_Separators+1)
	Wend
	If ((Not(Instr(NewFile$,"."))) And (LastPath<Extension_Separator))Then Extension$="."+Right(Path,(Len(Path)-Extension_Separator))
	Return ExecuteRename(Path$,NewFile$+Consecutor$+Extension$)
End Function 
Function ExecuteRename%(Path$,NewFile$)
	ExecFile(Chr(34)+"cmd"+Chr(34)+" /c RENAME "+Chr(34)+Path$+Chr(34)+" "+Chr(34)+NewFile$+Chr(34))
	Local Path_Separators
	Local LastPath=0
	While (Path_separators)
		LastPath=Path_Separators
		Path_Separators=Instr(Path$,"\",Path_Separators+1)
	Wend
	Local Parent$=Left$(Path,LastPath)+NewFile$
	Return (FileType(Parent$))
End Function
