; ID: 1789
; Author: gellyware
; Date: 2006-08-19 20:53:02
; Title: Armadillo/Software Passport Functions
; Description: Functions to get information from an Armadillo/Software Passport Protected EXE

'*********************************************************************
' ARMADILLO ACCESS FUNCTIONS
' Compiled by: Gellyware - 2006
'*********************************************************************


'*********************************************************************
'get environment variable
'*********************************************************************

Extern "Win32"
   Function GetEnvironmentVariable(lpName$z, lpBuffer:Byte Ptr, nSize) = "GetEnvironmentVariableA@12" 
End Extern

Function GetEnv$(envVar$)
  Local buff:Byte[256]
  
  Local rtnLen = GetEnvironmentVariable(envVar$, buff, buff.length) 
  If rtnLen > (buff.length - 1)
    buff = buff[..rtnLen]
    rtnLen = GetEnvironmentVariable(envVar$, buff, buff.length)
  EndIf
  
  Return String.FromBytes(buff, rtnLen)
End Function

'*********************************************************************
'armaccess.dll functions
'*********************************************************************
Global Libary=LoadLibraryA("ArmAccess.dll")
Global CheckCode( Name:String, Code:String )"win32"=getprocaddress(Libary,"CheckCode")
Global VerifyKey( Name:String, Code:String )"win32"=getprocaddress(Libary,"VerifyKey")
Global InstallKey( Name:String, Code:String )"win32"=getprocaddress(Libary,"InstallKey")
Global InstallKeyLater( Name:String, Code:String )"win32"=getprocaddress(Libary,"InstallKeyLater")
Global UninstallKey()"win32"=getprocaddress(Libary,"UninstallKey")
Global SetDefaultKey()"win32"=getprocaddress(Libary,"SetDefaultKey")
Global UpdateEnvironment()"win32"=getprocaddress(Libary,"UpdateEnvironment")
Global IncrementCounter()"win32"=getprocaddress(Libary,"IncrementCounter")
Global CopiesRunning()"win32"=getprocaddress(Libary,"CopiesRunning")
Global ChangeHardwareLock()"win32"=getprocaddress(Libary,"ChangeHardwareLock")
Global GetShellProcessID()"win32"=getprocaddress(Libary,"GetShellProcessID")
Global FixClock( FixClockKey:String )"win32"=getprocaddress(Libary,"FixClock")
Global RawFingerprintInfo( Item:Int )"win32"=getprocaddress(Libary,"RawFingerprintInfo")
Global SetUserString( Which:Int, Str:String )"win32"=getprocaddress(Libary,"SetUserString")
Global VBGetUserString( Which:Int )"win32"=getprocaddress(Libary,"VBGetUserString")
Global WriteHardwareChangeLog( FileName:String )"win32"=getprocaddress(Libary,"WriteHardwareChangeLog")
Global ConnectedToServer()"win32"=getprocaddress(Libary,"ConnectedToServer")
Global CallBuyNowURL( ParentWindow:Int )"win32"=getprocaddress(Libary,"CallBuyNowURL")
Global CallCustomerServiceURL( ParentWindow:Int )"win32"=getprocaddress(Libary,"CallCustomerServiceURL")
Global ShowReminderMessage( ParentWindow:Int )"win32"=getprocaddress(Libary,"ShowReminderMessage")
Global ShowReminderMessage2( ParentWindow:Int )"win32"=getprocaddress(Libary,"ShowReminderMessage2")
Global ShowEnterKeyDialog( ParentWindow:Int )"win32"=getprocaddress(Libary,"ShowEnterKeyDialog")
Global ExpireCurrentKey()"win32"=getprocaddress(Libary,"ExpireCurrentKey")


'*********************************************************************
'KEY INFORMATION
'*********************************************************************

Function getAltUserName$()
	Rem
	This is the name on the user's key, or the string "DEFAULT" 
	(without the quotation marks) If they're using the default certificate. Same as USERNAME. 
	Possible values: 
   	The name that the currently-installed key was made For, Or "DEFAULT". 
	EndRem 
	
	Return GetEnv("ALTUSERNAME")
EndFunction 

Function getCopiesAllowed$()
	Rem 
	If your program uses the Limit Copies feature, this variable is set to the number of 
	copies permitted for the current certificate and key. 

	Possible values: 
   The number of copies permitted by the current certificate and key, if the current 
	certificate uses the Limit Copies option. Otherwise NULL. 
	EndRem 
	
	Return GetEnv("COPIESALLOWED") 
