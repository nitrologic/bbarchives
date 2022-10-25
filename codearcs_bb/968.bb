; ID: 968
; Author: puki
; Date: 2004-03-15 08:16:48
; Title: Waypoints
; Description: Waypoints in Blitz3D

; mini Sausage Dweller - puki - 2004

; while developing 'Sausage Dweller' I decided to drop this out as it may be useful in helping
; some people understand waypoints - some of the code that you will have seen written by other
; people can often be very 'optimised' for efficiency and therefore difficult to understand what
; is going on or how it is being done - the code can just appear to do it.
; For example: Simon Harrison's 'FPS' demo sample - His 'Function GargPath()' may be a bit hard to
; understand (not that there is anything wrong with it).  It is very worth checking it out in your
; Samples directory.

; this code is just a simple waypoint system and it is aimed at new users of Bitz3D
; you wouldn't necessarily handle the data, the way I have done, if you where doing a large,
; complex project - I have done it this way to make it easy to see how the bot is choosing its
; next path.

; Hopefully, this makes sense to those people who may have found this sort of stuff difficult to
; understand - that is the reason why I did it.
 
; ps - it seems to be a bit bugged and will occasionaly behave odd - especially if you increase
; the speed of the bot - sometimes the bot will shoot up into the air!

; However, bare in mind that there is no actual AI or pathfinding going on.

; maybe soon, I'll drop out my multiple door system from 'Sausage Dweller'.


Graphics3D 800,600; change this to whatever you want
HidePointer

walkspeed#=.1; speed at which the camera/player moves at - originally set to .1 (just in case you changed it)
no_of_data=11; number of lines of data in mapdata
botmode=1; means the bot is able to move - 0 means it is static
bot$="moving"

SeedRnd MilliSecs(); this is vital for random numbers (helps the bot choose more randomly)

;create the ground
floor1=CreatePlane()
EntityColor floor1,50,255,50
PositionEntity floor1,0,-2.3,0 
EntityType floor1,2

;create camera/player
Global player=CreatePivot()
Global camera=CreateCamera(player)
CameraRange camera,1,100
CameraClsColor camera,50,50,200; colour the sky
PositionEntity player,5,1,-20
EntityType player,1

;create a 'bot'
bot=CreateCube()
EntityAlpha bot,.9
ScaleEntity bot,1,3.5,1
PositionEntity bot,0,-2,5

wallheight#=2.5
;create wall block
block=CreateCube()
ScaleEntity block,2.5,wallheight,2.5
EntityColor block,100,100,100
EntityAlpha block,.3
EntityType block,2
HideEntity block

;create waypoint entity - the little red domes in the ground
wayp=CreateSphere()
EntityColor wayp,255,0,0
EntityAlpha wayp,0.5
HideEntity wayp


; note: the map data and the maze you visually see when running the program are flipped and mirrored
; To keep the code simple, I haven't corrected this - it doesn't really matter
;
; 11    12  13  14

;        8   9  10

; 4      5   6   7

;

; 1      2       3

; ^entrance

.mapdata
Data "01000000000"
Data "03111311130"
Data "01000100010"
Data "01000100010"
Data "01000100010"
Data "03111313130"
Data "01000101010"
Data "01000313130"
Data "01000101010"
Data "03111313130"
Data "00000000000"

; this is where the decisons are made from - each node (1-14) has the possible neighbouring nodes
; precalculated - this is ideal for what we are doing here, but obviously a bit of a pain to do
; if you have hundreds of them.  'Sausage Dweller' does it (amongst other things) on the fly.
.nodedata
Data 1,4,2,0
Data 2,1,5,3,0
Data 3,7,2,0
Data 4,11,5,1,0
Data 5,4,8,6,2,0
Data 6,5,9,7,0
Data 7,6,10,3,0
Data 8,12,9,5,0
Data 9,8,13,10,6,0
Data 10,9,14,7,0
Data 11,12,4,0
Data 12,11,13,8,0
Data 13,12,14,9,0
Data 14,13,10,0

; Technically arrays begin at 0 - but let's forget that for the moment
; 'nodes' just holds the number of neighbouring waypoints/nodes
; basically, I have precalculated the logic
Dim nodes(14)
nodes(1)=2
nodes(2)=3
nodes(3)=2
nodes(4)=3
nodes(5)=4
nodes(6)=3
nodes(7)=3
nodes(8)=3
nodes(9)=4
nodes(10)=3
nodes(11)=2
nodes(12)=3
nodes(13)=3
nodes(14)=2

; 14 waypoints - maximum of 4 neighbouring links per waypoint
; 'node' holds the choices the bot can make
; basically, I have precalculated the logic
Dim node(14,4)
node(1,1)=4
node(1,2)=2
node(2,1)=1
node(2,2)=5
node(2,3)=3
node(3,1)=7
node(3,2)=2
node(4,1)=11
node(4,2)=5
node(4,3)=1
node(5,1)=4
node(5,2)=8
node(5,3)=6
node(5,4)=2
node(6,1)=5
node(6,2)=9
node(6,3)=7
node(7,1)=6
node(7,2)=10
node(7,3)=3
node(8,1)=12
node(8,2)=9
node(8,3)=5
node(9,1)=8
node(9,2)=13
node(9,3)=10
node(9,4)=6
node(10,1)=9
node(10,2)=14
node(10,3)=7
node(11,1)=12
node(11,2)=4
node(12,1)=11
node(12,2)=13
node(12,3)=8
node(13,1)=12
node(13,2)=14
node(13,3)=9
node(14,1)=13
node(14,2)=10


;find total number of waypoints in map data - purely by searching
;ps. this isn't really necessary - you could just count them
number_of_waypoints=0
Restore mapdata
For c=0 To no_of_data-1
Read m$
For a=1 To 11
	If Mid$(m$,a,1)="3"
		number_of_waypoints=number_of_waypoints+1
	End If
