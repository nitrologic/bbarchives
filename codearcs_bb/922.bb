; ID: 922
; Author: MPZ
; Date: 2004-02-06 09:26:35
; Title: What kind of OS?
; Description: Find the current OS with a WINAPI Call

; ONLY FOR THE TYPE VERSION--NUR FÜR DIE TYPE VERSION-------------
; 
; in dem USERLIBS Verzeichnis muß sich die kernel32.decls Datei befinden
; in the USERLIBS must be the file kernel32.decls
;.lib "kernel32.dll"
;api_GetVersionEx% (lpVersionInformation*) : "GetVersionExA"
;
; ONLY FOR THE TYPE VERSION--NUR FÜR DIE TYPE VERSION-------------


; ONLY FOR THE BANK VERSION--NUR FÜR DIE BANK VERSION-------------
; 
; in dem USERLIBS Verzeichnis muß sich die kernel32.decls Datei befinden
; in the USERLIBS must be the file kernel32.decls
;.lib "kernel32.dll"
;api_RtlMoveMemory(Destination*,Source,Length) : "RtlMoveMemory"
;api_GetVersionEx% (lpVersionInformation*) : "GetVersionExA"

; in dem USERLIBS Verzeichnis muß sich die comdlg32.decls Datei befinden
; in the USERLIBS must be the file comdlg32.decls
;.lib "comdlg32.dll"
;api_GetOpenFileName% (pOpenfilename*) : "GetOpenFileNameA"
;api_GetSaveFileName% (pOpenfilename*) : "GetSaveFileNameA"
;
; ONLY FOR THE BANK VERSION--NUR FÜR DIE BANK VERSION-------------



; WELCHES BETRIEBSSYTEM HAST DU? WHAT KIND OF OS DO YOU HAVE?
; Zwei verschiedene Möglichkeiten um das Betriebssystem zu mit WinApi Befehlen
; zu erkennen. Einmal als Type Befehl und einmal mit Bank Befehlen
;
; Michael Paulwitz () 1.2004
; Idee nachgebaut von http://www.activevb.de/tipps/vb6tipps/tipp0129.html
;
; Two different Version of OS recognition. One with Type, one with Bank
; Idee from the side http://www.activevb.de/tipps/vb6tipps/tipp0129.html

Type OSVERSIONINFO
  Field dwOSVersionInfoSize
  Field dwMajorVersion
  Field dwMinorVersion
  Field dwBuildNumber
  Field dwPlatformId
  Field szCSDVersion$
End Type

Print "Typeversion = "+GetVersiontype$ ()
Print "Bankversion = "+GetVersionbank$ ()

While Not KeyDown(1)
Wend
End


Function GetVersiontype$ ()
; Type Version der Betriebssystemerkennung
; Type Version of the OS Recognition


OS.OSVERSIONINFO = New OSVERSIONINFO 
OS\dwOSVersionInfoSize=148 
OS\szCSDVersion$=String$ (Chr$(0), 128)

api_GetVersionEx (OS)
VER_PLATFORM_WIN32s = 0 
VER_PLATFORM_WIN32_WINDOWS = 1 
VER_PLATFORM_WIN32_NT = 2

If (OS\dwBuildNumber And $FFFF) > $7FFF Then 
	BuildNr = (OS\dwBuildNumber And $FFFF) - $10000 
Else 
	BuildNr = OS\dwBuildNumber And $FFFF 
EndIf 

If OS\dwPlatformId = VER_PLATFORM_WIN32_NT Then 
	If OS\dwMajorVersion = 4 Then 
		OSString$ = "Windows NT" 
	ElseIf OS\dwMajorVersion = 5 Then 
		If OS\dwMinorVersion = 0 Then 
			OSString$ = "Windows 2000" 
		ElseIf OS\dwMinorVersion = 1 Then 
			OSString = "Windows XP" 
		EndIf
	EndIf 
ElseIf OS\dwPlatformId = VER_PLATFORM_WIN32_WINDOWS Then 
	If (OS\dwMajorVersion > 4) Or (OS\dwMajorVersion = 4 And OS\dwMinorVersion = 10) Then 
		If BuildNr = 1998 Then 
			OSString$ = "Windows 98" 
		Else 
			OSString$ = "Windows 98 SE" 
		EndIf 
	ElseIf (OS\dwMajorVersion = 4 And OS\dwMinorVersion = 0) Then 
		OSString$ = "Windows 95" 
	ElseIf (OS\dwMajorVersion = 4 And OS\dwMinorVersion = 90) Then 
		OSString$ = "Windows ME" 
	End If 