EndFunction 

Function getDaysInstalled$()
	Rem
	This variable contains the number of days since the current key was installed. 

	Possible values: 
    The decimal value of the number of days the current key has been installed. 
	EndRem 
	
	Return GetEnv("DAYSINSTALLED") 
EndFunction 

Function getDaysInstalledBitx$()
	Rem
	This is actually fifteen different variables, 
	DAYSINSTALLEDBIT1 through DAYSINSTALLEDBIT15. 
	They contain the number of days since the corresponding 
	"extra information" bit was last changed, and can be used with 
	Modification Keys to set up a trial period for separate modules in your program. 
	
	Note: although SoftwarePassport/Armadillo now supports 32 extra-info bits, 
	only bits 1 through 15 have a days-installed entry. This is by design. 
	
	Possible values: 
	The decimal value of the number of days the current key has been installed. 
	EndRem 
	
	Return GetEnv("DAYSINSTALLEDBITx") 
EndFunction 

Function getExtraInfo$()
	Rem
	Contains any user-defined "extra information" you have placed in the key, in decimal format. 

	Possible values: 
    The user-defined "extra information" stored in the key, if any. Otherwise "0". 
	EndRem 
	
	Return GetEnv("EXTRAINFO") 
EndFunction 

Function getExtraInfoBits$()
	Rem
	This stores the same information as the EXTRAINFO variable, but in binary format. 
	The least-significant bit (bit 1) is the first character of the string; the last 
	character is the most-significant bit (bit 32). If the bit is set (on), the character 
	in the corresponding location will be a one ("1"); otherwise, it will be a zero ("0"). 
	This string will always be thirty-two characters long, regardless of the information 
	stored in it. It is designed for those languages that don't have easy access to 
	bit-manipulation operators. 

	Possible values: 
    "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx", where each "x" can be a zero character or a one character. 
	EndRem 
	
	Return GetEnv("EXTRAINFOBITS") 
EndFunction 

Function getKeyCreated$()
	Rem
	This contains the date that the currently-installed key was created on. 

	Possible values: 
	The date, in the form "YYYY.MM.DD", that the currently-installed key was created on, 
	or NULL if the program is using the default certificate. 
	EndRem 
	
	Return GetEnv("KEYCREATED") 
EndFunction 

Function getUserKey$()
	Rem
    This is the key that the program is currently running under. It is not set for the default key. 

    Possible values: 
    The key that the program is currently using, if one was entered. 
    "CLIENT" if this is a client copy of a client/server Network Licensing setup. 
    NULL if neither of those applies. 
	EndRem 
	
	Return GetEnv("USERKEY") 
EndFunction 

Function getSeverKey$()
	Rem
	The USERKEY environment variable is set to "CLIENT" for the client side of a client/server 
	Network Licensing system. If you want to have access to the server's key on such a system, 
	you can use the SERVERKEY environment variable instead. It's not set unless USERKEY is "CLIENT". 

	Possible values: 
    The key used on the server copy, or NULL if this is not a client copy of the program. 
	EndRem 
	
	Return GetEnv("SERVERKEY") 
EndFunction 

Function getSeverAdress$()
	Rem
	On the client systems of a client/server Network Licensing system, the SERVERADDRESS 
	environment variable contains the IP address of the server. 

	Possible values: 
    The IP address, in dotted-decimal format, of the server (if this is a client copy). Otherwise NULL. 
	EndRem 
	
	Return GetEnv("SERVERADDRESS") 
EndFunction 

Function getStolenKey$()
	Rem
	When a key is entered or was previously entered, and is found to be in the Stolen Codes 
	Database, the program sets the STOLENKEY environment variable to that key. This variable 
	remains set until the program exits, or until a new key is successfully installed. 

	Possible values: 
   	The stolen key that was entered or found on the system, or NULL if no stolen key was found. 
	EndRem 
	
	Return GetEnv("STOLENKEY") 
EndFunction 

Function getTotalUses$()
	Rem
	This variable contains the total number of times that the program has been started since 
	the current key was installed. It is incremented every time the program is started. 

	Possible values: 
    The number of times the program has been started since the current key was installed. 
	Note that this will start at zero if the key was installed during the current run, but 
	that installing a key via the INI file method is done before the count is incremented, 
	so it will start at one in that case. 
	EndRem 
	
	Return GetEnv("TOTALUSES") 
EndFunction 

