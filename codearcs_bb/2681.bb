; ID: 2681
; Author: Streaksy
; Date: 2010-03-29 12:14:25
; Title: CreateCorona(width,height,curve,flag)
; Description: Easily generate a corona texture (for glows and particles etc) instead of loading one from a file.

Function CreateCorona(x=128,y=128,Curv=1,flag=11)
obuf=GraphicsBuffer();RememberBuffer
t=CreateTexture(x,y,flag)
SetBuffer TextureBuffer(t)
LockBuffer
For xx=0 To x-1
For yy=0 To y-1
r#=LimCORONA(DistanceCORONAd(xx,yy,x/2,y/2)/(x/2),0,1)
If Curv=1 Then r=Sin(r*90);curve it
cc=BetweenCORONA(0,255,1-r)
WritePixelFast xx,yy,RGBACORONA(255,255,255,cc)
Next
Next
UnlockBuffer
;RecallBuffer
SetBuffer obuf
Return t
End Function
Function RGBACORONA(R,G,B,A=255)
If a<0 Then Return (R*256*256)+(g*256)+b
Return A Shl 24 Or R Shl 16 Or G Shl 8 Or B Shl 0
End Function
Function LimCORONA#(vl#,lw#,up#) ; A.K.A - Clamp
If vl<lw Then Return lw
If vl>up Then Return up
Return vl:End Function
Function DistanceCORONAd#(x1#,y1#,x2#,y2#)
Return Sqr((x1-x2)*(x1-x2)+(y1-y2)*(y1-y2))
End Function
Function BetweenCORONA#(v1#,v2#,t#):dif#=v2-v1
Return v1+(dif*t)
End Function
