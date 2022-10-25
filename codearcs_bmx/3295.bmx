; ID: 3295
; Author: Bobysait
; Date: 2016-10-27 14:47:59
; Title: Interpolation library
; Description: linear/bilinear/trilinear interpolations for Object  Arrays

SuperStrict

Rem
undocument the @Module line to build as a module.
Save it as YOUR_BLITZMAX_PATH/mod/MDT.mod/Gradient.bmx
EndRem

'Module MDT.Gradient

' Abstract type ! Don't create instance of BGradient without extending it first !
' for purpose, see the Int4Gradient and Float4Gradient below
Type BGradient Abstract
	
	Field Objects:Object[];
	
	Method Interpolate:Object(a:Object,b:Object, v:Double) Abstract
	
	Method NewInstance:Object() Abstract
	
	Method OnCreate:BGradient(Size:Long=2)
		Self.Objects = New Object[Size];
		Local i:Int; For i=0 Until Size; Self.Objects[i] = Self.NewInstance(); Next;
		Return Self;
	End Method
	
	Method SetArray:BGradient( array:Object[])
		Self.Objects = array;
		Return Self;
	End Method
	
	Method Set:BGradient(id:Long, o:Object)
		If (id<0 Or id>=Self.Objects.Length) Then Return Self;
		Self.Objects[id] = o;
		Return Self;
	End Method
	
	Method Get:Object(v:Double)
		If (v<=0) Then Return Self.Objects[0];
		If (v>=1.0) Then Return Self.Objects[Self.Objects.Length-1];
		Local i:Double = v * (Self.Objects.Length-1), i0:Int = Floor(i); If (i0=i) Then Return	Self.Objects[i0];
		Return	Self.Interpolate (Self.Objects[i0], Self.Objects[i0+1], i-i0);
	End Method
End Type

Type BGradient2 Abstract
	Field Gradients:BGradient[];
	
	Method NewInstance:BGradient(SizeJ:Long) Abstract
	
	Method OnCreate:BGradient2(SizeI:Long=2, SizeJ:Long=2)
		Self.Gradients = New BGradient[SizeI];
		Local i:Int; For i=0 Until SizeI; Self.Gradients[i]=Self.NewInstance(SizeJ); Next;
		Return Self;
	End Method
	
	Method Get:Object(x:Double, y:Double)
		If (x<=0) Then Return Self.Gradients[0].Get(y);
		If (x>=1) Then Return Self.Gradients[Self.Gradients.Length-1].Get(y);
		Local i:Double = x * (Self.Gradients.Length-1), i0:Int = Floor(i);
		Return Self.Gradients[0].Interpolate (Self.Gradients[i0].Get(y), Self.Gradients[i0+1].Get(y), i-i0);
	End Method
	
	Method Gradient:BGradient(pId:Long)
		If (pId<0 or pId>=Self.Gradients.Length) Then Return Null;
		Return Self.Gradients[pId];
	End Method
End Type

Type BGradient3 Abstract
	Field Gradients:BGradient2[];
	
	Method NewInstance:BGradient2(SizeJ:Long, SizeK:Long) Abstract
	
	Method OnCreate:BGradient3(SizeI:Long=2, SizeJ:Long=2, SizeK:Long=2)
		Self.Gradients = New BGradient2[SizeI];
		Local i:Int; For i=0 Until SizeI; Self.Gradients[i] = Self.NewInstance(SizeJ,SizeK); Next;
		Return Self;
	End Method
	
	Method Get:Object(x:Double, y:Double, z:Double)
		If (x<=0) Then Return Self.Gradients[0].Get(y,z);
		If (x>=1) Then Return Self.Gradients[Self.Gradients.Length-1].Get(y,z);
		Local i:Double = x * (Self.Gradients.Length-1);
		Local i0:Int = Floor(i);
		Return Self.Gradients[0].Gradients[0].Interpolate (Self.Gradients[i0].Get(y,z), Self.Gradients[i0+1].Get(y,z), i-i0);
	End Method
	
	Method Gradient2:BGradient2(pId:Long)
		If (pId<0 or pId>=Self.Gradients.Length) Then Return Null;
		Return Self.Gradients[pId];
	End Method
	
	Method Gradient:BGradient(pIdI:Long, pIdJ:Long)
		If (pIdI<0 or pIdI>=Self.Gradients.Length) Then Return Null;
		If (Self.Gradients[pIdI]=Null) Then Return Null;
		Return Self.Gradients[pIdI].Gradient(pIdJ);
	End Method
