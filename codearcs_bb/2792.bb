; ID: 2792
; Author: jfk EO-11110
; Date: 2010-12-06 11:36:38
; Title: Shadow Experiment
; Description: Encode XYZ in VertexColor

; Experimantal Shadow Technic: determine visibility by 2 ICU colormaps. VertexColor
; is used to encode 8 Bit 3D coordinates in each rendered pixel.
; all colors that are visible from within both renders  (eye +light perspectives)
; will be lit point in space.

; Heavy rounding error artefacts and sampling problems due to low resolution.

; hit space to see the color space renders etc.

; Note: since it is using Vertices, it may not work with animated B3D again! grrr!

; NOTE: userlibs required!  In kernel32.decls you need this:


;.lib "kernel32.dll" 
;RtlMoveMemory2%(Destination*,Source,Length) : "RtlMoveMemory"


;of course without semicolons




Graphics3D 800,600,32,2

SetBuffer BackBuffer()
WBuffer 1


AmbientLight 20,20,20


Global demo_mode
Global wire

Global nshades#=768
Global fogrange#=20.0 ; this will also be the max range of the projected light!
Global f_divider#=256.0/fogrange#

Global PhotoCamZoom#=1.0

Global look_w=128 ;512 ;128;256 ; texturemap width (attention: max=graphicswidth/icu2rel !) Note: needs to be in 4:3 ratio with look_h ! Needs to be power of 2, eg 64,128,256...
Global look_h=96  ;384 ;96 ;192
Global icu2rel=2 ;oversampling (1 to 4): render from light view will be N times bigger for higher precision (eg look_w=256 and icu2rel=2, render will be 512x384)
Global sshr=2; (0,1,2,3) additional bitshift oversampling (shr N)
Global blurmode=1 ;(0/1) initially define if blur by multi quad overlay (slow)
blur#=0.014


Dim icu1#(look_w,look_h,2)
Dim icu2#(look_w,look_h,2)
Dim PhotonCache(look_w,look_h)
Global RGB_2_XYZ=CreateBank(256*256*256)

;create screen overlay quad
Global ol_quad=CreateMesh()
surf=CreateSurface(ol_quad)
v0=AddVertex(surf,-1,-1,0)
VertexTexCoords(surf,v0,0,   1   ,0 ,0)
v1=AddVertex(surf, 1,-1,0)
VertexTexCoords(surf,v1,1   ,1   ,0 ,0)
v2=AddVertex(surf, 1, 1,0)
VertexTexCoords(surf,v2,1   ,0   ,0 ,0)
v3=AddVertex(surf,-1, 1,0)
VertexTexCoords(surf,v3,0   ,0   ,0 ,0)

tr=AddTriangle(surf,v0,v1,v2)
tr=AddTriangle(surf,v0,v2,v3)

Global ol_quadtex=CreateTexture(look_w,look_w,0)
Global ol_quadtex_yo=Floor(look_w-look_h)/2.0
TextureBlend ol_quadtex,2
EntityFX ol_quad,17
EntityBlend ol_quad,1  ;3;3
EntityTexture ol_quad,ol_quadtex,0,0

q_al#=0.1 ; alpha of shadow overlay
If blurmode=0 Then Goto noblur
q_al#=0.01

TranslateEntity ol_quad,-blur#,-blur#,0
EntityAlpha ol_quad,q_al#

q2=CopyEntity(ol_quad,ol_quad)
TranslateEntity q2,blur*2.0,0,0

q3=CopyEntity(ol_quad,ol_quad)
TranslateEntity q3,blur*2.0,blur*2.0,0

q4=CopyEntity(ol_quad,ol_quad)
TranslateEntity q4,0,blur*2.0,0

EntityAlpha q2,q_al#
EntityAlpha q3,q_al#
EntityAlpha q4,q_al#

.noblur


EntityAlpha ol_quad,q_al#

HideEntity ol_quad





