; ID: 689
; Author: Richard Betson
; Date: 2003-05-13 17:59:48
; Title: Fast FullScreen Blur/Fade
; Description: Blur/Fade a screen at 640x480x16.

;Blur/Fade Demo for B+ ver.beta
;Copyright 2003, Richard R Betson
;www.redeyeware.50megs.com
;vidiot@getgoin.net


;Blur/Fade FAST! Blit Method
;640 x 480 x 16 I get 330 Fps avg.
;


Const width=640
Const height=480
Const depth=16
Global fade_clr=2
Global fade#=7.57
Global blur=4;.05
Global height1=height-1
Global width1=(width/2)-1


xx1=308


Graphics width,height,depth,1


Global blockbank=CreateBank(500*500*4)
Global clrbank=CreateBank(height*width*4)


Global	clrt=((254/8 Shl 27) Or((24/4) Shl 21) Or((20/8) Shl 16) Or ((254/8) Shl 11) Or ((24/4) Shl 5) Or (20/8))  


SetBuffer BackBuffer()



LockBuffer
For y=0 To 49
	offset=y*LockedPitch()
	
	For x= 0 To 24
		PokeInt blockbank,offset+x*4,clr;
	Next
Next
UnlockBuffer


Repeat

If KeyHit(1) Then End
	If fpsl=True
		blur_fade()
	Else
		If ct<MilliSecs()
			ct=MilliSecs()+80
			blur_fade()
		EndIf
	EndIf


		If ct1<=MilliSecs()
			ct1=MilliSecs()+10
			clrt=((254/8 Shl 27) Or((Rnd(32)/4) Shl 21) Or((Rnd(254)/8) Shl 16) Or ((254/8) Shl 11) Or ((Rnd(32)/4) Shl 5) Or (Rnd(254)/8))  
		
			LockBuffer 
			
			xx=xx+2
			For i= 0 To 90
			yy=Rnd(468)+1
			;xx=Rnd(288)+1

			For y=yy To yy+1
				offset=y*LockedPitch()
				For x=xx To 10+xx
					PokeInt clrbank,offset+x*4,clrt

				Next
			Next
			Next
			If xx>=306 Then xx=0
			clrt=((254/8 Shl 27) Or((Rnd(32)/4) Shl 21) Or((Rnd(254)/8) Shl 16) Or ((254/8) Shl 11) Or ((Rnd(32)/4) Shl 5) Or (Rnd(254)/8))  
			
			xx1=xx1-2
			For i= 0 To 90
			yy=Rnd(468)+1
			;xx=Rnd(288)+1

			For y=yy To yy+1
				offset=y*LockedPitch()
				For x=xx1 To 10+xx1
					PokeInt clrbank,offset+x*4,clrt

				Next
			Next
			Next
			If xx1<=2 Then xx1=308
			UnlockBuffer
			;xx1=2		
		EndIf



	
		tm=MilliSecs()

	fps=fps+1
	If fps_t<MilliSecs()
	fp$=" "+Str$(fps)
	fps2=fps
	fps_t=1000+MilliSecs()
	fps=0
	EndIf
	;Color 255,255,255
	Text 10,15,"FPS: "+fp$

If KeyHit(6)=True
	SetBuffer FrontBuffer()
	SaveBuffer(FrontBuffer(),"shot.bmp") 
EndIf




	Flip 0
	
	
Forever

End







Function blur_fade()


LockBuffer
	bank=LockedPixels()
For y=1 To height

	offset=y*LockedPitch()
	offsetn=(y-1)*LockedPitch()
	offsets=(y+1)*LockedPitch()
	offsetz=(y-f_step)*LockedPitch()

	For x= 1 To width1

	If csa>10

		tc=tc4
		tc2=tc5
		tc3=tc6

		lc=((tc Shl 11) Or (tc2 Shl 5) Or (tc3))

		PokeShort clrbank,(offsetz)+x*4,lc 
	EndIf

		tmpa=PeekShort(clrbank,(offset+2)+x*4) 
		c1=PeekShort(clrbank,(offsetn+2)+x*4) 
		c2=PeekShort(clrbank,(offsets+2)+x*4) 
		c3=PeekShort(clrbank,(offset+2)+(x-1)*4) 
		c4=PeekShort(clrbank,(offset+2)+(x+1)*4) 


csa=(tmpa+c1+c2+c3+c4)

If csa>10

	tmp=((tmpa Shr 11) ) And $1F; And 255
	tc4=((tmp *blur)+(c1 Shr 11 And $1F) +(c2 Shr 11 And $1F) +(c3 Shr 11 And $1F) +(c4 Shr 11 And $1F))/fade; Shr 3
	tmp=(tmpa Shr 5 ) And $3F;And$FF;And 255 
	tc5=((tmp*blur)+(c1 Shr 5 And $3F) +(c2 Shr 5 And $3F) +(c3 Shr 5 And $3F) +(c4 Shr 5 And $3F) )/fade
	tmp=(tmpa ) And $1F;And$FF
	tc6=((tmp*blur)+(c1 And $1F)  + (c2 And $1F) + (c3 And $1F) + (c4 And $1F)) /fade


tc4=(tc4-fade_clr)
tc5=(tc5-fade_clr)
tc6=(tc6-fade_clr)

If tc4<0 Then tc4=0
If tc5<0 Then tc5=0
If tc6<0 Then tc6=0

hc=((tc4 Shl 11) Or (tc5 Shl 5) Or (tc6))

PokeShort clrbank,(offsetz+2)+x*4,hc 

EndIf


PokeInt bank,offset+x*4,(hc Shl 16 Or lc)

		
	Next
Next

UnlockBuffer 


End Function
