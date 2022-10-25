; ID: 2983
; Author: BlitzSupport
; Date: 2012-10-09 14:02:52
; Title: Windows version detection
; Description: Detects and prints Windows version

Type OSVERSIONINFOEX
	Field dwOSVersionInfoSize:Int
	Field dwMajorVersion:Int
	Field dwMinorVersion:Int
	Field dwBuildNumber:Int
	Field dwPlatformId:Int
	Field szCSDVersion:Int[32] ' 128 bytes
	Field wServicePackMajor:Short
	Field wServicePackMinor:Short
	Field wSuiteMask:Short
	Field wProductType:Byte
	Field wReserved:Byte
End Type

Const SIZEOF_OSVERSIONINFOEX:Int = 156

Const VER_NT_WORKSTATION:Int = 1
Const VER_NT_DOMAIN_CONTROLLER:Int = 2
Const VER_NT_SERVER:Int = 3

Extern "win32"
	Function GetVersionExA:Int (version:Byte Ptr)
	Function GetSystemMetrics:Int (index:Int)
	Function GetSystemInfo:Int (info:Byte Ptr)
End Extern

Local version:OSVERSIONINFOEX = New OSVERSIONINFOEX
version.dwOSVersionInfoSize = SIZEOF_OSVERSIONINFOEX

If GetVersionExA (version)

	' Using table at http://msdn.microsoft.com/en-us/library/windows/desktop/ms724833(v=vs.85).aspx

	Select version.dwMajorVersion
	
		' NB. The REM'd comments refer to alternative server versions. See above URL for
		' PARTIAL method of sub-detection (doesn't cover all variants, eg. 32/64)...
		
		Case 5
			
			Select version.dwMinorVersion
				Case 0
					Print "Windows 2000"
				Case 1
					Print "Windows XP"
				Case 2
					Print "Windows XP 64-bit"	' and Server 2003/Server 2003 R2/Home Server"
			End Select
			
		Case 6
		
			Select version.dwMinorVersion
				Case 0
					Print "Windows Vista"		' and Server 2008
				Case 1
					Print "Windows 7"			' and Windows Server 2008 R2
				Case 2
					Print "Windows 8"			' and Windows Server 2012
			End Select
			
	End Select
	
EndIf
