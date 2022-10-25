; ID: 1406
; Author: Nicstt
; Date: 2005-06-21 19:49:31
; Title: Data from an Image
; Description: Create Data from an Image or Picture

;***********************************************************
;**                                                       **
;**            Data Maker by Nicholas Tindall             **
;** Turn Pictures into Data for copying into your program **
;**           Copyright Nicholas Tindall 2005             **
;**                                                       **
;***********************************************************



Const EVENT_None		= $0		; No event (eg. a WaitEvent timeout)
Const EVENT_KeyDown		= $101		; Key pressed
Const EVENT_KeyUp		= $102		; Key released
Const EVENT_ASCII		= $103		; ASCII key pressed
Const EVENT_MouseDown	= $201		; Mouse button pressed
Const EVENT_MouseUp		= $202		; Mouse button released
Const EVENT_MouseMove	= $203		; Mouse moved
Const EVENT_Gadget		= $401		; Gadget clicked
Const EVENT_Move		= $801		; Window moved
Const EVENT_Size		= $802		; Window resized
Const EVENT_Close		= $803		; Window closed
Const EVENT_Front		= $804		; Window brought to front
Const EVENT_Menu		= $1001		; Menu item selected
Const EVENT_LostFocus	= $2001		; App lost focus
Const EVENT_GotFocus	= $2002		; App got focus
Const EVENT_Timer		= $4001		; Timer event occurred


Global gfxBankDisplay		=	0
Global gfxBank3				=	0
Global gfxBank4				=	0
Global encrypting			=	0
Global cleaning				=	0
Global height1				=	0
Global width1				= 	0
Global heightY				=	0
Global widthX				=	0
Global totalA				=	0
Global progress				=	0
Global count				=	0
Global counter				=	0
Global timeSet				=	0
Global timeS				=	0
Global timeM				=	0
Global timeH				=	0
Global timeSec$				=	"00"
Global timeMin$				=	"00"
Global timeHour$			=	"00"
Global baseR				=	0
Global baseG				=	0
Global baseB				=	0
Global hexDec$				=	""
Global canvasPicEncrypt
Global picdataBank
Global dirPath$				=	""

Dim progBar (102)
Dim progBar2# (101)
For a = 0 To 100 
	progBar2#(a) = Float(a) * 0.01
Next
progBar2#(101) = 1

mainwindow= CenterWindow ("Data Maker v1.0", 640, 480, 0, 11) ; name, width, height, group, style
SetMinWindowSize mainwindow, 640, 200

; data at the end if copied into a text file will produce a bmp that i turned into the icon, you need the specific .decl for it - see a post by grey alien
;icon = ExtractIconA (QueryObject (mainwindow, 1), "Data Maker.ico", 0) ; used to display own icon if have the required userlibs in blitz
;SetClassLongA ( QueryObject (mainwindow, 1), - 14, icon) ; thx to Grey Alien for that:)

Global buttonLoadPic	=	CreateButton( "Load Picture", 	ClientWidth(mainwindow)*0.00+25, 5, 85, 32, mainwindow, 1 )
Global buttonLoadData	=	CreateButton( "Load Data", ClientWidth(mainwindow)*0.15+25, 5, 85, 32, mainwindow, 1 )
Global buttonSavePic	=	CreateButton( "Save Picture", 	ClientWidth(mainwindow)*0.30+25, 5, 85, 32, mainwindow, 1 )
Global buttonSaveData	=	CreateButton( "Save Data Info", ClientWidth(mainwindow)*0.45+25, 5, 85, 32, mainwindow, 1 )
Global buttonCompData	=	CreateButton( "Compile Data",   ClientWidth(mainwindow)*0.60+25, 5, 85, 32, mainwindow, 1 )
Global buttonConvertData=	CreateButton( "Convert Data",   ClientWidth(mainwindow)*0.00+25, 45, 85, 32, mainwindow, 1 )
Global buttonViewPic	=	CreateButton( "View Picture",   ClientWidth(mainwindow)*0.15+25, 45, 85, 32, mainwindow, 1 )
Global buttonStopComp	=	CreateButton( "Stop Compilation",ClientWidth(mainwindow)*0.30+25, 45, 85, 32, mainwindow, 1 )
Global buttonStopConv	=	CreateButton( "Stop Conversion",  ClientWidth(mainwindow)*0.30+25, 45, 85, 32, mainwindow, 1 )
Global buttonClearDisplay=	CreateButton( "Clear Display",	ClientWidth(mainwindow)*0.45+25, 45, 85, 32, mainwindow, 1 )
Global buttonQuit		=	CreateButton( "Quit Program",   ClientWidth(mainwindow)*0.60+25, 45, 85, 32, mainwindow, 1 )
Global buttonDeleteData	=	CreateButton( "Delete Data",	ClientWidth(mainwindow)*0.75+25, 5, 110, 32, mainwindow, 1 )
Global buttonDeletePic	=	CreateButton( "Delete Picture",	ClientWidth(mainwindow)*0.75+25, 45, 110, 32, mainwindow, 1 )

