; ID: 905
; Author: Techlord
; Date: 2004-02-03 12:46:56
; Title: Project PLASMA FPS 2004: Waypointer.bb
; Description: Waypoint Editing Utility

.ENGINE_INCLUDES

	Include "start.bb"
	Include "vector.bb"
	Include "math.bb"
	Include "stack.bb"
	Include "action.bb"
	Include "clock.bb"
	Include "queue.bb"
	Include "worker.bb"	
	Include "bot.bb"
	Include "camera.bb"
	Include "level.bb"
	Include "markerset.bb" ;waypointer

.ENGINE_START

	SeedRnd MilliSecs() 

	;Resolution Setup
	splash("Waypointer - Waypoint Editing Utility.","by Frankie 'Techlord' Taylor")
	
	;Camera Setup
	camera.camera=cameraCreate()

	;Scene Setup
	AmbientLight 255,255,255	

	;Collision Setup
	levelCollisionSet() ;level.bb
		
	;Floor Setup
	Global plane=CreatePlane()
	PositionEntity plane,0,-.5,0
	EntityColor plane,63,127,255
	
	;markerset setup
	markersetStart()
	
	;level Setup
	Global levelfilename$=Input("Load Level Map Name:")
	FlushKeys()
	Global level=levelLoad(levelfilename$);
	markersetOpen(levelfilename$)

	enginesync=CreateTimer(120)	
	
.ENGINE_MAIN_LOOP ;aka Main Loop

	enginesync=CreateTimer(120)
	While Not KeyHit(1)
	
		.ENGINE_SYSTEM_UPDATE
		
			WaitTimer(enginesync)
			If KeyHit(62) Delay 3000			
			
		.ENGINE_3D_UPDATE
		
			cameraUpdate()
			markersetUpdate()
	
			UpdateWorld()
			RenderWorld()
		
		.ENGINE_2D_UPDATE
	
			Color 255,255,255
			fontoffset%=FontHeight()+2
			Text 0,fontoffset%*0,"Guide Mode TRIs:"+Str(TrisRendered())+"/FPS:"+Str(curFPS)
			Text 0,fontoffset%*1,"Guide "+Str(markersetGuideWidth#)+","+Str(markersetGuideLength#)+","+Str(EntityX(markersetGuide))+","+Str(EntityY(markersetGuide))+","+Str(EntityZ(markersetGuide))+" F1-Help"

			If KeyDown(markersetkeymap(12)) markersetHelp()
				
			;FPS Timer Slip this code somewheres immediately before your Flip
			curTime = MilliSecs()
			If curTime > checkTime
				checkTime = curTime + 1000
				curFPS = fpscounter
				fpscounter = 0
			Else
				fpscounter = fpscounter + 1
			End If
			
			Flip()
	Wend

.ENGINE_STOP

	End

;==================================== JFK'S WAYPOINT RECORDER =====================================
Graphics3D 640,480,16,2
SetBuffer BackBuffer()
Collisions 1,2,2,3

player=CreatePivot()
camera=CreateCamera(player)
TranslateEntity camera,0,.25,0
CameraRange camera,.1,25
PositionEntity player,0,0.7,-1
EntityRadius player,0.3,0.6
EntityType player,1

mesh=LoadMesh( "pledit05/building/pasma1_test1d4.b3d" )
EntityFX mesh,1
EntityType mesh,2
sp#=.1
EntityAlpha mesh,.5
AmbientLight 255,255,255

maxrec=100000
Dim wp#(maxrec,2)
Dim isnode(maxrec)
wp#(0,0)=EntityX(player)
wp#(0,1)=EntityY(player)
wp#(0,2)=EntityZ(player)
wpcount=1


While Not KeyHit(1)

	CameraClsColor camera,255,127,63
  ; standard navigation
  mxs#=MouseXSpeed()/4
  mys#=MouseYSpeed()/4
  xa#=(xa#-mxs#)Mod 360
  ya#=(ya#+mys#)Mod 360

  If oldxa#<>xa#
   createnode=1
  Else
   createnode=0
  EndIf

  MoveMouse GraphicsWidth()/2,GraphicsHeight()/2
  RotateEntity camera,ya#,0,0
  RotateEntity player,0,xa#,0

  oldxa#=xa#
  If KeyDown(200) Then MoveEntity player,0,0,sp
  If KeyDown(208) Then MoveEntity player,0,0,-sp
	If KeyDown(30) MoveEntity player,0,sp,0
	If KeyDown(44) MoveEntity player,0,-sp,0
  If Not MouseDown(2) TranslateEntity player,0,-.05,0
	If KeyHit(57) recordwaypoints=1-recordwaypoints
  UpdateWorld()
  RenderWorld()
  Text 0,0,TrisRendered()
  Text 0,16,wpcount
  Flip

If recordwaypoints

  ; **** recording waypoints ****
  If createnode Then ; create node
   isnode(wpcount)=1
    c=CreateCube()
    EntityColor c,255,0,0
    PositionEntity c,wp#(wpcount,0),wp#(wpcount,1),wp#(wpcount,2)
    RotateEntity c,45,45,45
    ScaleEntity c,.05,.05,.05
    EntityFX c,1
    While KeyDown(59)
     Delay 1
    Wend
  EndIf
  ; record standard points
  xd#=Abs(wp#(wpcount,0)-EntityX(player))
  yd#=Abs(wp#(wpcount,1)-EntityY(player))
  zd#=Abs(wp#(wpcount,2)-EntityZ(player))
  curdist#=Sqr(xd#*xd# + yd#*yd# + zd#*zd#)
  If curdist#>=1.0 Then
   wpcount=wpcount+1
   If wpcount>maxrec Then
    End
   EndIf
   wp#(wpcount,0)=EntityX(player)
   wp#(wpcount,1)=EntityY(player)-.55;offset
   wp#(wpcount,2)=EntityZ(player)
    c=CreatePivot()
	HideEntity c
    PositionEntity c,wp#(wpcount,0),wp#(wpcount,1),wp#(wpcount,2)
  EndIf
EndIf
Wend

wr=WriteFile("waypoints.bin")
For i=0 To wpcount
 WriteFloat wr,wp#(i,0)
 WriteFloat wr,wp#(i,1)
 WriteFloat wr,wp#(i,2)
 WriteInt wr,isnode(i)
Next
CloseFile wr

End
