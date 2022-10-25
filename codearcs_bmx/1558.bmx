; ID: 1558
; Author: Fabian.
; Date: 2005-12-11 14:26:01
; Title: fmc.ProcessStream
; Description: A module containing a process stream factory using pub.freeprocess.

Strict
Rem
bbdoc:ProcessStream
EndRem
Module fmc.ProcessStream

ModuleInfo "Version: 0.01"
ModuleInfo "Modserver: Fabian"

Import pub.freeprocess
Import fmc.Development
Import fmc.ObjectTool
Import fmc.IOStream

Rem
bbdoc:Create a new process stream
returns:A new process stream
about:
This function creates a new process and its stream.
Closing the process stream will only close the stream to the process and not terminate the process itself.
Note that processes created with this command are not terminated at the end of the program.
To terminate the process of a process stream you can get the process stream's process using #ProcessStreamProcess
and terminate it using @pub.freeprocess.TerminateProcess or you can just call #TerminateProcessStreamProcess.
To create a process stream you also can call #brl.stream.OpenStream with "execute::" and the process name
as parameter.
EndRem
Function CreateProcessStream:TStream ( Name$ )
  Return TProcessStream.Create ( Name )
EndFunction

Rem
bbdoc:Get a process stream's process
returns:A process stream's process
about:
This function returns a process stream's process.
EndRem
Function ProcessStreamProcess:TProcess ( Stream:TStream )
  Local ProcessStream:TProcessStream = TProcessStream ( Stream )
  If ProcessStream
    Return ProcessStream.Proc
  EndIf
EndFunction

Rem
bbdoc:Terminate a process stream's process and close the stream itself
about:
This function terminates the process stream's process and than closes the process stream
EndRem
Function TerminateProcessStreamProcess ( Stream:TStream )
  Local ProcessStream:TProcessStream = TProcessStream ( Stream )
  If ProcessStream And ProcessStream.Proc
    ProcessStream.Proc.Terminate
    ProcessStream.Close
  EndIf
EndFunction

Private

New TProcessStreamFactory
?Win32

Function Proc ( Context )
  Local ProcessStream:TProcessStream = TProcessStream ( ObjectForHandle ( ( Int Ptr Context ) [ 0 ] ) )
  TEvent.Create ( EVENT_READAVAIL , ProcessStream , ( Int Ptr Context ) [ 1 ] ).Emit
  MemFree Byte Ptr Context
EndFunction

Extern "Win32"
  Function PeekNamedPipe ( Pipe , Buffer:Byte Ptr , Size , Read Ptr , Avail Ptr , BLTM Ptr )
  Function ReadFile ( File , Buffer:Byte Ptr , Size , Read Ptr , O:Byte Ptr )
  Function WriteFile ( File , Buffer:Byte Ptr , Size , Written Ptr , O:Byte Ptr )
  Function CreatePipe ( In Ptr , Out Ptr , PA:Byte Ptr , Size )
  Function CloseHandle ( Handle )
EndExtern
?

Type TProcessStreamFactory Extends TStreamFactory
  Method CreateStream:TStream ( url:Object , proto$ , path$ , readable , writeable )
    If proto = "execute"
      Return TProcessStream.Create ( path )
    EndIf
  EndMethod
EndType

Type TProcessStream Extends TStream
  Field Proc:TProcess
?Win32
  Field PipeI
  Field PipeO
  Field PipeT
  Field BufferLen
  Field Buffer:Byte Ptr
  Field Sync:TSync
  Field Thread:TThread
  Field Closed
?

  Function Create:TProcessStream ( Name$ )
    Local ProcessStream:TProcessStream = New TProcessStream
    ProcessStream.Proc = TProcess.Create ( Name , HIDECONSOLE )
    If ProcessStream.Proc
      TProcess.ProcessList.Remove ProcessStream.Proc
?Win32
      CreatePipe Varptr ProcessStream.PipeI , Varptr ProcessStream.PipeO , Null , 0
      ProcessStream.PipeT = ProcessStream.Proc.pipe.readhandle
      ProcessStream.BufferLen = 1024
      ProcessStream.Buffer = MemAlloc ( ProcessStream.BufferLen )
      ProcessStream.Sync = New TSync
      ProcessStream.Sync.Sync
      ProcessStream.Thread = TThread.Create ( Func , OpenHandle ( ProcessStream ) )

      Function Func ( Context )
        Local ProcessStream:TProcessStream = TProcessStream ( ObjectForHandle ( Context ) )
        Local ThreadOut = ProcessStream.PipeO
        Local ThreadIn = ProcessStream.PipeT
        Local BufferLen = ProcessStream.BufferLen
        Local Buffer:Byte Ptr = ProcessStream.Buffer
        Local Sync:TSync = ProcessStream.Sync
        Repeat
          Local All
          Local Cur
          Sync.EndSync
          If Not ReadFile ( ThreadIn , Buffer , BufferLen , Varptr All , Null )
            Sync.Sync
            CloseHandle ThreadOut
            ProcessStream.Closed = True
            Sync.EndSync
            Return
          EndIf
          Sync.Sync
          While All > Cur
            Local N
            WriteFile ThreadOut , Buffer + Cur , All - Cur , Varptr N , Null
            Cur :+ N
          Wend
          If All
            Local Mem Ptr = Int Ptr MemAlloc ( 8 )
            Mem [ 0 ] = Context
            Mem [ 1 ] = All
            CallbackMain Proc , Int Mem
          EndIf
        Forever
      EndFunction

?
      Return ProcessStream
    EndIf
  EndFunction

  Method Eof ( )
    If Not Proc
      Return True
    EndIf
    If Not ( Proc.Status ( ) Or Size ( ) )
      Close
      Return True
    EndIf
  EndMethod

  Method Pos ( )
    Return -1
  EndMethod

  Method Size ( )
    If Proc
?Win32
      Local Avail
      PeekNamedPipe PipeI , Null , 0 , Null , Varptr Avail , Null
      Return Avail
?Linux | MacOS
      Return Proc.pipe.ReadAvail ( )
?
    EndIf
  EndMethod

  Method Seek ( pos )
    If pos = -1
?Win32
      Return -3
?Linux | MacOS
      Return -2
?
    EndIf
    Return -1
  EndMethod

  Method Flush ( )
  EndMethod

  Method Close ( )
    If Proc
?Win32
      Sync.Sync
      Thread.Terminate
      MemFree Buffer
      CloseHandle PipeI
      If Not Closed
        CloseHandle PipeO
      EndIf
      ReleaseObject Self
?
      Proc.Close
      Proc = Null
    EndIf
  EndMethod

  Method Read ( buf:Byte Ptr , count )
    If Proc
?Win32
      ReadFile PipeI , buf , count , Varptr count , Null
      Return count
?Linux | MacOS
      Return Proc.pipe.Read ( buf , count )
?
    EndIf
  EndMethod

  Method Write ( buf:Byte Ptr , count )
    If Proc
      Return Proc.pipe.Write ( buf , count )
    EndIf
  EndMethod
EndType
