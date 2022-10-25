; ID: 138
; Author: bradford6
; Date: 2001-11-14 18:32:06
; Title: 3D Entity Pick
; Description: Pick several 3d objects with mouse 

; 3d pick
grid=4 ; change to suit the speed of your machine
Graphics3D 640,480,16,2
AppTitle "entity picker 3D"
light=CreateLight()
cam=CreateCamera()

Type box
Field xpos#,ypos#,zpos#
Field spinspeed#
Field entity
Field alpha#,r,g,b
End Type


box3d=CreateCube()
EntityPickMode box3d,3 ; IMPORTANT- make sure the entity is "pickable"
; this creates a cube full of cubes. if grid=4 then 4x4x4 = 64 cubes or (8x8x8 = 512 cubes) try different values
For x= 1 To grid 
For y = 1 To grid
For z = 1 To grid
b.box=New box ; adds a new box type to the pointer b (or something like that, I'm still learning this stuff)
b\entity = CopyEntity(box3d)
b\xpos#=x*4
b\ypos#=y*4
b\zpos#=z*4

rc=Rnd(10,200)
gc=Rnd(10,205)
bc=Rnd(10,205)
EntityColor b\entity,rc,gc,bc
EntityAlpha b\entity,.9
PositionEntity b\entity,b\xpos#,b\ypos#,b\zpos#
Next
Next
Next


MoveEntity cam,10,10,-4

HideEntity box3d 




Repeat
; camera code

If MouseX()<80 Then camturnx#=camturnx#-.01 ; TurnEntity cam,0,1,0
If MouseY()<80 Then camturny#=camturny#+.01
If MouseX()>580 Then camturnx#=camturnx#+.01
If MouseY()>400 Then camturny#=camturny#-.01
camturnx#=camturnx#*.98 ; fricion to slow the turning camera down to a stop
camturny#=camturny#*.98
TurnEntity cam,camturny#,camturnx#,0



If MouseDown(1) Then camspeed#=camspeed#+.001
If MouseDown(2) Then camspeed#=camspeed#-.001
camspeed#=camspeed#*.98 ; friction to slow the movement of the cam
MoveEntity cam,0,0,camspeed#

; run the pick test
pictentity=CameraPick ( cam,MouseX(),MouseY()) 
For b.box=Each box ; cycle thru all TYPES
If PickedEntity()=b\entity Then b\spinspeed#=b\spinspeed#+.2
b\spinspeed#=b\spinspeed#*.99
TurnEntity b\entity,0,b\spinspeed#,0 ; update all entities
Next 





UpdateWorld
RenderWorld
Text 0,0,("written by Bill Radford")
Flip

Until KeyHit(1)=1



