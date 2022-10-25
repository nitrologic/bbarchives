; ID: 2685
; Author: Warner
; Date: 2010-04-01 06:48:42
; Title: 2d in 3d
; Description: engine to do 2d in 3d

;-------------------------------------------------------------------------------------------------------------------------------------
;															FILE:DEMO
;-------------------------------------------------------------------------------------------------------------------------------------

;	all include files are marked with the "FILE:" tag.
; 	since the lib can only LOAD images, images are generated on the fly using "SaveImage"
;   in the function CreateImages()
;
;	First part is demo
;	For manual, see end of file

;-------------------------------------------------------------------------------------------------------------------------------------
;															Setup Graphics
;-------------------------------------------------------------------------------------------------------------------------------------

	Graphics3D 800,600, 0, 2
	SetBuffer BackBuffer()
	;init engine			
	SetupGraphics()	
	
	CreateImages()
	
	cursor = LoadImage("cursor.bmp")
	MidHandle cursor

;-------------------------------------------------------------------------------------------------------------------------------------
;															Globals
;-------------------------------------------------------------------------------------------------------------------------------------

	Global background.TObject
	Global alien.TObject
	Global turret.TObject
	Global projectile.TObject

;-------------------------------------------------------------------------------------------------------------------------------------
;															Setup scene
;-------------------------------------------------------------------------------------------------------------------------------------
		
	;load background
	background.TObject = LoadObject("background.bmp")
	Position background, 400, 300

	;create lists	
	Global AlienList.TList = CreateList()
	Global TurretList.TList = CreateList()
	Global ProjectileList.TList = CreateList()

;-------------------------------------------------------------------------------------------------------------------------------------
;															Main Loop
;-------------------------------------------------------------------------------------------------------------------------------------
	
	Repeat

		;RMB = create new alien	
		If MouseHit(2) Then
		
			;create and add to list
			alien = LoadObject("alien.bmp", 3, 64, 64)		
			AddToList(AlienList, alien)

			;place at mouse			
			msX = MouseX() * 800 / GraphicsWidth()
			msY = MouseY() * 600 / GraphicsHeight()
			Position alien, msX, msY
			
		End If			

		;LMB = create new turret		
		If MouseHit(1) Then
		
			;create and add to list
			turret = LoadObject("turret.bmp", 4, 64, 64)
			AddToList(TurretList, turret)
			
			;place at mouse
			msX = MouseX() * 800 / GraphicsWidth()
			msY = MouseY() * 600 / GraphicsHeight()
			Position turret, msX, msy
			
		End If

;------------------------------------------------------------------------------------------------------------------------------------
;																Turrets
;------------------------------------------------------------------------------------------------------------------------------------
				
		;aim all Turrets to closest alien
		For i = 0 To ListCount(TurretList) - 1
			turret = GetListObject(TurretList, i)
						
			;find closest and aim
			max# = 10000.0
			For j = 0 To ListCount(AlienList) - 1
				alien = GetListObject(AlienList, j)			
				dist# = Distance(alien, turret)
				If dist < max Then 
					PointAt turret, alien
					max = dist
				End If
			Next

			;shooting control
			turret\intVar[1] = turret\intVar[1] + 1
			If turret\intVar[1] > 250 Then turret\intVar[1] = 0
			If turret\intVar[1] = 0 Then
			;when the time is there, start shooting
			
				;create new projectile
				projectile = LoadObject("projectile.bmp")
			
				;store direction (based on turret rotation)	
				projectile\floatVar[0] = Cos(-turret\rotation) * 5
				projectile\floatVar[1] = Sin(-turret\rotation) * 5
				
				;position projectile at torret
				Position projectile, turret\x, turret\y
				
				;add to list
				AddToList(ProjectileList, projectile)				
			End If
			
		Next

