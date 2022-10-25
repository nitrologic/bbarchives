; ID: 1417
; Author: altitudems
; Date: 2005-07-09 17:50:21
; Title: Yet Another String to Array Routine - BMX
; Description: Takes a string and splits it into either an

Function StringToIntArray:Int [] (_String:String, _Delimiter:String)
	Local TempArray:Int [1]
	Local TempString:String
	While _String.Find(_Delimiter) <> -1
		TempString = _String[.._String.Find(_Delimiter)]
		_String = _String[TempString.Length+1..]
		TempArray[TempArray.Length - 1] = Int(TempString)
		TempArray = TempArray[..TempArray.Length+1]
		Flushmem
	Wend
	TempString = _String
	TempArray[TempArray.Length - 1] = Int(TempString)
	Return TempArray
End Function

Function StringToStringArray:String [] (_String:String, _Delimiter:String)
	Local TempArray:String [1]
	Local TempString:String
	While _String.Find(_Delimiter) <> -1
		TempString = _String[.._String.Find(_Delimiter)]
		_String = _String[TempString.Length + 1..]
		TempArray[TempArray.Length - 1] = TempString.Trim()
		TempArray = TempArray[..TempArray.Length + 1]
		Flushmem
	Wend
	TempString = _String
	TempArray[TempArray.Length - 1] = TempString.Trim()
	Return TempArray
End Function
