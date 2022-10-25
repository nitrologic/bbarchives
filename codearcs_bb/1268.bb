; ID: 1268
; Author: RiverRatt
; Date: 2005-01-26 18:15:32
; Title: rpg_style_movement
; Description: Moves a entity by mouse on a plane like in Diablo. Mouse down 1 to move

Graphics3D 640,480
AutoMidHandle True 
SeedRnd(MilliSecs)
;; FPS
MFPS = 5
Period = 1000/MFPS
Time = MilliSecs()-Period

Global fps,timenext,frames
;player info_________________________________________________________________________________________
;____________________________________________________________________________________________________
Global player_mesh
;The players Pointer Location
Global marker_X#
Global marker_Y#
Global marker_Z#
player_mesh=CreateCube()
ScaleEntity player_mesh,2,2,2
EntityShininess player_mesh,.015
PositionEntity player_mesh,10,1,10
EntityOrder player_mesh,0
camera=CreateCamera()
CameraRange camera,35,150 

light=CreateLight(2)
PositionEntity light,0,130,500
LightRange light,6000

terra_size=16 ; initial size of terrain, and no. of grids segments, along each  side
x_scale=100 ; x scale of terrain
y_scale=1 ; y scale of terrain
z_scale=100 ; z scale of terrain

terra=CreateTerrain(terra_size)
ScaleEntity terra,x_scale,y_scale,z_scale
EntityPickMode terra,2
TerrainDetail terra,100,1 ; Set terrain detail, vertex morphing 
grid_tex=CreateTexture( 32,32,8 )
ScaleTexture grid_tex,.5,.5
SetBuffer TextureBuffer( grid_tex )
Color 0,0,64:Rect 0,0,32,32
Color 0,0,255:Rect 0,0,32,32,False
SetBuffer BackBuffer()
EntityTexture terra,grid_tex

Global marker=CreateSphere()
ScaleEntity marker,1,1,1
EntityColor marker,255,0,0

While Not KeyDown(1)
PointEntity light,player_mesh

;temperary move cam function
move_cam(camera)
;keep track of mouse position relative to visable grid function
track_mouse(marker,camera)
;move the player to a picked position 
update_player(player_mesh,marker,camera)










UpdateWorld
RenderWorld

If KeyHit(57)=True  m=m+1
If m=2 Then m=0
If m=1 Then 
	Text 0,40,"PickedX: "+PickedX#()
	Text 0,60,"PickedY: "+PickedY#()
	Text 0,80,"PickedZ: "+PickedZ#()
	Text 180,40,"marker_X: "+marker_X
	Text 180,60,"marker_Y: "+marker_Y
	Text 180,80,"marker_Z: "+marker_Z
	Text 0,100,"targetangle#"+targetangle#
	Text 0,120,"currentangle#"+currentangle#
	
EndIf 
Text 2,2,"FPS: "+GetFPS(False)

Flip False 
;VWait : Flip False
Wend

End

;camera movement
Function move_cam(camera)
	If KeyDown( 205 )=True Then TurnEntity camera,0,-1,0
	If KeyDown( 203 )=True Then TurnEntity camera,0,1,0
	If KeyDown( 208 )=True Then TurnEntity camera,0,0,-1
	If KeyDown( 200 )=True Then TurnEntity camera,0,0,1
End Function

;keep track of mouse position relative to visable grid function
Function track_mouse(marker,camera)
	marker_X#=Int(PickedX#())
	marker_Z#=Int(PickedZ#())
	marker_Y#=Int(PickedY#())
	CameraPick(camera,MouseX(),MouseY())
End Function

Function update_player(player_mesh,marker,camera)
	If MouseDown(1) Then 
		FlushKeys 
		PositionEntity marker,marker_X#,marker_Y#,marker_Z#
		;Playerwalking=1 
	;Else Playerwalking=0 
	EndIf
	
	If EntityDistance(player_mesh,marker)>5
		PointEntity player_mesh,marker
		MoveEntity player_mesh,0,0,2
		PositionEntity camera,EntityX(player_mesh)-22.5,EntityY(player_mesh)+65,EntityZ(player_mesh)-22.5 
		PointEntity camera,player_mesh
		;Playerwalking=1
		
	;Else Playerwalking=0 
	EndIf
End Function

Function GetFPS(JustChecking = False)
     If Not JustChecking Then frames = frames+1

     If MilliSecs() > timenext Then
          timenext = MilliSecs()+1000
          fps = frames
          frames = 0
     EndIf
     Return fps
End Function
