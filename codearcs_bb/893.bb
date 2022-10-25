; ID: 893
; Author: pantsonhead.com
; Date: 2004-01-19 08:27:41
; Title: Get TTF Font File Data
; Description: Get any TTF file fontname directly from the file.

;   Example for BlitzPlus
;	Include "GetTTFdata.bb"
;	filename$=RequestFile$( "Select a TTF file","ttf" )
;	Print filename$
;	For x = 0 To 7
;		fontdata$=GetTTFdata$(filename$,x)
;		Print x+"="+fontdata$
;	Next
;	WaitKey()


;	datatypes
;   gfiCopyrightNotice = 0	; Copyright notice
;   gfiFontFamilyName = 1	; Font Family name
;   gfiFontSubfamilyName = 2	; Font Style (usually)
;   gfiUniqueFontIdentifier = 3 ; Some unique ID For this font.
;   gfiFullFontName = 4   	; combination of strings 1 And 2. Exception: 
;   gfiVersionString = 5  	; Version String (sometime includes a date). 
;   gfiPostscriptName = 6 	; Postscript name.
;   gfiTrademark = 7      	; Trademark notice/information.


Function GetTTFdata$(filename$, datatype=1)

	;Create Banks
	bnkTableOffsets = CreateBank(12)
	   
	; Open the file to read from 
	FontName$=""
	If Lower(Right(Trim(filename$),4))=".ttf" And FileType(filename$)=1 Then

		fontfile=ReadFile(filename$) 
		
		;Read the OffSetTable
		ReadBytes bnkTableOffsets,fontfile,0,12 
		For i = 0 To PeekShort(bnkTableOffsets,5)-1
			SeekFile(fontfile,12+i*16)
			word$=""
			For x = 1 To 4
				word$=word$+Chr$(ReadByte(fontfile))
			Next
			If Lower(word$)="name" Then
				ChkSum=Int_SwapEndian%(ReadInt(fontfile))
				Offset=Int_SwapEndian%(ReadInt(fontfile))
				Length=Int_SwapEndian%(ReadInt(fontfile))
			ElseIf Lower(word$)="cmap" Then
				cmapChkSum=Int_SwapEndian%(ReadInt(fontfile))
				cmapOffset=Int_SwapEndian%(ReadInt(fontfile))
				cmapLength=Int_SwapEndian%(ReadInt(fontfile))
				SeekFile(fontfile,cmapOffset)
				TableVersion = Short_SwapEndian%(ReadShort(fontfile))
				TableCount = Short_SwapEndian%(ReadShort(fontfile))
				PlatformID = Short_SwapEndian%(ReadShort(fontfile))
				EncodingID = Short_SwapEndian%(ReadShort(fontfile))
				cmapSubOffset=Int_SwapEndian%(ReadInt(fontfile))
				SeekFile(fontfile,cmapSubOffset)
				cmapFormat=Short_SwapEndian%(ReadShort(fontfile))
				words$=Words$+"|"+cmapOffset+">"+cmapFormat
			EndIf

			words$=Words$+"|"+word$
		Next
		
		
		;now we find the Correct Name Record
		SeekFile(fontfile,offset)
		FormatSelector=Short_SwapEndian%(ReadShort(fontfile))
		NumberNameRecords=Short_SwapEndian%(ReadShort(fontfile))
		StorageOffset= Short_SwapEndian%(ReadShort(fontfile))
		
		;loop thru NameRecords
		While (NameID<>datatype) And  (Eof(fontfile) <>1)
			PlatformID = Short_SwapEndian%(ReadShort(fontfile))
			EncodingID = Short_SwapEndian%(ReadShort(fontfile))
			LanguageID = Short_SwapEndian%(ReadShort(fontfile))
			NameID = Short_SwapEndian%(ReadShort(fontfile))
			
			If NameID=datatype	;This is what we're looking for
				NameLength = Short_SwapEndian%(ReadShort(fontfile))
				NameOffset = Short_SwapEndian%(ReadShort(fontfile))
			Else
				junk = ReadInt(fontfile) ;NameLength + NameOffset 
			EndIf
	 	Wend
 
		;Now we can get the data
		SeekFile(fontfile,offset+StorageOffset+NameOffset)
		For x = 1 To NameLength
			newChar=ReadByte(fontfile)
			If newChar<>0 Then fontdata$=fontdata$+ Chr$(newChar) ;filters unicode chr(0)s
		Next
		Return fontdata$
		CloseFile(fontfile)
	EndIf
	FreeBank bnkTableOffsets

End Function

; We use these for "ByteSwapping" since TTF format is other endian
Function Int_SwapEndian%(n%) 
	Return ((n And $FF) Shl 24) Or ((n And $FF00) Shl 8) Or ((n And $FF0000) Shr 8) Or ((n And $FF000000) Shr 24) 
End Function 

Function Short_SwapEndian%(n%) 
	Return ((n And $FF) Shl 8) Or ((n And $FF00) Shr 8) 
End Function
