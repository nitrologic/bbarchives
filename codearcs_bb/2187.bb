; ID: 2187
; Author: Cygnus
; Date: 2008-01-16 11:45:06
; Title: Test if cubemaps are *truly* supported.
; Description: GFXDriverCaps3D doesn't always give the correct result. This fixes it.

;TestCubeMap function example (fixed by markcw)

;setup scene
Graphics3D 320,240,0,2
SetBuffer BackBuffer()
camera=CreateCamera()
light=CreateLight()
RotateEntity light,90,0,0

;sky sphere
sky=CreateSphere(16)
ScaleEntity sky,500,500,500
FlipMesh sky
EntityFX sky,1
skytex=CreateSkyTexture(256,256)
EntityTexture sky,skytex,0,0

;cubemapped cube
cube=CreateCube()
PositionEntity cube,0,0,5
tex=CreateTexture(32,32,1+128+256)
For face=0 To 5
 If face=0 Then RotateEntity camera,0,90,0 ;left
 If face=1 Then RotateEntity camera,0,0,0 ;front
 If face=2 Then RotateEntity camera,0,-90,0 ;right
 If face=3 Then RotateEntity camera,0,180,0 ;back
 If face=4 Then RotateEntity camera,-90,0,0 ;up
 If face=5 Then RotateEntity camera,90,0,0 ;down
 SetCubeFace tex,face
 RenderWorld
 CopyRect 0,0,32,32,0,0,BackBuffer(),TextureBuffer(tex)
Next
RotateEntity camera,0,0,0
EntityTexture cube,tex

cubemapsupport=TestCubeMap(camera)
caps=GfxDriverCaps3D()

;main loop
While Not KeyDown(1)

 pitch#=0 : yaw#=0
 If KeyDown(208) Then pitch#=-1 
 If KeyDown(200) Then pitch#=1
 If KeyDown(203) Then yaw#=-1
 If KeyDown(205) Then yaw#=1
 TurnEntity cube,pitch#,yaw#,0

 RenderWorld

 Text 0,0,"cubemapsupport="+cubemapsupport
 Text 0,12,"caps="+caps

 Flip
Wend

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

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;ACTUAL FUNCTION
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

Function TestCubeMap(maincamera)
 ;This function tests if a card truely does support cubemapping.
 ;Unfortunately some cards report that they do when they in fact do not.
 ;This results in garbled output. To test properly, we render some
 ;cubemaps and check if the texture matches what was rendered to the BackBuffer.
 ;Function by Damien Sturdy AKA Cygnus, Modified by MarkCW for accuracy.
 ;returns true or false. true if the test succeeded.

 If GfxDriverCaps3D()<>110 Then Return 0
 Local camera,tex,texsize=32
 Local clr[5],badpixels,tolerance=10
 Local ok=1,x,y,face,p1,p2
 camera=CreateCamera()
 CameraProjMode maincamera,0
 CameraViewport camera,0,0,texsize,texsize
 tex=CreateTexture(texsize,texsize,1+128+256) 
 For face=0 To 5
  If face=0 Then RotateEntity camera,0,90,0 ;left
  If face=1 Then RotateEntity camera,0,0,0 ;front
  If face=2 Then RotateEntity camera,0,-90,0 ;right
  If face=3 Then RotateEntity camera,0,180,0 ;back
  If face=4 Then RotateEntity camera,-90,0,0 ;up
  If face=5 Then RotateEntity camera,90,0,0 ;down
  SetCubeFace tex,face
  RenderWorld
  CopyRect 0,0,texsize,texsize,0,0,BackBuffer(),TextureBuffer(tex)
  For x=1 To texsize-2
   For y=1 To texsize-2
    p1=ReadPixel(x,y,BackBuffer()) And $FFFFFF
    p2=ReadPixel(x,y,TextureBuffer(tex)) And $FFFFFF
    clr[0]=((p1 And $FF0000)/$FF00)/16
    clr[3]=((p2 And $FF0000)/$FF00)/16
    clr[1]=((p1 And $00FF00)/$FF)/16
    clr[4]=((p2 And $00FF00)/$FF)/16
    clr[2]=(p1 And $0000FF)/16
    clr[5]=(p2 And $0000FF)/16
    If clr[0]<>clr[3] And clr[1]<>clr[4] And clr[2]<>clr[5]
     badpixels=badpixels+1
    EndIf
   Next
  Next
 ;Print " " : Delay(500)
 Next
 If badpixels<(((6*texsize*texsize)/100)*tolerance) Then ok=1
 CameraProjMode maincamera,1
 FreeEntity camera
 FreeTexture tex
 Return ok

End Function