SetGadgetLayout buttonLoadPic, 2, 2, 2, 2 : SetGadgetLayout buttonLoadData, 2, 2, 2, 2 : SetGadgetLayout buttonSavePic, 2, 2, 2, 2
SetGadgetLayout buttonSaveData, 2, 2, 2, 2 : SetGadgetLayout buttonCompData, 2, 2, 2, 2 : SetGadgetLayout buttonConvertData, 2, 2, 2, 2
SetGadgetLayout buttonViewPic, 2, 2, 2, 2 SetGadgetLayout buttonStopComp, 2, 2, 2, 2 : SetGadgetLayout buttonStopConv, 2, 2, 2, 2
SetGadgetLayout buttonClearDisplay, 2, 2, 2, 2 : SetGadgetLayout buttonQuit, 2, 2, 2, 2
SetGadgetLayout buttonDeleteData, 2, 2, 2, 2 : SetGadgetLayout buttonDeletePic, 2, 2, 2, 2

HideGadget buttonStopConv

DisableGadget buttonSavePic : DisableGadget buttonSaveData : DisableGadget buttonCompData : DisableGadget buttonConvertData
DisableGadget buttonViewPic : DisableGadget buttonStopComp : DisableGadget buttonDeleteData : DisableGadget buttonDeletePic
DisableGadget buttonClearDisplay

; * * * listbox that will display list of files chosen
Global listBoxFiles = CreateListBox (10, 90, 250, 230, mainwindow)
DisableGadget listBoxFiles
; ******

; * * * for viewing pictures and displaying data
Global canvasPicPreview = CreateCanvas (270, 88, 359, 388 , mainwindow)
Global listBoxTotal 		= CreateListBox (10, 325, 250, 20, mainwindow)
Global listBoxTimeH 		= CreateListBox (10, 380, 28, 18, mainwindow)
Global listBoxTimeM 		= CreateListBox (40, 380, 28, 18, mainwindow)
Global listBoxTimeS 		= CreateListBox (70, 380, 28, 18, mainwindow)
DisableGadget listBoxTotal
DisableGadget listBoxTimeH : DisableGadget listBoxTimeM : DisableGadget listBoxTimeS
; ******

SetGadgetLayout listBoxFiles, 2, 2, 2, 2
SetGadgetLayout canvasPicPreview, 2, 2, 2, 2 : SetGadgetLayout listBoxTotal, 2, 2, 2, 2
SetGadgetLayout listBoxTimeH, 2, 2, 2, 2 : SetGadgetLayout listBoxTimeM, 2, 2, 2, 2 : SetGadgetLayout listBoxTimeS, 2, 2, 2, 2

Global barProcessed 	= CreateProgBar (10, 350, 250, 20, mainwindow) : SetGadgetLayout barProcessed, 2, 2, 2, 2

