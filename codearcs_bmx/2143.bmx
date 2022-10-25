; ID: 2143
; Author: JoshK
; Date: 2007-11-06 20:37:53
; Title: AppLog Module
; Description: A useful module to handle data logging

Module leadwerks.applog

Import brl.standardio
Import brl.filesystem
Import brl.system

Strict


Private

Global AppLogEnabled
Global AppLogStream:TStream
Global AppLogCallback(text$)

Public

Rem
bbdoc:
EndRem
Function AppLogMode(mode,callback:Byte Ptr=Null)
	If Not mode AppLog "Logging stopped"
	AppLogEnabled=mode
	AppLogCallback=callback
	If mode AppLog "Logging started"
EndFunction

'Flags:
'1 - Error
'2 - Don't return line
'4 - Notification box
Rem
bbdoc:
EndRem
Function AppLog(text$,flags=0)
	If Not AppLogEnabled Return
	If Not AppLogStream AppLogStream=WriteFile(StripExt(AppFile)+".log")
	
	If (4 & flags) Notify text,(1 & flags)
	If (1 & flags) text="Error: "+text
	If Not (2 & flags) text:+Chr(13)+Chr(10)

	If AppLogStream AppLogStream.WriteString text
	WriteStdout text
	If AppLogCallback<>Null AppLogCallback(text)	
EndFunction
