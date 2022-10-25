; ID: 2855
; Author: Luke111
; Date: 2011-05-25 09:19:29
; Title: Install Cache Root
; Description: Installs the multi threaded php server cache root.

'Install Cache Root
'Luke Bullard
'For luhttpd's PHP extension
'Just change the cache root extension.
'If you don't know what this is for, look in the networking section's Multithreaded PHP Server
'Cache Root Location:
Global cacheroot$ = "D:\cacheroot"
Const numofdirs:Long = 800000
Print "Creating " + String.FromLong(numofdirs) + " Directories"
If Input("Continue? (Y/N) - ") = "Y" Then
If FileType(cacheroot$) = 0 Then
	CreateDir(cacheroot$)
	Print cacheroot$
Else
	RuntimeError "Error: Could not make the Cache Root Base Directory; it already exists!"
EndIf
For x:Long = 0 To numofdirs Step 1
	CreateDir(cacheroot$ + "\" + String.FromLong(x))
	CreateFile(cacheroot$ + "\" + String.FromLong(x) + "\batch.bat")
	Print cacheroot$ + "\" + String.FromLong(x)
Next
Print "Done"
Delay 10000
Else
Print "Done"
Delay 10000
EndIf
