; ID: 3215
; Author: Dan
; Date: 2015-07-28 13:23:35
; Title: Path(way) Editor
; Description: Path(way) Editor with Loading and Saving data,Simple File Requester, Scrolling Text, OnScreenDisplay

;====================================================================
; Project: Path(Way) Editor 
; Version: 1.0 - 22.7.2015 ~Dan ~
; Author: Dan
; Email: -.-
; Copyright: PD
; Description: You can Draw a path with Mouse, and save the data
;              as blitzbasic data statements or as binary file.
;  (see ***)   To save the data go into Animate mode and see if the
;              Path is ok, then choose the format to save.
;              Here you can press Left MouseButton to change the moving graphic
;              In Edit mode you can press d multiple times to  
;              load the Demo Data from the data statements.
;              m changes the type of drawing.
;              Press f4 to see a Scrolling text following the path.
;              Hold F1 to exit the scroller.
;              L Opens a simple File requester, which displays the binary saved data.
;***           Make sure you have data folder inside blitzbasic\bin\
;              folder if you start this demo from the IDE,
;              or a data folder in the exe's folder,if you compile it 
;              Space key shows the last The OnScreenDisplay(OSD) message
;              using Blu_Matt's hex2dec function 
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


; Graphic 320,250,32,2         ;Uncomment this
; Graphic 320,250,32,3         ;and this line if you dont want to use Screen function

; Screen fuction gets the desktop resolution and streches the small window to fullscreen
Screen 320,250				  ;Comment this, and uncomment the above if you dont want to use Screen function
;Screen 800,600

SetBuffer BackBuffer()

ChangeDir SystemProperty$("appdir")

bgimage=LoadImage("320x250.png")

;TextY function setting:
Global TxtY=-2
;/

;File Requester Setting:
FR_folder$=SystemProperty$("appdir")+"Data\"  ; Setting up folders
FR_ext$=".dat"
ReqMaxShow=5
;/

Repeat
    Cls
	Color $ff,$ff,$ff
	TextY (-2,"Usage: (Edit Mode)")
	TextY (1,"Left Mouse Button to draw path")
	TextY (0,"Right Mouse B.to Animate")
	TextY (0,"Middle Mouse B.to Clear (BackSpace <-)")
	TextY (0,"L to Load, F4 Scroller demo")
	TextY (0,"D to cycle through Demo Data")
	TextY (0,"Hold Shift + Left MouseButton to ")
	TextY (0,"draw faster. + - Shift speed Modifier ")
	TextY (0,"M To change mode, Space for OSD")
	TextY (0,"Press ESC to END")
	TextY (1,"While in Animate Mode")
	TextY (0,"S= Save as BB-Data statements")
	TextY (0,"E= Save as Binary Data (see L key)")
	Color $ff,0,0
	TextY (1,"Press left mouse key to continue")
	Flip
	Delay 1
Until MouseDown(1)

FlushMouse()
Delay 500
PathwayUsed=0				; Tracks how many path points are set
PathwayMax=3555			; How many Pathways to use 
demod=-1					; Demo tracking Variable

SKMSpeed=80					;Slows down the drawing speed while shift is held down
							;Changeable with + and - keys

Dim Pathway(PathwayMax,1)  	; Store the pathway data. Coordinates: 0=x, 1=y 
Global Timer_m=MilliSecs() 	; For the Timer_mSec function
MDL=MilliSecs() 			; Left mouse Click Delay in edit mode
AppTitle "Edit Mode"
OSDtimeout=5000 ; OSD - OnScreenDisplay Settings

DKP=MilliSecs()  
Gosub SetEditModeOSD

