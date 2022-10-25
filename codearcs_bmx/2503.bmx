; ID: 2503
; Author: Nilium
; Date: 2009-06-11 17:33:41
; Title: Number Class Cluster
; Description: Type for boxing primitive types as objects (int, float, double, etc.)

SuperStrict

Public

Const DOUBLE_DELTA! = 4.94065645841247e-324!
Const FLOAT_DELTA# = 1.4e-45#

' Used mainly for comparing types
Const TYPE_INVALID:Int = -1
Const TYPE_BOOL:Int = 0
Const TYPE_BYTE:Int = 1
Const TYPE_SHORT:Int = 2
Const TYPE_INT:Int = 3
Const TYPE_LONG:Int = 4
Const TYPE_FLOAT:Int = 5
Const TYPE_DOUBLE:Int = 6

Type TNumber Abstract
	Function ForDouble:TNumber(v!)
		Type TDouble Extends TNumber
			Field _value:Double

			Method InitWithDouble:TDouble(v!)
				_value = v
				Return Self
			End Method

			Method DoubleValue!()
				Return _value
			End Method

			Method FloatValue#()
				Return Float(_value)
			End Method

			Method ByteValue@()
				Return Byte(_value)
			End Method

			Method ShortValue@@()
				Return Short(_value)
			End Method

			Method IntValue%()
				Return Int(_value)
			End Method

			Method LongValue:Long()
				Return Long(_value)
			End Method

			Method BoolValue:Int()
				Return Int(_value)>0
			End Method

			Method GetType:Int()
				Return TYPE_DOUBLE
			End Method

			Method ToString:String()
				Return String(_value)
			End Method
		End Type
		
		Return New TDouble.InitWithDouble(v)
	End Function

	Function ForFloat:TNumber(v#)
		Type TFloat Extends TNumber
			Field _value:Float

			Method InitWithFloat:TFloat(v#)
				_value = v
				Return Self
			End Method

			Method DoubleValue!()
				Return Double(_value)
			End Method

			Method FloatValue#()
				Return Float(_value)
			End Method

			Method ByteValue@()
				Return Byte(_value)
			End Method

			Method ShortValue@@()
				Return Short(_value)
			End Method

			Method IntValue%()
				Return Int(_value)
			End Method

			Method LongValue:Long()
				Return Long(_value)
			End Method

			Method BoolValue:Int()
				Return Int(_value)>0
			End Method

			Method GetType:Int()
				Return TYPE_FLOAT
			End Method

			Method ToString:String()
				Return String(_value)
			End Method
		End Type
		
		Return New TFloat.InitWithFloat(v)
	End Function

	Function ForByte:TNumber(v@)
		Type TByte Extends TNumber
			Field _value:Byte

			Method InitWithByte:TByte(v:Byte)
				_value = v
				Return Self
			End Method

			Method DoubleValue!()
				Return Double(_value)
			End Method

			Method FloatValue#()
				Return Float(_value)
			End Method

			Method ByteValue@()
				Return _value
			End Method

			Method ShortValue@@()
				Return Short(_value)
			End Method

			Method IntValue%()
				Return Int(_value)
			End Method

			Method LongValue:Long()
				Return Long(_value)
			End Method

			Method BoolValue:Int()
				Return _value>0
			End Method

			Method GetType:Int()
				Return TYPE_BYTE
			End Method

			Method ToString:String()
				Return String(_value)
			End Method
		End Type
		
		Return New TByte.InitWithByte(v)
	End Function

	Function ForShort:TNumber(v@@)
		Type TShort Extends TNumber
			Field _value:Short

			Method InitWithShort:TShort(v:Short)
				_value = v
				Return Self
			End Method

			Method DoubleValue!()
				Return Double(_value)
			End Method

			Method FloatValue#()
				Return Float(_value)
			End Method

			Method ByteValue@()
				Return Byte(_value)
			End Method

			Method ShortValue@@()
				Return _value
			End Method

			Method IntValue%()
				Return Int(_value)
			End Method

			Method LongValue:Long()
				Return Long(_value)
			End Method

			Method BoolValue:Int()
				Return _value>0
			End Method

			Method GetType:Int()
				Return TYPE_SHORT
			End Method

			Method ToString:String()
				Return String(_value)
			End Method
		End Type
		
		Return New TShort.InitWithShort(v)
	End Function

	Function ForInt:TNumber(v%)
		Type TInt Extends TNumber
			Field _value:Int

			Method InitWithInt:TInt(v:Int)
				_value = v
				Return Self
			End Method

			Method DoubleValue!()
				Return Double(_value)
			End Method

			Method FloatValue#()
				Return Float(_value)
			End Method

			Method ByteValue@()
				Return Byte(_value)
			End Method

			Method ShortValue@@()
				Return Short(_value)
			End Method

			Method IntValue%()
				Return _value
			End Method

			Method LongValue:Long()
				Return Long(_value)
			End Method

			Method BoolValue:Int()
				Return _value>0
			End Method

			Method GetType:Int()
				Return TYPE_INT
			End Method

			Method ToString:String()
				Return String(_value)
			End Method
		End Type
		
		Return New TInt.InitWithInt(v)
	End Function

	Function ForLong:TNumber(v:Long)
		Type TLong Extends TNumber
			Field _value:Long

			Method InitWithLong:TLong(v:Long)
				_value = v
				Return Self
			End Method

			Method DoubleValue!()
				Return Double(_value)
			End Method

			Method FloatValue#()
				Return Float(_value)
			End Method

			Method ByteValue@()
				Return Byte(_value)
			End Method

			Method ShortValue@@()
				Return Short(_value)
			End Method

			Method IntValue%()
				Return Int(_value)
			End Method

			Method LongValue:Long()
				Return _value
			End Method

			Method BoolValue:Int()
				Return _value>0
			End Method

			Method GetType:Int()
				Return TYPE_LONG
			End Method

			Method ToString:String()
				Return String(_value)
			End Method
		End Type
		
		Return New TLong.InitWithLong(v)
	End Function

	Function ForBool:TNumber(b:Int)
		Type TBool Extends TNumber
			Field _value:Int

			Method InitWithBool:TBool(v:Int)
				_value = v>0
				Return Self
			End Method

			Method DoubleValue!()
				Return Double(_value)
			End Method

			Method FloatValue#()
				Return Float(_value)
			End Method

			Method ByteValue@()
				Return Byte(_value)
			End Method

			Method ShortValue@@()
				Return Short(_value)
			End Method

			Method IntValue%()
				Return _value
			End Method

			Method LongValue:Long()
				Return Long(_value)
			End Method

			Method BoolValue:Int()
				Return _value
			End Method

			Method ToString:String()
				Return String(_value)
			End Method

			Method GetType:Int()
				Return TYPE_BOOL
			End Method
		End Type
		
		Return New TBool.InitWithBool(b)
	End Function

	Method DoubleValue!() Abstract
	Method FloatValue#() Abstract
	Method ByteValue@() Abstract
	Method ShortValue@@() Abstract
	Method IntValue%() Abstract
	Method LongValue:Long() Abstract
	Method BoolValue:Int() Abstract
	Method ToString:String() Abstract

	Method GetType:Int() Abstract

	Method Compare:Int(other:Object)
		Local n:TNumber = TNumber(other)
		If n Then
			Local _type:Int, t2:Int
			_type = GetType()
			t2 = GetType()

			If _type = TYPE_INVALID Or t2 = TYPE_INVALID Then
				Throw "Attempt to compare invalid number"
			EndIf

			If t2 > _type Then
				_type = t2
			EndIf

			Select _type
				Case TYPE_BOOL
					Local b1%, b2%
					b1 = n.BoolValue()
					b2 = n.BoolValue()
					If b1 = b2 Then
						Return 0
					ElseIf b1 Then
						Return 1
					EndIf
					Return -1
				Case TYPE_DOUBLE
					Local d! = DoubleValue()-n.DoubleValue()
					If d < -DOUBLE_DELTA Then
						Return -1
					ElseIf d > DOUBLE_DELTA then
						Return 1
					EndIf
					Return 0
				Case TYPE_FLOAT
					Local f! = FloatValue()-n.FloatValue()
					If f < -FLOAT_DELTA Then
						Return -1
					ElseIf f > FLOAT_DELTA then
						Return 1
					EndIf
					Return 0
				Case TYPE_LONG
					Local l1:Long, l2:Long
					l1 = LongValue()
					l2 = n.LongValue()
					If l1 = l2 Then
						Return 0
					ElseIf l1 < l2 Then
						Return -1
					EndIf
					Return 1
				Default ' int and under
					Local i1%, i2%
					i1 = IntValue()
					i2 = n.IntValue()
					If i1 = i2 Then
						Return 0
					ElseIf i1 < i2 Then
						Return -1
					EndIf
					Return 1
			End Select
		EndIf
		Return Super.Compare(other)
	End Method
End Type
