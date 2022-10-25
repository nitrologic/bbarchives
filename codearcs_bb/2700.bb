; ID: 2700
; Author: Malice
; Date: 2010-04-17 08:28:46
; Title: OGG Info
; Description: Reads Metadata and other details from OGG format files

;Const OGG_HEX_CHECK$="OggS"
Const OGG_SIZE_MIN%=512
Const OGG_MAX_TAGS%=5

Global OGG_TAG_LABEL$[OGG_MAX_TAGS]
OGG_TAG_LABEL[0]="ARTIST="
OGG_TAG_LABEL[1]="ALBUM="
OGG_TAG_LABEL[2]="GENRE="
OGG_TAG_LABEL[3]="TITLE="
OGG_TAG_LABEL[4]="ALBUM ARTIST="

Const OGG_INF_UNKNOWN%			=	0
Const OGG_INF_TITLE%  			=	1
Const OGG_INF_ARTIST% 			=	3
Const OGG_INF_ALBUM% 			=	4
Const OGG_INF_ALBUM_ARTIST% 	=	5
Const OGG_INF_GENRE% 			=	6
;Const MP3_INF_TRACK% 			=	7
;Const MP3_INF_YEAR% 			=	8
;Const OGG_INF_DURATION% 		=	9

Type OGG_Info
	Field OGG_Filepath$
	Field OGG_FileSize%
	Field OGG_Container_Version%
	Field Ogg_PageCount%
	Field OGG_Album$
	Field OGG_Artist$
	Field OGG_Album_Artist$
	Field OGG_Genre$
	Field OGG_Title$
;	Field OGG_DurationMilliseconds%
End Type

Type OGG_PageInfo
	Field OGG_Parent.OGG_Info
	Field OGG_Header%
	Field OGG_Header_Type
	Field OGG_Granule1%
	Field OGG_Granule2%
	Field OGG_Bitstream%
	Field OGG_Page%
	Field OGG_Checksum%
	Field OGG_Start%
	Field Ogg_Length%
End Type

Function GetOGGSpecificInfo$(OGG_FileName$,OGGInfoType%,AutoClean%=True)
	;	Autoclean flag frees up stored MP3 memory automatically
	
	Local sReturn$=GetOGGData$(OGG_FileName$,OGGInfoType%)
	If(AutoClean)
		ClearAllOggData
	End If
	Return sReturn$
End Function

Function CheckOGGDataExists%()
	Local OGG_DataType.OGG_Info=First OGG_Info
	Return (OGG_DataType.OGG_Info<>Null)
End Function

Function GetOGGData$(OGG_FileName$,OGG_Info_Type%)
	
	;Ensure Matching OGG Data Has Been Read
	
	If (Not(CheckOGGDataExists()))
		ClearAllOggData
		ReadOggData(OGG_FileName$)
	End If
	If (Not(CheckOGGDataExists()))
		ClearAllOggData
		Return ""
	End If	
	Local OGG_DataType.OGG_Info=First OGG_Info	
	If (OGG_DataType\OGG_Filepath<<OGG_FileName$)
		ClearAllOggData
		ReadOggData(OGG_FileName$)
	End If
	If (Not(CheckOGGDataExists()))
		ClearAllOggData
		Return ""
	End If
	
	OGG_DataType.OGG_Info=First OGG_Info
	
	Select (OGG_Info_Type%)
		Case OGG_INF_ALBUM%:Return OGG_DataType\OGG_Album$
		Case OGG_INF_ARTIST%:Return OGG_DataType\OGG_Artist$
		Case OGG_INF_ALBUM_ARTIST%:Return OGG_DataType\OGG_Album_Artist$
		;Case OGG_INF_DURATION%:Return Str(OGG_DataType\OGG_DurationMilliseconds)
		Case OGG_INF_GENRE%:Return OGG_DataType\OGG_Genre$
		Case OGG_INF_TITLE%:Return OGG_DataType\OGG_Title$
;		Case OGG_INF_TRACK%:Return Str(OGG_DataType\OGG_Track_No%)
;		Case OGG_INF_YEAR%:Return OGG_DataType\OGG_ReleaseYear$
			
	End Select
	
	Return ""
End Function

Function AddOGGTag(nTagReference%,sTagData$)
	
	If (nTagReference>=OGG_MAX_TAGS%)
		DebugLog("OGG tag reference ("+Str(nTagReference%)+") out of bounds")
		Return
	End If
	
	Local OggInfo.OGG_Info=First OGG_Info
	If (OggInfo.OGG_Info=Null)
		Return
	End If
	
	OggInfo.OGG_Info=First OGG_Info
	
	If (sTagData$="")
		"OGG Tag data string is null"
	End If
	
	Select (nTagReference%)
		Case 0: OggInfo\OGG_Artist$=sTagData$
		Case 1: OggInfo\OGG_Album$=sTagData$
		Case 2: OggInfo\OGG_Genre$=sTagData$
		Case 3: OggInfo\OGG_Title$=sTagData$
		Case 4: OggInfo\OGG_Album_Artist$=sTagData$
		Default:
			DebugLog("OGG tag reference ("+Str(nTagReference%)+") out of bounds")
			Return
	End Select
	
	DebugLog(OGG_TAG_LABEL[nTagReference]+sTagData+" added")
End Function

