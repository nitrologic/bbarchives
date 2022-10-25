; ID: 2895
; Author: Noobody
; Date: 2011-10-21 17:59:31
; Title: PickedUV / Move texture with mouse
; Description: Calculates UV coordinates at picked location. Code comes with a sample on how to use this function to move a texture on a mesh with the mouse

Graphics3D 800, 600, 0, 2
SetBuffer BackBuffer()

Global PickedU#, PickedV#


Local Cam = CreateCamera()
Local Texture = CreateFunkyTexture()
Local Mesh = CreateCone(64)
EntityPickMode Mesh, 2
EntityTexture Mesh, Texture

MoveEntity Cam, 0.0, 0.0, -3.0

Local Timer = CreateTimer(60)

Local OldPickedU#, OldPickedV#, OldPickedSurface

While Not KeyHit(1)
	TurnEntity Mesh, KeyDown(200) - KeyDown(208), KeyDown(205) - KeyDown(203), 0.0, True
	
	If MouseDown(1) Then
		CameraPick(Cam, MouseX(), MouseY())
		
		If PickedEntity() <> 0 And PickedSurface() <> 0 Then
			CalculatePickedUV()
			
			If PickedSurface() = OldPickedSurface Then
				DU# = PickedU# - OldPickedU#
				DV# = PickedV# - OldPickedV#
				
				Local Surface = PickedSurface()
				For Vertex = 0 To CountVertices(Surface) - 1
					Local U# = VertexU(Surface, Vertex)
					Local V# = VertexV(Surface, Vertex)
					
					VertexTexCoords Surface, Vertex, U# - DU#, V# - DV#
				Next
				
				PickedU# = PickedU# - DU#
				PickedV# = PickedV# - DV#
			Else
				OldPickedSurface = PickedSurface()
			EndIf
			
			OldPickedU# = PickedU#
			OldPickedV# = PickedV#
		Else
			OldPickedSurface = 0
		EndIf
	Else
		OldPickedSurface = 0
	EndIf
	
	RenderWorld
	
	Text 0, 0, "Click and drag the texture!"
	Text 0, 15, "Use arrow keys to turn the mesh"
	
	Flip 0
	WaitTimer Timer
Wend
End

Function CreateFunkyTexture()
	Local Texture = CreateTexture(256, 256, 1)
	
	SetBuffer TextureBuffer(Texture)
	LockBuffer()
	
	For X = 0 To 255
		For Y = 0 To 255
			WritePixelFast X, Y, (X*$010001) Xor (Y*$010101)
		Next
	Next
	
	UnlockBuffer()
	SetBuffer BackBuffer()
	
	Return Texture
End Function

Function CalculatePickedUV()
	Local Surface = PickedSurface()
	If Surface Then
		Local Mesh = PickedEntity()
		
		TFormPoint PickedX(), PickedY(), PickedZ(), 0, Mesh
		
		Local Triangle = PickedTriangle()
		
		Local V1 = TriangleVertex(Surface, Triangle, 0)
		Local V2 = TriangleVertex(Surface, Triangle, 1)
		Local V3 = TriangleVertex(Surface, Triangle, 2)
		
		Local DX1# = VertexX(Surface, V2) - VertexX(Surface, V1)
		Local DY1# = VertexY(Surface, V2) - VertexY(Surface, V1)
		Local DZ1# = VertexZ(Surface, V2) - VertexZ(Surface, V1)
		Local DX2# = VertexX(Surface, V3) - VertexX(Surface, V1)
		Local DY2# = VertexY(Surface, V3) - VertexY(Surface, V1)
		Local DZ2# = VertexZ(Surface, V3) - VertexZ(Surface, V1)
		
		Local NX# = DY1#*DZ2# - DY2#*DZ1#
		Local NY# = DX2#*DZ1# - DX1#*DZ2#
		Local NZ# = DX1#*DY2# - DY1#*DX2#
		Local UX# = NY #*DZ2# - DY2#*NZ #
		Local UY# = DX2#*NZ # - NX #*DZ2#
		Local UZ# = NX #*DY2# - NY #*DX2#
		
		Local InvLength1# = 1.0/Sqr(UX#*UX# + UY#*UY# + UZ#*UZ#)
		Local Length2# = Sqr(DX2#*DX2# + DY2#*DY2# + DZ2#*DZ2#)
		Local InvLength2# = 1.0/Length2#
		
		UX# = UX#*InvLength1#
		UY# = UY#*InvLength1#
		UZ# = UZ#*InvLength1#
		DX2# = DX2#*InvLength2#
		DY2# = DY2#*InvLength2#
		DZ2# = DZ2#*InvLength2#
		
		Local T1# = 0.0
		Local S1# = 0.0
		Local T2# = DX1#*UX # + DY1#*UY # + DZ1#*UZ #
		Local S2# = DX1#*DX2# + DY1#*DY2# + DZ1#*DZ2#
		Local T3# = 0.0
		Local S3# = Length2#
		Local T4# = (TFormedX() - VertexX(Surface, V1))*UX # + (TFormedY() - VertexY(Surface, V1))*UY # + (TFormedZ() - VertexZ(Surface, V1))*UZ #
		Local S4# = (TFormedX() - VertexX(Surface, V1))*DX2# + (TFormedY() - VertexY(Surface, V1))*DY2# + (TFormedZ() - VertexZ(Surface, V1))*DZ2#

		
		Local Denominator# = 1.0/((S2# - S3#)*(T1# - T3#) + (T3# - T2#)*(S1# - S3#))
		Local Lambda1# = ((S2# - S3#)*(T4# - T3#) + (T3# - T2#)*(S4# - S3#))*Denominator#
		Local Lambda2# = ((S3# - S1#)*(T4# - T3#) + (T1# - T3#)*(S4# - S3#))*Denominator#
		Local Lambda3# = 1.0 - Lambda1# - Lambda2#
		
		PickedU# = VertexU(Surface, V1)*Lambda1# + VertexU(Surface, V2)*Lambda2# + VertexU(Surface, V3)*Lambda3#
		PickedV# = VertexV(Surface, V1)*Lambda1# + VertexV(Surface, V2)*Lambda2# + VertexV(Surface, V3)*Lambda3#
	EndIf
End Function
