; ID: 464
; Author: Vertex
; Date: 2002-10-17 10:44:58
; Title: Open CD Door
; Description: Open CD-audio-door

Open  = CreateBank(Len("Set CDaudio door open"))
For I = 1 To Len("Set CDaudio door open")
	PokeByte Open,I - 1,Asc(Mid$( "Set CDaudio door open",I,1))
Next 
CallDLL("winmm.dll","mciExecute",Open)