Repeat						;Main Loop (editor)
	Cls
	Gosub DrawBack
	If mmode=1 
		Text MouseX()-3,MouseY()-7,"X"
	Else 
		Rect MouseX(),MouseY(),1,1,0
	EndIf
	
	If KeyDown(42) Or KeyDown(54)		;Left or Right Shift Keys
		
	    ;Checks if Left Mouse and If slowdown timer is reached
		If MouseDown(1)=True And MilliSecs()-MDL=>SKMSpeed And PathwayUsed<PathwayMax ;Left Mousebutton Clicked, and the timer is reached, and PathWay used has not reached The PathWayMax
			MDL=MilliSecs() 
			Gosub PlotIt
		EndIf
	Else
	    ;Checks if Left Mouse is Pressed and if MDL=0
		If MouseDown(1)=True And MDL=0 And PathwayUsed<PathwayMax	;Left Mousebutton Clicked, and It was released allready, and PathWay used has not reached The PathWayMax
			MDL=1
			Gosub PlotIt
		EndIf
		If MouseDown(1)=False And MDL=>1 Then MDL=0 		;Wait until Left MouseButton is released 
		
	EndIf
	
	If MouseDown(2)=True  				;Right Mouse Button shows a Replay
	    Gosub SetAniModeOSD
		Gosub Replay
		Gosub SetEditModeOSD
	EndIf
	
	If MouseDown(3) Or KeyDown(14)						;Middle Mouse Button Erase pathway(or backspace)
		Gosub ClearDim
		While MouseDown(3) Or KeyDown(14)				;Wait untill the keys are released
			Delay 1
		Wend  
	EndIf
	
	If KeyDown(13) Or KeyDown(78)  ; + Numpad +
	   SKMSpeed=SKMSpeed-5
	   If SKMSpeed<=0 Then SKMSpeed=0
	EndIf 
	
	If KeyDown(12) Or KeyDown(74)  ; - Numpad -
	   SKMSpeed=SKMSpeed+5
	   If SKMSpeed >700 Then SKMSpeed=700
	EndIf 
	
	
	If KeyDown(38) 		;l to load
		AppTitle "Loader"
		
		Gosub FileRequester
		
		If FR_Error=0 And Toload$<>""
			Gosub loaddata
			DKP=MilliSecs()
			OSDText$="Loaded File:"
			OSDText1$=ToLoad$
			OSDText2$=""
		Else
			OSDText$="File Requester: Error Loading"
			If Fr_Error=1 
				OSDText2$="Folder does not exists"
				OSDText1$=FR_folder$
				DKP=MilliSecs()
			Else
			  OSDText1$="Cancelled or No files"
			  OSDText2$=""
			EndIf
			AppTitle "Edit Mode"
			FlushKeys
			Delay 250
		EndIf
	EndIf 
	
	If KeyDown(50)					;M for Mode
		mmode=Not mmode
		While KeyDown(50)
			Delay 1
		Wend
	EndIf
	
	If KeyDown(62) And pathwayused>0         ;F4 for scroller
		Gosub scrollit
		Delay 100
		FlushKeys
		Gosub SetEditModeOSD
	ElseIf KeyDown(62) And PathwayUsed=0	;No pathway set, display error msg
	    DKP=MilliSecs() ; OSD - OnScreenDisplay Settings
		OSDText$="No Pathways set !"
		OSDText1$=" Use the Left mouse button"
		OSDText2$=" or D for demo path "
		FlushKeys()
		AppTitle "Edit Mode"
	EndIf
	
	If KeyDown(32)						;D to load Demo data
		Gosub ClearDim
		demod=(demod+1) Mod 3			;Cycle demod from 0 to 2
		Select demod
			Case 0
				Restore demo
			Case 1
				Restore demo1
			Case 2
				Restore demo2
		End Select
		
		Read PathwayUsed				;Here we have no error checking, assuming that the data is correct
		For LoopX=0 To PathwayUsed-1	;Read the data into Pathway()
			Read Pathway(LoopX,0) 
			Read Pathway(LoopX,1)
		Next
		
		While KeyDown(32)			;Wait untill D key is released 
			Delay 1
		Wend 
		DKP=MilliSecs() ; OSD - OnScreenDisplay Settings
		OSDText$="Demo data loaded"
		OSDText1$="Demo #0"+(demod+1)+" / 03"
		OSDText2$=""
	EndIf
	
	Flip 
Until KeyDown(1)				;/Main Loop /editor

End  ; 														Thats all Folks!

.scrollit

AppTitle "Hold F1 to exit the Scroller"

TEX$=" "
TEX$=TEX$+String$(" ",PathWayUsed) ; Do not change this
; Write new text down here
TEX$=TEX$+"Dan presents : SSTT * Super Scrolling Test Text *  "
TEX$=TEX$+"you can Setup the path with the Mouse and save it for later use.      "
TEX$=TEX$+"Press F1 to Return to the Editor, visit www.blitzbasic.com and dont forget to subscribe !!!   :)     .-''-."
TEX$=TEX$+"             "

;and above here as Tex$=Tex$+" new text "
TEX$=Tex$+String$(" ",40) ;And as last, to have the smooth text in/out effect
TLE=Len(TEX$)

