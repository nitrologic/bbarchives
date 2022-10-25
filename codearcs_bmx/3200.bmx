; ID: 3200
; Author: cps
; Date: 2015-04-07 15:40:10
; Title: Masscopy (Mac)
; Description: Copy multiple files to one folder

Import MaxGui.Drivers
SuperStrict
Const MaxFile%=2000' largest number of files that can be processed in one pass
Const Lz$="/"' note  \ for windows build ------------- NB !!!!!!!!!!
Local Exten$=""' file extension of files searched for
Local Prefix$=""' prefix for written files
Local StNum$=""'start number as string of file sequence to be written
Local DesFold$=""' destination folder for saved files
Local Status%=0' Initial state for option response
' 0= initial settings pass on screen 1, 1= edit settings phase screen 1, 2=file select phase screen 2
'3=copy file screen 3, 4=do the copying
Local Tst1$=""
Local Temp1%=0 ; Local Temp2%=0'; Local Temp3%=0; Local Temp4%=0; Local Temp5%=0

Local FList:VarStore = New VarStore' generates folder index and file name storage
FList.Initilise' clears all vars to zero




'Local ScreenSize%=0
'Local GUIFont:TGuiFont


Temp1=DesktopWidth()
If Temp1<800 Then' check to see if screen size allows 800 by 600
	Notify "Sorry screen sizes less than 800 by 600 "+Chr$(13)+"are not supported." 
	End' no support for less than 800 * 600
End If


'smaller fonts for PC larger for Mac
'GUIFont=LoadGuiFont( "Ariel",15) GUIFont=LoadGuiFont( "Ariel",10.8)

Local W2:TGadget = CreateWindow( "Mass Copy",10,10,798,598,,WINDOW_CENTER | WINDOW_TITLEBAR)

Local W2P1:TGadget= CreatePanel(5,5,250,150,W2,PANEL_RAISED)
Tst1="What type of file do you wish to copy"+Chr$(13)
Tst1=Tst1+"( A combination of 3 letters/numbers. )"
Local W2P1L1:Tgadget=CreateLabel(Tst1,5,5,240,35,W2P1,LABEL_CENTER)
Local W2P1Rad1:TGadget=CreateButton("From List",20,45,80,25, W2P1,BUTTON_RADIO)
Local W2P1Rad2:TGadget=CreateButton("Other",20,75,80,25, W2P1,BUTTON_RADIO)
Local W2P1Tf1:Tgadget=CreateTextField(118,75,90,25,W2P1)
Local W2P1L2:Tgadget=CreateLabel("*.",95,75,20,25,W2P1,LABEL_RIGHT)
SetButtonState(W2P1Rad1,True); DisableGadget(W2P1Tf1)
Local W2P1Cb1:TGadget=CreateComboBox(120,45,90,100,W2P1)
AddGadgetItem W2P1Cb1,"*.mp3"; AddGadgetItem W2P1Cb1,"*.wav"; AddGadgetItem W2P1Cb1,"*.jpg"
AddGadgetItem W2P1Cb1,"*.bmp"; AddGadgetItem W2P1Cb1,"*.gif"
Local W2P1But1:Tgadget=CreateButton("Continue",80,118,100,25,W2P1)
SelectGadgetItem(W2P1Cb1,0)
SetPanelColor(W2P1,255,255,200)


Local W2P2:TGadget= CreatePanel(261,5,276,150,W2,PANEL_RAISED)
Tst1="Select a prefix for the files you create" + Chr$(13) + "( Max Length = 12 )"
Local W2P2L1:Tgadget=CreateLabel(Tst1,5,5,256,35,W2P2,LABEL_CENTER)
Local W2P2Rad1:TGadget=CreateButton("From List",20,45,80,25, W2P2,BUTTON_RADIO)
Local W2P2Rad2:TGadget=CreateButton("Other",20,75,80,25, W2P2,BUTTON_RADIO)
Local W2P2Tf1:Tgadget=CreateTextField(116,75,135,25,W2P2)
Local W2P2L2:Tgadget=CreateLabel("-",253,77,10,25,W2P2,LABEL_LEFT)
SetButtonState(W2P2Rad1,True); DisableGadget(W2P2Tf1)
Local W2P2Cb1:TGadget=CreateComboBox(120,45,130,100,W2P2)
AddGadgetItem W2P2Cb1,"my-"; AddGadgetItem W2P2Cb1,"trk-"; AddGadgetItem W2P2Cb1,"mus-"
AddGadgetItem W2P2Cb1,"pic-"; AddGadgetItem W2P2Cb1,"img-"
Local W2P2But1:Tgadget=CreateButton("Continue",80,118,100,25,W2P2)
SelectGadgetItem(W2P2Cb1,1)
DisableGadget(W2P2)

Local W2P3:TGadget= CreatePanel(542,5,250,150,W2,PANEL_RAISED)
Tst1="Enter a start number for your new files" + Chr$(13) + "1 to 3000"
Local W2P3L1:Tgadget=CreateLabel(Tst1,5,5,240,35,W2P3,LABEL_CENTER)
Local W2P3Tf1:TGadget =CreateTextField(80,45,90,25,W2P3)
SetGadgetText(W2P3Tf1,"1")
Local W2P3But1:Tgadget=CreateButton("Continue",80,118,100,25,W2P3)
DisableGadget(W2P3)

'ChangeDir(lz+Lz+Lz)' ////to root
ChangeDir(Lz+Lz+Lz+"users")
'ChangeDir(".."+Lz+Lz)'up a level
'ChangeDir(".."+Lz+Lz+Lz)

Local W2P4:TGadget= CreatePanel(5,160,532,410,W2,PANEL_RAISED)
Local W2P4L1:Tgadget=CreateLabel("Select a folder to place your copies in.",5,4,240,20,W2P4,LABEL_CENTER)

Local W2P4But1:TGadget=CreateButton("Up A Level",5,23,250,30,W2P4,LABEL_CENTER)
Local W2P4Lb1:TGadget=CreateListBox(5,56,250,279,W2P4)
Folderview(CurrentDir(),W2P4Lb1,W2P4But1)

Local W2P4L2:Tgadget=CreateLabel("Files in selected folder.",274,30,250,20,W2P4,LABEL_CENTER)
Local W2P4Lb2:TGadget=CreateListBox(274,56,250,279,W2P4)
FileView(CurrentDir(),W2P4Lb2)
Local W2P4L3:Tgadget=CreateLabel("The files you create will be stored in :",5,342,400,18,W2P4)
Local W2P4Lb4:Tgadget=CreateListBox(5,365,520,35,W2P4)
DisableGadget(W2P4)

