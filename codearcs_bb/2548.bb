; ID: 2548
; Author: RGF
; Date: 2009-07-27 20:07:56
; Title: Distance between many entities
; Description: Calculate the distance between several entities, and choose the nearest one

;CODE BY WARNER, BOBYSAIT AND SAGITARIO

Graphics3D 800,600,16,2

cam=CreateCamera()
RotateEntity cam,90,0,0
PositionEntity cam,0,50,0

Type enemy
	Field x,y,z,mesh
	Field distancias[99]
	Field lowest
	Field loindex
End Type

Type bot
	Field x,y,z,mesh
	Field distancias[99]
	Field number
End Type

e.enemy=New enemy
e\x=5 : e\y=0 : e\z=5
e\mesh=CreateCube()
e\lowest=65536
e\loindex=-1
EntityColor e\mesh,255,0,0

e.enemy=New enemy
e\x=10 : e\y=0 : e\z=0
e\mesh=CreateCube()
e\lowest=65536
e\loindex=-1
EntityColor e\mesh,0,0,255

b.bot=New bot
b\x=-5 : b\y=0 : b\z=-5
b\mesh=CreateSphere()
b\number=1
EntityColor b\mesh,255,255,0

b.bot=New bot
b\x=5 : b\y=0 : b\z=15
b\mesh=CreateSphere()
b\number=2
EntityColor b\mesh,255,0,255

b.bot=New bot
b\x=15 : b\y=0 : b\z=5
b\mesh=CreateSphere()
b\number=3
EntityColor b\mesh,0,255,255

For e.enemy=Each enemy
	PositionEntity e\mesh,e\x,e\y,e\z
Next

For b.bot=Each bot
	PositionEntity b\mesh,b\x,b\y,b\z
Next

Repeat
For b.bot=Each bot	
If KeyDown(203)=True Then MoveEntity b\mesh,-.2,0,0
	
If KeyDown(205)=True Then MoveEntity b\mesh,+.2,0,0	

If KeyDown(200)=True Then MoveEntity b\mesh,0,0,+.2
	
If KeyDown(208)=True Then MoveEntity b\mesh,0,0,-.2
Next

For e.enemy = Each enemy
	e\lowest=65536
	e\loindex=-1
	i=0
	For b.bot = Each bot
		i=i+1
		If b\number=i
			e\distancias[i]=EntityDistance(b\mesh,e\mesh)
			If e\distancias[i]<e\lowest Then e\lowest = e\distancias[i] : e\loindex=i	
		EndIf
	Next
Next

RenderWorld()

Color 255,255,255
Text 0,550,"Use cursor keys to move spheres"

e.enemy = First enemy
Color 255,0,0
Text 0,10,"Red enemy"
Color 255,255,255
Text 0,20,"distance to 1: "+e\distancias[1]
Text 0,30,"distance to 2: "+e\distancias[2]
Text 0,40,"distance to 3: "+e\distancias[3]
If e\loindex=1 Then Color 255,255,0
If e\loindex=2 Then Color 255,0,255
If e\loindex=3 Then Color 0,255,255
Text 0,50,"nearest: "+e\loindex

e = After e
Color 0,0,255
Text 200,10,"Blue enemy"
Color 255,255,255
Text 200,20,"distance to 1: "+e\distancias[1]
Text 200,30,"distance to 2: "+e\distancias[2]
Text 200,40,"distance to 3: "+e\distancias[3]
If e\loindex=1 Then Color 255,255,0
If e\loindex=2 Then Color 255,0,255
If e\loindex=3 Then Color 0,255,255
Text 200,50,"nearest: "+e\loindex

Flip
Until KeyDown(1)
End
