; ID: 3284
; Author: Blitzplotter
; Date: 2016-08-18 07:39:35
; Title: Dropping 3 bombs
; Description: Drops 3 bombs onto a heightmapped terrain

Global screen_width = 800, screen_height = 600

Graphics3D screen_width, screen_height, 32,1
SetBuffer BackBuffer() 
	
light = CreateLight()
RotateEntity light, 90, 0, 0

Global terrainscale=4
Global height#=0.0

createdTerrain = LoadTerrain( "terrain_hmap.jpg" )

tex_A = LoadTexture( "A.png" )

ScaleTexture tex_A, 1, 1

EntityTexture createdTerrain, tex_A



ScaleEntity createdTerrain, terrainscale, 25.5, terrainscale
TerrainDetail createdTerrain,2000*terrainscale,True : TerrainShading createdTerrain,True


ModifyTerrain createdTerrain, 8, 8, 1, 1 ; put in test spike
ModifyTerrain createdTerrain, 13, 8, 1, 1 ; put in test spike
ModifyTerrain createdTerrain, 18, 8, 1, 1 ; put in test spike
ModifyTerrain createdTerrain, 24, 8, 1, 1 ; put in test spike
ModifyTerrain createdTerrain, 33, 8, 1, 1 ; put in test spike

ModifyTerrain createdTerrain, 5, 10, 1, 1; put in test spike
ModifyTerrain createdTerrain, 5, 20, 1, 1; put in test spike



;Bravo's terrain------------------------------

landTerrain = CreateTerrain(4)

ScaleEntity landTerrain,3,25.5,3

MoveEntity landTerrain,190,0,-30

tex_B = LoadTexture( "B.png" )

ScaleTexture tex_B, 5, 5

EntityTexture landTerrain, tex_B


;Chariles terrain-----------------------------

ClandTerrain = CreateTerrain(4)


ScaleEntity ClandTerrain,3,25.5,3

MoveEntity ClandTerrain,390,0,-30

tex_C = LoadTexture( "C.png" )

ScaleTexture tex_C, 5, 5

EntityTexture ClandTerrain, tex_C



;---------------------------------------
;insertions;
;---------------------------------------

lit=CreateLight()
cam=CreateCamera()

CameraRange cam,.1,1000
PositionEntity cam,32,20,25
TurnEntity cam,0,90,0





Const BEAKS=9
Const PEAKS=3

riseup=0

Global raiseBy#=0.2

bomberx=20
bombery=150
bomberz=50
bombSpeed#=0.2


;Bbomberx=204
;Bbombery=100
;Bbomberz=6
;BbombSpeed#=2.2

Bbomberx=25
Bbombery=200
Bbomberz=100
BbombSpeed#=0.4


;Cbomberx=404
;Cbombery=150
;Cbomberz=9
;CbombSpeed#=2.2



Cbomberx=16
Cbombery=220
Cbomberz=350
CbombSpeed#=0.8

Global bomb_a_falling=0
Global bomb_b_falling=0
Global bomb_c_falling=0

Global bomb_a_exploding=0
Global bomb_b_exploding=0
Global bomb_c_exploding=0

xDivide=5.2
zDivide=4.2

Bsize=6


;make a bomb carrier

Blimp = CreateSphere(25)
PositionEntity Blimp,bomberx,bombery,bomberz
ScaleEntity Blimp,Bsize,Bsize,Bsize

;make a  A bomb

bomb = CreateSphere(25)
PositionEntity bomb,bomberx,bombery-5,bomberz
ScaleEntity bomb,0.9,1.5,0.9

;make a Bravo bomb carrier

BBlimp = CreateSphere(25)
PositionEntity BBlimp,Bbomberx,Bbombery,Bbomberz
ScaleEntity BBlimp,Bsize,Bsize,Bsize

;make a B bomb

Bbomb = CreateSphere(25)
PositionEntity Bbomb,Bbomberx,Bbombery-5,Bbomberz
ScaleEntity Bbomb,0.9,1.5,0.9

;make a C bomb carrier

CBlimp = CreateSphere(25)
PositionEntity CBlimp,Cbomberx,Cbombery,Cbomberz
ScaleEntity CBlimp,Bsize,Bsize,Bsize

;make a C bomb

Cbomb = CreateSphere(25)
PositionEntity Cbomb,Cbomberx,Cbombery-5,Cbomberz
ScaleEntity Cbomb,0.9,1.5,0.9

;bomb vars

bombs=0
Global bombstext=0


