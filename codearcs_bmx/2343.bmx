; ID: 2343
; Author: Firstdeathmaker
; Date: 2008-10-23 11:08:09
; Title: TCrypt
; Description: Type to use Xor and AES encryption

Rem
	Easy Cryptography module by Christian Gei&#65533;ler (c) 2008
	about: delivers easy to use encryption and decryption classes for bmax.
	
	Version 1.2
	- added TAesCrypt	

	Version 1.0
	- added TXorCrypt
	- added TCrypt Interface


	
End Rem




Rem
	bbdoc: Interface for encryption / decryption types
End Rem

Type TCrypt Abstract
	
	Rem
		bbdoc: ciphers a stream into another with the specified KEY
		also: setKey()
	End Rem
	Method cipher(in:TStream , out:TStream) Abstract
		
	Rem
		bbdoc: deciphers a stream into another with the specified KEY
		also: setKey()
	End Rem
	Method decipher(in:TStream , out:TStream) Abstract
		
	Rem
		bbdoc: sets the key for this encryption type
	End Rem
	Method setKey(key:String) Abstract

End Type



Rem
	bbdoc: Weak encryption type, uses XOR to encrypt files.
End Rem

Type TXorCrypt Extends TCrypt Final
	Field key:Int
	
	Method cipher(in:TStream , out:TStream) 
		If in=Null Or out = Null Return
		While Not Eof(in) 
			WriteInt(out,(ReadInt(in) ~ Self.key))
		Wend
	End Method
		

	Method decipher(in:TStream , out:TStream)
		If in = Null Or out = Null Return
		Self.cipher(in,out)
	End Method
		

	Method setKey(key:String) 
		Self.key = Int(key)
	End Method
End Type


