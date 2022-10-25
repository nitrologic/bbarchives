; ID: 179
; Author: Rob
; Date: 2002-01-04 00:17:26
; Title: Large scale world/entity management
; Description: Keeping the framerate high with really high entity counts

; World management - very large management of entities
;
; Using copyentity to reduce the amount of possible entities which
; in large scale games can kill performance - this code is a guide not a rule
; the fps counter is slow to update too (1 second increments)
; 
; by rob cummings (rob@redflame.net)
;
;

Global pos=1000,mx#,my#,freecount,maxfreeperloop
Global fpsindex,fpstime,fpsfold_millisecs,fpsfps


; free entity entails a performance hit so lets kill em slow
; this needs To change depending on numbers, and can affect performance
maxfreeperloop=25

Type vistype
	Field entity,ball
End Type


Graphics3D 640,480,16,2
camera=CreateCamera()
CameraRange camera,1,8000
light=CreateLight()
worldpivot=CreatePivot()
ball=CreateSphere()

; massive amount here

For i=0 To 5000
	a=CreatePivot()
	PositionEntity a,Rnd(-pos,pos),Rnd(-pos,pos),Rnd(-pos,pos)
	addvis(a)
Next


While Not KeyHit(1)
	mx#=MouseXSpeed()*0.5
	my#=MouseYSpeed()*0.5
	MoveMouse 320,240
	
	TurnEntity camera,my,-mx,0


	freecount=0


	For vis.vistype=Each vistype
		If EntityInView(vis\entity,camera)
			If vis\ball=0
				vis\ball=CopyEntity(ball,vis\entity)
				PositionEntity vis\ball,EntityX(vis\entity),EntityY(vis\entity),EntityZ(vis\entity),1
			EndIf
		Else 
			If vis\ball<>0 And freecount<maxfreeperloop
				FreeEntity vis\ball
				vis\ball=0
				freecount=freecount+1
			EndIf
		EndIf
		;TurnEntity vis\entity,0.5,0,0
		;MoveEntity vis\entity,0,0,1
	Next 

	
	
	UpdateWorld
	RenderWorld
	Text 0,0,fps()
	Text 0,16,TrisRendered()
	Flip
	
Wend
End

Function addvis(ent)
	vis.vistype=New vistype
	vis\entity=ent
	EntityParent vis\entity,worldpivot
End Function


Function fps()
	fpsindex=fpsindex+1
	fpstime=fpstime+MilliSecs()-fpsfold_millisecs
	If fpstime=>1000
		fpsfps=fpsindex
		fpstime=0
		fpsindex=0
	EndIf
	fpsfold_millisecs=MilliSecs()
	Return fpsfps
End Function