Local W2P5:TGadget= CreatePanel(542,160,250,410,W2,PANEL_RAISED)
Local W2P5L1:Tgadget=CreateLabel("Your Choices",5,4,240,18,W2P5,LABEL_CENTER)
Local W2P5L2:Tgadget=CreateLabel("Search pattern for files to be copied.",5,30,240,18,W2P5,LABEL_CENTER)
Local W2P5But1:Tgadget=CreateButton("Edit",10,51,80,28,W2P5)
Local W2P5L3:Tgadget=CreateLabel("Not Set",95,55,145,18,W2P5,LABEL_CENTER)

Local W2P5L5:Tgadget=CreateLabel("Prefix for the files to be created.",5,90,240,18,W2P5,LABEL_CENTER)
Local W2P5But2:Tgadget=CreateButton("Edit",10,111,80,28,W2P5)
Local W2P5L6:Tgadget=CreateLabel("Not Set",95,115,145,18,W2P5,LABEL_CENTER)

Local W2P5L7:Tgadget=CreateLabel("Start number for created files.",5,150,240,18,W2P5,LABEL_CENTER)
Local W2P5But3:Tgadget=CreateButton("Edit",10,171,80,28,W2P5)
Local W2P5L8:Tgadget=CreateLabel("Not Set",95,175,145,18,W2P5,LABEL_CENTER)
Tst1="Continue With"+Chr$(13)+"Current Settings."
Local W2P5But4:Tgadget=CreateButton(Tst1,65,245,120,60,W2P5)

DisableGadget(W2P5But1); DisableGadget(W2P5But2); DisableGadget(W2P5But3); DisableGadget(W2P5But4)
Local W2P5But5:Tgadget=CreateButton("Exit",10,375,100,30,W2P5)
Local W2P5But6:Tgadget=CreateButton("Help",140,375,100,30,W2P5)

Local W2P6:TGadget= CreatePanel(5,5,788,566,W2,PANEL_RAISED)' help panel
Local W2P6Tf1:TGadget = CreateTextArea(129,20,530,490,W2P6,TEXTAREA_READONLY)

Local W2P6But1:Tgadget=CreateButton("Return",344,530,100,30,W2P6)
SetPanelColor(W2P6,200,255,200)
HideGadget(W2P6)


Local W2P7:TGadget= CreatePanel(5,5,788,330,W2,PANEL_RAISED)
SetPanelColor(W2P7,255,255,200)

Local W2P7But1:TGadget=CreateButton("Folders - Up A Level",5,3,245,30,W2P7,LABEL_CENTER)
Local W2P7Lb1:TGadget=CreateListBox(5,35,245,230,W2P7)
Local W2P7L1:Tgadget=CreateLabel("Files in opened folder.",270,8,245,20,W2P7,LABEL_CENTER)
Local W2P7Lb2:TGadget=CreateListBox(270,35,245,230,W2P7)
Local W2P7L2:Tgadget=CreateLabel("0 Files selected for copy list",535,8,245,20,W2P7,LABEL_CENTER)
Local W2P7Lb3:TGadget=CreateListBox(535,35,245,190,W2P7)
Local W2P7But5:Tgadget=CreateButton("Remove File From Copy list",555,230,200,25,W2P7)
Local W2P7But2:Tgadget=CreateButton("Select All",535,266,100,25,W2P7)
Local W2P7But3:Tgadget=CreateButton("Clear All",670,266,100,25,W2P7)
Local W2P7But4:Tgadget=CreateButton("Add Files To Copy List",555,301,200,25,W2P7)
DisableGadget(W2P7But5)
Local W2P7L3:Tgadget=CreateLabel("You are viewing files in the folder :",5,270,400,18,W2P7)
Local W2P7Lb4:Tgadget=CreateListBox(5,290,510,34,W2P7)
HideGadget(W2P7)

Local W2P8:TGadget= CreatePanel(5,339,788,233,W2,PANEL_RAISED)
SetPanelColor(W2P8,255,255,200)
Tst1="Click on a folder shown below to see the files you have selected for copying."
Local W2P8L1:Tgadget=CreateLabel(Tst1,5,2,510,18,W2P8)
Local W2P8Lb1:TGadget=CreateListBox(5,25,510,200,W2P8)
Local W2P8L2:Tgadget=CreateLabel("File Count = 0",590,23,125,18,W2P8,LABEL_CENTER)
SetGadgetColor(W2P8L2,200,255,200)
Tst1="Ready To Copy"+Chr$(13)+"The Files."
Local W2P8But3:Tgadget=CreateButton(Tst1,578,84,150,60,W2P8)
Local W2P8But1:Tgadget=CreateButton("Exit",540,190,100,30,W2P8)
Local W2P8But2:Tgadget=CreateButton("Help",665,190,100,30,W2P8)
HideGadget(W2P8)


Local W2P9:TGadget= CreatePanel(5,5,788,567,W2,PANEL_RAISED)'help screen
Local W2P9L1:Tgadget=CreateLabel("Files selected For copying",5,2,778,18,W2P9,LABEL_CENTER)
Local W2P9Lb2:TGadget=CreateListBox(5,25,776,55,W2P9)
Local W2P9Lb1:TGadget=CreateListBox(60,88,656,442,W2P9)
Local W2P9But1:Tgadget=CreateButton("Return",339,535,100,30,W2P9)
HideGadget(W2P9)

Local W2P10:TGadget= CreatePanel(5,5,788,260,W2,PANEL_RAISED)
SetPanelColor(W2P10,255,255,200)
Local W2P10L1:Tgadget=CreateLabel("Options",294,2,200,18,W2P10,LABEL_CENTER)
Tst1="If options are not avaliable then these files must allready exsist in the destination folder."+Chr$(13)
Tst1=Tst1+"Delete the files then click 'Check Folder' to enable the options."
Local W2P10L2:Tgadget=CreateLabel(Tst1,50,25,688,36,W2P10,LABEL_CENTER)
Tst1="Create file 'alist.txt', a text file containing the old/new"
Tst1=tst1+" file names of the files copied."
Local W2P10Chk1:TGadget=CreateButton(Tst1,50,80,688,30,W2P10,BUTTON_CHECKBOX)
Tst1="Create file 'acdt.txt', a comma deliniated text file listing the old/new"
Tst1=tst1+" file names suitable for a spreadsheet etc."
Local W2P10Chk2:TGadget=CreateButton(Tst1,50,120,688,30,W2P10,BUTTON_CHECKBOX)
Tst1="Create file 'anerror.txt', a text file listing all the failed attemps to copy"
Tst1=tst1+" files, or an empty file if no errors."
Local W2P10Chk3:TGadget=CreateButton(Tst1,50,160,688,30,W2P10,BUTTON_CHECKBOX)
Local W2P10But1:Tgadget=CreateButton("Check Folder",294,220,200,30,W2P10)
HideGadget(W2P10)

