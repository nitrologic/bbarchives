; ID: 1937
; Author: BlitzSupport
; Date: 2007-03-02 10:10:50
; Title: Process Tree - BlitzMax version
; Description: Displays tree view of running processes and their children

Import maxgui.drivers

' -----------------------------------------------------------------------------
' Process list... see which processes spawned which programs!
' -----------------------------------------------------------------------------

k32 = LoadLibraryA ("kernel32.dll")

If Not k32 Then Notify "No kernel! Yikes!"; End

Global CreateToolhelp32Snapshot (flags, th32processid) "Win32" = GetProcAddress (k32, "CreateToolhelp32Snapshot")
Global Process32First (snapshot, entry:Byte Ptr) "Win32" = GetProcAddress (k32, "Process32First")
Global Process32Next (snapshot, entry:Byte Ptr) "Win32" = GetProcAddress (k32, "Process32Next")
Global Module32First (snapshot, entry:Byte Ptr) "Win32" = GetProcAddress (k32, "Module32First")
Global Module32Next (snapshot, entry:Byte Ptr) "Win32" = GetProcAddress (k32, "Module32Next")
Global Thread32First (snapshot, entry:Byte Ptr) "Win32" = GetProcAddress (k32, "Thread32First")
Global Thread32Next (snapshot, entry:Byte Ptr) "Win32" = GetProcAddress (k32, "Thread32Next")
Global Heap32First (snapshot, entry:Byte Ptr, th32heapid) "Win32" = GetProcAddress (k32, "Heap32First")
Global Heap32Next (entry:Byte Ptr) "Win32" = GetProcAddress (k32, "Heap32Next")
Global Heap32ListFirst (snapshot, entry:Byte Ptr) "Win32" = GetProcAddress (k32, "Heap32ListFirst")
Global Heap32ListNext (snapshot, entry:Byte Ptr) "Win32" = GetProcAddress (k32, "Heap32ListNext")
Global Toolhelp32ReadProcessMemory (th32processid, baseaddress, buffer:Byte Ptr, Read_bytes, _bytesread) "Win32" = GetProcAddress (k32, "Toolhelp32ReadProcessMemory")
Global CloseHandle (_Object) "Win32" = GetProcAddress (k32, "CloseHandle")

' -----------------------------------------------------------------------------
' PROCESSENTRY32 structure hack...
' -----------------------------------------------------------------------------
' Hopefully won't have to do this in BlitzMax... hint hint, Mark... :)
' -----------------------------------------------------------------------------

Const SizeOf_PE32 = 296

Type PE32

	Field bank:TBank
	
'    dwSize.l
'    cntUsage.l
'    th32ProcessID.l
'    th32DefaultHeapID.l
'    th32ModuleID.l
'    cntThreads.l
'    th32ParentProcessID.l
'    pcPriClassBase.l
'    dwFlags.l
'    szExeFile.b [#MAX_PATH]

End Type

Global PE32List:TList = CreateList ()

' -----------------------------------------------------------------------------

' -----------------------------------------------------------------------------
' Create a new 'process' list entry...
' -----------------------------------------------------------------------------

Function CreatePE32:PE32 ()
	p:PE32 = New PE32
	ListAddLast PE32List, p
	p.bank = CreateBank (SizeOf_PE32)
	If p.bank
		PokeInt p.bank, 0, SizeOf_PE32
	Else
		ListRemove PE32List, p
		Return Null
	EndIf
	Return p
End Function

' -----------------------------------------------------------------------------
' Free process list entry...
' -----------------------------------------------------------------------------

Function FreePE32 (p:PE32)
	If p.bank
		ListRemove PE32List, p
	EndIf
End Function

' -----------------------------------------------------------------------------
' Redundant info...
' -----------------------------------------------------------------------------

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

' -----------------------------------------------------------------------------
' Eeuurrggghhhh... leech process name from bank...
' -----------------------------------------------------------------------------

Function ProcessName$ (bank:TBank)
	For s = 36 To BankSize (bank) - 1
		_byte = PeekByte (bank, s)
		If _byte
			result$ = result$ + Chr (_byte)
		Else
			Exit
		EndIf
	Next
	Return result$
End Function

Global PROC_COUNT

' -----------------------------------------------------------------------------
' Constants required by process functions, etc...
' -----------------------------------------------------------------------------

Const TH32CS_SNAPHEAPLIST = $1
Const TH32CS_SNAPPROCESS = $2
Const TH32CS_SNAPTHREAD = $4
Const TH32CS_SNAPMODULE = $8
Const TH32CS_SNAPALL = (TH32CS_SNAPHEAPLIST | TH32CS_SNAPPROCESS | TH32CS_SNAPTHREAD | TH32CS_SNAPMODULE)
Const TH32CS_INHERIT = $80000000
Const INVALID_HANDLE_VALUE = -1
Const MAX_PATH = 260

