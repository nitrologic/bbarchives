; ID: 2921
; Author: Arska
; Date: 2012-02-14 04:09:32
; Title: Tile engine
; Description: Terraria like

' By Ari Salonen
' Arska134


Strict 


Graphics 800,600


Global gravity:Float = 0.0
Global generatedBlocks:Int

Global natureLight:Int = 150

Global frames:Int
Global mouse2:Int

' Type for world's blocks
Type BLOCKS
	Field img:TImage
	Field frame:Int
	Field obj:String
	Field selected:Int
	Field health:Float
	Field isDeleted:Int
	Field distance:Int
	Field light:Int
	Field lightR:Int
	Field lightG:Int
	Field lightB:Int
	
	Field x:Int
	Field y:Int

	Function ShowText()
		For Local x:Int =0 Until MAPSIZE_X
			For Local y:Int =0 Until MAPSIZE_Y
			
				If map[x,y].selected
					DrawText "X: "+x+": Y "+y+" Detoration: "+map[x,y].health, MouseX(), MouseY()
				EndIf
				
			Next 
		Next 
	
	End Function
	
	
	Method draw(x:Float, y:Float)
			
		SetBlend ALPHABLEND
		
		
		SetColor 255,255,255
		
		
		If light = True Then 
			SetColor lightR,lightG,lightB
		Else
			SetColor natureLight,natureLight,natureLight
		EndIf
		
		
		SetAlpha 1
		DrawRect x,y,TILESIZE_X, TILESIZE_Y
		
		
		
		If selected
			SetColor 255,0,0
			SetAlpha 0.4
			DrawRect x,y,TILESIZE_X, TILESIZE_Y
		EndIf
		
		
		DrawImage img, x, y, frame ' Draw all tiles
		
		
		' Default color and alpha
		SetColor 255,255,255
		SetAlpha 1
				
	End Method
	
	' Sky draw
	Method draw2(x:Float, y:Float)
			
		SetBlend ALPHABLEND
		
		' Ambient light?
		SetColor natureLight,natureLight,natureLight
		SetAlpha 1.0
		DrawRect x,y,TILESIZE_X, TILESIZE_Y
		
		
		DrawImage img, x, y, frame ' Draw all tiles
		
		obj = "sky"
		
		' Default color and alpha
		SetColor 255,255,255
		SetAlpha 1.0
				
	End Method
	
	
End Type



Const TILESiZE_X:Int = 10
Const TILESIZE_Y:Int = 10

Global MAPSIZE_X:Int, MAPSIZE_Y:Int


MAPSIZE_X = GraphicsWidth()/TILESIZE_X
MAPSIZE_Y = GraphicsHeight()/TILESIZE_Y

Global map:BLOCKS[MAPSIZE_X, MAPSIZE_Y] 


Local tileset:TImage = LoadAnimImage("GFX/tileset.png", TILESiZE_X, TILESiZE_Y, 0, 14, MASKEDIMAGE)


If Not tileset RuntimeError "Tileset not found!"




SeedRnd(MilliSecs())


' Generate map
For Local x:Int = 0 Until MAPSIZE_X
	For Local y:Int = 0 Until MAPSIZE_Y
	
		map[x,y] = New BLOCKS

		map[x,y].img = tileset ' Our tileset image
		Local val:Int = Rand(0,1)
		If val = 0 And y > 25 Then val = Rand(1,1)

		map[x,y].health = 100
		If y<Rand(20,25) Then map[x,y].health =0
		
		generatedBlocks = generatedBlocks + 1
		
		
		
		If map[x,y].health >0 Then
		Select val
			Case 0   ' GrassBlock
				map[x,y].frame = 2
				
				Local randSeed:Int = Rand(0,10)
				If randSeed = 10 Then 
					map[x,y].frame = 7
				EndIf
				
				
			Case 1   ' DirtBlock
				map[x,y].frame = 0
					
				
		End Select 
		EndIf
		
		
		
	Next 
Next 		

' world.txt where is info about all generated blocks
Global mapDataFile:TStream=WriteFile("world.txt")


