; ID: 692
; Author: kRUZe
; Date: 2003-05-15 13:39:32
; Title: SinusPlayer
; Description: A twisted (literally) movie playing routine :-)  NJOi

;---------------------------------------------------------
;	
;	(c)2003 Zerosynapse
;	
;
;	Dunno why I wrote this but you may like it? :)
;
;
;	Use F1/F2 Increase/Decrease the pixel zoom size
;	Use F3/F4 Increase/Decrease the sinus values
;---------------------------------------------------------


width=800
height=600
	
filename$="yourmovie.avi"	; point to your movie file here :)

Graphics width,height,0,1

Global fnt_verdana=LoadFont("verdana",12,1,0,0)
SetFont fnt_verdana

movie=OpenMovie(filename$) 
If movie=0 Then RuntimeError "Error - Movie not loaded!" 
If Not(MoviePlaying(movie)) Then RuntimeError "Error - Movie not playing!" 

steps=12	; Starting Zoom/Pixel Size
w=64		; width of the movie displayed multiplied by the steps
h=48		; height of the movie displayed multiplied by the steps
x=0		; x position of the movie on screen 
y=0		; y position of the movie on screen

SetBuffer BackBuffer()

Dim pixcol(w,h)
movie_tmp=CreateImage(w,h)

freq=2		;
amp=128		; Sin Variables
count=1		;

Repeat

	SetBuffer ImageBuffer(movie_tmp)
	DrawMovie movie,x,y,w,h ; draw the movie
	
	LockBuffer ImageBuffer(movie_tmp)
	For x_=1 To w
		For y_=1 To h
			col=ReadPixelFast(x_,y_, ImageBuffer(movie_tmp)) And $ffffff	
			r = (col Shr 16) And $FF 
			;g = (col Shr 8) And $FF	; Twisted things with Xvid using G ?
			b = (col And $FF)
			value=(r+b)/2
			pixcol(x_,y_) = value
		Next
	Next
	UnlockBuffer ImageBuffer(movie_tmp)
	
	offx=(width-(w*steps))/2 ; the x position of the movie on screen 
	offy=(height-(h*steps))/2 ; the y position of the movie on screen

	SetBuffer BackBuffer()	
	Cls
	For x_=0 To w-1
		For y_=0 To h-1
			Color 0,pixcol(x_,y_),pixcol(x_,y_)
			sinx=Sin((count*freq)+(y_))*amp
			Rect ((x_*steps)+sinx)+offx,((y_*steps)-siny)+offy,steps+4,steps+4,1
		Next
		siny=Sin((count*freq)+(x_))*amp
	Next

	Color 255,255,255
	Text 0,0,"Steps = "+steps
	Text 0,12,"Amp = "+amp

	If KeyDown(59) steps=steps-1
	If steps<1 steps=1
	If KeyDown(60) steps=steps+1
	If steps>64 steps=64
	
	If KeyDown(61) amp=amp-1
	If amp<1 amp=1
	If KeyDown(62) amp=amp+1
	If amp>256 amp=256

	count=count+1
	If count>359 count=1	
Flip 
Until KeyHit(1)

CloseMovie(movie) 

End
