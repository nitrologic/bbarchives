; ID: 567
; Author: jfk EO-11110
; Date: 2003-02-02 13:40:49
; Title: Recording Studio
; Description: Use Keyboard as Piano and record a Song.

; Mini-Sound-Studio: Record your ultimative Hit!!! Become a Star!
; Keyboard acts as Piano, Melodies can be recorded, replayed,
; saved and loaded. Supports multiple Tracks.

; This Program shows how to use Realtime Sequencing. You might add an Instrument Selection
; and a Beat-based Quantisation / Rythm-Correction  (rounding every Keystrokes Time
; Information to the next legal 1/8 or 1/6 Beat relative to the main Beat, pretty simple)

Graphics 640,480,16,1    ;(on my Win98 the windowed Mode gave me noisy Sounds - so using Fullscreen)
SetBuffer BackBuffer()

; Use any Sound. Maybe .OGG is better than .WAV, dunno.
sound=LoadSound("piano.wav")

Dim kbd(23) ; define keys for piano
kbd(0)=86
kbd(1)=30
kbd(2)=44
kbd(3)=31
kbd(4)=45
kbd(5)=46
kbd(6)=33
kbd(8)=47
kbd(9)=34
kbd(10)=48
kbd(11)=35
kbd(12)=49
kbd(13)=50
kbd(14)=37
kbd(15)=51
kbd(16)=38
kbd(17)=52
kbd(18)=53
Dim key_virgin(18)

;-----------------------
; Table for Piano Keys Frequencies used with ChannelPitch()
; (I doubt this Piano is "tuned" correctly)
Dim freq(23)
x=22000
For i=0 To 23
  freq(i)=x
  x=x+(44000.0/(36-(1.5*Float(i))))
Next
freq(6)=freq(6)+50
freq(8)=freq(8)-750
freq(17)=freq(17)-1500
freq(18)=freq(18)-2500
;-----------------------


Global start_time
Global song_end_time
; max keyhits per track
Global max_note=100000
; number of tracks -1
Global max_trax=2 ; (use max. 7 for Keys F1 to F8. F9 is used for "Play"!)
Dim track(max_trax,max_note,1)
Dim track_counter(max_trax)
Dim track_octave(max_trax)
For i=0 To max_trax
  track_octave(i)=4
Next

;---------------------------------------------- MAINLOOP -----------------------------------------

