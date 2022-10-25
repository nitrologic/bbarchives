; ID: 1656
; Author: Nilium
; Date: 2006-03-31 21:23:37
; Title: Vein R3 2D-in-3D System
; Description: The 2D-in-3D system used in Vein R3.

;#Region DESCRIPTION
	;; System for creating pixel perfect 3D sprites on screen
;#End Region

;#Region CLASSES
	Type t2dI3d
		Field X#,Y#,Z#,W#,H#,Camera%
	End Type
	
	Type Panel
		Field Width#
		Field Height#
		Field X#
		Field Y#
		Field Mesh%
	End Type
	
	Global X3D#,Y3D#,Z3D#		;; 2D-in-3D coordinates
;#End Region

;#Region PROCEDURES
	Function Create2DIn3DSource(Camera,Z#=24,ResW=0,ResH=0)
		If ResW <= 0 Then ResW = GraphicsWidth()
		If ResH <= 0 Then ResH = GraphicsHeight()
		P = CreatePlane(1)
		EntityPickMode P,2
		PositionEntity P,EntityX(Camera,1),EntityY(Camera,1),EntityZ(Camera,1),1
		RotateEntity P,EntityPitch(Camera,1),EntityYaw(Camera,1),EntityRoll(Camera,1),1
		MoveEntity P,0,0,Z#
		TurnEntity P,-90,0,0
		CameraPick Camera,0,0
		While PickedEntity() <> P And Z# > 1.0
			Z# = Z# - .1
			MoveEntity P,0,.1,0
			CameraPick Camera,0,0
		Wend
		If Z < 1.0 Or PickedEntity() <> P Then RuntimeError "Unable to initialize 2d in 3d module"
		TFormPoint PickedX(),PickedY(),Z,0,Camera
		X# = Abs TFormedX()
		Y# = Abs TFormedY()
		i.t2dI3d = New t2dI3d
		i\X = X : i\Y = Y : i\Z = Z : i\W = ResW : i\H = ResH : i\Camera = Camera
		FreeEntity P
		Return Handle(i)
	End Function
	
	
	Function To3D(Source,X#,Y#,TFormGlobal = 0,TFormTo=0)
		i.t2dI3d = Object.t2dI3d(Source)
		X# = -i\X + ((X# / i\W) * (i\X * 2))
		Y# = i\Y - ((Y# / i\H) * (i\Y * 2))
		If TFormGlobal = 0 Then
			TFormPoint X,Y,i\Z,i\Camera,TFormTo
			X3D = TFormedX() : Y3D = TFormedY() : Z3D = TFormedZ()
			Return
		EndIf
		
		X3D = X : Y3D = Y : Z3D = i\Z
		Return
	End Function
	
	Function MovePanel(Source,Panel,X#,Y#)
		i.t2dI3d = Object.t2dI3d(Source)
		To3D(Source,i\W/2+X,i\H/2+Y)
		p.Panel = Object.Panel(Panel)
		To3D(Source,X,Y)
	End Function
	
	Function PositionPanel(Source,Panel,X#,Y#,CenterX=0,CenterY=0)
		i.t2dI3d = Object.t2dI3d(Source)
		p.Panel = Object.Panel(Panel)
		
		If CenterX Then X = X - p\Width/2
		If CenterY Then Y = Y - p\Height/2
		
		To3D(Source,X,Y)
		PositionEntity p\Mesh,X3D,Y3D,Z3D,0
	End Function
	
	Function CreateQuad(Source,X#,Y#,Width#,Height#,CenterX=0,CenterY=0)
		If CenterX >= 1 Then : CenterX = 1 : Else : CenterX = 0 : EndIf
		If CenterY >= 1 Then : CenterY = 1 : Else : CenterY = 0 : EndIf
		X = X - Width * (CenterX*.5)
		Y = Y - Height * (CenterY*.5)
		
		i.t2dI3d = Object.t2dI3d(Source)
		M = CreateMesh()
		S = CreateSurface(M)
		To3D(Source,X,Y)
		TFormPoint X3D,Y3D,Z3D,i\Camera,0
		PositionEntity M,TFormedX(),TFormedY(),TFormedZ(),1
		RotateEntity M,EntityPitch(i\Camera,1),EntityYaw(i\Camera,1),EntityRoll(i\Camera,1),1
		EntityParent M,i\Camera,1
		
		To3D Source,X,Y
		TFormPoint X3D,Y3D,Z3D,i\Camera,M
		V = AddVertex(S,TFormedX(),TFormedY(),TFormedZ(),0,0)
		
		To3D Source,X+Width,Y
		TFormPoint X3D,Y3D,Z3D,i\Camera,M
		AddVertex(S,TFormedX(),TFormedY(),TFormedZ(),1,0)
		
		To3D Source,X+Width,Y+Height
		TFormPoint X3D,Y3D,Z3D,i\Camera,M
		AddVertex(S,TFormedX(),TFormedY(),TFormedZ(),1,1)
		
		To3D Source,X,Y+Height
		TFormPoint X3D,Y3D,Z3D,i\Camera,M
		AddVertex(S,TFormedX(),TFormedY(),TFormedZ(),0,1)
		
		AddTriangle S,V,V+1,V+2
		AddTriangle S,V+2,V+3,V
		
		EntityFX M,1+2+16+32
		EntityOrder M,-100
		
		p.Panel = New Panel
		p\X = X
		p\Y = Y
		p\Width = Width
		p\Height = Height
		p\Mesh = M
		Return Handle(p)
	End Function
	
	
	Function AddQuad(Source,Panel,X#,Y#,Width#,Height#,CenterX=0,CenterY=0)
		i.t2dI3d = Object.t2dI3d(Source)
		
		p.Panel = Object.Panel(Panel)
		
		M = p\Mesh
		S = GetSurface(M,1)
		
		To3D Source,X,Y
		TFormPoint X3D,Y3D,Z3D,i\Camera,M
		V = AddVertex(S,TFormedX(),TFormedY(),TFormedZ(),0,0)
		
		To3D Source,X+Width,Y
		TFormPoint X3D,Y3D,Z3D,i\Camera,M
		AddVertex(S,TFormedX(),TFormedY(),TFormedZ(),1,0)
		
		To3D Source,X+Width,Y+Height
		TFormPoint X3D,Y3D,Z3D,i\Camera,M
		AddVertex(S,TFormedX(),TFormedY(),TFormedZ(),1,1)
		
		To3D Source,X,Y+Height
		TFormPoint X3D,Y3D,Z3D,i\Camera,M
		AddVertex(S,TFormedX(),TFormedY(),TFormedZ(),0,1)
		
		AddTriangle S,V,V+1,V+2
		AddTriangle S,V+2,V+3,V
	End Function
	
	
	Function CreateTriangleFan(Source,X#,Y#,Radius#,InnerRadius#,StartAngle#=0,AngleMax#=360,Segments=24,FlipY=0,FlipX=0)
		i.t2dI3d = Object.t2dI3d(Source)
		M = CreateMesh()
		S = CreateSurface(M)
		TurnEntity M,0,0,180*FlipY
		TurnEntity M,0,180*FlipX,0
		To3D(Source,X,Y)
		TFormPoint X3D,Y3D,Z3D,i\Camera,0
		PositionEntity M,TFormedX(),TFormedY(),TFormedZ(),1
		RotateEntity M,EntityPitch(i\Camera,1),EntityYaw(i\Camera,1),EntityRoll(i\Camera,1),1
		EntityParent M,i\Camera,1
		
		AStep# = (AngleMax-StartAngle)/Segments
		A# = StartAngle
		
		For n = 0 To Segments-1
			A# = 180 + StartAngle + AStep*n
			To3D(Source, X+Sin(A)*Radius, Y+Cos(A)*Radius )
			TFormPoint X3D,Y3D,Z3D,i\Camera,M
			V = AddVertex( S, TFormedX(),TFormedY(),TFormedZ(), 0, 0 )
			
			To3D(Source, X+Sin(A+AStep)*Radius, Y+Cos(A+AStep)*Radius )
			TFormPoint X3D,Y3D,Z3D,i\Camera,M
			AddVertex( S, TFormedX(),TFormedY(),TFormedZ(), 1, 0 )
			
			To3D(Source, X+Sin(A+AStep)*InnerRadius, Y+Cos(A+AStep)*InnerRadius )
			TFormPoint X3D,Y3D,Z3D,i\Camera,M
			AddVertex( S, TFormedX(),TFormedY(),TFormedZ(), 1, 1 )
			
			To3D(Source, X+Sin(A)*InnerRadius, Y+Cos(A)*InnerRadius )
			TFormPoint X3D,Y3D,Z3D,i\Camera,M
			AddVertex( S, TFormedX(),TFormedY(),TFormedZ(), 0, 1 )
			
			AddTriangle S,V+2,V+1,V
			AddTriangle S,V,V+3,V+2
		Next
		EntityFX M,1+2+16+32
		TurnEntity M,0,0,180*FlipY
		TurnEntity M,0,180*FlipX,0
		EntityOrder M,-100
		
		p.Panel = New Panel
		p\X = X
		p\Y = Y
		p\Width = .001
		p\Height = .001
		p\Mesh = M
		Return Handle(P)
	End Function
	
	
	Function AddTriangleFan(Source,Panel,X#,Y#,Radius#,InnerRadius#,StartAngle#=0,AngleMax#=360,Segments=24)
		i.t2dI3d = Object.t2dI3d(Source)
		
		p.Panel = Object.Panel(Panel)
		M = p\Mesh
		
		S = GetSurface(M,1)
		
		AStep# = (AngleMax-StartAngle)/Segments
		A# = StartAngle
		
		For n = 0 To Segments-1
			A# = 180 + StartAngle + AStep*n
			To3D(Source, X+Sin(A)*Radius, Y+Cos(A)*Radius )
			TFormPoint X3D,Y3D,Z3D,i\Camera,Mesh
			V = AddVertex( S, TFormedX(),TFormedY(),TFormedZ(), 0, 0 )
			
			To3D(Source, X+Sin(A+AStep)*Radius, Y+Cos(A+AStep)*Radius )
			TFormPoint X3D,Y3D,Z3D,i\Camera,Mesh
			AddVertex( S, TFormedX(),TFormedY(),TFormedZ(), 1, 0 )
			
			To3D(Source, X+Sin(A+AStep)*InnerRadius, Y+Cos(A+AStep)*InnerRadius )
			TFormPoint X3D,Y3D,Z3D,i\Camera,Mesh
			AddVertex( S, TFormedX(),TFormedY(),TFormedZ(), 1, 1 )
			
			To3D(Source, X+Sin(A)*InnerRadius, Y+Cos(A)*InnerRadius )
			TFormPoint X3D,Y3D,Z3D,i\Camera,Mesh
			AddVertex( S, TFormedX(),TFormedY(),TFormedZ(), 0, 1 )
			
			AddTriangle S,V+2,V+1,V
			AddTriangle S,V,V+3,V+2
		Next
	End Function
	
	
	Function ColorMeter(Panel,Percent#,RFrom=255,GFrom=255,BFrom=255,AFrom#=1,RTo=128,GTo=128,BTo=128,ATo#=.5)
		p.Panel = Object.Panel(Panel)
		Mesh = p\Mesh
		S = GetSurface(Mesh,1)
		
		Vs = CountVertices(S)
		For v = 0 To Vs-1 Step 4
			A# = (Float(v+4)/(Vs))*100
			If A <= Percent Then
				For n = 0 To 3
					VertexColor S,v+n,RFrom,GFrom,BFrom,AFrom
				Next
			ElseIf NoBlend = 0 Then
				D# = (A#-Percent)/((1.0/(Vs/4))*100)
				D2# = 1.0 - D
				
				R# = RFrom*D2 + RTo*D
				G# = GFrom*D2 + GTo*D
				B# = BFrom*D2 + BTo*D
				A# = AFrom*D2 + ATo*D
				For n = 0 To 3
					VertexColor S,v+n,R,G,B,A
				Next
				NoBlend = 1
			Else
				For n = 0 To 3
					VertexColor S,v+n,RTo,GTo,BTo,ATo
				Next
			EndIf
		Next
	End Function
	
	Function TexturePanel(Panel,Texture,Frame=0,Layer=0)
		p.Panel = Object.Panel(Panel)
		EntityTexture p\Mesh,Texture,Frame,Layer
	End Function
	
	Function PanelOrder(Panel,Z%)
		p.Panel = Object.Panel(Panel)
		EntityOrder p\Mesh,-100-Z
	End Function
	
	Function PanelBlend(Panel,Blend)
		p.Panel = Object.Panel(Panel)
		EntityBlend p\Mesh,Blend
	End Function
	
	Function PanelFX(Panel,FX)
		p.Panel = Object.Panel(Panel)
		EntityFX p\Mesh,FX
	End Function
	
	Function LoadPanel(Source,X,Y,Path$,Flags=1+8,CenterX=0,CenterY=0)
		I = LoadImage(Path$)
		T = LoadTexture(Path$,Flags)
		W = ImageWidth(I)
		H = ImageHeight(I)
		Q = CreateQuad(Source,X,Y,W,H,CenterX,CenterY)
		TexturePanel Q,T
		FreeTexture T
		FreeImage I
		Return Q
	End Function
;#End Region
