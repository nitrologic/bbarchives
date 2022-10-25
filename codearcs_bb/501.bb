; ID: 501
; Author: superqix
; Date: 2002-11-21 12:00:20
; Title: Entity Recorder
; Description: Record Enitities and save the actions to disk

; ****************************************************
; * Simple Entity Recorder by Michael Wilson
; ****************************************************
; * These functions provide a way To record Enitities
; * and save the actions to disk
; *
; * 4 Tracks are available to record to/from, but only
; * the first track increments the frame counter
; ****************************************************

; ******************************************
; * Released under the LPGL 11/21/2002 by
; * Michael Wilson wilson(at)no2games.com
; ******************************************

; ****************************************************
; * Sample useage:
; *
;	*	  If KeyHit(19) Then ; R = Record
; *     If Not EntityRecording Then RewindFrames()
; *     EntityRecording = 1 - EntityRecording
; *   EndIf
; *
;	*	  If KeyHit(25) Then ; P = Play
; *     If Not EntityPlayinging Then RewindFrames()
; *     EntityPlayinging = 1 - EntityPlayinging
; *   EndIf
; *
; *   [ ... ]
; *
; *   If EntityPlaying Then PlayEntity(Camera)
; *   UpdateWorld
; *   If EntityRecording Then RecordEntity(Camera)
; ****************************************************

Global EntityRecording = 0
Global EntityPlayinging = 0
Global CurrentFrame = 0
Const MaxRecordedFrames = 99999

Type RecorderType
	Field x0#,y0#,z0#,p0#,w0#,r0#
	Field x1#,y1#,z1#,p1#,w1#,r1#
	Field x2#,y2#,z2#,p2#,w2#,r2#
	Field x3#,y3#,z3#,p3#,w3#,r3#		
End Type

Dim Frame.RecorderType(MaxRecordedFrames)

Function RecordEntity(Entity,Track=0)
	If Entity=0 Then
		RuntimeError("No entity to record")
		Return
	EndIf
	Select Track
	Case 0
		CurrentFrame = CurrentFrame + 1
		Frame.RecorderType(CurrentFrame)=New RecorderType
		Frame(CurrentFrame)\x0# = EntityX(Entity,1)
		Frame(CurrentFrame)\y0# = EntityY(Entity,1)
		Frame(CurrentFrame)\z0# = EntityZ(Entity,1)
		Frame(CurrentFrame)\p0# = EntityPitch(Entity,1)
		Frame(CurrentFrame)\w0# = EntityYaw(Entity,1)
		Frame(CurrentFrame)\r0# = EntityRoll(Entity,1)
	Case 1
		Frame(CurrentFrame)\x1# = EntityX(Entity,1)
		Frame(CurrentFrame)\y1# = EntityY(Entity,1)
		Frame(CurrentFrame)\z1# = EntityZ(Entity,1)
		Frame(CurrentFrame)\p1# = EntityPitch(Entity,1)
		Frame(CurrentFrame)\w1# = EntityYaw(Entity,1)
		Frame(CurrentFrame)\r1# = EntityRoll(Entity,1)
	Case 2
		Frame(CurrentFrame)\x2# = EntityX(Entity,1)
		Frame(CurrentFrame)\y2# = EntityY(Entity,1)
		Frame(CurrentFrame)\z2# = EntityZ(Entity,1)
		Frame(CurrentFrame)\p2# = EntityPitch(Entity,1)
		Frame(CurrentFrame)\w2# = EntityYaw(Entity,1)
		Frame(CurrentFrame)\r2# = EntityRoll(Entity,1)
	Case 3
		Frame(CurrentFrame)\x3# = EntityX(Entity,1)
		Frame(CurrentFrame)\y3# = EntityY(Entity,1)
		Frame(CurrentFrame)\z3# = EntityZ(Entity,1)
		Frame(CurrentFrame)\p3# = EntityPitch(Entity,1)
		Frame(CurrentFrame)\w3# = EntityYaw(Entity,1)
		Frame(CurrentFrame)\r3# = EntityRoll(Entity,1)
	End Select
End Function

Function RewindFrames()
	CurrentFrame = 0
	Frame.RecorderType(CurrentFrame)=First RecorderType
End Function

Function PlayEntity(Entity,Track=0) ; Smooth=False
	If Entity=0 Then
		RuntimeError("No target entity for playback")
		Return
	EndIf
	If Frame(CurrentFrame+1) = Null Then RewindFrames()
	Select Track
	Case 0
    CurrentFrame = CurrentFrame + 1
		PositionEntity Entity,Frame(CurrentFrame)\x0,Frame(CurrentFrame)\y0,Frame(CurrentFrame)\z0,1
		RotateEntity Entity,Frame(CurrentFrame)\p0,Frame(CurrentFrame)\w0,Frame(CurrentFrame)\r0,1
	Case 1
		PositionEntity Entity,Frame(CurrentFrame)\x1,Frame(CurrentFrame)\y1,Frame(CurrentFrame)\z1,1
		RotateEntity Entity,Frame(CurrentFrame)\p1,Frame(CurrentFrame)\w1,Frame(CurrentFrame)\r1,1
	Case 2
		PositionEntity Entity,Frame(CurrentFrame)\x2,Frame(CurrentFrame)\y2,Frame(CurrentFrame)\z2,1
		RotateEntity Entity,Frame(CurrentFrame)\p2,Frame(CurrentFrame)\w2,Frame(CurrentFrame)\r2,1
	Case 3
		PositionEntity Entity,Frame(CurrentFrame)\x3,Frame(CurrentFrame)\y3,Frame(CurrentFrame)\z3,1
		RotateEntity Entity,Frame(CurrentFrame)\p3,Frame(CurrentFrame)\w3,Frame(CurrentFrame)\r3,1
	End Select
