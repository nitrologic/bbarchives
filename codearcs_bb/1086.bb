; ID: 1086
; Author: Lorenzo
; Date: 2004-06-14 23:47:57
; Title: Song List Player
; Description: Plays music files in a folder.

; ReadDir/NextFile$/CloseDir example
Graphics 320,240,16,2
SetBuffer BackBuffer()

Global chnWave=0
Global chnDave=0
Global songtime=0
Global songswitch=0
Global wvol#=0
Global dvol#=0
Global song=0
; Define what folder to start with ...
folder$="music"

; Open up the directory, and assign the handle to myDir
myDir=ReadDir(folder$)

While Not KeyHit(1)
	Cls
	If songswitch>1
		songswitch=0
	EndIf
	ChannelVolume chnWave,wvol#
	ChannelVolume chnDave,dvol#
	If songswitch=1
		If dvol>0
			dvol#=dvol#-.01
		EndIf
		If dvol#=0
			StopChannel chnDave
		EndIf
		If wvol#<1
			wvol#=wvol#+.01
		EndIf
	EndIf
	If songswitch=0
		If wvol#>0
			wvol#=wvol#-.01
		EndIf
		If wvol#=0
			StopChannel chnWave
		EndIf
		If dvol#<1
			dvol#=dvol#+.01
		EndIf	
	EndIf
	If  time<500
		time=time+1
		If song<3
			time=500
			song=song+1
			songswitch=0
		EndIf
	Else
		time=0
		songswitch=songswitch+1
		file$=NextFile$(myDir)
		If songswitch=1
			chnWave=PlayMusic("music\"+file$)
		Else
			chnDave=PlayMusic("music\"+file$)
		EndIf
		If file$=""
			myDir=ReadDir(folder$)
			song=0
		EndIf
	EndIf
	If KeyHit(28)
		StopChannel chnWave
		StopChannel chnDave
		songswitch=songswitch+1 
	EndIf
	Text 10,20, "Song: "+file$	
	Text 10,30, "Timer: "+time	
	Text 10,40, "Switch: "+songswitch
	Text 10,50, "Dvolume: "+dvol#
	Text 10,60, "Wvolume: "+wvol#
	Flip
Wend
Text 10,40, "DONE!"
CloseDir myDir

End