; simple scene...
Global camera=CreateCamera()
CameraRange camera,0.5,100
EntityParent ol_quad,camera
TranslateEntity ol_quad,0,0,1.0
Global scene_center=CreatePivot()
maincam_targetpiv=CreatePivot()


light=CreateLight(3) ; position and angle of this light will be used for the light mesh projection
test_range#=16.7
LightRange light,test_range# ; actually this may be very low, we don't want it to enlight things in the shade
PositionEntity light,0,-2,7 ; note, this important position for the system
PointEntity light,scene_center

Global light_cube=CreateCone(12,1,light)
RotateMesh light_cube,-90,0,0
ScaleEntity light_cube,0.2,0.2,0.1
EntityFX light_cube,1
;EntityAlpha light_cube,0.3
EntityColor light_cube,255,255,0



Global PhotonCamPiv=CreatePivot()
Global PhotonCam=CreateCamera(PhotonCamPiv)
CameraProjMode PhotonCam,0
Global helper=CreatePivot()


; simple test textures
Global walltex=CreateTexture(256,256)
Color 120,120,80
Rect 0,0,256,256,1
For i=0 To 20000
 r=Rand(10,40)
 Color 120+r,120+r,80+r
 rx=Rand(256)
 ry=Rand(256)
 Plot rx,ry

 r=-r
 If ry0 Then r=0
 Color 120+r,120+r,80+r
 Plot rx,ry-1
 Plot rx-1,ry-1
 Plot rx+1,ry-1
Next

For j=0 To 255 Step 32
 For i=0 To 300 Step 64
  jo=-(Floor(j/32) And 1)*32
  Color 40,40,20
  Rect jo+i,j+1,63,31,0
  Color 180,180,150
  Rect jo+i+1,j,63,31,0
 Next
Next

CopyRect 0,0,256,256,0,0,BackBuffer(),TextureBuffer(walltex)

Global white =CreateTexture(16,16)
Color 255,255,255
Rect 0,0,16,16,1
CopyRect 0,0,16,16,0,0,BackBuffer(),TextureBuffer(white)



CameraProjMode camera,1
CameraProjMode PhotonCam,0


; world design...
; init entity iteration------------------------
Global Cycle_bank=CreateBank(16)
Const  Cycle_NextEntity=4
Const  Cycle_LastEntity=8
Global Cycle_FirstEntity=CreatePivot()
Global Cycle_CurrentEntityPointer=Cycle_FirstEntity
;----------------------------------------------------

; from here on, created entities can be parsed with MoreEntities function.
dis#=10
For i=0 To 30
 c=CreateCube()
 PositionEntity c,Rnd(-10,10),Rnd(-6,-4),Rnd(-10,10)
 RotateEntity c,0,Rand(360),0
 ScaleEntity c,3,1,3
 EntityPickMode c,2
 EntityTexture c,walltex
Next

w1=CreateCube() ;walls
ScaleEntity w1,10,10,1
TranslateEntity w1,0,0,10
EntityTexture w1,walltex

w2=CreateCube()
ScaleEntity w2,10,10,1
TranslateEntity w2,0,0,-10
EntityTexture w2,walltex

w3=CreateCube()
ScaleEntity w3,1,10,10
TranslateEntity w3,10,0,0
EntityTexture w3,walltex

w4=CreateCube()
ScaleEntity w4,1,10,10
TranslateEntity w4,-10,0,0
EntityTexture w4,walltex

w5=CreateCube() ; ceil
ScaleEntity w5,10,1,10
TranslateEntity w5,0,10,0
EntityTexture w5,walltex

col=CreateCylinder()
ScaleEntity col,0.5,10,0.5
TranslateEntity col,-5,0,-5
EntityTexture col,walltex

col=CreateCylinder()
ScaleEntity col,0.5,10,0.5
TranslateEntity col,0,0,-5
EntityTexture col,walltex


npc=CreateSphere(20)
EntityTexture npc,walltex
ScaleEntity npc,2,5,2


