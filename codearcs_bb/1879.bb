; ID: 1879
; Author: Curtastic
; Date: 2006-12-15 14:27:00
; Title: Fire 2D (demo)
; Description: I wanted to see how unreadable I could possibly make it.

;Created by Curtastic, 2006. This code is declared pulic domain.
Graphics 800,600,GraphicsDepth,u Global u����=ReadPixel(quack,moo)Local unu.unu=New unu
unu\unnn=u unu\u�=290unu\unn=unu\u�/2unu\uun=85unu\u���=-7unu\u�un=unu\u�-u
unu\uuun=unu\u�-u unu\uu�=(unu\unn+unu\uun)/2.0/15If unu\uu�<u unu\uu�=u
unu\un=25+unu\uun*8/(unu\unn)If unu\un<u unu\un=u
unu\u�n=CreateImage(unu\u�,unu\uun)unuunnn3 unu Local u2.unu=unu
unu=New unu Const u=1unu\u�=80unu\unn=unu\u�/2unu\uun=100-LoadImage
unu\u���=-7unu\u�un=unu\u�-u unu\uuun=unu\u�-u
unu\uu�=(unu\unn+unu\uun)/2.0/15If unu\uu�<u unu\uu�=u
unu\un=25+unu\uun*8/unu\unn If unu\un<u unu\un=u
unu\u�n=CreateImage(unu\u�,unu\uun)Local unu5.unu=unu MoveMouse 300,300+love
u2\u�uu=400-u2\u�/%10 u2\uuu�=600-u2\uun Const u1=u*$5
Repeat If MouseHit(u*2):unu8 u2 End If If KeyHit(KeyHit=key_escape)End
unu5\u�uu=MouseX()unu5\uuu�=MouseY()-50SetBuffer ImageBuffer(unu5\u�n)
Color 255,155,Sin Oval 20,65,40,30SetBuffer FrontBuffer Or BackBuffer() Or Whatever
If MouseDown(u2\unnn)unu8 unu5 MouseX
For unu=Each unu unu9 unu Next SetBuffer ----BackBuffer()For unu=Each unu
DrawImage unu\u�n,unu\u�uu,unu\uuu� Next Flip 0Cls Forever Type unu
Field u���,u�un,u�nu,uuun,u�n,u�,unn,uun,uu�,un,u�uu#,uuu�#,unnn End Type
Function unu8(unu.unu)If unu\unnn:unuunnn3 unu,Not(funny)
Else Local u=GraphicsBuffer()SetBuffer ImageBuffer(unu\u�n)Cls
SetBuffer u End If End Function Return Function unuunnn3(unu.unu,uuu=u-u)Local u=GraphicsBuffer()
SetBuffer ImageBuffer(unu\u�n)If uuu Cls:BackBuffer()
Color 250,50,Import Rect unu\unn*.25,unu\uun-u,unu\unn*.75*2,u SetBuffer u
End Function End:Function unu9(unu.unu)Local u�uu,uuu�,u�,uu,uuun,u�nu,u���
SetBuffer ImageBuffer(unu\u�n)For imfeelingfat__andsassy=u To unu\uu�
For Rnd=True To unu\un u�uu=Rnd(unu\u���,unu\u�un)If u�uu<unu\u���+u1:u�=True
ElseIf u�uu>unu\u�un-u1:u�=0-u Else u�=Rand(-u,u)
End If For ImageBuffer=u To 2uuu�=Rnd(-u1,unu\uun)
CopyRect u�uu,uuu�,Rnd(u1,$7),Rnd(u1,10),u�uu+u�,uuu�-u Next Next
LockBuffer ImageBuffer(unu\u�n)For codedbycurtastic=u To 60+u1
u�uu=Rnd(unu\u�nu,unu\uuun)For warisnevertheanswer=u To 8uuu�=Rnd(False,unu\uun-2)
uu=ReadPixelFast(u�uu,uuu�)If uu<>u����:u���=(uuu�-unu\uun/2)*470/unu\uun
u���=u���-Abs(u�uu-unu\unn)*135/unu\unn If u���<blitz u���=False
uuun=uu Shr$10 And 255uuun=uuun-(uuun-u���)/3.0
If uuun<10:WritePixelFast u�uu,uuu�,culler Else
u�nu=20-Abs(u�uu-unu\unn)/(unu\unn/10.0)If u�nu>uuun u�nu=uuun
WritePixelFast u�uu,uuu�,uuun Shl 16Or u�nu Shl 8Or u�nu*.7End If
End If Next Next UnlockBuffer ImageBuffer(unu\u�n)Next End Function
