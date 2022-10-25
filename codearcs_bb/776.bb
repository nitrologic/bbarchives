; ID: 776
; Author: Rob Farley
; Date: 2003-08-21 12:29:22
; Title: Ini File Updater
; Description: Update, Read and Write to an ini file with one function

;ini file updater
; 2003 Mental Illusion
; http://www.mentalillusion.co.uk
; rob@mentalillusion.co.uk

; Usage:
; value of command = ini_file(Filename,Command)
;
; or
;
; ini_file(Filename,Command,New Value,[Date Stamp])


Function ini_file$(file$,cmd$,setting$="Null",date_stamp=False)

cmd=Lower(cmd)
found=False



filein = ReadFile(file$)

If setting<>"Null"
	fileout= WriteFile("temp.ini")
	If Left(setting,1)="$" Then setting=Chr(34)+Right(setting,Len(setting)-1)+Chr(34)
	If date_stamp Then setting=setting+" ; Updated "+CurrentDate()+" "+CurrentTime()
	EndIf

return_value$="NULL"

While Not Eof(filein)

temp$=ReadLine(filein)

If temp<>"" And Left(temp,1)<>";"
	command$=Lower(Left$(temp,Instr(temp,"=")-1)) ;extract command
	value$=Mid(temp,Instr(temp,"=")+1,(Instr(temp,";")-1)-(Instr(temp,"=")+1)) ;extract value
	If Left(value,1)=Chr(34)
		value=Mid(value,2,Instr(value,Chr(34),2)-2) ;extract a string if it is one
		Else
		If Instr(value," ")>0 Then value=Left(value,Instr(value," ")-1) ;trim spaces off value
		If Instr(value,Chr(9))>0 Then value=Left(value,Instr(value,Chr(9))-1) ;trim tabs off value
		EndIf

	If command=cmd
		found=True
		If setting="Null"
			return_value=value
			Else
			WriteLine(fileout,command+"="+setting)
			EndIf
		Else
		If setting<>"Null" Then WriteLine(fileout,temp)		
		EndIf
	Else
	If setting<>"Null" Then WriteLine(fileout,temp)
	EndIf

Wend

CloseFile (filein)

If setting="Null"
	Return return_value
	Else
	If found=False Then WriteLine(fileout,cmd+"="+setting)
	CloseFile (fileout)
	DeleteFile file
	CopyFile "temp.ini",file
	DeleteFile "temp.ini"
	EndIf

End Function
