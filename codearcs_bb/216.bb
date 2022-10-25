; ID: 216
; Author: David Bird(Birdie)
; Date: 2002-02-02 06:20:38
; Title: Fast Entity Find
; Description: A method to retrieve type info from an entity

Graphics3D 640,480
SetBuffer BackBuffer()

piv=CreatePivot()
cam=CreateCamera(piv)
PositionEntity cam,0,0,-40

Type entity
	Field ent
	Field name$
	Field inf1$
	Field inf2$
End Type

CreateLight()

For r=1 To 2000
	a.entity=New entity
	a\ent=CreateCube()
	EntityPickMode a\ent,2
	PositionEntity a\ent,Rnd(-120,120),Rnd(-120,120),Rnd(0,240)
	a\name$=Handle( a )
	NameEntity a\ent,a\name
	a\inf1$="System num="+r
	a\inf2$="Planets "+Rand(12)
Next
old.entity=Null

While Not KeyDown(1)
	UpdateWorld
	RenderWorld
	xm=MouseX()
	ym=MouseY()
	If MouseDown(1)						;new method
		ent=CameraPick(cam,xm,ym)
		If ent<>0 Then
			t=MilliSecs()
			tempint=EntityName(ent)
			old=e.entity
			For test=1 To 15000
			e=FindEntityFast(ent);Object.entity(tempint)
			Next
			tk=MilliSecs()-t
			searchtype=0

		End If
	End If

	If MouseHit(2)						;old method
		ent=CameraPick(cam,xm,ym)
		If ent<>0 Then
			old=e
			t=MilliSecs()
			For test=1 To 15000
			e=FindEntity(ent)
			Next
			tk=MilliSecs()-t
			searchtype=1
		End If
	End If

	Color 0,0,100
	Rect 0,0,GraphicsWidth(),105
	Color 255,255,0

	If e<>Null Then 
		EntityColor e\ent,Rnd(255),Rnd(255),Rnd(255)
		If searchtype=0 Then
			Text 320,0,"New search been done Results",1
		Else
			Text 320,0,"Old search been done Results",1
		End If

		Text 320,15,"Time taken to pick type *15000 times="+tk+" Millisecs",1
	End If
	If old<>Null Then 
		If old<>e Then EntityColor old\ent,255,255,255
	End If
	Text 320,30,"(c)2002 David Bird  enquire@davebird.fsnet.co.uk",1
	Text 0,60,"Search function. Old sys intterates all 2000 new sys direct pointer to type"
	Text 0,75,"Click LMB for new search"
	Text 0,90,"Click RMB for old search"
	Color 255,0,0
	Line xm,ym,xm+10,ym+10
	Rect xm-1,ym-1,3,3
	Flip
Wend

Function FindEntity.entity(ent)
	For e.entity=Each entity
		If e\ent=ent Then Return e
	Next
End Function

Function FindEntityFast.entity(ent)
	ti=EntityName(ent)
	Return Object.entity(ti)
End Function
