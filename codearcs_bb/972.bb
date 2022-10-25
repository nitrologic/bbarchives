; ID: 972
; Author: Graythe
; Date: 2004-03-21 07:02:32
; Title: Filename lister
; Description: File lister

Const One%=1,Two%=2

Global TextBuffer%,XPels%,YPels%

Graphics 640,480,32,2

YPels=FontHeight()-One
XPels=GraphicsWidth()-One

NoFiles=FileList("C:\","CList.txt",True)

End





Function FileList(FolderName$,StreamName$,Recurse%)

;Read elapsed millisecs
T=MilliSecs()

;Create workspace
WorkSpace%=CreateBank(8)

;Create text buffer
TextBuffer=CreateImage(XPels+One,YPels+One,Two)

;Ensure empty output file exists
StreamNo=WriteFile(StreamName)
;Terminate output file
CloseFile StreamNo
;Prepare output file
StreamNo=OpenFile(StreamName)

;Descriptors to screen
Text 0,0,"Scrutinising directory: "
Text 0,40,"Files Found: "

;Switch active buffer to textbuffer
SetBuffer ImageBuffer(TextBuffer,False)
;White text
Color 255,255,255

FlushFileList(FolderName,StreamNo,Recurse,WorkSpace)

;Calculate time taken
TString$=(MilliSecs()-T)/1000.
;Write duration and filecount to file
WriteLine StreamNo,PeekInt(WorkSpace,False)+" files found in "+TString$+" seconds"
;Terminate file
CloseFile StreamNo
;Free textbuffer
FreeImage TextWindow
FreeBank WorkSpace

End Function












Function FlushFileList(FolderName$,StreamNo%,Recurse%,WorkSpace%)

; V1.1 
;--------------------------------------------------------
;Output list of files from named directory to named file.
;--------------------------------------------------------


;Prepare folder for read
ThisFolder=ReadDir(FolderName)

;If a valid file Handle was obtained
If ThisFolder

	;Update screen
	;Initialise text
	CopyRect False,False,XPels,YPels,False,False,ImageBuffer(TextBuffer,One)
	;Apply text
	Text 0,0,FolderName
	;Update screen
	CopyRect False,False,XPels,YPels,False,20,ImageBuffer(TextBuffer,False),FrontBuffer()

	;Iterate files
	Repeat
		
		;Enquire next filename
		FileName$=NextFile(ThisFolder)

		;Ensure a name returned otherwise exit loop
		If Len(FileName)
		
			;Determine - subdirectory name or a file?
			Select FileType(FolderName+FileName)
	
				Case One
					
					PokeInt WorkSpace,False,PeekInt(WorkSpace,False)+One

					;Update screen
					CopyRect False,False,XPels,YPels,False,False,ImageBuffer(TextBuffer,One)
					Text False,False,PeekInt(WorkSpace,False)
					CopyRect False,False,XPels,YPels,100,40,ImageBuffer(TextBuffer,False),FrontBuffer()

					;Its a valid name - Add to file
					WriteLine StreamNo,FolderName+FileName
			
					;Update screen
					CopyRect False,False,XPels,YPels,False,False,ImageBuffer(TextBuffer,One)
					Text False,False,FileName
					CopyRect False,False,XPels,YPels,False,60,ImageBuffer(TextBuffer,False),FrontBuffer()
										
				
				Case Two
					;Its a directory
					Select FileName
						
						Case ".",".."
							;Ignore these (means dir (where we are) and parent dir (in which we are))
							
						Default
	
							;Recurse with subdirectory name
							If Recurse Then FlushFileList(FolderName+FileName+"\",StreamNo,Recurse,WorkSpace)
					End Select
	
			End Select
		
		Else
		
			Exit
			
		End If
		
	
	Forever
	
	;Terminate directory commitment
	CloseDir ThisFolder

End If

End Function
