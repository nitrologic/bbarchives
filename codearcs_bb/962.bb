; ID: 962
; Author: Klaas
; Date: 2004-03-11 09:22:54
; Title: gzip files and banks
; Description: uses zlib.dll

If Not FileType("zlib.dll")=1 Then RuntimeError("zlib.dll missing")

Function gZipFile$(org_file$,destination$="")
	source_bank=CreateBank(FileSize(org_file$))
	source_len=BankSize(source_bank)
	f=OpenFile(org_file$)
	If Not f
		Return "-ERROR cannot open source file"
	EndIf
	ReadBytes source_bank,f,0,BankSize(source_bank)
	CloseFile f
	
	new_file$=org_file$+".gz"
	
	file = zip_open(new_file$, "wb")
	If (file = 0)
		Return("-ERROR cannot open destination file")
	EndIf
	
	suc = zip_write(file,source_bank,source_len)
	If Not suc
		Return("-ERROR cannot compress file")
	EndIf
	file = zip_close(file)
	Return FileSize(new_file$)
End Function

Function gzip_adler32file(file$)
	f = ReadFile(file)
	If Not f Then Return False
	
	bufferSize = 4096
	buffer = CreateBank(bufferSize)
	adler = zip_adler32(0,buffer,0)
	While Not Eof(f)
		length = ReadBytes(buffer,f,0,bufferSize)
		adler = zip_adler32(adler,buffer,length)
	Wend
	CloseFile(f)
	Return adler
End Function

Function gzip_adler32bank(bank)
	adler = zip_adler32(0,bank,0)
	adler = zip_adler32(adler,bank,BankSize(bank))
	Return adler
End Function

Function gzip_compressBank(bank)
	sourceLen = BankSize(bank)
	dest = CreateBank(Ceil(sourceLen * 1.1)+12)
	destLen = CreateBank(4)
	PokeInt(destLen,0,BankSize(dest))
	If zip_compress(dest, destLen, bank,sourceLen) <> 0 Then Return False
	newLen = PeekInt(destLen,0)
	
	If newlen <= 0
		FreeBank destLen
		FreeBank dest
		Return False
	EndIf
	ResizeBank(bank,newLen)
	CopyBank(dest,0,bank,0,newlen)
	
	FreeBank destLen
	FreeBank dest
	Return bank
End Function

Function gzip_uncompressBank(bank,Length)
	sourceLen = BankSize(bank)
	dest = CreateBank(Length)
	destLen = CreateBank(4)
	PokeInt(destLen,0,Length)
	If zip_uncompress(dest, destLen, bank,sourceLen) <> 0 Then Return callback
	ResizeBank(bank,Length)
	CopyBank(dest,0,bank,0,Length)
	
	FreeBank destLen
	FreeBank dest
	Return bank
End Function