End Type






' Prototype for Int4 And Float4 arrays
' for colors or vector position ...

Type Int4Gradient Extends BGradient
	Method NewInstance:Object()
		Return New Int[4];
	End Method
	
	Function Create:Int4Gradient(Size:Long=2)
		Return Int4Gradient(New Int4Gradient.OnCreate(Size));
	End Function
	
	' Mandatory -> interpolate between two objects and returns an object
	Method Interpolate:Object(o1:Object,o2:Object,v:Double)
		Local a:Int[] = Int[](o1)
		Local b:Int[] = Int[](o2)
		' returns a new array containing a linear interpolated color/position/...
		Return [Int(a[0]+(b[0]-a[0])*v), Int(a[1]+(b[1]-a[1])*v), Int(a[2]+(b[2]-a[2])*v), Int(a[3]+(b[3]-a[3])*v)];
	End Method
	
	Method Int4:Int[](x:Double)
		Return Int[] (Self.Get(x));
	End Method
End type

Type Int4Gradient2 Extends BGradient2
	Method NewInstance:BGradient(SizeJ:Long)
		Return New Int4Gradient.OnCreate(SizeJ);
	End Method
	
	Function Create:Int4Gradient2(SizeI:Long=2, SizeJ:Long=2)
		Return Int4Gradient2(New Int4Gradient2.OnCreate(SizeI,SizeJ));
	End Function
	
	Method Int4:Int[](x:Double,y:Double)
		Return Int[] (Self.Get(x,y));
	End Method
End Type

Type Int4Gradient3 Extends BGradient3
	Method NewInstance:BGradient2(SizeJ:Long, SizeK:Long)
		Return New Int4Gradient2.OnCreate(SizeJ, SizeK);
	End Method
	
	Function Create:Int4Gradient3(SizeI:Long=2, SizeJ:Long=2, SizeK:Long=2)
		Return Int4Gradient3(New Int4Gradient3.OnCreate(SizeI,SizeJ,SizeK));
	End Function
	
	Method Int4:Int[](x:Double,y:Double,z:Double)
		Return Int[] (Self.Get(x,y,z));
	End Method
End Type




Type Float4Gradient Extends BGradient
	Method NewInstance:Object()
		Return New Float[4];
	End Method
	
	Function Create:Float4Gradient(Size:Long=2)
		Return Float4Gradient(New Float4Gradient.OnCreate(Size));
	End Function
	
	Method Interpolate:Object(o1:Object,o2:Object,d:Double)
		Local a:Float[] = Float[](o1);
		Local b:Float[] = Float[](o2);
		Local v:Float = d;
		Return [a[0]+(b[0]-a[0])*v, a[1]+(b[1]-a[1])*v, a[2]+(b[2]-a[2])*v, a[3]+(b[3]-a[3])*v];
	End Method
	
	Method Float4:Float[](x:Double)
		Return Float[] (Self.Get(x));
	End Method
End type

Type Float4Gradient2 Extends BGradient2
	Method NewInstance:BGradient(SizeJ:Long)
		Return New Float4Gradient.OnCreate(SizeJ);
	End Method
	
	Function Create:Float4Gradient2(SizeI:Long=2, SizeJ:Long=2)
		Return Float4Gradient2(New Float4Gradient2.OnCreate(SizeI,SizeJ));
	End Function
	
	Method Float4:Float[](x:Double,y:Double)
		Return Float[] (Self.Get(x,y));
	End Method
End Type

Type Float4Gradient3 Extends BGradient3
	Method NewInstance:BGradient2(SizeJ:Long, SizeK:Long)
		Return New Float4Gradient2.OnCreate(SizeJ, SizeK);
	End Method
	
	Function Create:Float4Gradient3(SizeI:Long=2, SizeJ:Long=2, SizeK:Long=2)
		Return Float4Gradient3(New Float4Gradient3.OnCreate(SizeI,SizeJ,SizeK));
	End Function
	
	Method Float4:Float[](x:Double,y:Double,z:Double)
		Return Float[] (Self.Get(x,y,z));
	End Method
End Type
