; ID: 690
; Author: Richard Betson
; Date: 2003-05-13 18:02:27
; Title: Nice Fire FX for B+ using Blit's
; Description: Nice Fire FX for B+ using Blit's

;Fire Demo for B+
;Copyright 2003, Richard R Betson
;www.redeyeware.50megs.com
;vidiot@getgoin.net

;Blur/Fade Fire... Blit Method

Global fpsl=True ;<------------ Change this to False for Fastest Diplay
							;   May result in some scree errors.
							;default = true

Const width=640
Const height=480
Const depth=16
Global fade_clr=2	;"                         "
Global fade#=7.58	;<=- Adjust for fade control
Global blur=4
Global height1=height-1
Global width1=(width/2)-1
Global f_step=3		;<=- Adjust for flame rise
Global yy=474

If fpsl=True
	timer=CreateTimer(32)
	fade_clr=2
	fade#=7.57
EndIf





Graphics width,height,depth,1
Global blockbank=CreateBank(500*500*4)
Global clrbank=CreateBank(width*height*4)
SetBuffer BackBuffer()

 


LockBuffer
	
For y=0 To 49
	offset=y*LockedPitch()
	
	For x= 0 To 24
		PokeInt blockbank,offset+x*4,clr;
	Next
Next
UnlockBuffer

While  Not KeyHit(1)
		If ct1<=tm
			ct1=tm+Rnd(30)
			LockBuffer 
		For i= 0 To 120+Rnd(50)
		
			xx=Rnd(345)+1
			gr=Rnd(112)+30
			clrt=((254/8 Shl 27) Or((gr/4) Shl 21) Or((40/8) Shl 16) Or ((254/8) Shl 11) Or ((gr/4) Shl 5) Or (40/8))  

			For y=yy To yy+5
				offset=y*LockedPitch()
				For x=xx To Rnd(15)+xx
					PokeInt clrbank,offset+x*4,clrt
				Next
			Next
		Next
			UnlockBuffer		
		EndIf



;Adjust this for fire speed
;		If ct<tm
			;ct=tm+32
;			blur_fade()
	If fpsl=True
		blur_fade()
		WaitTimer(timer)
	Else
		If ct<tm
			ct=tm+32
			blur_fade()
		EndIf
	EndIf
		;EndIf

;	If fpsl=True
;		WaitTimer(timer)
;	EndIf

	
	tm=MilliSecs()
	fps=fps+1
	If fps_t<MilliSecs()
	fp$=" "+Str$(fps)
	fps2=fps
	fps_t=1000+MilliSecs()
	fps=0
	EndIf
	Text 10,465,"FPS: "+fp$
	
	Flip False

Wend

End






Function blur_fade()


LockBuffer
		bank=LockedPixels()
For y=240 To height

	offset=y*LockedPitch()
	offsetn=(y-1)*LockedPitch()
	offsets=(y+1)*LockedPitch()
	offsetz=(y-f_step)*LockedPitch()

	For x= 1 To width1

		tmpa=PeekShort(clrbank,(offset+2)+x*4) ;And 255
		c1=PeekShort(clrbank,(offsetn+2)+x*4) ;And 255
		c2=PeekShort(clrbank,(offsets+2)+x*4) ;And 255
		c3=PeekShort(clrbank,(offset+2)+(x-1)*4) ;And 255
		c4=PeekShort(clrbank,(offset+2)+(x+1)*4) ;And 255


csa=(tmpa+c1+c2+c3+c4)

If csa>0
		tc=tc4
		tc2=tc5
		tc3=tc6

		lc=((tc Shl 11) Or (tc2 Shl 5) Or (tc3))


	tmp=((tmpa Shr 11) ) And $1F; And 255
	tc4=((tmp *blur)+(c1 Shr 11 And $1F) +(c2 Shr 11 And $1F) +(c3 Shr 11 And $1F) +(c4 Shr 11 And $1F))/fade; Shr 3
	tmp=(tmpa Shr 5 ) And $3F;And$FF;And 255 
	tc5=((tmp*blur)+(c1 Shr 5 And $3F) +(c2 Shr 5 And $3F) +(c3 Shr 5 And $3F) +(c4 Shr 5 And $3F) )/fade
	tmp=(tmpa ) And $1F;And$FF
	tc6=((tmp*blur)+(c1 And $1F)  + (c2 And $1F) + (c3 And $1F) + (c4 And $1F)) /fade
;EndIf


tc4=(tc4-fade_clr)
tc5=(tc5-fade_clr)
tc6=(tc6-fade_clr)

If tc4<0 Then tc4=0
If tc5<0 Then tc5=0
If tc6<0 Then tc6=0

hc=((tc4 Shl 11) Or (tc5 Shl 5) Or (tc6))

PokeInt clrbank,offsetz+x*4,(hc Shl 16 Or lc);


EndIf


PokeInt bank,(offset)+x*4,(hc Shl 16 Or lc)

		
	Next
Next

UnlockBuffer 

End Function
