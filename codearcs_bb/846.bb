; ID: 846
; Author: Ziltch
; Date: 2003-12-01 07:35:37
; Title: Recording example - Sample based piano
; Description: audio functions library Example

;Recording Samples using Blitz and Windows MCI Functions.
;
;ADAmor ZILTCH 20 Aug 2003

Include "audio.bb"

Graphics 640,400,32,2
SetBuffer BackBuffer()

Global LastNote$ = ""
Global Octave = 2
Global Capinc = 1
SampleFreq = 44100

Dim KeyDownTime(255)
Dim Waverecorded(11)
Dim Whitekeys.keynote(59)
Dim Blackkeys.keynote(44)

start = MilliSecs()

snd=LoadSound("E:\AUDIO\WAV\Acoustic Instruments\Strings\Viola.wav")
;^^ Put any sample here. This is for the piano keyboard.
; This sample is not Altered in any way.

SetupKeyboard()

While Not KeyHit(1)

  Cls
  Color 255,250,250
  Text 10,100,"Keyboard is laid out like piano"
  Text 10,180,"Use the Mouse wheel to change octave"
  Text 10,220,"[R]  to Start/Stop Next Recording"
  Text 10,240,"[1234567890] For Recording Playback"
  Text 10,260,"[esc] to exit"

  Color 25,20,150
  Text 15,120,"SD GHJ L;'  are the Black keys"
  Color 85,80,150
  Text 10,140,"ZXCVBNM,./  are the White keys"

  octave = Abs(MouseZ() Mod 5) ;Start Note for range

  Freq = CheckPiano(SampleFreq,octave)
  KeyPressed = GetKey()

  now = MilliSecs() - start

  If Freq > 0 Then
    If (Now - KeyDownTime(KeyPressed)) => Freq/1100 Then
     ch=PlaySound(snd)
     ChannelPitch ch,Freq
     KeyDownTime(KeyPressed) = now
     LastFreq = Freq
    EndIf

  EndIf

  RecordingStatus$ = CaptureStatusMode$()

  If KeyHit(19) Then
    ;Stop recording
    If RecordingStatus$ = "recording" Then
      StopWAVrecord()
      SaveWAVrecord("Stest"+(Capinc+1)+".wav")
;       Saved as Stest'x'.wav in current directory.

      Waverecorded(Capinc) = True

      ; Create a new capure alias so we can play previous samples and record new ones 
      Capinc = Capinc  + 1
      CaptureAlias$ = "Capture"+Capinc

    Else

      ; Start recording, set Sample Bits per sample  and Frequency (Samples per second)
      StartWAVrecord(16,44100)
      ; Capture time format 0 = bytes
      ;                     1 = milliseconds
      ;                     2 = samples
      SetCaptureTimeFormat$(1)

    End If
  End If

  For playkey = 2 To 11
    If KeyHit(playkey) And Waverecorded(playkey-1) Then
      ; This plays the recorded Samples when
      ; User hits a number (1234567890) key
      PlayrecordedWAV("Capture"+Str(playkey-1))
      DebugLog  "Playing Capture"+Str(playkey-1)
    End If
  Next

  If RecordingStatus$ = "recording" Then
    Color 155,250,250
    Text 10,10,"Record pos " + Str( CaptureWavPos()*100) + " "+ StatusCapTimeFormat$()+ "."
  End If
  Color 255,150,150
  Text 10,30,"Hz "+ LastFreq  +  " Note " + LastNote$

  Color 255,80,50
  Text 10,75, RecordingStatus$
  Flip False

Wend

CloseAllWAVrecord()

End



Function CheckPiano(SampleFreq,octave=3)
  For numkey = 44 To 53
    If KeyHit(numkey) Then
      FreqPitch = Whitekeys(numkey-43+octave*7)\Freq*1000
      LastNote$ = Whitekeys(numkey-43+octave*7)\name$
    End If
  Next

  Bcount = octave*5
  For numkey = 31 To 40
    Select numkey
      Case 33,37  ;skip some keys
        ; numkey = numkey +1
      Default
        Bcount = Bcount + 1
        If KeyHit(numkey) Then
          FreqPitch = blackkeys(bcount)\Freq*1000
          LastNote$ = blackkeys(bcount)\name$
        End If
    End Select
  Next

  Return FreqPitch

End Function



Type KeyNote
  Field name$,Freq#,wave
End Type



