; ID: 2680
; Author: Streaksy
; Date: 2010-03-29 07:58:46
; Title: DrawTexture - Like DrawImage but with TONS of real-time options
; Description: This has made such a difference to my projects!  Time to stop hoarding it.  This goes against every stingy bone in my body :P

;NOTE: This lib has leftover junk because it's been stripped of some functions I've remade better seperately



Global yrat#,guicam,desktopmesh,flaggy,GLcursoralpha#=1,GLcursorhidden=0
Global glcamoffset=30000
Global GLInited
Const GLEnabled=1 ;so apps know if they can use these commands!!
Const GLLibPresent=1 ;so apps know if they can use these commands!!
Global masktext=1
Global DrawTextureMesh,DrawTexturePiv
Global DrawTextureMeshhidden
Global lastalph#=1
Global lastfilter=1
Const DrawTextureMethod=1
Global quadded ;remember if last drawtexture command was for a quad, so it can return the UV to 0-1 instead of the custom range
Dim quadgridx(513,513)
Dim quadgridy(513,513)








;TEXT PLATES
Const TextAlphaFlag=2 ;2 for smooth, 4 for sharp
Global texplates,maxtexplates=300
Dim texplate(maxtexplates)
Dim texplateimg(maxtexplates)
Dim texplatew(maxtexplates)
Dim texplateh(maxtexplates)
Dim texplatetime(maxtexplates)
Dim texplatefontfullw(maxtexplates)
Dim texplatefontw(maxtexplates)
Dim texplatefonth(maxtexplates)
Dim texplatetext$(maxtexplates)
Dim texplatefilter(maxtexplates)









;SILLY DEMO
AppTitle "DrawTexture, DrawTextureQuad, TextureLine, Texture Text Demo"
Global FPS_fpstime ;FPS()
Graphics3D 1024,768,32,2
SetBuffer BackBuffer()
SetFont LoadFont("impact",60,1)
;ClsColor 150,150,150
Cls
SeedRnd MilliSecs()
	w=300:h=300
	For t=1 To 230
	Color Rnd(255),Rnd(255),Rnd(255)
	thickLine Rand(-100,w),Rand(-100,h),Rand(-100,w)/2,Rand(-100,h)/2
	Next
	testtex=CreateTexture(128,128,1+16+32)
	CopyRect 0,0,128,128,0,0,BackBuffer(),TextureBuffer(testtex)
	Cls
	w=GraphicsWidth():h=GraphicsHeight()
	For t=1 To 130
	sh=Rand(1,3)
	If Rand(1,100)=1 Then Color Rnd(255),Rnd(255),Rnd(255) Else Color Rnd(155),Rnd(155),Rnd(155)
	If sh=1 Then thickLine Rand(0,w),Rand(0,h),Rand(0,w),Rand(0,h)
	If sh=2 Then Rect Rand(0,w)-75,Rand(0,h)-75,150,150,1
	If sh=3 Then thickOval Rand(0,w)-75,Rand(0,h)-75,150,150
	Next