;frameTimer=CreateTimer(10)
FlushKeys()
DMSpeed=2		;Demo Scroll-speed Setting (1 fast 4 slowest)
DKP=MilliSecs() ; OSD - OnScreenDisplay Settings
Time_mSec(0)
Repeat
	;WaitTimer(frameTimer)
	For SY=1 To 2									;Repeat 2 times
		For SX=1 To TLE
			THELP$=Mid$(TEX$,SX,PathWayUsed)
			Cls
			If bgimage<>0 Then DrawBlock bgimage,0,0
			Color $ff,$ff,$ff
			For SX1=0 To PathWayUsed-1
				If KeyHit(59) Then Goto loopend 	;F1 Ends it
				THELP1$=Mid$(THELP$,SX1+1,1)
				If SY=1 Then OutlineText1 (Pathway(sx1,0),Pathway(sx1,1),THELP1$) 
				If SY=2 Then OutlineText1 (Pathway((PathwayUsed-1)-sx1,0),Pathway((PathWayUsed-1)-sx1,1),THELP1$)
			Next
			
			If (KeyDown(13) Or KeyDown(78) ) And MilliSecs()-DKP>250  ; + Numpad +
				DMSpeed=DMSpeed-1
				DKP=MilliSecs()
				If DMSpeed<=1 Then DMSpeed=1
			EndIf 
			
			If (KeyDown(12) Or KeyDown(74)) And MilliSecs()-DKP>250  ; - Numpad -
				DMSpeed=DMSpeed+1
				DKP=MilliSecs()
				If DMSpeed >4 Then DMSpeed=4
			EndIf 
			
			While Time_mSec(DMSpeed)>1
			Delay 1
			Wend
			OSDtext$="Demo Text Speed="+DMSpeed
			OSDtext1$="Change with + -"
			OSDtext2$="F1 = Go Back to Edit Mode"
			Gosub OSDtext
			Time_mSec(0)
			Flip
		Next
	Next 
Until KeyHit(59)		; F1, If ever

.loopend 
Delay 1000
FlushKeys()
AppTitle "Edit Mode"
Return

.OSDtext
; Pass OSDtext$ OSDtext1$ OSDtext2$ to display different lines empty will be ignored
; Set OSDtimeout in seconds for display duration
If KeyDown(57) And MilliSecs()-DKP>OSDtimeout Then DKP=MilliSecs()
If MilliSecs()-DKP<OSDtimeout
	If OSDtext$<>"" Then OutlineText(3,3+(FontHeight()*2),OSDText$)
	If OSDtext1$<>"" Then OutlineText(3,3+(FontHeight()*3),OSDText1$)
	If OSDtext2$<>"" Then OutlineText(3,3+(FontHeight()*4),OSDText2$)
EndIf

Return

.ClearDim
For LoopX=0 To PathwayUsed-1
	Pathway(LoopX,0)=0 
	Pathway(LoopX,1)=0
Next
PathwayUsed=0
Return

.PlotIt

Pathway(PathwayUsed,0)=MouseX() 
Pathway(PathwayUsed,1)=MouseY()
PathwayUsed=PathwayUsed+1

Return

.DrawBack						;Draws the Background text and every Way-Point for the Edit mode
If bgimage<>0 Then DrawBlock bgimage,0,0
For LoopX=0 To PathwayUsed-1
	If LoopX=pathwayUsed-1 Then 
		Color $ff,00,00
    ElseIf LoopX=PathwayUsed-2
	    Color $ef,$6f,00
	ElseIf LoopX=PathwayUsed-3
	    Color $cf,$af,00
	ElseIf LoopX=0
	    Color $0,$ff,$0
	Else
		Color $ff,$ff,00
	EndIf 
	If mmode=1 
		Text Pathway(LoopX,0)-3,Pathway(LoopX,1)-7,"X"
	Else
		Rect Pathway(LoopX,0),Pathway(LoopX,1),2,2
	EndIf
Next
Color $0,$ff,$0				; The 1st position of the path shall be always seen (here in green)
If mmode=1
	Text Pathway(0,0)-3,Pathway(0,1)-7,"X"
Else
	Rect Pathway(0,0),Pathway(0,1),2,2
EndIf
Color $80,$f0,$f0
Text 0,0,PathwayUsed+"/"+PathWayMax+" MX="+MouseX()+" MY="+MouseY()+" Shift="+SKMSpeed

Gosub OSDtext
Return

.SetEditModeOSD
OSDtext$="Edit Mode Keys: Esc  BackSpace <-"
OSDtext1$="Left MouseB + Shift / M / F4 + - D"
OSDtext2$="L=Load / Middle/Right MouseButton"
Return

.SetAniModeOSD
OSDText1$="Press Right Mouse B. to Edit"
OSDText2$="S=Save Data,E=Export Binary"
OSDText$="Animate Mode Keys:"
Return

.DrawBackReplay				;Draws the Text/or Image if used for the Animate part
Cls
If bgimage<>0 Then DrawImage bgimage,0,0
Gosub OSDtext
Color $ff,$ff,$ff
Text 0,GraphicsHeight()-18,"Nr. ("+Replay+") of {"+PathwayUsed+"} "
Return

.Replay						;Replay Mode
AppTitle "Animate Mode"
Cls
Replay=0
FlushMouse()
Delay 500
Time_mSec(0)
DKP=MilliSecs()  ; OSD - OnScreenDisplay Settings

