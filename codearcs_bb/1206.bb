; ID: 1206
; Author: skn3[ac]
; Date: 2004-11-25 13:01:24
; Title: Read/Write to an app/process's local memory
; Description: Could be used to modify the health (a trainer) in an external game

Make a blitz file called "ProcessMemory.bb":

Global pmem_bank = CreateBank(4)
Global pmem_proc32 = CreateBank(296) : PokeInt(pmem_proc32,0,296)

Function GetWindowProcessId(name$)
	Local hwnd,bank
	
	hwnd = Pmem_FindWindow(0,name$)
	If hwnd = 0 Return False
	Pmem_GetWindowThreadProcessId(hwnd,pmem_bank)
	Return PeekInt(pmem_bank,0)	
End Function

Function GetProcessId(name$)
	Local snapshot,processid,offset,processname$,char
	
	snapshot = Pmem_CreateToolhelp32Snapshot(2,0)
	If snapshot = 0 Return False
	
	If Pmem_Process32First(snapshot,pmem_proc32)
		While Pmem_Process32Next(snapshot,pmem_proc32)
			processid = PeekInt(pmem_proc32,8)
			processname$ = ""
			offset = 36
			char = PeekByte(pmem_proc32,offset)
			If char <> 0
				While char <> 0
					processname$ = processname$ + Chr$(char)
					offset = offset + 1
					char = PeekByte(pmem_proc32,offset)
				Wend
				If Lower(processname$) = Lower(name$)
					Pmem_CloseHandle(snapshot)
					Return processid
				End If
			End If
		Wend
	End If
	
	Pmem_CloseHandle(snapshot)
	Return False
End Function

Function OpenProcessMemory(processid,access=983040 Or 1048576 Or 4095)
	Return Pmem_OpenProcess(access,False,processid)
End Function

Function WriteProcessByte(process,offset,i)
	PokeByte(pmem_bank,0,i)
	If Pmem_WriteProcessMemory(process,offset,pmem_bank,1,0) = 0 Return False
	Return True
End Function

Function WriteProcessShort(process,offset,i)
	PokeShort(pmem_bank,0,i)
	If Pmem_WriteProcessMemory(process,offset,pmem_bank,3,0) = 0 Return False
	Return True
End Function

Function WriteProcessInt(process,offset,i)
	PokeInt(pmem_bank,0,i)
	If Pmem_WriteProcessMemory(process,offset,pmem_bank,4,0) = 0 Return False
	Return True
End Function

Function ReadProcessByte(process,offset)
	If Pmem_ReadProcessMemory(process,offset,pmem_bank,1,0) = 0 Return False
	Return PeekByte(pmem_bank,0)
End Function

Function ReadProcessShort(process,offset)
	If Pmem_ReadProcessMemory(process,offset,pmem_bank,2,0) = 0 Return False
	Return PeekShort(pmem_bank,0)
End Function

Function ReadProcessInt(process,offset)
	If Pmem_ReadProcessMemory(process,offset,pmem_bank,4,0) = 0 Return False
	Return PeekInt(pmem_bank,0)
End Function

Function CloseProcessMemory(process)
	Pmem_CloseHandle(process)
End Function
