; ID: 933
; Author: Mike Yurgalavage
; Date: 2004-02-11 17:06:27
; Title: Create List similar to Task Manager with Blitz3D
; Description: Task Manager like Program

; -----------------------------------------------------------------------------
; Create or open existing kernel32.decls file in userlibs folder and place
; these lines in it (uncommented!)...
; -----------------------------------------------------------------------------
;
; .lib "kernel32.dll"
;
; CreateToolhelp32Snapshot% (flags, th32processid)
; Process32First% (snapshot, entry*)
; Process32Next% (snapshot, entry*)
; CloseHandle% (object)
;
; -----------------------------------------------------------------------------

Const MAX_PATH = 264
Const TH32CS_SNAPHEAPLIST = $1
Const TH32CS_SNAPPROCESS = $2
Const TH32CS_SNAPTHREAD = $4
Const TH32CS_SNAPMODULE = $8
Const TH32CS_SNAPALL = TH32CS_SNAPHEAPLIST Or TH32CS_SNAPPROCESS Or TH32CS_SNAPTHREAD Or TH32CS_SNAPMODULE
Const TH32CS_INHERIT = $80000000
Const INVALID_HANDLE_VALUE = -1
Const SizeOf_PE32 = 296

Type PROCESSENTRY32
    Field dwSize
    Field cntUsage
    Field th32ProcessID
    Field th32DefaultHeapID
    Field th32ModuleID
    Field cntThreads
    Field th32ParentProcessID
    Field pcPriClassBase
    Field dwFlags
    Field szExeFile$ [MAX_PATH]
End Type


snap = CreateToolhelp32Snapshot (TH32CS_SNAPPROCESS, 0)

If snap
	;Print snap
	Proc32=CreateBank(SizeOf_PE32)
        PokeInt(Proc32, 0, BankSize(Proc32)) ; dwSize 


	If Process32First (snap, Proc32)
	;cntUsage = PeekInt(Proc32, 4) ; Offset at 4th byte -- read 4 bytes (sizeof Int) 
	;th32ProcessID = PeekInt(Proc32, 8) 
	;th32DefaultHeapID = PeekInt(Proc32, 12) 
	
	Print "Process ID: " + th32ProcessID
		Print

   		While Process32Next (snap, Proc32)
			dwSize=PeekInt(Proc32,0)
			cntUsage=PeekInt(Proc32,4)
			th32ProcessID=PeekInt(Proc32,8)
			th32DefaultHeapID=PeekInt(Proc32,12)
			th32ModuleID=PeekInt(Proc32,16)
			cntThreads=PeekInt(Proc32,20)
			th32ParentProcessID=PeekInt(Proc32,24)
			pcPriClassBase=PeekInt(Proc32,28)
			dwFlags=PeekInt(Proc32,32)
			offset = 36 
			Repeat 
			char = PeekByte(Proc32, offset) 
			offset = offset + 1 
			szExeFile$ = szExeFile$ + Chr$(char) 
			Until char = 0  
			szExeFile$=Left$(szExeFile$,Len(szExeFile$)-1)
			Print szExeFile$
			Print "Process ID: " + th32ProcessID
			Print "----> Parent process ID: " + th32ParentProcessID
			szExeFile$=""
			
			Input()
	    Wend
		Input()
            
    EndIf

    CloseHandle (snap)
    
EndIf

Print "Hit ENTER..."
Input ()
End
