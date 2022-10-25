; ID: 1786
; Author: Fabian.
; Date: 2006-08-14 16:05:33
; Title: fmc.Development
; Description: With this module you can create threads and hook functions

Strict
Rem
bbdoc:Development
about:
This module contains some useful features for win32 programmers.
<table><tr><th>Constant</th><th>Value</th><th>Description</th></tr>
<tr><td>EVENT_ENDSESSION</td><td>$40001</td><td>This event is posted when the user logs off or shuts down the computer.</td></tr>
</table>
EndRem
Module fmc.Development
?Win32

ModuleInfo "Version: 0.09"
ModuleInfo "Modserver: Fabian"

Import brl.event

Const EVENT_ENDSESSION = $40001

Rem
bbdoc:Suspend the debugger
EndRem
Function DebugSuspend ( ) NoDebug
  OnDebugSuspend :+ 1
EndFunction

Rem
bbdoc:Resume the debugger
EndRem
Function DebugResume ( ) NoDebug
  OnDebugSuspend :- 1
EndFunction

Rem
bbdoc:Callback in main thread
EndRem
Function CallbackMain ( func ( context ) , context , sync = 0 )
  If sync
    Local Mem Ptr = Int Ptr MemAlloc ( 12 )
    Mem [ 0 ] = Int Byte Ptr func
    Mem [ 1 ] = context
    Mem [ 2 ] = sync
    WaitForSingleObject ( ( Int Ptr sync ) [ 1 ] , -1 )
    ( Int Ptr sync ) [ 0 ] :+ 1
    SetEvent ( ( Int Ptr sync ) [ 1 ] )
    PostMessageW Wnd , $400 , Int Byte Ptr CallbackSyncFunc , Int Mem
  Else
    PostMessageW Wnd , $400 , Int Byte Ptr func , context
  EndIf
EndFunction

Rem
bbdoc:Callback in main thread and wait for result
EndRem
Function CallbackMainWithReturn ( func ( context ) , context )
  Local Mem Ptr = Int Ptr MemAlloc ( 12 )
  Mem [ 0 ] = Int Byte Ptr func
  Mem [ 1 ] = context
  Mem [ 2 ] = CreateEventW ( Null , 0 , 0 , Null )
  PostMessageW Wnd , $400 , Int Byte Ptr CallbackMainFunc , Int Mem
  WaitForSingleObject Mem [ 2 ] , -1
  CloseHandle Mem [ 2 ]
  context = Mem [ 1 ]
  MemFree Mem
  Return context
EndFunction

Rem
bbdoc:Create a callback synchronization object
EndRem
Function CreateCallbackSync ( )
  Local Mem Ptr = Int Ptr MemAlloc ( 12 )
  Mem [ 0 ] = 1
  Mem [ 1 ] = CreateEventW ( Null , 0 , 1 , Null )
  Mem [ 2 ] = True
  Return Int Mem
EndFunction

Rem
bbdoc:Cancel callback operations
EndRem
Function CancelCallback ( sync )
  WaitForSingleObject ( ( Int Ptr sync ) [ 1 ] , -1 )
  ( Int Ptr sync ) [ 2 ] = False
  ( Int Ptr sync ) [ 0 ] :- 1
  If Not ( Int Ptr sync ) [ 0 ]
    CloseHandle ( ( Int Ptr sync ) [ 1 ] )
    MemFree Byte Ptr sync
  Else
    SetEvent ( ( Int Ptr sync ) [ 1 ] )
  EndIf
EndFunction

Rem
bbdoc:Thread type
EndRem
Type TThread
  Field Handle

Rem
bbdoc:Create a new thread
EndRem
  Function Create:TThread ( func ( context ) , context )
    Local Thread:TThread = New TThread
    Thread.Handle = CreateThread ( Null , 0 , func , context , 0 , Null )
    Return Thread
  EndFunction

Rem
bbdoc:Terminate the thread
EndRem
  Method Terminate ( )
    TerminateThread Handle , 0
  EndMethod

  Method Delete ( )
    CloseHandle Handle
  EndMethod
EndType

Rem
bbdoc:Synchronization type
EndRem
Type TSync
  Field Handle

  Method New ( )
    Handle = CreateEventW ( Null , 0 , 1 , Null )
  EndMethod

Rem
bbdoc:Begin the synchronization
EndRem
  Method Sync ( )
    WaitForSingleObject Handle , -1
  EndMethod

Rem
bbdoc:Try to begin the synchronization
returns:#brl.blitz.True if the synchronization began successfully, else #brl.blitz.False
EndRem
  Method TrySync ( )
    Return Not WaitForSingleObject ( Handle , 0 )
  EndMethod

Rem
bbdoc:End the synchronization
EndRem
  Method EndSync ( )
    SetEvent Handle
  EndMethod

  Method Delete ( )
    CloseHandle Handle
  EndMethod
