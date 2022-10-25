; ID: 2759
; Author: Malice
; Date: 2010-08-30 10:38:31
; Title: Read/Write Progress Meters
; Description: An example of accurate progress bars

Global nPROGRESS_CURRENT_BYTES%
Global nPROGRESS_MAXIMUM_BYTES%

Global nPROGRESS_DIR_MAXIMUM_BYTES%
Global nPROGRESS_DIR_CURRENT_BYTES%


Global nPROGRESS_BANK_MAXIMUM_BYTES%
Global nPROGRESS_BANK_CURRENT_BYTES%

; Example:
	Graphics 1024,768,32,6
	SetBuffer BackBuffer()
	Global sPath$=GetEnv("ProgramFiles")+"\Blitz3D"
	If Right(sPath,1)<>"\" Then sPath=sPath+"\"
	Local hDir=hReadProgressDir(sPath)
	Local sFileName$=NextFile(hDir)
	
		While (sFileName<>"")
			If (Right(sFileName,1)<>".")
				hFile=hReadFileProgress(sPath+sFileName)
				If (hFile)
					While (Not (Eof(hFile)))
						nReadByteProgress(hFile)
						
						Cls
						
						DisplayProgressBar(0,0,256,32,nProgressDir(),sPath+" ("+Str(nProgressDir())+"%)")
						DisplayProgressBar(0,128,256,32,nProgress(),sFIleName+" ("+Str(nProgress)+"%)")				
						Flip
						If (bProgressDirComplete())
							CloseFile hFile
							Exit
						End If
					Wend
				End If
			End If
			sFileName$=NextFile(hDir)
			If (bProgressDirComplete()) Then Exit
		Wend
	CloseDir hDir
		
;Functions	
	
Function SetByteLimit(nLimit%)
	nPROGRESS_MAXIMUM_BYTES=nLimit
End Function

Function SetDirByteLimit(nLimit%)
	nPROGRESS_DIR_MAXIMUM_BYTES=nLimit
End Function

Function SetBankByteLimit(nLimit%)
	nPROGRESS_BANK_MAXIMUM_BYTES=nLimit
End Function

Function SetProgress(nBytes%)
	nPROGRESS_CURRENT_BYTES=nBytes
End Function

