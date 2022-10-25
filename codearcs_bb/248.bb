; ID: 248
; Author: Chevron
; Date: 2002-03-08 00:54:45
; Title: FTP Funcs
; Description: A Collection of FTP Functions

;====================================================
; FTP Function library Ver.1.00 29/10/02 ;
; Coded by Chevron ;
; Feel Free to use and adapt ;
; No restrictions on use, just give me a credit ;
; ;
; ;
; Credit goes to M.silby for the FTP_saveFile Func. ;
;====================================================


;====================================================
;If you need any help using these functions feel free
; to drop me a email at Steve@kreationsoftware.com
;----------------------------------------------------

;Version History
;---------------

;ver 1.00 
;---------
; Initial Release.

;ver 1.01 
;--------
; Fixed Log on bug
; Added runtime error on unable to acheive passive mode.
; Added optional progress indicator To upload transfer operation.

;ver 1.02
;--------
;Added optional progress indicator to download transfer operation
;Tweaked the progress indicator for better looks

;ver 1.03
;--------
;Added Chmod by request

;FOR ADVANCED USERS ONLY!!
;Not understanding the use of this function could render files un-readable!!


;--------------------
;Functions Overview
;--------------------

;FTP_Connect(ftp$,logon name$,Log on password$, logon account$) - connects To the ftp server

;FTP_List(dir$) - Downloads the directory list from ftp server and saves to a text file in local root directory. The dir$ parameter allows you to specifiy a directory to list, set to "" to list the parent.

;FTP_ParentDir() - Sets the Current FTP server directory to the parent directory.

;FTP_ChangeDir(Dir$) - Sets the current FTP server directory to the specified directory.

;FTP_Currentdir() - Returns the current working directory to the FTP_msg$.

;FTP_makedir() - Creates the specified directory. Note: Will return fail msg to FTP_msg$ if directory exsists.

;FTP_DelDir(dir$) - Deletes the passed remote directory.

;FTP_Delfile(file$) - Deletes the passed remote file.

;FTP_Rename(file1$,file2$) - Renames remote file1$ to file2$. Can also be used to rename directorys.

;FTP_Mode(code) - Sets the transfer mode. Pass 'I' for binary and 'A' for Ascil.

;FTP-ServerType() - Returns information about the current FTP server such as operating system e.t.c to FTP_msg$.

;FTP_Waitcode(code) - waits for the passed server response code, returns on error.

;FTP_Put(trans_file$,progress,x,y) - Uploads the passed file$. Simple progress indication (soon to be improved) can obtained by passing '1' to progress parameter.pass x,y coords for the position of the progress window.

;FTP_Get(Trans_file$,progress,x,y) - Downloads the passed file$.Simple progress indication (soon to be improved) can obtained by passing '1' to progress parameter.pass x,y coords for the position of the progress window.

;FTP_Filesize(file$) - Returns the passed remote file size in bytes to FTP_msg$.

;FTP_Help() - Returns the remote help file to FTP_help$.

;FTP_Quit() - Closes the FTP connection and associated streams.

;FTP_OpenData() - A sub-function used by other functions to open a data transfer stream to the ftp server in passive mode.

;FTP_SaveFile(Name$) - A sub-function used by other functions.

;FTP_Getmsg() - A sub_function used to check for incomming messages from the ftp server.

;FTP_Chmod(file$,user,group,other) - Set the passed file/directorys file attributes, use the table below to determine the required value.
;
;---------------------------------------------------
;              user	group	other(everyone else)
;---------------------------------------------------
;readable	1	  1 		1
;---------------------------------------------------
;writeable	2	  2	        2
;---------------------------------------------------
;executable	4	  4         4
;---------------------------------------------------

;add numbers To make a file attributes a cominnation of the above options

;eg To make it readable,writeable And executable pass 7 (1+2+4)

;---This Function is untested at present and I would appreiate some feedback if there is any problems-----



;-----you Must declare these as globals-----

Global FTP$; Adress of the ftp server
Global FTP_login$; your login name
Global FTP_PASS$;your login password
Global FTP_ACCT$;you login account(not required for most ftp accounts)