Function CheckOggTag(nTagReference%,OGGFileStream%)
	Local OggInfo.OGG_Info=First OGG_Info
	If (OggInfo.OGG_Info=Null)
		Return
	End If
	
	If (Not(OGGFileStream))
		Return False
	End If
	
	Local sData$
	Local sByte$=" "
	Local nIterBytes%
	SeekFile(OGGFileStream,False)
	For nIterBytes%=0 To OGG_SIZE_MIN
		sData$=sData$+Chr(ReadByte(OGGFileStream))
		If (Instr(sData$,OGG_TAG_LABEL[nTagReference%]))
			sData$=""
			While (sByte$<<Chr(False) And (Not(Eof(OGGFileStream))))
				sByte$=Chr(ReadByte(OGGFileStream))
				sData$=sData+sByte$
			Wend	
			AddOGGTag(nTagReference%,Trim(sData$))
			Exit
		End If
	Next
End Function	

Function ReadOGGTags(OGG_Filestream%)
	If (Not(OGG_Filestream%))
		Return False
	End If
	Local nCheck%
	For nCheck%=0 To (OGG_MAX_TAGS-1)
		CheckOggTag(nCheck%,OGG_Filestream%)
	Next
End Function	

Function ClearAllOggData()
	Delete Each OGG_Info
	Delete Each OGG_PageInfo
End Function

Function ReadOggData%(OGG_Filename$)
	Local OggInfo.OGG_Info=First OGG_Info
	
	;Ensure valid ogg info
	If (OggInfo.OGG_Info=Null)
		ClearAllOggData
		OggInfo.OGG_Info=New OGG_Info
	End If
	If (OggInfo\OGG_Filepath$<>OGG_Filename$)
		ClearAllOggData
		OggInfo.OGG_Info=New OGG_Info
	End If
	
	Local OGG_FileHandle=ReadFile(OGG_Filename$)
	
	If (Not(OGG_FileHandle))
		ClearAllOggData
		Return False
	End If
	
	OggInfo\OGG_Filepath$=OGG_Filename$
	OggInfo\OGG_FileSize%=FileSize(OGG_Filename$)
	ReadOGGTags(OGG_FileHandle)
	SeekFile(OGG_FileHandle,False)
	Local sHexCheck$=""
	Local nIterByteInt%,bResult%
	Local bValid%=False
	OggInfo\Ogg_PageCount%=False
	While (bValid=Eof(OGG_FileHandle))
		sHexCheck=""
		For nIterByteInt%=0 To 3
			sHexCheck$=sHexCheck$+Chr(ReadByte(OGG_FileHandle))
		Next
		If (sHexCheck$=OGG_HEX_CHECK$)
			OggInfo\Ogg_PageCount%=OggInfo\Ogg_PageCount+1
			DebugLog FilePos(OGG_FileHandle)
			bResult%=ReadOGGPageData%(OGG_FileHandle,FilePos(OGG_FileHandle))
			If (bResult%) Then SeekFile(OGG_FileHandle,bResult)
		End If
		If ((FilePos(OGG_FileHandle)<((OggInfo\OGG_FileSize%)-4)))  Then SeekFile(OGG_FileHandle,FilePos(OGG_FileHandle)-(4-1))
	Wend
	Return True
End Function

Function ReadOGGPageData%(OGGFile%,nPos)
	If (Not(OGGFile))
		Return False
	End If
	Local OGGInfo.OGG_Info=First OGG_Info
	If (OGGInfo.OGG_Info=Null)
		ClearAllOggData
		Return False
	End If
	Local OGG.OGG_PageInfo=New OGG_PageInfo
	OGGInfo\OGG_Container_Version%=ReadByte(OGGFile)*ReadByte(OGGFile)
	OGG\OGG_Parent.OGG_Info=OGGInfo
	OGG\OGG_Header%=ReadByte(OGGFile)*ReadByte(OGGFile)
	OGG\OGG_Granule1%=ReadInt(OGGFile)
	OGG\OGG_Granule2%=ReadInt(OGGFile)
	OGG\OGG_Bitstream%=ReadInt(OGGFile)
	OGG\OGG_Page%=ReadInt(OGGFile)
	OGG\OGG_Checksum%=ReadInt(OGGFile)
	Local Segments%=ReadByte(OGGFile)*ReadByte(OGGFile)
	Local NullByte%=False
	While (Not(NullByte%))
		NullByte%=ReadByte(OGGFile)
	Wend	
	OGG\OGG_Start%=FilePos(OGGFile)
	
	Local sHexCheck$=""
	Local nIterbyteInt%
	Local bValid=False
	While (bValid=Eof(OGGFile))
		If ((FilePos(OGGFile)>((OGG\OGG_Parent\OGG_FileSize%)-4))) Then Exit
		sHexCheck$=""
		While(Chr(ReadByte(OGGFile)))<>(Left(OGG_HEX_CHECK,1)) And (Not(Eof(OGGFile)))
		Wend
		If (Not(Eof(OGGFile)))
			sHexCheck$=Left(OGG_HEX_CHECK,1)
			For nIterbyteInt%=0 To 2
				sHexCheck$=sHexCheck$+Chr(ReadByte(OGGFile))
			Next
			If (sHexCheck$=OGG_HEX_CHECK$) Then Exit
			SeekFile(OGGFile,FilePos(OGGFile)-(4-1))
		EndIf
	Wend
	If ((FilePos(OGGFile)<((OGG\OGG_Parent\OGG_FileSize%)-4)))  Then SeekFile(OGGFile,FilePos(OGGFile)-4)
	OGG\Ogg_Length%=((FilePos(OGGFile))-OGG\OGG_Start)
	Return OGG\Ogg_Length%+OGG\OGG_Start
End Function	

Function CheckIsValidOGG%(OGG_Filename$)
	Return ((FileType(OGG_Filename$))And (Lower(Right(OGG_Filename$,4))=".ogg") And (FileSize(OGG_Filename$)>OGG_SIZE_MIN))
End Function
