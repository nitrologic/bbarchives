; ID: 3096
; Author: Dabhand
; Date: 2014-01-03 11:10:57
; Title: BambooBasic Editor
; Description: An editor for my BambooBasic language

SuperStrict

Framework wx.wxApp
Import wx.wxScintilla
Import wx.wxMouseEvent
Import wx.wxFrame
Import wx.wxPanel
Import wx.wxButton
Import wx.wxStaticText
Import wx.wxTextCtrl
Import wx.wxNotebook
Import wx.wxToolBar
Import wx.wxBitmap
Import wx.wxHtmlWindow
Import wx.wxFileDialog
Import wx.wxDirDialog
Import wx.wxDialog 
Import wx.wxListCtrl
Import wx.wxcheckbox
Import wx.wxProcess

Import wx.wxTaskBarIcon

Import brl.retro
Import brl.map
Import brl.Standardio

'Import bah.volumes
Import MaxGUI.Drivers

Import "bambooiconres.o"


Global moduleList:TList
Global functionMap:TMap
Global variableMap:TMap
Global projectPath:String = ""
Global projectFilePath:String = ""

Include "types.bmx"
Include "common.bmx"
Include "Lex.bmx"
Include "NewProject.bmx"
Include "ProjectSettings.bmx"
Include "ProjectExplorer.bmx"

Global gframe:GLBWorkFrame

'Load config file
LoadConfig()

Const ideversion:String = "v1.3"
Const header:String = "BambooBasic " + ideversion

Const Main_Frame:Int = 1
Const Home_Frame:Int = 2
Const Output_Frame:Int = 3

Const TREEVIEW:Int = 9000

Const Menu_File_New_Project:Int = 101
Const Menu_File_New_File:Int = 102
Const Menu_File_Open_Project:Int = 103
Const Menu_File_Open_File:Int = 104
Const Menu_File_Save:Int = 105
Const Menu_File_Save_As:Int = 106
Const Menu_File_Save_Project:Int = 107
Const Menu_File_Close_File:Int = 108
Const Menu_File_Close_Project:Int = 109
Const Menu_File_Remove_File:Int = 110
Const Menu_File_Quit:Int = 111

Const Menu_File_About:Int = 201

Const BOOKCTRL:Int = 2001

Const Tool_New_Project:Int = 3001
Const Tool_Open_Project:Int = 3002
Const Tool_New_File:Int = 3003
Const Tool_Save_File:Int = 3004
Const Tool_Save_All:Int = 3005
Const Tool_Open_File:Int = 3006
Const Tool_Close_File:Int = 3007
Const Tool_Open_Project_Folder:Int = 3008
Const Tool_Project_Explorer:Int = 3009

Const Tool_Cut:Int = 3110
Const Tool_Copy:Int = 3111
Const Tool_Paste:Int = 3112
Const Tool_Select_All:Int = 3113
Const Tool_Undo:Int = 3114
Const Tool_Redo:Int = 3115

Const Tool_Build:Int = 3201
Const Tool_Start:Int = 3202
Const Tool_Settings:Int = 3203
Const Tool_Open_Output_Folder:Int = 3204

Const Tool_Browser_Home:Int = 3301
Const Tool_Browser_Back:Int = 3302
Const Tool_Browser_Forward:Int = 3303

New GLBWork.Run()

Type GLBWork Extends wxApp
	Field frame:GLBWorkFrame

	Method OnInit:Int()
		' Create application frame
		frame = GLBWorkFrame(New GLBWorkFrame.Create(, Main_Frame, header, 0, 0, 800, 600))
		 
		' open application frame
		frame.Center()
		frame.Layout()
		frame.Show(True)
		SetTopWindow(frame)
		
		Return True
	End Method
	
End Type

Global editPages:TList
Global notebook:wxNotebook