Global FTP_msg$;returns command status and error messages from ftp server.
Global ftpcom;ftp command stream
Global FTP_data;ftp data stream
Global FTP_code;returns the last reply code from the ftp server.
Global transkbyte# ; returns the number of kbytes transfered in the last transfer.
Global transtime# ; returns the time taken to perform the last transfer.
Global transrate# ; returns the kbp's for the last data transfer.
Global FTP_help$; holds the remote help file, after help command execution.
Dim dp(6);used for 32bit ip address translations.
;-----------------------------------------------------------------

;-------------------------------------------------------------
Function FTP_connect(ftp$,FTP_login$,FTP_pass$,FTP_acct$)
;-------------------------------------------------------------
If ftpcom Then FTP_quit()
ftpcom=OpenTCPStream( ftp$,21 )

If Not ftpcom RuntimeError( "FTP Connection Failed. Check your internet connection and retry" )


Repeat

;upload connection FTP_data

FTP_getmsg()

Select FTP_code
Case 220
WriteLine ftpcom,"USER "+ftp_login$
Case 331
WriteLine ftpcom,"PASS "+ftp_pass$
Case 332
WriteLine ftpcom,"ACCT "+ftp_acct$
Case 530
RuntimeError "Log On Failed "+ftp_login$+" Cannot Log On" 
End Select
Until FTP_code=230


FTP_Mode("I");select binary transfer mode


End Function

;--------------------------------
Function FTP_list(dir$)
;--------------------------------
If Not ftpcom Then FTP_connect(ftp$,FTP_login$,FTP_pass$,FTP_acct$)
dir_list$=""
open_data()
WriteLine ftpcom,"LIST "+dir$

While ReadAvail(FTP_data)=0
Wend

FTP_savefile("Dirlist.txt",0,0,0,0)
CloseTCPStream FTP_data
FTP_data=0
End Function

;------------------------------
Function FTP_ParentDir()
;------------------------------
If Not ftpcom Then FTP_connect(ftp$,FTP_login$,FTP_pass$,FTP_acct$)
WriteLine ftpcom,"CDUP"
FTP_waitcode(250)
End Function

;---------------------------
Function FTP_Changedir(dir$)
;---------------------------
If Not ftpcom Then FTP_connect(ftp$,FTP_login$,FTP_pass$,FTP_acct$) 
WriteLine ftpcom,"CWD "+dir$
FTP_waitcode(250)
End Function

;----------------------------
Function FTP_Makedir(dir$)
;----------------------------
If Not ftpcom Then FTP_connect(ftp$,FTP_login$,FTP_pass$,FTP_acct$)
WriteLine ftpcom,"MKD "+dir$
FTP_waitcode(257)
End Function

;----------------------------
Function FTP_DelDir(dir$)
;----------------------------
If Not ftpcom Then FTP_connect(ftp$,FTP_login$,FTP_pass$,FTP_acct$)
WriteLine ftpcom,"RMD "+Dir$
FTP_waitcode(250)
End Function

;-----------------------------
Function FTP_currentdir()
;-----------------------------
If Not ftpcom Then FTP_connect(ftp$,FTP_login$,FTP_pass$,FTP_acct$)
WriteLine ftpcom,"PWD"
FTP_waitcode(257)
End Function

;-----------------------------
Function FTP_DelFile(file$)
;-----------------------------
If Not ftpcom Then FTP_connect(ftp$,FTP_login$,FTP_pass$,FTP_acct$)
WriteLine ftpcom,"DELE "+file$
FTP_waitcode(250)
End Function

;-----------------------------------
Function FTP_Rename(file1$,file2$)
;-----------------------------------
If Not ftpcom Then FTP_connect(ftp$,FTP_login$,FTP_pass$,FTP_acct$)
WriteLine ftpcom,"RNFR "+file1$
FTP_waitcode(350)
WriteLine ftpcom,"RNTO "+file2$
FTP_waitcode(250)
End Function

;---------------------------------
Function FTP_filesize(file$)
;---------------------------------
If Not ftpcom Then FTP_connect(ftp$,FTP_login$,FTP_pass$,FTP_acct$)
WriteLine ftpcom,"SIZE "+file$
FTP_waitcode(213)
End Function