bgpic=CreateImage(w,h):GrabImage bgpic,0,0
mem=AvailVidMem()
Repeat
mx2#=MouseX()/2:my2#=MouseY()/2
DrawBlock bgpic,0,0
Color 255,0,0
texturetext MouseX(),MouseY(),"Behind",1,1,.7,1,0,(mx2/2)-110,(my2/200)+1,(my2/150)+1
drawtexture testtex,0,0,.8,1,(MouseX()*255)/w,(MouseY()*255)/h,255,Rand(-5,5),(mx2/2)+1,(my2/2)+1
drawtexturequadgrid testtex,0,500,MouseX(),MouseY(),0,h,500,h-144,.7,3,255-(MouseDown(1)*100),255-(MouseDown(2)*100),255,5
Color 255,255,255
texline 0,500,MouseX(),MouseY(),20,testtex
texline 500,h-144,MouseX(),MouseY(),20,testtex
Color 255,255,0
texturetext 100+mx2,200+my2,"Infront but transparent",0,0,.5
Color 255,255,255
texturetext 200+mx2,300+my2,"This text drawn with shadow",0,0,1,1,1
Color 150,50,255
texturetext 200+mx2,600+my2,"This text drawn with add-filter",0,0,.65,2
Color 255,255,255
Text 300,50,"Memory used by text: "+((mem-AvailVidMem())/1024)+"kb";,0,0,.6,2
Text 0,0,"FPS: "+FPS()
Flip
Until KeyHit(1)
End
Function FPS()
oldtime=FPS_fpstime
FPS_fpstime=MilliSecs()
elapsed=FPS_fpstime-oldtime
If Not elapsed elapsed=1
FPS_fps=1000/elapsed
Return FPS_FPS
End Function
Function ThickLine(x1,y1,x2,y2,th=4)
For x=-th To th
For y=-th To th
Line x1+x,y1+y,x2+x,y2+y
Next
Next
End Function
Function Thickoval(x1,y1,w,h,th=1)
For x=-th To th
For y=-th To th
Oval x1+x,y1+y,w,h,0
Next
Next
End Function
















Function X3D#(x):Return betweengl(-1,1,Float(x)/(Float(GraphicsWidth()))):End Function
Function Y3D#(y):If yrat=0 Then yrat#=Float(GraphicsHeight())/Float(GraphicsWidth())
Return -betweengl(-yrat,yrat,Float(y)/(Float(GraphicsHeight()))):End Function
Function directionGL#(EnX#,EnY#,OtX#,OtY#)
If OtX#>EnX# And EnY#>=OtY# Then Return ATan((OtX#-EnX#)/(EnY#-OtY#))
If OtX#>=EnX# And OtY#>EnY# Then Return 90+ATan((OtY#-EnY#)/(OtX#-EnX#))
If EnX#>OtX# And OtY#>=EnY# Then Return 180+ATan((EnX#-OtX#)/(OtY#-EnY#))
If EnX#>=OtX# And EnY#>OtY# Then Return 270+ATan((EnY#-OtY#)/(EnX#-OtX#))
End Function
Function distanceGL#(x1#,y1#,x2#,y2#):Return Sqr((x1-x2)*(x1-x2)+(y1-y2)*(y1-y2)):End Function




