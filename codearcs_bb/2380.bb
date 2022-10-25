; ID: 2380
; Author: Otus
; Date: 2008-12-22 17:30:44
; Title: LZMA Streams
; Description: [BMX] Easily read and write to compressed streams

SuperStrict

Import BRL.BankStream
Import BRL.Stream

Import "lzma.bmx"

Rem
bbdoc: LZMA stream wrapper type
about:
#TLzmaStream wraps a raw stream and allows access to uncompressed data.

When writing, the data is compressed and the compressed daa written to the wrapped stream.
If compression expands the data, uncompressed data is written instead.

Changes in the raw stream don't automatically appear in a TLzmaStream 
- #ReadSync updates to the current raw stream, but any changes are lost.

Similarly, changes written to a TLzmaStream are only written to the raw stream 
on a Flush/FlushStream call or when the stream is closed.

Note: You may lose data if you fail to close/flush the stream before program ends.
Do not rely on the automatic Delete->Close call!
End Rem
Type TLzmaStream Extends TStreamWrapper
	
	Field _basestream:TStream
	
	Field _level:Int = 5
	
Rem
bbdoc: Closes the stream, writing any changes
End Rem
	Method Close()
		Flush()
		_basestream.Close()
		_stream.Close()
	End Method
	
Rem
bbdoc: Updates to current raw stream data
End Rem
	Method ReadSync()
		'Empty stream?
		If _basestream.Size()=0
			_stream = CreateBankStream(Null)
			Return
		End If
		
		'Copy stream contents to a bank
		Local b:TBank = CreateBank(_basestream.Size())
		CopyStream _basestream, CreateBankStream(b)
		
		'Set up bank for raw access
		Local buf:Byte Ptr = b.Lock()
		Local size:Int = b.Size()-4
		
		
		'Is this uncompressed data?
		Local usize:Int = Int Ptr(buf)[0] + 1
		If usize<=1
			Local u:TBank = CreateBank(-usize)
			Local ubuf:Byte Ptr = u.Lock()
			MemCopy ubuf, buf+4, -usize
			u.Unlock()
			_stream = CreateBankStream(u)
			Return
		End If
		
		
		'Create a bank for uncompressed data
		Local u:TBank = CreateBank(usize)
		Local ubuf:Byte Ptr = u.Lock()
		
		LzmaUncompress ubuf, usize, buf+4, size
		
		'Not valid LZMA?
		If usize <> u.Size()-1 Then Return
		
		u.Unlock()
		u.Resize(usize)
		
		_stream = CreateBankStream(u)
	End Method
	
Rem
bbdoc: Flushes current data to the raw stream
End Rem
	Method Flush()
		'Set up bank for raw access
		Local b:TBank = TBankStream(_stream)._bank
		Local bsize:Int = b.Size()
		Local buf:Byte Ptr = b.Lock()
		
		'Create bank for compressed data
		Local csize:Int = bsize + 1024
		Local c:TBank = CreateBank(csize)
		Local cbuf:Byte Ptr = c.Lock()
		
		LzmaCompress2 cbuf, csize, buf, bsize, _level
		
		_basestream.Seek 0
		
		'Does it fit? 
		If csize<b.Size()
			_basestream.WriteInt b.Size()
			_basestream.WriteBytes cbuf, csize
		Else
			'Write uncompressed
			_basestream.WriteInt -b.Size()
			_basestream.WriteBytes buf, b.Size()
		End If
		
		b.Unlock()
	End Method
	
	Function Create:TLzmaStream( stream:TStream )
		'Stream must be seekable
		If stream=Null Or stream.Seek(0)=-1 Then Return Null
		
		Local l:TLzmaStream = New TLzmaStream
		l._basestream = stream
		l.ReadSync()
		
		If Not l._stream Then Return Null
		
		Return l
	End Function
	
End Type

Rem
bbdoc: Opens #url as TLzmaStream
about:
An alternative to using OpenStream("lzma::-blah").
End Rem
Function CreateLzmaStream:TLzmaStream( url:Object )
	Return TLzmaStream.Create( OpenStream(url) )
End Function

New TLzmaStreamFactory

Type TLzmaStreamFactory Extends TStreamFactory
	
	Method CreateStream:TStream( url:Object,proto$,path$,readable%,writeable% )
		If proto<>"lzma" Then Return Null
		Local stream:TStream = OpenStream(path)
		Assert stream<>Null
		Return TLzmaStream.Create( stream )
	End Method
	
End Type