;------------------------------------------------------------------------------------------------------------------------------------
;																Projectiles
;------------------------------------------------------------------------------------------------------------------------------------
		
		;move all projectiles
		For i = 0 To ListCount(ProjectileList) - 1
		
			;move projectile in direction it was fired
			projectile = GetListObject(ProjectileList, i)
			Move projectile, projectile\floatVar[0], projectile\floatVar[1]

			;reset remove flag			
			remove = 0
			
			;check if any of the aliens was hit
			For j = 0 To ListCount(AlienList) - 1
				alien = GetListObject(AlienList, j)
				;measure distance to determine impact
				If Distance(alien, projectile) < (alien\radius + projectile\radius)/2 Then
					;remove alien
					RemoveFromList(AlienList, alien)
					Free alien
					;set remove flag
					remove = 1
					;exit loop
					Exit
				End If
			Next

			;when projectile gets out of screen, set remove flag
			If projectile\x < 0 Then remove = 1
			If projectile\y < 0 Then remove = 1
			If projectile\x > 800 Then remove = 1
			If projectile\y > 600 Then remove = 1

			;remove projectile if flag was set			
			If remove Then
				RemoveFromList(ProjectileList, projectile)
				Free projectile
			End If
		Next

;------------------------------------------------------------------------------------------------------------------------------------
;																Aliens
;------------------------------------------------------------------------------------------------------------------------------------

		;alien eating animation control
		
		time = time + 1

		;move all aliens		
		For i = 0 To ListCount(AlienList) - 1

			alien = GetListObject(AlienList, i)

			;find closest turret		
			max# = 10000
			turret = Null
			
			For j = 0 To ListCount(TurretList) - 1
				turr.TObject = GetListObject(TurretList, j)
				
				dist# = distance(turr, alien)
				If dist < max Then
					max = dist
					turret = turr
				End If
			Next
				
			;if a turret was found
			If turret <> Null
												
				;point alien at turret
				PointAt alien, turret
				
				;get distance		
				dist# = Distance( turret, alien )
				
				;if too far away, move closer
				If dist > 50 Then 
					Move alien, 1, 0
				End If

				;if close enough, start eating animation
				If dist < 50 Then
					If time > 19 Then time = 0
					Frame alien, time / 10 + 1
					
					;each object has 255 intVars you can use
					turret\intVar[0] = turret\intVar[0] + 1
				Else
					Frame alien, 0
				End If
				
				;move turret animation while it is eaten
				Frame turret, turret\intVar[0] / 40
				;if eaten completely, remove object from list and destroy it
				If turret\intVar[0] >= 160 Then RemoveFromList(TurretList, turret): Free turret
				
			;if no turret was found
			Else
			
				;turn alien back into position and move along
				Frame alien, 0
				Turn alien, Sgn(270 - alien\rotation)
				Move alien, 1, 0
				
			End If
			
			If alien\x > 800 Then
				RemoveFromList AlienList, alien
				Free alien
			End If
			
		Next			

;------------------------------------------------------------------------------------------------------------------------------------
;																Rendering
;------------------------------------------------------------------------------------------------------------------------------------

		;render all TObjects
		Render()			
		
		Text 0,  0, "You have " + ListCount(TurretList) + " turrets"
		Text 0, 20, "There are " + ListCount(AlienList) + " aliens"
		
		Text 0, 40, "Use LMB to place turrets, use RMB to place aliens"
		Text 0, 60, "ESC=END"
		
		DrawImage cursor, MouseX(), MouseY()
		
		Flip
				
	Until KeyHit(1)
	
	DestroyImages()
	
	End
	
	
	
;------------------------------------------------------------------------------------------------------------------------------------
;																FILE:2DENGINE
;------------------------------------------------------------------------------------------------------------------------------------

Type TTexture
	Field name$
	Field tex
	Field ww#
	Field hh#
End Type

Global orgQuad

;-------------------------------------------------------------------------------------------------------------------------------------
;															SetupGraphics()
;-------------------------------------------------------------------------------------------------------------------------------------
Function SetupGraphics()

	;maak orthographic camera
	cam = CreateCamera() ;create camera
	CameraProjMode cam, 2 ;no perspective rendering
	PositionEntity cam, 0, 0, -512 ;camera to back
	CameraZoom cam, 0.313 ;800x600
	AmbientLight 255, 255, 255 ;fullbright

End Function

