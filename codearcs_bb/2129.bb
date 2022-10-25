; ID: 2129
; Author: John Blackledge
; Date: 2007-10-27 07:16:39
; Title: 3d movie playback
; Description: Playing a movie onto an entity

Graphics3D 800,600,32,2
SetBuffer BackBuffer()

font=LoadFont("arial",20) : SetFont font
camera=CreateCamera()
light=CreateLight()
cube = CreateCube()
MoveEntity cube,0,0,3
ScaleEntity cube,1,0.5,1
movietex = CreateTexture(256,128)
EntityTexture cube,movietex

movie$ = "sample.avi" ; <- your movie here ; any AVI or MPG

hmovie = OpenMovie(movie$)
framePeriod = 1000/30
frametimer = CreateTimer(framePeriod)

While Not KeyHit(1)
	WaitTimer frametimer
	If t>5
		t=0
		If Not MoviePlaying(hmovie)
			CloseMovie(hmovie)
			hmovie = OpenMovie(movie$)
			frame = 0
		Else
			SetBuffer	TextureBuffer(movietex)
			DrawMovie(hmovie)
			frame = frame + 1 : Color 255,255,255	:	Text 0,0,"Frame "+frame
			SetBuffer BackBuffer()
		EndIf
	EndIf
	t=t+1
	TurnEntity cube,0,.4,0
	UpdateWorld() : RenderWorld()
	Text 0,0,"AvailVidMem() "+AvailVidMem()
	Flip()
Wend
End
