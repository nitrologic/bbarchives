; ID: 423
; Author: Rimmsy
; Date: 2002-09-09 18:18:03
; Title: Entity Recorder
; Description: Records an entity's position and rotation to playback

; Entity recorder

; by matt griffiths (Rims)
; use any way you like.
; any questions ca0mgr@hotmail.com

Const RECORD_FILE_VERSION$="1.00"

Graphics3D 640,480,0,2
SetBuffer BackBuffer()

Type trail
	Field ent,life
End Type

; required global
Global recordFile=0
Global RECORD_state=0

Global leave_a_trail=1 ; 0 for no trail, 1 for.

; for freeLook(cam)
Global FL_Pitch#,FL_Yaw#,FL_Roll#,FL_XSpeed#,FL_YSpeed#,FL_ZSpeed#
Global camera=CreateCamera()
	MoveEntity camera,0,3,-5
	
	
	
	
	
; just make some objects to test with
plane=CreatePlane()
	EntityColor plane,255,0,0
	t=CreateTexture(32,32) : SetBuffer TextureBuffer(t)
	For i=0 To 31 Step 16
		For j=0 To 31 Step 16
			Color i*120,i*120,i*120
			Rect i,j,32/3,32/3
		Next
	Next
	SetBuffer BackBuffer()
	EntityTexture plane,t
	FreeTexture t	
c1=CreateCube()
	EntityColor c1,255,255,0
	PositionEntity c1,5,1,5	
c2=CopyEntity(c1)
	EntityColor c2,255,0,255
	PositionEntity c2,-5,1,-5
s1=CreateSphere()
	EntityColor s1,90,90,145
	EntityAlpha s1,0.4
	PositionEntity s1,-5,1,5
s2=CopyEntity(s1)
	PositionEntity s2,5,1,-5
	EntityBlend s2,3
	

; timing
gameFPS = 30 
framePeriod = 1000 / gameFPS 
frameTime = MilliSecs () - framePeriod
	

	
	
	
	
While KeyHit(1)=0
	Repeat 
		frameElapsed = MilliSecs () - frameTime 
	Until frameElapsed 
	frameTicks = frameElapsed / framePeriod 
	frameTween# = Float (frameElapsed Mod framePeriod) / Float (framePeriod) 
	
	For frameLimit = 1 To frameTicks 
		If frameLimit = frameTicks Then CaptureWorld 
		frameTime = frameTime + framePeriod
		
		; update everything...
		If KeyHit(19) Then startRecording("recording.rec")	; R
		If KeyHit(25) Then startPlayBack("recording.rec")	; P
	
		If KeyHit(57) Then stopRecordingAndPlayback()	; Space
	
		If RECORD_state=1 ; are we recording?
			record(camera)
		ElseIf RECORD_state=2 ; oh, we're playing back a file
			playback(camera) ; change camera to any object handle
		EndIf
	
		; handles all trail left behind
		If leave_a_trail
			For h.trail=Each trail
				If h\life < 0
					; kill it
					FreeEntity h\ent
					Delete h
				Else
					h\life=h\life-1
					d#=(1.0/150.0)*Float(h\life) ; (current alpha is (1.0/Total Life)*current life)
					EntityAlpha h\ent,d
				EndIf
			Next
		EndIf
	
		FreeLook(Camera)
	
		UpdateWorld	
	Next
	
	RenderWorld	frametween
	
	Color 255,0,0
	If RECORD_state=1 Then Text 0,0,"RECORDING" ElseIf RECORD_state=2 Then Text 0,0,"PLAYBACK"
	
	Flip
Wend
End



