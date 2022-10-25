; ID: 169
; Author: Rob Farley
; Date: 2001-12-28 16:38:15
; Title: Height Map Generator
; Description: Make unique random nice looking landscapes

; Height Map Generator

; Rob Farley 2001
; rob@mentalhangover.co.uk
; http://www.mentalhangover.co.uk

; *** This assumes you have a grass.bmp file in the same folder, please put a grass texture here. ***


Graphics 640,480
SetBuffer BackBuffer()

; set up varibles


size=256
; Play with these varibles to change the look of the landscape

scale=5
hill_size=50
hill_number=30
dip_size=100
dip_number=10

menu=1

While Not KeyHit(28)
Cls
Color 255,255,255
Text 320,0,"Height Map Generator",True,False
Text 320,250,"Use Arrow keys to adjust settings, enter to confirm selections",True,False
Color 255,255,0
Text 320,20,"Rob Farley 2001",True,False
Color 0,128,128

If KeyDown(208)=True Then menu=menu+1
If KeyDown(200)=True Then menu=menu-1
If menu>6 Then menu=1
If menu<1 Then menu=6

Color 0,128,128
If menu=1
	Color 0,255,255
	If KeyDown(205)=True Then size=size+256
	If KeyDown(203)=True Then size=size-256
	If size<256 Then size=256
	If size>1024 Then size=1024
	EndIf
Text 320,100,"Size:"+size,True,False
Color 0,128,128
If menu=2
	Color 0,255,255
	If KeyDown(205)=True Then scale=scale+1
	If KeyDown(203)=True Then scale=scale-1
	If scale<0 Then scale=0
	If scale>50 Then scale=50
	EndIf
Text 320,120,"Scale:"+scale,True,False
Color 0,128,128
If menu=3 
	Color 0,255,255
	If KeyDown(205)=True Then hill_size=hill_size+1
	If KeyDown(203)=True Then hill_size=hill_size-1
	If hill_size<0 Then hill_size=0
	EndIf
Text 320,140,"Hill Size:"+hill_size,True,False
Color 0,128,128
If menu=4
	Color 0,255,255
	If KeyDown(205)=True Then hill_number=hill_number+1
	If KeyDown(203)=True Then hill_number=hill_number-1
	If hill_number<0 Then hill_number=0
	EndIf
Text 320,160,"Hill Quantity:"+hill_number,True,False
Color 0,128,128
If menu=5
	Color 0,255,255
	If KeyDown(205)=True Then dip_size=dip_size+1
	If KeyDown(203)=True Then dip_size=dip_size-1
	If dip_size<0 Then dip_size=0
	EndIf
Text 320,180,"Dip Size:"+dip_size,True,False
Color 0,128,128
If menu=6
	Color 0,255,255
	If KeyDown(205)=True Then dip_number=dip_number+1
	If KeyDown(203)=True Then dip_number=dip_number-1
	If dip_number<0 Then dip_number=0
	EndIf
Text 320,200,"Dip Quantity:"+dip_number,True,False
Flip
Delay 100
Wend



Dim land#(size,size)
Dim land2#(size,size)
Dim hill#(45,45)
Dim blur#(9,9)
Data 0,0,0,1,1,1,0,0,0
Data 0,0,1,2,3,2,1,0,0
Data 0,1,2,3,5,3,2,1,0
Data 1,2,3,5,7,5,3,1,1
Data 1,3,5,7,9,7,5,3,1
Data 1,2,3,5,7,5,3,1,1
Data 0,1,2,3,5,3,2,1,0
Data 0,0,1,2,3,2,1,0,0
Data 0,0,0,1,1,1,0,0,0

For x=1 To 9
	For y=1 To 9
	Read blur#(x,y)
	blur#(x,y)=blur#(x,y)/10
	Next
Next

For x=0 To 20
For r=0 To 359
sx=(Sin(r)*x)+23
sy=(Cos(r)*x)+23
hill#(sx,sy)=Cos(x*4)*10
Next
Next





SeedRnd MilliSecs()

