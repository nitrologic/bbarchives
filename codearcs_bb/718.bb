; ID: 718
; Author: mrtricks
; Date: 2003-06-19 09:42:32
; Title: FILE SELECTOR (save/load) for BlitzUI
; Description: With double click, extension filtering, creation of new folders, overwrite warning, illegal character warning... very simple to include

;---------------------------------;
;---FILE SELECTOR by Robin King---;
;-----------19 Jun 2003-----------;
;---for BlitzUI by Chris Fuller---;
;---------------------------------;

;INSTRUCTIONS: (Hope this isn't too confusing!)
;
; Save this as "save_gui.bb"
; 
; In your main program, after the line:
;
;
;
;--------------------------------------------------------
;;;;		Initialise(  )
;--------------------------------------------------------
;
;
;
; ...type the Line:
;
;
;
;--------------------------------------------------------
;;;;		Include "save_gui.bb"
;--------------------------------------------------------
;
;
;
; Next, in the GUI code, after the section:
;
;
;
;--------------------------------------------------------
;;;;		;Event Handling
;;;;		Select app\Event
;;;;			Case EVENT_WINDOW
;;;;				Select app\WindowEvent
;;;;
;;;;					- ALL YOUR WINDOW EVENTS
;;;;
;;;;				End Select
;;;;			Case EVENT_MENU
;;;;				Select app\MenuEvent
;;;;
;;;;					- ALL YOUR MENU EVENTS
;;;;
;;;;				End Select
;;;;			Case EVENT_GADGET
;;;;				Select app\GadgetEvent
;;;;
;;;;					- ALL YOUR GADGET EVENTS
;;;;
;;;;				End Select
;--------------------------------------------------------
;
;
; 
; ... type this line:
;
;
;
;--------------------------------------------------------
;;;;			FILE_SELECTOR_GADGETS
;--------------------------------------------------------
;			
;
;
; NB: It's VERY important that it comes before the final:
;
;
;
;--------------------------------------------------------
;;;;		End Select
;--------------------------------------------------------
;
;
; Then, just AFTER that final End Select and before
; the DrawMouse(  ) Line, Type:
;
;
;
;--------------------------------------------------------
;;;;		SAVE_GUI_OTHER_FUNCTIONS
;--------------------------------------------------------
;
;
;
; These are functions that need to be called every frame,
; such as checking for double-click.
;
;
;
;
; Then: to start the save dialogue running
; (at any point in your program), type:
;
;
;
;--------------------------------------------------------
;;;;		SAVE_DIALOGUE( "All Files (.*);Image Files (.bmp .jpg .tif)*;Blitz Code (.bb)" , "New File" )
;--------------------------------------------------------
;
;
;
; The first parameter is all the extensions you wish to show
; in the file selector - Put the description (if you want), then IN BRACKETS,
; the actual extensions separated by a space. The * after the brackets in the Image
; Files entry in the example above denotes that this filter is the default one.
; Put "" if you wish not to filter at all.
;
; The first extension in a group will be appended to a save name if there is none
; added by the user. (ie if you type in Image1, it will be called Image1.bmp)
;
; And the 2nd parameter is a default Filename you want to save as.
; (Again, put "" if you want it left blank.)
;
;
;
; To start the open dialogue running
; (at any point in your program), type:
;
;
;
;--------------------------------------------------------
;;;;		OPEN_DIALOGUE( "All Files (.*);Image Files (.bmp .jpg .tif)*;Blitz Code (.bb)" )
;--------------------------------------------------------
;
;
;
; The first parameter is all the extensions you wish to show
; in the file selector - Put the description (if you want), then IN BRACKETS
; (only if you've put descriptions),
; the actual extensions separated by a space. Separate the filter groups with a semi colon (;)
; The * After the brackets in the Image
; Files entry in the example above denotes that this filter is the default one.
; Put "" if you wish not to filter at all.
;
;
;
;
;
;
;
;
;
; Lastly, you must include functions in your main program:
; OPEN(f$) and SAVE(f$) - where f$ is the filename -
; where you do your file saving and opening.
;

;-------------------------------------------------------------------------------------------
;-------------------------------------------------------------------------------------------
;-------------------------------------------------------------------------------------------
;-------------------------------------------------------------------------------------------
;-------------------------------------------------------------------------------------------
;-------------------------------------------------------------------------------------------
;-------------------------------------------------------------------------------------------



;create images for buttons and file list
Global imgNew, imgOpen, imgNewFolder, imgUpFolder
MAKE_ICONS



;Main File selector Window
	Global win_sav = Window(100,100,266,316,"Save/Open","0",1,0,0,1)
	Global btn_sav_up = Button(212,8,20,21,"","0",1,0,0)
	sendmessage(btn_sav_up,"BM_SETIMAGE",imgUpFolder)
	Global btn_sav_newfolder = Button(234,8,21,21,"","0",1,0,0)
	sendmessage(btn_sav_newfolder,"BM_SETIMAGE",imgNewFolder)
	Global btn_sav_yes = Button(195,237,60,21,"Save","0",1,0,0)
	Global btn_sav_no = Button(195,262,60,21,"Cancel","0",1,0,0)
	Global lbl_sav_saveas = Label(8,242,"Name:",0)
	Global lst_sav_files = ListBox(8,33,247,200,20,20,10,0)
	Global txt_sav_filename = TextBox(40,237,152,21,0,0,18,10,0)
	Global cbo_sav_foldername = ComboBox(8,8,200,21)	
	Global cbo_sav_filter = ComboBox(8,262,184,21)

;Create new folder Window
	Global win_newfolder = Window(180,150,219,87,"Create New Folder","0",1,0,0,1)
	Global btn_newfolder_yes = Button(148,33,60,21,"Create","0",1,0,0)
	Global btn_newfolder_no = Button(86,33,60,21,"Cancel","0",1,0,0)
	Global txt_newfolder = TextBox(8,8,200,21,0,0,18,10,1)
	SendMessage(txt_newfolder,"TM_SETTEXT",0,"New Folder")

;File already exists. Overwrite? Window
	Global win_overwrite = Window(210,180,204,88,"Warning","0",1,0,0,0)
	Global btn_overwrite_yes = Button(30,34,60,21,"Yes","0",1,0,0)
	Global btn_overwrite_no = Button(110,34,60,21,"No","0",1,0,0)
	l$="That file already exists."+Chr(10)+"Do you wish to overwrite?"
	Global lbl_overwrite = Label(100,4,l$,1)

;Folder already exists window
	Global win_folderalreadyexists = Window(210,180,204,88,"Warning","0",1,0,0,0)
	Global btn_folderalreadyexists_okay = Button(72,34,60,21,"Okay","0",1,0,0)
	l$="That folder already exists."+Chr(10)+"Please choose a different name."
	Global lbl_folderalreadyexists = Label(102,3,l$,1)

;Filename contains illegal characters Window
	Global win_illegalchars = Window(210,180,204,88,"Warning","0",1,0,0,0)
	Global btn_illegalchars_okay = Button(72,34,54,20,"Okay","0",1,0,0)
	l$="The characters \ / * "+Chr$(34)+" ? : | < > are"+Chr(10)+"not allowed and have been edited out."
	Global lbl_illegalchars = Label(102,3,l$,1)



;close all folders
sendmessage(win_sav,"WM_CLOSE")
sendmessage(win_newfolder,"WM_CLOSE")
sendmessage(win_overwrite,"WM_CLOSE")
sendmessage(win_folderalreadyexists,"WM_CLOSE")
sendmessage(win_illegalchars,"WM_CLOSE")

;for folder sorting
Type folderitem
	Field name$
	Field what
End Type

;other save_gui variables
Global doubleclick=300,singleclick,doubleclicked
Global path$

Global filter$,extension$

Global save_open ;1=open, 2=save
Global file_list







;--------------------------------------------------------------------------------------------
;--------------------------------------------------------------------------------------------
Function FILE_SELECTOR_GADGETS()
;--------------------------------------------------------------------------------------------			
;--------------------------------------------------------------------------------------------

	Select app\GadgetEvent

;-------MAIN FILE SELECTOR WINDOW------------------------------------------------------------
				
			;CREATE NEW FOLDER BUTTON
				Case btn_sav_newfolder
				sendmessage(win_newfolder,"WM_OPEN")
				sendmessage(win_newfolder,"WM_SETMODAL")
				If FileType("New Folder")>1
					a=1
					While FileType("New Folder "+Str$(a))>1
						a=a+1
					Wend
					SendMessage(txt_newfolder,"TM_SETTEXT",0,"New Folder "+Str$(a))
				Else
					SendMessage(txt_newfolder,"TM_SETTEXT",0,"New Folder")
				EndIf				
			;GO UP A FOLDER
				Case btn_sav_up
				ft=FileType(sendmessage(txt_sav_filename,"TM_GETTEXT"))
				If MOVE_DIR_UP()=True
					If ft=2 Then sendmessage(txt_sav_filename,"TM_SETTEXT",0,"")
					GET_DIR
				EndIf
			;CHOOSE FOLDER FROM COMBO BOX
				Case cbo_sav_foldername
				i=sendmessage(cbo_sav_foldername,"CM_GETINDEX")+1
				c$=sendmessage(cbo_sav_foldername,"CM_GETCAPTION")
				f$=""
				a=0
				Repeat
					sendmessage(cbo_sav_foldername,"CM_SETINDEX",a)
					cn$=sendmessage(cbo_sav_foldername,"CM_GETCAPTION")
					f$=f$+cn$+"\"
					a=a+1
				Until cn$=c$
				ChangeDir f$
				;	sendmessage(txt_sav_filename,"TM_SETTEXT",0,f$)
				If FileType(sendmessage(txt_sav_filename,"TM_GETTEXT"))=2
					sendmessage(txt_sav_filename,"TM_SETTEXT",0,"")
				EndIf
				GET_DIR
			;CLICK ON A LIST ENTRY
				Case lst_sav_files
				f$=SendMessage(lst_sav_files,"LM_GETCAPTION")
				If f$<>"" Then sendmessage(txt_sav_filename,"TM_SETTEXT",0,f$)
				;are you double clicking, ie selecting?
				If doubleclicked
					doubleclicked=False
					SELECT_ITEM(f$)
				EndIf
			;SAVE/OPEN/OVERWRITE BUTTON
				Case btn_sav_yes
				f$=SendMessage(txt_sav_filename,"TM_GETTEXT")
				If f$<>"" Then SELECT_ITEM(f$)
			;CANCEL SAVE
				Case btn_sav_no
				sendmessage(win_sav,"WM_CLOSE")			
			;PRESS ENTER WHILE TYPING FILENAME
				Case txt_sav_filename
				If KeyHit(28) Or KeyHit(156)
					f$=SendMessage(txt_sav_filename,"TM_GETTEXT")
					If f$<>"" Then SELECT_ITEM(f$)
				EndIf
			;CONFIRM SAVE BUTTON
				Case btn_sav_yes
				f$=SendMessage(txt_sav_filename,"TM_GETTEXT")
				SELECT_ITEM(f$)
			;CHANGE FILTER
				Case cbo_sav_filter
				f$=sendmessage(cbo_sav_filter,"CM_GETCAPTION")
				filter$=EXTRACT_FILTER$(f$)
				extension$=EXTRACT_EXTENSION$(filter$)
				GET_DIR
				
			
				
				
;-------CREATE NEW FOLDER WINDOW------------------------------------------------------------

			;CANCEL CREATE NEW FOLDER
				Case btn_newfolder_no
				sendmessage(win_newfolder,"WM_CLOSE")
			;CONFIRM CREATE NEW FOLDER
				Case btn_newfolder_yes
				CREATE_NEW_FOLDER
			;ALTERNATIVE CONFIRM - PRESS RETURN OR ENTER
				Case txt_newfolder
				If KeyHit(28) Or KeyHit(156) Then CREATE_NEW_FOLDER
				
				
				
;-------ESCAPE FOLDER ALREADY EXISTS WINDOW--------------------------------------------------

			;OKAY
				Case btn_folderalreadyexists_okay
				sendmessage(win_folderalreadyexists,"WM_CLOSE")



;-------ESCAPE ILLEGAL CHARS WINDOW----------------------------------------------------------

			;OKAY
				Case btn_illegalchars_okay
				sendmessage(win_illegalchars,"WM_CLOSE")



;-------OVERWRITE WINDOW---------------------------------------------------------------------

			;NO
				Case btn_overwrite_no
				sendmessage(win_overwrite,"WM_CLOSE")
			;YES
				Case btn_overwrite_yes
				sendmessage(win_overwrite,"WM_CLOSE")
				f$=sendmessage(txt_sav_filename,"TM_GETTEXT")
				sendmessage(win_sav,"WM_CLOSE")
				SAVE(f$) ;perhaps put a backup of original file?

	End Select

End Function






;-------------------------------------------------------------------------------------------------
Function SAVE_GUI_OTHER_FUNCTIONS()
	;doubleclick?
	If MouseHit(1)
		If MilliSecs()<=singleclick+doubleclick
			doubleclicked=True: singleclick=0
		Else
			singleclick=MilliSecs()
		EndIf
	EndIf
	;change confirm button text
	f$=SendMessage(txt_sav_filename,"TM_GETTEXT")
	If FileType(f$)<>2
		;cannot end with .
		While Right$(f$,1)="."
			f$=Left$(f$,Len(f$)-1)
		Wend
		;get current extension
		If Instr(f$,".",1)
			a=Len(f$)
			While Mid$(f$,a,1)<>"."
				a=a-1
			Wend
			ext$=Right$(f$,Len(f$)-(a-1))
		Else
			ext$=""
		EndIf
		If Instr(filter$,ext$,1)=0 Or (ext$="" And f$<>"")
		;If Instr(f$,".",1)=0
			f$=f$+extension$
		EndIf
	EndIf
	Select FileType(f$)
		Case 0
			If save_open=2
				SendMessage(btn_sav_yes,"BM_SETTEXT","Save")
				If f$=""
					SendMessage(btn_sav_yes,"BM_DISABLE")
				Else
					SendMessage(btn_sav_yes,"BM_ENABLE")
				EndIf
			Else
				SendMessage(btn_sav_yes,"BM_SETTEXT","Open")
				SendMessage(btn_sav_yes,"BM_DISABLE")
			EndIf
		Case 1
			If save_open=2
				SendMessage(btn_sav_yes,"BM_SETTEXT","Overwrite")
			Else
				SendMessage(btn_sav_yes,"BM_SETTEXT","Open")
			EndIf
			SendMessage(btn_sav_yes,"BM_ENABLE")
		Case 2
			If save_open=2 Then SendMessage(btn_sav_yes,"BM_SETTEXT","Open")
			SendMessage(btn_sav_yes,"BM_ENABLE")
	End Select
	
End Function



;-------------------------------------------------------------------------------------------------
Function CREATE_NEW_FOLDER()

	f$=SendMessage(txt_newfolder,"TM_GETTEXT")
	;does it already exist?
	If f$<>""
		If FileType(f$)>0
			sendmessage(win_folderalreadyexists,"WM_OPEN")
			sendmessage(win_folderalreadyexists,"WM_SETMODAL")
		Else
			;need to stop certain chars ie *:/\?"<>|
			ff$=ILLEGAL_CHARS(f$)
			If ff$=f$
				;create new folder
				sendmessage(win_newfolder,"WM_CLOSE")
				CreateDir CurrentDir$()+f$
				GET_DIR
			Else
				sendmessage(txt_newfolder,"TM_SETTEXT",0,ff$)
				sendmessage(win_illegalchars,"WM_OPEN")
				sendmessage(win_illegalchars,"WM_SETMODAL")
			EndIf
		EndIf
	EndIf

End Function



;-------------------------------------------------------------------------------------------------
Function ILLEGAL_CHARS$(f$)
	a=0
	Repeat
		a=a+1
		For b=1 To 9
			If Mid$(f$,a,1)=Mid$("*:/\?<>|"+Chr$(34),b,1)
				f$=Left$(f$,a-1)+Right$(f$,Len(f$)-a)
				a=0
				Exit
			EndIf
		Next
	Until a=Len(f$)
	Return f$

End Function



;-------------------------------------------------------------------------------------------------
Function SAVE_DIALOGUE(f$,n$)
	If sendmessage(win_sav,"WM_VISIBLE")=0
		sendmessage(win_sav,"WM_OPEN")
		sendmessage(win_sav,"WM_SETMODAL")
		save_open=2
		SendMessage(btn_sav_yes,"BM_SETTEXT","Save")					
		SendMessage(btn_sav_newfolder,"BM_ENABLE")
		sendmessage(win_sav,"WM_SETCAPTION","Save") ;doesn't yet work - may in future BlitzUI's
		
		;filter box
		sendmessage(cbo_sav_filter,"CM_RESET") ;get rid of all items in combo box
		aa=0: fa=0
		Repeat
			a=Instr(f$,";",1)
			If a>0
				fi$=Left$(f$,a-1)
				f$=Right$(f$,Len(f$)-a)
			Else
				fi$=f$
			EndIf
			If Right$(fi$,1)="*"
				fa=aa
				fi$=Left$(fi$,Len(fi$)-1)
			EndIf
			AddComboBoxItem(cbo_sav_filter,0,fi$)
			aa=aa+1
		Until a=0
		sendmessage(cbo_sav_filter,"CM_SETINDEX",fa)
		f$=sendmessage(cbo_sav_filter,"CM_GETCAPTION")
		filter$=EXTRACT_FILTER$(f$)
		
		;default extension
		extension$=EXTRACT_EXTENSION$(filter$)
		;If Len(n$)>Len(extension$)
		;	If Right$(n$,Len(extension$))<>extension$ Then n$=n$+extension$
		;Else
		;	If n$<>"" Then n$=n$+extension$
		;EndIf
		sendmessage(txt_sav_filename,"TM_SETTEXT",0,n$)

		GET_DIR
		
	EndIf
End Function



;-------------------------------------------------------------------------------------------------
Function OPEN_DIALOGUE(f$)
	If sendmessage(win_sav,"WM_VISIBLE")=0
		sendmessage(win_sav,"WM_OPEN")
		sendmessage(win_sav,"WM_SETMODAL")
		save_open=1
		SendMessage(btn_sav_yes,"BM_SETTEXT","Open")
		SendMessage(btn_sav_yes,"BM_DISABLE")
		SendMessage(btn_sav_newfolder,"BM_DISABLE")
		
		sendmessage(win_sav,"WM_SETCAPTION","Open") ;doesn't yet work - may in future BlitzUI's
		
		;filter box
		sendmessage(cbo_sav_filter,"CM_RESET") ;get rid of all items in combo box
		aa=0: fa=0
		Repeat
			a=Instr(f$,";",1)
			If a>0
				fi$=Left$(f$,a-1)
				f$=Right$(f$,Len(f$)-a)
			Else
				fi$=f$
			EndIf
			If Right$(fi$,1)="*"
				fa=aa
				fi$=Left$(fi$,Len(fi$)-1)
			EndIf
			AddComboBoxItem(cbo_sav_filter,0,fi$)
			aa=aa+1
		Until a=0
		sendmessage(cbo_sav_filter,"CM_SETINDEX",fa)
		f$=sendmessage(cbo_sav_filter,"CM_GETCAPTION")
		filter$=EXTRACT_FILTER$(f$)
		extension$=""
		
		GET_DIR
		
	EndIf
End Function



;-------------------------------------------------------------------------------------------------
Function EMPTY_DIR_LIST()
	For dir.folderitem=Each folderitem
		Delete dir
	Next
End Function



;-------------------------------------------------------------------------------------------------
Function EXTRACT_EXTENSION$(f$)
	If f$=""
		e$=""
	Else
		a=Instr(f$," ",1)
		If a>0
			e$=Left$(f$,a-1)
		Else
			e$=f$
		EndIf
	EndIf
	Return e$
End Function



;-------------------------------------------------------------------------------------------------
Function EXTRACT_FILTER$(f$)
	If f$="All Files" Then f$=""
	a=Instr(f$,"(",1)
	If a>0 Then f$=Right$(f$,Len(f$)-a)
	a=Instr(f$,")",1)
	If a>0 Then f$=Left$(f$,a-1)
	If f$=".*" Then f$=""
	Return f$
End Function



;-------------------------------------------------------------------------------------------------
Function EXTRACT_TOP_DIR$(p$)
	;;take off last \
	;p$=Left$(p$,Len(p$)-1)
	If Instr(p$,"\",1)<Len(p$)
		l=Len(p$)-1
		While Mid$(p$,l,1)<>"\"
			l=l-1
		Wend
		p$=Right$(p$,Len(p$)-l)
	EndIf
	Return p$
End Function



;------------------------------------------------------------------------------------------
Function MOVE_DIR_UP()
	LastSlash=0
	Slash=0
	Repeat
		Slash=Instr(path$,"\",Slash+1)
		If Slash>0 And Slash<Len(path$)
			LastSlash=Slash
		EndIf
	Until Slash=0
	If Left(path$,LastSlash)<>path$
		path$=Left(path$,LastSlash)
	EndIf
	If path$<>CurrentDir$()
		ChangeDir path$
		Return True
	Else
		Return False
	EndIf
End Function



;------------------------------------------------------------------------------------------
Function SELECT_ITEM(f$)
	;cannot end with .
	While Right$(f$,1)="."
		f$=Left$(f$,Len(f$)-1)
	Wend
	;add default extension?
	If FileType(f$)<>2
		;get current extension
		If Instr(f$,".",1)
			a=Len(f$)
			While Mid$(f$,a,1)<>"."
				a=a-1
			Wend
			ext$=Right$(f$,Len(f$)-(a-1))
		Else
			ext$=""
		EndIf
		If Instr(filter$,ext$,1)=0 Or (ext$="" And f$<>"")
		;If Right$(f$,Len(extension$))<>extension$
			f$=f$+extension$
		EndIf
	EndIf
	Select FileType(f$)
	;folder?
	Case 2
		sendmessage(txt_sav_filename,"TM_SETTEXT",0,"")
		path$=CurrentDir$()+f$
		ChangeDir path$
		GET_DIR()
	;file already existing?
	Case 1
		If save_open=2
			sendmessage(win_overwrite,"WM_OPEN")
			sendmessage(win_overwrite,"WM_SETMODAL")
		Else
			sendmessage(win_sav,"WM_CLOSE")
			OPEN(f$)
		EndIf
	;new filename?
	Case 0
		If save_open=2
			;any illegal characters ie *:/\?"<>|
			ff$=ILLEGAL_CHARS(f$)
			If ff$=f$
				While Right$(f$,1)="."
					f$=Left$(f$,Len(f$)-1)
				Wend
				;NOW does it already exist?
				If FileType(f$)<>0
				Else
					;create new file
					sendmessage(win_sav,"WM_CLOSE")
					SAVE(f$)
				EndIf
			Else
				sendmessage(txt_sav_filename,"TM_SETTEXT",0,ff$)
				sendmessage(win_illegalchars,"WM_OPEN")
				sendmessage(win_illegalchars,"WM_SETMODAL")
			EndIf
		EndIf
	End Select
End Function



;------------------------------------------------------------------------------------------
Function GET_DIR()

	;set folder name
	path$=CurrentDir$()
	;If Len(path$)>3 Then folder$=EXTRACT_TOP_DIR$(path$) Else folder$=path$
	folder$=EXTRACT_TOP_DIR$(path$)
	;sendmessage(txt_sav_foldername,"TM_SETTEXT",0,folder$)
	
	;folder combo box
	path_c$=path$
	n=0 ;number of folders
	o=1
	Repeat
		o=Instr(path_c$,"\",o)+1
		If o>1 n=n+1
		If KeyDown(1) End
	Until o=1
	nm=n
	sendmessage(cbo_sav_foldername,"CM_RESET")
	Repeat
		path_t$=EXTRACT_TOP_DIR$(path_c$)
		AddComboBoxItem(cbo_sav_foldername,n,Left$(path_t$,Len(path_t$)-1))
		path_c$=Left$(path_c$,Len(path_c$)-Len(path_t$))
		n=n-1
		If KeyDown(1) End
	Until path_c$=""
	sendmessage(cbo_sav_foldername,"CM_SETINDEX",nm-1)

	
	
	EMPTY_DIR_LIST
	;put files into type
	items=0
	thisdir=ReadDir(path$)
	Repeat
		;f$=Lower$(NextFile$(thisdir))
		f$=NextFile$(thisdir)
		If f$<>"" And f$<>"." And f$<>".."
			;extension filtering
			legal=False
			If filter$="" Or FileType(f$)=2
				legal=True
			Else
				;get the extension of the item
				If Instr(f$,".",1)
					ff=Len(f$)
					While Mid$(f$,ff,1)<>"."
						ff=ff-1
					Wend
					ext$=Right$(f$,Len(f$)-(ff-1))
					If Instr(filter$,ext$,1)>0
						legal=True
					EndIf
				EndIf
			EndIf
			If legal
				dir.folderitem=New folderitem
				dir\name$=f$
				dir\what=FileType(f$)
				items=items+1
			EndIf
		EndIf
	Until f$=""
	CloseDir thisdir
	;SORT ALPHABETICALLY AND INTO FOLDERS / FILES / FILE TYPES
	dir.folderitem=First folderitem
	Repeat
		If dir=Null Then Exit
		a$=Lower$(dir\name$): aa=dir\what
			;put extension before name
			If Instr(a$,".")
				aaa=Len(a$)
				Repeat
					aaa=aaa-1
				Until Mid$(a$,aaa,1)="."
				a$=Right$(a$,Len(a$)-(aaa-1))+Left$(a$,aaa-1)
			EndIf
		dir.folderitem=After dir
		If dir=Null Then Exit
		b$=Lower$(dir\name$): bb=dir\what
			;put extension before name
			If Instr(b$,".")
				bbb=Len(b$)
				Repeat
					bbb=bbb-1
				Until Mid$(b$,bbb,1)="."
				b$=Right$(b$,Len(b$)-(bbb-1))+Left$(b$,bbb-1)
			EndIf
		If (bb=aa And a$>b$) Or bb>aa
			Insert dir Before Before dir
			If dir.folderitem<>First folderitem Then dir=Before dir
		EndIf
	Forever
	;put into GUI
	file_list=0
	sendmessage(lst_sav_files,"LM_RESET") ;get rid of all items in list
	For dir.folderitem=Each folderitem
		If dir\what=2
			AddListBoxItem(lst_sav_files,0,dir\name$,imgOpen)
		Else
			AddListBoxItem(lst_sav_files,0,dir\name$,imgNew)
		EndIf
		file_list=file_list+1
	Next
End Function



;------------------------------------------------------------------------------------------
Function MAKE_ICONS()

Restore NewIcon
imgNew = createimage(16, 16)
for A = 0 to 15
	for B = 0 to 15
		read RGB
		writepixel B, A, RGB, imagebuffer( imgNew )
	next
next
MaskImage imgNew, 255, 0, 0

Restore OpenIcon
imgOpen = CreateImage(16, 16)
For A = 0 To 15
	For B = 0 To 15
		Read RGB
		WritePixel B, A, RGB, ImageBuffer( imgOpen )
	Next
Next
MaskImage imgOpen, 255, 0, 0

Restore NewFolderIcon
imgNewFolder = CreateImage(16, 16)
For A = 0 To 15
	For B = 0 To 15
		Read RGB
		WritePixel B, A, RGB, ImageBuffer( imgNewFolder )
	Next
Next
MaskImage imgNewFolder, 0, 255, 0

Restore UpFolderIcon
imgUpFolder = CreateImage(16, 16)
For A = 0 To 15
	For B = 0 To 15
		Read RGB
		WritePixel B, A, RGB, ImageBuffer( imgUpFolder )
	Next
Next
MaskImage imgUpFolder, 0, 255, 0

End Function



;NEW ICON
;--------
.NewIcon
Data -65536,-65536,-65536,-65536,-65536,-65536,-65536,-65536,-65536,-65536,-65536,-65536,-65536,-65536,-65536,-65536
Data -65536,-65536,-16777216,-16777216,-16777216,-16777216,-16777216,-16777216,-16777216,-16777216,-65536,-65536,-65536,-65536,-65536,-65536
Data -65536,-65536,-16777216,-1,-1,-1,-1,-1,-1,-8355712,-16777216,-65536,-65536,-65536,-65536,-65536
Data -65536,-65536,-16777216,-1,-1,-1,-1,-1,-1,-8355712,-1,-16777216,-65536,-65536,-65536,-65536
Data -65536,-65536,-16777216,-1,-1,-1,-1,-1,-1,-16777216,-16777216,-16777216,-16777216,-65536,-65536,-65536
Data -65536,-65536,-16777216,-1,-1,-1,-1,-1,-1,-1,-1,-1,-16777216,-65536,-65536,-65536
Data -65536,-65536,-16777216,-1,-1,-1,-1,-1,-1,-1,-1,-1,-16777216,-65536,-65536,-65536
Data -65536,-65536,-16777216,-1,-1,-1,-1,-1,-1,-1,-1,-1,-16777216,-65536,-65536,-65536
Data -65536,-65536,-16777216,-1,-1,-1,-1,-1,-1,-1,-1,-1,-16777216,-65536,-65536,-65536
Data -65536,-65536,-16777216,-1,-1,-1,-1,-1,-1,-1,-1,-1,-16777216,-65536,-65536,-65536
Data -65536,-65536,-16777216,-1,-1,-1,-1,-1,-1,-1,-1,-1,-16777216,-65536,-65536,-65536
Data -65536,-65536,-16777216,-1,-1,-1,-1,-1,-1,-1,-1,-1,-16777216,-65536,-65536,-65536
Data -65536,-65536,-16777216,-1,-1,-1,-1,-1,-1,-1,-1,-1,-16777216,-65536,-65536,-65536
Data -65536,-65536,-16777216,-1,-1,-1,-1,-1,-1,-1,-1,-1,-16777216,-65536,-65536,-65536
Data -65536,-65536,-16777216,-16777216,-16777216,-16777216,-16777216,-16777216,-16777216,-16777216,-16777216,-16777216,-16777216,-65536,-65536,-65536
Data -65536,-65536,-65536,-65536,-65536,-65536,-65536,-65536,-65536,-65536,-65536,-65536,-65536,-65536,-65536,-65536


;OPEN ICON
;---------
.OpenIcon
Data -65536,-65536,-65536,-65536,-65536,-65536,-65536,-65536,-65536,-65536,-65536,-65536,-65536,-65536,-65536,-65536
Data -65536,-65536,-65536,-65536,-65536,-65536,-65536,-65536,-65536,-16777216,-16777216,-16777216,-65536,-65536,-65536,-65536
Data -65536,-65536,-65536,-65536,-65536,-65536,-65536,-65536,-16777216,-65536,-65536,-65536,-16777216,-65536,-16777216,-65536
Data -65536,-65536,-65536,-65536,-65536,-65536,-65536,-65536,-65536,-65536,-65536,-65536,-65536,-16777216,-16777216,-65536
Data -65536,-16777216,-16777216,-16777216,-65536,-65536,-65536,-65536,-65536,-65536,-65536,-65536,-16777216,-16777216,-16777216,-65536
Data -16777216,-459584,-459584,-459584,-16777216,-16777216,-16777216,-16777216,-16777216,-16777216,-16777216,-65536,-65536,-65536,-65536,-65536
Data -16777216,-459584,-459584,-459584,-459584,-459584,-459584,-459584,-459584,-459584,-16777216,-65536,-65536,-65536,-65536,-65536
Data -16777216,-459584,-459584,-459584,-459584,-459584,-459584,-459584,-459584,-459584,-16777216,-65536,-65536,-65536,-65536,-65536
Data -16777216,-459584,-459584,-459584,-459584,-16777216,-16777216,-16777216,-16777216,-16777216,-16777216,-16777216,-16777216,-16777216,-16777216,-65536
Data -16777216,-459584,-459584,-459584,-16777216,-7829504,-4145152,-7829504,-4145152,-7829504,-4145152,-7829504,-4145152,-7829504,-16777216,-65536
Data -16777216,-459584,-459584,-16777216,-7829504,-4145152,-7829504,-4145152,-7829504,-4145152,-7829504,-4145152,-7829504,-16777216,-65536,-65536
Data -16777216,-459584,-16777216,-7829504,-4145152,-7829504,-4145152,-7829504,-4145152,-7829504,-4145152,-7829504,-16777216,-65536,-65536,-65536
Data -16777216,-16777216,-7829504,-4145152,-7829504,-4145152,-7829504,-4145152,-7829504,-4145152,-7829504,-16777216,-65536,-65536,-65536,-65536
Data -16777216,-16777216,-16777216,-16777216,-16777216,-16777216,-16777216,-16777216,-16777216,-16777216,-16777216,-65536,-65536,-65536,-65536,-65536
Data -65536,-65536,-65536,-65536,-65536,-65536,-65536,-65536,-65536,-65536,-65536,-65536,-65536,-65536,-65536,-65536
Data -65536,-65536,-65536,-65536,-65536,-65536,-65536,-65536,-65536,-65536,-65536,-65536,-65536,-65536,-65536,-65536

;NEW FOLDER ICON
;---------
.NewFolderIcon
Data -16711936,-16711936,-16711936,-16711936,-16711936,-16711936,-16711936,-16711936,-16711936,-16711936,-16711936,-16711936,-16711936,-16711936,-16711936,-16711936
Data -16711936,-16711936,-16711936,-16711936,-16711936,-16711936,-16711936,-16711936,-16711936,-16711936,-16711936,-16711936,-16711936,-16711936,-16711936,-16711936
Data -16711936,-16711936,-16711936,-16711936,-16711936,-16711936,-16711936,-16711936,-16711936,-16711936,-16711936,-65536,-16711936,-16711936,-16711936,-16711936
Data -16711936,-16711936,-16711936,-16711936,-16711936,-16711936,-16711936,-16711936,-65536,-16711936,-16711936,-65536,-16711936,-16711936,-65536,-16711936
Data -16711936,-16711936,-16711936,-16711936,-16711936,-16711936,-16711936,-16711936,-16711936,-65536,-16711936,-65536,-16711936,-65536,-16711936,-16711936
Data -16711936,-16711936,-16711936,-16711936,-16711936,-16711936,-16711936,-16711936,-16711936,-16711936,-65536,-65536,-65536,-16711936,-16711936,-16711936
Data -16711936,-16711936,-16777216,-16777216,-16777216,-16711936,-16711936,-65536,-65536,-65536,-65536,-65536,-65536,-65536,-65536,-65536
Data -16711936,-16777216,-459584,-459584,-459584,-16777216,-16777216,-16777216,-16777216,-16777216,-65536,-65536,-65536,-16711936,-16711936,-16711936
Data -16711936,-16777216,-459584,-459584,-459584,-459584,-459584,-459584,-459584,-65536,-459584,-65536,-16711936,-65536,-16711936,-16711936
Data -16711936,-16777216,-459584,-459584,-459584,-459584,-459584,-459584,-65536,-459584,-459584,-65536,-16711936,-16711936,-65536,-16711936
Data -16711936,-16777216,-459584,-459584,-459584,-459584,-16777216,-16777216,-16777216,-16777216,-16777216,-65536,-16777216,-16777216,-16777216,-16777216
Data -16711936,-16777216,-459584,-459584,-459584,-16777216,-7829504,-4145152,-7829504,-4145152,-7829504,-4145152,-7829504,-4145152,-7829504,-16777216
Data -16711936,-16777216,-459584,-459584,-16777216,-7829504,-4145152,-7829504,-4145152,-7829504,-4145152,-7829504,-4145152,-7829504,-16777216,-16711936
Data -16711936,-16777216,-459584,-16777216,-7829504,-4145152,-7829504,-4145152,-7829504,-4145152,-7829504,-4145152,-7829504,-16777216,-16711936,-16711936
Data -16711936,-16777216,-16777216,-7829504,-4145152,-7829504,-4145152,-7829504,-4145152,-7829504,-4145152,-7829504,-16777216,-16711936,-16711936,-16711936
Data -16711936,-16777216,-16777216,-16777216,-16777216,-16777216,-16777216,-16777216,-16777216,-16777216,-16777216,-16777216,-16711936,-16711936,-16711936,-16711936


;UP FOLDER ICON
;---------
.UpFolderIcon
Data -16711936,-16711936,-16711936,-16711936,-16711936,-16711936,-16711936,-16711936,-16711936,-16711936,-16777216,-16777216,-16711936,-16711936,-16711936,-16711936
Data -16711936,-16711936,-16711936,-16711936,-16711936,-16711936,-16711936,-16711936,-16711936,-16777216,-65281,-65281,-16777216,-16711936,-16711936,-16711936
Data -16711936,-16711936,-16711936,-16711936,-16711936,-16711936,-16711936,-16711936,-16777216,-65281,-65281,-65281,-65281,-16777216,-16711936,-16711936
Data -16711936,-16711936,-16711936,-16711936,-16711936,-16711936,-16711936,-16777216,-16777216,-16777216,-65281,-65281,-16777216,-16777216,-16777216,-16711936
Data -16711936,-16711936,-16711936,-16711936,-16711936,-16711936,-16711936,-16711936,-16711936,-16777216,-65281,-65281,-16777216,-16711936,-16711936,-16711936
Data -16711936,-16711936,-16711936,-16711936,-16711936,-16711936,-16711936,-16711936,-16711936,-16777216,-65281,-65281,-16777216,-16711936,-16711936,-16711936
Data -16711936,-16711936,-16777216,-16777216,-16777216,-16711936,-16711936,-16711936,-16711936,-16777216,-65281,-65281,-16777216,-16711936,-16711936,-16711936
Data -16711936,-16777216,-459584,-459584,-459584,-16777216,-16777216,-16777216,-16777216,-16777216,-65281,-65281,-16777216,-16711936,-16711936,-16711936
Data -16711936,-16777216,-459584,-459584,-459584,-459584,-459584,-459584,-459584,-16777216,-65281,-65281,-16777216,-16711936,-16711936,-16711936
Data -16711936,-16777216,-459584,-459584,-459584,-459584,-459584,-459584,-459584,-16777216,-65281,-65281,-16777216,-16711936,-16711936,-16711936
Data -16711936,-16777216,-459584,-459584,-459584,-459584,-16777216,-16777216,-16777216,-16777216,-65281,-65281,-16777216,-16777216,-16777216,-16777216
Data -16711936,-16777216,-459584,-459584,-459584,-16777216,-7829504,-4145152,-7829504,-16777216,-65281,-65281,-16777216,-4145152,-7829504,-16777216
Data -16711936,-16777216,-459584,-459584,-16777216,-7829504,-4145152,-7829504,-4145152,-16777216,-65281,-65281,-16777216,-7829504,-16777216,-16711936
Data -16711936,-16777216,-459584,-16777216,-7829504,-4145152,-7829504,-4145152,-7829504,-16777216,-16777216,-16777216,-16777216,-16777216,-16711936,-16711936
Data -16711936,-16777216,-16777216,-7829504,-4145152,-7829504,-4145152,-7829504,-4145152,-7829504,-4145152,-7829504,-16777216,-16711936,-16711936,-16711936
Data -16711936,-16777216,-16777216,-16777216,-16777216,-16777216,-16777216,-16777216,-16777216,-16777216,-16777216,-16777216,-16711936,-16711936,-16711936,-16711936


;------------------------------------------------------------------------------------
;------------------------------------------------------------------------------------
;------------------------------------------------------------------------------------
;------------------------------------------------------------------------------------
;------------------------------------------------------------------------------------
;------------------------------------------------------------------------------------
;------------------------------------------------------------------------------------