;-------------------------------------------------------------------------------------------------------------------------------------
;															GetSprite()
;-------------------------------------------------------------------------------------------------------------------------------------
Global returnTexture%
Global returnRadius#
Global returnWidth#
Global returnHeight#
Function GetSprite(name$, numframes=1, framewidth=0, frameheight=0)

	name$ = Lower$(Trim$(name$))

	tex = 0
	For tx.TTexture = Each TTexture
		If tx\name$ = name$ Then 
			ww = tx\ww
			hh = tx\hh
			tex = tx\tex
			Exit
		End If
	Next

	If tex = 0 Then
		;get image properties
		im = LoadImage(name$)
		
		;if not defined, autodetect width/height
		If framewidth = 0  Then ww = ImageWidth(im)  Else ww = framewidth
		If frameheight = 0 Then hh = ImageHeight(im) Else hh = frameheight
			
		FreeImage im
	
		;load image as texture - flag 4=transparent	
		If numframes > 1 Then
			tex = LoadAnimTexture(name$, 4, ww, hh, 0, numframes)
		Else
			tex = LoadTexture(name$, 4)
		End If
		
		If tex = 0 Then RuntimeError "Could not load texture: " + tex
		
		tx.TTexture = New TTexture
		tx\name$ = name$
		tx\tex = tex
		tx\ww = ww
		tx\hh = hh
	End If
		
	;create quad with image size
	If orgQuad = 0 Then orgQuad = CreateQuad(1, 1): HideEntity orgQuad
	quad = CopyEntity(orgQuad)
	ScaleEntity quad, ww, hh, 1
	EntityTexture quad, tex ;apply texture

	;return biggest radius	
	returnRadius# = Max(ww, hh)
	;return width&height	
	returnWidth = ww
	returnHeight = hh
		
	;return newly created quad and texture
	returnTexture = tex
	Return quad
	
End Function

