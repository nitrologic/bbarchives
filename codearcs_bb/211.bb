; ID: 211
; Author: Faz
; Date: 2002-01-31 14:29:36
; Title: Bouncing Ball
; Description: Can be controlled with joypad.

; Small Test to see how things work, BOUNCE, JOYPAD and SOUND.... PLUS OVER A BACKGROUND!!!!
; FAZ

Graphics 800,600,16			;Do NOT ever change the 16 to 8 THE MACHINE WILL LOCK UP COMPLETELY...
SetBuffer BackBuffer()	    ;   ---                 --    - --------------------------------------
c$=CurrentDir$()
ChangeDir c$
ball1=LoadImage("GFX/ball_sml.bmp"):MidHandle ball1:MaskImage ball1,0,0,0
tile1=LoadImage("GFX/bg_temp.bmp"):HandleImage tile1,0,0
charge=LoadImage("GFX/glow.bmp"):HandleImage charge,12.5,12.5
wel=LoadImage("GFX/1.bmp")
bnce=LoadSound ("SFX/metal.wav")

.Variables
xv#=00:yv#=3
xb1#=400:yb1#=128
ledge=32:redge=800-32
wy=128
n=50
Dim tx(n),ty(n),amtx(n),amty(n)

SetBuffer BackBuffer()
;Gosub drawbg 
Gosub drawbg2
Gosub test1
VWait 8 

While MouseDown(2)<>1
	Gosub jpad
	DrawImage ball1,xb1,yb1
	Gosub velocity
	Flip
	Gosub drawbg2
Wend
End

.velocity
yb1=yb1+yv
If yv>=0 And yv<24  Then yv=yv+.5 ;falling
If yv<0    		    Then yv=yv+.60 ;rising

If yb1>540 Then SoundPan bnce,0:PlaySound bnce:yb1=540:yv=-yv
If yb1<32  Then SoundPan bnce,0:PlaySound bnce:yv=-yv

If xv<-12 Then xv=-12
If xv>12  Then xv=12 
If xv>0   Then xv=xv-.10
If xv<0   Then xv=xv+.10

If xb1<ledge Then SoundPan bnce,.05:PlaySound bnce:xb1=ledge:xv=-xv
If xb1>redge Then SoundPan bnce,-.05:PlaySound bnce:xb1=redge:xv=-xv
xb1=xb1+xv
Return

.drawbg2
	DrawImage tile1,0,0  ; **** Change tile1 to whatever file the image is!!!!
	Gosub mtest	
Return

.jpad
; ******** remove below when done
;If MouseDown(1)=1 Then SaveBuffer(FrontBuffer(),"GFX/buffer1.bmp")
; ********
If yb1>500 Then Goto magnet2
If JoyXDir()=-1 And yb1>138 Then xv=xv-.25 Else If JoyXDir()=-1 And yb1<=138 And xb1>ledge Then xb1=xb1-8 
If JoyXDir()=1  And yb1>138 Then xv=xv+.25 Else If JoyXDir()=1  And yb1<=138 And xb1<redge Then xb1=xb1+8
.magnet2
If JoyYDir()=1  And yb1>128 Then yb1=yb1-8:DrawImage charge,xb1,yb1:xv=0:yv=0
If JoyYDir()=-1 And yb1>0 Then yv=yv+.5
Return
.grab
ch#=ImageHeight(charge)
For cy=128 To yb1 Step -24
	DrawImage charge,xb1,cy
Next
Return
.mtest
For a=1 To n
	tx(a)=tx(a)+amtx(a)
	ty(a)=ty(a)+amty(a)
	If tx(a)>redge Then tx(a)=redge:amtx(a)=-amtx(a)
	If tx(a)<ledge Then tx(a)=ledge:amtx(a)=-amtx(a)
	If ty(a)>540 Then ty(a)=540:amty(a)=-amty(a)
	If ty(a)<128 Then ty(a)=128:amty(a)=-amty(a)
	DrawImage charge,tx(a),ty(a)
Next
Return
.test1
	For a=1 To n
		tx(a)=24+Int(Rnd(740))
		ty(a)=128+Int(Rnd(540))
		amt(a)=4+Int(Rnd(8))
		DrawImage charge,tx(a),ty(a)
	Next
Return
;.DrawBG
;x=12:y=12
;.aga1
;	For x=12 To 800-128 Step 128
;		DrawBlock tile1,x,y
;	Next
;	If y<600-128 Then y=y+128:Goto aga1
;Return