SetBuffer CanvasBuffer (canvasPicPreview)
ClsColor 212, 208, 200 : Cls : FlipCanvas canvasPicPreview
Repeat
	Select WaitEvent(0)
		Case EVENT_Gadget ; gadget clicked
			Select EventSource() 
				Case buttonQuit
					If Confirm( "Confirm Quit?" ) = True Then Gosub freeallgadgets2 : EndGraphics : End
				Case buttonLoadPic ; select picture to load
					dirPath$ = RequestFile ("Select a picture to load...", "bmp,BMP,jpg,*.*,jpeg,JPG,JPEG", 0 )
					If dirPath$ <> ""
						If gfxBankDisplay <> 0 Then FreeImage gfxBankDisplay : gfxBankDisplay = 0
						If gfxBank3 <> 0 Then FreeImage gfxBank3 : gfxBank3 = 0
						If gfxBank4 <> 0 Then FreeImage gfxBank4 : gfxBank4 = 0
						gfxBank3 = LoadImage(dirPath$) : gfxBankDisplay = CopyImage(gfxBank3)
						canvasPicEncrypt = CreateCanvas (635, 470, ImageWidth(gfxBank3), ImageHeight(gfxBank3) , mainwindow)
						;ClsColor 0, 0, 0 : Cls : FlipCanvas canvasPicEncrypt
						gfxBank4 = CreateImage ( ImageWidth(gfxBank3), ImageHeight(gfxBank3) )
						gfxBank4 = CopyImage(gfxBank3) : EnableGadget buttonViewPic : EnableGadget buttonSavePic
						EnableGadget buttonCompData : EnableGadget buttonDeletePic
						If Len(dirPath$) < 120
							SetStatusText mainwindow, dirPath$
						ElseIf Len(dirPath$) > 119
							SetStatusText mainwindow, "..." + Right$ (dirPath$,119)
						EndIf
					EndIf
					ActivateGadget mainwindow
				Case buttonLoadData ; select encripted file to load
					dirPath$ = RequestFile ("Select a file to load...", "txt, dat", 0 )
					If dirPath$ <> ""
						ClearGadgetItems listBoxTimeH : ClearGadgetItems listBoxTimeM : ClearGadgetItems listBoxTimeS : ClearGadgetItems listBoxTotal
						If gfxBankDisplay <> 0 Then FreeImage gfxBankDisplay : gfxBankDisplay = 0
						If gfxBank3 <> 0 Then FreeImage gfxBank3 : gfxBank3 = 0
						If gfxBank4 <> 0 Then FreeImage gfxBank4 : gfxBank4 = 0
						count = 0 : progress = 0 : counter = 0 : timeSet = MilliSecs()
						PreCleanAndLoad()
						canvasPicEncrypt = CreateCanvas (638, 478, width1, height1, mainwindow)
						SetBuffer CanvasBuffer (canvasPicEncrypt)
						ClsColor 0, 0, 0 : Cls : FlipCanvas canvasPicEncrypt
						EnableGadget buttonDeleteData : EnableGadget buttonConvertData : DisableGadget buttonViewPic : DisableGadget buttonClearDisplay
						If Len(dirPath$) < 120
							SetStatusText mainwindow, dirPath$
						ElseIf Len(dirPath$) > 119
							SetStatusText mainwindow, "..." + Right$ (dirPath$,119)
						EndIf
					EndIf
					ActivateGadget mainwindow
				Case buttonViewPic
					If ( ImageWidth(gfxBankDisplay) > 359 ) Or ( ImageHeight(gfxBankDisplay) > 388 )
						ResizeImage gfxBankDisplay, 359,388
					EndIf
					SetBuffer CanvasBuffer (canvasPicPreview)
					ClsColor 0, 0, 0 : Cls : DrawImage gfxBankDisplay, 2, 2
					FlipCanvas canvasPicPreview
					EnableGadget buttonClearDisplay : ActivateGadget mainwindow
				Case buttonSavePic ; save picture
					dirPath$ = RequestFile ("Save picture to file...", "bmp, jpg", 1 )
					If dirPath$ <> ""
						SaveImage(gfxBank3, dirPath$) 
					EndIf
					ActivateGadget mainwindow
				Case buttonClearDisplay
					SetBuffer CanvasBuffer (canvasPicPreview)
					ClsColor 212, 208, 200 : Cls : FlipCanvas canvasPicPreview
					DisableGadget buttonClearDisplay
					ActivateGadget mainwindow
				Case buttonStopComp
					EnableGadget buttonLoadPic : EnableGadget buttonLoadData : EnableGadget buttonCompData
					DisableGadget buttonStopComp
					SetBuffer CanvasBuffer (canvasPicEncrypt)
					Cls : FlipCanvas canvasPicEncrypt
					If ImageWidth (gfxBank3) > 0 Or ImageHeight(gfxBank3) > 0
						EnableGadget buttonSavePic : EnableGadget buttonViewPic : EnableGadget buttonDeletePic; : EnableGadget buttonClearDisplay
					EndIf
					encrypting  = 0 : ActivateGadget mainwindow : FreeBank picdataBank : picdataBank = 0
				Case buttonDeleteData
					DisableGadget buttonSavePic : DisableGadget buttonCompData : DisableGadget buttonDeleteData
					DisableGadget buttonSaveData : DisableGadget buttonConvertData : HideGadget buttonStopConv : ShowGadget buttonStopComp
					ClearGadgetItems listBoxTimeH : ClearGadgetItems listBoxTimeM : ClearGadgetItems listBoxTimeS
					ClearGadgetItems listBoxTotal : FreeGadget canvasPicEncrypt : UpdateProgBar barProcessed, 0
					SetStatusText mainwindow, "" : ActivateGadget mainwindow : FreeBank picdataBank : picdataBank = 0
				Case buttonDeletePic
					DisableGadget buttonViewPic : DisableGadget buttonSavePic : DisableGadget buttonCompData : DisableGadget buttonDeletePic
					ClearGadgetItems listBoxTimeH : ClearGadgetItems listBoxTimeM : ClearGadgetItems listBoxTimeS
					ClearGadgetItems listBoxTotal : SetStatusText mainwindow, ""
					If gfxBankDisplay <> 0 Then FreeImage gfxBankDisplay : gfxBankDisplay = 0
					If gfxBank3 <> 0 Then FreeImage gfxBank3 : gfxBank3 = 0
					If gfxBank4 <> 0 Then FreeImage gfxBank4 : gfxBank4 = 0
					SetStatusText mainwindow, "" : UpdateProgBar barProcessed, 0
					ClearGadgetItems listBoxTimeH : ClearGadgetItems listBoxTimeM : ClearGadgetItems listBoxTimeS
					SetBuffer CanvasBuffer (canvasPicPreview)
					ClsColor 212, 208, 200 : Cls : FlipCanvas canvasPicPreview : ActivateGadget mainwindow : DisableGadget buttonClearDisplay
				Case buttonSaveData
					dirPath$ = RequestFile ("Save Picture Data to Disk...", "txt", 1 )
					If dirPath$ <> "" And Right$(dirPath$,4) = ".txt"
						count = 0 : progress = 0 : counter = 0 : timeSet = MilliSecs()
						savingdata = WriteFile(dirPath$)
						WriteLine savingdata, "Data " + height1 + " ; height of picture" : WriteLine savingdata, "Data " + width1 + " ; width of picture"
						WriteLine savingdata, "; rgb values for picture - copy and paste into program"
						ab$ = "" : count = 0
						For a = 1 To height1
							For b = 1 To width1							
								ab$ = ab$ + PeekByte (picdataBank, count) + ", "
								count = count + 1
								ab$ = ab$ + PeekByte (picdataBank, count) + ", "
								count = count + 1
								ab$ = ab$ + PeekByte (picdataBank, count) + ", "
								count = count + 1
								progress = progress + 1
								If progBar(counter) < progress And progBar(counter + 1) > progress	
									TimeAndProgression()
								EndIf
							Next
							ab$ = Left$(ab$, (Len(ab$) - 2)) + " "
							WriteLine savingdata, "Data " + ab$
							ab$ = ""
						Next
						CloseFile(savingdata)
						TimeTaken()
						DisableGadget buttonSaveData
						;ClearGadgetItems listBoxTimeH : ClearGadgetItems listBoxTimeM : ClearGadgetItems listBoxTimeS : ClearGadgetItems listBoxTotal : 
						FreeBank picdataBank : picdataBank = 0
					EndIf
					ActivateGadget mainwindow
				Case buttonCompData
					SetBuffer CanvasBuffer (canvasPicEncrypt)
					Cls : DrawImage gfxBank4, 0, 0 : FlipCanvas canvasPicEncrypt
					ClearGadgetItems listBoxTimeH : ClearGadgetItems listBoxTimeM : ClearGadgetItems listBoxTimeS
					ClearGadgetItems listBoxTotal
					DisableGadget buttonLoadPic : DisableGadget buttonLoadData : DisableGadget buttonSavePic : DisableGadget buttonSaveData
				 	DisableGadget buttonConvertData : DisableGadget buttonViewPic : DisableGadget buttonClearDisplay : DisableGadget buttonCompData
					DisableGadget buttonDeletePic : EnableGadget buttonStopComp
					timeS = 0 : timeM = 0 : timeH = 0 : timeSec$ = "00" : timeMin$ = "00" : timeHour$ = "00"
					encrypting = 1 : height1 = ImageHeight (gfxBank4) : heightY = 0 : width1 = ImageWidth (gfxBank4) : widthX = 0
					progress = 0 : totalA = width1 * height1 : count = 0
					For counter = 0 To 100 : progBar(counter) = (totalA / 100) * counter : Next : counter = 0 : progBar(101) = totalA * 2
					AddGadgetItem listBoxTotal, "Total Data:  " + Str totalA
					AddGadgetItem listBoxTimeH, "00" : AddGadgetItem listBoxTimeM, "00" : AddGadgetItem listBoxTimeS, "00" : timeSet = MilliSecs()
					ActivateGadget mainwindow : picdataBank = CreateBank (totalA * 3)
				Case buttonConvertData
					ClearGadgetItems listBoxTimeH : ClearGadgetItems listBoxTimeM : ClearGadgetItems listBoxTimeS : ClearGadgetItems listBoxTotal
					DisableGadget buttonLoadPic : DisableGadget buttonLoadData : DisableGadget buttonSavePic : DisableGadget buttonSaveData
				 	DisableGadget buttonConvertData : DisableGadget buttonViewPic : DisableGadget buttonClearDisplay : DisableGadget buttonCompData
					DisableGadget buttonDeletePic : DisableGadget buttonStopComp : HideGadget buttonStopComp ShowGadget buttonStopConv
					EnableGadget buttonStopConv : DisableGadget buttonDeleteData
					timeS = 0 : timeM = 0 : timeH = 0 : timeSec$ = "00" : timeMin$ = "00" : timeHour$ = "00"
					cleaning = 1 : heightY = 0 : widthX = 0 : progress = 0 : count = 0
					For counter = 0 To 100 : progBar(counter) = (totalA / 100) * counter : Next : counter = 0 : progBar(101) = totalA * 2
					AddGadgetItem listBoxTotal, "Total to Draw: " + Str totalA
					AddGadgetItem listBoxTimeH, "00" : AddGadgetItem listBoxTimeM, "00" : AddGadgetItem listBoxTimeS, "00" : timeSet = MilliSecs()
					ActivateGadget mainwindow
				Case buttonStopConv 
					EnableGadget buttonLoadPic : EnableGadget buttonLoadData : EnableGadget buttonConvertData : EnableGadget buttonClearDisplay 
					ShowGadget buttonStopComp : EnableGadget buttonConvertData : HideGadget buttonStopConv : EnableGadget buttonDeleteData
					ClsColor 212, 208, 200 : Cls : FlipCanvas canvasPicPreview
					ClearGadgetItems listBoxTimeH : ClearGadgetItems listBoxTimeM : ClearGadgetItems listBoxTimeS
					ClearGadgetItems listBoxTotal
					cleaning = 0 : ActivateGadget mainwindow
			End Select
		Case EVENT_KeyUp
			If EventData() = 1 ; quit
				Gosub freeallgadgets2 : EndGraphics : End
			EndIf
			If EventData() = 25 ; preferences

			EndIf
		Case EVENT_Close	;window close
			Gosub freeallgadgets2 : EndGraphics : End
	End Select

	If encrypting  = 1 Then EncryptFile()
	If cleaning = 1 Then CleanFile()

