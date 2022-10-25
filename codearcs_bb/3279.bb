; ID: 3279
; Author: Yue
; Date: 2016-06-26 15:13:59
; Title: Impact of bombs on the ground.
; Description: Impact craters .

: by Yue 2016

Graphics3D 800, 600, 32, 2
SetBuffer ( BackBuffer())

Local camara% 	= CreateCamera()
Local luz%		= CreateLight()
Local terreno% 	= LoadTerrain("Terreno.png")
Local bomba%    = LoadMesh("Bomba.b3d")

Local texturaT% = LoadTexture("Tierra.jpg")
ScaleTexture (texturaT%, 4,4 )
EntityTexture ( terreno%, texturaT%)
PositionEntity terreno%, -128, 0, -128
ScaleEntity terreno%, 1, 40, 1
ScaleEntity bomba%, .2, .2, .2

PositionEntity camara%, 0, 25,10
PositionEntity bomba%, 0, 60, 30


EntityType bomba%, 1
EntityType terreno, 2

Collisions 1, 2,2, 2 

TerrainShading terreno,True
TerrainDetail terreno,2000

ModifyTerrain(terreno%,128, 128, 500,True)


WireFrame False

While Not KeyHit(1)
	
	For  c% = 1 To CountCollisions(terreno%)
		
		colisionBomba% = GetEntityType(bomba%)
		
		
		If c% = colisionbomba% Then 
			
			
			x# = EntityX(bomba%)
			y# = EntityY(bomba%)
			z# = EntityZ(bomba%)
			TFormPoint (x#, y#, z#,  0, terreno%  )
			
			
			
			
			h# = TerrainHeight(terreno,TFormedX(),TFormedY())
			
			
			
			If h > 0 Then
				h = h -.1
				If h < .5  Then  h = .5
				
				
				
				ModifyTerrain ( terreno%, TFormedX()+ Rnd(-2,2), TFormedZ()+Rnd(-2,2),h#,True)	
				
				
			End If 	
			
			
			
		End If 
		
		
		
		
		
	Next 
	
	
	
	
		
   ; gravity
	TranslateEntity bomba%, 0, -.5, 0
	
	
	UpdateWorld() 
	RenderWorld()
	
	Text 0, 0, TFormedY()
	Flip() 
	
	
	
	
Wend
