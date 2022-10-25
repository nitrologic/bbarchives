; ID: 1107
; Author: Barliesque
; Date: 2004-07-15 03:37:30
; Title: Lens Flares
; Description: Beautiful lens flares.  Complete with flare images and demo.

;=======================================================================================
;  "SUN FLARES"
;  by David Barlia (a.k.a. Barliesque)
;  david[at]barliesque[dot]com
;
;  Please feel free to make use of this code as you wish.
;  If you do use it in a project, a little credit would be nice.
;  Also, if you should happen to make some exciting improvements,
;  don't hesitate to post your changes (or alternate flares image?)
;  to the code archives.
;
;  This code makes use of a modified version of "Sprite COntrol" by SyntaxError.
;  If you are already using "Sprite Control" in your project, and want to add
;  this code, you will probably have no problem changing to this modified version.
;  See "Sprite Control.bb" for further info.
;
;=======================================================================================

AppTitle = "Sun Flares Demo"
HidePointer


graphics3D 640,480,32,1


Include "Sprite Control.bb"


Global camera, player
Global cam_pitch#, cam_yaw#
Global mvx#,mvy#,mvz#,targetpitch#,targetyaw#

Global playerheight#=32		;height of collision sphere

Global FrameTime, FPS#=80.0

;-------------------------
; Set up the Camera/Player
;-------------------------
setbuffer Backbuffer()
player=CreatePivot()
camera=CreateCamera(player)
RotateEntity player,0,180,0
MoveEntity camera,0,playerheight,0

camerarange camera,1,5000
CameraFogMode camera,0
CameraFogColor camera,210,200,150
CameraFogRange camera,200,1000
AmbientLight 35,30,40

CameraClsMode camera,True,True

ClearTextureFilters

MoveMouse GraphicsWidth()/2,GraphicsHeight()/2


;-------------------------
; Set up Sprite Control
; and our flares
;-------------------------
Global ViewX=GraphicsWidth(),ViewY=GraphicsHeight()
Global ViewAspect# = float(viewx)/float(viewy)

global FlareRed, FlareGreen, FlareBlue
dim Flare(15)

spritecamera = camera
spritepivot = CreateSpritePivot(spritecamera,1.01)
SetupFlares("Media\lens-flares.jpg")


;-------------------------
;  Load the Scene
;-------------------------
Scene = LoadAnimMesh("Media\Scene.b3d")
Sun = FindChild(Scene,"Sun")
Sky = FindChild(Scene,"Sky")
SkyTurn# = 0.0


;----------------------------------------------
;  Set PickModes...
;    Use 1 for flare sources
;    Use 2 for objects that can block the sun
;----------------------------------------------
restore PickSettings
repeat
		read ChildName$, PickMode
		if ChildName<>"" then EntityPickMode FindChild(scene,ChildName$),PickMode
until ChildName=""



.PickSettings
data "Sun", 1
data "Landscape", 2
data "Stand 1 Upper", 2
data "Stand 1 Lower", 2
;data "Stand 1 Frame", 2
data "Stand 2 Upper", 2
data "Stand 2 Lower", 2
;data "Stand 2 Frame", 2
data "Stand 3 Upper", 2
data "Stand 3 Lower", 2
;data "Stand 3 Frame", 2
data "Stand 4 Upper", 2
data "Stand 4 Lower", 2
;data "Stand 4 Frame", 2
data "", 0



;MAIN LOOP * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

While Not KeyHit(1)
	PlayerControls()

	rate# = RatePerFrame#(0.0005, 240.0)	
	skyTurn = SkyTurn + rate
	if SkyTurn>=360.0 then skyTurn=SkyTurn-360.0
	TurnEntity Sky,0,SkyTurn,0

	PointEntity Sun, Camera

	UpdateFlare(camera, Sun)

	Sync()

Wend

End

;* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

