; ID: 1275
; Author: n8r2k
; Date: 2005-01-31 20:24:19
; Title: AppTitle()
; Description: Scrolls the applet title and changes it

Graphics 800,600,16,2
Global apptime = 0
Global apptext$ = app1$
Global appnum = 0
Global texttime = 0
Global appdir = 0
Const app1$ = "If you like you could"  ;change these lines
Const app2$ = "Mess with this code and"
Const app3$ = "Add more messages"
SeedRnd(MilliSecs())

While Not KeyHit(1)
AppletTitle()
Flip
Wend

Function AppletTitle()
AppTitle apptext$
Cls
apptext$ = " " + apptext$ 
apptime = apptime + 1
If apptime = 100 ;mess with this var
	If appnum = 0
		apptext$ = app2$
		apptime = 0
		appnum = 1
	ElseIf appnum = 1
		apptext$ = app3$
		apptime = 0
		appnum = 2
	ElseIf appnum = 2
		apptext$ = app1$
		apptime = 0
		appnum = 0
	EndIf
EndIf
Color Rand(0,255),Rand(0,255),Rand(0,255)
Text 0,500,apptext$
End Function