;Assign collision numbers to items

EntityType bomb, 1
EntityType createdTerrain, 3    ; A
EntityType Blimp, 4

;-------------------------------------

EntityType Bbomb,7
EntityType BBlimp,8
EntityType landterrain, 2       ; B

;--------------------------------------

EntityType Cbomb,5
EntityType CBlimp,6
EntityType CLandTerrain,9       ; C


;------------------------------------------------------------
;
; Collisions 1 sph to sph, 2 sph to poly, 3 sph to box
; response 1:stop,  2:full sliding collision,   
; 3:slide 2 - prevent entities from sliding down slopes?
;
;----------------------------------------------------------

Collisions 1,2,2,1

Collisions 1,3,2,1     ;makes bomb A land on the terrain



;---------------------------
; bBomb collisions
;---------------------------

Collisions 7,2,2,1

Collisions 7,3,2,1  ;lets B Bomb collide with A Terrain


;------------------------
; cBomb collisions
;------------------------

Collisions 5,9,2,1

;------------------------

MoveEntity Cam,0,90,-340


;MoveEntity createdTerrain,20,15,10



While Not KeyDown(1)
	
	If KeyDown(203) MoveEntity cam,-1,0,0
		If KeyDown(205) MoveEntity cam,1,0,0
			If KeyDown(200) MoveEntity cam,0,0,1
				If KeyDown(208) MoveEntity cam,0,0,-1
					
					
					If KeyDown(16) ;q
						
						TurnEntity cam,1,0,0
						
						Flip
						
					EndIf
					
					If KeyDown(30) ;a
						
						TurnEntity cam,-1,0,0
						
					EndIf
					
					
					If KeyDown(44) 
						
						TurnEntity cam,0,1,0
						
					EndIf
					
					
					If KeyDown(45) 
						
						TurnEntity cam,0,-1,0
						
					EndIf
					
					If KeyDown(48)   ;  B
						
						bombs=1
						
					EndIf
					
					If KeyDown(19)   ;  R  -  Rise the terrain up
						
						riseup=1
						
					Else
						
						riseup = 0
						
					EndIf
					
					
					If riseup = 1
						
						pickgrid=TerrainSize(createdTerrain)
						
						raiseTerrain(createdTerrain,pickgrid)
						
					EndIf
					
					
					
					If bombs = 1
						
						dropbombs()
						
					Else
						
						stopbombs()
						
					EndIf
					
					UpdateWorld
					RenderWorld
					
					If bombstext=1
						Text 20,20,"Dropping A, B and C bombs...."
						
						;---------- bomb A --------------------------
						
						If bomb_a_falling=1
							
							
							MoveEntity bomb,0,-bombSpeed,0
							
							bomx#=EntityX(bomb)
							bomy#=EntityY(bomb)
							bomz#=EntityZ(bomb)
							
							Text 40,100,"bomy#: "+bomy#
							
							Text 40,120,"TerrainY#:  "+TerrainY#(createdTerrain,bomx#,bomy#,bomz#)
							
							;TerrainY is 15.0
							;15.0/25.5 = 0.588
							;lets say I want to dig a 4 unit hole, this'll equate to 4x0.588
							;0.5/25.5 = 0.019607
							; 0.588 - 0.08 = 0.50 (new heightnafter hole dig)
							
							If bomy#-3<TerrainY#(createdTerrain,bomx#,bomy#,bomz#)
								
								bomb_a_exploding=1
								
								TFormPoint (x, y, z,  createdTerrain, bomb  )
							
							Else
								
								Text 40,160,"Bomb a still intact with bomy#:"+bomy#+" not less than TerrainY#: "+TerrainY#(createdTerrain,bomx#,bomy#,bomz#)
								
								
								
							EndIf
							
							
						EndIf
						
						If bomb_a_exploding=1
							
							x = EntityX(bomb)
							y = EntityY(bomb)
							z = EntityZ(bomb)
							
							ModifyTerrain createdTerrain, x/xdivide, (z/zdivide), 0.02, 1; 
							ModifyTerrain createdTerrain, (x/xdivide)+1, (z/zdivide)+1, 0.02, 1; 
							ModifyTerrain createdTerrain, (x/xdivide)+1, (z/zdivide)+1, 0.02, 1; 
							ModifyTerrain createdTerrain, x/xdivide, (z/zdivide)+1, 0.02, 1; 
							
							;if bomb is at terrain level explode and hide
							;Text 40,40,"Exploding Bomb A"
							
						EndIf
						
						
						
						;---------- bomb B --------------------------
						
						If bomb_b_falling=1
							
						
							MoveEntity Bbomb,0,-BbombSpeed,0    
							
							bombx#=EntityX(Bbomb)
							bomby#=EntityY(Bbomb)
							bombz#=EntityZ(Bbomb)
							
							If bomby#-3<TerrainY#(createdTerrain,bombx#,bomby#,bombz#); Or ; Failing to crater, commented
								
								bomb_b_exploding=1
								
								Text 40,180,"Bomb B Booming............"
								
							Else
								
								Text 40,180,"Bomb B still intact with bomby#:"+bomby#+" not less than TerrainY#: "+TerrainY#(createdTerrain,bombx#,bomby#,bombz#)
								
								
								
							EndIf
							
						EndIf
						
						;If 0
						
						If bomb_b_exploding=1
							
							
							Text 40,60,"Attempting to Crater where bomb B was"
							
							bx = EntityX(Bbomb)
							by = EntityY(Bbomb)
							bz = EntityZ(Bbomb)
							
							ModifyTerrain createdTerrain, bx/xdivide, (bz/zdivide), 0.02, 1; 
							ModifyTerrain createdTerrain, (bx/xdivide)+1, (bz/zdivide)+1, 0.02, 1; 
							ModifyTerrain createdTerrain, (bx/xdivide)+1, (bz/zdivide)+1, 0.02, 1; 
							ModifyTerrain createdTerrain, bx/xdivide, (bz/zdivide)+1, 0.02, 1; 
							
							
							;Text 40,40,"Exploding Bomb B"
							
						EndIf
						
					;EndIf
						
						
						;---------- bomb C --------------------------
						
						If bomb_c_falling=1
							
							
							MoveEntity Cbomb,0,-CbombSpeed,0    
							
							bomcx#=EntityX(Cbomb)
							bomcy#=EntityY(Cbomb)
							bomcz#=EntityZ(Cbomb)
							
							If bomcy#-3<TerrainY#(createdTerrain,bomcx#,bomcy#,bomcz#); Or ; Failing to crater, commented
								
								bomb_c_exploding=1
								
								;Text 40,220,"bomc c Booming............"
								
							Else
								
								;Text 40,220,"bomc c still intact with bomcy#:"+bomcy#+" not less than TerrainY#: "+TerrainY#(createdTerrain,bomcx#,bomcy#,bomcz#)
								
								
								
							EndIf
							
						EndIf
						
						;If 0
						
						If bomb_c_exploding=1
							
							
							Text 40,60,"Attempting to Crater where bomc B was"
							
							bx = EntityX(Cbomb)
							by = EntityY(Cbomb)
							bz = EntityZ(Cbomb)
							
							ModifyTerrain createdTerrain, bx/xdivide, (bz/zdivide), 0.02, 1; 
							ModifyTerrain createdTerrain, (bx/xdivide)+1, (bz/zdivide)+1, 0.02, 1; 
							ModifyTerrain createdTerrain, (bx/xdivide)+1, (bz/zdivide)+1, 0.02, 1; 
							ModifyTerrain createdTerrain, bx/xdivide, (bz/zdivide)+1, 0.02, 1; 
							
							
							;Text 40,40,"Exploding bomb C"
							
						EndIf
						
					Else
						Text 20,20,"Press B to drop a bomb., Press R to raise the terrain - set peaks required by PEAKS variable."
						Text 20,1,"                              Cursors, Q, A, Z, W manipulate camera"
					EndIf
					
					Text 300,40,"Raising Terrain by: "+raiseBy+" thru a grid of : "+pickgrid
					
					bombstext=0
					
					Flip
					
				Wend
				
				
				FreeEntity createdTerrain
				FreeEntity landterrain
				FreeEntity lit
				FreeEntity cam
				EndGraphics
				End
				
				
				
Function dropbombs()
	
	bombstext=1
	
	bomb_a_falling=1
	bomb_b_falling=1
	bomb_c_falling=1
	
	
End Function

Function stopbombs()
	
	bombstext=0
	
End Function


Function raiseTerrain(createdTerrain,pickgrid)
	
	raiseBy=raiseBy+0.02
	
	If raiseBy=1 Then raiseBy=0.01
	
	For x=1 To pickgrid Step PEAKS
		
		For z=1 To pickgrid Step BEAKS
	
			ModifyTerrain(createdTerrain,x,z,raiseBy,True)
	
		Next
		
	Next
	
End Function