Forever
	Gosub freeallgadgets2 : EndGraphics
End
; * * * * *  F U N C T I O N S  * * * * *

Function CleanFile()

	If progress = 0
		ClsColor 0, 0, 0 : Cls

		red = PeekByte (picdataBank, count)
		count = count + 1
		green = PeekByte (picdataBank, count)
		count = count + 1
		blue = PeekByte (picdataBank, count)
		count = count + 1

		Color red, green, blue
		Plot widthX, heightY
		FlipCanvas canvasPicEncrypt
	Else
		red = PeekByte (picdataBank, count)
		count = count + 1
		green = PeekByte (picdataBank, count)
		count = count + 1
		blue = PeekByte (picdataBank, count)
		count = count + 1

		Color red, green, blue
		Plot widthX, heightY : FlipCanvas canvasPicEncrypt		
	EndIf
	widthX  = widthX + 1
	If widthX = width1
		heightY = heightY + 1
		widthX = 0
	EndIf
	progress = progress + 1
	If progBar(counter) < progress And progBar(counter + 1) > progress 
		TimeAndProgression()
	EndIf
	If progress = totalA
		TimeTaken()
		If gfxBank3 <> 0 Then FreeImage gfxBank3 : gfxBank3 = 0
		gfxBank3 = CreateImage ( width1, height1 )
		CopyRect 0, 0, width1, height1, 0, 0, CanvasBuffer(canvasPicEncrypt), ImageBuffer(gfxBank3)
		cleaning = 10 : heightY = 0 : widthX = 0
		FreeGadget canvasPicEncrypt
		EnableGadget buttonLoadPic : EnableGadget buttonLoadData : EnableGadget buttonSavePic : EnableGadget buttonClearDisplay 
		ShowGadget buttonStopComp HideGadget buttonStopConv : DisableGadget buttonStopComp : EnableGadget buttonDeletePic
		DisableGadget buttonStopComp : DisableGadget buttonCompData : ClearGadgetItems listBoxTotal
	EndIf