Type TAESCrypt Extends TCrypt Final

	'SECURITY RELEVANT VAR's, need to be cleared after algorithm was performed
	Field Key:String
	Field expKey:Byte[,]
	Field State:Byte[,]
	'SECURITY VAR's end
	
	Field Nk:Byte 'choose between 4, 6 or 8, Keysize
	Field Nb:Byte 'choose between 4, 6 or 8, Blocksize
	
	Method New()
		Self.Nk = 4
		Self.Nb = 4
	End Method

	Method init() 
		calcNr()
		keyExpansion()
		Self.State = New Byte[4, Self.Nb]
	End Method
	
	Field Nr:Byte
	Method calcNr() 
		If Self.Nb = 8
			Nr = 14
		ElseIf Self.Nb = 6
			If Self.Nk < 8
				Nr = 12
			Else
				Nr = 14
			EndIf 
		Else
			If Self.Nk < 6
				Nr = 10
			ElseIf Self.Nk < 8
				Nr = 12
			Else
				Nr = 14
			EndIf
		EndIf
	End Method
	
	'256 
	Field sBox:Byte[] =    [Byte($63), Byte($7c), Byte($77), Byte($7b), Byte($f2), Byte($6b), Byte($6f), Byte($c5), Byte($30), Byte($01), Byte($67), Byte($2b), Byte($fe), Byte($d7), Byte($ab), Byte($76),Byte($ca), Byte($82), Byte($c9), Byte($7d), Byte($fa), Byte($59), Byte($47), Byte($f0), Byte($ad), Byte($d4), Byte($a2), Byte($af), Byte($9c), Byte($a4), Byte($72), Byte($c0),Byte($b7), Byte($fd), Byte($93), Byte($26), Byte($36), Byte($3f), Byte($f7), Byte($cc), Byte($34), Byte($a5), Byte($e5), Byte($f1), Byte($71), Byte($d8), Byte($31), Byte($15),Byte($04), Byte($c7), Byte($23), Byte($c3), Byte($18), Byte($96), Byte($05), Byte($9a), Byte($07), Byte($12), Byte($80), Byte($e2), Byte($eb), Byte($27), Byte($b2), Byte($75),Byte($09), Byte($83), Byte($2c), Byte($1a), Byte($1b), Byte($6e), Byte($5a), Byte($a0), Byte($52), Byte($3b), Byte($d6), Byte($b3), Byte($29), Byte($e3), Byte($2f), Byte($84),Byte($53), Byte($d1), Byte($00), Byte($ed), Byte($20), Byte($fc), Byte($b1), Byte($5b), Byte($6a), Byte($cb), Byte($be), Byte($39), Byte($4a), Byte($4c), Byte($58), Byte($cf),Byte($d0), Byte($ef), Byte($aa), Byte($fb), Byte($43), Byte($4d), Byte($33), Byte($85), Byte($45), Byte($f9), Byte($02), Byte($7f), Byte($50), Byte($3c), Byte($9f), Byte($a8),Byte($51), Byte($a3), Byte($40), Byte($8f), Byte($92), Byte($9d), Byte($38), Byte($f5), Byte($bc), Byte($b6), Byte($da), Byte($21), Byte($10), Byte($ff), Byte($f3), Byte($d2),Byte($cd), Byte($0c), Byte($13), Byte($ec), Byte($5f), Byte($97), Byte($44), Byte($17), Byte($c4), Byte($a7), Byte($7e), Byte($3d), Byte($64), Byte($5d), Byte($19), Byte($73),Byte($60), Byte($81), Byte($4f), Byte($dc), Byte($22), Byte($2a), Byte($90), Byte($88), Byte($46), Byte($ee), Byte($b8), Byte($14), Byte($de), Byte($5e), Byte($0b), Byte($db),Byte($e0), Byte($32), Byte($3a), Byte($0a), Byte($49), Byte($06), Byte($24), Byte($5c), Byte($c2), Byte($d3), Byte($ac), Byte($62), Byte($91), Byte($95), Byte($e4), Byte($79),Byte($e7), Byte($c8), Byte($37), Byte($6d), Byte($8d), Byte($d5), Byte($4e), Byte($a9), Byte($6c), Byte($56), Byte($f4), Byte($ea), Byte($65), Byte($7a), Byte($ae), Byte($08),Byte($ba), Byte($78), Byte($25), Byte($2e), Byte($1c), Byte($a6), Byte($b4), Byte($c6), Byte($e8), Byte($dd), Byte($74), Byte($1f), Byte($4b), Byte($bd), Byte($8b), Byte($8a),Byte($70), Byte($3e), Byte($b5), Byte($66), Byte($48), Byte($03), Byte($f6), Byte($0e), Byte($61), Byte($35), Byte($57), Byte($b9), Byte($86), Byte($c1), Byte($1d), Byte($9e),Byte($e1), Byte($f8), Byte($98), Byte($11), Byte($69), Byte($d9), Byte($8e), Byte($94), Byte($9b), Byte($1e), Byte($87), Byte($e9), Byte($ce), Byte($55), Byte($28), Byte($df),Byte($8c), Byte($a1), Byte($89), Byte($0d), Byte($bf), Byte($e6), Byte($42), Byte($68), Byte($41), Byte($99), Byte($2d), Byte($0f), Byte($b0), Byte($54), Byte($bb), Byte($16)]
	Field sBoxInvert:Byte[] = [Byte($52), Byte($09), Byte($6a), Byte($d5), Byte($30), Byte($36), Byte($a5), Byte($38), Byte($bf), Byte($40), Byte($a3), Byte($9e), Byte($81), Byte($f3), Byte($d7), Byte($fb), Byte($7c), Byte($e3), Byte($39), Byte($82), Byte($9b), Byte($2f), Byte($ff), Byte($87), Byte($34), Byte($8e), Byte($43), Byte($44), Byte($c4), Byte($de), Byte($e9), Byte($cb),Byte($54), Byte($7b), Byte($94), Byte($32), Byte($a6), Byte($c2), Byte($23), Byte($3d), Byte($ee), Byte($4c), Byte($95), Byte($0b), Byte($42), Byte($fa), Byte($c3), Byte($4e),Byte($08), Byte($2e), Byte($a1), Byte($66), Byte($28), Byte($d9), Byte($24), Byte($b2), Byte($76), Byte($5b), Byte($a2), Byte($49), Byte($6d), Byte($8b), Byte($d1), Byte($25),Byte($72), Byte($f8), Byte($f6), Byte($64), Byte($86), Byte($68), Byte($98), Byte($16), Byte($d4), Byte($a4), Byte($5c), Byte($cc), Byte($5d), Byte($65), Byte($b6), Byte($92),Byte($6c), Byte($70), Byte($48), Byte($50), Byte($fd), Byte($ed), Byte($b9), Byte($da), Byte($5e), Byte($15), Byte($46), Byte($57), Byte($a7), Byte($8d), Byte($9d), Byte($84),Byte($90), Byte($d8), Byte($ab), Byte($00), Byte($8c), Byte($bc), Byte($d3), Byte($0a), Byte($f7), Byte($e4), Byte($58), Byte($05), Byte($b8), Byte($b3), Byte($45), Byte($06),Byte($d0), Byte($2c), Byte($1e), Byte($8f), Byte($ca), Byte($3f), Byte($0f), Byte($02), Byte($c1), Byte($af), Byte($bd), Byte($03), Byte($01), Byte($13), Byte($8a), Byte($6b),Byte($3a), Byte($91), Byte($11), Byte($41), Byte($4f), Byte($67), Byte($dc), Byte($ea), Byte($97), Byte($f2), Byte($cf), Byte($ce), Byte($f0), Byte($b4), Byte($e6), Byte($73),Byte($96), Byte($ac), Byte($74), Byte($22), Byte($e7), Byte($ad), Byte($35), Byte($85), Byte($e2), Byte($f9), Byte($37), Byte($e8), Byte($1c), Byte($75), Byte($df), Byte($6e),Byte($47), Byte($f1), Byte($1a), Byte($71), Byte($1d), Byte($29), Byte($c5), Byte($89), Byte($6f), Byte($b7), Byte($62), Byte($0e), Byte($aa), Byte($18), Byte($be), Byte($1b),Byte($fc), Byte($56), Byte($3e), Byte($4b), Byte($c6), Byte($d2), Byte($79), Byte($20), Byte($9a), Byte($db), Byte($c0), Byte($fe), Byte($78), Byte($cd), Byte($5a), Byte($f4),Byte($1f), Byte($dd), Byte($a8), Byte($33), Byte($88), Byte($07), Byte($c7), Byte($31), Byte($b1), Byte($12), Byte($10), Byte($59), Byte($27), Byte($80), Byte($ec), Byte($5f),Byte($60), Byte($51), Byte($7f), Byte($a9), Byte($19), Byte($b5), Byte($4a), Byte($0d), Byte($2d), Byte($e5), Byte($7a), Byte($9f), Byte($93), Byte($c9), Byte($9c), Byte($ef),Byte($a0), Byte($e0), Byte($3b), Byte($4d), Byte($ae), Byte($2a), Byte($f5), Byte($b0), Byte($c8), Byte($eb), Byte($bb), Byte($3c), Byte($83), Byte($53), Byte($99), Byte($61),Byte($17), Byte($2b), Byte($04), Byte($7e), Byte($ba), Byte($77), Byte($d6), Byte($26), Byte($e1), Byte($69), Byte($14), Byte($63), Byte($55), Byte($21), Byte($0c), Byte($7d)]
	'255
	Field Rcon:Byte[] = [Byte($8d), Byte($01), Byte($02), Byte($04), Byte($08), Byte($10), Byte($20), Byte($40), Byte($80), Byte($1b), Byte($36), Byte($6c), Byte($d8),Byte($ab), Byte($4d), Byte($9a), Byte($2f), Byte($5e), Byte($bc), Byte($63), Byte($c6), Byte($97), Byte($35), Byte($6a), Byte($d4), Byte($b3),Byte($7d), Byte($fa), Byte($ef), Byte($c5), Byte($91), Byte($39), Byte($72), Byte($e4), Byte($d3), Byte($bd), Byte($61), Byte($c2), Byte($9f),Byte($25), Byte($4a), Byte($94), Byte($33), Byte($66), Byte($cc), Byte($83), Byte($1d), Byte($3a), Byte($74), Byte($e8), Byte($cb), Byte($8d),Byte($01), Byte($02), Byte($04), Byte($08), Byte($10), Byte($20), Byte($40), Byte($80), Byte($1b), Byte($36), Byte($6c), Byte($d8), Byte($ab),Byte($4d), Byte($9a), Byte($2f), Byte($5e), Byte($bc), Byte($63), Byte($c6), Byte($97), Byte($35), Byte($6a), Byte($d4), Byte($b3), Byte($7d),Byte($fa), Byte($ef), Byte($c5), Byte($91), Byte($39), Byte($72), Byte($e4), Byte($d3), Byte($bd), Byte($61), Byte($c2), Byte($9f), Byte($25),Byte($4a), Byte($94), Byte($33), Byte($66), Byte($cc), Byte($83), Byte($1d), Byte($3a), Byte($74), Byte($e8), Byte($cb), Byte($8d), Byte($01),Byte($02), Byte($04), Byte($08), Byte($10), Byte($20), Byte($40), Byte($80), Byte($1b), Byte($36), Byte($6c), Byte($d8), Byte($ab), Byte($4d),Byte($9a), Byte($2f), Byte($5e), Byte($bc), Byte($63), Byte($c6), Byte($97), Byte($35), Byte($6a), Byte($d4), Byte($b3), Byte($7d), Byte($fa),Byte($ef), Byte($c5), Byte($91), Byte($39), Byte($72), Byte($e4), Byte($d3), Byte($bd), Byte($61), Byte($c2), Byte($9f), Byte($25), Byte($4a),Byte($94), Byte($33), Byte($66), Byte($cc), Byte($83), Byte($1d), Byte($3a), Byte($74), Byte($e8), Byte($cb), Byte($8d), Byte($01), Byte($02),Byte($04), Byte($08), Byte($10), Byte($20), Byte($40), Byte($80), Byte($1b), Byte($36), Byte($6c), Byte($d8), Byte($ab), Byte($4d), Byte($9a),Byte($2f), Byte($5e), Byte($bc), Byte($63), Byte($c6), Byte($97), Byte($35), Byte($6a), Byte($d4), Byte($b3), Byte($7d), Byte($fa), Byte($ef),Byte($c5), Byte($91), Byte($39), Byte($72), Byte($e4), Byte($d3), Byte($bd), Byte($61), Byte($c2), Byte($9f), Byte($25), Byte($4a), Byte($94),Byte($33), Byte($66), Byte($cc), Byte($83), Byte($1d), Byte($3a), Byte($74), Byte($e8), Byte($cb), Byte($8d), Byte($01), Byte($02), Byte($04),Byte($08), Byte($10), Byte($20), Byte($40), Byte($80), Byte($1b), Byte($36), Byte($6c), Byte($d8), Byte($ab), Byte($4d), Byte($9a), Byte($2f),Byte($5e), Byte($bc), Byte($63), Byte($c6), Byte($97), Byte($35), Byte($6a), Byte($d4), Byte($b3), Byte($7d), Byte($fa), Byte($ef), Byte($c5),Byte($91), Byte($39), Byte($72), Byte($e4), Byte($d3), Byte($bd), Byte($61), Byte($c2), Byte($9f), Byte($25), Byte($4a), Byte($94), Byte($33),Byte($66), Byte($cc), Byte($83), Byte($1d), Byte($3a), Byte($74), Byte($e8), Byte($cb)]

	

	Method galois_multiplication:Byte(a:Byte , b:Byte) 
		Local p:Byte
		Local hi_bit_set:Byte
		
		For Local counter:Byte = 0 To 7
			If (b & 1) = 1 p:~ a
			
			hi_bit_set = a ~ $80
			a = a Shl 1
			
			If hi_bit_set = $80 a:~ $1b
			b = b Shr 1
		Next	
		Return p
	End Method

	Method gm:Byte(a:Byte , b:Byte) 
		Return galois_multiplication(a , b) 
	End Method

	
	Rem
		bbdoc: calculates the expanded key from the normal key.
	End Rem
	Method keyExpansion:Byte()
		Local expKeySize:Int = Self.Nb * (Self.Nr + 1)
		DebugLog "expand key to " + expKeySize + " with " + Nr + " Nr."
		Self.expKey = New Byte[4, expKeySize]
		'fill array...
		Local keyTmp:String = Self.Key
		For Local i:Int = 0 Until Self.Nk
			Self.expKey[0, i] = Asc(keyTmp)
			keyTmp = Mid(keyTmp, 1)
			Self.expKey[1, i] = Asc(keyTmp)
			keyTmp = Mid(keyTmp, 1)
			Self.expKey[2, i] = Asc(keyTmp)
			keyTmp = Mid(keyTmp, 1)
			Self.expKey[3, i] = Asc(keyTmp)
			keyTmp = Mid(keyTmp, 1)
		Next
		Local round:Byte = 0
		For Local i:Int = Self.Nk Until expKeySize
			If (i Mod Self.Nk) > 0
				Self.expKey[0, i] = Self.expKey[0, i - 1] ~ Self.expKey[0, i - Self.Nb]
				Self.expKey[1, i] = Self.expKey[1, i - 1] ~ Self.expKey[1, i - Self.Nb]
				Self.expKey[2, i] = Self.expKey[2, i - 1] ~ Self.expKey[2, i - Self.Nb]
				Self.expKey[3, i] = Self.expKey[3, i - 1] ~ Self.expKey[3, i - Self.Nb]
			Else
				round:+1
				Local tmpWord:Byte[] = [Self.expKey[0, i - 1], Self.expKey[1, i - 1], Self.expKey[2, i - 1], Self.expKey[3, i - 1] ]
				tmpWord = Self.rotWord(tmpWord)
				tmpWord = Self.subWord(tmpWord)
				tmpWord[0] = Self.rCon[round] ~ tmpWord[0]
				
				Self.expKey[0, i] = tmpWord[0]
				Self.expKey[1, i] = tmpWord[1]
				Self.expKey[2, i] = tmpWord[2]
				Self.expKey[3, i] = tmpWord[3]
			EndIf
		Next
	End Method
	
	Rem
		bbdoc: support function for keyExpansion. 
		about: Rotates a word 1 to the left
	end rem
	Method rotWord:Byte[] (word:Byte[])
		Local tmp:Byte = word[0]
		word[0] = word[1]
		word[1] = word[2]
		word[2] = word[3]
		word[3] = tmp
		Return word
	End Method
	
	Rem
		bbdoc: support function for keyExpansion.
		about: substitutes bytes with sBox entries.
	end rem
	Method subWord:Byte[] (word:Byte[])
		word[0] = Self.sBox[word[0] ]
		word[1] = Self.sBox[word[1] ]
		word[2] = Self.sBox[word[2] ]
		word[3] = Self.sBox[word[3] ]
		Return word
	End Method
	
	
	
	'cipher methods
	
	Method rijndael()
		addRoundKey(0)
		For Local i:Int = 1 Until Self.Nr
			round(i)
		Next
		finalRound()
	End Method
	
	Method inv_rijndael()
		addRoundKey(Self.nr)
		For Local i:Int = 1 Until Self.Nr
			inv_round(i)
		Next
		inv_finalRound()
	End Method
	

	'#####################################
	Method round(nr:Int)
		byteSub()
		shiftRow()
		mixColumns()
		addRoundKey(nr)
	End Method
	
	Method inv_round(nr:Int)
		inv_shiftRow()
		inv_byteSub()
		addRoundKey(Self.nr - nr)
		inv_mixColumns()
	End Method
	'###############################
	
	Method finalRound()
		byteSub()
		shiftRow()
		addRoundKey(Self.Nr)
	End Method

	Method inv_finalRound()
		inv_shiftRow() 
		inv_byteSub() 
		addRoundKey(0)
	End Method


	Rem
		bbdoc: adds the round-key to the current state-block
	End Rem
	Method addRoundKey(roundnr:Byte)
		roundnr:*Self.Nb
		Local newState:Byte[4, Self.Nb]
		For Local i:Byte = 0 Until Self.Nb
			newState[0, i] = State[0, i] ~ expKey[0, i + roundnr]
			newState[1, i] = State[1, i] ~ expKey[1, i + roundnr]
			newState[2, i] = State[2, i] ~ expKey[2, i + roundnr]
			newState[3, i] = State[3, i] ~ expKey[3, i + roundnr]
		Next
		Self.State = newState
	End Method
	
	Rem
		bbdoc: substitutes the state-bytes with values in the s-box
	end rem
	Method byteSub()
		For Local i:Int = 0 Until Nb
			Self.State[0, i] = sBox[Self.State[0, i] ]
			Self.State[1, i] = sBox[Self.State[1, i] ]
			Self.State[2, i] = sBox[Self.State[2, i] ]
			Self.State[3, i] = sBox[Self.State[3, i] ]
		Next
	End Method
	
	Rem
		bbdoc: substitutes the state-bytes with values in the s-box
	end rem
	Method inv_byteSub()
		For Local i:Int = 0 Until Nb
			Self.State[0, i] = sBoxInvert[Self.State[0, i] ]
			Self.State[1, i] = sBoxInvert[Self.State[1, i] ]
			Self.State[2, i] = sBoxInvert[Self.State[2, i] ]
			Self.State[3, i] = sBoxInvert[Self.State[3, i] ]
		Next
	End Method
	
	
	Method shiftRow()
		Local newState:Byte[4, Self.Nk]
		If Self.Nb = 4 Or Self.Nb = 6
			For Local i:Byte = 0 Until Self.Nk
				newState[0,i] = State[0,i]
				newState[1, i] = State[1, ((i + 1) Mod Self.Nk)]
				newState[2, i] = State[2, ((i + 2) Mod Self.Nk)]
				newState[3, i] = State[3, ((i + 3) Mod Self.Nk)]
			Next
		Else
			For Local i:Byte = 0 Until Self.Nk
				newState[0,i] = State[0,i]
				newState[1, i] = State[1, ((i + 1) Mod Self.Nk)]
				newState[2, i] = State[2, ((i + 3) Mod Self.Nk)]
				newState[3, i] = State[3, ((i + 4) Mod Self.Nk)]
			Next
		EndIf
		Self.State = newState
	End Method
	
	Method inv_shiftRow()
		Local newState:Byte[4, Self.Nk]
		If Self.Nb = 4 Or Self.Nb = 6
			For Local i:Byte = 0 Until Self.Nk
				newState[0,i] = State[0,i]
				newState[1, ((i + 1) Mod Self.Nk)] = State[1, i]
				newState[2, ((i + 2) Mod Self.Nk)] = State[2, i]
				newState[3, ((i + 3) Mod Self.Nk)] = State[3, i]
			Next
		Else
			For Local i:Byte = 0 Until Self.Nk
				newState[0,i] = State[0,i]
				newState[1, ((i + 1) Mod Self.Nk)] = State[1, i]
				newState[2, ((i + 3) Mod Self.Nk)] = State[2, i]
				newState[3, ((i + 4) Mod Self.Nk)] = State[3, i]
			Next
		EndIf
		Self.State = newState
	End Method
	
	
	Rem
		bbdoc: rotates row of specified state block n to the right
	end rem
	Method mixColumns()
		Local newState:Byte[4, Self.Nk]
		For Local i:Byte = 0 Until Self.Nk
			newState[0, i] = gm(2,State[0, i]) ~ gm(3,State[1, i]) ~ gm(1,State[2, i]) ~ gm(1,State[3, i])
			newState[1, i] = gm(1,State[0, i]) ~ gm(2,State[1, i]) ~ gm(3,State[2, i]) ~ gm(1,State[3, i])
			newState[2, i] = gm(1,State[0, i]) ~ gm(1,State[1, i]) ~ gm(2,State[2, i]) ~ gm(3,State[3, i])
			newState[3, i] = gm(3,State[0, i]) ~ gm(1,State[1, i]) ~ gm(1,State[2, i]) ~ gm(2,State[3, i])
		Next
		Self.State = newState
	End Method
	
	Rem
		bbdoc: rotates row of specified state block n to the right
	end rem
	Method inv_mixColumns()
		Local newState:Byte[4, Self.Nk]
		For Local i:Byte = 0 Until Self.Nk
			newState[0, i] = gm($0e , State[0, i]) ~ gm($0b , State[1, i]) ~ gm($0d , State[2, i]) ~ gm($09 , State[3, i])
			newState[1, i] = gm($09 , State[0, i]) ~ gm($0e , State[1, i]) ~ gm($0b , State[2, i]) ~ gm($0d , State[3, i])
			newState[2, i] = gm($0d , State[0, i]) ~ gm($09 , State[1, i]) ~ gm($0e , State[2, i]) ~ gm($0b , State[3, i])
			newState[3, i] = gm($0b , State[0, i]) ~ gm($0d , State[1, i]) ~ gm($09 , State[2, i]) ~ gm($0e , State[3, i])
		Next
		Self.State = newState
	End Method
	
	
	Method cipher(in:TStream, out:TStream)
		DebugLog "cipher with AES, init"
		Self.Init()
		
		If Self.Nb = 0
			DebugLog "Nb = 0!"
			Return
		EndIf

		
		While Not Eof(in)
			For Local i:Int = 0 Until Self.Nb
				Self.State[0, i] = ReadByte(in)
				Self.State[1, i] = ReadByte(in)
				Self.State[2, i] = ReadByte(in)
				Self.State[3, i] = ReadByte(in)
			Next
			Self.rijndael()
			For Local i:Int = 0 Until Self.Nb	
				WriteByte(out, Self.State[0, i])
				WriteByte(out, Self.State[1, i])
				WriteByte(out, Self.State[2, i])
				WriteByte(out ,Self.State[3 ,i]) 
			Next
		Wend
	End Method
		
	Method decipher(in:TStream, out:TStream)
		DebugLog "decipher with AES, init"
		Self.Init()
		
		If Self.Nb = 0
			DebugLog "Nb = 0!"
			Return
		EndIf
		
		While Not Eof(in)
			For Local i:Int = 0 Until Self.Nb
				Self.State[0, i] = ReadByte(in)
				Self.State[1, i] = ReadByte(in)
				Self.State[2, i] = ReadByte(in)
				Self.State[3, i] = ReadByte(in)
			Next
			Self.inv_rijndael()
			For Local i:Int = 0 Until Self.Nb
				WriteByte(out, Self.State[0, i])
				WriteByte(out, Self.State[1, i])
				WriteByte(out, Self.State[2, i])
				WriteByte(out, Self.State[3, i])
			Next
		Wend
	End Method
	
	Method setKey(key:String) 
		Self.Key = Key
	End Method
End Type
