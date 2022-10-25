; ID: 1129
; Author: Cygnus
; Date: 2004-08-06 19:33:19
; Title: Add an image to a Texture
; Description: Add an image to a Texture!

Graphics3D 640,480
cube=CreateCube()
camera=CreateCamera()
PositionEntity camera,0,1,-5
PointEntity camera, cube
light=CreateLight()

;create the texture
Texture=CreateTexture(640,640)
SetBuffer TextureBuffer(Texture)
For n=1 To 100
Color Rnd(255),Rnd(255),Rnd(255)
Rect Rnd(640),Rnd(640),Rnd(320),Rnd(320),1
Next

;;;Create the sample image

image=CreateImage(640,128)
SetBuffer ImageBuffer(image)
Color 255,255,255
 font=LoadFont(arial,128)
SetFont font
Text 320,64,"HEYYYY!!!!!",1,1
SetBuffer BackBuffer()

Color 255,255,255



;;;;;:DO THE FUNCTION!

MergeImageWithTexture(Texture,image,.5,.5,640,32)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;



EntityTexture cube,Texture
Repeat
RenderWorld
TurnEntity cube,-.2,0,0
TurnEntity cube,0,1,0,1
Flip
Until KeyDown(1)
End





;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
Function mergeimagefilewithtexture(Texture,filename$,u#,v#,sizex#,sizey#,maskr=-1,maskg=0,maskb=0)
Local img=LoadImage(filename$),th#,tw#,x#,y#,ratio1#,ratio2#,use#,cb
cb=GraphicsBuffer()
If maskr<>-1 Then MaskImage img,maskr,maskg,masgb
	x#=ImageWidth(img)
	targx#=sizex
	y#=ImageHeight(img)
	targy#=sizey
	ratio1#=targx#/x#
	ratio2#=targy#/y#
	
	x2#=x#/ratio1#
	y2#=y#/ratio2#
	If ratio1>ratio2 Then use#=ratio1 Else use#=ratio2
	ScaleImage img,use,use

	cb=GraphicsBuffer()
	SetBuffer TextureBuffer(Texture)
	tw#=TextureWidth(Texture)
	tw#=tw#/100.0
	th#=TextureHeight(Texture)
	th#=th#/100.0
	MidHandle img
	If maskr<>-1 Then DrawImage img,tw#*(u#*100),th#*(v*100) Else DrawBlock img,tw#*(u#*100),th#*(v*100)
	SetBuffer cb
FreeImage img
End Function

Function mergeimagewithtexture(Texture,Image,u#,v#,sizex#,sizey#,maskr=-1,maskg=0,maskb=0)
Local img=CopyImage(image),th#,tw#,x#,y#,ratio1#,ratio2#,use#,cb
cb=GraphicsBuffer()
	If maskr<>-1 Then MaskImage img,maskr,maskg,masgb

	x#=ImageWidth(img)
	targx#=sizex
	y#=ImageHeight(img)
	targy#=sizey
	ratio1#=targx#/x#
	ratio2#=targy#/y#
	
	x2#=x#/ratio1#
	y2#=y#/ratio2#
	If ratio1>ratio2 Then use#=ratio1 Else use#=ratio2
	ScaleImage img,use,use

	cb=GraphicsBuffer()
	SetBuffer TextureBuffer(Texture)
	tw#=TextureWidth(Texture)
	tw#=tw#/100.0
	th#=TextureHeight(Texture)
	th#=th#/100.0
	MidHandle img
	If maskr<>-1 Then DrawImage img,tw#*(u#*100),th#*(v*100) Else DrawBlock img,tw#*(u#*100),th#*(v*100)
	SetBuffer cb
FreeImage img
End Function
