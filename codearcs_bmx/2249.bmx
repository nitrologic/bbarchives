; ID: 2249
; Author: Helios
; Date: 2008-04-29 11:58:31
; Title: Pak file system
; Description: Allows the user to pak all resources together into a single encrypted/compressed file.

Rem

	This type allows us to implement the 'eachin' support for pak files.
	files.

End Rem
Type TPakFileEnum

	Field _index:Int
	Field _pak:TPakFile

	Method HasNext:Int()
		Return _index < _pak.FileCount() 
	End Method

	Method NextObject:Object()
		Local value:Object = TPakFileChunk(_pak._fileList.ValueAtIndex(_index)).URL
		_index :+ 1
		Return value
	End Method

End Type

Rem

	This type allows the user to request files from the pak framework by using the pak:: protocol
	when requesting streams.

End Rem
Type TPAKStreamFactory Extends TStreamFactory

	Method CreateStream:TStream(url:Object,proto:String,path:String,readable:Int,writeable:Int)
	
		If (proto.ToLower() = "pak")

			' If a physical file exists, override the pak file.
			If (FileType(path) = 1)
			
				Return OpenStream(path, readable, writeable)
			
			EndIf

			' Look for file in pak files.
			path = Replace(path, "/", "\")
			For Local pak:TPakFile = EachIn TPakFile.PakFilePool
			
				If (pak.ContainsFile(path))

					Return CreateBankStream(pak.GetFileBank(path))
					
				EndIf
			
			Next
			
		EndIf
	
	End Method

End Type

New TPAKStreamFactory

Rem

	This type stores details on an individual file chunk contained within a
	pak file.

End Rem
Type TPakFileChunk

	Field URL:String

	Field DataOffset:Int
	Field DataSize:Int

	Rem
		
		Saves this chunks data from a physical file to a bank, encrypts and compresses it, then
		returns it in the form of a bank.
		
	End Rem
	Method CompileDataBank:TBank(encrypted:Int, compressed:Int, encryptionKey:String)
	
		Local bank:TBank = LoadBank(URL)
		If (bank = Null) Return Null
		
		If (compressed = True)
			bank = CompressBank(bank)
		EndIf

		If (encrypted = True)
			bank = EncryptBank(bank, encryptionKey)		
		EndIf
		
		Return bank
	
	End Method
	
	Rem
	
		Loads this chunks data out of a given stream, encrypts and compresses it, then
		returns it in the form of a bank.
	
	End Rem
	Method GetDataBank:TBank(mainStream:TStream, encrypted:Int, compressed:Int, encryptionKey:String)

		' If this chunk is still waiting to be saved to a file, then
		' you can get its data just by loading it out of the physical file.
		If (DataSize = 0 And FileType(URL) = 1)
			Return LoadBank(URL)
		EndIf
		
		Local bank:TBank = CreateBank(DataSize)
		ReadBank(bank, mainStream, 0, DataSize)
		
		If (encrypted = True)
			bank = DecryptBank(bank, encryptionKey)
		EndIf
		
		If (compressed = True)
			bank = DecompressBank(bank)
		EndIf
		
		Return bank

	End Method

End Type

Rem

	This type stores all the code used to access, save and load multiple files into
	a single 'pak' file.
	
	Files can also be encrypted, and compressed for security purposes.

End Rem
Type TPakFile

	Global PakFilePool:TList = New TList

	Field Encrypted:Int
	Field EncryptionKey:String
	Field Compressed:Int
	
	Field _fileList:TList = New TList
	
	Field _mainDataStream:TStream = Null
	Field _mainDataOffset:Int = 0
	
	Rem
		
		Gets a bank that contains the data for the given file.
		
	End Rem
	Method GetFileBank:TBank(url:String)
	
		For Local file:TPakFileChunk = EachIn _fileList
			If (file.URL.ToLower() = url.ToLower())
				SeekStream(_mainDataStream, _mainDataOffset + file.DataOffset)
				Return file.GetDataBank(_mainDataStream, Encrypted, Compressed, EncryptionKey)
			EndIf 
		Next
	
	End Method
	
	Rem
		
		Returns the number of file chunks in this pak file.
		
	End Rem	
	Method FileCount:Int()
	
		Return _fileList.Count()
	
	End Method
	
	Rem
		
		Clears all files out of this pak file.
		
	End Rem		
	Method Clear()
	
		_fileList.Clear()
	
	End Method
	
	Rem
	
		Adds support to this type for enumeration. So that the user can use the eachin keyword
		to iterate through all files in this pak file.
	
	End Rem	
	Method ObjectEnumerator:TPakFileEnum()
		Local enum:TPakFileEnum = New TPakFileEnum
		enum._index = 0
		enum._pak = Self
		Return enum
	End Method
	
	Rem
	
		Adds a reference to a physical file into this pak file. The file must
		exist when and if Save is called so its data can be read. 
	
	End Rem	
	Method AddFile(url:String)
	
		Local file:TPakFileChunk = New TPakFileChunk
		file.URL = Replace(url, "/", "\")
		
		ListAddLast(_fileList, file)
	
	End Method
	
	Rem
	
		Removes a reference to a physical file from this pak file.
	
	End Rem	
	Method RemoveFile(url:String)
	
		For Local file:TPakFileChunk = EachIn _fileList
			If (file.URL.ToLower() = url.ToLower())
				ListRemove(_fileList, file)
			EndIf 
		Next

	End Method
	
	Rem
	
		Adds all the files within a directory into this pak file.
	
	End Rem
	Method AddDirectory(url:String)
	
		url = StripSlash(url) + "/"
	
		Local files:String[] = LoadDir(url, True)
		For Local file:String = EachIn files
		
			If (FileType(url + file) = 1)
		
				AddFile(url + file)
			
			Else If (FileType(url + file) = 2)
	
				AddDirectory(url + file + "/")
						
			EndIf
		
		Next
	
	End Method
	
	Rem
		
		Remove all the files within this pak file that are in the given directory.
		
	End Rem
	Method RemoveDirectory(url:String)

		For Local file:TPakFileChunk = EachIn _fileList
			If (StripSlash(ExtractDir(file.URL)).ToLower() = StripSlash(ExtractDir(url)).ToLower()) 
				ListRemove(_fileList, file)
			EndIf 
		Next	
	
	End Method
	
	Rem
	
		Returns true if the given file exists in this pak file. 
	
	End Rem	
	Method ContainsFile:Int(url:String)
	
		url = Replace(url, "/", "\")
	
		For Local file:TPakFileChunk = EachIn _fileList
			If (file.URL.ToLower() = url.ToLower())
				Return True
			EndIf 
		Next	
		
	
	End Method
	
	Rem
	
		Saves this pak file to a physical file. All file that are referenced in
		this pak file must exist for this function to operate correctly.
	
	End Rem	
	Method Save(url:Object)
	
		Local stream:TStream = WriteStream(url)
		
		' Write in the header contains details on how
		' this pak file is tored.
		WriteString(stream, "PAK")
		WriteByte(stream, Encrypted)
		WriteByte(stream, Compressed)
		WriteInt(stream, _fileList.Count())
		
		Local offset:Int = 0
		Local dataChunks:TList = New TList

		' Write out the file table as well as working out
		' file offsets and compiling the files data.
		For Local file:TPakFileChunk = EachIn _fileList
			
			' Compile this files data and add it to the chunk list.
			Local dataBank:TBank = file.CompileDataBank(Encrypted, Compressed, EncryptionKey)
			If (dataBank = Null)
				Continue
			EndIf
			ListAddLast(dataChunks, dataBank)

			' Write out the file table information for this file.
			WriteInt(stream, file.URL.Length)
			WriteString(stream, file.URL)
			WriteInt(stream, offset)
			WriteInt(stream, BankSize(dataBank))
			
			offset :+ BankSize(dataBank)

		Next

		' Write out the data block which contains all the files data chunks
		' appended together.
		For Local data:TBank = EachIn dataChunks
			WriteBank(data, stream, 0, BankSize(data))
		Next
		
		CloseStream(stream)
	
	End Method
	
	Rem
	
		Loads this pak file from a physical file. No file data will actually be loaded
		until the user requests it.
	
	End Rem	
	Method Load(url:Object)
	
		Local stream:TStream = ReadStream(url)
		If (stream = Null) Return

		' Make sure the header is there, if its not the file
		' has been corrupted and we can ignore loading it.
		If (ReadString(stream, 3) <> "PAK") Then Return
		Encrypted = ReadByte(stream)
		Compressed = ReadByte(stream)
		Local fileCount:Int = ReadInt(Stream)
		
		' Read in the file table.
		For Local fileIndex:Int = 0 To fileCount - 1

			Local url:String = ReadString(stream, ReadInt(stream))
			Local offset:Int = ReadInt(stream)
			Local size:Int = ReadInt(stream)
			
			Local chunk:TPakFileChunk = New TPakFileChunk
			chunk.URL = url
			chunk.DataOffset = offset
			chunk.DataSize = size
			ListAddLast(_fileList, chunk)

		Next
		
		_mainDataOffset = StreamPos(stream)
		_mainDataStream = stream
			
	End Method
	
	Rem
	
		Initializes a new instance of this pak file and adds it to the 
		main pak file pool.
		
	End Rem
	Method New()
	
		ListAddLast(PakFilePool, Self)
	
	End Method
	
	Rem
	
		Disposes of all resources held by this pak file.
		
	End Rem
	Method Dispose()
	
		If (_mainDataStream) 
			CloseStream(_mainDataStream)
			_mainDataStream = Null
		EndIf
		
		ClearList(_fileList)
		
		ListRemove(PakFilePool, Self)
	
	End Method

End Type

Rem

	Creates a new pak file instance.

End Rem
Function CreatePakFile:TPakFile()
	Return New TPakFile
End Function

Rem

	Loads a given pak file from the given url.
	
	url           : URL to load pak file from.
	encryptionKey : If the pak file is encrypted this is the 
					key that will be used to decrypt it.

End Rem
Function LoadPakFile:TPakFile(url:Object, encryptionKey:String = "")
	Local pak:TPakFile = New TPakFile
	pak.EncryptionKey = encryptionKey
	pak.Load(url)
	Return pak
End Function

Rem

	Saves a given pak file to the given url.
	
	pak 			: Pak file to save.
	url 			: URL to save pak file to.
	compress 		: If set to true the pak file will be compressed.
	encrypt 		: If set to true the pak file will be encrypted.
	encryptionKey 	: If the pak file is being encrypted this is the key it will be encrypted with.

End Rem
Function SavePakFile(pak:TPakFile, url:Object, compress:Int = False, encrypt:Int = False, encryptionKey:String = "") 
	pak.Compressed = compress
	pak.Encrypted = encrypt
	pak.EncryptionKey = encryptionKey
	pak.Save(url)
End Function

Rem

	Clears all files out of the given pak file.
	
	pak : Pak file to clear.

End Rem
Function ClearPakFile(pak:TPakFile)
	pak.Clear()
End Function

Rem

	Disposes of all resources in a given pak file.
	
	pak : Pak file to dispose.

End Rem
Function DisposePakFile(pak:TPakFile)
	pak.Dispose()
End Function

Rem

	Returns true if the pak file contains the given file.
	
	pak : Pak file to search.
	url : URL of file to search for.

End Rem
Function PakFileContainsFile:Int(pak:TPakFile, url:String)
	Return pak.ContainsFile(url)
End Function

Rem

	Adds the given physical file to the pak file. The file must exist
	when and if Save is called for it to be saved correctly.
	
	pak : Pak file to clear.
	url : URL of physical file to add to pak file.

End Rem
Function AddFileToPakFile(pak:TPakFile, url:String)
	pak.AddFile(url)
End Function

Rem
	
	Removes the given physical file from the pak file. 
	
	pak : Pak file to remove physical file from.
	url : URL of physical file to remove.
	
End Rem
Function RemoveFileFromPakFile(pak:TPakFile, url:String)
	pak.RemoveFile(url)
End Function

Rem

	Adds the given directory and all the files contained in it to the pak file. The directory
	and its files must exist when and if Save is called for it to be saved correctly.
	
	pak : Pak file to clear.
	url : URL of physical directory to add to pak file.

End Rem
Function AddDirToPakFile(pak:TPakFile, url:String)
	pak.AddDirectory(url)
End Function

Rem
	
	Removes the given physical directory from the pak file. 
	
	pak : Pak file to remove physical file from.
	url : URL of physical directory to remove.
	
End Rem
Function RemoveDirFromPakFile(pak:TPakFile, url:String)
	pak.RemoveDirectory(url)
End Function

Rem

	Returns the amount of physical files contained in the given pak file.

	pak : Pak file to count files.

End Rem
Function PakFileFileCount:Int(pak:TPakFile)
	Return pak.FileCount()
End Function

Rem

	Return a bank containing the data of the given physical file stored
	in the given pak file.
	
	pak : Pak file to extract data from.
	url : URL of physical file to retrieve data for.

End Rem
Function PakFileGetFileBank:TBank(pak:TPakFile, url:String)
	Return pak.GetFileBank(url)
End Function

Rem
	
	Looks through all pak files, and all physical files for the given resource. If it exists
	this function will return true, else false.
	
	url : URL of resource file to look for.
	
End Rem
Function FileExists:Int(url:String)

	url = Replace(url, "\", "/")

	If (FileType(url) = 1) Return True
	
	For Local pak:TPakFile = EachIn TPakFile.PakFilePool
		If (pak.ContainsFile(url))
			Return True
		EndIf
	Next
	
	Return False
	
End Function

Rem

	This function will compress a bank's data and return it in another bank.
	
	Credit to Mark Sibly for this code.

End Rem
Function CompressBank:TBank(bank:TBank)

	Local size:Int = bank.Size()
	Local out_size:Int = size + size / 10 + 32
	Local out:TBank = TBank.Create(out_size)
	
	compress(out.Buf() + 4, out_size, bank.Buf(), size)
	
	out.PokeByte(0, size)
	out.PokeByte(1, size Shr 8)
	out.PokeByte(2, size Shr 16)
	out.PokeByte(3, size Shr 24)
	out.Resize(out_size + 4)
	
	Return out
	
End Function

Rem

	This function will decompress a bank's data and returns it in another bank.
	
	Credit to Mark Sibly for this code.

End Rem
Function DecompressBank:TBank(bank:TBank)

	Local out_size:Int
	out_size :| (bank.PeekByte(0))
	out_size :| (bank.PeekByte(1) Shl 8)
	out_size :| (bank.PeekByte(2) Shl 16)
	out_size :| (bank.PeekByte(3) Shl 24)
	
	Local out:TBank = TBank.Create(out_size)
	uncompress(out.Buf(), out_size, bank.Buf() + 4, bank.Size() - 4)
	
	Return out

End Function

Rem

	This function will encrypt a bank using a simple XOr encryption algorithem and return
	the encrypted data in another bank.
	
	TODO: Use a less repetative, more powerfull encryption algorithem, XOr is near useless.

End Rem
Function EncryptBank:TBank(bank:TBank, key:String)

	Local cryptBank:TBank = CreateBank(BankSize(bank))

	For Local index:Int = 0 To BankSize(bank) - 1
	
		Local plain:Int = PeekByte(bank, index)
		Local keyIndex:Int = key[index Mod key.Length]
		
		PokeByte(cryptBank, index, plain ~ keyIndex) 
	
	Next

	Return cryptBank

End Function

Rem

	This function will decrypt a bank using a simple XOr encryption algorithem and return
	the decrypted data in another bank.
	
	TODO: Use a less repetative, more powerfull encryption algorithem, XOr is near useless.

End Rem
Function DecryptBank:TBank(bank:TBank, key:String)

	Return EncryptBank(bank, key) ' Encryption and decryption uses same algorithem.

End Function




Rem

     Compiling pak file example

End Rem

' Create a new pak file and set it up as compressed.
Local pak:TPakFile = New TPakFile 
pak.Compressed = True

' Add every file in our bin directory to the pak file.
pak.AddDirectory("Bin\")

' Save this pak file to a physical file.
pak.Save("game.dat")

' Dispose of resources held by pak file.
pak.Dispose()
	


Rem

     Using pak file example

End Rem

' Create a new pak file and set it up as compressed.
Local pak:TPakFile = New TPakFile 
pak.Compressed = True

' Load it from our file.
pak.Load("game.dat")

' Lets extract some resources from it.
local image:TImage = LoadImage("pak::Bin\Graphics\MyImage.png")
local sound:TSound = LoadSound("pak::Bin\Audio\MySound.wav")

' Dispose of resources held by pak file.
pak.Dispose()