End Function

Function EncryptFile()
	SetBuffer CanvasBuffer (canvasPicEncrypt) ;canvasPicPreview
	GetColor(widthX, heightY)
	red = ColorRed()
	green = ColorGreen()
	blue = ColorBlue()

	PokeByte picdataBank, count, red
	count = count + 1
	PokeByte picdataBank, count, green
	count = count + 1
	PokeByte picdataBank, count, blue
	count = count + 1

	widthX  = widthX + 1

	If widthX = width1
		heightY = heightY + 1
		widthX = 0
	EndIf
	If progBar(counter) < progress And progBar(counter + 1) > progress 
		TimeAndProgression()
	EndIf
	progress = progress + 1
	If progress = totalA
		TimeTaken()
		encrypting = 10 : heightY = 0 : widthX = 0
		EnableGadget buttonLoadPic : EnableGadget buttonLoadData : EnableGadget buttonSaveData : EnableGadget buttonDeleteData
		EnableGadget buttonClearDisplay
		DisableGadget buttonStopComp : DisableGadget buttonCompData : ClearGadgetItems listBoxTotal
	EndIf
End Function

Function PreCleanAndLoad()
	loadingData = ReadFile(dirPath$)
	linesh$ = ReadLine$(loadingData)
	linesw$ = ReadLine$(loadingData)
	linesdata$ = ReadLine$(loadingData)
	linesdata$ = ReadLine$(loadingData)
	linesh$ = Replace$(linesh$, "Data ", "")
	linesw$ = Replace$(linesw$, "Data ", "")
	linesdata$ = Replace$(linesdata$, "Data ", "")
	height1 = Int (linesh$)
	width1 = Int (linesw$)
	totalA = width1 * height1 : picdataBank = CreateBank (totalA * 3)
	For counter = 0 To 100
		progBar(counter) = (totalA / 100) * counter
	Next
	AddGadgetItem listBoxTotal, "Total to Load: " + Str totalA
	AddGadgetItem listBoxTimeH, "00" : AddGadgetItem listBoxTimeM, "00" : AddGadgetItem listBoxTimeS, "00"
	counter = 0 : progBar(101) = totalA * 2 
	While  linesdata$ <> ""
		temp$ = linesdata$
		Repeat
			rgbR = Int(temp$)
			pos = Instr (temp$, " ")
			temp$ = Right$(temp$, Len(temp$) - pos)
			PokeByte picdataBank, count, rgbR 
			count = count + 1

			rgbG = Int(temp$)
			pos = Instr (temp$, " ")
			temp$ = Right$(temp$, Len(temp$) - pos)
			PokeByte picdataBank, count, rgbG
			count = count + 1

			rgbB = Int(temp$)
			pos = Instr (temp$, " ")
			temp$ = Right$(temp$, Len(temp$) - pos)
			PokeByte picdataBank, count, rgbB
			count = count + 1
			progress = progress + 1
			If progBar(counter) < progress And progBar(counter + 1) > progress
				TimeAndProgression()
			EndIf
		Until Len(temp$) = 0
		linesdata$ = ReadLine$(loadingData)
		linesdata$ = Replace$(linesdata$, "Data ", "")
	Wend
	CloseFile(loadingData)
	TimeTaken()
End Function

Function TimeTaken()
	totalsecs = ( MilliSecs() - timeSet ) / 1000
	If totalsecs > 3599
		timeH = totalsecs / 3600
		totalsecs = totalsecs - (timeH * 3600)
		timeM = totalsecs / 60
		totalsecs = totalsecs - (timeM * 60)
		If timeH > 9
			ClearGadgetItems listBoxTimeH
			AddGadgetItem listBoxTimeH, Str timeH
		ElseIf timeH < 10
			ClearGadgetItems listBoxTimeH
			AddGadgetItem listBoxTimeH, "0" + Str timeH
		EndIf
		If timeM > 9
			ClearGadgetItems listBoxTimeM
			AddGadgetItem listBoxTimeM, Str timeM
		ElseIf timeM < 10
			ClearGadgetItems listBoxTimeM
			AddGadgetItem listBoxTimeM, "0" + Str timeM
		EndIf
		If totalsecs > 9
			ClearGadgetItems listBoxTimeS
			AddGadgetItem listBoxTimeS, Str totalsecs
		ElseIf totalsecs < 10
			ClearGadgetItems listBoxTimeS
			AddGadgetItem listBoxTimeS, "0" + Str totalsecs
		EndIf
	ElseIf totalsecs > 59
		timeM = totalsecs / 60
		totalsecs = totalsecs - (timeM * 60)
		If timeM > 9
			ClearGadgetItems listBoxTimeM
			AddGadgetItem listBoxTimeM, Str timeM
		ElseIf timeM < 10
			ClearGadgetItems listBoxTimeM
			AddGadgetItem listBoxTimeM, "0" + Str timeM
		EndIf
		If totalsecs > 9
			ClearGadgetItems listBoxTimeS
			AddGadgetItem listBoxTimeS, Str totalsecs
		ElseIf totalsecs < 10
			ClearGadgetItems listBoxTimeS
			AddGadgetItem listBoxTimeS, "0" + Str totalsecs
		EndIf
	ElseIf totalsecs < 60
		If totalsecs > 9
			ClearGadgetItems listBoxTimeS
			AddGadgetItem listBoxTimeS, Str totalsecs
		ElseIf totalsecs < 10
			ClearGadgetItems listBoxTimeS
			AddGadgetItem listBoxTimeS, "0" + Str totalsecs
		EndIf
	EndIf
