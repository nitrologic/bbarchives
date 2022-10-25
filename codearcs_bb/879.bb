; ID: 879
; Author: Techlord
; Date: 2004-01-07 13:57:46
; Title: Project PLASMA FPS 2004: Bot.bb
; Description: Bot Module

;============================
;WAYPOINT
;============================
Const WAYPOINT_MAX=2048
Dim waypointId.waypoint(WAYPOINT_MAX)
Global waypointIndex.stack=stackIndexCreate(WAYPOINT_MAX)
Global waypointAvail.stack=stackIndexCreate(WAYPOINT_MAX)
Global waypoint0.waypoint=New waypoint
Global waypoints%

Type waypoint
	Field id%
	Field typeid%
	Field parent.waypoint
	Field e% ;terrain cost
	Field f% ;fcost
	Field g% ;parent+cost
	Field h% ;heuristic manhattan
	Field state%
	Field position.vector
	Field entity
End Type

Function waypointStart()
	entity%=CreateCube()
	HideEntity entity%
	For loop = 1 To  WAYPOINT_MAX
		this.waypoint=waypointNew()
		this\entity%=CopyEntity(entity%)
		EntityPickMode this\entity,1,False 
		ScaleEntity this\entity%,.1,.1,.1
		HideEntity this\entity%
	Next
	DebugLog("Waypoint Initialized ["+Str(WAYPOINT_MAX)+"]")
End Function

Function waypointStop()
	For this.waypoint=Each waypoint
		waypointDelete(this)
	Next
End Function

Function waypointNew.waypoint()
	this.waypoint=New waypoint
	this\id%=0
	this\typeid%=0
	this\e%=0
	this\f%=0
	this\g%=0
	this\h%=0
	this\state%=0
	this\position.vector=vectorNew()
	this\id%=StackPop(waypointIndex.stack)
	waypointId(this\id)=this
	Return this
End Function

Function waypointDelete(this.waypoint)
	waypointId(this\id)=Null
	StackPush(waypointIndex.stack,this\id%)
	vectorDelete(this\position.vector)
	this\state%=0
	Delete this
End Function

Function waypointRead.waypoint(file)
	this.waypoint=New waypoint
	this\id%=ReadInt(file)
	this\typeid%=ReadInt(file)
	;this\parent.waypoint=waypointRead(file)
	this\e%=ReadInt(file)
	this\f%=ReadInt(file)
	this\g%=ReadInt(file)
	this\h%=ReadInt(file)
	this\state%=ReadByte(file)
	this\position.vector=vectorRead(file)
	Return this
End Function

Function waypointWrite(file,this.waypoint)
	WriteInt(file,this\id%)
	WriteInt(file,this\typeid%)
	;waypointWrite(file,this\parent.waypoint)
	WriteInt(file,this\e%)
	WriteInt(file,this\f%)
	WriteInt(file,this\g%)
	WriteInt(file,this\h%)
	WriteByte(file,this\state%)
	vectorWrite(file,this\position.vector)
End Function

Function waypointSave(filename$="Default")
	file=WriteFile(filename$+".waypoint")
	For this.waypoint= Each waypoint
		waypointWrite(file,this)
	Next
	CloseFile(file)
End Function

Function waypointOpen(filename$="Default")
	file=ReadFile(filename+".waypoint")
	Repeat
		waypointRead(file)
	Until Eof(file)
	CloseFile(file)
End Function

Function waypointCopy.waypoint(this.waypoint)
	copy.waypoint=New waypoint
	copy\id%=this\id%
	copy\typeid%=this\typeid%
	copy\parent.waypoint=waypointCopy(this\parent.waypoint)
	copy\e%=this\e%	
	copy\f%=this\f%
	copy\g%=this\g%
	copy\h%=this\h%
	copy\state%=this\state%
	copy\position.vector=vectorCopy(this\position.vector)
	Return copy
End Function

Function waypointMimic(mimic.waypoint,this.waypoint)
	mimic\id%=this\id%
	mimic\typeid%=this\typeid%
	waypointMimic(mimic\parent.waypoint,this\parent.waypoint)
	mimic\e%=this\e%	
	mimic\f%=this\f%
	mimic\g%=this\g%
	mimic\h%=this\h%
	mimic\state%=this\state%
	vectorMimic(mimic\position.vector,this\position.vector)
End Function

