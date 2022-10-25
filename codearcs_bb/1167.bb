; ID: 1167
; Author: Cygnus
; Date: 2004-09-26 13:53:19
; Title: Cube mapping made 6x faster and easier
; Description: An efficient, simple, Organised, portable Cubemapping System (6x faster)

Global scx=640,scy=480
Graphics3D scx,scy,16,2

light=CreateLight()
plane=CreatePlane()


gld=CreateTexture(255,255)
SetBuffer TextureBuffer(gld)
For x=0 To 128
Color x,x*2,x*3
Rect x,x,255-x/2,255-x/2,0
Next
SetBuffer BackBuffer()

ScaleTexture gld,100,100
EntityTexture plane,gld

camera=CreateCamera()
CameraClsColor camera,10,20,250
Pivot=CreatePivot()
EntityParent camera,Pivot


PositionEntity camera,0,20,-30


;Create some entities to cubemap
sphere=CreateSphere(20)
PositionEntity sphere,0,20,10
ScaleEntity sphere,10,10,10
cube=CreateCube()
PositionEntity cube,0,20,-10
ScaleEntity cube,5,5,5

;Cubemap them!
addcubemapobject(cube,256,0)
addcubemapobject(sphere,128,0)


;You dont need to do the next couple of lines, but its good for efficiency:
setcubemapupdatefrequency(cube,2,1)		;Update cube every 15 frames, and enable "face per render" (update only a part of the cubemap every update)
setcubemapupdatefrequency(sphere,2,1)		;Update sphere every 15 frames, and enable "face per render" (update only a part of the cubemap every update)




;change the camera range of these two to speed things up
cubemeshCameraRange(cube,10,250)
cubemeshCameraRange(sphere,10,250)



;do a render loop
Repeat
mlt#=1
If KeyDown(57) Then mlt#=10
TurnEntity Pivot,0,.1*mlt#,0
TurnEntity cube,.1*mlt#,0,0
updatecubemaps()   ;Update all cubemaps!

;or you can use : updatenextcubemap()   ;Update NEXT cubemap!


RenderWorld
Flip
Until KeyDown(1)
Deletecubemapobject(cube)	;Delete the cubemap stuff from "cube"
Deletecubemapobject(sphere)	;Delete the cubemap stuff from "sphere"

Deleteallcubemapobjects()		;Alternatively, call this at the end to wipe all cubemap stuff

End











;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;Cubemap System;;;;;;;;;;;;;;;;;;;;;;;;;;;;
Type cubemapobject
Field entity
Field Texture
Field detail
Field cammin#,cammax#
Field Lastupdate,updatefrequency
Field Lastcamupdate
Field faceperrender
Field Lastface=1
End Type

Global cubemapcameraent=0
Global Lastcubemapcount,cubemapcount

Function clearcubemapsystem()
Deleteallcubemapobjects()
If cubemapcameraent<>0 Then FreeEntity cubemapcameraent:cubemapcameraent=0
End Function

Function addcubemapobject(mesh,detail,Texturelayer=0,blendmode=2)
Local t.cubemapobject=New cubemapobject
t\entity=mesh
Tx=CreateTexture(detail,detail,1+128+256)
EntityTexture mesh,tx,0,Texturelayer
TextureBlend tx,blendmode
t\Texture=tx
t\detail=detail
If cubemapcameraent=0 Then cubemapcameraent=CreateCamera():CameraProjMode cubemapcameraent,0
t\updatefrequency=1
t\Lastupdate=Lastcubemapcount
CameraProjMode cubemapcameraent,0
t\cammin=1
t\cammax=1000
CameraRange cubemapcameraent,t\cammin,t\cammax
updatecubemap(t\Texture,cubemapcameraent,t\entity)
Lastcubemapcount=Lastcubemapcount+1;MilliSecs()
End Function


