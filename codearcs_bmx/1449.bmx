; ID: 1449
; Author: Yan
; Date: 2005-08-21 17:43:27
; Title: A medley of digests
; Description: Functions to create MD5, SHA-1 and SHA-256 digests.

Strict

'====================================== TEST ======================================

Print
Print "==== MD5 ===="
Print MD5("The quick brown fox jumps over the lazy dog")
Print "9e107d9d372bb6826bd81d3542a419d6 <- Should be this"
Print
Print "===== SHA-1 ====="
Print SHA1("The quick brown fox jumps over the lazy dog")
Print "2fd4e1c67a2d28fced849ee1bb76e7391b93eb12 <- Should be this"
Print
Print "===== SHA-256 ====="
Print SHA256("The quick brown fox jumps over the lazy dog")
Print "d7a8fbb307d7809469ca9abcb0082e4f8d5651e46d3cdb762d02d0bf37c9e592 <- Should be this"

End

'====================================== TEST ======================================


Function MD5$(in$)
  Local h0 = $67452301, h1 = $EFCDAB89, h2 = $98BADCFE, h3 = $10325476
    
  Local r[] = [7, 12, 17, 22,  7, 12, 17, 22,  7, 12, 17, 22,  7, 12, 17, 22,..
                5,  9, 14, 20,  5,  9, 14, 20,  5,  9, 14, 20,  5,  9, 14, 20,..
                4, 11, 16, 23,  4, 11, 16, 23,  4, 11, 16, 23,  4, 11, 16, 23,..
                6, 10, 15, 21,  6, 10, 15, 21,  6, 10, 15, 21,  6, 10, 15, 21]
                
  Local k[] = [$D76AA478, $E8C7B756, $242070DB, $C1BDCEEE, $F57C0FAF, $4787C62A,..
                $A8304613, $FD469501, $698098D8, $8B44F7AF, $FFFF5BB1, $895CD7BE,..
                $6B901122, $FD987193, $A679438E, $49B40821, $F61E2562, $C040B340,..
                $265E5A51, $E9B6C7AA, $D62F105D, $02441453, $D8A1E681, $E7D3FBC8,..
                $21E1CDE6, $C33707D6, $F4D50D87, $455A14ED, $A9E3E905, $FCEFA3F8,..
                $676F02D9, $8D2A4C8A, $FFFA3942, $8771F681, $6D9D6122, $FDE5380C,..
                $A4BEEA44, $4BDECFA9, $F6BB4B60, $BEBFBC70, $289B7EC6, $EAA127FA,..
                $D4EF3085, $04881D05, $D9D4D039, $E6DB99E5, $1FA27CF8, $C4AC5665,..
                $F4292244, $432AFF97, $AB9423A7, $FC93A039, $655B59C3, $8F0CCC92,..
                $FFEFF47D, $85845DD1, $6FA87E4F, $FE2CE6E0, $A3014314, $4E0811A1,..
                $F7537E82, $BD3AF235, $2AD7D2BB, $EB86D391]
                
  Local intCount = (((in$.length + 8) Shr 6) + 1) Shl 4
  Local data[intCount]
  
  For Local c=0 Until in$.length
    data[c Shr 2] = data[c Shr 2] | ((in$[c] & $FF) Shl ((c & 3) Shl 3))
  Next
  data[in$.length Shr 2] = data[in$.length Shr 2] | ($80 Shl ((in$.length & 3) Shl 3)) 
  data[data.length - 2] = (Long(in$.length) * 8) & $FFFFFFFF
  data[data.length - 1] = (Long(in$.length) * 8) Shr 32
  
  For Local chunkStart=0 Until intCount Step 16
    Local a = h0, b = h1, c = h2, d = h3
        
    For Local i=0 To 15
      Local f = d ~ (b & (c ~ d))
      Local t = d
      
      d = c ; c = b
      b = Rol((a + f + k[i] + data[chunkStart + i]), r[i]) + b
      a = t
    Next
    
    For Local i=16 To 31
      Local f = c ~ (d & (b ~ c))
      Local t = d

      d = c ; c = b
      b = Rol((a + f + k[i] + data[chunkStart + (((5 * i) + 1) & 15)]), r[i]) + b
      a = t
    Next
    
    For Local i=32 To 47
      Local f = b ~ c ~ d
      Local t = d
      
      d = c ; c = b
      b = Rol((a + f + k[i] + data[chunkStart + (((3 * i) + 5) & 15)]), r[i]) + b
      a = t
    Next
    
    For Local i=48 To 63
      Local f = c ~ (b | ~d)
      Local t = d
      
      d = c ; c = b
      b = Rol((a + f + k[i] + data[chunkStart + ((7 * i) & 15)]), r[i]) + b
      a = t
    Next
    
    h0 :+ a ; h1 :+ b
    h2 :+ c ; h3 :+ d
  Next
  
  Return (LEHex(h0) + LEHex(h1) + LEHex(h2) + LEHex(h3)).ToLower()  
End Function