End Function

Function TimeAndProgression()
	UpdateProgBar barProcessed, progBar2#(counter)
	counter = counter + 1
End Function

Function CenterWindow (title$, width, height, group, style)
	Return CreateWindow (title$, (ClientWidth (Desktop ()) / 2) - (width / 2), (ClientHeight (Desktop ()) / 2) - (height / 2), width, height, group, style)
End Function

;*******************************************************************************************************************
.freeallgadgets2
If gfxBankDisplay <> 0 Then FreeImage gfxBankDisplay : gfxBankDisplay = 0
If gfxBank3 <> 0 Then FreeImage gfxBank3 : gfxBank3 = 0
If gfxBank4 <> 0 Then FreeImage gfxBank4 : gfxBank4 = 0
If picdataBank <> 0 Then FreeBank picdataBank : picdataBank = 0
FreeGadget buttonLoadPic : FreeGadget buttonLoadData : FreeGadget buttonSavePic : FreeGadget buttonSaveData : FreeGadget buttonClearDisplay
FreeGadget buttonCompData : FreeGadget buttonConvertData : FreeGadget buttonViewPic : FreeGadget buttonStopComp : FreeGadget canvasPicPreview
FreeGadget buttonQuit : FreeGadget buttonDeleteData : FreeGadget buttonDeletePic : FreeGadget progressBarEncrypt1
FreeGadget progressBarEncrypt2 : FreeGadget listBoxFiles : FreeGadget listBoxTotal : FreeGadget listBoxTimeH
FreeGadget listBoxTimeM : FreeGadget listBoxTimeS : FreeGadget buttonStopConv : FreeGadget mainwindow

Return

End