Function cubemeshCameraRange(mesh,CameraRangemin#,CameraRangemax#)  ;sets how a cubemaped texture is rendered.
Local n.cubemapobject
For n=Each cubemapobject
If n\entity=mesh Then
n\cammin=CameraRangemin
n\cammax=CameraRangemax
EndIf
Next
End Function


Function Deleteallcubemapobjects()
Local n.cubemapobject
For n=Each cubemapobject
Deletecubemapobject n\entity
Next
If cubemapcameraent<>0 Then FreeEntity cubemapcameraent:cubemapcameraent=0
End Function


Function Deletecubemapobject(mesh)
Local n.cubemapobject
For n=Each cubemapobject
If n\entity=mesh Then
If n\Texture<>0 Then FreeTexture n\Texture
Delete n
EndIf
Next
checkifanycubemapsleft()
End Function

Function checkifanycubemapsleft()
Local cnt=0,n.cubemapobject
For n=Each cubemapobject
cnt=cnt+1
Next
If cnt=0 Then clearcubemapsystem()
End Function

Function updatecubemaps()
Local o.cubemapobject
For o.cubemapobject=Each cubemapobject
If (cubemapcount-o\Lastupdate)>o\updatefrequency Then
CameraProjMode cubemapcameraent,0
CameraRange cubemapcameraent,o\cammin,o\cammax
updatecubemap(o\Texture,cubemapcameraent,o\entity,o\Lastface*o\faceperrender):o\Lastface=(o\Lastface Mod 6)+1
o\Lastupdate=cubemapcount
EndIf
Next
cubemapcount=cubemapcount+1
End Function


Function updatenextcubemap()
Local o.cubemapobject
Local cnt=0,cnt2=0

For o.cubemapobject=Each cubemapobject
cnt2=cnt2+1
Next

For o.cubemapobject=Each cubemapobject
cnt=cnt+1
If cubemapcount Mod (cnt2+1)=cnt Then;If (cubemapcount-o\Lastupdate)>o\updatefrequency Then
CameraProjMode cubemapcameraent,0
CameraRange cubemapcameraent,o\cammin,o\cammax
updatecubemap(o\Texture,cubemapcameraent,o\entity,o\Lastface*o\faceperrender):o\Lastface=(o\Lastface Mod 6)+1
o\Lastupdate=cubemapcount
EndIf
Next
cubemapcount=cubemapcount+1
End Function

Function setcubemapupdatefrequency(mesh,updaterate,faceperrender=0)
Local o.cubemapobject
For o.cubemapobject=Each cubemapobject
If o\entity=mesh Then o\updatefrequency=updaterate:o\faceperrender=faceperrender
Next
End Function

Function UpdateCubemap(tex,camera,entity,side=0)   ;the bit ripped from the Blitz demo


	tex_sz=TextureWidth(tex)

	; Show the camera we have specifically created for updating the cubemap
	CameraProjMode camera,1
	; Hide entity that will have cubemap applied to it. This is so we can get cubemap from its position, without it blocking the view
	HideEntity entity

	; Position camera where the entity is - this is where we will be rendering views from for cubemap
	PositionEntity camera,EntityX#(entity,1),EntityY#(entity,1),EntityZ#(entity,1)

	CameraClsMode camera,False,True
	
	; Set the camera's viewport so it is the same size as our texture - so we can fit entire screen contents into texture
	CameraViewport camera,0,0,tex_sz,tex_sz

	; Update cubemap
If side=0 Or side=1 Then
	; do left view	
	SetCubeFace tex,0
	RotateEntity camera,0,90,0
	RenderWorld
	CopyRect 0,0,tex_sz,tex_sz,0,0,BackBuffer(),TextureBuffer(tex)
EndIf
If side=0 Or side=2 Then
	; do forward view
	SetCubeFace tex,1
	RotateEntity camera,0,0,0
	RenderWorld
	CopyRect 0,0,tex_sz,tex_sz,0,0,BackBuffer(),TextureBuffer(tex)

EndIf
If side=0 Or side=3 Then

	; do right view	
	SetCubeFace tex,2
	RotateEntity camera,0,-90,0
	RenderWorld
	CopyRect 0,0,tex_sz,tex_sz,0,0,BackBuffer(),TextureBuffer(tex)
	
EndIf
If side=0 Or side=4 Then
	; do backward view
	SetCubeFace tex,3
	RotateEntity camera,0,180,0
	RenderWorld
	CopyRect 0,0,tex_sz,tex_sz,0,0,BackBuffer(),TextureBuffer(tex)
	
EndIf
If side=0 Or side=5 Then

	; do up view
	SetCubeFace tex,4
	RotateEntity camera,-90,0,0
	RenderWorld
	CopyRect 0,0,tex_sz,tex_sz,0,0,BackBuffer(),TextureBuffer(tex)
	
EndIf
If side=0 Or side=6 Then

	; do down view
	SetCubeFace tex,5
	RotateEntity camera,90,0,0
	RenderWorld
	CopyRect 0,0,tex_sz,tex_sz,0,0,BackBuffer(),TextureBuffer(tex)
	
EndIf
	; Show entity again
	ShowEntity entity
	
	; Hide the cubemap camera
	CameraProjMode camera,0
	
End Function
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
