; ID: 3057
; Author: EsseEmmeErre
; Date: 2013-06-08 12:40:09
; Title: Simple MIDI player
; Description: This code shows how to develop a very simple MIDI player using BlitzPlus. No Hw/Sw MIDI device check supported yet!

AppTitle("MIDI Player by Stefano Maria Regattin")
MusicaMIDI$=RequestFile("Load a MIDI song...","MID")
;MIDI song path and name
Graphics(400,100,0,2)
;open a graphics window
AltezzaFinestra=FontHeight()*3
;evaluate the desired window height
EndGraphics()
;close the window
Graphics(400,AltezzaFinestra,0,2)
;reopen the window with the desired height
SetBuffer(BackBuffer())
;meaningless command
Posizione=Instr(MusicaMIDI$,"\")
;first find the device
Repeat
 NomeMusicaMIDI$=Right$(MusicaMIDI$,Len(MusicaMIDI$)-Posizione)
 Posizione=Instr(MusicaMIDI$,"\",Posizione+1)
Until Posizione=0
;loop to find the MIDI file name
If NomeMusicaMIDI$<>"" Then
;if a MIDI file has been selected
 Musica=PlayMusic(MusicaMIDI$)
;music handle
 FineDellaMusica=False
;end of music flag
 Repeat
  TastoPremuto=GetKey()
;check for a key pressed
  Text(0,0,"You are listening to "+NomeMusicaMIDI$):Flip()
  Text(0,0,"You are listening to "+NomeMusicaMIDI$):Flip()
  Text(0,FontHeight(),"Press SPACE to stop listening..."):Flip()
  Text(0,FontHeight(),"Press SPACE to stop listening..."):Flip()
;on window messages
  If TastoPremuto=32 Then FineDellaMusica=True
;if SPACE BAR is pressed leave the listening
  If ChannelPlaying(Musica)=False Then FineDellaMusica=True
;if the song is ended leave the listening
 Until FineDellaMusica=True
;listening loop end
 StopChannel(Musica)
;stop music
 Text(0,FontHeight()*2,"Listening ended, program is closing."):Flip()
 Text(0,FontHeight()*2,"Listening ended, program is closing."):Flip()
;ending message on window
Else
;if no MIDI file has been selected
 Text(0,0,"You have choosen no MIDI file, listening canceled."):Flip()
 Text(0,0,"You have choosen no MIDI file, listening canceled."):Flip()
;no MIDI file message on window
 Delay 1000
 Text(0,FontHeight(),"Program is closing, Bye."):Flip()
 Text(0,FontHeight(),"Program is closing, Bye."):Flip()
;no MIDI file message on window
;wait for a second
EndIf
Delay 1000
;wait for a second
EndGraphics()
;close the window
End
