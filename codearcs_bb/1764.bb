; ID: 1764
; Author: Iamhere
; Date: 2006-07-27 04:10:55
; Title: AnimB3D
; Description: Complete Bone-Animation Program

Include "C:\Blitz3d\userlibs\keys.bb"



;#Region Types

	
Type TEXS        
	Field name$      
	Field flags       
	Field blend       
	Field xpos#      
	Field ypos#
	Field xscale#
	Field yscale#
	Field rot#
End Type
Global texs.texs


Type BRUS
	Field name$
	Field red#
	Field green#
	Field blue#
	Field alpha#
	Field shine#
	Field blend
	Field fx
	Field texID[7]
End Type
Global brus.brus


Type VRTS
	Field x#
	Field y#
	Field z#
	Field nx#
	Field ny#
	Field nz#
	Field red#
	Field green#
	Field blue#
	Field alpha#
	Field tex_coords#[32]
End Type
Global vrts.vrts

Type TRIS
    Field brushid
    Field vxbank
    Field anztris
End Type
Global tris.tris

Dim CountVerts(1)

Global fsize, outoffile

Type node
	Field name$
	Field parent
    Field parentHD
	Field aktchild
	Field childbank	
	Field anzchild	
	Field num			
	Field lastChild
	Field ChunkNodeBank
	Field VXanz  
	Field bonebank
	Field KEYSflags
	Field key1bank
	Field key2bank
	Field key3bank   
	Field nchunk$
	Field nchunksize
	Field nchunkFP
	Field endchunkFP
	Field posX#
	Field posY#
	Field posZ#
	Field scaleX#
	Field scaleY#
	Field scaleZ#
	Field rotW#
	Field rotX#
	Field rotY#
	Field rotZ#
	Field Bone
    Field sphere
    Field bsphere
    Field spiv
    Field bsphereparent
End Type


Dim Modus(100) 
Dim outmode(6)
Dim wfi(6)
Dim wfmode(6)

;#End Region

;#Region Settings
;  Settings

startup()

.start
Dim modus(0)
Dim merke(39)
SeedRnd MilliSecs()

bnx = CreateBank(100)
;Menu from East-Power-Soft
Dim Mnu$(20),MnuX(20);,MnuIcon(20,20)  ;--> Menüeinträge, Position und Handle für Icons
                                      ;--> Hier den Wert 20 ändern wenn mehr Einträge benötigt werden
Dim FFRQ$(0)
Dim DFRQ$(0)
Dim frqSel$(0)                                     
 
Dim Gtext$(49)
 
Global node.node
Dim tempArray(1)
Dim banks(1)

Global anzBanks = 0
Global KeyIsLoad = 0
Global FirstNodeHD

Global rotspeed# = 0.2
Global movespeed# = 0.02
Global scalespeed# = 0.02

Global rt1speed# = 0.05
Global mv1speed# = 0.005
Global sc1speed# = 0.005

Global rt2speed# = 0.2
Global mv2speed# = 0.02
Global sc2speed# = 0.02

Global rt3speed# = 0.4
Global mv3speed# = 0.04
Global sc3speed# = 0.04

Global fx = 16
Global aktualModus
Global differentVertexMode = 0
Global SaveFirstFrameNull = 0
Global FrameStart = 1

Local hasanim = 0
Global E_RM_mode = 1
Global aktbonespeed = 1
Global rotBonespeed# = 0.5
Global moveBonespeed# = 0.04
Global bonespeedS# = 0.005
Global bonespeedM# = 0.04
Global bonespeedF# = 0.1

Global scrnd# = 0.02
Global weight# = 1.0
 
Global machfarbig = 0
Local dummyname = 0
Local ERRORnode1 = 0
Local ERRORnode2 = 0
Local ErrorBONE = 0
Local ErrorKEYS = 0
Local ErrorFILEEND = 0

Global bonemodus = 1
font=LoadFont( "verdana",16 )
bigfont=LoadFont( "verdana",30 )
SetFont font

tex=CreateTexture(128,128,12)
SetBuffer TextureBuffer(tex)

Color 50,60,80
Rect 0,0,128,20,1
Rect 0,0,20,128
Color 100,120,140
Rect 0,5,128,10,1
Rect 5,0,10,128

ScaleTexture tex,0.001,0.001
plane=CreateCube()
ScaleEntity plane,1000,0.001,1000
EntityTexture plane,tex
;EntityOrder plane, 20
EntityAlpha plane,0.6


SetBuffer BackBuffer()
piv = CreatePivot()
PositionEntity piv, 0, 5, 0
camera = CreateCamera()
PositionEntity camera, 0, 0, -10
EntityParent Camera, piv, 0
CameraRange Camera, 0.01, 1000
light = CreateLight(1,piv)
PositionEntity light,0,0,-0
;light2 = CREATELIGHT(1,piv)
;Positionentity light2,-200,2,-200
;light3 = CREATELIGHT(1,piv)
;Positionentity light3,200,2,200
;light4 = CREATELIGHT(1,piv)
;Positionentity light4,-200,2,200
;Lightrange light,1000
RotateEntity light, 90, 0, 0
;ROTATEENTITY light2, 90, 90, 0
;ROTATEENTITY light3, 90, 180, 0
;ROTATEENTITY light4, 90, 270, 0



darky = CreateCube (camera)
EntityColor darky, 15,27,30
EntityAlpha darky,0.9
MoveEntity darky,0,0,2
EntityOrder darky, -20
HideEntity darky

rot = CreateBrush (255, 0, 0)
;BrushFX rot,1
BrushAlpha rot,0.7
gruen = CreateBrush (0, 255, 0)
;BrushFX gruen,1
BrushAlpha gruen,0.7
blau = CreateBrush (0, 0, 255)
;BrushFX blau,1
BrushAlpha blau,0.7
gelb = CreateBrush (255, 255, 0)
;BrushFX gelb,1
BrushAlpha gelb,0.7
hellblau = CreateBrush (0, 255, 255)
;BrushFX hellblau,1
BrushAlpha hellblau,0.7
violett = CreateBrush (255, 0, 255)
;BrushFX violett,1
BrushAlpha violett,0.7
weiss = CreateBrush (255, 255, 255)
;BrushFX weiss,1
BrushAlpha weiss,0.7

wg10 = CreateBrush(65,60,90)
;BrushFX wg10,1
BrushAlpha wg10,0.7
wg20 = CreateBrush(50,70,140)
;BrushFX wg20,1
BrushAlpha wg20,0.7
wg30 = CreateBrush(40,130,150)
;BrushFX wg30,1
BrushAlpha wg30,0.7
wg40 = CreateBrush(30,200,150)
;BrushFX wg40,1
BrushAlpha wg40,0.7
wg50 = CreateBrush(25,240,120)
;BrushFX wg50,1
BrushAlpha wg50,0.7
wg60 = CreateBrush(255,255,0)
;BrushFX wg60,1
BrushAlpha wg60,0.7
wg70 = CreateBrush(255,210,10)
;BrushFX wg70,1
BrushAlpha wg70,0.7
wg80 = CreateBrush(255,160,20)
;BrushFX wg80,1
BrushAlpha wg80,0.7
wg90 = CreateBrush(255,100,30)
;BrushFX wg90,1
BrushAlpha wg90,0.7
wg100 = CreateBrush(255,60,40)
;BrushFX wg100,1
BrushAlpha wg100,0.7


;Kreuz
xyz = CreateCube()
PositionMesh xyz,0,0,0
PaintMesh xyz,rot
ScaleMesh xyz,1,0.02,0.02

greenY =CreateCube()
PositionMesh greenY,0,0,0
PaintMesh greenY,gruen
ScaleMesh greenY,0.02,1,0.02
AddMesh greenY,xyz
FreeEntity greenY

blueZ = CreateCube()
PositionMesh blueZ,0,0,0
PaintMesh blueZ,blau
ScaleMesh blueZ,0.02,0.02,1
AddMesh blueZ,xyz
FreeEntity blueZ

;MoveEntity xyz,0,0,5
EntityAlpha xyz,0.5
;HideEntity xyz
EntityOrder XYZ,-10
ScaleEntity xyz,0.1,0.1,0.1

AppTitle "AnimB3D Version 057d Beta","Are you sure ? "

HidePointer

;Menu from East-Power-Soft
Global MnuBackC: MnuBackC=$033D4E     ;--> Farbe Hintergrund (Menü)  --- color background menu
Global MnuForeC: MnuForeC=$9CD1C7     ;--> Farbe Vordergrund (Menü)  --- color foreground menu
Global MnuBorderH: MnuBorderH=$9CD1C7 ;--> Farbe SUB-Menü-Rahmen (hell)  --- light color framework submenu
Global MnuBorderD: MnuBorderD=$347265 ;--> Farbe SUB-Menü-Rahmen (dunkel) --- dark color framework submenu

Global MnuBackM: MnuBackM=$B6BDD2     ;--> Farbe Hintergrund (Markierung)  --- selection color  background
Global MnuForeM: MnuForeM=$9CD1C7     ;--> Farbe Vordergrund (Markierung)  --- selection color  foreground
Global MnuBorderM: MnuBorderM=$4877BD ;--> Farbe Rahmen (Markierung)       --- selection color framework

