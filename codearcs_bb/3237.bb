; ID: 3237
; Author: Dan
; Date: 2016-01-05 13:10:19
; Title: Load Image, Sound, Music and AnimImage with basic error checking
; Description: Error checking for loading media files

Global Lerror=0			;Set in main program 
Dim LerrorDat$(100,1)		;Set in main program and make sure the dimension is high enough to
;Const loaddebug=1		;used for the PrintF() function 

;lerrorappdir$=SystemProperty("appdir")+"loadingerrors.txt" ;use this variable as CheckLoadError(1,lerrorappdir$) if the printF() function is loaded
; to save the textfile into app's directory 

Function CheckLoad(A$,b$="i",fsize=-1)
; LoadImage and LoadSound Checking.  (replace them whith this command)
; Returns the handle of the loaded image,
; or 0 if none, and increases Lerror for each failed loading.
;
; set the b$ parameter to i for images, s for sound or m for movies/anim gif's
;
; Checks also if file exists, and can be optionally set to check if the filesize is right. (to prevent changes ?!)
;
;copy the 2 lines below to the top of your main Program
;Global Lerror=0			;CheckLoad + CheckAnimLoad function ;<----Copy this to the start of your main program 
;Dim LerrorDat$(100,1)		;CheckLoad + CheckAnimLoad function ;
;
;Make sure the dimension of LerrorDat$(x,1) is high enough to hold the numbers of the loading filenames
;LerrorDat$ - (x,0) contains the reason, (x,1) contains the filename !
;
; if you have the PrintF function installed, you can Copy&Paste and uncomment following 2 lines to the top of your main prog 
;const loaddebug=1 in your mainfile, to enable the writing the filesize to a file 
;If loaddebug=1 Then PrintF("R:\filesize.txt",1)		;Sets the filename for the CheckLoad+CheckAnimLoad function
;	
    b$=Lower$(b$)
    If b$="i" Then Err$="Img "
	If b$="s" Then Err$="Snd "
	If b$="m" Then Err$="Mov "

	If FileType(A$)=0							;Check if file exists !
		Lerror=Lerror+1
		LerrorDat$(Lerror,0)=Err$+"Filename Missing: "
		LerrorDat$(Lerror,1)=A$
		stopnext=1
	ElseIf FileType(A$)=2
	    Lerror=Lerror+1
		LerrorDat$(Lerror,0)=Err$+"Filename is a Directory: "
		LerrorDat$(Lerror,1)=A$
		stopnext=1
	EndIf
	
	If FileSize(A$)=0
		Lerror=Lerror+1
		LerrorDat$(Lerror,0)=Err$+"Filename exists but the filesize is 0: "
		LerrorDat$(Lerror,1)=A$
		stopnext=1
    EndIf
	
	If fsize>-1 And stopnext=0
		If FileSize(A$)<>fsize
	     	Lerror=Lerror+1
			LerrorDat$(Lerror,0)="Reason: "+Err$+"filesize doesnt match:"
			LerrorDat$(Lerror,1)=A$
			stopnext=2
		EndIf
	EndIf
	
	;		;uncomment the following 3 lines if you want to use the PrintF function
;	If loaddebug=1 
;		PrintF ("=CheckLoad("+Chr$(34)+A$+Chr$(34)+","+Chr$(34)+b$+Chr$(34)+","+FileSize(A$)+")")  ; Debugging, to print filesize for the fsize parameter
;	EndIf
	
	If stopnext=0
		If b$="i" 
			Image=LoadImage(A$)
		ElseIf b$="s"
			Image=LoadSound(A$)
		ElseIf b$="m"
		    Image=OpenMovie(A$)
		EndIf
		
		If Image=0 And stopnext=0				;Dont show following if the file was not found !
			Lerror=Lerror+1
			LerrorDat$(Lerror,0)="Reason: "+Err$+"file corruption:"
			LerrorDat$(Lerror,1)=A$
		EndIf
	EndIf
	Return Image
	
End Function