Repeat
	If MouseDown(1) And amm=0					;M for Mode
		ammode=(ammode+1) Mod 4
        amm=1
    EndIf
	If Not(MouseDown(1)) And amm=1
		amm=0
	EndIf
    If KeyDown(31) Then savetype=0 : Gosub savedata : DKP=MilliSecs() ;key s
	If KeyDown(18) Then savetype=1 : Gosub savedata : DKP=MilliSecs() ;key e
	Gosub DrawBackReplay
	
	If Time_mSec(-1)>2 Then Time_mSec(0) : Replay=Replay+1
	If Replay>PathwayUsed-1 Then Replay=0
	Select ammode
		Case 0
			Color $00,$ff,$ff
			Oval Pathway(Replay,0),Pathway(Replay,1),8,6,1
			Color $ff,0,0
			Oval Pathway(Replay,0),Pathway(Replay,1),8,6,0
		Case 1
		    Color Rand(0,255),Rand(0,255),Rand(0,255)
			Rect Pathway(Replay,0),Pathway(Replay,1),3,3,1
			Color $ff,0,$FF
			Rect Pathway(Replay,0),Pathway(Replay,1),4,4,0
		Case 2	
			If Replay>0 Then Replay2=Replay-1
			If Replay2>0 Then Replay3=Replay2-1
			If Replay3>0 Then Replay4=Replay3-1
			Color $0,$9f,$ff
			Text Pathway(Replay4,0)-3,Pathway(Replay4,1)-7,"w"
			Color $0,$bf,$ff
			Text Pathway(Replay3,0)-3,Pathway(Replay3,1)-7,"x"
			Color $0,$df,$ff
			Text Pathway(Replay2,0)-3,Pathway(Replay2,1)-7,"y"
			Color $00,$ff,$ff
			Text Pathway(Replay,0)-3,Pathway(Replay,1)-7,"z"
		Case 3
		    Color $bc,10,$cd
			Rect Pathway(Replay,0),Pathway(Replay,1),1,1,0
	End Select
	 Flip
Until MouseDown(2)

While MouseDown(2)   ;Release the Right mousebutton to continue 
	Delay 10
Wend

FlushMouse
AppTitle "Edit Mode"
DKP=MilliSecs() ; OSD - OnScreenDisplay Settings
Return 

;				Load Data
.loaddata

; Binary data format:
; $000000    - Pathways used
; $0000 $0000  - LoopX,LoopY coordinates (as many as Pathways used)

If FileSize(FR_folder$+Toload$)=>7		;Check if file holds at least 7 bytes (see binary data format (at least 1 x,y coordinate pair)
	TextY (1,FR_folder$+Toload$)
	Gosub ClearDim						;Erase the Pathway()

	filein=ReadFile(FR_folder$+Toload$)	;Open the file to read
	
	For LoopX=2 To 0 Step -1			;Read the PathwayUsed from the File (3 bytes)
		RData=ReadByte(filein)
		BB$=BB$+Right$(Hex$(RData),2)	;Stick the Bytes together as hex value ! 
	Next
	PathwayUsed=hex2dec(BB$)			;Calculate the Decimal value of the readed data and set it into PathWayUsed
	bb$=""								;Clear the BB$ to use it later
	For LoopX=0 To PathwayUsed-1		;Start reading X and Y mouse Coordinates from the file
	    For loopy=0 To 1				;Read 2 bytes for X, Then 2 bytes for Y
			RData=ReadByte(filein) : BB$=BB$+Right$(Hex$(RData),2)
			RData=ReadByte(filein) : BB$=BB$+Right$(Hex$(RData),2)
			Pathway(LoopX,LoopY)=hex2dec(BB$) : BB$="" ; Calculate The Bytes and set them into PathWay(x,y)
		Next
	Next
	CloseFile(filein)					;Done ?! Close the file.
	
EndIf

Return

;				Save Data
.savedata
Delay 1			;in case you have 100000 files saved as dat (probably wont, but hey ;) )
FlushKeys
If PathwayUsed=0 
    OSDText$="Not Saved!!!" 
	OSDText1$="Reason:No pathways set.Count=0"
	OSDText2$="Switch to Edit and add some"
	Delay 1000
	FlushKeys 
	Return ; Dont save if no Path(ways) are set 
EndIf 
fnamenum$=String$("0",4-Len(Str(fname)))+Str(fname)	;Calculate the filename numbers with leading 0'os

If savetype=0 			;0 means user has choosen to export it as Data statements
	fileext$=".bb"
Else					;1(else) means to save it in binary format, set the appropriate file ext.
	fileext$=".dat"
EndIf

FileName$=FR_folder$+"PathWay_"+fnamenum$+fileext$	;Forms the filename folder + "pathway_" + filenamenumber