Local W2P11:TGadget= CreatePanel(5,270,788,300,W2,PANEL_RAISED)
SetPanelColor(W2P11,255,255,200)
Local W2P11L1:Tgadget=CreateLabel("",5,5,400,18,W2P11)' number of files to be created
Local W2P11Lb1:Tgadget=CreateListBox(5,30,776,35,W2P11)
Local W2P11L2:Tgadget=CreateLabel("The first file name generated will be :",5,75,400,18,W2P11)
Local W2P11L3:Tgadget=CreateLabel("",5,100,776,18,W2P11)' file name generated
SetGadgetColor(W2P11l3,255,255,255)
Local W2P11L4:Tgadget=CreateLabel("",269,140,240,18,W2P11,LABEL_CENTER)' number of files to copy
Local W2P11Pb1:TGadget=CreateProgBar(289,170,200,20,W2P11)
Local W2P11But1:Tgadget=CreateButton("Copy Selected Files",289,200,200,30,W2P11)
Local W2P11But2:Tgadget=CreateButton("Exit",5,265,100,30,W2P11)
Local W2P11But3:Tgadget=CreateButton("Return To Select Files",289,265,200,30,W2P11)
Local W2P11But4:Tgadget=CreateButton("Help",682,265,100,30,W2P11)
HideGadget(W2P11)

Local W2P12:TGadget= CreatePanel(105,150,588,150,W2,PANEL_RAISED)' final screen
SetPanelColor(W2P12,200,255,200)
Local W2P12L1:Tgadget=CreateLabel("",44,10,500,18,W2P12,LABEL_CENTER)' final screen number of files created
Local W2P12Lb1:Tgadget=CreateListBox(44,35,500,35,W2P12)' list box to show were the files have ben put
Local W2P12L2:Tgadget=CreateLabel("Enjoy Your Day...",44,80,500,18,W2P12,LABEL_CENTER)
Local W2P12But1:Tgadget=CreateButton("Exit",246,105,100,30,W2P12)

HideGadget(W2P12)

