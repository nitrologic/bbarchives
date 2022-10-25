; ID: 1788
; Author: markcw
; Date: 2006-08-18 04:53:56
; Title: Simple Cubemapped Water
; Description: Example of how to create water using cubemapping

;Simple Cubemapped Water, on 24/8/06

Graphics3D 640,480,0,2
AppTitle "Simple Cubemapped Water"
SetBuffer BackBuffer()

;create view camera on pivot, init position and rotation
campivot=CreatePivot()
viewcam=CreateCamera(campivot)
PositionEntity viewcam,0,1,0
PositionEntity campivot,14,3,-14
MoveMouse GraphicsWidth()/2,GraphicsHeight()/2
HidePointer ;don't need to show cursor
camx#=25 : camy#=45
RotateEntity campivot,camx#,camy#,0

;init cubemap camera
texsize=256 ;cubemap texture size, multiple of 2
cubecam=CreateCamera()
CameraViewport cubecam,0,0,texsize,texsize ;viewport same as texture
CameraClsMode cubecam,False,True ;color-buffer, z-buffer
CameraProjMode cubecam,0 ;disable cubemap camera

light=CreateLight() ;light
RotateEntity light,75,45,0

;create a sky sphere 
sky=CreateSphere(16)
ScaleEntity sky,500,500,500
FlipMesh sky
EntityFX sky,1 ;full bright
skytex=CreateSkyTexture(256,256) ;generate sky
EntityTexture sky,skytex,0,0

;create a mesh to cubemap
meshsize=24 : quadsize=1
water=CreateFlatMesh(meshsize,meshsize,quadsize,0,0,0)
wsurf=GetSurface(water,1) ;mesh surface
EntityAlpha water,0.8 ;transparency
watertex=CreateTexture(texsize,texsize,1+48+128+256) ;cubemap
EntityTexture water,watertex,0,0

;create a basin cube (swimming pool) mesh
basin=CreateBasinCube(48,48,meshsize,5,meshsize,0,0.5,0)
EntityFX basin,1
stonetex=CreateConcreteTexture(256,256) ;generate paved stone
EntityTexture basin,stonetex,0,0

;create a box to float around
box=CreateCube()
boxtex=CreateB3dLogoTexture()
EntityTexture box,boxtex,0,0

;create a plane for camera collision
plane=CreatePlane()
PositionEntity plane,0,-5,0
EntityAlpha plane,0 ;invisible but allow collisions

;create a target to draw in the screen center
target=CreateTargetImage()
MidHandle target

;init collisions between camera and ground
groundid=1 : cameraid=2
Collisions cameraid,groundid,2,2 ;2=ellipsoid-to-polygon, 2=slide1
EntityType basin,groundid
EntityType plane,groundid
EntityType campivot,cameraid
EntityRadius campivot,2 ;ellipsoid radius

;init linepick, for box on water
EntityPickMode water,2 ;2=polygon

;init camera fog range and color, for underwater
CameraFogRange viewcam,-50,50
CameraFogColor viewcam,100,150,200

frametimer=CreateTimer(60) ;set framerate