Global MnuPosX:MnuPosX=0              ;--> Menüversatz X (falls das Menü nicht oben links sitzen soll)  --- menu-offset X
Global MnuPosY:MnuPosY=0              ;--> Menüversatz Y (falls das Menü nicht oben links sitzen soll)  --- menu-offset Y

Global MnuState, MnuActiv             ;--> Menüstatus, Submenü Aktivität   

Global MnuFont
MnuFont=LoadFont("tahoma",13) ;--> empfohlene Schriftart
;Global Mouse: Mouse=LoadImage("system\mouse.png"):MaskImage Mouse,255,0,255



;
;#End Region

;#Region Load B3D
; Load Anim 
.loadMeshAnim;

    ; Load the Mesh 
   Pfad$=CurrentDir$ ()
.dofilein
   filename$ = ListDir$(Pfad$, "                          Select a B3D File","L","F",".b3d")
   If  Trim$(filename$) = "" 
        Goto auscl
   ElseIf Instr(Upper(filename$) , ".B3D") = 0 
        Goto dofilein
   EndIf

   Pos=Instr (filename$, "\",1)
    Repeat
        Pos2 = Pos
        If Pos > 0									
            Pos=Instr (filename$, "\",Pos+1)
        EndIf
    Until Pos = 0
    If Pos2 > 0 
         pfad2$ = Left$ (filename$, Pos2)
         ChangeDir pfad2$
    EndIf

                                    
infile = ReadFile(filename$)
fsize = FileSize(filename$)
fn2$ = filename$

i = 0

theanim = LoadAnimMesh(filename$)
EntityFX theanim,0
savefilename$ = filename$
BB3Dchunk$ = Read4Char$(infile)
BB3Dchunksize = ReadInt( infile )
BB3Dversion = ReadInt( infile )
seqi = 0
boni = 0
keyi = 0
zmesh = 0
;HideEntity themesh
HideEntity theanim


gw = GraphicsWidth()
gh = GraphicsHeight()
gw2 = gw/2
gh2 = gh/2
;

Repeat

chunk$ = Read4Char$(infile) 


	Select chunk$
    ; CASE TEXS
		Case "TEXS"
			TEXSchunk$ = chunk$
			TEXSchunksize = ReadInt( infile )
			fp = FilePos( infile )
			Repeat
				texs.texs = New texs
				;txhd = handle(texs)
				texs\name = ReadNullString$(infile)
				TEXS\flags = ReadInt( infile )
				TEXS\blend = ReadInt( infile )
				TEXS\xpos# = ReadFloat( infile )
				TEXS\ypos# = ReadFloat( infile )
				TEXS\xscale# = ReadFloat( infile )
				TEXS\yscale# = ReadFloat( infile )
				TEXS\rot# = ReadFloat( infile )
				If FilePos( infile ) > fp+TEXSchunksize 
					RuntimeError "ERROR in TEXS chunk"
					Exit
				EndIf
			Until FilePos( infile ) >= fp+TEXSchunksize

;
    ; CASE BRUS
		Case "BRUS"
			BRUSchunk$ = chunk$
			BRUSchunksize = ReadInt( infile )
			fp = FilePos( infile )
			BRUSntexs = ReadInt( infile )
			
			Repeat
				brus.brus = New brus
				;brhd = handle(brus)
				;brus.brus = Object.brus(brhd)
				BRUS\name$ = ReadNullString$(infile)
				BRUS\red# = ReadFloat( infile )
				BRUS\green# = ReadFloat( infile )
				BRUS\blue# = ReadFloat( infile )
				BRUS\alpha# = ReadFloat( infile )
				BRUS\shine# = ReadFloat( infile )
				BRUS\blend = ReadInt( infile )
				BRUS\fx = ReadInt( infile )
				For k = 0 To BRUSntexs-1
					BRUS\texid[k] = ReadInt( infile )
				Next
				If FilePos( infile ) > fp+BRUSchunksize 
					RuntimeError "ERROR in BRUS chunk"
					Exit
				EndIf
	
			Until FilePos( infile ) >= fp+BRUSchunksize
			AnzBrush = i-1


;
    ; CASE MESH
		Case "MESH"
            If zmesh > 0 
                SetFont bigfont
                st$ = "AnimB3D does not handle multiple MESH chunks"
                ln = StringWidth(st$)
                Text gw2-(ln/2),gh2, st$
                SetFont font
                Goto auscl
            EndIf
			MESHchunk$ = chunk$
			MESHchunksize = ReadInt( infile )
			MESHbrushID = ReadInt( infile )
            zmesh = zmesh + 1
;
    ; CASE VRTS
		Case "VRTS"
			VRTSchunk$ = chunk$
			VRTSchunksize = ReadInt( infile )
			fp = FilePos( infile )
			VRTSflags = ReadInt( infile )
			VRTStex_coord_sets = ReadInt( infile )
			VRTStex_coord_set_size = ReadInt( infile )
            i = 0
			Repeat
				vrts.vrts = New vrts
				VRTS\x# = ReadFloat( infile )
				VRTS\y# = ReadFloat( infile )
				VRTS\z# = ReadFloat( infile )
				If VRTSflags And 1
					VRTS\nx# = ReadFloat( infile )
					VRTS\ny# = ReadFloat( infile )
					VRTS\nz# = ReadFloat( infile )
				EndIf
				If VRTSflags And 2
					VRTS\red# = ReadFloat( infile )
					VRTS\green# = ReadFloat( infile )
					VRTS\blue# = ReadFloat( infile )
					VRTS\alpha# = ReadFloat( infile )
				EndIf
				For k = 0 To (VRTStex_coord_sets*VRTStex_coord_set_size)-1
					VRTS\tex_coords#[k] = ReadFloat( infile )
				Next
				If FilePos( infile ) > fp+VRTSchunksize 
					RuntimeError "ERROR VRTS chunk too long"
					Exit
				EndIf
				i = i+1
			Until FilePos( infile ) >= fp+VRTSchunksize
			AnzVert = i-1
			i = 0


;
    ; CASE TRIS            
		Case "TRIS"
			TRchunk$ = chunk$
			TRchunksize = ReadInt( infile )
			fp = FilePos( infile )
            tris.tris = New tris
			TRIS\brushid = ReadInt( infile )
            TRIS\vxbank = CreateBank(0)
			i = 0            
			Repeat               
				TRvertexID_1 = ReadInt( infile )
				TRvertexID_2 = ReadInt( infile )
				TRvertexID_3 = ReadInt( infile )
                blocknum = AddBlockInt( TRIS\vxbank, 12, TRvertexID_1, 0 )   
                InsertBlockInt( TRIS\vxbank, blocknum, 12,TRvertexID_2, 4 )    
				InsertBlockInt( TRIS\vxbank, blocknum, 12, TRvertexID_3, 8 )
				If FilePos( infile ) > fp+TRchunksize 
					RuntimeError "ERROR in TRIS chunk"
					Exit
				EndIf
				i = i+1
			Until FilePos( infile ) >= fp+TRchunksize
			TRIS\AnzTris = i
			i = 0
			trisi = trisi+1

;
    ; CASE ANIM
		Case "ANIM"
			ANIMchunk$ = chunk$
			ANIMchunksize = ReadInt( infile )
			ANIMflags = ReadInt( infile ) 
			ANIMframes = ReadInt( infile ) 
			ANIMfps# = ReadFloat( infile ) 
            hasanim = 1

;

    ; CASE NODE
		Case "NODE"

			If Countnode = 0 
				ROOTNODEchunk$ = chunk$
				ROOTNODEchunksize = ReadInt( infile )
				ROOTNODEchunkFP = ROOTNODEchunksize+FilePos( infile )
				ROOTNODEname$ = ReadNullString$(infile)
				ROOTNODEposX# = ReadFloat( infile )
				ROOTNODEposY# = ReadFloat( infile )
				ROOTNODEposZ# = ReadFloat( infile )
				ROOTNODEscaleX# = ReadFloat( infile )
				ROOTNODEscaleY# = ReadFloat( infile )
				ROOTNODEscaleZ# = ReadFloat( infile )
				ROOTNODErotW# = ReadFloat( infile )
				ROOTNODErotX# = ReadFloat( infile )
				ROOTNODErotY# = ReadFloat( infile )
				ROOTNODErotZ# = ReadFloat( infile )
			Else

				TNchunk$ = chunk$
				TNchunksize = ReadInt( infile )
				TNchunkFP = TNchunksize+FilePos( infile )
				TNname$ = ReadNullString$(infile)
                If Trim$(TNname$) = "" 
                    dummyname = dummyname + 1
                    If dummyname < 10 
                        TNname$ = "Bone  " + dummyname
                    ElseIf dummyname < 100 
                        TNname$ = "Bone " + dummyname
                    Else
                        TNname$ = "Bone" + dummyname
                    EndIf
                    
                EndIf
				TNposX# = ReadFloat( infile )
				TNposY# = ReadFloat( infile )
				TNposZ# = ReadFloat( infile )
				TNscaleX# = ReadFloat( infile )
				TNscaleY# = ReadFloat( infile )
				TNscaleZ# = ReadFloat( infile )
				TNrotW# = ReadFloat( infile )
				TNrotX# = ReadFloat( infile )
				TNrotY# = ReadFloat( infile )
				TNrotZ# = ReadFloat( infile )
			
				If nodi < 1 

					phandle = AddNode(0, TNname$)
					Node.Node = Object.Node(phandle)
                    FirstNodeHD = phandle
					node\nchunk$ = chunk$
					node\nchunksize = TNchunksize
					node\nchunkFP = TNchunkFP
					node\posX# = TNposX#
					node\posY# = TNposY#
					node\posZ# = TNposZ#
					node\scaleX# = TNscaleX#
					node\scaleY# = TNscaleY#
					node\scaleZ# = TNscaleZ#
					node\rotW# = TNrotW#
					node\rotX# = TNrotX#
					node\rotY# = TNrotY#
					node\rotZ# = TNrotZ#
					nodi = nodi + 1
				Else
					If node\NchunkFP >= FilePos( infile ) ;4

						phandle = AddNode(phandle, TNname$)						
						Node.Node = Object.Node(phandle)
						node\nchunk$ = chunk$
						node\nchunksize = TNchunksize
						node\nchunkFP = TNchunkFP
						node\posX# = TNposX#
						node\posY# = TNposY#
						node\posZ# = TNposZ#
						node\scaleX# = TNscaleX#
						node\scaleY# = TNscaleY#
						node\scaleZ# = TNscaleZ#
						node\rotW# = TNrotW#
						node\rotX# = TNrotX#
						node\rotY# = TNrotY#
						node\rotZ# = TNrotZ#

						nodi = nodi + 1
					ElseIf node\NchunkFP < FilePos( infile )
						Repeat
							If phandle > 0
								If node\NchunkFP >= FilePos( infile )	
									phandle = AddNode(phandle, TNname$)
									Node.Node = Object.Node(phandle)
									node\nchunk$ = chunk$
									node\nchunksize = TNchunksize
									node\nchunkFP = TNchunkFP
									node\posX# = TNposX#
									node\posY# = TNposY#
									node\posZ# = TNposZ#
									node\scaleX# = TNscaleX#
									node\scaleY# = TNscaleY#
									node\scaleZ# = TNscaleZ#
									node\rotW# = TNrotW#
									node\rotX# = TNrotX#
									node\rotY# = TNrotY#
									node\rotZ# = TNrotZ#

									nodi = nodi + 1
									Exit
								Else	
									node = Before node
									phandle = Handle(node)										
									If phandle > 0 
										Node.Node = Object.Node(phandle)
									Else
                                        ERRORnode1 = ERRORnode1 + 1
										;RUNTIMEERROR "Can not read B3D File1"
										;EXIT
									EndIf
								EndIf
							Else
                                ERRORnode2 = ERRORnode2 + 1
								;RUNTIMEERROR "Can not read B3D File2"
								;EXIT
							EndIf
						Forever	
					
					EndIf				
                EndIf 
				NodeKeyAnz = 0
			EndIf ;1
			Countnode = Countnode+1
;

    ; CASE BONE

		Case "BONE"
	
			;BONEchunk$ = chunk$
			BNEchunksize = ReadInt( infile )
			fp = FilePos( infile )
			tempbank = node\bonebank
			z = 0
			If BNEchunksize > 0
				Repeat
					tmpInt = ReadInt( infile )
					tmpfloat# = ReadFloat( infile )
					blocknum = AddBlockInt( tempbank, 8, tmpInt, 0 )
					InsertBlockFloat( tempbank,blocknum, 8, tmpfloat#, 4 )                    
					allbones = allbones+1
					z = z + 1
					If FilePos( infile ) > fp + BNEchunksize
                        ErrorBONE = ErrorBONE + 1
						;RUNTIMEERROR "ERROR BONE chunk too long"
						;EXIT
					EndIf
				Until FilePos( infile ) >=  fp + BNEchunksize
			EndIf
			node\VXanz = z
			boni = boni + 1	

;
    ; CASE KEYS
		Case "KEYS"
			NodeKeyAnz = NodeKeyAnz + 1
			;KEYSchunk$ = chunk$			
			KYSchunksize = ReadInt( infile )	
            If KYSchunksize > 0
    			fp = FilePos( infile )

    			bankK1 = node\key1bank
    			bankK2 = node\key2bank
    			bankK3 = node\key3bank

    			node\KEYSflags = ReadInt( infile )	
    			z = 0
                If KYSchunksize > 4
                    If nodi < 1 Then SaveFirstFrameNull = 1            
        			Repeat
        				KYSframe = ReadInt( infile )
                        If KYSframe > AnimFrames Then AnimFrames = KYSframe
        				If node\KEYSflags And 1
        					KYSposX# = ReadFloat( infile )
        					KYSposY# = ReadFloat( infile )
        					KYSposZ# = ReadFloat( infile )
        					blocknum = AddBlockInt( bankK1, 16, KYSframe, 0 )
        					InsertBlockFloat( bankK1, blocknum, 16,KYSposX#, 4 )    
        					InsertBlockFloat( bankK1, blocknum, 16, KYSposY#, 8 )
        					InsertBlockFloat( bankK1, blocknum, 16 ,KYSposZ#, 12 )

        				EndIf
        				If node\KEYSflags And 2
        					KYSscaleX# = ReadFloat( infile )
        					KYSscaleY# = ReadFloat( infile )
        					KYSscaleZ# = ReadFloat( infile )
        					blocknum = AddBlockInt( bankK2, 16, KYSframe, 0 )
        					InsertBlockFloat( bankK2, blocknum, 16, KYSscaleX#, 4 )    
        					InsertBlockFloat( bankK2, blocknum, 16, KYSscaleY#, 8 )
        					InsertBlockFloat( bankK2, blocknum, 16, KYSscaleZ#, 12 )

        				EndIf
        				If node\KEYSflags And 4
        					KYSrotW# = ReadFloat( infile )
        					KYSrotX# = ReadFloat( infile )
        					KYSrotY# = ReadFloat( infile )
        					KYSrotZ# = ReadFloat( infile )
        					blocknum = AddBlockInt( bankK3, 20, KYSframe, 0 )
        					InsertBlockFloat( bankK3, blocknum, 20, KYSrotW#, 4 )    
        					InsertBlockFloat( bankK3, blocknum, 20, KYSrotX#, 8 )
        					InsertBlockFloat( bankK3, blocknum, 20, KYSrotY#, 12 )
        					InsertBlockFloat( bankK3, blocknum, 20, KYSrotZ#, 16 )


        				EndIf
        				z = z + 1
        				allkeys = allkeys+1
        				If FilePos( infile ) > fp + KYSchunksize
                            ErrorKEYS = ErrorKEYS + 1
        					;RUNTIMEERROR  "ERROR KEYS chunk too long"                                                                                  ;Abfrage erstellen ------ <<
        					;EXIT
        				EndIf
        			Until FilePos( infile ) >=  fp + KYSchunksize
                EndIf
            EndIf
			keyi = keyi + 1	

;
    ; DEFAULT 
		Default
			csz = ReadInt( infile )	
			nfp = FilePos( infile )+csz
			SeekFile(infile, nfp)
;
    
	End Select
	; EndSelect
	If FilePos( infile ) > fsize 
        ErrorFILEEND = ErrorFILEEND + 1
		;RUNTIMEERROR  "ERROR file reads after fileend"
		;EXIT
	EndIf
	If outoffile = 1 Then Exit
.endchunk
Until FilePos( infile ) >= fsize
AnzTrisi = trisi-1
AnzNodi = nodi-1
AnzBoni = boni-1
AnzKeyi = keyi-1

CloseFile infile

;#End Region

;#Region not animated
If animframes = 0 Then animframes = 1

If hasanim = 0
    phandle = AddNode(0, "RootBone")
    Node.Node = Object.Node(phandle)
    node\nchunk$ = chunk$
    node\nchunksize = TNchunksize
    node\nchunkFP = TNchunkFP

    node\posX# = ROOTNODEposX#
    node\posY# = ROOTNODEposY#-1
    node\posZ# = ROOTNODEposZ#
    node\scaleX# = ROOTNODEscaleX#
    node\scaleY# = ROOTNODEscaleY#
    node\scaleZ# = ROOTNODEscaleZ#
    node\rotW# = ROOTNODErotW#
    node\rotX# = ROOTNODErotX#
    node\rotY# = ROOTNODErotY#
    node\rotZ# = ROOTNODErotZ#
   
    bankK1 = node\key1bank
    bankK2 = node\key2bank
    bankK3 = node\key3bank

    node\KEYSflags = 7	

    blocknum = AddBlockInt( bankK1, 16, 1, 0 )
    InsertBlockFloat( bankK1, blocknum, 16,0.0, 4 )    
    InsertBlockFloat( bankK1, blocknum, 16, 0.0, 8 )
    InsertBlockFloat( bankK1, blocknum, 16 ,0.0, 12 )

    blocknum = AddBlockInt( bankK2, 16, 1, 0 )
    InsertBlockFloat( bankK2, blocknum, 16, 1.0, 4 )    
    InsertBlockFloat( bankK2, blocknum, 16, 1.0, 8 )
    InsertBlockFloat( bankK2, blocknum, 16, 1.0, 12 )

    blocknum = AddBlockInt( bankK3, 20, 1, 0 )
    InsertBlockFloat( bankK3, blocknum, 20, 0.0, 4 )    
    InsertBlockFloat( bankK3, blocknum, 20, 0.0, 8 )
    InsertBlockFloat( bankK3, blocknum, 20, 0.0, 12 )
    InsertBlockFloat( bankK3, blocknum, 20, 0.0, 16 )

    ANIMchunk$ = chunk$
    ANIMchunksize = 0
    ANIMflags = 0
    ANIMframes = 1 
    ANIMfps# = 60
    AnzNodes = 1
    Dim FRposX#(ANIMframes ,AnzNodes)
    Dim FRposY#(ANIMframes ,AnzNodes)
    Dim FRposZ#(ANIMframes ,AnzNodes)
    Dim FRposDO(ANIMframes ,AnzNodes)
    
    Dim FRscaleX#(ANIMframes ,AnzNodes)
    Dim FRscaleY#(ANIMframes ,AnzNodes)
    Dim FRscaleZ#(ANIMframes ,AnzNodes)
    Dim FRscaleDO(ANIMframes ,AnzNodes)
    
    Dim FRrotW#(ANIMframes ,AnzNodes)
    Dim FRrotX#(ANIMframes ,AnzNodes)
    Dim FRrotY#(ANIMframes ,AnzNodes)
    Dim FRrotZ#(ANIMframes ,AnzNodes)
    Dim FRrotDO(ANIMframes ,AnzNodes)
    
    Dim FReuX#(ANIMframes ,AnzNodes)
    Dim FReuY#(ANIMframes ,AnzNodes)
    Dim FReuZ#(ANIMframes ,AnzNodes)
    Dim FReuDO(ANIMframes ,AnzNodes)
    
                FRposX#(1,0) = ROOTNODEposX#
                FRposY#(1,0) = ROOTNODEposY#-1
                FRposZ#(1,0) = ROOTNODEposZ#
                FRposDO(1,0) = 1

                FRscaleX#(1,0) = ROOTNODEscaleX#
                FRscaleY#(1,0) = ROOTNODEscaleY#
                FRscaleZ#(1,0) = ROOTNODEscaleZ#
                FRscaleDO(1,0) = 1

                FRrotW#(1,0) = ROOTNODErotW#
                FRrotX#(1,0) = ROOTNODErotX#
                FRrotY#(1,0) = ROOTNODErotY#
                FRrotZ#(1,0) = ROOTNODErotZ# 
                FRrotDO(1,0) = 1

    filename$ = "Temp.b3d" 
    saveQuestion = 0
    Gosub saveall
    FreeEntity theanim

    theanim = LoadAnimMesh(filename$)
    HideEntity theanim
    EntityFX theanim,0
EndIf
;#End Region

;#Region Create Vertex-boxes and Bone-Spheres
bn2 = CreateBank(100)
PutAllLCB2()
Dim FRkeySEQ$(ANIMframes)
;
;
; Create Vertex-boxes and Bone-Spheres

saveshort = 1
filename$ = "anim0.b3d"
Node.Node = First Node
saveQuestion = 0
Gosub saveNull
saveshort = 0

anim0 = LoadAnimMesh("anim0.b3d")
;EntityOrder anim0, 10
fx = 16
EntityFX anim0,fx

Dim Cubes(AnzVert+1)
sccubes# = 0.05
scsph# = 0.05
i = 0

For vrts.vrts = Each vrts	
	Cubes(i) = CreateCube()
	EntityPickMode Cubes(i), 2,0
	ScaleEntity Cubes(i), sccubes#, sccubes#, sccubes#
    PositionEntity Cubes(i), VRTS\x#, VRTS\y#, VRTS\z#
    EntityOrder Cubes(i),-10
    If  i = 0 
        vxtempstore# = VRTS\y#
    Else
        If VRTS\y# < vxtempstore# Then vxtempstore# = VRTS\y#
    EndIf
    
    i = i + 1
Next

i = 0
anzNodes = 0
Node.Node = First Node
minusNode = node\num
Animate anim0,3,1
UpdateWorld
RenderWorld
Flip
    
Node.Node = First Node
minusNode = node\num
For Node.Node = Each node
	thisHD = Node\num
	If node\parent  > 0 
		node.node = Object.node(node\parent)
		parentHD = node\sphere
		node.node = Object.node(thisHD)
        node\parentHD = parentHD
        node\bsphereparent = FindChild(anim0,node\name)
		Node\Sphere = CreateCube(node\bsphereparent)
        Node\spiv = CreateSphere(6,Node\Sphere)
        EntityParent Node\sphere, node\parentHD
        EntityAlpha  node\sphere,0
      	ScaleEntity Node\Sphere,node\scaleX#,node\scaleY#,node\scaleZ#,0
        EntityOrder Node\spiv,-2
        ;EntityAlpha node\spiv,0.6
        ;EntityBlend node\spiv,3
	ElseIf anznodes = 0
        node\bsphereparent = FindChild(anim0,node\name)
        node\parentHD = 0;node\bsphereparent
        Node\Sphere = CreateCube(node\bsphereparent)
        Node\spiv = CreateSphere(6,Node\Sphere)
        ;EntityParent Node\sphere, node\parentHD
        EntityAlpha  node\sphere,0
        ScaleEntity Node\Sphere,node\scaleX#,node\scaleY#,node\scaleZ#,1
        EntityParent Node\sphere,Node\bsphereparent
        EntityOrder Node\spiv,-2
        ;EntityAlpha node\spiv,0.6
        ;EntityBlend node\spiv,3
	EndIf
	EntityPickMode Node\spiv, 2,0
	PaintEntity Node\spiv, blau
    anzNodes = anzNodes + 1
Next

For node.node = Each node
	ScaleEntity Node\spiv, scsph#, scsph#, scsph# ,1    
Next

node.node = First node
aktiveBone = node\num
firstBone = aktiveBone

Gosub readspeedconfig

PositionEntity plane,0,vxtempstore#,0
  
;
;#End Region

; =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-

;#Region Edit Loop 
; Edit Loop  ------------
.MainLoop

aktualModus = 1
For I=0 To MnuCount(Mnu$(0))
    mnu$(i) = ""
Next
b2dw = 40
b2dh = 22

b2Xpos = gw-120
b2ypos = gh-140

EntityFX anim0,fx


Repeat

    
    If animmode = 1
        animmode = 0
        waittext = 0
        node.node = First node
        Goto AnimLoop
    End If
	msx = MouseX()
	msy = MouseY()

    entity = CameraPick(camera, msx, msy)       
    
;#Region Maustasten --- Mousebuttons

		; Linke Maustaste  --- left mousebutton
    If MouseHit(1) Or machfarbig = 1
        ;EntityOrder anim0,20
        If msx >= b2Xpos And msy >= b2ypos-24 And msx <= b2Xpos+82  And msy <= b2ypos+b2dh-24 
            If bonemodus = 1
                bonemodus = 2
                E_RM_mode = 2
                ShowEntity XYZ
                EntityParent xyz,0
                ScaleEntity xyz,scsph#*3, scsph#*3, scsph#*3
                PositionEntity xyz,0,0,0,1
                RotateEntity xyz,0,0,0,1
                EntityParent xyz, Node\Sphere,0
                ;bonemodus = 2
            ElseIf bonemodus = 2 
                bonemodus = 1
                E_RM_mode = 1
                ShowEntity XYZ
                EntityParent xyz,0
                ScaleEntity xyz,scsph#*3, scsph#*3, scsph#*3
                PositionEntity xyz,0,0,0,1
                RotateEntity xyz,0,0,0,1
                EntityParent xyz, node\Sphere,0
                ;HideEntity XYZ
                bonemodus = 1
            EndIf
        ElseIf msx >= b2Xpos And msy >= b2ypos-48 And msx <= b2Xpos+26  And msy <= b2ypos+b2dh-48
            moveBonespeed# = bonespeedS#
            aktbonespeed = 0
        ElseIf msx >= b2Xpos+28 And msy >= b2ypos-48 And msx <= b2Xpos+28+26  And msy <= b2ypos+b2dh-48
            moveBonespeed# = bonespeedM#
            aktbonespeed = 1
        ElseIf msx >= b2Xpos+56 And msy >= b2ypos-48 And msx <= b2Xpos+56+26  And msy <= b2ypos+b2dh-48
            moveBonespeed# = bonespeedF#
            aktbonespeed = 2
    	ElseIf entity<>0 Or machfarbig = 1 
            If machfarbig = 1
                machfarbig = 0
                entity = storeEntity
            EndIf
            thisID = node\num
        	For node.node = Each node
        		If Node\spiv > 0 Then PaintEntity Node\spiv, blau ;: EntityAlpha node\spiv,0.6 : EntityBlend node\spiv,3 ;paint all bones blue
        	Next
            ;node.node = Object.node(thisID)    
        	For node.node = Each node
                
                If entity = Node\spiv   ;if you selected a bone, then.....
                    thisID = node\num
                    For j = 0 To AnzVert
                        PaintEntity Cubes(j), weiss	;paint all cubes white
                    Next
                    PaintEntity Node\spiv, rot        ;paint the selected bone red
                    ;IF E_RM_mode = 2                            
                        EntityParent XYZ,Node\Sphere,0
                    ;ENDIF
                    aktbonename$ =  node\name         ;get Name of the active bone 
                    AktiveBone = Node\num            
                    If node\anzchild > 0
                        tempbank = node\childbank
                        For anc = 1 To node\anzchild
                            CHandle = PeekInt (tempbank, (anc-1)*4)
                            node.node = Object.node(CHandle)
                            If node\spiv > 0  
                                PaintEntity node\spiv,gruen
                                ;EntityAlpha node\spiv,0.6
                                ;EntityBlend node\spiv,3
                            EndIf
                            node.node = Object.node(thisID)
                        Next
                        node.node = Object.node(thisID)
                    EndIf
                    If node\parent > 0
                        node.node = Object.node(node\parent)
                        PaintEntity node\spiv,violett
                        ;EntityAlpha node\spiv,0.6
                        ;EntityBlend node\spiv,3
                    EndIf
                    node.node = Object.node(thisID)               
                    If node\VXanz > 0
                        tempbank = node\bonebank
                            For k = 0 To node\VXanz-1
                                tmpvx = GetBlockInt( tempbank, k, 8, 0 )
                                tmpwgt# = GetBlockFloat( tempbank, k, 8, 4 )
                                If tmpwgt# <0.101
                                    PaintEntity Cubes(tmpvx), wg10
                                ElseIf tmpwgt# < 0.201
                                    PaintEntity Cubes(tmpvx), wg20
                                ElseIf tmpwgt# < 0.301
                                    PaintEntity Cubes(tmpvx), wg30     
                                
                                ElseIf tmpwgt# < 0.401
                                    PaintEntity Cubes(tmpvx), wg40
                                ElseIf tmpwgt# < 0.501
                                    PaintEntity Cubes(tmpvx), wg50
                                ElseIf tmpwgt# < 0.601
                                    PaintEntity Cubes(tmpvx), wg60
                                ElseIf tmpwgt# < 0.701
                                    PaintEntity Cubes(tmpvx), wg70
                                ElseIf tmpwgt# < 0.801
                                    PaintEntity Cubes(tmpvx), wg80
                                ElseIf tmpwgt# < 0.901
                                    PaintEntity Cubes(tmpvx), wg90
                                Else
                                    PaintEntity Cubes(tmpvx), wg100
                                EndIf
                                            
                            Next
                    EndIf
                    Goto allegelb
                EndIf
        	Next
.allegelb
            node.node = Object.node(thisID) 
        EndIf
         
        If entity<>0     
            startentity = entity                                    ;Entity merken um gegen alle überschneidenden Cubes vergleichen zu können
            For mk = 0 To 39
                merke(mk) = 0
            Next
            merkZ = 0                                               ;merk Zähler für Cubes (Vertexes) welche am selben Platz sind oder sich berühren (muss hier vor der Schleife stehen)
            merkV = 0                                               ;von wo ab wurde schon getestet
        	If node\num <> firstBone
            
.nochmalvrts
                vrt = 0
        		For vrt = 0 To AnzVert                  ;Scan all Cubes --- Alle Cubes durchsuchen
        			If entity = Cubes(vrt)              ;If found selected vertex --- Wenn geklickter Vertex gefunden 
                ;Loop to search vertexes from different tris --- Suchschleife  um verschiedene Vertexe von verschiedenen TRIS zu finden
                        If differentVertexMode = 1            ;                 <<<<<<<<<<<<<<<<<<<<<<<<<<<   Menü einbinden und Toggle- Hotkey (K)
                                                                            ;    mit (L) Liste durchblättern differentVertexMode = 2
                            If zufallmodus = 1 Then Gosub positionVertexes
                            suchX# = EntityX#(entity)
                            suchY# = EntityY#(entity)
                            suchZ# = EntityZ#(entity)
                            
                            Dim VertexListe(100)           ; 100 ist natürlich viel zu viel, aber sicher ist sicher.
                            vertexzahl = 0                     ;Zähler für vrts.vrts Type
                            lz = 0
                            For vrts.vrts = Each vrts
                                If suchX# = vrts\x# And suchY# = vrts\y# And suchZ# = vrts\z#
                                    VertexListe(lz) = vertexzahl
                                    PaintEntity cubes(vertexzahl),hellblau
                                    lz = lz + 1
                                EndIf
                                vertexzahl = vertexzahl +1
                            Next
                            diffdone = lz
                            lz = lz - 1
                            VXliste = lz
                            If zufallmodus = 1 Then Gosub VertexRND
                        ElseIf differentVertexMode = 0  ; normaler Selektiermodus 
                            If weight# <0.101
                                PaintEntity entity, wg10
                            ElseIf weight# < 0.201
                                PaintEntity entity, wg20
                            ElseIf weight# < 0.301
                                PaintEntity entity, wg30     
                            
                            ElseIf weight# < 0.401
                                PaintEntity entity, wg40
                            ElseIf weight# < 0.501
                                PaintEntity entity, wg50
                            ElseIf weight# < 0.601
                                PaintEntity entity, wg60
                            ElseIf weight# < 0.701
                                PaintEntity entity, wg70
                            ElseIf weight# < 0.801
                                PaintEntity entity, wg80
                            ElseIf weight# < 0.901
                                PaintEntity entity, wg90
                            Else
                                PaintEntity entity, wg100
                            EndIf
                            ;PaintEntity entity, gelb                       ;<---------------------------------- ändern-------------------------------------<<<<<<<<<<<<<<<<<<<<<<<<
                            aktvx = vrt   ;aktueller Vertex
                            node.node = Object.node(AktiveBone)
                            If node\bonebank = 0 Then node\bonebank = CreateBank(0)   ;In bonebank sind Vertexnummer und Weight enthalten  (Int, Float)
                            vxIN = 0
                            For k = 0 To node\VXanz-1
                                If aktvx = GetBlockInt( node\bonebank, k, 8, 0 ) Then vxIN = 1 : vxBLnum = k ;prüfe ob Vertex schon selektiert ist
                            Next
                            If vxIN = 0                                                                         ;wenn Vertex noch nicht selektiert, dann selektiere jetzt
                                blocknum = AddBlockInt( node\bonebank, 8, aktvx, 0 )      ;aktuelle Vertexnummer dem Bone zufügen
                                InsertBlockFloat( node\bonebank,blocknum, 8, weight#, 4 )       ; Weight (hier 1.0)                                                                  <<<<<<<<<<<<<<<<< ändern
                                node\VXanz = node\VXanz+1                                          ;merke wieviel Vertexe pro Bone selektiert sind
                            Else
                                InsertBlockInt( node\bonebank, vxBLnum ,8, aktvx, 0 )  
                                InsertBlockFloat( node\bonebank, vxBLnum ,8, weight#, 4 )  
                            EndIf
                            node.node = Object.node(AktiveBone)
                            Goto paintfertig
                        EndIf
                    EndIf

        		Next
        	EndIf   
    	EndIf
       
    EndIf

    Goto nachpaintfertig
.paintfertig

    For vrt2 = merkV To AnzVert
 
        If startentity <> Cubes(vrt2) And MeshesIntersect (startentity, Cubes(vrt2)) And merkZ < 40  ;teste ob ein VertexCube einen anderen berührt oder am selben Platz ist.
            schongemerkt  = 1
            For mi = 0 To 39
                If  merke(mi) = Cubes(vrt2)   ;testen ob Cube Berührung schon gemerkt wurde. 
                    Exit                                    ;wenn ja dann Schleife verlassen
                ElseIf merke(mi) = 0
                    schongemerkt = 0              ;noch frei und nicht gefunden
                    Exit
                EndIf
            Next
            If schongemerkt = 0                                    ;wenn noch nicht gemerkt, dann merke jetzt (bis zu 40 Cubes)
                merke(merkZ) = Cubes(vrt2)
                entity = Cubes(vrt2)                                ;gefundene Cube als entity setzen und zum > färben und speichern schicken
                merkZ = merkZ+1                                  ;wieder eins mehr gemerkt
                merkV = vrt2+1                                      ;bis dahin schon gemerkt, als neue start-suchposition setzen
                Goto nochmalvrts
            EndIf
        EndIf
        
    Next
.nachpaintfertig
    PaintEntity Node\spiv, rot
    ;EntityAlpha node\spiv,0.6
    ;EntityBlend node\spiv,3
	;

		; Rechte Maustaste  --- right mousebutton 
		;clear selected vertex
	If entity<>0 And MouseHit(2) > 0 And MouseHit(1) = 0

        startentity = entity  
        For mk = 0 To 39
            merke(mk) = 0
        Next
        merkZ = 0                     ;merk Zähler für Cubes (Vertexes) welche am selben Platz sind oder sich berühren (muss hier vor der Schleife stehen)
        merkV = 0                     ;von wo ab wurde schon getestet
        If node\num <> firstBone
.nochmalWvrts
        	For i = 0 To AnzVert
        		If entity = Cubes(i)
        			PaintEntity entity, weiss
            		aktvx = i
            		node.node = Object.node(AktiveBone)
            		;tempbank = node\bonebank
            		vxIN = 0
            		For k = 0 To node\VXanz-1
            			If aktvx = GetBlockInt( node\bonebank, k, 8, 0 ) 
                            vxIN = 1
                            vxBLnum = k
            			EndIf
            		Next
            		If vxIN = 1
            			DeleteBlock( node\bonebank, 8, vxBLnum ) 
            			node\VXanz = node\VXanz-1
            		EndIf
            		Goto wpaintfertig
                EndIf
            Next
        EndIf
    EndIf
    node.node = Object.node(AktiveBone)
    Goto nachwpaintfertig
.wpaintfertig

   vrt2 = 0
    For vrt2 = merkV To AnzVert

        If startentity <> Cubes(vrt2) And MeshesIntersect (startentity, Cubes(vrt2)) And merkZ < 40  ;teste ob ein VertexCube einen anderen berührt oder am selben Platz ist.
            schongemerkt  = 1
            For mi = 0 To 39
                If  merke(mi) = Cubes(vrt2)   ;testen ob Cube Berührung schon gemerkt wurde. 
                    Exit                                    ;wenn ja dann Schleife verlassen
                ElseIf merke(mi) = 0
                    schongemerkt = 0              ;noch frei und nicht gefunden
                    Exit
                EndIf
            Next
            If schongemerkt = 0                                    ;wenn noch nicht gemerkt, dann merke jetzt (bis zu 40 Cubes)
                merke(merkZ) = Cubes(vrt2)
                entity = Cubes(vrt2)                                
                merkZ = merkZ+1                                  ;wieder eins mehr gemerkt
                merkV = vrt2+1
                Goto nochmalwvrts
            EndIf
        EndIf
        
    Next
    node.node = Object.node(AktiveBone)
.nachwpaintfertig
	;
	
     ;EntityOrder themesh,0
    ; Mittlere Maustaste   ---  middle mousebutton
	mzspeed#=MouseZSpeed() 
	If mzspeed# And ( KeyDown(KEY_SHIFT_LINKS  ) <> 0 ) Or (  KeyDown(KEY_SHIFT_RECHTS  ) <> 0) 
       MoveEntity Camera, 0, 0, (mzspeed#/15)
    ElseIf mzspeed# And ( KeyDown(KEY_STRG_LINKS  ) <> 0 ) Or (  KeyDown(KEY_STRG_RECHTS  ) <> 0) 
        MoveEntity Camera, 0, 0, (mzspeed#*2.9)
    Else
        MoveEntity Camera, 0, 0, (mzspeed#/1.5)
    EndIf
	If MouseDown(3)   Or ( MouseDown(1) And MouseDown(2))
		mxspeed#=MouseXSpeed() 
		myspeed#=MouseYSpeed() 
		If KeyDown(KEY_CTRL_RIGHT) Or KeyDown(KEY_CTRL_Left)
    		mxspeed = mxspeed-(mxspeed*2)
    		If delspeed = 1
    			MoveEntity Camera, mxspeed#/5.0, 0, 0
    			MoveEntity Camera, 0, myspeed#/5.0, 0
    		EndIf
        ElseIf KeyDown(KEY_SHIFT_LINKS  ) Or  KeyDown(KEY_SHIFT_RECHTS  ) 
            If delspeed = 1
        			mxspeed = mxspeed-(mxspeed*2)
        			
        			If msx > (gw2-(gw2/10)) And msx < (gw2+(gw2/10))
                        TurnEntity piv, myspeed#,0, 0 , 0
        			Else
                        If msx < gw2 Then myspeed = myspeed-(myspeed*2)
                        TurnEntity piv, 0, 0, myspeed#
                    EndIf
                    TurnEntity piv, 0, mxspeed#, 0 , 0
            EndIf
    		
        Else
            If delspeed = 1
        			mxspeed = mxspeed-(mxspeed*2)
                    TurnEntity piv, myspeed#,0, 0 , 0
                    TurnEntity piv, 0, mxspeed#, 0 , 1

            EndIf
        EndIf
		
		delspeed = 1
	Else
		delspeed = 0
	End If
	;   
   
;#End Region   
 
;#Region Tasten --- Keys

		;  Tasten

		If KeyDown(KEY_CTRL_RIGHT) Or KeyDown(KEY_CTRL_Left) And  KeyDown(KEY_ALT_RECHTS) = 0 ;--- CTRL/STRG +
            ; CTRL / STRG Keys
    		If KeyDown(Key_Links) 
                MoveEntity piv, -0.2, 0, 0
    		ElseIf KeyDown(Key_Rechts) 
                MoveEntity piv, 0.2, 0, 0
    		ElseIf KeyDown(Key_Auf) 
                MoveEntity piv, 0, 0.2, 0
    		ElseIf KeyDown(Key_Ab) 
                MoveEntity piv, 0, -0.2, 0
            ElseIf KeyDown(Key_BILD_Auf) 
                MoveEntity plane, 0.0, 0.01,0.0
            ElseIf KeyDown(Key_BILD_Ab) 
                MoveEntity plane, 0.0, -0.01,0.0
            ElseIf KeyDown(Key_0) Or KeyDown(KEY_NUM_0)
                PositionEntity piv, 0, 0, 0
            ElseIf KeyDown(Key_N) 
                ShowEntity darky
                st$ = "New name of the bone: "
                ln = StringWidth(st$)
                node\name = GetInput$(gw2-(ln/2),gh2, st$,50)  ;new bone name
            EndIf

            ;
		ElseIf KeyDown(KEY_ALT_RIGHT) Or KeyDown(KEY_ALT_Left) Or aktMenu = 201 Or aktMenu = 202 ;------- ALT +
            ; ALT + Keys
            ; DEL Bone
    		If KeyDown(KEY_D) Or aktMenu = 202; Delete Node/Bone
.dellastbone
    			If node\anzchild = 0 And  node\num <> minusNode
                    FreeEntity node\spiv
                    FreeEntity node\sphere
                    thisHD = DeleteLastNode( node\num )
                    node.node = Object.node(thisHD)
                    aktivebone = node\num
                EndIf
                machfarbig = 1
                storeentity = node\spiv
                DownWait(KEY_D)
    		;
            ; add Bone
    		ElseIf KeyDown(KEY_A) Or aktMenu = 201 ;Add new Bone

.addnewbone
                FlushKeys
    			Locate gw2-100, gh-50

                ShowEntity darky
                st$ = "Input a Name for the new Bone: "
                ln = StringWidth(st$)
                TNname$ = GetInput$(gw2-(ln/2),gh2, st$,50)
    			;TNname$ = Input$( "Input a Name for the new Bone: ")                
    			phandle = AddNode(node\num, TNname$)						
    			Node.Node = Object.Node(phandle)

    			thisHD = node\num
    			node.node = Object.node(node\parent)
    			parentHD = node\sphere
    			node.node = Object.node(thisHD)
                node\parentHD = parentHD
    			Node\Sphere = CreateCube(parentHD)
                Node\spiv = CreateSphere(6,node\sphere)
                EntityOrder Node\spiv,-2
                ;EntityAlpha node\spiv,0.6
                ;EntityBlend node\spiv,3
                MoveEntity node\sphere ,0,0.3,0
                EntityAlpha node\sphere,0
                RotateEntity Node\Sphere,0,0,0,1
    			ScaleEntity Node\spiv, scsph#, scsph#, scsph# ,1
                MemoryToBank(bnx,node\sphere,100)
                node\rotW#   = PeekFloat(bnx,12*4)
                node\rotX#    = PeekFloat(bnx,13*4)
                node\rotY#    = PeekFloat(bnx,14*4)
                node\rotZ#    = PeekFloat(bnx,15*4)
                node\posX#   = PeekFloat(bnx,16*4)
                node\posY#   = PeekFloat(bnx,17*4) 
                node\posZ#   = PeekFloat(bnx,18*4)        
                node\scaleX# = PeekFloat(bnx,19*4)
                node\scaleY#  = PeekFloat(bnx,20*4) 
                node\scaleZ#  = PeekFloat(bnx,21*4)      
    			EntityPickMode Node\spiv, 2,0
 
 AnzNodes = AnzNodes + 1
                storeentity = node\spiv
                machfarbig = 1
                DownWait(KEY_A)
            ElseIf KeyDown(KEY_O) 
                aktmenu = 0
                aktmenu2 = 0
                Gosub opennew
                Goto start
    		EndIf
            ;
            ;
		Else ;------------------------------------------------------------------------ pure Tasten 
            ; Keys
			If KeyDown(Key_INSERT) 
                MoveEntity Camera, 0, 0, 0.2
			ElseIf KeyDown(Key_DELETE) 
                MoveEntity camera, 0, 0, -0.2
            ElseIf KeyDown(Key_POS1) 
                MoveEntity Camera, 0, 0, 0.004
            ElseIf KeyDown(Key_ENDE) 
                MoveEntity camera, 0, 0, -0.004
			ElseIf KeyDown(Key_Links) 
                TurnEntity piv, 0.0, 1, 0.0
			ElseIf KeyDown(Key_Rechts) 
                TurnEntity piv, 0.0, -1, 0.0
			ElseIf KeyDown(Key_Auf) 
                TurnEntity piv, 1.0, 0, 0.0
			ElseIf KeyDown(Key_Ab) 
                TurnEntity piv, -1.0, 0, 0.0
			ElseIf KeyDown(Key_BILD_Auf) 
                TurnEntity piv, 0.0, 0, 1.0
			ElseIf KeyDown(Key_BILD_Ab) 
                TurnEntity piv, .0, 0, -1.0         
;            ELSEIF KeyDown(KEY_SPACE) or aktMenu = 403
;                BoObMode = 1-BoObMode
;                DownWait(Key_Space)

                
       			;  MoveBone
       		ElseIf KeyDown(Key_1) Or KeyDown(Key_NUM_1) Or xpressm = 1                  
                  If E_RM_mode = 1
                  
                    Gosub beforeMove
                    MoveEntity node\sphere, -moveBonespeed#, 0, 0 
                    Gosub afterMove
                
                ElseIf E_RM_mode = 2
                    TurnEntity node\sphere, -rotBonespeed#, 0, 0, 1
                    MemoryToBank(bnx,node\sphere,100)
                    node\rotW#   = PeekFloat(bnx,12*4)
                    node\rotX#    = PeekFloat(bnx,13*4)
                    node\rotY#    = PeekFloat(bnx,14*4)
                    node\rotZ#    = PeekFloat(bnx,15*4)
                EndIf
                
                
       		ElseIf KeyDown(Key_2) Or KeyDown(Key_NUM_3) Or xpressp = 1
            
                  EntityParent node\sphere,node\parentHD
                  If E_RM_mode = 1
                  
                    Gosub beforeMove
                    MoveEntity node\sphere, moveBonespeed#, 0, 0
                    Gosub afterMove
                  
                  
                ElseIf E_RM_mode = 2
                    TurnEntity node\sphere, rotBonespeed#, 0, 0, 0
                    MemoryToBank(bnx,node\sphere,100)
                    node\rotW#   = PeekFloat(bnx,12*4)
                    node\rotX#    = PeekFloat(bnx,13*4)
                    node\rotY#    = PeekFloat(bnx,14*4)
                    node\rotZ#    = PeekFloat(bnx,15*4)
                EndIf
                
                
                 
       		ElseIf KeyDown(Key_3) Or KeyDown(Key_NUM_4) Or ypressm = 1
                If E_RM_mode = 1
                    Gosub beforeMove
           			MoveEntity node\sphere, 0, -moveBonespeed#, 0
           			Gosub afterMove
                    
                ElseIf E_RM_mode = 2
                    TurnEntity node\sphere, 0,-rotBonespeed#,  0, 0
                    MemoryToBank(bnx,node\sphere,100)
                    node\rotW#   = PeekFloat(bnx,12*4)
                    node\rotX#    = PeekFloat(bnx,13*4)
                    node\rotY#    = PeekFloat(bnx,14*4)
                    node\rotZ#    = PeekFloat(bnx,15*4)
                EndIf
       		ElseIf KeyDown(Key_4) Or KeyDown(Key_NUM_6) Or ypressp = 1
                If E_RM_mode = 1
                    Gosub beforeMove
           			MoveEntity node\sphere, 0, moveBonespeed#, 0
           			Gosub afterMove
                    
                ElseIf E_RM_mode = 2
                    TurnEntity node\sphere,0, rotBonespeed#,  0, 0
                    MemoryToBank(bnx,node\sphere,100)
                    node\rotW#   = PeekFloat(bnx,12*4)
                    node\rotX#    = PeekFloat(bnx,13*4)
                    node\rotY#    = PeekFloat(bnx,14*4)
                    node\rotZ#    = PeekFloat(bnx,15*4)
                EndIf
       		ElseIf KeyDown(Key_5) Or KeyDown(Key_NUM_7) Or zpressm = 1
                If E_RM_mode = 1
                    Gosub beforeMove
           			MoveEntity node\sphere, 0,  0, -moveBonespeed#
           			Gosub afterMove
                    
                ElseIf E_RM_mode = 2
                    TurnEntity node\sphere, 0,0,-rotBonespeed#, 0
                    MemoryToBank(bnx,node\sphere,100)
                    node\rotW#   = PeekFloat(bnx,12*4)
                    node\rotX#    = PeekFloat(bnx,13*4)
                    node\rotY#    = PeekFloat(bnx,14*4)
                    node\rotZ#    = PeekFloat(bnx,15*4)
                EndIf
       		ElseIf KeyDown(Key_6) Or KeyDown(Key_NUM_9) Or zpressp = 1
                If E_RM_mode = 1
                    Gosub beforeMove
           			MoveEntity node\sphere, 0,  0, moveBonespeed#
           			Gosub afterMove
                    
                ElseIf E_RM_mode = 2
                    TurnEntity node\sphere,0,0, rotBonespeed#, 0
                    MemoryToBank(bnx,node\sphere,100)
                    node\rotW#   = PeekFloat(bnx,12*4)
                    node\rotX#    = PeekFloat(bnx,13*4)
                    node\rotY#    = PeekFloat(bnx,14*4)
                    node\rotZ#    = PeekFloat(bnx,15*4)
                EndIf
       			;
              
            ElseIf KeyDown(KEY_M)
                E_RM_mode = 1
                ;HideEntity XYZ
                bonemodus = 1
                ShowEntity XYZ
                    EntityParent xyz,0
                    ScaleEntity xyz,scsph#*3, scsph#*3, scsph#*3
                    RotateEntity xyz,0,0,0,1
                    EntityParent xyz, Node\Sphere,0
            ElseIf KeyDown(KEY_R)
                If E_RM_mode < 2
                    E_RM_mode = 2
                    ShowEntity XYZ
                    EntityParent xyz,0
                    ScaleEntity xyz,scsph#*3, scsph#*3, scsph#*3
                    PositionEntity xyz,0,0,0,1
                    RotateEntity xyz,0,0,0,1
                    EntityParent xyz, Node\Sphere,0
                    bonemodus = 2
                    DownWait(Key_R)
                EndIf
            ElseIf KeyDown(1) Or aktmenu = 103
                checkend = 1
                DownWait(1)
            ElseIf KeyDown(Key_TAB) Or aktmenu = 401
                i = 0
                For vrts.vrts = Each vrts	
                HideEntity Cubes(i)	
                i = i+1
                Next
                For node.node = Each node
                    HideEntity Node\Sphere   
                    HideEntity Node\Spiv
                Next
                waittext = 1
                animmode = 1
                ShowEntity darky
                aktmenu = 0
                xpressm = 0
                xpressp = 0
                ypressm = 0
                ypressp = 0
                zpressm = 0
                zpressp = 0
            ElseIf KeyDown(KEY_K) ;-------------------------------------------------------------- Menüeintrag erstellen
                If differentVertexMode = 0 
                    differentVertexMode = 1
                    newlz = 0
                    SETVX = 0
                Else
                    differentVertexMode = 0
                    For j = 0 To AnzVert
                        ScaleEntity Cubes(j), sccubes#, sccubes#, sccubes#
                    Next
                EndIf
                DownWait(Key_K)
            ElseIf KeyDown(KEY_L) ;-------------------------------------------------------------- Menüeintrag erstellen
                If differentVertexMode = 1 Then differentVertexMode = 2
                If differentVertexMode = 2 And diffdone > 0
                    For j = 0 To AnzVert
                        PaintEntity Cubes(j), weiss	
                        ScaleEntity Cubes(j), sccubes#, sccubes#, sccubes#
                    Next
                    tz = 0
                    For tris.tris = Each tris
                        For BlockTri = 0 To tris\anztris-1
                            TRvertexID_1 = GetBlockInt( tris\vxbank, BlockTri, 12, 0 )
                            TRvertexID_2 = GetBlockInt( tris\vxbank, BlockTri, 12, 4 )
                            TRvertexID_3 = GetBlockInt( tris\vxbank, BlockTri, 12, 8 )
                            If TRvertexID_1 = VertexListe(newlz) Or TRvertexID_2 = VertexListe(newlz) Or TRvertexID_3 = VertexListe(newlz)
                                PaintEntity cubes(TRvertexID_1),gruen
                                PaintEntity cubes(TRvertexID_2),gruen
                                PaintEntity cubes(TRvertexID_3),gruen
                                ScaleEntity Cubes(TRvertexID_1), sccubes#*1.5, sccubes#*1.5, sccubes#*1.5
                                ScaleEntity Cubes(TRvertexID_2), sccubes#*1.5, sccubes#*1.5, sccubes#*1.5
                                ScaleEntity Cubes(TRvertexID_3), sccubes#*1.5, sccubes#*1.5, sccubes#*1.5
                                PaintEntity cubes(VertexListe(newlz)),hellblau
                            EndIf
                        Next
                        tz = tz + 1
                    Next
                    SETVX = newlz
                    newlz = newlz + 1
                    If newlz > VXListe Then  newlz = 0
                EndIf
                DownWait(Key_L)
            ElseIf KeyDown(KEY_ENTER)   ;Set DIFF-Vertex
                If differentVertexMode = 2
                    If node\bonebank = 0 Then node\bonebank = CreateBank(0)   ;In bonebank sind Vertexnummer und Weight enthalten  (Int, Float)
                    vxIN = 0
                    For k = 0 To node\VXanz-1
                        If VertexListe(SETVX)  = GetBlockInt( node\bonebank, k, 8, 0 ) Then vxIN = 1  ;prüfe ob Vertex schon selektiert ist
                    Next
                    If vxIN = 0                                                                         ;if vertex not selected --- wenn Vertex noch nicht selektiert
                        blocknum = AddBlockInt( node\bonebank, 8, VertexListe(SETVX), 0 )      ;give Bone the aktual Vertex number ---   aktuelle Vertexnummer dem Bone zufügen
                        InsertBlockFloat( node\bonebank,blocknum, 8, weight#, 4 )       ; Weight                                                                <<<<<<<<<<<<<<<<< ändern
                        node\VXanz = node\VXanz+1                                       ;notice how much vertexes are selected --- merke wieviel Vertexe pro Bone selektiert sind
                    EndIf
                EndIf
                DownWait(Key_ENTER)
            ElseIf KeyDown(KEY_V)                                                ;Input Vertex Weight
                st$ = "New weight for selected vertices: (default 1.0) "
                ln = StringWidth(st$)
                ss$ = Trim$(GetInput$(gw2-(ln/2),gh2, st$))
                If ss$ = "" Then ss$ = "1.0"
                weight# = Float#(ss$)
            ElseIf KeyDown(KEY_F1) Or aktmenu = 601    ;Help --- Hilfe
                help = 1-help
                DownWait(Key_F1)
                MouseUpWait(1)
            ElseIf KeyDown(KEY_W) Or aktmenu = 304  ;Wiredframe
                wired = 1-wired
                WireFrame wired              
                DownWait(KEY_W)
    		            
            ElseIf KeyDown(KEY_F) Or aktmenu = 305  ;EntityFX
                If fx = 0 
                    fx = 16
                Else 
                    fx = 0
                EndIf
                EntityFX anim0, fx           
                DownWait(KEY_F)
            ElseIf KeyDown(KEY_C) Or aktmenu = 302   ; Center
                    thisHD = node\num
                    spxr# = 0
                    spyr# = 0
                    spzr# = 0
                    spcount = 0
                    For node.node = Each node
                        spxr# = spxr# + EntityX#(node\sphere,1)     
                        spyr# = spyr# + EntityY#(node\sphere,1)     
                        spzr# = spzr# + EntityZ#(node\sphere,1)  
                        spcount = spcount + 1
                    Next
                    spxr# = spxr#  / spcount    
                    spyr# = spyr#  / spcount   
                    spzr# = spzr#  / spcount 
                    node.node = Object.node(thisHD)
                    PositionEntity piv,spxr#,spyr#,spzr#
                    DownWait(KEY_C)
            ElseIf KeyDown(Key_SPACE)  Or KeyDown(KEY_J) Or aktmenu = 301
                spx# = EntityX#(node\sphere,1)     
                spy# = EntityY#(node\sphere,1)     
                spz# = EntityZ#(node\sphere,1)     
                PositionEntity piv,spx#,spy#,spz#
                DownWait(KEY_SPACE)
            ElseIf KeyDown(Key_F6)
                moveBonespeed# = bonespeedS#
            ElseIf KeyDown(Key_F7)
                moveBonespeed# = bonespeedM#
            ElseIf KeyDown(Key_F8)
                moveBonespeed# = bonespeedF#
            ElseIf  aktmenu = 204   ;New BoneName
                ShowEntity darky
                st$ = "New name of the bone: "
                ln = StringWidth(st$)
                node\name = GetInput$(gw2-(ln/2),gh2, st$,50)
                    
    ;  Scale Bones and Vertices
    		ElseIf KeyDown(KEY_F9)
                If sccubes# < 0.0005 Then sccubes# = 0.0005
                If scrnd# < 0.00005 Then scrnd# = 0.00005
                scrnd# = scrnd# * 0.98
    			sccubes# = sccubes# *0.98
    			For i = 0 To AnzVert
    			ScaleEntity Cubes(i), sccubes#, sccubes#, sccubes#
    			Next
    		ElseIf KeyDown(KEY_F10)
                If sccubes# < 0.0005 Then sccubes# = 0.0005
                If scrnd# < 0.00005 Then scrnd# = 0.00005
                scrnd# = scrnd# * 1.02
    			sccubes# = sccubes# *1.02
    			For i = 0 To AnzVert
    			ScaleEntity Cubes(i), sccubes#, sccubes#, sccubes#
    			Next
    		
    		ElseIf KeyDown(KEY_F11)
                If scsph# < 0.0005 Then scsph# = 0.0005
    			scsph# = scsph# *0.98
    			For node.node = Each node
    			ScaleEntity Node\spiv, scsph#, scsph#, scsph#     
                ScaleEntity xyz,scsph#*3, scsph#*3, scsph#*3
    			Next
    			node.node = Object.node(aktiveBone)
    		ElseIf KeyDown(KEY_F12)
                If scsph# < 0.0005 Then scsph# = 0.0005
    			scsph# = scsph# *1.02
    			For node.node = Each node
    			ScaleEntity Node\spiv, scsph#, scsph#, scsph#    
                ScaleEntity xyz,scsph#*3, scsph#*3, scsph#*3
    			Next
    			node.node = Object.node(aktiveBone)
    		End If
        EndIf
		; 
;#End Region    

;#Region nur Menü --- only Menu
        ;
        ;
        ; Menu
        If aktmenu = 101
            aktmenu = 0
            aktmenu2 = 0

            Gosub opennew

            Goto start
        ElseIf aktmenu = 206
            ShowEntity darky
            st$ = "Give in the weight for the vertexes you set (actual = " +weight# +") "
            ln = StringWidth(st$)
            weight# = Float(Trim$(GetInput$(gw2-(ln/2),gh2, st$)))
        ElseIf  aktmenu = 501
            ShowEntity darky
            st$ = "Slow speed of bone movement: (actual = " +bonespeedS# +") "
            ln = StringWidth(st$)
            tmp# = Float(Trim$(GetInput$(gw2-(ln/2),gh2, st$)))
            If tmp# = 0.0 Then bonespeedS# = 0.005 Else bonespeedS# = tmp# 
            Gosub writespeedconfig    
        ElseIf  aktmenu = 502
            ShowEntity darky
            st$ = "Middle speed of bone movement: (actual = " +bonespeedM# +") "
            ln = StringWidth(st$)
            tmp# = Float(Trim$(GetInput$(gw2-(ln/2),gh2, st$)))
            If tmp# = 0.0 Then bonespeedM# = 0.02 Else bonespeedM# = tmp# 
            Gosub writespeedconfig    
        ElseIf  aktmenu = 503
            ShowEntity darky
            st$ = "Fast speed of bone movement: (actual = " +bonespeedF# +") "
            ln = StringWidth(st$)
            tmp# = Float(Trim$(GetInput$(gw2-(ln/2),gh2, st$)))
            If tmp# = 0.0 Then bonespeedF# = 0.02 Else bonespeedF# = tmp# 
            Gosub writespeedconfig    
        ElseIf  aktmenu = 505   ;  VertexRND
            zufallmodus = 1
            Gosub VertexRND
        ElseIf  aktmenu = 506    ; Vertex Position
            zufallmodus = 0
            Gosub positionVertexes
        EndIf
        ; 
;#End Region
        UpdateWorld
		RenderWorld
;#Region Text
		;  Texte
        SetFont font
        Color 180,180,180
	If help = 1
        ShowEntity darky
		Text 10,30, "Cursor keys and Pup / Pdown - rotate around the Mesh"
		Text 10,50, "CTRL + cursor keys to move camera"
		Text 10,70, "Middle mousebutton or left and right mousebutton - press down and move mouse - move camera around the mesh"
		Text 10,90, "Mousewheel or INS+DEL - Zoom  |  [+ SHIFT] = slow or [+ CTRL] = fast"
		Text 10,110, "Left mousebutton - select vertices and toggle between the bones "
		Text 10,130, "Right mousebutton - deselect vertices"
		Text 10,150, "F9 and F10 - scale vertice-cubes"
		Text 10,170, "F11 and F12 - scale bone-spheres"		
		Text 10,190, "TAB - Animations-mode"
		Text 10,210, "ALT + D - Delete actual Bone"
		Text 10,230, "ALT + A - Add new Bone"
        Color 160,255,160       
        Text 10,290, "V - weight,  then give in the active weight for vertexes"
        Color 160,255,255
        Text 10,310, "Move Bones X-axis with 1 and 2 or NUM_1 and NUM_3, Y-axis 3/4 or NUM 4/6, Z-axis  5/6 or NUM 7/9 or with the GUI"
        Text 10,330, "M - Move-Modus,   R - Rotate-Modus"
        Color 180,180,180
        Text 10,350, "Space or J - Position View on selected Bone / C - Center View"
        Text 10,370, "W - toggle wiredframe / F - toggle between FX 1 and FX 17"
        Text 10,390, "CTRL+POSup AND CTRL+POSdn
