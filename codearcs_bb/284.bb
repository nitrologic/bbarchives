; ID: 284
; Author: Rob
; Date: 2002-04-06 21:33:28
; Title: MathLights
; Description: easy way to light an object and beat the 8 light limit

;Code example for Stellar.
; By rob cummings (rob@redflame.net)
;
Global radius=8 ; higher radius = smaller light - crazy but true here!
HidePointer:AppTitle "Rob's mathlights"
Graphics3D 640,480,16,2
camera=CreateCamera()
CameraRange camera,1,5000
PositionEntity camera,0,150,0
RotateEntity camera,90,0,0

sun=CreateLight(2)
PositionEntity sun,2000,2000,2000
shield=CreateSphere(32)
ScaleEntity shield,20,20,40
EntityFX shield,2+1
shieldlight=CreateSphere() ;use createpivot() when ready and attach to lazer blast etc - you know as an emitter of some kind
EntityFX shieldlight,1
MoveMouse GraphicsWidth()/2,GraphicsHeight()/2
PositionEntity shieldlight,50,0,0


While Not KeyHit(1)
	;for testing, move the shield light emitter with mouse
	MoveEntity shieldlight,MouseXSpeed(),0,-MouseYSpeed()
	MoveMouse GraphicsWidth()/2,GraphicsHeight()/2

	;now calculate vert colors for the shield based upon the position of shieldlight
	s=GetSurface(shield,1)
	For i=1 To CountVertices(s)-1
		TFormPoint VertexX(s,i),VertexY(s,i),VertexZ(s,i),shield,0
		xd#=TFormedX()-EntityX(shieldlight)
		yd#=TFormedY()-EntityY(shieldlight)
		zd#=TFormedZ()-EntityZ(shieldlight)
		dist#=Sqr(xd*xd + yd*yd + zd*zd)*radius		
		If dist<0 Then dist=0
		If dist>255 Then dist=255
		VertexColor s,i,0,0,255-dist
	Next

	RenderWorld
	Text 0,0,"Move the mouse to move the mathlight"
	Flip
Wend
End
