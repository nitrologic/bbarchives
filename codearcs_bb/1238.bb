; ID: 1238
; Author: Picklesworth
; Date: 2004-12-15 18:26:37
; Title: Movement Pegs
; Description: These are commonly used in 3d modelling tools to move objects about

;;;;;;;;;;;;;Example code;;;;;;;;;;;;;
;Movement Pegs v 0.4, example program
;2004, Dylan McCall
;(Mr. Picklesworth)

Graphics3D 1024,768,False,2
SetBuffer BackBuffer()

AmbientLight 200,200,200
light = CreateLight()
PositionEntity light,-10,5,-10

cam = CreateCamera()
PositionEntity cam,0,0,-10

cube = CreateCube()

MovePegs=MovePegs_Create(700,500,5,1,8,80)

While Not KeyDown(1)		
	TranslateEntity cube,MovePegs_GetOutputX(MovePegs),MovePegs_GetOutputY(MovePegs),MovePegs_GetOutputZ(MovePegs)	
	TurnEntity cube,MovePegs_GetOutputPitch(MovePegs),MovePegs_GetOutputYaw(MovePegs),MovePegs_GetOutputRoll(MovePegs)
	
	RenderWorld
	MovePegs_Update()
	Flip
	Cls
Wend
End

;;;;;;;;;;;;;System code;;;;;;;;;;;;;
;Movement Pegs v 0.4
;2004, Dylan McCall
;(Mr. Picklesworth)

Type MovePegs
	Field BaseX,BaseY	
	Field BaseDrag
	Field PegXDrag,PegYDrag,PegZDrag
	Field RotXDrag,RotYDrag,RotZDrag
	Field LastX,LastY	
	Field XSpeed#,YSpeed#,ZSpeed#
	Field PitchSpeed#,YawSpeed#,RollSpeed#
	Field MoveScale#
	Field RotScale#
	Field Size#
	Field BaseSize
	Field CircleSize
End Type

