; ID: 661
; Author: Ziltch
; Date: 2003-04-26 09:39:28
; Title: 3D Desktop Cubes
; Description: Lots of  Cubes over the desktop

; 3D Cubes  Desktop Sample 

Const SRCCOPY = $CC0020
Const CF_BITMAP = 2
Const SW_HIDE = 0
Const SW_SHOW = 5

Function GetDesktop(flag=0,update=0)

; ADAmor ZILTCH 2003
;
; This command must come after your GRAPHICS(3D) x,y command.
;
;  flag 0 = create texture
;       1 = create image

   DeskHwnd = GetDesktopWindow()

   ; Get screen coordinates

   fwidth  = GetSystemMetrics%(0) 
   fheight = GetSystemMetrics%(1) 

   BlitzHwnd = GetActiveWindow()
   ShowWindow(BlitzHwnd,SW_HIDE)

   ; Get the device context of Desktop and allocate memory
   hdc = GetDC(DeskHwnd)
   Blitzhdc = GetDC(BlitzHwnd)

   ; Copy data
   BitBlt(Blitzhdc, 0, 0, fwidth, fheight, hdc, 0,0, SRCCOPY)

   ; Clean up handles
   ReleaseDC(DeskHwnd, hdc)
   ReleaseDC(BlitzHwnd, Blitzhdc)
   ShowWindow(BlitzHwnd,SW_SHOW)

   ; Create/update texture or image
   Select flag
     Case 0 
       If update = 0 Then 
         tex=CreateTexture(fwidth,fheight) 
       Else 
         tex=update
       End If 
       CopyRect 0,0,fwidth,fheight,0,0,FrontBuffer(),TextureBuffer(tex) 
       Return tex 
     Case 1 
       If update = 0 Then 
         image=CreateImage(fwidth,fheight) 
       Else 
         image=update 
       End If 
       CopyRect 0,0,fwidth,fheight,0,0,FrontBuffer(),ImageBuffer(image) 
       Return image 
   End Select 

End Function

;--start sample code


deskwidth=GetSystemMetrics(0);800
deskheight=GetSystemMetrics(1);600
Graphics3D deskwidth,deskheight


cam=CreateCamera()
PositionEntity cam,0,0,-4
CameraClsMode  cam,False,True

l1=CreateLight(2)
LightColor     l1,255,255,50
LightRange     l1,300
PositionEntity l1,-100,50,100

l2=CreateLight(2)
LightColor     l2,255,200,100
LightRange     l2,300
PositionEntity l2,100,50,100

l3=CreateLight(2)
LightColor     l3,255,100,255
LightRange     l3,300
PositionEntity l3,0,-50,-10

tex=CreateTexture(1024,1024,1)
ScaleTexture tex,(1024/Float(deskwidth)),(1024/Float(deskheight))

cube1=CreateCube()
EntityTexture  cube1,tex
ScaleMesh      cube1,3,3,3
GetDesktop(0,tex)
image = getdesktop(1)

Type cube
 Field ent
End Type

For x = -200 To 200 Step 20
  For z = -100 To 300 Step 20
    a =a + 1
    c.cube = New cube
    c\ent= CopyEntity(cube1)
    PositionEntity c\ent,x,Rand(-100,150),z
    TurnEntity c\ent,Rand(360),Rand(360),Rand(360)
    UpdateNormals c\ent
  Next
Next

HideEntity cube1
FreeEntity cube1

SetBuffer BackBuffer()
While Not GetKey()

  DrawBlock image,0,0
  For tc.cube = Each cube
    TurnEntity tc\ent,1,1,0
    MoveEntity tc\ent,0,0,.1
  Next

  TurnEntity cam,.05,.1,0
  MoveEntity cam,0,0,.2

  ms = MilliSecs() Shr 2
  AmbientLight Sin(ms)*128+127,Cos(ms)*128+127,Sin(ms*3.5)*Cos(ms*4.7)*128+127

  RenderWorld
  Flip
Wend
End
