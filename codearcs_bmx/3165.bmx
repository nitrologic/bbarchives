; ID: 3165
; Author: GW
; Date: 2014-12-07 18:06:16
; Title: K-Means Clustering example
; Description: Implements the K-Means clustering algo in 2D

SuperStrict
Framework brl.retro
Import brl.glmax2d

Rem
	K-Means Clustering algo for 2d by Aaron Woodard 2014 admin at kerneltrick.com
	Hold down the space bar to demo
Endrem

SeedRnd MilliSecs()


Const NUMCLUSTERS:Int = 5
Const GW:Int = 800
Const GH:Int = 600

Global pointslist:TList = CreateList()
Global Centers:tPoint[NUMCLUSTERS]
Global Colors:Int[NUMCLUSTERS]



'---------------------------------------------------------------------------------------------------------------------------------
Type tPoint
	Field x:Int
	Field y:Int
	Field class:Int
	
	Function Create:tPoint(x:Int, y:Int, class:Int = 0, add:Int = True)
		Local p:tPoint = New tPoint
		p.x = x
		p.y = y
		p.class = class
		If add Then pointslist.AddLast p
		Return p
	End Function
End Type
'---------------------------------------------------------------------------------------------------------------------------------


'---------------------------------------------------------------------------------------------------------------------------------
Function Init()
	'// Create the cluster centers and give them a random color
		For Local I:Int = 0 Until NUMCLUSTERS
			Centers[I] = tPoint.Create(Rand(1, GW - 1) , Rand(1, GH - 1), 0, False)
			Colors[I] = Rand($FF000000, $FFFFFFFF)
		Next
End Function
'---------------------------------------------------------------------------------------------------------------------------------
Function AddPoint(p:tPoint)
		Rem
			1) Find the closest cluster center for the new point
			2) Add the new point to that group
			3) update to chosen cluster center to be the average of all it's members 
		Endrem
		
		Local d:Float
		Local bestd:Float = 9999999		'// Best distance 
		Local bestc:Float				'// best cluster matched
		
		'// Find Closest center to this new point  //
		For Local i:Int = 0 Until NUMCLUSTERS
			d = dist(Centers[i].x, Centers[i].y, p.x, p.y)
			If d < bestd Then
				bestd = d
				bestc = I
			End If
		Next
		
		p.class = bestc	'// assign the new point to the closest cluster
		
		'// Adjust the center of this cluster to account for the new point //
		Local totX:Int
		Local totY:Int
		Local count:Int
		For Local tp:tPoint = EachIn pointslist
			If tp.class <> bestc Then Continue
			totX:+tp.x
			totY:+tp.y
			count:+1
		Next
		
		If count < 1 Then Return
		
		Centers[bestc].x = totX / count
		Centers[bestc].y = totY / count
End Function
'---------------------------------------------------------------------------------------------------------------------------------
Function DrawPoints()
	Local colr:Int
	For Local tp:tPoint = EachIn pointslist
		colr = Colors[tp.class]
		SetColor((colr Shr 16) & $FF, (colr Shr 8) & $FF, (colr) & $FF)
		DrawOval tp.x, tp.y, 5, 5
	Next
End Function
'---------------------------------------------------------------------------------------------------------------------------------
Function DrawCenters()
	For Local i:Int = 0 Until NUMCLUSTERS
		SetColor((Colors[I] Shr 16) & $FF, (Colors[I] Shr 8) & $FF, (Colors[I]) & $FF)
		DrawRect(Centers[i].x, Centers[i].y, 10, 10)
	Next
End Function
'---------------------------------------------------------------------------------------------------------------------------------



'//BEGIN 
Graphics GW, GH
SetClsColor 32, 32, 32
Init




While Not KeyHit(KEY_ESCAPE)
	Cls
	
		If KeyDown(KEY_SPACE) Then
			Local p:tPoint = tPoint.Create(Rand(GW - 2) + 1, Rand(GH - 2) + 1, 0, True)
			
			AddPoint p
			
			'// Pull an old point off the list and re-apply it, clusters may have shifted and the old points might have changed class //
			Local p2:tPoint = tPoint(pointslist.RemoveFirst())
			AddPoint p2
			pointslist.AddLast p2
			
			Print pointslist.Count()
			'Delay 20
		EndIf
		
		DrawCenters
		DrawPoints
	
	Flip
Wend



'//Utility stuff
'---------------------------------------------------------------------------------------------------------------------------------
Function dist:Float(x1:Float, y1:Float, x2:Float, y2:Float)
	Local dx:Float = x2 - x1
	Local dy:Float = y2 - y1
	Return Sqr(dx * dx + dy * dy)
End Function
'---------------------------------------------------------------------------------------------------------------------------------
