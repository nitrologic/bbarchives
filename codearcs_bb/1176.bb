; ID: 1176
; Author: AntMan - Banned in the line of duty.
; Date: 2004-10-20 12:01:27
; Title: Simple file output debugger
; Description: Debug a file both in and out.

Function ReadInt(file)
      return ReadLine(file)
End Function

Function ReadFloat#(file)
	return ReadLine(file)
End Function

Function ReadString$(file)
	return ReadLine(file)
End Function

Function WriteInt(file,val)
	WriteLine file,val
End Function

Function WriteFloat(File,val#)
	WriteLine file,val
End Function

Function WriteString(file,val$)
	WriteLine file,val
End Function
