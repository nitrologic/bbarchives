; ID: 1983
; Author: bradford6
; Date: 2007-04-07 16:17:49
; Title: miniB3D Cube picker
; Description: miniB3D version of my cube pick demo

Import sidesign.minib3d
AppTitle =  "miniB3D entity picker"

' 3d pick
grid=8 ' change To suit the speed of your machine
Local width:Int = 800
Local height:Int = 600
Graphics3D width , height
Local light:Tentity =CreateLight()
Local cam:Tentity =CreateCamera()

Type box
	Global List:TList = CreateList()
	Method New()
		ListAddLast(List , Self)
	End Method
	Field xpos#,ypos#,zpos#
	Field spinspeed#
	Field entity:Tentity
	Field alpha#,r,g,b
End Type


' this creates a cube full of cubes. If grid=4 Then 4x4x4 = 64 cubes Or (8x8x8 = 512 cubes) Try different values
For x= 1 To grid 
	For y = 1 To grid
		For z = 1 To grid
			b:box=New box 'adds a New box Type To the pointer b (Or something like that, I'm still learning this stuff)
			b.entity = CreateCube()
				EntityPickMode b.entity , 3
				EntityBox(b.entity, -1,-1,-1, 2,2,2)
				b.xpos#=x*6
				b.ypos#=y*6
				b.zpos#=z*6
	
				rc=Rnd(10,200)
				gc=Rnd(10,205)
				bc=Rnd(10,205)
				EntityColor b.entity,rc,gc,bc
				EntityAlpha b.entity,.9
				PositionEntity b.entity,b.xpos#,b.ypos#,b.zpos#
		Next
	Next
Next

MoveEntity cam,10,10,-4
 
Repeat
' camera code

If MouseX()<width*0.20 Then camturnx#:+.01 ' TurnEntity cam,0,1,0
If MouseY()<height*0.20 Then camturny#:-.01
If MouseX()>width - (width*0.20) Then camturnx#:-.01
If MouseY()>height - (height*0.20) Then camturny#:+.01
camturnx#=camturnx#*.99 ' fricion To slow the turning camera down To a stop
camturny#=camturny#*.99
TurnEntity cam,camturny#,camturnx#,0



If MouseDown(1) Then camspeed#=camspeed#+.001
If MouseDown(2) Then camspeed#=camspeed#-.001
camspeed#=camspeed#*.98 ' friction To slow the movement of the cam
MoveEntity cam,0,0,camspeed#

' run the pick test
pictentity:tentity = CameraPick ( Tcamera(cam),MouseX(),MouseY()) 

For b:box=EachIn box.List ' cycle thru all TYPES
	If PickedEntity()=b.entity Then b.spinspeed#=b.spinspeed#+.2
	b.spinspeed#=b.spinspeed#*.99
	TurnEntity b.entity,0,b.spinspeed#,0 ' update all entities
Next 

UpdateWorld
RenderWorld

Flip

Until KeyHit(KEY_ESCAPE)=1
