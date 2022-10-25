; ID: 1737
; Author: Byteemoz
; Date: 2006-06-20 03:58:24
; Title: ProgressStream
; Description: Hook-Functions for Streams

' ProgressStream.bmx

SuperStrict

Import BRL.Stream

Type TProgressStream Extends TStreamWrapper

	Field _readcount:Int = 0
	Field _writecount:Int = 0
	Field _lasthook:Int = 0

	Method Close()
		SetStream Null
	End Method

	Function Create:TProgressStream( stream:TStream )
		Local t:TProgressStream = New TProgressStream
		t.SetStream stream
		Return t
	End Function

	Method Read:Int( buf:Byte Ptr,count:Int )
		_readcount:+ count ; Progress()
		Return _stream.Read( buf, count ) 
	End Method

	Method Write:Int( buf:Byte Ptr,count:Int )
		_writecount:+ count ; Progress()
		Return _stream.Write( buf,count )
	End Method
	
	Method ReadByte:Int()
		_readcount:+ 1 ; Progress()
		Return _stream.ReadByte()
	End Method
	
	Method WriteByte( n:Int )
		_writecount:+ 1 ; Progress()
		_stream.WriteByte n
	End Method
	
	Method ReadShort:Int()
		_readcount:+ 2 ; Progress()
		Return _stream.ReadShort()
	End Method
	
	Method WriteShort( n:Int )
		_writecount:+ 2 ; Progress()
		_stream.WriteShort n
	End Method
	
	Method ReadInt:Int()
		_readcount:+ 4 ; Progress()
		Return _stream.ReadInt()
	End Method
	
	Method WriteInt( n:Int )
		_writecount:+ 4 ; Progress()
		_stream.WriteInt n
	End Method
	
	Method ReadFloat:Float()
		_readcount:+ 4 ; Progress()
		Return _stream.ReadFloat()
	End Method
	
	Method WriteFloat( n:Float )
		_writecount:+ 4 ; Progress()
		_stream.WriteFloat n
	End Method
	
	Method ReadDouble:Double()
		_readcount:+ 8 ; Progress()
		Return _stream.ReadDouble()
	End Method
	
	Method WriteDouble( n:Double )
		_writecount:+ 8 ; Progress()
		_stream.WriteDouble n
	End Method
	
	Method ReadLong:Long()
		_readcount:+ 8 ; Progress()
		Return _stream.ReadLong()
	End Method
	
	Method WriteLong( n:Long )
		_writecount:+ 8 ; Progress()
		_stream.WriteLong n
	End Method
	
	Method ReadLine$()
		Local t$ = _stream.ReadLine()
		_readcount:+ t.Length ; Progress()
		Return t$
	End Method
	
	Method WriteLine:Int( t$ )
		_writecount:+ t.Length ; Progress()
		Return _stream.WriteLine( t )
	End Method
	
	Method ReadString$( n:Int )
		_readcount:+ n ; Progress()
		Return _stream.ReadString( n )
	End Method
	
	Method WriteString( t$ )
		_writecount:+ t.Length ; Progress()
		_stream.WriteString t
	End Method
	
	Method Progress()
		If MilliSecs() - _lasthook < ProgressInterval Then Return
		ProgressHook _stream, _readcount, _writecount, ProgressContext
		_lasthook = MilliSecs()
	EndMethod
EndType

Function SetProgressStreamHook(Hook(Stream:TStream, Read:Int, Written:Int, Context:Object), Interval:Int = 100, Context:Object = Null)
	ProgressHook = Hook
	ProgressInterval = Interval
	ProgressContext = Context
EndFunction

Private
	
Type TProgressStreamFactory Extends TStreamFactory

	Method CreateStream:TStream(URL:Object, Proto:String, Path:String, Readable:Int, Writeable:Int)
		If Proto = "progress" Then
			Local stream:TStream=OpenStream(path,readable,writeable)
			If stream Return TProgressStream.Create(stream)
		EndIf
	End Method
End Type

New TProgressStreamFactory

Function NullProgressHook(Stream:TStream, Read:Int, Written:Int, Context:Object) ; EndFunction
Global ProgressHook(Stream:TStream, Read:Int, Written:Int, Context:Object) = NullProgressHook
Global ProgressInterval:Int = 100
Global ProgressContext:Object = Null
