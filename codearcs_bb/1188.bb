; ID: 1188
; Author: Cygnus
; Date: 2004-11-07 18:59:45
; Title: Ultimate Mushroom Collector 2004
; Description: Boulderdash with computer players playing too.... chav fight for 'shrooms!

Global ss_channelcount%
Global ss_bpsample%=16

Global ss_maxchans=50
Global ss_buffersize=1000
Global ss_maxenvs=30
Global ss_sndtype=8
Global ss_lfocount=2

Dim ss_sounds(ss_maxchans)
Dim ss_volume#(ss_maxchans)
Dim ss_schan(ss_maxchans)
Dim ss_sLength#(ss_maxchans)
Dim ss_started(ss_maxchans)
Dim ss_actualvol#(ss_maxchans)
Dim ss_attackdone(ss_maxchans)
Dim ss_decaydone(ss_maxchans)

Dim ss_attack#(ss_maxchans)
Dim ss_decay#(ss_maxchans)
Dim ss_sustain#(ss_maxchans)
Dim ss_release#(ss_maxchans)
Dim ss_currentpitch#(ss_maxchans)
Dim ss_envelope(ss_maxchans)

Dim ss_defattack#(ss_maxenvs)
Dim ss_defdecay#(ss_maxenvs)
Dim ss_defsustain#(ss_maxenvs)
Dim ss_defrelease#(ss_maxenvs)
Dim ss_attackchange#(ss_maxenvs)
Dim ss_decaychange#(ss_maxenvs)
Dim ss_sustainchange#(ss_maxenvs)
Dim ss_releasechange#(ss_maxenvs)

Dim ss_lfoattack#(ss_lfocount,ss_maxenvs)
Dim ss_lfodecay#(ss_lfocount,ss_maxenvs)
Dim ss_lfosustain#(ss_lfocount,ss_maxenvs)
Dim ss_lforelease#(ss_lfocount,ss_maxenvs)

Dim ss_lfo#(ss_lfocount,ss_maxchans)
Dim ss_lfofreq#(ss_lfocount,ss_maxenvs)
Dim ss_lfocounter#(ss_maxchans)
Dim ss_bufferchan(ss_maxchans,ss_buffersize)
Dim ss_buffervol#(ss_maxchans,ss_buffersize)
Dim ss_bufferenv#(ss_maxchans,ss_buffersize)
Dim ss_bufferpitch(ss_maxchans,ss_buffersize)
Dim ss_bufferlength(ss_maxchans,ss_buffersize)
Dim ss_bufferpointer(ss_maxchans)
Dim ss_buffercount(ss_maxchans)
Dim ss_chancheck(ss_maxchans)
Dim ss_channelloop(ss_maxchans)
Dim ss_samplecnt(ss_maxchans)
Dim ss_soundbank(ss_sndtype),ss_samplecntbank(ss_sndtype)
Global forcesoundupdate=0
Global ss_stimer#=100
Global audiotimer=MilliSecs(),updateperiod#=1
Dim wavdata(0,0)
initsoundsystem()


updateperiod#=15
For n=1 To ss_maxchans
soundshape n,1
Next

Global Nxtchan=1
Global dirx,diry
Type path
Field x
Field y
End Type

Type boulder
Field sx,sy
Field x,y
Field player
Field reposition
Field timer
Field triggered
Field rocknumber
End Type
Global  ignoreboulders

Global Lastspread
Global scx#=640,scy#=480
scx=800
scy=600

Global Topbit=50
Graphics scx,scy,16,2
Global gametimer=CreateTimer(80)
Global basey=Topbit
TFormFilter 0
Global maxx=40
Global maxy=30
maxx=40/1.3
maxy=30/1.3
;maxx=40*2
;maxy=30*2
Global scrollmode
Global camx=0,ocamx
Global camy=0,ocamy
Global cammvx,cammvy

