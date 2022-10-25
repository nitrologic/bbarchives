; ID: 196
; Author: Milky Joe
; Date: 2002-01-16 20:31:04
; Title: Huffman Expand
; Description: Use in conjunction with the compress routines (below) to decompress your files/data

; Title:		Curve Compression (EXPAND)
; Author:		Leigh Bowers
;			(c) Copyright 2001 Curve Software
; Version:	1.0
; Distribution:	Free for non commercial use
;
; Email:		leigh.bowers@curvesoftware.co.uk
; WWW:		www.curvesoftware.co.uk/blitz

Type TreeNode
    Field Weight%
    Field SavedWeight%
    Field Child1%
    Field Child0%
End Type

Type BitFile
    Field Mask%
    Field Rack%
End Type

Const SRCCOPY% = $CC0020
Const ENDOFSTREAM% = 256
Const ENDWEIGHTSTREAM% = $FFF

Dim Nodes.TreeNode(514)

Global Bitio.BitFile

; --------------
; Example Usage
; --------------

s$ = "packed.dat"
Print "Curve Compression - Expand"
Print ""
Print "Packed length:" + FileSize(s)
Start# = MilliSecs()
;
If ExpandFile (s, "dest.bmp") = 0 Then Print "Expand failed!"
;
Finish# = MilliSecs()
Print "Expanded length:" + FileSize("dest.bmp")
Print ((Finish - Start)/1000) + " seconds taken."
WaitKey

End

; --------------

Function InitHuffman()

	Local i%

	For i = 0 To 514 : Nodes.TreeNode(i) = New TreeNode : Next
	bitio.bitfile = New bitfile
	Bitio\Mask = $80

End Function

Function InputCounts%(hFileIn%)

	Local FirstNode%, LastNode%, w%, i%
	
	lExpandedSize% = ReadInt(hFileIn) ; Retrieve the size of the expanded (original) file
    
	If Eof(hFileIn) Then Return 0 Else FirstNode = ReadShort(hFileIn)
	If Eof(hFileIn) Then Return 0 Else LastNode = ReadShort(hFileIn)

	Repeat
		For i = FirstNode To LastNode
			If Eof(hFileIn) Then
				Return 0
			Else
				w = ReadShort (hFileIn)
				Nodes(i)\Weight = w
			End If
		Next
		If Eof(hFileIn) Then Return 0 Else FirstNode = ReadShort(hFileIn)
		If FirstNode <> EndWeightStream Then
			If Eof(hFileIn) Then Return 0 Else LastNode = ReadShort(hFileIn)
		End If	
	Until FirstNode = EndWeightStream

	Nodes(EndOfStream)\Weight = 1

	Return lExpandedSize

End Function

Function BuildTree%()

	Local iNextFree%, i%, Min1%, Min2%
    
	Nodes(513)\Weight = $7FFF
	For iNextFree = (EndOfStream + 1) To 514

		Min1 = 513
		Min2 = 513
		For i = 0 To iNextFree - 1
			If Nodes(i)\Weight <> 0 Then
				If Nodes(i)\Weight < Nodes(Min1)\Weight Then
					Min2 = Min1
					Min1 = i
				Else
					If Nodes(i)\Weight < Nodes(Min2)\Weight Then Min2 = i
				End If
			End If
		Next
		If Min2 = 513 Then Exit

		Nodes(iNextFree)\Weight = Nodes(Min1)\Weight + Nodes(Min2)\Weight
		Nodes(Min1)\SavedWeight = Nodes(Min1)\Weight
		Nodes(Min1)\Weight = 0
		Nodes(Min2)\SavedWeight = Nodes(Min2)\Weight
		Nodes(Min2)\Weight = 0
		Nodes(iNextFree)\Child0 = Min1
		Nodes(iNextFree)\Child1 = Min2

	Next
	iNextFree = iNextFree - 1
	Nodes(iNextFree)\SavedWeight = Nodes(iNextFree)\Weight

	Return iNextFree

End Function

Function ExpandData(hFileIn%, hFileOut%, iRootNode%, lDestBank%)

	Local Node%, lDestPos%

	Bitio\Mask = $80
	lDestPos = 0

	Repeat

		Node = iRootNode

		Repeat
			If InputBit(hFileIn) = True Then Node = Nodes(Node)\Child1 Else Node = Nodes(Node)\Child0
		Until Node <= EndOfStream

		If Node <> EndOfStream Then
			PokeByte lDestBank, lDestPos, Node
			lDestPos = lDestPos + 1
		End If

	Until Node = EndOfStream

End Function

Function InputBit%(hFileIn%)

	Local Value%
    
	If Bitio\Mask = $80 Then Bitio\Rack = ReadByte(hFileIn)
	Value = Bitio\Rack And Bitio\Mask
	Bitio\Mask = Bitio\Mask / 2
	If Bitio\Mask = 0 Then Bitio\Mask = $80

	If Value <> 0 Then Return True Else Return False

End Function

Function ExpandFile%(sSource$, sDest$)

	Local lExpandedSize%, iRootNode%, hFileIn%, hFileOut%

	InitHuffman
	
	; Open packed file
	
	hFileIn = ReadFile(sSource)
	
	; Expand the Source file to the Destination file
	
	lExpandedSize = InputCounts(hFileIn) ; Import the header information (expanded size & counts)
	If lExpandedSize  = 0 Then
		Return ; Error with retrieving counts
	Else
		; Create bank of size "lExpandedSize" (expanded data is written to this)
		lDestBank = CreateBank(lExpandedSize)
	End If
	iRootNode = BuildTree()
	ExpandData hFileIn, hFileOut, iRootNode, lDestBank

	; Close packed file

	CloseFile hFileIn

	; Save expanded file

	hFileOut = WriteFile(sDest)
	WriteBytes lDestBank, hFileOut, 0, lExpandedSize
	CloseFile hFileOut
	
	FreeBank lDestBank
	
	; Success!
	
	Return True

End Function
