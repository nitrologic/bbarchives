; ID: 2469
; Author: JoshK
; Date: 2009-05-03 16:48:50
; Title: Search Files
; Description: Search for files and contents

SuperStrict

Framework brl.filesystem
Import brl.standardio

Local file:String
Local results:String[]

results=SearchFolder(AppDir,"search",["bmx","o"],SEARCH_RECURSIVE)
If results
	For file=EachIn results
		Print file
	Next
EndIf


Const SEARCH_RECURSIVE:Int=1
Const SEARCH_CONTENTS:Int=2
Const SEARCH_CASESENSITIVE:Int=4

Function SearchFolder:String[](path:String,token:String,extensions:String[]=Null,options:Int=SEARCH_RECURSIVE,results:String[]=Null)
	Local dir:String[],ext:String,n:Int,i:Int
	
	dir=LoadDir(path)	
	If Not dir Return results
	
	If Not results results=New String[0]
	
	For n=0 To dir.length-1
		Select FileType(path+"/"+dir[n])
			Case 1
				If extensions
					ext=ExtractExt(dir[n])
					If Not (SEARCH_CASESENSITIVE & options)
						ext=ext.tolower()
					EndIf
					For i=0 To extensions.length-1
						If extensions[i]=ext
							If SearchFile(path+"/"+dir[n],token,options)
								results=results[..results.length+1]
								results[results.length-1]=path+"/"+dir[n]
							EndIf
							Exit
						EndIf
					Next
				Else
					If SearchFile(path+"/"+dir[n],token,options)
						results=results[..results.length+1]
						results[results.length-1]=path+"/"+dir[n]
					EndIf
				EndIf
			Case 2
				If (SEARCH_RECURSIVE & options)
					results=SearchFolder(path+"/"+dir[n],token,extensions,options,results)
				EndIf
				If Not (SEARCH_CONTENTS & options)
					If SearchFile(path+"/"+dir[n],token,options)
						results=results[..results.length+1]
						results[results.length-1]=path+"/"+dir[n]
					EndIf
				EndIf
		EndSelect
	Next
	Return results
EndFunction

Function SearchFile:Int(path:String,token:String,options:Int=0)
	Local stream:TStream,s:String
	
	If Not (SEARCH_CASESENSITIVE & options)
		token=token.tolower()
	EndIf
	If (SEARCH_CONTENTS & options)
		stream=ReadFile(path)
		If Not stream Return False
		While Not stream.Eof()
			s=stream.ReadLine()
			If Not (SEARCH_CASESENSITIVE & options)
				s=s.tolower()
			EndIf
			If s.contains(token)
				stream.close()
				Return True
			EndIf
		Wend
		stream.close()
	Else
		If Not (SEARCH_CASESENSITIVE & options)
			path=path.tolower()
		EndIf
		path=StripDir(path)
		If path.contains(token) Return True
	EndIf
	Return False
EndFunction
