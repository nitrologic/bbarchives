; ID: 2675
; Author: Charrua
; Date: 2010-03-23 15:11:06
; Title: Path following system
; Description: Create paths made by wayPoints and let the system interpolate between them

Global Path_x#, Path_y#, Path_z#

Type tWayPoint

	Field PathId	; with path it belongs
	Field Entity	; a pivot with position and orientation
	Field cp1		; control point1
	Field cp2		; control point2
	Field nSteps	; number of interpolated steps to the next waypoint
	Field CurrStep	; current step interpolated (from 1 to nSteps)
	Field Pitch#, Yaw#, Roll#
	

End Type

Type tPath

	Field PathId		;id 
	Field pFirst		;handle to the first on this list
	Field pLast			;same for last

	Field WPCount		;quantity of waypoints on this path
	Field CurrentWP		;handlw of the current waypoint, in whic we are
	Field CurrPos		;pivot wiht pos and orientation of the step in the path
						;acrodingly with CurrentStep of the CurrentWP

End Type



;wp functions

Function NextWP.tWayPoint(wp.tWayPoint)
	wpTemp.tWayPoint = After wp
	If wpTemp = Null
		wpTemp = FirstWP()
	EndIf
	Return wpTemp
End Function

Function PrevWP.tWayPoint(wp.tWayPoint)
	wpTemp.tWayPoint = Before wp
	If wpTemp = Null
		wpTemp = LastWP()
	EndIf
	Return wpTemp
End Function

Function FirstWP.tWayPoint()
	Return First tWayPoint
End Function


Function LastWP.tWayPoint()
	Return Last tWayPoint
End Function

