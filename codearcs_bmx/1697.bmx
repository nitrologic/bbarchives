; ID: 1697
; Author: Nilium
; Date: 2006-05-05 19:22:38
; Title: GetRegistry
; Description: Function to get a registry entry

SuperStrict

?Win32
Import "-ladvapi32" ' Doesn't seem to be imported by default

Const HKEY_CLASSES_ROOT%            =$80000000
Const HKEY_CURRENT_USER%            =$80000001
Const HKEY_LOCAL_MACHINE%           =$80000002
Const HKEY_USERS%                   =$80000003
Const HKEY_PERFORMANCE_DATA%        =$80000004
Const HKEY_CURRENT_CONFIG%          =$80000005
Const HKEY_DYN_DATA%                =$80000006'Win98

Extern "OS"
    Function RegQueryValueExW%( key%, valname$w, reserved@ Ptr, _type% Var, data@ Ptr, dataSize% Var )
    Function RegOpenKeyExW%( key%, name$w, zero%, mode%, outkey% Var )
    Function RegCloseKey%( key% )
End Extern

Function GetRegistry$( name$ )
    Local buf@[256], bufsize%, typ%
    Local dir$ = StripSlash(Extractdir( name )).Replace("/","\").ToLower( ), base% ' not case sensitive
    Select name[..dir.Find("\")]
        Case "hkey_classes_root"
            base = HKEY_CLASSES_ROOT
        Case "hkey_local_machine"
            base = HKEY_LOCAL_MACHINE
        Case "hkey_users"
            base = HKEY_USERS
        Case "hkey_performance_data"
            base = HKEY_PERFORMANCE_DATA
        Case "hkey_current_config"
            base = HKEY_CURRENT_CONFIG
        Case "hkey_dyn_data"        ' Win98 - for the sake of compatibility you should NOT access this
            base = HKEY_DYN_DATA
        Default
            base = HKEY_CURRENT_USER
    End Select
    dir = dir[dir.Find("\")+1..]
    
    bufsize = SizeOf(buf)
    Local key%
    Local e% = RegOpenKeyExW( base, dir, 0, $20019, key )
    If e <> 0 Then
        DebugLog "Registry Error: "+e
        Return ""
    EndIf
    e = RegQueryValueExW( key, StripDir(name), Null, typ, buf, bufsize )
    RegCloseKey( key )
    If e <> 0 Then
        DebugLog "Registry Error: "+e
        Return ""
    EndIf
    Select typ
        Case 1,2,7
            Return String.FromShorts( Short Ptr(Varptr buf[0]), (bufsize-1)/2 )
        Case 4
            Return (Int Ptr(Varptr buf[0]))[0]
        Case 11
            Return (Long Ptr(Varptr buf[0]))[0]
        Default
            Return ""
    End Select
End Function

?Linux
Function GetRegistry$( name$ )
    DebugLog "Registry Error: Not implemented"
End Function

?MacOS
Function GetRegistry$( name$ )
    DebugLog "Registry Error: Not implemented"
End Function

?
