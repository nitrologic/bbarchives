; ID: 2294
; Author: Otus
; Date: 2008-08-07 10:06:13
; Title: Salsa20
; Description: Salsa20 stream cipher in BlitzMax

SuperStrict

Framework BRL.StandardIO

Import BRL.Retro

Extern "C"
	Function ROL:Int(i:Int, n:Int)="_rotl"
End Extern

Type TSalsa
	
	Const ROUNDS:Int = 10
	
	Function QuarterRound( y0:Int Var, y1:Int Var, y2:Int Var, y3:Int Var )
		y1 :~ ROL( y0 + y3, 7 )
		y2 :~ ROL( y1 + y0, 9 )
		y3 :~ ROL( y2 + y1, 13 )
		y0 :~ ROL( y3 + y2, 18 )
	End Function
	
	Function RowRound( y:Int[] )
		QuarterRound y[0] , y[1] , y[2] , y[3]
		QuarterRound y[5] , y[6] , y[7] , y[4]
		QuarterRound y[10], y[11], y[8] , y[9]
		QuarterRound y[15], y[12], y[13], y[14]
	End Function
	
	Function ColumnRound( y:Int[] )
		QuarterRound y[0] , y[4] , y[8] , y[12]
		QuarterRound y[5] , y[9] , y[13], y[1]
		QuarterRound y[10], y[14], y[2] , y[6]
		QuarterRound y[15], y[3] , y[7] , y[11]
	End Function
	
	Function DoubleRound( y:Int[] )
		ColumnRound y
		RowRound y
	End Function
	
	Function Salsa20( x:Byte[] )
		GCSuspend
		Local y:Int[16]
		MemCopy Varptr y[0], Varptr x[0], 64
		For Local i:Int = 0 Until ROUNDS
			DoubleRound y
		Next
		Local xp:Int Ptr = Int Ptr Varptr x[0]
		Local yp:Int Ptr = Varptr y[0]
		For Local i:Int = 0 Until 16
			xp[i] :+ yp[i]
		Next
		GCResume
	End Function
	
	Function Salsa20K32:Byte[]( k:Byte[], n:Byte[] )
		Assert k.length = 32, "Key must be 32 bytes!"
		Assert n.length = 16, "Nonce must be 16 bytes!"
		Local x:Byte[] = ..
			[101:Byte, 120:Byte, 112:Byte, 97:Byte] +..
			k[..16]+..
			[110:Byte, 100:Byte, 32:Byte, 51:Byte]+..
			n+..
			[50:Byte, 45:Byte, 98:Byte, 121:Byte]+..
			k[16..]+..
			[116:Byte, 101:Byte, 32:Byte, 107:Byte]
		Salsa20 x
		Return x
	End Function
	
	Function Salsa20K16:Byte[]( k:Byte[], n:Byte[] )
		Assert k.length = 16, "Key must be 16 bytes!"
		Assert n.length = 16, "Nonce must be 16 bytes!"
		Local x:Byte[] = ..
			[101:Byte, 120:Byte, 112:Byte, 97:Byte] +..
			k+..
			[110:Byte, 100:Byte, 32:Byte, 49:Byte]+..
			n+..
			[54:Byte, 45:Byte, 98:Byte, 121:Byte]+..
			k+..
			[116:Byte, 101:Byte, 32:Byte, 107:Byte]
		Salsa20 x
		Return x
	End Function
	
	Function Encrypt256( k:Byte[], v:Byte[], m:Byte[] )
		Assert k.length = 32, "Key must be 32 bytes!"
		Assert v.length = 8, "Nonce must be 8 bytes!"
		v = v[..16]
		Local i:Int, j:Long
		While j*64 < m.length
			MemCopy Varptr v[8], Varptr j, 8
			Local c:Byte[] = Salsa20K32(k,v)
			i = 0
			While i<64 And j*64+i < m.length
				m[j*64+i] :~ c[i]
				i :+ 1
			Wend
			j :+ 1
		Wend
	End Function
	
	Function Encrypt128( k:Byte[], v:Byte[], m:Byte[] )
		Assert k.length = 16, "Key must be 16 bytes!"
		Assert v.length = 8, "Nonce must be 8 bytes!"
		v = v[..16]
		Local i:Int, j:Long
		While j*64 < m.length
			MemCopy Varptr v[8], Varptr j, 8
			Local c:Byte[] = Salsa20K16(k,v)
			i = 0
			While i<64 And j*64+i < m.length
				m[j*64+i] :~ c[i]
				i :+ 1
			Wend
			j :+ 1
		Wend
	End Function
	
End Type

Print "QuarterRound..."

Local a:Int[] = [$e7e8c006, $c4f9417d, $6479b4b2, $68c67137]
Local at:Int[] = [$e876d72b, $9361dfd5, $f1460244, $948541a3]
TSalsa.QuarterRound a[0], a[1], a[2], a[3]
For Local i:Int = 0 Until a.length
	If a[i]<>at[i]
		Print Hex( a[i] ) + "!"
	Else
		Print Hex( a[i] )
	End If