Global szx#=(scx/(maxx))
Global szy#=((scy-Topbit)/(maxy))
szx=32
szy=32
;szx=16
;szy=16
maxx=maxx-1
maxy=maxy-1
Global Textcnt=8
Dim Charimg(256,Textcnt)
Dim map(maxx+1,maxy+1)
Dim map2(maxx+1,maxy+1) ;Stores walkable area. Used to ensure players arent put where they cant get to or from.
Dim map3(maxx+1,maxy+1) ;Mushroom layer
Dim map4(maxx+1,maxy+1);AI layer
;Dim map4(m
Dim alive(maxx+1,maxy+1)
Dim mapc(4),mapd(4)
mapc(1)=1:mapd(1)=0
mapc(2)=0:mapd(2)=1
mapc(3)=-1:mapd(3)=0
mapc(4)=0:mapd(4)=-1
Global stack
Dim stackx(maxx*maxy)
Dim stacky(maxx*maxy)
Dim stackc(maxx*maxy)
Dim stackd(maxx*maxy)
Global mushrooms
Global mushlife=7
Global images=8
Dim img(images)
Global maxplayers=16
Global players=1
Dim playertype(maxplayers)
playertype(1)=1
Dim px#(maxplayers),py#(maxplayers),pc#(maxplayers),pd#(maxplayers)
Dim fr#(maxplayers)
Dim ok2move(maxplayers)
Dim score(maxplayers)
Dim lives(maxplayers),energy#(maxplayers)
Dim sx(maxplayers)
Dim sy(maxplayers)
Dim diecounter(maxplayers)
Dim dead(maxplayers)
Dim mushs(maxplayers)
Dim playername$(maxplayers)
Dim Lastupdate(maxplayers)
For n=1 To maxplayers
playername$(n)="Player"+Str$(n)
Next
Dim upkey(maxplayers),downkey(maxplayers),Leftkey(maxplayers),Rightkey(maxplayers),dropkey(maxplayers)

Leftkey(1)=203
rightkey(1)=205
upkey(1)=200
downkey(1)=208

Dim red(maxplayers),green(maxplayers),blue(maxplayers)
Dim pdx(maxplayers),pdy(maxplayers)
Dim cfx(maxplayers),cfy(maxplayers),ctx(maxplayers),cty(maxplayers)
Dim playerimg(maxplayers,2)
For o=0 To maxplayers
red(o)=0
green(o)=255
blue(o)=0
Next
Read numcols
For o=1 To numcols
Read red(o)
Read green(o)
Read blue(o)
Next
Global hatred=100,hatgreen=200,hatblue=0


;setup text
Global txsize#=16,txscale#=2,txxdis#=.5
setuptext()
rszx=szx
rszy=szy
Global level=1

.restart
;setup music
setupmusic
Repeat:Until KeyDown(1)=0
level=1
SetBuffer BackBuffer()
menuoption=0
Cls

createlevel(100)
camx=0:camy=0

quit=0
Repeat
by=(scy/2)
option=option-(KeyHit(200)-KeyHit(208))
If option<0 Then option=2
If option>2 Then option=0
yy=by+option*32

text2 scx/2,scy/10,"ULTIMATE MUSHROOM COLLECTOR 2004",1,1
Text2 scx/2,(scy/8)+42,"Collect as many mushrooms as you can!",1,1

Text2 scx/2,(scy/8)+64,"Be the Last surviving player!",1,1
Text2 scx/3,yy,">>"
Text2 scx/2.5,by,"START"
done=0
kh=KeyHit(57)
If KH And option=0 Then done=1
If KH And option=1 Then sel=sel+1:If sel>1 Then sel=0
If kh And option=2 Then quit=1
If sel=0 Then scrollmode=0:Op$="Non-Scrolling" Else scrollmode=1:op$="Scrolling mode"
Text2 scx/2.5,by+32,op$
Text2 scx/2.5,by+64,"Quit"
updatesound
Flip
Cls
Until done Or KeyDown(1) Or quit=1

If scrollmode=0 Then
	szx=(scx/(maxx+1))
	szy=((scy-basey)/(maxy+1))
Else
	szx=rszx
	szy=rszy
EndIf
If KeyDown(1) Or quit=1 Then End

;do graphics
Global inv=Rand(1,4)

For o=0 To images
img(o)=creategfx(o)
Next
img(0)=img(3)


SetBuffer BackBuffer()

For o=1 To maxplayers
hatred=red(o)
hatgreen=green(o)
hatblue=blue(o)
If o=1 Then vr=1 Else vr=Rand(2,3)
playerimg(o,1)=creategfx(255,vr)
playerimg(o,2)=creategfx(256,vr)
Next

For n=1 To players
playertype(n)=1
lives(n)=3
Next

For n=players+1 To maxplayers
lives(n)=3
Next


SeedRnd MilliSecs()
.nextlevel
Delete Each boulder
createlevel(level)
;findpath(px(1),py(1),px(2),py(2))
;map(px(1),py(1))=1
;map(px(2),py(2))=1


Flip
drawmap()
Flip
drawmap()
Flip

SetBuffer BackBuffer()
Global starting=MilliSecs()+5000
Global gamestarted=0
acnt=0
finished=0
finishtimer=0
Global updbuffer
If (maxx*szx)<scx And (maxy*szy)<scy Then scrollmode=0 Else scrollmode=1
won=0
Repeat
updbuffer=0
If (MilliSecs()-lstbf)>1000/30 Then updbuffer=1:lstbf=MilliSecs()
;updbuffer=1

;updbuffer=(updbuffer+1) Mod 2
spread
If gamestarted=0 Then drawmap()
If gamestarted=0 And starting<MilliSecs() Then gamestarted=1:DRAWMAP

dfloor

dfx=camx
dfy=camy
dfx=dfx+(scx/2)
dfy=dfy+(scy/2)
dfx=dfx/szx
dfy=dfy/szy

If scrollmode Then drawmapfrom dfx,dfy;camx,camy;px(1),py(1)


If (finished=1 And scrollmode=0) Or dm=1 Then
For x=(maxx/2)-7 To (maxx/2)+7
For y=(maxy/2)-5 To (maxy/2)+2
drawtile x,y
Next
Next
EndIf

dm=0


drawplayers
If gamestarted Then updateplayers

For n=1 To maxplayers
If starting>MilliSecs() And playertype(n)=1 Then
mx=px(n)*szx
my=py(n)*szy

Text2 mx,((my)+basey)-szy,playername$(n),1,0
EndIf
dm=1
Next

updateboulders


colour 0,0,0
Rect 0,0,scx,basey-1,1
colour 255,255,255
dm=0
If gamestarted=0 Then
tx$="Level "+Str$(level)+"!!!"
Text2 scx/2,((scy-basey)/2)-txsize,tx$,1,1
tx$=Int((starting-MilliSecs())/1000)
If tx$="4" Then tx$=""
If tx$="0" Then tx$="GO!!!!"
Text2 scx/2,(scy-basey)/2,tx$,1,1
dm=1
EndIf
nv2=1
For nv#=1 To maxplayers
If lives(nv)>0 Then
Text2 (scx/4)*(nv2-.5),10,playername$(nv),1,1
Text2 (scx/4)*(nv2-.25),32,"x"+lives(nv),1,1
Text2 (scx/4)*(nv2-.9),32,constr(score(nv),6),0,1
nv2=nv2+1
EndIf
Next

;won=0
cnt=0
ck=0
For n=1 To maxplayers
	If lives(n)>0 Then cnt=cnt+1:If won=0 Then ck=1
Next
sc=0
If cnt=1 Then
	If ck=1 Then
		For n2=1 To maxplayers
			If score(n2)>sc Then won=n2:sc=score(n2)
		Next
	EndIf
EndIf

If cnt<2 Then Text2 scx/2,(scy-basey)/2,playername$(won)+" WINS!!!",1,1:dm=1
If finished=1 And lives(1)>0 And won=0 And cnt>0 Then Text2 scx/2,(scy-basey)/2,"Level Complete!",1,1:dm=1
If finished=1 And lives(1)<1 Then Text2 scx/2,(scy-basey)/2,"G A M E   O V E R ! ! !",1,1:dm=1:gamestarted=1

If updbuffer=1 Then Flip 0:CopyRect 0,0,scx,scy,0,0,FrontBuffer(),BackBuffer()
Repeat
updatesound()
Until (MilliSecs()-Lastcycle)>1000/60 Or KeyDown(1)
Lastcycle=MilliSecs()
;Cls
;updatesound()
If mushrooms=0 And finished=0 Then finished=1:finishedtimer=MilliSecs()+5000
If lives(1)=0 And FINISHED=0 Then finished=1:finishedtimer=MilliSecs()+5000:STOPSOUND():diefx
If cnt=1 And FINISHED=0 Then finished=1:finishedtimer=MilliSecs()+5000:STOPSOUND():winfx

Until KeyDown(1) Or (finished=1 And finishedtimer<MilliSecs())
If lives(1)=0 Or cnt<2 Then Goto restart
level=level+1
If KeyDown(1) Then Goto restart
Goto Nextlevel
End
Function colour(r1,g1,b1)
r=r1:g=g1:b=b1
inv=4
If inv=1 Then r=b1:g=g1:b=r1
If inv=2 Then r=g1:g=b1:b=r1
If inv=3 Then r=r1:g=b1:b=g1
If inv=4 Then r=r1:g=g1:b=b1
rn#=Rnd(.75,1.5)
rn=-((r-128)+(g-128)+(b-120))/3
;rn=((r-128)+(g-128)+(b-120))/6
rn=rn*1.5
rn=1-(rn/128)
;rn=rn*20

;r=r*rn:g=g*rn:b=b*rn
If r>255 Then r=255
If g>255 Then g=255
If b>255 Then b=255
If r<0 Then r=0
If g<0 Then g=0
If b<0 Then b=0
;If r<>0 Or g<>0 Or b<>0 Then r=255-r:g=255-g:b=255-b
Color r,g,b
End Function
Function drawmap()
dfloor
Local x,y
For x=0 To maxx
For y=0 To maxy
updatesound
drawtile x,y
Next
Next
;Flip
End Function

Function drawmapfrom(x,y)
Local sx,sy,ex,ey,mx,my
If cammvx<>0  Or cammvy<>0 Then
;Cls
scy=scy-basey
sx=(scx/2)/szx+x
sy=(scy/2)/szy+y
ex=sx+(scx/szx)+x
ey=sy+(scy/szy)+y

sx=x-(scx/2)/szx
sy=y-(scy/2)/szy
ex=x+(scx/2)/szx
ey=y+(scy/2)/szy
CopyRect 0,basey,scx,scy,cammvx,cammvy+basey
xtm#=Float(cammvx)/Float(szx)
ytm#=Float(cammvy)/Float(szy)
If Abs(xtm)<1 Then xtm=Sgn(xtm)
If Abs(ytm)<1 Then xtm=Sgn(ytm)
If Abs(xtm)>3 Or Abs(ytm)>3 Then cammvx=0:cammvy=0
If cammvx>0 Then ex=sx+xtm+1:sy=sy-1:ey=ey+1
If cammvx<0 Then sx=(ex-xtm)-1:sy=sy-1:ey=ey+1

If cammvy>0 Then ey=(sy+ytm)+1:sy=sy-1
If cammvy<0 Then sy=(ey-ytm)-2
For mx=sx-1 To ex
For my=sy-1 To ey
If mx>-1 And mx<maxx+1 And my>-1 And my<maxy+1 Then
drawtile mx,my
Else
EndIf
Next
Next
scy=scy+basey
cammvx=0
cammvy=0
EndIf
End Function

Function updateplayers()
For n=1 To  maxplayers
If dead(n) Then Goto dontupdateplayer
mx#=px(n)
my#=py(n)
If MX<0 Then MX=0
If MX>MAXX Then MX=MAXX
If MY<0 Then MY=0
If MY>MAXY Then MY=MAXY
If map4(mx,my-1) Then addboulder(mx,my-1,n,25)
If map4(mx,my+1) And pd(n)>0 Then addboulder(mx,my+1,n,1)
If ((MilliSecs()-Lastupdate(n))>50000)  Or ok2move(n)=1 Then



If playertype(n)=0 Then ; if AI player
ignoreboulders=1
If pc(n)<>0 Or pd(N)<>0 Then
musha=0
	For c=-1 To 1
		For d=-1 To 1
			If c=0 Or d=0 And (c<>0 Or d<>0) Then
				If map3(mx+c,my+d)<>0 Then pc(n)=c:pd(n)=d:musha=1
			EndIf
		Next
	Next
	If Rnd(0,100)>60 Or musha=1 Then Goto dontdoanythingAI
EndIf

mdst=500
c=pc(n)
d=pd(n)
If map3(px(n)+c,py(n)+d )=4 Then Goto nochangecd
pc(n)=0
pd(n)=0
tx=0:ty=0
For c=-30 To 30
For d=-30 To 30
xx=mx+c:yy=my+d
dst=Abs((xx-mx))+Abs((yy-my));/2.0
If xx<0 Then xx=0 Else If xx>maxx Then xx=maxx
If yy<0 Then yy=0 Else If yy>maxy Then yy=maxy
;If (c<>0 Or d<>0) And (c=0 Or d=0) Then
If map3(xx,yy)<>0 And dst<=mdst Then
;	If mapok(mx+Sgn(c),my+Sgn(d)) Then pc(n)=Sgn(c):pd(n)=Sgn(d):mdst=dst:tx=mx+c:ty=my+d
ok=1
For c2=1 To c:For d2=1 To d
If mapok(mx+c2,my+d2)=0 Then ok=0
Next:Next
If ok=1 Then pc(n)=Sgn(c):pd(n)=Sgn(d):mdst=dst:tx=mx+c:ty=my+d:If dst<3 Then tx=0:ty=0
EndIf
;EndIf
Next
Next
;If mapok(mx+pc(n),my+pd(n))=0 Then pc(n)=-pc(n):pd(n)=-pd(n)

;pc(n)=0
;pd(n)=0
If Rand(0,100)>95 Then pc(n)=0:pd(n)=0

If pc(n)=0 And pd(n)=0 Then

For c=-1 To 1
For d=-1 To 1
xx=mx+c:yy=my+d
dst=Abs((xx-mx))+Abs((yy-my))/2.0
If xx<0 Then xx=0 Else If xx>maxx Then xx=maxx
If yy<0 Then yy=0 Else If yy>maxy Then yy=maxy
;If (c<>0 Or d<>0) And (c=0 Or d=0) Then
If mapok(xx,yy) Then
pc(n)=Sgn(c):pd(n)=Sgn(d):mdst=dst
EndIf
;EndIf
Next
Next


EndIf

.nochangecd
;ln=findpath(cfx(n),cfy(n),ctx(n),cty(n))

;If mapok(mx+pc(n),my+pd(n))=0 Then
oka=okahead(mx,my,pc(n),pd(n)) 
If oka<0 And oka>-3 Then
lds=1
opc=pc(n)
opd=pd(n)

findbestdirection(mx,my)
If dirx<>0 Or diry<>0 Then pc(n)=dirx:pd(n)=diry

If pc(n)=0 And pd(n)=0 Then
For c=-10 To 10
For d=-10 To 10
xx=mx+c:yy=my+d
;dst=Abs((xx-mx))+Abs((yy-my))
If xx<0 Then xx=0 Else If xx>maxx Then xx=maxx
If yy<0 Then yy=0 Else If yy>maxy Then yy=maxy
If (c<>0 Or d<>0) And (c=0 Or d=0) Then
If map2(xx,yy)<>0 Then
pc(n)=Sgn(c):pd(n)=Sgn(d):mdst=dst
EndIf
EndIf
Next
Next
EndIf


;If OKAHEAD(Xx,yy,pc(n),pd(n))<>1 Then pc(n)=-opc:pd(n)=-opd
EndIf

.justmove


xx=mx+Sgn(pc(n))
yy=my+Sgn(pd(n))


For n2=1 To maxplayers
If xx=px(n2) And yy=py(n2) Then pc(n)=-pc(n):pd(n)=-pd(n)
Next
ok2move(n)=0
If pc(n)<>0 And pd(n)<>0 Then
If Rand(2)=1 Then pc(n)=0 Else pd(n)=0
EndIf
;If map3(mx,my)=4 Then map3(mx,my)=0

mapv=map(mx+pc(n),my+pd(n))
If mapv<>3 And mapv<>2 Then pc(n)=0:pd(n)=0


If (pc(n)=0 And pd(N)=0) Or mapok(mx+pc(n),my+pd(n))=0 Then
findbestdirection(mx,my)
If dirx<>0 Or diry<>0 Then pc(n)=dirx:pd(n)=diry
EndIf


.dontdoanythingAI
ignoreboulders=0

Else

;Player controlled stuff
opc=pc(N)
opd=pd(n)
pc(N)=0
pd(n)=0
pc(n)=-(KeyDown(Leftkey(n))-KeyDown(Rightkey(n)))
pd(n)=KeyDown(downkey(n))-KeyDown(upkey(n))
If pc(N)<>0 And pd(N)<>0 Then
If pc(n)<>opc Then pd(n)=0
If pd(N)<>opd Then pc(N)=0
EndIf

EndIf
ignoreboulders=0

c=Sgn(pc(n))
mx=px(n):my=py(N)
If map4(mx+c,my)<>0 And Abs(c)<>0 Then
addboulder(mx+c,my,n,10)
If mx>2 And mx<maxx-2 Then
If map4(mx+c*2,my)=0 And mapok(mx+c*2,my) Then map4(mx+c*2,my)=map4(mx+c,my):map4(mx+c,my)=0:addboulder(mx+c*2,my,n,10):If mapok(mx+c*3,my)=0 And mapok(mx+c*2,my+1)=0 Then map3(mx+c*2,my)=0:map2(mx+c*2,my)=0
EndIf
EndIf


If mapok(px(n)+pc(n),py(n)+pd(n))=0 Then pc(n)=0:pd(N)=0


px(n)=Int(px(n)):py(n)=Int(py(n))
px(n)=px(n)+Sgn(pc(n))
py(n)=py(n)+Sgn(pd(n))
ok2move(n)=0
lastupdate(n)=MilliSecs()
EndIf
.dontupdateplayer
Next
End Function
Function drawplayers()
Local n,x,y

For n=1 To maxplayers
mx=px(n)
my=py(n)
If MAP3(MX,MY)=4 Then map3(mx,my)=0:score(n)=score(n)+10:If n=1 Then collectsfx(1)
For x=mx-1 To mx+1
For y=my-1 To my+1
;If scrollmode=0 Then
drawtile(x,y)
Next:Next
Next

For n=1 To maxplayers
If lives(n)<1 Then diecounter(n)=10:dead(n)=1:px(n)=0:py(N)=0:pdx(n)=0:pdy(n)=0
If diecounter(n)>0 Then
	diecounter(n)=diecounter(n)-1
	If diecounter(n)=0 Then positionplayer(n,sx(n),sy(n)):dead(n)=0:map4(sx(n),sy(n))=0:drawmap()
	Goto nxtplay
EndIf
If dead(n)=1 Then Goto nxtplay
mx=px(n);/szx
my=py(n);/szy
xad#=(((px(n)*szx)-pdx(n)))
yad#=(((py(n)*szy)-pdy(n)))
maxm=szx/16
If Abs(xad)>maxm Then xad=Sgn(xad)*maxm
If Abs(yad)>maxm Then yad=Sgn(yad)*maxm

If Abs(xad)<1 And Abs(yad)<1 Then ok2move(n)=1

ok2m=0
If Abs(xad)<1 Then pdx(n)=mx*szx:xad=0
If Abs(yad)<1 Then pdy(n)=my*szy:yad=0
;xad=xad/10.0
;yad=yad/10.0

pdx(n)=pdx(n)+xad
pdy(n)=pdy(n)+yad
;PDX(N)=PX(N)*SZX
;PDY(N)=PY(N)*SZY
	;sound 2,1,101,1
;	sound 3,1,102,1

If pc(n)<>0 Or pd(n)<>0 Then fr(n)=fr(n)+.1
If fr(n)>1.9 Then fr(n)=0

If updbuffer=1 Then DrawImage playerimg(n,Floor(fr(n))+1),pdx(n)-camx,(pdy(n)-camy)+basey
 If map4(Int(pdx(n)/szx),Int(pdy(n)/szy))<>0 Then
 	For x=mx-1 To mx+1
	For y=my-1 To my+1
	;If scrollmode=0 Then
	drawtile(x,y)
	Next:Next
 	die n
EndIf

 .nxtplay
Next

End Function

Function Creategfx(number,variant=1)
Local image=CreateImage(szx,szy),cb#,x#,y#,x1#,x2#,cnt,n#,ex#,ey#,bx#,by#,sx#
cb=GraphicsBuffer()
SetBuffer ImageBuffer(image)
If number=1 ;brick
	colour 0,220,50
	colour 0,255,255
	Rect 0,0,szx,szy,1
	colour 0,0,0
	stp=szy/4.0
	cnt=0
	For n=0 To szy Step 1
		cnt=(cnt+1) Mod 2
		Line 0,n,szx,n
		For p=0 To 4
			bs=cnt*(szx/8)
			Line (p*szx/4)+bs,n,(p*szx/4)+bs,stp+n
		Next
		n=n+stp-1
	Next
EndIf

If number=2 Then	;mud
	colour 150,100,0
	Rect 0,0,szx,szy,1
	colour 0,0,0
	For n=0 To szx
		Plot Rnd(szx),Rnd(szy)
	Next
EndIf

If number=3 Then	;grass
	colour 0,170,0
	Rect 0,0,szx,szy,1
	colour 0,100,0
	For n=0 To 50
		Plot Rnd(szx),Rnd(szy)
	Next
EndIf

If number=4 Then
;	colour 0,170,0
	;Rect 0,0,szx,szy,1
	;colour 0,254,200
	For sx#=0 To 1 Step .05;(szx/4)*2
	ex#=szx/2.0
	ey#=szy/3.0
	x1#=ex*sx
	x2#=ey*sx
	colour 0,254*sx,200*sx
	bx=(szx/4)
	by=(szy/3)
		Oval bx+(ex-x1)/2.0,by+(ey-x2)/2.0,x1,x2,0
	Next
;	Oval szx/4,szy/3,(szx/4)*2,szy/3

	colour 0,0,0
	Rect 0,(szy/2)+szy/10,szx,szy/2
	colour 140,120,0
	Rect (szx/2)-szx/8,(szy/3)*2,szx/4,szy/6,1
	Oval (szx/2)-szx/8,(szy/2),szx/4,szy/3,1

	;For n=0 To szx
	;x=Rnd(szx)
	;y=Rnd(szy)
;	Getcolour(x,y)
	;If colourRed()=0 And colourGreen()=170 And colourBlue()=0 Then colour 0,100,0:Plot x,y
;	Next


EndIf

If number=5 ;red brick
	colour 224,70,0
	;colour 224,50,100
	Rect 0,0,szx,szy,1
	colour 0,0,0
	stp=szy/4.0
	cnt=0
	For n=0 To szy Step 1
		cnt=(cnt+1) Mod 2
		Line 0,n,szx,n
		For p=0 To 4
			bs=cnt*(szx/8)
			Line (p*szx/4)+bs,n,(p*szx/4)+bs,stp+n
		Next
		n=n+stp-1
	Next
EndIf

If number=6 ;edge wall
For sx=1 To 0 Step -.01
colour 255,255*sx,0
x=sx*szx
y=sx*szy
Line x,y,szx-x,y
Line szx-x,y,szx-x,szy-y
;Line x,y,szx/2,y
;Line x,y,x,szy/2
x=szx-(sx*szx)
y=szy-(sx*szy)
Next
colour 10,10,10
Line 0,0,szx,szy
Line szx,0,0,szy
Rect 0,0,szx+1,szy+1,0
EndIf

If number=7 ;water
	colour 50,60,200
	Rect 0,0,szx,szy,1
	colour 100,100,255
	For n=0 To szx/2.0
		Plot Rnd(szx),Rnd(szy)
	Next
EndIf

If number=8 Then ;rock
colour 200,200,0
;Oval (scx/2)-scx/3,szy/2,(szx/2)-1,szy/2
Oval 0,0,szx-1,(szy-1)/1.1

colour 255,255,0
Oval 0,0,szx-1,(szy-1)/1.2
EndIf
If number=255 Then	;Player walk frame 1
colour 255,255,255
Oval szx/4,szy/6,(szx/4.0)*3.0,szy/6+szx/2.5,0
Oval (szx/2)-szx/6,0,(szx/2)+szx/12,szy/8+szy/2,0

x1=1
x2=2
colour hatred,hatgreen,hatblue
Oval ((szx/2)-szx/6)+x1,0+x1,((szx/2)+szx/12)-x2,(szy/8+szy/2)-x2,1
colour 0,250,200
If variant=2 Then colour 80,120,0
If variant=3 Then colour 200,200,10
Oval (szx/4)+x1,(szy/6)+x1,((szx/4.0)*3.0)-x2,(szy/6+szx/2.5)-x2,1


colour 255,255,255
Line ((szx/2)+szx/3),szy/6,0,szy/6.0
colour 255,0,255
Oval (szx/3)+szx/12,szy/3,szx/8,szy/8,1
Oval ((szx/3)*2)+szx/12,szy/3,szx/8,szy/8,1
;Plot szx/3,szy/3
;Plot (szx/3)*2,szy/3

colour 255,255,255
Line szx/3,(szy/3)*2,szx/5,((szy/3)*2)+szy/12
Line (szx-(szx/3))+szx/6,(szy/3)*2,(szx-(szx/5)+szx/6),((szy/3)*2)+szy/6

Line szx/2.2,(szy/3)*2,szx/2.2,((szy/3)*2)+szy/5
Line (szx-(szx/2.2))+szx/6,(szy/3)*2,(szx-(szx/2.2))+szx/6,((szy/3)*2)+szy/12
EndIf

If number=256 Then	;Player walk frame 2
colour 255,255,255
Oval szx/4,szy/6,(szx/4.0)*3.0,szy/6+szx/2.5,0
Oval (szx/2)-szx/6,0,(szx/2)+szx/12,szy/8+szy/2,0

x1=1
x2=2
colour hatred,hatgreen,hatblue
Oval ((szx/2)-szx/6)+x1,0+x1,((szx/2)+szx/12)-x2,(szy/8+szy/2)-x2,1
colour 0,250,200
If variant=2 Then colour 80,120,0
If variant=3 Then colour 200,200,10

Oval (szx/4)+x1,(szy/6)+x1,((szx/4.0)*3.0)-x2,(szy/6+szx/2.5)-x2,1


colour 255,255,255
Line ((szx/2)+szx/3),szy/6,0,szy/6.0
colour 255,0,255
Oval (szx/3)+szx/12,szy/3,szx/8,szy/8,1
Oval ((szx/3)*2)+szx/12,szy/3,szx/8,szy/8,1
;Plot szx/3,szy/3
;Plot (szx/3)*2,szy/3

colour 255,255,255
Line szx/3,(szy/3)*2,szx/5,((szy/3)*2)+szy/6
Line (szx-(szx/3))+szx/6,(szy/3)*2,(szx-(szx/5)+szx/6),((szy/3)*2)+szy/12

Line szx/2.2,(szy/3)*2,szx/2.2,((szy/3)*2)+szy/12
Line (szx-(szx/2.2))+szx/6,(szy/3)*2,(szx-(szx/2.2))+szx/6,((szy/3)*2)+szy/5
EndIf

SetBuffer cb
Return image
End Function

Function setmap(x,y,set,mapset=1)
If mapset=1 Then
map(x,y)=set
map(maxx-x,y)=set
map(maxx-x,maxy-y)=set
map(x,maxy-y)=set
EndIf
If mapset=2 Then
map2(x,y)=set
map2(maxx-x,y)=set
map2(maxx-x,maxy-y)=set
map2(x,maxy-y)=set
EndIf
If mapset=3 Then
map3(x,y)=set
map3(maxx-x,y)=set
map3(maxx-x,maxy-y)=set
map3(x,maxy-y)=set
EndIf
End Function
Function createlevel(lev)
mushrooms=0
Local msx,msy
SeedRnd lev

For x=0 To maxx
For y=0 To maxy
map(x,y)=3
map2(x,y)=0
map3(x,y)=0
map4(x,y)=0
If x=0 Or x=maxx Or y=0 Or y=maxy Then map(x,y)=6
Next
Next




x=Rand(2,maxx-2)
y=Rand(2,maxy-2)
cd=0
c=0:d=0
For nv=3 To 0 Step -1
If nv=3 Then ng=7:amt=1000
If nv=2 Then ng=1:amt=500
If nv=1 Then ng=5:amt=500
If nv=0 Then ng=3:amt=Sqr(maxx*maxy)*Rand(3,5+(lev/3)):msx=x:msy=y
difr=((maxx*maxy)/200)+1
;RuntimeError difr
cnt=0
For g=1 To amt
;If Rand(1,10)=10 Then x=Rand(1,maxx-1):y=Rand(1,maxy-1):Cd=1
If Rand(1,difr)=Int(difr/2) And cnt>4 Then cd=1
If nv<>0 And nv<>3 Then
	If map(x,y)=1 Or map(x,y)=5 Then setmap(x,y,3):x=x-c:y=y-d:setmap(x,y,3):cd=1:x=x+c:y=y+d

	x=x+c:y=y+d
	If x>0 And x<maxx And y>0 And y<maxy Then If map(x,y)=1 Or map(x,y)=5 Then cd=1
	x=x+c:y=y+d
	;If x>0 And x<maxx And y>0 And y<maxy Then If map(x,y)=1 Or map(x,y)=5 Then cd=1
	x=x-c:y=y-d
	x=x-c:y=y-d
EndIf
cnt=cnt+1
If cd=1 Then
cnt=0
Repeat:c=Rand(-1,1):d=Rand(-1,1):
If (c=0 And d=0) Or (c<>0 And d<>0) Then c=0:d=0
xx=x+c
yy=y+d
If xx<1 Then xx=1 Else If xx>maxx Then xx=maxx
If yy<1 Then yy=1 Else If yy>maxy Then yy=maxy
mp=map(xx,yy)
;If KeyDown(1) Then End
Until (c<>0 Or d<>0)
cd=0

EndIf
If x<1 Then x=maxx-1
If y<1 Then y=maxy-1
If x>maxx-1 Then x=1
If y>maxy-1 Then y=1
setmap(x,y,ng)
If ng=3 Then setmap(x,y,ng,2)
If ng<>3 Then setmap(x,y,0,2)
;map(x,y)=ng
;map(x,maxy-y)=ng
;map(maxx-x,y)=ng
;map(maxx-x,maxy-y)=ng
x=x+c
y=y+d
;If ng=4 Then ng=3
Next
Next


For n=1 To maxplayers
Repeat:x=Rand(1,maxx)
y=Rand(1,maxy)
Until map2(x,y)<>0
setmap(x,y,4,3)
Next
;setmap(x,y,0,2)
;map(x,y)=4
;map(x,maxy-y)=4
;map(maxx-x,y)=4
;map(maxx-x,maxy-y)=4
;alive(x,y)=mushlife
;alive(maxx-x,y)=mushlife
;alive(x,maxy-y)=mushlife
;alive(maxx-x,maxy-y)=mushlife






Cls
For x=0 To maxx
For y=0 To maxy
If map2(x,y)<>0 Then map(x,y)=2
;DrawBlock img(map(x,y)),x*szx,y*szy
drawtile x,y
;If map2(x,y)=0 Then DrawBlock img(1),x*szx,y*szy
Next
Next





pl=1
For n=1 To maxplayers/4
n2=4:If n+n2>maxplayers Then n2=(maxplayers Mod 4)+1
Repeat:x=Rand(1,(maxx/2)-2)
y=Rand(1,(maxy/2)-2)
ok=1
If mapok(x,y)=0 Then ok=0
For o=1 To maxplayers
If x=px(o) And y=py(o) Then ok=0
Next
If map2(x,y)=0 Then ok=0
Until  ok=1
sx(pl)=x
sy(pl)=y
pl=pl+1
If pl<maxplayers+1 Then
sx(pl)=maxx-x
sy(pl)=y
pl=pl+1
EndIf
If pl<maxplayers+1 Then
sx(pl)=x
sy(pl)=maxy-y
pl=pl+1
EndIf
If pl<maxplayers+1 Then
sx(pl)=maxx-x
sy(pl)=maxy-y
pl=pl+1
EndIf
Next


For n=1 To maxplayers
px(n)=0
py(n)=0
pdx(n)=0
pdy(n)=0
If lives(n)<>0 Then
px(n)=sx(n)
py(n)=sy(n)
pdx(n)=sx(n)*szx
pdy(n)=sy(n)*szy
EndIf
Next


CT=lev*2
If CT>1024 Then CT=1024
For n=1 To CT

MS=MilliSecs()
Repeat:x=Rand(1,maxx)
y=Rand(1,maxy)
;ok=1
ok=1
If mapok(x,y-1)<>0 Then ok=0
;If mapok(x,y-1)=0 And  mapok(x,y)=1 Then ok=1
If map4(x,y-1)<>0 Then ok=0
If mapok(x-1,y)=0 And mapok(x+1,y)=0 Then ok=0
;If mapok(x-2,y)=0 Then ok=0
;If mapok(x+2,y)=0 Then ok=0
If mapok(x,y)=0 Then ok=0
If mapok(x,y+1)=0 Then ok=0

If map4(x-1,y)<>0 Or map4(x+1,y)<>0 Then ok=0
For o=1 To maxplayers
If x=px(o) And y=py(o) Then ok=0
Next
;If map2(x,y)=0 Then ok=0
Until  ok=1 Or KeyDown(1) Or MilliSecs()-MS>200
If MilliSecs()-MS<200 Then map4(x,y)=8:map4(maxx-x,y)=8 Else N=10250

Next



End Function
Function spread()
Local x,y,x2,y2
mushrooms=0
For x=1 To maxx-1
	For y=1 To maxy-1
		If map4(x,y)<>0 And map3(x,y)<>0 Then map3(x,y)=0
		If map3(x,y)<>0 Then mushrooms=mushrooms+1
	Next
Next

ct=0
For nn=1 To maxplayers
If lives(nn)>0 Then ct=ct+1
Next

For nn=1 To (maxx/16)*(ct*.7)
	x=Rand(1,maxx-1)
	y=Rand(1,maxy-1)
		If map3(x,y)=4 Then

			Repeat:c=Rand(-1,1):d=Rand(-1,1):
				If (c=0 And d=0) Or (c<>0 And d<>0) Then c=0:d=0
			Until (c<>0 Or d<>0)


			x2=x+c
			y2=y+d
			If map3(x2,y2)=4 Then alive(x2,y2)=mushlife
			If map(x2,y2)=3 Or map(x2,y2)=2
 				If Rand(100)>0 Then
						ok=1
						
						For n2=1 To maxplayers:If px(n2)=x2 And py(n2)=y2 Then ok=0
						If px(n2)+pc(n2)=x2 And py(n2)+pd(n2)=y2 Then ok=0
						If px(n2)-pc(n2)=x2 And py(n2)-pd(n2)=y2 Then ok=0
						Next
						If ok=1 Then
						map3(x2,y2)=4:Lastspread=MilliSecs():alive(x2,y2)=mushlife
						drawtile(x2,y2)
						EndIf
				EndIf
			EndIf
		EndIf
Next

;EndIf
End Function

Function drawtileo(x,y)
If updbuffer=1 Then

If map(x,y)<>0 Then DrawBlock img(map(x,y)),(x*(szx))-camx,((y*(szy))-camy)+basey
If map3(x,y)<>0 Then DrawImage img(map3(x,y)),(x*szx)-camx,((y*szy)-camy)+basey
If map4(x,y)<>0 Then DrawImage img(8),(x*szx)-camx,((y*szy)-camy)+basey
EndIf

End Function
Function drawtile(x,y)
Local mx=(x*szx)-camx
Local my=(y*szy)-camy
If mx>-szx And mx<scx And my>-szy And my<scy And updbuffer=1 Then
;Local o,n=WaitTimer(audiotimer):If n>0 Then For o=1 To n::Next

If x>-1 And x<maxx+1 And y>-1 And y<maxy+1 Then
If map(x,y)<>0 Then DrawBlock img(map(x,y)),(x*(szx))-camx,((y*(szy))-camy)+basey
If map3(x,y)<>0 Then DrawImage img(map3(x,y)),(x*szx)-camx,((y*szy)-camy)+basey
If map4(x,y)<>0 Then DrawImage img(8),(x*szx)-camx,((y*szy)-camy)+basey

EndIf
EndIf
End Function

Function dfloor()
If DEAD(1)=0 Then
camx=pdx(1)-(scx/2)
camy=pdy(1)-(scy/2)
If camx<0 Then camx=0
If camy<0 Then camy=0
If camx>((maxx+1)*szx)-scx Then camx=((maxx+1)*szx)-scx
If camy>((maxy+1)*szy)-(scy-basey) Then camy=((maxy+1)*szy)-(scy-basey)
cammvx=-(camx-ocamx)
cammvy=-(camy-ocamy)
ocamx=camx
ocamy=camy
EndIf
End Function

Function mapok(x,y) ;checks for presence of anything including players
Local n
If ignoreboulders=0 Then
For n=1 To maxplayers
If px(n)=x And py(n)=y Then Return 0
If Int(pdx(n)/szx)=x And Int(pdy(n)/szy)=y Then Return 0
Next
EndIf
If ignoreboulders=0 Then If map4(x,y)<>0 Then Return 0
If x<1 Or x>maxx-1 Or y<1 Or y>maxy-1 Then Return 0
If map(x,y)=3 Or map(x,y)=2 Then Return 1
Return 0
End Function

Function mapok2(x,y) ;checks for presence of anything excludin players and boulders
Local n
;If map4(x,y)<>0 Then Return 0
If x<1 Or x>maxx-1 Or y<1 Or y>maxy-1 Then Return 0
If map(x,y)=3 Or map(x,y)=2 Then Return 1
Return 0
End Function


;Hat colours for players

Data 4 ;Number of colours listed
Data 100,200,0
Data 255,0,0
Data 0,0,255
Data 100,0,255

Data 100,255,0
Data 50,50,150
Data 255,0,255
Data 100,255,50

Function findpath(sx,sy,ex,ey)
For x=1 To maxx
For y=1 To maxy
;If map4(x,y)<>0 Then map4(x,y)=0:drawtile x,y
Next
Next

n.path=New path
n\x=sx
n\y=sy
cntr=0
done=0
;Repeat
For n.path=Each path
If done=0 Then
done=checkpath(n\x,n\y,ex,ey,cntr):cntr=cntr+1:Delete n
EndIf
Next
;Until done
Return done
End Function
Function checkpath(x,y,ex,ey,cntr)
Local c,d,n.path
For c=-1 To 1
For d=-1 To 1
If c=0 Or d=0 And Abs(c)<>Abs(d) Then
If (map(x+c,y+d)=2 Or map(x+c,y+d)=3) And map4(x+c,y+d)=0 Then
map4(x+c,y+d)=cntr
drawtile(x+c,y+d)
n.path=New path
n\x=x+c
n\y=y+d
;If KeyDown(1) Then End
EndIf
EndIf
Next
Next
If x=ex And y=ey Then Return 1
End Function
Function positionplayer(n,x,y)
px(n)=x:py(n)=y:pdx(n)=x*szx:pdy(n)=y*szy
End Function
Function die(player)
dead(player)=1
diecounter(player)=60
lives(player)=lives(player)-1
;If playertype(player)<>0 Then
diefx;EndIf
End Function
Function diefx()
FLUSHCHANNEL 3
For nv#=1 To 0 Step -.4
sound 3,nv,220,15
;sound 3,.8,100,10
Next
End Function
Function winfx()
FLUSHCHANNEL 3
FLUSHCHANNEL 4
FLUSHCHANNEL 5
For nv#=1 To 0 Step -.4
sound 3,nv,220,15,3
sound 4,nv,232,15,3
sound 5,nv,240,15,3
;sound 3,.8,100,10
Next
End Function

Function okahead(x,y,c,d)
ok=-1000
x=x+c:y=y+d
cnt=0
For nn=1 To maxx
If x<0 Then x=0 Else If x>maxx Then x=maxx
If y<0 Then y=0 Else If y>maxy Then y=maxy

If map3(x,y)<>0 Then ok=1:dsn=cnt+9000:nn=maxx+1
If mapok(x,y)=0 And ok<>1 And nn<5 Then ok=2:dsn=cnt:nn=maxx+1

For c2=-1 To 1
For d2=-1 To 1
If (c2=0 Or d2=0) And (c2<>0 Or d2<>0) Then
If mapok(x+c2,y+d2) Then cnt=cnt+1
EndIf
Next
Next

cnt=cnt+1
x=x+c:y=y+d
Next
If ok=1 Then ok=dsn
If ok=2 Then ok=-dsn;dsn+1024

Return ok
End Function


Function distancehead(x,y,c,d)
ok=-1000
x=x+c:y=y+d
cnt=0
For nn=1 To maxx
If x<0 Then x=0 Else If x>maxx Then x=maxx
If y<0 Then y=0 Else If y>maxy Then y=maxy
If map3(x,y)<>0 Then dsn=cnt+1024:ok=1:nn=maxx+1
If mapok(x,y)<>0 Then dsn=cnt:ok=1:nn=maxx+1
cnt=cnt+1
x=x+c:y=y+d
Next
If ok=1 Then ok=dsn

Return ok
End Function


Function finddirection(x,y)
Local c,d,dst=0
odirx=dirx:odiry=diry
For bc=-1 To 1
For bd=-1 To 1
If (bc=0 Or bd=0) And (bd<>0 Or bc<>0) Then
distance=distancehead(x,y,bc,bd)
;If distance=-1000 Then distance=0
;If distance>0 Then distance=distance+1000
;If distance<0 Then distance=-distance
If distance>=dst Then dst=distance:dirx=bc*dst:diry=bd*dst
EndIf
Next
Next
If dirx=0 And diry=0 Then
dst=0
For bc=-1 To 1
For bd=-1 To 1
If (bc=0 Or bd=0) And (bd<>0 Or bc<>0) Then
distance=distancehead(x,y,bc,bd)
If distance>=dst Then dst=distance:dirx=bc*dst:diry=bd*dst
EndIf
Next
Next
If dirx=0 And diry=0 Then dirx=odirx:diry=odiry
EndIf
End Function

Function findbestdirection(x,y)
Local c[4],d[4]


If mapok(x,y+1) Then
finddirection(x,y+1)
c[1]=dirx:d[1]=diry
EndIf

If mapok(x,y-1) Then
finddirection(x,y-1)
c[2]=dirx:d[2]=diry
EndIf

If mapok(x+1,y) Then
finddirection(x+1,y)
c[3]=dirx:d[3]=diry
EndIf

If mapok(x-1,y) Then
finddirection(x-1,y)
c[4]=dirx:d[4]=diry
EndIf

bd=0
bc=0

For n=1 To 4
If Abs(c[n])>abc Or Abs(d[n])>abd Then bc=c[n]:bd=d[n]:abc=Abs(bc):abd=Abs(bd):seldn=n
Next
If seldn=1 Then bc=0:bd=1
If seldn=2 Then bc=0:bd=-1
If seldn=3 Then bc=1:bd=0
If seldn=4 Then bc=-1:bd=0

fs=WriteFile("temp.txt")
For n=1 To 4
If c[n]<>0 Or d[n]<>0 Then fns=1
WriteLine fs,c[n]+",  "+d[n]
Next
WriteLine fs,bc+", "+bd
CloseFile fs

;If fns=1 Then
;ExecFile "temp.txt"
;End
;EndIf

dirx=bc
diry=bd
Goto Exitbit


For bc=-1 To 1
	For bd=-1 To 1
		If (bc=0 Or bd=0) And (bd<>0 Or bc<>0) Then
			If mapok(x+bc,y+bd)<>0 Then






			EndIf
		EndIf
Next
Next
.Exitbit
End Function

Function setuptext()
Local n,bf
For n=32 To 128
charimg(n,0)=CreateImage(txsize,txsize)
bf=GraphicsBuffer()
SetBuffer ImageBuffer(charimg(N,0))

colour 128,128,128
colour 0,0,0
;colour 255,255,0
For c=-1 To 1
For d=-1 To 1
Text (txsize/2.0)-c,(txsize/2.0)-d,Chr$(n),1,1
Next:Next


colour 255,255,255
colour 255,255,0
Text (txsize/2.0),(txsize/2.0),Chr$(n),1,1
;Text (txsize/2.0)+1,(txsize/2.0),Chr$(n),1,1
;Text (txsize/2.0),(txsize/2.0)+1,Chr$(n),1,1
;Text (txsize/2.0)+1,(txsize/2.0)+1,Chr$(n),1,1

ScaleImage charimg(n,0),txscale,txscale
MidHandle charimg(n,0)
For n2=1 To Textcnt
charimg(n,n2)=CopyImage(charimg(n,0))
MidHandle charimg(n,0)
RotateImage charimg(n,n2),((n2-1)-Textcnt/2.0)*4
Next
Next
txsize=txsize*txscale
SetBuffer bf
End Function

Function Text2(x,y,Txt$,centre_x=0,centre_y=0)
Local n,a,t$,drawx,drawy
If centre_x=1 Then x=x-Len(txt$)*((txsize*txxdis)/2.0)
If centre_y=1 Then y=y-txsize/2.0

x=x+((txsize*txxdis)/2.0)
y=y+((txsize)/2.0)

For n=1 To Len(Txt$)
t$=Mid$(txt$,n,1)
a=Asc(t$)
If a>32 And a<128 Then
vl#=(Sin(MilliSecs()+(n*500))/2)+.5
vl=(vl*(Textcnt-1))+1
DrawImage charimg(a,vl),x+(n-1)*(txsize*txxdis),y
EndIf
Next
End Function
Function addboulder(x,y,player,timer)
Local n.boulder,do=1,X2,Y2
For X2=X-1 To X+1
For Y2=Y-1 To Y+1
If X2>-1 And X2<MAXX+1 And Y2>-1 And Y2<MAXY+1 Then
If MAP4(X2,Y2)<>0 And ((X2=X Or Y2=Y) And (X2<>X Or Y2<>Y)) Then MAP4(X2,Y2)=0
EndIf
Next
Next
If map4(x,y+1)=0 And mapok2(x,y+1)<>0 Then
For n=Each boulder
If n\x=x And n\y=y Then do=0
Next
If do=1 Then
n=New boulder
n\sx=x
n\sy=y
n\x=x:n\y=y
n\player=0
n\timer=timer
n\triggered=0
n\rocknumber=map4(x,y)
EndIf
EndIf
End Function

Function updateboulders()
Local n.boulder
Local oudb=updbuffer
updbuffer=1
For n=Each boulder
deld=0
ox=n\x
oy=n\y
If map4(n\x,n\y)=0 And n\reposition=0 Then Delete n:Goto nxtn
If mapok(n\x,n\y+1)=0 And n\triggered=0 Then Goto nxtn
If n\reposition=0 Then drawtile n\x,n\y
If n\timer>0 Then n\timer=n\timer-1:Goto Nxtn
If n\triggered=0 Then n\triggered=1:flushchannel 2:sound 2,1,180,20
If n\reposition=0 Then
n\timer=10
	If map4(n\x,n\y+1)=0 And mapok2(n\x,n\y+1) Then
		map4(n\x,n\y+1)=n\rocknumber:map4(n\x,n\y)=0:drawtile n\x,n\y:drawtile n\x,n\y+1
		n\y=n\y+1
	Else
	n\reposition=1
	map4(n\x,n\y)=0
	drawtile n\x,n\y
	n\timer=256
	EndIf
EndIf
If n\reposition=0 Then drawtile ox,oy
If n\reposition And n\timer=0 Then
If mapok(n\sx,n\sy) Then map4(n\sx,n\sy)=n\rocknumber:map4(n\x,n\y)=0:drawtile n\x,n\y:drawtile n\sx,n\sy:Delete n:deld=1
EndIf
.Nxtn
Next
updbuffer=oudb
End Function

Function collectsfx(channel)
flushchannel channel
sound channel,1,150,6
End Function

Function Music(n)
Restore music1
Repeat
Read chan
If chan<>-1 Then
If chan<>0 Then chan=chan+5
;If chan=6 Then chan=4

Read vol#,Pitch,Length#
If chan=6 Or chan=7 Or chan=8 Then Pitch=Pitch-48
Pitch=Pitch+48
If chan=9 Then Pitch=Pitch-48*2
;Pitch=Pitch-24
sound chan,vol#,Pitch+48,Length*3,chan
loopchannel chan,1
EndIf
Until chan=-1
;Repeat
;updatesound()
;Until KeyDown(1)
;End
Restore
End Function
Function setupmusic()
adsr 0,1,-.05,.1,-.1
adsr 9,1,-.05,.1,-.1
adsr 10,1,-.05,.2,-.1
adsr 1,1,0,1,-.1
adsr 2,.2,-.05,.5,-.02
Pitchadsr 1,0,-16,0,16
Pitchadsr 2,-4,-1,-1,-2
Pitchadsr 3,-7,-7,-7,-7
adsr 3,1,-.01,.1,-.1
music(1)

soundshape 5,1
soundshape 6,1
soundshape 7,1

soundshape 3,2
soundshape 2,2
soundshape 1,2
;loopchannel 2 0

For n=5 To 8
adsr n,.2,-.05,.5,-1
Next
End Function
Function Constr$(txt$,ln)
txt$=String$(" ",ln-Len(txt$))+txt$
Return Replace(txt$," ","0")
End Function



.music1


Data 1,.5,52,10
Data 2,.5,68,10
Data 3,.5,80,10
Data 1,.5,52,10
Data 2,.5,68,10
Data 3,.5,80,10

Data 1,.5,52,10
Data 2,.5,72,10
Data 3,.5,80,10
Data 1,.5,52,10
Data 2,.5,72,10
Data 3,.5,80,10

;Data 4,1,4,5




Data 5,1,100,10
Data 5,1,100,10
Data 5,1,92,5
Data 5,1,88,5
Data 5,1,80,10
Data 5,1,72,10
Data 5,1,80,10
Data 5,1,88,10
Data 5,1,80,10
;
Data 5,1,100,10
Data 5,1,100,10
Data 5,1,92,5
Data 5,1,88,5
Data 5,1,80,5
Data 5,1,80,45

Data 4,.5,52,5
;Data 4,.5,68,5
Data 4,.5,80,5

Data 0,1,5,5
Data 0,0,50,15
Data 0,.5,150,10
Data 0,0,0,10
Data 0,1,5,5
Data 0,0,0,5
Data 0,1,5,5
Data 0,.5,20,5
Data 0,.5,150,10
Data 0,0,50,10

Data 0,1,5,5
Data 0,0,50,15
Data 0,.5,150,10
Data 0,0,0,10
Data 0,1,5,5
Data 0,0,0,5
Data 0,1,5,5
Data 0,.5,20,5
Data 0,.5,150,5
Data 0,0,150,5
Data 0,.5,150,5
Data 0,.5,150,5



Data -1



;
;
; Cygnus Software's "chip" simulator...
;
; Programmed Oct-Nov 2004
;
; Feel free to use this code anywhere,
; give credits where credits are due? I would do the same.
; http://danjeruz.servegame.com
; Have fun!!!!




Function initsoundsystem();channels,envelopes)
Local samples,o,nm,snd
Dim wavdata(1,4096)

