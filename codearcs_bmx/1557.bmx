; ID: 1557
; Author: Fabian.
; Date: 2005-12-11 12:15:51
; Title: fmc.Application
; Description: An abstract type for writing event handling applications.

Strict
Rem
bbdoc:Application
EndRem
Module fmc.Application

ModuleInfo "Version: 0.01"
ModuleInfo "Modserver: Fabian"

Import brl.linkedlist
Import brl.event

TApplication.List = CreateList ( )
AddHook EmitEventHook , TApplication.EventHook

Rem
bbdoc:Abstract application type
about:
Extend this type to create an application object.
EndRem
Type TApplication
  Global List:TList
  Field Link:TLink

  Function EventHook:Object ( ID , Data:Object , Context:Object )
    Local Event:TEvent = TEvent ( Data )
    For Local Application:TApplication = EachIn List
      Application.OnEvent Event
    Next
    Return Data
  EndFunction

Rem
bbdoc:An application's event handler
about:
Overwrite this method to handle events.
An event with @id = #brl.event.EVENT_APPTERMINATE and @source = #brl.blitz.Self is posted when #Stop method is called.
EndRem
  Method OnEvent ( Event:TEvent )
  EndMethod

Rem
bbdoc:Init application
about:
Extend this method to add any start up code needed by the application.
EndRem
  Method New ( )
    Link = List.AddLast ( Self )
  EndMethod

Rem
bbdoc:Return whether application is running
returns:#brl.blitz.True if application is running, else #brl.blitz.False
EndRem
  Method Running ( )
    Return Link <> Null
  EndMethod

Rem
bbdoc:Stop application
about:
This method stops the application.
If the application is already stopped this method does nothing.
With #Running you can check whether the application has already been stopped or is still running.
EndRem
  Method Stop ( )
    If Link
      TEvent.Create ( EVENT_APPTERMINATE , Self ).Emit
      Link.Remove
      Link = Null
    EndIf
  EndMethod
EndType
