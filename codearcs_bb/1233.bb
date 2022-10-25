; ID: 1233
; Author: Eikon
; Date: 2004-12-13 11:30:40
; Title: System Memory
; Description: userlib

; Required Userlib
;.lib "kernel32.dll"
;GlobalMemoryStatus%(lpBuffer*)

Graphics 640, 300, 16, 2
SetBuffer BackBuffer()

Type MEMORYSTATUS
    Field dwLength%
    Field dwMemoryLoad%
    Field dwTotalPhys%
    Field dwAvailPhys%
    Field dwTotalPageFile%
    Field dwAvailPageFile%
    Field dwTotalVirtual%
    Field dwAvailVirtual%
End Type

m.MEMORYSTATUS = New MEMORYSTATUS
MemTime = CreateTimer(60)
Repeat
Cls

GlobalMemoryStatus m.MEMORYSTATUS
Text 1, 1, (m\dwAvailPhys% / 1024) + "kbs" + " / " + (m\dwTotalPhys% / 1024) + "kbs"

Delay 5
WaitTimer MemTime: Flip 0
Until KeyDown(1)
