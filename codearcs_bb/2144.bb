; ID: 2144
; Author: JoshK
; Date: 2007-11-06 20:44:07
; Title: AppSettings module
; Description: Advanced settings with the command line

Import brl.map
Import brl.retro

Rem
bbdoc:
End Rem
Module leadwerks.appsettings

Private

Global AppSettings:TMap=New TMap

Function ParseAppArgs()
	For n=1 To AppArgs.length-1
		indicator$=Left(AppArgs[n],1)
		key$=Lower(Right(AppArgs[n],AppArgs[n].length-1))
		Select indicator$
			Case "+"
				n:+1
				If n>=AppArgs.length Exit
				value$=AppArgs[n]
				SetAppSetting key,value
			Case "-"
				SetAppSetting key,"1"
		EndSelect
	Next
EndFunction

Public

ParseAppArgs()

Rem
bbdoc:
End Rem
Function AppSetting$(key$,defaultvalue$="")
	If key="" Return
	key=Lower(key)
	value$=String(AppSettings.valueforkey(key$))
	If value=""
		Return defaultvalue
	Else
		Return value
	EndIf
EndFunction
