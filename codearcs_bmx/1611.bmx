; ID: 1611
; Author: po
; Date: 2006-02-11 22:51:32
; Title: A Simple Text Editor
; Description: A very simple text editor similar to Notepad.

SuperStrict

Const MENU_NEW:Int=11
Const MENU_OPEN:Int=3
Const MENU_SAVE:Int=4
Const MENU_SAVEAS:Int=12
Const MENU_EXIT:Int=1

Const MENU_CUT:Int=5
Const MENU_COPY:Int=6
Const MENU_PASTE:Int=7
Const MENU_SELECT:Int=10

Const MENU_ABOUT:Int=2

Global filename$
Global content$
Global latestsave$
Global name$
Global prosave:Int
Global action:Int
Global skip:Int=False
Global s$,c$

Local font:TGuiFont
Local linenum:Int
Local line:Int

Local font1:TGuiFont=LoadGuiFont("verdana",10,False,False,False)

Local window:TGadget=CreateWindow("Untitled - Text Editor",100,100,600,500,Null,WINDOW_TITLEBAR|WINDOW_RESIZABLE|WINDOW_MENU|WINDOW_STATUS)

SetMinWindowSize(window,200,200)

Local filemenu:TGadget=CreateMenu("File",0,WindowMenu(window))
	CreateMenu "New",MENU_NEW,filemenu,78,2
	CreateMenu "Open",MENU_OPEN,filemenu,79,2
	CreateMenu "Save",MENU_SAVE,filemenu,83,2
	CreateMenu "Save As",MENU_SAVEAS,filemenu,83,1+2
	CreateMenu "",Null,filemenu
	CreateMenu "Exit",MENU_EXIT,filemenu
	
Local editmenu:TGadget=CreateMenu("Edit",0,WindowMenu(window))
	CreateMenu "Cut",MENU_CUT,editmenu,88,2
	CreateMenu "Copy",MENU_COPY,editmenu,67,2
	CreateMenu "Paste",MENU_PASTE,editmenu,86,2	
	CreateMenu "",Null,editmenu
	CreateMenu "Select All",MENU_SELECT,editmenu,65,2	
	
Local helpmenu:TGadget=CreateMenu("Help",0,WindowMenu(window))
	CreateMenu "About",MENU_ABOUT,helpmenu

UpdateWindowMenu(window)

Global textbox:TGadget=CreateTextArea(0,0,592,434,window,0)
	SetGadgetLayout(textbox,1,1,1,1)	
	SetGadgetColor(textbox,255,255,255,True)
	SetTextAreaFont(textbox,font1)
	SetTextAreaColor(textbox,0,0,0,False)
	
	
While WaitEvent()

	Select EventID()
	
		Case EVENT_WINDOWCLOSE
			ProceedSave(1)
			
		Case EVENT_MENUACTION
			
			Select EventData()
							
				Case MENU_EXIT
					ProceedSave(1)					
											
				Case MENU_ABOUT
					AppTitle="Text Editor"
					Notify "Text Editor v1.0 by Paul Leduc."
					
				Case MENU_CUT
					GadgetCut(textbox)
					
				Case MENU_COPY
					GadgetCopy(textbox)
					
				Case MENU_PASTE
					GadgetPaste(textbox)
					
				Case MENU_SELECT
					SelectTextAreaText(textbox,0,TextAreaLen(textbox,1),1)					
				
				Case MENU_NEW
					ProceedSave(2)
					If skip=False Then SetTextAreaText(textbox,"")
					filename$=""
					name$=""
					latestsave$=TextAreaText(textbox)
					SetGadgetText(window,"Untitled - Text Editor")
					
				Case MENU_OPEN
					ProceedSave(2)
					If skip=False Then
						filename$=RequestFile("Open","Text Files (*.txt):txt;All Files (*):*")
						If filename$ Then 											
							Local file:TStream=ReadFile(filename$)					
							SetTextAreaText(textbox,LoadString$(file),0,-1,1)
							latestsave$=TextAreaText(textbox)
							name$=StripDir(filename$)
							SetGadgetText(window,name$+" - Text Editor")					
						EndIf
					EndIf
										
				Case MENU_SAVE
					Save()
					
				Case MENU_SAVEAS
					SaveAs()				

			End Select

		
		Default
		
			line=TextAreaLine(textbox,TextAreaCursor(textbox,1))+1			
			SetStatusText window," Line: "+line
					
	End Select

Wend

Function Save()

	Print filename$

	content$=TextAreaText(textbox)
					
	If filename$ Then 
					
		SaveString(content$,filename$)							
					
		latestsave$=TextAreaText(textbox)
						
	Else
					
		SaveAs()
											
	EndIf

End Function

Function SaveAs()

	content$=TextAreaText(textbox)
					
	filename$=RequestFile("Save As","Text Files (*.txt):txt;All Files (*):*",True)
	
	If filename$ Then
	
		If ExtractExt(filename$)="" Then filename$=filename$+".txt"
		
		Local newfile:TStream=WriteFile(filename$)			
		SaveString(content$,newfile)
					
		While Not(Eof(newfile))
			s$=ReadLine(newfile)
			c$=c$+s$+Chr(13)+Chr(10)
			Print s$			
		Wend
		
		SaveString("",newfile)					
		
		SaveString(c$,newfile)		
		
		CloseStream(newfile)
				
		latestsave$=TextAreaText(textbox)
		name$=StripDir(filename$)
		
	Else
	
		skip=True
					
	EndIf

End Function

Function ProceedSave(action:Int)

	skip=False

	If latestsave$=TextAreaText(textbox) Then
		If action=1 Then End
	Else
		If name$ Then 
			prosave=Proceed("Save changes to "+name$+"?")
		Else
			prosave=Proceed("Save changes to Untitled?")
		EndIf
		If prosave=0 And action=1 Then End		
		If prosave=-1 And action=2 Then skip=True
		If prosave=1 Then Save()
	EndIf

End Function