If FileType(FileName$)>0		; If the file exists ( > 0 ) 
	fname=fname+1				;increase fname variable
	Delay 2
	Goto savedata				;and form the filename again, until free filename is found
EndIf

AppTitle "saving file:  "+FileName$

file=WriteFile (FileName$)
z=-1							;counter for data type
Written=0						;variable to write the data at the end of the loop 

If file>0 			;Check if the file could be opened for writing, and if yes:
	If savetype=0 				;Header for the data statements :
	    OSDText2$="as Data File"
	    WriteLine (file,";Format: Data pathways")		;Some informational text 
		WriteLine (file,";Data Mouse X,MouseY in Hexadecimal")
		WriteLine (file,";You can safely Delete the Last "+Chr$(34)+"ffff"+Chr$(34)+",it is Not used")
;		WriteLine (file,";and probably 2~3 last $0000 pairs")
		WriteLine (file,"Data "+PathwayUsed)						; Here is the 1st data statement
	Else						; For binary data (header)
	    OSDText2$="as Binary File"
		For LoopX=1 To 3		; write 3 bytes of the hexadecimal PathwayUsed variable 
			WriteByte (file,hex2dec(Mid$(Hex$(PathwayUsed),(LoopX*2)+1,2)))
		Next
	EndIf
	For LoopX=0 To PathwayUsed-1	; Mouse X,Y loop of all pathways
		If savetype=0 				;As data statements
			z=z+1					;Counts how many data pairs are saved in one line in one data 
			If z=0  				;z0 means the dataitem$ will hold "data x,y,"
				Dataitem$="Data "+"$"+Right$(Hex$(Pathway(LoopX,0)),4)+",$" +Right$(Hex$(Pathway(LoopX,1)),4) + "," : Written=0
			ElseIf z>0 And z<=3		;>0 and <3 the dataitem$ will have "," at the end of the line
				Dataitem$=Dataitem$+"$"+Right$(Hex$(Pathway(LoopX,0)),4) +",$" +Right$(Hex$(Pathway(LoopX,1)),4) + "," : Written=0
			ElseIf z=4				;dataitem$ will have the last pair of x,y without ( , )
				Dataitem$=DataItem$+"$"+Right$(Hex$(Pathway(LoopX,0)),4) +",$" +Right$(Hex$(Pathway(LoopX,1)),4) 
				WriteLine (file,Dataitem$)
				Written=1			;Written 1 to bypass the exit loop, or to write the last data items into the file ! important
				z=-1
			EndIf
		Else						;As binary Data
			For LoopX=0 To PathwayUsed-1	; Every pathwayused (-1 because Pathway() is saved at 0 as 1st)
				For LoopY=0 To 1		;Count for X and Y 
					WriteByte (file,hex2dec(Mid$(Hex$(Pathway(LoopX,LoopY)),5,2))) ;Writes the 1st byte of X or Y
					WriteByte (file,hex2dec(Mid$(Hex$(Pathway(LoopX,LoopY)),7,2))) ;Writes the 2nd byte of X or Y
				Next
			Next
			Written=1   ;Needed only for the Data statements 
		EndIf
	Next
	
	If Written=0		;and Savetype=0		; written=0 to catch the last pairs of data were not written in some cases 
	                    ;(if the above loop ends because the pathway end is reached and the z was lower than 4) 
		WriteLine (file,Dataitem$+Chr$(34)+"FFFF"+Chr$(34)) ;Writes the Lats dataitems and "FFFF" (for readability)
	End If
	CloseFile (file)
	OSDText$="Saved:"
	OSDText1$=FileName$
Else 					;The file could not be opened for Writing
    Cls
	AppTitle "Error "+FileName$+" could NOT be saved" ; Show it in the App Titlebar
	Color $ff,0,0
;	TextY (-2,"")							;Display an error text 
;	TextY (1,"File could NOT be saved !!!")
;	TextY (0,"ERROR! File could NOT be saved !!!")
	OSDText$="Error: 
	If FileType(FR_folder$)=0 				;Displays if the reason was, because the folder doesnt exists (i know it could be checked earlier  :p (but it could be deleted meanwhile aswell ^^))
;		TextY (0,"Folder does not exist:")
;		TextY (0,FR_folder$)
    OSDtext1$="Reason: Folder does not exist"
	OSDText2$=FR_folder$
	EndIf
;	TextY (1,"Press any Key on Keyboard")
	Color $ff,$ff,$ff
	Flip 
;	WaitKey()							;Wait for a keypress 
EndIf

Flip 
FlushKeys()
Delay 250
Return

;				File Requester

.FileRequester		; Returns filename in ToLoad$, if its empty then it was cancelled or not available 
; 					; Returns FR_Error - 1 Directory doesnt exists
; before call set :
; FR_folder$="c:\data"	; 		to the folder which will be checked
; FR_ext$=".*"	;				as extension of the file displayed, ".*" for every file
; ReqMaxShow=5	;				set how many files will be displayed at once
;ReqMaxShow=(GraphicsHeight()/FontHeight())-4  ;Use this to have height resolution-dependant number of the displayed items 

