; ID: 1002
; Author: degac
; Date: 2004-04-18 14:12:50
; Title: SIMPLE SHADOW SYSTEM
; Description: Shadow system

;lightmap world 01
;test
;graphio - degac72 at katamail.com
;16-04-2004 
;please inform me if the algo is useful!
; --- good playing --- 
; use linepick code from Tim Fisher (Flynn)
; probably needs some triks...
Graphics3D 640,480,32,2
SetBuffer BackBuffer()
Dim lm#(129,129),lmap#(129,129)

AmbientLight 20,20,20
Global fps=30
Global numeroOggetti=5
Global showMap=CreateImage(64,64)
Global imgbuffer=ImageBuffer(showMap)
Color 255,255,255
For xx=0 To 63
For yy=0 To 63
Plot xx,yy
Next
Next
GrabImage showMap,0,0
Cls
; --------------


txtBrick=LoadTexture("gfx\gothic3.bmp",1)
txtPillar=LoadTexture("gfx\castlest.bmp",1)

Global cuplane=CreateCube()
ScaleMesh cuplane,30,1,30
PositionEntity cuplane,30,-2,30
Global LMbuffer=CreateTexture(256,256,1); 256-->scale texture 4 128-->2  64-->1
ScaleTexture txtBrick,.5,.5
EntityTexture cuplane,txtBrick,0,0

cu=CreateCube()
ScaleTexture txtPillar,.5,.5
EntityTexture cu,txtPillar
;EntityColor cu,200,200,200


ScaleMesh cu,1,4,1
HideEntity cu
WireFrame 0
SeedRnd(MilliSecs())
Dim oggetti(numeroOggetti)
hh#=1
px=5
py=15
For k=0 To numeroOggetti
oggetti(k)=CopyEntity(cu)
Next

For k=0 To numeroOggetti
px=k+5+Rand(0,15)*3
py=k+5+Rand(0,15)*3
PositionEntity oggetti(k),px,0,py
If Rand(1,10)>8 Then ScaleMesh oggetti(k),1.2,1.2,1.2
EntityTexture oggetti(k),txtBrick
Next

Global sfera=CreatePivot()
Global sprite=LoadSprite("gfx\flare.png",1+2,sfera)
ScaleSprite sprite,2,2
movLight=CreateLight(2,sfera)
LightRange movLight,8
cameraPivot=CreatePivot(cuplane)
camera=CreateCamera(cameraPivot)
PositionEntity camera,30,10,-5
RotateEntity camera,20,90,0

CreatePlaceMap()


Global lightX=16
Global lightY=16

CreateMAP()
;ShowPlaceMap()
CreateShowMap()

Repeat
kk=kk+1
timeStart=MilliSecs()
;TurnEntity sfera,1,0,0
If giro=1 Then
		 TurnEntity cameraPivot,0,.5,0
		End If
UpdateWorld
RenderWorld
If lightX<>MouseX() Or lightY<>MouseY()
	lightX=MouseX()
	lightY=64-MouseY()
	PositionEntity sfera,lightX,1,lightY
	CreateMap()
End If
If KeyDown(200) Then MoveEntity camera,0,1,0:PointEntity camera,cuplane
If KeyDown(208) Then MoveEntity camera,0,-1,0:PointEntity camera,cuplane
If KeyDown(57) Then giro=1-giro

		
timeEnd=MilliSecs()
elapsed=(timeEnd-TimeStart)
Text 0,460,"Time: "+elapsed
Text 0,470,"FPS : "+((1000-elapsed)/FPS)
Flip
Until KeyDown(1)

EndGraphics()
End

Function CreateMAP()
CreateLightMap(lightX,lightY,10)
CreateShowMap()
CopyRect 0,0,64,64,0,0,imgBuffer,TextureBuffer(LMbuffer)
ScaleTexture LMbuffer,4,4
EntityTexture cuplane,LMbuffer,0,2


End Function


Function ShowPlaceMap()
Local stepx=1
Local sizeX=64
Local sizeY=sizex
xx=0
yy=0
For x=0 To sizeX-1
For y=SizeY-1 To 0 Step -1
p=lm(x,y)
alm=255-lmap(x,y)
yy=yy+stepx
If alm>0 Then Color alm,alm,alm:Rect xx,yy,2,2,1
Next
xx=xx+stepx
yy=0
Next
End Function



Function CreateShowMap()
LockBuffer(imgBuffer)
Local stepx=1
Local sizeX=64
Local sizeY=sizex
xx=0
yy=0
cc=128
rgb=cc Or (cc Shl 8) Or (cc Shl 16)
rrgb=255 Or (255 Shl 8) Or (255 Shl 16)

For x=0 To sizeX-1
For y=SizeY-1 To 0 Step -1
;p=lm(x,y)
alm=lmap(x,y)
yy=yy+stepx
;rrgb=ReadPixelFast(xx,yy,imgBuffer)

If alm>0 Then 
WritePixelFast(xx,yy,rgb,imgBuffer)
;WritePixelFast(xx+1,yy,rgb,imgBuffer)
;WritePixelFast(xx,yy+1,rgb,imgBuffer)
;WritePixelFast(xx+1,yy+1,rgb,imgBuffer)
Else
WritePixelFast(xx,yy,rrgb,imgBuffer)
;WritePixelFast(xx+1,yy,rrgb,imgBuffer)
;WritePixelFast(xx,yy+1,rrgb,imgBuffer)
;WritePixelFast(xx+1,yy+1,rrgb,imgBuffer)

End If
Next
xx=xx+stepx
yy=0
Next
UnlockBuffer(ImgBuffer)


End Function




Function CreateLightMap(sx#,sy#,sz#)
If sz>1060 Then Return
For x=0 To 63
For y=0 To 63
If lockedLine(x,y,lm(x,y),sx,sy,sz) Then lmap(x,y)=100 Else lmap(x,y)=0
Next
Next
End Function

Function LockedLine(x1#,y1#,z1#,x2#,y2#,z2#)
Local steps,xi#
x2=x2-x1
y2=y2-y1
z2=z2-z1
If Abs(x2)>Abs(y2) steps=Abs(x2) Else steps=Abs(y2)
xI=x2/steps
y2=y2/steps
z2=z2/steps

While (x1<=64) And (y1<=64) And (z1<=64) And (x1=>0) And (y1=>0) And (z1=>0)
If lm(x1,y1)>z1 Then Return True
x1=x1+XI
y1=y1+y2
z1=z1+z2
Wend
Return False
End Function 


Function CreatePlaceMap()
For k=0 To numeroOggetti

cu=Oggetti(k)

x=EntityX(cu)
z=EntityZ(cu)
w=MeshWidth(cu)
d=MeshDepth(cu)
h=MeshHeight(cu)

x1=x+1
y1=z+2
If x1<0 Then x1=0
If y1<0 Then y1=0


x2=x1+w-1
y2=(y1+d-1)

If x2>64 Then x2=64
If y2>64 Then y2=64

For xx=x1 To x2
For yy=y1 To y2

lm(xx,yy)=h
Next
Next
Next
End Function