Function getTotalUsesAllKeys$()
	Rem
	Similar to TOTALUSES, but doesn't get reset when a key is installed. 

	Possible values: 
    The total number of times the program has been started since it was installed. 
	EndRem 
	
	Return GetEnv("TOTALUSESALLKEYS") 
EndFunction 

Function getUserName$()
	Rem
	This is the name on the user's key, or the string "DEFAULT" 
	(without the quotation marks) if they're using the default certificate. 

	Please note that Windows also sets this variable, to the name of the 
	logged-in user. SoftwarePassport/Armadillo will override this for protected programs, 
	but because of this, you cannot use the existence of USERNAME as an indication that 
	he program is protected. For this reason, the environment variable ALTUSERNAME carries the same information. 

	Possible values: 
    The name that the currently-installed key was made for, or "DEFAULT". 
	EndRem 
	
	Return GetEnv("USERNAME") 
EndFunction 

'*********************************************************************
'EXPIRATION 
'*********************************************************************

Function getDaysLeft$()
	Rem
	For date- and day-limited certificates, this is set to the number of valid days left 
	on the key. When this is set to 1, the key will expire at midnight. 
	
	Possible values: 
	The number of days remaining on the certificate, or NULL if the certificate is not 
	date- or days-limited. 
	EndRem 
	
	Return GetEnv("DAYSLEFT") 
EndFunction 

Function getExpired$()
	Rem
	This variable is set if you've chosen the "don't show expiration message" option and the 
	certificate is expired, if the key entered via ArmAccess.DLL's functions is already expired, 
	or if the current key becomes expired after a call to an ArmAccess.DLL function. 
	
	SoftwarePassport/Armadillo can only automatically stop the program from running if the key 
	is expired when the program starts up; if the key expires after that, and the program is 
	still running, it will continue to run unless you check for the existence of this variable, 
	and take action if it's set. 
	
	Possible values: 
	"True" if the certificate is expired, otherwise NULL. 
	EndRem 
	
	Return GetEnv("EXPIRED") 
EndFunction 

Function getExpiredDate$()
	Rem
	For expire-by-date certificates (only), this is set to the date the certificate expires on. 
	
	Possible values: 
	The date the expire-by-date certificate expires on, in YYYY.MM.DD format, or NULL 
	if the certificate is not expire-by-date. 
	EndRem 
	
	Return GetEnv("EXPIREDATE") 
EndFunction 

Function getExpiredEver$()
	Rem
	For expire-by-version certificates, this tells you what version the key will expire on. 
	
	Possible values: 
	The decimal value of the expiration version, with two digits after the decimal point, 
	if the current certificate is expire-by-version, otherwise NULL. Example: "3.75". 
	EndRem 
	
	Return GetEnv("EXPIREVER") 
EndFunction 

Function getTimeLeftNow$()
	Rem
	If you set the expire-by-minutes per-run timer, the TIMELEFTNOW environment variable 
	is set to the amount of time left in this run, in the form HH:MM:SS, where HH is the 
	number of hours (one or two digits), MM is the number of minutes (always two digits), 
	and SS is the number of seconds (again, always two digits). 
	
	Possible values: 
	The amount of time left during this run, in hours, minutes, and seconds, or NULL if 
	the per-run timer isn't used in this certificate. 
	EndRem 
	
	Return GetEnv("TIMELEFTNOW") 
EndFunction 

Function getTimeLeftTotal$()
	Rem
	If you set the expire-by-minutes total-time timer, the TIMELEFTTOTAL environment 
	variable is set to the total amount of time left on the timer, in the form HH:MM:SS, 
	where HH is the number of hours (one, two, or three digits), MM is the number of minutes 
	(always two digits), and SS is the number of seconds (again, always two digits). 
	
	Possible values: 
	The amount of time left on the timer, in hours, minutes, and seconds, or NULL if 
	the total-time timer isn't used in this certificate. 
	EndRem 
	
	Return GetEnv("TIMELEFTTOTAL") 
EndFunction 

Function getUsesLeft$()
	Rem
	For certificates offering a limited number-of-uses, this is set to the number of 
	uses remaining. If the user is on the last use, this is set to 1. 
	
	Possible values: 
	The number of uses remaining, in decimal format, if the certificate is one of 
	the expire-by-uses ones. Otherwise NULL. 
	EndRem 
	
	Return GetEnv("USESLEFT") 
EndFunction 