;----------------------------
Function FTP_Mode(code$)
;----------------------------
If Not ftpcom Then FTP_connect(ftp$,FTP_login$,FTP_pass$,FTP_acct$)
WriteLine ftpcom,"TYPE "+code
FTP_waitcode(200)
End Function

;---------------------------
Function FTP_servertype()
;---------------------------
If Not ftpcom Then FTP_connect(ftp$,FTP_login$,FTP_pass$,FTP_acct$)
WriteLine ftpcom,"SYST"
FTP_waitcode(200)
End Function

;---------------------------
Function FTP_help()
;---------------------------
If Not ftpcom Then FTP_connect(ftp$,FTP_login$,FTP_pass$,FTP_acct$)
WriteLine ftpcom,"HELP"
FTP_waitcode(211)
FTP_help$=FTP_msg$
End Function

;---------------------------------
Function FTP_Put(Trans_file$,progress,x,y)
;---------------------------------
If Not ftpcom Then FTP_connect(ftp$,FTP_login$,FTP_pass$,FTP_acct$)
size#=1:time#=MilliSecs()

If FileSize(trans_file$)=0 
FTP_msg$="Error: Cannot Find Upload File!!"
Return
EndIf

transfilesize#=FileSize(trans_file$)


open_data()

WriteLine ftpcom,"STOR "+trans_file$

file=ReadFile(trans_file$)
rd=file:wt=FTP_data
b=CreateBank(16384)

Repeat

