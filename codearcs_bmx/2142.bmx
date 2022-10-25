; ID: 2142
; Author: JoshK
; Date: 2007-11-06 18:20:52
; Title: Set desktop wallpaper
; Description: Randomly choose a new desktop wallpaper from a directory

Framework brl.linkedlist
Import brl.filesystem
Import brl.retro

Const SPI_SETDESKWALLPAPER=20

Extern "win32"
	Function SystemParametersInfoA:Int(uiAction,uiParam,pvParam$z,fWinIni)
EndExtern

bgpath$="J:\Documents\Wallpapers"

Local list:TList=New TList


d=ReadDir(bgpath)
If Not d End
Repeat
	file$=NextFile(d)
	If file="" Exit
	If Lower(ExtractExt(file))="bmp"
		count:+1
		list.addlast bgpath+"/"+file
	EndIf
Forever
CloseDir d

If list.isempty() End

bgfile$=String(list.valueatindex(Rand(0,count-1)))
SystemParametersInfoA SPI_SETDESKWALLPAPER,0,bgfile,0

End
