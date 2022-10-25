; ID: 2861
; Author: AdamRedwoods
; Date: 2011-06-20 03:14:19
; Title: Image sheet and strip maker
; Description: Image sheet maker

SuperStrict

''image strip maker
'' copyright 2011 Adam Piette
'' You may not sell this program or code for profit, but you may use the files it makes for commercial uses. Makes sense. 
'
'makes image strips For animated textures
'assumes sheet and strip images are all same sizes of first image loaded
'only does PNG so far

Framework MaxGUI.Win32MaxGUIEx
Import BRL.pngloader
Import BRL.jpgloader
Import BRL.tgaloader
Import BRL.bmploader
Import BRL.Event
Import BRL.EventQueue
Import "multifilerequestor.bmx"

Global window:TGadget =CreateWindow("Image Strip Maker",100,40,700,500)

Global panel:TGadget = CreatePanel(10,10,690,150,window,0,"")
Global loadbtn:TGadget  = CreateButton("Load",10,10,100,30,panel,BUTTON_PUSH)
Global filelabel:TGadget  = CreateLabel(".",130,10,520,30,panel,LABEL_LEFT|LABEL_SUNKENFRAME)
CreateLabel("Save File: ",10,50,100,30,panel,LABEL_RIGHT)
Global file2label:TGadget = CreateLabel(".",130,50,520,30,panel,LABEL_LEFT|LABEL_SUNKENFRAME)

Global combobox:TGadget = CreateComboBox(10,90,200,25,panel)
AddGadgetItem combobox, "Make Image Strip", True
AddGadgetItem combobox, "Make Image Sheet", False



Global runbtn:TGadget  = CreateButton("RUN",10,120,100,30,panel,BUTTON_PUSH)

Global imagepanel:TGadget = CreatePanel(10,170,690,200,window,PANEL_BORDER,"")

Global files:String[] = New String[101]
Global currDir:String = ""
Global outfile:String = ""
Global ext:String=""

Global imageFinal:TPixmap

SetGadgetLayout( panel,EDGE_ALIGNED,0,EDGE_ALIGNED,0 )
SetGadgetLayout( imagepanel,EDGE_ALIGNED,0,EDGE_ALIGNED,0 )

Repeat
	Select WaitEvent()
		Case EVENT_WINDOWCLOSE, EVENT_APPTERMINATE
			End
		Case EVENT_GADGETACTION
			''
			DoGadget(TGadget(EventSource()), EventData() )
	EndSelect
	
Forever

End

Function DoGadget(tg:TGadget, edata:Int)
	If tg=loadbtn
		files = RequestMultiFile( "Open Files", "jpg,png,bmp,tga")
		If (files.length>2)
			Print
			Print "dir:"+files[0]
			currDir = files[0]
			SetGadgetText( filelabel, (StripDir(files[1])+"..."+StripDir(files[files.length-1])))
			
			outfile = files[1]
			ext:String = "."+ExtractExt(outfile).toLower()
			outfile = StripExt(outfile)
			For Local i:Int = outfile.length-1 To 1 Step -1
				If outfile[i] >=48 And outfile[i] <=57
					outfile = outfile[..i]
				Else
					Exit
				EndIf
			Next
			outfile :+ ext
			SetGadgetText( file2label, outfile)
			outfile = currDir+outfile
		EndIf
	EndIf
	If tg=runbtn
		Local action:Int = SelectedGadgetItem(combobox)
		If files.length>2 And outfile<>""
			Local err:Int

			If action = 0 Then  Print "image strip" ; err = MakeImageStrip()
			If action = 1 Then Print "image sheet" ; err = MakeImageSheet()
			If err =-1 Then Notify("First Image load failed.")
			If err =-2 Then Notify("Multi Image load failed.")
			If Not err
				Print "done."
				SetGadgetPixmap( imagepanel, imageFinal, PANELPIXMAP_FIT )
				
				If ext = ".png"
					SavePixmapPNG(imageFinal,outfile,7)
				EndIf
				Print"Saved."
			EndIf
		EndIf
	EndIf
EndFunction

Function MakeImageStrip:Int()
	Local firstpix:TPixmap = LoadPixmap(currDir+files[1])
	
	If Not firstpix Then Return -1
	
	Local w:Int = firstpix.width
	Local h:Int = firstpix.height
	Local p:Int = firstpix.pitch
	Local total:Int = files.length-1
	Print "total:"+total+" width:"+w+" p:"+p
	Local newpix:TPixmap = CreatePixmap(w*total,h,PF_RGBA8888)
	Local srcpix:TPixmap
	Local destptr:Byte Ptr, srcptr:Byte Ptr
	
	For Local j:Int = 0 To total-1
		srcpix = LoadPixmap(currDir+files[j+1])
		Print currDir+files[j+1]
		If Not srcpix Then Return -2
		srcpix = ConvertPixmap( srcpix,PF_RGBA8888)
		srcptr = srcpix.pixelptr(0,0)
		
		For Local y:Int = 0 To h-1
			destptr = newpix.pixelptr(w*j,y)
			srcptr = srcpix.pixelptr(0,y) 
			'MemCopy (destptr, srcptr, w*3)
			For Local x:Int = 0 To w*4 ''use width since we're reading from source, pasting to dest
				destptr[x] = srcptr[x]
			Next
			
			
		Next
	Next
	
	imageFinal = newpix

EndFunction

Function MakeImageSheet:Int()
	Local firstpix:TPixmap = LoadPixmap(currDir+files[1])
	
	If Not firstpix Then Return -1
	
	Local w:Int = firstpix.width
	Local h:Int = firstpix.height
	Local p:Int = firstpix.pitch
	Local total:Int = files.length-1
	
	Local sheetsize:Int = Ceil(Sqr(Float(total) ))
	
	Print "total:"+total+" width:"+w+" p:"+p+" sqsize:"+sheetsize
	Local newpix:TPixmap = CreatePixmap(sheetsize*w,sheetsize*h,PF_RGBA8888)
	Local srcpix:TPixmap
	Local destptr:Byte Ptr, srcptr:Byte Ptr
	
	Local offx:Int=0, offy:Int=0
	
	For Local j:Int = 0 To total-1
		srcpix = LoadPixmap(currDir+files[j+1])
		Print currDir+files[j+1]
		If Not srcpix Then Return -2
		srcpix = ConvertPixmap( srcpix,PF_RGBA8888)
		srcptr = srcpix.pixelptr(0,0)
		
		For Local y:Int = 0 To h-1
			destptr = newpix.pixelptr(w*offx,y+h*offy)
			srcptr = srcpix.pixelptr(0,y) 
			'MemCopy (destptr, srcptr, w*3)
			For Local x:Int = 0 To w*4 ''use width since we're reading from source, pasting to dest
				destptr[x] = srcptr[x]
			Next
			
			
		Next
		
		offx=offx+1
		If offx>sheetsize-1
			offy=offy+1
			offx=0
		EndIf
	Next
	
	imageFinal = newpix

EndFunction