'------------------------ Start of main loop-------------------------
Repeat
	'Delay 10	
	PollEvent
	Select EventID()
  		Case EVENT_WINDOWCLOSE
  		   EndIt()
		
		Case EVENT_GADGETSELECT
			Select EventSource()				
				Case W2P4Lb1' folder select for destination folder
					Temp1=EventData()
					If Temp1<>-1 Then 'something selected
						Tst1=GadgetItemText(W2P4Lb1,SelectedGadgetItem(W2P4Lb1))
						ChangeDir(FullPath(CurrentDir(),Tst1))
						FolderView(CurrentDir(),W2P4Lb1,W2P4But1)
						FileView(CurrentDir(),W2P4Lb2)						
						ClearGadgetItems(W2P4Lb4)
						AddGadgetItem(W2P4Lb4,CurrentDir())											
					End If			
			
				Case  W2P7Lb1' folder select during select files for copying
					Temp1=EventData()
					If Temp1<>-1 Then 'something selected
						Tst1=GadgetItemText(W2P7Lb1,SelectedGadgetItem(W2P7Lb1))							
						ChangeDir(FullPath(CurrentDir(),Tst1))
						ClearGadgetItems(W2P7Lb3)
						DisableGadget(W2P7But5)
						FolderView(CurrentDir(),W2P7Lb1,W2P7But1)						
						FileView2(CurrentDir(),W2P7Lb2,Exten)
						ClearGadgetItems(W2P7Lb4)
						AddGadgetItem(W2P7Lb4,CurrentDir())
						SetGadgetText(W2P7L2,"0 Files selected for copy list")																																	
					End If
					
				Case W2P7Lb2' file select during select files for copying
					Temp1=EventData()				
					If Temp1<>-1 Then 'something selected
						Tst1=GadgetItemText(W2P7Lb2,SelectedGadgetItem(W2P7Lb2))
						SelFile(Tst1,W2P7Lb3)' put file in list if not repeated
						SetGadgetText(W2P7L2,String(CountGadgetItems(W2P7Lb3))+" Files selected for copy list")		
					End If	
				
				Case W2P8Lb1' show files in copy list for selected folder
					Temp1=EventData()
					If Temp1<>-1 Then 'something selected
						Tst1=GadgetItemText(W2P8Lb1,SelectedGadgetItem(W2P8Lb1))
						HideGadget(W2P7); HideGadget(W2P8)
						ClearGadgetItems(W2P9Lb2)
						AddGadgetItem(W2P9Lb2,"From Folder : "+ Tst1)
						AddGadgetItem(W2P9Lb2,"To Folder : "+ DesFold)												
						FList.ShowCFiles(W2P9Lb1,Temp1)
						ShowGadget(W2P9)
					End If
					
				Case W2P7Lb3' select a file for removal from file copy list
					Temp1=EventData()
					If Temp1<>-1 Then 'something selected
						EnableGadget(W2P7But5)' enables delete button
					End If			
			End Select		
		
		Case EVENT_GADGETACTION
			Select EventSource()
				Case W2P1Rad1'File extension choice 
					SetGadgetText(W2P1Tf1,""); DisableGadget(W2P1Tf1); EnableGadget(W2P1Cb1)
				
				Case W2P1Rad2' file extension other
					DisableGadget(W2P1Cb1); EnableGadget(W2P1Tf1); ActivateGadget(W2P1Tf1)	
				
				Case W2P1Tf1' extension input
					SetGadgetText(W2P1Tf1,CheckNameLength$(3,GadgetText(W2P1Tf1)))
					
				Case W2P2Rad1' prefix choice
					SetGadgetText(W2P2Tf1,""); DisableGadget(W2P2Tf1); EnableGadget(W2P2Cb1)
				
				Case W2P2Rad2'prefix other
					DisableGadget(W2P2Cb1); EnableGadget(W2P2Tf1); ActivateGadget(W2P2Tf1)	
				
				Case W2P2Tf1' prefix input 
					SetGadgetText(W2P2Tf1,CheckNameLength$(12,GadgetText(W2P2Tf1)))
					
				Case W2P3Tf1' start number input
					SetGadgetText(W2P3Tf1,CheckNameLength$(4,GadgetText(W2P3Tf1)))
					
				Case W2P1But1' continue after selecting file extension
					If ButtonState(W2P1Rad1)=True Then' extension from list
						Exten=GadgetItemText(W2P1Cb1,SelectedGadgetItem(W2P1Cb1))
						DisableGadget(W2P1); SetPanelColor(W2P1,200,255,200)
						SetGadgetText(W2P5L3,Exten); SetGadgetColor(W2P5L3,200,255,200)				
						If Status=0 Then' first pass
							EnableGadget(W2P2); SetPanelColor(W2P2,255,255,200)	
						Else
							If AllOk(W2P5L3,W2P5L6,W2P5L8)=1 Then EnableGadget(W2P5But4)												
						End If																	
					Else' other extension
						If Len(GadgetText(W2P1Tf1))<3 Then
							Notify "Extensions must have 3 letters/numbers and no spaces."
							SetGadgetText(W2P1Tf1,""); ActivateGadget(W2P1Tf1)
						Else'other input OK
							If CheckText(GadgetText(W2P1Tf1))<>0 Then' Input error
								Notify "Extensions must have 3 letters/numbers and no spaces."
								SetGadgetText(W2P1Tf1,""); ActivateGadget(W2P1Tf1)
							Else' input OK
								Exten="*."+GadgetText(W2P1Tf1)
								DisableGadget(W2P1); SetPanelColor(W2P1,200,255,200)
								SetGadgetText(W2P5L3,Exten); SetGadgetColor(W2P5L3,200,255,200)								
								If Status=0 Then' first pass
									EnableGadget(W2P2); SetPanelColor(W2P2,255,255,200)									
								Else
									If AllOk(W2P5L3,W2P5L6,W2P5L8)=1 Then EnableGadget(W2P5But4)															
								End If											
							End If
						End If
					End If
				
				Case W2P2But1' continue after selecting prefix
					If ButtonState(W2P2Rad1)=True Then' prefix from list
						Prefix=GadgetItemText(W2P2Cb1,SelectedGadgetItem(W2P2Cb1))
						DisableGadget(W2P2); SetPanelColor(W2P2,200,255,200)
						SetGadgetText(W2P5L6,Prefix); SetGadgetColor(W2P5L6,200,255,200)												
						If Status=0 Then
							EnableGadget(W2P3); SetPanelColor(W2P3,255,255,200)	
						Else
							If AllOk(W2P5L3,W2P5L6,W2P5L8)=1 Then EnableGadget(W2P5But4)											
						End If						
					Else' other pefix
						If Len(GadgetText(W2P2Tf1))<1 Then'input error
							Notify "Prefix must have at least one letter/number and no spaces."
							SetGadgetText(W2P2Tf1,""); ActivateGadget(W2P2Tf1)
						Else'other input OK
							If CheckText(GadgetText(W2P2Tf1))<>0  Then 'input error
								Notify "Prefix must have at least one letter/number and no spaces."
								SetGadgetText(W2P2Tf1,""); ActivateGadget(W2P2Tf1)							
							Else' input ok
								Prefix=GadgetText(W2P2Tf1)+"-"
								DisableGadget(W2P2); SetPanelColor(W2P2,200,255,200)
								SetGadgetText(W2P5L6,Prefix); SetGadgetColor(W2P5L6,200,255,200) 							
								If status=0 Then
									EnableGadget(W2P3); SetPanelColor(W2P3,255,255,200)															
								Else
									If AllOk(W2P5L3,W2P5L6,W2P5L8)=1 Then EnableGadget(W2P5But4)								
								End If													
							End If
						End If
					End If
				
				Case W2P3But1' continue after selecting start number
					Temp1=Int(GadgetText(W2P3Tf1))
					If Temp1<1 Or Temp1>3000 Then' invalid number
						Notify "Please enter a number between 1 and 3000."
						SetGadgetText(W2P3Tf1,"1"); ActivateGadget(W2P3Tf1)			
					Else'valid number
						StNum=GadgetText(W2P3Tf1)
						DisableGadget(W2P3); SetPanelColor(W2P3,200,255,200) 
						SetGadgetText(W2P5L8,StNum); SetGadgetColor(W2P5L8,200,255,200)
						If status=0 Then
							EnableGadget(W2P4); SetPanelColor(W2P4,255,255,200)'; SetGadgetColor(W2P4L4,200,255,200)
							SetPanelColor(W2P5,255,255,200); EnableGadget(W2P5But1);
							EnableGadget(W2P5But2);EnableGadget(W2P5But3); EnableGadget(W2P5But4)	
							Status=1
						Else
							If AllOk(W2P5L3,W2P5L6,W2P5L8)=1 Then EnableGadget(W2P5But4)																			
						End If		

					End If

				Case W2P4But1'up a level during destination folder select
					Tst1=UpDir(CurrentDir())
					ChangeDir(Tst1)
					FolderView(CurrentDir(),W2P4Lb1,W2P4But1)
					FileView(CurrentDir(),W2P4Lb2)
					ClearGadgetItems(W2P4Lb4)
					AddGadgetItem(W2P4Lb4,CurrentDir())						
				
				Case W2P5But1' edit file extension choice
					SetGadgetText(W2P5L3,""); SetGadgetColor(W2P5L3,255,255,255)
					EnableGadget(W2P1); SetPanelColor(W2P1,255,255,200); DisableGadget(W2P5But4)				
					If ButtonState(W2P1Rad2)=True Then
						SetGadgetText(W2P1Tf1,""); ActivateGadget(W2P1Tf1)						
					End If									
				
				Case W2P5But2' edit prefix choice
					SetGadgetText(W2P5L6,""); SetGadgetColor(W2P5L6,255,255,255)
					EnableGadget(W2P2); SetPanelColor(W2P2,255,255,200); DisableGadget(W2P5But4)
					If ButtonState(W2P2Rad2)=True Then
						SetGadgetText(W2P2Tf1,""); ActivateGadget(W2P2Tf1)						
					End If									

				Case W2P5But3' edit start number
					SetGadgetText(W2P5L8,""); SetGadgetColor(W2P5L8,255,255,255)
					EnableGadget(W2P3); SetPanelColor(W2P3,255,255,200); DisableGadget(W2P5But4)	
					SetGadgetText(W2P3Tf1,"1"); ActivateGadget(W2P3Tf1)	
		
				Case W2P5But4 ' continue to file select screen
					Desfold=CurrentDir()+Lz
					'Print DesFold ' storage for destination folder name
					HideGadget(W2P1); HideGadget(W2P2); HideGadget(W2P3)
					HideGadget(W2P4); HideGadget(W2P5); Status=2
					'ChangeDir(Lz+Lz+Lz)' to root
					ChangeDir(Lz+Lz+Lz+"users"); Folderview(CurrentDir(),W2P7Lb1,W2P7But1)
					FileView2(CurrentDir(),W2P7Lb2,Exten)
					ClearGadgetItems(W2P7Lb4)
					AddGadgetItem(W2P7Lb4,CurrentDir())										
					ClearGadgetItems(W2P7Lb3)
					SetGadgetText(W2P7L1,Right$(Exten,4)+" files in opened folder.")
					ShowGadget(W2P7); ShowGadget(W2P8)					
					
				Case W2P5But5' exit
					Endit()
					
				Case W2P5But6' screen 1 help
					HideGadget(W2P1); HideGadget(W2P2); HideGadget(W2P3)
					HideGadget(W2P4); HideGadget(W2P5); ShowGadget(W2P6)
					HelpScreen(1,W2P6Tf1)' display screen 1 help
					
				Case W2P6But1' return from help screen
					If Status<2 Then' return to screen 1					
						HideGadget(W2P6); ShowGadget(W2P1); ShowGadget(W2P2) 
						ShowGadget(W2P3); ShowGadget(W2P4); ShowGadget(W2P5)
					Else If Status=2 Then 'return to screen 2
						HideGadget(W2P6); ShowGadget(W2P7); ShowGadget(W2P8) 						
					Else' status = 3 return to screen 3
						HideGadget(W2P6)
						ShowGadget(W2P10); ShowGadget(W2P11)					
					End If
					
				Case W2P7But1'up a level during files to copy select
					Tst1=UpDir(CurrentDir())
					ChangeDir(Tst1); ClearGadgetItems(W2P7Lb3)
					FolderView(CurrentDir(),W2P7Lb1,W2P7But1)
					FileView2(CurrentDir(),W2P7Lb2,Exten)					
					ClearGadgetItems(W2P7Lb4)
					AddGadgetItem(W2P7Lb4,CurrentDir())
					SetGadgetText(W2P7L2,"0 Files selected for copy list")						

				Case W2P7But2' select all files in current folder for copying
					ClearGadgetItems(W2P7Lb3)
					DisableGadget(W2P7But5)
					Temp1=CountGadgetItems(W2P7Lb2)
					If Temp1<>0 Then 
						temp1=temp1-1
						For temp2=0 To temp1
					 		AddGadgetItem(W2P7Lb3,GadgetItemText$(W2P7Lb2,temp2 ))					
						Next 
						SetGadgetText(W2P7L2,String(CountGadgetItems(W2P7Lb3))+" Files selected for copy list")						
					End If							
				
				Case W2P7But3' clear all files for copying from list
					ClearGadgetItems(W2P7Lb3)
					DisableGadget(W2P7But5)
					SetGadgetText(W2P7L2,"0 Files selected for copy list")						
					
				Case W2P7But4' add selected files to copy list
					Temp1=CountGadgetItems(W2P7Lb3)	' see if any files have been selected
					If Temp1=0 Then
						Notify "You have not selected any files for copying."
					
					Else' some files have been selected
						If FList.GetFcount()+Temp1>MaxFile Then
							Notify "You have selected more than "+MaxFile+" files for copying."
						Else' less than 2000 files to copy add these files to the copy list
							FList.SetFNames(W2P7Lb3); ClearGadgetItems(W2P7Lb3)
							FList.SetFold(W2P8Lb1,CurrentDir())
							SetGadgetText(W2P8L2,"File Count = "+FList.GetFCount())
							SetGadgetText(W2P7L2,"0 Files selected for copy list")
							DisableGadget(W2P7But5)							
						End If					
					End If				
				
				Case W2P7But5' deleat selected file from copy list
					DelCFile(W2P7Lb3)' delete file from list function
					DisableGadget(W2P7But5)				
					SetGadgetText(W2P7L2,String(CountGadgetItems(W2P7Lb3))+" Files selected for copy list")	
									
				Case W2P8But1' exit from select files screen
					EndIt()
					
				Case W2P8But2' help from select files screen screen 2
					HideGadget(W2P7); HideGadget(W2P8); ShowGadget(W2P6) 				
					HelpScreen(2,W2P6Tf1)' display screen 2 help	
					
				Case W2P9But1' return from viewing files in files to copy list
					HideGadget(W2P9); ShowGadget(W2P7); ShowGadget(W2P8)
					ToggleGadgetItem(W2P8Lb1,SelectedGadgetItem(W2P8Lb1))
	
				Case W2P8But3' ready to copy the files
					If Flist.GetFCount()>0 Then' some file to copy
						HideGadget(W2P7); HideGadget(W2P8); Status=3
						Tst1="The " + String(FList.GetFCount()) + " files you create will be stored in :"
						SetGadgetText(W2P11L1,Tst1); ClearGadgetItems(W2P11Lb1)
						AddGadgetItem(W2P11Lb1,DesFold)
						Tst1=Prefix+StNum+Right$(Exten,4); SetGadgetText(W2P11L3,Tst1)
						Tst1="Ready to Copy "+String(Flist.GetFCount())+" Files."; SetGadgetText(W2P11L4,Tst1)
						SetButtonState(W2P10Chk1,False); SetButtonState(W2P10Chk2,False)
						SetButtonState(W2P10Chk3,False)	
						ChkFiles(W2P10Chk1,W2P10Chk2,W2P10Chk3,DesFold)									
						ShowGadget(W2P10);ShowGadget(W2P11)
	
					Else' no files for copying
						Notify "You have not selected ay files for copying."					
					End If
	
				Case W2P10But1'enable/disable check buttons if files/don't exsist in destination folder
					ChkFiles(W2P10Chk1,W2P10Chk2,W2P10Chk3,DesFold)
			
				Case W2P11But1' copy  the files
					DisableGadget(W2P10); DisableGadget(W2P11But1); DisableGadget(W2P11But2)
					DisableGadget(W2P11But3); DisableGadget(W2P11But4)
					Tst1="Please wait. Copying "+String(Flist.GetFCount())+" Files."
					SetGadgetText(W2P11L4,Tst1)					
					RedrawGadget(W2)					
					Temp1=0 ' set var for text files created during copy procedure
					If ButtonState(W2P10Chk1)<>0 Then Temp1=Temp1+1
					If ButtonState(W2P10Chk2)<>0 Then Temp1=Temp1+2					
					If ButtonState(W2P10Chk3)<>0 Then Temp1=Temp1+4					
					FList.Init2(Exten,Prefix,StNum,DesFold,Temp1)' sets vars in FList														
					Temp1=CountGadgetItems(W2P8Lb1); Temp1=Temp1-1													
					For Temp2=0 To Temp1
						Tst1=GadgetItemText(W2P8Lb1,Temp2)
						FList.DoCopy(Tst1,Temp2,W2P11Pb1)' send origin folder name and index, and progbar label															
					Next
					HideGadget(W2P10); HideGadget(W2P11); ShowGadget(W2P12)
					Tst1="You Have Copied "+String(Flist.GetFCount())+" Files To :"; SetGadgetText(W2P12L1,Tst1)
					AddGadgetItem(W2P12Lb1,DesFold)					

				Case W2P11But2' exit from copy files screen
					EndIt()
				
				Case W2P11But3' return to select files to copy screen
					HideGadget(W2P10); HideGadget(W2P11); Status=2
					ShowGadget(W2P7); ShowGadget(W2P8)
				
				Case W2P11But4' help for copy files screen 3
					HideGadget(W2P10); HideGadget(W2P11); ShowGadget(W2P6) 				
					HelpScreen(3,W2P6Tf1)' display screen 3 help	
				
				Case W2P12But1' exit from final screen
					EndIt()
																	
      		End Select		
	End Select