;main loop
While Not KeyDown(1)

 WaitTimer(frametimer) ;wait until timer reaches fps

 ;render the cubemap
 If under=1 Then HideEntity basin ;hide basin mesh when underwater
 HideEntity water ;hide cubemap mesh, before render
 UpdateCubemap(watertex,cubecam,viewcam)
 ShowEntity water
 If under=1 Then ShowEntity basin

 ;the cubemap is smoother if rendered before other calculations
 UpdateWorld
 RenderWorld ;render the scene

 ;create waves on cubemap mesh, from "cubedemo.bb" by Rob Cummings
 wh#=0.15 ;wave height, "calm" range 0.05..0.2
 ws#=ws#+3 : If ws#>=360 Then ws#=0 ;wave speed, wrap to avoid error
 wd#=wd#+0.1 : If wd#>=360 Then wd#=0 ;wave direction
 xd#=Sin(wd#)*45 : If Abs(xd#)<0.01 Then xd#=xd#*10 ;min to avoid error
 zd#=Cos(wd#)*-45 : If Abs(zd#)<0.01 Then zd#=zd#*10
 For i=0 To CountVertices(wsurf)-1
  ;vy=wheight*sin(wspeed+(vx*sin(wdir)*45)+(vxz*cos(wdir)*-45))
  vy#=wh#*Sin(ws#+(VertexX(wsurf,i)*xd#)+(VertexZ(wsurf,i)*zd#))
  VertexCoords wsurf,i,VertexX(wsurf,i),vy#,VertexZ(wsurf,i)
 Next
 UpdateNormals water ;set normals, to distort cubemap texture

 ;flip cubemap mesh and fog if camera goes underwater
 lastunder=under : under=0 : fog=0
 If EntityY(viewcam,True)<0 Then under=1
 If lastunder<>under Then FlipMesh water
 If EntityX(campivot)>-12 And EntityX(campivot)<12 ;fog if inside basin
  If EntityZ(campivot)>-12 And EntityZ(campivot)<12 Then fog=under
 EndIf
 CameraFogMode viewcam,fog ;set camera fog, 0=off/1=linear

 ;move the box around the water
 btic#=btic#+0.2
 bx#=10*Sin(btic#)
 bz#=10*Cos(btic#)
 ;by#=0.2*Sin(btic#*16) ;for a fake bob
 ;linepick y position of water polygon, for a real bob
 hpick=LinePick(EntityX(box),wh#+EntityY(water),EntityZ(box),0,-wh#*2,0)
 by#=PickedY()
 PositionEntity box,bx#,by#+0.5,bz#

 ;fps counter
 If MilliSecs()-settime>1000
  getfps=setfps : setfps=0 : settime=MilliSecs()
 Else
  setfps=setfps+1
 EndIf

 ;first-person view controls, from "cubedemo.bb" by Rob Cummings
 camx#=camx#+(MouseYSpeed()*0.15) ;mousey
 If camx#<-85 Then camx#=-85 ;limit mousey
 If camx#>85 Then camx#=85
 camy#=EntityYaw(campivot)-(MouseXSpeed()*0.15) ;mousex
 MoveMouse GraphicsWidth()/2,GraphicsHeight()/2
 RotateEntity campivot,camx#,camy#,0
 If MouseDown(1) Then zspd#=zspd#+0.05 ;left/right mouse, move speed
 If MouseDown(2) Then zspd#=zspd#-0.05
 xspd#=xspd#*0.8 : zspd#=zspd#*0.8 ;decay move speed
 yspd#=yspd#-0.02 : yspd#=yspd#*0.8 ;gravity and decay
 MoveEntity campivot,xspd#,0,zspd#
 TranslateEntity campivot,0,yspd#,0

 If KeyHit(17) Then wf=Not wf : WireFrame wf ;W key, wireframe mode

 DrawImage target,GraphicsWidth()/2,GraphicsHeight()/2

 Text 0,0,"Press W for wireframe, Left/Right mouse for move"
 Text 0,12,"FPS="+getfps+" Tris="+TrisRendered()

 Flip
Wend

;functions
Function UpdateCubemap(Cubetex,Cubecam,Viewcam)
 ;Render a cubemap with a given texture and camera

 Local xpos#,ypos#,zpos#,texsize

 CameraProjMode Viewcam,0 ;disable view camera
 CameraProjMode Cubecam,1 ;enable cubemap camera

 xpos#=EntityX(Viewcam,True) ;cubemap camera positions, global
 ypos#=-EntityY(Viewcam,True)
 zpos#=EntityZ(Viewcam,True)
 PositionEntity Cubecam,xpos#,ypos#,zpos# ;set position of render

 texsize=TextureWidth(Cubetex) ;assume width/height are the same

 RotateEntity Cubecam,0,90,0
 RenderWorld
 SetCubeFace Cubetex,0 ;left view, -x axis
 CopyRect 0,0,texsize,texsize,0,0,BackBuffer(),TextureBuffer(Cubetex)
 RotateEntity Cubecam,0,0,0
 RenderWorld
 SetCubeFace Cubetex,1 ;forward view, +z axis
 CopyRect 0,0,texsize,texsize,0,0,BackBuffer(),TextureBuffer(Cubetex)
 RotateEntity Cubecam,0,-90,0
 RenderWorld
 SetCubeFace Cubetex,2 ;right view, +x axis
 CopyRect 0,0,texsize,texsize,0,0,BackBuffer(),TextureBuffer(Cubetex)
 RotateEntity Cubecam,0,180,0
 RenderWorld
 SetCubeFace Cubetex,3 ;back view, -z axis
 CopyRect 0,0,texsize,texsize,0,0,BackBuffer(),TextureBuffer(Cubetex)
 If EntityY(Viewcam,True)>0 ;optimize cubemapping
  RotateEntity Cubecam,-90,0,0
  RenderWorld
  SetCubeFace Cubetex,4 ;up view, +y axis
  CopyRect 0,0,texsize,texsize,0,0,BackBuffer(),TextureBuffer(Cubetex)
 Else
  RotateEntity Cubecam,90,0,0
  RenderWorld
  SetCubeFace Cubetex,5 ;down view, -y axis
  CopyRect 0,0,texsize,texsize,0,0,BackBuffer(),TextureBuffer(Cubetex)
 EndIf

 CameraProjMode Cubecam,0 ;disable cubemap camera
 CameraProjMode Viewcam,1 ;enable view camera
 
End Function

Function CreateFlatMesh(Xlen#,Zlen#,Size#,Xpos#,Ypos#,Zpos#)
 ;Create a flat mesh grid of given dimensions and position
 ;Xlen#/Zlen#=mesh dimensions, Size#=quad size
 ;Xpos#/Ypos#/Zpos#=x/y/z center

 Local hmesh,hsurf,xnum,znum,ix,iz,ptx#,ptz#,iv

 hmesh=CreateMesh()
 hsurf=CreateSurface(hmesh)

 xnum=Xlen#/Size# ;number of vertices on axis
 znum=Zlen#/Size#

 ;create grid vertices, centered and offset
 For iz=0 To znum
  For ix=0 To xnum
   ptx#=(ix*Size#)-(xnum*Size#*0.5) ;ipos-midpos
   ptz#=(iz*Size#)-(znum*Size#*0.5)
   AddVertex(hsurf,ptx#+Xpos#,Ypos#,ptz#+Zpos#) ;pos+offset
  Next
 Next

 ;fill in quad triangles, created in "reverse z" order
 For iz=0 To znum-1
  For ix=0 To xnum-1
   iv=ix+(iz*(xnum+1)) ;iv=x+(z*x1)
   AddTriangle(hsurf,iv,iv+xnum+1,iv+xnum+2) ;0,x1,x2
   AddTriangle(hsurf,iv+xnum+2,iv+1,iv) ;x2,1,0
  Next
 Next

 UpdateNormals hmesh ;set normals, for cubemaps and lighting

 Return hmesh ;mesh handle

End Function

Function CreateBasinCube(Xlen#,Zlen#,Xmid#,Ymid#,Zmid#,Xpos#,Ypos#,Zpos#)
 ;Create a basin cube of given dimensions and position
 ;Xlen#/Zlen#=mesh dimensions, Xmid#/Ymid#/Zmid#=basin dimensions
 ;Xpos#/Ypos#/Zpos#=x/y/z center

 Local hmesh,hsurf,xnum,znum,i,ptx#,pty#,ptz#,ix,iz

 hmesh=CreateMesh()
 hsurf=CreateSurface(hmesh)

 ;create grid vertices, centered and offset
 For i=0 To 19
  If i=0 Or i=4 Or i=8 Or i=12 Then ptx#=(Xlen#/2)-Xlen# ;x
  If i=1 Or i=5 Or i=9 Or i=13 Or i=16 Or i=18 Then ptx#=(Xmid#/2)-Xmid#
  If i=2 Or i=6 Or i=10 Or i=14 Or i=17 Or i=19 Then ptx#=Xmid#-(Xmid#/2)
  If i=3 Or i=7 Or i=11 Or i=15 Then ptx#=Xlen#-(Xlen#/2)
  pty#=0 : If i>15 Then pty#=-Ymid# ;y
  If i=0 Or i=1 Or i=2 Or i=3 Then ptz#=(Zlen#/2)-Zlen# ;z
  If i=4 Or i=5 Or i=6 Or i=7 Or i=16 Or i=17 Then ptz#=(Zmid#/2)-Zmid#
  If i=8 Or i=9 Or i=10 Or i=11 Or i=18 Or i=19 Then ptz#=Zmid#-(Zmid#/2)
  If i=12 Or i=13 Or i=14 Or i=15 Then ptz#=Zlen#-(Zlen#/2)
  AddVertex(hsurf,ptx#+Xpos#,pty#+Ypos#,ptz#+Zpos#) ;pos+offset
 Next

 ;fill in quad triangles, created in "reverse z" order
 For iz=0 To 2
  For ix=0 To 2
   i=ix+(iz*4) ;i=x+(z*x1)
   If ix=1 And iz=1 ;basin quads
    AddTriangle(hsurf,16,18,19)
    AddTriangle(hsurf,19,17,16) ;top view
    AddTriangle(hsurf,5,16,17)  ;_|_|_
    AddTriangle(hsurf,17,6,5)   ; | | 
    AddTriangle(hsurf,9,10,18)  ;¯|¯|¯
    AddTriangle(hsurf,19,18,10) ;side view
    AddTriangle(hsurf,5,9,18)   ;_   _
    AddTriangle(hsurf,16,5,18)  ; |_|
    AddTriangle(hsurf,17,19,10)
    AddTriangle(hsurf,10,6,17)
   Else ;surrounding quads
    AddTriangle(hsurf,i,i+4,i+5) ;0,x1,x2
    AddTriangle(hsurf,i+5,i+1,i) ;x2,1,0
   EndIf
  Next
 Next

 ;add uv coordinates for texture, planar-mapped
 For i=0 To 19
  ptx#=(VertexX(hsurf,i)/Xlen#)*((Xlen#-Xmid#)/2)
  ptz#=(VertexZ(hsurf,i)/Zlen#)*((Zlen#-Zmid#)/2)
  VertexTexCoords hsurf,i,ptx#,ptz#,0,0
 Next

 Return hmesh ;mesh handle

End Function

Function CreateTargetImage()
 ;Create a target crosshair

 Local himg,hbuf

 himg=CreateImage(32,32)
 hbuf=GraphicsBuffer()

 SetBuffer ImageBuffer(himg)
 Cls : Color 16,16,16
 Line 16,0,16,31
 Line 0,16,31,16
 Color 255,255,255 ;reset
 SetBuffer hbuf

 Return himg ;image handle

End Function

Function CreateB3dLogoTexture()
 ;Create a version of the "b3dlogo.jpg"

 Local htex,hbuf,hfont

 htex=CreateTexture(256,128)
 hbuf=GraphicsBuffer()

 SetBuffer TextureBuffer(htex)
 ClsColor 255,255,255 : Cls
 hfont=LoadFont("arial",72,True) ;bold
 SetFont hfont
 Color 255,128,0 : Oval 166,6,82,82
 Color 255,255,255 : Oval 184,6,64,64
 Color 0,0,0 : Rect 6,51,211,64,True ;fill
 Color 255,255,255 : Text 6,50,"Blitz"
 Color 128,128,128 : Text 142,50,"3D"
 Color 64,64,128 : Text 148,50,"3D"
 FreeFont hfont
 ClsColor 0,0,0 : Color 255,255,255 ;reset
 SetBuffer hbuf

 Return htex ;texture handle

End Function

Function CreateSkyTexture(Xsize,Ysize)
 ;Create a sky sphere texture of given dimensions

 Local hpal,hmap,htex,ix,iy,ip

 hpal=CreatePalette(256,100,160,230,255,255,255) ;cyan gradient
 hmap=CreateHeightMap(hpal,Xsize,Ysize,8,2,1,16,2,0,1,48,0)
 htex=CreateTexture(Xsize,Ysize)

 ;set all heightmap points
 LockBuffer(TextureBuffer(htex))
 For iy=0 To Ysize-1
  For ix=0 To Xsize-1
   ip=PeekByte(hmap,ix+(iy*Xsize)) ;index=x+(y*width)
   WritePixelFast ix,iy,PeekInt(hpal,ip*4),TextureBuffer(htex)
  Next
 Next
 UnlockBuffer(TextureBuffer(htex))

 FreeBank hmap
 FreeBank hpal

 Return htex ;texture handle

End Function

Function CreateConcreteTexture(Xsize,Ysize)
 ;Create a concrete slab texture of given dimensions

 Local hmap,hpal,htex,ix,iy,ip

 hpal=CreatePalette(256,180,180,180,80,80,80) ;grey gradient
 hmap=CreateHeightMap(hpal,Xsize,Ysize,1,1,1,2,8,5,84,4,4)
 htex=CreateTexture(Xsize,Ysize)

 ;set all heightmap points
 LockBuffer(TextureBuffer(htex))
 For iy=0 To Ysize-1
  For ix=0 To Xsize-1
   ip=PeekByte(hmap,ix+(iy*Xsize)) ;index=x+(y*width)
   WritePixelFast ix,iy,PeekInt(hpal,ip*4),TextureBuffer(htex)
  Next
 Next
 UnlockBuffer(TextureBuffer(htex))

 FreeBank hmap
 FreeBank hpal

 Return htex ;texture handle

End Function

Function CreatePalette(ncol,rmin,gmin,bmin,rmax,gmax,bmax)
 ;Create a palette for a heightmap of given size and color range

 Local hpal,ic,red,green,blue

 hpal=CreateBank(ncol*4)
 For ic=0 To ncol-1
  red=(ic*(rmax-rmin)/ncol)+rmin
  green=(ic*(gmax-gmin)/ncol)+gmin
  blue=(ic*(bmax-bmin)/ncol)+bmin
  PokeInt hpal,ic*4,(red Shl 16)+(green Shl 8)+blue
 Next

 Return hpal ;palette bank handle

End Function

Function CreateHeightMap(hpal,Xdm,Ydm,Xps,Yps,Bps,Blr,Wpr,Mcv,Bcv,Bxs,Bys)
 ;From "lands.bas" by Per Larsson (www.programmersheaven.com)
 ;Xps/Yps/Bps=X/Y/Blur pixel step, Blr=blur (smoothing) amount,
 ;Wpr=water probability (not 0), Mcv=minimum color value,
 ;Bcv=border color value (0 for none), Bxs/Bys=border X/Y size

 Local hmap,hnewmap,ix,iy,ystep,xstep,val,ptx,pty,ib,lf,rt,up,dn

 SeedRnd MilliSecs() ;randomize seed
 hmap=CreateBank(Xdm*Ydm)
 hnewmap=CreateBank(Xdm*Ydm)

 ;make random 2-colors map
 For iy=0 To Ydm-1
  If ystep=0 ;instead of using Step, for variable steps
   For ix=0 To Xdm-1
    If xstep=0
     val=Rand(0,Wpr) ;water probability
     If val=1 : val=BankSize(hpal)-1 : Else : val=1 : EndIf ;set 2-colors
     If Bcv>0 ;draw border around map
      If ix<Bys Or ix>=Xdm-Bys Then val=Bcv
      If iy<Bxs Or iy>=Ydm-Bxs Then val=Bcv
     EndIf
     ;set heightmap points, and boxfill in-between points
     For ptx=0 To Xps-1
      For pty=0 To Yps-1
       PokeByte hmap,(ix+ptx)+((iy+pty)*Xdm),val ;calculate x,y offset
      Next
     Next
    EndIf
    xstep=xstep+1 : If xstep>=Xps Then xstep=0
   Next 
  EndIf
  ystep=ystep+1 : If ystep>=Yps Then ystep=0
 Next

 If Blr=0 Then CopyBank hmap,0,hnewmap,0,Xdm*Ydm ;copy 2-colors map

  ;blur smooth map by pixel steps, average out 2-colors map
  For ib=1 To Blr
   For iy=0 To Ydm-1
    If ystep=0
     For ix=0 To Xdm-1
      If xstep=0
       ;get surrounding points, and wrap overlapping points
       lf=ix-Bps : If lf<0 Then lf=Xdm-Bps
       rt=ix+Bps : If rt>Xdm-Bps Then rt=0
       up=iy-Bps : If up<0 Then up=Ydm-Bps
       dn=iy+Bps : If dn>Ydm-Bps Then dn=0
       ;calculate average of current point, blur
       ;color=(up+lf+rt+down+(pt*2)+lfup+rtup+lfdown+rtdown)/10
       val=PeekByte(hmap,ix+(up*Xdm))+PeekByte(hmap,lf+(iy*Xdm))
       val=val+PeekByte(hmap,rt+(iy*Xdm))+PeekByte(hmap,ix+(dn*Xdm))
       val=val+(PeekByte(hmap,ix+(iy*Xdm))*2)
       val=val+PeekByte(hmap,lf+(up*Xdm))+PeekByte(hmap,rt+(up*Xdm))
       val=val+PeekByte(hmap,lf+(dn*Xdm))+PeekByte(hmap,rt+(dn*Xdm))
       val=val/10
       If val<Mcv Then val=Mcv ;set minimum color
       If ib>1 Then PokeByte hmap,ix+(iy*Xdm),val ;set smoothed average
       ;Set actual heightmap points, in pixel steps
       For ptx=0 To Bps-1
        For pty=0 To Bps-1
         PokeByte hnewmap,(ix+ptx)+((iy+pty)*Xdm),val
        Next
       Next
      EndIf
      xstep=xstep+1 : If xstep>=Bps Then xstep=0
     Next
    EndIf
    ystep=ystep+1 : If ystep>=Bps Then ystep=0
   Next
  Next

 FreeBank hmap

 Return hnewmap ;heightmap bank handle

End Function