Function record(ent)
	If ent=0 Then 
		DebugLog "Entity does not exist"
		Return	
	EndIf
	If recordFile=0 Then Return

	WriteFloat recordFile,EntityX(ent)
	WriteFloat recordFile,EntityY(ent)
	WriteFloat recordFile,EntityZ(ent)

	WriteFloat recordFile,EntityPitch(ent)
	WriteFloat recordFile,EntityYaw(ent)
	WriteFloat recordFile,EntityRoll(ent)
	
	If leave_a_trail
		If Rand(1,3)=1 ; only once every 3 cycles (on average)
			; optional... leaves a trail which fades out.
			t.trail=New trail
			t\ent=CreateCube()
			EntityColor t\ent,255,0,0
			ScaleEntity t\ent,0.5,0.5,0.5
			PositionEntity t\ent,EntityX(ent),EntityY(ent),EntityZ(ent)
			t\life=150
		EndIf
	EndIf
End Function


Function startRecording(file$)
	If recordFile <> 0 Then CloseFile recordFile
	recordFile=WriteFile(file)
	RECORD_state=1
	; write the recorder version
	WriteLine recordFile,RECORD_FILE_VERSION
End Function


Function startPlayBack(file$)
	recordFile=ReadFile(file)
	RECORD_state=2	
	; check the file and recorder versions
	r$=ReadLine(recordFile)
	If r <> RECORD_FILE_VERSION
		RuntimeError("Wrong file version (file: "+r+". Rrecorder: "+RECORD_FILE_VERSION+")")
		End
	EndIf
End Function


Function stopRecordingAndPlayback()
	If RECORD_state=1 Or RECORD_state=2 ; are we recording OR playing back?
		CloseFile recordFile
		RECORD_state=0
	EndIf	
End Function


Function playBack(entity)
	If entity=0 Then Return
	If recordFile=0 Then Return

	If Not Eof(recordFile)
		x#=ReadFloat(recordFile)
		y#=ReadFloat(recordFile)
		z#=ReadFloat(recordFile)
		
		PositionEntity entity,x,y,z
		
		New_pitch#=ReadFloat(recordFile)
		New_yaw#=ReadFloat(recordFile)
		New_roll#=ReadFloat(recordFile)
		
		RotateEntity entity,new_pitch,new_yaw,new_roll

		If leave_a_trail
			If Rand(1,3)=1 ; only once every 3 cycles on average
				; optional... leaves a trail which fades out.
				t.trail=New trail
				t\ent=CreateCube()
				EntityColor t\ent,255,0,0
				ScaleEntity t\ent,0.5,0.5,0.5
				PositionEntity t\ent,EntityX(entity),EntityY(entity),EntityZ(entity)
				t\life=150
			EndIf
		EndIf		
	Else
		CloseFile(recordFile)				
		RECORD_state=0
	EndIf	
End Function




; FreeLook function by Syntax Error
Function FreeLook(FL_Cam)
		FL_Pitch#=FL_Pitch#-(-MouseYSpeed()*0.02) : FL_Pitch#=FL_Pitch#/1.2
    FL_Yaw#=FL_Yaw#+-(MouseXSpeed()*0.02) : FL_Yaw#=FL_Yaw#/1.2
    MoveMouse (GraphicsWidth()/2,GraphicsHeight()/2)
    FL_ZSpeed#=FL_ZSpeed#+Float(KeyDown(17)-KeyDown(31))*0.12 : FL_ZSpeed#=FL_ZSpeed#/1.14;  w & s
		FL_XSpeed#=FL_XSpeed#+Float(KeyDown(32)-KeyDown(30))*0.12 : FL_XSpeed#=FL_XSpeed#/1.14 ; a & d
		FL_YSpeed#=FL_YSpeed#+Float(KeyDown(19)-KeyDown(33))*0.12 : FL_YSpeed#=FL_YSpeed#/1.14 ; r & f
		FL_Roll#=(FL_Yaw#*1.1)-(FL_XSpeed#*2.3)
    MoveEntity FL_Cam,FL_XSpeed#,FL_YSpeed#+Abs(FL_Roll#*FL_XSpeed#)/50,FL_ZSpeed#
		Local cp#=EntityPitch(FL_Cam,True)+FL_Pitch#
		If cp<-89 Then cp=-89
		If cp>89 Then cp=89
		RotateEntity FL_Cam,cp,EntityYaw(FL_Cam)+FL_Yaw#,FL_Roll#
End Function