If progress=True
transpercent=100-(((transfilesize#-size#)/transfilesize#)*100)
ftp_progressind(transpercent,trans_file$,1,x,y)
EndIf

n=ReadBytes( b,rd,0,16384)
WriteBytes b,wt,0,n
size#=size#+n
Until n<>16384

transkbyte#=size/1000
transtime#=(MilliSecs()-time#)/1000
transrate#=transkbyte#/transtime#

FreeBank b:CloseFile file:file=0:CloseTCPStream FTP_data:FTP_data=0

If progress=True
transpercent=100
ftp_progressind(transpercent,trans_file$,2,x,y)
EndIf

End Function

;-----------------------------
Function FTP_Get(Trans_file$,progressind,x,y)
;-----------------------------
If Not ftpcom Then FTP_connect(ftp$,FTP_login$,FTP_pass$,FTP_acct$)

If progressind=1
ftp_filesize(Trans_file$)
trans_fileSize#=Mid$(ftp_msg$,5)
EndIf

open_data()

WriteLine ftpcom,"RETR "+trans_file$

While ReadAvail(FTP_data)=0
Wend

FTP_Savefile(Trans_file$,1,x,y,trans_FileSize)

CloseTCPStream FTP_data:FTP_data=0

End Function

;-----------------------------
Function open_data()
;-----------------------------
If Not ftpcom Then FTP_connect(ftp$,FTP_login$,FTP_pass$,FTP_acct$)
trys=0
.retry

WriteLine ftpcom,"PASV"

FTP_waitcode(227)

i1=Instr( FTP_msg$,"(" )
i2=Instr( FTP_msg$,")",i1 )
pt$=Mid$( FTP_msg$,i1+1,i2-i1-1 )+","

For k=1 To 6
i=Instr( pt$,"," )
dp(k)=Left$( pt$,i-1 )
pt$=Mid$(pt$,i+1)
Next

ip$=dp(1)+"."+dp(2)+"."+dp(3)+"."+dp(4)
port=(dp(5)Shl 8)Or dp(6)
FTP_data=OpenTCPStream(ip$,port)


If Not FTP_data

WriteLine ftpcom,"QUIT"
FTP_waitcode(221)
CloseTCPStream ftpcom:ftpcom=0

FTP_connect(ftp$,FTP_login$,FTP_pass$,FTP_acct$)
trys=trys+1
If trys=5 Then RuntimeError("Unable to connect to FTP server "+ftp$+" in passive mode") 
Goto retry
EndIf

End Function

;--------------------------------------------------------
Function FTP_Savefile(filename$,cond,x,y,trans_filesize#)
;--------------------------------------------------------
time#=MilliSecs()
size#=0
file=WriteFile(filename$)
rd=FTP_data:wt=file

b=CreateBank(16384)

Repeat
n=ReadBytes( b,rd,0,16384 )
WriteBytes b,wt,0,n
size#=size#+n


If cond=1
transpercent=100-(((trans_filesize#-size#)/trans_filesize#)*100)
ftp_progressind(transpercent,filename$,3,x,y)
EndIf

Until n<>16384

transkbyte#=size/1000
transtime#=(MilliSecs()-time#)/1000
transrate#=transkbyte#/transtime#

FreeBank b
CloseFile file
file=0

If cond=1
transpercent=100
ftp_progressind(transpercent,filename$,4,x,y)
EndIf

End Function

;----------------------------
Function FTP_Getmsg()
;----------------------------

FTP_code=0
FTP_msg$=""
If ReadAvail(ftpcom)>0
Repeat
Repeat
FTP_msg$=ReadLine$(ftpcom)
Until Len(FTP_msg$)>3
FTP_code=Left$(FTP_msg$,3 )
Until FTP_code>=100 And FTP_code<600 And Mid$(FTP_msg$,4,1 )=" "
EndIf

End Function

;-------------------------------
Function FTP_waitcode(code)
;-------------------------------
FTP_code=0

While FTP_code<>code
FTP_getmsg()


If KeyHit(1)=True Return;allow user to escape if trapped
If FTP_code>400;error return
Return
EndIf

Wend
End Function

;------------------------------------------
Function FTP_Chmod(file$,user,group,other)
;------------------------------------------
If Not ftpcom Then FTP_connect(ftp$,FTP_login$,FTP_pass$,FTP_acct$)

WriteLine ftpcom,"SITE CHMOD "+files$+" "+Str$(user)+Str$(group)+Str$(other)
ftp_waitcode(200)

End Function




;----------------------------
Function FTP_Quit()
;----------------------------
WriteLine ftpcom,"QUIT"
;FTP_waitcode(221)
If ftpcom Then CloseTCPStream ftpcom:ftpcom=0
If FTP_data Then CloseTCPStream FTP_data:FTP_data=0

End Function

;------------------------------------------------------------
Function ftp_progressind(transpercent,trans_file$,status,x,y)
;------------------------------------------------------------
Cls

Color 255,255,255
Rect x-1,y-1,301,141,0

Color 192,192,192
Rect x,y,300,140,1

For Y1=Y To Y+14 
BLUE=255
For X1=X To X+298
Color 0,0,255-BLUE
If BLUE>20 Then BLUE=BLUE-1

Plot X1,Y1
Next
Next

Color 0,0,0
Rect x+120,y+100,60,20,0
Line x+180,y+100,x+180,y+120
Line x+120,y+120,x+180,y+120
Text x+130,y+103,"ABORT"

Rect x+45,y+60,200,20,1
Color 48,136,248

If transpercent<>100
For Y1=Y+61 To Y+79 Step 2
Line X+45,Y1,X+45+(TRANSPERCENT Shl 1),y1
Next

Else
Color 255,255,255
Locate x+75,y+64
Print "Operation Complete"
EndIf

Color 255,255,255
Locate x+5,y
Print "FTP Transfer In Progress..."
Locate x+5,y+35
If status=1
Color 0,0,0
Print "Uploading "+trans_file$+" to FTP Server"
EndIf

If status=2
Color 0,0,0
Print trans_file$+" Uploaded to FTP server"
Locate x+42,y+82
Print Int(transkbyte#)+"Kb Uploaded at "+Int(transrate#)+" Kbps"
Delay 1500
Return
EndIf

If status=3
Color 0,0,0
Print "Downloading "+trans_file$+" from FTP Server"
EndIf

If status=4
Color 0,0,0

Print trans_file$+" Downloaded from FTP server"
Locate x+42,y+82
Print Int(transkbyte#)+"Kb Downloaded at "+Int(transrate#)+" Kbps"
Delay 1500
Return
EndIf

Locate x+255,y+62
Print transpercent+"%"
Flip
End Function
