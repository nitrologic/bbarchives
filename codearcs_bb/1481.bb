; ID: 1481
; Author: Cygnus
; Date: 2005-10-07 08:12:47
; Title: Faster vertice manipulation
; Description: Get a huge speed increase modifying meshes when using collision such as linepick()

Graphics3D 640,480,16,2
light=CreateLight()

mesh=CreateSphere(8)
Global dst#
camera=CreateCamera()
EntityFX mesh,3
MoveEntity camera,0,20,-30
PointEntity camera,mesh

EntityPickMode mesh,2
sf=GetSurface(mesh,1)

ms=MilliSecs()
For n=1 To 2500
	vert=Rand(0,CountVertices(sf))
	VertexCoords(sf,vert,Rand(-10,10),Rand(-10,10),Rand(-10,10))
	VertexTexCoords(sf,vert,Rand(-10,10),Rand(-10,10),Rand(-10,10))
	LinePick(0,0,0,1,1,1)
Next
ms2=MilliSecs()
normalupdate=ms2-ms

ms=MilliSecs()
For n=1 To 2500
	vert=Rand(0,CountVertices(sf))
	VertexCoords2(sf,vert,Rand(-10,10),Rand(-10,10),Rand(-10,10))
	VertexTexCoords2(sf,vert,Rand(-10,10),Rand(-10,10),Rand(-10,10))
	LinePick(0,0,0,1,1,1)
Next
updatemeshes()
ms2=MilliSecs()
Speedupdate=ms2-ms


WireFrame 1

Repeat
	If mode=1 Then
		For n=1 To 1000
			vert=Rand(0,CountVertices(sf))
			VertexCoords2(sf,vert,Rand(-10,10),Rand(-10,10),Rand(-10,10))
			VertexTexCoords2(sf,vert,Rand(-10,10),Rand(-10,10),Rand(-10,10))
			LinePick(0,0,0,1,1,1)
		Next
		updatemeshes
	Else
		For n=1 To 1000
			vert=Rand(0,CountVertices(sf))
			VertexCoords(sf,vert,Rand(-10,10),Rand(-10,10),Rand(-10,10))
			VertexTexCoords(sf,vert,Rand(-10,10),Rand(-10,10),Rand(-10,10))
			LinePick(0,0,0,1,1,1)
		Next
EndIf

MoveEntity mesh,KeyDown(203)-KeyDown(205),0,KeyDown(200)-KeyDown(208)
TurnEntity mesh,1,1,1
RenderWorld
Text 0,0,dst
Text 0,20,"Normal update:"+normalupdate
Text 0,32,"Speed update:"+speedupdate

Text 0,50,"Hit space to toggle mode"
If KeyHit(57) Then mode=(mode+1) Mod 2
If mode=0 Then txt$="Normal" Else txt$="Speed"
Text 0,62,"Current mode:"+txt$

Flip
Until KeyDown(1)
End
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;                 Functions                   ;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

Type vertexupd
     Field commandindex
     Field surface,index,x#,y#,z#
     Field u#,v#,w#,coordset
     Field r#,g#,b#,a#
End Type

Function VertexCoords2(surface,index,x#,y#,z#)
Local t.vertexupd
t.vertexupd=New vertexupd
t\commandindex=1
t\surface=surface
t\index=index
t\x=x
t\y=y
t\z=z
End Function

Function VertexTexCoords2(surface,index,u#,v#,w#=0,coord_set=0)
Local t.vertexupd
t.vertexupd=New vertexupd
t\commandindex=2
t\surface=surface
t\index=index
t\u#=u
t\v#=v
t\w#=w
t\coordset=coord_set
End Function

Function VertexColor2(surface,index,r#,g#,b#,a#=1)
Local t.vertexupd
t.vertexupd=New vertexupd
t\commandindex=3
t\surface=surface
t\index=index
t\r=r
t\g=g
t\b=b
t\a=a
End Function

Function updatemeshes()
Local t.vertexupd
Local dogrouped=1
For t.vertexupd=Each vertexupd
Select t\commandindex
Case 1
VertexCoords t\surface,t\index,t\x,t\y,t\z
Case 2
VertexTexCoords t\surface,t\index,t\u,t\v,t\w,t\coordset
Case 3
VertexColor t\surface,t\index,t\r,t\g,t\b,t\a
End Select
Delete t
Next
End Function
