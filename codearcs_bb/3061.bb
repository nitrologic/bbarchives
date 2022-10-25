; ID: 3061
; Author: _PJ_
; Date: 2013-06-24 10:42:21
; Title: B+ LoadFont Validation
; Description: Checks to see if LoadFont has loaded a valid font

;The functions - Please include both functions in your programs for this to work.
;Before using LoadFont, check the font will load correctly by calling ValidateFont()
;If the font loads and is printable by Blitz, then ValidateFont() will return TRUE

Function ValidateFont%(Name$)
	Local Font=LoadFont(Name,0)
	Local Validate=( Lower(Name)=Lower(FontName(Font)) ) + ( RetrieveFileName(Name)=Lower(FontName(Font)) )
	
	FreeFont Font
	
	Return Validate
End Function

Function RetrieveFileName$(FilePath$)
	Local TestPath$=Lower(FilePath)
	If (FileType(FilePath)=2)
		DebugLog("Path leads to directory, not file")
		Return ""
	End If
	
	If (Not(Instr(FilePath,"\")))
		Return RetrieveFileName(SystemProperty("appdir")+FilePath)
	End If
	
	If (FileType(FilePath)<>1)
		If (Left(TestPath,Len(SystemProperty("appdir"))) = Lower(SystemProperty("appdir")))
			DebugLog("File does not exist")
			Return ""
		Else
			Return RetrieveFileName(SystemProperty("appdir")+FilePath)
		End If
	End If
	
	Local ns_Iterbyte%
	Local ns_Len=Len(TestPath)
	
	For ns_Iterbyte = ns_Len To 1 Step -1
		If (Mid(TestPath,ns_Iterbyte,1)="\")
			; Trim target from path
			TestPath=(Right(TestPath,ns_Len-ns_Iterbyte))
			Exit
		EndIf
	Next
	
	If (Instr(TestPath,"."))
		If (Lower(Right(TestPath,4)=".ttf"))
			TestPath=Left(TestPath,Len(TestPath)-4)
		End If
	End If
	
	Return TestPath
End Function