Next

Print "RowRound..."

a = [..
	$08521bd6, $1fe88837, $bb2aa576, $3aa26365,..
	$c54c6a5b, $2fc74c2f, $6dd39cc3, $da0a64f6,..
	$90a2f23d, $067f95a6, $06b35f61, $41e4732e,..
	$e859c100, $ea4d84b7, $0f619bff, $bc6e965a ]
at = [..
	$a890d39d, $65d71596, $e9487daa, $c8ca6a86,..
	$949d2192, $764b7754, $e408d9b9, $7a41b4d1,..
	$3402e183, $3c3af432, $50669f96, $d89ef0a8,..
	$0040ede5, $b545fbce, $d257ed4f, $1818882d]
TSalsa.RowRound a
For Local i:Int = 0 Until a.length
	If a[i]<>at[i]
		Print Hex( a[i] ) + "!"
	Else
		Print Hex( a[i] )
	End If
Next

Print "ColumnRound..."

a = [..
	$08521bd6, $1fe88837, $bb2aa576, $3aa26365,..
	$c54c6a5b, $2fc74c2f, $6dd39cc3, $da0a64f6,..
	$90a2f23d, $067f95a6, $06b35f61, $41e4732e,..
	$e859c100, $ea4d84b7, $0f619bff, $bc6e965a ]
at = [..
	$8c9d190a, $ce8e4c90, $1ef8e9d3, $1326a71a,..
	$90a20123, $ead3c4f3, $63a091a0, $f0708d69,..
	$789b010c, $d195a681, $eb7d5504, $a774135c,..
	$481c2027, $53a8e4b5, $4c1f89c5, $3f78c9c8 ]
TSalsa.ColumnRound a
For Local i:Int = 0 Until a.length
	If a[i]<>at[i]
		Print Hex( a[i] ) + "!"
	Else
		Print Hex( a[i] )
	End If
Next

Print "DoubleRound..."

a = [..
	$de501066, $6f9eb8f7, $e4fbbd9b, $454e3f57,..
	$b75540d3, $43e93a4c, $3a6f2aa0, $726d6b36,..
	$9243f484, $9145d1e8, $4fa9d247, $dc8dee11,..
	$054bf545, $254dd653, $d9421b6d, $67b276c1 ]
at = [..
	$ccaaf672, $23d960f7, $9153e63a, $cd9a60d0,..
	$50440492, $f07cad19, $ae344aa0, $df4cfdfc,..
	$ca531c29, $8e7943db, $ac1680cd, $d503ca00,..
	$a74b2ad6, $bc331c5c, $1dda24c7, $ee928277 ]
TSalsa.DoubleRound a
For Local i:Int = 0 Until a.length
	If a[i]<>at[i]
		Print Hex( a[i] ) + "!"
	Else
		Print Hex( a[i] )
	End If
Next

Print "Salsa20..."

Local b:Byte[] = [88:Byte, 118:Byte, 104:Byte, 54:Byte, 79:Byte, 201:Byte, 235:Byte, 79:Byte, 3:Byte, 81:Byte, 156:Byte, 47:Byte, 203:Byte, 26:Byte, 244:Byte, 243:Byte, ..
			191:Byte, 187:Byte, 234:Byte, 136:Byte, 211:Byte, 159:Byte, 13:Byte, 115:Byte, 76:Byte, 55:Byte, 82:Byte, 183:Byte, 3:Byte, 117:Byte, 222:Byte, 37:Byte, ..
			86:Byte, 16:Byte, 179:Byte, 207:Byte, 49:Byte, 237:Byte, 179:Byte, 48:Byte, 1:Byte, 106:Byte, 178:Byte, 219:Byte, 175:Byte, 199:Byte, 166:Byte, 48:Byte, ..
			238:Byte, 55:Byte, 204:Byte, 36:Byte, 31:Byte, 240:Byte, 32:Byte, 63:Byte, 15:Byte, 83:Byte, 93:Byte, 161:Byte, 116:Byte, 147:Byte, 48:Byte, 113:Byte]
Local t:Byte[] = [179:Byte, 19:Byte, 48:Byte, 202:Byte, 219:Byte, 236:Byte, 232:Byte, 135:Byte, 111:Byte, 155:Byte, 110:Byte, 18:Byte, 24:Byte, 232:Byte, 95:Byte, 158:Byte,.. 
			26:Byte, 110:Byte, 170:Byte, 154:Byte, 109:Byte, 42:Byte, 178:Byte, 168:Byte, 156:Byte, 240:Byte, 248:Byte, 238:Byte, 168:Byte, 196:Byte, 190:Byte, 203:Byte, ..
			69:Byte, 144:Byte, 51:Byte, 57:Byte, 29:Byte, 29:Byte, 150:Byte, 26:Byte, 150:Byte, 30:Byte, 235:Byte, 249:Byte, 190:Byte, 163:Byte, 251:Byte, 48:Byte, ..
			27:Byte, 111:Byte, 114:Byte, 114:Byte, 118:Byte, 40:Byte, 152:Byte, 157:Byte, 180:Byte, 57:Byte, 27:Byte, 94:Byte, 107:Byte, 42:Byte, 236:Byte, 35:Byte]