While KeyDown(1)=0
  ; check some Keys
  
  ; start recording?
  If recording=0
    For rec=0 To max_trax
      If KeyHit(59+rec) ;f1,f2,f3...
        recording=1 ; initialize recording
        playing=0
        active_track=rec
        For i=0 To max_note ; clean the Track we want to record on
          track(active_track,i,0)=0
          track(active_track,i,1)=0
        Next
        If last_note_track = active_track Then ;define new song lenght if last note was on this track
          For i2=0 To max_trax
            For i=max_note To 0 Step -1 ; search for last note
              If track(i2,i,0)<>0 Then
                song_end_time=track(i2,i,1)+3000 ;set song lenght to last note of other trax (+ 3s)
                Goto getoutofhere ; sometimes we even use Goto: used to Exit from nested For-Loops
              EndIf
            Next
          Next
          .getoutofhere
        EndIf
        For i=0 To max_trax
          track_counter(i)=0
        Next
        start_time=MilliSecs() ; init timer: all time data will be used relative to this "Moment Zero"
      EndIf
    Next
  EndIf
  
  ; start play?
  If KeyHit(67) ; f9
    If recording=1 Then
      If MilliSecs()-start_time>song_end_time ; probably define new song lenght after recording
        song_end_time=MilliSecs()-start_time
        last_note_track=active_track
      EndIf
    EndIf
    playing=1
    recording=0
    For i=0 To max_trax
      track_counter(i)=0
    Next
    start_time=MilliSecs() ; init timer
    FlushKeys()
  EndIf
  
  ; stop?
  If KeyHit(68) ; f10
    If recording=1
      If MilliSecs()-start_time>song_end_time ; probably define new song lenght after recording
        song_end_time=MilliSecs()-start_time
        last_note_track=active_track
      EndIf
    EndIf
    recording=0
    playing=0
    FlushKeys()
  EndIf
  
  ; load?
  If KeyHit(87) ; f11
    If recording=0
      Color 255,0,0
      Locate 0,260
      f$=Input("Please enter Filename to load: ")
      load_song(f$)
      FlushKeys()
    EndIf
  EndIf
  
  ; save?
  If KeyHit(88) ; f12
    If recording=0
      Color 255,0,0
      Locate 0,260
      f$=Input("Please enter Filename to SAVE: ")
      save_song(f$)
      FlushKeys()
    EndIf
  EndIf
  
  ; 1,2,3...change octave?
  For i=0 To max_trax
    If KeyHit(i+2)
      track_octave(i)=track_octave(i)*2
      If track_octave(i)>7 Then
        track_octave(i)=1
      EndIf
      If recording=0 Then active_track=i
    EndIf
  Next
  
  ; check Pianokeys and record if required
  For i=0 To 18
    If KeyDown(kbd(i))=1 ; using keydown together with key_virgin instead of keyhit: better accuracy.
      If key_virgin(i)=0
        If recording=1
          track(active_track,track_counter(active_track),1)=MilliSecs()-start_time ; remember time of keyhit
          track(active_track,track_counter(active_track),0)=1+i ; remember wich key (stored +1)
          track_counter(active_track)=track_counter(active_track)+1
          If track_counter(active_track)>=max_note Then ; reached maximum hits?
            recording=0
          EndIf
        EndIf
        chn=PlaySound(sound)
        ChannelPitch chn,freq(i)/track_octave(active_track)
        key_virgin(i)=1
      EndIf
    Else
      key_virgin(i)=0
    EndIf
  Next
  
  ; replay other tracks if recording
  If recording=1
    For i=0 To max_trax
      If i<>active_track
        If MilliSecs()>=(track(i,track_counter(i),1)+start_time)
          If track_counter(i)< max_note
            If track(i,track_counter(i),0)<>0
              chn=PlaySound(sound)
              ChannelPitch chn,freq(track(i,track_counter(i),0)-1)/track_octave(i)
            EndIf
            track_counter(i)=track_counter(i)+1
          EndIf
        EndIf
      EndIf
    Next
  EndIf
  
  ; replay all tracks if playing
  If playing=1
    For i=0 To max_trax
      If MilliSecs()>=(track(i,track_counter(i),1)+start_time)
        If track_counter(i)< max_note
          If track(i,track_counter(i),0)<>0
            chn=PlaySound(sound)
            ChannelPitch chn,freq(track(i,track_counter(i),0)-1)/track_octave(i)
          EndIf
          track_counter(i)=track_counter(i)+1
        EndIf
      EndIf
      If MilliSecs()>=song_end_time+start_time ; end of song? replay if so!
        For i2=0 To max_trax
          track_counter(i2)=0
        Next
        start_time=MilliSecs()
        Exit
      EndIf
    Next
  EndIf
  
  ; GFX-------------------------write some Info to screen------------------------------
  Cls
  ; optical metronom at song-start
  cutime=Floor(MilliSecs()-start_time)/500
  If (recording=1 Or playing=1) And cutime And 1=1  And cutime < 16 Then
    Color 0,255,0
    Rect 620,0,20,480,1
  EndIf
  ; more info
  Color 255,255,255
  Text 0,0,"Press F1,F2,F3 to record Track, F9 to play, F10 to stop"
  Text 0,16,"1,2,3 to select Octaves per Track, F11=Load, F12=Save Song"
  If recording=1 Then
    Text 0,32,"Recording Track: "+ active_track+ " : "+(MilliSecs()-start_time)+" ms"
  EndIf
  If playing=1
    Text 0,32,"Currently Replaying all Tracks : "+(MilliSecs()-start_time)+" ms"
  EndIf
  Text 0,48,"Active Track "+active_track
  For i=0 To max_trax
    Text 0,64+(i*16),"Track "+i+" uses Octave "+ track_octave(i)
  Next
  Text 0,184,"Piano Keys: (germ. layout)"
  bkey$=" A S  F G H  K L "
  wkey$="< Y XC V B NM ; :_"
  Color 255,0,0
  Text 0,204,bkey$
  Color 255,255,255
  For i=1 To Len(bkey$)
    If Mid$(bkey$,i,1)<>" "
      Rect (i-1)*8,201,9,20,0
    EndIf
  Next
  For i=1 To Len(wkey$)
    If Mid$(wkey$,i,1)<>" "
      Rect (i-1)*8,202,7,32,1
    EndIf
  Next
  Color 255,0,0
  Text 0,220,wkey$
  Color 0,0,0
  Line 0,201,200,201

  ; done ! pooh !
  Flip 0
Wend
End
;--------------------------------------------------------------------------------------

Function save_song(file$)
  If file$<>""
    wr=WriteFile(file$)
    For i2=0 To max_trax
      For i=0 To max_note ; search for last note
        If track(i2,i,0)=0 Then Exit
      Next
      num=i
      WriteInt wr,num ; write number of keystrokes for this Track
      WriteInt wr,track_octave(i2) ; save octave info for this Track
      For i=0 To num ; and write data
        WriteInt wr,track(i2,i,1) ; save time info
        WriteInt wr,track(i2,i,0) ; save key info
      Next
    Next
    CloseFile wr
  EndIf
End Function

Function load_song(file$)
  If file$<>"" And FileType(file$)=1
    For i2=0 To max_trax
      For i=0 To max_note ; clear memory
        track(i2,i,1)=0
        track(i2,i,0)=0
      Next
    Next
    song_end_time=0
    re=ReadFile(file$)
    For i2=0 To max_trax
      num=ReadInt(re) ; read number of keystrokes for this Track
      track_octave(i2)=ReadInt(re) ; read Octave for this Track
      For i=0 To num ; and read data
        track(i2,i,1)=ReadInt(re)  ; read time info
        track(i2,i,0)=ReadInt(re)  ; read key info
        If track(i2,i,1) > song_end_time Then song_end_time = track(i2,i,1);also check for last note
      Next
    Next
    CloseFile re
    For i2=0 To max_trax
      track_counter(i2)=0
    Next
    start_time=MilliSecs()
    ;playing=0
    recording=0
    song_end_time=song_end_time+3000 ; add 3s after last keyhit
  EndIf
End Function
