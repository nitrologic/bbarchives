; ID: 2670
; Author: Streaksy
; Date: 2010-03-21 06:39:13
; Title: Envelope - Read a Point From Curve-Interpolated Envelope (Easily)
; Description: Functions to make an envelope wave and read any point within it (0-1)

Global Envs,MaxEnvs=100 ;Envelope stuff
Global MaxEnvPoints=100
Dim EnvPoints(MaxEnvs)
Dim EnvActive(MaxEnvs)
Dim EnvPoint#(MaxEnvs,MaxEnvPoints)






; ******  DEMO
Graphics 1024,768,32,2
SetBuffer BackBuffer()
SetFont LoadFont("verdana",20)
w#=GraphicsWidth()
h#=GraphicsHeight()
env=CreateEnvelope()
curvy=1
.restart
		;Make a random envelope
		SeedRnd MilliSecs()
		ResetEnvelope env
		pnts=Rand(3,10)
		For t=1 To pnts
		AddEnvelopePoint env,Float(Rand(h*200,h*800))/1000
		Next
Repeat
Cls
	;show envelope point locations (grey vertical lines)
	For p=1 To envpoints(env)
	tw#=WhereBetween(1,envpoints(env),p)
	x=tw*w
	Color 40,40,40
	Line x,0,x,h
	Oval x-11,envpoint(env,p)-11,23,23,1
	Color 150,150,150
	Text x,envpoint(env,p),Int(envpoint(env,p)),1,1
	Next
;draw envelope line
For x=1 To w
If curvy=0 Then Color 85,225,65 Else Color 40,112,33
Plot x,envelope(env,x/w,0)
If curvy=1 Then Color 255,65,255 Else Color 112,33,122
Plot x,envelope(env,x/w,1)
Next
	;draw cursor
	Color 95,80,80
	Line MouseX(),0,MouseX(),h
	If curvy Then Color 255,100,255 Else Color 100,255,100
	EnvTween#=Float(MouseX()) / w
	Oval MouseX()-8,envelope(env,envtween,curvy)-8,17,17,1
	Color 255,255,255
	Text MouseX(),envelope(env,envtween,curvy)+8,envelope(env,envtween,curvy),1
	Text 0,0,"Points in envelope: "+envpoints(env)
	Text 0,20,"Reading from: "+envtween
	Text 0,50,"Click left mouse button to randomise points"
	Text 0,70,"Click right mouse button to toggle curving"
If MouseHit(1) Then Goto restart
If MouseHit(2) Then curvy=curvy+1:If curvy=2 Then curvy=0
Flip
Until KeyHit(1)
End











;ENVELOPES
Function CreateEnvelope()
For t=1 To envs
If envactive(t)=0 Then envactive(t)=1:Return t
Next
If envs=maxenvs Then RuntimeError "Too many envelopes."
envs=envs+1:envactive(envs)=1:Return envs
End Function

Function FreeEnvelope(e)
envactive(e)=0
envpoints(e)=0
End Function

Function AddEnvelopePoint(e,val#)
If envpoints(e)=maxenvpoints Then RuntimeError "Too many envelope points."
envpoints(e)=envpoints(e)+1
envpoint(e,envpoints(e))=val
End Function

Function Envelope#(e,tween#,usecurve=1)
If envpoints(e)=0 Then Return 0
If envpoints(e)=1 Then Return envpoint(e,1)
If envpoints(e)=2 Then Return between(envpoint(e,1),envpoint(e,2),tween)
point1=Floor(tween*(envpoints(e)-1))+1:point2=point1+1
tw#=wherebetween(point1-1,point2-1,tween*(envpoints(e)-1))
p1#=(point1-1)-0
p2#=(point1-1)+1
p3#=(point1-1)+2
p4#=(point1-1)+3
If p1<1 Then p1=1
If p1>envpoints(e) Then p1=envpoints(e)
If p2<1 Then p2=1
If p2>envpoints(e) Then p2=envpoints(e)
If p3<1 Then p3=1
If p3>envpoints(e) Then p3=envpoints(e)
If p4<1 Then p4=1
If p4>envpoints(e) Then p4=envpoints(e)
p1x#=(p1-1)/Float(envpoints(e)-1)
p2x#=(p2-1)/Float(envpoints(e)-1)
p3x#=(p3-1)/Float(envpoints(e)-1)
p4x#=(p4-1)/Float(envpoints(e)-1)
If usecurve Then Return spliney(p1x,envpoint(e,p1),p2x,envpoint(e,p2),p3x,envpoint(e,p3),p4x,envpoint(e,p4),      between(p2x,p3x,tw))
If Not usecurve Then Return between(envpoint(e,point1),envpoint(e,point2),tw)
End Function

Function ResetEnvelope(e)
envpoints(e)=0
End Function

;This function made by Kieryn "Mr Snidesmin" Phipps from the Code Archives.  Huge thanks.
Function SplineY#(x0#, y0#, x1#, y1#, x2#, y2#, x3#, y3#, x#)
a1#=(y2-y0)/(x2-x0):a2#=(y3-y1)/(x3-x1)
c1#=y1-a1*x1:c2#=y2-a2*x2
t#=(x-x1)/(x2-x1):t#=(Cos(180*(1-t))+1)/2
c#=c1*(1-t)+c2*t
a#=a1*(1-t)+a2*t
Return a*x+c
End Function




;MATHS FUNCTIONS ALSO IN MY MATHS LIB

Function Between#(v1#,v2#,t#):dif#=v2-v1
Return v1+(dif*t)
End Function

Function WhereBetween#(l#,r#,m#)
r=r-l:m=m-l
Return m/r
End Function
