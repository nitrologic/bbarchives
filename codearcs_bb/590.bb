; ID: 590
; Author: EdzUp[GD]
; Date: 2003-02-16 02:49:15
; Title: Path noding
; Description: Path noding demonstration

;
;	NodeTest.bb - Copyright ©2002 EdzUp
;	Coded by Ed Upton
;

Type NodeType
	Field Entity
	Field NodeID
	Field LinkedTo$
End Type

Graphics3D 640,480,16,2
SetBuffer BackBuffer()

Global camera = CreateCamera()
AmbientLight 255,255,255

Global FloorBoard = CreateCube()
ScaleEntity FloorBoard, 4, .1, 3
EntityColor FloorBoard, 255, 0, 0
MoveEntity FloorBoard, .5, 0, 0
EntityPickMode FloorBoard, 2, True

Global Floor2 = CreateCube()
ScaleEntity Floor2, 1, .1, 3
EntityColor Floor2, 255, 0, 0
EntityPickMode Floor2, 2, True

Global wall = CreateCube()
EntityColor Wall, 0, 255, 0
MoveEntity Wall, -.5, 0, 0
ScaleEntity Wall, 1.1, 1.1, 1.1
EntityPickMode Wall, 2, True

MoveEntity Floor2, 7, 0, 0

MoveEntity camera, .5, 10, 0
PointEntity camera, FloorBoard

MoveEntity Camera, 4, 0, 0

Global NodeID=0

SetupNodes()
SetupPaths()

Global cp=0

While Not KeyDown(1)
	cp=0

	UpdateWorld
	RenderWorld
	Color 255,255,255
	For Node.NodeType = Each NodeType
		If Node<>Null
			Text 450,cp, Node\NodeID+"["+Node\LinkedTo$+"]"
			cp=cp+12
		EndIf
	Next
	ShowPaths( NodeID )
	If KeyDown(77)=1
		NodeID = NodeID + 1
		Repeat: Until KeyDown(77)=0
	EndIf
	If KeyDown(75)=1
		NodeID = NodeID - 1
		Repeat: Until KeyDown(75)=0
	EndIf
	Flip
Wend
End

Function SetupNodes()
	Local Xp#=-2.5
	Local Yp#=-2
	Local NodeR
	Local NodeC=0
	
	Restore NodeLayout
	For y=0 To 2
		For x=0 To 5
			Read NodeR
			
			If NodeR=1
				Node.NodeType = New NodeType
				Node\NodeID = NodeC
				NodeC = NodeC + 1
				Node\Entity = CreateCube()
				ScaleEntity Node\Entity, .1, .1, .1
				PositionEntity Node\Entity, Xp#, .5, Yp#
			EndIf
			Xp# = Xp# +2
		Next
		Xp# = -2.5
		Yp# = Yp# + 2
	Next
	
End Function

Function CheckFall( FromEntity, ToEntity )
	Local Dist# = EntityDistance#( FromEntity, ToEntity )
	Local Whole = Dist#/1
	Local Parts# = Dist#/.5
	Local MoverPivot = CreatePivot()				;this is the mover
	Local TargetPivot = CreatePivot()

	PositionEntity MoverPivot, EntityX#( FromEntity ), EntityY#( FromEntity ), EntityZ#( FromEntity )
	
	Repeat
		RotateEntity MoverPivot, 90, 0, 0
		Pick = EntityPick( MoverPivot, 500.0 )
		
		If Pick=0 Then Return False				;if it cant find anything within 500 units then its a fall
		
		If Pick<>0
			PositionEntity TargetPivot, PickedX#(), PickedY#(), PickedZ#()
			Dista# = EntityDistance#( MoverPivot, TargetPivot )
			If Dista#>.5 Then Return False
		EndIf
		
		PointEntity MoverPivot, ToEntity
		MoveEntity MoverPivot, 0, 0, .05
		
		If KeyDown(1)=1 Then End
	Until EntityDistance#( MoverPivot, ToEntity )<.1
	
	Return True			;woohoo we made it so now its safe for walkers
End Function