Data 32 ; height of picture
Data 32 ; width of picture
; rgb values for picture - copy and paste into program
Data 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255 
Data 255, 255, 255, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 255, 255, 255, 255, 255, 255, 255, 255, 255, 0, 0, 0, 0, 0, 0, 255, 255, 255, 0, 0, 0, 0, 0, 0, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255 
Data 255, 255, 255, 0, 0, 0, 255, 255, 255, 255, 255, 255, 255, 255, 255, 0, 0, 0, 255, 255, 255, 255, 255, 255, 0, 0, 0, 255, 255, 255, 0, 0, 0, 255, 255, 255, 0, 0, 0, 255, 255, 255, 0, 0, 0, 0, 0, 0, 0, 0, 0, 255, 255, 255, 0, 0, 0, 255, 255, 255, 0, 0, 0, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255 
Data 255, 255, 255, 0, 0, 0, 255, 255, 255, 255, 255, 255, 255, 255, 255, 0, 0, 0, 255, 255, 255, 255, 255, 255, 0, 0, 0, 255, 255, 255, 255, 255, 255, 255, 255, 255, 0, 0, 0, 255, 255, 255, 0, 0, 0, 255, 255, 255, 0, 0, 0, 255, 255, 255, 0, 0, 0, 255, 255, 255, 0, 0, 0, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255 
Data 255, 255, 255, 0, 0, 0, 255, 255, 255, 255, 255, 255, 255, 255, 255, 0, 0, 0, 255, 255, 255, 255, 255, 255, 0, 0, 0, 255, 255, 255, 255, 255, 255, 255, 255, 255, 0, 0, 0, 255, 255, 255, 0, 0, 0, 0, 0, 0, 0, 0, 0, 255, 255, 255, 0, 0, 0, 0, 0, 0, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255 
Data 255, 255, 255, 0, 0, 0, 255, 255, 255, 255, 255, 255, 255, 255, 255, 0, 0, 0, 255, 255, 255, 255, 255, 255, 0, 0, 0, 255, 255, 255, 255, 255, 255, 255, 255, 255, 0, 0, 0, 255, 255, 255, 0, 0, 0, 255, 255, 255, 0, 0, 0, 255, 255, 255, 0, 0, 0, 255, 255, 255, 0, 0, 0, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255 
Data 255, 255, 255, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 255, 255, 255, 0, 0, 0, 255, 255, 255, 0, 0, 0, 255, 255, 255, 255, 255, 255, 255, 255, 255, 0, 0, 0, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255 
Data 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 247, 252, 251, 246, 252, 250, 252, 253, 253, 192, 213, 225, 192, 213, 225, 233, 242, 245, 249, 254, 255, 255, 255, 255 
Data 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 217, 231, 237, 82, 186, 165, 115, 179, 205, 197, 222, 231, 188, 215, 226, 206, 228, 235, 196, 222, 230, 87, 224, 249, 255, 255, 255 
Data 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 233, 242, 245, 229, 238, 243, 231, 239, 243, 162, 190, 205, 169, 202, 216, 199, 222, 237, 116, 122, 252, 215, 234, 247, 220, 240, 245, 212, 234, 239, 221, 239, 243, 232, 248, 251, 255, 255, 255 
Data 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 248, 253, 255, 255, 255, 255, 236, 249, 253, 218, 235, 240, 190, 217, 227, 210, 232, 239, 218, 238, 242, 104, 134, 151, 115, 153, 174, 199, 224, 232, 211, 232, 239, 195, 220, 230, 189, 217, 227, 194, 221, 230, 249, 251, 252, 255, 255, 255, 255, 255, 255 
Data 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 213, 238, 249, 255, 255, 255, 135, 220, 245, 76, 194, 235, 141, 233, 252, 23, 204, 245, 189, 232, 242, 229, 247, 250, 219, 239, 244, 222, 241, 245, 151, 180, 194, 176, 206, 220, 139, 167, 180, 118, 161, 186, 177, 207, 220, 189, 217, 227, 222, 236, 241, 255, 255, 255, 255, 255, 255, 255, 255, 255 
Data 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 228, 246, 252, 255, 255, 255, 168, 231, 248, 46, 186, 233, 40, 186, 233, 54, 209, 246, 23, 203, 244, 25, 208, 248, 22, 197, 242, 20, 195, 240, 87, 199, 234, 196, 222, 230, 172, 204, 218, 134, 175, 197, 148, 182, 198, 122, 150, 163, 132, 156, 167, 200, 222, 229, 226, 240, 244, 246, 250, 251, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255 
Data 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 196, 228, 244, 183, 222, 242, 200, 240, 251, 78, 199, 237, 52, 193, 237, 88, 221, 251, 22, 200, 243, 25, 205, 245, 24, 203, 245, 20, 193, 239, 18, 189, 237, 8, 162, 222, 56, 178, 228, 4, 150, 215, 60, 166, 220, 153, 191, 212, 146, 183, 203, 191, 217, 228, 226, 242, 244, 237, 243, 245, 253, 254, 254, 245, 250, 252, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255 
Data 255, 255, 255, 255, 255, 255, 255, 255, 255, 199, 233, 248, 5, 157, 219, 28, 191, 238, 22, 202, 244, 22, 200, 243, 25, 207, 247, 20, 195, 240, 36, 205, 245, 12, 173, 228, 30, 168, 223, 155, 224, 246, 48, 158, 217, 153, 208, 237, 255, 255, 255, 204, 231, 245, 255, 255, 255, 243, 247, 249, 252, 254, 254, 241, 248, 250, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255 
Data 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 85, 221, 250, 67, 214, 248, 18, 188, 237, 89, 207, 242, 2, 146, 213, 124, 211, 240, 246, 252, 254, 146, 205, 236, 246, 251, 253, 255, 255, 255, 252, 253, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255 
Data 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 246, 253, 255, 237, 249, 253, 215, 243, 251, 255, 255, 255, 225, 241, 249, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 0, 0, 0, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255 
Data 255, 255, 255, 255, 255, 255, 255, 255, 255, 0, 0, 0, 0, 128, 128, 0, 128, 128, 0, 0, 0, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 0, 0, 0, 0, 0, 0, 0, 158, 255, 0, 0, 0, 0, 0, 0, 222, 222, 0, 222, 222, 0, 0, 0, 0, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255 
Data 255, 255, 255, 255, 255, 255, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 255, 255, 255, 255, 255, 255, 0, 0, 0, 255, 255, 255, 255, 255, 255, 222, 222, 0, 0, 0, 0, 0, 158, 255, 0, 158, 255, 0, 158, 255, 0, 158, 255, 0, 158, 255, 0, 158, 255, 0, 0, 0, 94, 64, 191, 222, 222, 0, 222, 222, 0, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 0, 0, 0 
Data 255, 255, 255, 255, 255, 255, 255, 255, 255, 0, 0, 0, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 0, 0, 0, 255, 255, 255, 222, 222, 0, 0, 0, 0, 0, 158, 255, 0, 158, 255, 0, 158, 255, 0, 158, 255, 0, 158, 255, 0, 158, 255, 0, 158, 255, 0, 158, 255, 94, 64, 191, 222, 222, 0, 191, 191, 0, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 0, 0, 0, 0, 0, 0, 158, 94, 0 
Data 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 0, 94, 158, 0, 0, 0, 255, 255, 255, 222, 222, 0, 0, 0, 0, 0, 158, 255, 0, 158, 255, 0, 158, 255, 0, 158, 255, 0, 158, 255, 0, 158, 255, 0, 158, 255, 0, 158, 255, 0, 0, 0, 222, 222, 0, 191, 191, 0, 191, 191, 0, 0, 0, 0, 0, 0, 0, 191, 0, 0, 191, 0, 0, 191, 0, 0, 191, 0, 0, 158, 94, 0, 158, 94, 0 
Data 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 0, 94, 158, 0, 0, 0, 255, 255, 255, 0, 0, 0, 222, 222, 0, 222, 222, 0, 222, 222, 0, 94, 64, 191, 0, 0, 0, 0, 158, 255, 0, 158, 255, 0, 158, 255, 0, 158, 255, 0, 158, 255, 0, 0, 0, 222, 222, 0, 191, 191, 0, 191, 191, 0, 191, 0, 0, 191, 0, 0, 191, 0, 0, 191, 0, 0, 191, 0, 0, 255, 255, 48, 158, 94, 0, 158, 94, 0, 255, 255, 255 
Data 255, 255, 255, 255, 255, 255, 255, 255, 255, 0, 94, 158, 255, 255, 255, 255, 255, 255, 255, 255, 255, 222, 222, 0, 222, 222, 0, 255, 255, 255, 255, 0, 0, 0, 0, 0, 255, 255, 255, 222, 222, 0, 222, 222, 0, 222, 222, 0, 94, 64, 191, 94, 64, 191, 94, 64, 191, 222, 222, 0, 191, 191, 0, 0, 0, 0, 191, 0, 0, 191, 0, 0, 191, 0, 0, 191, 0, 0, 191, 0, 0, 222, 222, 0, 255, 255, 48, 158, 94, 0, 158, 94, 0, 255, 255, 255 
Data 255, 255, 255, 255, 255, 255, 0, 94, 158, 255, 255, 255, 255, 255, 255, 255, 255, 255, 222, 222, 0, 222, 222, 0, 255, 255, 255, 255, 255, 255, 0, 0, 0, 255, 255, 255, 255, 255, 255, 0, 0, 0, 0, 0, 0, 255, 255, 255, 0, 0, 0, 222, 222, 0, 222, 222, 0, 191, 191, 0, 191, 191, 0, 191, 0, 0, 191, 0, 0, 191, 0, 0, 191, 0, 0, 191, 0, 0, 191, 0, 0, 191, 0, 0, 255, 255, 48, 158, 94, 0, 255, 255, 255, 255, 255, 255 
Data 255, 255, 255, 0, 0, 0, 255, 255, 255, 255, 255, 255, 255, 255, 255, 222, 222, 0, 0, 0, 0, 0, 0, 0, 222, 222, 0, 222, 222, 0, 222, 222, 0, 0, 0, 0, 255, 0, 0, 255, 0, 0, 255, 255, 255, 255, 222, 0, 222, 222, 0, 222, 222, 0, 191, 191, 0, 191, 191, 0, 191, 0, 0, 191, 0, 0, 191, 0, 0, 191, 0, 0, 191, 0, 0, 191, 0, 0, 191, 0, 0, 158, 94, 0, 158, 94, 0, 255, 255, 255, 255, 255, 255, 255, 255, 255 
Data 255, 255, 255, 0, 94, 158, 255, 255, 255, 255, 255, 255, 0, 0, 0, 222, 222, 0, 222, 222, 0, 222, 222, 0, 222, 222, 0, 222, 222, 0, 0, 255, 64, 222, 222, 0, 222, 222, 0, 222, 222, 0, 255, 0, 0, 222, 222, 0, 222, 222, 0, 0, 0, 0, 191, 191, 0, 191, 0, 0, 191, 0, 0, 191, 0, 0, 191, 0, 0, 191, 0, 0, 191, 0, 0, 191, 0, 0, 158, 94, 0, 158, 94, 0, 0, 0, 0, 255, 255, 255, 255, 255, 255, 255, 255, 255 
Data 255, 255, 255, 0, 94, 158, 255, 255, 255, 0, 0, 0, 191, 191, 0, 191, 191, 0, 191, 191, 0, 191, 191, 0, 222, 222, 0, 222, 222, 0, 222, 222, 0, 222, 222, 0, 222, 222, 0, 222, 222, 0, 222, 222, 0, 222, 222, 0, 191, 191, 0, 191, 191, 0, 0, 0, 0, 191, 0, 0, 0, 0, 0, 191, 0, 0, 191, 0, 0, 191, 0, 0, 191, 0, 0, 0, 0, 0, 158, 94, 0, 158, 94, 0, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255 
Data 255, 255, 255, 0, 0, 0, 255, 255, 255, 0, 0, 0, 191, 0, 0, 191, 191, 0, 191, 191, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 222, 222, 0, 222, 222, 0, 222, 222, 0, 222, 222, 0, 191, 191, 0, 191, 191, 0, 0, 0, 0, 191, 0, 0, 191, 0, 0, 191, 0, 0, 191, 0, 0, 191, 0, 0, 191, 0, 0, 191, 0, 0, 158, 94, 0, 158, 94, 0, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255 
Data 255, 255, 255, 255, 255, 255, 0, 94, 158, 255, 255, 255, 255, 255, 255, 255, 255, 255, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 191, 191, 0, 191, 191, 0, 191, 191, 0, 0, 0, 0, 191, 191, 0, 191, 191, 0, 191, 0, 0, 191, 0, 0, 0, 0, 0, 191, 0, 0, 191, 0, 0, 191, 0, 0, 191, 0, 0, 191, 0, 0, 191, 0, 0, 0, 0, 0, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255 
Data 255, 255, 255, 255, 255, 255, 255, 255, 255, 0, 0, 0, 0, 94, 158, 0, 0, 0, 255, 255, 255, 255, 255, 255, 255, 255, 255, 191, 0, 0, 0, 0, 0, 191, 191, 0, 191, 191, 0, 191, 191, 0, 191, 191, 0, 0, 0, 0, 191, 0, 0, 191, 0, 0, 191, 0, 0, 191, 0, 0, 191, 0, 0, 191, 0, 0, 191, 0, 0, 191, 0, 0, 0, 0, 0, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255 
Data 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 191, 0, 0, 0, 0, 0, 191, 0, 0, 191, 0, 0, 191, 0, 0, 191, 0, 0, 0, 0, 0, 191, 0, 0, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255 
Data 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 0, 0, 0, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255