End Function

Function SaveRecording(Filename$,Track=0)
	RewindFrames()
	RecordFile=WriteFile(Filename$)
	While Frame(CurrentFrame+1) <> Null
		CurrentFrame = CurrentFrame + 1
  	Select Track
  	Case 0
  		WriteFloat RecordFile,Frame(CurrentFrame)\x0#
  		WriteFloat RecordFile,Frame(CurrentFrame)\y0#
  		WriteFloat RecordFile,Frame(CurrentFrame)\z0#
  		WriteFloat RecordFile,Frame(CurrentFrame)\p0#
  		WriteFloat RecordFile,Frame(CurrentFrame)\w0#
  		WriteFloat RecordFile,Frame(CurrentFrame)\r0#
  	Case 1
  		WriteFloat RecordFile,Frame(CurrentFrame)\x1#
  		WriteFloat RecordFile,Frame(CurrentFrame)\y1#
  		WriteFloat RecordFile,Frame(CurrentFrame)\z1#
  		WriteFloat RecordFile,Frame(CurrentFrame)\p1#
  		WriteFloat RecordFile,Frame(CurrentFrame)\w1#
  		WriteFloat RecordFile,Frame(CurrentFrame)\r1#
  	Case 2
  		WriteFloat RecordFile,Frame(CurrentFrame)\x2#
  		WriteFloat RecordFile,Frame(CurrentFrame)\y2#
  		WriteFloat RecordFile,Frame(CurrentFrame)\z2#
  		WriteFloat RecordFile,Frame(CurrentFrame)\p2#
  		WriteFloat RecordFile,Frame(CurrentFrame)\w2#
  		WriteFloat RecordFile,Frame(CurrentFrame)\r2#
  	Case 3
  		WriteFloat RecordFile,Frame(CurrentFrame)\x3#
  		WriteFloat RecordFile,Frame(CurrentFrame)\y3#
  		WriteFloat RecordFile,Frame(CurrentFrame)\z3#
  		WriteFloat RecordFile,Frame(CurrentFrame)\p3#
  		WriteFloat RecordFile,Frame(CurrentFrame)\w3#
  		WriteFloat RecordFile,Frame(CurrentFrame)\r3#
  	End Select
	Wend
	CloseFile RecordFile
  SavedFrames = CurrentFrame - 1
  RewindFrames()
	Return SavedFrames; DebugLog "Saved "+SavedFrames+" Frames"
End Function

Function LoadRecording(Filename$,Track=0)
	RewindFrames()
	RecordFile=ReadFile(Filename$)
	While Not Eof(RecordFile)
		CurrentFrame = CurrentFrame + 1
  	Select Track
  	Case 0
  		Frame.RecorderType(CurrentFrame)=New RecorderType
  		Frame(CurrentFrame)\x0# = ReadFloat(RecordFile)
  		Frame(CurrentFrame)\y0# = ReadFloat(RecordFile)
  		Frame(CurrentFrame)\z0# = ReadFloat(RecordFile)
  		Frame(CurrentFrame)\p0# = ReadFloat(RecordFile)
  		Frame(CurrentFrame)\w0# = ReadFloat(RecordFile)
  		Frame(CurrentFrame)\r0# = ReadFloat(RecordFile)
  	Case 1
  		Frame(CurrentFrame)\x1# = ReadFloat(RecordFile)
  		Frame(CurrentFrame)\y1# = ReadFloat(RecordFile)
  		Frame(CurrentFrame)\z1# = ReadFloat(RecordFile)
  		Frame(CurrentFrame)\p1# = ReadFloat(RecordFile)
  		Frame(CurrentFrame)\w1# = ReadFloat(RecordFile)
  		Frame(CurrentFrame)\r1# = ReadFloat(RecordFile)
  	Case 2
  		Frame(CurrentFrame)\x2# = ReadFloat(RecordFile)
  		Frame(CurrentFrame)\y2# = ReadFloat(RecordFile)
  		Frame(CurrentFrame)\z2# = ReadFloat(RecordFile)
  		Frame(CurrentFrame)\p2# = ReadFloat(RecordFile)
  		Frame(CurrentFrame)\w2# = ReadFloat(RecordFile)
  		Frame(CurrentFrame)\r2# = ReadFloat(RecordFile)
  	Case 3
  		Frame(CurrentFrame)\x3# = ReadFloat(RecordFile)
  		Frame(CurrentFrame)\y3# = ReadFloat(RecordFile)
  		Frame(CurrentFrame)\z3# = ReadFloat(RecordFile)
  		Frame(CurrentFrame)\p3# = ReadFloat(RecordFile)
  		Frame(CurrentFrame)\w3# = ReadFloat(RecordFile)
  		Frame(CurrentFrame)\r3# = ReadFloat(RecordFile)
  	End Select
	Wend
	CloseFile RecordFile
	ReadFrames = CurrentFrame - 1
  RewindFrames()
	Return ReadFrames; DebugLog "Read "+ReadFrames+" Frames"
End Function
