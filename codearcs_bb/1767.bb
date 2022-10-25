; ID: 1767
; Author: markcw
; Date: 2006-07-28 22:52:50
; Title: Clipboard Image functions
; Description: Uses User32.dll and Kernel32.dll decls

.lib "Kernel32.dll"

; Memory Management Functions
Api_GlobalAlloc%(uFlags,dwBytes):"GlobalAlloc"
Api_GlobalLock%(hMem):"GlobalLock"
Api_GlobalUnlock%(hMem):"GlobalUnlock"
Api_RtlCopyMemory(Destination,Source*,Length):"RtlMoveMemory"
Api_RtlMoveMemory(Destination*,Source,Length):"RtlMoveMemory"