Function SHA1$(in$)
  Local h0 = $67452301, h1 = $EFCDAB89, h2 = $98BADCFE, h3 = $10325476, h4 = $C3D2E1F0
  
  Local intCount = (((in$.length + 8) Shr 6) + 1) Shl 4
  Local data[intCount]
  
  For Local c=0 Until in$.length
    data[c Shr 2] = (data[c Shr 2] Shl 8) | (in$[c] & $FF)
  Next
  data[in$.length Shr 2] = ((data[in$.length Shr 2] Shl 8) | $80) Shl ((3 - (in$.length & 3)) Shl 3) 
  data[data.length - 2] = (Long(in$.length) * 8) Shr 32
  data[data.length - 1] = (Long(in$.length) * 8) & $FFFFFFFF
  
  For Local chunkStart=0 Until intCount Step 16
    Local a = h0, b = h1, c = h2, d = h3, e = h4

    Local w[] = data[chunkStart..chunkStart + 16]
    w = w[..80]
    
    For Local i=16 To 79
      w[i] = Rol(w[i - 3] ~ w[i - 8] ~ w[i - 14] ~ w[i - 16], 1)
    Next
    
    For Local i=0 To 19
      Local t = Rol(a, 5) + (d ~ (b & (c ~ d))) + e + $5A827999 + w[i]
      
      e = d ; d = c
      c = Rol(b, 30)
      b = a ; a = t
    Next
    
    For Local i=20 To 39
      Local t = Rol(a, 5) + (b ~ c ~ d) + e + $6ED9EBA1 + w[i]
      
      e = d ; d = c
      c = Rol(b, 30)
      b = a ; a = t
    Next
    
    For Local i=40 To 59
      Local t = Rol(a, 5) + ((b & c) | (d & (b | c))) + e + $8F1BBCDC + w[i]
      
      e = d ; d = c
      c = Rol(b, 30)
      b = a ; a = t
    Next

    For Local i=60 To 79
      Local t = Rol(a, 5) + (b ~ c ~ d) + e + $CA62C1D6 + w[i]
      
      e = d ; d = c
      c = Rol(b, 30)
      b = a ; a = t
    Next
    
    h0 :+ a ; h1 :+ b ; h2 :+ c
    h3 :+ d ; h4 :+ e
  Next
  
  Return (Hex(h0) + Hex(h1) + Hex(h2) + Hex(h3) + Hex(h4)).ToLower()  
End Function

Function SHA256$(in$)
  Local h0 = $6A09E667, h1 = $BB67AE85, h2 = $3C6EF372, h3 = $A54FF53A
  Local h4 = $510E527F, h5 = $9B05688C, h6 = $1F83D9AB, h7 = $5BE0CD19
  
  Local k[] = [$428A2F98, $71374491, $B5C0FBCF, $E9B5DBA5, $3956C25B, $59F111F1,..
                $923F82A4, $AB1C5ED5, $D807AA98, $12835B01, $243185BE, $550C7DC3,..
                $72BE5D74, $80DEB1FE, $9BDC06A7, $C19BF174, $E49B69C1, $EFBE4786,..
                $0FC19DC6, $240CA1CC, $2DE92C6F, $4A7484AA, $5CB0A9DC, $76F988DA,..
                $983E5152, $A831C66D, $B00327C8, $BF597FC7, $C6E00BF3, $D5A79147,..
                $06CA6351, $14292967, $27B70A85, $2E1B2138, $4D2C6DFC, $53380D13,..
                $650A7354, $766A0ABB, $81C2C92E, $92722C85, $A2BFE8A1, $A81A664B,..
                $C24B8B70, $C76C51A3, $D192E819, $D6990624, $F40E3585, $106AA070,..
                $19A4C116, $1E376C08, $2748774C, $34B0BCB5, $391C0CB3, $4ED8AA4A,..
                $5B9CCA4F, $682E6FF3, $748F82EE, $78A5636F, $84C87814, $8CC70208,..
                $90BEFFFA, $A4506CEB, $BEF9A3F7, $C67178F2]

  Local intCount = (((in$.length + 8) Shr 6) + 1) Shl 4
  Local data[intCount]
  
  For Local c=0 Until in$.length
    data[c Shr 2] = (data[c Shr 2] Shl 8) | (in$[c] & $FF)
  Next
  data[in$.length Shr 2] = ((data[in$.length Shr 2] Shl 8) | $80) Shl ((3 - (in$.length & 3)) Shl 3) 
  data[data.length - 2] = (Long(in$.length) * 8) Shr 32
  data[data.length - 1] = (Long(in$.length) * 8) & $FFFFFFFF
  
  For Local chunkStart=0 Until intCount Step 16
    Local a = h0, b = h1, c = h2, d = h3, e = h4, f = h5, g = h6, h = h7

    Local w[] = data[chunkStart..chunkStart + 16]
    w = w[..64]
    
    For Local i=16 To 63
      w[i] = w[i - 16] + (Ror(w[i - 15], 7) ~ Ror(w[i - 15], 18) ~ (w[i - 15] Shr 3))..
            + w[i - 7] + (Ror(w[i - 2], 17) ~ Ror(w[i - 2], 19) ~ (w[i - 2] Shr 10))
    Next
    
    For Local i=0 To 63
      Local t0 = (Ror(a, 2) ~ Ror(a, 13) ~ Ror(a, 22)) + ((a & b) | (b & c) | (c & a))
      Local t1 = h + (Ror(e, 6) ~ Ror(e, 11) ~ Ror(e, 25)) + ((e & f) | (~e & g)) + k[i] + w[i]
      
      h = g ; g = f ; f = e ; e = d + t1
      d = c ; c = b ; b = a ;  a = t0 + t1  
    Next
    
    h0 :+ a ; h1 :+ b ; h2 :+ c ; h3 :+ d
    h4 :+ e ; h5 :+ f ; h6 :+ g ; h7 :+ h
  Next
  
  Return (Hex(h0) + Hex(h1) + Hex(h2) + Hex(h3) + Hex(h4) + Hex(h5) + Hex(h6) + Hex(h7)).ToLower()  
End Function

Function Rol(val, shift)
  Return (val Shl shift) | (val Shr (32 - shift))
End Function

Function Ror(val, shift)
  Return (val Shr shift) | (val Shl (32 - shift))
End Function

Function LEHex$(val)
  Local out$ = Hex(val)
  
  Return out$[6..8] + out$[4..6] + out$[2..4] + out$[0..2]
End Function
