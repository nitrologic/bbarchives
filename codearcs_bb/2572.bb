; ID: 2572
; Author: Bobysait
; Date: 2009-08-26 13:43:35
; Title: TForm Vector
; Description: TForm Vector using GetMatElement

Global TFormedX_#=0.0
Global TFormedY_#=0.0
Global TFormedZ_#=0.0

Function TFormedX_#() Return TFormedX_ End Function
Function TFormedY_#() Return TFormedY_ End Function
Function TFormedZ_#() Return TFormedZ_ End Function

Function TFormVector_ ( x#, y#, z#, source=0, dest=0 )
	
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
		TFormedX_ = x * xx + y * yx + z * zx;
		TFormedY_ = x * xy + y * yy + z * zy;
		TFormedZ_ = x * xz + y * yz + z * zz;
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
		Local x_# = xy*yz-xz*yy;
		Local y_# = xz*yx-xx*yz;
		Local z_# = xx*yy-xy*yx;
		Local t_# = 1.0 / (zx*x_+zy*y_+zz*z_);
		Local xz_# = t_*x_ ;
		Local yz_# = t_*y_ ;
		Local zz_# = t_*z_ ;
		x_ = yy*zz-yz*zy;
		y_ = zx*yz-yx*zz;
		z_ = yx*zy-yy*zx;
		Local xx_# = t_*x_ ;
		Local yx_# = t_*y_ ;
		Local zx_# = t_*z_ ;
		x_ = zy*xz-zz*xy;
		y_ = xx*zz-zx*xz;
		z_ = zx*xy-zy*xx;
		Local xy_# = t_*x_ ;
		Local yy_# = t_*y_ ;
		Local zy_# = t_*z_ ;
		
		x_ = TFormedX_;
		y_ = TFormedY_;
		z_ = TFormedZ_;
		TFormedX_ = x_ * xx_ + y_ * yx_ + z_ * zx_;
		TFormedY_ = x_ * xy_ + y_ * yy_ + z_ * zy_;
		TFormedZ_ = x_ * xz_ + y_ * yz_ + z_ * zz_;
	EndIf
	
End Function
