; ID: 2571
; Author: Bobysait
; Date: 2009-08-26 12:10:41
; Title: TForm Point
; Description: TFormPoint using getmatElement

Global TFormedX_#;
Global TFormedY_#;
Global TFormedZ_#;

Function TFormedX_#() : Return TFormedX_ : End Function
Function TFormedY_#() : Return TFormedY_ : End Function
Function TFormedZ_#() : Return TFormedZ_ : End Function

Function TFormPoint_ ( x#, y#, z#, source=0, dest=0 )
	TFormedX_ = x;
	TFormedY_ = y;
	TFormedZ_ = z;
	If (source<>0)
		Local xx# = GetMatElement(source,0,0);
		Local xy# = GetMatElement(source,0,1);
		Local xz# = GetMatElement(source,0,2);
		Local yx# = GetMatElement(source,1,0);
		Local yy# = GetMatElement(source,1,1);
		Local yz# = GetMatElement(source,1,2);
		Local zx# = GetMatElement(source,2,0);
		Local zy# = GetMatElement(source,2,1);
		Local zz# = GetMatElement(source,2,2);
		Local px# = GetMatElement(source,3,0);
		Local py# = GetMatElement(source,3,1);
		Local pz# = GetMatElement(source,3,2);
		TFormedX_ = x * xx + y * yx + z * zx + px;
		TFormedY_ = x * xy + y * yy + z * zy + py;
		TFormedZ_ = x * xz + y * yz + z * zz + pz;
	EndIf
	
	If (dest<>0)
		; invert destination matrix
		xx# = GetMatElement(dest,0,0);
		xy# = GetMatElement(dest,0,1);
		xz# = GetMatElement(dest,0,2);
		yx# = GetMatElement(dest,1,0);
		yy# = GetMatElement(dest,1,1);
		yz# = GetMatElement(dest,1,2);
		zx# = GetMatElement(dest,2,0);
		zy# = GetMatElement(dest,2,1);
		zz# = GetMatElement(dest,2,2);
		px# = GetMatElement(dest,3,0);
		py# = GetMatElement(dest,3,1);
		pz# = GetMatElement(dest,3,2);
		Local x_# = xy*yz-xz*yy;
		Local y_# = xz*yx-xx*yz;
		Local z_# = xx*yy-xy*yx;
		Local t_# = 1.0 / (zx*x_+zy*y_+zz*z_);
		Local xz_# = t_*x_ ;
		Local yz_# = t_*y_ ;
		Local zz_# = t_*z_ ;
		Local pz_# = -t_*(px*x_+py*y_+pz*z_);
		x_ = yy*zz-yz*zy;
		y_ = zx*yz-yx*zz;
		z_ = yx*zy-yy*zx;
		Local xx_# = t_*x_ ;
		Local yx_# = t_*y_ ;
		Local zx_# = t_*z_ ;
		Local px_# = -t_*(px*x_+py*y_+pz*z_);
		x_ = zy*xz-zz*xy;
		y_ = xx*zz-zx*xz;
		z_ = zx*xy-zy*xx;
		Local xy_# = t_*x_ ;
		Local yy_# = t_*y_ ;
		Local zy_# = t_*z_ ;
		Local py_# = -t_*(px*x+py*y+pz*z);
		
		x_ = TFormedX_;
		y_ = TFormedY_;
		z_ = TFormedZ_;
		TFormedX_ = x_ * xx_ + y_ * yx_ + z_ * zx_ + px_;
		TFormedY_ = x_ * xy_ + y_ * yy_ + z_ * zy_ + py_;
		TFormedZ_ = x_ * xz_ + y_ * yz_ + z_ * zz_ + pz_;
	EndIf
	
End Function




; and here a sample.


Graphics3D 1440,900,0,2
SetBuffer BackBuffer()
ClearTextureFilters()

AmbientLight 255,255,255

Local PCm%	=	CreatePivot		( )
Local Cam%	=	CreateCamera	( PCm )
				PositionEntity	( PCm, +00.0,+05.0,-05.0 )

Local plane%=	CreatePlane		( 6 )
				EntityAlpha		( plane, .3 )
				EntityColor		( plane, 100,80,130 )

Local lit%	=	CreateLight		( 2 )
				PositionEntity	( lit, +80.0,+60.0,-40.0 )
				LightRange		( lit, 150 )
				LightColor		( lit, 180,200,220 )

Local piv%	=	CreatePivot		()
				TurnEntity		( piv, 15,-40,59 )
				MoveEntity		( piv, -07.2,+12.3,-05.2 )
Local cube%	=	CreateCube		( piv )
				PositionEntity	( cube, +21.5,+08.4,+12.1 )

Local msx#	=	0.0
Local msy#	=	0.0
Local mt%	=	0
Local st%	=	MilliSecs()
Local lt%	=	mt
Local dt#	=	1.0

Local Speed#=	1.0
Local MOV_UD#=	0.0
Local MOV_LR#=	0.0
Local VEL_P#=	0.0
Local VEL_Y#=	0.0
Local Redress%=	False

Local HELP%	=	True
Local SYNC%	=	True
Local WIRE%	=	False
Repeat
	; -----------------
	; tbm
	; -----------------
		lt=mt
		mt=MilliSecs()-st
		dt=Float(mt-lt)
		If dt<1		dt=.5
		If dt>60	dt=60
		Speed	=	(dt*60)/1000

	; -----------------
	; inputs
	; -----------------
		If KeyHit(59)  HELP=1-HELP
		If KeyHit(60)  SYNC=1-SYNC
		If KeyHit(61)  WIRE=1-WIRE:WireFrame WIRE
		MOV_UD = (KeyDown(200) Or KeyDown(17)) - (KeyDown(208) Or KeyDown(31))
		MOV_LR = (KeyDown(205) Or KeyDown(32)) - (KeyDown(203) Or KeyDown(30))

	; -----------------
	; cam update
	; -----------------
		msx#=MouseXSpeed()
		msy#=MouseYSpeed()

		If MouseDown(2) And (msx<>0 Or msy<>0)
			VEL_P=VEL_P+msy*.01
			VEL_Y=VEL_Y-msx*.01
			Redress=False
		Else
			If Abs(VEL_Y)>.0005
				VEL_Y=VEL_Y-Float(Sgn(VEL_Y))*.001*dt
			Else
				VEL_Y=0
			EndIf
			If Abs(VEL_P)>.0005
				VEL_P=VEL_P-Float(Sgn(VEL_P))*.001*dt
			Else
				VEL_P=0
			EndIf
			If Redress=False
				If Abs(VEL_P)<.1
					If GetMatElement(PCm,1,1)<-.45	Then Redress=True
				EndIf
			EndIf
		EndIf


		TurnEntity PCm, +VEL_P*dt*.05,+VEL_Y*dt*.05,0
		MoveEntity PCm, Speed*MOV_LR,0,Speed*MOV_UD
		If Redress=True
			If GetMatElement(PCm,1,1)>.75
				Redress=True
			Else
				TurnEntity PCm, 0,0,.001*dt
			EndIf
		EndIf
		TurnEntity piv, dt*.1,dt*.075,-dt*.12

	RenderWorld

		Color 000,255,000
		Text 10,10,"[F1] Show Help"
		If HELP
			Rect 10,25,200,50,0
			Text 20,30,"[F2] Flip VSync ("+SYNC+")"
			Text 20,45,"[F3] Wire ("+WIRE+")"
		EndIf
		
		Color 255,128,000

		Text 010,200,LSet(GetMatElement(Cam,0,0),4)+" "+LSet(GetMatElement(Cam,0,1),4)+" "+LSet(GetMatElement(Cam,0,2),4)
		Text 010,215,LSet(GetMatElement(Cam,1,0),4)+" "+LSet(GetMatElement(Cam,1,1),4)+" "+LSet(GetMatElement(Cam,1,2),4)
		Text 010,230,LSet(GetMatElement(Cam,2,0),4)+" "+LSet(GetMatElement(Cam,2,1),4)+" "+LSet(GetMatElement(Cam,2,2),4)

		TFormVector 1,0,0,Cam,0
		Text 150,200,LSet(TFormedX(),4)+" "+LSet(TFormedY(),4)+" "+LSet(TFormedZ(),4)
		TFormVector 0,1,0,Cam,0
		Text 150,215,LSet(TFormedX(),4)+" "+LSet(TFormedY(),4)+" "+LSet(TFormedZ(),4)
		TFormVector 0,0,1,Cam,0
		Text 150,230,LSet(TFormedX(),4)+" "+LSet(TFormedY(),4)+" "+LSet(TFormedZ(),4)

		Color 200,200,000
		Local x_#=12
		Local y_#=7
		Local z_#=4
		Text 010,300," Transform from Camera"
		TFormPoint x_,y_,z_,Cam,0
		Text 010,320,LSet(TFormedX(),4)+" "+LSet(TFormedY(),4)+" "+LSet(TFormedZ(),4)
		TFormPoint_ x_,y_,z_,Cam,0
		Text 150,320,LSet(TFormedX_(),4)+" "+LSet(TFormedY_(),4)+" "+LSet(TFormedZ_(),4)

		Color 128,100,255
		Text 010,350," Transform To Camera"
		TFormPoint x_,y_,z_,0,Cam
		Text 010,370,LSet(TFormedX(),4)+" "+LSet(TFormedY(),4)+" "+LSet(TFormedZ(),4)
		TFormPoint_ x_,y_,z_,0,Cam
		Text 150,370,LSet(TFormedX_(),4)+" "+LSet(TFormedY_(),4)+" "+LSet(TFormedZ_(),4)

		Color 100,200,255
		Text 010,400," Transform from Cube to Camera"
		TFormPoint x_,y_,z_,cube,Cam
		Text 010,420,LSet(TFormedX(),4)+" "+LSet(TFormedY(),4)+" "+LSet(TFormedZ(),4)
		TFormPoint_ x_,y_,z_,cube,Cam
		Text 150,420,LSet(TFormedX_(),4)+" "+LSet(TFormedY_(),4)+" "+LSet(TFormedZ_(),4)
		
		Color 255,100,150
		Text 010,450," Transform from Camera to cube"
		TFormPoint x_,y_,z_,Cam,cube
		Text 010,470,LSet(TFormedX(),4)+" "+LSet(TFormedY(),4)+" "+LSet(TFormedZ(),4)
		TFormPoint_ x_,y_,z_,Cam,cube
		Text 150,470,LSet(TFormedX_(),4)+" "+LSet(TFormedY_(),4)+" "+LSet(TFormedZ_(),4)

		Color 180,180,180
		Text 050,500,"TFormPoint()",1,0
		Text 200,500,"TFormPoint_()",1,0

	Flip SYNC

Until KeyHit(1)

End
