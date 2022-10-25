; ID: 2558
; Author: JoshK
; Date: 2009-08-13 11:52:19
; Title: Bank utilities
; Description: Bank compression and encryption commands

SuperStrict

Import brl.bank
Import pub.zlib

Function CompressBank:TBank( bank:TBank, level:Int=6 )
	Local size:Int=bank.Size()
	Local out_size:Int=size+size/10+32
	Local out:TBank=TBank.Create( out_size )
	compress2 out.Buf()+4,out_size,bank.Buf(),size,level
	out.PokeByte 0,size
	out.PokeByte 1,size Shr 8
	out.PokeByte 2,size Shr 16
	out.PokeByte 3,size Shr 24
	out.Resize out_size+4
	Return out
End Function

Function DecompressBank:TBank( bank:TBank )
	Local out_size:Int
	out_size:|bank.PeekByte(0)
	out_size:|bank.PeekByte(1) Shl 8
	out_size:|bank.PeekByte(2) Shl 16
	out_size:|bank.PeekByte(3) Shl 24
	Local out:TBank=TBank.Create( out_size )
	uncompress out.Buf(),out_size,bank.Buf()+4,bank.Size()-4
	Return out
EndFunction

Function EncryptBank(bank:TBank,key:String)
	Local c$
	Local i:Int
	For i = 0 To bank.size()-1
		c = Chr$(PeekByte(bank,i))
		PokeByte(bank,i,Asc(XorCrypt(c,key))) 
	Next

	Function XorCrypt:String(str$, key$)
		Local ml%, pl%, i%, result$,k:Int,c:Byte
		ml% = str.length
		pl% = key.length
		For i = 0 Until ml
			c=str$[i]
			k=key$[i Mod pl]
			c = c ~ k
			result$ :+ Chr(c)
		Next 
		Return result$
	EndFunction

EndFunction

Function DecryptBank(bank:TBank,key:String)
	EncryptBank(bank,key)
EndFunction