Function CheckPath$( Entity )
	;this checks the LOS from any node to all other nodes :)
	Local ReturnString$=""		;this is incremented with the node information
	Local Source = CreateCube()
	Local Target = CreateCube()
	
	ScaleEntity Source, .1, .1, .1
	ScaleEntity Target, .1, .1, .1
	
	EntityPickMode Target, 2
	
	For Node.NodeType = Each NodeType
		If Node<>Null
			If Node\Entity<>Entity
				PositionEntity Source, EntityX#( Entity ), EntityY#( Entity ), EntityZ#( Entity )
				PositionEntity Target, EntityX#( Node\Entity ), EntityY#( Node\Entity ), EntityZ#( Node\Entity )
				PointEntity Source, Target
						
				If EntityPick( Source, 500 )=Target
					If CheckFall( Source, Target ) = True
						ReturnString$ = ReturnString$ + "1"
					Else
						ReturnString$ = ReturnString$ + "0"
					EndIf
				Else
					ReturnString$ = ReturnString$ + "-"
				EndIf
			Else
				ReturnString$ = ReturnString$ + "-"	 ;the node shouldnt be able to see itself
			EndIf
		EndIf
	Next
	
	Return ReturnString$
End Function

Function SetupPaths()
	;lays out all paths in editor system
	For Node.NodeType = Each NodeType
		If Node<>Null
			Node\LinkedTo$ = CheckPath$( Node\Entity )
		EndIf
	Next
End Function

Function SourceX#( NodeID )
	For Node.NodeType = Each NodeType
		If Node<>Null
			If Node\NodeID = NodeID
				CameraProject camera, EntityX#( Node\Entity ), EntityY#( Node\Entity ), EntityZ#( Node\Entity )
				Return ProjectedX#()
			EndIf
		EndIf
	Next
End Function

Function SourceY#( NodeID )
	For Node.NodeType = Each NodeType
		If Node<>Null
			If Node\NodeID = NodeID
				CameraProject camera, EntityX#( Node\Entity ), EntityY#( Node\Entity ), EntityZ#( Node\Entity )
				Return ProjectedY#()
			EndIf
		EndIf
	Next
End Function

Function SourceZ#( NodeID )
	For Node.NodeType = Each NodeType
		If Node<>Null
			If Node\NodeID = NodeID
				CameraProject camera, EntityX#( Node\Entity ), EntityY#( Node\Entity ), EntityZ#( Node\Entity )
				Return ProjectedZ#()
			EndIf
		EndIf
	Next
End Function

Function DrawPaths( ID, LinkString$ )
	Local X = SourceX#( ID )
	Local Y = SourceY#( ID )
	Local Z = SourceZ#( ID )
	
	For Node.NodeType = Each NodeType
		If Node<>Null
			If Node\NodeID<>ID
			
				c$ = Mid$( LinkString$, Node\NodeID+1, 1 )
				If c$<>"-"
					NX = SourceX#( Node\NodeID )
					NY = SourceY#( Node\NodeID )
					NZ = SourceZ#( Node\NodeID )
					If c$="1" Then Color 255,255,0 Else Color 0, 0, 255
					Line X, Y, NX, NY
				EndIf
			EndIf
		EndIf
	Next
End Function

Function ShowPaths( ID )
	For Node.NodeType = Each NodeType
		If Node<>Null
			If Node\NodeID = ID
				DrawPaths( Node\NodeID, Node\LinkedTo$ )
				Return Node\LinkedTo$
			EndIf
		EndIf
	Next
End Function

;
; LineOfSight3D()
;
; Usage:
;	observer	= Entity that is looking
;	target		= Entity that the observer is looking for
;	viewrange	= How far can the observer see (in units)
;	viewangle	= How wide is the view of the observer (in degrees)
;
; Created by Mikkel Fredborg - Use as you please
;
Function LineOfSight3D(observer,target,viewrange#=10.0,viewangle# = 90.0, Radius# = 0.01)

	;distance between observer and target
	Local dist# = EntityDistance(observer,target)

	;check if the target is within viewrange 
	If dist<=viewrange
		
		;observer vector
		TFormVector 0,0,1,observer,0
		Local ox# = TFormedX()
		Local oy# = TFormedY()
		Local oz# = TFormedZ()
	
		;pick vector
		Local dx# = (EntityX(target,True)-EntityX(observer,True))/dist#
		Local dy# = (EntityY(target,True)-EntityY(observer,True))/dist#
		Local dz# = (EntityZ(target,True)-EntityZ(observer,True))/dist#

		;dot product
		Local dot# = ox*dx + oy*dy + oz*dz

		;check if the target is within the viewangle
		If dot => Cos(viewangle/2.0)
			; check if something is blocking the view
			If LinePick( EntityX(observer,True), EntityY(observer,True), EntityZ(observer,True), dx*viewrange, dy*viewrange, dz*viewrange, Radius# ) =target
				; observer can see target
				Return True
			End If
		End If
		
	End If

	; observer cannot see target	
	Return False

End Function

.NodeLayout
Data 1, 1, 1, 1, 0, 1
Data 1, 0, 1, 1, 0, 1
Data 1, 1, 1, 1, 0, 1