function Sync()

   UpdateWorld
	RenderWorld

	time = millisecs() - FrameTime
	FrameTime = millisecs()
	FPS = 1000.0/float(time)

	text 50,0,"FPS: " + FPS
	
	vwait : Flip false

end function


function RatePerFrame#(delta# = 1.0, secs# = 1.0)
	rate# = delta / float(secs * FPS)
	return rate#
end function

;* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *


Function PlayerControls()

	;***
	;***  Free Look
	;***

	mxspd# = MouseXSpeed()*0.25
	myspd# = MouseYSpeed()*0.25

	MoveMouse GraphicsWidth()/2,GraphicsHeight()/2

	targetpitch = targetpitch + myspd
	targetpitch = clampvaLUE(targetpitch, -85,85)
	targetyaw = targetyaw - mxspd	

	cam_pitch = cam_pitch + (targetpitch - cam_pitch)/8.0
	cam_yaw = cam_yaw + (targetyaw - cam_yaw)/8.0
	
	RotateEntity player,0,cam_yaw,0
	RotateEntity camera,cam_pitch,0,0

	;***
	;***  Movement
	;***
	
	If KeyDown(203) Then mvx=mvx-.25
	If KeyDown(205) Then mvx=mvx+.25
	If KeyDown(200) Then mvz=mvz+.25
	If KeyDown(208) Then mvz=mvz-.25
	
	If KeyDown(30) Then mvx=mvx-.25
	If KeyDown(32) Then mvx=mvx+.25
	If KeyDown(17) Then mvz=mvz+.25
	If KeyDown(31) Then mvz=mvz-.25
	
	TranslateEntity player,0,mvy,0
	MoveEntity player,mvx,0,mvz

	mvx=mvx/1.2
	mvy=mvy/1.2
	mvz=mvz/1.2
	
End Function


;-----------------------------------------------------

