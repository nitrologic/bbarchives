; ID: 2582
; Author: spacerat
; Date: 2009-09-13 15:55:46
; Title: RIFF Reader/Writer
; Description: Read from and write to files using the RIFF structure

SuperStrict

Rem
bbdoc: RIFF File Manipulator
about: The RIFF module allows easy read/write access to RIFF format files.
End Rem
Module JOE.RIFF


ModuleInfo "Version: 0.9.0"
ModuleInfo "Author: Joseph 'Spacerat' Atkins-Turkish"
ModuleInfo "License: Public Domain (see source for notes)"
ModuleInfo "History: 0.9.0 Release to blitzmax.com"

Rem
This module is public domain because it's pretty simple, but if you use it for anything you release
I would appreciate it if you send a link to your project to spacerat3004@gmail.com, just because I like to know (if) my stuff is being used.
EndRem

Import Brl.Filesystem
Import Brl.endianstream
Import Brl.linkedlist
Import brl.math



Rem
bbdoc: Basic RIFF Chunk type
about: The RIFF file structure is based on chunks, which all have a four character ID and contain binary data.
End Rem
Type RIFFChunk
	Field ID:String
	Field Data:TBank
	Field Parent:RIFFList
	
	Function CreateChunk:RIFFChunk(ID:String, Size:Int = 0)
		Local n:RIFFChunk = New RIFFChunk
		n.ID = FourChrString(ID)
		n.Data = CreateBank()
		Return n
	End Function
	
	Rem
	bbdoc: Fill the chunk with data from a stream
	EndRem
	Method Fill(stream:TStream, datasize:Long)
		Data = CreateBank()
		
		local bs:TBankStream = GetNewDataStream()
		
		bs.WriteString(stream.ReadString(datasize))
	End Method
	
	Rem
	bbdoc: Get the size in bytes of the chunk's data
	EndRem
	Method GetSize:Long()
		Return Data.Size()
	End Method
	
	Rem
	bbdoc: Return a new stream for easy access to the chunk's data
	EndRem
	Method GetNewDataStream:TBankStream()
		Return TBankStream.Create(Data)
	EndMethod
	
	Rem
	bbdoc: Write the chunk to a stream
	EndRem
	Method Write(stream:TStream)
		stream.WriteString(FourChrString(ID))
		Local s:Long = GetSize()
		WriteUnsignedInt32(stream, s)
		
		stream.WriteBytes(Data.Lock(), s)
		Data.Unlock()
		
		'Extra null byte
		If GetSize() / 2.0 <> ceil(GetSize() / 2.0)
			stream.WriteByte(0)
		EndIf
		
	End Method
	
	Rem
	bbdoc: Set the ID of the chunk
	EndRem
	Method SetName(fourchrs:String)
		ID = FourChrString(ID)
	End Method
	

	
EndType

Rem
bbdoc: RIFF List type
about: The RIIF list is a special type of chunk which contains subchunks.
EndRem
Type RIFFList Extends RIFFChunk
	Rem
	bbdoc: The subchunk list.
	about: This list contains all subchunks of the list in the order that they were read and will be written.
	EndRem
	Field Subchunks:TList = New TList
	Field ChunkType:String
	
	Function CreateList:RIFFList(FormType:String)
		Local n:RIFFList = New RIFFList
		n.ID = "LIST"
		n.ChunkType = FourChrString(FormType)
		Return n
	End Function
		
	Rem
	bbdoc: Fill the chunk list with data from a stream
	about: The stream data should consist of a a number of separate chunks in RIFF format.
	EndRem
	Method Fill(stream:TStream, datasize:Long)
		While datasize > 4
			Local nid:String = stream.ReadString(4)
			Local nsize:Long = ReadUnsignedInt32(stream)
			
			Local n:RIFFChunk
			
			If nid = "LIST"
				n = New RIFFList
				RIFFList(n).ChunkType = stream.ReadString(4)
				n.ID = nid
			Else
				n = New RIFFChunk
				n.ID = nid
			EndIf
			
			n.ID = nid
			n.Fill(stream, nsize)
			AddChunk(n)
						
			datasize:-(nsize + 8)
			If nsize / 2.0 <> ceil(nsize / 2.0)
				datasize:-1
				stream.ReadByte()
			EndIf
						
		Wend
	EndMethod
	rem
	bbdoc: Get the size of all chunks in the list, and the header
	endrem
	Method GetSize:Long()
		Local Size:Long
		For Local c:RIFFChunk = EachIn Subchunks
			Size = Size + c.GetSize() + 8
		Next
		
		Return Size + 4
	End Method
	
	Rem
	bbdoc: Write the list to a stream
	EndRem
	Method Write(stream:TStream)
		stream.WriteString(FourChrString(ID))
		WriteUnsignedInt32(stream, GetSize())
		stream.WriteString(FourChrString(ChunkType))
		
		For Local c:RIFFChunk = Eachin Subchunks
			c.Write(stream)
		Next
		
		'Extra null byte
		If GetSize() / 2.0 <> ceil(GetSize() / 2.0)
			stream.WriteByte(0)
		EndIf
				
	End Method
	
	Rem
	bbdoc: Add a chunk to the end of the chunk list
	EndRem
	Method AddChunk(chunk:RIFFChunk)
		Subchunks.AddLast(chunk)
		chunk.Parent = Self
	End Method
	
	Rem
	bbdoc: Add a new chunk containing string data
	returns: Returns the new chunk.
	EndRem
	Method AddTag:RIFFChunk(name:String, str:String)
		Local n:RIFFChunk = RIFFChunk.CreateChunk(name)
		AddChunk(n)
		n.GetNewDataStream().WriteString(str)
		Return n
	End Method
	
	Rem
	bbdoc: Add a new chunk to the list
	EndRem
	Method NewChunk:RIFFChunk(name:String)
		Local n:RIFFChunk = RIFFChunk.CreateChunk(name)
		AddChunk(n)
		Return n
	End Method
	
	
	Rem
	bbdoc: Add a new list to the list
	EndRem
	Method NewList:RIFFList(format:String)
		Local n:RIFFList = RIFFList.CreateList(format)
		AddChunk(n)
		Return n
	End Method
		
	Rem
	bbdoc: Find a chunk by its name
	about: Searches for and returns the first chunk found with an ID equal to @name,
	or in the case of list chunks, a chunk type equal to @name.
	
	The search is not recursive, and will not search in subchunks of subchunks.
	Endrem
	Method FindChunk:RIFFChunk(name:String)
		name = FourChrString(name)
		For local c:RIFFChunk = EachIn Subchunks
			
			If c.ID = name Return c
			IF RIFFList(c)
				if RIFFList(c).ChunkType = name Return c
			End If
			
		Next
	End Method
	
	Rem
	bbdoc: Set the list type of the list.
	EndRem
	Method SetName(fourchrs:String)
		ChunkType = FourChrString(ID)
	End Method	
	
	Method GetNewDataStream:TBankStream()
		Throw "Cannot create data streams for list chunks."
	End Method
	