Forever

'------------------------ End of Main Loop-------------------------	

Function EndIt()'common exit point
	Select Confirm("Are you sure you want to quit?")
		Case 1
			End
		Case 0	
			Return
	End Select
End Function

Function ChkFiles(Tv1:Tgadget,Tv2:Tgadget,Tv3:Tgadget,Ts1$)' ids for the 3 check buttonsand destination folder
	If FileType(Ts1+"alist.txt")=0 Then EnableGadget(Tv1) Else DisableGadget(Tv1)
	If FileType(Ts1+"alist.cdt")=0 Then EnableGadget(Tv2) Else DisableGadget(Tv2)
	If FileType(Ts1+"anerror.txt")=0 Then EnableGadget(Tv3) Else DisableGadget(Tv3)	
End Function

Function UpDir$(Dir$)' returns directory path up one level, dir = current directory
	Local Tx1%=0; Local Ts1$=""; Local Ts2$=""
	If Dir=Lz  Then' at the root
		Ts2=Dir
	Else	
		Ts2=Dir; Tx1=1
		If Instr(Dir,Lz)<>0 Then'folder separator
			Repeat
				Ts1=Right$(Ts2,Tx1)
				If Instr(Ts1,Lz) Then' found folder name plus \
					Ts2=Lz
				Else
					Tx1=Tx1+1
				End If						
			Until Ts2=Lz
			Ts2=Left$(Dir,(Len(Dir)-Tx1))
			If Ts2="" Then Ts2=Lz

		Else' must be at root
		 	' should never get hear
			Notify " I've made a programing error in 'UpDir()' opps!!!"
		End If	
	End If
	Return Ts2
