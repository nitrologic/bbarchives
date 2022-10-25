; ID: 1118
; Author: Kel
; Date: 2004-07-25 15:20:31
; Title: mini frame counter
; Description: frame counter in a few lines

Graphics 640, 480, 32, 0
Global timer,fpscount,fpstemp,fps,x,y
SetBuffer BackBuffer()
timer=MilliSecs()

While Not KeyDown(1)
Cls
fps(0,0)
Flip 
Wend
End

Function fps(x,y)
fpscount=fpscount+1
If MilliSecs()<timer+1000
Else
	fps=fpscount-fpstemp
	fpstemp=fpscount
	timer=MilliSecs()
EndIf
Text x,y,"FPS "+fps
End Function
