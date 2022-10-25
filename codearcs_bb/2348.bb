; ID: 2348
; Author: Nate the Great
; Date: 2008-10-30 02:44:23
; Title: 2d alpha rotation and scaling realtime for b3d only sry blitz+ :(
; Description: uses commands that I made look similar to blitz max

Graphics3D 640,480,0,2

lit = CreateLight(2)

MoveEntity lit,0,-101,0

SetBuffer BackBuffer()

Global NTGox#
Global NTGoz#
Global NTGpixelwidth#
Global NTGpixelheight#
Global NTGinit = False
Global NTGplane
Global NTGcam
Global NTGheight#
Global NTGrotation#
Global NTGscalex# = 1
Global NTGscaley# = 1
Global NTGalpha# = 1
Global NTGblend = 3

Type NTGsprite
	Field entity,active
End Type


NTGinit2dAlpha()
;|||||||||||||||||||||||||||||||||||||||||||||||||||||||||
;|||||||||||||||||||||||||||||||||||||||||||||||||||||||||
;|||||||||||||||||||||||||||||||||||||||||||||||||||||||||
;|||||||||||||||||||||||||||||||||||||||||||||||||||||||||





Type rects
	Field x#,y#,rot#,rotspeed#,vx#,vy#,lif,ent.NTGsprite
End Type



rect1.NTGsprite = NTGcreateRect.NTGsprite(40,40)

cnt# = 1

timer = MilliSecs()
While Not KeyDown(1)
Cls

NTGsetrotation cnt#
NTGsetscale Sin(cnt#),Sin(cnt#)
NTGsetalpha Sin(cnt#/2)/2 + .5
DrawNTG rect1.NTGsprite,MouseX(),MouseY()

NTGsetscale 1,1

	For r.rects = Each rects
		NTGsetrotation r\rot#
		NTGsetalpha r\lif/255.0
		DrawNTG r\ent.NTGsprite,r\x#,r\y#
		r\lif = r\lif - 1
		r\rot# = r\rot# + r\rotspeed#
		r\x# = r\x# + r\vx#
		r\y# = r\y# + r\vy#
		r\vy# = r\vy# + .07
		If r\y# > 480 Then
			r\vy# = - r\vy#*Rnd#(.5,.9)
			r\y# = 480
		EndIf
		
		If r\lif = 0 Then
			FreeEntity r\ent\entity
			Delete r\ent.NTGsprite
			Delete r.rects
		EndIf
	Next

If MouseDown(1) Then
	r.rects = New rects
	r\x# = MouseX()
	r\y# = MouseY()
	r\rot# = Rnd(360)
	r\rotspeed# = Rnd#(-10,10)
	r\vx# = Rnd#(-3,3)
	r\vy# = Rnd#(-3,4)
	r\lif = 255
	r\ent.NTGsprite = NTGCreateRect.NTGsprite(40,40,255,0,255)
EndIf

cnt = cnt + 1
If cnt = 720 Then cnt = 0

UpdateWorld()
RenderWorld()
UpdateNTG()

.stt1
If MilliSecs()-timer > 0 Then
.stt	
	x = 1000/(MilliSecs()-timer)
	If x > 58 Then Goto stt
	Text 1,1,x
Else
	Goto stt1
EndIf
timer = MilliSecs()

Flip
Wend


Flip
WaitKey()









;|||||||||||||||||||||||||||||||||||||||||||||||||||||||||
;|||||||||||||||||||||||||||||||||||||||||||||||||||||||||
;|||||||||||||||||||||||||||||||||||||||||||||||||||||||||
;|||||||||||||||||||||||||||||||||||||||||||||||||||||||||










Function DrawNTG(hand.NTGsprite,x#,y#)

ShowEntity hand\entity
hand\active = True
PositionEntity hand\entity,NTGox# + x#*NTGpixelwidth#,0,NTGoz# + y#*NTGpixelheight
RotateEntity hand\entity,0,NTGrotation#,0
ScaleEntity hand\entity, NTGscalex#,1,NTGscaley#
EntityAlpha hand\entity, NTGalpha#
EntityBlend hand\entity, NTGblend

End Function


Function UpdateNTG()

For s.NTGsprite = Each NTGsprite
	s\active = False
	HideEntity s\entity
Next

End Function

Function NTGSetBlend(bld)
	NTGblend = bld
End Function

Function NTGSetRotation(Rot# = 0)
	NTGrotation = rot#
End Function

Function NTGsetscale(sclx# = 1,scly# = 1)
	NTGscalex# = sclx#
	NTGscaley# = scly#
End Function

Function NTGsetalpha(alph# = 1)
	NTGalpha# = alph#
End Function


Function NTGCreateRect.NTGsprite(width#,height#,r = 255,g = 255,b = 255)

tmp.NTGsprite = New NTGsprite
tmp\entity = CreateMesh()
tmpsurf = CreateSurface(tmp\entity)
width# = width# / 2
height# = height# / 2
v1 = AddVertex(tmpsurf,0-(width#*NTGpixelwidth),NTGheight#,0-(height#*NTGpixelheight#))
v2 = AddVertex(tmpsurf,(width#*NTGpixelwidth),NTGheight#,0-(height#*NTGpixelheight#))
v3 = AddVertex(tmpsurf,(width#*NTGpixelwidth),NTGheight#,(height#*NTGpixelheight#))
v4 = AddVertex(tmpsurf,0-(width#*NTGpixelwidth),NTGheight#,(height#*NTGpixelheight#))


AddTriangle(tmpsurf,v3,v4,v2)
AddTriangle(tmpsurf,v2,v4,v1)

EntityColor tmp\entity,r,g,b

UpdateNormals tmp\entity

HideEntity tmp\entity

Return tmp.NTGsprite

End Function









Function NTGinit2dAlpha(maxrng# = 1000)

NTGcam = CreateCamera()
NTGinit = True

If NTGcam = 0 Then RuntimeError("Camera does not exist.")

NTGplane = CreatePlane()

TurnEntity NTGplane,-90,0,0
MoveEntity NTGplane,0,-2,0
EntityParent NTGplane,NTGcam
EntityAlpha NTGplane,0
EntityPickMode NTGplane,2

CameraRange NTGcam,.01,maxrng#
TurnEntity NTGcam,90,0,0
MoveEntity NTGcam,0,0,100
CameraZoom NTGcam,3
CameraClsMode NTGcam,0,1

CameraPick NTGcam,1,1

x1# = PickedX()
z1# = PickedZ()
NTGheight# = PickedY()

CameraPick NTGcam,GraphicsWidth(),GraphicsHeight()

x2# = PickedX()
z2# = PickedZ()


NTGox# = x1#
NTGoz# = z1#

NTGpixelwidth# = (x2#-x1#)/GraphicsWidth()
NTGpixelheight# = (z2#-z1#)/GraphicsHeight()

End Function
