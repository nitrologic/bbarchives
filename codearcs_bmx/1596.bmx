; ID: 1596
; Author: Fabian.
; Date: 2006-01-14 11:27:48
; Title: fmc.StdIOStream
; Description: A module for easy access to the standard input and output stream

Strict
Rem
bbdoc:StdIOStream
EndRem
Module fmc.StdIOStream

ModuleInfo "Version: 0.05"
ModuleInfo "Modserver: Fabian"

Import brl.standardio
Import fmc.IOStream
Import fmc.Development
Import fmc.ObjectTool

Rem
bbdoc:Get standard input and output stream
returns:The standard input and output stream
about:
This function returns the application's standard input and output stream.
You can use this stream to exchange information with the calling application or the user.
Note that #brl.standardio.StandardIOStream is set to this stream when the application starts up.
You also can get this stream calling #brl.stream.OpenStream with "object::stdio" as parameter.
Note that #brl.stream.OpenStream with "object::standardio" as parameter will return
#brl.standardio.StandardIOStream.
EndRem
Function StdIOStream:TStream ( )
  Return Stream
EndFunction

Rem
bbdoc:Get the application's command line
returns:The application's command line
about:
This function returns the application's command line.
With the optional parameter @RemoveApp you can control whether the function removes the
application's name and trims the line before it returns it.
EndRem
Function CommandLine$ ( RemoveApp = True )
  If RemoveApp
    Return Line1
  EndIf
  Return Line0
EndFunction

Private

Global Line0$
Global Line1$
MakeCmdLine
Global Stream:TStdIOStream = New TStdIOStream
StandardIOStream = Stream
New TObjectStreamFactory
?Win32
BufferedInput
Global InHandle = GetStdHandle ( -10 )
Global OutHandle = GetStdHandle ( -11 )
Global DataEof

Function BufferedInput ( )
  Global BufferLen = 1024
  Global Buffer:Byte Ptr = MemAlloc ( BufferLen )
  Global ThreadIn = GetStdHandle ( -10 )
  Global ThreadOut
  Local Pipe
  CreatePipe Varptr Pipe , Varptr ThreadOut , Null , 0
  SetStdHandle -10 , Pipe
  DebugSuspend
  UpdateCStream
  Global T:TThread = TThread.Create ( Thread , 0 )
  DebugResume

  Function Thread ( Context )
    Local PipeThreadIn = PeekNamedPipe ( ThreadIn , Null , 0 , Null , Null , Null )
    Repeat
      Local All
      Local Cur
      If Not ( ReadFile ( ThreadIn , Buffer , BufferLen , Varptr All , Null ) And ( All Or PipeThreadIn ) )
        DataEof = True
        CloseHandle ThreadOut
        Return
      EndIf
      While All > Cur
        Local N
        WriteFile ThreadOut , Buffer + Cur , All - Cur , Varptr N , Null
        Cur :+ N
      Wend
      If All
        CallbackMain Func , All
      EndIf
    Forever
  EndFunction

  Function UpdateCStream ( )
    ( Int Ptr stdin_ ) [ 4 ] = _open_osfhandle ( GetStdHandle ( -10 ) , $8000 )
    ( Int Ptr stdout_ ) [ 4 ] = _open_osfhandle ( GetStdHandle ( -11 ) , $8001 )
    ( Int Ptr stderr_ ) [ 4 ] = _open_osfhandle ( GetStdHandle ( -12 ) , $8001 )

    Extern
      Function _open_osfhandle ( osfhandle , access )
    EndExtern
  EndFunction

  Function Func ( Context )
    TEvent.Create ( EVENT_READAVAIL , Stream , Context ).Emit
  EndFunction
EndFunction

Extern "Win32"
  Function GetStdHandle ( N )
  Function SetStdHandle ( N , Handle )
  Function CreatePipe ( In Ptr , Out Ptr , PA:Byte Ptr , Size )
  Function FlushFileBuffers ( File )
  Function PeekNamedPipe ( Pipe , Buffer:Byte Ptr , Size , Read Ptr , Avail Ptr , BLTM Ptr )
  Function ReadFile ( File , Buffer:Byte Ptr , Size , Read Ptr , O:Byte Ptr )
  Function WriteFile ( File , Buffer:Byte Ptr , Size , Written Ptr , O:Byte Ptr )
  Function GetCommandLineW:Short Ptr ( )
  Function CloseHandle ( Handle )
EndExtern
?

Function MakeCmdLine ( )
?Win32
  Line0 = String.FromWString ( GetCommandLineW ( ) )
?Linux | MacOS
  For Local AppArg$ = EachIn AppArgs
    Line0 :+ " ~q" + AppArg + "~q"
  Next
  Line0 = Line0 [ 1 ..]
?
  Line1 = Line0.Trim ( )
  If Line1 [.. 1 ] = "~q"
    Line1 = Line1 [ Line1.Find ( "~q" , 1 ) + 1 ..]
  Else
    While Line1 [.. 1 ].Trim ( )
      Line1 = Line1 [ 1 ..]
    Wend
  EndIf
  Line1 = Line1.Trim ( )
EndFunction

Type TStdIOStream Extends TStream
  Method Eof ( )
?Win32
    Return DataEof And Not Size ( )
?Linux | MacOS
    Return feof_ ( stdin_ )
?
  EndMethod

  Method Pos ( )
    Return -1
  EndMethod

  Method Size ( )
?Win32
    Local Avail
    PeekNamedPipe InHandle , Null , 0 , Null , Varptr Avail , Null
    Return Avail
?
  EndMethod

  Method Seek ( pos )
?Win32
    If pos = -1
      Return -3
    EndIf
?
    Return -1
  EndMethod

  Method Flush ( )
?Win32
    FlushFileBuffers OutHandle
?Linux | MacOS
    fflush_ stdout_
?
  EndMethod

  Method Close ( )
  EndMethod

  Method Read ( buf:Byte Ptr , count )
?Win32
    ReadFile InHandle , buf , count , Varptr count , Null
    Return count
?Linux | MacOS
    Return fread_ ( buf , 1 , count , stdin_ )
?
  EndMethod

  Method Write ( buf:Byte Ptr , count )
?Win32
    WriteFile OutHandle , buf , count , Varptr count , Null
    Return count
?Linux | MacOS
    Return fwrite_ ( buf , 1 , count , stdout_ )
?
  EndMethod
EndType

Type TObjectStreamFactory Extends TStreamFactory
  Method CreateStream:TStream ( url:Object , proto$ , path$ , readable , writeable )
    Local Array:Object [] = Object [] ( url )
    If Array
      Local U$
      Local H [ Len Array ]
      For Local I = 0 Until Len Array
        Local T$ = String Array [ I ]
        If T
          U :+ T
        Else
          H [ I ] = OpenHandle ( Array [ I ] )
          U :+ "object::" + H [ I ]
        EndIf
      Next
      Local S:TStream = OpenStream ( U , readable , writeable )
      For Local N = EachIn H
        If N
          CloseHandle N
        EndIf
      Next
      Return S
    EndIf
    If proto = "object"
      Local I = Int path
      If I
        Return OpenStream ( ObjectForHandle ( I ) , readable , writeable )
      EndIf
      If path.ToLower ( ) = "stdio"
        Return Stream
      EndIf
      If path.ToLower ( ) = "standardio"
        Return StandardIOStream
      EndIf
    EndIf
  EndMethod
EndType
