; ID: 2677
; Author: Streaksy
; Date: 2010-03-25 07:59:00
; Title: Faster/Smarter Screen-Coordinates &gt; 3D Coordinates
; Description: Here's a mega fast way of doing it without even using CamerPick().  You can specify depth (Z) if you need to.

Global pickmesh,pastpickcam,cp3piv

Global Picked3X# ;Values returned from CameraPick3()
Global Picked3Y#
Global Picked3Z#






;test
Graphics3D 1024,768,32,2
SetBuffer BackBuffer()
dist#=10
light=CreateLight(2):LightRange light,20:PositionEntity light, 10,15,-10:AmbientLight 50,50,50
ball=CreateSphere(64):ScaleEntity ball,.025,1,.025:EntityColor ball,255,0,0:EntityShininess ball,.5
ball2=CreateSphere(64):ScaleEntity ball2,1,.025,.025:EntityColor ball2,0,0,255:EntityShininess ball2,.5
midball=CreateCube():ScaleEntity midball,.05,.05,.05:EntityColor midball,255,255,0:EntityShininess midball,.5

	cball1=CreateSphere(64):ScaleEntity cball1,.05,.05,.05:EntityColor cball1,155,255,155:EntityShininess cball1,.5
	cball2=CreateSphere(64):ScaleEntity cball2,.05,.05,.05:EntityColor cball2,155,255,155:EntityShininess cball2,.5
	cball3=CreateSphere(64):ScaleEntity cball3,.05,.05,.05:EntityColor cball3,155,255,155:EntityShininess cball3,.5
	cball4=CreateSphere(64):ScaleEntity cball4,.05,.05,.05:EntityColor cball4,155,255,155:EntityShininess cball4,.5

cam=CreateCamera()
	For rep=1 To 800 ;background spheres
	b=CreateSphere()
	EntityAlpha b,.5
	EntityColor b,Rnd(255),Rnd(255),Rnd(255)
	EntityShininess b,.5
	PositionEntity b,Rand(-50,50),Rand(-50,50),Rand(-50,50)
	Next
Repeat
Cls
	CameraPick2 cam,MouseX(),MouseY(),dist
	PositionEntity ball,PickedX(),PickedY(),PickedZ()
		CameraPick3 cam,MouseX(),MouseY(),dist
		PositionEntity ball2,Picked3X,Picked3Y,Picked3Z
PositionEntity midball,Picked3X,Picked3Y,Picked3Z

				cornerx1=MouseX()-100:cornery1=MouseY()-100
				cornerx2=MouseX()+100:cornery2=MouseY()-100
				cornerx3=MouseX()-100:cornery3=MouseY()+100
				cornerx4=MouseX()+100:cornery4=MouseY()+100
				CameraPick3 cam,cornerx1,cornery1,2:PositionEntity cball1,Picked3X,Picked3Y,Picked3Z
				CameraPick3 cam,cornerx2,cornery2,2:PositionEntity cball2,Picked3X,Picked3Y,Picked3Z
				CameraPick3 cam,cornerx3,cornery3,2:PositionEntity cball3,Picked3X,Picked3Y,Picked3Z
				CameraPick3 cam,cornerx4,cornery4,2:PositionEntity cball4,Picked3X,Picked3Y,Picked3Z
RenderWorld
				Rect cornerx1,cornery1,cornerx2-cornerx1,cornery3-cornery1,0 ;draw rectangle
				Line cornerx1,cornery1,cornerx4,cornery4
				Line cornerx2,cornery2,cornerx3,cornery3
Color 255,90,90:Text 20,5,"CameraPick2 cam,MouseX(),MouseY(),10   =   X="+PickedX()+", Y="+PickedY()+", Z="+PickedZ()
Color 90,90,255:Text 20,35,"CameraPick3 cam,MouseX(),MouseY(),10   =   X="+picked3x+", Y="+picked3y+", Z="+picked3z
Color 255,255,255:Text 40,60,"Use mouse buttons to control the depth, and cursor-keys left & right to turn camera"
If MouseDown(1) Then dist=dist+.5
If MouseDown(2) Then dist=dist-.5
If KeyDown(203) Then TurnEntity cam,0,1,0
If KeyDown(205) Then TurnEntity cam,0,-1,0
If dist<1.5 Then dist=1.5
If dist>30 Then dist=30
Flip
Until KeyHit(1)
End








; CAMERAPICK2()
;
;     PROS:
; * Changing the camera viewport doesn't require you to pass the new width and height
; * Results are returned to PickedX#(), PickedY#(), and PickedZ#(), as normal
;
;     CONS:
; * Relies on CameraPick() which means other pickable entities could cause conflicts,
;   affecting the resulting Z coordinate (but who cares)
; * Slower than CameraPick3()
;
Function CameraPick2(cm,x,y,z#=10)
If pickmesh=0 Then
pickmesh=CreateMesh()
srf=CreateSurface(pickmesh)
sz=100
v1=AddVertex(srf,-sz,-sz,0)
v2=AddVertex(srf,sz,-sz,0)
v3=AddVertex(srf,-sz,sz,0)
v4=AddVertex(srf,sz,sz,0)
AddTriangle srf,v1,v3,v4
AddTriangle srf,v1,v4,v2
EntityPickMode pickmesh,2
Else ShowEntity pickmesh
EndIf
	If cm<>lastpickcam Then
	lastpickcam=cm
	EntityParent pickmesh,cm
	PositionEntity pickmesh,0,0,z,0
	CameraPick cm,x,y
	EndIf
HideEntity pickmesh
End Function







; CAMERAPICK3() - Probably the best one
;
;     PROS:
; * Way fast
; * Doesn't rely on CameraPick() so there will be no conflicts with other pickable entities
;
;     CONS:
; * If the camera viewport has been changed, you must suply it's width and height
; * Results are returned to Picked3X#, Picked3Y# and Picked3Z# instead of the normal functions.  Not really a problem, though
;
Function CameraPick3#(cm,x,y,z#=10,sw=0,sh=0)
If sw=0 Then sw=GraphicsWidth()
If sh=0 Then sh=GraphicsHeight()
x=x-(sw/2):y=y-(sh/2)
If cp3piv=0 Then cp3piv=CreatePivot()
aspectratio#=Float(sh)/Float(sw)
ex#=(Float(x)/Float(sw))*(z*2)
ey#=-(Float(y)/Float(sh))*aspectratio*(z*2)
EntityParent cp3piv,cm
PositionEntity cp3piv,ex,ey,z,0
Picked3X=EntityX(cp3piv,1)
Picked3Y=EntityY(cp3piv,1)
Picked3Z=EntityZ(cp3piv,1)
End Function
