; ID: 1387
; Author: Perturbatio
; Date: 2005-05-27 20:09:45
; Title: StrInsert Function
; Description: Insert a string into another via a slice

Rem
bbdoc: insert inString into SourceStr at the specified index
End Rem
Function StrInsert(SourceStr:String Var, inString:String, Index:Int)
	SourceStr = SourceStr[..Index] + inString + SourceStr[Index..]
End Function
