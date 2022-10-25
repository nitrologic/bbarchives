; ID: 801
; Author: LostCargo
; Date: 2003-09-28 12:32:47
; Title: Unlimited Terrain w. Unique meshes
; Description: Allows the developer to place unique meshes at certain locations while also allwing them to use a 'generic' tile for all other areas. Ie wilderness

;===================================================================================
;SOURCE :     Lucidforge.com
;DEVELOPER:   Richard N. Wright
;APPLICATION: UNLIMITED TERRAINS
;
;DESCRIPTION: This source provides a process for loading tiles togeather so that a 
;player can have an unlimited terrain, while allowing unique tiles at certain locations
;the application for use would be suitable for situations similar to a large Massively multiplayer game
;where a wilderness area can be repeated and not have to be designed, 
; but a town or special area can placed at a specific location. This removes any need to have
; all the information about the entire world stored, but rather assumes that the developer
; only stores the information specific to the special areas
;
; The code is not the best code i have written, and i think i may scrap it
; but as promised il pass it on to others. Currently i am working on other projects so this one is stalled out.
; The code is free to use and abuse as you like, but any credits would be appreciated
; Please email anvil@lucidforge if you have any comments, critiques, or have used it.
;
;CREDIT : (c)2003 Ken Lynch for the databank code  
;         
;===================================================================================





; -------------------- 
Include  "./databank.bb"  
Graphics3D 800,600,16,2
SetBuffer BackBuffer()  
 SeedRnd (MilliSecs()) 


;NAME: Protagonist 
;USE:  For tracking  the protagoinist movements through the system
;==================================== 

Type ProtagonistType 
	Field PHYS_Velocity_Forward#	  	;this is the forward velocity of the object relative to the direction its facing
	Field PHYS_Velocity_Vertical#	
	Field PHYS_Velocity_Rotation#
	Field Phys_velocity_rotation_Vertical#
 

	Field STATE_HitGround%	
	Field STATE_HitHead%	
	Field STATE_ACTIVE_CAMERA%			;this is the active camera player 1=true 0=false
	

  	Field VAL_PROTAG_NAME$				;this is the protagonists name

	Field VAL_OBJ_SCALE#				;this is the scale of the spheres
	Field VAL_MESH_SCALE#				;this is the scale of the mesh
		
	Field VAL_CORE_MESH_VERT_OFFSET#	;this is the vertical offset of the core vs the mesh\
	Field VAL_MESH_camera_VERT_OFFSET#	;this is the vertical offset of the core vs the mesh
	Field VAL_MESH_camera_HORIZ_OFFSET#	;this is the vertical offset of the core vs the mesh

	Field OBJ_CAMERA_PIVOT				;this is the location where the camera optimumly should be.

	Field OBJ_CORE						;center of the body mass. Basicly the pivot on which everything sits
	Field OBJ_MESH						;this is the mesh
	Field SPR_SHADOW					;this is the shadow near feet with animated sprite
	Field VAL_SHADOW_ALPHA_FADE%		;this is a boolean value that will allow us to cycle the shadow of the selected object
    Field VAL_SHADOW_ALPHA#				;this is a boolean value that will allow us to cycle the shadow of the selected object

	Field OBJ_TARGETING_ID%				;this is the id of the object that the protag is targeting

										;this is the texture used for the characters skin
	Field TEX_SKIN						;this is the texture of the players skin
 		 
										;deals with NPC actions
	 
	Field STATE_ACTIVITY%				;This is the activity that the protagonist is participating in
	Field VAL_PATIENCE%					;this current value determines how many cycles it takes before the player switches states
	Field VAL_CONST_MAX_PATIENCE%		;this is the max value of the protagonist patience

End Type 

 
 
;NAME: Environment
;USE:  For tracking  the environment  setup
;==================================== 
Type GAME_ENVIRONMENT 
	 Field MESH_LOOKUP							;this is the data  that we will be using for the mesh lookup
	  
End Type 




;NAME: Environment Land Tiles
;USE:  For tracking  the actual meshes and the meshes  
;==================================== 
Type GAME_ENVIRONMENT_LANDS
	Field MESH_TYPE
	Field MESH
End Type





;======================================================================================= 
;================================CONSTANTS==============================================
;=======================================================================================
; create constants
Const C_ENV_GRAVITY_ACCEL# = .025 ; def .17note this is the acceleration for each tick rather than second/assuming 60fps

Const COLL_PLAYER_TYPE = 1
Const COLL_ENVIRO_TYPE = 2

Const CD_ELIPS_ELIPS = 1
Const CD_ELIPS_POLY = 2
Const CD_ELIPS_box = 3

Const CR_FULLSTOP = 1 
Const CR_FULLSLIDE = 2
Const CR_NOSLIDE = 3
 
Const  PLAYER_ROTATE_SPEED = 1	;speed at which the user likes to have their player rotate
Const  Const_MAX_FALL_SPEED = -2.8
Const  CONST_TILE_SIZE% = 1024
Const  CONST_TILE_REMOVE_DISTANCE% = CONST_TILE_SIZE% * 3
 
;ScaleImage cursor ,2,2
;   Load textures
;====================================================

