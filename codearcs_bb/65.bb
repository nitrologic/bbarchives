; ID: 65
; Author: bradford6
; Date: 2001-10-01 20:41:50
; Title: 3D mode Chooser
; Description: 3D graphics mode chooser from a window

; Part Tickle Engine (3D MODE CHOOSER) 

Graphics3D 640,480,0,2
Global cam=CreateCamera()
Global lite=CreateLight()

Global modes3d=CountGfxModes3D()
Global fobs=Sqr(modes3d)

Type fob3d
Field xpos#,ypos#,zpos#
Field entity
Field hilite
Field tex,spinspeed#
Field gmode

End Type



setgraphics("set graphicsmodes")



camera=CreateCamera()
light=CreateLight()
cube=CreateCube()

newtex=CreateTexture(256,256)
SetBuffer TextureBuffer(newtex)
For x=0 To 255
	
	For y=255 To 0 Step -1
		Color x,y/2,Rnd(x,y)
		Plot x,y


Next Next

EntityTexture(cube,newtex)


MoveEntity cube,0,0,3
dir=0
 Repeat
If dir=0
  Stp#=Stp#+.001
  If STP#=50 Then dir=1
EndIf
If dir=1
  Stp#=Stp#-.001
  If STP#=-50 Then dir=0
EndIf



s=s+Stp#
If s=360 Then s=0



TurnEntity cube,Sin(s),Sin(s),Cos(s)

UpdateWorld
RenderWorld
Flip
Until KeyHit(1)=1


; place code here

; please keep this open and improve it--let me know what you do

;  b_radford@yahoo.com
;



Function SetGraphics(title$)
AppTitle(title$)

SetBuffer BackBuffer()

backtex=CreateTexture(256,256)
SetBuffer TextureBuffer(backtex)
For x=0 To 255
	
	For y=255 To 0 Step -1
		Color x,y,Rnd(x,255)
		Plot x,y


Next Next

sky=CreateSphere()
EntityTexture(sky,backtex)
ScaleEntity sky,30,30,30
PositionEntity sky,fobs*2,fob*2,3
FlipMesh sky







cube=CreateCube()
MoveEntity cam,fobs*2,fobs*2,-5

For x=1 To fobs
For y = 1 To fobs
b.fob3d=New fob3d
b\xpos#=x*3
b\ypos#=y*3
b\zpos#=5
b\entity = CopyEntity(cube)
HideEntity(b\entity)
EntityPickMode(b\entity,3)
b\tex = CreateTexture(64,64)

PositionEntity b\entity,b\xpos#,b\ypos#,b\zpos#

Next
Next
; windowed mode entity
b.fob3d=New fob3d
b.fob3d=Last fob3d
b\xpos#=fobs*2
b\ypos#=fobs*2
b\zpos#=fobs
b\entity= CopyEntity(cube)
b\gmode=modes3d+1
EntityPickMode(b\entity,3)
b\tex = CreateTexture(64,64)
SetBuffer TextureBuffer(b\tex)

Text 0,0,"windowed"
Text 3,FontHeight(),"mode"
;ScaleEntity b\entity,8,8,8
;EntityBox b\entity,0,0,0,8,8,8
EntityTexture (b\entity,b\tex)
EntityColor (b\entity,Rnd(100,255),Rnd(100,255),Rnd(100,255))
PositionEntity b\entity,b\xpos#,b\ypos#,b\zpos#



; * * * * * * * * * * * *

hilited=CreateBrush(240,240,0)
BrushAlpha (hilited,.6)

SetBuffer BackBuffer()

b.fob3d = First fob3d
For x=1 To modes3d
b\gmode = x
SetBuffer TextureBuffer(b\tex)
r=Rnd(0,100) g=Rnd(0,100) bl=Rnd(0,100)



fntArial=LoadFont("Arial",22,False,False,False)
SetFont fntarial

ClsColor 255,255,255
Text 0,0,GfxModeWidth(x) 
Text 0,FontHeight(),GfxModeHeight(x)
Text 0,FontHeight()*2,GfxModeDepth(x)
ShowEntity(b\entity)
EntityTexture (b\entity,b\tex)
EntityColor (b\entity,Rnd(100,255),Rnd(100,255),Rnd(100,255))
b = After b
Next


  


Repeat

pictentity=CameraPick ( cam,MouseX(),MouseY()) 

For b.fob3d=Each fob3d ; cycle thru all TYPES
	If PickedEntity()=b\entity 
 		b\spinspeed#=b\spinspeed#+.2
			If MouseDown(1) Then modepicked=b\gmode 
					
	EndIf


b\spinspeed#=b\spinspeed#*.9
EntityAlpha b\entity,.8
TurnEntity b\entity,0,b\spinspeed#,0 

TurnEntity sky,0,-.01,0



Next

UpdateWorld
RenderWorld
Flip
Until modepicked>0
For b.fob3d=Each fob3d
FreeEntity b\entity
Next

FreeEntity cam
FreeEntity lite
FreeEntity cube



EndGraphics 
If modepicked=modes3d+1
	Graphics3D 640,480,0,3
Else
	Graphics3D GfxModeWidth(modepicked),GfxModeHeight(modepicked),GfxModeDepth(modepicked),1
EndIf

 
End Function


