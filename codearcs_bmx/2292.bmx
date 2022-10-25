; ID: 2292
; Author: Otus
; Date: 2008-07-29 09:03:38
; Title: HC-256
; Description: HC-256 stream cipher in BlitzMax

SuperStrict

Extern "C"

	Function Ror:Int(i:Int, n:Int) = "_rotr"
	
End Extern

Type THcContext
	
	'S-boxes
	Field _P:Int[]
	Field _Q:Int[]
	
	'Counter
	Field _i:Int
	
	Method Init(k:Int[] , iv:Int[])
		Assert (k.Length = 8) And (iv.Length = 8), "K and IV must be 8 Ints long!"
		Local W:Int[] = k + iv + New Int[2544]
		
		For Local i:Int = 16 To 2559
			W[i] = _f2(W[i-2]) + W[i-7] + _f1(W[i-15]) + W[i-16] + i
		Next
		
		_P = W[512..1536]
		_Q = W[1536..]
		
		Assert _P.Length = 1024 , "P size: " + _P.Length
		Assert _Q.Length = 1024 , "Q size: " + _Q.Length
		
		For Local i:Int = 0 Until 4096
			Local j:Int = i & 1023
			If (i & 2047) < 1024
				_P[j] :+ _P[(j - 10) & 1023] ..
					+ _g1( _P[(j-3) & 1023], _P[(j-1023) & 1023] )
			Else
				_Q[j] :+ _Q[(j - 10) & 1023] ..
					+ _g2( _Q[(j-3) & 1023], _Q[(j-1023) & 1023] )
			End If
		Next
		
		_i = 0
	End Method
	
	Method Initialize(key:String , salt:String) 
		If key.Length < 8 Then key = key[..8]
		If salt.Length < 8 Then salt = salt[..8]
		Local k:Int[8] , iv:Int[8]
		Local l:Int = key.Length
		For Local i:Int = 0 Until l
			k[i & 7] = key[i] + _f1( key[(i+3) Mod l] ) + _f2( key[(i+5) Mod l] )
		Next
		l = salt.Length
		For Local i:Int = 0 Until l
			iv[i & 7] = salt[i] + _f1( salt[(i+3) Mod l] ) + _f2( salt[(i+5) Mod l] )
		Next
		Init k, iv
	End Method
	
	Method Output:Int() 
		Local j:Int = _i & 1023
		If (_i & 2047) < 1024
			_i :+ 1
			_P[j] :+ _P[(j - 10) & 1023] ..
				+ _g1( _P[(j-3) & 1023], _P[(j-1023) & 1023] ) 
			Return _h1( _P[(j-12) & 1023] ) ~ _P[j]
		Else
			_i :+ 1
			_Q[j] :+ _Q[(j - 10) & 1023] ..
				+ _g2( _Q[(j-3) & 1023], _Q[(j-1023) & 1023] ) 
			Return _h2( _Q[(j-12) & 1023] ) ~ _Q[j]
		End If
	End Method
	
	Method DecryptString:String(s:String)
		Local s2:String , i:Int, k:Int
		Local j:Int, slen:Int
		
		For k = 0 To 3
			j :+ s[k] Shl (k Shl 3)
		Next
		slen = j ~ Output()
		s = s[4..]
		
		While i < slen
			j = s[i] + (s[i + 1] Shl 8) + (s[i + 2] Shl 16) + (s[i + 3] Shl 24) 
			j :~ Output() 
			For k = 0 To 3
				If s2.Length = slen Then Exit
				s2 :+ Chr( (j Shr (k Shl 3) ) & 255 )
			Next
			i :+ 4
		Wend
		
		Return s2
	End Method
	
	Method EncryptString:String(s:String)
		Local s2:String , i:Int, k:Int
		Local j:Int = s.Length ~ Output() 
		
		For k = 0 To 3
			s2 :+ Chr( (j Shr (k Shl 3)) & 255 ) 
		Next
		
		While i < s.Length
			j = s[i] + (s[i + 1] Shl 8) + (s[i + 2] Shl 16) + (s[i + 3] Shl 24) 
			j :~ Output() 
			For k = 0 To 3
				s2 :+ Chr( (j Shr (k Shl 3)) & 255 ) 
			Next
			i :+ 4
		Wend
		
		Return s2
	End Method
	
	Function _f1:Int(x:Int) 
		Return Ror(x , 7) ~ Ror(x , 18) ~ Ror(x , 3) 
	End Function
	
	Function _f2:Int(x:Int) 
		Return Ror(x , 17) ~ Ror(x , 19) ~ Ror(x , 10) 
	End Function
	
	Method _g1:Int(x:Int , y:Int) 
		Return ( Ror(x , 10) ~ Ror(y , 23) ) + _Q[ (x ~ y) & 1023 ]
	End Method
	
	Method _g2:Int(x:Int , y:Int) 
		Return ( Ror(x , 10) ~ Ror(y , 23) ) + _P[ (x ~ y) & 1023 ]
	End Method
	
	Method _h1:Int(x:Int) 
		Local b:Byte Ptr = Byte Ptr( Varptr x ) 
		Return _Q[ b[0] ] + _Q[ 256 + b[1] ] + _Q[ 512 + b[2] ] + _Q[ 768 + b[3] ]
	End Method
	
	Method _h2:Int(x:Int) 
		Local b:Byte Ptr = Byte Ptr( Varptr(x) ) 
		Return _P[ b[0] ] + _P[ 256 + b[1] ] + _P[ 512 + b[2] ] + _P[ 768 + b[3] ]
	End Method
	
End Type