Type GLBWorkFrame Extends wxFrame
	Field menu:wxMenu
	
	Field m_toolBar1:wxToolBar
	Field icon:wxBitmap
	Field home:wxHtmlWindow
	Field outputWindow:wxTextCtrl
	
	Method OnInit()
		wxInitAllImageHandlers()
		CreateStatusBar(1)
		Connect(, wxEVT_CLOSE_WINDOW, onCloseBoxQuit)
		wxInitAllImageHandlers()
		
		editPages = New TList
		
		Local fileMenu:wxMenu = New wxMenu.Create()
		
		Local fileNewMenu:wxMenu = New wxMenu.Create()
		fileNewMenu.Append(Menu_File_New_Project, "New Project")
		fileNewMenu.Append(Menu_File_New_File, "New File")
		fileMenu.AppendSubMenu(fileNewMenu, "New...")
		
		fileMenu.AppendSeparator()
		Local fileMenuOpenProject:wxMenuItem = New wxMenuItem.Create(fileMenu, Menu_File_Open_Project, "Open Project")
		fileMenu.AppendItem(fileMenuOpenProject)
		Local fileMenuOpenFile:wxMenuItem = New wxMenuItem.Create(fileMenu, Menu_File_Open_File, "Open File")
		fileMenu.AppendItem(fileMenuOpenFile)
		fileMenu.AppendSeparator()
		
		Local fileMenuSave:wxMenuItem = New wxMenuItem.Create(fileMenu, Menu_File_Save, "Save")
		fileMenu.AppendItem(fileMenuSave)
		
		Local fileMenuSaveAs:wxMenuItem = New wxMenuItem.Create(fileMenu, Menu_File_Save_As, "Save as...")
		fileMenu.AppendItem(fileMenuSaveAs)
		
		Local fileMenuSaveProject:wxMenuItem = New wxMenuItem.Create(fileMenu, Menu_File_Save_Project, "Save Project")
		fileMenu.AppendItem(fileMenuSaveProject)
		
		fileMenu.AppendSeparator()
		
		Local fileMenuCloseFilet:wxMenuItem = New wxMenuItem.Create(fileMenu, Menu_File_Close_File, "Close File")
		fileMenu.AppendItem(fileMenuCloseFilet)
		
		Local fileMenuCloseProject:wxMenuItem = New wxMenuItem.Create(fileMenu, Menu_File_Close_Project, "Close Project")
		fileMenu.AppendItem(fileMenuCloseProject)
		
		fileMenu.AppendSeparator()
				
		Local fileMenuRemoveFile:wxMenuItem = New wxMenuItem.Create(fileMenu, Menu_File_Remove_File, "Remove File")
		fileMenu.AppendItem(fileMenuRemoveFile)
		
		fileMenu.AppendSeparator()
		
		Local fileMenuQuit:wxMenuItem = New wxMenuItem.Create(fileMenu, Menu_File_Quit, "Exit BambooBasic")
		fileMenu.AppendItem(fileMenuQuit)
		
		Local menuBar:wxMenuBar = New wxMenuBar.Create( wxMB_DOCKABLE )
		menuBar.Append(fileMenu, "&File")
		
		' associate the menu bar with the frame
		'SetMenuBar(menuBar)
		
		Connect(Menu_File_Quit,wxEVT_COMMAND_MENU_SELECTED, OnQuit)
		Connect(Menu_File_New_Project, wxEVT_COMMAND_MENU_SELECTED, OnNewProject)
		Connect(Menu_File_New_File, wxEVT_COMMAND_MENU_SELECTED, OnNewFile)
		Connect(Menu_File_Open_Project, wxEVT_COMMAND_MENU_SELECTED, OnOpenProject)
		Connect(Menu_File_Open_File, wxEVT_COMMAND_MENU_SELECTED, OnOpenFile)
		Connect(Menu_File_Save, wxEVT_COMMAND_MENU_SELECTED, OnSaveFile)
		Connect(Menu_File_Save_As, wxEVT_COMMAND_MENU_SELECTED, OnSaveFileAs)
		Connect(Menu_File_Save_Project, wxEVT_COMMAND_MENU_SELECTED, OnSaveProject)
		Connect(Menu_File_Close_File, wxEVT_COMMAND_MENU_SELECTED, OnCloseFile)
		Connect(Menu_File_Close_Project, wxEVT_COMMAND_MENU_SELECTED, OnCloseProject)
		Connect(Menu_File_Remove_File, wxEVT_COMMAND_MENU_SELECTED, OnRemoveFile)
		icon = wxBitmap.CreateBitmap()
		
		Local editMenu:wxMenu = New wxMenu.Create()
		menuBar.Append(editMenu, "Edit")
		
		Local editUndo:wxMenuItem = New wxMenuItem.Create(editMenu, Tool_Undo, "Undo")
		editMenu.AppendItem(editUndo)
		
		Local editRedo:wxMenuItem = New wxMenuItem.Create(editMenu, Tool_Redo, "Redo")
		editMenu.AppendItem(editRedo)
		
		editMenu.AppendSeparator()
		
		Local editCut:wxMenuItem = New wxMenuItem.Create(editMenu, Tool_Cut, "Cut")
		editMenu.AppendItem(editCut)
		
		Local editCopy:wxMenuItem = New wxMenuItem.Create(editMenu, Tool_Copy, "Copy")
		editMenu.AppendItem(editCopy)
		
		Local editPaste:wxMenuItem = New wxMenuItem.Create(editMenu, Tool_Paste, "Paste")
		editMenu.AppendItem(editPaste)
		
		editMenu.AppendSeparator()
		
		Local editSelectAll:wxMenuItem = New wxMenuItem.Create(editMenu, Tool_Select_All, "Select All")
		editMenu.AppendItem(editSelectAll)
		
		Connect(Tool_Undo, wxEVT_COMMAND_MENU_SELECTED, OnUndo)
		Connect(Tool_Redo, wxEVT_COMMAND_MENU_SELECTED, OnRedo)
		Connect(Tool_Cut, wxEVT_COMMAND_MENU_SELECTED, OnCut)
		Connect(Tool_Copy, wxEVT_COMMAND_MENU_SELECTED, OnCopy)
		Connect(Tool_Paste, wxEVT_COMMAND_MENU_SELECTED, OnPaste)
		Connect(Tool_Select_All, wxEVT_COMMAND_MENU_SELECTED, OnSelectAll)
		
		'Local viewMenu:wxMenu = New wxMenu.Create()
		'menuBar.Append(viewMenu, "View")
		
		Local projectMenu:wxMenu = New wxMenu.Create()
		menuBar.Append(projectMenu, "Project")
		
		Local projectMenuProjectExplorer:wxMenuItem = New wxMenuItem.Create(projectMenu, Tool_Project_Explorer, "Project Explorer")
		projectMenu.AppendItem(projectMenuProjectExplorer)
		
		projectMenu.AppendSeparator()
		
		Local projectMenuBuild:wxMenuItem = New wxMenuItem.Create(projectMenu, Tool_Build, "Build Project")
		projectMenu.AppendItem(projectMenuBuild)
		
		projectMenu.AppendSeparator()
		
		Local projectMenuStart:wxMenuItem = New wxMenuItem.Create(projectMenu, Tool_Start, "Build and Run Project")
		projectMenu.AppendItem(projectMenuStart)
		
		projectMenu.AppendSeparator()
		
		Local projectMenuOutputFolder:wxMenuItem = New wxMenuItem.Create(projectMenu, Tool_Open_Output_Folder, "Open Output Directory")
		projectMenu.AppendItem(projectMenuOutputFolder)
		
		projectMenu.AppendSeparator()
		
		Local projectMenuSettings:wxMenuItem = New wxMenuItem.Create(projectMenu, Tool_Settings, "Project Settings")
		projectMenu.AppendItem(projectMenuSettings)
		
		Connect(Tool_Project_Explorer, wxEVT_COMMAND_MENU_SELECTED, OnProjectExplorer)
		Connect(Tool_Build, wxEVT_COMMAND_MENU_SELECTED, OnBuild)
		Connect(Tool_Start, wxEVT_COMMAND_MENU_SELECTED, onStart)
		Connect(Tool_Settings, wxEVT_COMMAND_MENU_SELECTED, OnSettings)
		Connect(Tool_Open_Output_Folder, wxEVT_COMMAND_MENU_SELECTED, OnOpenOutputFolder)
		
		Local helpMenu:wxMenu = New wxMenu.Create()
		menuBar.Append(helpMenu, "Twaddle")
		
		Local helpAboutMenu:wxMenuItem = New wxMenuItem.Create(helpMenu, Menu_File_About, "About")
		helpMenu.AppendItem(helpAboutMenu)
		
		Connect(Menu_File_About, wxEVT_COMMAND_MENU_SELECTED, OnAbout)
		
		SetMenuBar(menuBar)
		
		Connect(Tool_Open_Output_Folder, wxEVT_COMMAND_MENU_SELECTED, OnOpenOutputFolder)
		
		Local m_panel1:wxPanel
		Local m_panel2:wxPanel
		
		Local bSizer4:wxBoxSizer
		bSizer4 = New wxBoxSizer.Create(wxVERTICAL)

		m_panel1 = New wxPanel.Create(Self, wxID_ANY,,,,, wxTAB_TRAVERSAL)

		Local bSizer5:wxBoxSizer
		bSizer5 = New wxBoxSizer.Create(wxVERTICAL)
		
		notebook = wxNotebook.CreateNotebook(m_panel1, BOOKCTRL, wxID_ANY)
		
		Connect(-1, wxEVT_COMMAND_NOTEBOOK_PAGE_CHANGING, OnPageChanging)
		Connect(-1, wxEVT_COMMAND_NOTEBOOK_PAGE_CHANGED, OnPageChanged)
		
		m_panel2 = New wxPanel.Create(notebook, wxID_ANY,,, ,, wxTAB_TRAVERSAL)
		
		home = New wxHtmlWindow.CreateHtmlWindow(m_panel2, Home_Frame,,,,,wxHW_SCROLLBAR_AUTO)
		home.LoadPage(AppDir:String + "\doc\index.html")
		
		Local bSizer6:wxBoxSizer
		bSizer6 = New wxBoxSizer.Create(wxVERTICAL)

		bSizer6.Add(home, 1, wxEXPAND | wxALL, 5)

		m_panel2.SetSizer(bSizer6)
		m_panel2.Layout()
		bSizer6.Fit(m_panel2)
		
		' the first page
		notebook.AddPage(m_panel2, "Home", True)
		
		bSizer5.Add(notebook, 4, wxEXPAND | wxALL, 5)
		outputWindow:wxTextCtrl = New wxTextCtrl.Create(m_panel1, 5000, "Welcome to BambooBasic...",,,,,wxTE_MULTILINE)
		bSizer5.Add(outputWindow, 1, wxEXPAND | wxALL, 5)
		
		'Add home stuff below
		m_panel1.SetSizer(bSizer5)
		m_panel1.Layout()
		bSizer5.Fit(m_panel1)
		bSizer4.Add(m_panel1, 1, wxEXPAND | wxALL, 5)
		
		m_toolBar1 = CreateToolbar(wxTB_HORIZONTAL, wxID_ANY)
		m_toolBar1.SetToolBitmapSize(24, 24)
		icon.LoadFile("config\newproject.bmp", wxBITMAP_TYPE_BMP)
		m_toolBar1.AddTool(Tool_New_Project, "NewProject", icon,,, "New Project")
		Connect(Tool_New_Project,wxEVT_COMMAND_TOOL_CLICKED,OnNewProject)
		
		icon.LoadFile("config\openproject.bmp", wxBITMAP_TYPE_BMP)
		m_toolBar1.AddTool(Tool_Open_Project, "OpenProject", icon,,, "Open Project")
		Connect(Tool_Open_Project, wxEVT_COMMAND_TOOL_CLICKED, onOpenProject)
		
		m_toolBar1.AddSeparator()
		
		icon.LoadFile("config\newfile.bmp", wxBITMAP_TYPE_BMP)
		m_toolBar1.AddTool(Tool_New_File, "NewFile", icon,,, "New File")
		Connect(Tool_New_File, wxEVT_COMMAND_TOOL_CLICKED, OnNewFile)
		
		icon.LoadFile("config\openfile.bmp", wxBITMAP_TYPE_BMP)
		m_toolBar1.AddTool(Tool_Open_File, "OpenFile", icon,,, "Open File")
		Connect(Tool_Open_File, wxEVT_COMMAND_TOOL_CLICKED, OnOpenFile)
		
		icon.LoadFile("config\savefile.bmp", wxBITMAP_TYPE_BMP)
		m_toolBar1.AddTool(Tool_Save_File, "SaveFile", icon,,, "Save File")
		Connect(Tool_Save_File, wxEVT_COMMAND_TOOL_CLICKED, OnSaveFile)
		
		icon.LoadFile("config\saveall.bmp", wxBITMAP_TYPE_BMP)
		m_toolBar1.AddTool(Tool_Save_All, "SaveAll", icon,,, "Save All")
		Connect(Tool_Save_All, wxEVT_COMMAND_TOOL_CLICKED, OnSaveProject)
		
		icon.LoadFile("config\closefile.bmp", wxBITMAP_TYPE_BMP)
		m_toolBar1.AddTool(Tool_Close_File, "CloseFile", icon,,, "Close File")
		Connect(Tool_Close_File, wxEVT_COMMAND_TOOL_CLICKED, OnCloseFile)
		
		'Tool_Project_Explorer
		icon.LoadFile("config\projectexplorer.bmp", wxBITMAP_TYPE_BMP)
		m_toolBar1.AddTool(Tool_Project_Explorer, "ProjectExplorer", icon,,, "Project Explorer")
		Connect(Tool_Project_Explorer, wxEVT_COMMAND_TOOL_CLICKED, OnProjectExplorer)
		
		icon.LoadFile("config\opencontainingfolder.bmp", wxBITMAP_TYPE_BMP)
		m_toolBar1.AddTool(Tool_Open_Project_Folder, "OpenProjectFolder", icon,,, "Open Project Folder")
		Connect(Tool_Open_Project_Folder, wxEVT_COMMAND_TOOL_CLICKED, OnOpenProjectFolder)
		
		m_toolBar1.AddSeparator()
		
		icon.LoadFile("config\cut.bmp", wxBITMAP_TYPE_BMP)
		m_toolBar1.AddTool(Tool_Cut, "Cut", icon,,, "Cut Text")
		Connect(Tool_Cut, wxEVT_COMMAND_TOOL_CLICKED, OnCut)
		
		icon.LoadFile("config\copy.bmp", wxBITMAP_TYPE_BMP)
		m_toolBar1.AddTool(Tool_Copy, "Copy", icon,,, "Copy Text")
		Connect(Tool_Copy, wxEVT_COMMAND_TOOL_CLICKED, OnCopy)
		
		icon.LoadFile("config\paste.bmp", wxBITMAP_TYPE_BMP)
		m_toolBar1.AddTool(Tool_Paste, "Paste", icon,,, "Paste Text")
		Connect(Tool_Paste, wxEVT_COMMAND_TOOL_CLICKED, OnPaste)
		
		m_toolBar1.AddSeparator()
		
		icon.LoadFile("config\settings.bmp", wxBITMAP_TYPE_BMP)
		m_toolBar1.AddTool(Tool_Settings, "ProjectSettings", icon,,, "Project Settings")
		Connect(Tool_Settings, wxEVT_COMMAND_TOOL_CLICKED, OnSettings)
		
		icon.LoadFile("config\start.bmp", wxBITMAP_TYPE_BMP)
		m_toolBar1.AddTool(Tool_Start, "StartProject", icon,,, "Build and run")
		Connect(Tool_Start, wxEVT_COMMAND_TOOL_CLICKED, OnStart)
		
		icon.LoadFile("config\build.bmp", wxBITMAP_TYPE_BMP)
		m_toolBar1.AddTool(Tool_Build, "BuildProject", icon,,, "Build only")
		Connect(Tool_Build, wxEVT_COMMAND_TOOL_CLICKED, OnBuild)
		
		icon.LoadFile("config\openoutputdir.bmp", wxBITMAP_TYPE_BMP)
		m_toolBar1.AddTool(Tool_Open_Output_Folder, "OutputFolder", icon,,, "Open output folder")
		Connect(Tool_Open_Output_Folder, wxEVT_COMMAND_TOOL_CLICKED, OnOpenOutputFolder)
		
		m_toolBar1.AddSeparator()
		
		icon.LoadFile("config\home.bmp", wxBITMAP_TYPE_BMP)
		m_toolBar1.AddTool(Tool_Browser_Home, "HomeTab", icon,,, "Open home page")
		Connect(Tool_Browser_Home, wxEVT_COMMAND_TOOL_CLICKED, OnHome)
		
		icon.LoadFile("config\back.bmp", wxBITMAP_TYPE_BMP)
		m_toolBar1.AddTool(Tool_Browser_Back, "Back", icon,,, "Back one home page")
		Connect(Tool_Browser_Back, wxEVT_COMMAND_TOOL_CLICKED, OnBack)
		
		icon.LoadFile("config\forward.bmp", wxBITMAP_TYPE_BMP)
		m_toolBar1.AddTool(Tool_Browser_Forward, "Forward", icon,,, "Forward one home page")
		Connect(Tool_Browser_Forward, wxEVT_COMMAND_TOOL_CLICKED, OnForward)
		
		m_toolBar1.Realize()
		
		
		SetSizer(bSizer4)
		Layout()
		Center(wxBOTH)
		
		gframe = Self
		
	End Method
	
	Function OnAbout(events:wxEvent)
		wxMessageBox "BambooBasic "+Chr$(169)+" Michael Denathorn 2012-2013~n~nVersion: "+ideversion+"~n~nFor updates, help and support, please visit www.bamboocoder.com~n~nwxWidgets wrapper by Bruce Henderson~nhttp://brucey.net/programming/blitz/~n~nSpecial thanks to: Steve Ancell (Bug hunter)","About BambooBasic..."
	End Function
	
	Function OnProjectExplorer(events:wxEvent)
		
		If projectFilePath = ""
			wxMessageBox "You need to create or load a project!", "BambooEditor Message"
			Return
		EndIf
		
		Local frame:ProjectExplorer
		frame = ProjectExplorer(New ProjectExplorer.Create(,, "Project Explorer...", 0, 0, 400, 500, wxCLOSE_BOX | wxCAPTION))
		frame.CenterOnParent()
		frame.Layout()
		frame.Show(True)
	End Function
	
	Function OnHome(event:wxEvent)
		notebook.SetSelection(0)
		Local page:Int = notebook.GetSelection()
		If page = 0
			gframe.home.LoadPage(AppDir:String + "\doc\index.html")
		EndIf
	End Function
	
	Function OnBack(event:wxEvent)
		Local page:Int = notebook.GetSelection()
		If page = 0
			If gframe.home.HistoryCanBack()
				gframe.home.HistoryBack()
			EndIf
		EndIf 
	End Function
	
	Function OnForward(event:wxEvent)
		Local page:Int = notebook.GetSelection()
		If page = 0
			If gframe.home.HistoryCanForward()
				gframe.home.HistoryForward()
			EndIf
		EndIf 
	End Function
	
	Function OnCloseBoxQuit(event:wxEvent)
		If projectFilePath <> ""
			projectProperties.SaveProject()
		End If
		End
	End Function

	Function OnQuit(event:wxEvent)
		If projectFilePath <> ""
			projectProperties.SaveProject()
		End If
		wxFrame(event.parent).Close(True)
	End Function
	
	Function OnSettings(events:wxEvent)
		
		If projectFilePath = ""
			wxMessageBox "You need to create or load a project!", "BambooEditor Message"
			Return
		EndIf
		
		Local frame:ProjectSettingsFrame
		frame = ProjectSettingsFrame(New ProjectSettingsFrame.Create(,, "Project Settings...", 0, 0, 300, 240, wxCLOSE_BOX | wxCAPTION))
		frame.CenterOnParent()
		frame.Layout()
		frame.Show(True)
	End Function
	
	Function CallBSG(run:Int = False)
		If projectPath <> ""
			gframe.SaveAll()
			'ChangeDir(AppDir$)
			
			gframe.outputWindow.Clear()

			Local cmdstring:String
			
			cmdstring = AppDir:String + "\bin\bsg_win"
			
			
			
			cmdstring = cmdstring + " -s ~q" + projectPath + "\" + projectProperties.editFile + "~q"
			cmdstring = cmdstring + " -o ~q" + projectProperties.outputDir + "~q"
			
			If run = True
				cmdstring = cmdstring + " -b ~q" + projectProperties.localURL + "~q"
			EndIf 
			
			If projectProperties.useBambooHeader Then cmdstring = cmdstring + " -httphead"
			If projectProperties.doTrace Then cmdstring = cmdstring + " -t"
			If projectProperties.outputComments Then cmdstring = cmdstring + " -c"
			
			Local m_versionProcess:wxProcess = New wxProcess.CreateWithFlags(wxPROCESS_REDIRECT)
			
			Local m_pid:Int = wxExecute(cmdstring, wxEXEC_SYNC, m_versionProcess )
			
			Local p_in:wxInputStream = m_versionProcess.GetInputStream()
		
			If p_in Then
				Local textInput:wxTextInputStream = New wxTextInputStream.Create(p_in)
				
				While Not p_in.Eof()
					gframe.outputWindow.AppendText(textInput.ReadLine() +"~n")
				Wend
				
			End If

			m_versionProcess.free()
			
			gframe.outputWindow.Enable()
			gframe.setfocus()
		EndIf 
	End Function 
	
	Function OnBuild(event:wxEvent)
		CallBSG()
	End Function
	
	Function OnStart(event:wxEvent)
		CallBSG(True)
	End Function
	
	Function OnUndo(event:wxEvent)
		Local page:Int = notebook.GetSelection()
		If page = 0
			wxMessageBox "Cannot redo here!", "BambooEditor Message"
			Return
		End If
		
		For Local loopEditIDs:Edit = EachIn editPages
			If loopEditIDs.id = page
				loopEditIDs.Undo()
				Exit
			EndIf
		Next
	End Function
	
	Function OnRedo(event:wxEvent)
		Local page:Int = notebook.GetSelection()
		If page = 0
			wxMessageBox "Cannot redo here!", "BambooEditor Message"
			Return
		End If
		
		For Local loopEditIDs:Edit = EachIn editPages
			If loopEditIDs.id = page
				loopEditIDs.Redo()
				Exit
			EndIf
		Next
	End Function
	
	Function OnCut(event:wxEvent)
		Local page:Int = notebook.GetSelection()
		If page = 0
			wxMessageBox "Cannot cut here!", "BambooEditor Message"
			Return
		End If
		
		For Local loopEditIDs:Edit = EachIn editPages
			If loopEditIDs.id = page
				loopEditIDs.Cut()
				Exit
			EndIf
		Next
	End Function
	
	Function OnCopy(event:wxEvent)
		Local page:Int = notebook.GetSelection()
		If page = 0
		wxMessageBox "Cannot copy here!", "BambooEditor Message"
			Return
		End If
		
		For Local loopEditIDs:Edit = EachIn editPages
			If loopEditIDs.id = page
				loopEditIDs.Copy()
				Exit
			EndIf
		Next
	End Function
	
	Function OnPaste(event:wxEvent)
		Local page:Int = notebook.GetSelection()
		If page = 0
		wxMessageBox "Cannot paste here!", "BambooEditor Message"
			Return
		End If
		
		For Local loopEditIDs:Edit = EachIn editPages
			If loopEditIDs.id = page
				loopEditIDs.Paste()
				Exit
			EndIf
		Next
	End Function
	
	Function OnSelectAll(event:wxEvent)
		Local page:Int = notebook.GetSelection()
		If page = 0
			wxMessageBox "Cannot select text here!", "BambooEditor Message"
			Return
		End If
		
		For Local loopEditIDs:Edit = EachIn editPages
			If loopEditIDs.id = page
				loopEditIDs.SelectAll()
				Exit
			EndIf
		Next
		
	End Function
	
	Function OnOpenOutputFolder(event:wxEvent)
		If projectFilePath = ""
			wxMessageBox "You need to create or load a project!", "BambooEditor Message"
			Return
		EndIf
		
		DebugLog(projectProperties.outputDir)
		
		?MacOs
			system_("open "+projectProperties.outputDir)
		?Win32
			OpenURL(projectProperties.outputDir)
		?Linux
			system_("xdg-open "+projectProperties.outputDir)
		?
	End Function
	
	Function OnOpenProjectFolder(event:wxEvent)
		If projectPath = "" Then Return
		
		?MacOs
			system_("open "+projectPath)
		?Win32
			OpenURL(projectPath)
		?Linux
			system_("xdg-open "+projectPath)
		?
	End Function
	
	Function OnRemoveFile(event:wxEvent)
		Local page:Int = notebook.GetSelection()
		If page = 0
			wxMessageBox "You cannot remove the 'Home' tab.", "BambooEditor Message"
			Return 
		EndIf
		
		If projectProperties.editFiles.Count() < 2
			wxMessageBox "You need at least one file open in the editor project!", "BambooEditor Message"
			Return
		EndIf
		
		Local result:Int = wxMessageBox("Are you sure you want to remove this file from the project?", "BambooEditor Message",wxICON_QUESTION | wxYES_NO)
		
		If result = wxNo
			Return
		EndIf
		
		Local doExit:Int = False
		For Local loopEditIDs:Edit = EachIn editPages
			If loopEditIDs.id = page 
				For Local loopEditFiles:String = EachIn projectProperties.editFiles
					If loopEditFiles = loopEditIDs.filename
						
						If loopEditFiles = projectProperties.appName
							wxMessageBox("This is the entry file to your application.~n~nYou cannot remove this file from the project.", "BambooEditor Message")
							Return
						EndIf
						
						projectProperties.editFiles.Remove(loopEditFiles)
						projectProperties.srcFiles.Remove(loopEditFiles)
						editPages.Remove(loopEditIDs)
						projectProperties.SaveProject()
						notebook.DeletePage(page)
						
						For loopEditIDs:Edit = EachIn editPages
							If loopEditIDs.id >= page
								loopEditIDs.id = loopEditIDs.id - 1
							End If
						Next
						doExit = True
						Exit
					EndIf
				Next
				If doExit = True Then Exit
			End If
		Next
		
		wxMessageBox "Note: File removed from project, but still exists in your project folder!", "BambooEditor Message"
	End Function
	
	Function OnCloseProject(event:wxEvent)		
		If projectFilePath = ""
			wxMessageBox "There is no project to close!", "BambooEditor Message"
			Return
		EndIf
		
		Local result:Int = wxMessageBox("Are you sure you want to close this project?", "BambooEditor Message",wxICON_QUESTION | wxYES_NO)
			
		If result = wxYES
			gframe.SaveAll()
		Else
			Return
		EndIf
		
		gframe.SetLabel(header)
		CloseProject()
	End Function
	
	Function CloseProject()
		If projectPath <> ""
			projectFilePath = ""
			projectPath = ""
			functionMap.Clear()
			variableMap.Clear()
			projectProperties.appName = ""
			projectProperties.companyName = ""
			projectProperties.editFile = ""
	
			For Local removePages:Int = notebook.GetPageCount() To 1 Step - 1
				notebook.DeletePage(removePages)
			Next
			
			projectProperties.editFiles.Clear()
			projectProperties.srcFiles.Clear()
			editPages.Clear()
		EndIf 		
	End Function
	
	Function OnCloseFile(event:wxEvent)
		Local page:Int = notebook.GetSelection()
		If page = 0
			wxMessageBox "You cannot close the Home tab!", "BambooEditor Message"	
			Return 
		EndIf
		
		If projectProperties.editFiles.Count() < 2
			wxMessageBox "You need at least one file open in the project!", "BambooEditor Message"
			Return
		EndIf
		
		Local doExit:Int = False
		For Local loopEditIDs:Edit = EachIn editPages
			If loopEditIDs.id = page 
				For Local loopEditFiles:String = EachIn projectProperties.editFiles
					If loopEditFiles = loopEditIDs.filename
						projectProperties.editFiles.Remove(loopEditFiles)
						editPages.Remove(loopEditIDs)
						projectProperties.SaveProject()
						notebook.DeletePage(page)
						
						For loopEditIDs:Edit = EachIn editPages
							If loopEditIDs.id >= page
								loopEditIDs.id = loopEditIDs.id - 1
							End If
						Next
						doExit = True
						Exit
					EndIf
				Next
				If doExit = True Then Exit
			End If
		Next
		
	End Function

	Function OnPageChanging(event:wxEvent)
		'Print "Old page is: " + wxNotebookEvent(event).GetOldSelection()
	End Function 
	
	Function OnPageChanged(event:wxEvent)
		'Print "New page is now: " + wxNotebookEvent(event).GetSelection()
	End Function 
	
	Function OnSaveFileAs(event:wxEvent)
		Local page:Int = notebook.GetSelection()
		If page = 0
			Return
		EndIf
		
		If projectFilePath = ""
			wxMessageBox "You need to create or load a project!", "BambooEditor Message"
			Return
		EndIf
				
		Local filename:String =  wxFileSelector( "Save file as...", projectPath, "", "bam","BambooBasic Source File (*.bam)|*.bam", wxFD_SAVE | wxFD_OVERWRITE_PROMPT )

		If filename = "" Then Return
		Local path:String = filename
		
		filename = Replace(filename, projectPath + "\", "")

		Local fileExists:Int = False
		
		For Local checkFiles:String = EachIn projectProperties.editFiles
			If checkFiles = filename
				fileExists = True
				Exit
			End If
		Next
		
		If fileExists = False
			projectProperties.editFiles.AddLast(filename)
			
			fileExists = False
			For Local checkFiles:String = EachIn projectProperties.srcFiles
				If checkFiles = filename
					fileExists = True
					Exit
				End If
			Next
			
			If fileExists = False
				projectProperties.srcFiles.AddLast(filename)
			End If
			
		Else
			For Local loopeditors:Edit = EachIn editPages
				If loopeditors.filename = filename
					wxMessageBox "There is a matching filename in the editor, please close it!", "BambooEditor Message"
					notebook.SetSelection(loopeditors.id)
					Return
				EndIf	
			Next
		EndIf
		
		projectProperties.SaveProject()
		
		For Local loopEditIDs:Edit = EachIn editPages
			If loopEditIDs.id = page
				SaveSource(path, page)
				Exit
			EndIf
		Next
		
		page = notebook.GetPageCount()
		
		Local m_panel2:wxPanel
		
		m_panel2 = New wxPanel.Create(notebook, wxID_ANY,,, ,, wxTAB_TRAVERSAL)
		Local newEdit:Edit = Edit(New Edit.Create(m_panel2, wxID_ANY,,, ,, wxWANTS_CHARS))
		Local bSizer6:wxBoxSizer
		bSizer6 = New wxBoxSizer.Create(wxVERTICAL)

		bSizer6.Add(newEdit, 1, wxEXPAND | wxALL, 5)

		m_panel2.SetSizer(bSizer6)
		m_panel2.Layout()
		bSizer6.Fit(m_panel2)
		notebook.InsertPage(page, m_panel2, StripDir(filename), False)
		notebook.SetSelection(page)
		
		newEdit.id = page
		newEdit.filename = filename
		Local formattedText:String = gframe.LoadSource(path)
		newEdit.AddText(formattedText)
		editPages.AddLast(newEdit)
		newEdit.SetFocus()
		
	End Function
	
	Function OnSaveFile(event:wxEvent)
		Local page:Int = notebook.GetSelection()
		If page = 0
			Return
		EndIf
		
		gframe.SaveAll()
	End Function
	
	Function OnSaveProject(event:wxEvent)
		For Local filesloop:Int = notebook.GetPageCount() To 1 Step - 1
			For Local loopEditFiles:Edit = EachIn editPages
				If filesloop = loopEditFiles.id
					SaveSource(projectPath + "\" + loopEditFiles.filename, filesloop)
					Exit
				End If
			Next
		Next
	End Function
	
	Function SaveAll()
		For Local filesloop:Int = notebook.GetPageCount() To 1 Step - 1
			For Local loopEditFiles:Edit = EachIn editPages
				If filesloop = loopEditFiles.id
					SaveSource(projectPath + "\" + loopEditFiles.filename, filesloop)
					Exit
				End If
			Next
		Next
	End Function
	
	Function OpenSourceFile(filename:String)
		
		filename = Replace(filename, projectPath + "\", "")
		
		Local fileExists:Int = False
		
		For Local checkFiles:String = EachIn projectProperties.editFiles
			If checkFiles = filename
				fileExists = True
				Exit
			End If
		Next
		
		If fileExists = False
			projectProperties.editFiles.AddLast(filename)
			
			fileExists = False
			For Local checkFiles:String = EachIn projectProperties.srcFiles
				If checkFiles = filename
					fileExists = True
					Exit
				End If
			Next
			
			If fileExists = False
				projectProperties.srcFiles.AddLast(filename)
			End If
			
		Else
			For Local loopeditors:Edit = EachIn editPages
				If loopeditors.filename = filename
					notebook.SetSelection(loopeditors.id)
					Return
				EndIf	
			Next
		EndIf
		
		projectProperties.SaveProject()
		
		Local page:Int = notebook.GetPageCount()
		
		Local m_panel2:wxPanel
		
		m_panel2 = New wxPanel.Create(notebook, wxID_ANY,,, ,, wxTAB_TRAVERSAL)
		Local newEdit:Edit = Edit(New Edit.Create(m_panel2, wxID_ANY,,, ,, wxWANTS_CHARS))
		Local bSizer6:wxBoxSizer
		bSizer6 = New wxBoxSizer.Create(wxVERTICAL)

		bSizer6.Add(newEdit, 1, wxEXPAND | wxALL, 5)

		m_panel2.SetSizer(bSizer6)
		m_panel2.Layout()
		bSizer6.Fit(m_panel2)
		notebook.InsertPage(page, m_panel2, StripDir(filename), False)
		notebook.SetSelection(page)
		
		newEdit.id = page
		newEdit.filename = filename
		Local formattedText:String = gframe.LoadSource(projectPath + "\" + filename)
		
		newEdit.Cancel()
		newEdit.SetUndoCollection(False)
		newEdit.AddText(formattedText)
		newEdit.SetUndoCollection(True)
		newEdit.EmptyUndoBuffer()
		newEdit.SetSavePoint()
		
		editPages.AddLast(newEdit)
		newEdit.SetFocus()
	End Function 
	
	Function OnOpenFile(event:wxEvent)
		If projectFilePath = ""
			wxMessageBox "You need to create or load a project!", "BambooEditor Message"
			Return
		EndIf
		Local filename:String =  wxFileSelector( "Open source file.", projectFilePath, "", "bam","BambooBasic Source File (*.bam)|*.bam", wxFD_OPEN)
		
		If filename = "" Then Return
		
		If(Instr(filename, projectPath) = 0) 
			wxMessageBox "File to be opened needs to be in the project root folder.", "BambooEditor Message"
			Return
		EndIf
		
		DebugLog "Open File: " + filename
		OpenSourceFile(filename)
	End Function
	
	Function onOpenProject(event:wxEvent)
		If projectFilePath <> ""
			Local result:Int = Confirm("Do you really want to open an existing project?")
			If result = False Then Return
			result = Confirm("Save any unsaved files in project?")
			If result
				gframe.SaveAll()
			EndIf
			
			gframe.CloseProject()
		End If
		
		
		
		gframe.SetLabel(header)
		
		Local filename:String =  wxFileSelector( "Open project.", CurrentDir$(), "", "bbp","BambooBasic Projects (*.bbp)|*.bbp", wxFD_OPEN)
	
		If filename = "" Then Return
		
		CloseProject() 
		projectProperties = Null
		projectProperties = TProject.OpenProject(filename)
		
		
		For Local openeditfiles:String = EachIn projectProperties.editFiles
			Local filepath:String = projectPath + "\" + openeditfiles
			
			Local page:Int = notebook.GetPageCount()
		
			Local m_panel2:wxPanel
			
			m_panel2 = New wxPanel.Create(notebook, wxID_ANY,,, ,, wxTAB_TRAVERSAL)
			Local newEdit:Edit = Edit(New Edit.Create(m_panel2, wxID_ANY,,, ,, wxWANTS_CHARS))
			Local bSizer6:wxBoxSizer
			bSizer6 = New wxBoxSizer.Create(wxVERTICAL)
	
			bSizer6.Add(newEdit, 1, wxEXPAND | wxALL, 5)
	
			m_panel2.SetSizer(bSizer6)
			m_panel2.Layout()
			bSizer6.Fit(m_panel2)
			notebook.InsertPage(page, m_panel2, StripDir(filepath), False)
			notebook.SetSelection(page)
			
			newEdit.id = page
			newEdit.filename = openeditfiles
			
			DebugLog "Loading Source"
			Local formattedText:String = gframe.LoadSource(filepath)
			DebugLog "Source Loaded"
			newEdit.Cancel()
			newEdit.SetUndoCollection(False)
			newEdit.AddText(formattedText)
			newEdit.SetUndoCollection(True)
			newEdit.EmptyUndoBuffer()
			newEdit.SetSavePoint()
			
			editPages.AddLast(newEdit)
			newEdit.SetFocus()
		Next
		
		projectProperties.SaveProject()
	End Function
	
	Function OnNewProject(event:wxEvent)
		If projectFilePath <> ""
			Local result:Int = Confirm("Do you really want to create a new project?")
			If result = False Then Return
			
			result = Confirm("Save any unsaved files in project?")
			If result
				gframe.SaveAll()
			EndIf
			
			gframe.CloseProject()
		End If
		
		
		
		Local frame:GLBWorkNewProjectFrame
		frame = GLBWorkNewProjectFrame(New GLBWorkNewProjectFrame.Create(,, "New BambooBasic Project...", 0, 0, 300, 325, wxCLOSE_BOX | wxCAPTION))
		frame.CenterOnParent()
		frame.Layout()
		frame.Show(True)
	End Function
	
	Function OnNewFile(event:wxEvent)
		If projectFilePath = ""
			wxMessageBox "You need to create or load a project first.", "BambooEditor Message"
			Return
		EndIf
		
		Local filename:String =  wxFileSelector( "Save file to...", projectPath, "", "bam","BambooBasic Source File (*.bam)|*.bam", wxFD_SAVE | wxFD_OVERWRITE_PROMPT )
		If filename = "" Then Return
		
		If(Instr(filename, projectPath) = 0)
			wxMessageBox "New file needs to be created in the project root folder.", "BambooEditor Message"
			Return
		EndIf
		
		If Lower$(Right$(filename,4)) <> ".bam" Then filename = filename + ".bam"
		
		Local filepath:String = filename 	
		
		filename = Replace(filename, projectPath + "\", "")
		
		Local fileExists:Int = False
		
		For Local checkFiles:String = EachIn projectProperties.editFiles
			If checkFiles = filename
				fileExists = True
				Exit
			End If
		Next
		
		If fileExists = False
			projectProperties.editFiles.AddLast(filename)
			
			fileExists = False
			For Local checkFiles:String = EachIn projectProperties.srcFiles
				If checkFiles = filename
					fileExists = True
					Exit
				End If
			Next
			
			If fileExists = False
				projectProperties.srcFiles.AddLast(filename)
			End If
			
		Else
			For Local loopeditors:Edit = EachIn editPages
				If loopeditors.filename = filename
					wxMessageBox "Please manually close file in editor first.", "BambooEditor Message"
					notebook.SetSelection(loopeditors.id)
					Return
				EndIf	
			Next
		EndIf
		
		projectProperties.SaveProject()

		Local fileout:TStream = WriteFile(filepath)
			WriteLine(fileout, "'------------------------")
			WriteLine(fileout, "'File: " + filename)
			WriteLine(fileout, "'Company: " + projectProperties.companyName)
			WriteLine(fileout, "'Created: " + CurrentDate())
		CloseFile fileout
		
		Local page:Int = notebook.GetPageCount()
		
		Local m_panel2:wxPanel
		
		m_panel2 = New wxPanel.Create(notebook, wxID_ANY,,, ,, wxTAB_TRAVERSAL)
		Local newEdit:Edit = Edit(New Edit.Create(m_panel2, wxID_ANY,,, ,, wxWANTS_CHARS))
		Local bSizer6:wxBoxSizer
		bSizer6 = New wxBoxSizer.Create(wxVERTICAL)

		bSizer6.Add(newEdit, 1, wxEXPAND | wxALL, 5)

		m_panel2.SetSizer(bSizer6)
		m_panel2.Layout()
		bSizer6.Fit(m_panel2)
		notebook.InsertPage(page, m_panel2, StripDir(filename), False)
		notebook.SetSelection(page)
		
		newEdit.id = page
		newEdit.filename = filename
		
		Local formattedText:String = gframe.LoadSource(projectPath + "\" + filename)
		
		newEdit.Cancel()
		newEdit.SetUndoCollection(False)
		newEdit.AddText(formattedText)
		newEdit.SetUndoCollection(True)
		newEdit.EmptyUndoBuffer()
		newEdit.SetSavePoint()
		
		editPages.AddLast(newEdit)
		newEdit.SetFocus()
		
	End Function
	
	Function LoadSource:String(unformattedSourceFile:String)
		Local fileContents:String
		DebugLog "Load Source: " + unformattedSourceFile
		Local filein:TStream = ReadFile(unformattedSourceFile)
		
		
		Repeat
			Local line:String = ReadLine(filein)
			fileContents = fileContents + line + Chr:String(13) + Chr:String(10)
		Until Eof(filein)
		
		CloseFile filein
		
		fileContents = Left(fileContents, Len(fileContents) - 2)
		
		Return fileContents
	End Function
	
	Function SaveSource(formattedSourceFile:String, page:Int)
		For Local loopEditIDs:Edit = EachIn editPages
			If loopEditIDs.id = page
		
				Local fileout:TStream = WriteFile(formattedSourceFile)
		
				Local lines:Int = loopEditIDs.GetLineCount() - 1
				
				Local inrem:Int = False
				For Local loopLine:Int = 0 To lines
					Local line:String = loopEditIDs.GetLine(loopLine)
					Local inComment:Int = False
					Local lineStr:String
					For Local loopChars:Int = 1 To Len(line)
						Local char:String = Mid:String(line, loopChars, 1)
						
						lineStr = lineStr + char
					Next
					lineStr = Replace(lineStr, Chr:String(13), "")
					lineStr = Replace(lineStr, Chr:String(10), "")
					WriteLine fileout, lineStr
				Next
					
				CloseFile fileout
				Return
			EndIf
		Next
	End Function
End Type


Type Edit Extends wxScintilla
	' "keys" holds the BlitzMax command list
	Global keys:String
	' the callback functions needs access to wxScintilla, a global object makes the job
	Field editControl:Edit
	Global moduleFunctionWords:String[]
	'Global keyList:String[]
	
	Field showCallTip:Int = False
	Field callTipString:String
	Field currentWords:String
	Field checkForMembers:Int
	Field filename:String
	Field id:Int
	
	Method OnInit()
		moduleList = New TList
		functionMap= New TMap
		variableMap= New TMap 
		keys:String = CreateCMDList()
		Self.editControl = Self
		
		Self.SetSelection(0, 0)
		
		ConnectAny(wxEVT_SCI_MARGINCLICK, onMarginClick)
		ConnectAny(wxEVT_SCI_CHARADDED, onCharAdd)
		ConnectAny(wxEVT_SCI_AUTOCOMP_SELECTION, onAutoCompSelection)
		ConnectAny(wxEVT_KEY_DOWN, OnKeyDown)
		
		
		SetLexerLanguage("blitzmax")
		SetKeyWords(0, keys.toLower())
		
		Local FONT:wxFont = New wxFont.CreateWithAttribs(12, wxTELETYPE, wxNORMAL, wxBOLD)
		Local kw:wxFont = New wxFont.CreateWithAttribs(12, wxTELETYPE, wxNORMAL, wxBOLD)
		StyleSetFontFont(wxSCI_STYLE_DEFAULT, FONT) 
		StyleSetFontFont(wxSCI_B_KEYWORD, kw) 
		
		StyleSetBackground(wxSCI_STYLE_DEFAULT, New wxColour.CreateNamedColour("RGB(51,68,85)"))
		StyleSetForeground(wxSCI_STYLE_DEFAULT, New wxColour.CreateNamedColour("RGB(255,255,255)"))
		StyleClearAll()
		
		SetCaretForeground(New wxColour.CreateNamedColour("RGB(255,255,255)"))
		SetCaretLineBackground(New wxColour.CreateNamedColour("RGB(51,68,85)"))
		Self.SetCaretLineVisible(True)
		
		StyleSetBackground(wxSCI_B_KEYWORD, New wxColour.CreateNamedColour("RGB(51,68,85)"))
		StyleSetBackground(wxSCI_B_STRING, New wxColour.CreateNamedColour("RGB(51,68,85)"))
		StyleSetBackground(wxSCI_B_COMMENT, New wxColour.CreateNamedColour("RGB(51,68,85)"))
		StyleSetBackground(wxSCI_B_NUMBER, New wxColour.CreateNamedColour("RGB(51,68,85)"))
		
		StyleSetForeground(wxSCI_B_KEYWORD, New wxColour.CreateNamedColour("RGB(255, 255, 0)"))
		StyleSetForeground(wxSCI_B_COMMENT, New wxColour.CreateNamedColour("RGB(187, 238, 255)"))
		StyleSetForeground(wxSCI_B_STRING, New wxColour.CreateNamedColour("RGB(0, 255, 102)"))
		StyleSetForeground(wxSCI_B_NUMBER, New wxColour.CreateNamedColour("RGB(64, 255, 255)"))
		StyleSetForeground(wxSCI_B_COMMENTREM, New wxColour.CreateNamedColour("RGB(187, 238, 255)"))
		StyleSetForeground(wxSCI_B_INLINE, New wxColour.CreateNamedColour("RGB(230,230,230)"))
		
		StyleSetBold(wxSCI_B_KEYWORD, True)
		StyleSetBold(wxSCI_B_STRING, True)
		StyleSetBold(wxSCI_B_COMMENT, True)
		StyleSetBold(wxSCI_B_NUMBER, True)
		
		SetTabWidth(4)
		SetIndent(0)
		SetTabIndents(1)
		SetBackSpaceUnIndents(1)
		Self.editControl.SetIndentationGuides(2)
		
		StyleSetForeground(wxSCI_STYLE_INDENTGUIDE, New wxColour.CreateNamedColour("RGB(145, 145, 0)"))
		
		
		' Linenumber Margin	
		setmargintype(0, wxSCI_MARGIN_NUMBER)
		SetMarginWidth(0, TextWidth(wxSCI_STYLE_LINENUMBER, "_999999"))
		StyleSetForeground(wxSCI_STYLE_LINENUMBER, New wxColour.CreateNamedColour("RGB(50,50,50)"))
		
		AutoCompSetSeparator(Asc(" "))
		AutoCompSetAutoHide(False)
		AutoCompSetIgnoreCase(True)
		AutoCompSetChooseSingle(True)
		AutoCompSetFillUps(" (\")
		
		moduleFunctionWords = keys.Split(" ")
		
		UsePopUp(1)
		SetLayoutCache(wxSCI_CACHE_PAGE)
		SetBufferedDraw(1)
		
	End Method
	
	Function GetControl:Edit()
		Local selection:Int = notebook.GetSelection()
		
		For Local loopEditors:Edit = EachIn editPages
			If loopEditors.id = selection
				Return(loopEditors.editControl)
			EndIf
		Next 
		
		Return(Null)
	EndFunction
	
	Function OnKeyDown(event:wxEvent)
		Local ed:Edit = GetControl()
		Local evt:wxKeyEvent = wxKeyEvent(event)
		
		If evt.GetKeyCode() = WXK_RETURN And Not ed.AutoCompActive()
			Local strLine:String
			Local iPos:Int = ed.GetCurrentPos()
	 		Local iLine:Int = ed.LineFromPosition(iPos)
			
			Local tabCount:Int = 0
			strLine = ed.GetLine(iLine)
			For Local checkTabs:Int = 1 To Len(strLine)
				If Mid:String(strLine, checkTabs, 1) = "~t"
					tabCount = tabCount + 1
				Else
					Exit
				EndIf
			Next
			
			ed.NewLine()
			For Local tabLoop:Int = 1 To tabCount
				ed.Tab()
			Next
			
			event.Skip(False)
			Return
		EndIf
		
		event.Skip()
	End Function
	
	Function onAutoCompSelection(event:wxEvent)
		event.Skip()
	End Function
	
	Function CheckForString:Int(currentPos:Int)
		Local ed:Edit = GetControl()
		Local result:Int = False
		Local quoteCount:Int = 0
		Local iLine:Int = ed.LineFromPosition(currentPos)
		Local strLine:String = ed.GetLine(iLine)
		
		For Local index:Int = Len(strLine) To 1 Step -1
			Local char:String = Mid$(strLine,index,1)
			
			If char = "~q"
				quoteCount = quoteCount + 1
				result = quoteCount Mod 2
			EndIf
			
			If char = "'" And result = False
				Return(False)
			End If 
		Next
		
		Return (Not result)
	End Function
	
	Function onCharAdd(event:wxEvent)
		Local ed:Edit = GetControl()
		
		Local iPos:Int = ed.GetCurrentPos()
		
		If CheckForString(iPos) = True
			Local charAtLeft:String = Chr:String(ed.GetCharAt(iPos - 1))
			
			Self.Intellisense(iPos, charAtLeft)
		
		EndIf 
		
		ed.BeginUndoAction()
		ed.EndUndoAction()
			
		event.Skip()
	End Function
	
	Function onMarginClick(event:wxEvent)
		Local ed:Edit = GetControl()
		Local p:Int = wxScintillaEvent(event).getPosition()
		ed.togglefold(ed.linefromposition(p))
		
		event.Skip()
	End Function
	
	Function Intellisense(iPos:Int, charAtLeft:String)
	
		Local isCommand:Int = CheckForAutoCompCommands(iPos)
		
		If isCommand = False
			CheckForCallTips(iPos)	
		EndIf
		
	End Function
	
	Function RemoveUnwantedCallTip:Int(strLine:String)
		Local bracketCount:Int = 0
		
		If Instr(strLine, "(", 1)
			For Local Loop:Int = 1 To Len(strLine)
				Local char:String = Mid:String(strLine, Loop, 1)
			
				If char = "("
					bracketCount = bracketCount + 1
				EndIf
			
				If char = ")"
					bracketCount = bracketCount - 1
				EndIf
			Next
		
			If bracketCount = 0
				Return True
			EndIf
		EndIf
		Return False
	End Function
	
	Function CheckForCallTips(iPos:Int)
		Local ed:Edit = GetControl()
		
		Local strLine:String
	 	Local iLine:Int = ed.LineFromPosition(iPos)
		Local iStart:Int = ed.PositionFromLine(iLine)
		
		strLine = ed.GetTextRange(iStart, iPos)
		Local openBracketCount:Int
		Local closedBracketCount:Int
		Local bracketLoop:Int = 0
		Local callTipFound:Int = False
		'Sort out available words
		Local lex:TLexer = New TLexer
		lex.mLexLine(strLine)
		
		For Local loopTokens:Int = 0 To (lex.iTokenAmount + 1)
			If lex.TokenType[loopTokens] = lex.TYPE_SYMBOL
				If lex.sTokenBank[loopTokens] = "("
				 	If lex.TokenType[loopTokens - 1] = lex.TYPE_WORD
						openBracketCount = openBracketCount + 1
					End If
				EndIf
			EndIf
			
			If lex.sTokenBank[loopTokens] = ")"
				closedBracketCount = closedBracketCount + 1
			End If
		Next

		bracketLoop = openBracketCount
		
		For Local loopTokens:Int = (lex.iTokenAmount + 1) To 1 Step - 1
			If lex.TokenType[loopTokens] = lex.TYPE_SYMBOL
	
				If lex.sTokenBank[loopTokens] = "("
					If lex.TokenType[loopTokens - 1] = lex.TYPE_WORD
						If StringArrayContains(ed.moduleFunctionWords, lex.sTokenBank[loopTokens - 1])
							ed.CallTipCancel()
								If RemoveUnwantedCallTip(strLine) = False
									ed.CallTipShow(iPos, String(functionMap.ValueForKey(lex.sTokenBank[loopTokens - 1])))
									callTipFound = True
									Exit
								EndIf
							EndIf
						EndIf
					EndIf
				
				
				If lex.sTokenBank[loopTokens] = ")"
					For Local newLoop:Int = loopTokens To 1 Step - 1
						If lex.TokenType[newLoop] = lex.TYPE_SYMBOL
							If lex.sTokenBank[newLoop] = "("
								bracketLoop = bracketLoop - 1
								If lex.TokenType[newLoop - 1] = lex.TYPE_WORD And (bracketLoop = (openBracketCount - closedBracketCount))
								loopTokens = newLoop
								ed.CallTipCancel()
								Exit
								EndIf
							EndIf
						EndIf
					Next
					
				End If
			EndIf
		Next
	End Function
	
	Function CheckForAutoCompCommands:Int(iPos:Int)
	Local ed:Edit = GetControl()
	
	Local autoCompFireAmount:Int = 1
	Local token:String = ""
	Local objectToken:String = ""
	Local reversedToken:String = ""
	Local char:Int
	Local currentWords:String
	
	Local iLine:Int = ed.LineFromPosition(iPos)
	Local iStart:Int = ed.PositionFromLine(iLine)
	
	ed.checkForMembers = False
	Local loopLine:Int
	
			For loopLine = iPos To iStart Step - 1		
				char = ed.GetCharAt(loopLine)
				
				Select char
					Case Asc("~t")
						Exit
					Case 32
						Exit
					Case 10
						Exit
					Case Asc(",")
						Exit
					Case Asc("(")
						Exit
					Case Asc(".")
						ed.checkForMembers = True
						Exit
					Default
						token = token + Chr:String(char)
				End Select
			Next
			
			token = Right(token, Len(token) - 1)
			Local tempChar:String
			
			If ed.checkForMembers = True
				Local lex:TLexer = New TLexer
				Local strLine:String = ed.GetTextRange(iStart, loopLine)
				
				lex.mLexLine(strLine)
				objectToken = lex.sTokenBank[lex.iTokenAmount + 1]
				autoCompFireAmount = 0
			End If
			
			For Local Loop:Int = Len(token) To 1 Step - 1
				tempChar = Mid(token, Loop, 1)
				reversedToken = reversedToken + tempChar
			Next
			
			If ed.checkForMembers = True
				currentWords = ed.BuildAutoMemberList(reversedToken, objectToken, autoCompFireAmount)
			Else
				currentWords = ed.BuildAutoList(reversedToken, autoCompFireAmount)
			End If
			
			If currentWords <> ""
				Local tempString:String[] = currentWords.Split(" ") 
				tempString.Sort()
				currentWords = ""
				For Local remapList:String = EachIn tempString
					currentWords = currentWords + remapList + " "
				Next
				
				ed.AutoCompShow(Len(reversedToken), currentWords)
				ed.AutoCompSelect(reversedToken)
				
				If ed.checkForMembers = True
					gframe.SetStatusText("")
					For Local classes:TAutoCompContainer = EachIn classList
						If classes.className = objectToken
							For Local key:String = EachIn MapKeys(classes.fieldMap)
								Local autoIndex:Int = ed.AutoCompGetCurrent()
								If autoIndex <> - 1
									If autoIndex > 0 Then autoIndex = autoIndex + 1
									If key = tempString[autoIndex + 1]
										Local statusTip:String = Right(String(classes.fieldMap.ValueForKey(key)), Len(String(classes.fieldMap.ValueForKey(key))))
										gframe.SetStatusText(statusTip)
										Exit
									Else
										gframe.SetStatusText("")
									EndIf
								EndIf
							Next
						EndIf
					Next
				End If
				
				Return True
			End If
			
			
			Return False
		End Function

		Function StringArrayContains:Int(strArray:String[], matchString:String)
			For Local strLoop:Int = 0 To strArray.Length - 1
				If strArray[strLoop] = matchString Then Return True
			Next
			Return False
		End Function
		
		Function BuildAutoList:String(currentWord:String, fireBoundry:Int)
			Local ed:Edit = GetControl()
			
			Local autoWordList:String = ""
			currentWord = Lower(currentWord)
			If Len(currentWord) > fireBoundry
				'Add Functions to List
				For Local Loop:Int = 0 To Len(ed.moduleFunctionWords) - 1
					If Lower(Left(ed.moduleFunctionWords[Loop], Len(currentWord))) = currentWord
						autoWordList = autoWordList + (ed.moduleFunctionWords[Loop]) + " "
					EndIf
				Next
				
				'Add variables to List
				For Local key:String = EachIn MapKeys(variableMap)
					If Lower(Left(key, Len(currentWord))) = currentWord
						autoWordList = autoWordList + key + " "
					EndIf
				Next
				
			EndIf
			
			If autoWordList = ""
				ed.AutoCompCancel()
			EndIf
			
			Return autoWordList
		End Function
		
		Function BuildAutoMemberList:String(currentWord:String, objectToken:String, fireBoundry:Int)
			Local autoWordList:String = ""
			Local ed:Edit = GetControl()
			
			If Len(currentWord) > fireBoundry
				For Local classes:TAutoCompContainer = EachIn classList
					If classes.className = objectToken
							
						For Local key:String = EachIn MapKeys(classes.methodMap)
							If Len(currentWord) > 2
								If Left(key, Len(currentWord)) = currentWord
									autoWordList = autoWordList + (key + " ")
								EndIf
							Else
								autoWordList = autoWordList + (key + " ")
							EndIf
						Next
							
						For Local key:String = EachIn MapKeys(classes.fieldMap)
							If Len(currentWord) > 2
								If Left(key, Len(currentWord)) = currentWord
									autoWordList = autoWordList + (key + " ")
								EndIf
							Else
								autoWordList = autoWordList + (key + " ")
							EndIf
						Next
						Exit
					EndIf
				Next
			End If
			If autoWordList = ""
				ed.AutoCompCancel()
			EndIf
			
			Return autoWordList
		End Function
		
		Method OnSave()
			
		End Method
End Type
