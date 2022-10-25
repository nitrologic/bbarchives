; ID: 2335
; Author: Nate the Great
; Date: 2008-10-16 22:38:41
; Title: RIPPLE EFFECT
; Description: ripples pictures

Graphics3D 1440,900,0,1

ShowPointer()

tex = LoadTexture("desktoppic1.bmp") ;PUT YOUR PICTURE HERE

Type ripples
	Field x#,y#,dist#
End Type


cam = CreateCamera()
TurnEntity cam,90,0,0
MoveEntity cam,0,0,-2.9
CameraZoom cam, 5


lit = CreateLight()
TurnEntity lit,90,0,0;135,0,0

square = CreateMesh()

EntityPickMode square,2

ripsurf = CreateSurface(square)

numverts = 50

Dim verts(numverts,numverts)

For x# = 0 To numverts
	For y# = 0 To numverts
		verts(x,y) = AddVertex(ripsurf,x#/numverts-.5,0,y#/numverts-.5,x#/numverts,-y#/numverts+1)
	Next
Next

For y = 0 To numverts-1
	For x = 0 To numverts-1
		AddTriangle(ripsurf,verts(x,y),verts(x,y+1),verts(x+1,y))
		AddTriangle(ripsurf,verts(x+1,y+1),verts(x+1,y),verts(x,y+1))
	Next
Next



; We have a one-sided square with no normals defined. Now apply quick and dirty fix.



;EntityColor square,100,0,0

;EntityShininess square,1
;EntityAlpha square,.5

;VertexCoords(ripsurf,843,VertexX(ripsurf,843),.03,VertexZ(ripsurf,843))

TurnEntity square,0,0,0

EntityTexture square,tex


While Not KeyDown(1)
	;TurnEntity square,1,0,0
	
	If MouseHit(1) Then
		CameraPick cam,MouseX(),MouseY()
		
		r.ripples = New ripples
		r\x# = PickedX()
		r\y# = PickedZ()
		r\dist# = .0001
	EndIf
	
	
	For x = 0 To numverts
		For y = 0 To numverts
			VertexCoords(ripsurf,verts(x,y),VertexX( ripsurf,verts(x,y)) , 0, VertexZ( ripsurf,verts(x,y)))
		Next
	Next
	
	For r.ripples = Each ripples
		
		For x = 0 To numverts
			For y = 0 To numverts
				
				dist# = Sqr(      (   VertexX( ripsurf,verts(x,y) )   -   r\x#   )^2                +                 (   VertexZ( ripsurf,verts(x,y) ) - r\y#)^2 )
				
				dif# = Abs(r\dist#-dist#)
				
				If dif# < .04 Then
					num = x*40 + y
					VertexCoords(ripsurf,verts(x,y),VertexX( ripsurf,verts(x,y)) ,.04-dif#, VertexZ( ripsurf,verts(x,y)))
				EndIf
				
			Next
		Next
		
		r\dist# = r\dist# + .005
		If r\dist# => 1.5 Then
			Delete r.ripples
		EndIf
	Next
	
	
	UpdateNormals square

	UpdateWorld()
	RenderWorld
	
	Oval MouseX()-5,MouseY()-5,10,10
	Flip
wend
