; ID: 2130
; Author: SebHoll
; Date: 2007-10-27 17:26:31
; Title: GetIntersectionLineCircle()
; Description: Get Points of Intersection for Line and Circle

SuperStrict

Local tmpIntersectLineCircle#[][]
Local mx:Float, my: Float, mode:Int = 2

Graphics 640, 480, 0

While Not (KeyHit(key_escape) Or AppTerminate())
	
	Cls
		
		SetColor(50, 200, 100);DrawCircle(320, 240, 40)
		mx = MouseX();my = MouseY()
		
		SetColor(50, 70, 222);DrawLine(0,0, mx, my)
		
		SetColor(255,50,30)
		DrawText "No. of Collisions: " + tmpIntersectLineCircle.length, (GraphicsWidth()-TextWidth("No. of Collisions:  "))/2, 20
		
		If (mode=1) Then
			DrawText("Using GetIntersectionLineCircle()",(GraphicsWidth()-TextWidth("Using GetIntersectionLineCircle()"))/2,5)
			tmpIntersectLineCircle = GetIntersectionLineCircle( [0.0,0.0], [mx, my], [320.0, 240.0], 40.0 )
		ElseIf (mode=2) Then
			DrawText("Using GetIntersectionLineCircle2()",(GraphicsWidth()-TextWidth("Using GetIntersectionLineCircle2()"))/2,5)
			tmpIntersectLineCircle = GetIntersectionLineCircle2( [0.0,0.0], [mx, my], [320.0, 240.0], 40.0 )
		EndIf
		
		SetColor(255, 0,0)
		
		For Local tmpIntersectionPoint#[] = EachIn tmpIntersectLineCircle	
			DrawCircle(tmpIntersectionPoint[0], tmpIntersectionPoint[1], 4)
		Next
		
		SetColor(255,255,255)
		DrawText("[F1] Use GetIntersectionLineCircle()", GraphicsWidth()-TextWidth("[F1] Use GetIntersectionLineCircle()"),GraphicsHeight()-(TextHeight("A")*2))
		DrawText("[F2] Use GetIntersectionLineCircle2()", GraphicsWidth()-TextWidth("[F2] Use GetIntersectionLineCircle2()"),GraphicsHeight()-TextHeight("A"))
		
		If KeyHit(KEY_F1) Then mode = 1
		If KeyHit(KEY_F2) Then mode = 2
		
	Flip 1
	
Wend

Function GetIntersectionLineCircle#[][]( pLinePoint1#[], pLinePoint2#[], pCircleCenter#[], pCircleRadius# )

	Local tmpIntersections#[][]
	
	Local p# = pCircleCenter[0], q# = pCircleCenter[1]
	Local m# = (pLinePoint2[1]-pLinePoint1[1])/(pLinePoint2[0]-pLinePoint1[0])
	Local r# = pCircleRadius
	Local t# = pLinePoint2[1]- (m*pLinePoint2[0])
	Local s# = t-q
	
	Local a# = m*m + 1, b# = (2*m*s) - (2*p), c# = (s*s) + (p*p) - (r*r)
	
	Local bsqminfourac# = b*b-4*a*c
	
	If bsqminfourac > 0 Then
		
		bsqminfourac = Sqr(bsqminfourac)
		
		Local x1# = ((-b)+bsqminfourac)/(2*a)
		Local x2# = ((-b)-bsqminfourac)/(2*a)
		
		tmpIntersections = [[x1,(m*x1)+t],[x2,(m*x2)+t]]
		
	ElseIf bsqminfourac = 0 Then
		
		tmpIntersections = [[(-b)/(2*a),(-b*m)/(2*a)+t]]
		
	EndIf
	
	Return tmpIntersections

EndFunction

Function GetIntersectionLineCircle2#[][]( pLineStart#[], pLineEnd#[], pCircleCenter#[], pCircleRadius# )
	
	Local tmpResult#[][]
	Local tmpIntersectLineCircle#[][] = GetIntersectionLineCircle( pLineStart, pLineEnd, pCircleCenter, pCircleRadius )
	
	Local minX# = Min(pLineStart[0],pLineEnd[0]), maxX# = Max(pLineStart[0],pLineEnd[0])
	Local minY# = Min(pLineStart[1],pLineEnd[1]), maxY# = Max(pLineStart[1],pLineEnd[1])
	
	For Local tmpIntersectionPoint#[] = EachIn tmpIntersectLineCircle
		
		If tmpIntersectionPoint[0] < maxX And tmpIntersectionPoint[0] > minX And ..
			tmpIntersectionPoint[1] < maxY And tmpIntersectionPoint[1] > minY Then
			
			tmpResult = tmpResult + [tmpIntersectionPoint]
			
		EndIf
	
	Next
	
	Return tmpResult
	
EndFunction

Function DrawCircle(xCentre:Float, yCentre:Float, Radius:Float) 
	DrawOval(xCentre - (Radius), yCentre - (Radius), Radius * 2, Radius * 2) 
EndFunction
