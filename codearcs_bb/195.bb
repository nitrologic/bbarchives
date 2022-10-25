; ID: 195
; Author: Milky Joe
; Date: 2002-01-16 20:30:32
; Title: Huffman Compression
; Description: Huffman compress your files/data

; Title:		Curve Compression (COMPRESS)
; Author:		Leigh Bowers
;			(c) Copyright 2001 Curve Software
; Version:	1.0
;
; Email:		leigh.bowers@curvesoftware.co.uk
; WWW:		www.curvesoftware.co.uk/blitz

Type TreeNode
    Field Weight%
    Field SavedWeight%
    Field Child1%
    Field Child0%
End Type

Type CharCodes
    Field Code%
    Field CodeBits%
End Type

Type BitFile
    Field Mask%
    Field Rack%
    Field OutputByteCount%
End Type

Const SRCCOPY% = $CC0020
Const ENDOFSTREAM% = 256
Const ENDWEIGHTSTREAM% = $FFF

Dim Counters%(256)
Dim Nodes.TreeNode(514)
Dim Codes.CharCodes(257)

Global Bitio.BitFile

; --------------
; Example Usage
; --------------

s$ = "source.bmp"
Print "Curve Compression - Compress"
Print ""
Print "Source length:" + FileSize(s)
Start# = MilliSecs()
;
CompressFile s, "packed.dat" ; The extention can be anything (.dat, .pak, .abc etc)
;
Finish# = MilliSecs()
Print "Packed length:" + FileSize("packed.dat")
Print ((Finish - Start)/1000) + " seconds taken."
WaitKey

End

; --------------

Function InitHuffman()

	Local i%

	For i = 0 To 514 : Nodes.TreeNode(i) = New TreeNode : Next
	For i = 0 To 257 : Codes.CharCodes(i) = New CharCodes : Next
	bitio.bitfile = New bitfile
	Bitio\Mask = $80

End Function

Function CountOccurrences(lSourceBank%, lSourceSize%)

	Local bChar%, l%

	For l = 0 To (lSourceSize - 1)
		bChar = PeekByte(lSourceBank, l)
    		Counters(bChar) = Counters(bChar) + 1
	Next

End Function

Function ScaleCounts()

	Local MaxCount%, i%
    
    	MaxCount = 0
    	For i = 0 To 256
        	If Counters(i) > MaxCount Then MaxCount = Counters(i)
    	Next
        
    	MaxCount = (MaxCount / 255) + 1
    	For i = 0 To 256
        	Nodes(i)\Weight = Counters(i) / MaxCount
        	If Nodes(i)\Weight = 0 And Counters(i) <> 0 Then Nodes(i)\Weight = 1
    	Next
    	Nodes(ENDOFSTREAM)\Weight = 1

End Function

Function BuildTree%()

	Local NextFree%, i%, Min1%, Min2%
    
	Nodes(513)\Weight = $7FFF
	For NextFree = (EndOfStream + 1) To 514
	    	Min1 = 513
	    	Min2 = 513
	    	For i = 0 To (NextFree - 1)
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

		Nodes(NextFree)\Weight = Nodes(Min1)\Weight + Nodes(Min2)\Weight
		Nodes(Min1)\SavedWeight = Nodes(Min1)\Weight
	      	Nodes(Min1)\Weight = 0
	      	Nodes(Min2)\SavedWeight = Nodes(Min2)\Weight
	      	Nodes(Min2)\Weight = 0
		Nodes(NextFree)\Child0 = Min1
		Nodes(NextFree)\Child1 = Min2
	Next
	
	NextFree = NextFree - 1
	Nodes(NextFree)\SavedWeight = Nodes(NextFree)\Weight

	Return NextFree

End Function

Function ConvertTreeToCode(CodeSoFar%, Bits%, node%)

	If node <= ENDOFSTREAM Then
		Codes(node)\Code = CodeSoFar
		Codes(node)\CodeBits = Bits
		Return
	End If
	CodeSoFar = CodeSoFar * 2
	Bits = Bits + 1
	ConvertTreeToCode CodeSoFar, Bits, Nodes(node)\Child0
	ConvertTreeToCode (CodeSoFar Or 1), Bits, Nodes(node)\Child1
	CodeSoFar = CodeSoFar / 2
	Bits = Bits - 1

