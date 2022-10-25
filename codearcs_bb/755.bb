; ID: 755
; Author: BlitzSupport
; Date: 2003-07-31 14:54:10
; Title: Process Tree
; Description: List of running processes...

; -----------------------------------------------------------------------------
; Process list... see which processes spawned which programs!
; -----------------------------------------------------------------------------

; Works for me under Windows 2000, anyway...

; -----------------------------------------------------------------------------
; ADD TO kernel32.decls IN USERLIBS FOLDER!
; -----------------------------------------------------------------------------

; .lib "kernel32.dll"

; CreateToolhelp32Snapshot% (flags, th32processid)
; Process32First% (snapshot, entry*)
; Process32Next% (snapshot, entry*)
; Module32First% (snapshot, entry*)
; Module32Next% (snapshot, entry*)
; Thread32First% (snapshot, entry*)
; Thread32Next% (snapshot, entry*)
; Heap32First% (snapshot, entry*, th32heapid)
; Heap32Next% (entry*)
; Heap32ListFirst% (snapshot, entry*)
; Heap32ListNext% (snapshot, entry*)
; Toolhelp32ReadProcessMemory% (th32processid, baseaddress, buffer*, ReadBytes, bytesread)
; CloseHandle% (Object)

; -----------------------------------------------------------------------------
; PROCESSENTRY32 structure hack...
; -----------------------------------------------------------------------------
; Hopefully won't have to do this in BlitzMax... hint hint, Mark... :)
; -----------------------------------------------------------------------------

Const SizeOf_PE32 = 296

Type PE32

	Field bank
	
