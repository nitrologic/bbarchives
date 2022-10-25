; ID: 2550
; Author: Jeppe Nielsen
; Date: 2009-07-28 09:51:05
; Title: Verlet physics with constraint collisions.
; Description: Verlet physics, with constraint collisions and an interactive demo

; Verlet physics demo by Jeppe Nielsen 2009
; email: nielsen_jeppe@hotmail.com
; portfolio: www.sarkania.dk/jeppe
; Converted from my xna source to blitz3d
; Partially based on this paper by Thomas Jakobsen:
; http://www.teknikus.dk/tj/gdc2001.htm
; give credit where credit is due :-)

Global debugComparisons

Graphics3D 1024,768,32,2

useSpace = True
gravityEnabled = True
renderPoints = True
useMotionBlur = True

CreateChain(100,500,GraphicsWidth()-100,500,10)

CreateBox(GraphicsWidth()/2,200,60,30,20)



draggingPoint.Point = Null
selectedPoint.Point = Null
selectedSpring.Spring = Null

; The space partitioning for much faster collision queries, space to see all tested against all
SpaceInit(0,0,1280,960,32,1000) ;<- increase the 1000, if you get array out of bounds errors

Repeat

If KeyHit(48)

	CreateBox(MouseX(),MouseY(),40,40)

EndIf

If MouseHit(1)
		
	If selectedPoint=Null
	
		selectedPoint = PointPick(MouseX(),MouseY(),10)
		If selectedPoint = Null
		
			selectedPoint = PointNew(MouseX(),MouseY(),1)
			selectedPoint\enabled = False
		
		EndIf
	
	Else
	
		otherPoint.Point = PointPick(MouseX(),MouseY(),10)
		If otherPoint= Null
		
			otherPoint = PointNew(MouseX(),MouseY(),1)
			otherPoint \enabled = False
		
		EndIf

		If selectedPoint<>otherPoint

			selectedPoint\enabled = True
			otherPoint \enabled = True
			selectedSpring = SpringNew(selectedPoint,otherPoint,16)
			
			selectedPoint = otherPoint
		
		EndIf
	
	EndIf
	
EndIf


If MouseDown(2)

	selectedSpring = SpringPick(MouseX(),MouseY(),10)
	
	selectedPoint = Null

	If draggingPoint=Null
	
		draggingPoint= PointPick(MouseX(),MouseY(),10)
	
	Else
	
		PointPosition (draggingPoint,MouseX(),MouseY())
	
	EndIf
	
Else

	draggingPoint= Null

EndIf

If (KeyHit(211) And selectedSpring<>Null)

SpringDelete selectedSpring

selectedSpring = Null

EndIf


If (KeyHit(57))

useSpace  = Not useSpace 

EndIf

If (KeyHit(25))

renderPoints = Not renderPoints 

EndIf

If (KeyHit(50))

useMotionBlur = Not useMotionBlur 

EndIf

If selectedSpring<>Null

	If KeyDown(200)
		selectedSpring\length = selectedSpring\length + 1
	ElseIf KeyDown(208)
		selectedSpring\length = selectedSpring\length - 1
	EndIf
	
	If KeyDown(203)
		selectedSpring\elasticity = selectedSpring\elasticity - 0.01
		If selectedSpring\elasticity<0.01
			selectedSpring\elasticity = 0.01
		EndIf
	ElseIf KeyDown(205)
		selectedSpring\elasticity = selectedSpring\elasticity + 0.01
		If selectedSpring\elasticity>1.5
			selectedSpring\elasticity = 1.5
		EndIf

	EndIf

EndIf


If (KeyHit(31))

	point.Point = PointPick(MouseX(),MouseY(),20)
	If point<>Null
		point\enabled = Not point\enabled
	EndIf

EndIf

If (KeyHit(34))

gravityEnabled = Not gravityEnabled 

EndIf

If gravityEnabled 
	SimulationUpdate(0.1,useSpace ,6)
