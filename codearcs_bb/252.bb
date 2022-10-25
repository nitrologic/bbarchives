; ID: 252
; Author: jfk EO-11110
; Date: 2002-02-28 02:45:05
; Title: Underwater Scene
; Description: Plasmabased Water with Waveshade-Projection on Seaground

; Plasmabased Water with Seaground-Projection
; by CSP 2002
;
Graphics3D 640,480,16,1

SetBuffer BackBuffer()
AmbientLight 90,90,100 

; Camera
camera=CreateCamera()
CameraRange camera,0.1,17000
CameraFogMode camera,1
CameraFogRange camera,400,2500
CameraFogColor camera,150,170,200
CameraClsMode camera,1,1
CameraClsColor camera,150,170,200

light=CreateLight()
RotateEntity light,0,0,45
PositionEntity light,40,300,45

; init Cam Position
Kx#=3528.0
Ky#=-120.0
Kz#=6069.0


PositionEntity camera,Kx#,Ky#,Kz#
myangel#=110;70
RotateEntity camera,0,myangel,0

EntityType camera,TYPE_PLAYER
EntityRadius camera,3.4;3.4


; seafloor terrain
If FileType("seahmap1.bmp")=1       ; if File exist then
 ground=LoadTerrain("seahmap1.bmp") ; use this 128*128 heightmap !!!
Else
 ground=CreateTerrain(128)          ; else create one on the fly
 For i=0 To 127 
  For j=0 To 127
   If (i And $f)=8
    ModifyTerrain ground,(i),(j),Rnd(0.7,0.9)
   Else
    ModifyTerrain ground,(i),(j),Rnd(0.2)
   EndIf
  Next
 Next
EndIf

; terrain noise
For i=0 To 127 
 For j=0 To 127
  tlo#=(TerrainHeight(ground,i,j)+Rnd(-0.1,0.1))/2
  If tlo#<0 Then tlo#=0
  If tlo#>0.62 Then tlo#=0.65
  ModifyTerrain ground,(i),(j),tlo#
 Next
Next

; terrain border
h059#=0.65
For i=0 To 128 
 ModifyTerrain ground,i,0,h059#
 ModifyTerrain ground,i,127,h059#
 ModifyTerrain ground,0,i,h059#
 ModifyTerrain ground,127,i,h059#
Next
TerrainShading ground,1  


ScaleEntity ground,125,1000,125
PositionEntity ground,-8000,0,-6000
TranslateEntity ground,0,-650,0

; init Water etc.
Dim cosinus(640)
position = 0
For c = 0 To 640
 cosinus(c) = Cos((115*3.14159265358 * c) / 320) * 32 + 32
Next 
Dim div10(320)
For i=0 To 320
 div10(i)=i/10
Next
Dim f3(255),f45(255),f5(255)
For i=0 To 255
 f3(i)=3*i
 f45(i)=4.5*i
 f5(i)=5*i
Next
mywater=CreateTexture(32,32,9) ; used 4 texture on water
mywater2=CreateTexture(32,32,9) ; used 4 watershade blend on ground
Gosub water
water=CreateTerrain(1)
ScaleTexture mywater,0.01,0.01
ScaleTexture mywater2,2,2
EntityTexture water, mywater,0
ScaleEntity water,500*32,500*32,500*32
PositionEntity water,-8000,0,-6000
EntityAlpha water,0.6
EntityFX water,17

; init sky
sky=CreateSphere()
FlipMesh sky
If FileType("sky.bmp")=1 Then
 sky_tex=LoadTexture("sky.bmp"); use a seamless sky-texture
Else
 sky_tex=CreateTexture(256,256)
 SetBuffer TextureBuffer(sky_tex)
 Color 100,175,255
 Rect 0,0,640,480,1
 SetBuffer BackBuffer()
EndIf
EntityTexture sky,sky_tex
ScaleTexture sky_tex,0.2,0.2
ScaleEntity sky,6000,12000,6000
EntityFX sky,9 
EntityOrder sky,1
PositionEntity sky,Kx#,Ky#,Kz#