ElseIf OS\dwPlatformId = VER_PLATFORM_WIN32s Then 
	OSString$ = "Windows 32s" 
End If 
Return OSString$ 
End Function



Function GetVersionBank$ ()

; Bank Version der Betriebssystemerkennung
; Bank Version of the OS Recognition

nextOffset%=0 
theBank=CreateBank(148)
dwOSVersionInfoSize=148
PokeInt theBank,nextOffset%,dwOSVersionInfoSize
nextOffset%=nextOffset%+4 
		
dwMajorVersion=0
PokeInt theBank,nextOffset%,dwMajorVersion
nextOffset%=nextOffset%+4 
		
dwMinorVersion=0
PokeInt theBank,nextOffset%,dwMinorVersion
nextOffset%=nextOffset%+4 

dwBuildNumber=0
PokeInt theBank,nextOffset%,dwBuildNumber
nextOffset%=nextOffset%+4 
		
dwPlatformId=0
PokeInt theBank,nextOffset%,dwPlatformId
nextOffset%=nextOffset%+4 

szCSDVersion$=String$ (" ", 128)
szCSDVersion_ = CreateBank(Len(szCSDVersion$)) 
string_in_bank(szCSDVersion$,szCSDVersion_)
PokeInt theBank,nextOffset%,AddressOf(szCSDVersion_)

api_GetVersionEx (thebank)

nextOffset%=0 
dwOSVersionInfoSize = PeekInt (thebank,nextOffset%) 
nextOffset%=nextOffset%+4 

dwMajorVersion = PeekInt (thebank,nextOffset%) 
nextOffset%=nextOffset%+4 

dwMinorVersion = PeekInt (thebank,nextOffset%) 
nextOffset%=nextOffset%+4 

dwBuildNumber = PeekInt (thebank,nextOffset%) 
nextOffset%=nextOffset%+4 

dwPlatformId = PeekInt (thebank,nextOffset%) 


FreeBank theBank
FreeBank szCSDVersion_

VER_PLATFORM_WIN32s = 0 
VER_PLATFORM_WIN32_WINDOWS = 1 
VER_PLATFORM_WIN32_NT = 2

If (dwBuildNumber And $FFFF) > $7FFF Then 
	BuildNr = (dwBuildNumber And $FFFF) - $10000 
Else 
	BuildNr = dwBuildNumber And $FFFF 
EndIf 

If dwPlatformId = VER_PLATFORM_WIN32_NT Then 
	If dwMajorVersion = 4 Then 
		OSString$ = "Windows NT" 
	ElseIf dwMajorVersion = 5 Then 
		If dwMinorVersion = 0 Then 
			OSString$ = "Windows 2000" 
		ElseIf dwMinorVersion = 1 Then 
			OSString = "Windows XP" 
		EndIf
	EndIf 
ElseIf dwPlatformId = VER_PLATFORM_WIN32_WINDOWS Then 
	If (dwMajorVersion > 4) Or (dwMajorVersion = 4 And dwMinorVersion = 10) Then 
		If BuildNr = 1998 Then 
			OSString$ = "Windows 98" 
		Else 
			OSString$ = "Windows 98 SE" 
		EndIf 
	ElseIf (dwMajorVersion = 4 And dwMinorVersion = 0) Then 
		OSString$ = "Windows 95" 
	ElseIf (dwMajorVersion = 4 And dwMinorVersion = 90) Then 
		OSString$ = "Windows ME" 
	End If 
ElseIf dwPlatformId = VER_PLATFORM_WIN32s Then 
	OSString$ = "Windows 32s" 
End If 
Return OSString$ 
End Function


Function AddressOf(Bank) ; Find the correct Adress of a Bank (for C *Pointer)
	Local Address = CreateBank(4) 
	api_RtlMoveMemory(Address,Bank+4,4) 
	Return PeekInt(Address,0) 
End Function

Function string_in_bank(s$,bankhandle) ; Put a String in a Bank
	Local pos=1
	Local pos2=0
	Repeat
		PokeByte(bankhandle,pos2,Asc(Mid(s$,pos,Len(s$))))
		pos=pos+1
		pos2=pos2+1
	Until pos=Len(s$)+1
End Function

Function bank_in_string$(bankhandle) ; Get a String from a Bank
	Local s$=""
	Local pos=0
	Repeat
		s$=s$+Chr(PeekByte(bankhandle,pos))
		pos=pos+1
	Until pos=BankSize(bankhandle)
	s$=Replace$(s$,Chr(0)," ")
	Return s$
End Function