Function NewWP.tWayPoint(Path.tPath, x#, y#, z#, nSteps=100, pitch#=0, yaw#=0, roll#=0)

	wp.tWayPoint = New tWayPoint

	wp\PathID = Path\PathID
	
	Path\WPCount = Path\WPCount+1
	If Path\pFirst=0 Then
		Path\pFirst = Handle(wp)
		Path\CurrentWP = Path\pFirst
		PositionEntity Path\CurrPos,x,y,z
		RotateEntity Path\CurrPos,pitch,yaw,roll
	End If
	Path\pLast = Handle(wp)
	wp\Entity = CreatePivot()
	wp\cp1 = CreatePivot(wp\Entity)
	wp\cp2 = CreatePivot(wp\Entity)
	PositionEntity wp\Entity,x,y,z
	wp\nSteps = nSteps
	wp\CurrStep = 0
	wp\pitch = pitch
	wp\yaw = yaw
	wp\roll = roll
	
	Return wp

End Function

;path functions


Function PathNextWP.tWayPoint(Path.tPath)
	Local wp.tWayPoint
	If Path\CurrentWP = Path\pLast Then
		Path\CurrentWP = Path\pFirst
	Else
		wp = After PathCurrWP(Path)
		Path\CurrentWP = Handle(wp)
	End If
	Return PathCurrWP(Path)
End Function

Function PathPrevWP.tWayPoint(Path.tPath)
	Local wp.tWayPoint
	If Path\CurrentWP = Path\pFirst Then
		Path\CurrentWP = Path\pLast
	Else
		wp = Before PathCurrWP(Path)
		Path\CurrentWP = Handle(wp)
	End If
	Return PathCurrWP(Path)
End Function

Function PathFirstWP.tWayPoint(Path.tPath)
	Path\CurrentWP = Path\pFirst
	Return PathCurrWP(Path)
End Function

Function PathLastWP.tWayPoint(Path.tPath)
	Path\CurrentWP = Path\pLast
	Return PathCurrWP(Path)
End Function

Function PathCurrWP.tWayPoint(Path.tPath)
	Return Object.tWayPoint(Path\CurrentWP)
End Function

Function NewPath.tPath()

	Path.tPath = New tPath
	Path\PathId = Handle(Path)
	Path\CurrPos = CreatePivot()
	
	Return Path
		
End Function


Function InitPath(Path.tPath, Save=False)

	;precalculates 2 control points for each WayPoint
	;they are needed for the InterpolatePos function based on a Bezier curve
	
	If Save Then
		CurrentWP = Path\CurrentWP
	End If

	CurrWP.tWayPoint = PathFirstWP(Path)

	For i=1 To Path\WPCount+1
	
		PrevWP.tWayPoint = PathPrevWP(Path)	;previous	-1
		CurrWP.tWayPoint = PathNextWP(Path)	;actual		 0
		NextWP.tWayPoint = PathNextWP(Path)	;next		+1, so in each loop advance one element

		If save Then
			If Handle(CurrWP)<>Path\CurrentWP Then CurrWP\CurrStep=0
		Else
			CurrWP\CurrStep = 0
		End If

		NextAux = CopyEntity(NextWP\Entity)
		PrevAux = CopyEntity(PrevWP\Entity)
		PointEntity CurrWP\Entity,NextAux
		PointEntity PrevAux,NextAux
		
		RotateEntity CurrWP\cp1, EntityPitch(PrevAux,True), EntityYaw(PrevAux,True), EntityRoll(PrevAux,True),True
		Dist# = D3D(CurrWP\Entity,NextAux)
		PositionEntity CurrWP\cp1,0,0,0
		MoveEntity CurrWP\cp1,0,0,-Dist/3.0
		PositionEntity PrevWp\cp2, EntityX(CurrWP\cp1,True), EntityY(CurrWP\cp1,True), EntityZ(CurrWP\cp1,True), True
		MoveEntity CurrWP\cp1,0,0,2*Dist/3
		FreeEntity NextAux
		FreeEntity PrevAux

	Next
	
	If Save Then
		Path\CurrentWP = CurrentWP
	Else
		wp.tWayPoint = PathFirstWP(Path)
		PositionEntity Path\CurrPos, EntityX(wp\Entity,True), EntityY(wp\Entity,True), EntityY(wp\Entity,True)
	End If


End Function

Function InitPaths(Save=True)

	For Path.tPath = Each tPath
		
		InitPath(Path,Save)
		
	Next
End Function

Function PathNextPos(Path.tPath, SignalEnd=False)

	Local CurrWP.tWayPoint, NextWP.tWayPoint
	
	CurrWP = PathCurrWP(Path)

	If CurrWP\CurrStep=CurrWP\nSteps Then
		CurrWP\CurrStep=0
		CurrWP = PathNextWP(Path)
		If Path\CurrentWP = Path\pFirst Then
			If SignalEnd Then Stop=True
		End If
	End If

	If Not(Stop) Then
	
		NextWP = PathNextWP(Path)
		CurrWP = PathPrevWP(Path)
		
		t# = (1.0*(Float(CurrWP\CurrStep))) / (Float(CurrWP\nSteps))
		
		InterpolatePos( t, CurrWP\Entity, CurrWP\cp1, CurrWP\cp2, NextWP\Entity)
		
		Pitch# = Interpolate(t,CurrWP\Pitch,NextWP\Pitch)
		Yaw#   = Interpolate(t,CurrWP\Yaw  ,NextWP\Yaw  )
		Roll#  = Interpolate(t,CurrWP\Roll ,NextWP\Roll )
		
		PositionEntity Path\CurrPos, Path_x, Path_y, Path_z
		RotateEntity Path\CurrPos, Pitch, Yaw, Roll
	
		CurrWP\CurrStep = CurrWP\CurrStep + 1
	
		
		Return Path\CurrPos
	Else
		Return -1
	End If

End Function

Function PathCurrPos(Path.tPath)
	Return Path\CurrPos
End Function

Function PathsNextPos(SignalEnd=False)
	For Path.tPath = Each tPath
		PathNextPos(Path,SignalEnd)
	Next
End Function

Function Interpolate#(t#, v1#, v2#)
	Return v1 + (v2-v1)*t
End Function

Function InterpolatePos( t#, p1, p2, p3, p4)

	x1# = EntityX(p1,True)
	y1# = EntityY(p1,True)
	z1# = EntityZ(p1,True)
	
	x2# = EntityX(p2,True)
	y2# = EntityY(p2,True)
	z2# = EntityZ(p2,True)

	x3# = EntityX(p3,True)
	y3# = EntityY(p3,True)
	z3# = EntityZ(p3,True)
	
	x4# = EntityX(p4,True)
	y4# = EntityY(p4,True)
	z4# = EntityZ(p4,True)
	
	Path_x = x1 * (1-t)^3 + 3 * x2 * (1-t)^2 * t + 3 * x3 * (1-t) * t^2 + x4 * t^3 
	Path_y = y1 * (1-t)^3 + 3 * y2 * (1-t)^2 * t + 3 * y3 * (1-t) * t^2 + y4 * t^3 
	Path_z = z1 * (1-t)^3 + 3 * z2 * (1-t)^2 * t + 3 * z3 * (1-t) * t^2 + z4 * t^3 

	
End Function


Function PlotPath(camera, Path.tPath)

	SaveCurrent = Path\CurrentWP

	Color 200,200,200
	
	LockBuffer()
	
	CurrWP.tWayPoint = PathFirstWP(Path)
	
	For wp=1 To Path\WPCount
	
		CurrWP.tWayPoint = PathCurrWP(Path)
		NextWP.tWayPoint = PathNextWP(Path)
	
		t# = 1.0 / CurrWP\nSteps
		tinc# = t	
	
		For i=1 To CurrWP\nSteps
		
			InterpolatePos( t, CurrWP\Entity, CurrWP\cp1, CurrWP\cp2, NextWP\Entity)
			
			CameraProject( camera, Path_x, Path_y, Path_z)
			x=ProjectedX()
			y=ProjectedY()
			If x>0 And x<GraphicsWidth() And y>0 And y<GraphicsHeight()
				WritePixelFast ProjectedX(), ProjectedY(), $FFFFFFFF
			End If
			t = t + tinc
		
		Next

	Next
	
	UnlockBuffer()

	Path\CurrentWP = SaveCurrent

End Function

Function PlotPaths(Camera)
	For Path.tPath = Each tPath
		PlotPath(Camera, Path)
	Next
End Function

Function D3D#(obj1, obj2)
	x# = EntityX(obj1) - EntityX(obj2)
	y# = EntityY(obj1) - EntityY(obj2)
	z# = EntityZ(obj1) - EntityZ(obj2)
	Return Sqr(x*x + y*y + z*z)
End Function

Function Example()
	;
	;	Path following system
	;
	;	
	
	Graphics3D 800,600,0,6
	SetBuffer BackBuffer()
	
	pivotCam=CreatePivot()
	camera = CreateCamera(pivotcam)
	light = CreateLight(2)
	PositionEntity light,20,10,-10
	LightRange light,50
	PositionEntity camera,0,0,-10
	
	Viajero = CreateCone()	;viajero : voyager
	RotateMesh Viajero,90,0,0
	EntityColor Viajero,255,255,0
	Temp = CreateCube()
	ScaleEntity Temp,2,.2,.5
	EntityColor Temp,255,0,0
	EntityParent Temp,Viajero
	
	
	; Usage
	;
	;Create some paths	: 
	;						Path_N.tPaht = NewPath()
	;
	
	;Add WayPoints 		:
	;						WP.tWayPoint = NewWP(Path_n.tPath, x#, y#, z#, steps, pitch#, yaw#, roll#)	
	;
	;
	
	; define at least 3 points: 
	;		x,y,z, pitch, yaw, roll	(position and orientation of the waypoint)
	;		Steps are the number of interpolations the system will do to reach the next WayPoint
	;
	;
	
	;Initialize the path:
	;					InitPath(Path_n.tPath), or InitPaths() for init all defined path's
	;
	;InitPath precalculate 2 control points for each waypoint defined on the path, they are needed for the InterpolatePos function
	;wich is based on a Bezier curve.
	
	;
	;To move to the next position: PathNextPos(Path_N), or PathsNextPos() for all
	;
	;this function returns the handle to a pivot that has the newly calculated position and orientation
	;
	
	
	;in this demo:
	;
	;
	;Path1 and Path2 are identical: defined by the same waypoints
	;Path3 and Path4 are identical
	;
	;in the demo, i use Path1 and Path2 for the same object. Path2 is one step ahead Path1 so, we know the next position
	;and force the object to point to the next step.
	;
	;Path3 and Path4 has associated diferent objects and one of them is automatically stepped, the other is stepped under
	;user control (pressing the "A" key)
	;
	
	Restore WayPointDefinition
	
	
		Path1.tPath = NewPath()		;create Path1
	
		EntityParent viajero,Path1\CurrPos
		
		For nPoint = 1 To 4			;define some waypoints
			Read  x# 
			Read  y# 
			Read  z#
			Read Steps
			Read Pitch#
			Read yaw#
			Read roll
	
			wp.tWayPoint = NewWP(Path1, x,y,z, steps, pitch, yaw, roll)
			;create a visible cube for each way point
			temp = CreateCube(wp\entity)
			PositionEntity temp,0,0,0
			RotateEntity temp,0,0,0
			ScaleEntity temp,.1,.1,.1
			EntityColor temp,255,255,0
			
		Next	
		
		Path3.tPath = NewPath()		;create Path3 from a diferent set of points
		temp = CreateCube(Path3\CurrPos)
		ScaleEntity temp,.3,.3,.3
		EntityColor temp,0,0,255
		
		For nPoint = 1 To 4
			Read  x# 
			Read  y# 
			Read  z# 
			Read Pasos
			Read Pitch#
			Read yaw#
			Read roll
	
			wp.tWayPoint = NewWP(Path3, x,y,z, pasos, pitch, yaw, roll)
			
			;create a visible cube for each way point
			temp = CreateCube(wp\entity)
			PositionEntity temp,0,0,0
			RotateEntity temp,0,0,0
			ScaleEntity temp,.1,.1,.1
			EntityColor temp,0, 255,255
	
			
		Next
	
	Restore WayPointDefinition	;create 2 aditional path from the same set of points
	
		Path2.tPath = NewPath()
		
		For nPoint = 1 To 4
			Read  x# 
			Read  y# 
			Read  z# 
			Read Pasos
			Read Pitch#
			Read yaw#
			Read roll
	
			wp.tWayPoint = NewWP(Path2, x,y,z, pasos, pitch, yaw, roll)
			
		Next	
	
		Path4.tPath = NewPath()
		temp = CreateCube(Path4\CurrPos)
		ScaleEntity temp,.3,.3,.3
		EntityColor temp,0,255,0
		
		For nPoint = 1 To 4
			Read  x# 
			Read  y# 
			Read  z# 
			Read Pasos
			Read Pitch#
			Read yaw#
			Read roll
	
			wp.tWayPoint = NewWP(Path4, x,y,z, pasos, pitch, yaw, roll)
			
		Next
		
	;at this moment, we have 4 paths, Path1 and Path2 are clones of each other and se same for Path3 and Path4
	
	
	;Initialize each path
	InitPaths()	;after initialization all paths are on The first waypoint and in the step 0
	
	
	Piv1 = PathCurrPos(Path1)	;fist position of Path1
	Piv2 = PathNextPos(Path2)	;second position of Path2
	PointEntity Piv1,Piv2,EntityRoll(piv1)	;point the Voyager to the next step in the path
	
	ShowPaths = True	;controls the plot of the interpolated steps
	
	
		
	
	Repeat
	
		If KeyDown(203) Then TurnEntity pivotCam, 0,+.5,0
		If KeyDown(205) Then TurnEntity pivotCam, 0,-.5,0
		If KeyDown(30)	Then PathNextPos(Path4)	;only stepped if A key is held down
		
		If KeyHit(57) Then
			ShowPaths = Not(ShowPaths)
		End If
	
		RenderWorld
		
		If ShowPaths Then
			PlotPath(Camera,Path1)
			PlotPath(Camera,Path3)
		End If
		
		;the path system not only interpolate position from one way point to the next via a bezier curve
		;plus, it interpolate pitch, yaw and roll from WayPoint form the next lineary.
		
		;in this demo, i use the Roll interpolated by the system but i prefere to do the voyager to point to the next waypoint.
		Piv1 = PathNextPos(Path1)	;both path are equal but, Path2 is one step ahead of Path1
		Piv2 = PathNextPos(Path2)	;used to force the entity to Point to the next step
		R# = EntityRoll(Piv1)		;the calculated roll is saved and used to stablish the en roll of the object
		PointEntity Piv1,Piv2
		RotateEntity Piv1, EntityPitch(Piv1), EntityYaw(Piv1), R
	
		;for the boxes, the system use the pitch, yaw and roll interpolated by the system		
		temp = PathNextPos(Path3,True)
		If temp=-1 Then
			DebugLog "end of Path3"
		End If
			
		Text 10,10,"Space to show/hide paths"
		Text 10,30,"<- -> tu turn camera"
		Text 10,50,"A to step Path4"
		Text 10,70,TrisRendered()
		
		Flip
	
	Until KeyHit(1)
	

End Function

Example()

End 

.WayPointDefinition
;Path 1
	Data -3, 0, 0, 100, 0, 0,  0
	Data -1, 2,-5, 150, 0, 0, 50
	Data  1, 1, 0, 100, 0, 0,  0
	Data  3,-2,+4, 100, 0, 0,-30
;Path 2
	Data -3, 0, 4,  50,  60,   0,   0
	Data -1, 3, 5, 100,   0,   0,  60
	Data  1, 1, 6,  50,   0, -50,   0
	Data  3,-3, 2, 100, -20,   0, -60
