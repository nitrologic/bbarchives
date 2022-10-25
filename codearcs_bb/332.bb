; ID: 332
; Author: jfk EO-11110
; Date: 2002-05-29 02:22:15
; Title: Realtime Shadow
; Description: Pretty fast thanks Peter Scheutz's Turbo Hack

; One more Realtime Shadow by norc
; hack added by Peter Scheutz
width=640
height=480
Graphics3D width,height,16,2
SetBuffer BackBuffer()

flo=CreateCube()
ScaleEntity flo,120,1,120

flotex=CreateTexture(128,128)
SetBuffer TextureBuffer(flotex)
For i=0 To 63
    Color ((i*10)And $ff)*.8,(i*3)*.8,((i*20)And $ff)*.8
    Rect 64-i,64-i,i+i,i+i,0
Next
SetBuffer BackBuffer()
ScaleTexture flotex,0.25,0.25

cube=CreateCube()
ScaleEntity cube,30,5,50
TranslateEntity cube,-35,90,-15
EntityColor cube,255,0,0

Dim cubef(1000)
Dim cubecolor(1000,3)
canz=50

For i=0 To canz
    cubef(i)=CreateCube()
    ScaleEntity cubef(i),2,2,5
    TranslateEntity cubef(i),Rand(-70,70),90+Rand(-20,20),Rand(-70,70)

    cubecolor(i,0)=255
    cubecolor(i,1)=Rnd(255)
    cubecolor(i,2)=0

    EntityColor cubef(i),cubecolor(i,0),cubecolor(i,1),cubecolor(i,2)
    RotateEntity cubef(i),Rand(360),Rand(360),Rand(360)
Next

cube2=CreateCube()
ScaleEntity cube2,10,10,50
TranslateEntity cube2,35,90,15
EntityColor cube2,0,255,0

sphere=CreateSphere(3)
ScaleEntity sphere,10,10,10
TranslateEntity sphere,50,50,50
EntityColor sphere,255,0,255

camera=CreateCamera()
PositionEntity camera,0,100,200
PointEntity camera,flo

light=CreateLight(2)
PositionEntity light,0,500,0

mng=128
mng2=Int(mng/2)
halfwidth=width/2
halfheight=height/2
sp_s=0
sp_e=127
grabx=halfwidth-mng2
graby=halfheight-mng2
Const stpx=1
Const stpz=1

; the lightmap texture - it tries to use flag 256, some cards don't like it, but usually it gives a 400% speed boost
limat=CreateTexture(mng,mng,256)

HWMultiTex 0 ; might be faster if you turn this on ; that's for my machine
EntityTexture flo,flotex,0,0
EntityTexture flo,limat,0,1
EntityBlend flo,1

shadowColor=100

While KeyDown(1)=0
    ;-----------------------------------------------------------------
    ; Produce a Lightmap
    EntityAlpha flo,0
    PositionEntity camera,0,180,0 ; Play with this Y-Value
    RotateEntity camera,90,0,0
    CameraViewport camera,grabx,graby,mng,mng
    CameraZoom camera,1.2         ; and play with this Zoom-Value
    CameraClsColor camera ,255,255,255

    For i=0 To canz
        EntityColor cubef(i),shadowColor,shadowColor,shadowColor
        EntityFX cubef(i),1
    Next
    EntityColor sphere,shadowColor,shadowColor,shadowColor
    EntityColor cube,shadowColor,shadowColor,shadowColor
    EntityColor cube2,shadowColor,shadowColor,shadowColor
    EntityFX sphere,1
    EntityFX cube,1
    EntityFX cube2,1
   
    RenderWorld()

    For i=0 To canz
        EntityColor cubef(i),cubecolor(i,0),cubecolor(i,1),cubecolor(i,2)
        EntityFX cubef(i),0
    Next

    EntityColor sphere,255,0,255
    EntityColor cube,255,0,0
    EntityColor cube2,0,255,0
    EntityFX sphere,0
    EntityFX cube,0
    EntityFX cube2,0

    CameraZoom camera,1.0
    EntityAlpha flo,1.0
   
    CopyRect grabx,graby,128,128,0,0,BackBuffer(),TextureBuffer(limat)

    ;-----------------------------------------------------------------
    ; Render the Scene   
    CameraClsColor camera ,0,0,0

    CameraViewport camera,0,0,width,height
    camrot#=camrot#+1.0
    If camrot#>360 Then camrot#=camrot#-360
    camrot2#=camrot2#+3.0
    If camrot2#>360 Then camrot2#=camrot2#-360
    PositionEntity sphere,Cos(camrot2#)*80,50,Sin(camrot2#)*60
    RotateEntity cube,camrot#,camrot#,camrot#
    RotateEntity cube2,360-camrot2#,camrot#,15

    PositionEntity camera,Cos(camrot#)*200,50+MouseY()/3,Sin(camrot#)*200
    PointEntity camera,flo

    UpdateWorld()
    RenderWorld()

    tt=MilliSecs()
    fps#=1000/(tt-ttold)
    ttold=tt
    Text 10,10,"FPS: "+Int(fps#)
    Flip False
Wend
End
