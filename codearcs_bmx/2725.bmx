; ID: 2725
; Author: Nate the Great
; Date: 2010-06-07 22:11:39
; Title: small encryption algorithm for ints
; Description: encrypts most files pretty securely

'make a file in notepad or whatever you like and call it myfile.bla and type in some random stuff... then this will encrypt/decrypt it

rs:TStream = ReadFile("myfile.bla")
ws:TStream = WriteFile("myfileencrypted.bla")
encrypt(rs,ws,"12340987437643987234")
CloseStream rs
CloseStream ws

rs:TStream = ReadFile("myfileencrypted.bla")
Print "now lets encrypt it!"
While Not Eof(rs)
	Print ReadByte(rs)
Wend
CloseStream rs

rs:TStream = ReadFile("myfileencrypted.bla")
ws:TStream = WriteFile("myfiledecrypted.bla")
decrypt(rs,ws,"12340987437643987234")
CloseStream rs
CloseStream ws

Print "now lets decrypt it!"
rs:TStream = ReadFile("myfiledecrypted.bla")
While Not Eof(rs)
	Print ReadByte(rs)
Wend
CloseStream rs

Function Encrypt(rstream:TStream,wstream:TStream,key:String)	'the key is a set of numbers any length... "432197843" or "324" or "3244325"

	Local cnt:Int
	Local n:Byte
	Local leng:Int = Len(key)
	While Not Eof(rstream)	
		n = (ReadByte(rstream) + Int(Mid(key,(cnt Mod leng)+1,1)))
		WriteByte(wstream,n)
		cnt:+1
	Wend
End Function

Function Decrypt(rstream:TStream,wstream:TStream,key:String)

	Local cnt:Int
	Local n:Byte
	Local leng:Int = Len(key)
	While Not Eof(rstream)	
		n = (ReadByte(rstream) - Int(Mid(key,(cnt Mod leng)+1,1)))		'the only difference in these functions is the minus sign...
		WriteByte(wstream,n)
		cnt:+1
	Wend
End Function