' Reading data about map and writing it to file
For Local x:Int =0 Until MAPSIZE_X
	For Local y:Int =0 Until MAPSIZE_Y
		MakeRandomCave(Rand(3,12))
		MakeRandomDeposite(3,5) ' Rock deposites
		WriteLine(mapDataFile:TStream, x+" "+y+" "+map[x,y].frame)
	Next
Next

CloseStream mapDataFile:TStream


Local countFPS:Float=MilliSecs()
Local currentFPS:Int = 0

HideMouse()
Repeat
	
	Cls
	
		frames = frames +1

		Local mx:Int = (MouseX()/TILESiZE_X)
		Local my:Int = (MouseY()/TILESiZE_Y)
		

		For Local x:Int =0 Until MAPSIZE_X
			For Local y:Int = 0 Until MAPSIZE_Y
				map[x,y].selected = False
			Next 
		Next 
	
		If mx=>0 And mx <MAPSIZE_X
			If my=>0 And my<MAPSIZE_Y
				map[mx, my].selected = True
				If MouseHit(1) Then map[mx, my].health = map[mx, my].health - 10
			EndIf	
		EndIf
		
		
		

	
		For Local x:Int = 0 Until MAPSIZE_X
			For Local y:Int = 0 Until MAPSIZE_Y			

				
				' Let's check distance of blocks near pointer
				map[x,y].distance = (x - mx)*(x - mx) + (y - my)*(y - my)
				
				' Some light if block is near enough
				If map[x,y].distance < 70 Then
					map[x,y].light  = True
					map[x,y].lightR = 255
					map[x,y].lightG = 255
					map[x,y].lightB = 230
				Else
					map[x,y].light = 0
				EndIf
				
				
				
								
				If map[x,y].health =< 0 Then 
					map[x,y].frame = 12
				EndIf
				
				If map[x,y].health > 0 Then
					map[x,y].draw(x*TILESIZE_X, y*TILESIZE_Y)
				EndIf
				
				If map[x,y].health =< 0 And y < 20 Then
					map[x,y].draw2(x*TILESIZE_X, y*TILESIZE_Y)
				EndIf
				
				If map[mx,my].health =< 0 And map[mx,my].selected=1 And mouse2=True Then 
					map[mx,my].health = 10
					map[mx,my].frame = 13
					map[mx,my].obj = "light"
				EndIf
				
				
			
			Next 
		Next 

		
		
		
		
		If MouseHit(2) Then 
			mouse2=True
		Else
			mouse2=False
		EndIf
		

		' Change daylight with F1 and F2
		If KeyDown(KEY_F1) And natureLight > 20 Then natureLight=natureLight-1
		If KeyDown(KEY_F2) And natureLight =< 255 Then natureLight=natureLight+1



		DrawText "Generated blocks: "+generatedBlocks,10,20
		DrawText "FPS: "+currentFPS+" Frames: "+frames,10,40
		
		
		
		
		If MilliSecs() > countFPS + 1000 Then
			currentFPS=frames
			frames=0
			countFPS = MilliSecs()
		EndIf
		
		BLOCKS.ShowText()
	Flip
Until KeyHit(KEY_ESCAPE)




' Deposit creation function
Function MakeRandomDeposite(frame:Int,size:Int)
	Local del:Int = Rand(0,100)
	
	If del=100 Then
	Local x:Int, y:Int
	
	x = Rand(0, MAPSIZE_X-size)
	y = Rand(0, MAPSIZE_Y-size)
	Local delrand:Int
	
	For Local xx:Int = x Until x+size
		For Local yy:Int = y Until y+size
			delrand = Rand(0,10)
			If delrand>3 Then map[xx, yy].frame = frame
		Next 
	Next 
	
	
	EndIf
End Function
	

' Cave creation function
Function MakeRandomCave(size:Int)
	Local del:Int = Rand(0,150)
	
	If del=150 Then
	Local x:Int, y:Int
	
	x = Rand(0, MAPSIZE_X-size)
	y = Rand(0, MAPSIZE_Y-size)
	Local delrand:Int
	
	For Local xx:Int = x Until x+size
		For Local yy:Int = y Until y+size
			delrand = Rand(0,10)
			If delrand>3 Then map[xx, yy].health = 0
		Next 
	Next 
	

	EndIf
End Function
