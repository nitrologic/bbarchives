; ID: 507
; Author: Techlord
; Date: 2002-11-24 17:12:03
; Title: Project PLASMA FPS 2004: Level.bb
; Description: Level Loader and more

;============================
;LEVEL
;============================
Const LEVEL_CSVFIELD_MAX%=255
Const LEVEL_ENTITY_MAX%=1024

Global level.level

Type level
	Field map%
;	Field configuration
	Field filename$
	Field file%
	Field entities%
	Field entity%[LEVEL_ENTITY_MAX%]
	Field entitylabel$
	Field entityID%
	Field csvfields%
	Field csvfield$[LEVEL_CSVFIELD_MAX%]
End Type

Function levelLoad.level(filename$) 
	;-------------------------------
	;LOAD LEVEL levelcsv DATA 
	;Note: Loading Order is Critical
	;-------------------------------
	
	this.level=New level
	this\filename$=filename$
	
	this\file%=ReadFile(this\filename$+".csv") 	

	If Not this\file% RuntimeError (this\filename$+".csv File Not Found")
	
	While Not Eof(this\file%) 
	
		this\csvfields%=levelCSVRead(this)
	
		 Select this\csvfield$[1] ;label
		
			Case "map"
			
			Case "texture"
				
			Case "model"
			
			Case "sound"
			
			Case "action" 
			;load handler for action data
			
			Case "script"			
					
			Case "waypoint"
				waypoint.waypoint=waypointNew()
				waypoint\id%=this\csvfield$[2] ;id
				waypointId.waypoint(waypoint\id%)=waypoint ;assign to id pointer
				waypoint\typeid%=this\csvfield$[3] ;typeid
				waypoint\position\x#=this\csvfield$[4] ;x
				waypoint\position\y#=this\csvfield$[5] ;y
				waypoint\position\z#=this\csvfield$[6] ;z
				
			Case "bot"
				
			Case "struct"
				;load stuff for this entitytypeid%
						
			Case "door"
				;load stuff For this entitytypeid%
		
			Case "platform"
				;load stuff For this entitytypeid%
				
			Case "switch"
				;load stuff For this entitytypeid%
				
			Case "light"
				;load stuff For this entitytypeid%
				light.light=lightNew()
				light\id%=this\csvfield$[2] ;id
				lightId.light(light\id%)=light ;assign to id pointer
				light\typeid%=this\csvfield$[3] ;typeid
				light\entity%=this\csvfield$[4] ;modelfile: if integer id% then copy else its a this\file%
				If light\entity%
					light\entity%=CopyEntity(lightId(light\entity%)\entity%) 
				Else
					light\entity%=LoadAnimMesh(this\csvfield$[4])
					If Not light\entity% RuntimeError("No Entity File or Copy Found for light"+Str(light\id%)) 
				EndIf
				EntityType light\entity%,1
				EntityFX light\entity%,1+4+8				
				light\position\x#=this\csvfield$[8] ;x
				light\position\y#=this\csvfield$[9] ;y
				light\position\z#=this\csvfield$[10] ;z
				light\angle\x#=this\csvfield$[14]
				light\angle\y#=this\csvfield$[15]
				light\angle\z#=this\csvfield$[16]
				If this\csvfield$[17] ;uniformscale
					light\scale\x#=this\csvfield$[17] ;xscale
					light\scale\y#=this\csvfield$[17] ;yscale
					light\scale\z#=this\csvfield$[17] ;zscale					
				Else
					light\scale\x#=this\csvfield$[11] ;xscale
					light\scale\y#=this\csvfield$[12] ;yscale
					light\scale\z#=this\csvfield$[13] ;zscale						
				EndIf 
				PositionEntity light\entity%,light\position\x#,light\position\y#,light\position\z#
				ScaleEntity light\entity%,light\scale\x#,light\scale\y#,light\scale\z# 
				RotateEntity light\entity%,light\angle\x#,light\angle\y#,light\angle\z#	
				
			Case "prop"	
				prop.prop=propNew()
				prop\id%=this\csvfield$[2] ;id
				propId.prop(prop\id%)=prop ;assign to id pointer
				prop\typeid%=this\csvfield$[3] ;typeid
				prop\entity%=this\csvfield$[4] ;modelfile: if integer id% then copy else its a this\file%
				prop\textureid%=this\csvfield$[5];texture
				If prop\entity%
					prop\entity%=CopyEntity(propId(prop\entity%)\entity%) 
				Else
					Select this\csvfield$[6]
						Case 2 ;animated
							prop\entity%=LoadAnimMesh(this\csvfield$[4])					
						Default ;stactic
							prop\entity%=LoadMesh(this\csvfield$[4])
					End Select
					If Not prop\entity% RuntimeError("No Entity File or Copy Found for Prop"+Str(prop\id%)) 
				EndIf
				If prop\textureid%
					prop\textureid%=propId(prop\textureid%)\textureid%
				Else
					prop\textureid%=LoadTexture(this\csvfield$[5]) ;to be change to support texture
				EndIf
				If prop\textureid% EntityTexture prop\entity%,prop\textureid%
				EntityType prop\entity%,1				
				prop\position\x#=this\csvfield$[8] ;x
				prop\position\y#=this\csvfield$[9] ;y
				prop\position\z#=this\csvfield$[10] ;z
				prop\angle\x#=this\csvfield$[14]
				prop\angle\y#=this\csvfield$[15]
				prop\angle\z#=this\csvfield$[16]
				If this\csvfield$[17] ;uniformscale
					prop\scale\x#=this\csvfield$[17] ;xscale
					prop\scale\y#=this\csvfield$[17] ;yscale
					prop\scale\z#=this\csvfield$[17] ;zscale					
				Else
					prop\scale\x#=this\csvfield$[11] ;xscale
					prop\scale\y#=this\csvfield$[12] ;yscale
					prop\scale\z#=this\csvfield$[13] ;zscale						
				EndIf
				PositionEntity prop\entity%,prop\position\x#,prop\position\y#,prop\position\z#
				ScaleEntity prop\entity%,prop\scale\x#,prop\scale\y#,prop\scale\z# 
				RotateEntity prop\entity%,prop\angle\x#,prop\angle\y#,prop\angle\z#		
				
			Case "soundfield"
				;load stuff For this entitytypeid%			

			Case "particle"
	
			Case "flare"			
	
			Case "gadget"
			
			Case "controlmap"
			
			Case "network"
			
		End Select

	Wend
	CloseFile(this\file%)
	
	;---------------------------
	;LOAD LEVEL GEOMETRY
	;---------------------------
	this\map%=LoadAnimMesh(this\filename$+".b3d")
	
	If Not this\map% RuntimeError (this\filename$+".b3d File Not Found")
	
	levelHierarchy(this,this\map%) 	

	For loop = 1 To this\entities% 
		this\entitylabel$=EntityName$(this\entity%[loop])
		
		;load handler for bot geometry
		this\entityID%=levelEntityLabel%(this,"spawner")
		If this\entityID% 
			EntityColor this\entity%[loop],0,0,255
			EntityFX this\entity%[loop],1+4+8
			EntityAlpha this\entity%[loop],.4
		EndIf
		
		this\entityID%=levelEntityLabel%(this,"waypoint")		
		If this\entityID%	
			If waypointId(this\entityID%)=Null 
				waypoint.waypoint=waypointNew()
				waypointId(this\entityID%)=waypoint.waypoint
			Else
				waypoint.waypoint=waypointId(this\entityID%) ;object reference				
			EndIf
			waypoint\position\x#=EntityX(this\entity%[loop],True)
			waypoint\position\y#=EntityY(this\entity%[loop],True)
			waypoint\position\z#=EntityZ(this\entity%[loop],True)
			Select waypoint\typeid% ;start-goal testing
				Case 0 
					EntityColor this\entity%[loop],255,255,0;FreeEntity this\entity%[loop]
					startwaypoint.waypoint=waypoint
				Case 1 
					EntityColor this\entity%[loop],0,255,0
				Case 2
					goalwaypoint.waypoint=waypoint
					EntityColor this\entity%[loop],255,0,0
			End Select
			;freeEntity this\entity%[loop]	
		EndIf		
		
		this\entityID%=levelEntityLabel%(this,"portal")
		If this\entityID%
		EndIf
		
		this\entityID%=levelEntityLabel%(this,"switch")
		If this\entityID%
			EntityColor this\entity%[loop],255,0,255
			EntityFX this\entity%[loop],1+4+8
			EntityAlpha this\entity%[loop],.4		
		EndIf
		
		this\entityID%=levelEntityLabel%(this,"door")
		If this\entityID%
			EntityFX this\entity%[loop],1+4+8
			EntityColor this\entity%[loop],255,0,0
		EndIf
		
		this\entityID%=levelEntityLabel%(this,"platform")
		If this\entityID%
			EntityColor this\entity%[loop],255,0,127
		EndIf					
		
		this\entityID%=levelEntityLabel%(this,"climber")
		If this\entityID%
			EntityColor this\entity%[loop],255,255,0
			EntityFX this\entity%[loop],1+4+8
			EntityAlpha this\entity%[loop],.4			
		EndIf
		
		this\entityID%=levelEntityLabel%(this,"struct")
		If this\entityID%
			;struct.struct=structId(this\entityID%)
			;If struct\collisiontype%=1
			EntityType this\entity%[loop],1;level collision
		EndIf	
		
		this\entityID%=levelEntityLabel%(this,"soundfield")
		If this\entityID%
			If soundfieldId(this\entityID%)=Null 
				soundfield.soundfield=soundfieldNew()
				soundfieldId(this\entityID%)=soundfield.soundfield
			Else
				soundfield.soundfield=soundfieldId(this\entityID%) ;object reference				
			EndIf		
			soundfield\position\x#=EntityX(this\entity%[loop],True)
			soundfield\position\y#=EntityY(this\entity%[loop],True)
			soundfield\position\z#=EntityZ(this\entity%[loop],True)		
		EndIf	
		
		this\entityID%=levelEntityLabel%(this,"light")
		If this\entityID%
			EntityColor this\entity%[loop],255,255,0
			EntityFX this\entity%[loop],1+4+8
		EndIf	
		
	Next 

	;---------------------------
	;LOAD LEVEL AUXILLARY DATA
	;---------------------------
	
	;WAYPOINTS
	this\file%=ReadFile(filename+"._markerset")
	If this\file%
		Repeat
			waypoint.waypoint=waypointRead(this\file%) ;replace waypoint initialized at markersetStart()
			waypoint\entity%=waypointId(waypoint\id)\entity%
			waypointDelete(waypointId(waypoint\id))
			waypointId(stackPop(waypointIndex))=waypoint
			PositionEntity waypoint\entity%,waypoint\position\x#,waypoint\position\y#,waypoint\position\z#
			;EntityColor waypoint\entity%,0,255,0
			;ShowEntity waypoint\entity%
			;ScaleEntity waypoint\entity%,.05,.05,.05
			waypoint\typeid%=1
			waypoints%=waypoints%+1
		Until Eof(this\file%)
		CloseFile(this\file%)
		;correct WaypointAvail
		waypointAvail\pointer=reset
		For waypoint.waypoint=Each waypoint
			If waypoint\state%=0 stackPush(waypointAvail,waypoint\id%)		
		Next		
	EndIf	

	this\file%=ReadFile(this\filename$+".waypoints")
	If this\file%
		While Not Eof(this\file%)
			waypoint.waypoint=waypointID(stackPop(waypointAvail)) 
			waypoint\position\x#=ReadFloat(this\file%)
			waypoint\position\y#=ReadFloat(this\file%)
			waypoint\position\z#=ReadFloat(this\file%)
			waypoint\typeid=ReadInt(this\file%)
			PositionEntity waypoint\entity%,waypoint\position\x#,waypoint\position\y#,waypoint\position\z#
			;show waypoint
			;ShowEntity waypoint\entity%			
			;ScaleEntity waypoint\entity%,.05,.05,.05
			;If waypoint\typeid%
			;	EntityColor waypoint\entity%,255,0,255
			;EndIf
			;EntityAlpha waypoint\entity%,.75
			waypoints%=waypoints%+1
		Wend
	EndIf
	
	Return this 