Function SetProgressPercent%(fPercent#)
	nPROGRESS_CURRENT_BYTES=Int((Float(nPROGRESS_MAXIMUM_BYTES)*0.01*fPercent))
	Return nPROGRESS_CURRENT_BYTES
End Function

Function SetProgressDir(nBytes%)
	nPROGRESS_DIR_CURRENT_BYTES=nBytes
End Function

Function SetProgressDirPercent%(fPercent#)
	nPROGRESS_DIR_CURRENT_BYTES=Int((Float(nPROGRESS_DIR_MAXIMUM_BYTES)*0.01*fPercent))
	Return nPROGRESS_DIR_CURRENT_BYTES
End Function

Function SetProgressBank(nBytes%)
	nPROGRESS_BANK_CURRENT_BYTES=nBytes
End Function

Function SetProgressBankPercent%(fPercent#)
	nPROGRESS_BANK_CURRENT_BYTES=Int((Float(nPROGRESS_BANK_MAXIMUM_BYTES)*0.01*fPercent))
	Return nPROGRESS_BANK_CURRENT_BYTES
End Function

;--

Function hCreateBankProgress(nBankSize%)
	Local hReturn%=CreateBank(nBankSize)
	nPROGRESS_BANK_MAXIMUM_BYTES=nBankSize
	nPROGRESS_BANK_CURRENT_BYTES=0
End Function
	
Function ResizeBankProgress(hBank,nSize%)
	nPROGRESS_BANK_MAXIMUM_BYTES=nSize
	ResizeBank hBank,nSize
End Function

Function ResetProgress()
	nPROGRESS_CURRENT_BYTES=0
End Function

Function ResetProgressDir()
	nPROGRESS_DIR_CURRENT_BYTES=0
End Function

Function ResetProgressBank()
	nPROGRESS_BANK_CURRENT_BYTES=0
End Function

Function ProgressSkip()
	nPROGRESS_CURRENT_BYTES=nPROGRESS_MAXIMUM_BYTES
End Function

Function ProgressDirSkip()
	nPROGRESS_DIR_CURRENT_BYTES=nPROGRESS_DIR_MAXIMUM_BYTES
End Function

Function ProgressBankSkip()
	nPROGRESS_BANK_CURRENT_BYTES=nPROGRESS_BANK_MAXIMUM_BYTES
End Function

Function bProgressComplete%()
	Return (nPROGRESS_CURRENT_BYTES>=nPROGRESS_MAXIMUM_BYTES)
End Function

Function bProgressDirComplete%()
	Return (nPROGRESS_DIR_CURRENT_BYTES>=nPROGRESS_DIR_MAXIMUM_BYTES)
End Function

Function bProgressBankComplete%()
	Return (nPROGRESS_BANK_CURRENT_BYTES>=nPROGRESS_BANK_MAXIMUM_BYTES)
End Function

;--

Function hReadProgressDir%(sDirPath$)
	nPROGRESS_DIR_CURRENT_BYTES=0
	nPROGRESS_DIR_MAXIMUM_BYTES=0
	Local hReturn%=0
	If (FileType(sDirPath)=2)
		hReturn=ReadDir(sDirpath)		
		Local sFile$=NextFile(hReturn)
		While (sFile<>"")
			If (Right(sFile,1)<>".")
				nPROGRESS_DIR_MAXIMUM_BYTES=nPROGRESS_DIR_MAXIMUM_BYTES+FileSize(sDirPath+sFile)
			End If
			sFile=NextFile(hReturn)
		Wend
		CloseDir hReturn
		hReturn=ReadDir(sDirPath)
	End If
	nPROGRESS_CURRENT_BYTES=0

	Return hReturn
End Function

;--

Function hReadFileProgress%(sFilepath$)
	Local hReturn%=0
	If (FileType(sFilepath)=1)
		nPROGRESS_MAXIMUM_BYTES=FileSize(sFilepath)
		nPROGRESS_CURRENT_BYTES=0
		hReturn=ReadFile(sFilepath)
	End If
	Return hReturn
End Function

Function hOpenFileProgress%(sFilepath$)
	Local hReturn%=0
	If (FileType(sFilepath)=1)
		nPROGRESS_MAXIMUM_BYTES=FileSize(sFilepath)
		nPROGRESS_CURRENT_BYTES=0
		hReturn=OpenFile(sFilepath)
	End If
	Return hReturn
End Function

Function sReadLineProgress$(hFilehandle)
	Local sReturn$=ReadLine(hFilehandle)
	nPROGRESS_CURRENT_BYTES=nPROGRESS_CURRENT_BYTES+Len(sReturn)+1	;Add 1 for terminator
	nPROGRESS_DIR_CURRENT_BYTES=nPROGRESS_DIR_CURRENT_BYTES+Len(sReturn)+1
	Return sReturn
End Function

Function nReadIntProgress%(hFilehandle)
	Local nReturn%=ReadInt(hFilehandle)
	nPROGRESS_CURRENT_BYTES=nPROGRESS_CURRENT_BYTES+4
	nPROGRESS_DIR_CURRENT_BYTES=nPROGRESS_DIR_CURRENT_BYTES+4
	Return nReturn
End Function

Function nReadShortProgress(hFilehandle)
	Local nReturn%=ReadShort(hFilehandle)
	nPROGRESS_CURRENT_BYTES=nPROGRESS_CURRENT_BYTES+2
	nPROGRESS_DIR_CURRENT_BYTES=nPROGRESS_DIR_CURRENT_BYTES+2
	Return nReturn
End Function

Function sReadStringProgress$(hFilehandle)
	Local sReturn$=ReadString(hFilehandle)
	nPROGRESS_CURRENT_BYTES=nPROGRESS_CURRENT_BYTES+Len(sReturn)+1	; Add 1 for terminator
	nPROGRESS_DIR_CURRENT_BYTES=nPROGRESS_DIR_CURRENT_BYTES+Len(sReturn)+1
	Return sReturn
End Function

Function fReadFloatProgress#(hFilehandle)
	Local fReturn#=ReadFloat(hFilehandle)
	nPROGRESS_CURRENT_BYTES=nPROGRESS_CURRENT_BYTES+4
	nPROGRESS_DIR_CURRENT_BYTES=nPROGRESS_DIR_CURRENT_BYTES+4
	Return fReturn
End Function

Function nReadByteProgress$(hFilehandle)
	Local nReturn=ReadByte(hFilehandle)
	nPROGRESS_CURRENT_BYTES=nPROGRESS_CURRENT_BYTES+1
	nPROGRESS_DIR_CURRENT_BYTES=nPROGRESS_DIR_CURRENT_BYTES+1
	Return nReturn
End Function

Function nPeekByteProgress%(hBank%,nOffset%)
	Local nReturn%=PeekByte(hBank,nOffset)
	nPROGRESS_CURRENT_BYTES=nPROGRESS_CURRENT_BYTES+1
	nPROGRESS_BANK_CURRENT_BYTES=nPROGRESS_BANK_CURRENT_BYTES+1
	Return nReturn
End Function

Function nPeekIntProgress%(hBank%,nOffset%)
	Local nReturn%=PeekInt(hBank,nOffset)
	nPROGRESS_CURRENT_BYTES=nPROGRESS_CURRENT_BYTES+4
	nPROGRESS_BANK_CURRENT_BYTES=nPROGRESS_BANK_CURRENT_BYTES+4
	Return nReturn
End Function

Function fPeekFloatProgress%(hBank%,nOffset%)
	Local fReturn#=PeekFloat(hBank,nOffset)
	nPROGRESS_CURRENT_BYTES=nPROGRESS_CURRENT_BYTES+4
	nPROGRESS_BANK_CURRENT_BYTES=nPROGRESS_BANK_CURRENT_BYTES+4
	Return fReturn
End Function	

Function nPeekShortProgress%(hBank%,nOffset%)
	Local nReturn%=PeekShort(hBank,nOffset)
	nPROGRESS_CURRENT_BYTES=nPROGRESS_CURRENT_BYTES+2
	nPROGRESS_BANK_CURRENT_BYTES=nPROGRESS_BANK_CURRENT_BYTES+2
	Return nReturn
End Function	

;--

Function WriteLineProgress$(hFilehandle,sLine$)
	nPROGRESS_CURRENT_BYTES=nPROGRESS_CURRENT_BYTES+Len(sLine)+1	;Add 1 for terminator
	nPROGRESS_DIR_CURRENT_BYTES=nPROGRESS_DIR_CURRENT_BYTES+Len(sLine)+1
	WriteLine hFileHandle,sLine
End Function

Function WriteIntProgress%(hFilehandle,nInt%)
	nPROGRESS_CURRENT_BYTES=nPROGRESS_CURRENT_BYTES+4
	nPROGRESS_DIR_CURRENT_BYTES=nPROGRESS_DIR_CURRENT_BYTES+4
	WriteInt hFileHandle,nInt
End Function

Function WriteShortProgress%(hFilehandle,nShort%)
	nPROGRESS_CURRENT_BYTES=nPROGRESS_CURRENT_BYTES+2
	nPROGRESS_DIR_CURRENT_BYTES=nPROGRESS_DIR_CURRENT_BYTES+2
	WriteShort hFileHandle,nShort
End Function

Function WriteStringProgress$(hFilehandle,sString$)
	nPROGRESS_CURRENT_BYTES=nPROGRESS_CURRENT_BYTES+Len(sString)+1	;Add 1 for terminator
	nPROGRESS_DIR_CURRENT_BYTES=nPROGRESS_DIR_CURRENT_BYTES+Len(sString)+1
	WriteString hFileHandle,sString
End Function

Function WritefloatProgress%(hFilehandle,fFloat#)
	nPROGRESS_CURRENT_BYTES=nPROGRESS_CURRENT_BYTES+4
	nPROGRESS_DIR_CURRENT_BYTES=nPROGRESS_DIR_CURRENT_BYTES+4
	WriteFloat hFileHandle,fFLoat
End Function

Function WriteByteProgress%(hFilehandle,nInt%)
	nPROGRESS_CURRENT_BYTES=nPROGRESS_CURRENT_BYTES+1
	nPROGRESS_DIR_CURRENT_BYTES=nPROGRESS_DIR_CURRENT_BYTES+1
	WriteByte hFileHandle,nInt Mod 256
End Function

Function PokeByteProgress%(hBank%,nOffset%,nByte%)
	nPROGRESS_CURRENT_BYTES=nPROGRESS_CURRENT_BYTES+1
	nPROGRESS_BANK_CURRENT_BYTES=nPROGRESS_BANK_CURRENT_BYTES+1
	PokeByte hBank,nOffset,nByte
End Function

Function PokeIntProgress%(hBank%,nOffset%,nInt%)
	nPROGRESS_CURRENT_BYTES=nPROGRESS_CURRENT_BYTES+4
	nPROGRESS_BANK_CURRENT_BYTES=nPROGRESS_BANK_CURRENT_BYTES+4
	PokeInt hBank,nOffset,nInt
End Function

Function PokeFloatProgress%(hBank%,nOffset%,fFloat%)
	nPROGRESS_CURRENT_BYTES=nPROGRESS_CURRENT_BYTES+4
	nPROGRESS_BANK_CURRENT_BYTES=nPROGRESS_BANK_CURRENT_BYTES+4
	PokeFloat hBank,nOffset,fFloat
End Function

Function PokeShortProgress%(hBank%,nOffset%,nShort%)
	nPROGRESS_CURRENT_BYTES=nPROGRESS_CURRENT_BYTES+2
	nPROGRESS_BANK_CURRENT_BYTES=nPROGRESS_BANK_CURRENT_BYTES+2
	PokeShort hBank,nOffset,nShort
End Function

;--

Function nProgress%()
	Return Int (fPercentage(nPROGRESS_CURRENT_BYTES,nPROGRESS_MAXIMUM_BYTES))
End Function

Function nProgressDir%()
	Return Int (fPercentage(nPROGRESS_DIR_CURRENT_BYTES,nPROGRESS_DIR_MAXIMUM_BYTES))
End Function

Function nProgressBank%()
	Return Int (fPercentage(nPROGRESS_BANK_CURRENT_BYTES,nPROGRESS_BANK_MAXIMUM_BYTES))
End Function

Function DisplayProgressBar(X%,Y%,Width%,Height%,nProportion%,Txt$)
	Color 255,255,255
	Rect X,Y,Width,Height,0

	Text X,Y+Height*1.5,Txt$

	Color 255,0,0
	Rect X+1,Y+1,Width-2,Height-2,1
	
	Color 0,255,0
	Rect X+1,Y+1,nProportion*0.01*(Width-2),Height-2,1
End Function

Function fPercentage#(nFraction%,nMaximum%)
	If (nFraction=nMaximum)  Then Return 100.0
	If (nFraction)*(nMaximum)
		Return Float(nFraction)/Float(nMaximum)*100
	End If
		Return 0.0
End Function