EndType
Rem
bbdoc: The RIFF File type
about: A RIFF file is essentially one large list chunk, with a number of subchunks/lists.
It is entirely possible to construct, save and load RIFF files just using RIFFList, but
this type exists for conveniance.
EndRem
Type RIFFFile Extends RIFFList
	
	Function CreateFile:RIFFFile(FileFormat:String, LittleEndian:Int = True)
		Local n:RIFFFile = New RIFFFile
		n.ChunkType = FileFormat
		If LittleEndian = 1
			n.ID = "RIFF"
		Else
			n.ID = "RIFX"
		End If
		Return n
	EndFunction
	
	Rem
	bbdoc: Load a RIFF file from a url
	EndRem
	Function Load:RIFFFile(url:Object)
		Local stream:TStream = OpenStream(url)
		If Not(stream) Return Null
		
		Local n:RIFFFile = New RIFFFile
		
		Local id:String = stream.ReadString(4)
		Select id
			Case "RIFF"
				stream = LittleEndianStream(stream)
			Case "RIFX"
				stream = BigEndianStream(stream)
			Default
				Return Null
		End Select		
		
		n.ID = id
		Local nsize:Long = ReadUnsignedInt32(stream)
		n.ChunkType = stream.ReadString(4)
		
		n.Fill(stream, nsize)
		
		Return n
	End Function
	
	Rem
	bbdoc: Save the file to a stream or URL
	about: Files are not automatically created.
	EndRem
	Method Save(url:Object)
		Local s:TStream = OpenStream(url)
		If s = Null
		'	Print "Save error: null stream"
			Return
		End If
		Write(s)
	End Method

End Type

Rem
bbdoc: Create and initialise a new RIFF chunk
about: The @ID should be four characters, and is trimmed or padded otherwise.
EndRem
Function CreateRIFFChunk:RIFFChunk(ID:String, parent:RIFFList = Null)
	Local c:RIFFChunk = RIFFChunk.CreateChunk(ID, 0)
	If (parent) parent.AddChunk(c)
	Return c
EndFunction

Rem
bbdoc: Create and initialise a new RIFF LIST
about: The @FormType should be four characters, and is trimmed or padded otherwise.
EndRem
Function CreateRIFFList:RIFFList(FormType:String, parent:RIFFList = Null)
	Local c:RIFFList = RIFFList.CreateList(FormType)
	If (parent) parent.AddChunk(c)
	Return c
Endfunction

Rem
bbdoc: Create and initialise a new RIFF File
about: The @FileFormat should be four characters, and is trimmed or padded otherwise.

If @LittleEndian is true the file is a RIFF file, else it is a RIFX file.
EndRem
Function CreateRIFFFile:RIFFFile(FileFormat:String, LittleEndian:Int = True)
	Return RIFFFile.CreateFile(FileFormat, LittleEndian)
EndFunction

Rem
bbdoc: Load a RIFF file from a url
EndRem
Function LoadRIFFFile:RIFFFile(url:Object)
	Return RIFFFile.Load(url)
End Function

Rem
Some extra utility functions required for the module. You also might find them useful, espectially read/write unsignedInt32.
Also please let me know if you can think of a better way of doing it that doesn't involve longs.
EndRem

Function FourChrString:String(str:String)
	If str.Length = 4 Return str
	If str.Length > 4
		Return str[0..4]
	End If
	
	While str.Length < 4
		str = str + " "
	WEnd
	Return str
End Function

Function WriteUnsignedInt32(stream:TStream, l:Long)
	Local p:Int Ptr = Int Ptr VarPtr l
	
	?LittleEndian
	stream.WriteInt(p[0])
	?BigEndian
	stream.WriteInt(p[1])
	?

End Function

Function ReadUnsignedInt32:Long(stream:TStream)
	Local i:Int = stream.ReadInt()
	Local l:Long
	Local p:Int Ptr = Int Ptr VarPtr l
	?LittleEndian
	p[0] = i
	?BigEndian
	p[1] = i
	?
	Return l
End Function