; Create user defined object objects
;====================================================
Global OBJ_CAMERA=CreateCamera( )


 
Global PLAYER_ENTITY.ProtagonistType  = New ProtagonistType
Global ENVIRONMENT.GAME_ENVIRONMENT = New GAME_ENVIRONMENT		;this is the  environment variables
Global ENVIRONMENT_SECTIONS.GAME_ENVIRONMENT_LANDS	;=	New GAME_ENVIRONMENT_LANDS	;this is the meshes
environment\MESH_LOOKUP = CreateDataBank( 4)



;============= DEBUG  
;============= DEBUG 
;============= DEBUG 
mysphere = CreateSphere(10)
;myplane = 	CreatePlane ()
 

Global mytexture1 = LoadTexture("./3.jpg")
Global TEX_BULKHEAD = LoadTexture( "./3.jpg" ) 
 
 

EntityTexture mysphere ,mytexture1
 

;  START GAME LOOP
;======================================================== 
    Render_Load ("Organizing Objects...")

	ENVIRONMENT_INIT()
	

    Render_Load ("Setting Up Player ...")
 
	PLAYER_LOAD     ( PLAYER_ENTITY, 1 )	;load the captain  and change camera view to this one
 


	 
	
  

 ;===debug stuff remove later

  

;=====================================MAIN APPLICATION LOOP=================================
;=====================================MAIN APPLICATION LOOP=================================
;=====================================MAIN APPLICATION LOOP=================================
;=====================================MAIN APPLICATION LOOP=================================
;=====================================MAIN APPLICATION LOOP=================================


While Not KeyDown( 1 ) 
 
	UpdateWorld
	
	RenderWorld
  

;MoveEntity (	PLAYER_ENTITY\OBJ_CORE ,1,0,1)
	COORDINATE_AVATAR()
 	ENVIRONMENT_TILE_CHECKADD(PLAYER_ENTITY\OBJ_MESH )

	;Text 1,200, "  > " +	EntityX(PLAYER_ENTITY\OBJ_CORE)+ "   " + " " + EntityZ(PLAYER_ENTITY\OBJ_CORE)
	;Text 1,10,  " N> " +	ENVIRONMENT_GET_TILENAME("N", EntityX (PLAYER_ENTITY\OBJ_CORE) , EntityZ (PLAYER_ENTITY\OBJ_CORE))
	;Text 1,30,  " E> " +	ENVIRONMENT_GET_TILENAME("E", EntityX (PLAYER_ENTITY\OBJ_CORE) , EntityZ (PLAYER_ENTITY\OBJ_CORE))
	;Text 1,50,  " S> " +	ENVIRONMENT_GET_TILENAME("S", EntityX (PLAYER_ENTITY\OBJ_CORE) , EntityZ (PLAYER_ENTITY\OBJ_CORE))
	;Text 1,70,  " W> " +	ENVIRONMENT_GET_TILENAME("W", EntityX (PLAYER_ENTITY\OBJ_CORE) , EntityZ (PLAYER_ENTITY\OBJ_CORE))
	;Text 1,90,  " X> " +	ENVIRONMENT_GET_TILENAME("X", EntityX (PLAYER_ENTITY\OBJ_CORE) , EntityZ (PLAYER_ENTITY\OBJ_CORE))


	;Text 1,110,  " NE> " +	ENVIRONMENT_GET_TILENAME("NE", EntityX (PLAYER_ENTITY\OBJ_CORE) , EntityZ (PLAYER_ENTITY\OBJ_CORE))
	;Text 1,130,  " SE> " +	ENVIRONMENT_GET_TILENAME("SE", EntityX (PLAYER_ENTITY\OBJ_CORE) , EntityZ (PLAYER_ENTITY\OBJ_CORE))
	;Text 1,150,  " SW> " +	ENVIRONMENT_GET_TILENAME("SW", EntityX (PLAYER_ENTITY\OBJ_CORE) , EntityZ (PLAYER_ENTITY\OBJ_CORE))
	;Text 1,170,  " NW> " +	ENVIRONMENT_GET_TILENAME("NW", EntityX (PLAYER_ENTITY\OBJ_CORE) , EntityZ (PLAYER_ENTITY\OBJ_CORE))
 
	;Text 1,300,  "POLYS : > " + TrisRendered()
	   ENVIRONMENT_TILE_CHECKREMOVE(  PLAYER_ENTITY\OBJ_MESH  )
	;If KeyHit(2)
	;	ENVIRONMENT_TILE_CHECKADD(  PLAYER_ENTITY\OBJ_CORE )
	;End If
  	Flip 

	Wend 



End ;end of app


;====================================== END OF APPLICATION =========================================
;===================================================================================================

;===================================================================================================
;===================================================================================================
;===================================================================================================
;===================================================================================================
;===================================================================================================
;===================================================================================================
;===================================================================================================
;===================================================================================================
;===================================================================================================
;===================================================================================================
 
;===================================================================================================
;===================================================================================================
;===================================================================================================
;===================================================================================================
;===================================================================================================
;===================================================================================================
 
;===================================================================================================

;===================================================================================================
;====================================== START OF FUNCTIONS =========================================
 

 





 
   




