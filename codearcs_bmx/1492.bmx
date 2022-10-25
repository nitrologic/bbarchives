; ID: 1492
; Author: FlameDuck
; Date: 2005-10-19 11:46:19
; Title: PNG Header
; Description: A Type that describes the PNG Header

Strict

Import BRL.Stream
Import BRL.EndianStream

Type PNGHeader

	Field signiture:String
	Field chunksize:Int
	Field chunkID:String
	Field width:Int
	Field height:Int
	Const PNG_ID:String = Chr($89) + Chr($50) + Chr($4E) + Chr($47) + Chr($0D) + Chr($0A) + Chr($1A) + Chr($0A)

	Function fromFile:PNGHeader( url:Object )
		Local myStream:TStream = ReadStream( url )
		Local temp:PNGHeader
		If StreamSize (myStream) > 24
			temp = New PNGHeader
			Local eStream:TStream = BigEndianStream(myStream)
			temp.signiture = ReadString (eStream , 8)
			temp.chunksize = Readint (eStream)
			temp.chunkID = ReadString (eStream , 4)
			temp.width = Readint (eStream)
			temp.height = Readint (eStream)
			CloseStream eStream
		EndIf
		CloseStream myStream
		Return temp
	EndFunction	

	Method isPNG:Int()
		If signiture = PNG_ID
			Return True
		EndIf
		Return False
	EndMethod
	
	Method toString:String()
		Local temp:String = "isPng: "
		If isPNG()
			temp:+"True "
		Else
			temp:+"False "
		EndIf
		temp:+"Width: " + width + " Height: " + height
		
		Return temp
	EndMethod
EndType