End Function

Function OutputCounts%(lDestBank%, lDestSize%)

	Local i%, FirstNode%, LastNode%, NextNode%
	
	FirstNode = 0
	While (FirstNode < 256)
		If Nodes(FirstNode)\SavedWeight <> 0 Then
			For LastNode = FirstNode + 1 To 255
			    If Nodes(LastNode)\SavedWeight = 0 Then Exit
			Next
			LastNode = LastNode - 1
			For NextNode = LastNode + 1 To 255
				If Nodes(NextNode)\SavedWeight = 0 Then
					If (NextNode > 255) Or (NextNode - LastNode > 3) Then Exit
				Else
					LastNode = NextNode
				End If
			Next
			PokeShort lDestBank, lDestSize, FirstNode : lDestSize = lDestSize + 2
			PokeShort lDestBank, lDestSize, LastNode : lDestSize = lDestSize + 2
			For i = FirstNode To LastNode
				PokeShort lDestBank, lDestSize, Nodes(i)\SavedWeight : lDestSize = lDestSize + 2
			Next
			FirstNode = NextNode + 1
		Else
			FirstNode = FirstNode + 1
		End If
	Wend
	PokeShort lDestBank, lDestSize, ENDWEIGHTSTREAM : lDestSize = lDestSize + 2
	
	Return lDestSize

End Function

Function CompressFile(sSource$, sDest$)

	Local iSymbol%, iBitcount%, bChar%
	Local lSourceBank%, lDestBank%, lSourceSize%, lDestSize%
	
	InitHuffman

	; Create Banks

	lSourceSize = FileSize(sSource)

	lSourceBank = CreateBank(lSourceSize)
	lDestBank = CreateBank((lSourceSize + (lSourceSize * 0.01)) + 1000 ) ; Allow for 1% file increase

	; Read source file in to bank

	hFileIn% = ReadFile(sSource)
	ReadBytes lSourceBank, hFileIn, 0, lSourceSize
	CloseFile hFileIn
	
	; Pre-Processing
	
	CountOccurrences lSourceBank, lSourceSize
	ScaleCounts()

	iRootNode% = BuildTree()
	ConvertTreeToCode 0, 0, iRootNode
	
	; Main Compression
	
	PokeInt lDestBank, 0, lSourceSize ; Store the original (uncompressed) file size at the head of the file

	lDestSize = OutputCounts(lDestBank, 4)

	For l% = 0 To (lSourceSize - 1)
		bChar = PeekByte(lSourceBank, l)
		iSymbol = Codes(bChar)\Code
		iBitcount = Codes(bChar)\CodeBits
		lDestSize = BitHandler(lDestBank, iSymbol, iBitcount, lDestSize)
	Next

	iSymbol = Codes(ENDOFSTREAM)\Code
	iBitcount = Codes(ENDOFSTREAM)\CodeBits
	lDestSize = BitHandler(lDestBank, iSymbol, iBitcount, lDestSize)
	If Bitio\Mask <> $80 Then PokeByte lDestBank, lDestSize, Bitio\Rack : lDestSize = lDestSize + 1

	FreeBank lSourceBank

	; Write the compressed file

	hFileOut% = WriteFile(sDest)
	WriteBytes lDestBank, hFileOut, 0, lDestSize
	CloseFile hFileOut

	FreeBank lDestBank

End Function

Function BitHandler%(lDestBank%, iSymbol%, iBitcount%, lDestSize%)

	Local SymbolMask%, i%

	SymbolMask = 1
	For i = 1 To (iBitcount - 1) : SymbolMask = SymbolMask * 2 : Next
	While (SymbolMask <> 0)
		If (iSymbol And SymbolMask) Then Bitio\Rack = Bitio\Rack Or Bitio\Mask
		SymbolMask = SymbolMask / 2
		Bitio\Mask = Bitio\Mask / 2
		If Bitio\Mask = 0 Then
			PokeByte lDestBank, lDestSize, Bitio\Rack : lDestSize = lDestSize + 1
			Bitio\Mask = $80
			Bitio\Rack = 0
			Bitio\OutputByteCount = Bitio\OutputByteCount + 2
		End If
	Wend
	
	Return lDestSize

End Function
