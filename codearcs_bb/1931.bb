; ID: 1931
; Author: b32
; Date: 2007-02-20 19:36:14
; Title: polygon drawing program
; Description: add points to polygon

;----------------------------------------------	
	;setup graphics	
	Graphics3D 800, 600, 0, 2
	SetBuffer BackBuffer()
	
	;point type
	Type exPoint
		Field x#
		Field y#
		Field z#
	End Type

	;globals
	Dim 	Cursor_Down(2)
	Dim 	Cursor_Hit(2)
	Global 	Cursor_X, Cursor_Y		
	Global 	camera
	
	;create picking plane
	plane = CreatePlane()
	RotateEntity plane, -90, 0, 0
	EntityPickMode plane, 2
	
	;create picking camera
	camera = CreateCamera()
	MoveEntity camera, 0, 0, -15
	
	;main loop		
	Repeat
	
		ReadMouse()
			
		RenderWorld()
			
		CameraPick camera, Cursor_X, Cursor_Y

		;create/select point	
		If Cursor_Hit(1) Or Cursor_Hit(2) Then
			;within this range, a point is selected
			maxdist# = 25
			
			;init selection at Null
			sel.exPoint = Null
			
			;check all points
			For ex.exPoint = Each exPoint
				CameraProject camera, ex\x, ex\y, ex\z
				dist# = VDist(ProjectedX(), ProjectedY(), Cursor_X, Cursor_Y)
				;if point is within range, select it
				If dist# < maxdist# Then 
					sel = ex
					maxdist = dist
				End If
			Next
			
			;if no point is selected, and the left MB is hit, create new point			
			If sel = Null Then			
				If Cursor_Hit(1) Then sel = CheckPoint (PickedX(), PickedY(), PickedZ())
			End If
		End If
		
		;if mouse is down, move selected point
		If Cursor_Down(1) Then					
			If sel <> Null Then
				sel\x = PickedX()
				sel\y = PickedY()
				sel\z = PickedZ()
				
				;if point is dragged to the screen boundries, remove it
				If Cursor_X = 0 Then 
					Delete sel
				ElseIf Cursor_X = GraphicsWidth() Then 
					Delete sel
				ElseIf Cursor_Y = 0 Then 
					Delete sel
				ElseIf Cursor_Y = GraphicsHeight() Then 
					Delete sel
				End If
			End If
		End If
	
		;draw points and lines		
		Color 255, 255, 255
		ex2.exPoint = Null
		For ex.exPoint = Each exPoint
		
			;connect first point to last point
			If ex = Last exPoint Then
				ex2 = First exPoint
			Else
				ex2 = After ex
			End If
					
			DrawProjLine ex2, ex
			
		Next
		
		;draw selected point		
		If sel <> Null Then DrawProjPoint sel
		
		;remove point with DEL (keyhit needs to be called every loop to clear buffer)
		If KeyHit(211) Then If sel <> Null Then Delete sel
			
		Text 0, 0, "Add vertices with the mouse"
		Flip
	
	;ESC = exit	
	Until KeyHit(1)
	
	End

;-----------------------------------------------------------------------------------------------------
;													VDist()
;-----------------------------------------------------------------------------------------------------
;gets distance between two points
Function VDist#(x1#, y1#, x2#, y2#)

	Return Sqr((x2 - x1) ^ 2 + (y2 - y1) ^ 2)
	
End Function

; ID: 1162
; Author: TomToad
; Date: 2004-09-21 14:58:10
; Title: minimum distance from point to line
; Description: find the distance from a point to a line in 2D

;-----------------------------------------------------------------------------------------------------
;												PointToPointDist()
;-----------------------------------------------------------------------------------------------------
;get distance between two points
Function PointToPointDist#(x1#,y1#,x2#,y2#)
	
	dx# = x1-x2
	dy# = y1-y2
	
	Return Sqr(dx*dx + dy*dy)
	