function ClampValue(Original#, low#, high#)
	if Original<low  then return low
	if Original>high then return high
	return Original
end function

;-----------------------------------------------------

Function UpdateFlare(cam_entity,source)

	cameraProject cam_entity,EntityX(source,True),EntityY(source,True),EntityZ(source,True)
	SourceX# = ProjectedX#()
	SourceY# = ProjectedY#()
	x# = SourceX/viewx
	y# = SourceY/viewy

	text 40,0,"X,Y:  " + X + ", " + Y

	SeeSource = camerapick(cam_entity,SourceX,SourceY)
	if ((SeeSource = Source) or (SeeSource = 0)) and entityinview(source,cam_entity) and (x>0 And x<=1) And (y>0 And y<=1)

      GetFlareColor(cam_entity, source, SourceX, SourceY)
      
		scale# = ViewX/800.0
		restore FlareData
		read TotalFlares		
		for f=1 to TotalFlares
			read Distance, FlareSize, ColorInfluence#, Alpha#, Frame
			flare_x# = SourceX - (((x-0.5)*2.0)*Distance)
			flare_y# = SourceY - (((y-0.5)*2.0)*(Distance/ViewAspect))

			r = (ColorInfluence * FlareRed   + 255.0*(1.0-ColorInfluence))
			g = (ColorInfluence * FlareGreen + 255.0*(1.0-ColorInfluence))
			b = (ColorInfluence * FlareBlue  + 255.0*(1.0-ColorInfluence))
			entitycolor Flare(f), r,g,b



			FlareSize = FlareSize * scale * (x + y + (cos(Distance*0.45)/2.0) + 0.5)
   		ResizeImage3D Flare(f),FlareSize,FlareSize

			if lowest#(x,y)<0.1 then
				EntityAlpha Flare(f),Alpha * lowest#(x,y)/0.2
			else
				EntityAlpha Flare(f),Alpha
			endif

			DrawImage3D Flare(f),flare_x,flare_y,Frame
 	  		ShowEntity Flare(f)
		next
		
   else
 	   restore FlareData
 	   read TotalFlares
 	   for f=1 to TotalFlares
 	  		HideEntity Flare(f)
	   next

   endif

End Function



; FLARE DATA:   Distance, FlareSize, ColorInfluence#, Alpha#, Frame
;
; Distance       - Maximum offset from source
; FlareSize      - Maximum size of flare
; ColorInfluence - How strongly colour of source affects the flare's colour
; Alpha          - Maximum alpha level (0.0 to 1.0)
; Frame          - Frame of the lens flare texture (1 to 16)

.FlareData
Data 15		
Data -100, 400, 1.00, 0.40,  1  ;Red Crescent (50% to 0%)
Data  -95,  35, 0.80, 0.40,  2  ;Orange/Yellow Gradient (80% to 0%)
Data    0, 130, 0.60, 1.00,  3  ;Bright Flare Center (100%)
Data   95,  35, 0.35, 0.50,  4  ;Purple Disc (60%)
Data  150,  45, 0.50, 0.30,  5  ;Blue Disc (60%)
Data  120,  70, 0.60, 0.40,  6  ;Blue Gradient (50% to 20%)
Data  200,  20, 0.90, 0.40,  7  ;Orange Disc (80%)
Data  250,   8, 0.10, 1.00, 15  ;Sharp point (100%)
Data  280,  15, 0.15, 1.00,  8  ;Fuzzy Star (100%)
Data  345,  80, 0.90, 0.20,  7  ;Orange Disc (50%)
Data  390,  60, 0.80, 0.60,  7  ;Orange Disc (80%)
Data  395,  30, 0.40, 0.40,  9  ;Green Disc (80%)
Data  460, 100, 0.90, 0.40, 10  ;Orange Gradiant (90% to 0%)
Data  550, 160, 0.70, 0.50, 11  ;Yellow Ring (90%) with Green Gradient (60% to 0%)
Data  800, 350, 0.80, 0.30, 12  ;Rainbow Halo (outside to in:  Red,Orange/Yellow,Violet) (50%)

;-----------------------------------------------------------

function SetupFlares(filename$)

	FirstFlare = LoadAnimImage3D(filename$,4,4,1)
	restore FlareData
	read TotalFlares
	for i=1 to TotalFlares
 		 read Distance, FlareSize, ColorInfluence#, Alpha#, Frame

		 Flare(i) = CopyImage3D(FirstFlare)
		
		 ResizeImage3D Flare(i),FlareSize,FlareSize
		 EntityAlpha Flare(i),Alpha
		 EntityBlend Flare(i),3
		 EntityOrder Flare(i),-100-i
		 midhandle3D Flare(i)
		 entitycolor Flare(i),255,255,255
		 HideEntity Flare(i)
	next
	FreeImage3D(FirstFlare)

end function

;-----------------------------------------------------------

function GetFlareColor(cam_entity, source, SourceX, SourceY)

	;  This function is responsible for the majority of
	;  resource drain.  For just the sun, it may be an acceptable
	;  slowdown.  For things like candles and indoor lights
	;  this feature should probably not be used.

	CameraProjMode cam_entity, 0

	sample_cam = createcamera()

	positionentity sample_cam, EntityX(player,true),playerheight,EntityZ(player,true)
	pointentity sample_cam, source
	CameraProjMode sample_cam, 2
	
	if SourceX<0 then SourceX=0
	if SourceY<0 then SourceY=0
	cameraviewport sample_cam, SourceX, SourceY, 1,1
	
	for f=1 to 15 : hideentity Flare(f) : next
					
	updateworld
	RenderWorld
	
	getcolor SourceX,SourceY
	FlareRed   = ColorRed()
	FlareGreen = ColorGreen()
	FlareBlue  = ColorBlue()
	color 255,255,255

	CameraProjMode cam_entity, 1
	FreeEntity sample_cam
	
end function


;-----------------------------------------------------------

function lowest#(val1#, val2#)

   if val1<val2 then
		return val1
   else
      return val2
	endif
	
end function
