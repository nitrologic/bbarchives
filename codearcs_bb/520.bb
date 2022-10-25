; ID: 520
; Author: Ziltch
; Date: 2002-12-05 08:45:29
; Title: Sound Managment  (replacing playsound and emitsound)
; Description: Sound Managment

type sample
	field snd
end type

Function playsnd(snd,src_ent=0,vol#=1)
  nosounds = true
  If snd = 0 Then 
;   DebugLog "no sound to play"
    Return
  end if
  For as.sample = each sample
    If Not ChannelPlaying(as\snd) Then 
      nosounds = false
      exit
    end if
  Next
  if nosounds then as.sample = new sample
  SoundVolume snd,vol
  
  If src_ent = 0 Then
    as\snd=PlaySound(snd)
  Else 
    as\snd=EmitSound(snd,src_ent)
  End If
  
;	debuglog snd +" = " +		as\snd
  return as\snd
		  
End Function