End Function

Function levelSave(filename$)
End Function

Function levelClear()
End Function

Function levelLoadPLD(filename$)
End Function

Function levelcsvRead%(this.level,csvdelimiter$=",")
	csvrow$=ReadLine(this\file%)
	csvcolumn%=1
	this\csvfield$[csvcolumn%]=nil$
	csvquote%=False
	For loop=1 To Len(csvrow$)
		csvchar$=Mid$(csvrow$,loop,1)
		If csvchar$=Chr$(34) 
			csvquote%=True-csvquote%
		ElseIf csvchar$=csvdelimiter$ And csvquote%=False 
				; end of column
				csvcolumn%=csvcolumn%+1
				this\csvfield$[csvcolumn%]=nil$
		Else
				this\csvfield$[csvcolumn%]=this\csvfield$[csvcolumn%]+csvchar$
		End If
	Next
	Return csvcolumn%
End Function

Function levelEntityLabel%(this.level,label$);compare label and returns id integer
	If Left(this\entitylabel$,Len(label$))=label$ Return Right(this\entitylabel$,Len(this\entitylabel$)-Len(label$) )
End Function

Function levelHierarchy(this.level,parent%) 
	children%=CountChildren(parent%) 
	For loop = 1 To children% 
		this\entities%=this\entities%+1 
		child%=GetChild(parent%,loop) 
		If child%
			DebugLog("levelHierarchy:child["+Str(this\entities%)+"]="+Str(child%)+"/"+EntityName(child%)+" Surfaces="+CountSurfaces(child%));testing
		EndIf	
		this\entity%[this\entities%]=child% 
		levelHierarchy(this,child%) 
	Next 
End Function

Function levelCollisionSet()
	;Collisions src_type,dest_type,method,response
	Collisions 2,1,2,2 ;entity,level,sphere-to-polygon,slide
	Collisions 3,1,2,1 ;worker,level,sphere-to-polygon,stop
End Function

Function levelEntityCollide%(worker.worker,typeofentity%);perform non stop collision detection
	worker\collision%=EntityCollided(worker\entity%,typeofentity%)	
	If worker\collision%
		ResetEntity worker\entity%
		entitycollisioncount%=entitycollisioncount%+1 ;testing
	EndIf
	Return worker\collision%		
End Function
