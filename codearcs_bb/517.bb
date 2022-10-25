; ID: 517
; Author: Ziltch
; Date: 2002-11-30 16:01:34
; Title: Recording Wav Files
; Description: Recording Wav

; Wav Recording 
; ZILTCH 2002

snd=LoadSound("anysoundfile.wav")
; put a sample here so we can here something to record
; so we can test the recording

StartWAVrecord(16,22000)
Print "Make sure you recording level in your Windows mixer is set above zero"
Print "Also make sure the mixer  is set so all channels can be recorded."
Print ""
Print "Recording now."

Print "Press space key to trigger sample."
Print "Escape to stop."

a#=0
hz=40000
While Not KeyHit(1)
   If KeyHit(57) Then
     a=a+1
     If Float (a/2) = Int(a/2) Then 
       ch=PlaySound(snd)
       a = 0
     End If
   EndIf
   If ch> 0 Then ChannelPitch ch,hz
   hz=hz-1
   If hz < 20000 Then hz =44000
Wend

StopWAVrecord()
FlushKeys
WaitKey()
Print "playback before saving"
PlayrecordedWAV()
WaitKey()


SaveWAVrecord("Stest.wav")
; the new wav file is saved with this name.

FreeSound ch

CloseWAVrecord()
;it is important to close the wave at the end;

End
;end test code

;------------ functions


Function StartWAVrecord(bitspersample=16,samplespersec=44000)

If (bitspersample <> 8) And (bitspersample <> 16)And (bitspersample <> 24) Then
  RuntimeError "Bits per sample musr be 8,16 or 24"
  Return
End If
mmstr$ = "open New type WaveAudio alias Capture"
mminfo = CreateBank(Len(mmstr$))
For I = 1 To Len(mmstr$)
  PokeByte mminfo,I - 1,Asc(Mid$(mmstr$,I,1))
Next
CallDLL("winmm.dll","mciExecute",mminfo)
Print "*"

bytes  = bitspersample/4
mmstr$ = "Set Capture time format ms bitspersample "+bitspersample+" channels 2 samplespersec "+samplespersec+" bytespersec "+samplespersec*bytes+" alignment "+bytes+" "
mminfo = CreateBank(Len(mmstr$))
For I = 1 To Len(mmstr$)-1
  PokeByte mminfo,I - 1,Asc(Mid$(mmstr$,I,1))
Next
CallDLL("winmm.dll","mciExecute",mminfo)

Print "**"
mmstr$ = "record Capture "
mminfo = CreateBank(Len(mmstr$))
For I = 1 To Len(mmstr$)-1
  PokeByte mminfo,I - 1,Asc(Mid$(mmstr$,I,1))
Next
CallDLL("winmm.dll","mciExecute",mminfo)
End Function


Function SaveWAVrecord(fname$)
mmstr$ = "save Capture "+ fname$
mminfo = CreateBank(Len(mmstr$))
For I = 1 To Len(mmstr$)
  PokeByte mminfo,I - 1,Asc(Mid$(mmstr$,I,1))
Next
CallDLL("winmm.dll","mciExecute",mminfo)
End Function


Function StopWAVrecord()
mmstr$ = "stop Capture "
mminfo = CreateBank(Len(mmstr$))
For I = 1 To Len(mmstr$)-1
  PokeByte mminfo,I - 1,Asc(Mid$(mmstr$,I,1))
Next
CallDLL("winmm.dll","mciExecute",mminfo)
End Function


Function CloseWAVrecord()
mmstr$ = "close Capture "
mminfo = CreateBank(Len(mmstr$))
For I = 1 To Len(mmstr$)-1
  PokeByte mminfo,I - 1,Asc(Mid$(mmstr$,I,1))
Next
CallDLL("winmm.dll","mciExecute",mminfo)
End Function


Function PlayrecordedWAV()
mmstr$ = "seek Capture to start "
mminfo = CreateBank(Len(mmstr$))
For I = 1 To Len(mmstr$   )-1
  PokeByte mminfo,I - 1,Asc(Mid$(mmstr$,I,1))
Next
CallDLL("winmm.dll","mciExecute",mminfo)

mmstr$ = "play Capture "
mminfo = CreateBank(Len(mmstr$))
For I = 1 To Len(mmstr$)-1
  PokeByte mminfo,I - 1,Asc(Mid$(mmstr$,I,1))
Next
CallDLL("winmm.dll","mciExecute",mminfo)
End Function
