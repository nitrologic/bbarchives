; ID: 660
; Author: Ricky Smith
; Date: 2003-04-25 21:21:11
; Title: .b3d Animation using ExtractAnimSeq
; Description: An Example of using The ExtractAnimSeq Command

; ID: 660
; Author: Smiff
; Date: 2003-04-25 21:21:11
; Title: .b3d Animation using ExtractAnimSeq
; Description: An Example of using The ExtractAnimSeq Command

;**********************************************************************************
; 
;	B3D Animation Example using ExtractAnimSeq ------------------- Rik Smiff April 2003
;
;
;   Use Keys Q through to  X  to transpose  animation sequences.
;
;	Use the Cursor Keys to Pitch and Yaw the Ninjas Head 
;   - this will blend with the currently running animation
;   
;   You will need the B3D Animated Ninja Model "Ninja.b3d"
;   Kindly  donated by Psionic available at
;   
;   http://www.psionic3d.co.uk

;   Many Thanks to Psionic. 


;  Animation Frames
;
; 1-14	    Walk (normal) 
; 15-30	    Stealth Walk
; 32-44	    Punch And swipe sword
; 45-59	    Swipe And spin sword
; 60-68	    Overhead twohanded downswipe
; 69-72  	Up To block position (play backwards To Lower sword If you want)
; 73-83 	Forward kick
; 84-93	    Pick up from Floor (Or down To crouch at frame 87)
; 94-102	Jump
; 103-111	Jump without height (For programmer controlled jumps)
; 112-125	High jump To Sword Kill (Finish em off move??)
; 126-133	Side Kick
; 134-145	Spinning Sword attack (might wanna speed this up in game)
; 146-158	Backflip
; 159-165	Climb wall
; 166-173	Death 1 - Fall back onto ground
; 174-182	Death 2 - Fall forward onto ground
; 184-205	Idle 1 - Breathe heavily
; 206-250	Idle 2
; 251-300	Idle 3
;**********************************************************************************

Graphics3D 800,600
SetBuffer BackBuffer()
camera=CreateCamera()
MoveEntity camera,0,3,0
CameraViewport camera,0,0,GraphicsWidth(),GraphicsHeight()
AmbientLight 255,255,255

;********** Load Model  and Prepare Animation Sequences

Global ninja=LoadAnimMesh("ninja4.b3d"); This will load All Frames into Sequence 0
Global walk=ExtractAnimSeq (ninja,0,14); Extract frames 0 to 14 into Sequence 1 - the value 1 is assigned to the Global walk
Global stealth=ExtractAnimSeq (ninja,15,30); Extract frames 15 to 30 into Sequence 2 - the value 2 is assigned to the Global stealth
Global punch_swipe=ExtractAnimSeq (ninja,32,44); And so on extracting each set of frames into a seperate sequence 
Global swipe_spin=ExtractAnimSeq (ninja,45,59); and assigning the sequence number to a global variable.
Global overhead=ExtractAnimSeq (ninja,60,68)
Global block=ExtractAnimSeq (ninja,69,72)
Global fwd_kick=ExtractAnimSeq (ninja,73,83)
Global pickup=ExtractAnimSeq (ninja,84,93)
Global jump=ExtractAnimSeq (ninja,94,102)
Global jump_static=ExtractAnimSeq (ninja,103,111)
Global jump_kill_sword=ExtractAnimSeq (ninja,112,125)
Global sidekick=ExtractAnimSeq (ninja,126,133)
Global spin_sword_attack=ExtractAnimSeq (ninja,134,145)
Global backflip=ExtractAnimSeq (ninja,146,158)
Global climb_wall=ExtractAnimSeq (ninja,159,165)
Global death_fall_back=ExtractAnimSeq (ninja,166,173)
Global death_fall_fwd=ExtractAnimSeq (ninja,174,182)
Global idle_breathe_heavy=ExtractAnimSeq (ninja,184,205)
Global idle_2=ExtractAnimSeq (ninja,206,250)
Global idle_3=ExtractAnimSeq (ninja,251,300)
Global crouch=ExtractAnimSeq (ninja,84,87)
;
;locate the head bone in the model and assign it to the Global 'head'
Global head=FindChild(ninja,"Joint7")
 
;State values
Global s_walk=0
Global s_stealth=1

;Head rotation Variables
Global headyaw,headpitch
Global state
Const transpose = 40
;Position the model 
MoveEntity ninja,0,0,7
ScaleEntity ninja,0.5,0.5,0.5
RotateEntity ninja,0,180,0
;************** Main Loop ***********************************
While Not KeyHit(1)




; Get Cursor Key Input for Manual Head Rotation
If KeyDown(203) Then headyaw=headyaw+1:If headyaw > 90 Then headyaw = 90
If KeyDown(205) Then headyaw=headyaw-1: If headyaw < -90 Then headyaw = -90
If KeyDown(200) Then headpitch=headpitch+1: If headpitch > 30 Then headpitch= 30
If KeyDown(208) Then headpitch=headpitch-1 : If headpitch < -45 Then headpitch= -45


; Get Key Input for Animation sequence change
If KeyHit(16)  Then  Animate ninja,1,0.1,walk,transpose:state=s_walk
If KeyHit(17)  Then  Animate ninja,1,0.1,stealth,transpose:state=s_stealth
If KeyHit(18)  Then  Animate ninja,3,0.1,punch_swipe,transpose
If KeyHit(19)  Then  Animate ninja,3,0.1,swipe_spin,transpose
If KeyHit(20)  Then  Animate ninja,3,0.1,overhead,transpose
If KeyHit(21)  Then  Animate ninja,3,0.1,block,transpose
If KeyHit(22)  Then  Animate ninja,3,0.1,fwd_kick,transpose
If KeyHit(23)  Then  Animate ninja,3,0.1,pickup,transpose
If KeyHit(24)  Then  Animate ninja,3,0.1,jump,transpose
If KeyHit(25)  Then  Animate ninja,3,0.1,jump_static,transpose
If KeyHit(30)  Then  Animate ninja,3,0.1,jump_kill_sword,transpose
If KeyHit(31)  Then  Animate ninja,3,0.1,sidekick,transpose
If KeyHit(32)  Then  Animate ninja,3,0.1,spin_sword_attack,transpose
If KeyHit(33)  Then  Animate ninja,3,0.1,backflip,transpose
If KeyHit(34)  Then  Animate ninja,1,0.1,climb_wall,transpose
If KeyHit(35)  Then  Animate ninja,3,0.1,death_fall_back,transpose
If KeyHit(36)  Then  Animate ninja,3,0.1,death_fall_fwd,transpose
If KeyHit(37)  Then  Animate ninja,1,0.1,idle_breathe_heavy,transpose
If KeyHit(38)  Then  Animate ninja,1,0.1,idle_2,transpose
If KeyHit(45)  Then  Animate ninja,1,0.1,idle_3,transpose
If KeyHit(44)  Then  Animate ninja,3,0.1,crouch,transpose



If Not Animating(ninja) Then 

		If state=s_walk Then Animate ninja,1,0.1,walk,40
		If state=s_stealth Then  Animate ninja,1,0.1,stealth,40

End If


UpdateWorld

;Update head rotation
TurnEntity head,headpitch,headyaw,0

RenderWorld
Flip

Wend