EndType

Rem
bbdoc:Function hook type
EndRem
Type TFunctionHook
  Field HookLeft:TFunctionHook
  Field HookRight:TFunctionHook
  Field Hooked:THooked
  Field Data:Byte []
  Field Active

Rem
bbdoc:Create a new function hook
EndRem
  Function Create:TFunctionHook ( func:Byte Ptr , newfunc:Byte Ptr ) NoDebug
    Local Hooked:THooked = FirstHooked
    While Hooked
      If Hooked.Func = func
        Exit
      EndIf
      Hooked = Hooked.HookedDown
    Wend
    If Not Hooked
      Hooked = New THooked
      If FirstHooked
        FirstHooked.HookedUp = Hooked
        Hooked.HookedDown = FirstHooked
      EndIf
      FirstHooked = Hooked
      Hooked.FirstHook = New TFunctionHook
      Hooked.FirstHook.Hooked = Hooked
      Hooked.FirstHook.Data = New Byte [ 7 ]
      MemCopy Hooked.FirstHook.Data , func , 7
      Hooked.FirstHook.Active = True
      Hooked.Func = func
    EndIf
    Local Hook:TFunctionHook = New TFunctionHook
    Hooked.FirstHook.HookLeft = Hook
    Hook.HookRight = Hooked.FirstHook
    Hooked.FirstHook = Hook
    Hook.Hooked = Hooked
    Hook.Data = New Byte [ 7 ]
    Hook.Data [ 0 ] = 184
    ( Byte Ptr Ptr ( Byte Ptr Hook.Data + 1 ) ) [ 0 ] = newfunc
    Hook.Data [ 5 ] = 255
    Hook.Data [ 6 ] = 224
    Hook.Active = True
    UpdateHooked Hooked
    Return Hook
  EndFunction

Rem
bbdoc:Disable the function hook
EndRem
  Method Disable ( ) NoDebug
    If Hooked
      Active = False
      UpdateHooked Hooked
    EndIf
  EndMethod

Rem
bbdoc:Enable the function hook
EndRem
  Method Enable ( ) NoDebug
    If Hooked
      Active = True
      UpdateHooked Hooked
    EndIf
  EndMethod

Rem
bbdoc:Free the function hook
EndRem
  Method Free ( ) NoDebug
    If Hooked
      If Hooked.FirstHook = Self
        Hooked.FirstHook = HookRight
      Else
        HookLeft.HookRight = HookRight
      EndIf
      HookRight.HookLeft = HookLeft
      UpdateHooked Hooked
      If Not Hooked.FirstHook.HookRight
        Hooked.FirstHook = Null
        If FirstHooked = Hooked
          FirstHooked = Hooked.HookedDown
        Else
          Hooked.HookedUp.HookedDown = Hooked.HookedDown
        EndIf
        If Hooked.HookedDown
          Hooked.HookedDown.HookedUp = Hooked.HookedUp
        EndIf
      EndIf
      Hooked = Null
    EndIf
  EndMethod
EndType

Type THooked
  Field HookedUp:THooked
  Field HookedDown:THooked
  Field FirstHook:TFunctionHook
  Field Func:Byte Ptr
EndType

Private

