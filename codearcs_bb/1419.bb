; ID: 1419
; Author: Booticus
; Date: 2005-07-12 14:35:42
; Title: 2d continent generator
; Description: Creates a random 2d continent

' This program uses the Fault Terrain Generation method to generate...yep! TERRAINS!
' Im was aiming to use this to generate simple textures for some spheres to make planets
' when Blitzmax 3d comes out. But in the meantime, if anyone can use this, great! If not,
' bail! ;)
' Super detailed info on this method of terrain generation can be found at:
' http://www.lighthouse3d.com/opengl/terrain/index.php3?fault

Strict

' -------------------------------------
Framework brl.glmax2d
Import  brl.linkedlist
Import  brl.random
Import  brl.system


SetGraphicsDriver GLMax2DDriver()

SeedRnd MilliSecs()

Graphics 1024,768
SetBlend ALPHABLEND	' Select ALPHABLEND

' Set up our map types, they'll initialize
' in their own Tlist, defined in the type
Local maps=0

For Local a:Int = 0 To maps
	Cls
	Local thecontinent:continent = New continent
	thecontinent.init
	thecontinent.createcontinent(800)
Next

'#Region Main loop
While Not KeyHit(KEY_ESCAPE)
	checkkey
	Cls
	displaymaps
	FlushMem
	SetColor 255,255,255
	drawtext "Hit SPACE to generate another random continent. ESC to exit.",100,15
	Flip
Wend
'#End Region

