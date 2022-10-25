; ID: 1688
; Author: Nilium
; Date: 2006-04-28 19:47:09
; Title: Long Filesize
; Description: Get a filesize using a Long instead of an Int

?Win32
Const FILE_READ_ATTRIBUTES% = $00000080
Const FILE_SHARE_READ% = $00000001
Const OPEN_EXISTING% = 3

Extern "OS"
    Function GetFileSizeEx:Int( handle%, size:Long Ptr )
    Function CreateFileW:Int( filename$W, daccess%, sharem%, seca@ Ptr, cDisp%, flags%, template% )
    Function CloseHandle:Int( handle% )
End Extern

?Linux
Private
Global statbank:TBank = TBank.Create(256) ' Maximum possible size
Public

Extern "OS"
    Function stat64:Int( path$z, buf@ Ptr )
End Extern

?MacOS

?

Function GetFileSize:Long( file$ )
    Local out:Long=-1
    
    ?Win32
    Local handle% = CreateFileW( file, FILE_READ_ATTRIBUTES, FILE_SHARE_READ, Null, OPEN_EXISTING, 0, 0 )
    ASsert handle<>-1, "File does not exist"
    GetFileSizeEx( handle, Varptr out )
    CloseHandle( handle )
    
    ?Linux
    If stat64( file, statbank.Buf( ) ) <> 0 Then Assert "Unable to get file stats"
    out = statBank.PeekLong( 44 )
    
    ?MacOS
    Assert "GetFileSize not implemented for this platform"
    
    ?
    
    Return out
End Function
