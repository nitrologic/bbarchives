; ID: 444
; Author: Richard Betson
; Date: 2002-10-01 00:20:16
; Title: The Perfect Sign
; Description: Sin Wave Plot..VERY FAST

;The Perfect Sign - Copyright 2002, Richard R Betson
;vidiot@getgoin.net -- www.redeyeware.50megs.com
;Speed with lookups! Use at your own risk. All rights reserved.



Graphics 800,600,16
stp=45
Dim mb#(201,201,stp+1,2)

Text 300,290,"Making look-up's"

SetBuffer BackBuffer()

clr=(255 Or 146 Shl 8 Or 146 Shl 16)

timer=CreateTimer(75)

lu(stp)
cr1=MilliSecs()+10000
cr2=MilliSecs()+20000
cr3=MilliSecs()+30000
fnt=LoadFont("Arial",36,False,False,False)
SetFont fnt

While Not KeyHit(1)
LockBuffer BackBuffer()

For y=0 To 200
	For x=0 To 200
	clr=((255-(y/2)) Shl 8 Or (16+(y/4)) Or  (155+(x/2)) Shl 16 )

	a#=mb#(x,y,ii,0)
	b#=mb#(x,y,ii,1)

	If a>1 And a<799 And b>-299 And b<399
		WritePixelFast a+110,b+240,clr;-x;x+100
	EndIf

	Next
Next
UnlockBuffer BackBuffer

	fps=fps+1
	If fps_t<MilliSecs()
	fp$=" "+Str$(fps)
	fps_t=1000+MilliSecs()
	fps=0
	EndIf
	Color 255,0,0
	Text 10,10,"The Perfect Sign - Copyright 2002, Richard R Betson"
	
	
	
	Text 10,35,"FPS: "+fp$




WaitTimer(timer)

Flip
Cls
ii=ii+1
If ii>stp Then ii=0
Wend


End



Function LU(stp)


For i=0 To stp

.jmp

If t<MilliSecs()
t=MilliSecs()+50
mt#=(MilliSecs()/8)*1.2


For y=0 To 200
	For x=0 To 200
		rad#=Abs(((x-100))*(x-100))+Abs((y-100)*(y-100))

		If rad>0
			rad= Sqr(rad)
		Else
			rad=0
		EndIf

		a#=(x*2)+(Sin((y-100-mt))/y) + y-(Cos(y-100-mt)/y)
		b#=(y)-100-(Sin((rad*20)-(mt))*(rad/5.9)) + y/2-(Cos((rad*34)-(mt))/(rad/40))
		mb#(x,y,i,0)=a#
		mb#(x,y,i,1)=b#
	Next
Next
Else
Goto jmp
EndIf

Next

End Function
