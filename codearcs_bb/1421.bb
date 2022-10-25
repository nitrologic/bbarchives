; ID: 1421
; Author: perseus
; Date: 2005-07-14 15:21:36
; Title: 3d Glass - Red Blue
; Description: the source of having 3d solution using red blue filtering

; Red Blue 3d Glasses
; 2005 Burcak Duruoz

Global red,blue,redt,bluet,black
Dim cb(8),tt(8)

Graphics3D 800,600,32,2
SetBuffer BackBuffer()
AmbientLight 0,0,0

global cam=CreateCamera()
PositionEntity cam,0,2,-12
RotateEntity cam,2,0,0

light=CreateLight(2)
PositionEntity light,10,10,-20
LightColor light,2,2,2

spht=CreateTexture(256,256)
SetBuffer TextureBuffer(spht)
For ttx=0 To 255 Step 8
    For tty=0 To 255 Step 16
        Color 255,255,255
        Rect ttx,tty,3,3
    Next
Next
SetBuffer BackBuffer()

sph=CreateSphere()
ScaleEntity sph,15,15,15
FlipMesh sph
EntityFX sph,1
EntityTexture sph,spht

sph2=CreateSphere()
ScaleEntity sph2,5.2,5.2,5.2
EntityAlpha sph2,.5
EntityFX sph2,1+16
EntityTexture sph2,spht

obj=CreateCylinder()
ScaleEntity obj,0.4,8,0.4
Entityblend obj,3

ff=CreatePivot()

For cc=1 To 8
    tt(cc)=CreatePivot(ff)
    cb(cc)=CreateCube(tt(cc))
    EntityColor cb(cc),Rnd(100,255),Rnd(0,255),Rnd(50,255)
    PositionEntity cb(cc),0,0,-6
    RotateEntity tt(cc),0,45*cc,0
Next

map=256

redt=createtexture(map,map)
bluet=createtexture(map,map)

initglass()

While not keydown(1)
    time#=time#+2
    tx#=(Sin(time*2)*0.5)+0.7
    ty#=Cos(time/2)*3
    For xx=1 to 8
        ScaleEntity cb(xx),tx,tx,tx
        TurnEntity cb(xx),ty,ty,ty
    Next
    If KeyHit(57) Then waitkey:WaitKey
    TurnEntity obj,1,1,1
    TurnEntity ff,0,1,0
    TurnEntity sph,tx/5,ty/5,0.1
    TurnEntity sph2,-tx/5,-ty/5,-0.1
    stereo(map,redt,bluet)
    UpdateWorld
    RenderWorld
    Flip
Wend

End

Function initglass()
    black=makequad()
    EntityAlpha black,1
    PositionEntity black,0,0,1.01

    red=makequad()
    EntityColor red,255,0,0
    EntityFX red,1
    EntityBlend red,3
    PositionEntity red,0,0,1
    TextureBlend redt,5
    EntityTexture red,redt

    blue=makequad()
    EntityColor blue,0,255,255
    EntityFX blue,1
    EntityBlend blue,3
    PositionEntity blue,0,0,1
    TextureBlend bluet,5
    EntityTexture blue,bluet
End Function

Function makequad()
    meshquad = CreateMesh(cam)
	srf      = CreateSurface(meshquad)

	AddVertex   srf, -1, 1, 0, 0, 0
	AddVertex   srf,  1, 1, 0, 1, 0
	AddVertex   srf, -1,-1, 0, 0, 1
	AddVertex   srf,  1,-1, 0, 1, 1
		
   	AddTriangle srf, 0, 1, 2
	AddTriangle srf, 3, 2, 1
	return meshquad
End Function

Function stereo(map,rtext,btext)
    HideEntity red
    HideEntity blue
    HideEntity black
	CameraViewport Cam, 0, 0, map, map
    ;red
    MoveEntity cam,-.1,0,0
    RenderWorld
    CopyRect 0,0,map,map,0,0,BackBuffer(),TextureBuffer(rtext)
    ;blue
    MoveEntity cam,.2,0,0
    RenderWorld
    CopyRect 0,0,map,map,0,0,BackBuffer(),TextureBuffer(btext)
	CameraViewport Cam,0,0,GraphicsWidth(),GraphicsHeight()
    MoveEntity cam,-.1,0,0
    ShowEntity red
    ShowEntity blue
    ShowEntity black
End Function
