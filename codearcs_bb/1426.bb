; ID: 1426
; Author: Afke
; Date: 2005-07-22 05:44:56
; Title: dragon fire
; Description: dragon fire

Graphics3D 800,600,32,1
SetBuffer BackBuffer()
enable=True
vert=False

Global camera=CreateCamera ()
;environment cube
cube=CreateCube ()
FitMesh cube,-250,0,-250,500,500,500
FlipMesh cube
tex=LoadTexture( "D:\Program Files\Blitz3D\samples\mak\dragon\chorme-2.bmp" )
ScaleTexture tex,1.0/3,1.0/3
EntityTexture cube,tex
EntityAlpha cube,.4
EntityFX cube,1

;floor mirror
m=CreateMirror()

;simple light
light=CreateLight()
TurnEntity light,45,45,0
dragon=LoadMD2 ("D:\Program Files\Blitz3D\samples\mak\dragon\model\dragon.md2 ")
tex=LoadTexture("D:\Program Files\Blitz3D\samples\mak\dragon\model\dragon.bmp")
PositionEntity dragon,0,25,0

EntityTexture dragon,tex
AnimateMD2 dragon,1,0.05,90,130


;camera
camera=CreateCamera()

cam_xr#=30:cam_yr#=0:cam_zr#=0:cam_z#=-100
;--------------- fire--------------------
pivFire=CreatePivot()
Type fire
	Field x#
	Field y#
	Field z#
	Field alpha#
	Field entity
	Field scale#
End Type
sprite=LoadSprite("D:\Igra Dracula\sprite\fire.bmp",2,pivFire)
PositionEntity sprite,3,20,43
RotateEntity sprite,0,90,0
HideEntity sprite
con#=0.5:conx#=0.3:no=5
Md=4
;----------------------------------------
time=MilliSecs() 


While Not KeyDown(1)
;-------------- mod of fire------

If Md=1 Then
a1=117:b1=122
con#=1:conx#=0.8:no=5
PositionEntity sprite,3,20,43
RotateEntity sprite,0,90,0

End If
If Md=2 Then
a1=125 :b1=127
con#=0.4:conx#=0.4:no=1
PositionEntity sprite,-18,10,23

End If
If Md=3 Then
a1=100 :b1=102
con#=0.4:conx#=0.3:no=3
PositionEntity sprite,-3,3,33

End If
If Md=4 Then
a1=90 :b1=93
con#=1:conx#=1:no=7
PositionEntity sprite,10,15,43
RotateEntity sprite,0,75,0
End If
;--------------- fire --------------------
	u=u+1
	If u>360 Then u=1
	Repeat
		elapsed=MilliSecs()-time
	Until elapsed>0
	
	time=time+elapsed
	dt#=elapsed*60.0/1000.0
	anim=MD2AnimTime(dragon)
	If anim>129 Then
	Md=4
	End If
	If anim>93 And anim<100 Then 
	Md=3
	End If
	If anim>100  And anim<122 Then 
	Md=1
	End If
	

	If anim>122  And anim<127 Then 
	Md=2
	End If


If anim>a1 And anim<b1
For i=1 To no
f.fire=New fire
f\alpha#=1;Rnd(0,1)

f\scale#=con#*Rnd(8,12)

f\entity=CopyEntity (sprite,piv1)
SpriteViewMode f\entity,1
RotateSprite f\entity,Rnd(360)
num=mum+1
Next
End If

For f.fire=Each fire
	f\alpha#=f\alpha#-0.02
	;f\scale#=f\scale#-Rnd(0.02,0.1)
	
		If f\alpha>0
			EntityAlpha f\entity,f\alpha
			If f\alpha#<0.4Then 
			EntityColor f\entity,80,200,150
			f\x#=con#*conx#*3
			f\y#=con#*Rnd(2.4,6.4)
			f\z#=con#*Rnd(-4.8,4.8)
			ElseIf f\alpha#<0.8 And f\alpha#>0.4Then
			f\scale#=con#*Rnd(10,14)
			f\x#=con#*conx#*4;Rnd(1.9,2)
			f\y#=con#*Rnd(-6,6)
			f\z#=con#*Rnd(-6,6)
			Else
			f\scale#=con#*4
			f\x#=con#*conx#*Rnd(2,4)
			f\y#=con#*Rnd(-2,2)
			f\z#=con#*Rnd(-2,2)
		EndIf
			ScaleSprite f\entity,f\scale#,f\scale#
			MoveEntity f\entity,f\x,f\y,f\z
			
		Else
			FreeEntity f\entity
			Delete f
			num=num-1
		EndIf
Next
;--------------------------------------------
If KeyDown(17)WireFrame enable
If KeyDown(203)
		cam_yr=cam_yr-2
	Else If KeyDown(205)
		cam_yr=cam_yr+2
	EndIf
	
	If KeyDown(200)
		cam_xr=cam_xr+2
		If cam_xr>90 cam_xr=90
	Else If KeyDown(208)
		cam_xr=cam_xr-2
		If cam_xr<5 cam_xr=5
	EndIf
	
	If KeyDown(26)
		cam_zr=cam_zr+2
	Else If KeyDown(27)
		cam_zr=cam_zr-2
	EndIf
	
	If KeyDown(30)
		cam_z=cam_z+1:If cam_z>-10 cam_z=-10
	Else If KeyDown(44)
		cam_z=cam_z-1:If cam_z<-180 cam_z=-180
	EndIf
	
	PositionEntity camera,0,0,0
	RotateEntity camera,cam_xr,cam_yr,cam_zr
	MoveEntity camera,0,0,cam_z
If KeyDown(16) surf=GetSurface (dragon,1)
If KeyDown(78) Then
x=x+1
If x>CountVertices (surf) Then x=CountVertices(surf)
vert=True
End If
If vert=True Then
VertexX(surf,x)
VertexY(surf,x)
VertexZ(surf,x)
VertexColor surf,a,255,0,0,1
EndIf 

UpdateWorld
RenderWorld
Flip

Wend
End
