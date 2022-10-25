; ID: 934
; Author: Klaas
; Date: 2004-02-13 17:49:58
; Title: File-Function collection
; Description: some function to deal with files and folders

;some functions to deal with files and folders
;i use arrays for easy access, but can be changed to types easily
;
;hope that helps someone with this anoying recursive folder stuff


Dim xff_splitresult$(100)
Dim ff_results$(100)

;----------------------------------------- Functions
;ff_is_dir(dirname$)
;returns true if dirname$ is a existing directory

;------
;ff_is_file(filename$)
;returns true if filename$ is a existing file


;------
;ff_searchFile(name$,path$)
;name$ = searchpattern like "*.jpg" or "*metal*.png" ... "*" is the wildcard
;path$ = the path to search in
;returns the number of matches .. matches can then be found in "ff_results" array

;------
;ff_pathname$(filename$)
;returns the file path ... "D:\progs\file.jpg" -> "D:\progs"

;------
;ff_filename$(filename$)
;returns the file name ... "D:\progs\file.jpg" -> "file.jpg"

;------
;ff_dirname$(filename$)
;returns the directory name ... "D:\progs\file.jpg" -> "\progs"

;------
;ff_volumename$(filename$)
;returns the volume name ... "D:\progs\file.jpg" -> "D:"

;------
;ff_copy(file$,new_file$,[overwrite],[move])
;file = original file
;new_file = file to copy to
;overwrite = set to true if you wish to overwrite an existing file
;move = set to true if you wish to move the original file
;returns true on success

;------
;ff_mkpath(path$)
;creates a given filepath
;path = path to create ... "D:\progs\things\foo" .. if "D:\progs" exists then "D:\progs\things" and ""D:\progs\things\foo" wil be created
;returns true on success

;------
;ff_smartcopy(file$,new_file$,[overwrite],[move])
;file = original file or folder
;new_file = file or folder to copy to
;overwrite = set to true if you wish to overwrite an existing file's
;move = set to true if you wish to move the original file's
;returns true on success
;
;this function can copy whole directory trees with all files in it

;------
;ff_unlink(file$)
;file = file or folder to delete
;returns true on success
;
;deletes files or whole directory structures

;------

;file functions
Function ff_is_dir(folder$)
	If FileType(folder$) = 2 Then Return True
End Function

Function ff_is_file(file$)
	If FileType(file$) = 1 Then Return True
End Function

Function ff_searchFile(name$,path$,count=0)
	comp = xff_split("*",name$)

	dir = ReadDir(path$)
	If Not dir Then Return False	
	entry$ = NextFile(dir)

	While (entry$ <> "")
		If entry$ <> "." And entry$ <> ".."
			full_entry$ = path$+"\"+entry$
			
			Select FileType(full_entry$)
			Case 0
				Return False
			Case 1
				result$ = full_entry
				found = True
				pos = 1
				For i=0 To comp - 1
					If Not xff_splitresult(i) = ""
						pos = Instr(entry$,xff_splitresult(i),pos)
						If Not pos
							found = False
							Exit
						EndIf
					EndIf
				Next
				If found
					ff_results(count) = path$+"\"+entry
					count = count + 1
				EndIf
			Case 2
				count = ff_searchFile(name$,full_entry$,count)
			End Select
		End If
		entry$ = NextFile(dir)
	Wend
	Return count
End Function

Function ff_pathname$(txt$)
	txt$ = Replace(txt$,"/","\")
	length = xff_split("\",txt$)
	
	dir$ = xff_splitresult(0)
	For i=1 To length-2
		dir$ = dir$ + "\" + xff_splitresult(i)
	Next
	
	Return dir$
End Function

Function ff_filename$(txt$)
	txt$ = Replace(txt$,"/","\")
	length = xff_split("\",txt$)
	
	file$ = xff_splitresult(0)
	For i=0 To length - 1
		file$ = xff_splitresult(i)
	Next
	
	Return file$
End Function

Function ff_dirname$(txt$)
	txt$ = Replace(txt$,"/","\")
	xff_split(":\",txt$)

	txt$ = xff_splitresult(1)
	length = xff_split("\",txt$)
	
	For i=0 To length-2
		dir$ = dir$ + "\" + xff_splitresult(i)
	Next
	
	Return dir$
End Function

Function ff_volumename$(txt$)
	length=xff_split(":",txt$)
	
	Return xff_splitresult(0)
End Function

Function ff_mkpath(path$)
	path$=Replace(path$,"/","\")
	length=xff_split("\",path$)
	
	path_by_now$=xff_splitresult(0)
	For i=0 To length-1
		If Not FileType(path_by_now$) = 2 
			CreateDir(path_by_now$)
		End If
		If Not FileType(path_by_now$) Then Return False
		path_by_now$ = path_by_now$+"/"+xff_splitresult(i+1)
	Next
	Return True
End Function

Function ff_unlink(file$)
	Select FileType(file$)
	Case 0
		Return False
	Case 1
		DeleteFile(file$)
		If FileType(file$) <> 0 Then Return False
	Case 2
		dir = ReadDir(file$)
		entry$ = NextFile(dir)
		While (entry$ <> "")
			If entry$ <> "." And entry$ <> ".."
				full_entry$ = file$+"\"+entry$
				Select FileType(full_entry$)
				Case 0
				Case 1
					If Not ff_unlink(full_entry$) Then Return False
				Case 2
					If Not ff_unlink(full_entry$) Then Return False
				End Select
			End If
			entry$=NextFile(dir)
		Wend
		DeleteDir(file$)
	End Select
	Return True
End Function

Function ff_copy(file$,new_file$,overwrite=0,move=0)
	If FileType(file$) = 2 Then Return False
	
	If Not overwrite
		If FileType(new_file$) Return False
	End If

	CopyFile(file$,new_file$)	
	If FileType(new_file$) <> 1 Return False	
	
	If move
		DeleteFile(file$)
	End If
	Return True	
End Function

Function ff_smartcopy(file$,new_file$,overwrite=0,move=0)
	If Not overwrite
		If FileType(new_file$)=1 Return False
	End If

	Select FileType(file$)
	Case 0
		Return False
	Case 1
		dir$ = ff_pathname$(new_file$)
		
		If Not ff_is_dir(dir$)
			If Not ff_mkpath(dir$) Then Return False
		End If

		CopyFile(file$,new_file$)
		If Not ff_is_file(new_file$) Then Return False
		If move Then DeleteFile(file$)
	Case 2
		ff_mkpath(new_file$)
		
		dir = ReadDir(file$)
		entry$ = NextFile(dir)
		While (entry$ <> "")
			If entry$ <> "." And entry$ <> ".."
				full_entry$ = file$+"\"+entry$
				Select FileType(full_entry$)
				Case 0
				Case 1
					If Not ff_smartcopy(full_entry$,new_file$+"\"+entry$,overwrite,move) Then Return False
				Case 2
					If Not ff_smartcopy(full_entry$,new_file$+"\"+entry$,overwrite,move) Then Return False
				End Select
			End If
			entry$=NextFile(dir)
		Wend
		If move Then DeleteDir(file$)
	End Select
	Return True
End Function

;help functions
Function xff_split(seperator$,txt$)
	pos=Instr(txt$,seperator$,1)	
	count = 1
	While (pos)
		xff_splitresult(count-1) = Left(txt$,pos-Len(seperator))
		
		txt$=Right(txt$,Len(txt$)-pos-Len(seperator)+1)
		pos=Instr(txt$,seperator$,1)
		count=count+1
	Wend
	xff_splitresult(count-1)=txt$
	
	Return count
End Function