End Function

;-----------------------------------------------------------------------------------------------------
;												MinDistPointLine()
;-----------------------------------------------------------------------------------------------------
;find distance point->line (thanks tomtoad)
Function MinDistPointLine#(px#,py#,x1#,y1#,x2#,y2#)

	If x1 = x2 And y1 = y2 Then Return PointToPointDist(px,py,x1,y1)
	
	sx# = x2-x1
	sy# = y2-y1
	
	q# = ((px-x1) * (x2-x1) + (py - y1) * (y2-y1)) / (sx*sx + sy*sy)
	
	If q < 0.0 Then q = 0.0
	If q > 1.0 Then q = 1.0
	
	Return PointToPointDist(px,py,(1-q)*x1+q*x2,(1-q)*y1 + q*y2)

End Function

;-----------------------------------------------------------------------------------------------------
;												DrawProjPoint()
;-----------------------------------------------------------------------------------------------------
;draw projected point
Function DrawProjPoint( ex.exPoint )

	CameraProject camera, ex\x, ex\y, ex\z
	x1# = ProjectedX()
	y1# = ProjectedY()
	Color 0, 255, 0
	Oval x1 - 5, y1 - 5, 11, 11

End Function

;-----------------------------------------------------------------------------------------------------
;												DrawProjLine()
;-----------------------------------------------------------------------------------------------------
;draw projected line
Function DrawProjLine( ex.exPoint, ex2.exPoint )

	CameraProject camera, ex\x, ex\y, ex\z
	x1# = ProjectedX()
	y1# = ProjectedY()
	CameraProject camera, ex2\x, ex2\y, ex2\z
	x2# = ProjectedX()
	y2# = ProjectedY()
	Color 255, 255, 255
	Line x1, y1, x2, y2
	Oval x1 - 5, y1 - 5, 11, 11
	Oval x2 - 5, y2 - 5, 11, 11
	
End Function

;-----------------------------------------------------------------------------------------------------
;												CheckPoint()
;-----------------------------------------------------------------------------------------------------
;create new point and insert it at the closest line
Function CheckPoint.exPoint( ix#, iy#, iz# )

		;get projected point
		CameraProject camera, ix#, iy#, iz#
		xx# = ProjectedX()
		yy# = ProjectedY()

		;set a big maximum distance		
		maxdist# = 10000

		selEx.exPoint = Null
		For ex.exPoint = Each exPoint
		
			;connect first point with last point
			If ex = Last exPoint Then
				ex2.exPoint = First exPoint
			Else
				ex2.exPoint = After ex
			End If

			;project line
			CameraProject camera, ex\x, ex\y, ex\z
			x1# = ProjectedX()
			y1# = ProjectedY()
			CameraProject camera, ex2\x, ex2\y, ex2\z
			x2# = ProjectedX()
			y2# = ProjectedY()
			
			;get distance
			dist# = MinDistPointLine(xx, yy, x1, y1, x2, y2)
			;if this distance is smaller than maxdist, select point
			If dist# < maxdist# Then 
				selEx = ex
				;set new maxdist
				maxdist# = dist#
			End If
				
		Next

		;create new point		
		cex.exPoint = New exPoint
		cex\x = ix
		cex\y = iy
		cex\z = iz
		
		;if a line is selected, insert this point on it
		If selEx <> Null Then Insert cex After selEx

		;return created point		
		Return cex

End Function

;-----------------------------------------------------------------------------------------------------
;													ReadMouse()
;-----------------------------------------------------------------------------------------------------
;read mouse properties
Function ReadMouse()
		Cursor_Hit(1) = MouseHit(1)
		Cursor_Hit(2) = MouseHit(2)
		Cursor_X = MouseX()
		Cursor_Y = MouseY()
		Cursor_Down(1) = MouseDown(1)
		Cursor_Down(2) = MouseDown(2)
End Function
