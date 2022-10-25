; ID: 1228
; Author: Rogue Vector
; Date: 2004-12-10 10:11:00
; Title: Zone Occlusion System
; Description: simple and fast system for managing large levels

; ***************************************************************
; PROG:   OCCLUSION SYSTEM  -  (FREEWARE)
; ETHOS:  Simple and Fast
; AUTHOR: Rogue Vector for Octane Digital Studios Ltd
; DATE:   Tuesday 7th December 2004 
; ***************************************************************




; BASIC DESIGN OVERVIEW
; ---------------------

; This is a zone (or cell) based system.

; The system works by reading the name of each object in the (.b3d) file hierarchy. 

; The zone data is extracted from the name and placed into the TZone data structure.

; The Level Designer determines which zones can be seen from a particular vantage point in the level map.

; He records this data in the object name, before exporting to a (.b3d) file.

; Thus, during run-time, if the player is in zone X, simply get the 'can_see' zones from the TZone data.

; No need for portals.

; No need for an additional script file.




;CONSTANTS
Const SUCCESS          = 1
Const FAILURE          = -1
Const EMPTY            = -1
Const VIS_FORMAT$      = "[ zone: # vis: # ]"  ;The basic format of the vis data, in its simplest form.
Const VIS_MAX_ZONES    = 10					   ;The maximum number of zones that can be seen from the current zone.
Const VIS_STOP_SYMBOL$ = "]"			       ;End of line (terminator) used when parsing the vis data.
Const VIS_DELIMITER$   = " "				   ;Words in the vis data are seperated by this character (i.e. space).
Const VIS_INFO_START   = 5					   ;The first vis data begins at the fifth word in.
Const ZONE_IDENT_START = 3



;TYPES
Type TZone

	Field entity
	Field name$
	Field can_see[VIS_MAX_ZONES]
	Field can_see_count
	Field max_X#
	Field max_Y#
	Field max_Z#
	Field min_X#
	Field min_Y#
	Field min_Z#

End Type 



;GLOBALS
Global g_check_format       = True
Global g_current_zone.TZone = Null
Global g_last_zone          = 0




;TEST PROGRAM ************************
Global g_keytimer, g_wireframe, g_time
Global g_gravity# = -1
runtest("level_map.b3d")
;DE-ACTIVATE AS DEFAULT **************