Else
	SimulationUpdate(0.1,useSpace ,0)
EndIf

;SimulationRender()

SimulationRender3D(selectedSpring,renderPoints,useMotionBlur)


y=10
yh=12
Text 10,y,"Comparisons : " + debugComparisons : y=y+yh

If useSpace
	Text 10,y,"Space partitioning is used (space to change)" : y=y+yh
Else
	Text 10,y,"Brute force is used (space to change)" : y=y+yh
EndIf

Text 10,y,"B to spawn box at mouse position" : y=y+yh
Text 10,y,"Left click to create springs" : y=y+yh
Text 10,y,"Right click to drag points and select springs (selected spring is red)" : y=y+yh

If gravityEnabled 
Text 10,y, "G to toggle gravity : on" : y=y+yh
Else
Text 10,y, "G to toggle gravity : off" : y=y+yh
EndIf
Text 10,y, "S to toggle point static (blue) / dynamic (white)" : y=y+yh
Text 10,y, "Delete to delete selected spring (red)" : y=y+yh
Text 10,y, "P to toggle points rendering" : y=y+yh
Text 10,y, "M to toggle motion blur" : y=y+yh
Text 10,y, "Up/down arrow keys to extend/withdraw selected spring (red)" : y=y+yh

If selectedSpring<>Null
	Text 10,y, "Left/right to change elasticity of selected spring:"+selectedSpring\elasticity : y=y+yh
Else

	Text 10,y, "Left/right to change elasticity of selected spring:" : y=y+yh
EndIf

Flip

Until KeyDown(1)
End

