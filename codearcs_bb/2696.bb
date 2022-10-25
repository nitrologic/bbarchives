; ID: 2696
; Author: Malice
; Date: 2010-04-10 13:33:18
; Title: BB Data packing
; Description: More manageable  data packaging

;Example

Test_Data=ReadFileData("C:\WINDOWS\Media\tada.wav")
If (Not(Test_Data)) Then RuntimeError "Data was not read."
Pack_File=PackData(Test_Data,"Test Pack.bb","DataPack")
ClearPackBank(Test_Data)
If (Not(Pack_File)) Then RuntimeError "Data was not packed to file."

;Copy and Uncomment from a new blitz file to test Unpacking

;Include "Test Pack.bb"
;Restore DataPack
;If (Not(UnpackData("Unpacked Test.wav"))) Then RuntimeError "File was not unpacked"
;End
;Function UnpackData(fp_FilePath$)																																			;	Unpacks an included Data Package into a real file. User MUST use Restore command with the correct label before calling this
;	If (fp_FilePath$<>"")
;		Local d_ReadByte
;		Local n_ByteCount
;		Local h_FileHandle=WriteFile(fp_FilePath$)
;		Repeat
;			Read d_ReadByte
;			If (d_ReadByte<0) Then Exit
;				n_ByteCount=n_ByteCount+1
;			WriteByte(h_FileHandle,d_ReadByte)
;		Until KeyDown(True)
;		CloseFile h_FileHandle
;		If (Not(n_ByteCount)) Then DebugLog "No data written"
;		Return n_ByteCount
;	End If
;	DebugLog "Invalid Filename"
;	Return False
;End Function





























; The Functions
Function ReadFileData(fp_FilePath$)																																		;	Reads a File to Memory Bank and returns the Bank
	If(FileType(fp_FilePath$)=1)
		Local n_Size=FileSize(fp_FilePath$)
		If (n_Size)
			Local h_FileHandle=ReadFile(fp_FilePath$)
			If (h_FileHandle)
				Local bb_Databank=CreateBank(n_Size)
				If (bb_Databank)
					Local n_IterBytes
					Local d_Byte
					For n_IterBytes=1 To n_Size
						d_Byte=ReadByte(h_FileHandle)
						PokeByte bb_Databank,n_IterBytes-1,d_Byte
					Next
					CloseFile h_FileHandle
					Return bb_Databank
				End If
			End If
		End If
	End If
	DebugLog "Error Reading File "+fp_FilePath$
	Return False
End Function					

Function PackData(bb_PackBank%, fp_WritePath$, s_DataLabel$)																					;	Writes the Bank to a .bb File
	If (bb_PackBank)
		Local n_Size=BankSize(bb_PackBank)
		If (n_Size)
			If (s_DataLabel$="") Or (Not(GetLabelNameValid(s_DataLabel)))
				DebugLog "Invalid Data Label Name"
				Return False
			End If
			s_DataLabel$="."+s_DataLabel$
			Local h_FileHandle=WriteFile(fp_WritePath$)
			If (h_FileHandle)
				WriteStringData(h_FileHandle,s_DataLabel$)
				Local d_Byte
				Local n_IterBytes
				Local n_IterBlocks
				Local n_OverFlow=n_Size Mod 16
				Local n_EndBlock=((n_Size-n_OverFlow) Shr 4)
				For n_IterBlocks=0 To n_EndBlock-1
					WriteStringData(h_FileHandle,"Data ",True)
					For n_IterBytes=0 To 15
						d_Byte=PeekByte(bb_PackBank,n_IterBytes+(n_IterBlocks Shl 4))
						WriteByteData(h_FileHandle,d_Byte)
						If (n_IterBytes<15) Then WriteStringData(h_FileHandle,",")
					Next
				Next
				If (n_OverFlow)
					WriteStringData(h_FileHandle,"Data ",True)
					For n_IterBlocks=0 To n_OverFlow
						d_Byte=PeekByte(bb_PackBank, n_Size-(n_IterBlocks+1))
						WriteByteData(h_FileHandle,d_Byte)
						WriteStringData(h_FileHandle,",")
					Next
				End If
				WriteByte  h_FileHandle,-1
				CloseFile h_FileHandle
				Return True
			End If
		End If
	End If
	DebugLog "Memory Bank Error"
	Return False
End Function

Function ClearPackBank(bb_PackBank%)																																	;	Frees up the memory
	If (bb_PackBank) Then FreeBank bb_PackBank
End Function

Function UnpackData(fp_FilePath$)																																			;	Unpacks an included Data Package into a real file. User MUST use Restore command with the correct label before calling this
	If (fp_FilePath$<>"")
		Local d_ReadByte
		Local n_ByteCount
		Local h_FileHandle=WriteFile(fp_FilePath$)
		Repeat
			Read d_ReadByte
			If (d_ReadByte<0) Then Exit
			n_ByteCount=n_ByteCount+1
			WriteByte(h_FileHandle,d_ReadByte)
		Until KeyDown(True)
		CloseFile h_FileHandle
		If (Not(n_ByteCount)) Then DebugLog "No data written"
		Return n_ByteCount
	End If
	DebugLog "Invalid Filename"
	Return False
End Function









; The following are system functions to enable operation of the Packing/Unpacking. These are not for user calls.

Function WriteStringData(h_FileHandle%,s_String$,b_NewLine=False)																		;	Writes Strings as Byets to the pack file
	If (b_NewLine)
		WriteByte h_FileHandle,13
		WriteByte h_FileHandle,10 
	End If
	Local n_IterBytes
	Local n_Length=Len(s_String$)
	If (n_Length)
		For n_IterBytes=1 To n_Length
			WriteByte h_FileHandle,Asc(Mid(s_String$,n_IterBytes,1))
		Next
	End If
End Function

Function WriteByteData(h_FileHandle%,d_ByteData)																											;	Writes Bytes to the pack file
	Local n_IterDecimal
	Local n_DecSize=Len(Str(d_ByteData))
	If (d_ByteData)
		Local d_WriteByte
		For n_IterDecimal=n_DecSize-1 To 1 Step -1
			d_WriteByte=(Int(Floor(d_ByteData/(10^n_IterDecimal))) Mod 10^n_IterDecimal)
			WriteByteValue(h_FileHandle,d_WriteByte)
		Next
		WriteByteValue(h_FileHandle,(d_ByteData Mod 10))
	Else
		WriteByteValue(h_FileHandle,0)
	End If
End Function

Function WriteByteValue(h_FileHandle%,n_Value)																												;	Helps translate Byte values for writing
	n_Value=n_Value+48
	WriteByte h_FileHandle,n_Value
End Function

Function GetLabelNameValid(s_String$)																																	;	Used to ensure validity of the Blitz .Label
	If (s_String$="") Then Return False
	Local n_IterAsc
	For n_IterAsc=0 To 47
		If Instr(s_String,Chr(n_IterAsc)) Then Return False
	Next
	For n_IterAsc=58 To 64
		If Instr(s_String,Chr(n_IterAsc)) Then Return False
	Next
	For n_IterAsc=91 To 94
		If Instr(s_String,Chr(n_IterAsc)) Then Return False
	Next
	If Instr(s_String,Chr(96)) Then Return False
	For n_IterAsc=122 To 255
		If Instr(s_String,Chr(n_IterAsc)) Then Return False
	Next		
	Return True
End Function
