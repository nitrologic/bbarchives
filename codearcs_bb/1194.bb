; ID: 1194
; Author: Techlord
; Date: 2004-11-13 17:47:47
; Title: The Open Source Single Surface Quad Library
; Description: The Open Source Single Surface Quad Library

;==================================================================
;The Open Source 3D Quad Library
;==================================================================
Function QuadAdd%(s%,width#,height#)
	Local o%,v%
	v=AddVertex(s,-width#,-height#,0 ,0.0,1.0)
	AddVertex s,width#,-height#,0 , 1.0,1.0
	AddVertex s,-width#,height#,0 , 0.0,0.0
	AddVertex s,width#,height#,0 , 1.0,0.0
	AddTriangle s,v+0,v+2,v+1
	AddTriangle s,v+1,v+2,v+3
	For o=0 To 3
		VertexNormal s,v+o,0,0,-1
	Next
	Return v
End Function

Function QuadColor(s%,i%,r%,g%,b%,a#=1.0)
	Local o%
	For o=0 To 3
		VertexColor s,i+o,r,g,b,a
	Next
End Function

Function QuadPosition(s%,i%,x#,y#,z#=0.0)
	Local dx#,dy#,dz# , o%
	dx=x-VertexX(s,i)
	dy=y-VertexY(s,i)
	dz=z-VertexZ(s,i)
	For o=0 To 3
		VertexCoords s,i+o,VertexX(s,i+o)+dx,VertexY(s,i+o)+dy,VertexZ(s,i+o)+dz
	Next
End Function

Function QuadRotate(s,i,x#,y#,z#)
	For v% = i To i+3
		VertexCoords(s,v,(Cos (z#) * VertexX(s,v)) - (Sin (z#) * VertexY(s,v%)),(Sin (z#) * VertexX(s,v)) + (Cos (z#) * VertexY(s,v%)),VertexZ(s,v%)) 
		VertexCoords(s,v,(Cos (y#) * VertexX(s,v)) + (Sin (y#) * VertexZ(s,v%)),VertexY(s,v%),-(Sin (y#) * VertexX(s,v)) + (Cos (y#) * VertexZ(s,v%))) 
		VertexCoords(s,v,VertexX(s,v%),(Cos (x#) * VertexY(s,v%)) - (Sin (x#) * VertexZ(s,v%)) ,(Sin (x#) * VertexY(s,v%)) + (Cos (x#) * VertexZ(s,v%)))
	Next
End Function


;==================================================================
;DEMO 
;==================================================================

Graphics3D 640,480,0,2
SetBuffer BackBuffer()

HidePointer

cam=CreateCamera()
MoveEntity cam,0,0,-6.5
MoveMouse 320,240

; container for quads
container=CreateMesh()
surface=CreateSurface(container)
EntityFX container,1+2+32

quad=QuadAdd(surface,1,1)

While Not KeyDown(1)

	If KeyHit(17) ;[W]
		w=Not w
		WireFrame w
	EndIf

	If KeyDown(57) ; [SPACE]
		QuadRotate(surface,quad,0,0,1.5)
	EndIf
	
	mx#=mx#+Float(MouseXSpeed())/16.0
	my#=my#-Float(MouseYSpeed())/16.0
	MoveMouse 320,240

	QuadPosition(surface,quad,mx,my,0)

	RenderWorld

	Color 100,100,100
	Text 20,40,"[MOUSE] = move main quad"
	Text 20,60,"[SPACE] = rotate main quad"
	Text 20,80,"[W] = Wireframe"
	Text 80,130,"Angle# = "+ang#

	; reference box	
	Color 200,0,0
	Rect 320,140,100,100,0
	Flip

Wend

End