; Make Landscape - Drawing edges...
printstuff(1)
Text 320,100,"Drawing Edges...",True,False
Flip
h=120
	For x=0 To size-1
	land#(x+1,1)=h
	h=h+Rnd(0,scale)-(scale/2)
	Next
h=120
	For x=0 To size-1
	land#(1,x+1)=h
	h=h+Rnd(0,scale)-(scale/2)
	Next


; make landscape - fill in middle
printstuff(2)
Text 320,120,"Filling Middle...",True,False
Flip
For x=2 To size
 	For z=2 To size
	h=land#(x-1,z)
	i=land#(x,z-1)
	j=(h+i)/2
	h=j+Rnd(0,scale)-(scale/2)
	land#(x,z)=h
	Next
Next


; make landscape - Create hills
For n=1 To hill_number
printstuff(3)
Text 320,140,"Creating Hills..."+n+"/"+hill_number,True,False
Flip
	hx=Rnd(0,size)
	hy=Rnd(0,size)
	For x=1 To hill_size
		For xx=1 To 45
		For yy=1 To 45
		hxx=hx+xx-1
		hyy=hy+yy-1
		If hxx>size Then hxx=hxx-size
		If hxx<1 Then hxx=hxx+size
		If hyy>size Then hyy=hyy-size
		If hyy<1 Then hyy=hyy+size
		land#(hxx,hyy)=land#(hxx,hyy)+hill#(xx,yy)
		Next
		Next
		hx=hx+Rnd(0,10)-5
		hy=hy+Rnd(0,10)-5
		If hx<20 Then hx=size-20
		If hx>size-20 Then hx=20
		If hy<20 Then hy=size-20
		If hy>size-20 Then hy=20
	Next
Next


; make landscape - Create Dips
For n=1 To dip_number
printstuff(4)
Text 320,160,"Creating Dips..."+n+"/"+dip_number,True,False
Flip
	hx=Rnd(0,size)
	hy=Rnd(0,size)
	For x=1 To dip_size
		For xx=1 To 45
		For yy=1 To 45
		hxx=hx+xx-1
		hyy=hy+yy-1
		If hxx>size Then hxx=hxx-size
		If hxx<1 Then hxx=hxx+size
		If hyy>size Then hyy=hyy-size
		If hyy<1 Then hyy=hyy+size
		land#(hxx,hyy)=land#(hxx,hyy)-hill#(xx,yy)
		If land#(hxx,hyy)<-20 Then land#(hxx,hyy)=-20
		Next
		Next
		hx=hx+Rnd(0,10)-5
		hy=hy+Rnd(0,10)-5
		If hx<20 Then hx=size-20
		If hx>size-20 Then hx=20
		If hy<20 Then hy=size-20
		If hy>size-20 Then hy=20
	Next
Next


; Blur Landscape
printstuff(5)
Text 320,180,"Blurring Landscape pass 1",True,False
Flip
For x=1 To size
	For y=1 To size
		p=land#(x,y)
		For bx=-4 To 4
		For by=-4 To 4
		xx=x+bx
		yy=y+by
		If xx>=1 And xx<=size And yy>=1 And yy<=size Then land2#(xx,yy)=land2#(xx,yy)+(p*blur#(bx+5,by+5))
		Next
		Next
	Next
Next
For x=1 To size
	For y=1 To size
	land#(x,y)=land2#(x,y)
	land2#(x,y)=0
	Next
Next

; Normalise Landscape
printstuff(6)
Text 320,200,"Normalising Landscape pass 1",True,False
Flip
high#=-10000
low#=10000
For x=0 To size-1
	For y=0 To size-1
	If land#(x+1,y+1)>high# Then high#=land#(x+1,y+1)
	If land#(x+1,y+1)<low# Then low#=land#(x+1,y+1)
	Next
Next
normal#=high#-low#
For x=0 To size-1
	For y=0 To size-1
	l#=land#(x+1,y+1)
	l#=l#-low#
	l#=(l#/normal#)*255
	land#(x+1,y+1)=Int(l#)
	Next
Next