EntityFX npc,1

npc_x#=-5



Global Cycle_EndMarker=CreatePivot()




Type e_vert
Field x#
Field y#
Field z#
Field depth#
Field ent
Field sur
Field ind
Field red#
Field green#
Field blue#
Field alpha#
End Type



Type rem_mat
 Field index
End Type






ft=MilliSecs()
MoveMouse GraphicsWidth()/1.8,GraphicsHeight()/2.0

Color 255,0,0
a#=50 
While KeyHit(1)=0 ; MMMMMMMMMMMMMMMMMMMMMMMMMMMMM MAINLOOP MMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
 npc_x#=npc_x+.1
 If npc_x>12 Then npc_x=-12
 PositionEntity npc,npc_x,0,0
 TurnEntity npc,1,2,3
 If KeyDown(200)
  test_range#=test_range#*1.1
  If test_range#>20 Then test_range#=20
  LightRange light, test_range#
 EndIf
 If KeyDown(208)
  test_range#=test_range#*0.9
  If test_range#<0.1 Then test_range#=0.1
  LightRange light, test_range#
 EndIf

;  a#=a#+2
 If KeyDown(203)
  a#=a#+2
 EndIf
 If KeyDown(205)
  a#=a#-2
 EndIf
 If KeyHit(17) Then 
  wire =wire Xor 1
  If wire=0 Then WireFrame 0
 EndIf
 If KeyHit(57) Then
  demo_mode=demo_mode+1
  If demo_mode>3 Then demo_mode=0
 EndIf
