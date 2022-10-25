; ID: 1780
; Author: Fabian.
; Date: 2006-08-07 15:56:41
; Title: fmc.IOStream
; Description: A module with some stream definitions used by other modules

Strict
Module fmc.IOStream

ModuleInfo "Version: 0.04"
ModuleInfo "Modserver: Fabian"

Import brl.stream
Import brl.event

Const EVENT_READAVAIL = $80001
Const STREAMTYPE_FILE = 1
Const STREAMTYPE_COMM = 2
Const STREAMTYPE_AVAIL = 3
Const STREAMTYPE_EVENT = 4

Function StreamType ( stream:TStream )
  Select stream.Seek ( -1 )
    Case 0
      Return STREAMTYPE_FILE
    Case -1
      Return STREAMTYPE_COMM
    Case -2
      Return STREAMTYPE_AVAIL
    Case -3
      Return STREAMTYPE_EVENT
  EndSelect
EndFunction

Function IOStreamPos:Long ( stream:TStream )
  Local IO:TIOStream = TIOStream ( stream )
  If IO
    Return IO.IOPos ( )
  Else
    Return stream.Pos ( )
  EndIf
EndFunction

Function IOStreamSize:Long ( stream:TStream )
  Local IO:TIOStream = TIOStream ( stream )
  If IO
    Return IO.IOSize ( )
  Else
    Return stream.Size ( )
  EndIf
EndFunction

Function IOSeekStream:Long ( stream:TStream , pos:Long )
  Local IO:TIOStream = TIOStream ( stream )
  If IO
    Return IO.IOSeek ( pos )
  Else
    Return stream.Seek ( pos )
  EndIf
EndFunction

Type TIOStream Extends TStream
  Method Pos ( )
    Return IOPos ( )
  EndMethod

  Method Size ( )
    Return IOSize ( )
  EndMethod

  Method Seek ( pos )
    Return IOSeek ( pos )
  EndMethod

  Method IOPos:Long ( )
    Return -1
  EndMethod

  Method IOSize:Long ( )
  EndMethod

  Method IOSeek:Long ( pos:Long )
    Return -1
  EndMethod
EndType
