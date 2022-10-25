; ID: 3009
; Author: JoshK
; Date: 2012-12-10 15:59:26
; Title: Permanent Environment Variables
; Description: Use this for permanent vars other apps can see.

SuperStrict

SetEnv("MyAwesomeKey","Happy happy fun time")

Function SetEnv:Int(key:String,value:String)
	Local stream:TStream=WriteFile("temp.bat")
	If Not stream Return False
	stream.WriteLine "% reg add ~qHKLM\SYSTEM\CurrentControlSet\Control\Session Manager\Environment~q /v ~q"+key+"~q /t REG_SZ /d ~q"+value+"~q"
	stream.close()
	Local proc:TProcess = CreateProcess("temp.bat")
	If Not proc Return False
	While proc.status()
		Delay 1
	Wend
	If getenv_(key)=value Return True
EndFunction