;FUNCTIONS
Function Occlusion_Initialise(v_level)

	Local node         = 0
	Local node_name$   = ""
	Local token$       = ""
	Local surface      = 0
	Local max_surfaces = 0
	Local vis_index    = VIS_INFO_START
	Local child_count  = CountChildren(v_level)
	Local Vx#=0.0, Vy#=0.0, Vz#=0.0

	If (child_count = 0) RuntimeError("ERROR!... NO SCENE ELEMENTS FOUND")	
	
	;Iterate through every child object in the mesh hierarchy and populate the TZone objects
	For i = 1 To child_count
		
		;Find the zones in the level mesh hierarchy.
		node = GetChild(v_level,i)
		
		;Get the name of the node.
		node_name$ = EntityName$(node)

		;Check formatting (SLOW - switched off by default).
		If (g_check_format) CheckZoneInfoFormat(node_name)
				
		;Create zone object to hold data.
		zone.TZone  = New TZone
			
		;Populate object with initial values.
		zone\entity        = node
		zone\name          = node_name 
		zone\max_X         = -1000000.0
		zone\max_Y         = -1000000.0
		zone\max_Z         = -1000000.0
		zone\min_X         = 1000000.0
		zone\min_Y         = 1000000.0 
		zone\min_Z         = 1000000.0
		zone\can_see_count = 0

		;Set default values for can_see array.
		For p=0 To VIS_MAX_ZONES
		
			zone\can_see[p] = EMPTY
				
		Next
						
		;Create a bounding box around the zone.
		max_surfaces = CountSurfaces(zone\entity)
			
		For k=1 To max_surfaces 
					
			surface = GetSurface(zone\entity, k)
			
			For m = 0 To CountVertices(surface) - 1
			
				;Transform points to world (global) space
				TFormPoint VertexX(surface, m), VertexY(surface, m), VertexZ(surface, m), zone\entity, 0
				
				Vx# = TFormedX#()
				Vy# = TFormedY#()
				Vz# = TFormedZ#()
				
				If (Vx# > zone\max_X) Then zone\max_X = Vx#
				If (Vy# > zone\max_Y) Then zone\max_Y = Vy#
				If (Vz# > zone\max_Z) Then zone\max_Z = Vz#
										
				If (Vx# < zone\min_X) Then zone\min_X = Vx#
				If (Vy# < zone\min_Y) Then zone\min_Y = Vy#
				If (Vz# < zone\min_Z) Then zone\min_Z = Vz#
								
			Next

		Next
							
		;Check that there is a stopping condition symbol in the zone info string.
		If Instr(zone\name, VIS_STOP_SYMBOL)

			;Parse initial vis data into the internal array. 
			token = GetWord(node_name, vis_index, " ")
							
			If Not(Int(token) => 0) RuntimeError("ERROR!... DATA IN VIS ARRAY IS NOT OF THE REQUIRED TYPE.")

			Repeat
								
					zone\can_see[vis_index - VIS_INFO_START] = Int(token)
								
					vis_index = vis_index + 1
				
					token = GetWord(zone\name, vis_index, " ")
				
			Until (token = VIS_STOP_SYMBOL)

			;Reset vis array index
			vis_index = VIS_INFO_START

		Else
		
			RuntimeError("ERROR!...VIS DATA FORMAT INCORRECT")

		EndIf

	Next
	
			
	;Modify the data. Need to convert the zone numbers to the corresponding entity id's.
	;Also, need to count the number of zones that can be seen from this zone and store in 'can_see_count'.
	;This is a preparatory stage to help keep Update function small and fast.
	Local tmp_entity = 0
	
	For l_tmp.TZone = Each TZone
	
		For index=0 To VIS_MAX_ZONES
	
			If (l_tmp\can_see[index] = EMPTY) Exit
			
			tmp_entity = FindEntity(l_tmp\can_see[index])
			
			l_tmp\can_see[index] = tmp_entity
		
			l_tmp\can_see_count  = l_tmp\can_see_count + 1
							
		Next
	
	Next
			
		
	;Check formatting of zone data structures.
	If (g_check_format) CheckZoneDataStructure()
	
	
End Function





Function Occlusion_Update(v_playerX#, v_playerY#, v_playerZ#)

	Text 5,30, "X: " + v_playerX
	Text 5,40, "Y: " + v_playerY
	Text 5,50, "Z: " + v_playerZ
	
	Local n   = 0

	For g_current_zone = Each TZone
			
		If (IsInsideZone(v_playerX, v_playerY, v_playerZ, g_current_zone)) 
		
			n = n + 1
			
			Text 5, 70+(n*20), "Zone name: " + g_current_zone\name
						
			If Not(g_last_zone = Handle g_current_zone)
					
				HideAllZones()
				
				ShowEntity g_current_zone\entity
								
				For l_index = 0 To g_current_zone\can_see_count - 1
				
					ShowEntity 	g_current_zone\can_see[l_index]
					
				Next
				
				g_last_zone = Handle g_current_zone
				
				Exit
			
			EndIf
										
		EndIf

	Next

End Function





Function Occlusion_ClearAll()

	Local l_tmp.TZone = Null
	
	For l_tmp = Each TZone
	
		Delete l_tmp
		
	Next

	Return SUCCESS

End Function





Function FindEntity(v_number)

	Local l_tmp.TZone = Null
	Local token$      = ""
	
	For l_tmp = Each TZone
	
		token = GetWord(l_tmp\name, ZONE_IDENT_START, VIS_DELIMITER)
		If (Int(token) = v_number) Return l_tmp\entity 
		
	Next

	Return FAILURE

End Function





Function FindZone.TZone(v_number)

	Local l_tmp.TZone = Null
	Local token$      = ""
	
	For l_tmp = Each TZone
	
		token = GetWord(l_tmp\name, ZONE_IDENT_START, VIS_DELIMITER)
		If (Int(token) = v_number) Return l_tmp 
		
	Next

	Return Null

End Function





Function HideAllZones()
		
	For l_tmp.TZone = Each TZone
	
		For l_index = 0 To MAX_VIS_ZONES
		
			HideEntity l_tmp\can_see[l_index]
			HideEntity l_tmp\entity
						
		Next
	
	Next

	Return SUCCESS

End Function




Function EntityAlphaLevel(v_amount#)

	For tmp.TZone = Each TZone
	
		EntityAlpha tmp\entity, v_amount
	
	Next
	
End Function





Function CheckZoneInfoFormat(v_name$)
	
	Print "CHECKING FORMAT OF VIS DATA"
		
	Print "Analysing Zone: [ " + GetWord(v_name, 3, VIS_DELIMITER) + " ]"
	
	
	If  Not(GetWord(v_name, 1 , VIS_DELIMITER) = GetWord(VIS_FORMAT, 1, VIS_DELIMITER)) Then RuntimeError("ERROR!...VIS DATA FORMAT INCORRECT")
	If  Not(GetWord(v_name, 2 , VIS_DELIMITER) = GetWord(VIS_FORMAT, 2, VIS_DELIMITER)) Then RuntimeError("ERROR!...VIS DATA FORMAT INCORRECT")
	If  Not(GetWord(v_name, 4 , VIS_DELIMITER) = GetWord(VIS_FORMAT, 4, VIS_DELIMITER)) Then RuntimeError("ERROR!...VIS DATA FORMAT INCORRECT")
	If  Not(Int(GetWord(v_name, 3 , VIS_DELIMITER)) => 0) Then RuntimeError("ERROR!...VIS DATA FORMAT INCORRECT")
	If  Not(Int(GetWord(v_name, 5 , VIS_DELIMITER)) => 0) Then RuntimeError("ERROR!...VIS DATA FORMAT INCORRECT")
	
	Print "        Result: [ PASS ]"
	Print "------------------------"

	Delay 200
 
	Return SUCCESS

End Function





Function CheckZoneDataStructure()
	
	Print
	Print
	Print "CHECKING ZONE DATA STRUCTURE"
		
	For l_tmp.TZone = Each TZone
	
		Print "Analysing Zone: [ " + GetWord(l_tmp\name, 3, VIS_DELIMITER) + " ]"
		Print "  Entity Ident: [ " + Str(l_tmp\entity) + " ]"
		Print "   BoundingBox: [ max_X = " + Str(l_tmp\max_X) + " ]"		
		Print "                [ max_Y = " + Str(l_tmp\max_Y) + " ]"
		Print "                [ max_Z = " + Str(l_tmp\max_Z) + " ]"
		Print "                [ min_X = " + Str(l_tmp\min_X) + " ]"
		Print "                [ min_Y = " + Str(l_tmp\min_Y) + " ]"
		Print "                [ min_Z = " + Str(l_tmp\min_Z) + " ]"
		
		For p=0 To VIS_MAX_ZONES
	
			If Not(l_tmp\can_see[p] = -1) Print "       Can See: [ " + Str(l_tmp\can_see[p]) + " ]"
		
		Next
		
		Print "---------------------"
		
		;WaitKey
		Delay 200
		
	Next

	Print
	Print "FINISHED ANALYSIS..."
	Print
	Print "HIT A KEY TO CONTINUE"
	Print
	
	Return SUCCESS

End Function





Function IsInsideZone%(v_objectspaceX#, v_objectspaceY#, v_objectspaceZ#, v_zone.TZone) 

	Return (v_objectspaceX > v_zone\min_X) And (v_objectspaceX < v_zone\max_X) And (v_objectspaceY > v_zone\min_Y) And (v_objectspaceY < v_zone\max_Y) And (v_objectspaceZ > v_zone\min_Z) And (v_objectspaceZ < v_zone\max_Z)

End Function






Function GetWord$(InputString$, WordNum, Seperators$=" ") ;by sswift

	FoundWord  = False
	WordsFound = 0

	; Loop through each character in the input string.
	For CharLoop = 1 To Len(InputString$)

		; Get the character at this location in the string.
		ThisChar$ = Mid$(InputString$, CharLoop, 1)

		; If the character at this position is one of the characters in the seperator list...
		If Instr(Seperators$, ThisChar$, 1)
		
			; If a word has been started...
			If FoundWord
		
				; ...then this character must mark the end of a word.

				; Increment the number of words we've found.
				WordsFound = WordsFound + 1

				; Is this word the word we want?
				If WordsFound = WordNum
				
					; Yes!  Exit the function and return the word.
					Return Word$
			
				Else
				
					; No.  Discard this word.
					Word$ = ""
					FoundWord = False
				
				EndIf
				
			Else
			
				; Ignore this character.  We have either not reached a word yet, or are between words.
			
			EndIf				
					
		Else
		
			; This is not a character in our seperator list.  Add it to our word.
			FoundWord = True
			Word$ = Word$ + ThisChar$
			
		EndIf
		
	Next	
		
	; We have finished looking through the string.  Was the last word we were on the one we were looking for?
	If (WordsFound+1) = WordNum

		; Yes! 
		; Return the word that at the end of the string which didn't have any seperators after it.
		Return Word$

	Else
	
		; No. 
		; The word number passed to the function was greater than the number of words in the string. 
		; Return an empty string.
		Return ""

	EndIf
	
End Function





Function QuickTexture()

	tex=CreateTexture(512,512)
	ScaleTexture tex,.2,.5
	SetBuffer TextureBuffer(tex)
	
	Color 50,50,50
	
	Rect 0,0,512,512
	
	Color 200,200,200
	Rect 8,8,496,496
	
	Color 255,255,255
	SetBuffer BackBuffer()
	 
	For tmp.TZone = Each TZone
	
		EntityTexture tmp\entity, tex 
			
	Next
	
	Return tex
	
End Function





Function SuperCam(cam,ent,cspeed#,dist#,hite#,xrot#,tilt#) ;by PsychicParrot

	TFormPoint 0,hite#,-dist#,ent,0
	
	cx#=(TFormedX()-EntityX(cam))*cspeed#
	cy#=(TFormedY()-EntityY(cam))*cspeed#
	cz#=(TFormedZ()-EntityZ(cam))*cspeed#
	
	TranslateEntity cam,cx,cy,cz
	PointEntity cam,ent
	RotateEntity cam,xrot#,EntityYaw(cam),tilt#
	
End Function





Function DoWireFrame(v_key)

	g_time = MilliSecs()
	
	If (g_KeyTimer + 200 < g_time)

		If (KeyDown(v_key)) 
		
			g_wireFrame = 1 - g_wireFrame
			WireFrame g_wireFrame
			g_keyTimer = g_time : Return SUCCESS
		
		EndIf
 		
	EndIf

End Function





Function RunTest(v_level_filename$)

	AppTitle "Occlusion Test Program","Are you sure you want to quit?"
	Graphics3D 800,600,16,2
	SetBuffer BackBuffer()
	
	C_PLAYER   = 1
	C_LEVEL	   = 2
	C_TRIGGER  = 3
	
	Collisions C_PLAYER,C_LEVEL,2,2
	
	level = LoadAnimMesh(v_level_filename)
	
	EntityType level,C_LEVEL,True
	
	;Initialise Occlusion system
	Occlusion_Initialise(level)
		
	If (g_check_format) WaitKey
	
	player  = CreateSphere(8) ;the player
	
	ScaleMesh player, 1,1,1
	MoveEntity player, 0,2,0
	TurnEntity player, 0,90,0
	EntityColor player, 255,0,0
	EntityType player,C_PLAYER
	EntityRadius player, 1 
	
	camera = CreateCamera()
	
	PositionEntity camera, -200,50,-200
	PointEntity camera, player
		
	light = CreateLight()
	RotateEntity light, 60,30,0
	
	texture = QuickTexture()	
		
	Repeat
		
		DoWireFrame(17)						 			;W for wireframe
			
		If (KeyDown(200)) MoveEntity player, 0,0,1.5	;Up arrow	
		If (KeyDown(208)) MoveEntity player, 0,0,-1.5	;Down arrow
		If (KeyDown(203)) TurnEntity player, 0,3.8,0	;Left arrow
		If (KeyDown(205)) TurnEntity player, 0,-3.8,0	;Right arrow
		If (KeyDown(57 )) MoveEntity player, 0,3, 0	    ;Space to jump
		
		SuperCam(camera,player,0.5,12,5,0,2)

		MoveEntity player, 0, g_gravity, 0
				
		UpdateWorld()
		
		RenderWorld()
				
		Occlusion_Update(EntityX(player,True),EntityY(player,True),EntityZ(player,True))
		
		Text 5,5, "Triangles Rendered: " + TrisRendered()
		
		Flip
		
	Until KeyHit(1)
	
	Occlusion_ClearAll()
	
	FreeEntity  level
	FreeTexture texture
	ClearWorld()
	End

End Function