;    dwSize.l
;    cntUsage.l
;    th32ProcessID.l
;    th32DefaultHeapID.l
;    th32ModuleID.l
;    cntThreads.l
;    th32ParentProcessID.l
;    pcPriClassBase.l
;    dwFlags.l
;    szExeFile.b [#MAX_PATH]

End Type

; -----------------------------------------------------------------------------

; -----------------------------------------------------------------------------
; Create a new 'process' list entry...
; -----------------------------------------------------------------------------

Function CreatePE32.PE32 ()
	p.PE32 = New PE32
	p\bank = CreateBank (SizeOf_PE32)
	If p\bank
		PokeInt p\bank, 0, SizeOf_PE32
	Else
		Delete p
		Return Null
	EndIf
	Return p
End Function

; -----------------------------------------------------------------------------
; Free process list entry...
; -----------------------------------------------------------------------------

Function FreePE32 (p.PE32)
	If p\bank
		FreeBank p\bank
	EndIf
	Delete p
End Function

; -----------------------------------------------------------------------------
; Redundant info...
; -----------------------------------------------------------------------------

Function PrintProc (bank)
	Print ""
	Print "Name    : " + ProcessName$ (bank)
	Print "Usage   : " + PeekInt (bank, 4)
	Print "Proc ID : " + PeekInt (bank, 8)
	Print "Heap ID : " + PeekInt (bank, 12)
	Print "Mod  ID : " + PeekInt (bank, 16)
	Print "Threads : " + PeekInt (bank, 20)
	Print "Parent  : " + PeekInt (bank, 24)
	Print "ClasBas : " + PeekInt (bank, 28)
	Print "Flags   : " + PeekInt (bank, 32)
End Function

; -----------------------------------------------------------------------------
; Eeuurrggghhhh... leech process name from bank...
; -----------------------------------------------------------------------------

Function ProcessName$ (bank)
	For s = 36 To BankSize (bank) - 1
		byte = PeekByte (bank, s)
		If byte
			result$ = result$ + Chr (byte)
		Else
			Exit
		EndIf
	Next
	Return result$
End Function

Global PROC_COUNT

; -----------------------------------------------------------------------------
; Constants required by process functions, etc...
; -----------------------------------------------------------------------------

Const TH32CS_SNAPHEAPLIST = $1
Const TH32CS_SNAPPROCESS = $2
Const TH32CS_SNAPTHREAD = $4
Const TH32CS_SNAPMODULE = $8
Const TH32CS_SNAPALL = (TH32CS_SNAPHEAPLIST Or TH32CS_SNAPPROCESS Or TH32CS_SNAPTHREAD Or TH32CS_SNAPMODULE)
Const TH32CS_INHERIT = $80000000
Const INVALID_HANDLE_VALUE = -1
Const MAX_PATH = 260

; -----------------------------------------------------------------------------
; Take snapshot of running processes...
; -----------------------------------------------------------------------------

Function CreateProcessList ()
	PROC_COUNT = 0
	Return CreateToolhelp32Snapshot (TH32CS_SNAPPROCESS, 0)
End Function

; -----------------------------------------------------------------------------
; Free list of processes (created via CreateProcessList and GetProcesses)...
; -----------------------------------------------------------------------------

Function FreeProcessList (snap)
	For p.PE32 = Each PE32
		FreePE32 (p)
	Next
	CloseHandle (snap)
	PROC_COUNT = 0
End Function

Function GetProcesses (snap)

	PROC_COUNT = 0
	
	; Check snapshot is valid...
	
	If snap <> INVALID_HANDLE_VALUE
		
		; Hack up a PE32 (PROCESSENTRY32) structure...
		
		p.PE32 = CreatePE32 ()
		
		; Find the first process, stick info into PE32 bank...
		
		If Process32First (snap, p\bank)
	
			; Increase global process counter...
			
			PROC_COUNT = PROC_COUNT + 1
			
			Repeat
		
				; Create a new PE32 structure for every following process...
				
				p.PE32 = CreatePE32 ()
				
				; Find the next process, stick into PE32 bank...
				
				nextproc = Process32Next (snap, p\bank)
		
				; Got one? Increase process count. If not, free the last PE32 structure...
				
				If nextproc			
					PROC_COUNT = PROC_COUNT + 1
				Else
					FreePE32 (p)
				EndIf
				
			; OK, no more processes...
			
			Until nextproc = 0
			
		Else
		
			; No first process found, so delete the PE32 structure it used...
			
			FreePE32 (p)
			Return False
			
		EndIf
				
		Return True
	
	Else
	
		Return False
		
	EndIf
	
End Function

; -----------------------------------------------------------------------------
; Used to create a list of treeview nodes...
; -----------------------------------------------------------------------------

Type Node
	Field node
End Type

; -----------------------------------------------------------------------------
; Fill treeview gadget...
; -----------------------------------------------------------------------------

Function FillProcessTree (root)

	snap = CreateProcessList ()

	If GetProcesses (snap)
	
		For p.PE32 = Each PE32
			pid = PeekInt (p\bank, 8)
			parent = PeekInt (p\bank, 24)
			proc$ = ProcessName$ (p\bank)
			n.Node = New Node
			n\node = AddTreeViewNode (proc$, root)
			CompareProcs (p, n\node)
		Next
	
		FreeProcessList (snap)

	Else
		Notify "Failed to create process list!", True
	EndIf			

End Function

; -----------------------------------------------------------------------------
; Find child processes (ah, the joys of trial and error)...
; -----------------------------------------------------------------------------

Function CompareProcs (p.PE32, node)

	For q.PE32 = Each PE32
		
		If p <> q
		
			pid		= PeekInt (p\bank, 8)
			qid		= PeekInt (q\bank, 8)
			qparent = PeekInt (q\bank, 24)
		
			If pid = qparent
			
				proc$ = ProcessName (q\bank)
				n.Node = New Node
				n\node = AddTreeViewNode (proc$, node)
				CompareProcs (q, n\node)
				Delete q
				
			EndIf
		
		EndIf
		
	Next
	
End Function

; -----------------------------------------------------------------------------
; D E M O . . .
; -----------------------------------------------------------------------------

; Slight oddity: if it crashes, try sticking a second's Delay () in here. Seems
; to sometimes do this when run from the IDE (maybe snapshotting while a process
; is being spawned is buggy in Windoze? That's my story and I'm sticking to it)...

AppTitle "Process Tree..."

window = CreateWindow ("Process Tree...", 300, 200, 500, 350)

tree = CreateTreeView (0, 0, ClientWidth (window), ClientHeight (window) - 30, window)
root = TreeViewRoot (tree)
SetGadgetLayout tree, 1, 1, 1, 1

button = CreateButton ("Refresh list", 0, ClientHeight (window) - 25, 150, 21, window)
SetGadgetLayout button, 1, 0, 0, 1

menu = CreateMenu ("&File", 0, WindowMenu (window))
CreateMenu "&Refresh", 1, menu
CreateMenu "", 2, menu
CreateMenu "&About", 3, menu
CreateMenu "E&xit", 4, menu
UpdateWindowMenu window

FillProcessTree (root)

Repeat

	Select WaitEvent ()
	
		Case $803
			End
		
		Case $1001
			Select EventData ()
				Case 1
					FreeGadget tree
					tree = CreateTreeView (0, 0, ClientWidth (window), ClientHeight (window) - 30, window)
					root = TreeViewRoot (tree)
					SetGadgetLayout tree, 1, 1, 1, 1
					FillProcessTree (root)

				Case 3
					Notify "Process Tree..." + Chr (10) + Chr (10) + "An amazing Hi-Toro production, public domain 2003."
					
				Case 4
					End

			End Select
		Case $401
		
			Select EventSource ()

				Case button
				
					FreeGadget tree
					tree = CreateTreeView (0, 0, ClientWidth (window), ClientHeight (window) - 30, window)
					root = TreeViewRoot (tree)
					SetGadgetLayout tree, 1, 1, 1, 1
					FillProcessTree (root)

			End Select
			
	End Select

Forever