; cube around camera for additional 'fog'
trueb=CreateCube()
FlipMesh trueb
ScaleEntity trueb,10,10,10
EntityTexture trueb,mywater2
EntityAlpha trueb,0.6 
EntityFX trueb,1

; blended Ground-Texture
If FileType("rockground.jpg")=1
 groundtex=LoadTexture("rockground.jpg") ; use a 512*512 Texture (alter Scaling if smaller)
Else
 groundtex=CreateTexture(512,512)
 SetBuffer TextureBuffer(groundtex)
 Color 255,220,100
 Rect 0,0,512,512,1
 For i=0 To 1000
 patt=Rand(80,255)
  Color patt,patt*0.6,patt*0.3
  Line Rand(512),Rand(512),Rand(512),Rand(512)
 Next
 SetBuffer BackBuffer()
EndIf
ScaleTexture groundtex,10,10 ; relatice to Texture-Size
TextureBlend groundtex,3
EntityTexture ground,groundtex,0,0
EntityTexture ground,mywater2,0,1 ; projecting waves onto Floor

; init fps-counter
tt=MilliSecs()
tt2=tt-50
HidePointer


; ______________________________________MAINLOOP_____________________________________
While KeyDown(1)=0

 Gosub water ; update waves

 ; Cam Nav
 my=MouseYSpeed()*5
 If KeyDown(200)=1
  If speed#<50
   speed#=speed#+2
  EndIf 
 EndIf
 If speed#>0 
  speed#=speed#-1
  If speed#<0 
   speed#=0
  EndIf
 EndIf
 Kx#=kx#-Cos(myangel#-90)*speed#
 Kz#=kz#-Sin(myangel#-90)*speed#
 camy#=camy#-my
 If camy#>70 Then camy#=70
 If camy#<-500 Then camy#=-500
 terry#=TerrainY(ground,kx#,0,kz#)+20
 If camy#<terry# Then camy#=terry# 
 PositionEntity camera,Kx#,camy#,Kz#
 moro#=MouseXSpeed()
 myangel2#=myangel2#-moro#
 If myangel2#<>myangel#
  myangel#=myangel#+((myangel2#-myangel#)/5)
 EndIf
 RotateEntity camera,0,myangel#,0
 trueby#=camy#
 If trueby#>-10 Then trueby#=-10
 PositionEntity trueb,Kx#,trueby#,Kz#
 RotateEntity trueb,0,myangel#,0
 PositionEntity sky,Kx#,0,Kz#
 MoveMouse 320,240

 UpdateWorld()
 RenderWorld()

 tt=MilliSecs()
 fps=1000.0/(tt-tt2)
 tt2=tt
 Text 0,0,fps

 Flip

Wend
; ------------------------------------- eo mainloop -------------------------------------

End

.water
 wave1% = wave1% + 4
 wave2% = wave2% + 2
; use this if the waves are moving to fast:
; wave1% = wave1% + 2
; wave2% = wave2% + 1
 If wave1% >= 320 Then wave1% = 0 
 If wave2% >= 320 Then wave2% = 0
 SetBuffer TextureBuffer(mywater)
 LockBuffer
   For y% = 0 To 319 Step 10
    y10=div10(y)
    d = cosinus(y + wave2) + cosinus(Y + wave2)
    For x% = 0 To 319 Step 10
     x10=div10(x)
     f = 1 + Abs(((cosinus(x + wave1) + cosinus(x + y) + d) / 8) - 16)
     rr=105-f3(f) 
     If rr<0 Then rr=0
     gg=185-f45(f)
     If gg<0 Then gg=0
     bb=195-f5(f) 
     If bb<0 Then bb=0
    WritePixelFast x10,y10,((rr Shl 16) Or (gg Shl 8) Or bb)
   Next 
  Next
 UnlockBuffer
CopyRect 0,0,32,32,0,0, TextureBuffer(mywater), TextureBuffer(mywater2)
SetBuffer BackBuffer()
Return