Type continent
	Field continentwidth:Int = 64
	Field continentheight:Int = 32
	Field terrain:Float[continentwidth,continentheight]
	Field ascii:String[continentwidth,continentheight]
	Global continentlist:TList
	
	Method New ()
		continentwidth:-1
		continentheight:-1		
		If continentlist = Null Then continentlist = CreateList ()
		ListAddLast continentlist, Self
	End Method
	
	Method init()
		For Local x:Int = 0 To continentwidth
			For Local y:Int = 0 To continentheight
				terrain[x,y]=50 ' We're resetting each spot on our continent to be 50 high.
				ascii[x,y]="A"
			Next
		Next
	End Method
	
	Method createcontinent(theiterations:Int) 
		' Iteration is the amount of passes
		' the algorithm runs. The more passes, the
		' more detailed. Fiddle with variables!
		' This method works well, I like its results
		Local increase:Float = .75 ' Fiddle with this in small amounts to see some different products.
		Local a:Int
		Local x:Int
		Local y:Int
		Local b:Int
		Local d:Int
		Local c:Int
		Local w:Int = continentwidth
		Local l:Int = continentheight
		Local iterations:Int = theiterations
		
		For x = 0 To continentwidth
			For y = 0 To continentheight
				terrain[x,y]=50 ' We're resetting each spot on our continent
		 		ascii[x,y]="A"
			Next
		Next
	
		For Local j:Int = 0 To iterations
			Local x1:Int=Rnd(-w,w*2)
			Local z1:Int=Rnd(-l,l)
			Local x2:Int	
			Local z2:Int	
			Repeat
				x2=Rnd(-w,w*2)
				z2=Rnd(-l,l)
			Until x2<>x1 And z2<>z1
			'a = (z2 - z1)
			'b = -(x2 - x1)
			a = (z2 - z1)
			b = -(x2 - x1)
			c = -x1*(z2-z1) + z1*(x2-x1)		
	
			For x = 0 To continentwidth
				For y = 0 To continentheight
					If (a*x + b*y - c > 0) 
						terrain[x,y] :+ increase
						If terrain[x,y] > 255
							terrain[x,y]=255
						EndIf					
					Else
						terrain[x,y] :- increase
						If terrain[x,y] < 1
							terrain[x,y]=1
						EndIf					
					EndIf
				Next
			Next
		Next

		' Smooth the terrain
		' THIS takes a lot of CPU time...remove for quicker
		' continent generation, but less smooth detail.
		Self.smooth

		
		' OK weve generated our continent by faultlines.
		' Now, begin checking terraint height, and assign
		' an asciiI character accordingly for tilemap.
		' This function will draw our continent data
		' as derived from our terrain[x,y] array
		Local offsetx:Int=0
		Local offsety:Int=0
		Local level1:Int=50
		Local level2:Int = 55
		Local level3:Int = 58
		Local level4:Int = 61
		Local level5:Int = 64
		Local level6:Int = 67
		Local level7:Int = 70
		Local level8:Int = 73
		Local level9:Int = 76
		Local level10:Int = 79
		For x = 0 To continentwidth
			For y = 0 To continentheight
				' Now in here you can experiment with the coloring of the
				' display depending on height. So for instance I currently
				' have only the sealevel as a coloring, anything above
				' the sealevel is drawn as a increasingly bright shade of
				' green. Maybe above certain heights, change to white
				' color for snowcapped mountains, etc.
				Local i:Float=terrain[x,y]
		 		If i>=level1 And i<level2
		 			ascii[x,y]="A"
				Else If i>=level2 And i<level3
		 			ascii[x,y]="B"				
				Else If i >=level3 And i<level4
		 			ascii[x,y]="C"
				Else If i>=level4 And i<level5
		 			ascii[x,y]="D"
				Else If i>=level5 And i<level6
		 			ascii[x,y]="E"
				Else If i>=level6 And i<level7
		 			ascii[x,y]="F"
				Else If i>=level7 And i<level8
		 			ascii[x,y]="G"
				Else If i>=level8 And i<level9
		 			ascii[x,y]="H"
				Else If i>=level9 And i<level10
		 			ascii[x,y]="J"
				Else If i>level10
		 			ascii[x,y]="J"
				EndIf
			Next
		Next
		
		' This next is optional. It will Normalize
		' our terrain, EACH TERRAIN x,y AT A TIME!
		' So it might be a time consumer
		
	End Method
	
	Method draw(offsetx:Int,offsety:Int)
		' This function will draw our continent data
		' as derived from our terrain[x,y] array
		' Yes, we can probably use a grabimage
		' to grab an image of the continent instead
		' of drawing each and every pixel....
		' but Im far too lazy.
		Local x:Int
		Local y:Int
		For x = 0 To continentwidth
			For y = 0 To continentheight
				SetColor 75,75,255
				Select ascii[x,y]
					Case "A"
						SetColor 75,75,255
					Case "B"
						SetColor 132,247,140
					Case "C"
						SetColor 231,239,115
					Case "D"
						SetColor 255,206,82
					Case "E"
						SetColor 214,115,66
					Case "F"
						SetColor 148,0,0
					Case "G"
						SetColor 214,0,0
					Case "H"
						SetColor 200,200,200
					Case "I"
						SetColor 222,222,222
					Case "J"
						SetColor 255,255,255
				End Select
				Plot x+offsetx,y+offsety
			Next
		Next
	
	End Method
	
	Method drawascii(offsetx:Int,offsety:Int)
		' This function will draw our continent data
		' as derived from our terrain[x,y] array
		Local textoffset:Int = 10
		Local x:Int
		Local y:Int
		' Thin of these as the height levels
		' on an elevation map...
		For x = 0 To continentwidth
			For y = 0 To continentheight	
				' Now in here you can experiment with the coloring of the
				' display depending on height. So for instance I currently
				' have only the sealevel as a coloring, anything above
				' the sealevel is drawn as a increasingly bright shade of
				' green. Maybe above certain heights, change to white
				' color for snowcapped mountains, etc.
				Select ascii[x,y]
					Case "A"
						SetColor 75,75,255
					Case "B"
						SetColor 132,247,140
					Case "C"
						SetColor 231,239,115
					Case "D"
						SetColor 255,206,82
					Case "E"
						SetColor 214,115,66
					Case "F"
						SetColor 148,0,0
					Case "G"
						SetColor 214,0,0
					Case "H"
						SetColor 200,200,200
					Case "I"
						SetColor 222,222,222
					Case "J"
						SetColor 255,255,255
				End Select

				DrawText ascii[x,y],x*textoffset+offsetx,y*textoffset+offsety
			Next
		Next
	End Method
	
	Method smooth()
		Local x:Int
		Local y:Int
		Local k:Float = 0.75
	
		'/* Rows, left to right */
		For x = 1 To continentwidth
			For y = 0 To continentheight
				terrain[x,y] = terrain[x-1,y] * (1-k) + terrain[x,y] * k
			Next
		Next
	
		'/* Rows, right to left*/
		For x = continentwidth-1 To 0 Step -1
			For y = 0 To continentheight
				terrain[x,y] = terrain[x+1,y] * (1-k) + terrain[x,y] * k
			Next
		Next
	
		'/* Columns, bottom to top */
		For x = 0 To continentwidth
			For y = 1 To continentheight
				terrain[x,y] = terrain[x,y-1] * (1-k) + terrain[x,y] * k
			Next
		Next
	
		'/* Columns, top to bottom */
		For x = 0 To continentwidth
			For y = continentheight -1To 0 Step-1
				terrain[x,y] = terrain[x,y+1] * (1-k) + terrain[x,y] * k
			Next
		Next
	
	End Method
	
	Method destroy()
		ListRemove(continentlist,Self)					
	End Method

End Type

Function displaymaps()
	Local i:Int=0
    For Local thecontinent:continent = EachIn continent.continentlist
        thecontinent.Draw(i*32+2,0)
        thecontinent.Drawascii(i*32,40)
        i:+1
    Next
End Function

Function checkkey()
	If KeyHit(KEY_SPACE)
		resetall
	EndIf	
End Function

Function resetall()
	Local i:Int=1
	   For Local thecontinent:continent = EachIn continent.continentlist
	       thecontinent.destroy
	       i:+1
	   Next
		' Set up our maps
		Local maps=0
		For Local a:Int = 0 To maps
			Local thecontinent:continent = New continent
			thecontinent.init
			thecontinent.createcontinent(800)
		Next
End Function
