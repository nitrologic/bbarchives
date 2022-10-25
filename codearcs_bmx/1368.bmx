; ID: 1368
; Author: Robert
; Date: 2005-05-08 07:10:00
; Title: Enumerate Files &amp; Folders
; Description: Creates a list of all files / folders in a given folder and its subdirectories

'File & Folder Enumeration 
'Public Domain Code - Robert Knight 2005

'These are the minimum set of imports needed to use these functions
'==================================================================
Strict
Framework BRL.Blitz

Import BRL.FileSystem
Import BRL.LinkedList
Import BRL.StandardIO

Rem

Example Usage
====================================

-- This prints the names of all files on the C:\ drive
enumFilesCallback(Print,"C:\")

-- This prints the names of all folders on the C:\ drive
enumFoldersCallback(Print,"C:\")

-- This is the format for a callback function:  Returns Int, with one string parameter:

Function myCallback(path:String)
	
	<code to do something with the path goes here>
	
End Function

-- This creates a list, and fills it with the names of all files on the C:\ drive

Local fileList=New TList
enumFiles(fileList,"C:\")

-- This creates a list, and fills it with the names of all folders on the C:\ drive

Local folderList=New TList
enumFolders(fileList,"C:\")

-- This would print the contents of a list filled using enumFiles or enumFolders

For Local file=EachIn fileList
	Print file
Next

Usage Notes
===========

For most purposes, the enumFiles and enumFolders functions are probably more convenient.
For uses where there are likely to be many files or folders involved
(eg. Searching for all the BlitzMAX source files on the computer), the enumFilesCallback 
and enumFoldersCallback functions are recommended since they are more memory efficient.

All of the functions do their own housekeeping and free any memory used.

EndRem

'File & Folder Enumeration Functions
'====================================

'For each file in the folder <dir> And its subdirectories,
'enumFilesCallback calls the function <callback>, passing the filename as an argument.

Function enumFilesCallback(callback:Int(path:String),dir:String)
	
	Local folder=ReadDir(dir)
	Local file:String
	
	Repeat
		file=NextFile(folder)
		
		If (file) And (file <> ".") And (file <> "..")
			Local fullPath:String=RealPath(dir+"/"+file)
			
			If FileType(fullPath)=FILETYPE_DIR
				enumFilesCallback(callback,fullPath)
			Else
				callback(fullPath)
			End If
		End If
	Until (file=Null)
	
	CloseDir folder
	FlushMem
	
End Function

'For each subfolder in the folder <dir> and its subdirectories,
'enumFoldersCallback calls the function <callback>, passing the folder path as an argument.

Function enumFoldersCallback(callback:Int(path:String),dir:String)
	
	Local folder=ReadDir(dir)
	Local file:String
	
	Repeat
		file=NextFile(folder)
		
		If (file) And (file <> ".") And (file <> "..")
			Local fullPath:String=RealPath(dir+"/"+file)
			
			If FileType(fullPath)=FILETYPE_DIR
				callback(fullPath)
				enumFoldersCallback(callback,fullPath)
			End If
		End If
		
	Until (file=Null)
	
	CloseDir folder
	FlushMem	
End Function	

'Adds the full path of all folders found inside the folder <dir> and its subdirectories to a list.
'The <list> parameter is a Linked List which can be created using the CreateList command.
'The list object must be created first, and then passed to this function
 
Function enumFolders(list:TList,dir:String)
	
	Local folder=ReadDir(dir)
	Local file:String

	Repeat
		file=NextFile(folder)
		
		If (file) And (file <> ".") And (file <> "..")
			Local fullPath:String=RealPath(dir+"/"+file)
			
			If FileType(fullPath)=FILETYPE_DIR
				
				list.addLast(fullPath)
				enumFolders(list,fullPath)
				
			End If
		End If
	Until (file=Null)
	
	CloseDir folder
	FlushMem
	
End Function

'Adds the full path of all files found inside the folder <dir> and its subdirectories to a list.
'The <list> parameter is a Linked List which can be created using the CreateList command.
'The list object must be created first, and then passed to this function

Function enumFiles(list:TList,dir:String)
	
	Local folder=ReadDir(dir)
	Local file:String

	Repeat
		
		file=NextFile(folder)
	
		If (file <> ".") And (file <> "..") And (file)
			Local fullPath:String=RealPath(dir+"/"+file)
		
			If FileType(fullPath)=FILETYPE_DIR
				enumFiles(list,fullPath)
			Else
				list.AddLast(fullPath)
			End If	
		End If
		
	Until (file=Null)
	
	CloseDir folder
	FlushMem
	
End Function
