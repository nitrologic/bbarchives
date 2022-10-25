; ID: 2055
; Author: Diablo
; Date: 2007-07-06 10:22:22
; Title: Pre-Rendered backgrounds
; Description: Use this method when you want to mix 3d actors with 2d scenes

Rem

==========================================================
Pre-rendered Backgrounds
----------------------------------------------------------
This example shows how you can mix 3d objects into scenes
with 2d pre-rendered backgrounds.

Required:
One pre-rendered background
One simple 3d scene (low poly)
----------------------------------------------------------

End Rem
SuperStrict

' Framework + modules
Framework BRL.GLMax2D
Import sidesign.minib3d ' Uses simonh's awsome minib3d

' Collision consts
Const COLLISION_SCENE%			= 1
Const COLLISION_ACTOR%			= 2

' Render camera information
Const RENDERCAMERA_WIDTH%		= 1024
Const RENDERCAMERA_HEIGHT%		= 768

' Render image information
Const PRERENDER_WIDTH%			= 1024
Const PRERENDER_HEIGHT%			= 900

' Realtime camera information
Const CAMERA_WIDTH%				= 800
Const CAMERA_HEIGHT%			= 600

' Setup the 3d graphics
Graphics3D(CAMERA_WIDTH, CAMERA_HEIGHT, 32, 2)
SetMaskColor(255, 0, 255)

' Load the scene pre-rendered image (+ extra pointer image)
Local scene_prerender:TImage = LoadImage("media/scene.bmp")
Local pointer:TImage = LoadImage("media/pointer.bmp")
MidHandleImage(pointer)

' ** Create the 3d world **
Local camera:TCamera = CreateCamera()
Local camera_yscroll# = 0.0 ' This is used to scroll the camera for coolness

' Position the camera (make sure its in the same place used in rendering program)
PositionEntity(camera, 113.8, 300.3, 307)
RotateEntity(camera, 25.8, 148.2, 0)

' Load the scene (low-poly)
Local scene:TMesh = LoadMesh("media/scene.b3d")
EntityType(scene, COLLISION_SCENE)

' Create our actor
Local actor:TMesh = CreateCube()
EntityColor(actor, 0, 255, 0)
ScaleEntity(actor, 10, 10, 10)
MoveEntity(actor, 0, 10, 0)
EntityRadius(actor, 13)
EntityType(actor, COLLISION_ACTOR)

' Setup collisions
Collisions(COLLISION_ACTOR, COLLISION_SCENE, 4, 2)

' Main loop
Local running% = True

