; ID: 2721
; Author: beanage
; Date: 2010-05-24 16:46:46
; Title: Float/Double from Bytes without using a Pointer
; Description: This prototype code provides functions to convert from bytes to Floats/Doubles without pointer maths

SuperStrict

Const	IEEE754_32_0_SIGN:Int = $8000000
Const	IEEE754_32_0_EXPONENT:Int = $7F800000
Const	IEEE754_32_0_FRACTION:Int = $007FFFFF
Const	IEEE754_32_FRACTION_LEN:Int = 23
Const	IEEE754_32_EXPONENT_LEN:Int = 8
Const	IEEE754_32_EXPONENT_BIAS:Int = 127

Const	IEEE754_64_1_SIGN:Int = $80000000
Const	IEEE754_64_1_EXPONENT:Int = $7FF00000
Const	IEEE754_64_1_FRACTION:Int = $000FFFFF
Const	IEEE754_64_0_FRACTION:Int = $FFFFFFFF
Const	IEEE754_64_FRACTION_LEN:Int = 52
Const	IEEE754_64_EXPONENT_LEN:Int = 11
Const	IEEE754_64_EXPONENT_BIAS:Int= 1023

Function _getFloatFromParts:Float( sign_:Byte, exponent_:Int, significand_:Float ) 'exponent already biased here
	If ( significand_< 1 )
		Return ( ( 1.0 - 2.0*Float(sign_) )* significand_ )
	Else
		Return ( ( 1.0 - 2.0*Float(sign_) )* significand_* 2^Float(exponent_) )
	End If
End Function

Function _significandFromFraction:Float( exponent_:Int, fraction_:Int ) 'exponent unbiased here
	If Not ( fraction_|exponent_ ) Then Return .0
	Local ret_:Float = Float(fraction_)/ 2^31.0
	
	ret_:+ ( exponent_<> 0 )
	Return ret_
End Function

Function _getIEEEFloatParts( data_:Int[], sign_:Byte var, exponent_:Int var, significand_:Float var ) 'data must be passed in a big endian order
	Select data_.Length
		Case 1
			sign_ = data_[0]< 0
			exponent_ = ( data_[0]& IEEE754_32_0_EXPONENT ) Shr IEEE754_32_FRACTION_LEN
			significand_ = _significandFromFraction( exponent_, (data_[0]& IEEE754_32_0_FRACTION) Shl IEEE754_32_EXPONENT_LEN ) 'call significand extraction with unbiased exponent
			exponent_:- IEEE754_32_EXPONENT_BIAS 'apply bias
		
		Case 2
'			B B B B B B B B   B B B B B B B B
'			^ ^   ^           ^
'			| |11 |20         |32
'			| |   SIGNIFICAND SIGNIFICAND
'			| EXPONENT        
'			SIGN BIT
			sign_ = ( ( data_[1]& IEEE754_64_1_SIGN )<> 0 )
			exponent_ = Int( ( data_[1]& IEEE754_64_1_EXPONENT ) Shr ( IEEE754_64_FRACTION_LEN- 32 ) )
			significand_ = _significandFromFraction( exponent_, ( ( data_[0] Shr (IEEE754_64_FRACTION_LEN-32+1) ) | ( (data_[1]& IEEE754_64_1_FRACTION) Shl IEEE754_64_EXPONENT_LEN ) ) ) '+1 to avoid shifting into the sign bit
			exponent_:- IEEE754_64_EXPONENT_BIAS 'apply bias
			
	End Select
End Function

'TextureBuffer Value Retreival

Function GetInt32:Int( buffer_:TBank, handle_:Int, idx_:Int ) 'returns int
	Local vec_:Byte
	Local ret_:Int
	
?LittleEndian
	vec_ = PeekByte( buffer_, idx_+ 0 )
	ret_ :| ( Int(vec_) )
	vec_ = PeekByte( buffer_, idx_+ 1 )
	ret_ :| ( Int(vec_) Shl 8 )
	vec_ = PeekByte( buffer_, idx_+ 2 )
	ret_ :| ( Int(vec_) Shl 16 )
	vec_ = PeekByte( buffer_, idx_+ 3 )
	ret_ :| ( Int(vec_) Shl 24 )
?BigEndian
	vec_ = PeekByte( buffer_, idx_+ 3 )
	ret_ :| ( Int(vec_) )
	vec_ = PeekByte( buffer_, idx_+ 2 )
	ret_ :| ( Int(vec_) Shl 8 )
	vec_ = PeekByte( buffer_, idx_+ 1 )
	ret_ :| ( Int(vec_) Shl 16 )
	vec_ = PeekByte( buffer_, idx_+ 0 )
	ret_ :| ( Int(vec_) Shl 24 )
?
	Return ret_
End Function

Function GetFloat32:Float( buffer_:TBank, handle_:Int, idx_:Int ) 'returns float
	Local sign_:Byte
	Local exponent_:Int
	Local significand_:Float
	
	_getIEEEFloatParts( [ GetInt32( buffer_, handle_, idx_ ) ], sign_, exponent_, significand_ );
	Return _getFloatFromParts( sign_, exponent_, significand_ );
End Function

Function GetFloat64:Float( buffer_:TBank, handle_:Int, idx_:Int ) 'converts from IEEE double to IEEE float
	Local sign_:Byte
	Local exponent_:Int
	Local significand_:Float
	
?LittleEndian
	_getIEEEFloatParts( [ GetInt32( buffer_, handle_, idx_ ), GetInt32( buffer_, handle_, idx_+ 4 ) ], sign_, exponent_, significand_ )
?BigEndian
	_getIEEEFloatParts( [ GetInt32( buffer_, handle_, idx_+ 4 ), GetInt32( buffer_, handle_, idx_ ) ], sign_, exponent_, significand_ )
?
	Return _getFloatFromParts( sign_, exponent_, significand_ );
End Function
