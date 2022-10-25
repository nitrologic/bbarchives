; ID: 557
; Author: Chroma
; Date: 2003-01-23 12:14:46
; Title: Animated Texture FrameRate Control
; Description: This will allow you to set the real framerate you want your animated texture to run at.

;// CPU independent Animated Texture FrameRate Control
;// by Chroma

;// This routine will let you specify the framerate that your
;// animated textures will run at no matter the speed of the users CPU

Graphics3D 800,600,32,2
SetBuffer BackBuffer()
AppTitle "Animated Texture Frame Control"


camera=CreateCamera()
PositionEntity camera,0,0,-5
light=CreateLight()

;// Animtated Texture Settings
Anim_FPS=10
Anim_StartFrame=0
Anim_EndFrame=15

;// Create Sprite/Load Anim Texture
firetex=LoadAnimTexture("fire.png",1,64,64,0,16)
mysprite=CreateSprite()
EntityTexture mysprite,firetex,0


;// Store Initial Time
NewTime=MilliSecs()
OldTime=NewTime


;// Main Loop //
While Not KeyHit(1)
Cls


;// Delta and FPS Calc
NewTime = MilliSecs()	
delta# = Float (NewTime - OldTime) / 1000
OldTime = NewTime
fps = 1.0 / delta#


;//-=Anim FrameRate Control=-//
;Time Advance Calc - Ensures even frame advance even if there's an FPS dip
Time_Adv# = 1.0 / Anim_FPS 
;Advance Timer
Anim_Timer# = Anim_Timer# + delta#
;Frame Check
If Anim_Timer# > Time_Adv#
	Anim_Timer# = 0
	Anim_Frame = Anim_Frame + 1
	;Loop if at last frame
	If Anim_Frame > Anim_EndFrame Then Anim_Frame = Anim_StartFrame
	;Apply Texture 
	EntityTexture mysprite,firetex,Anim_Frame
EndIf
;//-========================-//


;// Interactive Frame Control (Temp)
If KeyHit(200) Then Anim_FPS = Anim_FPS + 1
If KeyHit(208) Then Anim_FPS = Anim_FPS - (1 And Anim_FPS > 1)


RenderWorld
UpdateWorld

;// Info
Text 5,5,"Time Delta:"+delta
Text 5,20,"Frame Rate:"+fps

Text 5,50,"Anim Tex Frame Rate:"+Anim_FPS
Text 5,65,"Current Anim Frame:"+Anim_Frame

Text 5,100,"Use Up and Down arrow keys to alter framerate."

Flip
Wend