' -----------------------------------------------------------------------------
' Take snapshot of running processes...
' -----------------------------------------------------------------------------

Function CreateProcessList ()
	PROC_COUNT = 0
	Return CreateToolhelp32Snapshot (TH32CS_SNAPPROCESS, 0)
End Function

' -----------------------------------------------------------------------------
' Free list of processes (created via CreateProcessList and GetProcesses)...
' -----------------------------------------------------------------------------

Function FreeProcessList (snap)
	For p:PE32 = EachIn PE32List
		FreePE32 (p)
	Next
	CloseHandle (snap)
	PROC_COUNT = 0
End Function

Function GetProcesses (snap)

	PROC_COUNT = 0
	
	' Check snapshot is valid...
	
	If snap <> INVALID_HANDLE_VALUE

		' Hack up a PE32 (PROCESSENTRY32) structure...
		
		p:PE32 = CreatePE32 ()

		' Find the first process, stick info into PE32 bank...
		
		If Process32First (snap, BankBuf (p.bank))
	
			' Increase global process counter...
			
			PROC_COUNT = PROC_COUNT + 1
			
			Repeat
		
				' Create a new PE32 structure for every following process...
				
				p:PE32 = CreatePE32 ()
			
				' Find the next process, stick into PE32 bank...
				
				nextproc = Process32Next (snap, BankBuf (p.bank))
		
				' Got one? Increase process count. If not, free the last PE32 structure...
				
				If nextproc	
					PROC_COUNT = PROC_COUNT + 1
				Else
					FreePE32 (p)
				EndIf
				
			' OK, no more processes...
			
			Until nextproc = 0
			
		Else
		
			' No first process found, so delete the PE32 structure it used...
			
			FreePE32 (p)
			Return False
			
		EndIf
				
		Return True
	
	Else
	
		Return False
		
	EndIf
	
End Function

' -----------------------------------------------------------------------------
' Fill treeview gadget...
' -----------------------------------------------------------------------------

Function FillProcessTree (root:TGadget)

	snap = CreateProcessList ()

	If GetProcesses (snap)
	
		For p:PE32 = EachIn PE32List
			pid = PeekInt (p.bank, 8)
			parent = PeekInt (p.bank, 24)
			proc$ = ProcessName$ (p.bank)
			node = AddTreeViewNode (proc$, root)
			CompareProcs (p, node)
		Next
	
		FreeProcessList (snap)

	Else
		Notify "Failed to create process list!", True
	EndIf			

End Function

' -----------------------------------------------------------------------------
' Find child processes (ah, the joys of trial and error)...
' -----------------------------------------------------------------------------

Function CompareProcs (p:PE32, pnode:TGadget)

	For q:PE32 = EachIn PE32List
		
		If p <> q
		
			pid		= PeekInt (p.bank, 8)
			qid		= PeekInt (q.bank, 8)
			qparent = PeekInt (q.bank, 24)
		
			If pid = qparent
			
				proc$ = ProcessName (q.bank)
				node = AddTreeViewNode (proc$, pnode)
				CompareProcs (q, node)
				ListRemove PE32List, q
				
			EndIf
		
		EndIf
		
	Next
	
End Function

' -----------------------------------------------------------------------------
' D E M O . . .
' -----------------------------------------------------------------------------

' Slight oddity: if it crashes, try sticking a second's Delay () in here. Seems
' to sometimes do this when run from the IDE (maybe snapshotting while a process
' is being spawned is buggy in Windoze? That's my story and I'm sticking to it)...

AppTitle = "Process Tree..."

window:TGadget = CreateWindow ("Process Tree...", 300, 200, 500, 350)

tree:TGadget = CreateTreeView (0, 0, ClientWidth (window), ClientHeight (window) - 30, window)
root:TGadget = TreeViewRoot (tree)
SetGadgetLayout tree, 1, 1, 1, 1

button:TGadget = CreateButton ("Refresh list", 0, ClientHeight (window) - 25, 150, 21, window)
SetGadgetLayout button, 1, 0, 0, 1

menu:TGadget = CreateMenu ("&File", 0, WindowMenu (window))
CreateMenu "&Refresh", 1, menu
CreateMenu "", 2, menu
CreateMenu "&About", 3, menu
CreateMenu "E&xit", 4, menu
UpdateWindowMenu window

FillProcessTree (root)

Repeat

	Select WaitEvent ()
	
		Case EVENT_WINDOWCLOSE
			End
		
		Case EVENT_MENUACTION
		
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
			
		Case EVENT_GADGETACTION
		
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
