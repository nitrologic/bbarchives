; ID: 2927
; Author: JoshK
; Date: 2012-03-04 13:45:34
; Title: Delete to Recycle Bin
; Description: Send files to the recycle bin instead of permanently deleting them

SuperStrict

?win32
Import pub.win32

Type SHFILEOPSTRUCT
	Field hwnd:Int
	Field wFunc:Int
	Field pFrom:Int
	Field pTo:Byte Ptr
	Field fFlags:Int
	Field fAnyOperationsAborted:Int
	Field hNameMappings:Int
	Field lpszProgressTitle:Int
EndType

Extern "win32"
	Function SHFileOperation:Int(lpFileOp:Byte Ptr)
EndExtern

Const DE_SAMEFILE     :Int = $71
Const DE_MANYSRC1DEST :Int = $72
Const DE_OPCANCELLED  :Int = $75
Const DE_DESTSUBTREE  :Int = $76
Const DE_INVALIDFILES :Int = $7C
Const DE_DESTSAMETREE :Int = $7D
Const DE_DIFFDIR       :Int = $73
Const DE_FILEDESTISFLD :Int = $80

Const FO_DELETE:Int = $3
Const FOF_ALLOWUNDO:Int = $40
Const FOF_NOCONFIRMATION:Int = $10
Const FOF_SILENT:Int = $4
?

Function DeleteDir:Int(filename:String,recursive:Int=False,permanent:Int=False)
	Local dir:String[]
	Local file:String
	
	If permanent Return BRL.FileSystem.DeleteDir(filename,recursive)
	If FileType(filename)<>2 Return False
	If Not recursive
		dir=LoadDir(filename)
		If dir.length Return False
	EndIf
	Return DeleteFile(filename)
EndFunction

Function DeleteFile:Int(filename:String,permanent:Int=False)
?macos
	Return BRL.FileSystem.DeleteFile(filename)
?
?linux
	Return BRL.FileSystem.DeleteFile(filename)
?
?win32
	'CallWindowProc function
	'http://msdn.microsoft.com/en-us/library/windows/Desktop/ms633571%28v=vs.85%29.aspx
	
	'SHFileOperation function 
	'http://msdn.microsoft.com/en-us/library/windows/Desktop/bb762164%28v=vs.85%29.aspx
	
	'SHFILEOPSTRUCT structure
	'http://msdn.microsoft.com/en-us/library/windows/desktop/bb759795%28v=vs.85%29.aspx
	
	Local lpFileOp:SHFILEOPSTRUCT=New SHFILEOPSTRUCT
	Local sfrom:Short[]
	Local sto:Short[]
	Local n:Int
	Local str_addr:Int
	Local asm:Byte[5]
	Local result:Int
	Local doublenulltermstring:Byte[]
	
	If permanent Return BRL.FileSystem.DeleteFile(filename)
	
	filename=RealPath(filename).Replace("\","/")
	
	'We have to get some funny unicode string
	asm[0]=$8b
	asm[1]=$44
	asm[2]=$24
	asm[3]=$10
	asm[4]=$c3
	
	doublenulltermstring=doublenulltermstring[..filename.length+2]
	For n=0 To filename.length-1
		doublenulltermstring[n] = filename[n]
	Next
	doublenulltermstring[doublenulltermstring.length-2]=0
	doublenulltermstring[doublenulltermstring.length-1]=0
	str_addr = CallWindowProcA(asm,0,0,0,Int(Varptr doublenulltermstring[0]))
	
	lpFileOp.wFunc=FO_DELETE
	lpFileOp.pFrom=str_addr
	lpFileOp.fFlags=FOF_ALLOWUNDO|FOF_SILENT|FOF_NOCONFIRMATION
	
	result=SHFileOperation(lpFileOp)
	If result=0 Return True Else Return False
?
EndFunction

Local path:String = "test.txt"

If DeleteFile(path)
	Print "sent "+path+" to Bin"
Else
	Print "problem sending file "+path+" to bin"
EndIf