End Function

Function FullPath$(Dir$,Ts1$)
	Local Ts2$=""
	Ts2 = RealPath(Dir+Lz+Ts1)	
	Return Ts2
End Function


Function FolderView(Dir$,Tv:TGadget,Bv:TGadget)
	Local Tx1%=0; Local Ts1$=""; Local FP$=""
	ClearGadgetItems(Tv)
	Tx1=ReadDir(Dir)
	If Dir=Lz Then
		DisableGadget(Bv)
	Else
		EnableGadget(Bv)	
	End If
	Repeat
   	 	Ts1=NextFile(Tx1)
		FP = RealPath(Dir+Lz+Ts1)
		If Instr(Ts1,".")<>0 Or Ts1=Null Then
			'don't show these items
		Else' show these item
			If FP<>"" Then
				If FileType(FP)=2 Then AddGadgetItem(TV,Ts1)		
			End If
		End If	
	Until Ts1 = Null	
End Function

Function FileView(Dir$,Tv:Tgadget)
	Local Tx1%=0; Local Ts1$=""; Local FP$=""
	Tx1=ReadDir(Dir)
	ClearGadgetItems(TV)
	Repeat
   	 	Ts1=NextFile(Tx1)
		If Left$(Ts1,1)<>"." Then	
			FP = RealPath(Dir+Lz+Ts1)
			If FP<>"" Then
				If FileType(FP)=1 Then AddGadgetItem(TV,Ts1)
			End If
		End If
	Until Ts1=Null
End Function

Function FileView2(Dir$,Tv:Tgadget,Ts3$)' search directory, gadget list for display, extension. ie '*.mp3'
	Local Tx1%=0; Local Ts1$=""; Local Ts2$=""
	Tx1=ReadDir(Dir)
	ClearGadgetItems(TV)
	Repeat
		Ts1=NextFile(Tx1)
		Ts2=Right$(Ts1,4)
		If Ts2=Right$(Ts3,4) Then AddGadgetItem(TV,Ts1)' file has selected extension	
	Until Ts1=Null
End Function

Function SelFile(Ts1$,Tv:Tgadget)' Ts1=filename selected, Tv=list box to display in
	Local Tx1%=0; Local Tx2%=0; Local Tx3%=0;  Local Ts2$=""
	Tx1=CountGadgetItems(Tv)	
	If Tx1=0 Then 
		AddGadgetItem(Tv,Ts1)	
	Else If Tx1>0 Then' some items in list
		Tx1=Tx1-1
		For Tx2= 0 To Tx1
			If Ts1=GadgetItemText$(Tv,Tx2 ) Then Tx3=1		
		Next
		If Tx3=0 Then AddGadgetItem(Tv,Ts1)
	End If
End Function

Function DelCFile(Tv:Tgadget)'removes seleted file name from the copy list
	Local Tx1%=0
	Tx1=SelectedGadgetItem(Tv)
	If Tx1<>-1 Then 'something  selected
		RemoveGadgetItem(Tv,Tx1)		
	End If
	Tx1=SelectedGadgetItem(Tv)
	If Tx1<>-1 Then DeselectGadgetItem(Tv,Tx1)
End Function


Function CheckText%(Ts4$)' only allow a-z, A-Z, 0 to 9
	Local Ts1$=Ts4
	Local Tx1%=Len(Ts1)-1
	Local Tx2%=0; Local Tx3%=0; Local Tx4%=0
	Local Ts2$=""; Local Ts3$=""
	If Tx1>-1 Then
		For Tx2=0 To Tx1
			Tx3=Tx2+1; Ts2=Ts1[Tx2..Tx3]
			If (Asc(Ts2)>64) And (Asc(Ts2)<91) Then'A..Z
				Ts3=Ts3+Ts2
			Else If (Asc(Ts2)>96) And (Asc(Ts2)<123) Then'a..z
				Ts3=Ts3+Ts2
			Else If (Asc(Ts2)>47) And (Asc(Ts2)<58) Then'0..9
				Ts3=Ts3+Ts2
			Else 
				Ts3=Ts3+Ts2
				Tx4=1				
			End If							
		Next
	End If
	Return Tx4
End Function

Function CheckNameLength$(Tx1%,Ts2$) 'keeps name input Ts2 to Tx1 length max
	Local Ts1$=Ts2
	If Len(Ts1)>Tx1 Then
		Ts1=Ts1[0..Tx1]						
	End If
	Return Ts1	
End Function

Function AllOk%(Tg1:Tgadget,Tg2:Tgadget,Tg3:Tgadget)' check to see if all vars set before enabling 'continue'
	Local Tx1%=1
	If GadgetText(Tg1)="" Or GadgetText(Tg2)="" Or GadgetText(Tg3)="" Then Tx1=0 	
	Return Tx1 ' return 0 if any vars not set
End Function

Function HelpScreen(Tx1%,Tf:Tgadget)' text for help screen
	Local S1$=Chr$(13);	SetTextAreaText(Tf,"")	
	
	If Tx1=1 Then' help screen 1