Function getUsesLeftAfter$()
	Rem
	This will always be equal to USESLEFT minus one, and is intended for use in the 
	show-after Reminder Message. 
	
	Possible values: 
	The number of uses remaining minus one, in decimal format, if the certificate is 
	one of the expire-by-uses ones. Otherwise NULL. 
	EndRem 
	
	Return GetEnv("USESLEFTAFTER") 
EndFunction 

'*********************************************************************
'CLOCK PROBLEMS
'*********************************************************************

Function getClockBack$()
	Rem
	If you have chosen To disable SoftwarePassport/Armadillo's clock-back checking 
	(by way of the Don't Report Clock-Back option), this variable will be set when 
	SoftwarePassport/Armadillo detects a system clock that has been turned back. 
	
	Possible values: 
	"True" If the clock has been set back, otherwise NULL. 
	EndRem 
	
	Return GetEnv("CLOCKBACK")
EndFunction

Function getClockForward$()
	Rem
	Similar To CLOCKBACK. If you have chosen To disable the clock-forward checking 
	(using the Don't Report Clock-Forward option), this variable will be set when 
	SoftwarePassport/Armadillo detects a system clock that has been set to a date 
	in the future. Please note that SoftwarePassport/Armadillo allows for several weeks' 
	leeway before reporting a problem. 

	Possible values: 
    "True" If the clock has been set forward, otherwise NULL. 
	EndRem 
	
	Return GetEnv("CLOCKFORWARD")
EndFunction

Function getClockWhy$()
	Rem
	When CLOCKBACK Or CLOCKFORWARD are set, CLOCKWHY contains the reason-code that would 
	normally be shown on the error screen. 

	Possible values: 
   "CCB-A" (For CLOCKFORWARD only) 
   "CCB-F" (CLOCKBACK) 
   "CCB-TT" (special) 
   "GDDM" (same as CCB-F, but For Limit Install Time keys) 
   "xxxx,yyyy,zzzz" (where x, y, And z are numbers; CLOCKBACK) 
    Null (If there is no clock problem) 

	If you need To know the meaning of a particular code, please contact us by e-mail 
	at support@siliconrealms.com. 
	EndRem
	
	Return GetEnv("CLOCKWHY")
EndFunction


'*********************************************************************
'HARDWARE FINGERPRINTS
'*********************************************************************
Function getEnhFingerprint$()
	Rem
	Similar to the FINGERPRINT variable, this one holds the enhanced hardware-locking 
	fingerprint, for certificates that use enhanced hardware locking. 
	
	Possible values: 
	The 32-bit Enhanced hardware fingerprint, in hexadecimal format, separated into two 
	four-digit groups by a dash. Example: "A1B2-C3D4". 
	EndRem 
	
	Return GetEnv("ENHFINGERPRINT") 
EndFunction 

Function getFingerPrint$()
	Rem
	This variable contains the eight-digit hexadecimal "machine fingerprint" of the 
	computer your program is currently running on, which is necessary to make a 
	hardware-locked key. This is the Standard fingerprint value; for the Enhanced 
	fingerprint value, see ENHFINGERPRINT. 
	
	Possible values: 
	The 32-bit Standard hardware fingerprint, in hexadecimal format, separated into 
	two four-digit groups by a dash. Example: "A1B2-C3D4". 
	EndRem 
	
	Return GetEnv("FINGERPRINT") 
EndFunction 

'*********************************************************************
'MISCELANEOUS OR SPECIAL PURPOSE
'*********************************************************************

Function getCommandLine$()
	Rem
	When you have enabled the Allow Only One Copy option, and a user tries to start 
	a second copy of your program, the raw command line of the second copy is passed 
	to your program via this variable. Please see the description of the Allow Only One 
	Copy option for details. 
	
	Possible values: 
	The command line arguments of the second copy. 
	EndRem 
	
	Return GetEnv("COMMANDLINE") 
EndFunction 

Function getControlPID$()
	Rem
	When using CopyMem-II or the Debugger-Blocker, this variable will contain the program 
	ID of the SoftwarePassport/Armadillo control process for your program (the number that 
	is normally returned by the GetCurrentProcessId() API call), in decimal format. This is 
	sometimes needed to handle DDE messages. 
	
	Possible values: 
	This variable will either contain the ProcessID of the control process 
	(if using the Debugger-Blocker or CopyMem-II settings) or the ProcessID of the current process. 

	EndRem 
	
	Return GetEnv("CONTROLPID") 
EndFunction 

Function getDateLastRun$()
	Rem
	Contains the date that this program was last run on this system. 
	The first time it's run, this will contain the current date. 
	
	Possible values: 
	The date of the last time the program was run, in YYYY.MM.DD format. 
	EndRem 
	
	Return GetEnv("DATELASTRUN") 
EndFunction 

Function getDr_Tagged$()
	Rem
	If your program is tagged for use with the Digital River Network, this environment 
	variable will be set. 
	
	Possible values: 
	"True" or NULL. 
	EndRem 
	
	Return GetEnv("DR_TAGGED") 
EndFunction 

Function getEmulator$()
	Rem
	If your protected program is running under the VMware or VirtualPC emulators, 
	the EMULATOR environment variable will be set. Your program can use this to refuse 
	to run under such emulators, if desired. 
	
	Possible values: 
	"VMware", "VirtualPC", or "VirtualPC/Mac" (without the quotation marks) if your 
	program is running under one of those emulators. Otherwise NULL. 
	EndRem 
	
	Return GetEnv("EMULATOR") 
EndFunction 

Function getFirstRun$()
	Rem
	This variable is set on the first run of your program. Your program can use it to 
	initialize things that are only set on the first run, or to show dialogs that only 
	need to be shown once. 
	
	Possible values: 
	"True" on the first run of your program on a particular machine, otherwise NULL. 

	EndRem 
	
	Return GetEnv("FIRSTRUN") 
EndFunction 

Function getInvalidKey$()
	Rem
	This variable is used if you have the "Auto-revert on invalid key" option set, and the key 
	stored on a user's system is invalid (probably because you've changed an encryption template 
	(this is referred to as encryption key in the SoftwarePassport GUI) or disabled or removed 
	the certificate entirely). It is only set during the first such run, and the program will 
	automatically revert to the default certificate, if any. This is intended to let your program 
	gracefully warn the user of the change. 
	
	It is also now set if the key was found in the Stolen Codes Database. 
	
	Possible values: 
	"True" if the previously-installed key was invalid or discovered in the Stolen Codes Database, 
	otherwise NULL. 
	EndRem 
	
	Return GetEnv("INVALIDKEY") 
EndFunction 

Function getLoadingWindow$()
	Rem
	If you use a custom bitmap splash screen window, this variable holds the handle of 
	this window. You can use this handle to hide the splash screen window when your program 
	starts up, if you don't wish to rely on SoftwarePassport/Armadillo's timer (you can't close 
	it directly, because it's opened by a different thread). 
	
	Possible values: 
	The value of the window handle, in hexadecimal format, if your program has a splash screen. 
	Otherwise NULL. 
	EndRem 
	
	Return GetEnv("LOADINGWINDOW") 
EndFunction 

Function getProtectedFile$()
	Rem
	This variable holds the path and filename of the protected file. Not really needed anymore, but it remains for compatibility. 
	
	Possible values: 
	The full path and filename of the protected file. 
	EndRem 
	
	Return GetEnv("PROTECTEDFILE") 
EndFunction 

Function getProtectedFilePath$()
	Rem
	This is the directory path to the protected file, without the filename. 
	For example, if the protected program (stored in PROTECTEDFILE) is 
	"C:\Program Files\My Program\My Program.exe", then the PROTECTEDFILEPATH will be 
	"C:\Program Files\My Program\". This is primarily here for backwards compatibility, 
	but can also be used in HTML messages to locate graphics files. 
	
	Possible values: 
	The full directory path to the protected file, without the filename. It always ends 
	in a back-slash. 
	EndRem 
	
	Return GetEnv("PROTECTEDFILEPATH") 
EndFunction 

Function getVersionNumber$()
	Rem
	If you define a version (on the Edit Project screen), SoftwarePassport/Armadillo will put that 
	version number in this variable. 
	
	Possible values: 
	The version number you've entered on the Edit Project screen, if any, in decimal format with 
	two digits after the decimal point. Otherwise NULL. 
	EndRem 
	
	Return GetEnv("VERSIONNUMBER") 
EndFunction 

Function getWebsiteUrl$()
	Rem
	This contains the URL you've entered into the Website/Buy Now box in 
	SoftwarePassport/Armadillo, if any. 
	
	Possible values: 
	The URL in the Website/Buy Now box for the current certificate, if any. Otherwise NULL. 
	EndRem 
	
	Return GetEnv("WEBSITEURL") 
EndFunction
