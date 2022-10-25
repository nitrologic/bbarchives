; ID: 667
; Author: DarkNature
; Date: 2003-05-03 14:56:46
; Title: 2D Golf ball code
; Description: Top-down 2D Golf ball code (not optimised)

Graphics 800,600
SeedRnd(MilliSecs())

Type ball

	Field x#,y#,h#
	Field lastx,lasty
	Field xv#,yv#,hv#
	Field t
	Field inFlight
	
End Type

Const grav#=.00058
Const fric#=0.975
Const bounce#=0.8
Global vAng#=45

Global tim=CreateTimer(60)
Global b.ball=New ball
Global windPwr#
Global windAng#

b\x#=GraphicsWidth()/2
b\y#=GraphicsHeight()-50
b\h#=0
b\xv#=0
b\yv#=0
b\hv#=0
b\inFlight=False

Function makeWind()
	windPwr#=Rnd#(0.01,0.02)
	windAng#=Rnd#(360)
End Function

Function hitBall()

	hAng#=ATan2(MouseX()-b\x#, MouseY()-b\y#)
	pwr#=Float(Sqr((b\x#-MouseX())^2+(b\y#-MouseY())^2))/100
	
	b\lastx=b\x#
	b\lasty=b\y#
	b\xv#=Sin(hAng#)*pwr#
	b\yv#=Cos(hAng#)*pwr#
	b\hv#=Cos(vAng#)*pwr#
	b\t=0
	b\h#=0
	b\inFlight=True

End Function

Function doBall()
	
	myFric#=fric#
	myBounce#=bounce#
	b\t=b\t+1
	b\x#=b\x#+b\xv#
	b\y#=b\y#+b\yv#
	b\h#=b\h#+b\hv#
		
	If b\h#>0
		b\hv#=b\hv#-(grav#*(b\t*b\t))*.5
	Else
		b\h#=0
		b\hv#=-b\hv#-(grav#*(b\t*b\t))*myBounce#
		b\xv#=b\xv#*myFric#
		b\yv#=b\yv#*myFric#
	End If
	
	If Abs(b\h#)>1
		b\xv#=b\xv#+Sin(windAng#)*windPwr#
		b\yv#=b\yv#+Cos(-windAng#)*windPwr#
	End If
	
	If Abs(b\xv#)<0.1 And Abs(b\yv#)<0.1
		b\inFlight=False
		b\h#=0
	End If
	
	Color 255,255,255
	Oval b\x#-((b\h#/5)/2)-2,b\y#-((b\h#/5)/2)-2,4+(b\h#/5),4+(b\h#/5)

End Function

SetBuffer BackBuffer()
ClsColor 0,120,0
MoveMouse(400,300)
makeWind()
windtime=MilliSecs()
While Not KeyHit(1)
	
	WaitTimer(tim)
	Cls
		
	Rect MouseX()-5,MouseY()-5,10,10,0
	
	If MouseHit(1) And b\inFlight=False hitBall()
	If KeyDown(30) And vAng#>0 vAng#=vAng#-0.5
	If KeyDown(44) And vAng#<90 vAng#=vAng#+0.5
	
	xoff=30
	yoff=550
	Line xoff,yoff,xoff-Sin(-vAng#)*50,yoff-Cos(vAng#)*50
	Line xoff,yoff,xoff+50,yoff
	Text xoff,yoff+20,90-vAng#
	
	xoff=GraphicsWidth()-60
	yoff=550
	Line xoff-30,yoff,xoff+30,yoff
	Line xoff,yoff-30,xoff,yoff+30
	Color 2555,255,0
	Line xoff,yoff,xoff+Sin(windAng#)*30,yoff+Cos(windAng#)*30
	Text xoff-25,yoff+35,Int(windPwr#*1000)+" KM/H"
	
	If MilliSecs()>windtime+5000+Rnd(2000) makeWind(): windtime=MilliSecs()
	
	doBall()
	
	Flip

Wend
End
