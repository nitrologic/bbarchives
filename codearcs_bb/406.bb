; ID: 406
; Author: denzilquixode
; Date: 2002-08-24 12:13:59
; Title: IPS Routines
; Description: International Patching System - creating and using IPS files.


; International Patching System (IPS) routines, v1.0
; Written by Duncan Cross, 24/08/2002

Dim IPSData(5)

Function ApplyIPSPatch(Source$, IPSFile$, Dest$)
	Dim IPSData(5)
	IPS = ReadFile(IPSFile$)
	CheckStr$ = ""
	For X = 1 To 5
		CheckStr$ = CheckStr$ + Chr( ReadByte(IPS) )
	Next
	If Upper$(CheckStr$) <> "PATCH" Then RuntimeError("Error: Not an IPS patchfile")
	CopyFile Source$, Dest$
	PatchedFile = OpenFile(Dest$)
	For X = 0 To 4
		IPSData(X) = ReadByte(IPS)
	Next
	Repeat

		Address = IPSData(2) + (IPSData(1) * $100) + (IPSData(0) * $10000)
		ByteCount = IPSData(4) + (IPSData(3) * $100)
		SeekFile PatchedFile, Address
		If ByteCount = 0
			RLE_Count = ( ReadByte(IPS) * $100 ) + ReadByte(IPS)
			RLE_Value = ReadByte(IPS)
			For X = 1 To RLE_Count
				WriteByte PatchedFile, RLE_Value
			Next
		Else
			For X = 1 To ByteCount
				WriteByte PatchedFile, ReadByte(IPS)
			Next
		End If
			
		For X = 0 To 4
			IPSData(X) = ReadByte(IPS)
		Next
		If IPSData(0)=Asc("E") And IPSData(1)=Asc("O") And IPSData(2)=Asc("F") Then Stopped = True
		If Eof(IPS) Then Stopped = True

	Until Stopped
	CloseFile(IPS)
	CloseFile(PatchedFile)
End Function

Function MakeIPSPatch(From_n$, To_n$, Dest$)
	Dim IPSData(5)
	IPS = WriteFile(Dest$)
	FromFile = ReadFile(From_n$)
	ToFile = ReadFile(To_n$)
	WriteByte IPS, Asc("P")
	WriteByte IPS, Asc("A")
	WriteByte IPS, Asc("T")
	WriteByte IPS, Asc("C")
	WriteByte IPS, Asc("H")

	Use_RLE = True

	While Eof(FromFile) = False

		FromByte = ReadByte(FromFile)
		ToByte = ReadByte(ToFile)

		If InBlock = False
			If FromByte <> ToByte Then
				FirstByte = ToByte
				BeginLoc = FilePos(FromFile) - 1
				InBlock = True
				BlockLength = 0
			End If
		End If

		If InBlock
			If ToByte <> FirstByte Then Use_RLE = False
			If FromByte = ToByte
				InBlock = False
				WriteByte IPS, (BeginLoc And $FF0000) / $10000
				WriteByte IPS, (BeginLoc And   $FF00) / $100
				WriteByte IPS, (BeginLoc And     $FF)
				If Use_RLE And BlockLength >= 3 ; If the block is less than 3 bytes,
					WriteByte IPS, 0            ; RLE is counter-productive.
					WriteByte IPS, 0
					WriteByte IPS, (BlockLength And $FF00) / $100
					WriteByte IPS, (BlockLength And   $FF)
					WriteByte IPS, FirstByte
				Else
					WriteByte IPS, (BlockLength And $FF00) / $100
					WriteByte IPS, (BlockLength And   $FF)
					SeekFile ToFile, BeginLoc
					For X = 1 To BlockLength
						WriteByte IPS, ReadByte(ToFile)
					Next
					ReadByte ToFile 
				End If
				Use_RLE = True
			Else
				BlockLength = BlockLength + 1
			End If
		End If

	Wend

	WriteByte IPS, Asc("E")
	WriteByte IPS, Asc("O")
	WriteByte IPS, Asc("F")
	CloseFile(IPS)
	CloseFile(ToFile)
	CloseFile(FromFile)
End Function