samples=1
ss_channelcount=1

For o=0 To 10
wavdata(0,o)=-65525/2.1
Next
wavdata(0,1)=-65525/3
wavdata(0,2)=65525/3


samplerate=1
bpsample=16

wavdata(0,1)=-65525/3
wavdata(0,2)=-65525/3
wavdata(0,3)=65525/3
wavdata(0,4)=65525/3


For snd=1 To ss_sndtype
If SND=3 Then
For o=0 To 10
WAVDATA(0,O)=Sin(O*57)*65525/3.0
Next
EndIf
If SND=4 Then
For o=0 To SND*2
WAVDATA(0,O)=Sin((Float(O)/Float(SND*2))*360)*65525/3.0
Next
EndIf



samples=snd*4
Writewav(samples,"S"+Str$(snd)+".wav")
Next

For o=0 To 4096
wavdata(0,o)=Rnd(-65525/2,65525/2)
wavdata(1,o)=Rnd(-65525/2,65525/2)
Next
samples=4096
Writewav(samples,"S0.wav")

For o=0 To ss_sndtype
ss_soundbank(o)=LoadSound("s"+Str$(o)+".wav")
o2=o:If o=0 Then o2=5
ss_samplecntbank(o)=o2
DeleteFile "s"+Str$(o)+".wav"
LoopSound ss_soundbank(o)
Next