Function CheckAnimload (A$,Width,Height,FirstI,Count,fsize=-1)
;Replace LoadAnimImage with CheckAnimLoad, the parameters are same, part from fsize parameter, which can be used to check if the filesize was changed 
;
; if you have the PrintF function installed, you can set the 
;const loaddebug=1 in your mainfile, to enable the writing the filesize to a file (uncomment the If loaddebug=1 below in this function)
;
	If FileType(A$)=0							;Check if file exists !
		Lerror=Lerror+1
		LerrorDat$(Lerror,0)=Err$+"Ani Filename Missing: "
		LerrorDat$(Lerror,1)=A$
		stopnext=1
	ElseIf FileType(A$)=2
	    Lerror=Lerror+1
		LerrorDat$(Lerror,0)=Err$+"Filename is a Directory: "
		LerrorDat$(Lerror,1)=A$
		stopnext=1
	EndIf
	
	If FileSize(A$)=0
		Lerror=Lerror+1
		LerrorDat$(Lerror,0)=Err$+"Filename exists but the filesize is 0: "
		LerrorDat$(Lerror,1)=A$
		stopnext=1
    EndIf
	
	If fsize>-1 And stopnext=0
		If FileSize(A$)<>fsize
			Lerror=Lerror+1
			LerrorDat$(Lerror,0)="Reason: "+Err$+"filesize doesnt match:"
			LerrorDat$(Lerror,1)=A$
			stopnext=2
		EndIf
	EndIf
	
	;uncomment the following 3 lines if you want to use the PrintF function
;	If loaddebug=1 
;		PrintF ("=CheckAnimLoad("+Chr$(34)+A$+Chr$(34)+","+Width+","+Height+","+FirstI+","+Count+","+FileSize(A$)+")")  ; Debugging, to Print filesizes for the fsize parameter
;	EndIf
	
	If stopnext=0
		Image=LoadAnimImage(A$,Width,Height,FirstI,Count)
		
		If Image=0 And stopnext=0				;Dont show following if the file was not found !
			Lerror=Lerror+1
			LerrorDat$(Lerror,0)="Reason: "+Err$+"file corruption:"
			LerrorDat$(Lerror,1)=A$
		EndIf
	EndIf
	Return Image
End Function 

Function CheckLoadError(save=0,file$="r:\loadingerror.txt")
    ;lerrorappdir$=SystemProperty("appdir")+"loadingerrors.txt"		;Copy and paste this to the beginning of your 
	;															;program and call this function as CheckLoadError(1,lerrorappdir$) to save the error log into app's directory
	;usage: 
	;after the last CheckLoad and CheckAnimLoad function call this function will display 
	;if following errors have occured:
	;
	;file cannot be loaded by the LoadAnimImage or LoadImage or LoadSound
	;Filename does not exists
	;filename is a directory
	;filsize check is optional and can be expanded to whatever you like it to be if you write the functions for the checking (hash, md5 etc etc)
	;
	;
	;!!!!! if you have the PrintF() function installed, then 
	; set save to 1 if you want to save the file as the file$ filename
	;
	;!!!!!!!!!!!!!!!!!!!!!!!!
	;after checking errors with this function, the lerror variable is reseted to 0 !
	;so it can be reused after next calls
	;the LerrorDat$() is not cleared ! it still contains the names 
	
	Color $ff,$ff,$ff
	If Lerror>0 
		Cls
		Locate 0,0
		Print "OOps,there were errors while loading !"
		Print "Please check following files:"
		Print "" 
		y=3
		
		;Uncomment the every if save=1 to enable the PrintF() function saving the text to a file
        ;there are 3 in this function !
		
;		If save=1					;#1
;		    Print "Saving Debuglog to :"
;			Print file$
;			Print ""
;			PrintF()			;Close previous opened files
;			PrintF(file$,1)
;		y=6
;		EndIf 
		
		For X=1 To Lerror				;Lists every error message to the screen
			
;			If save=1				;#2
;				PrintF (LerrorDat$(x,0)+LerrorDat$(x,1))
;			EndIf
			
			Print LerrorDat$(x,0)+LerrorDat$(x,1)			;Displaying the error message
			y=y+1
			If y=14
				Print ("Press any key to continue !")
				Flip 
				WaitKey()
				y=0 
				Cls
				Locate 0,0
				FlushKeys()
			EndIf
		Next
		Flip
		Delay 10
		Lerror=0
		
;		If save=1 Then PrintF("")		;#3
		
		Return True 
	EndIf
	Return False
End Function
