; ID: 190
; Author: Rick
; Date: 2002-01-13 08:13:06
; Title: Sound Sorter
; Description: Easily Manage your sounds to play according to channels that are open or by a Sounds Priority

Const Channels = 15

Type Sound_Struc
    Field Channel        ;Channel used to play
    Field Sound            ;Sound to be played
    Field Priority#        ;Importance Rating of Sound
    Field Volume#        ;How loud to play Sound
    Field silence#        ;How fast Volume decreases
    Field Pitch            ;Htz pitch to play sound
End Type
    Global Play.Sound_Struc = New Sound_Struc

SeedRnd(MilliSecs())
For a = 0 To channels        ;As program starts or reloops we want
Delete play                    ;to make sure all Channels are cleared
Next                        ;of possible old sounds

;Create 16 Empty Channels
For a = 0 To channels
Play.Sound_Struc = New Sound_Struc
Play\Channel    =0            ;what sound is being played
play\sound        =0            ;what sound is remembered
play\priority =0            ;sounds priority
play\volume        =0            ;Sound Volume
play\silence    =0            ;Volume decrease rate
play\Pitch        =0            ;Hertz Range
Next

;XXXXXXXXXXXXXXXXXXXXX
;Main Program with all sound calls
;XXXXXXXXXXXXXXXXXXXXX

;XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
Function UpdateSound()
;XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
;Update the channel information

For a.sound_struc = Each sound_struc
    If ChannelPlaying (a\channel) Then
        a\volume=a\volume-(a\silence*.00001)
        If a\volume<0 Then a\volume=0
        ChannelVolume a\channel,a\volume
        If a\pitch <> -1 Then ChannelPitch a\channel,a\pitch
        a\priority=a\priority-.01
            If a\priority<1 Then a\priority=1
    Else
        a\sound=0
        a\priority=0
        a\volume=0
        a\silence=0
        a\pitch=0
    EndIf
    If a\volume=0 Or a\priority<0 And a\sound <> 0 Then StopChannel a\channel
Next

End Function


;XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
Function RequestSound(Sound,priority,volume#,silence,pitch,job)
;XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
    
    ;sound,priority,volume,silence,pitch,job
    
    ;sound - Any sound variable loaded with a sound
    ;priority - importance of sound in the event that it needs to overide another
    ;volume - how loud to play the sound
    ;silence - how fast volume is decreased *.00001
    ;pitch - what hertz to play sound at
    ;job - 0=None 1=kill 2=Loop (only loops new sounds)

If (Not soundoff) Then         ;Global Sound Flag
    SoundPlaced=False
    ;Determine if requested sound already exhists
    For play.sound_struc = Each sound_struc
        If play\sound = sound Then
            SoundPlaced=True
            If job = 1 Then            ;Do we kill the current sound?
                If ChannelPlaying (play\channel) Then StopChannel play\channel
                play\priority=0
                play\volume=0
                play\silence=0
                play\pitch=0
            Else                    ;If not a kill then update Sound Variables
                play\priority=priority
                play\volume=volume
                play\silence=silence
                play\pitch=pitch
                If play\pitch <> -1 Then ChannelPitch play\channel,play\pitch
            EndIf
        EndIf
    Next
    
    ;Is there an open channel?
    If (Not SoundPlaced) And job <> 1 Then
        For play.sound_struc = Each sound_struc
            If (Not ChannelPlaying (play\channel)) And (Not SoundPlaced) Then
                SoundPlaced = True
                play\sound = sound
                play\priority=priority
                play\volume=volume
                play\silence=silence
                play\pitch=pitch
            ;    If play\pitch <> -1 Then ChannelPitch play\channel,play\pitch
                If job = 2 Then LoopSound(sound)
                play\channel = PlaySound(sound)
                
            EndIf
        Next
    EndIf
    ;Is the priority higher than exhisting Sound?
    If (Not soundplaced) And job <> 1 Then
        For play.sound_struc = Each sound_struc
            If priority >= play\priority Then
                StopChannel play\channel
                play\sound = sound
                play\priority=priority
                play\volume=volume
                play\silence=silence
                play\pitch=pitch
            ;    If play\pitch <> -1 Then ChannelPitch play\channel,play\pitch
                If job = 2 Then LoopSound(sound)
                play\channel = PlaySound(sound)
                
            EndIf
        Next
    EndIf
EndIf
End Function