While (running = True)
	
	Cls()
	
	' Allow us to move the actor
	If (KeyDown(KEY_UP)) Then MoveEntity(actor, 0, 0, 1)
	If (KeyDown(KEY_DOWN)) Then MoveEntity(actor, 0, 0, -1)

	If (KeyDown(KEY_RIGHT)) Then TurnEntity(actor, 0, -3, 0)
	If (KeyDown(KEY_LEFT)) Then TurnEntity(actor, 0, 3, 0)
	
	' ** Actual cool stuff **
	' == STAGE 1 ==
	' First step is to render the occlusion scene
	' To do this we need to make sure we dont write to the color buffer, so...
	glColorMask(False, False, False, False)
	
	' Now we need to hide the actor so it doesnt mask itself
	HideEntity(actor)
	
	' Now we need to make sure we use the same aspect ratio of that which was used in rendering and also
	' it needs to be offset by the resolution difference
	CameraViewport(camera, (CAMERA_WIDTH - RENDERCAMERA_WIDTH), (CAMERA_HEIGHT - RENDERCAMERA_HEIGHT) + camera_yscroll#, RENDERCAMERA_WIDTH, RENDERCAMERA_HEIGHT)
	
	Begin3D()
	RenderWorld() ' Render pass one
	
	glColorMask(True, True, True, True)
	
	' == STAGE 2 ==
	' Now we render the 2d scene which is simple we just need to disable depth so it gets drawen in the back
	Begin2D()
	glDepthMask(False)
	DrawImage(scene_prerender, (CAMERA_WIDTH - PRERENDER_WIDTH), (CAMERA_HEIGHT - PRERENDER_HEIGHT) + camera_yscroll#)
	glDepthMask(True)

	' == STAGE 3 ==
	' The final stage is to render the actor - but we also need to render the collision scene for collision purposes
	' We dont want to clear ethier the color buffer or the z-buffer(because it will just draw the actor on top), so...
	CameraClsMode(camera, False, False)
	
	' Show the actor but *hide* the scene
	ShowEntity(actor)
	EntityAlpha(scene, 0)
	
	Begin3D()
	RenderWorld()
	UpdateWorld()
	
	EntityAlpha(scene, 1)
	
	CameraClsMode(camera, True, True)
	
	' == FINAL ==
	' Now we scroll the screen given the actors y position, of course there are better ways...
	camera_yscroll# = EntityY(actor)
	' *cough* gravity *cough*
	If (EntityY(actor) > 10) Then MoveEntity actor, 0, -1, 0
	
	' Draw a pointer
	Begin2D()
	CameraProject(camera, EntityX(actor), EntityY(actor), EntityZ(actor))
	DrawImage(pointer, (ProjectedX() + (CAMERA_WIDTH - RENDERCAMERA_WIDTH)), (ProjectedY() + ((CAMERA_HEIGHT - RENDERCAMERA_HEIGHT) + camera_yscroll#)) - (32 + (Sin(MilliSecs()) * 2)))
	
	Flip()
	
	If (AppTerminate() Or KeyHit(KEY_ESCAPE)) Then running = False
	
Wend

' Extra functions required - Code copied from klepto's minib3d extra module (which is awsome also)

Function Begin3D()

	glDisable(GL_TEXTURE_CUBE_MAP)
	glDisable(GL_TEXTURE_GEN_S)
	glDisable(GL_TEXTURE_GEN_T)
	glDisable(GL_TEXTURE_GEN_R)
	
	glDisable(GL_TEXTURE_2D)

	glEnable(GL_LIGHTING)
   	glEnable(GL_DEPTH_TEST)
	glEnable(GL_FOG)
	glEnable(GL_CULL_FACE)
	glEnable(GL_SCISSOR_TEST)
		
	glEnable(GL_NORMALIZE)
		
	glEnableClientState(GL_VERTEX_ARRAY)
	glEnableClientState(GL_COLOR_ARRAY)
	glEnableClientState(GL_NORMAL_ARRAY)

End Function

Function Begin2D()

	Local x%,y%,w%,h%
	GetViewport(x,y,w,h)
		
	glDisable(GL_LIGHTING)
	glDisable(GL_DEPTH_TEST)
	glDisable(GL_SCISSOR_TEST)
	glDisable(GL_FOG)
	glDisable(GL_CULL_FACE)

	glMatrixMode GL_TEXTURE
	glLoadIdentity
		
	glMatrixMode GL_PROJECTION
	glLoadIdentity
	glOrtho 0,GraphicsWidth(),GraphicsHeight(),0,-1,1
		
	glMatrixMode GL_MODELVIEW
	glLoadIdentity
		
	SetViewport x,y,w,h
			
	Local MaxTex:Int 
	glGetIntegerv(GL_MAX_TEXTURE_UNITS, Varptr(MaxTex))

		
	For Local Layer% = 0 Until MaxTex
		glActiveTexture(GL_TEXTURE0+Layer)
					
		glDisable(GL_TEXTURE_CUBE_MAP)
		glDisable(GL_TEXTURE_GEN_S)
		glDisable(GL_TEXTURE_GEN_T)
		glDisable(GL_TEXTURE_GEN_R)
	
		glDisable(GL_TEXTURE_2D)
	Next
		
	glActiveTexture(GL_TEXTURE0)
		
	DrawRect - 10 , - 10 , 5 , 5
		
	glViewport(0,0,TGlobal.Width,TGlobal.Height)
	glScissor(0,0,TGlobal.Width,TGlobal.Height)
	
	SetBlend(MASKBLEND)
		
End Function
