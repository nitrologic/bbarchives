; ID: 3049
; Author: _PJ_
; Date: 2013-04-10 07:33:04
; Title: Read/Write Progress Meters
; Description: Check Progress of Reading/Writing

Global nPROGRESS_STREAM_CURRENT_BYTES%
Global nPROGRESS_STREAM_MAXIMUM_BYTES%

Global nPROGRESS_DIR_MAXIMUM_BYTES%
Global nPROGRESS_DIR_CURRENT_BYTES%

Global nPROGRESS_BANK_MAXIMUM_BYTES%
Global nPROGRESS_BANK_CURRENT_BYTES%

Global nPROGRESS_STREAM_TIMESTAMP%
Global nPROGRESS_BANK_TIMESTAMP%
Global nPROGRESS_DIR_TIMESTAMP%









; Example:
Graphics 1024,768,32,6
SetBuffer BackBuffer()
Global sPath$=CurrentDir()
Global sBlitzFilesOnly$=".bb"

Local hDir=hReadProgressDir(sPath,sBlitzFilesOnly)
Local sStreamName$=NextFile(hDir)
Local hStream

While (sStreamName<>"")
	If (Right(sStreamName,1)<>".")
		hStream=hReadProgressStream(sPath+sStreamName,sBlitzFilesOnly)
		If (hStream)
			While (Not (Eof(hStream)))
				
				;Normally we might do something with this...
				nReadByteProgress(hStream)
				
				Cls
				
				DisplayProgressBar(0,0,256,32,fProgressDirPercentage()*0.01,sPath+" ("+Str(Floor(fProgressDirPercentage()))+"%) "+sProgressDirTimeRemaining()+" : "+sStreamName+" Filtered: *"+sBlitzFilesOnly)
				DisplayProgressBar(0,128,256,32,fProgressStreamPercentage()*0.01,sStreamName+" ("+Str(Floor(fProgressStreamPercentage()))+"%) "+sProgressStreamTimeRemaining())
				
				Flip
				
				If (bProgressDirComplete())
					CloseFile hStream
					Exit
				End If
				
			Wend
			CloseFile hStream
		End If
	End If
	If (bProgressDirComplete()) Then Exit
	sStreamName$=NextFile(hDir)
Wend

CloseDir hDir













;User Functions

Function fCalculateRemainingTime#(Proportion#,BaselineTimestamp)
	Local Duration#=(Float(MilliSecs()-BaselineTimestamp))
	Local Rate#=1.0/Float(Proportion/Duration#)
	Return Rate*(1.0-Proportion)
End Function