; PositionEntity light,( (GraphicsWidth()/2)-MouseX() )/40.0,((GraphicsHeight()/2)-MouseY())/40.0,7 ; note, this important position for the system
 PositionEntity light, Sin( ((GraphicsWidth()/2)-MouseX())/2 )*7.5, ((GraphicsHeight()/2)-MouseY())/40.0, Cos(((GraphicsWidth()/2)-MouseX())/2 )*7.5 ; note, this important position for the system
 PointEntity light,scene_center
 If a#>359 Then a#=0
 If a#<0 Then a#=359
 PositionEntity camera,Sin(a#)*dis,0,Cos(a#)*dis
 PointEntity camera,maincam_targetpiv

 UpdateWorld()
 LetThereBeLight(light)

 If wire<>0 Then Wireframe 1
 RenderWorld()
 If wire<>0 Then WireFrame 0


 Color 255,0,0
 Text 250,0,"Tris:"+TrisRendered()
 Text 150,0,"FPS:"+1000/ft
 Text 350,0,"left,right, up, down,w,space,mouse"
 Text 150,16,"DXlight range:"+test_range+" Eye :"+a
 If demo_mode=0 Then Flip 0; to hide the color space maps
 ms=MilliSecs()
 ft=ms-ms2
 ms2=ms
Wend

End
;-----------------------------------------------------------------------










Function LetThereBeLight(l)

;erase some arrays
 For y=0 To look_h-1
  For x=0 To look_w-1
    PhotonCache(x,y)=0
  Next
 Next

 HideEntity light_cube
 CameraProjMode camera,0
 CameraProjMode PhotonCam,1
 CameraFogMode PhotonCam,0
 CameraFogRange PhotonCam,0,fogrange#*2.0
 CameraViewport(PhotonCam,0,0,look_w,look_h)
 CameraZoom PhotonCam,PhotoCamZoom#
 PositionEntity PhotonCam,EntityX(camera,1),EntityY(camera,1),EntityZ(camera,1),1
 RotateEntity PhotonCam,EntityPitch(camera,1),EntityYaw(camera,1),EntityRoll(camera,1),1
 CameraRange PhotonCam,0.01,100
 HideEntity ol_quad


; coloring world (RGB-encode vertices locations)
 While MoreEntities()
  e=NextEntity()
  If EntityClass$(e)="Mesh"
   For su=1 To CountSurfaces(e)
    surf=GetSurface(e,su)
    For v=0 To CountVertices(surf)-1
     enti.e_vert = New e_vert
        enti\ent=e
        enti\sur=surf
        enti\ind=v
        enti\red#=VertexRed(surf,v)
        enti\green#=VertexGreen(surf,v)
        enti\blue#=VertexBlue(surf,v)
        enti\alpha#=VertexAlpha#(surf,v)

       TFormPoint VertexX(surf,v),VertexY(surf,v),VertexZ(surf,v),e,0
       dis__x#=TFormedX()+10.0
       dis__y#=TFormedY()+10.0
       dis__z#=TFormedZ()+10.0

       bri__x#=255.0-(255.0 / fogrange#) * dis__x#
       If bri__x#<0 Then bri__x#=0
       If bri__x#>255 Then bri__x#=255

       bri__y#=255.0-(255.0 / fogrange#) * dis__y#
       If bri__y#<0 Then bri__y#=0
       If bri__y#>255 Then bri__y#=255

       bri__z#=255.0-(255.0 / fogrange#) * dis__z#
       If bri__z#<0 Then bri__z#=0
       If bri__z#>255 Then bri__z#=255
       VertexColor surf, v, bri__x#, bri__y#, bri__z#,1.0 
    Next
   Next

   EntityFX e,2 Or 1;4
   EntityColor e,255,255,255
   EntityTexture e,white
  EndIf
 Wend


; render color space from player perspective
 RenderWorld()

 LockBuffer BackBuffer()
 For y=0 To look_h-1
  For x=0 To look_w-1
   rgb=ReadPixelFast(x,y) And $ffffff
   ; store texel world coords (rgb encoded)
   icu1#(x,y,0)=(255-(rgb And $FF0000) Shr 16) 
   icu1#(x,y,1)=(255-(rgb And $FF00) Shr 8) 
   icu1#(x,y,2)=(255-rgb And $FF) ;/f_divider#
  Next
 Next
 UnlockBuffer BackBuffer()

 If demo_mode=1 Then  Flip 0 ; too see color space from eye



;render color space from lights point of view

 PositionEntity PhotonCam,EntityX(l),EntityY(l),EntityZ(l),1
 RotateEntity PhotonCam,EntityPitch(l),EntityYaw(l),EntityRoll(l),1
 CameraViewport(PhotonCam,0,0,look_w*icu2rel,look_h*icu2rel) ; allow higher precision

 RenderWorld()

 LockBuffer BackBuffer()
 For y=0 To look_h*icu2rel-1
  For x=0 To look_w*icu2rel-1
   x2=x/icu2rel
   y2=y/icu2rel
   rgb=ReadPixelFast(x,y) And $ffffff
   icu2#(x2,y2,0)=(255-(rgb And $FF0000) Shr 16) 
   icu2#(x2,y2,1)=(255-(rgb And $FF00) Shr 8) 
   icu2#(x2,y2,2)=(255-rgb And $FF) 
   ; store flag in rgb encoded array index (reverse lookup table), and allow oversampling by bitshifting
    rr.rem_mat = New rem_mat ; will have to erase this matrix point later, so store its bank index
    rr\index=((icu2#(x2,y2,0)Shr sshr)Shl 16)Or((icu2#(x2,y2,1)Shr sshr)Shl 8)Or((icu2#(x2,y2,2)Shr sshr))
    PokeByte RGB_2_XYZ,rr\index ,1
  Next
 Next
 UnlockBuffer BackBuffer()


 CameraViewport(PhotonCam,0,0,look_w,look_h) 
 PositionEntity PhotonCam,EntityX(camera,1),EntityY(camera,1),EntityZ(camera,1),1
 RotateEntity PhotonCam,EntityPitch(camera,1),EntityYaw(camera,1),EntityRoll(camera,1),1

; The following is calculating the visability of each shadow overlay texel
; set a shadow texels only if it was seen on both color space renders (point that can be seen from the eye and from the light as well)

 For y=0 To look_h-1
  For x=0 To look_w-1
   p=PeekByte( RGB_2_XYZ, ((icu1#(x,y,0)Shr sshr)Shl 16)+((icu1#(x,y,1)Shr sshr)Shl 8)+((icu1#(x,y,2)Shr sshr))  )
   If p=1
    CameraProject PhotonCam, icu1#(x,y,0)/f_divider#-10.0,icu1#(x,y,1)/f_divider#-10.0,icu1#(x,y,2)/f_divider# -10.0
    xx=ProjectedX#()
    yy=ProjectedY#()
    If (xx>=0) And (yy>=0) And (xx<look_w) And (yy<look_h) Then
     PhotonCache(xx,yy)=1 ; will later write this pixel
    EndIf
   EndIf
  Next
 Next



; clean up matrix changes
 For rr.rem_mat = Each rem_mat
  PokeByte RGB_2_XYZ,rr\index,0
 Next
 For rr.rem_mat = Each rem_mat
  Delete rr
 Next




;; artefacts filter attempt....fill single isolated shadow texels
; For y=1 To look_h-2
;  For x=1 To look_w-2
;   If (PhotonCache(x,y)=0) And (PhotonCache(x-1,y)=1) And (PhotonCache(x,y-1)=1) And (PhotonCache(x+1,y)=1) And (PhotonCache(x,y+1)=1) Then
;    PhotonCache(x,y)=1
;   EndIf
;  Next
; Next


If demo_mode=2 Then Flip 0; to see color space from light



; finally write the texels to the screen
 Color 0,0,0
 Rect 0,0,look_w,look_h,1

 LockBuffer()
 For y=0 To look_h-1
  For x=0 To look_w-1
   If PhotonCache(x,y)<>0 Then
    WritePixelFast x,y,$ffffff
   EndIf
  Next
 Next
 UnlockBuffer()
 CopyRect 0,0,look_w,look_h,0,ol_quadtex_yo,BackBuffer(), TextureBuffer(ol_quadtex)



 For enti.e_vert = Each e_vert ; restore original vertex colors of scene
     VertexColor enti\sur,enti\ind,enti\red#,enti\green#,enti\blue#,enti\alpha#
 Next
 For enti.e_vert = Each e_vert ; free types
    Delete enti
 Next

;


If demo_mode=3 Then Flip 0; to see shadow mapping

 While MoreEntities()
  ; this is where we set the scene objects back to their original state, FX etc. wise.
  ; (in this demo the orig settings are not known, there for just zeroing FX)
  e= NextEntity()
  If EntityClass$(e)="Mesh"
   EntityFX e,0
   EntityColor e,255,255,255
   EntityTexture e,walltex
  EndIf
 Wend

 CameraProjMode camera,1
 CameraProjMode PhotonCam,0
 ShowEntity ol_quad
 ShowEntity light_cube

End Function




; NOTE: userlibs required!  In kernel32.decls you need this:


;.lib "kernel32.dll" 
;RtlMoveMemory2%(Destination*,Source,Length) : "RtlMoveMemory"


;of course without semicolons






Function MoreEntities() ; iterate all world content entities
 RtlMoveMemory2(Cycle_bank,Cycle_CurrentEntityPointer+Cycle_NextEntity,4)
 If PeekInt(Cycle_bank,0)<>Cycle_EndMarker
  Return True
 Else
  Cycle_CurrentEntityPointer=Cycle_FirstEntity
 EndIf
End Function


Function NextEntity()
 Local entity
 RtlMoveMemory2(Cycle_bank,Cycle_CurrentEntityPointer+Cycle_NextEntity,4)
 entity=PeekInt(Cycle_bank,0)
 Cycle_CurrentEntityPointer = entity
 Return entity
End Function


Function EntityExists(entity)
 While MoreEntities()
  If NextEntity()=entity Then Return True
 Wend
End Function
