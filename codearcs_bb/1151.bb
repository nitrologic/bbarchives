; ID: 1151
; Author: AntMan - Banned in the line of duty.
; Date: 2004-08-30 19:08:57
; Title: Code Cleaner/De-formatter
; Description: It would be more confusing than the title.

;Code Cleaner
;--
;- 
;-

;-works directly in ide by file, or you can exe it and simply drag a file
;on the exe.

Global changes
com$=commandLine()
if com<>"" 
   file=com
else
   file$="rename me to test file"
endif
failSilent=False


fileIn=ReadFile(file)
If Not fileIn
	If failSilent End
	RuntimeError "File "+file+" could not be opened"
EndIf
fileOut=WriteFile("Clean_"+file)

While Not Eof(fileIn)
	WriteLine fileOut,cleanCode( ReadLine(fileIn))	
Wend
CloseFile fileIn
CloseFile fileOut
If failSilent
	End
EndIf
Print "Succesful."
Print "Made "+changes+" alterations."
Print "Press any key to exit."
WaitKey
End

	

Function cleanCode$(code$)
	code=stripSeps(code," ")
	code=stripSeps(code,",")
	code=Trim(code)
	cl=Len(code)
	
	
	
	For j=1 To cl-1
		c$=Mid(code,j,1)
		nc$=Mid(code,j+1,1)
		Select c	
			Case " "
			
				Select nc
					Case ",","(","+","-","/",")",".","*"
					   kc=True
				End Select
					
				Select lc$
					Case ",","(",")"," ","*","+","/","-"
						 kc=True
				End Select
		End Select
		If kc
			code=Mid(code,1,j-1)+Mid(code,j+1)
			changes=changes+1
			kc=False
		EndIf
		lc$=c
	Next
	Return code
End Function

Function stripSeps$(code$,sep$) ;strips multiple (adjancent) seperators
	sl=Len(code)
	For j=1 To sl
		c$=Mid(code,j,1)
		Select c
			Case Chr$(34) ;ignore strings
				ignore=1-ignore
			Case sep
				If Not ignore			
					If run=False rS=j:tc=0
					run=True
					tc=tc+1
				EndIf
			Default
				If run=True
					If tc>1
						code=Mid(code,1,rs)+Mid(code,rs+tc)
						changes=changes+1
					EndIf
					run=False
				EndIf
		End Select
	Next
	Return code
End Function
