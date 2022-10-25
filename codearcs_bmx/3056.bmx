; ID: 3056
; Author: BlitzSupport
; Date: 2013-06-04 20:44:28
; Title: Get default browser path (Windows only)
; Description: Retrieves path to system default browser on Windows systems

' Some registry references to common browsers:

' Note that, for most web browsers, this string can be found in the registry under:

' HKEY_LOCAL_MACHINE\SOFTWARE\Clients\StartMenuInternet\WEB_BROWSER\Capabilities\FileAssociations\.html

' ... hence it should be fairly easy to check for other browsers by passing this string to GetBrowserPath...

Const IE:String			= "IE.HTTP"
Const FIREFOX:String	= "FirefoxHTML"
Const CHROME:String		= "ChromeHTML"
Const OPERA:String		= "Opera.HTML"
Const OPERANEXT:String	= "Operanext"

Extern "win32"

	Const ERROR_SUCCESS:Int			= 0

	Const HKEY_CLASSES_ROOT:Int		= $80000000
	Const HKEY_CURRENT_USER:Int		= $80000001
	Const HKEY_LOCAL_MACHINE:Int	= $80000002
	Const HKEY_USERS:Int			= $80000003
	Const HKEY_CURRENT_CONFIG:Int	= $80000005
	Const KEY_READ:Int				= $20019

	Function RegOpenKeyExA (hKey:Int, lpSubKey:Byte Ptr, ulOptions:Int, samDesired:Int, phkResult:Byte Ptr)
	Function RegQueryValueExA (hKey:Int, lpValueName:Byte Ptr, lpReserved:Byte Ptr, lpType:Byte Ptr, lpData:Byte Ptr, lpcbData:Byte Ptr)
	Function RegCloseKey (hKey:Int)

End Extern

Function GetBrowserPath:String (force:String = "")

	' Can pass "default" or "" for default browser...
	
	If Lower (force) = "default"
		force = ""
	EndIf
	
	Local browser:Byte []	' C string to receive browser path
	Local key:Int			' Registry key handle (re-used repeatedly)
	
	' Find user choice browser ID...
	
	If RegOpenKeyExA (HKEY_CURRENT_USER, "Software\Microsoft\Windows\CurrentVersion\Explorer\FileExts\.html\UserChoice".ToCString (), 0, KEY_READ, Varptr key) = ERROR_SUCCESS
	
		If key
		
			Local size:Int
			
			If RegQueryValueExA (key, "Progid".ToCString (), Null, Null, Null, Varptr size) = ERROR_SUCCESS
				Local classname:Byte [size]
				RegQueryValueExA (key, "Progid".ToCString (), Null, Null, classname, Varptr size)
			EndIf
		
			RegCloseKey key
		
			Local browserclassname:String = String.FromCString (classname)
			
			' Got it, but caller wants to override to retrieve specific browser path...
			
			If force
				browserclassname = force
			EndIf
			
			' Use browser ID to get path...
			
			If RegOpenKeyExA (HKEY_CLASSES_ROOT, (String.FromCString (browserclassname) + "\shell\open\command").ToCString (), 0, KEY_READ, Varptr key) = ERROR_SUCCESS
				If RegQueryValueExA (key, "".ToCString (), Null, Null, Null, Varptr size) = ERROR_SUCCESS
					browser = New Byte [size]
					RegQueryValueExA (key, "".ToCString (), Null, Null, browser, Varptr size)
				EndIf
				RegCloseKey key
			EndIf
			
			Local defaultbrowser:String = String.FromCString (browser)
			Local quote2:Int	' Used to find second quote in browser string...
			
			' Usually "path/subpath/browser.exe" plus some extra crap. Just want browser in quotes...
			
			If defaultbrowser
				quote2 = Instr (defaultbrowser, Chr (34), 2) - 1
				defaultbrowser = Chr (34) + Mid (defaultbrowser, 2, quote2 - 1) + Chr (34)
			Else
			
				' Still nothing? Try generic http handler instead...
				
				If RegOpenKeyExA (HKEY_CLASSES_ROOT, "http\shell\open\command".ToCString (), 0, KEY_READ, Varptr key) = ERROR_SUCCESS
					If RegQueryValueExA (key, "".ToCString (), Null, Null, Null, Varptr size) = ERROR_SUCCESS
						browser = New Byte [size]
						RegQueryValueExA (key, "".ToCString (), Null, Null, browser, Varptr size)
					EndIf
					RegCloseKey key
				EndIf

				defaultbrowser = String.FromCString (browser)

				' Strip junk...
				
				If defaultbrowser
					quote2 = Instr (defaultbrowser, Chr (34), 2) - 1
					defaultbrowser = Chr (34) + Mid (defaultbrowser, 2, quote2 - 1) + Chr (34)
				EndIf

			EndIf
			
			Return defaultbrowser
			
		EndIf
	
	EndIf

End Function

' D E M O . . .

Print ""
Print "Checking for default browser..."
Print ""

	Print "~tDefault: " + GetBrowserPath () ' or "Default" or ""

Print ""
Print "Checking for a few common browsers..."
Print ""

	Print "~tInternet Explorer: "	+ GetBrowserPath (IE)
	Print "~tFirefox: "		+ GetBrowserPath (FIREFOX)
	Print "~tChrome: "		+ GetBrowserPath (CHROME)
	Print "~tOpera: "		+ GetBrowserPath (OPERA)
	Print "~tOpera Next: "		+ GetBrowserPath (OPERANEXT)

' Done!
