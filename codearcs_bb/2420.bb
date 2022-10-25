; ID: 2420
; Author: Nate the Great
; Date: 2009-02-22 21:55:43
; Title: Verlet water :-)
; Description: verlet water code

Graphics3D 640,480,0,2
SeedRnd(MilliSecs())

Global WATERLEVEL = 0
waterpln = CreatePlane()
EntityColor waterpln,0,0,255
EntityAlpha waterpln,.5
MoveEntity waterpln,0,waterlevel,0

cam = CreateCamera()
MoveEntity cam,0,2,-30
CameraZoom cam,2
CameraFogMode cam,1
CameraFogRange cam,20,100
CameraRange cam,.001,100
;CameraClsColor cam,100,100,100
;CameraFogColor cam,100,100,100

piv = CreatePivot()
EntityParent cam,piv
;TurnEntity piv,0,180,0
plane = CreatePlane()
tex = CreateTexture(256,256)
SetBuffer TextureBuffer(tex)
Color 255,255,255
Rect 0,0,128,128,1
Rect 128,128,128,128
Color 0,225,0
Rect 0,128,128,128
Rect 128,0,128,128
Color 255,255,255

mir = CreateMirror()
MoveEntity mir,0,-4,0

SetBuffer BackBuffer()
EntityTexture plane,tex
MoveEntity plane,0,-4,0
ScaleTexture tex,10,10
EntityAlpha plane,.5

lit = CreateLight()
MoveEntity lit,0,5,0
TurnEntity lit,90,0,0

Type verlet
	Field x#,y#,z#
	Field vx#,vy#,vz#
	Field ox#,oy#,oz#
	Field ent,piv
	Field collided,ID,mass#
End Type


Type rigidbody
	Field x#,y#,z#,ent
	Field yaw#,pitch#,roll#
	Field v1.verlet,v2.verlet,v3.verlet,v4.verlet,v5.verlet,v6.verlet,v7.verlet,v8.verlet,cpiv
	Field ID
End Type


Type constraint
	Field p1.verlet
	Field p2.verlet
	Field ent
	Field length#
End Type

Global rigidbodynum = 0

Global cube = CreateCube()
EntityAlpha cube,1
Applyphysicscube(cube,10)

SetBuffer BackBuffer()

tim = MilliSecs()


While Not KeyDown(1)
Cls

If KeyDown(200) Then MoveEntity cam,0,0,.1
If KeyDown(208) Then MoveEntity cam,0,0,-.1
If KeyDown(203) Then MoveEntity cam,-.1,0,0
If KeyDown(205) Then MoveEntity cam,.1,0,0

;TurnEntity piv,0,1,0

updateverlets()

updateconstraints()

equalizeverlets()

drawstuff()

UpdateWorld()
RenderWorld()

cnt = 0
For v.verlet = Each verlet
	cnt = cnt + 1
Next
Text 1,20,"Verticies: "+cnt
Text 1,1,"FPS: "+1000/(MilliSecs()-tim)
tim = MilliSecs()

Flip

Wend

WaitKey

