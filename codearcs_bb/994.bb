; ID: 994
; Author: TygerWulf
; Date: 2004-04-12 03:53:21
; Title: Random Access File Utilities
; Description: Allows you to randomly access a file

Type Datafile
	Field FName$
	Field Size
	Field ID
End Type

;FWriteS(File.DataFile, Key$, KeyVal$)
;File.DataFile--A file opened with FOpen
;Key$--The identifier that will be used to retrieve the data later
;KeyVal$--The Value to be stored for retrieval later
;Writes a string to a file or updates a string value in the file.

Function FWriteS(File.DataFile, Key$,KeyVal$)
	Key$ = "$" +Key$
	If File\Size = 0 Then
		X$ = Key$+"="+KeyVal
		WriteLine(File\ID,X$)
		QSFile(File)
		Return 2
	End If
	P = KeyExists(File, Key$)
	If P <> -1 Then

		SeekFile(File\ID,P)
		X$ = ReadLine(File\ID)
		SeekFile(File\ID,P)
		L = Len(X$)
		X$ = Key$+"="+KeyVal
		If Len(X$) < L Then X$ = LSet$(X$,L)
		WriteLine(File\ID, X$)
		QSFile(File)
		Return 1 ;Updated an existing value
	Else
		SeekFile(File\ID,File\Size)
		X$ = Key$+"="+KeyVal
		WriteLine(File\ID,X$)
		QSFile(File)
		Return 2 ;Created a new value
	End If
	
	Return 0

End Function


;FReadS$(File.DataFile, Key$)
;File.DataFile--A File Opened with FOpen
;Key$--The identifier to look for in the file, where the information is stored
;Returns the string value found at identifier Key$ or "" if Key$ was not found

Function FReadS$(File.DataFile, Key$)
	Key$ = "$" +Key$
	P = KeyExists(File, Key$)
	If P <> -1 Then
		SeekFile(File\ID,P)
		X$ = ReadLine(File\ID)
		L = Instr(X$, "=")
		If L = 0 Then Return 0
		L = Len(X$) - (L)
		Return Right$(X$, L)

	End If
	Return ""
	
End Function


;FWriteI(File.DataFile, Key$, KeyVal%)
;File.DataFile--A file opened with FOpen
;Key$--The identifier that will be used to retrieve the data later
;KeyVal%--The Value to be stored for retrieval later
;Writes an integer to a file or updates an integer value in the file.

Function FWriteI(File.DataFile, Key$,KeyVal%)
	Key$ = "%" +Key$
	If File\Size = 0 Then
		X$ = Key$+"="+Str(KeyVal)
		WriteLine(File\ID,X$)
		QSFile(File)
		Return 2
	End If
	P = KeyExists(File, Key$)
	If P <> -1 Then
		SeekFile(File\ID,P)
		X$ = ReadLine(File\ID)
		SeekFile(File\ID,P)
		L = Len(X$)
		X$ = Key$+"="+Str(KeyVal)
		If Len(X$) < L Then X$ = LSet$(X$,L)
		WriteLine(File\ID, X$)
		QSFile(File)
		Return 1 ;Updated an existing value
	Else
		SeekFile(File\ID,File\Size)
		X$ = Key$+"="+Str(KeyVal)
		WriteLine(File\ID,X$)
		QSFile(File)
		Return 2 ;Created a new value
	End If
	
	Return 0

End Function


;FReadI%(File.DataFile, Key$)
;File.DataFile--A File Opened with FOpen
;Key$--The identifier to look for in the file, where the information is stored
;Returns the intiger value found at identifier Key$ or 0 if Key$ was not found

Function FReadI%(File.DataFile, Key$)

	Key$ = "%" +Key$
	P = KeyExists(File, Key$)
	If P <> -1 Then
		SeekFile(File\ID,P)
		X$ = ReadLine(File\ID)
		L = Instr(X$, "=")
		If L = 0 Then Return 0
		L = Len(X$) - (L)
		X$ = Right$(X$, L)
		L = X$
		Return L

	End If
	Return 0
	
End Function