;-------------------------------------------------------------------------------------------------------------------------------------
;															CreateQuad()
;-------------------------------------------------------------------------------------------------------------------------------------
Function CreateQuad(ww#, hh#)
	
	ww# = ww# / 250.0
	hh# = hh# / 250.0

	mesh = CreateMesh()
	surf = CreateSurface(mesh)
	
	AddVertex surf, -ww,  hh, 0, 0.0, 0.0
	AddVertex surf,  ww,  hh, 0, 1.0, 0.0
	AddVertex surf,  ww, -hh, 0, 1.0, 1.0
	AddVertex surf, -ww, -hh, 0, 0.0, 1.0
	
	AddTriangle surf, 0, 1, 2
	AddTriangle surf, 0, 2, 3
	
	Return mesh
	
End Function

;-------------------------------------------------------------------------------------------------------------------------------------
;															Max()
;-------------------------------------------------------------------------------------------------------------------------------------
Function Max#(a#, b#)
	If a > b Then Return a Else Return b
End Function


;-------------------------------------------------------------------------------------------------------------------------------------
;															FILE:ENTITYENGINE
;-------------------------------------------------------------------------------------------------------------------------------------

;object definition
Type TObject
	Field x#
	Field y#
	Field rotation
	Field alpha#	
	Field show%
	
	Field width#
	Field height#
	
	Field scaleX#
	Field scaleY#
	
	Field entity
	Field texture
	Field framecount%	
	Field radius#
	
	Field intVar%[255]
	Field floatVar#[255]
	
	Field parent.TList
End Type

;-------------------------------------------------------------------------------------------------------------------------------------
;															LoadObject()
;-------------------------------------------------------------------------------------------------------------------------------------
;load object from image
Function LoadObject.TObject(file$, numframes=1, framewidth=0, frameheight=0)

	If FileType(file$) <> 1 Then RuntimeError "could not find file: " + file$
	
	obj.TObject = New TObject
	obj\x = 0
	obj\y = 0
	obj\alpha = 1.0
	obj\show = True
	
	obj\entity = GetSprite(file$, numframes, framewidth, frameheight)
	obj\texture = returnTexture
	obj\radius = returnRadius
	
	obj\width = returnWidth
	obj\height = returnHeight
	obj\scaleX = 1
	obj\scaleY = 1
	
	obj\framecount = numframes
	
	If obj\entity = 0 Then RuntimeError "could not load sprite: " + file$
	
	Return obj
	
End Function


;-------------------------------------------------------------------------------------------------------------------------------------
;															PointAt()
;-------------------------------------------------------------------------------------------------------------------------------------
;point one object towards the other
Function PointAt(obj.TObject, obj2.TObject)

	obj\rotation = 270 - ATan2(obj2\y - obj\y, obj2\x - obj\x)

End Function

;-------------------------------------------------------------------------------------------------------------------------------------
;															Distance()
;-------------------------------------------------------------------------------------------------------------------------------------
;return distance between two objects
Function Distance#(obj.TObject, obj2.TObject)

	x# = (obj\x - obj2\x)
	y# = (obj\y - obj2\y)
	
	Return Sqr(x*x+y*y)
	
End Function

;-------------------------------------------------------------------------------------------------------------------------------------
;															PointDistance()
;-------------------------------------------------------------------------------------------------------------------------------------
;return distance between objects and point
Function PointDistance#(obj.TObject, x#, y#)

	x# = (obj\x - x)
	y# = (obj\y - y)
	
	Return Sqr(x*x+y*y)
	
End Function

;-------------------------------------------------------------------------------------------------------------------------------------
;															Move()
;-------------------------------------------------------------------------------------------------------------------------------------
;move object
Function Move(obj.TObject, x#, y#)

	obj\x = obj\x - Sin(obj\rotation) * x + Sin(obj\rotation + 90) * y
	obj\y = obj\y - Cos(obj\rotation) * x + Cos(obj\rotation + 90) * y

End Function

;-------------------------------------------------------------------------------------------------------------------------------------
;															Frame()
;-------------------------------------------------------------------------------------------------------------------------------------
;select animation frame
Function Frame(obj.TObject, frame#)

	frame = Floor(frame)

	While frame < 0
		frame = frame + obj\framecount
	Wend
	frame = frame Mod obj\framecount
	
	EntityTexture obj\entity, obj\texture, frame

End Function

;-------------------------------------------------------------------------------------------------------------------------------------
;															Position()
;-------------------------------------------------------------------------------------------------------------------------------------
;set position
Function Position(obj.TObject, x#, y#)

	obj\x = x
	obj\y = y
	
End Function

;-------------------------------------------------------------------------------------------------------------------------------------
;															Rotate()
;-------------------------------------------------------------------------------------------------------------------------------------
;set rotation
Function Rotate(obj.TObject, angle#)

	obj\rotation = angle
	
End Function

;-------------------------------------------------------------------------------------------------------------------------------------
;															Turn()
;-------------------------------------------------------------------------------------------------------------------------------------
;turn object
Function Turn(obj.TObject, angle#)

	obj\rotation = obj\rotation + angle
	
End Function

;-------------------------------------------------------------------------------------------------------------------------------------
;															Scale()
;-------------------------------------------------------------------------------------------------------------------------------------
;scale object
Function Scale(obj.TObject, scaleX#, scaleY#)

	ScaleEntity obj\entity, obj\width * scaleX, obj\height * scaleY, 1
	obj\scaleX = scaleX
	obj\scaleY = scaleY

End Function

;-------------------------------------------------------------------------------------------------------------------------------------
;															SetOrder()
;-------------------------------------------------------------------------------------------------------------------------------------
;set z-order
Function SetOrder(obj.TObject, order#)
	PositionEntity obj\entity, EntityX(obj\entity), EntityY(obj\entity), -order
End Function

;-------------------------------------------------------------------------------------------------------------------------------------
;															Hide()
;-------------------------------------------------------------------------------------------------------------------------------------
;hide object
Function Hide(obj.TObject)
	obj\show = False
	HideEntity obj\entity
End Function

;-------------------------------------------------------------------------------------------------------------------------------------
;															Show()
;-------------------------------------------------------------------------------------------------------------------------------------
;show object
Function Show(obj.TObject)
	obj\show = True
	ShowEntity obj\entity
End Function

;-------------------------------------------------------------------------------------------------------------------------------------
;															Alpha()
;-------------------------------------------------------------------------------------------------------------------------------------
Function Alpha(obj.TObject, a#)
	obj\alpha = a
End Function

;-------------------------------------------------------------------------------------------------------------------------------------
;															Free()
;-------------------------------------------------------------------------------------------------------------------------------------
;remove entity
Function Free(obj.TObject)
	FreeEntity obj\entity
	Delete obj
End Function


;-------------------------------------------------------------------------------------------------------------------------------------
;															Render()
;-------------------------------------------------------------------------------------------------------------------------------------
;render all
Function Render()

	UpdateObjects()		
	RenderWorld

End Function

;-------------------------------------------------------------------------------------------------------------------------------------
;															UpdateObjects()
;-------------------------------------------------------------------------------------------------------------------------------------
Function UpdateObjects()

	For obj.TObject = Each TObject
		RotateEntity obj\entity, 0, 0, obj\rotation
		PositionEntity obj\entity, (obj\x - 400) * 3.2 / 400, -(obj\y - 300) * 2.4 / 300, EntityZ(obj\entity)
		EntityAlpha obj\entity, obj\alpha
	Next
	
End Function

;-------------------------------------------------------------------------------------------------------------------------------------
;															FILE:TLISTS
;-------------------------------------------------------------------------------------------------------------------------------------

;list type definition
Type TList
	Field index
	Field size
	Field bank
End Type

;-------------------------------------------------------------------------------------------------------------------------------------
;															CreateList()
;-------------------------------------------------------------------------------------------------------------------------------------
Function CreateList.TList()
	l.TList = New TList
	l\size = 1
	l\bank = CreateBank(4)
	l\index = 0
	Return l
End Function

;-------------------------------------------------------------------------------------------------------------------------------------
;															AddToList()
;-------------------------------------------------------------------------------------------------------------------------------------
Function AddToList(l.TList, obj.TObject)

	If l = Null Then RuntimeError "Invalid list"
	If obj = Null Then RuntimeError "Invalid/Null object"

	index = l\index	
	size = l\size
	bank = l\bank
	
	If index >= size Then
		newbank = CreateBank(size*4 * 2)
		CopyBank bank, 0, newbank, 0, size*4
		FreeBank bank
		bank = newbank
		size = size * 2
	End If
	
	PokeInt bank, index * 4, Handle(obj)
	index = index + 1
	
	l\index = index
	l\size = size
	l\bank = bank
	
	obj\parent = l
	
End Function

;-------------------------------------------------------------------------------------------------------------------------------------
;															RemoveFromList()
;-------------------------------------------------------------------------------------------------------------------------------------
Function RemoveFromList(l.TList, obj.TObject)

	If l = Null Then RuntimeError "Invalid list"
	If obj = Null Then Return

	index = l\index	
	size = l\size
	bank = l\bank

	found = -1	
	For i = 0 To index
		getObject.TObject = Object.TObject(PeekInt(bank, i * 4))
		If getObject = obj Then
			found = i
			Exit
		End If
	Next
	
	If found > -1 Then
		If found < index Then CopyBank bank, (found + 1) * 4, bank, found * 4, (index-found-1) * 4
		index = index - 1
	End If
	
	l\index = index
	l\size = size
	l\bank = bank	
	
End Function

;-------------------------------------------------------------------------------------------------------------------------------------
;															ListCount()
;-------------------------------------------------------------------------------------------------------------------------------------
Function ListCount(l.TList)

	Return l\index
	
End Function

;-------------------------------------------------------------------------------------------------------------------------------------
;															GetListObject()
;-------------------------------------------------------------------------------------------------------------------------------------
Function GetListObject.TObject(l.TList, i)

	If l = Null Then RuntimeError "Invalid list"
	If l\index = 0 Then Return Null
	Return Object.TObject(PeekInt(l\bank, i*4))
	
End Function

;-------------------------------------------------------------------------------------------------------------------------------------
;															FILE:IMAGES
;-------------------------------------------------------------------------------------------------------------------------------------

;-------------------------------------------------------------------------------------------------------------------------------------
;															CreateImages()
;-------------------------------------------------------------------------------------------------------------------------------------
Function CreateImages()

	Locate 0, 0
	Cls
	Print "cursor.bmp"
	Print "alien.bmp"
	Print "background.bmp"
	Print "projectile.bmp"
	Print "turret.bmp"
	Print
	a$ = Input("you want to create all these image files (y/n) ?")
	a$ = Lower$(Trim$(a$))
	If a$ <> "y" Then RuntimeError "cannot proceed.." :End

	CreateCursorImage()
	CreateAlienImage()
	CreateBackgroundImage()
	CreateProjectileImage()
	CreateTurretImage()
	
End Function

Function DestroyImages()

	Locate 0, 0
	Cls
	Print "cursor.bmp"
	Print "alien.bmp"
	Print "background.bmp"
	Print "projectile.bmp"
	Print "turret.bmp"
	Print
	a$ = Input("you want to delete all these image files (y/n) ?")
	a$ = Lower$(Trim$(a$))
	If a$ <> "y" Then Return
	If FileType("cursor.bmp") = 1 Then DeleteFile "cursor.bmp"
	If FileType("alien.bmp") = 1 Then DeleteFile "alien.bmp"
	If FileType("background.bmp") = 1 Then DeleteFile "background.bmp"
	If FileType("projectile.bmp") = 1 Then DeleteFile "projectile.bmp"
	If FileType("turret.bmp") = 1 Then DeleteFile "turret.bmp"
End Function

Function CreateCursorImage()
	im = CreateImage(32, 32)
	Cls
	Rect 15, 0, 3, 32
	Rect 0, 15, 32, 3
	GrabImage im, 0, 0
	SaveImage im, "cursor.bmp"
	FreeImage im
End Function

Function CreateAlienImage()
	im = CreateImage(128, 128)
	Cls
	sc = 5
	For i = 0 To 1
	For j = 0 To 1
		Color 64, 128, 255
		Oval i*64,j*64,64,64, 1
		mx = i *64+32
		my = j*64+32
		Color 8, 12, 64
		Oval mx-sc, my-sc+8, sc*2,sc*2, 1
		
		Oval mx-16, my - 14, 4, 4
		Oval mx+12, my - 14, 4, 4
		sc = sc + 2
	Next
	Next
	GrabImage im, 0, 0
	SaveImage im, "alien.bmp"
	FreeImage im
End Function

Function CreateBackgroundImage()
	im = CreateImage(800, 600)
	Cls
	Color 0, 0, 255
	Rect 0, 0, 800, 300
	Color 0, 255, 0
	Rect 0, 300, 800, 300
	GrabImage im,0,0
	SaveImage im, "background.bmp"
	FreeImage im
	Color 255,255,255
End Function

Function CreateProjectileImage()
	Cls
	For i = 16 To 0 Step -1
		c = i * 16
		Color c, c, c
		Oval 16-i,16-i,i*2,i*2
	Next
	im = CreateImage(32,32)
	GrabImage im, 0, 0
	SaveImage im, "projectile.bmp"
	FreeImage im
End Function

Function CreateTurretImage()
	im = CreateImage(128, 128)
	Cls
	sc = 0
	For j = 0 To 1
	For i = 0 To 1
		Color 255, 0, 0
		Oval i*64,j*64,64,64
		For ic = 1 To sc
			x = Rand(54) + i * 64
			y = Rand(54) + j * 64
			Color 0, 0, 0
			Oval x, y, 10, 10
		Next
		sc = sc + 35
		Color 255,255,255
		Rect i * 64 + 30, j * 64, 4, 32
	Next
	Next
	GrabImage im,0,0
	SaveImage im, "turret.bmp"
	FreeImage im
	Color 255,255,255
End Function



;-------------------------------------------------------------------------------------------------------------------------------------
;																Manual
;-------------------------------------------------------------------------------------------------------------------------------------
;
;NB: where "object" is "TObject instance"
;
;LoadObject( file$ )
;	Loads and returns an object from an image file
;
;LoadObject( file$, numFrames%, frameWidth%, frameHeight% )
;	Loads and returns an object from an image that contains animation
;	
;PointAt( object1, object2 )
;	Points an object towards another object
;
;Distance( object1, object2 )
;	Returns the distance between two objects
;
;PointDistance( object1, x#, y# )
;	Returns the distance between a point and an object
;
;Move( object, x#, y# )
;	Moves the object in the direction it is aimed (x=forward)
;
;Frame( object, frame% )
;	Selects the animation frame on the object
;
;Position( object, x#, y# )
;	Places the object on a specific x,y location (screen=allways 800x600)
;
;Rotate( object, angle# )
;	Rotates an object to a specific angle
;
;Scale( object, scaleX#, scaleY#)
;	Scales an object
;
;Turn( object, angle# )
;	Turns an object with a relative angle
;
;SetOrder( object, order )
;	Sets the Z-order for the object
;
;Alpha( object, alpha# )
;	Sets the transparency of an object
;
;Hide( object )
;	Hides the object
;
;Show( object )
;	Shows an (previously hidden) object
;
;Free( object )
;	Removes an object
;
;Render
;	Renders all objects
;
;CreateList()
;	Create (and returns) a new TList
;
;AddToList( list, object )
;	Add object to TList
;
;RemoveFromList( list, object)
;	Remove object from TList
;
