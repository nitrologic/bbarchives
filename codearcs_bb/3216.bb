; ID: 3216
; Author: Dan
; Date: 2015-07-28 14:43:16
; Title: file requester - B3d
; Description: single directory - file requester

;====================================================================
; Project: FileRequester
; Version: 1.0
; Author: ~Dan~
; Email: 
; Copyright: PD
; Description: Simple File Chooser 
;				displays a list of files in a choosen folder
;				with choosen extension.
;				Use Arrow Up/Down to choose a file
;				Return/Enter to set the filename into ToLoad$ variable
;				Set FR_ext$ to ".*" to accept all files
;		Returns FR_Error=1 if directory does not exist		
;====================================================================
; The following decls are needed ONLY for the Screen function
; if you dont want to use it, comment the whole Screen Function
; and remove the calls to it 
;
; User32.decls	
;;.lib "user32.dll"
;api_GetSystemMetrics% (nIndex%) : "GetSystemMetrics"
;api_GetActiveWindow%():"GetActiveWindow"
;api_GetDC% (hwnd%) : "GetDC"
;api_GetDesktopWindow% () : "GetDesktopWindow"
;
;
; GDI32.decls
;.lib "gdi32.dll"
;api_GetDeviceCaps% (hdc%, nIndex%) : "GetDeviceCaps"
;====================================================================


Screen 320,200
Global TxtY=-2

FR_folder$="c:\"
FR_ext$=".*"
ReqMaxShow=5
;ReqMaxShow=(GraphicsHeight()/FontHeight())-4  ;Use this to have resolution dependant height of the displayed items 

For Z=1 To 3         ; to test the reuseability 
	Gosub FileRequester
	If fr_Error 
		Print FR_folder$
		Print " - No such directory !": Delay 5000 :Exit
	EndIf
	Cls
	Locate 0,0
	If ToLoad$<>""
		Print "("+Z+")"+"filename: "
		Print ToLoad$
	Else
		Print "No filename, or Cancelled!"
	EndIf 
	FlushKeys()
	Print "Press any key to continue"
	WaitKey()
Next

End


.FileRequester 		; Returns filename in ToLoad$, if its empty then it was cancelled or not available 
; 					; Returns FR_Error - 1 Directory doesnt exists
; before call set :
; FR_folder$="c:\data"	; 		to the folder which will be checked
; FR_ext$=".*"	;				as extension of the file displayed, ".*" for every file
; ReqMaxShow=5	;				set how many files will be displayed at once
;ReqMaxShow=(GraphicsHeight()/FontHeight())-4  ;Use this to have height resolution-dependant number of the displayed items 


FR_LoadedFile=0

fr_delay=80   ;Arrow Up/Down Delay time, in millisecs
FR_Error=0

If FileType(FR_folder$)<2 Then ToLoad$="": FR_Error=1 : Goto theend

For fr_x=1 To 2								;Reads 2 time the Current folder !
	FR_DIR=ReadDir(FR_folder$)
	If fr_x=2 									;Second pass, make a dim statement
		Dim FR_Filename$(FR_LoadedFile)
		FR_LoadedFile=0
	EndIf
	
	Repeat
		FR_File$=NextFile$(FR_DIR)
		If FR_File$="" Then Exit 
		If FileType(FR_folder$+"\"+FR_File$) = 1 Then 
			If Lower$(Right$(FR_File$,Len(FR_ext$)))=FR_ext$ Or FR_ext$=".*";Check for .dat files
				FR_LoadedFile=FR_LoadedFile+1							;1st pass count .dat files
				If fr_x=2 Then FR_Filename$(FR_LoadedFile)=FR_File$		;2ns pass add to FR_Filename$
			End If
		End If 
	Forever
	
	CloseDir (FR_DIR)
	If FR_LoadedFile=0 Then ToLoad$="" : Goto theend
Next

FR_RequesterPos=1
FR_ReqOffset=0

;Check if Enter,Return or Esc keys are Held down 


While KeyDown(156) Or KeyDown(28) Or KeyDown(1) ; Delay The Program Execution until theese keys are released
Delay 1
Wend
FlushKeys()
Delay 10

FR_time=MilliSecs()

Repeat 
	Cls
	Color $ff,$ff,$ff
	TextY (-2,"Please Choose: ("+FR_LoadedFile+") files")
	Color $FF,$FF,$0
	TextY (0, String$("-",40))
	
	
	If KeyDown(200) And MilliSecs()-FR_time>fr_delay ; Key Arrow Up
		FR_time=MilliSecs()
		FR_RequesterPos=FR_RequesterPos-1
	EndIf
	
	If KeyDown(208) And MilliSecs()-FR_time>fr_delay; Key Arrow Down
		FR_time=MilliSecs()
		FR_RequesterPos=FR_RequesterPos+1
	EndIf

	If KeyDown(156) Or KeyDown(28)			;Return or Enter
		Exit
	EndIf
	
	If KeyDown(1) 							;Esc
		ToLoad$=""
		Exit
	EndIf
	
	If FR_RequesterPos<1 
		FR_RequesterPos=1
		If FR_ReqOffset>0
			FR_ReqOffset=FR_ReqOffset-1
		EndIf
	EndIf

	
	If FR_RequesterPos>ReqMaxSHow 
		FR_RequesterPos=ReqMaxShow
		If FR_ReqOffset<FR_LoadedFile-ReqMaxShow
			FR_ReqOffset=FR_ReqOffset+1
		EndIf
	EndIf
	
	If FR_RequesterPos>FR_LoadedFile Then FR_RequesterPos=FR_LoadedFile
	For fr_LX=1 To ReqMaxShow
		If fr_LX=FR_RequesterPos
			Color $0,$ff,$ff
			ToLoad$=FR_Filename$(fr_LX+FR_ReqOffset)
		Else
			Color $Af,$af,$af
		EndIf
		If fr_LX <= FR_LoadedFile
			TextY (0,FR_Filename$(fr_LX+FR_ReqOffset))
		Else
			;TextY (0,"-empty slot-") ; Uncheck to display Empty Slot if there are not 
			;							enough files to fit the ReqMaxShow display
		EndIf
	Next
	
	Color $FF,$FF,$0
	TextY (0,String$("-",40))
	Color $ff,$ff,$ff
	TextY (0,"Return to Accept, Esc to Cancel")
	Delay 10
	Flip
Forever
.theend
FlushKeys()
Return 


Function TextY (num,txt$)
;Num = How many columns to skip
;Replaces Print with Text
;Every Function call displays the text
;1 line under the old one, like calling
;multiple print statements after eachother.
;
; Set num to -2 to make the text go on top (like locate 0,0)
; Use the Global TxtY=-2 outside this function
;
;Global TxtY=-2       ;Make TxtY global variable for displaying help text 
   If num=-2 
      TxtY=0
   Else
	  TxtY=TxtY+(FontHeight()*(num+1))
   EndIf
   Text 0,TxtY,txt$
End Function	

Function Screen(x,y)
   DeskX=api_GetSystemMetrics(0)
	DeskY=api_GetSystemMetrics(1)
	If x>DeskX Then x=DeskX
    If x<64 Then x=64
	If y>DeskY Then y=DeskY
    If y<64 Then y=64
    bits=api_GetDeviceCaps(api_GetDC( api_GetDesktopWindow()),12)
	Graphics x,y,bits,2
	Graphics x,y,bits,3
	api_MoveWindow(api_GetActiveWindow(),0,0,DeskX,DeskY,True)
End Function