Function Applyphysicscube(ent,mass#,idle = 0,siz# = 5)

rigidbodynum = rigidbodynum + 1

alph = 0

r.rigidbody = New rigidbody
r\id = rigidbodynum
r\ent = ent
r\x# = EntityX(r\ent)
r\y# = EntityY(r\ent)
r\z# = EntityZ(r\ent)
r\yaw# = EntityYaw(r\ent)
r\pitch# = EntityPitch(r\ent)
r\roll# = EntityRoll(r\ent)



r\v1.verlet = New verlet
r\v1\id = rigidbodynum
r\v1\x# = r\x#-MeshWidth(r\ent)/2
r\v1\y# = r\y#-MeshHeight(r\ent)/2
r\v1\z# = r\z#-MeshDepth(r\ent)/2
r\v1\ox# = r\x#-MeshWidth(r\ent)/2-.9
r\v1\oy# = r\y#-MeshHeight(r\ent)/2-.2
r\v1\oz# = r\z#-MeshDepth(r\ent)/2-.1
r\v1\ent = r\ent
r\v1\mass# = mass#/8
r\v1\piv = CreateSphere()
EntityAlpha r\v1\piv,alph
PositionEntity r\v1\piv,r\v1\x#,r\v1\y#,r\v1\z#

r\v2.verlet = New verlet
r\v2\id = rigidbodynum
r\v2\x# = r\x#-MeshWidth(r\ent)/2
r\v2\y# = r\y#+MeshHeight(r\ent)/2
r\v2\z# = r\z#+MeshDepth(r\ent)/2
r\v2\ox# = r\x#-MeshWidth(r\ent)/2-.3
r\v2\oy# = r\y#+MeshHeight(r\ent)/2-.3
r\v2\oz# = r\z#+MeshDepth(r\ent)/2-.1
r\v2\ent = r\ent
r\v2\mass# = mass#/8
r\v2\piv = CreateSphere()
EntityAlpha r\v2\piv,alph
PositionEntity r\v2\piv,r\v2\x#,r\v2\y#,r\v2\z#

r\v3.verlet = New verlet
r\v3\id = rigidbodynum
r\v3\x# = r\x#-MeshWidth(r\ent)/2
r\v3\y# = r\y#-MeshHeight(r\ent)/2
r\v3\z# = r\z#+MeshDepth(r\ent)/2
r\v3\ox# = r\x#-MeshWidth(r\ent)/2-.5
r\v3\oy# = r\y#-MeshHeight(r\ent)/2-.1
r\v3\oz# = r\z#+MeshDepth(r\ent)/2-.5
r\v3\ent = r\ent
r\v3\mass# = mass#/8
r\v3\piv = CreateSphere()
EntityAlpha r\v3\piv,alph
PositionEntity r\v3\piv,r\v3\x#,r\v3\y#,r\v3\z#

r\v4.verlet = New verlet
r\v4\id = rigidbodynum
r\v4\x# = r\x#-MeshWidth(r\ent)/2
r\v4\y# = r\y#+MeshHeight(r\ent)/2
r\v4\z# = r\z#-MeshDepth(r\ent)/2
r\v4\ox# = r\x#-MeshWidth(r\ent)/2
r\v4\oy# = r\y#+MeshHeight(r\ent)/2-.1
r\v4\oz# = r\z#-MeshDepth(r\ent)/2
r\v4\ent = r\ent
r\v4\mass# = mass#/8
r\v4\piv = CreateSphere()
EntityAlpha r\v4\piv,alph
PositionEntity r\v4\piv,r\v4\x#,r\v4\y#,r\v4\z#

r\v5.verlet = New verlet
r\v5\id = rigidbodynum
r\v5\x# = r\x#+MeshWidth(r\ent)/2
r\v5\y# = r\y#+MeshHeight(r\ent)/2
r\v5\z# = r\z#+MeshDepth(r\ent)/2
r\v5\ox# = r\x#+MeshWidth(r\ent)/2
r\v5\oy# = r\y#+MeshHeight(r\ent)/2-.2
r\v5\oz# = r\z#+MeshDepth(r\ent)/2
r\v5\ent = r\ent
r\v5\mass# = mass#/8
r\v5\piv = CreateSphere()
EntityAlpha r\v5\piv,alph
PositionEntity r\v5\piv,r\v5\x#,r\v5\y#,r\v5\z#

r\v6.verlet = New verlet
r\v6\id = rigidbodynum
r\v6\x# = r\x#+MeshWidth(r\ent)/2
r\v6\y# = r\y#+MeshHeight(r\ent)/2
r\v6\z# = r\z#-MeshDepth(r\ent)/2
r\v6\ox# = r\x#+MeshWidth(r\ent)/2
r\v6\oy# = r\y#+MeshHeight(r\ent)/2-.15
r\v6\oz# = r\z#-MeshDepth(r\ent)/2
r\v6\ent = r\ent
r\v6\mass# = mass#/8
r\v6\piv = CreateSphere()
EntityAlpha r\v6\piv,alph
PositionEntity r\v6\piv,r\v6\x#,r\v6\y#,r\v6\z#

r\v7.verlet = New verlet
r\v7\id = rigidbodynum
r\v7\x# = r\x#+MeshWidth(r\ent)/2
r\v7\y# = r\y#-MeshHeight(r\ent)/2
r\v7\z# = r\z#-MeshDepth(r\ent)/2
r\v7\ox# = r\x#+MeshWidth(r\ent)/2
r\v7\oy# = r\y#-MeshHeight(r\ent)/2-.15
r\v7\oz# = r\z#-MeshDepth(r\ent)/2
r\v7\ent = r\ent
r\v7\mass# = mass#/8
r\v7\piv = CreateSphere()
EntityAlpha r\v7\piv,alph
PositionEntity r\v7\piv,r\v7\x#,r\v7\y#,r\v7\z#

r\v8.verlet = New verlet
r\v8\id = rigidbodynum
r\v8\x# = r\x#+MeshWidth(r\ent)/2
r\v8\y# = r\y#-MeshHeight(r\ent)/2
r\v8\z# = r\z#+MeshDepth(r\ent)/2
r\v8\ox# = r\x#+MeshWidth(r\ent)/2
r\v8\oy# = r\y#-MeshHeight(r\ent)/2-.15
r\v8\oz# = r\z#+MeshDepth(r\ent)/2
r\v8\ent = r\ent
r\v8\mass# = mass#/8
r\v8\piv = CreateSphere()
EntityAlpha r\v8\piv,alph
PositionEntity r\v8\piv,r\v8\x#,r\v8\y#,r\v8\z#

r\cpiv = CreatePivot()
PositionEntity r\cpiv,r\x#,r\y#,r\z#


For v.verlet = Each verlet
	If v\ID = rigidbodynum Then
		If idle = 0 Then
			For vv.verlet = Each verlet
				If vv\id = rigidbodynum Then
					If vv\piv <> v\piv
						c.constraint = New constraint
						c\p1.verlet = v.verlet
						c\p2.verlet = vv.verlet
						dx# = c\p1\x# - c\p2\x#
						dy# = c\p1\y# - c\p2\y#
						dz# = c\p1\z# - c\p2\z#
						c\length# = Sqr(dx#*dx# + dy#*dy# + dz#*dz#)
						c\ent = c\p1\ent
					EndIf
				EndIf
			Next
		EndIf
	EndIf
Next


For c.constraint = Each constraint
	For cc.constraint = Each constraint
		If c\p1\piv = cc\p1\piv And c\p2\piv = c\p1\piv Then
			Delete cc.constraint
		EndIf
	Next
Next

End Function



Function updateverlets()


For v.verlet = Each verlet
		v\collided = False
		v\vx# = (v\x# - v\ox#)*.985 ; Get the velocities of the verlet,...add a bit of decay to simulate friction
		v\vy# = (v\y# - v\oy#)*.985
		v\vz# = (v\z# - v\oz#)*.985

		v\ox# = v\x# ; store position in "old"
		v\oy# = v\y#
		v\oz# = v\z#
		
		v\x# = v\x# + v\vx# ;store new postion based on velocity
		
		v\y# = v\y# + v\vy# - .007
		
		v\z# = v\z# + v\vz#
		
		;check screen bounds
		If v\y#	< -4 ;ground collision
			v\y# = -4
			v\collided = True
			v\vy# = -v\vy#
		EndIf
		
		If v\y# < waterlevel Then
			dtmp# = waterlevel-v\y#
			If dtmp# > 1 Then
				v\oy# = v\oy# - .02
			Else
				v\oy# = v\oy# - .02*dtmp#
			EndIf
		EndIf
Next


End Function



Function drawstuff()
	;For v.verlet = Each verlet
		;VertexCoords v\surf,v\index,v\x#,v\y#,v\z#
	;Next
	
	For r.rigidbody = Each rigidbody
		cnt = 0
		avgx# = 0
		avgy# = 0
		avgz# = 0
		For v.verlet = Each verlet
			If v\ent = r\ent
				cnt = cnt + 1
				avgx# = avgx# + v\x#
				avgy# = avgy# + v\y#
				avgz# = avgz# + v\z#
				PositionEntity v\piv,v\x#,v\y#,v\z#
			EndIf
		Next
		avgx# = avgx#/cnt
		avgy# = avgy#/cnt
		avgz# = avgz#/cnt
		
		r\x# = avgx#
		r\y# = avgy#
		r\z# = avgz#
		
		RotateEntity r\ent,0,0,0
		PositionEntity r\ent,avgx#,avgy#,avgz#
		
		cnt = 0
		avgyaw =0
		avgpitch = 0
		avgroll = 0
		
		
		;this computes the orientation of the verticies using stevie g's code  Thnx Stevie G!!!
			
			;align mesh to verlet cage
			x# = EntityX( r\v5\piv ) - EntityX( r\v2\piv ) + EntityX( r\v6\piv ) - EntityX( r\v4\piv )
			y# = EntityY( r\v5\piv ) - EntityY( r\v2\piv ) + EntityY( r\v6\piv ) - EntityY( r\v4\piv )
			z# = EntityZ( r\v5\piv ) - EntityZ( r\v2\piv ) + EntityZ( r\v6\piv ) - EntityZ( r\v4\piv )
			AlignToVector r\ent, x#,y#,z#,1  
			x# = EntityX( r\v5\piv ) - EntityX( r\v6\piv ) + EntityX( r\v2\piv ) - EntityX( r\v4\piv )
			y# = EntityY( r\v5\piv ) - EntityY( r\v6\piv ) + EntityY( r\v2\piv ) - EntityY( r\v4\piv )
			z# = EntityZ( r\v5\piv ) - EntityZ( r\v6\piv ) + EntityZ( r\v2\piv ) - EntityZ( r\v4\piv )
			AlignToVector r\ent, x#,y#,z#, 3
			

			
		
	Next
End Function


Function updateconstraints()

For a = 1 To 10
	For c.constraint = Each constraint
		dx#=c\p2\x#	-	c\p1\x#
		dy#=c\p2\y#	-	c\p1\y#
		dz#=c\p2\z#	-	c\p1\z#
				
		length#=Sqr(dx*dx + dy*dy + dz*dz) ; distance between p1 and p2

		If length#<>0 ;avoid divide by 0, then normalize the vector
			diff# = (length# - c\length#) / length#  ; vector length minus constraint length
		Else
			diff# = 0
		EndIf

		dx# = dx# * .5 ;find the midpoint
		dy# = dy# * .5
		dz# = dz# * .5
		
		If c\p1\collided = 0 Or a > 8 Then
			c\p1\x# = c\p1\x# + diff# * dx# 
			c\p1\y# = c\p1\y# + diff# * dy#
			c\p1\z# = c\p1\z# + diff# * dz#
		EndIf
		If c\p2\collided = 0 Or a > 8 Then
			c\p2\x# = c\p2\x# - diff# * dx#
			c\p2\y# = c\p2\y# - diff# * dy#
			c\p2\z# = c\p2\z# - diff# * dz#
		EndIf
	Next  ;constraints
Next


End Function



Function equalizeverlets()

;For c.constraint = Each constraint
;	If c\length# = 0 Then
;		dx#=c\p2\x#	+	c\p1\x#
;		dy#=c\p2\y#	+	c\p1\y#
;		dz#=c\p2\z#	+	c\p1\z#
;		
;		dx# = dx# * .5 ;find the midpoint
;		dy# = dy# * .5
;		dz# = dz# * .5
;		
;		c\p2\x# = dx#
;		c\p2\y# = dy#
;		c\p2\z# = dz#
;		
;		c\p1\x# = dx#
;		c\p1\y# = dy#
;		c\p1\z# = dz#
;		
;	EndIf
;Next

End Function



Function rotatephysicsentity(ent)

For r.rigidbody = Each rigidbody
	If r\ent = ent Then
		tmppiv = CreatePivot()
		PositionEntity tmppiv,r\x#,r\y#,r\z#
	EndIf
Next

End Function
