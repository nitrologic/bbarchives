; ID: 2982
; Author: col
; Date: 2012-10-09 04:10:03
; Title: Modern RequestDir :- RequestFolder
; Description: Use for a more modern look when browsing folders on Vista/Win7

Strict

?Win32

Import "-lole32"
Import Pub.Win32

Global Shell32Dll = LoadLibraryA("Shell32.dll")
Global SHCreateItemFromParsingName(pszPath$w,pbc:Byte Ptr,riid:Byte Ptr,ppv:IUnknown Var)"Win32" = GetProcAddress(Shell32Dll,"SHCreateItemFromParsingName")

Global CLSID_FileOpenDialog[] = [$dc1c5a9c,$4ddee88a,$f860a1a5,$f7ae202a]
Global IID_IFileOpenDialog[] = [$d57c7288,$4768d4ad,$969d02be,$60d93295]
Global IID_IShellItem[] = [$43826d1e,$42eee718,$e2a155bc,$fe7bc361]


Extern"Win32"
	'These types are INCOMPLETE - DO NOT USE FOR ANYTHING ELSE !!!!!!
	Type IModalWindow Extends IUnknown
		Method Show(hWnd)
	EndType

	Type IFileDialog Extends IModalWindow
		Method SetFileTypes()
		Method SetFileTypeIndex()
		Method GetFileTypeIndex()
		Method Advise()
		Method Unadvise()
		Method SetOptions(dwOptions)
		Method GetOptions(dwOptions Ptr)
		Method SetDefaultFolder(pShellItem:Byte Ptr)
		Method SetFolder(pSI:Byte Ptr)
		Method GetFolder()
		Method GetCurrentSelection()
		Method SetFilename(pszName$w)
		Method GetFileName()
		Method SetTitle(pszName$w)
		Method SetOKButtonLabel()
		Method SetFilenameLabel()
		Method GetResult(pItem:IShellItem Var)
		Method AddPlace()
		Method SetDefaultExtension()
		Method Close()
		Method SetClientGuid()
		Method ClearClientData()
		Method SetFilter()
	EndType
	
	Type IShellItem Extends IUnknown
		Method BindToHandler()
		Method GetParent()
		Method GetDisplayName(sigdnName,ppszName:Short Ptr Var)
		Method GetAttributes()
		Method Compare()
	EndType
	
	Function CoCreateInstance(rclsid:Byte Ptr,pUnkOuter:Byte Ptr,dwClsContext,riid:Byte Ptr,ppv:IUnknown Var) 'My version
	Function CoInitialize(pvReserved)
	Function CoUninitialize()
EndExtern

Function RequestFolder$(Title$,InitialPath$)
	Global pDialog:IFileDialog
	Global pInitialPath:IShellItem
	Global pFolder:IShellItem
	Global hr,ResultFolder$

	CoInitialize(0)

	'Create Instance of the Dialog	
	hr = CoCreateInstance(CLSID_FileOpenDialog,Null,CLSCTX_INPROC_SERVER,IID_IFileOpenDialog,pDialog)

	'Not on Vista or Win7?
	If hr < 0 CoUninitialize(); Return RequestDir(Title,InitialPath)
	
	'Set it to Browse Folders
	Local dwOptions
	pDialog.GetOptions(Varptr dwOptions)
	pDialog.SetOptions(dwOptions|$20)
	
	'Set Title
	pDialog.SetTitle(Title)
	
	'Create an IShellItem for a default folder path
	InitialPath = Replace(InitialPath,"/","\")
	SHCreateItemFromParsingName(InitialPath,Null,IID_IShellItem,pInitialPath)
	
	If pDialog.SetFolder(pInitialPath) < 0
		CleanUp()
		Return RequestDir(Title,InitialPath)
	EndIf
		
	'Show the Dialog
	pDialog.Show(0)

	'Test the result
	If pDialog.GetResult(pFolder) < 0
		CleanUp
		Return ""
	EndIf
	
	'Get the result
	Local pName:Short Ptr
	pFolder.GetDisplayName($80058000,pName)
	ResultFolder = String.FromWString(pName)
	
	CleanUp()
	CoUninitialize()
	
	Return ResultFolder
	
	Function CleanUp()
		If pDialog pDialog.Release_()
		If pInitialPath pInitialPath.Release_()
		If pFolder pFolder.Release_()
	EndFunction
EndFunction
?

'Example Usage
Print RequestFolder("Select a Folder...","d:\BlitzMax")