Function DisplayProgressBar(X%,Y%,Width%,Height%,fProportion#,Txt$)
	Color 255,255,255
	Rect X,Y,Width,Height,0
	
	Text X,Y+Height*1.5,Txt$
	
	Color 255,0,0
	Rect X+1,Y+1,Width-2,Height-2,1
	
	Color 0,255,0
	Rect X+1,Y+1,fProportion*(Width-2),Height-2,1
End Function

Function TimeText$(Milliseconds)
	Local RemainText$=""
	Milliseconds=(Milliseconds*0.001)
	If (Milliseconds>60.0)
		If (Milliseconds>3600.0)
			If (Milliseconds>86400.0)
				If (Milliseconds>604800.0)
					RemainText=RemainText+Str(Int(Floor(Milliseconds/604800.0)))+" Weeks, "
					Milliseconds=Milliseconds Mod 604800
				End If
				RemainText=RemainText+Str(Int(Floor(Milliseconds/86400.0)))+" Days, "
				Milliseconds=Milliseconds Mod 86400
			End If
			RemainText=RemainText+Str(Int(Floor(Milliseconds/3600.0)))+" Hours, "
			Milliseconds=Milliseconds Mod 3600
		End If
		RemainText=RemainText+Str(Int(Floor(Milliseconds/60.0)))+" Minutes, and "
		Milliseconds=Milliseconds Mod 60.0
	End If
	RemainText=RemainText+Str(Int(Ceil(Milliseconds)))+" Seconds remaining"
	Return RemainText
End Function

























;Functions	

Function SetStreamByteLimit(nLimit%)
	nPROGRESS_STREAM_MAXIMUM_BYTES=nLimit
End Function

Function SetDirByteLimit(nLimit%)
	nPROGRESS_DIR_MAXIMUM_BYTES=nLimit
End Function

Function SetBankByteLimit(nLimit%)
	nPROGRESS_BANK_MAXIMUM_BYTES=nLimit
End Function

;--

Function SetProgressStreamBytePosition(nBytes%)
	nPROGRESS_STREAM_CURRENT_BYTES=nBytes
End Function

Function SetProgressStreamPercent(fPercent#)
	nPROGRESS_STREAM_CURRENT_BYTES=Int((Float(nPROGRESS_STREAM_MAXIMUM_BYTES)*0.01*fPercent))
End Function

Function SetProgressDir(nBytes%)
	nPROGRESS_DIR_CURRENT_BYTES=nBytes
End Function

Function SetProgressDirPercent(fPercent#)
	nPROGRESS_DIR_CURRENT_BYTES=Int((Float(nPROGRESS_DIR_MAXIMUM_BYTES)*0.01*fPercent))
End Function

Function SetProgressBank(nBytes%)
	nPROGRESS_BANK_CURRENT_BYTES=nBytes
End Function

Function SetProgressBankPercent(fPercent#)
	nPROGRESS_BANK_CURRENT_BYTES=Int((Float(nPROGRESS_BANK_MAXIMUM_BYTES)*0.01*fPercent))
End Function

;--

Function hCreateBankProgress(nBankSize%)
	Local hReturn%=CreateBank(nBankSize)
	nPROGRESS_BANK_MAXIMUM_BYTES=nBankSize
	nPROGRESS_BANK_CURRENT_BYTES=0
	nPROGRESS_BANK_TIMESTAMP=MilliSecs()
End Function

;--

Function ResizeBankProgress(hBank,nSize%)
	nPROGRESS_BANK_MAXIMUM_BYTES=nSize
	ResizeBank hBank,nSize
End Function

;--

Function ResetProgressStream()
	nPROGRESS_STREAM_CURRENT_BYTES=0
End Function

Function ResetProgressDir()
	nPROGRESS_DIR_CURRENT_BYTES=0
End Function

Function ResetProgressBank()
	nPROGRESS_BANK_CURRENT_BYTES=0
End Function

;--

Function ProgressStreamSkip()
	nPROGRESS_STREAM_CURRENT_BYTES=nPROGRESS_STREAM_MAXIMUM_BYTES
End Function

Function ProgressDirSkip()
	nPROGRESS_DIR_CURRENT_BYTES=nPROGRESS_DIR_MAXIMUM_BYTES
	nPROGRESS_STREAM_CURRENT_BYTES=nPROGRESS_STREAM_MAXIMUM_BYTES
End Function

Function ProgressBankSkip()
	nPROGRESS_BANK_CURRENT_BYTES=nPROGRESS_BANK_MAXIMUM_BYTES
End Function

;--

Function bProgressStreamComplete%()
	Return (nPROGRESS_STREAM_CURRENT_BYTES>=nPROGRESS_STREAM_MAXIMUM_BYTES)
End Function

Function bProgressDirComplete%()
	Return (nPROGRESS_DIR_CURRENT_BYTES>=nPROGRESS_DIR_MAXIMUM_BYTES)
End Function

Function bProgressBankComplete%()
	Return (nPROGRESS_BANK_CURRENT_BYTES>=nPROGRESS_BANK_MAXIMUM_BYTES)
End Function

;--

Function hReadProgressDir%(sDirPath$,sFilter$=".")
	nPROGRESS_DIR_CURRENT_BYTES=0
	nPROGRESS_DIR_MAXIMUM_BYTES=0
	nPROGRESS_STREAM_CURRENT_BYTES=0
	nPROGRESS_STREAM_MAXIMUM_BYTES=0
	nPROGRESS_DIR_TIMESTAMP=MilliSecs()
	Local hReturn%=0
	If (FileType(sDirPath)=2)
		hReturn=ReadDir(sDirPath)		
		If (hReturn)
			If (Right(sDirPath,1)<>"\") Then sDirPath=sDirPath+"\"
			Local sFile$=NextFile(hReturn)
			Local sFilePath$
			While (sFile<>"")
				sFilePath=sDirPath+sFile
				If ((FileType(sFilePath)=1) And (Instr(sFilePath,sFilter)))
					nPROGRESS_DIR_MAXIMUM_BYTES=nPROGRESS_DIR_MAXIMUM_BYTES+FileSize(sDirPath+sFile)
				End If
				sFile=NextFile(hReturn)
			Wend
			CloseDir hReturn
		End If
	End If
	If (hReturn)
		Return ReadDir(sDirPath)
	End If
End Function

Function hReadProgressStream%(sStreampath$,sFilter$=".")
	Local hReturn%=0
	
	If ((FileType(sStreampath)=1) And (Instr(sStreampath,sFilter)))
		nPROGRESS_STREAM_MAXIMUM_BYTES=FileSize(sStreampath)
		nPROGRESS_STREAM_CURRENT_BYTES=0
		nPROGRESS_STREAM_TIMESTAMP=MilliSecs()
		hReturn=ReadFile(sStreampath)
	End If
	Return hReturn
End Function

Function hOpenProgressStream%(sStreampath$,sFilter$=".")
	Local hReturn%=0
	
	If ((FileType(sStreampath)=1) And (Instr(sStreampath,sFilter)))
		nPROGRESS_STREAM_MAXIMUM_BYTES=FileSize(sStreampath)
		nPROGRESS_STREAM_CURRENT_BYTES=0
		nPROGRESS_STREAM_TIMESTAMP=MilliSecs()
		hReturn=OpenFile(sStreampath)
	End If
	Return hReturn
End Function

;--

Function sReadProgressLine$(hStreamHandle)
	Local sReturn$=ReadLine(hStreamHandle)
	nPROGRESS_STREAM_CURRENT_BYTES=nPROGRESS_STREAM_CURRENT_BYTES+Len(sReturn)+2	;Add 2 for line length short bytes
	nPROGRESS_DIR_CURRENT_BYTES=nPROGRESS_DIR_CURRENT_BYTES+Len(sReturn)+2	;Add 2 for line length short bytes
	Return sReturn
End Function

Function nReadProgressInt%(hStreamHandle)
	Local nReturn%=ReadInt(hStreamHandle)
	nPROGRESS_STREAM_CURRENT_BYTES=nPROGRESS_STREAM_CURRENT_BYTES+4
	nPROGRESS_DIR_CURRENT_BYTES=nPROGRESS_DIR_CURRENT_BYTES+4
	Return nReturn
End Function

Function nReadShortProgress(hStreamhandle)
	Local nReturn%=ReadShort(hStreamhandle)
	nPROGRESS_STREAM_CURRENT_BYTES=nPROGRESS_STREAM_CURRENT_BYTES+2
	nPROGRESS_DIR_CURRENT_BYTES=nPROGRESS_DIR_CURRENT_BYTES+2
	Return nReturn
End Function

Function sReadStringProgress$(hStreamhandle)
	Local sReturn$=ReadString(hStreamhandle)
	nPROGRESS_STREAM_CURRENT_BYTES=nPROGRESS_STREAM_CURRENT_BYTES+Len(sReturn)+2	;Add 2 for line length short bytes
	nPROGRESS_DIR_CURRENT_BYTES=nPROGRESS_DIR_CURRENT_BYTES+Len(sReturn)+2	;Add 2 for line length short bytes
	Return sReturn
End Function

Function fReadFloatProgress#(hStreamhandle)
	Local fReturn#=ReadFloat(hStreamhandle)
	nPROGRESS_STREAM_CURRENT_BYTES=nPROGRESS_STREAM_CURRENT_BYTES+4
	nPROGRESS_DIR_CURRENT_BYTES=nPROGRESS_DIR_CURRENT_BYTES+4
	Return fReturn
End Function

Function nReadByteProgress%(hStreamhandle)
	Local nReturn=ReadByte(hStreamhandle)
	nPROGRESS_STREAM_CURRENT_BYTES=nPROGRESS_STREAM_CURRENT_BYTES+1
	nPROGRESS_DIR_CURRENT_BYTES=nPROGRESS_DIR_CURRENT_BYTES+1
	Return nReturn
End Function

Function ReadBytesStreamProgress(hBankhandle,hStreamhandle,nOffset,nCount);Use this function when Progress meter refers to STREAM
	ReadBytes hBankhandle,hStreamhandle,nOffset,nCount
	nPROGRESS_STREAM_CURRENT_BYTES=nPROGRESS_STREAM_CURRENT_BYTES+nCount
	nPROGRESS_DIR_CURRENT_BYTES=nPROGRESS_DIR_CURRENT_BYTES+nCount
End Function

Function ReadBytesBankProgress(hBankhandle,hStreamhandle,nCount);Use this function when Progress meter refers to BANK
	ReadBytes hBankhandle,hStreamhandle,nPROGRESS_BANK_CURRENT_BYTES,nCount
	nPROGRESS_BANK_CURRENT_BYTES=nPROGRESS_BANK_CURRENT_BYTES+nCount
End Function

Function nPeekByteProgress%(hBank%)
	Local nReturn%=PeekByte(hBank,nPROGRESS_BANK_CURRENT_BYTES)
	nPROGRESS_BANK_CURRENT_BYTES=nPROGRESS_BANK_CURRENT_BYTES+1
	Return nReturn
End Function

Function nPeekIntProgress%(hBank%)
	Local nReturn%=PeekInt(hBank,nPROGRESS_BANK_CURRENT_BYTES)
	nPROGRESS_BANK_CURRENT_BYTES=nPROGRESS_BANK_CURRENT_BYTES+4
	Return nReturn
End Function

Function fPeekFloatProgress%(hBank%)
	Local fReturn#=PeekFloat(hBank,nPROGRESS_BANK_CURRENT_BYTES)
	nPROGRESS_BANK_CURRENT_BYTES=nPROGRESS_BANK_CURRENT_BYTES+4
	Return fReturn
End Function	

Function nPeekShortProgress%(hBank%,nOffset%)
	Local nReturn%=PeekShort(hBank,nPROGRESS_BANK_CURRENT_BYTES)
	nPROGRESS_BANK_CURRENT_BYTES=nPROGRESS_BANK_CURRENT_BYTES+2
	Return nReturn
End Function	

Function sPeekStringProgress$(hBank%)
	;Strings read from banks must end at a Null terminator
	Local sString$=""
	Local Byte=PeekByte(hBank,nPROGRESS_BANK_CURRENT_BYTES)
	While (Byte)
		sString=sString+Chr(Byte)
		nPROGRESS_BANK_CURRENT_BYTES=nPROGRESS_BANK_CURRENT_BYTES+1
		Byte=PeekByte(hBank,nPROGRESS_BANK_CURRENT_BYTES)
	Wend	
	Return sString
End Function

;--

Function WriteLineProgress$(hStreamhandle,sLine$)
	nPROGRESS_STREAM_CURRENT_BYTES=nPROGRESS_STREAM_CURRENT_BYTES+Len(sLine)+2; Add Chr(13)+Chr(10) for Linefeed & CarriageReturn
	nPROGRESS_DIR_CURRENT_BYTES=nPROGRESS_DIR_CURRENT_BYTES+Len(sLine)+2; Add Chr(13)+Chr(10) for Linefeed & CarriageReturn
	WriteLine hStreamhandle,sLine
End Function

Function WriteIntProgress%(hStreamhandle,nInt%)
	nPROGRESS_STREAM_CURRENT_BYTES=nPROGRESS_STREAM_CURRENT_BYTES+4
	nPROGRESS_DIR_CURRENT_BYTES=nPROGRESS_DIR_CURRENT_BYTES+4
	WriteInt hStreamhandle,nInt
End Function

Function WriteShortProgress%(hStreamhandle,nShort%)
	nPROGRESS_STREAM_CURRENT_BYTES=nPROGRESS_STREAM_CURRENT_BYTES+2
	nPROGRESS_DIR_CURRENT_BYTES=nPROGRESS_DIR_CURRENT_BYTES+2
	WriteShort hStreamhandle,nShort
End Function

Function WriteStringProgress$(hStreamhandle,sString$)
	nPROGRESS_STREAM_CURRENT_BYTES=nPROGRESS_STREAM_CURRENT_BYTES+Len(sString)+2	;Add 2 for line length short bytes
	nPROGRESS_DIR_CURRENT_BYTES=nPROGRESS_DIR_CURRENT_BYTES+Len(sString)+2	;Add 2 for line length short bytes
	WriteString hStreamhandle,sString
End Function

Function WriteFloatProgress%(hStreamhandle,fFloat#)
	nPROGRESS_STREAM_CURRENT_BYTES=nPROGRESS_STREAM_CURRENT_BYTES+4
	nPROGRESS_DIR_CURRENT_BYTES=nPROGRESS_DIR_CURRENT_BYTES+4
	WriteFloat hStreamhandle,fFloat
End Function

Function WriteByteProgress%(hStreamhandle,nInt%)
	nPROGRESS_STREAM_CURRENT_BYTES=nPROGRESS_STREAM_CURRENT_BYTES+1
	nPROGRESS_DIR_CURRENT_BYTES=nPROGRESS_DIR_CURRENT_BYTES+1
	WriteByte hStreamhandle,nInt Mod 256
End Function

;--

Function WriteBytesStreamProgress(hBank, hStreamHandle,nOffset,nCount)
	WriteBytes hBank,hStreamHandle,nOffset,nCount
	nPROGRESS_STREAM_CURRENT_BYTES=nPROGRESS_STREAM_CURRENT_BYTES+nCount
	nPROGRESS_DIR_CURRENT_BYTES=nPROGRESS_DIR_CURRENT_BYTES+nCount
End Function

Function WriteBytesBankProgress(hBank, hStreamHandle,nOffset,nCount)
	WriteBytes hBank,hStreamHandle,nOffset,nCount
	nPROGRESS_BANK_CURRENT_BYTES=nPROGRESS_STREAM_CURRENT_BYTES+nCount
End Function

;--

Function PokeByteProgress(hBank%,nOffset%,nByte%)
	PokeByte hBank,nPROGRESS_BANK_CURRENT_BYTES,nByte
	nPROGRESS_BANK_CURRENT_BYTES=nPROGRESS_BANK_CURRENT_BYTES+1
End Function

Function PokeIntProgress(hBank%,nOffset%,nInt%)
	PokeInt hBank,nPROGRESS_BANK_CURRENT_BYTES,nInt
	nPROGRESS_BANK_CURRENT_BYTES=nPROGRESS_BANK_CURRENT_BYTES+4
End Function

Function PokeFloatProgress(hBank%,nOffset%,fFloat%)
	PokeFloat hBank,nPROGRESS_BANK_CURRENT_BYTES,fFloat
	nPROGRESS_BANK_CURRENT_BYTES=nPROGRESS_BANK_CURRENT_BYTES+4
End Function

Function PokeShortProgress(hBank%,nShort%)
	PokeShort hBank,nPROGRESS_BANK_CURRENT_BYTES,nShort
	nPROGRESS_BANK_CURRENT_BYTES=nPROGRESS_BANK_CURRENT_BYTES+2
End Function

Function PokeStringProgress(hBank,sString$)
	;Strings sent to banks do not include Length ShortBytes, but instead have a null terminator
	Local Iter
	Local Byte
	Local Count=Len(sString)+nPROGRESS_BANK_CURRENT_BYTES
	
	For Iter=nPROGRESS_BANK_CURRENT_BYTES To Count
		Byte=Asc(Mid(sString,Iter,1))
		PokeByte hBank,Iter,Byte
	Next
	nPROGRESS_BANK_CURRENT_BYTES=nPROGRESS_BANK_CURRENT_BYTES+1;Add terminator
	PokeByte hBank,nPROGRESS_BANK_CURRENT_BYTES,0
End Function

;--

Function fProgressStreamPercentage#()
	Return fPercentage(nPROGRESS_STREAM_CURRENT_BYTES,nPROGRESS_STREAM_MAXIMUM_BYTES)
End Function

Function fProgressDirPercentage#()
	Return fPercentage(nPROGRESS_DIR_CURRENT_BYTES,nPROGRESS_DIR_MAXIMUM_BYTES)
End Function

Function fProgressBankPercentage#()
	Return fPercentage(nPROGRESS_BANK_CURRENT_BYTES,nPROGRESS_BANK_MAXIMUM_BYTES)
End Function

;--

Function sProgressStreamTimeRemaining$()
	Local Time#=fCalculateRemainingTime(fProgressStreamPercentage()*0.01,nPROGRESS_STREAM_TIMESTAMP)
	Return TimeText(Time#)
End Function

Function sProgressDirTimeRemaining$()
	Local Time#=fCalculateRemainingTime(fProgressDirPercentage()*0.01,nPROGRESS_DIR_TIMESTAMP)
	Return TimeText(Time#)
End Function

Function sProgressBankTimeRemaining$()
	Local Time#=fCalculateRemainingTime(fProgressBankPercentage()*0.01,nPROGRESS_BANK_TIMESTAMP)
	Return TimeText(Time#)
End Function

Function fPercentage#(nFraction%,nMaximum%)
	If (nFraction=nMaximum)  Then Return 100.0
	If (nFraction)*(nMaximum)
		Return Float(nFraction)/Float(nMaximum)*100.0
	End If
	Return 0.0
End Function