AddTextAreaText(Tf,"                       Program Help  -  Screen One."+S1+S1)
FormatTextAreaText( Tf,0,180,0,TEXTFORMAT_UNDERLINE,23,TEXTAREA_ALL,TEXTAREA_CHARS )
AddTextAreaText(Tf,"Hint : Create a folder for your copies before starting !"+S1+S1)	
AddTextAreaText(Tf,"This program is designed to copy multiple files from multiple areas into a single folder and"+S1)	
AddTextAreaText(Tf,"a name sequence of your choice."+S1+S1)
AddTextAreaText(Tf,"First (top left) you will be asked to select the file extension of the file type you want to"+S1)	
AddTextAreaText(Tf,"copy, click 'Continue' when you have made your selection."+S1+S1)
AddTextAreaText(Tf,"You can then select a prefix for the files you want to create. After clicking 'Continue' you can"+S1)	
AddTextAreaText(Tf,"provide a start number for the files you are going to create. Click 'Continue' again to activate"+S1)
AddTextAreaText(Tf,"the folder selection area (bottom left) to choose the folder to place your copied files into."+S1+S1)	
AddTextAreaText(Tf,"You can change any of your choices by clicking the appropriate 'Edit' button."+S1)
AddTextAreaText(Tf,"At the second screen you select the files for copying, another help screen is provided."+S1+S1)
AddTextAreaText(Tf,"As an example :"+S1)
AddTextAreaText(Tf,"You select  *.wav  as a file extension,  'mus-'  as a prefix and  120  as a start number."+S1)
AddTextAreaText(Tf,"You choose to place the copied files into  \users\me\music."+S1)
AddTextAreaText(Tf,"You choose to copy  'fred1.wav' ,  'gojo73.wav'  and  'somemusic.wav'"+S1+S1)
AddTextAreaText(Tf,"With these choices the following files will be created in '\users\me\music'."+S1)	
AddTextAreaText(Tf,"A copy of  'fred1.wav'  named  'mus-120.wav'"+S1)
AddTextAreaText(Tf,"A copy of  'gojo73.wav'  named  'mus-121.wav'"+S1)
AddTextAreaText(Tf,"A copy of  'somemusic.wav'  named  'mus-122.wav'"+S1+S1)
AddTextAreaText(Tf,"When you have made your selections click 'Continue with current settings' to get to the file"+S1)
AddTextAreaText(Tf,"selection screen.")
	
	Else If Tx1=2 Then 'help for screen 2 file select screen
AddTextAreaText(Tf,"                       Program Help  -  Screen Two."+S1+S1)
FormatTextAreaText( Tf,0,180,0,TEXTFORMAT_UNDERLINE,23,TEXTAREA_ALL,TEXTAREA_CHARS )
AddTextAreaText(Tf,"Select the folder from the top left list, containing the files you wish to copy."+S1)	
AddTextAreaText(Tf,"The files with the extension you previously specified will be shown in the top centre list."+S1+S1)	
AddTextAreaText(Tf,"Select a file by clicking the file name, or all files using the 'Select All' option."+S1)
AddTextAreaText(Tf,"You can click on a file in the top right hand list and the use the 'Remove File From Copy list'"+S1)	
AddTextAreaText(Tf,"option to remove this file from the list, or the 'Clear All' option to clear the list."+S1)
AddTextAreaText(Tf,"Once you have a list of all the files you want to copy from a folder, click the"+S1)
AddTextAreaText(Tf,"'Add Files To Copy List' option."+S1+S1)	
AddTextAreaText(Tf,"The list at the base of the screen will show the folders that have files in that you have"+S1)
AddTextAreaText(Tf,"selected for copying. You can review the files to be copied from each folder by clicking the"+S1)
AddTextAreaText(Tf,"appropriate folder name."+S1+S1)
AddTextAreaText(Tf,"The program will show a running total of the files selected for copying, when you have selected"+S1)	
AddTextAreaText(Tf,"all the files you wish to copy click the 'Ready To Copy The Files.' option"+S1+S1)
AddTextAreaText(Tf,"The maximum number of files that can be copied in one pass is "+MaxFile+".")

	Else' Tx1=3 ready for copying screen 3
AddTextAreaText(Tf,"                       Program Help  -  Screen Three."+S1+S1)
FormatTextAreaText( Tf,0,180,0,TEXTFORMAT_UNDERLINE,23,TEXTAREA_ALL,TEXTAREA_CHARS )
AddTextAreaText(Tf,"The top half of the screen offers offers three options."+S1+S1)
AddTextAreaText(Tf,"OPTION create alist.txt : Generates a text file 'alist.txt' in your selected destination folder"+S1)	
AddTextAreaText(Tf,"that contains. The name of the destination folder. The name of the original folder and file."+S1)
AddTextAreaText(Tf,"And the new name for the file you have created. "+S1+S1)
AddTextAreaText(Tf,"OPTION create acdt.txt : Generates a text file 'acdt.txt' in your selected destination folder"+S1)	
AddTextAreaText(Tf,"that contains. The original file name and the new file name seperated by a comma."+S1)
AddTextAreaText(Tf,"This can be imported into a spreadsheet or database using the comma deliniated text setting."+S1+S1)		
AddTextAreaText(Tf,"OPTION create anerror.txt : Generates a text file 'anerror.txt' in your selected destination folder"+S1)	
AddTextAreaText(Tf,"that contains. All the failed attemps to copy a file or an empty file if no errors occured."+S1+S1)
AddTextAreaText(Tf,"If an option is not avaliable then the file must allready exsist in the destination folder."+S1)	
AddTextAreaText(Tf,"To generate the file go to the destination folder and delete the appropriate file, then click"+S1)	
AddTextAreaText(Tf,"the 'Check Folder' option. The option will now be avaliable."+S1+S1)
AddTextAreaText(Tf,"The lower panel shows the name of the destination folder, the name of the first new file to be"+S1)	
AddTextAreaText(Tf,"created and the total number of files to be copied."+S1)
AddTextAreaText(Tf,"You can return to the file select screen by clicking the 'Return To Select Files' option."+S1+S1)	
AddTextAreaText(Tf,"Clicking the 'Copy Selected Files' option starts the copying process."+S1)	
AddTextAreaText(Tf,"Progress is indicated by the progress bar shown, the program will let you know when"+S1)
AddTextAreaText(Tf,"the copying has been compleated."+S1+S1)
AddTextAreaText(Tf,"NB.. The program has been writen so as to prevent overwriting of files. If a file with the"+S1)	
AddTextAreaText(Tf,"same name as the one being created allready exsists in the destination folder it will not"+S1)
AddTextAreaText(Tf,"generate a copy."+S1+S1)
AddTextAreaText(Tf,"It is a good idea to start with an empty destination older.... Have Fun !")

	End If

End Function

'----------------  start of Types --------------------------

