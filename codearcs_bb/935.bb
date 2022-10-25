; ID: 935
; Author: Mr Brine
; Date: 2004-02-16 06:46:25
; Title: Gadget Tabber
; Description: Tabs through OS Gadgets

; (c)oded by Mr Brine
;
; Press tab/shift tab to iterate through the os objects
;
;
; User Libs
; =========
; 
; .lib "user32.dll"
;
; GetFocus():"GetFocus"
;


test()


Function Test()

	; create some test data

	win = CreateWindow("tab test", 100, 100, 300, 300, 0)
	
	tf = CreateTextField(0, 0, 100, 22, win)
	ta = CreateTextArea(0, 30, 100, 70, win)
	lb = CreateListBox(0, 110, 100, 100, win)
	AddGadgetItem lb, "1", True
	AddGadgetItem lb, "2"
	AddGadgetItem lb, "3"
	AddGadgetItem lb, "4"
	bt = CreateButton("button", 110,  0, 100, 22, win)
	cb = CreateButton("check", 110, 30, 100, 22, win, 2)
	rd = CreateButton("radio", 110, 60, 100, 22, win, 3)
	tv = CreateTreeView(110, 90, 100, 100, win)
	
	t = AddTreeViewNode("1", TreeViewRoot(tv))
	AddTreeViewNode("1-1", t)
	AddTreeViewNode("1-2", t)	
	AddTreeViewNode("1-3", t)	
			
	; add gadgets to tab list
			
	tablist = Tab_Add(0, tf)
	tablist = Tab_Add(tablist, ta)
	tablist = Tab_Add(tablist, lb)			
	tablist = Tab_Add(tablist, bt)
	tablist = Tab_Add(tablist, cb)
	tablist = Tab_Add(tablist, rd)
	tablist = Tab_Add(tablist, tv)
				
	; set up hot key events to scan for 'tab' & 'shift-tab'			
	; the event id's for the hotkeys can be any value	

	HotKeyEvent 15, 0, $8888
	HotKeyEvent 15, 1, $8889
	
	Repeat
	
		WaitEvent()
		
		Select EventID()
		
			Case $8888

				HotKeyEvent 15, 0, $8888	; resinitialise hot key events
				HotKeyEvent 15, 1, $8889
				
				Tab_Process(tablist, 1)		; goto next os object in tab list
				
			Case $8889

				HotKeyEvent 15, 0, $8888	; resinitialise hot key events
				HotKeyEvent 15, 1, $8889
	
				Tab_Process(tablist, -1)	; goto prev os object in tab list
					
			Case $803
			
				Exit
				
		End Select 
	
	Forever

	FreeBank tablist
	FreeGadget win

End Function 




; ----------------------------------------------------------------------------------------------------


; tablist	= tab list (pass 0 if no tablist yet defined)
; oh 		= os object to assign to tablist
;
; returns a pointer to tablist 
;
Function Tab_Add(tablist, oh)
	
	If(oh <> 0)
	
		If(tablist = 0)
		
			tablist = CreateBank(4) 
		
		Else 	
		
			ResizeBank tablist, BankSize(tablist) + 4
			
		
		End If 
		
		PokeInt tablist, BankSize(tablist) - 4, oh
	
	End If 
	
	Return tablist

End Function 




; tablist	- tablist to work from
; dir		- direction tabber to go
;
Function Tab_Process(tablist, dir)

	If(tablist = 0) Return 
	If(BankSize(tablist) = 0) Return 

	Local	lo = 0
	
	While lo < BankSize(tablist)

		If(QueryObject(PeekInt(tablist, lo), 1) = GetFocus()) Exit
		lo = lo + 4
		
	Wend 
	
	If(lo => BankSize(tablist))
	
		lo = 0							; current focus not in tab list
	
	Else
	
		lo = lo + dir * 4				; current focus is in tab list
		
	End If 
	
	While lo < 0
	
		lo = lo + BankSize(tablist)
		
	Wend  
	
	lo = lo Mod BankSize(tablist)

	ActivateGadget PeekInt(tablist, lo)

End Function