Function CreateBox(x#,y#,width#,height#, mass#=1, thickness#=16, elasticity#=1.5)

p1.Point = PointNew(x-width,y-height,mass)
p1\collisionID = Handle(p1)
	
p2.Point = PointNew(x+width,y-height,mass)
p2\collisionID = p1\collisionID 

p3.Point = PointNew(x+width,y+height,mass)
p3\collisionID = p1\collisionID 

p4.Point = PointNew(x-width,y+height,mass)
p4\collisionID = p1\collisionID 

s1.Spring = SpringNew(p1,p2,thickness,elasticity)
s1\collisionID = p1\collisionID 

s2.Spring = SpringNew(p2,p3,thickness,elasticity)
s2\collisionID = p1\collisionID 

s3.Spring = SpringNew(p3,p4,thickness,elasticity)
s3\collisionID = p1\collisionID 

s4.Spring = SpringNew(p4,p1,thickness,elasticity)
s4\collisionID = p1\collisionID 

s5.Spring = SpringNew(p1,p3,thickness,elasticity)
s5\collisionID = p1\collisionID 

End Function

Function CreateChain(x1#,y1#,x2#,y2#,links = 20,mass#=1, thickness#=16, elasticity#=1.5)

thickness#=16

dx#=x2-x1
dy#=y2-y1

dx=dx/links
dy=dy/links

lastPoint.Point = Null

For i = 0 To links

x#=x1 + dx * i
y#=y1 + dy * i

p.Point = PointNew(x,y,mass)

If i = 0 Or i = links

	p\enabled = False

EndIf

If lastPoint<>Null

	SpringNew(lastPoint,p,thickness,elasticity)

EndIf

lastPoint = p

Next


End Function

Type Point
	
	Field x#,y#,xOld#,yOld#
	
	Field mass#
	
	Field otherPoint.Point
	
	Field enabled
	
	Field collisionID
	
End Type

Function PointNew.Point(x#,y#, mass# = 1)

p.Point = New Point

p\x=x
p\y=y
p\xOld = x
p\yOld = y
p\mass = mass
p\enabled = True

Return p
End Function

Function PointPick.Point(x#,y#,radius#)

radius = radius * radius

For p.Point = Each Point

dx#=p\x - x
dy#=p\y - y

If ((dx*dx+dy*dy)<radius)

	Return p

EndIf

Next

End Function

Function PointDelete(p.Point)

Delete p

End Function

Function PointPosition(p.Point,x#,y#)

p\x = x
p\y = y

If Not p\enabled

p\xOld = x
p\yOld = y

EndIf

End Function

Function PointHasSpring.Spring(p.Point)

	For s.Spring = Each Spring
		If s\a = p Or s\b = p
			Return s
		EndIf
	Next

	Return Null
End Function

Type Spring

Field a.Point
Field b.Point
Field length#
Field enabled
Field thickness#
Field tension#
Field elasticity#

Field minX#,minY#
Field maxX#,maxY#

Field collisionID
	
End Type

Function SpringNew.Spring(a.Point, b.Point, thickness#, elasticity#=1.0)

s.Spring = New Spring
s\a = a
s\b = b
s\thickness = thickness
s\enabled = True
s\tension = 0
s\elasticity = elasticity
SpringCalculateRestLength(s)

s\a\otherPoint = s\b
s\b\otherPoint = s\a

Return s
End Function

Function SpringCalculateRestLength(s.Spring)

dx#=s\b\x - s\a\x
dy#=s\b\y - s\a\y

s\length = Sqr(dx*dx+dy*dy)

End Function

Function SpringDelete(s.Spring)

pA.Point = s\a
pB.Point = s\b

Delete s

springThatHasA.Spring = PointHasSpring(pA)
springThatHasB.Spring = PointHasSpring(pB)


If springThatHasA = Null
	PointDelete pA
Else

	If pA\otherPoint = pB
		If pA<>springThatHasA\b
			pA\otherPoint = springThatHasA\b
		Else
			pA\otherPoint = springThatHasA\a
		EndIf
	EndIf
EndIf

If springThatHasB = Null
	PointDelete pB
Else

	If pB\otherPoint = pB
		If pB<>springThatHasB\a
			pB\otherPoint = springThatHasB\a
		Else
			pB\otherPoint = springThatHasB\b
		EndIf
	EndIf
EndIf



End Function

Function SpringPick.Spring(x#,y#, radius#)

radius = radius * radius

For s.Spring = Each Spring
	
	distance# = SimulationLineToCircle(s\a\x,s\a\y,s\b\x,s\b\y,x,y)
	
	If (distance<radius)
	
		Return s
	
	EndIf
	
Next

End Function

Function SpringUpdate(s.Spring)

If s\enabled

	dx#=s\b\x - s\a\x
	dy#=s\b\y - s\a\y

	realLength# = Sqr(dx*dx+dy*dy)
	
	dx=dx/realLength
	dy=dy/realLength
	
	totalMass# = s\a\mass + s\b\mass
	
	m1#=s\a\mass / totalMass
	m2#=1 - m1;s\b\mass / totalMass

	dl#=(realLength - s\length) * s\elasticity#

	s\tension = s\tension + dl
	
	If s\a\enabled
	
		s\a\x = s\a\x + dx * dl * m2
		s\a\y = s\a\y + dy * dl * m2
	
	EndIf

		
	If s\b\enabled
	
		s\b\x = s\b\x - dx * dl * m1
		s\b\y = s\b\y - dy * dl * m1
	
	EndIf

EndIf

End Function

Function SpringCalculateMinMax(s.Spring)

If s\a\x<s\b\x
	s\minX = s\a\x - s\thickness
	s\maxX = s\b\x + s\thickness
Else
	s\minX = s\b\x - s\thickness
	s\maxX = s\a\x + s\thickness
EndIf

If s\a\y<s\b\y
	s\minY = s\a\y - s\thickness
	s\maxY = s\b\y + s\thickness
Else
	s\minY = s\b\y - s\thickness
	s\maxY = s\a\y + s\thickness
EndIf

End Function

Function SimulationRender()

For s.Spring = Each Spring
	
	Line s\a\x,s\a\y,s\b\x,s\b\y

Next


For p.Point = Each Point

	Rect p\x-2,p\y-2,4,4,True

Next

End Function

Global simulationCamera, simulationPivot, simulationMesh, simulationSurface, simulationCube

Function SimulationRender3D(selected.Spring, renderPoints, useMotionBlur, lineWidth#=1, pointSize#=2)

If simulationCamera=0

simulationCamera = CreateCamera()
CameraRange simulationCamera,0.1,2

simulationPivot = CreatePivot(simulationCamera)

a#=Float(GraphicsHeight()) / GraphicsWidth()
w#=2.0/Float(GraphicsWidth())
PositionEntity simulationPivot,-1,a,1
ScaleEntity simulationPivot,w,-w,1

simulationMesh = CreateMesh(simulationPivot)

simulationSurface = CreateSurface(simulationMesh)

EntityFX simulationMesh,1+2+32

EntityColor simulationMesh,255,255,0


CameraClsColor simulationCamera,0,0,0
CameraClsMode simulationCamera,0,1

simulationCube= CreateCube(simulationCamera)
EntityColor simulationCube,0,0,0
PositionEntity simulationCube,0,0,3
ScaleEntity simulationCube,2,2,1
EntityFX simulationCube,1

EndIf

If useMotionBlur
	EntityAlpha simulationCube,0.2	
Else
	EntityAlpha simulationCube,1
EndIf


ClearSurface simulationSurface 


For s.Spring = Each Spring

dx# = s\b\x - s\a\x
dy# = s\b\y - s\a\y

l#=Sqr(dx*dx+dy*dy)

nx#=(dy/l)*lineWidth
ny#=-(dx/l)*lineWidth


v = AddVertex( simulationSurface,s\a\x+nx,s\a\y+ny,0)
AddVertex( simulationSurface,s\b\x+nx,s\b\y+ny,0)
AddVertex simulationSurface,s\b\x-nx,s\b\y-ny,0
AddVertex simulationSurface,s\a\x-nx,s\a\y-ny,0

AddTriangle simulationSurface,v,v+1,v+2
AddTriangle simulationSurface,v,v+2,v+3

If s = selected
	For i =0 To 3
		VertexColor simulationSurface,v+i,255,0,0,1
	Next
Else
	For i =0 To 3
		VertexColor simulationSurface,v+i,255,255,0,1
	Next
EndIf



Next

If renderPoints

For p.Point = Each Point

v = AddVertex( simulationSurface,p\x-pointSize,p\y-pointSize,0)
AddVertex( simulationSurface,p\x+pointSize,p\y-pointSize,0)
AddVertex simulationSurface,p\x+pointSize,p\y+pointSize,0
AddVertex simulationSurface,p\x-pointSize,p\y+pointSize,0

AddTriangle simulationSurface,v,v+1,v+2
AddTriangle simulationSurface,v,v+2,v+3

If p\enabled
	For i =0 To 3
		VertexColor simulationSurface,v+i,255,255,255,0.5
	Next
Else
	For i =0 To 3
		VertexColor simulationSurface,v+i,0,0,255,0.5
	Next
EndIf

Next

EndIf



RenderWorld

End Function





Function SimulationUpdate(dt#, useSpace, gravity# = 6, iterations = 10)

debugComparisons = 0

gravity = gravity * dt * dt

damping# = 0.85 ^ dt

For p.Point = Each Point

	If p\enabled
	
		tempX#=p\x
		tempY#=p\y
		
		p\x = p\x + (p\x - p\xOld) * damping
		p\y = p\y + (p\y - p\yOld) * damping + gravity
		
		p\xOld = tempX
		p\yOld = tempY
			
	EndIf

Next

For s.Spring = Each Spring

	s\tension = 0

Next

For i = 1 To iterations

	For s.Spring = Each Spring

		SpringUpdate(s)

	Next
	
	If useSpace
		
		SpaceClear()
		
		For s.Spring = Each Spring
		
			SpaceInsert(s)
		
		Next
	
	EndIf
	

	For p.Point = Each Point
	
		If useSpace
		
			SpaceUpdate(p)
		
		Else
	
			For s.Spring = Each Spring
					
				SimulationCheckPointAgainstSpring p,s
						
			Next
			
		EndIf
		
		
		If p\y>GraphicsHeight()
				
			p\y = GraphicsHeight()
		
		EndIf
		
		If p\x<0
		
			p\x = 0
		
		ElseIf p\x>GraphicsWidth()
		
			p\x = GraphicsWidth()
		
		EndIf


	Next


Next



End Function

Function SimulationCheckPointAgainstSpring(p.Point, s.Spring)



If (p = s\a Or p = s\b)
	
	Return		
				
EndIf

If (p\collisionID <> 0 And s\collisionID <> 0 And p\collisionID = s\collisionID)

	Return

EndIf

debugComparisons = debugComparisons + 1

Local pointVectorX#,pointVectorY#

If p\otherPoint<>Null

	If p\otherPoint = s\a Or p\otherPoint = s\b
	
		pointVectorX = s\a\x - p\x
		pointVectorY = s\a\y - p\y
		
	Else
	
		pointVectorX = s\a\x - p\otherPoint\x
		pointVectorY = s\a\y - p\otherPoint\y
	
	EndIf


Else
	
	pointVectorX = p\xOld - p\x
	pointVectorY = p\yOld - p\y

EndIf

distance# = SimulationLineToCircle(s\a\x,s\a\y,s\b\x,s\b\y,p\x,p\y)

thickSq#= s\thickness * s\thickness

If distance < thickSq

dirX# = s\b\x - s\a\x
dirY# = s\b\y - s\a\y

l#=Sqr(dirX*dirX + dirY*dirY)

dirX = dirX / l
dirY = dirY / l

normalX# = -dirY
normalY# = dirX

dot# = normalX * pointVectorX + normalY * pointVectorY

If dot>0

	normalX = -normalX
	normalY = -normalY

EndIf

dcoll# = (s\thickness - Sqr(distance)) * 0.5

ima# = 1.0 / p\mass
imb# = 1.0 / (s\a\mass + (s\b\mass - s\a\mass) * SimulationIntersectAB)

dcollMasses# = dcoll / (ima + imb)

separationX# = normalX * dcollMasses
separationY# = normalY * dcollMasses 

i1# = SimulationIntersectAB
i2# = 1 - SimulationIntersectAB

i1i2Inv# = 1.0 / (i1*i1+i2*i2)

seX# = separationX * imb * i1i2Inv
seY# = separationY * imb * i1i2Inv

vel1X# = p\x - p\xOld
vel1Y# = p\y - p\yOld

velAX# = s\a\x - s\a\xOld
velAY# = s\a\y - s\a\yOld

velBX# = s\b\x - s\b\xOld
velBY# = s\b\y - s\b\yOld

vel2X# = velAX + (velBX - velAX) * SimulationIntersectAB
vel2Y# = velAY + (velBY - velAY) * SimulationIntersectAB

vcollX# = vel2X - vel1X
vcollY# = vel2Y - vel1Y

dirX = normalY
dirY = -normalX

friction# = 0.2

dot = vcollX * dirX + vcollY * dirY

massInv# = 1.0 / (ima + imb)

impulseX# = dirX * dot * friction * massInv
impulseY# = dirY * dot * friction * massInv

If p\enabled

	p\x = p\x + separationX * ima
	p\y = p\y + separationY * ima
	
	p\xOld = p\xOld - impulseX * ima 
	p\yOld = p\yOld - impulseY * ima

EndIf


dImX# = impulseX * imb * i1i2Inv
dImY# = impulseY * imb * i1i2Inv

If s\a\enabled

	s\a\x = s\a\x - seX * i2
	s\a\y = s\a\y - seY * i2
	
	s\a\xOld = s\a\xOld + dImX * i2
	s\a\yOld = s\a\yOld + dImY * i2

EndIf

If s\b\enabled

	s\b\x = s\b\x - seX * i1
	s\b\y = s\b\y - seY * i1
	
	s\b\xOld = s\b\xOld + dImX * i1
	s\b\yOld = s\b\yOld + dImY * i1

EndIf

EndIf

End Function

Global SimulationIntersectAB#

Function SimulationLineToCircle#(x1#,y1#,x2#,y2#,x#,y#)

	dx#=x2-x1
	dy#=y2-y1

	SimulationIntersectAB = ((x-x1) * dx + (y-y1) * dy) / (dx*dx+dy*dy)

	If SimulationIntersectAB < 0 
		SimulationIntersectAB = 0
	ElseIf SimulationIntersectAB > 1
		SimulationIntersectAB = 1
	EndIf

	cx#=x1 + dx * SimulationIntersectAB 
	cy#=y1 + dy * SimulationIntersectAB 
		
	tx# = x - cx
	ty# = y - cy
	
	Return tx*tx + ty*ty
End Function

Dim SpaceCell.Spring(0,0,0)
Dim SpaceCellCount(0,0)

Global SpaceMinX#,SpaceMinY#,SpaceMaxX#,SpaceMaxY#
Global SpaceCells,SpaceMulX#, SpaceMulY#

Function SpaceInit(minX#,minY#,maxX#,maxY#,cells,maxInEachCell)

SpaceMinX = minX
SpaceMinY = minY

SpaceMaxX = maxX
SpaceMaxY = maxY

SpaceCells = cells


Dim SpaceCell(SpaceCells,SpaceCells, maxInEachCell)
Dim SpaceCellCount(SpaceCells,SpaceCells)

SpaceMulX = SpaceCells / (SpaceMaxX - SpaceMinX)
SpaceMulY = SpaceCells / (SpaceMaxY - SpaceMinY)

End Function


Function SpaceClear()

For y=0 To SpaceCells

	For x=0 To SpaceCells

		SpaceCellCount(x,y) = 0

	Next

Next

End Function

Function SpaceInsert(s.Spring)

SpringCalculateMinMax(s)

minX = Floor((s\minX -  SpaceMinX) * SpaceMulX)
minY = Floor((s\minY -  SpaceMinY) * SpaceMulY)

maxX = Floor((s\maxX -  SpaceMinX) * SpaceMulX)
maxY = Floor((s\maxY -  SpaceMinY) * SpaceMulY)

If (minX<0)
	minX = 0
Else If (minX>=SpaceCells)
	minX = SpaceCells
EndIf

If (minY<0)
	minY = 0
Else If (minY>=SpaceCells)
	minY = SpaceCells
EndIf


If (maxX<0)
	maxX= 0
Else If (maxX>=SpaceCells)
	maxX= SpaceCells
EndIf

If (maxY<0)
	maxY= 0
Else If (maxY>=SpaceCells)
	maxY= SpaceCells
EndIf



For y=minY To maxY
	For x=minX To maxX
	
		
		SpaceCell(x,y,SpaceCellCount(x,y)) = s
		
		SpaceCellCount(x,y) = SpaceCellCount(x,y)+1
		
	Next
Next
End Function


Function SpaceUpdate(p.Point)

x = Floor((p\x -  SpaceMinX) * SpaceMulX)
y = Floor((p\y -  SpaceMinY) * SpaceMulY)



If (x<0)
	x= 0
Else If (x>=SpaceCells)
	x= SpaceCells
EndIf

If (y<0)
	y= 0
Else If (y>=SpaceCells)
	y= SpaceCells
EndIf


count = SpaceCellCount(x,y)-1


For n=0 To count
	SimulationCheckPointAgainstSpring(p,SpaceCell(x,y,n))

Next

End Function