; Blur Landscape
printstuff(7)
Text 320,220,"Blurring Landscape pass 2",True,False
Flip
For x=1 To size
	For y=1 To size
		p=land#(x,y)
		For bx=-4 To 4
		For by=-4 To 4
		xx=x+bx
		yy=y+by
		If xx>=1 And xx<=size And yy>=1 And yy<=size Then land2#(xx,yy)=land2#(xx,yy)+(p*blur#(bx+5,by+5))
		Next
		Next
	Next
Next
For x=1 To size
	For y=1 To size
	land#(x,y)=land2#(x,y)
	land2#(x,y)=0
	Next
Next

; Normalise Landscape
printstuff(8)
Text 320,240,"Normalising Landscape pass 2",True,False
Flip
high#=-10000
low#=10000
For x=0 To size-1
	For y=0 To size-1
	If land#(x+1,y+1)>high# Then high#=land#(x+1,y+1)
	If land#(x+1,y+1)<low# Then low#=land#(x+1,y+1)
	Next
Next
normal#=high#-low#
For x=0 To size-1
	For y=0 To size-1
	l#=land#(x+1,y+1)
	l#=l#-low#
	l#=(l#/normal#)*255
	land#(x+1,y+1)=Int(l#)
	Next
Next

; Render Heightmap and capture image
printstuff(9)
Text 320,260,"Rendering Height Map",True,False
Flip

height=CreateImage (size,size)
SetBuffer ImageBuffer(height)
For x=0 To size-1
	For y=0 To size-1
	dot(x,y,land#(x+1,y+1))
	Next
Next

SetBuffer BackBuffer()

SaveImage height,"heightmap.bmp"


; Show 3D view of Landscape
Cls
Graphics3D 640,480,16			;Set graphics mode
SetBuffer BackBuffer()

terrain = LoadTerrain ("heightmap.bmp")
ScaleEntity terrain, 5, 100, 5
TerrainShading terrain, True
TerrainDetail terrain, 2500
grass=LoadTexture( "grass.bmp" )
ScaleTexture grass,20,20
EntityTexture terrain,grass

camera = CreateCamera()
CameraFogRange camera,700,1000
CameraFogMode camera,1
AmbientLight 255,255,255
middle= CreatePivot()
PositionEntity middle,640,0,640
rot#=0.0
While Not KeyHit(1)

cx#=640+(Sin(rot#)*400)
cz#=640+(Cos(rot#)*400)
cy#=TerrainY#(terrain,cx#,0.0,cz#)+5
PositionEntity camera,cx#,cy#,cz#


lx#=640+(Sin(rot#)*400)+(Cos(rot#+90)*20)
lz#=640+(Cos(rot#)*400)+(Sin(rot#+90)*20)

ly#=TerrainY#(terrain,lx#,0.0,lz#)+5
PositionEntity middle,lx#,ly#,lz#

PointEntity camera,middle
rot#=rot#+.1
If rot#=360 Then rot#=0
UpdateWorld
RenderWorld
Flip
Wend


End

Function dot(x,y,c)
If c>255 Then c=255
If c<0 Then c=0
Color c,c,c
Plot x,y
End Function

Function printstuff(prtstf)
Cls
Color 255,255,255
Text 320,0,"Height Map Generator",True,False
Color 255,255,0
Text 320,20,"Rob Farley 2001",True,False
Color 0,128,128
If prtstf<>1 Then Text 320,100,"Drawing Edges...",True,False
If prtstf<>2 Then Text 320,120,"Filling Middle...",True,False
If prtstf<>3 Then Text 320,140,"Creating Hills...",True,False
If prtstf<>4 Then Text 320,160,"Creating Dips...",True,False
If prtstf<>5 Then Text 320,180,"Blurring Landscape pass 1",True,False
If prtstf<>6 Then Text 320,200,"Normalising Landscape pass 1",True,False
If prtstf<>7 Then Text 320,220,"Blurring Landscape pass 2",True,False
If prtstf<>8 Then Text 320,240,"Normalising Landscape pass 2",True,False
If prtstf<>9 Then Text 320,260,"Rendering Height Map",True,False
Color 0,255,255
End Function
