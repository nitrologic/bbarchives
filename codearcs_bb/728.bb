; ID: 728
; Author: -=Darkheart=-
; Date: 2003-06-25 10:48:58
; Title: Thumbnail Creator
; Description: Creates Thumbnail images (any size) of any loadable image file,  (Includes batch convertor for directories).

;#####################################################################################
;################################                          ###########################
;################################     Thumbnail Creator    ###########################
;################################                          ###########################
;#####################################################################################
;
;A program that creates thumbnail images.
;
;Written by Darkheart 23/6/03
;
;Open Source and Freeware
;
Graphics 800,600,32,2
SetBuffer BackBuffer()
AppTitle "Thumbnail Creator Version 1 by Darkheart"

;File and Path Functions courtsey of Rob (see archieves)

Function bbGetDir$(path$)
	For a = Len(path$) To 1 Step -1
		byte$ = Mid(path$,a,1)
		If byte$ = "\"
			Return Left(path$,a)
		EndIf
	Next
	Return ""
End Function


Function bbGetFile$(path$)
	For a = Len(path$) To 1 Step -1
		byte$ = Mid(path$,a,1)
		If byte$ = "\"
			Return Right(path$,Len(path$)-a)
		EndIf
	Next
	Return path$
End Function

Print "Enter filename and full path if not in same directory as program."
Print "Enter ALL to produce thumbnails for all files in that directory."
Print "e.g. c:\pics\mypic.bmp or c:\pics\all"
Print ""
Print "Resized images will be saved as t_<image name>"
Print "e.g. boat.bmp will have thumbnail t_boat.bmp"
FILE$=Input ("Filename and Path: ")
thedir$=bbgetdir$(FILE$)
thefile$=bbgetfile$(FILE$)
c=FileType (thedir$)

	If c<>2 Then
	Print "Path does not exist!"
	Delay 3000
	End
EndIf

b=FileType(FILE$)

allfiles=0
	If thefile$="all" Or thefile$="ALL" Then allfiles=1
	
	If allfiles=0 And b=0 Then
	Print "File not Found!"
	Delay 3000
	End
EndIf

Print ""
height#=Input("Enter desiered thumbnail hieght: ")
width#=Input ("Enter desiered thumbnail width: ")

Select allfiles

;If there is only 1 file then convert and save as t_<filename>.bmp.

	Case 0
	thisimage=LoadImage (file$)
	ResizeImage thisimage,width,height
	
	newfile$="t_"+thefile$
	k=Len(newfile$)
	newfile$=Left$(newfile$,k-4)
	newfile$=newfile$+".bmp"
		
	fileandpath$=thedir$+newfile$
	SaveImage thisimage,fileandpath$
	
;If we are converting all files in the directory loop through the directory and convert all files
;as above.
	
	Case 1
	ThisDir=ReadDir (thedir$)
	ChangeDir thedir$
	Repeat 
	
	thisfile$=NextFile(ThisDir)
		If thisfile="" Then Exit
	
	isfile=FileType (thisfile$)
		If isfile=1 And Left$(thisfile$,2)<>"t_" Then
		Print "Processing File: " + thisfile$
		thisimage=LoadImage (thisfile$)
		ResizeImage thisimage,width,height
		newfile$="t_"+thisfile$
		k=Len(newfile$)
		newfile$=Left$(newfile$,k-4)
		newfile$=newfile$+".bmp"	
		fileandpath$=thedir$+newfile$
		SaveImage thisimage,fileandpath$
		Print "Saved File: " + fileandpath$
	EndIf
	Forever
	
	
End Select
Print ""
Print "All files Done!"
Delay 3000
End
