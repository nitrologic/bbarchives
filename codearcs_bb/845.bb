; ID: 845
; Author: Ziltch
; Date: 2003-12-01 07:25:12
; Title: Audio Recording &amp; extended CD Functions V2
; Description: Audio Functions using MCI commands

; ZILTCH Audio Sample recording and CD functions
; Version 2.1 20 Aug 2003


;--------------------------------------------------------------
;  Add to winmm.decls (or create) in userlib directory
;
;.lib "winmm.dll" ; this is a standard windows file
; mciExecute%(Text$)
; mciSendString%(Command$,ReturnString*,ReturnLength%,Callback):"mciSendStringA"
;--------------------------------------------------------------


; NOTE:  This library uses the Windows Audio Mixer recording settings.
;        Make sure you are set to record 'Wave' or 'Audio mix' or 'What U hear' etc
;        Also check recording levels are not set to low!


Global CaptureAlias$ = "Capture1"
Global nul =CreateBank(4)

;CD Functions

Function StartCD(); init CD music at start of your program
  Return mciExecute("open cdaudio shareable")
End Function


Function StopCD() ;stop CD music at end of your program
  Return mciExecute("stop cdaudio")
End Function


Function OpenDoorCD()
  Return mciExecute("Set CDaudio door open")
End Function


Function closeDoorCD()
  Return mciExecute("Set CDaudio door closed" )
End Function


Function PlayCD();play CD form start
  If CDstatusMode$() = "stopped" Then startCD()
  Return mciExecute( "play cdaudio from 1")
End Function


Function PauseCD()
  Return mciExecute("pause cdaudio")
End Function


Function closeCD(); stop and closr CD music
  Return mciExecute("stop cdaudio")
  Return mciExecute("close cdaudio")
End Function


Function CDstatusTimeMode$() ; Get Time mode
  Retbank = CreateBank(12)
  mciSendString("Status CDAudio Time Format",Retbank,12,0)
  retval$ = PeekStr(retbank,5,0)
  FreeBank Retbank
  Return  Retval$
End Function


Function CDstatusCurrTrack()
  Retbank = CreateBank(12)
  mciSendString("Status CDAudio Current Track",Retbank,12,0)
  retval = PeekInt2(retbank,0)
  FreeBank Retbank
  Return  Retval
End Function


Function CDstatusMode$()
  Retbank = CreateBank(12)
  mciSendString("Status CDAudio mode",Retbank,12,0)
  retval$ = PeekStr(retbank,10,0)
  FreeBank Retbank
  Return  Retval$
End Function



Function PlayCDtracks(startpos$,endpos$="")
; eg PlayCDtracks(2:0:10,4:1:0)
;This function allows you start playing track 2 10 seconds in
;and stop 1 minute into track 4.
  endpost$ = ""
  If endpos > "" Then endpost$ = " To " + endpos

  If CDstatusCurrTrack() = 0 Then DebugLog "startCD()"
  SetCDTimeFormat(1)

  Return  mciExecute("Play CDAudio From " + startpos +endpost$)
End Function


Function NumCDtracks()
  Retbank = CreateBank(12)
  mciSendString("Status CDAudio Number of Tracks",Retbank,12,0)
  retval = PeekInt2(retbank,0)
  FreeBank Retbank
  Return  Retval
End Function


Function SetCDTimeFormat(mode)
  Select mode
    Case 0
      Return mciExecute("Set CDAudio time format msf")
    Case 1
      Return mciExecute("Set CDAudio time format tmsf")
  End Select
End Function


Function CDstatusPos$(); Get current position in Track : Min : Sec
  Retbank = CreateBank(12)
  mciSendString("Status CDAudio Position",Retbank,12,0)
  retval$ = PeekStr(retbank,11,0)
;  printbank(retbank)
  FreeBank Retbank
  Return  Retval$
End Function


; Recording Function


Function StartWAVrecord(bitspersample=16,samplespersec=44000)
  DebugLog CaptureAlias$
  If (bitspersample <> 8) And (bitspersample <> 16)And (bitspersample <> 24) Then
    RuntimeError "Bits per sample musr be 8,16 or 24"
    Return
  End If

  mciExecute("open New type WaveAudio alias "+ CaptureAlias$)
  bytes  = bitspersample/4
  mciExecute("Set "+ CaptureAlias$+" time format ms bitspersample "+bitspersample+" channels 2 samplespersec "+samplespersec+" bytespersec "+samplespersec*bytes+" alignment "+bytes)
  SetCaptureTimeFormat(0)
  Return mciExecute("record "+ CaptureAlias$)

End Function


Function CloseWAVrecord()
  Return mciExecute( "close "+ CaptureAlias$)
End Function

Function CloseAllWAVrecord()
;Use this at end of program to close/free all samples
  Return mciSendString( "close all",nul,0,0 )
End Function


Function CapturestatusReady()
  Retbank = CreateBank(12)
  mciSendString("Status "+ CaptureAlias$ +" Ready",Retbank,12,0)
  retval$ = PeekInt2(retbank,0)
  FreeBank Retbank
  Return  Retval
End Function


Function SaveWAVrecord(fname$)
;  RETURN mciExecute("save "+ CaptureAlias$ +" "+ fname$ )
  Return mciSendString("save "+ CaptureAlias$ +" "+ fname$,nul,0,0 )
End Function


Function StopWAVrecord()
  Return mciExecute("stop "+ CaptureAlias$)
End Function



Function PlayrecordedWAV(CaptureName$="")

  If CaptureName$="" Then CaptureName$ = CaptureAlias$

  mciExecute( "seek "+ CaptureName$ +" to start" )
  Return mciExecute( "play "+ CaptureName$ )

End Function


Function CaptureWavPos()
; Get current recording in Min : Sec

  Retbank = CreateBank(32)
  mciSendString("Status "+ CaptureAlias$ +" Position",Retbank,32,0)
  retval = PeekInt2(retbank,0)
  FreeBank Retbank
  Return  Retval

End Function


Function SetCaptureTimeFormat$(mode)
  Select mode
    Case 0
      Return mciExecute("Set "+ CaptureAlias$ +" time format bytes")
    Case 1
      Return mciExecute("Set "+ CaptureAlias$ +" time format milliseconds")
    Case 2
      Return mciExecute("Set "+ CaptureAlias$ +" time format samples")
  End Select
End Function


Function StatusCapTimeFormat$()
  Retbank = CreateBank(16)
  mciSendString("Status "+ CaptureAlias$ +" Time Format",Retbank,16,0)
  retval$ = PeekStr(retbank,15,0)
  FreeBank Retbank
  Return  Retval$
End Function

Function CaptureStatusMode$()
  Retbank = CreateBank(12)
  mciSendString("Status "+ CaptureAlias$ +" mode",Retbank,12,0)
  retval$ = PeekStr(retbank,10,0)
  FreeBank Retbank
  Return  Retval$
End Function



;-- Bank Commands

Function PeekInt2%(Tbank,Offset=0)
  Return Chr$(PeekByte(Tbank,Offset))+Chr$(PeekByte(Tbank,Offset+1))
End Function

Function PeekStr$(Tbank,Size=64,Offset=0)
  Local NewStr$ = ""
  For count = offset To (offset+size-1)
    newchr = PeekByte(Tbank,count)
    If newchr = 0 Then Exit
    newstr$ = newstr$ + Chr$(newchr)
  Next
  Return Newstr$

End Function