Function RectOverLine(x1#,y1#,x2#,y2#,rx#,ry#,rw#,rh#)	
	For h = -rh/2 To rh
	For w = -rw/2 To rw
		If PointOverLine(x1#,y1#,x2#,y2#,rx#+w,ry#+h) = True Then Return True
	Next
	Next
End Function

Function PointOverLine(x1#,y1#,x2#,y2#,px#,py#)
	;m1# = (y2-y1)/(x2-x1)
	m1# = (y2-y1)/(x2-x1)
	m2# = (py-y1)/(px-x1)
	If m1=m2 And Distance(x1,y1,px,py) <= Distance(x1,y1,x2,y2)
		If px => x1 And py <= y1 Return True
	EndIf
End Function

;Quick note regarding moveScale and RotationScale - I was asleep when I programmed the
;movement scale, and so it is the reverse of what scale would normally mean. Rather
;than multiply by movescale or rotscale, it divides. Consider this a good thing,
;because the numbers can get rather high so it would be confusing with decimals.
Function MovePegs_Create(x,y,moveScale#=1,rotationScale#=0.5,BaseSize=8,size#=80)
	m.movepegs = New movepegs
	m\MoveScale = moveScale
	m\RotScale = rotationScale
	m\baseX = x
	m\baseY = y
	m\BaseSize=BaseSize
	
	m\size=size
	m\CircleSize#=m\size / 8
	
	Return Handle(m)
End Function

Function MovePegs_Position(entity,x,y,Glob=True)
	m.movepegs=Object.movepegs(entity)
	If Glob
		m\baseX = x
		m\baseY = y		
	Else
		m\baseX = m\baseX + x
		m\baseY = m\baseY + y
	EndIf
End Function

Function MovePegs_SetBaseSize(entity,size#)
	m.movepegs=Object.movepegs(entity)
	m\BaseSize=Size
End Function

Function MovePegs_SetSize(entity,size#)
	m.movepegs=Object.movepegs(entity)
	m\size# = size
	m\CircleSize#=m\size / 8
End Function

Function MovePegs_SetMoveScale(entity,scale#)
	m.movepegs=Object.movepegs(entity)
	m\MoveScale# = scale
End Function

Function MovePegs_SetRotationScale(entity,scale#)
	m.movepegs=Object.movepegs(entity)
	m\RotScale# = scale
End Function

Function MovePegs_Update()
	For m.movepegs = Each movepegs
		
		CircleXX=m\BaseX+(m\size/1.6)
		CircleXY=m\BaseY-((m\CircleSize*1.4)/2)
		CircleYX=m\BaseX-((m\CircleSize*1.4)/2)
		CircleYY=m\BaseY-(m\size/8)-(m\size/1.6)
		CircleZX=m\BaseX+(m\size/2.8)
		CircleZY=m\BaseY-(m\size/2)	
		
		If m\pegXdrag Then Color 255,255,0 Else Color 255,0,0 ;X line
		Line m\BaseX,m\BaseY,m\BaseX+m\size,m\BaseY
		
		If m\rotXDrag Then Color 255,255,0 Else Color 255,0,0 ;X circle
		Oval CircleXX,CircleXY,m\CircleSize,m\CircleSize*1.4			
			
		If m\pegYdrag Then Color 255,255,0 Else Color 0,255,0 ;Y line
		Line m\BaseX,m\BaseY,m\BaseX,m\BaseY-m\size
		
		If m\rotYDrag Then Color 255,255,0 Else Color 0,255,0 ;Y circle
		Oval CircleYX,CircleYY,m\CircleSize*1.4,m\CircleSize

		If m\pegZdrag Then Color 255,255,0 Else Color 0,0,255 ;Z line
		Line m\BaseX,m\BaseY,m\BaseX+(m\size/1.6),m\BaseY-(m\size/1.6)	
				
		If m\rotZDrag Then Color 255,255,0 Else Color 0,0,255 ;Z circle
		Oval CircleZX,CircleZY,m\CircleSize*1.4,m\CircleSize
		
		Color 100,100,100 ;Base
		Rect m\BaseX-(m\BaseSize/2),m\BaseY-(m\BaseSize/2),m\BaseSize,m\BaseSize		
		
		;Color 255,255,255		
		;Line m\baseX+4,m\baseY-4,m\BaseX+50,m\BaseY-50
		
		;m\pegXdrag=0 And m\pegYdrag=0 And m\pegZdrag=0
				
		If MouseHit(1)
			If RectsOverlap(MouseX(),MouseY(),1,1,m\BaseX-(m\BaseSize/2),m\BaseY-(m\BaseSize/2),m\BaseSize,m\BaseSize)
				m\BaseDrag = True
				Goto skipclick ;Clicking on base
			EndIf
			
			If RectsOverlap(MouseX(),MouseY(),1,1,CircleXX,CircleXY,m\CircleSize,m\CircleSize*1.4)
				m\RotXDrag = True
				Goto skipclick ;Clicking on X axis Rotate (Pitch)			
			EndIf
			If RectsOverlap(MouseX(),MouseY(),1,1,CircleYX,CircleYY,m\CircleSize*1.4,m\CircleSize)
				m\RotYDrag = True
				Goto skipclick ;Clicking on Y axis Rotate (Yaw)
			EndIf
			If RectsOverlap(MouseX(),MouseY(),1,1,CircleZX,CircleZY,m\CircleSize*1.4,m\CircleSize)
				m\RotZDrag = True
				Goto skipclick ;Clicking on Z axis Rotate (Roll)
			EndIf
			
			If RectOverLine(m\baseX,m\baseY,m\BaseX+(m\size/1.6),m\BaseY-(m\size/1.6),MouseX(),MouseY(),8,8)
				m\PegZDrag = True
				m\Lastx=MouseX() : m\LastY=MouseY()
				Goto skipclick ;Z Axis Click
			EndIf
			If RectOverLine(m\BaseX,m\BaseY,m\BaseX+m\size,m\BaseY,MouseX(),MouseY(),8,8)
				m\PegXDrag = True
				m\Lastx=MouseX() : m\LastY=MouseY()
				Goto skipclick ;X Axis Click
			EndIf
			If RectOverLine(m\BaseX,m\BaseY,m\BaseX,m\BaseY-m\size,MouseX(),MouseY(),8,8)
				m\PegYDrag = True
				m\Lastx=MouseX() : m\LastY=MouseY()
				Goto skipclick ;Y Axis Click
			EndIf

			ElseIf Not MouseDown(1)
				m\BaseDrag = False
				m\PegXDrag = False : m\PegYDrag = False : m\PegZDrag = False
				m\RotXDrag = False : m\RotYDrag = False : m\RotZDrag = False
				m\XSpeed# = 0 : m\YSpeed# = 0 : m\ZSpeed# = 0	
				m\PitchSpeed# = 0 : m\YawSpeed# = 0 : m\RollSpeed# = 0
				m\LastX = mousex() : m\LastY = mousey()
		EndIf
		.skipclick
		If m\BaseDrag
			m\baseX = MouseX()
			m\baseY = MouseY()			
		EndIf

		If m\pegXDrag
			;m\XSpeed = Distance(m\LastX,m\LastY,MouseX(),MouseY())
			;If MouseX() < m\LastX Or MouseY() < m\LastY Then m\XSpeed= -m\XSpeed
			m\XSpeed = MouseX() - m\LastX
			m\LastX = MouseX()
			m\LastY = MouseY()				
		EndIf
		If m\pegYDrag
			;m\YSpeed = Distance(m\LastX,m\LastY,MouseX(),MouseY())
			;If MouseX() < m\LastX Or MouseY() < m\LastY Then m\YSpeed= -m\YSpeed
			m\YSpeed = m\LastY - MouseY()
			m\LastX = MouseX()
			m\LastY = MouseY()			
		EndIf
		If m\pegZDrag
			;ZSpeedA = MouseY() - m\LastY
			;ZSpeedB = MouseX() - m\LastX
			m\ZSpeed = m\LastY - MouseY()
			m\LastX = MouseX()
			m\LastY = MouseY()				
		EndIf
		
		If m\RotXDrag
			m\PitchSpeed = Distance2(m\LastX,m\LastY,MouseX(),MouseY())
			
			;Checks if we should go backwards, or forwards, in the rotation.
			m\LastX = MouseX()
			m\LastY = MouseY()				
		EndIf
		If m\RotYDrag
			m\YawSpeed = Distance2(m\LastX,m\LastY,MouseX(),MouseY())
			m\LastX = MouseX()
			m\LastY = MouseY()				
		EndIf
		If m\RotZDrag
			m\RollSpeed = Distance2(m\LastX,m\LastY,MouseX(),MouseY())
			m\LastX = MouseX()
			m\LastY = MouseY()			
		EndIf

	Next
End Function

function MovePegs_IsActive(entity) ;This is grammatically correct. Honest!
	;Note: Does not return anything is it is just being moved by the base
	m.movepegs = Object.movepegs(entity)
	if m\PegXDrag or m\PegYDrag or m\PegZDrag or m\RotXDrag or m\RotYDrag or m\RotZDrag then return 1
end function

Function MovePegs_GetOutputX#(entity)
	m.movepegs = Object.movepegs(entity)
	Return m\XSpeed# / m\MoveScale
End Function
Function MovePegs_GetOutputY#(entity)
	m.movepegs = Object.movepegs(entity)
	Return m\YSpeed# / m\MoveScale
End Function
Function MovePegs_GetOutputZ#(entity)
	m.movepegs = Object.movepegs(entity)
	Return m\ZSpeed# / m\MoveScale
End Function


Function MovePegs_GetOutputPitch#(entity)
	m.movepegs = Object.movepegs(entity)
	Return m\PitchSpeed# / m\RotScale
End Function
Function MovePegs_GetOutputYaw#(entity)
	m.movepegs = Object.movepegs(entity)
	Return m\YawSpeed# / m\RotScale
End Function
Function MovePegs_GetOutputRoll#(entity)
	m.movepegs = Object.movepegs(entity)
	Return m\RollSpeed# / m\RotScale
End Function

Function EntityProject(camera,entity)
	CameraProject camera,EntityX(entity,1),EntityY(entity,1),EntityZ(entity,1)
End Function

Function GetGreatest(a#,b#)
	If a#>b# Then Return a# Else Return b#
End Function

Function Distance#(x1#,y1#,x2#,y2#)
	;Uses Pythagorus theorum
	Return Sqr(((x2-x1)^2)+((y2-y1)^2))
End Function

Function Distance2#(x1#,y1#,x2#,y2#)
	;Returns negative or positive numbers
	X# = x1-x2
	Y# = y1-y2
	Return X+Y
End Function