TSalsa.Salsa20 b
For Local i:Int = 0 Until 64
	If b[i] <> t[i]
		Print b[i]+"!"
	Else
		Print b[i]
	End If
Next

Print "Salsa20K32..."

Local k:Byte[] = [1:Byte, 2:Byte, 3:Byte, 4:Byte, 5:Byte, 6:Byte, 7:Byte, 8:Byte, 9:Byte, 10:Byte, 11:Byte, 12:Byte, 13:Byte, 14:Byte, 15:Byte, 16:Byte, ..
	201:Byte, 202:Byte, 203:Byte, 204:Byte, 205:Byte, 206:Byte, 207:Byte, 208:Byte, 209:Byte, 210:Byte, 211:Byte, 212:Byte, 213:Byte, 214:Byte, 215:Byte,216:Byte]
Local n:Byte[] = [101:Byte, 102:Byte, 103:Byte, 104:Byte, 105:Byte, 106:Byte, 107:Byte, 108:Byte, 109:Byte, 110:Byte, 111:Byte, 112:Byte, 113:Byte, 114:Byte, 115:Byte, 116:Byte]
t = [69:Byte, 37:Byte, 68:Byte, 39:Byte, 41:Byte, 15:Byte, 107:Byte, 193:Byte, 255:Byte, 139:Byte, 122:Byte, 6:Byte, 170:Byte, 233:Byte, 217:Byte, 98:Byte, ..
	89:Byte, 144:Byte, 182:Byte, 106:Byte, 21:Byte, 51:Byte, 200:Byte, 65:Byte, 239:Byte, 49:Byte, 222:Byte, 34:Byte, 215:Byte, 114:Byte, 40:Byte, 126:Byte, ..
	104:Byte, 197:Byte, 7:Byte, 225:Byte, 197:Byte, 153:Byte, 31:Byte, 2:Byte, 102:Byte, 78:Byte, 76:Byte, 176:Byte, 84:Byte, 245:Byte, 246:Byte, 184:Byte, ..
	177:Byte, 160:Byte, 133:Byte, 130:Byte, 6:Byte, 72:Byte, 149:Byte, 119:Byte, 192:Byte, 195:Byte, 132:Byte, 236:Byte, 234:Byte, 103:Byte, 246:Byte, 74:Byte]
b = TSalsa.Salsa20K32( k,n )
For Local i:Int = 0 Until 64
	If b[i] <> t[i]
		Print b[i]+"!"
	Else
		Print b[i]
	End If
Next

Print "Encrypt256..."

Local m:Byte[] = t[..]
t = m[..]
TSalsa.Encrypt256 k, n[..8], m
TSalsa.Encrypt256 k, n[..8], m
For Local i:Int = 0 Until m.length
	If m[i] <> t[i]
		Print m[i]+"!"
	Else
		Print m[i]
	End If
Next

Print "Salsa20K16..."
k = k[..16]
t = [39:Byte, 173:Byte, 46:Byte, 248:Byte, 30:Byte, 200:Byte, 82:Byte, 17:Byte, 48:Byte, 67:Byte, 254:Byte, 239:Byte, 37:Byte, 18:Byte, 13:Byte, 247:Byte, ..
	241:Byte, 200:Byte, 61:Byte, 144:Byte, 10:Byte, 55:Byte, 50:Byte, 185:Byte, 6:Byte, 47:Byte, 246:Byte, 253:Byte, 143:Byte, 86:Byte, 187:Byte, 225:Byte, ..
	134:Byte, 85:Byte, 110:Byte, 246:Byte, 161:Byte, 163:Byte, 43:Byte, 235:Byte, 231:Byte, 94:Byte, 171:Byte, 51:Byte, 145:Byte, 214:Byte, 112:Byte, 29:Byte, ..
	14:Byte, 232:Byte, 5:Byte, 16:Byte, 151:Byte, 140:Byte, 183:Byte, 141:Byte, 171:Byte, 9:Byte, 122:Byte, 181:Byte, 104:Byte, 182:Byte, 177:Byte, 193:Byte]
b = TSalsa.Salsa20K16( k,n )
For Local i:Int = 0 Until 64
	If b[i] <> t[i]
		Print b[i]+"!"
	Else
		Print b[i]
	End If
Next

Print "Encrypt128..."

m = t[..]
t = m[..]
TSalsa.Encrypt128 k, n[..8], m
TSalsa.Encrypt128 k, n[..8], m
For Local i:Int = 0 Until m.length
	If m[i] <> t[i]
		Print m[i]+"!"
	Else
		Print m[i]
	End If
Next
