; ID: 1392
; Author: ozak
; Date: 2005-06-11 13:45:13
; Title: Heightmap class
; Description: Heightmap class

' Heightmap class by Odin Jensen (www.furi.dk)
' Free to use as you please :)

Strict

' Heightmap class
Type HeightMap

	' Dimensions of map
	Field Width:Int
	Field Height:Int
	Field TileSize:Int
	
	' Map data
	Field Data:TBank
	
	' Byte buffer for speed (Still ass slow though :)
	Field DataPTR:Byte Ptr
	
	' Load map
	Method Load(URL:Object, Width:Int, Height:Int, TileSize:Int)
	
		' Save dimensions
		self.Width = Width
		self.Height = Height
		self.TileSize = TileSize
		
		' Load raw map (Now this is easier than java :)
		Data = LoadBank(URL)	
		
		' Grab a pointer
		DataPTR = BankBuf(Data)						
	
	EndMethod
	
	' Draw map (Could be optimized with display lists as it doesn't change, but I'll leave that as an exersize for me or the reader :)
	Method Draw()
	
		' Temp height storage
		Local Height1:Int = 0;
        Local Height2:Int = 0;
		Local Height3:Int = 0;
		Local Height4:Int = 0;
                
        ' Calc offset to center mesh at 0,0,0
        Local StartX:Int = -(Width / 2);
        Local StartY:Int = -(Height / 2);
        
        ' UV coordinate storage
        Local u1:Float, u2:Float, u3:Float, u4:Float, v1:Float, v2:Float, v3:Float, v4:Float;  

		' Vertex color storage
		Local Color1:Float, Color2:Float, Color3:Float, Color4:Float
		
		' Shade factor
		Local ShadeFactor:Float = 0.20

	    ' We'll convert the 2D map to triangles thank you.
		glBegin(GL_TRIANGLES);
		
		' Loop through it
		For Local x:Int = 0 To Width				
			For Local y:Int = 0 To Height
			
			   ' Grab heights
			   Height1 = GetHeight(x, y)
               Height2 = GetHeight(x, y + TileSize)
               Height3 = GetHeight(x + TileSize, y + TileSize)
               Height4 = GetHeight(x + TileSize, y)

			   ' Calculate color
	     	   Color1 = ShadeFactor + Height1 / 256.0
               Color2 = ShadeFactor + Height2 / 256.0
               Color3 = ShadeFactor + Height3 / 256.0
               Color4 = ShadeFactor + Height4 / 256.0
			    
			   ' Calculate UV coords
			   u1 = Float(x) / Float(Width);
               v1 = Float(y) / Float(Height);
               
               u2 = Float(x) / Float(Width);
               v2 = Float(y + TileSize) / Float(Height);
               
               u3 = Float(x + TileSize) / Float(Width);
               v3 = Float(y + TileSize) / Float(Height);
               
               u4 = Float(x + TileSize) / Float(Width);
               v4 = Float(y) / Float(Height);			

			   ' Draw triangles
			   glColor3f(Color1, Color1, Color1);
               glTexCoord2f(u1, v1)
               glVertex3i(StartX + x, Height1, StartY + y)
               
 			   glColor3f(Color2, Color2, Color2);
               glTexCoord2f(u2, v2)
               glVertex3i(StartX + x, Height2, (StartY + y) + TileSize)
               
			   glColor3f(Color4, Color4, Color4);                   
			   glTexCoord2f(u4, v4)
               glVertex3i((StartX + x) + TileSize, Height4, (StartY+ y))
               
               ' Triangle 2
			   glColor3f(Color4, Color4, Color4);                   
               glTexCoord2f(u4, v4)
               glVertex3i((StartX + x) + TileSize, Height4, StartY+ y)
               
 			   glColor3f(Color2, Color2, Color2);
               glTexCoord2f(u2, v2)
               glVertex3i(StartX + x, Height2, (StartY + y) + TileSize)
               
 			   glColor3f(Color3, Color3, Color3);
               glTexCoord2f(u3, v3)
               glVertex3i((StartX + x) + TileSize, Height3, (StartY + y) + TileSize)

			   ' Advance y
			   y = y + TileSize-1
			
			Next
			
			  ' Advance x
			  x = x + TileSize-1

        Next
         
		
		' End triangle rendering
		glEnd();
	
	
	EndMethod
	
	' Get height. Use this to make the player walk on the map :)
	Method GetHeight:Int(X:Int, Y:Int)
		
		' Get position in 2D map space
		Local Tx:Int = X Mod Width;					
        Local Ty:Int = Y Mod Height;	

		' Grab from data
		Return DataPTR[Tx + (Ty * Width)]

	EndMethod


EndType
