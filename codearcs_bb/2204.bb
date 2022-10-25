; ID: 2204
; Author: Nebula
; Date: 2008-01-20 18:29:52
; Title: 3D city and drive flying
; Description: Drive fly thru a city like 3d map

;
;smooth...
;
Graphics3D 640,480,16,2
SetBuffer BackBuffer()

Global cam = CreateCamera()
PositionEntity cam,10,.5,-7

Dim asteroid(50)

Type missile
	Field ent
	Field x#,y#,z#
	Field v#
	Field graad#
	Field graad2#
End Type

cnt=0
Function asteroidfield()
	For x = 0 To 50
	For z = 0 To 50
		;
		If Rand(77) = 1
			asteroid(cnt) = CreateCube()
			PositionEntity asteroid(cnt) ,x*3 ,Rand(10,50) ,z*3
			cnt = cnt + 1
		End If
		;
	Next : Next
End Function

asteroidfield

cnt = Rand(123)


For x = 0 To 10 : For z = 0 To 10
	;
	i = 1
	;
	For ax = 0 To 4:For az = 0 To 4
		;
		a = Int(Mid(Right(Bin(cnt),9),i,1))
		i = i + 1:cnt=cnt+1
		;
		Select a
			Case 1 : 	mesh1 = makecube(1,0,1,1,1,1)
						EntityColor mesh1,95,109,110
						PositionMesh mesh1,x*14+(ax*3),0,z*14+(az*3)			
						ScaleEntity mesh1,1,Rnd(3),1
						mesh2 = makecube(1, 0, 1, 1, 1, 1)
						EntityColor mesh2,130, 139, 139
						ScaleEntity mesh2,1.3, .1, 1.3
						PositionMesh mesh2,x * 12 + (ax * 3 / 1.3), 0, z * 12 + (az * 3 / 1.3)
		End Select
	Next:Next
Next:Next

Local w#
allm=False

While KeyDown(1) = False
	;
	RenderWorld()
	;
	If KeyDown(32) = True  MoveEntity cam,0,-.1,0 ; down
	If KeyDown(18) = True  MoveEntity cam,0,.1,0  ; up
	
	If KeyDown(200) = True Then v# = v# + .01  ; voren
	If v#>.05
	If KeyDown(208) = True Then v# = v# - .02     ; achteren
	Else
	If KeyHit(208) = True Then v# = v# -.03     ; achteren

	End If
	If KeyDown(203) = True Then w# = w# + 1	   ; links
	If KeyDown(205) = True Then w# = w# - 1	   ; rects
	;
	MoveEntity cam,0,0,v
	RotateEntity cam,0,w,0	
	;
	Text 0,0,TrisRendered()
	Flip
	;
	If KeyHit(57) = True Then
		m.missile = New missile
		m\x = EntityX(cam)
		m\y = EntityY(cam)
		m\z = EntityZ(cam)
		m\ent = CreateCube()
		PositionEntity m\ent,EntityX(cam),EntityY(cam),EntityZ(cam)
		m\v = v
		allm = True
	End If
	If allm = True Then
		m\x = m\x + (Cos( m\graad ) *3)
		m\y = m\y + (Sin( m\graad ) *3)
		m\v = m\v + .01
		m\graad = w
		MoveEntity m\ent,Cos( m\graad ),0,Sin( m\graad )
		;If Rand(20) = 1 Then m\graad = volledigehoek(m\x,m\z,EntityX(asteroid(20)),EntityZ(asteroid(20)))
		;
	End If
	;
	;
Wend
End

Function makecube(a=True,b=True,c=True,d=True,e=True,f=True)
	z=CreateMesh() 

	surf=CreateSurface(z) 

	v0 = AddVertex(surf,0,0,0,	0,1)
	v3 = AddVertex(surf,4,0,0,	1,1)	
	v4 = AddVertex(surf,0,4,0,	0,0)
	v7 = AddVertex(surf,4,4,0,	1,0)

	v1 = AddVertex(surf,0,0,4	,0,1)
	v2 = AddVertex(surf,4,0,4	,1,1)	
	v5 = AddVertex(surf,0,4,4	,0,0)
	v6 = AddVertex(surf,4,4,4	,1,0)

	v8  = AddVertex(surf,0,0,0	,0,1)
	v9  = AddVertex(surf,0,0,4	,1,1)	
	v10 = AddVertex(surf,0,4,0	,0,0)
	v11 = AddVertex(surf,0,4,4	,1,0)

	v12 = AddVertex(surf,4,0,0  ,0,1)
	v13 = AddVertex(surf,4,0,4  ,1,1)	
	v14 = AddVertex(surf,4,4,0	,0,0)
	v15 = AddVertex(surf,4,4,4	,1,0)

	v16 = AddVertex(surf,0,4,0  ,0,1)
	v17 = AddVertex(surf,0,4,4	,1,1)	
	v18 = AddVertex(surf,4,4,0	,0,0)
	v19 = AddVertex(surf,4,4,4	,1,0)

	v20 = AddVertex(surf,0,0,0  ,0,1)
	v21 = AddVertex(surf,0,0,4	,1,1)	
	v22 = AddVertex(surf,4,0,0	,0,0)
	v23 = AddVertex(surf,4,0,4	,1,0)

	If a = True
		AddTriangle(Surf,v16,v17,v18) ;top
		AddTriangle(surf,v18,v17,v19)
	End If
	If b = True
		AddTriangle(surf,v21,v20,v22) ; bottom
		AddTriangle(surf,v21,v22,v23)
	End If
	If e = True Then
		AddTriangle(surf,v5,v1,v2) ; front
		AddTriangle(surf,v5,v2,v6)
	End If
	If f = True Then
		AddTriangle(surf,v0,v4,v3) ; back
		AddTriangle(surf,v3,v4,v7) ; 
	End If
	If c = True
		AddTriangle(surf,v15,v13,v12) ; left
		AddTriangle(surf,v15,v12,v14)
	End If
	If d = True
		AddTriangle(surf,v9,v11,v8) ; right
		AddTriangle(surf,v8,v11,v10)
	End If
	
	Return z
	
End Function

Function volledigehoek#(x1,y1,x2,y2) ; x2 y2 naar
	Local hoek = 0
	Local laagste = 1024
	For i=0 To 360
		x3 = x1+Cos(hoek) * 211
		y3 = y1+Sin(hoek) * 211
		hoek = hoek + 1
		nieuw = Sqr((x3-x2)^2+(y3-y2)^2)
		If nieuw < laagste Then laagste = nieuw : uitgraad = i
	Next
	Return uitgraad
End Function