For o=0 To ss_maxchans
O2=ss_maxchans-O
nm=(((o2-1)/3) Mod ss_sndtype)+1
If o=0 Then nm=0
ss_sounds(o)=ss_soundbank(nm)
ss_samplecnt(o)=ss_samplecntbank(nm)
Next

For o=0 To ss_maxenvs
ss_defattack(o)=.1
ss_defdecay(o)=-.5
ss_defsustain(o)=0
ss_defrelease(o)=-.1
Next

For o=0 To ss_maxenvs
Next
End Function
Function sound(channel,vol#,Pitch,Length,envelope=-1)
;channel=ss_maxchans-channel
If envelope=-1 Then envelope=channel

Local count
;dim ss_bufferchan(ss_maxchans,ss_buffersize),buffervol(ss_maxchans,ss_buffersize)
;dim ss_bufferpitch(ss_maxchans,ss_buffersize)
;dim ss_bufferlength(ss_maxchans,ss_buffersize)
;dim ss_buffercount(ss_maxchans)
count=ss_buffercount(channel)
If count<0 Then count=0
ss_buffercount(channel)=count
ss_buffervol(channel,count)=vol
ss_bufferpitch(channel,count)=Pitch
ss_bufferlength(channel,count)=Length
ss_bufferenv(channel,count)=envelope

ss_buffercount(channel)=ss_buffercount(channel)+1
If ss_buffercount(channel)>ss_buffersize-1 Then Repeat:updatesound():Until KeyDown(1) Or ss_buffercount(channel)<ss_buffersize-1 Or ss_buffercount(channel)=0;:If buffercount(channel)<0 Then buffercount(channel)=0
End Function
Function updatesound()
Local n,ct,n2
If MilliSecs()-audiotimer>updateperiod Then
audiotimer=MilliSecs()
ct=Float(MilliSecs()-audiotimer)/Float(updateperiod)
If ct>2 Then ct=0
For n=0 To ct
For n2=1 To 1
doupdatesound
updatesoundtimer()
Next
Next

EndIf

End Function
Function doupdatesound()

;soundtimer()=soundtimer()+.01
Local o,count,o2,updvols=0,nx,env
Local divm#=1


For o=0 To ss_maxchans
ss_lfocounter(o)=ss_lfocounter(o)+1

env=ss_envelope(o)
;env=o
updvols=1

pastend=0
attacked=ss_attackdone(o)
If soundtimer()-ss_started(o)>=ss_slength(o)*1 Then pastend=1

count=ss_buffercount(o)

If ss_schan(o)<>0 Then
;ChannelVolume ss_schan(o),ss_volume(o)
If updvols=1 Then


;Dim ss_lfo#(ss_lfocount,ss_maxchans)
;Dim ss_lfofreq#(ss_lfocount,ss_maxchans)
;Dim ss_lfocounter(ss_maxchans)

;vlfo#=lfovalue(ss_lfocounter(o))*ss_lfofreq(2,o)*ss_lfo(2,o)
vlfo#=volumemultiplier(o)
plfo#=Pitchlfo(o)
;plfo#=lfovalue(ss_lfocounter(o))*ss_lfofreq(1,o)*ss_lfo(1,o)
;vlfo=1
;plfo=1
		;For n=1 To ss_lfocount
			;If ss_lfo(n,o)<ss_lfosustain(n,env) Then ss_lfo(n,o)=ss_lfo(n,o)+ss_lfoattack(n,env) Else ss_lfo(n,o)=ss_lfosustain(n,env)
		;Next
		
If pastend=0 And attacked=0 Then			;Attack section
	If ss_volume(o)<1 Then ss_volume(o)=ss_volume(o)+ss_attack(o)
	If ss_volume(o)>=1 Then ss_volume(o)=1

		For n=1 To ss_lfocount
			If ss_lfo(n,o)<1 Then ss_lfo(n,o)=ss_lfo(n,o)+ss_lfoattack(n,env) Else ss_lfo(n,o)=1;ss_lfosustain(n,env)
		Next

	
	av#=ss_volume#(o):ChannelVolume ss_schan(o),(av+vlfo)*ss_actualvol(o)
	ss_currentpitch(o)=ss_currentpitch(o)+ss_attackchange(env)
	nf=freq(ss_currentpitch(O),o)
	If nf>10 Then ChannelPitch ss_schan(o),nf
EndIf

If ss_volume(o)>=1 Then attacked=1:ss_attackdone(o)=1

If pastend=0 And attacked=1 Then			;Decay section
	ss_volume(o)=ss_volume(o)+ss_decay(o):If ss_volume(o)<ss_sustain(o) Then ss_volume(o)=ss_sustain(o):ss_decaydone(o)=1


		For n=1 To ss_lfocount
			ss_lfo(n,o)=ss_lfo(n,o)+ss_lfodecay(n,env)
			If ss_lfo(n,o)<ss_lfosustain(n,env) Then ss_lfo(n,o)=ss_lfosustain(n,env)
		Next

		av#=ss_volume#(o):ChannelVolume ss_schan(o),(av+vlfo)*ss_actualvol(o)
		ss_currentpitch(o)=ss_currentpitch(o)+ss_decaychange(env)
		nf=freq(ss_currentpitch(O),o)
	If nf>10 Then ChannelPitch ss_schan(o),nf
EndIf
If ss_decaydone(o)=1 And pastend=0 Then	;Sustain section
		ss_currentpitch(o)=ss_currentpitch(o)+ss_sustainchange(env)
		av#=ss_volume#(o):ChannelVolume ss_schan(o),(av+vlfo)*ss_actualvol(o)
		nf=freq(ss_currentpitch(O),o)
	If nf>10 Then ChannelPitch ss_schan(o),nf
EndIf

If pastend=1 Then
	ss_volume(o)=ss_volume(o)+ss_release(o):If ss_volume(o)<0 Then ss_volume(o)=0

		For n=1 To ss_lfocount
			ss_lfo(n,o)=ss_lfo(n,o)+ss_lforelease(n,env)
			If ss_lfo(n,o)<0 Then ss_lfo(n,o)=0
		Next

	av#=ss_volume#(o):ChannelVolume ss_schan(o),(av+vlfo)*ss_actualvol(o)
	ss_currentpitch(o)=ss_currentpitch(o)+ss_releasechange(env)
	nf=freq(ss_currentpitch(O),o)
	If nf>10 Then ChannelPitch ss_schan(o),nf
EndIf

EndIf




EndIf
If pastend=1 And forcesoundupdate=0 Then
count=ss_buffercount(o)
If count>0 And ss_buffercount(o)<ss_buffersize+1 Then
;For nx=1 To 10
;volume(o)=volume(o)*.9
;ChannelVolume schan(o),volume(o)
;Delay 1
;Next
	If ss_channelloop(o) Then
	
		addsound(o,ss_buffervol(o,ss_bufferpointer(o)),ss_bufferpitch(o,ss_bufferpointer(o)),ss_bufferlength(o,ss_bufferpointer(o)),ss_bufferenv(o,ss_bufferpointer(o))):ss_bufferpointer(o)=(ss_bufferpointer(o)+1) Mod count
		;For o2=1 To count:buffervol(o,o2-1)=buffervol(o,o2):bufferpitch(o,o2-1)=bufferpitch(o,o2):bufferlength(o,o2-1)=bufferlength(o,o2):Next:buffercount(o)=buffercount(o)-1
	EndIf
	If ss_channelloop(o)=0 Then
		addsound(o,ss_buffervol(o,ss_bufferpointer(o)),ss_bufferpitch(o,ss_bufferpointer(o)),ss_bufferlength(o,ss_bufferpointer(o)),ss_bufferenv(o,ss_bufferpointer(o)))
		For o2=1 To count
		ss_buffervol(o,o2-1)=ss_buffervol(o,o2)
		ss_bufferpitch(o,o2-1)=ss_bufferpitch(o,o2)
		ss_bufferlength(o,o2-1)=ss_bufferlength(o,o2)
		ss_bufferenv(o,o2-1)=ss_bufferenv(o,o2)

		Next:ss_buffercount(o)=ss_buffercount(o)-1:If ss_buffercount(o)<0 Then ss_buffercount(o)=0
	EndIf
		
EndIf

EndIf
Next
End Function

Function freq(Pitch,chan)
Local p2,f
If Pitch<0 Then Pitch=Pitch+512
If Pitch>512 Then Pitch=Pitch-512
p2=(Pitch+200)+(Pitchlfo(chan));+Sin(ss_lfocounter(chan))
	f = 440 * 2^(((P2/4.0) - 58)/12)
	If f<10 Then f=10 Else If f>48000 Then f=48000
Return f*ss_samplecnt(chan)
End Function
Function lfovalue#(cnt)
;Return (Cos#(cnt)+1)/2.0
Return (Cos#(cnt))
End Function

Function volumemultiplier#(channel)
;env=ss_envelope(channel)
;If env<0 Or env>ss_maxenvs Then RuntimeError env
Return lfovalue(ss_lfocounter(channel)*ss_lfofreq(2,ss_envelope(channel)))*ss_lfo(2,channel)
;Return lfovalue(ss_lfocounter(channel))
End Function

Function Pitchlfo#(channel)
Return lfovalue(ss_lfocounter(channel)*ss_lfofreq(1,ss_envelope(channel)))*ss_lfo(1,channel)
End Function

Function addSound(channel,vol#,Pitch,Length#,envelope=-1)
Local p2#,o=ss_maxchans-channel,n
If envelope=-1 Then RuntimeError "sound not buffered correctly!"
ss_chancheck(channel)=0
;Goto Endbit
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
If ss_sounds(channel)<>0 Then
ss_lfocounter(channel)=0
;For n=0 To ss_lfocount
;Next
p2=freq(pitch,channel)

If p2>100 Then
SoundVolume ss_sounds(channel),0
If ChannelPlaying(ss_schan(channel))=0 Then ss_schan(channel)=0 Else StopChannel ss_schan(channel):ss_schan(channel)=0
If ss_schan(channel)=0 Then If ss_sounds(channel)<>0 Then ss_schan(channel)=PlaySound(ss_sounds(channel))

ChannelPitch ss_schan(channel),P2;+((P2+(P2*2.0))/12.0)*4.0
ChannelVolume ss_schan(channel),ss_defattack(envelope)*vol*volumemultiplier(channel);(vol)
SoundVolume ss_sounds(channel),1
For n=1 To ss_lfocount
	ss_lfo(n,channel)=ss_lfoattack(n,channel)
Next
ss_currentpitch(channel)=Pitch
ss_volume(channel)=ss_defattack(envelope);vol
ss_actualvol(channel)=vol
ss_sLength(channel)=Length
ss_started(channel)=soundtimer()
ss_attack(channel)=(ss_defattack(envelope)/15)*updateperiod#
ss_release(channel)=(ss_defrelease(envelope)/15)*updateperiod#;-.2
ss_sustain(channel)=ss_defsustain(envelope);.2
ss_decay(channel)=(ss_defdecay(envelope)/15)*updateperiod#;-.8
ss_attackdone(channel)=0
ss_decaydone(channel)=0
ss_chancheck(channel)=soundtimer()
ss_envelope(channel)=envelope
EndIf
EndIf
forcesoundupdate=1
updatesound()
forcesoundupdate=0
.endbit
End Function

Function updatesoundtimer()
;Return MilliSecs()
ss_stimer#=ss_stimer#+1
Return ss_stimer
End Function
Function soundtimer()
Return ss_stimer
End Function
Function loopchannel(channel,loop=1)
ss_channelloop(channel)=loop
End Function

Function LfoADSR(envelope,Lfo,cattack#,cdecay#,csustain#,crelease#)
;ss_lfo#(lfo,ss_maxchans)
;ss_lfo(lfo,
ss_lfoattack(lfo,envelope)=cattack
ss_lfodecay(lfo,envelope)=cdecay
ss_lfosustain(lfo,envelope)=csustain
ss_lforelease(lfo,envelope)=crelease

;Dim ss_lfoattack#(ss_lfocount,ss_maxenvs)
;Dim ss_lfodecay#(ss_lfocount,ss_maxenvs)
;Dim ss_lfosustain#(ss_lfocount,ss_maxenvs)
;Dim ss_lforelease#(ss_lfocount,ss_maxenvs)
;Dim ss_lfo#(ss_lfocount,ss_maxenvs)
;Dim ss_lfofreq#(ss_lfocount,ss_maxenvs)
End Function
Function Lfo(envelope,lfo,frequency)
ss_lfofreq#(lfo,envelope)=frequency
End Function

Function ADSR(envelope,cattack#,cdecay#,csustain#,crelease#)
ss_defattack(envelope)=cattack
ss_defdecay(envelope)=cdecay
ss_defsustain(envelope)=csustain
ss_defrelease(envelope)=crelease
End Function
Function PitchADSR(envelope,cattack#,cdecay#,csustain#,crelease#)
ss_attackchange(envelope)=cattack
ss_decaychange(envelope)=cdecay
ss_sustainchange(envelope)=csustain
ss_releasechange(envelope)=crelease
End Function
Function Soundshape(channel,sound)
If sound>-1 And sound<ss_sndtype+1 Then
ss_sounds(channel)=ss_soundbank(sound)
ss_samplecnt(channel)=ss_samplecntbank(sound)

EndIf
End Function
Function flushchannel(channel)
ss_bufferpointer(channel)=0
ss_buffercount(channel)=0
If ss_schan(channel)<>0 Then If ChannelPlaying(ss_schan(channel)) Then StopChannel ss_schan(channel):ss_schan(channel)=0
ss_started(channel)=-ss_slength(channel)
End Function
Function Stopsound()
Local n
For n=0 To ss_maxchans
flushchannel n
Next
End Function

Function Writewav(wavdatalen,filename$,ss_channelcount=1,samplerate=44100)
samples=wavdatalen
;Function Writewav(nosamples,filename$)
;wavdatalen=(ss_channelcount*nosamples*(bitspersample/8))/4
bitspersample=ss_bpsample
fs=WriteFile(filename$)
If fs=0 Then Return 0
WriteBinString fs,"RIFF"

wavlen=36+((wavdatalen*2)*ss_channelcount)
WriteInt fs,wavlen
Writebinstring fs,"WAVE"
Writebinstring fs,"fmt "
WriteInt fs,16

WriteShort fs,1

WriteShort fs,ss_channelcount
WriteInt fs,samplerate
bitspersample=16
byterate=SampleRate * ss_channelcount * bitspersample/8

WriteInt fs,byterate
WriteShort fs,ss_channelcount * bitspersample/8
WriteShort fs,bitspersample
Writebinstring fs,"data"
WriteInt fs,samples*ss_channelcount * BitsPerSample/8
;WriteInt fs,wavdatalen*4
For p=1 To wavdatalen
If ss_bpsample=8 Then Midr=128
If ss_bpsample=16 Then Midr=Midbit/2.0
If ss_bpsample=32 Then Midr=(Midbit*255)/2.0

For chan=0 To ss_channelcount-1
rval=wavdata(chan,p)
rval=rval-Midr
If rval<0 Then rval=rval+(Midr*2)
If rval<0 Then rval=0 Else If rval>(Midr*2)*2 Then rval=(Midr*2)
;If ss_bpsample>16 Then rval=rval/2.0;WriteShort fs,rval
WriteShort fs,wavdata(chan,p);rval

Next
Next
If ss_channelcount=1 Then ster$="Mono" Else ster$="Stereo"
ss_debugprint"Created "+filename$+" as "+ss_bpsample+" bit, "+samplerate+"hz, "+ster$+" wave file."

CloseFile fs
End Function

Function WriteBinString(filehandle,dat$)
For p=1 To Len(dat$)
WriteByte filehandle,Asc(Mid$(dat$,p,1))
Next
End Function

Function ss_debugprint(St$)
Print st$
End Function