;FWriteF(File.DataFile, Key$, KeyVal#)
;File.DataFile--A file opened with FOpen
;Key$--The identifier that will be used to retrieve the data later
;KeyVal#--The Value to be stored for retrieval later
;Writes a float to a file or updates a float value in the file.

Function FWriteF(File.DataFile, Key$,KeyVal#)
	Key$ = "#" +Key$
	If File\Size = 0 Then
		X$ = Key$+"="+Str(KeyVal)
		WriteLine(File\ID,X$)
		QSFile(File)
		Return 2
	End If
	P = KeyExists(File, Key$)
	If P <> -1 Then
		SeekFile(File\ID,P)
		X$ = ReadLine(File\ID)
		SeekFile(File\ID,P)
		L = Len(X$)
		X$ = Key$+"="+Str(KeyVal)
		If Len(X$) < L Then X$ = LSet$(X$,L)
		WriteLine(File\ID, X$)
		QSFile(File)
		Return 1 ;Updated an existing value
	Else
		SeekFile(File\ID,File\Size)
		X$ = Key$+"="+Str(KeyVal)
		WriteLine(File\ID,X$)
		QSFile(File)
		Return 2 ;Created a new value
	End If
	
	Return 0

End Function


;FReadF#(File.DataFile, Key#)
;File.DataFile--A File Opened with FOpen
;Key$--The identifier to look for in the file, where the information is stored
;Returns the float value found at identifier Key$ or 0.0 if Key$ was not found

Function FReadF#(File.DataFile, Key$)

	Key$ = "#" +Key$
	P = KeyExists(File, Key$)
	If P <> -1 Then
		SeekFile(File\ID,P)
		X$ = ReadLine(File\ID)
		L = Instr(X$, "=")
		If L = 0 Then Return 0
		L = Len(X$) - (L)
		X$ = Right$(X$, L)
		L = X$
		Return L

	End If
	Return 0
	
End Function


;KeyExists(File.DataFile, Key$)
;File.DataFile--A File opened with FOpen
;Key$--The identifier to search for.
;Returns the location in the file where Key$ is found, or -1 if Key$ was not found

Function KeyExists(File.DataFile, Key$)

	L = Len(Key$)
	SeekFile(File\ID,0)
	While Not Eof(File\ID)
		N = FilePos(File\ID)
		X$ = ReadLine(File\ID)
		If Lower$(Key$) = Lower$(Left$(X$,L)) Then
			Return N
		End If
	Wend
	Return -1

End Function


;QSFile(File.DataFile)
;File.Datafile--A file opened with FOpen
;Closes and reopens a file to save data changes to disk.

Function QSFile(File.DataFile)

	CloseFile(File\ID)
	File\ID = OpenFile(File\FName$)
	UpdateFile(File)
	Return

End Function


;UpdateFile(File.DataFile)
;File.Datafile--A file opened with FOpen
;Updates the information of a Datafile type.  Currently only updates size.

Function UpdateFile(File.DataFile)

	File\Size = FileSize(File\FName$)
	Return

End Function

;FOpen.DataFile(Filename$,RC)
;Filename$--a string containing the file you want to open
;RC--a flag indicating if the file should be created when not found
;Opens a file and returns a Datafile custom type with information and a
;pointer to the file in its fields.

Function FOpen.DataFile(Filename$,RC)

File.DataFile = New DataFile
Select RC
	Case 0 ;Read/Write, create if not found
		If ff_is_file(Filename$) <> True Then
			CurFile = WriteFile(FileName$)  ;Creating Unfound File
			CloseFile(CurFile)
		End If
		File\ID = OpenFile(Filename$)
		File\FName$ = Filename$
		File\Size = FileSize(Filename$)
		Return File
		
		
	Case 1 ;Read/Write, Return Error on no file found
		If ff_is_file(Filename$) <> True Then
			Return Null ;File not found
		End If
		File\ID = OpenFile(Filename$)
		File\FName$ = Filename$
		File\Size = FileSize(Filename$)
		Return File

	Default
		Delete File
		Return Null ;Unexpected error
	
End Select

End Function


;FClose(File.DataFile)
;File.DataFile--A File opened with FOpen
;Closes a file previously opened with FOpen

Function FClose(File.DataFile)

	CloseFile(File\ID)
	Delete File

End Function

;A function to check if a file exists
;Not mine.

Function ff_is_file(file$)
	If FileType(file$) = 1 Then Return True
End Function