Function DrawTexture(tex,x,y,alph#=1,filter=1,r=255,g=255,b=255,ang#=0,sx=0,sy=0,scx#=1,scy#=1,axisx=-1,axisy=-1)
If GLInited=0 Then InitGL
If axisx=-1 And axisy=-1 Then DrawTextureRaw tex,x,y,alph,filter,r,g,b,ang,sx,sy,scx,scy:Return
If sx=0 Then sx=TextureWidth(tex)
If sy=0 Then sy=TextureHeight(tex)
If axisx=-1 Then axisx=sx/2
If axisy=-1 Then axisy=sy/2
x1=x+(axisx)	;center of rotation
y1=y+(axisy)
x2=x+(sx/2)	;center of texture
y2=y+(sy/2)
dir#=directionGL(x1,y1,x2,y2):dis#=distanceGL(x1,y1,x2,y2)
x=x1+(anglex2(dir+ang)*dis)
y=y1+(angley2(dir+ang)*dis)
x=x-(sx/2)
y=y-(sy/2)
DrawTextureRaw tex,x,y,alph,filter,r,g,b,ang,sx,sy,scx,scy
End Function



Function DrawTextureRAW(tex,x,y,alph#=1,filter=1,r=255,g=255,b=255,ang#=0,sx=0,sy=0,scx#=1,scy#=1);,axisx=-1,axisy=-1)
If tex=0 Then RuntimeError "Texture doesn't exist."
;DrawTextureMethod=1
dtox=x:dtoy=y
If sx=0 Then sx=TextureWidth(tex)
If sy=0 Then sy=TextureHeight(tex)
If camera<>0 Then HideEntity camera
If cam<>0 Then HideEntity cam

If DrawTextureMethod=2 Then
If ang<>0 Or scx<>1 Or scy<>1 Then
If axisx=0 Then tx=x-(sx*.5) Else tx=axisx
If axisy=0 Then ty=y-(sy*.5) Else ty=axisy
dis#=distanceGL#(dtox,dtoy,tx,ty)
If (scx*sx)=(scy*sy) Then ank=-45 Else ank=directionGL(dtox,dtoy,tx,ty)
x=x+anglex2(ang+ank)*dis*scx
y=y+angley2(ang+ank)*dis*scy
x=x+(sx*.5)
y=y+(sy*.5)
EndIf
EndIf

If DrawTextureMeshhidden=1 Then
DrawTextureMeshhidden=0
ShowEntity DrawTextureMesh
msh=DrawTextureMesh
srf=GetSurface(msh,1)
xx#=X3D(x)
yy#=Y3D(y)
w#=X3D#(x+sx)-xx
h#=Y3D#(y+sy)-yy
VertexCoords srf,0,0,0,0
VertexCoords srf,1,w,0,0
VertexCoords srf,2,0,h,0
VertexCoords srf,3,w,h,0
	If quadded Then ;return texcoords to normal after the drawtexturequad command has messed with them
	lo#=.0056-.0008
	hi#=1.0033
	VertexTexCoords srf,0,lo,lo
	VertexTexCoords srf,1,hi,lo
	VertexTexCoords srf,2,lo,hi
	VertexTexCoords srf,3,hi,hi
	EndIf
Else
datex=DrawTextureMesh
msh=CreateMesh():srf=CreateSurface(msh)
DrawTextureMesh=msh
xx#=X3D(x)
yy#=Y3D(y)
w#=X3D#(x+sx)-xx
h#=Y3D#(y+sy)-yy
lo#=.0056-.0008
hi#=1.0033
v1=AddVertex(srf,0,0,0,lo,lo)
v2=AddVertex(srf,w,0,0,hi,lo)
v3=AddVertex(srf,0,h,0,lo,hi)
v4=AddVertex(srf,w,h,0,hi,hi)
AddTriangle srf,v1,v2,v3
AddTriangle srf,v4,v3,v2
EntityFX msh,1
EntityOrder msh,-1
EndIf

If DrawTexturePiv=0 Then DrawTexturePiv=CreatePivot():EntityParent msh,DrawTexturePiv,1

PositionEntity DrawTexturePiv,xx,yy+glcamoffset,0,1:PositionEntity msh,0,0,0,0

EntityTexture msh,tex,0,0
If lastalph#<>alph# Then EntityAlpha msh,alph#:lastalph#=alph#
If lastfilter<>filter Then EntityBlend msh,filter:lastfilter=filter
EntityColor msh,r,g,b

If DrawTextureMethod=1 Then
If scx<>1 Or scy<>1 Or ang<>0 Then PositionMesh msh,-(w/2),-(h/2),0
If scx<>1 Or scy<>1 Then ScaleMesh msh,scx,scy,1
If ang<>0 Then RotateMesh msh,0,0,-ang
If scx<>1 Or scy<>1 Or ang<>0 Then PositionMesh msh,(w/2)*1,(h/2)*1,0
EndIf

If DrawTextureMethod=2 Then If scx<>1 Or scy<>1 Then ScaleMesh msh,scx,scy,1
If DrawTextureMethod=2 Then If ang<>0 Then RotateMesh msh,0,0,-ang

If DrawTextureMethod=3 Or DrawTextureMethod=4 Then
RotateEntity msh,0,0,-ang
ScaleEntity msh,scx,scy,1
MoveEntity msh,-(w/2)*scx,-(h/2)*scy,0
TranslateEntity DrawTexturePiv,(w/2)*scx,(h/2)*scy,0
EndIf

If DrawTextureMethod=4 Then
oxx=xx:oyy=yy
If ang<>0 Or scx<>1 Or scy<>1 Then
xx=xx-(w*scx*.5)
yy=yy-(h*scy*.5)
dis#=distanceGL(oxx,oyy,xx,yy)
If scx=scy Then ank=-45 Else ank=directionGL(oxx,oyy,xx,yy)
xx=xx+anglex2(ang+ank)*dis
yy=yy+angley2(ang+ank)*dis
xx=xx+(w*scx)
yy=yy+(h*scy)
PositionEntity DrawTexturePiv,xx,yy,0,1
EndIf
EndIf

ShowEntity guicam:RenderWorld:UpdateGL:HideEntity guicam
If camera<>0 Then ShowEntity camera
If cam<>0 Then ShowEntity cam
End Function

Function AngleX2#(aa#):Return Cos(aa-90):End Function
Function AngleY2#(aa#):Return Sin(aa-90):End Function



Function UpdateGL()
If DrawTextureMesh<>0 Then If DrawTextureMeshhidden=0 Then DrawTextureMeshhidden=1:HideEntity DrawTextureMesh
End Function



Function InitGL()
glinited=1
guicam=CreateCamera()
PositionEntity guicam,0,glcamoffset,-1
CameraRange guicam,1,20000
CameraProjMode guicam,2
CameraZoom guicam,1
;AmbientLight 255,255,255
CameraClsMode guicam,0,0
;Dither 0
End Function




Function AlphaRect(x,y,w,h,a#=.5,filt=1)
If whitetex=0 Then
oldybuf=GraphicsBuffer()
whitetex=CreateTexture(2,2,1+2)
SetBuffer TextureBuffer(whitetex)
rrr=ColorRed()
ggg=ColorGreen()
bbb=ColorBlue()
Color 255,255,255
Rect 0,0,2,2,1
SetBuffer oldybuf
Color rrr,ggg,bbb
EndIf
DrawTexture whitetex,x,y,a,filt,ColorRed(),ColorGreen(),ColorBlue(),0,w,h,1,1
End Function


Function betweenglgl#(v1#,v2#,t#):dif#=v2-v1:Return v1+(dif*t):End Function


Function Limglgl#(vl#,lw#,up#) ; A.K.A - Clamp
If vl<lw Then Return lw
If vl>up Then Return up
Return vl:End Function


Function ARGBglgl(A,R,G,B):Return RGBAglgl(r,g,b,a):End Function

Function RGBAglgl(R,G,B,A=255):If a<0 Then Return (R*256*256)+(g*256)+b
Return A Shl 24 Or R Shl 16 Or G Shl 8 Or B Shl 0:End Function




Function GLTriangle(x1#,y1#,x2#,y2#,x3#,y3#,fill=0)
If fill=0 Then Line x1,y1,x2,y2:Line x2,y2,x3,y3:Line x3,y3,x1,y1:Return
If x2<x1 Then x#=x2:y#=y2:x2=x1:y2=y1:x1=x:y1=y
If x3<x1 Then x#=x3:y#=y3:x3=x1:y3=y1:x1=x:y1=y
If x3<x2 Then x#=x3:y#=y3:x3=x2:y3=y2:x2=x:y2=y
If x1<>x3 Then slope1#=(y3-y1)/(x3-x1)
length#=x2-x1
If length<>0 Then slope2#=(y2-y1)/(x2-x1):For x=0 To length:Line x+x1,x*slope1+y1,x+x1,x*slope2+y1:Next
y=length*slope1+y1:length=x3-x2
If length<>0 Then slope3#=(y3-y2)/(x3-x2):For x=0 To length:Line x+x2,x*slope1+y,x+x2,x*slope3+y2:Next
End Function








Function glLim#(vl#,lw#,up#) ; A.K.A - Clamp
If vl<lw Then Return lw
If vl>up Then Return up
Return vl:End Function








Function BetweenGL#(v1#,v2#,t#):dif#=v2-v1
Return v1+(dif*t)
End Function

Function TexLine(dx1,dy1,dx2,dy2,th=5,tex,ignorecolour=0)
dis#=distancegl(dx1,dy1,dx2,dy2)
dir#=directiongl(dx1,dy1,dx2,dy2)-90
bx=betweengl(dx1,dx2,.5)
by=betweengl(dy1,dy2,.5)
dnx=dx1:dny=dy1:dnx=dnx+(anglex2(dir)*(th/2)):dny=dny+(angley2(dir)*(th/2))
If ignorecolour=0 Then DrawTexture tex,  dnx,dny,    1,1,  ColorRed(),ColorGreen(),ColorBlue(),  dir,   dis,th,   1,1,    0,0
If ignorecolour<>0 Then DrawTexture tex,  dnx,dny,    1,1,  255,255,255,  dir,   dis,th,   1,1,    0,0
End Function








Function DrawTextureQuad(tex,x1,y1,x2,y2,x3,y3,x4,y4,alph#=1,filter=1,r=255,g=255,b=255,ulo#=0,vlo#=0,uhi#=1,vhi#=1)
quadded=1
;pass the cordinates in this order:
;  1----2
;  :    :
;  :    :
;  3----4
If tex=0 Then RuntimeError "Texture doesn't exist."
If GLInited=0 Then InitGL
dtox=x:dtoy=y
If sx=0 Then sx=TextureWidth(tex)
If sy=0 Then sy=TextureHeight(tex)
If camera<>0 Then HideEntity camera
If cam<>0 Then HideEntity cam
xx#=0
yy#=0
vx1#=x3d(x1)
vy1#=y3d(y1)
vx2#=x3d(x2)
vy2#=y3d(y2)
vx3#=x3d(x3)
vy3#=y3d(y3)
vx4#=x3d(x4)
vy4#=y3d(y4)

If DrawTextureMeshhidden=1 Then	;reposition mesh
DrawTextureMeshhidden=0
ShowEntity DrawTextureMesh
msh=DrawTextureMesh
srf=GetSurface(msh,1)
VertexCoords srf,0,vx1,vy1,0
VertexCoords srf,1,vx2,vy2,0
VertexCoords srf,2,vx3,vy3,0
VertexCoords srf,3,vx4,vy4,0
VertexTexCoords srf,0,ulo,vlo
VertexTexCoords srf,1,uhi,vlo
VertexTexCoords srf,2,ulo,vhi
VertexTexCoords srf,3,uhi,vhi
Else
datex=DrawTextureMesh			;create mesh in the first place
msh=CreateMesh():srf=CreateSurface(msh)
DrawTextureMesh=msh

;baselo#=.0056-.0008
;basehi#=1.0033
;xlo#=betweengl(baselo,basehi,ulo)
;ylo#=betweengl(baselo,basehi,vlo)
;xhi#=betweengl(baselo,basehi,uhi)
;yhi#=betweengl(baselo,basehi,vhi)
xlo#=ulo
ylo#=vlo
xhi#=uhi
yhi#=vhi

v1=AddVertex(srf,vx1,vy1,0,xlo,ylo)
v2=AddVertex(srf,vx2,vy2,0,xhi,ylo)
v3=AddVertex(srf,vx3,vy3,0,xlo,yhi)
v4=AddVertex(srf,vx4,vy4,0,xhi,yhi)

AddTriangle srf,v1,v2,v3
AddTriangle srf,v4,v3,v2
EntityFX msh,1
EntityOrder msh,-1
EndIf
If DrawTexturePiv=0 Then DrawTexturePiv=CreatePivot():EntityParent msh,DrawTexturePiv,1
PositionEntity DrawTexturePiv,xx,yy+glcamoffset,0,1:PositionEntity msh,0,0,0,0
EntityTexture msh,tex,0,0
If lastalph#<>alph# Then EntityAlpha msh,alph#:lastalph#=alph#
If lastfilter<>filter Then EntityBlend msh,filter:lastfilter=filter
EntityColor msh,r,g,b
ShowEntity guicam:RenderWorld:UpdateGL:HideEntity guicam
If camera<>0 Then ShowEntity camera
If cam<>0 Then ShowEntity cam
End Function












;draw an interpolated quad!
Function DrawTextureQuadGrid(tex,x1,y1,x2,y2,x3,y3,x4,y4,A#=1,filter=1,rd=255,gr=255,bl=255,steps=5)
;pass the cordinates in this order:
;  1----2
;  :    :
;  :    :
;  3----4
steps=steps+1
If steps<3 Then steps=3
If steps>512 Then steps=512

For x=1 To steps		;get the grid positions
For y=1 To steps
twx#=Float(x-1)/Float(steps-1)
twy#=Float(y-1)/Float(steps-1)
dxup=betweengl(x1,x2,twx)
dyup=betweengl(y1,y2,twx)
dxdn=betweengl(x3,x4,twx)
dydn=betweengl(y3,y4,twx)
dxlf=betweengl(x1,x3,twy)
dylf=betweengl(y1,y3,twy)
dxrt=betweengl(x2,x4,twy)
dyrt=betweengl(y2,y4,twy)
quadgridx(x,y)=betweengl(dxlf,dxrt,twx)
quadgridy(x,y)=betweengl(dyup,dydn,twy)
Next
Next


For x=1 To steps-1		;draw in the quad segments
For y=1 To steps-1
dx1=quadgridx(x,y)
dy1=quadgridy(x,y)
dx2=quadgridx(x+1,y)
dy2=quadgridy(x+1,y)
dx3=quadgridx(x,y+1)
dy3=quadgridy(x,y+1)
dx4=quadgridx(x+1,y+1)
dy4=quadgridy(x+1,y+1)
u1#=Float(x-1) / Float(steps-1)
v1#=Float(y-1) / Float(steps-1)
u2#=Float(x-0) / Float(steps-1)
v2#=Float(y-0) / Float(steps-1)
DrawTextureQuad tex,dx1,dy1,dx2,dy2,dx3,dy3,dx4,dy4,a,filter,rd,gr,bl,u1#,v1#,u2#,v2#
Next
Next


End Function















Function TextureText(x,y,tx$,cx=0,cy=0,a#=1,filter=1,shadow=0,angle#=0,scalex#=1,scaley#=1,axisx=0,axisy=0)
startred=ColorRed():startgreen=ColorGreen():startblue=ColorBlue()
		If shadow Then
		Color 0,0,0
		TextureText x-2,y+3,tx$,cx,cy,a*.6,1,0,angle#,scalex#,scaley#,axisx,axisy
		EndIf
tw=gettexturesize(StringWidth(tx))
th=gettexturesize(StringHeight(tx))

fontw=FontWidth()
fonth=FontHeight()
fontfullw=StringWidth(tx)
If cx Then x=x-((fontfullw/2)*scalex)
If cy Then y=y-((fonth/2)*scaley)
		For t=1 To texplates ;see if the exact plate already exists
		If texplate(t)<>0 Then
		If texplatew(t)=tw And texplateh(t)=th Then
		If texplatefontfullw(t)=fontfullw Then
		If texplatefontw(t)=fontw And texplatefonth(t)=fonth Then
		If texplatefilter(t)=filter Then
		If texplatetext(t)=tx Then
		clearplate=0
		daplate=t
		Goto drawplate
		EndIf
		EndIf
		EndIf
		EndIf
		EndIf
		EndIf
		Next
If texplates=maxtexplates Then ;no space left, so find a plate to replace
	ms=MilliSecs()
	For t=1 To texplates ;find a suitable existing plate thats been disused for at least 2 minutes
	If texplate(t)<>0 Then
	If texplatew(t)=tw And texplateh(t)=th Then
	If texplatefilter(t)=filter Then
	If ms-texplatetime(t)<120000 Then
	clearplate=1
	daplate=t
	Goto makeplate
	EndIf
	EndIf
	EndIf
	EndIf
	Next
oldest=0:time=0	;find the oldest and most disused plate
For t=1 To texplates
If texplatetime(t)>time Then oldest=t:time=texplatetime(t)
Next
If oldest=0 Then Color startred,startgreen,startblue:Text x,y,tx,cx,cy:Return ;couldn't find the oldest one for some reason so give up
daplate=oldest
If texplate(daplate)<>0 Then FreeTexture texplate(daplate):texplate(daplate)=0
If texplateimg(daplate)<>0 Then FreeImage texplateimg(daplate):texplateimg(daplate)=0
Goto updateplate
EndIf
							texplates=texplates+1:daplate=texplates ;none found so make new one
			.updateplate
			texplate(daplate)=CreateTexture(tw,th,1+((filter=1)*textalphaflag)+16+32)
			If filter=1 Then texplateimg(daplate)=CreateImage(tw,th)
.makeplate
buf=GraphicsBuffer()
	If filter=2 Then ;add filter is simple
	SetBuffer TextureBuffer(texplate(daplate))
	If clearplate Then Color 0,0,0:Rect 0,0,tw,th,1
	Color 255,255,255
	Text 0,0,tx
	SetBuffer buf
	EndIf
;		If filter=2 Then ;multiply filter is simple (having problems with this)
;		SetBuffer TextureBuffer(texplate(daplate))
;		If clearplate Then Color 255,255,255:Rect 0,0,tw,th,1
;		Color startted,startgreen,startblue
;		Text 0,0,tx
;		SetBuffer buf
;		EndIf
	If filter=1 Then ;normal filter requires a bit extra messing around with images cos blitz complicates it by not allowing text on alpha-capable textures
	SetBuffer ImageBuffer(texplateimg(daplate))
	If clearplate Then Color 0,0,0:Rect 0,0,tw,th,1
	Color 255,255,255
	Text 0,0,tx
	SetBuffer buf
	LockBuffer ImageBuffer(texplateimg(daplate))
	LockBuffer TextureBuffer(texplate(daplate))
	For dx=0 To tw-1
	For dy=0 To th-1
	hue=ReadPixelFast(dx,dy,ImageBuffer(texplateimg(daplate)))
	cred=(hue And $00FF0000) Shr 16
	calp=cred
	hue=calp Shl 24 Or 255 Shl 16 Or 255 Shl 8 Or 255
	WritePixelFast dx,dy,hue,TextureBuffer(texplate(daplate))
	Next
	Next
	UnlockBuffer ImageBuffer(texplateimg(daplate))
	UnlockBuffer TextureBuffer(texplate(daplate))
	EndIf
.drawplate
texplatew(daplate)=tw
texplateh(daplate)=th
texplatefontfullw(daplate)=fontfullw
texplatefontw(daplate)=fontw
texplatefonth(daplate)=fonth
texplatetext(daplate)=tx
texplatefilter(daplate)=filter
texplatetime(daplate)=MilliSecs()
If filter=2 Then filter=3
drawtexture texplate(daplate),x,y,a,filter,startred,startgreen,startblue,angle#,0,0,scalex#,scaley#,axisx,axisy
Color startred,startgreen,startblue
End Function



Function GetTextureSize(s) ;returns the smallest texture size that will encompass the given size.
power=2^18:oldpower=power
Repeat
If s=>power Then Return oldpower
oldpower=power:power=power/2
Until power=1
Return 1
End Function
