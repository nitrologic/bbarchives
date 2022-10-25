; ID: 2178
; Author: Retimer
; Date: 2008-01-05 19:42:06
; Title: Socket Reading
; Description: Prevent freezing or runtime errors

Function MReadByte:Byte(Socket:TSocket,Stream:TStream)
	If SocketReadAvail(Socket) >= 1
		Return ReadByte(Stream)
	Else
		CloseSocket(Socket)
		CloseStream(Stream)
		Return 0
	End If
End Function

Function MReadShort:Short(Socket:TSocket,Stream:TStream)
	If SocketReadAvail(Socket) >= 2
		Return ReadShort(Stream)
	Else
		CloseSocket(Socket)
		CloseStream(Stream)
		Return 0
	End If
End Function

Function MReadInt:Int(Socket:TSocket,Stream:TStream)
	If SocketReadAvail(Socket) >= 4
		Return ReadInt(Stream)
	Else
		CloseSocket(Socket)
		CloseStream(Stream)
		Return 0
	End If
End Function

Function MReadLong:Long(Socket:TSocket,Stream:TStream)
	If SocketReadAvail(Socket) >= 8
		Return ReadLong(Stream)
	Else
		CloseSocket(Socket)
		CloseStream(Stream)
		Return 0
	End If
End Function

Function MReadFloat:Float(Socket:TSocket,Stream:TStream)
	If SocketReadAvail(Socket) >= 4
		Return ReadFloat(Stream)
	Else
		CloseSocket(Socket)
		CloseStream(Stream)
		Return 0
	End If
End Function

Function MReadDouble:Double(Socket:TSocket,Stream:TStream)
	If SocketReadAvail(Socket) >= 8
		Return ReadDouble(Stream)
	Else
		CloseSocket(Socket)
		CloseStream(Stream)
		Return 0
	End If
End Function

Function MReadString:String(Socket:TSocket,Stream:TStream,length:Int)
	If SocketReadAvail(Socket) >= length
		Return ReadString(Stream,length)
	Else
		CloseSocket(Socket)
		CloseStream(Stream)
		Return ""
	End If
End Function

Function MReadLine:String(Socket:TSocket,Stream:TStream)
	If SocketReadAvail(Socket) >= 0
		Return ReadLine(Stream)
	Else
		CloseSocket(Socket)
		CloseStream(Stream)
		Return ""
	End If
End Function
