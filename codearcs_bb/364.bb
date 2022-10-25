; ID: 364
; Author: CyBeRGoth
; Date: 2002-07-07 09:44:07
; Title: 3D Water
; Description: Creates the effect of water by moving the vertices of a mesh in a wave like fashion

Graphics3D 800,600,16,2
SetBuffer BackBuffer()

; --------------------------------------------------------------------------
; Moving water by CyBeRGoth
;
; Creates the effect of waves on the water by moving the vertices of a mesh
; --------------------------------------------------------------------------


; Load the mesh to be used as the water
; this mesh is just a flat rectangle, but it has been sub divided many times
; so that it contains a lot of vertices
Global watermesh=LoadMesh("20x20quad.3ds") : RotateMesh watermesh,90,0,0 : RotateEntity watermesh,-90,0,0
ScaleEntity watermesh,10,10,10

; Load the animation strip for the water texture
watertexture=LoadAnimTexture("wateranim.jpg",256,124,124,0,25)
EntityTexture watermesh,watertexture,0,1
ScaleTexture watertexture,.100,.100
EntityShininess watermesh,0.2

Camera=CreateCamera()
CameraRange camera,0.1,4000
CameraFogMode Camera,1
CameraFogColor Camera,10,10,50
CameraClsColor Camera,10,10,50
CameraFogRange Camera,900,2000
MoveEntity Camera,0,150,1000
PointEntity Camera,watermesh

MoonLight=CreateLight(2)
PositionEntity Moonlight,5,100,-1500

light2=CreateLight(2)
PositionEntity light2,100,100,100

Moon=CreateSphere(10,MoonLight)
ScaleEntity Moon,200,200,200
MoveEntity Moon,0,180,-50
EntityFX Moon,9

AmbientLight 0,0,0






; ##########################
; # Water Code Starts Here #
; ##########################

; DEPTH#= The maximum amount of movement a vertex will move up or down

Global surface=CountSurfaces(watermesh)
surface=GetSurface(watermesh,1)

Global VertexCount=CountVertices(surface)

Type Vertices
	Field x#
	Field y#
	Field z#
End Type

Dim Vertex.Vertices(VertexCount)

For A=0 To VertexCount-1
   Vertex(a) = New Vertices
   Vertex(a)\x#=VertexX#(surface,a)
   Vertex(a)\y#=VertexY#(surface,a)
   Vertex(a)\z#=VertexZ#(surface,a)
Next

Repeat

If KeyDown(200) Then MoveEntity camera,+0,+0.5,0
If KeyDown(208) Then MoveEntity camera,+0,-0.5,0
If KeyDown(203) Then MoveEntity camera,-1,0,0
If KeyDown(205) Then MoveEntity camera,+1,0,0

; D is used as a delay, everytime it gets to 4 the water animation frame is updated
; Frame is the current frame in the animation strip
d=d+1
If d=4
   EntityTexture watermesh,watertexture,frame,1
   frame=frame+1
   If frame=22 Then frame=0
   d=0
EndIf

; Update The Water By Moving The Vertices
UpdateWater()

UpdateWorld()
RenderWorld()

Flip
Until KeyHit(1)
End



Function UpdateWater()
; Create a wave effect by moving all the vertices in the mesh up and down using Sin
; Try editing:
; Freq#=MilliSecs()/10 
;                   ^ The Bigger the divide, the slower the water moves
; Vertex(a)\z#=Sin(freq+Vertex(a)\x#*300+Vertex(a)\y#*400)*1.125
;                                                         ^ The Bigger the Multiply The Higher 
;                                                           the waves will be, lower = smaller

For a=0 To VertexCount-1 
   Freq#=MilliSecs()/10
   Vertex(a)\z#=Sin(freq+Vertex(a)\x#*300+Vertex(a)\y#*400)*1.125
   VertexCoords surface,a,Vertex(a)\x#,Vertex(a)\y#,Vertex(a)\z#
Next

UpdateNormals watermesh

End Function
