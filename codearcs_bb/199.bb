; ID: 199
; Author: SopiSoft
; Date: 2002-01-21 17:21:26
; Title: BlitzMedia Player!
; Description: Simple media player!

;BlitzMedia Player
;Copyright (c) 2002 SopiSoft ;-)
;Adjust it the way you want.....!

Graphics3D 800,600,16,2
AppTitle "BlitzMedia Player!   © 2002 SopiSoft"
SetBuffer BackBuffer()
.begin
Cls
Global moviename$=Input$("Please type in the Filename of the movie! >> ")

If FileType(moviename)=1
 size$=Input$("Fullscreen? (Y/N)") 
 movie=OpenMovie(moviename)
 Cls
Else
 Print "Movie doesn't exist!......press any key to continue!"
 WaitKey()
 Goto begin
EndIf

While Not KeyDown(1)

If size="y" Then DrawMovie movie,0,0,800,600
If size="n" Then DrawMovie movie,0,0
If MoviePlaying(movie)=False Then Delay 3000 : Exit

UpdateWorld
RenderWorld

Flip

Wend

CloseMovie movie

End