Global FirstHooked:THooked
Global CurrentProcess = GetCurrentProcess ( )
Global MainThreadID = GetCurrentThreadId ( )
Local Class:TWinClass = New TWinClass
Class.Proc = Proc
Class.ClassName = ( "CLASS#" + Int Byte Ptr Proc ).ToWString ( )
RegisterClassW Class
Global Wnd = CreateWindowExW ( 0 , Class.ClassName , Null , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 )
MemFree Class.ClassName
'Area "Debug"
Global OnDebugSuspend
Extern
Global bbOnDebugStop()
Global bbOnDebugLog(message$)
Global bbOnDebugEnterStm(stm Ptr)
Global bbOnDebugEnterScope(scope Ptr,inst:Byte Ptr)
Global bbOnDebugLeaveScope()
Global bbOnDebugPushExState()
Global bbOnDebugPopExState()
Global bbOnDebugUnhandledEx(ex:Object)
EndExtern
Global _OnDebugStop()                             =bbOnDebugStop
Global _OnDebugLog(message$)                      =bbOnDebugLog
Global _OnDebugEnterStm(stm Ptr)                  =bbOnDebugEnterStm
Global _OnDebugEnterScope(scope Ptr,inst:Byte Ptr)=bbOnDebugEnterScope
Global _OnDebugLeaveScope()                       =bbOnDebugLeaveScope
Global _OnDebugPushExState()                      =bbOnDebugPushExState
Global _OnDebugPopExState()                       =bbOnDebugPopExState
Global _OnDebugUnhandledEx(ex:Object)             =bbOnDebugUnhandledEx
bbOnDebugStop=OnDebugStop
bbOnDebugLog=OnDebugLog
bbOnDebugEnterStm=OnDebugEnterStm
bbOnDebugEnterScope=OnDebugEnterScope
bbOnDebugLeaveScope=OnDebugLeaveScope
bbOnDebugPushExState=OnDebugPushExState
bbOnDebugPopExState=OnDebugPopExState
bbOnDebugUnhandledEx=OnDebugUnhandledEx
Function OnDebugStop()                             NoDebug;If Dbg()Return _OnDebugStop()
EndFunction
Function OnDebugLog(message$)                      NoDebug;If Dbg()Return _OnDebugLog(message)
EndFunction
Function OnDebugEnterStm(stm Ptr)                  NoDebug;If Dbg()Return _OnDebugEnterStm(stm)
EndFunction
Function OnDebugEnterScope(scope Ptr,inst:Byte Ptr)NoDebug;If Dbg()Return _OnDebugEnterScope(scope,Inst)
EndFunction
Function OnDebugLeaveScope()                       NoDebug;If Dbg()Return _OnDebugLeaveScope()
EndFunction
Function OnDebugPushExState()                      NoDebug;If Dbg()Return _OnDebugPushExState()
EndFunction
Function OnDebugPopExState()                       NoDebug;If Dbg()Return _OnDebugPopExState()
EndFunction
Function OnDebugUnhandledEx(ex:Object)             NoDebug;If Dbg()Return _OnDebugUnhandledEx(ex)
EndFunction
Function Dbg ( ) NoDebug
  Return GetCurrentThreadId ( ) = MainThreadID And Not OnDebugSuspend
EndFunction
'EndArea

Function Proc ( Win , Msg , WP ( context ) , LP )
  If Win = Wnd And Msg = $400
    WP LP
    Return
  EndIf
  If Win = Wnd And Msg = 22 And Byte Ptr WP
    TEvent.Create ( EVENT_ENDSESSION ).Emit
  EndIf
  Return DefWindowProcW ( Win , Msg , Int Byte Ptr WP , LP )
EndFunction

Function UpdateHooked ( Hooked:THooked ) NoDebug
  Local Hook:TFunctionHook = Hooked.FirstHook
  While Not Hook.Active
    Hook = Hook.HookRight
  Wend
  WriteProcessMemory CurrentProcess , Hooked.Func , Hook.Data , 7 , Null
EndFunction

Function CallbackMainFunc ( Mem Ptr )
  Local Func ( context ) = Byte Ptr Mem [ 0 ]
  Mem [ 1 ] = Func ( Mem [ 1 ] )
  SetEvent Mem [ 2 ]
EndFunction

Function CallbackSyncFunc ( Mem Ptr )
  Local Func ( context ) = Byte Ptr Mem [ 0 ]
  Local Context = Mem [ 1 ]
  Local Sync Ptr = Int Ptr Mem [ 2 ]
  MemFree Byte Ptr Mem
  WaitForSingleObject ( ( Int Ptr Sync ) [ 1 ] , -1 )
  Local Call = ( Int Ptr Sync ) [ 2 ]
  ( Int Ptr Sync ) [ 0 ] :- 1
  If Not ( Int Ptr Sync ) [ 0 ]
    CloseHandle ( ( Int Ptr Sync ) [ 1 ] )
    MemFree Byte Ptr Sync
  Else
    SetEvent ( ( Int Ptr Sync ) [ 1 ] )
  EndIf
  If Call
    Func Context
  EndIf
EndFunction

Type TWinClass
  Field Style
  Field Proc:Byte Ptr
  Field ClsExtra
  Field WndExtra
  Field Instance
  Field Icon
  Field Cursor
  Field Background
  Field MenuName:Short Ptr
  Field ClassName:Short Ptr
EndType

Extern "Win32"
  Function GetCurrentProcess ( )
  Function GetCurrentThreadId ( )
  Function WriteProcessMemory ( DstProc , Dst:Byte Ptr , Src:Byte Ptr , Size , Written Ptr )
  Function CreateThread ( TA:Byte Ptr , Size , Func:Byte Ptr , P , Flags , ID Ptr )
  Function TerminateThread ( H , E )
  Function RegisterClassW ( Class:Byte Ptr )
  Function CreateWindowExW ( ExS , CN:Short Ptr , WN:Short Ptr , S , X , Y , W , H , P , M , I , LP )
  Function DefWindowProcW ( Win , Msg , WP , LP )
  Function PostMessageW ( Win , Msg , WP , LP )
  Function CreateEventW ( EA:Byte Ptr , MR , IS , Name:Short Ptr )
  Function WaitForSingleObject ( Handle , Millis )
  Function SetEvent ( Handle )
  Function CloseHandle ( Handle )
EndExtern
?
