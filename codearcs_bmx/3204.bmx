; ID: 3204
; Author: AdamStrange
; Date: 2015-05-08 05:56:36
; Title: 2d lightning
; Description: draws lighting

SuperStrict



Type TLightning
	Const MIN_SEG_LENGTH:Int = 20			'Minimum segment length
	Const MIN_SEG_BRANCH_LENGTH:Int = 5		'Minimum segment length on branches
	Const BRANCH_ANGLE_MOD:Int = 40			'Max angle difference between mainlightning and branches
	
	Field glowred:Int = 100
	Field glowgreen:Int = 100
	Field glowblue:Int = 255
	
	Field segments:TList = CreateList()		'List of segments of the mainlightning
	Field branches:TList = CreateList()		'List of branches
	Field branches2:TList = CreateList()	'Branches of the segmentized branches
	Field generations:Int = 4				'Iterations of seperation of the mainlightning
	Field offset:Int = 10					'Segments offset
	
	
	Rem
	bbdoc:Creates a new lightning from x1, y1 to x2, y2.
	Public Function
	End Rem
	Function getInstance:TLightning(x1:Int, y1:Int, x2:Int, y2:Int)
		Local instance:TLightning = New TLightning
		instance.setStartEndPoint(x1, y1, x2, y2)
		
		Return instance
	End Function
	
	
	Rem
	bbdoc:Changes the color of the lightning
	Public Function
	End Rem
	Method SetGlowColor(r:Int, g:Int, b:Int)
		glowred = r
		glowgreen = g
		glowblue = b
	End Method


	Rem
	bbdoc:Sets new start- and endpoint coordinates.
	Public Method
	End Rem
	Method setStartEndPoint(x1:Int, y1:Int, x2:Int, y2:Int)
		'Create segment from x1,y1 to x2,y2
		Local startpoint:TPoint = TPoint.getInstance(x1, y1)
		Local endpoint:TPoint = TPoint.getInstance(x2, y2)
		Local firstSeg:TSegment = TSegment.getInstance(startpoint, endpoint)
		
		'Remove old lightning
		Self.segments.Clear()
		Self.branches.Clear()
		Self.branches2.Clear()
		
		'Create the lightning out of the first segment
		Self.segments.AddFirst(firstSeg)
		Self.segmentize(Self.segments, Self.generations, Self.offset)
		Self.branches = Self.createBranches(Self.segments)
		Self.segmentize(Self.branches, Self.generations - 1, Self.offset - 7, True)
		Self.branches2 = Self.createBranches(Self.branches)
		Self.segmentize(Self.branches2, Self.generations - 3, Self.offset - 7, True)
	End Method
	
	
	Rem
	bbdoc:Draws the lightning (Scale must be set to 1.0/1.0)
	Public Method
	End Rem
	Method draw()
		SetBlend(ALPHABLEND)
	
		'Branch2 Glow
		Self.setDrawings(0.4, 3, True)
		Self.drawSegments(Self.branches2)
		'Branches2
		Self.setDrawings(0.5, 1)
		Self.drawSegments(Self.branches2)
		
		'Branch Glow
		Self.setDrawings(0.5, 5, True)
		Self.drawSegments(Self.branches)
		'Branches
		Self.setDrawings(0.6, 1)
		Self.drawSegments(Self.branches)
		
		'Glow Paths
		Self.setDrawings(0.09, 20, True)
		Self.drawSegments(Self.segments)
		Self.setDrawings(0.25, 11, True)
		Self.drawSegments(Self.segments)
		Self.setDrawings(0.55, 6, True)
		Self.drawSegments(Self.segments)
		'Original Paths
		SetColor(255, 255, 255)
		SetAlpha(0.45)
		Local counter:Int = 0
		For Local seg:TSegment = EachIn Self.segments
			counter:+1
			Local thickness:Float = 0.3 * Float(Self.segments.Count() - counter)
			If thickness < 2.0 Then thickness = 2.0
			SetLineWidth(thickness)
			DrawLine(seg.startpoint.x, seg.startpoint.y, seg.endpoint.x, seg.endpoint.y)
		Next
	End Method
	
	
	Rem
	bbdoc:Private Method (don__COMMENT18__
	Draws a list of segments.
	End Rem
	Method drawSegments(givenList:TList)
		Local realLineWidth:Float = GetLineWidth()
		For Local seg:TSegment = EachIn givenList
			Local lineWidth:Float = realLineWidth/2
			
			Local ps1point:TPoint
			Local ps2point:TPoint
			Local pe1point:TPoint
			Local pe2point:TPoint
			Local lastSeg:TSegment = seg.lastSegment
			Local nextSeg:TSegment = seg.nextSegment
			
			Local tvec:TPoint = seg.endpoint.sub(seg.startpoint)
			Local tvecn:TPoint = tvec.normalize()
			Local tnorm:TPoint = tvec.normalize().normal()
			
			If (lastSeg=Null) Then
				ps1point = seg.startpoint.add(tnorm.mult(lineWidth))
				ps2point = seg.startpoint.add(tnorm.mult(-lineWidth))
			Else
				Local lvec:TPoint = lastSeg.endpoint.sub(lastSeg.startpoint)
				Local lnorm:TPoint = lvec.normalize().normal()
				
				ps1point = seg.startpoint.add(lnorm.mult(lineWidth))
				ps2point = seg.startpoint.add(lnorm.mult(-lineWidth))
			EndIf
			
			If (nextSeg=Null) Then
				lineWidth = lineWidth/2
			EndIf
			
			pe1point = seg.endpoint.add(tnorm.mult(-lineWidth))
			pe2point = seg.endpoint.add(tnorm.mult(lineWidth))
			
			
			Local corners:Float[] = [ps1point.x,ps1point.y,ps2point.x,ps2point.y,pe1point.x,pe1point.y,pe2point.x,pe2point.y]
			
			DrawPoly(corners)
		Next
	End Method
	
	
	Rem
	bbdoc:Private Method (don__COMMENT19__
	Sets the color to "Glow-Color"
	End Rem
	Method setDrawings(alpha:Float, lineThickness:Float, isGlowing:Byte = False)
		SetColor(255, 255, 255)
		If isGlowing Then SetColor(glowred, glowgreen, glowblue)
		SetAlpha(alpha)
		SetLineWidth(lineThickness)
	End Method
	
	
	Rem
	bbdoc:Private Method (don__COMMENT20__
	Splits a line (segment) into two lines, which are moved by maxOffset at one point (start or endpoint of the line).
	End Rem
	Method segmentize(givenList:TList, generations:Int, offset:Int, isBranch:Byte = False)
		'Preconditions
		If givenList.IsEmpty() Then Return
		Local minLength:Int = TLightning.MIN_SEG_LENGTH
		If isBranch Then minLength = TLightning.MIN_SEG_BRANCH_LENGTH
		
		'Start segmentation
		For Local i:Int = 1 To generations
			Local workList:TList = givenList.Copy()
			For Local seg:TSegment = EachIn workList
			
					'Check if the segments length allows a split into 2 segments
					If TDistance.pointToPoint(seg.startpoint.x, seg.startpoint.y, seg.endpoint.x, seg.endpoint.y) >= minLength Then
						'The original segment is obsolete, as we replace it with 2 new ones
					Local lastSegment:TSegment = seg.lastSegment
					Local nextSegment:TSegment = seg.nextSegment
		    			givenList.Remove(seg)
			
			    		Local midpoint:TPoint = seg.getMidPoint()
						Local randomAngle:Int = 90
						If Rand(1, 2) = 1 Then randomAngle = -90
						'Move the midpoint
						midpoint.x:+(Cos(seg.angle + randomAngle) * offset)
						midpoint.y:+(Sin(seg.angle + randomAngle) * offset)
			
						'Add the new segments
						Local seg1:TSegment = TSegment.getInstance(seg.startpoint, midpoint)
						Local seg2:TSegment = TSegment.getInstance(midpoint, seg.endpoint)
						seg1.lastSegment = lastSegment
						seg1.nextSegment = seg2
						seg2.lastSegment = seg1
						seg2.nextSegment = nextSegment
						
						If (lastSegment<>Null) Then lastSegment.nextSegment = seg1
						If (nextSegment<>Null) Then nextSegment.lastSegment = seg2
						
						givenList.AddLast(seg1)
						givenList.AddLast(seg2)
					End If
				Next
			Next
	End Method
	
	
	Rem
	bbdoc:Private Method (don__COMMENT27__
	Creates a branch at every segments startpoint.
	End Rem
	Method createBranches:TList(givenList:TList)
		'Preconditions
		If givenList.IsEmpty() Then Return Null
	
		Local returnList:TList = New TList
		Local i:Int = 0
	
		For Local seg:TSegment = EachIn givenList
			i:+1
			Local branchAngle:Int = seg.angle + Rand(-TLightning.BRANCH_ANGLE_MOD, TLightning.BRANCH_ANGLE_MOD)
			Local branchLength:Int = Float((Rnd(1.0, 3.0) * TDistance.pointToPoint(seg.startpoint.x, seg.startpoint.y, seg.endpoint.x, seg.endpoint.y))) * Float((Float(i) / Float(givenList.Count())))
			Local branchX:Int = seg.endpoint.x + Cos(branchAngle) * branchLength
			Local branchY:Int = seg.endpoint.y + Sin(branchAngle) * branchLength
			Local branchpoint:Tpoint = TPoint.getInstance(branchX, branchY)
			
			Local branch:TSegment = TSegment.getInstance(seg.endpoint, branchpoint)
			
			returnList.AddLast(branch)
		Next
		Return returnList
	End Method
	
End Type



Rem
bbdoc:Defines a linesegment between two points in 2D space.
End Rem
Type TSegment
	Field startpoint:TPoint
	Field endpoint:TPoint
	Field angle:Int
	Field lastSegment:TSegment
	Field nextSegment:TSegment
	
	
	Rem
	bbdoc:Constructor.
	End Rem
	Function getInstance:TSegment(startpoint:TPoint, endpoint:TPoint)
		Local instance:TSegment = New TSegment
		instance.startpoint = startpoint
		instance.endpoint = endpoint
		instance.angle = ATan2((endpoint.y - startpoint.y), (endpoint.x - startpoint.x))
		Return instance
	End Function
	
	
	Rem
	bbdoc:Returns a point on the mid of the segment.
	End Rem
	Method getMidPoint:TPoint()
		Local midpoint:Tpoint = New TPoint
		midpoint.x = (startpoint.x + endpoint.x)/2
		midpoint.y = (startpoint.y + endpoint.y)/2
		Return midpoint
	End Method
End Type



Rem
bbdoc:2D Point helper class.
End Rem
Type TPoint
	Field x:Float
	Field y:Float
	
	
	Rem
	bbdoc:Constructor.
	End Rem
	Function getInstance:TPoint(x:Float, y:Float)
		Local instance:TPoint = New TPoint
		instance.x = x
		instance.y = y
		Return instance
	End Function
	
	Rem
	bbdoc:Addiert zwei TPoints zusammen (Vektorrechnung)
	End Rem
	Method add:TPoint(other:TPoint)
		Return getInstance(x+other.x,y+other.y)
	End Method
	
	Rem
	bbdoc:Subtrahiert zwei TPoints (Vektorrechnung)
	End Rem
	Method sub:TPoint(other:TPoint)
		Return getInstance(x-other.x,y-other.y)
	End Method
	
	Rem
	bbdoc:Multipliziert einen TPoint mit einem Skalar (Vektorrechnung)
	End Rem
	Method mult:TPoint(f:Float)
		Return getInstance(x*f,y*f)
	End Method
	
	Rem
	bbdoc:Liefert die Laenge des Vektors (Vektorrechnung)
	End Rem
	Method length:Float()
		Return Sqr(x*x+y*y)
	End Method
	
	Rem
	bbdoc:Normalisiert den Vektor (Vektorrechnung)
	End Rem
	Method normalize:TPoint()
		Return mult(1.0/length())
	End Method
	
	Rem
	bbdoc:Erstellt einen Normalvektor (Vektorrechnung)
	End Rem
	Method normal:TPoint()
		Return getInstance(y,-x)
	End Method
	
	Rem
	bbdoc:Liefert das Skalarprodukt zweier Vektoren (Vektorrechnung)
	End Rem
	Method dot:Float(other:TPoint)
		Return (x*other.x+y*other.y)
	End Method
End Type



Rem
bbdoc:Helper tool to calculate the distance between 2 points in a 2D environment.
End Rem
Type TDistance Abstract
	
	Rem
	bbdoc:Berechnet die Distanz zwischen 2 Punkten.
	End Rem
	Function pointToPoint:Double(x1:Double, y1:Double, x2:Double, y2:Double)
		Return Sqr(TDistance.quad(x2 - x1) + TDistance.quad(y2 - y1))
	End Function
	
	
	Rem
	bbdoc:Quadriert einen Integer.
	End Rem
	Function quad:Double(v:Double)
		Return (v * v)
	End Function
	
End Type



'Test
'Include "Lightning.bmx"

Graphics 1024, 768
Global blitz:TLightning = TLightning.getInstance(100, 100, 500, 500)
blitz.setGlowColor(255,0,0)
Global oldMausX:Int
Global oldMausY:Int
SetClsColor(10, 10, 10)

Repeat
	Cls
	
	If MouseX() <> oldMausX Or MouseY() <> oldMausY Then
		oldMausX = MouseX()
		oldMausY = MouseY()
		blitz.setStartEndPoint(100, 100, MouseX(), MouseY())
	End If
	
	blitz.draw()
	
	Flip
Until AppTerminate()