FR_LoadedFile=0		;Resets the variable for multiple uses

fr_delay=80   ;Arrow Up/Down Delay time, in millisecs
FR_Error=0

If FileType(FR_folder$)<2 Then ToLoad$="": FR_Error=1 : Goto theend

For fr_x=1 To 2						;Reads the Current folder 2 times ! (to get the Right number for the Dim)
									;Because of the filtering ability, to store only filtered files in the FR_Filename$ 
	FR_DIR=ReadDir(FR_folder$)		;Open the directory for reading
	If fr_x=2 														;Second pass, make a dim statement
		Dim FR_Filename$(FR_LoadedFile)		;dimensioning the Array
		FR_LoadedFile=0						;reset it for later use
	EndIf
	
	Repeat
		FR_File$=NextFile$(FR_DIR)			;Get the next file from the directory
		If FR_File$="" Then Exit 			;Exit the loop if the last file is reached
		If FileType(FR_folder$+"\"+FR_File$) = 1 Then 		; File Exists !
			If Lower$(Right$(FR_File$,Len(FR_ext$)))=FR_ext$ Or FR_ext$=".*"	;Check the extension or every file
				FR_LoadedFile=FR_LoadedFile+1							;1st pass: count the available files
				If fr_x=2 Then FR_Filename$(FR_LoadedFile)=FR_File$		;2ns pass: add to FR_Filename$()
			End If
		End If 
	Forever
	
	CloseDir (FR_DIR)			;Close the Directory 
	If FR_LoadedFile=0 Then ToLoad$="" : Goto theend	;There were no matching Files found, 
								;set the ToLoad$ as "" and go to the End, bypassing the requester
Next

FR_RequesterPos=1		;Resetting the variables for multiple calls !
FR_ReqOffset=0

;Check if Enter,Return or Esc keys are Held down 

While KeyDown(156) Or KeyDown(28) Or KeyDown(1) ; Delay The Program Execution until theese keys are released
	Delay 1
Wend
FlushKeys()
Delay 10

FR_time=MilliSecs()				;Arrow Up and Down delay

Repeat 							;Requester Loop
	Cls
	If bgimage<>0 Then DrawImage bgimage,0,0	;Draw the background image if available, or skip it ^^
	Color $ff,$ff,$ff
	TextY (-2,"Please Choose:")					;self expained
	Color $FF,$FF,$0
	TextY (0, String$("-",40))					;Displays a line 
	
	
	If KeyDown(200) And MilliSecs()-FR_time>fr_delay ; Arrow Up
		FR_time=MilliSecs()
		FR_RequesterPos=FR_RequesterPos-1
	EndIf
	
	If KeyDown(208) And MilliSecs()-FR_time>fr_delay; Arrow Down
		FR_time=MilliSecs()
		FR_RequesterPos=FR_RequesterPos+1
	EndIf
	
	If KeyDown(156) Or KeyDown(28)			;Return or Enter
		Exit
	EndIf
	
	If KeyDown(1) 							;Esc
		ToLoad$=""		; Set the ToLoad$ as ""
		Exit
	EndIf
	
	If FR_RequesterPos<1 					;Calculating the Highlighted Text position
		FR_RequesterPos=1
		If FR_ReqOffset>0					;if it were lower than 0, scroll up
			FR_ReqOffset=FR_ReqOffset-1
		EndIf
	EndIf
	
	If FR_RequesterPos>ReqMaxSHow 
		FR_RequesterPos=ReqMaxShow
		If FR_ReqOffset<FR_LoadedFile-ReqMaxShow	;;if it were lower than 0, scroll down
			FR_ReqOffset=FR_ReqOffset+1
		EndIf
	EndIf
	
	If FR_RequesterPos>FR_LoadedFile Then FR_RequesterPos=FR_LoadedFile ; Dont go below Maximal loaded files
	
	For fr_LX=1 To ReqMaxShow				;This loop displays the filenames available up to the ReqMaxShow
		If fr_LX=FR_RequesterPos
			Color $0,$ff,$ff				; This is the highlight Color
			ToLoad$=FR_Filename$(fr_LX+FR_ReqOffset)	;Set ToLoad$ as the filename
		Else
			Color $Af,$af,$af				;This is the normal Color
		EndIf
		If fr_LX <= FR_LoadedFile
			TextY (0,FR_Filename$(fr_LX+FR_ReqOffset))	;Here is the text displayed
		Else
			;TextY (0,"-empty slot-") ; Uncheck to display Empty Slot if there are not 
			;							enough files to fit the ReqMaxShow display
		EndIf
	Next
	
	Color $FF,$FF,$0
	TextY (0,String$("-",40))					;Displays a line 
	Color $ff,$ff,$ff
	TextY (0,"Return to Accept, Esc to Cancel")
	Delay 10
	Flip
Forever						;Requester loop goes on forever and forever (until esc or return) or no files found

.theend
FlushKeys()
Return 

Function OutlineText(x,y,Txt$)
				Color $30,$50,$90
				For zzx=-1 To 1
					For zzy=-1 To 1
						If zzx<>0 And zzy<>0 Then Text x+zzx,y+zzy,Txt$
					Next
				Next
				Color $ff,$ff,$ff
				Text x,y,Txt$
End Function

Function OutlineText1(x,y,Txt$)
				Color $90,$50,$30
				For zzx=-1 To 0
					For zzy=-1 To 0
					  Text x+zzx,y+zzy,Txt$
					Next
				Next
				Color $ff,$ff,$ff
				Text x,y,Txt$
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

Function Time_mSec(x)
;Global Timer_m=MilliSecs() ;<---Put at the start of your program---
; x can be -1<,0 or >0 
;- numbers returns miliseconds passed since last function call with 0
;0 resets the timer
;above 0 sets a countdown timer in seconds
	
	If x>0
	    y1=MilliSecs()-Timer_m
		If Len(y1)>2
			y2=Left$(y1,Len(y1)-2)
			Return x-Int(y2)
		Else
			Return x
		EndIf
	ElseIf x=0
		Timer_m=MilliSecs()
    Else
	    y1=Left$(MilliSecs(),7)
		y2=Left$(Timer_m,7)
		Return Int(y1)-Int(y2)
	EndIf
End Function

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

Function hex2dec%(hex_number$)
; Converts the supplied hex number into a decimal number
; If hex_number isn't a valid hex number, then returns -1
; written by Matt Burns (Blu_Matt / matt@blis.co.uk)
	Local the_hex$=Upper(Trim(hex_number$))		; the hex number
	Local base_power%=0							; the current base power
	Local base%=16								; the base to convert from
	Local the_dec%=0							; the decimal representation
	Local chars%=0
	Local hex_length%=Len(the_hex$)				; current length of the hex
	If Left(the_hex$,1)="$" Then 				; removes any leading "$"
		the_hex$=Right(the_hex$,hex_length%-1)
	EndIf
	hex_length%=Len(the_hex$)					; current length of the hex
	Local chars_left%=hex_length%				; current number of chars left
	Local hex_left$=the_hex$					; current hex left
	Repeat
		current_hex$=Right(hex_left$,1) 		; gets the current hex char
		If ((Asc(current_hex$)>=Asc("0")) And (Asc(current_hex$)<=Asc("9"))) Then
			hex_dec%=current_hex$				; digit 0-9
		ElseIf ((Asc(current_hex$)>=Asc("A")) And (Asc(current_hex$)<=Asc("F"))) Then
			hex_dec%=Asc(current_hex$)-55		; char A-F
		Else
			Return -1 							; found an illegal character, aborting...
		EndIf
		the_dec%=the_dec%+((base%^base_power%)*hex_dec%)	; add the local hex digit to the total
		base_power%=base_power%+1				; increase the base power
		chars_left%=chars_left%-1				; reduce the number of chars left
		hex_left$=Left(hex_left$,chars_left%)	; sets the remainder
	Until chars_left%=0
	Return the_dec%								; return the result
End Function

.demo
Data 152
Data $0002,$0006,$0008,$0009,$000E,$000E,$0015,$0014,$001D,$0013,$0025,$0011,$002E,$000C,$0036,$0006,$0036,$0006,$003C,$000C
Data $0042,$0015,$0049,$0018,$0050,$0017,$0058,$0014,$005F,$0010,$0066,$000B,$006E,$0005,$006E,$0005,$0075,$0010,$007D,$0015
Data $0087,$0019,$0090,$0018,$0099,$0015,$00A2,$0011,$00AA,$000D,$00B2,$0007,$00B9,$0004,$00B9,$0004,$00B9,$0004,$00C0,$000E
Data $00C9,$0015,$00D2,$0016,$00DC,$0016,$00E6,$0016,$00EE,$0014,$00F5,$0010,$00FD,$000E,$0105,$000A,$010D,$0006,$010D,$0006
Data $010D,$0006,$0115,$000E,$0118,$0019,$0119,$0024,$0120,$0029,$0127,$0023,$012A,$0017,$012E,$000D,$0131,$0004,$0131,$0004
Data $0134,$0010,$0139,$0019,$013F,$001E,$013E,$001E,$0136,$0024,$012F,$0029,$012A,$0032,$0128,$003C,$0128,$0045,$0128,$004E
Data $0129,$0059,$012D,$0064,$0131,$006E,$0138,$0076,$013D,$0080,$013D,$0080,$0135,$0085,$012B,$0087,$0121,$008C,$0119,$0094
Data $011A,$00A0,$0124,$00A8,$012C,$00AF,$0135,$00B6,$013B,$00BA,$013B,$00BA,$013B,$00BA,$013B,$00BA,$013B,$00BA,$0134,$00C1
Data $012C,$00C7,$012A,$00D2,$0129,$00DD,$0129,$00E8,$0128,$00F3,$0128,$00F3,$0128,$00F3,$0121,$00EA,$011A,$00E1,$0112,$00D9
Data $0109,$00D0,$0101,$00CF,$00F8,$00D3,$00EE,$00DC,$00E4,$00E7,$00DC,$00EF,$00D2,$00F3,$00CA,$00E9,$00C3,$00DE,$00BA,$00D2
Data $00B0,$00CC,$00A6,$00CC,$009C,$00CC,$0094,$00D3,$008C,$00DB,$0084,$00E5,$0081,$00EF,$0079,$00F3,$0079,$00F3,$0071,$00E8
Data $0069,$00DD,$0060,$00D8,$0057,$00D8,$004E,$00DB,$0046,$00DF,$003D,$00E7,$0035,$00EC,$002E,$00F4,$002E,$00F4,$0025,$00EA
Data $001C,$00DF,$0014,$00D4,$000B,$00D0,$0002,$00CD,$0002,$00CD,$000B,$00C4,$0014,$00BC,$001B,$00B1,$001B,$00A5,$0017,$0099
Data $0010,$0090,$0009,$0087,$0003,$007C,$0003,$007C,$000A,$0074,$0012,$006B,$0013,$0060,$0010,$0055,$000B,$004B,$0006,$0041
Data $0001,$0038,$0001,$0038,$0009,$0034,$0012,$0034,$001B,$002F,$0019,$0025,$0013,$001F,$000C,$0018,$0005,$0014,$0001,$000B
Data $0000,$0004,$0000,$0004,"FFFF"

.demo1

Data 104
Data $013F,$0067,$013A,$005F,$0134,$0057,$012C,$0050,$0121,$004D,$0117,$004D,$010C,$0050,$0104,$0055,$00FE,$005B,$00F7,$0061
Data $00F0,$0068,$00E8,$006E,$00DE,$0074,$00D5,$0078,$00CC,$0077,$00C4,$0073,$00BE,$006C,$00B7,$0062,$00B2,$005A,$00AE,$0052
Data $00A9,$004B,$00A3,$0045,$009A,$0043,$0092,$0046,$008C,$004C,$0086,$0053,$0080,$005A,$007A,$0061,$0074,$0067,$006F,$006E
Data $0068,$0073,$005E,$0076,$0058,$0071,$0054,$006B,$004F,$0062,$004B,$005B,$0048,$0055,$0043,$004D,$003E,$0046,$003A,$0042
Data $0033,$003E,$002C,$003D,$0024,$003E,$001E,$0042,$0017,$0048,$0012,$004E,$000D,$0054,$0008,$005B,$0004,$0063,$0000,$006D
Data $0000,$00AF,$0009,$00A6,$0012,$00A2,$001B,$009D,$0024,$00A1,$002C,$00A9,$0033,$00B2,$003A,$00BA,$0041,$00C2,$0049,$00BD
Data $0052,$00B5,$005D,$00AC,$0066,$00A6,$0072,$00A1,$0080,$009F,$008A,$00A3,$0092,$00A9,$009B,$00B1,$00A0,$00B8,$00A3,$00C3
Data $00A4,$00D0,$00A4,$00DE,$00A7,$00EB,$00AE,$00EC,$00B4,$00E5,$00B7,$00DA,$00BA,$00CC,$00BD,$00BF,$00C0,$00B7,$00C9,$00AF
Data $00D4,$00AD,$00DC,$00AD,$00E2,$00B4,$00E7,$00BB,$00EE,$00C6,$00F4,$00D2,$00F6,$00DD,$00FD,$00E9,$0102,$00ED,$010C,$00ED
Data $0111,$00E5,$0116,$00DC,$0119,$00D2,$011B,$00C7,$011A,$00BC,$011A,$00AE,$0121,$00AE,$0128,$00AE,$0130,$00AF,$0139,$00AF
Data $013E,$00AC,$013E,$009F,$013E,$0090,$013E,$007E

.demo2
Data 12
Data $0033,$0047,$003E,$0047,$004A,$0047,$0054,$0047,$0062,$0048
Data $006E,$0048,$007C,$0048,$0089,$0048,$0096,$0048,$00A3,$0049
Data $00AE,$0049,$00B9,$0049