;================================== LOAD THE ONSHIP ENVIRONMENT
Function  ENVIRONMENT_INIT()
 ;WireFrame  True 
 	AmbientLight 200,200,200
    CameraRange OBJ_CAMERA,1,CONST_TILE_SIZE% * 2

	;set up all collision types
 	 Collisions COLL_PLAYER_TYPE,	COLL_PLAYER_TYPE, CD_ELIPS_ELIPS ,CR_NOSLIDE
	 Collisions COLL_PLAYER_TYPE,	COLL_ENVIRO_TYPE, CD_ELIPS_POLY , CR_NOSLIDE


	;load all of the mesh info about the areas around the player from the save file 
	;when the player Last stopped (Or where they are when they start)
	 ENVIRONMENT_STARTLOAD()
	

	 	
End Function




;=================================================================================================
;FUNCTION ::   
;
;NOTE:  this functionis provided with the compass direction  of interest, and the 
;		current mesh the player is on, this function returns what the meshType  for that direction
;       
;
;=================================================================================================
Function ENVIRONMENT_GET_TILEID$(STR_DIR$,LOC_X#,LOC_Z#)

 Local ABS_LOC_X% =    (LOC_X# / CONST_TILE_SIZE%)  
 
Local ABS_LOC_Z% =     (LOC_Z# / CONST_TILE_SIZE%) 

 
Local MESHTYPE$
 

Select STR_DIR$

Case "N"
	     MESHTYPE$ = "TILE:" + ( MATH_FC (ABS_LOC_X%)*  CONST_TILE_SIZE% ) 					+ "," + (MATH_FC(ABS_LOC_Z%)*  CONST_TILE_SIZE%+ CONST_TILE_SIZE%)

Case "NE"
	 	 MESHTYPE$ = "TILE:" + ( MATH_FC (ABS_LOC_X%)*  CONST_TILE_SIZE%+ CONST_TILE_SIZE%) + "," + (MATH_FC (ABS_LOC_Z%)*  CONST_TILE_SIZE%+ CONST_TILE_SIZE%)

Case "E"
	 	 MESHTYPE$ = "TILE:" + ( MATH_FC (ABS_LOC_X%)*  CONST_TILE_SIZE%+ CONST_TILE_SIZE%) + "," + (MATH_FC (ABS_LOC_Z%)*  CONST_TILE_SIZE%)

Case "SE"
	 	 MESHTYPE$ = "TILE:" + ((MATH_FC (ABS_LOC_X%)*  CONST_TILE_SIZE%) + CONST_TILE_SIZE% )+ "," + ( MATH_FC (ABS_LOC_Z%)*  CONST_TILE_SIZE%- CONST_TILE_SIZE%)

Case "S"
	 	 MESHTYPE$ = "TILE:" + ( MATH_FC (ABS_LOC_X%)*  CONST_TILE_SIZE%) + "," + (MATH_FC (ABS_LOC_Z%)*  CONST_TILE_SIZE%- CONST_TILE_SIZE%)

Case "SW"
	 	 MESHTYPE$ = "TILE:" + ( MATH_FC (ABS_LOC_X%)*  CONST_TILE_SIZE%- CONST_TILE_SIZE%) + "," + (MATH_FC (ABS_LOC_Z%)*  CONST_TILE_SIZE%- CONST_TILE_SIZE%)

Case "W"
	 	 MESHTYPE$ = "TILE:" + ( MATH_FC (ABS_LOC_X%)*  CONST_TILE_SIZE%- CONST_TILE_SIZE%) + "," + MATH_FC (ABS_LOC_Z%)*  CONST_TILE_SIZE%

Case "NW"
	 	 MESHTYPE$ = "TILE:" + ( MATH_FC (ABS_LOC_X%)*  CONST_TILE_SIZE%- CONST_TILE_SIZE%) + "," + (MATH_FC (ABS_LOC_Z%)*  CONST_TILE_SIZE%+ CONST_TILE_SIZE%)

Case "X"
	 	 MESHTYPE$ = "TILE:" + ( MATH_FC (ABS_LOC_X%)*  CONST_TILE_SIZE%) + "," + MATH_FC (ABS_LOC_Z%)*  CONST_TILE_SIZE%
	
End Select
  
Return  "" +   GetFloatField (environment\MESH_LOOKUP , MESHTYPE$)   


End Function




;=================================================================================================
;FUNCTION ::   
;
;NOTE:  this functionis provided with the compass direction  of interest, and the 
;		current mesh the player is on, this function returns what the meshType  for that direction
;       
;
;=================================================================================================
Function ENVIRONMENT_GET_TILENAME$(STR_DIR$,LOC_X#,LOC_Z#)

 Local ABS_LOC_X% =    (LOC_X# / CONST_TILE_SIZE%)  
 
Local ABS_LOC_Z% =     (LOC_Z# / CONST_TILE_SIZE%) 

 
Local MESHTYPE$
 

Select STR_DIR$

Case "N"
	     MESHTYPE$ = "TILE:" + ( MATH_FC (ABS_LOC_X%)*  CONST_TILE_SIZE% ) 					+ "," + (MATH_FC(ABS_LOC_Z%)*  CONST_TILE_SIZE%+ CONST_TILE_SIZE%)

Case "NE"
	 	 MESHTYPE$ = "TILE:" + ( MATH_FC (ABS_LOC_X%)*  CONST_TILE_SIZE%+ CONST_TILE_SIZE%) + "," + (MATH_FC (ABS_LOC_Z%)*  CONST_TILE_SIZE%+ CONST_TILE_SIZE%)

Case "E"
	 	 MESHTYPE$ = "TILE:" + ( MATH_FC (ABS_LOC_X%)*  CONST_TILE_SIZE%+ CONST_TILE_SIZE%) + "," + (MATH_FC (ABS_LOC_Z%)*  CONST_TILE_SIZE%)

Case "SE"
	 	 MESHTYPE$ = "TILE:" + ((MATH_FC (ABS_LOC_X%)*  CONST_TILE_SIZE%) + CONST_TILE_SIZE% )+ "," + ( MATH_FC (ABS_LOC_Z%)*  CONST_TILE_SIZE%- CONST_TILE_SIZE%)

Case "S"
	 	 MESHTYPE$ = "TILE:" + ( MATH_FC (ABS_LOC_X%)*  CONST_TILE_SIZE%) + "," + (MATH_FC (ABS_LOC_Z%)*  CONST_TILE_SIZE%- CONST_TILE_SIZE%)

Case "SW"
	 	 MESHTYPE$ = "TILE:" + ( MATH_FC (ABS_LOC_X%)*  CONST_TILE_SIZE%- CONST_TILE_SIZE%) + "," + (MATH_FC (ABS_LOC_Z%)*  CONST_TILE_SIZE%- CONST_TILE_SIZE%)

Case "W"
	 	 MESHTYPE$ = "TILE:" + ( MATH_FC (ABS_LOC_X%)*  CONST_TILE_SIZE%- CONST_TILE_SIZE%) + "," + MATH_FC (ABS_LOC_Z%)*  CONST_TILE_SIZE%

Case "NW"
	 	 MESHTYPE$ = "TILE:" + ( MATH_FC (ABS_LOC_X%)*  CONST_TILE_SIZE%- CONST_TILE_SIZE%) + "," + (MATH_FC (ABS_LOC_Z%)*  CONST_TILE_SIZE%+ CONST_TILE_SIZE%)

Case "X"
	 	 MESHTYPE$ = "TILE:" + ( MATH_FC (ABS_LOC_X%)*  CONST_TILE_SIZE%) + "," + MATH_FC (ABS_LOC_Z%)*  CONST_TILE_SIZE%
	
End Select
Return  "" + MESHTYPE$ 


End Function
	
	

  


;=================================================================================================
;FUNCTION ::  
;
;NOTE:  
;
;=================================================================================================
Function ENVIRONMENT_STARTLOAD(POS_X%=0,POS_Z%=0)
;load environmental meshes IDS
;////////////////////////////////////// REPLACE BELOW \|/ WITH LOAD FROM FILE ///////////////////////////
;////////////////////////////////////// load the meshes that surround the character          ///////////
 
Local x_LOC$ 
Local z_LOC$ 
Local TERRAIN_ID$ = ""	;this is the type/name of the terrain square
 


; Open the file to Read 
Local ENVIROFILE

ENVIROFILE = ReadFile("./DATA/SAVEDLOCAREA.txt") 

; Lets read the Greatest score from the file 


For x% = 1 To 9

	temp_data$ =   ReadLine$( ENVIROFILE ) 
	;Print "READ IN :> " + temp_data$
	INT_POS_firstcomma%  = Instr( ""+ temp_data$,",",1 )
	INT_POS_SECONDcomma% = Instr( ""+ temp_data$,",", INT_POS_firstcomma%+1 )
	int_POS_THIRDCOMMA%  = Instr( ""+ TEMP_DATA$,",", INT_POS_SECONDCOMMA%+1)


 	x_LOC$      =  Mid$(temp_data$ , 1, INT_POS_firstcomma%-1 )   
  	z_LOC$      =  Mid$(temp_data$ , INT_POS_firstcomma% +1  ,   ( INT_POS_SECONDcomma% - INT_POS_firstcomma% )-1 )
  	TERRAIN_ID$ =  Mid$(temp_data$ , INT_POS_SECONDcomma% +1  ,   ( INT_POS_THIRDcomma% - INT_POS_SECONDcomma% )-1 )

	 ;DebugLog  "LOADED " + "TILE:" + x_LOC$ + ","+ z_LOC$  + " > " +  TERRAIN_ID$
	
	
	SetFloatField environment\MESH_LOOKUP , "TILE:" + x_LOC$ + ","+ z_LOC$  , TERRAIN_ID$
	ENVIRONMENT_TILE_LOAD(TERRAIN_ID$,x_LOC ,z_LOC)

Next   
 
  
; Close the file once reading is finished 
CloseFile( ENVIROFILE ) 

 

End Function








;=================================================================================================
;FUNCTION :: Load Area
;
;
;PARAMETERS:
;	AREATYPE ID  :: this is the name of the tile. Ie the type to display
;   LOCX,LOCZ	:: this is the location to place the tile at
;   see ENVIRONMENT_GET_TILEID$ FOR how to look up the tileID
;
;NOTE: Loads the mesh area for the target land ID
;
;=================================================================================================

Function ENVIRONMENT_TILE_LOAD( STR_AREATYPE_ID$,LOCX# ,LOCZ#  )


ENVIRONMENT_TILE.GAME_ENVIRONMENT_LANDS	=	New GAME_ENVIRONMENT_LANDS

ENVIRONMENT_TILE\mesh =   LoadMesh ("./1.x");

EntityTexture ENVIRONMENT_TILE\mesh, TEX_BULKHEAD
 

 


	EntityAlpha    ENVIRONMENT_TILE\mesh,1

 	EntityAlpha    ENVIRONMENT_TILE\mesh,1
	PositionEntity ENVIRONMENT_TILE\mesh,locx#,0,locz#
	EntityTexture  ENVIRONMENT_TILE\mesh, mytexture1
	PositionEntity ENVIRONMENT_TILE\mesh, locx#,0,locz#
	EntityType     ENVIRONMENT_TILE\mesh,COLL_ENVIRO_TYPE
 
	;EntityAutoFade ENVIRONMENT_TILE\mesh, CONST_TILE_SIZE%+(CONST_TILE_SIZE%/2),CONST_TILE_REMOVE_DISTANCE%

    DebugLog "ADDED :> TILE" + STR_AREATYPE_ID$ + "    @  X:" +  EntityX(ENVIRONMENT_TILE\mesh) + " , Y:" EntityZ( ENVIRONMENT_TILE\mesh)
 

End Function



;=================================================================================================
;FUNCTION ::  
;
;NOTE:  
;
;=================================================================================================
Function ENVIRONMENT_TILE_ADD(STR_DIR$,LOC_X#,LOC_Z#)
 

 
Local INT_POS_firstcomma% ,INT_POS_SECONDcomma% , STR_TILENAME$
 
    STR_TILENAME$ = Replace$ ( Upper (""+ ENVIRONMENT_GET_TILENAME$(STR_DIR$,LOC_X#,LOC_Z#)), "TILE:", "")

 	INT_POS_firstcomma%  = Instr( STR_TILENAME$ , "," , 1 						)
	INT_POS_SECONDcomma% = Len(STR_TILENAME$)



 	x_LOC%      = Int( Mid$(STR_TILENAME$ , 1, INT_POS_firstcomma%-1 )  ) 
  	z_LOC%      = Int( Mid$(STR_TILENAME$ , INT_POS_firstcomma% +1  ,   ( INT_POS_SECONDcomma% - INT_POS_firstcomma% ) )  )
    ENVIRONMENT_TILE_LOAD( ENVIRONMENT_GET_TILEID$(STR_DIR$,x_LOC% ,z_LOC% ), x_LOC%,  z_LOC%   )
 
End Function





;=================================================================================================
;FUNCTION ::  
;
;NOTE:  
;
;=================================================================================================
Function ENVIRONMENT_TILE_CHECKREMOVE( TARGET_MESH)
 
For TARGET_TILE.GAME_ENVIRONMENT_LANDS=Each GAME_ENVIRONMENT_LANDS
 
If EntityDistance# ( TARGET_MESH,TARGET_TILE\mesh) > CONST_TILE_REMOVE_DISTANCE% Then	
	  ENVIRONMENT_TILE_DESTROY(TARGET_TILE)
End If



Next 

End Function










;=================================================================================================
;FUNCTION ::  
;
;NOTE:  
;
;=================================================================================================
Function ENVIRONMENT_TILE_CHECKADD(TARGET_MESH)

 
 
 
;NOTES:
;To add more 'range' to the pop in tiles, simply call the tile exists for a + range
  ;And ensure that the deletes dont kill the New one
 
  
	
Select 0 ;true 
Case ENVIRONMENT_TILE_EXISTS( ENVIRONMENT_GET_TILENAME$("X",EntityX(TARGET_MESH), EntityZ(TARGET_MESH) ))   
 	DebugLog "ADD X"
	ENVIRONMENT_TILE_ADD("X",EntityX(TARGET_MESH), EntityZ(TARGET_MESH) )
Case ENVIRONMENT_TILE_EXISTS( ENVIRONMENT_GET_TILENAME$("N",EntityX(TARGET_MESH), EntityZ(TARGET_MESH) ) )    
   	DebugLog "ADD N"
	ENVIRONMENT_TILE_ADD("N",EntityX(TARGET_MESH), EntityZ(TARGET_MESH) )
Case ENVIRONMENT_TILE_EXISTS( ENVIRONMENT_GET_TILENAME$("E",EntityX(TARGET_MESH), EntityZ(TARGET_MESH) ) )    
   	DebugLog "ADD E"
	ENVIRONMENT_TILE_ADD("E",EntityX(TARGET_MESH), EntityZ(TARGET_MESH) )
Case ENVIRONMENT_TILE_EXISTS( ENVIRONMENT_GET_TILENAME$("S",EntityX(TARGET_MESH), EntityZ(TARGET_MESH) ) )   
  	DebugLog "ADD S"
	ENVIRONMENT_TILE_ADD("S",EntityX(TARGET_MESH), EntityZ(TARGET_MESH) )
Case ENVIRONMENT_TILE_EXISTS(  ENVIRONMENT_GET_TILENAME$("W",EntityX(TARGET_MESH), EntityZ(TARGET_MESH) ) )   
  	DebugLog "ADD W"
	ENVIRONMENT_TILE_ADD("W",EntityX(TARGET_MESH), EntityZ(TARGET_MESH) )
Case ENVIRONMENT_TILE_EXISTS( ENVIRONMENT_GET_TILENAME$("NE",EntityX(TARGET_MESH), EntityZ(TARGET_MESH) ) )    
  	DebugLog "ADD NE"
	ENVIRONMENT_TILE_ADD("NE",EntityX(TARGET_MESH), EntityZ(TARGET_MESH) )
Case ENVIRONMENT_TILE_EXISTS( ENVIRONMENT_GET_TILENAME$("SE",EntityX(TARGET_MESH), EntityZ(TARGET_MESH) ) )    
  	DebugLog "ADD SE"
	ENVIRONMENT_TILE_ADD("SE",EntityX(TARGET_MESH), EntityZ(TARGET_MESH) )
Case ENVIRONMENT_TILE_EXISTS( ENVIRONMENT_GET_TILENAME$("SW",EntityX(TARGET_MESH), EntityZ(TARGET_MESH) ) )   
  	DebugLog "ADD SW"
	ENVIRONMENT_TILE_ADD("SW",EntityX(TARGET_MESH), EntityZ(TARGET_MESH) )
Case ENVIRONMENT_TILE_EXISTS(  ENVIRONMENT_GET_TILENAME$("NW",EntityX(TARGET_MESH), EntityZ(TARGET_MESH) ) )   
 	DebugLog "ADD NW"
	ENVIRONMENT_TILE_ADD("NW",EntityX(TARGET_MESH), EntityZ(TARGET_MESH) )
Default 
 
 End Select 
 




End Function






;=================================================================================================
;FUNCTION ::  
;NOTE:  
;
;=================================================================================================


Function ENVIRONMENT_TILE_EXISTS%(STR_TILENAME_IN$)
 
For TARGET_TILE.GAME_ENVIRONMENT_LANDS=Each GAME_ENVIRONMENT_LANDS
 If STR_TILENAME_IN$ = ("TILE:" +   Int(EntityX (TARGET_TILE\Mesh)) +"," +Int(EntityZ(TARGET_TILE\Mesh)) ) Then
		Return     1
		  Exit

	End If
Next 
Return 0
End Function




 

 



;=================================================================================================;=
;=================================================================================================
Function ENVIRONMENT_TILE_DESTROY(TARGET_TILE_IN.GAME_ENVIRONMENT_LANDS)

	FreeEntity TARGET_TILE_IN\mesh
	Delete TARGET_TILE_IN
	DebugLog "DELETED TILE: " 


End Function
 
;=================================================================================================
;FUNCTION :: Get Input
;
;
;
;=================================================================================================
Function USER_INPUT(OBJECT_IN.ProtagonistType )

	 OBJECT_IN\PHYS_Velocity_Forward = 0
	 OBJECT_IN\PHYS_Velocity_Rotation =  0
 

	If  MouseDown(2) 	;forward
	 	
   		OBJECT_IN\PHYS_Velocity_Forward =  2 
	End If


	If  KeyDown (208) 	;back
	 	OBJECT_IN\PHYS_Velocity_Forward = -0.25 
	End If

	If  KeyDown (203) 	;turn left
	 	OBJECT_IN\PHYS_Velocity_Rotation =  3
	End If

	If  KeyDown (205) 	;turn right
	 	OBJECT_IN\PHYS_Velocity_Rotation = -3
	End If
	
	 
	
	
End Function 


;=================================================================================================
;FUNCTION :: Load PROtagonists
;
;NOTE: Loads the Protagonist from the datafile
;
;=================================================================================================

Function PLAYER_LOAD (OBJECT_IN.ProtagonistType, SETCAMERA# ) 

  

Local PLAYER_CAMERA_ZOOM_FIX# 	= 1.6
Local PLAYER_START_STATE%		=	2	;random movement
Local CORE_ALPHA#   = 	1
Local START_LOC_X# 	=	0
Local START_LOC_y# 	=	300
Local START_LOC_z# 	=	10

 
 

 

  		    OBJECT_IN\STATE_ACTIVE_CAMERA 			= SETCAMERA 

 

 


 
 	;set up mesh object
	    	OBJECT_IN\OBJ_MESH   = CreateCube ()  
	  		 EntityColor OBJECT_IN\OBJ_MESH,255,0,0
			RotateEntity OBJECT_IN\OBJ_MESH,PLAYER_MESH_ORIENT_X,PLAYER_MESH_ORIENT_y,PLAYER_MESH_ORIENT_z	
        	 
	 		 
 		 


 
		;set up the optimum location for the camera
			OBJECT_IN\OBJ_CAMERA_PIVOT = CreatePivot( OBJECT_IN\OBJ_CORE )
			PositionEntity OBJECT_IN\OBJ_CAMERA_PIVOT, 0,  PLAYER_CAMERA_OFFSET_v , PLAYER_CAMERA_OFFSET_H 

			If OBJECT_IN\STATE_ACTIVE_CAMERA = 1 Then
				 PositionEntity  OBJ_CAMERA, EntityX(OBJECT_IN\OBJ_CAMERA_PIVOT,1), EntityY(OBJECT_IN\OBJ_CAMERA_PIVOT,1),EntityZ(OBJECT_IN\OBJ_CAMERA_PIVOT,1)
			     PointEntity 	 OBJ_CAMERA, OBJECT_IN\OBJ_MESH 
			     EntityParent    OBJ_CAMERA, OBJECT_IN\OBJ_MESH ,1
				 CameraZoom  	 OBJ_CAMERA	,PLAYER_CAMERA_ZOOM_FIX#	
			End If
				
 
	 

    

End Function


;=================================================================================================
;FUNCTION :: Moves the player mesh
;
;
;
;=================================================================================================
Function CM_MOVE(OBJECT_IN.ProtagonistType)

	 MoveEntity     ( OBJECT_IN\OBJ_MESH    ,	0 ,0, OBJECT_IN\PHYS_Velocity_Forward)
	 ;PositionEntity ( OBJECT_IN\OBJ_MESH   ,	EntityX(OBJECT_IN\OBJ_CORE) , EntityY(OBJECT_IN\OBJ_CORE) - OBJECT_IN\VAL_CORE_MESH_VERT_OFFSET , EntityZ(OBJECT_IN\OBJ_CORE))
	 ;PositionEntity ( OBJECT_IN\SPR_SHADOW , 	EntityX(OBJECT_IN\OBJ_CORE) , EntityY(OBJECT_IN\OBJ_CORE) - OBJECT_IN\VAL_CORE_MESH_VERT_OFFSET , EntityZ(OBJECT_IN\OBJ_CORE))
 	 
	OBJECT_IN\PHYS_Velocity_Forward = 0
End Function 

;=================================================================================================
;FUNCTION :: rotate the player mesh
;
;
;
;=================================================================================================
Function CM_ROTATE(OBJECT_IN.ProtagonistType)

	If OBJECT_IN\PHYS_Velocity_Rotation <> 0 Then
	 	TurnEntity ( OBJECT_IN\OBJ_MESH ,   0, OBJECT_IN\PHYS_Velocity_Rotation  , 0)
	 	 
	End If
	OBJECT_IN\PHYS_Velocity_Rotation  = 0
 
	
	
End Function 


;=================================================================================================
;=================================================================================================

Function COORDINATE_AVATAR()
	 
		  For TARGET_ENTITY.ProtagonistType=Each ProtagonistType
		  		CM_MOVE          	  (TARGET_ENTITY		 )				;move the player based on velocity  
				CM_ROTATE        	  (TARGET_ENTITY		 )		
		 
		
				If (TARGET_ENTITY\STATE_ACTIVE_CAMERA) = 1    Then
					;do input directed to current protagonist
   					USER_INPUT (TARGET_ENTITY )
					COORDINATE_INTERIOR_CAMERA  (TARGET_ENTITY)
 
				End If
				
				
			
 		  Next 
End Function   



;=================================================================================================
;FUNCTION :: coordinate the position of the ENVIRONMENT\OBJ_CAMERA
;
;NOTE: We may want to add as an attribute  the object that the ENVIRONMENT\OBJ_CAMERA is to follow 
;
;=================================================================================================
 

Function COORDINATE_INTERIOR_CAMERA(OBJECT_IN.protagonisttype )
 
	
	myspeed = MouseYSpeed ( ) * .5 
	If EntityPitch(  OBJ_CAMERA ) + myspeed < -89
		pitch = -89
	ElseIf EntityPitch(  OBJ_CAMERA  ) + myspeed > 89
		pitch = 89
	Else
		pitch = EntityPitch( OBJ_CAMERA ) + myspeed
	EndIf
	 	
	yaw = MouseXSpeed() * -1 * .5 + EntityYaw(  OBJ_CAMERA)
	
	
	
	OBJECT_IN\PHYS_Velocity_Rotation = yaw
    
	OBJECT_IN\Phys_velocity_rotation_Vertical = pitch

	PositionEntity OBJ_CAMERA , EntityX(OBJECT_IN\OBJ_MESH ,1),EntityY(OBJECT_IN\OBJ_MESH ,1)+100,EntityZ(OBJECT_IN\OBJ_MESH ,1),1
	 
    RotateEntity   OBJ_CAMERA, pitch, EntityYaw( OBJ_CAMERA ), 0
	  
 	 MoveMouse GraphicsWidth()/2, GraphicsHeight()/2

		 
	
    
End Function





;=================================================================================================
;FUNCTION :: does the math for rounding
;
;NOTE: we use a float. we still need to determine precision. post this code too once we add precision
;
;=================================================================================================

Function MATH_ROUND# (IN_VALUE# ) 

Local temp#
 
	temp# = Int(IN_VALUE#  * 100 ) 
	temp# = temp# / 100	
	Return (temp#    )

End Function


;=================================================================================================
;FUNCTION ::  MATH FLOOR CEILING
;
;NOTE:  This function trims to the integer. 
;		Simply rounds To the nearest integer in the direction of zero
;		excellent function for simply trimming th e
;=================================================================================================

Function MATH_FC%(TARGET_VALUE#)

	If  TARGET_VALUE# >0 Then
		Return Floor(TARGET_VALUE#)
	End If

	If target_value <0 Then
		Return Ceil(TARGET_VALUE#)
	End If


End Function




 

 


;==================================================================================
;RENDER LOAD OF FEATURES
;==================================================================================
 
Function  RENDER_LOAD(strloaditem$)
 
		UpdateWorld
		Cls
		RenderWorld
	    Text GraphicsWidth()/3,GraphicsHeight()/2, "[ " + strloaditem + " ]"	;display what is loading
        Flip

End Function 






;==================================================================================
;MOVE CAMERA TO THE NEXT PLAYER
;==================================================================================
 
 




 








 


 


;=======================================================
;CENTERS THE MESH HANDLE INTO THE EXACT CENTER OF THE MESH
;CREDITS: Sorry but this isnt my code. I cant remember 
;         who i should pay credit to. =(
;======================================================
Function TOOLS_MESH_CENTER_HANDLE(mesh)
 ux#=-100000
 uy#=-100000
 uz#=-100000
 lx#=100000
 ly#=100000
 lz#=100000
 cs=CountSurfaces(mesh)
 For s=1 To cs
  surf=GetSurface(mesh,s)
  cv=CountVertices(surf)-1
  For v=0 To cv
   vx#=VertexX#(surf,v)
   vy#=VertexY#(surf,v)
   vz#=VertexZ#(surf,v)
   If vx#<lx# Then lx#=vx#
   If vx#>ux# Then ux#=vx#
   If vy#<ly# Then ly#=vy#
   If vy#>uy# Then uy#=vy#
   If vz#<lz# Then lz#=vz#
   If vz#>uz# Then uz#=vz#
  Next
 Next
 ax#=(ux#+lx#)/2
 ay#=(uy#+ly#)/2
 az#=(uz#+lz#)/2
 PositionMesh mesh,-ax#,-ay#,-az#
End Function









 



;=================================================
;
; DataBank system
;
; (c)2003 Ken Lynch
;
;=================================================

;=================================================
;
; Internal functions
;
;=================================================

;
; PokeString bank,offset,value$,length
;
Function PokeString(bank, offset, value$, length)
	value$ = LSet(value$, length)
	For i = 0 To length-1
		a = Asc(Mid(value$, i+1, 1))
		PokeByte bank, offset+i, a
	Next
End Function

;
; PeekString bank,offset,length
;
Function PeekString$(bank, offset, length)
	For i = 0 To length-1
		a = PeekByte(bank, offset+i)
		s$ = s$ + Chr(a)
	Next
	Return Trim(s$)
End Function

;
; AddField bank,name$,size
;
Function AddField(bank, name$, size)
	c = PeekInt(bank, 0)
	os = 20 * c + 4
	bs = BankSize(bank)
	ResizeBank bank, bs + 20 + size
	CopyBank bank, os, bank, os+20, bs-os
	PokeString bank, os, name$, 16
	PokeInt bank, os+16, size
	PokeInt bank, 0, c+1
	Return bs+20
End Function

;
; GetFieldOffset(bank,name$)
;
Function GetFieldOffset(bank, name$)
	c = PeekInt(bank, 0)
	offset = 20 * c + 4
	For i = 0 To c-1
		os = 20 * i + 4
		n$ = PeekString(bank, os, 16)
		If n$ = name$ Then Return offset
		offset = offset + PeekInt(bank, os+16)
	Next
	Return 0
End Function

;=================================================
;
; Create function
;
;=================================================

;
; CreateDataBank()
;
Function CreateDataBank(size%)
	bank = CreateBank(size%)
	Return bank
End Function

;=================================================
;
; Set field functions
;
;=================================================

;
; SetByteField bank,name$,value
;
Function SetByteField(bank, name$, value)
	offset = GetFieldOffset(bank, name$)
	If offset = 0 Then offset = AddField(bank, name$, 1)
	PokeByte bank, offset, value
End Function

;
; SetShortField bank,name$,value
;
Function SetShortField(bank, name$, value)
	offset = GetFieldOffset(bank, name$)
	If offset = 0 Then offset = AddField(bank, name$, 2)
	PokeShort bank, offset, value
End Function

;
; SetIntField bank,name$,value
;
Function SetIntField(bank, name$, value)
	offset = GetFieldOffset(bank, name$)
	If offset = 0 Then offset = AddField(bank, name$, 4)
	PokeInt bank, offset, value
End Function

;
; SetFloatField bank,name$,value#
;
Function SetFloatField(bank, name$, value#)
	offset = GetFieldOffset(bank, name$)
	If offset = 0 Then offset = AddField(bank, name$, 4)
	PokeFloat bank, offset, value#
End Function

;=================================================
;
; Get field functions
;
;=================================================

;
; GetByteField(bank,name$)
;
Function GetByteField(bank, name$)
	offset = GetFieldOffset(bank, name$)
	If offset = 0 Then Return 0
	Return PeekByte(bank, offset)
End Function

;
; GetShortField(bank,name$)
;
Function GetShortField(bank, name$)
	offset = GetFieldOffset(bank, name$)
	If offset = 0 Then Return 0
	Return PeekShort(bank, offset)
End Function

;
; GetIntField(bank,name$)
;
Function GetIntField(bank, name$)
	offset = GetFieldOffset(bank, name$)
	If offset = 0 Then Return 0
	Return PeekInt(bank, offset)
End Function

;
; GetFloatField#(bank,name$)
;
Function GetFloatField#(bank, name$)
	offset = GetFieldOffset(bank, name$)
	If offset = 0 Then Return 0
	Return PeekFloat(bank, offset)
End Function
