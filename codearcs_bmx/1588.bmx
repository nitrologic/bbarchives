; ID: 1588
; Author: Fabian.
; Date: 2006-01-04 11:00:05
; Title: NTFS-Links
; Description: Creates NTFS file links

Strict

Framework brl.blitz

Import brl.system
Import brl.filesystem

AppTitle = "NTFS-Links"
If Proceed ( "Do you want to use this tool to create a NTFS file link?" ) = 1
  Local FileTo$ = RequestFile ( "Select the file to create the link to" , "All files (*):*" )
  If FileTo
    Local Exts$ = "All files (*):*"
    If ExtractExt ( FileTo )
      Exts = "Type of file to link to (*." + ExtractExt ( FileTo ) + "):" + ExtractExt ( FileTo ) + ";" + Exts
    EndIf
    Local FileFrom$ = RequestFile ( "Select the folder and file name to create the link" , Exts , True )
    If FileFrom
      LinkFiles FileFrom , FileTo
      NotifySystemError GetSystemError ( )
    EndIf
  EndIf
EndIf

Function LinkFiles ( LinkFrom$ , LinkTo$ )
?Win32
  Return CreateHardLinkW ( LinkFrom.ToWString ( ) , LinkTo.ToWString ( ) , Null ) <> 0

  Extern "Win32"
    Function CreateHardLinkW ( Src:Short Ptr , Dst:Short Ptr , SAttr:Byte Ptr )
  EndExtern
?
?Linux | MacOS
  Throw "This function is win32 only."
?
EndFunction

Function NotifySystemError ( ID )
?Win32
  Local Buf:Short [ 1024 ]
  If FormatMessageW ( $1000 , Null , ID , 0 , Buf , Len Buf , Null )
    Notify String.FromWString ( Buf )
    Return True
  EndIf

  Extern "Win32"
    Function FormatMessageW ( Flags , Src:Byte Ptr , ID , Lang , Buf:Short Ptr , Size , Args:Byte Ptr )
  EndExtern
?
?Linux | MacOS
  Throw "This function is win32 only."
?
EndFunction

Function GetSystemError ( )
?Win32
  Return GetLastError ( )

  Extern "Win32"
    Function GetLastError ( )
  EndExtern
?
?Linux | MacOS
  Throw "This function is win32 only."
?
EndFunction
