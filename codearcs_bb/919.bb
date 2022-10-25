; ID: 919
; Author: Matty
; Date: 2004-02-06 07:21:41
; Title: Crater Function
; Description: This function creates a crater model out of quads which will align itself with the terrain beneath it

;
;New simpler craters function.  Works with multiple quads joined into a flat mesh which is then 
;arranged into a crater formation...takes a 8x8 quad (128 polygons)....
;
;It can use a single surface for each crater, simply specify the same surface that has the texture
;of the crater on it...
;
;
;MakeCrater(Surface,Size#,X#,Y#,Z#,Terrain,Height#,RandomFactor#)
;
;Typically the Size parameter is at least 4 times as large as the height parameter.
;Terrain is the Terrain handle, leave it at zero if there is no terrain.
;
;
;
;
;
Type CraterObj

Field ID
Field Width#
Field Height#
Field X#
Field Y#
Field Z#
Field Surface
Field Radius#

End Type

Type CraterVertexObj


Field ID
Field Index
Field X#
Field Y#
Field Z#
Field U#
Field V#
Field VertexNum
End Type


Function MakeCrater(Surface,Size#,X#,Y#,Z#,Terrain,Height#,RandomFactor#)
Myid=0
If size<=0 Then Return 0
If surface<=0 Then Return 0

For crater.craterobj=Each craterobj
MyId=crater\id+1
Next

Crater.CraterObj=New CraterObj
crater\id=myid
crater\width=Size
crater\height=Size
crater\surface=surface
;crater\radius=radius
crater\x=x
crater\y=y
crater\z=z

VertexNum=0

Radius#=(4*Size)^2
For j=1 To 8
For i=1 To 8
VertexNum=VertexNum+1
C.CraterVertexObj=New CraterVertexObj
C\ID=crater\id

C\x=Float((i-5))*Size
C\z=Float((j-5))*Size
C\U=0.125*Float(i-1)
C\V=0.125*Float(j-1)

Dist#=C\x^2+C\z^2
c\y=crater\y

;this is the function which determines the height of the craters....
If Dist<0.33*Radius Then 
c\y=c\y+9.0*Height*(Dist/Radius)
Else
c\y=c\y+9.0*Height*0.25*((Sqr(Radius)-Sqr(Dist))/Radius)^2
EndIf


If Terrain>0 Then C\Y=c\Y+TerrainY(Terrain,crater\x+C\X,C\Y,crater\z+C\Z)
C\Y=C\Y+Rnd(-RandomFactor,RandomFactor)
C\Index=AddVertex (Surface,C\X,C\Y,C\Z,C\U,C\V)
C\VertexNum=VertexNum
VertexCoords(Surface,C\Index,C\X+crater\x,C\Y,C\Z+crater\z)
Next 
Next

poly=0
For C.cratervertexobj=Each cratervertexobj

If c\id=crater\id Then 
poly=poly+1
Vertex1=c\index
Vertex1Num=c\vertexnum
If c\vertexnum Mod 8<>0 And c\vertexnum<8*7 Then 
For d.cratervertexobj=Each cratervertexobj
If d\id=c\id Then 
If d\vertexnum=c\vertexnum+1 Then Vertex2=d\index
If d\vertexnum=c\vertexnum+9 Then Vertex3=d\index
If d\vertexnum=c\vertexnum+8 Then vertex4=d\index 
EndIf
Next
AddTriangle(Surface,Vertex3,Vertex2,Vertex1)
AddTriangle(Surface,Vertex4,Vertex3,Vertex1)
EndIf 
EndIf

If poly=128 Then Exit 
Next

End Function
