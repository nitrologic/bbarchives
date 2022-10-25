; ID: 254
; Author: Rhodan
; Date: 2002-02-28 08:47:14
; Title: Relative Radar example
; Description: Plots a 2D radar from a 3D scene

;======================================
; Relative Radar (2D coords)
; Mostly resolution independant except
; scope circle will be ovalish at some
; resolutions
;
; The AngleFinder entity is there to let
; me find the relative angle between the
; emitter heading and target position.
; There's likely a math method of figuring
; this out but, I'm lazy =)

; See my Sin()/Cos() demo in the code
; archives if you don't understand
; what they do.

Graphics3D 1280,1024
SetBuffer BackBuffer()

Global mycountry=0,radarrange,contactcount
; screen size dependant stuff
Global radarscoperadius=GraphicsWidth()*0.1
Global radarscopecenterx=GraphicsWidth()*0.88
Global radarscopecentery=GraphicsHeight()*0.15
; plotting a single pixel at 1280x1024 is a TAD small so
Global radardotsize=GraphicsWidth()/300


; Make a radar emitter object and angle-finding pivot
Global Emitter=CreateCube()
ScaleEntity emitter ,1,1,3

Global AngleFinder=CreatePivot(Emitter)

; Make some contacts, random placement and countries
Type t_contacts
	Field country
	Field entity
End Type


SeedRnd(MilliSecs())
For i = 0 To 9
	x#=Rand(-100,100)
	z#=Rand(-100,100)
	contact.t_contacts=New t_contacts
	contact\country=Rand(0,2)
	contact\entity=CreateCube()
	PositionEntity contact\entity,x#,0,Z#
	Select contact\country
		Case 0	
			EntityColor contact\entity,255,0,0
		Case 1
			EntityColor contact\entity,0,255,0
		Case 2
			EntityColor contact\entity,0,0,255
	End Select
Next
; although the X/Z difference is only 100, radar range
; of 100 won't necessarily catch them all. Straight
; line distance to 100,100 is MORE than 100 away.

camera=CreateCamera()
PositionEntity camera, 0,-150,0
RotateEntity camera,-90,0,0

light=CreateLight()
AmbientLight 100,100,100

; variable radar ranges, why not?
Dim radarranges(2)
	radarranges(0)=100
	radarranges(1)=50
	radarranges(2)=20
	
Repeat
	UpdateWorld
	RenderWorld
	If KeyHit(200) Then radarindex=radarindex+1
	If KeyHit(208) Then radarindex=radarindex-1
	If radarindex>2 Then radarindex=0
	If radarindex<0 Then radarindex=2
	radarrange=radarranges(radarindex)
	If KeyDown(203) Then TurnEntity emitter, 0,-10,0
	If KeyDown(205) Then TurnEntity emitter, 0,10,0

	Radar()

	Color 255,255,255
	Text 20,10,"Heading:"+Int(EntityYaw(emitter))+" Radar Range:"+radarrange+" Contacts:"+contactcount
	Delay 100
	Flip
Until KeyHit(1)
End

Function Radar()

	contactcount=0
	Color 255,255,255
	Oval radarscopecenterx-radarscoperadius,radarscopecentery-radarscoperadius,radarscoperadius*2,radarscoperadius*2,False
	Plot radarscopecenterx,radarscopecentery
	radarrangeratio#=Float radarscoperadius/Float radarrange
	For c.t_contacts=Each t_contacts
		If EntityDistance(emitter,c\entity)<radarrange
			PointEntity AngleFinder,c\entity
			radarpointx=Sin(EntityYaw(AngleFinder))*radarrangeratio#*EntityDistance(emitter,c\entity)
			radarpointz=Cos(EntityYaw(AngleFinder))*radarrangeratio#*EntityDistance(emitter,c\entity)
			Select c\country
				Case 0	
					Color 255,0,0
				Case 1
					Color 0,255,0
				Case 2
					Color 0,0,255
			End Select
			Oval radarscopecenterx-radarpointx,radarscopecentery+radarpointz,radardotsize,radardotsize,True
			contactcount=contactcount+1
		EndIf
	Next
End Function
