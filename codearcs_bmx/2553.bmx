; ID: 2553
; Author: JoshK
; Date: 2009-08-01 16:40:47
; Title: Wallpaper Rotater
; Description: Selects a random wallpaper image

Framework brl.linkedlist
Import brl.filesystem
Import brl.retro

Const SPI_SETDESKWALLPAPER=20

Extern "win32"
	Function SystemParametersInfoA:Int(uiAction,uiParam,pvParam$z,fWinIni)
EndExtern

bgpath$=AppDir


Local list:TList=New TList

d=ReadDir(bgpath)
If Not d End
Repeat
	file$=NextFile(d)
	If file="" Exit
	Select Lower(ExtractExt(file))
	Case "bmp"
		count:+1
		list.addlast bgpath+"/"+file
	EndSelect
Forever
CloseDir d

SeedRnd MilliSecs()

bgfile$=String(list.valueatindex(Rand(0,count-1)))
SystemParametersInfoA SPI_SETDESKWALLPAPER,0,bgfile,0

End
