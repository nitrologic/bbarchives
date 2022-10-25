; ID: 1060
; Author: skn3[ac]
; Date: 2004-05-30 12:30:44
; Title: Custom Gadget Properties
; Description: Assign unlimited integers/images/banks/etc to any gadget using win api

Below is the code from the example included in the file.
You will still need to download from http://www.acsv.net/acsite/viewsoftware.php?id=103
to get the library and userlib files.





;example
;this example demonstrates attatching blitz objects to a window.

;Include the library functions
Include "skn3gadgetobjects.bb"


;This function will create a blitz window, and a label which is pattatched to the window.
;It will store the handle to the label, as an integer in the window.
;This way, any functions that refer to this window, can fetch the panel.

Function CreateMyCustomWindow(name$,width,height,r,g,b,parent=0)
	;this function creates a custom blitz window, with a background color
	;it uses the CreateGadgetInt() to attatch a color panel, and store its
	;handle in the window, for later use

	;create gadgets
	Local window = CreateWindow(name$,(ClientWidth(Desktop())/2)-(width/2),(ClientHeight(Desktop())/2)-(height/2),width,height,parent,1+2)
	Local panel  = CreatePanel(0,0,ClientWidth(window),ClientHeight(window),window)
	;setup gadgets
	SetGadgetLayout(panel,1,1,1,1)
	SetPanelColor(panel,r,g,b)
	;link panel to window as a sub object
	CreateGadgetInt(window,"MyCustomWindow_panel",panel)
	;return blitz handle to window
	Return window
End Function



;This function will obtain the nested panel object from the window, and modify its r,g,b color

Function SetMyCustomWindowColor(window,r,g,b)
	SetPanelColor(GetGadgetInt(window,"MyCustomWindow_panel"),r,g,b)
End Function



;This function will obtain the nested panel, and free it, then free the window.

Function FreeMyCustomWindow(window)
	FreeGadget(FreeGadgetInt(window,"MyCustomWindow_panel"))
	FreeGadget(window)
End Function




;----------Main section of code----------------
Global window = CreateMyCustomWindow("Resize to change color",400,250, 255,0,0, 0)

;----------Main Loop---------------------------
Repeat
	Select WaitEvent()
		Case $802
			SetMyCustomWindowColor(window,Rand(0,255),Rand(0,255),Rand(0,255))
		Case $803
			Exit
	End Select
Forever

FreeMyCustomWindow(window)
