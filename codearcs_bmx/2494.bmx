; ID: 2494
; Author: Nilium
; Date: 2009-06-01 16:59:29
; Title: Color Class
; Description: Class to handle RGBA, HSV, and CMYK colors

SuperStrict

Import Brl.Math

Private

' 1 over 255
Const b2f! = 0.003921568627450980392156862745098!

'#region Utility - Value clamping
Function ClampD!( a!, b!, c! )
	Return Min( Max( a, b ), c )
End Function
'#endregion

Public

Type TColor

	Field r!=0!, g!=0!, b!=0!, a!=0!

	'#region RGBA

	Function ForRGBA:TColor( r!, g!, b!, a! )
		Return New TColor.InitWithRGBA( r, g, b, a )
	End Function
	
	Method InitWithIntComponents:TColor( r%, g%, b%, a% )
		Self.r = r*b2f
		Self.g = g*b2f
		Self.b = b*b2f
		Self.a = a*b2f
		Return Self
	End Method
	
	Method InitWithRGBA:TColor( r!, g!, b!, a! )
		Self.r = r
		Self.g = g
		Self.b = b
		Self.a = a
		Return Self
	End Method

	Method GetRGBA( r! Var, g! Var, b! Var, a! Var )
		r = Self.r
		g = Self.g
		b = Self.b
		a = Self.a
	End Method
	
	Method GetRed!()
		Return r
	End Method
	
	Method GetGreen!()
		Return g
	End Method
	
	Method GetBlue!()
		Return b
	End Method
	
	Method GetAlpha!()
		Return a
	End Method

	Method ToInt%( )
		Local ir%, ig%, ib%, ia%
		ir = Min( Max( r*255, 0 ), 255 )
		ig = Min( Max( g*255, 0 ), 255 )
		ib = Min( Max( b*255, 0 ), 255 )
		ia = Min( Max( a*255, 0 ), 255 )
		Return ia Shl 24 | ir Shl 16 | ig Shl 8 | ib
	End Method
	
	Method IntComponents(r% Var, g% Var, b% Var, a% Var)
		r = Self.r * 255
		g = Self.g * 255
		b = Self.b * 255
		a = Self.a * 255
	End Method

	Function ForInt:TColor( rgba:Int )
		Return New TColor.InitWithRGBA( ..
			(rgba & 255)*b2f, ..
			(rgba Shr 8 & 255)*b2f, ..
			(rgba Shr 16 & 255)*b2f, ..
			(rgba Shr 24)*b2f )
	End Function
	
	'#endregion

	'#region HSV

	Method InitWithHSV:TColor( h!, s!, v!, a!=1! )
		Local hi%, f!, p!, q!, t!
		f = h / 60!
		hi = Int(f) Mod 6
		f :- hi
		p = V*(1!-s)
		q = V*(1!-(f*s))
		t = V*(1!-((1!-f)*s))
		Select hi
			Case 0; r=V;	g=t;	b=p
			Case 1; r=q;	g=V;	b=p
			Case 2; r=p;	g=V;	b=t
			Case 3; r=p;	g=q;	b=V
			Case 4; r=t;	g=p;	b=V
			Case 5; r=V;	g=p;	b=q
		End Select
		Self.a = a
		Return Self
	End Method

	Function ForHSV:TColor( h!, s!, v!, a!=1! )
		Return New TColor.InitWithHSV( h, s, v, a )
	End Function

	Method ToHSV( h! Var, s! Var, v! Var )
		Local mn!, mx!, dif!, ad!, dv!, md!
		If ( r < g and r < b )
			mn = r
		Else If ( g < b )
			mn = g
		Else
			mn = b
		EndIf

		If ( r > g and r > b )
			mx = r
			dif = g-b
			ad = 0!
		Else If ( g > b )
			dif = b-r
			mx = g
			ad = 120!
		Else
			dif = r-g
			ad = 240!
			mx = b
		EndIf
		
		md = mx-mn

		h = (60!*(dif / md))+ad
		s = md/mx
		V = mx
	End Method
	
	Method GetHue!()
		Local h!,s!,v!
		ToHSV(h,s,v)
		Return h
	End Method
	
	Method GetSaturation!()
		Local h!,s!,v!
		ToHSV(h,s,v)
		Return s
	End Method
	
	Method GetValue!()
		Local h!,s!,v!
		ToHSV(h,s,v)
		Return v
	End Method
	
	'#endregion
	
	'#region CMYK
	
	Method InitWithCMYK:TColor( c!, m!, y!, k!, a!=1! )
		Return Self.InitWithRGBA( ..
			1! - (c*(1!-k)+k), ..
			1! - (m*(1!-k)+k), ..
			1! - (y*(1!-k)+k), a )
	End Method
	
	Function ForCMYK:TColor( c!, m!, y!, k!, a!=1! )
		Return New TColor.InitWithCMYK( c, m, y, k, a )
	End Function

	Method ToCMYK( c! Var, m! Var, y! Var, k! Var )
		Local cmy![] = [1!-r, 1!-g, 1!-b]
		Local sm% = 0
		
		If cmy[1] < cmy[0] And cmy[1] < cmy[2] Then
			sm = 1
		ElseIf cmy[2] < cmy[0] And cmy[2] < cmy[1] Then
			sm = 2
		EndIf
		
		If cmy[sm] >= 1 Then
			c = 0!
			m = 0!
			y = 0!
			k = 1!
		Else
			k = cmy[sm]
			Local kd! = 1.0 / (1! - k)
			c = (cmy[0]-k)*kd
			m = (cmy[1]-k)*kd
			y = (cmy[2]-k)*kd
		EndIf
	End Method
	
	Method GetCyan!()
		Local c!,m!,y!,k!
		ToCMYK(c,m,y,k)
		Return c
	End Method
	
	Method GetMagenta!()
		Local c!,m!,y!,k!
		ToCMYK(c,m,y,k)
		Return m
	End Method
	
	Method GetYellow!()
		Local c!,m!,y!,k!
		ToCMYK(c,m,y,k)
		Return y
	End Method
	
	Method GetKey!()
		Local c!,m!,y!,k!
		ToCMYK(c,m,y,k)
		Return k
	End Method
	
	'#endregion

	'#region Operations

	Method Multiply:TColor( m:TColor )
		Return New TColor.InitWithRGBA( r*m.r, g*m.g, b*m.b, a*m.a )
	End Method

	Method Add:TColor( o:TColor )
		Return New TColor.InitWithRGBA( r+o.r, g+o.g, b+o.b, a+o.a )
	End Method

	Method Subtract:TColor( o:TColor )
		Return New TColor.InitWithRGBA( r-o.r, g-o.g, b-o.b, a-o.a )
	End Method
	
	Method Clamp:TColor( bot!, top! )
		Return New TColor.InitWithRGBA(..
			ClampD( r, bot, top ),..
			ClampD( g, bot, top ),..
			ClampD( b, bot, top ),..
			ClampD( a, bot, top ) )
	End Method
	
	Method Scale:TColor( x! )
		Return New TColor.InitWithRGBA(r*x, g*x, b*x, a*x)
	End Method
	
	Method Invert:TColor()
		Return New TColor.InitWithRGBA(1!-r, 1!-g, 1!-b, 1!-a)
	End Method
	
	' If no function is specified, default to linear interpolation
	Method Interpolate:TColor( other:TColor, delta:Double, fn:Double(start:Double, finish:Double, delta:Double, context:Object)=Null, context:Object=Null )
		delta = ClampD(delta, 0!, 1!)
		If fn Then
			Return New TColor.InitWithRGBA( ..
				fn(r, other.r, delta, context), ..
				fn(g, other.g, delta, context), ..
				fn(b, other.b, delta, context), ..
				fn(a, other.a, delta, context) )
		EndIf
		Local invdelta:Double = (1.0! - delta)
		Return New TColor.InitWithRGBA( ..
			other.r*delta + r*invdelta, ..
			other.g*delta + g*invdelta, ..
			other.b*delta + b*invdelta, ..
			other.a*delta + a*invdelta)
	End Method
	
	'#endregion

	'#region Colors

	Function White:TColor( ); Return New TColor.InitWithRGBA( 1!, 1!, 1!, 1! ); End Function
	Function Red:TColor( ); Return New TColor.InitWithRGBA( 1!, 0!, 0!, 1! ); End Function
	Function Green:TColor( ); Return New TColor.InitWithRGBA( 0!, .75!, 0!, 1! ); End Function
	Function Blue:TColor( ); Return New TColor.InitWithRGBA( 0!, 0!, .75!, 1! ); End Function
	Function Lime:TColor( ); Return New TColor.InitWithRGBA( .25!, 1!, 0!, 1! ); End Function
	Function LightGreen:TColor( ); Return New TColor.InitWithRGBA( .4!, 1!, .4!, 1! ); End Function
	Function Pink:TColor( ); Return New TColor.InitWithRGBA( 1!, .25!, .25!, 1! ); End Function
	Function Purple:TColor( ); Return New TColor.InitWithRGBA( 1!, 0!, .5!, 1! ); End Function
	Function Turqoise:TColor( ); Return New TColor.InitWithRGBA( 0!, 1!, 1!, 1! ); End Function
	Function Grey:TColor( ); Return New TColor.InitWithRGBA( .5!, .5!, .5!, 1! ); End Function
	Function Black:TColor( ); Return New TColor.InitWithRGBA( 0!, 0!, 0!, 1! ); End Function
	Function Yellow:TColor( ); Return New TColor.InitWithRGBA( 1!, 1!, 0!, 1! ); End Function
	Function LightBlue:TColor( ); Return New TColor.InitWithRGBA( .4!, .4!, 1!, 1! ); End Function
	Function Maroon:TColor( ); Return New TColor.InitWithRGBA( .5!, 0!, 0!, 1! ); End Function
	Function Orange:TColor( ); Return New TColor.InitWithRGBA( 1!, .55!, 0!, 1! ); End Function
	Function Brown:TColor( ); Return New TColor.InitWithRGBA( .5!, .4!, 0!, 1! ); End Function
	Function Clear:TColor( ); Return New TColor.InitWithRGBA( 1!, 1!, 1!, 0! ); End Function

	'#endregion
	
	Method ToString$()
		Return "#<TColor:0x"+Super.ToString()+" @r="+r+" @g="+g+" @b="+b+" @a="+a+">"
	End Method
End Type
