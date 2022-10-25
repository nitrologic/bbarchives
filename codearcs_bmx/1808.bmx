; ID: 1808
; Author: tonyg
; Date: 2006-09-09 08:47:48
; Title: GC Debug output
; Description: Outputs GC debug messages

Extern
	Function bbGCSetDebug(mode:Int)
EndExtern

Type test
	Field x:Int=5
EndType
'GCSetMode 4
Local mytest:test=New test
bbGCSetDebug(1)
Print GCCollect()
Print GCMemAlloced()
DebugLog(mytest.x)