Function SetupKeyboard()

  Restore pianokeys
  For count = 0 To 99
    tn.keynote = New keynote
    Read tn\name
    Read tn\Freq
    Read tn\wave
    If Mid$(tn\name,2,1) = "#" Then
      Bcount = Bcount + 1
      Blackkeys.keynote(Bcount) = tn
    Else
      Wcount = Wcount + 1
      Whitekeys.keynote(Wcount) = tn
    End If
    DebugLog "Wcount = "+ Wcount
  Next

End Function

.pianokeys
Data "C0",16.35,2100.
Data "C#0/Db0",17.32,1990.
Data "D0",18.35,1870.
Data "D#0/Eb0",19.45,1770.
Data "E0",20.60,1670.
Data "F0",21.83,1580.
Data "F#0/Gb0",23.12,1490.
Data "G0",24.50,1400.
Data "G#0/Ab0",25.96,1320.
Data "A0",27.50,1250.
Data "A#0/Bb0",29.14,1180.
Data "B0",30.87,1110.
Data "C1",32.70,1050.
Data "C#1/Db1",34.65,996.
Data "D1",36.71,940.
Data "D#1/Eb1",38.89,887.
Data "E1",41.20,837.
Data "F1",43.65,790.
Data "F#1/Gb1",46.25,746.
Data "G1",49.00,704.
Data "G#1/Ab1",51.91,666.
Data "A1",55.00,627.
Data "A#1/Bb1",58.27,592.
Data "B1",61.74,559.
Data "C2",65.41,527.
Data "C#2/Db2",69.30,498.
Data "D2",73.42,470.
Data "D#2/Eb2",77.78,444.
Data "E2",82.41,419.
Data "F2",87.31,395.
Data "F#2/Gb2",92.50,373.
Data "G2",98.00,352.
Data "G#2/Ab2",103.83,332.
Data "A2",110.00,314.
Data "A#2/Bb2",116.54,296.
Data "B2",123.47,279.
Data "C3",130.81,264.
Data "C#3/Db3",138.59,249.
Data "D3",146.83,235.
Data "D#3/Eb3",155.56,222.
Data "E3",164.81,209.
Data "F3",174.61,198.
Data "F#3/Gb3",185.00,186.
Data "G3",196.00,176.
Data "G#3/Ab3",207.65,166.
Data "A3",220.00,157.
Data "A#3/Bb3",233.08,148.
Data "B3",246.94,140.
Data "C4",261.63,132.
Data "C#4/Db4",277.18,124.
Data "D4",293.66,117.
Data "D#4/Eb4",311.13,111.
Data "E4",329.63,105.
Data "F4",349.23,98.8
Data "F#4/Gb4",369.99,93.2
Data "G4",392.00,88.0
Data "G#4/Ab4",415.30,83.1
Data "A4",440.00,78.4
Data "A#4/Bb4",466.16,74.0
Data "B4",493.88,69.9
Data "C5",523.25,65.9
Data "C#5/Db5",554.37,62.2
Data "D5",587.33,58.7
Data "D#5/Eb5",622.25,55.4
Data "E5",659.26,52.3
Data "F5",698.46,49.4
Data "F#5/Gb5",739.99,46.6
Data "G5",783.99,44.0
Data "G#5/Ab5",830.61,41.5
Data "A5",880.00,39.2
Data "A#5/Bb5",932.33,37.0
Data "B5",987.77,34.9
Data "C6",1046.50,33.0
Data "C#6/Db6",1108.73,31.1
Data "D6",1174.66,29.4
Data "D#6/Eb6",1244.51,27.7
Data "E6",1318.51,26.2
Data "F6",1396.91,24.7
Data "F#6/Gb6",1479.98,23.3
Data "G6",1567.98,22.0
Data "G#6/Ab6",1661.22,20.8
Data "A6",1760.00,19.6
Data "A#6/Bb6",1864.66,18.5
Data "B6",1975.53,17.5
Data "C7",2093.00,16.5
Data "C#7/Db7",2217.46,15.6
Data "D7",2349.32,14.7
Data "D#7/Eb7",2489.02,13.9
Data "E7",2637.02,13.1
Data "F7",2793.83,12.3
Data "F#7/Gb7",2959.96,11.7
Data "G7",3135.96,11.0
Data "G#7/Ab7",3322.44,10.4
Data "A7",3520.00,9.8
Data "A#7/Bb7",3729.31,9.3
Data "B7",3951.07,8.7
Data "C8",4186.01,8.2
Data "C#8/Db8",4434.92,7.8
Data "D8",4698.64,7.3
Data "D#8/Eb8",4978.03,6.9
