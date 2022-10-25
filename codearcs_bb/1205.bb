; ID: 1205
; Author: Jonathan Nguyen
; Date: 2004-11-23 22:01:07
; Title: Direct Entity-to-Type Relation
; Description: Basically how to grab the type related to an entity without iterating through the type.

; // Initialize Graphics
Graphics3D 640,480,32,0
SeedRnd MilliSecs()

; // Color Sphere
Type ColorSphere
	Field name$
	Field r,g,b
	Field ent
End Type

; // Create Color Sphere
Function CreateColorSphere(name$,r,g,b)
	CS.ColorSphere=New ColorSphere
	CS\name$=name$
	CS\r=r
	CS\g=g
	CS\b=b
	CS\ent=CreateSphere(8)
		NameEntity CS\ent,Str$(Handle(CS.ColorSphere))
		PositionEntity CS\ent,Rnd(-480,480),Rnd(-360,360),640+Rnd(-100,100)
		tRadius#=Rnd(32,128)
		ScaleEntity CS\ent,tRadius#,tRadius#,tRadius#
		EntityPickMode CS\ent,2
		EntityColor CS\ent,CS\r,CS\g,CS\b
End Function

; // Create Some
CreateColorSphere("Red Rod",192,64,64)
CreateColorSphere("Blue Bill",64,64,192)
CreateColorSphere("Green George",64,192,64)
CreateColorSphere("Purple Pill",192,64,192)
CreateColorSphere("Yellow Yanny",192,192,64)
CreateColorSphere("Orange Orange",192,96,64)
CreateColorSphere("Teal Tiger",64,192,192)
CreateColorSphere("White Washington",192,192,192)
CreateColorSphere("Black BMW",64,64,64)

; // Create Camera and Sun
Global Camera=CreateCamera()
Global Sun=CreateLight(2)
	PositionEntity Sun,0,0,320
	LightColor Sun,160,160,160
	AmbientLight 48,48,48

; // Main Loop
SetFont LoadFont("Tahoma",12,False,False,False)
SetBuffer BackBuffer()
While Not KeyDown(1)
	
	; // Check Pick
	CameraPick Camera,MouseX(),MouseY()
	CS.ColorSphere=Null
	If Not PickedEntity()=0
		CS.ColorSphere=Object.ColorSphere(Int(EntityName(PickedEntity())))
	EndIf

	; // Render
	UpdateWorld
	RenderWorld

	; // Draw Cursor
	mx=MouseX()
	my=MouseY()
	Color 32,32,32
	For tx=-1 To 1
		For ty=-1 To 1
			Line mx+tx,my+ty,mx+6+tx,my+6+ty
			Line mx+tx,my+ty,mx+tx,my+8+ty
		Next
	Next
	Color 255,255,255
	Line mx,my,mx+6,my+6
	Line mx,my,mx,my+8
	
	; // Show Data
	If Not CS.ColorSphere=Null
		Color 32,32,32
		OutlineText mx+9,my,"Name: "+CS\name$
		OutlineText mx+9,my+12,"Color: "+Str$(CS\r)+","+Str$(CS\g)+","+Str$(CS\b)
		Color 255,255,255
		Text mx+9,my,"Name: "+CS\name$
		Text mx+9,my+12,"Color: "+Str$(CS\r)+","+Str$(CS\g)+","+Str$(CS\b)
	EndIf

	; // Flip
	Flip

; // End Main Loop
Wend

; // Outline Text
Function OutlineText(TextX,TextY,TextTxt$,TextCenter=False,TextVertical=False)
	For tx=-1 To 1 
		For ty=-1 To 1
			Text TextX+tx,TextY+ty,TextTxt$,TextCenter,TextVertical
		Next
	Next
End Function