Function waypointCreate.waypoint(typeid%,parent.waypoint,g%,startx#,starty#,startz#,goalx#,goaly#,goalz#)
	this.waypoint=waypointNew()
	this\typeid%=typeid%
	this\parent.waypoint=parent
	this\position\x#=startx#
	this\position\y#=starty#
	this\position\z#=startz#
	this\g%=g%+this\parent\g%;find g
	this\h%=10*(Abs(goalx#-this\position\x#)+Abs(goalz#-this\position\z#));find h (manhattan)
	;this\h%=10*(Abs(goalx#-this\position\x#)+Abs(goaly#-this\position\y#)+Abs(goalz#-this\position\z#));find h (manhattan)
	this\f%=this\g%+this\h%;find f%
	Return this
End Function

Function waypointSet(this.waypoint,typeid%,parent.waypoint,g%,startx#,starty#,startz#,goalx#,goaly#,goalz#)
	this\typeid%=typeid%
	this\parent.waypoint=parent.waypoint
	this\position\x#=startx#
	this\position\y#=starty#
	this\position\z#=startz#
	this\g%=g%+this\parent\g%;find g
	this\h%=10*(Abs(goalx#-this\position\x#)+Abs(goalz#-this\position\z#));find h (manhattan)
	;this\h%=10*(Abs(goalx#-this\position\x#)+Abs(goaly#-this\position\y#)+Abs(goalz#-this\position\z#));find h (manhattan)
	this\f%=this\g%+this\h%;find f%
	this\state%=0;set/reset
End Function

;============================
;PATH
;============================
Const PATH_MAX=255
Dim pathId.path(PATH_MAX)
Global pathIndex.stack=stackIndexCreate(PATH_MAX)
Global pathAvail.stack=stackIndexCreate(PATH_MAX);return id of available queue
Dim pathTable(WAYPOINT_MAX,WAYPOINT_MAX)
Dim pathCollisionMap(MAP_WIDTH,MAP_HEIGHT,MAP_DEPTH)

;create open & close stack list
;Global pathopen.queue=queueCreate()
;Global pathclose.queue=queueCreate()

Type path
	Field id%
	Field typeid%
	Field waypoints%
	Field route.stack
	Field state% ;1=building, 2=destroying ;process slicing
	Field waypoint.waypoint
	Field goal.vector
	Field radius# ;tier worker radius
	Field distance# ;tier increment
	Field worker.worker[8]
	Field waypointadjacentg[8]
	Field open.queue
	Field close.queue
	Field waypointadjacent
End Type

Function pathStop()
	For this.path=Each path
		pathDelete(this)
	Next
End Function

Function pathNew.path()
	this.path=New path
	this\id%=0
	this\typeid%=0
	this\waypoints%=0
	this\id%=StackPop(pathIndex.stack)
	pathId(this\id)=this
	Return this
End Function

Function pathDelete(this.path)
	pathId(this\id)=Null
	StackPush(pathIndex.stack,this\id%)
	stackDelete(this\route)
	Delete this
End Function

Function pathUpdate()
	For this.path=Each path
		Select this\state%
			;Path Building
			Case 1 pathWaypointAdjacentBuild(this)
			Case 12 pathWaypointAdjacentCollisionBuild(this)
			Case 13 pathWaypointNextBuild(this)
			;Path Created
			Case 2 pathRouteBuild(this)
			;Path Destroy
			Case 3 pathOpenClean(this)
			Case 4 pathCloseClean(this)
			Case 5 pathDestroy(this)
		End Select
	Next
End Function

;A* path generation
;http://www.gamedev.net/reference/articles/article2003.asp
;http://www.policyalmanac.org/games/twoTiered.htm
	
Function pathCreate.path(pathstart.vector,pathgoal.vector,pathdistance#=2.0,pathradius#=1.0) ;two-tier A* reduce distance and worker radius for finer searches
	this.path=pathNew()
	this\open.queue=queueId(stackPop(queueAvail)) ;get available queues id
	this\close.queue=queueId(stackPop(queueAvail))
	this\goal.vector=pathgoal.vector
	this\distance#=pathdistance#
	this\radius#=pathradius#
	this\route.stack=stackCreate(127)
	;create startnode at start vector
	this\waypoint.waypoint=waypointId(stackPop(waypointAvail))
	waypointSet(this\waypoint,1,waypoint0,0,pathstart\x#,pathstart\y#,pathstart\z#,pathgoal\x#,pathgoal\y#,pathgoal\z#)
	;place waypointstartid id close queue
	queuePush(this\close,this\waypoint\f%,this\waypoint\id%)
	this\waypointadjacent=8
	pathWaypointAdjacentBuild(this)	
	Return this
End Function

Function pathWaypointAdjacentBuild(this.path)
	this\state%=12
	PositionEntity this\waypoint\entity%,this\waypoint\position\x#,this\waypoint\position\y#,this\waypoint\position\z#:EntityColor this\waypoint\entity%,0,255,0:EntityAlpha this\waypoint\entity%,.9:ShowEntity this\waypoint\entity%
	;For Each of these squares, save point A as its "parent square"
	;Look at all the reachable or walkable squares adjacent to the starting point, 
	;Move worker in 8 adjacent positions  X#,Z# (Y# influenced by gravity) 
	;other configurations: 3-8 star 	
	Restore waypointadjacent
	For loop = 1 To this\waypointadjacent
		this\worker.worker[loop]=workerID(stackPop(workerAvail))
		EntityRadius this\worker[loop]\entity%,this\radius#
		ScaleEntity worker\entity%,this\radius#,this\radius#,this\radius#
	 	;Uses waypointadjacent dataset for adjacent waypoint position & cost		
		Read waypointadjacentx%,waypointadjacentz%,waypointadjacentg%
		;adjust adjacent positions to parent
		this\worker[loop]\vector\x#=this\waypoint\position\x#+(this\distance#*waypointadjacentx%)
		this\worker[loop]\vector\z#=this\waypoint\position\z#+(this\distance#*waypointadjacentz%)
		;floor collision check/gravity adjust this\worker[loop]\vector\y#=waypointgravityoffsety#		
		;navigator wall collsion check
		this\waypointadjacentg%[loop]=waypointadjacentg%

		PositionEntity this\worker%[loop]\entity%,this\worker[loop]\vector\x#,this\worker[loop]\vector\y#,this\worker[loop]\vector\z#
		DebugLog "pathxy workerentity["+Str(this\worker[loop]\entity%)+"]="+Str(EntityX(this\worker[loop]\entity%))+","+Str(EntityY(this\worker[loop]\entity%))+","+Str(EntityZ(this\worker[loop]\entity%))
	Next
End Function 

Function pathWaypointAdjacentBuild2(this.path)
	this\state%=12
	;testing
	PositionEntity this\waypoint\entity%,this\waypoint\position\x#,this\waypoint\position\y#,this\waypoint\position\z#:EntityColor this\waypoint\entity%,0,255,0:EntityAlpha this\waypoint\entity%,.9:ShowEntity this\waypoint\entity%
	;For Each of these squares, save point A as its "parent square"
	;Look at all the reachable or walkable squares adjacent to the starting point, 
	;Move worker in 8 adjacent positions  X#,Z# (Y# influenced by gravity) 
	;other configurations: 3-8 star 
	Restore waypointadjacentang
	For loop = 1 To this\waypointadjacent
		this\worker.worker[loop]=workerID(stackPop(workerAvail))
		EntityRadius this\worker[loop]\entity%,this\radius#
		;ScaleEntity worker\entity%,this\radius#,this\radius#,this\radius#
	 	;Uses waypointadjacent dataset for adjacent waypoint position & cost
		Read waypointadjacentangle#,waypointadjacentg%
		;adjust adjacent positions to parent 
		this\worker[loop]\vector\x#=this\waypoint\position\x#+Cos2#(waypointadjacentangle#)*this\distance#
		this\worker[loop]\vector\z#=this\waypoint\position\z#+Sin2#(waypointadjacentangle#)*this\distance#	
		;floor collision check/gravity adjust this\worker[loop]\vector\y#=waypointgravityoffsety#		
		;navigator wall collsion check
		this\waypointadjacentg%[loop]=waypointadjacentg%
		PositionEntity this\worker%[loop]\entity%,this\worker[loop]\vector\x#,this\worker[loop]\vector\y#,this\worker[loop]\vector\z#
	Next
End Function 

Function pathWaypointAdjacentCollisionBuild(this.path)	
	this\state%=13
	For loop = 1 To this\waypointadjacent
		DebugLog "collision workerentity["+Str(this\worker[loop]\entity%)+"]="+Str(EntityX(this\worker[loop]\entity%))+","+Str(EntityY(this\worker[loop]\entity%))+","+Str(EntityZ(this\worker[loop]\entity%))
	
		;ignore illegal terrain  			
		If Not pathNavigatorCollision2(this\worker%[loop],level\map) ;pathCollisionMap(pathx#,pathy#,pathz#)) ;collision/ collision map array
		
			;ignore illegal positions on the closed list 
			If pathWaypointInQueue(this\close,EntityX(this\worker[loop]\entity%),EntityY(this\worker[loop]\entity%),EntityZ(this\worker[loop]\entity%))=Null ;position does not match a waypoint\position on the close list
				;If an adjacent position is already on the open list, check To see If this path To that square is a better one.
				waypoint.waypoint=pathWaypointInQueue(this\open,EntityX(this\worker[loop]\entity%),EntityY(this\worker[loop]\entity%),EntityZ(this\worker[loop]\entity%))
				;In other words, check To see If the G score For that square is Lower If we use the current square
				;If Not, don't do anything.		
				If waypoint<>Null
					;On the other hand, if the G cost of the new path is lower, change the parent of the adjacent
					;node to the selected square (in the diagram above, change the direction of the pointer 
					;to point at the selected square).
					waypointnewg%=this\waypoint\g%+this\waypointadjacentg%[loop]					
					If waypointnewg%<waypoint\g%
						waypoint\parent=this\waypoint
						;Finally, recalculate both the F And G scores of that square.
						waypoint\g%=waypointnewg%
						waypoint\f%=waypoint\g%+waypoint\h%
						;resort
					EndIf	
				Else		
					;new waypoint
					waypoint.waypoint=waypointId(stackPop(waypointAvail))
					waypointSet(waypoint,1,this\waypoint,this\waypointadjacentg%[loop],EntityX(this\worker[loop]\entity%),EntityY(this\worker[loop]\entity%),EntityZ(this\worker[loop]\entity%),this\goal\x#,this\goal\y#,this\goal\z#)
					;Add them To the open list, too.
					queuePush(this\open,waypoint\f%,waypoint\id%)
					;testing
					PositionEntity waypoint\entity%,waypoint\position\x#,waypoint\position\y#,waypoint\position\z#:EntityColor waypoint\entity%,255,255,0:EntityAlpha waypoint\entity%,.9:ShowEntity waypoint\entity%
				EndIf
			EndIf	
		Else ;testing to be removed?
			navigator%=CreateSphere(8)
			ScaleEntity navigator%,this\radius#,this\radius#,this\radius#
			PositionEntity navigator%,EntityX(this\worker[loop]\entity%),EntityY(this\worker[loop]\entity%),EntityZ(this\worker[loop]\entity%)
			EntityAlpha navigator%,.5
			RenderWorld()
			Flip()
		EndIf
		stackPush(workerAvail,this\worker[loop]\id)
	Next
End Function

Function pathWaypointNextBuild(this.path)	
	this\state%=1	
	;to continue the search
	;we simply choose the waypoint with the lowest F score square from all those that are on the open list
	;Drop it from the open list And add it To the closed list.
	If Not this\open\queueitems RuntimeError("No items in open queue | collisioncount["+Str(entitycollisioncount%)+"]") ;testing
	EntityColor this\waypoint\entity%,0,0,255 ;testing
	this\waypoint=waypointId(queuePop(this\open))	
	queuePush(this\close,this\waypoint\f%,this\waypoint\id%)
	EntityColor this\waypoint\entity,0,255,255;testing	
	entitycollisioncount=reset
	;path found
;	If this\waypoint\h%=0 this\state%=2
	If this\waypoint\position\x#<this\goal\x#+this\distance# And this\waypoint\position\x#>this\goal\x#-this\distance#
;		;If this\waypoint\position\y#<this\goal\y#+this\distance# And this\waypoint\position\y#>this\goal\y#-this\distance#; omit for gravity and collision
			If this\waypoint\position\z#<this\goal\z#+this\distance# And this\waypoint\position\z#>this\goal\z#-this\distance#
				this\state%=2;Path Route
			EndIf
;		;EndIf
	EndIf
End Function

Function pathNavigatorCollision%(worker.worker,typeofentity%);perform non stop collision detection
	DebugLog("worker\entity["+Str(worker\entity%)+"]="+Str$(worker\collision%)+","+Str(EntityX(worker\entity%))+","+Str(EntityY(worker\entity%))+","+Str(EntityZ(worker\entity%)))
	worker\collision%=EntityCollided(worker\entity%,typeofentity%)	
	If worker\collision%
		ResetEntity worker\entity%
		entitycollisioncount%=entitycollisioncount%+1 ;testing
	EndIf
	Return worker\collision%		
End Function

Function pathNavigatorCollision2%(worker.worker,typeofentity%);perform non stop collision detection
	DebugLog("worker\entity["+Str(worker\entity%)+"]="+Str$(worker\collision%)+","+Str(EntityX(worker\entity%))+","+Str(EntityY(worker\entity%))+","+Str(EntityZ(worker\entity%)))
	worker\collision%=MeshesIntersect(worker\entity%,typeofentity%)	
	If worker\collision% entitycollisioncount%=entitycollisioncount%+1 ;testing
	Return worker\collision%		
End Function

Global waypointincqueuecount,waypointincqueuecountmax ;testing

Function pathWaypointInQueue.waypoint(queue.queue,x#,y#,z#)
	For loop = 1 To queue\queueitems%
		waypointincqueuecount=loop;testing
		waypoint.waypoint=waypointId(queue\queueitem[loop]\dat%)
		If waypoint=Null Return waypoint
		If waypoint\position\x#=x# 
			;If waypoint\position\y#=y# 
				If waypoint\position\z#=z#
					Return waypoint
				EndIf	
			;EndIf
		EndIf			
	Next	
End Function

Function pathOpenClean(this.path) 
	waypoint.waypoint=waypointId(queuePopLast(this\open))
	If waypoint<>Null
		stackPush(waypointAvail,waypoint\id%)
		HideEntity waypoint\entity;testing
		If Not this\open\queueitems%
			stackPush(queueAvail,this\open\id%)		
			this\state%=4
		EndIf	
	EndIf	
End Function

Function pathCloseClean(this.path)
	waypoint.waypoint=waypointId(queuePopLast(this\close))
	If waypoint<>Null 
		EntityColor waypoint\entity,255,0,0;testing
		If Not waypoint\state%
			stackPush(waypointAvail,waypoint\id%)
			HideEntity waypoint\entity;testing
		EndIf
	EndIf
	If Not this\close\queueitems%
		stackPush(queueAvail,this\close\id%)		
		this\state%=5
	EndIf
End Function

Function pathDestroy(this.path)
	If Not this\waypoints% pathDelete(this)
End Function

Function pathRouteBuild(this.path)
	this\waypoint\state%=1
	this\waypoints%=this\waypoints%+1
	EntityColor this\waypoint\entity%,0,255,0;testing
	stackPush(this\route,this\waypoint\id%)
	this\waypoint=this\waypoint\parent
	If Not this\waypoint\parent\id this\state%=3 ;path build complete
End Function

Function pathRouteNext(this.path)
	this\waypoint=waypointId(stackPop(this\route))
	DebugLog("pathRouteNext="+Str(this\waypoint\id))
	stackPush(waypointAvail,this\waypoint\id%)				
	HideEntity this\waypoint\entity%;testing
	this\waypoints%=this\waypoints%-1
End Function

Function pathCollisionMapLoad(filename$)
	file=ReadFile(filename$+".collisionmap")
	If file
		While Not Eof(file)
			;				 MAP_WIDTH 		MAP_HIEGHT	   MAP_WIDTH       walkableflg
			pathCollisionMap(ReadByte(file),ReadByte(file),ReadByte(file))=ReadByte(file)
		Wend
		CloseFile(file)
	EndIf
End Function

Function pathCollisionMapBuild(this.path)
End Function
			
.waypointadjacent
;+14    +10    +14 ;diagonal movement g = 14
;  \    |    /
;    \  |  /
;      \|/     
;+10----0------+10 ;horizontal\vertical g  = 10
;      /|\   
;    /  |  \
;  /    |    \	
;+14    +10    +14
;...  x,  z, gcost
Data  0,  1, 10
Data -1,  0, 10
Data  0, -1, 10
Data  1,  0, 10
Data -1,  1, 14
Data -1, -1, 14
Data  1, -1, 14
Data  1,  1, 14
	
.waypointadjacent3D
;horizontal\vertical g  = 10
;diagonal movement g = 14
;...  x,  y,  z, gcost
Data  0,  0,  1, 10
Data -1,  0,  0, 10
Data  0,  0, -1, 10
Data  1,  0,  0, 10
Data -1,  0,  1, 14
Data -1,  0, -1, 14
Data  1,  0, -1, 14
Data  1,  0,  1, 14
Data  0, -1,  0, 10
Data  0,  1,  0, 10
Data  0, -1,  1, 14
Data -1, -1,  0, 14
Data  0, -1, -1, 14
Data  1, -1,  0, 14
Data  0,  1,  1, 14
Data -1,  1,  0, 14
Data  0,  1, -1, 14
Data  1,  1,  0, 14
Data -1, -1,  1, 14
Data -1, -1, -1, 14
Data  1, -1, -1, 14
Data  1, -1,  1, 14
Data -1,  1,  1, 14
Data -1,  1, -1, 14
Data  1,  1, -1, 14
Data  1,  1,  1, 14

.waypointadjacentang
;Data angle,cost
;hort,vert 90 deg
Data 0.0,10
Data 90.0,10
Data 180.0,10
Data 270.0,10
;diagonal 45 deg
Data 45.0,14
Data 135.0,14
Data 225.0,14
Data 315.0,14
;diag 22.5
Data 22.5,12
Data 67.5,12
Data 112.5,12
Data 157.7,12
Data 202.5,12
Data 247.5,12
Data 292.5,12
Data 337.5,12
;diagonal 15
Data 15.0,13
Data 30.0,13
Data 60.0,13
Data 75.0,13
Data 105.0,13
Data 120.0,13
Data 150.0,13
Data 165.0,13
Data 195.0,13
Data 210.0,13
Data 240.0,13
Data 255.0,13
Data 285.0,13
Data 300.0,13
Data 330.0,13
Data 345.0,13

;============================
;WEAPON (temp)
;============================
Type weapon
End Type

;============================
;BOT
;============================
Const BOT_MAX=64
Dim botId.bot(BOT_MAX)
Global botIndex.stack=stackIndexCreate(BOT_MAX)

;Const BOT_STATE_STAND_IDLE=0
;Const BOT_STATE_STAND_FIRE =0
;Const BOT_STATE_STAND_TAUNT=0
;Const BOT_STATE_WALK=0
;Const BOT_STATE_WALK_FIRE =0
;Const BOT_STATE_RUN=0
;Const BOT_STATE_RUN_REVERSE=0
;Const BOT_STATE_RUN_REVERSE_FIRE=0
;Const BOT_STATE_RUN_FIRE=0
;Const BOT_STATE_STRAFE_LEFT=0
;Const BOT_STATE_STRAFE_RIGHT=0
;Const BOT_STATE_STRAFE_LEFT_FIRE=0
;Const BOT_STATE_STRAFE_RIGHT_FIRE=0
;Const BOT_STATE_JUMP=0
;Const BOT_STATE_JUMP_FIRE=0
;Const BOT_STATE_SCAN=0
;Const BOT_STATE_ACQUIRE_WEAPON=0
;Const BOT_STATE_ACQUIRE_AMMO=0
;Const BOT_STATE_ACQUIRE_ARMOR=0
;Const BOT_STATE_ACQUIRE_COVER=0
;Const BOT_STATE_ACQUIRE_TEAMMEMBER=0
;Const BOT_STATE_CALL_TEAMMEMBER=0

Type bot
	Field id%
	Field typeid%
	Field entity%,thinker%; temp
	Field texture.texture
	Field position.vector
	Field old.vector
	Field angle.vector
	;sensors
	Field sight.vector
	Field hearing#
	Field fullhealth#
	Field health#
	Field speed#
	Field turn#
	;motor
	Field direction%
	Field target%
	Field goal.vector
	Field path.path
	Field waypointID%
	Field sequence%
	Field frame%
	Field weapon.weapon
	;ai
	Field state%
	Field action.action
End Type

Function botStop()
	For this.bot=Each bot
		botDelete(this)
	Next
End Function

Function botNew.bot()
	this.bot=New bot
	this\id%=0
	this\typeid%=0
	this\entity%=0
	;this\texture.texture=textureNew()
	this\position.vector=vectorNew()
	this\old.vector=vectorNew()
	this\angle.vector=vectorNew()
	this\sight.vector=vectorNew()
	this\hearing#=0.0
	this\fullhealth#=0.0
	this\health#=0.0
	this\speed#=0.0
	this\goal.vector=vectorNew()
	;this\path.path=pathNew()
	this\frame%=0
	;this\weapon.weapon=weaponNew()
	this\state%=0
	this\action.action=actionNew()
	this\id%=StackPop(botIndex.stack)
	botId(this\id)=this
	Return this
End Function

Function botDelete(this.bot)
	botId(this\id)=Null
	StackPush(botIndex.stack,this\id%)
	actionDelete(this\action.action)
	;weaponDelete(this\weapon.weapon)
	;pathDelete(this\path.path)
	vectorDelete(this\goal.vector)
	this\speed#=0.0
	this\health#=0.0
	this\fullhealth#=0.0
	this\hearing#=0.0
	vectorDelete(this\sight.vector)
	vectorDelete(this\angle.vector)
	vectorDelete(this\old.vector)
	vectorDelete(this\position.vector)
	;textureDelete(this\texture.texture)
	FreeEntity this\entity%
	Delete this
End Function

Function botUpdate()
	For this.bot = Each bot
		Select this\typeid
			Case 1
				botSensor(this)
				botReaction(this)
				botMotor(this)
			Default
				If player_visible  ; this mode will let us watch them walking along the waypoints  
					;botSDSensor(this)	
				Else
					botSDMotor(this)
				EndIf	
		End Select	
	Next
End Function 

Function botRead.bot(file)
	this.bot=New bot
	this\id%=ReadInt(file)
	this\typeid%=ReadInt(file)
	this\entity%=ReadInt(file)
	;this\texture.texture=textureRead(file)
	this\position.vector=vectorRead(file)
	this\old.vector=vectorRead(file)
	this\angle.vector=vectorRead(file)
	this\sight.vector=vectorRead(file)
	this\hearing#=ReadFloat(file)
	this\fullhealth#=ReadFloat(file)
	this\health#=ReadFloat(file)
	this\speed#=ReadFloat(file)
	this\goal.vector=vectorRead(file)
	;this\path.path=pathRead(file)
	this\frame%=ReadInt(file)
	;this\weapon.weapon=weaponRead(file)
	this\state%=ReadInt(file)
	;this\action.action=actionRead(file)
	Return this
End Function

Function botWrite(file,this.bot)
	WriteInt(file,this\id%)
	WriteInt(file,this\typeid%)
	WriteInt(file,this\entity%)
	;textureWrite(file,this\texture.texture)
	vectorWrite(file,this\position.vector)
	vectorWrite(file,this\old.vector)
	vectorWrite(file,this\angle.vector)
	vectorWrite(file,this\sight.vector)
	WriteFloat(file,this\hearing#)
	WriteFloat(file,this\fullhealth#)
	WriteFloat(file,this\health#)
	WriteFloat(file,this\speed#)
	vectorWrite(file,this\goal.vector)
	;pathWrite(file,this\path.path)
	WriteInt(file,this\frame%)
	;weaponWrite(file,this\weapon.weapon)
	WriteInt(file,this\state%)
	;actionWrite(file,this\action.action)
End Function

Function botSave(filename$="Default")
	file=WriteFile(filename$+".bot")
	For this.bot= Each bot
		botWrite(file,this)
	Next
	CloseFile(file)
End Function

Function botOpen(filename$="Default")
	file=ReadFile(filename+".bot")
	Repeat
		botRead(file)
	Until Eof(file)
	CloseFile(file)
End Function

Function botCopy.bot(this.bot)
	copy.bot=New bot
	copy\id%=this\id%
	copy\typeid%=this\typeid%
	copy\entity%=this\entity%
	;copy\texture.texture=textureCopy(this\texture.texture)
	copy\position.vector=vectorCopy(this\position.vector)
	copy\old.vector=vectorCopy(this\old.vector)
	copy\angle.vector=vectorCopy(this\angle.vector)
	copy\sight.vector=vectorCopy(this\sight.vector)
	copy\hearing#=this\hearing#
	copy\fullhealth#=this\fullhealth#
	copy\health#=this\health#
	copy\speed#=this\speed#
	copy\goal.vector=vectorCopy(this\goal.vector)
	;copy\path.path=pathCopy(this\path.path)
	copy\frame%=this\frame%
	;copy\weapon.weapon=weaponCopy(this\weapon.weapon)
	copy\state%=this\state%
	copy\action.action=actionCopy(this\action.action)
	Return copy
End Function

Function botMimic(mimic.bot,this.bot)
	mimic\id%=this\id%
	mimic\typeid%=this\typeid%
	mimic\entity%=this\entity%
	;textureMimic(mimic\texture.texture,this\texture.texture)
	vectorMimic(mimic\position.vector,this\position.vector)
	vectorMimic(mimic\old.vector,this\old.vector)
	vectorMimic(mimic\angle.vector,this\angle.vector)
	vectorMimic(mimic\sight.vector,this\sight.vector)
	mimic\hearing#=this\hearing#
	mimic\fullhealth#=this\fullhealth#
	mimic\health#=this\health#
	mimic\speed#=this\speed#
	vectorMimic(mimic\goal.vector,this\goal.vector)
	;pathMimic(mimic\path.path,this\path.path)
	mimic\frame%=this\frame%
	;weaponMimic(mimic\weapon.weapon,this\weapon.weapon)
	mimic\state%=this\state%
	actionMimic(mimic\action.action,this\action.action)
End Function

Function botCreate.bot(id%,typeid%,entity%,texture.texture,position.vector,old.vector,angle.vector,sight.vector,hearing#,fullhealth#,health#,speed#,goal.vector,path.path,frame%,weapon.weapon,state%,action.action)
	this.bot=botNew()
	this\id%=id%
	this\typeid%=typeid%
	this\entity%=entity%
	;this\texture.texture=texture.texture
	this\position.vector=position.vector
	this\old.vector=old.vector
	this\angle.vector=angle.vector
	this\sight.vector=sight.vector
	this\hearing#=hearing#
	this\fullhealth#=fullhealth#
	this\health#=health#
	this\speed#=speed#
	this\goal.vector=goal.vector
	this\path.path=path.path
	this\frame%=frame%
	;this\weapon.weapon=weapon.weapon
	this\state%=state%
	this\action.action=action.action
	Return this
End Function

Function botSet(this.bot,id%,typeid%,entity%,texture.texture,position.vector,old.vector,angle.vector,sight.vector,hearing#,fullhealth#,health#,speed#,goal.vector,path.path,frame%,weapon.weapon,state%,action.action)
	this\id%=id%
	this\typeid%=typeid%
	this\entity%=entity%
	this\texture.texture=texture.texture
	this\position.vector=position.vector
	this\old.vector=old.vector
	this\angle.vector=angle.vector
	this\sight.vector=sight.vector
	this\hearing#=hearing#
	this\fullhealth#=fullhealth#
	this\health#=health#
	this\speed#=speed#
	this\goal.vector=goal.vector
	this\path.path=path.path
	this\frame%=frame%
	this\weapon.weapon=weapon.weapon
	this\state%=state%
	this\action.action=action.action
End Function

Function botSensor(this.bot);input
;	If LinePick(this\position\x#,this\position\y#,this\position\z#,this\sight\x#,this\sight\y#,this\sight\z#,radius#)=player this\state=1 ;visual check
;	If ChannelPlaying(sound) And EntityDistance(this\entity%,sound)<=this\hear# this\state=1;audio check
;	If this\health<this\fullhealth*.25 And this\state=0 this\state=2;health check
;	If this\health<this\fullhealth*.25 And this\state=1 this\state=3;health check 2
;	If this\weapon[this\weapon\current]\ammo<this\fullammo*.25 this\state=4;ammo check
;	If this\health<0 this\state=5
	
	If this\state=4 ;seek path
		If this\path<>Null
			If this\path\waypoints%=0 this\state%=0;goal achieve
		Else
			this\state=0
		EndIf	
	EndIf
	
	If this\state=3 ;building path
		If this\path\state=3 
			pathRouteNext(this\path)
			this\state%=4;moving						
		EndIf	
	EndIf
			
	If this\state=1 Or this\state=2 this\state=3

	If KeyHit(2) ;create path
		;Delay 2000 
		this\state=1;testing 
	EndIf	
	
	If KeyHit(3) ;create path
		;Delay 2000
		this\state=2;testing
	EndIf	
	
	If KeyHit(4) 
		vectorSet(this\position,Int(Rnd(40)*2),0,Int(Rnd(40)*2))
		PositionEntity this\entity,this\position\x#,this\position\y#,this\position\z#
	EndIf
	
	If KeyHit(5) 
		vectorSet(this\goal,Int(Rnd(40)*2),0,Int(Rnd(40)*2))
		PositionEntity goalentity,this\goal\x#,this\goal\y#,this\goal\z#
	EndIf	
	
	If KeyHit(6) botSpawn()
		
End Function

Function botReaction(this.bot);action
	Select this\state
		;idle long acquisition
		Case 1 this\path.path=pathCreate(this\position,this\goal,2,.2)
		;short acquisition					
		Case 2 this\path.path=pathCreate(this\position,this\goal,1,.2)
	End Select
End Function

Function botMotor(this.bot);movement and animation
;	;follow path
	If this\state=4 botwaypointSeek(this);moving
	If this\state=6 botWaypointSeek2(this);testing
;	If collision regeneratepath
;	If this\animation botAnimate(this)
;	If this\physics botPhysics(this)
End Function

Function botwaypointSeek(this.bot)
	;simple this\path\waypoint AI
	PositionEntity this\path\waypoint\entity%,this\path\waypoint\position\x#,this\path\waypoint\position\y#,this\path\waypoint\position\z#
	PointEntity this\entity,this\path\waypoint\entity%
	MoveEntity this\entity,0,0,this\speed#
	vectorSet(this\position,EntityX(this\entity),EntityY(this\entity),EntityZ(this\entity))
	If this\position\x#<this\path\waypoint\position\x#+.5 And this\position\x#>this\path\waypoint\position\x#-.5
		;If this\position\y#<this\path\waypoint\position\y#+.5 And this\position\y#>this\path\waypoint\position\y#-.5; omit for gravity and collision
			If this\position\z#<this\path\waypoint\position\z#+.5 And this\position\z#>this\path\waypoint\position\z#-.5
				pathRouteNext(this\path)
			EndIf
		;EndIf
	EndIf
	 	
End Function

Function botWaypointSeek2(this.bot)
	;simple waypoint AI
	PositionEntity this\path\waypoint\entity%,this\path\waypoint\position\x#,this\path\waypoint\position\y#,this\path\waypoint\position\z#
	PointEntity this\entity,this\path\waypoint\entity%
	MoveEntity this\entity,0,0,this\speed#
	vectorSet(this\position,EntityX(this\entity),EntityY(this\entity),EntityZ(this\entity))
	If this\position\x#<this\path\waypoint\position\x#+.5 And this\position\x#>this\path\waypoint\position\x#-.5
		;If this\position\y#<this\path\waypoint\position\y#+.5 And this\position\y#>this\path\waypoint\position\y#-.5; omit for gravity and collision
			If this\position\z#<this\path\waypoint\position\z#+.5 And this\position\z#>this\path\waypoint\position\z#-.5
				this\path\waypoint=After this\path\waypoint 
				If this\path\waypoint=Null this\path\waypoint=waypointId(1)
			EndIf
		;EndIf
	EndIf 	
End Function

Function botSpawn.bot()
	this.bot=botNew()
	this\typeid%=typeid%
	this\entity%=CreateCylinder(8,True)
	EntityColor this\entity,Rnd(255),Rnd(255),Rnd(255)
	vectorSet(this\position,Int(Rnd(40)*2),0,Int(Rnd(40)*2))
	PositionEntity this\entity%,this\position\x#,this\position\y#,this\position\z#
	vectorSet(this\goal,Int(Rnd(40)*2),0,Int(Rnd(40)*2))
	PositionEntity goalentity,this\goal\x#,this\goal\y#,this\goal\z#
	this\speed#=Rnd(.01,.5)	
	Return this
End Function

Function botSDSensor(this.bot)
	;PositionEntity worker\entity%,waypointID(this\waypointID%+this\direction)\position\x#,waypointID(this\waypointID%+this\direction)\position\y#,waypointID(this\waypointID+this\direction)\position\z#
	EntityColor this\thinker%,0,255,0
	cur_d#=EntityDistance(this\target%,this\entity%) ;(Abs(EntityX(this\target)-this\position\x#)+Abs(EntityY(this\target)#-this\position\y#)+Abs(EntityZ(this\target)#-this\position\z#));
	If cur_d<1.0 
		this\state%=0
		botSDReaction(this)
		Return
	ElseIf cur_d>10.0 
		this\state%=3
		botSDReaction(this)
		Return
	EndIf
	
	If cur_d<targetrange
		If EntityVisible(this\entity%,this\target%)
			; target% visible from current bot position 
			cur_v=1
		EndIf
	EndIf

	PositionEntity worker\entity%,waypointID(this\waypointID%+this\direction%)\position\x,waypointID(this\waypointID%+this\direction%)\position\y,waypointID(this\waypointID%+this\direction%)\position\z

    nex_d#=EntityDistance(worker\entity%,this\target%)
	If nex_d< targetrange
		If EntityVisible(worker\entity%,this\target%)
			; target% visible from next bot waypoint
			nex_v=1
		EndIf
    EndIf

	PositionEntity worker\entity%,waypointID(this\waypointID%-this\direction%)\position\x,waypointID(this\waypointID%-this\direction%)\position\y,waypointID(this\waypointID%-this\direction%)\position\z

    prev_d#=EntityDistance(this\target%,worker\entity%)
	If prev_d< targetrange
		If EntityVisible(worker\entity%,this\target%)
			; target% visible from prev. bot waypoint
			prev_v=1
		EndIf
	EndIf

	this\state%=3

	If cur_v=1 And nex_v=1 And prev_v=1 ; visible from all 3 points
		If cur_d<nex_d And cur_d<prev_d
			; current pos optimal
			this\state%=0
     	EndIf
		If nex_d<cur_d And nex_d<prev_d
			; next waypoint better
			this\state%=1
		EndIf
		If prev_d<nex_d And prev_d<cur_d
			; prev waypoint better
			this\state%=2
		EndIf
	EndIf

	If cur_v=1 And nex_v=1 And prev_v=0 ; visible from cur and nex
		If cur_d<nex_d
			; current pos optimal
			this\state%=0
     	Else
      		this\state%=1
		EndIf
	EndIf

	If cur_v=1 And nex_v=0 And prev_v=1 ; visible from cur and prev
		If cur_d<prev_d
      		; current pos optimal
			this\state%=0
		Else
			this\state%=2
		EndIf
	EndIf

	If cur_v=0 And nex_v=1 And prev_v=1 ; visible from nex and prev
		If nex_d<prev_d
			; current pos optimal
			this\state%=1
		Else
			this\state%=2
		EndIf
	EndIf

	If cur_v=1 And nex_v=0 And prev_v=0 ; visible only from cur
		this\state%=0
	EndIf

	If cur_v=0 And nex_v=1 And prev_v=0 ; visible only from nex
		this\state%=1
    EndIf

	If cur_v=0 And nex_v=0 And prev_v=1 ; visible only from prev
		this\state%=2
	EndIf

	botSDReaction(this)

	;optimization notes: reduce the number of entitydistance calls, or faster alternative

End Function

Function botSDReaction(this.bot)
	Select this\state%
		Case 0; already in optimal position to attack
	    	EntityColor this\thinker%,255,0,0
		Case 1; next waypoint is better, go there
			EntityColor this\thinker%,255,255,0
			botSDMotor(this)
	    Case 2 ; prev waypoint is better, go there
			EntityColor this\thinker%,255,255,0
			If this\direction=1
				this\direction=-1
			Else
				this\direction=1
			EndIf
			botSDMotor(this)
		Case 3 ; not visible at all - walk waypoints
			EntityColor this\thinker%,0,255,255
			botSDMotor(this)				
	End Select
End Function


Function botSDMotor(this.bot)
	PositionEntity worker\entity%,waypointID(this\waypointID%+this\direction)\position\x#,waypointID(this\waypointID%+this\direction)\position\y#,waypointID(this\waypointID+this\direction)\position\z#
	; rotate
	this\old\y#=EntityYaw(this\entity%)
	PointEntity this\entity%, worker\entity%
	this\angle\y#=EntityYaw(this\entity%)
	MoveEntity this\entity%,0,0,this\speed#
	adist#=this\old\y#-this\angle\y#
	turn#=Abs(this\old\y#-this\angle\y#)
	If turn#>180 turn#=360.0-turn#
	turn=turn*this\turn#
	If (adist <-180) Or (adist >0 And adist <180)
		RotateEntity this\entity%,0,this\old\y#-turn#,0
	Else
		RotateEntity this\entity%,0,this\old\y#+turn#,0
	EndIf
	; move
	If player_visible  ; this mode will let us watch them walking along the waypoints  
		this\speed#=.5
	Else		
		this\speed#=.25
	EndIf	
	If EntityX(this\entity%)>=EntityX(worker\entity%)-this\speed# And EntityX(this\entity%)<=EntityX(worker\entity%)+this\speed#
		If EntityY(this\entity%)>=EntityY(worker\entity%)-this\speed# And EntityY(this\entity%)<=EntityY(worker\entity%)+this\speed#
			If EntityZ(this\entity%)>=EntityZ(worker\entity%)-this\speed# And EntityZ(this\entity%)<=EntityZ(worker\entity%)+this\speed#
				this\waypointID%=this\waypointID%+this\direction
			EndIf
		EndIf
	EndIf
	If this\waypointID%>=waypoints%
		this\direction=-1
	EndIf
	If this\waypointID%<=1
		this\direction=1
	EndIf
	;animate
	Select this\state%
		Case 1
			If AnimSeq(this\entity)<>stillfire Animate this\entity%,1,this\speed#*1.25,stillfire 
		Default 
			If AnimSeq(this\entity)<>RunFire Animate this\entity%,1,this\speed#*1.25,RunFire	
	End Select
End Function
