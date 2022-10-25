; ID: 354
; Author: matt!
; Date: 2002-06-27 08:11:57
; Title: Replaying user input using a collection of Types 
; Description: Replaying user input using a collection of Types 

;
; REPLAYING USER INPUT USING A COLLECTION OF TYPES
; 23/24 June 2002, version 0.10, tracks user controlled element
; 26/27 June 2002, version 0.20, tracks multiple computer controlled elements
;
; to do: enable early quit of replay to go back to game
; 
; *** by Matt Sephton (matt@ewtoo.org)
; *** feel free to use this method as long as you give me credit
;
; this method can be used to replay user input. this simple demo records the player control of a dot.
;
; we choose to do the correct branch of the main loop (moving or drawing) depending on if we are 
; recording (game) or replaying (watching).
;
; this could be extended To keep track of multiple items, such as computer/AI controlled characters.
;

AppTitle "cursors to move, R to replay"

Type coord		;standard element type
	Field x,y
End Type

Type complex	;complex element type
	Field x,y
	Field r		;has additional properties
End Type

Type replay		;replay data type (notice all tracked properties are combined in this one type)
	Field x,y
	Field r
End Type

Const W = 320, H = 200		;screen dimensions

ball.coord = New coord		;position player controlled element at screen centre
ball\x = W/2
ball\y = H/2

ai.complex = New complex	;ai "player" #1
ai\x = 180
ai\y = 180
ai\r = 4

ai2.complex = New complex	;ai "player" #2
ai2\x = 0
ai2\y = 0
ai2\r = 4

Graphics W,H,16,2		;gfx mode, small 16-bit window
SetBuffer BackBuffer()	;set backbuffer
ClsColor 31,127,255		;cls blue

Global replay_counter = 0		;current buffer size (number of types in collection)
Global playback_pointer = 0		;where we are in the buffer at playback
Global replaying = False		;are we replaying data or not?

Const target_fps = 24			;so we can reuse this constant

Const total_objects = 3			;total number of objects to track
Const replay_length = 5			;

;max buffer size, limited only by memory size
Const max_buffer_size = total_objects * target_fps * replay_length

fps = CreateTimer(target_fps)	;limit drawing speed to 25fps

While Not KeyDown(1)
	Cls						;clear screen
	Color 255,255,255			;draw white

	If KeyDown(19) Then			;R = replay mode on
		Delay 200	;pause
		replaying = True			;flag that we're want to go through the main loop in replay mode
	EndIf
	
	If (replaying = False)			;record mode
		;move ball with cursor keys
		ball\x = ball\x - KeyDown(203) + KeyDown(205)	;left/right
		ball\y = ball\y - KeyDown(200) + KeyDown(208)	;up/down

		;ai
		For team.complex = Each complex
			team\x = (team\x - 2) Mod 360
			team\y = (team\y - 2) Mod 360
		Next

		;limit replay data to max_buffer_size by deletingg oldest data item
		If (replay_counter >= max_buffer_size)
			Delete First replay
			Delete First replay
			Delete First replay
			replay_counter = replay_counter - total_objects		;less data used
		EndIf

		;record new data item (player)
		replay.replay = New replay
		replay\x = ball\x
		replay\y = ball\y
		replay_counter = replay_counter + 1		;more data

		;record new data item (multiple ai)
		For team.complex = Each complex
			replay.replay = New replay
			replay\x = team\x
			replay\y = team\y
			replay_counter = replay_counter + 1		;more data
		Next
	
		Text 10,10,"R"+ replay_counter

	Else				;replay mode
		If (playback_pointer = 0) Then rd.replay = First replay	;rd = start of replay collection

		;draw replay cursor
		If (playback_pointer Mod target_fps > target_fps/2) Then Text W-20,10,"R"
		
		If (playback_pointer < replay_counter) Then			;still in replay data
			;set player draw position to be replay data
			ball\x = rd\x
			ball\y = rd\y
			rd = After rd			;go to next replay data item in collection
			playback_pointer = playback_pointer + 1

			For team.complex = Each complex
				team\x = rd\x
				team\y = rd\y
				rd = After rd							;go to next replay data item in collection
				playback_pointer = playback_pointer + 1	;move forward through replay data
			Next

		Else 												;end of the replay data
			playback_pointer = 0	;rewind pointer to beginning of replay data
			replaying = False		;turn off replay
			
			Delay 200
			SetBuffer FrontBuffer()	;show a message to make obvious the transition from replay to game
			Cls
			Text W/2,(H/2)-10,"RESUMING GAME",True,True
			Delay 800
			SetBuffer BackBuffer()
		EndIf

		Text 10,10,"@" + playback_pointer + "/" + max_buffer_size

		Delay 75	;slow motion replay!
	EndIf

	Rect ball\x, ball\y, 4,4,True

	Color 196,196,196				;draw paler grey
	Oval (W/2)-48, (H/2)-48, 100,100,False

	Color 255,255,0				;draw yellow
	For team.complex = Each complex
		Oval Sin(team\x)*50+(W/2), Cos(team\y)*50+(H/2), team\r,team\r,True
	Next

	k = WaitTimer(fps)			;limit drawing speed
	
	Flip
Wend

End
;
