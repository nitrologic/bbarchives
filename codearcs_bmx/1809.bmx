; ID: 1809
; Author: Fabian.
; Date: 2006-09-11 09:30:57
; Title: fmc.Requester
; Description: A module to show some system requesters

Strict
Module fmc.Requester
?Win32

ModuleInfo "Version: 0.01"
ModuleInfo "Modserver: Fabian"

Import brl.system
Import fmc.Development
Import fmc.ObjectTool

Type TThreadContext
  Field Thread:TThread

  Method Start ( )
    If Not Thread
      Thread = TThread.Create ( Func , OpenHandle ( Self ) )
    EndIf
  EndMethod

  Method Run ( ) Abstract
  Method Finish ( ) Abstract
EndType

Type TNotify Extends TThreadContext
  Field Text$
  Field Serious
  Field Func ( Data , Context:Object , O:TNotify )
  Field Context:Object
  Field Data

  Function Create:TNotify ( Text$ , Serious = False , Func ( Data , Context:Object , O:TNotify ) = NotifyFunc , Context:Object = Null )
    Local O:TNotify = New TNotify
    O.Text = Text
    O.Serious = Serious
    O.Func = Func
    O.Context = Context
    O.Start
    Return O
  EndFunction

  Method Run ( )
    Data = bbSystemNotify ( Text , Serious )
  EndMethod

  Method Finish ( )
    If Func
      Func Data , Context , Self
    EndIf
  EndMethod
EndType

Function NotifyFunc ( Data , Context:Object , O:TNotify )
  TEvent.Create ( EVENT_GADGETDONE , O , Data ).Emit
EndFunction

Type TConfirm Extends TThreadContext
  Field Text$
  Field Serious
  Field Func ( Data , Context:Object , O:TConfirm )
  Field Context:Object
  Field Data

  Function Create:TConfirm ( Text$ , Serious = False , Func ( Data , Context:Object , O:TConfirm ) = ConfirmFunc , Context:Object = Null )
    Local O:TConfirm = New TConfirm
    O.Text = Text
    O.Serious = Serious
    O.Func = Func
    O.Context = Context
    O.Start
    Return O
  EndFunction

  Method Run ( )
    Data = bbSystemConfirm ( Text , Serious )
  EndMethod

  Method Finish ( )
    If Func
      Func Data , Context , Self
    EndIf
  EndMethod
EndType

Function ConfirmFunc ( Data , Context:Object , O:TConfirm )
  TEvent.Create ( EVENT_GADGETDONE , O , Data ).Emit
EndFunction

Type TProceed Extends TThreadContext
  Field Text$
  Field Serious
  Field Func ( Data , Context:Object , O:TProceed )
  Field Context:Object
  Field Data

  Function Create:TProceed ( Text$ , Serious = False , Func ( Data , Context:Object , O:TProceed ) = ProceedFunc , Context:Object = Null )
    Local O:TProceed = New TProceed
    O.Text = Text
    O.Serious = Serious
    O.Func = Func
    O.Context = Context
    O.Start
    Return O
  EndFunction

  Method Run ( )
    Data = bbSystemProceed ( Text , Serious )
  EndMethod

  Method Finish ( )
    If Func
      Func Data , Context , Self
    EndIf
  EndMethod
EndType

Function ProceedFunc ( Data , Context:Object , O:TProceed )
  TEvent.Create ( EVENT_GADGETDONE , O , Data ).Emit
EndFunction
Rem
Type TRequestFile Extends TThreadContext
  Field Text$
  Field Exts$
  Field Save
  Field File$
  Field Dir$
  Field Buf:Byte []
  Field Func ( Data$ , Context:Object , O:TRequestFile )
  Field Context:Object
  Field Data

  Function Create:TRequestFile ( Text$ , Extensions$ = "" , Save_Flag = False , Initial_Path$ = "" , Func ( Data$ , Context:Object , O:TRequestFile ) = RequestFileFunc , Context:Object = Null )
    Local O:TRequestFile = New TRequestFile
    O.Text = Text
    If Extensions
      If Extensions.Find ( ":" ) = -1
        O.Exts = "Files~0*." + Extensions
      Else
        O.Exts = Extensions.Replace ( ":" , "~0*." )
      EndIf
      O.Exts = O.Exts.Replace ( ";" , "~0" )
      O.Exts = O.Exts.Replace ( "," , ";*." ) + "~0"
    EndIf
    O.Save = Save_Flag
    Initial_Path = Initial_Path.Replace ( "/" , "\" )
    Local I = Initial_Path.FindLast ( "\" )
    If I <> -1
      O.Dir = Initial_Path [.. I ]
      O.File = Initial_Path [ I + 1 ..]
    Else
      O.File = Initial_Path
    EndIf
    O.Buf = New Byte [ 4096 ]
    O.Func = Func
    O.Context = Context
    O.Start
    Return O
  EndFunction

  Method Run ( )
    Data = bbSystemRequestFile ( Text , Exts , DefExt , Save , File , Dir , Buf , Len Buf )
  EndMethod

  Method Finish ( )
    If Func
      If Data
        Func String.FromCString ( Buf ) , Context , Self
      Else
        Func "" , Context , Self
      EndIf
    EndIf
  EndMethod
EndType

Function RequestFileFunc ( Data$ , Context:Object , O:TRequestFile )
  TEvent.Create ( EVENT_GADGETDONE , O , 0 , 0 , 0 , 0 , Data ).Emit
EndFunction
EndRem
Type TRequestDir Extends TThreadContext
  Field Text$
  Field Dir$
  Field Buf:Byte []
  Field Func ( Data$ , Context:Object , O:TRequestDir )
  Field Context:Object
  Field Data

  Function Create:TRequestDir ( Text$ , Initial_Path$ = "" , Func ( Data$ , Context:Object , O:TRequestDir ) = RequestDirFunc , Context:Object = Null )
    Local O:TRequestDir = New TRequestDir
    O.Text = Text
    O.Dir = Initial_Path.Replace ( "/" , "\" )
    O.Buf = New Byte [ 4096 ]
    O.Func = Func
    O.Context = Context
    O.Start
    Return O
  EndFunction

  Method Run ( )
    Data = bbSystemRequestDir ( Text , Dir , Buf , Len Buf )
  EndMethod

  Method Finish ( )
    If Func
      If Data
        Func String.FromCString ( Buf ) , Context , Self
      Else
        Func "" , Context , Self
      EndIf
    EndIf
  EndMethod
EndType

Function RequestDirFunc ( Data$ , Context:Object , O:TRequestDir )
  TEvent.Create ( EVENT_GADGETDONE , O , 0 , 0 , 0 , 0 , Data ).Emit
EndFunction

Private

Function Func ( Context )
  TThreadContext ( ObjectForHandle ( Context ) ).Run
  CallbackMain Proc , Context
EndFunction

Function Proc ( Context )
  Local T:TThreadContext = TThreadContext ( ObjectForHandle ( Context ) )
  T.Finish
  CloseHandle Context
  T.Thread = Null
EndFunction
?
