; ID: 1259
; Author: Vertex
; Date: 2005-01-16 09:04:54
; Title: Burrows-Wheeler-Transformation
; Description: Optimizing Huffman

Strict

Print "Start"
BWT_Encode("test.bmp", "test.bwt")
Print "End"
Print "Start"
BWT_Decode("test.bwt", "test2.bmp")
Print "End"

End

Function BWT_Encode(sFileIn:String, sFileOut:String)
	Local iIndex:Int
	Local iFileSize:Int, iEncodeEnd:Int, tStreamIn:TStream, tStreamOut:TStream
	Local tDataBlock:TBank, sFirst:String, sSortedTable:String[512]
	Local iFirstIndex:Int
	Local sAlphabet:String, bFind:Byte

	iFileSize = FileSize(sFileIn)
	iEncodeEnd = iFileSize-(iFileSize Mod 512)
	
	tStreamIn = ReadFile(sFileIn)
	If tStreamIn = Null Then
		Return False
	EndIf
    
	tStreamOut = WriteFile(sFileOut)
	If tStreamOut = Null Then
		CloseFile tStreamIn
		Return False
	EndIf
	
	WriteInt tStreamOut, iEncodeEnd
	
	tDataBlock = CreateBank(512)
	While StreamPos(tStreamIn) < iEncodeEnd
		ReadBank(tDataBlock, tStreamIn, 0, 512)
		
		sFirst = ""
		For iIndex = 0 To 511
			sFirst = sFirst+Chr(PeekByte(tDataBlock, iIndex))
		Next
		sSortedTable[0] = sFirst
		
		For iIndex = 1 To 511
			sSortedTable[iIndex] = sSortedTable[iIndex-1][1..512]+Chr(sSortedTable[iIndex-1][0])
		Next
		sSortedTable.Sort()
		
		sFirst = sFirst[1..512]+Chr(sFirst[0])
		For iIndex = 0 To 511
			If sSortedTable[iIndex] = sFirst Then
				iFirstIndex = iIndex
				Exit
			EndIf
		Next
		
		sAlphabet = ""
		For iIndex = 0 To 255
			sAlphabet = sAlphabet+Chr(iIndex)
		Next
		
		For iIndex = 0 To 511
			bFind = sSortedTable[iIndex][511]
			WriteByte tStreamOut, sAlphabet.Find(Chr(bFind))
			sAlphabet = Chr(bFind)+sAlphabet.Replace(Chr(bFind), "")
		Next
		WriteInt tStreamOut, iFirstIndex
		
	Wend
	
	While Not Eof(tStreamIn)
		WriteByte tStreamOut, ReadByte(tStreamIn)
	Wend
	
	CloseFile tStreamOut
	CloseFile tStreamIn
	
	Return True
End Function

Function BWT_Decode(sFileIn:String, sFileOut:String)
	Local iIndex:Int, iIndex2:Int, bChar:Byte
	Local iEncodeEnd:Int, tStreamIn:TStream, tStreamOut:TStream
	Local tDataBlock:TBank, iFirst:Int[512], iLast:Int[512], iTrans:Int[512]
	Local iFirstIndex:Int
	Local sAlphabet:String, bFind:Byte
	
	tStreamIn = ReadFile(sFileIn)
	If tStreamIn = Null Then
		Return False
	EndIf
    
	tStreamOut = WriteFile(sFileOut)
	If tStreamOut = Null Then
		CloseFile tStreamIn
		Return False
	EndIf
	
	iEncodeEnd = (ReadInt(tStreamIn)/512)*516
	
	tDataBlock = CreateBank(512)
	While StreamPos(tStreamIn) < iEncodeEnd
		ReadBank(tDataBlock, tStreamIn, 0, 512)
		iFirstIndex = ReadInt(tStreamIn)

		sAlphabet = ""
		For iIndex = 0 To 255
			sAlphabet = sAlphabet+Chr(iIndex)
		Next
		
		For iIndex = 0 To 511
			bFind = PeekByte(tDataBlock, iIndex)
			PokeByte tDataBlock, iIndex, sAlphabet[bFind]
			sAlphabet = Chr(sAlphabet[bFind])+sAlphabet.Replace(Chr(sAlphabet[bFind]), "")
		Next
		
		For iIndex = 0 To 511
			iFirst[iIndex] = PeekByte(tDataBlock, iIndex) 
			iLast[iIndex] = PeekByte(tDataBlock, iIndex) 
		Next
		
		For iIndex = 0 To 511
			iFirst[iIndex] = PeekByte(tDataBlock, iIndex) 
			iLast[iIndex] = PeekByte(tDataBlock, iIndex) 
		Next
		iFirst.Sort
		
		For iIndex = 0 To 511
			bChar = iFirst[iIndex]
			For iIndex2 = 0 To 511
				If iLast[iIndex2] = bChar Then
					iTrans[iIndex] = iIndex2
					iLast[iIndex2] = iLast[iIndex2]+256
					Exit
				EndIf
			Next
		Next

		iIndex2 = iFirstIndex
		For iIndex = 0 To 511
			WriteByte tStreamOut, iLast[iIndex2]-256
			iIndex2 = iTrans[iIndex2]
		Next
		
		FlushMem
	Wend

	While Not Eof(tStreamIn)
		WriteByte tStreamOut, ReadByte(tStreamIn)
	Wend
	
	CloseFile tStreamOut
	CloseFile tStreamIn
	
	Return True
End Function