Type VarStore' store for folder index and file names to be copied
	Field FNum%[MaxFile]' array for holding next avaliable index for folder names in list box (0 to maxfile-1)
	Field FName$[MaxFile]' array for file names to be copied (0 to maxfile-1)
	Field Exten$=""; Field Prefix$=""; Field DesFold$=""
	Field PTxt:Byte=0; 	Field PCdt:Byte=0; 	Field PErr:Byte=0
	Field MyFile1:TStream; Field MyFile2:TStream; Field MyFile3:TStream	
	Field StNum%=0
	Field FCount% ' number of files selected for copying
	Field PBarCount%=0' actual number of files copied only used by the progress bar
	Field FCBig:Float=0.0'base number only used by progress bar
	
	Method Initilise()'set initial vars
		Local Tx1%=0
		For Tx1=0 To MaxFile-1
			FNum[Tx1]=0; Fname[Tx1]=""
		Next
		SetFCount(0)	
	End Method
	
	Method Init2(Ts1$,Ts2$,Ts3$,Ts4$,Tx1%)' sets Exten,Prefix,StNum,DesFold vars, Tx1=1 to 7
		Exten=Right$(Ts1,4); Prefix=Ts2; DesFold=Ts4
		StNum=Int(Ts3)
		Select Tx1
			Case 0
				PTxt=0; PCdt=0; PErr=0
			Case 1 PTxt=1
			Case 2 Pcdt=1				
			Case 3
				PTxt=1; Pcdt=1
			Case 4 PErr=1
			Case 5
				PErr=1; PTxt=1
			Case 6
				PErr=1; PCdt=1
			Case 7
				PErr=1; PTxt=1; PCdt=1		
		End Select
	End Method
		
	Method SetFNum(Tx1%)' sets FNum var
		FNum[Tx1]=GetFCount()
	End Method
	
	Method GetFNum%(Tx1%)' retrive FNum var
		Local Tx2%=0
		Tx2=FNum[Tx1]
		Return Tx2
	End Method
	
	Method SetStNum(Tx1%)' incrimants StNum% during file copy
		StNum=Tx1
	End Method
	
	Method GetStNum%()' gets StNum%
		Local Tx1%=0
		Tx1=StNum
		Return Tx1	
	End Method
	
	Method GetPrefix$()' gets new file prefix
		Return Prefix	
	End Method
	
	Method GetExten$()' gets new file extension
		Return Exten	
	End Method
	
	Method GetDesFold$()' gets new file destination folder
		Return DesFold
	End Method
	
	Method SetFCount(Tx1%)' sets FCount
		FCount=Tx1
	End Method
	
	Method GetFcount%()' returns current FCount
		Local Tx1%=0
		Tx1=Fcount
		Return Tx1
	End Method
	
	Method GetFName$(Tx1%)' returns file name stored at location FName[Tx1]
		Local Ts1$=""
		Ts1=FName[Tx1]		
		Return Ts1
	End Method
	
	Method ShowCFiles(Tv:Tgadget,Tx1%)' Tv=where to display file names, Tx1=index number
		Local Tx2%=0; Local Tx3%=0; Local Tx4%=0
		ClearGadgetItems(TV)
		Tx2=GetFNum(Tx1); Tx3=GetFNum(Tx1+1); Tx3=Tx3-1
		For Tx4=Tx2 To Tx3
			AddGadgetItem(Tv,GetFname(Tx4))			
		Next	
	End Method
	
	Method SetFNames(Tv1:Tgadget) 'Tv1=listbox with file names' puts file names into array
		Local Tx1%=0; Local Tx2%=0 ; Local Fc1%=0
		Fc1=GetFCount() ' start file count number
		Tx1=CountGadgetItems(Tv1)' number of file names in list box to copy		
		For Tx2=0 To Tx1-1
			Fname[Fc1+Tx2]=GadgetItemText(Tv1,Tx2)
			'Print Fc1+Tx2 +"  :  "+Fname[Fc1+Tx2]
		Next		
		SetFCount(Fc1+Tx2)	
	End Method
	
	Method SetFold(Tv1:Tgadget,Dir$)' sets Fnum values. Tv1=listbox for folder info, dir=current directory
		Local Tx1%=0; Local Ts1$=""
		Ts1=Dir+Lz
		AddGadgetItem(Tv1,Ts1)
		Tx1=CountGadgetItems(Tv1)
		SetFNum(Tx1)	
	End Method

	Method Docopy(Ts1$,InX%,Tv1:Tgadget)' origin folder name plus index for FNum array, progbar Tgadget
		Local Tx1%=0; Local Tx2%=0; Local Tx3%=0; Local Tx4%=0
		Local Ts2$=""; Local Ts3$=""; Local Ts4$=""	
		FcBig=GetFcount()'set FcBig to Number of files to copy
		
		If Inx=0 Then
			If PTxt Then MyFile1=WriteFile(DesFold+"alist.txt")' text file of copies
			If PCdt Then MyFile2=WriteFile(DesFold+"acdt.txt")'cdt file of copies 
			If PErr Then MyFile3=WriteFile(DesFold+"anerror.txt")' error log, if any	
		End If	
		Ts2=GetDesFold(); Tx4=GetStNum()
		Tx1=GetFNum(Inx); Tx2=GetFNum(Inx+1); Tx2=Tx2-1	
		If PTxt Then' write text file list of copies enabled
			If InX=0 Then ' first pass
				WriteLine(MyFile1,"All Files Copied To :")
				WriteLine(MyFile1,DesFold)
			End If			
			WriteLine(MyFile1,"Files Copied From :")
			WriteLine(MyFile1,Ts1)
		End If						
		For Tx3= Tx1 To Tx2
			Ts3=Ts1+GetFname(Tx3)' Ts3 = original filespec
			Ts4=Ts2+GetPrefix()+String(Tx4)+GetExten()' Ts4 = new filespec						
			If FileType(Ts4)=0' file does not exsist in destination folder, do the copy			
				If CopyFile( Ts3,Ts4 )=True Then			
					If PTxt Then WriteLine(MyFile1,GetFname(Tx3)+"  to  "+GetPrefix()+String(Tx4)+GetExten())
					If PCdt Then WriteLine(MyFile2,GetFname(Tx3)+","+GetPrefix()+String(Tx4)+GetExten())				
				Else' error
					If PErr Then WriteLine(MyFile3,Ts3+Chr$(13)+"was not copied to"+Chr$(13)+Ts4+Chr$(13))			
				End If
			Else' file allready exsists
If PErr Then WriteLine(MyFile3,Ts4 +Chr$(13)+"was not created as it exsisted in the destination folder"+Chr$(13))			
			End If						
			PBarCount=PbarCount+1					
			UpdateProgBar Tv1,PBarCount/FcBig
			Tx4=Tx4+1
		Next	
		SetStNum(Tx4)		
	End Method

	
	Method CloseCopy()	
		If PTxt Then CloseStream Myfile1	
		If PCdt Then CloseStream Myfile2
		If PErr Then CloseStream Myfile3
	End Method

End Type