Next
Next

;now we know the amount of waypoints, set up an array
Dim wpa(number_of_waypoints)

;draw the 2D maze data into a 3D world
Restore mapdata; find the block of data
number_of_blocks=0
wpc=0
For c=0 To no_of_data-1
Read m$
For a=1 To 11; loop through the data

	;draw the wall blocks
	If Mid$(m$,a,1)="0"
		newblock=CopyEntity(block); make a copy of the original entity
		PositionEntity newblock,xx,0.2,zz
		number_of_blocks=number_of_blocks+1; not necessary - just counts total number of blocks
	End If

	;drop-in the waypoints
	If Mid$(m$,a,1)="3"
		wpc=wpc+1
		wpa(wpc)=CopyEntity(wayp); make a copy of the original entity
		PositionEntity wpa(wpc),xx,-3,zz
	End If

;NOTE "1"'s are ignored, the are empty spaces - the values I have chosen are irrelevent

;this code block is used to space out the 'wall blocks'
x=x+1; a simple counter
xx=xx+5; increment the spacing
If x=11; maximum number of items (numbers 0-9) in data statment
	x=0; make it=0 as we are at the end of a line of data
	zz=zz+5; increment the spacing
	xx=0; move the location of the next 'wall block' back to position 0
EndIf

Next 
Next


;create the 'bot'
Type bot
Field botty
Field current_waypoint
End Type

b.bot=New bot
b\botty=bot
b\current_waypoint=1; starting waypoint that bot heads for

Collisions 1,2,2,3; stops you walking though the walls


; MAIN LOOP ***************************************************
While Not KeyHit(1)

pz=EntityZ(player)
px=EntityX(player)

Gosub MoveCamera
If botmode>0 Gosub movebot Else bot$="static"

UpdateWorld
RenderWorld

Text 15,15,"Number of waypoints in level: "+number_of_waypoints+" Number of wall blocks in level: "+number_of_blocks+" Triangles rendered: "+TrisRendered()
Text 15,30,"Player X: "+EntityX(player)+" Player Y: "+EntityY(player)+" Player Z:"+EntityZ(player)
Text 15,400,"mini Sausage Dweller - for Blitz3D users - by puki - 2004"
Text 15,445,"The bot is travelling from waypoint: "+las+" to waypoint: "+b\current_waypoint
Text 15,460,"The bot chose option "+decision+" out of "+choices+" options of where to head next - it is now heading for waypoint "+final
Text 15,475,"The bot is currently "+bot$+ " - Press 'SpaceBar' to toggle'"
Text 15,500,"NOTE: if the bot ping-pongs a few times between the same waypoints, it isn't a bug - it is just"
Text 15,515,"      doing it by chance based on the low number of choices it has to make (2-4)"
Flip
Wend
End
;---------------------------------------------------------------

; I could have used functions - but gosubs will suffice (no need to use 'Global' variables)
.MoveCamera
mx#=-.25*MouseXSpeed()
my#=.25*MouseYSpeed()
MoveMouse GraphicsWidth()/2,GraphicsHeight()/2

TurnEntity player,0,mx#,0,1
TurnEntity camera,my#,0,0,0

If KeyDown(200) Or KeyDown(17) Then MoveEntity player,0,0,walkspeed
If KeyDown(208) Or KeyDown(31) Then MoveEntity player,0,0,-walkspeed
If KeyDown(205) Or KeyDown(32) Then MoveEntity player,walkspeed,0,0
If KeyDown(203) Or KeyDown(30) Then MoveEntity player,-walkspeed,0,0
If KeyHit(57) Then botmode= Not botmode; flip botmode between 1 and 0 (moving and static)
Return
;---------------------------------------------------------------

;move the bot around
.movebot
bot$="moving"
current=b\current_waypoint; make current equal the intended waypoint

If EntityDistance(b\botty,wpa(b\current_waypoint))<2
		choices=nodes(current); get number of path choices for intended waypoint (2, 3 or 4)
		las=b\current_waypoint; store the waypoint arrived at as 'las' 
		decision=Int(Rnd(choices)); choose one of the paths
		If decision=0 Then decision=1; int(rnd(3) will result in 0, 1 or 2 - adding 1 makes the results 1, 2 or 3
		final=node(current,decision); find the path
		b\current_waypoint=final; make the current waypoint equal the decision
EndIf

; Note: I didn't design the following calculations - not sure who did:
; (I wish people would put their name in their code, not just in the thread they posted it in)
bx#=EntityX(wpa(b\current_waypoint))-EntityX(b\botty)
bz#=EntityZ(wpa(b\current_waypoint))-EntityZ(b\botty)
bn#=Sqr(bx^2+by^2+bz^2)
bx=bx/bn
bz=bz/bn
AlignToVector b\botty,bx,0,bz,0,.1; the 'rate' (.1) makes the bot visually turn
MoveEntity b\botty,0,0,.05; speed of the bot - originally set to .05 (just in case you change it)
Return


; NOTES:
; You could precalculate further with things like (all examples make the bot travel anti-clockwise
; around the maze):

;	If EntityDistance(b\botty,wpa(1))<2 Then b\current_waypoint=3
;	If EntityDistance(b\botty,wpa(3))<2 Then b\current_waypoint=14
;	If EntityDistance(b\botty,wpa(14))<2 Then b\current_waypoint=11
;	If EntityDistance(b\botty,wpa(11))<2 Then b\current_waypoint=1

; A better way would be to store certain paths the bot will take (there are various ways to do this):

; botai$(1)="3*14*11*1"
; data 3,14,11,1

; or, you could just design an AI system - you can still use precalculated stuff as well.
