; ID: 903
; Author: Techlord
; Date: 2004-02-03 12:36:58
; Title: Project PLASMA FPS 2004: Markerset.bb
; Description: Marketset Code Module

;============================
;MARKERSET MODULE for WAYPOINTER
;============================
Const MARKERSET_MAX%=32
Dim markersetId.markerset(MARKERSET_MAX%)
Global markersetIndex.stack=stackIndexCreate(MARKERSET_MAX%)
Global markerset.markerset
Global markersetGuide,markersetGuideWidth#,markersetGuideHeight#,markersetGuideLength#
Dim markersetkeymap(16)
Dim markerSelected(MARKERSET_MAX%)

Type markerset
	Field id%
	Field typeid%
	Field width%
	Field length%
	Field spacing#
	Field costfactor%
	Field scale#
	Field threshholds%
	Field threshhold#[24]
	Field waypoints%
	Field waypoint.waypoint[WAYPOINT_MAX]
	Field complete%
End Type

Function markersetStart()
	waypointStart()
	;markersetGuide
	markersetGuideWidth#=20
	markersetGuideLength#=20
	markersetGuide=markersetGuideCreate(markersetGuideWidth#,markersetGuideLength#)	
	;keymap
	markersetKeymapAssign(13)
End Function

Function markersetStop()
	For this.markerset=Each markerset
		markersetDelete(this)
	Next
End Function

Function markersetNew.markerset()
	this.markerset=New markerset
	this\id%=0
	this\typeid%=0
	this\width%=0
	this\length%=0
	this\spacing#=0.0
	this\costfactor%=0
	this\scale#=0.0
	this\threshholds%=0
	this\waypoints%=0
	this\complete%=0
	this\id%=StackPop(markersetIndex.stack)
	markersetId(this\id)=this
	Return this
End Function

Function markersetDelete(this.markerset)
	markersetId(this\id)=Null
	StackPush(markersetIndex.stack,this\id%)
	Delete this
End Function

Function markersetUpdate()
	markersetGuideControl();waypoint guide control
	For this.markerset=Each markerset
		;apply gravity to waypoints in set
		For loop = 1 To this\waypoints%
			If this\waypoint[loop]\state%=1	
				For loop2 = 1 To this\threshholds%
					If EntityY#(this\waypoint[loop]\entity%)=this\threshhold#[loop2]
						this\complete%=this\complete%+1
						this\waypoint[loop]\state%=0
						HideEntity this\waypoint[loop]\entity%
						stackPush(waypointAvail,this\waypoint[loop]\id%)
					EndIf
				Next
				this\waypoint[loop]\e%=this\waypoint[loop]\e%+5;terrain cost testing
				TranslateEntity this\waypoint[loop]\entity%,0,-this\scale#,0
				If EntityCollided(this\waypoint[loop]\entity%,1) 
					this\complete%=this\complete%+1
					this\waypoint[loop]\state%=2
					EntityColor this\waypoint[loop]\entity%,0,255,0;testing
					EntityType this\waypoint[loop]\entity%,0
				EndIf
			EndIf	
		Next
		If this\complete%=this\waypoints% markersetDelete(this);remove waypoint
	Next
End Function

Function markersetSave(filename$)
	file=WriteFile(filename$+".markerset")
	For waypoint.waypoint= Each waypoint
		If waypoint\state%=2
			waypoint\position\x#=EntityX(waypoint\entity%)
			waypoint\position\y#=EntityY(waypoint\entity%)
			waypoint\position\z#=EntityZ(waypoint\entity%)
			waypointWrite(file,waypoint)
		EndIf	
	Next
	CloseFile(file)
End Function

Function markersetSave2(filename$)
	file=WriteFile(filename$+".waypoints.bin")
	For waypoint.waypoint= Each waypoint
		If waypoint\state%=2
			waypoint\position\x#=EntityX(waypoint\entity%)
			waypoint\position\y#=EntityY(waypoint\entity%)
			waypoint\position\z#=EntityZ(waypoint\entity%)
			vectorWrite(file,waypoint\position)
		EndIf	
	Next
	CloseFile(file)
End Function

Function markersetOpen(filename$)
	file=ReadFile(filename+".markerset")
	If file
		Repeat
			waypoint.waypoint=waypointRead(file) ;replace waypoint initialized at markersetStart()
			waypoint\entity%=waypointId(waypoint\id)\entity%
			waypointDelete(waypointId(waypoint\id))
			waypointId(stackPop(waypointIndex))=waypoint
			PositionEntity waypoint\entity%,waypoint\position\x#,waypoint\position\y#,waypoint\position\z#
			EntityColor waypoint\entity%,0,255,0
			ShowEntity waypoint\entity%
			ScaleEntity waypoint\entity%,.5,.1,.5
		Until Eof(file)
		CloseFile(file)
		;correct WaypointAvail
		waypointAvail\pointer=reset
		For waypoint.waypoint=Each waypoint
			If waypoint\state%=0 stackPush(waypointAvail,waypoint\id%)		
		Next		
	EndIf
End Function

Function markersetCreate.markerset(scale#,costfactor%,spacing#)
	;waypoint generate
	this.markerset=markersetNew()
	this\typeid%=1
	this\width%=Ceil(Abs(markersetGuideWidth#)/spacing#)
	this\length%=Ceil(Abs(markersetGuideLength#)/spacing#)
	this\spacing#=spacing#
	this\costfactor%=costfactor%
	this\scale#=scale#
	fontoffset%=FontHeight()+2
	Locate 0,fontoffset%*5
	FlushKeys()
	this\threshholds%=Input("Threshholds [Use 1]:")
	For loop = 1 To this\threshholds%
		markersetThreshholdSet(this,loop,Input("Threshhold Height Value#"+Str(loop)+":"));
	Next
	For loop = 0 To this\width%
		For loop2 = 0 To this\length%
			this\waypoints%=this\waypoints%+1
			this\waypoint.waypoint[this\waypoints%]=waypointID(stackPop(waypointAvail))
			this\waypoint[this\waypoints%]\state%=1
			ShowEntity this\waypoint[this\waypoints%]\entity%
			PositionEntity this\waypoint[this\waypoints%]\entity%,EntityX(markersetGuide)+loop*this\spacing#,EntityY#(markersetGuide),EntityZ#(markersetGuide)+loop2*this\spacing#
			ScaleEntity this\waypoint[this\waypoints%]\entity%,this\scale#,.1,this\scale#
			EntityColor this\waypoint[this\waypoints%]\entity%,255,0,0
			EntityType this\waypoint[this\waypoints%]\entity%,3
			EntityRadius this\waypoint[this\waypoints%]\entity%,this\scale#
		Next
	Next	
	Return this	
End Function

Function markersetThreshholdSet(this.markerset,index%,threshhold#)
	this\threshhold#[index%]=threshhold#
End Function

Function markersetGuideControl()
	If KeyDown(markersetkeymap(1)) TranslateEntity markersetGuide,0,.5,0;up
	If KeyDown(markersetkeymap(2)) TranslateEntity markersetGuide,0,-.5,0;down
	If KeyDown(markersetkeymap(3)) TranslateEntity markersetGuide,0,0,.5;forward
	If KeyDown(markersetkeymap(4)) TranslateEntity markersetGuide,0,0,-.5;backward		
	If KeyDown(markersetkeymap(5)) TranslateEntity markersetGuide,-.5,0,0;left
	If KeyDown(markersetkeymap(6)) TranslateEntity markersetGuide,.5,0,0;right
	markersetGuideHeight#=EntityY(markersetGuide)
	If KeyDown(markersetkeymap(7))
		markersetGuideLength#=markersetGuideLength#-.5
		If markersetGuideLength#<1 markersetGuideLength#=1
		markersetGuideReseat()
	EndIf
	If KeyDown(markersetkeymap(8))
		markersetGuideLength#=markersetGuideLength#+.5
		markersetGuideReseat()
	EndIf
	If KeyDown(markersetkeymap(9))
		markersetGuideWidth#=markersetGuideWidth#-.5
		If markersetGuideWidth#<1 markersetGuideWidth#=1
		markersetGuideReseat()
	EndIf
	If KeyDown(markersetkeymap(10))
		markersetGuideWidth#=markersetGuideWidth#+.5
		markersetGuideReseat()
	EndIf
	If KeyHit(markersetkeymap(11)) markersetEdit()			
	If MouseHit(1)
		FlushKeys()	
		fontoffset%=FontHeight()+2
		Locate 0,fontoffset%*2
		markerset.markerset=markersetCreate(Input("Marker Scale [Use 0.5]:"),Input("CostFactor[Use 0]:"),Input("Marker Spacing [Use 5.0]:"));
	EndIf
	If MouseDown(3) PositionEntity markersetGuide,Ceil(EntityX(camera\entity%)),Ceil(EntityY(camera\entity%)),Ceil(EntityZ(camera\entity%))
End Function

Function markersetEdit()
	ShowPointer() 
	While Not KeyHit(markersetkeymap(11))
		If MouseDown(1)=True 
			CameraPick(camera\entity,MouseX(),MouseY())
			entity%=PickedEntity()
			If entity%
				For loop = 1 To markerSelectedList; check entities on selected list
					If entity%=markerSelected(loop) entity%=reset
				Next
				If entity%
					markerSelectedList=markerSelectedList+1
					markerSelected(markerSelectedList)=entity% 
					EntityColor entity%,255,0,0
				EndIf
			EndIf		
		EndIf
		
		If KeyDown(markersetkeymap(1)) 
			For loop=1 To markerSelectedList
				TranslateEntity markerSelected(loop),0,.5,0;up
			Next
		EndIf

		If KeyDown(markersetkeymap(2))
			For loop=1 To markerSelectedList 
				TranslateEntity markerSelected(loop),0,-.5,0;down
			Next
		EndIf
			
		If KeyDown(markersetkeymap(3))
			For loop=1 To markerSelectedList
				TranslateEntity markerSelected(loop),0,0,.5;forward
			Next
		EndIf	
		
		If KeyDown(markersetkeymap(4))
			For loop=1 To markerSelectedList
				TranslateEntity markerSelected(loop),0,0,-.5;backward		
			Next	
		EndIf
			
		If KeyDown(markersetkeymap(5))
			For loop=1 To markerSelectedList
				TranslateEntity markerSelected(loop),-.5,0,0;left
			Next	
		EndIf
			
		If KeyDown(markersetkeymap(6))
			For loop=1 To markerSelectedList
				TranslateEntity markerSelected(loop),.5,0,0;right
			Next	
		EndIf
		
		If MouseHit(2) ;unselect
			For loop=1 To markerSelectedList
				EntityColor markerSelected(loop),0,255,0
			Next
			markerSelectedList=reset	
		EndIf	

		If KeyHit(211) ;remove
			For loop=1 To markerSelectedList
				For loop2 = 1 To WAYPOINT_MAX
					If waypointid(loop2)\entity%=markerSelected(loop)
						waypointid(loop2)\state%=0
						HideEntity waypointid(loop2)\entity%
						stackPush(waypointAvail,waypointid(loop2)\id%)
					EndIf	
				Next	
			Next
			markerSelectedList=reset	
		EndIf
		
		If KeyHit(markersetkeymap(7)) ;height balance
			For loop=2 To markerSelectedList	
				PositionEntity markerSelected(loop),EntityX(markerSelected(loop)),EntityY(markerSelected(1)),EntityZ(markerSelected(loop))
			Next
		EndIf

		RenderWorld()
		
		If KeyDown(markersetkeymap(12)) markersetHelp()	

		If KeyHit(markersetkeymap(13))
			Text 0,0,"Saving "+levelfilename$+".markerset"
			markersetSave(levelfilename$)
			markersetSave2(levelfilename$)
		EndIf 	
			
		
		Text 0,0,"Edit Mode"
	Flip()
	Wend
	For loop = 1 To WAYPOINT_MAX
		waypoint.waypoint=waypointId(loop)
		If waypoint\state%=2
			EntityColor waypoint\entity,0,255,0
			waypoint\position\X#=EntityX(waypoint\entity%)
			waypoint\position\y#=EntityY(waypoint\entity%)
			waypoint\position\z#=EntityZ(waypoint\entity%)
		EndIf
	Next
	FlushKeys()
	FlushMouse()
	HidePointer()
End Function

Function markersetKeymapAssign(keys%)
	Restore markersetkeymapdata
	For loop = 1 To keys%
		Read key%
		markersetkeymap(loop)=key%
	Next	
End Function

.markersetkeymapdata
;    pgU pgD up  dwn lft rgt i  m  j  k  e  f1 f2
Data 201,209,200,208,203,205,50,23,36,37,18,59,60

Function markersetGuideCreate(width#,length#)
	sprite=CreateMesh()
	brush=CreateBrush(255,255,255)
	surface=CreateSurface(sprite,brush)
	FreeBrush brush
	AddVertex (surface,0,0,length#,1,0)  ; top left 0,1;1,0
	AddVertex (surface,width#,0,length#,0,0)   ; top right 1,1;1,1
	AddVertex (surface,0,0,0,1,1) ; bottom left 0,0;,0,0
	AddVertex (surface,width#,0,0,0,1)  ; bottom right 1,0;0,1
	AddTriangle(surface,0,1,2)
	AddTriangle(surface,3,2,1)
	EntityColor sprite,255,255,0
	EntityAlpha sprite,.4	
	EntityFX sprite,1+4+8+16
	EntityBlend sprite,3	
	Return sprite
End Function

Function markersetGuideReseat()
	x#=EntityX(markersetGuide)
	y#=EntityY(markersetGuide)
	z#=EntityZ(markersetGuide)
	FreeEntity markersetGuide
	markersetGuide=markersetGuideCreate(markersetGuideWidth#,markersetGuideLength#)
	PositionEntity markersetGuide,x#,y#,z#
End Function


Function markersetHelp()
	fontoffset%=FontHeight()+2
	Color 63,255,127	  
	Text 0,fontoffset%*3,"Camera Controls"
	Text 0,fontoffset%*4,"    Mouse - Rotate"
	Text 0,fontoffset%*5,"    W - Forward"
	Text 0,fontoffset%*6,"    S - Backward"  
	Text 0,fontoffset%*7,"    A - Strafe Left"
	Text 0,fontoffset%*8,"    D - Strafe Right"  
	Text 0,fontoffset%*9,"    Q - Up"
	Text 0,fontoffset%*10,"    Z - Down"
	Text 0,fontoffset%*11,"    Mouse B3 - Fetch Guide"
	Color 255,255,0	       
	Text 200,fontoffset%*3,"Guide Controls"
	Text 200,fontoffset%*4,"    Up - Forward"
	Text 200,fontoffset%*5,"    Down - Backward"
	Text 200,fontoffset%*6,"    Left"
	Text 200,fontoffset%*7,"    Right"
	Text 200,fontoffset%*8,"    PageUp - Up"
	Text 200,fontoffset%*9,"    PageDn - Down"
	Text 200,fontoffset%*10,"    J - Scale Width Down"
	Text 200,fontoffset%*11,"    K - Scale Width Up"  
	Text 200,fontoffset%*12,"    I - Scale Length Up"
	Text 200,fontoffset%*13,"    M - Scale Length Down"
	Text 200,fontoffset%*14,"    Mouse B1 - Drop Markers"
	Color 255,255,255	  
	Text 400,fontoffset%*3,"Edit Controls"
	Text 400,fontoffset%*4,"    E - Edit Mode On/Off" 
	Text 400,fontoffset%*5,"    Mouse - Move Cursor"
	Text 400,fontoffset%*6,"    Mouse B1 - Select Markers"
	Text 400,fontoffset%*7,"    Mouse B2 - Deselect Markers"
	Text 400,fontoffset%*8,"    Movement:"
	Text 400,fontoffset%*9,"      Up - Forward"
	Text 400,fontoffset%*10,"      Down - Backward"
	Text 400,fontoffset%*11,"      Left"
	Text 400,fontoffset%*12,"      Right"
	Text 400,fontoffset%*13,"      PageUp - Up"
	Text 400,fontoffset%*14,"      PageDn - Down"
	Text 400,fontoffset%*15,"    M - Balance Height" 
	Text 400,fontoffset%*16,"    Delete"
	Text 400,fontoffset%*17,"    F2 - Save"

End Function
